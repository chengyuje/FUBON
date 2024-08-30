package com.systex.jbranch.app.server.fps.sot240;

import java.text.SimpleDateFormat;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.server.fps.sot230.SOT230InputVO;
import com.systex.jbranch.app.server.fps.sot230.SOT230OutputVO;
import com.systex.jbranch.app.server.fps.sot705.CustOrderETFVO;
import com.systex.jbranch.app.server.fps.sot705.SOT705;
import com.systex.jbranch.app.server.fps.sot705.SOT705InputVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/*
 * 
 * 	
 *   @author Rick
 *   @date 2017/11/10
 *   @spec ETF/股票取消交易、即時查詢 系統畫面
 */
@Component("sot240")
@Scope("request")
public class SOT240 extends FubonWmsBizLogic {

	SimpleDateFormat sdfYYYYMMDD = new SimpleDateFormat("yyyyMMdd");

	public void query(Object body, IPrimitiveMap header) throws Exception {
		
		SOT230InputVO inputVO = (SOT230InputVO) body;
		SOT230OutputVO outputVO = new SOT230OutputVO();

		SOT705InputVO sot705InputVO = new SOT705InputVO();
		sot705InputVO.setCustId(inputVO.getCustId());
		sot705InputVO.setQueryType("SOT240");
		
		SOT705 sot705 = (SOT705) PlatformContext.getBean("sot705");
		List<CustOrderETFVO> cOETFVOs = sot705.getCustOrderETFData(sot705InputVO);
		
		outputVO.setCustOrderETFVOs(cOETFVOs);
		
		sendRtnObject(outputVO);
	}
}
