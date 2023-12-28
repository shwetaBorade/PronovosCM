package com.pronovoscm.model.response.darksky;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Hourly{

	@SerializedName("summary")
	private String summary;

	@SerializedName("data")
	private List<HourlyReport> hourlyReportList;

	@SerializedName("icon")
	private String icon;

	public void setSummary(String summary){
		this.summary = summary;
	}

	public String getSummary(){
		return summary;
	}

	public List<HourlyReport> getHourlyReportList() {
		return hourlyReportList;
	}

	public void setHourlyReportList(List<HourlyReport> hourlyReportList) {
		this.hourlyReportList = hourlyReportList;
	}

	public void setIcon(String icon){
		this.icon = icon;
	}

	public String getIcon(){
		return icon;
	}

	@Override
 	public String toString(){
		return 
			"Hourly{" + 
			"summary = '" + summary + '\'' + 
			",data = '" + hourlyReportList + '\'' +
			",icon = '" + icon + '\'' + 
			"}";
		}
}