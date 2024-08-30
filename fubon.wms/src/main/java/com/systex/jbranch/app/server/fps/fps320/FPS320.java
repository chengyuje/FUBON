package com.systex.jbranch.app.server.fps.fps320;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.util.IPrimitiveMap;

@Component("fps320")
@Scope("request")
public class FPS320 extends FubonWmsBizLogic {
	
	public void inquire (Object body, IPrimitiveMap<Object> header) throws JBranchException {
		FPS320InputVO inputVO = (FPS320InputVO) body;
		FPS320OutputVO outputVO = new FPS320OutputVO();
		DataAccessManager dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		HashMap<String, Object> outputMap = new HashMap<String, Object>();
		outputMap.put("checkHelp", "");
		try {
			StringBuffer sbE = new StringBuffer("SELECT * FROM TBFPS_SPP_PLAN_HELP_E WHERE PLAN_ID = :planId ");
			StringBuffer sbR = new StringBuffer("SELECT * FROM TBFPS_SPP_PLAN_HELP_R WHERE PLAN_ID = :planId ");
			queryCondition.setObject("planId", inputVO.getPlanId());
			queryCondition.setQueryString(sbE.toString());
			List<Map<String, Object>> list = dam.exeQuery(queryCondition);
			if (list.size() > 0) {
				outputMap.put("checkHelp", "EDUCATION");
				outputMap.put("checkHelpValue", list.get(0));
			} else {
				queryCondition.setQueryString(sbR.toString());
				list = dam.exeQuery(queryCondition);
				if (list.size() > 0) {
					outputMap.put("checkHelp", "RETIRE");
					outputMap.put("checkHelpValue", list.get(0));
				}
			}
			outputVO.setOutputMap(outputMap);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		sendRtnObject(outputVO);
	}
	
	
	public void delHelp (Object body, IPrimitiveMap<Object> header) throws JBranchException {
		FPS320InputVO inputVO = (FPS320InputVO) body;
		DataAccessManager dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sbE = new StringBuffer("DELETE TBFPS_SPP_PLAN_HELP_E WHERE PLAN_ID = :planId ");
		StringBuffer sbR = new StringBuffer("DELETE TBFPS_SPP_PLAN_HELP_R WHERE PLAN_ID = :planId ");
		queryCondition.setObject("planId", inputVO.getPlanId());
		queryCondition.setQueryString(sbE.toString());
		dam.exeUpdate(queryCondition);
		queryCondition.setQueryString(sbR.toString());
		dam.exeUpdate(queryCondition);
		sendRtnObject(null);
	}
}
