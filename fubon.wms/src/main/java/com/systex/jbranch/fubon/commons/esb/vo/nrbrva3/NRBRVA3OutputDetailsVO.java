package com.systex.jbranch.fubon.commons.esb.vo.nrbrva3;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlElement;

/**
 * Created by Ocean on 2016/09/22.
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType
public class NRBRVA3OutputDetailsVO {
	
	@XmlElement
	private String PROD_ID; //商品代號
	
	@XmlElement
	private String APPLY_SEQ; //議價編號
	
	@XmlElement
	private String UNIT; //委託數量
	
	@XmlElement
	private String PRICE; //委託價格
	
	@XmlElement
	private String FEE_RATE; //折扣費率
	
	@XmlElement
	private String FEE_DISCOUNT; //折扣數
	
	@XmlElement
	private String FEE; //手續費金額
	
	@XmlElement
	private String DISCOUNT_TYPE; //折扣方式

	public String getPROD_ID() {
		return PROD_ID;
	}

	public void setPROD_ID(String pROD_ID) {
		PROD_ID = pROD_ID;
	}

	public String getAPPLY_SEQ() {
		return APPLY_SEQ;
	}

	public void setAPPLY_SEQ(String aPPLY_SEQ) {
		APPLY_SEQ = aPPLY_SEQ;
	}

	public String getUNIT() {
		return UNIT;
	}

	public void setUNIT(String uNIT) {
		UNIT = uNIT;
	}

	public String getPRICE() {
		return PRICE;
	}

	public void setPRICE(String pRICE) {
		PRICE = pRICE;
	}

	public String getFEE_RATE() {
		return FEE_RATE;
	}

	public void setFEE_RATE(String fEE_RATE) {
		FEE_RATE = fEE_RATE;
	}

	public String getFEE_DISCOUNT() {
		return FEE_DISCOUNT;
	}

	public void setFEE_DISCOUNT(String fEE_DISCOUNT) {
		FEE_DISCOUNT = fEE_DISCOUNT;
	}

	public String getFEE() {
		return FEE;
	}

	public void setFEE(String fEE) {
		FEE = fEE;
	}

	public String getDISCOUNT_TYPE() {
		return DISCOUNT_TYPE;
	}

	public void setDISCOUNT_TYPE(String dISCOUNT_TYPE) {
		DISCOUNT_TYPE = dISCOUNT_TYPE;
	}
}