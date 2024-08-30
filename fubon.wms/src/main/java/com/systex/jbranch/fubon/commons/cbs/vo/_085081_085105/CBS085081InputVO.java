package com.systex.jbranch.fubon.commons.cbs.vo._085081_085105;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class CBS085081InputVO {
    @XmlElement
    private String tib_ID1;

    @XmlElement
    private String DefaultString1;

    @XmlElement
    private String DefaultString2;

    @XmlElement
    private String CIFOption1 = "1";

    @XmlElement
    private String yesno1 = "N";

    public String getTib_ID1() {
        return tib_ID1;
    }

    public void setTib_ID1(String tib_ID1) {
        this.tib_ID1 = tib_ID1;
    }

    public String getDefaultString1() {
        return DefaultString1;
    }

    public void setDefaultString1(String defaultString1) {
        DefaultString1 = defaultString1;
    }

    public String getDefaultString2() {
        return DefaultString2;
    }

    public void setDefaultString2(String defaultString2) {
        DefaultString2 = defaultString2;
    }

    public String getCIFOption1() {
        return CIFOption1;
    }

    public void setCIFOption1(String CIFOption1) {
        this.CIFOption1 = CIFOption1;
    }

    public String getYesno1() {
        return yesno1;
    }

    public void setYesno1(String yesno1) {
        this.yesno1 = yesno1;
    }
}
