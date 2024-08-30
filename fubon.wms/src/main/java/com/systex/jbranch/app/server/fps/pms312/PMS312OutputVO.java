package com.systex.jbranch.app.server.fps.pms312;

import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;
/**
 * 
 * Copy Right Information :  <br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.7.0 <br>
 * Description : 房信貸案件來源統計表OutputVO<br>
 * Comments Name : PMS312OutputVO.java<br>
 * Author :Kevin<br>
 * Date :2016年08月02日 <br>
 * Version : 1.01 <br>
 * Editor : Kevin<br>
 * Editor Date : 2017年08月02日<br>
 */
public class PMS312OutputVO extends PagingOutputVO{
	private List resultList;   //儲存
	private List resultList2;	
	private List list; 
	private List csvList;   //暫存csv list
	private List AoCntLst;
	public List getAoCntLst() {
		return AoCntLst;
	}
	public void setAoCntLst(List aoCntLst) {
		AoCntLst = aoCntLst;
	}
	public List getList() {
		return list;
	}
	public void setList(List list) {
		this.list = list;
	}
	public List getResultList() {
		return resultList;
	}
	public List getCsvList() {
		return csvList;
	}
	public void setCsvList(List csvList) {
		this.csvList = csvList;
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
}
