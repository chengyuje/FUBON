package com.systex.jbranch.app.server.fps.pms715;
import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;
/**
 * 
 * Copy Right Information : Forms Syntron <br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.7.0 <br>
 * Description : 年度KPI成績查詢OutputVO<br>
 * Comments Name : PMS715OutputVO.java<br>
 * Author :zhouyiqiong<br>
 * Date :2017年2月6日 <br>
 * Version : 1.01 <br>
 * Editor : zhouyiqiong<br>
 * Editor Date : 2017年2月6日<br>
 */
public class PMS715OutputVO extends PagingOutputVO{
	private  List<Map<String, Object>> resultList;  //結果集
	private  List<Map<String, Object>> inquireRoleList;  //結果集
	private  List<String> showList;  //結果集
	private  List<Map<String, Object>> csvList;     
	private  List<String> roleList;  
	private  List<String> openFlagList;
	private  List<String> ofList;
	private List<Map<String, String>> orgList;
	private List<Map<String, Object>> totalList;
	//調度信息反饋
	private String errorMessage;   
	
	public List<String> getShowList()
	{
		return showList;
	}
	public void setShowList(List<String> showList)
	{
		this.showList = showList;
	}
	public List<Map<String, Object>> getInquireRoleList()
	{
		return inquireRoleList;
	}
	public void setInquireRoleList(List<Map<String, Object>> inquireRoleList)
	{
		this.inquireRoleList = inquireRoleList;
	}
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
	public List<Map<String, Object>> getTotalList()
	{
		return totalList;
	}
	public void setTotalList(List<Map<String, Object>> totalList)
	{
		this.totalList = totalList;
	}
	public List<String> getOfList()
	{
		return ofList;
	}
	public void setOfList(List<String> ofList)
	{
		this.ofList = ofList;
	}
	public List<String> getOpenFlagList() {
		return openFlagList;
	}
	public void setOpenFlagList(List<String> openFlagList) {
		this.openFlagList = openFlagList;
	}
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
}
