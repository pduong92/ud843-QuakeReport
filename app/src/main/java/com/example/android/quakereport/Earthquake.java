package com.example.android.quakereport;

import java.sql.Date;
import java.text.SimpleDateFormat;

/**
 * Created by phduo on 4/8/2017.
 */

public class Earthquake {
    private double magnitude;
    private String location;
    private long timeInMiliseconds;
    private Date date;
    private String URL;

    private String[] months = {" ", "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug" ,"Sep", "Oct", "Nov", "Dec"};

    public Earthquake(double mag, String loc, long time) {
        this.magnitude = mag;
        this.location = loc;
        this.timeInMiliseconds = time;
        this.date = new Date(time);
    }

    public Earthquake(double mag, String loc, Date d) {
        this.magnitude = mag;
        this.location = loc;
        this.date = d;
    }

    public Earthquake(double mag, String loc, String date) {
        this.magnitude = mag;
        this.location = loc;
        this.date = Date.valueOf(date);
    }

    public Earthquake(double mag, String loc, long time, String url) {
        this.magnitude = mag;
        this.location = loc;
        this.timeInMiliseconds = time;
        this.date = new Date(time);
        this.URL = url;
    }

    public double getMagnitude() {
        return magnitude;
    }

    public String getLocation() {
        return location;
    }

    public long getTimeInMiliseconds() {
        return timeInMiliseconds;
    }

    public String getDate() {
        String date = this.date.toString();

        String[] comps = date.split("-");
        int year = Integer.parseInt(comps[0]);
        int month = Integer.parseInt(comps[1]);
        int day = Integer.parseInt(comps[2]);

        return months[month] + " " + day + ", " + year;
    }

    public String getTime() {
        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");

        return timeFormat.format(date);
    }

    public String getURL() {
        return URL;
    }
}
