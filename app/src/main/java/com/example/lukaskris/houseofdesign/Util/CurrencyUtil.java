package com.example.lukaskris.houseofdesign.Util;

import com.google.common.base.Strings;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Created by xub on 01/08/17.
 */

public class CurrencyUtil {

    public static String rupiah(BigDecimal bdValue) {
        return "Rp " + formatDecimal(bdValue) + ",-";
    }

    public static String formatDecimal(BigDecimal bdValue) {
        if (bdValue == null) {
            return "0";
        }
        String fieldValue;
        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(new Locale("in", "id"));
        DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();
        formatter.setDecimalFormatSymbols(symbols);
        fieldValue = formatter.format(bdValue);
        return fieldValue;
    }


    /**
     * Check Whether String is Empty or Null
     *
     * @param compare
     * @return
     */
    public static boolean isEmptyStringOrNull(String compare) {

        boolean isEmpty = Strings.isNullOrEmpty(compare);

        if (isEmpty) return true;

        return compare.trim().equalsIgnoreCase("null");

    }

    public static boolean isEmptyStringOrNull(Object compare) {
        return isEmptyStringOrNull(String.valueOf(compare));
    }

    public static String prepend(String s, int length){
        StringBuilder sb = new StringBuilder(s);

        for (int i=0; i<length - s.length(); i++){
            sb.insert(0, "0");
        }
        return sb.toString();
    }

    /**
     * Merubah String ke targetlength, tidak dijamin keutuhan String asal
     *
     * @param s String
     * @param targetLength length yang diharapkan
     *
     * @return String yang terpotong
     */
    public static String toLength(String s, int targetLength){
        if (s.length() < targetLength){
            return prepend(s, targetLength - s.length());
        }else if (s.length() > targetLength){
            return s.substring(0, s.length() - targetLength);
        }else{
            return s;
        }
    }
}
