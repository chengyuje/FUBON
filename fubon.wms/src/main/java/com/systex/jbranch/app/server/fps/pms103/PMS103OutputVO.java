package com.systex.jbranch.app.server.fps.pms103;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class PMS103OutputVO extends PagingOutputVO {
	
	private List resultList;
	private List resultList2;
	private List resultTotal;
	private List<Map<String,Object>> ymList;
	private List<Map<String,Object>> tarList;
	private String currentYM;
	
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

	public List<Map<String, Object>> getYmList() {
		return ymList;
	}

	public void setYmList(List<Map<String, Object>> ymList) {
		this.ymList = ymList;
	}

	public String getCurrentYM() {
		return currentYM;
	}

	public void setCurrentYM(String currentYM) {
		this.currentYM = currentYM;
	}

	public List<Map<String, Object>> getTarList() {
		return tarList;
	}

	public void setTarList(List<Map<String, Object>> tarList) {
		this.tarList = tarList;
	}

	public List getResultTotal() {
		return resultTotal;
	}

	public void setResultTotal(List resultTotal) {
		this.resultTotal = resultTotal;
	}
	
	
}
