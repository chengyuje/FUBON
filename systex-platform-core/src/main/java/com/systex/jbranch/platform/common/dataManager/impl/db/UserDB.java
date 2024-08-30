package com.systex.jbranch.platform.common.dataManager.impl.db;

import com.systex.jbranch.platform.common.dataManager.User;
import com.systex.jbranch.platform.common.errHandle.JBranchException;

/**
 * @author Alex Lin
 * @version 2011/03/15 12:03 PM
 */
public class UserDB extends User {
// --------------------------- CONSTRUCTORS ---------------------------

    public UserDB(String wsId) throws JBranchException {
        this.setPlatFormVO(new UserPlatformVODB(wsId));
        this.setBizlogicVO(new UserBizlogicVODB(wsId));
    }
}
