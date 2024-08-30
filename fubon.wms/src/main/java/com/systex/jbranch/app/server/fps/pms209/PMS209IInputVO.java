package com.systex.jbranch.app.server.fps.pms209;

import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

/**
 * 
 * Copy Right Information :  <br>
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
public class PMS209IInputVO extends PagingInputVO{

	private String eTime;
	private String sTime;
	private String aocode;
	private String type;
	private String branch;
	private String region;
	private String op;
	
	private List list;
	private String ROB;
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
	
	public String geteTime() {
		return eTime;
	}
	public void seteTime(String eTime) {
		this.eTime = eTime;
	}
	
	public String getsTime() {
		return sTime;
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
	public void setsTime(String sTime) {
		this.sTime = sTime;
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
}
