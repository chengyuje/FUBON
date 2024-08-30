package com.systex.jbranch.app.server.fps.iot310;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class IOT310OutputVO extends PagingOutputVO{
	
	private List<Map<String, Object>> DEPID;
	private List<Map<String, Object>> EMP_NAME;
	private List<Map<String, Object>> CUST_NAME;
	private List<Map<String, Object>> INSURED_NAME;
	private List<Map<String, Object>> COM_ADDRESS;
	private List<Map<String, Object>> INS_INFORMATION;
	private List<Map<String, Object>> REPRESETList;
	private List<Map<String, Object>> InitList;
	private List<Map<String, Object>> InitList1;
	public List<Map<String, Object>> getREPRESETList() {
		return REPRESETList;
	}

	public void setREPRESETList(List<Map<String, Object>> rEPRESETList) {
		REPRESETList = rEPRESETList;
	}

	public List<Map<String, Object>> getINSURED_NAME() {
		return INSURED_NAME;
	}

	public void setINSURED_NAME(List<Map<String, Object>> iNSURED_NAME) {
		INSURED_NAME = iNSURED_NAME;
	}

	public List<Map<String, Object>> getINS_INFORMATION() {
		return INS_INFORMATION;
	}

	public void setINS_INFORMATION(List<Map<String, Object>> iNS_INFORMATION) {
		INS_INFORMATION = iNS_INFORMATION;
	}

	public List<Map<String, Object>> getCUST_NAME() {
		return CUST_NAME;
	}

	public void setCUST_NAME(List<Map<String, Object>> cUST_NAME) {
		CUST_NAME = cUST_NAME;
	}

	public List<Map<String, Object>> getCOM_ADDRESS() {
		return COM_ADDRESS;
	}

	public void setCOM_ADDRESS(List<Map<String, Object>> cOM_ADDRESS) {
		COM_ADDRESS = cOM_ADDRESS;
	}

	public List<Map<String, Object>> getEMP_NAME() {
		return EMP_NAME;
	}

	public void setEMP_NAME(List<Map<String, Object>> eMP_NAME) {
		EMP_NAME = eMP_NAME;
	}

	public List<Map<String, Object>> getDEPID() {
		return DEPID;
	}

	public void setDEPID(List<Map<String, Object>> dEPID) {
		DEPID = dEPID;
	}

	public List<Map<String, Object>> getInitList() {
		return InitList;
	}

	public void setInitList(List<Map<String, Object>> initList) {
		InitList = initList;
	}

	public List<Map<String, Object>> getInitList1() {
		return InitList1;
	}

	public void setInitList1(List<Map<String, Object>> initList1) {
		InitList1 = initList1;
	}
	
	

}
