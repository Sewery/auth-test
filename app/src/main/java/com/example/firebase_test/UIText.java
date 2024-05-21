package com.example.firebase_test;

import androidx.annotation.StringRes;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class UIText {
    @Getter
    @AllArgsConstructor
    public static final class DynamicString extends UIText{
        private String val;
    }
    @Getter
    @AllArgsConstructor
    public static final class ResourceString extends UIText{
        @StringRes
        private int resId;
    }
}
