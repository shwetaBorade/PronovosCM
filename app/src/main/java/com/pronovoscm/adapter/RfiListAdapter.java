package com.pronovoscm.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.pronovoscm.R;
import com.pronovoscm.activity.RfiDetailActivity;
import com.pronovoscm.model.RFIStatusEnum;
import com.pronovoscm.model.RfiListItem;
import com.pronovoscm.persistence.domain.PjRfi;
import com.pronovoscm.persistence.domain.PjRfiContactList;
import com.pronovoscm.utils.Constants;
import com.pronovoscm.utils.DateFormatter;

import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RfiListAdapter extends
        RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private final Context context;
    private List<RfiListItem> rfiItemsList;
    private int projectId;

    public RfiListAdapter(Context context,
                          List<RfiListItem> rfiList, int projectId) {
        this.context = context;
        this.rfiItemsList = rfiList;
        this.projectId = projectId;
    }

    public void setRfiList(List<RfiListItem> rfis) {
        this.rfiItemsList = rfis;
        // Log.d("RfiAdapter", "setRfiList: rfiList " + rfiItemsList.size());
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_view_rfi_list, parent, false);
        ButterKnife.bind(this, view);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        PjRfi rfiItem = rfiItemsList.get(holder.getAbsoluteAdapterPosition()).getPjRfi();
        PjRfiContactList rfiContactList = rfiItemsList.get(holder.getAbsoluteAdapterPosition()).getPjRfiContactList();
        if (holder instanceof ViewHolder) {
            ((ViewHolder) holder).bind(context, rfiItem, position, rfiContactList);
        }
    }

    @Override
    public int getItemCount() {
        if (rfiItemsList != null && rfiItemsList.size() > 0) {
            return rfiItemsList.size();
        } else
            return
                    0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.rfiNumberTV)
        TextView rfiNumberTV;
        @BindView(R.id.textViewRfiTitle)
        TextView textViewRfiTitle;
        @BindView(R.id.textViewDateSubmitValue)
        TextView textViewDateSubmitValue;
        @BindView(R.id.textViewAssginTo)
        TextView textViewAssginTo;
        @BindView(R.id.textViewStatus)
        TextView textViewStatus;
        @BindView(R.id.textViewDateDue)
        TextView textViewDateDue;
        @BindView(R.id.rfiItemCardView)
        RelativeLayout rfiItemCardView;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }

        void bind(Context context, PjRfi rfi, int position, PjRfiContactList pjRfiContactList) {
            rfiNumberTV.setText(rfi.getRfiNumber());


      /*      if (!TextUtils.isEmpty(rfi.getRfiTitle()))
                textViewRfiTitle.setText(rfi.getRfiTitle());
            else {
                textViewRfiTitle.setText("-");
            }*/
            if (!TextUtils.isEmpty(rfi.getRfiTitle())) {
                String title = rfi.getRfiNumber() + " - " + rfi.getRfiTitle();
                rfiNumberTV.setText(title);
            } else {
                textViewRfiTitle.setText("-");
            }


            if (rfi.getDateSubmitted() != null)
                textViewDateSubmitValue.setText(DateFormatter.formatDateForPunchList(rfi.getDateSubmitted()));
            else {
                textViewDateSubmitValue.setText("-");
            }
            if (rfi.getDueDate() != null)
                textViewDateDue.setText(DateFormatter.formatDateForPunchList(rfi.getDueDate()));
            else textViewDateDue.setText("-");
            showStatus(context, rfi);
            if (pjRfiContactList != null) {
                if (!TextUtils.isEmpty(pjRfiContactList.getName())) {
                    Log.d("ListRfiAdapter", "bind: ");
                    textViewAssginTo.setText(pjRfiContactList.getName());
                } else textViewAssginTo.setText("-");
            } else textViewAssginTo.setText("-");
            rfiItemCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent detailIntent = new Intent(context, RfiDetailActivity.class);

                    detailIntent.putExtra(Constants.INTENT_KEY_PROJECT_ID, projectId);
                    detailIntent.putExtra(Constants.INTENT_KEY_PROJECT_RFI_ID, rfi.getPjRfiId());
                    detailIntent.putExtra(Constants.INTENT_KEY_PROJECT_RFI, rfi);
                    detailIntent.putExtra(Constants.INTENT_KEY_PROJECT_RFI_CONTACT, pjRfiContactList);
                    context.startActivity(detailIntent);
                }
            });
        }

        /*
      Open Status should only show red when the date is past today's date. Any RFIs with Open Status with date due in future should be black text
       */
        private void showStatus(Context context, PjRfi rfi) {
            if (rfi.getStatus() == 0) {
                textViewStatus.setText(RFIStatusEnum.Draft.getStatusString());
                textViewStatus.setTextColor(ContextCompat.getColor(textViewStatus.getContext(), R.color.gray_535a73));
                textViewDateDue.setTextColor(ContextCompat.getColor(textViewDateDue.getContext(), R.color.gray_535a73));
            } else if (rfi.getStatus() == 1) {
                textViewStatus.setText(RFIStatusEnum.Open.getStatusString());
                textViewStatus.setTextColor(ContextCompat.getColor(textViewStatus.getContext(), R.color.gray_535a73));
                textViewDateDue.setTextColor(ContextCompat.getColor(textViewStatus.getContext(), R.color.gray_535a73));
                if (rfi != null && rfi.getDueDate() != null && rfi.getDueDate().before(new Date())) {
                    textViewStatus.setTextColor(ContextCompat.getColor(textViewStatus.getContext(), R.color.red_ff2424));
                    textViewDateDue.setTextColor(ContextCompat.getColor(textViewDateDue.getContext(), R.color.red_ff2424));
                }
            } else {
                textViewStatus.setText(RFIStatusEnum.Closed.getStatusString());
                textViewStatus.setTextColor(ContextCompat.getColor(textViewStatus.getContext(), R.color.gray_535a73));
                textViewDateDue.setTextColor(ContextCompat.getColor(textViewDateDue.getContext(), R.color.gray_535a73));
            }
        }
    }
}
