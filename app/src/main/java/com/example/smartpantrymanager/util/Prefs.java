package com.example.smartpantrymanager.util;

import android.content.Context;
import android.content.SharedPreferences;

/** Wrapper around SharedPreferences for the Settings screen. */
public class Prefs {
    private static final String FILE = "smart_pantry_prefs";
    private static final String KEY_EXPIRY_ALERTS = "expiry_alerts";
    private static final String KEY_DEFAULT_UNIT = "default_unit";

    private static SharedPreferences prefs(Context c) {
        return c.getSharedPreferences(FILE, Context.MODE_PRIVATE);
    }

    public static boolean expiryAlertsEnabled(Context c) {
        return prefs(c).getBoolean(KEY_EXPIRY_ALERTS, true);
    }

    public static void setExpiryAlerts(Context c, boolean enabled) {
        prefs(c).edit().putBoolean(KEY_EXPIRY_ALERTS, enabled).apply();
    }

    public static String defaultUnit(Context c) {
        return prefs(c).getString(KEY_DEFAULT_UNIT, "g");
    }

    public static void setDefaultUnit(Context c, String unit) {
        prefs(c).edit().putString(KEY_DEFAULT_UNIT, unit).apply();
    }
}
