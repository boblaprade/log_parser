package com.cjmware.models;

import java.util.Arrays;
import java.util.List;

public class Parameters {
    private String startDate = "";
    private String duration = "hourly";
    private Integer threshold = 100;
    private String source = "./access.log";
    public static final List<String> durationValues = Arrays.asList("hourly", "daily");

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public Integer getThreshold() {
        return threshold;
    }

    public void setThreshold(Integer threshold) {
        this.threshold = threshold;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    @Override
    public String toString() {
        return "Parameters{" +
                "startDate='" + startDate + '\'' +
                ", duration='" + duration + '\'' +
                ", threshold=" + threshold +
                ", source='" + source + '\'' +
                '}';
    }
}
