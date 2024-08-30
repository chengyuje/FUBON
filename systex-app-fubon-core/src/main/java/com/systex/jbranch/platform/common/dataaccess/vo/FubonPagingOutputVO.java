package com.systex.jbranch.platform.common.dataaccess.vo;

import java.math.BigDecimal;

public class FubonPagingOutputVO extends PagingOutputVO {
	private BigDecimal totalCntRecord;
	
	public BigDecimal getTotalCntRecord() {
		return totalCntRecord;
	}
	public void setTotalCntRecord(BigDecimal totalRecord) {
		this.totalCntRecord = totalRecord;
	}
}
