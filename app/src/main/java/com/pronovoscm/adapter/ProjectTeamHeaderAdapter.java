package com.pronovoscm.adapter;

import android.app.Activity;
import android.graphics.Rect;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Spannable;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pronovoscm.R;
import com.pronovoscm.model.response.projectteam.Contact;
import com.pronovoscm.model.response.projectteam.Team;
import com.pronovoscm.utils.NoUnderlineSpan;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProjectTeamHeaderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    NoUnderlineSpan mNoUnderlineSpan;
    private List<Team> mResourceList;
    private Activity mActivity;

    public ProjectTeamHeaderAdapter(Activity mActivity, List<Team> photosList) {
        this.mResourceList = photosList;
        this.mActivity = mActivity;
        mNoUnderlineSpan = new NoUnderlineSpan();
        setHasStableIds(true);

    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        if (viewType == -1) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.project_team_header, parent, false);

        return new ResourceHeaderViewHolder(view);
       /* } else {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.resource_item_list, parent, false);

            return new ResourceViewHolder(view);

        }*/
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
      /*  if (mResourceList.get(position) instanceof Users) {
            ((ResourceViewHolder) holder).bind((Users) mResourceList.get(position));
        } else {*/
        ((ResourceHeaderViewHolder) holder).bind();

//        }
    }

    @Override
    public int getItemCount() {
        if (mResourceList != null) {
            return mResourceList.size();
        } else {
            return 0;
        }
    }

    @Override
    public int getItemViewType(int position) {
//        if (mResourceList.get(position) instanceof Users) {
        return position;
//        } else {
//            return -1;
//        }
    }


    public class ResourceHeaderViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.teamNameTV)
        TextView teamNameTV;
        @BindView(R.id.teamCompanyTextView)
        TextView teamCompanyTextView;
        @BindView(R.id.teamAddressTextView)
        TextView teamAddressTextView;
        @BindView(R.id.teamphoneTextView)
        TextView teamphoneTextView;
        @BindView(R.id.teamContactRecyclerView)
        RecyclerView teamContactRecyclerView;
        @BindView(R.id.bottomView)
        View bottomView;
        @BindView(R.id.invisibleView)
        View invisibleView;
        private LinearLayoutManager linearLayoutManager;
        private ProjectTeamContactAdapter projectResourceUserAdapter;
        private List<Contact> contactList = new ArrayList<>();

        public ResourceHeaderViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            linearLayoutManager = new LinearLayoutManager(mActivity);
            teamContactRecyclerView.setLayoutManager(linearLayoutManager);
            projectResourceUserAdapter = new ProjectTeamContactAdapter(mActivity, contactList);
            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mActivity, linearLayoutManager.getOrientation()) {
                @Override
                public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                    int position = parent.getChildAdapterPosition(view);
                    // hide the divider for the last child
                    if (position == parent.getAdapter().getItemCount() - 1) {
                        outRect.setEmpty();
                    } else {
                        super.getItemOffsets(outRect, view, parent, state);
                    }
                }
            };
            dividerItemDecoration.setDrawable(ContextCompat.getDrawable(teamContactRecyclerView.getContext(), R.drawable.divider));

            teamContactRecyclerView.addItemDecoration(dividerItemDecoration);
            teamContactRecyclerView.setAdapter(projectResourceUserAdapter);
        }


        private void bind() {
            Team resources = mResourceList.get(getAdapterPosition());

            if (getAdapterPosition() == 0) {
                invisibleView.setVisibility(View.VISIBLE);
            } else {
                invisibleView.setVisibility(View.GONE);
            }
            teamNameTV.setText(TextUtils.isEmpty(resources.getDiscipline()) ? "-" : resources.getDiscipline());
            teamCompanyTextView.setText(TextUtils.isEmpty(resources.getCompany()) ? "-" : resources.getCompany());
            teamAddressTextView.setText(TextUtils.isEmpty(resources.getAddress()) ? "-" : resources.getAddress());
            teamphoneTextView.setText(TextUtils.isEmpty(resources.getPhone()) ? "-" : resources.getPhone());
            if (teamphoneTextView.getText() instanceof Spannable) {
                Spannable s = (Spannable) teamphoneTextView.getText();
                s.setSpan(mNoUnderlineSpan, 0, s.length(), Spanned.SPAN_MARK_MARK);
            }
            if (resources.getContacts() == null || resources.getContacts().size() == 0) {
                bottomView.setVisibility(View.GONE);
            } else {

                bottomView.setVisibility(View.VISIBLE);
            }
            contactList.clear();
            contactList.addAll(resources.getContacts());
            projectResourceUserAdapter.notifyDataSetChanged();
        }

    }
}