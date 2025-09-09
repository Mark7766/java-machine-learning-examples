package com.sandy.ml.forecast;

import ai.djl.ModelException;
import ai.djl.inference.Predictor;
import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDManager;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ZooModel;
import ai.djl.timeseries.Forecast;
import ai.djl.timeseries.TimeSeriesData;
import ai.djl.timeseries.dataset.FieldName;
import ai.djl.timeseries.translator.DeepARTranslator;
import ai.djl.training.util.ProgressBar;
import ai.djl.translate.TranslateException;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DeepARTranslatorTest {
    public static void main(String[] args) throws ModelException, TranslateException, IOException {
        new DeepARTranslatorTest().testDeepARTranslator();
    }

    public void testDeepARTranslator() throws IOException, TranslateException, ModelException {

        String modelUrl = "https://resources.djl.ai/test-models/mxnet/timeseries/deepar.zip";
        Map<String, Object> arguments = new ConcurrentHashMap<>();
        int predictionLength = 28;
        arguments.put("prediction_length", predictionLength);
        DeepARTranslator translator = DeepARTranslator.builder(arguments).build();
        Criteria<TimeSeriesData, Forecast> criteria =
                Criteria.builder()
                        .setTypes(TimeSeriesData.class, Forecast.class)
                        .optModelUrls(modelUrl)
                        .optTranslator(translator)
                        .optProgress(new ProgressBar())
                        .build();

        try (NDManager manager = NDManager.newBaseManager()) {
            manager.getEngine().setRandomSeed(1);
            // The "target" here is a fake series data which precedes the forecast series.
            // It plays the role of input based on which the prediction is made.
            NDArray target = manager.arange(0.0f, 50.0f, (float) 50 / 1856);

            TimeSeriesData input = new TimeSeriesData(1);
            input.setStartTime(LocalDateTime.parse("2011-01-29T00:00"));
            input.setField(FieldName.TARGET, target);

            try (ZooModel<TimeSeriesData, Forecast> model = criteria.loadModel();
                 Predictor<TimeSeriesData, Forecast> predictor = model.newPredictor()) {
                Forecast forecast = predictor.predict(input);
                // Here forecast.mean() is a predicted sequence of length "predictionLength"。
                // Doing `System.out.println(forecast.mean());` the result still has randomness.
                // This is because the model imported from
                // https://resources.djl.ai/test-models/mxnet/timeseries/deepar.zip
                // was trained on a sparse data with many zero sales (inactive sale
                // amount). So during the inference it also predict for such inactive data once
                // in a while interweaving the active non-zero data.
                // A model trained on an aggregated dataset (aggregated by week) is presented in
                // https://github.com/Carkham/m5_blog/blob/main/bloh.md
                float[] forecastArray = forecast.mean().toFloatArray();
               System.out.println("predict:"+forecastArray[0]);
            }
        }
    }
}