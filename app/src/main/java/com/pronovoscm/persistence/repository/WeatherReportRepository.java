package com.pronovoscm.persistence.repository;

import android.content.Context;

import com.google.gson.Gson;
import com.pronovoscm.PronovosApplication;
import com.pronovoscm.model.SyncDataEnum;
import com.pronovoscm.model.TransactionModuleEnum;
import com.pronovoscm.model.response.forecastresponse.Forecast;
import com.pronovoscm.model.response.login.LoginResponse;
import com.pronovoscm.model.response.weatherconditions.Conditions;
import com.pronovoscm.model.response.weatherreport.SyncData;
import com.pronovoscm.model.response.weatherreport.WeatherReports;
import com.pronovoscm.persistence.domain.DaoSession;
import com.pronovoscm.persistence.domain.TransactionLogMobile;
import com.pronovoscm.persistence.domain.TransactionLogMobileDao;
import com.pronovoscm.persistence.domain.WeatherConditions;
import com.pronovoscm.persistence.domain.WeatherConditionsDao;
import com.pronovoscm.persistence.domain.WeatherReport;
import com.pronovoscm.persistence.domain.WeatherReportDao;
import com.pronovoscm.persistence.domain.WeatherWidget;
import com.pronovoscm.persistence.domain.WeatherWidgetDao;
import com.pronovoscm.utils.DateFormatter;
import com.pronovoscm.utils.SharedPref;

import org.greenrobot.greendao.query.DeleteQuery;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

public class WeatherReportRepository extends AbstractRepository {
    private final Context context;
    private LoginResponse loginResponse;

    public WeatherReportRepository(DaoSession daoSession, Context context) {
        super(daoSession);
        this.context = context;
    }

