package com.wafphlez.voicerecorder;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class ListAdapter extends ArrayAdapter<Recording> {

    private final int layout;
    boolean isPlaying;
    MediaPlayer mp = null;
    ImageButton prevPlayed;

    public ListAdapter(@NonNull Context context, ArrayList<Recording> recordings) {
        super(context, R.layout.record_item, recordings);
        layout = R.layout.record_item;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        File file = getItem(position).file;

        ViewHolder mainViewHolder = null;

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(layout, parent, false);

            TextView name = (TextView) convertView.findViewById(R.id.recordName);
            TextView date = (TextView) convertView.findViewById(R.id.recordDate);
            ImageButton deleteButton = (ImageButton) convertView.findViewById(R.id.editName);
            ImageButton play = (ImageButton) convertView.findViewById(R.id.playPause);

            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });

            play.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (mp != null && mp.isPlaying()) {
                        try {
                            mp.reset();
                            mp.prepare();
                            mp.stop();
                            mp.release();
                            mp = null;

                            play.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.ic_play_record_button));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    else {
                        try {
                            mp = new MediaPlayer();

                            mp.setDataSource(file.getPath());
                            mp.prepare();
                            mp.start();

                            mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                @Override
                                public void onCompletion(MediaPlayer mp) {
                                    play.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.ic_play_record_button));
                                }
                            });

                            play.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.ic_pause_record_button));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }


                }
            });

            ViewHolder viewHolder = new ViewHolder(name, date, deleteButton, play);

            convertView.setTag(viewHolder);
        } else {
            mainViewHolder = (ViewHolder) convertView.getTag();
            mainViewHolder.name.setText(getItem(position).file.getName());
        }

        TextView name = (TextView) convertView.findViewById(R.id.recordName);
        TextView date = (TextView) convertView.findViewById(R.id.recordDate);

        Date lastModDate = new Date(file.lastModified());

        name.setText(file.getName().replace(".wav", ""));
        date.setText(DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM).format(lastModDate));

        return convertView;
    }
}
