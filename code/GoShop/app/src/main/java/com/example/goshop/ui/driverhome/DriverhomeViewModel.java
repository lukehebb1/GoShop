package com.example.goshop.ui.driverhome;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class DriverhomeViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public DriverhomeViewModel() {
        mText = new MutableLiveData<>();
    }

    public LiveData<String> getText() {
        return mText;
    }
}
