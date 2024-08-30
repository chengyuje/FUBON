package com.systex.jbranch.fubon.commons.cbs.vo._017050_017000;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class CBS017050InputVO {
    /** 貸款帳號 **/
    private String accntNumber1;
    private String DefaultString1 = "1";

    public String getAccntNumber1() {
        return accntNumber1;
    }

    public void setAccntNumber1(String accntNumber1) {
        this.accntNumber1 = accntNumber1;
    }

    public String getDefaultString1() {
        return DefaultString1;
    }

    public void setDefaultString1(String defaultString1) {
        DefaultString1 = defaultString1;
    }
}
