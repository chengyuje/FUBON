package com.systex.jbranch.app.server.fps.pms711;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

/**
 * 
 * Copy Right Information : Forms Syntron <br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.6.0 <br>
 * Description : <br>
 * Comments Name : PMS711InputVO.java<br>
 * Author :WKK<br>
 * Date :2016年12月12日 <br>
 * Version : 1.01 <br>
 * Editor : WKK<br>
 * Editor Date : 2016年12月12日<br>
 */
public class PMS711InputVO extends PagingInputVO
{
	private String userId;        //登陸者ID
	
	private String date_year;     //KPI年度
	private String yearMon;
	private String personType;    //人員類別
	private String SDate1;           //計績開始日期
	private String EDate1;           //計績開始日期
	private Date SDate;           //計績開始日期
	private Date EDate;            //計績結束日期
	private String fullScore;      //滿分
	private String scoreValue;      //小計分數
	private Date sDate;           //計績開始日期
	private Date eDate;            //計績結束日期
	private String selectFlag;     //勾選
	private String examStats;       //設定目標
	private String upRate;          //超額達成
	private String kpiScore;        //分數
	private String statRate;       //所有人員達成率暫以此計
	private String subProjectSeqId;       //點擊的子項目
	
	private String isSpecial;   //是否為特殊分行設定
	private String branchs;     //分行設定
	//子項目01判斷
	private String flag;        
	//子項目12,25
	private String tsType;
	
	//公用页面显示参数
	private Map<String,String> columnMap;
	private List<Map<String, Object>> showList;      
	private List<Map<String, Object>> statsList;
	private String fileName;// 上傳檔案名
	
	//新增KPI項目
	private String KPI_PROJECT;   //KPI項目
	private String SUB_PROJECT_SEQ_NAME;   //子項目名稱
	private List<Map<String, String>> addToList;
	private List<Map<String, String>> newCustRateList;
	
	private String checkflag;   //
	
	private String cflag;
	
	private String fusub1;

	
	
	
		
	public Map<String, String> getColumnMap()
	{
		return columnMap;
	}
	public void setColumnMap(Map<String, String> columnMap)
	{
		this.columnMap = columnMap;
	}
	public String getFusub1() {
		return fusub1;
	}
	public void setFusub1(String fusub1) {
		this.fusub1 = fusub1;
	}
	public String getCflag() {
		return cflag;
	}
	public void setCflag(String cflag) {
		this.cflag = cflag;
	}
	public String getCheckflag() {
		return checkflag;
	}
	public void setCheckflag(String checkflag) {
		this.checkflag = checkflag;
	}
	public String getTsType()
	{
		return tsType;
	}
	public void setTsType(String tsType)
	{
		this.tsType = tsType;
	}
	public String getFlag()
	{
		return flag;
	}
	public void setFlag(String flag)
	{
		this.flag = flag;
	}
	public List<Map<String, String>> getAddToList()
	{
		return addToList;
	}
	public void setAddToList(List<Map<String, String>> addToList)
	{
		this.addToList = addToList;
	}
	public String getKPI_PROJECT()
	{
		return KPI_PROJECT;
	}
	public void setKPI_PROJECT(String kPI_PROJECT)
	{
		KPI_PROJECT = kPI_PROJECT;
	}
	public String getSUB_PROJECT_SEQ_NAME()
	{
		return SUB_PROJECT_SEQ_NAME;
	}
	public void setSUB_PROJECT_SEQ_NAME(String sUB_PROJECT_SEQ_NAME)
	{
		SUB_PROJECT_SEQ_NAME = sUB_PROJECT_SEQ_NAME;
	}
	public String getFileName()
	{
		return fileName;
	}

	public void setFileName(String fileName)
	{
		this.fileName = fileName;
	}

	public String getDate_year()
	{
		return date_year;
	}

	public void setDate_year(String date_year)
	{
		this.date_year = date_year;
	}
	
	public String getYearMon()
	{
		return yearMon;
	}

	public void setYearMon(String yearMon)
	{
		this.yearMon = yearMon;
	}

	public List<Map<String, Object>> getShowList()
	{
		return showList;
	}
	
	public void setShowList(List<Map<String, Object>> showList)
	{
		this.showList = showList;
	}
	
	public Date getsDate()
	{
		return sDate;
	}

	public void setsDate(Date sDate)
	{
		this.sDate = sDate;
	}

	public Date geteDate()
	{
		return eDate;
	}

	public void seteDate(Date eDate)
	{
		this.eDate = eDate;
	}

	public String getPersonType()
	{
		return personType;
	}

	public void setPersonType(String personType)
	{
		this.personType = personType;
	}

	public List<Map<String, Object>> getStatsList()
	{
		return statsList;
	}

	public void setStatsList(List<Map<String, Object>> statsList)
	{
		this.statsList = statsList;
	}
	
	public String getSDate1()
	{
		return SDate1;
	}
	public void setSDate1(String sDate1)
	{
		SDate1 = sDate1;
	}
	public String getEDate1()
	{
		return EDate1;
	}
	public void setEDate1(String eDate1)
	{
		EDate1 = eDate1;
	}
	public Date getSDate()
	{
		return SDate;
	}
	public void setSDate(Date sDate)
	{
		SDate = sDate;
	}
	public Date getEDate()
	{
		return EDate;
	}
	public void setEDate(Date eDate)
	{
		EDate = eDate;
	}
	public String getFullScore()
	{
		return fullScore;
	}

	public void setFullScore(String fullScore)
	{
		this.fullScore = fullScore;
	}

	
	public String getScoreValue() {
		return scoreValue;
	}
	public void setScoreValue(String scoreValue) {
		this.scoreValue = scoreValue;
	}
	public String getSelectFlag()
	{
		return selectFlag;
	}

	public void setSelectFlag(String selectFlag)
	{
		this.selectFlag = selectFlag;
	}

	public String getExamStats()
	{
		return examStats;
	}

	public void setExamStats(String examStats)
	{
		this.examStats = examStats;
	}

	public String getUpRate()
	{
		return upRate;
	}

	public void setUpRate(String upRate)
	{
		this.upRate = upRate;
	}

	public String getKpiScore()
	{
		return kpiScore;
	}

	public void setKpiScore(String kpiScore)
	{
		this.kpiScore = kpiScore;
	}

	public String getStatRate()
	{
		return statRate;
	}

	public void setStatRate(String statRate)
	{
		this.statRate = statRate;
	}

	public String getUserId()
	{
		return userId;
	}

	public void setUserId(String userId)
	{
		this.userId = userId;
	}

	public String getSubProjectSeqId()
	{
		return subProjectSeqId;
	}

	public void setSubProjectSeqId(String subProjectSeqId)
	{
		this.subProjectSeqId = subProjectSeqId;
	}
	
	public String getIsSpecial() {
		return isSpecial;
	}
	
	public void setIsSpecial(String isSpecial) {
		this.isSpecial = isSpecial;
	}
	
	public String getBranchs() {
		return branchs;
	}
	
	public void setBranchs(String branchs) {
		this.branchs = branchs;
	}
	public List<Map<String, String>> getNewCustRateList()
	{
		return newCustRateList;
	}
	public void setNewCustRateList(List<Map<String, String>> newCustRateList)
	{
		this.newCustRateList = newCustRateList;
	}	
	
	
}
