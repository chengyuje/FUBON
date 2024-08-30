package com.systex.jbranch.app.server.fps.pms208;

import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;
/**
 * 
 * Copy Right Information : <br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.7.0 <br>
 * Description : 落點排名(含晉級目標達成率)<br>
 * Comments Name : PMS208OutputVO.java<br>
 * Author :Kevin<br>
 * Date :2016年06月27日 <br>
 * Version : 1.01 <br>
 * Editor : Kevin<br>
 * Editor Date : 2017年02月01日<br>
 */
public class PMS208OutputVO extends PagingOutputVO {
	private List resultList;
	private String type; //YTD/MTD
	private List list;
	private List csvList;
	
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public List getList() {	
		return list;
	}
	public List getCsvList() {
		return csvList;
	}
	public void setCsvList(List csvList) {
		this.csvList = csvList;
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
}
