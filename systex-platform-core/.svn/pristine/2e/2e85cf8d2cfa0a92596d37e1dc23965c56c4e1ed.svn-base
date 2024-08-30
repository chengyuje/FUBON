package com.systex.jbranch.platform.common.scheduler;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.systex.jbranch.platform.common.dataManager.DataManager;
import com.systex.jbranch.platform.common.util.PlatformContext;

@Repository
public class PropertyUtil {
// ------------------------------ FIELDS ------------------------------

    private static PropertyUtil instance = null;
    private static final String APSERVER = "scheduler.apserver";
    private static final String CONFIG_FOLDER = "Config";

    private static final String CRON_EXPRESSION = "scheduler.cronexpression";
    private static final String PROCESSOR = "scheduler.processor";
    private static final String PROPERITY_FILE = "scheduler.properties";
    private static final String STANDALONE = "scheduler.standalone";
    private static final String START_SCHEDULE = "scheduler.start";

    private Properties prop = null;
    private String root;
	private Logger logger = LoggerFactory.getLogger(PropertyUtil.class);
    private String cronExpression;
    private String startSchedule;
    private String standALone;

// -------------------------- STATIC METHODS --------------------------

    public static PropertyUtil getInstance() throws Exception {
        return (PropertyUtil) PlatformContext.getBean("propertyUtil");
    }

// --------------------------- CONSTRUCTORS ---------------------------

    public PropertyUtil() throws Exception {
    }

// -------------------------- OTHER METHODS --------------------------

    public String getApServer() {
        return prop.getProperty(APSERVER);
    }

    public String getProcessor() {

        return (String) DataManager.getSystem().getInfo().get("apServerName");
    }

// --------------------- GETTER / SETTER METHODS ---------------------

    public String getCronExpression() {
        return this.cronExpression;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

    public String getStandALone() {
        return this.standALone;
    }

    public void setStandALone(String standALone) {
        this.standALone = standALone;
    }

    public String getStartSchedule() {
        return this.startSchedule;
    }

    public void setStartSchedule(String startSchedule) {
        this.startSchedule = startSchedule;
    }

    public void setRoot(String root) {
        this.root = root;
    }
}
