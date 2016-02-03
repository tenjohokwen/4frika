package org.fourfrika.concurrency;

import org.fourfrika.commons.MDCWrapper;
import org.slf4j.MDC;

import java.util.Collections;
import java.util.Map;

public class ClientContext {
    public final Exception clientStack;
    public final String clientThreadName;
    private final Map<String, String> mdcCtxt;

    private ClientContext(Exception clientStack, String clientThreadName, Map<String, String> mdcCxt) {
        this.clientStack = clientStack;
        this.clientThreadName = clientThreadName;
        this.mdcCtxt = mdcCxt == null ? Collections.emptyMap() : mdcCxt;
    }

    public static ClientContext instance() {
        return new ClientContext(new Exception(), Thread.currentThread().getName(), MDC.getCopyOfContextMap());
    }


    public void setClientMDCContext() {
        MDCWrapper.setContext(this.mdcCtxt);
    }
}
