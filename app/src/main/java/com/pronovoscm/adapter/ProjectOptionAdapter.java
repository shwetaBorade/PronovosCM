package com.pronovoscm.adapter;

import android.app.Activity;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.pronovoscm.R;
import com.pronovoscm.activity.DailyReportActivity;
import com.pronovoscm.activity.EquipmentActivity;
import com.pronovoscm.activity.FieldPaperWorkActivity;
import com.pronovoscm.activity.issue_tracking.IssueTrackingListActivity;
import com.pronovoscm.activity.ProjectAlbumActivity;
import com.pronovoscm.activity.ProjectDocumentsActivity;
import com.pronovoscm.activity.ProjectDrawingActivity;
import com.pronovoscm.activity.ProjectFormActivity;
import com.pronovoscm.activity.ProjectOverviewDetailsActivity;
import com.pronovoscm.activity.PunchListActivity;
import com.pronovoscm.activity.RfiListActivity;
import com.pronovoscm.activity.SubmittalsListActivity;
import com.pronovoscm.data.ProjectFormProvider;
import com.pronovoscm.data.ProviderResult;
import com.pronovoscm.model.OptionEnum;
import com.pronovoscm.model.response.formpermission.FormPermissionResponse;
import com.pronovoscm.services.NetworkService;
import com.pronovoscm.utils.Constants;
import com.pronovoscm.utils.FileUtils;
import com.pronovoscm.utils.IntentExtra;
import com.pronovoscm.utils.ui.CustomProgressBar;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProjectOptionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    ProjectFormProvider mprojectFormProvider;
    private int projectId;
    private ArrayList<OptionEnum> projectOptionList;
    private Activity mActivity;
    private String projectName;
    private long mLastClickTime = 0;

    public ProjectOptionAdapter(Activity mActivity, ArrayList<OptionEnum> projectOptionList, int projectId, String projectName, ProjectFormProvider projectFormProvider) {
        this.projectOptionList = projectOptionList;
        this.mActivity = mActivity;
        this.projectId = projectId;
        this.projectName = projectName;

        this.mprojectFormProvider = projectFormProvider;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View view = inflater.inflate(R.layout.project_option_list_item, parent, false);
        return new ProjectOptionHolder(view);

    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((ProjectOptionHolder) holder).bind();
    }

    @Override
    public int getItemCount() {
        if (projectOptionList != null) {
            return projectOptionList.size();
        } else {
            return 0;
        }
    }

    @Override
    public int getItemViewType(int position) {

        return position;

    }

    public class ProjectOptionHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.optionImageView)
        ImageView optionImageView;
        @BindView(R.id.optionNameTextView)
        TextView optionNameTextView;
        @BindView(R.id.optionCardView)
        CardView optionCardView;

        public ProjectOptionHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        private void bind() {
            if (projectOptionList.get(getAdapterPosition()).equals(OptionEnum.OVERVIEW)) {
                optionImageView.setBackground(ContextCompat.getDrawable(mActivity, R.drawable.ic_project_overview));
                optionNameTextView.setText(mActivity.getString(R.string.overview));
                optionCardView.setOnClickListener(v -> {
                    // Preventing multiple clicks, using threshold of 1 second
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    mActivity.startActivity(new Intent(mActivity, ProjectOverviewDetailsActivity.class).putExtra("project_name", projectName).putExtra("project_id", projectId));
                });
            } else if (projectOptionList.get(getAdapterPosition()).equals(OptionEnum.PHOTO)) {
                optionImageView.setBackground(ContextCompat.getDrawable(mActivity, R.drawable.ic_picture));
                optionNameTextView.setText(mActivity.getString(R.string.photos));
                optionCardView.setOnClickListener(v -> {
                    // Preventing multiple clicks, using threshold of 1 second
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();

                    mActivity.startActivity(new Intent(mActivity, ProjectAlbumActivity.class).putExtra("project_name", projectName)
                            .putExtra("project_id", projectId));
                });
            } else if (projectOptionList.get(getAdapterPosition()).equals(OptionEnum.DRAWING)) {
                optionImageView.setBackground(ContextCompat.getDrawable(mActivity, R.drawable.ic_drawings));
                optionNameTextView.setText(mActivity.getString(R.string.drawings));
                optionCardView.setOnClickListener(v -> {
                    // Preventing multiple clicks, using threshold of 1 second
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    FileUtils.deleteFile();
                    mActivity.startActivity(new Intent(mActivity, ProjectDrawingActivity.class).putExtra("project_name", projectName).putExtra("project_id", projectId));
                });
            } else if (projectOptionList.get(getAdapterPosition()).equals(OptionEnum.FIELDPAPERWORK)) {

                optionImageView.setBackground(ContextCompat.getDrawable(mActivity, R.drawable.ic_field_paper_work));
                optionNameTextView.setText(mActivity.getString(R.string.field_paper_work));
                optionCardView.setOnClickListener(v -> {
                    // Preventing multiple clicks, using threshold of 1 second
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    mActivity.startActivity(new Intent(mActivity, FieldPaperWorkActivity.class).putExtra("project_name", projectName).putExtra("project_id", projectId));
                });
            } else if (projectOptionList.get(getAdapterPosition()).equals(OptionEnum.DAILY_REPORTS)) {

                optionImageView.setBackground(ContextCompat.getDrawable(mActivity, R.drawable.ic_daily_report));
                optionNameTextView.setText(mActivity.getString(R.string.daily_reports));
                optionCardView.setOnClickListener(v -> {
                    // Preventing multiple clicks, using threshold of 1 second
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    mActivity.startActivity(new Intent(mActivity, DailyReportActivity.class).putExtra("project_name", projectName).putExtra("project_id", projectId));
                });
            } else if (projectOptionList.get(getAdapterPosition()).equals(OptionEnum.PUNCH_LIST)) {
                optionImageView.setBackground(ContextCompat.getDrawable(mActivity, R.drawable.ic_punch_list));
                optionNameTextView.setText(mActivity.getString(R.string.punch_list));
                optionCardView.setOnClickListener(v -> {
                    // Preventing multiple clicks, using threshold of 1 second
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    Log.e("TAG", " ProjectOptionAdapter optionCardView setOnClickListener: " );
                    mActivity.startActivity(new Intent(mActivity, PunchListActivity.class).putExtra(IntentExtra.PROJECT_ID.name(), projectId).putExtra(IntentExtra.PROJECT_NAME.name(), ""));
                });
            } else if (projectOptionList.get(getAdapterPosition()).equals(OptionEnum.FORMS)) {
                optionImageView.setBackground(ContextCompat.getDrawable(mActivity, R.drawable.ic_forms));
                optionNameTextView.setText(mActivity.getString(R.string.forms));
                optionCardView.setOnClickListener(v -> {
                    // Preventing multiple clicks, using threshold of 1 second
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                        return;
                    }

                    mLastClickTime = SystemClock.elapsedRealtime();
                    openFormListActivity();

                });
            } else if (projectOptionList.get(getAdapterPosition()).equals(OptionEnum.DOCUMENTS)) {
                optionImageView.setBackground(ContextCompat.getDrawable(mActivity, R.drawable.ic_documents));
                optionNameTextView.setText(mActivity.getString(R.string.documents));
                optionCardView.setOnClickListener(v -> {
                    // Preventing multiple clicks, using threshold of 1 second
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                        return;
                    }

                    mLastClickTime = SystemClock.elapsedRealtime();
                    mActivity.startActivity(new Intent(mActivity, ProjectDocumentsActivity.class).putExtra(Constants.INTENT_KEY_PROJECT_NAME, projectName)
                            .putExtra(Constants.INTENT_KEY_PROJECT_ID, projectId));

                });
            } else if (projectOptionList.get(getAdapterPosition()).equals(OptionEnum.RFIS)) {
                optionImageView.setBackground(ContextCompat.getDrawable(mActivity, R.drawable.ic_rfi));
                optionNameTextView.setText(mActivity.getString(R.string.rfis));
                optionCardView.setOnClickListener(v -> {
                    // Preventing multiple clicks, using threshold of 1 second
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                        return;
                    }

                    mLastClickTime = SystemClock.elapsedRealtime();
                    mActivity.startActivity(new Intent(mActivity, RfiListActivity.class).putExtra(Constants.INTENT_KEY_PROJECT_NAME, projectName)
                            .putExtra(Constants.INTENT_KEY_PROJECT_ID, projectId));

                });
            } else if (projectOptionList.get(getAdapterPosition()).equals(OptionEnum.SUBMITTALS)) {
                optionImageView.setBackground(ContextCompat.getDrawable(mActivity, R.drawable.ic_submittals));
                optionNameTextView.setText(mActivity.getString(R.string.submittals));
                optionCardView.setOnClickListener(v -> {
                    // Preventing multiple clicks, using threshold of 1 second
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                        return;
                    }

                    mLastClickTime = SystemClock.elapsedRealtime();
                    mActivity.startActivity(new Intent(mActivity, SubmittalsListActivity.class).putExtra(Constants.INTENT_KEY_PROJECT_NAME, projectName)
                            .putExtra(Constants.INTENT_KEY_PROJECT_ID, projectId));

                });
            } else if (projectOptionList.get(getAbsoluteAdapterPosition()).equals(OptionEnum.ISSUE_TRACKING)){
                optionImageView.setBackground(ContextCompat.getDrawable(mActivity,R.drawable.ic_issue_tracking));
                optionNameTextView.setText(mActivity.getString(R.string.issue_tracking));
                optionCardView.setOnClickListener(v ->{
                    // Preventing multiple clicks, using threshold of 1 second
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    mActivity.startActivity(new Intent(mActivity, IssueTrackingListActivity.class).putExtra(Constants.INTENT_KEY_PROJECT_NAME, projectName)
                            .putExtra(Constants.INTENT_KEY_PROJECT_ID, projectId));
                });

            } else {
                optionImageView.setBackground(ContextCompat.getDrawable(mActivity, R.drawable.ic_crane));
                optionNameTextView.setText(mActivity.getString(R.string.equipment));
                optionCardView.setOnClickListener(v -> {
                    // Preventing multiple clicks, using threshold of 1 second
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    mActivity.startActivity(new Intent(mActivity, EquipmentActivity.class).putExtra("project_name", projectName)
                            .putExtra("project_id", projectId));
                });

            }

        }


        private void openFormListActivity() {
            if (NetworkService.isNetworkAvailable(mActivity)) {
                CustomProgressBar.showDialog(mActivity);

                mprojectFormProvider.getProjectFormPermission(projectId, new ProviderResult<FormPermissionResponse>() {
                    @Override
                    public void success(FormPermissionResponse result) {
                        CustomProgressBar.dissMissDialog(mActivity);
                        Log.d("ProjectOptionAdapter", "openFormListActivity getProjectFormPermission success: ");
                        mActivity.startActivity(new Intent(mActivity, ProjectFormActivity.class).putExtra(Constants.INTENT_KEY_PROJECT_ID, projectId));
                    }

                    @Override
                    public void AccessTokenFailure(String message) {
                        CustomProgressBar.dissMissDialog(mActivity);
                    }

                    @Override
                    public void failure(String message) {
                        CustomProgressBar.dissMissDialog(mActivity);
                        mActivity.startActivity(new Intent(mActivity, ProjectFormActivity.class).putExtra(Constants.INTENT_KEY_PROJECT_ID, projectId));
                    }
                });


            } else {
                mActivity.startActivity(new Intent(mActivity, ProjectFormActivity.class).putExtra(Constants.INTENT_KEY_PROJECT_ID, projectId));
            }
        }

    }
}
