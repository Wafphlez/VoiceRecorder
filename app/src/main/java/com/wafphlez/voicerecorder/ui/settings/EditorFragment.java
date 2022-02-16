package com.wafphlez.voicerecorder.ui.settings;

import android.app.AlertDialog;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.wafphlez.voicerecorder.Helper;
import com.wafphlez.voicerecorder.PlayerVisualizerView;
import com.wafphlez.voicerecorder.R;
import com.wafphlez.voicerecorder.Recording;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.stream.Stream;

public class EditorFragment extends Fragment {

    private EditorViewModel editorViewModel;

    TextView saveName;

    TextView dateChanged;
    TextView size;
    TextView length;

    TextView volumeSliderValue;
    TextView pitchSliderValue;

    SeekBar volumeSlider;
    SeekBar pitchSlider;

    ImageButton nextSkip;
    ImageButton prevSkip;
    ImageButton playPause;
    ImageButton editName;

    Recording recording;

    int audioCounter = 0;
    public PlayerVisualizerView playerVisualizerView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        editorViewModel = new ViewModelProvider(this).get(EditorViewModel.class);
        View root = inflater.inflate(R.layout.fragment_editor, container, false);
        final TextView textView = root.findViewById(R.id.text_editor);

        playerVisualizerView = root.findViewById(R.id.visualizer);
        saveName = root.findViewById(R.id.saveName);
        size = root.findViewById(R.id.size);
        length = root.findViewById(R.id.length);
        volumeSlider = root.findViewById(R.id.volumeSlider);
        pitchSlider = root.findViewById(R.id.pitchSlider);
        volumeSliderValue = root.findViewById(R.id.volumeSliderValue);
        pitchSliderValue = root.findViewById(R.id.pitchSliderValue);
        editName = root.findViewById(R.id.editName);
        dateChanged = root.findViewById(R.id.dateChanged);
        nextSkip = root.findViewById(R.id.nextSkip);

        editorViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });


        editName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), saveName.getText(), Toast.LENGTH_SHORT).show();

                AlertDialog.Builder editDialog = new AlertDialog.Builder(getContext());

                editDialog.setTitle("Record title");

                EditText input = new EditText(getContext());
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                input.setText(saveName.getText().toString());

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

                        Toast.makeText(getContext(), Helper.GetRecordings(Helper.GetFiles(getContext())).get(i).file.getName(), Toast.LENGTH_SHORT).show();

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


        RefreshAudioInfo();

        nextSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (audioCounter >= Helper.GetFiles(getContext()).size() - 1) {
                    audioCounter = 0;
                } else {
                    audioCounter++;
                }

                RefreshAudioInfo();
            }
        });

        volumeSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                SetSliderValue(seekBar, volumeSliderValue);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                recording.SetVolume(seekBar.getProgress());
            }
        });

        pitchSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                SetSliderValue(seekBar, pitchSliderValue);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                recording.SetPitch(seekBar.getProgress());

                Toast.makeText(getContext(),Helper.GetRecordings(Helper.GetFiles(getContext()))
                                                .get(audioCounter).pitch + " " + recording.pitch, Toast.LENGTH_SHORT).show();
            }
        });


        return root;
    }

    private void RefreshAudioInfo() {

        recording = Helper.GetRecording(audioCounter);

        File file = recording.file;

        Date lastModDate = new Date(file.lastModified());

        saveName.setText(Helper.GetAudioName(file.getName()));
        dateChanged.setText(DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM).format(lastModDate));
        volumeSlider.setProgress(recording.volume);
        SetSliderValue(volumeSlider, volumeSliderValue);
        SetSliderValue(pitchSlider, pitchSliderValue);
        pitchSlider.setProgress(recording.pitch);
        int file_size = Integer.parseInt(String.valueOf(file.length() / 1024));
        size.setText(file_size + "KB");

        updateVisualizer(fileToBytes(file));

    }

    public void SetSliderValue(SeekBar seekBar, TextView textView)
    {
        int max = seekBar.getMax();
        int progressRaw;
        int progress;

        progressRaw = seekBar.getProgress();
        progress = progressRaw - max / 2;
        String value = "";

        if (progress > 0) {
            value = "+" + progress;
        } else {
            value = progress + "";
        }
        textView.setText(value);
    }

    public void updateVisualizer(byte[] bytes) {
        playerVisualizerView.updateVisualizer(bytes);
    }

    public void updatePlayerProgress(float percent) {
        playerVisualizerView.updatePlayerPercent(percent);
    }

    public static byte[] fileToBytes(File file) {
        int size = (int) file.length();
        byte[] bytes = new byte[size];
        try {
            BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
            buf.read(bytes, 0, bytes.length);
            buf.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bytes;
    }
}