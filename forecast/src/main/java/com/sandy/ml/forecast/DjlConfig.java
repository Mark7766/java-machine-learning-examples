package com.sandy.ml.forecast;

import ai.djl.ModelException;
import ai.djl.engine.Engine;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ModelZoo;
import ai.djl.repository.zoo.ZooModel;
import ai.djl.timeseries.Forecast;
import ai.djl.timeseries.TimeSeriesData;
import ai.djl.timeseries.translator.DeepARTranslator;
import ai.djl.training.util.ProgressBar;
import ai.djl.translate.Translator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class DjlConfig {

    @Bean
    public ZooModel<TimeSeriesData, Forecast> deepARModel() throws IOException, ModelException {
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
        return criteria.loadModel();
    }
}