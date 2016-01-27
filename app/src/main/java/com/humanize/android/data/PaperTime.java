package com.humanize.android.data;

/**
 * Created by kamal on 1/21/16.
 */
public class PaperTime {

    private int hour;
    private int minute;

    public PaperTime(int hour, int minute) {
        this.hour = hour;
        this.minute = minute;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }
}
