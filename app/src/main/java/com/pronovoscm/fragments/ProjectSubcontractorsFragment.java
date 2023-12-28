package com.pronovoscm.fragments;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.pronovoscm.PronovosApplication;
import com.pronovoscm.R;
import com.pronovoscm.adapter.ProjectSubcontractorHeaderAdapter;
import com.pronovoscm.model.response.login.LoginResponse;
import com.pronovoscm.model.response.projectsubcontractors.SubcontractorData;
import com.pronovoscm.model.response.projectsubcontractors.Subcontractors;
import com.pronovoscm.persistence.repository.ProjectOverviewRepository;
import com.pronovoscm.services.NetworkService;
import com.pronovoscm.utils.SharedPref;
import com.pronovoscm.utils.ui.CustomProgressBar;
import com.pronovoscm.utils.ui.RecyclerSectionItemDecoration;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

@SuppressLint("ValidFragment")
public class ProjectSubcontractorsFragment extends Fragment {
    @Inject
    ProjectOverviewRepository projectOverviewRepository;
    @BindView(R.id.subcontractorRV)
    RecyclerView subcontractorRV;

    @BindView(R.id.noRecordTextView)
    TextView noRecordTextView;
    private ProjectSubcontractorHeaderAdapter projectSubcontractorHeaderAdapter;
    private ArrayList<Subcontractors> subcontractors = new ArrayList<>();
    private int projectID;
    private LoginResponse loginResponse;
    private int userId;
    private RecyclerSectionItemDecoration sectionItemDecoration;

    @SuppressLint("ValidFragment")
    public ProjectSubcontractorsFragment() {
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.project_subcontractors_fragment, container, false);
        ButterKnife.bind(this, rootView);
        ((PronovosApplication) getActivity().getApplication()).getDaggerComponent().inject(this);
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(getActivity()).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        userId = loginResponse.getUserDetails().getUsers_id();
        projectID = getActivity().getIntent().getIntExtra("project_id", 0);
        SubcontractorData result = projectOverviewRepository.getProjectSubcontractors(userId, projectID);
       /* if (result != null && result.getSubcontractors() != null) {
            subcontractors.clear();

            subcontractors.addAll(result.getSubcontractors());

            ArrayList<Subcontractors> teamArrayList = new ArrayList<>();
            for (Subcontractors subcontractors : subcontractors) {
                if (TextUtils.isEmpty(subcontractors.getAddress())) {
                    teamArrayList.add(subcontractors);
                }
            }
            for (Subcontractors subcontractors : teamArrayList) {
                subcontractors.remove(subcontractors);
            }

        }*/
        projectSubcontractorHeaderAdapter = new ProjectSubcontractorHeaderAdapter(getActivity(), subcontractors);
        subcontractorRV.setLayoutManager(new LinearLayoutManager(getActivity()));
        sectionItemDecoration = new RecyclerSectionItemDecoration(getResources().getDimensionPixelSize(R.dimen._14sdp), true, getSectionCallback(subcontractors));
        subcontractorRV.addItemDecoration(sectionItemDecoration);
        subcontractorRV.setAdapter(projectSubcontractorHeaderAdapter);
        refreshData(result);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    public void refreshData(SubcontractorData result) {

      /*  if (getContext() != null)
            CustomProgressBar.dissMissDialog(getContext());
*/
        if (getContext()!=null && result != null && result.getSubcontractors() != null && projectSubcontractorHeaderAdapter != null) {
            if (getContext() != null)
                CustomProgressBar.dissMissDialog(getContext());
            subcontractors.clear();

            subcontractors.addAll(result.getSubcontractors());

            ArrayList<Subcontractors> teamArrayList = new ArrayList<>();
            for (Subcontractors subcontractors : subcontractors) {
                if (TextUtils.isEmpty(subcontractors.getCompany())) {
                    teamArrayList.add(subcontractors);
                }
            }
            for (Subcontractors subcontractors : teamArrayList) {
                this.subcontractors.remove(subcontractors);
            }
            if (getContext()!=null&&subcontractors.size()==0){
                noRecordTextView.setText("Project Subcontractors have not yet been assigned to this project.");
                noRecordTextView.setVisibility(View.VISIBLE);
            }else if (getContext()!=null){
                noRecordTextView.setVisibility(View.GONE);
            }
            subcontractorRV.removeItemDecoration(sectionItemDecoration);
            sectionItemDecoration = new RecyclerSectionItemDecoration(getResources().getDimensionPixelSize(R.dimen._14sdp), true, getSectionCallback(subcontractors));
            subcontractorRV.addItemDecoration(sectionItemDecoration);
            projectSubcontractorHeaderAdapter.notifyDataSetChanged();
        }else {
            if (getContext() != null && NetworkService.isNetworkAvailable(getContext()))
                CustomProgressBar.showDialog(getContext());

        }
    }
    /**
     * Get the listener of the recycler view decorator to show the section in the recycler view
     *
     * @param drawingList
     * @return
     */
    private RecyclerSectionItemDecoration.SectionCallback getSectionCallback(final List<Subcontractors> drawingList) {
        return new RecyclerSectionItemDecoration.SectionCallback() {
            @Override
            public boolean isSection(int position) {
                if (position >= 0) {
                    return true;
                } else return false;
            }

            @Override
            public CharSequence getSectionHeader(int position) {
                if (position >= 0) {
                    return TextUtils.isEmpty(drawingList.get(position).getScope())?"-":drawingList.get(position).getScope();
                } else {
                    return "";
                }
            }
        };
    }
    public void refreshAdapter() {
        if (getContext()!=null && sectionItemDecoration!=null && projectSubcontractorHeaderAdapter!=null && subcontractorRV!=null){
            subcontractorRV.removeItemDecoration(sectionItemDecoration);
            sectionItemDecoration = new RecyclerSectionItemDecoration(getResources().getDimensionPixelSize(R.dimen._14sdp), true, getSectionCallback(subcontractors));
            subcontractorRV.addItemDecoration(sectionItemDecoration);
            projectSubcontractorHeaderAdapter.notifyDataSetChanged();

        }
    }

}
