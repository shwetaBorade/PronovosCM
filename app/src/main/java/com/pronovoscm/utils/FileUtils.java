package com.pronovoscm.utils;

import android.content.Context;
import android.net.Uri;

import androidx.core.content.FileProvider;

import android.os.Environment;
import android.util.Log;

import com.google.gson.Gson;
import com.pronovoscm.BuildConfig;
import com.pronovoscm.LogData;
import com.pronovoscm.PronovosApplication;
import com.pronovoscm.R;
import com.pronovoscm.model.response.login.LoginResponse;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;

/**
 * Created on 16/11/18.
 *
 * @author Sanjay Kushwah
 */
public class FileUtils {
    private static final String TAG = FileUtils.class.getSimpleName();

    public static File getOutputMediaFile(int type) {

        // External sdcard location
        File mediaStorageDir = new File(
                PronovosApplication.getContext().getFilesDir().getAbsolutePath() + "/Pronovos/");


        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(TAG, "Oops! Failed create "
                        + "Attachments" + " directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "IMG_" + timeStamp + ".jpg");
        } else {
            return null;
        }

        return mediaFile;
    }

    public static File getOutputGalleryMediaFile(int type, Context context) {

        // External sdcard location
        File mediaStorageDir = new File(
                PronovosApplication.getContext().getFilesDir().getAbsolutePath() + "/Pronovos/");
        LoginResponse loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        String suffix = loginResponse.getUserDetails().getAuthtoken().substring(loginResponse.getUserDetails().getAuthtoken().length() - 3);
        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(TAG, "Oops! Failed create "
                        + "Attachments" + " directory");
                return null;
            }
        }

        // Create a media file name
        File mediaFile;
        String fileName = String.valueOf(DateFormatter.currentTimeMillisLocal());
        if (type == MEDIA_TYPE_IMAGE) {
            File folder = new File(context.getFilesDir().getAbsolutePath() + "/Pronovos/");
            mediaFile = new File(folder.getAbsolutePath() + File.separator
                    + fileName + suffix + ".jpg");
        } else {
            return null;
        }

        return mediaFile;
    }


    public static Uri getOutputMediaFileUri(int type) {
//        return Uri.fromFile(getOutputMediaFile(type));
        return FileProvider.getUriForFile(PronovosApplication.getContext(), BuildConfig.APPLICATION_ID + ".provider", getOutputMediaFile(type));
    }


    public static byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }


    /**
     * copies content from source file to destination file
     *
     * @param sourceFile
     * @param destFile
     * @throws IOException
     */
    public static void copyFile(File sourceFile, File destFile) throws IOException {
        if (!sourceFile.exists()) {
            return;
        }

        FileChannel source = null;
        FileChannel destination = null;
        source = new FileInputStream(sourceFile).getChannel();
        destination = new FileOutputStream(destFile).getChannel();
        if (destination != null && source != null) {
            destination.transferFrom(source, 0, source.size());
        }
        if (source != null) {
            source.close();
        }
        if (destination != null) {
            destination.close();
        }

    }

    public static void writeStreamToFile(InputStream input, File file) {
        try {
            try (OutputStream output = new FileOutputStream(file)) {
                byte[] buffer = new byte[4 * 1024]; // or other buffer size
                int read;
                while ((read = input.read(buffer)) != -1) {
                    output.write(buffer, 0, read);
                }
                output.flush();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                input.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static int getFileImage(String type) {
        if (type.equalsIgnoreCase("zip")) {
            return R.drawable.ic_zip;
        } else if (type.equalsIgnoreCase("pdf")) {
            return R.drawable.ic_file_pdf;
        } else if (type.equalsIgnoreCase("xls")) {
            return R.drawable.ic_file_xls;
        } else if (type.equalsIgnoreCase("xlsm")) {
            return R.drawable.ic_file_xlsm;
        } else if (type.equalsIgnoreCase("xlsx")) {
            return R.drawable.ic_file_xlsx;
        } else if (type.equalsIgnoreCase("doc")) {
            return R.drawable.ic_doc;
        } else if (type.equalsIgnoreCase("docx")) {
            return R.drawable.ic_file_docx;
        } else if (type.equalsIgnoreCase("docm")) {
            return R.drawable.ic_docm;
        } else if (type.equalsIgnoreCase("ppt")) {
            return R.drawable.ic_file_ppt;
        } else if (type.equalsIgnoreCase("pptx")) {
            return R.drawable.ic_pptx;
        } else if (type.equalsIgnoreCase("mp4")) {
            return R.drawable.ic_file_audio;
        } else if (type.equalsIgnoreCase("jpeg")) {
            return R.drawable.ic_file_jpeg;
        } else if (type.equalsIgnoreCase("jpg")) {
            return R.drawable.ic_file_jpeg;
        } else if (type.equalsIgnoreCase("png")) {
            return R.drawable.ic_png;
        } else if (type.equalsIgnoreCase("txt")) {
            return R.drawable.ic_file_txt;
        } else if (type.equalsIgnoreCase("mpp")) {
            return R.drawable.ic_mpp;
        } else if (type.equalsIgnoreCase("rvt")) {
            return R.drawable.ic_file_type_rvt;
        } else if (type.equalsIgnoreCase("msg")) {
            return R.drawable.ic_file_type_msg;
        } else if (type.equalsIgnoreCase("dwg")) {
            return R.drawable.ic_file_type_dwg;
        } else {
            return R.drawable.ic_file_txt;
        }
    }

    public static void createFolderAndFile(LogData logData) {
        // Check if external storage is available
        if (isExternalStorageWritable()) {
            // Get the Downloads directory
            File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

            String FOLDER_NAME = "Drawings";
            // Create a new folder inside Downloads
            File newFolder = new File(downloadsDir, FOLDER_NAME);
            if (!newFolder.exists()) {
                newFolder.mkdirs(); // Create the directory if it doesn't exist
            }

            // Create a file inside the new folder and write some dummy text
            if (newFolder.list()!=null && newFolder.list().length == 0) {
                String FILE_NAME = "BugReport.txt";
                File file = new File(newFolder, FILE_NAME);
                if (logData != null)
                    try {
                        FileOutputStream fos = new FileOutputStream(file);
                        String dummyText = logData.getUrl() + "\n" + logData.getRequest() + "\n" + logData.getResponse();
                        fos.write(dummyText.getBytes());
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
            } else {
                if (logData != null)
                    try {
                        appendDataToFile(String.valueOf(newFolder.listFiles()[0].getAbsoluteFile()), logData.getUrl() + "\n" + logData.getRequest() + "\n" + logData.getResponse());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
            }
        } else {
            // External storage is not available, handle this case as needed
        }
    }

    private static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    public static void deleteFile() {
        File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        String FOLDER_NAME = "Drawings";
        String FILE_NAME = "BugReport.txt";
        File fileToDelete = new File(downloadsDir + "/" + FOLDER_NAME + "/" + FILE_NAME);

        if (fileToDelete.exists()) {
            if (fileToDelete.delete()) {
                // File was successfully deleted
                Log.e("file deleted", "file deleted");
            } else {
                // Failed to delete the file
                Log.e("file deleted", "file deleted");
            }
        } else {
            // File does not exist
        }
    }

    public static void appendDataToFile(String filePath, String dataToAppend) {
        try {
            // Open the file in append mode
            FileWriter writer = new FileWriter(filePath, true);
            // Append the data
            writer.write(dataToAppend);
            // Close the file
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getFileName() {
        String FOLDER_NAME = "Drawings";
        String FILE_NAME = "BugReport.txt";
        File filePath = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                + "/" + FOLDER_NAME + "/" + FILE_NAME);
        return filePath.getAbsolutePath();
    }
}
