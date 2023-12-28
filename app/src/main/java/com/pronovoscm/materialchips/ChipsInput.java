package com.pronovoscm.materialchips;


import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AutoCompleteTextView;

import com.pronovoscm.chipslayoutmanager.ChipsLayoutManager;
import com.pronovoscm.R;
import com.pronovoscm.materialchips.adapter.ChipsAdapter;
import com.pronovoscm.materialchips.adapter.FilterableAdapter;
import com.pronovoscm.materialchips.model.Chip;
import com.pronovoscm.materialchips.model.ChipInterface;
import com.pronovoscm.materialchips.util.ActivityUtil;
import com.pronovoscm.materialchips.util.MyWindowCallback;
import com.pronovoscm.materialchips.util.ViewUtil;
import com.pronovoscm.materialchips.views.DetailedChipView;
import com.pronovoscm.materialchips.views.ScrollViewMaxHeight;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChipsInput extends ScrollViewMaxHeight {

    private static final String TAG = ChipsInput.class.toString();
    // context
    private Context mContext;
    // xml element
    @BindView(R.id.chips_recycler)
    RecyclerView mRecyclerView;
    // adapter
    private ChipsAdapter mChipsAdapter;
    // attributes
    private static final int NONE = -1;
    private String mHint;
    private ColorStateList mHintColor;
    private ColorStateList mTextColor;
    private int mMaxRows = 2;
    private ColorStateList mChipLabelColor;
    private boolean mChipHasAvatarIcon = true;
    private boolean mChipDeletable = false;
    private Drawable mChipDeleteIcon;
    private ColorStateList mChipDeleteIconColor;
    private ColorStateList mChipBackgroundColor;
    private boolean mShowChipDetailed = true;
    private ColorStateList mChipDetailedTextColor;
    private ColorStateList mChipDetailedDeleteIconColor;
    private ColorStateList mChipDetailedBackgroundColor;
    private ColorStateList mFilterableListBackgroundColor;
    private ColorStateList mFilterableListTextColor;
    // chips listener
    private List<ChipsListener> mChipsListenerList = new ArrayList<>();
    private ChipsListener mChipsListener;
    // chip list
    private List<? extends ChipInterface> mChipList;
    // chip validator
    private ChipValidator mChipValidator;

    private AutoCompleteTextView mEditText;

    public ChipsInput(Context context) {
        super(context);
        mContext = context;
        init(null);
    }

    public ChipsInput(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init(attrs);
    }

    /**
     * Inflate the view according to attributes
     *
     * @param attrs the attributes
     */
    private void init(AttributeSet attrs) {
        // inflate layout
        View rootView = View.inflate(getContext(), R.layout.chips_input, this);
        // butter knife
        ButterKnife.bind(this, rootView);

        // attributes
        if(attrs != null) {
            TypedArray a = mContext.getTheme().obtainStyledAttributes(
                    attrs,
                    R.styleable.ChipsInput,
                    0, 0);

            try {
                // hint
                mHint = a.getString(R.styleable.ChipsInput_hint);
                mHintColor = a.getColorStateList(R.styleable.ChipsInput_hintColor);
                mTextColor = a.getColorStateList(R.styleable.ChipsInput_textColor);
                mMaxRows = a.getInteger(R.styleable.ChipsInput_maxRows, 2);
                setMaxHeight(ViewUtil.dpToPx((40 * mMaxRows) + 8));
                //setVerticalScrollBarEnabled(true);
                // chip label color
                mChipLabelColor = a.getColorStateList(R.styleable.ChipsInput_chip_labelColor);
                // chip avatar icon
                mChipHasAvatarIcon = a.getBoolean(R.styleable.ChipsInput_chip_hasAvatarIcon, true);
                // chip delete icon
                mChipDeletable = a.getBoolean(R.styleable.ChipsInput_chip_deletable, false);
                mChipDeleteIconColor = a.getColorStateList(R.styleable.ChipsInput_chip_deleteIconColor);
                int deleteIconId = a.getResourceId(R.styleable.ChipsInput_chip_deleteIcon, NONE);
                if(deleteIconId != NONE) mChipDeleteIcon = ContextCompat.getDrawable(mContext, deleteIconId);
                // chip background color
                mChipBackgroundColor = a.getColorStateList(R.styleable.ChipsInput_chip_backgroundColor);
                // show chip detailed
                mShowChipDetailed = a.getBoolean(R.styleable.ChipsInput_showChipDetailed, true);
                // chip detailed text color
                mChipDetailedTextColor = a.getColorStateList(R.styleable.ChipsInput_chip_detailed_textColor);
                mChipDetailedBackgroundColor = a.getColorStateList(R.styleable.ChipsInput_chip_detailed_backgroundColor);
                mChipDetailedDeleteIconColor = a.getColorStateList(R.styleable.ChipsInput_chip_detailed_deleteIconColor);
                // filterable list
                mFilterableListBackgroundColor = a.getColorStateList(R.styleable.ChipsInput_filterable_list_backgroundColor);
                mFilterableListTextColor = a.getColorStateList(R.styleable.ChipsInput_filterable_list_textColor);
            }
            finally {
                a.recycle();
            }
        }
        mEditText = getEditText();
        // adapter
        mChipsAdapter = new ChipsAdapter(mContext, this, mRecyclerView,mEditText);
        ChipsLayoutManager chipsLayoutManager = ChipsLayoutManager.newBuilder(mContext)
                .setOrientation(ChipsLayoutManager.HORIZONTAL)
                .build();
        mRecyclerView.setLayoutManager(chipsLayoutManager);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setAdapter(mChipsAdapter);

        // set window callback
        // will hide DetailedOpenView and hide keyboard on touch outside
        Activity activity = ActivityUtil.scanForActivity(mContext);
        if(activity == null)
            throw new ClassCastException("android.view.Context cannot be cast to android.app.Activity");

        android.view.Window.Callback mCallBack = (activity).getWindow().getCallback();
        activity.getWindow().setCallback(new MyWindowCallback(mCallBack, activity));
    }

    public void addChip(ChipInterface chip) {
        mChipsAdapter.addChip(chip);
    }
    public void enableEditText(Boolean isEnable){
        mEditText.setEnabled(isEnable);
    }
    public void setMultiSelection(boolean isMultiSelection){
        if (mChipsAdapter!=null)mChipsAdapter.setMultiSelection(isMultiSelection);
    }

    public void addChip(Object id, Drawable icon, String label, String info) {
        Chip chip = new Chip(id, icon, label, info);
        mChipsAdapter.addChip(chip);
    }

    public void addChip(Drawable icon, String label, String info) {
        Chip chip = new Chip(icon, label, info);
        mChipsAdapter.addChip(chip);
    }

    public void addChip(Object id, Uri iconUri, String label, String info) {
        Chip chip = new Chip(id, iconUri, label, info);
        mChipsAdapter.addChip(chip);
    }

    public void addChip(Uri iconUri, String label, String info) {
        Chip chip = new Chip(iconUri, label, info);
        mChipsAdapter.addChip(chip);
    }

    public void addChip(String label, String info) {
        ChipInterface chip = new Chip(label, info);
        mChipsAdapter.addChip(chip);
    }

    public void removeChip(ChipInterface chip) {
        mChipsAdapter.removeChip(chip);
    }

    public void removeChipById(Object id) {
        mChipsAdapter.removeChipById(id);
    }

    public void removeChipByLabel(String label) {
        mChipsAdapter.removeChipByLabel(label);
    }

    public void removeChipByInfo(String info) {
        mChipsAdapter.removeChipByInfo(info);
    }

    public ChipView getChipView() {
        int padding = ViewUtil.dpToPx(4);
        ChipView chipView = new ChipView.Builder(mContext)
                .labelColor(mChipLabelColor)
                .hasAvatarIcon(mChipHasAvatarIcon)
                .deletable(mChipDeletable)
                .deleteIcon(mChipDeleteIcon)
                .deleteIconColor(mChipDeleteIconColor)
                .backgroundColor(mChipBackgroundColor)
                .build();

        chipView.setPadding(padding, padding, padding, padding);

        return chipView;
    }

    public AutoCompleteTextView getEditText() {
        LayoutInflater vi = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        AutoCompleteTextView editText = (AutoCompleteTextView)vi.inflate(R.layout.item_autocomplete, null);
        Typeface typeface = ResourcesCompat.getFont(mContext, R.font.source_sans_pro);
        editText.setTypeface(typeface);
        return editText;
    }

    public DetailedChipView getDetailedChipView(ChipInterface chip) {
        return new DetailedChipView.Builder(mContext)
                .chip(chip)
                .textColor(mChipDetailedTextColor)
                .backgroundColor(mChipDetailedBackgroundColor)
                .deleteIconColor(mChipDetailedDeleteIconColor)
                .build();
    }

    public void addChipsListener(ChipsListener chipsListener) {
        mChipsListenerList.add(chipsListener);
        mChipsListener = chipsListener;
    }

    public void onChipAdded(ChipInterface chip, int size) {
        for(ChipsListener chipsListener: mChipsListenerList) {
            chipsListener.onChipAdded(chip, size);
        }
    }

    public void onChipRemoved(ChipInterface chip, int size) {
        for(ChipsListener chipsListener: mChipsListenerList) {
            chipsListener.onChipRemoved(chip, size);
        }
    }

    public void onTextChanged(CharSequence text) {
        if(mChipsListener != null) {
            for(ChipsListener chipsListener: mChipsListenerList) {
                chipsListener.onTextChanged(text);
            }
        }
    }

    public List<? extends ChipInterface> getSelectedChipList() {
        return mChipsAdapter.getChipList();
    }

    public String getHint() {
        return mHint;
    }

    public void setHint(String mHint) {
        this.mHint = mHint;
    }

    public void setHintColor(ColorStateList mHintColor) {
        this.mHintColor = mHintColor;
    }

    public void setTextColor(ColorStateList mTextColor) {
        this.mTextColor = mTextColor;
    }

    public ChipsInput setMaxRows(int mMaxRows) {
        this.mMaxRows = mMaxRows;
        return this;
    }

    public void setChipLabelColor(ColorStateList mLabelColor) {
        this.mChipLabelColor = mLabelColor;
    }

    public void setChipHasAvatarIcon(boolean mHasAvatarIcon) {
        this.mChipHasAvatarIcon = mHasAvatarIcon;
    }

    public boolean chipHasAvatarIcon() {
        return mChipHasAvatarIcon;
    }

    public void setChipDeletable(boolean mDeletable) {
        this.mChipDeletable = mDeletable;
    }

    public void setChipDeleteIcon(Drawable mDeleteIcon) {
        this.mChipDeleteIcon = mDeleteIcon;
    }

    public void setChipDeleteIconColor(ColorStateList mDeleteIconColor) {
        this.mChipDeleteIconColor = mDeleteIconColor;
    }

    public void setChipBackgroundColor(ColorStateList mBackgroundColor) {
        this.mChipBackgroundColor = mBackgroundColor;
    }

    public ChipsInput setShowChipDetailed(boolean mShowChipDetailed) {
        this.mShowChipDetailed = mShowChipDetailed;
        return this;
    }

    public boolean isShowChipDetailed() {
        return mShowChipDetailed;
    }

    public void setChipDetailedTextColor(ColorStateList mChipDetailedTextColor) {
        this.mChipDetailedTextColor = mChipDetailedTextColor;
    }

    public void setChipDetailedDeleteIconColor(ColorStateList mChipDetailedDeleteIconColor) {
        this.mChipDetailedDeleteIconColor = mChipDetailedDeleteIconColor;
    }

    public void setChipDetailedBackgroundColor(ColorStateList mChipDetailedBackgroundColor) {
        this.mChipDetailedBackgroundColor = mChipDetailedBackgroundColor;
    }
    FilterableAdapter mAdapter;
    public void setFilterableList(List<? extends ChipInterface> list) {

        mChipList = list;
        mAdapter = new FilterableAdapter(mContext,  list, this, mFilterableListBackgroundColor, mFilterableListTextColor);
        mEditText.setSelectAllOnFocus(true);
        mEditText.setThreshold(1);
        mEditText.setAdapter(mAdapter);

    }

    public List<? extends ChipInterface> getFilterableList() {
        return mChipList;
    }

    public ChipValidator getChipValidator() {
        return mChipValidator;
    }

    public void setChipValidator(ChipValidator mChipValidator) {
        this.mChipValidator = mChipValidator;
    }

    public interface ChipsListener {
        void onChipAdded(ChipInterface chip, int newSize);
        void onChipRemoved(ChipInterface chip, int newSize);
        void onTextChanged(CharSequence text);
    }

    public interface ChipValidator {
        boolean areEquals(ChipInterface chip1, ChipInterface chip2);
    }

    public void requestEditFocus() {
        if (mChipsAdapter != null) {
            mChipsAdapter.requestFocus();
        }
    }

    public void dismissDropDown() {
        if (mEditText != null) {
            mEditText.dismissDropDown();
        }
    }
}
