package com.systex.jbranch.app.server.fps.pms714;
import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;
/**
 * 
 * Copy Right Information : Forms Syntron <br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.7.0 <br>
 * Description : 每月保險同意書補簽報表 OutputVO<br>
 * Comments Name : PMS714OutputVO.java<br>
 * Author :zhouyiqiong<br>
 * Date :2017年1月11日 <br>
 * Version : 1.01 <br>
 * Editor : zhouyiqiong<br>
 * Editor Date : 2017年1月11日<br>
 */
public class PMS714OutputVO extends PagingOutputVO{
	private  List<Map<String, Object>> resultList;  //結果集
	private  List<Map<String, Object>> csvList;
	private List<Map<String, String>> orgList;    
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
	public List<Map<String, String>> getOrgList()
	{
		return orgList;
	}
	public void setOrgList(List<Map<String, String>> orgList)
	{
		this.orgList = orgList;
	}
}
