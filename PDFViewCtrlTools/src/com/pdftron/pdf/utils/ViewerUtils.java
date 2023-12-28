//---------------------------------------------------------------------------------------
// Copyright (c) 2001-2019 by PDFTron Systems Inc. All Rights Reserved.
// Consult legal.txt regarding legal and license information.
//---------------------------------------------------------------------------------------

package com.pdftron.pdf.utils;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.FloatEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.ParcelFileDescriptor;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.pdftron.common.PDFNetException;
import com.pdftron.fdf.FDFDoc;
import com.pdftron.filters.FilterReader;
import com.pdftron.filters.FilterWriter;
import com.pdftron.filters.SecondaryFileFilter;
import com.pdftron.pdf.Annot;
import com.pdftron.pdf.Convert;
import com.pdftron.pdf.DocumentConversion;
import com.pdftron.pdf.Field;
import com.pdftron.pdf.PDFDoc;
import com.pdftron.pdf.PDFViewCtrl;
import com.pdftron.pdf.Page;
import com.pdftron.pdf.PageSet;
import com.pdftron.pdf.Rect;
import com.pdftron.pdf.TextExtractor;
import com.pdftron.pdf.WordToPDFOptions;
import com.pdftron.pdf.annots.FileAttachment;
import com.pdftron.pdf.annots.Widget;
import com.pdftron.pdf.dialog.PortfolioDialogFragment;
import com.pdftron.pdf.dialog.menueditor.model.MenuEditorItem;
import com.pdftron.pdf.dialog.menueditor.model.MenuEditorItemContent;
import com.pdftron.pdf.dialog.menueditor.model.MenuEditorItemHeader;
import com.pdftron.pdf.dialog.signature.CreateSignatureFragment;
import com.pdftron.pdf.model.ExternalFileInfo;
import com.pdftron.pdf.tools.FileAttachmentCreate;
import com.pdftron.pdf.tools.R;
import com.pdftron.pdf.tools.Signature;
import com.pdftron.pdf.tools.Stamper;
import com.pdftron.pdf.tools.ToolManager;
import com.pdftron.sdf.NameTree;
import com.pdftron.sdf.NameTreeIterator;
import com.pdftron.sdf.SDFDoc;
import com.pdftron.sdf.SecurityHandler;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;

/**
 * A utility class for pdf viewer
 */
public class ViewerUtils {

    // image intent
    private static final String IMAGE_INTENT_IS_CAMERA = "camera";
    private static final String IMAGE_INTENT_URI = "uri";
    private static final String IMAGE_INTENT_PATH = "path";

    /**
     * Checks whether the PDFViewCtrl is zoomed.
     *
     * @param pdfViewCtrl The PDFViewCtrl
     * @return True if the PDFViewCtrl is zoomed
     */
    public static boolean isViewerZoomed(PDFViewCtrl pdfViewCtrl) {
        final PDFViewCtrl.PageViewMode refMode;
        if (pdfViewCtrl.isMaintainZoomEnabled()) {
            refMode = pdfViewCtrl.getPreferredViewMode();
        } else {
            refMode = pdfViewCtrl.getPageRefViewMode();
        }
        // If page view mode is zoom, then we cannot determine if it is zoomed so just return false
        if (refMode == PDFViewCtrl.PageViewMode.ZOOM) {
            return false;
        }
        double zoom = pdfViewCtrl.getZoom();
        double refZoom = 0;
        try {
            refZoom = pdfViewCtrl.getZoomForViewMode(refMode);
        } catch (PDFNetException ex) {
            Log.v("Tool", ex.getMessage());
        }
        boolean zoomed = true;
        if (refZoom > 0 && zoom > 0) {
            double zoomFactor = refZoom / zoom;
            if (zoomFactor > 0.95 && zoomFactor < 1.05) {
                zoomed = false;
            }
        }
        return zoomed;
    }

    /**
     * Send a generic file picker intent
     *
     * @param activity the activity
     */
    public static void openFileIntent(@NonNull Activity activity) {
        openFileIntent(activity, null);
    }

    /**
     * Send a generic file picker intent
     *
     * @param fragment the fragment
     */
    public static void openFileIntent(@NonNull Fragment fragment) {
        openFileIntent(null, fragment);
    }

    /**
     * Send a generic file picker intent
     *
     * @param activity the activity
     * @param fragment the fragment
     */
    public static void openFileIntent(Activity activity, Fragment fragment) {
        if (!Utils.isKitKat()) {
            return;
        }
        if (activity == null && fragment == null) {
            return;
        }
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");

        if (fragment != null) {
            fragment.startActivityForResult(intent, RequestCode.SELECT_FILE);
        } else {
            activity.startActivityForResult(intent, RequestCode.SELECT_FILE);
        }
    }

    /**
     * Send a generic file creation intent
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static Intent getFileIntent(String fileName, String mimeType) {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        // Force advanced devices (SD cards) to always be visible
        intent.putExtra("android.content.extra.SHOW_ADVANCED", true);
        // Reference: https://android.googlesource.com/platform/frameworks/base/+/master/core/java/android/provider/DocumentsContract.java#109
        intent.putExtra("android.provider.extra.SHOW_ADVANCED", true);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType(mimeType);
        intent.putExtra(Intent.EXTRA_TITLE, fileName);
        return intent;
    }

    /**
     * Send a generic file creation intent
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void createFileIntent(Activity activity, Fragment fragment, String fileName, String mimeType) {
        if (!Utils.isLollipop()) {
            return;
        }
        if (activity == null && fragment == null) {
            return;
        }
        Intent intent = getFileIntent(fileName, mimeType);
        if (fragment != null) {
            fragment.startActivityForResult(intent, RequestCode.CREATE_FILE_IN_SYSTEM);
        } else {
            activity.startActivityForResult(intent, RequestCode.CREATE_FILE_IN_SYSTEM);
        }
    }

    public static void createFileAttachment(Activity activity, Intent data, PDFViewCtrl pdfViewCtrl, PointF targetPoint) {
        if (activity == null || activity.getContentResolver() == null || data == null || data.getData() == null || targetPoint == null) {
            return;
        }
        InputStream fis = null;
        OutputStream fos = null;
        String tempPath = null;
        try {
            Uri fileUri = data.getData();
            String extension = Utils.getUriExtension(activity.getContentResolver(), fileUri);
            String name = Utils.getUriDisplayName(activity, fileUri);
            if (Utils.isNullOrEmpty(extension)) {
                return;
            }
            if (Utils.isNullOrEmpty(name)) {
                name = "untitled" + extension;
            }

            // create temp file
            fis = activity.getContentResolver().openInputStream(fileUri);
            if (fis != null) {
                tempPath = activity.getFilesDir() + "/" + name;
                tempPath = Utils.getFileNameNotInUse(tempPath);
                fos = new FileOutputStream(new File(tempPath));
                IOUtils.copy(fis, fos);

                FileAttachmentCreate tool;
                if (((ToolManager) pdfViewCtrl.getToolManager()).getTool().getToolMode() != ToolManager.ToolMode.FILE_ATTACHMENT_CREATE) {
                    tool = (FileAttachmentCreate) ((ToolManager) pdfViewCtrl.getToolManager()).createTool(ToolManager.ToolMode.FILE_ATTACHMENT_CREATE, null);
                    ((ToolManager) pdfViewCtrl.getToolManager()).setTool(tool);
                } else {
                    tool = (FileAttachmentCreate) ((ToolManager) pdfViewCtrl.getToolManager()).getTool();
                }
                tool.setTargetPoint(targetPoint, false);
                int pageNum = pdfViewCtrl.getPageNumberFromScreenPt(targetPoint.x, targetPoint.y);
                boolean success = tool.createFileAttachment(targetPoint, pageNum, tempPath);
                if (!success) {
                    CommonToast.showText(activity, activity.getString(R.string.attach_file_error), Toast.LENGTH_SHORT);
                }
            }
        } catch (FileNotFoundException e) {
            CommonToast.showText(activity, activity.getString(R.string.image_stamper_file_not_found_error), Toast.LENGTH_SHORT);
            AnalyticsHandlerAdapter.getInstance().sendException(e);
        } catch (Exception e) {
            CommonToast.showText(activity, activity.getString(R.string.attach_file_error), Toast.LENGTH_SHORT);
            AnalyticsHandlerAdapter.getInstance().sendException(e);
        } finally {
            Utils.closeQuietly(fis);
            Utils.closeQuietly(fos);
            if (tempPath != null) {
                File tempFile = new File(tempPath);
                if (tempFile.exists()) {
                    tempFile.delete();
                }
            }
        }
    }

    /**
     * @param activity the activity in which you can expect onActivityResult will be called
     *                 with request code {@link RequestCode#PICK_PHOTO_CAM}
     * @return the output file Uri
     */
    public static Uri openImageIntent(@NonNull Activity activity) {
        return openImageIntent(activity, null, RequestCode.PICK_PHOTO_CAM);
    }

    /**
     * @param activity    the activity in which you can expect onActivityResult will be called
     *                    with given request code
     * @param requestCode the request code to returned in onActivityResult
     * @return the output file Uri
     */
    public static Uri openImageIntent(@NonNull Activity activity, int requestCode) {
        return openImageIntent(activity, null, requestCode);
    }

