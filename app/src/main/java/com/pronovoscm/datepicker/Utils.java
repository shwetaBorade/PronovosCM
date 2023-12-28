
package com.pronovoscm.datepicker;

import android.content.res.Resources;
import android.util.TypedValue;

import java.util.Calendar;

/**
 * Utility helper functions for time and date pickers.
 */
public class Utils {
    /**
     * Convert Dp to Pixel
     */
    @SuppressWarnings("unused")
    public static int dpToPx(float dp, Resources resources) {
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.getDisplayMetrics());
        return (int) px;
    }


    /**
     * Trims off all time information, effectively setting it to midnight
     * Makes it easier to compare at just the day level
     *
     * @param calendar The Calendar object to trim
     * @return The trimmed Calendar object
     */
    public static Calendar trimToMidnight(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar;
    }
}
