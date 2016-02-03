package org.fourfrika.commons;

import java.time.ZonedDateTime;

public interface DateTimeService {

    /**
     * Returns the current date and time.
     * @return ZonedDateTime
     */
    ZonedDateTime getCurrentDateAndTime();
}