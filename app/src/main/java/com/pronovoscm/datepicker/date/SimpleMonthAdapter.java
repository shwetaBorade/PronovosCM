package com.pronovoscm.datepicker.date;

import android.content.Context;

/**
 * An adapter for a list of {@link SimpleMonthView} items.
 */
public class SimpleMonthAdapter extends MonthAdapter {
    OnChangeDate onChangeDate;
    public SimpleMonthAdapter(DatePickerController controller, OnChangeDate onChangeDate) {
        super(controller);
        this.onChangeDate= onChangeDate;
    }

    @Override
    public MonthView createMonthView(Context context) {
        return new SimpleMonthView(context, null, mController, onChangeDate);
    }
}
