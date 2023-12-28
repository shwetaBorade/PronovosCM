package com.pronovoscm.utils.ui;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import androidx.annotation.RequiresApi;

public class LoadTextFilesInBackground extends AsyncTask<Object, Void, Object[]> {
    private Listener listener;
    private BitmapFactory.Options options = new BitmapFactory.Options();

    public LoadTextFilesInBackground(final Listener listener) {
        this.listener = listener;
    }


    @Override
    protected Object[] doInBackground(Object... urls) {
        // Logic to download an image from an URL
        final String urlStr = (String) urls[0];
        final File file = (File) urls[1];
        String sBody = "";
        try {
            URL url = new URL(urlStr); //My text file location
            //First open the connection
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(150000);

            BufferedInputStream in = new BufferedInputStream(conn.getInputStream());
            int bytesRead = 0;
            byte[] contents = new byte[1024];
            while ((bytesRead = in.read(contents)) != -1) {
                sBody += new String(contents, 0, bytesRead);
            }
            FileWriter writer = new FileWriter(file);
            writer.write(sBody);
            writer.flush();
            writer.close();
            in.close();
        } catch (Exception e) {
            Log.d("MyTag", e.toString());
        }
        return urls;
    }

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onPostExecute(Object[] result) {
        File from = (File) result[1];
        File myDir = new File(((Context) result[3]).getFilesDir().getAbsolutePath() + "/Pronovos/");//"/PronovosPronovos"
        if (!myDir.exists()) {
            myDir.mkdirs();
        }
        File to = new File(myDir, (String) result[2]);
        from.renameTo(to);
        listener.onImageDownloaded(true);
    }


    public interface Listener {
        void onImageDownloaded(Boolean bitmap);

        void onImageDownloadError();
    }
}
