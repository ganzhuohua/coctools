package com.ssyj.coc;

import android.content.Context;
import android.content.SharedPreferences;

public class SpUtils {
    static Context context;
    static String name = "cocData";

    public static void setString(Context context, String key, String value) {
        SharedPreferences sp = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        SharedPreferences.Editor et = sp.edit();
        et.putString(key, value);
        et.commit();
    }

    public static String getString(Context context,String key) {
        SharedPreferences sp = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        return sp.getString(key, "null");
    }

    public static String getString(Context context, String key, String defVAlue) {
        SharedPreferences sp = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        return sp.getString(key, defVAlue);
    }

    public static void setBoolean(Context context, String key, Boolean value) {
        SharedPreferences sp = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        SharedPreferences.Editor et = sp.edit();
        et.putBoolean(key, value);
        et.commit();
    }

    public static boolean getBoolean(Context context, String key, Boolean defVAlue) {
        SharedPreferences sp = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        return sp.getBoolean(key, defVAlue);
    }
}

