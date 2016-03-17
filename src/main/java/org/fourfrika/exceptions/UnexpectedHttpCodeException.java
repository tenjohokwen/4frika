package org.fourfrika.exceptions;

import com.google.common.collect.ImmutableMap;
import org.springframework.http.HttpStatus;

import java.util.UUID;

/**
 * Intended to be used when a not expected HTTP code is returned (e.g. one not specified in the Spec)
 * Holds information that may help in finding out the problem. e.g vendor, httpcode, and the request url
 */
public class UnexpectedHttpCodeException extends AppException {

    public UnexpectedHttpCodeException(HttpStatus status, String responseBodyAsString, String uri, String requestId, boolean alreadyLogged, UUID helpCodeAsUuid) {
        super(alreadyLogged, toMessage(status.value(), responseBodyAsString, uri, requestId, helpCodeAsUuid.toString()), helpCodeAsUuid);
    }

    private static String toMessage(int status, String responseBodyAsString, String uri, String requestId, String helpCode) {
        return String.format("Unexpected http code: '%s' in response with body: '%s' to request: '%s'. The reference requestId is: '%s' and support code: '%s'",  status, responseBodyAsString, uri, requestId, helpCode);
    }
}
