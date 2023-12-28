package com.pronovoscm.component;

import com.pronovoscm.CameraControls;
import com.pronovoscm.PronovosApplication;
import com.pronovoscm.activity.AlbumsPhotoActivity;
import com.pronovoscm.activity.CreateTransferDisputeMessage;
import com.pronovoscm.activity.CreateTransferEquipmentActivity;
import com.pronovoscm.activity.CreateTransfersActivity;
import com.pronovoscm.activity.DailyCrewReportActivity;
import com.pronovoscm.activity.DailyEmailActivity;
import com.pronovoscm.activity.DailyReportActivity;
import com.pronovoscm.activity.DailyWeatherReportActivity;
import com.pronovoscm.activity.DailyWorkDetailsActivity;
import com.pronovoscm.activity.DailyWorkImpactActivity;
import com.pronovoscm.activity.DrawingListTabActivity;
import com.pronovoscm.activity.DrawingPDFActivity;
import com.pronovoscm.activity.EquipmentActivity;
import com.pronovoscm.activity.EquipmentDetailsActivity;
import com.pronovoscm.activity.FieldPaperWorkActivity;
import com.pronovoscm.activity.ForgetPasswordActivity;
import com.pronovoscm.activity.FormEmailActivity;
import com.pronovoscm.activity.FullPhotoActivity;
import com.pronovoscm.activity.InventoryActivity;
import com.pronovoscm.activity.InventorySubcategoryActivity;
import com.pronovoscm.activity.issue_tracking.AddIssueActivity;
import com.pronovoscm.activity.issue_tracking.IssueTrackingListActivity;
import com.pronovoscm.activity.LoginActivity;
import com.pronovoscm.activity.PhotoDetailActivity;
import com.pronovoscm.activity.PhotoEditActivity;
import com.pronovoscm.activity.ProjectAlbumActivity;
import com.pronovoscm.activity.ProjectDocumentsActivity;
import com.pronovoscm.activity.ProjectDocumentsFilesActivity;
import com.pronovoscm.activity.ProjectDrawingActivity;
import com.pronovoscm.activity.ProjectFormActivity;
import com.pronovoscm.activity.ProjectFormDetailActivity;
import com.pronovoscm.activity.ProjectFormUserActivity;
import com.pronovoscm.activity.ProjectOptionsActivity;
import com.pronovoscm.activity.ProjectOverviewDetailsActivity;
import com.pronovoscm.activity.ProjectsActivity;
import com.pronovoscm.activity.PronovosCameraActivity;
import com.pronovoscm.activity.PunchListActivity;
import com.pronovoscm.activity.PunchlistPhotoEditFragment;
import com.pronovoscm.activity.RfiDetailActivity;
import com.pronovoscm.activity.RfiListActivity;
import com.pronovoscm.activity.SubmittalDetailActivity;
import com.pronovoscm.activity.SubmittalsListActivity;
import com.pronovoscm.activity.TransferLogActivity;
import com.pronovoscm.activity.TransferLogDetailsActivity;
import com.pronovoscm.activity.TransferLogFilterActivity;
import com.pronovoscm.activity.TransferOverviewActivity;
import com.pronovoscm.activity.TransferOverviewDetailsActivity;
import com.pronovoscm.activity.issue_tracking.ViewIssueDetailActivity;
import com.pronovoscm.adapter.AlbumAdapter;
import com.pronovoscm.adapter.AssigneeSubmittalsAttachmentAdapter;
import com.pronovoscm.adapter.EquipmentDetailsAdapter;
import com.pronovoscm.adapter.InventoryCategoryAdapter;
import com.pronovoscm.adapter.InventorySubCategoryAdapter;
import com.pronovoscm.adapter.ProjectFormCalendarAdapter;
import com.pronovoscm.adapter.ProjectUnSyncFormAdapter;
import com.pronovoscm.adapter.PunchListAttachmentAdapter;
import com.pronovoscm.adapter.RfiDetailsAttachmentAdapter;
import com.pronovoscm.adapter.RfiRepliesAdapter;
import com.pronovoscm.adapter.SubmittalsDetailsAttachmentAdapter;
import com.pronovoscm.adapter.TransferEquipmentsAdapter;
import com.pronovoscm.adapter.TransferLogsAdapter;
import com.pronovoscm.adapter.WorkDetailsAttachmentAdapter;
import com.pronovoscm.adapter.WorkDetailsListAdapter;
import com.pronovoscm.adapter.WorkImpactListAdapter;
import com.pronovoscm.broadcastreceivers.NetworkStateReceiver;
import com.pronovoscm.fragments.CategoryFragment;
import com.pronovoscm.fragments.CreateTransferAddEquipmentFragment;
import com.pronovoscm.fragments.CreateTransferInfoFragment;
import com.pronovoscm.fragments.CrewFragment;
import com.pronovoscm.fragments.DrawingListFragment;
import com.pronovoscm.fragments.EquipmentDetailFragment;
import com.pronovoscm.fragments.LookUpEquipmentFragment;
import com.pronovoscm.fragments.ProjectFormCalendarFragment;
import com.pronovoscm.fragments.ProjectFormListFragment;
import com.pronovoscm.fragments.ProjectInHouseResourcesFragment;
import com.pronovoscm.fragments.ProjectInfoFragment;
import com.pronovoscm.fragments.ProjectSubcontractorsFragment;
import com.pronovoscm.fragments.ProjectTeamFragment;
import com.pronovoscm.fragments.ProjectUnSyncFormFragment;
import com.pronovoscm.fragments.PunchlistFragment;
import com.pronovoscm.fragments.TransferLogPickupFragment;
import com.pronovoscm.fragments.TransferOverviewDetailFragment;
import com.pronovoscm.fragments.TransferOverviewFragment;
import com.pronovoscm.fragments.UploadPhotoFragment;
import com.pronovoscm.fragments.WorkDetailFragment;
import com.pronovoscm.fragments.WorkImpactFragment;
import com.pronovoscm.galleryimagepicker.GalleryPickerActivity;
import com.pronovoscm.modules.ApplicationModule;
import com.pronovoscm.persistence.DataBaseHelper;
import com.pronovoscm.services.OfflineDataSynchronizationService;
import com.pronovoscm.utils.DrawingWorker;
import com.pronovoscm.utils.TransactionWorker;
import com.pronovoscm.utils.dialogs.AddCrewDialog;
import com.pronovoscm.utils.dialogs.AddFolderDialog;
import com.pronovoscm.utils.dialogs.AlbumsDialog;
import com.pronovoscm.utils.dialogs.AutoSycFolderDialog;
import com.pronovoscm.utils.dialogs.CCDialog;
import com.pronovoscm.utils.dialogs.EquipmentCategoryDialog;
import com.pronovoscm.utils.dialogs.EquipmentRegionDialog;
import com.pronovoscm.utils.dialogs.EquipmentSubCategoryDialog;
import com.pronovoscm.utils.dialogs.ForgotPasswordDialog;
import com.pronovoscm.utils.dialogs.ObjectDialog;
import com.pronovoscm.utils.dialogs.PunchListFilterDialog;
import com.pronovoscm.utils.dialogs.RfiListFilterDialog;
import com.pronovoscm.utils.dialogs.SubmittalListFilterDialog;
import com.pronovoscm.utils.dialogs.TagsDialog;
import com.pronovoscm.utils.dialogs.ToDialog;
import com.pronovoscm.utils.dialogs.TransferLocationDialog;
import com.pronovoscm.utils.dialogs.WeatherConditionDialog;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {ApplicationModule.class})
public interface AppComponent {
    void inject(PronovosApplication pronovosApplication);

