package com.systex.jbranch.app.server.fps.pms214;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

/**
 * 
 * Copy Right Information : Forms Syntron <br>
 * Project : fubon_1.wms<br>
 * JDK version used : JDK1.6.0 <br>
 * Description : <br>
 * Comments Name : PMS214InputVO.java<br>
 * Author :WKK<br>
 * Date :2016年12月20日 <br>
 * Version : 1.01 <br>
 * Editor : WKK<br>
 * Editor Date : 2016年12月20日<br>
 */
public class PMS214InputVO extends PagingInputVO
{
	private String yearMon;
	private String userId;
	private String fileName;
	private String realfileName;
	
	public String getYearMon()
	{
		return yearMon;
	}
	public void setYearMon(String yearMon)
	{
		this.yearMon = yearMon;
	}
	public String getUserId()
	{
		return userId;
	}
	public void setUserId(String userId)
	{
		this.userId = userId;
	}
	public String getFileName()
	{
		return fileName;
	}
	public void setFileName(String fileName)
	{
		this.fileName = fileName;
	}
	public String getRealfileName()
	{
		return realfileName;
	}
	public void setRealfileName(String realfileName)
	{
		this.realfileName = realfileName;
	}
	
	
}
