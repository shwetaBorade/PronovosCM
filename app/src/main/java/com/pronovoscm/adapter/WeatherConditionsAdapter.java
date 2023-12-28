package com.pronovoscm.adapter;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.pronovoscm.R;
import com.pronovoscm.persistence.domain.WeatherConditions;
import com.pronovoscm.utils.dialogs.WeatherConditionDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WeatherConditionsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<WeatherConditions> mWeatherConditions;
    private WeatherConditionDialog mWeatherConditionDialog;
    private ArrayList<String> mselectedWeatherCondition;
    private int canAddWorkDetail;

    public WeatherConditionsAdapter(WeatherConditionDialog weatherConditionDialog, List<WeatherConditions> WeatherConditions, ArrayList<String> selectedWeatherCondition, int canAddWorkDetail) {
        this.mWeatherConditions = WeatherConditions;
        this.mselectedWeatherCondition = selectedWeatherCondition;
        this.mWeatherConditionDialog = weatherConditionDialog;
        this.canAddWorkDetail = canAddWorkDetail;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.tags_item_list, parent, false);

        return new WeatherConditionViewHolder(view);

    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        ((WeatherConditionViewHolder) holder).bind(mWeatherConditions.get(position));
    }

    @Override
    public int getItemCount() {
        if (mWeatherConditions != null) {
            return mWeatherConditions.size();
        } else {
            return 0;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public interface updateWeatherCondition {
        void onUpdateSelectedWeatherConditions(ArrayList<String> selectedTag);
    }

    public class WeatherConditionViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tagTextView)
        TextView tagTextView;
        @BindView(R.id.tagCheckBox)
        CheckBox tagCheckBox;
        @BindView(R.id.tagsView)
        ConstraintLayout tagsView;

        public WeatherConditionViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        private void bind(final WeatherConditions weatherConditions) {
            if (weatherConditions != null) {

                tagTextView.setText(weatherConditions.getLabel());
                if (mselectedWeatherCondition.contains(weatherConditions.getLabel())) {
                    tagCheckBox.setChecked(true);
                } else {
                    tagCheckBox.setChecked(false);
                }
                tagsView.setOnClickListener(v -> tagCheckBox.setChecked(!tagCheckBox.isChecked()));
                tagCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (isChecked) {
                        mselectedWeatherCondition.add(weatherConditions.getLabel());
                    } else {
                        mselectedWeatherCondition.remove(weatherConditions.getLabel());
                    }
                    (mWeatherConditionDialog).onUpdateSelectedWeatherConditions(mselectedWeatherCondition);
                });

            if (canAddWorkDetail!=1){
                tagsView.setClickable(false);
                tagCheckBox.setClickable(false);
                tagCheckBox.setEnabled(false);
            }
            }

        }

    }
}