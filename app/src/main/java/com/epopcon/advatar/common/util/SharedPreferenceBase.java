package com.epopcon.advatar.common.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.epopcon.advatar.common.CommonLibrary;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.Set;

import static com.epopcon.advatar.common.util.Utils.closeQuietly;
import static com.google.api.client.util.IOUtils.deserialize;
import static com.google.common.io.Files.toByteArray;

public class SharedPreferenceBase {
    static final String TAG = SharedPreferenceBase.class.getSimpleName();

    public static void putPrefString(Context context, String key, String value) {
        if (context == null) {
            context = CommonLibrary.getContext();
        }

        SharedPreferences prefs = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static String getPrefString(Context context, String key, String defValue) {
        if (context == null) {
            context = CommonLibrary.getContext();
        }

        SharedPreferences prefs = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        return prefs.getString(key, defValue);
    }

    public static void putPrefLong(Context context, String key, long value) {
        if (context == null) {
            context = CommonLibrary.getContext();
        }

        SharedPreferences prefs = context.getSharedPreferences(
                context.getPackageName(), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong(key, value);
        editor.commit();
    }

    public static long getPrefLong(Context context, String key, long defValue) {
        if (context == null) {
            context = CommonLibrary.getContext();
        }

        SharedPreferences prefs = context.getSharedPreferences(
                context.getPackageName(), Context.MODE_PRIVATE);
        return prefs.getLong(key, defValue);
    }

    public static void putPrefInt(Context context, String key, int value) {
        if (context == null) {
            context = CommonLibrary.getContext();
        }

        SharedPreferences prefs = context.getSharedPreferences(
                context.getPackageName(), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public static int getPrefInt(Context context, String key, int defValue) {
        if (context == null) {
            context = CommonLibrary.getContext();
        }

        SharedPreferences prefs = context.getSharedPreferences(
                context.getPackageName(), Context.MODE_PRIVATE);
        return prefs.getInt(key, defValue);
    }

    public static void putPrefFloat(Context context, String key, float value) {
        if (context == null) {
            context = CommonLibrary.getContext();
        }

        SharedPreferences prefs = context.getSharedPreferences(
                context.getPackageName(), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putFloat(key, value);
        editor.commit();
    }

    public static float getPrefFloat(Context context, String key, float defValue) {
        if (context == null) {
            context = CommonLibrary.getContext();
        }

        SharedPreferences prefs = context.getSharedPreferences(
                context.getPackageName(), Context.MODE_PRIVATE);
        return prefs.getFloat(key, defValue);
    }

    public static void putPrefBoolean(Context context, String key, boolean value) {
        if (context == null) {
            context = CommonLibrary.getContext();
        }

        SharedPreferences prefs = context.getSharedPreferences(
                context.getPackageName(), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public static boolean getPrefBoolean(Context context, String key, boolean defValue) {
        if (context == null) {
            context = CommonLibrary.getContext();
        }

        SharedPreferences prefs = context.getSharedPreferences(
                context.getPackageName(), Context.MODE_PRIVATE);
        return prefs.getBoolean(key, defValue);
    }

    public static void putPrefStringSet(Context context, String key, Set<String> value) {
        if (context == null) {
            context = CommonLibrary.getContext();
        }

        SharedPreferences prefs = context.getSharedPreferences(
                context.getPackageName(), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putStringSet(key, value);
        editor.commit();
    }

    public static Set<String> getPrefStringSet(Context context, String key, Set<String> defValue) {
        if (context == null) {
            context = CommonLibrary.getContext();
        }

        SharedPreferences prefs = context.getSharedPreferences(
                context.getPackageName(), Context.MODE_PRIVATE);
        return prefs.getStringSet(key, defValue);
    }

    public synchronized static void putPrefObject(Context context, String name, Object object) {
        serialize(new File(context.getDir("data", Context.MODE_PRIVATE), name), object);
    }

    public synchronized static Object getPrefObject(Context context, String name) {
        try {
            byte[] bytes = toByteArray(new File(context.getDir("data", Context.MODE_PRIVATE), name));
            if (bytes != null && bytes.length > 0)
                return deserialize(bytes);
        } catch (Exception e) {

        }
        return null;
    }

    public static void serialize(File file, Object object) {
        FileOutputStream fos = null;
        ObjectOutput out = null;
        try {
            fos = new FileOutputStream(file);
            out = new ObjectOutputStream(fos);

            out.writeObject(object);
            out.flush();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeQuietly(out);
            closeQuietly(fos);
        }
    }
}
