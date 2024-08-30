package com.systex.jbranch.fubon.commons.esb.vo.sdactq3;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by SebastianWu on 2016/10/5.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class SDACTQ3OutputVO {
    @XmlElement
	private String ERRID;   //處理結果
    @XmlElement
	private String ERRTXT;  //錯誤代碼說明

    public String getERRID() {
        return ERRID;
    }

    public void setERRID(String ERRID) {
        this.ERRID = ERRID;
    }

    public String getERRTXT() {
        return ERRTXT;
    }

    public void setERRTXT(String ERRTXT) {
        this.ERRTXT = ERRTXT;
    }

    @Override
    public String toString() {
        return "SDACTQ3OutputVO{" +
                "ERRID='" + ERRID + '\'' +
                ", ERRTXT='" + ERRTXT + '\'' +
                '}';
    }
}
