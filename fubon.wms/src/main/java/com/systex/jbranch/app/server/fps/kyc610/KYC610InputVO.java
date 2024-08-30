package com.systex.jbranch.app.server.fps.kyc610;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class KYC610InputVO extends PagingInputVO{
	
	private String CUST_ID;
	private String CUST_NAME;
	private String COMMENTARY_STAFF;//解說人員
	private boolean	QUEST_TYPE1;//1 = 金融(含債券)專案知識評估
	private boolean	QUEST_TYPE2;//2 = 結構型專案知識評估
	private String	QUEST_TYPE;//問卷種類
	private String EXAM_VERSION;
	private List<Map<String, Object>> quest_list;
	private String COMP_NAME;
	private String COMP_NBR;
	private String TIME_SEQ;
	private Map<String,Object> resultMap;
	
	public List<Map<String, Object>> getQuest_list() {
		return quest_list;
	}
	public void setQuest_list(List<Map<String, Object>> quest_list) {
		this.quest_list = quest_list;
	}
	public String getEXAM_VERSION() {
		return EXAM_VERSION;
	}
	public void setEXAM_VERSION(String eXAM_VERSION) {
		EXAM_VERSION = eXAM_VERSION;
	}

	public String getCUST_ID() {
		return CUST_ID.toUpperCase();
	}
	public void setCUST_ID(String cUST_ID) {
		CUST_ID = cUST_ID.toUpperCase();
	}
	public String getCOMMENTARY_STAFF() {
		return COMMENTARY_STAFF;
	}
	public void setCOMMENTARY_STAFF(String cOMMENTARY_STAFF) {
		COMMENTARY_STAFF = cOMMENTARY_STAFF;
	}
	public boolean isQUEST_TYPE1() {
		return QUEST_TYPE1;
	}
	public void setQUEST_TYPE1(boolean qUEST_TYPE1) {
		QUEST_TYPE1 = qUEST_TYPE1;
	}
	public boolean isQUEST_TYPE2() {
		return QUEST_TYPE2;
	}
	public void setQUEST_TYPE2(boolean qUEST_TYPE2) {
		QUEST_TYPE2 = qUEST_TYPE2;
	}
	public String getQUEST_TYPE() {
		return QUEST_TYPE;
	}
	public void setQUEST_TYPE(String qUEST_TYPE) {
		QUEST_TYPE = qUEST_TYPE;
	}
	public String getCUST_NAME() {
		return CUST_NAME;
	}
	public void setCUST_NAME(String cUST_NAME) {
		CUST_NAME = cUST_NAME;
	}
	public String getCOMP_NAME() {
		return COMP_NAME;
	}
	public void setCOMP_NAME(String cOMP_NAME) {
		COMP_NAME = cOMP_NAME;
	}
	public String getCOMP_NBR() {
		return COMP_NBR;
	}
	public void setCOMP_NBR(String cOMP_NBR) {
		COMP_NBR = cOMP_NBR;
	}
	public String getTIME_SEQ() {
		return TIME_SEQ;
	}
	public void setTIME_SEQ(String tIME_SEQ) {
		TIME_SEQ = tIME_SEQ;
	}
	public Map<String, Object> getResultMap() {
		return resultMap;
	}
	public void setResultMap(Map<String, Object> resultMap) {
		this.resultMap = resultMap;
	}
}