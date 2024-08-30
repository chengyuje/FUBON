package com.systex.jbranch.app.server.fps.ins210;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class INS210InputVO extends PagingInputVO{
	
	private String CUST_ID;
	private List<Map<String, Object>> planList;
	private Map<String, Object> select_data;
	private List<Map<String, Object>> suggestList;
	private List<Map<String, Object>> sendInsuredList;
	private String LIFE_EXPENSE;
	private String LIFE_EXPENSE_YEARS;
	private BigDecimal LIFE_EXPENSE_AMT;
	private String LOAN_AMT;
	private String EDU_FEE;
	private String PREPARE_AMT;
	private String EXTRA_PROTEXT;
	private String CAREFEES;				//看護費
	private String SEARCH_WORK;	
	private BigDecimal jobYears; 			//幾年勝任工作
	private String PROF_GRADE;				//職業等級
	private String TTL_FLAG;
	private String HOSPITAL_TYPE;			//醫院類型
	private String WARD_TYPE;				//病房類型
	private String PRD_ID;					//險種代號
	private BigDecimal SHD_PROTECT1;		//應備總金額
	private BigDecimal NOW_PROTECT1;		//已備總金額
	private BigDecimal SHD_PROTECT2;		//應備癌症住院日額
	private BigDecimal NOW_PROTECT2;		//已備癌症住院日額
	private BigDecimal SHD_PROTECT3;		//應備長期看護每月
	private BigDecimal NOW_PROTECT3;		//已備長期看護每月

	private BigDecimal PROTECT_GAP1;		//缺口
	private BigDecimal PROTECT_GAP2;		//住院日額缺口
	private BigDecimal PROTECT_GAP3;		//看護每月缺口
	
	private String INSPRD_ANNUAL;			//保障年期
	private String PRINT_YN;
	private String PLAN_TYPE;				//保險規劃類型
	private String STATUS;					//狀態
	private String PLAN_KEYNO;				//保險規劃主檔序號
	private String PLAN_D_KEYNO;			//保險規劃明細檔序號
	private String SICKROOM_FEE;			//醫院與病房種類KEYNO
	private Boolean TYPE_CANCER;			//對應類型-癌症
	private Boolean TYPE_MAJOR;				//對應類型-重大疾病
	private Boolean TYPE_LT;				//對應類型-長期看護
	private BigDecimal NURSE_FEE_PAY;			//長期照護照顧方式KEYNO
	private String currCD;					// 幣別
	private BigDecimal PARA_NO;
	private String SUGGEST_TYPE;
	private List<String> DISEASE;
	private BigDecimal MAJOR_DISEASES_PAY;
	private String PRD_UNIT;
	private String AGE;
	private String GENDER;
	private Boolean INS200_FROM_INS132;		//是否從家庭財務安全試算進入
	private BigDecimal TYPE3_PARA_NO;
//	private Boolean isFinancialTrial;			
	private String paraNO;
	private String policyNbr;
	private String comId;
	private String prdName;
	private BigDecimal insuredamt;
	private BigDecimal securityAmt;
	private String upqtySel;
	private BigDecimal inyearfee;
	private BigDecimal localInsyearfee;
	private BigDecimal otherInsured;
	private String insPrdKeyNo;
	private BigDecimal reInsuredamt;
	private BigDecimal reInyearfee;
	private String careWay;
	private String careStyle;
	
	
	
	public String getCUST_ID() {
		return CUST_ID;
	}
	public List<Map<String, Object>> getPlanList() {
		return planList;
	}
	public Map<String, Object> getSelect_data() {
		return select_data;
	}
	public List<Map<String, Object>> getSuggestList() {
		return suggestList;
	}
	public List<Map<String, Object>> getSendInsuredList() {
		return sendInsuredList;
	}
	public String getLIFE_EXPENSE() {
		return LIFE_EXPENSE;
	}
	public String getLIFE_EXPENSE_YEARS() {
		return LIFE_EXPENSE_YEARS;
	}
	public BigDecimal getLIFE_EXPENSE_AMT() {
		return LIFE_EXPENSE_AMT;
	}
	public String getLOAN_AMT() {
		return LOAN_AMT;
	}
	public String getEDU_FEE() {
		return EDU_FEE;
	}
	public String getPREPARE_AMT() {
		return PREPARE_AMT;
	}
	public String getEXTRA_PROTEXT() {
		return EXTRA_PROTEXT;
	}
	public String getCAREFEES() {
		return CAREFEES;
	}
	public String getSEARCH_WORK() {
		return SEARCH_WORK;
	}
	public String getPROF_GRADE() {
		return PROF_GRADE;
	}
	public String getTTL_FLAG() {
		return TTL_FLAG;
	}
	public String getHOSPITAL_TYPE() {
		return HOSPITAL_TYPE;
	}
	public String getWARD_TYPE() {
		return WARD_TYPE;
	}
	public String getPRD_ID() {
		return PRD_ID;
	}
	public BigDecimal getSHD_PROTECT1() {
		return SHD_PROTECT1;
	}
	public BigDecimal getNOW_PROTECT1() {
		return NOW_PROTECT1;
	}
	public BigDecimal getSHD_PROTECT2() {
		return SHD_PROTECT2;
	}
	public BigDecimal getNOW_PROTECT2() {
		return NOW_PROTECT2;
	}
	public BigDecimal getSHD_PROTECT3() {
		return SHD_PROTECT3;
	}
	public BigDecimal getNOW_PROTECT3() {
		return NOW_PROTECT3;
	}
	
	public String getINSPRD_ANNUAL() {
		return INSPRD_ANNUAL;
	}
	public String getPRINT_YN() {
		return PRINT_YN;
	}
	public String getPLAN_TYPE() {
		return PLAN_TYPE;
	}
	public String getSTATUS() {
		return STATUS;
	}
	public String getPLAN_KEYNO() {
		return PLAN_KEYNO;
	}
	public String getPLAN_D_KEYNO() {
		return PLAN_D_KEYNO;
	}
	public String getSICKROOM_FEE() {
		return SICKROOM_FEE;
	}
	public Boolean getTYPE_CANCER() {
		return TYPE_CANCER;
	}
	public Boolean getTYPE_MAJOR() {
		return TYPE_MAJOR;
	}
	public Boolean getTYPE_LT() {
		return TYPE_LT;
	}

	public void setCUST_ID(String cUST_ID) {
		CUST_ID = cUST_ID;
	}
	public void setPlanList(List<Map<String, Object>> planList) {
		this.planList = planList;
	}
	public void setSelect_data(Map<String, Object> select_data) {
		this.select_data = select_data;
	}
	public void setSuggestList(List<Map<String, Object>> suggestList) {
		this.suggestList = suggestList;
	}
	public void setSendInsuredList(List<Map<String, Object>> sendInsuredList) {
		this.sendInsuredList = sendInsuredList;
	}
	public void setLIFE_EXPENSE(String lIFE_EXPENSE) {
		LIFE_EXPENSE = lIFE_EXPENSE;
	}
	public void setLIFE_EXPENSE_YEARS(String lIFE_EXPENSE_YEARS) {
		LIFE_EXPENSE_YEARS = lIFE_EXPENSE_YEARS;
	}
	public void setLIFE_EXPENSE_AMT(BigDecimal lIFE_EXPENSE_AMT) {
		LIFE_EXPENSE_AMT = lIFE_EXPENSE_AMT;
	}
	public void setLOAN_AMT(String lOAN_AMT) {
		LOAN_AMT = lOAN_AMT;
	}
	public void setEDU_FEE(String eDU_FEE) {
		EDU_FEE = eDU_FEE;
	}
	public void setPREPARE_AMT(String pREPARE_AMT) {
		PREPARE_AMT = pREPARE_AMT;
	}
	public void setEXTRA_PROTEXT(String eXTRA_PROTEXT) {
		EXTRA_PROTEXT = eXTRA_PROTEXT;
	}
	public void setCAREFEES(String cAREFEES) {
		CAREFEES = cAREFEES;
	}
	public void setSEARCH_WORK(String sEARCH_WORK) {
		SEARCH_WORK = sEARCH_WORK;
	}
	public void setPROF_GRADE(String pROF_GRADE) {
		PROF_GRADE = pROF_GRADE;
	}
	public void setTTL_FLAG(String tTL_FLAG) {
		TTL_FLAG = tTL_FLAG;
	}
	public void setHOSPITAL_TYPE(String hOSPITAL_TYPE) {
		HOSPITAL_TYPE = hOSPITAL_TYPE;
	}
	public void setWARD_TYPE(String wARD_TYPE) {
		WARD_TYPE = wARD_TYPE;
	}
	public void setPRD_ID(String pRD_ID) {
		PRD_ID = pRD_ID;
	}
	public void setSHD_PROTECT1(BigDecimal sHD_PROTECT1) {
		SHD_PROTECT1 = sHD_PROTECT1;
	}
	public void setNOW_PROTECT1(BigDecimal nOW_PROTECT1) {
		NOW_PROTECT1 = nOW_PROTECT1;
	}
	public void setSHD_PROTECT2(BigDecimal sHD_PROTECT2) {
		SHD_PROTECT2 = sHD_PROTECT2;
	}
	public void setNOW_PROTECT2(BigDecimal nOW_PROTECT2) {
		NOW_PROTECT2 = nOW_PROTECT2;
	}
	public void setSHD_PROTECT3(BigDecimal sHD_PROTECT3) {
		SHD_PROTECT3 = sHD_PROTECT3;
	}
	public void setNOW_PROTECT3(BigDecimal nOW_PROTECT3) {
		NOW_PROTECT3 = nOW_PROTECT3;
	}

	public void setINSPRD_ANNUAL(String iNSPRD_ANNUAL) {
		INSPRD_ANNUAL = iNSPRD_ANNUAL;
	}
	public void setPRINT_YN(String pRINT_YN) {
		PRINT_YN = pRINT_YN;
	}
	public void setPLAN_TYPE(String pLAN_TYPE) {
		PLAN_TYPE = pLAN_TYPE;
	}
	public void setSTATUS(String sTATUS) {
		STATUS = sTATUS;
	}
	public void setPLAN_KEYNO(String pLAN_KEYNO) {
		PLAN_KEYNO = pLAN_KEYNO;
	}
	public void setPLAN_D_KEYNO(String pLAN_D_KEYNO) {
		PLAN_D_KEYNO = pLAN_D_KEYNO;
	}
	public void setSICKROOM_FEE(String sICKROOM_FEE) {
		SICKROOM_FEE = sICKROOM_FEE;
	}
	public void setTYPE_CANCER(Boolean tYPE_CANCER) {
		TYPE_CANCER = tYPE_CANCER;
	}
	public void setTYPE_MAJOR(Boolean tYPE_MAJOR) {
		TYPE_MAJOR = tYPE_MAJOR;
	}
	public void setTYPE_LT(Boolean tYPE_LT) {
		TYPE_LT = tYPE_LT;
	}

	public BigDecimal getPROTECT_GAP1() {
		return PROTECT_GAP1;
	}
	public BigDecimal getPROTECT_GAP2() {
		return PROTECT_GAP2;
	}
	public BigDecimal getPROTECT_GAP3() {
		return PROTECT_GAP3;
	}
	public void setPROTECT_GAP1(BigDecimal pROTECT_GAP1) {
		PROTECT_GAP1 = pROTECT_GAP1;
	}
	public void setPROTECT_GAP2(BigDecimal pROTECT_GAP2) {
		PROTECT_GAP2 = pROTECT_GAP2;
	}
	public void setPROTECT_GAP3(BigDecimal pROTECT_GAP3) {
		PROTECT_GAP3 = pROTECT_GAP3;
	}
	public BigDecimal getNURSE_FEE_PAY() {
		return NURSE_FEE_PAY;
	}
	public void setNURSE_FEE_PAY(BigDecimal nURSE_FEE_PAY) {
		NURSE_FEE_PAY = nURSE_FEE_PAY;
	}
	public String getCurrCD() {
		return currCD;
	}
	public void setCurrCD(String currCD) {
		this.currCD = currCD;
	}
	public BigDecimal getPARA_NO() {
		return PARA_NO;
	}
	public void setPARA_NO(BigDecimal pARA_NO) {
		PARA_NO = pARA_NO;
	}
	public String getSUGGEST_TYPE() {
		return SUGGEST_TYPE;
	}
	public void setSUGGEST_TYPE(String sUGGEST_TYPE) {
		SUGGEST_TYPE = sUGGEST_TYPE;
	}
	public List<String> getDISEASE() {
		return DISEASE;
	}
	public void setDISEASE(List<String> dISEASE) {
		DISEASE = dISEASE;
	}
	public BigDecimal getMAJOR_DISEASES_PAY() {
		return MAJOR_DISEASES_PAY;
	}
	public void setMAJOR_DISEASES_PAY(BigDecimal mAJOR_DISEASES_PAY) {
		MAJOR_DISEASES_PAY = mAJOR_DISEASES_PAY;
	}
	public String getPRD_UNIT() {
		return PRD_UNIT;
	}
	public void setPRD_UNIT(String pRD_UNIT) {
		PRD_UNIT = pRD_UNIT;
	}
	public String getAGE() {
		return AGE;
	}
	public void setAGE(String aGE) {
		AGE = aGE;
	}
	public String getGENDER() {
		return GENDER;
	}
	public void setGENDER(String gENDER) {
		GENDER = gENDER;
	}
	public Boolean getINS200_FROM_INS132() {
		return INS200_FROM_INS132;
	}
	public void setINS200_FROM_INS132(Boolean iNS200_FROM_INS132) {
		INS200_FROM_INS132 = iNS200_FROM_INS132;
	}
	public BigDecimal getTYPE3_PARA_NO() {
		return TYPE3_PARA_NO;
	}
	public void setTYPE3_PARA_NO(BigDecimal tYPE3_PARA_NO) {
		TYPE3_PARA_NO = tYPE3_PARA_NO;
	}
	public String getParaNO() {
		return paraNO;
	}
	public void setParaNO(String paraNO) {
		this.paraNO = paraNO;
	}
	public String getPolicyNbr() {
		return policyNbr;
	}
	public void setPolicyNbr(String policyNbr) {
		this.policyNbr = policyNbr;
	}
	public String getComId() {
		return comId;
	}
	public void setComId(String comId) {
		this.comId = comId;
	}
	public String getPrdName() {
		return prdName;
	}
	public void setPrdName(String prdName) {
		this.prdName = prdName;
	}
	public BigDecimal getInsuredamt() {
		return insuredamt;
	}
	public void setInsuredamt(BigDecimal insuredamt) {
		this.insuredamt = insuredamt;
	}
	public BigDecimal getSecurityAmt() {
		return securityAmt;
	}
	public void setSecurityAmt(BigDecimal securityAmt) {
		this.securityAmt = securityAmt;
	}
	public String getUpqtySel() {
		return upqtySel;
	}
	public void setUpqtySel(String upqtySel) {
		this.upqtySel = upqtySel;
	}
	public BigDecimal getInyearfee() {
		return inyearfee;
	}
	public void setInyearfee(BigDecimal inyearfee) {
		this.inyearfee = inyearfee;
	}
	public BigDecimal getLocalInsyearfee() {
		return localInsyearfee;
	}
	public void setLocalInsyearfee(BigDecimal localInsyearfee) {
		this.localInsyearfee = localInsyearfee;
	}
	public BigDecimal getOtherInsured() {
		return otherInsured;
	}
	public void setOtherInsured(BigDecimal otherInsured) {
		this.otherInsured = otherInsured;
	}
	public String getInsPrdKeyNo() {
		return insPrdKeyNo;
	}
	public void setInsPrdKeyNo(String insPrdKeyNo) {
		this.insPrdKeyNo = insPrdKeyNo;
	}
	public BigDecimal getReInsuredamt() {
		return reInsuredamt;
	}
	public void setReInsuredamt(BigDecimal reInsuredamt) {
		this.reInsuredamt = reInsuredamt;
	}
	public BigDecimal getReInyearfee() {
		return reInyearfee;
	}
	public void setReInyearfee(BigDecimal reInyearfee) {
		this.reInyearfee = reInyearfee;
	}
	public String getCareWay() {
		return careWay;
	}
	public void setCareWay(String careWay) {
		this.careWay = careWay;
	}
	public String getCareStyle() {
		return careStyle;
	}
	public void setCareStyle(String careStyle) {
		this.careStyle = careStyle;
	}
	public BigDecimal getJobYears() {
		return jobYears;
	}
	public void setJobYears(BigDecimal jobYears) {
		this.jobYears = jobYears;
	}
	
}