package com.systex.jbranch.app.server.fps.pms306;

import java.util.List;
import java.util.Map;
import java.util.Date;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;
/**
 * 
 * Copy Right Information :  <br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.7.0 <br>
 * Description : 保險速報作業InputVO<br>
 * Comments Name : PMS306InputVO.java<br>
 * Author :Kevin<br>
 * Date :2016年08月09日 <br>
 * Version : 1.01 <br>
 * Editor : Kevin<br>
 * Editor Date : 2017年01月31日<br>
 */
public class PMS306InputVO extends PagingInputVO {

	private String NOTE; // note使用的inputVO
	private String YEARMON; // 年月查詢條件
	private List TIMELISTL; // 傳來的INPUT時間LIST
	private Date UpDate;
	private Date SDate; // 批次起始時間
	private Date EDate; // 批次結束時間
	private String sTime; // 年月--月目標
	private String state;// 狀態
	private String YEARS; // 年月--
	private String FILE_NAME; // 檔名
	private String ACTUAL_FILE_NAME; // 真實檔名
	private String INSFNO; //保險文件編號
	private String BRANCH_NBR;
	private String INS_ID;
	private String sttType;
	private String DISCOUNTSALE;
	private String DISCOUNT;
	private String PARAM_ORDER;
	private String NBR_state;
	
	private String recruitId;
	private String keyinDate;
	
	

	public String getNBR_state() {
		return NBR_state;
	}

	public void setNBR_state(String nBR_state) {
		NBR_state = nBR_state;
	}

	public Date getUpDate() {
		return UpDate;
	}

	public void setUpDate(Date upDate) {
		UpDate = upDate;
	}

	public String getPARAM_ORDER() {
		return PARAM_ORDER;
	}

	public void setPARAM_ORDER(String pARAM_ORDER) {
		PARAM_ORDER = pARAM_ORDER;
	}

	public String getDISCOUNTSALE() {
		return DISCOUNTSALE;
	}

	public void setDISCOUNTSALE(String dISCOUNTSALE) {
		DISCOUNTSALE = dISCOUNTSALE;
	}

	public String getDISCOUNT() {
		return DISCOUNT;
	}

	public void setDISCOUNT(String dISCOUNT) {
		DISCOUNT = dISCOUNT;
	}

	public String getSttType() {
		return sttType;
	}

	public void setSttType(String sttType) {
		this.sttType = sttType;
	}

	public String getINS_ID() {
		return INS_ID;
	}

	public void setINS_ID(String iNS_ID) {
		INS_ID = iNS_ID;
	}

	public String getBRANCH_NBR() {
		return BRANCH_NBR;
	}

	public void setBRANCH_NBR(String bRANCH_NBR) {
		BRANCH_NBR = bRANCH_NBR;
	}

	private List<Map<String, Object>> list;
	private List<Map<String, Object>> list2;
	private Object listper;

	public String getNOTE() {
		return NOTE;
	}

	public Object getListper() {
		return listper;
	}

	public void setListper(Object listper) {
		this.listper = listper;
	}

	public Date getSDate() {
		return SDate;
	}

	public Date getEDate() {
		return EDate;
	}

	public void setSDate(Date sDate) {
		SDate = sDate;
	}

	public void setEDate(Date eDate) {
		EDate = eDate;
	}

	public void setNOTE(String nOTE) {
		NOTE = nOTE;
	}

	public String getYEARMON() {
		return YEARMON;
	}

	public void setYEARMON(String yEARMON) {
		YEARMON = yEARMON;
	}

	public List getTIMELISTL() {
		return TIMELISTL;
	}

	public void setTIMELISTL(List tIMELISTL) {
		TIMELISTL = tIMELISTL;
	}

	public String getsTime() {
		return sTime;
	}

	public void setsTime(String sTime) {
		this.sTime = sTime;
	}

	public String getFILE_NAME() {
		return FILE_NAME;
	}

	public String getACTUAL_FILE_NAME() {
		return ACTUAL_FILE_NAME;
	}

	public void setFILE_NAME(String fILE_NAME) {
		FILE_NAME = fILE_NAME;
	}

	public void setACTUAL_FILE_NAME(String aCTUAL_FILE_NAME) {
		ACTUAL_FILE_NAME = aCTUAL_FILE_NAME;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getYEARS() {
		return YEARS;
	}

	public List<Map<String, Object>> getList() {
		return list;
	}

	public List<Map<String, Object>> getList2() {
		return list2;
	}

	public void setList(List<Map<String, Object>> list) {
		this.list = list;
	}

	public void setList2(List<Map<String, Object>> list2) {
		this.list2 = list2;
	}

	public void setYEARS(String yEARS) {
		YEARS = yEARS;
	}

	public String getINSFNO() {
		return INSFNO;
	}

	public void setINSFNO(String iNSFNO) {
		INSFNO = iNSFNO;
	}

	public String getRecruitId() {
		return recruitId;
	}

	public void setRecruitId(String recruitId) {
		this.recruitId = recruitId;
	}

	public String getKeyinDate() {
		return keyinDate;
	}

	public void setKeyinDate(String keyinDate) {
		this.keyinDate = keyinDate;
	}

}
