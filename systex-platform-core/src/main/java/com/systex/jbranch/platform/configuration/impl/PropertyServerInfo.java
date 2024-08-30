package com.systex.jbranch.platform.configuration.impl;

import com.systex.jbranch.platform.configuration.ServerInfoIF;

/**
 * @author Alex Lin
 * @version 2010/11/22 12:26 PM
 */
public class PropertyServerInfo implements ServerInfoIF {
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

// --------------------- GETTER / SETTER METHODS ---------------------

    public void setJbranchRoot(String jbranchRoot) {
        this.jbranchRoot = jbranchRoot;
    }

    public void setServerPath(String serverPath) {
        this.serverPath = serverPath;
    }

// ------------------------ CANONICAL METHODS ------------------------

    @Override
    public String toString() {
        return "com.systex.jbranch.platform.configuration.impl.PropertyServerInfo{" +
                "serverPath='" + serverPath + '\'' +
                ", jbranchRoot='" + jbranchRoot + '\'' +
                '}';
    }
}
