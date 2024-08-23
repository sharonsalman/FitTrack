package com.sharonsalman.fittrack.mainscreen;

public class WeightMeasurement {
    private float weight;
    private long date;

    public WeightMeasurement() {
        // Default constructor required for calls to DataSnapshot.getValue(WeightMeasurement.class)
    }

    public WeightMeasurement(float weight, long date) {
        this.weight = weight;
        this.date = date;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }
}
