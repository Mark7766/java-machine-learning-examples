package com.sandy.ml.energy.predict;


import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDManager;
import ai.djl.timeseries.TimeSeriesData;
import ai.djl.translate.TranslateException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DataProcessingService {

    public TimeSeriesData processData(MultipartFile historicalFile, MultipartFile futureFile, String freq) throws Exception {
        try (NDManager manager = NDManager.newBaseManager()) {
            List<Map<String, String>> historicalData = parseCsv(historicalFile);
            List<Map<String, String>> futurePlans = parseCsv(futureFile);
            int numFactories = 3; // 简化测试
            int numMedia = 3;
            int numSequences = numFactories * numMedia;
            int historicalLength = freq.equals("1M") ? 36 : 1095;
            int predictionLength = freq.equals("1M") ? 12 : 7;

            float[][] target = aggregateTarget(historicalData, freq, numSequences, historicalLength);

            float[][] dynamicFeat = aggregateDynamicFeatures(historicalData, futurePlans, freq, numSequences, historicalLength, predictionLength);

            int[][] staticCat = generateStaticFeatures(numFactories, numMedia);

            TimeSeriesData data = new TimeSeriesData(100);
            data.setField("target", manager.create(target));
            data.setField("dynamic_feat", manager.create(dynamicFeat));
            data.setField("static_cat", manager.create(staticCat));
            data.setStartTime(recentTimestamps.get(0)); // 使用最近数据的起始时间
            return data;
        } catch (
        TranslateException e) {
            e.printStackTrace(); // 打印完整异常栈
            throw new RuntimeException("Prediction failed", e);
        }
    }

    private List<Map<String, String>> parseCsv(MultipartFile file) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
        List<String> lines = reader.lines().collect(Collectors.toList());
        String[] headers = lines.get(0).split(",");
        List<Map<String, String>> data = new ArrayList<>();
        for (int i = 0; i < lines.size(); i++) {
            String[] values = lines.get(i).split(",");
            Map<String, String> row = new HashMap<>();
            for (int j = 0; j < headers.length; j++) {
                row.put(headers[j], values[j]);
            }
            data.add(row);
        }
        return data;
    }

    private float[][] aggregateTarget(List<Map<String, String>> data, String freq, int numSequences, int length) {
        // 简化实现：假设数据已聚合
        float[][] target = new float[numSequences][length];
        // 填充随机值测试
        for (int i = 0; i < numSequences; i++) {
            for (int t = 0; t < length; t++) {
                target[i][t] = (float) Math.random() * 10000;
            }
        }
        return target;
    }

    private float[][] aggregateDynamicFeatures(List<Map<String, String>> historical, List<Map<String, String>> future, String freq, int numSequences, int historicalLength, int predictionLength) {
        float[][] dynamicFeat = new float[3][numSequences * (historicalLength + predictionLength)];
        // 简化实现：填充随机值
        for (int p = 0; p < 3; p++) {
            for (int i = 0; i < numSequences * (historicalLength + predictionLength); i++) {
                dynamicFeat[p][i] = (float) Math.random() * 100;
            }
        }
        return dynamicFeat;
    }

    private int[][] generateStaticFeatures(int numFactories, int numMedia) {
        int numSequences = numFactories * numMedia;
        int[][] staticCat = new int[numSequences][2];
        for (int i = 0; i < numSequences; i++) {
            staticCat[i][0] = i / numMedia; // factory_id
            staticCat[i][1] = i % numMedia; // media_type
        }
        return staticCat;
    }
}