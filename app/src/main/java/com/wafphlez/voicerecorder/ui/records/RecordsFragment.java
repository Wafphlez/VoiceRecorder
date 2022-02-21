package com.wafphlez.voicerecorder.ui.records;

import android.content.ContextWrapper;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.wafphlez.voicerecorder.Helper;
import com.wafphlez.voicerecorder.ListAdapter;
import com.wafphlez.voicerecorder.R;

import java.io.File;
import java.util.ArrayList;

public class RecordsFragment extends Fragment {

    public ListView listView;
    ListAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        RecordsViewModel recordsViewModel = new ViewModelProvider(this).get(RecordsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_records, container, false);
        final TextView textView = root.findViewById(R.id.text_records);
        recordsViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        listView = root.findViewById(R.id.listView);

        RefreshAdapter();

        return root;
    }

    public void RefreshAdapter() {
            adapter = new ListAdapter(getContext(), Helper.GetRecordings(Helper.GetFiles(getContext())));
            listView.setAdapter(adapter);
    }

    public ArrayList<File> FindAudioFile(File file) {
        ArrayList<File> arrayList = new ArrayList<>();

        File[] files = file.listFiles();

        for (File singlefile : files) {
            if (singlefile.isDirectory() && !singlefile.isHidden()) {
                arrayList.addAll(FindAudioFile(singlefile));
            } else {
                if (singlefile.getName().endsWith(".m4a")) {
                    arrayList.add(singlefile);
                }
            }
        }
        return arrayList;
    }
}