package com.systex.jbranch.app.server.fps.sot650;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class SOT650InputVO extends PagingInputVO{
	private String CUST_ID;
	private String BATCH_SEQ;
	private String PROD_CURR;
	private String STATUS;
	private String MON_PERIOD;
	private String PROD_ID;
	private Date sDate;
	private Date eDate;
	private String EMP_ID;
	private List<Map<String, Object>> printList;
	
	public String getCUST_ID() {
		return CUST_ID;
	}
	public void setCUST_ID(String cUST_ID) {
		CUST_ID = cUST_ID;
	}
	public String getBATCH_SEQ() {
		return BATCH_SEQ;
	}
	public void setBATCH_SEQ(String bATCH_SEQ) {
		BATCH_SEQ = bATCH_SEQ;
	}
	public String getPROD_CURR() {
		return PROD_CURR;
	}
	public void setPROD_CURR(String pROD_CURR) {
		PROD_CURR = pROD_CURR;
	}
	public String getSTATUS() {
		return STATUS;
	}
	public void setSTATUS(String sTATUS) {
		STATUS = sTATUS;
	}
	public String getMON_PERIOD() {
		return MON_PERIOD;
	}
	public void setMON_PERIOD(String mON_PERIOD) {
		MON_PERIOD = mON_PERIOD;
	}
	public String getPROD_ID() {
		return PROD_ID;
	}
	public void setPROD_ID(String pROD_ID) {
		PROD_ID = pROD_ID;
	}
	public Date getsDate() {
		return sDate;
	}
	public void setsDate(Date sDate) {
		this.sDate = sDate;
	}
	public Date geteDate() {
		return eDate;
	}
	public void seteDate(Date eDate) {
		this.eDate = eDate;
	}
	public String getEMP_ID() {
		return EMP_ID;
	}
	public void setEMP_ID(String eMP_ID) {
		EMP_ID = eMP_ID;
	}
	public List<Map<String, Object>> getPrintList() {
		return printList;
	}
	public void setPrintList(List<Map<String, Object>> printList) {
		this.printList = printList;
	}
	
}
