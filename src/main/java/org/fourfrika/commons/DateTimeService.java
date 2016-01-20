package org.fourfrika.commons;

import java.time.ZonedDateTime;

/**
 * Created by mokwen on 09.09.15.
 */
public interface DateTimeService {

    /**
     * Returns the current date and time.
     * @return
     */
    ZonedDateTime getCurrentDateAndTime();
}