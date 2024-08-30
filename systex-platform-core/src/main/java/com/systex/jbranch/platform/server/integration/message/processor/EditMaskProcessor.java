package com.systex.jbranch.platform.server.integration.message.processor;

import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.server.integration.message.Processor;
import org.apache.commons.lang.StringUtils;

import java.util.Map;

import static com.systex.jbranch.platform.server.integration.message.HostMessageConstants.ATTRIBUTE_V_EDIT_MASK;

/**
 * @author Alex Lin
 * @version 2010/12/21 4:07 PM
 */
public class EditMaskProcessor implements Processor {
// ------------------------------ FIELDS ------------------------------

    private Map<String, Formatter> formatters;

// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface Processor ---------------------

    public String process(String value, Map<String, String> attributes) throws JBranchException {
        //編輯格式
        String vEditMask = attributes.get(ATTRIBUTE_V_EDIT_MASK);
        String finalValue;
        if (StringUtils.isBlank(vEditMask)) {
            finalValue = value;
        }
        else {
            Formatter formatter = formatters.get(vEditMask);
            if (formatter != null) {
                finalValue = formatter.format(value, attributes);
            }
            else {
                throw new JBranchException("Unsupported EditMask : " + vEditMask);
            }
        }
        return finalValue;
    }

// --------------------- GETTER / SETTER METHODS ---------------------

    public void setFormatters(Map<String, Formatter> formatters) {
        this.formatters = formatters;
    }
}
