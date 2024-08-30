package com.systex.jbranch.platform.common.report.factory;

import java.util.Map;

/**
 * @author Alex Lin
 * @version 2010/11/08 3:30 PM
 */
public class ReportEngineFactory {
// ------------------------------ FIELDS ------------------------------

    private Map engines;
    private ReportEngineType defaultEngine;

// -------------------------- OTHER METHODS --------------------------

    public ReportEngine getEngine() {
        return getEngine(defaultEngine);
    }

    public ReportEngine getEngine(ReportEngineType engineType) {
        return (ReportEngine) engines.get(engineType);
    }

// --------------------- GETTER / SETTER METHODS ---------------------

    public void setDefaultEngine(ReportEngineType defaultEngine) {
        this.defaultEngine = defaultEngine;
    }

    public void setEngines(Map engines) {
        this.engines = engines;
    }
}
