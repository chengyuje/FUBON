package com.systex.jbranch.fubon.commons.mail;

import com.systex.jbranch.fubon.commons.Manager;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.PlatformContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static org.apache.commons.lang.StringUtils.defaultIfEmpty;

@Service
public class MailServiceJavaX implements MailService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void send(MailConfig cfg) throws Exception {
        Session session = prepareSession();
        MimeMessage mail = writeMessage(cfg, session);

        logger.info("\r\n--------MailServiceJavaX--------\r\n " +
                        "--from : {} \r\n " +
                        "--to : {} \r\n " +
                        "--subject : {} \r\n" +
                        "--content : {} \r\n" +
                        "------------------------------"
                        , mail.getFrom()[0]
                        , mail.getRecipients(Message.RecipientType.TO)[0]
                        , mail.getSubject()
                        , mail.getContent());

        logger.info("connecting...");
        Transport transport = session.getTransport(defaultIfEmpty(session.getProperty("protocol"), "smtp"));
        transport.connect();
        logger.info("prepare to send...");
        transport.sendMessage(mail, mail.getAllRecipients());
        logger.info("sent...");
        transport.close();
        logger.info("finish...");
    }

    private MimeMessage writeMessage(MailConfig cfg, Session session) throws MessagingException, UnsupportedEncodingException {
        MimeMessage mail = new MimeMessage(session);
        String[] from = defaultIfEmpty(session.getProperty("from"), "wmsr_bank@fubon.com,台北富邦銀行").split(",");
        mail.setFrom(new InternetAddress(from[0], from[1]));
        mail.setRecipients(Message.RecipientType.TO, getMailToArray(cfg.getMailToMap()));
        mail.setRecipients(Message.RecipientType.CC, getMailToArray(cfg.getMailCcMap()));
        mail.setRecipients(Message.RecipientType.BCC, getMailToArray(cfg.getMailBccMap()));
        mail.setSubject(cfg.getSubject(), cfg.getEncoding());
        mail.setContent(composePart(cfg));
        mail.setSentDate(new Date());
        mail.saveChanges();
        return mail;
    }

    private Multipart composePart(MailConfig cfg) throws MessagingException {
        Multipart body = new MimeMultipart();
        MimeBodyPart content = new MimeBodyPart();
        content.setContent(cfg.getContent(), cfg.getContentType() + "; charset=" + cfg.getEncoding());
        body.addBodyPart(content);

        addAttachFilesIfExists(cfg, body);
        addHtmlImagesIfExists(cfg, body);
        return body;
    }

    private void addHtmlImagesIfExists(MailConfig cfg, Multipart body) throws MessagingException {
        if (cfg.getImageMap() == null || cfg.getImageMap().isEmpty()) return;

        for (Map.Entry<String, File> entry: cfg.getImageMap().entrySet()) {
            MimeBodyPart content = new MimeBodyPart();
            content.setDataHandler(new DataHandler(new FileDataSource(entry.getValue())));
            content.setHeader("Content-ID", entry.getKey());
            body.addBodyPart(content);
        }
    }

    private void addAttachFilesIfExists(MailConfig cfg, Multipart body) throws MessagingException {
        if (cfg.getAttachFileMap() == null || cfg.getAttachFileMap().isEmpty()) return;

        for (Map.Entry<String, Object> each: cfg.getAttachFileMap().entrySet()) {
            MimeBodyPart filePart = new MimeBodyPart();
            DataSource file = new ByteArrayDataSource(((byte[])each.getValue()),"application/octet-stream");
            DataHandler dataHandler = new DataHandler(file);
            filePart.setDataHandler(dataHandler);
            filePart.setFileName(each.getKey());
            body.addBodyPart(filePart);
        }
    }

    private InternetAddress[] getMailToArray(Map<String, String> map) throws UnsupportedEncodingException {
        if (map == null || map.isEmpty()) return null;

        InternetAddress[] addresses = new InternetAddress[map.size()];
        int index = 0;
        for (Map.Entry<String, String> each: map.entrySet()) {
            addresses[index++] = new InternetAddress(each.getKey(), each.getValue());
        }
        return addresses;
    }

    private Session prepareSession() throws JBranchException {
        List<Map<String, String>> setting = loadMailSetting();
        final Properties props = new Properties();

        for (Map<String, String> each: setting) {
            props.put(each.get("PARAM_CODE"), each.get("PARAM_NAME"));
        }
        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication((String) props.get("username"), (String) props.get("password"));
            }
        });
        return session;
    }

    private List loadMailSetting() throws JBranchException {
        logger.info("Mail：讀取【SYS.MAIL】設定");
        List setting = Manager.manage(PlatformContext.getBean(DataAccessManager.class))
                .append("select PARAM_CODE, PARAM_NAME from TBSYSPARAMETER where PARAM_TYPE = 'SYS.MAIL' ")
                .query();
        if (setting.isEmpty())
            throw new JBranchException("沒有【SYS.MAIL】的系統參數");
        return setting;
    }
}
