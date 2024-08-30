package com.systex.jbranch.app.server.fps.pms716;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

/**
 * 
 * Copy Right Information : Forms Syntron <br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.7.0 <br>
 * Description : <br>
 * Comments Name : pms716OutputVO.java<br>
 * Author :CTY<br>
 * Date :2016年12月22日 <br>
 * Version : 1.01 <br>
 * Editor : CTY<br>
 * Editor Date : 2016年11月14日<br>
 */
public class PMS716OutputVO extends PagingOutputVO
{
	//操作結果標誌位
	private int flag;
	
	//主表查詢結果返回值
	private List<Map<String, Object>> resultList;
	//分行代碼，分行別
	private List<Map<String, Object>> branchList;
	//上傳結果錯誤信息反饋
	private String errorMessage;   
	
	//撥款額度定額獎金表參數
	private List<Map<String, Object>> paramList1;
	private List<Map<String, Object>> paramList2;
	private List<Map<String, Object>> paramList3;

	public List<Map<String, Object>> getParamList1()
	{
		return paramList1;
	}

	public void setParamList1(List<Map<String, Object>> paramList1)
	{
		this.paramList1 = paramList1;
	}

	public List<Map<String, Object>> getParamList2()
	{
		return paramList2;
	}

	public void setParamList2(List<Map<String, Object>> paramList2)
	{
		this.paramList2 = paramList2;
	}

	public List<Map<String, Object>> getParamList3()
	{
		return paramList3;
	}

	public void setParamList3(List<Map<String, Object>> paramList3)
	{
		this.paramList3 = paramList3;
	}

	public int getFlag()
	{
		return flag;
	}

	public void setFlag(int flag)
	{
		this.flag = flag;
	}

	public List<Map<String, Object>> getBranchList()
	{
		return branchList;
	}

	public void setBranchList(List<Map<String, Object>> branchList)
	{
		this.branchList = branchList;
	}

	public List<Map<String, Object>> getResultList()
	{
		return resultList;
	}

	public void setResultList(List<Map<String, Object>> resultList)
	{
		this.resultList = resultList;
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