package com.systex.jbranch.platform.common.mail.content.impl;

import com.systex.jbranch.platform.common.mail.Attachment;
import com.systex.jbranch.platform.common.mail.MailContent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * @author Alex Lin
 * @version 2010/08/30 2:28:39 PM
 */
public abstract class AbstractMailContent implements MailContent {
// ------------------------------ FIELDS ------------------------------

    protected Map<String, Object> parameters = new HashMap<String, Object>();
    private String subject;
    private Priority priority;
    private Importance importance;
    private Properties headers;
    private String template;
    private List<Attachment> attachments = new ArrayList<Attachment>();

// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface MailContent ---------------------

    /** @see com.systex.jbranch.platform.common.mail.MailContent#addParameter(String, Object) */
    public void addParameter(String key, Object value) {
        this.parameters.put(key, value);
    }

    /** @see com.systex.jbranch.platform.common.mail.MailContent#getHeaders() */
    public Properties getHeaders() {
        return headers;
    }

    /** @see com.systex.jbranch.platform.common.mail.MailContent#getImportance() */
    public Importance getImportance() {
        return importance;
    }

    /** @see com.systex.jbranch.platform.common.mail.MailContent#getPriority() */
    public Priority getPriority() {
        return priority;
    }

    /** @see com.systex.jbranch.platform.common.mail.MailContent#getSubject() */
    public String getSubject() {
        return subject;
    }

    /** @see com.systex.jbranch.platform.common.mail.MailContent#getTemplate() */
    public String getTemplate() {
        return template;
    }

    /** @see com.systex.jbranch.platform.common.mail.MailContent#addAttachment(com.systex.jbranch.platform.common.mail.Attachment) */
    public void addAttachment(Attachment attachment) {
        this.attachments.add(attachment);
    }

    /** @see com.systex.jbranch.platform.common.mail.MailContent#getAttachments() */
    public Attachment[] getAttachments() {
        return attachments.toArray(new Attachment[attachments.size()]);
    }

// --------------------- GETTER / SETTER METHODS ---------------------

    public void setHeaders(Properties headers) {
        this.headers = headers;
    }

    public void setImportance(Importance importance) {
        this.importance = importance;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setTemplate(String template) {
        this.template = template;
    }
}
