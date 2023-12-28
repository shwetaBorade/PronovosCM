package com.pronovoscm.persistence.domain;


import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.converter.PropertyConverter;

import java.util.Date;

/**
 * Entity mapped to table "event".
 * The application will utilize event table to track the sign-in and out events while the tablet is offline.
 *
 * @author Nitin Bhawsar
 */

@Entity(nameInDb = "offline_event")
public class OfflineEvent {

    @Id(autoincrement = true)
    private Long id;

    /**
     * ID of task sign-in or sign-out event
     */
    @Property(nameInDb = "task_id")
    private Long taskId;
    /**
     * ID of task Activity
     */
    @Property(nameInDb = "activity_id")
    private Long activityId;
    /**
     * Date time the event occurred.
     */
    @Property(nameInDb = "date")
    private Date date;
    /**
     * latitude of event occurred.
     */
    @Property(nameInDb = "latitude")
    private Double latitude;
    /**
     * longitude of event occurred.
     */
    @Property(nameInDb = "longitude")
    private Double longitude;
    /**
     * signin or signout event
     */
    @Convert(converter = OfflineEvent.EventTypeConverter.class, columnType = String.class)
    @Property(nameInDb = "event")
    private OfflineEvent.EventEnum event;
    /**
     * signin or signout event
     */
    @Convert(converter = OfflineEvent.OfflineTypeConverter.class, columnType = String.class)
    @Property(nameInDb = "type")
    @NotNull
    private OfflineEvent.OfflineEventType type;

    @Generated(hash = 853258982)
    public OfflineEvent(Long id, Long taskId, Long activityId, Date date, Double latitude, Double longitude,
                        OfflineEvent.EventEnum event, @NotNull OfflineEvent.OfflineEventType type) {
        this.id = id;
        this.taskId = taskId;
        this.activityId = activityId;
        this.date = date;
        this.latitude = latitude;
        this.longitude = longitude;
        this.event = event;
        this.type = type;
    }

    @Generated(hash = 687013642)
    public OfflineEvent() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTaskId() {
        return this.taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public Long getActivityId() {
        return this.activityId;
    }

    public void setActivityId(Long activityId) {
        this.activityId = activityId;
    }

    public Date getDate() {
        return this.date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Double getLatitude() {
        return this.latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return this.longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public OfflineEvent.EventEnum getEvent() {
        return this.event;
    }

    public void setEvent(OfflineEvent.EventEnum event) {
        this.event = event;
    }

    public OfflineEvent.OfflineEventType getType() {
        return this.type;
    }

    public void setType(OfflineEvent.OfflineEventType type) {
        this.type = type;
    }


    /**
     * Enumeration of signin or signout event
     */
    public enum EventEnum {
        SIGN_IN,
        SIGN_OUT
    }

    public enum OfflineEventType {
        TASK,
        ACTIVITY,
    }

    public static class EventTypeConverter implements PropertyConverter<EventEnum, String> {
        @Override
        public EventEnum convertToEntityProperty(String databaseValue) {
            if (databaseValue == null) {
                return null;
            }
            return EventEnum.valueOf(databaseValue);
        }

        @Override
        public String convertToDatabaseValue(EventEnum entityProperty) {
            if (entityProperty == null) {
                return null;
            }
            return entityProperty.name();
        }
    }

    public static class OfflineTypeConverter implements PropertyConverter<OfflineEventType, String> {
        @Override
        public OfflineEventType convertToEntityProperty(String databaseValue) {
            return OfflineEventType.valueOf(databaseValue);
        }

        @Override
        public String convertToDatabaseValue(OfflineEventType entityProperty) {
            return entityProperty.name();
        }
    }

}
