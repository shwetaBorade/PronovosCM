package com.pronovoscm.datepicker.date;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * Controller Template class to communicate among the various components of the date picker dialog.
 */
public interface DatePickerController {

    void onYearSelected(int year);

    void onDayOfMonthSelected(int year, int month, int day);

    void registerOnDateChangedListener(DatePickerFragmentDialog.OnDateChangedListener listener);

    @SuppressWarnings("unused")
    void unregisterOnDateChangedListener(DatePickerFragmentDialog.OnDateChangedListener listener);

    MonthAdapter.CalendarDay getSelectedDay();

    boolean isThemeDark();

    int getAccentColor();

    boolean isHighlighted(int year, int month, int day);

    int getFirstDayOfWeek();

    int getMinYear();

    int getMaxYear();

    Calendar getStartDate();

    Calendar getEndDate();

    boolean isOutOfRange(int year, int month, int day);

    TimeZone getTimeZone();
}
