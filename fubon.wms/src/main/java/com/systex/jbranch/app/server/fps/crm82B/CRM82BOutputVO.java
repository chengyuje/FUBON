package com.systex.jbranch.app.server.fps.crm82B;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class CRM82BOutputVO extends PagingOutputVO{
	private List<Map<String,Object>> resultList; //明細資料
	private BigDecimal SUMTrustVal;	//總投資金額(台幣)
	private List<Map<String,Object>> txnList;  //交易明細
	
	
	public List<Map<String,Object>> getTxnList() {
		return txnList;
	}
	public void setTxnList(List<Map<String,Object>> txnList) {
		this.txnList = txnList;
	}
	public List<Map<String,Object>> getResultList() {
		return resultList;
	}
	public void setResultList(List<Map<String,Object>> resultList) {
		this.resultList = resultList;
	}
	public BigDecimal getSUMTrustVal() {
		return SUMTrustVal;
	}
	public void setSUMTrustVal(BigDecimal SUMTrustVal) {
		this.SUMTrustVal = SUMTrustVal;
	}
	
}
