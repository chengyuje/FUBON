package com.systex.jbranch.app.server.fps.prd170;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.server.fps.cam190.CAM190InputVO;
import com.systex.jbranch.app.server.fps.cam190.CAM190OutputVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataManager.DataManager;
import com.systex.jbranch.platform.common.dataManager.WorkStation;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * prd170
 * 
 * @author moron
 * @date 2016/05/27
 * @spec null
 */
@Component("prd170")
@Scope("request")
public class PRD170 extends FubonWmsBizLogic {
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(PRD170.class);
	
	public void inquire(Object body, IPrimitiveMap header) throws JBranchException {
//		PRD170InputVO inputVO = (PRD170InputVO) body;
//		PRD170OutputVO return_VO = new PRD170OutputVO();
//		dam = this.getDataAccessManager();
//		
//		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
//		StringBuffer sql = new StringBuffer();
//		sql.append("SELECT a.*, b.EMP_NAME FROM TBCRM_DKYC_QSTN_SET a ");
//		sql.append("left join TBORG_MEMBER b on a.MODIFIER = b.EMP_ID where 1=1 ");
		
	}
	
	public void inquireEditData(Object body, IPrimitiveMap header) throws JBranchException {
		PRD170InputVO inputVO = (PRD170InputVO) body;
		PRD170OutputVO return_VO = new PRD170OutputVO();
		dam = this.getDataAccessManager();
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT PARAM_TYPE, PARAM_CODE, PARAM_NAME, PARAM_DESC FROM TBSYSPARAMETER  ");
		sql.append("WHERE PARAM_TYPE = 'SYS.MAPP_SSO_ACC_PW' AND PARAM_CODE = 'DOWN_MAPP_URL' ");
		
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> resultList = dam.exeQuery(queryCondition);
		
		if (resultList.size() > 0) {
			return_VO.setResultList(resultList); 
			sendRtnObject(return_VO);
		} else {
			throw new APException("ehl_01_common_009");
		}
	}
	
	public void updateParameter (Object body, IPrimitiveMap header) throws JBranchException {
		WorkStation ws = DataManager.getWorkStation(uuid);
		
		PRD170InputVO inputVO = (PRD170InputVO) body;
		PRD170OutputVO return_VO = new PRD170OutputVO();
		
		StringBuffer sb = new StringBuffer();
		sb.append(" UPDATE TBSYSPARAMETER ");
		sb.append(" SET PARAM_NAME = :PARAM_NAME, MODIFIER = :MODIFIER, LASTUPDATE = SYSDATE ");
		sb.append(" WHERE PARAM_TYPE = :PARAM_TYPE AND PARAM_CODE = :PARAM_CODE ");
		
		dam = this.getDataAccessManager();
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		queryCondition.setQueryString(sb.toString());
		queryCondition.setObject("PARAM_NAME", inputVO.getPARAM_NAME());
		queryCondition.setObject("PARAM_TYPE", inputVO.getPARAM_TYPE());
		queryCondition.setObject("PARAM_CODE", inputVO.getPARAM_CODE());
		queryCondition.setObject("MODIFIER", ws.getUser().getUserID());
		
		int row = dam.exeUpdate(queryCondition);
				
		this.sendRtnObject(return_VO);
	}
	
	
	
	
	
}