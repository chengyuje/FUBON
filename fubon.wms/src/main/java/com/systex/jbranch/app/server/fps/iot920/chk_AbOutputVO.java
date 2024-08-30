package com.systex.jbranch.app.server.fps.iot920;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class chk_AbOutputVO extends PagingOutputVO{
	
	private String AbTranSEQ_YN;	//是否需非常態錄音電訪

	public String getAbTranSEQ_YN() {
		return AbTranSEQ_YN;
	}

	public void setAbTranSEQ_YN(String abTranSEQ_YN) {
		AbTranSEQ_YN = abTranSEQ_YN;
	}
	
	
}
