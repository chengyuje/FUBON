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
public class MVC310001OutputVO {
	@XmlElement
	private String MESSAGE; // 回覆訊息('交易成功'/'交易失敗')
	@XmlElement
	private String MESSAGE_CODE; // 成功0000/失敗9999
	@XmlElement
	private String TX_DATE; // 交易日期
	@XmlElement
	private String TX_TIME; // 交易時間
	@XmlElement
	private String STATUS; // 交易整體狀態
	@XmlElement
	private String TX_STATUS; // 交易明細狀態
	
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
		
	public String getMESSAGE() {
		return MESSAGE;
	}
	public void setMESSAGE(String mESSAGE) {
		MESSAGE = mESSAGE;
	}
	public String getMESSAGE_CODE() {
		return MESSAGE_CODE;
	}
	public void setMESSAGE_CODE(String mESSAGE_CODE) {
		MESSAGE_CODE = mESSAGE_CODE;
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
	
	

}
