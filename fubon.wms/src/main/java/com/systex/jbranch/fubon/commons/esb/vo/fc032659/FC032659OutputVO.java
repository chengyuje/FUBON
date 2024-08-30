package com.systex.jbranch.fubon.commons.esb.vo.fc032659;

import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlElement;

/**
 * Created by Valentino on 2017/03/29.
 *
 * @version 2017/03/29 Valentino 
 
 * FC032659新版電文格式
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class FC032659OutputVO {
	 
	@XmlElement
	private String CUST_NO;// 客戶統編
	@XmlElement
	private String SEQ;// 查詢類別
	@XmlElement
	private String COD;// 查詢代碼
	@XmlElement
	private String ADV_COD;// 債清註記 
	@XmlElement(name="TxRepeat")
	private List<FC032659OutputDetailsVO> details;
	public String getCUST_NO() {
		return CUST_NO;
	}
	public void setCUST_NO(String cUST_NO) {
		CUST_NO = cUST_NO;
	}
	public String getSEQ() {
		return SEQ;
	}
	public void setSEQ(String sEQ) {
		SEQ = sEQ;
	}
	public String getCOD() {
		return COD;
	}
	public void setCOD(String cOD) {
		COD = cOD;
	}
	public String getADV_COD() {
		return ADV_COD;
	}
	public void setADV_COD(String aDV_COD) {
		ADV_COD = aDV_COD;
	}
	public List<FC032659OutputDetailsVO> getDetails() {
		return details;
	}
	public void setDetails(List<FC032659OutputDetailsVO> details) {
		this.details = details;
	}
	
	
	
}