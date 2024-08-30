package com.systex.jbranch.app.server.fps.sot709;

import java.math.BigDecimal;

public class DefaultFeeRateVO {
	
	private BigDecimal defaultFeeRateL;//表定手續費(低) / 表定手續費
	private BigDecimal defaultFeeRateM;//表定手續費(中)
	private BigDecimal defaultFeeRateH;//表定手續費(高)
	private BigDecimal feeL;//最優手續費(低) / 手續費
	private BigDecimal feeRateL;//最優手續費率(低) / 手續費率
	private BigDecimal feeM;//最優手續費(中)
	private BigDecimal feeRateM;//最優手續費率(中)
	private BigDecimal feeH;//最優手續費(高)
	private BigDecimal feeRateH;//最優手續費率(高)
	private String trustCurr;//投資幣別
	private String groupOfa;//團體優惠代碼
	
	public BigDecimal getDefaultFeeRateL() {
		return defaultFeeRateL;
	}
	public BigDecimal getDefaultFeeRateM() {
		return defaultFeeRateM;
	}
	public BigDecimal getDefaultFeeRateH() {
		return defaultFeeRateH;
	}
	public BigDecimal getFeeL() {
		return feeL;
	}
	public BigDecimal getFeeRateL() {
		return feeRateL;
	}
	public BigDecimal getFeeM() {
		return feeM;
	}
	public BigDecimal getFeeRateM() {
		return feeRateM;
	}
	public BigDecimal getFeeH() {
		return feeH;
	}
	public BigDecimal getFeeRateH() {
		return feeRateH;
	}
	public String getTrustCurr() {
		return trustCurr;
	}
	public String getGroupOfa() {
		return groupOfa;
	}
	public void setDefaultFeeRateL(BigDecimal defaultFeeRateL) {
		this.defaultFeeRateL = defaultFeeRateL;
	}
	public void setDefaultFeeRateM(BigDecimal defaultFeeRateM) {
		this.defaultFeeRateM = defaultFeeRateM;
	}
	public void setDefaultFeeRateH(BigDecimal defaultFeeRateH) {
		this.defaultFeeRateH = defaultFeeRateH;
	}
	public void setFeeL(BigDecimal feeL) {
		this.feeL = feeL;
	}
	public void setFeeRateL(BigDecimal feeRateL) {
		this.feeRateL = feeRateL;
	}
	public void setFeeM(BigDecimal feeM) {
		this.feeM = feeM;
	}
	public void setFeeRateM(BigDecimal feeRateM) {
		this.feeRateM = feeRateM;
	}
	public void setFeeH(BigDecimal feeH) {
		this.feeH = feeH;
	}
	public void setFeeRateH(BigDecimal feeRateH) {
		this.feeRateH = feeRateH;
	}
	public void setTrustCurr(String trustCurr) {
		this.trustCurr = trustCurr;
	}
	public void setGroupOfa(String groupOfa) {
		this.groupOfa = groupOfa;
	}
	
	

	
	
}
