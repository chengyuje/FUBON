package com.systex.jbranch.platform.server.integration.message.processor;

import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.server.integration.message.Processor;
import org.apache.commons.lang.StringUtils;

import java.util.Map;

import static com.systex.jbranch.platform.server.integration.message.HostMessageConstants.ATTRIBUTE_V_DATA_FROM;

/**
 * @author Alex Lin
 * @version 2010/12/21 4:20 PM
 */
public class DataFromProcessor implements Processor {
// ------------------------------ FIELDS ------------------------------

    private Map<String, Formatter> formatters;

// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface Processor ---------------------

    public String process(String value, Map<String, String> attributes) throws JBranchException {
        //vDataFrom
        //0 無/1 固定資料/2 系統變數
        String vDataFrom = attributes.get(ATTRIBUTE_V_DATA_FROM);

        String finalValue;
        if (StringUtils.isBlank(vDataFrom)) {
            finalValue = value;
        }
        else {
            Formatter formatter = formatters.get(vDataFrom);
            if (formatter != null) {
                finalValue = formatter.format(value, attributes);
            }
            else {
                throw new JBranchException("Unsupported DataFrom : " + vDataFrom);
            }
        }
        return finalValue;
    }

// --------------------- GETTER / SETTER METHODS ---------------------

    public void setFormatters(Map<String, Formatter> formatters) {
        this.formatters = formatters;
    }
}
