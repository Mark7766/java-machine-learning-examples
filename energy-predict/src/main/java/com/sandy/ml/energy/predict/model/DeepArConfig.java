package com.sandy.ml.energy.predict.model;

import lombok.Data;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

@Data
@Entity
public class DeepArConfig {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int predictionLength = 24; // 指定预测的时间步长，默认24
    private int contextLength = 48; // 用于预测的历史时间步长，默认48
    private String freq = "1H"; // 时间序列数据的频率，默认每小时
    private String batchifier = "stack"; // 批处理方式，默认stack
    private int numLayers = 2; // LSTM层数，默认2
    private int hiddenSize = 40; // 每层LSTM隐藏单元数，默认40
    private float dropoutRate = 0.1f; // Dropout比例，默认0.1
    private int embeddingDimension = 10; // 类别变量嵌入维度，默认10
    private int epochs = 50; // 训练轮数，默认50
    private float learningRate = 0.001f; // 学习率，默认0.001
    private int batchSize = 32; // 每批次样本数，默认32
    private String optimizer = "adam"; // 优化器类型，默认adam
    private float weightDecay = 0.0f; // 权重衰减，默认0.0
    private boolean earlyStopping = true; // 是否提前停止，默认true
    private int patience = 5; // 提前停止容忍轮数，默认5
    private boolean useFeatStaticCat = false; // 是否使用静态类别特征，默认true
    private boolean useFeatStaticReal = false; // 是否使用静态数值特征，默认false
    private boolean useFeatDynamicReal = false; // 是否使用动态数值特征，默认true
    private boolean useFeatDynamicCat = false; // 是否使用动态类别特征，默认false
    private String[] staticCatFields ; // 静态类别特征字段最大集
    private String[] staticRealFields ; // 静态数值特征字段最大集
    private String[] dynamicRealFields ; // 动态数值特征字段最大集
    private String[] dynamicCatFields ; // 动态类别特征字段最大集
    private int numSamples = 100; // 预测时采样样本数，默认100
    private float[] quantiles = {0.1f, 0.5f, 0.9f}; // 需要输出的分位点列表，默认[0.1, 0.5, 0.9]
}
