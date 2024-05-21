package com.example.firebase_test.presentation.sign_in;

import static androidx.lifecycle.SavedStateHandleSupport.createSavedStateHandle;
import static androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.viewmodel.ViewModelInitializer;

import com.example.firebase_test.SnackbarUI;
import com.example.firebase_test.UIText;
import com.example.firebase_test.core.AccFirebaseDSError;
import com.example.firebase_test.core.FirebaseTestAppViewModel;
import com.example.firebase_test.FirebaseTestRoutes;
import com.example.firebase_test.core.AccountDataSource;
import com.example.firebase_test.core.FirebaseTestApp;
import com.example.firebase_test.core.TriConsumer;

import java.util.function.BiConsumer;

public class SignInViewModel extends FirebaseTestAppViewModel {
    private static final String TAG="SignInViewModel";
    private final AccountDataSource accountDataSource;
    public static final ViewModelInitializer<SignInViewModel> initializer = new ViewModelInitializer<>(
            SignInViewModel.class,
            creationExtras -> {
                FirebaseTestApp app = (FirebaseTestApp) creationExtras.get(APPLICATION_KEY);
                assert app != null;
                SavedStateHandle savedStateHandle = createSavedStateHandle(creationExtras);

                return new SignInViewModel(app.appContainer.getAccountDataSource(),  savedStateHandle);
            }
    );
    public SignInViewModel(AccountDataSource accountDataSource, SavedStateHandle savedStateHandle){
        this.accountDataSource=accountDataSource;
    }
    private final MutableLiveData<String> email=new MutableLiveData<>();
    public LiveData<String> getEmail(){
        return email;
    }
    public void updateEmail(String newEmail){
        email.setValue(newEmail);
    }

    private final MutableLiveData<String> password=new MutableLiveData<>();
    public LiveData<String> getPassword(){
        return password;
    }
    public void updatePassword(String newPassword){
        password.setValue(newPassword);
    }
    public void onSignInClick(BiConsumer<String,String> openAndPopUp, TriConsumer<SnackbarUI.SnackbarTypes, UIText,String> snackbarOnError){
        singleAction(TAG,
                accountDataSource.signIn(email.getValue(),password.getValue()),
                ()-> openAndPopUp.accept(FirebaseTestRoutes.SIGN_IN_FRAGMENT,FirebaseTestRoutes.FIREBASE_TEST_FRAGMENT),
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
//    public void onSignUpClick(BiConsumer<String,String> openAndPopUp){
//        openAndPopUp.accept(FirebaseTestRoutes.SIGN_IN_FRAGMENT,FirebaseTestRoutes.SIGN_UP_FRAGMENT);
//    }
}
