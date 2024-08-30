package com.systex.jbranch.app.server.fps.pms109;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;
/**
 * 
 * Copy Right Information : <br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.7.0 <br>
 * Description : <br>
 * Comments Name : PMS109OutputVO.java<br>
 * Author :Kevin<br>
 * Date :2016年06月13日 <br>
 * Version : 1.01 <br>
 * Editor : Kevin<br>
 * Editor Date : 2017年02月01日<br>
 */
public class PMS109OutputVO extends PagingOutputVO {
	private List resultList;
	private List resultList2;
	private List empList;
	private List AO_LIST;
	private List<Map<String,Object>> ymList;
	
	public List getResultList2() {
		return resultList2;
	}

	public void setResultList2(List resultList2) {
		this.resultList2 = resultList2;
	}

	public List getResultList() {
		return resultList;
	}

	public void setResultList(List resultList) {
		this.resultList = resultList;
	}

	public List getEmpList() {
		return empList;
	}

	public void setEmpList(List empList) {
		this.empList = empList;
	}

	public List<Map<String, Object>> getYmList() {
		return ymList;
	}

	public void setYmList(List<Map<String, Object>> ymList) {
		this.ymList = ymList;
	}

	public List getAO_LIST() {
		return AO_LIST;
	}

	public void setAO_LIST(List aO_LIST) {
		AO_LIST = aO_LIST;
	}
	
}
