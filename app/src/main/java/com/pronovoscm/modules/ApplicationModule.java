package com.pronovoscm.modules;

import android.content.Context;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pronovoscm.PronovosApplication;
import com.pronovoscm.api.CrewReportApi;
import com.pronovoscm.api.DarkSkyApi;
import com.pronovoscm.api.DrawingAnnotationApi;
import com.pronovoscm.api.EmailReportApi;
import com.pronovoscm.api.FieldPaperWorkApi;
import com.pronovoscm.api.FileUploadAPI;
import com.pronovoscm.api.InventoryApi;
import com.pronovoscm.api.LoginApi;
import com.pronovoscm.api.PDFFileDownloadAPI;
import com.pronovoscm.api.ProjectDrawingFolderApi;
import com.pronovoscm.api.ProjectDrawingListApi;
import com.pronovoscm.api.ProjectFormApi;
import com.pronovoscm.api.ProjectOverviewApi;
import com.pronovoscm.api.ProjectsApi;
import com.pronovoscm.api.PunchListApi;
import com.pronovoscm.api.TransferLogApi;
import com.pronovoscm.api.TransferOverviewApi;
import com.pronovoscm.api.UpdatePhotoDetailApi;
import com.pronovoscm.api.WeatherReportApi;
import com.pronovoscm.api.WorkDetailsApi;
import com.pronovoscm.api.WorkImpactApi;
import com.pronovoscm.data.CrewReportProvider;
import com.pronovoscm.data.DrawingAnnotationProvider;
import com.pronovoscm.data.EmailProvider;
import com.pronovoscm.data.FieldPaperWorkProvider;
import com.pronovoscm.data.FileUploadProvider;
import com.pronovoscm.data.InventoryProvider;
import com.pronovoscm.data.LoginProvider;
import com.pronovoscm.data.NetworkStateProvider;
import com.pronovoscm.data.PDFFileDownloadProvider;
import com.pronovoscm.data.ProjectDrawingFolderProvider;
import com.pronovoscm.data.ProjectDrawingListProvider;
import com.pronovoscm.data.ProjectFormProvider;
import com.pronovoscm.data.issuetracking.ProjectIssueTrackingProvider;
import com.pronovoscm.data.ProjectOverviewProvider;
import com.pronovoscm.data.ProjectsProvider;
import com.pronovoscm.data.PunchListProvider;
import com.pronovoscm.data.TransferLogProvider;
import com.pronovoscm.data.TransferOverviewProvider;
import com.pronovoscm.data.UpdatePhotoDetailsProvider;
import com.pronovoscm.data.WeatherReportProvider;
import com.pronovoscm.data.WorkDetailsProvider;
import com.pronovoscm.data.WorkImpactProvider;
import com.pronovoscm.persistence.repository.BackupSyncRepository;
import com.pronovoscm.persistence.repository.CrewReportRepository;
import com.pronovoscm.persistence.repository.DrawingListRepository;
import com.pronovoscm.persistence.repository.EquipementInventoryRepository;
import com.pronovoscm.persistence.repository.FieldPaperWorkRepository;
import com.pronovoscm.persistence.repository.OfflineEventRepository;
import com.pronovoscm.persistence.repository.ProjectDocumentsRepository;
import com.pronovoscm.persistence.repository.ProjectFormRepository;
import com.pronovoscm.persistence.repository.ProjectIssueTrackingRepository;
import com.pronovoscm.persistence.repository.ProjectOverviewRepository;
import com.pronovoscm.persistence.repository.ProjectRfiRepository;
import com.pronovoscm.persistence.repository.ProjectSubmittalsRepository;
import com.pronovoscm.persistence.repository.PunchListRepository;
import com.pronovoscm.persistence.repository.WeatherReportRepository;
import com.pronovoscm.persistence.repository.WorkDetailsRepository;
import com.pronovoscm.persistence.repository.WorkImpactRepository;
import com.pronovoscm.utils.Constants;

import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

@Module
public class ApplicationModule {
    private static final String TAG = ApplicationModule.class.getName();
    private final PronovosApplication pronovosApplication;
    private String url;

    public ApplicationModule(PronovosApplication pronovosApplication, String url) {
        super();
        this.pronovosApplication = pronovosApplication;
        this.url = url;
    }

