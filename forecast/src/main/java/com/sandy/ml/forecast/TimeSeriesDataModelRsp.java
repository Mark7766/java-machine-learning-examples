package com.sandy.ml.forecast;

import java.time.LocalDateTime;
import java.util.List;

public class TimeSeriesDataModelRsp {
    private List<String> timestamps;
    private List<Float> values;
    private List<Float> predictions;

    public List<String> getTimestamps() {
        return timestamps;
    }

    public List<Float> getValues() {
        return values;
    }

    public List<Float> getPredictions() {
        return predictions;
    }
    public void setTimestamps(List<String> timestamps) {
        this.timestamps = timestamps;
    }
    public void setValues(List<Float> values) {
        this.values = values;
    }
    public void setPredictions(List<Float> predictions) {
        this.predictions = predictions;
    }

    @Override
    public String toString() {
        return "TimeSeriesDataModel{" +
                "timestamps=" + timestamps +
                ", values=" + values +
                ", predictions=" + predictions +
                '}';
    }
}