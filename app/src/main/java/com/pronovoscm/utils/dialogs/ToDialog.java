package com.pronovoscm.utils.dialogs;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pronovoscm.PronovosApplication;
import com.pronovoscm.R;
import com.pronovoscm.adapter.SelectToAdapter;
import com.pronovoscm.data.ProjectsProvider;
import com.pronovoscm.model.response.emailassignee.AssigneeList;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ToDialog extends DialogFragment implements View.OnClickListener, SelectToAdapter.updateAssignee {
    @Inject
    ProjectsProvider projectsProvider;
    @BindView(R.id.saveTextView)
    TextView saveTextView;
    @BindView(R.id.cancelTextView)
    TextView cancelTextView;
    @BindView(R.id.searchEditText)
    TextView searchEditText;
    @BindView(R.id.tagsRecyclerView)
    RecyclerView tagsRecyclerView;
    private SelectToAdapter mToAdapter;
    private AssigneeList mAssigneeList;
    private List<AssigneeList> mAssigneeListList;
    private List<AssigneeList> mAssigneeLists;
    private int pjProjectId;

    public ToDialog() {
    }

    public static List<AssigneeList> cloneList(List<AssigneeList> imageTags) {
        ArrayList<AssigneeList> clonedList = new ArrayList<>(imageTags.size());
        for (AssigneeList imageTag : imageTags) {
            clonedList.add(imageTag);
        }
        return clonedList;
    }

    @SuppressLint("ValidFragment")
    public ToDialog(AssigneeList assigneeList, List<AssigneeList> assigneeListList) {
        mAssigneeList = assigneeList;
        mAssigneeListList = cloneList(assigneeListList);
        mAssigneeLists = cloneList(assigneeListList);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.Theme_Translucent_Dialog);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.search_dialog_view, container, false);
        ButterKnife.bind(this, rootview);
        return rootview;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        cancelTextView.setOnClickListener(this);
        saveTextView.setOnClickListener(this);
        pjProjectId = getArguments().getInt("pjProjectId");

        ((PronovosApplication) getActivity().getApplication()).getDaggerComponent().inject(this);

        mToAdapter = new SelectToAdapter(this, mAssigneeListList, mAssigneeList);
        tagsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        tagsRecyclerView.setAdapter(mToAdapter);
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                List<AssigneeList> assigneeListList = new ArrayList<>();
                for (int i = 0; i < mAssigneeLists.size(); i++) {
                    if (mAssigneeLists.get(i).getName().toLowerCase().contains(s.toString().toLowerCase())) {
                        assigneeListList.add(mAssigneeLists.get(i));
                    }
                }
                mAssigneeListList.clear();
                mAssigneeListList.addAll(assigneeListList);
                mToAdapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.saveTextView:
                EventBus.getDefault().post(mAssigneeList);
                dismiss();
                break;
            case R.id.cancelTextView:
                dismiss();
                break;
            default:
                break;
        }
    }

    @Override
    public void onUpdateSelectedAssignee(AssigneeList assigneeList) {
        mAssigneeList = assigneeList;
    }
}

