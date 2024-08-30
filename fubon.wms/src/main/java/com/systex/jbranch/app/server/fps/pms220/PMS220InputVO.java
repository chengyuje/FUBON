package com.systex.jbranch.app.server.fps.pms220;
import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;
/**
 * 
 * Copy Right Information : Forms Syntron <br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.7.0 <br>
 * Description : 主管財務非財務報表InputVO<br>
 * Comments Name : PMS220InputVO.java<br>
 * Author :zhouyiqiong<br>
 * Date :2016年11月10日 <br>
 * Version : 1.01 <br>
 * Editor : zhouyiqiong<br>
 * Editor Date : 2016年11月10日<br>
 */
public class PMS220InputVO extends PagingInputVO{
	private String sTime;
	private String rptVersion; //報表版本
	private String branch;
	private String region;
	private String op;
	private String role;       //登陸者角色
	private String personType;
	private String empId;
	public String getEmpId()
	{
		return empId;
	}
	public void setEmpId(String empId)
	{
		this.empId = empId;
	}
	public String getPersonType()
	{
		return personType;
	}
	public void setPersonType(String personType)
	{
		this.personType = personType;
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
	public String getBranch()
	{
		return branch;
	}
	public void setBranch(String branch)
	{
		this.branch = branch;
	}
	public String getRegion()
	{
		return region;
	}
	public void setRegion(String region)
	{
		this.region = region;
	}
	public String getOp()
	{
		return op;
	}
	public void setOp(String op)
	{
		this.op = op;
	}
	public String getRole()
	{
		return role;
	}
	public void setRole(String role)
	{
		this.role = role;
	}
}

