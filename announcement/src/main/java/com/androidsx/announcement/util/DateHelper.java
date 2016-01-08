package com.androidsx.announcement.util;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Androidsx on 18/12/15.
 */
public class DateHelper {

    private static final String TAG = DateHelper.class.getSimpleName();

    private DateHelper() {
        // Non-instantiate
    }

    public static boolean isBetweenDates(Date fromDate, Date toDate) {
        if (fromDate == null && toDate == null) {
            return true;
        }
        Calendar todayCal = Calendar.getInstance();
        Calendar fromCal = Calendar.getInstance();
        Calendar toCal = Calendar.getInstance();
        if (fromDate != null) {
            fromCal.setTime(fromDate);
        }
        if (toDate != null) {
            toCal.setTime(toDate);
        }

        if (fromDate == null) {
            return todayCal.before(toCal) || isToday(toCal);
        } else if (toDate == null) {
            return todayCal.after(fromCal) || isToday(fromCal);
        } else {
            return isToday(fromCal) || isToday(toCal) || (todayCal.after(fromCal) && todayCal.before(toCal));
        }
    }

    public static boolean isWeekDay(List<Integer> announcementDaysOfWeek) {
        Calendar today = Calendar.getInstance();
        return announcementDaysOfWeek.contains(today.get(Calendar.DAY_OF_WEEK));
    }

    public static boolean isToday(Calendar otherCal) {
        Calendar todayCal = Calendar.getInstance();
        return (todayCal.get(Calendar.DAY_OF_MONTH) == otherCal.get(Calendar.DAY_OF_MONTH)
                && todayCal.get(Calendar.MONTH) == otherCal.get(Calendar.MONTH)
                && todayCal.get(Calendar.YEAR) == otherCal.get(Calendar.YEAR));
    }
}
