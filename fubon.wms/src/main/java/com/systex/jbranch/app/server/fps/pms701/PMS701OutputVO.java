package com.systex.jbranch.app.server.fps.pms701;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

/**
 * Copy Right Information : Forms Syntron <br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.7.0 <br>
 * Description :調整客戶收益(整批)Controller<br>
 * Comments Name : PMS701.java<br>
 * Author : cty<br>
 * Date :2016年11月23日 <br>
 * Version : 1.0 <br>
 * Editor : cty<br>
 * Editor Date : 2016年11月23日<br>
 */
public class PMS701OutputVO extends PagingOutputVO
{
	private List<Map<String, Object>> largeAgrList;  //查詢結果列表

	private List<Map<String, Object>> ResultList;   //返回結果集
	
	private int flag;                              //操作結果判斷
	
	private String errorMessage;                   //上傳結果錯誤信息反饋
	
	public List<Map<String, Object>> getLargeAgrList()
	{
		return largeAgrList;
	}

	public void setLargeAgrList(List<Map<String, Object>> largeAgrList)
	{
		this.largeAgrList = largeAgrList;
	}
	
	public List<Map<String, Object>> getResultList() {
		return ResultList;
	}

	public void setResultList(List<Map<String, Object>> resultList) {
		ResultList = resultList;
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
}
