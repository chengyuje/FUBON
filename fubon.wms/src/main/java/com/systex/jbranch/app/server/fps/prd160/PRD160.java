package com.systex.jbranch.app.server.fps.prd160;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * prd160
 * 
 * @author moron
 * @date 2016/05/27
 * @spec null
 */
@Component("prd160")
@Scope("request")
public class PRD160 extends FubonWmsBizLogic {
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(PRD160.class);
	
	public void getInsName(Object body, IPrimitiveMap header) throws JBranchException {
		PRD160InputVO inputVO = (PRD160InputVO) body;
		PRD160OutputVO return_VO = new PRD160OutputVO();
		dam = this.getDataAccessManager();
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("select INSPRD_NAME from TBPRD_INS where PRD_ID = :id ");
		queryCondition.setObject("id", inputVO.getIns_id());
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		if (list.size() > 0)
			return_VO.setIns_name(ObjectUtils.toString(list.get(0).get("INSPRD_NAME")));
		else
			return_VO.setIns_name(null);
		this.sendRtnObject(return_VO);
	}
	
	public void inquire(Object body, IPrimitiveMap header) throws JBranchException {
		PRD160InputVO inputVO = (PRD160InputVO) body;
		PRD160OutputVO return_VO = new PRD160OutputVO();
		dam = this.getDataAccessManager();
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT a.* FROM TBPRD_INS a LEFT JOIN TBPRD_INSINFO b on a.PRD_ID = b.PRD_ID WHERE 1 = 1 ");
		// where
		if (StringUtils.isNotBlank(inputVO.getIns_id())) {
			sql.append("and a.PRD_ID like :prd_id ");
			queryCondition.setObject("prd_id", inputVO.getIns_id() + "%");
		}
		if (StringUtils.isNotBlank(inputVO.getIns_name())) {
			sql.append("and a.INSPRD_NAME like :name ");
			queryCondition.setObject("name", "%" + inputVO.getIns_name() + "%");
		}
		if(inputVO.getInsprd_annual() != null) {
			sql.append("and a.INSPRD_ANNUAL = :insprd_annual ");
			queryCondition.setObject("insprd_annual", inputVO.getInsprd_annual());
		}
		if (StringUtils.isNotBlank(inputVO.getIns_type())) {
			sql.append("and a.INS_TYPE = :ins_type ");
			queryCondition.setObject("ins_type", inputVO.getIns_type());
		}
		if(inputVO.getSale_sdate() != null) {
			sql.append("and a.SALE_SDATE >= TRUNC(:start) ");
			queryCondition.setObject("start", inputVO.getSale_sdate());
		}
		if(inputVO.getSale_edate() != null) {
			sql.append("and a.SALE_EDATE < TRUNC(:end)+1 ");
			queryCondition.setObject("end", inputVO.getSale_edate());
		}
		if (StringUtils.isNotBlank(inputVO.getObu_buy())) {
			sql.append("and a.OBU_BUY = :obu_buy ");
			queryCondition.setObject("obu_buy", inputVO.getObu_buy());
		}
		if (StringUtils.isNotBlank(inputVO.getGua_annual())) {
			sql.append("and a.GUARANTEE_ANNUAL = :gua_annual ");
			queryCondition.setObject("gua_annual", inputVO.getGua_annual());
		}
		if (StringUtils.isNotBlank(inputVO.getCurr_cd())) {
			sql.append("and a.CURR_CD = :curr ");
			queryCondition.setObject("curr", inputVO.getCurr_cd());
		}
		if (StringUtils.isNotBlank(inputVO.getMain_rider())) {
			sql.append("and a.MAIN_RIDER = :main_rider ");
			queryCondition.setObject("main_rider", inputVO.getMain_rider());
		}
		if (StringUtils.isNotBlank(inputVO.getIs_annuity())) {
			// Y:年金, 其他:壽險
			if("Y".equals(inputVO.getIs_annuity()))
				sql.append("and a.IS_ANNUITY = 'Y' ");
			else
				sql.append("and (a.IS_ANNUITY != 'Y' OR a.IS_ANNUITY IS NULL) ");
		}
		if (StringUtils.isNotBlank(inputVO.getIs_increasing())) {
			// Y:增額型, 其他:空白
			if("Y".equals(inputVO.getIs_increasing()))
				sql.append("and a.IS_INCREASING = 'Y' ");
			else
				sql.append("and (a.IS_INCREASING != 'Y' OR a.IS_INCREASING IS NULL) ");
		}
		if (StringUtils.isNotBlank(inputVO.getIs_repay())) {
			if("Y".equals(inputVO.getIs_repay()))
				sql.append("and a.IS_REPAY = 'Y' ");
			else
				sql.append("and (a.IS_REPAY != 'Y' OR a.IS_REPAY IS NULL) ");
		}
		if (StringUtils.isNotBlank(inputVO.getIs_rate_change())) {
			if("Y".equals(inputVO.getIs_rate_change()))
				sql.append("and a.IS_RATE_CHANGE = 'Y' ");
			else
				sql.append("and (a.IS_RATE_CHANGE != 'Y' OR a.IS_RATE_CHANGE IS NULL) ");
		}
		if (StringUtils.isNotBlank(inputVO.getIs_inv())) {
			if("Y".equals(inputVO.getIs_inv()))
				sql.append("and b.IS_INV = 'Y' ");
			else
				sql.append("and (b.IS_INV != 'Y' OR b.IS_INV IS NULL) ");
		}
		// 適配只是記憶cust_id
		// 是否可申購，若沒維護上架起迄日，歸為不可申購
		// 若修改請同時修改prd210 java checkID // sort
		if ("1".equals(inputVO.getType()) || "2".equals(inputVO.getType()))
			sql.append("and sysdate between a.SALE_SDATE and NVL(a.SALE_EDATE , sysdate) ");
		else if ("3".equals(inputVO.getType()))
			sql.append("and sysdate not between a.SALE_SDATE and NVL(a.SALE_EDATE , sysdate) ");
		//
		sql.append("ORDER BY a.PRD_ID");
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		return_VO.setResultList(list);
		this.sendRtnObject(return_VO);
	}
	
