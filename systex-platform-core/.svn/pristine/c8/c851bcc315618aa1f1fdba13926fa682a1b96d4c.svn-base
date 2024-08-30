package com.systex.jbranch.platform.server.integration.message.encoder;

import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.server.integration.message.Encoder;

import java.io.UnsupportedEncodingException;

/**
 * @author Alex Lin
 * @version 2011/02/16 5:06 PM
 */
public class DefaultEncoder implements Encoder {
// ------------------------------ FIELDS ------------------------------

    private String charset;

// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface Encoder ---------------------

    public byte[] encode(String source) throws JBranchException {
        try {
            return source.getBytes(charset);
        }
        catch (UnsupportedEncodingException e) {
            throw new JBranchException(e);
        }
    }

    public String decode(byte[] source) throws JBranchException {
        try {
            return new String(source, charset);
        }
        catch (UnsupportedEncodingException e) {
            throw new JBranchException(e);
        }
    }

// --------------------- GETTER / SETTER METHODS ---------------------

    public void setCharset(String charset) {
        this.charset = charset;
    }
}
