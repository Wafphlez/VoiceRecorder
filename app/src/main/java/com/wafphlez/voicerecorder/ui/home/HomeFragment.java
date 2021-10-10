package com.wafphlez.voicerecorder.ui.home;

import android.Manifest;
import android.content.ContextWrapper;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.wafphlez.voicerecorder.R;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;

    private static int MICROPHONE_PERMISSION_CODE = 200;
    MediaRecorder mediaRecorder;
    boolean isRecording = false;
    ImageButton recordButton;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        View root = inflater.inflate(R.layout.fragment_home, container, false);
        final TextView textView = root.findViewById(R.id.text_home);
        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        recordButton = (ImageButton) root.findViewById(R.id.recordButton);
        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                record(v);
            }
        });

        getMicrophonePermission();

        return root;

    }


    public void record(View view) {

        if (!isRecording) {
            try {
                getMicrophonePermission();

                mediaRecorder = new MediaRecorder();
                mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
                mediaRecorder.setOutputFile(getRecordingFilePath());
                mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
                mediaRecorder.prepare();
                mediaRecorder.start();

                recordButton.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.ic_pause_button));

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;

            recordButton.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.ic_play_button));

            Toast.makeText(getContext(), getFileName(), Toast.LENGTH_SHORT).show();
        }
        isRecording = !isRecording;
    }

    private void getMicrophonePermission() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.RECORD_AUDIO}, MICROPHONE_PERMISSION_CODE);
        }
    }

    private String getRecordingFilePath() {
        ContextWrapper contextWrapper = new ContextWrapper(getContext());
        File musicDirectory = contextWrapper.getExternalFilesDir(Environment.DIRECTORY_MUSIC);

        File file = new File(musicDirectory, getFileName());
        return file.getPath();
    }

    private  String getFileName(){
        String date = new SimpleDateFormat("dd.MM.yyyy_HH-mm-ss", Locale.getDefault()).format(new Date());
       return "Save_" + date + ".wav";
    }

}