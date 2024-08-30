package com.systex.jbranch.app.server.fps.pms348;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class PMS348InputVO extends PagingInputVO {

//	private String aocode; // 理專
//	private String EMP_ID; // 員編
//	private String YEARMON; // 年月
//	private String dataMonth; // 日期
//	
//
//	private String rc_id; // 區域中心
//	private String op_id; // 營運區
//	private String br_id; // 分行
	private List<Map<String, Object>> List; // 儲存比較用 LIST
	private List<Map<String, Object>> List2; // 儲存比較用 LIST2
//	private String branch; // 區域中心(舊)
//	private String region;	// 營運區(舊)
//	private String op;	// 分行(舊)
//	private Date sTime;
	private  String ao_code      ;// 理專
	private  String branch_nbr ;// 分行
	private  String region_center_id ;// 區域中心
	private  String branch_area_id  ;// 營運區

	private  String sCreDate ;// 時間
	
	
	public String getsCreDate() {
		return sCreDate;
	}

	public void setsCreDate(String sCreDate) {
		this.sCreDate = sCreDate;
	}

	public String getAo_code() {
		return ao_code;
	}

	public void setAo_code(String ao_code) {
		this.ao_code = ao_code;
	}

	public String getBranch_nbr() {
		return branch_nbr;
	}

	public void setBranch_nbr(String branch_nbr) {
		this.branch_nbr = branch_nbr;
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

	
	
	
	

	public List<Map<String, Object>> getList() {
		return List;
	}

	public List<Map<String, Object>> getList2() {
		return List2;
	}

	public void setList(List<Map<String, Object>> list) {
		List = list;
	}

	public void setList2(List<Map<String, Object>> list2) {
		List2 = list2;
	}

	
}