    /**
     * @param fragment the fragment in which you can expect onActivityResult will be called
     *                 with request code {@link RequestCode#PICK_PHOTO_CAM}
     * @return the output file Uri
     */
    public static Uri openImageIntent(@NonNull Fragment fragment) {
        return openImageIntent(null, fragment, RequestCode.PICK_PHOTO_CAM);
    }

    /**
     * @param fragment    the fragment in which you can expect onActivityResult will be called
     *                    with given request code
     * @param requestCode the request code to returned in onActivityResult
     * @return the output file Uri
     */
    public static Uri openImageIntent(@NonNull Fragment fragment, int requestCode) {
        return openImageIntent(null, fragment, requestCode);
    }

    /**
     * @param activity    the activity in which you can expect onActivityResult will be called
     *                    with request code {@link RequestCode#PICK_PHOTO_CAM}
     * @param fragment    the activity in which you can expect onActivityResult will be called
     *                    with request code {@link RequestCode#PICK_PHOTO_CAM}
     * @param requestCode the request code to returned in onActivityResult
     * @return the output file Uri
     * @hide
     */
    private static Uri openImageIntent(@Nullable Activity activity, @Nullable Fragment fragment, int requestCode) {
        if (null == activity && null == fragment) {
            return null;
        }
        Activity activityContext;
        if (activity != null) {
            activityContext = activity;
        } else {
            activityContext = fragment.getActivity();
        }
        if (null == activityContext) {
            return null;
        }
        //noinspection ConstantConditions
        return openImageIntent(activityContext, fragment, activityContext.getExternalCacheDir(), requestCode);
    }

    /**
     * @param activity    the activity in which you can expect onActivityResult will be called
     *                    with request code {@link RequestCode#PICK_PHOTO_CAM}
     * @param fragment    the activity in which you can expect onActivityResult will be called
     *                    with request code {@link RequestCode#PICK_PHOTO_CAM}
     * @param folder      the folder to store the image
     * @param requestCode the request code to returned in onActivityResult
     * @return the output file Uri
     * @hide
     */
    private static Uri openImageIntent(@NonNull Activity activity, @Nullable Fragment fragment, @NonNull File folder, int requestCode) {

        // Determine Uri of camera image to save.
        final String fname = "IMG_" + System.currentTimeMillis() + ".jpg"; // need to make in local cache
        File sdImageMainDirectory = new File(folder, fname);
        Uri outputFileUri = Utils.getUriForFile(activity, sdImageMainDirectory);
        if (outputFileUri == null) {
            return null;
        }

        // Camera.
        final List<Intent> cameraIntents = new ArrayList<>();
        final Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        final PackageManager packageManager = activity.getPackageManager();
        final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam) {
            final String packageName = res.activityInfo.packageName;
            final Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(packageName);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            cameraIntents.add(intent);
        }
        if (listCam.isEmpty()) {
            // queryIntentActivities does not work since Android 11
            final Intent intent = new Intent(captureIntent);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            cameraIntents.add(intent);
        }

        // Filesystem.
        final Intent fileIntent = new Intent(Intent.ACTION_GET_CONTENT);
        fileIntent.setType("image/*");
        cameraIntents.add(fileIntent);

        // gallery
        final Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");
        //galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

        // Chooser of filesystem options.
        final Intent chooserIntent = Intent.createChooser(galleryIntent, activity.getString(R.string.image_intent_title));