    /**
     * Insert or update PunchlistRequest Assignees
     *
     * @param conditions
     * @return
     */
    public List<WeatherConditions> doUpdateWeatherConditionTable(List<Conditions> conditions) {

        loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        try {
            DeleteQuery<WeatherConditions> weatherConditionsDeleteQuery = getDaoSession().queryBuilder(WeatherConditions.class)
                    .buildDelete();
            weatherConditionsDeleteQuery.executeDeleteWithoutDetachingEntities();

            getDaoSession().callInTx(new Callable<List<Conditions>>() {
                final WeatherConditionsDao mWeatherConditionsDao = getDaoSession().getWeatherConditionsDao();

                @Override
                public List<Conditions> call() {
                    for (Conditions condition : conditions) {


                        WeatherConditions weatherConditions = new WeatherConditions();
                        weatherConditions.setUsersId(loginResponse.getUserDetails().getUsers_id());
                        weatherConditions.setLabel(condition.getLabel());
                        weatherConditions.setCreatedDate(condition.getCreatedAt() != null && !condition.getCreatedAt().equals("") ? DateFormatter.getDateFromDateTimeString(condition.getCreatedAt()) : null);
                        weatherConditions.setUpdatedDate(condition.getUpdatedAt() != null && !condition.getUpdatedAt().equals("") ? DateFormatter.getDateFromDateTimeString(condition.getUpdatedAt()) : null);
                        weatherConditions.setWeatherConditionsId(condition.getWeatherConditionsId());
                        mWeatherConditionsDao.save(weatherConditions);

                    }
                    return conditions;
                }

            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return getWeatherConditions();
    }

    /**
     * Insert or update Weather Report
     *
     * @param reports
     * @param projectId
     * @param isSync
     * @return
     */
    public WeatherReport doUpdateWeatherReportTable(WeatherReports reports, int projectId, Date date, boolean isSync) {

        loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        try {
            DeleteQuery<WeatherReport> weatherReportDeleteQuery = getDaoSession().queryBuilder(WeatherReport.class)
                    .where(WeatherReportDao.Properties.ProjectId.eq(projectId), WeatherReportDao.Properties.ReportDate.eq(date))
                    .buildDelete();
            weatherReportDeleteQuery.executeDeleteWithoutDetachingEntities();

            getDaoSession().callInTx(new Callable<WeatherReports>() {
                final WeatherReportDao mWeatherConditionsDao = getDaoSession().getWeatherReportDao();

                @Override
                public WeatherReports call() {


                    WeatherReport weatherReport = new WeatherReport();
                    weatherReport.setUsersId(loginResponse.getUserDetails().getUsers_id());
                    weatherReport.setImpact(reports.getImpact());
                    weatherReport.setConditions(reports.getConditions());
                    weatherReport.setIsSync(isSync);
                    weatherReport.setNotes(reports.getNotes());
                    weatherReport.setReportDate(date);
                    weatherReport.setProjectId(projectId);
                    mWeatherConditionsDao.save(weatherReport);

                    /*getDaoSession().getCrewListDao().queryBuilder()
                            .where(CrewListDao.Properties.CrewReportIdMobile.isNotNull())
                            .orderDesc(CrewListDao.Properties.CrewReportIdMobile).limit(1).list()
                            .get(0).getCrewReportIdMobile();*/

                    long mobileId = getDaoSession().getWeatherReportDao().queryBuilder()
                            .where(WeatherReportDao.Properties.Id.isNotNull())
                            .orderDesc(WeatherReportDao.Properties.Id).limit(1).list()
                            .get(0).getId();
                    if (!isSync) {
                        TransactionLogMobileDao mPronovosSyncDataDao = getDaoSession().getTransactionLogMobileDao();
                        TransactionLogMobile transactionLogMobile = new TransactionLogMobile();
                        transactionLogMobile.setUsersId(loginResponse.getUserDetails().getUsers_id());
                        transactionLogMobile.setModule(TransactionModuleEnum.WEATHER.ordinal());
                        transactionLogMobile.setStatus(SyncDataEnum.NOTSYNC.ordinal());
                        transactionLogMobile.setMobileId((long) projectId);
                        transactionLogMobile.setServerId(0L);
                        transactionLogMobile.setCreateDate(date);
                        mPronovosSyncDataDao.save(transactionLogMobile);
                        PronovosApplication.getContext().setupAndStartWorkManager();
                    }
                    return reports;
                }

            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return getWeatherReports(projectId, date);
    }

    /**
     * update sync Weather Report
     *
     * @param syncDatas
     * @param projectId
     * @return
     */
    public void doUpdateSyncWeatherReportTable(List<SyncData> syncDatas, int projectId) {

        loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        try {
            getDaoSession().callInTx(new Callable<List<SyncData>>() {
                final WeatherReportDao mWeatherConditionsDao = getDaoSession().getWeatherReportDao();

                @Override
                public List<SyncData> call() {
                    for (SyncData syncData : syncDatas) {
                        if (syncData.getSync()) {

                            loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
                            List<WeatherReport> weatherReport = getDaoSession().getWeatherReportDao().queryBuilder().where(
                                    WeatherReportDao.Properties.ProjectId.eq(projectId), WeatherReportDao.Properties.UsersId.eq(loginResponse.getUserDetails().getUsers_id()),
                                    WeatherReportDao.Properties.ReportDate.eq(DateFormatter.getDateFromDateHHTimeString(syncData.getReportDate()))).limit(1).list();
                            if (weatherReport.size() > 0) {
                                weatherReport.get(0).setIsSync(true);
                                mWeatherConditionsDao.update(weatherReport.get(0));
                            }
                        }
                    }
                    return syncDatas;
                }

            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public List<WeatherConditions> getWeatherConditions() {
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        return getDaoSession().getWeatherConditionsDao().queryBuilder().where(WeatherConditionsDao.Properties.UsersId.eq(loginResponse.getUserDetails().getUsers_id())).list();
    }

    public WeatherReport getWeatherReports(int projectId, Date date) {
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        List<WeatherReport> weatherReport = getDaoSession().getWeatherReportDao().queryBuilder().where(WeatherReportDao.Properties.ProjectId.eq(projectId), WeatherReportDao.Properties.UsersId.eq(loginResponse.getUserDetails().getUsers_id()), WeatherReportDao.Properties.ReportDate.eq(date)).limit(1).list();
        if (weatherReport.size() > 0) {
            return weatherReport.get(0);
        } else {
            return null;
        }
    }

    public WeatherReport getWeatherReport(Integer usersId, Long mobileId, Date createAt) {
        return getDaoSession().getWeatherReportDao().queryBuilder()
                .where(WeatherReportDao.Properties.UsersId.eq(usersId),
                        WeatherReportDao.Properties.ProjectId.eq(mobileId), WeatherReportDao.Properties.ReportDate.eq(createAt))
                .list().get(0);
    }

    public List<com.pronovoscm.model.request.weatherreport.WeatherReports> getNonSyncWeatherReports(int projectId) {
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        List<WeatherReport> weatherReport = getDaoSession().getWeatherReportDao().queryBuilder().where(WeatherReportDao.Properties.ProjectId.eq(projectId), WeatherReportDao.Properties.UsersId.eq(loginResponse.getUserDetails().getUsers_id()), WeatherReportDao.Properties.IsSync.eq(false)).list();
        List<com.pronovoscm.model.request.weatherreport.WeatherReports> weatherReports = new ArrayList<>();
        for (WeatherReport weatherRep : weatherReport) {
            com.pronovoscm.model.request.weatherreport.WeatherReports wR = new com.pronovoscm.model.request.weatherreport.WeatherReports();
            wR.setProjectId(String.valueOf(projectId));
            wR.setReportDate(DateFormatter.formatDateTimeHHForService(weatherRep.getReportDate()));
            wR.setImpact(weatherRep.getImpact());
            wR.setNotes(weatherRep.getNotes());
            wR.setConditions(weatherRep.getConditions());
            weatherReports.add(wR);
        }


        return weatherReports;
    }

    /**
     * Insert or update hourly forecast
     *
     * @param forecastList
     * @return
     */
    public List<WeatherWidget> doUpdateWeatherWidgetTable(List<Forecast> forecastList, int projectId, Date date) {

        loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        try {
            DeleteQuery<WeatherWidget> weatherWidgetDeleteQuery = getDaoSession().queryBuilder(WeatherWidget.class)
                    .where(WeatherWidgetDao.Properties.ProjectId.eq(projectId), WeatherWidgetDao.Properties.ReportDate.eq(date))
                    .buildDelete();
            weatherWidgetDeleteQuery.executeDeleteWithoutDetachingEntities();

            getDaoSession().callInTx(new Callable<List<Forecast>>() {
                final WeatherWidgetDao mWeatherConditionsDao = getDaoSession().getWeatherWidgetDao();

                @Override
                public List<Forecast> call() {
                    for (Forecast forecast : forecastList) {


                        WeatherWidget weatherWidget = new WeatherWidget();
                        weatherWidget.setUsersId(loginResponse.getUserDetails().getUsers_id());
                        weatherWidget.setProjectId(projectId);
                        weatherWidget.setIcon(forecast.getIcon());
                        weatherWidget.setSummary(forecast.getSummary());
                        weatherWidget.setTemperature(forecast.getTemperature());
                        weatherWidget.setTime(forecast.getTime());
                        weatherWidget.setReportDate(date);

                        mWeatherConditionsDao.save(weatherWidget);

                    }
                    return forecastList;
                }

            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return getWeatherWidget(projectId, date);
    }


    public List<WeatherWidget> getWeatherWidget(int projectId, Date date) {
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        return getDaoSession().getWeatherWidgetDao().queryBuilder().where(
                WeatherWidgetDao.Properties.UsersId.eq(loginResponse.getUserDetails().getUsers_id()),
                WeatherWidgetDao.Properties.ProjectId.eq(projectId),
                WeatherWidgetDao.Properties.ReportDate.eq(date))
                .list();
    }


}
