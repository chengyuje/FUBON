package com.systex.jbranch.fubon.commons.esb.vo.MVC310001;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by SamTu on 2021/11/10.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class MVC310001InputVO {

	@XmlElement
	private String UUID; // 該筆交易UUID
	@XmlElement
	private String BRANCH; // 交易行
	@XmlElement
	private String TELLER_NO; // 櫃員代號
	@XmlElement
	private String TX_CODE; // "交易代號	前台(110001-新增/310001-查詢當前/310002-查詢歷史) 	mailhunter(120001-客戶確認回應)"
	@XmlElement
	private String CHNL; // 通路
	@XmlElement
	private String SUB_CHNL; // 子通路
	@XmlElement
	private String CUST_ID; // 身份證統一編號/居留證號
	@XmlElement
	private String ID_TYPE; // ID種類
	@XmlElement
	private String CUST_NAME; // 中文戶名(全部全形)
	@XmlElement
	private String ENG_NAME; // 英文戶名(全部全形或全部半形)
	@XmlElement
	private String PREV_EMAIL_ADDR; // 變更前EMAIL
	@XmlElement
	private String AFTER_EMAIL_ADDR; // 變更後EMAIL
	@XmlElement
	private String FROM_DATE; // 查詢交易起日期
	@XmlElement
	private String TO_DATE; // 查詢交易訖日期
	@XmlElement
	private String REASON; // 相同原因(1-父母、2-配偶、3-子女、4-擔任負責人之企業/組織、5-其他(請說明))
	@XmlElement
	private String REMARK; // 備註
	@XmlElement
	private String ON_OFF_LINE; // 線上:Y，線下:N
	@XmlElement
	private String QUERY_UUID; // 查詢UUID
	@XmlElement
	private String NEXT_KEY; // NextKey
	
	public String getUUID() {
		return UUID;
	}
	public void setUUID(String uUID) {
		UUID = uUID;
	}
	public String getBRANCH() {
		return BRANCH;
	}
	public void setBRANCH(String bRANCH) {
		BRANCH = bRANCH;
	}
	public String getTELLER_NO() {
		return TELLER_NO;
	}
	public void setTELLER_NO(String tELLER_NO) {
		TELLER_NO = tELLER_NO;
	}
	public String getTX_CODE() {
		return TX_CODE;
	}
	public void setTX_CODE(String tX_CODE) {
		TX_CODE = tX_CODE;
	}
	public String getCHNL() {
		return CHNL;
	}
	public void setCHNL(String cHNL) {
		CHNL = cHNL;
	}
	public String getSUB_CHNL() {
		return SUB_CHNL;
	}
	public void setSUB_CHNL(String sUB_CHNL) {
		SUB_CHNL = sUB_CHNL;
	}
	public String getCUST_ID() {
		return CUST_ID;
	}
	public void setCUST_ID(String cUST_ID) {
		CUST_ID = cUST_ID;
	}
	public String getID_TYPE() {
		return ID_TYPE;
	}
	public void setID_TYPE(String iD_TYPE) {
		ID_TYPE = iD_TYPE;
	}
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
	public String getPREV_EMAIL_ADDR() {
		return PREV_EMAIL_ADDR;
	}
	public void setPREV_EMAIL_ADDR(String pREV_EMAIL_ADDR) {
		PREV_EMAIL_ADDR = pREV_EMAIL_ADDR;
	}
	public String getAFTER_EMAIL_ADDR() {
		return AFTER_EMAIL_ADDR;
	}
	public void setAFTER_EMAIL_ADDR(String aFTER_EMAIL_ADDR) {
		AFTER_EMAIL_ADDR = aFTER_EMAIL_ADDR;
	}
	public String getFROM_DATE() {
		return FROM_DATE;
	}
	public void setFROM_DATE(String fROM_DATE) {
		FROM_DATE = fROM_DATE;
	}
	public String getTO_DATE() {
		return TO_DATE;
	}
	public void setTO_DATE(String tO_DATE) {
		TO_DATE = tO_DATE;
	}
	public String getREASON() {
		return REASON;
	}
	public void setREASON(String rEASON) {
		REASON = rEASON;
	}
	public String getREMARK() {
		return REMARK;
	}
	public void setREMARK(String rEMARK) {
		REMARK = rEMARK;
	}
	public String getON_OFF_LINE() {
		return ON_OFF_LINE;
	}
	public void setON_OFF_LINE(String oN_OFF_LINE) {
		ON_OFF_LINE = oN_OFF_LINE;
	}
	public String getQUERY_UUID() {
		return QUERY_UUID;
	}
	public void setQUERY_UUID(String qUERY_UUID) {
		QUERY_UUID = qUERY_UUID;
	}
	public String getNEXT_KEY() {
		return NEXT_KEY;
	}
	public void setNEXT_KEY(String nEXT_KEY) {
		NEXT_KEY = nEXT_KEY;
	}

}
