package com.systex.jbranch.fubon.commons.esb.vo.nb052650;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by SebastianWu on 2016/10/18.
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType
public class NB052650OutputVODetails {
    @XmlElement
    private String DATA;    //查詢結果

    public String getDATA() {
        return DATA;
    }

    public void setDATA(String DATA) {
        this.DATA = DATA;
    }
}
