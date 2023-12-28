package com.pronovoscm.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.Nullable;

import com.pronovoscm.PronovosApplication;
import com.pronovoscm.data.NetworkStateProvider;
import com.pronovoscm.persistence.domain.OfflineEvent;
import com.pronovoscm.persistence.repository.OfflineEventRepository;
import com.pronovoscm.utils.LogUtils;

import java.util.List;

import javax.inject.Inject;

/**
 * An Intent service to synchronized all offline event data to server.
 *
 * @author Nitin Bhawsar
 */
public class OfflineDataSynchronizationService extends IntentService {

    private static final String TAG = LogUtils.makeLogTag(OfflineDataSynchronizationService.class);

    private static final int INTERVAL = 120 * 1000;

    @Inject
    OfflineEventRepository offlineEventRepository;

    @Inject
    NetworkStateProvider networkStateProvider;
  /*  @Inject
    TaskApi taskApi;

    @Inject
    TaskRepository taskRepository;

    @Inject
    CscProvider cscProvider;

    @Inject
    VehicleProvider vehicleProvider;*/

    public OfflineDataSynchronizationService() {
        super(OfflineDataSynchronizationService.class.getSimpleName());
    }

    public static void start(Context context) {
        Intent background = new Intent(context, OfflineDataSynchronizationService.class);
        context.startService(background);
    }

    /*public static void startAlarm(Context context) {

        Intent alarmIntent = new Intent(context, OfflineDataSynchronizationService.class);

        boolean isRunning = PendingIntent.getService(context, 0, alarmIntent, PendingIntent.FLAG_NO_CREATE) != null;
        if (!isRunning) {
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            if (alarmManager == null) {
                LogUtils.LOGW(TAG, "Alarm manager is null, unable to start offline data sync alarm.");
                return;
            }
            LogUtils.LOGI(TAG, "Starting offline data sync alarm.");
            PendingIntent pendingIntent; //= PendingIntent.getService(context, 0, alarmIntent, 0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
    pendingIntent = PendingIntent.getService(context, 0, alarmIntent, PendingIntent.FLAG_IMMUTABLE)

}else {
    pendingIntent = PendingIntent.getService(context, 0, alarmIntent, 0);
}
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis(), INTERVAL, pendingIntent);
        } else {
            LogUtils.LOGD(TAG, "Offline data sync alarm already running.");
        }
    }*/

    /*private void doSignIn(OfflineEvent offlineEvent, Task task) throws IOException {

        LogUtils.LOGD(TAG, String.format("Processing offline sign in for task [%s].", offlineEvent.getTaskId()));

        TaskSignInRequest taskSignInRequest = new TaskSignInRequest(task.getId(), task.getActualStart(), task.getZonarCardId(), vehicleProvider.getAssetNumber());
        taskSignInRequest.setLocation(offlineEvent.getLocation());
        taskSignInRequest.setOffline(true);

        Call<EmployeeTaskResponse> call = taskApi.taskSignIn(task.getId(), taskSignInRequest);

        Response<EmployeeTaskResponse> response = call.execute();

        if (response.isSuccessful()) {
            offlineEventRepository.deleteOfflineEvent(offlineEvent);
        } else {
            ResponseBody responseBody = response.errorBody();
            if (responseBody != null) {
                throw new IOException(String.format("API Error, code [%s], body [%s]", response.code(), responseBody.string()));
            }
            throw new IOException(String.format("API Error, code [%s]", response.code()));
        }

    }*/

   /* private void doSignOut(OfflineEvent offlineEvent, Task task) throws IOException {

        LogUtils.LOGD(TAG, String.format("Processing offline sign out for task [%s].", offlineEvent.getTaskId()));

        TaskSignOutRequest taskSignOutRequest = new TaskSignOutRequest(task, task.getActualEnd());
        taskSignOutRequest.setLocation(offlineEvent.getLocation());
        taskSignOutRequest.setOffline(true);

        Call<EmployeeTaskResponse> call = taskApi.taskSignOut(task.getId(), taskSignOutRequest);
        //do the synchronus call to do sign out
        Response<EmployeeTaskResponse> response = call.execute();

        if (response.isSuccessful()) {
            offlineEventRepository.deleteOfflineEvent(offlineEvent);
        } else {
            ResponseBody responseBody = response.errorBody();
            if (responseBody != null) {
                throw new IOException(String.format("API Error, code [%s], body [%s]", response.code(), responseBody.string()));
            }
            throw new IOException(String.format("API Error, code [%s]", response.code()));
        }

    }*/


