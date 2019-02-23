package com.zhuanche.common.util;

public class TimeUtils {

    private static final double MILLI_TO_SECOND = 1000.0;
    private static final double SECOND_TO_MINUTE = 60.0;
    private static final double MINUTE_TO_HOUR = 60.0;

    public static double milliToSecond(double milliSeconds){
        return milliSeconds / MILLI_TO_SECOND ;
    }

    public static double secondToMinute(double seconds){
        return seconds / SECOND_TO_MINUTE;
    }

    public static double minuteToHour(double minutes){
        return minutes / MINUTE_TO_HOUR;
    }

    public static double milliToMinute(double milliSeconds){
        return secondToMinute(milliToSecond(milliSeconds));
    }


}
