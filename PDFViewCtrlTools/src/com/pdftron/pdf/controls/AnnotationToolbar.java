//---------------------------------------------------------------------------------------
// Copyright (c) 2001-2019 by PDFTron Systems Inc. All Rights Reserved.
// Consult legal.txt regarding legal and license information.
//---------------------------------------------------------------------------------------

package com.pdftron.pdf.controls;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.SparseIntArray;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.FragmentActivity;
import androidx.transition.ChangeBounds;
import androidx.transition.Fade;
import androidx.transition.Slide;
import androidx.transition.Transition;
import androidx.transition.TransitionManager;
import androidx.transition.TransitionSet;

import com.pdftron.pdf.Annot;
import com.pdftron.pdf.PDFViewCtrl;
import com.pdftron.pdf.config.ToolConfig;
import com.pdftron.pdf.config.ToolStyleConfig;
import com.pdftron.pdf.controls.UndoRedoPopupWindow.OnUndoRedoListener;
import com.pdftron.pdf.model.AnnotStyle;
import com.pdftron.pdf.model.GroupedItem;
import com.pdftron.pdf.tools.AdvancedShapeCreate;
import com.pdftron.pdf.tools.AnnotEditRectGroup;
import com.pdftron.pdf.tools.FreehandCreate;
import com.pdftron.pdf.tools.Pan;
import com.pdftron.pdf.tools.R;
import com.pdftron.pdf.tools.Tool;
import com.pdftron.pdf.tools.ToolManager;
import com.pdftron.pdf.tools.ToolManager.ToolMode;
import com.pdftron.pdf.utils.AnalyticsHandlerAdapter;
import com.pdftron.pdf.utils.AnalyticsParam;
import com.pdftron.pdf.utils.PdfViewCtrlSettingsManager;
import com.pdftron.pdf.utils.ShortcutHelper;
import com.pdftron.pdf.utils.StampStatePopup;
import com.pdftron.pdf.utils.Utils;
import com.pdftron.pdf.utils.ViewerUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static com.pdftron.pdf.tools.ToolManager.ToolMode.INK_CREATE;
import static com.pdftron.pdf.tools.ToolManager.ToolMode.RUBBER_STAMPER;
import static com.pdftron.pdf.tools.ToolManager.ToolMode.SIGNATURE;
import static com.pdftron.pdf.tools.ToolManager.ToolMode.STAMPER;
import static com.pdftron.pdf.utils.StampStatePopup.STATE_IMAGE_STAMP;
import static com.pdftron.pdf.utils.StampStatePopup.STATE_RUBBER_STAMP;
import static com.pdftron.pdf.utils.StampStatePopup.STATE_SIGNATURE;

/**
 * @deprecated use {@link com.pdftron.pdf.widget.toolbar.component.AnnotationToolbarComponent} instead
 * 
 * The AnnotationToolbar works with a {@link com.pdftron.pdf.tools.ToolManager} to
 * allow quick selection of different tools. The toolbar shows a list of buttons
 * which prompts the associated ToolManager to switch to that tool.
 * If undo/redo is enabled in the PDFViewCtrl the undo/redo buttons are also provided.
 */
