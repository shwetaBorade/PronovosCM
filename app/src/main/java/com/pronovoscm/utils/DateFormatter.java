package com.pronovoscm.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Class for static date formatting and conversions.
 */
public class DateFormatter {
    //2018-11-06 16:58:29
    public static final String DEFAULT_DATE = "1970-01-01 01:01:01";
    public static final String PHOTO_DATE_FORMATE = "MMMM dd, yyyy hh:mm";
    public static final String DATE_FORMAT_MMDDYYYY = "MM/dd/yyyy";//"11/26/2018"
    public static final String DATE_FORMAT_TRANSFER = "MM/dd/yyyy hh:mm aa";//09/03/2019 12:00 AM
    public static final String TIME_FORMAT_TRANSFER = "hh:mm aa";//09/03/2019 12:00 AM
    private static final String TAG = LogUtils.makeLogTag(DateFormatter.class);
    private static final String SERVICE_DATETIME_FORMAT = "yyyy-MM-dd kk:mm:ss";//"1990-01-01 01:01:01"
    private static final String SERVICE_DATETIMEHH_FORMAT = "yyyy-MM-dd HH:mm:ss";//"1990-01-01 01:01:01"
    private static final String DATE_FORMAT_DAILYREPORT = "MM/dd/yyyy";//"1990-01-01 01:01:01"
    private static final String DATE_FORMAT_DAY_DATE = "EEEE MM/dd/yyyy";//"1990-01-01 01:01:01"
    public static final String SERVICE_DATE_FORMAT = "yyyy-MM-dd";//"1990-01-01 01:01:01"
    private static final String IMAGE_DATE_FORMAT = "MMM dd, yyyy";
    private static final String SERVICE_DATE = "yyyy-MM-dd";
    private static final String SERVICE_DATE_TIME = "yyyy-MM-dd kk:mm:ss";// 15:02:17
    private static final String SERVICE_DRAWING_DATE_TIME = "yyyy-MM-dd";// 15:02:17
    private static final String DISPLAY_DATE_FORMAT = "hh:mm a";
    private static final String DATE_FORMAT_PUNCH_LIST = "MM/dd/yy";//"11/26/18"
    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;

    /**
     * Formats the input date for display in the app, ie "hh:mm a" format.
     *
     * @param date A date object.
     * @return The formatted date string.
     */
    public static String formatDateForDisplay(Date date) {
        SimpleDateFormat displayDateFormat = new SimpleDateFormat(DISPLAY_DATE_FORMAT);
        displayDateFormat.setTimeZone(TimeZone.getDefault());
        return displayDateFormat.format(date);
    }

    public static String formatDateInMMDDYYYY(Date date) {
        SimpleDateFormat displayDateFormat = new SimpleDateFormat("MM/dd/yyyy");
        displayDateFormat.setTimeZone(TimeZone.getDefault());
        return displayDateFormat.format(date);
    }

    /**
     * Formats the input date for use on the api, ie "yyyy-MM-DD'T'hh:mm" format.
     *
     * @param date A date object.
     * @return The formatted date string.
     */
    public static String formatDateTimeForService(Date date) {//"1990-01-01 01:01:01"
        SimpleDateFormat serviceDateFormat = new SimpleDateFormat(SERVICE_DATETIME_FORMAT);
        return serviceDateFormat.format(date);
    }

    /**
     * Formats the input date for use on the api, ie "yyyy-MM-DD'T'hh:mm" format.
     *
     * @param date A date object.
     * @return The formatted date string.
     */
    public static String formatDateTimeHHForService(Date date) {//"1990-01-01 01:01:01"
        SimpleDateFormat serviceDateFormat = new SimpleDateFormat(SERVICE_DATETIMEHH_FORMAT);
        return serviceDateFormat.format(date);
    }   /**
     * Formats the input date for use on the api, ie "yyyy-MM-DD'T'hh:mm" format.
     *
     * @param date A date object.
     * @return The formatted date string.
     */
    public static String formatDate(Date date) {//"1990-01-01 01:01:01"
        SimpleDateFormat serviceDateFormat = new SimpleDateFormat(SERVICE_DATE);
        return serviceDateFormat.format(date);
    }

