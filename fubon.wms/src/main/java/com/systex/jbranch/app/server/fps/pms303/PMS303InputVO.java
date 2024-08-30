package com.systex.jbranch.app.server.fps.pms303;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

/**
 * 
 * Copy Right Information : <br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.7.0 <br>
 * Description : 分行保險速報統計InputVO<br>
 * Comments Name : PMS303InputVO.java<br>
 * Author :Kevin<br>
 * Date :2016年08月28日 <br>
 * Version : 1.01 <br>
 * Editor : Kevin<br>
 * Editor Date : 2017年02月01日<br>
 */
public class PMS303InputVO extends PagingInputVO {
	private List<Map<String,Object>> list;
	private String rate;
	private List<Map<String,Object>> totalList;		//全行合計
	private List<Map<String,Object>> centerList;	//處合計
	private List<Map<String,Object>> areaList;		//區合計
	
	private  String branch_nbr ;					// 分行
	private  String region_center_id ;				// 區域中心
	private  String branch_area_id  ;				// 營運區
	private  Date sCreDate ;						// 時間
	private  String ao_code      ;					// 理專
	
	public List<Map<String, Object>> getList() {
		return list;
	}
	
	public void setList(List<Map<String, Object>> list) {
		this.list = list;
	}
	
	public String getRate() {
		return rate;
	}
	
	public void setRate(String rate) {
		this.rate = rate;
	}
	
	public List<Map<String, Object>> getTotalList() {
		return totalList;
	}
	
	public void setTotalList(List<Map<String, Object>> totalList) {
		this.totalList = totalList;
	}
	
	public List<Map<String, Object>> getCenterList() {
		return centerList;
	}
	
	public void setCenterList(List<Map<String, Object>> centerList) {
		this.centerList = centerList;
	}
	
	public List<Map<String, Object>> getAreaList() {
		return areaList;
	}
	
	public void setAreaList(List<Map<String, Object>> areaList) {
		this.areaList = areaList;
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
	
	public String getAo_code() {
		return ao_code;
	}
	
	public void setAo_code(String ao_code) {
		this.ao_code = ao_code;
	}
	
}
