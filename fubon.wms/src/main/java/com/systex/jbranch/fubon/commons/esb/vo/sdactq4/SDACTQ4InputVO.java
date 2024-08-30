package com.systex.jbranch.fubon.commons.esb.vo.sdactq4;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by SebastianWu on 2016/10/5.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class SDACTQ4InputVO {
    @XmlElement
    private String IVID; //客戶證號

    public String getIVID() {
        return IVID;
    }

    public void setIVID(String IVID) {
        this.IVID = IVID;
    }


    @Override
    public String toString() {
        return "SDACTQ4InputVO{" +
                "IVID='" + IVID + '\'' +
                '}';
    }
}
