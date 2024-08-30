package com.systex.jbranch.platform.server.integration.message.processor.dataformat;

import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.server.integration.message.processor.Formatter;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

import java.util.Map;

import static com.systex.jbranch.platform.server.integration.message.HostMessageConstants.ATTRIBUTE_V_FIELD_LEN;

/**
 * @author Alex Lin
 * @version 2010/12/22 11:10 AM
 */
class TruncateRightFormatter implements Formatter {
// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface Formatter ---------------------

    public String format(String source, Map<String, String> attributes) throws JBranchException {
        //欄位長度
        int fieldLen = NumberUtils.toInt(attributes.get(ATTRIBUTE_V_FIELD_LEN));
        return StringUtils.right(source, fieldLen);
    }
}
