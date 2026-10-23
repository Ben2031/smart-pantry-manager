package com.example.smartpantrymanager.util;

import java.util.Locale;

public class Format {

    /** 200.0 -> "200", 1.5 -> "1.5" */
    public static String qty(double q) {
        if (q == Math.rint(q)) return String.valueOf((long) q);
        return String.format(Locale.US, "%.1f", q);
    }

    /** "chicken breast" -> "Chicken breast" */
    public static String capitalise(String s) {
        if (s == null || s.isEmpty()) return "";
        return s.substring(0, 1).toUpperCase(Locale.ROOT) + s.substring(1);
    }
}
