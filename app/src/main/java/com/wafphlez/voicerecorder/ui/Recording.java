package com.wafphlez.voicerecorder.ui;

import java.io.File;

public class Recording {
    public String name;
    public String dateCreated;
    public String duration;
    public File file;

    public Recording(String name, String dateCreated, String duration, File file){
        this.name = name;
        this.dateCreated = dateCreated;
        this.duration = duration;
        this.file = file;
    }
}
