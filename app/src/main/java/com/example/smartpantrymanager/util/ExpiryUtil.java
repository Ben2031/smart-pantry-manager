package com.example.smartpantrymanager.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ExpiryUtil {

    /** Whole days from today until the given yyyy-MM-dd date (negative = already expired). */
    public static long daysUntil(String date) {
        try {
            SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            f.setLenient(false);
            Date expiry = f.parse(date);

            Calendar today = Calendar.getInstance();
            today.set(Calendar.HOUR_OF_DAY, 0);
            today.set(Calendar.MINUTE, 0);
            today.set(Calendar.SECOND, 0);
            today.set(Calendar.MILLISECOND, 0);

            double diff = expiry.getTime() - today.getTimeInMillis();
            return Math.round(diff / 86400000.0);
        } catch (ParseException | NullPointerException e) {
            return Long.MAX_VALUE;
        }
    }
}
