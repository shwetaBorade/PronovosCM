package com.pronovoscm.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.listeners.OnDayClickListener;
import com.google.gson.Gson;
import com.pronovoscm.PronovosApplication;
import com.pronovoscm.R;
import com.pronovoscm.activity.ProjectFormActivity;
import com.pronovoscm.activity.ProjectFormDetailActivity;
import com.pronovoscm.adapter.ProjectFormCalendarAdapter;
import com.pronovoscm.chipslayoutmanager.util.log.Log;
import com.pronovoscm.data.ProjectFormProvider;
import com.pronovoscm.data.ProviderResult;
import com.pronovoscm.model.CalendarFormObject;
import com.pronovoscm.model.request.formcomponent.ProjectFormComponentRequest;
import com.pronovoscm.model.response.login.LoginResponse;
import com.pronovoscm.persistence.domain.Forms;
import com.pronovoscm.persistence.domain.FormsComponent;
import com.pronovoscm.persistence.domain.FormsName;
import com.pronovoscm.persistence.domain.FormsPermission;
import com.pronovoscm.persistence.domain.FormsSchedule;
import com.pronovoscm.persistence.domain.UserForms;
import com.pronovoscm.persistence.repository.ProjectFormRepository;
import com.pronovoscm.utils.Constants;
import com.pronovoscm.utils.SharedPref;
import com.pronovoscm.utils.ui.CustomProgressBar;

import org.dmfs.rfc5545.DateTime;
import org.dmfs.rfc5545.recur.InvalidRecurrenceRuleException;
import org.dmfs.rfc5545.recur.RecurrenceRule;
import org.dmfs.rfc5545.recur.RecurrenceRuleIterator;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

@SuppressLint("ValidFragment")
public class ProjectFormCalendarFragment extends Fragment {

    @BindView(R.id.calendarView)
    CalendarView customCalendar;
    @BindView(R.id.noRecordTextView)
    TextView noRecordTextView;
    @BindView(R.id.mainLayout)
    LinearLayout mainLayout;
    @BindView(R.id.listView)
    LinearLayout listView;
    @BindView(R.id.lineView)
    View lineView;
    // Create a HashMap
    HashMap<String, ArrayList<Object>> map = new HashMap<>();
    @Inject
    ProjectFormRepository mprojectFormRepository;
    @BindView(R.id.formsRV)
    RecyclerView formsRV;
    @Inject
    ProjectFormProvider mprojectFormProvider;
    private ArrayList<CalendarFormObject> formsArrayList = new ArrayList();
    private List<EventDay> events;
    private int projectId;
    private Calendar calendar;
    private Calendar endCalendar;
    private Calendar selectedCalendar;
    private ProjectFormCalendarAdapter projectFormAdapter;
    private EventDay eventDay = new EventDay(Calendar.getInstance());
    private int count;
    private boolean callByEventDayClick = false;
    private int currentYear;
    private LoginResponse loginResponse;
    ProjectFormCalendarAdapter.CalendarCardClickListner cardClickListner = new ProjectFormCalendarAdapter.CalendarCardClickListner() {

        @Override
        public void onCardItemClick(CalendarFormObject calendarFormObject, String selectedDate) {
            SimpleDateFormat sdformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            if (calendarFormObject.getUserForms() != null) {
                Forms actualForm = mprojectFormRepository.getActualForm(calendarFormObject.getUserForms().getFormId(), calendarFormObject.getUserForms().getRevisionNumber());
                if (actualForm != null) {
                    boolean formComponent = mprojectFormRepository.isFormComponentDataExist(actualForm.formsId, actualForm.originalFormsId, actualForm.revisionNumber);
                    if (formComponent)
                        launchUserFormDetail(calendarFormObject, selectedDate, actualForm.getFormsId(), actualForm.getRevisionNumber());
                    else {
                        downLoadUserFormComponent(calendarFormObject, selectedDate, actualForm.revisionNumber, actualForm.originalFormsId, actualForm.formsId);
                    }
                } else {
                    downloadProjectForm(calendarFormObject.getUserForms().getFormId(), projectId, calendarFormObject.getRevisionNumber(), selectedDate, calendarFormObject);
                }

            } else {
                LoginResponse loginResponse = (new Gson().fromJson(SharedPref.getInstance(getActivity()).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
                    getActivity().startActivity(new Intent(getActivity(), ProjectFormDetailActivity.class)
                            .putExtra("manage_date_change", true)
                            .putExtra("project_id", projectId)
                            .putExtra("schedule_form_id", calendarFormObject.getFormsSchedule().getScheduledFormId())
                            .putExtra("form_id", calendarFormObject.getForms().getFormsId())
                            .putExtra("due_date", selectedDate)
                            .putExtra(Constants.INTENT_KEY_ORIGINAL_FORM_ID, calendarFormObject.getForms().getOriginalFormsId())
                            .putExtra(Constants.INTENT_KEY_FORM_ACTIVE_REVISION_NUMBER, calendarFormObject.getForms().getRevisionNumber())
                            .putExtra(Constants.INTENT_KEY_FORM_CREATED_DATE, sdf.format(sdformat.parse(selectedDate)))
                            .putExtra(Constants.INTENT_KEY_FORM_CREATED_BY, loginResponse.getUserDetails().getFirstname() + " " + loginResponse.getUserDetails().getLastname())
                            .putExtra("form_type", "Sync"));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }
        }
    };
    Runnable dayFirstRunnable = (new Runnable() {
        @Override
        public void run() {
            if (customCalendar.getCurrentPageDate().get(Calendar.YEAR) != currentYear) {
                updateData(customCalendar.getCurrentPageDate());
            }
            if (!callByEventDayClick) {
                formsArrayList.clear();
                projectFormAdapter.notifyDataSetChanged();
                Calendar calendar = customCalendar.getCurrentPageDate();
                calendar.set(Calendar.DAY_OF_MONTH, 1);
                if (calendar.get(Calendar.MONTH) == Calendar.getInstance().get(Calendar.MONTH) && calendar.get(Calendar.YEAR) == Calendar.getInstance().get(Calendar.YEAR)) {
                    calendar.set(Calendar.DAY_OF_MONTH, Calendar.getInstance().get(Calendar.DAY_OF_MONTH));

                }

                customCalendar.setSelectedDate(calendar);
                setAdapter(calendar);
            }
            callByEventDayClick = false;
        }
    });


    @SuppressLint("ValidFragment")
    public ProjectFormCalendarFragment() {
    }

    @Override
    public void onResume() {
        super.onResume();
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(getActivity()).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        if (selectedCalendar != null) {
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    updateData(selectedCalendar);
                }
            });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(getActivity()).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        View rootView = inflater.inflate(R.layout.project_form_calendar, container, false);
        ButterKnife.bind(this, rootView);
        ((PronovosApplication) getActivity().getApplication()).getDaggerComponent().inject(this);
        Configuration newConfig = getActivity().getResources().getConfiguration();
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // set background for landscape
            mainLayout.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 0.5f);
            customCalendar.setLayoutParams(param);
            lineView.setVisibility(View.GONE);
            listView.setLayoutParams(param);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            mainLayout.setOrientation(LinearLayout.VERTICAL);
            // set background for portrait
            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lineView.setVisibility(View.VISIBLE);

