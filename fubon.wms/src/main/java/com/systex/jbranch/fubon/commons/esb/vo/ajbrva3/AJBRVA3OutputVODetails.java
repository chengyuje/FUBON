package com.systex.jbranch.fubon.commons.esb.vo.ajbrva3;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by Carley on 2024/07/26
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class AJBRVA3OutputVODetails {
    @XmlElement
    private String HaveTradeRec;        // 是否為首購 	Y:是 N:否
    @XmlElement
    private String HaveTradeRecToday;   // 是否為當日首購 	Y:是 N:否

    public String getHaveTradeRec() {
        return HaveTradeRec;
    }

    public void setHaveTradeRec(String haveTradeRec) {
        HaveTradeRec = haveTradeRec;
    }

    public String getHaveTradeRecToday() {
        return HaveTradeRecToday;
    }

    public void setHaveTradeRecToday(String haveTradeRecToday) {
        HaveTradeRecToday = haveTradeRecToday;
    }
}
