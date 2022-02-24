package com.wafphlez.voicerecorder.ui.home;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContextWrapper;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
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

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.DialogOnDeniedPermissionListener;
import com.karumi.dexter.listener.single.PermissionListener;
import com.wafphlez.voicerecorder.Helper;
import com.wafphlez.voicerecorder.R;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;

    private static int MICROPHONE_PERMISSION_CODE = 200;
    private final static int ONE_SECOND = 1000;

    MediaRecorder mediaRecorder;
    ImageButton recordButton;

    TextView timerTextView;
    String minutes;
    String seconds;
    Timer timer;
    TimerTask timerTask;

    Double time = 0.0;

    boolean canStartRecording = true;
    boolean isRecording = false;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        View root = inflater.inflate(R.layout.fragment_home, container, false);
        final TextView textView = root.findViewById(R.id.text_home);
        timerTextView = root.findViewById(R.id.timer);
        recordButton = root.findViewById(R.id.recordButton);

        timer = new Timer();
        getMicrophonePermission();

        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!canStartRecording) {
                    return;
                }

                if (!isRecording) {
                    StartRecording();
                } else {
                    StopRecording();
                }
            }
        });


        return root;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        StopRecording();
    }

    public void StartRecording() {
        try {
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_DENIED) {
                getMicrophonePermission();
                return;
            }

            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
            mediaRecorder.setAudioEncodingBitRate(16*44100);
            mediaRecorder.setAudioSamplingRate(44100);
            mediaRecorder.setOutputFile(getRecordingFilePath());
            mediaRecorder.prepare();
            mediaRecorder.start();

            recordButton.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.ic_pause_button));

            isRecording = true;

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        timerTask = new TimerTask() {
            @Override
            public void run() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        timerTextView.setText(GetTimerText());
                        time++;
                    }
                });
            }
        };
        timer.scheduleAtFixedRate(timerTask, 0, ONE_SECOND);
    }

    public String GetTimerText() {
        int rounded = (int) Math.round(time);

        int secs = ((rounded % 86400) % 3600) % 60;
        int mins = ((rounded % 86400) % 3600) / 60;

        return FormatTime(mins, secs);
    }

    @SuppressLint("DefaultLocale")
    public String FormatTime(int mins, int secs) {

        if (mins < 10) {
            minutes = "0" + mins;
        }

        seconds = String.format("%02d", secs);


        return (minutes + ":" + seconds);

    }

    public void StopRecording() {
        try {
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;

            recordButton.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.ic_play_button));

            Toast.makeText(getContext(), getFileName(), Toast.LENGTH_SHORT).show();

            isRecording = false;


            timerTask.cancel();
            time = 0.0;
            timerTextView.setText(GetTimerText());
            Helper.GetRecordings(Helper.GetFiles(getContext()));

        } catch (Exception ex) { ex.printStackTrace(); }
    }

    private boolean isMicrophonePresent() {
        return getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_MICROPHONE);
    }

    private void getMicrophonePermission() {
        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.RECORD_AUDIO}, MICROPHONE_PERMISSION_CODE);
    }

    private String getRecordingFilePath() {
        ContextWrapper contextWrapper = new ContextWrapper(getContext());
        File musicDirectory = contextWrapper.getExternalFilesDir(Environment.DIRECTORY_MUSIC);

        File file = new File(musicDirectory, getFileName());
        return file.getPath();
    }

    private String getFileName() {
        String date = new SimpleDateFormat("dd.MM.yyyy_HH-mm-ss", Locale.getDefault()).format(new Date());
        return "Save_" + date + ".m4a";
    }

}