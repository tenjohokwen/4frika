package org.fourfrika.client;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import org.fourfrika.commons.Constants;
import org.fourfrika.exceptions.UnexpectedHttpCodeException;
import org.springframework.http.*;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.constraints.NotNull;
import java.util.*;

import static org.fourfrika.commons.Constants.REQUEST_ID_HEADER_NAME;

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
     * @return "OK" string is the expected return type for health checks.
     */
    public String healthCheck() {
        final ResponseEntity<String> responseEntity = restTemplate.exchange(buildClientURL(heartbeatPath), HttpMethod.GET, new HttpEntity<>(httpHeaders()), String.class);
        if(responseEntity.getStatusCode().is2xxSuccessful()) {
            return Constants.HEARTBEAT_OK;
        }
        throw new UnexpectedHttpCodeException(
                responseEntity.getStatusCode(),
                responseEntity.getBody(),
                host + heartbeatPath,
                null,
                false,
                UUID.randomUUID());
    }

    /**
     * default impl wherein no headers needed
     * @return HttpHeaders headers needed by downstream server
     */
    protected HttpHeaders httpHeaders() {
        return null;
    }

    protected static HttpHeaders createHttpHeadersFrom(Map<String, String> keyValues) {
        if(keyValues != null) {
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setAll(keyValues);
            return httpHeaders;
        }
        return null;
    }

    /**
     * General method for doing http call.
     * If any common paramters have to be added, they are added here. E.g. Accept header.
     *
     * @param uri endpoint to hit
     * @param method method to use
     * @param body body to send
     * @param responseType type of the response
     *
     * @return http call response
     */
    protected <B, R> ResponseEntity<R> makeHttpRequest(String uri, HttpMethod method, B body,
                                                       @NotNull Class<R> responseType, @NotNull HttpHeaders headers) {
        HttpEntity<B> requestEntity;
        if (body != null) {
            requestEntity = new HttpEntity<>(body, headers);
        } else {
            requestEntity = new HttpEntity<>(headers);
        }
        return restTemplate.exchange(uri, method, requestEntity, responseType);
    }


    protected <B, R> ResponseEntity<R> makeHttpRequest(String xRequestId, String uri, HttpMethod method, B body, Class<R> responseType) {
        HttpHeaders headersToSend = new HttpHeaders();
        headersToSend.setAccept(ImmutableList.of(MediaType.APPLICATION_JSON));
        if (xRequestId != null) {
            headersToSend.add(REQUEST_ID_HEADER_NAME, xRequestId);
        }
        if (httpHeaders() != null) {
            headersToSend.putAll(httpHeaders());
        }
        return makeHttpRequest(uri, method, body, responseType, headersToSend);
    }



    protected <B> ResponseEntity<Map> makeHttpRequest(String xRequestId, String uri, HttpMethod method, B body) {
        return makeHttpRequest(xRequestId, uri, method, body, Map.class);
    }

    protected <B> ResponseEntity<Map> makeHttpRequest(String uri, HttpMethod method, B body) {
        return makeHttpRequest(null, uri, method, body, Map.class);
    }

}
