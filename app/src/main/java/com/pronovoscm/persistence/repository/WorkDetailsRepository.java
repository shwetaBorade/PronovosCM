package com.pronovoscm.persistence.repository;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.pronovoscm.PronovosApplication;
import com.pronovoscm.model.SyncDataEnum;
import com.pronovoscm.model.TransactionModuleEnum;
import com.pronovoscm.model.request.workdetails.WorkDetailsReportRequest;
import com.pronovoscm.model.response.login.LoginResponse;
import com.pronovoscm.model.response.workdetails.Attachments;
import com.pronovoscm.model.response.workdetails.WorkDetailsReport;
import com.pronovoscm.persistence.domain.CompanyList;
import com.pronovoscm.persistence.domain.DaoSession;
import com.pronovoscm.persistence.domain.TransactionLogMobile;
import com.pronovoscm.persistence.domain.TransactionLogMobileDao;
import com.pronovoscm.persistence.domain.WorkDetails;
import com.pronovoscm.persistence.domain.WorkDetailsAttachments;
import com.pronovoscm.persistence.domain.WorkDetailsAttachmentsDao;
import com.pronovoscm.persistence.domain.WorkDetailsDao;
import com.pronovoscm.utils.DateFormatter;
import com.pronovoscm.utils.SharedPref;

import org.greenrobot.greendao.query.DeleteQuery;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

public class WorkDetailsRepository extends AbstractRepository {
    private Context context;
    private LoginResponse loginResponse;

    public WorkDetailsRepository(DaoSession daoSession, Context context) {
        super(daoSession);
        this.context = context;
    }

