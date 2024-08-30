package com.systex.jbranch.app.server.fps.sot709;

import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class SOT709OutputVO extends PagingOutputVO{
	
	private DefaultFeeRateVO defaultFeeRates;//手續費
	private List<PeriodFeeRateVO> periodFeeRateList;//期間議價資料
	private List<SingleFeeRateVO> singleFeeRateList;//單次議價資料
	private List<MeteringFeeRateVO> meteringFeeRateList;//優惠次數手續費資料
	private String errorCode;//錯誤代碼
	private String errorMsg;//錯誤訊息
	public DefaultFeeRateVO getDefaultFeeRates() {
		return defaultFeeRates;
	}
	public void setDefaultFeeRates(DefaultFeeRateVO defaultFeeRates) {
		this.defaultFeeRates = defaultFeeRates;
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
	public List<MeteringFeeRateVO> getMeteringFeeRateList() {
		return meteringFeeRateList;
	}
	public void setMeteringFeeRateList(List<MeteringFeeRateVO> meteringFeeRateList) {
		this.meteringFeeRateList = meteringFeeRateList;
	}
	public String getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
	public String getErrorMsg() {
		return errorMsg;
	}
	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}
	
	
	
}
