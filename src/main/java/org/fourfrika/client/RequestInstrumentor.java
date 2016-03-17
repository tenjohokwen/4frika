package org.fourfrika.client;

import com.codahale.metrics.Histogram;
import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;

import static com.codahale.metrics.MetricRegistry.name;

import java.io.IOException;

@Component
public class RequestInstrumentor implements ClientHttpRequestInterceptor {

    private static final Logger log = LoggerFactory.getLogger(RequestInstrumentor.class);

    private final MetricRegistry metricRegistry;

    @Autowired
    public RequestInstrumentor(MetricRegistry metricRegistry) {
        this.metricRegistry = metricRegistry;
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        Histogram latencyHistogram = metricRegistry.histogram(name(request.getMethod().name(), request.getURI().toASCIIString(), "latency"));
        long startTime = System.currentTimeMillis();
        try {
            ClientHttpResponse httpResponse = execution.execute(request, body);
            latencyHistogram.update(System.currentTimeMillis() - startTime);
            markResponse(request, httpResponse.getStatusCode());
            logRequestMetrics(request, httpResponse.getStatusCode().name(), startTime);
            if (log.isDebugEnabled() && HttpMethod.POST.equals(request.getMethod())) {
                log.debug("Payload: {}", new String(body));
            }
            return httpResponse;
        } catch (HttpStatusCodeException e) {
            markResponse(request, e.getStatusCode());
            logRequestMetrics(request, e.getStatusCode().name(), startTime);
            throw e;
        } catch (IOException e) {
            metricRegistry.meter(name(request.getMethod().name(), request.getURI().toASCIIString(), "exceptions"));
            logRequestMetrics(request, "", startTime);
            throw e;
        }

    }

    private void markResponse(HttpRequest request, HttpStatus status) {
        Meter responseMeter = metricRegistry.meter(name(request.getMethod().name(), request.getURI().toASCIIString(), status.name()));
        responseMeter.mark();
    }

    private void logRequestMetrics(HttpRequest request, String status, long startTime) {
        log.info("method: {}  url: {}  status: {}  latency: {}", request.getMethod(), request.getURI().toASCIIString(), StringUtils.trim(status), System.currentTimeMillis() - startTime);
    }

}