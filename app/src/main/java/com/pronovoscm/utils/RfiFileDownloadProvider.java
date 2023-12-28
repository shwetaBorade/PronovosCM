package com.pronovoscm.utils;

import android.text.TextUtils;

import com.pronovoscm.PronovosApplication;
import com.pronovoscm.api.PDFFileDownloadAPI;
import com.pronovoscm.data.ProviderResult;
import com.pronovoscm.model.PDFSynEnum;
import com.pronovoscm.persistence.domain.PjRfiAttachments;
import com.pronovoscm.persistence.repository.ProjectRfiRepository;
import com.pronovoscm.services.NetworkService;

import java.io.File;
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

public class RfiFileDownloadProvider {

    ProjectRfiRepository projectDocumentsRepository;
    private PronovosApplication pronovosApplication;

    public RfiFileDownloadProvider(PronovosApplication pronovosApplication, ProjectRfiRepository projectDocumentsRepository) {
        this.pronovosApplication = pronovosApplication;
        this.projectDocumentsRepository = projectDocumentsRepository;
    }

    public void getFileFromServer(final String location, PjRfiAttachments docFile, final ProviderResult<Boolean> providerResult) {

        if (NetworkService.isNetworkAvailable(pronovosApplication)) {
            if (!TextUtils.isEmpty(location)) {
                try {
                    URL url = new URL(location);
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
                                boolean writtenToDisk = writeResponseBodyToDisk(response.body(), fileName);
                                providerResult.success(writtenToDisk);
                            } else {
                                docFile.setFileStatus(PDFSynEnum.SYNC_FAILED.ordinal());
                                projectDocumentsRepository.updatePjRfiAttachments(docFile);
                                providerResult.failure("");
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            docFile.setFileStatus(PDFSynEnum.SYNC_FAILED.ordinal());
                            projectDocumentsRepository.updatePjRfiAttachments(docFile);
                            if (NetworkService.isNetworkAvailable(pronovosApplication)) {
                                providerResult.failure(t.getMessage());
                            } else {
                                providerResult.failure("");
                            }
                        }
                    });
                } catch (MalformedURLException e) {
                    providerResult.failure("");
                }
            }
        }
    }

    private boolean writeResponseBodyToDisk(ResponseBody body, String fileName) {
        try {

            File myDir = new File(pronovosApplication.getFilesDir() + Constants.DOCUMENT_FILES_PATH);//"/Pronovos"
            if (!myDir.exists()) {
                myDir.mkdirs();
            }
            File futureStudioIconFile = new File(pronovosApplication.getFilesDir() + Constants.RFI_ATTACHMENTS_PATH + fileName);

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


}
