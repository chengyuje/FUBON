
package com.systex.jbranch.app.server.fps.pms211;

import java.util.List;
import java.util.Map;

/**
 * 
 * Copy Right Information : Forms Syntron <br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.6.0 <br>
 * Description : <br>
 * Comments Name : PMS211QueryOutputVO.java<br>
 * Author :WKK<br>
 * Date :2016年12月1日 <br>
 * Version : 1.01 <br>
 * Editor : WKK<br>
 * Editor Date : 2016年12月1日<br>
 */
public class PMS211QueryOutputVO
{
	private String userId;
	
	private String errorMsg;

	private String message;
	
	private List<Map<String,Object>> outputLargeAgrList;

	
	public String getUserId()
	{
		return userId;
	}

	public void setUserId(String userId)
	{
		this.userId = userId;
	}

	public List<Map<String, Object>> getOutputLargeAgrList()
	{
		return outputLargeAgrList;
	}

	public void setOutputLargeAgrList(List<Map<String, Object>> outputLargeAgrList)
	{
		this.outputLargeAgrList = outputLargeAgrList;
	}

	public String getErrorMsg()
	{
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg)
	{
		this.errorMsg = errorMsg;
	}

	public String getMessage()
	{
		return message;
	}

	public void setMessage(String message)
	{
		this.message = message;
	}
	
}