    void inject(LoginActivity loginActivity);

    void inject(ProjectsActivity projectsActivity);

    void inject(ForgetPasswordActivity forgetPasswordActivity);

    void inject(ProjectAlbumActivity projectAlbumActivity);


    void inject(AlbumsPhotoActivity albumsPhotoActivity);

    void inject(AlbumAdapter albumAdapter);

    void inject(FullPhotoActivity fullPhotoActivity);

    void inject(AddFolderDialog addFolderDialog);

    void inject(OfflineDataSynchronizationService offlineDataSynchronizationService);

    void inject(TagsDialog tagsDialog);

    void inject(AlbumsDialog albumsDialog);

    void inject(UploadPhotoFragment uploadPhotoFragment);

    void inject(PhotoDetailActivity photoDetailActivity);

    void inject(CameraControls cameraControls);

    void inject(PronovosCameraActivity mainActivity);

    void inject(NetworkStateReceiver networkStateReceiver);

    void inject(PhotoEditActivity photoEditActivity);

    void inject(ProjectOptionsActivity projectOptionsActivity);

    void inject(ProjectDrawingActivity projectDrawingActivity);

//    void inject(DrawingListActivity drawingListActivity);

    void inject(AutoSycFolderDialog autoSycFolderDialog);

    void inject(DrawingPDFActivity drawingPDFActivity);

    void inject(PunchListActivity punchListActivity);

    void inject(FieldPaperWorkActivity fieldPaperWorkActivity);

    void inject(DailyReportActivity dailyReportActivity);

    void inject(DailyWeatherReportActivity dailyWeatherReportActivity);

    void inject(DailyCrewReportActivity dailyCrewReport);

    void inject(WeatherConditionDialog weatherConditionDialog);

    void inject(DailyWorkDetailsActivity dailyWorkDetailsActivity);

    void inject(AddCrewDialog addCrewDialog);

    void inject(WorkDetailsListAdapter workDetailsListAdapter);

//    void inject(WorkDetailsDialog workDetailsDialog);

    void inject(DailyEmailActivity dailyEmailActivity);

    void inject(ToDialog toDialog);

    void inject(CCDialog ccDialog);

    void inject(DailyWorkImpactActivity dailyWorkImpactActivity);

//    void inject(com.pronovoscm.presentation.album.ProjectAlbumActivity projectAlbumActivity);

//    void inject(WorkImpactDialog workImpactDialog);

    void inject(WorkDetailsAttachmentAdapter workDetailsAttachmentAdapter);

    void inject(PunchListAttachmentAdapter punchListAttachmentAdapter);

