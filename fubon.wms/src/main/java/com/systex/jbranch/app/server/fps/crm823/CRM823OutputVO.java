package com.systex.jbranch.app.server.fps.crm823;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class CRM823OutputVO extends PagingOutputVO {
	private List<Map<String, Object>> resultList;
	private List<Map<String, Object>> txnList; // 交易明細
	private List<Map<String, Object>> divList; // 配息明細

	private BigDecimal bondAmount = new BigDecimal(0);
	private BigDecimal snAmount = new BigDecimal(0);

	public List<Map<String, Object>> getResultList() {
		return resultList;
	}

	public void setResultList(List<Map<String, Object>> resultList) {
		this.resultList = resultList;
	}

	public List<Map<String, Object>> getTxnList() {
		return txnList;
	}

	public void setTxnList(List<Map<String, Object>> txnList) {
		this.txnList = txnList;
	}

	public List<Map<String, Object>> getDivList() {
		return divList;
	}

	public void setDivList(List<Map<String, Object>> divList) {
		this.divList = divList;
	}

	public BigDecimal getBondAmount() {
		return bondAmount;
	}

	public void setBondAmount(BigDecimal bondAmount) {
		this.bondAmount = bondAmount;
	}

	public BigDecimal getSnAmount() {
		return snAmount;
	}

	public void setSnAmount(BigDecimal snAmount) {
		this.snAmount = snAmount;
	}
}
