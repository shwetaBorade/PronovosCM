package com.pronovoscm.utils.dialogs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.widget.AppCompatSpinner;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.pronovoscm.PronovosApplication;
import com.pronovoscm.R;
import com.pronovoscm.adapter.CompanyAdapter;
import com.pronovoscm.adapter.TradeAdapter;
import com.pronovoscm.data.FieldPaperWorkProvider;
import com.pronovoscm.model.PunchListStatus;
import com.pronovoscm.persistence.domain.PunchlistAssignee;
import com.pronovoscm.persistence.domain.PunchlistDb;
import com.pronovoscm.persistence.repository.FieldPaperWorkRepository;
import com.pronovoscm.persistence.repository.PunchListRepository;
import com.pronovoscm.utils.PunchListFilterEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

@SuppressLint("ValidFragment")
public class PunchListFilterDialog extends DialogFragment {
    @Inject
    FieldPaperWorkRepository mFieldPaperWorkRepository;
    @Inject
    FieldPaperWorkProvider mFieldPaperWorkProvider;
    @Inject
    PunchListRepository mPunchListRepository;

    @BindView(R.id.assignedToSpinner)
    AppCompatSpinner assignedToSpinner;
    @BindView(R.id.statusSpinner)
    AppCompatSpinner statusSpinner;

    private List<PunchlistAssignee> mPunchlistAssignees;
    private List<PunchlistAssignee> sortAssignees;
    private TradeAdapter tradeAdapter;
    private CompanyAdapter statusAdapter;
    private int projectId;
    private List<PunchListStatus> statusList;
    private PunchListStatus mPunchListStatus;
    private PunchlistAssignee mPunchlistAssignee;
    private Context mActivity;
    private boolean linkExisting;

    @SuppressLint("ValidFragment")
    public PunchListFilterDialog(PunchListStatus punchListStatus, PunchlistAssignee punchlistAssignee, boolean linkExisting) {
        mPunchListStatus = punchListStatus;
        this.linkExisting = linkExisting;
        mPunchlistAssignee = punchlistAssignee;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.Theme_Translucent_Dialog);
        if (getArguments() != null) {
            projectId = getArguments().getInt("projectId");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dialog_filter, container, false);
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
        mPunchlistAssignees = new ArrayList<>();
        sortAssignees = new ArrayList<>();
        mPunchlistAssignees.add(new PunchlistAssignee(-1, -1, "All", true, projectId, false , false));
        List<PunchlistDb> punchlistDbs = mPunchListRepository.getFilterPunchList(projectId, linkExisting);
        for (PunchlistDb punchlistDb : punchlistDbs) {
            String assignedTo = punchlistDb.getAssignedTo().get(0); //TODO: Nitin
            if(punchlistDb.getAssignedTo().size() > 1){
                for(String assignee: punchlistDb.getAssignedTo()){
                    PunchlistAssignee punchlistAssignee = mFieldPaperWorkRepository.getAssignee(projectId, Integer.parseInt(assignee));
                    if (punchlistAssignee != null ) {
                        if(!contains(sortAssignees,punchlistAssignee))
                            sortAssignees.add(punchlistAssignee);
                    }
                }
            }else {
                if (!TextUtils.isEmpty(assignedTo)) {
                    PunchlistAssignee punchlistAssignee = mFieldPaperWorkRepository.getAssignee(projectId, Integer.parseInt(assignedTo));
                    if (punchlistAssignee != null && !contains(sortAssignees,punchlistAssignee)) {
                        sortAssignees.add(punchlistAssignee);
                    }
                }
            }
        }
        Collections.sort(sortAssignees, new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {
                PunchlistAssignee p1 = (PunchlistAssignee) o1;
                PunchlistAssignee p2 = (PunchlistAssignee) o2;
                return p1.getName().compareToIgnoreCase(p2.getName());
            }
        });
        mPunchlistAssignees.addAll(sortAssignees);
//        mPunchlistAssignees.stream().sorted();
        tradeAdapter = new TradeAdapter(getActivity(), R.layout.simple_spinner_item, mPunchlistAssignees);
        assignedToSpinner.setAdapter(tradeAdapter);

        assignedToSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                mPunchlistAssignee = mPunchlistAssignees.get(pos);
            }

            public void onNothingSelected(AdapterView<?> parent) {


            }
        });
        if (mPunchlistAssignee != null) {
            assigneeSpinnerSelection();
        }
        statusList = new ArrayList<>();
        statusList.add(PunchListStatus.All);
        statusList.add(PunchListStatus.Open);
        statusList.add(PunchListStatus.Complete);
        statusList.add(PunchListStatus.Approved);
        statusList.add(PunchListStatus.Rejected);

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
//        callAssigneeAPI();
    }

    private static boolean contains(List<PunchlistAssignee> list, PunchlistAssignee value) {
        Iterator<PunchlistAssignee> iterator = list.iterator();
        Boolean finalVal = false;
        while (iterator.hasNext()) {
            PunchlistAssignee punchlistAssignee = iterator.next();
            if (punchlistAssignee.getUsersId().equals(value.getUsersId())) {
                finalVal = true;
                break;
            }else {
                finalVal = false;
            }
        }
        return finalVal;
    }

    private void assigneeSpinnerSelection() {
        for (int i = 0; i < mPunchlistAssignees.size(); i++) {
            PunchlistAssignee punchlistAssignee = mPunchlistAssignees.get(i);
            if (punchlistAssignee != null) {

                int assigneeId = punchlistAssignee.getUsersId();
                if (mPunchlistAssignee.getUsersId() == assigneeId) {
                    assignedToSpinner.setSelection(i);
                }
            }
        }

    }

    private void statusSpinnerSelection() {
        for (int i = 0; i < statusList.size(); i++) {
            PunchListStatus listStatus = statusList.get(i);
            if (listStatus != null && mPunchListStatus != null && mPunchListStatus.getValue() == listStatus.getValue()) {
                statusSpinner.setSelection(i);
            }
        }
    }


    @OnClick(R.id.saveTextView)
    public void onSaveClick() {
        PunchListFilterEvent punchListFilterEvent = new PunchListFilterEvent(mPunchListStatus, mPunchlistAssignee);
        EventBus.getDefault().post(punchListFilterEvent);
        this.dismiss();

    }


    @OnClick(R.id.cancelTextView)
    public void onCancelClick() {
        this.dismiss();
    }
}
