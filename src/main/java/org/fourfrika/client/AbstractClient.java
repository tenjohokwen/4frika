package org.fourfrika.client;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Functionality common to clients
 */
public abstract class AbstractClient {

    private final String host;

    protected final RestTemplate restTemplate;

    protected final RequestInstrumentor requestInstrumentor;

    private String heartbeatPath;

    protected AbstractClient(RequestInstrumentor requestInstrumentor, String host) {
        this.requestInstrumentor = requestInstrumentor;
        restTemplate = new RestTemplate(new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory()));
        restTemplate.setInterceptors(Collections.singletonList(this.requestInstrumentor));
        restTemplate.setMessageConverters(messageConverters());
        this.host = host;
    }


    public RestTemplate getRestTemplate() {
        return this.restTemplate;
    }

    public String getHost() {
        return host;
    }

    protected List<HttpMessageConverter<?>> messageConverters() {
        List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
        MappingJackson2HttpMessageConverter jsonMessageConverter = new MappingJackson2HttpMessageConverter();

        jsonMessageConverter.setObjectMapper(objectMapper());

        messageConverters.add(jsonMessageConverter);
        return messageConverters;
    }

    protected ObjectMapper objectMapper() {
        return new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    protected String buildClientURL(String path) {
        return createUriComponentsBuilder(path).toUriString();
    }

    protected String buildClientURL(String path, Map<String, ?> uriVariables) {
        return createUriComponentsBuilder(path).buildAndExpand(uriVariables).toUriString();
    }

    private UriComponentsBuilder createUriComponentsBuilder(String path) {
        return UriComponentsBuilder.fromHttpUrl(host + path);
    }

    /**
     * Endpoint to check the health of the client.
     * The assumption here is that a 2xx/3xx http response status code implies that the client is up.
     * If a 4xx or 5xx is returned an exception is thrown to the invoker.
     * @return {@link HealthResponse} is the expected return type for health checks.
     */
    public HealthResponse healthCheck() {
        restTemplate.exchange(buildClientURL(heartbeatPath), HttpMethod.GET, new HttpEntity<>(httpHeaders()), Void.class);
        return HealthResponse.OK;
    }

    public enum HealthResponse {
        OK,
        DOWN
    }

    /**
     * default impl wherein no headers needed
     * @return HttpHeaders headers needed by downstream server
     */
    protected HttpHeaders httpHeaders() {
        return null;
    }


}
