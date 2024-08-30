package com.systex.jbranch.fubon.commons.esb.vo.nfbrn3;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by SebastianWu on 2016/10/14.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class NFBRN3InputVO {
    @XmlElement
	private String CheckCode;           //確認碼             
    @XmlElement
	private String ApplyDate;             //申請日期
    @XmlElement
	private String EffDate;               //生效日
    @XmlElement
	private String KeyinNo;             //理專登錄編號'         
    @XmlElement
	private String BranchNo;            //交易行             
    @XmlElement
	private String KeyinId;             //建機人員         
    @XmlElement
	private String TrustId;             //委託人統編         
    @XmlElement
	private String Type;                //類別         
    @XmlElement
	private String EviNum_1;            //憑證編號1             
    @XmlElement
	private String FundNo_1;            //基金代碼1             
    @XmlElement
	private String BackType_1;          //贖回方式1             
    @XmlElement
	private String RcvAcctId_1;         //入帳帳號1             
    @XmlElement
	private String UntNum_1;        //原單位數1
    @XmlElement
	private String BackUntNum_1;    //贖回單位數1
    @XmlElement
	private String BackFlag_1;          //是否贖扣1             
    @XmlElement
	private String ApplyDate_1;         //第一次申購日1             
    @XmlElement
	private String CurCode_1;           //贖回信託金額幣別1             
    @XmlElement
	private String BackAmt_1;       //贖回信託金額1
    @XmlElement
	private String Year_1;              //持有年度1         
    @XmlElement
	private String FeeRateA_1;          //後收手續費率1             
    @XmlElement
	private String FeeAmt_1;            //後收手續費 1             
    @XmlElement
	private String BuyFlag_1;           //贖回再申購1             
    @XmlElement
	private String ReFundNo_1;          //再申購基金1             
    @XmlElement
	private String CreditAcct_1;        //收益入帳帳號1                 
    @XmlElement
	private String FeeRate_1;       //手續費率1
    @XmlElement
	private String NarratorId_1;        //推薦人1                 
    @XmlElement
	private String Filler_1;            //預留欄位1             
    @XmlElement
	private String EviNum_2;            //憑證編號2             
    @XmlElement
	private String FundNo_2;            //基金代碼2             
    @XmlElement
	private String BackType_2;          //贖回方式2             
    @XmlElement
	private String RcvAcctId_2;         //入帳帳號2             
    @XmlElement
	private String UntNum_2;            //原單位數2             
    @XmlElement
	private String BackUntNum_2;        //贖回單位數2                 
    @XmlElement
	private String BackFlag_2;          //是否贖扣2             
    @XmlElement
	private String ApplyDate_2;         //第一次申購日2             
    @XmlElement
	private String CurCode_2;           //贖回信託金額幣別2             
    @XmlElement
	private String BackAmt_2;           //贖回信託金額2             
    @XmlElement
	private String Year_2;              //持有年度2         
    @XmlElement
	private String FeeRateA_2;          //後收手續費率2             
    @XmlElement
	private String FeeAmt_2;            //後收手續費 2             
    @XmlElement
	private String BuyFlag_2;           //贖回再申購2             
    @XmlElement
	private String ReFundNo_2;          //再申購基金2             
    @XmlElement
	private String CreditAcct_2;        //收益入帳帳號2                 
    @XmlElement
	private String FeeRate_2;           //手續費率2             
    @XmlElement
	private String NarratorId_2;        //推薦人2                 
    @XmlElement
	private String Filler_2;            //預留欄位2             
    @XmlElement
	private String EviNum_3;            //憑證編號3             
    @XmlElement
	private String FundNo_3;            //基金代碼3             
    @XmlElement
	private String BackType_3;          //贖回方式3             
    @XmlElement
	private String RcvAcctId_3;         //入帳帳號3             
    @XmlElement
	private String UntNum_3;            //原單位數3             
    @XmlElement
	private String BackUntNum_3;        //贖回單位數3                 
    @XmlElement
	private String BackFlag_3;          //是否贖扣3             
    @XmlElement
	private String ApplyDate_3;         //第一次申購日3             
    @XmlElement
	private String CurCode_3;           //贖回信託金額幣別3             
    @XmlElement
	private String BackAmt_3;           //贖回信託金額3             
    @XmlElement
	private String Year_3;              //持有年度3         
    @XmlElement
	private String FeeRateA_3;          //後收手續費率3             
    @XmlElement
	private String FeeAmt_3;            //後收手續費 3             
    @XmlElement
	private String BuyFlag_3;           //贖回再申購3             
    @XmlElement
	private String ReFundNo_3;          //再申購基金3             
    @XmlElement
	private String CreditAcct_3;        //收益入帳帳號3                 
    @XmlElement
	private String FeeRate_3;           //手續費率3             
    @XmlElement
	private String NarratorId_3;        //推薦人3                 
    @XmlElement
	private String Filler_3;            //預留欄位3             
    @XmlElement
	private String RecSeq;              //錄音序號         
    @XmlElement
	private String IsPL_1;              //是否停損停利1         
    @XmlElement
	private String TakeProfitPerc_1;//停利1
    @XmlElement
	private String TakeProfitSym_1;     //停利符號1                 
    @XmlElement
	private String StopLossPerc_1;  //停損1
    @XmlElement
	private String StopLossSym_1;       //停損符號1                 
    @XmlElement
	private String IsPL_2;              //是否停損停利2         
    @XmlElement
	private String TakeProfitPerc_2;    //停利2                     
    @XmlElement
	private String TakeProfitSym_2;     //停利符號2                 
    @XmlElement
	private String StopLossPerc_2;      //停損2                 
    @XmlElement
	private String StopLossSym_2;       //停損符號2                 
    @XmlElement
	private String IsPL_3;              //是否停損停利3         
    @XmlElement
	private String TakeProfitPerc_3;    //停利3                     
    @XmlElement
	private String TakeProfitSym_3;     //停利符號3                 
    @XmlElement
	private String StopLossPerc_3;      //停損3                 
    @XmlElement
	private String StopLossSym_3;       //停損符號3                 
    @XmlElement
	private String BargainApplySeq_1;   //議價編號                     
    @XmlElement
	private String BargainApplySeq_2;   //議價編號                     
    @XmlElement
	private String BargainApplySeq_3;   //議價編號
    
    
	public String getCheckCode() {
		return CheckCode;
	}
	public void setCheckCode(String checkCode) {
		CheckCode = checkCode;
	}
	public String getApplyDate() {
		return ApplyDate;
	}
	public void setApplyDate(String applyDate) {
		ApplyDate = applyDate;
	}
	public String getEffDate() {
		return EffDate;
	}
	public void setEffDate(String effDate) {
		EffDate = effDate;
	}
	public String getKeyinNo() {
		return KeyinNo;
	}
	public void setKeyinNo(String keyinNo) {
		KeyinNo = keyinNo;
	}
	public String getBranchNo() {
		return BranchNo;
	}
	public void setBranchNo(String branchNo) {
		BranchNo = branchNo;
	}
	public String getKeyinId() {
		return KeyinId;
	}
	public void setKeyinId(String keyinId) {
		KeyinId = keyinId;
	}
	public String getTrustId() {
		return TrustId;
	}
	public void setTrustId(String trustId) {
		TrustId = trustId;
	}
	public String getType() {
		return Type;
	}
	public void setType(String type) {
		Type = type;
	}
	public String getEviNum_1() {
		return EviNum_1;
	}
	public void setEviNum_1(String eviNum_1) {
		EviNum_1 = eviNum_1;
	}
	public String getFundNo_1() {
		return FundNo_1;
	}
	public void setFundNo_1(String fundNo_1) {
		FundNo_1 = fundNo_1;
	}
	public String getBackType_1() {
		return BackType_1;
	}
	public void setBackType_1(String backType_1) {
		BackType_1 = backType_1;
	}
	public String getRcvAcctId_1() {
		return RcvAcctId_1;
	}
	public void setRcvAcctId_1(String rcvAcctId_1) {
		RcvAcctId_1 = rcvAcctId_1;
	}
	public String getUntNum_1() {
		return UntNum_1;
	}
	public void setUntNum_1(String untNum_1) {
		UntNum_1 = untNum_1;
	}
	public String getBackUntNum_1() {
		return BackUntNum_1;
	}
	public void setBackUntNum_1(String backUntNum_1) {
		BackUntNum_1 = backUntNum_1;
	}
	public String getBackFlag_1() {
		return BackFlag_1;
	}
	public void setBackFlag_1(String backFlag_1) {
		BackFlag_1 = backFlag_1;
	}
	public String getApplyDate_1() {
		return ApplyDate_1;
	}
	public void setApplyDate_1(String applyDate_1) {
		ApplyDate_1 = applyDate_1;
	}
	public String getCurCode_1() {
		return CurCode_1;
	}
	public void setCurCode_1(String curCode_1) {
		CurCode_1 = curCode_1;
	}
	public String getBackAmt_1() {
		return BackAmt_1;
	}
	public void setBackAmt_1(String backAmt_1) {
		BackAmt_1 = backAmt_1;
	}
	public String getYear_1() {
		return Year_1;
	}
	public void setYear_1(String year_1) {
		Year_1 = year_1;
	}
	public String getFeeRateA_1() {
		return FeeRateA_1;
	}
	public void setFeeRateA_1(String feeRateA_1) {
		FeeRateA_1 = feeRateA_1;
	}
	public String getFeeAmt_1() {
		return FeeAmt_1;
	}
	public void setFeeAmt_1(String feeAmt_1) {
		FeeAmt_1 = feeAmt_1;
	}
	public String getBuyFlag_1() {
		return BuyFlag_1;
	}
	public void setBuyFlag_1(String buyFlag_1) {
		BuyFlag_1 = buyFlag_1;
	}
	public String getReFundNo_1() {
		return ReFundNo_1;
	}
	public void setReFundNo_1(String reFundNo_1) {
		ReFundNo_1 = reFundNo_1;
	}
	public String getCreditAcct_1() {
		return CreditAcct_1;
	}
	public void setCreditAcct_1(String creditAcct_1) {
		CreditAcct_1 = creditAcct_1;
	}
	public String getFeeRate_1() {
		return FeeRate_1;
	}
	public void setFeeRate_1(String feeRate_1) {
		FeeRate_1 = feeRate_1;
	}
	public String getNarratorId_1() {
		return NarratorId_1;
	}
	public void setNarratorId_1(String narratorId_1) {
		NarratorId_1 = narratorId_1;
	}
	public String getFiller_1() {
		return Filler_1;
	}
	public void setFiller_1(String filler_1) {
		Filler_1 = filler_1;
	}
	public String getEviNum_2() {
		return EviNum_2;
	}
	public void setEviNum_2(String eviNum_2) {
		EviNum_2 = eviNum_2;
	}
	public String getFundNo_2() {
		return FundNo_2;
	}
	public void setFundNo_2(String fundNo_2) {
		FundNo_2 = fundNo_2;
	}
	public String getBackType_2() {
		return BackType_2;
	}
	public void setBackType_2(String backType_2) {
		BackType_2 = backType_2;
	}
	public String getRcvAcctId_2() {
		return RcvAcctId_2;
	}
	public void setRcvAcctId_2(String rcvAcctId_2) {
		RcvAcctId_2 = rcvAcctId_2;
	}
	public String getUntNum_2() {
		return UntNum_2;
	}
	public void setUntNum_2(String untNum_2) {
		UntNum_2 = untNum_2;
	}
	public String getBackUntNum_2() {
		return BackUntNum_2;
	}
	public void setBackUntNum_2(String backUntNum_2) {
		BackUntNum_2 = backUntNum_2;
	}
	public String getBackFlag_2() {
		return BackFlag_2;
	}
	public void setBackFlag_2(String backFlag_2) {
		BackFlag_2 = backFlag_2;
	}
	public String getApplyDate_2() {
		return ApplyDate_2;
	}
	public void setApplyDate_2(String applyDate_2) {
		ApplyDate_2 = applyDate_2;
	}
	public String getCurCode_2() {
		return CurCode_2;
	}
	public void setCurCode_2(String curCode_2) {
		CurCode_2 = curCode_2;
	}
	public String getBackAmt_2() {
		return BackAmt_2;
	}
	public void setBackAmt_2(String backAmt_2) {
		BackAmt_2 = backAmt_2;
	}
	public String getYear_2() {
		return Year_2;
	}
	public void setYear_2(String year_2) {
		Year_2 = year_2;
	}
	public String getFeeRateA_2() {
		return FeeRateA_2;
	}
	public void setFeeRateA_2(String feeRateA_2) {
		FeeRateA_2 = feeRateA_2;
	}
	public String getFeeAmt_2() {
		return FeeAmt_2;
	}
	public void setFeeAmt_2(String feeAmt_2) {
		FeeAmt_2 = feeAmt_2;
	}
	public String getBuyFlag_2() {
		return BuyFlag_2;
	}
	public void setBuyFlag_2(String buyFlag_2) {
		BuyFlag_2 = buyFlag_2;
	}
	public String getReFundNo_2() {
		return ReFundNo_2;
	}
	public void setReFundNo_2(String reFundNo_2) {
		ReFundNo_2 = reFundNo_2;
	}
	public String getCreditAcct_2() {
		return CreditAcct_2;
	}
	public void setCreditAcct_2(String creditAcct_2) {
		CreditAcct_2 = creditAcct_2;
	}
	public String getFeeRate_2() {
		return FeeRate_2;
	}
	public void setFeeRate_2(String feeRate_2) {
		FeeRate_2 = feeRate_2;
	}
	public String getNarratorId_2() {
		return NarratorId_2;
	}
	public void setNarratorId_2(String narratorId_2) {
		NarratorId_2 = narratorId_2;
	}
	public String getFiller_2() {
		return Filler_2;
	}
	public void setFiller_2(String filler_2) {
		Filler_2 = filler_2;
	}
	public String getEviNum_3() {
		return EviNum_3;
	}
	public void setEviNum_3(String eviNum_3) {
		EviNum_3 = eviNum_3;
	}
	public String getFundNo_3() {
		return FundNo_3;
	}
	public void setFundNo_3(String fundNo_3) {
		FundNo_3 = fundNo_3;
	}
	public String getBackType_3() {
		return BackType_3;
	}
	public void setBackType_3(String backType_3) {
		BackType_3 = backType_3;
	}
	public String getRcvAcctId_3() {
		return RcvAcctId_3;
	}
	public void setRcvAcctId_3(String rcvAcctId_3) {
		RcvAcctId_3 = rcvAcctId_3;
	}
	public String getUntNum_3() {
		return UntNum_3;
	}
	public void setUntNum_3(String untNum_3) {
		UntNum_3 = untNum_3;
	}
	public String getBackUntNum_3() {
		return BackUntNum_3;
	}
	public void setBackUntNum_3(String backUntNum_3) {
		BackUntNum_3 = backUntNum_3;
	}
	public String getBackFlag_3() {
		return BackFlag_3;
	}
	public void setBackFlag_3(String backFlag_3) {
		BackFlag_3 = backFlag_3;
	}
	public String getApplyDate_3() {
		return ApplyDate_3;
	}
	public void setApplyDate_3(String applyDate_3) {
		ApplyDate_3 = applyDate_3;
	}
	public String getCurCode_3() {
		return CurCode_3;
	}
	public void setCurCode_3(String curCode_3) {
		CurCode_3 = curCode_3;
	}
	public String getBackAmt_3() {
		return BackAmt_3;
	}
	public void setBackAmt_3(String backAmt_3) {
		BackAmt_3 = backAmt_3;
	}
	public String getYear_3() {
		return Year_3;
	}
	public void setYear_3(String year_3) {
		Year_3 = year_3;
	}
	public String getFeeRateA_3() {
		return FeeRateA_3;
	}
	public void setFeeRateA_3(String feeRateA_3) {
		FeeRateA_3 = feeRateA_3;
	}
	public String getFeeAmt_3() {
		return FeeAmt_3;
	}
	public void setFeeAmt_3(String feeAmt_3) {
		FeeAmt_3 = feeAmt_3;
	}
	public String getBuyFlag_3() {
		return BuyFlag_3;
	}
	public void setBuyFlag_3(String buyFlag_3) {
		BuyFlag_3 = buyFlag_3;
	}
	public String getReFundNo_3() {
		return ReFundNo_3;
	}
	public void setReFundNo_3(String reFundNo_3) {
		ReFundNo_3 = reFundNo_3;
	}
	public String getCreditAcct_3() {
		return CreditAcct_3;
	}
	public void setCreditAcct_3(String creditAcct_3) {
		CreditAcct_3 = creditAcct_3;
	}
	public String getFeeRate_3() {
		return FeeRate_3;
	}
	public void setFeeRate_3(String feeRate_3) {
		FeeRate_3 = feeRate_3;
	}
	public String getNarratorId_3() {
		return NarratorId_3;
	}
	public void setNarratorId_3(String narratorId_3) {
		NarratorId_3 = narratorId_3;
	}
	public String getFiller_3() {
		return Filler_3;
	}
	public void setFiller_3(String filler_3) {
		Filler_3 = filler_3;
	}
	public String getRecSeq() {
		return RecSeq;
	}
	public void setRecSeq(String recSeq) {
		RecSeq = recSeq;
	}
	public String getIsPL_1() {
		return IsPL_1;
	}
	public void setIsPL_1(String isPL_1) {
		IsPL_1 = isPL_1;
	}
	public String getTakeProfitPerc_1() {
		return TakeProfitPerc_1;
	}
	public void setTakeProfitPerc_1(String takeProfitPerc_1) {
		TakeProfitPerc_1 = takeProfitPerc_1;
	}
	public String getTakeProfitSym_1() {
		return TakeProfitSym_1;
	}
	public void setTakeProfitSym_1(String takeProfitSym_1) {
		TakeProfitSym_1 = takeProfitSym_1;
	}
	public String getStopLossPerc_1() {
		return StopLossPerc_1;
	}
	public void setStopLossPerc_1(String stopLossPerc_1) {
		StopLossPerc_1 = stopLossPerc_1;
	}
	public String getStopLossSym_1() {
		return StopLossSym_1;
	}
	public void setStopLossSym_1(String stopLossSym_1) {
		StopLossSym_1 = stopLossSym_1;
	}
	public String getIsPL_2() {
		return IsPL_2;
	}
	public void setIsPL_2(String isPL_2) {
		IsPL_2 = isPL_2;
	}
	public String getTakeProfitPerc_2() {
		return TakeProfitPerc_2;
	}
	public void setTakeProfitPerc_2(String takeProfitPerc_2) {
		TakeProfitPerc_2 = takeProfitPerc_2;
	}
	public String getTakeProfitSym_2() {
		return TakeProfitSym_2;
	}
	public void setTakeProfitSym_2(String takeProfitSym_2) {
		TakeProfitSym_2 = takeProfitSym_2;
	}
	public String getStopLossPerc_2() {
		return StopLossPerc_2;
	}
	public void setStopLossPerc_2(String stopLossPerc_2) {
		StopLossPerc_2 = stopLossPerc_2;
	}
	public String getStopLossSym_2() {
		return StopLossSym_2;
	}
	public void setStopLossSym_2(String stopLossSym_2) {
		StopLossSym_2 = stopLossSym_2;
	}
	public String getIsPL_3() {
		return IsPL_3;
	}
	public void setIsPL_3(String isPL_3) {
		IsPL_3 = isPL_3;
	}
	public String getTakeProfitPerc_3() {
		return TakeProfitPerc_3;
	}
	public void setTakeProfitPerc_3(String takeProfitPerc_3) {
		TakeProfitPerc_3 = takeProfitPerc_3;
	}
	public String getTakeProfitSym_3() {
		return TakeProfitSym_3;
	}
	public void setTakeProfitSym_3(String takeProfitSym_3) {
		TakeProfitSym_3 = takeProfitSym_3;
	}
	public String getStopLossPerc_3() {
		return StopLossPerc_3;
	}
	public void setStopLossPerc_3(String stopLossPerc_3) {
		StopLossPerc_3 = stopLossPerc_3;
	}
	public String getStopLossSym_3() {
		return StopLossSym_3;
	}
	public void setStopLossSym_3(String stopLossSym_3) {
		StopLossSym_3 = stopLossSym_3;
	}
	public String getBargainApplySeq_1() {
		return BargainApplySeq_1;
	}
	public void setBargainApplySeq_1(String bargainApplySeq_1) {
		BargainApplySeq_1 = bargainApplySeq_1;
	}
	public String getBargainApplySeq_2() {
		return BargainApplySeq_2;
	}
	public void setBargainApplySeq_2(String bargainApplySeq_2) {
		BargainApplySeq_2 = bargainApplySeq_2;
	}
	public String getBargainApplySeq_3() {
		return BargainApplySeq_3;
	}
	public void setBargainApplySeq_3(String bargainApplySeq_3) {
		BargainApplySeq_3 = bargainApplySeq_3;
	}
}
