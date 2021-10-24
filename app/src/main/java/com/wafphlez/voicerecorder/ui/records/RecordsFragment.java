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

import com.wafphlez.voicerecorder.ListAdapter;
import com.wafphlez.voicerecorder.R;
import com.wafphlez.voicerecorder.databinding.ActivityMainBinding;
import com.wafphlez.voicerecorder.databinding.FragmentRecordsBinding;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class RecordsFragment extends Fragment {

    private RecordsViewModel recordsViewModel;
    ListView listView;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        recordsViewModel = new ViewModelProvider(this).get(RecordsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_records, container, false);
        final TextView textView = root.findViewById(R.id.text_records);
        recordsViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });


        ContextWrapper contextWrapper = new ContextWrapper(getContext());
        File musicDirectory = contextWrapper.getExternalFilesDir(Environment.DIRECTORY_MUSIC);


        File directory = new File(Environment.DIRECTORY_MUSIC);
        //File[] files = musicDirectory.listFiles();
        ArrayList<File> files = new ArrayList<>(Arrays.asList(musicDirectory.listFiles()));

        listView = root.findViewById(R.id.listView);

        //ArrayAdapter<File> adapter = new ArrayAdapter<File>(getActivity(), R.layout.record_item, files);

        ListAdapter adapter = new ListAdapter(getContext(), files);

        listView.setAdapter(adapter);

        return root;
    }
}