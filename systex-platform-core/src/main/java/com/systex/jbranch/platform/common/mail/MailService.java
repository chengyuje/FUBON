package com.systex.jbranch.platform.common.mail;

import com.systex.jbranch.platform.common.errHandle.JBranchException;

import java.util.Map;

/**
 * @author Alex Lin
 * @version 2010/08/27 2:04:19 PM
 */
public class MailService {
// ------------------------------ FIELDS ------------------------------

    private MailSender mailSender;
    private MailContent mailContent;

// --------------------- GETTER / SETTER METHODS ---------------------

    /**
     * 傳送mail
     *
     * @param parameters  mail template裡的變數
     * @param attachments 附檔
     * @throws JBranchException jbranch exception
     */
    public void send(Map<String, Object> parameters, Attachment... attachments) throws JBranchException {
        for (Map.Entry<String, Object> entry : parameters.entrySet()) {
            mailContent.addParameter(entry.getKey(), entry.getValue());
        }

        for (Attachment attachment : attachments) {
            mailContent.addAttachment(attachment);
        }
        mailSender.send(mailContent);
    }

    public void setMailContent(MailContent mailContent) {
        this.mailContent = mailContent;
    }

    public void setMailSender(MailSender mailSender) {
        this.mailSender = mailSender;
    }
}
