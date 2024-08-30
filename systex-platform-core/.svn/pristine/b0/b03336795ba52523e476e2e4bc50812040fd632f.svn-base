package com.systex.jbranch.platform.server.integration.message.processor;

import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.server.integration.message.Processor;
import org.apache.commons.lang.StringUtils;

import java.util.Map;

import static com.systex.jbranch.platform.server.integration.message.HostMessageConstants.ATTRIBUTE_V_EDIT_TYPE;

/**
 * @author Alex Lin
 * @version 2010/12/21 4:07 PM
 */
public class EditTypeProcessor implements Processor {
// ------------------------------ FIELDS ------------------------------

    private static final String SPACE_PAD_STR = " ";
    private static final String ZERO_PAD_STR = "0";
    private Map<String, Formatter> formatters;

// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface Processor ---------------------

    public String process(String value, Map<String, String> attributes) throws JBranchException {
        //不足位數處理方式
        //空白
        //C(XX△) 左靠右補空白
        //Z(XX0)  左靠右補零
        //N(0XX)  右靠左補零
        //S(△XX) 右靠左補空白
        String vEditType = attributes.get(ATTRIBUTE_V_EDIT_TYPE);
        String finalValue;
        if (StringUtils.isBlank(vEditType)) {
            finalValue = value;
        }
        else {
            //欄位長度
            Formatter formatter = formatters.get(vEditType);
            if (formatter != null) {
                finalValue = formatter.format(value, attributes);
            }
            else {
                throw new JBranchException("Unsupported EditType : " + vEditType);
            }
        }
        return finalValue;
    }

// --------------------- GETTER / SETTER METHODS ---------------------

    public void setFormatters(Map<String, Formatter> formatters) {
        this.formatters = formatters;
    }
}
