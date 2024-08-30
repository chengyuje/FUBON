package com.systex.jbranch.app.server.fps.iot920;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class INSIDInputVOinputVO extends PagingInputVO{
	
	private String INS_KIND;
	private String REG_TYPE;
	private String BRANCH_NBR;
	
	public String getINS_KIND() {
		return INS_KIND;
	}
	public String getREG_TYPE() {
		return REG_TYPE;
	}
	public String getBRANCH_NBR() {
		return BRANCH_NBR;
	}
	public void setINS_KIND(String iNS_KIND) {
		INS_KIND = iNS_KIND;
	}
	public void setREG_TYPE(String rEG_TYPE) {
		REG_TYPE = rEG_TYPE;
	}
	public void setBRANCH_NBR(String bRANCH_NBR) {
		BRANCH_NBR = bRANCH_NBR;
	}
	
	
}
