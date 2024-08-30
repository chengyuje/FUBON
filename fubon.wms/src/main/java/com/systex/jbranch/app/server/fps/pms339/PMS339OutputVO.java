package com.systex.jbranch.app.server.fps.pms339;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;
/**
 * 
 * Copy Right Information : Forms Syntron <br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.7.0 <br>
 * Description : 房貸壽險獎勵金報表OutputVO<br>
 * Comments Name : PMS339OutputVO.java<br>
 * Author :zhouyiqiong<br>
 * Date :2016年11月22日 <br>
 * Version : 1.01 <br>
 * Editor : zhouyiqiong<br>
 * Editor Date : 2016年11月22日<br>
 */
public class PMS339OutputVO extends PagingOutputVO{
	private List<Map<String, Object>> resultList;   //分頁結果
	private List<Map<String, Object>> csvList;      //全部結果
	private List<Map<String, String>> orgList;
	//新增腳色判斷
	private List<Map<String, Object>> role;
	//以下權限用flag
	private String flag;
	public List<Map<String, Object>> getCsvList()
	{
		return csvList;
	}
	public void setCsvList(List<Map<String, Object>> csvList)
	{
		this.csvList = csvList;
	}
	public List<Map<String, Object>> getResultList()
	{
		return resultList;
	}
	public void setResultList(List<Map<String, Object>> resultList)
	{
		this.resultList = resultList;
	}
	public List<Map<String, String>> getOrgList()
	{
		return orgList;
	}
	public void setOrgList(List<Map<String, String>> orgList)
	{
		this.orgList = orgList;
	}
	public List<Map<String, Object>> getRole() {
		return role;
	}
	public void setRole(List<Map<String, Object>> role) {
		this.role = role;
	}
	public String getFlag() {
		return flag;
	}
	public void setFlag(String flag) {
		this.flag = flag;
	}
}
