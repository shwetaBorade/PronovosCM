package com.pronovoscm.broadcastreceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.pronovoscm.PronovosApplication;
import com.pronovoscm.data.NetworkStateProvider;
import com.pronovoscm.services.NetworkService;
import com.pronovoscm.services.OfflineDataSynchronizationService;

import javax.inject.Inject;

/**
 * Broadcast receiver that monitors for connectivity changes,
 * the receiver will monitor for network connect/disconnects
 * events and update the Network State Provider accordingly.
 *
 * @author Nitin Bhawsar
 */
public class NetworkStateReceiver extends BroadcastReceiver {

    @Inject
    NetworkStateProvider networkStateProvider;

    /**
     * This method is called when the BroadcastReceiver is receiving network state change
     * // an Intent broadcast.
     *
     * @param context application context
     * @param intent  broadcast intent
     */

    @Override
    public void onReceive(Context context, Intent intent) {

        ((PronovosApplication) context.getApplicationContext()).getDaggerComponent().inject(this);
        boolean isNetworkConnected = NetworkService.isNetworkAvailable(context);

        if (isNetworkConnected) {
            if (networkStateProvider.isOffline()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                    JobUtil.scheduleJob(context);
                    Log.d("check", "start service");
                    networkStateProvider.setDeviceConnected();
                } else {
                    OfflineDataSynchronizationService.start(context);
                    networkStateProvider.setDeviceConnected();

                }
            }
        } else {
            networkStateProvider.setDeviceOffline();
        }


    }

}
