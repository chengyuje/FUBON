package com.systex.jbranch.app.server.fps.ins810;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class INS810OutputVO extends PagingOutputVO{
	
	private List<Map<String, Object>> chkList;
	private List<Map<String, Object>> COMList;
	private List<Map<String , Object>> GenealogyList;
	private int age;
	private String saveInsCustMastMsg;
	private String InsSeq;
	private String getReportMsg;

	public List<Map<String, Object>> getCOMList() {
		return COMList;
	}

	public void setCOMList(List<Map<String, Object>> cOMList) {
		COMList = cOMList;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public String getSaveInsCustMastMsg() {
		return saveInsCustMastMsg;
	}

	public void setSaveInsCustMastMsg(String saveInsCustMastMsg) {
		this.saveInsCustMastMsg = saveInsCustMastMsg;
	}

	public String getInsSeq() {
		return InsSeq;
	}

	public void setInsSeq(String insSeq) {
		InsSeq = insSeq;
	}

	public List<Map<String , Object>> getGenealogyList() {
		return GenealogyList;
	}

	public void setGenealogyList(List<Map<String , Object>> genealogyList) {
		GenealogyList = genealogyList;
	}

	public List<Map<String, Object>> getChkList() {
		return chkList;
	}

	public void setChkList(List<Map<String, Object>> chkList) {
		this.chkList = chkList;
	}

	public String getGetReportMsg() {
		return getReportMsg;
	}

	public void setGetReportMsg(String getReportMsg) {
		this.getReportMsg = getReportMsg;
	}
	
	

}
