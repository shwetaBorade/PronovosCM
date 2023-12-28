package com.pronovoscm.persistence.repository;

import android.content.Context;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;

import com.pronovoscm.PronovosApplication;
import com.pronovoscm.model.SyncDataEnum;
import com.pronovoscm.model.TransactionModuleEnum;
import com.pronovoscm.model.response.cssjs.FormAsset;
import com.pronovoscm.model.response.formarea.ProjectAreas;
import com.pronovoscm.model.response.formcategory.Categories;
import com.pronovoscm.model.response.formpermission.FormPermissionResponseData;
import com.pronovoscm.model.response.formpermission.FormPermissions;
import com.pronovoscm.model.response.forms.FormRevision;
import com.pronovoscm.model.response.forms.ProjectForms;
import com.pronovoscm.model.response.formscheduleresponse.ScheduledForms;
import com.pronovoscm.model.response.formuser.DeletedAttachments;
import com.pronovoscm.model.response.formuser.UserForm;
import com.pronovoscm.model.response.login.LoginResponse;
import com.pronovoscm.model.response.projectformcomponent.FormComponent;
import com.pronovoscm.persistence.domain.DaoSession;
import com.pronovoscm.persistence.domain.FormAssets;
import com.pronovoscm.persistence.domain.FormAssetsDao;
import com.pronovoscm.persistence.domain.FormCategory;
import com.pronovoscm.persistence.domain.FormCategoryDao;
import com.pronovoscm.persistence.domain.FormImage;
import com.pronovoscm.persistence.domain.FormImageDao;
import com.pronovoscm.persistence.domain.Forms;
import com.pronovoscm.persistence.domain.FormsComponent;
import com.pronovoscm.persistence.domain.FormsComponentDao;
import com.pronovoscm.persistence.domain.FormsDao;
import com.pronovoscm.persistence.domain.FormsName;
import com.pronovoscm.persistence.domain.FormsNameDao;
import com.pronovoscm.persistence.domain.FormsPermission;
import com.pronovoscm.persistence.domain.FormsPermissionDao;
import com.pronovoscm.persistence.domain.FormsSchedule;
import com.pronovoscm.persistence.domain.FormsScheduleDao;
import com.pronovoscm.persistence.domain.ProjectForm;
import com.pronovoscm.persistence.domain.ProjectFormArea;
import com.pronovoscm.persistence.domain.ProjectFormAreaDao;
import com.pronovoscm.persistence.domain.ProjectFormDao;
import com.pronovoscm.persistence.domain.TransactionLogMobile;
import com.pronovoscm.persistence.domain.TransactionLogMobileDao;
import com.pronovoscm.persistence.domain.UserForms;
import com.pronovoscm.persistence.domain.UserFormsDao;
import com.pronovoscm.utils.DateFormatter;

import org.dmfs.rfc5545.DateTime;
import org.dmfs.rfc5545.recur.InvalidRecurrenceRuleException;
import org.dmfs.rfc5545.recur.RecurrenceRule;
import org.dmfs.rfc5545.recur.RecurrenceRuleIterator;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.greendao.async.AsyncOperation;
import org.greenrobot.greendao.async.AsyncOperationListener;
import org.greenrobot.greendao.async.AsyncSession;
import org.greenrobot.greendao.query.DeleteQuery;
import org.greenrobot.greendao.query.Join;
import org.greenrobot.greendao.query.Query;
import org.greenrobot.greendao.query.QueryBuilder;
import org.greenrobot.greendao.query.WhereCondition;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.Callable;

public class ProjectFormRepository extends AbstractRepository {

    static final String DATEFORMAT = "yyyy-MM-dd HH:mm:ss";
    private Context context;
//    private LoginResponse loginResponse;

    public ProjectFormRepository(DaoSession daoSession, Context context) {
        super(daoSession);
        this.context = context;
    }

    public static Date getUTCDateTimeAsDate() {
        //note: doesn't check for null
        return StringDateToDate(GetUTCDateTimeAsString());
    }

    public static String GetUTCDateTimeAsString() {
        final SimpleDateFormat sdf = new SimpleDateFormat(DATEFORMAT);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        final String utcTime = sdf.format(new Date());

        return utcTime;
    }

    public static Date StringDateToDate(String StrDate) {
        Date dateToReturn = null;
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATEFORMAT);

