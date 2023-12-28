package com.pronovoscm.adapter;

import android.app.Activity;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Spannable;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pronovoscm.R;
import com.pronovoscm.model.response.projectsubcontractors.Contact;
import com.pronovoscm.utils.NoUnderlineSpan;
import com.pronovoscm.utils.TextViewNoUnderline;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProjectSubcontractorContactAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final NoUnderlineSpan mNoUnderlineSpan;
    private List<Contact> contactList;
    private Activity mActivity;

    public ProjectSubcontractorContactAdapter(Activity mActivity, List<Contact> contactList) {
        this.contactList = contactList;
        this.mActivity = mActivity;
        mNoUnderlineSpan = new NoUnderlineSpan();
        setHasStableIds(true);

    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.team_contact_item_list, parent, false);

        return new ResourceViewHolder(view);

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (contactList.get(position) instanceof Contact) {
            ((ResourceViewHolder) holder).bind();
        }
    }

    @Override
    public int getItemCount() {
        if (contactList != null) {
            return contactList.size();
        } else {
            return 0;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (contactList.get(position) instanceof Contact) {
            return position;
        } else {
            return -1;
        }
    }

    public class ResourceViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.userNameTextView)
        TextView userNameTextView;
        @BindView(R.id.userphoneTextView)
        TextView userphoneTextView;
        @BindView(R.id.userroleTextView)
        TextView userroleTextView;
        @BindView(R.id.useremailTextView)
        TextViewNoUnderline useremailTextView;

        public ResourceViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }


        private void bind() {
            Contact contact = contactList.get(getAdapterPosition());
            userNameTextView.setText(TextUtils.isEmpty(contact.getContactName())?"-":contact.getContactName());
            userroleTextView.setText(TextUtils.isEmpty(contact.getProjectrole())?"-":contact.getProjectrole());
            userphoneTextView.setText(TextUtils.isEmpty(contact.getCellphone())?"-":contact.getCellphone());
            useremailTextView.setText(TextUtils.isEmpty(contact.getEmail())?"-":contact.getEmail());
            if (userphoneTextView.getText() instanceof Spannable) {
                Spannable s = (Spannable) userphoneTextView.getText();
                s.setSpan(mNoUnderlineSpan, 0, s.length(), Spanned.SPAN_MARK_MARK);
            }
          /*  if (useremailTextView.getText() instanceof Spannable) {
                Spannable s = (Spannable) useremailTextView.getText();
                s.setSpan(mNoUnderlineSpan, 0, s.length(), Spanned.SPAN_MARK_MARK);
            }*/
        }

    }

}