/**
 * Project: Callga
 * Create At 2015-2-5.
 *
 * @author hhool
 */
package com.cchat.common.base.data;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtils {
    public final static String MYSQL_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss.S";
    public final static String YEAR_MONTH_DAY_HOUR_MM_SS = "yyyy-MM-dd HH:mm:ss";
    public final static String YEAR_MONTH_DAY_HOUR_MM = "yyyy-MM-dd HH:mm";
    public static final String FORMAT_FULL = "yyyy-MM-dd HH:mm:ss:SSS";

    public static long getMilliSecond(String format, String date) {
        long lTime = 0;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            Date dt2 = sdf.parse(date);
            lTime = dt2.getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lTime;
    }

    public static long getSeconds(String format, String date) {
        long lTime = getMilliSecond(format, date);
        if (lTime >= 0) {
            return lTime / 1000;
        }
        return lTime;
    }

    public static long getSystemMilliSecond() {
        java.util.Date dt = new Date();
        return dt.getTime();
    }

    public static Date getDate(String format, long milliSecond) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        java.util.Date dt = new Date(milliSecond);
        return dt;
    }

    public static String getTimeText(String format, long milliSecond) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        java.util.Date dt = new Date(milliSecond);
        return sdf.format(dt);
    }

    public static String mySqlTimeHack(String time) {
        long milliSeconds = timeStringToMilli(time);
        return getTimeText(YEAR_MONTH_DAY_HOUR_MM_SS, milliSeconds);
    }

    public static long timeStringToMilli(String time) {
        String format = YEAR_MONTH_DAY_HOUR_MM_SS;
        if (time.endsWith(".0")) {
//            SLogger.v("MySql Time:", time);
            time = time.substring(0, time.length() - 2);
        }
        return getMilliSecond(format, time);
    }
}
