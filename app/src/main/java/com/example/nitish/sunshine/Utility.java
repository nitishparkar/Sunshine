package com.example.nitish.sunshine;

/**
 * Created by nitish on 7/9/14.
 */
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Utility {
    public static String getPreferredLocation(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_pincode_key),
                context.getString(R.string.pref_pincode_default));
    }
}