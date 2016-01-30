package org.fourfrika.concurrency;

/**
 * Created by mokwen on 30.01.16.
 */
public class LogFlagException extends RuntimeException {

    /*
    Intended to be used to avoid logging this exception multiple times
     */
    private final boolean logged;

    public LogFlagException(Throwable cause, boolean isLogged) {
        //super(cause);
        logged = isLogged;
    }

    public boolean isLogged() {
        return logged;
    }
}
