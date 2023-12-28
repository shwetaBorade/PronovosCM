package com.pronovoscm.persistence.repository;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.pronovoscm.PronovosApplication;
import com.pronovoscm.model.PunchListStatus;
import com.pronovoscm.model.SyncDataEnum;
import com.pronovoscm.model.TransactionModuleEnum;
import com.pronovoscm.model.request.punchlist.Attachments;
import com.pronovoscm.model.request.punchlist.PunchList;
import com.pronovoscm.model.response.login.LoginResponse;
import com.pronovoscm.model.response.punchlist.Attachment;
import com.pronovoscm.model.response.punchlist.LinkedDrawings;
import com.pronovoscm.model.response.punchlist.PunchListHistory;
import com.pronovoscm.model.response.punchlist.Punchlist;
import com.pronovoscm.persistence.domain.DaoSession;
import com.pronovoscm.persistence.domain.DrawingList;
import com.pronovoscm.persistence.domain.DrwPunchlist;
import com.pronovoscm.persistence.domain.DrwPunchlistDao;
import com.pronovoscm.persistence.domain.PunchListAttachments;
import com.pronovoscm.persistence.domain.PunchListAttachmentsDao;
import com.pronovoscm.persistence.domain.PunchlistAssignee;
import com.pronovoscm.persistence.domain.PunchlistDb;
import com.pronovoscm.persistence.domain.PunchlistDbDao;
import com.pronovoscm.persistence.domain.PunchlistDrawing;
import com.pronovoscm.persistence.domain.PunchlistDrawingDao;
import com.pronovoscm.persistence.domain.TransactionLogMobile;
import com.pronovoscm.persistence.domain.TransactionLogMobileDao;
import com.pronovoscm.persistence.domain.punchlist.PunchListHistoryDb;
import com.pronovoscm.persistence.domain.punchlist.PunchListHistoryDbDao;
import com.pronovoscm.persistence.domain.punchlist.PunchListRejectReasonAttachments;
import com.pronovoscm.persistence.domain.punchlist.PunchListRejectReasonAttachmentsDao;
import com.pronovoscm.persistence.domain.punchlist.TransactionHistoryLogMobile;
import com.pronovoscm.persistence.domain.punchlist.TransactionHistoryLogMobileDao;
import com.pronovoscm.services.NetworkService;
import com.pronovoscm.ui.punchlist.adapter.PunchlistAssigneeList;
import com.pronovoscm.utils.DateFormatter;
import com.pronovoscm.utils.SharedPref;

import org.greenrobot.greendao.query.DeleteQuery;
import org.greenrobot.greendao.query.WhereCondition;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;


public class PunchListRepository extends AbstractRepository {
    public static final String TAG = PunchListRepository.class.getSimpleName();

    private Context context;
    private LoginResponse loginResponse;

    public PunchListRepository(DaoSession daoSession, Context context) {
        super(daoSession);
        this.context = context;
    }

