package com.systex.jbranch.app.server.fps.pms717;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

/**
 * 
 * Copy Right Information : Forms Syntron <br>
 * Project : fubon_1.wms<br>
 * JDK version used : JDK1.6.0 <br>
 * Description : <br>
 * Comments Name : PMS717InputVO.java<br>
 * Author :WKK<br>
 * Date :2017年3月30日 <br>
 * Version : 1.01 <br>
 * Editor : WKK<br>
 * Editor Date : 2017年3月30日<br>
 */
public class PMS717InputVO extends PagingInputVO
{
	private String yearMon;
	private String prodType;
	private String prodId;
	
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
	public String getProdType()
	{
		return prodType;
	}
	public void setProdType(String prodType)
	{
		this.prodType = prodType;
	}
	public String getProdId()
	{
		return prodId;
	}
	public void setProdId(String prodId)
	{
		this.prodId = prodId;
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
