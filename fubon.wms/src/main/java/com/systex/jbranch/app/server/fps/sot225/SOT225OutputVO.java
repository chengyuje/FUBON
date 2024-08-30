package com.systex.jbranch.app.server.fps.sot225;

import java.util.List;
import com.systex.jbranch.app.server.fps.sot705.CustAssetETFMonVO;
import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class SOT225OutputVO extends PagingOutputVO {
	
	private List<CustAssetETFMonVO> resultList;	
	private String errorMsg;
	
	public List<CustAssetETFMonVO> getResultList() {
		return resultList;
	}
	public void setResultList(List<CustAssetETFMonVO> resultList) {
		this.resultList = resultList;
	}
	public String getErrorMsg() {
		return errorMsg;
	}
	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}
	
	

}
