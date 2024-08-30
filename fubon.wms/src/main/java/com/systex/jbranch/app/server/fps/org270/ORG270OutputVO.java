package com.systex.jbranch.app.server.fps.org270;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

/*
 * #0662: WMS-CR-20210624-01_配合DiamondTeam專案調整系統模組功能_組織+客管
 */
public class ORG270OutputVO extends PagingOutputVO {
	
	private List<Map<String, Object>> resultList;

	public List<Map<String, Object>> getResultList() {
		return resultList;
	}

	public void setResultList(List<Map<String, Object>> resultList) {
		this.resultList = resultList;
	}

}
