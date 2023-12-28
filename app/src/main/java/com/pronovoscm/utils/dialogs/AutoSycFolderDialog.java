package com.pronovoscm.utils.dialogs;

import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;

import com.pronovoscm.PronovosApplication;
import com.pronovoscm.R;
import com.pronovoscm.data.ProjectDrawingFolderProvider;
import com.pronovoscm.persistence.domain.DrawingFolders;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AutoSycFolderDialog extends DialogFragment {
    @Inject
    ProjectDrawingFolderProvider mProjectsProvider;

    @BindView(R.id.previewSwitch)
    Switch switchAutoSync;
    @BindView(R.id.syncAllSwitch)
    Switch syncAllSwitch;

    private int folderId, projectId;
    private DrawingFolders drawingFolder;

    public static AutoSycFolderDialog newInstance(int folderId, int projectId) {

        Bundle args = new Bundle();
        args.putInt("folder_id", folderId);
        args.putInt("project_id", projectId);
        AutoSycFolderDialog fragment = new AutoSycFolderDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setStyle(DialogFragment.STYLE_NORMAL, R.style.Theme_Translucent_Dialog);
        if (getArguments() != null) {
            folderId = getArguments().getInt("folder_id");
            projectId = getArguments().getInt("project_id");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dialog_autosync, container, false);
        ((PronovosApplication) getActivity().getApplication()).getDaggerComponent().inject(this);
        ButterKnife.bind(this, rootView);
        setCancelable(true);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        drawingFolder = mProjectsProvider.getDrawingFolder(projectId, folderId);
        if (drawingFolder != null && drawingFolder.getSyncFolder()) {
            switchAutoSync.setChecked(drawingFolder.getSyncFolder());
        } else {
            switchAutoSync.setChecked(false);
        }
        if (drawingFolder != null && drawingFolder.getSyncDrawingFolder() != null && drawingFolder.getSyncDrawingFolder()) {
            syncAllSwitch.setChecked(drawingFolder.getSyncDrawingFolder());
        } else {
            syncAllSwitch.setChecked(false);
        }
        switchAutoSync.setOnCheckedChangeListener((buttonView, isChecked) -> {
            switchAutoSync.setEnabled(false);
            mProjectsProvider.updateDrawingFolder(projectId, folderId, isChecked);
            new Handler().postDelayed(() -> dismiss(), 500);
        });
        syncAllSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            syncAllSwitch.setEnabled(false);
            mProjectsProvider.updateDrawingFolderSync(projectId, folderId, isChecked);
            EventBus.getDefault().post(drawingFolder);

            new Handler().postDelayed(() -> dismiss(), 500);
        });
    }
}
