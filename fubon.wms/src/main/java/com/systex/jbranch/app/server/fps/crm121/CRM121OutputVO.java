package com.systex.jbranch.app.server.fps.crm121;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class CRM121OutputVO  extends PagingOutputVO {
	private String privilege;					 //分類
	private List<Map<String,Object>> resultList; //行事曆
	private List<Map<String,Object>> resultList2;
	private List<Map<String,Object>> MyTodoLst;  //待辦事項
	private List<Map<String,Object>> MyAUMLst;   //AUM
	
	
	public String getPrivilege() {
		return privilege;
	}
	public void setPrivilege(String privilege) {
		this.privilege = privilege;
	}
	public List<Map<String, Object>> getResultList() {
		return resultList;
	}
	public void setResultList(List<Map<String, Object>> resultList) {
		this.resultList = resultList;
	}
	public List<Map<String, Object>> getResultList2() {
		return resultList2;
	}
	public void setResultList2(List<Map<String, Object>> resultList2) {
		this.resultList2 = resultList2;
	}
	public List<Map<String, Object>> getMyTodoLst() {
		return MyTodoLst;
	}
	public void setMyTodoLst(List<Map<String, Object>> myTodoLst) {
		MyTodoLst = myTodoLst;
	}
	public List<Map<String, Object>> getMyAUMLst() {
		return MyAUMLst;
	}
	public void setMyAUMLst(List<Map<String, Object>> myAUMLst) {
		MyAUMLst = myAUMLst;
	}
}