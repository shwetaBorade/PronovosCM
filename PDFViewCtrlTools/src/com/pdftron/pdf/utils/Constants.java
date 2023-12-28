//---------------------------------------------------------------------------------------
// Copyright (c) 2001-2019 by PDFTron Systems Inc. All Rights Reserved.
// Consult legal.txt regarding legal and license information.
//---------------------------------------------------------------------------------------

package com.pdftron.pdf.utils;

/**
 * A utility classes that contains constant values
 */
@SuppressWarnings("SpellCheckingInspection")
public class Constants {

    /**
     * The file is a PDF
     */
    public static final int FILE_TYPE_PDF = 0;
    /**
     * The file is a document
     */
    public static final int FILE_TYPE_DOC = 1;
    /**
     * The file is an image
     */
    public static final int FILE_TYPE_IMAGE = 2;
    /**
     * The file is a text file or markdown file
     */
    public static final int FILE_TYPE_TEXT = 3;

    /**
     * The file extension is pdf
     */
    public static final String[] FILE_NAME_EXTENSIONS_PDF = {"pdf"};
    /**
     * The file extension is a document
     */
    public static final String[] FILE_NAME_EXTENSIONS_DOC = {"docx", "doc", "pptx", "ppt", "xlsx", "xls", "md", "txt"};
    /**
     * The file extension is an office document
     */
    public static final String[] FILE_NAME_EXTENSIONS_OFFICE = {"docx", "doc", "pptx", "ppt", "xlsx", "xls"};
    /**
     * The file extension is image
     */
    public static final String[] FILE_NAME_EXTENSIONS_IMAGE = {"jpeg", "jpg", "gif", "png", "bmp", "tif", "tiff", "cbz"};
    /**
     * The file extension is a text file
     */
    public static final String[] FILE_EXTENSIONS_TEXT = {"txt", "md"};

    /**
     * The file extension is a HTML document
     */
    public static final String[] FILE_NAME_EXTENSIONS_HTML = {"html"};
    /**
     * The file extension is supported through non-streaming conversion
     */
    public static final String[] FILE_NAME_EXTENSIONS_OTHERS = {"xps", "xod", "oxps", "svg", "html"};
    /**
     * The file extension is supported through WebView conversion
     */
    public static final String[] FILE_NAME_EXTENSIONS_WEBVIEW = {"svg", "html"};
    /**
     * The file extension is valid
     */
    public static final String[] FILE_NAME_EXTENSIONS_VALID = {
            "pdf", "docx", "doc", "pptx", "ppt", "xlsx", "xls",
            "jpeg", "jpg", "gif", "png", "bmp", "cbz", "md", "txt", "tif", "tiff"
    };

    /**
     * The file extension is not PDF
     */
    public static final String[] ALL_NONPDF_FILETYPES_WILDCARD = {
            "*.docx", "*.doc", "*.pptx", "*.ppt", "*.xlsx", "*.xls",
            "*.jpeg", "*.jpg", "*.gif", "*.png", "*.bmp", "*.cbz", "*.md", "*.txt", "*.tif", "*.tiff"
    };

    /**
     * All supported file meme types
     */
    public static final String[] ALL_FILE_MIME_TYPES = {
            "application/pdf", "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "application/vnd.openxmlformats-officedocument.presentationml.presentation", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            "application/msword", "application/vnd.ms-powerpoint", "application/vnd.ms-excel",
            "image/jpeg", "image/gif", "image/png", "image/bmp", "application/x-cbr", "text/markdown", "text/plain", "image/tiff"
    };
    /**
     * Office file meme types
     */
    public static final String[] OFFICE_FILE_MIME_TYPES = {
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "application/vnd.openxmlformats-officedocument.presentationml.presentation", "application/msword",
            "application/vnd.ms-powerpoint", "application/vnd.ms-excel", "text/markdown", "text/plain",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
    };
    /**
     * Image file meme types
     */
    public static final String[] IMAGE_FILE_MIME_TYPES = {"image/jpeg", "image/gif", "image/png", "image/bmp", "application/x-cbr", "image/tiff"};

    /**
     * Google docs file meme types
     */
    public static final String[] ALL_GOOGLE_DOCS_TYPES = {
            "application/vnd.google-apps.document",
            "application/vnd.google-apps.drawing", "application/vnd.google-apps.presentation",
            "application/vnd.google-apps.spreadsheet"
    };
}
