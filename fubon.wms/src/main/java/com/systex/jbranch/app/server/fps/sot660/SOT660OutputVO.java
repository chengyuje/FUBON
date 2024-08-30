package com.systex.jbranch.app.server.fps.sot660;

import java.math.BigDecimal;

import com.systex.jbranch.app.server.fps.sot701.CustHighNetWorthDataVO;
import com.systex.jbranch.app.server.fps.sot701.CustKYCDataVO;
import com.systex.jbranch.app.server.fps.sot701.FP032675DataVO;
import com.systex.jbranch.app.server.fps.sot714.WMSHAIADataVO;
import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class SOT660OutputVO extends PagingOutputVO {
	
	private CustHighNetWorthDataVO hnwcData;
	private WMSHAIADataVO wmshaiaData;
	private FP032675DataVO fp032675Data;
	private CustKYCDataVO custKYCData;
	private String custRiskChkVal; //風險檢核值
	private BigDecimal curAmt; //金額
	private BigDecimal twdAmt; //折台金額
	private String KYCExpiredYN; //KYC是否已過期 Y:已過期
	
	public CustHighNetWorthDataVO getHnwcData() {
		return hnwcData;
	}
	public void setHnwcData(CustHighNetWorthDataVO hnwcData) {
		this.hnwcData = hnwcData;
	}
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
	public CustKYCDataVO getCustKYCData() {
		return custKYCData;
	}
	public void setCustKYCData(CustKYCDataVO custKYCData) {
		this.custKYCData = custKYCData;
	}
	public String getCustRiskChkVal() {
		return custRiskChkVal;
	}
	public void setCustRiskChkVal(String custRiskChkVal) {
		this.custRiskChkVal = custRiskChkVal;
	}
	public BigDecimal getCurAmt() {
		return curAmt;
	}
	public void setCurAmt(BigDecimal curAmt) {
		this.curAmt = curAmt;
	}
	public BigDecimal getTwdAmt() {
		return twdAmt;
	}
	public void setTwdAmt(BigDecimal twdAmt) {
		this.twdAmt = twdAmt;
	}
	public String getKYCExpiredYN() {
		return KYCExpiredYN;
	}
	public void setKYCExpiredYN(String kYCExpiredYN) {
		KYCExpiredYN = kYCExpiredYN;
	}
	
}
