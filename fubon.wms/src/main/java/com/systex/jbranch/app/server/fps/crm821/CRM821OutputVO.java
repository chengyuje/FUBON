package com.systex.jbranch.app.server.fps.crm821;

import java.math.BigDecimal;
import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class CRM821OutputVO extends PagingOutputVO {
	
	private List resultList;
	private List redeemList;

	private BigDecimal fundAmount;

	public List getResultList() {
		return resultList;
	}

	public void setResultList(List resultList) {
		this.resultList = resultList;
	}

	public BigDecimal getFundAmount() {
		return fundAmount;
	}

	public void setFundAmount(BigDecimal fundAmount) {
		this.fundAmount = fundAmount;
	}

	public List getRedeemList() {
		return redeemList;
	}

	public void setRedeemList(List redeemList) {
		this.redeemList = redeemList;
	}
}
