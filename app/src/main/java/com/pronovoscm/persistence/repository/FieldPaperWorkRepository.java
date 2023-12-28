package com.pronovoscm.persistence.repository;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.pronovoscm.model.response.assignee.AssigneeList;
import com.pronovoscm.model.response.companylist.Companies;
import com.pronovoscm.model.response.login.LoginResponse;
import com.pronovoscm.persistence.domain.CompanyList;
import com.pronovoscm.persistence.domain.CompanyListDao;
import com.pronovoscm.persistence.domain.DaoSession;
import com.pronovoscm.persistence.domain.PunchlistAssignee;
import com.pronovoscm.persistence.domain.PunchlistAssigneeDao;
import com.pronovoscm.persistence.domain.Trades;
import com.pronovoscm.persistence.domain.TradesDao;
import com.pronovoscm.utils.DateFormatter;
import com.pronovoscm.utils.SharedPref;

import org.greenrobot.greendao.query.DeleteQuery;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class FieldPaperWorkRepository extends AbstractRepository {
    private Context context;
    private LoginResponse loginResponse;

    public FieldPaperWorkRepository(DaoSession daoSession, Context context) {
        super(daoSession);
        this.context = context;
    }

    /**
     * Insert or update Punchlist Assignees
     *
     * @param punchlistAssignees
     * @param projectId
     * @param users_id
     * @return
     */
    public List<PunchlistAssignee> doUpdateAssigneeTable(List<AssigneeList> punchlistAssignees, int projectId, int users_id) {

//        loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
//        if (loginResponse != null) {
            try {
                DeleteQuery<PunchlistAssignee> assigneeDeleteQuery = getDaoSession().queryBuilder(PunchlistAssignee.class)
                        .where(PunchlistAssigneeDao.Properties.PjProjectsId.eq(projectId),
                                PunchlistAssigneeDao.Properties.UserId.eq(users_id))
                        .buildDelete();
                assigneeDeleteQuery.executeDeleteWithoutDetachingEntities();

                getDaoSession().callInTx(new Callable<List<AssigneeList>>() {
                    PunchlistAssigneeDao mPunchlistAssigneeDao = getDaoSession().getPunchlistAssigneeDao();

                    @Override
                    public List<AssigneeList> call() throws Exception {
                        for (AssigneeList punchlistAssignee : punchlistAssignees) {
                            PunchlistAssignee mPunchlistAssignee = new PunchlistAssignee();
                            mPunchlistAssignee.setActive(punchlistAssignee.getActive() == 1);
                            mPunchlistAssignee.setUsersId(punchlistAssignee.getUsersId());
                            mPunchlistAssignee.setUserId(users_id);
                            mPunchlistAssignee.setName(punchlistAssignee.getName());
                            mPunchlistAssignee.setName(punchlistAssignee.getName());
                            mPunchlistAssignee.setPjProjectsId(projectId);

                            mPunchlistAssignee.setDefaultAssignee(punchlistAssignee.isDefaultAssignee());
                            mPunchlistAssignee.setDefaultCC(punchlistAssignee.isDefaultCC());

                            mPunchlistAssigneeDao.save(mPunchlistAssignee);

                        }
                        return punchlistAssignees;
                    }

                });
            } catch (Exception e) {
                e.printStackTrace();
            }
//        }
        return getAssignee(projectId);
    }

    /**
     * Insert or update Trades
     *
     * @param tradesList
     * @param userId
     * @param loginResponse
     * @return
     */
    public List<Trades> doUpdateTrades(List<com.pronovoscm.model.response.trades.Trades> tradesList, int userId, LoginResponse loginResponse) {

//        loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        try {
            DeleteQuery<Trades> tradesDeleteQuery = getDaoSession().queryBuilder(Trades.class)
                    .where(TradesDao.Properties.UsersId.eq(userId))
                    .buildDelete();
            tradesDeleteQuery.executeDeleteWithoutDetachingEntities();

            getDaoSession().callInTx(new Callable<List<com.pronovoscm.model.response.trades.Trades>>() {
                TradesDao mTradesDao = getDaoSession().getTradesDao();

                @Override
                public List<com.pronovoscm.model.response.trades.Trades> call() throws Exception {
                    for (com.pronovoscm.model.response.trades.Trades trades1 : tradesList) {


                        Trades trades = new Trades();
                        trades.setUsersId(userId);
                        trades.setName(trades1.getName());
                        trades.setCreatedAt(trades1.getCreatedAt() != null && !trades1.getCreatedAt().equals("") ? DateFormatter.getDateFromDateTimeString(trades1.getCreatedAt()) : null);
                        trades.setUpdatedAt(trades1.getUpdatedAt() != null && !trades1.getUpdatedAt().equals("") ? DateFormatter.getDateFromDateTimeString(trades1.getUpdatedAt()) : null);
                        trades.setDeletedAt(trades1.getDeletedAt() != null && !trades1.getDeletedAt().equals("") ? DateFormatter.getDateFromDateTimeString(trades1.getDeletedAt()) : null);
                        trades.setTenantId(trades1.getTenantId());
                        trades.setTradesId(trades1.getTradesId());

                        mTradesDao.save(trades);

                    }
                    return tradesList;
                }

            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return getTrades(loginResponse);
    }

    /**
     * Insert or update Company
     *
     * @param companiesList
     * @param projectId
     * @param users_id
     * @return
     */
    public List<CompanyList> doUpdateCompanyList(List<Companies> companiesList, int projectId, int users_id) {

//        loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
//        if (users_id!=null) {
            try {
                DeleteQuery<CompanyList> companyListDeleteQuery = getDaoSession().queryBuilder(CompanyList.class)
                        .where(CompanyListDao.Properties.PjProjectsId.eq(projectId),
                                CompanyListDao.Properties.UsersId.eq(users_id))
                        .buildDelete();
                companyListDeleteQuery.executeDeleteWithoutDetachingEntities();

                getDaoSession().callInTx(new Callable<List<Companies>>() {
                    CompanyListDao mCompanyListDao = getDaoSession().getCompanyListDao();

                    @Override
                    public List<Companies> call() throws Exception {
                        for (Companies companies : companiesList) {


                            CompanyList companyList = new CompanyList();
                            companyList.setUsersId(users_id);
                            companyList.setName(companies.getName());
                            companyList.setCompanyId(companies.getCompanyId());
                            companyList.setSelected(!TextUtils.isEmpty(companies.getSelected()));
                            companyList.setType(companies.getType());
                            companyList.setPjProjectsId(projectId);
                            companyList.setIsDeleted(companies.getIsDeleted());

                            mCompanyListDao.save(companyList);

                        }
                        return companiesList;
                    }

                });
            } catch (Exception e) {
                e.printStackTrace();
//            }
        }
        return getCompanyList(projectId);
    }

    /**
     * List of Punch list assignees from Database
     *
     * @return
     */
    public List<PunchlistAssignee> getAssignee(int projectId) {
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        if (loginResponse != null) {
            List<PunchlistAssignee> punchlistAssignees = getDaoSession().getPunchlistAssigneeDao().queryBuilder().where(
                    PunchlistAssigneeDao.Properties.Active.eq(true),
                    PunchlistAssigneeDao.Properties.PjProjectsId.eq(projectId),
                    PunchlistAssigneeDao.Properties.UserId.eq(loginResponse.getUserDetails().getUsers_id()))
                    .orderAsc(PunchlistAssigneeDao.Properties.Name).list();
            return punchlistAssignees;
        }
        return new ArrayList<>();
    }

    /**
     * List of Punch list assignees from Database for all (active and de-active)
     *
     * @return
     */
    public List<PunchlistAssignee> getDeActiveAssignee(int projectId) {
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        if (loginResponse != null) {
            List<PunchlistAssignee> punchlistAssignees = getDaoSession().getPunchlistAssigneeDao().queryBuilder().where(
                            PunchlistAssigneeDao.Properties.Active.eq(false),
                            PunchlistAssigneeDao.Properties.PjProjectsId.eq(projectId),
                            PunchlistAssigneeDao.Properties.UserId.eq(loginResponse.getUserDetails().getUsers_id()))
                    .orderAsc(PunchlistAssigneeDao.Properties.Name).list();
            return punchlistAssignees;
        }
        return new ArrayList<>();
    }

    public PunchlistAssignee getDeAcAssignee(int projectId,int userId) {
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        if (loginResponse != null) {
            List<PunchlistAssignee> punchlistAssignees = getDaoSession().getPunchlistAssigneeDao().queryBuilder().where(
                            PunchlistAssigneeDao.Properties.UsersId.eq(userId),
                            PunchlistAssigneeDao.Properties.PjProjectsId.eq(projectId),
                            PunchlistAssigneeDao.Properties.UserId.eq(loginResponse.getUserDetails().getUsers_id()))
                    .orderAsc(PunchlistAssigneeDao.Properties.Name).limit(1).list();
            if (punchlistAssignees.size()>0){
                return punchlistAssignees.get(0);
            }else {
                return null;
            }
        }
        return null;
    }

    /**
     * List of Punch list assignees
     *
     * @return
     */
    public PunchlistAssignee getAssignee(int projectId,int userId) {
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        if (loginResponse != null) {
            List<PunchlistAssignee> punchlistAssignees = getDaoSession().getPunchlistAssigneeDao().queryBuilder().where(
                    PunchlistAssigneeDao.Properties.Active.eq(true),
                    PunchlistAssigneeDao.Properties.UsersId.eq(userId),
                    PunchlistAssigneeDao.Properties.PjProjectsId.eq(projectId),
                    PunchlistAssigneeDao.Properties.UserId.eq(loginResponse.getUserDetails().getUsers_id()))
                    .orderAsc(PunchlistAssigneeDao.Properties.Name).limit(1).list();
            if (punchlistAssignees.size()>0){
                return punchlistAssignees.get(0);
            }else {
                return null;
            }
        }
        return null;
    }

    /**
     * Get list of trades
     *
     * @return
     */
    public List<Trades> getTrades(LoginResponse loginResponse) {
        if (loginResponse != null) {
            List<Trades> trades = getDaoSession().getTradesDao().queryBuilder().where(TradesDao.Properties.TenantId.eq(loginResponse.getUserDetails().getTenantId()),
                    TradesDao.Properties.UsersId.eq(loginResponse.getUserDetails().getUsers_id())).orderAsc(TradesDao.Properties.Name).list();
            return trades;
        } else {
            return new ArrayList<>();
        }

    }

    /**
     * Get list of Companies as per project
     *
     * @param projectId
     * @return
     */
    public List<CompanyList> getCompanyList(int projectId) {
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        if (loginResponse != null) {
            List<CompanyList> companyList = getDaoSession().getCompanyListDao().queryBuilder().where(CompanyListDao.Properties.PjProjectsId.eq(projectId), CompanyListDao.Properties.UsersId.eq(loginResponse.getUserDetails().getUsers_id())).orderAsc(CompanyListDao.Properties.Name).list();
            return companyList;
        } else {
            return new ArrayList<>();
        }
    }
}
