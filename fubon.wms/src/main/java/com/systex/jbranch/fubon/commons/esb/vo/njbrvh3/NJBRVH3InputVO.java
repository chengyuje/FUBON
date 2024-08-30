package com.systex.jbranch.fubon.commons.esb.vo.njbrvh3;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 業管發查各交易系統，取得客戶已委託高風險投資明細資訊
 * 
 * 以下交易電文代碼不同，但上下行內容相同
 * SI：WMSHAD001
 * DCI：WMSHAD003
 * 特金海外債&SN(DBU)：NJBRVH3
 * 特金海外債&SN(OBU)：AJBRVH3
 * 金市海外債：WMSHAD005
 * 境外私募基金：WMSHAD006
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class NJBRVH3InputVO {
    @XmlElement
    private String CUST_ID;   //身分證ID

	public String getCUST_ID() {
		return CUST_ID;
	}

	public void setCUST_ID(String cUST_ID) {
		CUST_ID = cUST_ID;
	}
    
}
