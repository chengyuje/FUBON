package com.systex.jbranch.app.server.fps.iot120;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class IOT120OutputVO extends PagingOutputVO{
	
	private List<Map<String, Object>> DEPID;
	private List<Map<String, Object>> EMP_NAME;
	private List<Map<String, Object>> CUST_NAME;
	private List<Map<String, Object>> INSURED_NAME;
	private List<Map<String, Object>> COM_ADDRESS;
	private List<Map<String, Object>> INS_INFORMATION;
	private List<Map<String, Object>> REPRESETList;
	private List<Map<String, Object>> REFList;
	private List<Map<String, Object>> SPECIAL_CONList;
	private List<Map<String, Object>> INVESTList;
	private List<Map<String, Object>> PreMatchList;
	private List<Map<String, Object>> INS_RIDER_DTLList;
	private String INS_ID;
	private String INSKEY_NO;
	private String ErrorINS_ID;
	private String ErrorINS_RIDER;
	private String FILE_URL;
	private Map<String, Object> webserviceData;
	private Map<String, Object> webservicePdfData;
	private String errorMsg;
	private Map<String, Object> wsPayServData; //富壽繳款服務單API回傳資料
	
	//
	private List<Map<String, Object>> TMSList;
	
	public List<Map<String, Object>> getTMSList() {
		return TMSList;
	}

	public void setTMSList(List<Map<String, Object>> tMSList) {
		TMSList = tMSList;
	}

	public String getFILE_URL() {
		return FILE_URL;
	}

	public void setFILE_URL(String fILE_URL) {
		FILE_URL = fILE_URL;
	}

	public Map<String, Object> getWebservicePdfData() {
		return webservicePdfData;
	}

	public void setWebservicePdfData(Map<String, Object> webservicePdfData) {
		this.webservicePdfData = webservicePdfData;
	}

	public String getErrorINS_RIDER() {
		return ErrorINS_RIDER;
	}

	public void setErrorINS_RIDER(String errorINS_RIDER) {
		ErrorINS_RIDER = errorINS_RIDER;
	}

	public String getErrorINS_ID() {
		return ErrorINS_ID;
	}

	public void setErrorINS_ID(String errorINS_ID) {
		ErrorINS_ID = errorINS_ID;
	}

	public Map<String, Object> getWebserviceData() {
		return webserviceData;
	}

	public void setWebserviceData(Map<String, Object> webserviceData) {
		this.webserviceData = webserviceData;
	}

	public String getINSKEY_NO() {
		return INSKEY_NO;
	}

	public void setINSKEY_NO(String iNSKEY_NO) {
		INSKEY_NO = iNSKEY_NO;
	}

	public String getINS_ID() {
		return INS_ID;
	}

	public void setINS_ID(String iNS_ID) {
		INS_ID = iNS_ID;
	}

	public List<Map<String, Object>> getINS_RIDER_DTLList() {
		return INS_RIDER_DTLList;
	}

	public void setINS_RIDER_DTLList(List<Map<String, Object>> iNS_RIDER_DTLList) {
		INS_RIDER_DTLList = iNS_RIDER_DTLList;
	}

	public List<Map<String, Object>> getPreMatchList() {
		return PreMatchList;
	}

	public void setPreMatchList(List<Map<String, Object>> preMatchList) {
		PreMatchList = preMatchList;
	}

	public List<Map<String, Object>> getINVESTList() {
		return INVESTList;
	}

	public void setINVESTList(List<Map<String, Object>> iNVESTList) {
		INVESTList = iNVESTList;
	}

	public List<Map<String, Object>> getSPECIAL_CONList() {
		return SPECIAL_CONList;
	}

	public void setSPECIAL_CONList(List<Map<String, Object>> sPECIAL_CONList) {
		SPECIAL_CONList = sPECIAL_CONList;
	}

	public List<Map<String, Object>> getREFList() {
		return REFList;
	}

	public void setREFList(List<Map<String, Object>> rEFList) {
		REFList = rEFList;
	}

	public List<Map<String, Object>> getREPRESETList() {
		return REPRESETList;
	}

	public void setREPRESETList(List<Map<String, Object>> rEPRESETList) {
		REPRESETList = rEPRESETList;
	}

	public List<Map<String, Object>> getINSURED_NAME() {
		return INSURED_NAME;
	}

	public void setINSURED_NAME(List<Map<String, Object>> iNSURED_NAME) {
		INSURED_NAME = iNSURED_NAME;
	}

	public List<Map<String, Object>> getINS_INFORMATION() {
		return INS_INFORMATION;
	}

	public void setINS_INFORMATION(List<Map<String, Object>> iNS_INFORMATION) {
		INS_INFORMATION = iNS_INFORMATION;
	}

	public List<Map<String, Object>> getCUST_NAME() {
		return CUST_NAME;
	}

	public void setCUST_NAME(List<Map<String, Object>> cUST_NAME) {
		CUST_NAME = cUST_NAME;
	}

	public List<Map<String, Object>> getCOM_ADDRESS() {
		return COM_ADDRESS;
	}

	public void setCOM_ADDRESS(List<Map<String, Object>> cOM_ADDRESS) {
		COM_ADDRESS = cOM_ADDRESS;
	}

	public List<Map<String, Object>> getEMP_NAME() {
		return EMP_NAME;
	}

	public void setEMP_NAME(List<Map<String, Object>> eMP_NAME) {
		EMP_NAME = eMP_NAME;
	}

	public List<Map<String, Object>> getDEPID() {
		return DEPID;
	}

	public void setDEPID(List<Map<String, Object>> dEPID) {
		DEPID = dEPID;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	public Map<String, Object> getWsPayServData() {
		return wsPayServData;
	}

	public void setWsPayServData(Map<String, Object> wsPayServData) {
		this.wsPayServData = wsPayServData;
	}
}
