package com.systex.jbranch.fubon.commons.esb.vo.nfbrn9;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlElement;

public class CustAssetFundVO {
	private String AssetType; // Header.‧HFMTID =0001：單筆 0002：定期定額 0003：定期不定額 0004：定存轉基金 0005：基金套餐
	private String SPRefId;
	private String AcctId16;
	private String Occur;
	private String AcctId02;
	private String EviNum;
	private String FundNO;
	private String FundName;
	private String CurFund;
	private String CurCode;
	private BigDecimal CurAmt;
	private BigDecimal CurAmtNT;
	private BigDecimal CurBal;
	private BigDecimal CurBalNT;
	private BigDecimal ProfitAndLoss;
	private BigDecimal Increase;
	private String SignDigit;
	private BigDecimal Return;
	private String RewRateDigit;
	private BigDecimal AccAllocateRewRate;
	private BigDecimal CurUntNum;
	private BigDecimal ReferenceExchangeRate;
	private String NetValueDate;
	private BigDecimal NetValue;
	private String StoplossSign;
	private BigDecimal Stoploss;
	private String SatisfiedSign;
	private BigDecimal Satisfied;
	private String Strdate;
	private String FundType;
	private String ApproveFlag;
	private String ProjectCode;
	private String GroupCode;
	private String PayAcctId;
	private BigDecimal TransferAmt;
	private String TransferDate01;
	private String TransferDate02;
	private String TransferDate03;
	private String TransferDate04;
	private String TransferDate05;
	private String TransferDate06;
	private String TransferCount;
	private String PayCount;
	private String Status;
	private String PayAccountNo;
	private String TxType;
	private String FrgnPurchaseFlag;
	private String Same;
	private BigDecimal TransferAmt_H;
	private BigDecimal TransferAmt_M;
	private BigDecimal TransferAmt_L;
	private String AcctId;
	private String TimeDepositPrjCd;
	private String Total_Cnt;
	private String Pay_Cnt;
	private String End_Flg;
	private String FundPackageNo;
	private String FundPackage;
	private BigDecimal LongDiscount;
	private BigDecimal AccAllocateRew;
	private String IsPledged;			//質借圈存註記(cmkType=MK03 ==> Y else N) 	MK01 法院圈存; MK02 警示戶圈存; MK03 質權設定圈存  => 區分 他行,個金,法金; MK99 其他 
	private String Dynamic;	//動態鎖利註記 1:母基金 2:子基金		
	private String ComboReturnDate;	//組合報酬參考日		
	private String ComboReturnSign;	//組合報酬正負		
	private BigDecimal ComboReturn;	//基金組合報酬率		
	private String SatelliteBuyDate1;	//約定申購子基金日1	
	private String SatelliteBuyDate2;	//約定申購子基金日2	
	private String SatelliteBuyDate3;	//約定申購子基金日3	
	private String SatelliteBuyDate4;	//約定申購子基金日4	
	private String SatelliteBuyDate5;	//約定申購子基金日5	
	private String SatelliteBuyDate6;	//約定申購子基金日6	
	private BigDecimal BenefitReturnRate1;	//約定停利報酬率1		
	private BigDecimal BenefitReturnRate2;	//約定停利報酬率2		
	private BigDecimal BenefitReturnRate3;	//約定停利報酬率3	
	private BigDecimal TRANSFERAmt;	//轉換金額
	private String EviNumType;	//狀態 Y=暫停
	