    /**
     * Insert or update PunchlistRequest Assignees
     *
     * @param workDetailsReports
     * @return
     */
    public List<WorkDetails> doUpdateWorkDetailsTable(List<WorkDetailsReport> workDetailsReports, Date date, int projectId) {

        loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        try {
//            DeleteQuery<WorkDetails> crewListDeleteQuery = getDaoSession().queryBuilder(WorkDetails.class).
//                    where(WorkDetailsDao.Properties.ProjectId.eq(projectId),
//                            WorkDetailsDao.Properties.UsersId.eq(loginResponse.getUserDetails().getUsers_id()),
//                            WorkDetailsDao.Properties.CreatedAt.eq(date)).buildDelete();
//            crewListDeleteQuery.executeDeleteWithoutDetachingEntities();

            getDaoSession().callInTx(new Callable<List<WorkDetailsReport>>() {
                WorkDetailsDao mWorkDetailsDao = getDaoSession().getWorkDetailsDao();
                WorkDetailsAttachmentsDao mWorkDetailsAttachmentsDao = getDaoSession().getWorkDetailsAttachmentsDao();

                @Override
                public List<WorkDetailsReport> call() throws Exception {
                    for (WorkDetailsReport workDetailsReport : workDetailsReports) {

                        long workDetailMobileId = 0;
                        if (workDetailsReport.getDeletedAt() != null && !TextUtils.isEmpty(workDetailsReport.getDeletedAt())) {

                            DeleteQuery<WorkDetailsAttachments> workDetailsAttachmentsDeleteQuery = getDaoSession().queryBuilder(WorkDetailsAttachments.class)
                                    .where(WorkDetailsAttachmentsDao.Properties.WorkDetailsReportId.eq(workDetailsReport.getWorkDetailsReportId()),
                                            WorkDetailsAttachmentsDao.Properties.UsersId.eq(loginResponse.getUserDetails().getUsers_id()))
                                    .buildDelete();
                            workDetailsAttachmentsDeleteQuery.executeDeleteWithoutDetachingEntities();
                            DeleteQuery<WorkDetails> workDetailsDeleteQuery = getDaoSession().queryBuilder(WorkDetails.class)
                                    .where(WorkDetailsDao.Properties.ProjectId.eq(projectId),
                                            WorkDetailsDao.Properties.WorkDetailsReportId.eq(workDetailsReport.getWorkDetailsReportId()),
                                            WorkDetailsDao.Properties.UsersId.eq(loginResponse.getUserDetails().getUsers_id()))
                                    .buildDelete();
                            workDetailsDeleteQuery.executeDeleteWithoutDetachingEntities();

                        } else {
                            List<WorkDetails> workDetailsList = getDaoSession().getWorkDetailsDao().queryBuilder().where(
                                    WorkDetailsDao.Properties.ProjectId.eq(projectId),
                                    WorkDetailsDao.Properties.WorkDetailsReportId.eq(workDetailsReport.getWorkDetailsReportId()),
                                    WorkDetailsDao.Properties.UsersId.eq(loginResponse.getUserDetails().getUsers_id())
                            ).limit(1).list();
                            List<WorkDetails> workDetailsMobile = getDaoSession().getWorkDetailsDao().queryBuilder().where(
                                    WorkDetailsDao.Properties.ProjectId.eq(projectId),
                                    WorkDetailsDao.Properties.WorkDetailsReportIdMobile.eq(workDetailsReport.getWorkDetailsReportIdMobile()),
                                    WorkDetailsDao.Properties.WorkDetailsReportId.eq(0),
                                    WorkDetailsDao.Properties.UsersId.eq(loginResponse.getUserDetails().getUsers_id())
                            ).limit(1).list();
                            if (workDetailsList.size() > 0 || workDetailsMobile.size() > 0) {
                                WorkDetails workDetails;
                                if (workDetailsList.size() > 0) {
                                    workDetails = workDetailsList.get(0);
                                } else {
                                    workDetails = workDetailsMobile.get(0);
                                }
                                workDetails.setCompanyId(workDetailsReport.getCompanyId());
                                workDetails.setCompanyName(workDetailsReport.getCompanyname());
                                workDetails.setDeletedAt(workDetailsReport.getDeletedAt() != null && !workDetailsReport.getDeletedAt().equals("") ? DateFormatter.getDateFromDateTimeString(workDetailsReport.getDeletedAt()) : null);
                                workDetails.setIsSync(true);
                                workDetails.setProjectId(projectId);
                                workDetails.setWorkDetailsReportId(workDetailsReport.getWorkDetailsReportId());
                                workDetails.setWorkDetLocation(workDetailsReport.getWorkDetLocation());
                                workDetails.setWorkSummary(workDetailsReport.getWorkSummary());
                                workDetails.setType(workDetailsReport.getType());
                                workDetails.setUsersId(loginResponse.getUserDetails().getUsers_id());
                                workDetails.setCreatedAt(date);
                                workDetails.setIsAttachmentSync(true);
                                workDetailMobileId = workDetails.getWorkDetailsReportIdMobile();
                                mWorkDetailsDao.update(workDetails);


                            } else {


                                WorkDetails workDetails = new WorkDetails();

                                workDetails.setCompanyId(workDetailsReport.getCompanyId());
                                workDetails.setCompanyName(workDetailsReport.getCompanyname());
                                workDetails.setDeletedAt(workDetailsReport.getDeletedAt() != null && !workDetailsReport.getDeletedAt().equals("") ? DateFormatter.getDateFromDateTimeString(workDetailsReport.getDeletedAt()) : null);
                                workDetails.setIsSync(true);
                                workDetails.setProjectId(projectId);
                                workDetails.setWorkDetailsReportId(workDetailsReport.getWorkDetailsReportId());
                                workDetails.setWorkDetLocation(workDetailsReport.getWorkDetLocation());
                                workDetails.setIsAttachmentSync(true);
                                workDetails.setWorkSummary(workDetailsReport.getWorkSummary());
                                workDetails.setType(workDetailsReport.getType());
                                workDetails.setUsersId(loginResponse.getUserDetails().getUsers_id());
                                workDetails.setCreatedAt(date);
                                mWorkDetailsDao.save(workDetails);
                                List<WorkDetails> detailsList = getDaoSession().getWorkDetailsDao().queryBuilder().where(WorkDetailsDao.Properties.WorkDetailsReportIdMobile.isNotNull()).orderDesc(WorkDetailsDao.Properties.WorkDetailsReportIdMobile).limit(1).list();
                                workDetailMobileId = detailsList.get(0).getWorkDetailsReportIdMobile();

                            }

                            for (Attachments attachment : workDetailsReport.getAttachments()) {
                                List<WorkDetailsAttachments> detailsAttachments = getDaoSession().getWorkDetailsAttachmentsDao().queryBuilder().where(
                                        WorkDetailsAttachmentsDao.Properties.AttachmentId.eq(attachment.getAttachmentsId()),
                                        WorkDetailsAttachmentsDao.Properties.WorkDetailsReportId.eq(workDetailsReport.getWorkDetailsReportId()),
                                        WorkDetailsAttachmentsDao.Properties.UsersId.eq(loginResponse.getUserDetails().getUsers_id())
                                ).limit(1).list();
                                List<WorkDetailsAttachments> detailsAttachmentMobile = getDaoSession().getWorkDetailsAttachmentsDao().queryBuilder().where(
                                        WorkDetailsAttachmentsDao.Properties.AttachmentIdMobile.eq(attachment.getAttachmentsIdMobile()),
                                        WorkDetailsAttachmentsDao.Properties.AttachmentId.eq(0),
                                        WorkDetailsAttachmentsDao.Properties.WorkDetailsReportIdMobile.eq(workDetailMobileId),
                                        WorkDetailsAttachmentsDao.Properties.UsersId.eq(loginResponse.getUserDetails().getUsers_id())
                                ).limit(1).list();

                                if (detailsAttachments.size() > 0) {

                                    Log.i("Attachment", "addWorkDetail: " + detailsAttachments.get(0).getAttachmentIdMobile() + "  " + detailsAttachments.get(0).getAttachmentId());
                                }
                                if (detailsAttachmentMobile.size() > 0) {
                                    Log.i("Attachment", "addWorkDetail: " + detailsAttachmentMobile.get(0).getAttachmentIdMobile() + "  " + detailsAttachmentMobile.get(0).getAttachmentId());
                                }

                                if (attachment.getDeletedAt() != null && !TextUtils.isEmpty(attachment.getDeletedAt())) {
                                    DeleteQuery<WorkDetailsAttachments> workDetailsAttachmentsDeleteQuery = getDaoSession().queryBuilder(WorkDetailsAttachments.class)
                                            .where(WorkDetailsAttachmentsDao.Properties.WorkDetailsReportId.eq(workDetailsReport.getWorkDetailsReportId()),
                                                    WorkDetailsAttachmentsDao.Properties.AttachmentId.eq(attachment.getAttachmentsId()),
                                                    WorkDetailsAttachmentsDao.Properties.UsersId.eq(loginResponse.getUserDetails().getUsers_id()))
                                            .buildDelete();
                                    workDetailsAttachmentsDeleteQuery.executeDeleteWithoutDetachingEntities();
                                } else if (detailsAttachments.size() > 0 || detailsAttachmentMobile.size() > 0) {
                                    WorkDetailsAttachments workDetailsAttachments;
                                    if (detailsAttachments.size() > 0) {
                                        workDetailsAttachments = detailsAttachments.get(0);
                                    } else {
                                        workDetailsAttachments = detailsAttachmentMobile.get(0);
                                    }

                                    workDetailsAttachments.setAttachmentId(attachment.getAttachmentsId());
                                    workDetailsAttachments.setAttachmentPath(attachment.getAttachPath());
                                    workDetailsAttachments.setIsAwsSync(true);
                                    workDetailsAttachments.setDeletedAt(attachment.getDeletedAt() != null && !attachment.getDeletedAt().equals("") ? DateFormatter.getDateFromDateTimeString(attachment.getDeletedAt()) : null);
                                    workDetailsAttachments.setWorkDetailsReportId(workDetailsReport.getWorkDetailsReportId());
                                    workDetailsAttachments.setWorkDetailsReportIdMobile(workDetailMobileId);
                                    workDetailsAttachments.setUsersId(loginResponse.getUserDetails().getUsers_id());
                                    workDetailsAttachments.setType(attachment.getType());
                                    if (attachment.getType() != null)
                                        workDetailsAttachments.setType(attachment.getType());
                                    else workDetailsAttachments.setType("JPEG");

                                    mWorkDetailsAttachmentsDao.update(workDetailsAttachments);

                                } else {
                                    WorkDetailsAttachments workDetailsAttachments = new WorkDetailsAttachments();
                                    workDetailsAttachments.setAttachmentId(attachment.getAttachmentsId());
                                    workDetailsAttachments.setAttachmentPath(attachment.getAttachPath());
                                    workDetailsAttachments.setDeletedAt(attachment.getDeletedAt() != null && !attachment.getDeletedAt().equals("") ? DateFormatter.getDateFromDateTimeString(attachment.getDeletedAt()) : null);
                                    workDetailsAttachments.setWorkDetailsReportId(workDetailsReport.getWorkDetailsReportId());
                                    workDetailsAttachments.setIsAwsSync(true);
                                    workDetailsAttachments.setWorkDetailsReportIdMobile(workDetailMobileId);
                                    workDetailsAttachments.setUsersId(loginResponse.getUserDetails().getUsers_id());
                                    if (attachment.getType() != null)
                                        workDetailsAttachments.setType(attachment.getType());
                                    else workDetailsAttachments.setType("JPEG");
                                    mWorkDetailsAttachmentsDao.save(workDetailsAttachments);
                                }
                            }
                        }
                    }
                    return workDetailsReports;
                }

            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return getWorkDetails(projectId, date);
    }


    public List<WorkDetails> getWorkDetails(int projectId, Date date) {
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        List<WorkDetails> workDetails = getDaoSession().getWorkDetailsDao().queryBuilder().where(
                WorkDetailsDao.Properties.ProjectId.eq(projectId),
                WorkDetailsDao.Properties.UsersId.eq(loginResponse.getUserDetails().getUsers_id()),
                WorkDetailsDao.Properties.CreatedAt.eq(date),
                WorkDetailsDao.Properties.DeletedAt.isNull()
        ).list();
        return workDetails;
    }

    public List<WorkDetailsAttachments> getAttachments(Long workDetailsReportIdMobile) {

        loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        List<WorkDetailsAttachments> attachments = getDaoSession()
                .getWorkDetailsAttachmentsDao().queryBuilder().where(
                        WorkDetailsAttachmentsDao.Properties.UsersId.eq(loginResponse.getUserDetails().getUsers_id()),
                        WorkDetailsAttachmentsDao.Properties.WorkDetailsReportIdMobile.eq(workDetailsReportIdMobile),
                        WorkDetailsAttachmentsDao.Properties.DeletedAt.isNull()).list();
        return attachments;
    }

    public WorkDetailsAttachments getAttachment(Long workDetailsReportIdMobile, int attachmentID) {

        loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        List<WorkDetailsAttachments> attachments = getDaoSession().getWorkDetailsAttachmentsDao().queryBuilder().where(WorkDetailsAttachmentsDao.Properties.UsersId.eq(loginResponse.getUserDetails().getUsers_id()), WorkDetailsAttachmentsDao.Properties.WorkDetailsReportIdMobile.eq(workDetailsReportIdMobile), WorkDetailsAttachmentsDao.Properties.AttachmentId.eq(attachmentID)).limit(1).list();
        return attachments.get(0);
    }

    public WorkDetails getWorkDetailsItem(long workDetailMobileId) {
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        List<WorkDetails> workDetails = getDaoSession().getWorkDetailsDao().queryBuilder().where(WorkDetailsDao.Properties.WorkDetailsReportIdMobile.eq(workDetailMobileId), WorkDetailsDao.Properties.UsersId.eq(loginResponse.getUserDetails().getUsers_id())).list();
        if (workDetails.size() > 0) {
            return workDetails.get(0);
        } else {
            return null;
        }
    }


    /**
     * Get list of non synced work details of a project
     *
     * @param projectId
     * @param workDetailsDate
     * @return
     */
    public List<WorkDetails> getNonSyncWorkDetailList(int projectId, Date workDetailsDate) {
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        List<WorkDetails> workDetails = getDaoSession().getWorkDetailsDao().queryBuilder().where(
                WorkDetailsDao.Properties.ProjectId.eq(projectId), WorkDetailsDao.Properties.UsersId.eq(loginResponse.getUserDetails().getUsers_id()),
                WorkDetailsDao.Properties.IsSync.eq(false), WorkDetailsDao.Properties.CreatedAt.eq(workDetailsDate),
                WorkDetailsDao.Properties.IsAttachmentSync.eq(true)).list();
        return workDetails;
    }

    /**
     * Get list of non synced work details of a project
     *
     * @param projectId
     * @param workDetailsDate
     * @return
     */
    public List<WorkDetails> getNonSyncWorkDetailList2(int projectId, Date workDetailsDate) {
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        List<WorkDetails> workDetails = getDaoSession().getWorkDetailsDao().queryBuilder().where(
                WorkDetailsDao.Properties.ProjectId.eq(projectId),
                WorkDetailsDao.Properties.UsersId.eq(loginResponse.getUserDetails().getUsers_id())).list();
        return workDetails;
    }

    /**
     * Get list of non synced work details of a project
     *
     * @param projectId
     * @param workDetailsDate
     * @return
     */
    public List<WorkDetailsReportRequest> getNonSyncWorkDetailSyncAttachmentList(int projectId, Date workDetailsDate) {
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        List<WorkDetails> workDetails = getDaoSession().getWorkDetailsDao().queryBuilder().where(
                WorkDetailsDao.Properties.ProjectId.eq(projectId),
                WorkDetailsDao.Properties.UsersId.eq(loginResponse.getUserDetails().getUsers_id()),
                WorkDetailsDao.Properties.IsSync.eq(false),
                WorkDetailsDao.Properties.CreatedAt.eq(workDetailsDate),
                WorkDetailsDao.Properties.IsAttachmentSync.eq(true)).list();

        List<WorkDetailsReportRequest> workDetailsReportRequests = new ArrayList<>();
        for (WorkDetails workDetail : workDetails) {
            WorkDetailsReportRequest workDetailsReport = new WorkDetailsReportRequest();
            workDetailsReport.setDeletedAt(workDetail.getDeletedAt() != null ? DateFormatter.formatDateTimeHHForService(workDetail.getDeletedAt()) : "");
            workDetailsReport.setType(workDetail.getType());
            workDetailsReport.setCompanyId(String.valueOf(workDetail.getCompanyId()));
            workDetailsReport.setProjectId(String.valueOf(projectId));
            workDetailsReport.setCompanyname(workDetail.getCompanyName());
            workDetailsReport.setWorkDetailsReportId(String.valueOf(workDetail.getWorkDetailsReportId()));
            workDetailsReport.setWorkDetailsReportIdMobile(String.valueOf(workDetail.getWorkDetailsReportIdMobile()));
            workDetailsReport.setWorkDetLocation(workDetail.getWorkDetLocation());
            workDetailsReport.setWorkSummary(workDetail.getWorkSummary());
            workDetailsReport.setCreatedAt(workDetail.getCreatedAt() != null ? DateFormatter.formatDateTimeHHForService(workDetail.getCreatedAt()) : "");
            List<WorkDetailsAttachments> attachments = getSyncedAttachments(workDetail.getWorkDetailsReportIdMobile());
            List<com.pronovoscm.model.request.workdetails.Attachments> attachmentsList = new ArrayList<>();
            for (WorkDetailsAttachments attachment :
                    attachments) {
                com.pronovoscm.model.request.workdetails.Attachments attachmnt = new com.pronovoscm.model.request.workdetails.Attachments();
                attachmnt.setAttachmentsId(attachment.getAttachmentId() != null ? attachment.getAttachmentId() : 0);
                attachmnt.setAttachmentsIdMobile((int) (long) attachment.getAttachmentIdMobile());
                attachmnt.setAttachPath(attachment.getAttachmentPath());
                attachmnt.setDeletedAt(attachment.getDeletedAt() != null ? DateFormatter.formatDateTimeHHForService(attachment.getDeletedAt()) : "");
                attachmentsList.add(attachmnt);

            }

            workDetailsReport.setAttachments(attachmentsList);
            workDetailsReportRequests.add(workDetailsReport);

        }
        return workDetailsReportRequests;
    }


    /**
     * Get list of non synced work details whose attachments are also not synced of a project
     *
     * @param projectId
     * @param workDetailsDate
     * @return
     */
    public List<WorkDetails> getNonSyncWorkDetailsWithNonSyncedAttachments(int projectId, Date workDetailsDate) {
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        List<WorkDetails> workDetails = getDaoSession().getWorkDetailsDao().queryBuilder().where(
                WorkDetailsDao.Properties.ProjectId.eq(projectId), WorkDetailsDao.Properties.UsersId.eq(loginResponse.getUserDetails().getUsers_id()),
                WorkDetailsDao.Properties.IsSync.eq(false), WorkDetailsDao.Properties.IsAttachmentSync.eq(false),
                WorkDetailsDao.Properties.CreatedAt.eq(workDetailsDate)).list();
        return workDetails;
    }


    /**
     * Get list of non synced attachments of work details
     *
     * @param workDetailsReportIdMobile
     * @return
     */
    public List<WorkDetailsAttachments> getNonSyncedWorkDetailAttachments(Long workDetailsReportIdMobile) {

        loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        List<WorkDetailsAttachments> attachments = getDaoSession().getWorkDetailsAttachmentsDao().queryBuilder().where(
                WorkDetailsAttachmentsDao.Properties.UsersId.eq(loginResponse.getUserDetails().getUsers_id()),
                WorkDetailsAttachmentsDao.Properties.IsAwsSync.eq(false), // sort only non synced attachments
                WorkDetailsAttachmentsDao.Properties.WorkDetailsReportIdMobile.eq(workDetailsReportIdMobile)).list();
        return attachments;
    }

    /**
     * Get list of non synced attachments of work details
     *
     * @return
     */
    public List<WorkDetailsAttachments> getNonSyncedAttachments() {

        loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        List<WorkDetailsAttachments> attachments = getDaoSession().getWorkDetailsAttachmentsDao().queryBuilder().where(
                WorkDetailsAttachmentsDao.Properties.UsersId.eq(loginResponse.getUserDetails().getUsers_id()),
                WorkDetailsAttachmentsDao.Properties.IsAwsSync.eq(false)).list();
        return attachments;
    }

    /**
     * Get list of non synced attachments of work details
     *
     * @param workDetailsReportIdMobile
     * @return
     */
    public List<WorkDetailsAttachments> getSyncedAttachments(Long workDetailsReportIdMobile) {

        loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        List<WorkDetailsAttachments> attachments = getDaoSession().getWorkDetailsAttachmentsDao().queryBuilder().where(
                WorkDetailsAttachmentsDao.Properties.UsersId.eq(loginResponse.getUserDetails().getUsers_id()),
                WorkDetailsAttachmentsDao.Properties.IsAwsSync.eq(true), // sort only non synced attachments
                WorkDetailsAttachmentsDao.Properties.WorkDetailsReportIdMobile.eq(workDetailsReportIdMobile)).list();
        return attachments;
    }

    /**
     * Add new work detail in db
     *
     * @param projectId
     * @param mSelectedCompany
     * @param attachmentList
     * @param dateWorkDetail
     * @param location
     * @param summary
     * @return
     */
    public WorkDetails addWorkDetail(int projectId, CompanyList mSelectedCompany, List<WorkDetailsAttachments> attachmentList,
                                     Date dateWorkDetail, String location, String summary) {
        WorkDetails workDetail = new WorkDetails();
        // saveWorkDetails.setWorkDetailsReportIdMobile(0); // it is auto incremented when saved in db
        if (mSelectedCompany != null) {
            workDetail.setCompanyId(mSelectedCompany.getCompanyId());
            workDetail.setCompanyName(mSelectedCompany.getName());
        }

        workDetail.setIsSync(false);
        if (attachmentList.size() > 0) {
            workDetail.setIsAttachmentSync(false);
        } else {
            workDetail.setIsAttachmentSync(true);

        }
        workDetail.setProjectId(projectId);
        workDetail.setType(mSelectedCompany.getType());
        workDetail.setUsersId(loginResponse.getUserDetails().getUsers_id());
        workDetail.setWorkDetailsReportId(0);
        workDetail.setWorkDetLocation(location);
        workDetail.setWorkSummary(summary);
        workDetail.setCreatedAt(dateWorkDetail);

        // Save work detail
        getDaoSession().getWorkDetailsDao().save(workDetail);
        List<WorkDetails> workDetailsList = getDaoSession().getWorkDetailsDao().queryBuilder().where(
                WorkDetailsDao.Properties.WorkDetailsReportIdMobile.isNotNull()).orderDesc(WorkDetailsDao.Properties.WorkDetailsReportIdMobile).limit(1).list();
        long workDetailMobileID = workDetailsList.get(0).getWorkDetailsReportIdMobile();


        // save attachments one by one in db with respect to {@code workDetailMobileID}
        for (WorkDetailsAttachments attachment : attachmentList) {
            attachment.setUsersId(loginResponse.getUserDetails().getUsers_id());
            attachment.setIsAwsSync(false);
            attachment.setWorkDetailsReportId(0);
            attachment.setAttachmentId(0);
            attachment.setWorkDetailsReportIdMobile(workDetailMobileID);
            // Save work detail
            getDaoSession().getWorkDetailsAttachmentsDao().save(attachment);
            List<WorkDetailsAttachments> attachmentList1 = getDaoSession().getWorkDetailsAttachmentsDao().queryBuilder().where(
                    WorkDetailsAttachmentsDao.Properties.AttachmentIdMobile.isNotNull()).orderDesc(WorkDetailsAttachmentsDao.Properties.AttachmentIdMobile)
                    .limit(1).list();
            Log.i("Attachment", "addWorkDetail addWorkDetail: " + attachmentList1.get(0).getAttachmentIdMobile() + "  " + attachmentList1.get(0).getAttachmentId());

            TransactionLogMobileDao mPronovosSyncDataDao = getDaoSession().getTransactionLogMobileDao();

            TransactionLogMobile transactionLogMobile = new TransactionLogMobile();

            transactionLogMobile.setUsersId(loginResponse.getUserDetails().getUsers_id());
            transactionLogMobile.setModule(TransactionModuleEnum.WORK_DETAIL_ATTACHMENT.ordinal());
            transactionLogMobile.setStatus(SyncDataEnum.NOTSYNC.ordinal());
            transactionLogMobile.setMobileId(attachmentList1.get(0).getAttachmentIdMobile());
            transactionLogMobile.setServerId(0L);
            transactionLogMobile.setCreateDate(Calendar.getInstance().getTime());
            mPronovosSyncDataDao.save(transactionLogMobile);
        }

        TransactionLogMobileDao mPronovosSyncDataDao = getDaoSession().getTransactionLogMobileDao();

        TransactionLogMobile transactionLogMobile = new TransactionLogMobile();

        transactionLogMobile.setUsersId(loginResponse.getUserDetails().getUsers_id());
        transactionLogMobile.setModule(TransactionModuleEnum.WORK_DETAIL.ordinal());
        transactionLogMobile.setStatus(SyncDataEnum.NOTSYNC.ordinal());
        transactionLogMobile.setMobileId(workDetailMobileID);
        transactionLogMobile.setServerId(0L);
        transactionLogMobile.setCreateDate(Calendar.getInstance().getTime());
        mPronovosSyncDataDao.save(transactionLogMobile);
        ((PronovosApplication) context.getApplicationContext()).setupAndStartWorkManager();

        return workDetail;
    }

    /**
     * Add new work detail in db
     *
     * @param projectId
     * @param mSelectedCompany
     * @param attachmentList
     * @param dateWorkDetail
     * @param location
     * @param summary
     * @return
     */
    public WorkDetails updateWorkDetail(int projectId, CompanyList mSelectedCompany, List<WorkDetailsAttachments> attachmentList,
                                        Date dateWorkDetail, String location, String summary, WorkDetails workDetail) {
        boolean workDetaiAttachmentupdated = false;
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        if (loginResponse != null) {
            int userId = loginResponse.getUserDetails().getUsers_id();
            workDetail.setCompanyId(mSelectedCompany.getCompanyId());
            workDetail.setCompanyName(mSelectedCompany.getName());
            workDetail.setIsSync(false);
            workDetail.setIsAttachmentSync(false);

            if (workDetail.getDeletedAt() != null) {
                workDetail.setIsAttachmentSync(true);
            }
            workDetail.setProjectId(projectId);
            workDetail.setType(mSelectedCompany.getType());
            workDetail.setUsersId(loginResponse.getUserDetails().getUsers_id());
            workDetail.setWorkDetLocation(location);
            workDetail.setWorkSummary(summary);
            workDetail.setCreatedAt(dateWorkDetail);
            // Update work detail
            // save attachments one by one in db with respect to {@code workDetailMobileID}
            for (WorkDetailsAttachments attachment : attachmentList) {
                attachment.setUsersId(loginResponse.getUserDetails().getUsers_id());
//            attachment.setIsAwsSync(false);
                attachment.setWorkDetailsReportId(workDetail.getWorkDetailsReportId());
                attachment.setWorkDetailsReportIdMobile(workDetail.getWorkDetailsReportIdMobile());

                // Save work detail
                if (attachment.getAttachmentId() == null || attachment.getAttachmentId() == 0) {
                    attachment.setIsAwsSync(false);
                    if (attachment.getDeletedAt() != null) {
                        attachment.setIsAwsSync(true);
                        List<TransactionLogMobile> transactionLogList =
                                getDaoSession().getTransactionLogMobileDao().queryBuilder().where(
                                        TransactionLogMobileDao.Properties.Module.eq(TransactionModuleEnum.WORK_DETAIL_ATTACHMENT.ordinal()),
                                        TransactionLogMobileDao.Properties.MobileId.eq(attachment.getAttachmentIdMobile()),
                                        TransactionLogMobileDao.Properties.ServerId.eq(0),
                                        TransactionLogMobileDao.Properties.UsersId.eq(userId)).list();
                        if (transactionLogList.size() > 0) {
                            TransactionLogMobile transactionLogMobile = transactionLogList.get(0);
                            deleteSyncData(transactionLogMobile);
                        }
                        DeleteQuery<WorkDetailsAttachments> workDetailsAttachmentsDeleteQuery = getDaoSession().queryBuilder(WorkDetailsAttachments.class)
                                .where(WorkDetailsAttachmentsDao.Properties.AttachmentIdMobile.eq(attachment.getAttachmentIdMobile()))
                                .buildDelete();
                        workDetailsAttachmentsDeleteQuery.executeDeleteWithoutDetachingEntities();
                    } else {

                        attachment.setAttachmentId(0);
                        getDaoSession().getWorkDetailsAttachmentsDao().save(attachment);
                    }

                }
                if (!attachment.getIsAwsSync()) {
                    WorkDetailsAttachments attachmnt = getAttachment(workDetail.getWorkDetailsReportIdMobile(), attachment.getAttachmentId());
                    attachment.setDeletedAt(attachmnt.getDeletedAt());
                    if (attachmnt.getDeletedAt() == null) {
                        attachment.setIsAwsSync(attachmnt.getIsAwsSync());
                        if (!attachmnt.getIsAwsSync()) {
                            workDetaiAttachmentupdated = true;
                            TransactionLogMobileDao mPronovosSyncDataDao = getDaoSession().getTransactionLogMobileDao();
                            TransactionLogMobile transactionLogMobile = new TransactionLogMobile();
                            transactionLogMobile.setUsersId(userId);
                            transactionLogMobile.setModule(TransactionModuleEnum.WORK_DETAIL_ATTACHMENT.ordinal());
                            transactionLogMobile.setStatus(SyncDataEnum.NOTSYNC.ordinal());
                            transactionLogMobile.setMobileId(attachment.getAttachmentIdMobile());
                            transactionLogMobile.setServerId(0L);
                            transactionLogMobile.setCreateDate(Calendar.getInstance().getTime());
                            mPronovosSyncDataDao.save(transactionLogMobile);

                        }
//                    workDetail.setIsAttachmentSync(false);
                    } else {
                        attachment.setIsAwsSync(true);
//                    attachment.setIsAwsSync(false);
                    }
                    getDaoSession().getWorkDetailsAttachmentsDao().update(attachment);


                }else if (attachment.getDeletedAt() != null){
                    getDaoSession().getWorkDetailsAttachmentsDao().update(attachment);
                }
            }
            getDaoSession().getWorkDetailsDao().update(workDetail);

            if (workDetail.getWorkDetailsReportId() != 0) {
                List<TransactionLogMobile> workDetailLogMobiles = getDaoSession().getTransactionLogMobileDao().queryBuilder()
                        .where(TransactionLogMobileDao.Properties.MobileId.eq(workDetail.getWorkDetailsReportIdMobile()),
                                TransactionLogMobileDao.Properties.ServerId.eq(Long.valueOf(workDetail.getWorkDetailsReportId())),
                                TransactionLogMobileDao.Properties.Module.eq(TransactionModuleEnum.WORK_DETAIL.ordinal()),
                                TransactionLogMobileDao.Properties.Status.eq(SyncDataEnum.NOTSYNC),
                                TransactionLogMobileDao.Properties.UsersId.eq(userId)).list();

                if (workDetailLogMobiles.size() <= 0) {
                    TransactionLogMobileDao mPronovosSyncDataDao = getDaoSession().getTransactionLogMobileDao();

                    TransactionLogMobile transactionLogMobile = new TransactionLogMobile();

                    transactionLogMobile.setUsersId(userId);
                    transactionLogMobile.setModule(TransactionModuleEnum.WORK_DETAIL.ordinal());
                    transactionLogMobile.setStatus(SyncDataEnum.NOTSYNC.ordinal());
                    transactionLogMobile.setMobileId(workDetail.getWorkDetailsReportIdMobile());
                    transactionLogMobile.setServerId(Long.valueOf(workDetail.getWorkDetailsReportId()));
                    transactionLogMobile.setCreateDate(Calendar.getInstance().getTime());
                    mPronovosSyncDataDao.save(transactionLogMobile);

                }
            }
            ((PronovosApplication) context.getApplicationContext()).setupAndStartWorkManager();
            return workDetail;
        } else return null;
    }

    private void deleteSyncData(TransactionLogMobile transactionLogMobile) {
        DeleteQuery<TransactionLogMobile> pronovosSyncDataDeleteQuery = getDaoSession().queryBuilder(TransactionLogMobile.class)
                .where(TransactionLogMobileDao.Properties.SyncId.eq(transactionLogMobile.getSyncId()))
                .buildDelete();
        pronovosSyncDataDeleteQuery.executeDeleteWithoutDetachingEntities();
    }


    public void updateAttachment(WorkDetailsAttachments attachment) {
        getDaoSession().getWorkDetailsAttachmentsDao().update(attachment);
    }

    public void updateWorkDetail(WorkDetails workDetails) {
        getDaoSession().getWorkDetailsDao().update(workDetails);

        loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        if (loginResponse != null && workDetails.getWorkDetailsReportId() != 0) {
            int userId = loginResponse.getUserDetails().getUsers_id();
            List<TransactionLogMobile> workDetailtLogMobiles = getDaoSession().getTransactionLogMobileDao().queryBuilder()
                    .where(TransactionLogMobileDao.Properties.MobileId.eq(workDetails.getWorkDetailsReportIdMobile()),
                            TransactionLogMobileDao.Properties.ServerId.eq(Long.valueOf(workDetails.getWorkDetailsReportId())),
                            TransactionLogMobileDao.Properties.Module.eq(TransactionModuleEnum.WORK_DETAIL.ordinal()),
                            TransactionLogMobileDao.Properties.Status.eq(SyncDataEnum.NOTSYNC),
                            TransactionLogMobileDao.Properties.UsersId.eq(userId)).list();

            if (workDetailtLogMobiles.size() <= 0) {
                TransactionLogMobileDao mPronovosSyncDataDao = getDaoSession().getTransactionLogMobileDao();

                TransactionLogMobile transactionLogMobile = new TransactionLogMobile();

                transactionLogMobile.setUsersId(userId);
                transactionLogMobile.setModule(TransactionModuleEnum.WORK_DETAIL.ordinal());
                transactionLogMobile.setStatus(SyncDataEnum.NOTSYNC.ordinal());
                transactionLogMobile.setMobileId(workDetails.getWorkDetailsReportIdMobile());
                transactionLogMobile.setServerId(Long.valueOf(workDetails.getWorkDetailsReportId()));
                transactionLogMobile.setCreateDate(Calendar.getInstance().getTime());
                mPronovosSyncDataDao.save(transactionLogMobile);
                ((PronovosApplication) context.getApplicationContext()).setupAndStartWorkManager();
            }
        }

    }

    public void updateWorkDetails(WorkDetails workDetails) {
        getDaoSession().getWorkDetailsDao().update(workDetails);
    }

    public WorkDetailsAttachments getWorkDetailsAttachments(Integer usersId, Long mobileId, Long serverId) {

        List<WorkDetailsAttachments> workDetailsAttachments = getDaoSession().getWorkDetailsAttachmentsDao().queryBuilder()
                .where(WorkDetailsAttachmentsDao.Properties.UsersId.eq(usersId),
                        WorkDetailsAttachmentsDao.Properties.AttachmentIdMobile.eq(mobileId),
                        WorkDetailsAttachmentsDao.Properties.AttachmentId.eq(serverId.intValue())).list();

        if (workDetailsAttachments.size() > 0) {
            return workDetailsAttachments.get(0);
        } else {
            return null;
        }
    }

    public WorkDetails getWorkDetails(Integer usersId, Long mobileId, Long serverId) {
        List<WorkDetails> workDetails = getDaoSession().getWorkDetailsDao().queryBuilder()
                .where(WorkDetailsDao.Properties.UsersId.eq(usersId),
                        WorkDetailsDao.Properties.WorkDetailsReportIdMobile.eq(mobileId),
                        WorkDetailsDao.Properties.WorkDetailsReportId.eq(serverId.intValue())).list();
        if (workDetails.size() > 0) {
            return workDetails.get(0);
        } else {
            return null;
        }
    }

    public List<WorkDetailsAttachments> getNotSyncWorkDetailAttachment() {

        List<WorkDetailsAttachments> workDetails = getDaoSession().getWorkDetailsAttachmentsDao().queryBuilder()
                .where(WorkDetailsAttachmentsDao.Properties.IsAwsSync.eq(false)).list();
        return workDetails;
    }
    public List<WorkDetails> getNotSyncWorkDetails() {

        List<WorkDetails> workDetails = getDaoSession().getWorkDetailsDao().queryBuilder()
                .where(WorkDetailsDao.Properties.IsSync.eq(false)).list();
        return workDetails;
    }
}
