package org.fourfrika.management;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

/**
 * For the actual environment, this class ensures that the configured db schema is up to date
 * It uses db versioning scripts in order to make the decision.
 */
@Component
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
            throw new IllegalStateException("The database still has pending updates");
        }
    }

}
