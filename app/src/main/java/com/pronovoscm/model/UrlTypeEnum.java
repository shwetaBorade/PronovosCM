package com.pronovoscm.model;

import com.pronovoscm.PronovosApplication;
import com.pronovoscm.R;

public enum UrlTypeEnum {

    PHOTOS("photos"),
    PUNCHLIST_FILES("punchlist_files"),
    PUNCHLIST_REJECT_REASON_FILE("punchlist_reject_reason_file"),
    REPORT_FILES("report_files"),
    ;
    private final String name;

    private UrlTypeEnum(String s) {
        name = s;
    }


    @Override
    public String toString() {
        return this.name;
    }
    }
