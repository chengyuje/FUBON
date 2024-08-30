package com.systex.jbranch.app.server.fps.crm122;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class CRM122OutputVO  extends PagingOutputVO {
	private List<Map<String,Object>> resultList;
	private List<Map<String,Object>> MyAOsList;
	private List<Map<String,Object>> MyTodoLst;  //待辦事項
	private List<Map<String,Object>> MyAUMLst;   //AUM
	

	public List<Map<String,Object>> getResultList() {
		return resultList;
	}

	public void setResultList(List<Map<String,Object>> resultList) {
		this.resultList = resultList;
	}

	public List<Map<String,Object>> getMyAOsList() {
		return MyAOsList;
	}

	public void setMyAOsList(List<Map<String,Object>> myAOsList) {
		MyAOsList = myAOsList;
	}

	public List<Map<String,Object>> getMyTodoLst() {
		return MyTodoLst;
	}

	public void setMyTodoLst(List<Map<String,Object>> myTodoLst) {
		MyTodoLst = myTodoLst;
	}

	public List<Map<String,Object>> getMyAUMLst() {
		return MyAUMLst;
	}

	public void setMyAUMLst(List<Map<String,Object>> myAUMLst) {
		MyAUMLst = myAUMLst;
	}
	
}
