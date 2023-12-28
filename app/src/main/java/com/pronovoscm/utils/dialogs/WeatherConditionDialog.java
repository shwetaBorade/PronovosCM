package com.pronovoscm.utils.dialogs;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pronovoscm.PronovosApplication;
import com.pronovoscm.R;
import com.pronovoscm.adapter.WeatherConditionsAdapter;
import com.pronovoscm.persistence.domain.WeatherConditions;
import com.pronovoscm.persistence.repository.WeatherReportRepository;
import com.pronovoscm.utils.WeatherConditionEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WeatherConditionDialog extends DialogFragment implements View.OnClickListener, WeatherConditionsAdapter.updateWeatherCondition {
    @Inject
    WeatherReportRepository mWeatherReportRepository;

    @BindView(R.id.saveTextView)
    TextView saveTextView;
    @BindView(R.id.cancelTextView)
    TextView cancelTextView;
    @BindView(R.id.tagsRecyclerView)
    RecyclerView tagsRecyclerView;
    private WeatherConditionsAdapter mWeatherConditionsAdapter;
    private ArrayList<String> selectedWeatherConditions;
    private ArrayList<String> cloneWeatherConditions;
    private List<WeatherConditions> weatherConditions;
    private int canAddWorkDetail;

    public static ArrayList<String> cloneList(ArrayList<String> imageTags) {
        ArrayList<String> clonedList = new ArrayList<>(imageTags.size());
        for (String imageTag : imageTags) {
            clonedList.add(imageTag);
        }
        return clonedList;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.Theme_Translucent_Dialog);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.tags_dialog_view, container, false);
        ButterKnife.bind(this, rootview);
        return rootview;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        cancelTextView.setOnClickListener(this);
        saveTextView.setOnClickListener(this);
        selectedWeatherConditions = (ArrayList<String>) getArguments().getSerializable("selected_weather_conditions");
        canAddWorkDetail = getArguments().getInt("canAddWorkDetail");
        cloneWeatherConditions = cloneList(selectedWeatherConditions);
        ((PronovosApplication) getActivity().getApplication()).getDaggerComponent().inject(this);
        weatherConditions = mWeatherReportRepository.getWeatherConditions();
        mWeatherConditionsAdapter = new WeatherConditionsAdapter(this, weatherConditions, cloneWeatherConditions, canAddWorkDetail);
        tagsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        tagsRecyclerView.setAdapter(mWeatherConditionsAdapter);
        if (canAddWorkDetail != 1) {
            saveTextView.setVisibility(View.GONE);
        }

    }

    @Override
    public void onClick(View v) {
        WeatherConditionEvent messageEvent = new WeatherConditionEvent();
        switch (v.getId()) {
            case R.id.saveTextView:
                messageEvent.setWeatherConditions(cloneWeatherConditions);
                EventBus.getDefault().post(messageEvent);
                dismiss();
                break;
            case R.id.cancelTextView:
                dismiss();
                break;
            default:
                break;
        }
    }

    @Override
    public void onUpdateSelectedWeatherConditions(ArrayList<String> selectedTag) {
        cloneWeatherConditions = selectedTag;
    }
}

