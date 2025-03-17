package org.cataract.web.helper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DateFormatHelper {

    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat yyyyMMddFormat = new SimpleDateFormat("yyyyMMdd");


    public static String date2StringSep(Date date) {
        return simpleDateFormat.format(date);
    }

    public static String date2StringSep(OffsetDateTime date) {
        return simpleDateFormat.format(date);
    }

    public static String date2StringWithoutSep(Date date) {
        return yyyyMMddFormat.format(date);
    }

    public static Date string2Date(String dateForm) throws ParseException {
        return simpleDateFormat.parse(dateForm);

    }

    public static String datetime2String(LocalDateTime date) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return date.format(formatter);
    }

    public static String date2StringSep(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return date.format(formatter);
    }

    private DateFormatHelper() {}

    public static int calculateAge(Date dateOfBirth) {
        if (dateOfBirth == null) {
            throw new IllegalArgumentException("Date of birth cannot be null");
        }
        LocalDate dob =  dateOfBirth.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        LocalDate today = LocalDate.now(); // Get the current date
        if (dob.isAfter(today)) {
            throw new IllegalArgumentException("Date of birth cannot be in the future");
        }

        return Period.between(dob, today).getYears();
    }

}
