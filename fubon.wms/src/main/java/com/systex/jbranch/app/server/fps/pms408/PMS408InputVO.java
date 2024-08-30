package com.systex.jbranch.app.server.fps.pms408;

import java.sql.Date;
import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class PMS408InputVO extends PagingInputVO {
	
	private String dataMonth;
	private String region_center_id;
	private String branch_area_id;
	private String branch_nbr;
	private String ao_code;
	private Date sCreDate;
	private Date endDate;
	private String roleID;
	private String needConfirmYN;
	private String rptVersion;

	private List<Map<String, Object>> list;
	private List<Map<String, Object>> list2;
	private List<Map<String, Object>> dataList; //更新資料
	private List<Map<String, Object>> compareDtlList;
	private String selectRoleID;
	
	public String getSelectRoleID() {
		return selectRoleID;
	}

	public void setSelectRoleID(String selectRoleID) {
		this.selectRoleID = selectRoleID;
	}
	public List<Map<String, Object>> getCompareDtlList() {
		return compareDtlList;
	}

	public void setCompareDtlList(List<Map<String, Object>> compareDtlList) {
		this.compareDtlList = compareDtlList;
	}

	private String memLoginFlag;
	
	private String noteStatus;
	private String kycType;
	
	private String compareDataDate;
	private String compareCustID;
	private String compareDataType;
	
	private String uhrmRC;
	private String uhrmOP;
	
	public String getUhrmRC() {
		return uhrmRC;
	}

	public void setUhrmRC(String uhrmRC) {
		this.uhrmRC = uhrmRC;
	}

	public String getUhrmOP() {
		return uhrmOP;
	}

	public void setUhrmOP(String uhrmOP) {
		this.uhrmOP = uhrmOP;
	}
	
	public String getCompareDataType() {
		return compareDataType;
	}

	public void setCompareDataType(String compareDataType) {
		this.compareDataType = compareDataType;
	}

	public String getRptVersion() {
		return rptVersion;
	}

	public void setRptVersion(String rptVersion) {
		this.rptVersion = rptVersion;
	}

	public String getCompareDataDate() {
		return compareDataDate;
	}

	public void setCompareDataDate(String compareDataDate) {
		this.compareDataDate = compareDataDate;
	}

	public String getCompareCustID() {
		return compareCustID;
	}

	public void setCompareCustID(String compareCustID) {
		this.compareCustID = compareCustID;
	}

	public String getKycType() {
		return kycType;
	}

	public void setKycType(String kycType) {
		this.kycType = kycType;
	}

	public List<Map<String, Object>> getDataList() {
		return dataList;
	}

	public void setDataList(List<Map<String, Object>> dataList) {
		this.dataList = dataList;
	}

	public String getNoteStatus() {
		return noteStatus;
	}

	public void setNoteStatus(String noteStatus) {
		this.noteStatus = noteStatus;
	}

	public String getMemLoginFlag() {
		return memLoginFlag;
	}

	public void setMemLoginFlag(String memLoginFlag) {
		this.memLoginFlag = memLoginFlag;
	}

	public Date getsCreDate() {
		return sCreDate;
	}

	public void setsCreDate(Date sCreDate) {
		this.sCreDate = sCreDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getDataMonth() {
		return dataMonth;
	}

	public void setDataMonth(String dataMonth) {
		this.dataMonth = dataMonth;
	}

	public List<Map<String, Object>> getDatalist() {
		return dataList;
	}

	public void setDatalist(List<Map<String, Object>> dataList) {
		this.dataList = dataList;
	}

	public String getRegion_center_id() {
		return region_center_id;
	}

	public void setRegion_center_id(String region_center_id) {
		this.region_center_id = region_center_id;
	}

	public String getBranch_area_id() {
		return branch_area_id;
	}

	public void setBranch_area_id(String branch_area_id) {
		this.branch_area_id = branch_area_id;
	}

	public String getBranch_nbr() {
		return branch_nbr;
	}

	public void setBranch_nbr(String branch_nbr) {
		this.branch_nbr = branch_nbr;
	}

	public String getAo_code() {
		return ao_code;
	}

	public void setAo_code(String ao_code) {
		this.ao_code = ao_code;
	}

	public String getNeedConfirmYN() {
		return needConfirmYN;
	}

	public void setNeedConfirmYN(String needConfirmYN) {
		this.needConfirmYN = needConfirmYN;
	}

	public List<Map<String, Object>> getList() {
		return list;
	}

	public void setList(List<Map<String, Object>> list) {
		this.list = list;
	}

	public List<Map<String, Object>> getList2() {
		return list2;
	}

	public void setList2(List<Map<String, Object>> list2) {
		this.list2 = list2;
	}

	public String getRoleID() {
		return roleID;
	}

	public void setRoleID(String roleID) {
		this.roleID = roleID;
	}

}
