package com.systex.jbranch.app.server.fps.cus140;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

/**
 * Created by SebastianWu on 2016/7/11.
 */
public class CUS140InputVO extends PagingInputVO {
	private String ivgPlanName; //回報計畫名稱
	private String ivgType; //回報類型
	private String ivgPlanType; //計畫狀態
	private String resFlag; //是否已回報
	private Date ivgStartDate; //開始日期
	private Date ivgEndDate; //截止日期
	private BigDecimal ivgResultSeq; //回報結果序號
	private BigDecimal ivgPlanSeq; //TBSA_IVG_PLAN_MAIN
	private String empID; //當前使用者ID
	private String regionID;
	private String areaID;
	private String branchID; //當前使用者分行代碼
	private String roleID;
	private List inputDynamicField; //動態欄位List

	private String memLoginFlag;

	public String getMemLoginFlag() {
		return memLoginFlag;
	}

	public void setMemLoginFlag(String memLoginFlag) {
		this.memLoginFlag = memLoginFlag;
	}

	public String getIvgPlanName() {
		return ivgPlanName;
	}

	public void setIvgPlanName(String ivgPlanName) {
		this.ivgPlanName = ivgPlanName;
	}

	public String getIvgType() {
		return ivgType;
	}

	public void setIvgType(String ivgType) {
		this.ivgType = ivgType;
	}

	public String getIvgPlanType() {
		return ivgPlanType;
	}

	public void setIvgPlanType(String ivgPlanType) {
		this.ivgPlanType = ivgPlanType;
	}

	public String getResFlag() {
		return resFlag;
	}

	public void setResFlag(String resFlag) {
		this.resFlag = resFlag;
	}

	public Date getIvgStartDate() {
		return ivgStartDate;
	}

	public void setIvgStartDate(Date ivgStartDate) {
		this.ivgStartDate = ivgStartDate;
	}

	public Date getIvgEndDate() {
		return ivgEndDate;
	}

	public void setIvgEndDate(Date ivgEndDate) {
		this.ivgEndDate = ivgEndDate;
	}

	public BigDecimal getIvgResultSeq() {
		return ivgResultSeq;
	}

	public void setIvgResultSeq(BigDecimal ivgResultSeq) {
		this.ivgResultSeq = ivgResultSeq;
	}

	public BigDecimal getIvgPlanSeq() {
		return ivgPlanSeq;
	}

	public void setIvgPlanSeq(BigDecimal ivgPlanSeq) {
		this.ivgPlanSeq = ivgPlanSeq;
	}

	public String getEmpID() {
		return empID;
	}

	public void setEmpID(String empID) {
		this.empID = empID;
	}

	public String getRegionID() {
		return regionID;
	}

	public void setRegionID(String regionID) {
		this.regionID = regionID;
	}

	public String getAreaID() {
		return areaID;
	}

	public void setAreaID(String areaID) {
		this.areaID = areaID;
	}

	public String getBranchID() {
		return branchID;
	}

	public void setBranchID(String branchID) {
		this.branchID = branchID;
	}

	public String getRoleID() {
		return roleID;
	}

	public void setRoleID(String roleID) {
		this.roleID = roleID;
	}

	public List getInputDynamicField() {
		return inputDynamicField;
	}

	public void setInputDynamicField(List inputDynamicField) {
		this.inputDynamicField = inputDynamicField;
	}
}