package com.systex.jbranch.platform.common.dataManager.impl.db;

import com.systex.jbranch.platform.common.dataManager.BizlogicVO;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.PlatformContext;

import java.io.Serializable;

/**
 * @author Alex Lin
 * @version 2011/03/14 1:48 PM
 */
public class SysBizlogicVODB extends BizlogicVO {
// ------------------------------ FIELDS ------------------------------

    private SingleVarManager varManagerUser;

// --------------------------- CONSTRUCTORS ---------------------------

    public SysBizlogicVODB() throws JBranchException {
        this.varManagerUser = (SingleVarManager) PlatformContext.getBean("sysBizlogicVarManager");
    }

// -------------------------- OTHER METHODS --------------------------

    @Override
    public Object getVar(String key) {
        return varManagerUser.getVar(key);
    }

    @Override
    public void setVar(String key, Object value) {
        varManagerUser.setVar(key, (Serializable) value);
    }
}
