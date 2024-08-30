package com.systex.jbranch.platform.common.mail;

import com.systex.jbranch.platform.common.errHandle.JBranchException;

/**
 * Mail的發送方法
 *
 * @author Alex Lin
 * @version 2010/08/27 2:06:31 PM
 */
public interface MailSender {
    void send(MailContent mailContent) throws JBranchException;

    Recipient[] getTo();

    Recipient[] getCc();

    Recipient[] getBcc();

    Recipient getFrom();
}
