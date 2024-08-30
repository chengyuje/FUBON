package com.systex.jbranch.app.server.fps.pms214;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

/**
 * Copy Right Information : Forms Syntron <br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.7.0 <br>
 * Description :調整客戶收益(整批)Controller<br>
 * Comments Name : PMS214.java<br>
 * Author : cty<br>
 * Date :2016年11月23日 <br>
 * Version : 1.0 <br>
 * Editor : cty<br>
 * Editor Date : 2016年11月23日<br>
 */
public class PMS214OutputVO extends PagingOutputVO
{
	private List<Map<String, Object>> outputLargeAgrList;  //查詢結果列表
	
	private int flag;                              //操作結果判斷
	
	private String errorMessage;                   //上傳結果錯誤信息反饋
	private List<Map<String, Object>> csvList;      //查詢結果匯出csv列表
	
	public List<Map<String, Object>> getOutputLargeAgrList()
	{
		return outputLargeAgrList;
	}

	public void setOutputLargeAgrList(List<Map<String, Object>> outputLargeAgrList)
	{
		this.outputLargeAgrList = outputLargeAgrList;
	}


	public int getFlag() {
		return flag;
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}

	public String getErrorMessage()
	{
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage)
	{
		this.errorMessage = errorMessage;
	}

	public List<Map<String, Object>> getCsvList() {
		return csvList;
	}

	public void setCsvList(List<Map<String, Object>> csvList) {
		this.csvList = csvList;
	}
	
	
}
