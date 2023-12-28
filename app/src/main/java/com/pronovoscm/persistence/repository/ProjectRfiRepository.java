package com.pronovoscm.persistence.repository;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.pronovoscm.PronovosApplication;
import com.pronovoscm.model.RFIStatusEnum;
import com.pronovoscm.model.response.login.LoginResponse;
import com.pronovoscm.persistence.domain.DaoSession;
import com.pronovoscm.persistence.domain.PjDocumentsFilesDao;
import com.pronovoscm.persistence.domain.PjRfi;
import com.pronovoscm.persistence.domain.PjRfiAttachments;
import com.pronovoscm.persistence.domain.PjRfiAttachmentsDao;
import com.pronovoscm.persistence.domain.PjRfiContactList;
import com.pronovoscm.persistence.domain.PjRfiContactListDao;
import com.pronovoscm.persistence.domain.PjRfiDao;
import com.pronovoscm.persistence.domain.PjRfiReplies;
import com.pronovoscm.persistence.domain.PjRfiRepliesDao;
import com.pronovoscm.utils.DateFormatter;
import com.pronovoscm.utils.SharedPref;

import org.greenrobot.greendao.query.DeleteQuery;
import org.greenrobot.greendao.query.QueryBuilder;
import org.greenrobot.greendao.query.WhereCondition;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ProjectRfiRepository extends AbstractRepository {
    private Context mContext;
    private LoginResponse loginResponse;

    public ProjectRfiRepository(DaoSession daoSession, Context context) {
        super(daoSession);
        mContext = context;
    }

    /**
     * Save PjRfi items
     *
     * @param rfilist
     */
    public void saveProjectRfiList(List<PjRfi> rfilist) {
        getDaoSession().getPjRfiDao().saveInTx(rfilist);
    }

    /**
     * update PjRfi items
     *
     * @param rfilist
     */
    public void updateProjectRfiList(List<PjRfi> rfilist) {
        getDaoSession().getPjRfiDao().insertOrReplaceInTx(rfilist);
    }

    /**
     * Save PjRfi items
     *
     * @param pjRfi
     */
    public void savePjRfi(PjRfi pjRfi) {
        getDaoSession().getPjRfiDao().saveInTx(pjRfi);
    }

    /**
     * Save PjRfi items
     *
     * @param pjRfi
     */
    public void savePjRfiReplies(PjRfiReplies pjRfi) {
        getDaoSession().getPjRfiRepliesDao().saveInTx(pjRfi);
    }

    /*

    RFI Contact List Assigned To and CC field:

    1) If default_type equals 2 then, it is for CC field.
    2) If default_type equals 3 then, it is for Assigned To field.

     */
    public PjRfiContactList getSearchPjRfiAssignToContact(PjRfi rfi) {

        List<PjRfiContactList> pjRfiContactLists = getDaoSession().getPjRfiContactListDao().queryBuilder()
                .where(PjRfiContactListDao.Properties.PjRfiId.eq(rfi.getPjRfiId()),
                        PjRfiContactListDao.Properties.DefaultType.eq(3)).list();
        if (pjRfiContactLists != null && pjRfiContactLists.size() > 0) {
            return pjRfiContactLists.get(0);
        } else
            return null;

    }

    public List<PjRfiContactList> getSearchPjRfiCcContact(PjRfi rfi) {
        List<PjRfiContactList> pjRfiContactLists = getDaoSession().getPjRfiContactListDao().queryBuilder()
                .where(PjRfiContactListDao.Properties.PjRfiId.eq(rfi.getPjRfiId()),
                        PjRfiContactListDao.Properties.DefaultType.eq(2)).list();
        if (pjRfiContactLists != null && pjRfiContactLists.size() > 0) {
            return pjRfiContactLists;
        } else
            return new ArrayList<>();

    }

    public List<PjRfiContactList> getSearchPjRfiReceivedFromContact(PjRfi rfi) {
        if (!TextUtils.isEmpty(rfi.getReceivedFrom())) {
            List<PjRfiContactList> pjRfiContactLists = getDaoSession().getPjRfiContactListDao().queryBuilder()
                    .where(/*PjRfiContactListDao.Properties.PjRfiId.eq(rfi.getPjRfiId()),*/
                            PjRfiContactListDao.Properties.ContactList.eq(rfi.getReceivedFrom())).list();
            if (pjRfiContactLists != null && pjRfiContactLists.size() > 0) {
                return pjRfiContactLists;
            } else
                return new ArrayList<>();
        } else
            return new ArrayList<>();

    }

    public List<PjRfiContactList> getSearchPjRfiContactList(int projectId) {
        List<PjRfiContactList> pjRfiContactLists = getDaoSession().getPjRfiContactListDao().queryBuilder()
                .where(/*PjRfiContactListDao.Properties.Pj.eq(rfi.getPjRfiId()),*/
                        PjRfiContactListDao.Properties.DefaultType.eq(3),
                        /*  PjRfiContactListDao.Properties.Name.*/
                        new WhereCondition.StringCondition(" pj_rfi_id IN " + "(SELECT pj_rfi_id FROM pj_rfi WHERE pj_projects_id = "
                                + projectId + " AND deleted_at is null )")

                ).list();
        if (pjRfiContactLists != null && pjRfiContactLists.size() > 0) {
            return pjRfiContactLists;
        } else
            return new ArrayList<>();
    }


/*    public List<PjRfi> getSearchRfiList(int projectId, String searchKey) {
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(mContext).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        if (loginResponse != null) {
            List<PjRfi> punchlistDbList = getDaoSession().getPjRfiDao().queryBuilder().where(
                    PjRfiDao.Properties.PjProjectsId.eq(projectId),
                    //PjRfiDao.Properties.UserId.eq(loginResponse.getUserDetails().getUsers_id()),
                    PjRfiDao.Properties.DeletedAt.isNull())
                    .whereOr(
                            PjRfiDao.Properties.RfiTitle.like("%" + searchKey + "%"),
                            PjRfiDao.Properties.RfiNumber.like("%" + searchKey + "%")
                    ).orderAsc(PjRfiDao.Properties.DueDate).list();
            return punchlistDbList;
        } else {
            return new ArrayList<>();
        }
    }*/
/*
    public List<PjRfi> getSearchRfiFilterList(int projectId, String searchKey, Date dueDate, Date submittedDate, PjRfiContactList assignToContact) {

            return new ArrayList<>();
        }
    }*/

    public List<PjRfi> getSearchRfiFilterList(int projectId, String searchKey, Date dueDate, Date submittedDate, PjRfiContactList assignToContact, RFIStatusEnum mRFIStatusEnum) {
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(mContext).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        if (loginResponse != null) {
            PjRfiDao pjRfiDao = getDaoSession().getPjRfiDao();
            List<PjRfi> punchlistDbList = new ArrayList<>();

            QueryBuilder<PjRfi> pjRfiQueryBuilder = pjRfiDao.queryBuilder().where(
                    PjRfiDao.Properties.PjProjectsId.eq(projectId),
                    PjRfiDao.Properties.DeletedAt.isNull());
            if (mRFIStatusEnum.getStatusValue() != -1)
                pjRfiQueryBuilder.where(PjRfiDao.Properties.Status.eq(mRFIStatusEnum.getStatusValue()));
            if (dueDate != null) {
                pjRfiQueryBuilder.where(PjRfiDao.Properties.DueDate.eq(dueDate));
            }
            if (submittedDate != null) {
                pjRfiQueryBuilder.where(PjRfiDao.Properties.DateSubmitted.eq(submittedDate));
            }
            if (assignToContact != null) {
                punchlistDbList = pjRfiQueryBuilder.where(new WhereCondition.StringCondition(" pj_rfi_id IN " + "(SELECT pj_rfi_id FROM pj_rfi_contact_list WHERE name = '"
                        + assignToContact.getName() + "'  )"))
                        .whereOr(
                                PjRfiDao.Properties.RfiTitle.like("%" + searchKey + "%"),
                                PjRfiDao.Properties.RfiNumber.like("%" + searchKey + "%")
                        )
                        .orderAsc(PjRfiDao.Properties.DueDate).list();
            } else {
                punchlistDbList = pjRfiQueryBuilder
                        .whereOr(PjRfiDao.Properties.RfiTitle.like("%" + searchKey + "%"),
                                PjRfiDao.Properties.RfiNumber.like("%" + searchKey + "%")
                        ).orderAsc(PjRfiDao.Properties.DueDate).list(); // complete
            }


            return punchlistDbList;
        } else {
            return new ArrayList<>();
        }
    }

    /**
     * Save PjRfiAttachments items
     *
     * @param pjRfi
     */
    public void savePjRfiAttachments(PjRfiAttachments pjRfi) {
        getDaoSession().getPjRfiAttachmentsDao().saveInTx(pjRfi);
    }

    /**
     * Save PjRfiAttachments items
     *
     * @param pjRfi
     */
    public void savePjRfiContactList(PjRfiContactList pjRfi) {
        getDaoSession().getPjRfiContactListDao().saveInTx(pjRfi);
    }

    public void deleteRfiReplies(int rfiId) {
        DeleteQuery<PjRfiReplies> deleteQuery = getDaoSession().queryBuilder(PjRfiReplies.class).
                where(PjRfiRepliesDao.Properties.PjRfiId.eq(rfiId)).buildDelete();
        deleteQuery.executeDeleteWithoutDetachingEntities();
    }

    public void deleteRFIAttachment(int rfiId, int attachmentId) {
        DeleteQuery<PjRfiAttachments> deleteQuery = getDaoSession().queryBuilder(PjRfiAttachments.class).
                where(PjRfiAttachmentsDao.Properties.PjRfiId.eq(rfiId),
                        PjRfiAttachmentsDao.Properties.PjRfiAttachmentsId.eq(attachmentId)).buildDelete();
        deleteQuery.executeDeleteWithoutDetachingEntities();
    }

    /**
     * Save PjRfi items
     *
     * @param rfis
     */
    public void savePjRfiList(List<PjRfi> rfis) {
        getDaoSession().getPjRfiDao().saveInTx(rfis);
    }

    /**
     * Save PjRfiContactList items
     *
     * @param rfis
     */
    public void savePjRfiContactList(List<PjRfiContactList> rfis) {
        getDaoSession().getPjRfiContactListDao().saveInTx(rfis);
    }

    /**
     * Save PjRfi item
     *
     * @param rfi
     */
    public void updatePjRfi(PjRfi rfi) {
        getDaoSession().getPjRfiDao().update(rfi);
    }

    /**
     * Save PjRfi item
     *
     * @param rfi
     */
    public void updatePjRfiContactListItem(PjRfiContactList rfi) {
        getDaoSession().getPjRfiContactListDao().update(rfi);
    }

    /**
     * Save PjRfiAttachments item
     *
     * @param rfi
     */
    public void updatePjRfiAttachments(PjRfiAttachments rfi) {
        getDaoSession().getPjRfiAttachmentsDao().update(rfi);
    }

    public void updatePjRfiReplies(PjRfiReplies rfi) {
        getDaoSession().getPjRfiRepliesDao().update(rfi);
    }

    /*
    1. Difference in RFI and RFI Reply attachments.
    a) If 'pj_rfi_replies_id' equals 0, then, it is RFI Attachment.
    b) If 'pj_rfi_replies_id' has a valid value, then, match it with 'pj_rfi_replies_id' in 'pj_rfi_replies' table to get the attachments.
     */
    public List<PjRfiAttachments> getRfiAttachmentsList(PjRfi pjRfi) {
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(mContext).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        if (loginResponse != null) {
            List<PjRfiAttachments> attachmentsList = getDaoSession().getPjRfiAttachmentsDao().queryBuilder()
                    .where(PjRfiAttachmentsDao.Properties.PjRfiId.eq(pjRfi.getPjRfiId()),
                            PjRfiAttachmentsDao.Properties.PjRfiRepliesId.eq(0)).list();
            return attachmentsList;
        } else {
            return new ArrayList<>();
        }

    }

    public List<PjRfiAttachments> getRfiReplyAttachmentsList(int pjRfiId, int pjRfiReplyId) {
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(mContext).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        if (loginResponse != null) {
            List<PjRfiAttachments> attachmentsList = getDaoSession().getPjRfiAttachmentsDao().queryBuilder()
                    .where(PjRfiAttachmentsDao.Properties.PjRfiId.eq(pjRfiId),
                            PjRfiAttachmentsDao.Properties.PjRfiRepliesId.eq(pjRfiReplyId)).list();
            return attachmentsList;
        } else {
            return new ArrayList<>();
        }

    }

    public List<PjRfiReplies> getRfiReplyList(int pjRfiId) {
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(mContext).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        if (loginResponse != null) {
            List<PjRfiReplies> attachmentsList = getDaoSession().getPjRfiRepliesDao().queryBuilder()
                    .where(PjRfiRepliesDao.Properties.PjRfiId.eq(pjRfiId)
                            /*,PjRfiRepliesDao.Properties.PjRfiRepliesId.eq(pjRfiReplyId)*/)
                    .orderDesc(PjRfiRepliesDao.Properties.UpdatedAt).list();
            return attachmentsList;
        } else {
            return new ArrayList<>();
        }

    }

    public String getMAXProjectRfiUpdateDate(int projectId) {
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(mContext).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        List<PjRfi> maxPostIdRow = PronovosApplication.getContext().getDaoSession().getPjRfiDao().queryBuilder()
                .where(PjRfiDao.Properties.UpdatedAt.isNotNull(),
                        PjRfiDao.Properties.PjProjectsId.eq(projectId)
                        /* , PjDocumentsFilesDao.Properties.UsersId.eq(loginResponse.getUserDetails().getUsers_id()))*/
                ).orderDesc(PjDocumentsFilesDao.Properties.UpdatedAt).limit(1).list();
        if (maxPostIdRow.size() > 0) {
            Date maxUpdatedAt = maxPostIdRow.get(0).getUpdatedAt();
            return DateFormatter.formatDateTimeForService(maxUpdatedAt);
        }
        return "1990-01-01 01:01:01";
    }

    public String getMAXProjectRfiRepliesUpdateDate(int rfiId) {
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(mContext).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        List<PjRfiReplies> maxPostIdRow = PronovosApplication.getContext().getDaoSession().getPjRfiRepliesDao().queryBuilder()
                .where(PjRfiRepliesDao.Properties.UpdatedAt.isNotNull(),
                        PjRfiRepliesDao.Properties.PjRfiId.eq(rfiId)
                        /* , PjDocumentsFilesDao.Properties.UsersId.eq(loginResponse.getUserDetails().getUsers_id()))*/
                ).orderDesc(PjRfiRepliesDao.Properties.UpdatedAt).limit(1).list();
        if (maxPostIdRow.size() > 0) {
            Date maxUpdatedAt = maxPostIdRow.get(0).getUpdatedAt();
            return DateFormatter.formatDateTimeForService(maxUpdatedAt);
        }
        return "1990-01-01 01:01:01";
    }
}
