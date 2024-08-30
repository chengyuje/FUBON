package com.systex.jbranch.app.server.fps.crm826;

import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class CRM826OutputVO extends PagingOutputVO{
	
	private List<CustAssetDCIVO>  resultList;   //客戶DCI資產資料
	private String errorCode;   //錯誤代碼
	private String errorMsg;    //錯誤訊息
	
	
	public List getResultList() {
		return resultList;
	}
	public void setResultList(List<CustAssetDCIVO> resultList) {
		this.resultList = resultList;
	}
	public String getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
	public String getErrorMsg() {
		return errorMsg;
	}
	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}
	
}
