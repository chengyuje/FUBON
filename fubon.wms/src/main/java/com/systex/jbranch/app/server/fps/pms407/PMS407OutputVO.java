package com.systex.jbranch.app.server.fps.pms407;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class PMS407OutputVO extends PagingOutputVO{
	private List resultList;
	private List totalList;
	private List msgList;
	
	
	
	private List<Map<String, String>> orgList;

	public List getResultList() {
		return resultList;
	}
	public void setResultList(List resultList) {
		this.resultList = resultList;
	}
	public List getTotalList() {
		return totalList;
	}
	public void setTotalList(List totalList) {
		this.totalList = totalList;
	}
	public List getMsgList() {
		return msgList;
	}
	public void setMsgList(List msgList) {
		this.msgList = msgList;
	}
	public List getOrgList() {
		return orgList;
	}
	public void setOrgList(List orgList) {
		this.orgList = orgList;
	}
}
