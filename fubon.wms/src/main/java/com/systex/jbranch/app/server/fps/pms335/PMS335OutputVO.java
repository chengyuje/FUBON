package com.systex.jbranch.app.server.fps.pms335;

import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;
/**
 * 
 * Copy Right Information : <br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.7.0 <br>
 * Description : 重覆經營客戶報表OutputVO<br>
 * Comments Name : PMS335OutputVO.java<br>
 * Author :Kevin<br>
 * Date :2016年06月06日 <br>
 * Version : 1.01 <br>
 * Editor : Kevin<br>
 * Editor Date : 2017年01月30日<br>
 */
public class PMS335OutputVO extends PagingOutputVO{
	private List resultList;  //分頁list
	private List list;   //查詢全部csv
	private List csvList;  //暫存csv
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
