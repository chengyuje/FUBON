package com.systex.jbranch.fubon.commons.esb.vo.eb032168;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * 洗錢防制電文
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class EB032168OutputVO {
	private String CUST_NAME;		// 中文戶名
	private String ENG_NAME;		// 英文戶名
	private String CCD_FLG;			// AML洗錢及資恐風險評估結果
	private String CCD_PRE; 		// Pre Check洗錢及資恐風險評估結果
	private String LST_TX_DATE; 	// 最近Pre Check異動日期
	private String LST_TX_BRH; 		// 異動行
	private String LST_EMP_NAME; 	// 異動經辦
	private String RESP_DATA;	 	// 回應資料 new add, 因為CBS下行中英文混合，所以不轉直接回前台由前台轉。資料結構 6(中文)+8(日期)

	public String getCUST_NAME() {
		return CUST_NAME;
	}
	public void setCUST_NAME(String cUST_NAME) {
		CUST_NAME = cUST_NAME;
	}
	public String getENG_NAME() {
		return ENG_NAME;
	}
	public void setENG_NAME(String eNG_NAME) {
		ENG_NAME = eNG_NAME;
	}
	public String getCCD_FLG() {
		return CCD_FLG;
	}
	public void setCCD_FLG(String cCD_FLG) {
		CCD_FLG = cCD_FLG;
	}
	public String getCCD_PRE() {
		return CCD_PRE;
	}
	public void setCCD_PRE(String cCD_PRE) {
		CCD_PRE = cCD_PRE;
	}
	public String getLST_TX_DATE() {
		return LST_TX_DATE;
	}
	public void setLST_TX_DATE(String lST_TX_DATE) {
		LST_TX_DATE = lST_TX_DATE;
	}
	public String getLST_TX_BRH() {
		return LST_TX_BRH;
	}
	public void setLST_TX_BRH(String lST_TX_BRH) {
		LST_TX_BRH = lST_TX_BRH;
	}
	public String getLST_EMP_NAME() {
		return LST_EMP_NAME;
	}
	public void setLST_EMP_NAME(String lST_EMP_NAME) {
		LST_EMP_NAME = lST_EMP_NAME;
	}


	public String getRESP_DATA() {
		return RESP_DATA;
	}

	public void setRESP_DATA(String RESP_DATA) {
		this.RESP_DATA = RESP_DATA;
	}
}