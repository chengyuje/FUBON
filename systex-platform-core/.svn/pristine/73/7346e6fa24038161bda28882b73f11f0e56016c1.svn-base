package com.systex.jbranch.platform.server.integration.message.processor.datafrom;

import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.integration.message.processor.Formatter;

import java.util.Map;

import static com.systex.jbranch.platform.server.integration.message.HostMessageConstants.ATTRIBUTE_V_VAR_ID;

/**
 * @author Alex Lin
 * @version 2010/12/22 10:53 AM
 */
public class SysVarFormatter implements Formatter {
// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface Formatter ---------------------

    public String format(String source, Map<String, String> attributes) throws JBranchException {
        //變數代號
        //0 無/1 固定資料/2 系統變數
        String vVarID = attributes.get(ATTRIBUTE_V_VAR_ID);
        return String.valueOf(SysInfo.getInfoValue(vVarID));
    }
}
