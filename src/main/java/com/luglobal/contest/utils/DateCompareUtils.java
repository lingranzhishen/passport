package com.luglobal.contest.utils;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by ZHAOZENGJIE004 on 2017/5/22.
 */
public class DateCompareUtils {
    public static boolean isBetweenRange(Date source, Date upper, Date lower) {
        boolean result = false;

        if (upper == null && lower == null){
            result = true;
        }else if (upper == null){
            result = source.compareTo(lower) >= 0;
        }else if (lower == null){
            result = source.compareTo(upper) <= 0;
        }else {
            if (lower.compareTo(upper)>0){
                result = false;
            }
            return source.compareTo(upper) <= 0 && source.compareTo(lower) >= 0;
        }
        return result;
    }

 

}
