package com.example.goshop.ui.userhome;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class UserhomeViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public UserhomeViewModel() {
        mText = new MutableLiveData<>();
    }

    public LiveData<String> getText() {
        return mText;
    }
}