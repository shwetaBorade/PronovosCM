package com.pronovoscm.ui.punchlist.adapter;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.pronovoscm.R;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class OpenImageFrmoServer extends DialogFragment {

    ProgressDialog mProgressDialog;
    ImageView mImageView;
    URL url;
    AsyncTask mMyTask;

    public static OpenImageFrmoServer newInstance(String title, String imagePath) {
        OpenImageFrmoServer frag = new OpenImageFrmoServer();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("image_path", imagePath);
        frag.setArguments(args);
        return frag;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_fragment_open_image, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get field from view
        mImageView =view.findViewById(R.id.imageView);
        mProgressDialog = new ProgressDialog(view.getContext());
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setTitle("AsyncTask");
        mProgressDialog.setMessage("Please wait, we are downloading your image file...");
        String title = getArguments().getString("title", "Enter Name");
        String imagePath = getArguments().getString("image_path", "Enter Name");
        getDialog().setTitle(title);
        mMyTask = new DownloadTask().execute(stringToURL(imagePath));
    }

    private class DownloadTask extends AsyncTask<URL,Void,Bitmap>{
        protected void onPreExecute(){
            mProgressDialog.show();
        }
        protected Bitmap doInBackground(URL...urls){
            URL url = urls[0];
            HttpURLConnection connection = null;
            try{
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream inputStream = connection.getInputStream();
                BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
                return BitmapFactory.decodeStream(bufferedInputStream);
            }catch(IOException e){
                e.printStackTrace();
            }
            return null;
        }
        // When all async task done
        protected void onPostExecute(Bitmap result){
            // Hide the progress dialog
            mProgressDialog.dismiss();
            if(result!=null){
                mImageView.setImageBitmap(result);
            } else {
                // Notify user that an error occurred while downloading image
                Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
            }
        }
    }
    protected URL stringToURL(String path) {
        try {
//            url = new URL("https://wallpapersite.com/images/pages/pic_w/6408.jpg");
            url = new URL(path);
            return url;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
