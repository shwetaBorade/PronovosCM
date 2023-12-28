package com.pronovoscm.utils.dialogs;

import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.textfield.TextInputEditText;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pronovoscm.PronovosApplication;
import com.pronovoscm.R;
import com.pronovoscm.data.ProjectsProvider;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddFolderDialog extends DialogFragment implements View.OnClickListener {
    @Inject
    ProjectsProvider mProjectsProvider;

    @BindView(R.id.saveTextView)
    TextView saveTextView;
    @BindView(R.id.cancelTextView)
    TextView cancelTextView;
    @BindView(R.id.folderNameEditText)
    TextInputEditText folderNameEditText;
    private int projectId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.Theme_Translucent_Dialog);
        ((PronovosApplication) getActivity().getApplication()).getDaggerComponent().inject(this);


    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.add_folder_dialog_view, container, false);
        ButterKnife.bind(this, rootview);
        projectId = getArguments().getInt("projectId");
        return rootview;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        cancelTextView.setOnClickListener(this);
        saveTextView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.saveTextView:

                if (folderNameEditText.getText().toString().trim().length() > 0) {
                    mProjectsProvider.addNewFolder(folderNameEditText.getText().toString(), projectId);
                }
                AsyncTask.execute(() -> {
                    EventBus.getDefault().post("addNEWFolder");
                    dismiss();
                });

                break;
            case R.id.cancelTextView:
                dismiss();
                break;
        }
    }
}
