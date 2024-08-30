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
@XmlAccessorType(XmlAccessType.NONE)
@XmlType
public class NJBRVH3OutputVODetails {
    @XmlElement
	private String BUY_DATE; //委託日期
    @XmlElement
	private String STATUS; //委託狀態
    @XmlElement
	private String PROD_CAT; //商品類型
    @XmlElement
	private String TRANS_TYPE; //交易類型
    @XmlElement
	private String PROD_ID; //債券代號
    @XmlElement
	private String PROD_NAME; //債券名稱
    @XmlElement
	private String CURR_ID; //計價幣別
    @XmlElement
	private String AMT_ORG; //原幣申購金額
    @XmlElement
	private String AMT_TWD; //台幣申購金額
    
    
	public String getBUY_DATE() {
		return BUY_DATE;
	}
	public void setBUY_DATE(String bUY_DATE) {
		BUY_DATE = bUY_DATE;
	}
	public String getSTATUS() {
		return STATUS;
	}
	public void setSTATUS(String sTATUS) {
		STATUS = sTATUS;
	}
	public String getPROD_CAT() {
		return PROD_CAT;
	}
	public void setPROD_CAT(String pROD_CAT) {
		PROD_CAT = pROD_CAT;
	}
	public String getTRANS_TYPE() {
		return TRANS_TYPE;
	}
	public void setTRANS_TYPE(String tRANS_TYPE) {
		TRANS_TYPE = tRANS_TYPE;
	}
	public String getPROD_ID() {
		return PROD_ID;
	}
	public void setPROD_ID(String pROD_ID) {
		PROD_ID = pROD_ID;
	}
	public String getPROD_NAME() {
		return PROD_NAME;
	}
	public void setPROD_NAME(String pROD_NAME) {
		PROD_NAME = pROD_NAME;
	}
	public String getCURR_ID() {
		return CURR_ID;
	}
	public void setCURR_ID(String cURR_ID) {
		CURR_ID = cURR_ID;
	}
	public String getAMT_ORG() {
		return AMT_ORG;
	}
	public void setAMT_ORG(String aMT_ORG) {
		AMT_ORG = aMT_ORG;
	}
	public String getAMT_TWD() {
		return AMT_TWD;
	}
	public void setAMT_TWD(String aMT_TWD) {
		AMT_TWD = aMT_TWD;
	}
    
	        
}
