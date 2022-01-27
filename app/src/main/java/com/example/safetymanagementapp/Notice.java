package com.example.safetymanagementapp;

public class Notice {
    int id;
    String title;
    String detail;
    String date;

    boolean expandable;

    public boolean isExpandable() {
        return expandable;
    }

    public void setExpandable(boolean expandable) {
        this.expandable = expandable;
    }

    public Notice(String title, String detail, String date){
        //this.id = id;
        this.title = title;
        this.detail = detail;
        this.date = date;
        this.expandable = false;
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
