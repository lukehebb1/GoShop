package com.example.goshop.ui.driverhome;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import com.example.goshop.R;

public class DriverhomeFragment extends Fragment {

    private DriverhomeViewModel driverhomeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        driverhomeViewModel =
                ViewModelProviders.of(this).get(DriverhomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_driverhome, container, false);
        final TextView textView = root.findViewById(R.id.text_driverhome);
        driverhomeViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
}