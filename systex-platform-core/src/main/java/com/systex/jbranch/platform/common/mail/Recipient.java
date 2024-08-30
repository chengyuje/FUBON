package com.systex.jbranch.platform.common.mail;

/**
 * @author Alex Lin
 * @version 2010/08/27 6:24:15 PM
 */
public class Recipient {
// ------------------------------ FIELDS ------------------------------

    /** 名字 */
    private String name;
    /** mail */
    private String mail;

// --------------------------- CONSTRUCTORS ---------------------------

    public Recipient() {
    }

    public Recipient(String name, String mail) {
        this.name = name;
        this.mail = mail;
    }

// --------------------- GETTER / SETTER METHODS ---------------------

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
