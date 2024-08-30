package com.systex.jbranch.app.server.fps.sqm310;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.common.fps.table.TBSQM_RSA_RC_PARVO;
import com.systex.jbranch.app.common.fps.table.TBSQM_RSA_SC_PARPK;
import com.systex.jbranch.app.common.fps.table.TBSQM_RSA_SC_PARVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.util.IPrimitiveMap;

@Component("sqm310")
@Scope("request")
public class SQM310 extends FubonWmsBizLogic {
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(SQM310.class);
	
	public void init_rc_par(Object body, IPrimitiveMap header) throws JBranchException {
		SQM310OutputVO return_VO = new SQM310OutputVO();
		dam = this.getDataAccessManager();
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT * FROM TBSQM_RSA_RC_PAR ORDER BY ROLE_NAME");
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		return_VO.setResultList(list);
		
		this.sendRtnObject(return_VO);
	}
	
	public void init_sc_par(Object body, IPrimitiveMap header) throws JBranchException {
		SQM310OutputVO return_VO = new SQM310OutputVO();
		dam = this.getDataAccessManager();
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT * FROM TBSQM_RSA_SC_PAR ORDER BY LEAD_TYPE, LEAD_SOURCE_ID");
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		return_VO.setResultList(list);
		
		this.sendRtnObject(return_VO);
	}
	
	public void save_rc(Object body, IPrimitiveMap header) throws JBranchException {
		SQM310InputVO inputVO = (SQM310InputVO) body;
		dam = this.getDataAccessManager();
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		queryCondition.setQueryString("truncate table TBSQM_RSA_RC_PAR");
		dam.exeUpdate(queryCondition);
		
		for(Map<String, Object> map : inputVO.getTotalList()) {
			TBSQM_RSA_RC_PARVO vo = new TBSQM_RSA_RC_PARVO();
			vo.setPREQ(new BigDecimal(map.get("PREQ").toString()));
			vo.setROLE_ID(map.get("ROLE_ID").toString());
			vo.setROLE_NAME(map.get("ROLE_NAME").toString());
			vo.setSC_CNT_TOT(new BigDecimal(map.get("SC_CNT_TOT").toString()));
			vo.setSC_CNT_L(new BigDecimal(map.get("SC_CNT_L").toString()));
			vo.setSP_CUST_PC(new BigDecimal(map.get("SP_CUST_PC").toString()));

			dam.create(vo);
		}
		
		this.sendRtnObject(null);
	}
	
	public void inquire_sc(Object body, IPrimitiveMap header) throws JBranchException {
		SQM310InputVO inputVO = (SQM310InputVO) body;
		SQM310OutputVO return_VO = new SQM310OutputVO();
		dam = this.getDataAccessManager();
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("select a.CAMPAIGN_ID, a.CAMPAIGN_NAME, a.STEP_ID, a.LEAD_SOURCE_ID, a.LEAD_TYPE, 0 as SC_CNT from TBCAM_SFA_CAMPAIGN a where 1=1 ");
		if(inputVO.getsDate() != null && inputVO.geteDate() != null) {
			sql.append("and a.START_DATE BETWEEN TRUNC(:start) AND TRUNC(:end) ");
			queryCondition.setObject("start", inputVO.getsDate());
			queryCondition.setObject("end", inputVO.geteDate());
		}
		else if(inputVO.getsDate() != null) {
			sql.append("and a.START_DATE >= TRUNC(:start) ");
			queryCondition.setObject("start", inputVO.getsDate());
		}
		else if(inputVO.geteDate() != null) {
			sql.append("and a.START_DATE <= TRUNC(:end) ");
			queryCondition.setObject("end", inputVO.geteDate());
		}
		if (StringUtils.isNotBlank(inputVO.getLead_type())) {
			sql.append("AND a.LEAD_TYPE = :lead_type ");
			queryCondition.setObject("lead_type", inputVO.getLead_type());
		}
		if("Y".equals(inputVO.getClosed())) {
			sql.append("AND not EXISTS (select 1 from TBCAM_SFA_LEADS b where  a.CAMPAIGN_ID = b.CAMPAIGN_ID and a.STEP_ID = b.STEP_ID and substr(b.LEAD_STATUS,1,2) <> '03' ");
			sql.append("group by b.CAMPAIGN_ID,b.STEP_ID,substr(b.LEAD_STATUS,1,2) having count(1) > 0) ");
		} else {
			sql.append("AND EXISTS (select 1 from TBCAM_SFA_LEADS b where  a.CAMPAIGN_ID = b.CAMPAIGN_ID and a.STEP_ID = b.STEP_ID and substr(b.LEAD_STATUS,1,2) <> '03' ");
			sql.append("group by b.CAMPAIGN_ID,b.STEP_ID,substr(b.LEAD_STATUS,1,2) having count(1) > 0) ");
		}
		if (StringUtils.isNotBlank(inputVO.getCamp_name())) {
			sql.append("AND a.CAMPAIGN_NAME like :camp_name ");
			queryCondition.setObject("camp_name", "%" + inputVO.getCamp_name() + "%");
		}
		sql.append("order by a.LEAD_TYPE, a.LEAD_SOURCE_ID ");
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		return_VO.setResultList(list);
		
		this.sendRtnObject(return_VO);
	}
	
	public void save_sc(Object body, IPrimitiveMap header) throws JBranchException {
		SQM310InputVO inputVO = (SQM310InputVO) body;
		dam = this.getDataAccessManager();
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		queryCondition.setQueryString("truncate table TBSQM_RSA_SC_PAR");
		dam.exeUpdate(queryCondition);
		
		for(Map<String, Object> map : inputVO.getTotalList()) {
			TBSQM_RSA_SC_PARVO vo = new TBSQM_RSA_SC_PARVO();
			TBSQM_RSA_SC_PARPK pk = new TBSQM_RSA_SC_PARPK(); 
			pk.setCAMPAIGN_ID(map.get("CAMPAIGN_ID").toString());
			pk.setSTEP_ID(map.get("STEP_ID").toString());
			vo.setcomp_id(pk);
			vo.setCAMPAIGN_NAME(ObjectUtils.toString(map.get("CAMPAIGN_NAME")));
			vo.setLEAD_SOURCE_ID(ObjectUtils.toString(map.get("LEAD_SOURCE_ID")));
			vo.setLEAD_TYPE(ObjectUtils.toString(map.get("LEAD_TYPE")));
			vo.setSC_CNT(new BigDecimal(map.get("SC_CNT").toString()));
			dam.create(vo);
		}
		
		this.sendRtnObject(null);
	}
	
	
}