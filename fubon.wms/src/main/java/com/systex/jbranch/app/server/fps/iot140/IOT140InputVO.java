package com.systex.jbranch.app.server.fps.iot140;

import java.sql.Date;
import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class IOT140InputVO extends PagingInputVO {

	private String REG_TYPE;
	private Date KEYIN_DATE_F;
	private Date KEYIN_DATE_T;
	private String INCLUDED;
	private String INSURED_ID;
	private String CUST_ID;
	private String POLICY_NO1;
	private String POLICY_NO2;
	private String POLICY_NO3;
	private String INS_ID;
	private String INCLUDED_REJECT;
	private String OP_BATCH_OPRID;
	private String OP_BATCH_OPRNAME;
	private List<Map<String, Object>> IOT_MAINList;
	private Map<String, Object> IOT_MAIN;
	private String FB_COM_YN;
	
	
	
	public String getINCLUDED_REJECT() {
		return INCLUDED_REJECT;
	}

	public void setINCLUDED_REJECT(String iNCLUDED_REJECT) {
		INCLUDED_REJECT = iNCLUDED_REJECT;
	}

	public Map<String, Object> getIOT_MAIN() {
		return IOT_MAIN;
	}

	public void setIOT_MAIN(Map<String, Object> iOT_MAIN) {
		IOT_MAIN = iOT_MAIN;
	}

	public List<Map<String, Object>> getIOT_MAINList() {
		return IOT_MAINList;
	}

	public void setIOT_MAINList(List<Map<String, Object>> iOT_MAINList) {
		IOT_MAINList = iOT_MAINList;
	}

	public String getREG_TYPE() {
		return REG_TYPE;
	}

	public void setREG_TYPE(String rEG_TYPE) {
		REG_TYPE = rEG_TYPE;
	}

	public Date getKEYIN_DATE_F() {
		return KEYIN_DATE_F;
	}

	public void setKEYIN_DATE_F(Date kEYIN_DATE_F) {
		KEYIN_DATE_F = kEYIN_DATE_F;
	}

	public Date getKEYIN_DATE_T() {
		return KEYIN_DATE_T;
	}

	public void setKEYIN_DATE_T(Date kEYIN_DATE_T) {
		KEYIN_DATE_T = kEYIN_DATE_T;
	}

	public String getINCLUDED() {
		return INCLUDED;
	}

	public void setINCLUDED(String iNCLUDED) {
		INCLUDED = iNCLUDED;
	}

	public String getINSURED_ID() {
		return INSURED_ID;
	}

	public void setINSURED_ID(String iNSURED_ID) {
		INSURED_ID = iNSURED_ID;
	}

	public String getCUST_ID() {
		return CUST_ID;
	}

	public void setCUST_ID(String cUST_ID) {
		CUST_ID = cUST_ID;
	}

	public String getPOLICY_NO1() {
		return POLICY_NO1;
	}

	public void setPOLICY_NO1(String pOLICY_NO1) {
		POLICY_NO1 = pOLICY_NO1;
	}

	public String getPOLICY_NO2() {
		return POLICY_NO2;
	}

	public void setPOLICY_NO2(String pOLICY_NO2) {
		POLICY_NO2 = pOLICY_NO2;
	}

	public String getPOLICY_NO3() {
		return POLICY_NO3;
	}

	public void setPOLICY_NO3(String pOLICY_NO3) {
		POLICY_NO3 = pOLICY_NO3;
	}

	public String getINS_ID() {
		return INS_ID;
	}

	public void setINS_ID(String iNS_ID) {
		INS_ID = iNS_ID;
	}

	public String getOP_BATCH_OPRID() {
		return OP_BATCH_OPRID;
	}

	public void setOP_BATCH_OPRID(String oP_BATCH_OPRID) {
		OP_BATCH_OPRID = oP_BATCH_OPRID;
	}

	public String getOP_BATCH_OPRNAME() {
		return OP_BATCH_OPRNAME;
	}

	public void setOP_BATCH_OPRNAME(String oP_BATCH_OPRNAME) {
		OP_BATCH_OPRNAME = oP_BATCH_OPRNAME;
	}

	public String getFB_COM_YN() {
		return FB_COM_YN;
	}

	public void setFB_COM_YN(String fB_COM_YN) {
		FB_COM_YN = fB_COM_YN;
	}

}
