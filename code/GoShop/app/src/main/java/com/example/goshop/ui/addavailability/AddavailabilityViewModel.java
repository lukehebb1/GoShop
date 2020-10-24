package com.example.goshop.ui.addavailability;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AddavailabilityViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public AddavailabilityViewModel() {
        mText = new MutableLiveData<>();
    }

    public LiveData<String> getText() {
        return mText;
    }
}