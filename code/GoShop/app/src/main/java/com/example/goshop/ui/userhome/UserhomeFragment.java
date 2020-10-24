package com.example.goshop.ui.userhome;

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

public class UserhomeFragment extends Fragment {

    private UserhomeViewModel userhomeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        userhomeViewModel =
                ViewModelProviders.of(this).get(UserhomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_userhome, container, false);
        final TextView textView = root.findViewById(R.id.text_userhome);
        userhomeViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
}