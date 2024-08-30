package com.systex.jbranch.app.server.fps.pms329;

import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;
/**
 * 
 * Copy Right Information : <br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.7.0 <br>
 * Description : AUM InputVO<br>
 * Comments Name : PMS328InputVO.java<br>
 * Author :Kevin<br>
 * Date :2016年05月24日 <br>
 * Version : 1.01 <br>
 * Editor : Kevin<br>
 * Editor Date : 2017年01月31日<br>
 */
public class PMS329IInputVO extends PagingInputVO{

	private String eTime;
	private String sTime;
	private String aocode;   //理專
	private String type;
	private String branch;   //分行
	private String region;	//區域中心
	private String op;		//營運區
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
