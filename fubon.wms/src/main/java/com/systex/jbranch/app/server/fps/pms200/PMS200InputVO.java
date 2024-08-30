package com.systex.jbranch.app.server.fps.pms200;

import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class PMS200InputVO extends PagingInputVO{
	  private String dataMonth;
	  private String region_center_id;
	  private String branch_area_id;
	  private String branch_nbr;
	  private String ao_code;
	  private List list;
	  
	  public List getList() {
		return list;
	}
	public void setList(List list) {
		this.list = list;
	}
	public String getDataMonth() {
		return dataMonth;
	}
	public void setDataMonth(String dataMonth) {
		this.dataMonth = dataMonth;
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
	
	
	
}
