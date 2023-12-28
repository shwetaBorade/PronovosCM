//---------------------------------------------------------------------------------------
// Copyright (c) 2001-2019 by PDFTron Systems Inc. All Rights Reserved.
// Consult legal.txt regarding legal and license information.
//---------------------------------------------------------------------------------------

package com.pdftron.pdf.tools;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import androidx.annotation.StringRes;
import androidx.appcompat.content.res.AppCompatResources;

import com.pdftron.pdf.PDFViewCtrl;
import com.pdftron.pdf.utils.Utils;

/**
 * The DialogAnnotNote is the super class for all annotation dialogs.
 * Annotation dialogs are used to add free text, sticky notes or
 * a note to an existing annotation.
 */
public class DialogAnnotNote extends AlertDialog {

    /**
     * Callback interface to be invoked when a button is pressed.
     */
    public interface DialogAnnotNoteListener {
        /**
         * Called when a button in the dialog has been pressed.
         *
         * @param button The button ID. See {@link android.content.DialogInterface}
         */
        void onAnnotButtonPressed(int button);
    }

    private static final int MAX_ALLOWED_SYSTEM_BAR_SIZE = 256;
    private static final int MIN_ALLOWED_SYSTEM_BAR_SIZE = 5;

    View mMainView;
    EditText mTextBox;
    Button mPositiveButton;
    private Button mNegativeButton;

    @StringRes
    private int mPositiveButtonRes;
    @StringRes
    private int mNegativeButtonRes;

    private DialogAnnotNoteListener mListener;

    GestureDetector mGestureDetector;

    boolean mInEditMode;

    private String mOriginalNote;

    @Nullable
    protected PDFViewCtrl mPdfViewCtrl;
    protected boolean mHasPermission = true;

    Theme mDialogAnnotNoteTheme;

    // Enables positive button when text has been changed
    public TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            mPositiveButton.setEnabled(true);
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    public DialogAnnotNote(@NonNull PDFViewCtrl pdfViewCtrl, String note) {
        this(pdfViewCtrl, note, true);
    }

    /**
     * Class constructor
     */
    @SuppressWarnings("WeakerAccess")
    public DialogAnnotNote(@NonNull PDFViewCtrl pdfViewCtrl, String note, boolean hasPermission) {
        super(pdfViewCtrl.getContext());
        init(pdfViewCtrl, note, hasPermission);
    }

    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    public DialogAnnotNote(@NonNull Context context, String note, boolean hasPermission) {
        super(context);
        init(null, note, hasPermission);
    }

