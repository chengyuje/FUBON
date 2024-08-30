package com.systex.jbranch.fubon.commons.esb.vo.MVC310002;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by SamTu on 2021/11/10.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class MVC310002OutputDetailsVO {
	@XmlElement
	private String PREV_EMAIL_ADDR; // 變更前EMAIL
	@XmlElement
	private String AFTER_EMAIL_ADDR; // 變更後EMAIL
	@XmlElement
	private String CHNL_NAME; // 通路名稱
	@XmlElement
	private String REASON; // 理由
	@XmlElement
	private String TX_DATE; // 交易日期
	@XmlElement
	private String TX_TIME; // 交易日期
	@XmlElement
	private String QUERY_UUID; // 查詢UUID
	@XmlElement
	private String ON_OFF_LINE; // 是否已即時更新
	@XmlElement
	private String STATUS; // 交易整體狀態
	@XmlElement
	private String TX_STATUS; // 交易明細狀態
	@XmlElement
	private String DESCRIPTION; // 狀態敘述
	@XmlElement
	private String ERR_CODE; // 錯誤代碼
	@XmlElement
	private String REMARK; // 備註
	
	/**********************************************
	 * STATUS                      TX_STATUS
	 * 00:處理中                                  01-收到申請     
	 *                             11-發送至EMAIL SERVER
	 *                            15-收到客戶確認
	 *                            21-發送核心
	 *                            31-發送回應前台
	 *******************************************
	 * 01成功完成	              00-全部完成
	 *******************************************
	 * 02失敗                                      11-發送至EMAIL SERVER
	 *                            15-收到客戶確認
	 *                            21-發送核心
	 *                            31-發送回應前台
	 ******************************************* 
	 * 99 作廢	                  don’t care ，因為此筆已經被作廢
	 *****************************************
	 */
	
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
	public String getCHNL_NAME() {
		return CHNL_NAME;
	}
	public void setCHNL_NAME(String cHNL_NAME) {
		CHNL_NAME = cHNL_NAME;
	}
	public String getREASON() {
		return REASON;
	}
	public void setREASON(String rEASON) {
		REASON = rEASON;
	}
	public String getTX_DATE() {
		return TX_DATE;
	}
	public void setTX_DATE(String tX_DATE) {
		TX_DATE = tX_DATE;
	}
	public String getTX_TIME() {
		return TX_TIME;
	}
	public void setTX_TIME(String tX_TIME) {
		TX_TIME = tX_TIME;
	}
	public String getQUERY_UUID() {
		return QUERY_UUID;
	}
	public void setQUERY_UUID(String qUERY_UUID) {
		QUERY_UUID = qUERY_UUID;
	}
	public String getON_OFF_LINE() {
		return ON_OFF_LINE;
	}
	public void setON_OFF_LINE(String oN_OFF_LINE) {
		ON_OFF_LINE = oN_OFF_LINE;
	}
	public String getSTATUS() {
		return STATUS;
	}
	public void setSTATUS(String sTATUS) {
		STATUS = sTATUS;
	}
	public String getTX_STATUS() {
		return TX_STATUS;
	}
	public void setTX_STATUS(String tX_STATUS) {
		TX_STATUS = tX_STATUS;
	}
	public String getDESCRIPTION() {
		return DESCRIPTION;
	}
	public void setDESCRIPTION(String dESCRIPTION) {
		DESCRIPTION = dESCRIPTION;
	}
	public String getERR_CODE() {
		return ERR_CODE;
	}
	public void setERR_CODE(String eRR_CODE) {
		ERR_CODE = eRR_CODE;
	}
	public String getREMARK() {
		return REMARK;
	}
	public void setREMARK(String rEMARK) {
		REMARK = rEMARK;
	}
	
	

}
