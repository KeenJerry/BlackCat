package com.android.keenjackdaw.blackcat.exception;

import android.support.annotation.RequiresApi;

public class BlackCatException extends Exception{
    public BlackCatException() {
    }

    public BlackCatException(String message) {
        super(message);
    }

    public BlackCatException(String message, Throwable cause) {
        super(message, cause);
    }

    public BlackCatException(Throwable cause) {
        super(cause);
    }

    @RequiresApi(24)
    public BlackCatException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
