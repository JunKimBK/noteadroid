package com.dut.note.lib;

import java.util.ArrayList;

/**
 * Created by VietHoang on 21/11/2015.
 */
public class DataUi {
    private String title;
    private String date;
    private String time;
    private ArrayList<String> job;

    public DataUi(String title, ArrayList<String> job, String date, String time) {
        this.title = new String(title);
        this.date = new String(date);
        this.time = new String(time);
        this.job = new ArrayList<>(job);
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

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public ArrayList<String> getJob() {
        return job;
    }

    public void setJob(ArrayList<String> job) {
        this.job = job;
    }
}
