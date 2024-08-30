package com.systex.jbranch.app.server.fps.pms225;
import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;
/**
 * 
 * Copy Right Information : Forms Syntron <br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.7.0 <br>
 * Description : <br>
 * Comments Name : PMS225InputVO.java<br>
 * Author :zhouyiqiong<br>
 * Date :2016年11月11日 <br>
 * Version : 1.01 <br>
 * Editor : zhouyiqiong<br>
 * Editor Date : 2016年11月11日<br>
 */
public class PMS225InputVO extends PagingInputVO{	
	private String sTime;          //报表年月
	private String loginRegionID;  //登录者的區域中心ID
	private int rptType;
	private String ao_code;    	   //理專
	private String emp_id;
	private String branch_nbr;            //分行
	private String region_center_id;     //區域中心
	private String branch_area_id;		   //營運區
	public String getsTime()
	{
		return sTime;
	}
	public void setsTime(String sTime)
	{
		this.sTime = sTime;
	}
	public String getLoginRegionID()
	{
		return loginRegionID;
	}
	public void setLoginRegionID(String loginRegionID)
	{
		this.loginRegionID = loginRegionID;
	}
	public int getRptType()
	{
		return rptType;
	}
	public void setRptType(int rptType)
	{
		this.rptType = rptType;
	}
	public String getAo_code()
	{
		return ao_code;
	}
	public void setAo_code(String ao_code)
	{
		this.ao_code = ao_code;
	}
	
	public String getEmp_id()
	{
		return emp_id;
	}
	public void setEmp_id(String emp_id)
	{
		this.emp_id = emp_id;
	}
	public String getBranch_nbr()
	{
		return branch_nbr;
	}
	public void setBranch_nbr(String branch_nbr)
	{
		this.branch_nbr = branch_nbr;
	}
	public String getRegion_center_id()
	{
		return region_center_id;
	}
	public void setRegion_center_id(String region_center_id)
	{
		this.region_center_id = region_center_id;
	}
	public String getBranch_area_id()
	{
		return branch_area_id;
	}
	public void setBranch_area_id(String branch_area_id)
	{
		this.branch_area_id = branch_area_id;
	}
}