    void inject(PunchListFilterDialog punchListFilterDialog);
    void inject(PunchlistPhotoEditFragment punchlistPhotoEditFragment);

    void inject(ForgotPasswordDialog forgotPasswordDialog);

    void inject(CrewFragment crewFragment);

    void inject(WorkDetailFragment workDetailFragment);

    void inject(WorkImpactFragment workImpactFragment);

    void inject(PunchlistFragment punchlistFragment);

    void inject(DrawingListTabActivity drawingListTabActivity);

    void inject(DrawingListFragment drawingListFragment);

    void inject(WorkImpactListAdapter workImpactListAdapter);

    void inject(TransactionWorker transactionWorker);

    void inject(DrawingWorker drawingWorker);

    void inject(DataBaseHelper dataBaseHelper);

    void inject(EquipmentActivity equipmentActivity);

    void inject(InventoryActivity inventoryActivity);

    void inject(InventorySubcategoryActivity inventorySubcategoryActivity);

    void inject(EquipmentDetailsActivity equipmentDetailsActivity);

    void inject(EquipmentDetailFragment equipmentDetailFragment);

    void inject(InventoryCategoryAdapter inventoryCategoryAdapter);

    void inject(InventorySubCategoryAdapter inventorySubCategoryAdapter);

    void inject(EquipmentDetailsAdapter equipmentDetailsAdapter);

    void inject(CategoryFragment categoryFragment);

    void inject(TransferOverviewActivity transferOverviewActivity);

    void inject(TransferOverviewFragment transferOverviewFragment);

    void inject(TransferOverviewDetailsActivity transferOverviewDetailsActivity);

    void inject(CreateTransfersActivity createTransfersActivity);

    void inject(TransferLocationDialog transferLocationDialog);

    void inject(CreateTransferEquipmentActivity createTransferEquipmentActivity);

    void inject(CreateTransferAddEquipmentFragment createTransferAddEquipmentFragment);

    void inject(LookUpEquipmentFragment lookUpEquipmentFragment);

    void inject(EquipmentCategoryDialog equipmentCategoryDialog);

    void inject(EquipmentSubCategoryDialog equipmentSubCategoryDialog);

    void inject(EquipmentRegionDialog equipmentRegionDialog);

    void inject(TransferOverviewDetailFragment transferOverviewDetailFragment);

    void inject(TransferEquipmentsAdapter transferEquipmentsAdapter);

    void inject(CreateTransferInfoFragment createTransferInfoFragment);

    void inject(CreateTransferDisputeMessage createTransferDisputeMessage);

    void inject(TransferLogActivity transferLogActivity);

    void inject(TransferLogsAdapter transferLogsAdapter);

    void inject(TransferLogFilterActivity transferLogFilterActivity);

    void inject(TransferLogDetailsActivity transferLogDetailsActivity);

    void inject(TransferLogPickupFragment transferLogPickupFragment);

    void inject(ProjectOverviewDetailsActivity projectOverviewDetailsActivity);

    void inject(ProjectTeamFragment projectTeamFragment);

    void inject(ProjectSubcontractorsFragment projectSubcontractorsFragment);

    void inject(ProjectInHouseResourcesFragment projectInHouseResourcesFragment);

    void inject(ProjectInfoFragment projectInfoFragment);

    void inject(ProjectFormActivity projectFormActivity);

    void inject(ProjectFormListFragment projectFormListFragment);

    void inject(ProjectFormCalendarFragment projectFormCalendarFragment);

    void inject(ProjectFormUserActivity projectFormUserActivity);

    void inject(ProjectFormDetailActivity projectFormDetailActivity);

    void inject(FormEmailActivity formEmailActivity);

    void inject(ProjectFormCalendarAdapter projectFormCalendarAdapter);

    void inject(ObjectDialog objectDialog);

    void inject(ProjectUnSyncFormFragment projectUnSyncFormFragment);

    void inject(ProjectUnSyncFormAdapter projectUnSyncFormAdapter);

    void inject(GalleryPickerActivity galleryPickerActivity);

    void inject(ProjectDocumentsActivity projectDocumentsActivity);

    void inject(ProjectDocumentsFilesActivity projectDocumentsFilesActivity);

    void inject(RfiListActivity rfiListActivity);

    void inject(RfiDetailActivity rfiListActivity);

    void inject(RfiRepliesAdapter attachmentAdapter);

    void inject(RfiDetailsAttachmentAdapter attachmentAdapter);

    void inject(RfiListFilterDialog rfiListFilterDialog);

    void inject(SubmittalsListActivity submittalsListActivity);

    void inject(SubmittalDetailActivity submittalDetailActivity);

    void inject(SubmittalListFilterDialog submittalListFilterDialog);

    void inject(SubmittalsDetailsAttachmentAdapter attachmentAdapter);

    void inject(AssigneeSubmittalsAttachmentAdapter attachmentAdapter);

    void inject(IssueTrackingListActivity issueTrackingListActivity);

    void inject(ViewIssueDetailActivity viewIssueDetailActivity);

    void inject(AddIssueActivity addIssueActivity);

}
