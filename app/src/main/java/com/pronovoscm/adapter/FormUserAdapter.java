package com.pronovoscm.adapter;

import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.pronovoscm.R;
import com.pronovoscm.activity.ProjectFormUserActivity;
import com.pronovoscm.persistence.domain.UserForms;
import com.pronovoscm.utils.DateFormatter;

import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FormUserAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private int projectId;
    private List<UserForms> userForms;
    private String projectName;
    private ProjectFormUserActivity projectFormUserActivity;
    private long mLastClickTime = 0;

    public FormUserAdapter(List<UserForms> userForms, int projectId, ProjectFormUserActivity projectFormUserActivity) {
        this.userForms = userForms;
        this.projectId = projectId;
        this.projectFormUserActivity = projectFormUserActivity;

    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View view = inflater.inflate(R.layout.form_user_list_item, parent, false);
        return new DrawingFolderHolder(view);

    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((DrawingFolderHolder) holder).bind();
    }

    @Override
    public int getItemCount() {
        if (userForms != null) {
            return userForms.size();
        } else {
            return 0;
        }
    }

    @Override
    public int getItemViewType(int position) {

        return position;

    }

    public interface clickUserForm {
        public void onClickUserForm(UserForms userForms);
    }

    public class DrawingFolderHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.folderNameTextView)
        TextView folderNameTextView;
        /*@BindView(R.id.formRevieionTextView)
        TextView formRevieionTextView;*/
        @BindView(R.id.folderCardView)
        CardView folderCardView;
        @BindView(R.id.unsyncImageView)
        ImageView unsyncImageView;

        public DrawingFolderHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        private void bind() {
            UserForms userForm = userForms.get(getAdapterPosition());
            Date date = /*userForm.getUpdatedAt() != null ? userForm.getUpdatedAt() : */userForm.getCreatedAt();
            folderNameTextView.setText((DateFormatter.formatDateForImage(date)) + " - " + (userForms.get(getAdapterPosition()).getCreatedByUserName()));
            // formRevieionTextView.setText(projectFormUserActivity.getString(R.string.revision_number, userForms.get(getAdapterPosition()).getRevisionNumber()));
            folderCardView.setOnClickListener(v -> {
                // Preventing multiple clicks, using threshold of 1 second
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                projectFormUserActivity.onClickUserForm(userForm);
            });
            if (userForm.getIsSync() && userForm.getTempSubmittedData() == null) {
                unsyncImageView.setVisibility(View.GONE);
            } else {
                unsyncImageView.setVisibility(View.VISIBLE);
            }
        }
    }
}
