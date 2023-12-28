package com.pronovoscm.utils;

import com.pronovoscm.persistence.domain.WeatherConditions;

import java.util.ArrayList;

public class WeatherConditionEvent {
    public ArrayList<String> mWeatherConditions;

    public ArrayList<String> getWeatherConditions() {
        return mWeatherConditions;
    }

    public void setWeatherConditions(ArrayList<String> weatherConditions) {
        mWeatherConditions = weatherConditions;
    }
}
