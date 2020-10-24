package com.example.goshop.ui.mytrips;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MytripsViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public MytripsViewModel() {
        mText = new MutableLiveData<>();
    }

    public LiveData<String> getText() {
        return mText;
    }
}

