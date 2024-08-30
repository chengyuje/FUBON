package com.systex.jbranch.app.server.fps.sot140;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

public class SOT140InputVO extends PagingInputVO{
	private String prodType;					//商品類別
	private String tradeType;					//交易種類
	private String tradeSEQ;					//交易序號
	
//    private String status;                      //下單狀態 1:暫存 2:風控檢核中
//    private String flag;                        //異動註記
//    private String seqNo;                       //交易序號
    private String custID;                      //客戶ID
    private String custName;                    //客戶姓名
    private String agentID;                     //代理人ID
    private String agentName;                   //代理人姓名
    private String kycLV;                       //KYC等級
    private Date   kycDueDate;                  //KYC效期
    private String custRemark;                  //客戶註記
    private String isOBU;                       //OBU註記
    private String isAgreeProdAdv;              //同意投資商品諮詢服務
    private String piRemark; //專業投資人註記
    private String isRecNeeded;               //是否需錄音
    private String hnwcYN;
	private String hnwcServiceYN;
    
    private Date piDueDate; //專業投資人效期
    private String queryProdID; //查詢商品資料(庫存，轉入標的)
    private Integer queryProdIDindex; //查詢商品資料(轉入標的1~3) for 適配
    private String queryOutProd;              //是否查詢庫存  (Y)
    
    //適配
    private String inProdID1;					//基金代號
    private String noSale;						//商品禁銷註記  (SOT701)
    private String profInvestorYN;			    //是否為專業投資人 	
    private String fatcaType;					//判斷身分fatca: 不合作、美國人、未簽署
    private String ageUnder70Flag;				//年齡小於70
    
    private String isFirstTrade;                //是否首購 
    
    //購物車
    private String custRemarks;					//客戶註記
    private Date bargainDueDate;				//期間議價效期
    private String custProType;					
    
    private BigDecimal carSEQ;					//
    
    
    private String outProdID;					//轉出基金代號
    private String outProdName;					//轉出基金名稱
    private String outProdCurr;					//轉出基金計價幣別
    private String outProdRiskLV;				//轉出產品風險等級
    private BigDecimal outPresentVal;				//轉出標的參考現值
    private BigDecimal outTrustAmt;					//轉出標的信託金額
    private String outTradeType;				/*轉出標的信託型態，參考 SOT.NEW_ASSET_TRADE_SUB_TYPE */

	private String outTradeTypeD;	           // 轉出標的詳細信託型態，參考 SOT.ASSET_TRADE_SUB_TYPE

    private String outCertificateID;			//轉出標的憑證編號
    private String outTrustCurr;				//轉出標的信託幣別
    private BigDecimal outUnit;						//轉出標的單位數
    private String transferType;				//轉出方式
    private String outTrustCurrType;            //轉出標的信託幣別類別 
    private String outTrustAcct;                //轉出標的信託帳號
    private String outNotVertify;               //轉出標的未核備(TBPRD_FUNDINFO.FUS40)

    
    private String inProdName1;					//轉入基金名稱 1
    private String inProdCurr1;					//轉入基金計價幣別 1
    private String inProdRiskLV1;				//轉入基金產品風險等級 1
    private BigDecimal inUnit1;						//轉入基金單位數 1
    private BigDecimal inUnitPrice1;				//轉入基金單位現值 1 
    private BigDecimal inPresentVal1;				//轉入基金參考現值 1
    private String inProdID2;					//轉入基金代碼 2
    private String inProdName2;					//轉入基金名稱 2
    private String inProdCurr2;					//轉入基金計價幣別 2
    private String inProdRiskLV2;				//轉入基金產品風險等級 2
    private BigDecimal inUnit2;						//轉入基金單位數 2
    private BigDecimal inUnitPrice2;				//轉入基金單位現值 2
    private BigDecimal inPresentVal2;				//轉入基金參考現值 2
    private String inProdID3;					//轉入基金代碼 3
    private String inProdName3;					//轉入基金名稱 3	
    private String inProdCurr3;					//轉入基金計價幣別 3
    private String inProdRiskLV3;				//轉入基金產品風險等級 3
    private BigDecimal inUnit3;						//轉入基金單位數 3
    private BigDecimal inUnitPrice3;				//轉入基金單位現值 3
    private BigDecimal inPresentVal3;				//轉入基金參考現值 3
    private String feeDebitAcct;				//手續費扣款帳號
    private String tradeDateType;				//交易日期類別
    private String tradeDate;				//交易日期
    private String narratorID;					//解說專員員編
    private String narratorName;				//解說專員姓名
    private String prospectusType;			//公開說明書
    public String getProspectusType() {
		return prospectusType;
	}

