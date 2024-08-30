package com.systex.jbranch.fubon.commons.esb.vo.nfbrx9;

import javax.xml.bind.annotation.XmlElement;

public class NFBRX9OutputVODetailsVO {

	@XmlElement
	private String SPRefId;
	@XmlElement
	private String AcctId16;
	@XmlElement
	private String Occur;

	// 單筆
	@XmlElement
	private String AcctId02;
	@XmlElement
	private String EviNum;
	@XmlElement
	private String FundNO;
	@XmlElement
	private String FundName;
	@XmlElement
	private String CurFund;
	@XmlElement
	private String CurCode;
	@XmlElement
	private String CurAmt;
	@XmlElement
	private String CurAmtNT;
	@XmlElement
	private String CurBal;
	@XmlElement
	private String CurBalNT;
	@XmlElement
	private String ProfitAndLoss;
	@XmlElement
	private String Increase;
	@XmlElement
	private String SignDigit;
	@XmlElement
	private String Return;
	@XmlElement
	private String RewRateDigit;
	@XmlElement
	private String AccAllocateRewRate;
	@XmlElement
	private String CurUntNum;
	@XmlElement
	private String ReferenceExchangeRate;
	@XmlElement
	private String NetValueDate;
	@XmlElement
	private String NetValue;
	@XmlElement
	private String StoplossSign;
	@XmlElement
	private String Stoploss;
	@XmlElement
	private String SatisfiedSign;
	@XmlElement
	private String Satisfied;
	@XmlElement
	private String Strdate;
	@XmlElement
	private String FundType;
	@XmlElement
	private String ApproveFlag;
	@XmlElement
	private String ProjectCode;
	@XmlElement
	private String GroupCode;
	@XmlElement
	private String PayAcctId;
	@XmlElement
	private String AccAllocateRew;
	@XmlElement
	private String MK_CREDIT_FALG;

	//定期定額
	@XmlElement
	private String TransferAmt;
	@XmlElement
	private String TransferDate01;
	@XmlElement
	private String TransferDate02;
	@XmlElement
	private String TransferDate03;
	@XmlElement
	private String TransferDate04;
	@XmlElement
	private String TransferDate05;
	@XmlElement
	private String TransferDate06;
	@XmlElement
	private String TransferCount;
	@XmlElement
	private String PayCount;
	@XmlElement
	private String Status;
	@XmlElement
	private String PayAccountNo;
	@XmlElement
	private String TxType;
	@XmlElement
	private String FrgnPurchaseFlag;
	@XmlElement
	private String Same;
	@XmlElement
	private String DiscountAmt;

	// 定期(不)定額
	@XmlElement
	private String TransferAmt_H;
	@XmlElement
	private String TransferAmt_M;
	@XmlElement
	private String TransferAmt_L;

	// 定存轉基金
	@XmlElement
	private String AcctId;
	@XmlElement
	private String TimeDepositPrjCd;
	@XmlElement
	private String Total_Cnt;
	@XmlElement
	private String Pay_Cnt;
	@XmlElement
	private String End_Flg;

	// 基金套餐
	@XmlElement
	private String FundPackageNo;
	@XmlElement
	private String FundPackage;

	// 申贖在途
	@XmlElement
	private String EffDate;
	@XmlElement
	private String FeeCurCode;
	@XmlElement
	private String Fee;
	@XmlElement
	private String TransferCurCode;
	@XmlElement
	private String TransferAmtNT;
	@XmlElement
	private String TrustType;

	// 轉換在途
	@XmlElement
	private String OutFundNO;
	@XmlElement
	private String OutFundName;
	@XmlElement
	private String InFundNO;
	@XmlElement
	private String InFundName;
	@XmlElement
	private String OutCurFund;
	@XmlElement
	private String OutCurCode;
	@XmlElement
	private String InCurFund;
	@XmlElement
	private String InCurCode;
	@XmlElement
	private String OutNetValueDate;
	@XmlElement
	private String OutNetValue;
	@XmlElement
	private String InNetValueDate;
	@XmlElement
	private String InNetValue;

	// 續回在途
	@XmlElement
	private String PchFundNO;
	@XmlElement
	private String PchFundName;
	@XmlElement
	private String PchCurCode;
	@XmlElement
	private String PchFeeRate;

	public String getSPRefId() {
		return SPRefId;
	}

