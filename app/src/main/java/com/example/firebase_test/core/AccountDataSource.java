package com.example.firebase_test.core;

import com.example.firebase_test.AppError;

import java.util.Optional;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;

public interface AccountDataSource {
    Single<UserData> currentUser();
    boolean hasUser();
    Single<UserData> signIn(String email, String password);
    Single<UserData> signInWithCredentials(String idToken);
    Single<UserData> signUp(String email, String password);
    void signOut();
    Single<Object> deleteAccount();
}
