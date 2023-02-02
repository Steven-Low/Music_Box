package com.example.musicbox;
import java.util.Date;

public class SleepTimer {
    long start;
    long sleep;
    long currentTime;
    public SleepTimer(){
        this(10000);
    }
    public SleepTimer(int sleep){
        start = new Date().getTime();
        this.sleep = sleep*60*1000;  // convert to milliseconds
    }

    public long getTime(){
        currentTime = new Date().getTime();
        return currentTime;
    }

    public void setTime(int sleep){
        start = new Date().getTime();
        this.sleep = sleep*60*1000;
    }

    public void reset(){
        start = new Date().getTime();
        sleep = 600_000_000;
    }
}

