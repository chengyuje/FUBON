package com.systex.jbranch.platform.common.errHandle;

/**
 * @author Alex Lin
 * @version 2009/12/21 1:30:10 PM
 */
public class SessionException extends JBranchException {

    public SessionException(EnumErrInputType errInputType, String value) {
        super(errInputType, value);
    }
}
