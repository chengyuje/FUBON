package com.systex.jbranch.app.server.fps.crm232;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.server.fps.crm230.CRM230;
import com.systex.jbranch.app.server.fps.crm230.CRM230OutputVO;
import com.systex.jbranch.app.server.fps.crm230.CRM230_ALLInputVO;
import com.systex.jbranch.app.server.fps.crm232.CRM232;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataManager.DataManager;
import com.systex.jbranch.platform.common.dataManager.WorkStation;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * MENU
 * 
 * @author Stella
 * @date 2016/05/27 初版
 * @spec null
 */

@Component("crm232")
@Scope("request")
public class CRM232 extends FubonWmsBizLogic{
	
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(CRM232.class);
	
	public void inquire(Object body, IPrimitiveMap header) throws JBranchException {
		
		WorkStation ws = DataManager.getWorkStation(uuid);
		CRM232InputVO inputVO = (CRM232InputVO) body;
		
		CRM230_ALLInputVO inputVO_all = new CRM230_ALLInputVO();
		inputVO_all.setCrm232inputVO(inputVO);
		inputVO_all.setCrm230inputVO(inputVO);
		inputVO_all.setAvailRegionList(getUserVariable(FubonSystemVariableConsts.AVAILREGIONLIST));
		inputVO_all.setAvailAreaList(getUserVariable(FubonSystemVariableConsts.AVAILAREALIST));
		inputVO_all.setAvailBranchList(getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
		inputVO_all.setLoginEmpID(ws.getUser().getUserID());
		inputVO_all.setLoginRole(getUserVariable(FubonSystemVariableConsts.LOGINROLE));
		
		CRM230 crm230 = (CRM230) PlatformContext.getBean("crm230");
		CRM230OutputVO outputVO_crm230 = new CRM230OutputVO();
		
		outputVO_crm230 = crm230.inquire_common(inputVO_all, "CRM232");

		this.sendRtnObject(outputVO_crm230);
	}

//	public void getMKT_TIER(Object body, IPrimitiveMap header) throws JBranchException {
//		
//		CRM232InputVO inputVO = (CRM232InputVO) body;
//		CRM232OutputVO outputVO = new CRM232OutputVO();
//		
//		dam = this.getDataAccessManager();
//
//		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
//		StringBuffer sql = new StringBuffer();
//		sql.append("SELECT DISTINCT MKT_TIER2 from TBPRD_INVEST_AREA WHERE MKT_TIER1 = :mkt_tier ");
//		queryCondition.setObject("mkt_tier", inputVO.getMkt_tier1());
//		queryCondition.setQueryString(sql.toString());
//		
//		List list = dam.exeQuery(queryCondition);
//		
//		outputVO.setResultList(list);
//		
//		this.sendRtnObject(outputVO);
//	}
	

}
