package com.systex.jbranch.platform.server.integration.message.processor;

import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.server.integration.message.Processor;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

import java.text.DecimalFormat;
import java.util.Map;

import static com.systex.jbranch.platform.server.integration.message.HostMessageConstants.ATTRIBUTE_V_FRACTION;

/**
 * @author Alex Lin
 * @version 2010/12/21 4:38 PM
 */
public class FractionProcessor implements Processor {
// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface Processor ---------------------

    public String process(String value, Map<String, String> attributes) throws JBranchException {
        //小數點位數
        String vFraction = attributes.get(ATTRIBUTE_V_FRACTION);
        String finalValue;
        if (StringUtils.isBlank(vFraction)) {
            finalValue = value;
        }
        else {
            int fraction = NumberUtils.toInt(vFraction);
            String pattern;
            if (fraction == 0) {
                pattern = "0";
            }
            else {
                pattern = StringUtils.rightPad("0.", fraction + 2, "0");
            }
            double num = NumberUtils.toDouble(value);
            finalValue = new DecimalFormat(pattern).format(num);
        }
        return finalValue;
    }
}
