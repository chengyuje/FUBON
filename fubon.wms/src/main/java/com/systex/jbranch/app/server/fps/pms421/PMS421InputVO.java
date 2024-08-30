package com.systex.jbranch.app.server.fps.pms421;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class PMS421InputVO extends PagingInputVO {

	private String region_center_id;
	private String branch_area_id;
	private String branch_nbr;
	private String ao_code;
	private String from181;
	private String loginRole;
	private Date sCreDate;
	private Date endDate;
	private String ebill_comfirm_sou;
	private List<Map<String, Object>> list;

	private String PABTH_NAME;
	private String DATA_DATE;
	private String MONTH;
	private Date EXE_TIME;
	private String EXE_DATE;
	private String fileName;

	private String person_role;
	private String memLoginFlag;
	private String selectRoleID;
	
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
	
	public String getSelectRoleID() {
		return selectRoleID;
	}

	public void setSelectRoleID(String selectRoleID) {
		this.selectRoleID = selectRoleID;
	}

	public String getEXE_DATE() {
		return EXE_DATE;
	}

	public void setEXE_DATE(String eXE_DATE) {
		EXE_DATE = eXE_DATE;
	}

	public Date getEXE_TIME() {
		return EXE_TIME;
	}

	public void setEXE_TIME(Date eXE_TIME) {
		EXE_TIME = eXE_TIME;
	}

	public String getPABTH_NAME() {
		return PABTH_NAME;
	}

	public void setPABTH_NAME(String pABTH_NAME) {
		PABTH_NAME = pABTH_NAME;
	}

	public String getDATA_DATE() {
		return DATA_DATE;
	}

	public void setDATA_DATE(String dATA_DATE) {
		DATA_DATE = dATA_DATE;
	}

	public String getMONTH() {
		return MONTH;
	}

	public void setMONTH(String mONTH) {
		MONTH = mONTH;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getLoginRole() {
		return loginRole;
	}

	public void setLoginRole(String loginRole) {
		this.loginRole = loginRole;
	}

	public String getEbill_Comfirm_Sou() {
		return ebill_comfirm_sou;
	}

	public void setEbill_Comfirm_Sou(String ebill_comfirm_sou) {
		this.ebill_comfirm_sou = ebill_comfirm_sou;
	}

	public String getFrom181() {
		return from181;
	}

	public void setFrom181(String from181) {
		this.from181 = from181;
	}

	public List<Map<String, Object>> getList() {
		return list;
	}

	public void setList(List<Map<String, Object>> list) {
		this.list = list;
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

}
