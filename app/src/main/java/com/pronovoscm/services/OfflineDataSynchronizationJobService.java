package com.pronovoscm.services;

import android.annotation.TargetApi;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.os.Build;

import com.pronovoscm.utils.LogUtils;

/**
 * An Intent service to synchronized all offline event data to server.
 *
 * @author Nitin Bhawsar
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class OfflineDataSynchronizationJobService extends JobService {

    private static final String TAG = LogUtils.makeLogTag(OfflineDataSynchronizationJobService.class);

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        LogUtils.LOGD(TAG, "Start job service" + jobParameters.getJobId());
        Intent service = new Intent(getApplicationContext(), OfflineDataSynchronizationService.class);
        getApplicationContext().startService(service);
//        JobUtil.scheduleJob(getApplicationContext());
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        LogUtils.LOGD(TAG, "Stop job services " + jobParameters.getJobId());
        return true;
    }


}
