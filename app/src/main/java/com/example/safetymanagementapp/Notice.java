package com.example.safetymanagementapp;

public class Notice {
    int id;
    String title;
    String detail;
    String date;

    public Notice(int id, String title, String detail, String date){
        this.id = id;
        this.title = title;
        this.detail = detail;
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String datail) {
        this.detail = datail;
    }
}
