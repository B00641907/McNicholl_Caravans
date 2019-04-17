package com.example.b00641907.mcnicholl_caravans.Utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {

    public static final String DATE_FORMAT_11 = "yyyy-MM-dd_HH:mm:ss";

    public static String dateToString(Date date, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }

    public static String toStringFormat_11(Date date) {
        if (date == null)
            return "";
        return dateToString(date, DATE_FORMAT_11);
    }



}
