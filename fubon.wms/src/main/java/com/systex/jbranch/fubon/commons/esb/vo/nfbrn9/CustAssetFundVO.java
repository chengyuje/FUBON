package com.systex.jbranch.fubon.commons.esb.vo.nfbrn9;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlElement;

public class CustAssetFundVO {
	private String AssetType;         // Header.‧HFMTID =0001：單筆 0002：定期定額 0003：定期不定額 0004：定存轉基金 0005：基金套餐
	private String SPRefId;           // 傳送序號
	private String AcctId16;          // 身分證ID
	private String Occur;             // 資料筆數
	private String AcctId02;          // 信託帳號
	private String EviNum;            // 憑證號碼
	private String FundNO;            // 基金代號
	private String FundName;          // 基金名稱
	private String CurFund;           // 基金幣別
	private String CurCode;           // 交易幣別
	private BigDecimal CurAmt;        // 投資金額
	private BigDecimal CurAmtNT;      // 投資金額(折臺)
	private BigDecimal CurBal;        // 參考市值
	private BigDecimal CurBalNT;      // 參考市值(折臺)
	private BigDecimal ProfitAndLoss; // 投資損益
	private BigDecimal Increase;      // 累積現金配息
	private String SignDigit;         // 報酬率正負
	private BigDecimal Return;        // 報酬率
	private String RewRateDigit;      // 含調整後累積現金配息報酬率正負
	private BigDecimal AccAllocateRewRate;  // 含調整後累積現金配息報酬率
	private BigDecimal CurUntNum;     // 單位數
	private BigDecimal ReferenceExchangeRate;  // 參考匯率
	private String NetValueDate;      // 參考淨值日期
	private BigDecimal NetValue;      // 參考淨值
	private String StoplossSign;      // 停損正負號
	private BigDecimal Stoploss;      // 停損點
	private String SatisfiedSign;     // 滿足正負號
	private BigDecimal Satisfied;     // 滿足點
	private String Strdate;           // 投資起日
	private String FundType;          // 基金類型 1. 一般　 3. 貨幣   4. 債券   5. 平衡
	private String ApproveFlag;       // 未核備
	private String ProjectCode;       // 專案代號
	private String GroupCode;         // 團體代號
	private String PayAcctId;         // 收益入帳帳號
	private BigDecimal TransferAmt;   // 扣款金額
	private String TransferDate01;    // 扣款日期一
	private String TransferDate02;    // 扣款日期二
	private String TransferDate03;    // 扣款日期三
	private String TransferDate04;    // 扣款日期四
	private String TransferDate05;    // 扣款日期五
	private String TransferDate06;    // 扣款日期六
	private String TransferCount;     // 扣款次數
	private String PayCount;          // 扣款成功次數
	private String Status;            // 狀態
	private String PayAccountNo;      // 扣款帳號
	private String TxType;            // 交易類別
	private String FrgnPurchaseFlag;  // 換匯申購 Y:是 N:否
	private String Same;              // 申請扣款是否為同一人 Y:同一人 N :不同人
	private BigDecimal TransferAmt_H; // 扣款金額(高)
	private BigDecimal TransferAmt_M; // 扣款金額(中)
	private BigDecimal TransferAmt_L; // 扣款金額(低)
	private String AcctId;            // 專案帳號
	private String TimeDepositPrjCd;  // 定存專案編號
	private String Total_Cnt;         // 總投資期數
	private String Pay_Cnt;           // 已投資期數
	private String End_Flg;           // 終止碼 Y:專案中止
	private String FundPackageNo;     // 基金套餐編號
	private String FundPackage;       // 基金套餐
	private BigDecimal LongDiscount;  // 與時聚金折扣金額
	private BigDecimal AccAllocateRew;  // 調整後累積現金配息
	private String IsPledged;		    //質借圈存註記(cmkType=MK03 ==> Y else N) 	MK01 法院圈存; MK02 警示戶圈存; MK03 質權設定圈存  => 區分 他行,個金,法金; MK99 其他 
	private String Dynamic;	            //動態鎖利註記 1:母基金 2:子基金		
	private String ComboReturnDate;	    //組合報酬參考日		
	private String ComboReturnSign;	    //組合報酬正負		
	private BigDecimal ComboReturn;	    //基金組合報酬率		
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
	private String EviNumType;	    //狀態 Y=暫停
	
	private BigDecimal AccAllocateRewN;      // 調整後累積現金配息(含轉換前息)
	private String RewRateDigitN;            // 含調整後累積現金配息報酬率正負(含轉換前息)
	private BigDecimal AccAllocateRewRateN;  // 含調整後累積現金配息報酬率(含轉換前息)
	
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
	public BigDecimal getAccAllocateRewN() {
		return AccAllocateRewN;
	}
	public void setAccAllocateRewN(BigDecimal accAllocateRewN) {
		AccAllocateRewN = accAllocateRewN;
	}
	public String getRewRateDigitN() {
		return RewRateDigitN;
	}
	public void setRewRateDigitN(String rewRateDigitN) {
		RewRateDigitN = rewRateDigitN;
	}
	public BigDecimal getAccAllocateRewRateN() {
		return AccAllocateRewRateN;
	}
	public void setAccAllocateRewRateN(BigDecimal accAllocateRewRateN) {
		AccAllocateRewRateN = accAllocateRewRateN;
	}
}
