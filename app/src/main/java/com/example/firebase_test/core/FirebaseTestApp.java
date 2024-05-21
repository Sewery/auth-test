package com.example.firebase_test.core;

import android.app.Application;

public class FirebaseTestApp extends Application {
    public AppContainer appContainer;
    @Override
    public void onCreate() {
        super.onCreate();
        appContainer=new AppContainer();
    }
}
