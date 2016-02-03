package org.fourfrika.service.core;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * Created by mokwen on 14.01.16.
 * This class provides the test context with beans needed just for tests
 * It is meant to be scanned and initialized by the Application class
 */
@Configuration
public class TestContext {

    @Autowired
    private DataSource dataSource;

    @Value("${db.name}")
    private String schema;

    /**
     * This bean is needed by @FlywayTest
     * @return Flyway
     */
    @Bean
    public Flyway init() {
        Flyway flyway = new Flyway();
        flyway.setDataSource(dataSource);
        flyway.setSchemas(schema);
        return flyway;
    }
}
