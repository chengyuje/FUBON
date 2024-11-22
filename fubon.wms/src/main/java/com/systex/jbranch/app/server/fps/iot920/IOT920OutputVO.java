package com.systex.jbranch.app.server.fps.iot920;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.systex.jbranch.app.server.fps.sot701.CustHighNetWorthDataVO;
import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class IOT920OutputVO extends PagingOutputVO{
	
	private List<Map<String, Object>> next_statusList;
	private List<Map<String, Object>> EMP_NAME;
	private List<Map<String, Object>> CUST_NAME;
	private List<Map<String, Object>> INSURED_NAME;
	private List<Map<String, Object>> REPRESETList;
	private List<Map<String, Object>> COM_ADDRESS;
	private List<Map<String, Object>> REFList;
	private List<Map<String, Object>> PAYERList;
	private String AML;
	private String PRECHECK;
	private BigDecimal INCOME3;
	private String Message;
	private CustHighNetWorthDataVO hnwcData;
	private BigDecimal INS_ASSET;

	
	public List<Map<String, Object>> getEMP_NAME() {
		return EMP_NAME;
	}

	public void setEMP_NAME(List<Map<String, Object>> eMP_NAME) {
		EMP_NAME = eMP_NAME;
	}

	public List<Map<String, Object>> getCUST_NAME() {
		return CUST_NAME;
	}

	public void setCUST_NAME(List<Map<String, Object>> cUST_NAME) {
		CUST_NAME = cUST_NAME;
	}

	public List<Map<String, Object>> getINSURED_NAME() {
		return INSURED_NAME;
	}

	public void setINSURED_NAME(List<Map<String, Object>> iNSURED_NAME) {
		INSURED_NAME = iNSURED_NAME;
	}

	public List<Map<String, Object>> getREPRESETList() {
		return REPRESETList;
	}

	public void setREPRESETList(List<Map<String, Object>> rEPRESETList) {
		REPRESETList = rEPRESETList;
	}

	public List<Map<String, Object>> getCOM_ADDRESS() {
		return COM_ADDRESS;
	}

	public void setCOM_ADDRESS(List<Map<String, Object>> cOM_ADDRESS) {
		COM_ADDRESS = cOM_ADDRESS;
	}

	public List<Map<String, Object>> getREFList() {
		return REFList;
	}

	public void setREFList(List<Map<String, Object>> rEFList) {
		REFList = rEFList;
	}

	public List<Map<String, Object>> getNext_statusList() {
		return next_statusList;
	}

	public void setNext_statusList(List<Map<String, Object>> next_statusList) {
		this.next_statusList = next_statusList;
	}

	public List<Map<String, Object>> getPAYERList() {
		return PAYERList;
	}

	public void setPAYERList(List<Map<String, Object>> pAYERList) {
		PAYERList = pAYERList;
	}

	public String getAML() {
		return AML;
	}

	public void setAML(String aML) {
		AML = aML;
	}

	public String getPRECHECK() {
		return PRECHECK;
	}

	public void setPRECHECK(String pRECHECK) {
		PRECHECK = pRECHECK;
	}

	public BigDecimal getINCOME3() {
		return INCOME3;
	}

	public void setINCOME3(BigDecimal iNCOME3) {
		INCOME3 = iNCOME3;
	}

	public String getMessage() {
		return Message;
	}

	public void setMessage(String message) {
		Message = message;
	}

	public CustHighNetWorthDataVO getHnwcData() {
		return hnwcData;
	}

	public void setHnwcData(CustHighNetWorthDataVO hnwcData) {
		this.hnwcData = hnwcData;
	}

	public BigDecimal getINS_ASSET() {
		return INS_ASSET;
	}

	public void setINS_ASSET(BigDecimal iNS_ASSET) {
		INS_ASSET = iNS_ASSET;
	}
	
}
