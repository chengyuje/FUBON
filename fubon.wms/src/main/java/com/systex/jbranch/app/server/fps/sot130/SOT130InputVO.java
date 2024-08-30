package com.systex.jbranch.app.server.fps.sot130;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

import java.util.List;
import java.util.Map;

public class SOT130InputVO extends PagingInputVO {
	private String type;
	private String status;
	private String tradeSEQ;
	private String seqNo;
	private String rdmProdID;
	private String prodType;
	private List<Map<String, Object>> custDTL;
	private List<Map<String, Object>> cartList;
	
	private String pchProdID;
	private String custID;
	private String outFlag;
	private String noSale;
	private String fatcaType;
	private String profInvestorYN;
	private String custTxFlag;
	private String tradeType;
	private String AgeUnder70Flag;
	private String kycLevel;
	
	private String isRecNeeded; //是否需要錄音
	private String recSEQ;      //錄音序號
	private String trustTS;				//M:金錢信託 S:特金交易
	
	//0000275: 金錢信託受監護受輔助宣告交易控管調整
	private String GUARDIANSHIP_FLAG; //受監護輔助 空白：無監護輔助 1.監護宣告 2輔助宣告
	
	//0687
	private String outProdID;
	
	
	
	public String getOutProdID() {
		return outProdID;
	}
	public void setOutProdID(String outProdID) {
		this.outProdID = outProdID;
	}
	public String getGUARDIANSHIP_FLAG() {
		return GUARDIANSHIP_FLAG;
	}
	public void setGUARDIANSHIP_FLAG(String gUARDIANSHIP_FLAG) {
		GUARDIANSHIP_FLAG = gUARDIANSHIP_FLAG;
	}
		
	
	public String getTrustTS() {
		return trustTS;
	}
	public void setTrustTS(String trustTS) {
		this.trustTS = trustTS;
	}
	public String getKycLevel() {
		return kycLevel;
	}
	public void setKycLevel(String kycLevel) {
		this.kycLevel = kycLevel;
	}
	public String getCustTxFlag() {
		return custTxFlag;
	}
	public void setCustTxFlag(String custTxFlag) {
		this.custTxFlag = custTxFlag;
	}
	public String getProdType() {
		return prodType;
	}
	public void setProdType(String prodType) {
		this.prodType = prodType;
	}
	public String getSeqNo() {
		return seqNo;
	}
	public void setSeqNo(String seqNo) {
		this.seqNo = seqNo;
	}
	public String getAgeUnder70Flag() {
		return AgeUnder70Flag;
	}
	public void setAgeUnder70Flag(String ageUnder70Flag) {
		AgeUnder70Flag = ageUnder70Flag;
	}
	public String getTradeType() {
		return tradeType;
	}
	public void setTradeType(String tradeType) {
		this.tradeType = tradeType;
	}
	public String getPchProdID() {
		return pchProdID;
	}
	public void setPchProdID(String pchProdID) {
		this.pchProdID = pchProdID;
	}
	public String getCustID() {
		return custID;
	}
	public void setCustID(String custID) {
		this.custID = custID;
	}
	public String getOutFlag() {
		return outFlag;
	}
	public void setOutFlag(String outFlag) {
		this.outFlag = outFlag;
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
	public String getProfInvestorYN() {
		return profInvestorYN;
	}
	public void setProfInvestorYN(String profInvestorYN) {
		this.profInvestorYN = profInvestorYN;
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
	public String getRdmProdID() {
		return rdmProdID;
	}
	public void setRdmProdID(String rdmProdID) {
		this.rdmProdID = rdmProdID;
	}
	public List<Map<String, Object>> getCustDTL() {
		return custDTL;
	}
	public void setCustDTL(List<Map<String, Object>> custDTL) {
		this.custDTL = custDTL;
	}
	public List<Map<String, Object>> getCartList() {
		return cartList;
	}
	public void setCartList(List<Map<String, Object>> cartList) {
		this.cartList = cartList;
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
	
	
}
