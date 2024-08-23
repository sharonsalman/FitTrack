package com.sharonsalman.fittrack.Location;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "location_data")
public class LocationData {
    public double latitude;
    public double longitude;
    public long timestamp;
    public long duration;
    public double pace;

    public LocationData() {
    }

    public LocationData(double latitude, double longitude, long timestamp, long duration, double pace) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.timestamp = timestamp;
        this.duration = duration;
        this.pace = pace;
    }
}
