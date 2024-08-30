package com.systex.jbranch.app.server.fps.pms406;

import java.sql.Date;
import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class PMS406InputVO extends PagingInputVO {
	
	private Date sCreDate;
	private Date endDate;
	private String freqType;
	private String rc_id;
	private String op_id;
	private String br_id;
	private String emp_id;

	private String previewType; //報表類型-暫傳空白
	private String reportDate; //YYYYMMDD(日報) YYYYMM(月報)
	private String aoFlag; //只顯示理專
	private String psFlag; //只顯示PS
	private String region_center_id;
	private String branch_area_id;
	private String branch_nbr;
	private String ao_code;
	
	private String memLoginFlag;
	
	private List<Map<String, Object>> list;
	
	private String from181;
	
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

	public String getMemLoginFlag() {
		return memLoginFlag;
	}

	public void setMemLoginFlag(String memLoginFlag) {
		this.memLoginFlag = memLoginFlag;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
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

	public String getPreviewType() {
		return previewType;
	}

	public void setPreviewType(String previewType) {
		this.previewType = previewType;
	}

	public String getReportDate() {
		return reportDate;
	}

	public void setReportDate(String reportDate) {
		this.reportDate = reportDate;
	}

	public String getAoFlag() {
		return aoFlag;
	}

	public void setAoFlag(String aoFlag) {
		this.aoFlag = aoFlag;
	}

	public String getPsFlag() {
		return psFlag;
	}

	public void setPsFlag(String psFlag) {
		this.psFlag = psFlag;
	}

	public Date getsCreDate() {
		return sCreDate;
	}

	public void setsCreDate(Date sCreDate) {
		this.sCreDate = sCreDate;
	}

	public String getFreqType() {
		return freqType;
	}

	public void setFreqType(String freqType) {
		this.freqType = freqType;
	}

	public String getRc_id() {
		return rc_id;
	}

	public void setRc_id(String rc_id) {
		this.rc_id = rc_id;
	}

	public String getOp_id() {
		return op_id;
	}

	public void setOp_id(String op_id) {
		this.op_id = op_id;
	}

	public String getBr_id() {
		return br_id;
	}

	public void setBr_id(String br_id) {
		this.br_id = br_id;
	}

	public String getEmp_id() {
		return emp_id;
	}

	public void setEmp_id(String emp_id) {
		this.emp_id = emp_id;
	}

}
