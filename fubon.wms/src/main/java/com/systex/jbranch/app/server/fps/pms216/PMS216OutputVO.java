package com.systex.jbranch.app.server.fps.pms216;
import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;
/**
 * Copy Right Information : Forms Syntron <br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.7.0 <br>
 * Description : FC獎勵金查詢OutputVO<br>
 * Comments Name : PMS216OutputVO.java<br>
 * Author : zzh<br>
 * Date :2016年11月10日 <br>
 * Version : 1.01 <br>
 * Editor : zzh<br>
 * Editor Date : 2016年11月10日<br>
 */
public class PMS216OutputVO extends PagingOutputVO
{
	private List<Map<String, Object>> orgList;
	private List<Map<String, Object>> largeAgrList; //查詢結果列表
	private List<Map<String, Object>> csvList; //查詢結果列表
	private List<Map<String, Object>> verList;
	private List<String> roleList;
	
	public List<Map<String, Object>> getOrgList()
	{
		return orgList;
	}
	public void setOrgList(List<Map<String, Object>> orgList)
	{
		this.orgList = orgList;
	}
	public List<Map<String, Object>> getLargeAgrList()
	{
		return largeAgrList;
	}
	public void setLargeAgrList(List<Map<String, Object>> largeAgrList)
	{
		this.largeAgrList = largeAgrList;
	}
	public List<Map<String, Object>> getVerList()
	{
		return verList;
	}
	public void setVerList(List<Map<String, Object>> verList)
	{
		this.verList = verList;
	}
	public List<String> getRoleList()
	{
		return roleList;
	}
	public void setRoleList(List<String> roleList)
	{
		this.roleList = roleList;
	}
	public List<Map<String, Object>> getCsvList()
	{
		return csvList;
	}
	public void setCsvList(List<Map<String, Object>> csvList)
	{
		this.csvList = csvList;
	}
}
