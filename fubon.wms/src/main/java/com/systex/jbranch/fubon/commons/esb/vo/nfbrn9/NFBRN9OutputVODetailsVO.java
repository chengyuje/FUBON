package com.systex.jbranch.fubon.commons.esb.vo.nfbrn9;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by SebastianWu on 2016/9/9.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class NFBRN9OutputVODetailsVO {
	@XmlElement
	private String SPRefId;
    @XmlElement
	private String AcctId16;
    @XmlElement
	private String Occur;
    @XmlElement
	private String AcctId02;
    @XmlElement
	private String EffDate;
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
	private String TransferAmt_H;
	@XmlElement
	private String TransferAmt_M;
	@XmlElement
	private String TransferAmt_L;
	
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
	
	@XmlElement
	private String FundPackageNo;
	@XmlElement
	private String FundPackage;
	
	@XmlElement
	private String LongDiscount;	//與時聚金折扣金額	
	@XmlElement
	private String AccAllocateRew;	//調整後累積現金配息
	
	@XmlElement
    private String CmkType;			//質借圈存註記 	MK01 法院圈存; MK02 警示戶圈存; MK03 質權設定圈存  => 區分 他行,個金,法金; MK99 其他
	
	// add by Carley 2021/3/9
	@XmlElement
	private String RedeemNetValue;	//贖回淨值	
	@XmlElement
	private String RedeemOrgAmt;	//預計贖回原幣金額	
	@XmlElement
	private String PostingDate;		//本行預計入帳日	
	
	@XmlElement
	private String Dynamic;				//動態鎖利註記 1:母基金 2:子基金
	@XmlElement
	private String ComboReturnDate;		//組合報酬參考日
	@XmlElement
	private String ComboReturnSign;		//組合報酬正負
	@XmlElement
	private String ComboReturn;			//基金組合報酬率
	@XmlElement
	private String SatelliteBuyDate1;	//約定申購子基金日1
	@XmlElement
	private String SatelliteBuyDate2;	//約定申購子基金日2
	@XmlElement
	private String SatelliteBuyDate3;	//約定申購子基金日3
	@XmlElement
	private String SatelliteBuyDate4;	//約定申購子基金日4
	@XmlElement
	private String SatelliteBuyDate5;	//約定申購子基金日5
	@XmlElement
	private String SatelliteBuyDate6;	//約定申購子基金日6
	@XmlElement
	private String BenefitReturnRate1;	//約定停利報酬率1	
	@XmlElement
	private String BenefitReturnRate2;	//約定停利報酬率2	
	@XmlElement
	private String BenefitReturnRate3;	//約定停利報酬率3	
	@XmlElement
	private String TRANSFERAmt;	//轉換金額
	@XmlElement
	private String EviNumType;	//狀態 Y=暫停
	
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
	public String getEffDate() {
		return EffDate;
	}
	public void setEffDate(String effDate) {
		EffDate = effDate;
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
	public String getLongDiscount() {
		return LongDiscount;
	}
	public void setLongDiscount(String longDiscount) {
		LongDiscount = longDiscount;
	}
	public String getAccAllocateRew() {
		return AccAllocateRew;
	}
	public void setAccAllocateRew(String accAllocateRew) {
		AccAllocateRew = accAllocateRew;
	}
	public String getCmkType() {
		return CmkType;
	}
	public void setCmkType(String cmkType) {
		CmkType = cmkType;
	}
	public String getRedeemNetValue() {
		return RedeemNetValue;
	}
	public void setRedeemNetValue(String redeemNetValue) {
		RedeemNetValue = redeemNetValue;
	}
	public String getRedeemOrgAmt() {
		return RedeemOrgAmt;
	}
	public void setRedeemOrgAmt(String redeemOrgAmt) {
		RedeemOrgAmt = redeemOrgAmt;
	}
	public String getPostingDate() {
		return PostingDate;
	}
	public void setPostingDate(String postingDate) {
		PostingDate = postingDate;
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
	public String getComboReturn() {
		return ComboReturn;
	}
	public void setComboReturn(String comboReturn) {
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
	public String getBenefitReturnRate1() {
		return BenefitReturnRate1;
	}
	public void setBenefitReturnRate1(String benefitReturnRate1) {
		BenefitReturnRate1 = benefitReturnRate1;
	}
	public String getBenefitReturnRate2() {
		return BenefitReturnRate2;
	}
	public void setBenefitReturnRate2(String benefitReturnRate2) {
		BenefitReturnRate2 = benefitReturnRate2;
	}
	public String getBenefitReturnRate3() {
		return BenefitReturnRate3;
	}
	public void setBenefitReturnRate3(String benefitReturnRate3) {
		BenefitReturnRate3 = benefitReturnRate3;
	}
	public String getTRANSFERAmt() {
		return TRANSFERAmt;
	}
	public void setTRANSFERAmt(String tRANSFERAmt) {
		TRANSFERAmt = tRANSFERAmt;
	}
	public String getEviNumType() {
		return EviNumType;
	}
	public void setEviNumType(String eviNumType) {
		EviNumType = eviNumType;
	}
	
}
