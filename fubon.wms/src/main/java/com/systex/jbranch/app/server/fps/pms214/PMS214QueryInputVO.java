package com.systex.jbranch.app.server.fps.pms214;


import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

/**
 * Copy Right Information : Forms Syntron <br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.7.0 <br>
 * Description :調整客戶收益(整批)Controller<br>
 * Comments Name : PMS214.java<br>
 * Author : cty<br>
 * Date :2016年11月23日 <br>
 * Version : 1.0 <br>
 * Editor : cty<br>
 * Editor Date : 2016年11月23日<br>
 */
public class PMS214QueryInputVO extends PagingInputVO {
	private String userId;
	
	private String custId;
	
	private String AOCODE;
	
	private String adjType;
	
	private String yearMon;
	
	private String assisId;
	
	private String adjDesc;
	
	private String branch_nbr;   //新增分行

	public String getUserId()
	{
		return userId;
	}

	public void setUserId(String userId)
	{
		this.userId = userId;
	}

	public String getCustId()
	{
		return custId;
	}

	public void setCustId(String custId)
	{
		this.custId = custId;
	}

	public String getAOCODE()
	{
		return AOCODE;
	}

	public void setAOCODE(String aOCODE)
	{
		AOCODE = aOCODE;
	}

	public String getAdjType()
	{
		return adjType;
	}

	public void setAdjType(String adjType)
	{
		this.adjType = adjType;
	}

	public String getYearMon()
	{
		return yearMon;
	}

	public void setYearMon(String yearMon)
	{
		this.yearMon = yearMon;
	}

	public String getAssisId()
	{
		return assisId;
	}

	public void setAssisId(String assisId)
	{
		this.assisId = assisId;
	}

	public String getAdjDesc()
	{
		return adjDesc;
	}

	public void setAdjDesc(String adjDesc)
	{
		this.adjDesc = adjDesc;
	}

	public String getBranch_nbr() {
		return branch_nbr;
	}

	public void setBranch_nbr(String branch_nbr) {
		this.branch_nbr = branch_nbr;
	}
	
	
	
}
