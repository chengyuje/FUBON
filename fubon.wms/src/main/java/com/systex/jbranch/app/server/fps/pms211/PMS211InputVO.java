package com.systex.jbranch.app.server.fps.pms211;
/**
 * 
 * Copy Right Information : Forms Syntron <br>
 * Project : fubon_1.wms<br>
 * JDK version used : JDK1.6.0 <br>
 * Description : <br>
 * Comments Name : PMS211InputVO.java<br>
 * Author :WKK<br>
 * Date :2016年12月27日 <br>
 * Version : 1.01 <br>
 * Editor : WKK<br>
 * Editor Date : 2016年12月27日<br>
 */
public class PMS211InputVO
{
	private String userId;
	private String execDate;
	private String yearMon;
	private String versionFlag;
	private String selectFlag;
	private String tableName;
	public String getUserId()
	{
		return userId;
	}
	public void setUserId(String userId)
	{
		this.userId = userId;
	}
	public String getExecDate()
	{
		return execDate;
	}
	public void setExecDate(String execDate)
	{
		this.execDate = execDate;
	}
	public String getYearMon()
	{
		return yearMon;
	}
	public void setYearMon(String yearMon)
	{
		this.yearMon = yearMon;
	}
	public String getVersionFlag()
	{
		return versionFlag;
	}
	public void setVersionFlag(String versionFlag)
	{
		this.versionFlag = versionFlag;
	}
	public String getSelectFlag()
	{
		return selectFlag;
	}
	public void setSelectFlag(String selectFlag)
	{
		this.selectFlag = selectFlag;
	}
	public String getTableName()
	{
		return tableName;
	}
	public void setTableName(String tableName)
	{
		this.tableName = tableName;
	}
	
}
