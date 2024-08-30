package com.systex.jbranch.app.server.fps.pms350;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class PMS350InputVO extends PagingInputVO {
	private BigDecimal seq;
	private Date sCreDate;
	private Date eCreDate;
	private String valid;
	private String rptName;
	private String rptExplain;
	private String marqueeFlag;
	private String marqueeTxt;
	private String fileName;
	private String realFileName;
	private String rolesName;
	private List<String> roles;
	private String uploadFlag;
	private List<String> UPLOAD_ROLES;
	private String RPT_DEPT;
	private String RPT_DEPT_1;
	private String RPT_DEPT_2;
	private String RPT_TYPE;
	private String  report_description;
	private String  ORG_TYPE;
	private String  DEPT_NAME;
	private String DEPT_ID;
	private String PARENT_DEPT_ID;
	private List<String> USER_ROLES;
	private String USER_TYPE;
	private String ALL_DEPT;
	private String updateUpdater;
	private Boolean EXPORT_YN ;
	
	//add by Carley：for chart
	private String[] seq_list;
	private String[] col_seq_list;
	private String col_seq;
	private Date sDate;
	private Date eDate;
	private String region_center_id;
	private String branch_area_id;
	private String branch_nbr;
	private String fc;
	private String char_type;	
	private String setupCategory;		
	private Boolean pms900;		
	
	public String getUpdateUpdater() {
		return updateUpdater;
	}

	public void setUpdateUpdater(String updateUpdater) {
		this.updateUpdater = updateUpdater;
	}

	public String getUSER_TYPE() {
		return USER_TYPE;
	}

	public void setUSER_TYPE(String uSER_TYPE) {
		USER_TYPE = uSER_TYPE;
	}

	// 2017/7/5
	private String wtfflag;
	private List<String> wtfuser;
	private List<String> wtfupload;
	
	
	public String getRPT_DEPT_1() {
		return RPT_DEPT_1;
	}

	public void setRPT_DEPT_1(String rPT_DEPT_1) {
		RPT_DEPT_1 = rPT_DEPT_1;
	}

	public String getRPT_DEPT_2() {
		return RPT_DEPT_2;
	}

	public void setRPT_DEPT_2(String rPT_DEPT_2) {
		RPT_DEPT_2 = rPT_DEPT_2;
	}


	public List<String> getUSER_ROLES() {
		return USER_ROLES;
	}

	public void setUSER_ROLES(List<String> uSER_ROLES) {
		this.USER_ROLES = uSER_ROLES;
	}

	public String getDEPT_ID() {
		return DEPT_ID;
	}

	public void setDEPT_ID(String dEPT_ID) {
		DEPT_ID = dEPT_ID;
	}

	public String getPARENT_DEPT_ID() {
		return PARENT_DEPT_ID;
	}

	public void setPARENT_DEPT_ID(String pARENT_DEPT_ID) {
		PARENT_DEPT_ID = pARENT_DEPT_ID;
	}

	public String getRolesName() {
		return rolesName;
	}

	public void setRolesName(String rolesName) {
		this.rolesName = rolesName;
	}

	public String getReport_description() {
		return report_description;
	}

	public void setReport_description(String report_description) {
		this.report_description = report_description;
	}

	public List<String> getUPLOAD_ROLES() {
		return UPLOAD_ROLES;
	}

	public void setUPLOAD_ROLES(List<String> UPLOAD_ROLES) {
		this.UPLOAD_ROLES = UPLOAD_ROLES;
	}

	public String getRPT_DEPT() {
		return RPT_DEPT;
	}

	public void setRPT_DEPT(String RPT_DEPT) {
		this.RPT_DEPT = RPT_DEPT;
	}

	public String getRPT_TYPE() {
		return RPT_TYPE;
	}

	public void setRPT_TYPE(String RPT_TYPE) {
		this.RPT_TYPE = RPT_TYPE;
	}

	public BigDecimal getSeq() {
		return seq;
	}

	public void setSeq(BigDecimal seq) {
		this.seq = seq;
	}

	public Date getsCreDate() {
		return sCreDate;
	}

	public void setsCreDate(Date sCreDate) {
		this.sCreDate = sCreDate;
	}

	public Date geteCreDate() {
		return eCreDate;
	}

	public void seteCreDate(Date eCreDate) {
		this.eCreDate = eCreDate;
	}

	public String getValid() {
		return valid;
	}

	public void setValid(String valid) {
		this.valid = valid;
	}

	public String getRptName() {
		return rptName;
	}

	public void setRptName(String rptName) {
		this.rptName = rptName;
	}

	public String getRptExplain() {
		return rptExplain;
	}

	public void setRptExplain(String rptExplain) {
		this.rptExplain = rptExplain;
	}

	public String getMarqueeFlag() {
		return marqueeFlag;
	}

	public void setMarqueeFlag(String marqueeFlag) {
		this.marqueeFlag = marqueeFlag;
	}

	public String getMarqueeTxt() {
		return marqueeTxt;
	}

	public void setMarqueeTxt(String marqueeTxt) {
		this.marqueeTxt = marqueeTxt;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getRealFileName() {
		return realFileName;
	}

	public void setRealFileName(String realFileName) {
		this.realFileName = realFileName;
	}

	public List<String> getRoles() {
		return roles;
	}

	public void setRoles(List<String> roles) {
		this.roles = roles;
	}

	public String getUploadFlag() {
		return uploadFlag;
	}

	public void setUploadFlag(String uploadFlag) {
		this.uploadFlag = uploadFlag;
	}

	public String getORG_TYPE() {
		return ORG_TYPE;
	}

	public void setORG_TYPE(String oRG_TYPE) {
		ORG_TYPE = oRG_TYPE;
	}

	public String getDEPT_NAME() {
		return DEPT_NAME;
	}

	public void setDEPT_NAME(String dEPT_NAME) {
		DEPT_NAME = dEPT_NAME;
	}

	public String getWtfflag() {
		return wtfflag;
	}

	public void setWtfflag(String wtfflag) {
		this.wtfflag = wtfflag;
	}

	public List<String> getWtfuser() {
		return wtfuser;
	}

	public void setWtfuser(List<String> wtfuser) {
		this.wtfuser = wtfuser;
	}

	public List<String> getWtfupload() {
		return wtfupload;
	}

	public void setWtfupload(List<String> wtfupload) {
		this.wtfupload = wtfupload;
	}

	public String getALL_DEPT() {
		return ALL_DEPT;
	}

	public void setALL_DEPT(String aLL_DEPT) {
		ALL_DEPT = aLL_DEPT;
	}

	public Boolean getEXPORT_YN() {
		return EXPORT_YN;
	}

	public void setEXPORT_YN(Boolean eXPORT_YN) {
		EXPORT_YN = eXPORT_YN;
	}
	
	public String[] getSeq_list() {
		return seq_list;
	}

	public void setSeq_list(String[] seq_list) {
		this.seq_list = seq_list;
	}

	public String[] getCol_seq_list() {
		return col_seq_list;
	}

	public void setCol_seq_list(String[] col_seq_list) {
		this.col_seq_list = col_seq_list;
	}

	public String getCol_seq() {
		return col_seq;
	}

	public void setCol_seq(String col_seq) {
		this.col_seq = col_seq;
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

	public String getFc() {
		return fc;
	}

	public void setFc(String fc) {
		this.fc = fc;
	}

	public String getChar_type() {
		return char_type;
	}

	public void setChar_type(String char_type) {
		this.char_type = char_type;
	}

	public String getSetupCategory() {
		return setupCategory;
	}

	public void setSetupCategory(String setupCategory) {
		this.setupCategory = setupCategory;
	}

	public Boolean getPms900() {
		return pms900;
	}

	public void setPms900(Boolean pms900) {
		this.pms900 = pms900;
	}
	
}
