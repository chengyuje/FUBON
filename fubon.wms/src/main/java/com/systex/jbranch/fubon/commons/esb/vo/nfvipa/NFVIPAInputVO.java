package com.systex.jbranch.fubon.commons.esb.vo.nfvipa;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by SebastianWu on 2016/9/23.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class NFVIPAInputVO {
    @XmlElement
	private String FUNCTION; //功能碼
    @XmlElement
	private String RANGE; //範圍
    @XmlElement
	private String CUSID; //身分證字號
    @XmlElement
	private String UNIT; //金融單位

    public String getFUNCTION() {
        return FUNCTION;
    }

    public void setFUNCTION(String FUNCTION) {
        this.FUNCTION = FUNCTION;
    }

    public String getRANGE() {
        return RANGE;
    }

    public void setRANGE(String RANGE) {
        this.RANGE = RANGE;
    }

    public String getCUSID() {
        return CUSID;
    }

    public void setCUSID(String CUSID) {
        this.CUSID = CUSID;
    }

    public String getUNIT() {
        return UNIT;
    }

    public void setUNIT(String UNIT) {
        this.UNIT = UNIT;
    }

    @Override
    public String toString() {
        return "NFVIPAInputVO{" +
                "FUNCTION='" + FUNCTION + '\'' +
                ", RANGE='" + RANGE + '\'' +
                ", CUSID='" + CUSID + '\'' +
                ", UNIT='" + UNIT + '\'' +
                '}';
    }
}
