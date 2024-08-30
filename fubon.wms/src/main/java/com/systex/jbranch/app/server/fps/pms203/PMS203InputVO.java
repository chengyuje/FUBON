package com.systex.jbranch.app.server.fps.pms203;

import java.sql.Date;
import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class PMS203InputVO extends PagingInputVO {
	private String tgtType;
	
	private String emp_id;
	private String rc_name;
	private String op_name;
	private String br_name;
	private String emp_name;
	private String levelDist;
	private String olTitle;
	private String jobTitleId;
	private double prodGoals;
	private double keepGoals;
	private double advanGoals;
	private String proStrLine;
	private String proHorLine;
	private String proSlaLine;
	private String demStrLine;
	private String demHorLine;
	private String demSlaLine;
	private String fixSal;

	private Date firstDate;
	private String jobTitle;
	private String tarAmount;

	private String invTarAmount;
	private String insTarAmount;
	private String totTarAmount;
	private String exchgTarAmount;
	private String mainPrdId;


	private String sCreDate;
	private String reportDate;
	private String region_center_id; //業務處
	private String branch_area_id; //營運區
	private String branch_nbr; //分行別
	private String ao_code; //理專
	private String NBR_state;
	
	
	private List<Map<String,Object>> list;
	private List<Map<String, Object>> delTARList; 
	private String fileName;

	
	
	public List<Map<String, Object>> getDelTARList() {
		return delTARList;
	}

	public void setDelTARList(List<Map<String, Object>> delTARList) {
		this.delTARList = delTARList;
	}

	public String getNBR_state() {
		return NBR_state;
	}

	public void setNBR_state(String nBR_state) {
		NBR_state = nBR_state;
	}

	public String getRegion_center_id() {
		return region_center_id;
	}

	public void setRegion_center_id(String region_center_id) {
		this.region_center_id = region_center_id;
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

	public String getReportDate() {
		return reportDate;
	}

	public void setReportDate(String reportDate) {
		this.reportDate = reportDate;
	}

	public String getTgtType() {
		return tgtType;
	}

	public void setTgtType(String tgtType) {
		this.tgtType = tgtType;
	}

	

	public String getBranch_area_id() {
		return branch_area_id;
	}

	public void setBranch_area_id(String branch_area_id) {
		this.branch_area_id = branch_area_id;
	}

	public String getEmp_id() {
		return emp_id;
	}

	public void setEmp_id(String emp_id) {
		this.emp_id = emp_id;
	}

	public String getOlTitle() {
		return olTitle;
	}

	public void setOlTitle(String olTitle) {
		this.olTitle = olTitle;
	}

	public String getJobTitleId() {
		return jobTitleId;
	}

	public void setJobTitleId(String jobTitleId) {
		this.jobTitleId = jobTitleId;
	}

	public double getProdGoals() {
		return prodGoals;
	}

	public void setProdGoals(int prodGoals) {
		this.prodGoals = prodGoals;
	}

	public double getKeepGoals() {
		return keepGoals;
	}

	public void setKeepGoals(int keepGoals) {
		this.keepGoals = keepGoals;
	}

	public double getAdvanGoals() {
		return advanGoals;
	}

	public void setAdvanGoals(int advanGoals) {
		this.advanGoals = advanGoals;
	}

	public Date getFirstDate() {
		return firstDate;
	}

	public void setFirstDate(Date firstDate) {
		this.firstDate = firstDate;
	}

	public String getJobTitle() {
		return jobTitle;
	}

	public void setJobTitle(String jobTitle) {
		this.jobTitle = jobTitle;
	}

	public String getMainPrdId() {
		return mainPrdId;
	}

	public void setMainPrdId(String mainPrdId) {
		this.mainPrdId = mainPrdId;
	}

	public String getRc_name() {
		return rc_name;
	}

	public void setRc_name(String rc_name) {
		this.rc_name = rc_name;
	}

	public String getOp_name() {
		return op_name;
	}

	public void setOp_name(String op_name) {
		this.op_name = op_name;
	}

	public String getBr_name() {
		return br_name;
	}

	public void setBr_name(String br_name) {
		this.br_name = br_name;
	}

	public String getEmp_name() {
		return emp_name;
	}

	public void setEmp_name(String emp_name) {
		this.emp_name = emp_name;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getLevelDist() {
		return levelDist;
	}

	public String getProStrLine() {
		return proStrLine;
	}

	public String getProHorLine() {
		return proHorLine;
	}

	public String getProSlaLine() {
		return proSlaLine;
	}

	public String getDemStrLine() {
		return demStrLine;
	}

	public String getDemHorLine() {
		return demHorLine;
	}

	public String getDemSlaLine() {
		return demSlaLine;
	}

	public String getFixSal() {
		return fixSal;
	}

	public String getTarAmount() {
		return tarAmount;
	}

	public String getInvTarAmount() {
		return invTarAmount;
	}

	public String getInsTarAmount() {
		return insTarAmount;
	}

	public String getTotTarAmount() {
		return totTarAmount;
	}

	public void setLevelDist(String levelDist) {
		this.levelDist = levelDist;
	}

	public void setProdGoals(double prodGoals) {
		this.prodGoals = prodGoals;
	}

	public void setKeepGoals(double keepGoals) {
		this.keepGoals = keepGoals;
	}

	public void setAdvanGoals(double advanGoals) {
		this.advanGoals = advanGoals;
	}

	public void setProStrLine(String proStrLine) {
		this.proStrLine = proStrLine;
	}

	public void setProHorLine(String proHorLine) {
		this.proHorLine = proHorLine;
	}

	public void setProSlaLine(String proSlaLine) {
		this.proSlaLine = proSlaLine;
	}

	public void setDemStrLine(String demStrLine) {
		this.demStrLine = demStrLine;
	}

	public void setDemHorLine(String demHorLine) {
		this.demHorLine = demHorLine;
	}

	public void setDemSlaLine(String demSlaLine) {
		this.demSlaLine = demSlaLine;
	}

	public void setFixSal(String fixSal) {
		this.fixSal = fixSal;
	}

	public void setTarAmount(String tarAmount) {
		this.tarAmount = tarAmount;
	}

	public void setInvTarAmount(String invTarAmount) {
		this.invTarAmount = invTarAmount;
	}

	public void setInsTarAmount(String insTarAmount) {
		this.insTarAmount = insTarAmount;
	}

	public void setTotTarAmount(String totTarAmount) {
		this.totTarAmount = totTarAmount;
	}

	public String getsCreDate() {
		return sCreDate;
	}

	public void setsCreDate(String sCreDate) {
		this.sCreDate = sCreDate;
	}

	public List<Map<String,Object>> getList() {
		return list;
	}

	public void setList(List<Map<String,Object>> list) {
		this.list = list;
	}

	public String getExchgTarAmount() {
		return exchgTarAmount;
	}

	public void setExchgTarAmount(String exchgTarAmount) {
		this.exchgTarAmount = exchgTarAmount;
	}
	
}
