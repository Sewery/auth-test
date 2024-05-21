package com.example.firebase_test.core;

import lombok.Getter;
public final class AppContainer {
    @Getter
    private final AccountDataSource accountDataSource;
    public AppContainer(){
        this.accountDataSource=new AccountFirebaseDataSource();

    }
}
