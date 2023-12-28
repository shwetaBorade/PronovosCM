package com.pronovoscm.utils.backupsync;


import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import com.pronovoscm.persistence.domain.BackupSyncImageFiles;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class CheckAWSFileInbackgraound extends AsyncTask<String, Void, Object[]> {
    private static final String TAG = "CheckAWSFileInBG";
    SyncOldFileUtil.AWSFileListener listener;
    BackupSyncImageFiles backupSyncImageFiles;

    public CheckAWSFileInbackgraound(SyncOldFileUtil.AWSFileListener listener, BackupSyncImageFiles backupSync) {
        this.listener = listener;
        this.backupSyncImageFiles = backupSync;
    }

    @Override
    protected Object[] doInBackground(String... urlsParam) {
        Bitmap bitmap = null;
        final String url = backupSyncImageFiles.getLocation();
        ;
        InputStream in = null;
        try {
            Log.i(TAG, "onImageDownloaded: image download a1 " + url);
            URL url1 = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) url1.openConnection();
            connection.connect();
            in = connection.getInputStream();
            // File f = new File("temp");
        } catch (final MalformedURLException malformedUrlException) {
            // Handle error
            //   Log.e(TAG, "doInBackground: MalformedURLException");
        } catch (final IOException ioException) {
            // Handle error
            Log.e(TAG, "file not exist doInBackground:IOException ");
        }
        Object[] obj = new Object[3];
        obj[0] = in;
        obj[1] = url;

        return obj;
    }

    @Override
    protected void onPostExecute(Object[] result) {
        super.onPostExecute(result);
        if (null != result[0]) {
            Log.e(TAG, "onPostExecute: file exist ");

            listener.onFileExist(backupSyncImageFiles);
            //TODO need to remove
            //listener.onFileDownloadError(backupSyncImageFiles);
        } else {
            listener.onFileDownloadError(backupSyncImageFiles);
        }
    }
}
