package com.systex.jbranch.app.server.fps.pms211;
/**
 * 
 * Copy Right Information : Forms Syntron <br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.6.0 <br>
 * Description : <br>
 * Comments Name : PMS211QueryInputVO.java<br>
 * Author :WKK<br>
 * Date :2016年12月1日 <br>
 * Version : 1.01 <br>
 * Editor : WKK<br>
 * Editor Date : 2016年12月1日<br>
 */
public class PMS211QueryInputVO
{
	private String userId;
	private String inputDataMonth;
	private String inputVersionChoose;
	public String getInputDataMonth()
	{
		return inputDataMonth;
	}
	
	public String getInputVersionChoose()
	{
		return inputVersionChoose;
	}

	public void setInputDataMonth(String inputDataMonth)
	{
		this.inputDataMonth = inputDataMonth;
	}

	public void setInputVersionChoose(String inputVersionChoose)
	{
		this.inputVersionChoose = inputVersionChoose;
	}
	public String getUserId()
	{
		return userId;
	}
	public void setUserId(String userId)
	{
		this.userId = userId;
	}
	
}
