package com.systex.jbranch.app.server.fps.pms333;

import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

/**
 * 
 * Copy Right Information : <br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.7.0 <br>
 * Description : 未掛Code客戶報表OutputVO<br>
 * Comments Name : PMS333OutputVO.java<br>
 * Author :Kevin<br>
 * Date :2016年09月29日 <br>
 * Version : 1.01 <br>
 * Editor : Kevin<br>
 * Editor Date : 2017年01月31日<br>
 */
public class PMS333OutputVO extends PagingOutputVO {
	private List resultList; // 分頁結果
	private List list; // csv結果
	private List csvList; // 暫存csv

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
