package com.example.firebase_test;

import static com.example.firebase_test.FirebaseTestRoutes.AUTH_PORT;
import static com.example.firebase_test.FirebaseTestRoutes.FIRESTORE_PORT;
import static com.example.firebase_test.FirebaseTestRoutes.LOCALHOST;

import android.os.Bundle;

import androidx.annotation.Nullable;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.BuildConfig;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;


public class MainActivity extends AppCompatActivity {
    private static final String TAG="MainActivity";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


    }
    private void configureFirebaseServices(){
        if(BuildConfig.DEBUG){
            FirebaseAuth.getInstance().useEmulator(LOCALHOST,AUTH_PORT);
            FirebaseFirestore.getInstance().useEmulator(LOCALHOST,FIRESTORE_PORT);
        }
    }
}