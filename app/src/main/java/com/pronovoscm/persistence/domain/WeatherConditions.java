package com.pronovoscm.persistence.domain;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Property;

import java.util.Date;

@Entity(nameInDb = "WeatherConditions")
public class WeatherConditions implements Parcelable {
    @Property(nameInDb = "users_id")
    Integer usersId;
    @Property(nameInDb = "weather_conditions_id")
    Integer weatherConditionsId;
    @Property(nameInDb = "label")
    String label;
    @Property(nameInDb = "updated_date")
    Date updatedDate;
    @Property(nameInDb = "created_date")
    Date createdDate;
    @Generated(hash = 1173177644)
    public WeatherConditions(Integer usersId, Integer weatherConditionsId,
            String label, Date updatedDate, Date createdDate) {
        this.usersId = usersId;
        this.weatherConditionsId = weatherConditionsId;
        this.label = label;
        this.updatedDate = updatedDate;
        this.createdDate = createdDate;
    }
    @Generated(hash = 1768868918)
    public WeatherConditions() {
    }

    public WeatherConditions(WeatherConditions weatherConditions) {
        this.usersId = weatherConditions.usersId;
        this.weatherConditionsId = weatherConditions.weatherConditionsId;
        this.label = weatherConditions.label;
        this.updatedDate = weatherConditions.updatedDate;
        this.createdDate = weatherConditions.createdDate;
    }

    protected WeatherConditions(Parcel in) {
        if (in.readByte() == 0) {
            usersId = null;
        } else {
            usersId = in.readInt();
        }
        if (in.readByte() == 0) {
            weatherConditionsId = null;
        } else {
            weatherConditionsId = in.readInt();
        }
        label = in.readString();
    }

    public static final Creator<WeatherConditions> CREATOR = new Creator<WeatherConditions>() {
        @Override
        public WeatherConditions createFromParcel(Parcel in) {
            return new WeatherConditions(in);
        }

        @Override
        public WeatherConditions[] newArray(int size) {
            return new WeatherConditions[size];
        }
    };

    public Integer getUsersId() {
        return this.usersId;
    }
    public void setUsersId(Integer usersId) {
        this.usersId = usersId;
    }
    public Integer getWeatherConditionsId() {
        return this.weatherConditionsId;
    }
    public void setWeatherConditionsId(Integer weatherConditionsId) {
        this.weatherConditionsId = weatherConditionsId;
    }
    public String getLabel() {
        return this.label;
    }
    public void setLabel(String label) {
        this.label = label;
    }
    public Date getUpdatedDate() {
        return this.updatedDate;
    }
    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }
    public Date getCreatedDate() {
        return this.createdDate;
    }
    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (usersId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(usersId);
        }
        if (weatherConditionsId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(weatherConditionsId);
        }
        dest.writeString(label);
    }
}
