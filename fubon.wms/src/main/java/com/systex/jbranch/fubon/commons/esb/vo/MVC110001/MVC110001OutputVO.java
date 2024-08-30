package com.systex.jbranch.fubon.commons.esb.vo.MVC110001;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by SamTu on 2021/11/10.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class MVC110001OutputVO {
	@XmlElement
	private String MESSAGE; // 回覆訊息('交易成功'/'交易失敗')
	@XmlElement
	private String MESSAGE_CODE; // 成功0000/失敗9999
	@XmlElement
	private String TX_DATE; // 交易日期
	@XmlElement
	private String TX_TIME; // 交易時間
	
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
	
	

}
