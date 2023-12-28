package com.pronovoscm.materialchips.adapter;


import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import androidx.annotation.NonNull;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import com.pronovoscm.R;
import com.pronovoscm.materialchips.ChipsInput;
import com.pronovoscm.materialchips.model.ChipInterface;
import com.pronovoscm.materialchips.util.ColorUtil;
import com.pronovoscm.materialchips.util.LetterTileProvider;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;


import static android.view.View.GONE;

public class FilterableAdapter extends ArrayAdapter {

    private static final String TAG = FilterableAdapter.class.toString();
    // context
    private Context mContext;
    // list
    private List<ChipInterface> mOriginalList = new ArrayList<>();
    private List<ChipInterface> mChipList = new ArrayList<>();
    private List<ChipInterface> mFilteredList = new ArrayList<>();
    private ChipFilter mFilter;
    private ChipsInput mChipsInput;
    private LetterTileProvider mLetterTileProvider;
    private ColorStateList mBackgroundColor;
    private ColorStateList mTextColor;
    // sort
    private Comparator<ChipInterface> mComparator;
    private Collator mCollator;


    public FilterableAdapter(Context context,
                             List<? extends ChipInterface> chipList,
                             ChipsInput chipsInput,
                             ColorStateList backgroundColor,
                             ColorStateList textColor) {
        super(context, R.layout.chip_item_list_filterable, chipList);

        mContext = context;
        mCollator = Collator.getInstance(Locale.getDefault());
        mCollator.setStrength(Collator.PRIMARY);
        mComparator = new Comparator<ChipInterface>() {
            @Override
            public int compare(ChipInterface o1, ChipInterface o2) {
                return mCollator.compare(o1.getLabel(), o2.getLabel());
            }
        };
        // remove chips that do not have label
        Iterator<? extends ChipInterface> iterator = chipList.iterator();
        while(iterator.hasNext()) {
            if(iterator.next().getLabel() == null)
                iterator.remove();
        }
        sortList(chipList);
        mOriginalList.addAll(chipList);
        mChipList.addAll(chipList);
        mFilteredList.addAll(chipList);
        mLetterTileProvider = new LetterTileProvider(mContext);
        mBackgroundColor = backgroundColor;
        mTextColor = textColor;
        mChipsInput = chipsInput;

        mChipsInput.addChipsListener(new ChipsInput.ChipsListener() {
            @Override
            public void onChipAdded(ChipInterface chip, int newSize) {
                removeChip(chip);
            }

            @Override
            public void onChipRemoved(ChipInterface chip, int newSize) {
                addChip(chip);
            }

            @Override
            public void onTextChanged(CharSequence text) {

            }
        });
    }

    @Override
    public View getView(int position, View view, @NonNull ViewGroup parent) {

        if (view == null) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chip_item_list_filterable, parent, false);
        }
        final ChipInterface chip = getItem(position);
        TextView mLabel =view.findViewById(R.id.label);
        TextView mInfo = view.findViewById(R.id.info);

        mLabel.setText(chip.getLabel());

        // info
        if(chip.getInfo() != null) {
            mInfo.setVisibility(View.VISIBLE);
            mInfo.setText(chip.getInfo());
        }
        else {
            mInfo.setVisibility(GONE);
        }

        mInfo.setEllipsize(TextUtils.TruncateAt.END);
        // colors
        if(mBackgroundColor != null)
            view.getBackground().setColorFilter(mBackgroundColor.getDefaultColor(), PorterDuff.Mode.SRC_ATOP);
        if(mTextColor != null) {
            mLabel.setTextColor(mTextColor);
            mInfo.setTextColor(ColorUtil.alpha(mTextColor.getDefaultColor(), 150));
        }

        // onclick
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mChipsInput != null)
                    mChipsInput.addChip(chip);
            }
        });
        return view;
    }


    @Override
    public int getCount() {
        return mFilteredList.size();
    }

    public ChipInterface getItem(int position) {
        return mFilteredList.get(position);
    }

    @Override
    public Filter getFilter() {
        if(mFilter == null)
            mFilter = new ChipFilter( mChipList);
        return mFilter;
    }

    private class ChipFilter extends Filter {

        private List<ChipInterface> originalList;
        private List<ChipInterface> filteredList;

        public ChipFilter( List<ChipInterface> originalList) {
            super();
            this.originalList = originalList;
            this.filteredList = new ArrayList<>();
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            filteredList.clear();
            FilterResults results = new FilterResults();
            if (constraint!=null){
                if (constraint.length() == 0) {
                    filteredList.addAll(originalList);
                } else {
                    final String filterPattern = constraint.toString().toLowerCase().trim();
                    for (ChipInterface chip : originalList) {
                        if (chip.getLabel().toLowerCase().contains(filterPattern)) {
                            filteredList.add(chip);
                        }
                        else if(chip.getInfo() != null && chip.getInfo().toLowerCase().replaceAll("\\s", "").contains(filterPattern)) {
                            filteredList.add(chip);
                        }
                    }
                }
            }


            results.values = filteredList;
            results.count = filteredList.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mFilteredList.clear();
            mFilteredList.addAll((ArrayList<ChipInterface>) results.values);
            notifyDataSetChanged();
        }
    }

    private void removeChip(ChipInterface chip) {
        int position = mFilteredList.indexOf(chip);
        if (position >= 0)
            mFilteredList.remove(position);

        position = mChipList.indexOf(chip);
        if(position >= 0)
            mChipList.remove(position);

        notifyDataSetChanged();
    }

    private void addChip(ChipInterface chip) {
        if(contains(chip)) {
            mChipList.add(chip);
            mFilteredList.add(chip);
            // sort original list
            sortList(mChipList);
            // sort filtered list
            sortList(mFilteredList);

            notifyDataSetChanged();
        }
    }

    private boolean contains(ChipInterface chip) {
        for(ChipInterface item: mOriginalList) {
            if(item.equals(chip))
                return true;
        }
        return false;
    }

    private void sortList(List<? extends ChipInterface> list) {
        Collections.sort(list, mComparator);
    }
}
