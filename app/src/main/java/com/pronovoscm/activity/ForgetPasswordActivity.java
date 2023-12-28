package com.pronovoscm.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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

/**
 * Activity offers user to change or reset the user password
 *
 * @author GWL
 */
public class ForgetPasswordActivity extends AppCompatActivity implements View.OnClickListener {

    static AlertDialog alertDialog;
    @BindView(R.id.emailEditText)
    EditText emailIdEt;

    @BindView(R.id.resetPasswordButton)
    TextView resetPasswordButton;

    @Inject
    LoginProvider loginRegistrationProvider;

    @BindView(R.id.emailErrorView)
    TextView emailErrorView;
    @BindView(R.id.titleTextView)
    TextView titleTextView;
    @BindView(R.id.errorTextView)
    TextView errorTextView;
    @BindView(R.id.rightImageView)
    ImageView rightImageView;
    @BindView(R.id.leftImageView)
    ImageView backImageView;
    private String emailId;

    public static void hideKeyboard(Activity activity) {
        if (activity != null && activity.getWindow() != null && activity.getWindow().getDecorView() != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot_password_view);
        ButterKnife.bind(this);
        ((PronovosApplication) getApplication()).getDaggerComponent().inject(this);
        resetPasswordButton.setOnClickListener(this);
        emailIdEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().length() > 0) {
                    emailErrorView.setText("");
                    errorTextView.setText("");

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        titleTextView.setText(R.string.forgot_password_heading);
        rightImageView.setVisibility(View.INVISIBLE);
        backImageView.setImageResource(R.drawable.ic_arrow_back);

        backImageView.setOnClickListener(v -> onBackPressed());
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
            case R.id.forgotPasswordTextView:
                break;
        }
    }

    /**
     * Forgot password api call
     */
    private void callForgetPasswordService() {
        CustomProgressBar.showDialog(this);
        ForgetPasswordRequest forgetPasswordRequest = new ForgetPasswordRequest(emailId);
        loginRegistrationProvider.forgotPassword(forgetPasswordRequest, new ProviderResult<ForgetPasswordResponse>() {
            @Override
            public void success(ForgetPasswordResponse result) {
                CustomProgressBar.dissMissDialog(ForgetPasswordActivity.this);
                errorTextView.setText("");

                /*showMessageAlert(ForgetPasswordActivity.this, "An email \n" +
                        "has been sent to \n" +
                        "your email address, \n" +
                        emailId + "\n" +
                        "Follow the directions in the \n" +
                        "email to reset your password.", getString(R.string.done));*/

                showMessageAlert(ForgetPasswordActivity.this, result.getData().getResponsemsg(), "Done");

            }

            @Override
            public void AccessTokenFailure(String message) {
                CustomProgressBar.dissMissDialog(ForgetPasswordActivity.this);

            }

            @Override
            public void failure(String message) {
                CustomProgressBar.dissMissDialog(ForgetPasswordActivity.this);
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
                onBackPressed();
                alertDialog.dismiss();
            });
            if (alertDialog != null && !alertDialog.isShowing()) {

                alertDialog.setCancelable(false);
                alertDialog.show();
                /*TextView textView = (TextView) alertDialog.findViewById(android.R.id.message);
                int valueInPixels = (int) getResources().getDimension(R.dimen.forget_password_text);
                textView.setTextSize(valueInPixels);
                textView.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
                textView.setGravity(Gravity.LEFT);
                textView.setTypeface(Typeface.create("sans-serif",Typeface.BOLD));*/
            }
            /*Button pbutton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
            pbutton.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
            int valueInPixelsButton = (int) getResources().getDimension(R.dimen.forget_password_button_text);
            pbutton.setTextSize(valueInPixelsButton);*/

        } catch (Exception e) {
        }

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        View v = getCurrentFocus();

        if (v != null && (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_MOVE) && v instanceof EditText && !v.getClass().getName().startsWith("android.webkit.")) {
            int scrcoords[] = new int[2];
            v.getLocationOnScreen(scrcoords);
            float x = ev.getRawX() + v.getLeft() - scrcoords[0];
            float y = ev.getRawY() + v.getTop() - scrcoords[1];

            if (x < v.getLeft() || x > v.getRight() || y < v.getTop() || y > v.getBottom()) {
                hideKeyboard(this);
            }
        }
        return super.dispatchTouchEvent(ev);
    }
}
