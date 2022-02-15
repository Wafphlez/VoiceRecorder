package com.wafphlez.voicerecorder.ui.settings;

import android.content.ContextWrapper;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.wafphlez.voicerecorder.PlayerVisualizerView;
import com.wafphlez.voicerecorder.R;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

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

    Button nextAudioVizualizer;

    int audioCounter = 0;
    public PlayerVisualizerView playerVisualizerView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        editorViewModel =
                new ViewModelProvider(this).get(EditorViewModel.class);
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

        dateChanged = root.findViewById(R.id.dateChanged);

        editorViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });


        nextAudioVizualizer = root.findViewById(R.id.nextAudioVizualizer);


        ContextWrapper contextWrapper = new ContextWrapper(getContext());
        File musicDirectory = contextWrapper.getExternalFilesDir(Environment.DIRECTORY_MUSIC);

        File directory = new File(Environment.DIRECTORY_MUSIC);
        ArrayList<File> files = new ArrayList<>(Arrays.asList(musicDirectory.listFiles()));


        RefreshAudioInfo(files);

        nextAudioVizualizer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (audioCounter >= files.size() - 1) {
                    audioCounter = 0;
                } else {
                    audioCounter++;
                }

                RefreshAudioInfo(files);
            }
        });

        volumeSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int max = volumeSlider.getMax();
            int progressRaw;
            int progress;

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                progressRaw = seekBar.getProgress();
                progress = progressRaw - max/2;
                String value = "";

                if (progress>0){
                    value="+"+progress;
                }
                else{
                    value = progress+"";
                }
                volumeSliderValue.setText(value);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        pitchSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int max = volumeSlider.getMax();
            int progressRaw;
            int progress;

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                progressRaw = seekBar.getProgress();
                progress = progressRaw - max/2;
                String value = "";

                if (progress>0){
                    value="+"+progress;
                }
                else{
                    value = progress+"";
                }
                pitchSliderValue.setText(value);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        return root;
    }

    private void RefreshAudioInfo(ArrayList<File> files) {
        File file = files.get(audioCounter);

        Date lastModDate = new Date(file.lastModified());

        saveName.setText(file.getName().replace(".wav", ""));
        dateChanged.setText(DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM).format(lastModDate));
        int file_size = Integer.parseInt(String.valueOf(file.length() / 1024));
        size.setText(file_size + "KB");

        updateVisualizer(fileToBytes(files.get(audioCounter)));
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