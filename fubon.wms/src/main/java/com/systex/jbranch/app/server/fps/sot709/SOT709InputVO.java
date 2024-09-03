package com.systex.jbranch.app.server.fps.sot709;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

import java.math.BigDecimal;
import java.util.Date;

public class SOT709InputVO extends PagingInputVO{
	
	private String custId;//客戶ID 
	private String branchNbr;//分行別(信託行)
	private String trustCurrType;//信託業務別
	private String tradeSubType;//交易類別
	private String prodId;//基金代號/基金套餐
	private String groupIfa;//優惠團體代碼
	private BigDecimal purchaseAmtL;//扣款金額低/扣款金額
	private BigDecimal purchaseAmtM;//扣款金額中
	private BigDecimal purchaseAmtH;//扣款金額高
	private String bthCoupon;//是否使用生日券
	private String autoCx;//自動換匯
	private String bargainApplySeq;//議價編號
	private Date startDate;//查詢起日
	private Date endDate;//查詢迄日
	private String type;//查詢狀態(期間議價)
	private String applySeq;//議價編號
	private String checkCode;//交易項目
	private boolean isNoUsedOnly;//是否回傳未使用過單次議價
	private BigDecimal purchaseAmt; //申購金額
	private String trustCurr; //信託幣別
	private Date tradeDate;//交易日期
	private String proCode; //專案碼
	private String trustAcct; //信託帳號
	private String trustTS;				//M:金錢信託 S:特金交易
	private String dynamicYN; //動態鎖利 Y/N

	private BigDecimal fee_rate;//手續費率

	public String getTrustTS() {
		return trustTS;
	}
	public void setTrustTS(String trustTS) {
		this.trustTS = trustTS;
	}
	public boolean isNoUsedOnly() {
		return isNoUsedOnly;
	}
	public void setNoUsedOnly(boolean isNoUsedOnly) {
		this.isNoUsedOnly = isNoUsedOnly;
	}
	public String getAutoCx() {
		return autoCx;
	}
	public void setAutoCx(String autoCx) {
		this.autoCx = autoCx;
	}
	public String getCustId() {
		return custId;
	}
	public void setCustId(String custId) {
		this.custId = custId;
	}
	public String getBranchNbr() {
		return branchNbr;
	}
	public void setBranchNbr(String branchNbr) {
		this.branchNbr = branchNbr;
	}
	public String getTrustCurrType() {
		return trustCurrType;
	}
	public void setTrustCurrType(String trustCurrType) {
		this.trustCurrType = trustCurrType;
	}
	public String getTradeSubType() {
		return tradeSubType;
	}
	public void setTradeSubType(String tradeSubType) {
		this.tradeSubType = tradeSubType;
	}
	public String getProdId() {
		return prodId;
	}
	public void setProdId(String prodId) {
		this.prodId = prodId;
	}
	public String getGroupIfa() {
		return groupIfa;
	}
	public void setGroupIfa(String groupIfa) {
		this.groupIfa = groupIfa;
	}
	public BigDecimal getPurchaseAmtL() {
		return purchaseAmtL;
	}
	public void setPurchaseAmtL(BigDecimal purchaseAmtL) {
		this.purchaseAmtL = purchaseAmtL;
	}
	public BigDecimal getPurchaseAmtM() {
		return purchaseAmtM;
	}
	public void setPurchaseAmtM(BigDecimal purchaseAmtM) {
		this.purchaseAmtM = purchaseAmtM;
	}
	public BigDecimal getPurchaseAmtH() {
		return purchaseAmtH;
	}
	public void setPurchaseAmtH(BigDecimal purchaseAmtH) {
		this.purchaseAmtH = purchaseAmtH;
	}
	public String getBthCoupon() {
		return bthCoupon;
	}
	public void setBthCoupon(String bthCoupon) {
		this.bthCoupon = bthCoupon;
	}
	public String getBargainApplySeq() {
		return bargainApplySeq;
	}
	public void setBargainApplySeq(String bargainApplySeq) {
		this.bargainApplySeq = bargainApplySeq;
	}
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getApplySeq() {
		return applySeq;
	}
	public void setApplySeq(String applySeq) {
		this.applySeq = applySeq;
	}
	public String getCheckCode() {
		return checkCode;
	}
	public void setCheckCode(String checkCode) {
		this.checkCode = checkCode;
	}

	public BigDecimal getPurchaseAmt() {
		return purchaseAmt;
	}

	public void setPurchaseAmt(BigDecimal purchaseAmt) {
		this.purchaseAmt = purchaseAmt;
	}

	public String getTrustCurr() {
		return trustCurr;
	}

	public void setTrustCurr(String trustCurr) {
		this.trustCurr = trustCurr;
	}
	public Date getTradeDate() {
		return tradeDate;
	}
	public void setTradeDate(Date tradeDate) {
		this.tradeDate = tradeDate;
	}
	public String getProCode() {
		return proCode;
	}
	public void setProCode(String proCode) {
		this.proCode = proCode;
	}
	public String getTrustAcct() {
		return trustAcct;
	}
	public void setTrustAcct(String trustAcct) {
		this.trustAcct = trustAcct;
	}
	public BigDecimal getFee_rate() {
		return fee_rate;
	}
	public void setFee_rate(BigDecimal fee_rate) {
		this.fee_rate = fee_rate;
	}
	public String getDynamicYN() {
		return dynamicYN;
	}
	public void setDynamicYN(String dynamicYN) {
		this.dynamicYN = dynamicYN;
	}



}