        try {
            dateToReturn = (Date) dateFormat.parse(StrDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return dateToReturn;
    }

    public List<Forms> insertProjectForm(int tenantId, int users_id, List<ProjectForms> projectForms, int projectId) {
        try {
            Log.e("ProjectForm", "   insertProjectForm  call: forms start ");
            getDaoSession().callInTx(new Callable<List<Forms>>() {
                final FormsDao formsDao = getDaoSession().getFormsDao();
                final ProjectFormDao projectFormDao = getDaoSession().getProjectFormDao();

                @Override
                public List<Forms> call() {
                    for (int i = 0; i < projectForms.size(); i++) {

                        ProjectForms forms = projectForms.get(i);
                        Log.e("ProjectForm", "   insertProjectForm  call: forms " + forms.toString());
                        List<ProjectForm> projectFormList = getDaoSession().getProjectFormDao().queryBuilder()
                                .where(ProjectFormDao.Properties.FormsId.eq(forms.getFormId()),
                                        ProjectFormDao.Properties.PjProjectsId.eq(projectId)).limit(1).list();


                        ProjectForm projectForm = new ProjectForm();
                        if (projectFormList.size() > 0) {
                            projectForm = projectFormList.get(0);
                        }
                        projectForm.setPjProjectsId(projectId);
                        projectForm.setDeletedAt(forms.getDeletedAt() != null && !forms.getDeletedAt().equals("") ? DateFormatter.getDateFromDateTimeString(forms.getDeletedAt()) : null);
                        projectForm.setFormsId(forms.getOriginalFormsId());
                        projectForm.setFormLastUpdatedDate(forms.getUpdatedAt() != null && !forms.getUpdatedAt().equals("") ? DateFormatter.getDateFromDateTimeString(forms.getUpdatedAt()) : null);

                        projectFormDao.insertOrReplace(projectForm);
                        insertProjectFormsForFormId(forms, projectId);
                        List<FormRevision> revisionList = forms.getFormRevisionList();
                        insertFormNames(revisionList, projectId);
                        // new logic implemented for setting active revision 0 to all existing forms for original form id
                        com.pronovoscm.persistence.domain.Forms forms1 = new com.pronovoscm.persistence.domain.Forms();
                        List<Forms> formList = getDaoSession().getFormsDao().queryBuilder()
                                .where(FormsDao.Properties.FormsId.eq(forms.getFormId())).limit(1).list();
                        if (formList.size() > 0) {
                            forms1 = formList.get(0);
                        }
                        forms1.setFormCategoriesId(forms.getFormCategoriesId());
                        forms1.setFormPrefix(forms.getFormPrefix());
                        forms1.setFormsId(forms.getFormId());
                        forms1.setFormName(forms.getFormName());
                        forms1.setPublish(forms.getPublish());
                        forms1.setTenantId(tenantId);
                        forms1.setCreatedAt(forms.getCreatedAt() != null && !forms.getCreatedAt().equals("") ? DateFormatter.getDateFromDateTimeString(forms.getCreatedAt()) : null);
                        forms1.setUpdatedAt(forms.getUpdatedAt() != null && !forms.getUpdatedAt().equals("") ? DateFormatter.getDateFromDateTimeString(forms.getUpdatedAt()) : null);
                        forms1.setFormDeletedAt(forms.getFormDeletedAt() != null && !forms.getFormDeletedAt().equals("") ? DateFormatter.getDateFromDateTimeString(forms.getFormDeletedAt()) : null);
                        forms1.formSections = forms.getFormSections();
                        forms1.setActiveRevision(forms.getActiveRevision());
                        forms1.setRevisionNumber(forms.getRevisionNumber());
                        forms1.setDefaultValues(forms.getDefaultValues());
                        forms1.setOriginalFormsId(forms.getOriginalFormsId());

                        formsDao.insertOrReplace(forms1);


                    }

                    return getProjectForm(projectId, "", users_id);
                }

            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return getProjectForm(projectId, "", users_id);
    }

    public Forms insertUpdateProjectForm(int tenantId, int userId, ProjectForms projectFormResponse, int projectId) {
        Log.d("ProjectForm", "doUpdateProjectForm: ");
        try {
            getDaoSession().callInTx(new Callable<Forms>() {
                final FormsDao formsDao = getDaoSession().getFormsDao();
                final ProjectFormDao projectFormDao = getDaoSession().getProjectFormDao();

                @Override
                public Forms call() {
                    ProjectForm projectForm = new ProjectForm();

                    projectForm.setPjProjectsId(projectId);
                    projectForm.setDeletedAt(projectFormResponse.getDeletedAt() != null && !projectFormResponse.getDeletedAt().equals("") ?
                            DateFormatter.getDateFromDateTimeString(projectFormResponse.getDeletedAt()) : null);
                    projectForm.setFormsId(projectFormResponse.getFormId());
                    projectForm.setFormLastUpdatedDate(projectFormResponse.getUpdatedAt() != null && !projectFormResponse.getUpdatedAt().equals("") ?
                            DateFormatter.getDateFromDateTimeString(projectFormResponse.getUpdatedAt()) : null);

                    projectFormDao.insertOrReplace(projectForm);
                    List<FormRevision> revisionList = projectFormResponse.getFormRevisionList();

                    if (revisionList != null && revisionList.size() > 0) {
                        insertFormNames(revisionList, projectId);
                    } else {
                        insertFormNames(projectFormResponse.getOriginalFormsId(), projectFormResponse.getFormName(), projectFormResponse.getRevisionNumber(), projectId, projectForm.getFormsId());
                    }
                    //insertProjectFormsForFormId(projectFormResponse, projectId);
                    Forms forms1 = new com.pronovoscm.persistence.domain.Forms();
                    forms1.setFormCategoriesId(projectFormResponse.getFormCategoriesId());
                    forms1.setFormPrefix(projectFormResponse.getFormPrefix());
                    forms1.setFormsId(projectFormResponse.getFormId());
                    forms1.setFormName(projectFormResponse.getFormName());
                    forms1.setPublish(projectFormResponse.getPublish());
                    forms1.setTenantId(tenantId);
                    forms1.setCreatedAt(projectFormResponse.getCreatedAt() != null && !projectFormResponse.getCreatedAt().equals("") ?
                            DateFormatter.getDateFromDateTimeString(projectFormResponse.getCreatedAt()) : null);
                    forms1.setUpdatedAt(projectFormResponse.getUpdatedAt() != null && !projectFormResponse.getUpdatedAt().equals("") ?
                            DateFormatter.getDateFromDateTimeString(projectFormResponse.getUpdatedAt()) : null);
                    forms1.setFormDeletedAt(projectFormResponse.getFormDeletedAt() != null && !projectFormResponse.getFormDeletedAt().equals("")
                            ? DateFormatter.getDateFromDateTimeString(projectFormResponse.getFormDeletedAt()) : null);
                    forms1.formSections = projectFormResponse.getFormSections();
                    forms1.setActiveRevision(projectFormResponse.getActiveRevision());
                    forms1.setRevisionNumber(projectFormResponse.getRevisionNumber());
                    forms1.setDefaultValues(projectFormResponse.getDefaultValues());
                    forms1.setOriginalFormsId(projectFormResponse.getOriginalFormsId());

                    formsDao.insertOrReplace(forms1);
                    return getActualForm(projectFormResponse.getOriginalFormsId(), projectFormResponse.getRevisionNumber());
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
        return getActualForm(projectFormResponse.getOriginalFormsId(), projectFormResponse.getRevisionNumber());
    }

    public ProjectFormArea doUpdateProjectArea(Long projectId, ProjectAreas projectAreas) {
        Log.d("ProjectForm", "doUpdateProjectArea: ");
        try {

            insertProjectArea(projectId, projectAreas);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return getProjectFormArea(projectId);
    }

    private void insertProjectArea(Long projectId, ProjectAreas projectAreas) {
        final ProjectFormAreaDao projectFormAreaDao = getDaoSession().getProjectFormAreaDao();
        ProjectFormArea area = getProjectFormArea(projectId);
        if (area == null)
            area = new ProjectFormArea();
        area.setPjProjectsId(projectId);
        area.setUpdatedAt(projectAreas.getUpdatedAt() != null && !projectAreas.getUpdatedAt().equals("") ? DateFormatter.getDateFromDateTimeString(projectAreas.getUpdatedAt()) : null);
        area.setProjectFormAreas(projectAreas.getData());
        projectFormAreaDao.insertOrReplace(area);
    }

    public ProjectFormArea getProjectFormArea(Long projectId) {
        final ProjectFormAreaDao projectFormAreaDao = getDaoSession().getProjectFormAreaDao();
        List<ProjectFormArea> maxPostIdRow = projectFormAreaDao.queryBuilder().where(
                ProjectFormAreaDao.Properties.PjProjectsId.eq(projectId)).limit(1).list();
        if (maxPostIdRow.size() > 0) {
            return maxPostIdRow.get(0);
        }
        return null;
    }

    public List<Forms> doUpdateProjectForm(int tenantId, int users_id, List<ProjectForms> projectForms, int projectId) {
        Log.d("ProjectForm", "doUpdateProjectForm: ");
        try {
            getDaoSession().callInTx(new Callable<List<Forms>>() {
                final FormsDao formsDao = getDaoSession().getFormsDao();
                final ProjectFormDao projectFormDao = getDaoSession().getProjectFormDao();

                @Override
                public List<Forms> call() {
                    for (int i = 0; i < projectForms.size(); i++) {
                        ProjectForms forms = projectForms.get(i);
                        List<ProjectForm> projectFormList = getDaoSession().getProjectFormDao().queryBuilder()
                                .where(ProjectFormDao.Properties.FormsId.eq(forms.getFormId()),
                                        ProjectFormDao.Properties.PjProjectsId.eq(projectId)).limit(1).list();
                        ProjectForm projectForm = new ProjectForm();
                        if (projectFormList.size() > 0) {
                            projectForm = projectFormList.get(0);
                        }
                        projectForm.setPjProjectsId(projectId);
                        projectForm.setDeletedAt(forms.getDeletedAt() != null && !forms.getDeletedAt().equals("") ? DateFormatter.getDateFromDateTimeString(forms.getDeletedAt()) : null);
                        projectForm.setFormsId(forms.getOriginalFormsId());
                        projectForm.setFormLastUpdatedDate(forms.getUpdatedAt() != null && !forms.getUpdatedAt().equals("") ? DateFormatter.getDateFromDateTimeString(forms.getUpdatedAt()) : null);

                        projectFormDao.insertOrReplace(projectForm);
                        List<FormRevision> revisionList = forms.getFormRevisionList();

                        if (revisionList != null && revisionList.size() > 0) {
                            insertFormNames(revisionList, projectId);
                        } else {
                            insertFormNames(forms.getOriginalFormsId(), forms.getFormName(), forms.getRevisionNumber(), projectId, projectForm.getFormsId());
                        }
                        insertProjectFormsForFormId(forms, projectId);
                        // new logic implemented for setting active revision 0 to all existing forms for original form id
                        List<Forms> oldFormsList = getDaoSession().getFormsDao().queryBuilder()
                                .where(FormsDao.Properties.OriginalFormsId.eq(forms.getOriginalFormsId())).list();

                        for (Forms oldForm : oldFormsList) {
                            oldForm.setActiveRevision(0);
                            formsDao.insertOrReplace(oldForm);
                        }


                        com.pronovoscm.persistence.domain.Forms forms1 = new com.pronovoscm.persistence.domain.Forms();
                        List<Forms> formList = getDaoSession().getFormsDao().queryBuilder()
                                .where(FormsDao.Properties.FormsId.eq(forms.getFormId())).limit(1).list();
                        if (formList.size() > 0) {
                            forms1 = formList.get(0);
                        }
                        forms1.setFormCategoriesId(forms.getFormCategoriesId());
                        forms1.setFormPrefix(forms.getFormPrefix());
                        forms1.setFormsId(forms.getFormId());
                        forms1.setFormName(forms.getFormName());
                        forms1.setPublish(forms.getPublish());
                        forms1.setTenantId(tenantId);
                        forms1.setCreatedAt(forms.getCreatedAt() != null && !forms.getCreatedAt().equals("") ? DateFormatter.getDateFromDateTimeString(forms.getCreatedAt()) : null);
                        forms1.setUpdatedAt(forms.getUpdatedAt() != null && !forms.getUpdatedAt().equals("") ? DateFormatter.getDateFromDateTimeString(forms.getUpdatedAt()) : null);
                        forms1.setFormDeletedAt(forms.getFormDeletedAt() != null && !forms.getFormDeletedAt().equals("") ? DateFormatter.getDateFromDateTimeString(forms.getFormDeletedAt()) : null);
                        forms1.formSections = forms.getFormSections();
                        forms1.setActiveRevision(forms.getActiveRevision());
                        forms1.setRevisionNumber(forms.getRevisionNumber());
                        forms1.setDefaultValues(forms.getDefaultValues());
                        forms1.setOriginalFormsId(forms.getOriginalFormsId());

                        formsDao.insertOrReplace(forms1);
                    }

                    return getProjectForm(projectId, "", users_id);
                }

            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return getProjectForm(projectId, "", users_id);

    }

    private void insertProjectFormsForFormId(ProjectForms forms, int projectId) {
        final ProjectFormDao projectFormDao = getDaoSession().getProjectFormDao();
        ProjectForm projectForm = new ProjectForm();
        projectForm.setPjProjectsId(projectId);
        projectForm.setDeletedAt(forms.getDeletedAt() != null && !forms.getDeletedAt().equals("") ?
                DateFormatter.getDateFromDateTimeString(forms.getDeletedAt()) : null);
        projectForm.setFormsId(forms.getFormId());
        projectForm.setFormLastUpdatedDate(forms.getUpdatedAt() != null && !forms.getUpdatedAt().equals("") ? DateFormatter.getDateFromDateTimeString(forms.getUpdatedAt()) : null);

        projectFormDao.insertOrReplace(projectForm);
        Log.e("ProjectForm", forms + " getProjectForm  ****** insertProjectFormsForFormId: projectForm " + projectForm);
    }

    private void insertFormNames(List<FormRevision> revisionList, Integer projectId) {
        final FormsNameDao formsNameDao = getDaoSession().getFormsNameDao();

        for (FormRevision revision : revisionList) {
            FormsName formsName = new FormsName();
            formsName.setFormName(revision.getFormName());
            formsName.setFormsId(revision.getFormsId().longValue());
            formsName.setOriginalFormsId(revision.getOriginalFormsId());
            formsName.setPjProjectsId(projectId);
            formsName.setRevisionNumber(revision.getRevisionNumber());
            formsNameDao.insertOrReplace(formsName);
            Log.d("ProjectForm", "insertFormNames: formsName " + formsName);
        }
    }

    private void insertFormNames(int originalFormsId, String formName, int revisionNumber, Integer projectId, Integer formsId) {
        final FormsNameDao formsNameDao = getDaoSession().getFormsNameDao();

        FormsName formsName = new FormsName();
        formsName.setFormName(formName);
        formsName.setFormsId(formsId.longValue());
        formsName.setOriginalFormsId(originalFormsId);
        formsName.setPjProjectsId(projectId);
        formsName.setRevisionNumber(revisionNumber);
        formsNameDao.insertOrReplace(formsName);
        Log.d("ProjectForm", "insertFormNames: formsName " + formsName);

    }

    public FormsName getUserFormsName(int originalFormsId, int revisionNumber, int projectId) {
        final FormsNameDao formsNameDao = getDaoSession().getFormsNameDao();
        QueryBuilder<FormsName> qb = formsNameDao.queryBuilder();
        qb.where(FormsNameDao.Properties.OriginalFormsId.eq(originalFormsId),
                FormsNameDao.Properties.PjProjectsId.eq(projectId), FormsNameDao.Properties.RevisionNumber.eq(revisionNumber));
        List<FormsName> formsNames = qb.list();
        if (formsNames != null && formsNames.size() > 0) {
            return formsNames.get(0);
        }
        return null;
    }

    public FormsName getUserFormsName(int formsId, int projectId) {
        final FormsNameDao formsNameDao = getDaoSession().getFormsNameDao();
        QueryBuilder<FormsName> qb = formsNameDao.queryBuilder();
        qb.where(FormsNameDao.Properties.FormsId.eq(formsId),
                FormsNameDao.Properties.PjProjectsId.eq(projectId));
        List<FormsName> formsNames = qb.list();
        if (formsNames != null && formsNames.size() > 0) {
            return formsNames.get(0);
        }
        return null;
    }

    public String getFormSection(int formId) {
        String formSection = null;
        try {

            QueryBuilder<Forms> qb = getDaoSession().getFormsDao().queryBuilder();

            qb.where(FormsDao.Properties.FormsId.eq(formId));
            qb.orderAsc(FormsDao.Properties.FormName);
            List<Forms> maxPostIdRow = qb.limit(1).list();
            if (maxPostIdRow.size() > 0) {
                return maxPostIdRow.get(0).formSections;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return formSection;
    }

    public List<FormsSchedule> doUpdateProjectFormSchedule(int tenantId, int users_id, List<ScheduledForms> scheduledFormsList, int projectId) {

        try {
            getDaoSession().callInTx(new Callable<List<FormsSchedule>>() {
                final FormsScheduleDao formsScheduleDao = getDaoSession().getFormsScheduleDao();

                @Override
                public List<FormsSchedule> call() {
                    for (int i = 0; i < scheduledFormsList.size(); i++) {

                        ScheduledForms scheduledForms = scheduledFormsList.get(i);
                        List<FormsSchedule> projectFormList = getDaoSession().getFormsScheduleDao()
                                .queryBuilder().where(FormsScheduleDao.Properties.FormsId.eq(scheduledForms.getFormId()),
                                        FormsScheduleDao.Properties.PjProjectId.eq(projectId),
                                        FormsScheduleDao.Properties.ScheduledFormId.eq(scheduledForms.getScheduledFormId())).limit(1).list();


                        FormsSchedule formsSchedule = new FormsSchedule();
                        if (projectFormList.size() > 0) {
                            formsSchedule = projectFormList.get(0);
                        }
                        formsSchedule.setNoOfTimes(scheduledForms.getNoOfTimes());
                        formsSchedule.setPjProjectId(scheduledForms.getProjectId());
                        formsSchedule.setRecurrence(scheduledForms.getRecurrence());
                        formsSchedule.setScheduledFormId(scheduledForms.getScheduledFormId());
                        formsSchedule.setTenantId(scheduledForms.getTenantId());
                        formsSchedule.setFormsId(scheduledForms.getFormId());
                        formsSchedule.setCreatedAt(scheduledForms.getCreatedAt() != null && !scheduledForms.getCreatedAt().equals("") ? DateFormatter.getDateFromDateTimeString(scheduledForms.getCreatedAt()) : null);
                        formsSchedule.setStartDate(scheduledForms.getStartDate() != null && !scheduledForms.getStartDate().equals("") ? DateFormatter.getDateFromDateTimeString(scheduledForms.getStartDate() + " 00:00:00") : null);
                        formsSchedule.setUpdatedAt(scheduledForms.getUpdatedAt() != null && !scheduledForms.getUpdatedAt().equals("") ? DateFormatter.getDateFromDateTimeString(scheduledForms.getUpdatedAt()) : null);
                        formsSchedule.setEndDate(scheduledForms.getEndDate() != null && !scheduledForms.getEndDate().equals("") ? DateFormatter.getDateFromDateTimeString(scheduledForms.getEndDate() + " 11:59:59") : null);

                        try {
                            if (scheduledForms.getDeletedAt() == null || TextUtils.isEmpty(scheduledForms.getDeletedAt())) {
                                formsScheduleDao.insertOrReplace(formsSchedule);
                                Log.e("OPEN_SCHEDULE", "Insert 111111 : scheduledForms.getDeletedAt()" + scheduledForms.getDeletedAt());
                            } else if (scheduledForms.getDeletedAt() != null && formsSchedule.getId() != null) {
                                formsScheduleDao.delete(formsSchedule);
                                Log.e("OPEN_SCHEDULE", "delete : scheduledForms.getDeletedAt()" + scheduledForms.getDeletedAt());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.e("OPEN_SCHEDULE", "doUpdateProjectFormSchedule: " + e.getStackTrace());
                        }
                    }

                    return getProjectFormSchedule(projectId);
                }

            });
        } catch (Exception e) {
            e.printStackTrace();

        }
        return getProjectFormSchedule(projectId);
    }

    public Forms getScheduleFormOfLetestRevision(int originalFormId) {
        FormsDao formsDao = getDaoSession().getFormsDao();
        QueryBuilder<Forms> qb = getDaoSession().getFormsDao().queryBuilder();
        qb.where(FormsDao.Properties.Publish.eq(1),
                FormsDao.Properties.ActiveRevision.eq(1), FormsDao.Properties.OriginalFormsId.eq(originalFormId));
        List<Forms> formList = qb.orderDesc(FormsDao.Properties.RevisionNumber).limit(1).list();
        if (formList != null && formList.size() > 0) {
            return formList.get(0);
        } else return null;
    }

    public List<FormsSchedule> getProjectFormSchedule(int projectId) {
        List<FormsSchedule> formsScheduleList = getDaoSession().getFormsScheduleDao().queryBuilder()
                .where(FormsScheduleDao.Properties.PjProjectId.eq(projectId)).list();
        return formsScheduleList;
    }

    public List<FormsSchedule> getProjectFormSchedule(int projectId, int formId) {
        List<FormsSchedule> formsScheduleList = getDaoSession().getFormsScheduleDao().queryBuilder().where(
                FormsScheduleDao.Properties.PjProjectId.eq(projectId),
                FormsScheduleDao.Properties.FormsId.eq(formId)
        ).list();
        return formsScheduleList;
    }

    public List<FormsComponent> doUpdateFormComponent(List<FormComponent> formComponent, int projectId) {

        try {
            getDaoSession().callInTx(new Callable<List<Forms>>() {
                final FormsComponentDao projectFormDao = getDaoSession().getFormsComponentDao();
                String lastComponentDateStr = "1970-01-01 01:01:01";
                Date lastComponentDate = DateFormatter.getDateFromDateTimeString(lastComponentDateStr);

                @Override
                public List<Forms> call() {
                    for (int i = 0; i < formComponent.size(); i++) {

                        FormComponent forms = formComponent.get(i);
                        List<FormsComponent> projectFormList = getDaoSession().getFormsComponentDao().queryBuilder().where(FormsComponentDao.Properties.FormsId.eq(forms.getFormId())).limit(1).list();


                        FormsComponent formsComponent = new FormsComponent();
                        if (projectFormList.size() > 0) {
                            formsComponent = projectFormList.get(0);
                        }
                        formsComponent.setDeletedAt(forms.getDeletedAt() != null && !forms.getDeletedAt().equals("") ? DateFormatter.getDateFromDateTimeString(forms.getDeletedAt()) : null);
                        formsComponent.setCreatedAt(forms.getCreatedAt() != null && !forms.getCreatedAt().equals("") ? DateFormatter.getDateFromDateTimeString(forms.getCreatedAt()) : null);
                        formsComponent.setUpdatedAt(forms.getUpdatedAt() != null && !forms.getUpdatedAt().equals("") ? DateFormatter.getDateFromDateTimeString(forms.getUpdatedAt()) : null);
                        if (forms.getUpdatedAt() != null && lastComponentDate.compareTo(DateFormatter.getDateFromDateTimeString(forms.getUpdatedAt())) < 0) {
                            lastComponentDate = DateFormatter.getDateFromDateTimeString(forms.getUpdatedAt());
                            List<ProjectForm> maxPostIdRow = getDaoSession().getProjectFormDao().queryBuilder().where(ProjectFormDao.Properties.PjProjectsId.eq(projectId), ProjectFormDao.Properties.FormsId.eq(forms.getFormId())).limit(1).list();
                            if (maxPostIdRow.size() > 0) {
                                ProjectForm projectForm = maxPostIdRow.get(0);
                                projectForm.setFormComponentLastUpdatedDate(lastComponentDate);
                                getDaoSession().getProjectFormDao().insertOrReplace(projectForm);
                            }
                        }
                        formsComponent.setFormsId(forms.getFormId());
                        formsComponent.setFormsComponents(forms.getComponent());
                        projectFormDao.insertOrReplace(formsComponent);


                    }


                    return new ArrayList<>();
                }

            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();

    }


    public List<FormCategory> doUpdateCategories(List<Categories> categoriesList) {

        try {
            getDaoSession().callInTx(new Callable<List<FormCategory>>() {
                final FormCategoryDao formCategoryDao = getDaoSession().getFormCategoryDao();

                @Override
                public List<FormCategory> call() {
                    for (int i = 0; i < categoriesList.size(); i++) {
                        Categories category = categoriesList.get(i);
                        FormCategory formCategory = new FormCategory();
                        List<FormCategory> formCategories = getDaoSession().getFormCategoryDao().queryBuilder().where(FormCategoryDao.Properties.FormCategoriesId.eq(category.getCategoryId())).limit(1).list();
                        if (formCategories.size() > 0) {
                            formCategory = formCategories.get(0);
                        }
                        formCategory.setCategoryName(category.getCategory());
                        formCategory.setFormCategoriesId(category.getCategoryId());
                        formCategory.setCreatedAt(category.getCreatedAt() != null && !category.getCreatedAt().equals("") ? DateFormatter.getDateFromDateTimeString(category.getCreatedAt()) : null);
                        formCategory.setUpdatedAt(category.getUpdatedAt() != null && !category.getUpdatedAt().equals("") ? DateFormatter.getDateFromDateTimeString(category.getUpdatedAt()) : null);
                        formCategory.setDeletedAt(category.getDeletedAt() != null && !category.getDeletedAt().equals("") ? DateFormatter.getDateFromDateTimeString(category.getDeletedAt()) : null);
                        formCategory.setTenantId(category.getTenantId());
                        formCategory.setIsDefault(category.getIsDefault());
                        formCategoryDao.insertOrReplace(formCategory);
                    }

                    return getProjectCategory();
                }

            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return getProjectCategory();

    }

    public List<FormAssets> doUpdateCSSJS(List<FormAsset> formAssetsList) {

        try {
            getDaoSession().callInTx(new Callable<List<FormAssets>>() {
                final FormAssetsDao formCategoryDao = getDaoSession().getFormAssetsDao();

                @Override
                public List<FormAssets> call() {
                    for (int i = 0; i < formAssetsList.size(); i++) {
                        FormAsset formAsset = formAssetsList.get(i);
                        FormAssets formAssets = new FormAssets();
                        List<FormAssets> formCategories = getDaoSession().getFormAssetsDao().queryBuilder().where(FormAssetsDao.Properties.FileName.eq(formAsset.getFileName())).limit(1).list();
                        if (formCategories.size() > 0) {
                            formAssets = formCategories.get(0);
                        }
                        formAssets.setFileName(formAsset.getFileName());
                        formAssets.setFileType(formAsset.getFileType());
                        formAssets.setFilePath(formAsset.getFilePath());
                        formAssets.setUpdatedAt(formAsset.getUpdatedAt() != null && !formAsset.getUpdatedAt().equals("") ? DateFormatter.getDateFromDateTimeString(formAsset.getUpdatedAt()) : null);
                        File myDir = new File(context.getFilesDir().getAbsolutePath() + "/Pronovos/");//"/PronovosPronovos"

                        String fname = formAsset.getFileName();
                        File file = new File(myDir, fname);
                        if (file.exists()) {
                            file.delete();
                        }
                        formCategoryDao.insertOrReplace(formAssets);
                    }
                    return new ArrayList<>();
                    //                    return getProjectCategory();
                }

            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();

    }

    public List<FormAssets> getFormAssets() {
        List<FormAssets> projectFormList = getDaoSession().getFormAssetsDao().queryBuilder().list();
        return projectFormList;
    }

    private List<FormCategory> getProjectCategory() {
        List<FormCategory> projectFormList = getDaoSession().getFormCategoryDao().queryBuilder().list();
        return projectFormList;

    }

    public List<ProjectForm> getProjectForms(int projectId) {
        List<ProjectForm> projectFormList = getDaoSession().getProjectFormDao().queryBuilder().where(ProjectFormDao.Properties.PjProjectsId.eq(projectId)).list();
        return projectFormList;
    }

    /**
     * Get max updated date from projects according to region id
     *
     * @return
     */
    public String getMAXCategoryUpdateDate() {
        List<FormCategory> maxPostIdRow = getDaoSession().getFormCategoryDao()
                .queryBuilder().where(FormCategoryDao.Properties.UpdatedAt.isNotNull()).orderDesc(FormCategoryDao.Properties.UpdatedAt).limit(1).list();
        if (maxPostIdRow.size() > 0) {
            Date maxUpdatedAt = maxPostIdRow.get(0).getUpdatedAt();
            return DateFormatter.formatDateTimeForService(maxUpdatedAt);
        }
        return "1970-01-01 01:01:01";
    }

    /**
     * Get max updated date from projects according to region id
     *
     * @param projectId
     * @return
     */
    public String getMAXFormScheduleUpdateDate(int projectId) {
        List<FormsSchedule> maxPostIdRow = getDaoSession().getFormsScheduleDao().queryBuilder().where(FormsScheduleDao.Properties.PjProjectId.eq(projectId),
                FormsScheduleDao.Properties.UpdatedAt.isNotNull()).orderDesc(FormsScheduleDao.Properties.UpdatedAt).limit(1).list();
        if (maxPostIdRow.size() > 0) {
            Date maxUpdatedAt = maxPostIdRow.get(0).getUpdatedAt();
            return DateFormatter.formatDateTimeForService(maxUpdatedAt);
        }
        return "1970-01-01 01:01:01";
    }

    /**
     * Get max updated date from projects according to region id
     *
     * @return
     */
    public String getMAXUserFormUpdateDate(int pjProjectId, int formId, int usersId) {
            List<UserForms> maxPostIdRow = getDaoSession().getUserFormsDao().queryBuilder().where(
                    UserFormsDao.Properties.PjProjectsId.eq(pjProjectId),
                    UserFormsDao.Properties.FormId.eq(formId),
                    UserFormsDao.Properties.UsersId.eq(usersId),
                    UserFormsDao.Properties.UpdatedAt.isNotNull()).orderDesc(UserFormsDao.Properties.UpdatedAt).limit(1).list();
            if (maxPostIdRow.size() > 0) {
                Date maxUpdatedAt = maxPostIdRow.get(0).getUpdatedAt();
                return DateFormatter.formatDateTimeForService(maxUpdatedAt);
            }
        return "1970-01-01 01:01:01";
    }

    /**
     * Get max updated date from projects according to region id
     *
     * @return
     */
    public String getMAXUserFormProjectUpdateDate(int pjProjectId, int users_id) {
        List<UserForms> maxPostIdRow = getDaoSession().getUserFormsDao().queryBuilder().
                where(UserFormsDao.Properties.PjProjectsId.eq(pjProjectId),
                        UserFormsDao.Properties.UpdatedAt.isNotNull(),
                        UserFormsDao.Properties.UsersId.eq(users_id),
                        UserFormsDao.Properties.IsSync.eq(true))
                .orderDesc(UserFormsDao.Properties.UpdatedAt).limit(1).list();
        if (maxPostIdRow.size() > 0) {
            Date maxUpdatedAt = maxPostIdRow.get(0).getUpdatedAt();
            return DateFormatter.formatDateTimeForService(maxUpdatedAt);
        }
        return "1970-01-01 01:01:01";
    }

    /**
     * Get max updated date from projects according to region id
     *
     * @return
     */
    public String getFormCategory(int categoryId) {
        List<FormCategory> maxPostIdRow = getDaoSession().getFormCategoryDao().queryBuilder().where(FormCategoryDao.Properties.FormCategoriesId.eq(categoryId)).limit(1).list();
        if (maxPostIdRow.size() > 0) {
            Date maxUpdatedAt = maxPostIdRow.get(0).getUpdatedAt();
            return maxPostIdRow.get(0).getCategoryName();
        }
        return "-";
    }

    public String getMAXFormUpdateDate(int projectId) {

        List<ProjectForm> maxPostIdRow = getDaoSession().getProjectFormDao().queryBuilder()
                .where(ProjectFormDao.Properties.PjProjectsId.eq(projectId)).orderDesc(ProjectFormDao.Properties.FormLastUpdatedDate).limit(1).list();

        if (maxPostIdRow.size() > 0 && maxPostIdRow.get(0).getFormLastUpdatedDate() != null) {
            Date maxUpdatedAt = maxPostIdRow.get(0).getFormLastUpdatedDate();
            return DateFormatter.formatDateTimeForService(maxUpdatedAt);
        }
        return "1970-01-01 01:01:01";
    }

    public String getMAXFormAreaUpdateDate(int projectId) {

        List<ProjectFormArea> maxPostIdRow = getDaoSession().getProjectFormAreaDao().queryBuilder()
                .where(ProjectFormAreaDao.Properties.PjProjectsId.eq(projectId)).orderDesc(ProjectFormAreaDao.Properties.UpdatedAt).limit(1).list();

        if (maxPostIdRow.size() > 0 && maxPostIdRow.get(0).getUpdatedAt() != null) {
            Date maxUpdatedAt = maxPostIdRow.get(0).getUpdatedAt();
            return DateFormatter.formatDateTimeForService(maxUpdatedAt);
        }
        return "1970-01-01 01:01:01";
    }

    public String getMAXFormcomponentUpdateDate(int projectId) {

        List<ProjectForm> maxPostIdRow = getDaoSession().getProjectFormDao().queryBuilder().where(ProjectFormDao.Properties.PjProjectsId.eq(projectId)).limit(1).list();
        if (maxPostIdRow.size() > 0 && maxPostIdRow.get(0).getFormComponentLastUpdatedDate() != null) {
            Date maxUpdatedAt = maxPostIdRow.get(0).getFormComponentLastUpdatedDate();
            return DateFormatter.formatDateTimeForService(maxUpdatedAt);
        }
        return "1970-01-01 01:01:01";
    }


    public List<Forms> getProjectForm(int projectId, String searchKey, int userId) {

        /*List<Forms> maxPostIdRow = getDaoSession().getFormsDao().queryBuilder().where
                (FormsDao.Properties.FormName.like("%" + searchKey + "%"), new WhereCondition.StringCondition("form_deleted_at IS NULL AND forms_id IN " +
                        "(SELECT forms_id FROM project_form where deleted_at IS NULL AND pj_projects_id = " + projectId + ")")
                )
                .orderDesc(FormsDao.Properties.FormCategoriesId).list();*/
        //        QueryBuilder.LOG_SQL = true;
        QueryBuilder<Forms> qb = getDaoSession().getFormsDao().queryBuilder();
        //QueryBuilder.LOG_SQL = true;
        Join<Forms, FormCategory> join = qb.join(FormsDao.Properties.FormCategoriesId, FormCategory.class,
                FormCategoryDao.Properties.FormCategoriesId);
        qb.where(FormsDao.Properties.FormName.like("%" + searchKey + "%"),
                FormsDao.Properties.Publish.eq(1),
                FormsDao.Properties.ActiveRevision.eq(1),
                new WhereCondition.StringCondition("form_deleted_at IS NULL AND forms_id IN "
                        + "(SELECT forms_id FROM project_form where deleted_at IS NULL AND pj_projects_id = " + projectId + ")"));
        String joinedTableName = join.getTablePrefix();
        String orderColName = FormCategoryDao.Properties.CategoryName.columnName;
        String rawOrder = joinedTableName + ".\"" + orderColName + "\" ASC";
        qb.orderRaw(rawOrder);

        //        String orderColName2 = FormsDao.Properties.FormName.columnName;
        //        String rawOrder2 = +".\""+orderColName2+"\" ASC";
        qb.orderAsc(FormsDao.Properties.FormName);
        List<Forms> maxPostIdRow = qb.list();
        HashMap<Integer, FormsPermission> permissionHashMap = getProjectFormPermissionMap(projectId, userId);
        /*getDaoSession().getFormsDao().queryRaw("select * from forms f join form_category fc on f.form_categories_id = fc.form_categories_id " +
                "where form_deleted_at IS NULL AND forms_id IN " +
                "(SELECT forms_id FROM project_form where deleted_at IS NULL AND pj_projects_id = ?)" +
                " order by fc.category_name, f.form_name", projectId+"");*/
        if (maxPostIdRow.size() > 0) {
            maxPostIdRow = filterFormListByPermission(maxPostIdRow, permissionHashMap);
            return maxPostIdRow;
        }
        return new ArrayList<>();
    }

    private List<Forms> filterFormListByPermission(List<Forms> formList, HashMap<Integer, FormsPermission> permissionHashMap) {
        //int i=1;
        List<Forms> filteredList = new ArrayList<>();
        if (permissionHashMap == null || permissionHashMap.size() == 0) {
            return formList;
        }
        for (Forms form : formList) {

            if (permissionHashMap.containsKey(form.originalFormsId)) {
                FormsPermission formsPermission = permissionHashMap.get(form.originalFormsId);
                if (formsPermission.getIsActive() == 1) {
                    filteredList.add(form);
                } else if (formsPermission.getDeletedAt() != null) {
                    filteredList.add(form);
                }
            } else {
                filteredList.add(form);
            }
            // i=i+1;
        }

        return filteredList;
    }

    public HashMap<Integer, FormsPermission> getProjectFormPermissionMap(int projectId, int userId) {
        List<FormsPermission> formsPermissionsList = getDaoSession().getFormsPermissionDao().queryBuilder()
                .where(FormsPermissionDao.Properties.PjProjectsId.eq(projectId),
                        FormsPermissionDao.Properties.UsersId.eq(userId)).list();
        HashMap<Integer, FormsPermission> permissionHashMap = new HashMap<>();
        if (formsPermissionsList != null && formsPermissionsList.size() > 0) {
            for (FormsPermission permission : formsPermissionsList) {
                permissionHashMap.put(permission.getFormsId(), permission);
            }
        }
        return permissionHashMap;
    }

    public String getFormComponent(int formId) {
        List<FormsComponent> maxPostIdRow = getDaoSession().getFormsComponentDao().queryBuilder().where(FormsComponentDao.Properties.FormsId.eq(formId)).limit(1).list();
        if (maxPostIdRow.size() > 0) {
            return maxPostIdRow.get(0).getFormsComponents();
        }
        return "";
    }

    public boolean isFormComponentDataExist(int formId, int originalFormId, int revisionNumber) {
        List<FormsComponent> maxPostIdRow = getDaoSession().getFormsComponentDao().queryBuilder().where(FormsComponentDao.Properties.FormsId.eq(formId)).limit(1).list();
        if (maxPostIdRow.size() > 0) {
            return true;
        }
        return false;
    }

    public String getFormName(int formId) {
        List<Forms> maxPostIdRow = getDaoSession().getFormsDao().queryBuilder().where(
                FormsDao.Properties.FormsId.eq(formId), FormsDao.Properties.Publish.eq(1)).limit(1).list();
        if (maxPostIdRow.size() > 0) {
            return maxPostIdRow.get(0).getFormName();
        }
        return "";
    }

    public List<UserForms> doUpdateUserForms(List<UserForm> userForms, int formId, int projectId, boolean b, int usersId) {

        try {
            getDaoSession().callInTx(new Callable<List<UserForms>>() {
                final UserFormsDao userFormsDao = getDaoSession().getUserFormsDao();

                @Override
                public List<UserForms> call() {
                    for (int i = 0; i < userForms.size(); i++) {

                        UserForm userForm = userForms.get(i);
                        List<UserForms> projectFormList = getDaoSession().getUserFormsDao().queryBuilder().where(
                                UserFormsDao.Properties.FormSubmitId.eq(userForm.getUserFormsId()),
                                UserFormsDao.Properties.UsersId.eq(usersId),
                                UserFormsDao.Properties.PjProjectsId.eq(userForm.getPjProjectsId()),
                                UserFormsDao.Properties.FormId.eq(userForm.getFormsId())).limit(1).list();

                        List<UserForms> projectFormListMobile = getDaoSession().getUserFormsDao().queryBuilder().where(
                                UserFormsDao.Properties.FormSubmitId.eq(0),
                                UserFormsDao.Properties.FormSubmitMobileId.eq(userForm.getUserFormMobileId()),
                                UserFormsDao.Properties.UsersId.eq(usersId),
                                UserFormsDao.Properties.PjProjectsId.eq(userForm.getPjProjectsId()),
                                UserFormsDao.Properties.FormId.eq(userForm.getFormsId())).limit(1).list();


                        UserForms userFormItem = new UserForms();
                        if (projectFormList.size() > 0) {
                            userFormItem = projectFormList.get(0);
                        } else if (projectFormListMobile.size() > 0) {
                            userFormItem = projectFormListMobile.get(0);
                        }
                        userFormItem.setDeletedAt(userForm.getDeletedAt() != null && !userForm.getDeletedAt().equals("") ? DateFormatter.getDateFromDateTimeString(userForm.getDeletedAt()) : null);
                        userFormItem.setCreatedAt(userForm.getCreatedAt() != null && !userForm.getCreatedAt().equals("") ? DateFormatter.getDateFromDateTimeString(userForm.getCreatedAt()) : null);
                        if (b) {
                            userFormItem.setUpdatedAt(userForm.getUpdatedAt() != null && !userForm.getUpdatedAt().equals("") ? DateFormatter.getDateFromDateTimeString(userForm.getUpdatedAt()) : null);
                        } else {
                            userFormItem.setUpdatedAt(null);
                        }
                        userFormItem.setDueDate(userForm.getDueDate() != null && !userForm.getDueDate().equals("") ? DateFormatter.getDateFromDateTimeString(userForm.getDueDate()) : null);
                        userFormItem.setDateSent(userForm.getDateSent() != null && !userForm.getDateSent().equals("") ? DateFormatter.getDateFromDateTimeString(userForm.getDateSent()) : null);
                        userFormItem.setFormId(userForm.getFormsId());
                        userFormItem.setCreatedByUserName(userForm.getCreatedBy());
                        userFormItem.setCreatedUserId(userForm.getUsersId());
                        userFormItem.setUpdatedByUserName(userForm.getUpdatedByUser());
                        userFormItem.setUpdatedUserId(userForm.getUpdatedBy());
                        userFormItem.setPublish(userForm.getPublish());
                        userFormItem.setRevisionNumber(userForm.getRevisionNumber());
                        if (TextUtils.isEmpty(userFormItem.getSubmittedData()) || !userFormItem.getSubmittedData().equals(userForm.getSubmittedData())) {
                            userFormItem.setTempSubmittedData(null);
                            userFormItem.setDeletedImages("[]");
                        }
                        userFormItem.setSubmittedData(userForm.getSubmittedData());

                        userFormItem.setEmailStatus(userForm.getEmailStatus());

                        userFormItem.setIsSync(Boolean.TRUE);
                        userFormItem.setPjProjectsId(userForm.getPjProjectsId());
                        userFormItem.setTenantId(userForm.getTenantId());
                        userFormItem.setFormSubmitId(userForm.getUserFormsId());
                        userFormItem.setFormSubmitMobileId(userForm.getUserFormMobileId());
                        userFormItem.setScheduleFormId(userForm.getScheduleFormId());
                        userFormItem.setUsersId(usersId);
                        userFormItem.setPjAreasId(userForm.getPjAreasId());
                        userFormItem.setFormSaveDate(getUTCDateTimeAsDate());
                        if (userForm.getDeletedAttachments() != null && userForm.getDeletedAttachments().size() > 0) {
                            for (DeletedAttachments deletedAttachment : userForm.getDeletedAttachments()) {
                                if (!TextUtils.isEmpty(deletedAttachment.getFilename())) {
                                    File myDir = new File(context.getFilesDir().getAbsolutePath() + "/Pronovos/Form/");
                                    String filePath = myDir.getAbsolutePath();
                                    File imgFile = new File(filePath + "/" + deletedAttachment.getFilename());
                                    if (imgFile.exists()) {
                                        imgFile.delete();
                                    }
                                }
                            }
                        }
                        userFormsDao.insertOrReplace(userFormItem);
                        //  Log.e("Form Issue", "doUpdateUserForms: " + userFormItem.toString());

                    }
                    if (formId == -1) {
                        return new ArrayList<>();
                    }

                    return getUserForms(projectId, formId, usersId);
                }

            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (formId == -1) {
            return new ArrayList<>();
        }
        return getUserForms(projectId, formId, usersId);
    }

    public List<UserForms> getUserForms(int projectId, int formId, int usersId) {
        List<UserForms> maxPostIdRow = getDaoSession().getUserFormsDao().queryBuilder().
                where(UserFormsDao.Properties.PjProjectsId.eq(projectId),
                        UserFormsDao.Properties.FormId.eq(formId),
                        UserFormsDao.Properties.UsersId.eq(usersId),
                        UserFormsDao.Properties.DeletedAt.isNull()/*,
                        UserFormsDao.Properties.TempSubmittedData.isNull()*//*,
                        UserFormsDao.Properties.IsSync.eq(true)*/

                )
                .orderDesc(UserFormsDao.Properties.CreatedAt).list();
        if (maxPostIdRow.size() > 0) {
            return maxPostIdRow;
        }
        return new ArrayList<>();
    }

    public List<UserForms> getUserForm(int projectId, int usersId, String searchKey) {

        QueryBuilder<Forms> qb = getDaoSession().getFormsDao().queryBuilder();
        Join<Forms, FormCategory> join = qb.join(FormsDao.Properties.FormCategoriesId, FormCategory.class, FormCategoryDao.Properties.FormCategoriesId);

        qb.where(FormsDao.Properties.FormName.like("%" + searchKey + "%"),
                FormsDao.Properties.Publish.eq(1),
                new WhereCondition.StringCondition("form_deleted_at IS NULL AND forms_id IN " + "(SELECT forms_id FROM project_form where deleted_at IS NULL AND pj_projects_id = " + projectId + ")"));
        String joinedTableName = join.getTablePrefix();
        String orderColName = FormCategoryDao.Properties.CategoryName.columnName;
        String rawOrder = joinedTableName + ".\"" + orderColName + "\" ASC";
        qb.orderRaw(rawOrder);
        //        String orderColName2 = FormsDao.Properties.FormName.columnName;
        //        String rawOrder2 = +".\""+orderColName2+"\" ASC";
        qb.orderAsc(FormsDao.Properties.FormName);
        List<Forms> forms = qb.list();
        List<UserForms> userFormsList = new ArrayList<>();
        for (Forms form : forms) {

            List<UserForms> maxPostIdRow = getDaoSession().getUserFormsDao().queryBuilder().
                    where(UserFormsDao.Properties.PjProjectsId.eq(projectId),
                            UserFormsDao.Properties.UsersId.eq(usersId),
                            UserFormsDao.Properties.FormId.eq(form.getOriginalFormsId()),
                            UserFormsDao.Properties.DeletedAt.isNull()).whereOr(
                    UserFormsDao.Properties.TempSubmittedData.isNotNull(),
                    UserFormsDao.Properties.IsSync.eq(Boolean.FALSE))
                    .orderDesc(UserFormsDao.Properties.CreatedAt).list();
            if (maxPostIdRow.size() > 0) {
                userFormsList.addAll(maxPostIdRow);
            }
        }

        return userFormsList;
    }

    public UserForms getUserForm(int projectId, Long formMobileId, int users_id) {
        List<UserForms> maxPostIdRow = getDaoSession().getUserFormsDao().queryBuilder().
                where(UserFormsDao.Properties.PjProjectsId.eq(projectId),
                        UserFormsDao.Properties.UsersId.eq(users_id),
                        UserFormsDao.Properties.FormSubmitMobileId.eq(formMobileId),
                        UserFormsDao.Properties.DeletedAt.isNull())
                .orderDesc(UserFormsDao.Properties.CreatedAt).list();
        if (maxPostIdRow.size() > 0) {
            return maxPostIdRow.get(0);
        }
        return null;
    }

    public List<UserForms> getUserForms(int projectId, int users_id) {
        List<UserForms> maxPostIdRow = getDaoSession().getUserFormsDao().queryBuilder().where(UserFormsDao.Properties.PjProjectsId.eq(projectId),
                UserFormsDao.Properties.UsersId.eq(users_id),
                UserFormsDao.Properties.DeletedAt.isNull(), new WhereCondition.StringCondition("form_id IN " +
                        "(SELECT forms_id FROM project_form where deleted_at IS NULL)")).orderAsc(UserFormsDao.Properties.CreatedAt).list();
        if (maxPostIdRow.size() > 0) {
            return maxPostIdRow;
        }
        return new ArrayList<>();
    }

    public String getUserFormSubmittedData(long userFormId, int usersId) {
        List<UserForms> maxPostIdRow = getDaoSession().getUserFormsDao().queryBuilder().where(UserFormsDao.Properties.Id.eq(userFormId), UserFormsDao.Properties.UsersId.eq(usersId)).list();
        if (maxPostIdRow.size() > 0) {
            return maxPostIdRow.get(0).getTempSubmittedData() != null ? maxPostIdRow.get(0).getTempSubmittedData() : maxPostIdRow.get(0).getSubmittedData();
        }
        return null;

    }

    public UserForms getUserFormDetails(long userFormId, int usersId) {
        List<UserForms> maxPostIdRow = getDaoSession().getUserFormsDao().queryBuilder().where(UserFormsDao.Properties.Id.eq(userFormId), UserFormsDao.Properties.UsersId.eq(usersId)).list();
        if (maxPostIdRow.size() > 0) {
            return maxPostIdRow.get(0);
        }
        return null;

    }
    public UserForms getUserFormDetails(long userFormId, int usersId, int projectId) {
        List<UserForms> maxPostIdRow = getDaoSession().getUserFormsDao().queryBuilder().where(
                UserFormsDao.Properties.Id.eq(userFormId),
                UserFormsDao.Properties.UsersId.eq(usersId),
                UserFormsDao.Properties.PjProjectsId.eq(projectId)).list();
        if (maxPostIdRow.size() > 0) {
            return maxPostIdRow.get(0);
        }
        return null;

    }

    public String getUserFormDeleteImages(long userFormId, int users_id) {
        List<UserForms> maxPostIdRow = getDaoSession().getUserFormsDao().queryBuilder().where(UserFormsDao.Properties.Id.eq(userFormId), UserFormsDao.Properties.UsersId.eq(users_id)).list();
        if (maxPostIdRow.size() > 0) {
            return maxPostIdRow.get(0).getDeletedImages() != null ? maxPostIdRow.get(0).getDeletedImages() : "";
        }
        return "";

    }

    public UserForms getUserFormSubmitted(long userFormId, int usersId) {
        List<UserForms> maxPostIdRow = getDaoSession().getUserFormsDao().queryBuilder().where(UserFormsDao.Properties.Id.eq(userFormId), UserFormsDao.Properties.UsersId.eq(usersId)).list();
        if (maxPostIdRow.size() > 0) {
            return maxPostIdRow.get(0);
        }
        return null;

    }

    public UserForms getOfflineSubmittedUserForm(long formSubmitMobileId, int users_id) {
        List<UserForms> maxPostIdRow = getDaoSession().getUserFormsDao().queryBuilder().where(
                // UserFormsDao.Properties.FormSubmitId.eq(formSubmitId),
                UserFormsDao.Properties.UsersId.eq(users_id),
                UserFormsDao.Properties.FormSubmitMobileId.eq(formSubmitMobileId)).list();
        if (maxPostIdRow.size() > 0) {
            return maxPostIdRow.get(0);
        }
        return null;

    }

    public UserForms getSubmittedUserForm(long formSubmitId, long formSubmitMobileId, int users_id) {
        List<UserForms> maxPostIdRow = getDaoSession().getUserFormsDao().queryBuilder().where(
                UserFormsDao.Properties.FormSubmitId.eq(formSubmitId),
                UserFormsDao.Properties.UsersId.eq(users_id),
                UserFormsDao.Properties.FormSubmitMobileId.eq(formSubmitMobileId)).list();
        if (maxPostIdRow.size() > 0) {
            return maxPostIdRow.get(0);
        }
        return null;

    }

    public UserForms saveUserFormSubmittedValue(int projectId, int formId, int revisionNumber, String value,
                                                Date createdDate, long userFormId, Integer pjAreaId, LoginResponse loginResponse) {
        UserForms userFormItem = new UserForms();
        UserFormsDao userFormsDao = getDaoSession().getUserFormsDao();
        if (userFormId != -1) {
            List<UserForms> maxPostIdRow = getDaoSession().getUserFormsDao().queryBuilder().where(UserFormsDao.Properties.Id.eq(userFormId)).list();
            if (maxPostIdRow.size() > 0) {
                userFormItem = maxPostIdRow.get(0);
            } else {
                userFormId = -1;
            }
        }
        userFormItem.setDeletedAt(null);
        userFormItem.setDueDate(null);
        userFormItem.setDateSent(null);
        userFormItem.setFormId(formId);

        if (userFormId == -1) {
            userFormItem.setCreatedByUserName(loginResponse.getUserDetails().getFirstname() + " " + loginResponse.getUserDetails().getLastname());
            userFormItem.setCreatedUserId(loginResponse.getUserDetails().getUsers_id());
            userFormItem.setFormSubmitId(0L);
            userFormItem.setFormSubmitMobileId(generateUniqueMobileUserFormsId());
            userFormItem.setUpdatedAt(new Date());
        } else {
            userFormItem.setUpdatedByUserName(loginResponse.getUserDetails().getFirstname() + " " + loginResponse.getUserDetails().getLastname());
            userFormItem.setUpdatedUserId(loginResponse.getUserDetails().getUsers_id());
            userFormItem.setCreatedAt(new Date());
        }
        userFormItem.setCreatedAt(createdDate);
        userFormItem.setSubmittedData(value);
        userFormItem.setUsersId(loginResponse.getUserDetails().getUsers_id());
        userFormItem.setDeletedImages("[]");
        userFormItem.setTempSubmittedData(null);
        userFormItem.setIsSync(Boolean.FALSE);
        userFormItem.setPublish(1);
        userFormItem.setPjProjectsId(projectId);
        userFormItem.setTenantId(loginResponse.getUserDetails().getTenantId());
        userFormItem.setFormSaveDate(getUTCDateTimeAsDate());
        userFormItem.setRevisionNumber(revisionNumber);
        userFormItem.setPjAreasId(pjAreaId);
        userFormsDao.insertOrReplace(userFormItem);
        return userFormItem;
    }

    public UserForms saveUserFormSubmittedData(int projectId, int formId, String value, long userFormId, LoginResponse loginResponse, String dueDate,
                                               int schedule_form_id, String deleteImageString, int published, Integer pjAreasId,
                                               Date createdDate, int revisionNumber) {


        UserForms userFormItem = new UserForms();
        UserFormsDao userFormsDao = getDaoSession().getUserFormsDao();
        if (userFormId != -1) {
            List<UserForms> maxPostIdRow = getDaoSession().getUserFormsDao().queryBuilder().where(UserFormsDao.Properties.Id.eq(userFormId)).list();
            if (maxPostIdRow.size() > 0) {
                userFormItem = maxPostIdRow.get(0);
            } else {
                userFormId = -1;
            }
        }
        if (TextUtils.isEmpty(dueDate) && schedule_form_id == 0) {

            List<FormsSchedule> formsScheduleList = getProjectFormSchedule(projectId, formId);
            for (FormsSchedule formsSchedule : formsScheduleList) {

                Forms forms = getFormDetails(projectId, formsSchedule.getFormsId(), loginResponse.getUserDetails().getUsers_id());
                if (!TextUtils.isEmpty(formsSchedule.getRecurrence())) {
                    if (formsSchedule.getStartDate() != null && forms != null) {
                        Date d1 = formsSchedule.getStartDate();
                        Calendar currentPageDate = Calendar.getInstance();
                        currentPageDate.setTime(createdDate);
                        Calendar calendar = Calendar.getInstance();
                        Calendar endCalendar = Calendar.getInstance();
                        calendar.set(currentPageDate.DAY_OF_MONTH, 1);
                        calendar.set(Calendar.MONTH, currentPageDate.get(currentPageDate.MONTH) - 1);
                        calendar.set(Calendar.YEAR, currentPageDate.get(Calendar.YEAR));
                        endCalendar.setTime(d1);
                        endCalendar.set(Calendar.YEAR, currentPageDate.get(Calendar.YEAR));
                        String day = (String) DateFormat.format("dd", formsSchedule.getStartDate()); // 20
                        String monthNumber = (String) DateFormat.format("MM", formsSchedule.getStartDate()); // 06
                        String year = (String) DateFormat.format("yyyy", formsSchedule.getStartDate()); // 2013
                        if (currentPageDate.get(Calendar.YEAR) - 1 > Integer.parseInt(year)) {
                            year = currentPageDate.get(Calendar.YEAR) - 1 + "";
                        }
                        FormsSchedule formsSchedule1 = showScheduleOnCalendar(Integer.parseInt(day), Integer.parseInt(monthNumber) - 1, Integer.parseInt(year), formsSchedule, currentPageDate, endCalendar, createdDate, loginResponse);
                        if (formsSchedule1 != null) {
                            schedule_form_id = formsSchedule1.getScheduledFormId();
                            SimpleDateFormat sdformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            dueDate = sdformat.format(createdDate);
                            break;
                        }
                    }
                } else if (forms != null) {
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(formsSchedule.getStartDate());
                    SimpleDateFormat sdformat = new SimpleDateFormat("yyyy-MM-dd");
                    List<UserForms> userForm = getUserFormDueDate(cal, formsSchedule.getFormsId(), formsSchedule.getPjProjectId(), formsSchedule.getScheduledFormId(),
                            loginResponse.getUserDetails().getUsers_id());
                    if (userForm.size() <= 0 && sdformat.format(cal.getTime()).equals(sdformat.format(createdDate))) {
                        schedule_form_id = formsSchedule.getScheduledFormId();
                        SimpleDateFormat sdformat1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        dueDate = sdformat1.format(createdDate);
                        break;
                    }
                }
            }
        }
        userFormItem.setDeletedAt(null);
        SimpleDateFormat sdformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date dDate = null;
        if (!TextUtils.isEmpty(dueDate)) {
            try {
                dDate = sdformat.parse(dueDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        userFormItem.setDueDate(dDate);
        userFormItem.setDateSent(null);
        userFormItem.setFormId(formId);
        userFormItem.setDeletedImages(deleteImageString);
        userFormItem.setScheduleFormId(schedule_form_id);
        if (userFormId == -1) {
            userFormItem.setCreatedByUserName(loginResponse.getUserDetails().getFirstname() + " " + loginResponse.getUserDetails().getLastname());
            userFormItem.setCreatedUserId(loginResponse.getUserDetails().getUsers_id());
            userFormItem.setFormSubmitId(0L);
            userFormItem.setFormSubmitMobileId(generateUniqueMobileUserFormsId());
            userFormItem.setCreatedAt(new Date());
            userFormItem.setPublish(published);
        } else {
            userFormItem.setUpdatedByUserName(loginResponse.getUserDetails().getFirstname() + " " + loginResponse.getUserDetails().getLastname());
//            userFormItem.setUpdatedAt(new Date());
            userFormItem.setUpdatedUserId(loginResponse.getUserDetails().getUsers_id());
            if (userFormItem.getPublish() == null || userFormItem.getPublish() == 0) {
                userFormItem.setPublish(published);
            }
        }
        // added for new  date feature
        userFormItem.setCreatedAt(createdDate);

        userFormItem.setSubmittedData(value);
        userFormItem.setTempSubmittedData(null);
        userFormItem.setIsSync(Boolean.FALSE);
        userFormItem.setUsersId(loginResponse.getUserDetails().getUsers_id());
        userFormItem.setPjProjectsId(projectId);
        userFormItem.setTenantId(loginResponse.getUserDetails().getTenantId());
        userFormItem.setFormSaveDate(getUTCDateTimeAsDate());
        userFormItem.setRevisionNumber(revisionNumber);
        userFormItem.setPjAreasId(pjAreasId);
        userFormsDao.insertOrReplace(userFormItem);

        TransactionLogMobileDao mPronovosSyncDataDao = getDaoSession().getTransactionLogMobileDao();


        List<TransactionLogMobile> transactionLogMobileList = getDaoSession().getTransactionLogMobileDao().queryBuilder().where(
                TransactionLogMobileDao.Properties.MobileId.eq(userFormItem.getFormSubmitMobileId()),
                TransactionLogMobileDao.Properties.ServerId.eq(userFormItem.getFormSubmitId()),
                TransactionLogMobileDao.Properties.Module.eq(TransactionModuleEnum.PROJECT_FORM_SUBMIT.ordinal())
        )
                .limit(1).list();
        if (transactionLogMobileList.size() <= 0) {
            TransactionLogMobile transactionLogMobile = new TransactionLogMobile();

            transactionLogMobile.setUsersId(loginResponse.getUserDetails().getUsers_id());
            transactionLogMobile.setModule(TransactionModuleEnum.PROJECT_FORM_SUBMIT.ordinal());
            transactionLogMobile.setStatus(SyncDataEnum.NOTSYNC.ordinal());
            transactionLogMobile.setMobileId(userFormItem.getFormSubmitMobileId());
            transactionLogMobile.setServerId(userFormItem.getFormSubmitId());
            transactionLogMobile.setCreateDate(Calendar.getInstance().getTime());

            mPronovosSyncDataDao.save(transactionLogMobile);
            ((PronovosApplication) context.getApplicationContext()).setupAndStartWorkManager();
        }
        return userFormItem;
    }

    private FormsSchedule showScheduleOnCalendar(int day, int month, int year, FormsSchedule formsSchedule, Calendar currentPageDate, Calendar cal1, Date createdDate, LoginResponse loginResponse) {
        // com.pronovoscm.chipslayoutmanager.util.log.Log.i("Calendar rr ", "showScheduleOnCalendar: " + day + "/" + month + "/" + year + "  rule  " + formsSchedule.getRecurrence());
        RecurrenceRule rule = null;
        ArrayList<String> exDateList = new ArrayList<>();
        String[] exDates = null;
        RecurrenceRuleIterator it = null;
        DateTime start = new DateTime(year, month, day, 0, 0, 0);
        try {
            String rrule = formsSchedule.getRecurrence();
            int maxInstances = 0;
            //            if (!rrule.contains("EXDATE") ) {
            rule = new RecurrenceRule(formsSchedule.getRecurrence());
            com.pronovoscm.chipslayoutmanager.util.log.Log.e("Test", "showScheduleOnCalendar: " + rule);
            it = rule.iterator(start);
            maxInstances = 366;
            if (rrule.contains("EXDATE")) {
                String[] parts = formsSchedule.getRecurrence().toUpperCase().split(";");
                for (String keyvalue : parts) {
                    if (keyvalue.startsWith("EXDATE")) {
                        int equals = keyvalue.indexOf("=");
                        if (equals > 0) {
                            String key = keyvalue.substring(0, equals);
                            if (key.equals("EXDATE")) {
                                String value = keyvalue.substring(equals + 1);
                                exDates = value.toUpperCase().split(",");
                                //                                    exDateList = Arrays.asList(value.toUpperCase().split(","));
                            }
                        }
                        break;
                    }
                }

            }

            if (exDates != null) {
                exDateList.addAll(Arrays.asList(exDates));
            }

            while (it.hasNext() && (!rule.isInfinite() || maxInstances-- > 0)) {

                DateTime nextInstance = it.nextDateTime();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd'T'HHmmss");
                Date date = null;
                Date date2 = null;
                try {
                    date = sdf.parse(nextInstance.toString());
                    date2 = sdf.parse(nextInstance.toString());
                    currentPageDate.set(Calendar.DAY_OF_MONTH, currentPageDate.getActualMaximum(Calendar.DAY_OF_MONTH));
                    currentPageDate.set(Calendar.MONTH, currentPageDate.MONTH + 2);
                    SimpleDateFormat sdformat = new SimpleDateFormat("yyyy-MM-dd");
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(date);
                    if (year < cal.get(Calendar.YEAR)) {
                        //   Log.i("Calendar", year + " end date showScheduleOnCalendar: == " + cal.get(Calendar.YEAR));
                        break;
                    }
                    if (year > cal.get(Calendar.YEAR)) {
                        maxInstances = maxInstances + 1;
                        continue;
                    }

                    SimpleDateFormat exsdformat = new SimpleDateFormat("yyyyMMdd");
                    List<UserForms> userForm = getUserFormDueDate(cal, formsSchedule.getFormsId(), formsSchedule.getPjProjectId(), formsSchedule.getScheduledFormId(), loginResponse.getUserDetails().getUsers_id());
                    //  com.pronovoscm.chipslayoutmanager.util.log.Log.i("formSchedule", start + " " + sdformat.format(cal.getTime()) + " new  == showScheduleOnCalendar: formSchedule " + formsSchedule.getRecurrence() + " id " + formsSchedule.getFormsId() + " schedule id " + formsSchedule.getScheduledFormId());
                    boolean excontain = exDateList.contains(exsdformat.format(cal.getTime()));
                    int userFormSize = userForm.size();
                    if (sdformat.format(cal.getTime()).equals("2020-01-16")) {
                        com.pronovoscm.chipslayoutmanager.util.log.Log.e("test", formsSchedule.getStartDate() + "userFormSize  " + userFormSize + " excontain = " + excontain + " rrule " + formsSchedule.getRecurrence() + " form schedule test " + cal.getTime());
                    }


                    if (!exDateList.contains(exsdformat.format(cal.getTime())) && userForm.size() <= 0 && sdformat.format(cal.getTime()).equals(sdformat.format(createdDate))) {
                        return formsSchedule;
                    }


                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        } catch (InvalidRecurrenceRuleException e) {
            com.pronovoscm.chipslayoutmanager.util.log.Log.i("RRule", "showScheduleOnCalendar: " + formsSchedule.getRecurrence());
            e.printStackTrace();
        }
        return null;
    }

    public UserForms saveUserFormTempData(int projectId, int formId, String value, long userFormId,
                                          LoginResponse loginResponse, String dueDate, int schedule_form_id,
                                          String deletedImages, Date createdDate, int formRevisionNumber, Integer pjAreaId) {
        Log.d("OpenForm", "repository saveUserFormTempData: formId " + formId + " formRevisionNumber " + formRevisionNumber);
        UserForms userFormItem = new UserForms();
        UserFormsDao userFormsDao = getDaoSession().getUserFormsDao();
        if (userFormId != -1) {
            List<UserForms> maxPostIdRow = getDaoSession().getUserFormsDao().queryBuilder().where(UserFormsDao.Properties.Id.eq(userFormId)).list();
            if (maxPostIdRow.size() > 0) {
                userFormItem = maxPostIdRow.get(0);
            }
        }
        userFormItem.setDeletedAt(null);
        SimpleDateFormat sdformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date dDate = null;
        if (!TextUtils.isEmpty(dueDate)) {
            try {
                dDate = sdformat.parse(dueDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        userFormItem.setDueDate(dDate);
        userFormItem.setDateSent(null);
        userFormItem.setFormId(formId);

        userFormItem.setScheduleFormId(schedule_form_id);
        if (userFormId == -1) {
            userFormItem.setCreatedByUserName(loginResponse.getUserDetails().getFirstname() + " " + loginResponse.getUserDetails().getLastname());
            userFormItem.setCreatedUserId(loginResponse.getUserDetails().getUsers_id());
            userFormItem.setFormSubmitId(0L);
            userFormItem.setFormSubmitMobileId(generateUniqueMobileUserFormsId());
            userFormItem.setCreatedAt(new Date());
            userFormItem.setPublish(0);
        } else {
            userFormItem.setUpdatedByUserName(loginResponse.getUserDetails().getFirstname() + " " + loginResponse.getUserDetails().getLastname());
//            userFormItem.setUpdatedAt(new Date());
            userFormItem.setUpdatedUserId(loginResponse.getUserDetails().getUsers_id());
        }
        // added date for fixing date change issue
        userFormItem.setCreatedAt(createdDate);
        userFormItem.setDeletedImages(deletedImages);
        userFormItem.setTempSubmittedData(value);
        userFormItem.setIsSync(Boolean.TRUE);
        userFormItem.setPjProjectsId(projectId);
        userFormItem.setTenantId(loginResponse.getUserDetails().getTenantId());
        userFormItem.setUsersId(loginResponse.getUserDetails().getUsers_id());
        userFormItem.setFormSaveDate(getUTCDateTimeAsDate());
        userFormItem.setRevisionNumber(formRevisionNumber);
        userFormItem.setPjAreasId(pjAreaId);
        userFormsDao.insertOrReplace(userFormItem);
        return getUserForm(userFormItem.getPjProjectsId(), userFormItem.getFormSubmitMobileId(), loginResponse.getUserDetails().getUsers_id());

    }

    public Long generateUniqueMobileUserFormsId() {

        long timeSeed = System.nanoTime(); // to get the current date time value

        double randSeed = Math.random() * 1000; // random number generation

        long mobileId = (long) (timeSeed * randSeed);

        String s = mobileId + "";
        String subStr = s.substring(0, 9);
        mobileId = Long.parseLong(subStr);

        List<UserForms> punchlistDbs = getDaoSession().getUserFormsDao().queryBuilder().where(UserFormsDao.Properties.FormSubmitMobileId.eq(mobileId)).limit(1).list();
        if (punchlistDbs.size() > 0) {
            return generateUniqueMobileUserFormsId();
        }
        return mobileId;
    }

    public void removeUserForm(UserForms userFormItem, int usersId) {
        DeleteQuery<UserForms> photoDeleteQuery = getDaoSession().queryBuilder(UserForms.class).where(
                UserFormsDao.Properties.UsersId.eq(usersId),
                UserFormsDao.Properties.Id.eq(userFormItem.getId())).buildDelete();
        photoDeleteQuery.executeDeleteWithoutDetachingEntities();
    }

    public void replaceDataWithOld(UserForms savedUserForm, int users_id) {
        UserForms userFormItem = new UserForms();
        UserFormsDao userFormsDao = getDaoSession().getUserFormsDao();
        List<UserForms> maxPostIdRow = getDaoSession().getUserFormsDao().queryBuilder().where(UserFormsDao.Properties.Id.eq(savedUserForm.getId())).list();
        if (maxPostIdRow.size() > 0) {
            userFormItem = maxPostIdRow.get(0);
        }
        userFormItem.setDeletedAt(savedUserForm.getDeletedAt());
        userFormItem.setDueDate(savedUserForm.getDueDate());
        userFormItem.setDateSent(savedUserForm.getDateSent());
        userFormItem.setFormId(savedUserForm.getFormId());
        userFormItem.setCreatedByUserName(savedUserForm.getCreatedByUserName());
        userFormItem.setCreatedUserId(savedUserForm.getCreatedUserId());
        userFormItem.setFormSubmitId(savedUserForm.getFormSubmitId());
        userFormItem.setUpdatedAt(savedUserForm.getUpdatedAt());
        userFormItem.setFormSubmitMobileId(savedUserForm.getFormSubmitMobileId());
        userFormItem.setUpdatedByUserName(savedUserForm.getUpdatedByUserName());
        userFormItem.setUpdatedUserId(savedUserForm.getUpdatedUserId());
        userFormItem.setCreatedAt(savedUserForm.getCreatedAt());
        userFormItem.setSubmittedData(savedUserForm.getSubmittedData());
        userFormItem.setDeletedImages("[]");
        userFormItem.setTempSubmittedData(null);
        userFormItem.setIsSync(savedUserForm.getIsSync());
        userFormItem.setPjProjectsId(savedUserForm.getPjProjectsId());
        userFormItem.setTenantId(savedUserForm.getTenantId());
        userFormItem.setUsersId(users_id);
        userFormItem.setRevisionNumber(savedUserForm.getRevisionNumber());
        userFormItem.setPublish(savedUserForm.getPublish());
        userFormItem.setPjAreasId(savedUserForm.getPjAreasId());
        userFormItem.setEmailStatus(savedUserForm.getEmailStatus());
        userFormsDao.insertOrReplace(userFormItem);
    }


    /**
     * Get max updated date from projects according to region id
     *
     * @return
     */
    public String getMAXCSSJSUpdateDate() {
        List<FormAssets> maxPostIdRow = getDaoSession().getFormAssetsDao().queryBuilder().where(FormAssetsDao.Properties.UpdatedAt.isNotNull()).orderDesc(FormAssetsDao.Properties.UpdatedAt).limit(1).list();
        if (maxPostIdRow.size() > 0) {
            Date maxUpdatedAt = maxPostIdRow.get(0).getUpdatedAt();
            return DateFormatter.formatDateTimeForService(maxUpdatedAt);
        }
        return "1970-01-01 01:01:01";
    }

    public Forms getActualForm(int originalFormID, int revisionNumber) {
        QueryBuilder<Forms> qb = getDaoSession().getFormsDao().queryBuilder();
        qb.where(FormsDao.Properties.OriginalFormsId.eq(originalFormID),
                FormsDao.Properties.RevisionNumber.eq(revisionNumber));
        List<Forms> maxPostIdRow = qb.limit(1).list();
        if (maxPostIdRow.size() > 0) {
            return maxPostIdRow.get(0);
        } else {
            return null;
        }
    }

    public Forms getUnsyncFormDetails(Integer projectId, Integer formsId, int userID, int revisionNumber) {
        Log.e("OPENFORM", "getUnsyncFormDetails: projectId " + projectId + " formsId =  " + formsId + "  revisionNumber " + revisionNumber);
        QueryBuilder<Forms> qb = getDaoSession().getFormsDao().queryBuilder();
        Join<Forms, FormCategory> join = qb.join(FormsDao.Properties.FormCategoriesId, FormCategory.class, FormCategoryDao.Properties.FormCategoriesId);
        qb.where(FormsDao.Properties.OriginalFormsId.eq(formsId),
                FormsDao.Properties.RevisionNumber.eq(revisionNumber),
                new WhereCondition.StringCondition("form_deleted_at IS NULL AND forms_id IN " +
                        "(SELECT forms_id FROM project_form where deleted_at IS NULL AND pj_projects_id = " + projectId + ")"));
        String joinedTableName = join.getTablePrefix();
        String orderColName = FormCategoryDao.Properties.CategoryName.columnName;
        String rawOrder = joinedTableName + ".\"" + orderColName + "\" ASC";
        qb.orderRaw(rawOrder);
        //        String orderColName2 = FormsDao.Properties.FormName.columnName;
        //        String rawOrder2 = +".\""+orderColName2+"\" ASC";
        qb.orderAsc(FormsDao.Properties.FormName);
        List<Forms> maxPostIdRow = qb.limit(1).list();

        /*getDaoSession().getFormsDao().queryRaw("select * from forms f join form_category fc on f.form_categories_id = fc.form_categories_id " +
                "where form_deleted_at IS NULL AND forms_id IN " +
                "(SELECT forms_id FROM project_form where deleted_at IS NULL AND pj_projects_id = ?)" +
                " order by fc.category_name, f.form_name", projectId+"");*/
        if (maxPostIdRow.size() > 0) {
            return maxPostIdRow.get(0);
        }/*
        return new ArrayList<>();
        if (maxPostIdRow.size() > 0) {
            return maxPostIdRow.get(0);
        } */ else {
            return null;
        }
    }

    public Forms getFormDetails(Integer projectId, Integer formsId, int userID) {
        QueryBuilder<Forms> qb = getDaoSession().getFormsDao().queryBuilder();
        Join<Forms, FormCategory> join = qb.join(FormsDao.Properties.FormCategoriesId, FormCategory.class, FormCategoryDao.Properties.FormCategoriesId);
        qb.where(FormsDao.Properties.FormsId.eq(formsId),
                FormsDao.Properties.Publish.eq(1),
                FormsDao.Properties.FormName.like("%" + "%"),
                new WhereCondition.StringCondition("form_deleted_at IS NULL AND forms_id IN " +
                        "(SELECT forms_id FROM project_form where deleted_at IS NULL AND pj_projects_id = " + projectId + ")"));
        String joinedTableName = join.getTablePrefix();
        String orderColName = FormCategoryDao.Properties.CategoryName.columnName;
        String rawOrder = joinedTableName + ".\"" + orderColName + "\" ASC";
        qb.orderRaw(rawOrder);
        //        String orderColName2 = FormsDao.Properties.FormName.columnName;
        //        String rawOrder2 = +".\""+orderColName2+"\" ASC";
        qb.orderAsc(FormsDao.Properties.FormName);
        List<Forms> maxPostIdRow = qb.limit(1).list();

        /*getDaoSession().getFormsDao().queryRaw("select * from forms f join form_category fc on f.form_categories_id = fc.form_categories_id " +
                "where form_deleted_at IS NULL AND forms_id IN " +
                "(SELECT forms_id FROM project_form where deleted_at IS NULL AND pj_projects_id = ?)" +
                " order by fc.category_name, f.form_name", projectId+"");*/
        if (maxPostIdRow.size() > 0) {
            return maxPostIdRow.get(0);
        }/*
        return new ArrayList<>();
        if (maxPostIdRow.size() > 0) {
            return maxPostIdRow.get(0);
        } */ else {
            return null;
        }
    }

    public List<UserForms> getUserFormDueDate(Calendar cal, Integer formsId, Integer pjProjectId, Integer scheduledFormId, int usersId) {
        cal.set(Calendar.HOUR, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        Date startDate = cal.getTime();
        cal.set(Calendar.HOUR, 11);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        Date endDate = cal.getTime();

        List<UserForms> userForms = getDaoSession().getUserFormsDao().queryBuilder().where(
                UserFormsDao.Properties.PjProjectsId.eq(pjProjectId),
                UserFormsDao.Properties.FormId.eq(formsId),
                UserFormsDao.Properties.DeletedAt.isNull(),
                UserFormsDao.Properties.ScheduleFormId.eq(scheduledFormId),
                UserFormsDao.Properties.UsersId.eq(usersId),
                UserFormsDao.Properties.DueDate.between(startDate, endDate))
                .orderAsc(UserFormsDao.Properties.CreatedAt).list();
        if (userForms.size() > 0) {
            return userForms;
        }
        return new ArrayList<>();
    }

    public void saveNewFile(String name, LoginResponse loginResponse) {
        FormImage formImage = new FormImage();
        FormImageDao formImageDao = getDaoSession().getFormImageDao();
        formImage.setImageName(name);
        formImage.setIsSync(false);
        formImageDao.insert(formImage);

        TransactionLogMobileDao mPronovosSyncDataDao = getDaoSession().getTransactionLogMobileDao();

        TransactionLogMobile transactionLogMobile = new TransactionLogMobile();
        List<FormImage> formImages = getDaoSession().getFormImageDao().queryBuilder().where(
                FormImageDao.Properties.ImageName.eq(name)).list();


        transactionLogMobile.setUsersId(loginResponse.getUserDetails().getUsers_id());
        transactionLogMobile.setModule(TransactionModuleEnum.FORM_IMAGE.ordinal());
        transactionLogMobile.setStatus(SyncDataEnum.NOTSYNC.ordinal());
        transactionLogMobile.setMobileId(formImages.get(0).getId());
        transactionLogMobile.setServerId(0L);
        transactionLogMobile.setCreateDate(Calendar.getInstance().getTime());
        mPronovosSyncDataDao.save(transactionLogMobile);
        ((PronovosApplication) context.getApplicationContext()).setupAndStartWorkManager();
    }

    public FormImage getFormImage(Long mobileId) {
        List<FormImage> formImages = getDaoSession().getFormImageDao().queryBuilder().where(
                FormImageDao.Properties.Id.eq(mobileId)).list();
        if (formImages.size() > 0) {
            return formImages.get(0);
        } else return null;
    }

    public void deleteFormImage(Long id) {
        DeleteQuery<FormImage> photoDeleteQuery = getDaoSession().queryBuilder(FormImage.class)
                .where(FormImageDao.Properties.Id.eq(id))
                .buildDelete();
        photoDeleteQuery.executeDeleteWithoutDetachingEntities();
        DeleteQuery<TransactionLogMobile> pronovosSyncDataDeleteQuery = getDaoSession().queryBuilder(TransactionLogMobile.class)
                .where(TransactionLogMobileDao.Properties.MobileId.eq(id),
                        TransactionLogMobileDao.Properties.Module.eq(TransactionModuleEnum.FORM_IMAGE.ordinal()))
                .buildDelete();
        pronovosSyncDataDeleteQuery.executeDeleteWithoutDetachingEntities();

    }

    public FormImage isFileExist(String imageName) {
        List<FormImage> formImages = getDaoSession().getFormImageDao().queryBuilder().where(
                FormImageDao.Properties.ImageName.eq(imageName)).list();
        if (formImages.size() > 0) {
            return formImages.get(0);
        } else return null;
    }

    public void removeUserFormTempData(long userFormId, int users_id) {
        List<UserForms> maxPostIdRow = getDaoSession().getUserFormsDao().queryBuilder()
                .where(UserFormsDao.Properties.Id.eq(userFormId),
                        UserFormsDao.Properties.UsersId.eq(users_id)).list();
        if (maxPostIdRow.size() > 0) {
            UserForms userForms = maxPostIdRow.get(0);
            userForms.setTempSubmittedData(null);
            userForms.setDeletedImages("[]");
            getDaoSession().getUserFormsDao().insertOrReplace(userForms);
        }
    }

    /**
     * Delete Form respective to the form id.
     *
     * @param formSubmittedId The submitted form id.
     * @param userId          The user id.
     */
    public void deleteForm(long formSubmittedId, int userId) {
       /* DeleteQuery<UserForms> photoDeleteQuery = getDaoSession().queryBuilder(UserForms.class).where(
                UserFormsDao.Properties.UsersId.eq(userId),
                UserFormsDao.Properties.FormSubmitId.eq(formSubmittedId)).buildDelete();
        photoDeleteQuery.executeDeleteWithoutDetachingEntities();*/
        Query<UserForms> deleteForm = getDaoSession().queryBuilder(UserForms.class).where(
                UserFormsDao.Properties.UsersId.eq(userId),
                UserFormsDao.Properties.FormSubmitId.eq(formSubmittedId)).build();
        List<UserForms> formList = deleteForm.list();
        if (formList != null && formList.size() > 0) {
            UserForms currentForm = formList.get(0);
            AsyncSession asyncSession = getDaoSession().startAsyncSession();
            asyncSession.setListener(new AsyncOperationListener() {
                @Override
                public void onAsyncOperationCompleted(AsyncOperation operation) {
                    // do whats needed
                    EventBus.getDefault().post("DeletedForm");
                }
            });
            asyncSession.delete(currentForm);
        }


    }

    /**
     * Delete Form respective to the form submit mobileid.
     *
     * @param FormSubmitId The submitted form mobile  id.
     * @param userId       The user id.
     */
    public void deleteForm1(long FormSubmitId, int userId) {
        // Log.d("repoository", "deleteForm1: ");
        Query<UserForms> photoDeleteQuery = getDaoSession().queryBuilder(UserForms.class).where(
                UserFormsDao.Properties.UsersId.eq(userId),
                UserFormsDao.Properties.FormSubmitId.eq(FormSubmitId)).build();
        List<UserForms> formList = photoDeleteQuery.list();

        if (formList != null && formList.size() > 0) {
            UserForms currentForm = formList.get(0);
            currentForm.setDeletedAt(new Date());
            currentForm.setUpdatedUserId(userId);
            // Log.d("repoository", "deleteForm1: FormSubmitId "+FormSubmitId+" user id   = "+userId+"   "+currentForm);
            getDaoSession().getUserFormsDao().updateInTx(currentForm);
        }

        //  Log.e("AfterDelete", "deleteForm1: delted at = "+formList1.get(0).getDeletedAt());

        //getDaoSession().getUserFormsDao().update();
        EventBus.getDefault().post("DeletedForm");
    }

    public void saveUserFormData(long userFormId, int users_id, String initialFormJson, String createdFormDate) {
        List<UserForms> maxPostIdRow = getDaoSession().getUserFormsDao().queryBuilder()
                .where(UserFormsDao.Properties.Id.eq(userFormId),
                        UserFormsDao.Properties.UsersId.eq(users_id)).list();
        if (maxPostIdRow.size() > 0) {
            Log.e("DISCARD", "showChangesAlert: initialFormJson " + createdFormDate + "   *** firstUserForms ");
            maxPostIdRow.get(0).setTempSubmittedData(initialFormJson);
            maxPostIdRow.get(0).setCreatedAt(createdFormDate != null && !createdFormDate.equals("") ? DateFormatter.getDateFromString(createdFormDate) : null);

            getDaoSession().getUserFormsDao().insertOrReplace(maxPostIdRow.get(0));
        }
    }

    public void deleteTempUserForm(int userID, Long formID) {
        DeleteQuery<UserForms> photoDeleteQuery = getDaoSession().queryBuilder(UserForms.class).where(
                UserFormsDao.Properties.UsersId.eq(userID),
                UserFormsDao.Properties.Id.eq(formID)).buildDelete();
        photoDeleteQuery.executeDeleteWithoutDetachingEntities();
        Log.d("Repository", "deleteTempUserForm: formID " + formID);
    }

    /**
     * Get max updated date from Form permission according to projectID
     *
     * @return
     */
    public String getMAXFormPermissionUpdateDate(long projectId) {
        List<FormsPermission> maxPostIdRow = getDaoSession().getFormsPermissionDao()
                .queryBuilder().where(FormsPermissionDao.Properties.UpdatedAt.isNotNull(),
                        FormsPermissionDao.Properties.PjProjectsId.eq(projectId))
                .orderDesc(FormsPermissionDao.Properties.UpdatedAt).limit(1).list();
        if (maxPostIdRow.size() > 0) {
            Date maxUpdatedAt = maxPostIdRow.get(0).getUpdatedAt();
            return DateFormatter.formatDateTimeForService(maxUpdatedAt);
        }
        return "1970-01-01 01:01:01";
    }

    public void saveFromPermissionResponse(FormPermissionResponseData permissionResponse) {
        if (permissionResponse.getFormPermissions() != null && permissionResponse.getFormPermissions().size() > 0) {
            ArrayList<FormsPermission> formsPermissionArrayList = new ArrayList<>();
            for (FormPermissions permission : permissionResponse.getFormPermissions()) {
                FormsPermission formsPermission = createFormsPermission(permission);
                formsPermissionArrayList.add(formsPermission);

            }
            try {
                getDaoSession().getFormsPermissionDao().insertOrReplaceInTx(formsPermissionArrayList);
                Log.d("repoository", "saveFromPermissionResponse: formsPermissionArrayList " + formsPermissionArrayList);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    private FormsPermission createFormsPermission(FormPermissions permission) {
        FormsPermission formsPermission = new FormsPermission();
        formsPermission.setFormPermissionsId(permission.getFormPermissionsId());
        formsPermission.setFormsId(permission.getFormsId());
        formsPermission.setPjProjectsId(permission.getPjProjectsId());
        formsPermission.setUsersId(permission.getUsersId());
        formsPermission.setPermissionsId(permission.getPermissionsId());
        formsPermission.setIsActive(permission.getIsActive());
        formsPermission.setTenantId(permission.getTenantId());
        formsPermission.setCreatedAt(permission.getCreatedAt() != null && !permission.getCreatedAt().equals("") ? DateFormatter.getDateFromDateTimeString(permission.getCreatedAt()) : null);
        formsPermission.setUpdatedAt(permission.getUpdatedAt() != null && !permission.getUpdatedAt().equals("") ? DateFormatter.getDateFromDateTimeString(permission.getUpdatedAt()) : null);
        formsPermission.setDeletedAt(permission.getDeletedAt() != null && !permission.getDeletedAt().equals("") ? DateFormatter.getDateFromDateTimeString(permission.getDeletedAt()) : null);

        return formsPermission;
    }
}
