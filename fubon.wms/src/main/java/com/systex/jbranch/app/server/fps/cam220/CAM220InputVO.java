package com.systex.jbranch.app.server.fps.cam220;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class CAM220InputVO extends PagingInputVO{
	
	private String branchID;
	private String channel;
	private String[] campIDList;
	private String[] stepIDList;
	
	private String[] custIDList;
	private String[] empIDList;
	private String[] aoCodeList;
	private String assCustID;
	private String assCustName;
	private String assCampaignName;
	private String vipDegree;
	private String conDegree;
	
	private String empID;
	private String yesORno;
	private String custID;
	private String custName; 
	private String campName;
	private String leadStatus;
	private Date sDate;
	private Date eDate;
	private String haveAo_code;
	
	private String action;
	
	private List<Map<String, Object>> history_list;
	

	public String getAssCampaignName() {
		return assCampaignName;
	}

	public void setAssCampaignName(String assCampaignName) {
		this.assCampaignName = assCampaignName;
	}

	public String getAssCustID() {
		return assCustID;
	}

	public void setAssCustID(String assCustID) {
		this.assCustID = assCustID;
	}

	public String getAssCustName() {
		return assCustName;
	}

	public void setAssCustName(String assCustName) {
		this.assCustName = assCustName;
	}
	
	public String getVipDegree() {
		return vipDegree;
	}

	public void setVipDegree(String vipDegree) {
		this.vipDegree = vipDegree;
	}

	public String getConDegree() {
		return conDegree;
	}

	public void setConDegree(String conDegree) {
		this.conDegree = conDegree;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String[] getCustIDList() {
		return custIDList;
	}

	public void setCustIDList(String[] custIDList) {
		this.custIDList = custIDList;
	}

	public String[] getEmpIDList() {
		return empIDList;
	}

	public void setEmpIDList(String[] empIDList) {
		this.empIDList = empIDList;
	}

	public String[] getAoCodeList() {
		return aoCodeList;
	}

	public void setAoCodeList(String[] aoCodeList) {
		this.aoCodeList = aoCodeList;
	}

	public String getEmpID() {
		return empID;
	}

	public void setEmpID(String empID) {
		this.empID = empID;
	}

	public String getYesORno() {
		return yesORno;
	}

	public void setYesORno(String yesORno) {
		this.yesORno = yesORno;
	}

	public String getCustID() {
		return custID;
	}

	public void setCustID(String custID) {
		this.custID = custID;
	}

	public String getCustName() {
		return custName;
	}

	public void setCustName(String custName) {
		this.custName = custName;
	}

	public String getCampName() {
		return campName;
	}

	public void setCampName(String campName) {
		this.campName = campName;
	}

	public String getLeadStatus() {
		return leadStatus;
	}

	public void setLeadStatus(String leadStatus) {
		this.leadStatus = leadStatus;
	}

	public Date getsDate() {
		return sDate;
	}

	public void setsDate(Date sDate) {
		this.sDate = sDate;
	}

	public Date geteDate() {
		return eDate;
	}

	public void seteDate(Date eDate) {
		this.eDate = eDate;
	}

	public String getHaveAo_code() {
		return haveAo_code;
	}

	public void setHaveAo_code(String haveAo_code) {
		this.haveAo_code = haveAo_code;
	}

	public String[] getCampIDList() {
		return campIDList;
	}

	public void setCampIDList(String[] campIDList) {
		this.campIDList = campIDList;
	}

	public String[] getStepIDList() {
		return stepIDList;
	}

	public void setStepIDList(String[] stepIDList) {
		this.stepIDList = stepIDList;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public String getBranchID() {
		return branchID;
	}

	public void setBranchID(String branchID) {
		this.branchID = branchID;
	}

	public List<Map<String, Object>> getHistory_list() {
		return history_list;
	}

	public void setHistory_list(List<Map<String, Object>> history_list) {
		this.history_list = history_list;
	}
	
}