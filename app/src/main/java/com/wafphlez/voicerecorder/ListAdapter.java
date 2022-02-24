package com.wafphlez.voicerecorder;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.wafphlez.voicerecorder.ui.records.RecordsFragment;

import org.w3c.dom.Text;

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

        Recording recording = Helper.GetRecording(position);
        File file = getItem(position).file;
        mp = new MediaPlayer();

        ViewHolder mainViewHolder = null;

        convertView = LayoutInflater.from(getContext()).inflate(layout, parent, false);
        TextView name = (TextView) convertView.findViewById(R.id.recordName);
        TextView date = (TextView) convertView.findViewById(R.id.recordDate);
        ImageButton deleteButton = (ImageButton) convertView.findViewById(R.id.editName);
        ImageButton playPause = (ImageButton) convertView.findViewById(R.id.playPause);

        Date lastModDate = new Date(file.lastModified());

        name.setText(file.getName().replace(".m4a", ""));
        name.setSelected(true);
        date.setText(DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM).format(lastModDate));
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder editDialog = new AlertDialog.Builder(getContext());
                editDialog.setTitle("Delete item");

                TextView input = new TextView(getContext());
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                input.setText("Are you sure you want to delete this recording?");
                input.setGravity(Gravity.CENTER);
                editDialog.setView(input);

                editDialog.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Helper.RemoveRecording(recording);
                        notifyDataSetChanged();
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


        playPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mp != null && mp.isPlaying()) {
                    try {
                        mp.reset();
                        mp.prepare();
                        mp.release();
                        mp = null;

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    playPause.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.ic_play_record_button));

                } else {
                    try {

                        mp.setDataSource(file.getPath());
                        mp.prepare();
                        mp.start();

                        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                playPause.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.ic_play_record_button));
                            }
                        });

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    playPause.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.ic_pause_record_button));

                }


            }
        });

        ViewHolder viewHolder = new ViewHolder(name, date, deleteButton, playPause);

        convertView.setTag(viewHolder);


        return convertView;
    }
}
