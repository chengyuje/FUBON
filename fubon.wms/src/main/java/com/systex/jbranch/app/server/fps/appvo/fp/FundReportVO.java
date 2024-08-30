package com.systex.jbranch.app.server.fps.appvo.fp;

import java.util.List;


public class FundReportVO {
	private String fundWelcome; 					//迎賓文字
	private List marketReviewList; 					//市場回顧資料清單
	private List invstPolicyList; 					//投資策略資料清單
	private Number sysStockRatio; 					//建議股比例
	private Number sysBondRatio; 					//建議債比例
	private Number sysCashRatio; 					//建議現金比例
	private List custRatioSetList; 					//資產股債比資料清單
	private List custMarketList; 					//資產市場別配置比清單
	private List assetList; 						//行內基金資產明細清單
	private List assetOutsideList; 					//行外基金資產明細清單
	private SelAssetFundAllVO selAssetFundAllVO; 	//股債配置資料VO
	private SelAdjListVO selAdjListVO; 				//調整清單資料VO
	private List rcmndFundList; 					//建議基金績效資料清單
	
	public String getFundWelcome() {
		return fundWelcome;
	}
	public void setFundWelcome(String fundWelcome) {
		this.fundWelcome = fundWelcome;
	}
	public List getMarketReviewList() {
		return marketReviewList;
	}
	public void setMarketReviewList(List marketReviewList) {
		this.marketReviewList = marketReviewList;
	}
	public List getInvstPolicyList() {
		return invstPolicyList;
	}
	public void setInvstPolicyList(List invstPolicyList) {
		this.invstPolicyList = invstPolicyList;
	}
	public Number getSysStockRatio() {
		return sysStockRatio;
	}
	public void setSysStockRatio(Number sysStockRatio) {
		this.sysStockRatio = sysStockRatio;
	}
	public Number getSysBondRatio() {
		return sysBondRatio;
	}
	public void setSysBondRatio(Number sysBondRatio) {
		this.sysBondRatio = sysBondRatio;
	}
	public Number getSysCashRatio() {
		return sysCashRatio;
	}
	public void setSysCashRatio(Number sysCashRatio) {
		this.sysCashRatio = sysCashRatio;
	}
	public List getCustRatioSetList() {
		return custRatioSetList;
	}
	public void setCustRatioSetList(List custRatioSetList) {
		this.custRatioSetList = custRatioSetList;
	}
	public List getCustMarketList() {
		return custMarketList;
	}
	public void setCustMarketList(List custMarketList) {
		this.custMarketList = custMarketList;
	}
	public List getAssetList() {
		return assetList;
	}
	public void setAssetList(List assetList) {
		this.assetList = assetList;
	}
	public List getAssetOutsideList() {
		return assetOutsideList;
	}
	public void setAssetOutsideList(List assetOutsideList) {
		this.assetOutsideList = assetOutsideList;
	}
	public SelAssetFundAllVO getSelAssetFundAllVO() {
		return selAssetFundAllVO;
	}
	public void setSelAssetFundAllVO(SelAssetFundAllVO selAssetFundAllVO) {
		this.selAssetFundAllVO = selAssetFundAllVO;
	}
	public SelAdjListVO getSelAdjListVO() {
		return selAdjListVO;
	}
	public void setSelAdjListVO(SelAdjListVO selAdjListVO) {
		this.selAdjListVO = selAdjListVO;
	}
	public List getRcmndFundList() {
		return rcmndFundList;
	}
	public void setRcmndFundList(List rcmndFundList) {
		this.rcmndFundList = rcmndFundList;
	}

}
