package com.systex.jbranch.fubon.commons.cbs.vo._010400_032105;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class CBS010400InputVO {
    /** 貸款帳號 **/
    private String accntNumber1;
    /** 就學貸款上送學年學期別用 **/
    private String yearterm;
    private String DefaultString1 = "1";

    public String getAccntNumber1() {
        return accntNumber1;
    }

    public void setAccntNumber1(String accntNumber1) {
        this.accntNumber1 = accntNumber1;
    }



    public String getYearterm() {
		return yearterm;
	}

	public void setYearterm(String yearterm) {
		this.yearterm = yearterm;
	}

	public String getDefaultString1() {
        return DefaultString1;
    }

    public void setDefaultString1(String defaultString1) {
        DefaultString1 = defaultString1;
    }
}
