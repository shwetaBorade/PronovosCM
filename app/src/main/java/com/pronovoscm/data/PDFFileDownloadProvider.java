package com.pronovoscm.data;

import android.graphics.Bitmap;
import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.pronovoscm.PronovosApplication;
import com.pronovoscm.api.PDFFileDownloadAPI;
import com.pronovoscm.persistence.domain.DrawingList;
import com.pronovoscm.services.NetworkService;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class PDFFileDownloadProvider {

    private PronovosApplication pronovosApplication;

    public PDFFileDownloadProvider(PronovosApplication pronovosApplication) {
        this.pronovosApplication = pronovosApplication;
    }

    public void getDrawingPDF(final DrawingList drawingList, int usersId, final ProviderResult<Boolean> providerResult) {

        if (NetworkService.isNetworkAvailable(pronovosApplication)) {
            if (drawingList.getPdfOrg() != null && !TextUtils.isEmpty(drawingList.getPdfOrg())) {

                try {
                    URL url = new URL(drawingList.getPdfOrg());
                    String baseUrl = url.getProtocol() + "://" + url.getHost();
                    String[] segments = url.getPath().split("/");
                    String fileName = segments[segments.length - 1];
                    url.getPath();

                    HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
                    //interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
                    OkHttpClient client = new OkHttpClient.Builder().readTimeout(200, TimeUnit.SECONDS)
                            .connectTimeout(60, TimeUnit.SECONDS).addInterceptor(interceptor).build();
                    // Set header for API call
                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(baseUrl).client(client)
                            .build();
                    PDFFileDownloadAPI service = retrofit.create(PDFFileDownloadAPI.class);
                    Call<ResponseBody> call = service.getDrawingPDF(url.getPath());

                    call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            if (response.isSuccessful()) {
                                boolean writtenToDisk = writeResponseBodyToDisk(response.body(), usersId + fileName);
                                        providerResult.success(writtenToDisk);

                            } else {
                                providerResult.failure("");
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {

                            if (NetworkService.isNetworkAvailable(pronovosApplication)) {
                                providerResult.failure(t.getMessage());
                            }else{

                                providerResult.failure("");
                            }
                        }
                    });
                } catch (MalformedURLException e) {
                    providerResult.failure("");
                }
            } else {
                try {
                    URL url = new URL(drawingList.getImageOrg());
                    String baseUrl = url.getProtocol() + "://" + url.getHost();
                    String[] segments = url.getPath().split("/");
                    String fileName = segments[segments.length - 1];
                    Glide.with(pronovosApplication)
                            .asBitmap().load(drawingList.getImageOrg())
                            .listener(new RequestListener<Bitmap>() {
                                          @Override
                                          public boolean onLoadFailed(@Nullable GlideException e, Object o, Target<Bitmap> target, boolean b) {
                                              providerResult.failure("");
                                              return false;
                                          }

                                          @Override
                                          public boolean onResourceReady(Bitmap bitmap, Object o, Target<Bitmap> target, DataSource dataSource, boolean b) {

                                              boolean writtenToDisk = SaveImageInStorage(bitmap, usersId + fileName);
                                              providerResult.success(writtenToDisk);
                                              return writtenToDisk;
                                          }
                                      }
                            ).submit();
                } catch (IllegalArgumentException e) {
                    providerResult.failure("");
                    e.printStackTrace();
                } catch (MalformedURLException e) {
                    providerResult.failure("");
                }
            }
        }
    }

    private boolean writeResponseBodyToDisk(ResponseBody body, String fileName) {
        try {

            File myDir = new File(pronovosApplication.getFilesDir() + "/Pronovos/PDF/");//"/Pronovos"
            if (!myDir.exists()) {
                myDir.mkdirs();
            }
            File futureStudioIconFile = new File(pronovosApplication.getFilesDir() + "/Pronovos/PDF/" + fileName);

            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {
                byte[] fileReader = new byte[4096];

                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;

                inputStream = body.byteStream();
                if (futureStudioIconFile.exists()) {
                    futureStudioIconFile.createNewFile();
                }
                outputStream = new FileOutputStream(futureStudioIconFile);
                while (true) {
                    int read = inputStream.read(fileReader);

                    if (read == -1) {
                        break;
                    }

                    outputStream.write(fileReader, 0, read);

                    fileSizeDownloaded += read;

                    // Log.d("File download", "file download: " + fileSizeDownloaded + " of " + fileSize);
                }

                outputStream.flush();

                return true;
            } catch (IOException e) {
                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            return false;
        }
    }


    /**
     * @param finalBitmap
     * @param fileName
     * @return
     */
    private boolean SaveImageInStorage(Bitmap finalBitmap, String fileName) {

//                  Bitmap bmImg = BitmapFactory.decodeFile(file.getAbsolutePath());
//                  imageView.setImageBitmap(bmImg);

//        File myDir = new File(Environment.getExternalStorageDirectory() + "/Pronovos/PDF");//"/Pronovos"
        File myDir = new File(pronovosApplication.getFilesDir() + "/Pronovos/PDF");//"/Pronovos"
        if (!myDir.exists()) {
            myDir.mkdirs();
        }
        String fname = fileName;
        File file = new File(myDir, fname);
        if (file.exists()) {
            file.delete();
        }

        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
            return true;

        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return false;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }


    }
}
