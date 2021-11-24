package org.lamisplus.modules.sync.utility;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.sql.Time;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Converter to persist LocalDate and LocalDateTime with
 * JPA 2.1 and Hibernate older than 5.0 version
 **/

@Converter(autoApply = true)
public class LocalTimeAttributeConverter implements AttributeConverter<LocalTime, Time> {

    @Override
    public Time convertToDatabaseColumn(LocalTime localTime) {
        return (localTime == null ? null : Time.valueOf(localTime));
    }

    @Override
    public LocalTime convertToEntityAttribute(Time time) {
        //clock-hour-of-am-pm (1-12)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a");

        return (time == null ? null : LocalTime.parse(formatter.format(time.toLocalTime()), formatter));
    }

}