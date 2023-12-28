package com.pronovoscm.utils;

import android.app.Activity;
import android.content.Context;
import androidx.fragment.app.DialogFragment;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class CustomDialogFragment extends DialogFragment {
    public void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
