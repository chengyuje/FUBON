package com.systex.jbranch.app.server.fps.kyc213;

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

/**
 * KYC213
 * 
 * @author Jimmy
 * @date 2016/08/03
 * @spec null
 */
@Component("kyc213")
@Scope("request")
public class KYC213 extends FubonWmsBizLogic{
	Logger logger = LoggerFactory.getLogger(this.getClass());
	
	public void queryData(Object body, IPrimitiveMap<Object> header) throws JBranchException{
		KYC213InputVO inputVO = (KYC213InputVO) body;
		KYC213OutputVO outputVO = new KYC213OutputVO();
		try {
			QueryConditionIF qc = getDataAccessManager().getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuilder sb = new StringBuilder();
			sb.append(" select * from TBKYC_QUESTIONNAIRE_RISK_LEVEL ");
			sb.append(" where RL_VERSION =  :rl_version");
			sb.append(" order by CUST_RL_ID ");
			qc.setObject("rl_version", inputVO.getRL_VERSION());
			qc.setQueryString(sb.toString());
			outputVO.setRiskLevelList(getDataAccessManager().exeQuery(qc));
			sendRtnObject(outputVO);
		
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s", StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}
	
	
	public void saveData(Object body, IPrimitiveMap<Object> header) throws JBranchException{
		KYC213InputVO inputVO = (KYC213InputVO) body;
		String loginID = (String) getCommonVariable(SystemVariableConsts.LOGINID);
		QueryConditionIF qc = getDataAccessManager().getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuilder sb = new StringBuilder();
		try {
			if(inputVO.getDEL_CUST_RL_ID().size()>0){
				for(Object delId:inputVO.getDEL_CUST_RL_ID()){
					qc = getDataAccessManager().getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
					sb = new StringBuilder();
					sb.append(" delete from TBKYC_QUESTIONNAIRE_RISK_LEVEL ");
					sb.append(" where RL_VERSION = :rl_version and CUST_RL_ID = :cust_rl_id ");
					qc.setObject("rl_version", inputVO.getRL_VERSION());
					qc.setObject("cust_rl_id", delId);
					qc.setQueryString(sb.toString());
					getDataAccessManager().exeUpdate(qc);
				}
			}
			if(inputVO.getRISK_LEVEL().size()>0){
				List<Map<String, Object>> haveData = new ArrayList<Map<String,Object>>();
				List<Map<String, Object>> notrepeatData = new ArrayList<Map<String,Object>>();
				qc = getDataAccessManager().getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sb = new StringBuilder();
				sb.append(" select * from TBKYC_QUESTIONNAIRE_RISK_LEVEL where RL_VERSION = :rl_version ");
				qc.setObject("rl_version", inputVO.getRL_VERSION());
				qc.setQueryString(sb.toString());
				haveData = getDataAccessManager().exeQuery(qc);
				if(haveData.size()>0){
					notrepeatData.addAll(inputVO.getRISK_LEVEL());
					for(Map<String, Object> updatedata:haveData){
						qc = getDataAccessManager().getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
						sb = new StringBuilder();
						sb.append(" update TBKYC_QUESTIONNAIRE_RISK_LEVEL set PROD_RL_UP_RATE= :prod_rl_up_rate,LASTUPDATE = sysdate,MODIFIER = :modifier ");
						sb.append(" where RL_VERSION = :rl_version and CUST_RL_ID = :cust_rl_id ");
						qc.setObject("prod_rl_up_rate", updatedata.get("PROD_RL_UP_RATE"));
						qc.setObject("rl_version", inputVO.getRL_VERSION());
						qc.setObject("cust_rl_id", updatedata.get("CUST_RL_ID"));
						qc.setObject("modifier", loginID);
						qc.setQueryString(sb.toString());
						getDataAccessManager().exeUpdate(qc);
						for(Map<String, Object> map:inputVO.getRISK_LEVEL()){
							if(updatedata.get("CUST_RL_ID").equals(map.get("CUST_RL_ID"))){
								notrepeatData.remove(map);
							}else{
								
							}
						}
					}
					for(Map<String, Object> addData : notrepeatData){
						qc = getDataAccessManager().getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
						sb = new StringBuilder();
						sb.append(" insert into TBKYC_QUESTIONNAIRE_RISK_LEVEL(RL_VERSION,CUST_RL_ID,RL_NAME,RL_NAME_ENG,RL_UP_RATE,PROD_RL_UP_RATE,VERSION,CREATETIME,CREATOR) ");
						sb.append(" VALUES(:rl_version,:cust_rl_id,:rl_name,:rl_name_eng,:rl_up_rate,:prod_rl_up_rate,'1',sysdate, :creator ) ");
						qc.setObject("rl_version", inputVO.getRL_VERSION());
						qc.setObject("cust_rl_id", addData.get("ECUST_RL_ID"));
						qc.setObject("rl_name", addData.get("ERL_NAME"));
						qc.setObject("rl_name_eng", addData.get("ERL_NAME_ENG"));
						qc.setObject("rl_up_rate", addData.get("ERL_UP_RATE"));
						qc.setObject("prod_rl_up_rate", addData.get("PROD_RL_UP_RATE"));
						qc.setObject("creator", loginID);
						qc.setQueryString(sb.toString());
						getDataAccessManager().exeUpdate(qc);
					}
				}else{
					for(Map<String,Object> map:inputVO.getRISK_LEVEL()){
							qc = getDataAccessManager().getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
							sb = new StringBuilder();
							sb.append(" insert into TBKYC_QUESTIONNAIRE_RISK_LEVEL(RL_VERSION,CUST_RL_ID,RL_NAME,RL_NAME_ENG,RL_UP_RATE,PROD_RL_UP_RATE,VERSION,CREATETIME,CREATOR) ");
							sb.append(" VALUES(:rl_version,:cust_rl_id,:rl_name,rl_name_eng,:rl_up_rate,:prod_rl_up_rate,'1',sysdate, :creator ) ");
							qc.setObject("rl_version", inputVO.getRL_VERSION());
							qc.setObject("cust_rl_id", map.get("ECUST_RL_ID"));
							qc.setObject("rl_name", map.get("ERL_NAME"));
							qc.setObject("rl_name_eng", map.get("ERL_NAME_ENG"));
							qc.setObject("rl_up_rate", map.get("ERL_UP_RATE"));
							qc.setObject("prod_rl_up_rate", map.get("PROD_RL_UP_RATE"));
							qc.setObject("creator", loginID);
							qc.setQueryString(sb.toString());
							getDataAccessManager().exeUpdate(qc);

					}
				}

			}
			sendRtnObject(null);
		} catch (Exception e) {
			throw new APException("系統發生錯誤請洽系統管理員");
		}

	}

}
