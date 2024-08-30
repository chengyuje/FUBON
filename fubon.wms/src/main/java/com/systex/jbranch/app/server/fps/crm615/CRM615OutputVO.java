package com.systex.jbranch.app.server.fps.crm615;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class CRM615OutputVO extends PagingOutputVO{
	private List<Map<String,Object>> resultList;
	
	private BigDecimal twoYAgoFee;
	private BigDecimal oneYAgoFee;
	private BigDecimal lastYearFee;
	private BigDecimal thisYearFee;
	private BigDecimal thisYInsure;
	private BigDecimal prftLastYear;
	private BigDecimal prftNewestYear;
	
	private String DATA_PERIOD_LAST_YEAR;
	private String DATA_PERIOD_NEWEST_YEAR;
	//#0504
	private String PRFT_LAST_YEAR_NOTE; // 前一年貢獻度註記
	private String PRFT_NEWEST_YEAR_NOTE; // 最新年貢獻度註記
	
	
	
	public String getDATA_PERIOD_LAST_YEAR() {
		return DATA_PERIOD_LAST_YEAR;
	}
	public void setDATA_PERIOD_LAST_YEAR(String dATA_PERIOD_LAST_YEAR) {
		DATA_PERIOD_LAST_YEAR = dATA_PERIOD_LAST_YEAR;
	}
	public String getDATA_PERIOD_NEWEST_YEAR() {
		return DATA_PERIOD_NEWEST_YEAR;
	}
	public void setDATA_PERIOD_NEWEST_YEAR(String dATA_PERIOD_NEWEST_YEAR) {
		DATA_PERIOD_NEWEST_YEAR = dATA_PERIOD_NEWEST_YEAR;
	}
	public BigDecimal getLastYearFee() {
		return lastYearFee;
	}
	public void setLastYearFee(BigDecimal lastYearFee) {
		this.lastYearFee = lastYearFee;
	}
	public BigDecimal getThisYearFee() {
		return thisYearFee;
	}
	public void setThisYearFee(BigDecimal thisYearFee) {
		this.thisYearFee = thisYearFee;
	}
	public List<Map<String, Object>> getResultList() {
		return resultList;
	}
	public void setResultList(List<Map<String, Object>> resultList) {
		this.resultList = resultList;
	}
	public BigDecimal getThisYInsure() {
		return thisYInsure;
	}
	public void setThisYInsure(BigDecimal thisYInsure) {
		this.thisYInsure = thisYInsure;
	}
	public BigDecimal getTwoYAgoFee() {
		return twoYAgoFee;
	}
	public void setTwoYAgoFee(BigDecimal twoYAgoFee) {
		this.twoYAgoFee = twoYAgoFee;
	}
	public BigDecimal getOneYAgoFee() {
		return oneYAgoFee;
	}
	public void setOneYAgoFee(BigDecimal oneYAgoFee) {
		this.oneYAgoFee = oneYAgoFee;
	}
	public BigDecimal getPrftLastYear() {
		return prftLastYear;
	}
	public void setPrftLastYear(BigDecimal prftLastYear) {
		this.prftLastYear = prftLastYear;
	}
	public BigDecimal getPrftNewestYear() {
		return prftNewestYear;
	}
	public void setPrftNewestYear(BigDecimal prftNewestYear) {
		this.prftNewestYear = prftNewestYear;
	}
	public String getPRFT_LAST_YEAR_NOTE() {
		return PRFT_LAST_YEAR_NOTE;
	}
	public void setPRFT_LAST_YEAR_NOTE(String pRFT_LAST_YEAR_NOTE) {
		PRFT_LAST_YEAR_NOTE = pRFT_LAST_YEAR_NOTE;
	}
	public String getPRFT_NEWEST_YEAR_NOTE() {
		return PRFT_NEWEST_YEAR_NOTE;
	}
	public void setPRFT_NEWEST_YEAR_NOTE(String pRFT_NEWEST_YEAR_NOTE) {
		PRFT_NEWEST_YEAR_NOTE = pRFT_NEWEST_YEAR_NOTE;
	}
	
	
	
}
