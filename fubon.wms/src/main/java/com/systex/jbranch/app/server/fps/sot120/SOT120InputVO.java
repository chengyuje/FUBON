package com.systex.jbranch.app.server.fps.sot120;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author valentino
 *
 */
public class SOT120InputVO extends PagingInputVO {
	/*private String type;
	private String status;
	private String tradeSEQ;
	private String seqNo;
	private List<Map<String, Object>> custDTL;
	private List<Map<String, Object>> carList;*/
	
	// 共同 
		private String prodType;
		private String tradeType; 
		
		private String type;//ALL 給刪除用
		private String status;
		private String tradeSEQ;
		private BigDecimal seqNo;
		private List<Map<String, Object>> carList;
		
		private String isFirstTrade;   //是否首購
		private String ageUnder70Flag; //年齡小於70
		private String eduJrFlag;      //教育程度國中以上(不含國中)
		private String healthFlag;     //未領有全民健保重大傷病證明
		private String noSale;
		private String fatcaType;
		
		// 主檔使用
		private String custID;
		private String custName;
		private String agentID;
		private String agentName;
		private String kycLV;
		private Date kycDueDate;
		private String profInvestorYN;
		private Date piDueDate;
		private String custRemarks;
		private String isOBU;
		private String isAgreeProdAdv;
		private String piRemark; //專業投資人註記
		private Date bargainDueDate;
		private String tradeStatus;
		private String isBargainNeeded;
		private String bargainFeeFlag;
		private String isRecNeeded;
		private String recSEQ;
		private Date sendDate;
		private String custProType; // 專業投資人類型
		private String hnwcYN;
		private String hnwcServiceYN;
		
		private String tradeSubType;     //信託型態 
		private String prodId;// 商品代號  (搜尋/儲存)
		private String prodName; // 基金名稱
		private String prodCurr; // 計價幣別
		private String prodRiskLv; // 產品風險等級
		private String notVertify; //未核備欄位(TBPRD_FUNDINFO.FUS40)
		private BigDecimal prodMinBuyAmt; // 最低申購金額
		private String trustCurrType; // 信託幣別類別
		private String trustCurr; // 信託幣別
		private BigDecimal purchaseAmt; // 申購金額
		private BigDecimal purchaseAmtL; // 申購金額_低 (小額用)
		private BigDecimal purchaseAmtM; // 申購金額_中 (小額用)
		private BigDecimal purchaseAmtH; // 申購金額_高 (小額用)
		private String isAutoCx; // 是否自動換匯
		private String chargeDateList;
		/*private String chargeDate1; // 每月扣款日期1 (小額用)
		private String chargeDate2; // 每月扣款日期2 (小額用)
		private String chargeDate3; // 每月扣款日期3 (小額用)
		private String chargeDate4; // 每月扣款日期4 (小額用)
		private String chargeDate5; // 每月扣款日期5 (小額用)
		private String chargeDate6; // 每月扣款日期6 (小額用)
*/		
		private BigDecimal defaultFeeRate; // 表定手續費率
		
		private BigDecimal defaultFeeRateL; // 表定手續費率
		private BigDecimal defaultFeeRateM; // 表定手續費率
		private BigDecimal defaultFeeRateH; // 表定手續費率
		
		private String feeType; // 手續費優惠方式
		/*A：申請議價
		B：生日券使用
		C：最優手續費
		D：單次議價*/
		
		private String brgReason;	//議價原因
		private String bargainStatus; // 議價狀態 (若為申請議價)
	 
		private BigDecimal feeRate; // 手續費率
		private BigDecimal feeRateL; // 手續費率_低
		private BigDecimal feeRateM; // 手續費率_中
		private BigDecimal feeRateH; // 手續費率_高
		private BigDecimal fee; // 手續費金額
		private BigDecimal feeL; // 手續費金額_低 (小額用)
		private BigDecimal feeM; // 手續費金額_中 (小額用)
		private BigDecimal feeH; // 手續費金額_高 (小額用)
		private BigDecimal feeDiscount; // 手續費折數
		private BigDecimal feeDiscountL; // 手續費折數
		private BigDecimal feeDiscountM; // 手續費折數
		private BigDecimal feeDiscountH; // 手續費折數
		private String tradeDateType; // 交易日期類別
		private String tradeDate; // 交易日期
		private String reservationTradeDate;//預約交易日
	