	public void getInsInfo(Object body, IPrimitiveMap header) throws JBranchException {
		PRD160InputVO inputVO = (PRD160InputVO) body;
		PRD160OutputVO return_VO = new PRD160OutputVO();
		dam = this.getDataAccessManager();
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT a.PRD_ID, a.INSPRD_NAME, a.INSPRD_ANNUAL, a.INS_TYPE, a.GUARANTEE_ANNUAL, a.CURR_CD, a.SALE_SDATE, a.SALE_EDATE, a.OBU_BUY, a.MAIN_RIDER, a.IS_ANNUITY, a.IS_INCREASING, a.IS_REPAY, a.IS_RATE_CHANGE, ");
		sql.append("b.MIN_AGE, b.MAX_AGE ");
		sql.append("FROM TBPRD_INS a ");
		sql.append("LEFT JOIN TBPRD_INS_AGE b on a.KEY_NO = b.F_KEY_NO and b.INSURED_OBJECT = '1' ");
		sql.append("WHERE a.KEY_NO = :key_no ");
		queryCondition.setObject("key_no", inputVO.getKey_no());
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		return_VO.setResultList(list);
		this.sendRtnObject(return_VO);
	}
	
	public void getInsRisk(Object body, IPrimitiveMap header) throws JBranchException {
		PRD160InputVO inputVO = (PRD160InputVO) body;
		PRD160OutputVO return_VO = new PRD160OutputVO();
		dam = this.getDataAccessManager();
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("select TARGET_ID, LINKED_NAME, PRD_RISK from TBPRD_INS_LINKING ");
		sql.append("where INSPRD_ID = :prd_id ");
		queryCondition.setObject("prd_id", inputVO.getIns_id());
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		return_VO.setResultList(list);
		this.sendRtnObject(return_VO);
	}
	
	public void getInsFeature(Object body, IPrimitiveMap header) throws JBranchException {
		PRD160InputVO inputVO = (PRD160InputVO) body;
		PRD160OutputVO return_VO = new PRD160OutputVO();
		dam = this.getDataAccessManager();
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("select b.IS_RETIRED,b.IS_EDUCATION,b.IS_PURPOSE,b.IS_LIFE_INS,b.IS_ACCIDENT,b.IS_MEDICAL,b.IS_DISEASES ");
		sql.append("from (select * from TBPRD_INS where KEY_NO = :key_no) a ");
		sql.append("left join TBPRD_INSINFO b on a.PRD_ID = b.PRD_ID ");
		queryCondition.setObject("key_no", inputVO.getKey_no());
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		return_VO.setResultList(list);
		this.sendRtnObject(return_VO);
	}
	
	
}