	public void setSPRefId(String sPRefId) {
		SPRefId = sPRefId;
	}

	public String getAcctId16() {
		return AcctId16;
	}

	public void setAcctId16(String acctId16) {
		AcctId16 = acctId16;
	}

	public String getOccur() {
		return Occur;
	}

	public void setOccur(String occur) {
		Occur = occur;
	}

	public String getAcctId02() {
		return AcctId02;
	}

	public void setAcctId02(String acctId02) {
		AcctId02 = acctId02;
	}

	public String getEviNum() {
		return EviNum;
	}

	public void setEviNum(String eviNum) {
		EviNum = eviNum;
	}

	public String getFundNO() {
		return FundNO;
	}

	public void setFundNO(String fundNO) {
		FundNO = fundNO;
	}

	public String getFundName() {
		return FundName;
	}

	public void setFundName(String fundName) {
		FundName = fundName;
	}

	public String getCurFund() {
		return CurFund;
	}

	public void setCurFund(String curFund) {
		CurFund = curFund;
	}

	public String getCurCode() {
		return CurCode;
	}

	public void setCurCode(String curCode) {
		CurCode = curCode;
	}

	public String getCurAmt() {
		return CurAmt;
	}

	public void setCurAmt(String curAmt) {
		CurAmt = curAmt;
	}

	public String getCurAmtNT() {
		return CurAmtNT;
	}

	public void setCurAmtNT(String curAmtNT) {
		CurAmtNT = curAmtNT;
	}

	public String getCurBal() {
		return CurBal;
	}

	public void setCurBal(String curBal) {
		CurBal = curBal;
	}

	public String getCurBalNT() {
		return CurBalNT;
	}

	public void setCurBalNT(String curBalNT) {
		CurBalNT = curBalNT;
	}

	public String getProfitAndLoss() {
		return ProfitAndLoss;
	}

	public void setProfitAndLoss(String profitAndLoss) {
		ProfitAndLoss = profitAndLoss;
	}

	public String getIncrease() {
		return Increase;
	}

	public void setIncrease(String increase) {
		Increase = increase;
	}

	public String getSignDigit() {
		return SignDigit;
	}

	public void setSignDigit(String signDigit) {
		SignDigit = signDigit;
	}

	public String getReturn() {
		return Return;
	}

	public void setReturn(String return1) {
		Return = return1;
	}

	public String getRewRateDigit() {
		return RewRateDigit;
	}

	public void setRewRateDigit(String rewRateDigit) {
		RewRateDigit = rewRateDigit;
	}

	public String getAccAllocateRewRate() {
		return AccAllocateRewRate;
	}

	public void setAccAllocateRewRate(String accAllocateRewRate) {
		AccAllocateRewRate = accAllocateRewRate;
	}

	public String getCurUntNum() {
		return CurUntNum;
	}

	public void setCurUntNum(String curUntNum) {
		CurUntNum = curUntNum;
	}

	public String getReferenceExchangeRate() {
		return ReferenceExchangeRate;
	}

	public void setReferenceExchangeRate(String referenceExchangeRate) {
		ReferenceExchangeRate = referenceExchangeRate;
	}

	public String getNetValueDate() {
		return NetValueDate;
	}

	public void setNetValueDate(String netValueDate) {
		NetValueDate = netValueDate;
	}

	public String getNetValue() {
		return NetValue;
	}

	public void setNetValue(String netValue) {
		NetValue = netValue;
	}

	public String getStoplossSign() {
		return StoplossSign;
	}

	public void setStoplossSign(String stoplossSign) {
		StoplossSign = stoplossSign;
	}

	public String getStoploss() {
		return Stoploss;
	}

	public void setStoploss(String stoploss) {
		Stoploss = stoploss;
	}

	public String getSatisfiedSign() {
		return SatisfiedSign;
	}

	public void setSatisfiedSign(String satisfiedSign) {
		SatisfiedSign = satisfiedSign;
	}

	public String getSatisfied() {
		return Satisfied;
	}

	public void setSatisfied(String satisfied) {
		Satisfied = satisfied;
	}

	public String getStrdate() {
		return Strdate;
	}

	public void setStrdate(String strdate) {
		Strdate = strdate;
	}

	public String getFundType() {
		return FundType;
	}

	public void setFundType(String fundType) {
		FundType = fundType;
	}

