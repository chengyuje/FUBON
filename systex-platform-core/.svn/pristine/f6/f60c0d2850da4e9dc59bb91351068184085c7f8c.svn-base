package com.systex.jbranch.platform.common.log;

import ch.qos.logback.core.PropertyDefinerBase;

/**
 * @author Alex Lin
 * @version 2010/12/15 4:28 PM
 */
public class JBranchRootPropertyDefiner extends PropertyDefinerBase {
    private static String jbranchRoot;

    public String getPropertyValue() {
    	if(jbranchRoot == null){
    		jbranchRoot = System.getenv("JBRANCH_ROOT");
    	}
        return JBranchRootPropertyDefiner.jbranchRoot;
    }

    public static void setJbranchRoot(String jbranchRoot) {
        JBranchRootPropertyDefiner.jbranchRoot = jbranchRoot;
    }
}
