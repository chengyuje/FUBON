package com.systex.jbranch.app.server.fps.kyc111;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class KYC111InputVO extends PagingInputVO{
	private String QUESTION_VERSION;
	private String QUESTION_DESC;
	private String ANS_OTHER_FLAG;
	private String ANS_MEMO_FLAG;
	private String QUESTION_TYPE;
	private List<Map<String,Object>> ANSWER_DESC;
	private List DEL_ANSWER_DESC;
	private String tempName;
	private String realTempName;
	private String DOC_ID;
	private String QUESTION_DESC_ENG;
	
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
	public List getDEL_ANSWER_DESC() {
		return DEL_ANSWER_DESC;
	}
	public void setDEL_ANSWER_DESC(List dEL_ANSWER_DESC) {
		DEL_ANSWER_DESC = dEL_ANSWER_DESC;
	}
	public String getQUESTION_DESC_ENG() {
		return QUESTION_DESC_ENG;
	}
	public void setQUESTION_DESC_ENG(String qUESTION_DESC_ENG) {
		QUESTION_DESC_ENG = qUESTION_DESC_ENG;
	}
	
		
}
