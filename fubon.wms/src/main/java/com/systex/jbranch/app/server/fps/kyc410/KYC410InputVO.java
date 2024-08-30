package com.systex.jbranch.app.server.fps.kyc410;


import java.sql.Date;
import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class KYC410InputVO extends PagingInputVO{
	
	private String QUESTION_DESC;
	private Date sDate;
	private Date eDate;
	private String QUESTION_VERSION;

	private String ANS_OTHER_FLAG;
	private String ANS_MEMO_FLAG;
	private String QUESTION_TYPE;
	private List<Map<String,Object>> ANSWER_DESC;
	private List<Map<String,Object>>  DEL_ANSWER_DESC;
	private String tempName;
	private String realTempName;
	private String DOC_ID;
	private String CORR_ANS;
	
	public List<Map<String, Object>> getDEL_ANSWER_DESC() {
		return DEL_ANSWER_DESC;
	}
	public void setDEL_ANSWER_DESC(List<Map<String, Object>> dEL_ANSWER_DESC) {
		DEL_ANSWER_DESC = dEL_ANSWER_DESC;
	}
	
	public String getDOC_ID() {
		return DOC_ID;
	}
	public void setDOC_ID(String dOC_ID) {
		DOC_ID = dOC_ID;
	}
	public String getTempName() {
		return tempName;
	}
	public void setTempName(String tempName) {
		this.tempName = tempName;
	}
	public String getRealTempName() {
		return realTempName;
	}
	public void setRealTempName(String realTempName) {
		this.realTempName = realTempName;
	}
	public String getANS_OTHER_FLAG() {
		return ANS_OTHER_FLAG;
	}
	public void setANS_OTHER_FLAG(String aNS_OTHER_FLAG) {
		ANS_OTHER_FLAG = aNS_OTHER_FLAG;
	}
	public String getANS_MEMO_FLAG() {
		return ANS_MEMO_FLAG;
	}
	public void setANS_MEMO_FLAG(String aNS_MEMO_FLAG) {
		ANS_MEMO_FLAG = aNS_MEMO_FLAG;
	}
	public String getQUESTION_TYPE() {
		return QUESTION_TYPE;
	}
	public void setQUESTION_TYPE(String qUESTION_TYPE) {
		QUESTION_TYPE = qUESTION_TYPE;
	}

	public List<Map<String, Object>> getANSWER_DESC() {
		return ANSWER_DESC;
	}
	public void setANSWER_DESC(List<Map<String, Object>> aNSWER_DESC) {
		ANSWER_DESC = aNSWER_DESC;
	}
		
	public String getQUESTION_VERSION() {
		return QUESTION_VERSION;
	}
	public void setQUESTION_VERSION(String qUESTION_VERSION) {
		QUESTION_VERSION = qUESTION_VERSION;
	}
	public String getQUESTION_DESC() {
		return QUESTION_DESC;
	}
	public void setQUESTION_DESC(String qUESTION_DESC) {
		QUESTION_DESC = qUESTION_DESC;
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
	public String getCORR_ANS() {
		return CORR_ANS;
	}
	public void setCORR_ANS(String cORR_ANS) {
		CORR_ANS = cORR_ANS;
	}
	
	
	
}
