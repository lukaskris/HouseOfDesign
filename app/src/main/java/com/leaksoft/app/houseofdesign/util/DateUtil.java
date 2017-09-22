package com.leaksoft.app.houseofdesign.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by lukaskris on 9/22/2017.
 */

public class DateUtil {
    private static final ThreadLocal<SimpleDateFormat> dateFormat = new ThreadLocal() {
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat();
        }
    };
    public static Date stringToDate(String sDate) {
        SimpleDateFormat sdf = (SimpleDateFormat)dateFormat.get();

        try {
            sdf.applyPattern("yyyy-MM-dd\'T\'HH:mm:ss.S\'Z\'");
            return sdf.parse(sDate);
        } catch (ParseException var3) {
            throw new RuntimeException(var3);
        }
    }

    public static Date stringToDate(String sDate, String format) {
        SimpleDateFormat sdf = (SimpleDateFormat)dateFormat.get();

        try {
            sdf.applyPattern(format);
            return sdf.parse(sDate);
        } catch (ParseException var4) {
            throw new RuntimeException(var4);
        }
    }

    public static String dateToString(Date date) {
        SimpleDateFormat df = (SimpleDateFormat)dateFormat.get();
        df.applyPattern("yyyy-MM-dd\'T\'HH:mm:ss.S\'Z\'");
        return df.format(date);
    }

    public static String dateToString(Date date, String format) {
        SimpleDateFormat df = (SimpleDateFormat)dateFormat.get();
        df.applyPattern(format);
        return df.format(date);
    }
}
