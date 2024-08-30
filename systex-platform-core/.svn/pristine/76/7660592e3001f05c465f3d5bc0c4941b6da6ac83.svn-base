package com.systex.jbranch.platform.common.dataManager.impl.db;

import com.systex.jbranch.platform.common.dataManager.BizlogicVO;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.PlatformContext;

/**
 * @author Alex Lin
 * @version 2011/03/14 1:48 PM
 */
public class BranchBizlogicVODB extends BizlogicVO {
// ------------------------------ FIELDS ------------------------------

    private VarManager varManagerUser;
    private String branchId;

// --------------------------- CONSTRUCTORS ---------------------------

    public BranchBizlogicVODB(String branchId) throws JBranchException {
        this.branchId = branchId;
        this.varManagerUser = (VarManager) PlatformContext.getBean("branchBizlogicVarManager");
    }

// -------------------------- OTHER METHODS --------------------------

    @Override
    public Object getVar(String key) {
        return varManagerUser.getVar(branchId, key);
    }

    @Override
    public void setVar(String key, Object value) {
        varManagerUser.setVar(branchId, key, value);
    }
}
