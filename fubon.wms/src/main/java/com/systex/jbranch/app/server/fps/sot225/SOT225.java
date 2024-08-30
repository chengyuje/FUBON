package com.systex.jbranch.app.server.fps.sot225;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.server.fps.sot705.CustAssetETFVO;
import com.systex.jbranch.app.server.fps.sot705.SOT705;
import com.systex.jbranch.app.server.fps.sot705.SOT705InputVO;
import com.systex.jbranch.app.server.fps.sot705.SOT705OutputVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * @spec 金錢信託_ETF股票庫存查詢
 */
@Component("sot225")
@Scope("request")
public class SOT225 extends FubonWmsBizLogic {
	private DataAccessManager dam = null;
	
	public void getCustAssetETFMonData (Object body, IPrimitiveMap header) throws JBranchException, Exception {
		SOT225InputVO inputVO = (SOT225InputVO) body;
		SOT225OutputVO outputVO = new SOT225OutputVO();
		SOT705InputVO inputVO705 = new SOT705InputVO();
		SOT705OutputVO outputVO705 = new SOT705OutputVO();
		
		inputVO705.setCustId(inputVO.getCustID());
		
		SOT705 sot705 = (SOT705) PlatformContext.getBean("sot705");
		outputVO705 = sot705.getCustAssetETFMonData(inputVO705);
		
		outputVO.setResultList(outputVO705.getCustAssetETFMonList());
		this.sendRtnObject(outputVO);
	}
}