    /**
     * Formats the input date for use on the api, ie "yyyy-MM-DD'T'hh:mm" format.
     *
     * @param date A date object.
     * @return The formatted date string.
     */
    public static String formatDateForDailyReport(Date date) {//"1990-01-01 01:01:01"

        SimpleDateFormat serviceDateFormat = new SimpleDateFormat(DATE_FORMAT_DAY_DATE);
        if (date != null) {
            return serviceDateFormat.format(date);
        } else return "";
    }

    /**
     * Formats the input date for use on the api, ie "11/26/18" format.
     *
     * @param date A date object.
     * @return The formatted date string.
     */
    public static String formatDateForPunchList(Date date) {//"11/26/18"
        if (date != null) {
            SimpleDateFormat serviceDateFormat = new SimpleDateFormat(DATE_FORMAT_PUNCH_LIST);
            return serviceDateFormat.format(date);
        } else {
            return "NULL";
        }
    }

    public static String formatDateForPunchListLandingPage(Date date) {//"11/26/18"
        if (date != null) {
            SimpleDateFormat serviceDateFormat = new SimpleDateFormat("MM/dd/yyyy");
            return serviceDateFormat.format(date);
        } else {
            return "NULL";
        }
    }

    /**
     * Formats the input date for use on the api, ie "11/18/2022" format.
     *
     * @param date A date object.
     * @return The formatted date string.
     */
    public static String formatDateForSubmittals(Date date) {//"11/18/2022"
        if (date != null) {
            SimpleDateFormat serviceDateFormat = new SimpleDateFormat(DATE_FORMAT_MMDDYYYY);
            return serviceDateFormat.format(date);
        } else {
            return "NULL";
        }
    }


    /**
     * Formats the input date for use on the api, ie "yyyy-MM-DD'T'hh:mm" format.
     *
     * @param date A date object.
     * @return The formatted date string.
     */
    public static String formatDateForService(Date date) {//"1990-01-01"
        SimpleDateFormat serviceDateFormat = new SimpleDateFormat(SERVICE_DATE_FORMAT);
        return serviceDateFormat.format(date);
    }

    public static String formatDateForImage(Date date) {
        if (date != null) {
            SimpleDateFormat serviceDateFormat = new SimpleDateFormat(IMAGE_DATE_FORMAT);
            return serviceDateFormat.format(date);
        } else return "";
    }

    public static String currentDateIntoMMDDYYY(Date date) {
        DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
        String today = formatter.format(date);
        return today;
    }
    public static String formatDateForDrawing(Date date) {
        SimpleDateFormat serviceDateFormat = new SimpleDateFormat(SERVICE_DATE);
        return serviceDateFormat.format(date);
    }

