package com.systex.jbranch.fubon.commons.esb.vo.sdactq8;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.math.BigDecimal;

/**
 * Created by CathyTang on 2018/09/05.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class SDACTQ8InputVO {
	@XmlElement
	private String SDIVID;		//客戶證號
          
	public String getSDIVID() {
		return SDIVID;
	}

	public void setSDIVID(String sDIVID) {
		SDIVID = sDIVID;
	}

	@Override
    public String toString() {
        return "SDACTQ8InputVO{" +
        		" SDIVID='" + SDIVID + '\'' +
                '}';
    }
}
