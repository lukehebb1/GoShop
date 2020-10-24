package com.example.goshop.ui.settings;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.goshop.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static androidx.constraintlayout.widget.Constraints.TAG;


public class SettingsFragment extends Fragment {


    private SettingsViewModel settingsViewModel;
    private ListView listView;
    private FirebaseDatabase database;
    private DatabaseReference ref;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        settingsViewModel =
                ViewModelProviders.of(this).get(SettingsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_settings, container, false);
        final TextView textView = root.findViewById(R.id.text_settings);


        settingsViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        listView = (ListView) root.findViewById(R.id.listview);
        final ArrayList<String> arrayList=new ArrayList<>();
        final ArrayAdapter arrayAdapter = new ArrayAdapter(getActivity(),android.R.layout.simple_list_item_1, arrayList);//
        listView.setAdapter(arrayAdapter);

        final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        database = FirebaseDatabase.getInstance();
        ref = database.getReference("Accounts/" + uid);

        //loop through account details
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot accSnapshot : dataSnapshot.getChildren()) {
                    if (!((accSnapshot.getKey().equals("trips")) || (accSnapshot.getKey().equals("availability"))
                            || (accSnapshot.getKey().equals("match")) || (accSnapshot.getKey().equals("confirmedTrip")))){
                        Log.d(TAG, "Account details " + accSnapshot.getValue());
                        //add account details to arraylist
                        StringBuilder strBuilder = new StringBuilder(accSnapshot.getKey() + " : ");
                        strBuilder.append(accSnapshot.getValue(String.class));
                        String value = strBuilder.toString();
                        arrayList.add(value);
                        arrayAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());

            }
        });
        return root;
    }
}