@Deprecated
public class AnnotationToolbar extends BaseToolbar implements
        ToolManager.ToolChangedListener,
        EditToolbarImpl.OnEditToolbarListener,
        AdvancedShapeCreate.OnEditToolbarListener,
        FormToolbar.FormToolbarListener {

    /**
     * Starts with regular annotation toolbar
     */
    public static final int START_MODE_NORMAL_TOOLBAR = 0;
    /**
     * Starts with edit toolbar
     */
    public static final int START_MODE_EDIT_TOOLBAR = 1;

    public static final int START_MODE_FORM_TOOLBAR = 2;

    public static final int START_MODE_FILL_AND_SIGN_TOOLBAR = 3;

    public static final String PREF_KEY_NOTE = "pref_note";
    public static final String PREF_KEY_LINE = "pref_line";
    public static final String PREF_KEY_RECT = "pref_rect";
    public static final String PREF_KEY_TEXT = "pref_text";

    private static final int NUM_TABLET_NORMAL_STATE_ICONS = 16;
    private static final int NUM_PHONE_NORMAL_STATE_ICONS = 9;
    private static final int NUM_PHONE_NORMAL_STATE_TOOL_ICONS = 6;
    private static final int START_MODE_UNKNOWN = -1;
    private static final int ANIMATION_DURATION = 250;

    private EditToolbarImpl mEditToolbarImpl;
    private PDFViewCtrl mPdfViewCtrl;
    private AnnotToolbarOverflowPopupWindow mOverflowPopupWindow;
    private SparseIntArray mButtonsVisibility = AnnotationToolbarButtonId.getButtonVisibilityArray();
    private boolean mDismissAfterExitEdit;
    private String mStampState;

    private FormToolbar mFormToolbar;

    private AnnotStyleDialogFragment mAnnotStyleDialog;

    private StampStatePopup mStampStatePopup;
    private boolean mLayoutChanged;
    private boolean mForceUpdateView;

    private AnnotationToolbarListener mAnnotationToolbarListener;
    private OnUndoRedoListener mOnUndoRedoListener;

    private SparseIntArray mButtonAnnotTypeMap;
    private HashMap<String, Integer> mVisibleAnnotTypeMap;
    private boolean mShouldExpand; // should the toolbar be expanded when phone is in portrait mode
    private boolean mIsExpanded; // is the toolbar in expanded mode
    private ArrayList<GroupedItem> mGroupItems;

    /**
     * Class constructor
     */
    public AnnotationToolbar(@NonNull Context context) {
        this(context, null);
    }

    /**
     * Class constructor
     */
    public AnnotationToolbar(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, R.attr.annotation_toolbar);
    }

    /**
     * Class constructor
     */
    public AnnotationToolbar(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr, R.style.AnnotationToolbarStyle);
    }

    /**
     * Class constructor
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public AnnotationToolbar(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init(@NonNull Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {

        // initialize colors
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.AnnotationToolbar, defStyleAttr, defStyleRes);
        try {
          /*  mToolbarBackgroundColor = typedArray.getColor(R.styleable.AnnotationToolbar_colorBackground, Color.BLACK);
            mToolbarToolBackgroundColor = typedArray.getColor(R.styleable.AnnotationToolbar_colorToolBackground, Color.BLACK);
            mToolbarToolIconColor = typedArray.getColor(R.styleable.AnnotationToolbar_colorToolIcon, Color.WHITE);
            mToolbarCloseIconColor = typedArray.getColor(R.styleable.AnnotationToolbar_colorCloseIcon, Color.WHITE);*/

            //TODO 07/15/2021 GWL UPDATE
            mToolbarBackgroundColor = typedArray.getColor(R.styleable.AnnotationToolbar_colorBackground, Color.RED);
            mToolbarToolBackgroundColor = typedArray.getColor(R.styleable.AnnotationToolbar_colorToolBackground, Color.GRAY);
            mToolbarToolIconColor = typedArray.getColor(R.styleable.AnnotationToolbar_colorToolIcon, Color.GRAY);
            mToolbarCloseIconColor = typedArray.getColor(R.styleable.AnnotationToolbar_colorCloseIcon, Color.RED);
        } finally {
            typedArray.recycle();
        }

        // collect annot types
        LayoutInflater.from(context).inflate(R.layout.controls_annotation_toolbar_layout, this, true);
        mButtonAnnotTypeMap = new SparseIntArray();
        mButtonAnnotTypeMap.put(R.id.controls_annotation_toolbar_tool_stickynote, Annot.e_Text);
        mButtonAnnotTypeMap.put(R.id.controls_annotation_toolbar_tool_text_highlight, Annot.e_Highlight);
        mButtonAnnotTypeMap.put(R.id.controls_annotation_toolbar_tool_text_strikeout, Annot.e_StrikeOut);
        mButtonAnnotTypeMap.put(R.id.controls_annotation_toolbar_tool_text_underline, Annot.e_Underline);
        mButtonAnnotTypeMap.put(R.id.controls_annotation_toolbar_tool_text_squiggly, Annot.e_Squiggly);
        mButtonAnnotTypeMap.put(R.id.controls_annotation_toolbar_tool_free_highlighter, AnnotStyle.CUSTOM_ANNOT_TYPE_FREE_HIGHLIGHTER);
        mButtonAnnotTypeMap.put(R.id.controls_annotation_toolbar_tool_stamp, AnnotStyle.CUSTOM_ANNOT_TYPE_SIGNATURE);
        mButtonAnnotTypeMap.put(R.id.controls_annotation_toolbar_tool_freehand, Annot.e_Ink);
        mButtonAnnotTypeMap.put(R.id.controls_annotation_toolbar_tool_eraser, AnnotStyle.CUSTOM_ANNOT_TYPE_ERASER);
        mButtonAnnotTypeMap.put(R.id.controls_annotation_toolbar_tool_freetext, Annot.e_FreeText);
        mButtonAnnotTypeMap.put(R.id.controls_annotation_toolbar_tool_callout, AnnotStyle.CUSTOM_ANNOT_TYPE_CALLOUT);
        mButtonAnnotTypeMap.put(R.id.controls_annotation_toolbar_tool_arrow, AnnotStyle.CUSTOM_ANNOT_TYPE_ARROW);
        mButtonAnnotTypeMap.put(R.id.controls_annotation_toolbar_tool_ruler, AnnotStyle.CUSTOM_ANNOT_TYPE_RULER);
        mButtonAnnotTypeMap.put(R.id.controls_annotation_toolbar_tool_line, Annot.e_Line);
        mButtonAnnotTypeMap.put(R.id.controls_annotation_toolbar_tool_polyline, Annot.e_Polyline);
        mButtonAnnotTypeMap.put(R.id.controls_annotation_toolbar_tool_rectangle, Annot.e_Square);
        mButtonAnnotTypeMap.put(R.id.controls_annotation_toolbar_tool_oval, Annot.e_Circle);
        mButtonAnnotTypeMap.put(R.id.controls_annotation_toolbar_tool_polygon, Annot.e_Polygon);
        mButtonAnnotTypeMap.put(R.id.controls_annotation_toolbar_tool_cloud, AnnotStyle.CUSTOM_ANNOT_TYPE_CLOUD);
        mButtonAnnotTypeMap.put(R.id.controls_annotation_toolbar_tool_sound, Annot.e_Sound);
        mButtonAnnotTypeMap.put(R.id.controls_annotation_toolbar_tool_perimeter_measure, AnnotStyle.CUSTOM_ANNOT_TYPE_PERIMETER_MEASURE);
        mButtonAnnotTypeMap.put(R.id.controls_annotation_toolbar_tool_area_measure, AnnotStyle.CUSTOM_ANNOT_TYPE_AREA_MEASURE);

        // collect visible annot types
        String visibleAnnotTypesJsonStr = PdfViewCtrlSettingsManager.getAnnotToolbarVisibleAnnotTypes(context);
        mVisibleAnnotTypeMap = new HashMap<>();
        if (!Utils.isNullOrEmpty(visibleAnnotTypesJsonStr)) {
            try {
                JSONObject object = new JSONObject(visibleAnnotTypesJsonStr);
                if (object.has(PREF_KEY_LINE)) {
                    mVisibleAnnotTypeMap.put(PREF_KEY_LINE, object.getInt(PREF_KEY_LINE));
                }
                if (object.has(PREF_KEY_RECT)) {
                    mVisibleAnnotTypeMap.put(PREF_KEY_RECT, object.getInt(PREF_KEY_RECT));
                }
                if (object.has(PREF_KEY_TEXT)) {
                    mVisibleAnnotTypeMap.put(PREF_KEY_TEXT, object.getInt(PREF_KEY_TEXT));
                }
                if (object.has(PREF_KEY_NOTE)) {
                    mVisibleAnnotTypeMap.put(PREF_KEY_NOTE, object.getInt(PREF_KEY_NOTE));
                }
            } catch (JSONException e) {
                AnalyticsHandlerAdapter.getInstance().sendException(e);
            }
        }

        // grouped items
        mGroupItems = new ArrayList<>();
        mGroupItems.add(new GroupedItem(this, PREF_KEY_LINE, new int[]{Annot.e_Line, AnnotStyle.CUSTOM_ANNOT_TYPE_ARROW, Annot.e_Polyline, AnnotStyle.CUSTOM_ANNOT_TYPE_RULER, AnnotStyle.CUSTOM_ANNOT_TYPE_PERIMETER_MEASURE}));
        mGroupItems.add(new GroupedItem(this, PREF_KEY_RECT, new int[]{Annot.e_Circle, Annot.e_Square, Annot.e_Polygon, AnnotStyle.CUSTOM_ANNOT_TYPE_CLOUD, AnnotStyle.CUSTOM_ANNOT_TYPE_AREA_MEASURE}));
        mGroupItems.add(new GroupedItem(this, PREF_KEY_TEXT, new int[]{Annot.e_FreeText, AnnotStyle.CUSTOM_ANNOT_TYPE_CALLOUT}));
        mGroupItems.add(new GroupedItem(this, PREF_KEY_NOTE, new int[]{Annot.e_Text, Annot.e_Sound}));
    }

    /**
     * Setups the annotation toolbar window.
     *
     * @param toolManager The toolManager class
     */
    public void setup(@NonNull ToolManager toolManager) {
        setup(toolManager, null);
    }

    /**
     * Setups the annotation toolbar window.
     *
     * @param toolManager The toolManager class
     * @param listener    The listener for undo/redo events
     */
    public void setup(@NonNull ToolManager toolManager,
            @Nullable OnUndoRedoListener listener) {
        Context context = getContext();
        if (context == null) {
            return;
        }

        mToolManager = toolManager;
        mPdfViewCtrl = mToolManager.getPDFViewCtrl();
        mOnUndoRedoListener = listener;

        // init signature state:
        SharedPreferences settings = Tool.getToolPreferences(context);
        mStampState = settings.getString(Tool.ANNOTATION_TOOLBAR_SIGNATURE_STATE, STATE_SIGNATURE);
        if ("stamper".equals(mStampState)) {
            // workaround for mistakenly change the value of STATE_IMAGE_STAMP
            // from "stamper" to "stamp"
            mStampState = STATE_IMAGE_STAMP;
            SharedPreferences.Editor editor = settings.edit();
            editor.putString(Tool.ANNOTATION_TOOLBAR_SIGNATURE_STATE, mStampState);
            editor.apply();
        }
        checkStampState();

        // Configure buttons
        initButtons();

        mToolManager.addToolChangedListener(this);
        // Force tool to be Pan when using the toolbar.
        mToolManager.setTool(mToolManager.createTool(ToolMode.PAN, null));

        initSelectedButton();

        setVisibility(View.GONE);
    }

    private void initViews() {
        Context context = getContext();
        if (context == null) {
            return;
        }

        // initialize the background color of the annotation toolbar:
        setBackgroundColor(mToolbarBackgroundColor);

        // initialize the color of each tool in the annotation toolbar:

        ArrayList<ToolItem> tools = new ArrayList<>();
        // TODO 07/14/2021 GWL Update since this class is depricated we have to find its alternative also
        // tools.add(new ToolItem(Annot.e_Text, R.id.controls_annotation_toolbar_tool_stickynote, true));
        tools.add(new ToolItem(Annot.e_Text, R.id.controls_annotation_toolbar_tool_stickynote, R.drawable.punchlist_drawing, true, mToolbarCloseIconColor));//GWL add punch icon

        tools.add(new ToolItem(Annot.e_Highlight, R.id.controls_annotation_toolbar_tool_text_highlight, true));
        tools.add(new ToolItem(Annot.e_StrikeOut, R.id.controls_annotation_toolbar_tool_text_strikeout, true));
        tools.add(new ToolItem(Annot.e_Underline, R.id.controls_annotation_toolbar_tool_text_underline, true));
        tools.add(new ToolItem(Annot.e_Squiggly, R.id.controls_annotation_toolbar_tool_text_squiggly, true));
        tools.add(new ToolItem(AnnotStyle.CUSTOM_ANNOT_TYPE_FREE_HIGHLIGHTER, R.id.controls_annotation_toolbar_tool_free_highlighter, true));
        tools.add(new ToolItem(AnnotStyle.CUSTOM_ANNOT_TYPE_SIGNATURE, R.id.controls_annotation_toolbar_tool_stamp, !mIsExpanded && getStampsEnabledCount() >= 2));
        tools.add(new ToolItem(Annot.e_Ink, R.id.controls_annotation_toolbar_tool_freehand, false));
        tools.add(new ToolItem(AnnotStyle.CUSTOM_ANNOT_TYPE_ERASER, R.id.controls_annotation_toolbar_tool_eraser, true));
        tools.add(new ToolItem(Annot.e_FreeText, R.id.controls_annotation_toolbar_tool_freetext, true));
        tools.add(new ToolItem(AnnotStyle.CUSTOM_ANNOT_TYPE_CALLOUT, R.id.controls_annotation_toolbar_tool_callout, true));
        tools.add(new ToolItem(-1, R.id.controls_annotation_toolbar_tool_image_stamper, R.drawable.ic_annotation_image_black_24dp, false));
        tools.add(new ToolItem(-1, R.id.controls_annotation_toolbar_tool_rubber_stamper, R.drawable.ic_annotation_stamp_black_24dp, false));
        tools.add(new ToolItem(Annot.e_Line, R.id.controls_annotation_toolbar_tool_line, true));
        tools.add(new ToolItem(AnnotStyle.CUSTOM_ANNOT_TYPE_ARROW, R.id.controls_annotation_toolbar_tool_arrow, true));
        tools.add(new ToolItem(AnnotStyle.CUSTOM_ANNOT_TYPE_RULER, R.id.controls_annotation_toolbar_tool_ruler, true));
        tools.add(new ToolItem(AnnotStyle.CUSTOM_ANNOT_TYPE_PERIMETER_MEASURE, R.id.controls_annotation_toolbar_tool_perimeter_measure, true));
        tools.add(new ToolItem(AnnotStyle.CUSTOM_ANNOT_TYPE_AREA_MEASURE, R.id.controls_annotation_toolbar_tool_area_measure, true));
        tools.add(new ToolItem(Annot.e_Polyline, R.id.controls_annotation_toolbar_tool_polyline, true));
        tools.add(new ToolItem(Annot.e_Square, R.id.controls_annotation_toolbar_tool_rectangle, true));
        tools.add(new ToolItem(Annot.e_Circle, R.id.controls_annotation_toolbar_tool_oval, true));
        tools.add(new ToolItem(Annot.e_Polygon, R.id.controls_annotation_toolbar_tool_polygon, true));
        tools.add(new ToolItem(AnnotStyle.CUSTOM_ANNOT_TYPE_CLOUD, R.id.controls_annotation_toolbar_tool_cloud, true));
        /*if (mToolManager.getMultiSelectMode() == AnnotEditRectGroup.SelectionMode.LASSO) {
            tools.add(new ToolItem(-1, R.id.controls_annotation_toolbar_tool_multi_select, R.drawable.ic_select_lasso, false));
        } else {
            tools.add(new ToolItem(-1, R.id.controls_annotation_toolbar_tool_multi_select, R.drawable.ic_select_rectangular_black_24dp, false));
        }*/
        tools.add(new ToolItem(-1, R.id.controls_annotation_toolbar_tool_pan, R.drawable.ic_pan_black_24dp, false));
        tools.add(new ToolItem(-1, R.id.controls_annotation_toolbar_btn_close, R.drawable.ic_close_black_24dp, false, mToolbarCloseIconColor));
        tools.add(new ToolItem(-1, R.id.controls_annotation_toolbar_btn_more, R.drawable.ic_overflow_white_24dp, false));
        tools.add(new ToolItem(Annot.e_Sound, R.id.controls_annotation_toolbar_tool_sound, R.drawable.ic_mic_black_24dp, true));

        int width = getToolWidth();
        int height = getToolHeight();

        //TODO 07/14/2021 GWL Update
        // initialize the color of each tool in the annotation toolbar:
       /* Drawable spinnerBitmapDrawable = getSpinnerBitmapDrawable(context,
                width, height, mToolbarToolBackgroundColor, mIsExpanded);
        Drawable normalBitmapDrawable = getNormalBitmapDrawable(context,
                width, height, mToolbarToolBackgroundColor, mIsExpanded);

        for (ToolItem tool : tools) {
            setViewDrawable(context, tool.id, tool.spinner, tool.drawable,
                    spinnerBitmapDrawable, normalBitmapDrawable,
                    tool.color);
        }*/

        // initialize the color of each tool in the annotation toolbar:
        int spinnerDrawableId = R.drawable.controls_annotation_toolbar_bg1;
        Drawable spinnerBitmapDrawable = ViewerUtils.getBitmapDrawable(context, spinnerDrawableId,
                width, height, R.color.gray, mIsExpanded);
        Drawable normalBitmapDrawable;
        if (mIsExpanded) {
            normalBitmapDrawable = Utils.getDrawable(context, R.drawable.rounded_corners);
            if (normalBitmapDrawable != null) {
                normalBitmapDrawable = normalBitmapDrawable.mutate();
                normalBitmapDrawable.setColorFilter(mToolbarToolBackgroundColor, PorterDuff.Mode.SRC_ATOP);
            }
            normalBitmapDrawable = ViewerUtils.getBitmapDrawable(context, R.drawable.controls_annotation_toolbar_bg_selected_gray,
                    width, height, R.color.gray , false);
        } else {
            normalBitmapDrawable = ViewerUtils.getBitmapDrawable(context, R.drawable.controls_annotation_toolbar_bg_selected_gray,
                    width, height, R.color.gray, false);
        }

        for (ToolItem tool : tools) {
            View v = findViewById(tool.id);
            if (v != null) {
                //GWL change
                //v.setBackground(ViewerUtils.createBackgroundSelector(tool.spinner ? spinnerBitmapDrawable : normalBitmapDrawable));
                v.setBackground(ViewerUtils.createBackgroundSelector(context.getResources().getDrawable(R.drawable.controls_annotation_toolbar_bg_selected_gray)));
                Drawable drawable = Utils.createImageDrawableSelector(context, tool.drawable, R.color.tools_dialog_floating_sig_signhere_text_color);
//                ((AppCompatImageButton) v).setImageDrawable(drawable);
                ((AppCompatImageButton) findViewById(tool.id)).setImageDrawable(drawable);
            }
        }
        //TODO 07/14/2021 GWL Update End

//        updateStampBtnState();
//        updateStampPopupSize();
    }

    private int getStampsEnabledCount() {

        int stampsEnabledCounts = 0;
        if (!mToolManager.isToolModeDisabled(SIGNATURE)) {
            ++stampsEnabledCounts;
        }
        if (!mToolManager.isToolModeDisabled(STAMPER)) {
            ++stampsEnabledCounts;
        }
        if (!mToolManager.isToolModeDisabled(RUBBER_STAMPER)) {
            ++stampsEnabledCounts;
        }
        return stampsEnabledCounts;
    }

    /**
     * Gets the ToolManager associated with this toolbar
     *
     * @return the ToolManager
     */
    public ToolManager getToolManager() {
        return mToolManager;
    }

    /**
     * Gets the visible annotation type when using grouping
     *
     * @return the map
     */
    public HashMap<String, Integer> getVisibleAnnotTypeMap() {
        return mVisibleAnnotTypeMap;
    }

    /**
     * Gets the annotation type grouping and modify accordingly
     * 1. open shape group: {@link #PREF_KEY_LINE}
     * 2. close shape group: {@link #PREF_KEY_RECT}
     * 3. FreeText group: {@link #PREF_KEY_TEXT}
     * 4. Icon type group: {@link #PREF_KEY_NOTE}
     *
     * @return the grouping
     */
    public ArrayList<GroupedItem> getGroupItems() {
        return mGroupItems;
    }

    /**
     * Shows the annotation toolbar.
     */
    public void show() {
        show(START_MODE_NORMAL_TOOLBAR);
    }

    /**
     * Shows the annotation toolbar.
     *
     * @param mode The mode that annotation toolbar should start with. Possible values are
     *             {@link AnnotationToolbar#START_MODE_NORMAL_TOOLBAR},
     *             {@link AnnotationToolbar#START_MODE_EDIT_TOOLBAR},
     *             {@link AnnotationToolbar#START_MODE_FORM_TOOLBAR}
     *             {@link AnnotationToolbar#START_MODE_FILL_AND_SIGN_TOOLBAR}
     */
    public void show(int mode) {
        show(mode, null, 0, null, false);
    }

    /**
     * Shows the annotation toolbar.
     *
     * @param mode                 The mode that annotation toolbar should start with. Possible values are
     *                             {@link AnnotationToolbar#START_MODE_NORMAL_TOOLBAR},
     *                             {@link AnnotationToolbar#START_MODE_EDIT_TOOLBAR},
     *                             {@link AnnotationToolbar#START_MODE_FORM_TOOLBAR}
     *                             {@link AnnotationToolbar#START_MODE_FILL_AND_SIGN_TOOLBAR}
     * @param inkAnnot             The ink annotation if the mode is {@link AnnotationToolbar#START_MODE_EDIT_TOOLBAR}
     *                             and the tool is Ink
     * @param toolMode             The tool mode that annotation toolbar should start with
     * @param dismissAfterExitEdit If it is true and the mode is {@link AnnotationToolbar#START_MODE_EDIT_TOOLBAR},
     *                             the regular annotation toolbar shouldn't be shown when the edit
     *                             toolbar is dismissed
     */
    public void show(int mode, Annot inkAnnot, int pageNum, ToolMode toolMode, boolean dismissAfterExitEdit) {
        Context context = getContext();
        if (context == null) {
            return;
        }

        mForceUpdateView = true;

        if (getWidth() > 0 && getHeight() > 0) {
            initViews();
        }

        if (mode == START_MODE_EDIT_TOOLBAR) {
            if (toolMode != null) {
                mDismissAfterExitEdit = dismissAfterExitEdit;
                showEditToolbar(toolMode, inkAnnot, pageNum);
                // update which item in the group items (e.g. line, arrow, polyline)
                // should be shown in annotation toolbar
                updateVisibleAnnotType(mToolManager.getTool().getCreateAnnotType());
            }
        } else if (mode == START_MODE_FORM_TOOLBAR) {
            showFormToolbar(toolMode, FormToolbar.START_MODE_PREPARE_FORM_TOOLBAR);
        } else if (mode == START_MODE_FILL_AND_SIGN_TOOLBAR) {
            showFormToolbar(toolMode, FormToolbar.START_MODE_FILL_AND_SIGN_TOOLBAR);
        } else {
            updateButtonsVisibility();
            showAnnotationToolbar();
        }

        if (getVisibility() != VISIBLE) {
            Transition slide = getOpenTransition();
            TransitionManager.beginDelayedTransition((ViewGroup) getParent(), slide);
            setVisibility(View.VISIBLE);
        }

        mShouldExpand = PdfViewCtrlSettingsManager.getDoubleRowToolbarInUse(context);
        if ((mShouldExpand && !mIsExpanded) || (!mShouldExpand && mIsExpanded)) {
            updateExpanded(getResources().getConfiguration().orientation);
        }

        if (toolMode != null && mode != START_MODE_EDIT_TOOLBAR) {
            mSelectedToolId = getResourceIdOfTool(toolMode);
        }

        if (mSelectedToolId != -1) {
            selectTool(null, mSelectedToolId);
            mSelectedToolId = -1;
        }

        AnalyticsHandlerAdapter.getInstance().sendTimedEvent(AnalyticsHandlerAdapter.EVENT_VIEWER_ANNOTATION_TOOLBAR_OPEN);
    }

    /**
     * Specifies whether the button should stay down or return to pan tool after an annotation is created.
     *
     * @param value True if the button should stay down after an annotation is created
     */
    public void setButtonStayDown(boolean value) {
        mButtonStayDown = value;
    }

    /**
     * The overloaded implementation of {@link View#onConfigurationChanged(Configuration)}.
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (mOverflowPopupWindow != null && mOverflowPopupWindow.isShowing()) {
            mOverflowPopupWindow.dismiss();
        }
        if (mStampStatePopup != null && mStampStatePopup.isShowing()) {
            mStampStatePopup.dismiss();
        }
        updateExpanded(newConfig.orientation);
        mForceUpdateView = true;
    }

    /**
     * The overloaded implementation of {@link FrameLayout#onLayout(boolean, int, int, int, int)}.
     */
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        if (getWidth() == 0 || getHeight() == 0) {
            mLayoutChanged = false;
            return;
        }

        if (mForceUpdateView && !changed) {
            mForceUpdateView = false;
            // in case layout still being modified i.e. dragging in multi-window mode
            // we still need to update the button size and background
            initViews();
        }

        if (changed) {
            mForceUpdateView = false;
            initViews();
            // layout changed to true
            if (!mLayoutChanged) {
                updateButtonsVisibility();
                initSelectedButton();
            }
        }
        mLayoutChanged = changed;
    }

    /**
     * Closes the annotation toolbar.
     */
    public void close() {
        closePopups();

        if (isInEditMode()) {
            mEditToolbarImpl.close();
            setBackgroundColor(mToolbarBackgroundColor); // revert back the toolbar background color
            return;
        }

        if (mToolManager == null) {
            return;
        }

        mToolManager.onClose();
        reset();
        ((Tool) mToolManager.getTool()).setForceSameNextToolMode(false);

        Transition slide = new Slide(Gravity.TOP).setDuration(ANIMATION_DURATION);
        slide.addListener(new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(@NonNull Transition transition) {

            }

            @Override
            public void onTransitionEnd(@NonNull Transition transition) {
                if (mFormToolbar != null) {
                    mFormToolbar.setVisibility(View.GONE);
                }
                if (mAnnotationToolbarListener != null) {
                    mAnnotationToolbarListener.onAnnotationToolbarClosed();
                }
            }

            @Override
            public void onTransitionCancel(@NonNull Transition transition) {

            }

            @Override
            public void onTransitionPause(@NonNull Transition transition) {

            }

            @Override
            public void onTransitionResume(@NonNull Transition transition) {

            }
        });

        TransitionManager.beginDelayedTransition((ViewGroup) getParent(), slide);

        setVisibility(View.GONE);

        saveVisibleAnnotTypes();
        AnalyticsHandlerAdapter.getInstance().endTimedEvent(AnalyticsHandlerAdapter.EVENT_VIEWER_ANNOTATION_TOOLBAR_OPEN);
    }

    private void reset() {
        if (mToolManager == null || mPdfViewCtrl == null) {
            return;
        }
        mToolManager.setTool(mToolManager.createTool(ToolMode.PAN, null));
        selectButton(getResourceIdOfTool(ToolMode.PAN));

        ((Tool) mToolManager.getTool()).setForceSameNextToolMode(mButtonStayDown);
        mPdfViewCtrl.clearSelection();

        mDismissAfterExitEdit = false;
    }

    private Transition getOpenTransition() {
        Transition slide = new Slide(Gravity.TOP).setDuration(ANIMATION_DURATION);
        slide.addListener(new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(@NonNull Transition transition) {

            }

            @Override
            public void onTransitionEnd(@NonNull Transition transition) {
                if (mAnnotationToolbarListener != null) {
                    mAnnotationToolbarListener.onAnnotationToolbarShown();
                }
            }

            @Override
            public void onTransitionCancel(@NonNull Transition transition) {

            }

            @Override
            public void onTransitionPause(@NonNull Transition transition) {

            }

            @Override
            public void onTransitionResume(@NonNull Transition transition) {

            }
        });
        return slide;
    }

    private void showAnnotationToolbar() {
        Transition slide = getOpenTransition();
        TransitionManager.beginDelayedTransition((ViewGroup) getParent(), slide);
        findViewById(R.id.controls_annotation_toolbar_state_normal).setVisibility(VISIBLE);
    }

    private void hideAnnotationToolbar() {
        findViewById(R.id.controls_annotation_toolbar_state_normal).setVisibility(GONE);
    }

    /**
     * Updates the visibility of the buttons on the annotation toolbar.
     * <p>
     * {@link #setup(ToolManager, OnUndoRedoListener)} or {@link #setup(ToolManager)}
     * must be called (before this method) for this method to take effect.
     */
    public void updateButtonsVisibility() {
        Context context = getContext();
        if (context == null || mToolManager == null || mPdfViewCtrl == null) {
            return;
        }

        boolean hasAllTools = hasAllTool();
        ArrayList<ToolMode> precedence = mToolManager.getAnnotToolbarPrecedence();
        boolean hasPrecedence = precedence != null && precedence.size() > 0;
        boolean needsPrecedenceCheck = !hasAllTools && hasPrecedence;

        for (View button : mButtons) {
            int viewId = button.getId();
            ToolMode toolMode = ToolConfig.getInstance().getToolModeByAnnotationToolbarItemId(viewId);
            safeShowHideButton(viewId, needsPrecedenceCheck, !hasPrecedence || precedence.contains(toolMode), VISIBLE);

            int idx = mButtonsVisibility.indexOfKey(viewId);
            if (idx >= 0) {
                button.setVisibility(mButtonsVisibility.valueAt(idx));
            }
        }
        int visibility = hasAllTools ? View.VISIBLE : View.GONE;
        if (!hasPrecedence) {
            // default visibility of buttons
            safeShowHideButton(R.id.controls_annotation_toolbar_tool_text_squiggly, visibility);
            safeShowHideButton(R.id.controls_annotation_toolbar_tool_text_strikeout, visibility);
            safeShowHideButton(R.id.controls_annotation_toolbar_tool_eraser, visibility);
            safeShowHideButton(R.id.controls_annotation_toolbar_tool_free_highlighter, visibility);
            safeShowHideButton(R.id.controls_annotation_toolbar_tool_multi_select, visibility);
            safeShowHideButton(R.id.controls_annotation_toolbar_tool_rubber_stamper, mIsExpanded ? visibility : GONE);
            safeShowHideButton(R.id.controls_annotation_toolbar_tool_image_stamper, mIsExpanded ? visibility : GONE);

            hideCustomisationButtons();
        }
        // combine tools into a single tool
        for (GroupedItem item : mGroupItems) {
            boolean groupVisible = hasAllTools || (!hasPrecedence && (item.getPrefKey().equals(PREF_KEY_TEXT) || item.getPrefKey().equals(PREF_KEY_NOTE)));
            for (int buttonId : item.getButtonIds()) {
                ToolMode toolMode = ToolConfig.getInstance().getToolModeByAnnotationToolbarItemId(buttonId);
                if (hasPrecedence && precedence.contains(toolMode)) {
                    groupVisible = true;
                }
                View v = findViewById(buttonId);
                if (v != null) {
                    v.setVisibility(GONE);
                }
            }
            if (groupVisible) {
                // show one of the tools from each group when showing all tools
                // show freetext group and sticky note group when not showing all tools
                int visibleButtonId = item.getVisibleButtonId();
                if (visibleButtonId != -1) {
                    safeShowHideButton(visibleButtonId, needsPrecedenceCheck, true, VISIBLE);
                }
            }
        }

        if (getStampsEnabledCount() == 0) {
            findViewById(R.id.controls_annotation_toolbar_tool_stamp).setVisibility(GONE);
        }
        if (!canShowMoreButton()) {
            findViewById(R.id.controls_annotation_toolbar_btn_more).setVisibility(View.GONE);
        }
    }

    /**
     * TODO GWL created on 15 Jul 2022
     * Hide some annotation from tool bar.
     */
    private void hideCustomisationButtons() {
        safeShowHideButton(R.id.controls_annotation_toolbar_tool_text_highlight, View.GONE);
        safeShowHideButton(R.id.controls_annotation_toolbar_tool_text_strikeout, View.GONE);
        safeShowHideButton(R.id.controls_annotation_toolbar_tool_text_squiggly, View.GONE);
        safeShowHideButton(R.id.controls_annotation_toolbar_tool_stamp, View.GONE);
        safeShowHideButton(R.id.controls_annotation_toolbar_tool_multi_select, View.GONE);
        safeShowHideButton(R.id.controls_annotation_toolbar_tool_free_highlighter, View.GONE);
        safeShowHideButton(R.id.controls_annotation_toolbar_tool_eraser, View.GONE);
    }

    private void safeShowHideButton(int viewId, int visibility) {
        safeShowHideButton(viewId, false, true, visibility);
    }

    private void safeShowHideButton(int viewId, boolean needsPrecedenceCheck, boolean groupVisible, int visibility) {
        View button = findViewById(viewId);
        ToolMode toolMode = ToolConfig.getInstance().getToolModeByAnnotationToolbarItemId(viewId);
        if (toolMode != null && button != null) {
            if (mToolManager.isToolModeDisabled(toolMode)) {
                button.setVisibility(GONE);
            } else {
                if (needsPrecedenceCheck) {
                    // user has defined which tools should show in collapsed mode
                    if (visibility == VISIBLE) {
                        button.setVisibility(groupVisible ? VISIBLE : GONE);
                    } else {
                        button.setVisibility(visibility);
                    }
                } else {
                    button.setVisibility(visibility);
                }
            }
        }
    }

    public boolean hasAllTool() {
        Context context = getContext();
        return context != null && (Utils.isTablet(context) || mIsExpanded || (Utils.isLandscape(context) && getWidth() > Utils.getRealScreenHeight(context)));
    }

    /**
     * Closes the popup windows.
     */
    public void closePopups() {
        if (mAnnotStyleDialog != null) {
            mAnnotStyleDialog.dismiss();
            mAnnotStyleDialog = null;
        }
        if (mStampStatePopup != null && mStampStatePopup.isShowing()) {
            mStampStatePopup.dismiss();
        }
        if (mOverflowPopupWindow != null && mOverflowPopupWindow.isShowing()) {
            mOverflowPopupWindow.dismiss();
        }
    }

    private void initButtons() {
        Context context = getContext();
        if (context == null || mToolManager == null || mPdfViewCtrl == null) {
            return;
        }

        updateStampBtnState();
        initializeButtons();
    }

    private void updateStampBtnState() {
        Context context = getContext();
        if (context == null) {
            return;
        }

        try {
            int imageResId;
            if (mIsExpanded) { // signature should have its own icon
                imageResId = R.drawable.ic_annotation_signature_black_24dp;
            } else {
                switch (mStampState) {
                    case STATE_SIGNATURE:
                        imageResId = R.drawable.ic_annotation_signature_black_24dp;
                        break;
                    case STATE_RUBBER_STAMP:
                        imageResId = R.drawable.ic_annotation_stamp_black_24dp;
                        break;
                    case STATE_IMAGE_STAMP:
                        imageResId = R.drawable.ic_annotation_image_black_24dp;
                        break;
                    default:
                        return;
                }
            }
            int iconColor = mToolbarToolIconColor;
            Drawable drawable = Utils.createImageDrawableSelector(context, imageResId, iconColor);
            ((AppCompatImageButton) findViewById(R.id.controls_annotation_toolbar_tool_stamp)).setImageDrawable(drawable);
        } catch (Exception e) {
            AnalyticsHandlerAdapter.getInstance().sendException(e);
        }
    }

    private void initSelectedButton() {
        if (mToolManager == null) {
            return;
        }

        // Let's make the button selected according to the current tool
        ToolMode toolMode = ToolManager.getDefaultToolMode(mToolManager.getTool().getToolMode());
        selectButton(getResourceIdOfTool(toolMode));
    }

    private int getResourceIdOfTool(ToolMode toolMode) {
        switch (toolMode) {
            case LINE_CREATE:
                return R.id.controls_annotation_toolbar_tool_line;
            case ARROW_CREATE:
                return R.id.controls_annotation_toolbar_tool_arrow;
            case RULER_CREATE:
                return R.id.controls_annotation_toolbar_tool_ruler;
            case PERIMETER_MEASURE_CREATE:
                return R.id.controls_annotation_toolbar_tool_perimeter_measure;
            case AREA_MEASURE_CREATE:
                return R.id.controls_annotation_toolbar_tool_area_measure;
            case POLYLINE_CREATE:
                return R.id.controls_annotation_toolbar_tool_polyline;
            case RECT_CREATE:
                return R.id.controls_annotation_toolbar_tool_rectangle;
            case OVAL_CREATE:
                return R.id.controls_annotation_toolbar_tool_oval;
            case POLYGON_CREATE:
                return R.id.controls_annotation_toolbar_tool_polygon;
            case CLOUD_CREATE:
                return R.id.controls_annotation_toolbar_tool_cloud;
            case INK_ERASER:
                return R.id.controls_annotation_toolbar_tool_eraser;
            case TEXT_ANNOT_CREATE:
                return R.id.controls_annotation_toolbar_tool_stickynote;
            case SOUND_CREATE:
                return R.id.controls_annotation_toolbar_tool_sound;
            case TEXT_CREATE:
                return R.id.controls_annotation_toolbar_tool_freetext;
            case CALLOUT_CREATE:
                return R.id.controls_annotation_toolbar_tool_callout;
            case TEXT_UNDERLINE:
                return R.id.controls_annotation_toolbar_tool_text_underline;
            case TEXT_HIGHLIGHT:
                return R.id.controls_annotation_toolbar_tool_text_highlight;
            case TEXT_SQUIGGLY:
                return R.id.controls_annotation_toolbar_tool_text_squiggly;
            case TEXT_STRIKEOUT:
                return R.id.controls_annotation_toolbar_tool_text_strikeout;
            case FREE_HIGHLIGHTER:
                return R.id.controls_annotation_toolbar_tool_free_highlighter;
            case ANNOT_EDIT_RECT_GROUP:
                return R.id.controls_annotation_toolbar_tool_multi_select;
            case SIGNATURE:
                return R.id.controls_annotation_toolbar_tool_stamp;
            case STAMPER:
                // when only one row of tools are displayed, we use
                // controls_annotation_toolbar_tool_stamp as the only
                // resource for all signature/stamper/rubber stamper annotations.
                // when two rows of tools are displayed, each annotation has
                // its own resource.
                return mIsExpanded ? R.id.controls_annotation_toolbar_tool_image_stamper
                        : R.id.controls_annotation_toolbar_tool_stamp;
            case RUBBER_STAMPER:
                return mIsExpanded ? R.id.controls_annotation_toolbar_tool_rubber_stamper
                        : R.id.controls_annotation_toolbar_tool_stamp;
            case PAN:
            default:
                return R.id.controls_annotation_toolbar_tool_pan;
        }
    }

    /**
     * Select a tool.
     *
     * @param view The view
     * @param id   The id of the tool to be selected.
     */
    public void selectTool(View view, int id) {
        Context context = getContext();
        if (context == null || mToolManager == null) {
            return;
        }

        ToolMode annotMode = ToolManager.getDefaultToolMode(mToolManager.getTool().getToolMode());
        if (Utils.isAnnotationHandlerToolMode(annotMode) ||
                annotMode == ToolMode.TEXT_CREATE ||
                annotMode == ToolMode.CALLOUT_CREATE ||
                annotMode == ToolMode.PAN) {
            mToolManager.onClose();
        }

        // Because the Controls is a library project, we can't use
        // R.id... as a constant value in a switch-case, so we need
        // to chain if-else instead.

        View button = findViewById(id);
        if (null == button) {
            return;
        }
        int annotType = getAnnotTypeFromButtonId(id);

        ToolMode toolMode = null;
        int analyticId = -1;
        Set<String> whiteListFonts = null;
        if (id == R.id.controls_annotation_toolbar_tool_line) {
            toolMode = ToolMode.LINE_CREATE;
            analyticId = AnalyticsHandlerAdapter.ANNOTATION_TOOL_LINE;
        } else if (id == R.id.controls_annotation_toolbar_tool_arrow) {
            toolMode = ToolMode.ARROW_CREATE;
            analyticId = AnalyticsHandlerAdapter.ANNOTATION_TOOL_ARROW;
        } else if (id == R.id.controls_annotation_toolbar_tool_ruler) {
            toolMode = ToolMode.RULER_CREATE;
            analyticId = AnalyticsHandlerAdapter.ANNOTATION_TOOL_RULER;
        } else if (id == R.id.controls_annotation_toolbar_tool_perimeter_measure) {
            toolMode = ToolMode.PERIMETER_MEASURE_CREATE;
            analyticId = AnalyticsHandlerAdapter.ANNOTATION_TOOL_PERIMETER_MEASURE;
        } else if (id == R.id.controls_annotation_toolbar_tool_area_measure) {
            toolMode = ToolMode.AREA_MEASURE_CREATE;
            analyticId = AnalyticsHandlerAdapter.ANNOTATION_TOOL_AREA_MEASURE;
        } else if (id == R.id.controls_annotation_toolbar_tool_polyline) {
            toolMode = ToolMode.POLYLINE_CREATE;
            analyticId = AnalyticsHandlerAdapter.ANNOTATION_TOOL_POLYLINE;
        } else if (id == R.id.controls_annotation_toolbar_tool_rectangle) {
            toolMode = ToolMode.RECT_CREATE;
            analyticId = AnalyticsHandlerAdapter.ANNOTATION_TOOL_RECTANGLE;
        } else if (id == R.id.controls_annotation_toolbar_tool_oval) {
            toolMode = ToolMode.OVAL_CREATE;
            analyticId = AnalyticsHandlerAdapter.ANNOTATION_TOOL_OVAL;
        } else if (id == R.id.controls_annotation_toolbar_tool_polygon) {
            toolMode = ToolMode.POLYGON_CREATE;
            analyticId = AnalyticsHandlerAdapter.ANNOTATION_TOOL_POLYGON;
        } else if (id == R.id.controls_annotation_toolbar_tool_cloud) {
            toolMode = ToolMode.CLOUD_CREATE;
            analyticId = AnalyticsHandlerAdapter.ANNOTATION_TOOL_CLOUD;
        } else if (id == R.id.controls_annotation_toolbar_tool_eraser) {
            toolMode = ToolMode.INK_ERASER;
            analyticId = AnalyticsHandlerAdapter.ANNOTATION_TOOL_ERASER;
        } else if (id == R.id.controls_annotation_toolbar_tool_free_highlighter) {
            toolMode = ToolMode.FREE_HIGHLIGHTER;
            analyticId = AnalyticsHandlerAdapter.ANNOTATION_TOOL_FREE_HIGHLIGHTER;
        } else if (id == R.id.controls_annotation_toolbar_tool_stickynote) {
            toolMode = ToolMode.TEXT_ANNOT_CREATE;
            analyticId = AnalyticsHandlerAdapter.ANNOTATION_TOOL_STICKY_NOTE;
        } else if (id == R.id.controls_annotation_toolbar_tool_sound) {
            toolMode = ToolMode.SOUND_CREATE;
            analyticId = AnalyticsHandlerAdapter.ANNOTATION_TOOL_SOUND;
        } else if (id == R.id.controls_annotation_toolbar_tool_freetext) {
            toolMode = ToolMode.TEXT_CREATE;
            analyticId = AnalyticsHandlerAdapter.ANNOTATION_TOOL_FREE_TEXT;
            whiteListFonts = mToolManager.getFreeTextFonts();
        } else if (id == R.id.controls_annotation_toolbar_tool_callout) {
            toolMode = ToolMode.CALLOUT_CREATE;
            analyticId = AnalyticsHandlerAdapter.ANNOTATION_TOOL_CALLOUT;
            whiteListFonts = mToolManager.getFreeTextFonts();
        } else if (id == R.id.controls_annotation_toolbar_tool_text_highlight) {
            toolMode = ToolMode.TEXT_HIGHLIGHT;
            analyticId = AnalyticsHandlerAdapter.ANNOTATION_TOOL_HIGHLIGHT;
        } else if (id == R.id.controls_annotation_toolbar_tool_text_underline) {
            toolMode = ToolMode.TEXT_UNDERLINE;
            analyticId = AnalyticsHandlerAdapter.ANNOTATION_TOOL_UNDERLINE;
        } else if (id == R.id.controls_annotation_toolbar_tool_text_squiggly) {
            toolMode = ToolMode.TEXT_SQUIGGLY;
            analyticId = AnalyticsHandlerAdapter.ANNOTATION_TOOL_SQUIGGLY;
        } else if (id == R.id.controls_annotation_toolbar_tool_text_strikeout) {
            toolMode = ToolMode.TEXT_STRIKEOUT;
            analyticId = AnalyticsHandlerAdapter.ANNOTATION_TOOL_STRIKEOUT;
        }
        boolean sendAnalytics = mSelectedButtonId != id;
        if (toolMode != null) {
            //TODO: GWL 26 July 2022 To prevent the double tap on annotation toolbar start.
            /*if (mSelectedButtonId == id) {
                AnnotStyle annotStyle = getCustomAnnotStyle(annotType);
                if (annotStyle == null) {
                    return;
                }
                AnnotStyleDialogFragment popupWindow = new AnnotStyleDialogFragment.Builder(annotStyle)
                        .setAnchorView(button)
                        .setMoreAnnotTypes(getMoreAnnotTypes(annotType))
                        .setWhiteListFont(whiteListFonts)
                        .build();
                showAnnotPropertyPopup(popupWindow, view, analyticId);
            }*/
            //TODO: GWL 26 July 2022 To prevent the double tap on annotation toolbar End.
            mToolManager.setTool(mToolManager.createTool(toolMode, mToolManager.getTool()));
            ToolManager.Tool tool = mToolManager.getTool();
            ((Tool) tool).setForceSameNextToolMode(mButtonStayDown);
            selectButton(id);
            mEventAction = true;
            if (tool instanceof AdvancedShapeCreate) {
                ((AdvancedShapeCreate) tool).setOnEditToolbarListener(this);
            }
        } else if (id == R.id.controls_annotation_toolbar_tool_freehand) {
            showEditToolbar(ToolMode.INK_CREATE);
            mEventAction = true;
            analyticId = AnalyticsHandlerAdapter.ANNOTATION_TOOL_FREEHAND;
        } else if (id == R.id.controls_annotation_toolbar_tool_stamp
                || id == R.id.controls_annotation_toolbar_tool_image_stamper
                || id == R.id.controls_annotation_toolbar_tool_rubber_stamper) {
            // only show popup containing the other tools if the image stamper is included.
            // Otherwise, there is no popup.
            if (getStampsEnabledCount() >= 2) {
                boolean imageStamperEnabled = !mToolManager.isToolModeDisabled(STAMPER);
                boolean rubberStamperEnabled = !mToolManager.isToolModeDisabled(RUBBER_STAMPER);
                if (!mIsExpanded && (imageStamperEnabled || rubberStamperEnabled)) {
                    if (mSelectedButtonId == id) {
                        showStampStatePopup(id, view);
                    }
                }
            }
            if (id == R.id.controls_annotation_toolbar_tool_stamp && (mIsExpanded || STATE_SIGNATURE.equals(mStampState))) {
                analyticId = AnalyticsHandlerAdapter.ANNOTATION_TOOL_SIGNATURE;
                mToolManager.setTool(mToolManager.createTool(SIGNATURE, mToolManager.getTool()));
                ((Tool) mToolManager.getTool()).setForceSameNextToolMode(mButtonStayDown);
                selectButton(id);
                mEventAction = true;
            } else if (id == R.id.controls_annotation_toolbar_tool_image_stamper
                    || (!mIsExpanded && STATE_IMAGE_STAMP.equals(mStampState))) {
                analyticId = AnalyticsHandlerAdapter.ANNOTATION_TOOL_STAMP;
                mToolManager.setTool(mToolManager.createTool(STAMPER, mToolManager.getTool()));
                ((Tool) mToolManager.getTool()).setForceSameNextToolMode(mButtonStayDown);
                selectButton(id);
                mEventAction = true;
            } else if (id == R.id.controls_annotation_toolbar_tool_rubber_stamper
                    || (!mIsExpanded && STATE_RUBBER_STAMP.equals(mStampState))) {
                analyticId = AnalyticsHandlerAdapter.ANNOTATION_TOOL_RUBBER_STAMP;
                mToolManager.setTool(mToolManager.createTool(RUBBER_STAMPER, mToolManager.getTool()));
                ((Tool) mToolManager.getTool()).setForceSameNextToolMode(mButtonStayDown);
                selectButton(id);
                mEventAction = true;
            }
        } else if (id == R.id.controls_annotation_toolbar_tool_multi_select) {
            analyticId = AnalyticsHandlerAdapter.ANNOTATION_TOOL_MULTI_SELECT;
            mToolManager.setTool(mToolManager.createTool(ToolMode.ANNOT_EDIT_RECT_GROUP, null)); // reset
            ((Tool) mToolManager.getTool()).setForceSameNextToolMode(mButtonStayDown);
            selectButton(id);
            mEventAction = true;
        } else if (id == R.id.controls_annotation_toolbar_tool_pan) {
            analyticId = AnalyticsHandlerAdapter.ANNOTATION_TOOL_PAN;
            mToolManager.setTool(mToolManager.createTool(ToolMode.PAN, null)); // reset
            selectButton(id);
        } else if (id == R.id.controls_annotation_toolbar_btn_close) {
            sendAnalytics = false;
            close();
            AnalyticsHandlerAdapter.getInstance().sendEvent(AnalyticsHandlerAdapter.EVENT_VIEWER_ANNOTATION_TOOLBAR_CLOSE,
                    AnalyticsParam.noActionParam(mEventAction));
            mEventAction = false;
        } else if (id == R.id.controls_annotation_toolbar_btn_more) {
            if (mOverflowPopupWindow != null && mOverflowPopupWindow.isShowing()) {
                mOverflowPopupWindow.dismiss();
            }
            mOverflowPopupWindow =
                    new AnnotToolbarOverflowPopupWindow(context,
                            mToolManager.getUndoRedoManger(),
                            mToolManager.isShowUndoRedo() ? mOnUndoRedoListener : null,
                            this
                    );
            try {
                mOverflowPopupWindow.showAsDropDown(view);
            } catch (Exception ex) {
                AnalyticsHandlerAdapter.getInstance().sendException(ex);
            }
            sendAnalytics = false;
        }

        if (sendAnalytics) {
            AnalyticsHandlerAdapter.getInstance().sendEvent(AnalyticsHandlerAdapter.EVENT_ANNOTATION_TOOLBAR, AnalyticsParam.annotationToolbarParam(analyticId));
        }
    }

    private void saveVisibleAnnotTypes() {
        Context context = getContext();
        if (context == null || mVisibleAnnotTypeMap == null) {
            return;
        }

        JSONObject object = new JSONObject();
        for (Map.Entry<String, Integer> entry : mVisibleAnnotTypeMap.entrySet()) {
            try {
                object.put(entry.getKey(), entry.getValue());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        PdfViewCtrlSettingsManager.setAnnotToolbarVisibleAnnotTypes(context, object.toString());
    }

    /**
     * The overloaded implementation of {@link ToolManager.ToolChangedListener#toolChanged(ToolManager.Tool, ToolManager.Tool)}.
     */
    @Override
    public void toolChanged(ToolManager.Tool newTool, ToolManager.Tool oldTool) {

        if (newTool == null || !isShowing()) {
            return;
        }

        boolean canSelectButton = false;

        if (oldTool != null && oldTool instanceof Tool && newTool instanceof Tool) {
            Tool oldT = (Tool) oldTool;
            Tool newT = (Tool) newTool;
            canSelectButton = !oldT.isForceSameNextToolMode() || !newT.isEditAnnotTool();

            if (isInEditMode() && newTool instanceof FreehandCreate) {
                ((FreehandCreate) newTool).setFromEditToolbar(true);
            }
        }

        if (canSelectButton) {
            ToolMode newToolMode = ToolManager.getDefaultToolMode(newTool.getToolMode());
            // The new tool might be grouped and not visible, so should update
            // group items and update buttons visibility
            updateVisibleAnnotType(newTool.getCreateAnnotType());
            updateButtonsVisibility();
            selectButton(getResourceIdOfTool(newToolMode));
            ToolManager.ToolModeBase toolModeBase = newTool.getToolMode();

            if (ToolMode.SIGNATURE.equals(toolModeBase)) {
                mStampState = STATE_SIGNATURE;
            } else if (RUBBER_STAMPER.equals(toolModeBase)) {
                mStampState = STATE_RUBBER_STAMP;
            } else if (STAMPER.equals(toolModeBase)) {
                mStampState = STATE_IMAGE_STAMP;
            }
            // if it is not in expand mode then for all signature, image stamp
            // and rubber stamp there is only one button id
            if (mSelectedButtonId == R.id.controls_annotation_toolbar_tool_stamp) {
                updateStampBtnState();
            }
        }

        if (newTool instanceof AdvancedShapeCreate) {
            ((AdvancedShapeCreate) newTool).setOnEditToolbarListener(this);
        }
    }

    private void showAnnotPropertyPopup(final AnnotStyleDialogFragment popupWindow, View view, final int annotToolId) {
        if (view == null || popupWindow == null) {
            return;
        }
        if (mToolManager.isSkipNextTapEvent()) {
            mToolManager.resetSkipNextTapEvent();
            return;
        }

        if (mAnnotStyleDialog != null) {
            // prev style picker is not closed yet
            return;
        }
        mAnnotStyleDialog = popupWindow;
        mAnnotStyleDialog.setCanShowRichContentSwitch(mToolManager.isShowRichContentOption());
        mAnnotStyleDialog.setCanShowTextAlignment(!mToolManager.isAutoResizeFreeText());
        mAnnotStyleDialog.setCanShowPressureSwitch(true); // pressure switch should always be available when customizing the tool properties
        mAnnotStyleDialog.setAnnotStyleProperties(mToolManager.getAnnotStyleProperties());

        popupWindow.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                mAnnotStyleDialog = null;

                Context context = getContext();
                if (context == null || mToolManager == null) {
                    return;
                }

                AnnotStyle annotStyle = popupWindow.getAnnotStyle();
                ToolStyleConfig.getInstance().saveAnnotStyle(context, annotStyle, "");

                Tool tool = (Tool) mToolManager.getTool();
                if (tool != null) {
                    tool.setupAnnotProperty(annotStyle);
                }
            }
        });
        popupWindow.setOnMoreAnnotTypesClickListener(new AnnotStyleView.OnMoreAnnotTypeClickedListener() {
            @Override
            public void onAnnotTypeClicked(int annotType) {
                Context context = getContext();
                if (context == null) {
                    return;
                }

                popupWindow.saveAnnotStyles();
                ToolStyleConfig.getInstance().saveAnnotStyle(context, popupWindow.getAnnotStyle(), "");
                updateAnnotStyleDialog(popupWindow, annotType);
            }
        });

        FragmentActivity activity = null;
        if (getContext() instanceof FragmentActivity) {
            activity = (FragmentActivity) getContext();
        } else if (mToolManager.getCurrentActivity() != null) {
            activity = mToolManager.getCurrentActivity();
        }
        if (activity == null) {
            AnalyticsHandlerAdapter.getInstance().sendException(new Exception("AnnotationToolbar is not attached to with an Activity"));
            return;
        }
        popupWindow.show(activity.getSupportFragmentManager(), AnalyticsHandlerAdapter.STYLE_PICKER_LOC_ANNOT_TOOLBAR,
                AnalyticsHandlerAdapter.getInstance().getAnnotationTool(annotToolId));
    }

    private void updateAnnotStyleDialog(AnnotStyleDialogFragment dialog, int annotType) {
        AnnotStyle previousAnnotStyle = dialog.getAnnotStyle();
        int previousAnnotType = previousAnnotStyle.getAnnotType();
        AnnotStyle annotStyle = getCustomAnnotStyle(annotType);
        if (annotStyle == null) {
            return;
        }
        dialog.setAnnotStyle(annotStyle);
        dialog.setCanShowRichContentSwitch(mToolManager.isShowRichContentOption());
        dialog.setCanShowTextAlignment(!mToolManager.isAutoResizeFreeText());
        int nextToolId = getButtonIdFromAnnotType(annotType);
        View nextToolButton = findViewById(nextToolId);
        View previousToolButton = findViewById(getButtonIdFromAnnotType(previousAnnotType));
        if (nextToolButton != null && previousToolButton != null && previousToolButton.getVisibility() == VISIBLE) {
            previousToolButton.setVisibility(GONE);
            nextToolButton.setVisibility(VISIBLE);
        }
        updateVisibleAnnotType(annotType);
        selectTool(null, nextToolId);
    }

    private void updateVisibleAnnotType(
            int annotType) {

        if (mVisibleAnnotTypeMap == null) {
            return;
        }
        for (GroupedItem item : mGroupItems) {
            if (item.contains(annotType)) {
                mVisibleAnnotTypeMap.put(item.getPrefKey(), annotType);
            }
        }
    }

    public int getButtonIdFromAnnotType(int annotType) {
        int indexAtValue = mButtonAnnotTypeMap.indexOfValue(annotType);
        if (indexAtValue > -1) {
            return mButtonAnnotTypeMap.keyAt(indexAtValue);
        }
        return -1;
    }

    private int getAnnotTypeFromButtonId(int buttonId) {
        return mButtonAnnotTypeMap.get(buttonId);
    }

    @Nullable
    private ArrayList<Integer> getMoreAnnotTypes(int annotType) {
        for (GroupedItem item : mGroupItems) {
            if (item.contains(annotType)) {
                return item.getAvailableAnnotTypes();
            }
        }
        return null;
    }

    private void showStampStatePopup(final int id, final View view) {
        Context context = getContext();
        if (context == null || view == null || mToolManager == null || getStampsEnabledCount() < 2) {
            return;
        }

        if (mStampStatePopup == null) {
            mStampStatePopup = new StampStatePopup(context, mToolManager, mStampState, mToolbarBackgroundColor, mToolbarToolIconColor);
            updateStampPopupSize();
        } else {
            mStampStatePopup.updateView(mStampState);
        }

        mStampStatePopup.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                Context context = getContext();
                if (context == null || mToolManager == null) {
                    return;
                }

                String currentStampState = mStampState;
                mStampState = mStampStatePopup.getStampState();
                if (mStampState == null) {
                    return;
                }
                checkStampState();
                updateStampBtnState();

                SharedPreferences settings = Tool.getToolPreferences(context);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString(Tool.ANNOTATION_TOOLBAR_SIGNATURE_STATE, mStampState);
                editor.apply();
                int analyticsId = 0;
                boolean differentState = !mStampState.equals(currentStampState);
                switch (mStampState) {
                    case STATE_SIGNATURE:
                        mToolManager.setTool(mToolManager.createTool(SIGNATURE, mToolManager.getTool()));
                        ((Tool) mToolManager.getTool()).setForceSameNextToolMode(mButtonStayDown);
                        selectButton(id);
                        analyticsId = AnalyticsHandlerAdapter.ANNOTATION_TOOL_SIGNATURE;
                        mEventAction = true;
                        break;
                    case STATE_IMAGE_STAMP:
                        mToolManager.setTool(mToolManager.createTool(STAMPER, mToolManager.getTool()));
                        ((Tool) mToolManager.getTool()).setForceSameNextToolMode(mButtonStayDown);
                        selectButton(id);
                        analyticsId = AnalyticsHandlerAdapter.ANNOTATION_TOOL_STAMP;
                        mEventAction = true;
                        break;
                    case STATE_RUBBER_STAMP:
                        mToolManager.setTool(mToolManager.createTool(RUBBER_STAMPER, mToolManager.getTool()));
                        ((Tool) mToolManager.getTool()).setForceSameNextToolMode(mButtonStayDown);
                        selectButton(id);
                        analyticsId = AnalyticsHandlerAdapter.ANNOTATION_TOOL_RUBBER_STAMP;
                        mEventAction = true;
                        break;
                }
                if (differentState) {
                    AnalyticsHandlerAdapter.getInstance().sendEvent(AnalyticsHandlerAdapter.EVENT_ANNOTATION_TOOLBAR,
                            AnalyticsParam.annotationToolbarParam(analyticsId));
                } else {
                    mToolManager.skipNextTapEvent();
                }
            }
        });
        mStampStatePopup.showAsDropDown(view);
    }

    private void updateStampPopupSize() {

        if (mStampStatePopup != null) {
            mStampStatePopup.setWidth(getToolWidth());
            mStampStatePopup.setHeight(getToolHeight() * (getStampsEnabledCount() - 1));
        }
    }

    private boolean canShowMoreButton() {
        if (mPdfViewCtrl != null && mPdfViewCtrl.isUndoRedoEnabled() && mOnUndoRedoListener != null && mToolManager.isShowUndoRedo()) {
            return true;
        }
        // if on phone, show "show/hide all tools" menu option
        Context context = getContext();
        return !Utils.isLandscape(context) && !Utils.isTablet(context);
    }

    /**
     * Sets the {@link AnnotationToolbarListener} listener.
     *
     * @param listener The listener
     */
    public void setAnnotationToolbarListener(AnnotationToolbarListener listener) {
        mAnnotationToolbarListener = listener;
    }

    /**
     * Sets the {@link OnUndoRedoListener} listener.
     *
     * @param listener The listener
     */
    @SuppressWarnings("unused")
    public void setOnUndoRedoListener(OnUndoRedoListener listener) {
        mOnUndoRedoListener = listener;
    }

    /**
     * @return True if the annotation toolbar is visible
     */
    public boolean isShowing() {
        return getVisibility() == View.VISIBLE;
    }

    @Override
    void addButtons() {
        safeAddButtons(R.id.controls_annotation_toolbar_tool_text_highlight);
        safeAddButtons(R.id.controls_annotation_toolbar_tool_text_underline);
        safeAddButtons(R.id.controls_annotation_toolbar_tool_stickynote);
        safeAddButtons(R.id.controls_annotation_toolbar_tool_sound);
        safeAddButtons(R.id.controls_annotation_toolbar_tool_text_squiggly);
        safeAddButtons(R.id.controls_annotation_toolbar_tool_text_strikeout);
        safeAddButtons(R.id.controls_annotation_toolbar_tool_free_highlighter);
        safeAddButtons(R.id.controls_annotation_toolbar_tool_stamp); // by default signature stamp
        safeAddButtons(R.id.controls_annotation_toolbar_tool_image_stamper);
        safeAddButtons(R.id.controls_annotation_toolbar_tool_rubber_stamper);
        safeAddButtons(R.id.controls_annotation_toolbar_tool_line);
        safeAddButtons(R.id.controls_annotation_toolbar_tool_arrow);
        safeAddButtons(R.id.controls_annotation_toolbar_tool_ruler);
        safeAddButtons(R.id.controls_annotation_toolbar_tool_perimeter_measure);
        safeAddButtons(R.id.controls_annotation_toolbar_tool_area_measure);
        safeAddButtons(R.id.controls_annotation_toolbar_tool_polyline);
        safeAddButtons(R.id.controls_annotation_toolbar_tool_freehand);
        safeAddButtons(R.id.controls_annotation_toolbar_tool_eraser);
        safeAddButtons(R.id.controls_annotation_toolbar_tool_freetext);
        safeAddButtons(R.id.controls_annotation_toolbar_tool_callout);
        safeAddButtons(R.id.controls_annotation_toolbar_tool_rectangle);
        safeAddButtons(R.id.controls_annotation_toolbar_tool_oval);
        safeAddButtons(R.id.controls_annotation_toolbar_tool_polygon);
        safeAddButtons(R.id.controls_annotation_toolbar_tool_cloud);
        safeAddButtons(R.id.controls_annotation_toolbar_tool_multi_select);
        safeAddButtons(R.id.controls_annotation_toolbar_tool_pan);
        safeAddButtons(R.id.controls_annotation_toolbar_btn_close);
        if (canShowMoreButton()) {
            safeAddButtons(R.id.controls_annotation_toolbar_btn_more);
        }
    }

    /**
     * @return True if the ink toolbar is visible
     */
    @SuppressWarnings("unused")
    public boolean isInEditMode() {
        return mEditToolbarImpl != null && mEditToolbarImpl.isToolbarShown();
    }

    public boolean isInFormMode() {
        return mFormToolbar != null && mFormToolbar.isShowing();
    }

    /**
     * Hides the toolbar button given a toolbar button id.
     *
     * @param id of the toolbar button to hide
     */
    public void hideButton(@NonNull AnnotationToolbarButtonId id) {
        setToolbarButtonVisibility(id, false);
    }

    /**
     * Shows the toolbar button given a toolbar button id.
     *
     * @param id of the toolbar button to show
     */
    public void showButton(@NonNull AnnotationToolbarButtonId id) {
        setToolbarButtonVisibility(id, true);
    }

    private void setToolbarButtonVisibility(@NonNull AnnotationToolbarButtonId id, boolean visible) {
        mButtonsVisibility.put(id.id, visible ? VISIBLE : GONE);
        updateButtonsVisibility(); // will do nothing if setup has not been called
    }

    /**
     * Handles the shortcuts key in the annotation toolbar.
     *
     * @param keyCode the key code
     * @param event   the key event
     * @return true if it is handled; false otherwise
     */
    // Note: we wouldn't override onKeyUp event here because it only gets called if the view is
    // focused, but we don't want this view gets focused since then the PDFViewCtrl will not
    // receive key events.
    public boolean handleKeyUp(int keyCode, KeyEvent event) {
        Context context = getContext();
        if (context == null || mToolManager == null) {
            return false;
        }

        if (isInEditMode()) {
            return mEditToolbarImpl.handleKeyUp(keyCode, event);
        }

        if (isInFormMode()) {
            return mFormToolbar.handleKeyUp(keyCode, event);
        }

        Tool tool = (Tool) mToolManager.getTool();
        if (tool == null) {
            return false;
        }

        if (findViewById(R.id.controls_annotation_toolbar_tool_pan).isShown()
                && !(tool instanceof Pan)
                && ShortcutHelper.isCancelTool(keyCode, event)) {
            closePopups();
            selectTool(null, R.id.controls_annotation_toolbar_tool_pan);
            return true;
        }

        if (findViewById(R.id.controls_annotation_toolbar_btn_close).isShown()
                && ShortcutHelper.isCloseMenu(keyCode, event)) {
            closePopups();
            selectTool(null, R.id.controls_annotation_toolbar_btn_close);
            return true;
        }

        int mode = START_MODE_UNKNOWN;
        mSelectedToolId = -1;

        if (ShortcutHelper.isHighlightAnnot(keyCode, event)) {
            mode = START_MODE_NORMAL_TOOLBAR;
            mSelectedToolId = R.id.controls_annotation_toolbar_tool_text_highlight;
        }

        if (ShortcutHelper.isUnderlineAnnot(keyCode, event)) {
            mode = START_MODE_NORMAL_TOOLBAR;
            mSelectedToolId = R.id.controls_annotation_toolbar_tool_text_underline;
        }

        if (ShortcutHelper.isStrikethroughAnnot(keyCode, event)) {
            mode = START_MODE_NORMAL_TOOLBAR;
            mSelectedToolId = R.id.controls_annotation_toolbar_tool_text_strikeout;
        }

        if (ShortcutHelper.isSquigglyAnnot(keyCode, event)) {
            mode = START_MODE_NORMAL_TOOLBAR;
            mSelectedToolId = R.id.controls_annotation_toolbar_tool_text_squiggly;
        }

        if (ShortcutHelper.isTextboxAnnot(keyCode, event)) {
            mode = START_MODE_NORMAL_TOOLBAR;
            mSelectedToolId = R.id.controls_annotation_toolbar_tool_freetext;
        }

        if (ShortcutHelper.isCommentAnnot(keyCode, event)) {
            mode = START_MODE_NORMAL_TOOLBAR;
            mSelectedToolId = R.id.controls_annotation_toolbar_tool_stickynote;
        }

        if (ShortcutHelper.isRectangleAnnot(keyCode, event)) {
            mode = START_MODE_NORMAL_TOOLBAR;
            mSelectedToolId = R.id.controls_annotation_toolbar_tool_rectangle;
        }

        if (ShortcutHelper.isOvalAnnot(keyCode, event)) {
            mode = START_MODE_NORMAL_TOOLBAR;
            mSelectedToolId = R.id.controls_annotation_toolbar_tool_oval;
        }

        if (ShortcutHelper.isDrawAnnot(keyCode, event)) {
            mode = START_MODE_NORMAL_TOOLBAR;
            mSelectedToolId = R.id.controls_annotation_toolbar_tool_freehand;
        }

        if (findViewById(R.id.controls_annotation_toolbar_tool_eraser).isShown()
                && ShortcutHelper.isEraserAnnot(keyCode, event)) {
            // don't let start with eraser when eraser will not be shown in the toolbar.
            mode = START_MODE_NORMAL_TOOLBAR;
            mSelectedToolId = R.id.controls_annotation_toolbar_tool_eraser;
        }

        if (ShortcutHelper.isLineAnnot(keyCode, event)) {
            mode = START_MODE_NORMAL_TOOLBAR;
            mSelectedToolId = R.id.controls_annotation_toolbar_tool_line;
        }

        if (ShortcutHelper.isArrowAnnot(keyCode, event)) {
            mode = START_MODE_NORMAL_TOOLBAR;
            mSelectedToolId = R.id.controls_annotation_toolbar_tool_arrow;
        }

        if (ShortcutHelper.isSignatureAnnot(keyCode, event) && !mToolManager.isToolModeDisabled(SIGNATURE)) {
            mode = START_MODE_NORMAL_TOOLBAR;
            mStampState = STATE_SIGNATURE;
            checkStampState();
            updateStampBtnState();
            mSelectedToolId = R.id.controls_annotation_toolbar_tool_stamp;
        }

        if (ShortcutHelper.isImageAnnot(keyCode, event) && !mToolManager.isToolModeDisabled(STAMPER)) {
            mode = START_MODE_NORMAL_TOOLBAR;
            mStampState = STATE_IMAGE_STAMP;
            checkStampState();
            updateStampBtnState();
            if (mIsExpanded) {
                mSelectedToolId = R.id.controls_annotation_toolbar_tool_image_stamper;
            } else {
                mSelectedToolId = R.id.controls_annotation_toolbar_tool_stamp;
            }
        }

        if (mode != START_MODE_UNKNOWN) {
            closePopups();
            if (!isShowing()) {
                if (mAnnotationToolbarListener != null) {
                    mAnnotationToolbarListener.onShowAnnotationToolbarByShortcut(mode);
                }
            } else {
                selectTool(null, mSelectedToolId);
            }
            return true;
        }

        return false;
    }

    @Nullable
    private AnnotStyle getCustomAnnotStyle(int annotType) {
        Context context = getContext();
        if (context == null) {
            return null;
        }

        AnnotStyle annotStyle = ToolStyleConfig.getInstance().getCustomAnnotStyle(context, annotType, "");
        annotStyle.setSnap(mToolManager.isSnappingEnabledForMeasurementTools());
        annotStyle.setTextHTMLContent(mToolManager.isRichContentEnabledForFreeText() ? "rc" : "");
        return annotStyle;
    }

    /**
     * Whether this annotation toolbar is in expanded mode
     *
     * @return true if expanded, false otherwise
     */
    public boolean isExpanded() {
        return mIsExpanded;
    }

    /**
     * Whether this annotation toolbar has enough tool to fill another row.
     * @return true if can be expended, false otherwise
     */
    public boolean canExpand() {
        // check if there is actually more buttons to show on first row
        int visibleCount = 0;
        for (View button : mButtons) {
            // if contains any second row item, will have to expand
            if (!isFirstRowItem(button)) {
                return true;
            }
            if (button.getVisibility() == VISIBLE) {
                visibleCount++;
            }
        }
        if (visibleCount <= NUM_PHONE_NORMAL_STATE_TOOL_ICONS) {
            return false;
        }

        return true;
    }

    private boolean isFirstRowItem(View button) {
        if (button.getId() == R.id.controls_annotation_toolbar_tool_stickynote ||
                button.getId() == R.id.controls_annotation_toolbar_tool_sound ||
                button.getId() == R.id.controls_annotation_toolbar_tool_text_highlight ||
                button.getId() == R.id.controls_annotation_toolbar_tool_text_underline ||
                button.getId() == R.id.controls_annotation_toolbar_tool_stamp ||
                button.getId() == R.id.controls_annotation_toolbar_tool_freehand ||
                button.getId() == R.id.controls_annotation_toolbar_tool_freetext ||
                button.getId() == R.id.controls_annotation_toolbar_tool_callout) {
            return true;
        }
        return false;
    }

    /**
     * Toggles the expanded mode in annotation toolbar
     */
    public void toggleExpanded() {
        mShouldExpand = !mShouldExpand;
        PdfViewCtrlSettingsManager.updateDoubleRowToolbarInUse(getContext(), mShouldExpand);
        updateExpanded(getResources().getConfiguration().orientation);
    }

    /**
     * Updates the expanded mode
     */
    private void updateExpanded(int orientation) {
        Context context = getContext();
        if (context == null || mToolManager == null) {
            return;
        }

        mIsExpanded = mShouldExpand && orientation == Configuration.ORIENTATION_PORTRAIT && !Utils.isTablet(context);

        if (!mIsExpanded) {
            if (mSelectedButtonId == R.id.controls_annotation_toolbar_tool_image_stamper) {
                mStampState = STATE_IMAGE_STAMP;
            } else if (mSelectedButtonId == R.id.controls_annotation_toolbar_tool_rubber_stamper) {
                mStampState = STATE_RUBBER_STAMP;
            }
        } else {
            mStampState = STATE_SIGNATURE;
        }
        checkStampState();

        ViewGroup root = findViewById(R.id.controls_annotation_toolbar_state_normal);
        int nextLayoutRes = mIsExpanded ? R.layout.controls_annotation_toolbar_expanded_layout
                : R.layout.controls_annotation_toolbar_collapsed_layout;

        // calculate next layout height
        int height;
        if (!mIsExpanded) {
            int[] attrs = new int[]{R.attr.actionBarSize};
            TypedArray ta = context.obtainStyledAttributes(attrs);
            try {
                height = ta.getDimensionPixelSize(0, (int) Utils.convDp2Pix(context, 56));
            } finally {
                ta.recycle();
            }
        } else {
            height = ViewGroup.LayoutParams.WRAP_CONTENT;
        }

        View nextLayout = LayoutInflater.from(context).inflate(nextLayoutRes, null);
        nextLayout.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height));

        // transition animation
        TransitionSet transitionSet = new TransitionSet();
        transitionSet.addTransition(new ChangeBounds());
        transitionSet.addTransition(new Fade());
        TransitionManager.beginDelayedTransition((ViewGroup) getParent(), transitionSet);
        root.removeViewAt(0);
        root.addView(nextLayout);

        initButtons();
        updateButtonsVisibility();
        initSelectedButton();
    }

    public void showFormToolbar(@Nullable ToolMode toolMode, int mode) {
        hideAnnotationToolbar();
        mFormToolbar = findViewById(R.id.controls_form_toolbar);
        mFormToolbar.setup(mToolManager);
        mFormToolbar.setMode(mode);
        mFormToolbar.setButtonStayDown(mButtonStayDown);
        mFormToolbar.setFormToolbarListener(this);
        mFormToolbar.show(toolMode);
    }

    public void showEditToolbar(@NonNull ToolMode toolMode) {
        showEditToolbar(toolMode, null, 0);
    }

    /**
     * The overloaded implementation of {@link AdvancedShapeCreate.OnEditToolbarListener#showEditToolbar(ToolMode, Annot, int)}.
     */
    @Override
    public void showEditToolbar(
            @NonNull ToolMode toolMode,
            @Nullable Annot inkAnnot, int pageNum) {

        FragmentActivity activity = mToolManager.getCurrentActivity();
        if (activity == null || isInEditMode()) {
            return;
        }
        setBackgroundColor(0); // not show toolbar background when starts with edit mode
        hideAnnotationToolbar();
        EditToolbar editToolbar = findViewById(R.id.controls_annotation_toolbar_state_edit);
        mEditToolbarImpl = new EditToolbarImpl(activity, editToolbar, mToolManager, toolMode, inkAnnot, pageNum, mShouldExpand);
        mEditToolbarImpl.setOnEditToolbarListener(this);
        mEditToolbarImpl.showToolbar();
    }

    /**
     * The overloaded implementation of {@link AdvancedShapeCreate.OnEditToolbarListener#closeEditToolbar()}.
     */
    @Override
    public void closeEditToolbar() {

        if (mEditToolbarImpl != null) {
            mEditToolbarImpl.close();
        }
    }

    /**
     * The overloaded implementation of {@link EditToolbarImpl.OnEditToolbarListener#onEditToolbarDismissed()}.
     */
    @Override
    public void onEditToolbarDismissed() {

        if (mToolManager == null) {
            return;
        }

        setBackgroundColor(mToolbarBackgroundColor); // revert back the toolbar background color
        if (mDismissAfterExitEdit) {
            close();
        } else {
            showAnnotationToolbar();
        }
        ToolManager.Tool tool = mToolManager.getTool();
        if (tool == null) {
            return;
        }
        ToolMode toolMode = ToolManager.getDefaultToolMode(tool.getToolMode());
        if (toolMode == INK_CREATE) {
            mToolManager.setTool(mToolManager.createTool(ToolMode.PAN, null));
            selectButton(R.id.controls_annotation_toolbar_tool_pan);
        } else {
            // otherwise stay in the same tool
            mToolManager.setTool(mToolManager.createTool(toolMode, tool));
            selectTool(null, getResourceIdOfTool(toolMode));
        }
    }

    @Override
    public void onFormToolbarWillClose() {
        close();
    }

    /**
     * Callback interface to be invoked when an interaction is needed.
     */
    public interface AnnotationToolbarListener {
        /**
         * Called when the annotation toolbar has been shown.
         */
        void onAnnotationToolbarShown();

        /**
         * Called when the annotation toolbar has been closed.
         */
        void onAnnotationToolbarClosed();

        /**
         * The implementation should show the annotation toolbar starting with the certain mode.
         * The listener may do additional checks such as checking whether the document is read-only
         * or has write access before showing the annotation toolbar.
         *
         * @param mode The mode that annotation toolbar should start with. Possible values are
         *             {@link AnnotationToolbar#START_MODE_NORMAL_TOOLBAR},
         *             {@link AnnotationToolbar#START_MODE_EDIT_TOOLBAR},
         *             {@link AnnotationToolbar#START_MODE_FORM_TOOLBAR}
         */
        void onShowAnnotationToolbarByShortcut(final int mode);
    }

    private void checkStampState() {

        if (STATE_RUBBER_STAMP.equals(mStampState)
                && mToolManager.isToolModeDisabled(RUBBER_STAMPER)) {
            AnalyticsHandlerAdapter.getInstance().sendException(
                    new Exception("rubber stamper is selected while it is disabled"));
            mStampState = STATE_SIGNATURE;
        }
        if (STATE_IMAGE_STAMP.equals(mStampState)
                && mToolManager.isToolModeDisabled(STAMPER)) {
            AnalyticsHandlerAdapter.getInstance().sendException(
                    new Exception("image stamper is selected while it is disabled"));
            mStampState = STATE_SIGNATURE;
        }
        if (STATE_SIGNATURE.equals(mStampState)
                && mToolManager.isToolModeDisabled(SIGNATURE)) {
            AnalyticsHandlerAdapter.getInstance().sendException(
                    new Exception("signature is selected while it is disabled"));
            mStampState = STATE_IMAGE_STAMP;
        }
    }

    private int getToolWidth() {

        Context context = getContext();
        if (context == null) {
            return 0;
        }

        int numIcons = NUM_PHONE_NORMAL_STATE_ICONS;
        if (Utils.isLandscape(context) || Utils.isTablet(context)) {
            numIcons = NUM_TABLET_NORMAL_STATE_ICONS;
        }
        int width = getWidth() / numIcons;
        View panBtn = findViewById(R.id.controls_annotation_toolbar_tool_pan);
        if (mIsExpanded && panBtn != null) {
            panBtn.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
            width = panBtn.getMeasuredWidth();
        }

        return width;
    }

    private int getToolHeight() {

        int height = getHeight();
        View panBtn = findViewById(R.id.controls_annotation_toolbar_tool_pan);
        if (mIsExpanded && panBtn != null) {
            panBtn.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
            height = panBtn.getMeasuredHeight();
        }

        return height;
    }
}
