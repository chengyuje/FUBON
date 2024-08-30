package com.systex.jbranch.fubon.commons.mplus;

import static com.systex.jbranch.fubon.commons.mplus.cons.MPlusCons.GET_AES_KEY_MEHOTD;

import com.systex.jbranch.fubon.commons.mplus.multipartEmpty.MultipartEntity;
import com.systex.jbranch.fubon.commons.mplus.multipartEmpty.SecretKeyType;

public class MPlusAlterEmployeeInputVO extends MPlusEmployeeInputVO{	
	/* add: 新增員工
	 * modify: 異動員工資料*/
	private String action;
	
	/* 員工編號
	 * 需加密 alterKey=2 時必填*/
	@MultipartEntity(secretKeyType = SecretKeyType.METHOD , secretKeyValue = GET_AES_KEY_MEHOTD)
	private String employeeNo;
	
	/* 電子郵件
	 * 需加密 alterKey=1 時必填 action=add時與(countryCode + msisdn)擇一必填*/
	@MultipartEntity(secretKeyType = SecretKeyType.METHOD , secretKeyValue = GET_AES_KEY_MEHOTD)
	private String email; 
	
	/* 國碼
	 * 以 "+" 開頭，例如 "+886"。需加密 註:action=add時與email擇一必填
	 * 註:需與 msisdn 同時存在*/
	//@MultipartEntity(secretKeyType = SecretKeyType.METHOD , secretKeyValue = AES_KEY)
	private String countryCode;
	
	 /* 門號
	  * 需加密 (09xxXxxXxx) 
	  * 註:action=add 時與 email 擇一必填
	  * 註:需與 countryCode 同時存在 */
	@MultipartEntity(secretKeyType = SecretKeyType.METHOD , secretKeyValue = GET_AES_KEY_MEHOTD)
	private String msisdn;
	
	/* 員工姓名
	 * 需加密 action=add時必填**/
	@MultipartEntity(secretKeyType = SecretKeyType.METHOD , secretKeyValue = GET_AES_KEY_MEHOTD)
	private String name; 
	
	/* 部門名稱
	 * 需加密*/
	@MultipartEntity(secretKeyType = SecretKeyType.METHOD , secretKeyValue = GET_AES_KEY_MEHOTD)
	private String departmentName;
	
	/* 公司通訊錄是否開放付費功能 
	 * 0: 關閉 
	 * 1: 開啟
	 *  2: 依企業帳號設定（預設值)*/
	private String showMsisdn;// = "2";
	
	/* MVPN 簡碼/總機#分機
	 * 需加密 註: 若為國外號碼, 需自行加國碼於前*/
	@MultipartEntity(secretKeyType = SecretKeyType.METHOD , secretKeyValue = GET_AES_KEY_MEHOTD)
	private String extension; 
	

	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public String getEmployeeNo() {
		return employeeNo;
	}
	public void setEmployeeNo(String employeeNo) {
		this.employeeNo = employeeNo;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getCountryCode() {
		return countryCode;
	}
	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}
	public String getMsisdn() {
		return msisdn;
	}
	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDepartmentName() {
		return departmentName;
	}
	public void setDepartmentName(String departmentName) {
		this.departmentName = departmentName;
	}
	public String getShowMsisdn() {
		return showMsisdn;
	}
	public void setShowMsisdn(String showMsisdn) {
		this.showMsisdn = showMsisdn;
	}
	public String getExtension() {
		return extension;
	}
	public void setExtension(String extension) {
		this.extension = extension;
	}
}
