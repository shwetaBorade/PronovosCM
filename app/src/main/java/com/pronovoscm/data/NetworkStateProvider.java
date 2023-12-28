package com.pronovoscm.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.pronovoscm.PronovosApplication;
import com.pronovoscm.utils.Constants;

import org.greenrobot.eventbus.EventBus;

import java.util.Date;

/**
 * Provide network state stored in shared pref
 * and other useful methods for updating network state pref value
 *
 * @author Nitin Bhawsar
 */
public class NetworkStateProvider {

    private static final String TAG = NetworkStateProvider.class.getName();

    private static final int OFFLINE_ALERT_ALARM_REQUEST_CODE = 1332121;
    private static final int ONE_DAY_MINUTES = 60 * 24;

    private final EventBus eventBus = EventBus.getDefault();

    private Context mContext;

    public NetworkStateProvider(Context context) {
        this.mContext = context;
    }

    /**
     * Sets device state to connected.
     */
    public void setDeviceConnected() {
        setState(NetworkStateEnum.CONNECTED);

    }

    /**
     * Sets device state to offline.
     */
    public void setDeviceOffline() {
        setState(NetworkStateEnum.OFFLINE);

    }

    /**
     * Checks if state is offline.
     *
     * @return true if device is offline, false if device is connected
     */
    public boolean isOffline() {
        String state = PronovosApplication.getSharedPreferences().getString(Constants.SHARED_PREFERENCES_NETWORK_STATE_KEY, null);
        if (state != null) {
            NetworkStateEnum stateEnum = NetworkStateEnum.toNetworkStateEnum(state);
            return stateEnum.isOffline();
        }
        return false;
    }

    /**
     * Gets the number of minutes the device has been offline.
     *
     * @return minutes device has been offline, zero if not offline.
     */
    public long getDeviceOfflineMinutes() {
        long offline = PronovosApplication.getSharedPreferences().getLong(Constants.DEVICE_OFFLINE_DATE, -1);
        if (offline > 0) {
            Date date = new Date(offline);
//            return DateUtils.differenceMinutes(new Date(), date);
        }
        return 0;
    }

    public boolean isOfflineMorethan24Hours() {
        long offlineMinutes = getDeviceOfflineMinutes();
        return offlineMinutes > ONE_DAY_MINUTES;
    }

    private void setState(NetworkStateEnum state) {

        Log.d(TAG, String.format("Setting network state to [%s]", state));

        SharedPreferences sharedPref = PronovosApplication.getSharedPreferences();
        SharedPreferences.Editor editor = sharedPref.edit();

        editor.putString(Constants.SHARED_PREFERENCES_NETWORK_STATE_KEY, state.name());

        if (state.isOffline()) {
            editor.putLong(Constants.DEVICE_OFFLINE_DATE, new Date().getTime());

        } else {
            editor.remove(Constants.DEVICE_OFFLINE_DATE);

        }

        editor.apply();
        eventBus.post(state);
    }

    private Date getDeviceOfflineDate() {
        long offline = PronovosApplication.getSharedPreferences().getLong(Constants.DEVICE_OFFLINE_DATE, -1);
        if (offline > 0) {
            return new Date(offline);
        }
        return null;
    }

    /**
     * Enumeration of Connected or Offline state
     */
    public enum NetworkStateEnum {

        CONNECTED,
        OFFLINE;

        public static NetworkStateEnum toNetworkStateEnum(String myEnumString) {
            try {
                return valueOf(myEnumString);
            } catch (Exception ex) {
                return CONNECTED;
            }
        }

        public boolean isOffline() {
            return this.equals(OFFLINE);
        }

    }


}
