package com.systex.jbranch.platform.common.errHandle;

public class UnLoginException extends JBranchException {

    private static final long serialVersionUID = 8239925503745362671L;

    public UnLoginException(String ml_idGroup, Throwable cause) {
        super(ml_idGroup, cause);
    }
    
    public UnLoginException(String ml_idGroup) {
        super(ml_idGroup);
    }

}
