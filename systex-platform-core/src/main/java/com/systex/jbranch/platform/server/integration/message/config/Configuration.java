package com.systex.jbranch.platform.server.integration.message.config;

import org.dom4j.Document;

import java.io.File;

/**
 * @author Alex Lin
 * @version 2010/12/21 1:23 PM
 */
public class Configuration {
// ------------------------------ FIELDS ------------------------------

    private String trxCode;
    private File file;
    private Document document;

// --------------------------- CONSTRUCTORS ---------------------------

    public Configuration(String trxCode, File file, Document document) {
        this.trxCode = trxCode;
        this.file = file;
        this.document = document;
    }

// --------------------- GETTER / SETTER METHODS ---------------------

    public Document getDocument() {
        return document;
    }

    public File getFile() {
        return file;
    }

    public String getTrxCode() {
        return trxCode;
    }

// ------------------------ CANONICAL METHODS ------------------------

    @Override
    public String toString() {
        return "com.systex.jbranch.platform.server.integration.message.config.Configuration{" +
                "trxCode='" + trxCode + '\'' +
                ", file=" + file +
                ", document=" + document +
                '}';
    }
}
