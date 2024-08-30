package com.systex.jbranch.platform.common.report.factory;

public enum ReportFormat {
    XLS("xls", "xls"),
    DOC("doc", "doc"),
    PPT("ppt", "ppt"),
    PDF("pdf", "pdf"),
    CSV("CSV", "csv"),
    MSWORD_WML("doc", "doc"),
    MSWORD_DOCX("doc", "doc"),
    LINE_MODE("json", "json");

// ------------------------------ FIELDS ------------------------------

    private String type;
    private String ext;

// --------------------------- CONSTRUCTORS ---------------------------

    ReportFormat(String type, String ext) {
        this.type = type;
        this.ext = ext;
    }

// --------------------- GETTER / SETTER METHODS ---------------------

    public String getExt() {
        return ext;
    }

    public String getType() {
        return type;
    }

// ------------------------ CANONICAL METHODS ------------------------

    @Override
    public String toString() {
        return "com.systex.jbranch.platform.common.report.factory.ReportFormat{" +
                "type='" + type + '\'' +
                ", ext='" + ext + '\'' +
                '}';
    }
}