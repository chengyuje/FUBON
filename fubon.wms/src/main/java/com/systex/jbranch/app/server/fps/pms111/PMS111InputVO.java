package com.systex.jbranch.app.server.fps.pms111;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class PMS111InputVO extends PagingInputVO {

	private List<Map<String, String>> rep_execStatisticsList = null;
	private List<Map<String, String>> rep_noContentDtlList = null;

	private String sDate;
	private String eDate;
	
	private BigDecimal monInterval = BigDecimal.ZERO;

	public BigDecimal getMonInterval() {
		return monInterval;
	}

	public void setMonInterval(BigDecimal monInterval) {
		this.monInterval = monInterval;
	}

	public List<Map<String, String>> getRep_execStatisticsList() {
		return rep_execStatisticsList;
	}

	public void setRep_execStatisticsList(List<Map<String, String>> rep_execStatisticsList) {
		this.rep_execStatisticsList = rep_execStatisticsList;
	}

	public List<Map<String, String>> getRep_noContentDtlList() {
		return rep_noContentDtlList;
	}

	public void setRep_noContentDtlList(List<Map<String, String>> rep_noContentDtlList) {
		this.rep_noContentDtlList = rep_noContentDtlList;
	}

	public String getsDate() {
		return sDate;
	}

	public void setsDate(String sDate) {
		this.sDate = sDate;
	}

	public String geteDate() {
		return eDate;
	}

	public void seteDate(String eDate) {
		this.eDate = eDate;
	}

}
