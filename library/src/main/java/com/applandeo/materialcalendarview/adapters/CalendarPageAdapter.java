package com.applandeo.materialcalendarview.adapters;

import android.content.Context;
import androidx.viewpager.widget.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.applandeo.materialcalendarview.R;
import com.applandeo.materialcalendarview.extensions.CalendarGridView;
import com.applandeo.materialcalendarview.listeners.DayRowClickListener;
import com.applandeo.materialcalendarview.utils.CalendarProperties;
import com.applandeo.materialcalendarview.utils.SelectedDay;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.applandeo.materialcalendarview.utils.CalendarProperties.CALENDAR_SIZE;

/**
 * This class is responsible for loading a calendar page content.
 * <p>
 * Created by Mateusz Kornakiewicz on 24.05.2017.
 */

public class CalendarPageAdapter extends PagerAdapter {

    private Context mContext;
    private CalendarGridView mCalendarGridView;

    private CalendarProperties mCalendarProperties;


    private int mPageMonth;
    private int numberOfPossibleCells = 42;

    public CalendarPageAdapter(Context context, CalendarProperties calendarProperties) {
        mContext = context;
        mCalendarProperties = calendarProperties;
        informDatePicker();
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        informDatePicker();
    }

    @Override
    public int getCount() {
        return CALENDAR_SIZE;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mCalendarGridView = (CalendarGridView) inflater.inflate(R.layout.calendar_view_grid, null);

        loadMonth(position);

        mCalendarGridView.setOnItemClickListener(new DayRowClickListener(this,
                mCalendarProperties, mPageMonth));

        container.addView(mCalendarGridView);
        return mCalendarGridView;
    }

    public void addSelectedDay(SelectedDay selectedDay) {
        if (!mCalendarProperties.getSelectedDays().contains(selectedDay)) {
            mCalendarProperties.getSelectedDays().add(selectedDay);
            informDatePicker();
            return;
        }
        mCalendarProperties.getSelectedDays().remove(selectedDay);
        informDatePicker();
    }

    public List<SelectedDay> getSelectedDays() {
        return mCalendarProperties.getSelectedDays();
    }

    public SelectedDay getSelectedDay() {
        return mCalendarProperties.getSelectedDays().get(0);
    }

    public void setSelectedDay(SelectedDay selectedDay) {
        mCalendarProperties.setSelectedDay(selectedDay);
        informDatePicker();
    }

    /**
     * This method inform DatePicker about ability to return selected days
     */
    private void informDatePicker() {
        if (mCalendarProperties.getOnSelectionAbilityListener() != null) {
            mCalendarProperties.getOnSelectionAbilityListener().onChange(mCalendarProperties.getSelectedDays().size() > 0);
        }
    }

    /**
     * This method fill calendar GridView with days
     *
     * @param position Position of current page in ViewPager
     */
    private void loadMonth(int position) {
        ArrayList<Date> days = new ArrayList<>();

        // Get Calendar object instance
        Calendar calendar = (Calendar) mCalendarProperties.getFirstPageCalendarDate().clone();

        // Add months to Calendar (a number of months depends on ViewPager position)
        calendar.add(Calendar.MONTH, position);

        // Set day of month as 1
        calendar.set(Calendar.DAY_OF_MONTH, 1);

        // Get a number of the first day of the week
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

        // Count when month is beginning
        int firstDayOfWeek = calendar.getFirstDayOfWeek();
        int monthBeginningCell = (dayOfWeek < firstDayOfWeek ? 7 : 0) + dayOfWeek - firstDayOfWeek;

        // Subtract a number of beginning days, it will let to load a part of a previous month
        calendar.add(Calendar.DAY_OF_MONTH, -monthBeginningCell);

        Calendar calendarMonth = (Calendar) mCalendarProperties.getFirstPageCalendarDate().clone();
        calendarMonth.add(Calendar.MONTH, position);
        if (monthBeginningCell==0 && calendarMonth.getActualMaximum(Calendar.DAY_OF_MONTH)==28)
        {// feb start with sunday
            numberOfPossibleCells = 28;
        }
        if(monthBeginningCell<5 || monthBeginningCell >5 && calendarMonth.getActualMaximum(Calendar.DAY_OF_MONTH)<30)
        {
            numberOfPossibleCells = 35;
        }

        if (monthBeginningCell >5 && calendarMonth.getActualMaximum(Calendar.DAY_OF_MONTH)<30){
            //leap year
            numberOfPossibleCells = 35;
        } if (monthBeginningCell >= 5 && calendarMonth.getActualMaximum(Calendar.DAY_OF_MONTH)>30||
                monthBeginningCell == 6 && calendarMonth.getActualMaximum(Calendar.DAY_OF_MONTH) == 30){
            numberOfPossibleCells = 42;
        }
        if (monthBeginningCell == 5 && calendarMonth.getActualMaximum(Calendar.DAY_OF_MONTH) == 28||
                monthBeginningCell <= 4 && calendarMonth.getActualMaximum(Calendar.DAY_OF_MONTH) == 31){

            numberOfPossibleCells = 35;
        }

        /*
        Get all days of one page (42 is a number of all possible cells in one page
        (a part of previous month, current month and a part of next month))
         */
        while (days.size() < numberOfPossibleCells) {
            days.add(calendar.getTime());
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        mPageMonth = calendar.get(Calendar.MONTH) - 1;
        CalendarDayAdapter calendarDayAdapter = new CalendarDayAdapter(this, mContext,
                mCalendarProperties, days, mPageMonth);

        mCalendarGridView.setAdapter(calendarDayAdapter);

    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}
