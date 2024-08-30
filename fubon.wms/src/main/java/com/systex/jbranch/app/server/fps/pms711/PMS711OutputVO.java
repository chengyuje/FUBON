package com.systex.jbranch.app.server.fps.pms711;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

/**
 * 
 * Copy Right Information : Forms Syntron <br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.6.0 <br>
 * Description : <br>
 * Comments Name : PMS711OutputVO.java<br>
 * Author :WKK<br>
 * Date :2016年12月12日 <br>
 * Version : 1.01 <br>
 * Editor : WKK<br>
 * Editor Date : 2016年12月12日<br>
 */
public class PMS711OutputVO extends PagingOutputVO
{
	//页面显示List
	private List<Map<String, Object>> showList;
	private List<Map<String, Object>> statsList;
	private List<Map<String, Object>> kpiDayList;
	private List<Map<String, Object>> addToList;
	private List<Map<String, Object>> personTypeList;
	private String errorMessage;
	private List<Map<String, Object>> showList1;
	

	
	
	public List<Map<String, Object>> getShowList1() {
		return showList1;
	}
	public void setShowList1(List<Map<String, Object>> showList1) {
		this.showList1 = showList1;
	}
	public List<Map<String, Object>> getAddToList()
	{
		return addToList;
	}
	public void setAddToList(List<Map<String, Object>> addToList)
	{
		this.addToList = addToList;
	}
	public String getErrorMessage()
	{
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage)
	{
		this.errorMessage = errorMessage;
	}
	//save時返回值
	private int saveList;
	
	private int flag;        //上傳結果判斷
	
	public int getFlag()
	{
		return flag;
	}
	public void setFlag(int flag)
	{
		this.flag = flag;
	}
	public List<Map<String, Object>> getShowList()
	{
		return showList;
	}
	public void setShowList(List<Map<String, Object>> showList)
	{
		this.showList = showList;
	}
	public int getSaveList()
	{
		return saveList;
	}
	public void setSaveList(int saveList)
	{
		this.saveList = saveList;
	}
	public List<Map<String, Object>> getStatsList()
	{
		return statsList;
	}
	public void setStatsList(List<Map<String, Object>> statsList)
	{
		this.statsList = statsList;
	}
	public List<Map<String, Object>> getKpiDayList()
	{
		return kpiDayList;
	}
	public void setKpiDayList(List<Map<String, Object>> kpiDayList)
	{
		this.kpiDayList = kpiDayList;
	}
	public List<Map<String, Object>> getPersonTypeList() {
		return personTypeList;
	}
	public void setPersonTypeList(List<Map<String, Object>> personTypeList) {
		this.personTypeList = personTypeList;
	}
}
