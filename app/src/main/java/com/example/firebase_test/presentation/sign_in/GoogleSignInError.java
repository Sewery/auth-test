package com.example.firebase_test.presentation.sign_in;

import com.example.firebase_test.AppError;
import com.example.firebase_test.R;
import com.example.firebase_test.UIText;

public class GoogleSignInError extends AppError {
    private static final UIText uiText= new  UIText.ResourceString (R.string.GOOGLE_SIGN_IN_FAILED);
    public GoogleSignInError(String logMessage) {
        super(logMessage, uiText);
    }
}
