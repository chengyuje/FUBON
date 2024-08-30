package com.systex.jbranch.platform.common.report.factory;

/**
 * @author Alex Lin
 * @version 2010/11/08 3:18 PM
 */
public enum ReportEngineType {
    BIRT("birt"),
    JASPER("jasper"),
    MSWORD("msword"),
    LINE_MODE("line_mode");

// ------------------------------ FIELDS ------------------------------

    private String beanPrefix;

// --------------------------- CONSTRUCTORS ---------------------------

    ReportEngineType(String beanPrefix) {
        this.beanPrefix = beanPrefix;
    }

// --------------------- GETTER / SETTER METHODS ---------------------

    public String getBeanPrefix() {
        return beanPrefix;
    }

// ------------------------ CANONICAL METHODS ------------------------

    @Override
    public String toString() {
        return "com.systex.jbranch.platform.common.report.factory.ReportEngineType{" +
                "beanPrefix='" + beanPrefix + '\'' +
                '}';
    }
}
