package com.systex.jbranch.app.server.init;

import com.systex.jbranch.platform.common.initiation.InitiatorIF;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BusinessInit implements InitiatorIF {
// ------------------------------ FIELDS ------------------------------

    private Logger logger = LoggerFactory.getLogger(BusinessInit.class);

// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface InitiatorIF ---------------------

    public void execute() throws Exception {
        //1.將產品匯率資料存入DataManager
        try {
            SysInfo.setInfoValue(SystemVariableConsts.SYS_REF_XRATE);
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
}
