package com.pronovoscm.fragments;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.pronovoscm.PronovosApplication;
import com.pronovoscm.R;
import com.pronovoscm.adapter.ProjectFormAdapter;
import com.pronovoscm.model.response.login.LoginResponse;
import com.pronovoscm.persistence.domain.Forms;
import com.pronovoscm.persistence.repository.ProjectFormRepository;
import com.pronovoscm.services.NetworkService;
import com.pronovoscm.utils.SharedPref;
import com.pronovoscm.utils.ui.RecyclerSectionItemDecoration;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

@SuppressLint("ValidFragment")
public class ProjectFormListFragment extends Fragment {

    @Inject
    ProjectFormRepository mprojectFormRepository;
    @BindView(R.id.formsRV)
    RecyclerView formsRV;
    @BindView(R.id.searchFormEditText)
    EditText searchFormEditText;
    @BindView(R.id.noRecordTextView)
    TextView noRecordTextView;
    @BindView(R.id.seachClearImageView)
    ImageView searchClearImageView;
    private LoginResponse loginResponse;
    private int userId;
    private int projectID;
    private ArrayList<Forms> arrayList = new ArrayList();
    private ProjectFormAdapter projectFormAdapter;
    private RecyclerSectionItemDecoration sectionItemDecoration;

    @SuppressLint("ValidFragment")
    public ProjectFormListFragment() {
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
        View rootView = inflater.inflate(R.layout.project_form_rv_fragment, container, false);
        ButterKnife.bind(this, rootView);
        ((PronovosApplication) getActivity().getApplication()).getDaggerComponent().inject(this);


        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        projectID = getActivity().getIntent().getIntExtra("project_id", 0);
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(getActivity()).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        arrayList.addAll(mprojectFormRepository.getProjectForm(projectID, searchFormEditText.getText().toString(), loginResponse.getUserDetails().getUsers_id()));
        projectFormAdapter = new ProjectFormAdapter(getActivity(), arrayList, projectID);
        formsRV.setLayoutManager(new LinearLayoutManager(getContext()));
        formsRV.setAdapter(projectFormAdapter);
        sectionItemDecoration = new RecyclerSectionItemDecoration(getResources().getDimensionPixelSize(R.dimen._14sdp), true, getSectionCallback(arrayList));
        formsRV.addItemDecoration(sectionItemDecoration);
        userId = loginResponse.getUserDetails().getUsers_id();

        searchFormEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(s.toString())) {
                    searchClearImageView.setVisibility(View.INVISIBLE);
                } else {
                    searchClearImageView.setVisibility(View.VISIBLE);
                }
                refreshAdapter(true,true);
            }


            @Override
            public void afterTextChanged(Editable s) {
            }
        });
//        refreshAdapter(false, false);
//        refreshData(info);
        if (getContext() != null && projectFormAdapter != null && formsRV != null) {
            arrayList.clear();
            loginResponse = (new Gson().fromJson(SharedPref.getInstance(getActivity()).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
            arrayList.addAll(mprojectFormRepository.getProjectForm(projectID, searchFormEditText.getText().toString(), loginResponse.getUserDetails().getUsers_id()));
            formsRV.removeItemDecoration(sectionItemDecoration);
            sectionItemDecoration = new RecyclerSectionItemDecoration(getResources().getDimensionPixelSize(R.dimen._14sdp), true, getSectionCallback(arrayList));
            formsRV.addItemDecoration(sectionItemDecoration);
            projectFormAdapter.notifyDataSetChanged();
        }

        if ( getContext() != null && NetworkService.isNetworkAvailable(getContext())&& noRecordTextView != null && getActivity() != null) {
            if (arrayList.size() <= 0 ) {
                noRecordTextView.setText("Loading Forms");
                noRecordTextView.setVisibility(View.VISIBLE);
            }

        }
    }

    public void refreshAdapter(boolean showNoList, boolean searching) {
        if (getContext() != null && projectFormAdapter != null && formsRV != null) {
            arrayList.clear();
            loginResponse = (new Gson().fromJson(SharedPref.getInstance(getActivity()).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
            arrayList.addAll(mprojectFormRepository.getProjectForm(projectID, searchFormEditText.getText().toString(), loginResponse.getUserDetails().getUsers_id()));
            formsRV.removeItemDecoration(sectionItemDecoration);
            sectionItemDecoration = new RecyclerSectionItemDecoration(getResources().getDimensionPixelSize(R.dimen._14sdp), true, getSectionCallback(arrayList));
            formsRV.addItemDecoration(sectionItemDecoration);
            projectFormAdapter.notifyDataSetChanged();
        }

       if (getContext() != null && (showNoList) && noRecordTextView != null && getActivity() != null) {
            if (arrayList.size() <= 0) {
                noRecordTextView.setText(getActivity().getString(R.string.form_no_record_message));
                noRecordTextView.setVisibility(View.VISIBLE);
            } else {
                noRecordTextView.setVisibility(View.GONE);

            }
        }

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        refreshAdapter(false, false);
    }

    /**
     * Get the listener of the recycler view decorator to show the section in the recycler view
     *
     * @param formsList
     * @return
     */
    private RecyclerSectionItemDecoration.SectionCallback getSectionCallback(final List<Forms> formsList) {
        return new RecyclerSectionItemDecoration.SectionCallback() {
            @Override
            public boolean isSection(int position) {
                if (position >= 0) {
                    return position == 0
                            || !(formsList.get(position)
                            .getFormCategoriesId() == (formsList.get(position - 1)
                            .getFormCategoriesId()));
                } else return false;

            }

            @Override
            public CharSequence getSectionHeader(int position) {
                if (position >= 0) {
                    return mprojectFormRepository.getFormCategory(formsList.get(position).getFormCategoriesId());
                } else {
                    return "";
                }
            }
        };
    }

    @OnClick({R.id.seachClearImageView})
    public void clickSearchClear() {
        searchFormEditText.setText("");
    }

}
