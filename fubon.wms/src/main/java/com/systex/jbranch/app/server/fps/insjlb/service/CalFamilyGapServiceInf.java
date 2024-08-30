package com.systex.jbranch.app.server.fps.insjlb.service;

import com.systex.jbranch.app.server.fps.insjlb.vo.CalFamilyGapInputVO;
import com.systex.jbranch.app.server.fps.insjlb.vo.CalFamilyGapOutputVO;
import com.systex.jbranch.platform.common.errHandle.JBranchException;

public interface CalFamilyGapServiceInf {
	/**計算家庭財務安全缺口*/
	public CalFamilyGapOutputVO doCalFamilyGap(CalFamilyGapInputVO inputVo) throws JBranchException;
}
