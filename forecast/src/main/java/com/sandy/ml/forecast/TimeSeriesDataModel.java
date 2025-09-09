package com.sandy.ml.forecast;

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

    public TimeSeriesDataModelRsp toTimeSeriesDataModelRsp() {
        TimeSeriesDataModelRsp rsp = new TimeSeriesDataModelRsp();
        if (this.timestamps != null) {
            List<String> ts = this.timestamps.stream()
                .map(LocalDateTime::toString)
                .toList();
            rsp.setTimestamps(ts);
        }
        rsp.setValues(this.values);
        rsp.setPredictions(this.predictions);
        return rsp;
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