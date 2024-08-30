package com.systex.jbranch.app.server.fps.pms328;

import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;
/**
 * 
 * Copy Right Information : <br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.7.0 <br>
 * Description : 客戶數OutputVO<br>
 * Comments Name : PMS328OutputVO.java<br>
 * Author :Kevin<br>
 * Date :2016年05月31日 <br>
 * Version : 1.01 <br>
 * Editor : Kevin<br>
 * Editor Date : 2017年01月31日<br>
 */
public class PMS328OutputVO extends PagingOutputVO {
	private List resultList;  //主分頁查詢list
	private List DATA;
	private List csvList;	//csv list
	private List list;		//暫存匯出csv list
	private List totalList;
	private String s_time;
	
	public List getTotalList() {
		return totalList;
	}
	public void setTotalList(List totalList) {
		this.totalList = totalList;
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
	public void setResultList(List resultList) {
		this.resultList = resultList;
	}
	public List getCsvList() {
		return csvList;
	}
	public void setCsvList(List csvList) {
		this.csvList = csvList;
	}
	public List getDATA() {
		return DATA;
	}
	public void setDATA(List dATA) {
		DATA = dATA;
	}
	public String getS_time() {
		return s_time;
	}
	public void setS_time(String s_time) {
		this.s_time = s_time;
	}
	
}
