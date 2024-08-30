package com.systex.jbranch.app.server.fps.pms429;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class PMS429InputVO extends PagingInputVO {

	private String region_center_id;
	private String branch_area_id;
	private String branch_nbr;
	private String ao_code;
	private String statement_send_type;
	private String loginRole;
	private String privilegeId;
	private Date sCreDate;
	private Date endDate;
	private String custId;
	private String recSeq;
	private Date recDate;
	
	private List<Map<String, Object>> list;
	private boolean printAllData;
	
	private List<Map<String, Object>> regionList;
	private List<Map<String, Object>> areaList;
	private List<Map<String, Object>> branchList;
	private List<Map<String, Object>> aoCodeList;
	
	private String person_role;
	private String memLoginFlag;
	private String UHRMMGR_FLAG;
	private String rotationBRMsg;
	private String process_status;
	private String cust_proce_status;
	private String PRJ_ID;

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

	public String getStatement_send_type() {
		return statement_send_type;
	}

	public void setStatement_send_type(String statement_send_type) {
		this.statement_send_type = statement_send_type;
	}

	public String getLoginRole() {
		return loginRole;
	}

	public void setLoginRole(String loginRole) {
		this.loginRole = loginRole;
	}

	public String getPrivilegeId() {
		return privilegeId;
	}

	public void setPrivilegeId(String privilegeId) {
		this.privilegeId = privilegeId;
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

	public String getCustId() {
		return custId;
	}

	public void setCustId(String custId) {
		this.custId = custId;
	}

	public String getRecSeq() {
		return recSeq;
	}

	public void setRecSeq(String recSeq) {
		this.recSeq = recSeq;
	}

	public Date getRecDate() {
		return recDate;
	}

	public void setRecDate(Date recDate) {
		this.recDate = recDate;
	}

	public List<Map<String, Object>> getList() {
		return list;
	}

	public void setList(List<Map<String, Object>> list) {
		this.list = list;
	}

	public boolean isPrintAllData() {
		return printAllData;
	}

	public void setPrintAllData(boolean printAllData) {
		this.printAllData = printAllData;
	}

	public List<Map<String, Object>> getRegionList() {
		return regionList;
	}

	public void setRegionList(List<Map<String, Object>> regionList) {
		this.regionList = regionList;
	}

	public List<Map<String, Object>> getAreaList() {
		return areaList;
	}

	public void setAreaList(List<Map<String, Object>> areaList) {
		this.areaList = areaList;
	}

	public List<Map<String, Object>> getBranchList() {
		return branchList;
	}

	public void setBranchList(List<Map<String, Object>> branchList) {
		this.branchList = branchList;
	}

	public List<Map<String, Object>> getAoCodeList() {
		return aoCodeList;
	}

	public void setAoCodeList(List<Map<String, Object>> aoCodeList) {
		this.aoCodeList = aoCodeList;
	}

	public String getPerson_role() {
		return person_role;
	}

	public void setPerson_role(String person_role) {
		this.person_role = person_role;
	}

	public String getMemLoginFlag() {
		return memLoginFlag;
	}

	public void setMemLoginFlag(String memLoginFlag) {
		this.memLoginFlag = memLoginFlag;
	}

	public String getUHRMMGR_FLAG() {
		return UHRMMGR_FLAG;
	}

	public void setUHRMMGR_FLAG(String uHRMMGR_FLAG) {
		UHRMMGR_FLAG = uHRMMGR_FLAG;
	}

	public String getRotationBRMsg() {
		return rotationBRMsg;
	}

	public void setRotationBRMsg(String rotationBRMsg) {
		this.rotationBRMsg = rotationBRMsg;
	}

	public String getProcess_status() {
		return process_status;
	}

	public void setProcess_status(String process_status) {
		this.process_status = process_status;
	}

	public String getCust_proce_status() {
		return cust_proce_status;
	}

	public void setCust_proce_status(String cust_proce_status) {
		this.cust_proce_status = cust_proce_status;
	}

	public String getPRJ_ID() {
		return PRJ_ID;
	}

	public void setPRJ_ID(String pRJ_ID) {
		PRJ_ID = pRJ_ID;
	}
	
}
