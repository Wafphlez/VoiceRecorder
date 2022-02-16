package com.wafphlez.voicerecorder;

import java.io.File;
import java.util.Date;

public class Recording {
    public String name;
    public Date date;
    public String duration;
    public File file;
    public int volume;
    public int pitch;

    public Recording(String name, Date date, String duration, File file, int volume, int pitch){
        this.name = name;
        this.date = date;
        this.duration = duration;
        this.file = file;
        this.volume = volume;
        this.pitch = pitch;
    }

    public void SetVolume(int volume){
        this.volume = volume;
    }

    public void SetPitch(int pitch){
        this.pitch = pitch;
    }
}
