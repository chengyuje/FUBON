package com.systex.jbranch.platform.common.mail.content.impl;

import com.systex.jbranch.platform.common.errHandle.JBranchException;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.VelocityException;
import org.springframework.ui.velocity.VelocityEngineUtils;

/**
 * @author Alex Lin
 * @version 2010/08/27 2:13:47 PM
 */
public class TemplateMailContent extends AbstractMailContent {
// ------------------------------ FIELDS ------------------------------

    private VelocityEngine velocityEngine;
    private String encoding;

// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface MailContent ---------------------

    /** @see com.systex.jbranch.platform.common.mail.MailContent#getContent() */
    public String getContent() throws JBranchException {
        try {
            return VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, getTemplate(), encoding, parameters);
        }
        catch (VelocityException e) {
            throw new JBranchException(e.getMessage());
        }
    }

// --------------------- GETTER / SETTER METHODS ---------------------

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public void setVelocityEngine(VelocityEngine velocityEngine) {
        this.velocityEngine = velocityEngine;
    }
}
