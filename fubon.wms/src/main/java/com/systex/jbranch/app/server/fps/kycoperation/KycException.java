package com.systex.jbranch.app.server.fps.kycoperation;

public class KycException extends Exception {
    private String code;
    private String message;

    public KycException(String code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() { return message; }
}
