package com.pronovoscm.utils.dialogs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.fragment.app.DialogFragment;
import com.pronovoscm.PronovosApplication;
import com.pronovoscm.R;
import com.pronovoscm.adapter.CompanyAdapter;
import com.pronovoscm.chipslayoutmanager.util.log.Log;
import com.pronovoscm.model.SubmittalStatusEnum;
import com.pronovoscm.persistence.repository.ProjectRfiRepository;
import com.pronovoscm.utils.Constants;
import com.pronovoscm.utils.SubmittalListFilterEvent;
import org.greenrobot.eventbus.EventBus;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

@SuppressLint("ValidFragment")
public class SubmittalListFilterDialog extends DialogFragment {

    @Inject
    ProjectRfiRepository projectRfiRepository;
    @BindView(R.id.statusSpinner)
    AppCompatSpinner statusSpinner;
    private CompanyAdapter statusAdapter;
    private int projectId;
    private List<SubmittalStatusEnum> statusList;
    private SubmittalStatusEnum submittalStatusEnum;
    private Context mActivity;

    @SuppressLint("ValidFragment")
    public SubmittalListFilterDialog(SubmittalStatusEnum submittalStatusEnum) {
        this.submittalStatusEnum = submittalStatusEnum;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.Theme_Translucent_Dialog);
        if (getArguments() != null) {
            projectId = getArguments().getInt(Constants.INTENT_KEY_PJ_PROJECT_ID);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dialog_submittal_filter, container, false);
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

        statusList = new ArrayList<>();
        statusList.add(SubmittalStatusEnum.All);
        statusList.add(SubmittalStatusEnum.Draft);
        statusList.add(SubmittalStatusEnum.Open);
        statusList.add(SubmittalStatusEnum.Closed);

        statusAdapter = new CompanyAdapter(getActivity(), R.layout.simple_spinner_item, statusList);
        statusSpinner.setAdapter(statusAdapter);
        statusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                submittalStatusEnum = statusList.get(pos);
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        statusSpinnerSelection();

    }

    private void statusSpinnerSelection() {
        for (int i = 0; i < statusList.size(); i++) {
            SubmittalStatusEnum listStatus = statusList.get(i);
            if (listStatus != null && submittalStatusEnum != null && submittalStatusEnum.getStatusValue() == listStatus.getStatusValue()) {
                statusSpinner.setSelection(i);
            }
        }
    }


    @OnClick(R.id.saveTextView)
    public void onSaveClick() {
        SubmittalListFilterEvent submittalListFilterEvent = new SubmittalListFilterEvent(submittalStatusEnum);
        EventBus.getDefault().post(submittalListFilterEvent);
        this.dismiss();
    }


    @OnClick(R.id.cancelTextView)
    public void onCancelClick() {
        this.dismiss();
    }
}
