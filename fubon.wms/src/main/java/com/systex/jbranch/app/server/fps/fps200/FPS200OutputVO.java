package com.systex.jbranch.app.server.fps.fps200;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;


public class FPS200OutputVO extends PagingOutputVO {

	private List<Map<String, Object>> custInfo;
	private List<Map<String, Object>> step1List;
	private List<Map<String, Object>> step2List;
	private List<Map<String, Object>> step3List;
	private List<Map<String, Object>> depositList;
	private List<Map<String, Object>> otherList;
	private List<Map<String, Object>> investList;
	private List<Map<String, Object>> modelList;
	private List<Map<String, Object>> fxList;
	private List<Map<String, Object>> prodList;
	private List<Map<String, Object>> effectHisList;
	private List<Map<String, Object>> nowHisList;
	private List<Map<String, Object>> eventList;
	private List<Map<String, Object>> rateList;
	private List<Map<String, Object>> rateAvgList;
	private List<Map<String, Object>> planList;
	private List<Map<String, Object>> printList;
	private String newFlag;
	private String newPlan;
	private String planID;
	private BigDecimal deposit;
	private BigDecimal otherProd;
	private BigDecimal invProd;
	private String marketOverview;
	private List<Map<String, Object>> outputList;
	private boolean hasInvest;

	public String getNewFlag() {
		return newFlag;
	}

	public void setNewFlag(String newFlag) {
		this.newFlag = newFlag;
	}

	public String getNewPlan() {
		return newPlan;
	}

	public void setNewPlan(String newPlan) {
		this.newPlan = newPlan;
	}

	public String getPlanID() {
		return planID;
	}

	public void setPlanID(String planID) {
		this.planID = planID;
	}

	public List<Map<String, Object>> getCustInfo() {
		return custInfo;
	}

	public void setCustInfo(List<Map<String, Object>> custInfo) {
		this.custInfo = custInfo;
	}

	public List<Map<String, Object>> getStep1List() {
		return step1List;
	}

	public void setStep1List(List<Map<String, Object>> step1List) {
		this.step1List = step1List;
	}

	public List<Map<String, Object>> getStep2List() {
		return step2List;
	}

	public void setStep2List(List<Map<String, Object>> step2List) {
		this.step2List = step2List;
	}

	public List<Map<String, Object>> getStep3List() {
		return step3List;
	}

	public void setStep3List(List<Map<String, Object>> step3List) {
		this.step3List = step3List;
	}

	public List<Map<String, Object>> getDepositList() {
		return depositList;
	}

	public void setDepositList(List<Map<String, Object>> depositList) {
		this.depositList = depositList;
	}

	public List<Map<String, Object>> getOtherList() {
		return otherList;
	}

	public void setOtherList(List<Map<String, Object>> otherList) {
		this.otherList = otherList;
	}

	public List<Map<String, Object>> getInvestList() {
		return investList;
	}

	public void setInvestList(List<Map<String, Object>> investList) {
		this.investList = investList;
	}

	public List<Map<String, Object>> getModelList() {
		return modelList;
	}

	public void setModelList(List<Map<String, Object>> modelList) {
		this.modelList = modelList;
	}

	public List<Map<String, Object>> getFxList() {
		return fxList;
	}

	public void setFxList(List<Map<String, Object>> fxList) {
		this.fxList = fxList;
	}

	public List<Map<String, Object>> getRateList() {
		return rateList;
	}

	public void setRateList(List<Map<String, Object>> rateList) {
		this.rateList = rateList;
	}

	public BigDecimal getDeposit() {
		return deposit;
	}

	public void setDeposit(BigDecimal deposit) {
		this.deposit = deposit;
	}

	public BigDecimal getOtherProd() {
		return otherProd;
	}

	public void setOtherProd(BigDecimal otherProd) {
		this.otherProd = otherProd;
	}

	public BigDecimal getInvProd() {
		return invProd;
	}

	public void setInvProd(BigDecimal invProd) {
		this.invProd = invProd;
	}

	public List<Map<String, Object>> getProdList() {
		return prodList;
	}

	public void setProdList(List<Map<String, Object>> prodList) {
		this.prodList = prodList;
	}

	public List<Map<String, Object>> getEffectHisList() {
		return effectHisList;
	}

	public void setEffectHisList(List<Map<String, Object>> effectHisList) {
		this.effectHisList = effectHisList;
	}

	public List<Map<String, Object>> getNowHisList() {
		return nowHisList;
	}

	public void setNowHisList(List<Map<String, Object>> nowHisList) {
		this.nowHisList = nowHisList;
	}

	public List<Map<String, Object>> getEventList() {
		return eventList;
	}

	public void setEventList(List<Map<String, Object>> eventList) {
		this.eventList = eventList;
	}

	public List<Map<String, Object>> getRateAvgList() {
		return rateAvgList;
	}

	public void setRateAvgList(List<Map<String, Object>> rateAvgList) {
		this.rateAvgList = rateAvgList;
	}

	public List<Map<String, Object>> getPlanList() {
		return planList;
	}

	public void setPlanList(List<Map<String, Object>> planList) {
		this.planList = planList;
	}

	public List<Map<String, Object>> getPrintList() {
		return printList;
	}

	public void setPrintList(List<Map<String, Object>> printList) {
		this.printList = printList;
	}

	public String getMarketOverview() {
		return marketOverview;
	}
	public void setMarketOverview(String marketOverview) {
		this.marketOverview = marketOverview;
	}

  public List<Map<String, Object>> getOutputList() {
    return outputList;
  }

  public void setOutputList(List<Map<String, Object>> outputList) {
    this.outputList = outputList;
  }

  public boolean isHasInvest() {
    return hasInvest;
  }

  public void setHasInvest(boolean hasInvest) {
    this.hasInvest = hasInvest;
  }

}
