package com.pronovoscm.persistence.repository;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.pronovoscm.PronovosApplication;
import com.pronovoscm.model.SyncDataEnum;
import com.pronovoscm.model.TransactionModuleEnum;
import com.pronovoscm.model.response.crewreport.CrewReport;
import com.pronovoscm.model.response.crewreport.SyncData;
import com.pronovoscm.model.response.login.LoginResponse;
import com.pronovoscm.persistence.domain.CrewList;
import com.pronovoscm.persistence.domain.CrewListDao;
import com.pronovoscm.persistence.domain.DaoSession;
import com.pronovoscm.persistence.domain.TransactionLogMobile;
import com.pronovoscm.persistence.domain.TransactionLogMobileDao;
import com.pronovoscm.utils.DateFormatter;
import com.pronovoscm.utils.SharedPref;

import org.greenrobot.greendao.query.DeleteQuery;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

import static com.pronovoscm.activity.DrawingPDFActivity.TAG;

public class CrewReportRepository extends AbstractRepository {
    private final Context context;
    private LoginResponse loginResponse;

    public CrewReportRepository(DaoSession daoSession, Context context) {
        super(daoSession);
        this.context = context;
    }


    /**
     * Insert or update CrewList
     *
     * @param crewReports
     * @return
     */
    public List<CrewList> doUpdateCrewListTable(List<CrewReport> crewReports, Date date, int projectId) {

        loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        try {

            getDaoSession().callInTx(new Callable<List<CrewReport>>() {
                final CrewListDao mCrewListDao = getDaoSession().getCrewListDao();

                @Override
                public List<CrewReport> call() {
                    for (CrewReport crewReport : crewReports) {
                        if (crewReport.getDeletedAt() != null && !TextUtils.isEmpty(crewReport.getDeletedAt())) {
                            DeleteQuery<CrewList> crewListDeleteQuery = getDaoSession().queryBuilder(CrewList.class)
                                    .where(CrewListDao.Properties.ProjectId.eq(projectId),
                                            CrewListDao.Properties.CrewReportId.eq(crewReport.getCrewReportId()),
                                            CrewListDao.Properties.UsersId.eq(loginResponse.getUserDetails().getUsers_id()))
                                    .buildDelete();
                            crewListDeleteQuery.executeDeleteWithoutDetachingEntities();

                        } else {
                            List<CrewList> crewLists = getDaoSession().getCrewListDao().queryBuilder().where(
                                    CrewListDao.Properties.ProjectId.eq(projectId),
                                    CrewListDao.Properties.CrewReportId.eq(crewReport.getCrewReportId()),
                                    CrewListDao.Properties.UsersId.eq(loginResponse.getUserDetails().getUsers_id())
                            ).limit(1).list();
                            List<CrewList> crewListMobile = getDaoSession().getCrewListDao().queryBuilder().where(
                                    CrewListDao.Properties.ProjectId.eq(projectId),
                                    CrewListDao.Properties.CrewReportIdMobile.eq(crewReport.getCrewReportIdMobile()),
                                    CrewListDao.Properties.CrewReportId.eq(0),
                                    CrewListDao.Properties.UsersId.eq(loginResponse.getUserDetails().getUsers_id())
                            ).limit(1).list();

                            if (crewLists.size() > 0 || crewListMobile.size() > 0) {
                                CrewList crewList;
                                if (crewLists.size() > 0) {
                                    crewList = crewLists.get(0);
                                } else {
                                    crewList = crewListMobile.get(0);
                                }
                                crewList.setCompanyId(crewReport.getCompanyId());
                                crewList.setApprentice(crewReport.getApprentice());
                                crewList.setCompanyName(crewReport.getCompanyname());
                                crewList.setCrewReportId(crewReport.getCrewReportId());
                                Date deletedAt = crewList.getDeletedAt();
                                crewList.setDeletedAt(crewReport.getDeletedAt() != null && !crewReport.getDeletedAt().equals("") ? DateFormatter.getDateFromDateTimeString(crewReport.getDeletedAt()) : deletedAt);
//                                crewList.setDeletedAt(crewReport.getDeletedAt() != null && !crewReport.getDeletedAt().equals("") ? DateFormatter.getDateFromDateTimeString(crewReport.getDeletedAt()) : crewList.getDeletedAt()==null?null:crewList.getDeletedAt());                                crewList.setForeman(crewReport.getForeman());
                                crewList.setIsSync(true);
                                crewList.setJourneyman(crewReport.getJourneyman());
                                crewList.setProjectId(projectId);
                                crewList.setSupt(crewReport.getSupt());
                                crewList.setTrade(crewReport.getTrade());
                                crewList.setTradesId(crewReport.getTradesId());
                                crewList.setType(crewReport.getType());
                                crewList.setUsersId(loginResponse.getUserDetails().getUsers_id());
                                crewList.setCreatedAt(date);
                                crewList.setForeman(crewReport.getForeman());
                                mCrewListDao.update(crewList);
                            } else {

                                CrewList crewList = new CrewList();
                                crewList.setCompanyId(crewReport.getCompanyId());
                                crewList.setApprentice(crewReport.getApprentice());
                                crewList.setCompanyName(crewReport.getCompanyname());
                                crewList.setCrewReportId(crewReport.getCrewReportId());
                                crewList.setDeletedAt(crewReport.getDeletedAt() != null && !crewReport.getDeletedAt().equals("") ? DateFormatter.getDateFromDateTimeString(crewReport.getDeletedAt()) : null);
                                crewList.setForeman(crewReport.getForeman());
                                crewList.setIsSync(true);
                                crewList.setJourneyman(crewReport.getJourneyman());
                                crewList.setProjectId(projectId);
                                crewList.setSupt(crewReport.getSupt());
                                crewList.setTrade(crewReport.getTrade());
                                crewList.setTradesId(crewReport.getTradesId());
                                crewList.setType(crewReport.getType());
                                crewList.setUsersId(loginResponse.getUserDetails().getUsers_id());
                                crewList.setCreatedAt(date);
                                mCrewListDao.save(crewList);
                            }
                        }
                    }
                    return crewReports;
                }

            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return getCrewList(projectId, date);
    }

    /**
     * List of Crew
     *
     * @param projectId
     * @param date
     * @return
     */
    public List<CrewList> getCrewList(int projectId, Date date) {
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        return getDaoSession().getCrewListDao().queryBuilder().where(CrewListDao.Properties.ProjectId.eq(projectId),
                CrewListDao.Properties.UsersId.eq(loginResponse.getUserDetails().getUsers_id()),
                CrewListDao.Properties.CreatedAt.eq(date), CrewListDao.Properties.DeletedAt.isNull()).list();
    }

    public void addUpdateCrewList(CrewList crewList) {
        long crewReportId;
        long crewReportServerId = 0;
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        CrewListDao mCrewListDao = getDaoSession().getCrewListDao();
        crewList.setUsersId(loginResponse.getUserDetails().getUsers_id());
        if (crewList.getCrewReportId() == 0) {
            mCrewListDao.save(crewList);
            crewReportId = getDaoSession().getCrewListDao().queryBuilder()
                    .where(CrewListDao.Properties.CrewReportIdMobile.isNotNull())
                    .orderDesc(CrewListDao.Properties.CrewReportIdMobile).limit(1).list()
                    .get(0).getCrewReportIdMobile();
        } else {
            mCrewListDao.update(crewList);
            crewReportId = crewList.getCrewReportIdMobile();
            crewReportServerId = crewList.getCrewReportId();
        }

        TransactionLogMobileDao transactionLogMobileDao = getDaoSession().getTransactionLogMobileDao();
        TransactionLogMobile transactionLogMobile = new TransactionLogMobile();
        transactionLogMobile.setUsersId(crewList.getUsersId());
        transactionLogMobile.setModule(TransactionModuleEnum.CREW.ordinal());
        transactionLogMobile.setStatus(SyncDataEnum.NOTSYNC.ordinal());
        transactionLogMobile.setMobileId(crewReportId);
        transactionLogMobile.setServerId(crewReportServerId);
        transactionLogMobile.setCreateDate(crewList.getCreatedAt());
        transactionLogMobileDao.save(transactionLogMobile);
        Log.i(TAG, "addupdatecrew: setupAndStartWorkManager");

        PronovosApplication.getContext().setupAndStartWorkManager();
    }

    /**
     * List of non sync Crew reports
     *
     * @param projectId
     * @return
     */
    public List<com.pronovoscm.model.request.crewreport.CrewReport> getNonSyncCrewReports(int projectId) {
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        List<CrewList> crewLists = getDaoSession().getCrewListDao().queryBuilder().where(CrewListDao.Properties.ProjectId.eq(projectId), CrewListDao.Properties.UsersId.eq(loginResponse.getUserDetails().getUsers_id()), CrewListDao.Properties.IsSync.eq(false)).list();
        List<com.pronovoscm.model.request.crewreport.CrewReport> crewReports = new ArrayList<>();
        for (CrewList crewList : crewLists) {
            com.pronovoscm.model.request.crewreport.CrewReport crewReport = new com.pronovoscm.model.request.crewreport.CrewReport();
            crewReport.setProjectId(String.valueOf(projectId));
            crewReport.setCreatedAt(DateFormatter.formatDateTimeHHForService(crewList.getCreatedAt()));
            crewReport.setApprentice(String.valueOf(crewList.getApprentice()));
            crewReport.setCompanyId(String.valueOf(crewList.getCompanyId()));
            crewReport.setCompanyname(crewList.getCompanyName());
            crewReport.setCrewReportId(String.valueOf(crewList.getCrewReportId()));
            crewReport.setCrewReportIdMobile(String.valueOf(crewList.getCrewReportIdMobile()));
            crewReport.setDeletedAt(crewList.getDeletedAt() != null ? DateFormatter.formatDateTimeHHForService(crewList.getDeletedAt()) : "");
            crewReport.setForeman(String.valueOf(crewList.getForeman()));
            crewReport.setJourneyman(String.valueOf(crewList.getJourneyman()));
            crewReport.setSupt(String.valueOf(crewList.getSupt()));
            crewReport.setTrade(String.valueOf(crewList.getTrade()));
            crewReport.setTradesId(String.valueOf(crewList.getTradesId()));
            crewReport.setType(String.valueOf(crewList.getType()));
            crewReports.add(crewReport);
        }
        return crewReports;
    }

    public void doUpdateSyncCrewListTable(List<SyncData> syncData, int projectId) {
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        try {
            getDaoSession().callInTx(new Callable<List<SyncData>>() {
                final CrewListDao mCrewListDao = getDaoSession().getCrewListDao();

                @Override
                public List<SyncData> call() {
                    for (SyncData syncData : syncData) {
                        if (syncData.getSync()) {

                            loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
                            List<CrewList> crewLists = getDaoSession().getCrewListDao().queryBuilder().where(
                                    CrewListDao.Properties.ProjectId.eq(projectId),
                                    CrewListDao.Properties.CrewReportIdMobile.eq(syncData.getCrewReportIdMobile()),
                                    CrewListDao.Properties.CrewReportId.eq(0),
                                    CrewListDao.Properties.UsersId.eq(loginResponse.getUserDetails().getUsers_id()),
                                    CrewListDao.Properties.CreatedAt.eq(DateFormatter.getDateFromDateHHTimeString(syncData.getReportDate()))).limit(1).list();
                            if (crewLists.size() > 0) {
                                crewLists.get(0).setIsSync(true);
                                crewLists.get(0).setCrewReportId(Integer.parseInt(syncData.getCrewReportId()));
                                mCrewListDao.update(crewLists.get(0));
                            }
                        }
                    }
                    return syncData;
                }

            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public CrewList getCrewListDetail(long crewListMobileId) {
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        List<CrewList> crewLists = getDaoSession().getCrewListDao().queryBuilder().where(CrewListDao.Properties.CrewReportIdMobile.eq(crewListMobileId),
                CrewListDao.Properties.UsersId.eq(loginResponse.getUserDetails().getUsers_id())).limit(1).list();
        if (crewLists.size() > 0) {
            return crewLists.get(0);
        } else {
            return null;
        }
    }
    public List<CrewList> getNotSyncCrewList() {
        List<CrewList> crewLists = getDaoSession().getCrewListDao().queryBuilder().where(CrewListDao.Properties.IsSync.eq(false)).limit(1).list();
        if (crewLists.size() > 0) {
            return crewLists;
        } else {
            return null;
        }
    }

    /**
     * To retrieve the crew detail from the database.
     *
     * @param userId an id of the user.
     *
     * @param crewMobileId mobile id of the crew.
     *
     * @param serverId server id of the crew.
     *
     * @return an object of CrewList.
     */
    public CrewList getCrewDetail(int userId,long crewMobileId, long serverId) {
        List<CrewList> crewLists=
         getDaoSession().getCrewListDao().queryBuilder()
                .where(CrewListDao.Properties.CrewReportId.eq(serverId),
                        CrewListDao.Properties.CrewReportIdMobile.eq(crewMobileId),
                        CrewListDao.Properties.UsersId.eq(userId)).list();
        if (crewLists.size()>0){
            return crewLists.get(0);
        }else {
            return null;
        }
    }
}
