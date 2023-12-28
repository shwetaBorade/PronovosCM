package com.pronovoscm.utils.ui;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;


public class LoadImageInBackground extends AsyncTask<String, Void, Object[]> {
    private static final String TAG = "LoadImageInBackground";
    private Listener listener;
    private BitmapFactory.Options options = new BitmapFactory.Options();

    public LoadImageInBackground(final Listener listener) {
        this.listener = listener;
    }


    @Override
    protected Object[] doInBackground(String... urls) {
        // Logic to download an image from an URL
        final String url = urls[0];
        Log.i(TAG, "onImageDownloaded: image download on background 1 " + url);
        Bitmap bitmap = null;
        try {
             Log.i(TAG, "onImageDownloaded: image download a1 " + url);
            URL url1 = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) url1.openConnection();
            connection.connect();
            InputStream in = connection.getInputStream();

            try {
                bitmap = BitmapFactory.decodeStream(in, null, options);
            } catch (OutOfMemoryError outOfMemoryError) {
                Log.e(TAG, "&&&&& doInBackground: OutOfMemoryError" + outOfMemoryError.getMessage());
            }

        } catch (final MalformedURLException malformedUrlException) {
            // Handle error
            Log.e(TAG, "&&&&& doInBackground: MalformedURLException");
        } catch (final IOException ioException) {
            // Handle error
            Log.e(TAG, "************ doInBackground:IOException "+ ioException.getMessage());
        }
        Object[] obj = new Object[3];
        obj[0] = bitmap;
        obj[1] = url;
        obj[2] = urls[1];
        return obj;
    }

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onPostExecute(Object[] result) {
        // Download is done
        //  Log.i(TAG, "onImageDownloaded: image download onpost ");

        if (null != result[0]) {

            listener.onImageDownloaded((Bitmap) result[0]);
            AsyncTask.execute(() -> {
                URI uri = null;
                try {
                    uri = new URI((String) result[1]);
                    String[] segments = uri.getPath().split("/");
                    String imageName = segments[segments.length - 1];
                    //  Log.i(TAG, "onImageDownloaded: image download a ");
                    saveImageInStorage((Bitmap) result[0], imageName, (String) result[2]);
                    //    Log.i(TAG, "onImageDownloaded: image download b ");
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                    listener.onImageDownloadError();
                }

            });

        } else {
            Log.d(TAG, " error in download %%%%% onPostExecute: ");
            listener.onImageDownloadError();
        }
    }

    /**
     * Save Image in storage and show it on the screen
     *
     * @param finalBitmap
     * @param fileName
     * @return
     */
    public void saveImageInStorage(Bitmap finalBitmap, String fileName, String path) {
        File myDir = new File(path);
        if (!myDir.exists()) {
            myDir.mkdirs();
        }
        String fname = "_temp" + fileName;

        File file = new File(myDir, fname);
        if (file.exists()) {
            file.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.WEBP, 90, out);
            out.flush();
            out.close();
            File from = new File(myDir, fname);
            File to = new File(myDir, fileName);
          /*  if (to.exists()) {
                to.delete();
            }*/
            from.renameTo(to);

        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public interface Listener {
        void onImageDownloaded(final Bitmap bitmap);

        void onImageDownloadError();
    }
}
