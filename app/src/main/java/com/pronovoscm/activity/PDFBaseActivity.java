package com.pronovoscm.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.pdftron.pdf.controls.DocumentActivity;
import com.pronovoscm.PronovosApplication;
import com.pronovoscm.data.NetworkStateProvider;
import com.pronovoscm.model.response.login.LoginResponse;
import com.pronovoscm.utils.LogUtils;
import com.pronovoscm.utils.SharedPref;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import javax.inject.Inject;

import butterknife.ButterKnife;

public abstract class PDFBaseActivity extends DocumentActivity {
    public static final int READ_WRITE_STORAGE = 52;
    private static final String TAG = LogUtils.makeLogTag(BaseActivity.class);
    @Inject
    NetworkStateProvider networkStateProvider;
    private ProgressDialog mProgressDialog;
    private AlertDialog alertDialog;

    public boolean requestPermission(String permission) {
        boolean isGranted = BaseActivity.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED;
        if (!isGranted) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{permission},
                    READ_WRITE_STORAGE);
        }
        return isGranted;
    }

    public void isPermissionGranted(boolean isGranted, String permission) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case READ_WRITE_STORAGE:
                isPermissionGranted(grantResults[0] == PackageManager.PERMISSION_GRANTED, permissions[0]);
                break;
        }
    }

    /**
     * Show the loader over any activity
     *
     * @param message message shown in the loader
     */
    protected void showLoading(@NonNull String message) {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage(message);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
    }

    /**
     * Hide the loader
     */
    protected void hideLoading() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }

    /**
     * Show snack bar to notify user for any event
     *
     * @param message message to be shown in snack bar
     */
    protected void showSnackbar(@NonNull String message) {
        View view = findViewById(android.R.id.content);
        if (view != null) {
            Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Defines content view.
     *
     * @return ID of view
     */
    protected abstract int doGetContentView();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null && savedInstanceState.getBoolean("not_get_intent")) {
        } else {
            super.onCreate(savedInstanceState);
        }

        setContentView(doGetContentView());

        ButterKnife.bind(this);

//        this.doSetupActionBar();

    }

    @Override
    public void onResume() {
        super.onResume();
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);

        Log.d(TAG, "onResume: " + "service start");
        LoginResponse loginResponse = (new Gson().fromJson(SharedPref.getInstance(this).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));

        if (loginResponse == null) {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
        }
        //   registerReceiver(networkStateReceiver,new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
        setOfflineStatus(networkStateProvider.isOffline());
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
        /*messageCountHandlerThread.quit();
        getContentResolver().unregisterContentObserver(messageCountObserver);*/

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: " + "service end");
        try {
            unregisterReceiver(((PronovosApplication) getApplication()).networkStateReceiver);
        } catch (IllegalArgumentException ignored) {

        }

    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


    /**
     * method to receive local network state changed events
     * EventBus library uses the Java Reflection API to access this method
     * and update UI for connected disconnected events
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNetworkStateChange(NetworkStateProvider.NetworkStateEnum event) {
        LogUtils.LOGD(getClass().getName(), String.format("Network state event [%s] received", event));
        setOfflineStatus(event.isOffline());
        if (!event.isOffline()) {
            Log.i(TAG, "BaseActivity: setupAndStartWorkManager");
            doGetApplication().setupAndStartWorkManager();
        }
    }

    /**
     * Post an event offline status
     *
     * @param offline Represent status for Online/Offline.
     */
    private void setOfflineStatus(boolean offline) {
        if (offline) {
           /* mOfflineConnectedIndicator.setText(getString(R.string.not_connected));
            mOfflineConnectedIndicator.setTextColor(ContextCompat.getColor(this, R.color.white));
            if (networkStateProvider.isOfflineMorethan24Hours()) {
                on24HoursOfflineState(Constants.DEVICE_OFFLINE);
                            }*/
            EventBus.getDefault().post(true);

        } else {
            EventBus.getDefault().post(false);
            /*mOfflineConnectedIndicator.setText(getString(R.string.connected));
            mOfflineConnectedIndicator.setTextColor(ContextCompat.getColor(this, R.color.color_CONNECTED_GREEN));
            if (mOfflineMessageDialogFragment != null) {
                mOfflineMessageDialogFragment.dismiss();
            }*/
        }
    }

    /**
     * Extended Offline If the tablet continues to be in offline state for over 24 hours
     * show the dialog with offline massage this event will be posted by Alarm Receiver
     */
   /* @Subscribe
    public void on24HoursOfflineState(String offlineDialog) {
        mOfflineMessageDialogFragment = MessageFragment.newInstance(offlineDialog, getString(R.string.offline_message));
        mOfflineMessageDialogFragment.show(getFragmentManager(), null);
        mOfflineMessageDialogFragment.hideOkButton();
    }*/

   /* @Subscribe
    public void handleUnreadMessageCountEvent(UnreadMessageCountEvent event) {
        LogUtils.LOGI(TAG, String.format("Received [%s] unread messages.", event.getTenantId()));
        if (messageBadgeCountView == null) {
            return;
        }
        if (event.getTenantId() <= 0) {
            messageBadgeCountView.setVisibility(View.GONE);
        } else {
            if (event.getTenantId() > 99) {
                messageBadgeCountView.setText("99+");
            } else {
                messageBadgeCountView.setText(String.valueOf(event.getTenantId()));
            }
            messageBadgeCountView.setVisibility(View.VISIBLE);
        }
    }
*/

    /*@Subscribe
    public void handleUnreadMessageReceivedEvent(UnreadMessageReceivedEvent event) {
        LogUtils.LOGI(TAG, "Unread message event triggered.");
        GetUnreadMessageCountTask.run(getContentResolver(), driverProvider.getActiveDriverId());
    }

    protected void setTitle(String value) {
        if (textViewHeaderTitle != null) {
            textViewHeaderTitle.setText(value);
        }
    }*/
    protected PronovosApplication doGetApplication() {
        return (PronovosApplication) getApplication();
    }


    public void hideKeyboard(Activity activity) {
        if (activity != null && activity.getWindow() != null && activity.getWindow().getDecorView() != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
        }
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
                alertDialog.dismiss();

            });
            if (alertDialog != null && !alertDialog.isShowing()) {
                alertDialog.setCancelable(false);
                alertDialog.show();
            }


        } catch (Exception e) {
        }

    }
}
