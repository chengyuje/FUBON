package com.systex.jbranch.platform.common.log.listener;

import ch.qos.logback.classic.LoggerContext;

import com.systex.jbranch.platform.common.log.JBranchRootPropertyDefiner;
import com.systex.jbranch.platform.common.log.LogInit;
import com.systex.jbranch.platform.common.termination.Terminable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import java.io.File;

/**
 * @author Alex Lin
 * @version 2010/11/24 11:03 AM
 */
public class LogHandler implements ServletContextListener, Terminable {
// ------------------------ INTERFACE METHODS ------------------------

	private Logger logger = LoggerFactory.getLogger(LogHandler.class);

// --------------------- Interface ServletContextListener ---------------------

    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        terminate();
    }

    public void contextInitialized(ServletContextEvent servletContextEvent) {
        ServletContext servletContext = servletContextEvent.getServletContext();
        String realPath = servletContext.getRealPath("/");
        String jbranchRoot = servletContext.getInitParameter("JBRANCH_ROOT");
        if(jbranchRoot != null && jbranchRoot.startsWith("/WEB-INF")){
        	jbranchRoot = new File(realPath, jbranchRoot).getAbsolutePath();
        }
        JBranchRootPropertyDefiner.setJbranchRoot(jbranchRoot);
        logger.info("log jbranchRoot=" + jbranchRoot);
        String logbackConfig = servletContext.getInitParameter("logConfig");
        new LogInit().init(new File(realPath, logbackConfig));
    }

// --------------------- Interface Terminable ---------------------


    public void terminate() {
        ((LoggerContext) LoggerFactory.getILoggerFactory()).stop();
    }
}
