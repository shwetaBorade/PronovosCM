package com.pronovoscm.utils.backupsync;

public enum BackupFileTypeEnum {

    PHOTOS("photos"),
    PHOTOS_THUMB_IMAGE("/Pronovos/ThumbImage/"),
    PUNCHLIST_FILES("punchlist_files"),
    REPORT_FILES("report_files"),
    WORK_DETAIL_ATTACHMENTS("WorkDetailAttachments"),
    WORK_IMPACT_ATTACHMENTS("WorkImpactAttachments"),
    ALBUM_COVER_PHOTO("AlbumCoverPhoto"),
    DrawingOrgIMAGE("DrawingOrgIMAGE"),
    DRAWING_PDF_ORG_FILE("DRAWING_PDF_ORG_FILE"),
    DRAWING_LIST_THUMB_IMAGE("DrawingListIMAGE"),
    PROJECT_SHOWCASE_IMAGE("PROJECT_SHOWCASE_IMAGE"),
    FORM_ATTACHMENTS("FORM_ATTACHMENTS"),
    EQUIPMENT_REGION_ATTACHMENTS("EQUIPMENT_REGION_ATTACHMENTS");
    private final String name;

    private BackupFileTypeEnum(String s) {
        name = s;
    }


    @Override
    public String toString() {
        return this.name;
    }
}

