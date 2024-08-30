package com.systex.jbranch.app.server.fps.pms328;

import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;
/**
 * 
 * Copy Right Information :  <br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.7.0 <br>
 * Description : 客戶數InputVO<br>
 * Comments Name : PMS328InputVO.java<br>
 * Author :Kevin<br>
 * Date :2016年05月31日 <br>
 * Version : 1.01 <br>
 * Editor : Kevin<br>
 * Editor Date : 2017年01月31日<br>
 */
public class PMS328IInputVO extends PagingInputVO {

	private String eTimes;
	private String sTimes;
	private String aocode;		//理專
	private String type;
	private String branch;		//分行
	private String region;		//區域中心
	private String op;			//營運區
	private List list;
	private List list_AOCODE;
	private String ROB;			//欄位型態
	
	private String eTime;
	private String sTime;
	
	private String NOT_EXIST_UHRM;

	public String getNOT_EXIST_UHRM() {
		return NOT_EXIST_UHRM;
	}

	public void setNOT_EXIST_UHRM(String nOT_EXIST_UHRM) {
		NOT_EXIST_UHRM = nOT_EXIST_UHRM;
	}

	public List getList_AOCODE() {
		return list_AOCODE;
	}

	public void setList_AOCODE(List list_AOCODE) {
		this.list_AOCODE = list_AOCODE;
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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getROB() {
		return ROB;
	}

	public void setROB(String rOB) {
		ROB = rOB;
	}

	public List getList() {
		return list;
	}

	public void setList(List list) {
		this.list = list;
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

	public void setOp(String op) {
		this.op = op;
	}

	public String geteTimes() {
		return eTimes;
	}

	public String getsTimes() {
		return sTimes;
	}

	public void seteTimes(String eTimes) {
		this.eTimes = eTimes;
	}

	public void setsTimes(String sTimes) {
		this.sTimes = sTimes;
	}
}
