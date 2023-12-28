package com.pronovoscm.utils.dialogs;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.fragment.app.DialogFragment;

import com.pronovoscm.PronovosApplication;
import com.pronovoscm.R;
import com.pronovoscm.adapter.CompanyAdapter;
import com.pronovoscm.adapter.TradeAdapter;
import com.pronovoscm.model.RFIStatusEnum;
import com.pronovoscm.persistence.domain.PjRfi;
import com.pronovoscm.persistence.domain.PjRfiContactList;
import com.pronovoscm.persistence.repository.ProjectRfiRepository;
import com.pronovoscm.utils.Constants;
import com.pronovoscm.utils.DateFormatter;
import com.pronovoscm.utils.RfiListFilterEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

@SuppressLint("ValidFragment")
public class RfiListFilterDialog extends DialogFragment {

    @Inject
    ProjectRfiRepository projectRfiRepository;
    @BindView(R.id.datedueTv)
    TextView datedueTv;
    @BindView(R.id.dateSubmittedTv)
    TextView dateSubmittedTv;
    @BindView(R.id.assignedToSpinner)
    AppCompatSpinner assignedToSpinner;
    @BindView(R.id.statusSpinner)
    AppCompatSpinner statusSpinner;
    PjRfi pjRfi;
    private List<PjRfiContactList> mContactlistAssignees;
    private List<String> contactNameList;
    private TradeAdapter tradeAdapter;
    private CompanyAdapter statusAdapter;
    private int projectId;
    private List<RFIStatusEnum> statusList;
    private RFIStatusEnum mPunchListStatus;
    private PjRfiContactList mPunchlistAssignee;
    private Context mActivity;
    private boolean linkExisting;
    private Date currentDate;
    private Date dueDate;
    private Date dateSubmitted;
    private Calendar mCalendar, calendar;

    @SuppressLint("ValidFragment")
    public RfiListFilterDialog(RFIStatusEnum punchListStatus, PjRfiContactList punchlistAssignee, Date submittedDate, PjRfi linkExisting, Date datedue) {
        mPunchListStatus = punchListStatus;
        this.pjRfi = linkExisting;
        mPunchlistAssignee = punchlistAssignee;
        this.dueDate = datedue;
        this.dateSubmitted = submittedDate;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.Theme_Translucent_Dialog);
        if (getArguments() != null) {
            projectId = getArguments().getInt(Constants.INTENT_KEY_PJ_PROJECT_ID);
        }
        calendar = Calendar.getInstance();
        mCalendar = new GregorianCalendar(calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        mCalendar.set(Calendar.HOUR, 0);
        mCalendar.set(Calendar.MINUTE, 0);
        mCalendar.set(Calendar.SECOND, 0);
        currentDate = mCalendar.getTime();
        if (dueDate != null) {
            currentDate = dueDate;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dialog_rfi_filter, container, false);
        ((PronovosApplication) getActivity().getApplication()).getDaggerComponent().inject(this);
        ButterKnife.bind(this, rootView);
        setCancelable(true);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mActivity = getActivity();
        if (getArguments() != null) {
            projectId = getArguments().getInt("projectId");
        }
        mContactlistAssignees = new ArrayList<>();
        mContactlistAssignees.add(new PjRfiContactList(-1l, -1, -1, "All", "All", "All", 3, new Date(), new Date()));
        List<PjRfiContactList> punchlistDbs = projectRfiRepository.getSearchPjRfiContactList(projectId);
        contactNameList = new ArrayList<>();
        for (PjRfiContactList punchlistDb : punchlistDbs) {
            String assignedTo = punchlistDb.getName();
            if (!TextUtils.isEmpty(assignedTo)) {
                if (!contactNameList.contains(assignedTo)) {
                    mContactlistAssignees.add(punchlistDb);
                    contactNameList.add(assignedTo);
                }

            }
        }
        tradeAdapter = new TradeAdapter(getActivity(), R.layout.simple_spinner_item, mContactlistAssignees);
        assignedToSpinner.setAdapter(tradeAdapter);

        assignedToSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                mPunchlistAssignee = mContactlistAssignees.get(pos);
            }

