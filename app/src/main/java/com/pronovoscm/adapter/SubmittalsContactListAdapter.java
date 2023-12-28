package com.pronovoscm.adapter;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pronovoscm.R;
import com.pronovoscm.model.SubmittalAssigneeEnum;
import com.pronovoscm.persistence.domain.PjAssigneeAttachments;
import com.pronovoscm.persistence.domain.PjSubmittalContactList;
import com.pronovoscm.persistence.repository.ProjectSubmittalsRepository;
import com.pronovoscm.utils.DateFormatter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SubmittalsContactListAdapter extends
        RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Activity context;
    boolean isOffline;
    private List<PjSubmittalContactList> submittalsList;
    ProjectSubmittalsRepository projectSubmittalsRepository;
    AssigneeSubmittalsAttachmentAdapter submittalsDetailsAttachmentAdapter;

    public SubmittalsContactListAdapter(Activity context,
                                        List<PjSubmittalContactList> submittalsList, ProjectSubmittalsRepository projectSubmittalsRepository, boolean isOffline) {
        this.context = context;
        this.submittalsList = submittalsList;
        this.projectSubmittalsRepository = projectSubmittalsRepository;
        this.isOffline = isOffline;
    }

    public void setSubmittalList(List<PjSubmittalContactList> submittals) {
        this.submittalsList = submittals;
        Log.d("SubmittalsListAdapter", "setSubmittalList: " + submittalsList.size());
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_view_submittal_contact, parent, false);
        ButterKnife.bind(this, view);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        PjSubmittalContactList submittalItem = submittalsList.get(holder.getAbsoluteAdapterPosition());
        if (holder instanceof ViewHolder) {
            ((ViewHolder) holder).bind(context, submittalItem, position);
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
        @BindView(R.id.tvName)
        TextView tvName;
        @BindView(R.id.tvCompanyName)
        TextView tvCompanyName;
        @BindView(R.id.tvResponse)
        TextView tvResponse;
        @BindView(R.id.tVComments)
        TextView tVComments;
        @BindView(R.id.tvAttachmentsLabel)
        TextView tvAttachmentsLabel;
        @BindView(R.id.tvAttachmentsEmpty)
        TextView tvAttachmentsEmpty;
        @BindView(R.id.tvResponseDate)
        TextView tvResponseDate;
        @BindView(R.id.submittalDetailAttachmentsRv)
        RecyclerView submittalDetailAttachmentsRv;


        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }

        void bind(Activity context, PjSubmittalContactList submittals, int position) {

            if (!TextUtils.isEmpty(submittals.getContactName())) {
                tvName.setText(submittals.getContactName());
            } else {
                tvName.setText("-");
            }
            if (!TextUtils.isEmpty(submittals.getCompanyName())) {
                tvCompanyName.setText(submittals.getCompanyName());
            } else {
                tvCompanyName.setVisibility(View.GONE);
            }
            if (submittals.getResponse() != null) {
                updateResponse(submittals);
            } else {
                tvResponse.setText("-");
            }
            if (submittals.getResponseDate() != null) {
                tvResponseDate.setText(context.getString(R.string.on, DateFormatter.formatDateForSubmittals(submittals.getResponseDate())));
            } else {
                tvResponseDate.setText("");
            }

            if (submittals.getComments() != null)
                tVComments.setText(submittals.getComments());
            else {
                tVComments.setText("-");
            }
            tvAttachmentsEmpty.setText("-");
            if (submittals.getPjSubmittalContactListId() != null) {
                submittalDetailAttachmentsRv.setVisibility(View.VISIBLE);
                List<PjAssigneeAttachments> pjAssigneeAttachments = projectSubmittalsRepository.getPjAssigneeAttachments(submittals.getPjSubmittalsId(), submittals.getPjSubmittalContactListId());
                if (pjAssigneeAttachments != null && !pjAssigneeAttachments.isEmpty()) {
                    tvAttachmentsEmpty.setVisibility(View.GONE);
                    int myInteger = context.getResources().getInteger(R.integer.quantity_length);
                    submittalsDetailsAttachmentAdapter = new AssigneeSubmittalsAttachmentAdapter(context, pjAssigneeAttachments, isOffline, projectSubmittalsRepository);
                    submittalDetailAttachmentsRv.setLayoutManager(new GridLayoutManager(submittalDetailAttachmentsRv.getContext(), myInteger));
                    submittalDetailAttachmentsRv.setAdapter(submittalsDetailsAttachmentAdapter);
                } else {
                    tvAttachmentsEmpty.setVisibility(View.VISIBLE);
                    submittalDetailAttachmentsRv.setVisibility(View.GONE);
                }
            } else {
                tvAttachmentsEmpty.setVisibility(View.VISIBLE);
                submittalDetailAttachmentsRv.setVisibility(View.GONE);
            }
        }

        private void updateResponse(PjSubmittalContactList submittals) {
            switch (submittals.getResponse()) {
                case 0:
                    tvResponse.setText(SubmittalAssigneeEnum.NoResponse.getStatusString());
                    break;
                case 1:
                    tvResponse.setText(SubmittalAssigneeEnum.Approved.getStatusString());
                    break;
                case 2:
                    tvResponse.setText(SubmittalAssigneeEnum.Rejected.getStatusString());
                    break;
                case 3:
                    tvResponse.setText(SubmittalAssigneeEnum.ApprovedAsNoted.getStatusString());
                    break;
                case 4:
                    tvResponse.setText(SubmittalAssigneeEnum.Reviewed.getStatusString());
                    break;
                case 5:
                    tvResponse.setText(SubmittalAssigneeEnum.ReviewedAsNoted.getStatusString());
                    break;
                case 6:
                    tvResponse.setText(SubmittalAssigneeEnum.ReviseAndResubmit.getStatusString());
                    break;
                case 7:
                    tvResponse.setText(SubmittalAssigneeEnum.Other.getStatusString());
                    break;
            }
        }
    }


}
