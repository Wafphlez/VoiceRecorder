package com.wafphlez.voicerecorder.ui.editor;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class EditorViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public EditorViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Editor");
    }

    public LiveData<String> getText() {
        return mText;
    }
}