		private String bargainApplySEQ; // 議價編號
		private String groupIfa;
		private String groupOfa;   //團體優惠代碼 (ESB DefaultFeeRateVO)
		 
		private String debitAcct; // 扣款帳號
		private String trustAcct; // 信託帳號
		private String creditAcct; // 收益入帳帳號
		private String prospectusType;  //公開說明書選項   1.已自行透過基金經理公司網站、貴行官網或境外基金資訊觀測站取得 2.已取得，本次毋須再交付
		
	
		private BigDecimal stopLossPerc; // 停損點
		private BigDecimal takeProfitPerc; // 停利點
		private String plNotifyWays; // 停損停利通知方式
		private String countryCode; // 交易市場別
		private String narratorID;
		private String narratorName;
		private String planID; //投組編號
		
		private String fundInfoSelling;//(XXXX)為本行建議售出之標的，請提醒客戶並確認其申購意願，謝謝！
		
		//WMS-CR-20191009-01_金錢信託套表需求申請單 2020-2-10 add by Jacky
		private String trustTS;				//M:金錢信託 S:特金交易
		private String contractID;			//契約編號
		private String trustPeopNum;		//是否為多委託人契約
		
		//0000275: 金錢信託受監護受輔助宣告交易控管調整
		private String GUARDIANSHIP_FLAG; //受監護輔助 空白：無監護輔助 1.監護宣告 2輔助宣告
		
		//金錢信託 定期定額相關
		private Boolean isTrueBargain = false;  //判斷真假議價 (選擇申請議價為真，其餘走假議價不覆核可套表)
		
		private String isWeb;				//是否為快速申購(Y/N)
		

