package com.example.safetymanagementapp;

public class Data {
    int imageUrl;
    String time;
    String location;


    public Data(int imageUrl, String time, String location) {
        this.imageUrl = imageUrl;
        this.time = time;
        this.location = location;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(int imageUrl) {
        this.imageUrl = imageUrl;
    }
}
