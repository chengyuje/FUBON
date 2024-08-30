package com.systex.jbranch.app.server.fps.crm641;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.fubon.commons.esb.vo.cew012r.CEW012ROutputDetailsVO;
import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class CRM641OutputVO extends PagingOutputVO {
	private List<Map<String,Object>> resultList;
	private List<CEW012ROutputDetailsVO> resultList2;
	
	public List<Map<String,Object>> getResultList() {
		return resultList;
	}

	public void setResultList(List<Map<String,Object>> resultList) {
		this.resultList = resultList;
	}

	public List<CEW012ROutputDetailsVO> getResultList2() {
		return resultList2;
	}

	public void setResultList2(List<CEW012ROutputDetailsVO> resultList2) {
		this.resultList2 = resultList2;
	}

}
