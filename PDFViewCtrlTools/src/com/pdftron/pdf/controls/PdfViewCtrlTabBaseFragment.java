//---------------------------------------------------------------------------------------
// Copyright (c) 2001-2019 by PDFTron Systems Inc. All Rights Reserved.
// Consult legal.txt regarding legal and license information.
//---------------------------------------------------------------------------------------

package com.pdftron.pdf.controls;

import android.animation.Animator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.ParcelFileDescriptor;
import android.os.SystemClock;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.style.ImageSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Pair;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.PointerIcon;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.webkit.URLUtil;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.ColorInt;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProviders;
import androidx.transition.ChangeBounds;
import androidx.transition.Fade;
import androidx.transition.Slide;
import androidx.transition.Transition;
import androidx.transition.TransitionManager;
import androidx.transition.TransitionSet;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.card.MaterialCardView;
import com.pdftron.common.PDFNetException;
import com.pdftron.common.RecentlyUsedCache;
import com.pdftron.filters.MappedFile;
import com.pdftron.filters.SecondaryFileFilter;
import com.pdftron.pdf.Annot;
import com.pdftron.pdf.ColorPt;
import com.pdftron.pdf.ConversionOptions;
import com.pdftron.pdf.Convert;
import com.pdftron.pdf.DocumentConversion;
import com.pdftron.pdf.OfficeToPDFOptions;
import com.pdftron.pdf.PDFDoc;
import com.pdftron.pdf.PDFNet;
import com.pdftron.pdf.PDFRasterizer;
import com.pdftron.pdf.PDFViewCtrl;
import com.pdftron.pdf.Page;
import com.pdftron.pdf.PageIterator;
import com.pdftron.pdf.Print;
import com.pdftron.pdf.Rect;
import com.pdftron.pdf.annots.FileAttachment;
import com.pdftron.pdf.asynctask.GetTextInPageTask;
import com.pdftron.pdf.asynctask.PDFDocLoaderTask;
import com.pdftron.pdf.config.PDFViewCtrlConfig;
import com.pdftron.pdf.config.ToolManagerBuilder;
import com.pdftron.pdf.config.ViewerConfig;
import com.pdftron.pdf.dialog.BookmarksDialogFragment;
import com.pdftron.pdf.dialog.OptimizeDialogFragment;
import com.pdftron.pdf.dialog.PortfolioDialogFragment;
import com.pdftron.pdf.dialog.pagelabel.PageLabelUtils;
import com.pdftron.pdf.dialog.redaction.SearchRedactionDialogFragment;
import com.pdftron.pdf.model.BaseFileInfo;
import com.pdftron.pdf.model.ExternalFileInfo;
import com.pdftron.pdf.model.FileInfo;
import com.pdftron.pdf.model.FreeTextCacheStruct;
import com.pdftron.pdf.model.OptimizeParams;
import com.pdftron.pdf.model.PageState;
import com.pdftron.pdf.model.PdfViewCtrlTabInfo;
import com.pdftron.pdf.model.UserBookmarkItem;
import com.pdftron.pdf.tools.AnnotEdit;
import com.pdftron.pdf.tools.AnnotEditLine;
import com.pdftron.pdf.tools.FreeHighlighterCreate;
import com.pdftron.pdf.tools.FreehandCreate;
import com.pdftron.pdf.tools.Pan;
import com.pdftron.pdf.tools.QuickMenu;
import com.pdftron.pdf.tools.QuickMenuItem;
import com.pdftron.pdf.tools.R;
import com.pdftron.pdf.tools.TextHighlighter;
import com.pdftron.pdf.tools.TextSelect;
import com.pdftron.pdf.tools.Tool;
import com.pdftron.pdf.tools.ToolManager;
import com.pdftron.pdf.tools.ToolManager.ToolMode;
import com.pdftron.pdf.tools.UndoRedoManager;
import com.pdftron.pdf.utils.AnalyticsHandlerAdapter;
import com.pdftron.pdf.utils.AnalyticsParam;
import com.pdftron.pdf.utils.AnnotSnappingManager;
import com.pdftron.pdf.utils.AppUtils;
import com.pdftron.pdf.utils.BasicHTTPDownloadTask;
import com.pdftron.pdf.utils.BasicHeadRequestTask;
import com.pdftron.pdf.utils.CommonToast;
import com.pdftron.pdf.utils.Constants;
import com.pdftron.pdf.utils.DialogGoToPage;
import com.pdftron.pdf.utils.ExceptionHandlerCallback;
import com.pdftron.pdf.utils.FileInfoManager;
import com.pdftron.pdf.utils.HTML2PDF;
import com.pdftron.pdf.utils.ImageMemoryCache;
import com.pdftron.pdf.utils.Logger;
import com.pdftron.pdf.utils.PageBackButtonInfo;
import com.pdftron.pdf.utils.PathPool;
import com.pdftron.pdf.utils.PdfDocManager;
import com.pdftron.pdf.utils.PdfViewCtrlSettingsManager;
import com.pdftron.pdf.utils.PdfViewCtrlTabsManager;
import com.pdftron.pdf.utils.RecentFilesManager;
import com.pdftron.pdf.utils.RequestCode;
import com.pdftron.pdf.utils.ShortcutHelper;
import com.pdftron.pdf.utils.SwipeDetector;
import com.pdftron.pdf.utils.UserCropUtilities;
import com.pdftron.pdf.utils.Utils;
import com.pdftron.pdf.utils.ViewerUtils;
import com.pdftron.pdf.utils.cache.BookmarksCache;
import com.pdftron.pdf.utils.cache.UriCacheManager;
import com.pdftron.pdf.viewmodel.BookmarksViewModel;
import com.pdftron.pdf.viewmodel.PageChangeViewModel;
import com.pdftron.pdf.viewmodel.RichTextEvent;
import com.pdftron.pdf.viewmodel.RichTextViewModel;
import com.pdftron.pdf.widget.ContentLoadingRelativeLayout;
import com.pdftron.pdf.widget.richtext.RCContainer;
import com.pdftron.sdf.SDFDoc;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleObserver;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

import static android.graphics.Color.HSVToColor;
import static android.graphics.Color.RGBToHSV;
import static com.pdftron.pdf.config.PDFViewCtrlConfig.MAX_RELATIVE_ZOOM_LIMIT;
import static com.pdftron.pdf.config.PDFViewCtrlConfig.MIN_RELATIVE_ZOOM_LIMIT;
import static com.pdftron.pdf.tools.Tool.IS_LINK;
import static com.pdftron.pdf.tools.Tool.LINK_URL;
import static com.pdftron.pdf.tools.Tool.METHOD_FROM;

/**
 * The PdfViewCtrlTabBaseFragment is the base class for other viewer classes.
 */
public abstract class PdfViewCtrlTabBaseFragment extends Fragment implements
        PDFViewCtrl.PageChangeListener,
        PDFViewCtrl.DocumentDownloadListener,
        PDFViewCtrl.UniversalDocumentConversionListener,
        PDFViewCtrl.DocumentLoadListener,
        PDFViewCtrl.RenderingListener,
        PDFViewCtrl.UniversalDocumentProgressIndicatorListener,
        ToolManager.PreToolManagerListener,
        ToolManager.QuickMenuListener,
        ToolManager.AnnotationModificationListener,
        ToolManager.PdfDocModificationListener,
        ToolManager.PdfTextModificationListener,
        ToolManager.BasicAnnotationListener,
        ToolManager.OnGenericMotionEventListener,
        ToolManager.ToolChangedListener,
        ToolManager.AdvancedAnnotationListener,
        ToolManager.FileAttachmentAnnotationListener,
        ReflowControl.OnReflowTapListener,
        PortfolioDialogFragment.PortfolioDialogFragmentListener {

    private static final String TAG = PdfViewCtrlTabBaseFragment.class.getName();
    protected static boolean sDebug;

    public static final String BUNDLE_TAB_TAG = "bundle_tab_tag";
    public static final String BUNDLE_TAB_TITLE = "bundle_tab_title";
    public static final String BUNDLE_TAB_FILE_EXTENSION = "bundle_tab_file_extension";
    public static final String BUNDLE_TAB_PASSWORD = "bundle_tab_password";
    public static final String BUNDLE_TAB_ITEM_SOURCE = "bundle_tab_item_source";
    public static final String BUNDLE_TAB_CONTENT_LAYOUT = "bundle_tab_content_layout";
    public static final String BUNDLE_TAB_PDFVIEWCTRL_ID = "bundle_tab_pdfviewctrl_id";
    public static final String BUNDLE_TAB_CONFIG = "bundle_tab_config";
    public static final String BUNDLE_TAB_CUSTOM_HEADERS = "bundle_tab_custom_headers";
    public static final String BUNDLE_TAB_INITIAL_PAGE = "bundle_tab_initial_page";
    public static final String BUNDLE_TAB_ANNOTATION_MANAGER_UNDO_MODE = "bundle_tab_annotation_manager_undo_mode";
    public static final String BUNDLE_TAB_ANNOTATION_MANAGER_EDIT_MODE = "bundle_tab_annotation_manager_edit_mode";

    protected File mCacheFolder;

    protected static final String BUNDLE_OUTPUT_FILE_URI = "output_file_uri";
    protected static final String BUNDLE_IMAGE_STAMP_TARGET_POINT = "image_stamp_target_point";
    protected static final String BUNDLE_ANNOTATION_TOOLBAR_SHOW = "bundle_annotation_toolbar_show";
    protected static final String BUNDLE_ANNOTATION_TOOLBAR_TOOL_MODE = "bundle_annotation_toolbar_tool_mode";

    protected static final int MAX_SIZE_PAGE_BACK_BUTTON_STACK = 50; // maximum size of the stack for the back and forward page button

    protected static final float TAP_REGION_THRESHOLD = (1f / 7f);
    protected static final int HIDE_PAGE_NUMBER_INDICATOR = 5000; // 5 sec
    protected static final int MAX_CONVERSION_TIME_WITHOUT_NOTIFICATION = 20000; // 20 sec

    protected static final int SAVE_DOC_INTERVAL = 30000; // 30 sec
    protected static final int FORCE_SAVE_DOC_INTERVAL = 120000; // 2 min

    public static final int ANIMATE_DURATION_SHOW = 250;
    public static final int ANIMATE_DURATION_HIDE = 250;

    protected static final int SAVEAS_FILE_FOLDER_REQUEST = 1;
    protected static final int FLATTEN_FILE_FOLDER_REQUEST = 2;
    protected static final int EXPORT_OPTIMIZE_COPY_FOLDER_REQUEST = 3;
    protected static final int EXPORT_CROPPED_COPY_FOLDER_REQUEST = 4;
    protected static final int EXPORT_PASSWORD_COPY_FOLDER_REQUEST = 5;

    // UI elements
    protected View mOverlayStub;
    protected ContentLoadingRelativeLayout mProgressBarLayout;
    protected ViewGroup mViewerHost;
    protected View mPasswordLayout;
    protected EditText mPasswordInput;
    protected CheckBox mPasswordCheckbox;
    protected PageIndicatorLayout mPageNumberIndicator;
    protected ProgressBar mPageNumberIndicatorSpinner;
    protected TextView mPageNumberIndicatorAll;
    protected FindTextOverlay mSearchOverlay;
    protected ImageButton mPageBackButton;
    protected ImageButton mPageForwardButton;
    protected MaterialCardView mPageNavContainer;

    // theme
    protected FloatingNavTheme mFloatingNavTheme;

    protected String mOpenUrlLink;
    protected String mTabTag;
    protected String mTabTitle;
    protected String mFileExtension;
    protected String mPassword;
    protected int mInitialPage = -1;
    protected int mTabSource;
    protected int mContentLayout;
    protected int mPdfViewCtrlId;
    @Nullable
    protected ViewerConfig mViewerConfig;
    @Nullable
    protected JSONObject mCustomHeaders;

    protected GetTextInPageTask mGetTextInPageTask;

    protected PDFDocLoaderTask mPDFDocLoaderTask;

    // Page Back and Forward Buttons
    // for toggling between previously viewed pages
    protected Deque<PageBackButtonInfo> mPageBackStack; // Page back button stack
    protected Deque<PageBackButtonInfo> mPageForwardStack; // Page forward button stack
    protected Boolean mInternalLinkClicked = false; // True if the next page change is due to an internal link
    protected PageBackButtonInfo mPreviousPageInfo; // Page info of previous page
    protected PageBackButtonInfo mCurrentPageInfo; // Page info of current page
    protected Boolean mPushNextPageOnStack = false; // True if the next page needs to be pushed onto the stack Used for pushing the landing page onto the stack

    // Document conversion
    protected DocumentConversion mDocumentConversion;
    protected boolean mHasWarnedAboutCanNotEditDuringConversion;
    protected boolean mShouldNotifyWhenConversionFinishes;
    protected String mTabConversionTempPath;
    protected boolean mUniversalConverted;

    protected View mRootView;
    protected View mStubPDFViewCtrl;
    protected FrameLayout mNavigationList;
    protected PDFViewCtrl mPdfViewCtrl;
    protected ToolManager mToolManager;
    protected PDFDoc mPdfDoc;
    protected boolean mIsEncrypted;
    protected boolean mIsOfficeDoc;
    protected boolean mIsOfficeDocReady;
    protected long mLastSuccessfulSave;

    protected boolean mDocumentLoading;
    protected boolean mDocumentLoaded;
    protected boolean mWaitingForSetPage;
    protected int mWaitingForSetPageNum;
    protected int mErrorCode = PdfDocManager.DOCUMENT_SETDOC_ERROR_NONE;
    protected int mDocumentState = PdfDocManager.DOCUMENT_STATE_CLEAN;
    protected volatile boolean mHasChangesSinceOpened;
    protected boolean mHasChangesSinceResumed;
    protected boolean mWasSavedAndClosedShown;
    protected ProgressDialog mDownloadDocumentDialog;
    protected boolean mDownloading;
    protected PDFViewCtrl.DownloadState mDownloadState;
    protected File mCurrentFile; // System files
    protected Uri mCurrentUriFile; // Uri files

    protected long mOriginalFileLength = -1;

    protected volatile boolean mNeedsCleanupFile = true;                        // do not delete backup file if something went wrong
    protected volatile boolean mShouldCleanupFile = false;
    protected boolean mPrintDocumentChecked = true;
    protected boolean mPrintAnnotationsChecked = true;
    protected boolean mPrintSummaryChecked;

    protected int mPageCount;
    protected boolean mIsRtlMode;
    protected ReflowControl mReflowControl;
    protected int mReflowTextSize = -1;
    protected boolean mIsReflowMode;
    protected boolean mUsedCacheCalled;
    protected int mSpinnerSize = 96; // TODO get this size from some dps
    protected ProgressBar mUniversalDocSpinner;
    protected boolean mAnnotNotAddedDialogShown;
    protected final Object saveDocumentLock = new Object();
    protected boolean mCanAddToTabInfo = true;
    protected boolean mErrorOnOpeningDocument;
    protected boolean mLocalReadOnlyChecked;
    protected boolean mLocalReadOnlyCheckedResultSave; // true if read-only
    protected boolean mColorModeChanged;
    protected boolean mAnnotationSelected;
    protected Annot mSelectedAnnot;
    protected boolean mIsPageNumberIndicatorConversionSpinningRunning;
    protected boolean mInSearchMode;
    protected int mBookmarkDialogCurrentTab;

    protected boolean mToolbarOpenedFromMouseMovement;

    // Listeners
    protected TabListener mTabListener;
    protected ArrayList<ToolManager.QuickMenuListener> mQuickMenuListeners;
    protected ArrayList<PageStackListener> mPageStackListeners;
    protected ArrayList<PasswordProtectedListener> mPasswordProtectedListeners;

    /////////////////////////////////////////////////////////////////
    // Insert Image Stamper or Signature
    protected android.net.Uri mOutputFileUri;
    protected PointF mAnnotTargetPoint;
    protected int mAnnotTargetPage;
    protected Intent mAnnotIntentData;
    protected Long mTargetWidget;
    protected ToolMode mImageCreationMode;
    protected boolean mImageStampDelayCreation = false;
    protected boolean mImageSignatureDelayCreation = false;

    // Insert File Attachment
    protected boolean mFileAttachmentDelayCreation = false;
    // Save File Attachment
    protected boolean mSaveFileAttachmentDelay = false;
    protected FileAttachment mFileAttachment;

    // Screenshot Sharing
    protected boolean mScreenshotTempFileCreated = false;
    protected String mScreenshotTempFilePath = null;

    protected int mAnnotationToolbarMode = AnnotationToolbar.START_MODE_NORMAL_TOOLBAR;
    protected boolean mAnnotationToolbarShow;
    protected ToolMode mAnnotationToolbarToolMode = null;

    // Rage scrolling
    protected int mRageScrollingCount = 0;
    protected boolean mRageScrollingAsked;
    protected boolean mOnUpCalled; // work around issue where onUp is called twice
    protected static final int RAGE_SCROLLING_COUNT_MAX = 3;
    protected final SwipeDetector mSwipeDetector = new SwipeDetector();

    // Saving
    protected boolean mShowingSpecialFileAlertDialog = false;
    protected AlertDialog mSpecialFileAlertDialog;

    // File Attachments
    protected String mSelectedFileAttachmentName = null;

    // Disposables
    protected CompositeDisposable mDisposables = new CompositeDisposable();
    @Nullable
    protected Disposable mSaveBackUriDisposable = null;

    // Observable for the temp file, used for cleanup later
    protected Single<File> mTempDownloadObservable;

    // enable saving document changes
    protected boolean mSavingEnabled = true;
    // enable auto save timer
    protected boolean mAutoSaveTimerEnabled = true;

    // enable saving document state
    protected boolean mStateEnabled = true;

    protected boolean mScaling;

    private boolean mDestroyCalled = false;

    @Nullable
    protected PageChangeViewModel mPageViewModel;

    @Nullable
    protected BookmarksViewModel mBookmarksViewModel;

    /**
     * Callback interface to be invoked when an interaction is needed.
     */
    public interface TabListener {

        /**
         * Called when the document has been loaded.
         *
         * @param tag The tab tab
         */
        void onTabDocumentLoaded(String tag);

        /**
         * Called when an error has been happened to this tab.
         *
         * @param errorCode The code of error
         * @param info      The information
         */
        void onTabError(int errorCode, String info);

        /**
         * Called when a new tab has been opened.
         *
         * @param itemSource  The item source of document
         * @param tag         The tab tag
         * @param name        The name of the document
         * @param password    The password
         * @param initialPage The initial page to scroll to when the document is opened, unused if less than 1.
         */
        void onOpenAddNewTab(int itemSource, String tag, String name, String password, int initialPage);

        /**
         * Called when show tab info has been triggered.
         */
        void onShowTabInfo(String tag, String title, String fileExtension, int itemSource, int duration);

        /**
         * Called when an ink edit has been selected.
         *
         * @param annot   The annotation
         * @param pageNum The page number
         */
        void onInkEditSelected(Annot annot, int pageNum);

        /**
         * Called when the annotation toolbar should open for a tool mode.
         *
         * @param mode The tool mode
         */
        @Deprecated
        void onOpenAnnotationToolbar(ToolMode mode);

        /**
         * Called when the edit toolbar should open for a tool mode.
         *
         * @param mode The tool mode
         */
        void onOpenEditToolbar(ToolMode mode);

        /**
         * Called when the reflow mode has been toggled.
         */
        void onToggleReflow();

        /**
         * Called when should find next/previous text.
         *
         * @param searchUp True if should go to previous search (up)
         * @return The status
         */
        SearchResultsView.SearchResultStatus onFullTextSearchFindText(boolean searchUp);

        /**
         * Called when thumbnail slider has been stopped tracking touch.
         */
        void onTabThumbSliderStopTrackingTouch();

        /**
         * Called when a single tap has been touched on the tab.
         */
        void onTabSingleTapConfirmed();

        /**
         * The implementation should show the search progress.
         */
        void onSearchProgressShow();

        /**
         * The implementation should hide the search progress.
         */
        void onSearchProgressHide();

        /**
         * The implementation should reset timer for hiding toolbars.
         */
        void resetHideToolbarsTimer();

        /**
         * The implementation should change the visibility of toolbars.
         */
        void setToolbarsVisible(boolean visible);

        /**
         * The implementation should change the visibility of top toolbar,
         * bottom navigation bar as well as system navigation bar.
         */
        void setViewerOverlayUIVisible(boolean visible);

        /**
         * The implementation should return the height of toolbar.
         *
         * @return The height of toolbar
         */
        int getToolbarHeight();

        /**
         * Called when download successfully has been finished.
         */
        void onDownloadedSuccessful();

        /**
         * The implementation should close undo/redo popup menu.
         *
         * @deprecated
         */
        @Deprecated
        void onUndoRedoPopupClosed();

        /**
         * Called when the identity of the tab has been changed.
         *
         * @param oldTabTag        The old tab tag
         * @param newTabTag        The new tab tag
         * @param newTabTitle      The new title of the tab
         * @param newFileExtension The new extension of the document
         * @param newTabSource     The new item source of document
         */
        void onTabIdentityChanged(String oldTabTag, String newTabTag, String newTabTitle,
                String newFileExtension, int newTabSource);

        /**
         * Called when outline option selected
         */
        void onOutlineOptionSelected();

        /**
         * Called when page thumbnail viewer selected
         *
         * @param thumbnailEditMode <code>true</code> if thumbnail is in edit mode
         * @param checkedItem       The index of the item that is checked
         */
        void onPageThumbnailOptionSelected(boolean thumbnailEditMode, Integer checkedItem);

        /**
         * Called when onBackPressed is called when viewing a document.
         *
         * @return true if custom handling is required, false otherwise.
         */
        boolean onBackPressed();

        /**
         * Called when the fragment is paused.
         *
         * @param fileInfo                  The file shown when tab has been paused
         * @param isDocModifiedAfterOpening True if document has been modified
         *                                  after opening
         */
        void onTabPaused(FileInfo fileInfo, boolean isDocModifiedAfterOpening);

        /**
         * Called when an SD card file is opened as a local file
         */
        void onTabJumpToSdCardFolder();

        /**
         * Called when toolbar overflow menu should be updated
         */
        void onUpdateOptionsMenu();

        void onViewModeOptionSelected();

        void onSearchOptionSelected();

        /**
         * Called when key shortcut event triggered
         */
        boolean onHandleKeyShortcutEvent(int keyCode, KeyEvent event);
    }

    public interface PageStackListener {
        boolean onPreJumpPageBack(Deque<PageBackButtonInfo> backStack, Deque<PageBackButtonInfo> forwardStack);

        void onPostJumpPageBack(Deque<PageBackButtonInfo> backStack, Deque<PageBackButtonInfo> forwardStack);

        boolean onPreJumpPageForward(Deque<PageBackButtonInfo> backStack, Deque<PageBackButtonInfo> forwardStack);

        void onPostJumpPageForward(Deque<PageBackButtonInfo> backStack, Deque<PageBackButtonInfo> forwardStack);
    }

    public interface PasswordProtectedListener {
        /**
         * Called when a valid password is supplied when opening a password protected document.
         */
        void onPasswordValid();

        /**
         * Called when an invalid password is supplied when opening a password protected document.
         *
         * @return true if the handled, false otherwise
         */
        boolean onPasswordInvalid();
    }

    protected enum RegionSingleTap {
        Left,
        Middle,
        Right
    }

    // Auto-saving setup
    protected Handler mRequestSaveDocHandler = new Handler(Looper.getMainLooper());
    protected Runnable mTickSaveDocCallback = new Runnable() {
        @Override
        public void run() {
            if (isNotPdf()) {
                return;
            }
            if (!mAutoSaveTimerEnabled) {
                return;
            }

            if (mPdfViewCtrl != null) {
                long currentTime = System.currentTimeMillis();
                boolean needsForceSave = false;
                if ((currentTime - mLastSuccessfulSave) > FORCE_SAVE_DOC_INTERVAL) {
                    // we couldn't save for 2 min now...
                    // let's force save
                    needsForceSave = true;
                }
                save(false, needsForceSave, false);
            }
            postTickSaveDoc();
        }
    };

    protected void postTickSaveDoc() {
        if (mRequestSaveDocHandler != null) {
            mRequestSaveDocHandler.postDelayed(mTickSaveDocCallback, SAVE_DOC_INTERVAL);
        }
    }

    // Handlers
    // Hide page indicator, forward and back button setup
    protected Handler mHidePageNumberAndPageBackButtonHandler = new Handler(Looper.getMainLooper());
    protected Runnable mHidePageNumberAndPageBackButtonRunnable = new Runnable() {
        @Override
        public void run() {
            hidePageNumberIndicator();
        }
    };

    protected Handler mPageNumberIndicatorConversionSpinningHandler = new Handler(Looper.getMainLooper());
    protected Runnable mPageNumberIndicatorConversionSpinnerRunnable = new Runnable() {
        @Override
        public void run() {
            if (getActivity() != null && mPageNumberIndicatorSpinner != null)
                mPageNumberIndicatorSpinner.setVisibility(View.VISIBLE);
        }
    };

    protected Handler mConversionFinishedMessageHandler = new Handler(Looper.getMainLooper());
    protected Runnable mConversionFinishedMessageRunnable = new Runnable() {
        @Override
        public void run() {
            if (mDocumentConversion != null) {
                mShouldNotifyWhenConversionFinishes = true;
            }
        }
    };

    protected Handler mResetTextSelectionHandler = new Handler(Looper.getMainLooper());
    protected Runnable mResetTextSelectionRunnable = new Runnable() {
        @Override
        public void run() {
            Activity activity = getActivity();
            if (activity == null || activity.isFinishing() || mToolManager == null) {
                return;
            }
            ToolManager.Tool tool = mToolManager.getTool();
            if (tool instanceof TextSelect) {
                ((TextSelect) tool).resetSelection();
            }
        }
    };

    protected final ReflowControl.OnPostProcessColorListener mOnPostProcessColorListener =
            new ReflowControl.OnPostProcessColorListener() {
                @Override
                public ColorPt getPostProcessedColor(ColorPt cp) {
                    if (mPdfViewCtrl != null) {
                        return mPdfViewCtrl.getPostProcessedColor(cp);
                    }
                    return cp;
                }
            };

    protected final ReflowControl.ReflowUrlLoadedListener mReflowUrlLoadedListener =
            new ReflowControl.ReflowUrlLoadedListener() {
                @Override
                public boolean onReflowExternalUrlLoaded(WebView view, String url) {
                    ToolManager toolManager = getToolManager();
                    if (toolManager != null) {
                        Bundle bundle = createBundle(view, url);
                        bundle.putString(METHOD_FROM, "onReflowExternalUrlLoaded");
                        return toolManager.raiseInterceptAnnotationHandlingEvent(null, bundle, null);
                    }
                    return false;
                }

                @Override
                public boolean onReflowInternalUrlLoaded(WebView view, String url) {
                    ToolManager toolManager = getToolManager();
                    if (toolManager != null) {
                        Bundle bundle = createBundle(view, url);
                        bundle.putString(METHOD_FROM, "onReflowInternalUrlLoaded");
                        return toolManager.raiseInterceptAnnotationHandlingEvent(null, bundle, null);
                    }
                    return false;
                }

                private Bundle createBundle(WebView view, String url) {
                    Bundle bundle = new Bundle();
                    bundle.putBoolean(IS_LINK, true);
                    bundle.putString(LINK_URL, url);
                    return bundle;
                }
            };

    public String getFileName(boolean needsCopy, String suffix) {
        String extension = ".pdf";
        if (needsCopy) {
            if (suffix == null) {
                suffix = "Copy";
            }
            extension = "-" + suffix + extension;
        }
        return mTabTitle + extension;
    }

    /**
     * Wraps either a File or an ExternalFileInfo in order to make saving easier.
     */
    public class SaveFolderWrapper {
        private File mSelectedFolder;
        private ExternalFileInfo mSelectedExternalFolder;
        private File mLocalCopyFile;
        private File mExternalTempFile;
        private ExternalFileInfo mExternalCopyFile;
        private Uri mTargetUri; // from system folder picker

        public SaveFolderWrapper(ExternalFileInfo ext, String suffix) {
            this(ext, null, true, suffix);
        }

        public SaveFolderWrapper(File local, String suffix) {
            this(local, null, true, suffix);
        }

        public SaveFolderWrapper(ExternalFileInfo ext, boolean needsCopy) {
            this(ext, null, needsCopy, null);
        }

        public SaveFolderWrapper(File local, boolean needsCopy) {
            this(local, null, needsCopy, null);
        }

        public SaveFolderWrapper(Uri targetUri) {
            mTargetUri = targetUri;
            Activity activity = getActivity();
            if (activity == null) {
                return;
            }
            try {
                mExternalTempFile = File.createTempFile("tmp", ".pdf", activity.getFilesDir());
            } catch (Exception e) {
                AnalyticsHandlerAdapter.getInstance().sendException(e);
            }
        }

        public SaveFolderWrapper(ExternalFileInfo ext, String fileName, boolean needsCopy, String suffix) {
            mSelectedExternalFolder = ext;
            mExternalTempFile = null;
            Activity activity = getActivity();
            if (activity == null || ext == null || !Utils.isKitKat()) {
                return;
            }
            fileName = (StringUtils.isEmpty(fileName) ? getFileName(needsCopy, suffix) : fileName);
            if (mViewerConfig == null) { // only save extra copy if no config
                fileName = Utils.getFileNameNotInUse(ext, fileName);
            }
            String extension = Utils.getExtension(fileName);
            String mimeType = null;
            if (!Utils.isNullOrEmpty(extension)) {
                mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
            }
            mExternalCopyFile = ext.createFile(mimeType, fileName);
            try {
                mExternalTempFile = File.createTempFile("tmp", String.format(".%s", extension),
                        activity.getFilesDir());
            } catch (Exception e) {
                AnalyticsHandlerAdapter.getInstance().sendException(e);
            }
        }

        public SaveFolderWrapper(File local, String fileName, boolean needsCopy, String suffix) {
            mSelectedFolder = local;
            fileName = (StringUtils.isEmpty(fileName) ? getFileName(needsCopy, suffix) : fileName);
            File tempFile = new File(local, fileName);
            String localCopyPath = tempFile.getAbsolutePath();
            if (mViewerConfig == null) { // only save extra copy if no config
                localCopyPath = Utils.getFileNameNotInUse(localCopyPath);
            }
            mLocalCopyFile = new File(localCopyPath);
        }

        public File getSelectedFolder() {
            return mSelectedFolder;
        }

        public ExternalFileInfo getSelectedExternalFolder() {
            return mSelectedExternalFolder;
        }

        public PDFDoc getDoc() {
            if (mLocalCopyFile != null) {
                copyFileSourceToTempFile(mLocalCopyFile);
            } else if (mExternalTempFile != null) {
                copyFileSourceToTempFile(mExternalTempFile);
            }
            PDFDoc copyDoc = null;
            try {
                if (mLocalCopyFile != null) {
                    copyDoc = new PDFDoc(mLocalCopyFile.getAbsolutePath());
                } else if (getNewExternalUri() != null || mTargetUri != null) {
                    if (mExternalTempFile != null) {
                        copyDoc = new PDFDoc(mExternalTempFile.getAbsolutePath());
                    }
                }
                if (copyDoc != null) {
                    if (null != mPassword) {
                        copyDoc.initStdSecurityHandler(mPassword);
                    }
                }
            } catch (Exception e) {
                AnalyticsHandlerAdapter.getInstance().sendException(e);
                copyDoc = null;
            }
            return copyDoc;
        }

        public Pair<Boolean, String> save(PDFDoc doc) {
            return save(doc, true);
        }

        public Pair<Boolean, String> save(PDFDoc doc, boolean closeDoc) {
            if (getActivity() == null) {
                return null;
            }
            boolean shouldUnlock = false;
            SecondaryFileFilter filter = null;
            try {
                if (mExternalCopyFile != null) {
                    filter = new SecondaryFileFilter(getActivity(), mExternalCopyFile.getUri());
                    doc.lock();
                    shouldUnlock = true;
                    doc.save(filter, SDFDoc.SaveMode.REMOVE_UNUSED);
                    return new Pair<>(false, mExternalCopyFile.getUri().toString()); // boolean: fromLocal
                } else if (mLocalCopyFile != null) {
                    doc.lock();
                    shouldUnlock = true;
                    doc.save(mLocalCopyFile.getAbsolutePath(), SDFDoc.SaveMode.REMOVE_UNUSED, null);
                    return new Pair<>(true, mLocalCopyFile.getAbsolutePath()); // boolean: fromLocal
                } else if (mTargetUri != null) {
                    filter = new SecondaryFileFilter(getActivity(), mTargetUri);
                    doc.lock();
                    shouldUnlock = true;
                    doc.save(filter, SDFDoc.SaveMode.REMOVE_UNUSED);
                    return new Pair<>(false, mTargetUri.toString()); // boolean: fromLocal
                }
            } catch (Exception e) {
                AnalyticsHandlerAdapter.getInstance().sendException(e);
            } finally {
                if (shouldUnlock) {
                    Utils.unlockQuietly(doc);
                }
                if (closeDoc) {
                    Utils.closeQuietly(doc);
                }
                Utils.closeQuietly(filter);
                cleanup();
            }
            return null;
        }

        public void openInNewTab() {
            if (mExternalCopyFile != null) {
                openFileUriInNewTab(mExternalCopyFile.getUri());
            } else if (mTargetUri != null) {
                openFileUriInNewTab(mTargetUri);
            } else {
                openFileInNewTab(mLocalCopyFile);
            }
        }

        public boolean isLocal() {
            return mLocalCopyFile != null;
        }

        public ExternalFileInfo getNewExternalFile() {
            return mExternalCopyFile;
        }

        public Uri getNewExternalUri() {
            return mExternalCopyFile != null ? mExternalCopyFile.getUri() : mTargetUri;
        }

        public File getNewLocalFile() {
            return mLocalCopyFile;
        }

        public Uri getTargetUri() {
            return mTargetUri;
        }

        public String getNewTabTag() {
            if (mExternalCopyFile != null) {
                return mExternalCopyFile.getUri().toString();
            } else if (mTargetUri != null) {
                return mTargetUri.toString();
            } else {
                return mLocalCopyFile != null ? mLocalCopyFile.getAbsolutePath() : null;
            }
        }

        public String getNewTabTitle() {
            if (mExternalCopyFile != null) {
                return mExternalCopyFile.getFileName();
            } else if (mTargetUri != null) {
                Context context = getContext();
                if (context != null) {
                    return Utils.getUriDisplayName(context, mTargetUri);
                }
                return "";
            } else {
                return mLocalCopyFile != null ? mLocalCopyFile.getName() : null;
            }
        }

        public int getNewTabType() {
            if (mExternalCopyFile != null) {
                return BaseFileInfo.FILE_TYPE_EXTERNAL;
            } else if (mTargetUri != null) {
                return BaseFileInfo.FILE_TYPE_EDIT_URI;
            } else {
                return BaseFileInfo.FILE_TYPE_FILE;
            }
        }

        public void cleanup() {
            if (mExternalTempFile != null) {
                //noinspection ResultOfMethodCallIgnored
                mExternalTempFile.delete();
            }
        }
    }

    protected boolean copyFileSourceToTempFile(File tempFile) {
        return copyFileSourceToTempFile(null, tempFile);
    }

    protected boolean copyFileSourceToTempFile(@Nullable File inputFile, File tempFile) {
        OutputStream fos = null;
        try {
            fos = new FileOutputStream(new File(tempFile.getAbsolutePath()));
            return copyFileSourceToOutputStream(getContext(), inputFile, fos);
        } catch (Exception e) {
            AnalyticsHandlerAdapter.getInstance().sendException(e);
            return false;
        } finally {
            IOUtils.closeQuietly(fos);
        }
    }

    protected boolean copyFileSourceToTempUri(Uri tempUri) {
        return copyFileSourceToTempUri(null, tempUri);
    }

    protected boolean copyFileSourceToTempUri(@Nullable File inputFile, Uri tempUri) {
        Context context = getContext();
        if (context == null) {
            return false;
        }

        OutputStream fos = null;
        ContentResolver contentResolver = Utils.getContentResolver(context);
        if (contentResolver == null) {
            return false;
        }
        try {
            fos = contentResolver.openOutputStream(tempUri);
            return copyFileSourceToOutputStream(context, inputFile, fos);
        } catch (Exception e) {
            AnalyticsHandlerAdapter.getInstance().sendException(e);
            return false;
        } finally {
            IOUtils.closeQuietly(fos);
        }
    }

    protected boolean copyFileSourceToOutputStream(Context context, OutputStream fos) {
        return copyFileSourceToOutputStream(context, null, fos);
    }

    protected boolean copyFileSourceToOutputStream(Context context, @Nullable File inputFile, OutputStream fos) {
        if (context == null || fos == null) {
            return false;
        }

        InputStream is = null;
        boolean success = false;

        if (inputFile != null) {
            try {
                is = new FileInputStream(inputFile);
            } catch (Exception e) {
                AnalyticsHandlerAdapter.getInstance().sendException(e);
            }
        } else {
            switch (mTabSource) {
                case BaseFileInfo.FILE_TYPE_EXTERNAL:
                case BaseFileInfo.FILE_TYPE_OFFICE_URI:
                    ContentResolver contentResolver = Utils.getContentResolver(context);
                    if (contentResolver != null && null != mCurrentUriFile) {
                        try {
                            is = contentResolver.openInputStream(mCurrentUriFile);
                        } catch (Exception e) {
                            AnalyticsHandlerAdapter.getInstance().sendException(e);
                        }
                    }
                    break;
                case BaseFileInfo.FILE_TYPE_FILE:
                case BaseFileInfo.FILE_TYPE_EDIT_URI:
                case BaseFileInfo.FILE_TYPE_OPEN_URL:
                    if (null != mCurrentFile) {
                        try {
                            is = new FileInputStream(mCurrentFile);
                        } catch (Exception e) {
                            AnalyticsHandlerAdapter.getInstance().sendException(e);
                        }
                    }
                    break;
            }
        }
        if (null != is) {
            try {
                IOUtils.copy(is, fos);
                success = true;
            } catch (Exception e) {
                AnalyticsHandlerAdapter.getInstance().sendException(e);
            } finally {
                Utils.closeQuietly(is);
            }
        }
        return success;
    }

    /**
     * The overloaded implementation of {@link Fragment#onCreate(Bundle)}.
     **/
    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (sDebug)
            Log.v("LifeCycle", "TabFragment.onCreate");

        super.onCreate(savedInstanceState);
        Activity activity = getActivity();
        if (activity == null) {
            return;
        }
        if (savedInstanceState != null) {
            mOutputFileUri = savedInstanceState.getParcelable(BUNDLE_OUTPUT_FILE_URI);
            mAnnotTargetPoint = savedInstanceState.getParcelable(BUNDLE_IMAGE_STAMP_TARGET_POINT);
            if (savedInstanceState.getBoolean(BUNDLE_ANNOTATION_TOOLBAR_SHOW, false)) {
                mAnnotationToolbarShow = true;
                String mode = savedInstanceState.getString(BUNDLE_ANNOTATION_TOOLBAR_TOOL_MODE, ToolMode.PAN.toString());
                mAnnotationToolbarToolMode = ToolMode.valueOf(mode);
            }
        }

        Bundle bundle = getArguments();
        if (bundle == null) {
            throw new NullPointerException("bundle cannot be null");
        }

        mCacheFolder = bundle.getBoolean(UriCacheManager.BUNDLE_USE_CACHE_FOLDER, true) ? // cache folder by default, otherwise use download folder
                UriCacheManager.getCacheDir(activity) : Utils.getExternalDownloadDirectory(activity);

        mViewerConfig = bundle.getParcelable(BUNDLE_TAB_CONFIG);

        String headerStr = bundle.getString(BUNDLE_TAB_CUSTOM_HEADERS);
        if (headerStr != null) {
            try {
                mCustomHeaders = new JSONObject(headerStr);
            } catch (JSONException ignored) {
            }
        }

        mTabTag = bundle.getString(BUNDLE_TAB_TAG);
        if (Utils.isNullOrEmpty(mTabTag)) {
            throw new NullPointerException("Tab tag cannot be null or empty");
        }

        mTabTitle = bundle.getString(BUNDLE_TAB_TITLE);
        if (mTabTitle != null) {
            mTabTitle = mTabTitle.replaceAll("\\/", "-"); // replace illegal characters in filename
        }

        mFileExtension = bundle.getString(BUNDLE_TAB_FILE_EXTENSION);

        mPassword = bundle.getString(BUNDLE_TAB_PASSWORD);
        if (Utils.isNullOrEmpty(mPassword)) {
            mPassword = Utils.getPassword(activity, mTabTag);
        }

        mTabSource = bundle.getInt(BUNDLE_TAB_ITEM_SOURCE);
        if (mTabSource == BaseFileInfo.FILE_TYPE_FILE) {
            mCurrentFile = new File(mTabTag);
        } else if (mTabSource == BaseFileInfo.FILE_TYPE_EXTERNAL) {
            mCurrentUriFile = Uri.parse(mTabTag);
        }

        mContentLayout = bundle.getInt(BUNDLE_TAB_CONTENT_LAYOUT, getContentLayoutRes());
        mPdfViewCtrlId = bundle.getInt(BUNDLE_TAB_PDFVIEWCTRL_ID, R.id.pdfviewctrl);
        mInitialPage = bundle.getInt(BUNDLE_TAB_INITIAL_PAGE, -1);

        mPreviousPageInfo = new PageBackButtonInfo();
        mCurrentPageInfo = new PageBackButtonInfo();

        mPageViewModel = ViewModelProviders.of(this).get(PageChangeViewModel.class);

        if ((mViewerConfig == null && PdfViewCtrlSettingsManager.getQuickBookmarkCreation(getActivity())) ||
                (mViewerConfig != null && mViewerConfig.isQuickBookmarkCreationEnabled())) {
            mBookmarksViewModel = ViewModelProviders.of(this).get(BookmarksViewModel.class);
        }
    }

    /**
     * The overloaded implementation of {@link Fragment#onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     **/
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (sDebug)
            Log.v("LifeCycle", "TabFragment.onCreateView");

        if (Utils.isNullOrEmpty(mTabTag)) {
            throw new NullPointerException("Tab tag (file path) cannot be null or empty");
        }

        int layoutResId = mContentLayout == 0 ? getContentLayoutRes() : mContentLayout;
        return inflater.inflate(layoutResId, container, false);
    }

    /**
     * The overloaded implementation of {@link Fragment#onViewCreated(View, Bundle)}.
     **/
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if (sDebug)
            Log.v("LifeCycle", "TabFragment.onViewCreated");

        Activity activity = getActivity();
        if (activity == null) {
            return;
        }

        mRootView = view;
        loadPDFViewCtrlView();

        initLayout();

        setViewerHostVisible(false);
        mViewerHost.setBackgroundColor(mPdfViewCtrl.getClientBackgroundColor());

        try {
            if (PdfViewCtrlSettingsManager.getColorManagement(activity) && (mViewerConfig == null || !mViewerConfig.isUseStandardLibrary())) {
                PDFNet.setColorManagement(PDFNet.e_lcms);
            } else {
                PDFNet.setColorManagement(PDFNet.e_no_cms);
            }
            if (mViewerConfig == null) {
                PDFNet.enableJavaScript(PdfViewCtrlSettingsManager.getEnableJavaScript(activity));
            }
        } catch (Exception e) {
            AnalyticsHandlerAdapter.getInstance().sendException(e);
        }
    }

    /**
     * The overloaded implementation of {@link Fragment#onResume()}.
     **/
    @Override
    public void onResume() {
        if (sDebug)
            Log.v("LifeCycle", "TabFragment.onResume");

        super.onResume();

        if (isHidden()) {
            return;
        }

        resumeFragment(true);

        if (mToolManager != null) {
            mToolManager.setCanResumePdfDocWithoutReloading(canResumeWithoutReloading());
        }

        if (mImageSignatureDelayCreation && canResumeWithoutReloading()) {
            mImageSignatureDelayCreation = false;
            consumeImageSignature();
        }
        if (mImageStampDelayCreation && canResumeWithoutReloading()) {
            mImageStampDelayCreation = false;
            ViewerUtils.createImageStamp(getActivity(), mAnnotIntentData, mPdfViewCtrl, mOutputFileUri, mAnnotTargetPoint);
        }
        if (mFileAttachmentDelayCreation && canResumeWithoutReloading()) {
            mFileAttachmentDelayCreation = false;
            ViewerUtils.createFileAttachment(getActivity(), mAnnotIntentData, mPdfViewCtrl, mAnnotTargetPoint);
        }
        if (mSaveFileAttachmentDelay) {
            // we are not touching the current document so can execute right away
            mSaveFileAttachmentDelay = false;
            if (mFileAttachment != null && mAnnotIntentData != null) {
                boolean success = ViewerUtils.exportFileAttachment(mPdfViewCtrl, mFileAttachment, mAnnotIntentData.getData());
                CommonToast.showText(mPdfViewCtrl.getContext(), success ? R.string.file_attachments_saved : R.string.tools_misc_operation_failed);
            }
        }
    }

    /**
     * The overloaded implementation of {@link Fragment#onPause()}.
     **/
    @Override
    public void onPause() {
        if (sDebug)
            Log.v("LifeCycle", "TabFragment.onPause");

        pauseFragment();
        forceSave();

        super.onPause();
    }

    /**
     * The overloaded implementation of {@link Fragment#onStop()}.
     **/
    @Override
    public void onStop() {
        if (sDebug)
            Log.v("LifeCycle", "TabFragment.onStop");

        if (mTabSource == BaseFileInfo.FILE_TYPE_OPEN_URL) {
            if (mDownloading) {
                mDownloading = false;
                // if file opened from openUrl and download is not done yet
                // cancel openUrl
                Utils.closeDocQuietly(mPdfViewCtrl);
                mPdfDoc = null;
                // delete the cache file
                if (mCurrentFile != null && mCurrentFile.exists()) {
                    //noinspection ResultOfMethodCallIgnored
                    mCurrentFile.delete();
                }
            }
        }

        super.onStop();
    }

    /**
     * The overloaded implementation of {@link Fragment#onDestroyView()}.
     **/
    @Override
    public void onDestroyView() {
        if (sDebug)
            Log.v("LifeCycle", "TabFragment.onDestroyView");

        super.onDestroyView();

        // remove listeners
        if (mReflowControl != null && mReflowControl.isReady()) {
            mReflowControl.cleanUp();
            mReflowControl.clearReflowOnTapListeners();
            mReflowControl.clearOnPageChangeListeners();
        }

        if (mToolManager != null) {
            mToolManager.removeToolChangedListener(this);
            mToolManager.removeAnnotationModificationListener(this);
            mToolManager.removePdfDocModificationListener(this);
            mToolManager.removePdfTextModificationListener(this);
        }

        if (mPdfViewCtrl != null) {
            mPdfViewCtrl.removeDocumentLoadListener(this);
            mPdfViewCtrl.removePageChangeListener(this);
            mPdfViewCtrl.removeDocumentDownloadListener(this);
            mPdfViewCtrl.removeUniversalDocumentConversionListener(this);
            mPdfViewCtrl.destroy();
            mPdfViewCtrl = null;
        }

        if (mPdfDoc != null) {
            try {
                mPdfDoc.close();
            } catch (Exception e) {
                AnalyticsHandlerAdapter.getInstance().sendException(e);
            } finally {
                mPdfDoc = null;
            }
        }

        // also remove reference of view so it can be re-created in the next onCreateView
        mStubPDFViewCtrl = null;
        mPasswordLayout = null;
        mReflowControl = null;
        mProgressBarLayout = null;
        mSearchOverlay = null;

        mDisposables.clear();
    }

    /**
     * The overloaded implementation of {@link Fragment#onDestroy()}.
     **/
    @Override
    public void onDestroy() {
        mDestroyCalled = true;
        if (sDebug)
            Log.v("LifeCycle", "TabFragment.onDestroy");

        // cleanup
        if (mTabConversionTempPath != null) {
            File file = new File(mTabConversionTempPath);
            //noinspection ResultOfMethodCallIgnored
            file.delete();
            mTabConversionTempPath = null;
        }

        if (mSaveBackUriDisposable == null) {
            cleanUpTemporaryUriFile();
        }

        super.onDestroy();
    }

    protected abstract @LayoutRes
    int getContentLayoutRes();

    protected abstract void sliderRefreshPageCount();

    protected abstract void sliderUpdateProgress(int curPage);

    protected abstract void sliderSetReversed(boolean reversed);

    protected abstract void sliderSetVisibility(int visibility);

    protected abstract void preparingNavChange();

    protected abstract void postNavChange();

    protected abstract View[] getGenericMotionEnabledViews();

    public abstract boolean isAnnotationMode();

    private void cleanupTemporaryFile() {
        if (mTempDownloadObservable == null) {
            return;
        }
        mTempDownloadObservable.subscribe(new SingleObserver<File>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onSuccess(File file) {
                // delete temp file we created
                if (file != null && file.exists()) {
                    // Delete file
                    String path = file.getAbsolutePath();
                    if (file.delete() && sDebug) {
                        Log.d(TAG, "edit uri temp file deleted: " + path);
                    }
                }
            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG, "Error at: " + e);
            }
        });
    }

    /**
     * The overloaded implementation of {@link Fragment#onHiddenChanged(boolean)}.
     **/
    @Override
    public void onHiddenChanged(boolean hidden) {
        if (sDebug)
            Log.v("LifeCycle", "TabFragment.onHiddenChanged called with " + (hidden ? "Hidden" : "Visible") + " <" + mTabTag + ">");

        if (hidden) {
            pauseFragment();
        } else {
            resumeFragment(false);
        }

        super.onHiddenChanged(hidden);
    }

    /**
     * The overloaded implementation of {@link Fragment#onLowMemory()}.
     **/
    @Override
    public void onLowMemory() {
        super.onLowMemory();

        if (mPdfViewCtrl != null) {
            mPdfViewCtrl.purgeMemoryDueToOOM();
        }
        ImageMemoryCache.getInstance().clearAll();
        PathPool.getInstance().clear();
    }

    @Override
    public void onSaveInstanceState(
            @NonNull Bundle outState
    ) {

        super.onSaveInstanceState(outState);

        if (mOutputFileUri != null) {
            outState.putParcelable(BUNDLE_OUTPUT_FILE_URI, mOutputFileUri);
        }
        if (mAnnotTargetPoint != null) {
            outState.putParcelable(BUNDLE_IMAGE_STAMP_TARGET_POINT, mAnnotTargetPoint);
        }
    }

    /**
     * The overloaded implementation of {@link Fragment#onConfigurationChanged(Configuration)}.
     **/
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Let the PDFViewCtrl know that the orientation
        // changed. This way the tools will also receive this
        // notification.
        if (mPdfViewCtrl != null) {
            mPdfViewCtrl.onConfigurationChanged(newConfig);
            updateZoomLimits();
        }
    }

    /**
     * Event called when the tool changes.
     *
     * @param newTool the new tool
     * @param oldTool the old tool
     */
    @Override
    public void toolChanged(ToolManager.Tool newTool, ToolManager.Tool oldTool) {
        mRageScrollingCount = 0; // reset count
    }

    @Override
    public void fileCreated(String filePath, AnnotAction action) {
        switch (action) {
            case SCREENSHOT_CREATE:
                mScreenshotTempFileCreated = true;
                mScreenshotTempFilePath = filePath;
                mToolManager.deselectAll();
                Intent shareIntent = Utils.createShareIntentForFile(getContext(), filePath, "image/png");
                startActivity(Intent.createChooser(shareIntent, getResources().getText(R.string.tools_screenshot_share_intent_title)));
                break;
        }
    }

    /**
     * Called when a file attachment has been selected.
     *
     * @param attachment The file attachment
     */
    @Override
    public void fileAttachmentSelected(FileAttachment attachment) {
        Activity activity = getActivity();
        if (activity == null) {
            return;
        }
        if (attachment == null) {
            return;
        }

        if (mPdfViewCtrl == null) {
            return;
        }

        String attachmentPath = ViewerUtils.exportFileAttachment(mPdfViewCtrl, attachment, getExportDirectory());
        if (null == attachmentPath) {
            return;
        }

        File attachmentFile = new File(attachmentPath);
        String extension = Utils.getExtension(attachmentPath);
        if (Utils.isExtensionHandled(extension)) {
            openFileInNewTab(attachmentFile);
        } else {
            Uri uri = Utils.getUriForFile(activity, attachmentFile);
            if (uri != null) {
                Utils.shareGenericFile(activity, uri);
            }
        }
    }

    @Override
    public void freehandStylusUsedFirstTime() {

    }

    /**
     * Called when a location has been selected for adding the image stamp.
     *
     * @param targetPoint The target location to add the image stamp
     */
    @Override
    public void imageStamperSelected(PointF targetPoint) {
        mImageCreationMode = ToolMode.STAMPER;
        mAnnotTargetPoint = targetPoint;
        mOutputFileUri = ViewerUtils.openImageIntent(this);
    }

    @Override
    public void imageSignatureSelected(PointF targetPoint, int targetPage, Long widget) {
        mImageCreationMode = ToolMode.SIGNATURE;
        mAnnotTargetPoint = targetPoint;
        mAnnotTargetPage = targetPage;
        mTargetWidget = widget;
        mOutputFileUri = ViewerUtils.openImageIntent(this);
    }

    @Override
    public void attachFileSelected(PointF targetPoint) {
        mAnnotTargetPoint = targetPoint;
        ViewerUtils.openFileIntent(this);
    }

    /**
     * Called when free text inline editing has started.
     */
    @Override
    public void freeTextInlineEditingStarted() {

    }

    @Override
    public boolean newFileSelectedFromTool(String filePath, int pageNumber) {
        if (Utils.isNullOrEmpty(filePath)) {
            return false;
        }
        File file = new File(filePath);
        if (file.exists()) {
            openFileInNewTab(file, "");
            return true;
        }
        // could also be a relative link to a file
        if (mCurrentFile != null) {
            File parent = mCurrentFile.getParentFile();
            File newFile = new File(parent, filePath);
            if (newFile.exists() && !mCurrentFile.equals(newFile)) {
                openFileInNewTab(newFile, "", pageNumber);
                return true;
            }
        }
        if (mCurrentUriFile != null) {
            Activity activity = getActivity();
            if (activity != null) {
                ExternalFileInfo fileInfo = Utils.buildExternalFile(activity, mCurrentUriFile);
                if (fileInfo != null) {
                    ExternalFileInfo parent = fileInfo.getParent();
                    if (parent != null) {
                        ExternalFileInfo newFile = parent.getFile(filePath);
                        if (newFile != null && newFile.exists() && !mCurrentUriFile.equals(newFile.getUri())) {
                            openFileUriInNewTab(newFile.getUri(), "", pageNumber);
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    @Override
    public void onSaveFileAttachmentSelected(FileAttachment fileAttachment, Intent intent) {
        if (intent != null) {
            mFileAttachment = fileAttachment;
            startActivityForResult(intent, RequestCode.CREATE_FILE_IN_SYSTEM);
        }
    }

    /**
     * Handles changes the page number.
     *
     * @param old_page the old page number
     * @param cur_page the current (new) page number
     * @param state    in non-continuous page presentation modes and when the
     *                 built-in page sliding is in process, this flag is used to
     *                 indicate the state of page change.
     */
    @Override
    public void onPageChange(int old_page, int cur_page, PDFViewCtrl.PageChangeState state) {
        Activity activity = getActivity();
        if (activity == null || mPdfViewCtrl == null) {
            return;
        }

        mRageScrollingCount = 0; // reset count

        boolean shouldUnlockRead = false;
        try {
            mPdfViewCtrl.docLockRead();
            shouldUnlockRead = true;
            mPageCount = mPdfViewCtrl.getDoc().getPageCount();
        } catch (Exception e) {
            AnalyticsHandlerAdapter.getInstance().sendException(e);
        } finally {
            if (shouldUnlockRead) {
                mPdfViewCtrl.docUnlockRead();
            }
        }

        updatePageIndicator();

        ///////////////////         Reflow        ///////////////////
        if (mIsReflowMode && mReflowControl != null) {
            try {
                mReflowControl.setCurrentPage(cur_page);
            } catch (Exception e) {
                AnalyticsHandlerAdapter.getInstance().sendException(e);
            }
        }

        ///////////    Page Back and Forward Buttons     ////////////
        // store current and previous page info
        mPreviousPageInfo.copyPageInfo(mCurrentPageInfo);
        mCurrentPageInfo = getCurrentPageInfo();

        // after a page change, push the current page onto the stack
        if (mPushNextPageOnStack && state == PDFViewCtrl.PageChangeState.END) {
            // if  top element of the back stack's page number does not equal the current page
            // (element we wish to push onto the back stack)
            if (mPageBackStack.isEmpty() || mPageBackStack.peek().pageNum != mPdfViewCtrl.getCurrentPage()) {
                // if the stack is at it's maximum size, then delete the bottom item of the stack
                if (mPageBackStack.size() >= MAX_SIZE_PAGE_BACK_BUTTON_STACK) {
                    mPageBackStack.removeLast();
                }

                mPageBackStack.push(getCurrentPageInfo());
                mPushNextPageOnStack = false;

                // if an element is pushed onto the back page stack we have
                // to clear the forward page stack (if it isn't empty already).
                if (!mPageForwardStack.isEmpty()) {
                    mPageForwardStack.clear();
                }
            }
        }

        // initial time set mPreviousPageInfo equal to mCurrentPageInfo
        if (mPreviousPageInfo.pageNum < 0) {
            mPreviousPageInfo.copyPageInfo(mCurrentPageInfo);
        }

        // if an internal link is clicked, call setCurrentPageHelper to
        // pass the event to the page back and forward buttons
        if (mInternalLinkClicked) {
            setCurrentPageHelper(cur_page, false, getCurrentPageInfo());
            mInternalLinkClicked = false;
        }

        ///////////    FreeText restore cache     ////////////
        if (mWaitingForSetPage && mWaitingForSetPageNum == cur_page) {
            mWaitingForSetPage = false;
            mWaitingForSetPageNum = -1;
            restoreFreeText();
        }

        // Update view model with new page
        if (mPageViewModel != null) {
            mPageViewModel.onPageChange(new PageState(cur_page));
        }

        // Update annot snapping cache
        if (mToolManager != null) {
            AnnotSnappingManager annotSnappingManager = mToolManager.getAnnotSnappingManager();
            annotSnappingManager.tryUpdateCache(mPdfViewCtrl, false);
        }
    }

    /**
     * Handles terminating document load.
     */
    @Override
    public void onDocumentLoaded() {
        if (getActivity() == null || mPdfViewCtrl == null) {
            return;
        }

        doDocumentLoaded();
    }

    /**
     * Handles an update during download.
     *
     * @param state           the state of the update.
     * @param page_num        the number of the page that was just downloaded. Meaningful
     *                        if type is {@link PDFViewCtrl.DownloadState#PAGE}.
     * @param page_downloaded the total number of pages that have been downloaded
     * @param page_count      the page count of the associated document
     * @param message         error message in case the download has failed
     */
    @Override
    public void onDownloadEvent(PDFViewCtrl.DownloadState state, int page_num, int page_downloaded, int page_count, String message) {
        Activity activity = getActivity();
        if (activity == null || mPdfViewCtrl == null) {
            return;
        }
        mDownloadState = state;
        Button retryBtn = mRootView.findViewById(R.id.downloader_retry_fab);
        retryBtn.setVisibility(View.GONE);

        if (mDocumentConversion != null && sDebug) {
            Log.e("UNIVERSAL SEQUENCE", "Got downloaded event of type " + state +
                    " even though it should be a conversion.");
        }

        switch (state) {
            case PAGE:
            case FINISHED:
                if (mPageCount != page_count) {
                    mPageCount = page_count;
                    sliderRefreshPageCount();
                }
                if (state == PDFViewCtrl.DownloadState.FINISHED) {
                    mDownloading = false;
                    CommonToast.showText(activity, R.string.download_finished_message, Toast.LENGTH_SHORT);
                    stopConversionSpinningIndicator();
                    if (mCurrentFile != null) {
                        if (Utils.isNotPdf(mCurrentFile.getAbsolutePath())) {
                            openOfficeDoc(mCurrentFile.getAbsolutePath(), false);
                            return;
                        }
                        try {
                            mPdfDoc = new PDFDoc(mCurrentFile.getAbsolutePath());
                        } catch (Exception e) {
                            // if the cache file was not saved properly, let's try to save it again here...
                            boolean shouldUnlock = false;
                            try {
                                mPdfViewCtrl.docLock(true);
                                shouldUnlock = true;
                                mPdfViewCtrl.getDoc().save(mCurrentFile.getAbsolutePath(), SDFDoc.SaveMode.REMOVE_UNUSED, null);
                                mPdfDoc = new PDFDoc(mCurrentFile.getAbsolutePath());
                            } catch (Exception e2) {
                                mPdfDoc = null;
                                AnalyticsHandlerAdapter.getInstance().sendException(e);
                            } finally {
                                if (shouldUnlock) {
                                    mPdfViewCtrl.docUnlock();
                                }
                            }
                        }
                        boolean error = false;
                        if (mPdfDoc != null) {
                            try {
                                String oldTabTag = mTabTag;
                                int oldTabSource = mTabSource;
                                mTabTag = mCurrentFile.getAbsolutePath();
                                mTabSource = BaseFileInfo.FILE_TYPE_FILE;
                                mTabTitle = FilenameUtils.removeExtension(new File(mTabTag).getName());
                                mFileExtension = "pdf";
                                if (!mTabTag.equals(oldTabTag) || mTabSource != oldTabSource) {
                                    if (mTabListener != null) {
                                        mTabListener.onTabIdentityChanged(oldTabTag, mTabTag, mTabTitle, mFileExtension, mTabSource);
                                    }
                                }
                                ToolManager.Tool currentTool = mToolManager.getTool();
                                int currentPage = mPdfViewCtrl.getCurrentPage();
                                mToolManager.setReadOnly(false);
                                checkPdfDoc();
                                mToolManager.setTool(currentTool);
                                mPdfViewCtrl.setCurrentPage(currentPage);
                            } catch (Exception e) {
                                error = true;
                                AnalyticsHandlerAdapter.getInstance().sendException(e, "checkPdfDoc");
                            }
                        } else {
                            error = true;
                        }

                        if (error) {
                            handleOpeningDocumentFailed(PdfDocManager.DOCUMENT_SETDOC_ERROR_NULL_PDFDOC);
                            return;
                        } else {
                            // download is done and successful
                            // add this item to tab list and recent list
                            PdfViewCtrlTabInfo info = new PdfViewCtrlTabInfo();
                            info.tabTitle = mTabTitle;
                            info.tabSource = BaseFileInfo.FILE_TYPE_FILE;
                            info.fileExtension = mFileExtension;
                            // set page presentation mode to the last chosen default
                            String mode = PdfViewCtrlSettingsManager.getViewMode(activity);
                            info.pagePresentationMode = getPagePresentationModeFromSettings(mode).getValue();

                            PdfViewCtrlTabsManager.getInstance().addPdfViewCtrlTabInfo(activity, mCurrentFile.getAbsolutePath(), info);
                            addToRecentList(info);

                            // if it is downloaded and added to tab manager successfully then we
                            // need to check if max tabs count is reached
                            if (mTabListener != null) {
                                mTabListener.onDownloadedSuccessful();
                            }
                        }
                    }
                }
                break;
            case FAILED:
                if (sDebug)
                    Log.d(TAG, "DOWNLOAD_FAILED: " + message);
                if (mDownloadDocumentDialog != null && mDownloadDocumentDialog.isShowing()) {
                    mDownloadDocumentDialog.dismiss();
                }
                stopConversionSpinningIndicator();

                // first check if it failed due to internet connection
                if (!Utils.hasInternetConnection(activity)) {
                    CommonToast.showText(activity, R.string.download_failed_no_internet_message, Toast.LENGTH_SHORT);

                    retryBtn = mRootView.findViewById(R.id.downloader_retry_fab);
                    retryBtn.setVisibility(View.VISIBLE);
                    retryBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (Utils.hasInternetConnection(v.getContext())) {
                                // let's try again
                                Utils.closeDocQuietly(mPdfViewCtrl);
                                openUrlFile(mTabTag);
                            } else {
                                CommonToast.showText(v.getContext(), R.string.download_failed_no_internet_message, Toast.LENGTH_SHORT);
                            }
                        }
                    });
                } else {
                    if ((mViewerConfig == null || mViewerConfig.isOpenUrlPasswordCheckEnabled()) &&
                            message != null && (message.toLowerCase().contains("password") || message.toLowerCase().contains("encrypted"))) {
                        handleEncryptedPdf(activity);
                    } else if (message != null && !message.equals("cancelled")) {
                        CommonToast.showText(activity, R.string.download_failed_message, Toast.LENGTH_SHORT);
                        mErrorCode = PdfDocManager.DOCUMENT_SETDOC_ERROR_NULL_PDFDOC;
                        handleOpeningDocumentFailed(mErrorCode);
                    }
                }
                break;
        }
    }

    /**
     * Handles when {@link com.pdftron.pdf.PDFViewCtrl} starts rendering.
     */
    @Override
    public void onRenderingStarted() {

    }

    /**
     * Handles when {@link com.pdftron.pdf.PDFViewCtrl} finishes rendering.
     */
    @Override
    public void onRenderingFinished() {
        if (mDownloadDocumentDialog != null && mDownloadDocumentDialog.isShowing() &&
                (mDownloadState == PDFViewCtrl.DownloadState.PAGE || mDownloadState == PDFViewCtrl.DownloadState.FINISHED)) {
            mDownloadDocumentDialog.dismiss();
        }
    }

    /**
     * Handles an update during document conversion.
     *
     * @param state               - the state of update.
     * @param totalPagesConverted The total number of pages converted so far. Only relevant
     *                            for the @link #CONVERSION_PROGRESS state. Note that pages
     *                            can be processed in batches, and so the number might not
     */
    @Override
    public void onConversionEvent(PDFViewCtrl.ConversionState state, int totalPagesConverted) {
        Activity activity = getActivity();
        if (activity == null || mPdfViewCtrl == null) {
            return;
        }

        switch (state) {
            case PROGRESS:
                if (mPdfDoc == null) {
                    mPdfDoc = mPdfViewCtrl.getDoc();
                }
                mPageCount = totalPagesConverted;
                if (mPageCount > 0 && !mUsedCacheCalled) {
                    if (mViewerConfig == null || !mViewerConfig.isUseStandardLibrary()) {
                        initRecentlyUsedCache();
                        RecentlyUsedCache.accessDocument(mTabTag, mPdfViewCtrl.getDoc());
                    }
                    mUsedCacheCalled = true;
                }
                sliderRefreshPageCount();
                updatePageIndicator();
                if (!mIsPageNumberIndicatorConversionSpinningRunning) {
                    mIsPageNumberIndicatorConversionSpinningRunning = mPageNumberIndicatorConversionSpinningHandler.
                            postDelayed(mPageNumberIndicatorConversionSpinnerRunnable, 1000);
                }
                break;
            case FINISHED:
                mDocumentLoading = false;
                if (mShouldNotifyWhenConversionFinishes) {
                    CommonToast.showText(activity, R.string.open_universal_succeeded, Toast.LENGTH_SHORT, Gravity.CENTER, 0, 0);
                }

                mIsOfficeDocReady = true;

                mDocumentConversion = null;
                mDocumentState = PdfDocManager.DOCUMENT_STATE_FROM_CONVERSION;

                stopConversionSpinningIndicator();

                // save a temp copy
                saveConversionTempCopy();
                break;
            case FAILED:
                if (sDebug) {
                    if (mDocumentConversion != null) {
                        try {
                            Log.e(TAG, mDocumentConversion.getErrorString());
                        } catch (PDFNetException e) {
                            e.printStackTrace();
                        }
                    }
                }

                stopConversionSpinningIndicator();
                break;
        }
    }

    /**
     * Handles when a blank page without content is added to the document
     * as a placeholder.
     */
    @Override
    public void onAddProgressIndicator() {
        if (mPdfViewCtrl == null) {
            return;
        }

        if (mUniversalDocSpinner != null && mPdfViewCtrl.indexOfChild(mUniversalDocSpinner) >= 0) {
            mPdfViewCtrl.removeView(mUniversalDocSpinner);
        }

        mUniversalDocSpinner = new ProgressBar(mPdfViewCtrl.getContext());
        mUniversalDocSpinner.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        int width = mUniversalDocSpinner.getMeasuredWidth();
        if (width > 0) {
            mSpinnerSize = width;
        }
        mUniversalDocSpinner.setIndeterminate(true);
        mUniversalDocSpinner.setVisibility(View.INVISIBLE);
        mPdfViewCtrl.addView(mUniversalDocSpinner);
    }

    /**
     * Handles when the position of the blank page without content moves.
     *
     * @param position position
     */
    @Override
    public void onPositionProgressIndicatorPage(Rect position) {
        if (mUniversalDocSpinner != null) {
            try {
                int spinnerSize = mSpinnerSize;
                if (spinnerSize > position.getWidth()) {
                    spinnerSize = (int) position.getWidth();
                }
                if (spinnerSize > position.getHeight()) {
                    spinnerSize = (int) position.getHeight();
                }
                int halfX = (int) (position.getX1() + position.getX2()) / 2;
                int halfY = (int) (position.getY1() + position.getY2()) / 2;
                halfX -= spinnerSize / 2;
                halfY -= spinnerSize / 2;

                mUniversalDocSpinner.layout(halfX, halfY, halfX + spinnerSize, halfY + spinnerSize);
            } catch (Exception e) {
                AnalyticsHandlerAdapter.getInstance().sendException(e);
            }
        }
    }

    /**
     * Handles when the blank page without content enters or leaves the screen.
     *
     * @param isVisible whether visible or not
     */
    @Override
    public void onProgressIndicatorPageVisibilityChanged(boolean isVisible) {
        if (mUniversalDocSpinner != null) {
            if (isVisible) {
                mUniversalDocSpinner.setVisibility(View.VISIBLE);
            } else {
                mUniversalDocSpinner.setVisibility(View.INVISIBLE);
            }
        }
    }

    /**
     * Handles when a blank page without content is removed from the document.
     */
    @Override
    public void onRemoveProgressIndicator() {
        if (mUniversalDocSpinner != null && mPdfViewCtrl != null &&
                mPdfViewCtrl.indexOfChild(mUniversalDocSpinner) >= 0) {
            mPdfViewCtrl.removeView(mUniversalDocSpinner);
        }
    }

    /**
     * Handles when it user is trying to access the next page of the PDFViewCtrl but there is no
     * room for the visual progress indicator in the viewer. For example in single page mode.
     */
    @Override
    public void onShowContentPendingIndicator() {
        if (sDebug)
            Log.i("UNIVERSAL PROGRESS", "Told to show content pendering indicator");
//        mConversionInProgressNotification.show();
    }

    /**
     * Handles when the content on the page the user was previously trying to access is
     * accessible.
     */
    @Override
    public void onRemoveContentPendingIndicator() {
        if (sDebug)
            Log.i("UNIVERSAL PROGRESS", "Told to hide content pendering indicator");
//        mConversionInProgressNotification.hide(true);
    }

    /**
     * Handles generic motion events.
     *
     * @param event The generic motion event being processed.
     */
    @Override
    public void onGenericMotionEvent(MotionEvent event) {
        if (!Utils.isNougat()) {
            return;
        }

        // If stylus is used, then force mToolbarOpenedFromMouseMovement = false. This will prevent
        // the stylus from ever opening and closing the toolbar as this causes strange behavior.
        if (getToolManager() != null) {
            boolean isStylus = event.getPointerCount() == 1 && event.getToolType(0) == MotionEvent.TOOL_TYPE_STYLUS;
            if (isStylus) {
                mToolbarOpenedFromMouseMovement = false;
            }
        }

        final View view = getView();
        if (view == null || mPdfViewCtrl == null || mToolManager == null || mToolManager.getTool() == null) {
            return;
        }
        Tool tool = (Tool) mToolManager.getTool();
        ToolMode mode = ToolManager.getDefaultToolMode(tool.getToolMode());
        float x = event.getX();
        float y = event.getY();

        float threshold = 2.0f;
        if (y < threshold) {
            if (mTabListener != null) {
                mTabListener.setViewerOverlayUIVisible(true);
                mToolbarOpenedFromMouseMovement = true;
            }
        } else {
            if (mTabListener != null && mToolbarOpenedFromMouseMovement) {
                int height = mTabListener.getToolbarHeight();
                if (y > (height + threshold)) {
                    if (mTabListener != null && tool instanceof Pan) {
                        mTabListener.setViewerOverlayUIVisible(false);
                    }
                    mToolbarOpenedFromMouseMovement = false;
                }
            }
        }

        Context context = getContext();
        if (context == null) {
            return;
        }
        if (tool.isInsideQuickMenu(x, y)) {
            view.setPointerIcon(PointerIcon.getSystemIcon(context, PointerIcon.TYPE_ARROW));
            return;
        }

        if (mode == ToolMode.ANNOT_EDIT) {
            AnnotEdit annotEdit = (AnnotEdit) tool;
            if (annotEdit.isCtrlPtsHidden()) {
                view.setPointerIcon(PointerIcon.getSystemIcon(context, PointerIcon.TYPE_ARROW));
            } else {
                int effectCtrlPointId = annotEdit.getEffectCtrlPointId(x + mPdfViewCtrl.getScrollX(), y + mPdfViewCtrl.getScrollY());
                switch (effectCtrlPointId) {
                    case AnnotEdit.e_ll:
                    case AnnotEdit.e_ur:
                        view.setPointerIcon(PointerIcon.getSystemIcon(getContext(), PointerIcon.TYPE_TOP_RIGHT_DIAGONAL_DOUBLE_ARROW));
                        break;
                    case AnnotEdit.e_lr:
                    case AnnotEdit.e_ul:
                        view.setPointerIcon(PointerIcon.getSystemIcon(getContext(), PointerIcon.TYPE_TOP_LEFT_DIAGONAL_DOUBLE_ARROW));
                        break;
                    case AnnotEdit.e_ml:
                    case AnnotEdit.e_mr:
                        view.setPointerIcon(PointerIcon.getSystemIcon(getContext(), PointerIcon.TYPE_HORIZONTAL_DOUBLE_ARROW));
                        break;
                    case AnnotEdit.e_lm:
                    case AnnotEdit.e_um:
                        view.setPointerIcon(PointerIcon.getSystemIcon(getContext(), PointerIcon.TYPE_VERTICAL_DOUBLE_ARROW));
                        break;
                    case AnnotEdit.e_moving:
                        view.setPointerIcon(PointerIcon.getSystemIcon(getContext(), PointerIcon.TYPE_ALL_SCROLL));
                        break;
                    default:
                        view.setPointerIcon(PointerIcon.getSystemIcon(getContext(), PointerIcon.TYPE_ARROW));
                        break;
                }
            }
            return;
        }

        if (mode == ToolMode.ANNOT_EDIT_LINE) {
            AnnotEditLine annotEditLine = (AnnotEditLine) tool;
            int effectCtrlPointId = annotEditLine.getEffectCtrlPointId(x + mPdfViewCtrl.getScrollX(), y + mPdfViewCtrl.getScrollY());
            if (effectCtrlPointId == 2) {
                view.setPointerIcon(PointerIcon.getSystemIcon(getContext(), PointerIcon.TYPE_ALL_SCROLL));
            } else {
                view.setPointerIcon(PointerIcon.getSystemIcon(getContext(), PointerIcon.TYPE_ARROW));
            }
            return;
        }

        if (mode == ToolMode.TEXT_SELECT) {
            TextSelect textSelect = (TextSelect) tool;
            if (textSelect.hitTest(x + mPdfViewCtrl.getScrollX(), y + mPdfViewCtrl.getScrollY()) >= 0) {
                view.setPointerIcon(PointerIcon.getSystemIcon(getContext(), PointerIcon.TYPE_HORIZONTAL_DOUBLE_ARROW));
                return;
            }
        }

        if (mode == ToolMode.TEXT_UNDERLINE || mode == ToolMode.TEXT_HIGHLIGHT
                || mode == ToolMode.TEXT_SQUIGGLY || mode == ToolMode.TEXT_STRIKEOUT) {
            view.setPointerIcon(PointerIcon.getSystemIcon(getContext(), PointerIcon.TYPE_TEXT));
            return;
        }

        boolean buttonPressed = event.isButtonPressed(MotionEvent.BUTTON_PRIMARY) || event.isButtonPressed(MotionEvent.BUTTON_TERTIARY);

        if (mode != ToolMode.PAN || tool.getNextToolMode() != ToolMode.PAN) {
            if (mode != ToolMode.TEXT_SELECT) {
                view.setPointerIcon(PointerIcon.getSystemIcon(getContext(), PointerIcon.TYPE_ARROW));
                return;
            }
            if (buttonPressed) {
                return;
            }
        }

        boolean shouldUnlockRead = false;
        try {
            mPdfViewCtrl.docLockRead();
            shouldUnlockRead = true;
            //noinspection ConstantConditions
            do {
                int pageNum = mPdfViewCtrl.getPageNumberFromScreenPt(x, y);
                boolean isTextUnderMouse = false;
                boolean isAnnotUnderMouse = false;

                if (pageNum > 0) {
                    if (mPdfViewCtrl.wereWordsPrepared(pageNum)) {
                        if (mPdfViewCtrl.isThereTextInRect(x - 1, y - 1, x + 1, y + 1)) {
                            isTextUnderMouse = true;
                        }
                    } else {
                        mPdfViewCtrl.prepareWords(pageNum);
                    }

                    if (mPdfViewCtrl.wereAnnotsForMousePrepared(pageNum)) {
                        if (mPdfViewCtrl.getAnnotTypeUnder(x, y) != Annot.e_Unknown) {
                            isAnnotUnderMouse = true;
                        }
                    } else {
                        mPdfViewCtrl.prepareAnnotsForMouse(pageNum);
                    }
                }

                if (isAnnotUnderMouse) {
                    view.setPointerIcon(PointerIcon.getSystemIcon(getContext(), PointerIcon.TYPE_HAND));
                } else if (isTextUnderMouse || mode == ToolMode.TEXT_SELECT) {
                    view.setPointerIcon(PointerIcon.getSystemIcon(getContext(), PointerIcon.TYPE_TEXT));
                } else if (buttonPressed) {
                    view.setPointerIcon(PointerIcon.getSystemIcon(getContext(), PointerIcon.TYPE_GRABBING));
                } else {
                    view.setPointerIcon(PointerIcon.getSystemIcon(getContext(), PointerIcon.TYPE_ARROW));
                }
            } while (false);
        } catch (Exception e) {
            AnalyticsHandlerAdapter.getInstance().sendException(e);
        } finally {
            if (shouldUnlockRead) {
                mPdfViewCtrl.docUnlockRead();
            }
        }
    }

    /**
     * The overloaded implementation of {@link ToolManager.PreToolManagerListener#onChangePointerIcon(PointerIcon)}.
     **/
    @Override
    public void onChangePointerIcon(PointerIcon pointerIcon) {
        if (Utils.isNougat() && getView() != null) {
            getView().setPointerIcon(pointerIcon);
        }
    }

    /**
     * The overloaded implementation of {@link ToolManager.PreToolManagerListener#onSingleTapConfirmed(MotionEvent)}.
     **/
    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        Activity activity = getActivity();
        if (activity == null || mPdfViewCtrl == null) {
            return false;
        }
        mRageScrollingCount = 0; // reset count

        int x = (int) (e.getX() + 0.5);
        int y = (int) (e.getY() + 0.5);

        if (mToolManager != null &&
                mToolManager.getTool() != null &&
                (mToolManager.getTool() instanceof Pan)) {
            boolean hasAnnotation = false;
            boolean hasLink = false;

            boolean shouldUnlockRead = false;
            try {
                mPdfViewCtrl.docLockRead();
                shouldUnlockRead = true;
                mSelectedAnnot = mPdfViewCtrl.getAnnotationAt(x, y);
                PDFViewCtrl.LinkInfo linkInfo = mPdfViewCtrl.getLinkAt(x, y);
                if (mSelectedAnnot != null && mSelectedAnnot.isValid()) {
                    hasAnnotation = true;
                }
                if (linkInfo != null) {
                    hasLink = true;
                }
                if (hasAnnotation && mSelectedAnnot.getType() == Annot.e_Link) {
                    hasLink = true;
                }
            } catch (Exception ex) {
                AnalyticsHandlerAdapter.getInstance().sendException(ex);
            } finally {
                if (shouldUnlockRead) {
                    mPdfViewCtrl.docUnlockRead();
                }
            }

            // handle read-only file
            // disable annotation edit except link
            if (hasLink) {
                // if anything is currently selected
                // deselect it before any further action
                return mToolManager.isQuickMenuJustClosed();
            }

            if (hasAnnotation) {
                handleSpecialFile();
            } else {
                // If we tapped an annotation, then return false and let the
                // tools process the event. Otherwise, we consume the event.
                if (!mToolManager.isQuickMenuJustClosed()) {
                    // if anything is currently selected
                    // deselect it before any further action
                    boolean handled = false;
                    // Check if tapped area should navigate to other pages.
                    RegionSingleTap region = getRegionTap(x, y);
                    if (isSinglePageMode() && region != RegionSingleTap.Middle) {
                        boolean allowPageChangeOnTapEnabled = PdfViewCtrlSettingsManager.getAllowPageChangeOnTap(activity);
                        if (allowPageChangeOnTapEnabled) {
                            boolean checkNext = false;
                            boolean checkPrevious = false;
                            if (region == RegionSingleTap.Left) {
                                if (isRtlMode()) {
                                    checkNext = true;
                                } else {
                                    checkPrevious = true;
                                }
                            } else if (region == RegionSingleTap.Right) {
                                if (isRtlMode()) {
                                    checkPrevious = true;
                                } else {
                                    checkNext = true;
                                }
                            }
                            if (checkPrevious) {
                                if (mPdfViewCtrl.canGotoPreviousPage()) {
                                    mPdfViewCtrl.gotoPreviousPage(PdfViewCtrlSettingsManager.getAllowPageChangeAnimation(activity));
                                    handled = true;
                                }
                            } else if (checkNext) {
                                if (mPdfViewCtrl.canGotoNextPage()) {
                                    mPdfViewCtrl.gotoNextPage(PdfViewCtrlSettingsManager.getAllowPageChangeAnimation(activity));
                                    handled = true;
                                }
                            }
                        }
                    }
                    if (!handled) {
                        if (mTabListener != null) {
                            mTabListener.onTabSingleTapConfirmed();
                        }
                    }
                }
                return true;
            }
        }
        return false;
    }

    /**
     * The overloaded implementation of {@link ToolManager.PreToolManagerListener#onMove(MotionEvent, MotionEvent, float, float)}.
     **/
    @Override
    public boolean onMove(MotionEvent e1, MotionEvent e2, float x_dist, float y_dist) {
        return false;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        mOnUpCalled = false;
        mSwipeDetector.handleOnDown(e);
        return false;
    }

    /**
     * The overloaded implementation of {@link ToolManager.PreToolManagerListener#onUp(MotionEvent, PDFViewCtrl.PriorEventMode)}.
     **/
    @Override
    public boolean onUp(MotionEvent e, PDFViewCtrl.PriorEventMode priorEventMode) {
        mSwipeDetector.handleOnUp(e);
        if (mPdfViewCtrl != null && priorEventMode == PDFViewCtrl.PriorEventMode.FLING) {
            // if not in pan mode, ignore
            // detect rage scrolling
            if (mToolManager != null &&
                    mToolManager.getTool() instanceof Pan &&
                    mPdfViewCtrl.getWidth() == mPdfViewCtrl.getViewCanvasWidth() &&
                    !mOnUpCalled) {
                mOnUpCalled = true;

                if (mSwipeDetector.isHorizontalSwipe() || mSwipeDetector.isVerticalSwipe()) {
                    // single page mode and not zoomed in
                    mRageScrollingCount++;

                    if (mRageScrollingCount >= RAGE_SCROLLING_COUNT_MAX) {
                        mRageScrollingCount = 0; // reset count
                        handleRageScrolling();
                    }
                }
            }
        }
        if (priorEventMode != PDFViewCtrl.PriorEventMode.FLING) {
            mRageScrollingCount = 0; // reset count
        }
        return false;
    }

    /**
     * The overloaded implementation of {@link ToolManager.PreToolManagerListener#onScaleBegin(float, float)}.
     **/
    @Override
    public boolean onScaleBegin(float x, float y) {
        mScaling = true;
        setThumbSliderVisible(false, false);
        return false;
    }

    /**
     * The overloaded implementation of {@link ToolManager.PreToolManagerListener#onScale(float, float)}.
     **/
    @Override
    public boolean onScale(float x, float y) {
        mRageScrollingCount = 0; // reset count
        return false;
    }

    /**
     * The overloaded implementation of {@link ToolManager.PreToolManagerListener#onScaleEnd(float, float)}.
     **/
    @Override
    public boolean onScaleEnd(float x, float y) {
        mScaling = false;
        return false;
    }

    /**
     * The overloaded implementation of {@link ToolManager.PreToolManagerListener#onLongPress(MotionEvent)}.
     **/
    @Override
    public boolean onLongPress(MotionEvent e) {
        return false;
    }

    /**
     * The overloaded implementation of {@link ToolManager.PreToolManagerListener#onScrollChanged(int, int, int, int)}.
     **/
    @Override
    public void onScrollChanged(int l, int t, int oldl, int oldt) {
    }

    /**
     * The overloaded implementation of {@link ToolManager.PreToolManagerListener#onDoubleTap(MotionEvent)}.
     **/
    @Override
    public boolean onDoubleTap(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        // non-CTRL-based
        return handleKeyUp(keyCode, event);
    }

    /**
     * The overloaded implementation of {@link ToolManager.QuickMenuListener#onQuickMenuClicked(QuickMenuItem)}.
     **/
    @Override
    public boolean onQuickMenuClicked(QuickMenuItem menuItem) {
        boolean result = false;
        if (mQuickMenuListeners != null) {
            for (ToolManager.QuickMenuListener listener : mQuickMenuListeners) {
                if (listener.onQuickMenuClicked(menuItem)) {
                    result = true;
                }
            }
        }
        mToolManager.setQuickMenuJustClosed(false); // next tap brings the toolbar/widgets up
        return result;
    }

    @Override
    public boolean onShowQuickMenu(QuickMenu quickMenu, Annot annot) {
        boolean result = false;
        if (mQuickMenuListeners != null) {
            for (ToolManager.QuickMenuListener listener : mQuickMenuListeners) {
                if (listener.onShowQuickMenu(quickMenu, annot)) {
                    result = true;
                }
            }
        }
        return result;
    }

    /**
     * The overloaded implementation of {@link ToolManager.QuickMenuListener#onQuickMenuShown()}.
     **/
    @Override
    public void onQuickMenuShown() {
        if (mQuickMenuListeners != null) {
            for (ToolManager.QuickMenuListener listener : mQuickMenuListeners) {
                listener.onQuickMenuShown();
            }
        }
    }

    /**
     * The overloaded implementation of {@link ToolManager.QuickMenuListener#onQuickMenuDismissed()}.
     **/
    @Override
    public void onQuickMenuDismissed() {
        if (mQuickMenuListeners != null) {
            for (ToolManager.QuickMenuListener listener : mQuickMenuListeners) {
                listener.onQuickMenuDismissed();
            }
        }
        if (Utils.isNougat() && getContext() != null) {
            this.onChangePointerIcon(PointerIcon.getSystemIcon(getContext(), PointerIcon.TYPE_ARROW));
        }
    }

    /**
     * The overloaded implementation of {@link ToolManager.AnnotationModificationListener#onAnnotationsAdded(Map)}.
     **/
    @Override
    public void onAnnotationsAdded(Map<Annot, Integer> annots) {
        handleSpecialFile();
    }

    /**
     * The overloaded implementation of {@link ToolManager.AnnotationModificationListener#onAnnotationsPreModify(Map)}.
     **/
    @Override
    public void onAnnotationsPreModify(Map<Annot, Integer> annots) {
        handleSpecialFile();
    }

    /**
     * The overloaded implementation of {@link ToolManager.AnnotationModificationListener#onAnnotationsModified(Map, Bundle,boolean, boolean)}
     **/
    /*@Override
    public void onAnnotationsModified(Map<Annot, Integer> annots, Bundle extra) {
        handleSpecialFile();
    }*/
    //TODO 07/14/2021 GWL modified need to check
    @Override
    public void onAnnotationsModified(Map<Annot, Integer> annots, Bundle extra, boolean b, boolean isStickAnnotAdded) {
        handleSpecialFile();
    }

    /**
     * The overloaded implementation of {@link ToolManager.AnnotationModificationListener#onAnnotationsPreRemove(Map)}.
     **/
    @Override
    public void onAnnotationsPreRemove(Map<Annot, Integer> annots) {
        handleSpecialFile();
    }

    /**
     * The overloaded implementation of {@link ToolManager.AnnotationModificationListener#onAnnotationsRemoved(Map)}.
     **/
    @Override
    public void onAnnotationsRemoved(Map<Annot, Integer> annots) {
        handleSpecialFile();
    }

    /**
     * The overloaded implementation of {@link ToolManager.AnnotationModificationListener#onAnnotationsRemovedOnPage(int)}.
     **/
    @Override
    public void onAnnotationsRemovedOnPage(int pageNum) {
        handleSpecialFile();
    }

    /**
     * The overloaded implementation of {@link ToolManager.PdfDocModificationListener#onAllAnnotationsRemoved()}.
     **/
    @Override
    public void onAllAnnotationsRemoved() {
        handleSpecialFile();
    }

    /**
     * The overloaded implementation of {@link ToolManager.PdfDocModificationListener#onAnnotationAction()}.
     **/
    @Override
    public void onAnnotationAction() {
        handleSpecialFile();
    }

    @Override
    public void onPageLabelsChanged() {
        handleSpecialFile();
    }

    @Override
    public void onPdfTextChanged() {
        handleSpecialFile();
    }

    @Override
    public void onBookmarkModified(@NonNull List<UserBookmarkItem> bookmarkItems) {
        handleSpecialFile(true);
        if (mBookmarksViewModel != null) {
            mBookmarksViewModel.setBookmarks(bookmarkItems);
        }
    }

    /**
     * The overloaded implementation of {@link ToolManager.PdfDocModificationListener#onPagesCropped()}.
     **/
    @Override
    public void onPagesCropped() {
        handleSpecialFile();
    }

    /**
     * The overloaded implementation of {@link ToolManager.PdfDocModificationListener#onPagesAdded(List)}.
     **/
    @Override
    public void onPagesAdded(List<Integer> pageList) {
        handleSpecialFile();
        loadBookmarks();
    }

    /**
     * The overloaded implementation of {@link ToolManager.PdfDocModificationListener#onPagesDeleted(List)}.
     **/
    @Override
    public void onPagesDeleted(List<Integer> pageList) {
        handleSpecialFile();
        loadBookmarks();
    }

    /**
     * The overloaded implementation of {@link ToolManager.PdfDocModificationListener#onPagesRotated(List)}.
     **/
    @Override
    public void onPagesRotated(List<Integer> pageList) {
        handleSpecialFile();
    }

    /**
     * The overloaded implementation of {@link ToolManager.PdfDocModificationListener#onPageMoved(int, int)}.
     **/
    @Override
    public void onPageMoved(int from, int to) {
        handleSpecialFile();
        loadBookmarks();
    }

    /**
     * The overloaded implementation of {@link ToolManager.AnnotationModificationListener#annotationsCouldNotBeAdded(String)}.
     **/
    @Override
    public void annotationsCouldNotBeAdded(String errorMessage) {
        Activity activity = getActivity();
        if (activity == null) {
            return;
        }

        if (!mAnnotNotAddedDialogShown) {
            if (null == errorMessage) {
                errorMessage = "Unknown Error";
            }
            Utils.showAlertDialog(activity, activity.getString(R.string.annotation_could_not_be_added_dialog_msg, errorMessage),
                    activity.getString(R.string.error));
            mAnnotNotAddedDialogShown = true;
        }
    }

    @Override
    public void onAnnotationSelected(Annot annot, int pageNum) {

    }

    @Override
    public void onAnnotationUnselected() {

    }

    @Override
    public boolean onInterceptAnnotationHandling(@Nullable Annot annot, Bundle extra, ToolMode toolMode) {
        try {
            if (annot != null && annot.isValid() && annot.getType() == Annot.e_Link) {
                mInternalLinkClicked = true;
                updateCurrentPageInfo();
            }
        } catch (PDFNetException e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public boolean onInterceptDialog(AlertDialog dialog) {
        return false;
    }

    /**
     * The overloaded implementation of {@link ReflowControl.OnReflowTapListener#onReflowSingleTapUp(MotionEvent)}.
     **/
    @Override
    public void onReflowSingleTapUp(MotionEvent event) {
        if (mTabListener != null) {
            mTabListener.onTabSingleTapConfirmed();
        }
    }

    /**
     * Dismisses the user crop dialog.
     */
    public void userCropDialogDismiss() {
        resetAutoSavingTimer();
    }

    /**
     * Sets the {@link TabListener} listener.
     *
     * @param listener The listener
     */
    public void setTabListener(TabListener listener) {
        mTabListener = listener;
    }

    /**
     * Adds {@link ToolManager.QuickMenuListener} listener.
     *
     * @param listener The listener
     */
    public void addQuickMenuListener(ToolManager.QuickMenuListener listener) {
        if (mQuickMenuListeners == null) {
            mQuickMenuListeners = new ArrayList<>();
        }
        if (!mQuickMenuListeners.contains(listener)) {
            mQuickMenuListeners.add(listener);
        }
    }

    /**
     * Removes {@link ToolManager.QuickMenuListener} listener.
     *
     * @param listener The listener
     */
    public void removeQuickMenuListener(ToolManager.QuickMenuListener listener) {
        if (mQuickMenuListeners != null) {
            mQuickMenuListeners.remove(listener);
        }
    }

    /**
     * Adds {@link PageStackListener} listener.
     *
     * @param listener The listener
     */
    public void addPageStackListener(PageStackListener listener) {
        if (mPageStackListeners == null) {
            mPageStackListeners = new ArrayList<>();
        }
        if (!mPageStackListeners.contains(listener)) {
            mPageStackListeners.add(listener);
        }
    }

    /**
     * Removes {@link PageStackListener} listener.
     *
     * @param listener The listener
     */
    public void removePageStackListener(PageStackListener listener) {
        if (mPageStackListeners != null) {
            mPageStackListeners.remove(listener);
        }
    }

    /**
     * Adds a {@link PasswordProtectedListener} listener.
     *
     * @param listener The listener to add to this viewer
     */
    public void addPasswordProtectedDocumentListener(@NonNull PasswordProtectedListener listener) {
        if (mPasswordProtectedListeners == null) {
            mPasswordProtectedListeners = new ArrayList<>();
        }
        if (!mPasswordProtectedListeners.contains(listener)) {
            mPasswordProtectedListeners.add(listener);
        }
    }

    /**
     * Removes the {@link PasswordProtectedListener}.
     *
     * @param listener The listener that needs to be removed
     */
    public void removePasswordProtectedDocumentListener(@NonNull PasswordProtectedListener listener) {
        if (mPasswordProtectedListeners != null) {
            mPasswordProtectedListeners.remove(listener);
        }
    }

    /**
     * Returns the bundle having information needed to create a tab fragment.
     *
     * @param tag           The tab tag
     * @param title         The title of tab
     * @param fileExtension The extension of the document
     * @param password      The password of the document
     * @param itemSource    The source of the document {@link FileInfo}
     * @return The bundle
     */
    public static Bundle createBasicPdfViewCtrlTabBundle(String tag, String title, String fileExtension,
            String password, int itemSource) {
        return createBasicPdfViewCtrlTabBundle(tag, title, fileExtension, password, itemSource, null);
    }

    /**
     * Returns the bundle having information needed to create a tab fragment.
     *
     * @param tag           The tab tag
     * @param title         The title of tab
     * @param fileExtension The extension of the document
     * @param password      The password of the document
     * @param itemSource    The source of the document {@link FileInfo}
     * @param config        The configuration of the Fragment {@link ViewerConfig}
     * @return The bundle
     */
    public static Bundle createBasicPdfViewCtrlTabBundle(String tag, String title, String fileExtension,
            String password, int itemSource, ViewerConfig config) {
        return createBasicPdfViewCtrlTabBundle(tag, title, fileExtension, password, itemSource, -1, config);
    }

    /**
     * Returns the bundle having information needed to create a tab fragment.
     *
     * @param tag           The tab tag
     * @param title         The title of tab
     * @param fileExtension The extension of the document
     * @param password      The password of the document
     * @param itemSource    The source of the document {@link FileInfo}
     * @param initialPage   The page to scroll to when the document is first opened, unused if less than 1
     * @param config        The configuration of the Fragment {@link ViewerConfig}
     * @return The bundle
     */
    public static Bundle createBasicPdfViewCtrlTabBundle(String tag, String title, String fileExtension,
            String password, int itemSource, int initialPage, ViewerConfig config) {
        Bundle args = new Bundle();
        args.putString(BUNDLE_TAB_TAG, tag);
        args.putString(BUNDLE_TAB_TITLE, title);
        args.putString(BUNDLE_TAB_FILE_EXTENSION, fileExtension);
        args.putString(BUNDLE_TAB_PASSWORD, password);
        args.putInt(BUNDLE_TAB_INITIAL_PAGE, initialPage);
        args.putInt(BUNDLE_TAB_ITEM_SOURCE, itemSource);
        args.putParcelable(BUNDLE_TAB_CONFIG, config);

        return args;
    }

    /**
     * Returns the bundle having information needed to create a tab fragment.
     *
     * @param context  The context
     * @param fileUri  The uri of the document
     * @param password The password of the document
     * @return The bundle
     */
    @SuppressWarnings("unused")
    public static Bundle createBasicPdfViewCtrlTabBundle(@NonNull Context context, @NonNull Uri fileUri, @Nullable String password) {
        return createBasicPdfViewCtrlTabBundle(context, fileUri, password, null);
    }

    /**
     * Returns the bundle having information needed to create a tab fragment.
     *
     * @param context  The context
     * @param fileUri  The uri of the document
     * @param password The password of the document
     * @param config   The configuration of the Fragment {@link ViewerConfig}
     * @return The bundle
     */
    public static Bundle createBasicPdfViewCtrlTabBundle(@NonNull Context context, @NonNull Uri fileUri, @Nullable String password, @Nullable ViewerConfig config) {
        return createBasicPdfViewCtrlTabBundle(context, fileUri, null, password, config);
    }

    /**
     * Returns the bundle having information needed to create a tab fragment.
     *
     * @param context  The context
     * @param fileUri  The uri of the document
     * @param title    The title of tab
     * @param password The password of the document
     * @param config   The configuration of the Fragment {@link ViewerConfig}
     * @return The bundle
     */
    public static Bundle createBasicPdfViewCtrlTabBundle(@NonNull Context context, @NonNull Uri fileUri, @Nullable String title, @Nullable String password, @Nullable ViewerConfig config) {
        String tag = fileUri.toString();
        if (null == title) {
            title = Utils.getValidTitle(context, fileUri);
        }
        String fileExtension = "";
        ContentResolver contentResolver = Utils.getContentResolver(context);
        if (contentResolver != null) {
            fileExtension = Utils.getUriExtension(contentResolver, fileUri);
        } else {
            String msg = "Could not get ContentResolver in createBasicPdfViewCtrlTabBundle.";
            Logger.INSTANCE.LogE(TAG, msg);
        }
        int itemSource;

        if (ContentResolver.SCHEME_CONTENT.equals(fileUri.getScheme())) {
            // If scheme is a content
            if (Utils.uriHasReadWritePermission(context, fileUri)) {
                itemSource = BaseFileInfo.FILE_TYPE_EXTERNAL; // read and write directly to file
            } else if (contentResolver != null && Utils.isNotPdf(contentResolver, fileUri)) {
                itemSource = BaseFileInfo.FILE_TYPE_OFFICE_URI;
            } else {
                itemSource = BaseFileInfo.FILE_TYPE_EDIT_URI;
            }
        } else if (URLUtil.isHttpUrl(tag) || URLUtil.isHttpsUrl(tag)) {
            itemSource = BaseFileInfo.FILE_TYPE_OPEN_URL;
        } else {
            // If scheme is a File
            tag = fileUri.getPath();
            if (tag != null && tag.startsWith("/android_asset/")) {
                // Make a copy of the file if it is assets
                File copy = Utils.copyAssetsToTempFolder(context, tag, true);
                if (copy != null) {
                    tag = copy.getAbsolutePath();
                }
            }
            itemSource = BaseFileInfo.FILE_TYPE_FILE;
        }
        return createBasicPdfViewCtrlTabBundle(tag, title, fileExtension, password, itemSource, config);
    }

    protected boolean isNightModeForToolManager() {
        Activity activity = getActivity();
        return activity != null
                && (PdfViewCtrlSettingsManager.getColorMode(activity) == PdfViewCtrlSettingsManager.KEY_PREF_COLOR_MODE_NIGHT
                || (PdfViewCtrlSettingsManager.getColorMode(activity) == PdfViewCtrlSettingsManager.KEY_PREF_COLOR_MODE_CUSTOM
                && Utils.isColorDark(PdfViewCtrlSettingsManager.getCustomColorModeBGColor(activity))));
    }

    /**
     * Checks whether a warning about can not edit during conversion has been shown.
     *
     * @return True if the warning has been shown
     */
    public boolean getHasWarnedAboutCanNotEditDuringConversion() {
        return mHasWarnedAboutCanNotEditDuringConversion;
    }

    /**
     * Specifies that the warning about can not edit during conversion has been shown.
     */
    public void setHasWarnedAboutCanNotEditDuringConversion() {
        mHasWarnedAboutCanNotEditDuringConversion = true;
        mShouldNotifyWhenConversionFinishes = true;
    }

    /**
     * Checks if the document is ready.
     *
     * @return True if the document is ready
     */
    public boolean isDocumentReady() {
        return mDocumentLoaded;
    }

    /**
     * Updates the view mode.
     *
     * @param pagePresentationMode The page presentation mode
     */
    public void updateViewMode(PDFViewCtrl.PagePresentationMode pagePresentationMode) {
        Activity activity = getActivity();
        if (activity == null || mPdfViewCtrl == null) {
            return;
        }

        if (mCanAddToTabInfo) {
            PdfViewCtrlTabsManager.getInstance().updateViewModeForTab(activity, mTabTag, pagePresentationMode);
        }
        boolean verticalSnapping = PdfViewCtrlSettingsManager.isVerticalScrollSnap(activity);
        if (verticalSnapping) {
            if (pagePresentationMode == PDFViewCtrl.PagePresentationMode.SINGLE_CONT) {
                pagePresentationMode = PDFViewCtrl.PagePresentationMode.SINGLE_VERT;
            } else if (pagePresentationMode == PDFViewCtrl.PagePresentationMode.FACING_CONT) {
                pagePresentationMode = PDFViewCtrl.PagePresentationMode.FACING_VERT;
            } else if (pagePresentationMode == PDFViewCtrl.PagePresentationMode.FACING_COVER_CONT) {
                pagePresentationMode = PDFViewCtrl.PagePresentationMode.FACING_COVER_VERT;
            }
        } else {
            if (pagePresentationMode == PDFViewCtrl.PagePresentationMode.SINGLE_VERT) {
                pagePresentationMode = PDFViewCtrl.PagePresentationMode.SINGLE_CONT;
            } else if (pagePresentationMode == PDFViewCtrl.PagePresentationMode.FACING_VERT) {
                pagePresentationMode = PDFViewCtrl.PagePresentationMode.FACING_CONT;
            } else if (pagePresentationMode == PDFViewCtrl.PagePresentationMode.FACING_COVER_VERT) {
                pagePresentationMode = PDFViewCtrl.PagePresentationMode.FACING_COVER_CONT;
            }
        }
        try {
            preparingNavChange();
            updateZoomLimits();
            mPdfViewCtrl.setPagePresentationMode(pagePresentationMode);

            if (mReflowControl != null && mIsReflowMode) {
                boolean vertical = isContinuousPageMode() || isNonContinuousVerticalPageMode();
                mReflowControl.setOrientation(vertical ? ReflowControl.VERTICAL : ReflowControl.HORIZONTAL);
            }

            postNavChange();
        } catch (Exception e) {
            AnalyticsHandlerAdapter.getInstance().sendException(e);
        }
    }

    /**
     * Sets the zoom limits for the PDFViewCtrl.
     */
    protected void updateZoomLimits() {
        Activity activity = getActivity();
        if (activity == null || mPdfViewCtrl == null) {
            return;
        }

        try {
            boolean isMaintainZoomEnabled = PdfViewCtrlSettingsManager.getMaintainZoomOption(activity);
            if (mViewerConfig != null && mViewerConfig.getPdfViewCtrlConfig() != null) {
                isMaintainZoomEnabled = getPDFViewCtrlConfig(activity).isMaintainZoomEnabled();
            }
            mPdfViewCtrl.setMaintainZoomEnabled(isMaintainZoomEnabled);

            PDFViewCtrl.PageViewMode viewMode = PdfViewCtrlSettingsManager.getPageViewMode(activity);
            if (mViewerConfig != null && mViewerConfig.getPdfViewCtrlConfig() != null) {
                viewMode = getPDFViewCtrlConfig(activity).getPageViewMode();
            }
            mPdfViewCtrl.setZoomLimits(PDFViewCtrl.ZoomLimitMode.RELATIVE, MIN_RELATIVE_ZOOM_LIMIT, MAX_RELATIVE_ZOOM_LIMIT);

            if (!isMaintainZoomEnabled) {
                mPdfViewCtrl.setPageRefViewMode(viewMode);
            } else {
                mPdfViewCtrl.setPreferredViewMode(viewMode);
            }
        } catch (Exception e) {
            AnalyticsHandlerAdapter.getInstance().sendException(e);
        }
    }

    /**
     * Updates the page indicator
     */
    protected void updatePageIndicator() {
        if (mPdfViewCtrl == null) {
            return;
        }

        int curPage = mPdfViewCtrl.getCurrentPage();

        if (mPageNumberIndicatorAll != null) {
            mPageNumberIndicatorAll.setText(PageLabelUtils.getPageNumberIndicator(
                    mPdfViewCtrl, curPage, mPageCount));
        }
        sliderUpdateProgress(curPage);
    }

    protected void hidePageNumberIndicator() {
        if (null != mPageNumberIndicator) {
            animatePageIndicator(false);
        }
        hideBackAndForwardButtons();
    }

    // Starts the show/hide page indicator animation
    protected void animatePageIndicator(boolean show) {
        if (show) {
            mPageNumberIndicator.animate()
                    .scaleX(1.0f)
                    .scaleY(1.0f)
                    .alpha(1.0f)
                    .setDuration(200)
                    .setInterpolator(new DecelerateInterpolator())
                    .setListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            mPageNumberIndicator.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {

                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {
                            mPageNumberIndicator.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    });
        } else {
            if (mViewerConfig != null && mViewerConfig.isPermanentPageNumberIndicator()) {
                return;
            }
            mPageNumberIndicator.animate()
                    .scaleX(0)
                    .scaleY(0)
                    .alpha(0)
                    .setDuration(200)
                    .setInterpolator(new AccelerateInterpolator())
                    .setListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mPageNumberIndicator.setVisibility(View.GONE);
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {
                            mPageNumberIndicator.setVisibility(View.GONE);
                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    });
        }
    }

    /**
     * Resets timer for hiding page number indicator.
     */
    protected void resetHidePageNumberIndicatorTimer() {
        if (!mDocumentLoaded) {
            return;
        }
        stopHidePageNumberIndicatorTimer();

        if (mPageNumberIndicator != null) {
            boolean canShow = mViewerConfig == null || mViewerConfig.isShowPageNumberIndicator();
            animatePageIndicator(canShow);
        }
        if (mHidePageNumberAndPageBackButtonHandler != null) {
            mHidePageNumberAndPageBackButtonHandler.postDelayed(mHidePageNumberAndPageBackButtonRunnable, HIDE_PAGE_NUMBER_INDICATOR);
        }
    }

    /**
     * Stops the timer for hiding the page number indicator.
     */
    protected void stopHidePageNumberIndicatorTimer() {
        if (mHidePageNumberAndPageBackButtonHandler != null) {
            mHidePageNumberAndPageBackButtonHandler.removeCallbacksAndMessages(null);
        }
    }

    /**
     * Returns the {@link ReflowControl} associated with this tab.
     */
    public ReflowControl getReflowControl() {
        return mReflowControl;
    }

    /**
     * Returns the {@link PDFViewCtrl} associated with this tab.
     *
     * @return The PDFViewCtrl
     */
    public PDFViewCtrl getPDFViewCtrl() {
        return mPdfViewCtrl;
    }

    /**
     * Returns {@link ToolManager} associated with this tab.
     *
     * @return The ToolManager
     */
    public ToolManager getToolManager() {
        return (mPdfViewCtrl == null) ? null : (ToolManager) mPdfViewCtrl.getToolManager();
    }

    /**
     * Returns the PDF document
     *
     * @return The PDF document associated with this tab
     */
    public PDFDoc getPdfDoc() {
        return (mPdfViewCtrl == null) ? null : mPdfViewCtrl.getDoc();
    }

    /**
     * Checks if opening file failed.
     *
     * @return True if opening file failed
     */
    public boolean isOpenFileFailed() {
        return mErrorOnOpeningDocument;
    }

    /**
     * Returns the error code, if any.
     * <p>
     * See {@link PdfDocManager}
     *
     * @return The error code
     */
    public int getTabErrorCode() {
        return mErrorCode;
    }

    /**
     * Saves the current PDFViewCtrl state
     */
    protected PdfViewCtrlTabInfo saveCurrentPdfViewCtrlState() {
        if (!mStateEnabled || !mCanAddToTabInfo || !isDocumentReady()) {
            return null;
        }

        Activity activity = getActivity();
        if (activity == null || mPdfViewCtrl == null) {
            return null;
        }

        PdfViewCtrlTabInfo info = PdfViewCtrlTabsManager.getInstance().getPdfFViewCtrlTabInfo(activity, mTabTag);
        if (info == null) {
            info = new PdfViewCtrlTabInfo();
        }

        // save tab info
        info.fileExtension = mFileExtension;
        info.tabTitle = mTabTitle;
        info.tabSource = mTabSource;
        info.hScrollPos = mPdfViewCtrl.getHScrollPos();
        info.vScrollPos = mPdfViewCtrl.getVScrollPos();
        info.zoom = mPdfViewCtrl.getZoom();
        info.lastPage = mPdfViewCtrl.getCurrentPage();
        info.pageRotation = mPdfViewCtrl.getPageRotation();
        info.setPagePresentationMode(mPdfViewCtrl.getPagePresentationMode());
        info.isRtlMode = mIsRtlMode;
        info.isReflowMode = mIsReflowMode;
        if (mReflowControl != null) {
            try {
                info.reflowTextSize = mReflowControl.getTextSizeInPercent();
            } catch (Exception e) {
                AnalyticsHandlerAdapter.getInstance().sendException(e);
            }
        }
        info.bookmarkDialogCurrentTab = mBookmarkDialogCurrentTab;

        PdfViewCtrlTabsManager.getInstance().addPdfViewCtrlTabInfo(activity, mTabTag, info);

        return info;
    }

    /**
     * Gets whether the navigation side sheet is visible
     *
     * @return true if the navigation side sheet is visible, false otherwise
     */
    public boolean isNavigationListShowing() {
        return mNavigationList != null && mNavigationList.getVisibility() == View.VISIBLE;
    }

    public void updateNavigationListLayout(int marginTop, int marginBottom, boolean animated) {
        if (isNavigationListShowing()) {
            // adjust top margin
            if (marginTop > -1 || marginBottom > -1) {
                if (mNavigationList.getLayoutParams() != null && mNavigationList.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
                    ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) mNavigationList.getLayoutParams();
                    boolean changed = false;
                    if (marginTop > -1 && layoutParams.topMargin != marginTop) {
                        layoutParams.topMargin = marginTop;
                        changed = true;
                    }
                    if (marginBottom > -1 && layoutParams.bottomMargin != marginBottom) {
                        layoutParams.bottomMargin = marginBottom;
                        changed = true;
                    }
                    if (changed) {
                        if (animated) {
                            TransitionSet transitionSet = new TransitionSet();
                            transitionSet.addTransition(new ChangeBounds());
                            transitionSet.addTransition(new Fade());
                            transitionSet.setDuration(ANIMATE_DURATION_SHOW);
                            TransitionManager.beginDelayedTransition(mNavigationList, transitionSet);
                        }
                        mNavigationList.setLayoutParams(layoutParams);
                    }
                }
            }
        }
    }

    /**
     * Opens a dialog fragment as side sheet
     */
    public void openSideSheet(DialogFragment dialogFragment, String tag,
            int marginTop, int marginBottom) {
        if (dialogFragment == null) {
            return;
        }
        if (mNavigationList == null) {
            mNavigationList = mRootView.findViewById(R.id.navigation_list);
        }

        TransitionSet transitionSet = new TransitionSet();
        transitionSet.addTransition(new ChangeBounds());
        Slide slideFromEnd = new Slide(Gravity.END);
        slideFromEnd.addTarget(mNavigationList);
        transitionSet.addTransition(new Fade());
        transitionSet.setDuration(ANIMATE_DURATION_SHOW);
        TransitionManager.beginDelayedTransition(mViewerHost, transitionSet);
        mNavigationList.setVisibility(View.VISIBLE);
        updateNavigationListLayout(marginTop, marginBottom, false);
        // adjust overlay side margin
        resizeOverlay(true);
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        ft.replace(R.id.navigation_list, dialogFragment, tag);
        ft.commitAllowingStateLoss();
    }

    public void openNavigationList(BookmarksDialogFragment bookmarksDialogFragment,
            int marginTop, int marginBottom) {
        openSideSheet(bookmarksDialogFragment,
                "bookmarks_dialog_" + mTabTag,
                marginTop,
                marginBottom);
    }

    /**
     * Opens the navigation list as a side sheet
     */
    public void openNavigationList(BookmarksDialogFragment bookmarksDialogFragment) {
        openNavigationList(bookmarksDialogFragment);
    }

    /**
     * Opens the redaction search dialog as a side sheet
     */
    public void openRedactionSearchList(SearchRedactionDialogFragment searchRedactionDialogFragment) {
        openRedactionSearchList(searchRedactionDialogFragment,
                0,
                0);
    }

    public void openRedactionSearchList(SearchRedactionDialogFragment searchRedactionDialogFragment,
            int marginTop, int marginBottom) {
        openSideSheet(searchRedactionDialogFragment,
                SearchRedactionDialogFragment.TAG + mTabTag,
                marginTop,
                marginBottom);
    }

    /**
     * Closes the side sheet
     *
     * @param tag the dialog fragment tag
     */
    public void closeSideSheet(String tag) {
        Fragment fragment = getChildFragmentManager().findFragmentByTag(tag);
        if (fragment instanceof DialogFragment) {
            final DialogFragment dialogFragment = (DialogFragment) fragment;
            if (mNavigationList != null) {
                TransitionSet transitionSet = new TransitionSet();
                transitionSet.addTransition(new ChangeBounds());
                Slide slideFromEnd = new Slide(Gravity.END);
                slideFromEnd.addTarget(mNavigationList);
                transitionSet.addTransition(slideFromEnd);
                transitionSet.addTransition(new Fade());
                transitionSet.setDuration(ANIMATE_DURATION_HIDE);
                transitionSet.addListener(new Transition.TransitionListener() {
                    @Override
                    public void onTransitionStart(@NonNull Transition transition) {

                    }

                    @Override
                    public void onTransitionEnd(@NonNull Transition transition) {
                        dialogFragment.dismiss();
                    }

                    @Override
                    public void onTransitionCancel(@NonNull Transition transition) {
                        dialogFragment.dismiss();
                    }

                    @Override
                    public void onTransitionPause(@NonNull Transition transition) {

                    }

                    @Override
                    public void onTransitionResume(@NonNull Transition transition) {

                    }
                });
                TransitionManager.beginDelayedTransition(mViewerHost, transitionSet);
                mNavigationList.setVisibility(View.GONE);
                // adjust overlay side margin
                resizeOverlay(false);
            }
        }
    }

    /**
     * Closes the navigation list if it was opened as side sheet
     */
    public void closeNavigationList() {
        closeSideSheet("bookmarks_dialog_" + mTabTag);
    }

    /**
     * Closes the redaction search dialog if it was opened as side sheet
     */
    public void closeRedactionSearchList() {
        closeSideSheet(SearchRedactionDialogFragment.TAG + mTabTag);
    }

    @Nullable
    public BookmarksDialogFragment getBookmarksNavigationList() {
        if (isAdded()) {
            Fragment fragment = getChildFragmentManager().findFragmentByTag("bookmarks_dialog_" + mTabTag);
            if (fragment instanceof BookmarksDialogFragment) {
                return (BookmarksDialogFragment) fragment;
            }
        }
        return null;
    }

    protected void resizeOverlay(boolean navListOpen) {
        if (mOverlayStub != null && mOverlayStub.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) mOverlayStub.getLayoutParams();
            int endMargin = navListOpen ? mOverlayStub.getContext().getResources().getDimensionPixelSize(R.dimen.standard_side_sheet) : 0;
            boolean changed = false;
            if (Utils.isJellyBeanMR1()) {
                if (endMargin != layoutParams.getMarginEnd()) {
                    layoutParams.setMarginEnd(endMargin);
                    changed = true;
                }
            } else {
                if (layoutParams.rightMargin != endMargin) {
                    layoutParams.rightMargin = endMargin;
                    changed = true;
                }
            }
            if (changed) {
                mOverlayStub.setLayoutParams(layoutParams);
                if (mOverlayStub instanceof ViewGroup) {
                    TransitionManager.beginDelayedTransition((ViewGroup) mOverlayStub, new ChangeBounds());
                }
            }
        }
    }

    /**
     * Initializes the layout.
     */
    protected void initLayout() {
        FragmentActivity activity = getActivity();
        if (activity == null || mRootView == null) {
            return;
        }

        loadProgressView();
        loadOverlayView();
        setupGenericMotionEvent();

        mDownloadDocumentDialog = new ProgressDialog(activity);
        mDownloadDocumentDialog.setMessage(getString(R.string.download_in_progress_message));
        mDownloadDocumentDialog.setIndeterminate(true);
        mDownloadDocumentDialog.setCancelable(true);
        mDownloadDocumentDialog.setCanceledOnTouchOutside(false);
        mDownloadDocumentDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (mDownloadDocumentDialog != null && mDownloadDocumentDialog.isShowing()) {
                    mDownloadDocumentDialog.dismiss();
                }
                handleOpeningDocumentFailed(PdfDocManager.DOCUMENT_SETDOC_ERROR_OPENURL_CANCELLED);
            }
        });

        if (Utils.isLollipop()) {
            final RCContainer rCContainer = new RCContainer(activity);
            rCContainer.setup(mToolManager);
            RichTextViewModel richTextViewModel = ViewModelProviders.of(activity).get(RichTextViewModel.class);
            mDisposables.add(richTextViewModel.getObservable()
                    .subscribe(new Consumer<RichTextEvent>() {
                        @Override
                        public void accept(RichTextEvent richTextEvent) throws Exception {
                            switch (richTextEvent.getEventType()) {
                                case OPEN_TOOLBAR:
                                    rCContainer.showAtLocation(mRootView, Gravity.START | Gravity.BOTTOM, 0, 0);
                                    break;
                                case CLOSE_TOOLBAR:
                                    rCContainer.dismiss();
                                    break;
                                case UPDATE_TOOLBAR:
                                    rCContainer.updateToolbar(richTextEvent.getDecorationType(), richTextEvent.isChecked());
                                    break;
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            AnalyticsHandlerAdapter.getInstance().sendException(new Exception(throwable));
                        }
                    }));
        } else {
            mToolManager.setShowRichContentOption(false);
        }
    }

    protected View loadStubProgress() {
        return ((ViewStub) mRootView.findViewById(R.id.stub_progress)).inflate();
    }

    protected void loadProgressView() {
        Activity activity = getActivity();
        if (activity == null || mRootView == null) {
            return;
        }
        if (mProgressBarLayout != null) {
            return;
        }
        View stub = loadStubProgress();

        mProgressBarLayout = stub.findViewById(R.id.progress_bar_layout);
        mProgressBarLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mDocumentConversion != null) {
                    try {
                        if (sDebug)
                            Log.i("UNIVERSAL", String.format("Conversion status is %d and label is %s, number of converted pages is %d, has been cancelled? %s",
                                    mDocumentConversion.getConversionStatus(), mDocumentConversion.getProgressLabel(), mDocumentConversion.getNumConvertedPages(),
                                    (mDocumentConversion.isCancelled() ? "YES" : "NO")));
                    } catch (Exception e) {
                        AnalyticsHandlerAdapter.getInstance().sendException(e);
                    }
                }
                if (mTabListener != null) {
                    mTabListener.onTabSingleTapConfirmed();
                }
            }
        });
    }

    protected View loadStubPDFViewCtrl() {
        return ((ViewStub) mRootView.findViewById(R.id.stub_pdfviewctrl)).inflate();
    }

    protected void loadPDFViewCtrlView() {
        Activity activity = getActivity();
        if (activity == null || mRootView == null) {
            return;
        }
        if (mStubPDFViewCtrl != null) {
            return;
        }
        mStubPDFViewCtrl = loadStubPDFViewCtrl();

        mViewerHost = mStubPDFViewCtrl.findViewById(R.id.pdfviewctrl_host);
        int pdfViewCtrlResId = mPdfViewCtrlId == 0 ? R.id.pdfviewctrl : mPdfViewCtrlId;
        mPdfViewCtrl = mStubPDFViewCtrl.findViewById(pdfViewCtrlResId);
        if (null == mPdfViewCtrl) {
            // we are in trouble
            AnalyticsHandlerAdapter.getInstance().sendException(new Exception("loadPDFViewCtrlView PDFViewCtrl is null"));
            return;
        }
        // mPdfViewCtrl.setAccessibilityDelegate(new View.AccessibilityDelegate() {
        //     @Override
        //     public void onPopulateAccessibilityEvent(View host, final AccessibilityEvent event) {
        //         super.onPopulateAccessibilityEvent(host, event);

        //         if (mGetTextInPageTask != null && mGetTextInPageTask.getStatus() != AsyncTask.Status.FINISHED) {
        //             return;
        //         }
        //         mGetTextInPageTask.setCallback(new GetTextInPageTask.Callback() {
        //             @Override
        //             public void getText(String text) {
        //                 if (!Utils.isNullOrEmpty(text)) {
        //                     event.getText().add(text);
        //                 }
        //             }
        //         });
        //         mGetTextInPageTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        //     }

        //     @Override
        //     public void onInitializeAccessibilityNodeInfo(View host, final AccessibilityNodeInfo info) {
        //         super.onInitializeAccessibilityNodeInfo(host, info);
        //         if (mGetTextInPageTask != null && mGetTextInPageTask.getStatus() != AsyncTask.Status.FINISHED) {
        //             return;
        //         }
        //         mGetTextInPageTask = new GetTextInPageTask(mPdfViewCtrl);
        //         mGetTextInPageTask.setCallback(new GetTextInPageTask.Callback() {
        //             @Override
        //             public void getText(String text) {
        //                 if (!Utils.isNullOrEmpty(text)) {
        //                     info.setText(text);
        //                 }
        //             }
        //         });
        //         mGetTextInPageTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        //     }
        // });

        try {
            AppUtils.setupPDFViewCtrl(mPdfViewCtrl, getPDFViewCtrlConfig(activity));
            mPdfViewCtrl.setBuiltInPageSlidingEnabled(true);
            mPdfViewCtrl.setPageBox(Page.e_user_crop);

            updateZoomLimits();
            PDFViewCtrl.PageViewMode viewMode = PdfViewCtrlSettingsManager.getPageViewMode(activity);
            if (mViewerConfig != null && mViewerConfig.getPdfViewCtrlConfig() != null) {
                viewMode = getPDFViewCtrlConfig(activity).getPageViewMode();
            }
            mPdfViewCtrl.setPageViewMode(viewMode);
            if (mViewerConfig != null && mViewerConfig.getPdfViewCtrlConfig() != null) {
                mPdfViewCtrl.setImageSmoothing(getPDFViewCtrlConfig(activity).isImageSmoothing());
            } else {
                if (PdfViewCtrlSettingsManager.getImageSmoothing(activity)) {
                    mPdfViewCtrl.setImageSmoothing(true);
                } else {
                    mPdfViewCtrl.setImageSmoothing(false);
                }
            }
        } catch (Exception e) {
            AnalyticsHandlerAdapter.getInstance().sendException(e);
        }

        // PDFViewCtrl listeners
        mPdfViewCtrl.addPageChangeListener(this);
        mPdfViewCtrl.addDocumentLoadListener(this);
        mPdfViewCtrl.addDocumentDownloadListener(this);
        mPdfViewCtrl.setRenderingListener(this);
        mPdfViewCtrl.addUniversalDocumentConversionListener(this);
        mPdfViewCtrl.setUniversalDocumentProgressIndicatorListener(this);

        // Attach ToolManager to PDFViewCtrl
        int toolManagerResId = (mViewerConfig != null && mViewerConfig.getToolManagerBuilderStyleRes() != 0) ?
                mViewerConfig.getToolManagerBuilderStyleRes() : R.style.TabFragmentToolManager;
        ToolManagerBuilder toolManagerBuilder = mViewerConfig == null ? null : mViewerConfig.getToolManagerBuilder();
        if (toolManagerBuilder == null) {
            // if no builder is supplied, create one with style res
            toolManagerBuilder = ToolManagerBuilder.from(activity, toolManagerResId);
            if (mViewerConfig == null) {
                // no config, let's load value from shared pref
                toolManagerBuilder.setCopyAnnot(PdfViewCtrlSettingsManager.getCopyAnnotatedTextToNote(activity))
                        .setStylusAsPen(PdfViewCtrlSettingsManager.getStylusAsPen(activity))
                        .setInkSmoothing(PdfViewCtrlSettingsManager.getInkSmoothing(activity))
                        .setFreeHighlighterAutoSmoothingRange(PdfViewCtrlSettingsManager.getFreeHighlighterSmoothing(activity) ? FreeHighlighterCreate.AUTO_SMOOTH_RANGE_DEFAULT : 0f)
                        .setAutoSelect(PdfViewCtrlSettingsManager.isAutoSelectAnnotation(activity))
                        .setShowAnnotIndicator(PdfViewCtrlSettingsManager.getShowAnnotationIndicator(activity));
            }
        }
        mToolManager = toolManagerBuilder.build(this);
        if (mViewerConfig != null) {
            mToolManager.setSkipReadOnlyCheck(mViewerConfig.skipReadOnlyCheck());
        }
        mToolManager.setNightMode(isNightModeForToolManager());
        mToolManager.setCacheFileName(mTabTag);
        mToolManager.getUndoRedoManger().addUndoRedoStateChangeListener(new UndoRedoManager.UndoRedoStateChangeListener() {
            @Override
            public void onStateChanged() {
                updateAnnotSnappingManager();
            }
        });
        // disable redaction tool in Standard version
        if (mViewerConfig != null && mViewerConfig.isUseStandardLibrary()) {
            mToolManager.disableToolMode(new ToolMode[]{ToolMode.RECT_REDACTION, ToolMode.TEXT_REDACTION});
        }
        mToolManager.getAnnotSnappingManager().setEnabled(mViewerConfig == null || mViewerConfig.annotationPositionSnappingEnabled());
        mToolManager.setAnnotationToolbarListener(new ToolManager.AnnotationToolbarListener() {
            @Override
            public void inkEditSelected(Annot annot, int pageNum) {
                if (mTabListener != null) {
                    mTabListener.onInkEditSelected(annot, pageNum);
                }
            }

            @Override
            public void openAnnotationToolbar(
                    ToolMode mode) {
                if (mTabListener != null) {
                    mTabListener.onOpenAnnotationToolbar(mode);
                }
            }

            @Override
            public int annotationToolbarHeight() {
                return -1;
            }

            @Override
            public int toolbarHeight() {
                if (mTabListener != null) {
                    return mTabListener.getToolbarHeight();
                }
                return -1;
            }

            @Override
            public void openEditToolbar(ToolMode mode) {
                if (mTabListener != null) {
                    mTabListener.onOpenEditToolbar(mode);
                }
            }
        });
    }

    protected PDFViewCtrlConfig getPDFViewCtrlConfig(Context context) {
        PDFViewCtrlConfig pdfViewCtrlConfig = mViewerConfig != null ? mViewerConfig.getPdfViewCtrlConfig() : null;
        if (null == pdfViewCtrlConfig) {
            pdfViewCtrlConfig = PDFViewCtrlConfig.getDefaultConfig(context);
        }
        return pdfViewCtrlConfig;
    }

    /**
     * @return True if the document is password protected.
     */
    public boolean isPasswordProtected() {
        return !Utils.isNullOrEmpty(mPassword);
    }

    protected View loadStubPassword(View view) {
        return ((ViewStub) view.findViewById(R.id.stub_password)).inflate();
    }

    protected void handleEncryptedPdf(@NonNull Context context) {
        loadPasswordView();
        // needs password
        mProgressBarLayout.hide(true);
        if (sDebug)
            Log.d(TAG, "hide progress bar");
        mPasswordLayout.setVisibility(View.VISIBLE);
        mPasswordInput.requestFocus();
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        }
        sliderSetVisibility(View.GONE);
    }

    protected void loadPasswordView() {
        Activity activity = getActivity();
        if (activity == null || mRootView == null) {
            return;
        }
        if (mPasswordLayout != null) {
            return;
        }
        View stub = loadStubPassword(mRootView);

        // password layout
        mPasswordLayout = stub.findViewById(R.id.password_layout);
        mPasswordInput = stub.findViewById(R.id.password_input);
        mPasswordInput.setTransformationMethod(HideReturnsTransformationMethod.getInstance()); // default show password
        if (mPasswordInput != null) {
            mPasswordInput.setImeOptions(EditorInfo.IME_ACTION_GO);
            mPasswordInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {

                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    Activity activity = getActivity();
                    if (activity == null) {
                        return false;
                    }

                    // If enter key was pressed, then submit password
                    if (actionId == EditorInfo.IME_ACTION_GO) {
                        try {
                            if (mPdfDoc != null && mPdfDoc.initStdSecurityHandler(mPasswordInput.getText().toString())) {
                                //password correct, open document.
                                mPassword = mPasswordInput.getText().toString();
                                checkPdfDoc();
                                InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                                if (imm != null) {
                                    imm.hideSoftInputFromWindow(mPasswordInput.getWindowToken(), 0);
                                }
                                if (mPasswordProtectedListeners != null) {
                                    for (PasswordProtectedListener listener : mPasswordProtectedListeners) {
                                        listener.onPasswordValid();
                                    }
                                }
                            } else if (mTabSource == BaseFileInfo.FILE_TYPE_OPEN_URL) {
                                mPassword = mPasswordInput.getText().toString();
                                openUrlFile(mTabTag);
                                InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                                if (imm != null) {
                                    imm.hideSoftInputFromWindow(mPasswordInput.getWindowToken(), 0);
                                }
                            } else {
                                boolean handled = handlePasswordInvalidEvent();
                                if (!handled) {
                                    //password incorrect
                                    mPasswordInput.setText("");
                                    CommonToast.showText(activity, R.string.password_not_valid_message, Toast.LENGTH_SHORT);
                                }
                            }
                        } catch (Exception e) {
                            handleOpeningDocumentFailed(PdfDocManager.DOCUMENT_SETDOC_ERROR_NULL_PDFDOC);
                            AnalyticsHandlerAdapter.getInstance().sendException(e, "checkPdfDoc");
                        }

                        return true;
                    }
                    return false;
                }
            });
            mPasswordInput.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    Activity activity = getActivity();
                    if (activity == null) {
                        return false;
                    }

                    if (keyCode == KeyEvent.KEYCODE_ENTER) {
                        try {
                            if (mPdfDoc != null && mPdfDoc.initStdSecurityHandler(mPasswordInput.getText().toString())) {
                                //password correct, open document.
                                mPassword = mPasswordInput.getText().toString();
                                checkPdfDoc();
                                InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                                if (imm != null) {
                                    imm.hideSoftInputFromWindow(mPasswordInput.getWindowToken(), 0);
                                }
                            } else {
                                //password incorrect
                                mPasswordInput.setText("");
                                CommonToast.showText(activity, R.string.password_not_valid_message, Toast.LENGTH_SHORT);
                            }
                        } catch (Exception e) {
                            handleOpeningDocumentFailed(PdfDocManager.DOCUMENT_SETDOC_ERROR_NULL_PDFDOC);
                            AnalyticsHandlerAdapter.getInstance().sendException(e, "checkPdfDoc");
                        }

                        return true;
                    }
                    return false;
                }
            });
        }
    }

    protected View loadStubReflow() {
        return ((ViewStub) mRootView.findViewById(R.id.stub_reflow)).inflate();
    }

    protected void loadReflowView() {
        Activity activity = getActivity();
        if (activity == null || mRootView == null) {
            return;
        }
        if (mReflowControl != null) {
            return;
        }
        View stub = loadStubReflow();
        mReflowControl = stub.findViewById(R.id.reflow_pager);
    }

    protected View loadStubOverlay() {
        return ((ViewStub) mRootView.findViewById(R.id.stub_overlay)).inflate();
    }

    protected void loadOverlayView() {
        FragmentActivity activity = getActivity();
        if (activity == null || mRootView == null) {
            return;
        }
        if (mSearchOverlay != null) {
            return;
        }
        View stub = loadStubOverlay();
        mOverlayStub = stub;

        mSearchOverlay = stub.findViewById(R.id.find_text_view);
        mSearchOverlay.setPdfViewCtrl(mPdfViewCtrl);
        mSearchOverlay.setFindTextOverlayListener(new FindTextOverlay.FindTextOverlayListener() {

            @Override
            public void onGotoNextSearch(boolean useFullTextResults) {
                SearchResultsView.SearchResultStatus status = SearchResultsView.SearchResultStatus.NOT_HANDLED;
                if (useFullTextResults) {
                    if (mTabListener != null) {
                        status = mTabListener.onFullTextSearchFindText(false);
                    }
                }
                if (status != SearchResultsView.SearchResultStatus.HANDLED) {
                    if (mSearchOverlay != null) {
                        mSearchOverlay.findText();
                    }
                }
            }

            @Override
            public void onGotoPreviousSearch(boolean useFullTextResults) {
                SearchResultsView.SearchResultStatus status = SearchResultsView.SearchResultStatus.NOT_HANDLED;
                if (useFullTextResults) {
                    if (mTabListener != null) {
                        status = mTabListener.onFullTextSearchFindText(true);
                    }
                }
                if (status != SearchResultsView.SearchResultStatus.HANDLED) {
                    if (mSearchOverlay != null) {
                        if (status == SearchResultsView.SearchResultStatus.USE_FINDTEXT_FROM_END) {
                            mSearchOverlay.findText(mPdfViewCtrl.getPageCount());
                        } else {
                            mSearchOverlay.findText();
                        }
                    }
                }
            }

            @Override
            public void onSearchProgressShow() {
                if (mTabListener != null) {
                    mTabListener.onSearchProgressShow();
                }
            }

            @Override
            public void onSearchProgressHide() {
                if (mTabListener != null) {
                    mTabListener.onSearchProgressHide();
                }
            }
        });

        mPageNumberIndicator = stub.findViewById(R.id.page_number_indicator_view);
        mPageNumberIndicator.setPdfViewCtrl(mPdfViewCtrl);
        if (mViewerConfig != null &&
                mViewerConfig.getPageNumberIndicatorPosition() != PageIndicatorLayout.POSITION_BOTTOM_START) {
            if (stub instanceof ConstraintLayout) {
                ConstraintSet constraintSet = new ConstraintSet();
                constraintSet.clone((ConstraintLayout) stub);
                constraintSet.connect(R.id.page_number_indicator_view, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
                constraintSet.connect(R.id.page_nav_button_container, ConstraintSet.BOTTOM, R.id.thumbseekbar, ConstraintSet.TOP);
                constraintSet.clear(R.id.page_number_indicator_view, ConstraintSet.BOTTOM);
                if (mViewerConfig.getPageNumberIndicatorPosition() == PageIndicatorLayout.POSITION_TOP_START) {
                    constraintSet.connect(R.id.page_number_indicator_view, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START);
                    constraintSet.clear(R.id.page_number_indicator_view, ConstraintSet.END);
                } else if (mViewerConfig.getPageNumberIndicatorPosition() == PageIndicatorLayout.POSITION_TOP_END) {
                    constraintSet.connect(R.id.page_number_indicator_view, ConstraintSet.END, R.id.thumbseekbar_vert, ConstraintSet.START);
                    constraintSet.clear(R.id.page_number_indicator_view, ConstraintSet.START);
                }
                constraintSet.applyTo((ConstraintLayout) stub);
            }
        }

        mPageNumberIndicator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Activity activity = getActivity();
                if (activity == null) {
                    return;
                }

                DialogGoToPage dlgGotoPage = new DialogGoToPage(activity, mPdfViewCtrl, new DialogGoToPage.DialogGoToPageListener() {
                    @Override
                    public void onPageSet(int pageNum) {
                        setCurrentPageHelper(pageNum, true);
                        if (mReflowControl != null) {
                            try {
                                mReflowControl.setCurrentPage(pageNum);
                            } catch (Exception e) {
                                AnalyticsHandlerAdapter.getInstance().sendException(e);
                            }
                        }
                    }
                });
                dlgGotoPage.show();
            }
        });

        mPageNumberIndicatorAll = mPageNumberIndicator.getIndicator();
        if (Utils.isJellyBeanMR1()) {
            mPageNumberIndicatorAll.setTextDirection(View.TEXT_DIRECTION_LTR);
        }

        mPageNumberIndicatorSpinner = mPageNumberIndicator.getSpinner();

        // page stack container
        mPageNavContainer = stub.findViewById(R.id.page_nav_button_container);
        mPageNavContainer.setVisibility(View.INVISIBLE);

        // initialize page back button
        mPageBackStack = new ArrayDeque<>();
        mPageBackButton = stub.findViewById(R.id.page_back_button);
        mPageBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jumpPageBack();
            }
        });

        // initialize page forward button
        mPageForwardStack = new ArrayDeque<>();
        mPageForwardButton = stub.findViewById(R.id.page_forward_button);
        mPageForwardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jumpPageForward();
            }
        });

        // styling
        mFloatingNavTheme = FloatingNavTheme.fromContext(activity);
        View divider = stub.findViewById(R.id.page_nav_divider);
        divider.setBackgroundColor(mFloatingNavTheme.dividerColor);
        mPageNavContainer.setCardBackgroundColor(mFloatingNavTheme.backgroundColor);
        mPageBackButton.setBackgroundColor(mFloatingNavTheme.backgroundColor);
        mPageBackButton.setColorFilter(mFloatingNavTheme.iconColor);
        mPageForwardButton.setBackgroundColor(mFloatingNavTheme.backgroundColor);
        mPageForwardButton.setColorFilter(mFloatingNavTheme.iconColor);
    }

    public static final class FloatingNavTheme {
        @ColorInt
        public final int iconColor;
        @ColorInt
        public final int backgroundColor;
        @ColorInt
        public final int dividerColor;
        @ColorInt
        public final int disabledIconColor;

        FloatingNavTheme(int iconColor, int backgroundColor, int dividerColor, int disabledIconColor) {
            this.iconColor = iconColor;
            this.backgroundColor = backgroundColor;
            this.dividerColor = dividerColor;
            this.disabledIconColor = disabledIconColor;
        }

        public static FloatingNavTheme fromContext(@NonNull Context context) {
            final TypedArray a = context.obtainStyledAttributes(
                    null, R.styleable.FloatingNavTheme, R.attr.pt_floating_nav_style, R.style.DefaultFloatingButtonNavStyle);
            int iconColor = a.getColor(R.styleable.FloatingNavTheme_iconTint, context.getResources().getColor(R.color.pt_secondary_color));
            int backgroundColor = a.getColor(R.styleable.FloatingNavTheme_backgroundTint, context.getResources().getColor(R.color.pt_background_color));
            int dividerColor = a.getColor(R.styleable.FloatingNavTheme_dividerColor, context.getResources().getColor(R.color.pt_subtle_utility_color));
            int disabledColor = a.getColor(R.styleable.FloatingNavTheme_disabledIconColor, context.getResources().getColor(R.color.pt_disabled_state_color));
            a.recycle();
            return new FloatingNavTheme(iconColor, backgroundColor, dividerColor, disabledColor);
        }
    }

    private void setupGenericMotionEvent() {
        if (Utils.isNougat()) {
            View[] views = getGenericMotionEnabledViews();
            for (View v : views) {
                v.setOnGenericMotionListener(new View.OnGenericMotionListener() {
                    @Override
                    public boolean onGenericMotion(View v, MotionEvent event) {
                        Activity activity = getActivity();
                        if (activity == null || !Utils.isNougat()) {
                            return false;
                        }

                        getToolManager().onChangePointerIcon(PointerIcon.getSystemIcon(activity, PointerIcon.TYPE_HAND));
                        return true;
                    }
                });
            }
        }
    }

    protected void jumpPageBack() {
        // reset the Toolbar/ Thumbnail slider so it doesn't disappear while using
        // the page back and forward buttons
        if (mTabListener != null) {
            mTabListener.resetHideToolbarsTimer();
        }

        if (mPageStackListeners != null) {
            for (PageStackListener listener : mPageStackListeners) {
                if (listener.onPreJumpPageBack(mPageBackStack, mPageForwardStack)) {
                    return;
                }
            }
        }

        if (!mPageBackStack.isEmpty()) {
            PageBackButtonInfo previousPageInfo = mPageBackStack.pop();
            PageBackButtonInfo currentPageInfo = getCurrentPageInfo();
            boolean successfulPageChange = false;

            // if the top page on stack is the same as the current page,
            // pop the next page info off the stack
            if (previousPageInfo.pageNum == currentPageInfo.pageNum) {
                if (!mPageBackStack.isEmpty()) {
                    previousPageInfo = mPageBackStack.pop();
                } else {
                    // if the current page equals the last page on the back stack, no
                    // need to chang the page, just add it to the forward stack
                    successfulPageChange = true;
                }
            }

            if (!successfulPageChange && previousPageInfo.pageNum > 0 && previousPageInfo.pageNum <= mPageCount) {
                successfulPageChange = setPageState(previousPageInfo);
            }

            // Add the current page to forward page stack.
            if (successfulPageChange && (mPageForwardStack.isEmpty() || mPageForwardStack.peek().pageNum != currentPageInfo.pageNum)) {
                mPageForwardStack.push(currentPageInfo);
            }
        }

        // if that was the last element on the stack, set the button to be
        // disabled.
        if (mPageBackStack.isEmpty()) {
            hidePageBackButton();
        }

        if (!mPageForwardStack.isEmpty()) {
            showPageForwardButton();
        }

        if (mPageStackListeners != null) {
            for (PageStackListener listener : mPageStackListeners) {
                listener.onPostJumpPageBack(mPageBackStack, mPageForwardStack);
            }
        }
    }

    protected void jumpPageForward() {
        // reset the Toolbar/ Thumbnail slider so it doesn't disappear while using
        // the page back and forward buttons
        if (mTabListener != null) {
            mTabListener.resetHideToolbarsTimer();
        }

        if (mPageStackListeners != null) {
            for (PageStackListener listener : mPageStackListeners) {
                if (listener.onPreJumpPageForward(mPageBackStack, mPageForwardStack)) {
                    return;
                }
            }
        }

        if (!mPageForwardStack.isEmpty()) {
            PageBackButtonInfo nextPageInfo = mPageForwardStack.pop();
            PageBackButtonInfo currentPageInfo = getCurrentPageInfo();
            boolean successfulPageChange = false;

            if (currentPageInfo.pageNum == nextPageInfo.pageNum) {
                if (!mPageForwardStack.isEmpty()) {
                    nextPageInfo = mPageForwardStack.pop();
                } else {
                    successfulPageChange = true;
                }
            }

            if (!successfulPageChange && nextPageInfo.pageNum > 0 && nextPageInfo.pageNum <= mPageCount) {
                successfulPageChange = setPageState(nextPageInfo);
            }

            // Add the current page to back page stack.
            if (successfulPageChange && (mPageBackStack.isEmpty() || mPageBackStack.peek().pageNum != currentPageInfo.pageNum)) {
                mPageBackStack.push(currentPageInfo);
            }
        }

        // if that was the last element on the stack, set the button to be
        // disabled.
        if (mPageForwardStack.isEmpty()) {
            hidePageForwardButton();
        }

        if (!mPageBackStack.isEmpty()) {
            showPageBackButton();
        }

        if (mPageStackListeners != null) {
            for (PageStackListener listener : mPageStackListeners) {
                listener.onPostJumpPageForward(mPageBackStack, mPageForwardStack);
            }
        }
    }

    /**
     * Goes to the next text in search.
     */
    public void gotoNextSearch() {
        if (mSearchOverlay != null) {
            mSearchOverlay.gotoNextSearch();
        }
    }

    /**
     * Goes to the previous text in search.
     */
    public void gotoPreviousSearch() {
        if (mSearchOverlay != null) {
            mSearchOverlay.gotoPreviousSearch();
        }
    }

    /**
     * Cancels finding text.
     */
    public void cancelFindText() {
        if (mSearchOverlay != null) {
            mSearchOverlay.cancelFindText();
        }
    }

    /**
     * Specifies the search query.
     *
     * @param text The search query
     */
    public void setSearchQuery(String text) {
        if (mSearchOverlay != null) {
            mSearchOverlay.setSearchQuery(text);
        }
    }

    /**
     * Sets the search rule for match case.
     *
     * @param matchCase True if match case is enabled
     */
    public void setSearchMatchCase(boolean matchCase) {
        if (mSearchOverlay != null) {
            mSearchOverlay.setSearchMatchCase(matchCase);
        }
    }

    /**
     * Sets the search rule for whole word.
     *
     * @param wholeWord True if whole word is enabled
     */
    public void setSearchWholeWord(boolean wholeWord) {
        if (mSearchOverlay != null) {
            mSearchOverlay.setSearchWholeWord(wholeWord);
        }
    }

    /**
     * Sets the search rules for match case and whole word.
     *
     * @param matchCase True if match case is enabled
     * @param wholeWord True if whole word is enabled
     */
    @SuppressWarnings("unused")
    public void setSearchSettings(boolean matchCase, boolean wholeWord) {
        if (mSearchOverlay != null) {
            mSearchOverlay.setSearchSettings(matchCase, wholeWord);
        }
    }

    /**
     * Starts the TextHighlighter tool.
     */
    public void highlightSearchResults() {
        if (mSearchOverlay != null) {
            mSearchOverlay.highlightSearchResults();
        }
    }

    /**
     * Resets full text results.
     */
    public void resetFullTextResults() {
        if (mSearchOverlay != null) {
            mSearchOverlay.resetFullTextResults();
        }
    }

    /**
     * Submits the query text.
     *
     * @param text The query text
     */
    public void queryTextSubmit(String text) {
        if (mSearchOverlay != null) {
            mSearchOverlay.queryTextSubmit(text);
        }
    }

    /**
     * Exits the search mode.
     */
    public void exitSearchMode() {
        if (mSearchOverlay != null) {
            mSearchOverlay.exitSearchMode();
        }
    }

    protected void stopConversionFinishedTimer() {
        if (mConversionFinishedMessageHandler != null) {
            mConversionFinishedMessageHandler.removeCallbacksAndMessages(null);
        }
    }

    protected void stopConversionSpinningIndicator() {
        if (mPageNumberIndicatorConversionSpinningHandler != null) {
            mPageNumberIndicatorConversionSpinningHandler.removeCallbacksAndMessages(null);
        }
        if (mPageNumberIndicatorSpinner != null) {
            mPageNumberIndicatorSpinner.setVisibility(View.GONE);
        }
        mIsPageNumberIndicatorConversionSpinningRunning = false;
    }

    protected void stopHandlers() {
        stopConversionSpinningIndicator();
        stopConversionFinishedTimer();
        stopAutoSavingTimer();
        stopHidePageNumberIndicatorTimer();
    }

    // special case where landingPageInfo must be pushed onto the back stack AFTER the departurePageInfo
    // applies to internal links
    @SuppressWarnings("SameParameterValue")
    private void setCurrentPageHelper(int nextPageNum, boolean setPDFViewCtrl, PageBackButtonInfo landingPageInfo) {
        setCurrentPageHelper(nextPageNum, setPDFViewCtrl);
        mPageBackStack.push(landingPageInfo);
    }

    /**
     * Helper to set the current page.
     *
     * @param nextPageNum    The next page number
     * @param setPDFViewCtrl True if PDFViewCtrl should be set too
     */
    public void setCurrentPageHelper(int nextPageNum, boolean setPDFViewCtrl) {
        if (mPdfViewCtrl == null) {
            return;
        }

        ///////////////////         Page Back and Forward Buttons        ///////////////////
        PageBackButtonInfo departurePageInfo = new PageBackButtonInfo();
        boolean pageChangeOccurred = false;

        // if setSelectedPage needs to be called
        if (setPDFViewCtrl) {
            departurePageInfo = getCurrentPageInfo();
            // set current page state
            mPdfViewCtrl.setCurrentPage(nextPageNum);

            // if the page change will occur on its own
        } else {
            if (nextPageNum == mCurrentPageInfo.pageNum) {
                // if the page change has already occurred
                departurePageInfo.copyPageInfo(mPreviousPageInfo);
                pageChangeOccurred = true;
            } else {
                // if the page change has not occurred
                departurePageInfo = mCurrentPageInfo;
            }
        }

        // if the departure page number is within bounds and if the departure page's
        // page number does not equal the next page number.
        if (departurePageInfo.pageNum > 0 && departurePageInfo.pageNum <= mPageCount && departurePageInfo.pageNum != nextPageNum) {
            // if the top element's page number on the back stack does not equal the page number we are about
            // to push onto the stack (the departure page's page number)
            if ((mPageBackStack.isEmpty() || mPageBackStack.peek().pageNum != departurePageInfo.pageNum)) {
                // if the stack is at it's maximum size, then delete the bottom item of the stack
                if (mPageBackStack.size() >= MAX_SIZE_PAGE_BACK_BUTTON_STACK) {
                    mPageBackStack.removeLast();
                }

                // top element's page number of the back stack is the same as the page we are trying to
                // push onto the stack (departure page's number)
            } else {
                // remove top element and replace with a more recent version of that page's state
                mPageBackStack.pop();
            }

            mPageBackStack.push(departurePageInfo);
            if (!pageChangeOccurred) {
                mPushNextPageOnStack = true;
            }

            // if an element is pushed onto the back page stack we have
            // to clear the forward page stack (if it isn't empty already).
            if (!mPageForwardStack.isEmpty()) {
                mPageForwardStack.clear();
            }
        }

        // set the visibility of the page back and forward buttons
        if (!mPageBackStack.isEmpty()) {
            if (!mSearchOverlay.isShown()) {
                showPageBackButton();
            }
        }

        if (mPageForwardStack.isEmpty()) {
            hidePageForwardButton();
        }

        ///////////////////              Reflow              ///////////////////
        if (mIsReflowMode && mReflowControl != null) {
            // Note no need to check setPdfViewCtrl since if it is in continuous mode, the current
            // page may not be updated in PDFViewCtrl (it actually updates in PDFViewCtrl.onDraw),
            // so we have to set the current page explicitly
            try {
                mReflowControl.setCurrentPage(nextPageNum);
            } catch (Exception e) {
                AnalyticsHandlerAdapter.getInstance().sendException(e);
            }
        }
    }

    /**
     * Updates the current page info.
     */
    public void updateCurrentPageInfo() {
        mCurrentPageInfo = getCurrentPageInfo();
    }

    /**
     * Returns the current page info.
     *
     * @return The current page info
     */
    public PageBackButtonInfo getCurrentPageInfo() {
        PageBackButtonInfo pageState = new PageBackButtonInfo();

        if (mPdfViewCtrl != null) {
            pageState.zoom = mPdfViewCtrl.getZoom();
            pageState.pageRotation = mPdfViewCtrl.getPageRotation();
            pageState.pagePresentationMode = mPdfViewCtrl.getPagePresentationMode();
            pageState.hScrollPos = mPdfViewCtrl.getHScrollPos();
            pageState.vScrollPos = mPdfViewCtrl.getVScrollPos();
            pageState.pageNum = mPdfViewCtrl.getCurrentPage();
        }

        return pageState;
    }

    private void hidePageForwardButton() {
        mPageForwardButton.setEnabled(false);
        mPageForwardButton.setBackgroundColor(mFloatingNavTheme.dividerColor);
        mPageForwardButton.setColorFilter(mFloatingNavTheme.disabledIconColor);
    }

    private void hidePageBackButton() {
        mPageBackButton.setEnabled(false);
        mPageBackButton.setBackgroundColor(mFloatingNavTheme.dividerColor);
        mPageBackButton.setColorFilter(mFloatingNavTheme.disabledIconColor);
    }

    protected void showPageForwardButton() {
        showBackAndForwardButtons();
        if (mViewerConfig == null || mViewerConfig.isShowQuickNavigationButton()) {
            mPageForwardButton.setEnabled(true);
            mPageForwardButton.setBackgroundColor(mFloatingNavTheme.backgroundColor);
            mPageForwardButton.setColorFilter(mFloatingNavTheme.iconColor);
        }
    }

    protected void showPageBackButton() {
        showBackAndForwardButtons();
        if (mViewerConfig == null || mViewerConfig.isShowQuickNavigationButton()) {
            mPageBackButton.setEnabled(true);
            mPageBackButton.setBackgroundColor(mFloatingNavTheme.backgroundColor);
            mPageBackButton.setColorFilter(mFloatingNavTheme.iconColor);
        }
    }

    /**
     * Clears the stacks of page backward/forward.
     */
    public void clearPageBackAndForwardStacks() {
        hidePageBackButton();
        hidePageForwardButton();

        // clear page button stacks
        mPageBackStack.clear();
        mPageForwardStack.clear();
    }

    /**
     * Checks if the tab is ready only.
     *
     * @return True if the tab is read only
     */
    public boolean isTabReadOnly() {
        if (mToolManager != null && mToolManager.skipReadOnlyCheck()) {
            return mDocumentState == PdfDocManager.DOCUMENT_STATE_DURING_CONVERSION;
        }
        return (mDocumentState == PdfDocManager.DOCUMENT_STATE_READ_ONLY ||
                mDocumentState == PdfDocManager.DOCUMENT_STATE_READ_ONLY_AND_MODIFIED ||
                mDocumentState == PdfDocManager.DOCUMENT_STATE_CORRUPTED ||
                mDocumentState == PdfDocManager.DOCUMENT_STATE_CORRUPTED_AND_MODIFIED ||
                mDocumentState == PdfDocManager.DOCUMENT_STATE_DURING_CONVERSION ||
                mDocumentState == PdfDocManager.DOCUMENT_STATE_FROM_CONVERSION ||
                mDocumentState == PdfDocManager.DOCUMENT_STATE_OUT_OF_SPACE ||
                (mToolManager != null && mToolManager.isReadOnly()));
    }

    @SuppressWarnings("unused")
    private RegionSingleTap getRegionTap(int x, int y) {
        RegionSingleTap regionSingleTap = RegionSingleTap.Middle;

        if (mPdfViewCtrl != null) {
            float width = mPdfViewCtrl.getWidth();
            float widthThresh = width * TAP_REGION_THRESHOLD;
            if (x <= widthThresh) {
                regionSingleTap = RegionSingleTap.Left;
            } else if (x >= width - widthThresh) {
                regionSingleTap = RegionSingleTap.Right;
            }
        }

        return regionSingleTap;
    }

    /**
     * Checks if the tab is in continuous page mode.
     *
     * @return True if the tab is in continuous page mode
     * @see #isSinglePageMode
     */
    public boolean isContinuousPageMode() {
        return ViewerUtils.isContinuousPageMode(mPdfViewCtrl);
    }

    public boolean isNonContinuousVerticalPageMode() {
        return ViewerUtils.isNonContinuousVerticalPageMode(mPdfViewCtrl);
    }

    /**
     * Checks if the tab is in single page mode.
     *
     * @return True if the tab is in single page mode
     * @see #isContinuousPageMode
     */
    public boolean isSinglePageMode() {
        return ViewerUtils.isSinglePageMode(mPdfViewCtrl);
    }

    private boolean setPageState(PageBackButtonInfo pageStateInfo) {
        if (mPdfViewCtrl == null) {
            return false;
        }

        boolean successfulPageChange = mPdfViewCtrl.setCurrentPage(pageStateInfo.pageNum);

        if (mIsReflowMode && mReflowControl != null) {
            try {
                mReflowControl.setCurrentPage(pageStateInfo.pageNum);
            } catch (Exception e) {
                AnalyticsHandlerAdapter.getInstance().sendException(e);
            }
        }

        // if the page change was successful AND the page rotation has not changed AND
        // the page presentation mode has not changed, set the page state based off of the
        // pageStateInfo
        if (successfulPageChange && pageStateInfo.pageRotation == mPdfViewCtrl.getPageRotation() &&
                pageStateInfo.pagePresentationMode == mPdfViewCtrl.getPagePresentationMode()) {

            // deal with situation where setZoom is larger than max zoom
            double desiredHPos = pageStateInfo.hScrollPos;
            double desiredVPos = pageStateInfo.vScrollPos;
            if (pageStateInfo.zoom > 0) {
                mPdfViewCtrl.setZoom(pageStateInfo.zoom);
                if (Math.abs(mPdfViewCtrl.getZoom() - pageStateInfo.zoom) > 0.01) {
                    double zoomDifference = mPdfViewCtrl.getZoom() / pageStateInfo.zoom;
                    desiredHPos *= zoomDifference;
                    desiredVPos *= zoomDifference;
                }
            }
            // end
            if (desiredHPos > 0 || desiredVPos > 0) {
                mPdfViewCtrl.scrollTo((int) desiredHPos, (int) desiredVPos);
            }
        }

        return successfulPageChange;
    }

    /**
     * Checks if the tab is in reflow mode.
     *
     * @return True if the tab is in reflow mode
     */
    public boolean isReflowMode() {
        return mIsReflowMode;
    }

    /**
     * Toggles the reflow mode.
     *
     * @return True if the reflow mode will be enabled after toggling
     */
    @SuppressWarnings("UnusedReturnValue")
    public boolean toggleReflow() {
        mIsReflowMode = !mIsReflowMode;
        setReflowMode(mIsReflowMode);
        return mIsReflowMode;
    }

    /**
     * Returns the reflow text size.
     *
     * @return The reflow text size.
     */
    public int getReflowTextSize() {
        try {
            if (mReflowControl != null && mReflowControl.isReady()) {
                return mReflowControl.getTextSizeInPercent();
            }
        } catch (Exception e) {
            AnalyticsHandlerAdapter.getInstance().sendException(e);
        }
        return 100;
    }

    /**
     * Zooms in/out reflow mode.
     *
     * @param flagZoomIn True if zoom in; False if zoom out
     */
    public void zoomInOutReflow(boolean flagZoomIn) {
        if (mIsReflowMode && mReflowControl != null) {
            try {
                if (flagZoomIn) {
                    mReflowControl.zoomIn();
                } else {
                    mReflowControl.zoomOut();
                }
            } catch (Exception e) {
                AnalyticsHandlerAdapter.getInstance().sendException(e);
            }
        }
    }

    /**
     * Sets the reflow mode.
     *
     * @param isReflowMode True if reflow mode is enabled
     */
    public void setReflowMode(boolean isReflowMode) {
        Activity activity = getActivity();
        if (activity == null || mPdfViewCtrl == null || mPdfDoc == null) {
            return;
        }
        loadReflowView();
        if (mReflowControl == null) {
            return;
        }

        mIsReflowMode = isReflowMode;
        if (mIsReflowMode) {
            int pageNum = mPdfViewCtrl.getCurrentPage();
            mReflowControl.setup(mPdfViewCtrl.getDoc(), mToolManager, mOnPostProcessColorListener);
            mReflowControl.setReflowUrlLoadedListener(mReflowUrlLoadedListener);
            mReflowControl.setAnnotStyleProperties(mToolManager.getAnnotStyleProperties());
            mReflowControl.setEditingEnabled(mViewerConfig == null || mViewerConfig.isDocumentEditingEnabled());
            // Reflow control might have not been ready at the time RTL mode was set,
            // so to make sure RTL mode is set appropriately set it once again here
            setRtlMode(mIsRtlMode);
            boolean vertical = isContinuousPageMode() || isNonContinuousVerticalPageMode();
            boolean followPdfViewCtrl = true;
            if (mViewerConfig != null) {
                if (mViewerConfig.getReflowOrientation() == ReflowControl.VERTICAL ||
                        mViewerConfig.getReflowOrientation() == ReflowControl.HORIZONTAL) {
                    mReflowControl.setOrientation(mViewerConfig.getReflowOrientation());
                    followPdfViewCtrl = false;
                }
                mReflowControl.setImageInReflowEnabled(mViewerConfig.isImageInReflowEnabled());
            }
            if (followPdfViewCtrl) {
                mReflowControl.setOrientation(vertical ? ReflowControl.VERTICAL : ReflowControl.HORIZONTAL);
            }
            mReflowControl.clearReflowOnTapListeners();
            mReflowControl.clearOnPageChangeListeners();
            mReflowControl.addReflowOnTapListener(this);
            mReflowControl.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                }

                @Override
                public void onPageSelected(int position) {
                    if (!mIsReflowMode) {
                        return;
                    }

                    if (mIsRtlMode) {
                        position = mPageCount - 1 - position;
                    }
                    int curPage = position + 1;
                    int oldPage = mPdfViewCtrl.getCurrentPage();

                    // if an internal link is clicked, call setCurrentPageHelper to
                    // pass the event to the page back and forward buttons
                    try {
                        if (mReflowControl.isInternalLinkClicked()) {
                            mReflowControl.resetInternalLinkClicked();
                            if (oldPage != curPage) {
                                setCurrentPageHelper(curPage, false, getCurrentPageInfo());
                            }
                        }
                        mReflowControl.updateTextSize();
                    } catch (Exception e) {
                        AnalyticsHandlerAdapter.getInstance().sendException(e);
                    }

                    mPdfViewCtrl.setCurrentPage(curPage);
                    updatePageIndicator();
                    if (mPageViewModel != null) {
                        mPageViewModel.onPageChange(new PageState(curPage));
                    }
                }

                @Override
                public void onPageScrollStateChanged(int state) {
                }
            });

            // It is possible that new markup annotations were added to the document
            save(false, true, false);
            try {
                mReflowControl.notifyPagesModified();
                mReflowControl.setCurrentPage(pageNum);
                mReflowControl.enableTurnPageOnTap(PdfViewCtrlSettingsManager.getAllowPageChangeOnTap(activity));
                if (mReflowTextSize >= 0) {
                    mReflowControl.setTextSizeInPercent(mReflowTextSize);
                }
            } catch (Exception e) {
                AnalyticsHandlerAdapter.getInstance().sendException(e);
            }
            mReflowControl.setVisibility(View.VISIBLE);
            updateReflowColorMode();
            mPdfViewCtrl.setCurrentPage(pageNum);

            updatePageIndicator();

            // hide and pause PDFViewCtrl
            setViewerHostVisible(false);
            mPdfViewCtrl.pause();
        } else {
            // hide reflow
            try {
                mReflowTextSize = mReflowControl.getTextSizeInPercent();
            } catch (PDFNetException e) {
                AnalyticsHandlerAdapter.getInstance().sendException(e);
            }
            mReflowControl.cleanUp();
            mReflowControl.setVisibility(View.GONE);
            mReflowControl.removeReflowOnTapListener(this);

            // since changes happening in reflow mode does not go through PDFViewCtrl,
            // here we take safety undo snapshot
            if (mToolManager.getUndoRedoManger() != null) {
                mToolManager.getUndoRedoManger().takeUndoSnapshotForSafety();
            }

            // switch back to PDFViewCtrl
            mPdfViewCtrl.resume();
            try {
                mPdfViewCtrl.docLockRead(new PDFViewCtrl.LockRunnable() {
                    @Override
                    public void run() throws Exception {
                        mPdfViewCtrl.update(true); // to update any added annots
                    }
                });
            } catch (Exception ignored) {
            }
            setViewerHostVisible(true);
        }
    }

    /**
     * Toggles right-to-left mode.
     *
     * @return True if right-to-left mode is on after toggling
     */
    @SuppressWarnings("UnusedReturnValue")
    public boolean toggleRtlMode() {
        setRtlMode(!mIsRtlMode);
        return mIsRtlMode;
    }

    /**
     * Sets right-to-left mode
     *
     * @param isRtlMode True if right-to-left mode is enabled
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void setRtlMode(boolean isRtlMode) {
        if (mPdfViewCtrl == null) {
            return;
        }

        mIsRtlMode = isRtlMode;
        PdfViewCtrlSettingsManager.updateInRTLMode(mPdfViewCtrl.getContext(), isRtlMode);
        try {
            if (mReflowControl != null && mReflowControl.isReady()) {
                mReflowControl.setRightToLeftDirection(isRtlMode);
                if (mIsReflowMode && mPdfViewCtrl != null) {
                    int pageNum = mPdfViewCtrl.getCurrentPage();
                    mReflowControl.reset();
                    mReflowControl.setCurrentPage(pageNum);
                    mPdfViewCtrl.setCurrentPage(pageNum);
                }
            }
            if (mPdfViewCtrl != null) {
                mPdfViewCtrl.docLockRead(new PDFViewCtrl.LockRunnable() {
                    @Override
                    public void run() throws Exception {
                        mPdfViewCtrl.setRightToLeftLanguage(isRtlMode);
                    }
                });
            }
        } catch (Exception e) {
            AnalyticsHandlerAdapter.getInstance().sendException(e);
        }
        if (Utils.isJellyBeanMR1()) {
            Configuration config = getResources().getConfiguration();
            if ((config.getLayoutDirection() == View.LAYOUT_DIRECTION_RTL && !isRtlMode) ||
                    (config.getLayoutDirection() != View.LAYOUT_DIRECTION_RTL && isRtlMode)) {
                sliderSetReversed(true);
            } else {
                sliderSetReversed(false);
            }
        }
    }

    /**
     * Checks whether right-to-left mode is enabled
     *
     * @return True if right-to-left mode is enabled.
     */
    public boolean isRtlMode() {
        return mIsRtlMode;
    }

    protected void doDocumentLoaded() {
        Activity activity = getActivity();
        if (activity == null || mPdfViewCtrl == null) {
            return;
        }

        if (mDocumentLoaded) {
            return;
        }
        mDocumentLoaded = true;

        mRageScrollingCount = 0; // reset count

        // setup reflow control
        if (mReflowControl != null) {
            mReflowControl.setup(mPdfViewCtrl.getDoc(), mToolManager, mOnPostProcessColorListener);
        }

        setViewerHostVisible(true);
        PdfViewCtrlTabInfo info = null;
        if (PdfViewCtrlSettingsManager.getRememberLastPage(activity)) {
            info = PdfViewCtrlTabsManager.getInstance().getPdfFViewCtrlTabInfo(activity, mTabTag);
            if (info == null && mStateEnabled) {
                info = getInfoFromRecentList(getCurrentFileInfo());
            }
        }

        boolean skipSetState = false;
        if (!mStateEnabled) {
            skipSetState = true;
        }
        if (mTabConversionTempPath == null &&
                (mDocumentState == PdfDocManager.DOCUMENT_STATE_FROM_CONVERSION ||
                        mDocumentState == PdfDocManager.DOCUMENT_STATE_DURING_CONVERSION)) {
            // do not load saved state for newly opened conversion file
            skipSetState = true;
        }

        if (info != null && !skipSetState) {
            // centralized place to restore previous viewing properties
            PDFViewCtrl.PagePresentationMode pagePresentationMode;
            if (info.hasPagePresentationMode()) {
                pagePresentationMode = info.getPagePresentationMode();
            } else {
                // set to last chosen default
                String mode = PdfViewCtrlSettingsManager.getViewMode(activity);
                pagePresentationMode = getPagePresentationModeFromSettings(mode);
            }
            updateViewMode(pagePresentationMode);

            // note: setRtlMode should be before setSelectedPage; otherwise, mCurCanvasScrollXOffsetSave
            // in PDFViewCtrl is not going to be set correctly for RTL
            if ((mViewerConfig != null && mViewerConfig.isShowRightToLeftOption()) ||
                    PdfViewCtrlSettingsManager.hasRtlModeOption(activity)) {
                PdfViewCtrlSettingsManager.updateRtlModeOption(activity, true);
                // don't allow RTL mode when there is no setting to turn it off
                if (!info.isRtlMode) {
                    info.isRtlMode = mViewerConfig != null && mViewerConfig.isRightToLeftModeEnabled();
                }
                setRtlMode(info.isRtlMode);
            }

            if (info.lastPage > 0) {
                mPdfViewCtrl.setCurrentPage(info.lastPage);
            } else {
                if (mViewerConfig != null) {
                    // if opened from open url, try to go back to the last page opened before
                    int lastOpenUrlPage = ViewerUtils.getLastPageForURL(activity, mOpenUrlLink);
                    if (lastOpenUrlPage > 0) {
                        mPdfViewCtrl.setCurrentPage(lastOpenUrlPage);
                    }
                }
            }

            try {
                switch (info.pageRotation) {
                    case Page.e_0:
                        // do nothing if no rotation
                        break;
                    case Page.e_90:
                        mPdfViewCtrl.rotateClockwise();
                        ViewerUtils.safeUpdatePageLayout(mPdfViewCtrl);
                        break;
                    case Page.e_180:
                        mPdfViewCtrl.rotateClockwise();
                        mPdfViewCtrl.rotateClockwise();
                        ViewerUtils.safeUpdatePageLayout(mPdfViewCtrl);
                        break;
                    case Page.e_270:
                        mPdfViewCtrl.rotateCounterClockwise();
                        ViewerUtils.safeUpdatePageLayout(mPdfViewCtrl);
                        break;
                }
            } catch (Exception e) {
                AnalyticsHandlerAdapter.getInstance().sendException(e);
            }
            if (info.zoom > 0.0) {
                mPdfViewCtrl.setZoom(info.zoom);
            }
            if (info.hScrollPos > 0 || info.vScrollPos > 0) {
                mPdfViewCtrl.scrollTo(info.hScrollPos, info.vScrollPos);
            }
            if (info.isReflowMode != isReflowMode()) {
                if (mTabListener != null) {
                    mTabListener.onToggleReflow();
                }
            }
            if (mReflowControl != null && mReflowControl.isReady()) {
                try {
                    mReflowControl.setTextSizeInPercent(info.reflowTextSize);
                } catch (Exception e) {
                    AnalyticsHandlerAdapter.getInstance().sendException(e);
                }
            }
            mBookmarkDialogCurrentTab = info.bookmarkDialogCurrentTab;
        } else {
            // set to last chosen default
            String mode = PdfViewCtrlSettingsManager.getViewMode(activity);
            PDFViewCtrl.PagePresentationMode pagePresentationMode = getPagePresentationModeFromSettings(mode);
            updateViewMode(pagePresentationMode);
        }

        if (mBookmarkDialogCurrentTab == -1) {
            mBookmarkDialogCurrentTab = Utils.getFirstBookmark(mPdfViewCtrl.getDoc()) != null ? 1 : 0;
        }

        updateColorMode();

        PdfViewCtrlTabInfo tempInfo = saveCurrentPdfViewCtrlState();

        if (info != null) {
            addToRecentList(info);
        } else {
            addToRecentList(tempInfo);
        }

        PdfViewCtrlTabsManager.getInstance().updateLastViewedTabTimestamp(getActivity(), mTabTag);

        if (mTabListener != null) {
            mTabListener.onTabDocumentLoaded(getTabTag());
        }

        toggleViewerVisibility(true);

        if (mToolManager != null) {
            String freeTextCacheFilename = mToolManager.getFreeTextCacheFileName();
            if (Utils.cacheFileExists(getContext(), freeTextCacheFilename)) {
                createRetrieveChangesDialog(freeTextCacheFilename);
            }
            if (mViewerConfig != null) {
                if (!mViewerConfig.isDocumentEditingEnabled()) {
                    mToolManager.setReadOnly(true);
                }
                if (!mViewerConfig.isLongPressQuickMenuEnabled()) {
                    mToolManager.setDisableQuickMenu(true);
                }
            }
        }

        if (mImageStampDelayCreation) {
            mImageStampDelayCreation = false;
            ViewerUtils.createImageStamp(activity, mAnnotIntentData, mPdfViewCtrl, mOutputFileUri, mAnnotTargetPoint);
        }

        if (mImageSignatureDelayCreation) {
            mImageSignatureDelayCreation = false;
            consumeImageSignature();
        }

        if (mFileAttachmentDelayCreation) {
            mFileAttachmentDelayCreation = false;
            ViewerUtils.createFileAttachment(getActivity(), mAnnotIntentData, mPdfViewCtrl, mAnnotTargetPoint);
        }

        if (Utils.isLargeScreenWidth(activity)) {
            mPdfViewCtrl.setFocusableInTouchMode(true);
            mPdfViewCtrl.requestFocus();
        }

        if (mAnnotationToolbarShow) {
            mAnnotationToolbarShow = false;
            if (mTabListener != null) {
                if (mAnnotationToolbarToolMode == ToolMode.INK_CREATE) {
                    mTabListener.onOpenEditToolbar(mAnnotationToolbarToolMode);
                } else {
                    mTabListener.onOpenAnnotationToolbar(mAnnotationToolbarToolMode);
                }
            }
        }

        if (mInitialPage > 0) {
            mPdfViewCtrl.setCurrentPage(mInitialPage);
        }

        loadBookmarks();
    }

    protected PDFViewCtrl.PagePresentationMode getPagePresentationModeFromSettings(String mode) {
        PDFViewCtrl.PagePresentationMode pagePresentationMode = PDFViewCtrl.PagePresentationMode.SINGLE_CONT;
        if (mode.equalsIgnoreCase(PdfViewCtrlSettingsManager.KEY_PREF_VIEWMODE_CONTINUOUS_VALUE)) {
            pagePresentationMode = PDFViewCtrl.PagePresentationMode.SINGLE_CONT;
        } else if (mode.equalsIgnoreCase(PdfViewCtrlSettingsManager.KEY_PREF_VIEWMODE_SINGLEPAGE_VALUE)) {
            pagePresentationMode = PDFViewCtrl.PagePresentationMode.SINGLE;
        } else if (mode.equalsIgnoreCase(PdfViewCtrlSettingsManager.KEY_PREF_VIEWMODE_FACING_VALUE)) {
            pagePresentationMode = PDFViewCtrl.PagePresentationMode.FACING;
        } else if (mode.equalsIgnoreCase(PdfViewCtrlSettingsManager.KEY_PREF_VIEWMODE_FACINGCOVER_VALUE)) {
            pagePresentationMode = PDFViewCtrl.PagePresentationMode.FACING_COVER;
        } else if (mode.equalsIgnoreCase(PdfViewCtrlSettingsManager.KEY_PREF_VIEWMODE_FACING_CONT_VALUE)) {
            pagePresentationMode = PDFViewCtrl.PagePresentationMode.FACING_CONT;
        } else if (mode.equalsIgnoreCase(PdfViewCtrlSettingsManager.KEY_PREF_VIEWMODE_FACINGCOVER_CONT_VALUE)) {
            pagePresentationMode = PDFViewCtrl.PagePresentationMode.FACING_COVER_CONT;
        }
        return pagePresentationMode;
    }

    protected PdfViewCtrlTabInfo getInfoFromRecentList(FileInfo fileInfo) {
        Activity activity = getActivity();
        if (activity == null) {
            return null;
        }

        // if it is a new tab let's use recent list's information
        FileInfo recentFileInfo = RecentFilesManager.getInstance().getFile(activity, fileInfo);
        return createTabInfoFromFileInfo(recentFileInfo);
    }

    protected PdfViewCtrlTabInfo createTabInfoFromFileInfo(FileInfo fileInfo) {
        PdfViewCtrlTabInfo info = new PdfViewCtrlTabInfo();
        if (fileInfo == null) {
            return null;
        }
        info.tabSource = fileInfo.getType();
        info.lastPage = fileInfo.getLastPage();
        info.pageRotation = fileInfo.getPageRotation();
        info.setPagePresentationMode(fileInfo.getPagePresentationMode());
        info.hScrollPos = fileInfo.getHScrollPos();
        info.vScrollPos = fileInfo.getVScrollPos();
        info.zoom = fileInfo.getZoom();
        info.isReflowMode = fileInfo.isReflowMode();
        info.reflowTextSize = fileInfo.getReflowTextSize();
        info.isRtlMode = fileInfo.isRtlMode();
        info.bookmarkDialogCurrentTab = fileInfo.getBookmarkDialogCurrentTab();
        return info;
    }

    /**
     * Saves the changes to the document forcefully
     */
    public void forceSave() {
        showDocumentSavedToast();
        save(true, true, false, true);
    }

    protected void showDocumentSavedToast() {
        Activity activity = getActivity();
        if (activity == null || isNotPdf()) {
            return;
        }

        if (mHasChangesSinceResumed) {
            mHasChangesSinceResumed = false;
            if (!mWasSavedAndClosedShown) {
                CommonToast.showText(activity, R.string.document_saved_toast_message, Toast.LENGTH_SHORT);
            }
        }
    }

    /**
     * Saves the changes to the document.
     *
     * @param close                True if the document should be closed
     * @param forceSave            True if save should be done forcefully
     * @param skipSpecialFileCheck True if special file check should be skipped
     */
    public void save(boolean close, boolean forceSave, boolean skipSpecialFileCheck) {
        save(close, forceSave, skipSpecialFileCheck, close);
    }

    /**
     * Saves the changes to the document.
     *
     * @param close                True if the document should be closed
     * @param forceSave            True if save should be done forcefully
     * @param skipSpecialFileCheck True if special file check should be skipped
     * @param upload               True if the document should be uploaded
     */
    public void save(boolean close, boolean forceSave, boolean skipSpecialFileCheck, boolean upload) {
        if (isNotPdf()) {
            // save changes to temp file
            saveConversionTempHelper(close, forceSave, true);
            return;
        }
        // commit multi-stroke ink first
        if (forceSave && getToolManager() != null) {
            ToolManager.Tool currentTool = getToolManager().getTool();
            if (currentTool instanceof FreehandCreate) {
                ((FreehandCreate) currentTool).saveAnnotation();
            }
        }
        // commit sound recordings
        if (close && getToolManager() != null) {
            getToolManager().getSoundManager().close();
        }
        synchronized (saveDocumentLock) {
            if (mDocumentConversion == null && Utils.isDocModified(mPdfDoc)) {
                switch (mDocumentState) {
                    case PdfDocManager.DOCUMENT_STATE_CLEAN:
                    case PdfDocManager.DOCUMENT_STATE_NORMAL:
                    case PdfDocManager.DOCUMENT_STATE_MODIFIED:
                        mDocumentState = PdfDocManager.DOCUMENT_STATE_MODIFIED;
                        saveHelper(close, forceSave, true, upload);
                        break;
                    case PdfDocManager.DOCUMENT_STATE_READ_ONLY:
                        saveHelper(close, forceSave, false, upload);
                        break;
                    case PdfDocManager.DOCUMENT_STATE_READ_ONLY_AND_MODIFIED:
                        if (!skipSpecialFileCheck) {
                            handleSpecialFile(close);
                        }
                        break;
                    case PdfDocManager.DOCUMENT_STATE_CORRUPTED:
                        saveHelper(close, forceSave, false, upload);
                        break;
                    case PdfDocManager.DOCUMENT_STATE_CORRUPTED_AND_MODIFIED:
                        if (!skipSpecialFileCheck) {
                            handleSpecialFile(close);
                        }
                        break;
                    case PdfDocManager.DOCUMENT_STATE_FROM_CONVERSION:
                        // save changes to temp file
                        saveConversionTempHelper(close, forceSave, true);
                        break;
                    default:
                        if (close) {
                            // likely during conversion
                            // responsible for closing any cloud resources
                            saveHelper(true, forceSave, false, upload);
                        }
                        break;
                }
            } else {
                saveHelper(close, forceSave, false, upload);
            }
        }
    }

    protected void handleFailedSave(boolean close, Exception e) {
        Activity activity = getActivity();
        if (activity == null) {
            return;
        }
        // we have trouble saving
        boolean handled = false;
        if (Utils.isLollipop() && mCurrentFile != null) {
            boolean isSDCardFile = Utils.isSdCardFile(activity, mCurrentFile);
            if (isSDCardFile) {
                // this is normal, no permission
                mDocumentState = PdfDocManager.DOCUMENT_STATE_READ_ONLY;
                handled = true;
            }
        }
        if (!handled) {
            mDocumentState = PdfDocManager.DOCUMENT_STATE_COULD_NOT_SAVE;
        }
        if (!mToolManager.isReadOnly()) {
            mToolManager.setReadOnly(true);
        }
        handleSpecialFile(close);
    }

    protected void saveHelper(boolean close, boolean forceSave, boolean hasChangesSinceLastSave, boolean upload) {
        if (!mSavingEnabled) return;
        if (forceSave) {
            if (mPdfViewCtrl != null) {
                mPdfViewCtrl.cancelRendering();
            }
        }
        switch (mTabSource) {
            case BaseFileInfo.FILE_TYPE_EXTERNAL:
                if (hasChangesSinceLastSave) {
                    saveExternalFile(close, forceSave);
                }
                break;
            case BaseFileInfo.FILE_TYPE_FILE:
                if (hasChangesSinceLastSave) {
                    saveLocalFile(close, forceSave);
                }
                break;
            case BaseFileInfo.FILE_TYPE_EDIT_URI:
                if (hasChangesSinceLastSave) {
                    saveLocalFile(close, forceSave);
                }
                if (close) {
                    saveBackEditUri();
                }
                break;
        }
        if (hasChangesSinceLastSave && mDocumentState == PdfDocManager.DOCUMENT_STATE_MODIFIED) {
            mDocumentState = PdfDocManager.DOCUMENT_STATE_NORMAL;
        }

        if (forceSave && !close) {
            if (mPdfViewCtrl != null) {
                mPdfViewCtrl.requestRendering();
            }
        }
    }

    protected boolean docLock(boolean forceLock) {
        if (mPdfViewCtrl == null || mPdfViewCtrl.getDoc() == null) {
            return false;
        }

        try {
            if (forceLock) {
                if (sDebug) {
                    Log.d(TAG, "PDFDoc FORCE LOCK");
                }
                mPdfViewCtrl.docLock(true);
                return true;
            } else {
                if (sDebug) {
                    Log.d(TAG, "PDFDoc TRY LOCK");
                }
                return mPdfViewCtrl.docTryLock(500);
            }
        } catch (PDFNetException e) {
            AnalyticsHandlerAdapter.getInstance().sendException(e);
            return false;
        }
    }

    protected void docUnlock() {
        if (mPdfViewCtrl == null) {
            return;
        }
        mPdfViewCtrl.docUnlock();
    }

    protected void checkDocIntegrity() {
        mHasChangesSinceOpened = true;
        mHasChangesSinceResumed = true;
        mNeedsCleanupFile = false;
    }

    /**
     * Saves local document file.
     *
     * @param close     True if the document should be closed
     * @param forceSave True if save should be forcefully
     */
    public void saveLocalFile(boolean close, boolean forceSave) {
        if (mCurrentFile != null) {
            if (Utils.isNotPdf(mCurrentFile.getAbsolutePath()))
                return;
            boolean shouldUnlock = false;
            try {
                shouldUnlock = docLock(close || forceSave);
                if (shouldUnlock) {
                    if (mPdfViewCtrl != null && mPdfViewCtrl.getDoc() == null) {
                        AnalyticsHandlerAdapter.getInstance().sendException(
                                new Exception("doc from PdfViewCtrl is null while we lock the document!"
                                        + (mPdfDoc == null ? "" : " and the mPdfDoc is not null!")
                                        + (" | source: " + mTabSource)));
                    }
                    if (sDebug) {
                        Log.d(TAG, "save local");
                        Log.d(TAG, "doc locked");
                    }
                    if (mToolManager.getUndoRedoManger() != null) {
                        mToolManager.getUndoRedoManger().takeUndoSnapshotForSafety();
                    }
                    mPdfDoc.save(mCurrentFile.getAbsolutePath(), SDFDoc.SaveMode.INCREMENTAL, null);
                    mLastSuccessfulSave = System.currentTimeMillis();
                    checkDocIntegrity();
                }
            } catch (Exception e) {
                // This file is most likely readonly (i.e. from external SD card for KitKat devices,
                // or can be a document with a repaired XRef table
                handleFailedSave(close, e);
                AnalyticsHandlerAdapter.getInstance().sendException(e);
            } finally {
                if (shouldUnlock) {
                    docUnlock();
                }
            }
        }
    }

    /**
     * Saves external document file.
     *
     * @param close     True if the document should be closed
     * @param forceSave True if save should be forcefully
     */
    public void saveExternalFile(boolean close, boolean forceSave) {
        if (mCurrentUriFile != null) {
            boolean shouldUnlock = false;
            try {
                shouldUnlock = docLock(close || forceSave);
                if (shouldUnlock) {
                    if (sDebug) {
                        Log.d(TAG, "save external file");
                        Log.d(TAG, "save external doc locked");
                    }
                    if (mToolManager.getUndoRedoManger() != null) {
                        mToolManager.getUndoRedoManger().takeUndoSnapshotForSafety();
                    }
                    mPdfDoc.save();
                    mLastSuccessfulSave = System.currentTimeMillis();
                    checkDocIntegrity();
                }
            } catch (Exception e) {
                handleFailedSave(close, e);
                AnalyticsHandlerAdapter.getInstance().sendException(e);
            } finally {
                if (shouldUnlock) {
                    docUnlock();
                }
            }
        }
    }

    private void saveBackEditUri() {
        Activity activity = getActivity();
        if (activity == null) {
            return;
        }
        if (mCurrentFile == null || !mCurrentFile.exists()) {
            return;
        }

        mSaveBackUriDisposable = saveBackEditUriImpl(activity)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally(new Action() {
                    @Override
                    public void run() throws Exception {
                        cleanUpTemporaryUriFile();
                        mSaveBackUriDisposable = null;
                    }
                })
                .subscribe(new Consumer<Boolean>() {
                               @Override
                               public void accept(Boolean failed) throws Exception {
                                   if (failed) {
                                       File backupDir = Utils.getExternalDownloadDirectory(activity);
                                       if (mCurrentFile != null && mCurrentFile.exists() &&
                                               mCurrentFile.getParent() != null && mCurrentFile.getParent().equals(backupDir.getPath())) {
                                           CommonToast.showText(activity, activity.getString(R.string.document_notify_failed_commit_message, backupDir.getName()));
                                       } else {
                                           CommonToast.showText(activity, R.string.document_save_error_toast_message);
                                       }
                                   }
                               }
                           }, new Consumer<Throwable>() {
                               @Override
                               public void accept(Throwable throwable) throws Exception {
                                   CommonToast.showText(activity, R.string.document_save_error_toast_message);
                               }
                           }
                );
    }

    private Single<Boolean> saveBackEditUriImpl(@NonNull Activity activity) {
        return Single.create(new SingleOnSubscribe<Boolean>() {
            @Override
            public void subscribe(@NonNull SingleEmitter<Boolean> emitter) throws Exception {
                Uri uri = Uri.parse(mTabTag);

                // about to close
                // let's try to write back the file
                if (mHasChangesSinceOpened) {
                    // has changes
                    mNeedsCleanupFile = false;
                    validateContentResolver(uri);
                    InputStream is = null;
                    OutputStream fos = null;
                    FileInputStream fileInputStream = null;
                    FileOutputStream fileOutputStream = null;
                    RandomAccessFile raf = null;
                    ParcelFileDescriptor pfd = null;
                    boolean failed = true;
                    boolean readWrite = false;
                    ContentResolver contentResolver = Utils.getContentResolver(activity);
                    if (contentResolver != null) {
                        try {
                            pfd = contentResolver.openFileDescriptor(uri, "rw");
                            readWrite = true;
                        } catch (Exception e) {
                            try {
                                pfd = contentResolver.openFileDescriptor(uri, "w");
                            } catch (Exception ignored) {
                            }
                        }
                    }
                    if (pfd != null) {
                        failed = false;
                        try {
                            if (readWrite) {
                                try {
                                    fileInputStream = new FileInputStream(pfd.getFileDescriptor());

                                    if (sDebug)
                                        Log.d(TAG, "editUri | originalLength: " + mOriginalFileLength + " | stream: "
                                                + fileInputStream.available() + " | localLength: " + mCurrentFile.length());

                                    long originalFileLength = mOriginalFileLength;
                                    long finalSize = mCurrentFile.length();
                                    if (originalFileLength > finalSize) {
                                        throw new Exception("Original file size is bigger than saved file size. Something went wrong");
                                    }
                                    // seek to the end of the file
                                    long skipped = fileInputStream.skip(originalFileLength);
                                    if (skipped == originalFileLength) {
                                        // Append to the file
                                        fileOutputStream = new FileOutputStream(pfd.getFileDescriptor());
                                        // Read backup copy's appended part
                                        raf = new RandomAccessFile(mCurrentFile, "r");
                                        raf.seek(originalFileLength);
                                        long count = Utils.copyLarge(raf, fileOutputStream);
                                        fileOutputStream.getChannel().truncate(finalSize);
                                        mShouldCleanupFile = true;

                                        if (sDebug)
                                            Log.d(TAG, "seek to position: " + originalFileLength +
                                                    " | sizeToWrite is: " + (finalSize - originalFileLength +
                                                    " | actualWriteSize is: " + count) +
                                                    " | truncate to: " + finalSize);
                                    } else {
                                        throw new Exception("Could not seek to size. Something went wrong");
                                    }
                                } catch (Exception ex) {
                                    AnalyticsHandlerAdapter.getInstance().sendException(ex);

                                    // For some reason we are in readwrite mode, but cannot readwrite
                                    // so simply write the file
                                    is = new FileInputStream(mCurrentFile);
                                    fos = new FileOutputStream(pfd.getFileDescriptor());
                                    IOUtils.copy(is, fos);
                                    mShouldCleanupFile = true;
                                }
                            } else {
                                is = new FileInputStream(mCurrentFile);
                                fos = new FileOutputStream(pfd.getFileDescriptor());
                                IOUtils.copy(is, fos);
                                mShouldCleanupFile = true;
                            }
                            mNeedsCleanupFile = true;
                        } catch (OutOfMemoryError oom) {
                            failed = true;
                            Utils.manageOOM(getContext(), mPdfViewCtrl);
                        } catch (Exception ex) {
                            failed = true;
                            AnalyticsHandlerAdapter.getInstance().sendException(ex);
                        } finally {
                            mHasChangesSinceOpened = false;
                            Utils.closeQuietly(is);
                            Utils.closeQuietly(fos);
                            Utils.closeQuietly(fileInputStream);
                            Utils.closeQuietly(fileOutputStream);
                            Utils.closeQuietly(raf);
                            Utils.closeQuietly(pfd);
                        }
                    }
                    if (failed) {
                        // toast will not show when opened from another app due to invalid context
                        // here we save to shared pref regarding backup file, to notify at a later time
                        if (mCurrentFile != null) {
                            PdfViewCtrlSettingsManager.updateEditUriBackupFilePath(activity, mCurrentFile.getAbsolutePath());
                        }
                        removeFromRecentList();
                    }
                    emitter.onSuccess(failed);
                } else {
                    mNeedsCleanupFile = true;
                    emitter.onSuccess(false);
                }
            }
        });
    }

    private void cleanUpTemporaryUriFile() {
        // cleanup
        if (mDestroyCalled) {
            if (mTabSource == BaseFileInfo.FILE_TYPE_EDIT_URI && mNeedsCleanupFile) {
                // delete temp file we created, unless for the ones we don't have permission
                // because we need to reload those in the future
                // but we can still clean up if we were able to write back
                if (!mHasChangesSinceOpened) {
                    // when no changes were made, we can try to cleanup temp file if we have permission to it
                    if (mDocumentState == PdfDocManager.DOCUMENT_STATE_CLEAN) {
                        localFileWriteAccessCheck();
                        if (!mToolManager.isReadOnly()) {
                            mShouldCleanupFile = true;
                        }
                    }
                }
                if (mShouldCleanupFile) {
                    cleanupTemporaryFile();
                }
            }
            // cleanup
            if (mTabSource == BaseFileInfo.FILE_TYPE_OFFICE_URI && mNeedsCleanupFile) {
                // delete temp file we created
                cleanupTemporaryFile();
            }
        }
    }

    @SuppressWarnings("SameParameterValue")
    private void saveConversionTempHelper(boolean close, boolean forceSave, boolean hasChangesSinceLastSave) {
        if (!hasChangesSinceLastSave) {
            return;
        }
        if (mTabConversionTempPath != null) {
            File file = new File(mTabConversionTempPath);
            boolean shouldUnlock = false;
            try {
                shouldUnlock = docLock(close || forceSave);
                if (shouldUnlock) {
                    if (sDebug) {
                        Log.d(TAG, "save Conversion Temp");
                        Log.d(TAG, "doc locked");
                    }
                    if (mToolManager.getUndoRedoManger() != null) {
                        mToolManager.getUndoRedoManger().takeUndoSnapshotForSafety();
                    }
                    mPdfDoc.save(file.getAbsolutePath(), SDFDoc.SaveMode.INCREMENTAL, null);
                }
            } catch (Exception e) {
                handleFailedSave(close, e);
                AnalyticsHandlerAdapter.getInstance().sendException(e);
            } finally {
                if (shouldUnlock) {
                    docUnlock();
                }
            }
        }
    }

    private void saveConversionTempCopy() {
        if (getActivity() == null) {
            return;
        }

        boolean shouldUnlock = false;
        try {
            File tempFolder = mViewerConfig != null && !Utils.isNullOrEmpty(mViewerConfig.getConversionCachePath()) ?
                    new File(mViewerConfig.getConversionCachePath()) : getActivity().getFilesDir();
            mTabConversionTempPath = File.createTempFile("tmp", ".pdf", tempFolder).getAbsolutePath();
            // make a copy of the buffer
            mPdfDoc.lock();
            shouldUnlock = true;
            mPdfDoc.save(mTabConversionTempPath, SDFDoc.SaveMode.REMOVE_UNUSED, null);
        } catch (Exception e) {
            // skip toast if it is license error
            boolean showToast = true;
            if (e instanceof PDFNetException) {
                showToast = ((PDFNetException) e).getErrorCode() != PDFNetException.e_error_credentials;
            }
            if (showToast) {
                handleFailedSave(false, e);
            }
            AnalyticsHandlerAdapter.getInstance().sendException(e);
        } finally {
            if (shouldUnlock) {
                Utils.unlockQuietly(mPdfDoc);
            }
        }
    }

    /**
     * Handles if local file doesn't have write access.
     */
    public void localFileWriteAccessCheck() {
        if (mLocalReadOnlyChecked && mLocalReadOnlyCheckedResultSave) {
            // we've already determined the document is read-only
            mDocumentState = PdfDocManager.DOCUMENT_STATE_READ_ONLY;
            mToolManager.setReadOnly(true);
            return;
        }
        if (!mLocalReadOnlyChecked) {
            // check once is enough for us to know if the file is read only
            mLocalReadOnlyChecked = true;
            if (mTabSource == BaseFileInfo.FILE_TYPE_FILE) {
                if (!isTabReadOnly()) {
                    boolean shouldUnlockRead = false;
                    try {
                        mPdfDoc.lockRead();
                        shouldUnlockRead = true;
                        boolean canSave = mPdfDoc.getSDFDoc().canSaveToPath(mCurrentFile.getAbsolutePath(), SDFDoc.SaveMode.INCREMENTAL);
                        mPdfDoc.unlockRead();
                        shouldUnlockRead = false;
                        if (!canSave) {
                            // This file is most likely readonly (i.e. from external SD card for KitKat devices)
                            mLocalReadOnlyCheckedResultSave = true;
                            mDocumentState = PdfDocManager.DOCUMENT_STATE_READ_ONLY;
                            mToolManager.setReadOnly(true);
                        }
                    } catch (Exception e) {
                        AnalyticsHandlerAdapter.getInstance().sendException(e);
                    } finally {
                        if (shouldUnlockRead) {
                            Utils.unlockReadQuietly(mPdfDoc);
                        }
                    }
                }
            } else if (mTabSource == BaseFileInfo.FILE_TYPE_EDIT_URI) {
                if (mTabTag == null || !Utils.uriHasWritePermission(getContext(), Uri.parse(mTabTag))) {
                    mLocalReadOnlyCheckedResultSave = true;
                    mDocumentState = PdfDocManager.DOCUMENT_STATE_READ_ONLY;
                    mAutoSaveTimerEnabled = false; // disable auto save
                    mToolManager.setReadOnly(true);
                }
            }
        }
    }

    /**
     * Handles if the file is special case.
     *
     * @return True if the file is special case.
     */
    public boolean handleSpecialFile() {
        return handleSpecialFile(false);
    }

    protected boolean handleSpecialFile(boolean close) {
        Activity activity = getActivity();
        if (activity == null) {
            return false;
        }

        if (mTabListener != null) {
            mTabListener.onUpdateOptionsMenu();
        }

        if (mTabSource == BaseFileInfo.FILE_TYPE_OPEN_URL) {
            // show warning message if the file is still being downloaded
            if (mToolManager.isReadOnly()) {
                CommonToast.showText(activity, R.string.download_not_finished_yet_with_changes_warning, Toast.LENGTH_SHORT);
                return true;
            }
        }

        if (mToolManager.skipReadOnlyCheck()) {
            return false;
        }

        boolean ignore = true;
        int message = R.string.document_read_only_warning_message;
        int title = R.string.document_read_only_warning_title;

        switch (mDocumentState) {
            case PdfDocManager.DOCUMENT_STATE_READ_ONLY:
            case PdfDocManager.DOCUMENT_STATE_READ_ONLY_AND_MODIFIED:
                mDocumentState = PdfDocManager.DOCUMENT_STATE_READ_ONLY_AND_MODIFIED;
                ignore = false;
                CommonToast.showText(activity, R.string.document_read_only_error_message, Toast.LENGTH_SHORT);
                break;
            case PdfDocManager.DOCUMENT_STATE_CORRUPTED:
            case PdfDocManager.DOCUMENT_STATE_CORRUPTED_AND_MODIFIED:
                mDocumentState = PdfDocManager.DOCUMENT_STATE_CORRUPTED_AND_MODIFIED;
                CommonToast.showText(activity, R.string.document_corrupted_error_message, Toast.LENGTH_SHORT);
                return true;
            case PdfDocManager.DOCUMENT_STATE_COULD_NOT_SAVE:
                CommonToast.showText(activity, R.string.document_save_error_toast_message, Toast.LENGTH_SHORT);
                return true;
            case PdfDocManager.DOCUMENT_STATE_DURING_CONVERSION:
                CommonToast.showText(activity, R.string.cant_edit_while_converting_message);
                return true;
            case PdfDocManager.DOCUMENT_STATE_FROM_CONVERSION:
                ignore = false;
                break;
        }

        if (mUniversalConverted) {
            title = R.string.document_converted_warning_title;
        }

        if (!ignore && !close) {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle(title).setMessage(message)
                    .setCancelable(false);

            showSpecialFileAlertDialog(builder, mDocumentState, null);
            return true;
        }

        return false;
    }

    public void showReadOnlyAlert(DialogFragment dialogToDismiss) {
        Activity activity = getActivity();
        if (activity == null) {
            return;
        }

        int message = R.string.document_read_only_warning_message;
        int title = R.string.document_read_only_warning_title;

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(title).setMessage(message)
                .setCancelable(false);

        showSpecialFileAlertDialog(builder, mDocumentState, dialogToDismiss);
    }

    protected void showSpecialFileAlertDialog(AlertDialog.Builder builder, int state, final DialogFragment dialogToDismiss) {
        if (mSpecialFileAlertDialog != null && mSpecialFileAlertDialog.isShowing()) {
            return;
        }
        try {
            Activity activity = getActivity();
            if (activity == null) {
                return;
            }
            mShowingSpecialFileAlertDialog = true;
            if (state == PdfDocManager.DOCUMENT_STATE_READ_ONLY_AND_MODIFIED ||
                    state == PdfDocManager.DOCUMENT_STATE_FROM_CONVERSION) {
                builder.setPositiveButton(R.string.action_export_options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (dialogToDismiss != null) {
                            dialogToDismiss.dismiss();
                        }
                        Activity activity = getActivity();
                        if (activity == null) {
                            return;
                        }

                        mShowingSpecialFileAlertDialog = false;
                        // do different action for SD card file
                        boolean isSDCardFile = false;
                        if (Utils.isLollipop() && mCurrentFile != null) {
                            isSDCardFile = Utils.isSdCardFile(activity, mCurrentFile);
                        }
                        if (isSDCardFile) {
                            if (mTabListener != null) {
                                mTabListener.onTabJumpToSdCardFolder();
                            }
                            AnalyticsHandlerAdapter.getInstance().sendEvent(AnalyticsHandlerAdapter.CATEGORY_VIEWER, "Read Only SD Card File Jump To SD Card");
                        } else {
                            handleSpecialFilePositive();
                            AnalyticsHandlerAdapter.getInstance().sendEvent(AnalyticsHandlerAdapter.CATEGORY_VIEWER, "Read Only File Saved a Copy");
                        }
                        dialog.dismiss();
                    }
                }).setNegativeButton(R.string.document_read_only_warning_negative, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mShowingSpecialFileAlertDialog = false;
                        if (mDocumentState != PdfDocManager.DOCUMENT_STATE_FROM_CONVERSION) {
                            mDocumentState = PdfDocManager.DOCUMENT_STATE_READ_ONLY;
                        }
                        dialog.dismiss();
                    }
                });
                mSpecialFileAlertDialog = builder.create();
                mSpecialFileAlertDialog.show();
            }
        } catch (Exception ex) {
            mShowingSpecialFileAlertDialog = false;
            AnalyticsHandlerAdapter.getInstance().sendException(ex);
        }
    }

    public boolean isExportDirectoryContentUri() {
        if (mViewerConfig != null && !Utils.isNullOrEmpty(mViewerConfig.getSaveCopyExportPath())) {
            Uri fileUri = Uri.parse(mViewerConfig.getSaveCopyExportPath());
            return ContentResolver.SCHEME_CONTENT.equals(fileUri.getScheme());
        }
        return false;
    }

    public File getExportDirectory() {
        Context context = getContext();
        if (context == null) {
            throw new IllegalStateException("Should not call getExportDirectory when context is invalid");
        }
        File folder = Utils.getExternalDownloadDirectory(context);
        if (mViewerConfig != null && !Utils.isNullOrEmpty(mViewerConfig.getSaveCopyExportPath())) {
            File tempFolder = new File(mViewerConfig.getSaveCopyExportPath());
            if (tempFolder.exists() && tempFolder.isDirectory()) {
                folder = tempFolder;
            }
        }
        return folder;
    }

    public ExternalFileInfo getExportUriDirectory() {
        Context context = getContext();
        if (context == null) {
            throw new IllegalStateException("Should not call getExportUriDirectory when context is invalid");
        }
        ExternalFileInfo info = null;
        if (mViewerConfig != null && !Utils.isNullOrEmpty(mViewerConfig.getSaveCopyExportPath())) {
            Uri fileUri = Uri.parse(mViewerConfig.getSaveCopyExportPath());
            info = Utils.buildExternalFile(context, fileUri);
        }
        return info;
    }

    protected void handleSpecialFilePositive() {
        if (isExportDirectoryContentUri()) {
            saveSpecialFile(new SaveFolderWrapper(getExportUriDirectory(), doesSaveDocNeedNewTab()));
        } else {
            saveSpecialFile(new SaveFolderWrapper(getExportDirectory(), doesSaveDocNeedNewTab()));
        }
    }

    protected Single<Pair<Boolean, String>> saveSpecialFileDisposable(final SaveFolderWrapper folderWrapper) {
        return Single.create(new SingleOnSubscribe<Pair<Boolean, String>>() {
            @Override
            public void subscribe(SingleEmitter<Pair<Boolean, String>> emitter) throws Exception {
                boolean needsCopy = doesSaveDocNeedNewTab();

                PDFDoc copyDoc = mPdfDoc;
                if (needsCopy) {
                    copyDoc = folderWrapper.getDoc();
                    if (null == copyDoc) {
                        folderWrapper.cleanup();
                        emitter.tryOnError(new IllegalStateException("Could not get a copy of the doc. PDFDoc is null."));
                        return;
                    }
                }
                try {
                    emitter.onSuccess(folderWrapper.save(copyDoc, needsCopy));
                } catch (Exception e) {
                    AnalyticsHandlerAdapter.getInstance().sendException(e);
                    emitter.tryOnError(e);
                }
            }
        });
    }

    protected void saveSpecialFile(final SaveFolderWrapper folderWrapper) {
        final Activity activity = getActivity();
        if (activity == null) {
            return;
        }

        final ProgressDialog progressDialog = new ProgressDialog(activity);

        mDisposables.add(saveSpecialFileDisposable(folderWrapper)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        progressDialog.setMessage(getString(R.string.save_as_wait));
                        progressDialog.setCancelable(false);
                        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        progressDialog.setIndeterminate(true);
                        progressDialog.show();
                    }
                })
                .subscribe(new Consumer<Pair<Boolean, String>>() {
                               @Override
                               public void accept(Pair<Boolean, String> booleanStringPair) throws Exception {
                                   progressDialog.dismiss();

                                   boolean needsCopy = doesSaveDocNeedNewTab();
                                   if (needsCopy) {
                                       folderWrapper.openInNewTab();
                                   } else {
                                       CommonToast.showText(activity, R.string.document_saved_toast_message);

                                       mDocumentState = PdfDocManager.DOCUMENT_STATE_NORMAL;
                                       mToolManager.setReadOnly(false);
                                       String oldTag = mTabTag;
                                       mTabTag = folderWrapper.getNewTabTag();
                                       mTabTitle = folderWrapper.getNewTabTitle();
                                       mTabSource = folderWrapper.getNewTabType();
                                       mFileExtension = "pdf";
                                       if (folderWrapper.isLocal()) {
                                           mCurrentFile = folderWrapper.getNewLocalFile();
                                       } else {
                                           mCurrentUriFile = folderWrapper.getNewExternalUri();
                                       }
                                       mUniversalConverted = false;
                                       if (mTabListener != null) {
                                           mTabListener.onTabIdentityChanged(oldTag, mTabTag, mTabTitle,
                                                   mFileExtension, mTabSource);
                                       }

                                       PdfViewCtrlTabsManager.getInstance().removeDocument(activity, oldTag);
                                       PdfViewCtrlTabsManager.getInstance().addDocument(activity, mTabTag);
                                       saveCurrentPdfViewCtrlState();

                                       if (folderWrapper.isLocal()) {
                                           openLocalFile(folderWrapper.getNewTabTag());
                                       } else {
                                           if (folderWrapper.getNewTabType() == BaseFileInfo.FILE_TYPE_EDIT_URI) {
                                               openEditUriFile(folderWrapper.getNewTabTag());
                                           } else {
                                               openExternalFile(folderWrapper.getNewTabTag());
                                           }
                                       }
                                   }
                               }
                           }, new Consumer<Throwable>() {
                               @Override
                               public void accept(Throwable throwable) throws Exception {
                                   progressDialog.dismiss();

                                   CommonToast.showText(activity, getString(R.string.save_to_copy_failed));

                                   AnalyticsHandlerAdapter.getInstance().sendException(new Exception(throwable), "saveSpecialFile");
                               }
                           }
                ));
    }

    public void handleSaveFlattenedCopy() {
        if (isExportDirectoryContentUri()) {
            handleSavePrompt(null, getExportUriDirectory(), "Flattened", FLATTEN_FILE_FOLDER_REQUEST, null);
        } else {
            handleSavePrompt(getExportDirectory(), null, "Flattened", FLATTEN_FILE_FOLDER_REQUEST, null);
        }
    }

    public void handleSavePasswordCopy() {
        PasswordDialogFragment passwordDialog = getPasswordDialog();
        passwordDialog.setListener(new PasswordDialogFragment.PasswordDialogFragmentListener() {
            @Override
            public void onPasswordDialogPositiveClick(int fileType, File file, String path, String password, String id) {
                String suffix = "Protected";
                if (Utils.isNullOrEmpty(password)) {
                    suffix = "Not_Protected";
                }
                if (isExportDirectoryContentUri()) {
                    handleSavePrompt(null, getExportUriDirectory(), suffix, EXPORT_PASSWORD_COPY_FOLDER_REQUEST, password);
                } else {
                    handleSavePrompt(getExportDirectory(), null, suffix, EXPORT_PASSWORD_COPY_FOLDER_REQUEST, password);
                }
            }

            @Override
            public void onPasswordDialogNegativeClick(int fileType, File file, String path) {

            }

            @Override
            public void onPasswordDialogDismiss(boolean forcedDismiss) {

            }
        });
        FragmentManager fragmentManager = getFragmentManager();
        if (fragmentManager != null) {
            passwordDialog.show(fragmentManager, "password_dialog");
        }
    }

    public void handleSaveCroppedCopy() {
        if (isExportDirectoryContentUri()) {
            handleSavePrompt(null, getExportUriDirectory(), "Cropped", EXPORT_CROPPED_COPY_FOLDER_REQUEST, null);
        } else {
            handleSavePrompt(getExportDirectory(), null, "Cropped", EXPORT_CROPPED_COPY_FOLDER_REQUEST, null);
        }
    }

    public void handleSaveOptimizedCopy(OptimizeParams params) {
        if (isExportDirectoryContentUri()) {
            handleSavePrompt(null, getExportUriDirectory(), "Reduced", EXPORT_OPTIMIZE_COPY_FOLDER_REQUEST, params);
        } else {
            handleSavePrompt(getExportDirectory(), null, "Reduced", EXPORT_OPTIMIZE_COPY_FOLDER_REQUEST, params);
        }
    }

    public void handleSaveAsCopy() {
        if (isExportDirectoryContentUri()) {
            handleSavePrompt(null, getExportUriDirectory(), null, SAVEAS_FILE_FOLDER_REQUEST, null);
        } else {
            handleSavePrompt(getExportDirectory(), null, null, SAVEAS_FILE_FOLDER_REQUEST, null);
        }
    }

    protected void handleSavePrompt(final File folder, final ExternalFileInfo externalFolder,
            String suffix, final int copyType, final Object param) {
        final Activity activity = getActivity();
        if (activity == null) {
            return;
        }
        final boolean external = externalFolder != null;

        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (inflater == null) {
            return;
        }
        View renameDialog = inflater.inflate(R.layout.dialog_rename_file, null);
        String title = getString(R.string.action_export_options);

        String copyPath;
        File tempFile;
        String extension = mTabTitle.contains("." + mFileExtension) ? "" : "." + mFileExtension;
        suffix = (!StringUtils.isEmpty(suffix) ? "-" + suffix : "");
        if (!external) {
            tempFile = new File(folder, mTabTitle + suffix + extension);
            copyPath = new File(Utils.getFileNameNotInUse(tempFile.getAbsolutePath())).getName();
        } else {
            copyPath = Utils.getFileNameNotInUse(externalFolder, mTabTitle + suffix + extension);
        }

        final EditText renameEditText = renameDialog.findViewById(R.id.dialog_rename_file_edit);
        renameEditText.setText(copyPath);
        // Show the edit text with name of file selected.

        int index = FilenameUtils.indexOfExtension(copyPath);
        if (index == -1) {
            index = copyPath.length();
        }
        renameEditText.setSelection(0, index);
        renameEditText.setHint(getString(R.string.dialog_rename_file_hint));

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setView(renameDialog)
                .setTitle(title)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (!isAdded()) {
                            return;
                        }
                        boolean doAction = true;
                        String message = "";
                        String newFileName = "";
                        File newFile = null;
                        ExternalFileInfo newExtFile = null;

                        newFileName = renameEditText.getText().toString().trim();
                        if (!newFileName.toLowerCase().endsWith("." + mFileExtension)) {
                            newFileName = newFileName + "." + mFileExtension;
                        }

                        if (!external) {
                            newFile = new File(folder, newFileName);
                        } else {
                            newExtFile = externalFolder.getFile(newFileName);
                        }
                        // Check if the new file name already exists
                        if ((!external && newFile.exists()) || (external && newExtFile != null)) {
                            doAction = false;
                            message = getString(R.string.dialog_rename_invalid_file_name_already_exists_message);
                        }

                        if (doAction) {
                            SaveFolderWrapper newSaveFolderWrapper;
                            if (external) {
                                newSaveFolderWrapper = new SaveFolderWrapper(externalFolder, newFileName, false, null);
                            } else {
                                newSaveFolderWrapper = new SaveFolderWrapper(folder, newFileName, false, null);
                            }

                            switch (copyType) {
                                case SAVEAS_FILE_FOLDER_REQUEST:
                                    handleSaveAsCopy(newSaveFolderWrapper);
                                    break;
                                case FLATTEN_FILE_FOLDER_REQUEST:
                                    handleSaveFlattenedCopy(newSaveFolderWrapper);
                                    break;
                                case EXPORT_OPTIMIZE_COPY_FOLDER_REQUEST:
                                    handleSaveOptimizedCopy(newSaveFolderWrapper, param);
                                    break;
                                case EXPORT_CROPPED_COPY_FOLDER_REQUEST:
                                    handleSaveCroppedCopy(newSaveFolderWrapper);
                                    break;
                                case EXPORT_PASSWORD_COPY_FOLDER_REQUEST:
                                    handleSavePasswordCopy(newSaveFolderWrapper, param);
                                    break;
                            }
                        } else {
                            if (message.length() > 0) {
                                Utils.showAlertDialog(activity, message,
                                        getString(R.string.alert));
                            }
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        final AlertDialog dialog = builder.create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(renameEditText.length() > 0);
            }
        });

        renameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Enable "Ok" button
                // Disable "Ok" button
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(s.length() > 0);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // Show keyboard automatically when the dialog is shown.
        renameEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus && dialog.getWindow() != null) {
                    dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
            }
        });

        dialog.show();
    }

    protected void handleSaveAsCopy(final SaveFolderWrapper folderWrapper) {
        final Activity activity = getActivity();
        if (activity == null) {
            return;
        }

        final ProgressDialog progressDialog = new ProgressDialog(activity);

        mDisposables.add(saveAsCopyDisposable(folderWrapper)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        progressDialog.setMessage(getString(R.string.save_as_wait));
                        progressDialog.setCancelable(false);
                        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        progressDialog.setIndeterminate(true);
                        progressDialog.show();
                    }
                })
                .subscribe(new Consumer<Pair<Boolean, String>>() {
                               @Override
                               public void accept(Pair<Boolean, String> params) throws Exception {
                                   progressDialog.dismiss();

                                   if (mViewerConfig == null || mViewerConfig.isOpenSavedCopyInNewTab()) {
                                       if (params.first) {
                                           File copyFile = new File(params.second);
                                           openFileInNewTab(copyFile);
                                       } else {
                                           Uri uri = Uri.parse(params.second);
                                           openFileUriInNewTab(uri);
                                       }
                                   }
                                   AnalyticsHandlerAdapter.getInstance().sendEvent(AnalyticsHandlerAdapter.EVENT_VIEWER_SAVE_COPY,
                                           AnalyticsParam.viewerSaveCopyParam(AnalyticsHandlerAdapter.VIEWER_SAVE_COPY_IDENTICAL));
                               }
                           }, new Consumer<Throwable>() {
                               @Override
                               public void accept(Throwable throwable) throws Exception {
                                   progressDialog.dismiss();

                                   CommonToast.showText(activity, R.string.save_to_copy_failed);

                                   AnalyticsHandlerAdapter.getInstance().sendException(new Exception(throwable), "handleSaveAsCopy");
                               }
                           }
                ));
    }

    protected Single<Pair<Boolean, String>> saveAsCopyDisposable(final SaveFolderWrapper folderWrapper) {
        return Single.create(new SingleOnSubscribe<Pair<Boolean, String>>() {
            @Override
            public void subscribe(@NonNull SingleEmitter<Pair<Boolean, String>> emitter) throws Exception {
                final boolean isLocal = (folderWrapper.mLocalCopyFile != null);
                final boolean isExternal = (folderWrapper.mExternalCopyFile != null);
                try {
                    File localFile = null;
                    ExternalFileInfo externalFile = null;
                    PDFDoc copyDoc = null;
                    if (isLocal) {
                        localFile = folderWrapper.mLocalCopyFile;
                    } else if (isExternal) {
                        externalFile = folderWrapper.mExternalCopyFile;
                    } else { // case where SaveFolderWrapper created via SaveFolderWrapper(Uri targetUri)
                        copyDoc = folderWrapper.getDoc();
                    }

                    boolean success = false;
                    Pair<Boolean, String> result = null;

                    if (isLocal) {
                        success = copyFileSourceToTempFile(localFile);
                    } else if (externalFile != null) {
                        success = copyFileSourceToTempUri(externalFile.getUri());
                    } else if (copyDoc != null) {
                        result = folderWrapper.save(copyDoc);
                        success = result != null;
                    }

                    if (!success) {
                        // unable to get a valid PDFDoc
                        emitter.tryOnError(new IllegalStateException("Unable to get a valid PDFDoc. Error occurred copying source file to temp file."));
                        return;
                    }

                    String resultPath = null;
                    if (isLocal) {
                        resultPath = localFile.getAbsolutePath();
                    } else if (externalFile != null) {
                        resultPath = externalFile.getUri().toString();
                    } else if (copyDoc != null) {
                        resultPath = result.second;
                    }
                    if (resultPath == null) {
                        emitter.tryOnError(new IllegalStateException("Unable to obtain path of copied file."));
                    } else {
                        emitter.onSuccess(new Pair<>(isLocal, resultPath));
                    }
                } catch (Exception e) {
                    AnalyticsHandlerAdapter.getInstance().sendException(e);
                    emitter.tryOnError(e);
                }
            }
        });
    }

    protected void handleSaveFlattenedCopy(final SaveFolderWrapper folderWrapper) {
        final Activity activity = getActivity();
        if (activity == null) {
            return;
        }

        final ProgressDialog progressDialog = new ProgressDialog(activity);

        mDisposables.add(saveFlattenedCopyDisposable(folderWrapper)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        progressDialog.setMessage(getString(R.string.save_flatten_wait));
                        progressDialog.setCancelable(false);
                        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        progressDialog.setIndeterminate(true);
                        progressDialog.show();
                    }
                })
                .subscribe(new Consumer<Pair<Boolean, String>>() {
                               @Override
                               public void accept(Pair<Boolean, String> params) throws Exception {
                                   progressDialog.dismiss();

                                   if (mViewerConfig == null || mViewerConfig.isOpenSavedCopyInNewTab()) {
                                       if (params.first) {
                                           File copyFile = new File(params.second);
                                           openFileInNewTab(copyFile);
                                       } else {
                                           Uri uri = Uri.parse(params.second);
                                           openFileUriInNewTab(uri);
                                       }
                                   }
                                   AnalyticsHandlerAdapter.getInstance().sendEvent(AnalyticsHandlerAdapter.EVENT_VIEWER_SAVE_COPY,
                                           AnalyticsParam.viewerSaveCopyParam(AnalyticsHandlerAdapter.VIEWER_SAVE_COPY_FLATTENED));
                               }
                           }, new Consumer<Throwable>() {
                               @Override
                               public void accept(Throwable throwable) throws Exception {
                                   progressDialog.dismiss();

                                   CommonToast.showText(activity, R.string.save_to_copy_failed);

                                   AnalyticsHandlerAdapter.getInstance().sendException(new Exception(throwable), "handleSaveFlattenedCopy");
                               }
                           }
                ));
    }

    protected Single<Pair<Boolean, String>> saveFlattenedCopyDisposable(final SaveFolderWrapper folderWrapper) {
        return Single.create(new SingleOnSubscribe<Pair<Boolean, String>>() {
            @Override
            public void subscribe(@NonNull SingleEmitter<Pair<Boolean, String>> emitter) throws Exception {
                PDFDoc copyDoc = folderWrapper.getDoc();
                if (null == copyDoc) {
                    // unable to get a valid PDFDoc
                    folderWrapper.cleanup();
                    emitter.tryOnError(new IllegalStateException("Unable to get a valid PDFDoc. PDFDoc is null"));
                    return;
                }

                try {
                    // flatten
                    ViewerUtils.flattenDoc(copyDoc);
                    // save
                    emitter.onSuccess(folderWrapper.save(copyDoc));
                } catch (Exception e) {
                    AnalyticsHandlerAdapter.getInstance().sendException(e);
                    emitter.tryOnError(e);
                }
            }
        });
    }

    protected void handleSaveOptimizedCopy(final SaveFolderWrapper folderWrapper, final Object customObject) {
        final Activity activity = getActivity();
        if (activity == null) {
            return;
        }

        final ProgressDialog progressDialog = new ProgressDialog(activity);

        mDisposables.add(saveOptimizedCopyDisposable(folderWrapper, customObject)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        progressDialog.setMessage(getString(R.string.save_optimize_wait));
                        progressDialog.setCancelable(false);
                        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        progressDialog.setIndeterminate(true);
                        progressDialog.show();
                    }
                })
                .subscribe(new Consumer<Pair<Boolean, String>>() {
                               @Override
                               public void accept(Pair<Boolean, String> params) throws Exception {
                                   progressDialog.dismiss();

                                   String size = null;
                                   if (params.first) {
                                       File copyFile = new File(params.second);
                                       if (mViewerConfig == null || mViewerConfig.isOpenSavedCopyInNewTab()) {
                                           openFileInNewTab(copyFile);
                                       }
                                       size = Utils.humanReadableByteCount(copyFile.length(), false);
                                   } else {
                                       Uri uri = Uri.parse(params.second);
                                       if (mViewerConfig == null || mViewerConfig.isOpenSavedCopyInNewTab()) {
                                           openFileUriInNewTab(uri);
                                       }
                                       ExternalFileInfo fileInfo = Utils.buildExternalFile(activity, uri);
                                       if (fileInfo != null) {
                                           size = fileInfo.getSizeInfo();
                                       }
                                   }
                                   if (size != null) {
                                       CommonToast.showText(activity, getString(R.string.save_optimize_new_size_toast, size));
                                   }
                                   AnalyticsHandlerAdapter.getInstance().sendEvent(AnalyticsHandlerAdapter.EVENT_VIEWER_SAVE_COPY,
                                           AnalyticsParam.viewerSaveCopyParam(AnalyticsHandlerAdapter.VIEWER_SAVE_COPY_REDUCED));
                               }
                           }, new Consumer<Throwable>() {
                               @Override
                               public void accept(Throwable throwable) throws Exception {
                                   progressDialog.dismiss();

                                   CommonToast.showText(activity, R.string.save_to_copy_failed);

                                   AnalyticsHandlerAdapter.getInstance().sendException(new Exception(throwable), "handleSaveOptimizedCopy");
                               }
                           }
                ));
    }

    protected Single<Pair<Boolean, String>> saveOptimizedCopyDisposable(final SaveFolderWrapper folderWrapper, final Object customObject) {
        return Single.create(new SingleOnSubscribe<Pair<Boolean, String>>() {
            @Override
            public void subscribe(@NonNull SingleEmitter<Pair<Boolean, String>> emitter) throws Exception {
                PDFDoc copyDoc = folderWrapper.getDoc();
                if (null == copyDoc) {
                    // unable to get a valid PDFDoc
                    folderWrapper.cleanup();
                    emitter.tryOnError(new IllegalStateException("Unable to get a valid PDFDoc. PDFDoc is null"));
                    return;
                }

                try {
                    final OptimizeParams optimizeParams = (OptimizeParams) customObject;
                    // optimize
                    OptimizeDialogFragment.optimize(copyDoc, optimizeParams);
                    // save
                    emitter.onSuccess(folderWrapper.save(copyDoc, false));
                } catch (Exception e) {
                    AnalyticsHandlerAdapter.getInstance().sendException(e);
                    emitter.tryOnError(e);
                }
            }
        });
    }

    protected void handleSavePasswordCopy(final SaveFolderWrapper folderWrapper, final Object customObject) {
        final Activity activity = getActivity();
        if (activity == null) {
            return;
        }

        final ProgressDialog progressDialog = new ProgressDialog(activity);

        mDisposables.add(savePasswordCopyDisposable(folderWrapper, customObject)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        progressDialog.setMessage(getString(R.string.save_password_wait));
                        progressDialog.setCancelable(false);
                        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        progressDialog.setIndeterminate(true);
                        progressDialog.show();
                    }
                })
                .subscribe(new Consumer<Pair<Boolean, String>>() {
                               @Override
                               public void accept(Pair<Boolean, String> params) throws Exception {
                                   progressDialog.dismiss();

                                   if (mViewerConfig == null || mViewerConfig.isOpenSavedCopyInNewTab()) {
                                       final String password = (String) customObject;
                                       if (params.first) {
                                           File copyFile = new File(params.second);
                                           openFileInNewTab(copyFile, password); // use new password to open tab
                                       } else {
                                           Uri uri = Uri.parse(params.second);
                                           openFileUriInNewTab(uri, password); // use new password to open tab
                                       }
                                   }
                                   AnalyticsHandlerAdapter.getInstance().sendEvent(AnalyticsHandlerAdapter.EVENT_VIEWER_SAVE_COPY,
                                           AnalyticsParam.viewerSaveCopyParam(AnalyticsHandlerAdapter.VIEWER_SAVE_COPY_PASSWORD_PROTECTED));
                               }
                           }, new Consumer<Throwable>() {
                               @Override
                               public void accept(Throwable throwable) throws Exception {
                                   progressDialog.dismiss();

                                   CommonToast.showText(activity, R.string.save_to_copy_failed);

                                   AnalyticsHandlerAdapter.getInstance().sendException(new Exception(throwable), "handleSavePasswordCopy");
                               }
                           }
                ));
    }

    protected Single<Pair<Boolean, String>> savePasswordCopyDisposable(final SaveFolderWrapper folderWrapper, final Object customObject) {
        return Single.create(new SingleOnSubscribe<Pair<Boolean, String>>() {
            @Override
            public void subscribe(@NonNull SingleEmitter<Pair<Boolean, String>> emitter) throws Exception {
                PDFDoc copyDoc = folderWrapper.getDoc();
                if (null == copyDoc) {
                    // unable to get a valid PDFDoc
                    folderWrapper.cleanup();
                    emitter.tryOnError(new IllegalStateException("Unable to get a valid PDFDoc. PDFDoc is null"));
                    return;
                }

                try {
                    final String password = (String) customObject;
                    // encrypt
                    ViewerUtils.passwordDoc(copyDoc, password);
                    // save
                    emitter.onSuccess(folderWrapper.save(copyDoc));
                } catch (Exception e) {
                    AnalyticsHandlerAdapter.getInstance().sendException(e);
                    emitter.tryOnError(e);
                }
            }
        });
    }

    protected void handleSaveCroppedCopy(final SaveFolderWrapper folderWrapper) {
        final Activity activity = getActivity();
        if (activity == null) {
            return;
        }

        final ProgressDialog progressDialog = new ProgressDialog(activity);

        mDisposables.add(saveCroppedCopyDisposable(folderWrapper)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        progressDialog.setMessage(getString(R.string.save_crop_wait));
                        progressDialog.setCancelable(false);
                        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        progressDialog.setIndeterminate(true);
                        progressDialog.show();
                    }
                })
                .subscribe(new Consumer<Pair<Boolean, String>>() {
                               @Override
                               public void accept(Pair<Boolean, String> params) throws Exception {
                                   progressDialog.dismiss();
                                   if (mViewerConfig == null || mViewerConfig.isOpenSavedCopyInNewTab()) {
                                       if (params.first) {
                                           File copyFile = new File(params.second);
                                           openFileInNewTab(copyFile);
                                       } else {
                                           Uri uri = Uri.parse(params.second);
                                           openFileUriInNewTab(uri);
                                       }
                                   }
                                   AnalyticsHandlerAdapter.getInstance().sendEvent(AnalyticsHandlerAdapter.EVENT_VIEWER_SAVE_COPY,
                                           AnalyticsParam.viewerSaveCopyParam(AnalyticsHandlerAdapter.VIEWER_SAVE_COPY_CROPPED));
                               }
                           }, new Consumer<Throwable>() {
                               @Override
                               public void accept(Throwable throwable) throws Exception {
                                   progressDialog.dismiss();

                                   CommonToast.showText(activity, R.string.save_to_copy_failed);

                                   AnalyticsHandlerAdapter.getInstance().sendException(new Exception(throwable), "handleSaveCroppedCopy");
                               }
                           }
                ));
    }

    protected Single<Pair<Boolean, String>> saveCroppedCopyDisposable(final SaveFolderWrapper folderWrapper) {
        return Single.create(new SingleOnSubscribe<Pair<Boolean, String>>() {
            @Override
            public void subscribe(@NonNull SingleEmitter<Pair<Boolean, String>> emitter) throws Exception {
                PDFDoc copyDoc = folderWrapper.getDoc();
                if (null == copyDoc) {
                    // unable to get a valid PDFDoc
                    folderWrapper.cleanup();
                    emitter.tryOnError(new IllegalStateException("Unable to get a valid PDFDoc. PDFDoc is null."));
                    return;
                }

                try {
                    // crop
                    UserCropUtilities.cropDoc(copyDoc);
                    // save
                    emitter.onSuccess(folderWrapper.save(copyDoc));
                } catch (Exception e) {
                    AnalyticsHandlerAdapter.getInstance().sendException(e);
                    emitter.tryOnError(e);
                }
            }
        });
    }

    protected PasswordDialogFragment getPasswordDialog() {
        // Show password Dialog
        String hint = "";
        if (isPasswordProtected()) {
            hint = getString(R.string.password_input_hint);
        }
        PasswordDialogFragment passwordDialog = PasswordDialogFragment.newInstance(mTabSource, null,
                null, null, hint);
        return passwordDialog;
    }

    protected Single<Boolean> hasUserCropBoxDisposable() {
        return Single.fromCallable(new Callable<Boolean>() {

            @Override
            public Boolean call() throws Exception {
                return hasUserCropBox();
            }
        });
    }

    public boolean hasUserCropBox() {
        boolean hasCrop = false;
        boolean shouldUnlockRead = false;
        try {
            mPdfViewCtrl.cancelRenderingAsync();
            mPdfViewCtrl.docLockRead();
            shouldUnlockRead = true;
            PageIterator pageIterator = mPdfDoc.getPageIterator();
            while (pageIterator.hasNext()) {
                Page page = pageIterator.next();
                if (!page.getCropBox().equals(page.getBox(Page.e_user_crop))) {
                    hasCrop = true;
                    break;
                }
            }
        } catch (Exception e) {
            AnalyticsHandlerAdapter.getInstance().sendException(e);
        } finally {
            if (shouldUnlockRead) {
                mPdfViewCtrl.docUnlockRead();
            }
        }
        return hasCrop;
    }

    public void handleViewFileAttachments() {
        PortfolioDialogFragment portfolioDialog;

        portfolioDialog = PortfolioDialogFragment.newInstance(PortfolioDialogFragment.FILE_TYPE_PDFDOC, R.string.file_attachments);
        portfolioDialog.initParams(mPdfDoc);
        portfolioDialog.setListener(this);
        FragmentManager fragmentManager = getFragmentManager();
        if (fragmentManager != null) {
            portfolioDialog.show(fragmentManager, "portfolio_dialog");
        }
    }

    public void handleViewSelectedFileAttachment(final File localFolder, final ExternalFileInfo extFolder) {
        handleViewSelectedFileAttachment(localFolder, extFolder, null);
    }

    public void handleViewSelectedFileAttachment(@Nullable final File localFolder,
            @Nullable final ExternalFileInfo extFolder,
            @Nullable final Uri destFileUri) {
        if (Utils.isNullOrEmpty(mSelectedFileAttachmentName)) {
            Log.e(TAG, "ERROR: mFileAttachment is NULL OR EMPTY");
            return;
        }

        final Activity activity = getActivity();
        if (activity == null || mPdfViewCtrl == null) {
            return;
        }

        Integer fileType = null;
        String destFolderPath = null;
        if (localFolder != null) {
            fileType = PortfolioDialogFragment.FILE_TYPE_FILE;
            destFolderPath = localFolder.getAbsolutePath();
        } else if (extFolder != null || destFileUri != null) {
            fileType = PortfolioDialogFragment.FILE_TYPE_FILE_URI;
            if (extFolder != null) {
                destFolderPath = extFolder.getAbsolutePath();
            }
        }
        if ((destFolderPath != null || destFileUri != null) && fileType != null) {
            final ProgressDialog progressDialog = new ProgressDialog(activity);
            mDisposables.add(ViewerUtils.extractFileFromPortfolioDisposable(fileType, activity, mPdfDoc, destFolderPath, destFileUri, mSelectedFileAttachmentName)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe(new Consumer<Disposable>() {
                        @Override
                        public void accept(Disposable disposable) throws Exception {
                            progressDialog.setMessage(getString(R.string.tools_misc_please_wait));
                            progressDialog.setCancelable(false);
                            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                            progressDialog.setIndeterminate(true);
                            progressDialog.show();
                        }
                    })
                    .subscribe(new Consumer<String>() {
                                   @Override
                                   public void accept(String param) throws Exception {
                                       progressDialog.dismiss();

                                       if (!Utils.isNullOrEmpty(param)) {
                                           if (Utils.isExtensionHandled(Utils.getExtension(mSelectedFileAttachmentName))) {
                                               if (localFolder != null) {
                                                   File file = new File(param);
                                                   openFileInNewTab(file);
                                               } else if (extFolder != null) {
                                                   openFileUriInNewTab(Uri.parse(param));
                                               } else if (destFileUri != null) {
                                                   openFileUriInNewTab(destFileUri);
                                               }
                                           } else {
                                               if (localFolder != null) {
                                                   File file = new File(param);
                                                   Uri uri = Utils.getUriForFile(activity, file);
                                                   if (uri != null) {
                                                       Utils.shareGenericFile(activity, uri);
                                                   }
                                               } else if (extFolder != null) {
                                                   Uri fileUri = Uri.parse(param);
                                                   Utils.shareGenericFile(activity, fileUri);
                                               } else if (destFileUri != null) {
                                                   Utils.shareGenericFile(activity, destFileUri);
                                               }
                                           }
                                       }

                                       mSelectedFileAttachmentName = null;
                                   }
                               }, new Consumer<Throwable>() {
                                   @Override
                                   public void accept(Throwable throwable) throws Exception {
                                       progressDialog.dismiss();

                                       mSelectedFileAttachmentName = null;
                                   }
                               }
                    ));
        }
    }

    @Override
    public void onPortfolioDialogFragmentFileClicked(int fileType, PortfolioDialogFragment dialog, String fileName) {
        mSelectedFileAttachmentName = fileName;
        if (isExportDirectoryContentUri()) {
            handleViewSelectedFileAttachment(null, getExportUriDirectory());
        } else {
            handleViewSelectedFileAttachment(getExportDirectory(), null);
        }
    }

    /**
     * Handles sharing.
     */
    public void handleOnlineShare() {
        Activity activity = getActivity();
        if (activity == null) {
            return;
        }

        switch (mTabSource) {
            case BaseFileInfo.FILE_TYPE_FILE:
            case BaseFileInfo.FILE_TYPE_EDIT_URI:
                Utils.sharePdfFile(activity, mCurrentFile);
                break;
            case BaseFileInfo.FILE_TYPE_OPEN_URL:
                // Show a dialog informing the user can't share a doc opened from a URL.
                if (mCurrentFile != null && mCurrentFile.isFile()) {
                    if (mToolManager.isReadOnly()) {
                        CommonToast.showText(activity, R.string.download_not_finished_yet_warning, Toast.LENGTH_SHORT);
                        return;
                    }
                    Utils.sharePdfFile(activity, mCurrentFile);
                }
                break;
            case BaseFileInfo.FILE_TYPE_EXTERNAL:
                if (mCurrentUriFile != null) {
                    Utils.shareGenericFile(activity, mCurrentUriFile);
                }
                break;
            case BaseFileInfo.FILE_TYPE_OFFICE_URI:
                Utils.shareGenericFile(activity, Uri.parse(mTabTag));
                break;
        }
    }

    private void cancelUniversalConversion() {
        if (sDebug)
            Log.i("UNIVERSAL_TABCYCLE", FilenameUtils.getName(mTabTag) + " Cancels universal conversion");
        Utils.closeDocQuietly(mPdfViewCtrl);
        setViewerHostVisible(false);
        mDocumentLoaded = false;
    }

    private String getUrlEncodedTabFilename(String realUrl) {
        String title = getTabTitleWithUniversalExtension(realUrl);
        try {
            return URLEncoder.encode(title, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            AnalyticsHandlerAdapter.getInstance().sendException(e);
            Log.e(TAG, "We don't support utf-8 encoding for URLs?");
        }
        return title;
    }

    /**
     * Returns the item source of the tab.
     *
     * @return the item source of the tab
     */
    public int getTabSource() {
        return mTabSource;
    }

    /**
     * Returns the tab tag.
     *
     * @return the tab tag
     */
    public String getTabTag() {
        return mTabTag;
    }

    /**
     * Returns the filename of the tab
     *
     * @return filename without extension
     */
    public String getTabTitle() {
        return mTabTitle;
    }

    /**
     * Returns the filename of the tab
     *
     * @return filename with extension
     */
    public String getTabTitleWithExtension() {
        if (mTabTitle.toLowerCase().endsWith(".pdf")) {
            return mTabTitle;
        }
        return mTabTitle + ".pdf";
    }

    private String getTabTitleWithUniversalExtension(String realUrl) {
        String ext = Utils.getExtension(realUrl);
        if (Utils.isNullOrEmpty(ext))
            ext = ".pdf";
        else
            ext = "." + ext;
        if (mTabTitle.toLowerCase().endsWith(ext)) {
            return mTabTitle;
        }
        return mTabTitle + ext;
    }

    /**
     * Set enable or disable auto-saving annotations.
     *
     * @param enabled True to enable auto-saving is enabled (default value is True), false otherwise
     */
    public void setSavingEnabled(boolean enabled) {
        mSavingEnabled = enabled;
    }

    /**
     * set enable or disable saving state of document from outside
     *
     * @param enabled True to enable auto-saving is enabled (default value is True), false otherwise
     */
    public void setStateEnabled(boolean enabled) {
        mStateEnabled = enabled;
    }

    /**
     * Handles when thumbnails view dialog is dismissed.
     *
     * @param pageNum          The selected page number
     * @param docPagesModified True if the document pages are modified.
     */
    public void onThumbnailsViewDialogDismiss(int pageNum, boolean docPagesModified) {
        if (mPdfViewCtrl == null) {
            return;
        }

        mPdfViewCtrl.resume();
        setCurrentPageHelper(pageNum, false);

        refreshPageCount();

        if (docPagesModified) {
            onDocPagesModified();
        }

        if (mIsReflowMode) {
            // hide and pause PDFViewCtrl when in reflow mode
            setViewerHostVisible(false);
            mPdfViewCtrl.pause();
        }

        resetAutoSavingTimer();
    }

    /**
     * Handles when document pages are modified.
     */
    public void onDocPagesModified() {
        if (mPdfViewCtrl == null) {
            return;
        }

        clearPageBackAndForwardStacks();

        // reset the reflow adapter
        if (mIsReflowMode && mReflowControl != null) {
            try {
                mReflowControl.notifyPagesModified();
            } catch (Exception e) {
                AnalyticsHandlerAdapter.getInstance().sendException(e);
            }
        }
    }

    public void onAddNewPages(Page[] pages) {
        if (pages == null || pages.length == 0 || mPdfViewCtrl == null || mToolManager == null) {
            return;
        }
        PDFDoc doc = mPdfViewCtrl.getDoc();
        if (doc == null) {
            return;
        }

        int currentPage = 0;
        boolean shouldUnlock = false;
        try {
            mPdfViewCtrl.docLock(true);
            shouldUnlock = true;
            List<Integer> pageList = new ArrayList<>();
            for (int i = 1, cnt = pages.length; i <= cnt; i++) {
                int newPageNum = mPdfViewCtrl.getCurrentPage() + i;
                pageList.add(newPageNum);
                doc.pageInsert(doc.getPageIterator(newPageNum), pages[i - 1]);
            }
            mPageCount = doc.getPageCount();
            currentPage = mPdfViewCtrl.getCurrentPage() + 1;
            mPdfViewCtrl.setCurrentPage(currentPage);
            updatePageIndicator();

            mToolManager.raisePagesAdded(pageList);
        } catch (Exception e) {
            AnalyticsHandlerAdapter.getInstance().sendException(e);
        } finally {
            if (shouldUnlock) {
                mPdfViewCtrl.docUnlock();
            }
            ViewerUtils.safeUpdatePageLayout(mPdfViewCtrl, new ExceptionHandlerCallback() {
                @Override
                public void onException(Exception e) {
                    AnalyticsHandlerAdapter.getInstance().sendException(e);
                }
            });
            onThumbnailsViewDialogDismiss(currentPage, true);
        }

        onDocPagesModified();
    }

    @SuppressWarnings("unused")
    public void onAddNewPage(Page page) {
        if (page == null) {
            return;
        }
        Page[] pages = new Page[1];
        pages[0] = page;
        onAddNewPages(pages);
    }

    public void onDeleteCurrentPage() {
        if (mPdfViewCtrl == null || mToolManager == null) {
            return;
        }

        PDFDoc doc = mPdfViewCtrl.getDoc();
        boolean shouldUnlock = false;
        int currentPageNum = 0;
        try {
            mPdfViewCtrl.docLock(true);
            shouldUnlock = true;
            currentPageNum = mPdfViewCtrl.getCurrentPage();
            doc.pageRemove(doc.getPageIterator(currentPageNum));
            mPageCount = doc.getPageCount();
            updatePageIndicator();

            List<Integer> pageList = new ArrayList<>(1);
            pageList.add(currentPageNum);
            mToolManager.raisePagesDeleted(pageList);
        } catch (Exception e) {
            AnalyticsHandlerAdapter.getInstance().sendException(e);
            return;
        } finally {
            if (shouldUnlock) {
                mPdfViewCtrl.docUnlock();
            }
            onThumbnailsViewDialogDismiss(currentPageNum, true);
            ViewerUtils.safeUpdatePageLayout(mPdfViewCtrl, new ExceptionHandlerCallback() {
                @Override
                public void onException(Exception e) {
                    AnalyticsHandlerAdapter.getInstance().sendException(e);
                }
            });
        }

        onDocPagesModified();
    }

    protected void openLocalFile(String tag) {
        try {
            if (mTabSource == BaseFileInfo.FILE_TYPE_FILE && !isNotPdf()) {
                mPdfDoc = new PDFDoc(tag);
                checkPdfDoc();
            }
        } catch (Exception e) {
            AnalyticsHandlerAdapter.getInstance().sendException(e, "checkPdfDoc");
            if (mCurrentFile != null && !mCurrentFile.exists()) {
                // does not exist
                mErrorCode = PdfDocManager.DOCUMENT_SETDOC_ERROR_NOT_EXIST;
            } else if (getContext() != null && !Utils.hasStoragePermission(getContext())) {
                mErrorCode = PdfDocManager.DOCUMENT_ERROR_MISSING_PERMISSIONS;
            } else {
                // document is damaged
                mErrorCode = PdfDocManager.DOCUMENT_SETDOC_ERROR_CORRUPTED;
            }
            handleOpeningDocumentFailed(mErrorCode);
        }
    }

    protected void openExternalFile(String tag) {
        if (!Utils.isNullOrEmpty(tag) && getContext() != null) {
            mCurrentUriFile = Uri.parse(tag);
            mPdfDoc = null;
            if (mPDFDocLoaderTask != null && mPDFDocLoaderTask.getStatus() != AsyncTask.Status.FINISHED) {
                mPDFDocLoaderTask.cancel(true);
            }
            mPDFDocLoaderTask = new PDFDocLoaderTask(getContext());
            mPDFDocLoaderTask.setFinishCallback(new PDFDocLoaderTask.onFinishListener() {
                @Override
                public void onFinish(PDFDoc pdfDoc) {
                    mPdfDoc = pdfDoc;
                    if (mPdfDoc == null) {
                        handleOpeningDocumentFailed(PdfDocManager.DOCUMENT_SETDOC_ERROR_NULL_PDFDOC);
                        return;
                    }
                    try {
                        checkPdfDoc();
                    } catch (Exception e) {
                        mPdfDoc = null;
                        handleOpeningDocumentFailed(PdfDocManager.DOCUMENT_SETDOC_ERROR_NULL_PDFDOC);
                        AnalyticsHandlerAdapter.getInstance().sendException(e, "checkPdfDoc");
                    }
                }

                @Override
                public void onCancelled() {
                    // do nothing
                }
            })
                    .execute(mCurrentUriFile);
        }
    }

    protected void validateContentResolver(Uri uri) {
        final Activity activity = getActivity();
        if (activity == null || uri == null) {
            return;
        }
        Utils.validateContentResolver(activity.getContentResolver(), uri);
    }

    protected void openEditUriFile(String uriString) {
        final Activity activity = getActivity();
        if (activity == null) {
            return;
        }
        Uri uri = Uri.parse(uriString);

        localFileWriteAccessCheck();
        File cacheFolder = mCacheFolder;
        if (mToolManager.isReadOnly()) {
            // it's safe to save to cache as the file will be read-only and user will not lose any changes
            // so they don't actually need to recover this file later on
            cacheFolder = UriCacheManager.getCacheDir2(activity);
        }

        // Store this observable to we can clean up the file later
        mTempDownloadObservable = Utils.duplicateInFolder(Utils.getContentResolver(activity), uri, getTabTitle(), cacheFolder)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .cache();

        // Duplicate the file here
        mDisposables.add(
                mTempDownloadObservable
                        .doOnSubscribe(new Consumer<Disposable>() {
                            @Override
                            public void accept(Disposable disposable) {
                                if (disposable != null && !disposable.isDisposed()
                                        && mDownloadDocumentDialog != null) {
                                    showDownloadDialog();
                                }
                            }
                        })
                        .subscribeWith(new DisposableSingleObserver<File>() {
                            @Override
                            public void onSuccess(File file) {
                                if (mDownloadDocumentDialog != null) {
                                    mDownloadDocumentDialog.dismiss();
                                }
                                if (file != null) {
                                    mCurrentFile = file;
                                    mOriginalFileLength = mCurrentFile.length();
                                    if (mOriginalFileLength <= 0) {
                                        mCurrentFile = null;
                                    } else {
                                        if (sDebug)
                                            Log.d(TAG, "save edit uri file to: " + mCurrentFile.getAbsolutePath());
                                    }
                                }

                                if (mCurrentFile != null) {
                                    try {
                                        mPdfDoc = new PDFDoc(mCurrentFile.getAbsolutePath());
                                        checkPdfDoc();
                                    } catch (Exception e) {
                                        mPdfDoc = null;
                                        handleOpeningDocumentFailed(PdfDocManager.DOCUMENT_SETDOC_ERROR_NULL_PDFDOC);
                                        String path = mCurrentFile.getAbsolutePath();
                                        AnalyticsHandlerAdapter.getInstance().sendException(e, "checkPdfDoc " + path);
                                    }
                                } else {
                                    handleOpeningDocumentFailed(PdfDocManager.DOCUMENT_SETDOC_ERROR_NULL_PDFDOC);
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                if (mDownloadDocumentDialog != null) {
                                    mDownloadDocumentDialog.dismiss();
                                }

                                FileInfo recentFileInfo = RecentFilesManager.getInstance().getFile(activity, getCurrentFileInfo());
                                if (recentFileInfo != null && recentFileInfo.getFile() != null &&
                                        recentFileInfo.getFile().exists()) {
                                    // this was an URI that we lost permission, try loading the saved cache
                                    mCurrentFile = recentFileInfo.getFile();
                                    mOriginalFileLength = mCurrentFile.length();
                                    mTabTitle = FilenameUtils.getBaseName(mCurrentFile.getAbsolutePath());

                                    try {
                                        mPdfDoc = new PDFDoc(mCurrentFile.getAbsolutePath());
                                        checkPdfDoc();
                                    } catch (Exception exception) {
                                        mPdfDoc = null;
                                        handleOpeningDocumentFailed(PdfDocManager.DOCUMENT_SETDOC_ERROR_NULL_PDFDOC);
                                        String path = mCurrentFile.getAbsolutePath();
                                        AnalyticsHandlerAdapter.getInstance().sendException(exception, "checkPdfDoc " + path);
                                    }
                                    return;
                                }

                                if (e instanceof Exception) {
                                    if (e instanceof FileNotFoundException) {
                                        handleOpeningDocumentFailed(PdfDocManager.DOCUMENT_SETDOC_ERROR_NOT_EXIST);
                                    } else if (e instanceof SecurityException) {
                                        handleOpeningDocumentFailed(PdfDocManager.DOCUMENT_ERROR_MISSING_PERMISSIONS);
                                    } else {
                                        AnalyticsHandlerAdapter.getInstance().sendException((Exception) e, "title: " + getTabTitle());
                                    }
                                }
                            }
                        }));
    }

    private String getUrlWithoutParameters(String url) throws URISyntaxException {
        URI uri = new URI(url);
        return new URI(uri.getScheme(),
                uri.getAuthority(),
                uri.getPath(),
                null, // Ignore the query part of the input url
                uri.getFragment()).toString();
    }

    protected void openUrlFile(final String tag) {
        // if the link has extension, we can tell what kind of file it is and handle it accordingly
        // if the link does not have extension, let's first try to find its extension

        Activity activity = getActivity();
        if (activity == null || mPdfViewCtrl == null) {
            return;
        }

        // try to find extension
        String realUrl = tag;
        String extension = FilenameUtils.getExtension(tag);
        if (Utils.isNullOrEmpty(extension) || extension.contains("?") || tag.contains("?")) {
            try {
                realUrl = getUrlWithoutParameters(tag);
            } catch (Exception ignored) {
            }
        }

        // extension check
        String ext = Utils.getExtension(realUrl);
        if (Utils.isNullOrEmpty(ext)) {
            // no extension, let's try a head request
            final String finalRealUrl = realUrl;
            BasicHeadRequestTask.BasicHeadRequestTaskListener headListener = new BasicHeadRequestTask.BasicHeadRequestTaskListener() {
                @Override
                public void onHeadRequestTask(Boolean pass, String result) {
                    if (pass && result != null) {
                        boolean nonPDF = Utils.isNonPDFByMimeType(result);
                        String ext = MimeTypeMap.getSingleton().getExtensionFromMimeType(result);
                        openUrlFileImpl(tag, finalRealUrl, nonPDF, ext);
                    } else {
                        // failed head request
                        openUrlFileImpl(tag, finalRealUrl, false, null);
                    }
                }
            };
            new BasicHeadRequestTask(activity, headListener, tag, mCustomHeaders).execute();
            return;
        }

        openUrlFileImpl(tag, realUrl, false, null);
    }

    protected void openUrlFileImpl(String tag, String realUrl, boolean isNonPDF, @Nullable String ext) {
        Activity activity = getActivity();
        if (activity == null || mPdfViewCtrl == null) {
            return;
        }
        try {
            mCanAddToTabInfo = false;
            if (!mToolManager.isReadOnly()) {
                mToolManager.setReadOnly(true);
            }

            if (isWebViewConvertibleFormat()) {
                openConvertibleFormats(tag);
                return;
            }

            if (Utils.isNotPdf(realUrl) || isNonPDF || Utils.isNotPdfFromExt(mFileExtension)) {
                BasicHTTPDownloadTask.BasicHTTPDownloadTaskListener downListener = new BasicHTTPDownloadTask.BasicHTTPDownloadTaskListener() {
                    @Override
                    public void onDownloadTask(Boolean pass, File saveFile) {
                        if (mDownloadDocumentDialog != null && mDownloadDocumentDialog.isShowing()) {
                            mDownloadDocumentDialog.dismiss();
                        }
                        if (!pass) {
                            mErrorCode = PdfDocManager.DOCUMENT_SETDOC_ERROR_NULL_PDFDOC;
                            handleOpeningDocumentFailed(mErrorCode);
                        } else {
                            openOfficeDoc(saveFile.getAbsolutePath(), false);
                        }
                    }
                };

                String name = FilenameUtils.getName(realUrl);
                // if user already defined an extension, use it, otherwise use what we got from header
                String realExt = Utils.isNullOrEmpty(mFileExtension) ? ext : mFileExtension;
                if (realExt != null) {
                    name = name + "." + realExt;
                }
                File saveFile = new File(getOpenUrlCacheFolder(), name);
                saveFile = new File(Utils.getFileNameNotInUse(saveFile.getAbsolutePath()));
                mCurrentFile = saveFile;
                new BasicHTTPDownloadTask(activity, downListener, tag, mCustomHeaders, saveFile).execute();
                showDownloadDialog();
            } else {
                String filename = getUrlEncodedTabFilename(realUrl);
                if (!FilenameUtils.getExtension(filename).equals(mFileExtension)) {
                    String basename = FilenameUtils.getBaseName(filename);
                    filename = basename + "." + mFileExtension;
                }
                File cacheFile = new File(getOpenUrlCacheFolder(), filename);
                String cacheFilePath = cacheFile.getAbsolutePath();
                if (!Utils.isNullOrEmpty(cacheFilePath)) {
                    if (mViewerConfig == null) {
                        // if opened from DocumentActivity, re-write to the same cache
                        // otherwise create new cache
                        cacheFilePath = Utils.getFileNameNotInUse(cacheFilePath);
                    }
                    mCurrentFile = new File(cacheFilePath);
                }
                mOpenUrlLink = tag;
                PDFViewCtrl.HTTPRequestOptions httpRequestOptions = null;
                if (mViewerConfig != null && mViewerConfig.isRestrictDownloadUsage()) {
                    httpRequestOptions = new PDFViewCtrl.HTTPRequestOptions();
                    httpRequestOptions.restrictDownloadUsage(true);
                }
                if (mCustomHeaders != null) {
                    if (null == httpRequestOptions) {
                        httpRequestOptions = new PDFViewCtrl.HTTPRequestOptions();
                    }
                    Iterator<String> iter = mCustomHeaders.keys();
                    while (iter.hasNext()) {
                        String key = iter.next();
                        String val = mCustomHeaders.optString(key);
                        if (!Utils.isNullOrEmpty(val)) {
                            httpRequestOptions.addHeader(key, val);
                        }
                    }
                }
                mPdfViewCtrl.openUrlAsync(tag, cacheFilePath, mPassword, httpRequestOptions);
                mDownloading = true;
                showDownloadDialog();
            }
        } catch (Exception e) {
            if (mDownloadDocumentDialog != null && mDownloadDocumentDialog.isShowing()) {
                mDownloadDocumentDialog.dismiss();
            }
            mErrorCode = PdfDocManager.DOCUMENT_SETDOC_ERROR_NULL_PDFDOC;
            handleOpeningDocumentFailed(mErrorCode);
            AnalyticsHandlerAdapter.getInstance().sendException(e);
        }
    }

    protected File getOpenUrlCacheFolder() {
        Context context = getContext();
        if (context == null) {
            throw new IllegalStateException("Should not call getExportDirectory when context is invalid");
        }
        File cacheFolder = Utils.getExternalDownloadDirectory(context);
        if (mViewerConfig != null && !Utils.isNullOrEmpty(mViewerConfig.getOpenUrlCachePath())) {
            File tempCacheFolder = new File(mViewerConfig.getOpenUrlCachePath());
            if (tempCacheFolder.exists() && tempCacheFolder.isDirectory()) {
                cacheFolder = tempCacheFolder;
            }
        }
        return cacheFolder;
    }

    protected void showConvertibleConversionProgressDialog(@NonNull ProgressDialog progressDialog) {
        if (mViewerConfig == null || mViewerConfig.showConversionDialog()) {
            progressDialog.setMessage(getString(R.string.convert_to_pdf_wait));
            progressDialog.setCancelable(false);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setIndeterminate(true);
            progressDialog.show();
        }
    }

    protected void handleConvertibleConversionSuccess(@NonNull ProgressDialog progressDialog, @NonNull String filePath) {
        progressDialog.dismiss();
        mCurrentFile = new File(filePath);
        String oldTabTag = mTabTag;
        int oldTabSource = mTabSource;
        mTabTag = mCurrentFile.getAbsolutePath();
        mTabSource = BaseFileInfo.FILE_TYPE_FILE;
        mTabTitle = FilenameUtils.removeExtension(new File(mTabTag).getName());
        mFileExtension = "pdf";
        if (!mTabTag.equals(oldTabTag) || mTabSource != oldTabSource) {
            if (mTabListener != null) {
                mTabListener.onTabIdentityChanged(oldTabTag, mTabTag, mTabTitle, mFileExtension, mTabSource);
            }
        }
        mToolManager.setReadOnly(false);
        openLocalFile(filePath);
    }

    protected void openConvertibleFormats(String tag) {
        final Activity activity = getActivity();
        if (activity == null) {
            return;
        }
        final ProgressDialog progressDialog = new ProgressDialog(activity);
        if (isWebViewConvertibleFormat()) {
            showConvertibleConversionProgressDialog(progressDialog);
            String fileName = FilenameUtils.getBaseName(tag) + ".pdf";
            HTML2PDF.fromUrl(activity,
                    tag,
                    Uri.fromFile(getExportDirectory()),
                    fileName,
                    new HTML2PDF.HTML2PDFListener() {
                        @Override
                        public void onConversionFinished(String pdfOutput, boolean isLocal) {
                            handleConvertibleConversionSuccess(progressDialog, pdfOutput);
                        }

                        @Override
                        public void onConversionFailed(@Nullable String error) {
                            progressDialog.dismiss();
                            handleOpeningDocumentFailed(PdfDocManager.DOCUMENT_SETDOC_ERROR_NULL_PDFDOC);
                        }
                    });
            return;
        }
        mDisposables.add(convertToPdf(tag)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        showConvertibleConversionProgressDialog(progressDialog);
                    }
                })
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String filePath) throws Exception {
                        handleConvertibleConversionSuccess(progressDialog, filePath);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        progressDialog.dismiss();
                        handleOpeningDocumentFailed(PdfDocManager.DOCUMENT_SETDOC_ERROR_NULL_PDFDOC);
                    }
                })
        );
    }

    protected Single<String> convertToPdf(final String filePath) {
        return Single.create(new SingleOnSubscribe<String>() {
            @Override
            public void subscribe(SingleEmitter<String> emitter) throws Exception {
                String name = FilenameUtils.getName(filePath);
                name = FilenameUtils.removeExtension(name);
                File resultFile = new File(getExportDirectory(), name + ".pdf");
                String resultPath = Utils.getFileNameNotInUse(resultFile.getAbsolutePath());
                PDFDoc newDoc = new PDFDoc();
                Convert.toPdf(newDoc, filePath);
                newDoc.save(resultPath, SDFDoc.SaveMode.REMOVE_UNUSED, null);
                emitter.onSuccess(resultPath);
            }
        });
    }

    protected void openOfficeDoc(String tag, boolean isUri) {
        openOfficeDoc(tag, isUri, null);
    }

    protected void openOfficeDoc(final String tag, final boolean isUri, final String pageOptionsJson) {
        final Activity activity = getActivity();
        if (activity == null || mPdfViewCtrl == null || Utils.isNullOrEmpty(tag)) {
            return;
        }

        if (mViewerConfig != null && mViewerConfig.isUseStandardLibrary()) {
            // no office support for Standard version
            return;
        }

        if (isConvertibleFormat()) {
            openConvertibleFormats(tag);
            return;
        }

        mPdfDoc = null;
        mToolManager.setReadOnly(true);

        if (isUri) {
            Uri uri = Uri.parse(tag);
            mIsOfficeDoc = Utils.isOfficeDocument(activity.getContentResolver(), uri);
        } else {
            mIsOfficeDoc = Utils.isOfficeDocument(tag);
        }

        // if URI not seekable, duplicate the file
        Uri uri = Uri.parse(tag);
        if (isUri && !Utils.isUriSeekable(activity, uri)) {
            mTempDownloadObservable =
                    Utils.duplicateInFolder(Utils.getContentResolver(activity), uri, getTabTitle(), mCacheFolder)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .cache();
            mDisposables.add(
                    mTempDownloadObservable
                            .doOnSubscribe(new Consumer<Disposable>() {
                                @Override
                                public void accept(Disposable disposable) {
                                    if (disposable != null && !disposable.isDisposed()
                                            && mDownloadDocumentDialog != null) {
                                        showDownloadDialog();
                                    }
                                }
                            })
                            .subscribeWith(new DisposableSingleObserver<File>() {
                                @Override
                                public void onSuccess(File file) {
                                    if (mDownloadDocumentDialog != null && isVisible()) {
                                        mDownloadDocumentDialog.dismiss();
                                    }
                                    if (file != null && file.exists()) {
                                        mNeedsCleanupFile = true;
                                        tryToOpenOfficeDoc(false, file.getAbsolutePath(), pageOptionsJson);
                                    } else {
                                        tryToOpenOfficeDoc(true, tag, pageOptionsJson);
                                    }
                                }

                                @Override
                                public void onError(Throwable e) {
                                    if (mDownloadDocumentDialog != null) {
                                        mDownloadDocumentDialog.dismiss();
                                    }

                                    if (e instanceof Exception) {
                                        if (e instanceof FileNotFoundException) {
                                            handleOpeningDocumentFailed(PdfDocManager.DOCUMENT_SETDOC_ERROR_NOT_EXIST);
                                        } else if (e instanceof SecurityException) {
                                            handleOpeningDocumentFailed(PdfDocManager.DOCUMENT_ERROR_MISSING_PERMISSIONS);
                                        } else {
                                            AnalyticsHandlerAdapter.getInstance().sendException((Exception) e, "title: " + getTabTitle());
                                        }
                                    }
                                }
                            }));
        } else {
            tryToOpenOfficeDoc(isUri, tag, pageOptionsJson);
        }
    }

    protected void tryToOpenOfficeDoc(boolean isUri, String tag, String pageOptionsJson) {
        try {
            OfficeToPDFOptions options = null;
            if (!Utils.isNullOrEmpty(pageOptionsJson)) {
                if (sDebug) {
                    Log.d(TAG, "PageSizes: " + pageOptionsJson);
                }
                options = new OfficeToPDFOptions(pageOptionsJson);
            }
            if (null == options) {
                if (mViewerConfig != null && mViewerConfig.getConversionOptions() != null) {
                    // check if user passed in options
                    options = new OfficeToPDFOptions(mViewerConfig.getConversionOptions());
                } else {
                    // chop off white space
                    if (sDebug) {
                        Log.d(TAG, "RemovePadding: true");
                    }
                    options = new OfficeToPDFOptions("{\"RemovePadding\": true}");
                }
            }
            if (!Utils.isNullOrEmpty(mFileExtension)) {
                options.setFileExtension(mFileExtension);
            }
            if (!isUri) {
                mCurrentFile = new File(tag);
                if (!mCurrentFile.exists()) {
                    handleOpeningDocumentFailed(PdfDocManager.DOCUMENT_SETDOC_ERROR_NOT_EXIST);
                    return;
                }
                if (Utils.isNullOrEmpty(mTabConversionTempPath)) {
                    mDocumentConversion = mPdfViewCtrl.openNonPDFUri(Uri.fromFile(mCurrentFile), options);
                }
            } else {
                Uri uri = Uri.parse(tag);
                mCurrentUriFile = uri;
                if (Utils.isNullOrEmpty(mTabConversionTempPath)) {
                    createSecondaryFileFilterAsync(uri, options);
                }
            }

            mUniversalConverted = true;
            mDocumentLoaded = false;

            if (Utils.isNullOrEmpty(mTabConversionTempPath)) {
                mDocumentState = PdfDocManager.DOCUMENT_STATE_DURING_CONVERSION;
            } else {
                // if have temp file, load temp file
                mPdfDoc = new PDFDoc(mTabConversionTempPath);
                checkPdfDoc();
                mDocumentState = PdfDocManager.DOCUMENT_STATE_FROM_CONVERSION;
            }

            mShouldNotifyWhenConversionFinishes = false;
            mConversionFinishedMessageHandler.postDelayed(mConversionFinishedMessageRunnable, MAX_CONVERSION_TIME_WITHOUT_NOTIFICATION);
            mIsEncrypted = false;

            mToolManager.setTool(mToolManager.createTool(ToolMode.PAN, null));

            mProgressBarLayout.show();
        } catch (Exception e) {
            AnalyticsHandlerAdapter.getInstance().sendException(e);
            handleOpeningDocumentFailed(PdfDocManager.DOCUMENT_SETDOC_ERROR_NULL_PDFDOC);
        }
    }

    private void createSecondaryFileFilterAsync(@NonNull Uri fileUri, @Nullable ConversionOptions options) {
        mDisposables.add(
                createSecondaryFileFilterSingle(fileUri)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<SecondaryFileFilter>() {
                            @Override
                            public void accept(SecondaryFileFilter fileFilter) throws Exception {
                                if (mPdfViewCtrl != null && mPdfViewCtrl.isValid()) {
                                    try {
                                        mDocumentConversion = mPdfViewCtrl.openNonPDFFilter(fileFilter, options);
                                    } catch (Exception ex) {
                                        AnalyticsHandlerAdapter.getInstance().sendException(ex);
                                        handleOpeningDocumentFailed(PdfDocManager.DOCUMENT_SETDOC_ERROR_NULL_PDFDOC);
                                    }
                                }
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable e) throws Exception {
                                AnalyticsHandlerAdapter.getInstance().sendException(new Exception(e));
                                handleOpeningDocumentFailed(PdfDocManager.DOCUMENT_SETDOC_ERROR_NULL_PDFDOC);
                            }
                        })
        );
    }

    private Single<SecondaryFileFilter> createSecondaryFileFilterSingle(@NonNull Uri fileUri) {
        return Single.create(new SingleOnSubscribe<SecondaryFileFilter>() {
            @Override
            public void subscribe(@NonNull SingleEmitter<SecondaryFileFilter> emitter) {
                if (mPdfViewCtrl != null && mPdfViewCtrl.isValid()) {
                    try {
                        SecondaryFileFilter fileFilter = new SecondaryFileFilter(mPdfViewCtrl.getContext(), fileUri);
                        emitter.onSuccess(fileFilter);
                    } catch (Exception e) {
                        emitter.onError(e);
                    }
                }
            }
        });
    }

    protected void checkPdfDoc() throws PDFNetException {
        Activity activity = getActivity();
        if (activity == null || mPdfViewCtrl == null || mPdfDoc == null) {
            return;
        }
        mDocumentLoading = false;

        mDocumentLoaded = false;
        mDocumentState = PdfDocManager.DOCUMENT_STATE_CLEAN;

        if (mTabListener != null) {
            mTabListener.onUpdateOptionsMenu();
        }

        boolean shouldUnlockRead = false;
        boolean hasRepairedXRef;
        boolean initStdSecurityHandler;
        int pageCount = 0;
        try {
            mPdfDoc.lockRead();
            shouldUnlockRead = true;
            hasRepairedXRef = mPdfDoc.hasRepairedXRef();
            initStdSecurityHandler = mPdfDoc.initStdSecurityHandler(mPassword);
            if (initStdSecurityHandler) {
                // cannot get page count when the given security is not valid
                pageCount = mPdfDoc.getPageCount();
            }
        } finally {
            if (shouldUnlockRead) {
                Utils.unlockReadQuietly(mPdfDoc);
            }
        }

        if (!initStdSecurityHandler) {
            handleEncryptedPdf(activity);
            return;
        }
        if (mPasswordLayout != null) {
            mPasswordLayout.setVisibility(View.GONE);
            sliderSetVisibility(View.VISIBLE);
        }

        if (hasRepairedXRef) {
            mToolManager.setReadOnly(true);
            mDocumentState = PdfDocManager.DOCUMENT_STATE_CORRUPTED;
        }

        if (pageCount < 1) {
            handleOpeningDocumentFailed(PdfDocManager.DOCUMENT_SETDOC_ERROR_ZERO_PAGE);
        } else {
            mPdfViewCtrl.setDoc(mPdfDoc);
            if (mCurrentFile != null) {
                if (!mCurrentFile.canWrite()) {
                    mToolManager.setReadOnly(true);
                    if (mDocumentState != PdfDocManager.DOCUMENT_STATE_CORRUPTED) {
                        mDocumentState = PdfDocManager.DOCUMENT_STATE_READ_ONLY;
                    }
                }
            }
            long size = getCurrentFileSize();
            boolean canSave = Utils.hasEnoughStorageToSave(size);
            if (!canSave) {
                mToolManager.setReadOnly(true);
                mDocumentState = PdfDocManager.DOCUMENT_STATE_OUT_OF_SPACE;
            }
            mPageCount = pageCount;

            // We only want to generate thumbs for non-secured documents.
            if (mPassword != null && mPassword.isEmpty()) {
                if (!Utils.isNullOrEmpty(mTabTag) && mPdfDoc != null) {
                    if (mViewerConfig == null || !mViewerConfig.isUseStandardLibrary()) {
                        initRecentlyUsedCache();
                        RecentlyUsedCache.accessDocument(mTabTag, mPdfDoc);
                    }
                }
            }
            mIsEncrypted = (mPassword != null && !mPassword.isEmpty());

            if (mToolManager != null && mToolManager.getTool() == null) {
                mToolManager.setTool(mToolManager.createTool(ToolMode.PAN, null));
            }

            FragmentManager fragmentManager = getFragmentManager();
            if (fragmentManager != null) {
                Fragment thumbFragment = fragmentManager.findFragmentByTag("thumbnails_fragment");
                if (thumbFragment != null && thumbFragment.getView() != null) {
                    if (thumbFragment instanceof ThumbnailsViewFragment) {
                        ((ThumbnailsViewFragment) thumbFragment).addDocPages();
                    }
                }
            }
        }

        // Start timer for auto-saving
        resetAutoSavingTimer();

        if (mTabListener != null) {
            mTabListener.onUpdateOptionsMenu();
        }
    }

    // CTRL based keys

    /**
     * Handles CTRL-based key when triggered.
     *
     * @param keyCode The key code
     * @param event   The key event
     * @return True if the key is handled
     */
    public boolean handleKeyShortcut(int keyCode, KeyEvent event) {
        // https://codelabs.developers.google.com/codelabs/optimized-for-chromeos/#5
        // CTRL based and non-CTRL based keys go through different routes

        Activity activity = getActivity();
        if (activity == null || activity.isFinishing() || !isDocumentReady() || mPdfViewCtrl == null) {
            return false;
        }

        if (ShortcutHelper.isUndo(keyCode, event)) {
            undo();
            return true;
        }

        if (ShortcutHelper.isRedo(keyCode, event)) {
            redo();
            return true;
        }

        if (ShortcutHelper.isPrint(keyCode, event)) {
            handlePrintAnnotationSummary();
            return true;
        }

        if (ShortcutHelper.isAddBookmark(keyCode, event)) {
            addPageToBookmark();
            return true;
        }

        if (!mPageBackStack.isEmpty() && ShortcutHelper.isJumpPageBack(keyCode, event)) {
            jumpPageBack();
            return true;
        }

        if (!mPageForwardStack.isEmpty() && ShortcutHelper.isJumpPageForward(keyCode, event)) {
            jumpPageForward();
            return true;
        }

        if (ShortcutHelper.isRotateClockwise(keyCode, event)) {
            mPdfViewCtrl.rotateClockwise();
            ViewerUtils.safeUpdatePageLayout(mPdfViewCtrl, new ExceptionHandlerCallback() {
                @Override
                public void onException(Exception e) {
                    AnalyticsHandlerAdapter.getInstance().sendException(e);
                }
            });
            return true;
        }

        if (ShortcutHelper.isRotateCounterClockwise(keyCode, event)) {
            mPdfViewCtrl.rotateCounterClockwise();
            ViewerUtils.safeUpdatePageLayout(mPdfViewCtrl, new ExceptionHandlerCallback() {
                @Override
                public void onException(Exception e) {
                    AnalyticsHandlerAdapter.getInstance().sendException(e);
                }
            });
            return true;
        }

        boolean isZoomIn = ShortcutHelper.isZoomIn(keyCode, event);
        boolean isZoomOut = ShortcutHelper.isZoomOut(keyCode, event);
        boolean isResetZoom = ShortcutHelper.isResetZoom(keyCode, event);
        if (isZoomIn || isZoomOut || isResetZoom) {
            final ToolManager.Tool tool = mToolManager.getTool();
            if (tool instanceof TextSelect) {
                ((TextSelect) tool).closeQuickMenu();
                ((TextSelect) tool).clearSelection();
            }

            if (isZoomIn) {
                mPdfViewCtrl.setZoom(0, 0, mPdfViewCtrl.getZoom() * PDFViewCtrl.SCROLL_ZOOM_FACTOR, true, true);
            } else if (isZoomOut) {
                mPdfViewCtrl.setZoom(0, 0, mPdfViewCtrl.getZoom() / PDFViewCtrl.SCROLL_ZOOM_FACTOR, true, true);
            } else { // if (isResetZoom) {
                PointF point = mPdfViewCtrl.getCurrentMousePosition();
                resetZoom(point);
            }

            if (tool instanceof TextSelect) {
                mResetTextSelectionHandler.removeCallbacksAndMessages(null);
                mResetTextSelectionHandler.postDelayed(mResetTextSelectionRunnable, 500);
            } else if (tool instanceof AnnotEdit) {
                mToolManager.setTool(mToolManager.createTool(((AnnotEdit) tool).getCurrentDefaultToolMode(), tool));
            }

            return true;
        }

        return false;
    }

    // Non-CTRL based keys

    /**
     * Handles key when pressed up.
     *
     * @param keyCode The key code
     * @param event   The key event
     * @return True if the key is handled
     */
    public boolean handleKeyUp(int keyCode, KeyEvent event) {
        Activity activity = getActivity();
        if (activity == null || activity.isFinishing() || !isDocumentReady() || mPdfViewCtrl == null) {
            return false;
        }

        if (ShortcutHelper.isGotoFirstPage(keyCode, event)) {
            setCurrentPageHelper(1, true);
            return true;
        }

        if (ShortcutHelper.isGotoLastPage(keyCode, event)) {
            setCurrentPageHelper(mPdfViewCtrl.getPageCount(), true);
            return true;
        }

        DisplayMetrics metrics = new DisplayMetrics();
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        display.getMetrics(metrics);
        int screenWidth = metrics.widthPixels;
        int screenHeight = metrics.heightPixels;
        int widthStep = screenWidth / 8;
        int heightStep = screenHeight / 8;

        if (ShortcutHelper.isPageUp(keyCode, event)) {
            int dy = mPdfViewCtrl.getHeight() - heightStep;
            int y = mPdfViewCtrl.getScrollY();
            mPdfViewCtrl.scrollBy(0, -dy);
            int newY = mPdfViewCtrl.getScrollY();
            if (y == newY) {
                mPdfViewCtrl.gotoPreviousPage();
            }
        }

        if (ShortcutHelper.isPageDown(keyCode, event)) {
            int dy = mPdfViewCtrl.getHeight() - heightStep;
            int y = mPdfViewCtrl.getScrollY();
            mPdfViewCtrl.scrollBy(0, dy);
            int newY = mPdfViewCtrl.getScrollY();
            if (y == newY) {
                mPdfViewCtrl.gotoNextPage();
            }
            return true;
        }

        if (ViewerUtils.isViewerZoomed(mPdfViewCtrl)) {
            // zoomed: scroll accordingly

            if (ShortcutHelper.isScrollToLeft(keyCode, event)) {
                if (!mPdfViewCtrl.turnPageInNonContinuousMode(mPdfViewCtrl.getCurrentPage(), false)) {
                    mPdfViewCtrl.scrollBy(-widthStep, 0);
                }
                return true;
            }
            if (ShortcutHelper.isScrollToUp(keyCode, event)) {
                mPdfViewCtrl.scrollBy(0, -heightStep);
                return true;
            }
            if (ShortcutHelper.isScrollToRight(keyCode, event)) {
                if (!mPdfViewCtrl.turnPageInNonContinuousMode(mPdfViewCtrl.getCurrentPage(), true)) {
                    mPdfViewCtrl.scrollBy(widthStep, 0);
                }
                return true;
            }
            if (ShortcutHelper.isScrollToDown(keyCode, event)) {
                mPdfViewCtrl.scrollBy(0, heightStep);
                return true;
            }
        } else {
            // not zoomed: turn page accordingly

            if (ShortcutHelper.isScrollToLeft(keyCode, event)) {
                mPdfViewCtrl.gotoPreviousPage();
                return true;
            }
            if (ShortcutHelper.isScrollToUp(keyCode, event)) {
                if (isContinuousPageMode()) {
                    mPdfViewCtrl.scrollBy(0, -heightStep);
                } else {
                    mPdfViewCtrl.gotoPreviousPage();
                }
                return true;
            }
            if (ShortcutHelper.isScrollToRight(keyCode, event)) {
                mPdfViewCtrl.gotoNextPage();
                return true;
            }
            if (ShortcutHelper.isScrollToDown(keyCode, event)) {
                if (isContinuousPageMode()) {
                    mPdfViewCtrl.scrollBy(0, heightStep);
                } else {
                    mPdfViewCtrl.gotoNextPage();
                }
                return true;
            }
        }

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (getToolManager() != null && getToolManager().getTool() != null && ((Tool) getToolManager().getTool()).isEditingAnnot()) {
                mPdfViewCtrl.closeTool();
                return true;
            }
            if (mTabListener != null) {
                return mTabListener.onBackPressed();
            }
        }

        // if in annotation mode, swallow all other shortcuts
        return isAnnotationMode();
    }

    protected void handleOpeningDocumentFailed(int errorCode) {
        handleOpeningDocumentFailed(errorCode, "");
    }

    protected void handleOpeningDocumentFailed(int errorCode, String info) {
        stopAutoSavingTimer();
        mDocumentLoading = false;
        mCanAddToTabInfo = false;
        mErrorOnOpeningDocument = true;
        mErrorCode = errorCode;
        if (mTabListener != null) {
            mTabListener.onTabError(mErrorCode, info);
        }
    }

    public void handlePrintAnnotationSummary() {
        if (checkTabConversionAndAlert(R.string.cant_print_while_converting_message, true, false)) {
            return;
        }

        PrintAnnotationsSummaryDialogFragment dialog = PrintAnnotationsSummaryDialogFragment.newInstance(
                mPrintDocumentChecked, mPrintAnnotationsChecked, mPrintSummaryChecked);
        dialog.setPrintAnnotationsSummaryListener(new PrintAnnotationsSummaryDialogFragment.PrintAnnotationsSummaryListener() {
            @Override
            public void onConfirmPrintAnnotationSummary(boolean documentChecked, boolean annotationsChecked, boolean summaryChecked) {
                updatePrintDocumentMode(documentChecked);
                updatePrintAnnotationsMode(annotationsChecked);
                updatePrintSummaryMode(summaryChecked);

                sentToPrinterDialog();
            }
        });
        FragmentManager fragmentManager = getFragmentManager();
        if (fragmentManager != null) {
            dialog.show(fragmentManager, "print_annotations_summary_dialog");
        }
        AnalyticsHandlerAdapter.getInstance().sendEvent(AnalyticsHandlerAdapter.EVENT_VIEWER_PRINT);
    }

    protected void addPageToBookmark() {
        Activity activity = getActivity();
        if (activity == null || isTabReadOnly()) {
            return;
        }

        int currentPage = mPdfViewCtrl.getCurrentPage();
        ViewerUtils.addPageToBookmark(activity, isTabReadOnly(), mPdfViewCtrl, currentPage);
    }

    protected void togglePageBookmark() {
        Activity activity = getActivity();
        if (activity == null) {
            return;
        }
        if (mBookmarksViewModel != null) {
            mBookmarksViewModel.toggleBookmark(activity, isTabReadOnly(), mPdfViewCtrl, mPdfViewCtrl.getCurrentPage());
        }
    }

    protected void resetZoom(PointF point) {
        PDFViewCtrl.PageViewMode refMode;
        if (mPdfViewCtrl.isMaintainZoomEnabled()) {
            refMode = mPdfViewCtrl.getPreferredViewMode();
        } else {
            refMode = mPdfViewCtrl.getPageRefViewMode();
        }
        mPdfViewCtrl.setPageViewMode(refMode, (int) point.x, (int) point.y, true);
    }

    protected void sentToPrinterDialog() {
        int printContent = 0;
        if (mPrintDocumentChecked) {
            printContent |= Print.PRINT_CONTENT_DOCUMENT_BIT;
        }
        if (mPrintAnnotationsChecked) {
            printContent |= Print.PRINT_CONTENT_ANNOTATION_BIT;
        }
        if (mPrintSummaryChecked) {
            printContent |= Print.PRINT_CONTENT_SUMMARY_BIT;
        }
        handlePrintJob(printContent);
    }

    private void handlePrintJob(int _printContent) {
        Activity activity = getActivity();
        if (activity == null || mPdfViewCtrl == null) {
            return;
        }

        if (_printContent < 1 || _printContent > (Print.PRINT_CONTENT_DOCUMENT_BIT |
                Print.PRINT_CONTENT_ANNOTATION_BIT | Print.PRINT_CONTENT_SUMMARY_BIT)) {
            return;
        }
        Integer printContent = _printContent;
        Boolean isRtl = isRtlMode();

        try {
            String outputName = FilenameUtils.getBaseName(getTabTitle()) + "-print";
            if (mTabSource == BaseFileInfo.FILE_TYPE_OPEN_URL) {
                Print.startPrintJob(activity, getString(R.string.app_name), outputName, mPdfViewCtrl.getDoc(), printContent, isRtl, mPdfViewCtrl.getOCGContext());
            } else {
                Print.startPrintJob(activity, getString(R.string.app_name), outputName, mPdfDoc, printContent, isRtl, mPdfViewCtrl.getOCGContext());
            }
        } catch (Exception e) {
            CommonToast.showText(activity, R.string.error_printing_file, Toast.LENGTH_SHORT);
            AnalyticsHandlerAdapter.getInstance().sendException(e);
        }
    }

    /**
     * Called when document annotations have been exported.
     *
     * @param outputDoc The PDFDoc containing the exported annotations
     */
    public void onExportAnnotations(PDFDoc outputDoc) {
        Activity activity = getActivity();
        if (activity == null) {
            return;
        }

        switch (mTabSource) {
            case BaseFileInfo.FILE_TYPE_FILE:
            case BaseFileInfo.FILE_TYPE_OPEN_URL:
            case BaseFileInfo.FILE_TYPE_EDIT_URI:
                if (null == mCurrentFile || !mCurrentFile.exists()) {
                    return;
                }
                handleExportAnnotations(mCurrentFile.getParentFile(), outputDoc);
                return;
            case BaseFileInfo.FILE_TYPE_EXTERNAL:
                if (mCurrentUriFile == null) {
                    return;
                }
                ExternalFileInfo fileInfo = Utils.buildExternalFile(activity, mCurrentUriFile);
                if (fileInfo != null) {
                    handleExportAnnotations(fileInfo.getParent(), outputDoc);
                }
                return;
        }
    }

    protected void handleExportAnnotations(File folder, PDFDoc outputDoc) {
        boolean shouldUnlock = false;
        File outputFile = null;
        boolean success = false;

        if (folder == null || outputDoc == null) {
            return;
        }

        try {
            String extension = getString(R.string.document_export_annotations_extension);
            File tempFile = new File(folder, mTabTitle + extension + ".pdf");
            String outputPath = Utils.getFileNameNotInUse(tempFile.getAbsolutePath());
            if (Utils.isNullOrEmpty(outputPath)) {
                return;
            }
            outputFile = new File(outputPath);
            outputDoc.lock();
            shouldUnlock = true;
            outputDoc.save(outputFile.getAbsolutePath(), SDFDoc.SaveMode.REMOVE_UNUSED, null);
            success = true;
        } catch (Exception ePDFNet) {
            AnalyticsHandlerAdapter.getInstance().sendException(ePDFNet);
        } finally {
            if (shouldUnlock) {
                Utils.unlockQuietly(outputDoc);
            }
            Utils.closeQuietly(outputDoc);
        }

        if (success) {
            openFileInNewTab(outputFile);
        }
    }

    protected void handleExportAnnotations(ExternalFileInfo folder, PDFDoc outputDoc) {
        Activity activity = getActivity();
        if (activity == null || folder == null || outputDoc == null) {
            return;
        }

        boolean shouldUnlock = false;
        SecondaryFileFilter filter = null;
        try {
            String extension = getString(R.string.document_export_annotations_extension);
            String outputPath = Utils.getFileNameNotInUse(folder, mTabTitle + extension + ".pdf");
            ExternalFileInfo outputFile = folder.createFile("application/pdf", outputPath);

            if (outputFile != null) {
                outputDoc.lock();
                shouldUnlock = true;

                filter = new SecondaryFileFilter(activity, outputFile.getUri());
                outputDoc.save(filter, SDFDoc.SaveMode.REMOVE_UNUSED);
                openFileUriInNewTab(outputFile.getUri());
            }
        } catch (Exception ePDFNet) {
            AnalyticsHandlerAdapter.getInstance().sendException(ePDFNet);
        } finally {
            if (shouldUnlock) {
                Utils.unlockQuietly(outputDoc);
            }
            Utils.closeQuietly(outputDoc, filter);
        }
    }

    protected void handleExportAnnotations(Uri targetFile, PDFDoc outputDoc) {
        Activity activity = getActivity();
        if (activity == null || targetFile == null || outputDoc == null) {
            return;
        }

        boolean shouldUnlock = false;
        SecondaryFileFilter filter = null;
        try {
            outputDoc.lock();
            shouldUnlock = true;

            filter = new SecondaryFileFilter(activity, targetFile);
            outputDoc.save(filter, SDFDoc.SaveMode.REMOVE_UNUSED);
            openFileUriInNewTab(targetFile);
        } catch (Exception ePDFNet) {
            AnalyticsHandlerAdapter.getInstance().sendException(ePDFNet);
        } finally {
            if (shouldUnlock) {
                Utils.unlockQuietly(outputDoc);
            }
            Utils.closeQuietly(outputDoc, filter);
        }
    }

    /**
     * Returns the URI file
     *
     * @return the URI file
     */
    public Uri getUriFile() {
        return mCurrentUriFile;
    }

    /**
     * Returns the file.
     *
     * @return the file
     */
    public File getFile() {
        if (mTabConversionTempPath != null && (new File(mTabConversionTempPath)).exists()) {
            return new File(mTabConversionTempPath);
        }
        return mCurrentFile;
    }

    /**
     * Refreshes the number of pages.
     */
    public void refreshPageCount() {
        boolean shouldUnlockRead = false;
        try {
            mPdfViewCtrl.docLockRead();
            shouldUnlockRead = true;
            mPageCount = mPdfViewCtrl.getDoc().getPageCount();
        } catch (Exception e) {
            AnalyticsHandlerAdapter.getInstance().sendException(e);
        } finally {
            if (shouldUnlockRead) {
                mPdfViewCtrl.docUnlockRead();
            }
        }
        sliderRefreshPageCount();
        updatePageIndicator();
        if (mTabListener != null) {
            mTabListener.onUpdateOptionsMenu();
        }
    }

    /**
     * Checks whether the document can be saved.
     *
     * @return True if the document can be saved
     */
    public boolean canDocBeSaved() {
        return mDocumentState != PdfDocManager.DOCUMENT_STATE_DURING_CONVERSION;
    }

    /**
     * Checks whether the document is not PDF.
     *
     * @return True if the document is not PDF
     */
    public boolean isNotPdf() {
        if (!Utils.isNullOrEmpty(mFileExtension)) {
            return Utils.isNotPdfFromExt(mFileExtension);
        }
        return Utils.isNotPdf(mTabTag);
    }

    public boolean isNotPdfUri() {
        if (!Utils.isNullOrEmpty(mFileExtension)) {
            return Utils.isNotPdfFromExt(mFileExtension);
        }
        ContentResolver contentResolver = Utils.getContentResolver(getActivity());
        if (contentResolver == null) {
            return false;
        }
        return Utils.isNotPdf(contentResolver, Uri.parse(mTabTag));
    }

    public boolean isConvertibleFormat() {
        // all formats that we may be able to convert
        if (!Utils.isNullOrEmpty(mFileExtension)) {
            return Utils.isConvertibleFormatFromExt(mFileExtension);
        }
        return Utils.isConvertibleFormat(mTabTag);
    }

    public boolean isWebViewConvertibleFormat() {
        if (!Utils.isLollipop()) {
            // WebView conversion only supported for Lollipop+
            return false;
        }
        // all formats that can be converted with WebView
        if (!Utils.isNullOrEmpty(mFileExtension)) {
            return Utils.isConvertibleFormatFromExt(mFileExtension, Constants.FILE_NAME_EXTENSIONS_WEBVIEW);
        }
        return Utils.isConvertibleFormat(mTabTag, Constants.FILE_NAME_EXTENSIONS_WEBVIEW);
    }

    public boolean doesSaveDocNeedNewTab() {
        return !(mDocumentState == PdfDocManager.DOCUMENT_STATE_DURING_CONVERSION ||
                mDocumentState == PdfDocManager.DOCUMENT_STATE_FROM_CONVERSION);
    }

    /**
     * Returns the client background color of PDFViewCtrl
     *
     * @param context used to obtain default PDFViewCtrlConfig if applicable
     * @return Returns the client background color of PDFViewCtrl
     */
    protected int getClientBackgroundColor(@NonNull Context context) {
        return getPDFViewCtrlConfig(context).getClientBackgroundColor();
    }

    /**
     * Returns the client background color of PDFViewCtrl when in dark mode
     *
     * @param context used to obtain default PDFViewCtrlConfig if applicable
     * @return Returns the client background color of PDFViewCtrl when in dark mode
     */
    protected int getClientBackgroundColorDark(@NonNull Context context) {
        return getPDFViewCtrlConfig(context).getClientBackgroundColorDark();
    }

    /**
     * Updates the color mode.
     */
    public void updateColorMode() {
        FragmentActivity activity = getActivity();
        if (activity == null || mPdfViewCtrl == null) {
            return;
        }

        int colorMode = PdfViewCtrlSettingsManager.getColorMode(activity);
        int clientBackgroundColor = getClientBackgroundColor(activity);
        int clientBackgroundColorDark = getClientBackgroundColorDark(activity);
        int mode = PDFRasterizer.e_postprocess_none;
        int customBGColor = 0;
        int customTxtColor = 0;
        switch (colorMode) {
            case PdfViewCtrlSettingsManager.KEY_PREF_COLOR_MODE_CUSTOM:
                customBGColor = PdfViewCtrlSettingsManager.getCustomColorModeBGColor(activity);
                customTxtColor = PdfViewCtrlSettingsManager.getCustomColorModeTextColor(activity);
                clientBackgroundColor = getViewerBackgroundColor(customBGColor);
                mode = PDFRasterizer.e_postprocess_gradient_map;
                break;
            case PdfViewCtrlSettingsManager.KEY_PREF_COLOR_MODE_SEPIA:
                mode = PDFRasterizer.e_postprocess_gradient_map;
                InputStream is = null;
                OutputStream os = null;
                try {
                    File filterFile = new File(activity.getCacheDir(), "sepia_mode_filter.png");
                    if (!filterFile.exists() || !filterFile.isFile()) {
                        is = getResources().openRawResource(R.raw.sepia_mode_filter);
                        os = new FileOutputStream(filterFile);
                        IOUtils.copy(is, os);
                    }
                    mPdfViewCtrl.setColorPostProcessMapFile(new MappedFile(filterFile.getAbsolutePath()));
                } catch (Exception e) {
                    AnalyticsHandlerAdapter.getInstance().sendException(e);
                } finally {
                    Utils.closeQuietly(is);
                    Utils.closeQuietly(os);
                }
                break;
            case PdfViewCtrlSettingsManager.KEY_PREF_COLOR_MODE_NIGHT:
                clientBackgroundColor = clientBackgroundColorDark;
                mode = PDFRasterizer.e_postprocess_night_mode;
                break;
        }

        try {
            mPdfViewCtrl.setClientBackgroundColor(
                    Color.red(clientBackgroundColor),
                    Color.green(clientBackgroundColor),
                    Color.blue(clientBackgroundColor), false);
            mPdfViewCtrl.setColorPostProcessMode(mode);
            if (colorMode == PdfViewCtrlSettingsManager.KEY_PREF_COLOR_MODE_CUSTOM) {
                mPdfViewCtrl.setColorPostProcessColors(customBGColor, customTxtColor);
            }
        } catch (Exception e) {
            AnalyticsHandlerAdapter.getInstance().sendException(e);
        }
        mViewerHost.setBackgroundColor(mPdfViewCtrl.getClientBackgroundColor());

        mToolManager.setNightMode(isNightModeForToolManager());
        if (mIsReflowMode) {
            updateReflowColorMode();
        }
    }

    private static int getViewerBackgroundColor(int color) {
        float[] hsv = new float[3];
        RGBToHSV(Color.red(color), Color.green(color), Color.blue(color), hsv);
        float hue = hsv[0] / 360f;
        float saturation = hsv[1];
        float value = hsv[2];

        float lowEarthHue = 0.05f;
        float highEarthHue = 0.11f;
        boolean earthTones = hue >= lowEarthHue && hue <= highEarthHue;
        if (value > 0.5) {
            if (earthTones) {
                value -= 0.2;
                saturation = Math.min(saturation * 2, Math.min(saturation + 0.05f, 1.0f));
            } else {
                value *= 0.6;
            }
        } else if (value >= 0.3) {
            value = (value / 2) + 0.05f;
        } else if (value >= 0.1) {
            value -= 0.1;
        } else {
            value += 0.1;
        }
        if (!earthTones) {
            float dist = Math.min(0.05f, lowEarthHue - hue);
            if (hue > highEarthHue) {
                dist = Math.min(0.05f, hue - highEarthHue);
            }
            saturation = saturation - (saturation * (20f * dist) * 0.6f);
        }

        hsv[0] = hue * 360f;
        hsv[1] = saturation;
        hsv[2] = value;
        return HSVToColor(hsv);
    }

    /**
     * Updates color mode in reflow
     */
    protected void updateReflowColorMode() {
        Activity activity = getActivity();
        if (activity == null) {
            return;
        }
        if (mReflowControl != null && mReflowControl.isReady()) {
            try {
                switch (PdfViewCtrlSettingsManager.getColorMode(activity)) {
                    case PdfViewCtrlSettingsManager.KEY_PREF_COLOR_MODE_NORMAL:
                        mReflowControl.setDayMode();
                        break;
                    case PdfViewCtrlSettingsManager.KEY_PREF_COLOR_MODE_SEPIA:
                        mReflowControl.setCustomColorMode(0xFFffead2);
                        break;
                    case PdfViewCtrlSettingsManager.KEY_PREF_COLOR_MODE_NIGHT:
                        mReflowControl.setNightMode();
                        break;
                    case PdfViewCtrlSettingsManager.KEY_PREF_COLOR_MODE_CUSTOM:
                        mReflowControl.setCustomColorMode(PdfViewCtrlSettingsManager.getCustomColorModeBGColor(activity));
                        break;
                }
            } catch (PDFNetException e) {
                AnalyticsHandlerAdapter.getInstance().sendException(e);
            }
        }
    }

    /**
     * Specifies that color mode has been changed.
     */
    public void setColorModeChanged() {
        mColorModeChanged = true;
    }

    // TODO: Part of listener/callback/interface?

    /**
     * Called when the {@link androidx.appcompat.widget.Toolbar toolbar} of the containing
     * host fragment will be shown.
     *
     * @return {@code true} if the toolbar can be shown, {@code false} otherwise.
     */
    public boolean onShowToolbar() {
        return true;
    }

    /**
     * Called when the {@link androidx.appcompat.widget.Toolbar toolbar} of the containing
     * host fragment will be hidden.
     *
     * @return {@code true} if the toolbar can be hidden, {@code false} otherwise.
     */
    public abstract boolean onHideToolbars();

    /**
     * Called before the containing host fragment enters
     * fullscreen mode (system status bar and navigation bar will be hidden).
     *
     * @return {@code true} if fullscreen mode can be entered, {@code false} otherwise.
     */
    public boolean onEnterFullscreenMode() {
        return true;
    }

    /**
     * Called before the containing host fragment exits
     * fullscreen mode (system status bar and/or navigation bar will be shown).
     *
     * @return {@code true} if fullscreen mode can be exited, {@code false} otherwise.
     */
    public boolean onExitFullscreenMode() {
        return true;
    }

    public void applyCutout(int top, int bottom) {
        if (mPdfViewCtrl == null) {
            return;
        }
        if (mPdfViewCtrl.getDisplayCutoutTop() != top || mPdfViewCtrl.getDisplayCutoutBottom() != bottom) {
            if (top == -1) {
                top = mPdfViewCtrl.getDisplayCutoutTop();
            }
            if (bottom == -1) {
                bottom = mPdfViewCtrl.getDisplayCutoutBottom();
            }
            mPdfViewCtrl.setDisplayCutout(top, bottom);
        }
        if (mReflowControl != null) {
            mReflowControl.setPadding(0, top, 0, bottom);
        }
    }

    /**
     * Sets the visibility of thumbnail slider.
     *
     * @param visible            True if the thumbnail slider should be visible
     * @param animateThumbSlider True if the visibility should be changed with animation
     */
    public abstract void setThumbSliderVisible(boolean visible, boolean animateThumbSlider);

    /**
     * Handles the activity result
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (Activity.RESULT_OK == resultCode) {
            if (requestCode == RequestCode.PICK_PHOTO_CAM) {
                // save the data and process the image stamp
                // after onResume is called.
                if (mImageCreationMode != null) {
                    if (mImageCreationMode == ToolMode.SIGNATURE) {
                        mImageSignatureDelayCreation = true;
                        mAnnotIntentData = data;
                    } else {
                        mImageStampDelayCreation = true;
                        mAnnotIntentData = data;
                    }
                }
            } else if (requestCode == RequestCode.SELECT_FILE) {
                // save the data and process the file attachment
                // after onResume is called.
                mFileAttachmentDelayCreation = true;
                mAnnotIntentData = data;
            } else if (requestCode == RequestCode.CREATE_FILE_IN_SYSTEM) {
                mSaveFileAttachmentDelay = true;
                mAnnotIntentData = data;
            }
        } else {
            if (mToolManager != null && mToolManager.getTool() != null) {
                ((Tool) mToolManager.getTool()).clearTargetPoint();
            }
        }
    }

    /**
     * Updates documentation mode in print.
     *
     * @param enabled True if the documentation mode is checked
     */
    public void updatePrintDocumentMode(boolean enabled) {
        Activity activity = getActivity();
        if (activity == null) {
            return;
        }

        mPrintDocumentChecked = enabled;
        PdfViewCtrlSettingsManager.setPrintDocumentMode(activity, mPrintDocumentChecked);
    }

    /**
     * Updates annotation mode in print.
     *
     * @param enabled True if the annotation mode is checked
     */
    public void updatePrintAnnotationsMode(boolean enabled) {
        Activity activity = getActivity();
        if (activity == null) {
            return;
        }

        mPrintAnnotationsChecked = enabled;
        PdfViewCtrlSettingsManager.setPrintAnnotationsMode(activity, mPrintAnnotationsChecked);
    }

    /**
     * Updates summery mode in print.
     *
     * @param enabled True if the summery mode is checked
     */
    public void updatePrintSummaryMode(boolean enabled) {
        Activity activity = getActivity();
        if (activity == null) {
            return;
        }

        mPrintSummaryChecked = enabled;
        PdfViewCtrlSettingsManager.setPrintSummaryMode(activity, mPrintSummaryChecked);
    }

    /**
     * Updates the recent list.
     */
    public void updateRecentList() {
        if (!isDocumentReady()) {
            return;
        }

        updateRecentFile(getCurrentFileInfo());
    }

    /**
     * Updates the recent file.
     *
     * @param fileInfo The {@link FileInfo}
     */
    protected void updateRecentFile(FileInfo fileInfo) {
        if (mPdfViewCtrl == null) {
            return;
        }

        if (fileInfo != null) {
            fileInfo.setHScrollPos(mPdfViewCtrl.getHScrollPos());
            fileInfo.setVScrollPos(mPdfViewCtrl.getVScrollPos());
            fileInfo.setZoom(mPdfViewCtrl.getZoom());
            fileInfo.setLastPage(mPdfViewCtrl.getCurrentPage());
            fileInfo.setPageRotation(mPdfViewCtrl.getPageRotation());
            fileInfo.setPagePresentationMode(mPdfViewCtrl.getPagePresentationMode());
            fileInfo.setReflowMode(mIsReflowMode);
            if (mReflowControl != null && mReflowControl.isReady()) {
                try {
                    int reflowTextSize = mReflowControl.getTextSizeInPercent();
                    fileInfo.setReflowTextSize(reflowTextSize);
                } catch (Exception e) {
                    AnalyticsHandlerAdapter.getInstance().sendException(e);
                }
            }
            fileInfo.setRtlMode(mIsRtlMode);
            fileInfo.setBookmarkDialogCurrentTab(mBookmarkDialogCurrentTab);
            updateRecentFilesManager(fileInfo);
        }
    }

    private void updateRecentFilesManager(FileInfo fileInfo) {
        Activity activity = getActivity();
        if (activity == null) {
            return;
        }

        getRecentFilesManager().updateFile(activity, fileInfo);
    }

    protected FileInfoManager getRecentFilesManager() {
        return RecentFilesManager.getInstance();
    }

    /**
     * Removes the current file from the recent list.
     */
    public void removeFromRecentList() {
        Activity activity = getActivity();
        if (activity == null) {
            return;
        }

        switch (mTabSource) {
            case BaseFileInfo.FILE_TYPE_FILE:
                if (mCurrentFile != null) {
                    RecentFilesManager.getInstance().removeFile(activity,
                            new FileInfo(BaseFileInfo.FILE_TYPE_FILE, mCurrentFile, mIsEncrypted, 1));
                }
                break;
            case BaseFileInfo.FILE_TYPE_EXTERNAL:
            case BaseFileInfo.FILE_TYPE_EDIT_URI:
            case BaseFileInfo.FILE_TYPE_OFFICE_URI:
                RecentFilesManager.getInstance().removeFile(activity,
                        new FileInfo(mTabSource, mTabTag, mTabTitle, mIsEncrypted, 1));
                break;
        }
    }

    protected boolean containsInRecentList(FileInfo fileInfo) {
        Activity activity = getActivity();
        return activity != null && RecentFilesManager.getInstance().containsFile(activity, fileInfo);
    }

    /**
     * Adds a new tab info to the recent list.
     *
     * @param tabInfo The {@link PdfViewCtrlTabInfo}
     */
    protected void addToRecentList(PdfViewCtrlTabInfo tabInfo) {
        if (tabInfo == null) {
            return;
        }

        FileInfo fileInfo = null;
        try {
            switch (tabInfo.tabSource) {
                case BaseFileInfo.FILE_TYPE_FILE:
                case BaseFileInfo.FILE_TYPE_OPEN_URL:
                    if (mCurrentFile != null) {
                        fileInfo = new FileInfo(BaseFileInfo.FILE_TYPE_FILE, mCurrentFile, mIsEncrypted, 1);
                    }
                    break;
                case BaseFileInfo.FILE_TYPE_EXTERNAL:
                case BaseFileInfo.FILE_TYPE_OFFICE_URI:
                    fileInfo = new FileInfo(tabInfo.tabSource, mTabTag, mTabTitle + "." + mFileExtension, mIsEncrypted, 1);
                    break;
                case BaseFileInfo.FILE_TYPE_EDIT_URI:
                    if (mCurrentFile != null) {
                        fileInfo = new FileInfo(BaseFileInfo.FILE_TYPE_EDIT_URI, mTabTag, mTabTitle + "." + mFileExtension, mIsEncrypted, 1);
                        fileInfo.setFile(mCurrentFile);
                    }
                    break;
            }
        } catch (Exception e) {
            AnalyticsHandlerAdapter.getInstance().sendException(e);
        }

        if (fileInfo != null) {
            addRecentFile(tabInfo, fileInfo);
        }
    }

    @Nullable
    File getAndroidQEditUriRealPath() {
        Activity activity = getActivity();
        if (activity != null && Utils.isAndroidQ()) {
            // for android 10 we use real path in error message when permission lost
            FileInfo recentFileInfo = RecentFilesManager.getInstance().getFile(activity,
                    new FileInfo(BaseFileInfo.FILE_TYPE_EDIT_URI, mTabTag, mTabTitle, mIsEncrypted, 1));
            if (recentFileInfo != null) {
                return recentFileInfo.getFile();
            }
        }
        return null;
    }

    protected void addRecentFile(@NonNull PdfViewCtrlTabInfo tabInfo, @Nullable FileInfo fileInfo) {
        if (fileInfo != null) {
            fileInfo.setLastPage(tabInfo.lastPage);
            fileInfo.setPageRotation(tabInfo.pageRotation);
            fileInfo.setPagePresentationMode(tabInfo.getPagePresentationMode());
            fileInfo.setHScrollPos(tabInfo.hScrollPos);
            fileInfo.setVScrollPos(tabInfo.vScrollPos);
            fileInfo.setZoom(tabInfo.zoom);
            fileInfo.setReflowMode(tabInfo.isReflowMode);
            fileInfo.setReflowTextSize(tabInfo.reflowTextSize);
            fileInfo.setRtlMode(tabInfo.isRtlMode);
            fileInfo.setBookmarkDialogCurrentTab(tabInfo.bookmarkDialogCurrentTab);

            addToRecentFilesManager(fileInfo);
        }
    }

    protected void addToRecentFilesManager(FileInfo fileInfo) {
        Activity activity = getActivity();
        if (activity == null) {
            return;
        }

        RecentFilesManager.getInstance().addFile(activity, fileInfo);
    }

    public void resetAutoSavingTimer() {
        stopAutoSavingTimer();
        if (mRequestSaveDocHandler != null) {
            mRequestSaveDocHandler.post(mTickSaveDocCallback);
        }
    }

    public void stopAutoSavingTimer() {
        if (mRequestSaveDocHandler != null) {
            mRequestSaveDocHandler.removeCallbacksAndMessages(null);
        }
    }

    public long getCurrentFileSize() {
        try {
            if (mCurrentFile != null) {
                return mCurrentFile.length();
            } else if (mCurrentUriFile != null) {
                ExternalFileInfo externalFileInfo = Utils.buildExternalFile(getContext(), mCurrentUriFile);
                if (externalFileInfo != null) {
                    return externalFileInfo.getSize();
                }
            }
        } catch (Exception ex) {
            AnalyticsHandlerAdapter.getInstance().sendException(ex);
        }
        return -1;
    }

    /**
     * Returns the current file info.
     *
     * @return The {@link FileInfo}
     */
    public FileInfo getCurrentFileInfo() {
        FileInfo fileInfo = null;
        switch (mTabSource) {
            case BaseFileInfo.FILE_TYPE_FILE:
            case BaseFileInfo.FILE_TYPE_OPEN_URL:
                if (mCurrentFile != null) {
                    fileInfo = new FileInfo(BaseFileInfo.FILE_TYPE_FILE, mCurrentFile, mIsEncrypted, 1);
                }
                break;
            case BaseFileInfo.FILE_TYPE_EXTERNAL:
            case BaseFileInfo.FILE_TYPE_OFFICE_URI:
                fileInfo = new FileInfo(mTabSource, mTabTag, mTabTitle + "." + mFileExtension, mIsEncrypted, 1);
                break;
            case BaseFileInfo.FILE_TYPE_EDIT_URI:
                fileInfo = new FileInfo(BaseFileInfo.FILE_TYPE_EDIT_URI, mTabTag, mTabTitle + "." + mFileExtension, mIsEncrypted, 1);
                if (mCurrentFile != null) {
                    fileInfo.setFile(mCurrentFile);
                }
                break;
        }
        return fileInfo;
    }

    protected PdfViewCtrlTabInfo getCurrentTabInfo(Activity activity) {
        PdfViewCtrlTabInfo info = new PdfViewCtrlTabInfo();
        info.tabTitle = mTabTitle;
        info.tabSource = mTabSource;
        info.fileExtension = mFileExtension;
        if (activity != null) {
            // restore last selected view mode
            String mode = PdfViewCtrlSettingsManager.getViewMode(activity);
            info.pagePresentationMode = getPagePresentationModeFromSettings(mode).getValue();
            info.isRtlMode = PdfViewCtrlSettingsManager.getInRTLMode(activity);
        }

        return info;
    }

    /**
     * Sets the visibility of search navigation buttons.
     *
     * @param visible True if visible
     */
    public void setSearchNavButtonsVisible(boolean visible) {
        Activity activity = getActivity();
        if (activity == null) {
            return;
        }

        int visibility = visible ? View.VISIBLE : View.GONE;
        mSearchOverlay.setVisibility(visibility);
    }

    /**
     * Shows backward/forward buttons.
     */
    public void showBackAndForwardButtons() {
        boolean canShow = mViewerConfig == null || mViewerConfig.isPageStackEnabled();
        if (canShow && mPageNavContainer != null) {
            mPageNavContainer.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Hides backward/forward buttons.
     */
    public void hideBackAndForwardButtons() {
        boolean canShow = mViewerConfig == null || mViewerConfig.isPageStackEnabled();
        if (canShow && mPageNavContainer != null) {
            mPageNavContainer.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * Highlights the results of full text search.
     *
     * @param result The {@link com.pdftron.pdf.TextSearchResult}
     */
    public void highlightFullTextSearchResult(com.pdftron.pdf.TextSearchResult result) {
        if (mSearchOverlay != null) {
            mSearchOverlay.highlightFullTextSearchResult(result);
        }
    }

    protected void openFileInNewTab(File file) {
        openFileInNewTab(file, mPassword, -1);
    }

    protected void openFileInNewTab(File file, String password) {
        openFileInNewTab(file, password, -1);
    }

    protected void openFileInNewTab(File file, String password, int pageNumber) {
        if (mPdfViewCtrl == null) {
            return;
        }

        if (file == null) {
            handleOpeningDocumentFailed(PdfDocManager.DOCUMENT_SETDOC_ERROR_NULL_PDFDOC);
            return;
        }
        if (!file.exists()) {
            handleOpeningDocumentFailed(PdfDocManager.DOCUMENT_SETDOC_ERROR_NOT_EXIST);
            return;
        }
        if (mTabListener != null) {
            mPdfViewCtrl.closeTool();
            mTabListener.onOpenAddNewTab(BaseFileInfo.FILE_TYPE_FILE, file.getAbsolutePath(), file.getName(), password, pageNumber);
        }
    }

    protected void openFileUriInNewTab(Uri fileUri) {
        openFileUriInNewTab(fileUri, mPassword);
    }

    protected void openFileUriInNewTab(Uri fileUri, String password) {
        openFileUriInNewTab(fileUri, password, -1);
    }

    protected void openFileUriInNewTab(Uri fileUri, String password, int pageNumber) {
        Activity activity = getActivity();
        if (activity == null || mPdfViewCtrl == null) {
            return;
        }

        if (fileUri == null) {
            handleOpeningDocumentFailed(PdfDocManager.DOCUMENT_SETDOC_ERROR_NULL_PDFDOC);
            return;
        }
        if (mTabListener != null) {
            ExternalFileInfo info = Utils.buildExternalFile(activity, fileUri);
            if (info != null) {
                mPdfViewCtrl.closeTool();
                mTabListener.onOpenAddNewTab(BaseFileInfo.FILE_TYPE_EXTERNAL, fileUri.toString(), info.getFileName(), password, pageNumber);
            } else {
                if (Utils.isNotPdf(activity.getContentResolver(), fileUri)) {
                    mTabListener.onOpenAddNewTab(BaseFileInfo.FILE_TYPE_OFFICE_URI, fileUri.toString(), Utils.getUriDisplayName(activity, fileUri), password, pageNumber);
                } else {
                    mTabListener.onOpenAddNewTab(BaseFileInfo.FILE_TYPE_EDIT_URI, fileUri.toString(), Utils.getUriDisplayName(activity, fileUri), password, pageNumber);
                }
            }
        }
    }

    protected void consumeImageSignature() {
        ViewerUtils.createImageSignature(getActivity(), mAnnotIntentData, mPdfViewCtrl,
                mOutputFileUri, mAnnotTargetPoint, mAnnotTargetPage, mTargetWidget);
    }

    protected boolean canResumeWithoutReloading() {
        Activity activity = getActivity();
        if (activity == null || mPdfDoc == null) {
            return false;
        }

        // PDFDoc was previously initialized
        // let's ensure file source is still valid
        switch (mTabSource) {
            case BaseFileInfo.FILE_TYPE_FILE:
            case BaseFileInfo.FILE_TYPE_OPEN_URL:
            case BaseFileInfo.FILE_TYPE_EDIT_URI:
                // check if local file still valid
                if (null == mCurrentFile || !mCurrentFile.exists()) {
                    return false;
                }
                // if universal conversion cancelled due to fragment pause/hidden change
                // then need to open it again
                return !(mTabSource == BaseFileInfo.FILE_TYPE_FILE
                        && isNotPdf() && !mIsOfficeDocReady);
            case BaseFileInfo.FILE_TYPE_EXTERNAL:
                // check if sd card file still valid
                if (mCurrentUriFile == null) {
                    return false;
                }
                boolean exists = Utils.uriHasReadPermission(activity, mCurrentUriFile);
                ContentResolver contentResolver = Utils.getContentResolver(activity);
                return exists && contentResolver != null
                        && !Utils.isNotPdf(contentResolver, Uri.parse(mTabTag));
        }

        return false;
    }

    protected void resumeFragment(boolean fromOnResume) {
        Activity activity = getActivity();
        if (activity == null || mPdfViewCtrl == null) {
            return;
        }
        if (sDebug)
            Log.d("timing", "resumeFragment start");

        mPdfViewCtrl.resume();
        mHasChangesSinceResumed = false;

        resetAutoSavingTimer();

        if (mCanAddToTabInfo) {
            PdfViewCtrlTabInfo tabInfo = PdfViewCtrlTabsManager.getInstance().getPdfFViewCtrlTabInfo(activity, mTabTag);
            if (tabInfo != null) {
                // update last viewed timestamp
                PdfViewCtrlTabsManager.getInstance().updateLastViewedTabTimestamp(activity, mTabTag);

                // update recent list
                addToRecentList(tabInfo);
            } else if (PdfViewCtrlTabsManager.getInstance().getNewPath(mTabTag) == null
                    && !containsInRecentList(getCurrentFileInfo())) {
                // add to recent list if not opened before
                addToRecentList(getCurrentTabInfo(activity));
            }
        }

        toggleViewerVisibility(false);

        if (null != mToolManager && mToolManager.getTool() instanceof TextHighlighter) {
            highlightSearchResults();
        }

        if (!mDocumentLoading) {
            boolean handled = true;
            mDocumentLoading = true;
            if (mPdfDoc != null) {
                // PDFDoc was previously initialized
                // let's ensure file source is still valid
                boolean fileNotExists = false;
                switch (mTabSource) {
                    case BaseFileInfo.FILE_TYPE_FILE:
                    case BaseFileInfo.FILE_TYPE_OPEN_URL:
                    case BaseFileInfo.FILE_TYPE_EDIT_URI:
                        // check if local file still valid
                        if (mCurrentFile == null) {
                            fileNotExists = true;
                        } else if (!mCurrentFile.exists()) {
                            String path = PdfViewCtrlTabsManager.getInstance().getNewPath(mTabTag);
                            if (!Utils.isNullOrEmpty(path) && new File(path).exists()) {
                                String oldTabTag = mTabTag;
                                mPdfDoc = null;
                                mTabTag = path;
                                mTabTitle = FilenameUtils.removeExtension(new File(path).getName());
                                mCurrentFile = new File(mTabTag);
                                if (mTabListener != null) {
                                    mTabListener.onTabIdentityChanged(oldTabTag, mTabTag, mTabTitle, mFileExtension, mTabSource);
                                }
                            } else {
                                fileNotExists = true;
                            }
                        } else {
                            if (!mIsOfficeDocReady
                                    && (mTabSource == BaseFileInfo.FILE_TYPE_FILE && isNotPdf())) {
                                // if universal conversion cancelled due to fragment pause/hidden change
                                // then need to open it again
                                openOfficeDoc(mTabTag, false);
                            } else {
                                // doc is loaded properly
                                toggleViewerVisibility(true);
                                mDocumentLoading = false;
                            }
                        }
                        break;
                    case BaseFileInfo.FILE_TYPE_OFFICE_URI:
                        if (!mIsOfficeDocReady) {
                            openOfficeDoc(mTabTag, true);
                        } else {
                            // doc is loaded properly
                            toggleViewerVisibility(true);
                            mDocumentLoading = false;
                        }
                        break;
                    case BaseFileInfo.FILE_TYPE_EXTERNAL:
                        // check if sd card file still valid
                        if (mCurrentUriFile == null) {
                            mErrorCode = PdfDocManager.DOCUMENT_SETDOC_ERROR_NOT_EXIST;
                            handleOpeningDocumentFailed(mErrorCode);
                        } else {
                            ExternalFileInfo externalFileInfo = Utils.buildExternalFile(getContext(), mCurrentUriFile);
                            // First check if file is seekable (and has read/write permission) before
                            // checking if external file exists. Could be the case where URI is seekable
                            // but no path is available, so in this case we can just continue with opening the document.
                            if (!Utils.isUriSeekable(activity, mCurrentUriFile) && (externalFileInfo == null || !externalFileInfo.exists())) {
                                String path = PdfViewCtrlTabsManager.getInstance().getNewPath(mTabTag);
                                if (path == null) {
                                    fileNotExists = true;
                                } else {
                                    externalFileInfo = Utils.buildExternalFile(getContext(), Uri.parse(path));
                                    if (externalFileInfo == null || !externalFileInfo.exists()) {
                                        fileNotExists = true;
                                    } else {
                                        String oldTabTag = mTabTag;
                                        mPdfDoc = null;
                                        mTabTag = path;
                                        mTabTitle = FilenameUtils.removeExtension(externalFileInfo.getName());
                                        mCurrentUriFile = Uri.parse(mTabTag);
                                        if (mTabListener != null) {
                                            mTabListener.onTabIdentityChanged(oldTabTag, mTabTag, mTabTitle, mFileExtension, mTabSource);
                                        }
                                    }
                                }
                            } else {
                                ContentResolver contentResolver = Utils.getContentResolver(activity);
                                if (contentResolver == null) {
                                    handled = false;
                                    break;
                                }
                                if (Utils.isNotPdf(contentResolver, Uri.parse(mTabTag))) {
                                    openOfficeDoc(mTabTag, true);
                                } else {
                                    // doc is loaded properly
                                    toggleViewerVisibility(true);
                                    mDocumentLoading = false;
                                }
                            }
                        }
                        break;
                    default:
                        handled = false;
                }
                if (fileNotExists) {
                    // something went wrong, report error
                    mErrorCode = PdfDocManager.DOCUMENT_SETDOC_ERROR_NOT_EXIST;
                    handleOpeningDocumentFailed(mErrorCode);
                }
            }

            if (mPdfDoc == null) {
                // open when onResume
                // close when onPause
                switch (mTabSource) {
                    case BaseFileInfo.FILE_TYPE_FILE:
                        if (isNotPdf()) {
                            openOfficeDoc(mTabTag, false);
                        } else {
                            openLocalFile(mTabTag);
                        }
                        break;
                    case BaseFileInfo.FILE_TYPE_EXTERNAL:
                        if (isNotPdfUri()) {
                            openOfficeDoc(mTabTag, true);
                        } else {
                            openExternalFile(mTabTag);
                        }
                        break;
                    case BaseFileInfo.FILE_TYPE_OPEN_URL:
                        openUrlFile(mTabTag);
                        break;
                    case BaseFileInfo.FILE_TYPE_EDIT_URI:
                        if (isNotPdfUri()) {
                            openOfficeDoc(mTabTag, true);
                        } else {
                            openEditUriFile(mTabTag);
                        }
                        break;
                    case BaseFileInfo.FILE_TYPE_OFFICE_URI:
                        openOfficeDoc(mTabTag, true);
                        break;
                    default:
                        handled = false;
                }
            }
            if (!handled) {
                mDocumentLoading = false;
            }
        }

        if (mColorModeChanged) {
            mColorModeChanged = false;
            updateColorMode();
        }

        if (mScreenshotTempFileCreated) {
            try {
                File oldFile = new File(mScreenshotTempFilePath);
                oldFile.delete();
            } catch (Exception e) {
                AnalyticsHandlerAdapter.getInstance().sendException(e);
            } finally {
                mScreenshotTempFileCreated = false;
                mScreenshotTempFilePath = null;
            }
        }

        if (sDebug)
            Log.d("timing", "resumeFragment end");
    }

    protected void pauseFragment() {
        Activity activity = getActivity();
        if (activity == null || mPdfViewCtrl == null) {
            return;
        }

        stopHandlers();

        if (mDocumentConversion != null) {
            cancelUniversalConversion();
        }

        updateRecentList();
        if (mViewerConfig != null) {
            // remember last opened URL page
            ViewerUtils.setLastPageForURL(activity, mOpenUrlLink, mPdfViewCtrl.getCurrentPage());
        }

        if (mDownloadDocumentDialog != null && mDownloadDocumentDialog.isShowing()) {
            mDownloadDocumentDialog.dismiss();
        }

        if (mGetTextInPageTask != null && mGetTextInPageTask.getStatus() != AsyncTask.Status.FINISHED) {
            mGetTextInPageTask.cancel(true);
            mGetTextInPageTask = null;
        }

        if (mPDFDocLoaderTask != null && mPDFDocLoaderTask.getStatus() != AsyncTask.Status.FINISHED) {
            mPDFDocLoaderTask.cancel(true);
            mPDFDocLoaderTask = null;
        }

        // always force to save when switching tabs
        // since we cancel rendering onPause, this should be quick enough to obtain a write lock
        showDocumentSavedToast();
        save(false, true, true, true); // skip showing the message as it is confusing as the current tab is going away

        saveCurrentPdfViewCtrlState();

        if (mPdfViewCtrl != null) {
            mPdfViewCtrl.closeTool();
            mPdfViewCtrl.pause();
            mPdfViewCtrl.purgeMemory();
        }

        closeKeyboard();
        mDocumentLoading = false;

        if (mTabListener != null) {
            mTabListener.onTabPaused(getCurrentFileInfo(), isDocModifiedAfterOpening());
        }
    }

    protected void closeKeyboard() {
        Activity activity = getActivity();
        if (activity == null) {
            return;
        }
        if (mPasswordLayout != null && mPasswordLayout.getVisibility() == View.VISIBLE) {
            Utils.hideSoftKeyboard(activity, mPasswordLayout);
        }
    }

    protected void setViewerHostVisible(boolean visible) {
        if (mPdfViewCtrl == null) {
            return;
        }
        if (visible) {
            mPdfViewCtrl.setVisibility(View.VISIBLE);
            if (sDebug)
                Log.d(TAG, "show viewer");
        } else {
            mPdfViewCtrl.setVisibility(View.INVISIBLE);
            if (sDebug)
                Log.d(TAG, "hide viewer");
        }
    }

    protected void toggleViewerVisibility(boolean visible) {
        if (mIsReflowMode) {
            return;
        }
        setViewerHostVisible(visible);
        if (mProgressBarLayout != null) {
            if (visible) {
                mProgressBarLayout.hide(false);
                if (sDebug)
                    Log.d(TAG, "hide progress bar");
            } else {
                mProgressBarLayout.show();
                if (sDebug)
                    Log.d(TAG, "show progress bar");
            }
        }
    }

    private void createRetrieveChangesDialog(final String cacheFileName) {
        Context context = getContext();
        if (context == null) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(R.string.freetext_restore_cache_message)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Context context = getContext();
                        if (context == null) {
                            return;
                        }
                        JSONObject obj = Utils.retrieveToolCache(context, cacheFileName);
                        if (null == obj || mPdfViewCtrl == null) {
                            return;
                        }

                        mToolManager.setTool(mToolManager.createTool(ToolMode.TEXT_CREATE, null));
                        try {
                            int page = obj.getInt(FreeTextCacheStruct.PAGE_NUM);
                            if (mPdfViewCtrl.getCurrentPage() != page) {
                                if (sDebug)
                                    Log.d(TAG, "restoreFreeText mWaitingForSetPage: " + page);
                                mPdfViewCtrl.setCurrentPage(page);
                                mWaitingForSetPage = true;
                                mWaitingForSetPageNum = page;
                            } else {
                                restoreFreeText();
                            }
                        } catch (JSONException e) {
                            AnalyticsHandlerAdapter.getInstance().sendException(e);
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Context context = getContext();
                        if (context == null) {
                            return;
                        }
                        if (sDebug)
                            Log.d(TAG, "cancel");
                        Utils.deleteCacheFile(context, cacheFileName);
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void restoreFreeText() {
        if (mPdfViewCtrl == null || mToolManager == null) {
            return;
        }

        mToolManager.setTool(mToolManager.createTool(ToolMode.TEXT_CREATE, null));
        String freeTextCacheFilename = mToolManager.getFreeTextCacheFileName();
        JSONObject obj = Utils.retrieveToolCache(getContext(), freeTextCacheFilename);
        if (obj != null) {
            try {
                JSONObject pointObj = obj.getJSONObject(FreeTextCacheStruct.TARGET_POINT);
                int x = pointObj.getInt(FreeTextCacheStruct.X);
                int y = pointObj.getInt(FreeTextCacheStruct.Y);
                MotionEvent event = MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, x, y, 0);
                mPdfViewCtrl.dispatchTouchEvent(event);
                event = MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, x, y, 0);
                mPdfViewCtrl.dispatchTouchEvent(event);
            } catch (JSONException e) {
                AnalyticsHandlerAdapter.getInstance().sendException(e);
            }
        }
    }

    /**
     * Sets if the file an be added to tab info.
     *
     * @param enabled True if enabled
     */
    @SuppressWarnings("SameParameterValue")
    public void setCanAddToTabInfo(boolean enabled) {
        mCanAddToTabInfo = enabled;
    }

    /**
     * Returns the path of the document
     *
     * @return the path of the document
     */
    public String getFilePath() {
        if (getFile() != null) {
            return getFile().getAbsolutePath();
        }
        if (getUriFile() != null) {
            return getUriFile().getPath();
        }
        return null;
    }

    /**
     * Checks whether the document modified after opening.
     *
     * @return true if the document is modified after opening; false otherwise
     */
    public boolean isDocModifiedAfterOpening() {
        return Utils.isDocModified(mPdfDoc) || mDocumentState == PdfDocManager.DOCUMENT_STATE_NORMAL;
    }

    /**
     * Sets search mode
     *
     * @param enabled true if search mode is enabled; false otherwise.
     */
    public void setSearchMode(boolean enabled) {
        mInSearchMode = enabled;
    }

    /**
     * Checks whether it is in search mode.
     *
     * @return true if in search mode; false otherwise
     */
    public boolean isSearchMode() {
        return mInSearchMode;
    }

    /**
     * Undo the last modification.
     */
    public void undo() {
        undo(true);
    }

    /**
     * Undo the last modification.
     *
     * @param sendAnalytics Whether it sends data to analytics
     */
    protected void undo(boolean sendAnalytics) {
        Activity activity = getActivity();
        if (activity == null || mPdfViewCtrl == null || mToolManager == null) {
            return;
        }

        UndoRedoManager undoRedoManager = mToolManager.getUndoRedoManger();
        if (undoRedoManager != null && undoRedoManager.canUndo()) {
            String undoInfo = undoRedoManager.undo(AnalyticsHandlerAdapter.LOCATION_VIEWER, sendAnalytics);
            UndoRedoManager.jumpToUndoRedo(mPdfViewCtrl, undoInfo, true);
            refreshPageCount();

            // if we are in handler tools, should go back to default tool
            if (Utils.isAnnotationHandlerToolMode(ToolManager.getDefaultToolMode(mToolManager.getTool().getToolMode()))) {
                mToolManager.backToDefaultTool();
            }
        }
    }

    /**
     * Redo the last undo operation.
     */
    public void redo() {
        redo(true);
    }

    /**
     * Redo the last undo operation.
     *
     * @param sendAnalytics Whether it sends data to analytics
     */
    protected void redo(boolean sendAnalytics) {
        Activity activity = getActivity();
        if (activity == null || mPdfViewCtrl == null || mToolManager == null) {
            return;
        }

        UndoRedoManager undoRedoManager = mToolManager.getUndoRedoManger();
        if (undoRedoManager != null && undoRedoManager.canRedo()) {
            String redoInfo = undoRedoManager.redo(AnalyticsHandlerAdapter.LOCATION_VIEWER, sendAnalytics);
            UndoRedoManager.jumpToUndoRedo(mPdfViewCtrl, redoInfo, false);
            refreshPageCount();

            // if we are in handler tools, should go back to default tool
            if (Utils.isAnnotationHandlerToolMode(ToolManager.getDefaultToolMode(mToolManager.getTool().getToolMode()))) {
                mToolManager.backToDefaultTool();
            }
        }
    }

    /**
     * Confirms that document saved toast has been shown.
     */
    public void setSavedAndClosedShown() {
        mWasSavedAndClosedShown = true;
    }

    /**
     * Sets the which tab in bookmarks dialog should be selected.
     *
     * @param index The index of tab
     */
    public void setBookmarkDialogCurrentTab(int index) {
        mBookmarkDialogCurrentTab = index;
    }

    /**
     * Returns which tab in bookmark dialog is selected.
     *
     * @return The index of tab
     */
    public int getBookmarkDialogCurrentTab() {
        return mBookmarkDialogCurrentTab;
    }

    /**
     * Checks tab conversion and shows the alert.
     *
     * @param messageID            The message ID
     * @param allowConverted       True if conversion is allowed
     * @param skipSpecialFileCheck True if spcecial files should be skipped
     * @return True if handled
     */
    protected boolean checkTabConversionAndAlert(int messageID, boolean allowConverted, boolean skipSpecialFileCheck) {
        Activity activity = getActivity();
        if (activity == null) {
            return false;
        }

        localFileWriteAccessCheck();
        if (isTabReadOnly()) {
            if (canDocBeSaved() && allowConverted)
                return false;

            if (canDocBeSaved()) {
                if (!isNotPdf() && skipSpecialFileCheck) {
                    return false;
                } else {
                    handleSpecialFile();
                }
            } else {
                if (getHasWarnedAboutCanNotEditDuringConversion()) {
                    CommonToast.showText(activity, messageID);
                } else {
                    setHasWarnedAboutCanNotEditDuringConversion();
                    Utils.getAlertDialogNoTitleBuilder(activity, messageID)
                            .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .setCancelable(false)
                            .create().show();
                }
            }

            return true;
        }

        return false;
    }

    protected void handleRageScrolling() {
        final Activity activity = getActivity();
        if (activity == null || mPdfViewCtrl == null) {
            return;
        }
        if (!PdfViewCtrlSettingsManager.getShowRageScrollingInfo(activity)) {
            return;
        }
        if (mViewerConfig != null) {
            // don't show rage scroll dialog for document viewer
            return;
        }
        if (mRageScrollingAsked) {
            return;
        }

        // detect which direction should we get user to scroll
        int titleRes = isContinuousPageMode() ? R.string.rage_scrolling_horizontal_title : R.string.rage_scrolling_title;
        int posRes = isContinuousPageMode() ? R.string.rage_scrolling_horizontal_positive : R.string.rage_scrolling_positive;

        mRageScrollingAsked = true;
        LayoutInflater inflater = LayoutInflater.from(activity);
        View customLayout = inflater.inflate(R.layout.alert_dialog_with_checkbox, null);
        final TextView dialogTextView = customLayout.findViewById(R.id.dialog_message);

        final CheckBox dialogCheckBox = customLayout.findViewById(R.id.dialog_checkbox);
        dialogCheckBox.setChecked(true);

        if (Utils.isTablet(activity)) {
            int pixelSize = (int) Utils.convDp2Pix(activity, 24);
            String rawContent = getString(R.string.rage_scrolling_body);
            SpannableString content = new SpannableString(rawContent);
            Drawable drawable = getResources().getDrawable(R.drawable.ic_viewing_mode_white_24dp);
            drawable.mutate().setColorFilter(getResources().getColor(R.color.gray600), PorterDuff.Mode.SRC_IN);
            drawable.setBounds(0, 0, pixelSize, pixelSize);
            ImageSpan span = new ImageSpan(drawable, ImageSpan.ALIGN_BOTTOM);
            String placeholder = "[gear]";
            int start = rawContent.indexOf(placeholder);
            if (start >= 0) {
                content.setSpan(span, start, start + placeholder.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            }
            dialogTextView.setText(content);
        } else {
            String content = getString(R.string.rage_scrolling_body_phone, getString(R.string.action_view_mode));
            dialogTextView.setText(content);
        }

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity)
                .setView(customLayout)
                .setTitle(titleRes)
                .setPositiveButton(posRes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        boolean showAgain = !dialogCheckBox.isChecked();

                        AnalyticsHandlerAdapter.getInstance().sendEvent(
                                AnalyticsHandlerAdapter.EVENT_VIEWER_RAGE_SCROLLING,
                                AnalyticsParam.rageScrollingParam("switch", dialogCheckBox.isChecked()));

                        PdfViewCtrlSettingsManager.updateShowRageScrollingInfo(activity, showAgain);

                        if (mPdfViewCtrl != null) {
                            PDFViewCtrl.PagePresentationMode mode = mPdfViewCtrl.getPagePresentationMode();
                            if (mode == PDFViewCtrl.PagePresentationMode.SINGLE) {
                                updateViewMode(PDFViewCtrl.PagePresentationMode.SINGLE_CONT);
                            } else if (mode == PDFViewCtrl.PagePresentationMode.FACING) {
                                updateViewMode(PDFViewCtrl.PagePresentationMode.FACING_CONT);
                            } else if (mode == PDFViewCtrl.PagePresentationMode.FACING_COVER) {
                                updateViewMode(PDFViewCtrl.PagePresentationMode.FACING_COVER_CONT);
                            } else if (mode == PDFViewCtrl.PagePresentationMode.SINGLE_CONT) {
                                updateViewMode(PDFViewCtrl.PagePresentationMode.SINGLE);
                            } else if (mode == PDFViewCtrl.PagePresentationMode.FACING_CONT) {
                                updateViewMode(PDFViewCtrl.PagePresentationMode.FACING);
                            } else if (mode == PDFViewCtrl.PagePresentationMode.FACING_COVER_CONT) {
                                updateViewMode(PDFViewCtrl.PagePresentationMode.FACING_COVER);
                            }
                        }
                    }
                }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        boolean showAgain = !dialogCheckBox.isChecked();
                        PdfViewCtrlSettingsManager.updateShowRageScrollingInfo(activity, showAgain);

                        AnalyticsHandlerAdapter.getInstance().sendEvent(
                                AnalyticsHandlerAdapter.EVENT_VIEWER_RAGE_SCROLLING,
                                AnalyticsParam.rageScrollingParam("cancel", dialogCheckBox.isChecked()));
                    }
                });
        dialogBuilder.create().show();
    }

    private void initRecentlyUsedCache() {
        if (!RecentlyUsedCache.hasBeenInitialized()) {
            try {
                RecentlyUsedCache.initializeRecentlyUsedCache(RecentFilesManager.MAX_NUM_RECENT_FILES, 10 * 1024 * 1024, 0.1);
            } catch (PDFNetException e) {
                e.printStackTrace();
            }
        }
    }

    public static void setDebug(boolean debug) {
        sDebug = debug;
    }

    private void showDownloadDialog() {
        if (mDownloadDocumentDialog != null && (mViewerConfig == null || mViewerConfig.showDownloadDialog())) {
            mDownloadDocumentDialog.show();
        }
    }

    /**
     * Returns the live data containing bookmarks in the document.
     */
    @Nullable
    public LiveData<BookmarksCache> getBookmarks() {
        if (mBookmarksViewModel != null) {
            return mBookmarksViewModel.getBookmarks();
        }
        return null;
    }

    /**
     * Returns the live data containing the current page state.
     */
    @Nullable
    public MutableLiveData<PageState> getPageChange() {
        if (mPageViewModel != null) {
            return mPageViewModel.getPageState();
        }
        return null;
    }

    /**
     * Reloads the bookmarks from PDFViewCtrl
     */
    private void loadBookmarks() {
        if (mBookmarksViewModel != null) {
            mBookmarksViewModel.loadBookmarks(mPdfViewCtrl, isTabReadOnly());
        }
    }

    private void updateAnnotSnappingManager() {
        if (mToolManager != null) {
            mToolManager.getAnnotSnappingManager().clearCache();
            mToolManager.getAnnotSnappingManager().tryUpdateCache(mPdfViewCtrl, false);
        }
    }

    private boolean handlePasswordInvalidEvent() {
        boolean handled = false;
        if (mPasswordProtectedListeners != null) {
            for (PasswordProtectedListener listener : mPasswordProtectedListeners) {
                handled = handled || listener.onPasswordInvalid();
            }
        }
        return handled;
    }
}
