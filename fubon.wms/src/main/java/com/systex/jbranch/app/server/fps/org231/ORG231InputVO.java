package com.systex.jbranch.app.server.fps.org231;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class ORG231InputVO extends PagingInputVO {

	private String RM1_CNT;
	private String RM2_CNT;
	private String SRM_CNT;

	public String getRM1_CNT() {
		return RM1_CNT;
	}

	public void setRM1_CNT(String rM1_CNT) {
		RM1_CNT = rM1_CNT;
	}

	public String getRM2_CNT() {
		return RM2_CNT;
	}

	public void setRM2_CNT(String rM2_CNT) {
		RM2_CNT = rM2_CNT;
	}

	public String getSRM_CNT() {
		return SRM_CNT;
	}

	public void setSRM_CNT(String sRM_CNT) {
		SRM_CNT = sRM_CNT;
	}

}
