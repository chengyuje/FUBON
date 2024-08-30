package com.systex.jbranch.app.server.fps.pms713;
import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;
/**
 * 
 * Copy Right Information : Forms Syntron <br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.7.0 <br>
 * Description : PMS713InputVO<br>
 * Comments Name : PMS713.java<br>
 * Author :zhouyiqiong<br>
 * Date :2017年1月6日 <br>
 * Version : 1.01 <br>
 * Editor : zhouyiqiong<br>
 * Editor Date : 2017年1月6日<br>
 */
public class PMS713InputVO extends PagingInputVO
{	
	private String yearMon;     //查詢資料月份
	private String empId;
	private String fileName;
	private String delEmpId;
	private String userId;     //登陸者ID
	public String getYearMon()
	{
		return yearMon;
	}
	public void setYearMon(String yearMon)
	{
		this.yearMon = yearMon;
	}
	public String getEmpId()
	{
		return empId;
	}
	public void setEmpId(String empId)
	{
		this.empId = empId;
	}
	public String getFileName()
	{
		return fileName;
	}
	public void setFileName(String fileName)
	{
		this.fileName = fileName;
	}
	public String getDelEmpId()
	{
		return delEmpId;
	}
	public void setDelEmpId(String delEmpId)
	{
		this.delEmpId = delEmpId;
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
