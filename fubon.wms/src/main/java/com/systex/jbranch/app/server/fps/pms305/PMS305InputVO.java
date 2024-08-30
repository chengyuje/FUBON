package com.systex.jbranch.app.server.fps.pms305;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;
/**
 * 
 * Copy Right Information :  <br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.7.0 <br>
 * Description : 險種別統計InputVO<br>
 * Comments Name : PMS305InputVO.java<br>
 * Author :Kevin<br>
 * Date :2016年07月28日 <br>
 * Version : 1.01 <br>
 * Editor : Kevin<br>
 * Editor Date : 2017年01月31日<br>
 */
public class PMS305InputVO extends PagingInputVO {
//	private String aocode;
//	private String branch;
//	private String region;
//	private String op;
//	private String EMP_ID;
//	private String YEARMON;
//	private Date sTime;
	private String INS_ID;
	private String INS_NAME;
	private List<Map<String, Object>> List;
	private List<Map<String, Object>> List2;
	private  String ao_code      ;// 理專
    private  String branch_nbr ;// 分行
	private  String region_center_id ;// 區域中心
	private  String branch_area_id  ;// 營運區
	private  Date sCreDate ;// 時間
	
	
	
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

	public Date getsCreDate() {
		return sCreDate;
	}

	public void setsCreDate(Date sCreDate) {
		this.sCreDate = sCreDate;
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

	

	public String getINS_ID() {
		return INS_ID;
	}

	public String getINS_NAME() {
		return INS_NAME;
	}

	public void setINS_ID(String iNS_ID) {
		INS_ID = iNS_ID;
	}

	public void setINS_NAME(String iNS_NAME) {
		INS_NAME = iNS_NAME;
	}
}
