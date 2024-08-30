package com.systex.jbranch.app.server.fps.pms214;

import java.util.List;
import java.util.Map;

/**
 * 
 * Copy Right Information : Forms Syntron <br>
 * Project : fubon_1.wms<br>
 * JDK version used : JDK1.6.0 <br>
 * Description : <br>
 * Comments Name : PMS214UpdateInputVO.java<br>
 * Author :WKK<br>
 * Date :2016年12月20日 <br>
 * Version : 1.01 <br>
 * Editor : WKK<br>
 * Editor Date : 2016年12月20日<br>
 */
public class PMS214UpdateInputVO
{
	private String userId;
	private String seqNo;
	private String fileName;
	private List<Map<String,Object>> inputList;
	public String getUserId()
	{
		return userId;
	}
	public void setUserId(String userId)
	{
		this.userId = userId;
	}
	public String getSeqNo()
	{
		return seqNo;
	}
	public void setSeqNo(String seqNo)
	{
		this.seqNo = seqNo;
	}
	public String getFileName()
	{
		return fileName;
	}
	public void setFileName(String fileName)
	{
		this.fileName = fileName;
	}
	public List<Map<String, Object>> getInputList()
	{
		return inputList;
	}
	public void setInputList(List<Map<String, Object>> inputList)
	{
		this.inputList = inputList;
	}
	
}
