package com.systex.jbranch.app.server.fps.pms109;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

/**
 * 2016/06/13 Create by Kevin 
 * 2017/02/01 Modify by Kevin
 */
public class PMS109InputVO extends PagingInputVO {

	private String SEQ; 				// 銷售計劃序號
	private String PLAN_YEARMON; 		// 銷售計劃月份
	private String BRANCH_NBR; 			// 分行代碼
	private String AO_CODE; 			// AO CODE
	private String EMP_ID; 				// 員工編號
	private String EMP_NAME; 			// 員工姓名
	private String CUST_ID; 			// 客戶ID
	private String CUST_NAME; 			// 客戶姓名
	private String SRC_TYPE; 			// 來源種類
	private String CF_AMT; 				// 金流金額
	private String PLAN_AMT; 			// 可規劃金額
	private String EST_PRD; 			// 預計承作商品
	private String EST_AMT; 			// 預計承做金額
	private String EST_EARNINGS_RATE; 	// 預估收益率
	private String EST_EARNINGS; 		// 預估收益
	private String MEETING_DATE_S; 		// M日期約訪時間起
	private String MEETING_DATE_E; 		// M日期約訪時間迄
	private String OS_FLAG; 			// 陪訪人員-營運督導
	private String PFD_FLAG; 			// 陪訪人員-個金主管
	private String BE_FLAG; 			// 陪訪人員-業務主管
	private String FA_FLAG; 			// 陪訪人員-FA
	private String IA_FLAG; 			// 陪訪人員-IA
	private String VISIT_PURPOSE; 		// 陪訪目的
	private String VISIT_PURPOSE_OTHER; // 陪訪目的_其他說明
	private String KEY_ISSUE; 			// 關鍵議題
	private String CLOSE_FLAG; 			// 結案
	private String CLOSE_C_DATE; 		// 結案日期
	private String MONTH; 				// 年月判斷
	private String PRD; 				// 確認有值
	
	private String ACTION_DATE; 		// A日期
	private String CLOSE_DATE; 			// C日期預計時間
	private String MEETING_DATE;
	private String queryModType;

	public String getMEETING_DATE() {
		return MEETING_DATE;
	}

	public void setMEETING_DATE(String mEETING_DATE) {
		MEETING_DATE = mEETING_DATE;
	}

	public String getQueryModType() {
		return queryModType;
	}

	public void setQueryModType(String queryModType) {
		this.queryModType = queryModType;
	}

	// 2017/3/8 PMS103 is object but other is string?
	private Object PMSROW;

	public String getSEQ() {
		return SEQ;
	}

	public String getPLAN_YEARMON() {
		return PLAN_YEARMON;
	}

	public String getBRANCH_NBR() {
		return BRANCH_NBR;
	}

	public String getAO_CODE() {
		return AO_CODE;
	}

	public String getEMP_ID() {
		return EMP_ID;
	}

	public String getEMP_NAME() {
		return EMP_NAME;
	}

	public String getCUST_ID() {
		return CUST_ID;
	}

	public String getCUST_NAME() {
		return CUST_NAME;
	}

	public String getSRC_TYPE() {
		return SRC_TYPE;
	}

	public String getPLAN_AMT() {
		return PLAN_AMT;
	}

	public String getEST_PRD() {
		return EST_PRD;
	}

	public String getEST_AMT() {
		return EST_AMT;
	}

	public String getEST_EARNINGS_RATE() {
		return EST_EARNINGS_RATE;
	}

	public String getEST_EARNINGS() {
		return EST_EARNINGS;
	}

	public String getACTION_DATE() {
		return ACTION_DATE;
	}

	public String getMEETING_DATE_S() {
		return MEETING_DATE_S;
	}

	public String getMEETING_DATE_E() {
		return MEETING_DATE_E;
	}

	public String getCLOSE_DATE() {
		return CLOSE_DATE;
	}

	public String getOS_FLAG() {
		return OS_FLAG;
	}

	public String getPFD_FLAG() {
		return PFD_FLAG;
	}

	public String getBE_FLAG() {
		return BE_FLAG;
	}

	public String getFA_FLAG() {
		return FA_FLAG;
	}

	public String getIA_FLAG() {
		return IA_FLAG;
	}

	public String getVISIT_PURPOSE() {
		return VISIT_PURPOSE;
	}

	public String getVISIT_PURPOSE_OTHER() {
		return VISIT_PURPOSE_OTHER;
	}

