package com.cjmware.utils;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DateUtil {

    public static Date convertToDate(String dateAsString, String dateFormatString) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(dateFormatString);

        Date convertedDate = null;
        try {
            LocalDateTime localDateTime =  LocalDateTime.parse(dateAsString, dateTimeFormatter);
            convertedDate = Timestamp.valueOf(localDateTime);
        }
        catch(Exception e) {
            e.toString();
        }
        return convertedDate;
    }
}
