package com.example.goshop.ui.mytrips;

import android.location.Address;
import android.location.Geocoder;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class MytripsFragment extends Fragment {

    private MytripsViewModel mytripsViewModel;
    private ListView listView;
    private FirebaseDatabase database;
    private DatabaseReference ref;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mytripsViewModel =
                ViewModelProviders.of(this).get(MytripsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_mytrips, container, false);
        final TextView textView = root.findViewById(R.id.text_mytrips);

        mytripsViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        listView = (ListView) root.findViewById(R.id.listview);//
        final ArrayList<String> arrayList=new ArrayList<>();
        final ArrayAdapter arrayAdapter = new ArrayAdapter(getActivity(),android.R.layout.simple_list_item_1, arrayList);//
        listView.setAdapter(arrayAdapter);

        final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        database = FirebaseDatabase.getInstance();
        ref = database.getReference("Accounts/" + uid + "/trips");

        //loop through added trips and display them
        ref.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int tripNum = 1;
                for (DataSnapshot trips : dataSnapshot.getChildren()){
                    arrayList.add("Trip " + tripNum + ":");
                    for(DataSnapshot tripSnapshot: trips.getChildren()){
                        Log.d(TAG, "Values are " + tripSnapshot.getValue());
                        StringBuilder strBuilder = new StringBuilder(tripSnapshot.getKey() + ": ");
                        if (tripSnapshot.hasChildren()){
                            //take note of location lat and lng
                            double lat = tripSnapshot.child("latitude").getValue(double.class);
                            double longitude = tripSnapshot.child("longitude").getValue(double.class);
                            Log.d(TAG, "LatLng: " + lat + " " + longitude);
                            try {
                                //reverse geocoding
                                Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
                                List<Address> addresses = geocoder.getFromLocation(lat, longitude, 1);
                                Log.d(TAG, addresses.get(0).getAddressLine(0));
                                arrayList.add(tripSnapshot.getKey() + ": " + addresses.get(0).getAddressLine(0));
                                arrayAdapter.notifyDataSetChanged();
                            }
                            catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            strBuilder.append(tripSnapshot.getValue(String.class));
                            String value = strBuilder.toString();
                            arrayList.add(value);
                            arrayAdapter.notifyDataSetChanged();
                        }
                    }
                    tripNum++;
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