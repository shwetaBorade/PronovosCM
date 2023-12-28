package com.pronovoscm.persistence.repository;

import android.content.Context;

import com.google.gson.Gson;
import com.pronovoscm.model.response.login.LoginResponse;
import com.pronovoscm.model.response.projectinfo.ProjectOverviewInfoData;
import com.pronovoscm.model.response.projectsubcontractors.SubcontractorData;
import com.pronovoscm.model.response.projectteam.TeamData;
import com.pronovoscm.model.response.resources.ResourceData;
import com.pronovoscm.persistence.domain.DaoSession;
import com.pronovoscm.persistence.domain.PjProjectsInfo;
import com.pronovoscm.persistence.domain.PjProjectsInfoDao;
import com.pronovoscm.persistence.domain.PjProjectsResources;
import com.pronovoscm.persistence.domain.PjProjectsResourcesDao;
import com.pronovoscm.persistence.domain.PjProjectsSubcontractors;
import com.pronovoscm.persistence.domain.PjProjectsSubcontractorsDao;
import com.pronovoscm.persistence.domain.PjProjectsTeam;
import com.pronovoscm.persistence.domain.PjProjectsTeamDao;

import java.util.List;
import java.util.concurrent.Callable;

public class ProjectOverviewRepository extends AbstractRepository {
    private Context context;
    private LoginResponse loginResponse;

    public ProjectOverviewRepository(DaoSession daoSession, Context context) {
        super(daoSession);
        this.context = context;
    }

