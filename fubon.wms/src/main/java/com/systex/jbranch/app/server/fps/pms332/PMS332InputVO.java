package com.systex.jbranch.app.server.fps.pms332;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

/**
 * 
 * Copy Right Information : <br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.7.0 <br>
 * Description : 百大貢獻度客戶報表InputVO<br>
 * Comments Name : PMS332InputVO.java<br>
 * Author :Kevin<br>
 * Date :2016年06月03日 <br>
 * Version : 1.01 <br>
 * Editor : Kevin<br>
 * Editor Date : 2017年01月31日<br>
 */
public class PMS332InputVO extends PagingInputVO {

	private String BT;
	private String VIP;
	private String aojob; // 職級
	private String NNUM;
	//private String region; // 區域中心
	//private String op; // 營運區
	private String EMP_ID; // 員編
	private String YEARMON; // 年月
	private String sTime;
	//private String aocode;
	private String sCreDate;
	private String reportDate;
	private String region_center_id; //區域中心
	private String branch_area_id; //營運區
	private String branch_nbr; //分行別
	private String ao_code; //理專
	
	

	public String getsCreDate() {
		return sCreDate;
	}

	public void setsCreDate(String sCreDate) {
		this.sCreDate = sCreDate;
	}

	public String getReportDate() {
		return reportDate;
	}

	public void setReportDate(String reportDate) {
		this.reportDate = reportDate;
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

	public String getBT() {
		return BT;
	}

	public String getVIP() {
		return VIP;
	}

	public String getAojob() {
		return aojob;
	}

	public String getNNUM() {
		return NNUM;
	}

	public void setBT(String bT) {
		BT = bT;
	}

	public void setVIP(String vIP) {
		VIP = vIP;
	}

	public void setAojob(String aojob) {
		this.aojob = aojob;
	}

	public void setNNUM(String nNUM) {
		NNUM = nNUM;
	}

//	private String branch;

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

//	public String getAocode() {
//		return aocode;
//	}

//	public String getBranch() {
//		return branch;
//	}

//	public String getRegion() {
//		return region;
//	}

//	public String getOp() {
//		return op;
//	}

	public String getsTime() {
		return sTime;
	}

	public void setsTime(String sTime) {
		this.sTime = sTime;
	}

//	public void setAocode(String aocode) {
//		this.aocode = aocode;
//	}

//	public void setBranch(String branch) {
//		this.branch = branch;
//	}

//	public void setRegion(String region) {
//		this.region = region;
//	}

//	public void setOp(String op) {
//		this.op = op;
//	}
}
