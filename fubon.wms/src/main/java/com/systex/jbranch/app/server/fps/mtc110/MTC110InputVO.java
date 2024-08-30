package com.systex.jbranch.app.server.fps.mtc110;

import java.sql.Date;
import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class MTC110InputVO extends PagingInputVO{
	// for查詢
	private String cust_id;			// 委託人ID
	private Date period_s;
	private Date period_e;
	// for改變狀態
	private String status;
	
	// TBMTC_CONTRACT_MAIN
	private String CON_NO;			// 序號
	private String CON_TYPE;		// 信託類別
	private String CON_CURR;		// 契約幣別
	private String CURR1;	    	// 交付幣別1
	private String AMT1;			// 交付金額1
	private String CURR2;			// 交付幣別2
	private String AMT2;			// 交付金額2
	private String CURR3;			// 交付幣別3
	private String AMT3;			// 交付金額3
	private String SIGNING_FEE;		// 簽約費
	private String MODIFY_FEE;		// 修約費
	private String MNG_FEE_RATE1;	// 管理費（年費率）1
	private String MNG_FEE_RATE2;	// 管理費（年費率）2
	private String MNG_FEE_RATE3;	// 管理費（年費率）3
	private String MNG_FEE_MIN;		// 最低管理費
	private String END_YEARS;		// 終止條件_成立滿幾年
	private String END_AMT_LIMIT;	// 終止條件_財產低於某金額
	private String TERM_CON;		// 提前終止條件
	private String MODI_CON;		// 變更契約條件
	private String DISC_CON;		// 運用決定權條件
	private String APP_SUP;			// 委任監察人
	private String DISC_ID;			// 運用決定權條件＿推派監察人ID
	private Boolean PAY_TYPE1;		// 信託財產給付方式
	private Boolean PAY_TYPE2;		// 信託財產給付方式
	private Boolean SIP_TYPE1;		// 定期定額給付方式
	private Boolean SIP_TYPE2;		// 定期定額給付方式
	private String APL_CON;			// 申請撥款_有權申請人
	private Boolean APL_DOC_Y;		// 申請撥款_是否需提供佐證單據
	private Boolean APL_DOC_N;		// 申請撥款_是否需提供佐證單據
	private Boolean BEN_AGE_YN1;	
	private String BEN_AGE1;		// 依受益人年紀給付_滿幾歲(含)前
	private String BEN_CURR1;		// 依受益人年紀給付_滿幾歲(含)前_幣別
	private String BEN_AMT1;		// 依受益人年紀給付_滿幾歲(含)前_金額
	private Boolean BEN_AGE_YN2;	
	private String BEN_AGE2;		// 依受益人年紀給付_超過幾歲
	private String BEN_AGE3;		// 依受益人年紀給付_未滿幾歲
	private String BEN_CURR2;		// 依受益人年紀給付_介於歲間_幣別
	private String BEN_AMT2;		// 依受益人年紀給付_介於歲間_金額
	private Boolean BEN_AGE_YN3;	
	private String BEN_AGE4;		// 依受益人年紀給付_滿幾歲(含)後
	private String BEN_CURR3;		// 依受益人年紀給付_滿幾歲(含)後_幣別
	private String BEN_AMT3;		// 依受益人年紀給付_滿幾歲(含)後_金額
//	private String AGR_MON_TYPE;	// 依約定月份給付類別
	private Boolean AGR_MON_TYPE_M;	// 依約定月份給付類別_每月
	private Boolean AGR_MON_TYPE_A;	// 依約定月份給付類別_每年某幾月
	private String M_CURR;			// 依約定月份給付_每月_幣別
	private String M_AMT;			// 依約定月份給付_每月_金額
	private List<String> list1;		// 依約定月份給付_每年幾月
	private String A_MONTHS;		// 依約定月份給付_每年幾月(for報表)
	private String A_CURR;			// 依約定月份給付_每年幾月_幣別
	private String A_AMT;			// 依約定月份給付_每年幾月_金額
	private String UNLIMIT_DOC_YN;	// 是否不限用途免付單據
	private String UNLIMIT_DOC_TYPE;// 不限用途免付單據適用人
	private String LIMIT_DOC_YN;	// 是否限制用途檢附單據
	private String LIMIT_DOC_TYPE;	// 限制用途檢附單據適用人
	private Boolean EDU_YN;			// 教育費
	private Boolean MED_YN;			// 醫療費
	private Boolean MED_PAY_FOR_A;	// 醫療費類別
	private Boolean MED_PAY_FOR_B;	// 醫療費類別
	private Boolean NUR_YN;			// 安養護機構及看護支出
	private Boolean NUR_PAY_FOR_A;	// 安養護機構及看護支出類別
	private Boolean NUR_PAY_FOR_B;	// 安養護機構及看護支出類別
	private Boolean OTR_YN;			// 其他
	private String MAR_CURR;		// 結婚_幣別
	private String MAR_AMT;			// 結婚_金額
	private String BIR_CURR;		// 生育_幣別
	private String BIR_AMT;			// 生育_金額
	private String HOS_CURR;		// 購屋_幣別
	private String HOS_AMT;			// 購屋_金額
	private String OTR_CURR;		// 其他_幣別
	private String OTR_AMT;			// 其他_金額
	private String OTR_ITEM;		// 其他_項目
	private String CON_STATUS;		// 狀態
	
	// TBMTC_CONTRACT_DETAIL
	private String BIRTH_DATE;
//	private Date BIRTH_DATE;
//	private List<String> list2;		// 報酬給付頻率
//	private List<String> list3;		// 報酬給付頻率
//	private List<String> list4;		// 報酬給付頻率
//	private List<String> list5;		// 報酬給付頻率
	private Map<String,Object> custList1;
	private Map<String,Object> custList2;
	private Map<String,Object> custList3;
	private Map<String,Object> custList4;
	private Map<String,Object> custList5;
	private String PAY_CURR;			// 報酬及給付幣別
	private String CUST_YN;				// 委託人是否為本行客戶
	private String BRA_NBR;				// 分行代碼
	private String BRANCH_NBR;			// 分行代碼
	private String BRANCH_NAME;			// 分行名稱
	private String SEAL_UNDER7;			// 未滿7歲_印鑑留存方式	1：併留法代雙方	2：僅併留＿＿＿印鑑
	private String SEAL_UNDER7_NAME;	// 未滿7歲_僅併留印鑑法定代理人姓名
	private String SEAL_UNDER20_1;		// 滿7歲未滿20歲_印鑑留存方式1	1：不併留法代雙方印鑑	2：併留印鑑
	private String SEAL_UNDER20_2;		// 滿7歲未滿20歲_印鑑留存方式2	1：併留法代雙方	2：僅併留＿＿＿印鑑
	private String SEAL_UNDER20_NAME;	// 滿7歲未滿20歲_僅併留印鑑法定代理人姓名
	
	private String bank_id;				// 他行銀行代碼
	
	/*===============================================*/
	public String getCust_id() {
		return cust_id;
	}
	public void setCust_id(String cust_id) {
		this.cust_id = cust_id;
	}
	public Date getPeriod_s() {
		return period_s;
	}
	public void setPeriod_s(Date period_s) {
		this.period_s = period_s;
	}
	public Date getPeriod_e() {
		return period_e;
	}
	public void setPeriod_e(Date period_e) {
		this.period_e = period_e;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getCON_NO() {
		return CON_NO;
	}
	public void setCON_NO(String cON_NO) {
		CON_NO = cON_NO;
	}
	public String getCON_TYPE() {
		return CON_TYPE;
	}
	public void setCON_TYPE(String cON_TYPE) {
		CON_TYPE = cON_TYPE;
	}
	public String getCON_CURR() {
		return CON_CURR;
	}
	public void setCON_CURR(String cON_CURR) {
		CON_CURR = cON_CURR;
	}
	public String getCURR1() {
		return CURR1;
	}
	public void setCURR1(String cURR1) {
		CURR1 = cURR1;
	}
	public String getAMT1() {
		return AMT1;
	}
	public void setAMT1(String aMT1) {
		AMT1 = aMT1;
	}
	public String getCURR2() {
		return CURR2;
	}
	public void setCURR2(String cURR2) {
		CURR2 = cURR2;
	}
	public String getAMT2() {
		return AMT2;
	}
	public void setAMT2(String aMT2) {
		AMT2 = aMT2;
	}
	public String getCURR3() {
		return CURR3;
	}
	public void setCURR3(String cURR3) {
		CURR3 = cURR3;
	}
	public String getAMT3() {
		return AMT3;
	}
	public void setAMT3(String aMT3) {
		AMT3 = aMT3;
	}
	public String getSIGNING_FEE() {
		return SIGNING_FEE;
	}
	public void setSIGNING_FEE(String sIGNING_FEE) {
		SIGNING_FEE = sIGNING_FEE;
	}
	public String getMODIFY_FEE() {
		return MODIFY_FEE;
	}
	public void setMODIFY_FEE(String mODIFY_FEE) {
		MODIFY_FEE = mODIFY_FEE;
	}
	public String getMNG_FEE_RATE1() {
		return MNG_FEE_RATE1;
	}
	public void setMNG_FEE_RATE1(String mNG_FEE_RATE1) {
		MNG_FEE_RATE1 = mNG_FEE_RATE1;
	}
	public String getMNG_FEE_RATE2() {
		return MNG_FEE_RATE2;
	}
	public void setMNG_FEE_RATE2(String mNG_FEE_RATE2) {
		MNG_FEE_RATE2 = mNG_FEE_RATE2;
	}
	public String getMNG_FEE_RATE3() {
		return MNG_FEE_RATE3;
	}
	public void setMNG_FEE_RATE3(String mNG_FEE_RATE3) {
		MNG_FEE_RATE3 = mNG_FEE_RATE3;
	}
	public String getMNG_FEE_MIN() {
		return MNG_FEE_MIN;
	}
	public void setMNG_FEE_MIN(String mNG_FEE_MIN) {
		MNG_FEE_MIN = mNG_FEE_MIN;
	}
	public String getEND_YEARS() {
		return END_YEARS;
	}
	public void setEND_YEARS(String eND_YEARS) {
		END_YEARS = eND_YEARS;
	}
	public String getEND_AMT_LIMIT() {
		return END_AMT_LIMIT;
	}
	public void setEND_AMT_LIMIT(String eND_AMT_LIMIT) {
		END_AMT_LIMIT = eND_AMT_LIMIT;
	}
	public String getTERM_CON() {
		return TERM_CON;
	}
	public void setTERM_CON(String tERM_CON) {
		TERM_CON = tERM_CON;
	}
	public String getMODI_CON() {
		return MODI_CON;
	}
	public void setMODI_CON(String mODI_CON) {
		MODI_CON = mODI_CON;
	}
	public String getDISC_CON() {
		return DISC_CON;
	}
	public void setDISC_CON(String dISC_CON) {
		DISC_CON = dISC_CON;
	}
	public String getAPP_SUP() {
		return APP_SUP;
	}
	public void setAPP_SUP(String aPP_SUP) {
		APP_SUP = aPP_SUP;
	}
	public String getDISC_ID() {
		return DISC_ID;
	}
	public void setDISC_ID(String dISC_ID) {
		DISC_ID = dISC_ID;
	}
	public Boolean getPAY_TYPE1() {
		return PAY_TYPE1;
	}
	public void setPAY_TYPE1(Boolean pAY_TYPE1) {
		PAY_TYPE1 = pAY_TYPE1;
	}
	public Boolean getPAY_TYPE2() {
		return PAY_TYPE2;
	}
	public void setPAY_TYPE2(Boolean pAY_TYPE2) {
		PAY_TYPE2 = pAY_TYPE2;
	}
	public Boolean getSIP_TYPE1() {
		return SIP_TYPE1;
	}
	public void setSIP_TYPE1(Boolean sIP_TYPE1) {
		SIP_TYPE1 = sIP_TYPE1;
	}
	public Boolean getSIP_TYPE2() {
		return SIP_TYPE2;
	}
	public void setSIP_TYPE2(Boolean sIP_TYPE2) {
		SIP_TYPE2 = sIP_TYPE2;
	}
	public String getAPL_CON() {
		return APL_CON;
	}
	public void setAPL_CON(String aPL_CON) {
		APL_CON = aPL_CON;
	}
	public Boolean getAPL_DOC_Y() {
		return APL_DOC_Y;
	}
	public void setAPL_DOC_Y(Boolean aPL_DOC_Y) {
		APL_DOC_Y = aPL_DOC_Y;
	}
	public Boolean getAPL_DOC_N() {
		return APL_DOC_N;
	}
	public void setAPL_DOC_N(Boolean aPL_DOC_N) {
		APL_DOC_N = aPL_DOC_N;
	}
	public Boolean getBEN_AGE_YN1() {
		return BEN_AGE_YN1;
	}
	public void setBEN_AGE_YN1(Boolean bEN_AGE_YN1) {
		BEN_AGE_YN1 = bEN_AGE_YN1;
	}
	public String getBEN_AGE1() {
		return BEN_AGE1;
	}
	public void setBEN_AGE1(String bEN_AGE1) {
		BEN_AGE1 = bEN_AGE1;
	}
	public String getBEN_CURR1() {
		return BEN_CURR1;
	}
	public void setBEN_CURR1(String bEN_CURR1) {
		BEN_CURR1 = bEN_CURR1;
	}
	public String getBEN_AMT1() {
		return BEN_AMT1;
	}
	public void setBEN_AMT1(String bEN_AMT1) {
		BEN_AMT1 = bEN_AMT1;
	}
	public Boolean getBEN_AGE_YN2() {
		return BEN_AGE_YN2;
	}
	public void setBEN_AGE_YN2(Boolean bEN_AGE_YN2) {
		BEN_AGE_YN2 = bEN_AGE_YN2;
	}
	public String getBEN_AGE2() {
		return BEN_AGE2;
	}
	public void setBEN_AGE2(String bEN_AGE2) {
		BEN_AGE2 = bEN_AGE2;
	}
	public String getBEN_AGE3() {
		return BEN_AGE3;
	}
	public void setBEN_AGE3(String bEN_AGE3) {
		BEN_AGE3 = bEN_AGE3;
	}
	public String getBEN_CURR2() {
		return BEN_CURR2;
	}
	public void setBEN_CURR2(String bEN_CURR2) {
		BEN_CURR2 = bEN_CURR2;
	}
	public String getBEN_AMT2() {
		return BEN_AMT2;
	}
	public void setBEN_AMT2(String bEN_AMT2) {
		BEN_AMT2 = bEN_AMT2;
	}
	public Boolean getBEN_AGE_YN3() {
		return BEN_AGE_YN3;
	}
	public void setBEN_AGE_YN3(Boolean bEN_AGE_YN3) {
		BEN_AGE_YN3 = bEN_AGE_YN3;
	}
	public String getBEN_AGE4() {
		return BEN_AGE4;
	}
	public void setBEN_AGE4(String bEN_AGE4) {
		BEN_AGE4 = bEN_AGE4;
	}
	public String getBEN_CURR3() {
		return BEN_CURR3;
	}
	public void setBEN_CURR3(String bEN_CURR3) {
		BEN_CURR3 = bEN_CURR3;
	}
	public String getBEN_AMT3() {
		return BEN_AMT3;
	}
	public void setBEN_AMT3(String bEN_AMT3) {
		BEN_AMT3 = bEN_AMT3;
	}
//	public String getAGR_MON_TYPE() {
//		return AGR_MON_TYPE;
//	}
//	public void setAGR_MON_TYPE(String aGR_MON_TYPE) {
//		AGR_MON_TYPE = aGR_MON_TYPE;
//	}
	public String getM_CURR() {
		return M_CURR;
	}
	public void setM_CURR(String m_CURR) {
		M_CURR = m_CURR;
	}
	public String getM_AMT() {
		return M_AMT;
	}
	public void setM_AMT(String m_AMT) {
		M_AMT = m_AMT;
	}
	public List<String> getList1() {
		return list1;
	}
	public void setList1(List<String> list1) {
		this.list1 = list1;
	}
	public String getA_MONTHS() {
		return A_MONTHS;
	}
	public void setA_MONTHS(String a_MONTHS) {
		A_MONTHS = a_MONTHS;
	}
	public String getA_CURR() {
		return A_CURR;
	}
	public void setA_CURR(String a_CURR) {
		A_CURR = a_CURR;
	}
	public String getA_AMT() {
		return A_AMT;
	}
	public void setA_AMT(String a_AMT) {
		A_AMT = a_AMT;
	}
	public String getUNLIMIT_DOC_YN() {
		return UNLIMIT_DOC_YN;
	}
	public void setUNLIMIT_DOC_YN(String uNLIMIT_DOC_YN) {
		UNLIMIT_DOC_YN = uNLIMIT_DOC_YN;
	}
	public String getUNLIMIT_DOC_TYPE() {
		return UNLIMIT_DOC_TYPE;
	}
	public void setUNLIMIT_DOC_TYPE(String uNLIMIT_DOC_TYPE) {
		UNLIMIT_DOC_TYPE = uNLIMIT_DOC_TYPE;
	}
	public String getLIMIT_DOC_YN() {
		return LIMIT_DOC_YN;
	}
	public void setLIMIT_DOC_YN(String lIMIT_DOC_YN) {
		LIMIT_DOC_YN = lIMIT_DOC_YN;
	}
	public String getLIMIT_DOC_TYPE() {
		return LIMIT_DOC_TYPE;
	}
	public void setLIMIT_DOC_TYPE(String lIMIT_DOC_TYPE) {
		LIMIT_DOC_TYPE = lIMIT_DOC_TYPE;
	}
	public Boolean getEDU_YN() {
		return EDU_YN;
	}
	public void setEDU_YN(Boolean eDU_YN) {
		EDU_YN = eDU_YN;
	}
	public Boolean getMED_YN() {
		return MED_YN;
	}
	public void setMED_YN(Boolean mED_YN) {
		MED_YN = mED_YN;
	}
//	public String getMED_PAY_FOR() {
//		return MED_PAY_FOR;
//	}
//	public void setMED_PAY_FOR(String mED_PAY_FOR) {
//		MED_PAY_FOR = mED_PAY_FOR;
//	}
	public Boolean getMED_PAY_FOR_A() {
		return MED_PAY_FOR_A;
	}
	public void setMED_PAY_FOR_A(Boolean mED_PAY_FOR_A) {
		MED_PAY_FOR_A = mED_PAY_FOR_A;
	}
	public Boolean getMED_PAY_FOR_B() {
		return MED_PAY_FOR_B;
	}
	public void setMED_PAY_FOR_B(Boolean mED_PAY_FOR_B) {
		MED_PAY_FOR_B = mED_PAY_FOR_B;
	}
	public Boolean getNUR_YN() {
		return NUR_YN;
	}
	public void setNUR_YN(Boolean nUR_YN) {
		NUR_YN = nUR_YN;
	}
//	public String getNUR_PAY_FOR() {
//		return NUR_PAY_FOR;
//	}
//	public void setNUR_PAY_FOR(String nUR_PAY_FOR) {
//		NUR_PAY_FOR = nUR_PAY_FOR;
//	}
	public Boolean getNUR_PAY_FOR_A() {
		return NUR_PAY_FOR_A;
	}
	public void setNUR_PAY_FOR_A(Boolean nUR_PAY_FOR_A) {
		NUR_PAY_FOR_A = nUR_PAY_FOR_A;
	}
	public Boolean getNUR_PAY_FOR_B() {
		return NUR_PAY_FOR_B;
	}
	public void setNUR_PAY_FOR_B(Boolean nUR_PAY_FOR_B) {
		NUR_PAY_FOR_B = nUR_PAY_FOR_B;
	}
	public Boolean getOTR_YN() {
		return OTR_YN;
	}
	public void setOTR_YN(Boolean oTR_YN) {
		OTR_YN = oTR_YN;
	}
	public String getMAR_CURR() {
		return MAR_CURR;
	}
	public void setMAR_CURR(String mAR_CURR) {
		MAR_CURR = mAR_CURR;
	}
	public String getMAR_AMT() {
		return MAR_AMT;
	}
	public void setMAR_AMT(String mAR_AMT) {
		MAR_AMT = mAR_AMT;
	}
	public String getBIR_CURR() {
		return BIR_CURR;
	}
	public void setBIR_CURR(String bIR_CURR) {
		BIR_CURR = bIR_CURR;
	}
	public String getBIR_AMT() {
		return BIR_AMT;
	}
	public void setBIR_AMT(String bIR_AMT) {
		BIR_AMT = bIR_AMT;
	}
	public String getHOS_CURR() {
		return HOS_CURR;
	}
	public void setHOS_CURR(String hOS_CURR) {
		HOS_CURR = hOS_CURR;
	}
	public String getHOS_AMT() {
		return HOS_AMT;
	}
	public void setHOS_AMT(String hOS_AMT) {
		HOS_AMT = hOS_AMT;
	}
	public String getOTR_CURR() {
		return OTR_CURR;
	}
	public void setOTR_CURR(String oTR_CURR) {
		OTR_CURR = oTR_CURR;
	}
	public String getOTR_AMT() {
		return OTR_AMT;
	}
	public void setOTR_AMT(String oTR_AMT) {
		OTR_AMT = oTR_AMT;
	}
	public String getOTR_ITEM() {
		return OTR_ITEM;
	}
	public void setOTR_ITEM(String oTR_ITEM) {
		OTR_ITEM = oTR_ITEM;
	}
	public String getCON_STATUS() {
		return CON_STATUS;
	}
	public void setCON_STATUS(String cON_STATUS) {
		CON_STATUS = cON_STATUS;
	}
	public String getBIRTH_DATE() {
		return BIRTH_DATE;
	}
	public void setBIRTH_DATE(String bIRTH_DATE) {
		BIRTH_DATE = bIRTH_DATE;
	}
	//	public Date getBIRTH_DATE() {
//		return BIRTH_DATE;
//	}
//	public void setBIRTH_DATE(Date bIRTH_DATE) {
//		BIRTH_DATE = bIRTH_DATE;
//	}
//	public List<String> getList2() {
//		return list2;
//	}
//	public void setList2(List<String> list2) {
//		this.list2 = list2;
//	}
//	public List<String> getList3() {
//		return list3;
//	}
//	public void setList3(List<String> list3) {
//		this.list3 = list3;
//	}
//	public List<String> getList4() {
//		return list4;
//	}
//	public void setList4(List<String> list4) {
//		this.list4 = list4;
//	}
//	public List<String> getList5() {
//		return list5;
//	}
//	public void setList5(List<String> list5) {
//		this.list5 = list5;
//	}
	public Map<String, Object> getCustList1() {
		return custList1;
	}
	public void setCustList1(Map<String, Object> custList1) {
		this.custList1 = custList1;
	}
	public Map<String, Object> getCustList2() {
		return custList2;
	}
	public void setCustList2(Map<String, Object> custList2) {
		this.custList2 = custList2;
	}
	public Map<String, Object> getCustList3() {
		return custList3;
	}
	public void setCustList3(Map<String, Object> custList3) {
		this.custList3 = custList3;
	}
	public Map<String, Object> getCustList4() {
		return custList4;
	}
	public void setCustList4(Map<String, Object> custList4) {
		this.custList4 = custList4;
	}
	public Map<String, Object> getCustList5() {
		return custList5;
	}
	public void setCustList5(Map<String, Object> custList5) {
		this.custList5 = custList5;
	}
	public String getPAY_CURR() {
		return PAY_CURR;
	}
	public void setPAY_CURR(String pAY_CURR) {
		PAY_CURR = pAY_CURR;
	}
	public String getCUST_YN() {
		return CUST_YN;
	}
	public void setCUST_YN(String cUST_YN) {
		CUST_YN = cUST_YN;
	}
	public String getBRA_NBR() {
		return BRA_NBR;
	}
	public void setBRA_NBR(String bRA_NBR) {
		BRA_NBR = bRA_NBR;
	}
	public String getBRANCH_NBR() {
		return BRANCH_NBR;
	}
	public void setBRANCH_NBR(String bRANCH_NBR) {
		BRANCH_NBR = bRANCH_NBR;
	}
	public String getBRANCH_NAME() {
		return BRANCH_NAME;
	}
	public void setBRANCH_NAME(String bRANCH_NAME) {
		BRANCH_NAME = bRANCH_NAME;
	}
	public String getSEAL_UNDER7() {
		return SEAL_UNDER7;
	}
	public void setSEAL_UNDER7(String sEAL_UNDER7) {
		SEAL_UNDER7 = sEAL_UNDER7;
	}
	public String getSEAL_UNDER7_NAME() {
		return SEAL_UNDER7_NAME;
	}
	public void setSEAL_UNDER7_NAME(String sEAL_UNDER7_NAME) {
		SEAL_UNDER7_NAME = sEAL_UNDER7_NAME;
	}
	public String getSEAL_UNDER20_1() {
		return SEAL_UNDER20_1;
	}
	public void setSEAL_UNDER20_1(String sEAL_UNDER20_1) {
		SEAL_UNDER20_1 = sEAL_UNDER20_1;
	}
	public String getSEAL_UNDER20_2() {
		return SEAL_UNDER20_2;
	}
	public void setSEAL_UNDER20_2(String sEAL_UNDER20_2) {
		SEAL_UNDER20_2 = sEAL_UNDER20_2;
	}
	public String getSEAL_UNDER20_NAME() {
		return SEAL_UNDER20_NAME;
	}
	public void setSEAL_UNDER20_NAME(String sEAL_UNDER20_NAME) {
		SEAL_UNDER20_NAME = sEAL_UNDER20_NAME;
	}
	public String getBank_id() {
		return bank_id;
	}
	public void setBank_id(String bank_id) {
		this.bank_id = bank_id;
	}
	public Boolean getAGR_MON_TYPE_M() {
		return AGR_MON_TYPE_M;
	}
	public void setAGR_MON_TYPE_M(Boolean aGR_MON_TYPE_M) {
		AGR_MON_TYPE_M = aGR_MON_TYPE_M;
	}
	public Boolean getAGR_MON_TYPE_A() {
		return AGR_MON_TYPE_A;
	}
	public void setAGR_MON_TYPE_A(Boolean aGR_MON_TYPE_A) {
		AGR_MON_TYPE_A = aGR_MON_TYPE_A;
	}
}
