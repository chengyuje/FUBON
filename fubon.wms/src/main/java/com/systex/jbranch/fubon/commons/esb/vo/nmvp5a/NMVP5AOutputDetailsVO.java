package com.systex.jbranch.fubon.commons.esb.vo.nmvp5a;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlElement;

/**
 * 台外幣定存明細查詢
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType
public class NMVP5AOutputDetailsVO {
	@XmlElement
	private String ACNO;			//定存帳號
	@XmlElement
	private String CD_NBR;			//定存單號
	@XmlElement
	private String CUR;				//幣別
	@XmlElement
	private String VALUE_DATE;		//起息日期
	@XmlElement
	private String DUE_DATE;		//到期日期
	@XmlElement
	private String INT_TYPE;		//固定/機動 1.固定  2.機動
	@XmlElement
	private String INT_RATE;		//利率
	@XmlElement
	private String CD_AMT;			//存單金額
	@XmlElement
	private String INT_ACNO;		//本利轉入帳號
	@XmlElement
	private String RENEW_TYPE;		//續存方式 1.本金展  2.本利展  空白表示不展期
	@XmlElement
	private String INT_DRAW_TYPE;	//利息領取方式 C1.到期  C2.按月  C3.按年
	
	public String getACNO() {
		return ACNO;
	}
	public void setACNO(String aCNO) {
		ACNO = aCNO;
	}
	public String getCD_NBR() {
		return CD_NBR;
	}
	public void setCD_NBR(String cD_NBR) {
		CD_NBR = cD_NBR;
	}
	public String getCUR() {
		return CUR;
	}
	public void setCUR(String cUR) {
		CUR = cUR;
	}
	public String getVALUE_DATE() {
		return VALUE_DATE;
	}
	public void setVALUE_DATE(String vALUE_DATE) {
		VALUE_DATE = vALUE_DATE;
	}
	public String getDUE_DATE() {
		return DUE_DATE;
	}
	public void setDUE_DATE(String dUE_DATE) {
		DUE_DATE = dUE_DATE;
	}
	public String getINT_TYPE() {
		return INT_TYPE;
	}
	public void setINT_TYPE(String iNT_TYPE) {
		INT_TYPE = iNT_TYPE;
	}
	public String getINT_RATE() {
		return INT_RATE;
	}
	public void setINT_RATE(String iNT_RATE) {
		INT_RATE = iNT_RATE;
	}
	public String getCD_AMT() {
		return CD_AMT;
	}
	public void setCD_AMT(String cD_AMT) {
		CD_AMT = cD_AMT;
	}
	public String getINT_ACNO() {
		return INT_ACNO;
	}
	public void setINT_ACNO(String iNT_ACNO) {
		INT_ACNO = iNT_ACNO;
	}
	public String getRENEW_TYPE() {
		return RENEW_TYPE;
	}
	public void setRENEW_TYPE(String rENEW_TYPE) {
		RENEW_TYPE = rENEW_TYPE;
	}
	public String getINT_DRAW_TYPE() {
		return INT_DRAW_TYPE;
	}
	public void setINT_DRAW_TYPE(String iNT_DRAW_TYPE) {
		INT_DRAW_TYPE = iNT_DRAW_TYPE;
	}
	
	
}