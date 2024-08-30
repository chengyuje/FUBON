package com.systex.jbranch.app.server.fps.pms315;

import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

/**
 * 
 * Copy Right Information : <br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.7.0 <br>
 * Description : 保單續期保費名單OutputVO<br>
 * Comments Name : PMS315OutputVO.java<br>
 * Author :Miler<br>
 * Date :2017年08月21日 <br>
 * Version : 1.01 <br>
 * Editor : Miler<br>
 * Editor Date : 2017年08月21日<br>
 */
public class PMS315OutputVO extends PagingOutputVO {
	private List resultList; // 查詢分頁結果
	private List list; // csv 結果List
	private List csvList; // csv 暫存List
	private List ymList;
	public List getResultList() {
		return resultList;
	}
	public void setResultList(List resultList) {
		this.resultList = resultList;
	}
	public List getList() {
		return list;
	}
	public void setList(List list) {
		this.list = list;
	}
	public List getCsvList() {
		return csvList;
	}
	public void setCsvList(List csvList) {
		this.csvList = csvList;
	}
	public List getYmList() {
		return ymList;
	}
	public void setYmList(List ymList) {
		this.ymList = ymList;
	}
	
	
	
}