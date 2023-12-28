package com.pronovoscm.utils;

import com.pronovoscm.BuildConfig;

public interface Constants {

    String DEVELOPMENT = "http://poc.pronovos.com/api/v5/";
    String PRODUCTION = "https://app.pronovos.com/api/v5/";

    String DEVELOPMENT_URL = "poc.pronovos.com";
    String PRODUCTION_URL = "app.pronovos.com";

    String BASE_API_URL = BuildConfig.BASE_URL;

    String SHARED_PREFERENCES_NETWORK_STATE_KEY = "NETWORK_STATE";

    String AUTHORIZATION_TOKEN = "TOKEN";
    String DEVICE_OFFLINE_DATE = "DEVICE_OFFLINE_DATE";
    String WEATHER_API = "https://api.darksky.net/";
    String DARKSKY_API_KEY = "2bb9a9ec7702db57d9ed9a8a7f3e3464";
    String INTENT_KEY_FORM_SECTIONS = "formSections";
    String INTENT_KEY_FORM_CREATED_DATE = "INTENT_KEY_FORM_CREATED_DATE";
    String INTENT_KEY_FORM_SAVE_DATE = "INTENT_KEY_FORM_SAVE_DATE";
    String INTENT_KEY_FORM_CREATED_BY = "INTENT_KEY_FORM_CREATED_BY";

    String INTENT_KEY_FORM_ID = "form_id";
    String INTENT_KEY_ORIGINAL_FORM_ID = "original_form_id";
    String INTENT_KEY_PROJECT_FORM_AREAS_ID = "PROJECT_FORM_AREAS_ID";
    String INTENT_KEY_FORM_ACTIVE_REVISION_NUMBER = "INTENT_KEY_FORM_ACTIVE_REVISION_NUMBER";

    String INTENT_KEY_ALBUM_MOBILE_ID = "albumMobileId";
    String INTENT_KEY_TRANSFER_TYPE = "INTENT_KEY_TRANSFER_TYPE";
    String INTENT_KEY_ALBUM_ID = "album_id";
    String INTENT_KEY_PROJECT_ID = "project_id";
    String INTENT_KEY_PROJECT_NAME = "project_name";
    String INTENT_KEY_PJ_PROJECT_ID = "pjProjectId";
    String INTENT_KEY_POSITION = "position";
    String INTENT_KEY_TOTAL_IMAGE_COUNT = "totalImageCount";
    int TOTAL_IMAGE_UPLOAD_COUNT = 25;
    String INTENT_KEY_PJ_PHOTOS_FOLDER_MOBILE_ID = "pjPhotosFolderMobileId";
    String DOCUMENT_FILES_PATH = "/Pronovos/Documents/";
    String WORK_DETAILS_FILES_PATH = "/Pronovos/";

    int ADAPTER_ITEM_TYPE_DOCUMENT_FOLDER = 1;
    int ADAPTER_ITEM_TYPE_DOCUMENT_FILE = 2;
    String INTENT_KEY_PROJECT_DOCUMENT_FOLDER = "INTENT_KEY_PROJECT_DOCUMENT_FOLDER";
    String INTENT_KEY_PROJECT_DOCUMENT_FOLDER_ID = "INTENT_KEY_PROJECT_DOCUMENT_FOLDER_ID";
    public static final int FILESTORAGE_REQUEST_CODE = 221;

    String INTENT_KEY_PROJECT_RFI_ID = "project_rfi_id";
    String INTENT_KEY_PROJECT_RFI = "project_rfi";
    String INTENT_KEY_PROJECT_RFI_CONTACT = "project_rfi_contact";
    String RFI_ATTACHMENTS_PATH = "/Pronovos/rfi/";

    String INTENT_KEY_PROJECT_SUBMITTALS_ID = "project_submittals_id";
    String INTENT_KEY_PROJECT_SUBMITTALS = "project_submittals";
    String INTENT_KEY_PROJECT_SUBMITTALS_CONTACT = "project_submittals_contact";
    String SUBMITTALS_ATTACHMENTS_PATH = "/Pronovos/submittals/";

    String INTENT_KEY_PJ_ISSUE_ID = "pj_issue_id";
    String INTENT_KEY_PJ_ISSUE_ID_MOBILE = "pj_issue_id_mobile";
}
