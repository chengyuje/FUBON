package com.systex.jbranch.app.server.fps.pms329;

import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;
/**
 * 
 * Copy Right Information :<br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.7.0 <br>
 * Description : AUM OutputVO<br>
 * Comments Name : PMS328OutputVO.java<br>
 * Author :Kevin<br>
 * Date :2016年05月31日 <br>
 * Version : 1.01 <br>
 * Editor : Kevin<br>
 * Editor Date : 2017年01月31日<br>
 */
public class PMS329OutputVO extends PagingOutputVO {
	private List resultList; 	 //分頁查詢結果
	private List DATA;			//圖專用
	private List csvList;    //CSV LIST
	private List list;				//CSV 暫存LIST
	private List totalList;
	private List resultList3; 	 //總計
	private List resultList4; 	 //區域總計
	private List resultList5; 	 //分行總計
	
	


	
	public List getResultList4() {
		return resultList4;
	}
	public void setResultList4(List resultList4) {
		this.resultList4 = resultList4;
	}
	public List getResultList5() {
		return resultList5;
	}
	public void setResultList5(List resultList5) {
		this.resultList5 = resultList5;
	}
	public List getResultList3() {
		return resultList3;
	}
	public void setResultList3(List resultList3) {
		this.resultList3 = resultList3;
	}
	private int ya;
	
	
	public int getYa() {
		return ya;
	}
	public void setYa(int ya) {
		this.ya = ya;
	}
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
	public List getDATA() {
		return DATA;
	}
	public List getCsvList() {
		return csvList;
	}
	public void setCsvList(List csvList) {
		this.csvList = csvList;
	}
	public void setDATA(List dATA) {
		DATA = dATA;
	}
}
