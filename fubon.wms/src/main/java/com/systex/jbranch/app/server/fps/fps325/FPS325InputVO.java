package com.systex.jbranch.app.server.fps.fps325;

import java.math.BigDecimal;
import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class FPS325InputVO extends PagingInputVO{
	private BigDecimal TARGET;
	private String PLANHEAD;
	private BigDecimal ONETIME;

	private String RISK_ATTR;
	private Double VOLATILITY;

	private List<FPS325PrdInputVO> prdList;

	public BigDecimal getTARGET() {
		return TARGET;
	}

	public void setTARGET(BigDecimal tARGET) {
		TARGET = tARGET;
	}

	public String getPLANHEAD() {
		return PLANHEAD;
	}

	public void setPLANHEAD(String pLANHEAD) {
		PLANHEAD = pLANHEAD;
	}

	public BigDecimal getONETIME() {
		return ONETIME;
	}

	public void setONETIME(BigDecimal oNETIME) {
		ONETIME = oNETIME;
	}

	public String getRISK_ATTR() {
		return RISK_ATTR;
	}

	public void setRISK_ATTR(String rISK_ATTR) {
		RISK_ATTR = rISK_ATTR;
	}

	public Double getVOLATILITY() {
		return VOLATILITY;
	}

	public void setVOLATILITY(Double vOLATILITY) {
		VOLATILITY = vOLATILITY;
	}

	public List<FPS325PrdInputVO> getPrdList() {
		return prdList;
	}

	public void setPrdList(List<FPS325PrdInputVO> prdList) {
		this.prdList = prdList;
	}
}