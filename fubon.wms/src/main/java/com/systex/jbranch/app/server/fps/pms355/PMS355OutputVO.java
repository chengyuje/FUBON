package com.systex.jbranch.app.server.fps.pms355;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class PMS355OutputVO extends PagingOutputVO {
	
	private List<Map<String, Object>> resultList;
	private List<Map<String, Object>> custList;
	private int flag; //操作結果判斷
	
	public int getFlag() {
		return flag;
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}

	public List<Map<String, Object>> getCustList() {
		return custList;
	}

	public void setCustList(List<Map<String, Object>> custList) {
		this.custList = custList;
	}

	public List<Map<String, Object>> getResultList() {
		return resultList;
	}

	public void setResultList(List<Map<String, Object>> resultList) {
		this.resultList = resultList;
	}

}
