package com.systex.jbranch.app.server.fps.insjlb;

/**
 * @author Alex Lin
 * @version 2011/02/23 4:34 PM
 */
public class WSException extends Exception {
// ------------------------------ FIELDS ------------------------------

    private String code;
    private String desc;

// --------------------------- CONSTRUCTORS ---------------------------

    public WSException(String code, String desc) {
        super(desc);
        this.code = code;
        this.desc = desc;
    }

// --------------------- GETTER / SETTER METHODS ---------------------

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

// ------------------------ CANONICAL METHODS ------------------------

    @Override
    public String toString() {
        return "com.systex.jbranch.app.msg.cktech.WSException{" +
                "code='" + code + '\'' +
                ", desc='" + desc + '\'' +
                '}';
    }
}
