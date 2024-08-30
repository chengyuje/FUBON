package com.systex.jbranch.platform.server.integration.message.processor;

import com.systex.jbranch.platform.common.errHandle.JBranchException;

import java.util.Map;

/**
 * @author Alex Lin
 * @version 2010/12/21 5:31 PM
 */
public interface Formatter {
// -------------------------- OTHER METHODS --------------------------

    String format(String source, Map<String, String> attributes) throws JBranchException;
}
