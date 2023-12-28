package com.pronovoscm.utils.dialogs;

import android.os.Bundle;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.pronovoscm.PronovosApplication;
import com.pronovoscm.R;
import com.pronovoscm.adapter.TagsAdapter;
import com.pronovoscm.data.ProjectsProvider;
import com.pronovoscm.model.response.login.LoginResponse;
import com.pronovoscm.persistence.domain.ImageTag;
import com.pronovoscm.utils.MessageEvent;
import com.pronovoscm.utils.SharedPref;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class TagsDialog extends DialogFragment implements View.OnClickListener, TagsAdapter.updateTags {
    @Inject
    ProjectsProvider projectsProvider;
    @BindView(R.id.saveTextView)
    TextView saveTextView;
    @BindView(R.id.cancelTextView)
    TextView cancelTextView;
    @BindView(R.id.titleTextView)
    TextView titleTextView;
    @BindView(R.id.tagsRecyclerView)
    RecyclerView tagsRecyclerView;
    @BindView(R.id.searchAlbumEditText)
    EditText searchAlbumEditText;
    @BindView(R.id.searchView)
    RelativeLayout searchView;
    private TagsAdapter mTagsAdapter;
    private ArrayList<ImageTag> selectedImageTag;
    private ArrayList<ImageTag> getSelectedImageTag;
    private List<ImageTag> allImageTag;
    private boolean unableToEditPhoto;
    private LoginResponse loginResponse;

    public static ArrayList<ImageTag> cloneList(ArrayList<ImageTag> imageTags) {
        ArrayList<ImageTag> clonedList = new ArrayList<ImageTag>(imageTags.size());
        for (ImageTag imageTag : imageTags) {
            clonedList.add(new ImageTag(imageTag));
        }
        return clonedList;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.Theme_Translucent_Dialog);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.tags_dialog_view, container, false);
        ButterKnife.bind(this, rootview);
        return rootview;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        cancelTextView.setOnClickListener(this);
        saveTextView.setOnClickListener(this);
        searchView.setVisibility(View.VISIBLE);
        selectedImageTag = (ArrayList<ImageTag>) (ArrayList<? extends Parcelable>) getArguments().getParcelableArrayList("selected_image_tags");
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(getActivity()).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        unableToEditPhoto = getArguments().getBoolean("unableToEditPhoto");
        getSelectedImageTag = new ArrayList<>(selectedImageTag);
        titleTextView.setText(R.string.select_keywords);
        ((PronovosApplication) getActivity().getApplication()).getDaggerComponent().inject(this);
        allImageTag = projectsProvider.getImageTags("", loginResponse);
        mTagsAdapter = new TagsAdapter(this, allImageTag, getSelectedImageTag, unableToEditPhoto);
        tagsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        tagsRecyclerView.setAdapter(mTagsAdapter);
        searchAlbumEditText.setHint(getString(R.string.search_here));
        searchAlbumEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {


            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                ArrayList<ImageTag> mSelectedImageTag = cloneList(getSelectedImageTag);
//                getSelectedImageTag.clear();
//                getSelectedImageTag.addAll(mSelectedImageTag);
                allImageTag.clear();
                allImageTag.addAll(projectsProvider.getImageTags(s.toString(), loginResponse));
//                mTagsAdapter = new TagsAdapter(TagsDialog.this, allImageTag, getSelectedImageTag,unableToEditPhoto);
//                tagsRecyclerView.setAdapter(mTagsAdapter);
                mTagsAdapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        if (unableToEditPhoto) {
            saveTextView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        MessageEvent messageEvent = new MessageEvent();
        switch (v.getId()) {
            case R.id.saveTextView:
                messageEvent.setImageTags(getSelectedImageTag);
                EventBus.getDefault().post(messageEvent);
                dismiss();
                break;
            case R.id.cancelTextView:
                dismiss();
                break;
            default:
                break;
        }
    }

    @Override
    public void onUpdateSelectedTags(ArrayList<ImageTag> selectedTag) {
//        getSelectedImageTag.clear();
        getSelectedImageTag=selectedTag;
        mTagsAdapter.notifyDataSetChanged();
    }
}

