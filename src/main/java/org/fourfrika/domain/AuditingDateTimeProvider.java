package org.fourfrika.domain;

import org.fourfrika.commons.DateTimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.GregorianCalendar;

@Component(AuditingDateTimeProvider.NAME)
public class AuditingDateTimeProvider implements DateTimeProvider {

    public static final String NAME = "dateTimeProvider";

    @Autowired
    private DateTimeService dateTimeService;

    @Override
    public Calendar getNow() {
        return GregorianCalendar.from(dateTimeService.getCurrentDateAndTime());
    }

}
