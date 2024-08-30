package com.systex.jbranch.fubon.commons.esb.vo.njbrva3;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by SebastianWu on 2016/11/18.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class NJBRVA3OutputVODetails {
    @XmlElement
    private String HaveTradeRec;        //是否為首購 Y:是 N:否
    @XmlElement
    private String HaveTradeRecToday;   //是否為當日首購 Y:是 N:否

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
