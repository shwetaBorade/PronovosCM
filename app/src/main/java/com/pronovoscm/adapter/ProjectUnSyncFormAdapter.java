package com.pronovoscm.adapter;

import android.app.Activity;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.pronovoscm.PronovosApplication;
import com.pronovoscm.R;
import com.pronovoscm.model.response.login.LoginResponse;
import com.pronovoscm.persistence.domain.Forms;
import com.pronovoscm.persistence.domain.UserForms;
import com.pronovoscm.persistence.repository.ProjectFormRepository;
import com.pronovoscm.utils.DateFormatter;
import com.pronovoscm.utils.SharedPref;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProjectUnSyncFormAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    @Inject
    ProjectFormRepository mprojectFormRepository;
    private int projectId;
    private List<UserForms> ProjectFormList;
    private Activity mActivity;
    // variable to track event time
    private long mLastClickTime = 0;
    OnUnSyncItemClickListner listner;

    public ProjectUnSyncFormAdapter(Activity mActivity, List<UserForms> ProjectFormList, int projectId, OnUnSyncItemClickListner listner) {
        this.ProjectFormList = ProjectFormList;
        this.mActivity = mActivity;
        this.projectId = projectId;
        ((PronovosApplication) mActivity.getApplication()).getDaggerComponent().inject(this);
        this.listner = listner;


    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View view = inflater.inflate(R.layout.project_unsyn_form_list_item, parent, false);
        return new ProjectFormHolder(view);

    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((ProjectFormHolder) holder).bind();
    }

    @Override
    public int getItemCount() {
        if (ProjectFormList != null) {
            return ProjectFormList.size();
        } else {
            return 0;
        }
    }

    @Override
    public int getItemViewType(int position) {

        return position;

    }

    public interface OnUnSyncItemClickListner {
        public void onUnsyncItemClick(Forms form, UserForms userForm, int projectID);
    }

    public class ProjectFormHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.formNameTextView)
        TextView formNameTextView;
        @BindView(R.id.userNameTextView)
        TextView userNameTextView;
        @BindView(R.id.folderCardView)
        CardView folderCardView;
        @BindView(R.id.invisibleView)
        View invisibleView;
        @BindView(R.id.invisibleFirstView)
        View invisibleFirstView;

        public ProjectFormHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        private void bind() {

            LoginResponse loginResponse = (new Gson().fromJson(SharedPref.getInstance(PronovosApplication.getContext()).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
            UserForms userForms = ProjectFormList.get(getAdapterPosition());
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
            Forms forms = mprojectFormRepository.getUnsyncFormDetails(userForms.getPjProjectsId(),
                    userForms.getFormId(), loginResponse.getUserDetails().getUsers_id(), userForms.getRevisionNumber());
            if (forms != null)
                formNameTextView.setText(forms.getFormName());
            Date date = userForms.getCreatedAt();
            userNameTextView.setText((DateFormatter.formatDateForImage(date)) + " - " + userForms.getCreatedByUserName());

            Forms prevForms = null;
            if (getAdapterPosition() > 0)
                prevForms = mprojectFormRepository.getFormDetails(ProjectFormList.get(getAdapterPosition() - 1).getPjProjectsId(), ProjectFormList.get(getAdapterPosition() - 1).getFormId(), loginResponse.getUserDetails().getUsers_id());
            if (getAdapterPosition() == 0 || (prevForms != null && forms != null && forms.getFormCategoriesId() != prevForms.getFormCategoriesId())) {
                invisibleView.setVisibility(View.VISIBLE);
            } else {
                invisibleView.setVisibility(View.GONE);
            }
            if (getAdapterPosition() == 0) {
                invisibleFirstView.setVisibility(View.VISIBLE);
            } else {
                invisibleFirstView.setVisibility(View.GONE);
            }
        folderCardView.setOnClickListener(view -> {
            // Preventing multiple clicks, using threshold of 1 second
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            listner.onUnsyncItemClick(forms, userForms, projectId);
          /*  mActivity.startActivity(new Intent(mActivity, ProjectFormDetailActivity.class)
                    .putExtra("project_id", projectId)
                    .putExtra(Constants.INTENT_KEY_FORM_CREATED_DATE,  sdf.format(userForms.getCreatedAt()) )
                    .putExtra(Constants.INTENT_KEY_FORM_ID, userForms.getFormId())
                    .putExtra(Constants.INTENT_KEY_ORIGINAL_FORM_ID,forms.getOriginalFormsId())
                    .putExtra(Constants.INTENT_KEY_FORM_ACTIVE_REVISION_NUMBER,forms.getRevisionNumber())
                    .putExtra("user_form_id", userForms.getId()).putExtra("form_type", "Un-sync"));*/

        });
        }
    }

}
