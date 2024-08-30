package com.systex.jbranch.fubon.commons.esb.vo.wmshacr;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlElement;

/**
 * Created 2023/04/26
 * 高風險商品承作系統發查微服務，取得集中度資訊
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class WMSHACRInputVO {
	@XmlElement
	private String CUST_ID; //客戶統編
	@XmlElement
	private String PROD_TYPE; //1: SI 2: SN 3: DCI 4: 特金海外債 5: 金市海外債 6: 境外私募基金
	@XmlElement
	private String AMT; //當下申購金額(折台)
	
	
	public String getCUST_ID() {
		return CUST_ID;
	}
	public void setCUST_ID(String cUST_ID) {
		CUST_ID = cUST_ID;
	}
	public String getPROD_TYPE() {
		return PROD_TYPE;
	}
	public void setPROD_TYPE(String pROD_TYPE) {
		PROD_TYPE = pROD_TYPE;
	}
	public String getAMT() {
		return AMT;
	}
	public void setAMT(String aMT) {
		AMT = aMT;
	}
	
}