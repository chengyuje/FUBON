package com.systex.jbranch.app.server.fps.pms717;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

/**
 * 
 * Copy Right Information : Forms Syntron <br>
 * Project : fubon_1.wms<br>
 * JDK version used : JDK1.6.0 <br>
 * Description : <br>
 * Comments Name : PMS717OutputVO.java<br>
 * Author :WKK<br>
 * Date :2017年3月30日 <br>
 * Version : 1.01 <br>
 * Editor : WKK<br>
 * Editor Date : 2017年3月30日<br>
 */
public class PMS717OutputVO extends PagingOutputVO
{
	private List<Map<String,Object>> resultList;
	private List<Map<String,Object>> csvList;
	private String prodType;
	private int flag;
	private String errorMessage;
	
	public String getProdType()
	{
		return prodType;
	}

	public void setProdType(String prodType)
	{
		this.prodType = prodType;
	}

	public List<Map<String, Object>> getCsvList()
	{
		return csvList;
	}

	public void setCsvList(List<Map<String, Object>> csvList)
	{
		this.csvList = csvList;
	}

	public String getErrorMessage()
	{
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage)
	{
		this.errorMessage = errorMessage;
	}

	public int getFlag()
	{
		return flag;
	}

	public void setFlag(int flag)
	{
		this.flag = flag;
	}

	public List<Map<String, Object>> getResultList()
	{
		return resultList;
	}

	public void setResultList(List<Map<String, Object>> resultList)
	{
		this.resultList = resultList;
	}

	
	
	
}
