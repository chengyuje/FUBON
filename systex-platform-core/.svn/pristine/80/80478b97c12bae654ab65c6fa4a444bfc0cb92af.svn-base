package com.systex.jbranch.platform.configuration.impl;

import com.systex.jbranch.platform.configuration.ServerInfoIF;
import org.springframework.web.context.ServletContextAware;

import javax.servlet.ServletContext;

/**
 * @author Alex Lin
 * @version 2010/11/22 12:21 PM
 */
public class WebServerInfo implements ServletContextAware, ServerInfoIF {
// ------------------------------ FIELDS ------------------------------

    private String serverPath;
    private String jbranchRoot;

// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface ServerInfoIF ---------------------

    public String getJbranchRoot() {
        return jbranchRoot;
    }

    public String getServerPath() {
        return serverPath;
    }

// -------------------------- OTHER METHODS --------------------------

    public void setServletContext(ServletContext servletContext) {
        this.serverPath = servletContext.getRealPath("/");
        this.jbranchRoot = servletContext.getInitParameter("JBRANCH_ROOT");
    }

// ------------------------ CANONICAL METHODS ------------------------

    @Override
    public String toString() {
        return "com.systex.jbranch.platform.configuration.impl.WebServerInfo{" +
                "serverPath='" + serverPath + '\'' +
                ", jbranchRoot='" + jbranchRoot + '\'' +
                '}';
    }
}
