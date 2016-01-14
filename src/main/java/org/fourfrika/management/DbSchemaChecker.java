package org.fourfrika.management;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContextException;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

/**
 * For the actual environment, this class ensures that the configured db schema is up to date
 * It uses db versioning scripts in order to make the decision.
 * Annotated with '@Profile("!test")' and therefore will not execute when context is loaded for running tests because tests could run on even an empty db
 */
@Component
@Profile("!test")
public class DbSchemaChecker {

    private final Flyway flyway = new Flyway();

    @Autowired
    private DataSource dataSource;

    @Value("${db.name}")
    private String schema;

    @PostConstruct
    private void init() {
        flyway.setDataSource(dataSource);
        flyway.setSchemas(schema);
        validateDbSchema();
    }

    private void validateDbSchema() {
        MigrationInfo[] pending = flyway.info().pending();
        if (pending.length > 0) {
            throw new ApplicationContextException("The database still has pending updates");
        }
    }

}
