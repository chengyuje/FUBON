package com.systex.jbranch.app.server.fps.cmmgr005;

import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

@SuppressWarnings({"rawtypes"})
public class CMMGR005OutputVO extends PagingOutputVO{
	private List queryList;
	private List messageList;
	private List ap1List;
	private List ap2List;
	private List errList;
	
	public List getQueryList() {
		return queryList;
	}
	public void setQueryList(List queryList) {
		this.queryList = queryList;
	}
	public List getMessageList() {
		return messageList;
	}
	public void setMessageList(List messageList) {
		this.messageList = messageList;
	}
	public void setAp1List(List ap1List) {
		this.ap1List = ap1List;
	}
	public void setAp2List(List ap2List) {
		this.ap2List = ap2List;
	}
	public void setErrList(List errList) {
		this.errList = errList;
	}
}
