package com.systex.jbranch.fubon.commons.esb.vo.njbrvc2;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 長效單查詢明細上行電文
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class NJBRVC2InputVO {
    @XmlElement
    private String GtcNo;   //長效單號

	public String getGtcNo() {
		return GtcNo;
	}

	public void setGtcNo(String gtcNo) {
		GtcNo = gtcNo;
	}
        
}