	public void setProspectusType(String prospectusType) {
		this.prospectusType = prospectusType;
	}

	private String isUnitORpresentVal;           //計算轉入單位數或參考現值
    private String recSEQ;                       //錄音序號
   // private List<SOT140DetailInputVO> details;  //基金下單明細資料

//    public String getStatus() {
//        return status;
//    }
//
//    public void setStatus(String status) {
//        this.status = status;
//    }

//    public String getFlag() {
//        return flag;
//    }
//
//    public void setFlag(String flag) {
//        this.flag = flag;
//    }
//
//    public String getSeqNo() {
//        return seqNo;
//    }
//
//    public void setSeqNo(String seqNo) {
//        this.seqNo = seqNo;
//    }

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

    public String getCustRemark() {
        return custRemark;
    }

    public void setCustRemark(String custRemark) {
        this.custRemark = custRemark;
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

 
 

//    @Override
//    public String toString() {
//        return "SOT140InputVO{" +
//                "status='" + status + '\'' +
//                ", flag='" + flag + '\'' +
//                ", seqNo='" + seqNo + '\'' +
//                ", custID='" + custID + '\'' +
//                ", custName='" + custName + '\'' +
//                ", agentID='" + agentID + '\'' +
//                ", agentName='" + agentName + '\'' +
//                ", kycLV='" + kycLV + '\'' +
//                ", kycDueDate='" + kycDueDate + '\'' +
//                ", custRemark='" + custRemark + '\'' +
//                ", isOBU='" + isOBU + '\'' +
//                ", isAgreeProdAdv='" + isAgreeProdAdv + '\'' +
//                ", details=" + details +
//                '}';
//    }

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

	public String getInProdID1() {
		return inProdID1;
	}

	public void setInProdID1(String inProdID1) {
		this.inProdID1 = inProdID1;
	}

 

	public String getProfInvestorYN() {
		return profInvestorYN;
	}

	public void setProfInvestorYN(String profInvestorYN) {
		this.profInvestorYN = profInvestorYN;
	}

	public String getFatcaType() {
		return fatcaType;
	}

	public void setFatcaType(String fatcaType) {
		this.fatcaType = fatcaType;
	}

	public String getAgeUnder70Flag() {
		return ageUnder70Flag;
	}

	public void setAgeUnder70Flag(String ageUnder70Flag) {
		this.ageUnder70Flag = ageUnder70Flag;
	}

	public String getTradeSEQ() {
		return tradeSEQ;
	}

	public void setTradeSEQ(String tradeSEQ) {
		this.tradeSEQ = tradeSEQ;
	}

	public String getCustRemarks() {
		return custRemarks;
	}

	public void setCustRemarks(String custRemarks) {
		this.custRemarks = custRemarks;
	}

	public Date getBargainDueDate() {
		return bargainDueDate;
	}

	public void setBargainDueDate(Date bargainDueDate) {
		this.bargainDueDate = bargainDueDate;
	}

	public String getCustProType() {
		return custProType;
	}

	public void setCustProType(String custProType) {
		this.custProType = custProType;
	}

	public BigDecimal getCarSEQ() {
		return carSEQ;
	}

	public void setCarSEQ(BigDecimal carSEQ) {
		this.carSEQ = carSEQ;
	}

	public String getOutProdID() {
		return outProdID;
	}

	public void setOutProdID(String outProdID) {
		this.outProdID = outProdID;
	}

	public String getOutProdName() {
		return outProdName;
	}

	public void setOutProdName(String outProdName) {
		this.outProdName = outProdName;
	}

	public String getOutProdCurr() {
		return outProdCurr;
	}

	public void setOutProdCurr(String outProdCurr) {
		this.outProdCurr = outProdCurr;
	}

	public String getOutProdRiskLV() {
		return outProdRiskLV;
	}

	public void setOutProdRiskLV(String outProdRiskLV) {
		this.outProdRiskLV = outProdRiskLV;
	}

	

	public BigDecimal getOutTrustAmt() {
		return outTrustAmt;
	}

	public void setOutTrustAmt(BigDecimal outTrustAmt) {
		this.outTrustAmt = outTrustAmt;
	}

	public String getOutTradeType() {
		return outTradeType;
	}

	public void setOutTradeType(String outTradeType) {
		this.outTradeType = outTradeType;
	}

	public String getOutCertificateID() {
		return outCertificateID;
	}

	public void setOutCertificateID(String outCertificateID) {
		this.outCertificateID = outCertificateID;
	}

	public String getOutTrustCurr() {
		return outTrustCurr;
	}

	public void setOutTrustCurr(String outTrustCurr) {
		this.outTrustCurr = outTrustCurr;
	}

	public BigDecimal getOutUnit() {
		return outUnit;
	}

	public void setOutUnit(BigDecimal outUnit) {
		this.outUnit = outUnit;
	}

	public String getTransferType() {
		return transferType;
	}

	public void setTransferType(String transferType) {
		this.transferType = transferType;
	}

	public String getInProdName1() {
		return inProdName1;
	}

	public void setInProdName1(String inProdName1) {
		this.inProdName1 = inProdName1;
	}

	public String getInProdCurr1() {
		return inProdCurr1;
	}

	public void setInProdCurr1(String inProdCurr1) {
		this.inProdCurr1 = inProdCurr1;
	}

	public String getInProdRiskLV1() {
		return inProdRiskLV1;
	}

	public void setInProdRiskLV1(String inProdRiskLV1) {
		this.inProdRiskLV1 = inProdRiskLV1;
	}

	public String getInProdID2() {
		return inProdID2;
	}

	public void setInProdID2(String inProdID2) {
		this.inProdID2 = inProdID2;
	}

	public String getInProdName2() {
		return inProdName2;
	}

	public void setInProdName2(String inProdName2) {
		this.inProdName2 = inProdName2;
	}

	public String getInProdCurr2() {
		return inProdCurr2;
	}

	public void setInProdCurr2(String inProdCurr2) {
		this.inProdCurr2 = inProdCurr2;
	}

	public String getInProdRiskLV2() {
		return inProdRiskLV2;
	}

	public void setInProdRiskLV2(String inProdRiskLV2) {
		this.inProdRiskLV2 = inProdRiskLV2;
	}


	public String getInProdID3() {
		return inProdID3;
	}

	public void setInProdID3(String inProdID3) {
		this.inProdID3 = inProdID3;
	}

	public String getInProdName3() {
		return inProdName3;
	}

	public void setInProdName3(String inProdName3) {
		this.inProdName3 = inProdName3;
	}

	public String getInProdCurr3() {
		return inProdCurr3;
	}

	public void setInProdCurr3(String inProdCurr3) {
		this.inProdCurr3 = inProdCurr3;
	}

	public String getInProdRiskLV3() {
		return inProdRiskLV3;
	}

	public void setInProdRiskLV3(String inProdRiskLV3) {
		this.inProdRiskLV3 = inProdRiskLV3;
	}

	public String getFeeDebitAcct() {
		return feeDebitAcct;
	}

	public void setFeeDebitAcct(String feeDebitAcct) {
		this.feeDebitAcct = feeDebitAcct;
	}

	public String getTradeDate() {
		return tradeDate;
	}

	public void setTradeDate(String tradeDate) {
		this.tradeDate = tradeDate;
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

	public BigDecimal getInUnit1() {
		return inUnit1;
	}

	public void setInUnit1(BigDecimal inUnit1) {
		this.inUnit1 = inUnit1;
	}

	public BigDecimal getInPresentVal1() {
		return inPresentVal1;
	}

	public void setInPresentVal1(BigDecimal inPresentVal1) {
		this.inPresentVal1 = inPresentVal1;
	}

	public BigDecimal getInUnit2() {
		return inUnit2;
	}

	public void setInUnit2(BigDecimal inUnit2) {
		this.inUnit2 = inUnit2;
	}

	public BigDecimal getInPresentVal2() {
		return inPresentVal2;
	}

	public void setInPresentVal2(BigDecimal inPresentVal2) {
		this.inPresentVal2 = inPresentVal2;
	}

	public BigDecimal getInUnit3() {
		return inUnit3;
	}

	public void setInUnit3(BigDecimal inUnit3) {
		this.inUnit3 = inUnit3;
	}

	public BigDecimal getInPresentVal3() {
		return inPresentVal3;
	}

	public void setInPresentVal3(BigDecimal inPresentVal3) {
		this.inPresentVal3 = inPresentVal3;
	}

	public String getTradeDateType() {
		return tradeDateType;
	}

	public void setTradeDateType(String tradeDateType) {
		this.tradeDateType = tradeDateType;
	}

	public String getNoSale() {
		return noSale;
	}

	public void setNoSale(String noSale) {
		this.noSale = noSale;
	}

	public String getIsUnitORpresentVal() {
		return isUnitORpresentVal;
	}

	public void setIsUnitORpresentVal(String isUnitORpresentVal) {
		this.isUnitORpresentVal = isUnitORpresentVal;
	} 
 

	public BigDecimal getInUnitPrice1() {
		return inUnitPrice1;
	}

	public void setInUnitPrice1(BigDecimal inUnitPrice1) {
		this.inUnitPrice1 = inUnitPrice1;
	}

	public BigDecimal getInUnitPrice2() {
		return inUnitPrice2;
	}

	public void setInUnitPrice2(BigDecimal inUnitPrice2) {
		this.inUnitPrice2 = inUnitPrice2;
	}

	public BigDecimal getInUnitPrice3() {
		return inUnitPrice3;
	}

	public void setInUnitPrice3(BigDecimal inUnitPrice3) {
		this.inUnitPrice3 = inUnitPrice3;
	}

	public String getOutTrustCurrType() {
		return outTrustCurrType;
	}

	public void setOutTrustCurrType(String outTrustCurrType) {
		this.outTrustCurrType = outTrustCurrType;
	}

	 
	public String getOutTrustAcct() {
		return outTrustAcct;
	}

	public void setOutTrustAcct(String outTrustAcct) {
		this.outTrustAcct = outTrustAcct;
	}

	public BigDecimal getOutPresentVal() {
		return outPresentVal;
	}

	public void setOutPresentVal(BigDecimal outPresentVal) {
		this.outPresentVal = outPresentVal;
	}

	public Date getPiDueDate() {
		return piDueDate;
	}

	public void setPiDueDate(Date piDueDate) {
		this.piDueDate = piDueDate;
	}

	public String getPiRemark() {
		return piRemark;
	}

	public void setPiRemark(String piRemark) {
		this.piRemark = piRemark;
	}

	public String getRecSEQ() {
		return recSEQ;
	}

	public void setRecSEQ(String recSEQ) {
		this.recSEQ = recSEQ;
	}

	public String getIsRecNeeded() {
		return isRecNeeded;
	}

	public void setIsRecNeeded(String isRecNeeded) {
		this.isRecNeeded = isRecNeeded;
	}

	public String getIsFirstTrade() {
		return isFirstTrade;
	}

	public void setIsFirstTrade(String isFirstTrade) {
		this.isFirstTrade = isFirstTrade;
	}

	public String getQueryProdID() {
		return queryProdID;
	}

	public void setQueryProdID(String queryProdID) {
		this.queryProdID = queryProdID;
	}

	public String getQueryOutProd() {
		return queryOutProd;
	}

	public void setQueryOutProd(String queryOutProd) {
		this.queryOutProd = queryOutProd;
	}

	public Integer getQueryProdIDindex() {
		return queryProdIDindex;
	}

	public void setQueryProdIDindex(Integer queryProdIDindex) {
		this.queryProdIDindex = queryProdIDindex;
	}

	public String getOutNotVertify() {
		return outNotVertify;
	}

	public void setOutNotVertify(String outNotVertify) {
		this.outNotVertify = outNotVertify;
	}


	public String getOutTradeTypeD() {
		return outTradeTypeD;
	}

	public void setOutTradeTypeD(String outTradeTypeD) {
		this.outTradeTypeD = outTradeTypeD;
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
