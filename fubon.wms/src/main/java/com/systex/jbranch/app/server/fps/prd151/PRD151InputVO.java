package com.systex.jbranch.app.server.fps.prd151;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class PRD151InputVO extends PagingInputVO{
	private BigDecimal SEQ_NO;
	private String CURR_ID;
	private String RISKCATE_ID;
	private BigDecimal REF_PRICE_Y;
	private BigDecimal MIN_UF;
	private BigDecimal BASE_AMT;
	private BigDecimal UNIT_AMT;
	private BigDecimal TRADER_CHARGE;
	private BigDecimal STRIKE_PRICE;
	private String TARGET_CURR_ID;
	private Date EFFECTIVE_DATE;
	private String saveType;
	private String monType;
	private BigDecimal C_PRD_PROFEE;
	private String C_CURR_ID;
	private String kycLV;
	
	private String PRICE_REMARK;
	private String P_START_HOUR;
	private String P_START_MIN;
	private String P_END_HOUR;
	private String P_END_MIN;
	private String T_START_HOUR;
	private String T_START_MIN;
	private String T_END_HOUR;
	private String T_END_MIN;
	
	public BigDecimal getSEQ_NO() {
		return SEQ_NO;
	}
	public void setSEQ_NO(BigDecimal sEQ_NO) {
		SEQ_NO = sEQ_NO;
	}
	public String getCURR_ID() {
		return CURR_ID;
	}
	public void setCURR_ID(String cURR_ID) {
		CURR_ID = cURR_ID;
	}
	public String getRISKCATE_ID() {
		return RISKCATE_ID;
	}
	public void setRISKCATE_ID(String rISKCATE_ID) {
		RISKCATE_ID = rISKCATE_ID;
	}
	public BigDecimal getREF_PRICE_Y() {
		return REF_PRICE_Y;
	}
	public void setREF_PRICE_Y(BigDecimal rEF_PRICE_Y) {
		REF_PRICE_Y = rEF_PRICE_Y;
	}
	public BigDecimal getMIN_UF() {
		return MIN_UF;
	}
	public void setMIN_UF(BigDecimal mIN_UF) {
		MIN_UF = mIN_UF;
	}
	public BigDecimal getBASE_AMT() {
		return BASE_AMT;
	}
	public void setBASE_AMT(BigDecimal bASE_AMT) {
		BASE_AMT = bASE_AMT;
	}
	public BigDecimal getUNIT_AMT() {
		return UNIT_AMT;
	}
	public void setUNIT_AMT(BigDecimal uNIT_AMT) {
		UNIT_AMT = uNIT_AMT;
	}
	public BigDecimal getTRADER_CHARGE() {
		return TRADER_CHARGE;
	}
	public void setTRADER_CHARGE(BigDecimal tRADER_CHARGE) {
		TRADER_CHARGE = tRADER_CHARGE;
	}
	public BigDecimal getSTRIKE_PRICE() {
		return STRIKE_PRICE;
	}
	public void setSTRIKE_PRICE(BigDecimal sTRIKE_PRICE) {
		STRIKE_PRICE = sTRIKE_PRICE;
	}
	public String getTARGET_CURR_ID() {
		return TARGET_CURR_ID;
	}
	public void setTARGET_CURR_ID(String tARGET_CURR_ID) {
		TARGET_CURR_ID = tARGET_CURR_ID;
	}
	public Date getEFFECTIVE_DATE() {
		return EFFECTIVE_DATE;
	}
	public void setEFFECTIVE_DATE(Date eFFECTIVE_DATE) {
		EFFECTIVE_DATE = eFFECTIVE_DATE;
	}
	public String getSaveType() {
		return saveType;
	}
	public void setSaveType(String saveType) {
		this.saveType = saveType;
	}
	public String getMonType() {
		return monType;
	}
	public void setMonType(String monType) {
		this.monType = monType;
	}
	public BigDecimal getC_PRD_PROFEE() {
		return C_PRD_PROFEE;
	}
	public void setC_PRD_PROFEE(BigDecimal c_PRD_PROFEE) {
		C_PRD_PROFEE = c_PRD_PROFEE;
	}
	public String getC_CURR_ID() {
		return C_CURR_ID;
	}
	public void setC_CURR_ID(String c_CURR_ID) {
		C_CURR_ID = c_CURR_ID;
	}
	public String getKycLV() {
		return kycLV;
	}
	public void setKycLV(String kycLV) {
		this.kycLV = kycLV;
	}
	public String getPRICE_REMARK() {
		return PRICE_REMARK;
	}
	public void setPRICE_REMARK(String pRICE_REMARK) {
		PRICE_REMARK = pRICE_REMARK;
	}
	public String getP_START_HOUR() {
		return P_START_HOUR;
	}
	public void setP_START_HOUR(String p_START_HOUR) {
		P_START_HOUR = p_START_HOUR;
	}
	public String getP_START_MIN() {
		return P_START_MIN;
	}
	public void setP_START_MIN(String p_START_MIN) {
		P_START_MIN = p_START_MIN;
	}
	public String getP_END_HOUR() {
		return P_END_HOUR;
	}
	public void setP_END_HOUR(String p_END_HOUR) {
		P_END_HOUR = p_END_HOUR;
	}
	public String getP_END_MIN() {
		return P_END_MIN;
	}
	public void setP_END_MIN(String p_END_MIN) {
		P_END_MIN = p_END_MIN;
	}
	public String getT_START_HOUR() {
		return T_START_HOUR;
	}
	public void setT_START_HOUR(String t_START_HOUR) {
		T_START_HOUR = t_START_HOUR;
	}
	public String getT_START_MIN() {
		return T_START_MIN;
	}
	public void setT_START_MIN(String t_START_MIN) {
		T_START_MIN = t_START_MIN;
	}
	public String getT_END_HOUR() {
		return T_END_HOUR;
	}
	public void setT_END_HOUR(String t_END_HOUR) {
		T_END_HOUR = t_END_HOUR;
	}
	public String getT_END_MIN() {
		return T_END_MIN;
	}
	public void setT_END_MIN(String t_END_MIN) {
		T_END_MIN = t_END_MIN;
	}
		
}
