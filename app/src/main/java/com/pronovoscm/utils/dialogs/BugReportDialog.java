/**
 * Class: BugReportDialog
 * Purpose: Report drawing bug
 * Created by: Shweta Jain on 14/09/23.
 */
package com.pronovoscm.utils.dialogs;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.pronovoscm.R;
import com.pronovoscm.activity.DrawingListTabActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

@SuppressLint("ValidFragment")
public class BugReportDialog extends DialogFragment {

    @BindView(R.id.issue_edit_text)
    EditText issueEditText;

    DrawingListTabActivity.onReportBugListener onReportBugListener;

    @SuppressLint("ValidFragment")
    public BugReportDialog(DrawingListTabActivity.onReportBugListener onReportBugListener) {
        this.onReportBugListener = onReportBugListener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.Theme_Translucent_Dialog);
            }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dialog_bug_report, container, false);
        ButterKnife.bind(this, rootView);
        setCancelable(true);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }


    @OnClick(R.id.saveTextView)
    public void onSaveClick() {
        if (!issueEditText.getText().toString().trim().isEmpty()) {
            this.dismiss();
            onReportBugListener.onReportBug(issueEditText.getText().toString().trim());
        } else {
            Toast.makeText(getContext(),
                    getResources().getString(R.string.please_provides_comments),
                    Toast.LENGTH_SHORT).show();
        }

    }


    @OnClick(R.id.cancelTextView)
    public void onCancelClick() {
        this.dismiss();
    }
}
