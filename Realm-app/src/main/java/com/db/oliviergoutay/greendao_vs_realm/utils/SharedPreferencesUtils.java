package com.db.oliviergoutay.greendao_vs_realm.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.security.SecureRandom;

/**
 * Created by oliviergoutay on 11/21/14.
 */
public final class SharedPreferencesUtils {

    /**
     * The key for SharedPreferences (private mode, internal system storage)
     */
    public static final String SHARED_PREFERENCES = "SEED_SHARED_PREFERENCES";

    /**
     * The length of the pseudo random constructed string
     */
    private static final int SEED_LENGTH = 64;

    /**
     * The space used by {@link java.util.Random#nextInt(int)}
     */
    private static final int SEED_SPACE = 96;

    /**
     * The key to save the seed in SharedPreferences (private mode, internal system storage)
     */
    public static final String SEED_KEY = "SEED_KEY";

    private SharedPreferencesUtils() {
    }

    /**
     * Generate a random string (used for database encryption)
     *
     * @return a random string of 64 characters
     */
    public static String generateRandomString() {
        SecureRandom generator = new SecureRandom();
        StringBuilder randomStringBuilder = new StringBuilder();
        char tempChar;
        int numbChar = 0;
        while (numbChar < SEED_LENGTH) {
            tempChar = (char) (generator.nextInt(SEED_SPACE) + 32);
            boolean num = tempChar >= 48 && tempChar < 58;//0-9
            boolean cap = tempChar >= 65 && tempChar < 91;//A-Z
            boolean lower = tempChar >= 65 && tempChar < 91;//a-z
            if (num || cap || lower) {
                randomStringBuilder.append(tempChar);
                numbChar++;
            }
        }
        return randomStringBuilder.toString();
    }

    /**
     * Store a String into the private {@link SharedPreferences} of the app.
     *
     * @param context The current context of the app
     * @param key     The key we want to be used to store the string
     * @param value   The String we want to be stored
     */
    public static void storeStringForKey(Context context, String key, String value) {
        if (context == null) {
            return;
        }
        SharedPreferences sharedPref = context.getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, value);
        editor.apply();
    }

    /**
     * Get a String from the private {@link SharedPreferences} of the app
     *
     * @param context The current context of the app
     * @param key     The key we want to request
     * @return The string retrieved from the given key
     */
    public static String getStringForKey(Context context, String key) {
        if (context == null) {
            return null;
        }
        SharedPreferences sharedPref = context.getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE);
        return sharedPref.getString(key, null);
    }

}
