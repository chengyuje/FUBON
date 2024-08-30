package com.systex.jbranch.app.server.fps.pms213;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

/**
 * 
 * Copy Right Information : Forms Syntron <br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.6.0 <br>
 * Description : <br>
 * Comments Name : PMS213QueryOutputVO.java<br>
 * Author :WKK<br>
 * Date :2016年12月5日 <br>
 * Version : 1.01 <br>
 * Editor : WKK<br>
 * Editor Date : 2016年12月5日<br>
 */
public class PMS213QueryOutputVO extends PagingOutputVO
{
	private String userId;
	
	private List<Map<String,Object>> outputLargeAgrList;
	
	private String errorMessage;                   //上傳結果錯誤信息反饋

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

	public String getErrorMessage()
	{
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage)
	{
		this.errorMessage = errorMessage;
	}
}
