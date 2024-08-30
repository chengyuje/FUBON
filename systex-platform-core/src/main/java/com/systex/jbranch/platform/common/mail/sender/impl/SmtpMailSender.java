package com.systex.jbranch.platform.common.mail.sender.impl;

import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.mail.Attachment;
import com.systex.jbranch.platform.common.mail.MailContent;
import com.systex.jbranch.platform.common.mail.Recipient;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.Properties;

/**
 * 實作SMTP發送
 *
 * @author Alex Lin
 * @version 2010/08/27 2:08:25 PM
 */
public class SmtpMailSender extends AbstractMailSender {
// ------------------------------ FIELDS ------------------------------

    private static final String HEADER_IMPORTANCE = "Importance";
    private static final String HEADER_X_PRIORITY = "X-Priority";
    private JavaMailSender mailSender;
    private String encdoing;

// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface MailSender ---------------------

    public void send(MailContent mailContent) throws JBranchException {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            message.setHeader(HEADER_X_PRIORITY, String.valueOf(mailContent.getPriority().getValue()));
            message.setHeader(HEADER_IMPORTANCE, String.valueOf(mailContent.getImportance().getValue()));
            Properties headers = mailContent.getHeaders();
            if (headers != null) {
                for (Map.Entry header : headers.entrySet()) {
                    message.setHeader((String) header.getKey(), (String) header.getValue());
                }
            }
            MimeMessageHelper helper = new MimeMessageHelper(message, true, encdoing);
            Attachment[] attachments = mailContent.getAttachments();
            for (final Attachment attachment : attachments) {
                InputStreamSource inputStreamSource;
                if (attachment.getFile() != null) {
                    inputStreamSource = new FileSystemResource(attachment.getFile());
                }
                else {
                    inputStreamSource = new ByteArrayResource(IOUtils.toByteArray(attachment.getInputStream()));
                }
                helper.addAttachment(attachment.getFileName(), inputStreamSource);
            }

            helper.setText(mailContent.getContent(), true);
            helper.setSubject(mailContent.getSubject());
            Recipient fromRecipient = getFrom();
            if (fromRecipient != null) {
                helper.setFrom(transform(fromRecipient));
            }
            helper.setTo(transform(getTo()));

            Recipient[] ccRecipients = getCc();
            if (ccRecipients != null) {
                helper.setCc(transform(ccRecipients));
            }
            Recipient[] bccRecipients = getBcc();
            if (bccRecipients != null) {
                helper.setBcc(transform(bccRecipients));
            }

            mailSender.send(message);
        }
        catch (MessagingException e) {
            throw new JBranchException(e);
        }
        catch (UnsupportedEncodingException e) {
            throw new JBranchException(e);
        }
        catch (IOException e) {
            throw new JBranchException(e);
        }
    }

    private InternetAddress[] transform(Recipient[] toRecipients) throws UnsupportedEncodingException {
        InternetAddress[] addresses = new InternetAddress[toRecipients.length];
        for (int i = 0; i < toRecipients.length; i++) {
            addresses[i] = transform(toRecipients[i]);
        }
        return addresses;
    }

    private InternetAddress transform(com.systex.jbranch.platform.common.mail.Recipient recipient) throws UnsupportedEncodingException {
        return new InternetAddress(recipient.getMail(), recipient.getName(), encdoing);
    }

// --------------------- GETTER / SETTER METHODS ---------------------

    public void setEncdoing(String encdoing) {
        this.encdoing = encdoing;
    }

    public void setMailSender(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }
}
