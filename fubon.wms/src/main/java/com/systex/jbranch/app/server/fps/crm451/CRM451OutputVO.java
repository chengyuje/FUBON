package com.systex.jbranch.app.server.fps.crm451;

import java.math.BigDecimal;
import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class CRM451OutputVO extends PagingOutputVO {
	private List resultList;
	private BigDecimal aum;
	private BigDecimal yProfee;
	
	public List getResultList() {
		return resultList;
	}
	public void setResultList(List resultList) {
		this.resultList = resultList;
	}
	public BigDecimal getAum() {
		return aum;
	}
	public void setAum(BigDecimal aum) {
		this.aum = aum;
	}
	public BigDecimal getyProfee() {
		return yProfee;
	}
	public void setyProfee(BigDecimal yProfee) {
		this.yProfee = yProfee;
	}
}
