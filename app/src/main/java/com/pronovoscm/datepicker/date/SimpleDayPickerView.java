package com.pronovoscm.datepicker.date;

import android.content.Context;
import android.util.AttributeSet;

/**
 * A DayPickerView customized for {@link SimpleMonthAdapter}
 */
public class SimpleDayPickerView extends DayPickerView {
    public SimpleDayPickerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SimpleDayPickerView(Context context, DatePickerController controller, OnChangeDate onChangeDate) {
        super(context, controller,onChangeDate);
    }

    @Override
    public MonthAdapter createMonthAdapter(DatePickerController controller, OnChangeDate onChangeDate) {
        return new SimpleMonthAdapter(controller, onChangeDate);
    }
}
