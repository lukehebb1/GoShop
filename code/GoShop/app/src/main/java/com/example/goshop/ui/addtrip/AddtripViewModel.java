package com.example.goshop.ui.addtrip;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AddtripViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public AddtripViewModel() {
        mText = new MutableLiveData<>();
    }

    public LiveData<String> getText() {
        return mText;
    }
}
