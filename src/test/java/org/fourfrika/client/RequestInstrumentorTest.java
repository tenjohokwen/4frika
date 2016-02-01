package org.fourfrika.client;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import com.codahale.metrics.Histogram;
import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;
import org.fourfrika.client.mocks.MockClientAPIUtils;
import org.fourfrika.utils.MemoryAppender;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpStatusCodeException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Map;
import java.util.function.Predicate;

import static com.codahale.metrics.MetricRegistry.name;
import static org.fourfrika.client.mocks.MockClientAPIUtils.RequestResponse.*;
import static org.assertj.core.api.Assertions.*;

@Test
public class RequestInstrumentorTest {

    private static final String HIST_METRIC_NAME = name("GET", OK.getRequestUrl(), "latency");

    private static final String HOST = "http://client.localhost";

    private final MetricRegistry registry = new MetricRegistry();

    private final AbstractClient client = new TestClient(new RequestInstrumentor(registry)) {};

    private final MemoryAppender memAppender = new MemoryAppender();

    @BeforeClass
    public void setUp() throws Exception {
        memAppender.start();
        addMemAppenderToRequestInstrumentor();
    }

    @BeforeMethod
    public void beforeMethod() {
        registry.removeMatching(MetricFilter.ALL);
        memAppender.reset();
    }

    @DataProvider(name = "request_response_data")
    private MockClientAPIUtils.RequestResponse[][] failureData() {
        return new MockClientAPIUtils.RequestResponse[][] {
                { BAD_REQUEST},
                { NOT_FOUND},
                { SERVER_ERR}
        };
    }

    @Test(dataProvider = "request_response_data")
    public void verifyHttpErrorCodeMetricRecorded(MockClientAPIUtils.RequestResponse requestResponse) {
        MockClientAPIUtils.mockResponse(client, requestResponse);
        invokeErroneousRequest(requestResponse);
        assertMetricCount(requestResponse, 1L);
    }

    @Test(dataProvider = "request_response_data")
    public void verifyHttpErrorCodeLogged(MockClientAPIUtils.RequestResponse requestResponse) {
        MockClientAPIUtils.mockResponse(client, requestResponse);
        invokeErroneousRequest(requestResponse);
        assertMetricLogged(requestResponse);
    }

    private void invokeErroneousRequest(MockClientAPIUtils.RequestResponse requestResponse) {
        try {
            client.getRestTemplate().getForEntity(requestResponse.getRequestUrl(), Map.class);
            fail("Exception of super type 'HttpStatusCodeException' should be thrown");
        } catch (HttpStatusCodeException e) {
            assertThat(e.getStatusCode().name()).isEqualTo(requestResponse.codeName());
        }
    }

    public void given200ResponseShouldRecordMetric() {
        okRequest();
        assertMetricCount(OK, 1L);
    }

    public void given200ResponseShouldLogMetric() {
        okRequest();
        assertMetricLogged(OK);
    }

    public void verify200RecordedByUrl() {
        okRequest();

        MockClientAPIUtils.mockResponse(client, OK);
        client.getRestTemplate().getForEntity(OK.getRequestUrl() + "/path", Map.class);

        assertMetricCount(OK, 1L);
    }

    private void okRequest() {
        MockClientAPIUtils.mockResponse(client, OK);

        ResponseEntity<Map> responseEntity = client.getRestTemplate().getForEntity(OK.getRequestUrl(), Map.class);
        assertThat(responseEntity.getStatusCode().name()).isEqualTo(OK.codeName());
    }

    public void givenMixedResponsesShouldRecordMetrics() {
        final long badRequestCount = 3;
        final long notFoundCount = 2;
        final long serverErrCount = 7;
        final long okCount = 1;

        mixRequests(badRequestCount, notFoundCount, serverErrCount);

        assertMetricCount(BAD_REQUEST, badRequestCount);
        assertMetricCount(NOT_FOUND, notFoundCount);
        assertMetricCount(SERVER_ERR, serverErrCount);
        assertMetricCount(OK, okCount);

        Histogram histogram = registry.histogram(HIST_METRIC_NAME);
        assertThat(histogram.getCount()).isEqualTo(okCount);
    }

    private void assertMetricCount(MockClientAPIUtils.RequestResponse requestResponse, long expectedCount) {
        Meter responseMeter = registry.meter(name("GET", requestResponse.getRequestUrl(), requestResponse.codeName()));
        assertThat(responseMeter.getCount()).isEqualTo(expectedCount);
    }

    public void givenMixedResponsesShouldLogMetrics() {
        final long badRequestCount = 1;
        final long notFoundCount = 3;
        final long serverErrCount = 5;
        final long okCount = 1;

        mixRequests(badRequestCount, notFoundCount, serverErrCount);

        assertThat(loggedEventsMatchExpectedCount(BAD_REQUEST, badRequestCount)).isTrue();
        assertThat(loggedEventsMatchExpectedCount(NOT_FOUND, notFoundCount)).isTrue();
        assertThat(loggedEventsMatchExpectedCount(SERVER_ERR, serverErrCount)).isTrue();
        assertThat(loggedEventsMatchExpectedCount(OK, okCount)).isTrue();
    }

    private void assertMetricLogged(MockClientAPIUtils.RequestResponse requestResponse) {
        assertThat(memAppender.hasMatch(containsEventFn(requestResponse))).isTrue();
    }

    private boolean loggedEventsMatchExpectedCount(MockClientAPIUtils.RequestResponse requestResponse, long count) {
        return memAppender.countEvents(containsEventFn(requestResponse))  == count;
    }

    private Predicate<ILoggingEvent> containsEventFn(MockClientAPIUtils.RequestResponse requestResponse) {
        String expectedLog = String.format("method: GET  url: %s  status: %s  latency:", requestResponse.getRequestUrl(), requestResponse.codeName());
        return logEvent -> logEvent.getFormattedMessage().contains(expectedLog);
    }


    private void mixRequests(long badRequestCount, long notFoundCount, long serverErrCount) {
        MockClientAPIUtils.mockResponse(client, OK);
        client.getRestTemplate().getForEntity(OK.getRequestUrl(), Map.class);

        invokeXTimes(BAD_REQUEST, badRequestCount);
        invokeXTimes(NOT_FOUND, notFoundCount);
        invokeXTimes(SERVER_ERR, serverErrCount);
    }

    private void invokeXTimes(MockClientAPIUtils.RequestResponse requestResponse, long count) {
        for(int i = 0; i < count; i++) {
            invokeQuietly(requestResponse);
        }
    }

    private void invokeQuietly(MockClientAPIUtils.RequestResponse requestResponse) {
        MockClientAPIUtils.mockResponse(client, requestResponse);
        try {
            client.getRestTemplate().getForEntity(requestResponse.getRequestUrl(), Map.class);
        } catch (HttpStatusCodeException e) {
            //do nothing
        }
    }


    private class TestClient extends AbstractClient {
        protected TestClient(RequestInstrumentor requestInstrumentor) {
            super(requestInstrumentor, HOST);
        }
    }

    private void addMemAppenderToRequestInstrumentor() {
        Logger logger = (Logger) LoggerFactory.getLogger(RequestInstrumentor.class);
        logger.addAppender(memAppender);
        logger.setLevel(Level.INFO);
    }

}