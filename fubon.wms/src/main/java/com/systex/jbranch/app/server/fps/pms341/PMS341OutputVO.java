package com.systex.jbranch.app.server.fps.pms341;
import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;
/**
 * 
 * Copy Right Information : Forms Syntron <br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.7.0 <br>
 * Description : 房貸火地險佣金明細Output<br>
 * Comments Name : PMS341OutputVO.java<br>
 * Author :zhouyiqiong<br>
 * Date :2016年11月16日 <br>
 * Version : 1.01 <br>
 * Editor : zhouyiqiong<br>
 * Editor Date : 2016年11月16日<br>
 */
public class PMS341OutputVO extends PagingOutputVO{
	private List<Map<String, Object>> resultList;
	private List<Map<String, Object>> csvList;
	private List<Map<String, Object>> empIDList;
	private List<Map<String, String>> orgList;
	private String type;
	private int flag;
	private String errorMessage;                   //上傳結果錯誤信息反饋
	public List<Map<String, Object>> getCsvList()
	{
		return csvList;
	}
	public void setCsvList(List<Map<String, Object>> csvList)
	{
		this.csvList = csvList;
	}
	public List<Map<String, Object>> getEmpIDList()
	{
		return empIDList;
	}
	public void setEmpIDList(List<Map<String, Object>> empIDList)
	{
		this.empIDList = empIDList;
	}
	public int getFlag()
	{
		return flag;
	}
	public void setFlag(int flag)
	{
		this.flag = flag;
	}
	public List<Map<String, Object>> getResultList()
	{
		return resultList;
	}
	public void setResultList(List<Map<String, Object>> resultList)
	{
		this.resultList = resultList;
	}
	public String getType()
	{
		return type;
	}
	public void setType(String type)
	{
		this.type = type;
	}
	public String getErrorMessage()
	{
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage)
	{
		this.errorMessage = errorMessage;
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