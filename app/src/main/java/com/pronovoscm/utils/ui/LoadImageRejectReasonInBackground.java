package com.pronovoscm.utils.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.pronovoscm.utils.dialogs.AttachmentDialog;
import com.pronovoscm.utils.dialogs.RejectReasonAttachmentDialog;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;


public class LoadImageRejectReasonInBackground extends AsyncTask<Object, Void, Object[]> {
    private static final String TAG = "LoadImageInBackground";
    private Listener listener;
    private BitmapFactory.Options options = new BitmapFactory.Options();

    public LoadImageRejectReasonInBackground(final Listener listener) {
        this.listener = listener;
    }


    @SuppressLint("CheckResult")
    @Override
    protected Object[] doInBackground(Object... urls) {
        // Logic to download an image from an URL

        /*final String url = urls[0];
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
        }*/
        Object[] obj = new Object[4];
        obj[0] = urls[2];
        obj[1] = urls[0];
        obj[2] = urls[1];
        obj[3] = urls[3];
        return obj;
    }

    @SuppressLint("CheckResult")
    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onPostExecute(Object[] result) {
        // Download is done
        //  Log.i(TAG, "onImageDownloaded: image download onpost ");
        Log.i(TAG, "onPostExecute:Context "+result[0]);
        Log.i(TAG, "onPostExecute: url"+result[1]);
        if(result[3] != null){
            Glide.with((Context) result[0]).load(result[1]).listener(new RequestListener<Drawable>() {

                @Override
                public boolean onLoadFailed(@Nullable @org.jetbrains.annotations.Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    Log.i(TAG, "onPostExecute:resource "+resource);
                    Bitmap bitmap = RejectReasonAttachmentDialog.drawableToBitmap(resource);
                    if (null != bitmap) {

                        listener.onImageDownloaded(bitmap);
                        AsyncTask.execute(() -> {
                            URI uri = null;
                            try {
                                uri = new URI((String) result[1]);
                                String[] segments = uri.getPath().split("/");
                                String imageName = segments[segments.length - 1];
                                //  Log.i(TAG, "onImageDownloaded: image download a ");
                                saveImageInStorage(bitmap, imageName, (String) result[2]);
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

                    return false;
                }
            }).into((ImageView) result[3]);
        }else {
            imageLoad(result);
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

    private void imageLoad(Object[] result) {
        Glide.with((Context) result[0]).load(result[1]).listener(new RequestListener<Drawable>() {

            @Override
            public boolean onLoadFailed(@Nullable @org.jetbrains.annotations.Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                Log.i(TAG, "onPostExecute:resource "+resource);
                Bitmap bitmap = RejectReasonAttachmentDialog.drawableToBitmap(resource);
                if (null != bitmap) {

                    listener.onImageDownloaded(bitmap);
                    AsyncTask.execute(() -> {
                        URI uri = null;
                        try {
                            uri = new URI((String) result[1]);
                            String[] segments = uri.getPath().split("/");
                            String imageName = segments[segments.length - 1];
                            //  Log.i(TAG, "onImageDownloaded: image download a ");
                            saveImageInStorage(bitmap, imageName, (String) result[2]);
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

                return false;
            }
        }).submit();
    }

    public interface Listener {
        void onImageDownloaded(final Bitmap bitmap);

        void onImageDownloadError();
    }
}