	public String getAssetType() {
		return AssetType;
	}
	public void setAssetType(String assetType) {
		AssetType = assetType;
	}
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
	public BigDecimal getCurAmt() {
		return CurAmt;
	}
	public void setCurAmt(BigDecimal curAmt) {
		CurAmt = curAmt;
	}
	public BigDecimal getCurAmtNT() {
		return CurAmtNT;
	}
	public void setCurAmtNT(BigDecimal curAmtNT) {
		CurAmtNT = curAmtNT;
	}
	public BigDecimal getCurBal() {
		return CurBal;
	}
	public void setCurBal(BigDecimal curBal) {
		CurBal = curBal;
	}
	public BigDecimal getCurBalNT() {
		return CurBalNT;
	}
	public void setCurBalNT(BigDecimal curBalNT) {
		CurBalNT = curBalNT;
	}
	public BigDecimal getProfitAndLoss() {
		return ProfitAndLoss;
	}
	public void setProfitAndLoss(BigDecimal profitAndLoss) {
		ProfitAndLoss = profitAndLoss;
	}
	public BigDecimal getIncrease() {
		return Increase;
	}
	public void setIncrease(BigDecimal increase) {
		Increase = increase;
	}
	public String getSignDigit() {
		return SignDigit;
	}
	public void setSignDigit(String signDigit) {
		SignDigit = signDigit;
	}
	public BigDecimal getReturn() {
		return Return;
	}
	public void setReturn(BigDecimal return1) {
		Return = return1;
	}
	public String getRewRateDigit() {
		return RewRateDigit;
	}
	public void setRewRateDigit(String rewRateDigit) {
		RewRateDigit = rewRateDigit;
	}
	public BigDecimal getAccAllocateRewRate() {
		return AccAllocateRewRate;
	}
	public void setAccAllocateRewRate(BigDecimal accAllocateRewRate) {
		AccAllocateRewRate = accAllocateRewRate;
	}
	public BigDecimal getCurUntNum() {
		return CurUntNum;
	}
	public void setCurUntNum(BigDecimal curUntNum) {
		CurUntNum = curUntNum;
	}
	public BigDecimal getReferenceExchangeRate() {
		return ReferenceExchangeRate;
	}
	public void setReferenceExchangeRate(BigDecimal referenceExchangeRate) {
		ReferenceExchangeRate = referenceExchangeRate;
	}
	public String getNetValueDate() {
		return NetValueDate;
	}
	public void setNetValueDate(String netValueDate) {
		NetValueDate = netValueDate;
	}
	public BigDecimal getNetValue() {
		return NetValue;
	}
	public void setNetValue(BigDecimal netValue) {
		NetValue = netValue;
	}
	public String getStoplossSign() {
		return StoplossSign;
	}
	public void setStoplossSign(String stoplossSign) {
		StoplossSign = stoplossSign;
	}
	public BigDecimal getStoploss() {
		return Stoploss;
	}
	public void setStoploss(BigDecimal stoploss) {
		Stoploss = stoploss;
	}
	public String getSatisfiedSign() {
		return SatisfiedSign;
	}
	public void setSatisfiedSign(String satisfiedSign) {
		SatisfiedSign = satisfiedSign;
	}
	public BigDecimal getSatisfied() {
		return Satisfied;
	}
	public void setSatisfied(BigDecimal satisfied) {
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
	public BigDecimal getTransferAmt() {
		return TransferAmt;
	}
	public void setTransferAmt(BigDecimal transferAmt) {
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
	public BigDecimal getTransferAmt_H() {
		return TransferAmt_H;
	}
	public void setTransferAmt_H(BigDecimal transferAmt_H) {
		TransferAmt_H = transferAmt_H;
	}
	public BigDecimal getTransferAmt_M() {
		return TransferAmt_M;
	}
	public void setTransferAmt_M(BigDecimal transferAmt_M) {
		TransferAmt_M = transferAmt_M;
	}
	public BigDecimal getTransferAmt_L() {
		return TransferAmt_L;
	}
	public void setTransferAmt_L(BigDecimal transferAmt_L) {
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
	public BigDecimal getLongDiscount() {
		return LongDiscount;
	}
	public void setLongDiscount(BigDecimal longDiscount) {
		LongDiscount = longDiscount;
	}
	public BigDecimal getAccAllocateRew() {
		return AccAllocateRew;
	}
	public void setAccAllocateRew(BigDecimal accAllocateRew) {
		AccAllocateRew = accAllocateRew;
	}
	public String getIsPledged() {
		return IsPledged;
	}
	public void setIsPledged(String isPledged) {
		IsPledged = isPledged;
	}
	public String getDynamic() {
		return Dynamic;
	}
	public void setDynamic(String dynamic) {
		Dynamic = dynamic;
	}
	public String getComboReturnDate() {
		return ComboReturnDate;
	}
	public void setComboReturnDate(String comboReturnDate) {
		ComboReturnDate = comboReturnDate;
	}
	public String getComboReturnSign() {
		return ComboReturnSign;
	}
	public void setComboReturnSign(String comboReturnSign) {
		ComboReturnSign = comboReturnSign;
	}
	public BigDecimal getComboReturn() {
		return ComboReturn;
	}
	public void setComboReturn(BigDecimal comboReturn) {
		ComboReturn = comboReturn;
	}
	public String getSatelliteBuyDate1() {
		return SatelliteBuyDate1;
	}
	public void setSatelliteBuyDate1(String satelliteBuyDate1) {
		SatelliteBuyDate1 = satelliteBuyDate1;
	}
	public String getSatelliteBuyDate2() {
		return SatelliteBuyDate2;
	}
	public void setSatelliteBuyDate2(String satelliteBuyDate2) {
		SatelliteBuyDate2 = satelliteBuyDate2;
	}
	public String getSatelliteBuyDate3() {
		return SatelliteBuyDate3;
	}
	public void setSatelliteBuyDate3(String satelliteBuyDate3) {
		SatelliteBuyDate3 = satelliteBuyDate3;
	}
	public String getSatelliteBuyDate4() {
		return SatelliteBuyDate4;
	}
	public void setSatelliteBuyDate4(String satelliteBuyDate4) {
		SatelliteBuyDate4 = satelliteBuyDate4;
	}
	public String getSatelliteBuyDate5() {
		return SatelliteBuyDate5;
	}
	public void setSatelliteBuyDate5(String satelliteBuyDate5) {
		SatelliteBuyDate5 = satelliteBuyDate5;
	}
	public String getSatelliteBuyDate6() {
		return SatelliteBuyDate6;
	}
	public void setSatelliteBuyDate6(String satelliteBuyDate6) {
		SatelliteBuyDate6 = satelliteBuyDate6;
	}
	public BigDecimal getBenefitReturnRate1() {
		return BenefitReturnRate1;
	}
	public void setBenefitReturnRate1(BigDecimal benefitReturnRate1) {
		BenefitReturnRate1 = benefitReturnRate1;
	}
	public BigDecimal getBenefitReturnRate2() {
		return BenefitReturnRate2;
	}
	public void setBenefitReturnRate2(BigDecimal benefitReturnRate2) {
		BenefitReturnRate2 = benefitReturnRate2;
	}
	public BigDecimal getBenefitReturnRate3() {
		return BenefitReturnRate3;
	}
	public void setBenefitReturnRate3(BigDecimal benefitReturnRate3) {
		BenefitReturnRate3 = benefitReturnRate3;
	}
	public BigDecimal getTRANSFERAmt() {
		return TRANSFERAmt;
	}
	public void setTRANSFERAmt(BigDecimal tRANSFERAmt) {
		TRANSFERAmt = tRANSFERAmt;
	}
	public String getEviNumType() {
		return EviNumType;
	}
	public void setEviNumType(String eviNumType) {
		EviNumType = eviNumType;
	}
		
}
