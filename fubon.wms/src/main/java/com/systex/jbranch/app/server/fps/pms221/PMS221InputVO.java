package com.systex.jbranch.app.server.fps.pms221;
import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;
/**
 * 
 * Copy Right Information : Forms Syntron <br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.7.0 <br>
 * Description : 主管獎勵金報表InputVO<br>
 * Comments Name : PMS221InputVO.java<br>
 * Author :zhouyiqiong<br>
 * Date :2016年11月10日 <br>
 * Version : 1.01 <br>
 * Editor : zhouyiqiong<br>
 * Editor Date : 2016年11月10日<br>
 */
public class PMS221InputVO extends PagingInputVO{
	private String sTime;
	private String rptVersion; //報表版本
	private String branch_nbr;            //分行
	private String region_center_id;     //區域中心
	private String branch_area_id;		   //營運區
	private String role;       //登陸者角色
	private String personType;
	private String emp_id;
	public String getPersonType()
	{
		return personType;
	}
	public void setPersonType(String personType)
	{
		this.personType = personType;
	}
	public String getRole()
	{
		return role;
	}
	public void setRole(String role)
	{
		this.role = role;
	}
	public String getsTime()
	{
		return sTime;
	}
	public void setsTime(String sTime)
	{
		this.sTime = sTime;
	}
	
	public String getRptVersion()
	{
		return rptVersion;
	}
	public void setRptVersion(String rptVersion)
	{
		this.rptVersion = rptVersion;
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
	public String getEmp_id()
	{
		return emp_id;
	}
	public void setEmp_id(String emp_id)
	{
		this.emp_id = emp_id;
	}
}
