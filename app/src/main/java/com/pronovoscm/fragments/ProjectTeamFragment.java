package com.pronovoscm.fragments;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.pronovoscm.PronovosApplication;
import com.pronovoscm.R;
import com.pronovoscm.adapter.ProjectTeamHeaderAdapter;
import com.pronovoscm.model.response.login.LoginResponse;
import com.pronovoscm.model.response.projectteam.Contact;
import com.pronovoscm.model.response.projectteam.Team;
import com.pronovoscm.model.response.projectteam.TeamData;
import com.pronovoscm.persistence.repository.ProjectOverviewRepository;
import com.pronovoscm.services.NetworkService;
import com.pronovoscm.utils.SharedPref;
import com.pronovoscm.utils.ui.CustomProgressBar;
import com.pronovoscm.utils.ui.RecyclerSectionItemDecoration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

@SuppressLint("ValidFragment")
public class ProjectTeamFragment extends Fragment {
    @Inject
    ProjectOverviewRepository projectOverviewRepository;
    @BindView(R.id.teamRV)
    RecyclerView teamRV;
    @BindView(R.id.searchTeamEditText)
    EditText searchTeamEditText;

    @BindView(R.id.noRecordTextView)
    TextView noRecordTextView;
    private ProjectTeamHeaderAdapter projectTeamHeaderAdapter;
    private ArrayList<Team> teamList = new ArrayList<>();
    private ArrayList<Team> originalTeamList = new ArrayList<>();
    private ArrayList<Team> teamSearchList = new ArrayList<>();
    private int projectID;
    private LoginResponse loginResponse;
    private int userId;
    private RecyclerSectionItemDecoration sectionItemDecoration;

    @SuppressLint("ValidFragment")
    public ProjectTeamFragment() {
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
        View rootView = inflater.inflate(R.layout.project_team_fragment, container, false);
        ButterKnife.bind(this, rootView);
        ((PronovosApplication) getActivity().getApplication()).getDaggerComponent().inject(this);
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(getActivity()).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        userId = loginResponse.getUserDetails().getUsers_id();
        projectID = getActivity().getIntent().getIntExtra("project_id", 0);
        TeamData result = projectOverviewRepository.getProjectTeam(userId, projectID);
       /* if (result != null && result.getTeam() != null) {
            teamList.clear();

            teamList.addAll(result.getTeam());

            ArrayList<Team> teamArrayList = new ArrayList<>();
            for (Team team : teamList) {
                if (TextUtils.isEmpty(team.getAddress())) {
                    teamArrayList.add(team);
                }
            }
            for (Team team : teamArrayList) {
                teamList.remove(team);
            }

        }*/
        Collections.sort(teamList, new Comparator<Team>() {
            @Override
            public int compare(Team o1, Team o2) {
                return o1.getOrder().compareTo(o2.getOrder());
            }
        });
        projectTeamHeaderAdapter = new ProjectTeamHeaderAdapter(getActivity(), teamList);
        teamRV.setLayoutManager(new LinearLayoutManager(getActivity()));
        sectionItemDecoration = new RecyclerSectionItemDecoration(getResources().getDimensionPixelSize(R.dimen._14sdp), true, getSectionCallback(teamList));
        teamRV.addItemDecoration(sectionItemDecoration);
        teamRV.setAdapter(projectTeamHeaderAdapter);
        refreshData(result);
        return rootView;
    }

/*    private ArrayList<Team> getSearchedFilterTeamList(String searchText) {
        teamSearchList.clear();
        //for (Team object : originalTeamList) {
        for (int i =0;i< originalTeamList.size();i++){
            Team object = originalTeamList.get(i);
            Team objectContained = null;
            if (object.getCompany().toLowerCase().contains(searchText)) {
                objectContained = object;
                if(!teamSearchList.contains(object))
                    teamSearchList.add(object);
                Log.d("FilterTeamList", i+"     111111 getSearchedFilterTeamList: "+object+"  list size "+teamSearchList.size());
                continue;
            }
            if (objectContained == null) {
                for (Contact contact : object.getContacts()) {
                    if (objectContained == null) {
                        if (contact.getContactName().toLowerCase().contains(searchText)) {
                            objectContained = object;
                            if(!teamSearchList.contains(object)){
                                teamSearchList.add(object);
                                Log.d("FilterTeamList", "2222222 getSearchedFilterTeamList: "+object+"  list size "+teamSearchList.size());
                                break;
                            }
                        }
                    } else {
                        break;
                    }
                }
            }
        }
        Log.d("TeamFrag", originalTeamList.size()+"    originalTeamList  getSearchedFilterTeamList: teamSearchList "+teamSearchList.size());
        return teamSearchList;

    }*/

