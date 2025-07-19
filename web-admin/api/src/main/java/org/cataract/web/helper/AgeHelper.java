package org.cataract.web.helper;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public class AgeHelper {

    private AgeHelper() {
        System.out.println("This is utility class");
    }


    public static int calculateAge(Date dateOfBirth) {
        if (dateOfBirth == null) {
            throw new IllegalArgumentException("Date of birth cannot be null");
        }
        LocalDate dob =  dateOfBirth.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        LocalDate today = LocalDate.now(); // Get the current date
        if (dob.isAfter(today)) {
            // throw new IllegalArgumentException("Date of birth cannot be in the future");
            return 0;
        }

        return Period.between(dob, today).getYears();
    }

    public static int calculateAge(Date birthDate, LocalDate baseTime) {

        if (birthDate == null) {
            throw new IllegalArgumentException("Date of birth cannot be null");
        }
        LocalDate dob = birthDate.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        baseTime = baseTime.plus(1, ChronoUnit.DAYS);
        if (dob.isAfter(baseTime)) {
            //throw new IllegalArgumentException("Date of birth cannot be after baseTime");
            return 0;
        }

        return Period.between(dob, baseTime).getYears();

    }
}
