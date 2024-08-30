package com.systex.jbranch.app.server.fps.pms213;

import java.util.List;
import java.util.Map;

/**
 * 
 * Copy Right Information : Forms Syntron <br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.6.0 <br>
 * Description : <br>
 * Comments Name : PMS213UpdateInputVO.java<br>
 * Author :WKK<br>
 * Date :2016年12月5日 <br>
 * Version : 1.01 <br>
 * Editor : WKK<br>
 * Editor Date : 2016年12月5日<br>
 */
public class PMS213UpdateInputVO
{
	private String yearMon;
	private String userId;
	private String seqNo;
	private String fileName;
	private List<Map<String,Object>> inputList;
	
	public String getYearMon()
	{
		return yearMon;
	}
	public void setYearMon(String yearMon)
	{
		this.yearMon = yearMon;
	}
	public String getSeqNo()
	{
		return seqNo;
	}
	public void setSeqNo(String seqNo)
	{
		this.seqNo = seqNo;
	}
	public String getUserId()
	{
		return userId;
	}
	public void setUserId(String userId)
	{
		this.userId = userId;
	}
	public List<Map<String, Object>> getInputList()
	{
		return inputList;
	}
	public void setInputList(List<Map<String, Object>> inputList)
	{
		this.inputList = inputList;
	}
	public String getFileName()
	{
		return fileName;
	}
	public void setFileName(String fileName)
	{
		this.fileName = fileName;
	}
	
	
}
