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
import com.pronovoscm.adapter.ProjectResourceAdapter;
import com.pronovoscm.model.response.login.LoginResponse;
import com.pronovoscm.model.response.resources.ResourceData;
import com.pronovoscm.model.response.resources.Resources;
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
public class ProjectInHouseResourcesFragment extends Fragment {
    @Inject
    ProjectOverviewRepository projectOverviewRepository;
    @BindView(R.id.resourceRV)
    RecyclerView resourceRV;
    @BindView(R.id.noRecordTextView)
    TextView noRecordTextView;
    private ArrayList<Resources> resourceObjectList = new ArrayList<>();
    private ProjectResourceAdapter projectResourceAdapter;

    private int projectID;
    private LoginResponse loginResponse;
    private int userId;
    private RecyclerSectionItemDecoration sectionItemDecoration;

    @SuppressLint("ValidFragment")
    public ProjectInHouseResourcesFragment() {
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
        View rootView = inflater.inflate(R.layout.project_in_house_fragment, container, false);
        ButterKnife.bind(this, rootView);

        ((PronovosApplication) getActivity().getApplication()).getDaggerComponent().inject(this);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(getActivity()).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        userId = loginResponse.getUserDetails().getUsers_id();
        projectID = getActivity().getIntent().getIntExtra("project_id", 0);

        ResourceData result = projectOverviewRepository.getProjectResources(userId, projectID);


        resourceRV.setLayoutManager(new LinearLayoutManager(getContext()));
        projectResourceAdapter = new ProjectResourceAdapter(getActivity(), resourceObjectList);

        sectionItemDecoration = new RecyclerSectionItemDecoration(getResources().getDimensionPixelSize(R.dimen._14sdp), true, getSectionCallback(resourceObjectList));
        resourceRV.addItemDecoration(sectionItemDecoration);
        resourceRV.setAdapter(projectResourceAdapter);
        refreshData(result);
    }

    public void refreshData(ResourceData result) {
        if (getContext()!=null && result != null && result.getResources() != null) {
            if (getContext() != null)
                CustomProgressBar.dissMissDialog(getContext());
            resourceObjectList.clear();
            resourceObjectList.addAll(result.getResources());
            ArrayList<Resources> teamArrayList = new ArrayList<>();
            for (Resources team : resourceObjectList) {
                if (team == null || team.getUsers() == null || team.getUsers().size() == 0) {
                    teamArrayList.add(team);
                }
            }
            for (Resources team : teamArrayList) {
                resourceObjectList.remove(team);
            }
            if (getContext() != null && resourceObjectList.size() == 0) {
                noRecordTextView.setText("No project in-house resources");
                noRecordTextView.setVisibility(View.VISIBLE);
            } else if (getContext() != null) {
                noRecordTextView.setVisibility(View.GONE);
            }
            resourceRV.removeItemDecoration(sectionItemDecoration);
            sectionItemDecoration = new RecyclerSectionItemDecoration(getResources().getDimensionPixelSize(R.dimen._14sdp), true, getSectionCallback(resourceObjectList));
            resourceRV.addItemDecoration(sectionItemDecoration);
            projectResourceAdapter.notifyDataSetChanged();

        } else {
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
    private RecyclerSectionItemDecoration.SectionCallback getSectionCallback(final List<Resources> drawingList) {
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
                    return TextUtils.isEmpty(drawingList.get(position).getProjectRoleName())?"-":drawingList.get(position).getProjectRoleName();
                } else {
                    return "";
                }
            }
        };
    }

    public void refreshAdapter() {
        if (getContext()!=null && sectionItemDecoration!=null && projectResourceAdapter!=null && resourceRV!=null){
            resourceRV.removeItemDecoration(sectionItemDecoration);
            sectionItemDecoration = new RecyclerSectionItemDecoration(getResources().getDimensionPixelSize(R.dimen._14sdp), true, getSectionCallback(resourceObjectList));
            resourceRV.addItemDecoration(sectionItemDecoration);

            projectResourceAdapter.notifyDataSetChanged();

        }
    }
}
