package com.systex.jbranch.platform.common.report.reportdata;

public class DatamanagerConfigAdapter implements ConfigAdapterIF {
// ------------------------------ FIELDS ------------------------------

    private String reportEngine = null;
    private String transactionpath = null;
    private String reportTemp = null;
    private String encryptReport = null;
    private String serverHome;
    private String root;
    private String transaction;
    private String platformTemp;

// --------------------------- CONSTRUCTORS ---------------------------

    public DatamanagerConfigAdapter() {
    }

// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface ConfigAdapterIF ---------------------

    public String getRoot() {
        return this.root;
    }

    public String getTransaction() {
        return this.transaction;
    }

    public String getServerHome() {
        return this.serverHome;
    }

    public String getReportEngine() {
        return this.reportEngine;
    }

    public String getTransactionpath() {
        return this.transactionpath;
    }

    public String getReportTemp() {
        return this.reportTemp;
    }

    public String getEncryptReport() {
        return this.encryptReport;
    }

    public void setReportEngine(String reportEngine) {
        this.reportEngine = reportEngine;
    }

    public void setTransactionpath(String transactionpath) {
        this.transactionpath = transactionpath;
    }

    public void setReportTemp(String reportTemp) {
        this.reportTemp = reportTemp;
    }

    public void setEncryptReport(String encryptReport) {
        this.encryptReport = encryptReport;
    }

// --------------------- GETTER / SETTER METHODS ---------------------

    public void setRoot(String root) {
        this.root = root;
    }

    public void setServerHome(String serverHome) {
        this.serverHome = serverHome;
    }

    public void setTransaction(String transaction) {
        this.transaction = transaction;
    }

	public String getPlatformTemp() {
		return platformTemp;
	}

	public void setPlatformTemp(String platformTemp) {
		this.platformTemp = platformTemp;
	}
}
