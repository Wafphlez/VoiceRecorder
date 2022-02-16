package com.wafphlez.voicerecorder;

import android.content.Context;
import android.content.ContextWrapper;
import android.os.Environment;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class Helper {

    private static ArrayList<Recording> recordings = new ArrayList<>();
    private static ArrayList<Recording> recordingsFiles = new ArrayList<>();
    private static ArrayList<File> files = new ArrayList<>();

    public static ArrayList<File> GetFiles(Context context) {
        ContextWrapper contextWrapper = new ContextWrapper(context);
        File musicDirectory = contextWrapper.getExternalFilesDir(Environment.DIRECTORY_MUSIC);
        files = new ArrayList<>(Arrays.asList(musicDirectory.listFiles()));
        return files;
    }


    public static ArrayList<Recording> GetRecordings(ArrayList<File> files) {

        for (int i = 0; i < files.size(); i++) {
            File file = files.get(i);

            if (recordingsFiles.contains(file)){
                continue;
            }

            Recording recording = new Recording(file.getName(), new Date(file.lastModified()), "?", file, 0, 0);
            recordings.add(recording);
        }

        return recordings;
    }

    public static Recording GetRecording(int index){
        return recordings.get(index);
    }

    public static String GetAudioName(String name) {
        return name.replace(".wav", "");
    }



}
