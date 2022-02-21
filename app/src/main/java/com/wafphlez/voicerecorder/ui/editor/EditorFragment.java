package com.wafphlez.voicerecorder.ui.editor;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.PlaybackParams;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
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
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class EditorFragment extends Fragment {

    private EditorViewModel editorViewModel;

    TextView saveName;

    TextView dateChanged;
    TextView size;
    TextView length;

    TextView volumeSliderValue;
    TextView pitchSliderValue;

    TextView currentTime;
    TextView allTime;

    SeekBar volumeSlider;
    SeekBar pitchSlider;
    SeekBar audioSlider;

    ImageButton nextSkip;
    ImageButton prevSkip;
    ImageButton playPause;
    ImageButton editName;

    Recording recording;
    Handler handler = new Handler();
    Runnable runnable;
    MediaPlayer mp = new MediaPlayer();

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

        currentTime = root.findViewById(R.id.currentTime);
        allTime = root.findViewById(R.id.allTime);

        prevSkip = root.findViewById(R.id.prevSkip);
        playPause = root.findViewById(R.id.playPause);
        nextSkip = root.findViewById(R.id.nextSkip);
        audioSlider = root.findViewById(R.id.audioSlider);

        editorViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        RefreshAudioInfo();


        runnable = new Runnable() {
            @Override
            public void run() {
                audioSlider.setProgress(mp.getCurrentPosition());
                currentTime.setText(ConvertToTime(mp.getCurrentPosition()));
                handler.postDelayed(this, 250);
            }
        };

        playPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mp.isPlaying()) {
                    //play
                    playAudio();
                } else {
                    //pause
                    pauseAudio();

                }
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

        prevSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (audioCounter <= 0) {
                    audioCounter = Helper.GetFiles(getContext()).size() - 1;
                } else {
                    audioCounter--;
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
                recording.SetVolume(seekBar.getProgress() + seekBar.getMax() / 2);

                AudioManager audioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
                int volumeLevel = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                int maxVolumeLevel = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                int volumePercent = (int) (((float) volumeLevel / maxVolumeLevel));
                Toast.makeText(getContext(), volumeLevel * seekBar.getProgress() / (seekBar.getMax() / 2) + "", 0).show();
                mp.setVolume(volumeLevel * seekBar.getProgress() / (seekBar.getMax() / 2),
                        volumeLevel * seekBar.getProgress() / (seekBar.getMax() / 2));

            }
        });

        pitchSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                SetSliderValue(seekBar, pitchSliderValue);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //pauseAudio();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                recording.SetPitch(seekBar.getProgress());

                //Toast.makeText(getContext(), (float)recording.pitch / (float)pitchSlider.getMax() + "", 0).show();

                PlaybackParams params = new PlaybackParams();
                params.setPitch((float) (recording.pitch / 10f));

                if (mp.isPlaying()) {
                    pauseAudio();
                    mp.stop();
                    try {
                        mp.prepare();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    mp.setPlaybackParams(params);
                    playAudio();
                } else {

                    mp.setPlaybackParams(params);
                    pauseAudio();
                }


            }
        });

        audioSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mp.seekTo(seekBar.getProgress());
            }
        });

        return root;
    }

    private void pauseAudio() {
        try {
            mp.pause();
            playPause.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.ic_play_record_button));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void playAudio() {
        mp.start();
        mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
        audioSlider.setMax(mp.getDuration());
        handler.postDelayed(runnable, 0);

        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                playPause.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.ic_play_record_button));
            }
        });

        playPause.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.ic_pause_record_button));
    }

    private void stopAudio() {
        playPause.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.ic_play_record_button));
        mp.reset();
        mp.release();
        mp = new MediaPlayer();

        try {
            mp.setDataSource(Helper.GetRecording(audioCounter).file.getPath());
            mp.prepare();
        } catch (Exception ex) {
            allTime.setText("00:00");
            return;
        }
    }

    private String ConvertToTime(int duration) {
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(duration),
                TimeUnit.MILLISECONDS.toSeconds(duration),
                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration)));
    }

    private void RefreshAudioInfo() {
        if (Helper.GetFiles(getContext()).size() == 0) {
            return;
        }
        recording = Helper.GetRecording(audioCounter);

        File file = recording.file;

        Date lastModDate = new Date(file.lastModified());

        saveName.setText(Helper.GetAudioName(file.getName()));
        dateChanged.setText(DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM).format(lastModDate));
        volumeSlider.setProgress(recording.volume + pitchSlider.getMax() / 2);
        SetSliderValue(volumeSlider, volumeSliderValue);
        SetSliderValue(pitchSlider, pitchSliderValue);
        pitchSlider.setProgress(recording.pitch + pitchSlider.getMax() / 2);
        int file_size = Integer.parseInt(String.valueOf(file.length() / 1024));
        size.setText(file_size + "KB");
        updateVisualizer(fileToBytes(file));

        stopAudio();

        int duration = mp.getDuration();
        String sDuration = ConvertToTime(duration);
        allTime.setText(sDuration);
        length.setText(sDuration);
    }


    public void SetSliderValue(SeekBar seekBar, TextView textView) {
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