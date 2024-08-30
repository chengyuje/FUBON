package com.systex.jbranch.app.server.fps.pms715;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;
/**
 * 
 * Copy Right Information : Forms Syntron <br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.7.0 <br>
 * Description : 年度KPI成績查詢InputVO<br>
 * Comments Name : PMS715InputVO.java<br>
 * Author :zhouyiqiong<br>
 * Date :2017年2月6日 <br>
 * Version : 1.01 <br>
 * Editor : zhouyiqiong<br>
 * Editor Date : 2017年2月6日<br>
 */
public class PMS715InputVO extends PagingInputVO{
	private String sTime;      //報表年月
	private String yearMon;    //
	private String yearMonOp;    //
	private String execFlag;    //
	private String role;       //登陸者角色
	private String openFlag;   //是否開放
	private String emp_id;    //理專
	private String branch_nbr;            //分行
	private String region_center_id;     //區域中心
	private String branch_area_id;		   //營運區
	private String personType;
	private String subProjectSeqId;
	
	public String getSubProjectSeqId()
	{
		return subProjectSeqId;
	}
	public void setSubProjectSeqId(String subProjectSeqId)
	{
		this.subProjectSeqId = subProjectSeqId;
	}
	public String getRole()
	{
		return role;
	}
	public void setRole(String role)
	{
		this.role = role;
	}
	public String getsTime() {
		return sTime;
	}
	public void setsTime(String sTime) {
		this.sTime = sTime;
	}
	public String getOpenFlag()
	{
		return openFlag;
	}
	public void setOpenFlag(String openFlag)
	{
		this.openFlag = openFlag;
	}
	public String getYearMon()
	{
		return yearMon;
	}
	public void setYearMon(String yearMon)
	{
		this.yearMon = yearMon;
	}
	public String getYearMonOp()
	{
		return yearMonOp;
	}
	public void setYearMonOp(String yearMonOp)
	{
		this.yearMonOp = yearMonOp;
	}
	public String getEmp_id()
	{
		return emp_id;
	}
	public void setEmp_id(String emp_id)
	{
		this.emp_id = emp_id;
	}
	public String getBranch_nbr()
	{
		return branch_nbr;
	}
	public void setBranch_nbr(String branch_nbr)
	{
		this.branch_nbr = branch_nbr;
	}
	public String getRegion_center_id()
	{
		return region_center_id;
	}
	public void setRegion_center_id(String region_center_id)
	{
		this.region_center_id = region_center_id;
	}
	public String getBranch_area_id()
	{
		return branch_area_id;
	}
	public void setBranch_area_id(String branch_area_id)
	{
		this.branch_area_id = branch_area_id;
	}
	public String getExecFlag() {
		return execFlag;
	}
	public void setExecFlag(String execFlag) {
		this.execFlag = execFlag;
	}
	public String getPersonType() {
		return personType;
	}
	public void setPersonType(String personType) {
		this.personType = personType;
	}
}
