package com.pronovoscm.persistence.domain;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;

import java.util.Date;
import org.greenrobot.greendao.annotation.Generated;

@Entity(nameInDb = "weather_widget")
public class WeatherWidget {

    @Id(autoincrement = true)
    private Long id;
    @Property(nameInDb = "icon")
    private String icon;
    @Property(nameInDb = "users_id")
    Integer usersId;
    @Property(nameInDb = "project_id")
    Integer projectId;
    @Property(nameInDb = "report_date")
    Date reportDate;
    @Property(nameInDb = "summary")
    private String summary;
    @Property(nameInDb = "temperature")
    private double temperature;
    @Property(nameInDb = "time")
    private String time;
    @Generated(hash = 1442290231)
    public WeatherWidget(Long id, String icon, Integer usersId, Integer projectId,
            Date reportDate, String summary, double temperature, String time) {
        this.id = id;
        this.icon = icon;
        this.usersId = usersId;
        this.projectId = projectId;
        this.reportDate = reportDate;
        this.summary = summary;
        this.temperature = temperature;
        this.time = time;
    }
    @Generated(hash = 1311031663)
    public WeatherWidget() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getIcon() {
        return this.icon;
    }
    public void setIcon(String icon) {
        this.icon = icon;
    }
    public Integer getUsersId() {
        return this.usersId;
    }
    public void setUsersId(Integer usersId) {
        this.usersId = usersId;
    }
    public Integer getProjectId() {
        return this.projectId;
    }
    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }
    public Date getReportDate() {
        return this.reportDate;
    }
    public void setReportDate(Date reportDate) {
        this.reportDate = reportDate;
    }
    public String getSummary() {
        return this.summary;
    }
    public void setSummary(String summary) {
        this.summary = summary;
    }
    public double getTemperature() {
        return this.temperature;
    }
    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }
    public String getTime() {
        return this.time;
    }
    public void setTime(String time) {
        this.time = time;
    }

}
