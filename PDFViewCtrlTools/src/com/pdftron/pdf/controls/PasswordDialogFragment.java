//---------------------------------------------------------------------------------------
// Copyright (c) 2001-2019 by PDFTron Systems Inc. All Rights Reserved.
// Consult legal.txt regarding legal and license information.
//---------------------------------------------------------------------------------------

package com.pdftron.pdf.controls;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.pdftron.pdf.tools.R;
import com.pdftron.pdf.utils.Utils;

import java.io.File;

/**
 * A Dialog fragment for entering password
 */
public class PasswordDialogFragment extends DialogFragment {

    /**
     * Callback interface to be invoked when an interaction is needed.
     */
    public interface PasswordDialogFragmentListener {
        /**
         * Called when OK button has been clicked.
         *
         * @param fileType The file type
         * @param file     The file
         * @param path     The file path
         * @param password The entered password
         * @param id       The ID
         */
        void onPasswordDialogPositiveClick(int fileType, @Nullable File file, @Nullable String path, String password, @Nullable String id);

        /**
         * Called when Cancel button has been clicked.
         *
         * @param fileType The file type
         * @param file     The file
         * @param path     The file path
         */
        void onPasswordDialogNegativeClick(int fileType, @Nullable File file, @Nullable String path);

        /**
         * Called when dialog is dismissed
         *
         * @param forcedDismiss True if the dialog is forced to dismiss
         */
        void onPasswordDialogDismiss(boolean forcedDismiss);
    }

    private PasswordDialogFragmentListener mListener;
    private File mFile;
    private boolean mForcedDismiss;
    private String mPath;
    private int mFileType;
    private int mMessageId = -1;
    private String mId;
    private boolean mAllowEmptyPassword = true;
    private boolean mRequireConfirmation = false;

    private String mPassword;
    private String mPasswordConfirm;

    private TextInputEditText mPasswordEditText;

    private static final String KEY_FILE = "key_file";
    private static final String KEY_FILETYPE = "key_filetype";
    private static final String KEY_PATH = "key_path";
    private static final String KEY_ID = "key_id";
    private static final String KEY_HINT = "key_hint";
    private static final String KEY_CONFIRMATION_HINT = "key_confirmation_hint";
    private static final String KEY_POSITIVE_STRING_RES = "key_positive_string_res";
    private static final String KEY_ALLOW_EMPTY = "key_allow_empty";
    private static final String KEY_REQUIRE_CONFIRMATION = "key_require_confirmation";

    /**
     * Class constructor
     */
    public PasswordDialogFragment() {

    }

    public static class Builder {
        private final Bundle bundle = new Bundle();

        public Builder setFileType(int fileType) {
            bundle.putInt(KEY_FILETYPE, fileType);
            return this;
        }

        public Builder setFile(File file) {
            bundle.putSerializable(KEY_FILE, file);
            return this;
        }

        public Builder setPath(String path) {
            bundle.putString(KEY_PATH, path);
            return this;
        }

        public Builder setId(String id) {
            bundle.putString(KEY_ID, id);
            return this;
        }

        public Builder setHint(String hint) {
            bundle.putString(KEY_HINT, hint);
            return this;
        }

        public Builder setConfirmationHint(String hint) {
            bundle.putString(KEY_CONFIRMATION_HINT, hint);
            return this;
        }

        public Builder setPositiveStringRes(@StringRes int res) {
            bundle.putInt(KEY_POSITIVE_STRING_RES, res);
            return this;
        }

        public Builder setAllowEmptyPassword(boolean allowEmptyPassword) {
            bundle.putBoolean(KEY_ALLOW_EMPTY, allowEmptyPassword);
            return this;
        }

        public Builder setRequireConfirmation(boolean requireConfirmation) {
            bundle.putBoolean(KEY_REQUIRE_CONFIRMATION, requireConfirmation);
            return this;
        }

        public PasswordDialogFragment build() {
            PasswordDialogFragment fragment = new PasswordDialogFragment();
            fragment.setArguments(bundle);
            return fragment;
        }
    }

    /**
     * Returns a new instance of the class.
     */
    public static PasswordDialogFragment newInstance(int fileType, File file, String path, String id) {
        return new Builder().setFileType(fileType)
                .setFile(file)
                .setPath(path)
                .setId(id)
                .build();
    }

    /**
     * Returns a new instance of the class.
     */
    public static PasswordDialogFragment newInstance(int fileType, File file, String path, String id, String hint) {
        return new Builder().setFileType(fileType)
                .setFile(file)
                .setPath(path)
                .setId(id)
                .setHint(hint)
                .build();
    }

