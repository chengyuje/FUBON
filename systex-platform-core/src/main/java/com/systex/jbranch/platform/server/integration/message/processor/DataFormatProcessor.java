package com.systex.jbranch.platform.server.integration.message.processor;

import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.server.integration.message.Processor;
import org.apache.commons.lang.StringUtils;

import java.util.Map;

import static com.systex.jbranch.platform.server.integration.message.HostMessageConstants.ATTRIBUTE_V_DATA_FORMAT;

/**
 * @author Alex Lin
 * @version 2010/12/21 4:01 PM
 */
public class DataFormatProcessor implements Processor {
// ------------------------------ FIELDS ------------------------------

    private Map<String, Formatter> formatters;

// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface Processor ---------------------

    public String process(String value, Map<String, String> attributes) throws JBranchException {
        //資料類型
        //空白/X 文數字/N 數字/C 中文有0E0F/H Hex Data
        //當資料類型=N時，截去左邊多餘的資料；當資料類型=其他時，截去右邊多餘的資料
        String vDataFormat = attributes.get(ATTRIBUTE_V_DATA_FORMAT);

        String finalValue;
        if (StringUtils.isBlank(vDataFormat)) {
            finalValue = value;
        }
        else {
            Formatter formatter = formatters.get(vDataFormat);
            if (formatter != null) {
                finalValue = formatter.format(value, attributes);
            }
            else {
                throw new JBranchException("Unsupported DataFormat : " + vDataFormat);
            }
        }
        return finalValue;
    }

// --------------------- GETTER / SETTER METHODS ---------------------

    public void setFormatters(Map<String, Formatter> formatters) {
        this.formatters = formatters;
    }
}
