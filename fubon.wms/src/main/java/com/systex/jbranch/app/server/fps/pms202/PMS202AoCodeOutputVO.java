package com.systex.jbranch.app.server.fps.pms202;

import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;
/**
 * 
 * Copy Right Information : <br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.7.0 <br>
 * Description : 主管評估排程管理<br>
 * Comments Name : pms202AoCodeInputVO.java<br>
 * Author :Kevin<br>
 * Date :2016年06月27日 <br>
 * Version : 1.01 <br>
 * Editor : Kevin<br>
 * Editor Date : 2017年02月01日<br>
 */
public class PMS202AoCodeOutputVO extends PagingOutputVO {
	private List resultList;
	private List alllist;  //全部
	private List aocode;   //aocode
	private List branch;   //分行
	private List region;   //區域中心
	private List op;       //營運區
	private List emp;      //姓名
	

	public List getAlllist() {
		return alllist;
	}
	public List getAocode() {
		return aocode;
	}
	public List getBranch() {
		return branch;
	}
	public List getRegion() {
		return region;
	}
	public List getOp() {
		return op;
	}
	public List getEmp() {
		return emp;
	}
	public void setAlllist(List alllist) {
		this.alllist = alllist;
	}
	public void setAocode(List aocode) {
		this.aocode = aocode;
	}
	public void setBranch(List branch) {
		this.branch = branch;
	}
	public void setRegion(List region) {
		this.region = region;
	}
	public void setOp(List op) {
		this.op = op;
	}
	public void setEmp(List emp) {
		this.emp = emp;
	}
	public List getResultList() {
		return resultList;
	}
	public void setResultList(List resultList) {
		this.resultList = resultList;
	}
}
