package com.systex.jbranch.app.server.fps.cmmgr014;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class CMMGR014OutputVO extends PagingOutputVO {
	
	public CMMGR014OutputVO()
	{
	}
	
	private List<Map<String, Object>> dataList;
	private List<Map<String, Object>> hostIdList;
	private List<Map<String, Object>> keyList;
	private String encryptKey = "null";


	public List<Map<String, Object>> getDataList() {
		return dataList;
	}
	public void setDataList(List<Map<String, Object>> dataList) {
		this.dataList = dataList;
	}
	public List<Map<String, Object>> getHostIdList() {
		return hostIdList;
	}
	public void setHostIdList(List<Map<String, Object>> hostIdList) {
		this.hostIdList = hostIdList;
	}
	public List<Map<String, Object>> getKeyList() {
		return keyList;
	}
	public void setKeyList(List<Map<String, Object>> keyList) {
		this.keyList = keyList;
	}
	public String getEncryptKey() {
		return encryptKey;
	}
	public void setEncryptKey(String encryptKey) {
		this.encryptKey = encryptKey;
	}
	
	
}
