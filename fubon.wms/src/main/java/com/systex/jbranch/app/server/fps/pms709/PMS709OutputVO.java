package com.systex.jbranch.app.server.fps.pms709;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

/**
 * Copy Right Information : Forms Syntron <br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.7.0 <br>
 * Description : 新增/編輯FCH轉介客戶Controller<br>
 * Comments Name : PMS709.java<br>
 * Author : cty<br>
 * Date :2016年11月22日 <br>
 * Version : 1.0 <br>
 * Editor : cty<br>
 * Editor Date : 2016年11月22日<br>
 */
public class PMS709OutputVO extends PagingOutputVO
{
	private List<Map<String, Object>> largeAgrList; //查詢結果列表

	private List<Map<String, Object>> ResultList;   //返回結果集
	
	private List<Map<String, Object>> flagList;     //判斷該月份有無cust_id
	
	private String errorMessage;                   //上傳結果錯誤信息反饋
	
	private int flag;                              //操作結果判斷

	private String FCH_BRANCH_NBR;  // fch_分行
	
	private String FCH_AO_DATE;  // fch_分行
	
	private String FCH_EMP_ID;   // FCH_EMP_ID
	
	private String REF_EMP_ID;   // REF_EMP_ID

	public String getFCH_BRANCH_NBR() {
		return FCH_BRANCH_NBR;
	}

	public void setFCH_BRANCH_NBR(String fCH_BRANCH_NBR) {
		FCH_BRANCH_NBR = fCH_BRANCH_NBR;
	}

	public String getFCH_AO_DATE() {
		return FCH_AO_DATE;
	}

	public void setFCH_AO_DATE(String fCH_AO_DATE) {
		FCH_AO_DATE = fCH_AO_DATE;
	}

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

	public List<Map<String, Object>> getFlagList() {
		return flagList;
	}

	public void setFlagList(List<Map<String, Object>> flagList) {
		this.flagList = flagList;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public String getFCH_EMP_ID() {
		return FCH_EMP_ID;
	}

	public void setFCH_EMP_ID(String fCH_EMP_ID) {
		FCH_EMP_ID = fCH_EMP_ID;
	}

	public String getREF_EMP_ID() {
		return REF_EMP_ID;
	}

	public void setREF_EMP_ID(String rEF_EMP_ID) {
		REF_EMP_ID = rEF_EMP_ID;
	}
	
}
