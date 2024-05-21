package com.example.firebase_test.core;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.firebase_test.AppError;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Objects;

import io.reactivex.rxjava3.core.Single;

public class AccountFirebaseDataSource implements AccountDataSource{

    private static final String TAG="AccountFirebaseDataSource";
    private final FirebaseAuth fireAuth=FirebaseAuth.getInstance();

    private <T> AccFirebaseDSError errorHandling(@NonNull Task<T> task) {
        String errorCode="";
        String logMes="";
        try {
            var exc =(FirebaseAuthException)task.getException();
            errorCode= Objects.requireNonNull(exc).getErrorCode();
            logMes= Objects.requireNonNull(exc).getLocalizedMessage();
            throw exc;
        } catch(FirebaseException e) {
            return AccFirebaseDSError.getByErrorCode(errorCode,logMes);
        }catch(Exception e){
            return new AccFirebaseDSError.UnknownError(e.getLocalizedMessage());
        }
    }
    private UserData mapToUserData(FirebaseUser firebaseUser){
        return  UserData.builder()
                .userId(firebaseUser.getUid())
                .email(firebaseUser.getEmail())
                .username(firebaseUser.getDisplayName())
                .photoUrl(firebaseUser.getPhotoUrl())
                .build();
    }
    @Override
    public Single<UserData> currentUser() {
        return Single.create(emitter-> {
            fireAuth.addAuthStateListener(firebaseAuth -> {
                FirebaseUser userFb = firebaseAuth.getCurrentUser();
                if(userFb==null || userFb.getUid()==null){
                    emitter.onError(new AccFirebaseDSError.NoneLoggedAccount("Couldn't retrieve user from firebaseAuth - it's null"));
                }else{
                    Log.i(TAG,"User data retrieved from firebaseAuth successfully");
                    emitter.onSuccess(mapToUserData(userFb));
                }
            });
        });

    }

    @Override
    public boolean hasUser() {
        if(fireAuth.getCurrentUser()==null){
            Log.i(TAG,"User data in device was not found");
        }else{
            Log.i(TAG,"User data in device was found");
        }
        return fireAuth.getCurrentUser()!=null;
    }
    @Override
    public Single<UserData> signIn(String email, String password)  {
        return Single.create(emitter->{
            fireAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                if ( task.isSuccessful() && task.getResult().getUser()!=null){
                    Log.i(TAG,"User signed in with email and password successfully");
                    emitter.onSuccess(mapToUserData(task.getResult().getUser()));
                }else{
                    emitter.onError(errorHandling(task));
                }
            });
        });
    }
    @Override
    public  Single<UserData> signInWithCredentials(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        return Single.create(emitter-> {
            fireAuth.signInWithCredential(credential).addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult().getUser()!=null) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.i(TAG, "User signed in with google credentials successfully");
                    emitter.onSuccess(mapToUserData(task.getResult().getUser()));
                } else {
                    emitter.onError(errorHandling(task));
                }
            });
        });
    }

    @Override
    public Single<UserData> signUp(String email, String password) {
        return Single.create(emitter-> {
            fireAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                if ( task.isSuccessful() && task.getResult()!=null){
                    Log.i(TAG,"User signed up with email and password successfully");
                    emitter.onSuccess(mapToUserData(task.getResult().getUser()));
                }else{
                    emitter.onError(errorHandling(task));
                }
                //Log.e(TAG,"User couldn't sign up: "+ Objects.requireNonNull(task.getException()).getLocalizedMessage());
            });
        });
    }

    @Override
    public void signOut() {
        FirebaseAuth.getInstance().signOut();
        Log.i(TAG,"User sign out successfully");
    }

    @Override
    public  Single<Object> deleteAccount() {
        return Single.create(emitter-> {
            Objects.requireNonNull(fireAuth.getCurrentUser()).delete().addOnCompleteListener(task -> {
                if ( task.isSuccessful()){
                    Log.i(TAG,"User deleted account successfully");
                    emitter.onSuccess(new Object());
                }else{
                    emitter.onError(errorHandling(task));
                }
                //Log.e(TAG,"User couldn't delete account: "+ Objects.requireNonNull(task.getException()).getLocalizedMessage());
            });
        });
    }
}
