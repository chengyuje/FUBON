package com.systex.jbranch.app.server.fps.prd151;

import java.math.BigDecimal;
import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class PRD151OutputVO extends PagingOutputVO {
	private List resultList;
	private String PRICE_REMARK;
	private String P_START_HOUR;
	private String P_START_MIN;
	private String P_END_HOUR;
	private String P_END_MIN;
	private String T_START_HOUR;
	private String T_START_MIN;
	private String T_END_HOUR;
	private String T_END_MIN;
	private BigDecimal C_RM_PROFEE;
	private BigDecimal MINUF_MON;
	
	public List getResultList() {
		return resultList;
	}
	public void setResultList(List resultList) {
		this.resultList = resultList;
	}
	public String getPRICE_REMARK() {
		return PRICE_REMARK;
	}
	public void setPRICE_REMARK(String pRICE_REMARK) {
		PRICE_REMARK = pRICE_REMARK;
	}
	public String getP_START_HOUR() {
		return P_START_HOUR;
	}
	public void setP_START_HOUR(String p_START_HOUR) {
		P_START_HOUR = p_START_HOUR;
	}
	public String getP_START_MIN() {
		return P_START_MIN;
	}
	public void setP_START_MIN(String p_START_MIN) {
		P_START_MIN = p_START_MIN;
	}
	public String getP_END_HOUR() {
		return P_END_HOUR;
	}
	public void setP_END_HOUR(String p_END_HOUR) {
		P_END_HOUR = p_END_HOUR;
	}
	public String getP_END_MIN() {
		return P_END_MIN;
	}
	public void setP_END_MIN(String p_END_MIN) {
		P_END_MIN = p_END_MIN;
	}
	public String getT_START_HOUR() {
		return T_START_HOUR;
	}
	public void setT_START_HOUR(String t_START_HOUR) {
		T_START_HOUR = t_START_HOUR;
	}
	public String getT_START_MIN() {
		return T_START_MIN;
	}
	public void setT_START_MIN(String t_START_MIN) {
		T_START_MIN = t_START_MIN;
	}
	public String getT_END_HOUR() {
		return T_END_HOUR;
	}
	public void setT_END_HOUR(String t_END_HOUR) {
		T_END_HOUR = t_END_HOUR;
	}
	public String getT_END_MIN() {
		return T_END_MIN;
	}
	public void setT_END_MIN(String t_END_MIN) {
		T_END_MIN = t_END_MIN;
	}
	public BigDecimal getC_RM_PROFEE() {
		return C_RM_PROFEE;
	}
	public void setC_RM_PROFEE(BigDecimal c_RM_PROFEE) {
		C_RM_PROFEE = c_RM_PROFEE;
	}
	public BigDecimal getMINUF_MON() {
		return MINUF_MON;
	}
	public void setMINUF_MON(BigDecimal mINUF_MON) {
		MINUF_MON = mINUF_MON;
	}
	
}
