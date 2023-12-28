//------------------------------------------------------------------------------
// Copyright (c) 2001-2019 by PDFTron Systems Inc. All Rights Reserved.
// Consult legal.txt regarding legal and license information.
//------------------------------------------------------------------------------

package com.pdftron.pdf.controls;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.PagerAdapter;

import com.google.android.material.tabs.TabLayout;
import com.pdftron.pdf.Annot;
import com.pdftron.pdf.model.AnnotStyle;
import com.pdftron.pdf.model.AnnotStyleProperty;
import com.pdftron.pdf.tools.R;
import com.pdftron.pdf.utils.AnalyticsAnnotStylePicker;
import com.pdftron.pdf.utils.AnalyticsHandlerAdapter;
import com.pdftron.pdf.utils.AnalyticsParam;
import com.pdftron.pdf.utils.AnnotationPropertyPreviewView;
import com.pdftron.pdf.utils.ColorPickerGridViewAdapter;
import com.pdftron.pdf.utils.PdfViewCtrlSettingsManager;
import com.pdftron.pdf.utils.Utils;
import com.pdftron.pdf.widget.CustomViewPager;

import java.util.ArrayList;
import java.util.HashMap;

import static com.pdftron.pdf.controls.AnnotStyleDialogFragment.COLOR;
import static com.pdftron.pdf.controls.AnnotStyleDialogFragment.FILL_COLOR;
import static com.pdftron.pdf.controls.AnnotStyleDialogFragment.STROKE_COLOR;
import static com.pdftron.pdf.controls.AnnotStyleDialogFragment.TEXT_COLOR;

/**
 * A Linear layout for changing annotation color. It contains a annotation preview
 * and three pages: {@link CustomColorPickerView}, {@link PresetColorGridView}, and {@link AdvancedColorView}.
 */
public class ColorPickerView extends LinearLayout implements CustomColorPickerView.OnEditFavoriteColorListener {
    // UI
    private LinearLayout mToolbar;
    private ImageButton mBackButton;
    private ImageButton mArrowBackward;
    private ImageButton mArrowForward;
    private TextView mToolbarTitle;
    private ImageButton mEditButton;
    private ImageButton mRemoveButton;
    private ImageButton mAddFavButton;
    private CustomViewPager mColorPager;
    private ColorPagerAdapter mColorPagerAdapter;
    private CharSequence mToolbarText;
    // View Pager views
    private PresetColorGridView mPresetColorView;
    private CustomColorPickerView mCustomColorView;
    private AdvancedColorView mAdvancedColorView;
    private TabLayout mPagerIndicator;
    private String mLatestAdvancedColor;
    private AnnotStyle.AnnotStyleHolder mAnnotStyleHolder;

    private OnBackButtonPressedListener mBackPressedListener;

    private @AnnotStyleDialogFragment.SelectColorMode
    int mColorMode = AnnotStyleDialogFragment.COLOR;

    private ArrayList<String> mSelectedAddFavoriteColors;
    private ColorPickerGridViewAdapter mAddFavoriteAdapter;
    private AnnotStyleDialogFragment.Theme mTheme;

    @Nullable
    private HashMap<Integer, AnnotStyleProperty> mAnnotStyleProperties;

    /**
     * Class constructor
     */
    public ColorPickerView(Context context) {
        this(context, null);
    }

