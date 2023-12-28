package com.pronovoscm.persistence.repository;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.pronovoscm.PronovosApplication;
import com.pronovoscm.model.SyncDataEnum;
import com.pronovoscm.model.TransactionModuleEnum;
import com.pronovoscm.model.request.workimpact.WorkImpactsReportRequest;
import com.pronovoscm.model.response.login.LoginResponse;
import com.pronovoscm.model.response.workimpact.WorkImpactsReport;
import com.pronovoscm.persistence.domain.CompanyList;
import com.pronovoscm.persistence.domain.DaoSession;
import com.pronovoscm.persistence.domain.TransactionLogMobile;
import com.pronovoscm.persistence.domain.TransactionLogMobileDao;
import com.pronovoscm.persistence.domain.WorkImpact;
import com.pronovoscm.persistence.domain.WorkImpactAttachments;
import com.pronovoscm.persistence.domain.WorkImpactAttachmentsDao;
import com.pronovoscm.persistence.domain.WorkImpactDao;
import com.pronovoscm.utils.DateFormatter;
import com.pronovoscm.utils.SharedPref;

import org.greenrobot.greendao.query.DeleteQuery;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

public class WorkImpactRepository extends AbstractRepository {
    private Context context;
    private LoginResponse loginResponse;

    public WorkImpactRepository(DaoSession daoSession, Context context) {
        super(daoSession);
        this.context = context;
    }

