package com.wafphlez.voicerecorder.ui.records;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class RecordsViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public RecordsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Records");
    }

    public LiveData<String> getText() {
        return mText;
    }
}