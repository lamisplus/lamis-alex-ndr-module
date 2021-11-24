package org.lamisplus.modules.sync.utility;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Converter
public class LocalDateConverter implements AttributeConverter<LocalDate, Date> {

    @Override
    public Date convertToDatabaseColumn(LocalDate entityValue) {
        if (entityValue == null) {
            return null;
        }
        return Date.valueOf(entityValue);
    }

    @Override
    public LocalDate convertToEntityAttribute(Date databaseValue) {
        if (databaseValue == null) {
            return null;
        }
        // Converting 'dd-MM-yyyy' SQL date to LocalDate
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate localDate = LocalDate.parse(formatter.format(databaseValue.toLocalDate()), formatter);
        return localDate;
    }


    public Timestamp convertToDatabaseColumn(LocalDateTime localDateTime) {
        return (localDateTime == null ? null : Timestamp.valueOf(localDateTime));
    }



    public java.util.Date convertToTimeStamp (LocalDate date, LocalTime time) {
        LocalDateTime timestamp = LocalDateTime.of(date, time);
        return java.util.Date
                .from(timestamp.atZone(ZoneId.systemDefault())
                        .toInstant());
    }
}
