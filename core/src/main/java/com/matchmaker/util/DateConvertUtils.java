package com.matchmaker.util;


import java.util.Calendar;
import java.util.Date;

public class DateConvertUtils {

    public static Date getStartOfDate(Date currDate) {
        // Create a Calendar instance and set the time to the current date
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currDate);

        // Set the time to the start of the day (00:00:00.000)
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        // Get the start of the day as a Date object
       return calendar.getTime();
    }

    public static Date getEndOfDate(Date currDate) {
        // Create a Calendar instance and set the time to the current date
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currDate);

        // Set the time to the start of the day (00:00:00.000)
        calendar.set(Calendar.HOUR_OF_DAY, 59);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);

        // Get the start of the day as a Date object
        return calendar.getTime();
    }

}
