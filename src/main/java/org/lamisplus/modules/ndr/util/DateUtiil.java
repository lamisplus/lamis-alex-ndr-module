package org.lamisplus.modules.ndr.util;

import liquibase.datatype.DataTypeFactory;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;

public class DateUtiil {

    public static XMLGregorianCalendar getXmlDate(Date date)
        throws DatatypeConfigurationException {
        return DatatypeFactory.newInstance().newXMLGregorianCalendar(
                new SimpleDateFormat("yyyy-MM-dd").format(date)
        );
    }

    public static XMLGregorianCalendar getXmlDateTime(Date date)
            throws DatatypeConfigurationException {
        return DatatypeFactory.newInstance().newXMLGregorianCalendar(
                new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(date)
        );
    }
    @SneakyThrows
    public static XMLGregorianCalendar getXmlDate(String date)
            throws DatatypeConfigurationException {
        System.out.println("date corrected "+StringUtils.remove(date,'"'));
        return DatatypeFactory.newInstance().newXMLGregorianCalendar(StringUtils.remove(date,'"'));
    }
}
