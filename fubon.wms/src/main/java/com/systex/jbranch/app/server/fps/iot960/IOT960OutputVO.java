package com.systex.jbranch.app.server.fps.iot960;

import java.math.BigDecimal;

import com.systex.jbranch.app.server.fps.sot701.CustHighNetWorthDataVO;
import com.systex.jbranch.app.server.fps.sot701.CustKYCDataVO;
import com.systex.jbranch.app.server.fps.sot701.FP032675DataVO;
import com.systex.jbranch.app.server.fps.sot714.WMSHAIADataVO;
import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class IOT960OutputVO extends PagingOutputVO {
	
	private WMSHAIADataVO wmshaiaData; //投組越級適配
	private FP032675DataVO fp032675Data;
	private String custRiskChkVal; //風險檢核值
	private BigDecimal prodCurrRate; //險種匯率
	
	public WMSHAIADataVO getWmshaiaData() {
		return wmshaiaData;
	}
	public void setWmshaiaData(WMSHAIADataVO wmshaiaData) {
		this.wmshaiaData = wmshaiaData;
	}
	public FP032675DataVO getFp032675Data() {
		return fp032675Data;
	}
	public void setFp032675Data(FP032675DataVO fp032675Data) {
		this.fp032675Data = fp032675Data;
	}
	public String getCustRiskChkVal() {
		return custRiskChkVal;
	}
	public void setCustRiskChkVal(String custRiskChkVal) {
		this.custRiskChkVal = custRiskChkVal;
	}
	public BigDecimal getProdCurrRate() {
		return prodCurrRate;
	}
	public void setProdCurrRate(BigDecimal prodCurrRate) {
		this.prodCurrRate = prodCurrRate;
	}
	
	
}
