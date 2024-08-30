package com.systex.jbranch.app.server.fps.pms332;

import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

/**
 * 
 * Copy Right Information : <br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.7.0 <br>
 * Description : 百大貢獻度客戶報表OutputVO<br>
 * Comments Name : PMS332OutputVO.java<br>
 * Author :Kevin<br>
 * Date :2016年06月03日 <br>
 * Version : 1.01 <br>
 * Editor : Kevin<br>
 * Editor Date : 2017年01月31日<br>
 */
public class PMS332OutputVO extends PagingOutputVO {
	private List resultList;	//主分頁查詢list
	private List list;		//暫存匯出用list
	private List csvList;  //匯出用csv list  

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
