package org.fourfrika.commons;

import java.time.ZoneId;
import java.time.ZonedDateTime;

public class DateTimeServiceImpl implements DateTimeService {

    private final String ZONE_ID;

    public DateTimeServiceImpl(String zone_id) {
        super();
        ZONE_ID = zone_id;
    }

    @Override
    public ZonedDateTime getCurrentDateAndTime() {
        return ZonedDateTime.now(ZoneId.of(ZONE_ID));
    }

}
