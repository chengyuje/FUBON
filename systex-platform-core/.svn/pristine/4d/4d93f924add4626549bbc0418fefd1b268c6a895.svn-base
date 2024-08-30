package com.systex.jbranch.platform.common.report.engine.listener;

import com.systex.jbranch.platform.common.termination.Terminable;
import org.eclipse.birt.core.framework.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * @author Alex Lin
 * @version 2010/11/24 10:55 AM
 */
public class BirtShutdownHandler implements ServletContextListener, Terminable {
// ------------------------------ FIELDS ------------------------------

	private Logger logger = LoggerFactory.getLogger(BirtShutdownHandler.class);
	
// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface ServletContextListener ---------------------

    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        terminate();
    }

    public void contextInitialized(ServletContextEvent servletContextEvent) {
    }

// --------------------- Interface Terminable ---------------------


    public void terminate() {
        try {
            Platform.shutdown();
            if (logger.isInfoEnabled()) {
                logger.info("birt engine shutdown complete!!");
            }
        }
        catch (Exception e) {
            logger.error("birt engine shutdown error!!", e);
        }
    }
}
