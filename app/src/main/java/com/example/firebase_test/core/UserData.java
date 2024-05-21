package com.example.firebase_test.core;

import android.net.Uri;

import java.net.URI;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserData{
    private String userId;
    private String email;
    private String username;
    private Uri photoUrl;
}
