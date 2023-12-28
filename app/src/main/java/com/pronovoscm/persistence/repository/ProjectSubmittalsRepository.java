package com.pronovoscm.persistence.repository;

import android.content.Context;

import com.google.gson.Gson;
import com.pronovoscm.chipslayoutmanager.util.log.Log;
import com.pronovoscm.model.SubmittalStatusEnum;
import com.pronovoscm.model.response.login.LoginResponse;
import com.pronovoscm.persistence.domain.DaoSession;
import com.pronovoscm.persistence.domain.PjAssigneeAttachments;
import com.pronovoscm.persistence.domain.PjAssigneeAttachmentsDao;
import com.pronovoscm.persistence.domain.PjSubmittalAttachments;
import com.pronovoscm.persistence.domain.PjSubmittalAttachmentsDao;
import com.pronovoscm.persistence.domain.PjSubmittalContactList;
import com.pronovoscm.persistence.domain.PjSubmittalContactListDao;
import com.pronovoscm.persistence.domain.PjSubmittals;
import com.pronovoscm.persistence.domain.PjSubmittalsDao;
import com.pronovoscm.utils.Constants;
import com.pronovoscm.utils.SharedPref;
import org.greenrobot.greendao.query.DeleteQuery;
import org.greenrobot.greendao.query.QueryBuilder;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProjectSubmittalsRepository extends AbstractRepository {
    private Context mContext;
    private LoginResponse loginResponse;

    public ProjectSubmittalsRepository(DaoSession daoSession, Context context) {
        super(daoSession);
        mContext = context;
    }

     /**
     * Save PjSubmittals items
     *
     * @param submittalsList
     */
    public void saveProjectSubmittalsList(List<PjSubmittals> submittalsList) {
        getDaoSession().getPjSubmittalsDao().saveInTx(submittalsList);
    }

    /**
     * Save PjSubmittals items
     *
     * @param submittals
     */
    public void savePjSubmittal(PjSubmittals submittals) {
        getDaoSession().getPjSubmittalsDao().saveInTx(submittals);
    }

    /**
     * Save PjSubmittals item
     *
     * @param pjSubmittals
     */
    public void updatePjSubmittal(PjSubmittals pjSubmittals) {
        getDaoSession().getPjSubmittalsDao().update(pjSubmittals);
    }

    /**
     * Save PjSubmittals items
     *
     * @param submittals
     */
    public void savePjSubmittalContact(PjSubmittalContactList submittals) {
        getDaoSession().getPjSubmittalContactListDao().saveInTx(submittals);
    }
    /**
     * Save PjAssigneeAttachments items
     *
     * @param pjAssigneeAttachments
     */
    public void savePjSubmittalContact(PjAssigneeAttachments pjAssigneeAttachments) {
        getDaoSession().getPjAssigneeAttachmentsDao().saveInTx(pjAssigneeAttachments);
    }
    /**
     * Save PjSubmittalAttachments items
     *
     * @param submittals
     */
    public void savePjSubmittalAttachments(PjSubmittalAttachments submittals) {
        getDaoSession().getPjSubmittalAttachmentsDao().saveInTx(submittals);
    }
    /**
     * Save PjSubmittalContactList item
     *
     * @param pjSubmittalContactList
     */
    public void updatePjSubmittalContact(PjSubmittalContactList pjSubmittalContactList) {
        getDaoSession().getPjSubmittalContactListDao().update(pjSubmittalContactList);
    }


    /**
     * Save PjAssigneeAttachments item
     *
     * @param pjAssigneeAttachments
     */
    public void updatePjSubmittalAssigneeAtt(PjAssigneeAttachments pjAssigneeAttachments) {
        getDaoSession().getPjAssigneeAttachmentsDao().update(pjAssigneeAttachments);
    }


    public void deletePjSubmittalAttachments(int id) {
        DeleteQuery<PjSubmittalAttachments> deleteQuery = getDaoSession().queryBuilder(PjSubmittalAttachments.class).
                where(PjSubmittalAttachmentsDao.Properties.AttachmentsId.eq(id)).buildDelete();
        deleteQuery.executeDeleteWithoutDetachingEntities();
    }

    public void deletePjContactSubmittal(int id) {
        DeleteQuery<PjSubmittalContactList> deleteQuery = getDaoSession().queryBuilder(PjSubmittalContactList.class).
                where(PjSubmittalContactListDao.Properties.PjSubmittalContactListId.eq(id)).buildDelete();
        deleteQuery.executeDeleteWithoutDetachingEntities();
        // to delete all the assignee attachment
        List<PjAssigneeAttachments> attachmentsList = getDaoSession().getPjAssigneeAttachmentsDao().queryBuilder()
                .where(PjAssigneeAttachmentsDao.Properties.PjSubmittalContactListId.eq(id)).list();
        for (PjAssigneeAttachments pjAssigneeAttachments : attachmentsList) {
            deletePjSubmittalAssigneeAtt(pjAssigneeAttachments.getAttachmentsId());
        }
    }

    public void deletePjCcContactSubmittal(int id) {
        DeleteQuery<PjSubmittalContactList> deleteQuery = getDaoSession().queryBuilder(PjSubmittalContactList.class).
                where(PjSubmittalContactListDao.Properties.PjSubmittalContactListId.eq(id)).buildDelete();
        deleteQuery.executeDeleteWithoutDetachingEntities();
    }

    public void deletePjSubmittals(int id) {
        DeleteQuery<PjSubmittals> deleteQuery = getDaoSession().queryBuilder(PjSubmittals.class).
                where(PjSubmittalsDao.Properties.PjSubmittalsId.eq(id)).buildDelete();
        deleteQuery.executeDeleteWithoutDetachingEntities();
    }

    public void deletePjSubmittalAssigneeAtt(int id) {
        DeleteQuery<PjAssigneeAttachments> deleteQuery = getDaoSession().queryBuilder(PjAssigneeAttachments.class).
                where(PjAssigneeAttachmentsDao.Properties.AttachmentsId.eq(id)).buildDelete();
        deleteQuery.executeDeleteWithoutDetachingEntities();
    }

    public void deleteAllContactPjSubmittals(int id) {
        List<PjSubmittalContactList> pjSubmittalContactLists = getDaoSession().getPjSubmittalContactListDao().queryBuilder()
                .where(PjSubmittalContactListDao.Properties.PjSubmittalsId.eq(id),
                        PjSubmittalContactListDao.Properties.IsCc.eq(false)).list();
        if (pjSubmittalContactLists != null && pjSubmittalContactLists.size() > 0) {
            for (PjSubmittalContactList pjSubmittalContactList : pjSubmittalContactLists) {
                DeleteQuery<PjSubmittalContactList> deleteQuery = getDaoSession().queryBuilder(PjSubmittalContactList.class).
                        where(PjSubmittalContactListDao.Properties.PjSubmittalContactListId.eq(pjSubmittalContactList.getPjSubmittalContactListId())).buildDelete();
                deleteQuery.executeDeleteWithoutDetachingEntities();
                // to delete all the assignee attachment

                List<PjAssigneeAttachments> attachmentsList = getDaoSession().getPjAssigneeAttachmentsDao().queryBuilder()
                        .where(PjAssigneeAttachmentsDao.Properties.PjSubmittalsId.eq(id),
                                PjAssigneeAttachmentsDao.Properties.PjSubmittalContactListId.eq(pjSubmittalContactList.getPjSubmittalContactListId())).list();
                for (PjAssigneeAttachments pjAssigneeAttachments : attachmentsList) {

                    URI uri = null;
                    try {
                        uri = new URI(pjAssigneeAttachments.getAttachPath());
                        String[] segments = uri.getPath().split("/");
                        String imageName = segments[segments.length - 1];
                        String filePath = mContext.getFilesDir().getAbsolutePath() + Constants.SUBMITTALS_ATTACHMENTS_PATH;
                        File imgFile = new File(filePath + "/" + imageName);
                        if (imgFile.exists()) {
                            imgFile.delete();
                        }
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                    DeleteQuery<PjAssigneeAttachments> deleteQuery1 = getDaoSession().queryBuilder(PjAssigneeAttachments.class).
                            where(PjAssigneeAttachmentsDao.Properties.AttachmentsId.eq(pjAssigneeAttachments.getAttachmentsId())).buildDelete();
                    deleteQuery1.executeDeleteWithoutDetachingEntities();
                }
            }
        }
    }
    public void deleteAllCcPjSubmittals(int id) {
        List<PjSubmittalContactList> pjSubmittalContactLists = getDaoSession().getPjSubmittalContactListDao().queryBuilder()
                .where(PjSubmittalContactListDao.Properties.PjSubmittalsId.eq(id),
                        PjSubmittalContactListDao.Properties.IsCc.eq(true)).list();
        if (pjSubmittalContactLists != null && pjSubmittalContactLists.size() > 0) {
            for (PjSubmittalContactList pjSubmittalContactList : pjSubmittalContactLists) {
                DeleteQuery<PjSubmittalContactList> deleteQuery = getDaoSession().queryBuilder(PjSubmittalContactList.class).
                        where(PjSubmittalContactListDao.Properties.PjSubmittalContactListId.eq(pjSubmittalContactList.getPjSubmittalContactListId())).buildDelete();
                deleteQuery.executeDeleteWithoutDetachingEntities();
            }
        }
    }
    public void deleteAllAttachmentPjSubmittals(int id) {
        List<PjSubmittalAttachments> attachmentsList = getDaoSession().getPjSubmittalAttachmentsDao().queryBuilder()
                .where(PjSubmittalAttachmentsDao.Properties.PjSubmittalsId.eq(id)).list();
        if (attachmentsList != null && attachmentsList.size() > 0) {
            for (PjSubmittalAttachments pjSubmittalAttachments : attachmentsList) {
                URI uri = null;
                try {
                    uri = new URI(pjSubmittalAttachments.getAttachPath());
                    String[] segments = uri.getPath().split("/");
                    String imageName = segments[segments.length - 1];
                    String filePath = mContext.getFilesDir().getAbsolutePath() + Constants.SUBMITTALS_ATTACHMENTS_PATH;
                    File imgFile = new File(filePath + "/" + imageName);
                    if (imgFile.exists()) {
                        imgFile.delete();
                    }
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }

                DeleteQuery<PjSubmittalAttachments> deleteQuery = getDaoSession().queryBuilder(PjSubmittalAttachments.class).
                        where(PjSubmittalAttachmentsDao.Properties.AttachmentsId.eq(pjSubmittalAttachments.getAttachmentsId())).buildDelete();
                deleteQuery.executeDeleteWithoutDetachingEntities();
            }
        }
    }
    /**
     * Save PjSubmittalAttachments item
     *
     * @param pjSubmittalAttachments
     */
    public void updatePjSubmittalAttachments(PjSubmittalAttachments pjSubmittalAttachments) {
        getDaoSession().getPjSubmittalAttachmentsDao().update(pjSubmittalAttachments);
    }
    public List<PjSubmittals> getSearchSubmittalFilterList(int projectId, String searchKey, SubmittalStatusEnum submittalStatusEnum) {
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(mContext).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        if (loginResponse != null) {
            PjSubmittalsDao pjSubmittalsDao = getDaoSession().getPjSubmittalsDao();
            List<PjSubmittals> punchlistDbList = new ArrayList<>();

            QueryBuilder<PjSubmittals> pjSubmittalsQueryBuilder = pjSubmittalsDao.queryBuilder().where(
                    PjSubmittalsDao.Properties.PjProjectsId.eq(projectId),
                    PjSubmittalsDao.Properties.DeletedAt.isNull());
            if (submittalStatusEnum.getStatusValue() != -1) {
                pjSubmittalsQueryBuilder.where(PjSubmittalsDao.Properties.SubmittalStatus.eq(submittalStatusEnum.getStatusValue()));
            }
            // in asc order of submittal number
            punchlistDbList = pjSubmittalsQueryBuilder
                    .whereOr(PjSubmittalsDao.Properties.SubmittalTitle.like("%" + searchKey + "%"),
                            PjSubmittalsDao.Properties.SubmittalTitle.like("%" + searchKey + "%")
                    ).orderAsc(PjSubmittalsDao.Properties.DueDate).list(); // complete
//                    ).orderAsc(PjSubmittalsDao.Properties.SubmittalNumber).list(); // complete
            return punchlistDbList;
        } else {
            return new ArrayList<>();
        }
    }

    public List<PjSubmittalContactList> getSearchPjSubmittalCcContact(PjSubmittals pjSubmittals) {
        List<PjSubmittalContactList> pjSubmittalContactLists = getDaoSession().getPjSubmittalContactListDao().queryBuilder()
                .where(PjSubmittalContactListDao.Properties.PjSubmittalsId.eq(pjSubmittals.getPjSubmittalsId()),
                        PjSubmittalContactListDao.Properties.IsCc.eq(true))
                .orderAsc(PjSubmittalContactListDao.Properties.ContactName).list();

        if (pjSubmittalContactLists != null && pjSubmittalContactLists.size() > 0) {
            return pjSubmittalContactLists;
        } else
            return new ArrayList<>();
    }

    public List<PjSubmittalContactList> getPjSubmittalContactList(PjSubmittals pjSubmittals) {
        List<PjSubmittalContactList> pjSubmittalContactLists = getDaoSession().getPjSubmittalContactListDao().queryBuilder()
                .where(PjSubmittalContactListDao.Properties.PjSubmittalsId.eq(pjSubmittals.getPjSubmittalsId()),
                        PjSubmittalContactListDao.Properties.IsCc.eq(false)
                ).orderAsc(PjSubmittalContactListDao.Properties.SortOrder).list();
        if (pjSubmittalContactLists != null && pjSubmittalContactLists.size() > 0) {
            return pjSubmittalContactLists;
        } else
            return new ArrayList<>();
    }
    /*

    RFI Contact List Assigned To and CC field:

    1) If default_type equals 2 then, it is for CC field.
    2) If default_type equals 3 then, it is for Assigned To field.

     */
    public PjSubmittalContactList getSearchPjSubmittalAssignToContact(PjSubmittals submittals) {
        List<PjSubmittalContactList> pjSubmittalContactLists = getDaoSession().getPjSubmittalContactListDao().queryBuilder()
                .where(PjSubmittalContactListDao.Properties.PjSubmittalsId.eq(submittals.getPjSubmittalsId()),
                        PjSubmittalContactListDao.Properties.IsCc.eq(false)).list();
        if (pjSubmittalContactLists != null && pjSubmittalContactLists.size() > 0) {
            for (PjSubmittalContactList pjC : pjSubmittalContactLists) {
                if (pjC.getContactName() != null && !pjC.getContactName().isEmpty()) {
                    return pjC;
                }
            }
            return pjSubmittalContactLists.get(0);
        } else
            return null;
    }


    public int getPjContactListSize(PjSubmittals submittals) {
        List<PjSubmittalContactList> pjSubmittalContactLists = getDaoSession().getPjSubmittalContactListDao().queryBuilder()
                .where(PjSubmittalContactListDao.Properties.PjSubmittalsId.eq(submittals.getPjSubmittalsId()),
                        PjSubmittalContactListDao.Properties.IsCc.eq(false)).list();
        if (pjSubmittalContactLists != null && pjSubmittalContactLists.size() > 0) {
            return pjSubmittalContactLists.size();
        } else
            return 0;

    }


    public List<PjSubmittalAttachments> getPjSubmittalAttachments(PjSubmittals pjSubmittals) {
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(mContext).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        if (loginResponse != null) {
            List<PjSubmittalAttachments> attachmentsList = getDaoSession().getPjSubmittalAttachmentsDao().queryBuilder()
                    .where(PjSubmittalAttachmentsDao.Properties.PjSubmittalsId.eq(pjSubmittals.getPjSubmittalsId())).list();
            return attachmentsList;
        } else {
            return new ArrayList<>();
        }

    }

    public List<PjAssigneeAttachments> getPjAssigneeAttachments(int submittalId, int contactId) {
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(mContext).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        if (loginResponse != null) {
            List<PjAssigneeAttachments> attachmentsList = getDaoSession().getPjAssigneeAttachmentsDao().queryBuilder()
                    .where(PjAssigneeAttachmentsDao.Properties.PjSubmittalsId.eq(submittalId),
                            PjAssigneeAttachmentsDao.Properties.PjSubmittalContactListId.eq(contactId)).list();
            return attachmentsList;
        } else {
            return new ArrayList<>();
        }

    }



}
