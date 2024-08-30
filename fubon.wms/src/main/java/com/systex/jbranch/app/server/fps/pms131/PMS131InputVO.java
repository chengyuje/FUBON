package com.systex.jbranch.app.server.fps.pms131;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class PMS131InputVO extends PagingInputVO {

	private String JOB_TITLE_NAME;
	private String ARBRM;
	
	public String getJOB_TITLE_NAME() {
		return JOB_TITLE_NAME;
	}
	
	public void setJOB_TITLE_NAME(String jOB_TITLE_NAME) {
		JOB_TITLE_NAME = jOB_TITLE_NAME;
	}
	
	public String getARBRM() {
		return ARBRM;
	}
	
	public void setARBRM(String aRBRM) {
		ARBRM = aRBRM;
	}

}
