package com.pronovoscm.utils.dialogs;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.pronovoscm.PronovosApplication;
import com.pronovoscm.R;
import com.pronovoscm.data.LoginProvider;
import com.pronovoscm.data.ProviderResult;
import com.pronovoscm.model.request.forgetpassword.ForgetPasswordRequest;
import com.pronovoscm.model.response.forgetpassword.ForgetPasswordResponse;
import com.pronovoscm.utils.ui.CustomProgressBar;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ForgotPasswordDialog extends DialogFragment implements View.OnClickListener {
    @BindView(R.id.emailEditText)
    EditText emailIdEt;
    @BindView(R.id.resetPasswordButton)
    TextView resetPasswordButton;

    @Inject
    LoginProvider loginRegistrationProvider;

    @BindView(R.id.emailErrorView)
    TextView emailErrorView;
    @BindView(R.id.errorTextView)
    TextView errorTextView;
    @BindView(R.id.closeImageView)
    ImageView closeImageView;
    private AlertDialog alertDialog;
    private String emailId;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.Theme_Translucent_Dialog);
        ((PronovosApplication) getActivity().getApplication()).getDaggerComponent().inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.forgot_password_view, container, false);
        ButterKnife.bind(this, rootview);
        initDialog();
        return rootview;
    }

    private void initDialog() {
        resetPasswordButton.setOnClickListener(this);
        closeImageView.setOnClickListener(this);
        emailIdEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().length() > 0) {
                    emailErrorView.setText("");

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    /**
     * Check for valid user email
     */
    public boolean checkValidation() {
        emailId = emailIdEt.getText().toString().trim();

        if (TextUtils.isEmpty(emailId)) {
            emailErrorView.setText(R.string.please_enter_a_valid_email_address);
        } else if (!Patterns.EMAIL_ADDRESS.matcher(emailId).matches()) {
            emailErrorView.setText(R.string.the_email_must_be_a_valid_email_address);
        } else {
            return true;
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.resetPasswordButton:
                if (checkValidation()) {
                    callForgetPasswordService();
                }
                break;
            case R.id.closeImageView:
                dismiss();
                break;
        }
    }

    /**
     * Forgot password api call
     */
    private void callForgetPasswordService() {
        CustomProgressBar.showDialog(getActivity());
        ForgetPasswordRequest forgetPasswordRequest = new ForgetPasswordRequest(emailId);
        loginRegistrationProvider.forgotPassword(forgetPasswordRequest, new ProviderResult<ForgetPasswordResponse>() {
            @Override
            public void success(ForgetPasswordResponse result) {
                CustomProgressBar.dissMissDialog(getActivity());
                errorTextView.setText("");

                showMessageAlert(getActivity(), result.getData().getResponsemsg(), getString(R.string.ok));

            }

            @Override
            public void AccessTokenFailure(String message) {
                CustomProgressBar.dissMissDialog(getActivity());

            }

            @Override
            public void failure(String message) {
                CustomProgressBar.dissMissDialog(getActivity());
                errorTextView.setText(message);

            }
        });
    }

    /**
     * Alert to show message
     *
     * @param context
     * @param message
     * @param positiveButtonText
     */
    public void showMessageAlert(final Context context, String message, String positiveButtonText) {
        try {
            if (alertDialog == null || !alertDialog.isShowing()) {
                alertDialog = new AlertDialog.Builder(context).create();
            }
            alertDialog.setMessage(message);
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, positiveButtonText, (dialog, which) -> {
//                onBackPressed();
                ForgotPasswordDialog.this.dismiss();
                alertDialog.dismiss();

            });
            if (alertDialog != null && !alertDialog.isShowing()) {
                alertDialog.setCancelable(false);
                alertDialog.show();
            }
            Button pbutton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
            pbutton.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
        } catch (Exception e) {
        }
    }
}
