package com.systex.jbranch.fubon.commons.esb.vo.sdactq4;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.List;

/**
 * Created by SebastianWu on 2016/10/5.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class SDACTQ4OutputVO {
    @XmlElement
	private String ID;          //客戶證號

    @XmlElement(name = "TxRepeat")
    private List<SDACTQ4OutputDetailsVO> details;

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public List<SDACTQ4OutputDetailsVO> getDetails() {
        return details;
    }

    public void setDetails(List<SDACTQ4OutputDetailsVO> details) {
        this.details = details;
    }

    @Override
    public String toString() {
        return "SDACTQ4OutputVO{" +
                "ID='" + ID + '\'' +
                ", details=" + details +
                '}';
    }
}
