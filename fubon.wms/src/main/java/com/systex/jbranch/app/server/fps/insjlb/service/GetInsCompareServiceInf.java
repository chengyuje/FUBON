package com.systex.jbranch.app.server.fps.insjlb.service;

import com.systex.jbranch.app.server.fps.insjlb.vo.GetInsCompareInputVO;
import com.systex.jbranch.app.server.fps.insjlb.vo.GetInsCompareOutputVO;
import com.systex.jbranch.platform.common.errHandle.JBranchException;

public interface GetInsCompareServiceInf {
	/**getInsCompare：保險險種比較<br>
	 * GetInsCompareInputVO：<br>
	 * --- ist<String> lstInsProd：險種清單：商品ID(資訊源商品id集合
	 */
	public GetInsCompareOutputVO getInsCompare(GetInsCompareInputVO inputVo) throws JBranchException;
}
