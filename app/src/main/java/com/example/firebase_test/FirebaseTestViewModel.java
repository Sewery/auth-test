package com.example.firebase_test;

import static androidx.lifecycle.SavedStateHandleSupport.createSavedStateHandle;
import static androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.viewmodel.ViewModelInitializer;

import com.example.firebase_test.core.AccFirebaseDSError;
import com.example.firebase_test.core.AccountDataSource;
import com.example.firebase_test.core.FirebaseTestApp;
import com.example.firebase_test.core.FirebaseTestAppViewModel;
import com.example.firebase_test.core.TriConsumer;
import com.example.firebase_test.core.UserData;

import java.util.function.BiConsumer;

public class FirebaseTestViewModel extends FirebaseTestAppViewModel {
    private static final String TAG="SignUpViewModel";
    private final AccountDataSource accountDataSource;
    private final MutableLiveData<UserData> userData=new MutableLiveData<>();
    public LiveData<UserData> getUserData(){
        return userData;
    }
    private void updateUserData(UserData newUserData){
        userData.setValue(newUserData);
    }
    public static final ViewModelInitializer<FirebaseTestViewModel> initializer = new ViewModelInitializer<>(
            FirebaseTestViewModel.class,
            creationExtras -> {
                FirebaseTestApp app = (FirebaseTestApp) creationExtras.get(APPLICATION_KEY);
                assert app != null;
                SavedStateHandle savedStateHandle = createSavedStateHandle(creationExtras);

                return new FirebaseTestViewModel(app.appContainer.getAccountDataSource(),  savedStateHandle);
            }
    );
    public FirebaseTestViewModel(AccountDataSource accountDataSource, SavedStateHandle savedStateHandle){
        this.accountDataSource=accountDataSource;
    }
    public void onSignOutClick(BiConsumer<String,String> openAndPopUp, TriConsumer<SnackbarUI.SnackbarTypes,UIText,String> snackbarOnError) {
        singleAction(TAG,
                accountDataSource::signOut,
                ()->{
                    openAndPopUp.accept(FirebaseTestRoutes.FIREBASE_TEST_FRAGMENT,FirebaseTestRoutes.SPLASH_FRAGMENT);
                },
                (err)->{
                    if(err instanceof AccFirebaseDSError.DifferentInternalError){
                        snackbarOnError.accept(SnackbarUI.SnackbarTypes.FIREBASE_AUTH_ERROR,((AccFirebaseDSError) err).getUserMsg(),((AccFirebaseDSError) err).getLogMessage());
                    }
                    else if(err instanceof AccFirebaseDSError){
                        snackbarOnError.accept(SnackbarUI.SnackbarTypes.FIREBASE_AUTH_ERROR,((AccFirebaseDSError) err).getUserMsg(),"");
                    } else{
                        snackbarOnError.accept(SnackbarUI.SnackbarTypes.BASIC_AUTH_ERROR,null, err.getLocalizedMessage());
                    }
                }
                );
    }
    public void getCurrentUser() {
        accountDataSource.currentUser().blockingSubscribe(
                this::updateUserData,
                (err)-> Log.e(TAG,"User data can't be retrieved because:"+err.getLocalizedMessage()));
    }

    public void onDeleteAccountClick() {
       singleAction(TAG, accountDataSource::deleteAccount);
    }
}
