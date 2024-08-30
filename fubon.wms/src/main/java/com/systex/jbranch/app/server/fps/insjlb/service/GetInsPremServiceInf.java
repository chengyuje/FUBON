package com.systex.jbranch.app.server.fps.insjlb.service;

import com.systex.jbranch.app.server.fps.insjlb.vo.GetInsPremInputVO;
import com.systex.jbranch.app.server.fps.insjlb.vo.GetInsPremOutputVO;
import com.systex.jbranch.platform.common.errHandle.JBranchException;

public interface GetInsPremServiceInf {
	public GetInsPremOutputVO getInsPrem(GetInsPremInputVO inputVo)throws JBranchException;
}
