package com.systex.jbranch.app.server.fps.pms709;

import java.io.File;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

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
public class PMS709InputVO extends PagingInputVO
{
	private String yearMon;       // 轉介年月
	
	private String cust_ID;       // 客戶ID
	

	private String fch_AO_CODE;   // FCH_AO CODE
	
	private String fch_EMP_name;    // FCH專員姓名
	
	private String ref_BRANCH_NO; // 客戶轉介後分行代碼
	
	private String ref_AO_CODE;   // 轉介後AO CODE
	
	private String ref_EMP_NAME;  // 轉介後理專姓名
	
	private String userId;        // 用戶名
	
	private String FCH_BRANCH_NBR;  // fch_分行
	
	private String FCH_AO_DATE;  // fch_分行
	
	private String FCH_EMP_ID;   // FCH_EMP_ID
	
	private String REF_EMP_ID;   // REF_EMP_ID

	public String getYearMon() {
		return yearMon;
	}

	public void setYearMon(String yearMon) {
		this.yearMon = yearMon;
	}

	public String getCust_ID() {
		return cust_ID;
	}

	public void setCust_ID(String cust_ID) {
		this.cust_ID = cust_ID;
	}

	public String getFch_AO_CODE() {
		return fch_AO_CODE;
	}

	public void setFch_AO_CODE(String fch_AO_CODE) {
		this.fch_AO_CODE = fch_AO_CODE;
	}

	public String getFch_EMP_name()
	{
		return fch_EMP_name;
	}

	public void setFch_EMP_name(String fch_EMP_name)
	{
		this.fch_EMP_name = fch_EMP_name;
	}

	public String getRef_BRANCH_NO() {
		return ref_BRANCH_NO;
	}

	public void setRef_BRANCH_NO(String ref_BRANCH_NO) {
		this.ref_BRANCH_NO = ref_BRANCH_NO;
	}

	public String getRef_AO_CODE() {
		return ref_AO_CODE;
	}

	public void setRef_AO_CODE(String ref_AO_CODE) {
		this.ref_AO_CODE = ref_AO_CODE;
	}

	public String getRef_EMP_NAME() {
		return ref_EMP_NAME;
	}

	public void setRef_EMP_NAME(String ref_EMP_NAME) {
		this.ref_EMP_NAME = ref_EMP_NAME;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

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
