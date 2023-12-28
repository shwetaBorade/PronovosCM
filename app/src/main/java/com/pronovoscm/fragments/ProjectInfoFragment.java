package com.pronovoscm.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.pronovoscm.PronovosApplication;
import com.pronovoscm.R;
import com.pronovoscm.adapter.ProjectDynamicInfoAdapter;
import com.pronovoscm.model.response.login.LoginResponse;
import com.pronovoscm.model.response.projectinfo.ProjectOverviewInfoData;
import com.pronovoscm.model.response.projectinfo.Section;
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
public class ProjectInfoFragment extends Fragment {
    @Inject
    ProjectOverviewRepository projectOverviewRepository;
    /*   @BindView(R.id.projectInfoTV)
       TextView projectInfoTV;
       @BindView(R.id.projectScheduleTV)
       TextView projectScheduleTV;
       @BindView(R.id.projectsiteSpecificInfoTV)
       TextView projectsiteSpecificInfoTV;
   */
  /*  //   info views

    @BindView(R.id.hiringReuirementsNotesTextView)
    TextView hiringReuirementsNotesTextView;*/
    @BindView(R.id.infoRV)
    RecyclerView infoRV;
    private LoginResponse loginResponse;
    private int userId;
    private int projectID;
    private ArrayList<Integer> arrayList = new ArrayList();
    List<Section> sections;
    private RecyclerSectionItemDecoration sectionItemDecoration;

    @SuppressLint("ValidFragment")
    public ProjectInfoFragment() {
    }


    @Override
    public void onResume() {
        super.onResume();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.project_info_rv_fragment, container, false);
        ButterKnife.bind(this, rootView);
        ((PronovosApplication) getActivity().getApplication()).getDaggerComponent().inject(this);


        return rootView;
    }

    ProjectOverviewInfoData result;
    private ProjectDynamicInfoAdapter projectInfoAdapter;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
      /*  infoView.setVisibility(View.GONE);
        projectSpecialAddress.setVisibility(View.GONE);
      */
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(getActivity()).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        userId = loginResponse.getUserDetails().getUsers_id();
        // setProjectDynamicAdapter();
        projectID = getActivity().getIntent().getIntExtra("project_id", 0);

        //  Info info  = null;//= projectOverviewRepository.getProjectInfo(userId, projectID);
        ProjectOverviewInfoData infoData = projectOverviewRepository.getDynamicProjectInfo(userId, projectID);
        refreshData(infoData);
    }

    void setProjectDynamicAdapter() {
        infoRV.removeAllViewsInLayout();
        /*if(sectionItemDecoration!=null)
        infoRV.removeItemDecoration(sectionItemDecoration);*/
        projectInfoAdapter = new ProjectDynamicInfoAdapter(getActivity(), arrayList, null, infoRV);
        infoRV.setLayoutManager(new LinearLayoutManager(getContext()));
        if (0 == infoRV.getItemDecorationCount()) {
            sectionItemDecoration = new RecyclerSectionItemDecoration(getResources().getDimensionPixelSize(R.dimen._8sdp), true, getSectionCallback());
            infoRV.addItemDecoration(sectionItemDecoration);
        }
        infoRV.setAdapter(projectInfoAdapter);
        infoRV.getRecycledViewPool().setMaxRecycledViews(0, 0);

    }

    private void filterInactiveInfo(ProjectOverviewInfoData infoData) {
        //Log.d("infoFragment", "filterInactiveInfo: infoData "+infoData);
        if (infoData != null)
            if (infoData.getSections() != null)
                for (Section s : infoData.getSections()) {
                    if (s.getActive() != 1) {
                        infoData.getSections().remove(s);
                    }
                }
    }

    public void setRecylerviewVisibilityGone() {
        infoRV.setVisibility(View.GONE);
    }

    public void refreshData(ProjectOverviewInfoData result1) {
        infoRV.setVisibility(View.VISIBLE);
        this.result = result1;
        if (getContext() != null && result != null) {
            if (result != null && result.getSections() != null) {
                filterInactiveInfo(result);
                sections = result.getSections();

                if (getContext() != null)
                    CustomProgressBar.dissMissDialog(getContext());
                arrayList.clear();
                int i = 0;
                if (result != null && result.getSections() != null) {
                    for (Section s : sections) {
                        arrayList.add(i);
                        i += 1;
                    }
                setProjectDynamicAdapter();

                projectInfoAdapter.setrefreshInfo(result);
                infoRV.removeItemDecoration(sectionItemDecoration);
                if (0 == infoRV.getItemDecorationCount()) {
                    sectionItemDecoration = new RecyclerSectionItemDecoration(getResources().getDimensionPixelSize(R.dimen._8sdp), true, getSectionCallback());
                    infoRV.addItemDecoration(sectionItemDecoration);
                }
                projectInfoAdapter.notifyDataSetChanged();
            }
            }
        } else {
            if (getContext() != null && NetworkService.isNetworkAvailable(getContext()))
                CustomProgressBar.showDialog(getContext());
        }
    }
    /**
     * Get the listener of the recycler view decorator to show the section in the recycler view
     *
     * @return
     */
    private RecyclerSectionItemDecoration.SectionCallback getSectionCallback() {
        return new RecyclerSectionItemDecoration.SectionCallback() {
            @Override
            public boolean isSection(int position) {
                if (position >= 0) {
                    return true;
                } else return false;
            }

            @Override
            public CharSequence getSectionHeader(int position) {
                // Log.d("getSectionCallback", "getSectionHeader: "+sections.get(position).getName());
                if (position >= 0)
                    return sections.get(position).getName();
                else return "";
                /*if (position >= 0) {
                    return position == 0 ? getString(R.string.project_info) : position == 1 ?
                            getString(R.string.project_schedule) : getString(R.string.site_specific_info);
                } else {
                    return "";
                }*/
            }
        };
    }

    public void refreshAdapter() {
        if (getContext()!=null && sectionItemDecoration!=null && projectInfoAdapter!=null && infoRV!=null) {
            infoRV.removeItemDecoration(sectionItemDecoration);
            infoRV.removeAllViews();
            setProjectDynamicAdapter();
            projectInfoAdapter.setrefreshInfo(result);
            if (0 == infoRV.getItemDecorationCount()) {
                sectionItemDecoration = new RecyclerSectionItemDecoration(getResources().getDimensionPixelSize(R.dimen._8sdp), true, getSectionCallback());
                infoRV.addItemDecoration(sectionItemDecoration);
            }
            projectInfoAdapter.clearViewMap();
            projectInfoAdapter.notifyDataSetChanged();
        }
    }
}
