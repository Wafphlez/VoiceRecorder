package com.wafphlez.voicerecorder;

import java.io.File;
import java.util.Date;

public class Recording {
    public String name;
    public Date date;
    public String duration;
    public File file;
    public int speed;
    public int pitch;

    public Recording(String name, Date date, String duration, File file, int speed, int pitch){
        this.name = name;
        this.date = date;
        this.duration = duration;
        this.file = file;
        this.speed = speed;
        this.pitch = pitch;
    }

    public void SetSpeed(int speed){
        this.speed = speed;
    }

    public void SetPitch(int pitch){
        this.pitch = pitch;
    }
}