            public void onNothingSelected(AdapterView<?> parent) {


            }
        });
        if (mPunchlistAssignee != null) {
            assigneeSpinnerSelection();
        }


        statusList = new ArrayList<>();
        statusList.add(RFIStatusEnum.All);
        statusList.add(RFIStatusEnum.Open);
        statusList.add(RFIStatusEnum.Draft);
        statusList.add(RFIStatusEnum.Closed);

        statusAdapter = new CompanyAdapter(getActivity(), R.layout.simple_spinner_item, statusList);
//        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        statusSpinner.setAdapter(statusAdapter);
        statusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                mPunchListStatus = statusList.get(pos);
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        statusSpinnerSelection();

        if (dueDate != null) {
            datedueTv.setText(DateFormatter.formatDateForPunchList(dueDate));
        }
        if (dateSubmitted != null) {
            dateSubmittedTv.setText(DateFormatter.formatDateForPunchList(dateSubmitted));
        }
    }

    private void assigneeSpinnerSelection() {
        for (int i = 0; i < mContactlistAssignees.size(); i++) {
            PjRfiContactList punchlistAssignee = mContactlistAssignees.get(i);
            if (punchlistAssignee != null) {

                int assigneeId = punchlistAssignee.getPjRfiContactListId();
                if (mPunchlistAssignee.getPjRfiContactListId() == assigneeId) {
                    assignedToSpinner.setSelection(i);
                }
            }
        }

    }

    private void statusSpinnerSelection() {
        for (int i = 0; i < statusList.size(); i++) {
            RFIStatusEnum listStatus = statusList.get(i);
            if (listStatus != null && mPunchListStatus != null && mPunchListStatus.getStatusValue() == listStatus.getStatusValue()) {
                statusSpinner.setSelection(i);
            }
        }
    }


    @OnClick(R.id.saveTextView)
    public void onSaveClick() {
        RfiListFilterEvent punchListFilterEvent = new RfiListFilterEvent(mPunchListStatus, mPunchlistAssignee, dueDate, dateSubmitted);
        EventBus.getDefault().post(punchListFilterEvent);
        this.dismiss();

    }


    @OnClick(R.id.cancelTextView)
    public void onCancelClick() {
        this.dismiss();
    }


    @OnClick(R.id.dateDueShowView)
    public void onDateClick() {
        Calendar calendar1 = new GregorianCalendar();
        calendar1.setTime(currentDate);
        int mYear = calendar1.get(Calendar.YEAR);
        int mMonth = calendar1.get(Calendar.MONTH);
        int mDay = calendar1.get(Calendar.DATE);
        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                (view, year, monthOfYear, dayOfMonth) -> {
                    Calendar calendar = new GregorianCalendar(year,
                            monthOfYear,
                            dayOfMonth);
                    dueDate = calendar.getTime();
                    datedueTv.setText(DateFormatter.formatDateForPunchList(dueDate));
                }, mYear, mMonth, mDay);

        datePickerDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                dueDate = null;
                datedueTv.setText("");
            }
        });
        // datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis() - 1000);
        datePickerDialog.show();
    }

    @OnClick(R.id.dateSubmittedShowView)
    public void onDateSubmittedClick() {
        Calendar calendar1 = new GregorianCalendar();
        calendar1.setTime(currentDate);
        int mYear = calendar1.get(Calendar.YEAR);
        int mMonth = calendar1.get(Calendar.MONTH);
        int mDay = calendar1.get(Calendar.DATE);
        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                (view, year, monthOfYear, dayOfMonth) -> {
                    Calendar calendar = new GregorianCalendar(year,
                            monthOfYear, dayOfMonth);
                    dateSubmitted = calendar.getTime();
                    dateSubmittedTv.setText(DateFormatter.formatDateForPunchList(dateSubmitted));
                }, mYear, mMonth, mDay);
        datePickerDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                dateSubmitted = null;
                dateSubmittedTv.setText("");
            }
        });
        datePickerDialog.show();
    }

}
