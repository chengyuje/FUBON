package com.systex.jbranch.app.server.fps.pms209;

import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;
/**
 * 
 * Copy Right Information : <br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.7.0 <br>
 * Description : 競爭力趨勢<br>
 * Comments Name : pms209InputVO.java<br>
 * Author :Kevin<br>
 * Date :2016年06月28日 <br>
 * Version : 1.01 <br>
 * Editor : Kevin<br>
 * Editor Date : 2017年02月01日<br>
 */
public class PMS209InputVO extends PagingInputVO{

	private String eTime;
	private String sTime;
	private String region_center_id;
	private String branch_area_id;
	private String branch_nbr;
	private String aojob;
	private String ao_code;
	private String prodType;    //產品類型
	private String prodID;    //產品代碼
	private String type;
	private String type_Q;
	
	private List paramList;
	private List titleList;
	
	public List getTitleList() {
		return titleList;
	}

	public void setTitleList(List titleList) {
		this.titleList = titleList;
	}

	public List getParamList() {
		return paramList;
	}

	public void setParamList(List paramList) {
		this.paramList = paramList;
	}

	public String getAojob() {
		return aojob;
	}
	
	public void setAojob(String aojob) {
		this.aojob = aojob;
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
	
	public String getProdType() {
		return prodType;
	}
	
	public void setProdType(String prodType) {
		this.prodType = prodType;
	}
	
	public String getProdID() {
		return prodID;
	}
	
	public void setProdID(String prodID) {
		this.prodID = prodID;
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public String geteTime() {
		return eTime;
	}
	
	public void seteTime(String eTime) {
		this.eTime = eTime;
	}

	public String getsTime() {
		return sTime;
	}
	
	public void setsTime(String sTime) {
		this.sTime = sTime;
	}
	
	public String getType_Q() {
		return type_Q;
	}
	
	public void setType_Q(String type_Q) {
		this.type_Q = type_Q;
	}
}
