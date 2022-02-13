package com.wafphlez.voicerecorder;

import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class ViewHolder {
    TextView name;
    TextView date;
    ImageButton editButton;
    ImageButton playPauseButton;

    public ViewHolder(TextView name, TextView date, ImageButton editButton, ImageButton playPauseButton){
        this.name = name;
        this.date = date;
        this.editButton = editButton;
        this.playPauseButton = playPauseButton;
    }

}
