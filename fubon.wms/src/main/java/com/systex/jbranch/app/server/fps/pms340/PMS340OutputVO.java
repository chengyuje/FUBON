package com.systex.jbranch.app.server.fps.pms340;
import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;
/**
 * 
 * Copy Right Information : Forms Syntron <br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.7.0 <br>
 * Description : <br>
 * Comments Name : PMS340OutputVO.java<br>
 * Author :zhouyiqiong<br>
 * Date :2016年11月11日 <br>
 * Version : 1.01 <br>
 * Editor : zhouyiqiong<br>
 * Editor Date : 2016年11月11日<br>
 */
public class PMS340OutputVO extends PagingOutputVO{
	private  List<Map<String, Object>> resultList;
	private  List<Map<String, Object>> list;
	private  List<Map<String, Object>> csvList;
	//以下權限用flag
	private String flag;
	//新增腳色判斷
	private List<Map<String, Object>> role;
	private String errorMessage;
	private int idCat;
	private String sTime;
	
	public List<Map<String, Object>> getResultList()
	{
		return resultList;
	}
	public void setResultList(List<Map<String, Object>> resultList)
	{
		this.resultList = resultList;
	}
	public List<Map<String, Object>> getList()
	{
		return list;
	}
	public void setList(List<Map<String, Object>> list)
	{
		this.list = list;
	}
	public List<Map<String, Object>> getCsvList()
	{
		return csvList;
	}
	public void setCsvList(List<Map<String, Object>> csvList)
	{
		this.csvList = csvList;
	}
	public int getIdCat()
	{
		return idCat;
	}
	public void setIdCat(int idCat)
	{
		this.idCat = idCat;
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
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	public String getsTime() {
		return sTime;
	}
	public void setsTime(String sTime) {
		this.sTime = sTime;
	}
	
}