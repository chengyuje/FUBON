package com.systex.jbranch.app.server.fps.sot712;

import java.util.List;

import com.systex.jbranch.app.server.fps.sot714.WMSHACRDataVO;


public class PRDFitInputVO {
	//共用參數
	private String custId;		//客戶ID
	private String custName;
	private String riskLevel;	//產品風險等級 P1~P4
	private int caseCode;  		//案例碼   (1:適配、2:下單)
	
	//適配相關參數
	private String prdType;		//商品種類
	private String prdId;		//商品ID
	private String prdName;		//商品名稱
	private String currency;	//幣別
	private String hnwcBuy;		//限高資產申購註記
	private String isPrintSOT819;		//是否列印貸款風險預告書
	
	//下單相關參數
	private String tradeSeq;	//交易序號
	private String batchSeq;    //批次序號
	private int tradeType;      //基金交易類別 1：單筆申購 2：贖回/贖回再申購 3：轉換 4：變更 5：定期(不)定額申購
	private int tradeDateType;  //交易日期類別 1：即時 2：預約
	private int tradeSubType;	//交易類型 1:申購 2:贖回
	
	private String isbBackend;	//判斷是否為後收型基金
	
	private String isPrintSot816;	//是否列印結構型商品交易自主聲明書
	private String isPrintSot817;	//是否列印結構型商品推介終止同意書
	
	private List<String> monTrustRptList;	//WMS-CR-20200707-01_應附文件清單隨業管套表文件印出(金錢信託)
	private WMSHACRDataVO hmshacrDataVO; //高資產集中度資訊
	private String overCentRateYN;		//超過集中度上限
	//動態鎖利
	private String prdIdC1;
	private String prdIdC2;
	private String prdIdC3;
	private String prdNameC1;		//商品名稱
	private String prdNameC2;		//商品名稱
	private String prdNameC3;		//商品名稱
	
	public String getIsbBackend() {
		return isbBackend;
	}
	public void setIsbBackend(String isbBackend) {
		this.isbBackend = isbBackend;
	}
	public String getCustId() {
		return custId;
	}
	public void setCustId(String custId) {
		this.custId = custId;
	}
	public String getCustName() {
		return custName;
	}
	public void setCustName(String custName) {
		this.custName = custName;
	}
	public String getRiskLevel() {
		return riskLevel;
	}
	public void setRiskLevel(String riskLevel) {
		this.riskLevel = riskLevel;
	}
	public int getCaseCode() {
		return caseCode;
	}
	public void setCaseCode(int caseCode) {
		this.caseCode = caseCode;
	}
	public String getPrdType() {
		return prdType;
	}
	public void setPrdType(String prdType) {
		this.prdType = prdType;
	}
	public String getPrdId() {
		return prdId;
	}
	public void setPrdId(String prdId) {
		this.prdId = prdId;
	}
	public String getPrdName() {
		return prdName;
	}
	public void setPrdName(String prdName) {
		this.prdName = prdName;
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public String getHnwcBuy() {
		return hnwcBuy;
	}
	public void setHnwcBuy(String hnwcBuy) {
		this.hnwcBuy = hnwcBuy;
	}
	public String getTradeSeq() {
		return tradeSeq;
	}
	public void setTradeSeq(String tradeSeq) {
		this.tradeSeq = tradeSeq;
	}
	public String getBatchSeq() {
		return batchSeq;
	}
	public void setBatchSeq(String batchSeq) {
		this.batchSeq = batchSeq;
	}
	public int getTradeType() {
		return tradeType;
	}
	public void setTradeType(int tradeType) {
		this.tradeType = tradeType;
	}
	public int getTradeDateType() {
		return tradeDateType;
	}
	public void setTradeDateType(int tradeDateType) {
		this.tradeDateType = tradeDateType;
	}
	public int getTradeSubType() {
		return tradeSubType;
	}
	public void setTradeSubType(int tradeSubType) {
		this.tradeSubType = tradeSubType;
	}
	public String getIsPrintSot816() {
		return isPrintSot816;
	}
	public void setIsPrintSot816(String isPrintSot816) {
		this.isPrintSot816 = isPrintSot816;
	}
	public String getIsPrintSot817() {
		return isPrintSot817;
	}
	public void setIsPrintSot817(String isPrintSot817) {
		this.isPrintSot817 = isPrintSot817;
	}
	public List<String> getMonTrustRptList() {
		return monTrustRptList;
	}
	public void setMonTrustRptList(List<String> monTrustRptList) {
		this.monTrustRptList = monTrustRptList;
	}
	public WMSHACRDataVO getHmshacrDataVO() {
		return hmshacrDataVO;
	}
	public void setHmshacrDataVO(WMSHACRDataVO hmshacrDataVO) {
		this.hmshacrDataVO = hmshacrDataVO;
	}
	public String getOverCentRateYN() {
		return overCentRateYN;
	}
	public void setOverCentRateYN(String overCentRateYN) {
		this.overCentRateYN = overCentRateYN;
	}
	public String getIsPrintSOT819() {
		return isPrintSOT819;
	}
	public void setIsPrintSOT819(String isPrintSOT819) {
		this.isPrintSOT819 = isPrintSOT819;
	}
	public String getPrdIdC1() {
		return prdIdC1;
	}
	public void setPrdIdC1(String prdIdC1) {
		this.prdIdC1 = prdIdC1;
	}
	public String getPrdIdC2() {
		return prdIdC2;
	}
	public void setPrdIdC2(String prdIdC2) {
		this.prdIdC2 = prdIdC2;
	}
	public String getPrdIdC3() {
		return prdIdC3;
	}
	public void setPrdIdC3(String prdIdC3) {
		this.prdIdC3 = prdIdC3;
	}
	public String getPrdNameC1() {
		return prdNameC1;
	}
	public void setPrdNameC1(String prdNameC1) {
		this.prdNameC1 = prdNameC1;
	}
	public String getPrdNameC2() {
		return prdNameC2;
	}
	public void setPrdNameC2(String prdNameC2) {
		this.prdNameC2 = prdNameC2;
	}
	public String getPrdNameC3() {
		return prdNameC3;
	}
	public void setPrdNameC3(String prdNameC3) {
		this.prdNameC3 = prdNameC3;
	}
	
}

