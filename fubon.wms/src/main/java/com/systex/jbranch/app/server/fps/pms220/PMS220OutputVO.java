package com.systex.jbranch.app.server.fps.pms220;
import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;
/**
 * 
 * Copy Right Information : Forms Syntron <br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.7.0 <br>
 * Description : 主管財務非財務報表OutputVO<br>
 * Comments Name : PMS220OutputVO.java<br>
 * Author :zhouyiqiong<br>
 * Date :2016年11月10日 <br>
 * Version : 1.01 <br>
 * Editor : zhouyiqiong<br>
 * Editor Date : 2016年11月10日<br>
 */
public class PMS220OutputVO extends PagingOutputVO{
	private List<Map<String, Object>> resultList;
	private List<Map<String, Object>> csvList;
	private List<Map<String, Object>> verList;
	private List<String> roleList;
	private String personType;
	private List<Map<String, Object>> empIDList;
	private List<Map<String, String>> orgList;
	private List<Map<String, Object>> totalList;
	public List<Map<String, Object>> getResultList()
	{
		return resultList;
	}
	public void setResultList(List<Map<String, Object>> resultList)
	{
		this.resultList = resultList;
	}
	public List<Map<String, Object>> getCsvList()
	{
		return csvList;
	}
	public void setCsvList(List<Map<String, Object>> csvList)
	{
		this.csvList = csvList;
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
	public String getPersonType()
	{
		return personType;
	}
	public void setPersonType(String personType)
	{
		this.personType = personType;
	}
	public List<Map<String, Object>> getEmpIDList()
	{
		return empIDList;
	}
	public void setEmpIDList(List<Map<String, Object>> empIDList)
	{
		this.empIDList = empIDList;
	}
	public List<Map<String, String>> getOrgList()
	{
		return orgList;
	}
	public void setOrgList(List<Map<String, String>> orgList)
	{
		this.orgList = orgList;
	}
	public List<Map<String, Object>> getTotalList()
	{
		return totalList;
	}
	public void setTotalList(List<Map<String, Object>> totalList)
	{
		this.totalList = totalList;
	}
}

