package com.example.goshop;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import androidx.core.app.NotificationManagerCompat;

public class MainActivity extends AppCompatActivity {
    private NotificationManagerCompat notificationManager;
    private EditText txtEmailLogin;
    private EditText txtPassword;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase database;
    private DatabaseReference ref;
    private String accountType;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        notificationManager = NotificationManagerCompat.from(this);
        txtEmailLogin = (EditText) findViewById((R.id.editEmail));
        txtPassword = (EditText) findViewById(R.id.editPassword);
        firebaseAuth = FirebaseAuth.getInstance();
    }

    public void buttonLogin(View v){
        final ProgressDialog progressDialog = ProgressDialog.show(MainActivity.this, "Please wait...", "Processing..", true);

        (firebaseAuth.signInWithEmailAndPassword(txtEmailLogin.getText().toString(), txtPassword.getText().toString()))
                .addOnCompleteListener(new OnCompleteListener<com.google.firebase.auth.AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<com.google.firebase.auth.AuthResult> task) {
                        progressDialog.dismiss();

                        //check if login was successful
                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "Login successful", Toast.LENGTH_LONG).show();
                            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                            database = FirebaseDatabase.getInstance();
                            //create reference node to user who logged in
                            ref = database.getReference("Accounts/" + uid + "/accountType");
                            ref.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    //read their account type
                                    accountType = dataSnapshot.getValue(String.class);
                                    Log.d(TAG,"account type: " + accountType);
                                    gotoAccountHome();
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        } else{
                            Log.e("ERROR", task.getException().toString());
                            Toast.makeText(MainActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void gotoAccountHome(){
        if (accountType.equals("user"))
        {
            Intent intent = new Intent(this, UserHomeActivity.class);
            startActivity(intent);
        }
        else{
            Intent intent = new Intent(this, DriverHomeActivity.class);
            startActivity(intent);
        }
    }
}
