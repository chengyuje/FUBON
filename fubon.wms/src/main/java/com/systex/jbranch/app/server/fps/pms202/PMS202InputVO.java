package com.systex.jbranch.app.server.fps.pms202;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;
import java.util.Date;
/**
 * 
 * Copy Right Information : <br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.7.0 <br>
 * Description : 主管評估排程管理<br>
 * Comments Name : pms202InputVO.java<br>
 * Author :Kevin<br>
 * Date :2016年06月27日 <br>
 * Version : 1.01 <br>
 * Editor : Kevin<br>
 * Editor Date : 2017年02月01日<br>
 */
public class PMS202InputVO extends PagingInputVO{
	
	private String eTime;
	
	private String ao_code;   //ao_code
	private String branch_nbr;   //分行
	private String region;   //區域中心
	private String branch_area_id    ;   //營運區
	private String aojob ;   //理專職級
	private String type  ;   //狀態
	private Date datec ;
	private String sCreDate;
	
	
	
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
	public String getBranch_area_id() {
		return branch_area_id;
	}
	public void setBranch_area_id(String branch_area_id) {
		this.branch_area_id = branch_area_id;
	}
	public String getsCreDate() {
		return sCreDate;
	}
	public void setsCreDate(String sCreDate) {
		this.sCreDate = sCreDate;
	}
	public Date getDatec() {
		return datec;
	}
	public void setDatec(Date datec) {
		this.datec = datec;
	}
	
	public String geteTime() {
		return eTime;
	}
	public void seteTime(String eTime) {
		this.eTime = eTime;
	}

//	public String getAocode() {
//		return aocode;
//	}
//	public String getBranch() {
//		return branch;
//	}
	public String getRegion() {
		return region;
	}
//	public String getOp() {
//		return op;
//	}
	public String getAojob() {
		return aojob;
	}
	public String getType() {
		return type;
	}
//	public void setAocode(String aocode) {
//		this.aocode = aocode;
//	}
//	public void setBranch(String branch) {
//		this.branch = branch;
//	}
	public void setRegion(String region) {
		this.region = region;
	}
//	public void setOp(String op) {
//		this.op = op;
//	}
	public void setAojob(String aojob) {
		this.aojob = aojob;
	}
	public void setType(String type) {
		this.type = type;
	}

}
