package com.systex.jbranch.platform.common.mail;

import org.springframework.beans.factory.ObjectFactory;

import java.util.Collections;
import java.util.Map;

/**
 * @author Alex Lin
 * @version 2010/08/31 1:38:26 PM
 */
public class MailServiceFactory {
// ------------------------------ FIELDS ------------------------------

    private Map<String, ObjectFactory> services = Collections.emptyMap();

// -------------------------- OTHER METHODS --------------------------

    /**
     * 依傳作id取得相對應的MailService
     *
     * @param id mail service id
     * @return MailService
     */
    public MailService getInstance(String id) {
        return (MailService) services.get(id).getObject();
    }

// --------------------- GETTER / SETTER METHODS ---------------------

    public void setServices(Map<String, ObjectFactory> services) {
        this.services = services;
    }
}
