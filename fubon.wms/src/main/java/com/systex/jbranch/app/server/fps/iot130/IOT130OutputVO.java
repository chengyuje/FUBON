package com.systex.jbranch.app.server.fps.iot130;

import java.util.List;
import java.util.Map;

public class IOT130OutputVO {
	
	private List<Map<String, Object>> CNCTList;
	private List<Map<String, Object>> chkData;
	private List<Map<String, Object>> INS_INFORMATION;
	private List<Map<String, Object>> POLICY_Data;
	private List<Map<String, Object>> ExchangeRateList;
	private String INS_ID;
	private String INSKEY_NO;
	private List<Map<String, Object>> checkREMARK_INS;
	private List<Map<String, Object>> PreMatchList;
	private List<Map<String, Object>> INVESTList;

	
	public List<Map<String, Object>> getCheckREMARK_INS() {
		return checkREMARK_INS;
	}

	public void setCheckREMARK_INS(List<Map<String, Object>> checkREMARK_INS) {
		this.checkREMARK_INS = checkREMARK_INS;
	}

	public String getINS_ID() {
		return INS_ID;
	}

	public String getINSKEY_NO() {
		return INSKEY_NO;
	}

	public void setINS_ID(String iNS_ID) {
		INS_ID = iNS_ID;
	}

	public void setINSKEY_NO(String iNSKEY_NO) {
		INSKEY_NO = iNSKEY_NO;
	}

	public List<Map<String, Object>> getExchangeRateList() {
		return ExchangeRateList;
	}

	public void setExchangeRateList(List<Map<String, Object>> exchangeRateList) {
		ExchangeRateList = exchangeRateList;
	}

	public List<Map<String, Object>> getPOLICY_Data() {
		return POLICY_Data;
	}

	public void setPOLICY_Data(List<Map<String, Object>> pOLICY_Data) {
		POLICY_Data = pOLICY_Data;
	}

	public List<Map<String, Object>> getINS_INFORMATION() {
		return INS_INFORMATION;
	}

	public void setINS_INFORMATION(List<Map<String, Object>> iNS_INFORMATION) {
		INS_INFORMATION = iNS_INFORMATION;
	}

	public List<Map<String, Object>> getCNCTList() {
		return CNCTList;
	}

	public void setCNCTList(List<Map<String, Object>> cNCTList) {
		CNCTList = cNCTList;
	}

	public List<Map<String, Object>> getChkData() {
		return chkData;
	}

	public void setChkData(List<Map<String, Object>> chkData) {
		this.chkData = chkData;
	}

	public List<Map<String, Object>> getPreMatchList() {
		return PreMatchList;
	}

	public void setPreMatchList(List<Map<String, Object>> preMatchList) {
		PreMatchList = preMatchList;
	}

	public List<Map<String, Object>> getINVESTList() {
		return INVESTList;
	}

	public void setINVESTList(List<Map<String, Object>> iNVESTList) {
		INVESTList = iNVESTList;
	}
	
	
}