    /*
        public Info doUpdateProjectOverviewInfo(int users_id, String infoJson, int projectId) {

            try {
                getDaoSession().callInTx(new Callable<Info>() {
                    @Override
                    public Info call() {
                        List<PjProjectsInfo> pjProjectsInfos = getDaoSession().getPjProjectsInfoDao().queryBuilder().where(
                                PjProjectsInfoDao.Properties.PjProjectsId.eq(projectId),
                                PjProjectsInfoDao.Properties.UsersId.eq(users_id)).limit(1).list();
                        PjProjectsInfo pjProjectsInfo = new PjProjectsInfo();
                        if (pjProjectsInfos.size() > 0) {
                            pjProjectsInfo = pjProjectsInfos.get(0);
                        }

                        pjProjectsInfo.setUsersId(users_id);
                        pjProjectsInfo.setPjProjectsId(projectId);
                        pjProjectsInfo.setProjectsInfo(infoJson);
                        getDaoSession().insertOrReplace(pjProjectsInfo);
                        return getProjectInfo(users_id, projectId);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
            return getProjectInfo(users_id, projectId);
        }*/
    public ProjectOverviewInfoData doUpdateDynamicProjectOverviewInfo(int users_id, String infoJson, int projectId) {

        try {
            getDaoSession().callInTx(new Callable<ProjectOverviewInfoData>() {
                @Override
                public ProjectOverviewInfoData call() {
                    List<PjProjectsInfo> pjProjectsInfos = getDaoSession().getPjProjectsInfoDao().queryBuilder().where(
                            PjProjectsInfoDao.Properties.PjProjectsId.eq(projectId),
                            PjProjectsInfoDao.Properties.UsersId.eq(users_id)).limit(1).list();
                    PjProjectsInfo pjProjectsInfo = new PjProjectsInfo();
                    if (pjProjectsInfos.size() > 0) {
                        pjProjectsInfo = pjProjectsInfos.get(0);
                    }

                    pjProjectsInfo.setUsersId(users_id);
                    pjProjectsInfo.setPjProjectsId(projectId);
                    pjProjectsInfo.setProjectsInfo(infoJson);
                    getDaoSession().insertOrReplace(pjProjectsInfo);
                    return getDynamicProjectInfo(users_id, projectId);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return getDynamicProjectInfo(users_id, projectId);
    }

    /*  public Info getProjectInfo(int userId, int projectId) {
          List<PjProjectsInfo> pjProjectsInfos = getDaoSession().getPjProjectsInfoDao().queryBuilder().where(
                  PjProjectsInfoDao.Properties.PjProjectsId.eq(projectId),
                  PjProjectsInfoDao.Properties.UsersId.eq(userId)).limit(1).list();
          if (pjProjectsInfos.size() > 0) {
              return (new Gson().fromJson(pjProjectsInfos.get(0).getProjectsInfo(), Info.class));
          } else {
              return null;
          }
      }*/
    public ProjectOverviewInfoData getDynamicProjectInfo(int userId, int projectId) {
        List<PjProjectsInfo> pjProjectsInfos = getDaoSession().getPjProjectsInfoDao().queryBuilder().where(
                PjProjectsInfoDao.Properties.PjProjectsId.eq(projectId),
                PjProjectsInfoDao.Properties.UsersId.eq(userId)).limit(1).list();
        if (pjProjectsInfos.size() > 0) {
            return (new Gson().fromJson(pjProjectsInfos.get(0).getProjectsInfo(), ProjectOverviewInfoData.class));
        } else {
            return null;
        }
    }

    public ResourceData doUpdateProjectOverviewResources(int users_id, String ResourcesJson, int projectId) {

        try {
            getDaoSession().callInTx(new Callable<ResourceData>() {
                @Override
                public ResourceData call() {
                    List<PjProjectsResources> pjProjectsResourcess = getDaoSession().getPjProjectsResourcesDao().queryBuilder().where(
                            PjProjectsResourcesDao.Properties.PjProjectsId.eq(projectId),
                            PjProjectsResourcesDao.Properties.UsersId.eq(users_id)).limit(1).list();
                    PjProjectsResources pjProjectsResources = new PjProjectsResources();
                    if (pjProjectsResourcess.size() > 0) {
                        pjProjectsResources = pjProjectsResourcess.get(0);
                    }

                    pjProjectsResources.setUsersId(users_id);
                    pjProjectsResources.setPjProjectsId(projectId);
                    pjProjectsResources.setProjectsResources(ResourcesJson);
                    getDaoSession().insertOrReplace(pjProjectsResources);
                    return getProjectResources(users_id, projectId);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return getProjectResources(users_id,projectId);
    }

    public ResourceData getProjectResources(int userId, int projectId) {
        List<PjProjectsResources> pjProjectsResourcess = getDaoSession().getPjProjectsResourcesDao().queryBuilder().where(
                PjProjectsResourcesDao.Properties.PjProjectsId.eq(projectId),
                PjProjectsResourcesDao.Properties.UsersId.eq(userId)).limit(1).list();
        if (pjProjectsResourcess.size() > 0) {
            return (new Gson().fromJson(pjProjectsResourcess.get(0).getProjectsResources(), ResourceData.class));

        } else {
            return null;
        }
    }

    public TeamData doUpdateProjectOverviewTeam(int users_id, String teamJson, int projectId) {

        try {
            getDaoSession().callInTx(new Callable<TeamData>() {
                @Override
                public TeamData call() {
                    List<PjProjectsTeam> pjProjectsTeams = getDaoSession().getPjProjectsTeamDao().queryBuilder().where(
                            PjProjectsTeamDao.Properties.PjProjectsId.eq(projectId),
                            PjProjectsTeamDao.Properties.UsersId.eq(users_id)).limit(1).list();
                    PjProjectsTeam pjProjectsTeam = new PjProjectsTeam();
                    if (pjProjectsTeams.size() > 0) {
                        pjProjectsTeam = pjProjectsTeams.get(0);
                    }

                    pjProjectsTeam.setUsersId(users_id);
                    pjProjectsTeam.setPjProjectsId(projectId);
                    pjProjectsTeam.setProjectTeam(teamJson);
                    getDaoSession().insertOrReplace(pjProjectsTeam);

                    return getProjectTeam(users_id, projectId);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return getProjectTeam(users_id, projectId);
    }

    public TeamData getProjectTeam(int userId, int projectId) {
        List<PjProjectsTeam> pjProjectsTeams = getDaoSession().getPjProjectsTeamDao().queryBuilder().where(
                PjProjectsTeamDao.Properties.PjProjectsId.eq(projectId),
                PjProjectsTeamDao.Properties.UsersId.eq(userId)).limit(1).list();
        if (pjProjectsTeams.size() > 0) {
            return (new Gson().fromJson(pjProjectsTeams.get(0).getProjectTeam(), TeamData.class));
        } else {
            return null;
        }
    }

    public SubcontractorData doUpdateProjectOverviewSubcontractors(int users_id, String SubcontractorsJson, int projectId) {

        try {
            getDaoSession().callInTx(new Callable<SubcontractorData>() {
                @Override
                public SubcontractorData call() {
                    List<PjProjectsSubcontractors> pjProjectsSubcontractors = getDaoSession().getPjProjectsSubcontractorsDao().queryBuilder().where(
                            PjProjectsSubcontractorsDao.Properties.PjProjectsId.eq(projectId),
                            PjProjectsSubcontractorsDao.Properties.UsersId.eq(users_id)).limit(1).list();
                    PjProjectsSubcontractors pjProjectsSubcontractor = new PjProjectsSubcontractors();
                    if (pjProjectsSubcontractors.size() > 0) {
                        pjProjectsSubcontractor = pjProjectsSubcontractors.get(0);
                    }

                    pjProjectsSubcontractor.setUsersId(users_id);
                    pjProjectsSubcontractor.setPjProjectsId(projectId);
                    pjProjectsSubcontractor.setProjectsSubcontractors(SubcontractorsJson);
                    getDaoSession().insertOrReplace(pjProjectsSubcontractor);
                    return getProjectSubcontractors(users_id, projectId);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return getProjectSubcontractors(users_id, projectId);
    }

    public SubcontractorData getProjectSubcontractors(int userId, int projectId) {
        List<PjProjectsSubcontractors> pjProjectsSubcontractorss = getDaoSession().getPjProjectsSubcontractorsDao().queryBuilder().where(
                PjProjectsSubcontractorsDao.Properties.PjProjectsId.eq(projectId),
                PjProjectsSubcontractorsDao.Properties.UsersId.eq(userId)).limit(1).list();
        if (pjProjectsSubcontractorss.size() > 0) {
            return (new Gson().fromJson(pjProjectsSubcontractorss.get(0).getProjectsSubcontractors(), SubcontractorData.class));
        } else {
            return null;
        }
    }

}
