package org.fourfrika.health;

import org.fourfrika.client.AbstractClient;
import org.fourfrika.commons.Constants;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;

public class GenericClientHealthIndicator extends AbstractHealthIndicator {

    private final AbstractClient client;

    public GenericClientHealthIndicator(AbstractClient client) {
        this.client = client;
    }

    @Override
    protected void doHealthCheck(Health.Builder builder) throws Exception {
        String healthResponse = client.healthCheck();
        builder = Constants.HEARTBEAT_OK.equals(healthResponse) ? builder.up() : builder.down() ;
        builder.withDetail("state", healthResponse);
    }
}
