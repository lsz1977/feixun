package com.cchat.common.base.data;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * User: villain
 * Date: 14-2-3
 */
public class VTimeUtil {
    public static final String FORMAT_SYEAR_MONTH_DAY = "yy-MM-dd";
    public static final String FORMAT_YEAR_MONTH_DAY = "yyyy-MM-dd";
    public static final String FORMAT_FULL = "yyyy-MM-dd HH:mm:ss:SSS";
    public static final String FORMAT_HOUR_MINUTE = "HH:mm";
    private static final long DAY_MILLIS = 86400000;

    public static long getTimeMillis() {
        return System.currentTimeMillis();
    }

    public static String getTimeMillisText() {
        return String.valueOf(getTimeMillis());
    }

    public static boolean isToday(long time) {
        Calendar calendar = Calendar.getInstance();
        int curYear = calendar.get(Calendar.YEAR);
        int curMethod = calendar.get(Calendar.MONTH);
        int curDay = calendar.get(Calendar.DAY_OF_YEAR);
        calendar.setTimeInMillis(time);
        return curDay == calendar.get(Calendar.DAY_OF_YEAR) && curMethod == calendar.get(Calendar.MONTH) && curYear == calendar.get(Calendar.YEAR);
    }

    public static String getTimeText(long time, String format) {
        Date date = new Date(time);
        return new SimpleDateFormat(format).format(date);
    }

    public static String getTimeText(String format) {
        return getTimeText(getTimeMillis(), format);
    }

    public static long getToday() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    public static long getTomorrow() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        return calendar.getTimeInMillis();
    }

    public static long getTime(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    public static void main(String[] args) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2014, 8, 7, 0, 0, 0);
//
        System.out.println(calendar.getTimeInMillis());
//        System.out.println(getTimeMillis());
//
//        System.out.println(isToday(calendar.getTimeInMillis()));
//        System.out.println(getTimeMillis() + (31l * 24l * 60l * 60l * 1000l));

    }
}
