package com.chintanpatel.materialeventcalendar;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class EventModal {

    String start;
    String end;
    String title = "My Event";
    String color = "#00AFFF";
    String id = "";

    public EventModal(String id, String startDt, String endDt, String title, String color) {
        this.id = id;
        this.start = startDt;
        this.end = endDt;
        this.title = title;
        this.color = color;
    }

    public EventModal(String startDt, String endDt, String title, String color) {
        this.start = startDt;
        this.end = endDt;
        this.title = title;
        this.color = color;
    }

    public EventModal(String startDt, String endDt, String title) {
        this.start = startDt;
        this.end = endDt;
        this.title = title;
    }

    public EventModal(String startDt, String endDt) {
        this.start = startDt;
        this.end = endDt;
    }

    public Date getStartDateToSort() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        try {
            return sdf.parse(start);
        } catch (Exception e) {
            return new Date();
        }

    }
}