		public String getIsWeb() {
			return isWeb;
		}
		public void setIsWeb(String isWeb) {
			this.isWeb = isWeb;
		}
		public Boolean getIsTrueBargain() {
			return isTrueBargain;
		}
		public void setIsTrueBargain(Boolean isTrueBargain) {
			this.isTrueBargain = isTrueBargain;
		}
		public String getGUARDIANSHIP_FLAG() {
			return GUARDIANSHIP_FLAG;
		}
		public void setGUARDIANSHIP_FLAG(String gUARDIANSHIP_FLAG) {
			GUARDIANSHIP_FLAG = gUARDIANSHIP_FLAG;
		}
		
		
		public String getTrustPeopNum() {
			return trustPeopNum;
		}
		public void setTrustPeopNum(String trustPeopNum) {
			this.trustPeopNum = trustPeopNum;
		}
		public String getTrustTS() {
			return trustTS;
		}
		public void setTrustTS(String trustTS) {
			this.trustTS = trustTS;
		}
		public String getContractID() {
			return contractID;
		}
		public void setContractID(String contractID) {
			this.contractID = contractID;
		}
		public String getTradeSubType() {
			return tradeSubType;
		}
		public void setTradeSubType(String tradeSubType) {
			this.tradeSubType = tradeSubType;
		}
		public String getProdType() {
			return prodType;
		}
		public void setProdType(String prodType) {
			this.prodType = prodType;
		}
		public String getTradeType() {
			return tradeType;
		}
		public void setTradeType(String tradeType) {
			this.tradeType = tradeType;
		}
		public String getType() {
			return type;
		}
		public void setType(String type) {
			this.type = type;
		}
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}
		public String getTradeSEQ() {
			return tradeSEQ;
		}
		public void setTradeSEQ(String tradeSEQ) {
			this.tradeSEQ = tradeSEQ;
		}
		public BigDecimal getSeqNo() {
			return seqNo;
		}
		public void setSeqNo(BigDecimal seqNo) {
			this.seqNo = seqNo;
		}
		public List<Map<String, Object>> getCarList() {
			return carList;
		}
		public void setCarList(List<Map<String, Object>> carList) {
			this.carList = carList;
		}
		public String getCustID() {
			return custID;
		}
		public void setCustID(String custID) {
			this.custID = custID;
		}
		public String getCustName() {
			return custName;
		}
		public void setCustName(String custName) {
			this.custName = custName;
		}
		public String getAgentID() {
			return agentID;
		}
		public void setAgentID(String agentID) {
			this.agentID = agentID;
		}
		public String getAgentName() {
			return agentName;
		}
		public void setAgentName(String agentName) {
			this.agentName = agentName;
		}
		public String getKycLV() {
			return kycLV;
		}
		public void setKycLV(String kycLV) {
			this.kycLV = kycLV;
		}
		public Date getKycDueDate() {
			return kycDueDate;
		}
		public void setKycDueDate(Date kycDueDate) {
			this.kycDueDate = kycDueDate;
		}
		public String getProfInvestorYN() {
			return profInvestorYN;
		}
		public void setProfInvestorYN(String profInvestorYN) {
			this.profInvestorYN = profInvestorYN;
		}
		public Date getPiDueDate() {
			return piDueDate;
		}
		public void setPiDueDate(Date piDueDate) {
			this.piDueDate = piDueDate;
		}
		public String getCustRemarks() {
			return custRemarks;
		}
		public void setCustRemarks(String custRemarks) {
			this.custRemarks = custRemarks;
		}
		public String getIsOBU() {
			return isOBU;
		}
		public void setIsOBU(String isOBU) {
			this.isOBU = isOBU;
		}
		public String getIsAgreeProdAdv() {
			return isAgreeProdAdv;
		}
		public void setIsAgreeProdAdv(String isAgreeProdAdv) {
			this.isAgreeProdAdv = isAgreeProdAdv;
		}
		public Date getBargainDueDate() {
			return bargainDueDate;
		}
		public void setBargainDueDate(Date bargainDueDate) {
			this.bargainDueDate = bargainDueDate;
		}
		public String getTradeStatus() {
			return tradeStatus;
		}
		public void setTradeStatus(String tradeStatus) {
			this.tradeStatus = tradeStatus;
		}
		public String getIsBargainNeeded() {
			return isBargainNeeded;
		}
		public void setIsBargainNeeded(String isBargainNeeded) {
			this.isBargainNeeded = isBargainNeeded;
		}
		public String getBargainFeeFlag() {
			return bargainFeeFlag;
		}
		public void setBargainFeeFlag(String bargainFeeFlag) {
			this.bargainFeeFlag = bargainFeeFlag;
		}
		public String getIsRecNeeded() {
			return isRecNeeded;
		}
		public void setIsRecNeeded(String isRecNeeded) {
			this.isRecNeeded = isRecNeeded;
		}
		public String getRecSEQ() {
			return recSEQ;
		}
		public void setRecSEQ(String recSEQ) {
			this.recSEQ = recSEQ;
		}
		public Date getSendDate() {
			return sendDate;
		}
		public void setSendDate(Date sendDate) {
			this.sendDate = sendDate;
		}
		public String getProdId() {
			return prodId;
		}
		public void setProdId(String prodId) {
			this.prodId = prodId;
		}
		public String getProdName() {
			return prodName;
		}
		public void setProdName(String prodName) {
			this.prodName = prodName;
		}
		public String getProdCurr() {
			return prodCurr;
		}
		public void setProdCurr(String prodCurr) {
			this.prodCurr = prodCurr;
		}
		public String getProdRiskLv() {
			return prodRiskLv;
		}
		public void setProdRiskLv(String prodRiskLv) {
			this.prodRiskLv = prodRiskLv;
		}
		public BigDecimal getProdMinBuyAmt() {
			return prodMinBuyAmt;
		}
		public void setProdMinBuyAmt(BigDecimal prodMinBuyAmt) {
			this.prodMinBuyAmt = prodMinBuyAmt;
		}
		public String getTrustCurrType() {
			return trustCurrType;
		}
		public void setTrustCurrType(String trustCurrType) {
			this.trustCurrType = trustCurrType;
		}
		public String getTrustCurr() {
			return trustCurr;
		}
		public void setTrustCurr(String trustCurr) {
			this.trustCurr = trustCurr;
		}
		public BigDecimal getPurchaseAmt() {
			return purchaseAmt;
		}
		public void setPurchaseAmt(BigDecimal purchaseAmt) {
			this.purchaseAmt = purchaseAmt;
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
		public String getIsAutoCx() {
			return isAutoCx;
		}
		public void setIsAutoCx(String isAutoCx) {
			this.isAutoCx = isAutoCx;
		}
		public String getChargeDateList() {
			return chargeDateList;
		}
		public void setChargeDateList(String chargeDateList) {
			this.chargeDateList = chargeDateList;
		}
		public BigDecimal getDefaultFeeRate() {
			return defaultFeeRate;
		}
		public void setDefaultFeeRate(BigDecimal defaultFeeRate) {
			this.defaultFeeRate = defaultFeeRate;
		}
		public String getFeeType() {
			return feeType;
		}
		public void setFeeType(String feeType) {
			this.feeType = feeType;
		}
		
		public String getBrgReason() {
			return brgReason;
		}
		public void setBrgReason(String brgReason) {
			this.brgReason = brgReason;
		}
		
		public String getBargainStatus() {
			return bargainStatus;
		}
		public void setBargainStatus(String bargainStatus) {
			this.bargainStatus = bargainStatus;
		}
		public BigDecimal getFeeRate() {
			return feeRate;
		}
		public void setFeeRate(BigDecimal feeRate) {
			this.feeRate = feeRate;
		}
		public BigDecimal getFee() {
			return fee;
		}
		public void setFee(BigDecimal fee) {
			this.fee = fee;
		}
		public BigDecimal getFeeL() {
			return feeL;
		}
		public void setFeeL(BigDecimal feeL) {
			this.feeL = feeL;
		}
		public BigDecimal getFeeM() {
			return feeM;
		}
		public void setFeeM(BigDecimal feeM) {
			this.feeM = feeM;
		}
		public BigDecimal getFeeH() {
			return feeH;
		}
		public void setFeeH(BigDecimal feeH) {
			this.feeH = feeH;
		}
		public BigDecimal getFeeDiscount() {
			return feeDiscount;
		}
		public void setFeeDiscount(BigDecimal feeDiscount) {
			this.feeDiscount = feeDiscount;
		}
		public String getTradeDateType() {
			return tradeDateType;
		}
		public void setTradeDateType(String tradeDateType) {
			this.tradeDateType = tradeDateType;
		}
		public String getTradeDate() {
			return tradeDate;
		}
		public void setTradeDate(String tradeDate) {
			this.tradeDate = tradeDate;
		}
		public String getBargainApplySEQ() {
			return bargainApplySEQ;
		}
		public void setBargainApplySEQ(String bargainApplySEQ) {
			this.bargainApplySEQ = bargainApplySEQ;
		}
		public String getDebitAcct() {
			return debitAcct;
		}
		public void setDebitAcct(String debitAcct) {
			this.debitAcct = debitAcct;
		}
		public String getTrustAcct() {
			return trustAcct;
		}
		public void setTrustAcct(String trustAcct) {
			this.trustAcct = trustAcct;
		}
		public String getCreditAcct() {
			return creditAcct;
		}
		public void setCreditAcct(String creditAcct) {
			this.creditAcct = creditAcct;
		}
		public BigDecimal getStopLossPerc() {
			return stopLossPerc;
		}
		public void setStopLossPerc(BigDecimal stopLossPerc) {
			this.stopLossPerc = stopLossPerc;
		}
		public BigDecimal getTakeProfitPerc() {
			return takeProfitPerc;
		}
		public void setTakeProfitPerc(BigDecimal takeProfitPerc) {
			this.takeProfitPerc = takeProfitPerc;
		}
		public String getPlNotifyWays() {
			return plNotifyWays;
		}
		public void setPlNotifyWays(String plNotifyWays) {
			this.plNotifyWays = plNotifyWays;
		}
		public String getCountryCode() {
			return countryCode;
		}
		public void setCountryCode(String countryCode) {
			this.countryCode = countryCode;
		}
		public String getNarratorID() {
			return narratorID;
		}
		public void setNarratorID(String narratorID) {
			this.narratorID = narratorID;
		}
		public String getNarratorName() {
			return narratorName;
		}
		public void setNarratorName(String narratorName) {
			this.narratorName = narratorName;
		}
		public String getGroupIfa() {
			return groupIfa;
		}
		public void setGroupIfa(String groupIfa) {
			this.groupIfa = groupIfa;
		}
		public String getGroupOfa() {
			return groupOfa;
		}
		public void setGroupOfa(String groupOfa) {
			this.groupOfa = groupOfa;
		}
		public String getFundInfoSelling() {
			return fundInfoSelling;
		}
		public void setFundInfoSelling(String fundInfoSelling) {
			this.fundInfoSelling = fundInfoSelling;
		}
		public BigDecimal getDefaultFeeRateL() {
			return defaultFeeRateL;
		}
		public void setDefaultFeeRateL(BigDecimal defaultFeeRateL) {
			this.defaultFeeRateL = defaultFeeRateL;
		}
		public BigDecimal getDefaultFeeRateM() {
			return defaultFeeRateM;
		}
		public void setDefaultFeeRateM(BigDecimal defaultFeeRateM) {
			this.defaultFeeRateM = defaultFeeRateM;
		}
		public BigDecimal getDefaultFeeRateH() {
			return defaultFeeRateH;
		}
		public void setDefaultFeeRateH(BigDecimal defaultFeeRateH) {
			this.defaultFeeRateH = defaultFeeRateH;
		}
		public BigDecimal getFeeRateL() {
			return feeRateL;
		}
		public void setFeeRateL(BigDecimal feeRateL) {
			this.feeRateL = feeRateL;
		}
		public BigDecimal getFeeRateM() {
			return feeRateM;
		}
		public void setFeeRateM(BigDecimal feeRateM) {
			this.feeRateM = feeRateM;
		}
		public BigDecimal getFeeRateH() {
			return feeRateH;
		}
		public void setFeeRateH(BigDecimal feeRateH) {
			this.feeRateH = feeRateH;
		}
		public BigDecimal getFeeDiscountL() {
			return feeDiscountL;
		}
		public void setFeeDiscountL(BigDecimal feeDiscountL) {
			this.feeDiscountL = feeDiscountL;
		}
		public BigDecimal getFeeDiscountM() {
			return feeDiscountM;
		}
		public void setFeeDiscountM(BigDecimal feeDiscountM) {
			this.feeDiscountM = feeDiscountM;
		}
		public BigDecimal getFeeDiscountH() {
			return feeDiscountH;
		}
		public void setFeeDiscountH(BigDecimal feeDiscountH) {
			this.feeDiscountH = feeDiscountH;
		}
		public String getCustProType() {
			return custProType;
		}
		public void setCustProType(String custProType) {
			this.custProType = custProType;
		}
		public String getIsFirstTrade() {
			return isFirstTrade;
		}
		public void setIsFirstTrade(String isFirstTrade) {
			this.isFirstTrade = isFirstTrade;
		}
		public String getAgeUnder70Flag() {
			return ageUnder70Flag;
		}
		public void setAgeUnder70Flag(String ageUnder70Flag) {
			this.ageUnder70Flag = ageUnder70Flag;
		}
		public String getEduJrFlag() {
			return eduJrFlag;
		}
		public void setEduJrFlag(String eduJrFlag) {
			this.eduJrFlag = eduJrFlag;
		}
		public String getHealthFlag() {
			return healthFlag;
		}
		public void setHealthFlag(String healthFlag) {
			this.healthFlag = healthFlag;
		}
		public String getNoSale() {
			return noSale;
		}
		public void setNoSale(String noSale) {
			this.noSale = noSale;
		}
		public String getFatcaType() {
			return fatcaType;
		}
		public void setFatcaType(String fatcaType) {
			this.fatcaType = fatcaType;
		}
		public String getPiRemark() {
			return piRemark;
		}
		public void setPiRemark(String piRemark) {
			this.piRemark = piRemark;
		}
		public String getNotVertify() {
			return notVertify;
		}
		public void setNotVertify(String notVertify) {
			this.notVertify = notVertify;
		}
		public String getProspectusType() {
			return prospectusType;
		}
		public void setProspectusType(String prospectusType) {
			this.prospectusType = prospectusType;
		}
		public String getReservationTradeDate() {
			return reservationTradeDate;
		}
		public void setReservationTradeDate(String reservationTradeDate) {
			this.reservationTradeDate = reservationTradeDate;
		}
		
		public String getPlanID() {
			return planID;
		}
		public void setPlanID(String planID) {
			this.planID = planID;
		}
		public String getHnwcYN() {
			return hnwcYN;
		}
		public void setHnwcYN(String hnwcYN) {
			this.hnwcYN = hnwcYN;
		}
		public String getHnwcServiceYN() {
			return hnwcServiceYN;
		}
		public void setHnwcServiceYN(String hnwcServiceYN) {
			this.hnwcServiceYN = hnwcServiceYN;
		}
		
}
