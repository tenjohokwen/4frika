package org.fourfrika.concurrency;

public class LogFlagException extends RuntimeException {

    /*
    Intended to be used to avoid logging this exception multiple times
     */
    private final boolean logged;

    public LogFlagException(boolean isLogged) {
        logged = isLogged;
    }

    public LogFlagException(Throwable cause) {
        super(cause);
        logged = false;
    }

    public boolean isLogged() {
        return logged;
    }
}
