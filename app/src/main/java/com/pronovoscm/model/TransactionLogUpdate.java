package com.pronovoscm.model;

import com.pronovoscm.persistence.domain.WeatherReport;

public class TransactionLogUpdate {
    private TransactionModuleEnum transactionModuleEnum;
    private WeatherReport weatherReport;
    private int drawingId;

    public TransactionModuleEnum getTransactionModuleEnum() {
        return transactionModuleEnum;
    }

    public void setTransactionModuleEnum(TransactionModuleEnum transactionModuleEnum) {
        this.transactionModuleEnum = transactionModuleEnum;
    }

    public int getDrawingId() {
        return drawingId;
    }

    public void setDrawingId(int drawingId) {
        this.drawingId = drawingId;
    }

    public WeatherReport getWeatherReport() {
        return weatherReport;
    }

    public void setWeatherReport(WeatherReport weatherReport) {
        this.weatherReport = weatherReport;
    }
}
