package com.systex.jbranch.app.server.fps.mpl110;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class MPL110OutputVO extends PagingOutputVO {
	
	private List<Map<String,Object>> resultList;
	private String isHEADMGR;	//是否有總行權限Y/N
	private String isARMGR;		//是否有業務處長權限Y/N
	private String isMBRMGR;	//是否有營運督導權限Y/N

	public List<Map<String, Object>> getResultList() {
		return resultList;
	}

	public void setResultList(List<Map<String, Object>> resultList) {
		this.resultList = resultList;
	}

	public String getIsHEADMGR() {
		return isHEADMGR;
	}

	public void setIsHEADMGR(String isHEADMGR) {
		this.isHEADMGR = isHEADMGR;
	}

	public String getIsARMGR() {
		return isARMGR;
	}

	public void setIsARMGR(String isARMGR) {
		this.isARMGR = isARMGR;
	}

	public String getIsMBRMGR() {
		return isMBRMGR;
	}

	public void setIsMBRMGR(String isMBRMGR) {
		this.isMBRMGR = isMBRMGR;
	}
	
}
