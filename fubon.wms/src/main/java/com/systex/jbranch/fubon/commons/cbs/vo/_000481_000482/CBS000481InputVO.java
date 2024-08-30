package com.systex.jbranch.fubon.commons.cbs.vo._000481_000482;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class CBS000481InputVO {
    private String accntNumber1;
    private String DefInteger1 = "1";

    public String getAccntNumber1() {
        return accntNumber1;
    }

    public void setAccntNumber1(String accntNumber1) {
        this.accntNumber1 = accntNumber1;
    }

    public String getDefInteger1() {
        return DefInteger1;
    }

    public void setDefInteger1(String defInteger1) {
        DefInteger1 = defInteger1;
    }
}
