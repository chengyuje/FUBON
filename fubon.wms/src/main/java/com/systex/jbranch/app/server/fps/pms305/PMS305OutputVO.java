package com.systex.jbranch.app.server.fps.pms305;

import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;
/**
 * 
 * Copy Right Information : <br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.7.0 <br>
 * Description :險種別統計 OutputVO<br>
 * Comments Name : PMS305OutputVO.java<br>
 * Author :Kevin<br>
 * Date :2016年07月28日 <br>
 * Version : 1.01 <br>
 * Editor : Kevin<br>
 * Editor Date : 2017年01月31日<br>
 */
public class PMS305OutputVO extends PagingOutputVO{
	private List resultList;  //頁面LIST
	private List totalList;
	private List list;
	private List csvList;   //csv
	private List discountList;
	
	public List getDiscountList() {
		return discountList;
	}
	public void setDiscountList(List discountList) {
		this.discountList = discountList;
	}
	public List getList() {
		return list;
	}
	public List getCsvList() {
		return csvList;
	}
	public void setList(List list) {
		this.list = list;
	}
	public void setCsvList(List csvList) {
		this.csvList = csvList;
	}
	public List getResultList() {
		return resultList;
	}
	public void setResultList(List resultList) {
		this.resultList = resultList;
	}
	public List getTotalList() {
		return totalList;
	}
	public void setTotalList(List totalList) {
		this.totalList = totalList;
	}
}
