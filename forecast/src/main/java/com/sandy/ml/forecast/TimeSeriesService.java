package com.sandy.ml.forecast;

import ai.djl.inference.Predictor;
import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDManager;
import ai.djl.repository.zoo.ZooModel;
import ai.djl.timeseries.Forecast;
import ai.djl.timeseries.TimeSeriesData;
import ai.djl.timeseries.dataset.FieldName;
import ai.djl.translate.TranslateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class TimeSeriesService {
    @Autowired
    private  ZooModel<TimeSeriesData, Forecast> model;

    public TimeSeriesDataModel processCsvFile(MultipartFile file) throws IOException {
        List<LocalDateTime> timestamps = new ArrayList<>();
        List<Float> values = new ArrayList<>();
        CsvUtils.parseCsv(file, timestamps, values);
        // 确保使用最后 24 个数据点作为上下文
        int contextLength = 24;
        try (NDManager manager = NDManager.newBaseManager()) {
            List<Float> recentValues = values.subList(values.size() - contextLength, values.size());
            List<LocalDateTime> recentTimestamps = timestamps.subList(timestamps.size() - contextLength, timestamps.size());
            float[] valueArray = new float[recentValues.size()];
            for (int i = 0; i < recentValues.size(); i++) {
                valueArray[i] = recentValues.get(i);
            }
            NDArray targetArray = manager.create(valueArray);
            System.out.println("Target Array Shape: " + targetArray.getShape()); // 调试形状

            TimeSeriesData input = new TimeSeriesData(recentValues.size());
            input.setField(FieldName.TARGET, targetArray);
            input.setStartTime(recentTimestamps.get(0)); // 使用最近数据的起始时间


            Predictor<TimeSeriesData, Forecast> predictor = model.newPredictor();
            Forecast forecast = predictor.predict(input);

            List<Float> predictions = new ArrayList<>();
            float[] forecastArray = forecast.mean().toFloatArray();
            for (float value : forecastArray) {
                predictions.add(value);
            }
            TimeSeriesDataModel model = new TimeSeriesDataModel();
            model.setTimestamps(timestamps); // 保留所有 timestamps
            model.setValues(values); // 保留所有 values
            model.setPredictions(predictions);
            return model;
        } catch (TranslateException e) {
            e.printStackTrace(); // 打印完整异常栈
            throw new RuntimeException("Prediction failed", e);
        }
    }
}