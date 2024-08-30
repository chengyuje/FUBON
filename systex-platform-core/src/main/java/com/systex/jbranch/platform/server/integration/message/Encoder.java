package com.systex.jbranch.platform.server.integration.message;

import com.systex.jbranch.platform.common.errHandle.JBranchException;

/**
 * @author Alex Lin
 * @version 2011/02/16 5:05 PM
 */
public interface Encoder {
    byte[] encode(String source) throws JBranchException;
    String decode(byte[] source) throws JBranchException;
}
