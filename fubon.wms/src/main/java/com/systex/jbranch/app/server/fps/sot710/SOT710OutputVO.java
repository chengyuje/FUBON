package com.systex.jbranch.app.server.fps.sot710;

import java.math.BigDecimal;
import java.util.List;

import com.systex.jbranch.fubon.commons.esb.vo.nrbrva3.NRBRVA3OutputDetailsVO;
import com.systex.jbranch.fubon.commons.esb.vo.nrbrvc3.NRBRVC3OutputDetailsVO;
import com.systex.jbranch.fubon.commons.esb.vo.nrbrvc4.NRBRVC4OutputDetailsVO;
import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class SOT710OutputVO extends PagingOutputVO {
	
	private BigDecimal defaultFeeRate;
	private BigDecimal bestFeeRate;
	private List<SingleFeeRateVO> feeTypeList;
	private String errorCode;
	private String errorTxt;
	private List<PeriodFeeRateVO> periodFeeRateList;
	private List<SingleFeeRateVO> singleFeeRateList;
	private List<AvailBalanceVO> availBalanceList;
	public List<SingleFeeRateVO> getFeeTypeList() {
		return feeTypeList;
	}

	public void setFeeTypeList(List<SingleFeeRateVO> feeTypeList) {
		this.feeTypeList = feeTypeList;
	}

	public List<PeriodFeeRateVO> getPeriodFeeRateList() {
		return periodFeeRateList;
	}

	public void setPeriodFeeRateList(List<PeriodFeeRateVO> periodFeeRateList) {
		this.periodFeeRateList = periodFeeRateList;
	}

	public List<SingleFeeRateVO> getSingleFeeRateList() {
		return singleFeeRateList;
	}

	public void setSingleFeeRateList(List<SingleFeeRateVO> singleFeeRateList) {
		this.singleFeeRateList = singleFeeRateList;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorTxt() {
		return errorTxt;
	}

	public void setErrorTxt(String errorTxt) {
		this.errorTxt = errorTxt;
	}

	public BigDecimal getDefaultFeeRate() {
		return defaultFeeRate;
	}
	
	public void setDefaultFeeRate(BigDecimal defaultFeeRate) {
		this.defaultFeeRate = defaultFeeRate;
	}
	
	public BigDecimal getBestFeeRate() {
		return bestFeeRate;
	}
	
	public void setBestFeeRate(BigDecimal bestFeeRate) {
		this.bestFeeRate = bestFeeRate;
	}
	public List<AvailBalanceVO> getAvailBalanceList() {
		return availBalanceList;
	}

	public void setAvailBalanceList(List<AvailBalanceVO> availBalanceList) {
		this.availBalanceList = availBalanceList;
	}
	@Override
	public String toString() {
		return "SOT710OutputVO{" +
				"defaultFeeRate=" + defaultFeeRate +
				", bestFeeRate=" + bestFeeRate +
				", feeTypeList=" + feeTypeList +
				'}';
	}


	
}