    private ArrayList<Team> getSearchedFilterTeamList(String searchText) {
        teamSearchList.clear();
        ArrayList<Team> copyList = new ArrayList<>();
        try {
            for (Team t : originalTeamList) {
                copyList.add(t.clone());
            }

            //for (Team object : originalTeamList) {
            for (int i = 0; i < copyList.size(); i++) {
                Team object = copyList.get(i);
                Team objectContained = null;
                if (object.getCompany().toLowerCase().contains(searchText)) {
                    objectContained = object.clone();
                    //if (!teamSearchList.contains(objectContained))
                    teamSearchList.add(objectContained);
                    Log.d("FilterTeamList", "     111111 getSearchedFilterTeamList: " + object + "  list size " + teamSearchList.size());
                    continue;
                }
                if (objectContained == null) {
                    for (Contact contact : object.getContacts()) {
                        if (objectContained == null) {
                            if (contact.getContactName().toLowerCase().contains(searchText)) {
                                objectContained = object.clone();
                                break;
                            }
                        } else {
                            break;
                        }
                    }
                    if (objectContained != null) {
                        if (!teamSearchList.contains(objectContained)) {
                            List<Contact> contactList = new ArrayList<>();
                            for (Contact cc : objectContained.getContacts()) {
                                if (cc.getContactName().toLowerCase().contains(searchText)) {
                                    contactList.add(cc.clone());
                                } else {
                                    //  objectContained.getContacts().remove(cc);
                                }
                            }
                            if (contactList.size() > 0) {
                                objectContained.setContacts(contactList);
                                teamSearchList.add(objectContained);
                            }

                            Log.d("FilterTeamList", "2222222 getSearchedFilterTeamList: " + object + "  list size " + teamSearchList.size());
                            break;
                        }
                    }


                }
            }
            Log.d("TeamFrag", originalTeamList.size() + "    originalTeamList  getSearchedFilterTeamList: teamSearchList " + teamSearchList.size());
            return teamSearchList;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return teamSearchList;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        searchTeamEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d("TeamFrag", "onTextChanged: " + s);
                if (!TextUtils.isEmpty(searchTeamEditText.getText().toString())) {
                    List<Team> searchResult = getSearchedFilterTeamList(searchTeamEditText.getText().toString().toLowerCase());
                    if (searchResult != null && searchResult.size() > 0) {
                        teamList.clear();
                        teamList.addAll(searchResult);
                    } else {
                        teamList.clear();
                    }
                } else {
                    teamList.clear();

                    try {
                        for (Team t : originalTeamList) {
                            teamList.add(t.clone());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
                if (0 == teamRV.getItemDecorationCount()) {
                    sectionItemDecoration = new RecyclerSectionItemDecoration(getResources().getDimensionPixelSize(R.dimen._14sdp), true, getSectionCallback(teamList));
                    teamRV.addItemDecoration(sectionItemDecoration);
                }
                if (teamList.size() == 0) {
                    noRecordTextView.setVisibility(View.VISIBLE);
                    noRecordTextView.setText(R.string.no_results);

                } else {
                    noRecordTextView.setVisibility(View.GONE);
                }
                Log.e("TeamFrag", "onTextChanged: teamList " + teamList.size());
                projectTeamHeaderAdapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    public void refreshData(TeamData result) {
        if (getContext() != null && result != null && result.getTeam() != null && projectTeamHeaderAdapter != null) {
            if (getContext() != null)
                CustomProgressBar.dissMissDialog(getContext());
            teamList.clear();
            teamList.addAll(result.getTeam());
            ArrayList<Team> teamArrayList = new ArrayList<>();
            for (Team team : teamList) {
                if (TextUtils.isEmpty(team.getCompany())) {
                    teamArrayList.add(team);
                }
            }
            for (Team team : teamArrayList) {
                teamList.remove(team);
            }
            Collections.sort(teamList, new Comparator<Team>() {
                @Override
                public int compare(Team o1, Team o2) {
                    return o1.getOrder().compareTo(o2.getOrder());
                }
            });
            originalTeamList.clear();
            // originalTeamList.addAll(teamList);

            try {
                for (Team t : teamList) {
                    originalTeamList.add(t.clone());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }


            Log.e("TeamFrag", originalTeamList.size() + "  = originalTeamList  refreshData: teamList  =  " + teamList.size());
            if (getContext() != null && teamList.size() == 0) {
                noRecordTextView.setText(R.string.a_project_team_has_not_yet_been);
                noRecordTextView.setVisibility(View.VISIBLE);
            } else if (getContext() != null) {
                noRecordTextView.setVisibility(View.GONE);
            }
            teamRV.removeItemDecoration(sectionItemDecoration);
            sectionItemDecoration = new RecyclerSectionItemDecoration(getResources().getDimensionPixelSize(R.dimen._14sdp), true, getSectionCallback(teamList));
            teamRV.addItemDecoration(sectionItemDecoration);
            projectTeamHeaderAdapter.notifyDataSetChanged();
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
    private RecyclerSectionItemDecoration.SectionCallback getSectionCallback(final List<Team> drawingList) {
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
                    return TextUtils.isEmpty(drawingList.get(position).getDiscipline()) ? "-" : drawingList.get(position).getDiscipline();
                } else {
                    return "";
                }
            }
        };
    }

    public void refreshAdapter() {
        if (getContext() != null && sectionItemDecoration != null && projectTeamHeaderAdapter != null && teamRV != null) {
            teamRV.removeItemDecoration(sectionItemDecoration);
            sectionItemDecoration = new RecyclerSectionItemDecoration(getResources().getDimensionPixelSize(R.dimen._14sdp), true, getSectionCallback(teamList));
            teamRV.addItemDecoration(sectionItemDecoration);
            projectTeamHeaderAdapter.notifyDataSetChanged();

        }
    }
}
