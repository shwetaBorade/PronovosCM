package com.pronovoscm.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.pronovoscm.PronovosApplication;
import com.pronovoscm.R;
import com.pronovoscm.data.LoginProvider;
import com.pronovoscm.data.ProviderResult;
import com.pronovoscm.model.request.login.LoginRequest;
import com.pronovoscm.model.response.login.LoginResponse;
import com.pronovoscm.utils.SharedPref;
import com.pronovoscm.utils.ui.CustomProgressBar;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Activity to login into app
 * Also have link to forgot password and privacy policy
 *
 * @author GWL
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    @BindView(R.id.emailEditText)
    EditText emailIdEt;
    @BindView(R.id.passwordEditText)
    EditText passwordEt;
    @BindView(R.id.errorTextView)
    TextView errorTextView;
    @BindView(R.id.emailErrorView)
    TextView emailErrorView;
    @BindView(R.id.passwordErrorView)
    TextView passwordErrorView;
    @BindView(R.id.forgotPasswordTextView)
    TextView forgotPasswordTextView;
    @BindView(R.id.showHide)
    CheckBox showHideCheckBox;
    @BindView(R.id.rememberMeCheckBox)
    CheckBox rememberMeCheckBox;
    //    @BindView(R.id.privacyPolicyTextView)
//    TextView privacyPolicyTextView;
    @BindView(R.id.loginButton)
    Button loginButton;
    @Inject
    LoginProvider loginRegistrationProvider;
    String userEmail = "tenant_user@example.com";
    String userPassword = "H#jn8eh4Dw2S4jV";
    private String emailId, password;
//    String userEmail = "nitin.bhawsar@galaxyweblinks.in";
//    String userPassword = "McC12345!";
//    String userEmail = "sourabh@example.com";
//    String userPassword = "Gwl@1234";
//    String userEmail = "nitin@yopmail.com";
//    String userPassword = "Nitin@123";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_view);
        ButterKnife.bind(this);
        ((PronovosApplication) getApplication()).getDaggerComponent().inject(this);
        loginButton.setOnClickListener(this);
        forgotPasswordTextView.setOnClickListener(this);
//        privacyPolicyTextView.setOnClickListener(this);
//        userEmail= "joansprabakaran.c@galaxyweblinks.in" ;
//        userPassword = "Cj@2023!";
//        emailIdEt.setText(userEmail);
//        passwordEt.setText(userPassword);
        String userEmail=SharedPref.getInstance(LoginActivity.this).readPrefs("user_name");
        if (userEmail != null && !TextUtils.isEmpty(userEmail)) {
            emailIdEt.setText(SharedPref.getInstance(LoginActivity.this).readPrefs("user_name"));
            rememberMeCheckBox.setChecked(true);
//            passwordEt.setText(SharedPref.getInstance(LoginActivity.this).readPrefs("user_password"));
        }
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
        passwordEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().length() > 0) {
                    passwordErrorView.setText("");
                    errorTextView.setText("");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        showHideCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    passwordEt.setTransformationMethod(HideReturnsTransformationMethod.getInstance());

                } else {
                    passwordEt.setTransformationMethod(PasswordTransformationMethod.getInstance());

                }
            }
        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        View v = getCurrentFocus();

        if (v != null &&
                (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_MOVE) &&
                v instanceof EditText &&
                !v.getClass().getName().startsWith("android.webkit.")) {
            int scrcoords[] = new int[2];
            v.getLocationOnScreen(scrcoords);
            float x = ev.getRawX() + v.getLeft() - scrcoords[0];
            float y = ev.getRawY() + v.getTop() - scrcoords[1];

            if (x < v.getLeft() || x > v.getRight() || y < v.getTop() || y > v.getBottom())
//                if (!emailIdEt.hasWindowFocus() && !passwordEt.hasWindowFocus()){
                    hideKeyboard(this);
//                }
        }
        return super.dispatchTouchEvent(ev);
    }
    public void hideKeyboard(Activity activity) {
        if (activity != null && activity.getWindow() != null && activity.getWindow().getDecorView() != null) {
            InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
        }
    }
    /**
     * Check for valid email id and password
     *
     * @return
     */
    public boolean checkValidation() {

        emailId = emailIdEt.getText().toString().trim();
        password = passwordEt.getText().toString().trim();
        if (TextUtils.isEmpty(emailId)) {
            emailErrorView.setText(R.string.please_enter_a_valid_email_address);
            if (TextUtils.isEmpty(password)) {
                passwordErrorView.setText(R.string.please_enter_your_password);
            }
        } else if (!Patterns.EMAIL_ADDRESS.matcher(emailId).matches()) {
            emailErrorView.setText(R.string.the_email_must_be_a_valid_email_address);
        } else if (TextUtils.isEmpty(password)) {
            passwordErrorView.setText(R.string.please_enter_your_password);
        } else {
            return true;
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.loginButton:
                errorTextView.setText("");
                if (checkValidation()) {
                    callLoginService();
                }

                break;
            case R.id.forgotPasswordTextView:
                startActivity(new Intent(this, ForgetPasswordActivity.class));

                break;
//            case R.id.privacyPolicyTextView:
//                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://pronovos.com/pronovos-privacy-policy/"));
//                startActivity(browserIntent);
//                break;
        }
    }


    /**
     * Api call for the user to login
     */
    private void callLoginService() {
        CustomProgressBar.showDialog(this);
        String version="1.0";
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            version = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        LoginRequest loginRequest = new LoginRequest(emailId, password, "android", "android", version);
        loginRegistrationProvider.loginUser(loginRequest, new ProviderResult<LoginResponse>() {
            @Override
            public void success(LoginResponse result) {
                if (rememberMeCheckBox.isChecked()) {
                    SharedPref.getInstance(LoginActivity.this).writePrefs("user_name", emailIdEt.getText().toString());
                } else {
                    SharedPref.getInstance(LoginActivity.this).writePrefs("user_name", "");
                }

                SharedPref.getInstance(LoginActivity.this).writePrefs(SharedPref.SESSION_DETAILS, new Gson().toJson(result));
                CustomProgressBar.dissMissDialog(LoginActivity.this);
                startActivity(new Intent(LoginActivity.this, ProjectsActivity.class).putExtra("after_login",true));
                SharedPref.getInstance(LoginActivity.this).writePrefs(SharedPref.LOGIN_SUCCESS_OR_NOT_PREF, SharedPref.LOGIN_SUCCESS_VALUE);
                finish();
            }

            @Override
            public void AccessTokenFailure(String message) {
                CustomProgressBar.dissMissDialog(LoginActivity.this);
            }

            @Override
            public void failure(String message) {
                CustomProgressBar.dissMissDialog(LoginActivity.this);
                errorTextView.setText(message);
            }
        });
    }

}
