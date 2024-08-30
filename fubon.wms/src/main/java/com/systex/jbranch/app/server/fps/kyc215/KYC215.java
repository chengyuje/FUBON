package com.systex.jbranch.app.server.fps.kyc215;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.StringUtil;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;

@Component("kyc215")
@Scope("request")
public class KYC215 extends FubonWmsBizLogic{
	Logger logger = LoggerFactory.getLogger(this.getClass());
	
	public void queryData(Object body, IPrimitiveMap<Object> header) throws JBranchException{
		KYC215InputVO inputVO = (KYC215InputVO) body;
		KYC215OutputVO outputVO = new KYC215OutputVO();
		try {
			DataAccessManager dam = this.getDataAccessManager();
			QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuilder sb = new StringBuilder();
			sb.append(" select C_LEVEL, W_LEVEL, CUST_RL_ID from TBKYC_QUESTIONNAIRE_RISK_SCORE ");
			sb.append(" where RS_VERSION =  :rs_version");
			qc.setObject("rs_version", inputVO.getRS_VERSION());
			qc.setQueryString(sb.toString());
			List<Map<String, Object>> resultList = dam.exeQuery(qc);
			outputVO.setResultList(resultList);
			
			//C-風險承受能力分數
			sb = new StringBuilder();
			sb.append(" SELECT DISTINCT C_VAL ");
			sb.append(" FROM ( ");
			sb.append("       SELECT DISTINCT C_SCORE_BTN AS C_VAL ");
			sb.append("       FROM TBKYC_QUESTIONNAIRE_RISK_SCORE ");
			sb.append("       WHERE RS_VERSION = :rs_version ");
			sb.append("       UNION ALL ");
			sb.append("       SELECT DISTINCT C_SCORE_TOP AS C_VAL "); 
			sb.append("       FROM TBKYC_QUESTIONNAIRE_RISK_SCORE ");
			sb.append("       WHERE RS_VERSION = :rs_version ) ");
			sb.append(" ORDER BY C_VAL ASC ");
			qc.setObject("rs_version", inputVO.getRS_VERSION());
			qc.setQueryString(sb.toString());
			List<Map<String, Object>> cList = dam.exeQuery(qc);
			outputVO.setCList(cList);
			
			//W-風險偏好分數
			sb = new StringBuilder();
			sb.append(" SELECT DISTINCT W_VAL ");
			sb.append(" FROM ( ");
			sb.append("       SELECT DISTINCT W_SCORE_BTN AS W_VAL ");
			sb.append("       FROM TBKYC_QUESTIONNAIRE_RISK_SCORE ");
			sb.append("       WHERE RS_VERSION = :rs_version ");
			sb.append("       UNION ALL ");
			sb.append("       SELECT DISTINCT W_SCORE_TOP AS W_VAL "); 
			sb.append("       FROM TBKYC_QUESTIONNAIRE_RISK_SCORE ");
			sb.append("       WHERE RS_VERSION = :rs_version ) ");
			sb.append(" ORDER BY W_VAL ASC ");
			qc.setObject("rs_version", inputVO.getRS_VERSION());
			qc.setQueryString(sb.toString());
			List<Map<String, Object>> wList = dam.exeQuery(qc);
			outputVO.setWList(wList);
			
			sendRtnObject(outputVO);
		
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s", StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}

}
