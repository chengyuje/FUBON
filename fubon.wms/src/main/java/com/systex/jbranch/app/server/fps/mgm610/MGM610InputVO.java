package com.systex.jbranch.app.server.fps.mgm610;

import java.sql.Date;
import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class MGM610InputVO extends PagingInputVO{
	private String act_seq;				//活動代碼
	private String region_center_id;	//業務處
	private String branch_area_id;		//營運區
	private String branch_nbr;			//分行
	private Date case_start;			//案件年月(起)
	private Date case_end;				//案件年月(迄)
	
	private List<Map<String,Object>> resultList;	//下載查詢結果
	
	public String getAct_seq() {
		return act_seq;
	}
	
	public void setAct_seq(String act_seq) {
		this.act_seq = act_seq;
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
	
	public Date getCase_start() {
		return case_start;
	}

	public void setCase_start(Date case_start) {
		this.case_start = case_start;
	}

	public Date getCase_end() {
		return case_end;
	}

	public void setCase_end(Date case_end) {
		this.case_end = case_end;
	}

	public List<Map<String, Object>> getResultList() {
		return resultList;
	}

	public void setResultList(List<Map<String, Object>> resultList) {
		this.resultList = resultList;
	}
	
}
