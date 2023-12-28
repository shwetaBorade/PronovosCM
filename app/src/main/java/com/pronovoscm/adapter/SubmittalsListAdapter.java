package com.pronovoscm.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.pronovoscm.R;
import com.pronovoscm.activity.ProjectFormUserActivity;
import com.pronovoscm.activity.SubmittalDetailActivity;
import com.pronovoscm.activity.SubmittalsListActivity;
import com.pronovoscm.model.SubmittalListItem;
import com.pronovoscm.model.SubmittalStatusEnum;
import com.pronovoscm.persistence.domain.PjSubmittalContactList;
import com.pronovoscm.persistence.domain.PjSubmittals;
import com.pronovoscm.services.NetworkService;
import com.pronovoscm.utils.Constants;
import com.pronovoscm.utils.DateFormatter;

import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SubmittalsListAdapter extends
        RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final SubmittalsListActivity activity;
    private final int projectId;
    private List<SubmittalListItem> submittalsList;

    public SubmittalsListAdapter(SubmittalsListActivity activity,
                                 List<SubmittalListItem> submittalsList, int projectId) {
        this.activity = activity;
        this.submittalsList = submittalsList;
        this.projectId = projectId;
    }

    public void setSubmittalList(List<SubmittalListItem> submittals) {
        this.submittalsList = submittals;
        Log.d("SubmittalsListAdapter", "setSubmittalList: " + submittalsList.size());
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_view_submittals_list, parent, false);
        ButterKnife.bind(this, view);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        PjSubmittals submittalItem = submittalsList.get(holder.getAbsoluteAdapterPosition()).getPjSubmittals();
        int contactCount = submittalsList.get(holder.getAbsoluteAdapterPosition()).getContactCount();
        {
            PjSubmittalContactList assigneeSubmittalsList = null;
            if (submittalsList.get(holder.getAbsoluteAdapterPosition()).getPjSubmittalContactList() != null) {
                assigneeSubmittalsList = submittalsList.get(holder.getAbsoluteAdapterPosition()).getPjSubmittalContactList();
            }
            if (holder instanceof ViewHolder) {
                ((ViewHolder) holder).bind(activity, submittalItem, position, assigneeSubmittalsList, contactCount);
            }
        }
    }

    @Override
    public int getItemCount() {
        if (submittalsList != null && submittalsList.size() > 0) {
            return submittalsList.size();
        } else
            return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.submittalsNumberTV)
        TextView submittalsNumberTV;
        @BindView(R.id.textViewSubmittalsTitle)
        TextView textViewSubmittalsTitle;
        @BindView(R.id.textViewDateSubmitValue)
        TextView textViewDateSubmitValue;
        @BindView(R.id.textViewAssginTo)
        TextView textViewAssginTo;
        @BindView(R.id.textViewStatus)
        TextView textViewStatus;
        @BindView(R.id.assignedCountId)
        TextView assignedCountId;
        @BindView(R.id.textViewDateDue)
        TextView textViewDateDue;
        @BindView(R.id.submittalsItemCardView)
        LinearLayout submittalsItemCardView;
        @BindView(R.id.ivStatus)
        ImageView ivStatus;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }

        @SuppressLint("SetTextI18n")
        void bind(SubmittalsListActivity activity, PjSubmittals submittals, int position, PjSubmittalContactList assigneeSubmittals, int contactCount) {
            submittalsNumberTV.setText(submittals.getSubmittalNumber());

            if (!TextUtils.isEmpty(submittals.getSubmittalTitle())) {
                String title = submittals.getSubmittalNumber() + " - " + submittals.getSubmittalTitle();
                submittalsNumberTV.setText(title);
            } else {
                textViewSubmittalsTitle.setText("-");
            }


            if (submittals.getSubmittedDate() != null)
                textViewDateSubmitValue.setText(DateFormatter.formatDateForSubmittals(submittals.getSubmittedDate()));
            else {
                textViewDateSubmitValue.setText("-");
            }
            if (submittals.getDueDate() != null)
                textViewDateDue.setText(DateFormatter.formatDateForSubmittals(submittals.getDueDate()));
            else textViewDateDue.setText("-");
            showStatus(activity, submittals);
            if (assigneeSubmittals != null && !TextUtils.isEmpty(assigneeSubmittals.getContactName())) {
                textViewAssginTo.setText(assigneeSubmittals.getContactName());
            } else if (contactCount > 0) {
                textViewAssginTo.setText("");
            } else {
                textViewAssginTo.setText("-");
            }

            if (contactCount > 1) {
                assignedCountId.setVisibility(View.VISIBLE);
                // decrease the count by one as we are showing one name here
                contactCount = contactCount - 1;
                assignedCountId.setText("+" + contactCount);
            } else if (contactCount == 1 && assigneeSubmittals != null && TextUtils.isEmpty(assigneeSubmittals.getContactName())) {
                assignedCountId.setVisibility(View.VISIBLE);
                assignedCountId.setText("+" + contactCount);
            } else {
                assignedCountId.setVisibility(View.GONE);
            }
            submittalsItemCardView.setOnClickListener(v -> {
                if (submittals.getIsDetailedSync()) {
                    Intent detailIntent = new Intent(activity, SubmittalDetailActivity.class);
                    detailIntent.putExtra(Constants.INTENT_KEY_PROJECT_ID, projectId);
                    detailIntent.putExtra(Constants.INTENT_KEY_PROJECT_SUBMITTALS_ID, submittals.getPjSubmittalsId());
                    detailIntent.putExtra(Constants.INTENT_KEY_PROJECT_SUBMITTALS, submittals);
                    activity.startActivity(detailIntent);
                } else {
                    // show alert dialog if details are not synced
                    if (NetworkService.isNetworkAvailable(activity)) {
                        activity.showMessageAlert(activity, activity.getString(R.string.detail_not_synced), activity.getString(R.string.ok));
                    } else {
                        activity.showMessageAlert(activity, activity.getString(R.string.detail_not_synced_internet), activity.getString(R.string.ok));
                    }
                }
            });
        }

        /*
      Open Status should only show red when the date is past today's date. Any submittal with Open Status with date due in future should be black text
       */
        private void showStatus(SubmittalsListActivity activity, PjSubmittals submittals) {
            if (submittals.getStatus().equalsIgnoreCase(SubmittalStatusEnum.Draft.getStatusString())) {
                textViewStatus.setText(SubmittalStatusEnum.Draft.getStatusString());
                textViewStatus.setTextColor(ContextCompat.getColor(textViewStatus.getContext(), R.color.gray_535a73));
                ivStatus.getDrawable().mutate().setColorFilter(activity.getColor(R.color.gray_cccccc), PorterDuff.Mode.SRC_IN);
            } else if (submittals.getStatus().equalsIgnoreCase(SubmittalStatusEnum.Open.getStatusString())) {
                textViewStatus.setText(SubmittalStatusEnum.Open.getStatusString());
                textViewStatus.setTextColor(ContextCompat.getColor(textViewStatus.getContext(), R.color.gray_535a73));
                textViewDateDue.setTextColor(ContextCompat.getColor(textViewStatus.getContext(), R.color.gray_535a73));
                if (submittals.getDueDate() != null && submittals.getDueDate().before(new Date())) {
                    textViewStatus.setTextColor(ContextCompat.getColor(textViewStatus.getContext(), R.color.red_ff2424));
                    ivStatus.getDrawable().mutate().setColorFilter(activity.getColor(R.color.red_ff2424), PorterDuff.Mode.SRC_IN);
                } else {
                    ivStatus.getDrawable().mutate().setColorFilter(activity.getColor(R.color.blue_color_picker), PorterDuff.Mode.SRC_IN);
                }
            } else {
                textViewStatus.setText(SubmittalStatusEnum.Closed.getStatusString());
                textViewStatus.setTextColor(ContextCompat.getColor(textViewStatus.getContext(), R.color.gray_535a73));
                ivStatus.getDrawable().mutate().setColorFilter(activity.getColor(R.color.green_complete), PorterDuff.Mode.SRC_IN);
            }
        }
    }

}
