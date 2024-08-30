package com.systex.jbranch.app.server.fps.mgm510;

import java.sql.Date;
import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class MGM510InputVO extends PagingInputVO{
	private String act_seq;				//活動代碼
	private String region_center_id;	//業務處
	private String branch_area_id;		//營運區
	private String branch_nbr;			//分行
	private String ao_code;				//理專AO CODE
	private String fc_level;			//理專職級
	
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
	
	public String getAo_code() {
		return ao_code;
	}
	
	public void setAo_code(String ao_code) {
		this.ao_code = ao_code;
	}
	
	public String getFc_level() {
		return fc_level;
	}
	
	public void setFc_level(String fc_level) {
		this.fc_level = fc_level;
	}

	public List<Map<String, Object>> getResultList() {
		return resultList;
	}

	public void setResultList(List<Map<String, Object>> resultList) {
		this.resultList = resultList;
	}
	
}
