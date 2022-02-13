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

public class ListAdapter extends ArrayAdapter<File> {

    private final int layout;
    boolean isPlaying;
    MediaPlayer mp = null;

    public ListAdapter(@NonNull Context context, ArrayList<File> files) {
        super(context, R.layout.record_item, files);
        layout = R.layout.record_item;

    }


    public void audioPlayer(String path, String fileName) {
        //set up MediaPlayer
        MediaPlayer mp = new MediaPlayer();

        try {
            mp.setDataSource(path + File.separator + fileName);
            mp.prepare();
            mp.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        File file = getItem(position);

        ViewHolder mainViewHolder = null;

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(layout, parent, false);

            TextView name = (TextView) convertView.findViewById(R.id.recordName);
            TextView date = (TextView) convertView.findViewById(R.id.recordDate);
            ImageButton edit = (ImageButton) convertView.findViewById(R.id.editName);
            ImageButton play = (ImageButton) convertView.findViewById(R.id.playPause);

            edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(getContext(), name.getText(), Toast.LENGTH_SHORT).show();

                    AlertDialog.Builder editDialog = new AlertDialog.Builder(getContext());

                    editDialog.setTitle("Record title");

                    EditText input = new EditText(getContext());
                    input.setInputType(InputType.TYPE_CLASS_TEXT);
                    input.setText(name.getText().toString());

                    editDialog.setView(input);

                    editDialog.setPositiveButton("Apply", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
//                            name.setText(input.getText().toString());
//                            File newFile = new File(file.getPath().replace("/" + name.getText(), ""), input.getText().toString());
//                            file.renameTo(newFile);
//
//                            file = new File(newFile.toString());
//
//                            File ffile = new File(file.toString());
//                            ffile=newFile;
//
//                            file.delete();
                            //name.setText(file.getName());
                            Toast.makeText(getContext(), file.getName(), Toast.LENGTH_SHORT).show();
                            //file = newFile;

                        }
                    });

                    editDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });
                    editDialog.show();
                }
            });

            play.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (isPlaying) {
                        try {
                            mp.reset();
                            mp.prepare();
                            mp.stop();
                            mp.release();
                            mp = null;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        play.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.ic_play_record_button));

                        isPlaying = false;
                    } else {
                        try {
                            mp = new MediaPlayer();

                            mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                                @Override
                                public void onCompletion(MediaPlayer mp) {

                                    play.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.ic_play_record_button));

                                    isPlaying = false;
                                }

                            });

                            mp.setDataSource(file.getPath());
                            mp.prepare();
                            mp.start();
                            play.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.ic_pause_record_button));

                            isPlaying = true;

                            Toast.makeText(getContext(), name.getText() + " playing...", Toast.LENGTH_SHORT).show();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }


                }
            });

            ViewHolder viewHolder = new ViewHolder(name, date, edit, play);

            convertView.setTag(viewHolder);
        } else {
            mainViewHolder = (ViewHolder) convertView.getTag();
            mainViewHolder.name.setText(getItem(position).getName());
        }

        TextView name = (TextView) convertView.findViewById(R.id.recordName);
        TextView date = (TextView) convertView.findViewById(R.id.recordDate);

        Date lastModDate = new Date(file.lastModified());

        name.setText(file.getName().replace(".wav", ""));
        date.setText(DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM).format(lastModDate));

//        Uri uri = Uri.parse(file.getAbsolutePath());
//        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
//        mmr.setDataSource(getContext(), uri);
//        String durationStr = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
//        int milliseconds = Integer.parseInt(durationStr);
//
//        int seconds = (int) (milliseconds / 1000) % 60;
//        int minutes = (int) ((milliseconds / (1000 * 60)) % 60);
//
//        String time = minutes + ":" + seconds;
//
//
//        MediaPlayer mp = new MediaPlayer();

//        playPause.setOnClickListener(new View.OnClickListener(){
//
//            public void onClick(View v) {
//                try {
//                    mp.setDataSource(file.getAbsoluteFile() + "/" + file.getName());
//
//                    mp.prepare();
//                    mp.start();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//                Toast.makeText(getContext(), recordName.getText(), Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        stop.setOnClickListener(new View.OnClickListener(){
//
//            public void onClick(View v) {
//                mp.stop();
//                Toast.makeText(getContext(), recordName.getText(), Toast.LENGTH_SHORT).show();
//            }
//        });


        return convertView;
    }
}