            customCalendar.setLayoutParams(param);
            listView.setLayoutParams(param);
        }
        return rootView;
    }

    private void launchUserFormDetail(CalendarFormObject calendarFormObject, String selectedDate, int formID, int revisionNumber) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        getActivity().startActivity(new Intent(getActivity(), ProjectFormDetailActivity.class)
                .putExtra("project_id", projectId)
                .putExtra("schedule_form_id", calendarFormObject.getUserForms().getScheduleFormId())
                .putExtra("form_id", formID)
                .putExtra("user_form_id", calendarFormObject.getUserForms().getId())
                .putExtra("due_date", selectedDate)
                .putExtra(Constants.INTENT_KEY_ORIGINAL_FORM_ID, calendarFormObject.getOriginalFormId())
                .putExtra(Constants.INTENT_KEY_FORM_ACTIVE_REVISION_NUMBER, revisionNumber)
                .putExtra(Constants.INTENT_KEY_FORM_CREATED_DATE, sdf.format(calendarFormObject.getUserForms().getCreatedAt()))
                .putExtra(Constants.INTENT_KEY_FORM_CREATED_BY, calendarFormObject.getUserForms().getCreatedByUserName())
                .putExtra("form_type", "Sync"));
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        events = new ArrayList<>();
        calendar = Calendar.getInstance();
        endCalendar = Calendar.getInstance();
        projectId = getActivity().getIntent().getIntExtra("project_id", 0);
        customCalendar.setEvents(events);
        formsArrayList = new ArrayList<>();
        projectFormAdapter = new ProjectFormCalendarAdapter(getActivity(), formsArrayList, projectId, cardClickListner);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        formsRV.setLayoutManager(linearLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getActivity(), linearLayoutManager.getOrientation()) {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                int position = parent.getChildAdapterPosition(view);
                // hide the divider for the last child
                if (position == parent.getAdapter().getItemCount() - 1) {
                    outRect.setEmpty();
                } else {
                    super.getItemOffsets(outRect, view, parent, state);
                }
            }
        };
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(formsRV.getContext(), R.drawable.divider));
        formsRV.addItemDecoration(dividerItemDecoration);
        formsRV.setAdapter(projectFormAdapter);
        //        eventDay = new EventDay(Calendar.getInstance());
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                android.util.Log.d("fragment", " onview created  handler run: ");
                updateData(Calendar.getInstance());
            }
        });
        //        formsRV.setVisibility(View.GONE);
        //        customCalendar.setVisibility(View.GONE);
        customCalendar.setOnDayClickListener(new OnDayClickListener() {
            @Override
            public void onDayClick(EventDay eventDay) {

                ProjectFormCalendarFragment.this.eventDay = eventDay;
                setAdapter(eventDay.getmDay());
                if (customCalendar.getCurrentPageDate().get(Calendar.MONTH) != eventDay.getCalendar().get(Calendar.MONTH)) {
                    callByEventDayClick = true;
                    Log.i("Date ", eventDay.getCalendar().get(Calendar.MONTH) + "  onDayClick: " + customCalendar.getCurrentPageDate().get(Calendar.MONTH));
                    if ((customCalendar.getCurrentPageDate().get(Calendar.MONTH) == 11 && eventDay.getCalendar().get(Calendar.MONTH) == 0)) {

                        customCalendar.changeCalendar(1);
                    } else if ((customCalendar.getCurrentPageDate().get(Calendar.MONTH) == 0 && eventDay.getCalendar().get(Calendar.MONTH) == 11) || customCalendar.getCurrentPageDate().get(Calendar.MONTH) > eventDay.getCalendar().get(Calendar.MONTH)) {

                        customCalendar.changeCalendar(-1);
                    } else {
                        customCalendar.changeCalendar(+1);

                    }
                }
            }
        });
        customCalendar.setOnForwardPageChangeListener(() -> {
            if (customCalendar.getCurrentPageDate().get(Calendar.YEAR) != currentYear) {
                map.clear();
                events.clear();
                customCalendar.setEvents(events);
                if (!callByEventDayClick) {
                    formsArrayList.clear();
                    projectFormAdapter.notifyDataSetChanged();

                    Calendar calendar = customCalendar.getCurrentPageDate();
                    calendar.set(Calendar.DAY_OF_MONTH, 1);

                    if (calendar.get(Calendar.MONTH) == Calendar.getInstance().get(Calendar.MONTH) && calendar.get(Calendar.YEAR) == Calendar.getInstance().get(Calendar.YEAR)) {
                        calendar.set(Calendar.DAY_OF_MONTH, Calendar.getInstance().get(Calendar.DAY_OF_MONTH));

                    }

                    customCalendar.setSelectedDate(calendar);
                    setAdapter(calendar);
                }
            }
            new Handler().post(dayFirstRunnable);
        });
        customCalendar.setOnPreviousPageChangeListener(() -> {
            if (customCalendar.getCurrentPageDate().get(Calendar.YEAR) != currentYear) {
                map.clear();
                events.clear();
                customCalendar.setEvents(events);
                if (!callByEventDayClick) {
                    formsArrayList.clear();
                    projectFormAdapter.notifyDataSetChanged();
                    //            updateData(customCalendar.getCurrentPageDate());
                    Calendar calendar = customCalendar.getCurrentPageDate();
                    calendar.set(Calendar.DAY_OF_MONTH, 1);
                    if (calendar.get(Calendar.MONTH) == Calendar.getInstance().get(Calendar.MONTH) && calendar.get(Calendar.YEAR) == Calendar.getInstance().get(Calendar.YEAR)) {
                        calendar.set(Calendar.DAY_OF_MONTH, Calendar.getInstance().get(Calendar.DAY_OF_MONTH));

                    }
           /* if (eventDay == null) {
                eventDay = new EventDay(calendar);
            }
            eventDay.setmDay(calendar);*/
                    customCalendar.setSelectedDate(calendar);
                    setAdapter(calendar);
                }
            }
            new Handler().post(new Runnable() {
                @Override
                public void run() {

                    if (customCalendar.getCurrentPageDate().get(Calendar.YEAR) != currentYear) {

                        updateData(customCalendar.getCurrentPageDate());

                    }
                    if (!callByEventDayClick) {
                        formsArrayList.clear();
                        projectFormAdapter.notifyDataSetChanged();
                        //            updateData(customCalendar.getCurrentPageDate());
                        Calendar calendar = customCalendar.getCurrentPageDate();
                        calendar.set(Calendar.DAY_OF_MONTH, 1);
                        if (calendar.get(Calendar.MONTH) == Calendar.getInstance().get(Calendar.MONTH) && calendar.get(Calendar.YEAR) == Calendar.getInstance().get(Calendar.YEAR)) {
                            calendar.set(Calendar.DAY_OF_MONTH, Calendar.getInstance().get(Calendar.DAY_OF_MONTH));

                        }
           /* if (eventDay == null) {
                eventDay = new EventDay(calendar);
            }
            eventDay.setmDay(calendar);*/
                        customCalendar.setSelectedDate(calendar);
                        setAdapter(calendar);
                    }
                    callByEventDayClick = false;
                }
            });


            //                updateData(customCalendar.getCurrentPageDate());
        });

    }

    private void setAdapter(Calendar eventDay) {
        formsArrayList.clear();
        String formNameTitle = "";
        HashMap<Integer, FormsPermission> permissionHashMap = mprojectFormRepository.getProjectFormPermissionMap(projectId, loginResponse.getUserDetails().getUsers_id());
        projectFormAdapter.notifyDataSetChanged();
        Calendar cal = eventDay;
        SimpleDateFormat sdformat = new SimpleDateFormat("yyyy-MM-dd");
        projectFormAdapter.setSelectedDate(cal.getTime());
        Log.d("OPENFORM", sdformat.format(cal.getTime()) + "  Calendar setAdapter: map " + map);
        map.get(sdformat.format(cal.getTime()));
        if (map.containsKey(sdformat.format(cal.getTime())) && map.get(sdformat.format(cal.getTime())) != null) {
            ArrayList<Object> formList = map.get(sdformat.format(cal.getTime()));
            for (int i = 0; i < formList.size(); i++) {
                FormsName formName = null;
                if (formList.get(i) instanceof FormsSchedule) {
                    FormsSchedule formsSchedule = (FormsSchedule) formList.get(i);
                    android.util.Log.d("OPENFORM", "Calendar setAdapter: formsSchedule.getFormsId() " + formsSchedule.getFormsId() + "  ");
                    Forms forms = mprojectFormRepository.getFormDetails(projectId, formsSchedule.getFormsId(), loginResponse.getUserDetails().getUsers_id());
                    if (forms == null) {
                        forms = mprojectFormRepository.getScheduleFormOfLetestRevision(formsSchedule.getFormsId());
                    }
                    if (forms != null)
                        formName = mprojectFormRepository.getUserFormsName(forms.getOriginalFormsId(), forms.revisionNumber, projectId);
                    else
                        formName = mprojectFormRepository.getUserFormsName(formsSchedule.getFormsId(), projectId);
                    if (formName != null)
                        formNameTitle = formName.formName;
                    if (forms != null && checkPermissionExist(permissionHashMap, forms)) {
                        formsArrayList.add(new CalendarFormObject(formsSchedule, forms, formsSchedule.getFormsId(), formsSchedule.getFormsId(), formNameTitle));
                    }
                } else if (formList.get(i) instanceof UserForms) {
                    UserForms userForms = (UserForms) formList.get(i);
                    Log.e("OPENFORM", "Calendar setAdapter userForms  " + userForms);
                    Forms forms = mprojectFormRepository.getFormDetails(projectId, userForms.getFormId(), loginResponse.getUserDetails().getUsers_id());
                    formName = mprojectFormRepository.getUserFormsName(userForms.getFormId(), userForms.getRevisionNumber(), projectId);
                    Log.e("OPENFORM", "Calendar FormsName = " + formName);
                    if (formName != null)
                        formNameTitle = formName.formName;
                    if (forms != null && checkPermissionExist(permissionHashMap, forms)) {
                        formsArrayList.add(new CalendarFormObject(userForms, forms, userForms.getRevisionNumber(), userForms.getFormId(), formNameTitle));
                        //
                    } else if (forms == null && checkPermissionExist(permissionHashMap, userForms.getFormId())) {
                        formsArrayList.add(new CalendarFormObject(userForms, forms, userForms.getRevisionNumber(), userForms.getFormId(), formNameTitle));
                        //   downloadProjectForm(userForms.getFormId(),projectId,userForms.getRevisionNumber(),eventDay);
                    }
                }
            }
            projectFormAdapter.notifyDataSetChanged();
        }
        customCalendar.setSelectedDate(eventDay);
        if (getActivity() != null && getContext() != null) {

            if (formsArrayList.size() <= 0) {
                noRecordTextView.setVisibility(View.VISIBLE);
                noRecordTextView.setText(getString(R.string.form_calendar_no_record_message));
            } else {
                noRecordTextView.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
       /* if (getActivity().getSupportFragmentManager() != null) {
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .detach(this)
                    .attach(this)
                    .commit();

            customCalendar.setSelectedDate(eventDay.getmDay());
        }*/
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // set background for landscape
            mainLayout.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 0.5f);
            customCalendar.setLayoutParams(param);
            lineView.setVisibility(View.GONE);

            listView.setLayoutParams(param);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            mainLayout.setOrientation(LinearLayout.VERTICAL);
            // set background for portrait
            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lineView.setVisibility(View.VISIBLE);
            customCalendar.setLayoutParams(param);
            listView.setLayoutParams(param);
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
       /* new Handler().postDelayed(() -> {
            if (formsArrayList != null && projectFormAdapter != null && customCalendar != null) {
                selectedCalendar = null;
                formsArrayList.clear();
                projectFormAdapter.notifyDataSetChanged();
                customCalendar.getmViewPager().setCurrentItem(customCalendar.getmViewPager().getCurrentItem());
            }
        }, 2000);*/

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    private boolean checkPermissionExist(HashMap<Integer, FormsPermission> permissionHashMap, int originalFormsId) {
        if (permissionHashMap != null && permissionHashMap.size() > 0) {
            if (permissionHashMap.containsKey(originalFormsId)) {
                FormsPermission formsPermission = permissionHashMap.get(originalFormsId);
                if (formsPermission.getDeletedAt() != null || formsPermission.getIsActive() == 1) {
                    return true;
                }
            } else
                return true;
        } else
            return true;
        return false;
    }

    private boolean checkPermissionExist(HashMap<Integer, FormsPermission> permissionHashMap, Forms form) {
        if (permissionHashMap != null && permissionHashMap.size() > 0) {
            if (form != null && permissionHashMap.containsKey(form.originalFormsId)) {
                FormsPermission formsPermission = permissionHashMap.get(form.originalFormsId);
                if (formsPermission.getDeletedAt() != null || formsPermission.getIsActive() == 1) {
                    return true;
                }
            } else
                return true;
        } else
            return true;
        return false;
    }

    public void updateData(Calendar currentPageDate) {
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(getActivity()).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        HashMap<Integer, FormsPermission> permissionHashMap = mprojectFormRepository.getProjectFormPermissionMap(projectId, loginResponse.getUserDetails().getUsers_id());
        count = 0;
        selectedCalendar = currentPageDate;
        map.clear();
        if (events != null) {
            events.clear();
        } else {
            events = new ArrayList<>();
        }
        customCalendar.setEvents(events);
        List<FormsSchedule> formsSchedules = mprojectFormRepository.getProjectFormSchedule(projectId);

        for (FormsSchedule formsSchedule : formsSchedules) {
            Log.d("SCHEDULE", "Calendar *****updateData: projectId " + projectId + "  formsSchedule.getFormsId() " + formsSchedule.getFormsId());
            Forms forms = mprojectFormRepository.getFormDetails(projectId, formsSchedule.getFormsId(), loginResponse.getUserDetails().getUsers_id());
            if (forms == null) {
                forms = mprojectFormRepository.getScheduleFormOfLetestRevision(formsSchedule.getFormsId());
            }
            if (!TextUtils.isEmpty(formsSchedule.getRecurrence()) && checkPermissionExist(permissionHashMap, forms)) {
                Log.e("SCHEDULE", "Calendar OPENFORM *********  formsSchedule  " + formsSchedule);
                //                if (forms != null) {
                if (formsSchedule.getStartDate() != null && forms != null) {
                    //                    try {
                    Date d1 = formsSchedule.getStartDate();
                    calendar.set(currentPageDate.DAY_OF_MONTH, 1);
                    calendar.set(Calendar.MONTH, currentPageDate.get(currentPageDate.MONTH) - 1);
                    calendar.set(Calendar.YEAR, selectedCalendar.get(Calendar.YEAR));
                    endCalendar.setTime(d1);
                    endCalendar.set(Calendar.YEAR, currentPageDate.get(Calendar.YEAR));

                    Date d2 = calendar.getTime();


                    String day = (String) DateFormat.format("dd", formsSchedule.getStartDate()); // 20
                    String monthNumber = (String) DateFormat.format("MM", formsSchedule.getStartDate()); // 06
                    String year = (String) DateFormat.format("yyyy", formsSchedule.getStartDate()); // 2013
                    Log.i("Calendar", "  Date " + (currentPageDate.get(Calendar.YEAR) - 1) + " updateData: year " + year);
                    if (currentPageDate.get(Calendar.YEAR) - 1 > Integer.parseInt(year)) {
                        year = currentPageDate.get(Calendar.YEAR) - 1 + "";
                    }
                    currentYear = currentPageDate.get(Calendar.YEAR);
                    showScheduleOnCalendar(Integer.parseInt(day), Integer.parseInt(monthNumber) - 1, Integer.parseInt(year), formsSchedule, currentPageDate, endCalendar);
                }
            } else if (forms != null && checkPermissionExist(permissionHashMap, forms)) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(formsSchedule.getStartDate());
                SimpleDateFormat sdformat = new SimpleDateFormat("yyyy-MM-dd");
                List<UserForms> userForm = mprojectFormRepository.getUserFormDueDate(cal, formsSchedule.getFormsId(), formsSchedule.getPjProjectId(), formsSchedule.getScheduledFormId(), loginResponse.getUserDetails().getUsers_id());
                android.util.Log.e("Calendar", " OPENFORM ****** map put OPENFORM updateData: userForm " + userForm);
                if (userForm.size() <= 0) {
                    count++;
                    if (!map.containsKey(sdformat.format(cal.getTime()))) {
                        EventDay eventDay = new EventDay(cal, R.drawable.ic_circle_filled_blue);
                        events.add(eventDay);
                        ArrayList<Object> arrayList = new ArrayList<>();
                        map.put(sdformat.format(cal.getTime()), arrayList);
                    }
                    ArrayList<Object> arrayList = map.get(sdformat.format(cal.getTime()));
                    arrayList.add(formsSchedule);
                    map.put(sdformat.format(cal.getTime()), arrayList);
                }
                //                }
            }

          /*  if (!TextUtils.isEmpty(formsSchedule.getRecurrence())) {
                Log.e("Calendar", "Start: " + formsSchedule.getStartDate());
                Forms forms = mprojectFormRepository.getFormDetails(projectId, formsSchedule.getFormsId());
                //                if (forms != null) {
                if (formsSchedule.getStartDate() != null) {
                    //                    try {
                        //                    } else {
                        *//*String day = (String) DateFormat.format("dd", formsSchedule.getStartDate()); // 20
                        String monthNumber = (String) DateFormat.format("MM", formsSchedule.getStartDate()); // 06
                        String year = (String) DateFormat.format("yyyy", formsSchedule.getStartDate()); // 2013
                        showScheduleOnCalendar(Integer.parseInt(day), Integer.parseInt(monthNumber) - 1, Integer.parseInt(year), formsSchedule, currentPageDate);
                    }*//*

                }
            } else {
                Calendar cal = Calendar.getInstance();
                cal.setTime(formsSchedule.getStartDate());
                SimpleDateFormat sdformat = new SimpleDateFormat("yyyy-MM-dd");
                List<UserForms> userForm = mprojectFormRepository.getUserFormDueDate(cal, formsSchedule.getFormsId(), formsSchedule.getPjProjectId(), formsSchedule.getScheduledFormId());
                count++;

                if (userForm.size() <= 0) {
                    if (!map.containsKey(sdformat.format(cal.getTime()))) {
                        EventDay eventDay = new EventDay(cal, R.drawable.ic_circle_filled_blue);
                        events.add(eventDay);
                        ArrayList<Object> arrayList = new ArrayList<>();
                        map.put(sdformat.format(cal.getTime()), arrayList);
                    }
                    ArrayList<Object> arrayList = map.get(sdformat.format(cal.getTime()));
                    arrayList.add(formsSchedule);
                    map.put(sdformat.format(cal.getTime()), arrayList);
                }
                //                }
            }*/
        }
        List<UserForms> userFormsList = mprojectFormRepository.getUserForms(projectId, loginResponse.getUserDetails().getUsers_id());
        SimpleDateFormat sdformat = new SimpleDateFormat("yyyy-MM-dd");

        for (UserForms userForm : userFormsList) {
            count++;
            Forms forms = mprojectFormRepository.getFormDetails(projectId, userForm.getFormId(), loginResponse.getUserDetails().getUsers_id());
            //Forms forms = mprojectFormRepository.getActualForm(userForm.getFormId(), userForm.getRevisionNumber());

            if (forms != null && checkPermissionExist(permissionHashMap, forms)) {
                Log.d("Calendar", "OPENFORM #**********# updateData: forms " + forms + "  userForm  " + userForm);
                addUserFormOnCalendar(userForm, sdformat);
            }
            if (forms == null) {
                Log.e("Calendar", "OPENFORM #**####***# updateData: forms " + forms + "  userForm  " + userForm);
                if (checkPermissionExist(permissionHashMap, userForm.getFormId())) {
                    addUserFormOnCalendar(userForm, sdformat);
                }
            }

        }
       /* if (eventDay == null) {
            eventDay = new EventDay(calendar);
        }
        eventDay.setmDay(calendar);*/
        customCalendar.setSelectedDate(eventDay.getmDay());

        if (eventDay != null) {
            setAdapter(eventDay.getmDay());
        }
    }

    private void addUserFormOnCalendar(UserForms userForm, SimpleDateFormat sdformat) {
        Date eventDate = userForm.getDueDate() != null ? userForm.getDueDate() : userForm.getCreatedAt();
        Log.d("Calendar", "OPENFORM ## updateData: eventDate " + eventDate + "  userForm  " + userForm);
        Calendar cal = Calendar.getInstance();
        if (eventDate != null) {
            cal.setTime(eventDate);
            //  cal.set(Calendar.MONTH,cal.get(Calendar.MONTH)+1);
            if (!map.containsKey(sdformat.format(cal.getTime()))) {
                android.util.Log.i("Calendar", "OPENFORM *********     updateData: if " + sdformat.format(cal.getTime()));
                EventDay eventDay = new EventDay(cal, R.drawable.ic_circle_filled_blue);
                events.add(eventDay);
                ArrayList<Object> arrayList = new ArrayList<>();
                arrayList.add(userForm);
                map.put(sdformat.format(cal.getTime()), arrayList);
            } else {
                android.util.Log.i("Calendar", "OPENFORM updateData: ##**# else " + sdformat.format(cal.getTime()));
                ArrayList<Object> arrayList = map.get(sdformat.format(cal.getTime()));
                arrayList.add(userForm);
                map.put(sdformat.format(cal.getTime()), arrayList);
            }
            customCalendar.setEvents(events);
        }
    }

    private void showScheduleOnCalendar(int day, int month, int year, FormsSchedule formsSchedule, Calendar currentPageDate, Calendar cal1) {
        //  Log.i("Calendar rr ", "showScheduleOnCalendar: " + day + "/" + month + "/" + year + "  rule  " + formsSchedule.getRecurrence());
        RecurrenceRule rule = null;
        ArrayList<String> exDateList = new ArrayList<>();
        String[] exDates = null;
        RecurrenceRuleIterator it = null;
        DateTime start = new DateTime(year, month, day, 0, 0, 0);
        try {
            String rrule = formsSchedule.getRecurrence();
            int maxInstances = 0;
            //            if (!rrule.contains("EXDATE") ) {
            rule = new RecurrenceRule(formsSchedule.getRecurrence());
            // Log.e("Test", "showScheduleOnCalendar: " + rule);
            it = rule.iterator(start);
            maxInstances = 366;
            if (rrule.contains("EXDATE")) {
                String[] parts = formsSchedule.getRecurrence().toUpperCase().split(";");
                for (String keyvalue : parts) {
                    if (keyvalue.startsWith("EXDATE")) {
                        int equals = keyvalue.indexOf("=");
                        if (equals > 0) {
                            String key = keyvalue.substring(0, equals);
                            if (key.equals("EXDATE")) {
                                String value = keyvalue.substring(equals + 1);

                                Log.i("TEST", "showScheduleOnCalendar:value " + value);
                                exDates = value.toUpperCase().split(",");
                                //                                    exDateList = Arrays.asList(value.toUpperCase().split(","));
                            }
                        }
                        break;
                    }
                }

            }

            if (exDates != null) {
                exDateList.addAll(Arrays.asList(exDates));
            }

            while (it.hasNext() && (!rule.isInfinite() || maxInstances-- > 0)) {
                count++;
                DateTime nextInstance = it.nextDateTime();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd'T'HHmmss");
                Date date = null;
                Date date2 = null;
                try {
                    date = sdf.parse(nextInstance.toString());
                    date2 = sdf.parse(nextInstance.toString());
                    currentPageDate.set(Calendar.DAY_OF_MONTH, currentPageDate.getActualMaximum(Calendar.DAY_OF_MONTH));
                    currentPageDate.set(Calendar.MONTH, currentPageDate.MONTH + 2);
                    SimpleDateFormat sdformat = new SimpleDateFormat("yyyy-MM-dd");
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(date);
                    if (currentYear < cal.get(Calendar.YEAR)) {
                        // Log.i("Calendar", year + " end date showScheduleOnCalendar: == " + cal.get(Calendar.YEAR));
                        break;
                    }
                    if (currentYear > cal.get(Calendar.YEAR)) {
                        maxInstances = maxInstances + 1;
                        continue;
                    }

                    SimpleDateFormat exsdformat = new SimpleDateFormat("yyyyMMdd");
                    List<UserForms> userForm = mprojectFormRepository.getUserFormDueDate(cal, formsSchedule.getFormsId(), formsSchedule.getPjProjectId(), formsSchedule.getScheduledFormId(), loginResponse.getUserDetails().getUsers_id());
                    Log.e("Calendar", userForm + " \n  " + start + " OPENFORM formSchedule " + sdformat.format(cal.getTime())
                            + " new  == showScheduleOnCalendar: formSchedule "
                            + formsSchedule.getRecurrence() + " id " + formsSchedule.getFormsId()
                            + " schedule id " + formsSchedule.getScheduledFormId());
                    boolean excontain = exDateList.contains(exsdformat.format(cal.getTime()));

                    if (sdformat.format(cal.getTime()).equals("2020-01-16")) {
                        // Log.e("test", formsSchedule.getStartDate() + "userFormSize  " + userFormSize + " excontain = " + excontain + " rrule " + formsSchedule.getRecurrence() + " form schedule test " + cal.getTime());
                    }


                    if (!exDateList.contains(exsdformat.format(cal.getTime())) && userForm.size() <= 0) {
                        if (!map.containsKey(sdformat.format(cal.getTime()))) {
                            EventDay eventDay = new EventDay(cal, R.drawable.ic_circle_filled_blue);
                            events.add(eventDay);
                            ArrayList<Object> arrayList = new ArrayList<>();
                            arrayList.add(formsSchedule);
                            map.put(sdformat.format(cal.getTime()), arrayList);
                        } else {
                            ArrayList<Object> arrayList = map.get(sdformat.format(cal.getTime()));
                            arrayList.add(formsSchedule);
                            map.put(sdformat.format(cal.getTime()), arrayList);
                        }
                    }

                    customCalendar.setEvents(events);

                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        } catch (InvalidRecurrenceRuleException e) {
            //   Log.i("RRule", "showScheduleOnCalendar: " + formsSchedule.getRecurrence());
            e.printStackTrace();
        }
    }


    private void downloadProjectForm(int originalFormId, int projectId, int revisionNum, String selectedDate, CalendarFormObject calendarFormObject) {
        CustomProgressBar.showDialog(getActivity());
        ProjectFormComponentRequest projectOverviewRequest = new ProjectFormComponentRequest(originalFormId, projectId, revisionNum);
        mprojectFormProvider.getProjectFromUsingID(projectOverviewRequest, loginResponse, new ProviderResult<Forms>() {
            @Override
            public void success(Forms result) {
                CustomProgressBar.dissMissDialog(getActivity());
                Forms actualForm = mprojectFormRepository.getActualForm(originalFormId, revisionNum);
                if (actualForm != null)
                    downLoadUserFormComponent(calendarFormObject, selectedDate, revisionNum, originalFormId, actualForm.formsId);
            }

            @Override
            public void AccessTokenFailure(String message) {
                CustomProgressBar.dissMissDialog(getActivity());
            }

            @Override
            public void failure(String message) {
                CustomProgressBar.dissMissDialog(getActivity());
                ((ProjectFormActivity) getActivity()).showMessageAlert(getActivity(), message, getString(R.string.ok));

            }
        });
    }


    private void downLoadUserFormComponent(CalendarFormObject calendarFormObject, String selectedDate, int revisionNumber, int originalFormId, int formID) {
        CustomProgressBar.showDialog(getActivity());
        ProjectFormComponentRequest request = new ProjectFormComponentRequest(originalFormId, projectId, revisionNumber);
        mprojectFormProvider.getProjectFormComponents(request, loginResponse, new ProviderResult<List<FormsComponent>>() {
            @Override
            public void success(List<FormsComponent> result) {
                CustomProgressBar.dissMissDialog(getActivity());
                // setAdapter(eventDay);
                launchUserFormDetail(calendarFormObject, selectedDate, formID, revisionNumber);

            }

            @Override
            public void AccessTokenFailure(String message) {
                CustomProgressBar.dissMissDialog(getActivity());
                // refreshAdapter();
                //   setAdapter(eventDay);
            }

            @Override
            public void failure(String message) {
                CustomProgressBar.dissMissDialog(getActivity());
                ((ProjectFormActivity) getActivity()).showMessageAlert(getActivity(), message, getString(R.string.ok));
                //  setAdapter(eventDay);
            }
        });
    }
}
