package com.systex.jbranch.fubon.commons.esb.vo.njbrvh3;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.systex.jbranch.fubon.commons.esb.vo.njbrvb1.NJBRVB1OutputVODetials;

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
public class NJBRVH3OutputVO {
	@XmlElement
    private String SP_REFLD;   //傳送序號
	@XmlElement
    private String CUST_ID;   //身分證ID
    @XmlElement
    private String OCCUR;   //筆數
    @XmlElement(name = "TxRepeat")
    private List<NJBRVH3OutputVODetails> details;
    
    
	public String getSP_REFLD() {
		return SP_REFLD;
	}
	public void setSP_REFLD(String sP_REFLD) {
		SP_REFLD = sP_REFLD;
	}
	public String getCUST_ID() {
		return CUST_ID;
	}
	public void setCUST_ID(String cUST_ID) {
		CUST_ID = cUST_ID;
	}
	public String getOCCUR() {
		return OCCUR;
	}
	public void setOCCUR(String oCCUR) {
		OCCUR = oCCUR;
	}
	public List<NJBRVH3OutputVODetails> getDetails() {
		return details;
	}
	public void setDetails(List<NJBRVH3OutputVODetails> details) {
		this.details = details;
	}   
    
}
