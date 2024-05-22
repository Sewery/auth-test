package com.example.firebase_test.presentation.sign_in;

import static androidx.activity.result.ActivityResultCallerKt.registerForActivityResult;
import static androidx.lifecycle.SavedStateHandleSupport.createSavedStateHandle;
import static androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY;

import android.content.Intent;
import android.util.Log;

import androidx.credentials.CustomCredential;
import androidx.credentials.GetCredentialResponse;
import androidx.credentials.PasswordCredential;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.viewmodel.ViewModelInitializer;

import com.example.firebase_test.core.FirebaseTestAppViewModel;
import com.example.firebase_test.FirebaseTestRoutes;
import com.example.firebase_test.R;
import com.example.firebase_test.SnackbarUI;
import com.example.firebase_test.UIText;
import com.example.firebase_test.core.AccFirebaseDSError;
import com.example.firebase_test.core.AccountDataSource;
import com.example.firebase_test.core.FirebaseTestApp;
import com.example.firebase_test.core.TriConsumer;
import com.example.firebase_test.core.UserData;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.common.api.ApiException;
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential;
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException;

import java.util.function.BiConsumer;

import io.reactivex.rxjava3.core.Single;

public class SplashViewModel extends FirebaseTestAppViewModel {
    private static final String TAG="SplashViewModel";
    private final AccountDataSource accountDataSource;
    public static final ViewModelInitializer<SplashViewModel> initializer = new ViewModelInitializer<>(
            SplashViewModel.class,
            creationExtras -> {
                FirebaseTestApp app = (FirebaseTestApp) creationExtras.get(APPLICATION_KEY);
                assert app != null;
                SavedStateHandle savedStateHandle = createSavedStateHandle(creationExtras);
                return new SplashViewModel(app.appContainer.getAccountDataSource(),  savedStateHandle);
            }
    );
    public SplashViewModel(AccountDataSource accountDataSource, SavedStateHandle savedStateHandle){
        this.accountDataSource=accountDataSource;
    }
    public void onAppStart(BiConsumer<String,String> openAndPopUp){
            if (accountDataSource.hasUser()) {
                //Log.i(TAG, "onAppStart: hasUser"+ accountDataSource.currentUser().blockingGet().getEmail());
                openAndPopUp.accept(FirebaseTestRoutes.SPLASH_FRAGMENT, FirebaseTestRoutes.FIREBASE_TEST_FRAGMENT);
            } //else
                //openAndPopUp.accept(FirebaseTestRoutes.SPLASH_FRAGMENT, FirebaseTestRoutes.SIGN_IN_FRAGMENT);
    }
    //To api<14
    private Single<UserData> firebaseAuthWithGoogle(Intent data, SignInClient signInClient)  {
        // Google Sign In was successful, authenticate with Firebase
        SignInCredential credential = null;
        try {
            credential = signInClient.getSignInCredentialFromIntent(data);
        } catch (ApiException e) {
           return Single.error(e);
        }
        String idToken = credential.getGoogleIdToken();
        return  accountDataSource.signInWithCredentials(idToken);
    }
    //From SDK api>=14
    private Single<UserData> firebaseAuthWithGoogle(GetCredentialResponse response)  {
        if (response.getCredential() instanceof PasswordCredential credential) {
            if (GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL.equals(credential.getType())) {
                GoogleIdTokenCredential googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.getData());
                return accountDataSource.signInWithCredentials(googleIdTokenCredential.getIdToken());
            }
        }
        Log.e(TAG, "No valid credential response");
        return Single.error(new GoogleSignInError(null));
    }
    public void handleSignInResult(Intent data, SignInClient signInClient, BiConsumer<String,String> openAndPopUp, TriConsumer<SnackbarUI.SnackbarTypes, UIText,String> snackbarOnError) {
        singleAction(TAG, firebaseAuthWithGoogle(data, signInClient),
                () -> openAndPopUp.accept(FirebaseTestRoutes.SPLASH_FRAGMENT, FirebaseTestRoutes.FIREBASE_TEST_FRAGMENT),
                (err) -> {
                    if(err instanceof AccFirebaseDSError.DifferentInternalError){
                        snackbarOnError.accept(SnackbarUI.SnackbarTypes.FIREBASE_AUTH_ERROR,((AccFirebaseDSError) err).getUserMsg(),((AccFirebaseDSError) err).getLogMessage());
                    }
                    else if (err instanceof AccFirebaseDSError) {
                        snackbarOnError.accept(SnackbarUI.SnackbarTypes.FIREBASE_AUTH_ERROR, ((AccFirebaseDSError) err).getUserMsg(),"");
                    } else if (err instanceof ApiException){
                        snackbarOnError.accept(SnackbarUI.SnackbarTypes.BASIC_AUTH_ERROR, new UIText.ResourceString(R.string.CREDENTIAL_NOT_PRESENT),"");
                        Log.e(TAG,"Google Auth problem: "+err.getLocalizedMessage());
                    }else{
                        snackbarOnError.accept(SnackbarUI.SnackbarTypes.BASIC_AUTH_ERROR,null,err.getLocalizedMessage());
                    }
                });
    }
    public void handleSignInResult(GetCredentialResponse credentialResponse, BiConsumer<String,String> openAndPopUp, TriConsumer<SnackbarUI.SnackbarTypes, UIText,String> snackbarOnError) {
        singleAction(TAG, firebaseAuthWithGoogle(credentialResponse),
                () -> openAndPopUp.accept(FirebaseTestRoutes.SPLASH_FRAGMENT, FirebaseTestRoutes.FIREBASE_TEST_FRAGMENT),
                (err) -> {
                    if(err instanceof AccFirebaseDSError.DifferentInternalError){
                        snackbarOnError.accept(SnackbarUI.SnackbarTypes.FIREBASE_AUTH_ERROR,((AccFirebaseDSError) err).getUserMsg(),((AccFirebaseDSError) err).getLogMessage());
                    }
                    else if (err instanceof AccFirebaseDSError) {
                        snackbarOnError.accept(SnackbarUI.SnackbarTypes.FIREBASE_AUTH_ERROR, ((AccFirebaseDSError) err).getUserMsg(),"");
                    } else if (err instanceof ApiException){
                        snackbarOnError.accept(SnackbarUI.SnackbarTypes.BASIC_AUTH_ERROR, new UIText.ResourceString(R.string.CREDENTIAL_NOT_PRESENT),"");
                        Log.e(TAG,"Google Auth problem: "+err.getLocalizedMessage());
                    }else if (err instanceof GoogleSignInError){
                        snackbarOnError.accept(SnackbarUI.SnackbarTypes.BASIC_AUTH_ERROR,((GoogleSignInError) err).getUserMsg(),"");
                    }
                    else{
                        snackbarOnError.accept(SnackbarUI.SnackbarTypes.BASIC_AUTH_ERROR,null,err.getLocalizedMessage());
                    }
                });
    }
}
