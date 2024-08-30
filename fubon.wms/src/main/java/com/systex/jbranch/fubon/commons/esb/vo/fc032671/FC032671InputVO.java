package com.systex.jbranch.fubon.commons.esb.vo.fc032671;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlElement;

/**
 * Created by Walalala on 2016/12/06.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class FC032671InputVO {
	@XmlElement
	private String DATA_IND;	//資料類別
	@XmlElement
	private String INP_DATA;	//CUST_ID
	@XmlElement
	private String INP_CHIN;	//資料內容-中文
	@XmlElement
	private String ENQ_IND;	//查詢類別
	@XmlElement
	private String FUNC;	//功能
	@XmlElement
	private String SEL_IND;	//選擇
	@XmlElement
	private String CTRY_COD;	//分行別
	public String getDATA_IND() {
		return DATA_IND;
	}
	public void setDATA_IND(String dATA_IND) {
		DATA_IND = dATA_IND;
	}
	public String getINP_DATA() {
		return INP_DATA;
	}
	public void setINP_DATA(String iNP_DATA) {
		INP_DATA = iNP_DATA;
	}
	public String getINP_CHIN() {
		return INP_CHIN;
	}
	public void setINP_CHIN(String iNP_CHIN) {
		INP_CHIN = iNP_CHIN;
	}
	public String getENQ_IND() {
		return ENQ_IND;
	}
	public void setENQ_IND(String eNQ_IND) {
		ENQ_IND = eNQ_IND;
	}
	public String getFUNC() {
		return FUNC;
	}
	public void setFUNC(String fUNC) {
		FUNC = fUNC;
	}
	public String getSEL_IND() {
		return SEL_IND;
	}
	public void setSEL_IND(String sEL_IND) {
		SEL_IND = sEL_IND;
	}
	public String getCTRY_COD() {
		return CTRY_COD;
	}
	public void setCTRY_COD(String cTRY_COD) {
		CTRY_COD = cTRY_COD;
	}

	
}