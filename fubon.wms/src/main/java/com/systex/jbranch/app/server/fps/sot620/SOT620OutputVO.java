package com.systex.jbranch.app.server.fps.sot620;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.systex.jbranch.app.server.fps.sot714.CentInvDataVO;
import com.systex.jbranch.app.server.fps.sot714.WMSHACRDataVO;
import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class SOT620OutputVO extends PagingOutputVO {
	
	private WMSHACRDataVO currRateData;
	private List<CentInvDataVO> centInvList;
	private Map<String, Object> prodData;
	private BigDecimal currencyRate;

	public WMSHACRDataVO getCurrRateData() {
		return currRateData;
	}

	public void setCurrRateData(WMSHACRDataVO currRateData) {
		this.currRateData = currRateData;
	}

	public List<CentInvDataVO> getCentInvList() {
		return centInvList;
	}

	public void setCentInvList(List<CentInvDataVO> centInvList) {
		this.centInvList = centInvList;
	}

	public Map<String, Object> getProdData() {
		return prodData;
	}

	public void setProdData(Map<String, Object> prodData) {
		this.prodData = prodData;
	}

	public BigDecimal getCurrencyRate() {
		return currencyRate;
	}

	public void setCurrencyRate(BigDecimal currencyRate) {
		this.currencyRate = currencyRate;
	}
	
}
