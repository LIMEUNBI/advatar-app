package com.epopcon.advatar.common.util;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.epopcon.advatar.common.CommonLibrary;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.util.Calendar;

public class Utils {
    static final String TAG = Utils.class.getSimpleName();

    protected Utils() {

    }

    public static Context getApplicationContext() {
        return CommonLibrary.getContext();
    }

    public static String getMd5String(File file) {
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
            byte[] buffer = new byte[1024];
            MessageDigest digest = MessageDigest.getInstance("MD5");
            int numRead = 0;
            while (numRead != -1) {
                numRead = inputStream.read(buffer);
                if (numRead > 0)
                    digest.update(buffer, 0, numRead);
            }

            byte[] md5Bytes = digest.digest();

            String result = "";
            for (int i = 0; i < md5Bytes.length; i++) {
                result += Integer.toString((md5Bytes[i] & 0xff) + 0x100, 16).substring(1);
            }
            return result.toUpperCase();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        } finally {
            Utils.closeQuietly(inputStream);
        }
        return null;
    }

    /**
     * String객체를 TimeInMillisecond 형식으로 반환한다.
     *
     * @param arg : 날짜(문자열중 숫자만 8,12,14자리가 되어야함)
     *            8자리 : 20110901 (yyyyMMdd)
     *            12자리 : 201109011300 (yyyyMMddhhmi)
     *            14자리 : 20110901130030 (yyyyMMddhhmiss)
     * @return
     * @throws Exception
     */
    public static long parseDate(String arg) {
        Calendar cal = Calendar.getInstance();
        int year, month, day;
        int hour = 0;
        int minute = 0;
        int second = 0;

        String date = arg.replaceAll("\\D", "");

        if (date.length() == 7) {
            year = Integer.parseInt(date.substring(0, 4));
            month = Integer.parseInt(date.substring(4, 5));
            day = Integer.parseInt(date.substring(5, 7));
        } else if (date.length() == 8) {
            year = Integer.parseInt(date.substring(0, 4));
            month = Integer.parseInt(date.substring(4, 6));
            day = Integer.parseInt(date.substring(6, 8));
        } else if (date.length() == 12) {
            year = Integer.parseInt(date.substring(0, 4));
            month = Integer.parseInt(date.substring(4, 6));
            day = Integer.parseInt(date.substring(6, 8));
            hour = Integer.parseInt(date.substring(8, 10));
            minute = Integer.parseInt(date.substring(10, 12));
        } else if (date.length() == 14) {
            year = Integer.parseInt(date.substring(0, 4));
            month = Integer.parseInt(date.substring(4, 6));
            day = Integer.parseInt(date.substring(6, 8));
            hour = Integer.parseInt(date.substring(8, 10));
            minute = Integer.parseInt(date.substring(10, 12));
            second = Integer.parseInt(date.substring(12, 14));
        } else {
            throw new IllegalArgumentException("Unable to parse the date -> " + arg);
        }
        cal.set(year, month - 1, day, hour, minute, second);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }

    public static void closeQuietly(Object c) {
        try {
            if (c != null) {
                if (c instanceof Closeable)
                    ((Closeable) c).close();
                else if (c instanceof ObjectInput)
                    ((ObjectInput) c).close();
                else if (c instanceof ObjectOutput)
                    ((ObjectOutput) c).close();
            }
        } catch (Exception e) {

        }
    }

    public static boolean copyFile(File from, File to) {
        FileChannel src = null;
        FileChannel dst = null;
        try {
            src = new FileInputStream(from).getChannel();
            dst = new FileOutputStream(to).getChannel();
            dst.transferFrom(src, 0, src.size());
            return true;
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        } finally {
            closeQuietly(src);
            closeQuietly(dst);
        }
        return false;
    }

    public static long convertStringToHash(String str) {
        long hashCode = 0L;

        if (TextUtils.isEmpty(str)) {
            return hashCode;
        }

        str = str.replaceAll("\\n", "");
        str = str.replaceAll("[\\x0D\\x0A]", "");
        for (int i = 0; i < str.length(); i++) {
            hashCode = 257 * hashCode + str.charAt(i);
        }
        return hashCode;
    }
}
