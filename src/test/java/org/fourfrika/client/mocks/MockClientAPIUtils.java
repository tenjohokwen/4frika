package org.fourfrika.client.mocks;

import org.fourfrika.client.AbstractClient;
import org.hamcrest.core.StringContains;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.response.DefaultResponseCreator;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

/**
 * Created by mokwen on 01.02.16.
 */
public class MockClientAPIUtils {
    public static MockRestServiceServer createMockServer(AbstractClient client) {
        return MockRestServiceServer.createServer(client.getRestTemplate());
    }

    public static void mockResponse(AbstractClient client, RequestResponse reqRes) {
        MockRestServiceServer mockServer = MockRestServiceServer.createServer(client.getRestTemplate());
        mockServer.expect(requestTo(new StringContains(reqRes.getRequestUrl())))
                .andExpect(method(HttpMethod.GET))
                .andRespond(reqRes.getReponseCreator());
    }

    public enum RequestResponse {
        BAD_REQUEST(HttpStatus.BAD_REQUEST),
        NOT_FOUND(HttpStatus.NOT_FOUND),
        SERVER_ERR(HttpStatus.INTERNAL_SERVER_ERROR),
        OK(HttpStatus.OK);

        private static final String BASE_URL = "http://client.localhost/";
        private final HttpStatus httpStatus;

        RequestResponse(HttpStatus status) {
            httpStatus = status;
        }

        protected DefaultResponseCreator getReponseCreator(){
            return withStatus(this.httpStatus);
        }
        public String getRequestUrl() {
            return BASE_URL + httpStatus.value();
        }
        public String codeName() {
            return httpStatus.name();
        }
    }
}
