package com.systex.jbranch.app.server.fps.crm828;

import java.math.BigDecimal;
import java.util.Date;

import javax.xml.bind.annotation.XmlElement;

/**
 * 
 * @author sam
 * @date 2018/01/19
 *
 */
public class CustAssetGD320140 {

	private String ACNO;
	private BigDecimal BAL;
	private String CUR;
	private BigDecimal BRD_PRICE;
	private BigDecimal AVG_COST;
	private BigDecimal P_VALUE;
	private BigDecimal INV_AMT;
	private BigDecimal YIELD_AMT;
	
	private String YIELD_S;
	private BigDecimal YIELD;
	
	private String M_STS;
	private String M_DATE;
	private BigDecimal M_AMT;
		
	public String getMSG_ID() {
		return MSG_ID;
	}
	public void setMSG_ID(String mSG_ID) {
		MSG_ID = mSG_ID;
	}
	public String getMSG_TXT() {
		return MSG_TXT;
	}
	public void setMSG_TXT(String mSG_TXT) {
		MSG_TXT = mSG_TXT;
	}
	private String MSG_ID;
	private String MSG_TXT;
	
	public String getACNO() {
		return ACNO;
	}
	public void setACNO(String aCNO) {
		ACNO = aCNO;
	}
	public BigDecimal getBAL() {
		return BAL;
	}
	public void setBAL(BigDecimal bAL) {
		BAL = bAL;
	}
	public String getCUR() {
		return CUR;
	}
	public void setCUR(String cUR) {
		CUR = cUR;
	}
	public BigDecimal getBRD_PRICE() {
		return BRD_PRICE;
	}
	public void setBRD_PRICE(BigDecimal bRD_PRICE) {
		BRD_PRICE = bRD_PRICE;
	}
	public BigDecimal getAVG_COST() {
		return AVG_COST;
	}
	public void setAVG_COST(BigDecimal aVG_COST) {
		AVG_COST = aVG_COST;
	}
	public BigDecimal getP_VALUE() {
		return P_VALUE;
	}
	public void setP_VALUE(BigDecimal p_VALUE) {
		P_VALUE = p_VALUE;
	}
	public BigDecimal getINV_AMT() {
		return INV_AMT;
	}
	public void setINV_AMT(BigDecimal iNV_AMT) {
		INV_AMT = iNV_AMT;
	}
	public BigDecimal getYIELD_AMT() {
		return YIELD_AMT;
	}
	public void setYIELD_AMT(BigDecimal yIELD_AMT) {
		YIELD_AMT = yIELD_AMT;
	}
	public String getM_STS() {
		return M_STS;
	}
	public void setM_STS(String m_STS) {
		M_STS = m_STS;
	}
	public String getM_DATE() {
		return M_DATE;
	}
	public void setM_DATE(String m_DATE) {
		M_DATE = m_DATE;
	}
	public BigDecimal getM_AMT() {
		return M_AMT;
	}
	public void setM_AMT(BigDecimal m_AMT) {
		M_AMT = m_AMT;
	}
	public String getYIELD_S() {
		return YIELD_S;
	}
	public void setYIELD_S(String yIELD_S) {
		YIELD_S = yIELD_S;
	}
	public BigDecimal getYIELD() {
		return YIELD;
	}
	public void setYIELD(BigDecimal yield) {
		YIELD = yield;
	}


	
}
