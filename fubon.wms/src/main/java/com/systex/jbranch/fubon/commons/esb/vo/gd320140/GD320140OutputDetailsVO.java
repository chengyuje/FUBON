package com.systex.jbranch.fubon.commons.esb.vo.gd320140;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlElement;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * 
 * @author sam
 *
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType
@Component
@Scope("request")
public class GD320140OutputDetailsVO {

	@XmlElement	// 黃金帳號
	private String ACNO;
	@XmlElement	// 幣別
	private String CUR;
	@XmlElement	// 庫存單位
	private String BAL;
	@XmlElement	// 參考牌價(最新且有效)
	private String BRD_PRICE;
	@XmlElement	// 牌價日期
	private String BRD_DATE;
	@XmlElement	// 牌價時間
	private String BRD_TIME;
	@XmlElement	// 參考現值
	private String P_VALUE;
	@XmlElement	// 平均成本(每單位成本)
	private String AVG_COST;
	@XmlElement	// 損益正負
	private String YIELD_S;
	@XmlElement	// 參考損益金額
	private String YIELD_AMT;
	@XmlElement	// 總投資金額(平均成本*庫存)
	private String INV_AMT;
	@XmlElement	// 參考損益%
	private String YIELD;
	@XmlElement	// 約定扣款日期
	private String M_DATE;
	@XmlElement	// 定期定額每期扣款金額
	private String M_AMT;
	@XmlElement	// 定期定額狀態代碼
	private String M_STS_COD;
	@XmlElement	// 定期定額狀態中文
	private String M_STS;
	@XmlElement	// 定期定額手續費
	private String M_FEE_AMT;
	@XmlElement	// 法扣單位
	private String DETA_AMT;
	@XmlElement	// 申請開戶日
	private String OPN_DATE;
	@XmlElement	// 上次異動日V(最後一次事件變更)
	private String MTN_DATE;
	@XmlElement	// 計價單位(公克、盎司)
	private String UNIT;
	
	public String getACNO() {
		return ACNO;
	}
	public void setACNO(String aCNO) {
		ACNO = aCNO;
	}
	public String getCUR() {
		return CUR;
	}
	public void setCUR(String cUR) {
		CUR = cUR;
	}
	public String getBAL() {
		return BAL;
	}
	public void setBAL(String bAL) {
		BAL = bAL;
	}
	public String getBRD_PRICE() {
		return BRD_PRICE;
	}
	public void setBRD_PRICE(String bRD_PRICE) {
		BRD_PRICE = bRD_PRICE;
	}
	public String getBRD_DATE() {
		return BRD_DATE;
	}
	public void setBRD_DATE(String bRD_DATE) {
		BRD_DATE = bRD_DATE;
	}
	public String getBRD_TIME() {
		return BRD_TIME;
	}
	public void setBRD_TIME(String bRD_TIME) {
		BRD_TIME = bRD_TIME;
	}
	public String getP_VALUE() {
		return P_VALUE;
	}
	public void setP_VALUE(String p_VALUE) {
		P_VALUE = p_VALUE;
	}
	public String getAVG_COST() {
		return AVG_COST;
	}
	public void setAVG_COST(String aVG_COST) {
		AVG_COST = aVG_COST;
	}
	public String getYIELD_S() {
		return YIELD_S;
	}
	public void setYIELD_S(String yIELD_S) {
		YIELD_S = yIELD_S;
	}
	public String getYIELD_AMT() {
		return YIELD_AMT;
	}
	public void setYIELD_AMT(String yIELD_AMT) {
		YIELD_AMT = yIELD_AMT;
	}
	public String getINV_AMT() {
		return INV_AMT;
	}
	public void setINV_AMT(String iNV_AMT) {
		INV_AMT = iNV_AMT;
	}
	public String getYIELD() {
		return YIELD;
	}
	public void setYIELD(String yIELD) {
		YIELD = yIELD;
	}
	public String getM_DATE() {
		return M_DATE;
	}
	public void setM_DATE(String m_DATE) {
		M_DATE = m_DATE;
	}
	public String getM_AMT() {
		return M_AMT;
	}
	public void setM_AMT(String m_AMT) {
		M_AMT = m_AMT;
	}
	public String getM_STS_COD() {
		return M_STS_COD;
	}
	public void setM_STS_COD(String m_STS_COD) {
		M_STS_COD = m_STS_COD;
	}
	public String getM_STS() {
		return M_STS;
	}
	public void setM_STS(String m_STS) {
		M_STS = m_STS;
	}
	public String getM_FEE_AMT() {
		return M_FEE_AMT;
	}
	public void setM_FEE_AMT(String m_FEE_AMT) {
		M_FEE_AMT = m_FEE_AMT;
	}
	public String getDETA_AMT() {
		return DETA_AMT;
	}
	public void setDETA_AMT(String dETA_AMT) {
		DETA_AMT = dETA_AMT;
	}
	public String getOPN_DATE() {
		return OPN_DATE;
	}
	public void setOPN_DATE(String oPN_DATE) {
		OPN_DATE = oPN_DATE;
	}
	public String getMTN_DATE() {
		return MTN_DATE;
	}
	public void setMTN_DATE(String mTN_DATE) {
		MTN_DATE = mTN_DATE;
	}
	public String getUNIT() {
		return UNIT;
	}
	public void setUNIT(String uNIT) {
		UNIT = uNIT;
	}

}