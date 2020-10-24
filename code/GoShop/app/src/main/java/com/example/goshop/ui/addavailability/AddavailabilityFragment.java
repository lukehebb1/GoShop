package com.example.goshop.ui.addavailability;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.goshop.DriverHomeActivity;
import com.example.goshop.R;
import com.example.goshop.Availability;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;

import java.util.Arrays;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddavailabilityFragment extends Fragment {

    private AddavailabilityViewModel addavailabilityViewModel;
    private LatLng startLatLng;
    private Availability availability;
    private String TAG = "AddavailabilityFragment";
    private EditText timeSlot, date, carType, travelDistance;
    private Button btnAddAvailability, btnTestAlg;
    private FirebaseDatabase database;
    private DatabaseReference ref, pushedAvailRef;
    private String tCarType, tDate, tTimeSlot, aCarType, aDate, aTimeSlot, uid;
    private int tStartTime, tStartTimeHours, tStartTimeMins, tEndTimeHours, tEndTimeMins, tEndTime,
            aStartTime, aStartTimeHours, aStartTimeMins, aEndTimeHours, aEndTimeMins, aEndTime, travelDist;
    private double latDStartPoint, longDStartPoint, latDestination, longDestination, latUPickUpPoint, longUPickUpPoint, totalDistance = 0;
    public static Point uPickUpPoint, dStartPoint, destination;
    double[] distances = new double[3];
    private View root;
    private boolean match;
    public static String userID, tripID;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        addavailabilityViewModel =
                ViewModelProviders.of(this).get(AddavailabilityViewModel.class);
        root = inflater.inflate(R.layout.fragment_addavailability, container, false);
        final TextView textView = root.findViewById(R.id.text_addavailability);

        addavailabilityViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        Mapbox.getInstance(getActivity(), getString(R.string.mapbox_access_token));

        //instantiate add trip form objects
        date = (EditText) root.findViewById(R.id.date);
        timeSlot = (EditText) root.findViewById(R.id.timeSlot);
        carType = (EditText) root.findViewById(R.id.typeOfCar);
        travelDistance = (EditText) root.findViewById(R.id.travelDist);
        btnAddAvailability = (Button) root.findViewById(R.id.btnAddAvailability);

        //Google Places API key
        String apiKey = getString(R.string.places_key);

        //Initialize Places
        if (!Places.isInitialized()) {
            Places.initialize(getActivity().getApplicationContext(), apiKey);
        }

        // Create a new Places client instance.
        PlacesClient placesClient = Places.createClient(getActivity());

        // Initialize the pickup AutocompleteSupportFragment.
        AutocompleteSupportFragment startAutocompleteFragment = (AutocompleteSupportFragment)
                getChildFragmentManager().findFragmentById(R.id.autocomplete_fragment_start);

        // Specify the types of place data to return.
        startAutocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.LAT_LNG, Place.Field.NAME));

        startAutocompleteFragment.setHint("Start point");
        startAutocompleteFragment.setCountry("IE");

        // Set up a PlaceSelectionListener to handle the response.
        startAutocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.i(TAG, "Place: " + place.getName() + ", " + place.getLatLng());
                startLatLng = place.getLatLng();
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });

        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        database = FirebaseDatabase.getInstance();
        ref = database.getReference("Accounts/" + uid + "/availability");
        availability = new Availability();

        btnAddAvailability.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setValues();
                //create new availability node with unique key
                pushedAvailRef = ref.push();
                //add availability details to newly created availability node
                pushedAvailRef.setValue(availability);
                Toast.makeText(getActivity(), "Availability added", Toast.LENGTH_LONG).show();
                //read driver availability, loop through user's trips, try and create match
                readDB();
            }
        });
        return root;
    }

    private void setValues() {
        //sets values of inputted availability
        availability.setStartPoint(startLatLng);
        availability.setDate(date.getText().toString());
        availability.setTimeSlot(timeSlot.getText().toString());
        availability.setCarType(carType.getText().toString());
        availability.setTravelDist(travelDistance.getText().toString());
    }

    //read from db
    private void readDB(){
        //get key of availability node just added
        String availID = pushedAvailRef.getKey();

        //read availability info
        DatabaseReference availRef = database.getReference("Accounts/" + uid + "/availability/" + availID);

        availRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot availSnapshot : dataSnapshot.getChildren()) {
                    if (availSnapshot.getKey().equals("carType")) {
                        aCarType = availSnapshot.getValue(String.class);
                    } else if (availSnapshot.getKey().equals("date")) {
                        aDate = availSnapshot.getValue(String.class);
                    } else if (availSnapshot.getKey().equals("timeSlot")) {
                        aTimeSlot = availSnapshot.getValue(String.class);
                    } else if (availSnapshot.getKey().equals("startPoint")) {
                        latDStartPoint = availSnapshot.child("latitude").getValue(double.class);
                        longDStartPoint = availSnapshot.child("longitude").getValue(double.class);
                        dStartPoint = Point.fromLngLat(longDStartPoint, latDStartPoint);
                    } else if (availSnapshot.getKey().equals("travelDist")) {
                        travelDist = Integer.parseInt(availSnapshot.getValue(String.class)) * 1000;
                    }
                }
                //convert timeSlots to ints and then minutes
                aStartTimeHours = Integer.parseInt(aTimeSlot.substring(0,2)) * 60;
                aStartTimeMins = Integer.parseInt(aTimeSlot.substring(3,5));
                aStartTime = aStartTimeHours + aStartTimeMins;
                aEndTimeHours = Integer.parseInt(aTimeSlot.substring(6,8)) * 60;
                aEndTimeMins = Integer.parseInt(aTimeSlot.substring(9,11));
                aEndTime = aEndTimeHours + aEndTimeMins;

                Log.d(TAG, "Availability info: " + " " + aCarType + " " + aDate + " " + aTimeSlot + " " + travelDist);
                Log.d(TAG, "Avail times in mins: " + aStartTime + " " + aEndTime);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

        //go through other accounts, if they're a user get their trip info, else go to next account
        DatabaseReference tripsRef = database.getReference("Accounts");

        tripsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                triploop:
                for (DataSnapshot accountsSnapshot : dataSnapshot.getChildren()){//loops through accounts
                    if (match){
                        break;
                    }
                    for (DataSnapshot accountSnapshot : accountsSnapshot.getChildren()){//loops through an account
                        if (match){
                            break triploop;
                        }
                        if (accountSnapshot.getValue().toString().equals("driver")){ //if account type is driver go to next account
                            Log.d(TAG, "Breaking...");
                            break;
                        }else if (accountSnapshot.getKey().equals("trips")){ //else it's a user and we want to look at their trips
                            //record the driver's ID
                            userID = accountsSnapshot.getKey();
                            Log.d(TAG, userID);
                            for (DataSnapshot tripsSnapshot : accountSnapshot.getChildren()){ //loop through user's trips
                                if (match){
                                    break triploop;
                                }
                                tripID = tripsSnapshot.getKey();
                                Log.d(TAG, tripID);
                                for (DataSnapshot tripSnapshot : tripsSnapshot.getChildren()){ //loop through trip details and set them
                                    if (match){
                                        break triploop;
                                    }
                                    if (tripSnapshot.getKey().equals("carType")) {
                                        tCarType = tripSnapshot.getValue(String.class);
                                    }
                                    else if (tripSnapshot.getKey().equals("date")){
                                        tDate = tripSnapshot.getValue(String.class);
                                    }
                                    else if (tripSnapshot.getKey().equals("timeSlot")){
                                        tTimeSlot = tripSnapshot.getValue(String.class);
                                    }
                                    else if (tripSnapshot.getKey().equals("pickupPoint")) {
                                        latUPickUpPoint = tripSnapshot.child("latitude").getValue(double.class);
                                        longUPickUpPoint = tripSnapshot.child("longitude").getValue(double.class);
                                        uPickUpPoint = Point.fromLngLat(longUPickUpPoint, latUPickUpPoint);
                                    }
                                    else if (tripSnapshot.getKey().equals("destination")) {
                                        latDestination = tripSnapshot.child("latitude").getValue(double.class);
                                        longDestination = tripSnapshot.child("longitude").getValue(double.class);
                                        destination = Point.fromLngLat(longDestination, latDestination);
                                    }
                                }
                                //convert timeSlots to ints and then minutes
                                tStartTimeHours = Integer.parseInt(tTimeSlot.substring(0,2)) * 60;
                                tStartTimeMins = Integer.parseInt(tTimeSlot.substring(3,5));
                                tStartTime = tStartTimeHours + tStartTimeMins;
                                tEndTimeHours = Integer.parseInt(tTimeSlot.substring(6,8)) * 60;
                                tEndTimeMins = Integer.parseInt(tTimeSlot.substring(9,11));
                                tEndTime = tEndTimeHours + tEndTimeMins;
                                Log.d(TAG, "Trip info: " + " " + tCarType + " " + tDate + " " + tTimeSlot);
                                Log.d(TAG, "Trip times in mins: " + tStartTime + " " + tEndTime);

                                //Get the distance of the driver's proposed trip
                                totalDistance = 0;
                                //Gets the distance between driver's start point and user's pickup point
                                NavigationRoute.builder(getActivity())
                                        .accessToken(Mapbox.getAccessToken())
                                        .origin(dStartPoint)
                                        .destination(uPickUpPoint)
                                        .build()
                                        .getRoute(new Callback<DirectionsResponse>() {
                                            @Override
                                            public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                                                // You can get the generic HTTP info about the response
                                                Log.d(TAG, "Response code: " + response.code());
                                                if (response.body() == null) {
                                                    Log.e(TAG, "No routes found, make sure you set the right user and access token.");
                                                    return;
                                                } else if (response.body().routes().size() < 1) {
                                                    Log.e(TAG, "No routes found");
                                                    return;
                                                }

                                                double distance = response.body().routes().get(0).distance();
                                                Log.d(TAG, "Dist1: " + distance);
                                                distances[0] = distance;
                                                //Gets the distance between user's pickup point and shop
                                                NavigationRoute.builder(getActivity())
                                                        .accessToken(Mapbox.getAccessToken())
                                                        .origin(uPickUpPoint)
                                                        .destination(destination)
                                                        .build()
                                                        .getRoute(new Callback<DirectionsResponse>() {
                                                            @Override
                                                            public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                                                                // You can get the generic HTTP info about the response
                                                                Log.d(TAG, "Response code: " + response.code());
                                                                if (response.body() == null) {
                                                                    Log.e(TAG, "No routes found, make sure you set the right user and access token.");
                                                                    return;
                                                                } else if (response.body().routes().size() < 1) {
                                                                    Log.e(TAG, "No routes found");
                                                                    return;
                                                                }

                                                                double distance = response.body().routes().get(0).distance();
                                                                Log.d(TAG, "Dist2: " + distance);
                                                                distances[1] = distance;
                                                                //Gets the distance between shop and user's pickup point
                                                                NavigationRoute.builder(getActivity())
                                                                        .accessToken(Mapbox.getAccessToken())
                                                                        .origin(destination)
                                                                        .destination(uPickUpPoint)
                                                                        .build()
                                                                        .getRoute(new Callback<DirectionsResponse>() {
                                                                            @Override
                                                                            public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                                                                                // You can get the generic HTTP info about the response
                                                                                Log.d(TAG, "Response code: " + response.code());
                                                                                if (response.body() == null) {
                                                                                    Log.e(TAG, "No routes found, make sure you set the right user and access token.");
                                                                                    return;
                                                                                } else if (response.body().routes().size() < 1) {
                                                                                    Log.e(TAG, "No routes found");
                                                                                    return;
                                                                                }

                                                                                double distance = response.body().routes().get(0).distance();
                                                                                Log.d(TAG, "Dist3: " + distance);
                                                                                distances[2] = distance;
                                                                                for (double dist : distances){
                                                                                    totalDistance += dist;
                                                                                }
                                                                                Log.d(TAG, "total dist: " + totalDistance);

                                                                                //compare availability details and trip details
                                                                                match = false;
                                                                                if (aDate.equals(tDate)){
                                                                                    if ((aStartTime <= tStartTime) && (aEndTime >= tEndTime)){
                                                                                        if (aCarType.equals(tCarType)){
                                                                                            if (totalDistance <= travelDist){
                                                                                                match = true;
                                                                                                sendNotifications();
                                                                                            }
                                                                                        }
                                                                                    }
                                                                                }
                                                                                Log.d(TAG, "Match? " + match);
                                                                            }

                                                                            @Override
                                                                            public void onFailure(Call<DirectionsResponse> call, Throwable throwable) {
                                                                                Log.e(TAG, "Error: " + throwable.getMessage());
                                                                            }
                                                                        });
                                                            }

                                                            @Override
                                                            public void onFailure(Call<DirectionsResponse> call, Throwable throwable) {
                                                                Log.e(TAG, "Error: " + throwable.getMessage());
                                                            }
                                                        });
                                            }

                                            @Override
                                            public void onFailure(Call<DirectionsResponse> call, Throwable throwable) {
                                                Log.e(TAG, "Error: " + throwable.getMessage());
                                            }
                                        });
                            }
                        }
                    }
                }
                if (!match){
                    Toast.makeText(getActivity(),"No current match", Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void sendNotifications(){
        //create references to put trip details into driver confirmedTrip node
        DatabaseReference pickupRef = database.getReference("Accounts/" + uid + "/confirmedTrip/" + "/User pickup");
        DatabaseReference shopRef = database.getReference("Accounts/" + uid + "/confirmedTrip/" + "/User destination");
        DatabaseReference dateRef = database.getReference("Accounts/" + uid + "/confirmedTrip/" + "/Date");
        DatabaseReference timeRef = database.getReference("Accounts/" + uid + "/confirmedTrip/" + "/Time slot");

        //write confirmed trip details to driver's confirmedTrip node
        pickupRef.setValue(uPickUpPoint.coordinates());
        shopRef.setValue(destination.coordinates());
        dateRef.setValue(tDate);
        timeRef.setValue(tTimeSlot);

        //send notification to driver
        ((DriverHomeActivity) getActivity()).notification1(root);
        DatabaseReference drivermatchRef = database.getReference("Accounts/" + uid + "/match");
        drivermatchRef.setValue("true");

        //send notification to user
        DatabaseReference usermatchRef = database.getReference("Accounts/" + userID + "/match");
        usermatchRef.setValue("true");
    }
}