	public String getKEY_ISSUE() {
		return KEY_ISSUE;
	}

	public String getCLOSE_FLAG() {
		return CLOSE_FLAG;
	}

	public String getCLOSE_C_DATE() {
		return CLOSE_C_DATE;
	}

	public void setSEQ(String sEQ) {
		SEQ = sEQ;
	}

	public String getPRD() {
		return PRD;
	}

	public void setPRD(String pRD) {
		PRD = pRD;
	}

	public void setPLAN_YEARMON(String pLAN_YEARMON) {
		PLAN_YEARMON = pLAN_YEARMON;
	}

	public void setBRANCH_NBR(String bRANCH_NBR) {
		BRANCH_NBR = bRANCH_NBR;
	}

	public void setAO_CODE(String aO_CODE) {
		AO_CODE = aO_CODE;
	}

	public void setEMP_ID(String eMP_ID) {
		EMP_ID = eMP_ID;
	}

	public void setEMP_NAME(String eMP_NAME) {
		EMP_NAME = eMP_NAME;
	}

	public void setCUST_ID(String cUST_ID) {
		CUST_ID = cUST_ID;
	}

	public void setCUST_NAME(String cUST_NAME) {
		CUST_NAME = cUST_NAME;
	}

	public void setSRC_TYPE(String sRC_TYPE) {
		SRC_TYPE = sRC_TYPE;
	}

	public void setPLAN_AMT(String pLAN_AMT) {
		PLAN_AMT = pLAN_AMT;
	}

	public void setEST_PRD(String eST_PRD) {
		EST_PRD = eST_PRD;
	}

	public void setEST_AMT(String eST_AMT) {
		EST_AMT = eST_AMT;
	}

	public void setEST_EARNINGS_RATE(String eST_EARNINGS_RATE) {
		EST_EARNINGS_RATE = eST_EARNINGS_RATE;
	}

	public void setEST_EARNINGS(String eST_EARNINGS) {
		EST_EARNINGS = eST_EARNINGS;
	}

	public void setACTION_DATE(String aCTION_DATE) {
		ACTION_DATE = aCTION_DATE;
	}

	public void setMEETING_DATE_S(String mEETING_DATE_S) {
		MEETING_DATE_S = mEETING_DATE_S;
	}

	public void setMEETING_DATE_E(String mEETING_DATE_E) {
		MEETING_DATE_E = mEETING_DATE_E;
	}

	public void setCLOSE_DATE(String cLOSE_DATE) {
		CLOSE_DATE = cLOSE_DATE;
	}

	public void setOS_FLAG(String oS_FLAG) {
		OS_FLAG = oS_FLAG;
	}

	public void setPFD_FLAG(String pFD_FLAG) {
		PFD_FLAG = pFD_FLAG;
	}

	public void setBE_FLAG(String bE_FLAG) {
		BE_FLAG = bE_FLAG;
	}

	public void setFA_FLAG(String fA_FLAG) {
		FA_FLAG = fA_FLAG;
	}

	public void setIA_FLAG(String iA_FLAG) {
		IA_FLAG = iA_FLAG;
	}

	public void setVISIT_PURPOSE(String vISIT_PURPOSE) {
		VISIT_PURPOSE = vISIT_PURPOSE;
	}

	public void setVISIT_PURPOSE_OTHER(String vISIT_PURPOSE_OTHER) {
		VISIT_PURPOSE_OTHER = vISIT_PURPOSE_OTHER;
	}

	public void setKEY_ISSUE(String kEY_ISSUE) {
		KEY_ISSUE = kEY_ISSUE;
	}

	public String getMONTH() {
		return MONTH;
	}

	public void setMONTH(String mONTH) {
		MONTH = mONTH;
	}

	public void setCLOSE_FLAG(String cLOSE_FLAG) {
		CLOSE_FLAG = cLOSE_FLAG;
	}

	public void setCLOSE_C_DATE(String cLOSE_C_DATE) {
		CLOSE_C_DATE = cLOSE_C_DATE;
	}

	public String getCF_AMT() {
		return CF_AMT;
	}

	public void setCF_AMT(String cF_AMT) {
		CF_AMT = cF_AMT;
	}

	public Object getPMSROW() {
		return PMSROW;
	}

	public void setPMSROW(Object pMSROW) {
		PMSROW = pMSROW;
	}

}
