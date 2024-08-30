package com.systex.jbranch.platform.common.mail;

import java.io.File;
import java.io.InputStream;

/**
 * 信件附檔物件。如果是大型附檔，建議使用File傳入。
 *
 * @author Alex Lin
 * @version 2010/08/31 3:01:30 PM
 */
public class Attachment {
// ------------------------------ FIELDS ------------------------------

    /** 檔名 */
    private String fileName;
    /** 附檔的串流 */
    private InputStream inputStream;
    /** 附檔File */
    private File file;

// --------------------------- CONSTRUCTORS ---------------------------

    public Attachment(String fileName, InputStream inputStream) {
        this.fileName = fileName;
        this.inputStream = inputStream;
    }

    public Attachment(String fileName, File file) {
        this.fileName = fileName;
        this.file = file;
    }

// --------------------- GETTER / SETTER METHODS ---------------------

    public File getFile() {
        return file;
    }

    public String getFileName() {
        return fileName;
    }

    public InputStream getInputStream() {
        return inputStream;
    }
}