    /**
     * Insert or update Punch List
     *
     * @param punchlists
     * @param projectId
     * @return
     */
    public List<PunchlistDb> doUpdatePunchListDb(List<Punchlist> punchlists, int projectId) {

        loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        if (loginResponse != null) {
            int userId = loginResponse.getUserDetails().getUsers_id();

            try {
                getDaoSession().callInTx(new Callable<List<Punchlist>>() {
                    PunchlistDbDao mPunchlistDbDao = getDaoSession().getPunchlistDbDao();
                    PunchListAttachmentsDao mPunchListAttachmentsDao = getDaoSession().getPunchListAttachmentsDao();
                    PunchlistDrawingDao punchlistDrawingDao = getDaoSession().getPunchlistDrawingDao();
                    DrwPunchlistDao mDrwPunchlistDao = getDaoSession().getDrwPunchlistDao();

                    @Override
                    public List<Punchlist> call() throws Exception {
                        for (Punchlist punchlist : punchlists) {

                            long punchListMobileId = 0;
                            if (punchlist.getDeletedAt() != null && !TextUtils.isEmpty(punchlist.getDeletedAt())) {

                                DeleteQuery<PunchListAttachments> PunchListAttachmentsDeleteQuery = getDaoSession()
                                        .queryBuilder(PunchListAttachments.class)
                                        .where(PunchListAttachmentsDao.Properties.PunchListId.eq(punchlist.getPunchListsId()),
                                                PunchListAttachmentsDao.Properties.UsersId.eq(userId))
                                        .buildDelete();
                                PunchListAttachmentsDeleteQuery.executeDeleteWithoutDetachingEntities();
                                DeleteQuery<PunchlistDb> punchlistDbDeleteQuery = getDaoSession().queryBuilder(PunchlistDb.class)
                                        .where(PunchlistDbDao.Properties.PjProjectsId.eq(projectId),
                                                PunchlistDbDao.Properties.PunchlistId.eq(punchlist.getPunchListsId()),
                                                PunchlistDbDao.Properties.UserId.eq(userId))
                                        .buildDelete();
                                punchlistDbDeleteQuery.executeDeleteWithoutDetachingEntities();

                            } else {
                                List<PunchlistDb> punchlistDbs = getDaoSession().getPunchlistDbDao().queryBuilder().where(
                                        PunchlistDbDao.Properties.PjProjectsId.eq(projectId),
                                        PunchlistDbDao.Properties.PunchlistId.eq(punchlist.getPunchListsId()),
                                        PunchlistDbDao.Properties.UserId.eq(userId)
                                ).limit(1).list();
                                List<PunchlistDb> punchlistDbs1 = getDaoSession().getPunchlistDbDao().queryBuilder().where(
                                        PunchlistDbDao.Properties.PjProjectsId.eq(projectId),
                                        PunchlistDbDao.Properties.PunchlistIdMobile.eq(punchlist.getPunchListsIdMobile()),
                                        PunchlistDbDao.Properties.PunchlistId.eq(0),
                                        PunchlistDbDao.Properties.UserId.eq(userId)
                                ).limit(1).list();
                                List<TransactionLogMobile> transactionLogMobiles = getDaoSession().getTransactionLogMobileDao().queryBuilder()
                                        .where(
                                                TransactionLogMobileDao.Properties.MobileId.eq(punchlist.getPunchListsIdMobile()),
                                                TransactionLogMobileDao.Properties.Module.eq(TransactionModuleEnum.PUNCHLIST.ordinal()),
                                                TransactionLogMobileDao.Properties.Status.notEq(SyncDataEnum.SYNC),
                                                TransactionLogMobileDao.Properties.UsersId.eq(userId)
                                        ).limit(1).list();
                                PunchListHistoryDb historyDb = getPunchListHistoryDetailSingle(userId, Long.valueOf(punchlist.getPunchListsIdMobile()));

                                if (punchlistDbs.size() > 0 || punchlistDbs1.size() > 0) {
                                    PunchlistDb punchlistDb;
                                    if (punchlistDbs.size() > 0) {
                                        punchlistDb = punchlistDbs.get(0);
                                    } else {
                                        punchlistDb = punchlistDbs1.get(0);
                                        List<DrwPunchlist> drwPunchlists = getDaoSession().getDrwPunchlistDao().queryBuilder().where(
                                                DrwPunchlistDao.Properties.PjProjectsId.eq(projectId),
                                                DrwPunchlistDao.Properties.PunchlistIdMobile.eq(punchlist.getPunchListsIdMobile()),
                                                DrwPunchlistDao.Properties.PunchlistId.eq(0),
                                                DrwPunchlistDao.Properties.UserId.eq(userId)
                                        ).limit(1).list();
                                        if (drwPunchlists.size() > 0) {
                                            DrwPunchlist drwPunchlist = drwPunchlists.get(0);
                                            drwPunchlist.setPunchlistId(punchlist.getPunchListsId());
                                            mDrwPunchlistDao.insertOrReplace(drwPunchlist);
                                        }

                                    }
//                                    if(punchlistDb.getIsInProgress()) {
//                                        Log.d("Manya", "IF call punchlistDbForStatusCheck.getIsSync(): " + punchlistDb.getIsSync());
                                    Date deletedAt = punchlistDb.getDeletedAt();
                                    punchlistDb.setDeletedAt(punchlist.getDeletedAt() != null && !punchlist.getDeletedAt().equals("") ? DateFormatter.getDateFromDateTimeString(punchlist.getDeletedAt()) : deletedAt != null ? deletedAt : null);
//                                    punchlistDb.setIsSync(true);
                                    punchlistDb.setPjProjectsId(projectId);
                                    //TODO: Nitin
                                    //punchlistDb.setAssignedTo(String.valueOf(punchlist.getAssignedTo()));
                                    punchlistDb.setAssignedTo(punchlist.getAssignedTo());
                                    punchlistDb.setAssignedCcList(punchlist.getAssignedCCs());

                                    punchlistDb.setAssigneeName(punchlist.getAssigneeName());
                                    punchlistDb.setDescriptions(punchlist.getDescription());
                                    /*if(!(transactionLogMobiles.size() > 0)) {
                                        punchlistDb.setIsSync(true);
                                        punchlistDb.setStatus(punchlist.getStatus());
                                        ((PronovosApplication) context.getApplicationContext()).setupAndStartWorkManager();
                                    }*/
                                    punchlistDb.setIsSync(true);
                                    punchlistDb.setStatus(punchlist.getStatus());

                                    punchlistDb.setItemNumber(punchlist.getItemNumber());
                                    punchlistDb.setLocation(punchlist.getLocation());
                                    punchlistDb.setComments(punchlist.getComments());
                                    punchlistDb.setPunchlistId(punchlist.getPunchListsId());

                                    punchlistDb.setCreatedBy(punchlist.getCreatedBy());
                                    punchlistDb.setCreatedByUserId(String.valueOf(punchlist.getCreatedByUserid()));
                                    punchlistDb.setDateCreated(punchlist.getDateCreated() != null && !punchlist.getDateCreated().equals("") ? DateFormatter.getDateFromString(punchlist.getDateCreated()) : null);
                                    punchlistDb.setDateDue(punchlist.getDateDue() != null && !punchlist.getDateDue().equals("") ? DateFormatter.getDateFromString(punchlist.getDateDue(), DateFormatter.DATE_FORMAT_MMDDYYYY) : null);
                                    punchlistDb.setUserId(userId);
                                    punchlistDb.setCreatedAt(punchlist.getCreatedAt() != null && !punchlist.getCreatedAt().equals("") ? DateFormatter.getDateFromString(punchlist.getCreatedAt(), DateFormatter.DATE_FORMAT_MMDDYYYY) : null);
                                    punchlistDb.setIsAttachmentSync(true);
                                    /*JSONObject obj = new JSONObject();
                                    obj.put("link_drawing",punchlist.getLinkedDrawings());
                                    punchlistDb.setLinkedDrawings(obj.toString());
                                    */
                                    //   Log.i(TAG, punchlistDb.getDateDue()+" edit mode indb "+punchlist.getDateDue() + "call: "+punchlist.getDescription());

                                    punchListMobileId = punchlistDb.getPunchlistIdMobile();

                                    punchlistDb.setSendEmail(0);

                                    mPunchlistDbDao.insertOrReplace(punchlistDb);
//                                    }
                                    updatePunchListHistory(punchlistDb);

                                } else {

                                    PunchlistDb punchlistDb = new PunchlistDb();
                                    punchListMobileId = generateUniqueMobilePunchListId();
                                    punchlistDb.setDeletedAt(punchlist.getDeletedAt() != null && !punchlist.getDeletedAt().equals("") ? DateFormatter.getDateFromDateTimeString(punchlist.getDeletedAt()) : null);
                                    punchlistDb.setIsSync(true);
                                    punchlistDb.setPjProjectsId(projectId);
                                    //TODO: Nitin
//                                    punchlistDb.setAssignedTo(String.valueOf(punchlist.getAssignedTo()));
                                    punchlistDb.setAssignedTo(punchlist.getAssignedTo());
                                    punchlistDb.setAssignedCcList(punchlist.getAssignedCCs());

                                    punchlistDb.setAssigneeName(punchlist.getAssigneeName());
                                    punchlistDb.setDescriptions(punchlist.getDescription());
//                                    punchlistDb.setIsSync(true);
                                    punchlistDb.setItemNumber(punchlist.getItemNumber());
                                    punchlistDb.setLocation(punchlist.getLocation());
                                    punchlistDb.setComments(punchlist.getComments());
                                    punchlistDb.setPunchlistId(punchlist.getPunchListsId());
                                    punchlistDb.setPunchlistIdMobile(punchListMobileId);
                                    punchlistDb.setStatus(punchlist.getStatus());
                                    punchlistDb.setCreatedByUserId(String.valueOf(punchlist.getCreatedByUserid()));
                                    punchlistDb.setCreatedBy(punchlist.getCreatedBy());
                                    punchlistDb.setDateCreated(punchlist.getDateCreated() != null && !punchlist.getDateCreated().equals("") ? DateFormatter.getDateFromString(punchlist.getDateCreated()) : null);
                                    punchlistDb.setDateDue(punchlist.getDateDue() != null && !punchlist.getDateDue().equals("") ? DateFormatter.getDateFromString(punchlist.getDateDue(), DateFormatter.DATE_FORMAT_MMDDYYYY) : null);
                                    punchlistDb.setUserId(userId);
                                    punchlistDb.setCreatedAt(punchlist.getCreatedAt() != null && !punchlist.getCreatedAt().equals("") ? DateFormatter.getDateFromString(punchlist.getCreatedAt(), DateFormatter.DATE_FORMAT_MMDDYYYY) : null);
                                    punchlistDb.setIsAttachmentSync(true);
                                    punchlistDb.setSendEmail(0);
                                    Log.i(TAG, punchlistDb.getDateDue() + " indb " + punchlist.getDateDue() + "call: " + punchlist.getDescription());
                                    /*JSONObject obj = new JSONObject();
                                    punchlistDb.setLinkedDrawings(obj.toString());
*/
                                    mPunchlistDbDao.save(punchlistDb);

                                    updatePunchListHistory(punchlistDb);
                                }
                                DeleteQuery<PunchlistDrawing> PunchListpunchlistDrawingDeleteQuery = getDaoSession().queryBuilder(PunchlistDrawing.class)
                                        .where(PunchlistDrawingDao.Properties.PunchlistId.eq(punchlist.getPunchListsId()))
                                        .buildDelete();
                                PunchListpunchlistDrawingDeleteQuery.executeDeleteWithoutDetachingEntities();

                                for (LinkedDrawings linkDrawing :
                                        punchlist.getLinkedDrawings()) {
                                    PunchlistDrawing punchlistDrawing = new PunchlistDrawing();
                                    punchlistDrawing.setDrawingName(linkDrawing.getDrawingName());
                                    punchlistDrawing.setDrawingsId(linkDrawing.getDrwDrawingsId());
                                    punchlistDrawing.setOriginalDrwId(linkDrawing.getOriginalDrwId());
                                    punchlistDrawing.setDrwDisciplineId(linkDrawing.getDrwDisciplineId());
                                    punchlistDrawing.setDrwDisciplinesId(linkDrawing.getDrwDisciplinesId());
                                    punchlistDrawing.setDrwFoldersId(linkDrawing.getDrwFoldersId());
                                    punchlistDrawing.setRevisitedNum(linkDrawing.getRevisitedNum());
                                    punchlistDrawing.setPunchlistId(punchlist.getPunchListsId());
                                    punchlistDrawing.setPunchlistIdMobile(punchListMobileId);
                                    punchlistDrawingDao.insert(punchlistDrawing);

                                }

                                for (Attachment attachment : punchlist.getAttachments()) {
                                    List<PunchListAttachments> impactAttachments = getDaoSession().getPunchListAttachmentsDao().queryBuilder().where(
                                            PunchListAttachmentsDao.Properties.AttachmentId.eq(attachment.getAttachmentsId()),
                                            PunchListAttachmentsDao.Properties.PunchListId.eq(punchlist.getPunchListsId()),
                                            PunchListAttachmentsDao.Properties.UsersId.eq(userId)
                                    ).limit(1).list();
                                    List<PunchListAttachments> impactAttachmentMobile = getDaoSession().getPunchListAttachmentsDao().queryBuilder().where(
                                            PunchListAttachmentsDao.Properties.AttachmentIdMobile.eq(attachment.getAttachmentsIdMobile()),
                                            PunchListAttachmentsDao.Properties.AttachmentId.eq(0),
                                            PunchListAttachmentsDao.Properties.PunchListIdMobile.eq(punchListMobileId),
                                            PunchListAttachmentsDao.Properties.UsersId.eq(userId)
                                    ).limit(1).list();


                                    if (attachment.getDeletedAt() != null && !TextUtils.isEmpty(attachment.getDeletedAt())) {
                                        DeleteQuery<PunchListAttachments> PunchListAttachmentsDeleteQuery = getDaoSession().queryBuilder(PunchListAttachments.class)
                                                .where(PunchListAttachmentsDao.Properties.PunchListId.eq(punchlist.getPunchListsId()),
                                                        PunchListAttachmentsDao.Properties.AttachmentId.eq(attachment.getAttachmentsId()),
                                                        PunchListAttachmentsDao.Properties.UsersId.eq(userId))
                                                .buildDelete();
                                        PunchListAttachmentsDeleteQuery.executeDeleteWithoutDetachingEntities();
                                    } else if (impactAttachments.size() > 0 || impactAttachmentMobile.size() > 0) {
                                        PunchListAttachments punchListAttachments;
                                        if (impactAttachments.size() > 0) {
                                            punchListAttachments = impactAttachments.get(0);
                                        } else {
                                            punchListAttachments = impactAttachmentMobile.get(0);
                                        }

                                        punchListAttachments.setAttachmentId(attachment.getAttachmentsId());
                                        punchListAttachments.setAttachmentPath(attachment.getAttachPath());
                                        punchListAttachments.setIsAwsSync(true);
                                        punchListAttachments.setDeletedAt(attachment.getDeletedAt() != null && !attachment.getDeletedAt().equals("") ? DateFormatter.getDateFromDateTimeString(attachment.getDeletedAt()) : null);
                                        punchListAttachments.setPunchListId(punchlist.getPunchListsId());
                                        punchListAttachments.setPunchListIdMobile(punchListMobileId);
                                        punchListAttachments.setUsersId(userId);
                                        punchListAttachments.setType(attachment.getType());
                                        mPunchListAttachmentsDao.update(punchListAttachments);

                                    } else {
                                        PunchListAttachments punchListAttachments = new PunchListAttachments();
                                        punchListAttachments.setAttachmentId(attachment.getAttachmentsId());
                                        punchListAttachments.setAttachmentPath(attachment.getAttachPath());
                                        punchListAttachments.setDeletedAt(attachment.getDeletedAt() != null && !attachment.getDeletedAt().equals("") ? DateFormatter.getDateFromDateTimeString(attachment.getDeletedAt()) : null);
                                        punchListAttachments.setPunchListId(punchlist.getPunchListsId());
                                        punchListAttachments.setIsAwsSync(true);
                                        punchListAttachments.setPunchListIdMobile(punchListMobileId);
                                        punchListAttachments.setUsersId(userId);
//                                        punchListAttachments.setType(attachment.getType());
                                        if (attachment.getType() != null)
                                            punchListAttachments.setType(attachment.getType());
                                        else punchListAttachments.setType("JPEG");

                                        mPunchListAttachmentsDao.save(punchListAttachments);
                                    }
                                }
                            }
                        }
                        return punchlists;
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
            return getPunchList(projectId);
        } else {
            return new ArrayList<>();
        }
    }

    public void updatePunchListHistory(PunchlistDb punchlistDb) {
        Log.d(TAG, "updatePunchListHistory: punc list id " + punchlistDb.getPunchlistId());
        Log.d(TAG, "updatePunchListHistory: punc list mobile id " + punchlistDb.getPunchlistIdMobile());
        List<PunchListHistoryDb> punchListHistoryDbs = getDaoSession().getPunchListHistoryDbDao().queryBuilder().where(
                PunchListHistoryDbDao.Properties.IsSync.eq(false),
                PunchListHistoryDbDao.Properties.PunchListAuditsId.eq(0),
                PunchListHistoryDbDao.Properties.UserId.eq(punchlistDb.getUserId()),
                PunchListHistoryDbDao.Properties.PunchListId.eq(0),
                PunchListHistoryDbDao.Properties.PunchListMobileId.eq(punchlistDb.getPunchlistIdMobile())
        ).list();
        Log.d(TAG, "updatePunchListHistory: " + punchListHistoryDbs.size());

        for (PunchListHistoryDb punchDb : punchListHistoryDbs) {
            Log.d(TAG, "updatePunchListHistory: punch list id " + punchDb.getPunchListId());
            Log.d(TAG, "updatePunchListHistory: punch list mobile id " + punchDb.getPunchListMobileId());
            PunchListHistoryDb punchListHistoryDb = punchDb;
            punchListHistoryDb.setPunchListId(punchlistDb.getPunchlistId());
            getDaoSession().getPunchListHistoryDbDao().update(punchListHistoryDb);
        }
    }

    public PunchlistDb getPunchListForMobileId(PunchlistDb punchList) {
        Log.d(TAG, "getPunchListForMobileId: punc list mobile id " + punchList.getPunchlistIdMobile());
        List<PunchlistDb> punchlistDbs = getDaoSession().getPunchlistDbDao().queryBuilder().where(
                PunchlistDbDao.Properties.UserId.eq(punchList.getUserId()),
                PunchlistDbDao.Properties.PunchlistIdMobile.eq(punchList.getPunchlistIdMobile())
        ).list();
        Log.d(TAG, "getPunchListForMobileId: " + punchlistDbs.size());
        if (punchlistDbs.size() > 0) {
            Log.d(TAG, "getPunchListForMobileId: after query " + punchlistDbs.get(0).getPunchlistIdMobile());
            return punchlistDbs.get(0);
        } else return null;
    }

    public List<PunchListHistoryDb> doUpdatePunchListHistoryInDb(List<PunchListHistory> punchListHistories, Long projectId) {
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        if (loginResponse != null) {
            int userId = loginResponse.getUserDetails().getUsers_id();
            try {
                getDaoSession().callInTx(new Callable<List<PunchListHistory>>() {

                    PunchListHistoryDbDao punchListHistoryDao = getDaoSession().getPunchListHistoryDbDao();
                    PunchListRejectReasonAttachmentsDao punchListRejectReasonAttachmentsDao = getDaoSession().getPunchListRejectReasonAttachmentsDao();

                    @Override
                    public List<PunchListHistory> call() throws Exception {

                        if (punchListHistories != null) {

                            Log.d("MAna***", " call() : " + punchListHistories.size());
//                            for (PunchListHistory punchListHistory : punchListHistories) {
                            for (int i = 0; i < punchListHistories.size(); i++) {
                                PunchListHistory punchListHistory = punchListHistories.get(i);
                                Log.d("MAna****", "call History top : " + punchListHistory.getPunchListAuditsMobileId() + " PUnhc id " + punchListHistory.getPunchListId());
                                List<PunchListHistoryDb> punchListHistoryDbs = getDaoSession().getPunchListHistoryDbDao().queryBuilder().where(
                                        PunchListHistoryDbDao.Properties.PjProjectsId.eq(projectId),
//                                        PunchListHistoryDbDao.Properties.PunchListAuditsId.eq(0),
                                        PunchListHistoryDbDao.Properties.PunchListId.eq(punchListHistory.getPunchListId()),
                                        PunchListHistoryDbDao.Properties.PunchListAuditsMobileId.eq(punchListHistory.getPunchListAuditsMobileId()),
                                        PunchListHistoryDbDao.Properties.Status.eq(punchListHistory.getStatus()),
                                        PunchListHistoryDbDao.Properties.UserId.eq(userId)
                                ).limit(1).list();

                                if (punchListHistoryDbs.size() == 0) {
                                    Long punchlistIdMobile = getDaoSession().getPunchlistDbDao().queryBuilder().where(
                                            PunchlistDbDao.Properties.PjProjectsId.eq(projectId),
                                            PunchlistDbDao.Properties.UserId.eq(userId),
                                            PunchlistDbDao.Properties.PunchlistId.eq(punchListHistory.getPunchListId())
                                    ).limit(1).list().get(0).getPunchlistIdMobile();
                                    Log.d("king", "call:if condition  " + punchListHistory.getPunchListAuditsId());
//                                    if(! punchListHistoryDbs.get(0).getPunchListAuditsId().equals(punchListHistory.getPunchListAuditsId())){
                                    PunchListHistoryDb punchListHistoryDb = new PunchListHistoryDb();
                                    punchListHistoryDb.setUserId(userId);
                                    punchListHistoryDb.setPjProjectsId(projectId.intValue());
                                    punchListHistoryDb.setPunchListId(punchListHistory.getPunchListId());
                                    punchListHistoryDb.setPunchListMobileId(punchlistIdMobile.intValue());
                                    punchListHistoryDb.setPunchListAuditsId(punchListHistory.getPunchListAuditsId());
                                    if (punchListHistory.getPunchListAuditsMobileId() == 0) {
                                        punchListHistoryDb.setPunchListAuditsMobileId(generateUniqueMobilePunchListHistoryId().intValue());
                                    } else {
                                        punchListHistoryDb.setPunchListAuditsMobileId(punchListHistory.getPunchListAuditsMobileId());
                                    }

                                    punchListHistoryDb.setComments(punchListHistory.getComments());
                                    punchListHistoryDb.setCreatedAt(punchListHistory.getCreatedAt() != null && !punchListHistory.getCreatedAt().equals("") ? DateFormatter.getDateFromDateHHTimeString(punchListHistory.getCreatedAt()) : null);
                                    Long createTimestamp = 0L;
                                    if (punchListHistory.getCreatedTimestamp() == null) {
                                        createTimestamp = punchListHistoryDb.getCreatedAt().getTime();
                                    } else {
                                        createTimestamp = punchListHistory.getCreatedTimestamp();
                                    }
                                    punchListHistoryDb.setCreatedTimestamp(createTimestamp);
                                    punchListHistoryDb.setUpdatedAt(
                                            punchListHistory.getUpdatedAt() != null && !punchListHistory.getCreatedAt().equals("") ?
                                                    DateFormatter.getDateFromDateHHTimeString(punchListHistory.getUpdatedAt()) : null
                                    );
                                    punchListHistoryDb.setDeletedAt(
                                            punchListHistory.getDeletedAt() != null && !punchListHistory.getDeletedAt().equals("") ?
                                                    DateFormatter.getDateFromDateHHTimeString(punchListHistory.getDeletedAt()) : null
                                    );
                                    punchListHistoryDb.setStatus(punchListHistory.getStatus());
                                    punchListHistoryDb.setIsSync(true);
                                    punchListHistoryDb.setAttachmentSync(true);
                                    punchListHistoryDb.setCreatedBy(punchListHistory.getCreatedBy());
                                    punchListHistoryDb.setCreatedByName(punchListHistory.getCreatedByName());

                                    for (Attachment attachment : punchListHistory.getAttachments()) {
                                        PunchListRejectReasonAttachments rejectReasonAttachment = new PunchListRejectReasonAttachments();
                                        rejectReasonAttachment.setAttachmentPath(attachment.getAttachPath());
                                        rejectReasonAttachment.setPjProjectsId(projectId.intValue());
                                        rejectReasonAttachment.setPunchListAuditsId(punchListHistory.getPunchListAuditsId());
                                        rejectReasonAttachment.setRejectAttachmentId(attachment.getAttachmentsId());
                                        rejectReasonAttachment.setPunchListId(punchListHistory.getPunchListId());
                                        rejectReasonAttachment.setRejectAttachmentIdMobile(Long.valueOf(attachment.getAttachmentsIdMobile()));
                                        rejectReasonAttachment.setFileStatus(punchListHistory.getStatus());
                                        rejectReasonAttachment.setAwsSync(true);
                                        rejectReasonAttachment.setPunchListAuditsIdMobile(Long.valueOf(punchListHistory.getPunchListAuditsMobileId()));
                                        rejectReasonAttachment.setDeletedAt(
                                                attachment.getDeletedAt() != null && !attachment.getDeletedAt().equals("") ?
                                                        DateFormatter.getDateFromDateHHTimeString(attachment.getDeletedAt()) : null
                                        );
                                        rejectReasonAttachment.setOriginalName(attachment.getOriginalName());
                                        rejectReasonAttachment.setIsAwsSync(true);
                                        rejectReasonAttachment.setType(attachment.getType());
                                        rejectReasonAttachment.setUsersId(userId);

                                        punchListRejectReasonAttachmentsDao.insertOrReplace(rejectReasonAttachment);
                                    }
                                /*if (punchListHistoryDbs.size() > 0) {
                                    punchListHistoryDao.insertOrReplace(punchListHistoryDb);
                                } else {
                                    punchListHistoryDao.save(punchListHistoryDb);
                                }*/
                                    punchListHistoryDao.save(punchListHistoryDb);
//                                    }
                                } else {

                                    Log.d("king", "call:else condition  " + punchListHistory.getPunchListAuditsId());
                                    PunchListHistoryDb punchListHistoryDb = punchListHistoryDbs.get(0);
                                    punchListHistoryDb.setPunchListAuditsId(punchListHistory.getPunchListAuditsId());
                                    punchListHistoryDb.setCreatedAt(DateFormatter.getDateFromDateHHTimeString(punchListHistory.getCreatedAt()));
                                    punchListHistoryDb.setCreatedByName(punchListHistory.getCreatedByName());
                                    punchListHistoryDb.setUpdatedAt(DateFormatter.getDateFromDateHHTimeString(punchListHistory.getUpdatedAt()));

                                    for (Attachment attachment : punchListHistory.getAttachments()) {
                                        PunchListRejectReasonAttachments rejectReasonAttachment = new PunchListRejectReasonAttachments();
                                        rejectReasonAttachment.setAttachmentPath(attachment.getAttachPath());
                                        rejectReasonAttachment.setPunchListAuditsId(punchListHistory.getPunchListAuditsId());
                                        rejectReasonAttachment.setRejectAttachmentId(attachment.getAttachmentsId());
                                        rejectReasonAttachment.setPunchListId(punchListHistory.getPunchListId());
                                        rejectReasonAttachment.setRejectAttachmentIdMobile(Long.valueOf(attachment.getAttachmentsIdMobile()));
                                        rejectReasonAttachment.setFileStatus(punchListHistory.getStatus());
                                        rejectReasonAttachment.setAwsSync(true);
//                                        rejectReasonAttachment.setPunchListAuditsIdMobile(Long.valueOf(punchListHistory.getPunchListAuditsMobileId()));
                                        rejectReasonAttachment.setDeletedAt(
                                                attachment.getDeletedAt() != null && !attachment.getDeletedAt().equals("") ?
                                                        DateFormatter.getDateFromDateHHTimeString(attachment.getDeletedAt()) : null
                                        );
                                        rejectReasonAttachment.setOriginalName(attachment.getOriginalName());
                                        rejectReasonAttachment.setIsAwsSync(true);
                                        rejectReasonAttachment.setType(attachment.getType());
                                        rejectReasonAttachment.setUsersId(userId);

                                        punchListRejectReasonAttachmentsDao.update(rejectReasonAttachment);
                                    }
                                    punchListHistoryDb.setIsSync(true);
                                    punchListHistoryDb.setIsAttachmentSync(true);

                                    punchListHistoryDao.update(punchListHistoryDb);
//                                    punchListHistoryDao.insertOrReplaceInTx(punchListHistoryDb);
                                }

                                Log.d("MAna****", "call History: " + punchListHistory.getPunchListAuditsMobileId() + " PUnhc id " + punchListHistory.getPunchListId());
                                List<TransactionLogMobile> transactionLogMobileList = getDaoSession().
                                        getTransactionLogMobileDao().queryBuilder().where(
                                                TransactionLogMobileDao.Properties.UsersId.eq(userId),
                                                TransactionLogMobileDao.Properties.MobileId.eq(punchListHistory.getPunchListAuditsMobileId())
                                        ).limit(1).list();
                                if (transactionLogMobileList.size() > 0) {
                                    Log.d("MAna****", "call: " + transactionLogMobileList.get(0).getMobileId());
                                    transactionLogMobileList.get(0).setStatus(SyncDataEnum.SYNC.ordinal());
                                    deleteSyncData(transactionLogMobileList.get(0));
                                }
                            }
                        }
                        return null;
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public List<PunchlistDb> getPunchList(int projectId) {
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        if (loginResponse != null) {
            List<PunchlistDb> punchlistDbList = getDaoSession().getPunchlistDbDao().queryBuilder().where(
                    PunchlistDbDao.Properties.PjProjectsId.eq(projectId),
                    PunchlistDbDao.Properties.UserId.eq(loginResponse.getUserDetails().getUsers_id()),
                    PunchlistDbDao.Properties.DeletedAt.isNull()
            ).list();
            return punchlistDbList;
        } else {
            return new ArrayList<>();
        }
    }

    public List<PunchlistDb> getSearchPunchList(int projectId, String searchKey) {
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        if (loginResponse != null) {
            List<PunchlistDb> punchlistDbList = getDaoSession().getPunchlistDbDao().queryBuilder().where(
                            PunchlistDbDao.Properties.PjProjectsId.eq(projectId),
                            PunchlistDbDao.Properties.UserId.eq(loginResponse.getUserDetails().getUsers_id()),
                            PunchlistDbDao.Properties.DeletedAt.isNull())
                    .whereOr(PunchlistDbDao.Properties.AssigneeName.like("%" + searchKey + "%"),
                            PunchlistDbDao.Properties.Descriptions.like("%" + searchKey + "%")).list();
            return punchlistDbList;
        } else {
            return new ArrayList<>();
        }
    }

    /**
     * Get punchlist detail
     *
     * @param punchListMobileId
     * @return
     */
    public PunchlistDb getPunchListDetail(long punchListMobileId) {
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        if (loginResponse != null) {
            List<PunchlistDb> punchlistDbs = getDaoSession().getPunchlistDbDao().queryBuilder().where(PunchlistDbDao.Properties.PunchlistIdMobile.eq(punchListMobileId),
                    PunchlistDbDao.Properties.UserId.eq(loginResponse.getUserDetails().getUsers_id())).limit(1).list();
            if (punchlistDbs.size() > 0) {
                return punchlistDbs.get(0);
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * Get punchlist detail
     *
     * @param punchId
     * @return
     */
    public PunchlistDb getPunchListDetail(int punchId, boolean isPunchId) {
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        if (loginResponse != null) {
            List<PunchlistDb> punchlistDbs = getDaoSession().getPunchlistDbDao().queryBuilder().where(PunchlistDbDao.Properties.PunchlistId.eq(punchId),
                    PunchlistDbDao.Properties.UserId.eq(loginResponse.getUserDetails().getUsers_id())).limit(1).list();
            if (punchlistDbs.size() > 0) {
                return punchlistDbs.get(0);
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * Get punchlist detail
     *
     * @param punchListId
     * @return
     */
    public PunchlistDb getPunchListPunchId(int punchListId) {
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        if (loginResponse != null) {
            List<PunchlistDb> punchlistDbs = getDaoSession().getPunchlistDbDao().queryBuilder().where(PunchlistDbDao.Properties.PunchlistId.eq(punchListId),
                    PunchlistDbDao.Properties.UserId.eq(loginResponse.getUserDetails().getUsers_id())).limit(1).list();
            if (punchlistDbs.size() > 0) {
                return punchlistDbs.get(0);
            } else {
                return null;
            }
        } else {
            return null;

        }
    }

    /**
     * Add new punch list in db
     *
     * @param projectId
     * @param punchListStatus
     * @param punchlistAssignee
     * @param attachmentList
     * @param dueDate
     * @param location
     * @param description
     * @param drawingView
     * @return
     */
    // Change the assignee in the of list Aug 23, 2022 add param List<PunchlistAssigneeList> punchlistAssigneeLists
    public PunchlistDb addPunchListDb(int projectId, PunchListStatus punchListStatus, PunchlistAssignee punchlistAssignee,
                                      List<PunchlistAssigneeList> punchlistAssigneeLists, List<PunchlistAssigneeList> punchlistAssigneeCcLists,
                                      List<PunchListAttachments> attachmentList,
                                      Date dueDate, String location, String description, boolean drawingView, int sendEmail,
                                      String comments) {
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        int userId = loginResponse.getUserDetails().getUsers_id();
        long punchlistIdMobile = generateUniqueMobilePunchListId();
        if (loginResponse != null) {
            PunchlistDb punchlistDb = new PunchlistDb();
            punchlistDb.setPjProjectsId(projectId);
            punchlistDb.setIsSync(false);
            if (attachmentList.size() > 0) {
                punchlistDb.setIsAttachmentSync(false);
            } else {
                punchlistDb.setIsAttachmentSync(true);

            }

            List<String> assignees = new ArrayList<>();
            for (PunchlistAssigneeList assigneeList : punchlistAssigneeLists) {
                assignees.add(assigneeList.getUsersId().toString());
            }

            punchlistDb.setAssignedTo(assignees);

            List<String> assigneesCc = new ArrayList<>();
            if (punchlistAssigneeCcLists != null) {
                for (PunchlistAssigneeList assigneeCcList : punchlistAssigneeCcLists) {
                    assigneesCc.add(assigneeCcList.getUsersId().toString());
                }
            }
            punchlistDb.setAssignedCcList(assigneesCc);

            punchlistDb.setUserId(loginResponse.getUserDetails().getUsers_id());
            punchlistDb.setCreatedAt(new Date());
            punchlistDb.setCreatedBy(loginResponse.getUserDetails().getFirstname() + " " + loginResponse.getUserDetails().getLastname());
            punchlistDb.setCreatedByUserId(String.valueOf(loginResponse.getUserDetails().getUsers_id()));
            punchlistDb.setDateDue(dueDate);
            Log.i(TAG, punchlistDb.getDateDue() + " add mode indb ");

            punchlistDb.setDateCreated(new Date());
            punchlistDb.setStatus(punchListStatus.getValue());
//            punchlistDb.setAssignedTo(punchlistAssignee.getUsersId() + ""); TODO: Nitin
            List<String> assigneeNames = new ArrayList<>();
            for (PunchlistAssigneeList name : punchlistAssigneeLists) {
                assigneeNames.add(name.getName());
            }
            punchlistDb.setAssigneeName(assigneeNames);

            punchlistDb.setLocation(location);
            punchlistDb.setComments(comments);
            punchlistDb.setItemNumber(-1);
            punchlistDb.setPunchlistId(0);
            punchlistDb.setPunchlistIdMobile(punchlistIdMobile);
            punchlistDb.setDescriptions(description);
            punchlistDb.setSendEmail(sendEmail);

            // Save punch list
            getDaoSession().getPunchlistDbDao().save(punchlistDb);
          /*  List<PunchlistDb> punchlistDbList = getDaoSession().getPunchlistDbDao().queryBuilder().where(
                    PunchlistDbDao.Properties.PunchlistIdMobile.isNotNull())
                    .orderDesc(PunchlistDbDao.Properties.PunchlistIdMobile)
                    .limit(1).list();
            long punchlistIdMobile = punchlistDbList.get(0).getPunchlistIdMobile();

*/
            // save attachments one by one in db with respect to {@code punchlistIdMobile}
            for (PunchListAttachments attachment : attachmentList) {
                if (attachment != null) {
                    attachment.setUsersId(loginResponse.getUserDetails().getUsers_id());
                    attachment.setIsAwsSync(false);
                    attachment.setPunchListId(0);
                    attachment.setAttachmentId(0);
                    attachment.setPunchListIdMobile(punchlistIdMobile);
                    // Save punch list attachment
                    getDaoSession().getPunchListAttachmentsDao().save(attachment);
                    long attachmentIdMobile = getDaoSession().getPunchListAttachmentsDao().queryBuilder().where(
                                    PunchListAttachmentsDao.Properties.AttachmentIdMobile.isNotNull())
                            .orderDesc(PunchListAttachmentsDao.Properties.AttachmentIdMobile)
                            .limit(1).list().get(0).getAttachmentIdMobile();
                    TransactionLogMobileDao mPronovosSyncDataDao = getDaoSession().getTransactionLogMobileDao();

                    TransactionLogMobile transactionLogMobile = new TransactionLogMobile();

                    transactionLogMobile.setUsersId(userId);
                    transactionLogMobile.setModule(TransactionModuleEnum.PUNCHLIST_ATTACHMENT.ordinal());
                    transactionLogMobile.setStatus(SyncDataEnum.NOTSYNC.ordinal());
                    transactionLogMobile.setMobileId(attachmentIdMobile);
                    transactionLogMobile.setServerId(0L);
                    transactionLogMobile.setCreateDate(Calendar.getInstance().getTime());
                    mPronovosSyncDataDao.save(transactionLogMobile);
//                    Log.i(TAG, "addPunchListDb: " + attachmentIdMobile);
                }
            }

            TransactionLogMobileDao mPronovosSyncDataDao = getDaoSession().getTransactionLogMobileDao();

            TransactionLogMobile transactionLogMobile = new TransactionLogMobile();

            transactionLogMobile.setUsersId(userId);
            transactionLogMobile.setModule(TransactionModuleEnum.PUNCHLIST.ordinal());
            Log.i("Temp", "doUpdateDrawingAnnotationTable: PUNCHLIST  " + TransactionModuleEnum.PUNCHLIST.ordinal());
            transactionLogMobile.setStatus(SyncDataEnum.NOTSYNC.ordinal());
            transactionLogMobile.setMobileId(punchlistIdMobile);
            transactionLogMobile.setServerId(0L);
            transactionLogMobile.setCreateDate(Calendar.getInstance().getTime());
            mPronovosSyncDataDao.save(transactionLogMobile);

            // Add punch list history entry in db
            savePunchListHistoryInDb(punchlistDb, "", new ArrayList<>());
            Log.i(TAG, "addPunchListDb: " + drawingView);
            if (!drawingView) {
                Log.i(TAG, "addpunchlist: setupAndStartWorkManager");

                ((PronovosApplication) context.getApplicationContext()).setupAndStartWorkManager();
            } else {
                addDrawingPunchlist(0, punchlistIdMobile, 0l, projectId, userId);

            }

            return punchlistDb;
        } else {
            return null;
        }
    }

    public void addDrawingPunchlist(Integer punch_id, Long punch_id_mobile, Long drawingID, int projectId, int userId) {
        DrwPunchlist drwPunch = new DrwPunchlist();
        drwPunch.setDrawingId(drawingID);
        drwPunch.setPjProjectsId(projectId);
        drwPunch.setPunchlistId(punch_id);
        drwPunch.setUserId(userId);
        drwPunch.setPunchlistIdMobile(punch_id_mobile);
        getDaoSession().insertOrReplace(drwPunch);

    }

    /**
     * Update punch list in db
     *
     * @param projectId
     * @param punchListStatus
     * @param punchlistAssignee
     * @param attachmentList
     * @param dueDate
     * @param location
     * @param description
     * @param byUserId
     * @param createdByUserId
     * @return
     */
    public PunchlistDb updatePunchListDb(int projectId, PunchListStatus punchListStatus,
                                         PunchlistAssignee punchlistAssignee, List<PunchListAttachments> attachmentList,
                                         Date dueDate, String location, String description,
                                         int punchListId, long punchListMobileId,
                                         int itemNumber, boolean attachmentSync,
                                         Date createdAt, Date dateCreate, String byUserId, String createdByUserId, int sendEmail,
                                         List<PunchlistAssigneeList> punchlistAssigneeLists, List<PunchlistAssigneeList> punchlistAssigneeCcLists,
                                         String comments) {
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        if (loginResponse != null) {
            int userId = loginResponse.getUserDetails().getUsers_id();
            PunchlistDb punchlistDb = new PunchlistDb();
            punchlistDb.setPjProjectsId(projectId);
            punchlistDb.setIsSync(false);
            punchlistDb.setIsAttachmentSync(attachmentSync);
            punchlistDb.setUserId(loginResponse.getUserDetails().getUsers_id());
            punchlistDb.setCreatedBy(createdByUserId);
            punchlistDb.setCreatedByUserId(byUserId);
            punchlistDb.setDateDue(dueDate);
            Log.i(TAG, punchlistDb.getDateDue() + " update mode indb ");

            punchlistDb.setStatus(punchListStatus.getValue());
//            punchlistDb.setAssignedTo(punchlistAssignee.getUsersId() + "");

            List<String> assignees = new ArrayList<>();
            for (PunchlistAssigneeList assigneeList : punchlistAssigneeLists) {
                assignees.add(assigneeList.getUsersId().toString());
            }
            punchlistDb.setAssignedTo(assignees);

            List<String> assigneesCc = new ArrayList<>();
            if (punchlistAssigneeCcLists != null) {
                for (PunchlistAssigneeList assigneeCcList : punchlistAssigneeCcLists) {
                    assigneesCc.add(assigneeCcList.getUsersId().toString());
                }
            }

            punchlistDb.setAssignedCcList(assigneesCc);


            //            punchlistDb.setAssignedTo(punchlistAssignee.getUsersId() + ""); TODO: Nitin
            List<String> assigneeNames = new ArrayList<>();
            for (PunchlistAssigneeList name : punchlistAssigneeLists) {
                assigneeNames.add(name.getName());
            }

            punchlistDb.setAssigneeName(assigneeNames);

            punchlistDb.setLocation(location);
            punchlistDb.setComments(comments);
            punchlistDb.setItemNumber(itemNumber);
            punchlistDb.setPunchlistId(punchListId);
            punchlistDb.setPunchlistIdMobile(punchListMobileId);
            punchlistDb.setDescriptions(description);
            punchlistDb.setCreatedAt(createdAt);
            punchlistDb.setDateCreated(dateCreate);
            punchlistDb.setSendEmail(sendEmail);

            // Save punch list
            getDaoSession().getPunchlistDbDao().insertOrReplace(punchlistDb);


            // save attachments one by one in db with respect to {@code punchlistIdMobile}
            for (PunchListAttachments attachment : attachmentList) {
                if (attachment != null) {
                    attachment.setUsersId(loginResponse.getUserDetails().getUsers_id());
//            attachment.setIsAwsSync(false);
                    attachment.setPunchListId(punchListId);
                    attachment.setAttachmentId(attachment.getAttachmentId());
                    attachment.setPunchListIdMobile(punchListMobileId);
                    if (attachment.getAttachmentId() == null || attachment.getAttachmentId() == 0) {
                        attachment.setIsAwsSync(false);
                        attachment.setAttachmentId(0);
//                        long attachmentMobileId = attachment.getAttachmentIdMobile();
                        if (attachment.getDeletedAt() != null) {
                            attachment.setIsAwsSync(true);
                            getDaoSession().getPunchListAttachmentsDao().save(attachment);

                            TransactionLogMobile transactionLogMobile = getDaoSession().getTransactionLogMobileDao().queryBuilder().where(
                                    TransactionLogMobileDao.Properties.Module.eq(TransactionModuleEnum.PUNCHLIST_ATTACHMENT.ordinal()),
                                    TransactionLogMobileDao.Properties.MobileId.eq(attachment.getAttachmentIdMobile()),
                                    TransactionLogMobileDao.Properties.ServerId.eq(Long.valueOf(attachment.getAttachmentId())),
                                    TransactionLogMobileDao.Properties.UsersId.eq(userId)).list().get(0);
                            deleteSyncData(transactionLogMobile);

                            DeleteQuery<PunchListAttachments> pronovosSyncDataDeleteQuery = getDaoSession().queryBuilder(PunchListAttachments.class)
                                    .where(PunchListAttachmentsDao.Properties.AttachmentIdMobile.eq(attachment.getAttachmentIdMobile()))
                                    .buildDelete();
                            pronovosSyncDataDeleteQuery.executeDeleteWithoutDetachingEntities();

                        } else if (attachment.getAttachmentIdMobile() == null) {
                            getDaoSession().getPunchListAttachmentsDao().save(attachment);
                            long attachmentIdMobile = getDaoSession().getPunchListAttachmentsDao().queryBuilder().where(
                                            PunchListAttachmentsDao.Properties.AttachmentIdMobile.isNotNull())
                                    .orderDesc(PunchListAttachmentsDao.Properties.AttachmentIdMobile)
                                    .limit(1).list().get(0).getAttachmentIdMobile();
                            TransactionLogMobileDao mPronovosSyncDataDao = getDaoSession().getTransactionLogMobileDao();

                            TransactionLogMobile transactionLogMobile = new TransactionLogMobile();

                            transactionLogMobile.setUsersId(userId);
                            transactionLogMobile.setModule(TransactionModuleEnum.PUNCHLIST_ATTACHMENT.ordinal());
                            transactionLogMobile.setStatus(SyncDataEnum.NOTSYNC.ordinal());
                            transactionLogMobile.setMobileId(attachmentIdMobile);
                            transactionLogMobile.setServerId(0L);
                            transactionLogMobile.setCreateDate(Calendar.getInstance().getTime());
                            mPronovosSyncDataDao.save(transactionLogMobile);

                        } else {
                            getDaoSession().getPunchListAttachmentsDao().save(attachment);
                        }
                    } else {
                        PunchListAttachments attachmnt = getAttachment(punchListMobileId, attachment.getAttachmentId());
                        if (attachmnt != null) {
                            attachment.setDeletedAt(attachmnt.getDeletedAt());
                            if (attachmnt.getDeletedAt() == null) {
                                attachment.setIsAwsSync(attachmnt.getIsAwsSync());
                                if (!attachmnt.getIsAwsSync()) {
                                    TransactionLogMobileDao mPronovosSyncDataDao = getDaoSession().getTransactionLogMobileDao();

                                    TransactionLogMobile transactionLogMobile = new TransactionLogMobile();

                                    transactionLogMobile.setUsersId(userId);
                                    transactionLogMobile.setModule(TransactionModuleEnum.PUNCHLIST_ATTACHMENT.ordinal());
                                    transactionLogMobile.setStatus(SyncDataEnum.NOTSYNC.ordinal());
                                    transactionLogMobile.setMobileId(attachment.getAttachmentIdMobile());
                                    transactionLogMobile.setServerId(0L);
                                    transactionLogMobile.setCreateDate(Calendar.getInstance().getTime());
                                    mPronovosSyncDataDao.save(transactionLogMobile);
                                }
                            } else {
                                attachment.setIsAwsSync(true);
                            }
                            // Save punch list attachment
                            getDaoSession().getPunchListAttachmentsDao().update(attachment);
                        }
                    }
                }
            }
            punchListTransaction(punchListId, userId, punchListMobileId);

            if (punchlistDb.getStatus() == PunchListStatus.Complete.getValue()
                    || punchlistDb.getStatus() == PunchListStatus.Approved.getValue()
                    || punchlistDb.getStatus() == PunchListStatus.Rejected.getValue()) {

                savePunchListHistoryInDb(punchlistDb, "", new ArrayList<>());
            }

            return punchlistDb;
        } else {
            return null;
        }
    }

    public PunchlistDb updatePunchListInDBForSaveAndSend(int projectId, PunchListStatus punchListStatus,
                                                         PunchlistAssignee punchlistAssignee, List<PunchListAttachments> attachmentList,
                                                         Date dueDate, String location, String description,
                                                         int punchListId, long punchListMobileId,
                                                         int itemNumber, boolean attachmentSync,
                                                         Date createdAt, Date dateCreate, String byUserId, String createdByUserId, int sendEmail,
                                                         List<PunchlistAssigneeList> punchlistAssigneeLists, List<PunchlistAssigneeList> punchlistAssigneeCcLists,
                                                         String comments) {
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        if (loginResponse != null) {
            int userId = loginResponse.getUserDetails().getUsers_id();
            PunchlistDb punchlistDb = new PunchlistDb();
            punchlistDb.setPjProjectsId(projectId);
            punchlistDb.setIsSync(false);
            punchlistDb.setIsAttachmentSync(attachmentSync);
            punchlistDb.setUserId(loginResponse.getUserDetails().getUsers_id());
            punchlistDb.setCreatedBy(createdByUserId);
            punchlistDb.setCreatedByUserId(byUserId);
            punchlistDb.setDateDue(dueDate);
            Log.i(TAG, punchlistDb.getDateDue() + " update mode indb ");

            punchlistDb.setStatus(punchListStatus.getValue());
//            punchlistDb.setAssignedTo(punchlistAssignee.getUsersId() + "");

            List<String> assignees = new ArrayList<>();
            for (PunchlistAssigneeList assigneeList : punchlistAssigneeLists) {
                assignees.add(assigneeList.getUsersId().toString());
            }
            punchlistDb.setAssignedTo(assignees);

            List<String> assigneesCc = new ArrayList<>();
            if (punchlistAssigneeCcLists != null) {
                for (PunchlistAssigneeList assigneeCcList : punchlistAssigneeCcLists) {
                    assigneesCc.add(assigneeCcList.getUsersId().toString());
                }
            }

            punchlistDb.setAssignedCcList(assigneesCc);


            //            punchlistDb.setAssignedTo(punchlistAssignee.getUsersId() + ""); TODO: Nitin
            List<String> assigneeNames = new ArrayList<>();
            for (PunchlistAssigneeList name : punchlistAssigneeLists) {
                assigneeNames.add(name.getName());
            }

            punchlistDb.setAssigneeName(assigneeNames);

            punchlistDb.setLocation(location);
            punchlistDb.setComments(comments);
            punchlistDb.setItemNumber(itemNumber);
            punchlistDb.setPunchlistId(punchListId);
            punchlistDb.setPunchlistIdMobile(punchListMobileId);
            punchlistDb.setDescriptions(description);
            punchlistDb.setCreatedAt(createdAt);
            punchlistDb.setDateCreated(dateCreate);
            punchlistDb.setSendEmail(sendEmail);

            // Save punch list
            getDaoSession().getPunchlistDbDao().insertOrReplace(punchlistDb);


            // save attachments one by one in db with respect to {@code punchlistIdMobile}
            for (PunchListAttachments attachment : attachmentList) {
                if (attachment != null) {
                    attachment.setUsersId(loginResponse.getUserDetails().getUsers_id());
//            attachment.setIsAwsSync(false);
                    attachment.setPunchListId(punchListId);
                    attachment.setAttachmentId(attachment.getAttachmentId());
                    attachment.setPunchListIdMobile(punchListMobileId);
                    if (attachment.getAttachmentId() == null || attachment.getAttachmentId() == 0) {
                        attachment.setIsAwsSync(false);
                        attachment.setAttachmentId(0);
//                        long attachmentMobileId = attachment.getAttachmentIdMobile();
                        if (attachment.getDeletedAt() != null) {
                            attachment.setIsAwsSync(true);
                            getDaoSession().getPunchListAttachmentsDao().save(attachment);

                            TransactionLogMobile transactionLogMobile = getDaoSession().getTransactionLogMobileDao().queryBuilder().where(
                                    TransactionLogMobileDao.Properties.Module.eq(TransactionModuleEnum.PUNCHLIST_ATTACHMENT.ordinal()),
                                    TransactionLogMobileDao.Properties.MobileId.eq(attachment.getAttachmentIdMobile()),
                                    TransactionLogMobileDao.Properties.ServerId.eq(Long.valueOf(attachment.getAttachmentId())),
                                    TransactionLogMobileDao.Properties.UsersId.eq(userId)).list().get(0);
                            deleteSyncData(transactionLogMobile);

                            DeleteQuery<PunchListAttachments> pronovosSyncDataDeleteQuery = getDaoSession().queryBuilder(PunchListAttachments.class)
                                    .where(PunchListAttachmentsDao.Properties.AttachmentIdMobile.eq(attachment.getAttachmentIdMobile()))
                                    .buildDelete();
                            pronovosSyncDataDeleteQuery.executeDeleteWithoutDetachingEntities();

                        } else if (attachment.getAttachmentIdMobile() == null) {
                            getDaoSession().getPunchListAttachmentsDao().save(attachment);
                            long attachmentIdMobile = getDaoSession().getPunchListAttachmentsDao().queryBuilder().where(
                                            PunchListAttachmentsDao.Properties.AttachmentIdMobile.isNotNull())
                                    .orderDesc(PunchListAttachmentsDao.Properties.AttachmentIdMobile)
                                    .limit(1).list().get(0).getAttachmentIdMobile();
                            TransactionLogMobileDao mPronovosSyncDataDao = getDaoSession().getTransactionLogMobileDao();

                            TransactionLogMobile transactionLogMobile = new TransactionLogMobile();

                            transactionLogMobile.setUsersId(userId);
                            transactionLogMobile.setModule(TransactionModuleEnum.PUNCHLIST_ATTACHMENT.ordinal());
                            transactionLogMobile.setStatus(SyncDataEnum.NOTSYNC.ordinal());
                            transactionLogMobile.setMobileId(attachmentIdMobile);
                            transactionLogMobile.setServerId(0L);
                            transactionLogMobile.setCreateDate(Calendar.getInstance().getTime());
                            mPronovosSyncDataDao.save(transactionLogMobile);

                        } else {
                            getDaoSession().getPunchListAttachmentsDao().save(attachment);
                        }
                    } else {
                        PunchListAttachments attachmnt = getAttachment(punchListMobileId, attachment.getAttachmentId());
                        if (attachmnt != null) {
                            attachment.setDeletedAt(attachmnt.getDeletedAt());
                            if (attachmnt.getDeletedAt() == null) {
                                attachment.setIsAwsSync(attachmnt.getIsAwsSync());
                                if (!attachmnt.getIsAwsSync()) {
                                    TransactionLogMobileDao mPronovosSyncDataDao = getDaoSession().getTransactionLogMobileDao();

                                    TransactionLogMobile transactionLogMobile = new TransactionLogMobile();

                                    transactionLogMobile.setUsersId(userId);
                                    transactionLogMobile.setModule(TransactionModuleEnum.PUNCHLIST_ATTACHMENT.ordinal());
                                    transactionLogMobile.setStatus(SyncDataEnum.NOTSYNC.ordinal());
                                    transactionLogMobile.setMobileId(attachment.getAttachmentIdMobile());
                                    transactionLogMobile.setServerId(0L);
                                    transactionLogMobile.setCreateDate(Calendar.getInstance().getTime());
                                    mPronovosSyncDataDao.save(transactionLogMobile);
                                }
                            } else {
                                attachment.setIsAwsSync(true);
                            }
                            // Save punch list attachment
                            getDaoSession().getPunchListAttachmentsDao().update(attachment);
                        }
                    }
                }
            }
            punchListTransaction(punchListId, userId, punchListMobileId);

            /*if(punchlistDb.getStatus() == PunchListStatus.Complete.getValue()
                    || punchlistDb.getStatus() == PunchListStatus.Approved.getValue()
                    || punchlistDb.getStatus() == PunchListStatus.Rejected.getValue()) {

                savePunchListHistoryInDb(punchlistDb,"",new ArrayList<>());
            }*/

            return punchlistDb;
        } else {
            return null;
        }
    }


    private void punchListTransaction(int punchListId, int userId, long punchListMobileId) {
        Log.d("Manya", "punchListTransaction: " + punchListId + "userid: " + userId + "PunchListMobile: " + punchListMobileId);


//        if (!NetworkService.isNetworkAvailable(context) && punchListId != 0 ) {
        Log.d("Manya", "punchListTransaction ###: " + punchListId);
        List<TransactionLogMobile> punchlistLogMobiles = getDaoSession().getTransactionLogMobileDao().queryBuilder()
                .where(TransactionLogMobileDao.Properties.MobileId.eq(punchListMobileId),
                        TransactionLogMobileDao.Properties.ServerId.eq(Long.valueOf(punchListId)),
                        TransactionLogMobileDao.Properties.Module.eq(TransactionModuleEnum.PUNCHLIST.ordinal()),
                        TransactionLogMobileDao.Properties.Status.eq(SyncDataEnum.NOTSYNC),
                        TransactionLogMobileDao.Properties.UsersId.eq(userId)).list();
        Log.d("Manya", "punchListTransaction: " + punchlistLogMobiles.size());

        if (punchlistLogMobiles.size() <= 0) {
            Log.d("king 13", "punchListTransaction: " + punchListId);
            TransactionLogMobileDao mPronovosSyncDataDao = getDaoSession().getTransactionLogMobileDao();

            TransactionLogMobile transactionLogMobile = new TransactionLogMobile();

            transactionLogMobile.setUsersId(userId);
            transactionLogMobile.setModule(TransactionModuleEnum.PUNCHLIST.ordinal());
            transactionLogMobile.setStatus(SyncDataEnum.NOTSYNC.ordinal());
            transactionLogMobile.setMobileId(punchListMobileId);
            transactionLogMobile.setServerId(Long.valueOf(punchListId));
            transactionLogMobile.setCreateDate(Calendar.getInstance().getTime());
            mPronovosSyncDataDao.save(transactionLogMobile);
            Log.i(TAG, "updatePunchlist attachment: setupAndStartWorkManager");
            ((PronovosApplication) context.getApplicationContext()).setupAndStartWorkManager();
        }
//        }

    }

    public void updatePunchListDb(PunchlistDb punchlistDb, String comment,
                                  List<PunchListRejectReasonAttachments> reasonAttachments) {
        getDaoSession().getPunchlistDbDao().insertOrReplace(punchlistDb);
        punchListTransaction(punchlistDb.getPunchlistId(), punchlistDb.getUserId(), punchlistDb.getPunchlistIdMobile());
        savePunchListHistoryInDb(punchlistDb, comment, reasonAttachments);
    }

    public void savePunchListHistoryInDb(PunchlistDb punchlistDb, String comment,
                                         List<PunchListRejectReasonAttachments> rejectReasonAttachments) {
//        PunchlistDb punchlistDb1 = getPunchListDetailForHistory(punchlistDb);
//        if(punchlistDb1 != null) {

        Log.d("King1", "savePunchListHistoryInDb: ");
        /*List<PunchListHistoryDb> punchListHistoryDbs = getDaoSession().getPunchListHistoryDbDao().queryBuilder().where(
                PunchListHistoryDbDao.Properties.PunchListId.eq(punchlistDb.getPunchlistId()),
                PunchListHistoryDbDao.Properties.PunchListMobileId.eq(punchlistDb.getPunchlistIdMobile()),
                PunchListHistoryDbDao.Properties.UserId.eq(punchlistDb.getUserId())
        ).orderDesc(PunchListHistoryDbDao.Properties.CreatedTimestamp).limit(1).list();
        if(punchListHistoryDbs.size() > 0) {
            PunchListHistoryDb historyDb = punchListHistoryDbs.get(0);
            if(historyDb.getStatus() == punchlistDb.getStatus()){

            }
        }*/
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));

        PunchListHistoryDb punchListHistoryDb = new PunchListHistoryDb();
        punchListHistoryDb.setPunchListId(punchlistDb.getPunchlistId());
        punchListHistoryDb.setPunchListMobileId(punchlistDb.getPunchlistIdMobile().intValue());
        punchListHistoryDb.setPunchListAuditsId(0);
       /* if(punchlistDb.getPunchlistId() == 0)
            punchListHistoryDb.setPunchListAuditsMobileId(generateUniqueMobilePunchListHistoryId().intValue());
        else
            punchListHistoryDb.setPunchListAuditsMobileId(0);*/
        punchListHistoryDb.setPunchListAuditsMobileId(generateUniqueMobilePunchListHistoryId().intValue());
        punchListHistoryDb.setComments(comment);
        punchListHistoryDb.setUserId(punchlistDb.getUserId());
        punchListHistoryDb.setPjProjectsId(punchlistDb.getPjProjectsId());
        punchListHistoryDb.setStatus(punchlistDb.getStatus());
        punchListHistoryDb.setCreatedBy(String.valueOf(punchlistDb.getUserId()));
        punchListHistoryDb.setCreatedByName(loginResponse.getUserDetails().getFirstname() + " "+ loginResponse.getUserDetails().getLastname());
        punchListHistoryDb.setCreatedAt(new Date()); //TODO: need to check utc time.
        punchListHistoryDb.setCreatedTimestamp(System.currentTimeMillis());
        punchListHistoryDb.setIsSync(false);
        punchListHistoryDb.setIsAttachmentSync(!(rejectReasonAttachments.size() > 0));
        if (rejectReasonAttachments.size() > 0) {
            for (PunchListRejectReasonAttachments reasonAttachment : rejectReasonAttachments) {
                PunchListRejectReasonAttachments rejectAttachment = new PunchListRejectReasonAttachments();
                rejectAttachment.setRejectAttachmentId(
                        (reasonAttachment.getRejectAttachmentId() != null) ? reasonAttachment.getRejectAttachmentId() : 0);
                rejectAttachment.setPunchListAuditsId(0);
                rejectAttachment.setPunchListId(punchlistDb.getPunchlistId());
                rejectAttachment.setRejectAttachmentIdMobile(
                        (reasonAttachment.getRejectAttachmentIdMobile() != null) ? reasonAttachment.getRejectAttachmentIdMobile() : generateUniqueMobilePunchListRejectReasonId());
                rejectAttachment.setPunchListAuditsIdMobile(punchListHistoryDb.getPunchListAuditsMobileId().longValue());
                rejectAttachment.setAttachmentPath(reasonAttachment.getAttachmentPath());
                rejectAttachment.setUsersId(punchlistDb.getUserId());
                rejectAttachment.setIsAwsSync(false);
                rejectAttachment.setOriginalName(reasonAttachment.getOriginalName());
                rejectAttachment.setAwsSync(false);
                rejectAttachment.setPjProjectsId(punchlistDb.getPjProjectsId());

                getDaoSession().getPunchListRejectReasonAttachmentsDao().insertOrReplace(rejectAttachment);

                /*long attachmentIdMobile = getDaoSession().getPunchListRejectReasonAttachmentsDao().queryBuilder().where(
                                PunchListRejectReasonAttachmentsDao.Properties.RejectAttachmentIdMobile.isNotNull())
                        .orderDesc(PunchListRejectReasonAttachmentsDao.Properties.RejectAttachmentIdMobile)
                        .limit(1).list().get(0).getRejectAttachmentIdMobile();*/
                TransactionLogMobileDao mPronovosSyncDataDao = getDaoSession().getTransactionLogMobileDao();

                TransactionLogMobile transactionLogMobile = new TransactionLogMobile();

                transactionLogMobile.setUsersId(punchlistDb.getUserId());
                transactionLogMobile.setModule(TransactionModuleEnum.PUNCHLIST_REJECT_REASON_ATTACHMENT.ordinal());
                transactionLogMobile.setStatus(SyncDataEnum.NOTSYNC.ordinal());
                transactionLogMobile.setMobileId(rejectAttachment.getRejectAttachmentIdMobile());
                transactionLogMobile.setServerId(0L);
                transactionLogMobile.setCreateDate(Calendar.getInstance().getTime());
                mPronovosSyncDataDao.save(transactionLogMobile);
            }
        }
        getDaoSession().getPunchListHistoryDbDao().save(punchListHistoryDb);
        punchListHistoryTransaction(punchListHistoryDb);


        /*}else {
            savePunchListHistoryInDb(punchlistDb,comment, rejectReasonAttachments);
        }*/
    }

    public PunchlistDb getPunchListDetailForHistory(PunchlistDb punchlistDb) {
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        if (loginResponse != null) {
            List<PunchlistDb> punchlistDbList = getDaoSession().getPunchlistDbDao().queryBuilder().where(
                    PunchlistDbDao.Properties.PjProjectsId.eq(punchlistDb.getPjProjectsId()),
                    PunchlistDbDao.Properties.PunchlistId.eq(punchlistDb.getPunchlistId()),
                    PunchlistDbDao.Properties.IsSync.eq(true),
                    PunchlistDbDao.Properties.PunchlistIdMobile.eq(punchlistDb.getPunchlistIdMobile()),
                    PunchlistDbDao.Properties.UserId.eq(loginResponse.getUserDetails().getUsers_id()),
                    PunchlistDbDao.Properties.DeletedAt.isNull()
            ).list();
            if (punchlistDbList.size() > 0) {
                return punchlistDbList.get(0);
            } else {
                return null;
            }

        } else {
            return null;
        }
    }

    public void punchListHistoryTransaction(PunchListHistoryDb punchListHistoryDb) {
//        if (punchListHistoryDb.getPunchListAuditsId() != 0) {
        List<TransactionLogMobile> punchlistLogMobiles = getDaoSession().getTransactionLogMobileDao().queryBuilder()
                .where(TransactionLogMobileDao.Properties.MobileId.eq(punchListHistoryDb.getPunchListAuditsMobileId()),
                        TransactionLogMobileDao.Properties.ServerId.eq(punchListHistoryDb.getPunchListAuditsId()),
                        TransactionLogMobileDao.Properties.Module.eq(TransactionModuleEnum.PUNCHLIST_HISTORY.ordinal()),
                        TransactionLogMobileDao.Properties.Status.eq(SyncDataEnum.NOTSYNC),
                        TransactionLogMobileDao.Properties.UsersId.eq(punchListHistoryDb.getUserId())).list();

        if (punchlistLogMobiles.size() <= 0) {
            TransactionLogMobileDao mPronovosSyncDataDao = getDaoSession().getTransactionLogMobileDao();

            TransactionLogMobile transactionLogMobile = new TransactionLogMobile();

            transactionLogMobile.setUsersId(punchListHistoryDb.getUserId());
            transactionLogMobile.setModule(TransactionModuleEnum.PUNCHLIST_HISTORY.ordinal());
            transactionLogMobile.setStatus(SyncDataEnum.NOTSYNC.ordinal());
            Log.d("MAna****", "punchListHistoryTransaction: " + punchListHistoryDb.getPunchListAuditsMobileId());
            transactionLogMobile.setMobileId(Long.valueOf(punchListHistoryDb.getPunchListAuditsMobileId()));
            transactionLogMobile.setServerId(Long.valueOf(punchListHistoryDb.getPunchListAuditsId()));
            transactionLogMobile.setCreateDate(Calendar.getInstance().getTime());
            mPronovosSyncDataDao.save(transactionLogMobile);
            Log.i(TAG, "updatePunchlist attachment: setupAndStartWorkManager");
            ((PronovosApplication) context.getApplicationContext()).setupAndStartWorkManager();
        }
//        }
    }

    public void punchListHistoryTransactionRecord(PunchListHistoryDb punchListHistoryDb) {
//        if (punchListHistoryDb.getPunchListAuditsId() != 0) {
        List<TransactionLogMobile> punchlistLogMobiles = getDaoSession().getTransactionLogMobileDao().queryBuilder()
                .where(TransactionLogMobileDao.Properties.MobileId.eq(punchListHistoryDb.getPunchListAuditsMobileId()),
                        TransactionLogMobileDao.Properties.ServerId.eq(punchListHistoryDb.getPunchListAuditsId()),
                        TransactionLogMobileDao.Properties.Module.eq(TransactionModuleEnum.PUNCHLIST.ordinal()),
                        TransactionLogMobileDao.Properties.Status.eq(SyncDataEnum.NOTSYNC),
                        TransactionLogMobileDao.Properties.UsersId.eq(punchListHistoryDb.getUserId())).list();

        List<TransactionHistoryLogMobile> punchlistHistoryLogMobiles = getDaoSession().getTransactionHistoryLogMobileDao().queryBuilder()
                .where(TransactionHistoryLogMobileDao.Properties.MobileId.eq(punchListHistoryDb.getPunchListAuditsMobileId()),
                        TransactionHistoryLogMobileDao.Properties.ServerId.eq(punchListHistoryDb.getPunchListAuditsId()),
                        TransactionHistoryLogMobileDao.Properties.Module.eq(TransactionModuleEnum.PUNCHLIST_HISTORY.ordinal()),
//                        TransactionHistoryLogMobileDao.Properties.PunchListSyncId.eq(),
                        TransactionHistoryLogMobileDao.Properties.Status.eq(SyncDataEnum.NOTSYNC),
                        TransactionHistoryLogMobileDao.Properties.UsersId.eq(punchListHistoryDb.getUserId())).list();

        if (punchlistLogMobiles.size() <= 0) {
            TransactionLogMobileDao mPronovosSyncDataDao = getDaoSession().getTransactionLogMobileDao();

            TransactionLogMobile transactionLogMobile = new TransactionLogMobile();

            transactionLogMobile.setUsersId(punchListHistoryDb.getUserId());
            transactionLogMobile.setModule(TransactionModuleEnum.PUNCHLIST_HISTORY.ordinal());
            transactionLogMobile.setStatus(SyncDataEnum.NOTSYNC.ordinal());
            Log.d("MAna****", "punchListHistoryTransaction: " + punchListHistoryDb.getPunchListAuditsMobileId());
            transactionLogMobile.setMobileId(Long.valueOf(punchListHistoryDb.getPunchListAuditsMobileId()));
            transactionLogMobile.setServerId(Long.valueOf(punchListHistoryDb.getPunchListAuditsId()));
            transactionLogMobile.setCreateDate(Calendar.getInstance().getTime());
            mPronovosSyncDataDao.save(transactionLogMobile);
            Log.i(TAG, "updatePunchlist attachment: setupAndStartWorkManager");
            ((PronovosApplication) context.getApplicationContext()).setupAndStartWorkManager();
        }
//        }
    }

    public void updatePunchListDbForDelete(PunchlistDb punchlistDb) {
        getDaoSession().getPunchlistDbDao().insertOrReplace(punchlistDb);
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        if (loginResponse != null && punchlistDb.getPunchlistId() != 0) {
            int userId = loginResponse.getUserDetails().getUsers_id();
            List<TransactionLogMobile> punchlistLogMobiles = getDaoSession().getTransactionLogMobileDao().queryBuilder()
                    .where(TransactionLogMobileDao.Properties.MobileId.eq(punchlistDb.getPunchlistIdMobile()),
                            TransactionLogMobileDao.Properties.ServerId.eq(Long.valueOf(punchlistDb.getPunchlistId())),
                            TransactionLogMobileDao.Properties.Module.eq(TransactionModuleEnum.PUNCHLIST.ordinal()),
                            TransactionLogMobileDao.Properties.Status.eq(SyncDataEnum.NOTSYNC),
                            TransactionLogMobileDao.Properties.UsersId.eq(userId)).list();

            if (punchlistLogMobiles.size() <= 0) {
                TransactionLogMobileDao mPronovosSyncDataDao = getDaoSession().getTransactionLogMobileDao();
                TransactionLogMobile transactionLogMobile = new TransactionLogMobile();
                transactionLogMobile.setUsersId(userId);
                transactionLogMobile.setModule(TransactionModuleEnum.PUNCHLIST.ordinal());
                transactionLogMobile.setStatus(SyncDataEnum.NOTSYNC.ordinal());
                transactionLogMobile.setMobileId(punchlistDb.getPunchlistIdMobile());
                transactionLogMobile.setServerId(Long.valueOf(punchlistDb.getPunchlistId()));
                transactionLogMobile.setCreateDate(Calendar.getInstance().getTime());
                mPronovosSyncDataDao.save(transactionLogMobile);
                Log.i(TAG, "updatePunchlistdbfordelete: setupAndStartWorkManager");
                ((PronovosApplication) context.getApplicationContext()).setupAndStartWorkManager();
            }
        }

    }

    public List<PunchListAttachments> getNonSyncedAttachmentsForSpecificPunchlist(Long punchListMobileId) {
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        if (loginResponse != null) {
            List<PunchListAttachments> attachments = getDaoSession().getPunchListAttachmentsDao().queryBuilder().where(
                    PunchListAttachmentsDao.Properties.UsersId.eq(loginResponse.getUserDetails().getUsers_id()),
                    PunchListAttachmentsDao.Properties.PunchListIdMobile.eq(punchListMobileId),
                    PunchListAttachmentsDao.Properties.IsAwsSync.eq(false)).list();
            return attachments;
        } else {
            return new ArrayList<>();
        }
    }

    public List<PunchListAttachments> getNonSyncedAttachments() {
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        if (loginResponse != null) {
            List<PunchListAttachments> attachments = getDaoSession().getPunchListAttachmentsDao().queryBuilder().where(
                    PunchListAttachmentsDao.Properties.UsersId.eq(loginResponse.getUserDetails().getUsers_id()),
                    PunchListAttachmentsDao.Properties.IsAwsSync.eq(false)).list();
            return attachments;
        } else {
            return new ArrayList<>();
        }
    }

    public List<PunchListAttachments> getNonSyncedAttachments(int userId) {
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        if (loginResponse != null) {
            List<PunchListAttachments> attachments = getDaoSession().getPunchListAttachmentsDao().queryBuilder().where(
                    PunchListAttachmentsDao.Properties.UsersId.eq(userId),
                    PunchListAttachmentsDao.Properties.IsAwsSync.eq(false)).list();
            return attachments;
        } else {
            return new ArrayList<>();
        }
    }

    public List<PunchListRejectReasonAttachments> getNonSyncedRejectReasonAttachments(int userId) {
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        if (loginResponse != null) {
            List<PunchListRejectReasonAttachments> attachments = getDaoSession().getPunchListRejectReasonAttachmentsDao().queryBuilder().where(
                    PunchListRejectReasonAttachmentsDao.Properties.UsersId.eq(userId),
                    PunchListRejectReasonAttachmentsDao.Properties.IsAwsSync.eq(false)).list();
            return attachments;
        } else {
            return new ArrayList<>();
        }
    }

    public List<PunchList> getNonSyncPunchListSyncAttachmentList(int projectId) {
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        if (loginResponse != null) {
            List<PunchlistDb> punchlistDbList = getDaoSession().getPunchlistDbDao().queryBuilder().where(
                            PunchlistDbDao.Properties.PjProjectsId.eq(projectId),
                            PunchlistDbDao.Properties.IsSync.eq(false),
                            PunchlistDbDao.Properties.IsAttachmentSync.eq(true),
                            PunchlistDbDao.Properties.UserId.eq(loginResponse.getUserDetails().getUsers_id()))
                    .list();
            List<PunchList> punchlistreq = new ArrayList<>();
            for (PunchlistDb punchlistdb : punchlistDbList) {
                PunchList punchlist = new PunchList();
//                punchlist.setAssignedTo(punchlistdb.getAssignedTo());
                punchlist.setAssignedTo(punchlistdb.getAssignedTo());
                punchlist.setAssignedCc(punchlistdb.getAssignedCcList());
                punchlist.setAssigneeName(punchlistdb.getAssigneeName());
                punchlist.setDeletedAt(punchlistdb.getDeletedAt() != null ? DateFormatter.formatDateTimeHHForService(punchlistdb.getDeletedAt()) : "");
                punchlist.setCreatedAt(punchlistdb.getCreatedAt() != null ? DateFormatter.formatDateTimeHHForService(punchlistdb.getCreatedAt()) : "");
                punchlist.setDateDue(punchlistdb.getDateDue() != null ? DateFormatter.formatDateTimeHHForService(punchlistdb.getDateDue()) : "");
                punchlist.setDateCreated(punchlistdb.getDateCreated() != null ? DateFormatter.formatDateTimeHHForService(punchlistdb.getDateCreated()) : "");
                punchlist.setCreatedBy(punchlistdb.getCreatedByUserId());
                punchlist.setDescription(punchlistdb.getDescriptions());
                punchlist.setItemNumber(String.valueOf(punchlistdb.getItemNumber()));
                punchlist.setLocation(punchlistdb.getLocation());
                punchlist.setComments(punchlistdb.getComments());
                punchlist.setPjProjectsId(String.valueOf(punchlistdb.getPjProjectsId()));
                punchlist.setPunchListsId(String.valueOf(punchlistdb.getPunchlistId()));
                punchlist.setPunchListsIdMobile(String.valueOf(punchlistdb.getPunchlistIdMobile()));
                punchlist.setStatus(String.valueOf(punchlistdb.getStatus()));

                List<PunchListAttachments> attachments = getSyncedAttachments(punchlistdb.getPunchlistIdMobile());
                List<Attachments> attachmentsList = new ArrayList<>();
                for (PunchListAttachments punchListAttachment :
                        attachments) {
                    if (punchListAttachment != null) {
                        Attachments attachment = new Attachments();
                        if (punchListAttachment.getAttachmentId() != null) {
                            attachment.setAttachmentsId(punchListAttachment.getAttachmentId());
                        } else {
                            attachment.setAttachmentsId(0);

                        }
                        attachment.setAttachmentsIdMobile((int) (long) punchListAttachment.getAttachmentIdMobile());
                        attachment.setAttachPath(punchListAttachment.getAttachmentPath());
                        attachment.setDeletedAt(punchListAttachment.getDeletedAt() != null ? DateFormatter.formatDateTimeHHForService(punchListAttachment.getDeletedAt()) : "");
                        attachmentsList.add(attachment);
                    }
                }
                punchlist.setAttachments(attachmentsList);
                punchlistreq.add(punchlist);
            }

            return punchlistreq;
        } else {
            return new ArrayList<>();
        }
    }

    public List<PunchListAttachments> getSyncedAttachments(Long punchListsIdMobile) {
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        if (loginResponse != null) {
            List<PunchListAttachments> attachments = getDaoSession().getPunchListAttachmentsDao().queryBuilder().where(
                    PunchListAttachmentsDao.Properties.UsersId.eq(loginResponse.getUserDetails().getUsers_id()),
                    PunchListAttachmentsDao.Properties.PunchListIdMobile.eq(punchListsIdMobile),
                    PunchListAttachmentsDao.Properties.IsAwsSync.eq(true)).list();
            return attachments;
        } else {
            return new ArrayList<>();
        }
    }

    public List<PunchListRejectReasonAttachments> getSyncedRejectReasonAttachments(Long punchListsAuditIdMobile) {
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        if (loginResponse != null) {
            List<PunchListRejectReasonAttachments> attachments = getDaoSession().getPunchListRejectReasonAttachmentsDao().queryBuilder().where(
                    PunchListRejectReasonAttachmentsDao.Properties.UsersId.eq(loginResponse.getUserDetails().getUsers_id()),
                    PunchListRejectReasonAttachmentsDao.Properties.PunchListAuditsIdMobile.eq(punchListsAuditIdMobile),
                    PunchListRejectReasonAttachmentsDao.Properties.IsAwsSync.eq(true)).list();
            return attachments;
        } else {
            return new ArrayList<>();
        }
    }

    public List<PunchlistDb> getNonSyncPunchListsWithNonSyncedAttachments(int projectId) {
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        if (loginResponse != null) {
            List<PunchlistDb> punchlistDbList = getDaoSession().getPunchlistDbDao().queryBuilder().where(
                            PunchlistDbDao.Properties.PjProjectsId.eq(projectId),
                            PunchlistDbDao.Properties.IsSync.eq(false),
                            PunchlistDbDao.Properties.IsAttachmentSync.eq(false),
                            PunchlistDbDao.Properties.UserId.eq(loginResponse.getUserDetails().getUsers_id()),
                            PunchlistDbDao.Properties.DeletedAt.isNull())
                    .list();
            return punchlistDbList;
        } else {
            return new ArrayList<>();
        }
    }

    public boolean getNonSyncPunchListSize(int projectId) {
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        if (loginResponse != null) {
            List<PunchlistDb> punchlistDbList = getDaoSession().getPunchlistDbDao().queryBuilder().where(
                            PunchlistDbDao.Properties.PjProjectsId.eq(projectId),
                            PunchlistDbDao.Properties.IsSync.eq(false),
                            PunchlistDbDao.Properties.UserId.eq(loginResponse.getUserDetails().getUsers_id()))
                    .list();
            if (punchlistDbList.size() > 0)
                return true;
            else {
                return false;
            }
        } else {
            return false;
        }
    }

    public boolean isNonSyncPunchListAvailable(int userId) {
        List<PunchlistDb> punchlistDbList = getDaoSession().getPunchlistDbDao().queryBuilder().where(
                        PunchlistDbDao.Properties.IsSync.eq(false),
                        PunchlistDbDao.Properties.UserId.eq(userId))
                .list();
        if (punchlistDbList.size() > 0)
            return true;
        else {
            return false;
        }
    }

    public boolean isNonSyncPunchListHistoryAvailable(int userId) {
        List<PunchListHistoryDb> punchListHistoryDbs = getDaoSession().getPunchListHistoryDbDao().queryBuilder().where(
                        PunchListHistoryDbDao.Properties.IsSync.eq(false),
                        PunchListHistoryDbDao.Properties.UserId.eq(userId))
                .list();
        if (punchListHistoryDbs.size() > 0)
            return true;
        else {
            return false;
        }
    }


    /**
     * Get list of non synced punch list of punch list history
     *
     * @param punchListAuditIdMobile
     * @return
     */
    public List<PunchlistDb> getNonSyncedPunchList(Long punchListAuditIdMobile) {
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        if (loginResponse != null) {
            List<PunchListHistoryDb> history = getDaoSession().getPunchListHistoryDbDao().queryBuilder().where(
                    PunchListHistoryDbDao.Properties.UserId.eq(loginResponse.getUserDetails().getUsers_id()),
                    PunchListHistoryDbDao.Properties.IsSync.eq(false), // sort only non synced attachments
                    PunchListHistoryDbDao.Properties.PunchListAuditsMobileId.eq(punchListAuditIdMobile)).list();
            PunchListHistoryDb punchListHistoryDb = history.get(0);
            List<PunchlistDb> punchlistDbs;
            if (punchListHistoryDb.getPunchListId() == 0) {
                punchlistDbs = getDaoSession().getPunchlistDbDao().queryBuilder().where(
                        PunchlistDbDao.Properties.UserId.eq(loginResponse.getUserDetails().getUsers_id()),
                        PunchlistDbDao.Properties.IsSync.eq(false),
                        PunchlistDbDao.Properties.PunchlistIdMobile.eq(punchListHistoryDb.getPunchListMobileId())
                ).list();
                Log.d("Manya", "getNonSyncedPunchList ---- IF : "+ punchlistDbs.size());
            } else {
                punchlistDbs = getDaoSession().getPunchlistDbDao().queryBuilder().where(
                        PunchlistDbDao.Properties.UserId.eq(loginResponse.getUserDetails().getUsers_id()),
                        PunchlistDbDao.Properties.IsSync.eq(false),
                        PunchlistDbDao.Properties.PunchlistId.eq(punchListHistoryDb.getPunchListId())
                ).list();
                Log.d("Manya", "getNonSyncedPunchList ---- ELSE : "+ punchlistDbs.size());
            }
            return punchlistDbs;
        } else {
            return new ArrayList<>();
        }
    }

    /**
     * Get list of non synced attachments of work details
     *
     * @param punchListIdMobile
     * @return
     */
    public List<PunchListAttachments> getNonSyncedPunchListAttachments(Long punchListIdMobile) {

        loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        if (loginResponse != null) {
            List<PunchListAttachments> attachments = getDaoSession().getPunchListAttachmentsDao().queryBuilder().where(
                    PunchListAttachmentsDao.Properties.UsersId.eq(loginResponse.getUserDetails().getUsers_id()),
                    PunchListAttachmentsDao.Properties.IsAwsSync.eq(false), // sort only non synced attachments
                    PunchListAttachmentsDao.Properties.PunchListIdMobile.eq(punchListIdMobile)).list();
            return attachments;
        } else {
            return new ArrayList<>();
        }
    }

    public List<PunchListRejectReasonAttachments> getNonSyncedPunchListRejectReasonAttachments(Long punchListAuditMobileId) {

        loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        if (loginResponse != null) {
            List<PunchListRejectReasonAttachments> attachments = getDaoSession().getPunchListRejectReasonAttachmentsDao().queryBuilder().where(
                    PunchListRejectReasonAttachmentsDao.Properties.UsersId.eq(loginResponse.getUserDetails().getUsers_id()),
                    PunchListRejectReasonAttachmentsDao.Properties.IsAwsSync.eq(false), // sort only non synced attachments
                    PunchListRejectReasonAttachmentsDao.Properties.PunchListAuditsIdMobile.eq(punchListAuditMobileId)).list();
            return attachments;
        } else {
            return new ArrayList<>();
        }
    }

    /**
     * Get list of non synced attachments of work details
     *
     * @param punchListIdMobile
     * @return
     */
    public List<PunchListAttachments> getPunchListAttachments(Long punchListIdMobile) {

        loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        if (loginResponse != null) {
            List<PunchListAttachments> attachments = getDaoSession().getPunchListAttachmentsDao().queryBuilder().where(
                    PunchListAttachmentsDao.Properties.UsersId.eq(loginResponse.getUserDetails().getUsers_id()),
                    PunchListAttachmentsDao.Properties.PunchListIdMobile.eq(punchListIdMobile), PunchListAttachmentsDao.Properties.DeletedAt.isNull()).list();
            return attachments;
        } else {
            return new ArrayList<>();
        }
    }


    public void updateAttachment(PunchListAttachments attachment) {
        getDaoSession().getPunchListAttachmentsDao().update(attachment);
    }

    public void updatePunchListReasonAttachment(PunchlistDb punchlistDb, String comment, List<PunchListRejectReasonAttachments> punchListRejectReasonAttachments) {
        punchlistDb.setStatus(PunchListStatus.Rejected.getValue());
        punchlistDb.setIsSync(false);
        updatePunchListDb(punchlistDb, comment, punchListRejectReasonAttachments);
//        savePunchListHistoryInDb(punchlistDb,comment,punchListRejectReasonAttachments);
//        getDaoSession().getPunchListRejectReasonAttachmentsDao().saveInTx(attachment);
    }

    public void updateRejectReasonAttachment(PunchListRejectReasonAttachments attachment) {
        getDaoSession().getPunchListRejectReasonAttachmentsDao().update(attachment);
    }


    /**
     * Get max updated date from projects according to region id
     *
     * @return
     */
    public String getMAXPunchListHistoryUpdateDate(int pjProjectId, int usersId) {
        List<PunchListHistoryDb> maxPostIdRow = getDaoSession().getPunchListHistoryDbDao().queryBuilder().where(
                PunchListHistoryDbDao.Properties.PjProjectsId.eq(pjProjectId),
                PunchListHistoryDbDao.Properties.UserId.eq(usersId),
                PunchListHistoryDbDao.Properties.UpdatedAt.isNotNull()).orderDesc(PunchListHistoryDbDao.Properties.UpdatedAt).limit(1).list();
        if (maxPostIdRow.size() > 0) {
            Date maxUpdatedAt = maxPostIdRow.get(0).getUpdatedAt();
            return DateFormatter.formatDateTimeForService(maxUpdatedAt);
        }
        return "1970-01-01 01:01:01";
    }

    public List<PunchlistDb> getFilterPunchList(int projectId, boolean linkExisting) {

        loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        if (loginResponse != null) {
            List<PunchlistDb> punchlistDbList = null;
            if (linkExisting) {
                punchlistDbList = getDaoSession().getPunchlistDbDao().queryBuilder().where(
//                            PunchlistDbDao.Properties.PjProjectsId.eq(projectId),
//                            PunchlistDbDao.Properties.UserId.eq(loginResponse.getUserDetails().getUsers_id()),
//                            PunchlistDbDao.Properties.DeletedAt.isNull(),
                        new WhereCondition.StringCondition("pj_projects_id = " + projectId + "  AND punch_list_id > 0  AND users_id =" + loginResponse.getUserDetails().getUsers_id() + " AND deleted_at is null AND punch_list_id NOT IN " +
                                "(SELECT punch_list_id FROM drw_punchlist where pj_projects_id = " + projectId + " AND punch_list_id > 0  AND users_id = " + loginResponse.getUserDetails().getUsers_id() + ") " +
                                " OR pj_projects_id = " + projectId + " AND punch_list_id=0 AND users_id =" + loginResponse.getUserDetails().getUsers_id() + " AND deleted_at is null AND punch_list_id_mobile NOT IN " +
                                "(SELECT punch_list_id_mobile FROM drw_punchlist where pj_projects_id = " + projectId + " AND punch_list_id = 0   AND users_id = " + loginResponse.getUserDetails().getUsers_id() + ") " +
                                " GROUP BY UPPER(assigned_to)")
                ).list();

            } else {
                punchlistDbList = getDaoSession().getPunchlistDbDao().queryBuilder().where(
                        new WhereCondition.StringCondition("pj_projects_id = " + projectId +
                                " GROUP BY UPPER(assigned_to)")).list();
            }
            return punchlistDbList;
        } else {
            return new ArrayList<>();
        }
    }

    //     public List<ImageTag> getWeatherCondition(long photoMobileId) {
//        loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
//
//
//        Query query = daoSession.getWeatherConditionsDao().queryBuilder().where(
//                new WhereCondition.StringCondition("id IN " +
//                        "(SELECT tag_id FROM Taggables where taggable_id_mobile = " + photoMobileId + ")")).build();
//        List<ImageTag> photoTags = query.list();
//
//        return photoTags;
//    }
    public List<PunchlistDb> getFilterPunchList(int projectId, PunchListStatus
            punchListStatus, PunchlistAssignee punchlistAssignee, String searchKey, boolean linkExisting) {

        loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        if (loginResponse != null) {
            List<PunchlistDb> punchlistDbList = null;
            if (!linkExisting) {
                if (punchListStatus.getValue() == PunchListStatus.All.getValue() && punchlistAssignee.getUsersId() == -1) {

                    punchlistDbList = getDaoSession().getPunchlistDbDao().queryBuilder().where(
                            PunchlistDbDao.Properties.PjProjectsId.eq(projectId),
                            PunchlistDbDao.Properties.UserId.eq(loginResponse.getUserDetails().getUsers_id()),
                            PunchlistDbDao.Properties.DeletedAt.isNull()
                    ).whereOr(PunchlistDbDao.Properties.AssigneeName.like("%" + searchKey + "%"),
                            PunchlistDbDao.Properties.Descriptions.like("%" + searchKey + "%")).orderAsc(PunchlistDbDao.Properties.DateDue).list();
                } else if (punchListStatus.getValue() != PunchListStatus.All.getValue() && punchlistAssignee.getUsersId() == -1) {
                    punchlistDbList = getDaoSession().getPunchlistDbDao().queryBuilder().where(
                            PunchlistDbDao.Properties.PjProjectsId.eq(projectId),
                            PunchlistDbDao.Properties.Status.eq(punchListStatus.getValue()),
                            PunchlistDbDao.Properties.UserId.eq(loginResponse.getUserDetails().getUsers_id()),
                            PunchlistDbDao.Properties.DeletedAt.isNull()
                    ).whereOr(PunchlistDbDao.Properties.AssigneeName.like("%" + searchKey + "%"),
                            PunchlistDbDao.Properties.Descriptions.like("%" + searchKey + "%")).orderAsc(PunchlistDbDao.Properties.DateDue).list();
                } else if (punchListStatus.getValue() == PunchListStatus.All.getValue() && punchlistAssignee.getUsersId() != -1) {
//                    punchlistDbList = punchlistFilter(projectId,punchlistAssignee,searchKey);
                    punchlistDbList = getDaoSession().getPunchlistDbDao().queryBuilder().where(
                            PunchlistDbDao.Properties.PjProjectsId.eq(projectId),
                            PunchlistDbDao.Properties.AssignedTo.like("%" + punchlistAssignee.getUsersId() + "%"),  // TODO: Nitin
                            PunchlistDbDao.Properties.AssigneeName.like("%" + punchlistAssignee.getName() + "%"),
                            PunchlistDbDao.Properties.UserId.eq(loginResponse.getUserDetails().getUsers_id()),
                            PunchlistDbDao.Properties.DeletedAt.isNull()
                    ).whereOr(PunchlistDbDao.Properties.AssigneeName.like("%" + searchKey + "%"),
                            PunchlistDbDao.Properties.Descriptions.like("%" + searchKey + "%")).orderAsc(PunchlistDbDao.Properties.DateDue).list();
                } else if (punchListStatus.getValue() != PunchListStatus.All.getValue() && punchlistAssignee.getUsersId() != -1) {
                    punchlistDbList = getDaoSession().getPunchlistDbDao().queryBuilder().where(
                            PunchlistDbDao.Properties.PjProjectsId.eq(projectId),
                            PunchlistDbDao.Properties.Status.eq(punchListStatus.getValue()),
                            PunchlistDbDao.Properties.AssignedTo.like("%" + punchlistAssignee.getUsersId() + "%"),  // TODO: Nitin
                            PunchlistDbDao.Properties.AssigneeName.like("%" + punchlistAssignee.getName() + "%"),
                            PunchlistDbDao.Properties.UserId.eq(loginResponse.getUserDetails().getUsers_id()),
                            PunchlistDbDao.Properties.DeletedAt.isNull()
                    ).whereOr(PunchlistDbDao.Properties.AssigneeName.like("%" + searchKey + "%"),
                            PunchlistDbDao.Properties.Descriptions.like("%" + searchKey + "%")).orderAsc(PunchlistDbDao.Properties.DateDue).list();
                }

            } else {
                if (punchListStatus.getValue() == PunchListStatus.All.getValue() && punchlistAssignee.getUsersId() == -1) {

                    punchlistDbList = getDaoSession().getPunchlistDbDao().queryBuilder().where(
//                            PunchlistDbDao.Properties.PjProjectsId.eq(projectId),
//                            PunchlistDbDao.Properties.UserId.eq(loginResponse.getUserDetails().getUsers_id()),
//                            PunchlistDbDao.Properties.DeletedAt.isNull(),
                            new WhereCondition.StringCondition("pj_projects_id = " + projectId + "  AND punch_list_id > 0  AND " +
                                    "users_id =" + loginResponse.getUserDetails().getUsers_id() + " AND deleted_at is null AND" +
                                    " assignee_name LIKE '" + searchKey + "%' AND  punch_list_id NOT IN " +
                                    "(SELECT punch_list_id FROM drw_punchlist where pj_projects_id = " + projectId + " AND punch_list_id > 0  AND " +
                                    "users_id = " + loginResponse.getUserDetails().getUsers_id() + ") OR " +

                                    "pj_projects_id = " + projectId + "  AND punch_list_id > 0  AND " +
                                    "users_id =" + loginResponse.getUserDetails().getUsers_id() + " AND deleted_at is null AND" +
                                    " descriptions LIKE '" + searchKey + "%'  AND punch_list_id NOT IN " +
                                    "(SELECT punch_list_id FROM drw_punchlist where pj_projects_id = " + projectId + " AND punch_list_id > 0  AND " +
                                    "users_id = " + loginResponse.getUserDetails().getUsers_id() + ") " +


                                    " OR pj_projects_id = " + projectId + " AND punch_list_id=0 AND " +
                                    "users_id =" + loginResponse.getUserDetails().getUsers_id() + " AND deleted_at is null AND punch_list_id_mobile NOT IN " +
                                    "(SELECT punch_list_id_mobile FROM drw_punchlist where " +
                                    "pj_projects_id = " + projectId + " AND punch_list_id = 0   AND users_id = " + loginResponse.getUserDetails().getUsers_id() + ")" +
                                    " AND  assignee_name LIKE '" + searchKey + "%' " +


                                    " OR pj_projects_id = " + projectId + " AND punch_list_id=0 AND " +
                                    "users_id =" + loginResponse.getUserDetails().getUsers_id() + " AND deleted_at is null AND punch_list_id_mobile NOT IN " +
                                    "(SELECT punch_list_id_mobile FROM drw_punchlist where " +
                                    "pj_projects_id = " + projectId + " AND punch_list_id = 0   AND users_id = " + loginResponse.getUserDetails().getUsers_id() + ")" +
                                    " AND  descriptions LIKE '" + searchKey + "%' ")
                    ).orderAsc(PunchlistDbDao.Properties.DateDue)/*.whereOr(PunchlistDbDao.Properties.AssigneeName.like("%" + searchKey + "%"),
                            PunchlistDbDao.Properties.Descriptions.like("%" + searchKey + "%"))*/.list();
                } else if (punchListStatus.getValue() != PunchListStatus.All.getValue() && punchlistAssignee.getUsersId() == -1) {
                    punchlistDbList = getDaoSession().getPunchlistDbDao().queryBuilder().where(
//                            PunchlistDbDao.Properties.PjProjectsId.eq(projectId),
//                            PunchlistDbDao.Properties.Status.eq(punchListStatus.getValue()),
//                            PunchlistDbDao.Properties.UserId.eq(loginResponse.getUserDetails().getUsers_id()),
//                            PunchlistDbDao.Properties.DeletedAt.isNull(),
                            new WhereCondition.StringCondition("pj_projects_id = " + projectId + "  AND punch_list_id > 0 AND" +
                                    " users_id =" + loginResponse.getUserDetails().getUsers_id() +
                                    " AND status =" + punchListStatus.getValue() + " AND deleted_at is null AND " +
                                    " assignee_name LIKE '" + searchKey + "%' AND punch_list_id NOT IN " +
                                    "(SELECT punch_list_id FROM drw_punchlist where pj_projects_id = " + projectId + " AND " +
                                    "punch_list_id > 0   AND users_id = " + loginResponse.getUserDetails().getUsers_id() + ")" +

                                    " OR pj_projects_id = " + projectId + "  AND punch_list_id > 0 AND users_id =" + loginResponse.getUserDetails().getUsers_id() +
                                    " AND status =" + punchListStatus.getValue() + " AND deleted_at is null AND" +
                                    " descriptions LIKE '" + searchKey + "%' AND punch_list_id NOT IN " +
                                    "(SELECT punch_list_id FROM drw_punchlist where pj_projects_id = " + projectId + " AND punch_list_id > 0   AND users_id = " + loginResponse.getUserDetails().getUsers_id() + ")" +


                                    " OR pj_projects_id = " + projectId + " AND punch_list_id=0 AND users_id =" + loginResponse.getUserDetails().getUsers_id() +
                                    " AND status = " + punchListStatus.getValue() +
                                    " AND deleted_at is null AND" +
                                    " assignee_name LIKE '" + searchKey + "%' AND punch_list_id_mobile NOT IN " +
                                    "(SELECT punch_list_id_mobile FROM drw_punchlist where pj_projects_id = " + projectId + " AND punch_list_id=0 " +
                                    " AND users_id = " + loginResponse.getUserDetails().getUsers_id() + ")" +

                                    " OR pj_projects_id = " + projectId + " AND punch_list_id=0 AND users_id =" + loginResponse.getUserDetails().getUsers_id() +
                                    " AND status = " + punchListStatus.getValue() +
                                    " AND deleted_at is null AND " +
                                    " descriptions LIKE '" + searchKey + "%' AND punch_list_id_mobile NOT IN " +
                                    "(SELECT punch_list_id_mobile FROM drw_punchlist where pj_projects_id = " + projectId + " AND punch_list_id=0  AND users_id = " + loginResponse.getUserDetails().getUsers_id() + ")")
                    ).orderAsc(PunchlistDbDao.Properties.DateDue).list();
                } else if (punchListStatus.getValue() == PunchListStatus.All.getValue() && punchlistAssignee.getUsersId() != -1) {
                    punchlistDbList = getDaoSession().getPunchlistDbDao().queryBuilder().where(
//                            PunchlistDbDao.Properties.PjProjectsId.eq(projectId),
//                            PunchlistDbDao.Properties.AssignedTo.eq(punchlistAssignee.getUsersId()),
//                            PunchlistDbDao.Properties.AssigneeName.eq(punchlistAssignee.getName()),
//                            PunchlistDbDao.Properties.UserId.eq(loginResponse.getUserDetails().getUsers_id()),
//                            PunchlistDbDao.Properties.DeletedAt.isNull(),
                                    new WhereCondition.StringCondition("pj_projects_id = " + projectId + " AND punch_list_id > 0 AND assigned_to LIKE '%" + punchlistAssignee.getUsersId() + "%' AND assignee_name LIKE '%" + punchlistAssignee.getName()
                                            + "%' AND users_id =" + loginResponse.getUserDetails().getUsers_id() + " AND deleted_at is null AND " +
                                            " assignee_name LIKE '" + searchKey + "%' AND punch_list_id NOT IN " +
                                            "(SELECT punch_list_id FROM drw_punchlist where pj_projects_id = " + projectId + " AND punch_list_id > 0   AND users_id = " + loginResponse.getUserDetails().getUsers_id() + ")" +


                                            " OR pj_projects_id = " + projectId + " AND punch_list_id > 0 AND assigned_to LIKE '%" + punchlistAssignee.getUsersId() + "%' AND assignee_name LIKE '%" + punchlistAssignee.getName()
                                            + "%' AND users_id =" + loginResponse.getUserDetails().getUsers_id() + " AND deleted_at is null AND " +
                                            " descriptions LIKE '" + searchKey + "%' AND punch_list_id NOT IN " +
                                            "(SELECT punch_list_id FROM drw_punchlist where pj_projects_id = " + projectId + " AND punch_list_id > 0   AND users_id = " + loginResponse.getUserDetails().getUsers_id() + ")" +


                                            " OR pj_projects_id = " + projectId + " AND punch_list_id=0 AND assigned_to LIKE '%" + punchlistAssignee.getUsersId() + "%' AND assignee_name LIKE '%" + punchlistAssignee.getName() + "%' AND users_id =" + loginResponse.getUserDetails().getUsers_id() +
                                            " AND deleted_at is null AND " +
                                            " assignee_name LIKE '" + searchKey + "%' AND punch_list_id_mobile NOT IN " +
                                            "(SELECT punch_list_id_mobile FROM drw_punchlist where pj_projects_id = " + projectId + " AND punch_list_id=0   AND users_id = " + loginResponse.getUserDetails().getUsers_id() + ")" +


                                            " OR pj_projects_id = " + projectId + " AND punch_list_id=0 AND assigned_to LIKE '%" + punchlistAssignee.getUsersId() + "%' AND assignee_name LIKE '%" + punchlistAssignee.getName() + "%' AND users_id =" + loginResponse.getUserDetails().getUsers_id() +
                                            " AND deleted_at is null AND " +
                                            " descriptions LIKE '" + searchKey + "%' AND punch_list_id_mobile NOT IN " +
                                            "(SELECT punch_list_id_mobile FROM drw_punchlist where pj_projects_id = " + projectId + " AND punch_list_id=0   AND users_id = " + loginResponse.getUserDetails().getUsers_id() + ")"))
                            .orderAsc(PunchlistDbDao.Properties.DateDue).list();
                } else if (punchListStatus.getValue() != PunchListStatus.All.getValue() && punchlistAssignee.getUsersId() != -1) {
                    punchlistDbList = getDaoSession().getPunchlistDbDao().queryBuilder().where(
//                            PunchlistDbDao.Properties.PjProjectsId.eq(projectId),
//                            PunchlistDbDao.Properties.Status.eq(punchListStatus.getValue()),
//                            PunchlistDbDao.Properties.AssignedTo.eq(punchlistAssignee.getUsersId()),
//                            PunchlistDbDao.Properties.AssigneeName.eq(punchlistAssignee.getName()),
//                            PunchlistDbDao.Properties.UserId.eq(loginResponse.getUserDetails().getUsers_id()),
//                            PunchlistDbDao.Properties.DeletedAt.isNull(),
                            new WhereCondition.StringCondition("pj_projects_id = " + projectId + " AND punch_list_id > 0 AND assigned_to = " + punchlistAssignee.getUsersId() + " AND assignee_name = '" + punchlistAssignee.getName() + "' AND users_id =" + loginResponse.getUserDetails().getUsers_id() +
                                    " AND status =" + punchListStatus.getValue() + " AND deleted_at is null AND " +
                                    " assignee_name LIKE '" + searchKey + "%' AND " +
                                    "punch_list_id NOT IN " +
                                    "(SELECT punch_list_id FROM drw_punchlist where pj_projects_id = " + projectId + " AND punch_list_id > 0   AND users_id = " + loginResponse.getUserDetails().getUsers_id() + ")" +

                                    " OR pj_projects_id = " + projectId + " AND punch_list_id > 0 AND assigned_to LIKE '%" + punchlistAssignee.getUsersId() + "%' AND assignee_name LIKE '%" + punchlistAssignee.getName() + "%' AND users_id =" + loginResponse.getUserDetails().getUsers_id() +
                                    " AND status =" + punchListStatus.getValue() + " AND deleted_at is null AND " +
                                    " descriptions LIKE '" + searchKey + "%' AND " +
                                    "punch_list_id NOT IN " +
                                    "(SELECT punch_list_id FROM drw_punchlist where pj_projects_id = " + projectId + " AND punch_list_id > 0   AND users_id = " + loginResponse.getUserDetails().getUsers_id() + ")" +


                                    " OR pj_projects_id = " + projectId + " AND punch_list_id=0 AND assigned_to LIKE '%" + punchlistAssignee.getUsersId() + "%' AND assignee_name LIKE '%" + punchlistAssignee.getName() + "%' AND users_id =" + loginResponse.getUserDetails().getUsers_id() +
                                    " AND status = " + punchListStatus.getValue() +
                                    " AND deleted_at is null AND " +
                                    " assignee_name LIKE '" + searchKey + "%' AND " +
                                    "punch_list_id_mobile NOT IN " +
                                    "(SELECT punch_list_id_mobile FROM drw_punchlist where pj_projects_id = " + projectId + " AND punch_list_id=0   AND users_id = " + loginResponse.getUserDetails().getUsers_id() + ")" +

                                    " OR pj_projects_id = " + projectId + " AND punch_list_id=0 AND assigned_to LIKE '%" + punchlistAssignee.getUsersId() + "%' AND assignee_name LIKE '%" + punchlistAssignee.getName() + "%' AND users_id =" + loginResponse.getUserDetails().getUsers_id() +
                                    " AND status = " + punchListStatus.getValue() +
                                    " AND deleted_at is null AND " +
                                    " descriptions LIKE '" + searchKey + "%' AND " +
                                    "punch_list_id_mobile NOT IN " +
                                    "(SELECT punch_list_id_mobile FROM drw_punchlist where pj_projects_id = " + projectId + " AND punch_list_id=0   AND users_id = " + loginResponse.getUserDetails().getUsers_id() + ")")
                    ).orderAsc(PunchlistDbDao.Properties.DateDue).list();
                }
            }
            return punchlistDbList;
        } else {
            return new ArrayList<>();
        }
    }

    private PunchListAttachments getAttachment(long punchListMobileId, Integer attachmentId) {

        loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        if (loginResponse != null) {
            List<PunchListAttachments> attachments = getDaoSession().getPunchListAttachmentsDao().queryBuilder().where(PunchListAttachmentsDao.Properties.UsersId.eq(loginResponse.getUserDetails().getUsers_id()), PunchListAttachmentsDao.Properties.PunchListIdMobile.eq(punchListMobileId),
                    PunchListAttachmentsDao.Properties.AttachmentId.eq(attachmentId)).limit(1).list();
            if (attachments.size() > 0) {
                return attachments.get(0);
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public PunchListAttachments getPunchlistAttachmentDetail(Integer usersId, Long mobileId, Long serverId) {
//        Log.i(TAG, "getPunchlistAttachmentDetail: " + mobileId + "  " + serverId);
        List<PunchListAttachments> punchListAttachments = getDaoSession().getPunchListAttachmentsDao().queryBuilder()
                .where(PunchListAttachmentsDao.Properties.AttachmentIdMobile.eq(mobileId),
                        PunchListAttachmentsDao.Properties.AttachmentId.eq(serverId),
                        PunchListAttachmentsDao.Properties.UsersId.eq(usersId)).list();

        return punchListAttachments.get(0);
    }

    public PunchListRejectReasonAttachments getPunchListRejectReasonAttachmentDetail(Integer usersId, Long mobileId, Long serverId) {
//        Log.i(TAG, "getPunchlistAttachmentDetail: " + mobileId + "  " + serverId);
        List<PunchListRejectReasonAttachments> punchListRejectReasonAttachments = getDaoSession().getPunchListRejectReasonAttachmentsDao().queryBuilder()
                .where(PunchListRejectReasonAttachmentsDao.Properties.RejectAttachmentIdMobile.eq(mobileId),
                        PunchListRejectReasonAttachmentsDao.Properties.RejectAttachmentId.eq(serverId),
                        PunchListRejectReasonAttachmentsDao.Properties.UsersId.eq(usersId)).list();

        return punchListRejectReasonAttachments.get(0);
    }

    public PunchlistDb getPunchlistDetail(Integer usersId, Long mobileId, Long serverId) {
        List<PunchlistDb> punchlistDbs = getDaoSession().getPunchlistDbDao().queryBuilder()
                .where(PunchlistDbDao.Properties.PunchlistIdMobile.eq(mobileId),
                        PunchlistDbDao.Properties.PunchlistId.eq(serverId),
                        PunchlistDbDao.Properties.UserId.eq(usersId)).list();
        if (punchlistDbs.size() > 0) {
            return punchlistDbs.get(0);
        } else return null;
    }

    public PunchListHistoryDb getPunchListHistoryDetailSingle(Integer usersId, Long mobileId, Long serverId) {

        List<PunchListHistoryDb> punchListHistoryDbs = getDaoSession().getPunchListHistoryDbDao().queryBuilder()
                .where(PunchListHistoryDbDao.Properties.PunchListAuditsMobileId.eq(mobileId),
                        PunchListHistoryDbDao.Properties.PunchListAuditsId.eq(serverId),
                        PunchListHistoryDbDao.Properties.UserId.eq(usersId)).orderDesc(PunchListHistoryDbDao.Properties.CreatedTimestamp).list();
        if (punchListHistoryDbs.size() > 0) {
            return punchListHistoryDbs.get(0);
        } else return null;
    }

    public PunchListHistoryDb getPunchListHistoryDetailSingle(Integer usersId, Long mobileId) {

        List<PunchListHistoryDb> punchListHistoryDbs = getDaoSession().getPunchListHistoryDbDao().queryBuilder()
                .where(PunchListHistoryDbDao.Properties.PunchListMobileId.eq(mobileId),
                        PunchListHistoryDbDao.Properties.UserId.eq(usersId)).orderDesc(PunchListHistoryDbDao.Properties.CreatedTimestamp).list();
        if (punchListHistoryDbs.size() > 0) {
            return punchListHistoryDbs.get(0);
        } else return null;
    }

    public List<PunchListHistoryDb> getPunchListHistoryDetail(Integer usersId, Long mobileId, Long serverId) {
        int punchListMobileId = getDaoSession().getPunchListHistoryDbDao().queryBuilder()
                .where(PunchListHistoryDbDao.Properties.PunchListAuditsMobileId.eq(mobileId),
                        PunchListHistoryDbDao.Properties.PunchListAuditsId.eq(serverId),
                        PunchListHistoryDbDao.Properties.UserId.eq(usersId)).limit(1).list().get(0).getPunchListMobileId();

        List<PunchListHistoryDb> punchListHistoryDbs = getDaoSession().getPunchListHistoryDbDao().queryBuilder()
                .where(/*PunchListHistoryDbDao.Properties.PunchListAuditsMobileId.eq(mobileId),*/
                        PunchListHistoryDbDao.Properties.PunchListAuditsId.eq(serverId),
                        PunchListHistoryDbDao.Properties.PunchListMobileId.eq(punchListMobileId),
                        PunchListHistoryDbDao.Properties.UserId.eq(usersId))
                .orderAsc(PunchListHistoryDbDao.Properties.CreatedAt).list();
        if (punchListHistoryDbs.size() > 0) {
            return punchListHistoryDbs;
        } else return null;
    }

    public List<PunchListHistoryDb> getPunchListHistories(Integer usersId, Long punchListId, Long punchlistIdMobile) {
        List<PunchListHistoryDb> punchListHistoryDbs = getDaoSession().getPunchListHistoryDbDao().queryBuilder()
                .where(PunchListHistoryDbDao.Properties.PunchListId.eq(punchListId),
                        PunchListHistoryDbDao.Properties.PunchListMobileId.eq(punchlistIdMobile),
                        PunchListHistoryDbDao.Properties.UserId.eq(usersId)).orderDesc(PunchListHistoryDbDao.Properties.CreatedTimestamp).list();
        if (punchListHistoryDbs.size() > 0) {
            return punchListHistoryDbs;
        } else return new ArrayList<>();
    }

    public List<PunchListRejectReasonAttachments> getPunchListRejectHistoryAttachments(Integer usersId, Long punchListId, int punchListAuditId, int punchListMobileAuditId) {
        List<PunchListRejectReasonAttachments> punchListRejectReasonAttachments;
        if(punchListAuditId == 0){
            punchListRejectReasonAttachments = getDaoSession().getPunchListRejectReasonAttachmentsDao().queryBuilder()
                    .where(PunchListRejectReasonAttachmentsDao.Properties.PunchListId.eq(punchListId),
                            PunchListRejectReasonAttachmentsDao.Properties.PunchListAuditsId.eq(punchListAuditId),
                            PunchListRejectReasonAttachmentsDao.Properties.PunchListAuditsIdMobile.eq(punchListMobileAuditId),
                            PunchListRejectReasonAttachmentsDao.Properties.UsersId.eq(usersId)).list();
      }else {
            punchListRejectReasonAttachments = getDaoSession().getPunchListRejectReasonAttachmentsDao().queryBuilder()
                    .where(PunchListRejectReasonAttachmentsDao.Properties.PunchListId.eq(punchListId),
                            PunchListRejectReasonAttachmentsDao.Properties.PunchListAuditsId.eq(punchListAuditId),
                            PunchListRejectReasonAttachmentsDao.Properties.UsersId.eq(usersId)).list();
        }

        if (punchListRejectReasonAttachments.size() > 0) {
            return punchListRejectReasonAttachments;
        } else return new ArrayList<>();
    }

    public PunchListRejectReasonAttachments getPunchListRejectReasonDetail(Integer usersId, Long mobileId, Long serverId) {
        List<PunchListRejectReasonAttachments> rejectReasonAttachments = getDaoSession().getPunchListRejectReasonAttachmentsDao().queryBuilder()
                .where(PunchListRejectReasonAttachmentsDao.Properties.RejectAttachmentIdMobile.eq(mobileId),
                        PunchListRejectReasonAttachmentsDao.Properties.RejectAttachmentId.eq(serverId),
                        PunchListRejectReasonAttachmentsDao.Properties.UsersId.eq(usersId)).list();
        if (rejectReasonAttachments.size() > 0) {
            return rejectReasonAttachments.get(0);
        } else return null;
    }

    /**
     * Delete the sync data from Transaction table.
     *
     * @param transactionLogMobile used to apply conditions for database operations.
     */
    private void deleteSyncData(TransactionLogMobile transactionLogMobile) {
        DeleteQuery<TransactionLogMobile> pronovosSyncDataDeleteQuery = getDaoSession().queryBuilder(TransactionLogMobile.class)
                .where(TransactionLogMobileDao.Properties.SyncId.eq(transactionLogMobile.getSyncId()))
                .buildDelete();
        pronovosSyncDataDeleteQuery.executeDeleteWithoutDetachingEntities();
    }


    public Long generateUniqueMobilePunchListId() {

        long timeSeed = System.nanoTime(); // to get the current date time value

        double randSeed = Math.random() * 1000; // random number generation

        long mobileId = (long) (timeSeed * randSeed);

        String s = mobileId + "";
        String subStr = s.substring(0, 9);
        mobileId = Long.parseLong(subStr);

        List<PunchlistDb> punchlistDbs = getDaoSession().getPunchlistDbDao().queryBuilder().where(
                PunchlistDbDao.Properties.PunchlistIdMobile.eq(mobileId)).limit(1).list();
        if (punchlistDbs.size() > 0) {
            return generateUniqueMobilePunchListId();
        }
        return mobileId;
    }

    public Long generateUniqueMobilePunchListHistoryId() {

        long timeSeed = System.nanoTime(); // to get the current date time value

        double randSeed = Math.random() * 1000; // random number generation

        long mobileId = (long) (timeSeed * randSeed);

        String s = mobileId + "";
        String subStr = s.substring(0, 9);
        mobileId = Long.parseLong(subStr);

        List<PunchListHistoryDb> punchListHistoryDbs = getDaoSession().getPunchListHistoryDbDao().queryBuilder().where(
                PunchListHistoryDbDao.Properties.PunchListAuditsMobileId.eq(mobileId)).limit(1).list();
        if (punchListHistoryDbs.size() > 0) {
            return generateUniqueMobilePunchListId();
        }
        return mobileId;
    }

    public Long generateUniqueMobilePunchListRejectReasonId() {

        long timeSeed = System.nanoTime(); // to get the current date time value

        double randSeed = Math.random() * 1000; // random number generation

        long mobileId = (long) (timeSeed * randSeed);

        String s = mobileId + "";
        String subStr = s.substring(0, 9);
        mobileId = Long.parseLong(subStr);

        List<PunchListRejectReasonAttachments> punchListRejectReasonAttachments = getDaoSession().getPunchListRejectReasonAttachmentsDao().queryBuilder().where(
                PunchListRejectReasonAttachmentsDao.Properties.RejectAttachmentIdMobile.eq(mobileId)).limit(1).list();
        if (punchListRejectReasonAttachments.size() > 0) {
            return generateUniqueMobilePunchListId();
        }
        return mobileId;
    }

    public List<PunchlistDb> getNotSyncPunchList() {
        List<PunchlistDb> punchlistDbs = getDaoSession().getPunchlistDbDao().queryBuilder().where(
                PunchlistDbDao.Properties.IsSync.eq(false)).list();
        return punchlistDbs;
    }

    public List<PunchListAttachments> getNotSyncPunchListAttachment() {
        List<PunchListAttachments> punchListAttachments = getDaoSession().getPunchListAttachmentsDao().queryBuilder().where(
                PunchListAttachmentsDao.Properties.IsAwsSync.eq(false)).list();
        return punchListAttachments;
    }

    public void deleteDrawingPunchlist(Integer projectId, Integer punchListId, Long punchlistMobileId, int userId) {
        DeleteQuery<DrwPunchlist> drwPunchlistDeleteQuery;
        if (punchListId != 0) {
            drwPunchlistDeleteQuery = getDaoSession().queryBuilder(DrwPunchlist.class)
                    .where(DrwPunchlistDao.Properties.PjProjectsId.eq(projectId),
                            DrwPunchlistDao.Properties.PunchlistId.eq(punchListId),
                            DrwPunchlistDao.Properties.UserId.eq(userId))
                    .buildDelete();
        } else {
            drwPunchlistDeleteQuery = getDaoSession().queryBuilder(DrwPunchlist.class)
                    .where(DrwPunchlistDao.Properties.PjProjectsId.eq(projectId),
                            DrwPunchlistDao.Properties.PunchlistIdMobile.eq(punchlistMobileId),
                            DrwPunchlistDao.Properties.UserId.eq(userId))
                    .buildDelete();
        }
        drwPunchlistDeleteQuery.executeDeleteWithoutDetachingEntities();

    }

    public List<PunchlistDb> getAllPunchlist() {
        return getDaoSession().getPunchlistDbDao().queryBuilder().limit(1).list();
    }

    public List<PunchlistDrawing> getPunchListDrawings(Long punchlistIdMobile) {
        List<PunchlistDrawing> punchListAttachments = getDaoSession().getPunchlistDrawingDao().queryBuilder().where(
                PunchlistDrawingDao.Properties.PunchlistIdMobile.eq(punchlistIdMobile)).list();
        return punchListAttachments;
    }

    public void addPunchlistDrawing(PunchlistDb punchlistDb, DrawingList drawingList) {
        /*PunchlistDrawing punchlistDrawing = new PunchlistDrawing();
        List<PunchlistDrawing> punchlistDrawings;
        if (punchlistDb.getPunchlistId() == 0) {
            punchlistDrawings = getDaoSession().getPunchlistDrawingDao().queryBuilder().where(
                    PunchlistDrawingDao.Properties.PunchlistIdMobile.eq(punchlistDb.getPunchlistIdMobile()),
                    PunchlistDrawingDao.Properties.OriginalDrwId.eq(drawingList.getOriginalDrwId())).list();
        } else {
            punchlistDrawings = getDaoSession().getPunchlistDrawingDao().queryBuilder().where(
                    PunchlistDrawingDao.Properties.PunchlistId.eq(punchlistDb.getPunchlistId()),
                    PunchlistDrawingDao.Properties.OriginalDrwId.eq(drawingList.getOriginalDrwId())).list();
        }
        if (punchlistDrawings.size() > 0) {
            return;
        }

        punchlistDrawing.setDrawingName(drawingList.getDrawingName());
        punchlistDrawing.setOriginalDrwId(drawingList.getOriginalDrwId());
        punchlistDrawing.setDrwDisciplineId(drawingList.getDrawingDiscipline());
        punchlistDrawing.setDrwDisciplinesId(Integer.parseInt(drawingList.getDrawingDisciplineId()));
        punchlistDrawing.setDrwFoldersId(drawingList.getDrwFoldersId());
        punchlistDrawing.setRevisitedNum(drawingList.getRevisitedNum());
        punchlistDrawing.setPunchlistId(punchlistDb.getPunchlistId());
        punchlistDrawing.setPunchlistIdMobile(punchlistDb.getPunchlistIdMobile());
        getDaoSession().getPunchlistDrawingDao().insert(punchlistDrawing);
*/
    }

    public void deletePunchlistDrawing(PunchlistDb mPunchlistDb, DrawingList drawingList) {
       /* if (mPunchlistDb.getPunchlistId() == 0) {
            DeleteQuery<PunchlistDrawing> PunchListAttachmentsDeleteQuery = getDaoSession().queryBuilder(PunchlistDrawing.class)
                    .where(PunchlistDrawingDao.Properties.PunchlistIdMobile.eq(mPunchlistDb.getPunchlistIdMobile()),
                            PunchlistDrawingDao.Properties.OriginalDrwId.eq(drawingList.getOriginalDrwId()))
                    .buildDelete();
            PunchListAttachmentsDeleteQuery.executeDeleteWithoutDetachingEntities();

        } else {
            DeleteQuery<PunchlistDrawing> PunchListAttachmentsDeleteQuery = getDaoSession().queryBuilder(PunchlistDrawing.class)
                    .where(PunchlistDrawingDao.Properties.PunchlistId.eq(mPunchlistDb.getPunchlistId()),
                            PunchlistDrawingDao.Properties.OriginalDrwId.eq(drawingList.getOriginalDrwId()))
                    .buildDelete();
            PunchListAttachmentsDeleteQuery.executeDeleteWithoutDetachingEntities();
        }
*/
    }

    public List<PunchlistDb> getPunchListForStatusCheck(int userId) {
        List<PunchlistDb> punchlistDb = getDaoSession().getPunchlistDbDao().queryBuilder().where(
                PunchlistDbDao.Properties.UserId.eq(userId),
                PunchlistDbDao.Properties.IsSync.eq(false)
        ).limit(1).list();
        return punchlistDb;
    }
}
