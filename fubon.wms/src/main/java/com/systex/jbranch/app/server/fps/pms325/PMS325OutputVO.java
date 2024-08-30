package com.systex.jbranch.app.server.fps.pms325;
import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;
/**
 * 
 * Copy Right Information : Forms Syntron <br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.7.0 <br>
 * Description : 理專降級報表OutputVO<br>
 * Comments Name : PMS325OutputVO.java<br>
 * Author :zhouyiqiong<br>
 * Date :2016年11月12日 <br>
 * Version : 1.01 <br>
 * Editor : zhouyiqiong<br>
 * Editor Date : 2016年11月14日<br>
 */
public class PMS325OutputVO extends PagingOutputVO{
	private List<Map<String, Object>> resultList;
	private List<Map<String, Object>> csvList;
	private List<String> roleList;
	private List<Map<String, Object>> durExamList;
	private List<Map<String, Object>> durExeExamList;
	private List<Map<String, String>> orgList;
	public List<Map<String, Object>> getResultList()
	{
		return resultList;
	}
	public void setResultList(List<Map<String, Object>> resultList)
	{
		this.resultList = resultList;
	}
	public List<String> getRoleList()
	{
		return roleList;
	}
	public void setRoleList(List<String> roleList)
	{
		this.roleList = roleList;
	}
	public List<Map<String, Object>> getDurExamList()
	{
		return durExamList;
	}
	public void setDurExamList(List<Map<String, Object>> durExamList)
	{
		this.durExamList = durExamList;
	}
	public List<Map<String, String>> getOrgList()
	{
		return orgList;
	}
	public void setOrgList(List<Map<String, String>> orgList)
	{
		this.orgList = orgList;
	}
	public List<Map<String, Object>> getDurExeExamList() {
		return durExeExamList;
	}
	public void setDurExeExamList(List<Map<String, Object>> durExeExamList) {
		this.durExeExamList = durExeExamList;
	}
	public List<Map<String, Object>> getCsvList() {
		return csvList;
	}
	public void setCsvList(List<Map<String, Object>> csvList) {
		this.csvList = csvList;
	}
}
