package com.systex.jbranch.app.server.fps.pms218;
import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;
/**
 * 
 * Copy Right Information : Forms Syntron <br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.7.0 <br>
 * Description : 理專計績商品報表InputVO<br>
 * Comments Name : PMS218InputVO.java<br>
 * Author :zhouyiqiong<br>
 * Date :2016年11月7日 <br>
 * Version : 1.01 <br>
 * Editor : zhouyiqiong<br>
 * Editor Date : 2016年11月7日<br>
 */
public class PMS218InputVO extends PagingInputVO{
	private String sTime;      //報表年月
	private String rptVersion; //報表版本
	private String role;       //登陸者角色
	private String ao_code;    //理專
	private String branch_nbr;            //分行
	private String region_center_id;     //區域中心
	private String branch_area_id;		   //營運區
	private String PROD_SOURCE;		   //CNR产品收益商品類別
	public String getsTime()
	{
		return sTime;
	}
	public void setsTime(String sTime)
	{
		this.sTime = sTime;
	}
	public String getRptVersion()
	{
		return rptVersion;
	}
	public void setRptVersion(String rptVersion)
	{
		this.rptVersion = rptVersion;
	}
	public String getRole()
	{
		return role;
	}
	public void setRole(String role)
	{
		this.role = role;
	}
	public String getAo_code()
	{
		return ao_code;
	}
	public void setAo_code(String ao_code)
	{
		this.ao_code = ao_code;
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
	public String getPROD_SOURCE()
	{
		return PROD_SOURCE;
	}
	public void setPROD_SOURCE(String pROD_SOURCE)
	{
		PROD_SOURCE = pROD_SOURCE;
	}
}
