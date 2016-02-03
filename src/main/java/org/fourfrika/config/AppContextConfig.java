package org.fourfrika.config;

import org.fourfrika.commons.DateTimeService;
import org.fourfrika.commons.DateTimeServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppContextConfig {

    @Bean
    public DateTimeService dateTimeService(@Value("${app.zone}")String zone_id) {
        return new DateTimeServiceImpl(zone_id);
    }
}
