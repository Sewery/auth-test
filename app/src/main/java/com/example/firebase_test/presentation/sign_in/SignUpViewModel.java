package com.example.firebase_test.presentation.sign_in;

import static androidx.lifecycle.SavedStateHandleSupport.createSavedStateHandle;
import static androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.viewmodel.ViewModelInitializer;

import com.example.firebase_test.R;
import com.example.firebase_test.SnackbarUI;
import com.example.firebase_test.UIText;
import com.example.firebase_test.core.AccFirebaseDSError;
import com.example.firebase_test.core.FirebaseTestAppViewModel;
import com.example.firebase_test.FirebaseTestRoutes;
import com.example.firebase_test.core.AccountDataSource;
import com.example.firebase_test.core.FirebaseTestApp;
import com.example.firebase_test.core.TriConsumer;

import java.util.Objects;
import java.util.function.BiConsumer;


public class SignUpViewModel extends FirebaseTestAppViewModel {
    private final AccountDataSource accountDataSource;
    private static final String TAG="SignUpViewModel";
    public static final ViewModelInitializer<SignUpViewModel> initializer = new ViewModelInitializer<>(
            SignUpViewModel.class,
            creationExtras -> {
                FirebaseTestApp app = (FirebaseTestApp) creationExtras.get(APPLICATION_KEY);
                assert app != null;
                SavedStateHandle savedStateHandle = createSavedStateHandle(creationExtras);
                return new SignUpViewModel(app.appContainer.getAccountDataSource(),  savedStateHandle);
            }
    );
    public SignUpViewModel(AccountDataSource accountDataSource, SavedStateHandle savedStateHandle){
        this.accountDataSource=accountDataSource;
    }
    private final MutableLiveData<String> email=new MutableLiveData<>();
    public void updateEmail(String newEmail){
        email.setValue(newEmail);
    }
    private final MutableLiveData<String> password=new MutableLiveData<>();
    public void updatePassword(String newPassword){
        password.setValue(newPassword);
    }
    private final MutableLiveData<String> confirmPassword=new MutableLiveData<>();
    public void updateConfirmPassword(String newConfirmPassword){
        confirmPassword.setValue(newConfirmPassword);
    }
    public void onSignUpClick(BiConsumer<String,String> openAndPopUp, TriConsumer<SnackbarUI.SnackbarTypes, UIText,String> snackbarOnError){
        if(!Objects.equals(password.getValue(), confirmPassword.getValue())){
            snackbarOnError.accept(SnackbarUI.SnackbarTypes.BASIC_AUTH_ERROR,new UIText.ResourceString(R.string.NOT_MATCHING_PASSWORDS),"");
            Log.e(TAG,"User entered not matching passwords in sign up");
        }else{
            singleAction(TAG,
                    accountDataSource.signUp(email.getValue(),password.getValue()),
                    ()->openAndPopUp.accept(FirebaseTestRoutes.SIGN_UP_FRAGMENT,FirebaseTestRoutes.FIREBASE_TEST_FRAGMENT),
                    (err)->{
                        if(err instanceof AccFirebaseDSError.DifferentInternalError){
                            snackbarOnError.accept(SnackbarUI.SnackbarTypes.FIREBASE_AUTH_ERROR,((AccFirebaseDSError) err).getUserMsg(),((AccFirebaseDSError) err).getLogMessage());
                        }
                        else if(err instanceof AccFirebaseDSError){
                            snackbarOnError.accept(SnackbarUI.SnackbarTypes.FIREBASE_AUTH_ERROR,((AccFirebaseDSError) err).getUserMsg(),"");
                        } else{
                            snackbarOnError.accept(SnackbarUI.SnackbarTypes.BASIC_AUTH_ERROR,null, err.getLocalizedMessage());
                        }
                    });
        }
    }
}