    /**
     * Sets {@link PasswordDialogFragmentListener} listener
     *
     * @param listener The listener
     */
    public void setListener(PasswordDialogFragmentListener listener) {
        mListener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Initialize member variables from the arguments
        Bundle args = getArguments();
        String hint = null;
        String confirmationHint = null;
        int positiveStringRes = R.string.ok;
        if (args != null) {
            if (args.containsKey(KEY_FILE)) {
                mFile = (File) args.getSerializable(KEY_FILE);
            }
            mFileType = args.getInt(KEY_FILETYPE);
            mPath = args.getString(KEY_PATH);
            mId = args.getString(KEY_ID);
            hint = args.getString(KEY_HINT);
            confirmationHint = args.getString(KEY_CONFIRMATION_HINT);
            mAllowEmptyPassword = args.getBoolean(KEY_ALLOW_EMPTY, true);
            mRequireConfirmation = args.getBoolean(KEY_REQUIRE_CONFIRMATION, false);
            positiveStringRes = args.getInt(KEY_POSITIVE_STRING_RES, R.string.ok);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialog = inflater.inflate(R.layout.fragment_password_dialog, null);

        TextInputLayout passwordLayout = dialog.findViewById(R.id.password_layout);
        if (!Utils.isNullOrEmpty(hint)) {
            passwordLayout.setHint(hint);
        }
        mPasswordEditText = dialog.findViewById(R.id.password_edit_text);
        if (mRequireConfirmation) {
            mPasswordEditText.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        } else {
            mPasswordEditText.setImeOptions(EditorInfo.IME_ACTION_GO);
        }
        if (mAllowEmptyPassword) {
            mPasswordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    // If enter key was pressed, then submit password
                    if (actionId == EditorInfo.IME_ACTION_GO) {
                        onPositiveClicked();
                        return true;
                    }
                    return false;
                }
            });

            mPasswordEditText.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_ENTER) {
                        onPositiveClicked();
                        return true;
                    }
                    return false;
                }
            });
        }

        TextInputLayout confirmLayout = dialog.findViewById(R.id.password_confirm_layout);
        confirmLayout.setVisibility(mRequireConfirmation ? View.VISIBLE : View.GONE);

        builder.setView(dialog)
                .setTitle(R.string.dialog_password_title)
                .setPositiveButton(positiveStringRes, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onPositiveClicked();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        PasswordDialogFragment.this.getDialog().cancel();
                    }
                });
        if (mMessageId != -1) {
            builder.setMessage(mMessageId);
        }

        final AlertDialog alertDialog = builder.create();

        // Show keyboard automatically when the dialog is shown.
        mPasswordEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus && alertDialog.getWindow() != null) {
                    alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
            }
        });

        if (!mAllowEmptyPassword) {
            mPasswordEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    mPassword = s.toString();
                    Button posButton = alertDialog.getButton(Dialog.BUTTON_POSITIVE);
                    if (posButton != null) {
                        posButton.setEnabled(canSubmit());
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (mRequireConfirmation) {
                        if (!canSubmit()) {
                            confirmLayout.setError(confirmLayout.getContext().getString(R.string.dialog_password_not_matching_warning));
                        } else {
                            confirmLayout.setError(null);
                        }
                    }
                }
            });
        }

        if (mRequireConfirmation) {
            TextInputEditText confirmEditText = dialog.findViewById(R.id.password_confirm_edit_text);
            if (!Utils.isNullOrEmpty(confirmationHint)) {
                confirmLayout.setHint(confirmationHint);
            }
            confirmEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    mPasswordConfirm = s.toString();
                    Button posButton = alertDialog.getButton(Dialog.BUTTON_POSITIVE);
                    if (posButton != null) {
                        posButton.setEnabled(canSubmit());
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (!canSubmit()) {
                        confirmLayout.setError(confirmLayout.getContext().getString(R.string.dialog_password_not_matching_warning));
                    } else {
                        confirmLayout.setError(null);
                    }
                }
            });
        }

        return alertDialog;
    }

    private void onPositiveClicked() {
        String password = mPasswordEditText.getText().toString().trim();
        mForcedDismiss = true;
        if (PasswordDialogFragment.this.getDialog().isShowing()) {
            PasswordDialogFragment.this.getDialog().dismiss();
        }
        if (null != mListener) {
            mListener.onPasswordDialogPositiveClick(mFileType, mFile, mPath, password, mId);
        }
    }

    private boolean canSubmit() {
        if (mRequireConfirmation) {
            return !Utils.isNullOrEmpty(mPassword) && mPassword.equals(mPasswordConfirm);
        }
        if (!mAllowEmptyPassword) {
            return !Utils.isNullOrEmpty(mPassword);
        }
        return true;
    }

    @Override
    public void onStart() {
        super.onStart();

        AlertDialog d = (AlertDialog) getDialog();
        if (d != null) {
            if (!mAllowEmptyPassword) {
                Button posButton = d.getButton(Dialog.BUTTON_POSITIVE);
                if (posButton != null) {
                    posButton.setEnabled(false);
                }
            }
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (null != mListener) {
            mListener.onPasswordDialogDismiss(mForcedDismiss);
        }
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        if (null != mListener) {
            mListener.onPasswordDialogNegativeClick(mFileType, mFile, mPath);
        }
    }

    /**
     * Sets the message ID
     *
     * @param messageId The message ID
     */
    public void setMessage(int messageId) {
        this.mMessageId = messageId;
    }
}
