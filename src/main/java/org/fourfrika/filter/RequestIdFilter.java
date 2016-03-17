package org.fourfrika.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.fourfrika.commons.Constants.REQUEST_ID_HEADER_NAME;

public class RequestIdFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(RequestIdFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String reqId = request.getHeader(REQUEST_ID_HEADER_NAME);
        try {
            MDC.put(REQUEST_ID_HEADER_NAME, reqId);
            log.trace("Inserted requestId '{}' to MDC context", reqId);
            filterChain.doFilter(request, response);
        } finally {
            log.trace("About to remove requestId '{}' from MDC context", reqId);
            MDC.remove(REQUEST_ID_HEADER_NAME);
        }
    }
}
