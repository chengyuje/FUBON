package com.systex.jbranch.app.server.fps.iot960;

import java.math.BigDecimal;
import java.util.Map;

import com.systex.jbranch.app.server.fps.sot701.CustKYCDataVO;
import com.systex.jbranch.app.server.fps.sot701.FP032675DataVO;
import com.systex.jbranch.app.server.fps.sot714.WMSHACRDataVO;
import com.systex.jbranch.app.server.fps.sot714.WMSHAIADataVO;
import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class IOT960InputVO extends PagingInputVO{
	
	private String CUST_ID;
	private String CUST_RISK;
	private String CURR_CD;
	private String SENIOR_OVER_PVAL;
	private String custRemarks;
	private BigDecimal overPvalAmt;
	
	public String getCUST_ID() {
		return CUST_ID;
	}
	public void setCUST_ID(String cUST_ID) {
		CUST_ID = cUST_ID;
	}
	public String getCUST_RISK() {
		return CUST_RISK;
	}
	public void setCUST_RISK(String cUST_RISK) {
		CUST_RISK = cUST_RISK;
	}
	public String getCURR_CD() {
		return CURR_CD;
	}
	public void setCURR_CD(String cURR_CD) {
		CURR_CD = cURR_CD;
	}
	public String getSENIOR_OVER_PVAL() {
		return SENIOR_OVER_PVAL;
	}
	public void setSENIOR_OVER_PVAL(String sENIOR_OVER_PVAL) {
		SENIOR_OVER_PVAL = sENIOR_OVER_PVAL;
	}
	public String getCustRemarks() {
		return custRemarks;
	}
	public void setCustRemarks(String custRemarks) {
		this.custRemarks = custRemarks;
	}
	public BigDecimal getOverPvalAmt() {
		return overPvalAmt;
	}
	public void setOverPvalAmt(BigDecimal overPvalAmt) {
		this.overPvalAmt = overPvalAmt;
	}
	
}
