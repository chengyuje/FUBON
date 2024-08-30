package com.systex.jbranch.platform.common.mail.sender.impl;

import com.systex.jbranch.platform.common.mail.Recipient;
import com.systex.jbranch.platform.common.mail.MailSender;

/**
 * @author Alex Lin
 * @version 2010/08/30 1:16:21 PM
 */
public abstract class AbstractMailSender implements MailSender {
// ------------------------------ FIELDS ------------------------------

    private Recipient[] to;
    private com.systex.jbranch.platform.common.mail.Recipient[] cc;
    private Recipient[] bcc;
    private Recipient from;

// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface MailSender ---------------------

    public Recipient[] getTo() {
        return to;
    }

    public com.systex.jbranch.platform.common.mail.Recipient[] getCc() {
        return cc;
    }

    public Recipient[] getBcc() {
        return bcc;
    }

    public Recipient getFrom() {
        return from;
    }

// --------------------- GETTER / SETTER METHODS ---------------------

    public void setBcc(com.systex.jbranch.platform.common.mail.Recipient[] bcc) {
        this.bcc = bcc;
    }

    public void setCc(Recipient[] cc) {
        this.cc = cc;
    }

    public void setFrom(com.systex.jbranch.platform.common.mail.Recipient from) {
        this.from = from;
    }

    public void setTo(com.systex.jbranch.platform.common.mail.Recipient[] to) {
        this.to = to;
    }
}
