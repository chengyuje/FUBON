package com.systex.jbranch.app.server.fps.crm121;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class CRM121InputVO extends PagingInputVO{
	private Date date;            //選取時間
	private String seq;
	private Date TASK_DATE;       //提醒日期
	private String TASK_TITLE;	  //提醒名稱
	private String TASK_MEMO;	  //提醒內容
	private String TASK_STIME;    //提醒時間-起
	private String TASK_ETIME;	  //提醒時間-迄
	private String TASK_SOURCE;
	private String TASK_STATUS;
	private String sHour;
	private String sMinute;
	private String eHour;
	private String eMinute;
	

	private String CUST_ID;       //客戶ID
	private String CUST_NAME;	  //客戶名稱
	private String EST_PRD;       //預計承作商品
	private String EST_AMT;		  //預計承作金額
	private String EST_EARNINGS;  //預估收益
	private Date ACTION_DATE;     //A日期
	private Date MEETING_DATE_S;  //M日期約訪時間起
	private Date MEETING_DATE_E;  //M日期約訪時間迄
	private Date CLOSE_DATE;      //C日期預計時間
	private List<Map<String, Object>> chkCodedata; 
	private List<Map<String, Object>> chkCode_1data;
	private List<Map<String, Object>> chkCode_2data;
	private List<String> taskDo;
	private List<String> taskAMC;
	private List<Map<String, Object>> data;
	private List<Map<String, Object>> data_1;
	private String delAMCLst;
	private String[] AMCcustIDLst;
	private String updateType;
	private String privilege;
	private String querytype;
	
	private Date startDate;
	private Date endDate;
	
	
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

	public Date getTASK_DATE() {
		return TASK_DATE;
	}

	public void setTASK_DATE(Date tASK_DATE) {
		TASK_DATE = tASK_DATE;
	}

	public String getTASK_STATUS() {
		return TASK_STATUS;
	}

	public void setTASK_STATUS(String tASK_STATUS) {
		TASK_STATUS = tASK_STATUS;
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

	public String getEST_PRD() {
		return EST_PRD;
	}

	public void setEST_PRD(String eST_PRD) {
		EST_PRD = eST_PRD;
	}

	public String getEST_AMT() {
		return EST_AMT;
	}

	public void setEST_AMT(String eST_AMT) {
		EST_AMT = eST_AMT;
	}

	public String getEST_EARNINGS() {
		return EST_EARNINGS;
	}

	public void setEST_EARNINGS(String eST_EARNINGS) {
		EST_EARNINGS = eST_EARNINGS;
	}

	public Date getACTION_DATE() {
		return ACTION_DATE;
	}

	public void setACTION_DATE(Date aCTION_DATE) {
		ACTION_DATE = aCTION_DATE;
	}

	public Date getMEETING_DATE_S() {
		return MEETING_DATE_S;
	}

	public void setMEETING_DATE_S(Date mEETING_DATE_S) {
		MEETING_DATE_S = mEETING_DATE_S;
	}

	public Date getMEETING_DATE_E() {
		return MEETING_DATE_E;
	}

	public void setMEETING_DATE_E(Date mEETING_DATE_E) {
		MEETING_DATE_E = mEETING_DATE_E;
	}

	public Date getCLOSE_DATE() {
		return CLOSE_DATE;
	}

	public void setCLOSE_DATE(Date cLOSE_DATE) {
		CLOSE_DATE = cLOSE_DATE;
	}



	public List<Map<String, Object>> getData() {
		return data;
	}

	public void setData(List<Map<String, Object>> data) {
		this.data = data;
	}

	public List<Map<String, Object>> getData_1() {
		return data_1;
	}

	public void setData_1(List<Map<String, Object>> data_1) {
		this.data_1 = data_1;
	}

	public String getDelAMCLst() {
		return delAMCLst;
	}

	public void setDelAMCLst(String delAMCLst) {
		this.delAMCLst = delAMCLst;
	}

	public String[] getAMCcustIDLst() {
		return AMCcustIDLst;
	}

	public void setAMCcustIDLst(String[] aMCcustIDLst) {
		AMCcustIDLst = aMCcustIDLst;
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

	public String getPrivilege() {
		return privilege;
	}

	public void setPrivilege(String privilege) {
		this.privilege = privilege;
	}

	public String getQuerytype() {
		return querytype;
	}

	public void setQuerytype(String querytype) {
		this.querytype = querytype;
	}

	public List<Map<String, Object>> getChkCode_2data() {
		return chkCode_2data;
	}

	public void setChkCode_2data(List<Map<String, Object>> chkCode_2data) {
		this.chkCode_2data = chkCode_2data;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}


	

}
