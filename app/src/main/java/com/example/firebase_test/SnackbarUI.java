package com.example.firebase_test;


import static com.example.firebase_test.SnackbarUI.SnackbarTypes.BASIC_AUTH_ERROR;
import static com.example.firebase_test.SnackbarUI.SnackbarTypes.FIREBASE_AUTH_ERROR;

import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import com.google.android.material.snackbar.Snackbar;

import java.util.Optional;

public class SnackbarUI {


    public static final String TAG="SnackbarUI";
    public static final int NONE_RES=-1;
    public enum SnackbarTypes {
        FIREBASE_AUTH_ERROR,
        BASIC_AUTH_ERROR,
        NO_INTERNET_CONNECTION,
        MARKED_AS_FAVORITE,
        COMMENT_ADDED
    }
    public static void showSnackbar(View view, SnackbarTypes type, @Nullable UIText.ResourceString authRes, String logMes) {
        int messageResId = authRes==null?getMessageResId(type):authRes.getResId();
        if(type==BASIC_AUTH_ERROR || type==FIREBASE_AUTH_ERROR)
            logMes+=view.getContext().getText(R.string.try_again);
        if(messageResId==NONE_RES)
            Snackbar.make(view,  logMes, Snackbar.LENGTH_LONG).show();
        else
            Snackbar.make(view,  view.getContext().getText(messageResId)+logMes, Snackbar.LENGTH_LONG).show();
    }

    @StringRes
    private static int getMessageResId(SnackbarTypes type) {
        switch (type) {
            case BASIC_AUTH_ERROR->{
                return R.string.auth_error_message;
            }
            case NO_INTERNET_CONNECTION -> {
                return R.string.no_internet_connection_message;
            }
            case MARKED_AS_FAVORITE -> {
                return R.string.marked_as_favorite_message;
            }
            case COMMENT_ADDED -> {
                return R.string.comment_added_message;
            }
            default-> Log.e(TAG,"Unknown SnackbarType: " + type);
        }
        return NONE_RES;
    }
}