    /**
     * Class constructor
     */
    public ColorPickerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * Class constructor
     */
    public ColorPickerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mTheme = AnnotStyleDialogFragment.Theme.fromContext(getContext());
        LayoutInflater.from(getContext()).inflate(R.layout.color_picker_layout, this);
        setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        setOrientation(VERTICAL);
        mToolbar = findViewById(R.id.toolbar);
        mBackButton = findViewById(R.id.back_btn);
        mBackButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackButtonPressed();
            }
        });
        mArrowBackward = findViewById(R.id.nav_backward);
        mArrowBackward.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mColorPager == null) {
                    return;
                }
                int index = mColorPager.getCurrentItem();
                int prevIndex = index - 1;
                index = Math.max(0, prevIndex);
                mColorPager.setCurrentItem(index);
                setArrowVisibility(index);
            }
        });
        mArrowForward = findViewById(R.id.nav_forward);
        mArrowForward.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mColorPager == null || mColorPagerAdapter == null) {
                    return;
                }
                int index = mColorPager.getCurrentItem();
                int nextIndex = index + 1;
                int lastIndex = mColorPagerAdapter.getCount() - 1;
                index = Math.min(lastIndex, nextIndex);
                mColorPager.setCurrentItem(index);
                setArrowVisibility(index);
            }
        });
        mToolbarTitle = findViewById(R.id.toolbar_title);
        mColorPager = findViewById(R.id.color_pager);
        mPagerIndicator = findViewById(R.id.pager_indicator_tabs);
        mPagerIndicator.addOnTabSelectedListener(new TabLayout.BaseOnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                setArrowVisibility(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        mPresetColorView = new PresetColorGridView(getContext());
        mAdvancedColorView = new AdvancedColorView(getContext());
        mCustomColorView = new CustomColorPickerView(getContext());
        mRemoveButton = mToolbar.findViewById(R.id.remove_btn);
        mEditButton = mToolbar.findViewById(R.id.edit_btn);
        mAddFavButton = mToolbar.findViewById(R.id.fav_btn);
        mEditButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mCustomColorView.editSelectedColor();
            }
        });
        mRemoveButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mCustomColorView.deleteAllSelectedFavColors();
            }
        });
        mAddFavButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                addColorsToFavorites();
            }
        });
