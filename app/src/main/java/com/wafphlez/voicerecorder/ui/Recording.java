package com.wafphlez.voicerecorder.ui;

import java.io.File;

public class Recording {
    public String name;
    public String dateCreated;
    public String duration;
    public File file;
    public int volume;
    public int pitch;

    public Recording(String name, String dateCreated, String duration, File file, int volume, int pitch){
        this.name = name;
        this.dateCreated = dateCreated;
        this.duration = duration;
        this.file = file;
        this.volume = volume;
        this.pitch = pitch;
    }
}