	public String getApproveFlag() {
		return ApproveFlag;
	}

	public void setApproveFlag(String approveFlag) {
		ApproveFlag = approveFlag;
	}

	public String getProjectCode() {
		return ProjectCode;
	}

	public void setProjectCode(String projectCode) {
		ProjectCode = projectCode;
	}

	public String getGroupCode() {
		return GroupCode;
	}

	public void setGroupCode(String groupCode) {
		GroupCode = groupCode;
	}

	public String getPayAcctId() {
		return PayAcctId;
	}

	public void setPayAcctId(String payAcctId) {
		PayAcctId = payAcctId;
	}

	public String getAccAllocateRew() {
		return AccAllocateRew;
	}

	public void setAccAllocateRew(String accAllocateRew) {
		AccAllocateRew = accAllocateRew;
	}

	public String getMK_CREDIT_FALG() {
		return MK_CREDIT_FALG;
	}

	public void setMK_CREDIT_FALG(String mK_CREDIT_FALG) {
		MK_CREDIT_FALG = mK_CREDIT_FALG;
	}

	public String getTransferAmt() {
		return TransferAmt;
	}

	public void setTransferAmt(String transferAmt) {
		TransferAmt = transferAmt;
	}

	public String getTransferDate01() {
		return TransferDate01;
	}

	public void setTransferDate01(String transferDate01) {
		TransferDate01 = transferDate01;
	}

	public String getTransferDate02() {
		return TransferDate02;
	}

	public void setTransferDate02(String transferDate02) {
		TransferDate02 = transferDate02;
	}

	public String getTransferDate03() {
		return TransferDate03;
	}

	public void setTransferDate03(String transferDate03) {
		TransferDate03 = transferDate03;
	}

	public String getTransferDate04() {
		return TransferDate04;
	}

	public void setTransferDate04(String transferDate04) {
		TransferDate04 = transferDate04;
	}

	public String getTransferDate05() {
		return TransferDate05;
	}

	public void setTransferDate05(String transferDate05) {
		TransferDate05 = transferDate05;
	}

	public String getTransferDate06() {
		return TransferDate06;
	}

	public void setTransferDate06(String transferDate06) {
		TransferDate06 = transferDate06;
	}

	public String getTransferCount() {
		return TransferCount;
	}

	public void setTransferCount(String transferCount) {
		TransferCount = transferCount;
	}

	public String getPayCount() {
		return PayCount;
	}

	public void setPayCount(String payCount) {
		PayCount = payCount;
	}

	public String getStatus() {
		return Status;
	}

	public void setStatus(String status) {
		Status = status;
	}

	public String getPayAccountNo() {
		return PayAccountNo;
	}

	public void setPayAccountNo(String payAccountNo) {
		PayAccountNo = payAccountNo;
	}

	public String getTxType() {
		return TxType;
	}

	public void setTxType(String txType) {
		TxType = txType;
	}

	public String getFrgnPurchaseFlag() {
		return FrgnPurchaseFlag;
	}

	public void setFrgnPurchaseFlag(String frgnPurchaseFlag) {
		FrgnPurchaseFlag = frgnPurchaseFlag;
	}

	public String getSame() {
		return Same;
	}

	public void setSame(String same) {
		Same = same;
	}

	public String getDiscountAmt() {
		return DiscountAmt;
	}

	public void setDiscountAmt(String discountAmt) {
		DiscountAmt = discountAmt;
	}

	public String getTransferAmt_H() {
		return TransferAmt_H;
	}

	public void setTransferAmt_H(String transferAmt_H) {
		TransferAmt_H = transferAmt_H;
	}

	public String getTransferAmt_M() {
		return TransferAmt_M;
	}

	public void setTransferAmt_M(String transferAmt_M) {
		TransferAmt_M = transferAmt_M;
	}

	public String getTransferAmt_L() {
		return TransferAmt_L;
	}

	public void setTransferAmt_L(String transferAmt_L) {
		TransferAmt_L = transferAmt_L;
	}

	public String getAcctId() {
		return AcctId;
	}

	public void setAcctId(String acctId) {
		AcctId = acctId;
	}

	public String getTimeDepositPrjCd() {
		return TimeDepositPrjCd;
	}

	public void setTimeDepositPrjCd(String timeDepositPrjCd) {
		TimeDepositPrjCd = timeDepositPrjCd;
	}

