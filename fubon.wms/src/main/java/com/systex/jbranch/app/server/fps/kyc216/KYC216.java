package com.systex.jbranch.app.server.fps.kyc216;

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


@Component("kyc216")
@Scope("request")
public class KYC216 extends FubonWmsBizLogic{
	Logger logger = LoggerFactory.getLogger(this.getClass());
	
	public void queryData(Object body, IPrimitiveMap<Object> header) throws JBranchException{
		KYC216InputVO inputVO = (KYC216InputVO) body;
		KYC216OutputVO outputVO = new KYC216OutputVO();
		try {
			QueryConditionIF qc = getDataAccessManager().getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuilder sb = new StringBuilder();
			sb.append(" SELECT RLR_VERSION, CUST_RL_ID as LRATE_CUST_RL_ID, RL_NAME as LRATE_RL_NAME, RL_UP_RATE as LRATE_RL_UP_RATE");
			sb.append(" FROM TBKYC_QUESTIONNAIRE_RISK_LRATE ");
			sb.append(" WHERE RLR_VERSION = :rlr_version ");
			sb.append(" ORDER BY CUST_RL_ID ASC ");
			qc.setObject("rlr_version", inputVO.getRLR_VERSION());
			qc.setQueryString(sb.toString());
			outputVO.setLRateList(getDataAccessManager().exeQuery(qc));
			sendRtnObject(outputVO);
		
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s", StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}
	
}
