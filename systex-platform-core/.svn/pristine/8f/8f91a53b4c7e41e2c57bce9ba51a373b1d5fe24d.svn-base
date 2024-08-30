package com.systex.jbranch.platform.common.dataManager.impl.db;

import com.systex.jbranch.platform.common.dataManager.BizlogicVO;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.PlatformContext;

/**
 * @author Alex Lin
 * @version 2011/03/14 1:48 PM
 */
public class UserBizlogicVODB extends BizlogicVO {
// ------------------------------ FIELDS ------------------------------

    private VarManager varManagerUser;
    private String wsId;

// --------------------------- CONSTRUCTORS ---------------------------

    public UserBizlogicVODB(String wsId) throws JBranchException {
        this.wsId = wsId;
        this.varManagerUser = (VarManager) PlatformContext.getBean("userBizlogicVarManager");
    }

// -------------------------- OTHER METHODS --------------------------

    @Override
    public Object getVar(String key) {
        return varManagerUser.getVar(wsId, key);
    }

    @Override
    public void setVar(String key, Object value) {
        varManagerUser.setVar(wsId, key, value);
    }
}