	public String getTotal_Cnt() {
		return Total_Cnt;
	}

	public void setTotal_Cnt(String total_Cnt) {
		Total_Cnt = total_Cnt;
	}

	public String getPay_Cnt() {
		return Pay_Cnt;
	}

	public void setPay_Cnt(String pay_Cnt) {
		Pay_Cnt = pay_Cnt;
	}

	public String getEnd_Flg() {
		return End_Flg;
	}

	public void setEnd_Flg(String end_Flg) {
		End_Flg = end_Flg;
	}

	public String getFundPackageNo() {
		return FundPackageNo;
	}

	public void setFundPackageNo(String fundPackageNo) {
		FundPackageNo = fundPackageNo;
	}

	public String getFundPackage() {
		return FundPackage;
	}

	public void setFundPackage(String fundPackage) {
		FundPackage = fundPackage;
	}

	public String getEffDate() {
		return EffDate;
	}

	public void setEffDate(String effDate) {
		EffDate = effDate;
	}

	public String getFeeCurCode() {
		return FeeCurCode;
	}

	public void setFeeCurCode(String feeCurCode) {
		FeeCurCode = feeCurCode;
	}

	public String getFee() {
		return Fee;
	}

	public void setFee(String fee) {
		Fee = fee;
	}

	public String getTransferCurCode() {
		return TransferCurCode;
	}

	public void setTransferCurCode(String transferCurCode) {
		TransferCurCode = transferCurCode;
	}

	public String getTransferAmtNT() {
		return TransferAmtNT;
	}

	public void setTransferAmtNT(String transferAmtNT) {
		TransferAmtNT = transferAmtNT;
	}

	public String getTrustType() {
		return TrustType;
	}

	public void setTrustType(String trustType) {
		TrustType = trustType;
	}

	public String getOutFundNO() {
		return OutFundNO;
	}

	public void setOutFundNO(String outFundNO) {
		OutFundNO = outFundNO;
	}

	public String getOutFundName() {
		return OutFundName;
	}

	public void setOutFundName(String outFundName) {
		OutFundName = outFundName;
	}

	public String getInFundNO() {
		return InFundNO;
	}

	public void setInFundNO(String inFundNO) {
		InFundNO = inFundNO;
	}

	public String getInFundName() {
		return InFundName;
	}

	public void setInFundName(String inFundName) {
		InFundName = inFundName;
	}

	public String getOutCurFund() {
		return OutCurFund;
	}

	public void setOutCurFund(String outCurFund) {
		OutCurFund = outCurFund;
	}

	public String getOutCurCode() {
		return OutCurCode;
	}

	public void setOutCurCode(String outCurCode) {
		OutCurCode = outCurCode;
	}

	public String getInCurFund() {
		return InCurFund;
	}

	public void setInCurFund(String inCurFund) {
		InCurFund = inCurFund;
	}

	public String getInCurCode() {
		return InCurCode;
	}

	public void setInCurCode(String inCurCode) {
		InCurCode = inCurCode;
	}

	public String getOutNetValueDate() {
		return OutNetValueDate;
	}

	public void setOutNetValueDate(String outNetValueDate) {
		OutNetValueDate = outNetValueDate;
	}

	public String getOutNetValue() {
		return OutNetValue;
	}

	public void setOutNetValue(String outNetValue) {
		OutNetValue = outNetValue;
	}

	public String getInNetValueDate() {
		return InNetValueDate;
	}

	public void setInNetValueDate(String inNetValueDate) {
		InNetValueDate = inNetValueDate;
	}

	public String getInNetValue() {
		return InNetValue;
	}

	public void setInNetValue(String inNetValue) {
		InNetValue = inNetValue;
	}

	public String getPchFundNO() {
		return PchFundNO;
	}

	public void setPchFundNO(String pchFundNO) {
		PchFundNO = pchFundNO;
	}

	public String getPchFundName() {
		return PchFundName;
	}

	public void setPchFundName(String pchFundName) {
		PchFundName = pchFundName;
	}

	public String getPchCurCode() {
		return PchCurCode;
	}

	public void setPchCurCode(String pchCurCode) {
		PchCurCode = pchCurCode;
	}

	public String getPchFeeRate() {
		return PchFeeRate;
	}

	public void setPchFeeRate(String pchFeeRate) {
		PchFeeRate = pchFeeRate;
	}

}
