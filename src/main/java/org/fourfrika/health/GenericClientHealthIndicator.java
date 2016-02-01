package org.fourfrika.health;

import org.fourfrika.client.AbstractClient;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;

public class GenericClientHealthIndicator extends AbstractHealthIndicator {

    private final AbstractClient client;

    public GenericClientHealthIndicator(AbstractClient client) {
        this.client = client;
    }

    @Override
    protected void doHealthCheck(Health.Builder builder) throws Exception {
        AbstractClient.HealthResponse healthResponse = client.healthCheck();
        builder = healthResponse == AbstractClient.HealthResponse.OK ? builder.up() : builder.down() ;
        builder.withDetail("message", healthResponse.toString());
    }
}
