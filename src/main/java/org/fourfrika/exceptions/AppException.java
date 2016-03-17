package org.fourfrika.exceptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by mokwen on 18.02.16.
 */
public class AppException extends RuntimeException {

    /*
    could be used for futures that may throw exceptions but whose 'get' methods may never be called.
    To avoid missing such exceptions you could log them and give them the logged flag then wrap within this class and rethrow.
    If the get method is ever called, the exception handler does not need to log again
     */
    private final boolean logged;

    private final String helpCode;

    private List<Map<String, Object>> errors = new ArrayList<>();


    public AppException(boolean alreadyLogged, Throwable cause, String msg, UUID helpCodeAsUuid) {
        super(msg, cause);
        helpCode = helpCodeAsUuid.toString();
        this.logged = alreadyLogged;
    }

    public AppException(boolean alreadyLogged, String msg, UUID helpCodeAsUuid) {
        super(msg);
        helpCode = helpCodeAsUuid.toString();
        this.logged = alreadyLogged;
    }

    public boolean isLogged() {
        return logged;
    }

    public String getHelpCode() {
        return helpCode;
    }

    public List<Map<String, Object>> getErrors() {
        return errors;
    }

    public AppException addErrorDetails(Map<String, Object> details) {
        errors.add(details);
        return this;
    }

}