        // Add the camera options.
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[]{}));

        if (fragment != null) {
            fragment.startActivityForResult(chooserIntent, requestCode);
        } else {
            activity.startActivityForResult(chooserIntent, requestCode);
        }
        return outputFileUri;
    }

    @Nullable
    public static String getImageSignaturePath(Activity activity, Intent data, Uri outputFileUri) {
        if (activity == null) {
            return null;
        }
        try {
            Map imageIntent = ViewerUtils.readImageIntent(data, activity, outputFileUri);
            if (!ViewerUtils.checkImageIntent(imageIntent)) {
                Utils.handlePdfFromImageFailed(activity, imageIntent);
                return null;
            }
            Uri imageUri = (Uri) imageIntent.get(IMAGE_INTENT_URI);
            return StampManager.getInstance().createSignatureFromImage(activity, imageUri);
        } catch (Exception e) {
            CommonToast.showText(activity, activity.getString(R.string.image_stamper_error), Toast.LENGTH_SHORT);
            AnalyticsHandlerAdapter.getInstance().sendException(e);
        }
        return null;
    }

    public static void createImageSignature(Activity activity, Intent data, PDFViewCtrl pdfViewCtrl,
            Uri outputFileUri, PointF targetPoint, int targetPage, Long widget) {
        if (activity == null) {
            return;
        }
        if (targetPoint == null && widget == null) {
            return;
        }
        try {
            //////////////   get bitmap of image   ////////////////
            Map imageIntent = ViewerUtils.readImageIntent(data, activity, outputFileUri);
            if (!ViewerUtils.checkImageIntent(imageIntent)) {
                Utils.handlePdfFromImageFailed(activity, imageIntent);
                return;
            }
            String filePath = (String) imageIntent.get(IMAGE_INTENT_PATH);
            Uri imageUri = (Uri) imageIntent.get(IMAGE_INTENT_URI);
            Boolean isCamera = (Boolean) imageIntent.get(IMAGE_INTENT_IS_CAMERA);

            /////////////   get page number   ////////////////

            Signature tool;
            if (((ToolManager) pdfViewCtrl.getToolManager()).getTool().getToolMode() != ToolManager.ToolMode.SIGNATURE) {
                tool = (Signature) ((ToolManager) pdfViewCtrl.getToolManager()).createTool(ToolManager.ToolMode.SIGNATURE, null);
                ((ToolManager) pdfViewCtrl.getToolManager()).setTool(tool);
            } else {
                tool = (Signature) ((ToolManager) pdfViewCtrl.getToolManager()).getTool();
            }
            if (targetPoint != null) {
                tool.setTargetPoint(targetPoint, targetPage);
            }
            Annot targetAnnot = null;
            if (widget != null) {
                targetAnnot = Widget.__Create(widget, pdfViewCtrl.getDoc());
            }
            String signaturePath = StampManager.getInstance().createSignatureFromImage(activity, imageUri);
            if (signaturePath != null) {
                tool.create(signaturePath, targetAnnot);
            }

            boolean storeSig = CreateSignatureFragment.getStoreSignature(activity);
            if (!storeSig) {
                // remove the file if not store
                StampManager.getInstance().deleteSignature(activity, signaturePath);
            }

            // cleanup the image if it is from camera
            if (isCamera) {
                FileUtils.deleteQuietly(new File(filePath));
            }
            AnalyticsHandlerAdapter.getInstance().sendEvent(AnalyticsHandlerAdapter.EVENT_CREATE_IMAGE_SIGNATURE,
                    AnalyticsParam.createNewParam(isCamera ? AnalyticsHandlerAdapter.CREATE_NEW_ITEM_IMAGE_FROM_CAMERA : AnalyticsHandlerAdapter.CREATE_NEW_ITEM_IMAGE_FROM_IMAGE,
                            AnalyticsHandlerAdapter.SCREEN_VIEWER));
        } catch (FileNotFoundException e) {
            CommonToast.showText(activity, activity.getString(R.string.image_stamper_file_not_found_error), Toast.LENGTH_SHORT);
            AnalyticsHandlerAdapter.getInstance().sendException(e);
        } catch (Exception e) {
            CommonToast.showText(activity, activity.getString(R.string.image_stamper_error), Toast.LENGTH_SHORT);
            AnalyticsHandlerAdapter.getInstance().sendException(e);
        }
    }

    @Nullable
    public static Uri getImageUriFromIntent(@Nullable Intent data, @NonNull Activity activity, @NonNull Uri outputFileUri) {
        try {
            Map imageIntent = ViewerUtils.readImageIntent(data, activity, outputFileUri);
            if (!ViewerUtils.checkImageIntent(imageIntent)) {
                Utils.handlePdfFromImageFailed(activity, imageIntent);
                return null;
            }
            return (Uri) imageIntent.get(IMAGE_INTENT_URI);
        } catch (FileNotFoundException e) {
            CommonToast.showText(activity, activity.getString(R.string.image_stamper_file_not_found_error), Toast.LENGTH_SHORT);
            AnalyticsHandlerAdapter.getInstance().sendException(e);
        }
        return null;
    }

    public static void createImageStamp(Activity activity, Intent data, PDFViewCtrl pdfViewCtrl,
            Uri outputFileUri, PointF imageStampTargetPoint) {
        if (activity == null) {
            return;
        }
        try {
            //////////////   get bitmap of image   ////////////////
            Map imageIntent = ViewerUtils.readImageIntent(data, activity, outputFileUri);
            if (!ViewerUtils.checkImageIntent(imageIntent)) {
                Utils.handlePdfFromImageFailed(activity, imageIntent);
                return;
            }
            String filePath = (String) imageIntent.get(IMAGE_INTENT_PATH);
            Uri imageUri = (Uri) imageIntent.get(IMAGE_INTENT_URI);
            Boolean isCamera = (Boolean) imageIntent.get(IMAGE_INTENT_IS_CAMERA);

            /////////////   get page number   ////////////////

            Stamper stamperTool;
            if (((ToolManager) pdfViewCtrl.getToolManager()).getTool().getToolMode() != ToolManager.ToolMode.STAMPER) {
                stamperTool = (Stamper) ((ToolManager) pdfViewCtrl.getToolManager()).createTool(ToolManager.ToolMode.STAMPER, null);
                ((ToolManager) pdfViewCtrl.getToolManager()).setTool(stamperTool);
            } else {
                stamperTool = (Stamper) ((ToolManager) pdfViewCtrl.getToolManager()).getTool();
            }
            stamperTool.setTargetPoint(imageStampTargetPoint, false);
            boolean success = stamperTool.createImageStamp(imageUri, 0, imageUri.getEncodedPath());
            if (!success) {
                CommonToast.showText(activity, activity.getString(R.string.image_stamper_error), Toast.LENGTH_SHORT);
            }

            // cleanup the image if it is from camera
            if (isCamera) {
                FileUtils.deleteQuietly(new File(filePath));
            }
            AnalyticsHandlerAdapter.getInstance().sendEvent(AnalyticsHandlerAdapter.EVENT_CREATE_IMAGE_STAMP,
                    AnalyticsParam.createNewParam(isCamera ? AnalyticsHandlerAdapter.CREATE_NEW_ITEM_IMAGE_FROM_CAMERA : AnalyticsHandlerAdapter.CREATE_NEW_ITEM_IMAGE_FROM_IMAGE,
                            AnalyticsHandlerAdapter.SCREEN_VIEWER));
        } catch (FileNotFoundException e) {
            CommonToast.showText(activity, activity.getString(R.string.image_stamper_file_not_found_error), Toast.LENGTH_SHORT);
            AnalyticsHandlerAdapter.getInstance().sendException(e);
        } catch (Exception e) {
            CommonToast.showText(activity, activity.getString(R.string.image_stamper_error), Toast.LENGTH_SHORT);
            AnalyticsHandlerAdapter.getInstance().sendException(e);
        }
    }

    @Nullable
    public static String exportFileAttachment(@NonNull PDFViewCtrl pdfViewCtrl, @NonNull FileAttachment attachment) {
        return exportFileAttachment(pdfViewCtrl, attachment, Utils.getExternalDownloadDirectory(pdfViewCtrl.getContext()));
    }

    @Nullable
    public static String exportFileAttachment(@NonNull PDFViewCtrl pdfViewCtrl, @NonNull FileAttachment attachment, @NonNull File directory) {
        if (!directory.isDirectory()) {
            return null;
        }
        boolean shouldUnlockRead = false;
        try {
            pdfViewCtrl.docLockRead();
            shouldUnlockRead = true;
            String filename = attachment.getFileSpec().getFilePath();
            String extension = Utils.getExtension(filename);
            filename = FilenameUtils.getName(filename);
            if (Utils.isNullOrEmpty(extension)) {
                // no extension, let's try to open it as PDF
                filename = filename + ".pdf";
            }
            File attachmentFile = new File(directory, filename);
            String attachmentFilePath = Utils.getFileNameNotInUse(attachmentFile.getAbsolutePath());
            attachmentFile = new File(attachmentFilePath);
            attachment.export(attachmentFile.getAbsolutePath());
            return attachmentFile.getAbsolutePath();
        } catch (Exception e) {
            AnalyticsHandlerAdapter.getInstance().sendException(e);
        } finally {
            if (shouldUnlockRead) {
                pdfViewCtrl.docUnlockRead();
            }
        }
        return null;
    }

    /**
     * Export file attachment to target URI, return whether successful.
     */
    public static boolean exportFileAttachment(@NonNull PDFViewCtrl pdfViewCtrl, @NonNull FileAttachment attachment, @NonNull Uri targetUri) {
        boolean shouldUnlockRead = false;
        File tempFile = null;
        InputStream fis = null;
        OutputStream fos = null;
        Context context = pdfViewCtrl.getContext();
        try {
            pdfViewCtrl.docLockRead();
            shouldUnlockRead = true;
            // create a temp file
            tempFile = File.createTempFile("tmp", null);
            attachment.export(tempFile.getAbsolutePath());
            // copy to uri
            fis = new FileInputStream(tempFile);
            ContentResolver contentResolver = Utils.getContentResolver(context);
            if (contentResolver != null) {
                fos = contentResolver.openOutputStream(targetUri);
                IOUtils.copy(fis, fos);
                return true;
            }
        } catch (Exception e) {
            AnalyticsHandlerAdapter.getInstance().sendException(e);
        } finally {
            if (shouldUnlockRead) {
                pdfViewCtrl.docUnlockRead();
            }
            if (tempFile != null) {
                tempFile.delete();
            }
            Utils.closeQuietly(fis);
            Utils.closeQuietly(fos);
        }
        return false;
    }

    /**
     * Reads an image intent.
     *
     * @param data          The intent
     * @param context       The context
     * @param outputFileUri The output URI
     * @return The internal image intent map
     * @throws FileNotFoundException FileNotFound exception
     */
    public static Map readImageIntent(Intent data, @NonNull Context context, @Nullable Uri outputFileUri) throws FileNotFoundException {
        if (outputFileUri == null) {
            return null;
        }

        final boolean isCamera;
        if (data == null || data.getData() == null) {
            isCamera = true;
        } else {
            final String action = data.getAction();
            isCamera = action != null && action.equals(MediaStore.ACTION_IMAGE_CAPTURE);
        }

        Uri imageUri;
        if (isCamera) {
            imageUri = outputFileUri;
        } else {
            imageUri = data.getData();
        }

        String filePath;
        if (isCamera) {
            filePath = imageUri.getPath();
        } else {
            filePath = Utils.getRealPathFromImageURI(context, imageUri);
            if (Utils.isNullOrEmpty(filePath)) {
                filePath = imageUri.getPath();
            }
        }

        // if a file is selected, check if it is an image file
        if (!isCamera) {
            // if type is null
            ContentResolver contentResolver = Utils.getContentResolver(context);
            if (contentResolver == null) {
                return null;
            }
            if (contentResolver.getType(imageUri) == null) {
                String extension = MimeTypeMap.getFileExtensionFromUrl(imageUri.getPath());
                final String[] extensions = {"jpeg", "jpg", "tiff", "tif", "gif", "png", "bmp"};

                // if file extension is not an image extension
                if (!Arrays.asList(extensions).contains(extension) && extension != null && !extension.equals("")) {
                    throw new FileNotFoundException("file extension is not an image extension");
                }
                // if type is not an image
            } else {
                String type = contentResolver.getType(imageUri);
                if (type != null && !type.startsWith("image/")) {
                    throw new FileNotFoundException("type is not an image");
                }
            }
        }

        Map<String, Object> output = new HashMap<>();
        output.put(IMAGE_INTENT_URI, imageUri);
        output.put(IMAGE_INTENT_PATH, filePath);
        output.put(IMAGE_INTENT_IS_CAMERA, isCamera);
        return output;
    }

    /**
     * Checks whether the internal image intent map is valid.
     *
     * @param imageIntent The internal image intent map obtained from {@link #readImageIntent}
     * @return True if the internal image intent is valid
     */
    public static boolean checkImageIntent(Map imageIntent) {
        return !(imageIntent == null ||
                imageIntent.get(IMAGE_INTENT_PATH) == null || !(imageIntent.get(IMAGE_INTENT_PATH) instanceof String) ||
                imageIntent.get(IMAGE_INTENT_URI) == null || !(imageIntent.get(IMAGE_INTENT_URI) instanceof Uri) ||
                imageIntent.get(IMAGE_INTENT_IS_CAMERA) == null || !(imageIntent.get(IMAGE_INTENT_IS_CAMERA) instanceof Boolean));
    }

    /**
     * Returns the image URI
     *
     * @param imageIntent The internal image intent map obtained from {@link #readImageIntent}
     * @return The image URI
     */
    public static Uri getImageUri(Map imageIntent) {
        return imageIntent == null ? null : (Uri) imageIntent.get(IMAGE_INTENT_URI);
    }

    /**
     * Returns the image file path.
     *
     * @param imageIntent The internal image intent map obtained from {@link #readImageIntent}
     * @return The image file path
     */
    public static String getImageFilePath(Map imageIntent) {
        return imageIntent == null ? null : (String) imageIntent.get(IMAGE_INTENT_PATH);
    }

    /**
     * Checks whether the image is taken from camera.
     *
     * @param imageIntent The internal image intent map obtained from {@link #readImageIntent}
     * @return True if the image is taken from camera
     */
    public static boolean isImageFromCamera(Map imageIntent) {
        return imageIntent != null && (boolean) imageIntent.get(IMAGE_INTENT_IS_CAMERA);
    }

    /**
     * Converts image to PDF from an image intent
     *
     * @param context    the context
     * @param imagePath  the image intent
     * @param outputPath the result file path
     * @return null if failed, otherwise returns the result file path
     */
    public static String imageIntentToPdf(Context context, Uri imageUri, String imagePath, String outputPath) throws PDFNetException, FileNotFoundException {
        if (context == null || (imageUri == null && Utils.isNullOrEmpty(imagePath)) || outputPath == null) {
            return null;
        }
        PDFDoc outDoc = null;
        SecondaryFileFilter filter = null;
        try {
            DocumentConversion conv;
            if (imageUri != null) {
                filter = new SecondaryFileFilter(context, imageUri);
                conv = Convert.universalConversion(filter, new WordToPDFOptions("{\"DPI\": 96.0}"));
            } else {
                conv = Convert.universalConversion(imagePath, new WordToPDFOptions("{\"DPI\": 96.0}"));
            }
            conv.convert();
            if (conv.getDoc() == null) {
                return null;
            }
            outDoc = conv.getDoc();
            outDoc.save(outputPath, SDFDoc.SaveMode.REMOVE_UNUSED, null);
            return outputPath;
        } finally {
            Utils.closeQuietly(outDoc, filter);
        }
    }

    /**
     * Converts image to PDF from an image intent
     *
     * @param context      the context
     * @param imageUri     the image intent
     * @param imagePath    the image path
     * @param documentFile the result file path
     * @return null if failed, otherwise returns the result file path
     */
    public static String imageIntentToPdf(Context context, Uri imageUri, String imagePath, ExternalFileInfo documentFile)
            throws PDFNetException, IOException {
        if (context == null || (imageUri == null && Utils.isNullOrEmpty(imagePath)) || documentFile == null) {
            return null;
        }
        PDFDoc outDoc = null;
        SecondaryFileFilter filter = null;
        SecondaryFileFilter outFilter;
        try {
            DocumentConversion conv;
            if (imageUri != null) {
                filter = new SecondaryFileFilter(context, imageUri);
                conv = Convert.universalConversion(filter, new WordToPDFOptions("{\"DPI\": 96.0}"));
            } else {
                conv = Convert.universalConversion(imagePath, new WordToPDFOptions("{\"DPI\": 96.0}"));
            }
            conv.convert();
            if (conv.getDoc() == null) {
                return null;
            }
            outDoc = conv.getDoc();

            outFilter = new SecondaryFileFilter(context, documentFile.getUri());

            outDoc.save(outFilter, SDFDoc.SaveMode.REMOVE_UNUSED);
            return documentFile.getAbsolutePath();
        } finally {
            Utils.closeQuietly(outDoc, filter);
        }
    }

    /**
     * Returns the image bitmap.
     *
     * @param context     The context
     * @param imageIntent The internal image intent map obtained from {@link #readImageIntent}
     * @return The image bitmap
     */
    public static Bitmap getImageBitmap(Context context, Map imageIntent) {
        Uri imageUri = getImageUri(imageIntent);
        String filePath = getImageFilePath(imageIntent);
        if (imageUri == null || Utils.isNullOrEmpty(filePath)) {
            return null;
        }

        Bitmap bitmap = Utils.getBitmapFromImageUri(context, imageUri, filePath);

        try {
            int imageRotation = getImageRotation(context, imageIntent);
            if (bitmap != null && imageRotation != 0) {
                Matrix matrix = new Matrix();
                matrix.postRotate(imageRotation);
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            }
        } catch (OutOfMemoryError oom) {
            Utils.manageOOM(context);
            return null;
        }

        return bitmap;
    }

    private static int getImageRotation(Context context, Map imageIntent) {
        Uri imageUri = getImageUri(imageIntent);
        File imageFile = new File(imageUri.getPath());
        ExifInterface exif = null;
        ParcelFileDescriptor pfd = null;
        try {
            try {
                if (imageFile.exists()) {
                    exif = new ExifInterface(imageFile.getAbsolutePath());
                } else if (Utils.isNougat()) {
                    ContentResolver contentResolver = Utils.getContentResolver(context);
                    if (contentResolver == null) {
                        return 0;
                    }
                    pfd = contentResolver.openFileDescriptor(imageUri, "r");
                    if (pfd != null) {
                        exif = new ExifInterface(pfd.getFileDescriptor());
                    }
                }
            } catch (Exception e) {
                return 0;
            }
            if (null == exif) {
                return 0;
            }
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);

            int imageRotation = 0;
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    imageRotation = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    imageRotation = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    imageRotation = 90;
                    break;
            }

            // in some devices (mainly Samsung), the EXIF is not saved with the image so look at the content
            // resolver as a second source of the image's rotation
            if (imageRotation == 0) {
                String[] orientationColumn = {MediaStore.Images.Media.ORIENTATION};
                Cursor cursor = null;
                ContentResolver contentResolver = Utils.getContentResolver(context);
                if (contentResolver == null) {
                    return 0;
                }
                try {
                    cursor = contentResolver.query(imageUri, orientationColumn, null, null, null);
                    orientation = -1;
                    if (cursor != null && cursor.moveToFirst() && cursor.getColumnCount() > 0 && cursor.getCount() > 0) {
                        int index = cursor.getColumnIndex(orientationColumn[0]);
                        if (index == -1) {
                            return imageRotation;
                        }
                        orientation = cursor.getInt(index);
                    }
                    if (orientation > 0) {
                        imageRotation = orientation;
                    }
                } finally {
                    if (cursor != null) {
                        cursor.close();
                    }
                }
            }

            return imageRotation;
        } finally {
            if (pfd != null) {
                Utils.closeQuietly(pfd);
            }
        }
    }

    public static void jumpToRect(PDFViewCtrl pdfViewCtrl, Rect rect, int pageNum) {
        try {
            pdfViewCtrl.setCurrentPage(pageNum);
            final int color = pdfViewCtrl.getContext().getResources().getColor(R.color.annotation_flashing_box);
            View flashingView = createFlashingView(pdfViewCtrl, rect, pageNum, color);
            // And finally animate the flashing view
            animateView(flashingView, pdfViewCtrl);
        } catch (Exception e) {
            AnalyticsHandlerAdapter.getInstance().sendException(e);
        }
    }

    /**
     * Jumps to the specified annotation.
     *
     * @param pdfViewCtrl The PDFViewCtrl
     * @param annot       The annotation
     * @param pageNum     The page number where the annotation is on
     */
    public static void jumpToAnnotation(PDFViewCtrl pdfViewCtrl, Annot annot, int pageNum) {
        try {
            pdfViewCtrl.setCurrentPage(pageNum);
            com.pdftron.pdf.Rect annotRect = pdfViewCtrl.getPageRectForAnnot(annot, pageNum);
            final int color = pdfViewCtrl.getContext().getResources().getColor(R.color.annotation_flashing_box);
            View flashingView = createFlashingView(pdfViewCtrl, annotRect, pageNum, color);
            // And finally animate the flashing view
            animateView(flashingView, pdfViewCtrl);
        } catch (Exception e) {
            AnalyticsHandlerAdapter.getInstance().sendException(e);
        }
    }

    /**
     * Animates for showing undo/redo action.
     *
     * @param pdfViewCtrl The PDFViewCtrl
     * @param annotRect   The annotation rectangle to be animated
     * @param pageNum     The page number
     */
    public static void animateUndoRedo(PDFViewCtrl pdfViewCtrl, Rect annotRect, int pageNum) {
        pdfViewCtrl.setCurrentPage(pageNum);
        final int color = pdfViewCtrl.getContext().getResources().getColor(R.color.undo_redo_flashing_box);
        View flashingView = createFlashingView(pdfViewCtrl, annotRect, pageNum, color);
        animateUndoRedoView(flashingView, pdfViewCtrl);
    }

    public static void animateScreenRect(PDFViewCtrl pdfViewCtrl, Rect screenRect) {
        // TODO: discuss with antoine for a better animation
        final int color = pdfViewCtrl.getContext().getResources().getColor(R.color.annotation_flashing_box);

        View flashingView = new View(pdfViewCtrl.getContext());
        flashingView.setBackgroundColor(color);

        try {
            double left = Math.min(screenRect.getX1(), screenRect.getX2());
            double top = Math.min(screenRect.getY1(), screenRect.getY2());
            double right = Math.max(screenRect.getX1(), screenRect.getX2());
            double bottom = Math.max(screenRect.getY1(), screenRect.getY2());

            int sx = pdfViewCtrl.getScrollX();
            int sy = pdfViewCtrl.getScrollY();

            Rect flashingRect = new Rect(left + sx, top + sy, right + sx, bottom + sy);
            flashingRect.normalize();

            int flashingRectLeft = (int) flashingRect.getX1();
            int flashingRectTop = (int) flashingRect.getY1();
            int flashingRectRight = (int) flashingRect.getX2();
            int flashingRectBottom = (int) flashingRect.getY2();
            flashingView.layout(flashingRectLeft, flashingRectTop, flashingRectRight, flashingRectBottom);
            pdfViewCtrl.addView(flashingView);
            animateView(flashingView, pdfViewCtrl);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Scroll to annotation rect.
     *
     * @param pdfViewCtrl the PDFViewCtrl
     * @param annotRect   the annot rect
     * @param pageNum     the annot page number
     * @return final rect in client space after scrolling
     * @throws PDFNetException PDFNet exception
     */
    public static Rect scrollToAnnotRect(PDFViewCtrl pdfViewCtrl, Rect annotRect, int pageNum) throws PDFNetException {
        // Get the annotation bounding box
        double[] pts1 = new double[]{0.0f, 0.0f};
        double[] pts2 = new double[]{0.0f, 0.0f};
        try {
            double annotX1 = annotRect.getX1();
            double annotY1 = annotRect.getY1();
            double annotX2 = annotRect.getX2();
            double annotY2 = annotRect.getY2();

            // check the annotation rectangle is large enough to attract attention
            final int MIN_LENGTH = 10;
            double annotWidth = annotRect.getWidth();
            if (annotWidth < MIN_LENGTH) {
                annotX1 -= (MIN_LENGTH - annotWidth) / 2;
                annotX2 += (MIN_LENGTH - annotWidth) / 2;
            }
            double annotHeight = annotRect.getHeight();
            if (annotHeight < MIN_LENGTH) {
                annotY1 -= (MIN_LENGTH - annotHeight) / 2;
                annotY2 += (MIN_LENGTH - annotHeight) / 2;
            }

            // Note that the returned Rect from the Annot object is in
            // page space, so we need to convert the points to screen
            // space before using them in the view layout.
            // Lower left corner
            pts1 = pdfViewCtrl.convPagePtToScreenPt(annotX1, annotY1, pageNum);
            // Upper right corner
            pts2 = pdfViewCtrl.convPagePtToScreenPt(annotX2, annotY2, pageNum);
        } catch (PDFNetException e) {
            AnalyticsHandlerAdapter.getInstance().sendException(e);
        }

        // [start] adjusting position so that the annotation shows up in the middle of the screen
        // Note that page might be rotated, so ensure left <= right and top <= bottom
        double top = pts1[1] < pts2[1] ? pts1[1] : pts2[1];
        double bottom = pts1[1] > pts2[1] ? pts1[1] : pts2[1];
        double left = pts1[0] < pts2[0] ? pts1[0] : pts2[0];
        double right = pts1[0] > pts2[0] ? pts1[0] : pts2[0];

        int x, y;
        double screenHeight = pdfViewCtrl.getHeight();
        double annotHeight = Math.abs(top - bottom);
        double offsetY = Math.abs(screenHeight - annotHeight) / 2.0;
        if (screenHeight > annotHeight) {
            y = pdfViewCtrl.getScrollY() + (int) Math.round(top - offsetY);
            top = offsetY;
        } else {
            y = pdfViewCtrl.getScrollY() + (int) Math.round(top + offsetY);
            top = -offsetY;
        }
        if (y < 0) {
            top += y;
        }
        int canvasOffsetY = (int) (y + screenHeight - pdfViewCtrl.getViewCanvasHeight());
        if (canvasOffsetY > 0) {
            top += y - (y > canvasOffsetY ? y - canvasOffsetY : 0);
            y -= canvasOffsetY;
        }
        bottom = top + annotHeight;
        if (y < 0) {
            y = 0;
        }

        double screenWidth = pdfViewCtrl.getWidth();
        double annotWidth = Math.abs(right - left);
        double offsetX = Math.abs(screenWidth - annotWidth) / 2.0;
        if (screenWidth > annotWidth) {
            x = pdfViewCtrl.getScrollX() + (int) Math.round(left - offsetX);
            left = offsetX;
        } else {
            x = pdfViewCtrl.getScrollX() + (int) Math.round(left + offsetX);
            left = -offsetX;
        }
        int scrollOffset = pdfViewCtrl.getScrollOffsetForCanvasId(pdfViewCtrl.getCurCanvasId());
        if (pdfViewCtrl.isMaintainZoomEnabled() || pdfViewCtrl.getScrollX() == scrollOffset) {
            // otherwise pdfViewCtrl.getScrollX() returns the offset from the screen instead of
            // the offset from the canvas origin
            x -= scrollOffset;
        }
        if (x < 0) {
            left += x;
        }
        int canvasOffsetX = (int) (x + screenWidth - pdfViewCtrl.getViewCanvasWidth());
        if (canvasOffsetX > 0) {
            left += x - (x > canvasOffsetX ? x - canvasOffsetX : 0);
            x -= canvasOffsetX;
        }
        right = left + annotWidth;
        if (x < 0) {
            x = 0;
        }
        int sx = pdfViewCtrl.getScrollX();
        int sy = pdfViewCtrl.getScrollY();
        int leftBound = (int) (x - annotWidth / 2);
        int rightBound = (int) (x + annotWidth / 2);
        int topBound = (int) (y - annotHeight / 2);
        int bottomBound = y + (int) annotHeight / 2;

        sx -= scrollOffset;

        if (rightBound >= sx + screenWidth / 2) {
            int dist = rightBound - sx - (int) screenWidth / 2;
            sx += dist;
        }
        if (leftBound <= sx - screenWidth / 2) {
            int dist = sx - (int) screenWidth / 2 - leftBound;
            sx -= dist;
        }

        if (bottomBound >= sy + screenHeight / 2) {
            int dist = bottomBound - sy;
            sy += dist;
        }
        if (topBound <= sy - screenHeight / 2 || (y <= sy - screenHeight / 4)) {
            int dist = sy - (int) screenHeight / 4 - topBound;
            sy -= dist;
        }
        if (x == 0) {
            sx = x;
        }
        if (y == 0) {
            sy = y;
        }
        pdfViewCtrl.scrollTo(sx, sy);

        // in case the viewer is currently scrolled

        sx = x + scrollOffset;
        sy = y;
        // [end]

        return new Rect(left + sx, top + sy, right + sx, bottom + sy);
    }

    private static View createFlashingView(PDFViewCtrl pdfViewCtrl, Rect annotRect, int pageNum, int color) {
        // Now add the flashing view on top of the selected annotation
        View flashingView = new View(pdfViewCtrl.getContext());
        flashingView.setBackgroundColor(color);

        try {
            Rect flashingRect = scrollToAnnotRect(pdfViewCtrl, annotRect, pageNum);
            flashingRect.normalize();

            // The following deal with rotated pages
            int flashingRectLeft = (int) flashingRect.getX1();
            int flashingRectTop = (int) flashingRect.getY1();
            int flashingRectRight = (int) flashingRect.getX2();
            int flashingRectBottom = (int) flashingRect.getY2();

            // Set the layout of the view (note that top/bottom are
            // inverted since in page space the lower-left corner
            // is the origin
            flashingView.layout(flashingRectLeft, flashingRectTop, flashingRectRight, flashingRectBottom);
            pdfViewCtrl.addView(flashingView);
        } catch (PDFNetException ex) {
            AnalyticsHandlerAdapter.getInstance().sendException(ex);
        }

        return flashingView;
    }

    private static void animateView(final View view, final PDFViewCtrl pdfViewCtrl) {
        // Example of another animation that could be applied to the view.
        //ValueAnimator colorAnim = ObjectAnimator.ofInt(view, "backgroundColor", /*Red*/0xFFFF8080, /*Blue*/0xFF8080FF);
        //colorAnim.setDuration(1500);
        //colorAnim.setEvaluator(new ArgbEvaluator());
        //colorAnim.setRepeatCount(2);
        //colorAnim.setRepeatMode(ValueAnimator.REVERSE);

        // Since we want the flashing view to be removed once the animation
        // is finished, we use this listener to remove the view from
        // PDFViewCtrl when the event is triggered.
        Animator.AnimatorListener animListener = new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                pdfViewCtrl.removeView(view);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                pdfViewCtrl.removeView(view);
            }
        };

        // Get animator for the flashing view.
        Animator fader = createAlphaAnimator(view, animListener);

        // If using more than one animator, you can create a set and
        // play them together, or in some other order...
        AnimatorSet animation = new AnimatorSet();
        animation.playTogether(/*colorAnim, */fader);
        animation.start();
    }

    private static void animateUndoRedoView(final View view, final PDFViewCtrl pdfViewCtrl) {
        // Since we want the flashing view to be removed once the animation
        // is finished, we use this listener to remove the view from
        // PDFViewCtrl when the event is triggered.
        Animator.AnimatorListener animListener = new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                pdfViewCtrl.removeView(view);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }
        };

        // We animate only the opacity of the view.
        ValueAnimator fader = ObjectAnimator.ofFloat(view, "alpha", 0f, .7f, 0.0f);

        fader.setDuration(500);
        fader.setEvaluator(new FloatEvaluator());
        fader.addListener(animListener);

        // If using more than one animator, you can create a set and
        // play them together, or in some other order...
        AnimatorSet animation = new AnimatorSet();
        animation.playTogether(fader);
        animation.start();
    }

    /**
     * Creates an Animator object that varies the alpha property of the view. The
     * interpolation used is linear.
     *
     * @param view     the view to be animated
     * @param listener the listener to be used by this Animator
     * @return a new Animator object that will change the "alpha" property
     * of the view.
     */
    private static Animator createAlphaAnimator(View view, Animator.AnimatorListener listener) {
        if (view == null) {
            return null;
        }
        // We animate only the opacity of the view.
        ValueAnimator fader = ObjectAnimator.ofFloat(view, "alpha", 0.0f, 1.0f, 0.4f, 1.0f, 0.0f);
        fader.setDuration(1500);
        fader.setEvaluator(new FloatEvaluator());
        if (listener != null) {
            fader.addListener(listener);
        }
        return fader;
    }

    /**
     * Returns annotation by providing its ID.
     *
     * @param pdfDoc  The PDFDoc
     * @param id      The ID of the annotation
     * @param pageNum The page number
     * @return The annotation having the specified ID
     */
    @Nullable
    public static Annot getAnnotById(PDFDoc pdfDoc, String id, int pageNum) {
        if (null == pdfDoc || null == id) {
            return null;
        }

        boolean shouldUnlockRead = false;
        try {
            pdfDoc.lockRead();
            shouldUnlockRead = true;
            Page page = pdfDoc.getPage(pageNum);
            int count = page.getNumAnnots();
            for (int i = 0; i < count; ++i) {
                Annot annotation = page.getAnnot(i);
                if (annotation != null && annotation.isValid() && annotation.getUniqueID() != null) {
                    String annotId = annotation.getUniqueID().getAsPDFText();
                    if (id.equals(annotId)) {
                        return annotation;
                    }
                }
            }
        } catch (Exception e) {
            AnalyticsHandlerAdapter.getInstance().sendException(e);
        } finally {
            if (shouldUnlockRead) {
                Utils.unlockReadQuietly(pdfDoc);
            }
        }
        return null;
    }

    /**
     * Returns annotation by providing its ID.
     *
     * @param ctrl    The PDFViewCtrl
     * @param id      The ID of the annotation
     * @param pageNum The page number
     * @return The annotation having the specified ID
     */
    @Nullable
    public static Annot getAnnotById(PDFViewCtrl ctrl, String id, int pageNum) {
        if (null == ctrl || null == ctrl.getDoc() || null == id) {
            return null;
        }

        boolean shouldUnlockRead = false;
        try {
            ctrl.docLockRead();
            shouldUnlockRead = true;
            ArrayList<Annot> annots = ctrl.getAnnotationsOnPage(pageNum);
            for (Annot annotation : annots) {
                if (annotation != null && annotation.isValid() && annotation.getUniqueID() != null) {
                    String annotId = annotation.getUniqueID().getAsPDFText();
                    if (id.equals(annotId)) {
                        return annotation;
                    }
                }
            }
        } catch (Exception e) {
            AnalyticsHandlerAdapter.getInstance().sendException(e);
        } finally {
            if (shouldUnlockRead) {
                ctrl.docUnlockRead();
            }
        }
        return null;
    }

    /**
     * Returns selected text in pdf
     *
     * @param pdfViewCtrl The PDFViewCtrl
     * @return The selected text in pdf
     */
    public static String getSelectedString(PDFViewCtrl pdfViewCtrl) {
        return getSelectedString(pdfViewCtrl, false);
    }

    /**
     * Returns selected text in pdf
     *
     * @param pdfViewCtrl   The PDFViewCtrl
     * @param appendNewLine Whether to append new line on line break
     * @return The selected text in pdf
     */
    public static String getSelectedString(PDFViewCtrl pdfViewCtrl, boolean appendNewLine) {
        StringBuilder text = new StringBuilder();
        if (pdfViewCtrl.hasSelection()) {
            int sel_pg_begin = pdfViewCtrl.getSelectionBeginPage();
            int sel_pg_end = pdfViewCtrl.getSelectionEndPage();
            boolean shouldUnlockRead = false;
            TextExtractor textExtractor = null;
            try {
                pdfViewCtrl.docLockRead();
                shouldUnlockRead = true;
                if (appendNewLine) {
                    textExtractor = new TextExtractor();
                }
                for (int pg = sel_pg_begin; pg <= sel_pg_end; ++pg) {
                    PDFViewCtrl.Selection sel = pdfViewCtrl.getSelection(pg);
                    String t = sel.getAsUnicode();
                    if (appendNewLine) {
                        double[] quads = sel.getQuads();
                        int sz = quads.length / 8;  // Each quad has eight numbers (x0, y0), ... (x3, y3)

                        if (sz == 0) {
                            continue;
                        }
                        int k = 0;
                        for (int i = 0; i < sz; ++i, k += 8) {
                            double[] xVals = new double[]{
                                    quads[k],
                                    quads[k + 2],
                                    quads[k + 4],
                                    quads[k + 6]
                            };
                            Arrays.sort(xVals);
                            double[] yVals = new double[]{
                                    quads[k + 1],
                                    quads[k + 3],
                                    quads[k + 5],
                                    quads[k + 7]
                            };
                            Arrays.sort(yVals);
                            com.pdftron.pdf.Rect quadRect = new com.pdftron.pdf.Rect(xVals[0], yVals[0], xVals[xVals.length - 1], yVals[yVals.length - 1]);
                            quadRect.normalize();

                            textExtractor.begin(pdfViewCtrl.getDoc().getPage(pg), quadRect);
                            text.append(textExtractor.getAsText());
                        }
                    } else {
                        text.append(t);
                    }
                }
            } catch (Exception e) {
                AnalyticsHandlerAdapter.getInstance().sendException(e);
            } finally {
                if (shouldUnlockRead) {
                    pdfViewCtrl.docUnlockRead();
                }
                if (textExtractor != null) {
                    textExtractor.destroy();
                }
            }
        }
        return text.toString();
    }

    public static BitmapDrawable getBitmapDrawable(@NonNull Context context,
            int drawableId,
            int width,
            int height,
            int targetColor,
            boolean roundedCorner) {
        return getBitmapDrawable(context, drawableId, width, height, targetColor, roundedCorner, false);
    }

    public static BitmapDrawable getBitmapDrawable(@NonNull Context context,
            int drawableId,
            int width,
            int height,
            int targetColor,
            boolean roundedCorner,
            boolean nonWhiteOnly) {
        Resources resources = context.getResources();
        Bitmap icon = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Drawable drawable = Utils.getDrawable(context, drawableId);
        Canvas canvas = new Canvas(icon);
        if (roundedCorner) {
            // 4dp rounded corner
            Path clipPath = new Path();
            RectF rect = new RectF(0, 0, width, height);
            float corner = Utils.convDp2Pix(context, 4);
            clipPath.addRoundRect(rect, corner, corner, Path.Direction.CW);
            canvas.clipPath(clipPath);
        }
        if (drawable != null) {
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
        }

        if (nonWhiteOnly) {
            int originalColor = icon.getPixel(width / 2, height / 2);
            if (originalColor != targetColor) {
                // only replace color if target is different from original
                icon = Utils.replace9PatchColor(icon, targetColor);
            }
        } else {
            icon = Utils.replace9PatchColor(icon, icon.getPixel(width / 2, height / 2), targetColor);
        }
        return new BitmapDrawable(resources, icon);
    }

    public static StateListDrawable createBackgroundSelector(Drawable drawable) {
        return new StateListDrawableBuilder()
                .setPressedDrawable(drawable)
                .setSelectedDrawable(drawable)
                .setHoveredDrawable(drawable)
                .setNormalDrawable(new ColorDrawable(Color.TRANSPARENT))
                .build();
    }

    public static int getLastPageForURL(Context context, String url) {
        if (context == null || Utils.isNullOrEmpty(url)) {
            return -1;
        }
        String cache = PdfViewCtrlSettingsManager.getOpenUrlAsyncCache(context);
        if (!Utils.isNullOrEmpty(cache)) {
            try {
                LinkedHashMap<String, String> cacheMap = Utils.convJSONToMap(cache);
                String result = cacheMap.get(url);
                if (!Utils.isNullOrEmpty(result)) {
                    int page = Integer.parseInt(result);
                    if (page > 0) {
                        return page;
                    }
                }
            } catch (Exception ex) {
                AnalyticsHandlerAdapter.getInstance().sendException(ex);
            }
        }
        return -1;
    }

    public static void setLastPageForURL(Context context, String url, int page) {
        if (context == null || Utils.isNullOrEmpty(url) || page < 1) {
            return;
        }
        int max = 25;
        String cache = PdfViewCtrlSettingsManager.getOpenUrlAsyncCache(context);
        try {
            LinkedHashMap<String, String> cacheMap = Utils.convJSONToMap(cache);
            // sets limit, remove oldest item if over max
            if (cacheMap.size() > max) {
                String firstKey = cacheMap.keySet().iterator().next();
                cacheMap.remove(firstKey);
            }
            cacheMap.put(url, String.valueOf(page));
            JSONObject cacheJson = Utils.convMapToJSON(cacheMap);
            PdfViewCtrlSettingsManager.setOpenUrlAsyncCache(context, cacheJson.toString());
        } catch (Exception ex) {
            AnalyticsHandlerAdapter.getInstance().sendException(ex);
        }
    }

    public static void passwordDoc(PDFDoc doc, String password) {
        if (doc != null) {
            boolean shouldUnlock = false;
            try {
                doc.lock();
                shouldUnlock = true;
                //remove all security on the document
                doc.removeSecurity();
                if (!Utils.isNullOrEmpty(password)) {
                    // Set a new password required to open a document
                    SecurityHandler new_handler = new SecurityHandler(SecurityHandler.e_AES);
                    new_handler.changeUserPassword(password);

                    // Set Permissions
                    new_handler.setPermission(SecurityHandler.e_print, true);

                    // Note: document takes the ownership of new_handler.
                    doc.setSecurityHandler(new_handler);
                }
            } catch (Exception e) {
                AnalyticsHandlerAdapter.getInstance().sendException(e);
            } finally {
                if (shouldUnlock) {
                    Utils.unlockQuietly(doc);
                }
            }
        }
    }

    public static void flattenDoc(PDFDoc doc) {
        if (doc != null) {
            boolean shouldUnlock = false;
            try {
                doc.lock();
                shouldUnlock = true;
                PDFDoc.FlattenMode[] flattenModes = {PDFDoc.FlattenMode.ANNOTS, PDFDoc.FlattenMode.FORMS};
                doc.flattenAnnotationsAdvanced(flattenModes);
            } catch (Exception e) {
                AnalyticsHandlerAdapter.getInstance().sendException(e);
            } finally {
                if (shouldUnlock) {
                    Utils.unlockQuietly(doc);
                }
            }
        }
    }

    public static Single<String> extractFileFromPortfolioDisposable(final int type, final Context context, final PDFDoc portfolioDoc,
            @Nullable final String destFolderPath, @Nullable final Uri destFileUri, final String fileName) {
        return Single.create(new SingleOnSubscribe<String>() {
            @Override
            public void subscribe(SingleEmitter<String> emitter) throws Exception {
                boolean shouldUnlockRead = false;
                try {
                    portfolioDoc.lockRead();
                    shouldUnlockRead = true;
                    emitter.onSuccess(extractFileFromPortfolio(type, context, portfolioDoc, destFolderPath, destFileUri, fileName));
                } catch (Exception e) {
                    AnalyticsHandlerAdapter.getInstance().sendException(e);
                    emitter.tryOnError(e);
                } finally {
                    if (shouldUnlockRead) {
                        portfolioDoc.unlockRead();
                    }
                }
            }
        });
    }

    public static String extractFileFromPortfolio(int type, Context context, PDFDoc portfolioDoc, String destFolderPath, String fileName) {
        return extractFileFromPortfolio(type, context, portfolioDoc, destFolderPath, null, fileName);
    }

    public static String extractFileFromPortfolio(int type, Context context, PDFDoc portfolioDoc,
            @Nullable final String destFolderPath, @Nullable final Uri destFileUri, String fileName) {
        // Note: this method assumes PDFDoc has already passed SecurityHandler check
        if (portfolioDoc == null) {
            return "";
        }
        String retValue = "";
        SecondaryFileFilter filter = null;
        try {
            NameTree files = NameTree.find(portfolioDoc.getSDFDoc(), "EmbeddedFiles");
            if (files.isValid()) {
                // Traverse the list of embedded files.
                NameTreeIterator iter = files.getIterator();
                while (iter.hasNext()) {
                    String entryName = iter.key().getAsPDFText();
                    com.pdftron.pdf.FileSpec file_spec = new com.pdftron.pdf.FileSpec(iter.value());
                    if (file_spec.isValid()) {
                        entryName = file_spec.getFilePath();
                    }
                    if (entryName.equalsIgnoreCase(fileName)) {
                        if (type == PortfolioDialogFragment.FILE_TYPE_FILE_URI) {
                            Uri destUri = destFileUri;
                            if (destFileUri != null) {
                                retValue = destFileUri.toString();
                            }
                            if (destFolderPath != null) {
                                String newFileName;
                                ExternalFileInfo newFileInfo = null;
                                int i;
                                for (i = 0; i < Utils.MAX_NUM_DUPLICATED_FILES; i++) {
                                    Uri parentUri = Uri.parse(destFolderPath);
                                    ExternalFileInfo parentUriInfo = Utils.buildExternalFile(context, parentUri);
                                    if (parentUriInfo != null) {

                                        if (i == 0) {
                                            newFileName = fileName;
                                        } else {
                                            String extension = FilenameUtils.getExtension(fileName);
                                            newFileName = FilenameUtils.removeExtension(fileName) + " (" + String.valueOf(i) + ")." + extension;
                                        }

                                        if (parentUriInfo.findFile(newFileName) == null) {
                                            Uri tempUri = ExternalFileInfo.appendPathComponent(parentUri, newFileName);
                                            String extension = MimeTypeMap.getFileExtensionFromUrl(tempUri.toString());
                                            MimeTypeMap mime = MimeTypeMap.getSingleton();
                                            String newFileType = mime.getMimeTypeFromExtension(extension);
                                            newFileInfo = parentUriInfo.createFile(newFileType, newFileName);
                                            break;
                                        }
                                    }
                                }
                                if (newFileInfo != null) {
                                    retValue = newFileInfo.getAbsolutePath();
                                    destUri = newFileInfo.getUri();
                                }
                            }

                            if (destUri != null) {
                                com.pdftron.filters.Filter stm = file_spec.getFileData();
                                if (stm != null) {
                                    filter = new SecondaryFileFilter(context, destUri, SecondaryFileFilter.WRITE_MODE);
                                    FilterWriter filterWriter = new FilterWriter(filter);
                                    FilterReader filterReader = new FilterReader(stm);
                                    filterWriter.writeFilter(filterReader);
                                    filterWriter.flushAll();
                                    break;
                                }
                            }
                        } else {
                            String fullFileName = (new File(destFolderPath, fileName)).getAbsolutePath();
                            String newFileName = fullFileName;
                            int i;
                            for (i = 1; i < Utils.MAX_NUM_DUPLICATED_FILES; i++) {
                                File file = new File(newFileName);
                                if (file.exists()) {
                                    // get file extension
                                    // it is possible that the file attachment is not a PDF file
                                    String extension = FilenameUtils.getExtension(fullFileName);
                                    newFileName = FilenameUtils.removeExtension(fullFileName)
                                            + " (" + String.valueOf(i) + ")." + extension;
                                } else {
                                    break;
                                }
                            }
                            if (i < Utils.MAX_NUM_DUPLICATED_FILES) {
                                retValue = newFileName;
                            } else {
                                break;
                            }

                            com.pdftron.filters.Filter stm = file_spec.getFileData();
                            if (stm != null) {
                                stm.writeToFile(newFileName, false);
                                break;
                            }
                        }
                    }
                    iter.next();
                }
            }
        } catch (Exception e) {
            retValue = "";
        } finally {
            Utils.closeQuietly(filter);
        }

        return retValue;
    }

    /**
     * Checks if the tab is in continuous page mode.
     *
     * @return True if the tab is in continuous page mode
     */
    public static boolean isContinuousPageMode(PDFViewCtrl pdfViewCtrl) {
        if (null == pdfViewCtrl) {
            return false;
        }
        PDFViewCtrl.PagePresentationMode mode = pdfViewCtrl.getPagePresentationMode();
        return mode == PDFViewCtrl.PagePresentationMode.SINGLE_CONT ||
                mode == PDFViewCtrl.PagePresentationMode.FACING_CONT ||
                mode == PDFViewCtrl.PagePresentationMode.FACING_COVER_CONT;
    }

    /**
     * Checks if the tab is in continuous snapping page mode.
     *
     * @return True if the tab is in continuous snapping page mode
     */
    public static boolean isNonContinuousVerticalPageMode(PDFViewCtrl pdfViewCtrl) {
        if (null == pdfViewCtrl) {
            return false;
        }
        PDFViewCtrl.PagePresentationMode mode = pdfViewCtrl.getPagePresentationMode();
        return mode == PDFViewCtrl.PagePresentationMode.SINGLE_VERT ||
                mode == PDFViewCtrl.PagePresentationMode.FACING_VERT ||
                mode == PDFViewCtrl.PagePresentationMode.FACING_COVER_VERT;
    }

    /**
     * Checks if the tab is in single page mode.
     *
     * @return True if the tab is in single page mode
     */
    public static boolean isSinglePageMode(PDFViewCtrl pdfViewCtrl) {
        return !isContinuousPageMode(pdfViewCtrl) && !isNonContinuousVerticalPageMode(pdfViewCtrl);
    }

    public static boolean isInFullScreenMode(Context context) {
        if (context != null) {
            return Utils.isKitKat() && PdfViewCtrlSettingsManager.getFullScreenMode(context);
        }
        return false;
    }

    private static final String KEY_MENU_EDITOR_ID = "id";
    private static final String KEY_MENU_EDITOR_GROUP_IF_ROOM = "ifroom";
    private static final String KEY_MENU_EDITOR_GROUP_NEVER = "never";

    /**
     * @deprecated
     */
    @Deprecated
    public static String getMenuEditorItemsJSON(@NonNull ArrayList<MenuEditorItem> newMenuItems) throws JSONException {
        int group = -1;
        JSONObject menuJson = new JSONObject();
        JSONArray shownArray = new JSONArray();
        JSONArray overflowArray = new JSONArray();
        for (int index = 0; index < newMenuItems.size(); index++) {
            MenuEditorItem newItem = newMenuItems.get(index);
            if (newItem.isHeader()) {
                MenuEditorItemHeader header = (MenuEditorItemHeader) newItem;
                group = header.getGroup();
            } else {
                MenuEditorItemContent itemContent = (MenuEditorItemContent) newItem;
                if (group == MenuEditorItemContent.GROUP_SHOW_IF_ROOM) {
                    JSONObject item = new JSONObject();
                    item.put(KEY_MENU_EDITOR_ID, itemContent.getId());
                    shownArray.put(item);
                } else {
                    JSONObject item = new JSONObject();
                    item.put(KEY_MENU_EDITOR_ID, itemContent.getId());
                    overflowArray.put(item);
                }
            }
        }
        menuJson.put(KEY_MENU_EDITOR_GROUP_IF_ROOM, shownArray);
        menuJson.put(KEY_MENU_EDITOR_GROUP_NEVER, overflowArray);

        return menuJson.toString();
    }

    /**
     * @deprecated
     */
    @Deprecated
    public static ArrayList<MenuEditorItem> getMenuEditorItemsArray(String savedToolbarMenu) throws JSONException {
        ArrayList<MenuEditorItem> menuEditorItems = new ArrayList<>();
        JSONObject menuJson = new JSONObject(savedToolbarMenu);
        JSONArray shownArray = menuJson.getJSONArray(KEY_MENU_EDITOR_GROUP_IF_ROOM);
        JSONArray overflowArray = menuJson.getJSONArray(KEY_MENU_EDITOR_GROUP_NEVER);
        MenuEditorItemHeader header1 = new MenuEditorItemHeader(MenuEditorItemContent.GROUP_SHOW_IF_ROOM, "placeholder", "");
        menuEditorItems.add(header1);
        for (int i = 0; i < shownArray.length(); i++) {
            JSONObject object = shownArray.getJSONObject(i);
            int id = object.getInt(KEY_MENU_EDITOR_ID);
            MenuEditorItemContent content = new MenuEditorItemContent(id, "placeholder", 0);
            menuEditorItems.add(content);
        }
        MenuEditorItemHeader header2 = new MenuEditorItemHeader(MenuEditorItemContent.GROUP_SHOW_NEVER, "placeholder", "");
        menuEditorItems.add(header2);
        for (int i = 0; i < overflowArray.length(); i++) {
            JSONObject object = overflowArray.getJSONObject(i);
            int id = object.getInt(KEY_MENU_EDITOR_ID);
            MenuEditorItemContent content = new MenuEditorItemContent(id, "placeholder", 0);
            menuEditorItems.add(content);
        }

        return menuEditorItems;
    }

    /**
     * Import annotation XFDF command to the document
     *
     * @param pdfViewCtrl the viewer
     * @param xfdfCommand the XFDF command string
     * @throws PDFNetException
     */
    public static void importAnnotationCommand(@NonNull PDFViewCtrl pdfViewCtrl, @NonNull String xfdfCommand) throws PDFNetException {
        PDFDoc pdfDoc = pdfViewCtrl.getDoc();
        if (pdfDoc == null) {
            throw new PDFNetException("", 0L, "ViewerUtils", "importAnnotationCommand", "PDFDoc is null.");
        }
        boolean shouldUnlockRead = false;
        try {
            pdfViewCtrl.docLockRead();
            shouldUnlockRead = true;

            if (pdfDoc.hasDownloader()) {
                // still downloading file
                throw new PDFNetException("", 0L, "ViewerUtils", "importAnnotationCommand", "Document download in progress, try again later.");
            }
        } finally {
            if (shouldUnlockRead) {
                pdfViewCtrl.docUnlockRead();
            }
        }

        boolean shouldUnlock = false;
        try {
            pdfViewCtrl.docLock(true);
            shouldUnlock = true;

            FDFDoc fdfDoc = pdfDoc.fdfExtract(PDFDoc.e_both);
            fdfDoc.mergeAnnots(xfdfCommand);

            pdfDoc.fdfUpdate(fdfDoc);
            pdfViewCtrl.update(true);
        } finally {
            if (shouldUnlock) {
                pdfViewCtrl.docUnlock();
            }
        }
    }

    /**
     * Removes a page bookmark
     *
     * @param context     the context
     * @param isReadOnly  whether to edit the original pdf
     * @param pdfViewCtrl the PDFViewCtrl
     * @param page        the page to remove from bookmark
     */
    public static void removePageBookmark(@NonNull Context context, boolean isReadOnly,
            @NonNull PDFViewCtrl pdfViewCtrl, int page) {
        boolean shouldUnlock = false;
        try {
            pdfViewCtrl.docLock(false);
            shouldUnlock = true;

            long pageObjNum = pdfViewCtrl.getDoc().getPage(page).getSDFObj().getObjNum();
            if (isReadOnly) {
                BookmarkManager.removeUserBookmark(context, pdfViewCtrl, pdfViewCtrl.getDoc().getFileName(), pageObjNum, page);
            } else {
                BookmarkManager.removePdfBookmark(context, pdfViewCtrl, pageObjNum, page);
            }
            CommonToast.showText(context, R.string.controls_misc_bookmark_removed);
        } catch (Exception e) {
            AnalyticsHandlerAdapter.getInstance().sendException(e);
        } finally {
            if (shouldUnlock) {
                pdfViewCtrl.docUnlock();
            }
        }
    }

    /**
     * Adds a page bookmark
     *
     * @param context     the context
     * @param isReadOnly  whether to edit the original pdf
     * @param pdfViewCtrl the PDFViewCtrl
     * @param page        the page to add to bookmark
     */
    public static void addPageToBookmark(@NonNull Context context, boolean isReadOnly,
            @NonNull PDFViewCtrl pdfViewCtrl, int page) {
        boolean shouldUnlock = false;
        try {
            pdfViewCtrl.docLock(false);
            shouldUnlock = true;

            long pageObjNum = pdfViewCtrl.getDoc().getPage(page).getSDFObj().getObjNum();
            if (isReadOnly) {
                BookmarkManager.addUserBookmark(context, pdfViewCtrl, pdfViewCtrl.getDoc().getFileName(), pageObjNum, page);
            } else {
                BookmarkManager.addPdfBookmark(context, pdfViewCtrl, pageObjNum, page);
            }
            CommonToast.showText(context, R.string.controls_misc_bookmark_added);
        } catch (Exception e) {
            AnalyticsHandlerAdapter.getInstance().sendException(e);
        } finally {
            if (shouldUnlock) {
                pdfViewCtrl.docUnlock();
            }
        }
    }

    /**
     * Prevents menu item from closing its popup menu on click
     *
     * @param item menu item to prevent from closing menu
     */
    public static void keepOnScreenAfterClick(@NonNull Context context, @NonNull MenuItem item) {
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
        item.setActionView(new View(context));
        item.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return false;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                return false;
            }
        });
    }

    /**
     * Converts page positions to the page set
     *
     * @param positions The page positions as boolean
     * @return The page set
     */
    public static PageSet getPageSet(SparseBooleanArray positions) {
        PageSet set = new PageSet();
        int rangeBegin = -1;
        int rangeEnd = -1;

        for (int i = 0; i < positions.size(); i++) {
            int key = positions.keyAt(i);
            boolean isSelected = positions.get(key);
            int page = key + 1;
            if (isSelected) {
                if (rangeBegin < 0) {
                    rangeBegin = page;
                    rangeEnd = page;
                } else if (rangeBegin > 0) {
                    if (rangeEnd + 1 == page) {
                        rangeEnd++;
                    } else {
                        set.addRange(rangeBegin, rangeEnd);
                        rangeBegin = rangeEnd = page;
                    }
                }
            } else {
                if (rangeBegin > 0) {
                    set.addRange(rangeBegin, rangeEnd);
                    rangeBegin = -1;
                    rangeEnd = -1;
                }
            }
        }

        if (rangeBegin > 0) {
            set.addRange(rangeBegin, rangeEnd);
        }

        return set;
    }

    /**
     * Safe version of updatePageLayout, will call readLock before and unlock after this operation
     * as recommended by Core Team
     * <p>
     * Updates the page layout within the view. This function must be called
     * after document page sequence is modified (such as when a page is being
     * added to or removed from a document) or after changes to page dimensions
     * (e.g. after a page is rotated or resized).
     *
     * @param pdfViewCtrl the PDFViewCtrl
     * @param callback    Callback object to provide exception handling, can be null
     */
    public static void safeUpdatePageLayout(@NonNull PDFViewCtrl pdfViewCtrl,
            @Nullable ExceptionHandlerCallback callback) {
        boolean shouldUnlockRead = false;
        try {
            pdfViewCtrl.docLockRead();
            shouldUnlockRead = true;
            pdfViewCtrl.updatePageLayout();
        } catch (Exception e) {
            if (callback != null) {
                callback.onException(e);
            } else {
                e.printStackTrace();
            }
        } finally {
            if (shouldUnlockRead) {
                pdfViewCtrl.docUnlockRead();
            }
        }
    }

    public static void safeUpdatePageLayout(@NonNull PDFViewCtrl pdfViewCtrl) {
        safeUpdatePageLayout(pdfViewCtrl, null);
    }

    // A write lock is expected around this method
    public static void renameAllFields(@NonNull Page page) throws PDFNetException {
        int numAnnots = page.getNumAnnots();
        for (int i = 0; i < numAnnots; ++i) {
            Annot annot = page.getAnnot(i);
            if (annot.isValid() && annot.getType() == Annot.e_Widget) {
                Widget widget = new Widget(annot);
                Field field = widget.getField();
                String currentName = field.getName();
                String newName = currentName + "_" + UUID.randomUUID().toString();
                field.rename(newName);
            }
        }
    }
}

