package com.systex.jbranch.app.server.fps.crm614;

import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class CRM614OutputVO extends PagingOutputVO {
	private String msgCust;    //增加客戶id訊息
	private List resultList;
	private List resultList2;
	private List resultList3;
	private List resultList4;
	public List getResultList() {
		return resultList;
	}
	public void setResultList(List resultList) {
		this.resultList = resultList;
	}
	public List getResultList2() {
		return resultList2;
	}
	public void setResultList2(List resultList2) {
		this.resultList2 = resultList2;
	}
	public List getResultList3() {
		return resultList3;
	}
	public void setResultList3(List resultList3) {
		this.resultList3 = resultList3;
	}
	public List getResultList4() {
		return resultList4;
	}
	public void setResultList4(List resultList4) {
		this.resultList4 = resultList4;
	}
	public String getMsgCust() {
		return msgCust;
	}
	public void setMsgCust(String msgCust) {
		this.msgCust = msgCust;
	}
}
