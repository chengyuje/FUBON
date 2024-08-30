package com.systex.jbranch.app.server.fps.pqc200;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class PQC200OutputVO extends PagingOutputVO {

	private List<Map<String, Object>> resultList;

	private Date showStartDate;
	private Date showEndDate;

	private String START_DATE;
	private String END_DATE;
	private BigDecimal PRD_QUOTA;
	private BigDecimal MIN_QUOTA;
	private BigDecimal MAX_QUOTA;
	private BigDecimal TOTAL_QUOTA;
	private BigDecimal LAVE_QUOTA;
	private String TOTAL_QUOTA_TYPE;

	public String getTOTAL_QUOTA_TYPE() {
		return TOTAL_QUOTA_TYPE;
	}

	public void setTOTAL_QUOTA_TYPE(String tOTAL_QUOTA_TYPE) {
		TOTAL_QUOTA_TYPE = tOTAL_QUOTA_TYPE;
	}

	public Date getShowStartDate() {
		return showStartDate;
	}

	public void setShowStartDate(Date showStartDate) {
		this.showStartDate = showStartDate;
	}

	public Date getShowEndDate() {
		return showEndDate;
	}

	public void setShowEndDate(Date showEndDate) {
		this.showEndDate = showEndDate;
	}

	public String getSTART_DATE() {
		return START_DATE;
	}

	public void setSTART_DATE(String sTART_DATE) {
		START_DATE = sTART_DATE;
	}

	public String getEND_DATE() {
		return END_DATE;
	}

	public void setEND_DATE(String eND_DATE) {
		END_DATE = eND_DATE;
	}

	public BigDecimal getPRD_QUOTA() {
		return PRD_QUOTA;
	}

	public void setPRD_QUOTA(BigDecimal pRD_QUOTA) {
		PRD_QUOTA = pRD_QUOTA;
	}

	public BigDecimal getMIN_QUOTA() {
		return MIN_QUOTA;
	}

	public void setMIN_QUOTA(BigDecimal mIN_QUOTA) {
		MIN_QUOTA = mIN_QUOTA;
	}

	public BigDecimal getMAX_QUOTA() {
		return MAX_QUOTA;
	}

	public void setMAX_QUOTA(BigDecimal mAX_QUOTA) {
		MAX_QUOTA = mAX_QUOTA;
	}

	public BigDecimal getTOTAL_QUOTA() {
		return TOTAL_QUOTA;
	}

	public void setTOTAL_QUOTA(BigDecimal tOTAL_QUOTA) {
		TOTAL_QUOTA = tOTAL_QUOTA;
	}

	public BigDecimal getLAVE_QUOTA() {
		return LAVE_QUOTA;
	}

	public void setLAVE_QUOTA(BigDecimal lAVE_QUOTA) {
		LAVE_QUOTA = lAVE_QUOTA;
	}

	public List<Map<String, Object>> getResultList() {
		return resultList;
	}

	public void setResultList(List<Map<String, Object>> resultList) {
		this.resultList = resultList;
	}

}
