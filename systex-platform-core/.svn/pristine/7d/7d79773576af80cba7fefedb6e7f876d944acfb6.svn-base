package com.systex.jbranch.platform.common.scheduler.listener;

import com.systex.jbranch.platform.common.termination.Terminable;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * @author Alex Lin
 * @version 2009/12/16 2:00:14 PM
 */
public class SchedulerShutdownHandler implements ServletContextListener, Terminable {
// ------------------------------ FIELDS ------------------------------

	private Logger logger = LoggerFactory.getLogger(SchedulerShutdownHandler.class);

// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface ServletContextListener ---------------------


    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        terminate();
    }

    public void contextInitialized(ServletContextEvent servletContextEvent) {
    }

    public void terminate() {
        try {
            StdSchedulerFactory.getDefaultScheduler().shutdown();
            if (logger.isInfoEnabled()) {
                logger.info("scheduler engine shutdown complete!!");
            }
        }
        catch (Exception e) {
            logger.error("scheduler engine shutdown error!!", e);
        }
    }
}
