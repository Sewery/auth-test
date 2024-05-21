package com.example.firebase_test;

import android.app.Activity;

import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.example.firebase_test.presentation.sign_in.SignInFragmentDirections;
import com.example.firebase_test.presentation.sign_in.SignUpFragmentDirections;
import com.example.firebase_test.presentation.sign_in.SplashFragmentDirections;

public final class FirebaseTestRoutes {
    public static final String SPLASH_FRAGMENT="SplashFragment";
    public static final String SIGN_IN_FRAGMENT="SignInFragment";
    public static final String SIGN_UP_FRAGMENT="SignUpFragment";
    public static final String FIREBASE_TEST_FRAGMENT="FirebaseTestFragment";
    public static final String LOCALHOST = "10.0.2.2";
    public static final Integer AUTH_PORT = 9099;
    public static final Integer FIRESTORE_PORT = 8080;
    public static NavDirections getNavDirections(String from , String to){
        return switch(from){
            case SPLASH_FRAGMENT -> switch (to){
                case SIGN_IN_FRAGMENT-> SplashFragmentDirections.actionSplashFragmentToSignInFragment();
                case SIGN_UP_FRAGMENT->SplashFragmentDirections.actionSplashFragmentToSignUpFragment();
                case FIREBASE_TEST_FRAGMENT-> SplashFragmentDirections.actionSplashFragmentToFirebaseTestFragment();
                default -> null;
            };
            case SIGN_UP_FRAGMENT -> SignUpFragmentDirections.actionSignUpFragmentToFirebaseTestFragment();
            case SIGN_IN_FRAGMENT ->switch(to){
                case FIREBASE_TEST_FRAGMENT->SignInFragmentDirections.actionSignInFragmentToFirebaseTestFragment();
                case SIGN_UP_FRAGMENT->SignInFragmentDirections.actionSignInFragmentToSignUpFragment();
                default -> null;
            };
            case FIREBASE_TEST_FRAGMENT -> FirebaseTestFragmentDirections.actionFirebaseTestFragmentToSplashFragment();
            default -> null;
        };
    }
}
