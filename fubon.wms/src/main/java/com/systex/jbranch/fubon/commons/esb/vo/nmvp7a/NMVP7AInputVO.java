package com.systex.jbranch.fubon.commons.esb.vo.nmvp7a;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class NMVP7AInputVO {
	
	@XmlElement
	private String P_TYPE;		//產品代碼
	
	@XmlElement
	private String PRD_ID;		//商品代號
	
	@XmlElement
	private String PRD_TYPE;	//產品類別
	
	@XmlElement
	private String CUST_REMARKS;//弱勢
	
	@XmlElement
	private String CUST_ID;		//客戶統編

	public String getP_TYPE() {
		return P_TYPE;
	}

	public void setP_TYPE(String p_TYPE) {
		P_TYPE = p_TYPE;
	}

	public String getPRD_ID() {
		return PRD_ID;
	}

	public void setPRD_ID(String pRD_ID) {
		PRD_ID = pRD_ID;
	}

	public String getPRD_TYPE() {
		return PRD_TYPE;
	}

	public void setPRD_TYPE(String pRD_TYPE) {
		PRD_TYPE = pRD_TYPE;
	}

	public String getCUST_REMARKS() {
		return CUST_REMARKS;
	}

	public void setCUST_REMARKS(String cUST_REMARKS) {
		CUST_REMARKS = cUST_REMARKS;
	}

	public String getCUST_ID() {
		return CUST_ID;
	}

	public void setCUST_ID(String cUST_ID) {
		CUST_ID = cUST_ID;
	}
		
}