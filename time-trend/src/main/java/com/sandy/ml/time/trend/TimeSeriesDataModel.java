package com.sandy.ml.time.trend;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class TimeSeriesDataModel {
    private List<LocalDateTime> timestamps;
    private List<Float> values;
    private List<Float> predictions;
}