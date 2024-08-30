package com.systex.jbranch.app.server.fps.prd171;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class PRD171OutputVO extends PagingOutputVO {
	private List<Map<String, Object>> INS_ANCDOCList;
	private List<Map<String, Object>> DILOGList;
	private List<Map<String, Object>> tempList;
	private List<Map<String, Object>> INSPRD_NAMEList;
	private List<Map<String, Object>> DOCCHKList;
	private List<Map<String, Object>> chk_list;
	private List resultList;
	private List errorList;

	
	public List<Map<String, Object>> getChk_list() {
		return chk_list;
	}
	public void setChk_list(List<Map<String, Object>> chk_list) {
		this.chk_list = chk_list;
	}
	public List<Map<String, Object>> getDOCCHKList() {
		return DOCCHKList;
	}
	public void setDOCCHKList(List<Map<String, Object>> dOCCHKList) {
		DOCCHKList = dOCCHKList;
	}
	public List<Map<String, Object>> getINSPRD_NAMEList() {
		return INSPRD_NAMEList;
	}
	public void setINSPRD_NAMEList(List<Map<String, Object>> iNSPRD_NAMEList) {
		INSPRD_NAMEList = iNSPRD_NAMEList;
	}
	public List<Map<String, Object>> getTempList() {
		return tempList;
	}
	public void setTempList(List<Map<String, Object>> tempList) {
		this.tempList = tempList;
	}
	public List<Map<String, Object>> getINS_ANCDOCList() {
		return INS_ANCDOCList;
	}
	public void setINS_ANCDOCList(List<Map<String, Object>> iNS_ANCDOCList) {
		INS_ANCDOCList = iNS_ANCDOCList;
	}
	public List<Map<String, Object>> getDILOGList() {
		return DILOGList;
	}
	public void setDILOGList(List<Map<String, Object>> dILOGList) {
		DILOGList = dILOGList;
	}
	public List getResultList() {
		return resultList;
	}
	public void setResultList(List resultList) {
		this.resultList = resultList;
	}
	public List getErrorList() {
		return errorList;
	}
	public void setErrorList(List errorList) {
		this.errorList = errorList;
	}
}
