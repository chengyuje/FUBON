package com.systex.jbranch.app.server.fps.crm122;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class CRM122InputVO extends PagingInputVO{
	private String seq; 
	private Date date;            					//選取時間
	private String EMP_ID;        					//理專ID
	private String AO_CODE;        					//AO_CODE
	private List<String> AOsList ;
	
	private String CUST_ID;
	private String CUST_NAME;
	private Date TASK_DATE;		  					//提醒日期
	private String TASK_TITLE;	  					//提醒名稱
	private String TASK_MEMO;	  					//提醒內容
	private String TASK_STIME;    					//提醒時間-起
	private String TASK_ETIME;	  					//提醒時間-迄
	private String TASK_SOURCE;
	private String TASK_STATUS;
	private String sHour;
	private String sMinute;
	private String eHour;
	private String eMinute;
	private String updateType;
	private String set;
	private List<String> taskDo;
	private List<String> taskAMC;
	private List<Map<String, Object>> chkCodedata; 
	private List<Map<String, Object>> chkCode_1data;
	
	
	public String getSet() {
		return set;
	}

	public void setSet(String set) {
		this.set = set;
	}

	public String getSeq() {
		return seq;
	}

	public void setSeq(String seq) {
		this.seq = seq;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getEMP_ID() {
		return EMP_ID;
	}

	public void setEMP_ID(String eMP_ID) {
		EMP_ID = eMP_ID;
	}

	public List<String> getAOsList() {
		return AOsList;
	}

	public void setAOsList(List<String> aOsList) {
		AOsList = aOsList;
	}

	public String getCUST_ID() {
		return CUST_ID;
	}

	public void setCUST_ID(String cUST_ID) {
		CUST_ID = cUST_ID;
	}

	public String getCUST_NAME() {
		return CUST_NAME;
	}

	public void setCUST_NAME(String cUST_NAME) {
		CUST_NAME = cUST_NAME;
	}

	public Date getTASK_DATE() {
		return TASK_DATE;
	}

	public void setTASK_DATE(Date tASK_DATE) {
		TASK_DATE = tASK_DATE;
	}

	public String getTASK_TITLE() {
		return TASK_TITLE;
	}

	public void setTASK_TITLE(String tASK_TITLE) {
		TASK_TITLE = tASK_TITLE;
	}

	public String getTASK_MEMO() {
		return TASK_MEMO;
	}

	public void setTASK_MEMO(String tASK_MEMO) {
		TASK_MEMO = tASK_MEMO;
	}

	public String getTASK_STIME() {
		return TASK_STIME;
	}

	public void setTASK_STIME(String tASK_STIME) {
		TASK_STIME = tASK_STIME;
	}

	public String getTASK_ETIME() {
		return TASK_ETIME;
	}

	public void setTASK_ETIME(String tASK_ETIME) {
		TASK_ETIME = tASK_ETIME;
	}

	public String getTASK_SOURCE() {
		return TASK_SOURCE;
	}

	public void setTASK_SOURCE(String tASK_SOURCE) {
		TASK_SOURCE = tASK_SOURCE;
	}

	public String getsHour() {
		return sHour;
	}

	public void setsHour(String sHour) {
		this.sHour = sHour;
	}

	public String getsMinute() {
		return sMinute;
	}

	public void setsMinute(String sMinute) {
		this.sMinute = sMinute;
	}

	public String geteHour() {
		return eHour;
	}

	public void seteHour(String eHour) {
		this.eHour = eHour;
	}

	public String geteMinute() {
		return eMinute;
	}

	public void seteMinute(String eMinute) {
		this.eMinute = eMinute;
	}

	public List<Map<String, Object>> getChkCodedata() {
		return chkCodedata;
	}

	public void setChkCodedata(List<Map<String, Object>> chkCodedata) {
		this.chkCodedata = chkCodedata;
	}

	public List<Map<String, Object>> getChkCode_1data() {
		return chkCode_1data;
	}

	public void setChkCode_1data(List<Map<String, Object>> chkCode_1data) {
		this.chkCode_1data = chkCode_1data;
	}

	public String getTASK_STATUS() {
		return TASK_STATUS;
	}

	public void setTASK_STATUS(String tASK_STATUS) {
		TASK_STATUS = tASK_STATUS;
	}

	public String getUpdateType() {
		return updateType;
	}

	public void setUpdateType(String updateType) {
		this.updateType = updateType;
	}

	public List<String> getTaskDo() {
		return taskDo;
	}

	public void setTaskDo(List<String> taskDo) {
		this.taskDo = taskDo;
	}

	public List<String> getTaskAMC() {
		return taskAMC;
	}

	public void setTaskAMC(List<String> taskAMC) {
		this.taskAMC = taskAMC;
	}

	public String getAO_CODE() {
		return AO_CODE;
	}

	public void setAO_CODE(String aO_CODE) {
		AO_CODE = aO_CODE;
	}	
	
}
