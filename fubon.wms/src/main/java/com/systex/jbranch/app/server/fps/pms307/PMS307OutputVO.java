package com.systex.jbranch.app.server.fps.pms307;

import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

/**
 * 
 * Copy Right Information : <br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.7.0 <br>
 * Description : 保險核實報表查詢OutputVO<br>
 * Comments Name : PMS307OutputVO.java<br>
 * Author :Kevin<br>
 * Date :2016年08月04日 <br>
 * Version : 1.01 <br>
 * Editor : Kevin<br>
 * Editor Date : 2017年01月31日<br>
 */
public class PMS307OutputVO extends PagingOutputVO {
	private List resultList;   //畫面結果
	private List list;
	private List csvList; // csv list
	private List AoCntLst;
    private String checked;
	
	
	
	
	public String getChecked() {
		return checked;
	}

	public void setChecked(String checked) {
		this.checked = checked;
	}

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
}
