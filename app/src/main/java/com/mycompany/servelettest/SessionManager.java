package com.mycompany.servelettest;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by som on 24/4/16.
 */
public class SessionManager {
    public void setPreferences(Context context, String key, String value) {

        SharedPreferences.Editor editor = context.getSharedPreferences("bloodHelp", Context.MODE_PRIVATE).edit();
        editor.putString(key, value);
        editor.commit();

    }


    public String getPreferences(Context context, String key) {

        SharedPreferences prefs = context.getSharedPreferences("bloodHelp", Context.MODE_PRIVATE);
        String position = prefs.getString(key, "");
        return position;
    }
}
