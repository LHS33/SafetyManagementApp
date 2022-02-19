package com.example.safetymanagementapp;

public class Data {
    int image;
    String time;
    String location;


    public Data(int image, String time, String location) {
        this.image = image;
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

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }
}
