package com.systex.jbranch.app.server.fps.sot701;

import java.util.Date;

/***
 * 高資產註記資料 
 * 電文：_060423_061432
 * 		取Memo1欄位
 * @author 1800036
 *
 */
public class CustHighNetWorthDataVO {
    private Date dueDate;    	//高資產註記到期日
    private Date invalidDate;	//高資產註記註銷日
    private String validHnwcYN; //是否為有效高資產客戶(未過期、未註銷) Y:有效 N:無效
    private String hnwcService;	//可提供高資產商品或服務
    private String spFlag; 		//是否為高資產特定客戶註記 (1)年齡70歲(含)以上。(2)教育程度為國中(含)以下。(3)有全民健康保險重大傷病證明。
    
	public Date getDueDate() {
		return dueDate;
	}
	
	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}
	
	public Date getInvalidDate() {
		return invalidDate;
	}
	
	public void setInvalidDate(Date invalidDate) {
		this.invalidDate = invalidDate;
	}

	public String getValidHnwcYN() {
		return validHnwcYN;
	}

	public void setValidHnwcYN(String validHnwcYN) {
		this.validHnwcYN = validHnwcYN;
	}

	public String getHnwcService() {
		return hnwcService;
	}

	public void setHnwcService(String hnwcService) {
		this.hnwcService = hnwcService;
	}

	public String getSpFlag() {
		return spFlag;
	}

	public void setSpFlag(String spFlag) {
		this.spFlag = spFlag;
	}
	
}
