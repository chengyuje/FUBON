package com.systex.jbranch.app.server.fps.insjlb;

import com.systex.jbranch.app.server.fps.insjlb.vo.CalFamilyGapInputVO;
import com.systex.jbranch.app.server.fps.insjlb.vo.CalFamilyGapOutputVO;
import com.systex.jbranch.app.server.fps.insjlb.vo.GetInsPremInputVO;
import com.systex.jbranch.app.server.fps.insjlb.vo.GetInsPremOutputVO;
import com.systex.jbranch.platform.common.errHandle.JBranchException;


public interface FubonInsjlbBusinessInf{
	/**計算家庭財務安全缺口*/
	public CalFamilyGapOutputVO calFamilyGap(CalFamilyGapInputVO inputVo) throws JBranchException;
	
	/**試算保費(單一險種)（行外保單輸入時使用 & 規劃試算保費）*/
	public GetInsPremOutputVO getInsPrem(GetInsPremInputVO inputVo)throws JBranchException;
}