//        setBackgroundColor(Utils.getThemeAttrColor(getContext(), android.R.attr.colorBackground));

        // layout params
        MarginLayoutParams mlp = new MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        mPresetColorView.setLayoutParams(mlp);
        mPresetColorView.setClipToPadding(false);

        mColorPagerAdapter = new ColorPagerAdapter();
        mColorPager.setAdapter(mColorPagerAdapter);
        int currentItem = PdfViewCtrlSettingsManager.getColorPickerPage(getContext());
        mColorPager.setCurrentItem(currentItem);
        mPagerIndicator.setupWithViewPager(mColorPager, true);
        setArrowVisibility(currentItem);

        // add color change listener
        mAdvancedColorView.setOnColorChangeListener(new OnColorChangeListener() {
            @Override
            public void OnColorChanged(View view, int color) {
                onColorChanged(view, color);
            }
        });
        mCustomColorView.setOnColorChangeListener(new OnColorChangeListener() {
            @Override
            public void OnColorChanged(View view, int color) {
                onColorChanged(view, color);
            }
        });
        mCustomColorView.setOnEditFavoriteColorlistener(this);
        mCustomColorView.setRecentColorLongPressListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                return onColorItemLongClickListener(parent, position);
            }
        });

        mBackButton.setColorFilter(mTheme.iconColor, PorterDuff.Mode.SRC_IN);
        mArrowBackward.setColorFilter(mTheme.iconColor, PorterDuff.Mode.SRC_IN);
        mArrowForward.setColorFilter(mTheme.iconColor, PorterDuff.Mode.SRC_IN);
    }

    /**
     * Sets annotation style holder
     *
     * @param annotStyleHolder annotation style holder
     */
    public void setAnnotStyleHolder(AnnotStyle.AnnotStyleHolder annotStyleHolder) {
        mAnnotStyleHolder = annotStyleHolder;
    }

    private AnnotStyle getAnnotStyle() {
        return mAnnotStyleHolder.getAnnotStyle();
    }

    private AnnotationPropertyPreviewView getAnnotStylePreview() {
        return mAnnotStyleHolder.getAnnotPreview();
    }

    /**
     * Sets the AnnotStyleProperties that will be used to hide elements of the AnnotStyleDialog.
     *
     * @param annotStyleProperties hash map of annot types and the AnnotStyleProperties
     */
    public void setAnnotStyleProperties(@Nullable HashMap<Integer, AnnotStyleProperty> annotStyleProperties) {
        mAnnotStyleProperties = annotStyleProperties;

        AnnotStyleProperty annotStyleProperty = getAnnotStyleProperty();
        if (annotStyleProperty != null) {
            if (!annotStyleProperty.canShowSavedColorPicker() && !annotStyleProperty.canShowAdvancedColorPicker()) {
                mPagerIndicator.setVisibility(GONE);
            }
        }
        mColorPagerAdapter.notifyDataSetChanged();
    }

    //fixes the colour wheel cut off issue in the alert dialog
    public void setIsDialogLayout(boolean isDialogLayout) {
        mAdvancedColorView.setIsDialogLayout(isDialogLayout);
    }

    @Nullable
    private AnnotStyleProperty getAnnotStyleProperty() {
        if (mAnnotStyleHolder != null) {
            Integer type = getAnnotStyle().getAnnotType();
            return mAnnotStyleProperties != null ? mAnnotStyleProperties.get(type) : null;
        }
        return null;
    }

    /**
     * Show color picker view with given colorMode, must be one of:
     * {@link com.pdftron.pdf.controls.AnnotStyleDialogFragment.SelectColorMode#STROKE_COLOR},
     * {@link com.pdftron.pdf.controls.AnnotStyleDialogFragment.SelectColorMode#FILL_COLOR},
     * {@link com.pdftron.pdf.controls.AnnotStyleDialogFragment.SelectColorMode#TEXT_COLOR},
     * {@link com.pdftron.pdf.controls.AnnotStyleDialogFragment.SelectColorMode#COLOR}
     *
     * @param colorMode color mode
     */
    public void show(@AnnotStyleDialogFragment.SelectColorMode int colorMode) {
        AnalyticsAnnotStylePicker.getInstance().setSelectedColorMode(colorMode);
        mColorMode = colorMode;

        AnnotStyle annotStyle = getAnnotStyle();
        getAnnotStylePreview().setAnnotType(annotStyle.getAnnotType());
        getAnnotStylePreview().updateFillPreview(annotStyle);

        // If highlight annotation related, then show a different set of highlight colors
        boolean showhighlightColors = annotStyle != null &&
                (annotStyle.getAnnotType() == Annot.e_Highlight
                        || annotStyle.getAnnotType() == AnnotStyle.CUSTOM_ANNOT_TYPE_FREE_HIGHLIGHTER);
        mPresetColorView.setShowHighlightColors(showhighlightColors);

        // don't show transparent color if the annotation has icon
        boolean presetShowTransparent;
        switch (colorMode) {
            case STROKE_COLOR:
                presetShowTransparent = true;
                setSelectedColor(annotStyle.getColor());
                mToolbarTitle.setText(R.string.tools_qm_stroke_color);
                break;
            case FILL_COLOR:
                presetShowTransparent = true;
                setSelectedColor(annotStyle.getFillColor());
                if (annotStyle.isFreeText()) {
                    mToolbarTitle.setText(R.string.pref_colormode_custom_bg_color);
                } else {
                    mToolbarTitle.setText(R.string.tools_qm_fill_color);
                }
                break;
            case TEXT_COLOR:
                presetShowTransparent = false;
                setSelectedColor(annotStyle.getTextColor());
                mToolbarTitle.setText(R.string.pref_colormode_custom_text_color);
                break;
            case COLOR:
            default:
                presetShowTransparent = annotStyle.hasFillColor();
                setSelectedColor(annotStyle.getColor());
                mToolbarTitle.setText(R.string.tools_qm_color);
                break;
        }
        mPresetColorView.showTransparentColor(presetShowTransparent);
        mPresetColorView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onPresetColorGridItemClicked(parent, position);
            }
        });
        mPresetColorView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                return onColorItemLongClickListener(parent, position);
            }
        });

        mToolbarText = mToolbarTitle.getText();

        setVisibility(VISIBLE);
    }

    /**
     * Hide Color picker view
     */
    public void dismiss() {
        setVisibility(GONE);
    }

    private void onBackButtonPressed() {
        if (mCustomColorView.onBackButonPressed()) {
            return;
        }
        if (mSelectedAddFavoriteColors != null && !mSelectedAddFavoriteColors.isEmpty()) {
            mSelectedAddFavoriteColors.clear();
            if (mAddFavoriteAdapter != null) {
                mAddFavoriteAdapter.notifyDataSetChanged();
            }
            toggleFavoriteToolbar();
            return;
        }
        if (!Utils.isNullOrEmpty(mLatestAdvancedColor)) {
            mCustomColorView.addRecentColorSource(mLatestAdvancedColor);
            AnalyticsAnnotStylePicker.getInstance().selectColor(mLatestAdvancedColor.toUpperCase(), AnalyticsHandlerAdapter.STYLE_PICKER_COLOR_WHEEL);
        }
        if (mBackPressedListener != null) {
            mBackPressedListener.onBackPressed();
        }
    }

    /**
     * Sets selected color.
     * If selected color matches any of the color grids, it will show white check mark.
     *
     * @param color color
     */
    public void setSelectedColor(@ColorInt int color) {
        mAdvancedColorView.setSelectedColor(color);
        mPresetColorView.setSelectedColor(color);
        mCustomColorView.setSelectedColor(Utils.getColorHexString(color));
    }

    /**
     * Sets on back button pressed listener
     *
     * @param listener back button pressed listener
     */
    public void setOnBackButtonPressedListener(OnBackButtonPressedListener listener) {
        mBackPressedListener = listener;
    }

    private void onColorChanged(View view, @ColorInt int color) {
        switch (mColorMode) {
            case FILL_COLOR:
                getAnnotStyle().setFillColor(color);
                break;
            case TEXT_COLOR:
                getAnnotStyle().setTextColor(color);
                break;
            case STROKE_COLOR:
            case COLOR:
            default:
                getAnnotStyle().setStrokeColor(color);
                break;
        }
        getAnnotStylePreview().updateFillPreview(getAnnotStyle());
        String colorStr = Utils.getColorHexString(color);
        if (view != mPresetColorView) {
            mPresetColorView.setSelectedColor(colorStr);
        } else {
            AnalyticsAnnotStylePicker.getInstance().selectColor(colorStr, AnalyticsHandlerAdapter.STYLE_PICKER_STANDARD);
        }
        if (view != mCustomColorView) {
            mCustomColorView.setSelectedColor(colorStr);
        }

        String source = color == Color.TRANSPARENT ? ColorPickerGridViewAdapter.TYPE_TRANSPARENT
                : Utils.getColorHexString(color);
        if (view != mAdvancedColorView) {
            mAdvancedColorView.setSelectedColor(color);
            mCustomColorView.addRecentColorSource(source);
            mLatestAdvancedColor = "";
        } else {
            mLatestAdvancedColor = source;
        }
    }

    private void onPresetColorGridItemClicked(AdapterView<?> parent, int position) {
        if (mSelectedAddFavoriteColors != null && !mSelectedAddFavoriteColors.isEmpty()
                && onColorItemLongClickListener(parent, position)) {
            return;
        }

        String colorStr = (String) parent.getAdapter().getItem(position);
        if (colorStr == null) {
            return;
        }
        mPresetColorView.setSelectedColor(colorStr);

        int color;
        if (colorStr.equals(ColorPickerGridViewAdapter.TYPE_TRANSPARENT)) {
            color = Color.TRANSPARENT;
            onColorChanged(parent, color);
        } else {
            try {
                color = Color.parseColor(colorStr);
                onColorChanged(parent, color);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean onColorItemLongClickListener(AdapterView<?> parent, int position) {
        ColorPickerGridViewAdapter adapter = (ColorPickerGridViewAdapter) parent.getAdapter();
        String color = adapter.getItem(position);
        if (color == null) {
            return false;
        }
        if (mSelectedAddFavoriteColors == null) {
            mSelectedAddFavoriteColors = new ArrayList<>();
            adapter.setSelectedList(mSelectedAddFavoriteColors);
        }
        if (mSelectedAddFavoriteColors.contains(color)) {
            mSelectedAddFavoriteColors.remove(color);
        } else {
            mSelectedAddFavoriteColors.add(color);
        }
        adapter.notifyDataSetChanged();
        toggleFavoriteToolbar();
        mAddFavoriteAdapter = adapter;
        return true;
    }

    private void addColorsToFavorites() {
        ArrayList<String> allFavorites = new ArrayList<>(mCustomColorView.getFavoriteColors());
        allFavorites.addAll(mSelectedAddFavoriteColors);
        mCustomColorView.setColorsToFavorites(allFavorites, FavoriteColorDialogFragment.ADD_COLOR);
        for (String color : mSelectedAddFavoriteColors) {
            AnalyticsHandlerAdapter.getInstance().sendEvent(AnalyticsHandlerAdapter.EVENT_STYLE_PICKER_ADD_FAVORITE,
                    AnalyticsParam.colorParam(color));
        }
        onBackButtonPressed();
    }

    private void toggleFavoriteToolbar() {
        if (mSelectedAddFavoriteColors == null || mSelectedAddFavoriteColors.isEmpty()) {
            mToolbar.setBackgroundColor(Utils.getThemeAttrColor(getContext(), android.R.attr.colorBackground));
            int textColor = Utils.getThemeAttrColor(getContext(), android.R.attr.textColorPrimary);

            mToolbarTitle.setTextColor(textColor);
            mToolbarTitle.setAlpha(0.54f);
            mToolbarTitle.setText(mToolbarText);

            mAnnotStyleHolder.setAnnotPreviewVisibility(VISIBLE);
            mAddFavButton.setVisibility(GONE);
            mPagerIndicator.setVisibility(VISIBLE);
            mColorPager.setSwippingEnabled(true);
            mBackButton.setImageResource(R.drawable.ic_arrow_back_black_24dp);
            mBackButton.setColorFilter(textColor);
            mBackButton.setAlpha(0.54f);
            mSelectedAddFavoriteColors = null;
            mAddFavoriteAdapter = null;

            mArrowBackward.setVisibility(View.VISIBLE);
            mArrowForward.setVisibility(View.VISIBLE);
        } else {
            mToolbar.setBackgroundColor(Utils.getAccentColor(getContext()));
            mToolbarTitle.setText(getContext().getString(R.string.controls_thumbnails_view_selected,
                    Utils.getLocaleDigits(Integer.toString(mSelectedAddFavoriteColors.size()))));
            int textColor = Utils.getThemeAttrColor(getContext(), android.R.attr.textColorPrimaryInverse);
            mToolbarTitle.setTextColor(textColor);
            mToolbarTitle.setAlpha(1f);
            mAnnotStyleHolder.setAnnotPreviewVisibility(GONE);
            mBackButton.setImageResource(R.drawable.ic_close_black_24dp);
            mBackButton.setColorFilter(textColor);
            mBackButton.setAlpha(1f);
            mColorPager.setSwippingEnabled(false);
            mAddFavButton.setVisibility(VISIBLE);
            mPagerIndicator.setVisibility(INVISIBLE);

            mArrowBackward.setVisibility(View.GONE);
            mArrowForward.setVisibility(View.GONE);
        }
    }

    private void setArrowVisibility(int currentTab) {
        if (mArrowBackward == null || mArrowForward == null ||
                mColorPager == null || mColorPagerAdapter == null) {
            return;
        }
        int lastTab = mColorPagerAdapter.getCount() - 1;
        if (currentTab == lastTab) {
            mArrowForward.setVisibility(View.INVISIBLE);
        } else {
            mArrowForward.setVisibility(View.VISIBLE);
        }
        if (currentTab == 0) {
            mArrowBackward.setVisibility(View.INVISIBLE);
        } else {
            mArrowBackward.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Save colors in custom color view to settings
     */
    public void saveColors() {
        mCustomColorView.saveColors();
        PdfViewCtrlSettingsManager.setColorPickerPage(getContext(), mColorPager.getCurrentItem());
    }

    /**
     * Sets activity to custom color view to show {@link FavoriteColorDialogFragment} fragment
     * when clicked in "Add favorite" button.
     *
     * @param activity the activity
     */
    public void setActivity(FragmentActivity activity) {
        mCustomColorView.setActivity(activity);
    }

    /**
     * Overload implementation of {@link CustomColorPickerView.OnEditFavoriteColorListener#onEditFavoriteItemSelected(int)}
     *
     * @param selectedCount selected favorite color count
     */
    @Override
    public void onEditFavoriteItemSelected(int selectedCount) {
        mToolbar.setBackgroundColor(Utils.getAccentColor(getContext()));
        mToolbarTitle.setText(getContext().getString(R.string.controls_thumbnails_view_selected,
                Utils.getLocaleDigits(Integer.toString(selectedCount))));

        int textColor = Utils.getThemeAttrColor(getContext(), android.R.attr.textColorPrimaryInverse);
        mToolbarTitle.setTextColor(textColor);
        mToolbarTitle.setAlpha(1f);
        mAnnotStyleHolder.setAnnotPreviewVisibility(GONE);
        mBackButton.setImageResource(R.drawable.ic_close_black_24dp);
        mBackButton.setColorFilter(textColor);
        mBackButton.setAlpha(1f);
        mColorPager.setSwippingEnabled(false);
        mRemoveButton.setVisibility(VISIBLE);
        mPagerIndicator.setVisibility(INVISIBLE);
        if (selectedCount == 1) {
            mEditButton.setVisibility(VISIBLE);
        } else {
            mEditButton.setVisibility(GONE);
        }
    }

    /**
     * Overload implementation of {@link CustomColorPickerView.OnEditFavoriteColorListener#onEditFavoriteColorDismissed()} (int)}
     */
    @Override
    public void onEditFavoriteColorDismissed() {
        mToolbar.setBackgroundColor(Utils.getThemeAttrColor(getContext(), android.R.attr.colorBackground));
        int textColor = Utils.getThemeAttrColor(getContext(), android.R.attr.textColorPrimary);

        mToolbarTitle.setTextColor(textColor);
        mToolbarTitle.setAlpha(0.54f);
        mToolbarTitle.setText(mToolbarText);

        mAnnotStyleHolder.setAnnotPreviewVisibility(VISIBLE);
        mRemoveButton.setVisibility(GONE);
        mEditButton.setVisibility(GONE);
        mPagerIndicator.setVisibility(VISIBLE);
        mColorPager.setSwippingEnabled(true);
        mBackButton.setImageResource(R.drawable.ic_arrow_back_black_24dp);
        mBackButton.setColorFilter(textColor);
        mBackButton.setAlpha(0.54f);
    }

    /**
     * A pager adapter to show three pages: {@link CustomColorPickerView}, {@link PresetColorGridView}, and {@link AdvancedColorView}
     */
    protected class ColorPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            int count = 3;
            AnnotStyleProperty annotStyleProperty = getAnnotStyleProperty();
            if (annotStyleProperty != null) {
                if (!annotStyleProperty.canShowSavedColorPicker()) {
                    count--;
                }
                if (!annotStyleProperty.canShowAdvancedColorPicker()) {
                    count--;
                }
            }
            return count;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            View view;
            switch (position) {
                case 0:
                    view = mCustomColorView;
                    break;
                case 1:
                    view = mPresetColorView;
                    break;
                default:
                    view = mAdvancedColorView;
                    break;
            }
            AnnotStyleProperty annotStyleProperty = getAnnotStyleProperty();
            if (annotStyleProperty != null) {
                if (!annotStyleProperty.canShowSavedColorPicker()) {
                    switch (position) {
                        case 0:
                            view = mPresetColorView;
                            break;
                        default:
                            view = mAdvancedColorView;
                            break;
                    }
                }
                if (!annotStyleProperty.canShowAdvancedColorPicker()) {
                    switch (position) {
                        case 0:
                            view = mCustomColorView;
                            break;
                        default:
                            view = mPresetColorView;
                            break;
                    }
                }
                if (!annotStyleProperty.canShowSavedColorPicker() && !annotStyleProperty.canShowAdvancedColorPicker()) {
                    view = mPresetColorView;
                }
            }
            view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }
    }

    /**
     * A interface that is invoked when there is color changes in color picker pages
     */
    public interface OnColorChangeListener {
        /**
         * This method invoked when there is color changed
         *
         * @param view  invoked color picker view that made the color change
         * @param color new color
         */
        void OnColorChanged(View view, @ColorInt int color);
    }

    /**
     * This method is used for back button in toolbar pressed event
     */
    public interface OnBackButtonPressedListener {
        /**
         * This method is invoked when back button in toolbar is pressed
         */
        void onBackPressed();
    }
}
