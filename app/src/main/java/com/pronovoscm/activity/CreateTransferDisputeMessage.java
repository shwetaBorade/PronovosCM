package com.pronovoscm.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.textfield.TextInputEditText;
import androidx.core.content.ContextCompat;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.pronovoscm.R;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.OnClick;

public class CreateTransferDisputeMessage extends BaseActivity {
    @BindView(R.id.disputeMessageEditText)
    TextInputEditText disputeMessageEditText;
    @BindView(R.id.leftImageView)
    ImageView backImageView;
    @BindView(R.id.rightImageView)
    ImageView rightImageView;
    @BindView(R.id.titleTextView)
    TextView titleTextView;
    @BindView(R.id.commentTitle)
    TextView commentTitle;
    @BindView(R.id.cancelTextView)
    TextView cancelTextView;
    @BindView(R.id.saveTextView)
    TextView saveTextView;
    @BindView(R.id.disputeErrorTextView)
    TextView disputeErrorTextView;
    @BindView(R.id.offlineTextView)
    TextView offlineTextView;
    private String transferOption;

    @Override
    protected int doGetContentView() {
        return R.layout.add_dispute_message_view;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        doGetApplication().getDaggerComponent().inject(this);
        transferOption = getIntent().getStringExtra("transfer_option");
        backImageView.setImageResource(R.drawable.ic_arrow_back);
        titleTextView.setText(transferOption);
        disputeMessageEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                disputeErrorTextView.setText("");
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        String text = "Please list exact equipment items and quantities that you are disputing on the current transfer <font color=#d0021b>*</font>";

        commentTitle.setText(Html.fromHtml(text));
        rightImageView.setVisibility(View.INVISIBLE);
    }

    @OnClick(R.id.leftImageView)
    public void backViewClick() {
        hideKeyboard();
        super.onBackPressed();
    }

    @OnClick(R.id.cancelTextView)
    public void cancelViewClick() {
        hideKeyboard();
        super.onBackPressed();
    }

    @OnClick(R.id.saveTextView)
    public void saveViewClick() {
        hideKeyboard();
        if (TextUtils.isEmpty(disputeMessageEditText.getText().toString())) {
            disputeErrorTextView.setText(getString(R.string.this_field_is_required));
            return;
        }

        Intent intent = getIntent();
        intent.putExtra("dispute_message", disputeMessageEditText.getText().toString());
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        hideKeyboard();
        super.onBackPressed();
    }

    public void hideKeyboard() {
        if (this != null && this.getWindow() != null && this.getWindow().getDecorView() != null) {
            InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(this.getWindow().getDecorView().getWindowToken(), 0);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(Boolean event) {
        if (event) {
            offlineTextView.setVisibility(View.VISIBLE);
        } else {
            offlineTextView.setVisibility(View.GONE);
        }
    }

}
