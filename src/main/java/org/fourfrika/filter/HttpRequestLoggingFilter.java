package org.fourfrika.filter;

import org.springframework.web.filter.AbstractRequestLoggingFilter;

import javax.servlet.http.HttpServletRequest;

public class HttpRequestLoggingFilter extends AbstractRequestLoggingFilter {

    /**
     * Writes a log message before the request is processed.
     */
    @Override
    protected void beforeRequest(HttpServletRequest request, String message) {
        //do not log here. The message at this point does not include the payload
    }

    /**
     * Writes a log message after the request is processed.
     */
    @Override
    protected void afterRequest(HttpServletRequest request, String message) {
        logger.info(message);
    }
}