    /**
     * Insert or update WorkImpact
     *
     * @param WorkImpactsReports
     * @return
     */
    public List<WorkImpact> doUpdateWorkImpactTable(List<WorkImpactsReport> WorkImpactsReports, Date date, int projectId) {

        loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        if (loginResponse != null) {
            int userId = loginResponse.getUserDetails().getUsers_id();

            try {
                getDaoSession().callInTx(new Callable<List<WorkImpactsReport>>() {
                    WorkImpactDao mWorkImpactDao = getDaoSession().getWorkImpactDao();
                    WorkImpactAttachmentsDao mWorkImpactAttachmentsDao = getDaoSession().getWorkImpactAttachmentsDao();

                    @Override
                    public List<WorkImpactsReport> call() throws Exception {
                        for (WorkImpactsReport WorkImpactsReport : WorkImpactsReports) {

                            long workImpactMobileId = 0;
                            if (WorkImpactsReport.getDeletedAt() != null && !TextUtils.isEmpty(WorkImpactsReport.getDeletedAt())) {

                                DeleteQuery<WorkImpactAttachments> WorkImpactAttachmentsDeleteQuery = getDaoSession().queryBuilder(WorkImpactAttachments.class)
                                        .where(WorkImpactAttachmentsDao.Properties.WorkImpactReportId.eq(WorkImpactsReport.getWorkImpactReportId()),
                                                WorkImpactAttachmentsDao.Properties.UsersId.eq(userId))
                                        .buildDelete();
                                WorkImpactAttachmentsDeleteQuery.executeDeleteWithoutDetachingEntities();
                                DeleteQuery<WorkImpact> WorkImpactDeleteQuery = getDaoSession().queryBuilder(WorkImpact.class)
                                        .where(WorkImpactDao.Properties.ProjectId.eq(projectId),
                                                WorkImpactDao.Properties.WorkImpactReportId.eq(WorkImpactsReport.getWorkImpactReportId()),
                                                WorkImpactDao.Properties.UsersId.eq(userId))
                                        .buildDelete();
                                WorkImpactDeleteQuery.executeDeleteWithoutDetachingEntities();

                            } else {
                                List<WorkImpact> WorkImpactList = getDaoSession().getWorkImpactDao().queryBuilder().where(
                                        WorkImpactDao.Properties.ProjectId.eq(projectId),
                                        WorkImpactDao.Properties.WorkImpactReportId.eq(WorkImpactsReport.getWorkImpactReportId()),
                                        WorkImpactDao.Properties.UsersId.eq(userId)
                                ).limit(1).list();
                                List<WorkImpact> WorkImpactMobile = getDaoSession().getWorkImpactDao().queryBuilder().where(
                                        WorkImpactDao.Properties.ProjectId.eq(projectId),
                                        WorkImpactDao.Properties.WorkImpactReportIdMobile.eq(WorkImpactsReport.getWorkImpactReportIdMobile()),
                                        WorkImpactDao.Properties.WorkImpactReportId.eq(0),
                                        WorkImpactDao.Properties.UsersId.eq(userId)
                                ).limit(1).list();
                                if (WorkImpactList.size() > 0 || WorkImpactMobile.size() > 0) {
                                    WorkImpact WorkImpact;
                                    if (WorkImpactList.size() > 0) {
                                        WorkImpact = WorkImpactList.get(0);
                                    } else {
                                        WorkImpact = WorkImpactMobile.get(0);
                                    }
                                    WorkImpact.setCompanyId(WorkImpactsReport.getCompanyId());
                                    WorkImpact.setCompanyName(WorkImpactsReport.getCompanyname());
                                    WorkImpact.setDeletedAt(WorkImpactsReport.getDeletedAt() != null && !WorkImpactsReport.getDeletedAt().equals("") ? DateFormatter.getDateFromDateTimeString(WorkImpactsReport.getDeletedAt()) : null);
                                    WorkImpact.setIsSync(true);
                                    WorkImpact.setProjectId(projectId);
                                    WorkImpact.setWorkImpactReportId(WorkImpactsReport.getWorkImpactReportId());
                                    WorkImpact.setWorkImpLocation(WorkImpactsReport.getWorkImpLocation());
                                    WorkImpact.setWorkSummary(WorkImpactsReport.getWorkSummary());
                                    WorkImpact.setType(WorkImpactsReport.getType());
                                    WorkImpact.setUsersId(userId);
                                    WorkImpact.setCreatedAt(date);
                                    WorkImpact.setIsAttachmentSync(true);
                                    workImpactMobileId = WorkImpact.getWorkImpactReportIdMobile();
                                    mWorkImpactDao.update(WorkImpact);

                                } else {
                                    WorkImpact WorkImpact = new WorkImpact();
                                    WorkImpact.setCompanyId(WorkImpactsReport.getCompanyId());
                                    WorkImpact.setCompanyName(WorkImpactsReport.getCompanyname());
                                    WorkImpact.setDeletedAt(WorkImpactsReport.getDeletedAt() != null && !WorkImpactsReport.getDeletedAt().equals("") ? DateFormatter.getDateFromDateTimeString(WorkImpactsReport.getDeletedAt()) : null);
                                    WorkImpact.setIsSync(true);
                                    WorkImpact.setProjectId(projectId);
                                    WorkImpact.setWorkImpactReportId(WorkImpactsReport.getWorkImpactReportId());
                                    WorkImpact.setWorkImpLocation(WorkImpactsReport.getWorkImpLocation());
                                    WorkImpact.setIsAttachmentSync(true);
                                    WorkImpact.setWorkSummary(WorkImpactsReport.getWorkSummary());
                                    WorkImpact.setType(WorkImpactsReport.getType());
                                    WorkImpact.setUsersId(userId);
                                    WorkImpact.setCreatedAt(date);
                                    mWorkImpactDao.save(WorkImpact);
                                    List<WorkImpact> workImpactList = getDaoSession().getWorkImpactDao().queryBuilder().where(WorkImpactDao.Properties.WorkImpactReportIdMobile.isNotNull()).orderDesc(WorkImpactDao.Properties.WorkImpactReportIdMobile).limit(1).list();
                                    workImpactMobileId = workImpactList.get(0).getWorkImpactReportIdMobile();
                                }

                                for (com.pronovoscm.model.response.workimpact.Attachments attachment : WorkImpactsReport.getAttachments()) {
                                    List<WorkImpactAttachments> impactAttachments = getDaoSession().getWorkImpactAttachmentsDao().queryBuilder().where(
                                            WorkImpactAttachmentsDao.Properties.AttachmentId.eq(attachment.getAttachmentsId()),
                                            WorkImpactAttachmentsDao.Properties.WorkImpactReportId.eq(WorkImpactsReport.getWorkImpactReportId()),
                                            WorkImpactAttachmentsDao.Properties.UsersId.eq(userId)
                                    ).limit(1).list();
                                    List<WorkImpactAttachments> impactAttachmentMobile = getDaoSession().getWorkImpactAttachmentsDao().queryBuilder().where(
                                            WorkImpactAttachmentsDao.Properties.AttachmentIdMobile.eq(attachment.getAttachmentsIdMobile()),
                                            WorkImpactAttachmentsDao.Properties.AttachmentId.eq(0),
                                            WorkImpactAttachmentsDao.Properties.WorkImpactReportIdMobile.eq(workImpactMobileId),
                                            WorkImpactAttachmentsDao.Properties.UsersId.eq(userId)
                                    ).limit(1).list();


                                    if (attachment.getDeletedAt() != null && !TextUtils.isEmpty(attachment.getDeletedAt())) {
                                        DeleteQuery<WorkImpactAttachments> WorkImpactAttachmentsDeleteQuery = getDaoSession().queryBuilder(WorkImpactAttachments.class)
                                                .where(WorkImpactAttachmentsDao.Properties.WorkImpactReportId.eq(WorkImpactsReport.getWorkImpactReportId()),
                                                        WorkImpactAttachmentsDao.Properties.AttachmentId.eq(attachment.getAttachmentsId()),
                                                        WorkImpactAttachmentsDao.Properties.UsersId.eq(userId))
                                                .buildDelete();
                                        WorkImpactAttachmentsDeleteQuery.executeDeleteWithoutDetachingEntities();
                                    } else if (impactAttachments.size() > 0 || impactAttachmentMobile.size() > 0) {
                                        WorkImpactAttachments workImpactAttachments;
                                        if (impactAttachments.size() > 0) {
                                            workImpactAttachments = impactAttachments.get(0);
                                        } else {
                                            workImpactAttachments = impactAttachmentMobile.get(0);
                                        }

                                        workImpactAttachments.setAttachmentId(attachment.getAttachmentsId());
                                        workImpactAttachments.setAttachmentPath(attachment.getAttachPath());
                                        workImpactAttachments.setIsAwsSync(true);
                                        workImpactAttachments.setDeletedAt(attachment.getDeletedAt() != null && !attachment.getDeletedAt().equals("") ? DateFormatter.getDateFromDateTimeString(attachment.getDeletedAt()) : null);
                                        workImpactAttachments.setWorkImpactReportId(WorkImpactsReport.getWorkImpactReportId());
                                        workImpactAttachments.setWorkImpactReportIdMobile(workImpactMobileId);
                                        workImpactAttachments.setUsersId(userId);
                                        workImpactAttachments.setType(attachment.getType());
                                        mWorkImpactAttachmentsDao.update(workImpactAttachments);

                                    } else {
                                        WorkImpactAttachments workImpactAttachments = new WorkImpactAttachments();
                                        workImpactAttachments.setAttachmentId(attachment.getAttachmentsId());
                                        workImpactAttachments.setAttachmentPath(attachment.getAttachPath());
                                        workImpactAttachments.setDeletedAt(attachment.getDeletedAt() != null && !attachment.getDeletedAt().equals("") ? DateFormatter.getDateFromDateTimeString(attachment.getDeletedAt()) : null);
                                        workImpactAttachments.setWorkImpactReportId(WorkImpactsReport.getWorkImpactReportId());
                                        workImpactAttachments.setIsAwsSync(true);
                                        workImpactAttachments.setWorkImpactReportIdMobile(workImpactMobileId);
                                        workImpactAttachments.setUsersId(userId);
//                                        workImpactAttachments.setType(attachment.getType());
                                        if (attachment.getType() != null)
                                            workImpactAttachments.setType(attachment.getType());
                                        else workImpactAttachments.setType("JPEG");
                                        mWorkImpactAttachmentsDao.save(workImpactAttachments);
                                    }
                                }
                            }
                        }
                        return WorkImpactsReports;
                    }

                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return getWorkImpact(projectId, date);
    }


    public List<WorkImpact> getWorkImpact(int projectId, Date date) {
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        List<WorkImpact> WorkImpact = getDaoSession().getWorkImpactDao().queryBuilder().where(
                WorkImpactDao.Properties.ProjectId.eq(projectId),
                WorkImpactDao.Properties.UsersId.eq(loginResponse.getUserDetails().getUsers_id()),
                WorkImpactDao.Properties.CreatedAt.eq(date),
                WorkImpactDao.Properties.DeletedAt.isNull()
        ).list();
        return WorkImpact;
    }

    public List<WorkImpactAttachments> getAttachments(Long WorkImpactReportIdMobile) {

        loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        if (loginResponse != null) {
            List<WorkImpactAttachments> attachments = getDaoSession().getWorkImpactAttachmentsDao().queryBuilder().where(WorkImpactAttachmentsDao.Properties.UsersId.eq(loginResponse.getUserDetails().getUsers_id()), WorkImpactAttachmentsDao.Properties.WorkImpactReportIdMobile.eq(WorkImpactReportIdMobile), WorkImpactAttachmentsDao.Properties.DeletedAt.isNull()).list();
            return attachments;
        } else {
            return new ArrayList<>();
        }
    }

    public WorkImpactAttachments getAttachment(Long WorkImpactReportIdMobile, int attachmentID) {

        loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        if (loginResponse != null) {
            List<WorkImpactAttachments> attachments = getDaoSession().getWorkImpactAttachmentsDao().queryBuilder().where(WorkImpactAttachmentsDao.Properties.UsersId.eq(loginResponse.getUserDetails().getUsers_id()), WorkImpactAttachmentsDao.Properties.WorkImpactReportIdMobile.eq(WorkImpactReportIdMobile), WorkImpactAttachmentsDao.Properties.AttachmentId.eq(attachmentID)).limit(1).list();
            return attachments.get(0);
        } else {
            return null;
        }
    }

    public WorkImpact getWorkImpactItem(long workImpactMobileId) {
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        if (loginResponse != null) {
            List<WorkImpact> WorkImpact = getDaoSession().getWorkImpactDao().queryBuilder().where(WorkImpactDao.Properties.WorkImpactReportIdMobile.eq(workImpactMobileId), WorkImpactDao.Properties.UsersId.eq(loginResponse.getUserDetails().getUsers_id())).list();
            if (WorkImpact.size() > 0) {
                return WorkImpact.get(0);
            } else {
                return null;
            }
        } else {
            return null;
        }
    }


    /**
     * Get list of non synced work impact of a project
     *
     * @param projectId
     * @param WorkImpactDate
     * @return
     */
    public List<WorkImpact> getNonSyncWorkImpactList(int projectId, Date WorkImpactDate) {
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        List<WorkImpact> WorkImpact = getDaoSession().getWorkImpactDao().queryBuilder().where(
                WorkImpactDao.Properties.ProjectId.eq(projectId), WorkImpactDao.Properties.UsersId.eq(loginResponse.getUserDetails().getUsers_id()),
                WorkImpactDao.Properties.IsSync.eq(false), WorkImpactDao.Properties.CreatedAt.eq(WorkImpactDate),
                WorkImpactDao.Properties.IsAttachmentSync.eq(false)).list();
        return WorkImpact;
    }

    /**
     * Get list of non synced work impacts of a project
     *
     * @param projectId
     * @param WorkImpactDate
     * @return
     */
    public List<WorkImpact> getNonSyncWorkImpactList2(int projectId, Date WorkImpactDate) {
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        List<WorkImpact> WorkImpact = getDaoSession().getWorkImpactDao().queryBuilder().where(
                WorkImpactDao.Properties.ProjectId.eq(projectId), WorkImpactDao.Properties.UsersId.eq(loginResponse.getUserDetails().getUsers_id())).list();
        return WorkImpact;
    }

    /**
     * Get list of non synced work impacts of a project
     *
     * @param projectId
     * @param WorkImpactDate
     * @return
     */
    public List<WorkImpactsReportRequest> getNonSyncWorkImpactyncAttachmentList(int projectId, Date WorkImpactDate) {
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        List<WorkImpact> WorkImpact = getDaoSession().getWorkImpactDao().queryBuilder().where(
                WorkImpactDao.Properties.ProjectId.eq(projectId), WorkImpactDao.Properties.UsersId.eq(loginResponse.getUserDetails().getUsers_id()),
                WorkImpactDao.Properties.IsSync.eq(false), WorkImpactDao.Properties.CreatedAt.eq(WorkImpactDate), WorkImpactDao.Properties.IsAttachmentSync.eq(true)).list();

        List<WorkImpactsReportRequest> WorkImpactsReportRequests = new ArrayList<>();
        for (WorkImpact workImpact : WorkImpact) {
            WorkImpactsReportRequest WorkImpactsReport = new WorkImpactsReportRequest();
            WorkImpactsReport.setDeletedAt(workImpact.getDeletedAt() != null ? DateFormatter.formatDateTimeHHForService(workImpact.getDeletedAt()) : "");
            WorkImpactsReport.setType(workImpact.getType());
            WorkImpactsReport.setCompanyId(String.valueOf(workImpact.getCompanyId()));
            WorkImpactsReport.setProjectId(String.valueOf(projectId));
            WorkImpactsReport.setCompanyname(workImpact.getCompanyName());
            WorkImpactsReport.setWorkImpactReportId(String.valueOf(workImpact.getWorkImpactReportId()));
            WorkImpactsReport.setWorkImpactReportIdMobile(String.valueOf(workImpact.getWorkImpactReportIdMobile()));
            WorkImpactsReport.setWorkImpLocation(workImpact.getWorkImpLocation());
            WorkImpactsReport.setWorkSummary(workImpact.getWorkSummary());
            WorkImpactsReport.setCreatedAt(workImpact.getCreatedAt() != null ? DateFormatter.formatDateTimeHHForService(workImpact.getCreatedAt()) : "");
            List<WorkImpactAttachments> attachments = getSyncedAttachments(workImpact.getWorkImpactReportIdMobile());
            List<com.pronovoscm.model.request.workimpact.Attachments> attachmentsList = new ArrayList<>();
            for (WorkImpactAttachments attachment :
                    attachments) {
                com.pronovoscm.model.request.workimpact.Attachments attachmnt = new com.pronovoscm.model.request.workimpact.Attachments();
                attachmnt.setAttachmentsId(attachment.getAttachmentId() != null ? attachment.getAttachmentId() : 0);
                attachmnt.setAttachmentsIdMobile((int) (long) attachment.getAttachmentIdMobile());
                attachmnt.setAttachPath(attachment.getAttachmentPath());
                attachmnt.setDeletedAt(attachment.getDeletedAt() != null ? DateFormatter.formatDateTimeHHForService(attachment.getDeletedAt()) : "");
                attachmentsList.add(attachmnt);

            }

            WorkImpactsReport.setAttachments(attachmentsList);
            WorkImpactsReportRequests.add(WorkImpactsReport);

        }
        return WorkImpactsReportRequests;
    }


    /**
     * Get list of non synced work impacts whose attachments are also not synced of a project
     *
     * @param projectId
     * @param WorkImpactDate
     * @return
     */
    public List<WorkImpact> getNonSyncWorkImpactWithNonSyncedAttachments(int projectId, Date WorkImpactDate) {
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        List<WorkImpact> WorkImpact = getDaoSession().getWorkImpactDao().queryBuilder().where(
                WorkImpactDao.Properties.ProjectId.eq(projectId),
                WorkImpactDao.Properties.UsersId.eq(loginResponse.getUserDetails().getUsers_id()),
                WorkImpactDao.Properties.IsSync.eq(false),
                WorkImpactDao.Properties.IsAttachmentSync.eq(false),
                WorkImpactDao.Properties.CreatedAt.eq(WorkImpactDate)).list();
        return WorkImpact;
    }


    /**
     * Get list of non synced attachments of work impacts
     *
     * @param WorkImpactReportIdMobile
     * @return
     */
    public List<WorkImpactAttachments> getNonSyncedWorkImpactAttachments(Long WorkImpactReportIdMobile) {

        loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        List<WorkImpactAttachments> attachments = getDaoSession().getWorkImpactAttachmentsDao().queryBuilder().where(
                WorkImpactAttachmentsDao.Properties.UsersId.eq(loginResponse.getUserDetails().getUsers_id()),
                WorkImpactAttachmentsDao.Properties.IsAwsSync.eq(false), // sort only non synced attachments
                WorkImpactAttachmentsDao.Properties.WorkImpactReportIdMobile.eq(WorkImpactReportIdMobile)).list();
        return attachments;
    }


    /**
     * Get list of non synced attachments of work impacts
     *
     * @return
     */
    public List<WorkImpactAttachments> getNonSyncedAttachments() {

        loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        if (loginResponse != null) {
            List<WorkImpactAttachments> attachments = getDaoSession().getWorkImpactAttachmentsDao().queryBuilder().where(
                    WorkImpactAttachmentsDao.Properties.UsersId.eq(loginResponse.getUserDetails().getUsers_id()),
                    WorkImpactAttachmentsDao.Properties.IsAwsSync.eq(false)).list();
            return attachments;
        } else {
            return new ArrayList<>();
        }
    }

    /**
     * Get list of non synced attachments of work impacts
     *
     * @param WorkImpactReportIdMobile
     * @return
     */
    public List<WorkImpactAttachments> getSyncedAttachments(Long WorkImpactReportIdMobile) {

        loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        List<WorkImpactAttachments> attachments = getDaoSession().getWorkImpactAttachmentsDao().queryBuilder().where(
                WorkImpactAttachmentsDao.Properties.UsersId.eq(loginResponse.getUserDetails().getUsers_id()),
                WorkImpactAttachmentsDao.Properties.IsAwsSync.eq(true), // sort only non synced attachments
                WorkImpactAttachmentsDao.Properties.WorkImpactReportIdMobile.eq(WorkImpactReportIdMobile)).list();
        return attachments;
    }

    /**
     * Add new work impact in db
     *
     * @param projectId
     * @param mSelectedCompany
     * @param attachmentList
     * @param dateWorkImpact
     * @param location
     * @param summary
     * @return
     */
    public WorkImpact addWorkImpact(int projectId, CompanyList mSelectedCompany, List<WorkImpactAttachments> attachmentList,
                                    Date dateWorkImpact, String location, String summary) {
        Log.d("ONSAVE", "addWorkImpact: 22222222222222222 ");
        WorkImpact workImpact = new WorkImpact();
        // saveWorkImpact.setWorkImpactReportIdMobile(0); // it is auto incremented when saved in db
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        workImpact.setCompanyId(mSelectedCompany.getCompanyId());
        workImpact.setCompanyName(mSelectedCompany.getName());
        workImpact.setIsSync(false);
        if (attachmentList.size() > 0) {
            workImpact.setIsAttachmentSync(false);
        } else {
            workImpact.setIsAttachmentSync(true);

        }
        workImpact.setProjectId(projectId);
        workImpact.setType(mSelectedCompany.getType());
        workImpact.setUsersId(loginResponse.getUserDetails().getUsers_id());
        workImpact.setWorkImpactReportId(0);
        workImpact.setWorkImpLocation(location);
        workImpact.setWorkSummary(summary);
        workImpact.setCreatedAt(dateWorkImpact);

        // Save work impact
        getDaoSession().getWorkImpactDao().save(workImpact);
        List<WorkImpact> WorkImpactList = getDaoSession().getWorkImpactDao().queryBuilder().where(
                WorkImpactDao.Properties.WorkImpactReportIdMobile.isNotNull()).orderDesc(WorkImpactDao.Properties.WorkImpactReportIdMobile).limit(1).list();
        long workworkImpactReportIdMobile = WorkImpactList.get(0).getWorkImpactReportIdMobile();


        // save attachments one by one in db with respect to {@code workworkImpactReportIdMobile}
        for (WorkImpactAttachments attachment : attachmentList) {
            if (attachment != null) {
                attachment.setUsersId(loginResponse.getUserDetails().getUsers_id());
                attachment.setIsAwsSync(false);
                attachment.setWorkImpactReportId(0);
                attachment.setAttachmentId(0);
                attachment.setWorkImpactReportIdMobile(workworkImpactReportIdMobile);
                // Save work impact
                getDaoSession().getWorkImpactAttachmentsDao().save(attachment);
                List<WorkImpactAttachments> attachmentList1 = getDaoSession().getWorkImpactAttachmentsDao().queryBuilder().where(
                        WorkImpactAttachmentsDao.Properties.AttachmentIdMobile.isNotNull()).orderDesc(WorkImpactAttachmentsDao.Properties.AttachmentIdMobile)
                        .limit(1).list();

                TransactionLogMobileDao mPronovosSyncDataDao = getDaoSession().getTransactionLogMobileDao();

                TransactionLogMobile transactionLogMobile = new TransactionLogMobile();

                transactionLogMobile.setUsersId(loginResponse.getUserDetails().getUsers_id());
                transactionLogMobile.setModule(TransactionModuleEnum.WORK_IMPACT_ATTACHMENT.ordinal());
                transactionLogMobile.setStatus(SyncDataEnum.NOTSYNC.ordinal());
                transactionLogMobile.setMobileId(attachmentList1.get(0).getAttachmentIdMobile());
                transactionLogMobile.setServerId(0L);
                transactionLogMobile.setCreateDate(Calendar.getInstance().getTime());
                mPronovosSyncDataDao.save(transactionLogMobile);
            }
        }

        TransactionLogMobileDao mPronovosSyncDataDao = getDaoSession().getTransactionLogMobileDao();

        TransactionLogMobile transactionLogMobile = new TransactionLogMobile();

        transactionLogMobile.setUsersId(loginResponse.getUserDetails().getUsers_id());
        transactionLogMobile.setModule(TransactionModuleEnum.WORK_IMPACT.ordinal());
        transactionLogMobile.setStatus(SyncDataEnum.NOTSYNC.ordinal());
        transactionLogMobile.setMobileId(workworkImpactReportIdMobile);
        transactionLogMobile.setServerId(0L);
        transactionLogMobile.setCreateDate(Calendar.getInstance().getTime());
        mPronovosSyncDataDao.save(transactionLogMobile);
        ((PronovosApplication) context.getApplicationContext()).setupAndStartWorkManager();

        return workImpact;
    }

    /**
     * update work impact in db
     *
     * @param projectId
     * @param mSelectedCompany
     * @param attachmentList
     * @param dateWorkImpact
     * @param location
     * @param summary
     * @return
     */
    public WorkImpact updateWorkImpact(int projectId, CompanyList mSelectedCompany, List<WorkImpactAttachments> attachmentList,
                                       Date dateWorkImpact, String location, String summary, WorkImpact workImpact) {
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        boolean attachmentUpdated = false;
        if (loginResponse != null) {
            int userId = loginResponse.getUserDetails().getUsers_id();
            workImpact.setCompanyId(mSelectedCompany.getCompanyId());
            workImpact.setCompanyName(mSelectedCompany.getName());
            workImpact.setIsSync(false);
            if (attachmentList.size() > 0) {
                workImpact.setIsAttachmentSync(false);
            } else {
                workImpact.setIsAttachmentSync(true);

            }
            workImpact.setProjectId(projectId);
            workImpact.setType(mSelectedCompany.getType());
            workImpact.setUsersId(loginResponse.getUserDetails().getUsers_id());
            workImpact.setWorkImpLocation(location);
            workImpact.setWorkSummary(summary);
            workImpact.setCreatedAt(dateWorkImpact);
            // Update work impact


            // save attachments one by one in db with respect to {@code workimpactMobileID}
            for (WorkImpactAttachments attachment : attachmentList) {
                attachment.setUsersId(loginResponse.getUserDetails().getUsers_id());
//            attachment.setIsAwsSync(false);
                attachment.setWorkImpactReportId(workImpact.getWorkImpactReportId());
                attachment.setWorkImpactReportIdMobile(workImpact.getWorkImpactReportIdMobile());

                // Save work impact
                if (attachment.getAttachmentId() == null || attachment.getAttachmentId() == 0) {
                    attachment.setIsAwsSync(false);
                    if (attachment.getDeletedAt() != null) {
                        attachment.setIsAwsSync(true);
                        List<TransactionLogMobile> transactionLogMobileList = getDaoSession().getTransactionLogMobileDao().queryBuilder().where(
                                TransactionLogMobileDao.Properties.Module.eq(TransactionModuleEnum.WORK_IMPACT_ATTACHMENT.ordinal()),
                                TransactionLogMobileDao.Properties.MobileId.eq(attachment.getAttachmentIdMobile()),
                                TransactionLogMobileDao.Properties.ServerId.eq(0), TransactionLogMobileDao.Properties.UsersId.eq(userId)).list();

                        if (transactionLogMobileList != null && transactionLogMobileList.size() > 0) {
                            TransactionLogMobile transactionLogMobile = transactionLogMobileList.get(0);
                            deleteSyncData(transactionLogMobile);
                        }

                        DeleteQuery<WorkImpactAttachments> workImpactAttachmentsDeleteQuery = getDaoSession().queryBuilder(WorkImpactAttachments.class)
                                .where(WorkImpactAttachmentsDao.Properties.AttachmentIdMobile.eq(attachment.getAttachmentIdMobile()))
                                .buildDelete();
                        workImpactAttachmentsDeleteQuery.executeDeleteWithoutDetachingEntities();
                    } else {
                        attachment.setAttachmentId(0);
                        getDaoSession().getWorkImpactAttachmentsDao().save(attachment);

                    }
                }
                if (!attachment.getIsAwsSync()) {
                    WorkImpactAttachments attachmnt = getAttachment(workImpact.getWorkImpactReportIdMobile(), attachment.getAttachmentId());
                    if (attachmnt != null) {
                        attachment.setDeletedAt(attachmnt.getDeletedAt());
                        if (attachmnt.getDeletedAt() == null) {
                            attachment.setIsAwsSync(attachmnt.getIsAwsSync());
                            if (!attachmnt.getIsAwsSync()) {
                                attachmentUpdated = true;
                                TransactionLogMobileDao mPronovosSyncDataDao = getDaoSession().getTransactionLogMobileDao();
                                TransactionLogMobile transactionLogMobile = new TransactionLogMobile();
                                transactionLogMobile.setUsersId(loginResponse.getUserDetails().getUsers_id());
                                transactionLogMobile.setModule(TransactionModuleEnum.WORK_IMPACT_ATTACHMENT.ordinal());
                                transactionLogMobile.setStatus(SyncDataEnum.NOTSYNC.ordinal());
                                transactionLogMobile.setMobileId(attachment.getAttachmentIdMobile());
                                transactionLogMobile.setServerId(0L);
                                transactionLogMobile.setCreateDate(Calendar.getInstance().getTime());
                                mPronovosSyncDataDao.save(transactionLogMobile);

                            }
//                    workImpact.setIsAttachmentSync(false);
                        } else {
                            attachment.setIsAwsSync(true);
                        }
                        getDaoSession().getWorkImpactAttachmentsDao().update(attachment);
                    }
                }else if (attachment.getDeletedAt() != null){
                    getDaoSession().getWorkImpactAttachmentsDao().update(attachment);
                }
            }
            getDaoSession().getWorkImpactDao().update(workImpact);

            if (workImpact.getWorkImpactReportId() != 0) {
                List<TransactionLogMobile> workDetailLogMobiles = getDaoSession().getTransactionLogMobileDao().queryBuilder()
                        .where(TransactionLogMobileDao.Properties.MobileId.eq(workImpact.getWorkImpactReportIdMobile()),
                                TransactionLogMobileDao.Properties.ServerId.eq(Long.valueOf(workImpact.getWorkImpactReportId())),
                                TransactionLogMobileDao.Properties.Module.eq(TransactionModuleEnum.WORK_IMPACT.ordinal()),
                                TransactionLogMobileDao.Properties.Status.eq(SyncDataEnum.NOTSYNC),
                                TransactionLogMobileDao.Properties.UsersId.eq(userId)).list();

                if (workDetailLogMobiles.size() <= 0 && !attachmentUpdated) {
                    TransactionLogMobileDao mPronovosSyncDataDao = getDaoSession().getTransactionLogMobileDao();
                    TransactionLogMobile transactionLogMobile = new TransactionLogMobile();
                    transactionLogMobile.setUsersId(userId);
                    transactionLogMobile.setModule(TransactionModuleEnum.WORK_IMPACT.ordinal());
                    transactionLogMobile.setStatus(SyncDataEnum.NOTSYNC.ordinal());
                    transactionLogMobile.setMobileId(workImpact.getWorkImpactReportIdMobile());
                    transactionLogMobile.setServerId(Long.valueOf(workImpact.getWorkImpactReportId()));
                    transactionLogMobile.setCreateDate(Calendar.getInstance().getTime());
                    mPronovosSyncDataDao.save(transactionLogMobile);

                }
            }
            ((PronovosApplication) context.getApplicationContext()).setupAndStartWorkManager();
            return workImpact;
        } else return null;
    }

    private void syncWorkImpectattachment(WorkImpactAttachments attachment) {
        TransactionLogMobileDao mPronovosSyncDataDao = getDaoSession().getTransactionLogMobileDao();

        TransactionLogMobile transactionLogMobile = new TransactionLogMobile();

        transactionLogMobile.setUsersId(loginResponse.getUserDetails().getUsers_id());
        transactionLogMobile.setModule(TransactionModuleEnum.WORK_IMPACT_ATTACHMENT.ordinal());
        transactionLogMobile.setStatus(SyncDataEnum.NOTSYNC.ordinal());
        transactionLogMobile.setMobileId(attachment.getAttachmentIdMobile());
        transactionLogMobile.setServerId(0L);
        transactionLogMobile.setCreateDate(Calendar.getInstance().getTime());
        mPronovosSyncDataDao.save(transactionLogMobile);
    }

    private void deleteSyncData(TransactionLogMobile transactionLogMobile) {
        DeleteQuery<TransactionLogMobile> pronovosSyncDataDeleteQuery = getDaoSession().queryBuilder(TransactionLogMobile.class)
                .where(TransactionLogMobileDao.Properties.SyncId.eq(transactionLogMobile.getSyncId()))
                .buildDelete();
        pronovosSyncDataDeleteQuery.executeDeleteWithoutDetachingEntities();
    }


    public void updateAttachment(WorkImpactAttachments attachment) {
        getDaoSession().getWorkImpactAttachmentsDao().update(attachment);
    }

    public void updateWorkImpacts(WorkImpact WorkImpact) {
        getDaoSession().getWorkImpactDao().update(WorkImpact);
    }

    public void updateWorkImpact(WorkImpact workImpact) {
        getDaoSession().getWorkImpactDao().update(workImpact);

        loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        if (loginResponse != null && workImpact.getWorkImpactReportId() != 0) {
            int userId = loginResponse.getUserDetails().getUsers_id();
            List<TransactionLogMobile> workImpactLogMobiles = getDaoSession().getTransactionLogMobileDao().queryBuilder()
                    .where(TransactionLogMobileDao.Properties.MobileId.eq(workImpact.getWorkImpactReportIdMobile()),
                            TransactionLogMobileDao.Properties.ServerId.eq(Long.valueOf(workImpact.getWorkImpactReportId())),
                            TransactionLogMobileDao.Properties.Module.eq(TransactionModuleEnum.WORK_IMPACT.ordinal()),
                            TransactionLogMobileDao.Properties.Status.eq(SyncDataEnum.NOTSYNC),
                            TransactionLogMobileDao.Properties.UsersId.eq(userId)).list();

            if (workImpactLogMobiles.size() <= 0) {
                TransactionLogMobileDao mPronovosSyncDataDao = getDaoSession().getTransactionLogMobileDao();

                TransactionLogMobile transactionLogMobile = new TransactionLogMobile();

                transactionLogMobile.setUsersId(userId);
                transactionLogMobile.setModule(TransactionModuleEnum.WORK_IMPACT.ordinal());
                transactionLogMobile.setStatus(SyncDataEnum.NOTSYNC.ordinal());
                transactionLogMobile.setMobileId(workImpact.getWorkImpactReportIdMobile());
                transactionLogMobile.setServerId(Long.valueOf(workImpact.getWorkImpactReportId()));
                transactionLogMobile.setCreateDate(Calendar.getInstance().getTime());
                mPronovosSyncDataDao.save(transactionLogMobile);
                ((PronovosApplication) context.getApplicationContext()).setupAndStartWorkManager();
            }
        }
    }

    public WorkImpactAttachments getWorkImpactAttachments(Integer usersId, Long mobileId, Long serverId) {

        return getDaoSession().getWorkImpactAttachmentsDao().queryBuilder()
                .where(WorkImpactAttachmentsDao.Properties.UsersId.eq(usersId),
                        WorkImpactAttachmentsDao.Properties.AttachmentIdMobile.eq(mobileId),
                        WorkImpactAttachmentsDao.Properties.AttachmentId.eq(serverId.intValue())).list().get(0);

    }

    public WorkImpact getWorkImpact(Integer usersId, Long mobileId, Long serverId) {

        List<WorkImpact> workImpacts = getDaoSession().getWorkImpactDao().queryBuilder()
                .where(WorkImpactDao.Properties.UsersId.eq(usersId),
                        WorkImpactDao.Properties.WorkImpactReportIdMobile.eq(mobileId),
                        WorkImpactDao.Properties.WorkImpactReportId.eq(serverId.intValue())).list();
        if (workImpacts.size() > 0) {
            return workImpacts.get(0);
        } else {
            return null;
        }
    }

    public List<WorkImpact> getNotSyncWorkImpact() {
        List<WorkImpact> workDetails = getDaoSession().getWorkImpactDao().queryBuilder()
                .where(WorkImpactDao.Properties.IsSync.eq(false)).list();
        return workDetails;
    }

    public List<WorkImpactAttachments> getNotSyncWorkImpactAttachments() {
        List<WorkImpactAttachments> workDetails = getDaoSession().getWorkImpactAttachmentsDao().queryBuilder()
                .where(WorkImpactAttachmentsDao.Properties.IsAwsSync.eq(false)).list();
        return workDetails;
    }
}
