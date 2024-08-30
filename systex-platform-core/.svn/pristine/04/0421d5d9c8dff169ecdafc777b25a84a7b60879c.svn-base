package com.systex.jbranch.platform.common.initiation;

import java.io.PrintStream;

/**
 * @author Alex Lin
 * @version 2010/02/08 1:02:04 PM
 */
public class EncodingInitiator implements InitiatorIF {
// ------------------------------ FIELDS ------------------------------

    private String encoding;

// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface InitiatorIF ---------------------

    public void execute() throws Exception {
        PrintStream out = new PrintStream(System.out, true, encoding);
        System.setOut(out);
        PrintStream err = new PrintStream(System.err, true, encoding);
        System.setErr(err);
        System.setProperty("file.encoding", encoding);
    }

// --------------------- GETTER / SETTER METHODS ---------------------

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }
}
