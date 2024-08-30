package com.systex.jbranch.fubon.commons.mail;

import java.io.File;
import java.util.Map;

public class MailConfig {
    private Map<String, String> mailToMap; // Map<Email, Name>
    private Map<String, String> mailCcMap; // Map<Email, Name>
    private Map<String, String> mailBccMap;// Map<Email, Name>
    private String subject;
    private String encoding = "utf8";
    private String content;
    private String contentType = "text/html";
    private Map<String, Object> attachFileMap;
    private Map<String, File> imageMap;

    public Map<String, String> getMailToMap() {
        return mailToMap;
    }

    public void setMailToMap(Map<String, String> mailToMap) {
        this.mailToMap = mailToMap;
    }

    public Map<String, String> getMailCcMap() {
        return mailCcMap;
    }

    public void setMailCcMap(Map<String, String> mailCcMap) {
        this.mailCcMap = mailCcMap;
    }

    public Map<String, String> getMailBccMap() {
        return mailBccMap;
    }

    public void setMailBccMap(Map<String, String> mailBccMap) {
        this.mailBccMap = mailBccMap;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Map<String, Object> getAttachFileMap() {
        return attachFileMap;
    }

    public void setAttachFileMap(Map<String, Object> attachFileMap) {
        this.attachFileMap = attachFileMap;
    }

    public Map<String, File> getImageMap() {
        return imageMap;
    }

    public void setImageMap(Map<String, File> imageMap) {
        this.imageMap = imageMap;
    }
}
