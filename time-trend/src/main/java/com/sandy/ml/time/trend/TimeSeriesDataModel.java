package com.sandy.ml.time.trend;

import java.time.LocalDateTime;
import java.util.List;


public class TimeSeriesDataModel {
    private List<LocalDateTime> timestamps;
    private List<Float> values;
    private List<Float> predictions;

    public List<LocalDateTime> getTimestamps() {
        return timestamps;
    }

    public List<Float> getValues() {
        return values;
    }

    public List<Float> getPredictions() {
        return predictions;
    }
    public void setTimestamps(List<LocalDateTime> timestamps) {
        this.timestamps = timestamps;
    }
    public void setValues(List<Float> values) {
        this.values = values;
    }
    public void setPredictions(List<Float> predictions) {
        this.predictions = predictions;
    }
}