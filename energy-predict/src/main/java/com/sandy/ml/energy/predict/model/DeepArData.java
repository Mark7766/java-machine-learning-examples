package com.sandy.ml.energy.predict.model;

import lombok.Data;

import java.util.List;

/**
 * 时间序列数据模型，对应deepar.md运行参数
 */
@Data
public class DeepArData {
    /** 时间序列的起始时间，格式如"2022-01-01 00:00:00" */
    private String start;

    /** 目标变量，通常为数值型时间序列数据 */
    private List<Double> target;

    /** 时间序列的唯一标识符 */
    private String itemId;

    /** 静态类别特征，类型为数组，如[工厂,介质] */
    private List<String> featStaticCat;

    /** 静态数值特征，类型为数组，如[容量,初始值] */
    private List<Double> featStaticReal;

    /** 动态数值特征，类型为二维数组，如[产品1,产品2,产品3] */
    private List<List<Double>> featDynamicReal;

    /** 动态类别特征，类型为二维数组，如[节假日标记,工作日标记] */
    private List<List<Integer>> featDynamicCat;
}

