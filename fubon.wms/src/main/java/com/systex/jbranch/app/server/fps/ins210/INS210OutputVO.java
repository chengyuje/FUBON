package com.systex.jbranch.app.server.fps.ins210;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class INS210OutputVO extends PagingOutputVO{
	
	private List<Map<String, Object>> planList;
	private List<Map<String, Object>> suggestList;
	private BigDecimal PROJECT_GAP_ONCE1;
	private BigDecimal PROJECT_GAP_ONCE2;
	private BigDecimal PROJECT_GAP_ONCE3;
	private BigDecimal PROJECT_GAP_ONCE4;
	
	private BigDecimal POLICY_ASSURE_AMT;
	
	
	private BigDecimal SHD_PROTECT1;		//應備總金額
	private BigDecimal NOW_PROTECT1;		//已備總金額
	private BigDecimal SHD_PROTECT2;		//應備癌症住院日額
	private BigDecimal NOW_PROTECT2;		//已備癌症住院日額
	private BigDecimal SHD_PROTECT3;		//應備長期看護每月
	private BigDecimal NOW_PROTECT3;		//已備長期看護每月

//	private BigDecimal PROJECT_GAP1;		//缺口
//	private BigDecimal PROJECT_GAP2;		//住院日額缺口
//	private BigDecimal PROJECT_GAP3;		//看護每月缺口
	
	
	private List<Map<String, Object>> sendInsuredList;
	private String PLAN_KEYNO;
	private String PLAN_D_KEYNO;
	private String INSPRD_KEYNO;
	private String SICKROOM_FEE;	//醫院與病房種類KEYNO
	private String NURSE_FEE_PAY;	//長期照護照顧方式KEYNO
	
	private BigDecimal HOSPIHALDAYS;	//住院日額
	private BigDecimal MONTH_AMT;		//長期看護(月)
	private BigDecimal premRate;
	private Map<String, BigDecimal> refExcRate;
	

	public List<Map<String, Object>> getPlanList() {
		return planList;
	}

	public List<Map<String, Object>> getSuggestList() {
		return suggestList;
	}

	public BigDecimal getPROJECT_GAP_ONCE1() {
		return PROJECT_GAP_ONCE1;
	}

	public BigDecimal getPROJECT_GAP_ONCE2() {
		return PROJECT_GAP_ONCE2;
	}

	public BigDecimal getPROJECT_GAP_ONCE3() {
		return PROJECT_GAP_ONCE3;
	}

	public BigDecimal getPROJECT_GAP_ONCE4() {
		return PROJECT_GAP_ONCE4;
	}

	public BigDecimal getPOLICY_ASSURE_AMT() {
		return POLICY_ASSURE_AMT;
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

	public List<Map<String, Object>> getSendInsuredList() {
		return sendInsuredList;
	}

	public String getPLAN_KEYNO() {
		return PLAN_KEYNO;
	}

	public String getPLAN_D_KEYNO() {
		return PLAN_D_KEYNO;
	}

	public String getINSPRD_KEYNO() {
		return INSPRD_KEYNO;
	}

	public String getSICKROOM_FEE() {
		return SICKROOM_FEE;
	}

	public BigDecimal getHOSPIHALDAYS() {
		return HOSPIHALDAYS;
	}

	public void setPlanList(List<Map<String, Object>> planList) {
		this.planList = planList;
	}

	public void setSuggestList(List<Map<String, Object>> suggestList) {
		this.suggestList = suggestList;
	}

	public void setPROJECT_GAP_ONCE1(BigDecimal pROJECT_GAP_ONCE1) {
		PROJECT_GAP_ONCE1 = pROJECT_GAP_ONCE1;
	}

	public void setPROJECT_GAP_ONCE2(BigDecimal pROJECT_GAP_ONCE2) {
		PROJECT_GAP_ONCE2 = pROJECT_GAP_ONCE2;
	}

	public void setPROJECT_GAP_ONCE3(BigDecimal pROJECT_GAP_ONCE3) {
		PROJECT_GAP_ONCE3 = pROJECT_GAP_ONCE3;
	}

	public void setPROJECT_GAP_ONCE4(BigDecimal pROJECT_GAP_ONCE4) {
		PROJECT_GAP_ONCE4 = pROJECT_GAP_ONCE4;
	}

	public void setPOLICY_ASSURE_AMT(BigDecimal pOLICY_ASSURE_AMT) {
		POLICY_ASSURE_AMT = pOLICY_ASSURE_AMT;
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

	public void setSendInsuredList(List<Map<String, Object>> sendInsuredList) {
		this.sendInsuredList = sendInsuredList;
	}

	public void setPLAN_KEYNO(String pLAN_KEYNO) {
		PLAN_KEYNO = pLAN_KEYNO;
	}

	public void setPLAN_D_KEYNO(String pLAN_D_KEYNO) {
		PLAN_D_KEYNO = pLAN_D_KEYNO;
	}

	public void setINSPRD_KEYNO(String iNSPRD_KEYNO) {
		INSPRD_KEYNO = iNSPRD_KEYNO;
	}

	public void setSICKROOM_FEE(String sICKROOM_FEE) {
		SICKROOM_FEE = sICKROOM_FEE;
	}

	public void setHOSPIHALDAYS(BigDecimal hOSPIHALDAYS) {
		HOSPIHALDAYS = hOSPIHALDAYS;
	}

	public String getNURSE_FEE_PAY() {
		return NURSE_FEE_PAY;
	}

	public BigDecimal getMONTH_AMT() {
		return MONTH_AMT;
	}

	public void setNURSE_FEE_PAY(String nURSE_FEE_PAY) {
		NURSE_FEE_PAY = nURSE_FEE_PAY;
	}

	public void setMONTH_AMT(BigDecimal mONTH_AMT) {
		MONTH_AMT = mONTH_AMT;
	}

	public BigDecimal getPremRate() {
		return premRate;
	}

	public void setPremRate(BigDecimal premRate) {
		this.premRate = premRate;
	}

	public Map<String, BigDecimal> getRefExcRate() {
		return refExcRate;
	}

	public void setRefExcRate(Map<String, BigDecimal> refExcRate) {
		this.refExcRate = refExcRate;
	}

	
	
	
}