    /*private void doUpdateActivity(OfflineEvent offlineEvent, TaskActivity taskActivity) throws IOException {

        LogUtils.LOGD(TAG, String.format("Processing offline event for activity [%s].", taskActivity.getActivityRowId()));

        TaskActivityUpdateRequest request = new TaskActivityUpdateRequest(taskActivity);


        Call<TaskActivityResponse> call = taskApi.updateTaskActivity(request);

        Response<TaskActivityResponse> response = call.execute();

        if (response.isSuccessful()) {
            offlineEventRepository.deleteOfflineEvent(offlineEvent);
        } else {
            ResponseBody responseBody = response.errorBody();
            if (responseBody != null) {
                throw new IOException(String.format("API Error, code [%s], body [%s]", response.code(), responseBody.string()));
            }
            throw new IOException(String.format("API Error, code [%s]", response.code()));
        }

    }*/

    /*public static void stopAlarm(Context context) {

        Intent alarmIntent = new Intent(context, OfflineDataSynchronizationService.class);

        PendingIntent pendingIntent; //= PendingIntent.getService(context, 0, alarmIntent, PendingIntent.FLAG_NO_CREATE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
    pendingIntent = PendingIntent.getService(context, 0, alarmIntent, PendingIntent.FLAG_NO_CREATE | PendingIntent.FLAG_IMMUTABLE)

}else {
    pendingIntent = PendingIntent.getService(context, 0, alarmIntent, PendingIntent.FLAG_NO_CREATE);
}
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager == null) {
            LogUtils.LOGW(TAG, "Alarm manager is null, unable to offline data sync alarm.");
            return;
        }
        LogUtils.LOGI(TAG, "Stopping offline data sync alarm.");
        alarmManager.cancel(pendingIntent);
        if (pendingIntent != null) {
            pendingIntent.cancel();
        }
    }*/

    @Override
    public void onCreate() {
        super.onCreate();
        ((PronovosApplication) getApplicationContext()).getDaggerComponent().inject(this);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        LogUtils.LOGI(TAG, "Running offline data synchronization service.");

        if (!NetworkService.isNetworkAvailable(this)) {
            return;
        }

        // get all offline tasks.
        List<OfflineEvent> offlineEvents = offlineEventRepository.getAllOfflineEvent();
        if (offlineEvents == null || offlineEvents.isEmpty()) {
            if (NetworkService.isNetworkAvailable(this)) {
                networkStateProvider.setDeviceConnected();
            }
            return;
        }

        LogUtils.LOGI(TAG, String.format("Processing [%s] offline events.", offlineEvents.size()));

        try {

            for (OfflineEvent offlineEvent : offlineEvents) {

                if (offlineEvent.getType().equals(OfflineEvent.OfflineEventType.TASK)) {
                    /*Task task = taskRepository.getTaskById(offlineEvent.getTaskId());
                    if (task == null) {
                        LogUtils.LOGW(TAG, String.format("Task [%s] not found for offline event of [%s], deleting event.", offlineEvent.getTaskId(), offlineEvent.getEvent()));
                        offlineEventRepository.deleteOfflineEvent(offlineEvent);
                        continue;
                    }

                    if (offlineEvent.isSignIn()) {
                        doSignIn(offlineEvent, task);
                    } else if (offlineEvent.isSignOut()) {
                        doSignOut(offlineEvent, task);
                    }
                } else {
                    TaskActivity taskActivity = taskRepository.getActivityByRowID(offlineEvent.getActivityId());
                    if (taskActivity == null) {
                        offlineEventRepository.deleteOfflineEvent(offlineEvent);
                        continue;
                    }
                    this.doUpdateActivity(offlineEvent, taskActivity);*/
                }

            }

            // check if any more offline events came in during processing, if not set state as connected
            offlineEvents = offlineEventRepository.getAllOfflineEvent();
            if (offlineEvents == null || offlineEvents.isEmpty()) {

                // if device has been offline for more than configured allowance run bulk refresh.
                long minutesOffline = networkStateProvider.getDeviceOfflineMinutes();
               /* Csc csc = cscProvider.getMostRecentUpdatedCsc();
                if (csc != null) {
                    Integer offlineBulkRefreshMinutes = csc.getOfflineBulkRefreshMinutes();
                    if (offlineBulkRefreshMinutes < minutesOffline) {
                        TaskBulkUpdateService.start(getApplicationContext());
                    }
                }
*/
                networkStateProvider.setDeviceConnected();

            }

        } catch (Exception e) {
            LogUtils.LOGE(TAG, "Error synchronizing offline data.", e);
            //if one of the events fails processing must not continue until that event can be processed. so exit the service
            stopSelf();
        }

    }

}
