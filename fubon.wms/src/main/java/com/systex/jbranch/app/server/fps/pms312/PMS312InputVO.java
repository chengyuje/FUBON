package com.systex.jbranch.app.server.fps.pms312;

import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;
/**
 * 
 * Copy Right Information : <br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.7.0 <br>
 * Description : 房信貸案件來源統計表InputVO<br>
 * Comments Name : PMS312InputVO.java<br>
 * Author :Kevin<br>
 * Date :2016年08月02日 <br>
 * Version : 1.01 <br>
 * Editor : Kevin<br>
 * Editor Date : 2017年08月02日<br>
 */

public class PMS312InputVO extends PagingInputVO{
	

	private String aocode;  //理專
	private String branch;	//分行
	private String region;	//區域中心
	private String op;		//營運區
	private String EMP_ID;	//員編
	private String YEARMON;	//年月
	private String sTime;
	private String checked;
	private List list;
	private String type2;
	
	private  String ao_code      ;// 理專
	private  String branch_nbr ;// 分行
	private  String region_center_id ;// 區域中心
	private  String branch_area_id  ;// 營運區
	private  String sCreDate ;// 時間



	
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

	public String getsCreDate() {
		return sCreDate;
	}

	public void setsCreDate(String sCreDate) {
		this.sCreDate = sCreDate;
	}

	public String getType2() {
		return type2;
	}

	public void setType2(String type2) {
		this.type2 = type2;
	}

	public List getList() {
		return list;
	}
	
	public String getsTime() {
		return sTime;
	}
	public void setsTime(String sTime) {
		this.sTime = sTime;
	}
	public void setList(List list) {
		this.list = list;
	}
	public String getEMP_ID() {
		return EMP_ID;
	}
	public String getYEARMON() {
		return YEARMON;
	}
	public void setEMP_ID(String eMP_ID) {
		EMP_ID = eMP_ID;
	}
	public void setYEARMON(String yEARMON) {
		YEARMON = yEARMON;
	}
	public String getAocode() {
		return aocode;
	}
	public String getBranch() {
		return branch;
	}

	public String getRegion() {
		return region;
	}
	public String getOp() {
		return op;
	}
	

	public void setAocode(String aocode) {
		this.aocode = aocode;
	}
	public void setBranch(String branch) {
		this.branch = branch;
	}
	public void setRegion(String region) {
		this.region = region;
	}
	public String getChecked() {
		return checked;
	}

	public void setChecked(String checked) {
		this.checked = checked;
	}

	public void setOp(String op) {
		this.op = op;
	}
}
