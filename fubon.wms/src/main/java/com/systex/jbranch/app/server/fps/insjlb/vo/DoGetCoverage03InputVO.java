package com.systex.jbranch.app.server.fps.insjlb.vo;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.app.server.fps.ins140.INS140InputVO;


@SuppressWarnings("rawtypes")
public class DoGetCoverage03InputVO {
	private String insCustID;//客戶ID
	private String callType;//呼叫類型(1: 要保人'得到全家',2: 被保險人)
	private String includeAll;//是否包含非家庭戶(個人)資料(Y: 是,N: 否 ,空白/NULL：亦否)	
	private List<Map> lstInsData;//客戶保單資訊
	private List lstException;//例外清單
	private List<Map<String , Object>> lstFamily;//家庭戶客戶id清單
	private String doSave;//是否存檔(Y: 存檔,N: 不存檔,空白/null：亦不存檔)
	
	public String getInsCustID() {
		return insCustID;
	}
	public void setInsCustID(String insCustID) {
		this.insCustID = insCustID;
	}
	public String getCallType() {
		return callType;
	}
	public void setCallType(String callType) {
		this.callType = callType;
	}
	public String getIncludeAll() {
		return includeAll;
	}
	public void setIncludeAll(String includeAll) {
		this.includeAll = includeAll;
	}
	public List<Map> getLstInsData() {
		return lstInsData;
	}
	public void setLstInsData(List<Map> lstInsData) {
		this.lstInsData = lstInsData;
	}
	public List getLstException() {
		return lstException;
	}
	public void setLstException(List lstException) {
		this.lstException = lstException;
	}
	public List<Map<String , Object>> getLstFamily() {
		return lstFamily;
	}
	public void setLstFamily(List<Map<String , Object>> lstFamily) {
		this.lstFamily = lstFamily;
	}
	public String getDoSave() {
		return doSave;
	}
	public void setDoSave(String doSave) {
		this.doSave = doSave;
	}
}