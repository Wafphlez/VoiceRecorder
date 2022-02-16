package com.wafphlez.voicerecorder;

import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class ViewHolder {
    TextView name;
    TextView date;
    ImageButton deleteButton;
    ImageButton playPauseButton;

    public ViewHolder(TextView name, TextView date, ImageButton deleteButton, ImageButton playPauseButton){
        this.name = name;
        this.date = date;
        this.deleteButton = deleteButton;
        this.playPauseButton = playPauseButton;
    }

}
