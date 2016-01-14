package org.fourfrika.config;

import com.fasterxml.jackson.datatype.hibernate4.Hibernate4Module;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
@Profile({"!dev"})
public class PooledDataSourceConfig {

    private final Logger log = LoggerFactory.getLogger(PooledDataSourceConfig.class);

    @Autowired
    private DataSourceProperties dataSourceProperties;

    @Autowired
    private Environment env;

    //@Autowired(required = false)
    //private MetricRegistry metricRegistry;

    @Bean(destroyMethod = "close")
    public DataSource dataSource() {
        RelaxedPropertyResolver relaxedPropertyResolver = new RelaxedPropertyResolver(env, "custom.datasource.");
        log.debug("Configuring Datasource");
        HikariConfig config = new HikariConfig();
        config.setDataSourceClassName(dataSourceProperties.getDriverClassName());
        config.addDataSourceProperty("url", dataSourceProperties.getUrl());
        config.addDataSourceProperty("user", StringUtils.defaultString(dataSourceProperties.getUsername()));
        config.addDataSourceProperty("password", StringUtils.defaultString(dataSourceProperties.getPassword()));

        //MySQL optimizations, see https://github.com/brettwooldridge/HikariCP/wiki/MySQL-Configuration
        config.addDataSourceProperty("cachePrepStmts", relaxedPropertyResolver.getProperty("cachePrepStmts"));
        config.addDataSourceProperty("prepStmtCacheSize", relaxedPropertyResolver.getProperty("prepStmtCacheSize"));
        config.addDataSourceProperty("prepStmtCacheSqlLimit", relaxedPropertyResolver.getProperty("prepStmtCacheSqlLimit"));
        /*if (metricRegistry != null) {
            config.setMetricRegistry(metricRegistry);
        }*/
        return new HikariDataSource(config);
    }


    /*
    TODO4frika
    For jackson to serialize entities with lazy-loading relations
    see http://stackoverflow.com/questions/21708339/avoid-jackson-serialization-on-non-fetched-lazy-objects/21760361#21760361
     */
    // TODO move this to the eventual web config
    @Bean
    public Hibernate4Module hibernate4Module() {
        return new Hibernate4Module();
    }
}
