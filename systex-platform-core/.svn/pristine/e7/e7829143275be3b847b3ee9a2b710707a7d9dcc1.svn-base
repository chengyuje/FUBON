package com.systex.jbranch.platform.common.report.engine;

import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.initiation.InitiatorIF;
import com.systex.jbranch.platform.common.report.reportdata.ConfigAdapterIF;

/**
 * @author Alex Lin
 * @version 2010/04/02 10:58:57 AM
 */
public class JasperReportService implements ReportServiceIF, InitiatorIF {
// ------------------------------ FIELDS ------------------------------

    private ConfigAdapterIF configAdapter;

// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface InitiatorIF ---------------------

    public void execute() throws Exception {
        initial();
    }

// --------------------- Interface ReportServiceIF ---------------------

    public void initial() throws JBranchException {

    }

    public void release() {
    }

// --------------------- GETTER / SETTER METHODS ---------------------

    public void setConfigAdapter(ConfigAdapterIF configAdapter) {
        this.configAdapter = configAdapter;
    }
}