    @Provides
    @Singleton
    Context provideAppContext() {
        return pronovosApplication.getApplicationContext();
    }

    @Provides
    @Singleton
    Retrofit retrofit() {
        //YYYY-MM-DDThh:mm:ssZ
//        GsonBuilder gsonBuilder = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");

       /* dailyTaskAssignActualBean.setActualStartTime(
                new SimpleDateFormat("MM-dd-yyyy hh:mm a").parse(new SimpleDateFormat("MM-dd-yyyy hh:mm a")
                                                                         .format(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                                                                                         .parse(dailyTaskAssignModel.getActualStartTime()))));*/

        GsonBuilder gsonBuilder = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE);
//        gsonBuilder.registerTypeAdapter(Date.class, new DateTypeAdapter ());
        Gson gson = gsonBuilder.create();

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.connectTimeout(20, TimeUnit.SECONDS);
        httpClient.readTimeout(120, TimeUnit.SECONDS);

//        String url = Constants.BASE_API_URL;
        if (!url.endsWith("/")) {
            url = url.concat("/");
        }

        return new Retrofit.Builder().baseUrl(url)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(httpClient.build()).build();
    }

    @Provides
    @Singleton
    NetworkStateProvider networkStateProvider(Context context) {
        return new NetworkStateProvider(context);
    }

    @Provides
    @Singleton
    LoginApi loginRegistrationApi(Retrofit retrofit) {
        return retrofit.create(LoginApi.class);
    }

    @Provides
    @Singleton
    LoginProvider loginProvider(NetworkStateProvider networkStateProvider, LoginApi loginApi) {
        return new LoginProvider(networkStateProvider, loginApi);
    }

    @Provides
    @Singleton
    UpdatePhotoDetailApi updatePhotoDetailApi(Retrofit retrofit) {
        return retrofit.create(UpdatePhotoDetailApi.class);
    }

    @Provides
    @Singleton
    UpdatePhotoDetailsProvider updatePhotoDetailsProvider(NetworkStateProvider networkStateProvider, UpdatePhotoDetailApi updatePhotoDetailApi) {
        return new UpdatePhotoDetailsProvider(networkStateProvider, updatePhotoDetailApi, pronovosApplication.getDaoSession());
    }


    @Provides
    @Singleton
    ProjectDrawingFolderApi projectDrawingApi(Retrofit retrofit) {
        return retrofit.create(ProjectDrawingFolderApi.class);
    }


    @Provides
    @Singleton
    ProjectDrawingFolderProvider projectDrawingProvider(NetworkStateProvider networkStateProvider, ProjectDrawingFolderApi projectDrawingApi) {
        return new ProjectDrawingFolderProvider(networkStateProvider, projectDrawingApi, pronovosApplication.getDaoSession());
    }

    @Provides
    @Singleton
    DrawingListRepository drawingListRepository(Context context) {
        return new DrawingListRepository(pronovosApplication.getDaoSession(), context);
    }

    @Provides
    @Singleton
    ProjectDrawingListApi mProjectDrawingListApi(Retrofit retrofit) {
        return retrofit.create(ProjectDrawingListApi.class);
    }


    @Provides
    @Singleton
    ProjectDrawingListProvider mProjectDrawingListProvider(NetworkStateProvider networkStateProvider, DrawingListRepository drawingListRepository, ProjectDrawingListApi projectDrawingApi) {
        return new ProjectDrawingListProvider(networkStateProvider, drawingListRepository, projectDrawingApi, pronovosApplication.getDaoSession());
    }


    @Provides
    @Singleton
    ProjectsApi projectApi(Retrofit retrofit) {
        return retrofit.create(ProjectsApi.class);
    }

    @Provides
    @Singleton
    ProjectsProvider projectsProvider(NetworkStateProvider networkStateProvider, ProjectsApi projectsApi, FieldPaperWorkRepository fieldPaperWorkRepository,
                                      ProjectDocumentsRepository projectDocumentsRepository, ProjectRfiRepository projectRfiRepository, ProjectSubmittalsRepository projectSubmittalsRepository) {
        return new ProjectsProvider(projectsApi, pronovosApplication.getDaoSession(), fieldPaperWorkRepository, projectDocumentsRepository, projectRfiRepository, projectSubmittalsRepository);
    }

    @Provides
    @Singleton
    DrawingAnnotationApi drawingAnnotationApitApi(Retrofit retrofit) {
        return retrofit.create(DrawingAnnotationApi.class);
    }

    @Provides
    @Singleton
    DrawingAnnotationProvider drawingAnnotationProvider(NetworkStateProvider networkStateProvider, DrawingAnnotationApi drawingAnnotationApi, DrawingListRepository drawingListRepository) {
        return new DrawingAnnotationProvider(networkStateProvider, drawingAnnotationApi, pronovosApplication.getDaoSession(), drawingListRepository);
    }

    @Provides
    @Singleton
    FileUploadAPI fileUploadAPI(Retrofit retrofit) {
        return retrofit.create(FileUploadAPI.class);
    }

    @Provides
    @Singleton
    FileUploadProvider fileUploadProvider(NetworkStateProvider networkStateProvider, FileUploadAPI fileUploadAPI) {
        return new FileUploadProvider(networkStateProvider, fileUploadAPI, pronovosApplication, pronovosApplication.getDaoSession());
    }

    @Provides
    @Singleton
    PDFFileDownloadAPI PDFFileDownload(Retrofit retrofit) {
        return retrofit.create(PDFFileDownloadAPI.class);
    }

    @Provides
    @Singleton
    PDFFileDownloadProvider pdfFileDownloadProvider() {
        return new PDFFileDownloadProvider(pronovosApplication);
    }

    @Provides
    @Singleton
    OfflineEventRepository offlineEventRepository() {
        return new OfflineEventRepository(pronovosApplication.getDaoSession());
    }


    @Provides
    @Singleton
    FieldPaperWorkApi fieldPaperWorkApiProvider(Retrofit retrofit) {
        return retrofit.create(FieldPaperWorkApi.class);
    }

    @Provides
    @Singleton
    FieldPaperWorkProvider fieldPaperWorkProvider(NetworkStateProvider networkStateProvider, FieldPaperWorkApi fieldPaperWorkApi, FieldPaperWorkRepository fieldPaperWorkRepository) {
        return new FieldPaperWorkProvider(networkStateProvider, fieldPaperWorkApi, pronovosApplication.getDaoSession(), fieldPaperWorkRepository);
    }


    @Provides
    @Singleton
    FieldPaperWorkRepository fieldPaperWorkRepository(Context context) {
        return new FieldPaperWorkRepository(pronovosApplication.getDaoSession(), context);
    }


    @Provides
    @Singleton
    PunchListApi punchListApiProvider(Retrofit retrofit) {
        return retrofit.create(PunchListApi.class);
    }


    @Provides
    @Singleton
    PunchListRepository punchListRepository(Context context) {
        return new PunchListRepository(pronovosApplication.getDaoSession(), context);
    }

    @Provides
    @Singleton
    BackupSyncRepository backupRepository(Context context) {
        return new BackupSyncRepository(pronovosApplication.getDaoSession(), context);
    }

    @Provides
    @Singleton
    PunchListProvider punchListProvider(NetworkStateProvider networkStateProvider, PunchListApi punchListApi, PunchListRepository punchListRepository, FileUploadProvider fileUploadProvider) {
        return new PunchListProvider(networkStateProvider, punchListApi, pronovosApplication.getDaoSession(), punchListRepository, fileUploadProvider);
    }


    @Provides
    @Singleton
    WeatherReportApi weatherReportApiProvider(Retrofit retrofit) {
        return retrofit.create(WeatherReportApi.class);
    }

    @Provides
    @Singleton
    WeatherReportProvider weatherReportProvider(NetworkStateProvider networkStateProvider, WeatherReportApi weatherReportApi, WeatherReportRepository weatherReportRepository) {
        return new WeatherReportProvider(networkStateProvider, weatherReportApi, pronovosApplication.getDaoSession(), weatherReportRepository);
    }


    @Provides
    @Singleton
    WeatherReportRepository weatherReportRepository(Context context) {
        return new WeatherReportRepository(pronovosApplication.getDaoSession(), context);
    }

    @Provides
    @Singleton
    CrewReportApi crewReportApi(Retrofit retrofit) {
        return retrofit.create(CrewReportApi.class);
    }

    @Provides
    @Singleton
    CrewReportProvider crewReportProvider(NetworkStateProvider networkStateProvider, CrewReportApi crewReportApi, CrewReportRepository weatherReportRepository) {
        return new CrewReportProvider(networkStateProvider, crewReportApi, pronovosApplication.getDaoSession(), weatherReportRepository);
    }


    @Provides
    @Singleton
    CrewReportRepository crewReportRepository(Context context) {
        return new CrewReportRepository(pronovosApplication.getDaoSession(), context);
    }


    @Provides
    @Singleton
    EquipementInventoryRepository equipementInventoryRepository(Context context) {
        return new EquipementInventoryRepository(pronovosApplication.getDaoSession(), context);
    }

    @Provides
    @Singleton
    EmailReportApi emailReportApi(Retrofit retrofit) {
        return retrofit.create(EmailReportApi.class);
    }

    @Provides
    @Singleton
    InventoryApi inventoryApi(Retrofit retrofit) {
        return retrofit.create(InventoryApi.class);
    }

    @Provides
    @Singleton
    TransferOverviewApi transferOverviewApi(Retrofit retrofit) {
        return retrofit.create(TransferOverviewApi.class);
    }

    @Provides
    @Singleton
    TransferLogApi transferLogApi(Retrofit retrofit) {
        return retrofit.create(TransferLogApi.class);
    }

    @Provides
    @Singleton
    EmailProvider emailProvider(NetworkStateProvider networkStateProvider, EmailReportApi emailReportApi) {
        return new EmailProvider(networkStateProvider, emailReportApi, pronovosApplication.getDaoSession());
    }

    @Provides
    @Singleton
    InventoryProvider inventoryProvider(NetworkStateProvider networkStateProvider, InventoryApi inventoryApi, EquipementInventoryRepository equipementInventoryRepository) {
        return new InventoryProvider(networkStateProvider, inventoryApi, pronovosApplication.getDaoSession(), equipementInventoryRepository);
    }

    @Provides
    @Singleton
    TransferOverviewProvider transferOverviewProvider(NetworkStateProvider networkStateProvider, TransferOverviewApi transferOverviewApi, EquipementInventoryRepository equipementInventoryRepository) {
        return new TransferOverviewProvider(networkStateProvider, transferOverviewApi, pronovosApplication.getDaoSession(), equipementInventoryRepository);
    }

    @Provides
    @Singleton
    TransferLogProvider transferLogProvider(NetworkStateProvider networkStateProvider, TransferLogApi transferOverviewApi, EquipementInventoryRepository equipementInventoryRepository) {
        return new TransferLogProvider(networkStateProvider, transferOverviewApi, equipementInventoryRepository);
    }

    @Provides
    @Singleton
    WorkDetailsApi workDetailsApi(Retrofit retrofit) {
        return retrofit.create(WorkDetailsApi.class);
    }

    @Provides
    @Singleton
    WorkDetailsProvider workDetailsProvider(NetworkStateProvider networkStateProvider, WorkDetailsApi workDetailsApi, WorkDetailsRepository workDetailsRepository, FileUploadProvider uploadProvider) {
        return new WorkDetailsProvider(networkStateProvider, workDetailsApi, pronovosApplication.getDaoSession(), workDetailsRepository, uploadProvider);
    }


    @Provides
    @Singleton
    WorkDetailsRepository workDetailsRepository(Context context) {
        return new WorkDetailsRepository(pronovosApplication.getDaoSession(), context);
    }

    @Provides
    @Singleton
    WorkImpactApi workImpactApi(Retrofit retrofit) {
        return retrofit.create(WorkImpactApi.class);
    }

    @Provides
    @Singleton
    WorkImpactProvider workImpactProvider(NetworkStateProvider networkStateProvider, WorkImpactApi workImpactApi, WorkImpactRepository workImpactRepository, FileUploadProvider fileUploadProvider) {
        return new WorkImpactProvider(networkStateProvider, workImpactApi, pronovosApplication.getDaoSession(), workImpactRepository, fileUploadProvider);
    }


    @Provides
    @Singleton
    WorkImpactRepository workImpactRepository(Context context) {
        return new WorkImpactRepository(pronovosApplication.getDaoSession(), context);
    }


    @Provides
    @Singleton
    ProjectOverviewApi projectOverviewApi(Retrofit retrofit) {
        return retrofit.create(ProjectOverviewApi.class);
    }

    @Provides
    @Singleton
    ProjectOverviewProvider projectOverviewProvider(NetworkStateProvider networkStateProvider, ProjectOverviewApi projectOverviewApi, ProjectOverviewRepository projectOverviewRepository) {
        return new ProjectOverviewProvider(networkStateProvider, projectOverviewApi, projectOverviewRepository);
    }


    @Provides
    @Singleton
    ProjectOverviewRepository projectOverviewRepository(Context context) {
        return new ProjectOverviewRepository(pronovosApplication.getDaoSession(), context);
    }

    @Provides
    @Singleton
    ProjectFormApi projectFormApi(Retrofit retrofit) {
        return retrofit.create(ProjectFormApi.class);
    }

    @Provides
    @Singleton
    ProjectFormProvider projectFormProvider(NetworkStateProvider networkStateProvider, ProjectFormApi projectprojectFormApi, ProjectFormRepository projectOverviewRepository) {
        return new ProjectFormProvider(networkStateProvider, projectprojectFormApi, projectOverviewRepository);
    }

    @Provides
    @Singleton
    ProjectDocumentsRepository projectDocumentsRepository(Context context) {
        return new ProjectDocumentsRepository(pronovosApplication.getDaoSession(), context);
    }

    @Provides
    @Singleton
    ProjectRfiRepository projectRfiRepository(Context context) {
        return new ProjectRfiRepository(pronovosApplication.getDaoSession(), context);
    }

    @Provides
    @Singleton
    ProjectSubmittalsRepository projectSubmittalsRepository(Context context) {
        return new ProjectSubmittalsRepository(pronovosApplication.getDaoSession(), context);
    }


    @Provides
    @Singleton
    ProjectFormRepository projectFormRepository(Context context) {
        return new ProjectFormRepository(pronovosApplication.getDaoSession(), context);
    }

    @Provides
    @Singleton
    ProjectIssueTrackingRepository projectIssueTrackingRepository(Context context) {
        return new ProjectIssueTrackingRepository(context, pronovosApplication.getDaoSession().getImpactsAndRootCauseCacheDao(), pronovosApplication.getDaoSession().getProjectIssuesCacheDao(), pronovosApplication.getDaoSession().getProjectIssueImpactsAndCausesCacheDao(), pronovosApplication.getDaoSession().getProjectIssuesItemBreakdownCacheDao(), pronovosApplication.getDaoSession().getTransactionLogMobileDao(),pronovosApplication.getDaoSession().getIssueTrackingSectionCacheDao(),pronovosApplication.getDaoSession().getIssueTrackingItemsCacheDao(),pronovosApplication.getDaoSession().getIssueTrackingItemTypesCacheDao(),pronovosApplication.getDaoSession().getIssueTrackingCustomFieldsCacheDao());
    }

    @Provides
    @Singleton
    ProjectIssueTrackingProvider projectIssueTrackingProvider(Context context, ProjectsApi projectsApi, ProjectIssueTrackingRepository projectIssueTrackingRepository) {
        return new ProjectIssueTrackingProvider(context, projectsApi, projectIssueTrackingRepository);
    }

    @Provides
    @Singleton
    DarkSkyApi darkSkyApiProvider() {
        GsonBuilder gsonBuilder = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE);
//        gsonBuilder.registerTypeAdapter(Date.class, new DateTypeAdapter ());
        Gson gson = gsonBuilder.create();
//        );

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
//        httpClient.connectTimeout(20, TimeUnit.SECONDS);
//        httpClient.readTimeout(120, TimeUnit.SECONDS);

//        String url = Constants.BASE_API_URL;
//        if (!url.endsWith("/")) {
//            url = url.concat("/");
//        }

        return new Retrofit.Builder().baseUrl(Constants.WEATHER_API)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(httpClient.build()).build().create(DarkSkyApi.class);
    }

}
