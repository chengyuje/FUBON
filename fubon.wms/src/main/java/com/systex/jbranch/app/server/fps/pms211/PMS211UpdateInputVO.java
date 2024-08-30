package com.systex.jbranch.app.server.fps.pms211;

import java.util.List;
import java.util.Map;

/**
 * 
 * Copy Right Information : Forms Syntron <br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.6.0 <br>
 * Description : <br>
 * Comments Name : PMS211UpdateInputVO.java<br>
 * Author :WKK<br>
 * Date :2016年12月2日 <br>
 * Version : 1.01 <br>
 * Editor : WKK<br>
 * Editor Date : 2016年12月2日<br>
 */
public class PMS211UpdateInputVO
{
	private String userId;
	private List<Map<String,Object>> inputNewList;
	public String getUserId()
	{
		return userId;
	}
	public void setUserId(String userId)
	{
		this.userId = userId;
	}
	public List<Map<String, Object>> getInputNewList()
	{
		return inputNewList;
	}
	public void setInputNewList(List<Map<String, Object>> inputNewList)
	{
		this.inputNewList = inputNewList;
	}
	
}
