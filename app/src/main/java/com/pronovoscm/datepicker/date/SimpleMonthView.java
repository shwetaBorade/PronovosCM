package com.pronovoscm.datepicker.date;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.util.AttributeSet;

public class SimpleMonthView extends MonthView {
    OnChangeDate onChangeDate;
    public SimpleMonthView(Context context, AttributeSet attr, DatePickerController controller,
                           OnChangeDate onChangeDate) {
        super(context, attr, controller, onChangeDate);
        this.onChangeDate= onChangeDate;
    }

    @Override
    public void drawMonthDay(Canvas canvas, int year, int month, int day,
                             int x, int y, int startX, int stopX, int startY, int stopY) {
        if (mSelectedDay == day) {
            canvas.drawCircle(x, y - (MINI_DAY_NUMBER_TEXT_SIZE / 3), DAY_SELECTED_CIRCLE_SIZE,
                    mSelectedCirclePaint);
        }

        if (isHighlighted(year, month, day) && mSelectedDay != day) {
            canvas.drawCircle(x, y + MINI_DAY_NUMBER_TEXT_SIZE - DAY_HIGHLIGHT_CIRCLE_MARGIN,
                    DAY_HIGHLIGHT_CIRCLE_SIZE, mSelectedCirclePaint);
            mMonthNumPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        } else {
            mMonthNumPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        }

        // gray out the day number if it's outside the range.
        if (mController.isOutOfRange(year, month, day)) {
            mMonthNumPaint.setColor(mDisabledDayTextColor);
        } else if (mSelectedDay == day) {
            mMonthNumPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
            mMonthNumPaint.setColor(mSelectedDayTextColor);
        } else if (mHasToday && mToday == day) {
            mMonthNumPaint.setColor(mTodayNumberColor);
        } else {
            mMonthNumPaint.setColor(isHighlighted(year, month, day) ? mHighlightedDayTextColor : mDayTextColor);
        }

        canvas.drawText(String.valueOf(day), x, y, mMonthNumPaint);
    }
}
