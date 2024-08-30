package com.systex.jbranch.platform.server.integration.message.processor.datafrom;

import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.server.integration.message.processor.Formatter;

import java.util.Map;

import static com.systex.jbranch.platform.server.integration.message.HostMessageConstants.ATTRIBUTE_V_DEFAULT;

/**
 * @author Alex Lin
 * @version 2010/12/22 10:52 AM
 */
public class DefaultValueFormatter implements Formatter {
// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface Formatter ---------------------

    public String format(String source, Map<String, String> attributes) throws JBranchException {
        return attributes.get(ATTRIBUTE_V_DEFAULT);
    }
}
