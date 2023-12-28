package com.pronovoscm.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
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
import com.pronovoscm.activity.ProjectFormDetailActivity;
import com.pronovoscm.adapter.ProjectUnSyncFormAdapter;
import com.pronovoscm.model.response.login.LoginResponse;
import com.pronovoscm.persistence.domain.Forms;
import com.pronovoscm.persistence.domain.UserForms;
import com.pronovoscm.persistence.repository.ProjectFormRepository;
import com.pronovoscm.utils.Constants;
import com.pronovoscm.utils.SharedPref;
import com.pronovoscm.utils.ui.RecyclerSectionItemDecoration;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

@SuppressLint("ValidFragment")
public class ProjectUnSyncFormFragment extends Fragment {


    List<UserForms> userFormsList = new ArrayList<>();
    @Inject
    ProjectFormRepository mprojectFormRepository;
    ProjectUnSyncFormAdapter projectUnSyncFormAdapter;
    @BindView(R.id.formsRV)
    RecyclerView formsRV;
    @BindView(R.id.noRecordTextView)
    TextView noRecordTextView;
    private LoginResponse loginResponse;
    private int projectID;
    private RecyclerSectionItemDecoration sectionItemDecoration;

    @SuppressLint("ValidFragment")
    public ProjectUnSyncFormFragment() {
    }


    @Override
    public void onResume() {
        super.onResume();
//        sectionItemDecoration = new RecyclerSectionItemDecoration(getResources().getDimensionPixelSize(R.dimen._14sdp), true, getSectionCallback(userFormsList));
        if (sectionItemDecoration != null) {
            formsRV.removeItemDecoration(sectionItemDecoration);
        }
        loadData();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        loadData();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(String s) {
        if (s.equals("DeletedForm")) {
            Log.e("ProjectUnSyncForm", "DeleteForm");
            loadData();
        }
    }

    public void loadData() {
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(getActivity()).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        projectID = getActivity().getIntent().getIntExtra("project_id", 0);
        userFormsList.clear();
        formsRV.setLayoutManager(new LinearLayoutManager(getContext()));
        userFormsList.addAll(mprojectFormRepository.getUserForm(projectID, loginResponse.getUserDetails().getUsers_id(), ""));
        formsRV.setHasFixedSize(true);
        formsRV.removeItemDecoration(sectionItemDecoration);
        sectionItemDecoration = new RecyclerSectionItemDecoration(getResources().getDimensionPixelSize(R.dimen.dashboard_text_size), true, getSectionCallback(userFormsList));
        if (sectionItemDecoration != null) {
            formsRV.addItemDecoration(sectionItemDecoration);
        }
        projectUnSyncFormAdapter = new ProjectUnSyncFormAdapter(getActivity(), userFormsList, projectID, new ProjectUnSyncFormAdapter.OnUnSyncItemClickListner() {
            @Override
            public void onUnsyncItemClick(Forms form, UserForms userForm, int projectId) {


                boolean formComponent = mprojectFormRepository.isFormComponentDataExist(form.formsId, form.originalFormsId, form.revisionNumber);
                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
                if (formComponent)
                    startActivity(new Intent(getActivity(), ProjectFormDetailActivity.class)
                            .putExtra("project_id", projectId)
                            .putExtra(Constants.INTENT_KEY_FORM_CREATED_DATE, sdf.format(userForm.getCreatedAt()))
                            .putExtra(Constants.INTENT_KEY_FORM_ID, form.formsId)
                            .putExtra(Constants.INTENT_KEY_ORIGINAL_FORM_ID, form.getOriginalFormsId())
                            .putExtra(Constants.INTENT_KEY_FORM_ACTIVE_REVISION_NUMBER, form.getRevisionNumber())
                            .putExtra("user_form_id", userForm.getId()).putExtra("form_type", "Un-sync"));
                else {
// show  mmessage ur offline  here
                }
            }
        });

        formsRV.setAdapter(projectUnSyncFormAdapter);
        if (userFormsList.size() == 0) {
            noRecordTextView.setText("There are currently no forms with unsynced changes.");
            noRecordTextView.setVisibility(View.VISIBLE);
        } else {
            noRecordTextView.setVisibility(View.GONE);
        }

    }




    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.project_unsync_form, container, false);
        ButterKnife.bind(this, rootView);
        ((PronovosApplication) getActivity().getApplication()).getDaggerComponent().inject(this);

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadData();

    }

    /**
     * Get the listener of the recycler view decorator to show the section in the recycler view
     *
     * @param userForms
     * @return
     */
    private RecyclerSectionItemDecoration.SectionCallback getSectionCallback(final List<UserForms> userForms) {
        return new RecyclerSectionItemDecoration.SectionCallback() {
            @Override
            public boolean isSection(int position) {
                if (position >= 0) {
                    return position == 0
                            || !(mprojectFormRepository.getUnsyncFormDetails(userForms.get(position).getPjProjectsId(),
                            userForms.get(position).getFormId(), loginResponse.getUserDetails().getUsers_id(), userForms.get(position).getRevisionNumber())
                            .getFormCategoriesId() ==
                            (mprojectFormRepository.getUnsyncFormDetails(userForms.get(position - 1).getPjProjectsId(),
                                    userForms.get(position - 1).getFormId(), loginResponse.getUserDetails().getUsers_id(), userForms.get(position - 1).getRevisionNumber())
                                    .getFormCategoriesId()));
                    /*    || !(mprojectFormRepository.getFormDetails(userForms.get(position).getPjProjectsId(),
                            userForms.get(position).getFormId(), loginResponse.getUserDetails().getUsers_id())
                            .getFormCategoriesId() == (mprojectFormRepository.getFormDetails(userForms.get(position - 1).getPjProjectsId(),
                            userForms.get(position - 1).getFormId(), loginResponse.getUserDetails().getUsers_id())
                            .getFormCategoriesId()));*/
                } else return false;

            }

            @Override
            public CharSequence getSectionHeader(int position) {
                if (position >= 0) {
                    return mprojectFormRepository.getFormCategory(
                            mprojectFormRepository.getUnsyncFormDetails(
                                    userForms.get(position).getPjProjectsId(), userForms.get(position).getFormId(),
                                    loginResponse.getUserDetails().getUsers_id(), userForms.get(position).getRevisionNumber()).getFormCategoriesId());
                } else {
                    return "";
                }
            }
        };
    }

    @Override
    public void onStart() {
        EventBus.getDefault().register(this);
        super.onStart();
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }
}
