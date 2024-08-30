package com.systex.jbranch.app.server.fps.pms217;
import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;
/**
 * Copy Right Information : Forms Syntron <br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.7.0 <br>
 * Description : FCH獎勵金查詢OutputVO<br>
 * Comments Name : PMS217OutputVO.java<br>
 * Author : zzh<br>
 * Date :2016年11月7日 <br>
 * Version : 1.01 <br>
 * Editor : zzh<br>
 * Editor Date : 2016年11月7日<br>
 */
public class PMS217OutputVO extends PagingOutputVO
{
	private List<Map<String, Object>> largeAgrList; //查詢結果列表
	private List<Map<String, Object>> verList;      //報表版本列表
	private List<Map<String, Object>> csvList;      //查詢結果列表
	private List<String> roleList;
	private List<Map<String, String>> orgList;
	public List<Map<String, Object>> getCsvList()
	{
		return csvList;
	}
	public void setCsvList(List<Map<String, Object>> csvList)
	{
		this.csvList = csvList;
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
	public List<Map<String, String>> getOrgList()
	{
		return orgList;
	}
	public void setOrgList(List<Map<String, String>> orgList)
	{
		this.orgList = orgList;
	}
}
