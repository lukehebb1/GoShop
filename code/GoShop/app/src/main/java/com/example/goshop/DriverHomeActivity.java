package com.example.goshop;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mapbox.mapboxsdk.Mapbox;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.app.Notification;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class DriverHomeActivity extends AppCompatActivity {
    private NotificationManagerCompat notificationManager;
    private static final String TAG = "DriverHomeActivity";
    private FirebaseDatabase database;
    private DatabaseReference matchRef;
    private String uid;
    public static final String NOTIFICATION_ID1 = "notification1";
    public static final String NOTIFICATION_ID3 = "notification3";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_home);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_driverhome, R.id.navigation_addavailability, R.id.navigation_jobs, R.id.navigation_settings)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        notificationManager = NotificationManagerCompat.from(this);
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));

        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        database = FirebaseDatabase.getInstance();
        matchRef = database.getReference("Accounts/" + uid + "/match");

        //read driver match node, if true send notification
        matchRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue().equals("true")) {
                    notification3(findViewById(android.R.id.content));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public void notification1(View v){
        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_check_black_24dp);

        Notification notification1 = new NotificationCompat.Builder(this, NOTIFICATION_ID1)
                .setSmallIcon(R.drawable.ic_notification1)
                .setLargeIcon(largeIcon)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(getString(R.string.matchfound_text))
                        .setBigContentTitle("Update")
                        .setSummaryText("Update"))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .build();
        notificationManager.notify(1, notification1);
    }

    public void notification3(View v){
        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_check_black_24dp);

        Notification notification3 = new NotificationCompat.Builder(this, NOTIFICATION_ID3)
                .setSmallIcon(R.drawable.ic_notification2)
                .setLargeIcon(largeIcon)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(getString(R.string.jobconfirmed_text))
                        .setBigContentTitle("Update")
                        .setSummaryText("Update"))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .build();
        notificationManager.notify(3, notification3);
    }

    public void gotoMap(View view){
        Intent intent = new Intent(this, MapActivity.class);
        startActivity(intent);
    }
}
