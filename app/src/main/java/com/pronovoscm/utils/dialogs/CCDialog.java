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
import com.pronovoscm.adapter.CCAdapter;
import com.pronovoscm.data.ProjectsProvider;
import com.pronovoscm.model.response.cclist.Cclist;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CCDialog extends DialogFragment implements View.OnClickListener, CCAdapter.updateTags {
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
    private CCAdapter mCCAdapter;

    private List<Cclist> mCclists;
    private List<Cclist> mSelectedAssigneeList;
    private List<Cclist> mCclistList;
    private int pjProjectId;

    public CCDialog() {
    }

    public static List<Cclist> cloneList(List<Cclist> imageTags) {
        ArrayList<Cclist> clonedList = new ArrayList<>(imageTags.size());
        for (Cclist imageTag : imageTags) {
            clonedList.add(imageTag);
        }
        return clonedList;
    }

    @SuppressLint("ValidFragment")
    public CCDialog(List<Cclist> selectedAssigneeList, List<Cclist> cclists) {
        mSelectedAssigneeList = cloneList(selectedAssigneeList);
        mCclists = cloneList(cclists);
        mCclistList = cloneList(cclists);
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
        mCCAdapter = new CCAdapter(this, mCclists, mSelectedAssigneeList);
        tagsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        tagsRecyclerView.setAdapter(mCCAdapter);
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                List<Cclist> assigneeListList = new ArrayList<>();
                for (int i = 0; i < mCclistList.size(); i++) {
                    if (mCclistList.get(i).getName().toLowerCase().contains(s.toString().toLowerCase())) {
                        assigneeListList.add(mCclistList.get(i));
                    }
                }
                mCclists.clear();
                mCclists.addAll(assigneeListList);
                mCCAdapter.notifyDataSetChanged();
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
                EventBus.getDefault().post(mSelectedAssigneeList);
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
    public void onUpdateSelectedTags(List<Cclist> selectedTag) {
        mSelectedAssigneeList = selectedTag;

    }
}