    protected void init(@Nullable PDFViewCtrl pdfViewCtrl, String note, boolean hasPermission) {
        Context context = getContext();
        if (note == null) {
            note = "";
        }
        mDialogAnnotNoteTheme = Theme.fromContext(context);
        mOriginalNote = note;
        mHasPermission = hasPermission;

        mInEditMode = mHasPermission;
        mMainView = LayoutInflater.from(context).inflate(R.layout.tools_dialog_annotation_popup_text_input, null);

        // EditText
        mTextBox = mMainView.findViewById(R.id.tools_dialog_annotation_popup_edittext);

        // Positive Button
        mPositiveButton = mMainView.findViewById(R.id.tools_dialog_annotation_popup_button_positive);
        mPositiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setButtonPressed(BUTTON_POSITIVE);
            }
        });

        // Negative Button
        mNegativeButton = mMainView.findViewById(R.id.tools_dialog_annotation_popup_button_negative);
        mNegativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setButtonPressed(BUTTON_NEGATIVE);
            }
        });
        if (!mHasPermission) {
            mNegativeButton.setVisibility(View.INVISIBLE);
        }

        // Show keyboard and set note to automatically readjust its size based off of the soft
        // input size
        if (mHasPermission) {
            Utils.showSoftKeyboard(context, mTextBox);
            if (getWindow() != null) {
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
            }
        }

        // Set view of dialog
        setView(mMainView);
        setCanceledOnTouchOutside(false);

        // create gesture detector
        mGestureDetector = new GestureDetector(context, new MyGestureDetector());
        mPdfViewCtrl = pdfViewCtrl;

        // Set theme colors
        View buttonBar = mMainView.findViewById(R.id.tools_dialog_annotation_popup_button_bar);
        buttonBar.setBackgroundColor(mDialogAnnotNoteTheme.backgroundColor);
        mTextBox.setBackgroundColor(mDialogAnnotNoteTheme.editTextBackgroundColor);
        mPositiveButton.setTextColor(mDialogAnnotNoteTheme.textButtonColor);
        mNegativeButton.setTextColor(mDialogAnnotNoteTheme.textButtonColor);
    }

    @Override
    public void dismiss() {
        Utils.hideSoftKeyboard(getContext(), mMainView);
        super.dismiss();
    }

    /**
     * Sets the {@link DialogAnnotNoteListener} listener.
     *
     * @param listener The listener
     */
    @SuppressWarnings("WeakerAccess")
    public void setAnnotNoteListener(DialogAnnotNoteListener listener) {
        mListener = listener;
    }

    /**
     * @return The content note
     */
    public String getNote() {
        return mTextBox.getText().toString();
    }

    /**
     * @return True if in edit mode
     */
    @SuppressWarnings("WeakerAccess")
    public boolean isEditEnabled() {
        return mInEditMode;
    }

    /**
     * Handles when a button is pressed.
     *
     * @param button The button index
     */
    @SuppressWarnings("WeakerAccess")
    public void setButtonPressed(int button) {
        if (mListener != null) {
            mListener.onAnnotButtonPressed(button);
        }
        dismiss();
    }

    public void setPositiveButtonRes(@StringRes int positiveButtonRes) {
        mPositiveButtonRes = positiveButtonRes;
        mPositiveButton.setText(positiveButtonRes);
    }

    public void setNegativeButtonRes(@StringRes int negativeButtonRes) {
        mNegativeButtonRes = negativeButtonRes;
        mNegativeButton.setText(negativeButtonRes);
    }

    /**
     * Switches to view mode
     */
    @SuppressWarnings("WeakerAccess")
    public void switchToViewMode() {
        // in view mode
        mInEditMode = false;

        // disable editing in TextBox
        mTextBox.setFocusable(false);
        mTextBox.setFocusableInTouchMode(false);
        mTextBox.setLongClickable(false);
        mTextBox.setCursorVisible(false);
        mTextBox.setSelection(0);

        // set positive and negative buttons names
        int posRes = mPositiveButtonRes == 0 ? R.string.tools_misc_close : mPositiveButtonRes;
        mPositiveButton.setText(getContext().getString(posRes));
        int negRes = mNegativeButtonRes == 0 ? R.string.delete : mNegativeButtonRes;
        mNegativeButton.setText(getContext().getString(negRes));

        // change hint
        if (mHasPermission) {
            mTextBox.setHint(getContext().getString(R.string.tools_dialog_annotation_popup_view_mode_hint));
        } else {
            mTextBox.setHint(getContext().getString(R.string.tools_dialog_annotation_popup_readonly_hint));
        }

        // add gesture detector to text box to detect when user taps text box
        mTextBox.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return mGestureDetector.onTouchEvent(event);
            }
        });
    }

    /*
     * Class for custom gesture detection:
     *  - When in view mode, tapping on the edit text turns the note
     *  into edit mode
     */
    class MyGestureDetector extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            if (!mInEditMode && mHasPermission) {
                setWrapNoteHeight();

                // enable editing in TextBox
                mTextBox.setFocusableInTouchMode(true);
                mTextBox.setFocusable(true);
                mTextBox.requestFocus();
                mTextBox.setCursorVisible(true);
                mTextBox.setSelection(mTextBox.getText().length());
                mTextBox.setLongClickable(true);

                // change positive and negative button names
                mPositiveButton.setText(getContext().getString(R.string.tools_misc_save));
                mNegativeButton.setText(getContext().getString(R.string.cancel));

                mTextBox.addTextChangedListener(textWatcher);
                mPositiveButton.setEnabled(false);

                // show keyboard
                Utils.showSoftKeyboard(getContext(), mTextBox);

                //change hint
                mTextBox.setHint(getContext().getString(R.string.tools_dialog_annotation_popup_note_hint));

                // now in edit mode
                mInEditMode = true;
                return true;
            }
            return false;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return false;
        }
    }

    private void setWrapNoteHeight() {
        Window window = getWindow();
        if (window != null) {
            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(window.getAttributes());
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(lp);
        }
    }

    // TODO: Try to remove status bar and nav bar calculations.
    private void setHeight() {
        // get screen's height and width
        DisplayMetrics metrics = getContext().getResources().getDisplayMetrics();
        int screenHeight = metrics.heightPixels;
        int screenWidth = metrics.widthPixels;

        // necessary assumption: height > width, so switch if in landscape orientation
        if (getContext().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            //noinspection SuspiciousNameCombination
            screenWidth = screenHeight;
        }

        Activity activity = getOwnerActivity();
        int statusBarHeight = 0;
        int navigationBarHeight = 0;
        if (activity == null) {
            int resourceId = getContext().getResources().getIdentifier("status_bar_height", "dimen", "android");
            if (resourceId > 0) {
                statusBarHeight = getContext().getResources().getDimensionPixelSize(resourceId);
            }
            resourceId = getContext().getResources().getIdentifier("navigation_bar_height", "dimen", "android");
            if (resourceId > 0) {
                navigationBarHeight = getContext().getResources().getDimensionPixelSize(resourceId);
            }
        } else {
            statusBarHeight = getStatusBarHeight(activity);
            navigationBarHeight = getNavigationBarHeight(activity);
        }

        int windowWidth = (int) ((screenWidth - statusBarHeight - navigationBarHeight) * 0.9f);

        // initialize note height:
        // The maximum note height is the window width. If it's a tablet,
        // set the note height as 50% of window width. Otherwise, set as
        // maximum set note height of window width.
        int noteHeight;
        double reduceFactor = 0.5;
        if (Utils.isTablet(getContext())) {
            noteHeight = (int) (windowWidth * reduceFactor);
        } else {
            //noinspection SuspiciousNameCombination
            noteHeight = windowWidth;
        }
        setNoteHeight(noteHeight);
        increaseNoteHeight(reduceFactor, windowWidth);
    }

    private void increaseNoteHeight(final double factor, final int windowWidth) {
        // Check if text is not all visible in initial note height
        mTextBox.post(new Runnable() {
            @Override
            public void run() {
                // number of lines text currently takes up
                int numTextLines = mTextBox.getLineCount();

                // number of lines text box can display
                int padding = mTextBox.getPaddingTop() + mTextBox.getPaddingBottom();
                int TextBoxSize = mTextBox.getHeight() - padding;
                int lineHeight = mTextBox.getLineHeight();
                int numLinesInBox = TextBoxSize / lineHeight;

                // if number of lines of text is greater than can be displayed
                // expand size of text box
                if (numTextLines > numLinesInBox) {
                    if (Utils.isTablet(getContext())) {
                        if (factor >= 1) {
                            setWrapNoteHeight();
                        } else {
                            double newFactor = factor + 0.25f;
                            int noteHeight = (int) (windowWidth * (newFactor));
                            setNoteHeight(noteHeight);
                            increaseNoteHeight(newFactor, windowWidth);
                        }
                    } else {
                        setWrapNoteHeight();
                    }
                }
            }
        });
    }

    private void setNoteHeight(int noteHeight) {
        // set note height only if height is small enough
        Window window = getWindow();
        if (window != null) {
            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(window.getAttributes());
            lp.height = noteHeight;
            window.setAttributes(lp);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        initTextBox(mOriginalNote);

        if (!mInEditMode) {
            setHeight();
        }
    }

    /**
     * Initializes the text box with the specified note.
     *
     * @param note The note
     */
    public void initTextBox(String note) {
        if (!note.equals("") || !mHasPermission) {
            String normalized = note.replaceAll("\\r\\n?", "\n");
            mTextBox.setText(normalized);
            // Set the caret position to the end of the text
            mTextBox.setSelection(mTextBox.getText().length());
            switchToViewMode();
        } else {
            Utils.showSoftKeyboard(getContext(), mTextBox);
        }

        // Enable save button only when text has been added
        if (mInEditMode) {
            mTextBox.addTextChangedListener(textWatcher);
            mPositiveButton.setEnabled(false);
        }
    }

    @SuppressLint("NewApi")
    private static int getNavigationBarHeight(@NonNull Activity activity) {
        // NOTE displayInfo.appWidth/appHeight comes from PWM.getNonDecorDisplayWidth/Height,
        // which includes the nav bar. So, don't need to subtract status bar height.
        int height = Utils.getRealScreenHeight(activity) - Utils.getScreenHeight(activity);
        if (height > MAX_ALLOWED_SYSTEM_BAR_SIZE) {
            // it could be in split-screen mode
            height = 0;
        }
        if (height < MIN_ALLOWED_SYSTEM_BAR_SIZE) {
            // it could be in split-screen mode
            height = 0;
        }
        int width = Utils.getRealScreenWidth(activity) - Utils.getScreenWidth(activity);
        if (width > MAX_ALLOWED_SYSTEM_BAR_SIZE) {
            // it could be in split-screen mode
            height = 0;
        }

        return height;
    }

    @SuppressLint("NewApi")
    private static int getStatusBarHeight(@NonNull Activity activity) {
        android.graphics.Rect rectangle = new android.graphics.Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(rectangle);

        if (rectangle.top > MAX_ALLOWED_SYSTEM_BAR_SIZE) {
            // it could be in split-screen mode
            rectangle.top = 0;
        } else if (rectangle.top == 0) {
            // sometimes, during screen rotation, the window visible display doesn't contain the
            // status bar. If this is the case, we obtain the status bar height using another approach
            int resourceId = activity.getResources().getIdentifier("status_bar_height", "dimen", "android");
            if (resourceId > 0) {
                rectangle.top = activity.getResources().getDimensionPixelSize(resourceId);
            }
        } else if (rectangle.top < MIN_ALLOWED_SYSTEM_BAR_SIZE && rectangle.top > 0) {
            // it could be in split-screen mode
            rectangle.top = 0;
        }

        return rectangle.top;
    }

    static class Theme {
        @ColorInt
        public final int backgroundColor;
        @ColorInt
        public final int editTextBackgroundColor;
        public final ColorStateList textButtonColor;

        Theme(int backgroundColor, int editTextBackgroundColor, ColorStateList textButtonColor) {
            this.backgroundColor = backgroundColor;
            this.editTextBackgroundColor = editTextBackgroundColor;
            this.textButtonColor = textButtonColor;
        }

        static Theme fromContext(@NonNull Context context) {

            final TypedArray a = context.obtainStyledAttributes(
                    null, R.styleable.DialogAnnotNote, R.attr.pt_annot_note_dialog_style, R.style.DialogAnnotNoteStyle);
            int backgroundColor = a.getColor(R.styleable.DialogAnnotNote_backgroundColor, context.getResources().getColor(R.color.tools_dialog_annotation_popup_textbox_background));
            int editTextBackgroundColor = a.getColor(R.styleable.DialogAnnotNote_editTextBackgroundColor, context.getResources().getColor(R.color.tools_dialog_annotation_popup_background));

            // We need to programmatically get the state list via resource id, since attributes declared
            // in selectors are not supported on Lollipop. Using AppCompatResources.getColorStateList
            // seems to work.
            int textButtonColorRes = a.getResourceId(R.styleable.DialogAnnotNote_textButtonColor, R.color.button_dialog_annotation_note_color);
            ColorStateList textButtonColor = AppCompatResources.getColorStateList(context, textButtonColorRes);
            a.recycle();

            return new Theme(backgroundColor, editTextBackgroundColor, textButtonColor);
        }
    }
}
