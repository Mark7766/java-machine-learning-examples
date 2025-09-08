package com.sandy.ml.time.trend;

import ai.djl.inference.Predictor;
import ai.djl.ndarray.NDArray;
import ai.djl.repository.zoo.ZooModel;
import ai.djl.timeseries.Forecast;
import ai.djl.timeseries.TimeSeriesData;
import ai.djl.timeseries.dataset.FieldName;
import ai.djl.translate.TranslateException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class TimeSeriesService {

    private final ZooModel<TimeSeriesData, Forecast> deepARModel;

    public TimeSeriesService(ZooModel<TimeSeriesData, Forecast> deepARModel) {
        this.deepARModel = deepARModel;
    }

    public TimeSeriesDataModel processCsvFile(MultipartFile file) throws IOException {
        List<LocalDateTime> timestamps = new ArrayList<>();
        List<Float> values = new ArrayList<>();
        CsvUtils.parseCsv(file, timestamps, values);
        float[] valueArray = new float[values.size()];
        for (int i = 0; i < values.size(); i++) {
            valueArray[i] = values.get(i);
        }
        NDArray targetArray= deepARModel.getNDManager().create(valueArray);
        TimeSeriesData data = new TimeSeriesData(1);
        data.setField(FieldName.TARGET, targetArray);
        data.setStartTime(timestamps.get(0));
        List<Float> predictions = predict(data);
        TimeSeriesDataModel model = new TimeSeriesDataModel();
        model.setTimestamps(timestamps);
        model.setValues(values);
        model.setPredictions(predictions);
        return model;
    }

    private List<Float> predict(TimeSeriesData data) {
        try (Predictor<TimeSeriesData, Forecast> predictor = deepARModel.newPredictor()) {
            Forecast forecastResult = predictor.predict(data);
            List<Float> predictions = new ArrayList<>();
            float[] forecastArray = forecastResult.mean().toFloatArray();
                for (float value : forecastArray) {
                    predictions.add(value);
                }
            return predictions;
        } catch (TranslateException e) {
            throw new RuntimeException("Prediction failed", e);
        }
    }
}