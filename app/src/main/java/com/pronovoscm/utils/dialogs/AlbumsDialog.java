package com.pronovoscm.utils.dialogs;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pronovoscm.PronovosApplication;
import com.pronovoscm.R;
import com.pronovoscm.adapter.SelectAlbumAdapter;
import com.pronovoscm.data.ProjectsProvider;
import com.pronovoscm.persistence.domain.PhotoFolder;
import com.pronovoscm.utils.PhotoFolderEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AlbumsDialog extends DialogFragment implements View.OnClickListener, SelectAlbumAdapter.updatePhotoFolder {
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
    private SelectAlbumAdapter mSelectAlbumAdapter;
    private PhotoFolder mPhotoFolder;
    private List<PhotoFolder> allPhotoFolder;
    private int pjProjectId;


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
        mPhotoFolder = getArguments().getParcelable("photoFolder");
        pjProjectId = getArguments().getInt("pjProjectId");
        titleTextView.setText(getString(R.string.select_album));
        ((PronovosApplication) getActivity().getApplication()).getDaggerComponent().inject(this);
//        allPhotoFolder = projectsProvider.getPhotoFolders(pjProjectId,"");
        allPhotoFolder = projectsProvider.getNonStaticPhotoFolders(pjProjectId,"");
        mSelectAlbumAdapter = new SelectAlbumAdapter(this, allPhotoFolder, mPhotoFolder);
        tagsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        tagsRecyclerView.setAdapter(mSelectAlbumAdapter);

    }

    @Override
    public void onClick(View v) {
        PhotoFolderEvent photoFolderEvent = new PhotoFolderEvent();
        switch (v.getId()) {
            case R.id.saveTextView:
                photoFolderEvent.setPhotoFolder(mPhotoFolder);
                EventBus.getDefault().post(photoFolderEvent);
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
    public void onUpdateSelectedPhototFolder(PhotoFolder photoFolder) {
        mPhotoFolder = photoFolder;
    }
}

