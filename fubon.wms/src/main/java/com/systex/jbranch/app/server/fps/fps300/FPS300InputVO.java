package com.systex.jbranch.app.server.fps.fps300;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;
import java.util.Map;
import java.sql.Timestamp;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;
/**
 * 
 * 我的客戶搜尋(FPS300InputVO)
 * <p>
 * WMSA-TDS-FPS300-理財規劃-框架查詢.doc
 * <p>
 * Notics: 修改歷程及說明<br>
 * 
 * </pre>
 * 
 * @author KevinHsu
 */

public class FPS300InputVO extends PagingInputVO{	
	private String custId;	//客戶ID	
	
	/*** 客戶快速查詢 ***/
	public String custName;
	public String vipCodeI;
	public String is_AO;
	public String loginID;
	public String rmcode;
	public boolean dialogFlag;
	public List<Map<String, Object>> RMCodeList;
	public List<Map<String, Object>> AvailBranchList;
	public String businsType;
	public String accNo;
	private String queryType;
	private String planId;
	private boolean commrYn;
	private String riskType;

	public String getPlanId() {
		return planId;
	}
	public void setPlanId(String planId) {
		this.planId = planId;
	}
	public boolean isCommrYn() {
		return commrYn;
	}
	public void setCommrYn(boolean commrYn) {
		this.commrYn = commrYn;
	}	
	public String getRiskType() {
		return riskType;
	}
	public void setRiskType(String riskType) {
		this.riskType = riskType;
	}
	public String getCustId() {
		return custId;
	}
	public void setCustId(String custId) {
		this.custId = custId;
	}
	public String getCustName() {
		return custName;
	}
	public void setCustName(String custName) {
		this.custName = custName;
	}
	public String getVipCodeI() {
		return vipCodeI;
	}
	public void setVipCodeI(String vipCodeI) {
		this.vipCodeI = vipCodeI;
	}
	public String getIs_AO() {
		return is_AO;
	}
	public void setIs_AO(String is_AO) {
		this.is_AO = is_AO;
	}
	public String getLoginID() {
		return loginID;
	}
	public void setLoginID(String loginID) {
		this.loginID = loginID;
	}
	public String getRmcode() {
		return rmcode;
	}
	public void setRmcode(String rmcode) {
		this.rmcode = rmcode;
	}
	public boolean isDialogFlag() {
		return dialogFlag;
	}
	public void setDialogFlag(boolean dialogFlag) {
		this.dialogFlag = dialogFlag;
	}
	public List<Map<String, Object>> getRMCodeList() {
		return RMCodeList;
	}
	public void setRMCodeList(List<Map<String, Object>> rMCodeList) {
		RMCodeList = rMCodeList;
	}
	public List<Map<String, Object>> getAvailBranchList() {
		return AvailBranchList;
	}
	public void setAvailBranchList(List<Map<String, Object>> availBranchList) {
		AvailBranchList = availBranchList;
	}
	public String getBusinsType() {
		return businsType;
	}
	public void setBusinsType(String businsType) {
		this.businsType = businsType;
	}
	public String getAccNo() {
		return accNo;
	}
	public void setAccNo(String accNo) {
		this.accNo = accNo;
	}
	public String getQueryType() {
		return queryType;
	}
	public void setQueryType(String queryType) {
		this.queryType = queryType;
	}
	
}
