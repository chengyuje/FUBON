package com.systex.jbranch.app.server.fps.pms216;
import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;
/**
 * Copy Right Information : Forms Syntron <br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.7.0 <br>
 * Description : FC獎勵金查詢InputVO<br>
 * Comments Name : PMS216InputVO.java<br>
 * Author : zzh<br>
 * Date :2016年11月10日 <br>
 * Version : 1.01 <br>
 * Editor : zzh<br>
 * Editor Date : 2016年11月10日<br>
 */
public class PMS216InputVO extends PagingInputVO
{
	private String rptVersion;       //報表版本
	private String yearMon;          //报表年月
	private String regionCenterId;   //區域中心
	private String branchAreaId;     //營運區
	private String branchNbr;        //分行
	private String aoCode;            //理專
	private String role;             //登陸者角色
	
	public String getRole()
	{
		return role;
	}
	public void setRole(String role)
	{
		this.role = role;
	}
	public String getRptVersion()
	{
		return rptVersion;
	}
	public void setRptVersion(String rptVersion)
	{
		this.rptVersion = rptVersion;
	}
	public String getYearMon()
	{
		return yearMon;
	}
	public void setYearMon(String yearMon)
	{
		this.yearMon = yearMon;
	}
	public String getRegionCenterId()
	{
		return regionCenterId;
	}
	public void setRegionCenterId(String regionCenterId)
	{
		this.regionCenterId = regionCenterId;
	}
	public String getBranchAreaId()
	{
		return branchAreaId;
	}
	public void setBranchAreaId(String branchAreaId)
	{
		this.branchAreaId = branchAreaId;
	}
	public String getBranchNbr()
	{
		return branchNbr;
	}
	public void setBranchNbr(String branchNbr)
	{
		this.branchNbr = branchNbr;
	}
	public String getAoCode()
	{
		return aoCode;
	}
	public void setAoCode(String aoCode)
	{
		this.aoCode = aoCode;
	}
	
}
