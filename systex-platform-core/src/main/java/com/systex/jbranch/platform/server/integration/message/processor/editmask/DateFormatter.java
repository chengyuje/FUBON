package com.systex.jbranch.platform.server.integration.message.processor.editmask;

import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.server.integration.message.processor.Formatter;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;

import java.text.ParseException;
import java.util.Date;
import java.util.Map;

/**
 * @author Alex Lin
 * @version 2010/12/21 5:38 PM
 */
public class DateFormatter implements Formatter {
// ------------------------------ FIELDS ------------------------------

    private String targetPattern;
    private String[] sourcePatterns;

// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface Formatter ---------------------

    public String format(String source, Map<String, String> attributes) throws JBranchException {
        try {
            Date date = DateUtils.parseDate(source, sourcePatterns);
            return DateFormatUtils.format(date, targetPattern);
        }
        catch (ParseException e) {
            throw new JBranchException(e);
        }
    }

// -------------------------- OTHER METHODS --------------------------

    public void setSourcePattern(String sourcePattern) {
        this.sourcePatterns = new String[]{sourcePattern};
    }

// --------------------- GETTER / SETTER METHODS ---------------------

    public void setTargetPattern(String targetPattern) {
        this.targetPattern = targetPattern;
    }
}
