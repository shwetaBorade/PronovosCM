package com.pronovoscm.adapter;

import android.app.Activity;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.pronovoscm.PronovosApplication;
import com.pronovoscm.R;
import com.pronovoscm.model.CalendarFormObject;
import com.pronovoscm.persistence.repository.ProjectFormRepository;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProjectFormCalendarAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    @Inject
    ProjectFormRepository mprojectFormRepository;
    private int projectId;
    private List<CalendarFormObject> projectFormList;
    private Activity mActivity;
    private Date selectedDate;
    private long mLastClickTime = 0;
    CalendarCardClickListner cardClickListner;

    public ProjectFormCalendarAdapter(Activity mActivity, List<CalendarFormObject> ProjectFormList, int projectId, CalendarCardClickListner cardClickListner) {
        ((PronovosApplication) mActivity.getApplication()).getDaggerComponent().inject(this);
        this.projectFormList = ProjectFormList;
        this.mActivity = mActivity;
        this.projectId = projectId;
        this.cardClickListner = cardClickListner;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View view = inflater.inflate(R.layout.form_calendar_list_item, parent, false);
        return new ProjectFormCalendarHolder(view);

    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((ProjectFormCalendarHolder) holder).bind();
    }

    @Override
    public int getItemCount() {
        if (projectFormList != null) {
            return projectFormList.size();
        } else {
            return 0;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public void setSelectedDate(Date time) {
        selectedDate = time;
    }

    public interface CalendarCardClickListner {
        void onCardItemClick(CalendarFormObject calendarFormObject, String selectedDate);
    }

    public class ProjectFormCalendarHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.folderNameTextView)
        TextView folderNameTextView;
        @BindView(R.id.folderCardView)
        RelativeLayout folderCardView;
        @BindView(R.id.view)
        View view;

        public ProjectFormCalendarHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        private void bind() {

            Calendar cSelected = Calendar.getInstance();
            cSelected.setTime(selectedDate);
            cSelected.set(Calendar.HOUR, 0);
            cSelected.set(Calendar.MINUTE, 0);
            cSelected.set(Calendar.SECOND, 0);
            selectedDate = cSelected.getTime();
            Calendar cCurrent = Calendar.getInstance();
            cCurrent.set(Calendar.DAY_OF_MONTH, cCurrent.get(Calendar.DAY_OF_MONTH) - 1);
            cCurrent.set(Calendar.HOUR, 0);
            cCurrent.set(Calendar.MINUTE, 0);
            cCurrent.set(Calendar.SECOND, 0);
            Date curdate = cCurrent.getTime();
            CalendarFormObject calendarFormObject = projectFormList.get(getAdapterPosition());
            if (calendarFormObject.getUserForms() != null) {
                if (calendarFormObject.getUserForms() == null || calendarFormObject.getUserForms().getPublish() == null || calendarFormObject.getUserForms().getPublish() != 1) {
                    view.setBackgroundColor(ContextCompat.getColor(folderCardView.getContext(), R.color.blue_26a3e5));
                } else {
                    view.setBackgroundColor(ContextCompat.getColor(folderCardView.getContext(), R.color.green_4b9f63));
                }

            } else if (selectedDate.compareTo(curdate) >= 0) {
                view.setBackgroundColor(ContextCompat.getColor(folderCardView.getContext(), R.color.gray));
            } else {
                view.setBackgroundColor(ContextCompat.getColor(folderCardView.getContext(), R.color.red));
            }

            SimpleDateFormat sdformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String selectedDate = sdformat.format(cSelected.getTime());
            String userName = "";
            if (calendarFormObject.getUserForms() != null) {

                String[] strArray = calendarFormObject.getUserForms().getCreatedByUserName().split(" ");
                for (String str : strArray) {
                    if (str.length() > 0) {
                        userName = userName + str.charAt(0);
                    }
                }
            }
            Log.e("OPENCalendar", "bind: " + calendarFormObject.getFormName() + "  forms  " + calendarFormObject.getForms() + "   originalFormsId= "
                    + calendarFormObject.getOriginalFormId() + "   revisionNumber = " + calendarFormObject.getRevisionNumber());
            folderNameTextView.setText(calendarFormObject.getFormName() + (!TextUtils.isEmpty(userName) ? " (" + userName/*.toUpperCase() */ + ")" : ""));
            folderCardView.setOnClickListener(v -> {
                // Preventing multiple clicks, using threshold of 1 second
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                cardClickListner.onCardItemClick(calendarFormObject, selectedDate);
            });

        }
    }
}