    public static String convertformatDateForDrawing(String date) {
        SimpleDateFormat serviceDateFormat = new SimpleDateFormat(SERVICE_DATE);
        try {
            Date date1 = serviceDateFormat.parse(date);
            SimpleDateFormat serviceDateFormat1 = new SimpleDateFormat(DATE_FORMAT_MMDDYYYY);
            return serviceDateFormat1.format(date1);
        } catch (ParseException | IllegalArgumentException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static String formatAMDateForUploadImage(Date date) {
        if (date != null) {
            SimpleDateFormat serviceDateFormat = new SimpleDateFormat(PHOTO_DATE_FORMATE);

            SimpleDateFormat formatter = new SimpleDateFormat("a");
            String s = " " + formatter.format(date);
            return serviceDateFormat.format(date) + s.toUpperCase();
        } else return "";
    }

    /**
     * Converts a date string from the service format, ie "yyyy-MM-DD'T'hh:mm", to a Date object.
     *
     * @param dateString The date string in "yyyy-MM-DD'T'hh:mm" format.
     * @return The resulting date object.
     */
    public static Date getDateFromDateString(String dateString) {
        SimpleDateFormat serviceDateFormatZone = new SimpleDateFormat(SERVICE_DATE, Locale.US);
//        dateString += " " + Constants.API_TIMEZONE;
        try {
            return serviceDateFormatZone.parse(dateString);
        } catch (ParseException e) {
            LogUtils.LOGW(TAG, "Unable to parse dateString of: " + dateString);
        }
        return null;
    }

    public static Date getDateFromMMDDYYYYDateString(String dateString) {
        SimpleDateFormat serviceDateFormatZone = new SimpleDateFormat(DATE_FORMAT_MMDDYYYY, Locale.US);
//        dateString += " " + Constants.API_TIMEZONE;
        try {
            return serviceDateFormatZone.parse(dateString);
        } catch (ParseException e) {
            LogUtils.LOGW(TAG, DATE_FORMAT_MMDDYYYY + "   Unable to parse dateString of: " + dateString);
        }
        return null;
    }

    /**
     * Converts a date string from the service format, ie "yyyy-MM-DD'T'hh:mm", to a Date object.
     *
     * @param dateString The date string in "yyyy-MM-DD'T'hh:mm" format.
     * @return The resulting date object.
     */
    public static Date getDateFromTransferDate(String dateString) {
        SimpleDateFormat serviceDateFormatZone = new SimpleDateFormat(DATE_FORMAT_TRANSFER, Locale.US);
//        dateString += " " + Constants.API_TIMEZONE;
        try {
            return serviceDateFormatZone.parse(dateString);
        } catch (ParseException e) {
            LogUtils.LOGW(TAG, "Unable to parse dateString of: " + dateString);
        }
        return null;
    }

    /**
     * Converts a date string from the service format, ie "yyyy-MM-DD'T'hh:mm", to a Date object.
     *
     * @param dateString The date string in "yyyy-MM-DD'T'hh:mm" format.
     * @return The resulting date object.
     */
    public static Date getTimeFromTransferDate(String dateString) {
        SimpleDateFormat serviceDateFormatZone = new SimpleDateFormat(TIME_FORMAT_TRANSFER, Locale.US);
//        dateString += " " + Constants.API_TIMEZONE;
        try {
            return serviceDateFormatZone.parse(dateString);
        } catch (ParseException e) {
            LogUtils.LOGW(TAG, "Unable to parse dateString of: " + dateString);
        }
        return null;
    }

    /**
     * Converts a date string from the service format, ie "yyyy-MM-DD'T'hh:mm", to a Date object.
     *
     * @param dateString The date string in "yyyy-MM-DD'T'hh:mm" format.
     * @return The resulting date object.
     */
    public static Date getDateFromDateTimeString(String dateString) {
        SimpleDateFormat serviceDateFormatZone = new SimpleDateFormat(SERVICE_DATE_TIME, Locale.US);
//        dateString += " " + Constants.API_TIMEZONE;
        try {
            Date d = serviceDateFormatZone.parse(dateString);
            //  Log.e(TAG, "getDateFromDateTimeString: date "+d);
            return d;
        } catch (ParseException e) {
            LogUtils.LOGW(TAG, "Unable to parse dateString of: " + dateString);
        }
        return null;
    }

    /**
     * Converts a date string from the service format, ie "yyyy-MM-DD'T'hh:mm", to a Date object.
     *
     * @param dateString The date string in "yyyy-MM-DD'T'hh:mm" format.
     * @return The resulting date object.
     */
    public static Date getDateFromDateHHTimeString(String dateString) {
        SimpleDateFormat serviceDateFormatZone = new SimpleDateFormat(SERVICE_DATETIMEHH_FORMAT, Locale.US);
//        dateString += " " + Constants.API_TIMEZONE;
        try {
            return serviceDateFormatZone.parse(dateString);
        } catch (ParseException e) {
            LogUtils.LOGW(TAG, "Unable to parse dateString of: " + dateString);
        }
        return null;
    }

    /**
     * Converts a date string from the service format, ie "yyyy-MM-DD'T'hh:mm", to a Date object.
     *
     * @param dateString The date string in "yyyy-MM-DD'T'hh:mm" format.
     * @return The resulting date object.
     */
    public static Date getDateFromString(String dateString) {
        SimpleDateFormat serviceDateFormatZone = new SimpleDateFormat(DATE_FORMAT_DAILYREPORT, Locale.US);
//        dateString += " " + Constants.API_TIMEZONE;
        try {
            return serviceDateFormatZone.parse(dateString);
        } catch (ParseException e) {
            LogUtils.LOGW(TAG, "Unable to parse dateString of: " + dateString);
        }
        return null;
    }

    /**
     * Converts a date string from the service format, ie "yyyy-MM-DD'T'hh:mm", to a Date object.
     *
     * @param dateString The date string in "yyyy-MM-DD'T'hh:mm" format.
     * @return The resulting date object.
     */
    public static Date getDateFromDrawingDateString(String dateString) {
        SimpleDateFormat serviceDateFormatZone = new SimpleDateFormat(SERVICE_DRAWING_DATE_TIME, Locale.US);
//        dateString += " " + Constants.API_TIMEZONE;
        try {
            return serviceDateFormatZone.parse(dateString);
        } catch (ParseException e) {
            LogUtils.LOGW(TAG, "Unable to parse dateString of: " + dateString);
        }
        return null;
    }

    /**
     * Converts a date string from the service format, ie {@link }, to a Date object.
     *
     * @param dateString The date string in "yyyy-MM-DD'T'hh:mm" format.
     * @return The resulting date object.
     */
    public static Date getDateFromString(String dateString, String inputFormat) {
        SimpleDateFormat serviceDateFormatZone = new SimpleDateFormat(inputFormat, Locale.US);
//        dateString += " " + Constants.API_TIMEZONE;
        try {
            return serviceDateFormatZone.parse(dateString);
        } catch (ParseException e) {
            LogUtils.LOGW(TAG, "Unable to parse dateString of: " + dateString);
        }
        return null;
    }

    public static long currentTimeMillisLocal() {
        return Calendar.getInstance(TimeZone.getDefault()).getTimeInMillis();
    }

    public static String getTimeAgo(long time) {
        if (time < 1000000000000L) {
            // if timestamp given in seconds, convert to millis
            time *= 1000;
        }

        long now = System.currentTimeMillis();
        if (time > now || time <= 0) {
            return null;
        }

        // TODO: localize
        final long diff = now - time;
        if (diff < MINUTE_MILLIS) {
            return "Just Now";
        } else if (diff < 2 * MINUTE_MILLIS) {
            return "a minute ago";
        } else if (diff < 50 * MINUTE_MILLIS) {
            return diff / MINUTE_MILLIS + " minutes ago";
        } else if (diff < 90 * MINUTE_MILLIS) {
            return "an hour ago";
        } else if (diff < 24 * HOUR_MILLIS && (diff / HOUR_MILLIS) == 1) {
            return diff / HOUR_MILLIS + " hour ago";
        } else if (diff < 24 * HOUR_MILLIS) {
            return diff / HOUR_MILLIS + " hours ago";
        } else if (diff < 48 * HOUR_MILLIS) {
            return "yesterday";
        } else {
            return diff / DAY_MILLIS + " days ago";
        }
    }

    public static String formatDayDateForDailyReport(Date date) {
        SimpleDateFormat serviceDateFormat = new SimpleDateFormat(DATE_FORMAT_DAY_DATE);
        return serviceDateFormat.format(date);
    }

    public static String ordinal(int i) {
        String[] sufixes = new String[]{"th", "st", "nd", "rd", "th", "th",
                "th", "th", "th", "th"};
        switch (i % 100) {
            case 11:
            case 12:
            case 13:
                if (i < 10) {
                    return "0" + i + "th";
                } else {
                    return i + "th";

                }
            default:
                if (i < 10) {
                    return "0" + i + sufixes[i % 10];
                } else {
                    return i + sufixes[i % 10];

                }
        }
    }

    public static String formatWeekDays(Date startDate, Date endDate) {

        SimpleDateFormat month_date = new SimpleDateFormat("MMMM");
        SimpleDateFormat date = new SimpleDateFormat("dd");
        SimpleDateFormat year_date = new SimpleDateFormat("yyyy");
        int startDateVal = Integer.parseInt(date.format(startDate));
        int endDateVal = Integer.parseInt(date.format(endDate));

        return month_date.format(startDate) + " " + ordinal(startDateVal) + " - " + month_date.format(endDate) + " " + ordinal(endDateVal) + ", " + year_date.format(endDate);
    }

    public static String getDisplayDate(String dateString, String inputFormat, String outputFormat) {
        SimpleDateFormat inputFormatter = new SimpleDateFormat(inputFormat, Locale.US);
        SimpleDateFormat outputFormatter = new SimpleDateFormat(outputFormat, Locale.US);
        try{
            Date date = inputFormatter.parse(dateString);
            return outputFormatter.format(date);
        }catch (IllegalArgumentException e){
            LogUtils.LOGW(TAG, "Invalid dateString of: " + dateString);
        }catch (ParseException e){
            LogUtils.LOGW(TAG, "Unable to parse dateString of: " + dateString);
        }
        return null;
    }
}
