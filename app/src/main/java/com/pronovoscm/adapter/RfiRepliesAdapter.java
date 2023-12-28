package com.pronovoscm.adapter;

import android.app.Activity;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pronovoscm.PronovosApplication;
import com.pronovoscm.R;
import com.pronovoscm.persistence.domain.PjRfi;
import com.pronovoscm.persistence.domain.PjRfiAttachments;
import com.pronovoscm.persistence.domain.PjRfiReplies;
import com.pronovoscm.persistence.repository.ProjectRfiRepository;
import com.pronovoscm.utils.DateFormatter;
import com.pronovoscm.utils.MyHtmlTagHandler;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RfiRepliesAdapter extends RecyclerView.Adapter {
    Activity mActivity;
    List<PjRfiReplies> pjRfiReplies;
    PjRfi pjRfi;
    ProjectRfiRepository projectRfiRepository;

    private boolean isOffline;

    public RfiRepliesAdapter(Activity activity, List<PjRfiReplies> pjRfiReplies, boolean isOffline,
                             ProjectRfiRepository projectRfiRepository, PjRfi pjRfi) {
        ((PronovosApplication) activity.getApplication()).getDaggerComponent().inject(this);
        this.pjRfiReplies = pjRfiReplies;
        this.mActivity = activity;
        this.isOffline = isOffline;
        this.projectRfiRepository = projectRfiRepository;
        this.pjRfi = pjRfi;

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_view_rfi_reply_list_item, parent, false);
        return new RfiDetailsAttachmentHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((RfiDetailsAttachmentHolder) holder).bind(pjRfiReplies.get(position), new OnItemClickListener() {
            @Override
            public void onItemClick(int position, PjRfiAttachments attachments) {
                Log.d("AttachmentAdapter", "onItemClick: ");
            }
        });
    }

    @Override
    public int getItemCount() {
        if (pjRfiReplies != null && pjRfiReplies.size() > 0) {
            return pjRfiReplies.size();
        }
        return 0;
    }


    public interface OnItemClickListener {
        void onItemClick(int position, PjRfiAttachments pjRfiAttachments);
    }

    public class RfiDetailsAttachmentHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tvRfiReplyNameTime)
        TextView tvRfiReplyNameTime;
        @BindView(R.id.tvRfiReplyValue)
        TextView tvRfiReplyValue;
        @BindView(R.id.tvRfiAttachmentLabel)
        TextView tvRfiAttachmentLabel;
        @BindView(R.id.rfiReplyAttachmentsRecyclerView)
        RecyclerView rfiReplyAttachmentsRecyclerView;

        public RfiDetailsAttachmentHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public String customizeListTags(@Nullable String html) {
            if (html == null) {
                return null;
            }
            String UL = "CUSTOM_UL";
            String OL = "CUSTOM_OL";
            String LI = "CUSTOM_LI";
            String DD = "CUSTOM_DD";

            html = html.replace("<ul", "<" + UL);
            html = html.replace("</ul>", "</" + UL + ">");
            html = html.replace("<ol", "<" + OL);
            html = html.replace("</ol>", "</" + OL + ">");
            html = html.replace("<dd", "<" + DD);
            html = html.replace("</dd>", "</" + DD + ">");
            html = html.replace("<li", "<" + LI);
            html = html.replace("</li>", "</" + LI + ">");
            return html;
        }

        void bind(PjRfiReplies re, OnItemClickListener listener) {
            tvRfiReplyNameTime.setText(re.getUsername() + " on " + DateFormatter.formatDateForPunchList(re.updatedAt));

            int myInteger = mActivity.getResources().getInteger(R.integer.quantity_length);
            if (TextUtils.isEmpty(re.getRfiReplies())) {
                tvRfiReplyValue.setText("-");

            } else {
                Log.d("REPLYADAPTER", "bind: " + re.getRfiReplies());
                tvRfiReplyValue.setText(Html.fromHtml(customizeListTags(re.getRfiReplies()), null, new MyHtmlTagHandler()));
               /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    // FROM_HTML_MODE_LEGACY is the behaviour that was used for versions below android N
                    // we are using this flag to give a consistent behaviour
                    tvRfiReplyValue.setText(Html.fromHtml( re.getRfiReplies(),Html.FROM_HTML_MODE_LEGACY,null, new MyHtmlTagHandler()));
                } else {
                    tvRfiReplyValue.setText(Html.fromHtml( re.getRfiReplies(),null, new MyHtmlTagHandler()));
                }*/
                //tvRfiReplyValue.setText(Html.fromHtml(, Html.FROM_HTML_MODE_LEGACY));
            }
            List<PjRfiAttachments> pjRfiAttachmentsList = projectRfiRepository.getRfiReplyAttachmentsList(pjRfi.getPjRfiId(), re.getPjRfiRepliesId());
            if (pjRfiAttachmentsList == null || pjRfiAttachmentsList.size() == 0) {
                tvRfiAttachmentLabel.setVisibility(View.GONE);
            } else {
                RfiDetailsAttachmentAdapter rfiDetailsAttachmentAdapter = new RfiDetailsAttachmentAdapter(mActivity, pjRfiAttachmentsList, isOffline, projectRfiRepository);
                rfiReplyAttachmentsRecyclerView.setLayoutManager(new GridLayoutManager(rfiReplyAttachmentsRecyclerView.getContext(), myInteger));
                rfiReplyAttachmentsRecyclerView.setAdapter(rfiDetailsAttachmentAdapter);
            }

        }

    }


}
