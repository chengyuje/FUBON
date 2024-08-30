package com.systex.jbranch.platform.server.integration.message.legacy;

import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.server.integration.message.Encoder;

import java.util.Map;

/**
 * @author Alex Lin
 * @version 2011/03/14 5:08 PM
 */
public abstract class SourceParser {
// ------------------------------ FIELDS ------------------------------

    protected Encoder encoder;

// -------------------------- OTHER METHODS --------------------------

    public abstract String getNextContent(byte[] source, Map attrMap) throws JBranchException;

// --------------------- GETTER / SETTER METHODS ---------------------

    public void setEncoder(Encoder encoder) {
        this.encoder = encoder;
    }
}
