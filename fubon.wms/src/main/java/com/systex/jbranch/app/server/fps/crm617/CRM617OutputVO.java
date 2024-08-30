package com.systex.jbranch.app.server.fps.crm617;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.fubon.commons.esb.vo.tp552697.TP552697OutputDetailsVO;
import com.systex.jbranch.fubon.commons.esb.vo.wms552697.WMS552697OutputDetailsVO;
import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class CRM617OutputVO extends PagingOutputVO {
	private List<Map<String,Object>> resultList;
	private List<TP552697OutputDetailsVO> resultList2;
	private List<WMS552697OutputDetailsVO> resultList3;
	
	
	public List<WMS552697OutputDetailsVO> getResultList3() {
		return resultList3;
	}

	public void setResultList3(List<WMS552697OutputDetailsVO> resultList3) {
		this.resultList3 = resultList3;
	}

	public  List<Map<String,Object>> getResultList() {
		return resultList;
	}

	public void setResultList( List<Map<String,Object>> resultList) {
		this.resultList = resultList;
	}

	public List<TP552697OutputDetailsVO> getResultList2() {
		return resultList2;
	}

	public void setResultList2(List<TP552697OutputDetailsVO> resultList2) {
		this.resultList2 = resultList2;
	}

}
