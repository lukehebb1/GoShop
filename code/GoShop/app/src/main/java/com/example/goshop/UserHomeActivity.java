package com.example.goshop;
import android.app.Notification;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.os.Bundle;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class UserHomeActivity extends AppCompatActivity {

    private NotificationManagerCompat notificationManager;
    private static final String TAG = "UserHomeActivity";
    private FirebaseDatabase database;
    private DatabaseReference matchRef;
    private String uid;

    public static final String NOTIFICATION_ID1 = "notification1";
    public static final String NOTIFICATION_ID2 = "notification2";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_userhome, R.id.navigation_addtrip, R.id.navigation_mytrips, R.id.navigation_settings)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
        notificationManager = NotificationManagerCompat.from(this);

        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        database = FirebaseDatabase.getInstance();
        matchRef = database.getReference("Accounts/" + uid + "/match");

        //read user match node, if true send notification
        matchRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue().equals("true")) {
                    notification2(findViewById(android.R.id.content));

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

    public void notification2(View v){
        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_check_black_24dp);

        Notification notification2 = new NotificationCompat.Builder(this, NOTIFICATION_ID2)
                .setSmallIcon(R.drawable.ic_notification2)
                .setLargeIcon(largeIcon)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(getString(R.string.tripconfirmed_text))
                        .setBigContentTitle("Update")
                        .setSummaryText("Update"))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .build();
        notificationManager.notify(2, notification2);
    }
}