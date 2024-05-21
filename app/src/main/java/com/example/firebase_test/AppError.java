package com.example.firebase_test;

import lombok.Getter;

@Getter
public abstract class AppError extends Throwable{
    private final UIText userMsg;
    private final String logMessage;
    public AppError(String logMessage, UIText userMsg) {
        super(logMessage);
        this.userMsg = userMsg;
        this.logMessage = logMessage;
    }

}

