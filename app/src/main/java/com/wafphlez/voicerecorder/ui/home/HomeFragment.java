package com.wafphlez.voicerecorder.ui.home;

import android.Manifest;
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
    int counter = 0;

    boolean canStartRecording = true;
    boolean isRecording = false;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        View root = inflater.inflate(R.layout.fragment_home, container, false);
        final TextView textView = root.findViewById(R.id.text_home);
        timerTextView = root.findViewById(R.id.timer);

        timer = new Timer();

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
                if (!canStartRecording) {
                    return;
                }

                if (isRecording) {
                    StopRecording(v);
                } else {
                    StartRecording(v);
                }


            }
        });

        getMicrophonePermission();

        return root;
    }

    public void runtimePermission() {

        Dexter.withContext(getContext()).withPermission(Manifest.permission.RECORD_AUDIO).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {

            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                Toast.makeText(getContext(), "Please add the permission to use your microphone for app to work!", Toast.LENGTH_SHORT).show();

                PermissionListener dialogPermissionListener =
                        DialogOnDeniedPermissionListener.Builder
                                .withContext(getContext())
                                .withTitle("Camera permission")
                                .withMessage("Camera permission is needed to take pictures of your cat")
                                .withButtonText(android.R.string.ok)
                                .withIcon(R.mipmap.ic_launcher)
                                .build();
            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();
            }
        }).check();
    }

    public void StartRecording(View view) {
        try {
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_DENIED) {
                getMicrophonePermission();
                return;
            }

            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            mediaRecorder.setOutputFile(getRecordingFilePath());
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
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

    public String FormatTime(int mins, int secs) {

        if (mins < 10) {
            minutes = "0" + mins;
        }

        String seconds = String.format("%02d", secs);


        return (minutes + ":" + seconds);

    }

    public void StopRecording(View view) {
        try {
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;

            recordButton.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.ic_play_button));

            Toast.makeText(getContext(), getFileName(), Toast.LENGTH_SHORT).show();

            isRecording = false;


            timerTask.cancel();
            time = 0.0;

        } catch (Exception ex) {
        }
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
        return "Save_" + date + ".wav";
    }

}