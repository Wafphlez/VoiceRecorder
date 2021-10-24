package com.wafphlez.voicerecorder;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class ListAdapter extends ArrayAdapter<File> {

    public ListAdapter(@NonNull Context context, ArrayList<File> files) {
        super(context, R.layout.record_item, files);
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        File file = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.record_item, parent, false);
        }


        TextView recordName = convertView.findViewById(R.id.recordName);
        TextView currentTime = convertView.findViewById(R.id.currentTime);
        TextView recordTime = convertView.findViewById(R.id.recordTime);

        SeekBar seekBar = convertView.findViewById(R.id.seekBar);

        ImageButton stop = convertView.findViewById(R.id.stopButton);
        ImageButton playPause = convertView.findViewById(R.id.playPause);


        recordName.setText(file.getName());

        Uri uri = Uri.parse(file.getAbsolutePath());
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(getContext(), uri);
        String durationStr = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        int milliseconds = Integer.parseInt(durationStr);

        int seconds = (int) (milliseconds / 1000) % 60 ;
        int minutes = (int) ((milliseconds / (1000*60)) % 60);
        int hours   = (int) ((milliseconds / (1000*60*60)) % 24);

        String time = minutes + ":" + seconds;
        recordTime.setText(time);



        MediaPlayer mp = new MediaPlayer();

        playPause.setOnClickListener(new View.OnClickListener(){

            public void onClick(View v) {
                try {
                    mp.setDataSource(file.getAbsoluteFile() + "/" + file.getName());

                    mp.prepare();
                    mp.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Toast.makeText(getContext(), recordName.getText(), Toast.LENGTH_SHORT).show();
            }
        });

        stop.setOnClickListener(new View.OnClickListener(){

            public void onClick(View v) {
                mp.stop();
                Toast.makeText(getContext(), recordName.getText(), Toast.LENGTH_SHORT).show();
            }
        });


        return convertView;
    }
}
