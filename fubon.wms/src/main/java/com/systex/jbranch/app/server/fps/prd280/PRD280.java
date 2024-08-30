package com.systex.jbranch.app.server.fps.prd280;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.common.fps.table.TBPRD_BONDINFOVO;
import com.systex.jbranch.app.common.fps.table.TBPRD_BONDVO;
import com.systex.jbranch.app.common.fps.table.TBPRD_CALENDARPK;
import com.systex.jbranch.app.common.fps.table.TBPRD_CALENDARVO;
import com.systex.jbranch.app.common.fps.table.TBPRD_ETFVO;
import com.systex.jbranch.app.common.fps.table.TBPRD_FUNDINFOVO;
import com.systex.jbranch.app.common.fps.table.TBPRD_INSINFOVO;
import com.systex.jbranch.app.common.fps.table.TBPRD_REST_DAYPK;
import com.systex.jbranch.app.common.fps.table.TBPRD_REST_DAYVO;
import com.systex.jbranch.app.common.fps.table.TBPRD_SIINFOVO;
import com.systex.jbranch.app.common.fps.table.TBPRD_SIINFO_REVIEWVO;
import com.systex.jbranch.app.common.fps.table.TBPRD_SIVO;
import com.systex.jbranch.app.common.fps.table.TBPRD_SNINFOVO;
import com.systex.jbranch.app.common.fps.table.TBPRD_SNVO;
import com.systex.jbranch.app.common.fps.table.TBPRD_STOCKVO;
import com.systex.jbranch.app.server.fps.prd230.PRD230OutputVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataManager.DataManager;
import com.systex.jbranch.platform.common.dataManager.WorkStation;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.platformdao.table.TBSYSPARAMETERPK;
import com.systex.jbranch.platform.common.platformdao.table.TBSYSPARAMETERVO;
import com.systex.jbranch.platform.common.util.CSVUtil;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * prd280
 * 
 * @author moron
 * @date 2016/09/23
 * @spec null
 */
@Component("prd280")
@Scope("request")
public class PRD280 extends FubonWmsBizLogic {
	
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(PRD280.class);
	
	SimpleDateFormat sdfYYYYMMDD = new SimpleDateFormat("yyyy/MM/dd");
	SimpleDateFormat sdfYYYYMMDDHHMMSS = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	
	public void inquire(Object body, IPrimitiveMap header) throws JBranchException {
		WorkStation ws = DataManager.getWorkStation(uuid);
		PRD280InputVO inputVO = (PRD280InputVO) body;
		PRD280OutputVO return_VO = new PRD280OutputVO();
		dam = this.getDataAccessManager();
		
		// getRoleList
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT COUNT(ROLEID) AS COUNTS FROM TBSYSSECUROLPRIASS ");
		sql.append("WHERE PRIVILEGEID IN (SELECT PRIVILEGEID FROM TBSYSSECUPRIFUNMAP WHERE ITEMID = 'PRD280' AND FUNCTIONID = 'confirm') AND ROLEID = :roleID ");
		queryCondition.setObject("roleID", getUserVariable(FubonSystemVariableConsts.LOGINROLE));
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> privilegeCount = dam.exeQuery(queryCondition);
		//
		sql = new StringBuffer();
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sql.append("SELECT BASE_AMT_OF_PURCHASE, UNIT_AMT_OF_PURCHASE, RATE_GUARANTEEPAY, STOCK_BOND_TYPE, SEQ,PRD_ID,SI_CNAME,START_DATE_OF_BUYBACK,FIXED_DIVIDEND_RATE,FIXED_RATE_DURATION,FLOATING_DIVIDEND_RATE,CURRENCY_EXCHANGE,INVESTMENT_TARGETS,CNR_YIELD,RATE_OF_RETURN,PERFORMANCE_REVIEW,ACT_TYPE,REVIEW_STATUS,CREATOR, PROJECT, CUSTOMER_LEVEL FROM ( ");
		// 覆核主管
		if (((BigDecimal) privilegeCount.get(0).get("COUNTS")).compareTo(new BigDecimal(0)) > 0) {
			sql.append("SELECT rw.BASE_AMT_OF_PURCHASE, rw.UNIT_AMT_OF_PURCHASE, rw.RATE_GUARANTEEPAY, rw.STOCK_BOND_TYPE, rw.SEQ,rw.PRD_ID,si.SI_CNAME,rw.START_DATE_OF_BUYBACK,rw.FIXED_DIVIDEND_RATE,rw.FIXED_RATE_DURATION,rw.FLOATING_DIVIDEND_RATE,rw.CURRENCY_EXCHANGE,rw.INVESTMENT_TARGETS,rw.CNR_YIELD,rw.RATE_OF_RETURN,rw.PERFORMANCE_REVIEW,rw.ACT_TYPE,rw.REVIEW_STATUS,rw.CREATOR, rw.PROJECT, rw.CUSTOMER_LEVEL ");
			sql.append("FROM TBPRD_SIINFO_REVIEW rw left join TBPRD_SI si on rw.PRD_ID = si.PRD_ID ");
			sql.append("WHERE rw.REVIEW_STATUS = 'W' ");
			sql.append("UNION ");
			sql.append("SELECT info.BASE_AMT_OF_PURCHASE, info.UNIT_AMT_OF_PURCHASE, si.RATE_GUARANTEEPAY, info.STOCK_BOND_TYPE, null as SEQ,info.PRD_ID,si.SI_CNAME,info.START_DATE_OF_BUYBACK,info.FIXED_DIVIDEND_RATE,info.FIXED_RATE_DURATION,info.FLOATING_DIVIDEND_RATE,info.CURRENCY_EXCHANGE,info.INVESTMENT_TARGETS,info.CNR_YIELD,info.RATE_OF_RETURN,info.PERFORMANCE_REVIEW,rw.ACT_TYPE,rw.REVIEW_STATUS,null as CREATOR, si.PROJECT, si.CUSTOMER_LEVEL ");
			sql.append("FROM TBPRD_SIINFO info left join TBPRD_SI si on info.PRD_ID = si.PRD_ID ");
			sql.append("left join TBPRD_SIINFO_REVIEW rw on rw.PRD_ID = info.PRD_ID and rw.REVIEW_STATUS = 'W' ");
			sql.append("WHERE NVL(info.REVIEW_STATUS, 'Y') = 'Y' ");
			sql.append("AND info.PRD_ID NOT IN (SELECT PRD_ID FROM TBPRD_SIINFO_REVIEW WHERE REVIEW_STATUS = 'W') ");
		}
		else {
			sql.append("SELECT rw.BASE_AMT_OF_PURCHASE, rw.UNIT_AMT_OF_PURCHASE, rw.RATE_GUARANTEEPAY, rw.STOCK_BOND_TYPE, rw.SEQ,rw.PRD_ID,si.SI_CNAME,rw.START_DATE_OF_BUYBACK,rw.FIXED_DIVIDEND_RATE,rw.FIXED_RATE_DURATION,rw.FLOATING_DIVIDEND_RATE,rw.CURRENCY_EXCHANGE,rw.INVESTMENT_TARGETS,rw.CNR_YIELD,rw.RATE_OF_RETURN,rw.PERFORMANCE_REVIEW,rw.ACT_TYPE,rw.REVIEW_STATUS,null as CREATOR, rw.PROJECT, rw.CUSTOMER_LEVEL ");
			sql.append("FROM TBPRD_SIINFO_REVIEW rw left join TBPRD_SI si on rw.PRD_ID = si.PRD_ID ");
			sql.append("WHERE rw.REVIEW_STATUS = 'W' ");
			sql.append("AND rw.CREATOR = :creator ");
			sql.append("UNION ");
			sql.append("SELECT info.BASE_AMT_OF_PURCHASE, info.UNIT_AMT_OF_PURCHASE, si.RATE_GUARANTEEPAY, info.STOCK_BOND_TYPE, null as SEQ,info.PRD_ID,si.SI_CNAME,info.START_DATE_OF_BUYBACK,info.FIXED_DIVIDEND_RATE,info.FIXED_RATE_DURATION,info.FLOATING_DIVIDEND_RATE,info.CURRENCY_EXCHANGE,info.INVESTMENT_TARGETS,info.CNR_YIELD,info.RATE_OF_RETURN,info.PERFORMANCE_REVIEW,rw.ACT_TYPE,rw.REVIEW_STATUS,null as CREATOR, si.PROJECT, si.CUSTOMER_LEVEL ");
			sql.append("FROM TBPRD_SIINFO info ");
			sql.append("left join TBPRD_SI si on info.PRD_ID = si.PRD_ID ");
			sql.append("left join TBPRD_SIINFO_REVIEW rw on rw.PRD_ID = info.PRD_ID and rw.REVIEW_STATUS = 'W' ");
			sql.append("WHERE NVL(info.REVIEW_STATUS, 'Y') = 'Y' ");
			sql.append("AND info.PRD_ID NOT IN (SELECT PRD_ID FROM TBPRD_SIINFO_REVIEW WHERE REVIEW_STATUS = 'W' AND CREATOR = :creator) ");
			queryCondition.setObject("creator", ws.getUser().getUserID());
		}
		sql.append(") WHERE 1=1 ");
		if (!StringUtils.isBlank(inputVO.getPrd_id())) {
			sql.append("AND PRD_ID like :id ");
			queryCondition.setObject("id", "%" + inputVO.getPrd_id() + "%");
		}
		sql.append("ORDER BY CASE WHEN REVIEW_STATUS = 'W' THEN 0 WHEN REVIEW_STATUS = 'Y' THEN 1 ELSE 2 END ASC,PRD_ID ");
		queryCondition.setQueryString(sql.toString());
		
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		return_VO.setResultList(list);
		this.sendRtnObject(return_VO);
	}
	
	public void checkID(Object body, IPrimitiveMap header) throws JBranchException {
		PRD280InputVO inputVO = (PRD280InputVO) body;
		PRD280OutputVO return_VO = new PRD280OutputVO();
		dam = this.getDataAccessManager();
		
		// update
		if(StringUtils.equals("Y", inputVO.getStatus())) {
			// TBPRD_SI
			QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sql = new StringBuffer();
			sql.append("SELECT PRD_ID,SI_CNAME FROM TBPRD_SI where PRD_ID = :id ");
			queryCondition.setObject("id", inputVO.getPrd_id());
			queryCondition.setQueryString(sql.toString());
			List<Map<String, Object>> list = dam.exeQuery(queryCondition);
			if (list.size() > 0) {
				return_VO.setCname(ObjectUtils.toString(list.get(0).get("SI_CNAME")));
				return_VO.setCanEdit(true);
			}
			else {
				return_VO.setCanEdit(false);
				return_VO.setErrorMsg("ehl_01_common_026");
			}
			
			// TBPRD_SIINFO_REVIEW
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sql = new StringBuffer();
			sql.append("SELECT PRD_ID FROM TBPRD_SIINFO_REVIEW where PRD_ID = :id and REVIEW_STATUS = 'W' ");
			queryCondition.setObject("id", inputVO.getPrd_id());
			queryCondition.setQueryString(sql.toString());
			List<Map<String, Object>> list2 = dam.exeQuery(queryCondition);
			if (list2.size() > 0) {
				return_VO.setCanEdit(false);
				return_VO.setErrorMsg("ehl_01_common_028");
			}
		}
		// sort
		else if(StringUtils.equals("S", inputVO.getStatus())) {
			// TBPRD_SI
			QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sql = new StringBuffer();
			sql.append("SELECT PRD_ID, SI_CNAME, RISKCATE_ID FROM TBPRD_SI where PRD_ID = :id ");
			queryCondition.setObject("id", inputVO.getPrd_id());
			queryCondition.setQueryString(sql.toString());
			List<Map<String, Object>> list = dam.exeQuery(queryCondition);
			if (list.size() > 0) {
				return_VO.setCname(ObjectUtils.toString(list.get(0).get("SI_CNAME")));
				return_VO.setRick_id(ObjectUtils.toString(list.get(0).get("RISKCATE_ID")));
				return_VO.setCanEdit(true);
			}
			else
				return_VO.setCanEdit(false);
		}
		// add
		else {
			// TBPRD_SI
			QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sql = new StringBuffer();
			sql.append("SELECT PRD_ID,SI_CNAME FROM TBPRD_SI where PRD_ID = :id ");
			queryCondition.setObject("id", inputVO.getPrd_id());
			queryCondition.setQueryString(sql.toString());
			List<Map<String, Object>> list = dam.exeQuery(queryCondition);
			if (list.size() > 0) {
				return_VO.setCname(ObjectUtils.toString(list.get(0).get("SI_CNAME")));
				return_VO.setCanEdit(true);
			}
			else {
				return_VO.setCanEdit(false);
				return_VO.setErrorMsg("ehl_01_common_026");
			}
			// TBPRD_SIINFO
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sql = new StringBuffer();
			sql.append("SELECT PRD_ID FROM TBPRD_SIINFO where PRD_ID = :id ");
			queryCondition.setObject("id", inputVO.getPrd_id());
			queryCondition.setQueryString(sql.toString());
			List<Map<String, Object>> list2 = dam.exeQuery(queryCondition);
			if (list2.size() > 0) {
				return_VO.setCanEdit(false);
				return_VO.setErrorMsg("ehl_01_common_027");
			}
			// TBPRD_SIINFO_REVIEW
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sql = new StringBuffer();
			sql.append("SELECT PRD_ID FROM TBPRD_SIINFO_REVIEW where PRD_ID = :id and REVIEW_STATUS = 'W' ");
			queryCondition.setObject("id", inputVO.getPrd_id());
			queryCondition.setQueryString(sql.toString());
			List<Map<String, Object>> list3 = dam.exeQuery(queryCondition);
			if (list3.size() > 0) {
				return_VO.setCanEdit(false);
				return_VO.setErrorMsg("ehl_01_common_028");
			}
		}
		
		this.sendRtnObject(return_VO);
	}
	
	public void addData(Object body, IPrimitiveMap header) throws JBranchException {
		PRD280InputVO inputVO = (PRD280InputVO) body;
		dam = this.getDataAccessManager();
		
		// check again
		// TBPRD_SI
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT PRD_ID FROM TBPRD_SI where PRD_ID = :id ");
		queryCondition.setObject("id", inputVO.getPrd_id());
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		if (list.size() == 0)
			throw new APException("ehl_01_common_026");
		// TBPRD_SIINFO
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sql = new StringBuffer();
		sql.append("SELECT PRD_ID FROM TBPRD_SIINFO where PRD_ID = :id ");
		queryCondition.setObject("id", inputVO.getPrd_id());
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list2 = dam.exeQuery(queryCondition);
		if (list2.size() > 0)
			throw new APException("ehl_01_common_027");
		// TBPRD_SIINFO_REVIEW
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sql = new StringBuffer();
		sql.append("SELECT PRD_ID FROM TBPRD_SIINFO_REVIEW where PRD_ID = :id and REVIEW_STATUS = 'W' ");
		queryCondition.setObject("id", inputVO.getPrd_id());
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list3 = dam.exeQuery(queryCondition);
		if (list3.size() > 0)
			throw new APException("ehl_01_common_028");
		
		// seq
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sql = new StringBuffer();
		sql.append("SELECT SQ_TBPRD_SIINFO_REVIEW.nextval AS SEQ FROM DUAL ");
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> SEQLIST = dam.exeQuery(queryCondition);
		BigDecimal seqNo = (BigDecimal) SEQLIST.get(0).get("SEQ");
		// add TBPRD_SIINFO_REVIEW
		TBPRD_SIINFO_REVIEWVO vo = new TBPRD_SIINFO_REVIEWVO();
		vo.setSEQ(seqNo);
		vo.setPRD_ID(inputVO.getPrd_id());
		if(inputVO.getBuy_Date() != null)
			vo.setSTART_DATE_OF_BUYBACK(new Timestamp(inputVO.getBuy_Date().getTime()));
		if(inputVO.getFixed() != null)
			vo.setFIXED_DIVIDEND_RATE(new BigDecimal(inputVO.getFixed()));
		vo.setFIXED_RATE_DURATION(inputVO.getFix_Date());
		if(inputVO.getFloating() != null)
			vo.setFLOATING_DIVIDEND_RATE(new BigDecimal(inputVO.getFloating()));
		vo.setCURRENCY_EXCHANGE(inputVO.getExchange());
		vo.setINVESTMENT_TARGETS(inputVO.getTarget());
		if(inputVO.getCnr_yield() != null)
			vo.setCNR_YIELD(new BigDecimal(inputVO.getCnr_yield()));
		if(inputVO.getRate_return() != null)
			vo.setRATE_OF_RETURN(new BigDecimal(inputVO.getRate_return()));
		if(inputVO.getSi_project() != null)
			vo.setPROJECT(inputVO.getSi_project());
		if(inputVO.getSi_customer_level() != null)
			vo.setCUSTOMER_LEVEL(inputVO.getSi_customer_level());
		vo.setPERFORMANCE_REVIEW(inputVO.getPerformance_review());
		vo.setACT_TYPE("A");
		vo.setREVIEW_STATUS("W");
		dam.create(vo);
		this.sendRtnObject(null);
	}
	
	private void addReviewStockBondType (String prd_id, String stock_bond_type) throws JBranchException {
		addReviewStockBondType(prd_id, stock_bond_type, null);
	}
	
	private void addStockBondType (String prd_id, String stock_bond_type) throws JBranchException {
		List<Map<String,Object>> resultList = new ArrayList<Map<String,Object>>();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		
		sql.append("SELECT PRD_ID FROM TBPRD_SIINFO where PRD_ID = :prd_id ");
		
		queryCondition.setObject("prd_id", prd_id);
		queryCondition.setQueryString(sql.toString());
		resultList = dam.exeQuery(queryCondition);
		
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		if(resultList.size() > 0) {
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sql = new StringBuffer();
			sql.append("UPDATE TBPRD_SIINFO SET STOCK_BOND_TYPE = :stock_bond_type WHERE PRD_ID = :prd_id ");
			
			queryCondition.setObject("stock_bond_type", stock_bond_type);
			queryCondition.setObject("prd_id", prd_id);
			queryCondition.setQueryString(sql.toString());
			dam.exeUpdate(queryCondition);
		}
	}
	
	public void editData(Object body, IPrimitiveMap header) throws JBranchException {
		PRD280InputVO inputVO = (PRD280InputVO) body;
		dam = this.getDataAccessManager();
		
		// check again
		// TBPRD_SIINFO_REVIEW
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT PRD_ID FROM TBPRD_SIINFO_REVIEW where PRD_ID = :id and REVIEW_STATUS = 'W' ");
		queryCondition.setObject("id", inputVO.getPrd_id());
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		if (list.size() > 0)
			throw new APException("ehl_01_common_028");
		
		// seq
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sql = new StringBuffer();
		sql.append("SELECT SQ_TBPRD_SIINFO_REVIEW.nextval AS SEQ FROM DUAL ");
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> SEQLIST = dam.exeQuery(queryCondition);
		BigDecimal seqNo = (BigDecimal) SEQLIST.get(0).get("SEQ");
		// add TBPRD_SIINFO_REVIEW
		TBPRD_SIINFO_REVIEWVO vo = new TBPRD_SIINFO_REVIEWVO();
		vo.setSEQ(seqNo);
		vo.setPRD_ID(inputVO.getPrd_id());
		if(inputVO.getBuy_Date() != null)
			vo.setSTART_DATE_OF_BUYBACK(new Timestamp(inputVO.getBuy_Date().getTime()));
		else
			vo.setSTART_DATE_OF_BUYBACK(null);
		if(inputVO.getFixed() != null)
			vo.setFIXED_DIVIDEND_RATE(new BigDecimal(inputVO.getFixed()));
		else
			vo.setFIXED_DIVIDEND_RATE(null);
		vo.setFIXED_RATE_DURATION(inputVO.getFix_Date());
		if(inputVO.getFloating() != null)
			vo.setFLOATING_DIVIDEND_RATE(new BigDecimal(inputVO.getFloating()));
		else
			vo.setFLOATING_DIVIDEND_RATE(null);
		vo.setCURRENCY_EXCHANGE(inputVO.getExchange());
		vo.setINVESTMENT_TARGETS(inputVO.getTarget());
		if(inputVO.getCnr_yield() != null)
			vo.setCNR_YIELD(new BigDecimal(inputVO.getCnr_yield()));
		else
			vo.setCNR_YIELD(null);
		if(inputVO.getRate_return() != null)
			vo.setRATE_OF_RETURN(new BigDecimal(inputVO.getRate_return()));
		else
			vo.setRATE_OF_RETURN(null);
		if(inputVO.getSi_project() != null)
			vo.setPROJECT(inputVO.getSi_project());
		else
			vo.setPROJECT(null);
		if(inputVO.getSi_customer_level() != null)
			vo.setCUSTOMER_LEVEL(inputVO.getSi_customer_level());
		else
			vo.setCUSTOMER_LEVEL(null);
		vo.setPERFORMANCE_REVIEW(inputVO.getPerformance_review());
		vo.setACT_TYPE("M");
		vo.setREVIEW_STATUS("W");
		dam.create(vo);
		
		if(StringUtils.isNotBlank(inputVO.getStock_bond_type()))
			addReviewStockBondType(inputVO.getPrd_id(), inputVO.getStock_bond_type());
		
		this.sendRtnObject(null);
	}
	
	public void deleteData(Object body, IPrimitiveMap header) throws JBranchException {
		PRD280InputVO inputVO = (PRD280InputVO) body;
		dam = this.getDataAccessManager();
		String prd_id = inputVO.getPrd_id();
		// check again
		// TBPRD_SIINFO_REVIEW
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT PRD_ID FROM TBPRD_SIINFO_REVIEW where PRD_ID = :id and REVIEW_STATUS = 'W' ");
		queryCondition.setObject("id", prd_id);
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		if (list.size() > 0)
			throw new APException("ehl_01_common_028");
		
		// seq
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sql = new StringBuffer();
		sql.append("SELECT SQ_TBPRD_SIINFO_REVIEW.nextval AS SEQ FROM DUAL ");
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> SEQLIST = dam.exeQuery(queryCondition);
		BigDecimal seqNo = (BigDecimal) SEQLIST.get(0).get("SEQ");
		// get ori
		TBPRD_SIINFOVO vo = new TBPRD_SIINFOVO();
		vo = (TBPRD_SIINFOVO) dam.findByPKey(TBPRD_SIINFOVO.TABLE_UID, prd_id);
		TBPRD_SIVO si_vo = new TBPRD_SIVO();
		si_vo = (TBPRD_SIVO) dam.findByPKey(TBPRD_SIVO.TABLE_UID, prd_id);
		if (vo != null) {
			// add TBPRD_SIINFO_REVIEW
			TBPRD_SIINFO_REVIEWVO rvo = new TBPRD_SIINFO_REVIEWVO();
			rvo.setSEQ(seqNo);
			rvo.setPRD_ID(vo.getPRD_ID());
			rvo.setSTART_DATE_OF_BUYBACK(vo.getSTART_DATE_OF_BUYBACK());
			rvo.setFIXED_DIVIDEND_RATE(vo.getFIXED_DIVIDEND_RATE());
			rvo.setFIXED_RATE_DURATION(vo.getFIXED_RATE_DURATION());
			rvo.setFLOATING_DIVIDEND_RATE(vo.getFLOATING_DIVIDEND_RATE());
			rvo.setCURRENCY_EXCHANGE(vo.getCURRENCY_EXCHANGE());
			rvo.setINVESTMENT_TARGETS(vo.getINVESTMENT_TARGETS());
			rvo.setCNR_YIELD(vo.getCNR_YIELD());
			rvo.setRATE_OF_RETURN(vo.getRATE_OF_RETURN());
			rvo.setPERFORMANCE_REVIEW(vo.getPERFORMANCE_REVIEW());
			rvo.setPROJECT(si_vo.getPROJECT());
			rvo.setCUSTOMER_LEVEL(si_vo.getCUSTOMER_LEVEL());
			rvo.setACT_TYPE("D");
			rvo.setREVIEW_STATUS("W");
			dam.create(rvo);
			
			list = new ArrayList<Map<String,Object>>();
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sb = new StringBuffer();
			sb.append("select STOCK_BOND_TYPE from TBPRD_SIINFO where PRD_ID = :prd_id ");
			
			queryCondition.setObject("prd_id", prd_id);
			queryCondition.setQueryString(sb.toString());
			list = dam.exeQuery(queryCondition);
			if(list.size() > 0) {
				String stock_bond_type = null;
				if(list.get(0).get("STOCK_BOND_TYPE") != null) {
					stock_bond_type = list.get(0).get("STOCK_BOND_TYPE").toString();								
				}
				addReviewStockBondType(prd_id, stock_bond_type);
			}
		} else {
			// 顯示資料不存在
			throw new APException("ehl_01_common_001");
		}
		this.sendRtnObject(null);
	}
	
	public void review(Object body, IPrimitiveMap header) throws JBranchException {
		PRD280InputVO inputVO = (PRD280InputVO) body;
		dam = this.getDataAccessManager();
		
		// 2017/2/23
		for(Map<String, Object> rmap : inputVO.getReview_list()) {
			TBPRD_SIINFO_REVIEWVO rvo = new TBPRD_SIINFO_REVIEWVO();
			BigDecimal seq = new BigDecimal(ObjectUtils.toString(rmap.get("SEQ")));
			rvo = (TBPRD_SIINFO_REVIEWVO) dam.findByPKey(TBPRD_SIINFO_REVIEWVO.TABLE_UID, seq);
			if (rvo != null) {
				// confirm
				if("Y".equals(inputVO.getStatus())) {
					// 新增
					if("A".equals(rvo.getACT_TYPE())) {
						// TBPRD_SIINFO
						TBPRD_SIINFOVO vo = new TBPRD_SIINFOVO();
						vo.setPRD_ID(rvo.getPRD_ID());
						vo.setSTART_DATE_OF_BUYBACK(rvo.getSTART_DATE_OF_BUYBACK());
						vo.setFIXED_DIVIDEND_RATE(rvo.getFIXED_DIVIDEND_RATE());
						vo.setFIXED_RATE_DURATION(rvo.getFIXED_RATE_DURATION());
						vo.setFLOATING_DIVIDEND_RATE(rvo.getFLOATING_DIVIDEND_RATE());
						vo.setCURRENCY_EXCHANGE(rvo.getCURRENCY_EXCHANGE());
						vo.setINVESTMENT_TARGETS(rvo.getINVESTMENT_TARGETS());
						vo.setCNR_YIELD(rvo.getCNR_YIELD());
						vo.setRATE_OF_RETURN(rvo.getRATE_OF_RETURN());
						vo.setPERFORMANCE_REVIEW(rvo.getPERFORMANCE_REVIEW());
						vo.setACT_TYPE("A");
						vo.setREVIEW_STATUS("Y");
						vo.setBASE_AMT_OF_PURCHASE(rvo.getBASE_AMT_OF_PURCHASE());
						vo.setUNIT_AMT_OF_PURCHASE(rvo.getUNIT_AMT_OF_PURCHASE());
						dam.create(vo);
						
						TBPRD_SIVO si_vo = new TBPRD_SIVO();
						si_vo = (TBPRD_SIVO) dam.findByPKey(TBPRD_SIVO.TABLE_UID, rvo.getPRD_ID());
						si_vo.setRATE_GUARANTEEPAY(rvo.getRATE_GUARANTEEPAY());
						si_vo.setPROJECT(rvo.getPROJECT());
						si_vo.setCUSTOMER_LEVEL(rvo.getCUSTOMER_LEVEL());
						dam.update(si_vo);
					}
					// 修改
					else if("M".equals(rvo.getACT_TYPE())) {
						TBPRD_SIINFOVO vo = new TBPRD_SIINFOVO();
						vo = (TBPRD_SIINFOVO) dam.findByPKey(TBPRD_SIINFOVO.TABLE_UID, rvo.getPRD_ID());
						if (vo != null) {
							vo.setSTART_DATE_OF_BUYBACK(rvo.getSTART_DATE_OF_BUYBACK());
							vo.setFIXED_DIVIDEND_RATE(rvo.getFIXED_DIVIDEND_RATE());
							vo.setFIXED_RATE_DURATION(rvo.getFIXED_RATE_DURATION());
							vo.setFLOATING_DIVIDEND_RATE(rvo.getFLOATING_DIVIDEND_RATE());
							vo.setCURRENCY_EXCHANGE(rvo.getCURRENCY_EXCHANGE());
							vo.setINVESTMENT_TARGETS(rvo.getINVESTMENT_TARGETS());
							vo.setCNR_YIELD(rvo.getCNR_YIELD());
							vo.setRATE_OF_RETURN(rvo.getRATE_OF_RETURN());
							vo.setPERFORMANCE_REVIEW(rvo.getPERFORMANCE_REVIEW());
							vo.setACT_TYPE("M");
							vo.setREVIEW_STATUS("Y");
							vo.setBASE_AMT_OF_PURCHASE(rvo.getBASE_AMT_OF_PURCHASE());
							vo.setUNIT_AMT_OF_PURCHASE(rvo.getUNIT_AMT_OF_PURCHASE());
							dam.update(vo);
							
							TBPRD_SIVO si_vo = new TBPRD_SIVO();
							si_vo = (TBPRD_SIVO) dam.findByPKey(TBPRD_SIVO.TABLE_UID, rvo.getPRD_ID());
							si_vo.setRATE_GUARANTEEPAY(rvo.getRATE_GUARANTEEPAY());
							si_vo.setPROJECT(rvo.getPROJECT());
							si_vo.setCUSTOMER_LEVEL(rvo.getCUSTOMER_LEVEL());
							dam.update(si_vo);
						} else {
							// 顯示資料不存在
							throw new APException("ehl_01_common_001");
						}
					}
					// 刪除
					else if("D".equals(rvo.getACT_TYPE())) {
						TBPRD_SIINFOVO vo = new TBPRD_SIINFOVO();
						vo = (TBPRD_SIINFOVO) dam.findByPKey(TBPRD_SIINFOVO.TABLE_UID, rvo.getPRD_ID());
						if (vo != null) {
							dam.delete(vo);
						} else {
							// 顯示資料不存在
							throw new APException("ehl_01_common_001");
						}
					}
					
					if(!"D".equals(rvo.getACT_TYPE())) {
						List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
						QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
						
						StringBuffer sb = new StringBuffer();
						sb.append("select STOCK_BOND_TYPE from TBPRD_SIINFO_REVIEW where SEQ = :seq ");
						
						queryCondition.setObject("seq", seq.toString());
						queryCondition.setQueryString(sb.toString());
						list = dam.exeQuery(queryCondition);
						if(list.size() > 0) {
							String stock_bond_type = null;
							if(list.get(0).get("STOCK_BOND_TYPE") != null) {
								stock_bond_type = list.get(0).get("STOCK_BOND_TYPE").toString();								
							}
							addStockBondType(rvo.getPRD_ID(), stock_bond_type);
						}
					}
				}
				rvo.setREVIEW_STATUS(inputVO.getStatus());
				dam.update(rvo);
			} else
				throw new APException("ehl_01_common_001");
		}
		
		this.sendRtnObject(null);
	}
	
	public void download(Object body, IPrimitiveMap header) throws JBranchException {
		WorkStation ws = DataManager.getWorkStation(uuid);
		PRD280InputVO inputVO = (PRD280InputVO) body;
		PRD280OutputVO return_VO = new PRD280OutputVO();
		dam = this.getDataAccessManager();
		
		// getRoleList
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT COUNT(ROLEID) AS COUNTS FROM TBSYSSECUROLPRIASS ");
		sql.append("WHERE PRIVILEGEID IN (SELECT PRIVILEGEID FROM TBSYSSECUPRIFUNMAP WHERE ITEMID = 'PRD280' AND FUNCTIONID = 'confirm') AND ROLEID = :roleID ");
		queryCondition.setObject("roleID", getUserVariable(FubonSystemVariableConsts.LOGINROLE));
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> privilegeCount = dam.exeQuery(queryCondition);
		//
		sql = new StringBuffer();
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sql.append("SELECT BASE_AMT_OF_PURCHASE, UNIT_AMT_OF_PURCHASE, RATE_GUARANTEEPAY, STOCK_BOND_TYPE, PRD_ID,SI_CNAME,START_DATE_OF_BUYBACK,FIXED_DIVIDEND_RATE,FIXED_RATE_DURATION,FLOATING_DIVIDEND_RATE,CURRENCY_EXCHANGE,INVESTMENT_TARGETS,CNR_YIELD,RATE_OF_RETURN,PERFORMANCE_REVIEW,ACT_TYPE,REVIEW_STATUS,CREATOR FROM ( ");
		// 覆核主管
		if (((BigDecimal) privilegeCount.get(0).get("COUNTS")).compareTo(new BigDecimal(0)) > 0) {
			sql.append("SELECT rw.BASE_AMT_OF_PURCHASE, rw.UNIT_AMT_OF_PURCHASE, rw.RATE_GUARANTEEPAY, rw.STOCK_BOND_TYPE, rw.PRD_ID,si.SI_CNAME,rw.START_DATE_OF_BUYBACK,rw.FIXED_DIVIDEND_RATE,rw.FIXED_RATE_DURATION,rw.FLOATING_DIVIDEND_RATE,rw.CURRENCY_EXCHANGE,rw.INVESTMENT_TARGETS,rw.CNR_YIELD,rw.RATE_OF_RETURN,rw.PERFORMANCE_REVIEW,rw.ACT_TYPE,rw.REVIEW_STATUS,rw.CREATOR ");
			sql.append("FROM TBPRD_SIINFO_REVIEW rw left join TBPRD_SI si on rw.PRD_ID = si.PRD_ID ");
			sql.append("WHERE rw.REVIEW_STATUS = 'W' ");
			sql.append("UNION ");
			sql.append("SELECT info.BASE_AMT_OF_PURCHASE, info.UNIT_AMT_OF_PURCHASE, si.RATE_GUARANTEEPAY, info.STOCK_BOND_TYPE, info.PRD_ID,si.SI_CNAME,info.START_DATE_OF_BUYBACK,info.FIXED_DIVIDEND_RATE,info.FIXED_RATE_DURATION,info.FLOATING_DIVIDEND_RATE,info.CURRENCY_EXCHANGE,info.INVESTMENT_TARGETS,info.CNR_YIELD,info.RATE_OF_RETURN,info.PERFORMANCE_REVIEW,rw.ACT_TYPE,rw.REVIEW_STATUS,null as CREATOR ");
			sql.append("FROM TBPRD_SIINFO info left join TBPRD_SI si on info.PRD_ID = si.PRD_ID ");
			sql.append("left join TBPRD_SIINFO_REVIEW rw on rw.PRD_ID = info.PRD_ID and rw.REVIEW_STATUS = 'W' ");
			sql.append("WHERE NVL(info.REVIEW_STATUS, 'Y') = 'Y' ");
			sql.append("AND info.PRD_ID NOT IN (SELECT PRD_ID FROM TBPRD_SIINFO_REVIEW WHERE REVIEW_STATUS = 'W') ");
		}
		else {
			sql.append("SELECT rw.BASE_AMT_OF_PURCHASE, rw.UNIT_AMT_OF_PURCHASE, rw.RATE_GUARANTEEPAY, rw.STOCK_BOND_TYPE, rw.PRD_ID,si.SI_CNAME,rw.START_DATE_OF_BUYBACK,rw.FIXED_DIVIDEND_RATE,rw.FIXED_RATE_DURATION,rw.FLOATING_DIVIDEND_RATE,rw.CURRENCY_EXCHANGE,rw.INVESTMENT_TARGETS,rw.CNR_YIELD,rw.RATE_OF_RETURN,rw.PERFORMANCE_REVIEW,rw.ACT_TYPE,rw.REVIEW_STATUS,null as CREATOR ");
			sql.append("FROM TBPRD_SIINFO_REVIEW rw left join TBPRD_SI si on rw.PRD_ID = si.PRD_ID ");
			sql.append("WHERE rw.REVIEW_STATUS = 'W' ");
			sql.append("AND rw.CREATOR = :creator ");
			sql.append("UNION ");
			sql.append("SELECT info.BASE_AMT_OF_PURCHASE, info.UNIT_AMT_OF_PURCHASE, si.RATE_GUARANTEEPAY, info.STOCK_BOND_TYPE, info.PRD_ID,si.SI_CNAME,info.START_DATE_OF_BUYBACK,info.FIXED_DIVIDEND_RATE,info.FIXED_RATE_DURATION,info.FLOATING_DIVIDEND_RATE,info.CURRENCY_EXCHANGE,info.INVESTMENT_TARGETS,info.CNR_YIELD,info.RATE_OF_RETURN,info.PERFORMANCE_REVIEW,rw.ACT_TYPE,rw.REVIEW_STATUS,null as CREATOR ");
			sql.append("FROM TBPRD_SIINFO info ");
			sql.append("left join TBPRD_SI si on info.PRD_ID = si.PRD_ID ");
			sql.append("left join TBPRD_SIINFO_REVIEW rw on rw.PRD_ID = info.PRD_ID and rw.REVIEW_STATUS = 'W' ");
			sql.append("WHERE NVL(info.REVIEW_STATUS, 'Y') = 'Y' ");
			sql.append("AND info.PRD_ID NOT IN (SELECT PRD_ID FROM TBPRD_SIINFO_REVIEW WHERE REVIEW_STATUS = 'W' AND CREATOR = :creator) ");
			queryCondition.setObject("creator", ws.getUser().getUserID());
		}
		sql.append(") WHERE 1=1 ");
		if (!StringUtils.isBlank(inputVO.getPrd_id())) {
			sql.append("AND PRD_ID like :id ");
			queryCondition.setObject("id", "%" + inputVO.getPrd_id() + "%");
		}
		sql.append("ORDER BY CASE WHEN REVIEW_STATUS = 'W' THEN 0 WHEN REVIEW_STATUS = 'Y' THEN 1 ELSE 2 END ASC,PRD_ID ");
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		if(list.size() > 0) {
			// gen csv
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			String fileName = "SI清單_"+ sdf.format(new Date()) + "_" + ws.getUser().getUserID() + ".csv";
			List listCSV = new ArrayList();
			for (Map<String, Object> map : list) {
				// 16 column
				String[] records = new String[16];
				int i = 0;
				if ("W".equals(ObjectUtils.toString(map.get("REVIEW_STATUS"))))
					records[i] = "覆核中";
				else
					records[i] = "已覆核";
				records[++i] = checkIsNull(map, "PRD_ID");
				records[++i] = checkIsNull(map, "SI_CNAME");
				records[++i] = "=\"" + checkIsNull(map, "START_DATE_OF_BUYBACK") + "\"";
				records[++i] = checkIsNull(map, "FIXED_DIVIDEND_RATE");
				records[++i] = "=\"" + checkIsNull(map, "FIXED_RATE_DURATION") + "\"";
				records[++i] = checkIsNull(map, "FLOATING_DIVIDEND_RATE");
				records[++i] = checkIsNull(map, "CURRENCY_EXCHANGE");
				records[++i] = checkIsNull(map, "INVESTMENT_TARGETS");
				records[++i] = checkIsNull(map, "CNR_YIELD");
				records[++i] = checkIsNull(map, "RATE_OF_RETURN");
				records[++i] = checkIsNull(map, "PERFORMANCE_REVIEW");
//				if ("W".equals(ObjectUtils.toString(map.get("REVIEW_STATUS"))))
//					records[++i] = "A".equals(checkIsNull(map, "ACT_TYPE")) ? "新增" : "M".equals(checkIsNull(map, "ACT_TYPE")) ? "編輯" : "刪除";
//				else
//					records[++i] = "";
				
				if ("S".equals(ObjectUtils.toString(map.get("STOCK_BOND_TYPE")))) {
					records[++i] = "股票型";
				} else if ("B".equals(ObjectUtils.toString(map.get("STOCK_BOND_TYPE")))) {
					records[++i] = "債券型";
				} else {
					records[++i] = "";
				}
				records[++i] = checkIsNull(map, "RATE_GUARANTEEPAY");
				records[++i] = checkIsNull(map, "BASE_AMT_OF_PURCHASE");
				records[++i] = checkIsNull(map, "UNIT_AMT_OF_PURCHASE");
				
				listCSV.add(records);
			}
			// header
			String[] csvHeader = new String[16];
			int j = 0;
			csvHeader[j] = "覆核狀態";
			csvHeader[++j] = "SI代碼";
			csvHeader[++j] = "SI名稱";
			csvHeader[++j] = "開始受理贖回日";
			csvHeader[++j] = "固定配息率";
			csvHeader[++j] = "固定配息期間";
			csvHeader[++j] = "浮動配息率";
			csvHeader[++j] = "幣轉";
			csvHeader[++j] = "連結標的";
			csvHeader[++j] = "CNR分配率";
			csvHeader[++j] = "銀行收益率";
			csvHeader[++j] = "計績檔次";
//			csvHeader[++j] = "狀態";
			csvHeader[++j] = "股債類型";
			csvHeader[++j] = "保本率";
			csvHeader[++j] = "最低申購金額";
			csvHeader[++j] = "申購單位金額";
						
			CSVUtil csv = new CSVUtil();
			csv.setHeader(csvHeader);  
			csv.addRecordList(listCSV);
			String url = csv.generateCSV();
			// download
			notifyClientToDownloadFile(url, fileName);
		} else
			return_VO.setResultList(list);
		this.sendRtnObject(return_VO);
	}
	
	private String checkIsNull(Map map, String key) {
		if(StringUtils.isNotBlank(ObjectUtils.toString(map.get(key)))) {
			return String.valueOf(map.get(key));
		} else {
			return "";
		}
	}
	
	public void upload(Object body, IPrimitiveMap header) throws JBranchException {
		PRD280InputVO inputVO = (PRD280InputVO) body;
		PRD280OutputVO return_VO = new PRD280OutputVO();
		dam = this.getDataAccessManager();
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy/MM/dd");
		List<String> error = new ArrayList<String>();
		List<String> error2 = new ArrayList<String>();
		List<String> error3 = new ArrayList<String>();
		List<String> error4 = new ArrayList<String>();
		List<String> error5 = new ArrayList<String>();
		List<String> error6 = new ArrayList<String>();
		Set<String> idList = new HashSet<String>();
		String tempPath = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
		List<String[]> dataCsv = CSVUtil.getBig5CSVFile(tempPath, inputVO.getFileName());
		if(!dataCsv.isEmpty()) {
			for(int i = 0;i < dataCsv.size();i++) {
				String[] str = dataCsv.get(i);
				if(i == 0) {
					try {
						if(!"商品代碼".equals(str[0].trim()))
							throw new Exception(str[0]);
						else if(!"開始受理贖回日".equals(str[1].trim()))
							throw new Exception(str[1]);
						else if(!"固定配息率".equals(str[2].trim()))
							throw new Exception(str[2]);
						else if(!"固定配息期間".equals(str[3].trim()))
							throw new Exception(str[3]);
						else if(!"浮動配息率".equals(str[4].trim()))
							throw new Exception(str[4]);
						else if(!"幣轉".equals(str[5].trim()))
							throw new Exception(str[5]);
						else if(!"連結標的".equals(str[6].substring(0, 4)))
							throw new Exception(str[6]);
						else if(!"CNR分配率".equals(str[7].trim()))
							throw new Exception(str[7]);
						else if(!"銀行收益率".equals(str[8].trim()))
							throw new Exception(str[8]);
						else if(!"計績檔次".equals(str[9].trim()))
							throw new Exception(str[9]);
						else if(!"股債類型".equals(str[10].substring(0, 4)))
							throw new Exception(str[10]);
						else if(!"保本率".equals(str[11].trim()))
							throw new Exception(str[11]);
						else if(!"最低申購金額".equals(str[12].trim()))
							throw new Exception(str[12]);
						else if(!"申購單位金額".equals(str[13].trim()))
							throw new Exception(str[13]);
						else if(!"專案代碼".equals(str[14].trim()))
							throw new Exception(str[14]);
						else if(!"客群代碼".equals(str[15].trim()))
							throw new Exception(str[15]);
					} catch(Exception ex) {
						throw new APException(ex.getMessage() + ":上傳格式錯誤，請下載範例檔案");
					}
					continue;
				}
				// check prd_id 請PG判斷，第一欄若為空，就跳過去不判斷。
				if(StringUtils.isBlank(str[0]))
					continue;
				// 使用者很愛重覆上傳
				if(idList.contains(str[0].trim())) {
					error4.add(str[0]);
					continue;
				}
				idList.add(str[0].trim());
				// TBPRD_SI
				QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				StringBuffer sql = new StringBuffer();
				sql.append("SELECT PRD_ID FROM TBPRD_SI where PRD_ID = :id ");
				queryCondition.setObject("id", str[0].trim());
				queryCondition.setQueryString(sql.toString());
				List<Map<String, Object>> list = dam.exeQuery(queryCondition);
				if (list.size() == 0) {
					error.add(str[0]);
					continue;
				}
				//#1404 更新標籤欄位 0608說要走審核流程故隱蔽供之後參考寫法
				//dam.newTransactionExeMethod(this, "updateTagsIndependent", Arrays.asList(str, error6));
				
				
				// TBPRD_SIINFO check edit
				Boolean exist = false;
				queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sql = new StringBuffer();
				sql.append("SELECT PRD_ID FROM TBPRD_SIINFO where PRD_ID = :id ");
				queryCondition.setObject("id", str[0].trim());
				queryCondition.setQueryString(sql.toString());
				List<Map<String, Object>> list2 = dam.exeQuery(queryCondition);
				if (list2.size() > 0)
					exist = true;
				// TBPRD_SIINFO_REVIEW
				queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sql = new StringBuffer();
				sql.append("SELECT PRD_ID FROM TBPRD_SIINFO_REVIEW where PRD_ID = :id and REVIEW_STATUS = 'W' ");
				queryCondition.setObject("id", str[0].trim());
				queryCondition.setQueryString(sql.toString());
				List<Map<String, Object>> list3 = dam.exeQuery(queryCondition);
				if (list3.size() > 0) {
					error2.add(str[0]);
					continue;
				}
				
				// seq
				queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sql = new StringBuffer();
				sql.append("SELECT SQ_TBPRD_SIINFO_REVIEW.nextval AS SEQ FROM DUAL ");
				queryCondition.setQueryString(sql.toString());
				List<Map<String, Object>> SEQLIST = dam.exeQuery(queryCondition);
				BigDecimal seqNo = (BigDecimal) SEQLIST.get(0).get("SEQ");
				// add TBPRD_SIINFO_REVIEW
				TBPRD_SIINFO_REVIEWVO vo = new TBPRD_SIINFO_REVIEWVO();
				TBPRD_SIINFOVO info_vo = new TBPRD_SIINFOVO();
				info_vo = (TBPRD_SIINFOVO) dam.findByPKey(TBPRD_SIINFOVO.TABLE_UID, str[0].trim());
				TBPRD_SIVO si_vo = new TBPRD_SIVO();
				si_vo = (TBPRD_SIVO) dam.findByPKey(TBPRD_SIVO.TABLE_UID, str[0].trim());
				
				vo.setSEQ(seqNo);
				if(utf_8_length(str[0]) > 16) {
					error3.add(str[0]);
					continue;
				}	
				else
					vo.setPRD_ID(str[0].trim());
				//
				if(StringUtils.isNotBlank(str[1]) && !StringUtils.equals(str[1], "$")) {
					try {
						vo.setSTART_DATE_OF_BUYBACK(new Timestamp(sdf.parse(str[1]).getTime()));
					} catch (Exception e) {
						try {
							vo.setSTART_DATE_OF_BUYBACK(new Timestamp(sdf2.parse(str[1]).getTime()));
						} catch (Exception e2) {
							error3.add(str[0]+":"+str[1]);
							continue;
						}
					}
				}
				else
					vo.setSTART_DATE_OF_BUYBACK(info_vo != null ? info_vo.getSTART_DATE_OF_BUYBACK() : null);
//					vo.setSTART_DATE_OF_BUYBACK(null);
				//
				if(StringUtils.isNotBlank(str[2]) && !StringUtils.equals(str[2], "$")) {
					try {
						BigDecimal str2 = new BigDecimal(str[2]);
						// NUMBER(6,2)
						if(getNumOfBigDecimal(str2) > 4)
							throw new Exception("");
						vo.setFIXED_DIVIDEND_RATE(str2);
					} catch (Exception e) {
						error3.add(str[0]+":"+str[2]);
						continue;
					}
				}
				else
					vo.setFIXED_DIVIDEND_RATE(info_vo != null ? info_vo.getFIXED_DIVIDEND_RATE() : null);
//					vo.setFIXED_DIVIDEND_RATE(null);
				//
				if(StringUtils.isNotBlank(str[3]) && !StringUtils.equals(str[3], "$")) {
					if(utf_8_length(str[3]) > 100) {
						error3.add(str[0]+":"+str[3]);
						continue;
					}	
					else
						vo.setFIXED_RATE_DURATION(str[3]);
				}
				else
					vo.setFIXED_RATE_DURATION(info_vo != null ? info_vo.getFIXED_RATE_DURATION() : null);
				
				//
				if(StringUtils.isNotBlank(str[4]) && !StringUtils.equals(str[4], "$")) {
					try {
						BigDecimal str4 = new BigDecimal(str[4]);
						// NUMBER(6,2)
						if(getNumOfBigDecimal(str4) > 4)
							throw new Exception("");
						vo.setFLOATING_DIVIDEND_RATE(str4);
					} catch (Exception e) {
						error3.add(str[0]+":"+str[4]);
						continue;
					}
				}
				else
					vo.setFLOATING_DIVIDEND_RATE(info_vo != null ? info_vo.getFLOATING_DIVIDEND_RATE() : null);
//					vo.setFLOATING_DIVIDEND_RATE(null);
				//
				if(StringUtils.isNotBlank(str[5]) && !StringUtils.equals(str[5], "$")) {
					if(utf_8_length(str[5]) > 5) {
						error3.add(str[0]+":"+str[5]);
						continue;
					}	
					else
						vo.setCURRENCY_EXCHANGE(str[5]);
				}
				else
					vo.setCURRENCY_EXCHANGE(info_vo != null ? info_vo.getCURRENCY_EXCHANGE() : null);
				//
				if(StringUtils.isNotBlank(str[6]) && !StringUtils.equals(str[6], "$")) {
					str[6] = str[6].replace("\n", "").replace("\r", "");
					String[] targets = str[6].split(";");
					if(targets.length > 20) {
						error3.add(str[0]+":"+str[6]);
						continue;
					}	
					else
						vo.setINVESTMENT_TARGETS(str[6]);
				}
				else
					vo.setINVESTMENT_TARGETS(info_vo != null ? info_vo.getINVESTMENT_TARGETS() : null);
//					vo.setINVESTMENT_TARGETS(null);
				//
				if(StringUtils.isNotBlank(str[7]) && !StringUtils.equals(str[7], "$")) {
					try {
						BigDecimal str7 = new BigDecimal(str[7]);
						// NUMBER(9,6)
						if(getNumOfBigDecimal(str7) > 3)
							throw new Exception("");
						vo.setCNR_YIELD(str7);
					} catch (Exception e) {
						error3.add(str[0]+":"+str[7]);
						continue;
					}
				}
				else
					vo.setCNR_YIELD(info_vo != null ? info_vo.getCNR_YIELD() : null);
//					vo.setCNR_YIELD(null);
				//
				if(StringUtils.isNotBlank(str[8]) && !StringUtils.equals(str[8], "$")) {
					try {
						BigDecimal str8 = new BigDecimal(str[8]);
						// NUMBER(9,6)
						if(getNumOfBigDecimal(str8) > 3)
							throw new Exception("");
						vo.setRATE_OF_RETURN(str8);
					} catch (Exception e) {
						error3.add(str[0]+":"+str[8]);
						continue;
					}
				}
				else
					vo.setRATE_OF_RETURN(info_vo != null ? info_vo.getRATE_OF_RETURN() : null);
//					vo.setRATE_OF_RETURN(null);
				//
				if(StringUtils.isNotBlank(str[9]) && !StringUtils.equals(str[9], "$")) {
					if(utf_8_length(str[9]) > 6) {
						error3.add(str[0]+":"+str[9]);
						continue;
					}	
					else
						vo.setPERFORMANCE_REVIEW(str[9]);
				}
				else
					vo.setPERFORMANCE_REVIEW(info_vo != null ? info_vo.getPERFORMANCE_REVIEW() : null);
				
				// 保本率
				if(StringUtils.isNotBlank(str[11]) && !StringUtils.equals(str[11], "$")) {
					try {
						BigDecimal str11 = new BigDecimal(str[11]);
						// NUMBER(12, 2)
						if(getNumOfBigDecimal(str11) > 10)
							throw new Exception("");
						vo.setRATE_GUARANTEEPAY(str11);
					} catch (Exception e) {
						error3.add(str[0]+":"+str[11]);
						continue;
					}
				} else
					vo.setRATE_GUARANTEEPAY(si_vo != null ? si_vo.getRATE_GUARANTEEPAY() : null);

				// 最低申購金額
				if(StringUtils.isNotBlank(str[12]) && !StringUtils.equals(str[12], "$")) {
					try {
						BigDecimal str12 = new BigDecimal(str[12]);
						// NUMBER(13, 2)
						if(getNumOfBigDecimal(str12) > 11)
							throw new Exception("");
						vo.setBASE_AMT_OF_PURCHASE(str12);
					} catch (Exception e) {
						error3.add(str[0]+":"+str[12]);
						continue;
					}
				} else
					vo.setBASE_AMT_OF_PURCHASE(info_vo != null? info_vo.getBASE_AMT_OF_PURCHASE(): null);

				// 申購單位金額
				if(StringUtils.isNotBlank(str[13]) && !StringUtils.equals(str[13], "$")) {
					try {
						BigDecimal str13 = new BigDecimal(str[13]);
						// NUMBER(13, 2)
						if(getNumOfBigDecimal(str13) > 11)
							throw new Exception("");
						vo.setUNIT_AMT_OF_PURCHASE(str13);
					} catch (Exception e) {
						error3.add(str[0]+":"+str[13]);
						continue;
					}
				} else
					vo.setUNIT_AMT_OF_PURCHASE(info_vo != null? info_vo.getUNIT_AMT_OF_PURCHASE(): null);
				
				// 專案代碼
				if (StringUtils.isNotBlank(str[14]) && !StringUtils.equals((str[14].trim()), "$")) {
					if (str[14].length() > 20) {
						error3.add(str[0] + ":" + str[14]);
						continue;
					}

					if (StringUtils.equals((str[14].trim()), "0")) {
						vo.setPROJECT(null);
					}else {
						vo.setPROJECT(str[14].trim());
					}
				} else {
					vo.setPROJECT(si_vo != null ? si_vo.getPROJECT() : null);
				}
				// 客群代碼
				if (StringUtils.isNotBlank(str[15]) && !StringUtils.equals((str[15].trim()), "$")) {
					if (str[15].length() > 20) {
						error3.add(str[0] + ":" + str[15]);
						continue;
					}
					if (StringUtils.equals((str[15].trim()), "0")) {
						vo.setCUSTOMER_LEVEL(null);
					} else {
						vo.setCUSTOMER_LEVEL(str[15].trim());
					}
				} else {
					vo.setCUSTOMER_LEVEL(si_vo != null ? si_vo.getCUSTOMER_LEVEL() : null);
				}
				
				if(StringUtils.isBlank(vo.getCUSTOMER_LEVEL())) {
					if (null != si_vo.getYEAR_OF_MATURITY() && si_vo.getYEAR_OF_MATURITY().floatValue() <= 1.5
							&& "P1".equals(si_vo.getRISKCATE_ID())) {
						vo.setCUSTOMER_LEVEL("A1;A2;A3");
					} else if (null != si_vo.getYEAR_OF_MATURITY() && si_vo.getYEAR_OF_MATURITY().floatValue() >= 3 && si_vo.getYEAR_OF_MATURITY().floatValue() <= 10
							&& ("P2".equals(si_vo.getRISKCATE_ID()) || "P3".equals(si_vo.getRISKCATE_ID()))) {
						vo.setCUSTOMER_LEVEL("A2;A3");
					} else if ("Y".equals(si_vo.getPI_BUY())) {
						vo.setCUSTOMER_LEVEL("A2");
					}
				}
				
				if(!exist)
					vo.setACT_TYPE("A");
				else
					vo.setACT_TYPE("M");
				vo.setREVIEW_STATUS("W");
				dam.create(vo);
				
				if(StringUtils.isNotBlank(str[10])) {
					String stock_bond_type = str[10].trim().toUpperCase();
					if("S".equals(stock_bond_type) || "B".equals(stock_bond_type) || "$".equals(stock_bond_type)) {
						addReviewStockBondType(str[0].trim(), stock_bond_type, info_vo);
					} else {
						throw new JBranchException("股債類型輸入格式有誤，請輸入S或B。");
					}
				} else {
					throw new JBranchException("股債類型為必填欄位。");
				}
			}
		}
		return_VO.setErrorList(error);
		return_VO.setErrorList2(error2);
		return_VO.setErrorList3(error3);
		return_VO.setErrorList4(error4);
		return_VO.setErrorList5(error5);
		this.sendRtnObject(return_VO);
	}
	
	private void addReviewStockBondType(String prd_id, String stock_bond_type, TBPRD_SIINFOVO info_vo) throws DAOException, JBranchException {
		List<Map<String,Object>> resultList = new ArrayList<Map<String,Object>>();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		
		sql.append("SELECT PRD_ID FROM TBPRD_SIINFO_REVIEW where PRD_ID = :prd_id AND REVIEW_STATUS = 'W' ");
		
		queryCondition.setObject("prd_id", prd_id);
		queryCondition.setQueryString(sql.toString());
		resultList = dam.exeQuery(queryCondition);
		
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		if(resultList.size() > 0) {
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sql = new StringBuffer();
			sql.append("UPDATE TBPRD_SIINFO_REVIEW SET STOCK_BOND_TYPE = :stock_bond_type ");
			sql.append("WHERE PRD_ID = :prd_id AND REVIEW_STATUS = 'W' ");
			if("$".equals(stock_bond_type)) {
				queryCondition.setObject("stock_bond_type", info_vo != null ? info_vo.getSTOCK_BOND_TYPE() : null);
			} else {
				queryCondition.setObject("stock_bond_type", stock_bond_type);
			}			
			queryCondition.setObject("prd_id", prd_id);
			queryCondition.setQueryString(sql.toString());
			dam.exeUpdate(queryCondition);
		}
		
	}

	public void downloadSimple(Object body, IPrimitiveMap header) throws Exception {
		this.notifyClientToDownloadFile("doc//PRD//PRD280_EXAMPLE.csv", "上傳指定商品代碼範例.csv");
	}
	
	public void getRank(Object body, IPrimitiveMap header) throws JBranchException {
		PRD280OutputVO return_VO = new PRD280OutputVO();
		dam = this.getDataAccessManager();
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		queryCondition.setQueryString("select PRD_ID, SI_CNAME, RISKCATE_ID, PRD_RANK FROM TBPRD_SI WHERE PRD_RANK IS NOT NULL ORDER BY PRD_RANK");
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		return_VO.setResultList(list);
		this.sendRtnObject(return_VO);
	}
	
	public void saveSort(Object body, IPrimitiveMap header) throws JBranchException {
		PRD280InputVO inputVO = (PRD280InputVO) body;
		dam = this.getDataAccessManager();
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		queryCondition.setQueryString("UPDATE TBPRD_SI SET PRD_RANK = null WHERE PRD_RANK IS NOT NULL");
		dam.exeUpdate(queryCondition);
		
		for(Map<String, Object> map : inputVO.getReview_list()) {
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			queryCondition.setQueryString("UPDATE TBPRD_SI SET PRD_RANK = :rank, PRD_RANK_DATE = sysdate WHERE PRD_ID = :prd_id");
			queryCondition.setObject("rank", map.get("rank"));
			queryCondition.setObject("prd_id", map.get("prd_id"));
			dam.exeUpdate(queryCondition);
		}
		
		this.sendRtnObject(null);
	}
	
	public void upload_inquire(Object body, IPrimitiveMap header) throws JBranchException {
		PRD280InputVO inputVO = (PRD280InputVO) body;
		PRD280OutputVO return_VO = new PRD280OutputVO();
		dam = this.getDataAccessManager();
		
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		// TBSYS_PRD_LINK
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("select a.PRD_ID,b.DOC_ID,b.DOC_NAME,b.DOC_TYPE,c.DOC_FILE_TYPE,c.DOC_URL,c.DOC_START_DATE,c.DOC_DUE_DATE,c.FILE_NAME,c.CREATETIME,'N' as SHARED from ");
		sql.append("(select DOC_ID,PRD_ID from TBSYS_PRD_LINK where PTYPE = :ptype ");
		queryCondition.setObject("ptype", inputVO.getPtype());
		if (!StringUtils.isBlank(inputVO.getPrd_id())) {
			sql.append("and PRD_ID = :prd_id ");
			queryCondition.setObject("prd_id", inputVO.getPrd_id());
		}
		sql.append(") a ");
		sql.append("inner join (select DOC_ID,DOC_NAME,DOC_TYPE from TBSYS_FILE_MAIN where SUBSYSTEM_TYPE = 'PRD' and (DOC_TYPE = '02' or DOC_TYPE = '03') ");
		sql.append(") b on a.DOC_ID = b.DOC_ID ");
		sql.append("inner join (select DOC_ID,DOC_FILE_TYPE,DOC_URL,DOC_START_DATE,DOC_DUE_DATE,FILE_NAME,CREATETIME from TBSYS_FILE_DETAIL where DOC_VERSION_STATUS = '2' and sysdate BETWEEN DOC_START_DATE and DOC_DUE_DATE) c on b.DOC_ID = c.DOC_ID ");
		queryCondition.setQueryString(sql.toString());
		list.addAll(dam.exeQuery(queryCondition));
		
		// TBSYS_PRD_SHARED_LINK
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sql = new StringBuffer();
		sql.append("select b.DOC_ID,b.DOC_NAME,b.DOC_TYPE,c.DOC_FILE_TYPE,c.DOC_URL,c.DOC_START_DATE,c.DOC_DUE_DATE,c.FILE_NAME,c.CREATETIME,'Y' as SHARED from ");
		sql.append("(select DOC_ID from TBSYS_PRD_SHARED_LINK where PTYPE = :ptype) a ");
		queryCondition.setObject("ptype", inputVO.getPtype());
		sql.append("inner join (select DOC_ID,DOC_NAME,DOC_TYPE from TBSYS_FILE_MAIN where SUBSYSTEM_TYPE = 'PRD' and (DOC_TYPE = '02' or DOC_TYPE = '03') ");
		sql.append(") b on a.DOC_ID = b.DOC_ID ");
		sql.append("inner join (select DOC_ID,DOC_FILE_TYPE,DOC_URL,DOC_START_DATE,DOC_DUE_DATE,FILE_NAME,CREATETIME from TBSYS_FILE_DETAIL where DOC_VERSION_STATUS = '2' and sysdate BETWEEN DOC_START_DATE and DOC_DUE_DATE) c on b.DOC_ID = c.DOC_ID ");
		queryCondition.setQueryString(sql.toString());
		list.addAll(dam.exeQuery(queryCondition));
		return_VO.setResultList(list);
		this.sendRtnObject(return_VO);
	}
	
	public void uploadINV(Object body, IPrimitiveMap header) throws JBranchException, Exception {
		PRD280InputVO inputVO = (PRD280InputVO) body;
		PRD230OutputVO return_VO = new PRD230OutputVO();
		dam = this.getDataAccessManager();
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
		List<String> error = new ArrayList<String>();
		List<String> error2 = new ArrayList<String>();
		List<String> error3 = new ArrayList<String>();
		List<String> error4 = new ArrayList<String>();
		Set<String> idList = new HashSet<String>();
		String tempPath = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
		List<String[]> dataCsv = CSVUtil.getBig5CSVFile(tempPath, inputVO.getFileName());
		if(!dataCsv.isEmpty()) {
			for(int i = 0; i < dataCsv.size();i++) {
				String[] str = dataCsv.get(i);
				if(i == 0) {
					try {
						if(!"商品代碼".equals(str[0].substring(0, 4)))
							throw new Exception(str[0]);
						else if(!"募集開始日".equals(str[1].substring(0, 5)))
							throw new Exception(str[1]);
						else if(!"募集結束日".equals(str[2].substring(0, 5)))
							throw new Exception(str[2]);
					} catch(Exception ex) {
						throw new APException(ex.getMessage() + ":上傳格式錯誤，請下載範例檔案");
					}
					continue;
				}
				// check prd_id 請PG判斷，第一欄若為空，就跳過去不判斷。
				if(StringUtils.isBlank(str[0]))
					continue;
				// 使用者很愛重覆上傳
				if(idList.contains(str[0].trim())) {
					error3.add(str[0]);
					continue;
				}
				idList.add(str[0].trim());
				// old code
				switch (inputVO.getPrdType()) {
					case "SI":
						TBPRD_SIINFOVO siInfoVO = new TBPRD_SIINFOVO();
						siInfoVO = (TBPRD_SIINFOVO) dam.findByPKey(TBPRD_SIINFOVO.TABLE_UID, str[0].trim());
						if(siInfoVO == null) {
							error.add(str[0]);
							continue;
						} else {
							// INV_SDATE
							try {
								siInfoVO.setINV_SDATE(new Timestamp(sdf.parse(str[1]).getTime()));
							} catch (Exception e) {
								error2.add(str[0]+":"+str[1]);
								continue;
							}
							// INV_EDATE
							try {
								siInfoVO.setINV_EDATE(new Timestamp(sdf.parse(str[2]).getTime()));
							} catch (Exception e) {
								error2.add(str[0]+":"+str[2]);
								continue;
							}
							dam.update(siInfoVO);
						}
						break;
					case "SN":
						TBPRD_SNINFOVO snInfoVO = new TBPRD_SNINFOVO();
						snInfoVO = (TBPRD_SNINFOVO) dam.findByPKey(TBPRD_SNINFOVO.TABLE_UID, str[0].trim());
						if(snInfoVO == null) {
							error.add(str[0]);
							continue;
						} else {
							// INV_SDATE
							try {
								snInfoVO.setINV_SDATE(new Timestamp(sdf.parse(str[1]).getTime()));
							} catch (Exception e) {
								error2.add(str[0]+":"+str[1]);
								continue;
							}
							// INV_EDATE
							try {
								snInfoVO.setINV_EDATE(new Timestamp(sdf.parse(str[2]).getTime()));
							} catch (Exception e) {
								error2.add(str[0]+":"+str[2]);
								continue;
							}
							dam.update(snInfoVO);
						}
						break;
					case "ETF":
						TBPRD_ETFVO etfVO = new TBPRD_ETFVO();
						etfVO = (TBPRD_ETFVO) dam.findByPKey(TBPRD_ETFVO.TABLE_UID, str[0].trim());
						if(etfVO == null) {
							error.add(str[0]);
							continue;
						} else {
							// INV_SDATE
							try {
								etfVO.setINV_SDATE(new Timestamp(sdf.parse(str[1]).getTime()));
							} catch (Exception e) {
								error2.add(str[0]+":"+str[1]);
								continue;
							}
							// INV_EDATE
							try {
								etfVO.setINV_EDATE(new Timestamp(sdf.parse(str[2]).getTime()));
							} catch (Exception e) {
								error2.add(str[0]+":"+str[2]);
								continue;
							}
							dam.update(etfVO);
						}
						break;
					case "STOCK":
						TBPRD_STOCKVO stockVO = new TBPRD_STOCKVO();
						stockVO = (TBPRD_STOCKVO) dam.findByPKey(TBPRD_STOCKVO.TABLE_UID, str[0].trim());
						if(stockVO == null) {
							error.add(str[0]);
							continue;
						} else {
							// INV_SDATE
							try {
								stockVO.setINV_SDATE(new Timestamp(sdf.parse(str[1]).getTime()));
							} catch (Exception e) {
								error2.add(str[0]+":"+str[1]);
								continue;
							}
							// INV_EDATE
							try {
								stockVO.setINV_EDATE(new Timestamp(sdf.parse(str[2]).getTime()));
							} catch (Exception e) {
								error2.add(str[0]+":"+str[2]);
								continue;
							}
							dam.update(stockVO);
						}
						break;
					case "BOND":
						TBPRD_BONDINFOVO bondInfoVO = new TBPRD_BONDINFOVO();
						bondInfoVO = (TBPRD_BONDINFOVO) dam.findByPKey(TBPRD_BONDINFOVO.TABLE_UID, str[0].trim());
						if(bondInfoVO == null) {
							error.add(str[0]);
							continue;
						} else {
							// INV_SDATE
							try {
								bondInfoVO.setINV_SDATE(new Timestamp(sdf.parse(str[1]).getTime()));
							} catch (Exception e) {
								error2.add(str[0]+":"+str[1]);
								continue;
							}
							// INV_EDATE
							try {
								bondInfoVO.setINV_EDATE(new Timestamp(sdf.parse(str[2]).getTime()));
							} catch (Exception e) {
								error2.add(str[0]+":"+str[2]);
								continue;
							}
							dam.update(bondInfoVO);
						}
						break;
					case "FUND":
						TBPRD_FUNDINFOVO fundInfoVO = new TBPRD_FUNDINFOVO();
						fundInfoVO = (TBPRD_FUNDINFOVO) dam.findByPKey(TBPRD_FUNDINFOVO.TABLE_UID, str[0].trim());
						if(fundInfoVO == null) {
							error.add(str[0]);
							continue;
						} else {
							// INV_SDATE
							try {
								fundInfoVO.setIPO_SDATE(new Timestamp(sdf.parse(str[1]).getTime()));
							} catch (Exception e) {
								error2.add(str[0]+":"+str[1]);
								continue;
							}
							// INV_EDATE
							try {
								fundInfoVO.setIPO_EDATE(new Timestamp(sdf.parse(str[2]).getTime()));
							} catch (Exception e) {
								error2.add(str[0]+":"+str[2]);
								continue;
							}
							dam.update(fundInfoVO);
						}
						break;
					case "INS":
						TBPRD_INSINFOVO insInfoVO = new TBPRD_INSINFOVO();
						insInfoVO = (TBPRD_INSINFOVO) dam.findByPKey(TBPRD_INSINFOVO.TABLE_UID, str[0].trim());
						if(insInfoVO == null) {
							error.add(str[0]);
							continue;
						} else {
							// INV_SDATE
							try {
								insInfoVO.setIPO_SDATE(new Timestamp(sdf.parse(str[1]).getTime()));
							} catch (Exception e) {
								error2.add(str[0]+":"+str[1]);
								continue;
							}
							// INV_EDATE
							try {
								insInfoVO.setIPO_EDATE(new Timestamp(sdf.parse(str[2]).getTime()));
							} catch (Exception e) {
								error2.add(str[0]+":"+str[2]);
								continue;
							}
							dam.update(insInfoVO);
						}
						break;
				}
			}
		}
		return_VO.setErrorList(error);
		return_VO.setErrorList2(error2);
		return_VO.setErrorList3(error3);
		return_VO.setErrorList4(error4);
		this.sendRtnObject(return_VO);
	}
	
	public void uploadREST(Object body, IPrimitiveMap header) throws JBranchException {
		PRD280InputVO inputVO = (PRD280InputVO) body;
		PRD230OutputVO return_VO = new PRD230OutputVO();
		dam = this.getDataAccessManager();
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
		List<String> error = new ArrayList<String>();
		List<String> error2 = new ArrayList<String>();
		String tempPath = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
		List<String[]> dataCsv = CSVUtil.getBig5CSVFile(tempPath, inputVO.getFileName());
		if(!dataCsv.isEmpty()) {
			for(int i = 0; i < dataCsv.size(); i++) {
				String[] str = dataCsv.get(i);
				if(i == 0) {
					try {
						if(!"交易所代碼".equals(str[0].substring(0, 5)))
							throw new Exception(str[0]);
						else if(!"商品代碼".equals(str[1].substring(0, 4)))
							throw new Exception(str[1]);
						else if(!"休市日".equals(str[2].substring(0, 3)))
							throw new Exception(str[2]);
						else if(!"備註".equals(str[3].trim()))
							throw new Exception(str[3]);
					} catch(Exception ex) {
						throw new APException(ex.getMessage() + ":上傳格式錯誤，請下載範例檔案");
					}
					continue;
				}
				// check
				if(StringUtils.equals("ETF", inputVO.getPrdType()) || StringUtils.equals("STOCK", inputVO.getPrdType())) {
					if(StringUtils.isBlank(str[0])) {
						error.add(str[0]);
						continue;
					}
				}
				if(StringUtils.isBlank(str[1]))
					continue;
				if(StringUtils.isBlank(str[2])) {
					error.add(str[2]);
					continue;
				}
				// old code
				TBPRD_REST_DAYPK pk = new TBPRD_REST_DAYPK();
				pk.setPTYPE(inputVO.getPrdType());
				pk.setSTOCK_CODE((StringUtils.equals("ETF", inputVO.getPrdType()) || StringUtils.equals("STOCK", inputVO.getPrdType()) ? str[0].trim() : "0"));
				pk.setPRD_ID(str[1].trim());
				try {
					pk.setREST_DAY(new Timestamp(sdf.parse(str[2]).getTime()));
				} catch (Exception e) {
					error.add(str[2]);
					continue;
				}
				TBPRD_REST_DAYVO vo = new TBPRD_REST_DAYVO();
				vo.setcomp_id(pk);
				vo = (TBPRD_REST_DAYVO) dam.findByPKey(TBPRD_REST_DAYVO.TABLE_UID, vo.getcomp_id());
				if (vo != null) {
					vo.setMEMO(str[3]);
					dam.update(vo);
				} else {
					vo = new TBPRD_REST_DAYVO();
					vo.setcomp_id(pk);
					vo.setMEMO(str[3]);
					dam.create(vo);
				}
			}
		}
		return_VO.setErrorList(error);
		return_VO.setErrorList2(error2);
		this.sendRtnObject(return_VO);
	}
	
	public void uploadDIVIDEND(Object body, IPrimitiveMap header) throws JBranchException {
		PRD280InputVO inputVO = (PRD280InputVO) body;
		PRD280OutputVO return_VO = new PRD280OutputVO();
		dam = this.getDataAccessManager();
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
		List<String> error = new ArrayList<String>();
		List<String> error2 = new ArrayList<String>();
		List<String> error3 = new ArrayList<String>();
		String tempPath = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
		List<String[]> dataCsv = CSVUtil.getBig5CSVFile(tempPath, inputVO.getFileName());
		if(!dataCsv.isEmpty()) {
			for(int i = 0; i < dataCsv.size(); i++) {
				String[] str = dataCsv.get(i);
				if(i == 0) {
					try {
						if(!"商品代碼".equals(str[0].substring(0, 4)))
							throw new Exception(str[0]);
						else if(!"配息率".equals(str[1].substring(0, 3)))
							throw new Exception(str[1]);
						else if(!"配息日".equals(str[2].substring(0, 3)))
							throw new Exception(str[2]);
					} catch(Exception ex) {
						throw new APException(ex.getMessage() + ":上傳格式錯誤，請下載範例檔案");
					}
					continue;
				}
				// check prd_id 請PG判斷，第一欄若為空，就跳過去不判斷。
				if(StringUtils.isBlank(str[0]))
					continue;
				// 2017/8/8 russle si, sn ,BOND insert to TBPRD_CALENDAR
				switch (inputVO.getPrdType()) {
					case "SI":
						// check TBPRD_SIINFO
						TBPRD_SIINFOVO siInfoVO = new TBPRD_SIINFOVO();
						siInfoVO = (TBPRD_SIINFOVO) dam.findByPKey(TBPRD_SIINFOVO.TABLE_UID, str[0].trim());
						if(siInfoVO == null) {
							error.add(str[0]);
							continue;
						}
						//
						TBPRD_CALENDARVO si_calenderVO = new TBPRD_CALENDARVO();
						TBPRD_CALENDARPK si_calenderPK = new TBPRD_CALENDARPK();
						si_calenderPK.setPRD_TYPE(inputVO.getPrdType());
						si_calenderPK.setCAL_TYPE("1");
						si_calenderPK.setPRD_ID(str[0].trim());
						try {
							si_calenderPK.setCUS_DATE(new Timestamp(sdf.parse(str[2]).getTime()));
						} catch (Exception e) {
							error2.add(str[0]+":"+str[2]);
							continue;
						}
						si_calenderVO = (TBPRD_CALENDARVO) dam.findByPKey(TBPRD_CALENDARVO.TABLE_UID, si_calenderPK);
						if(si_calenderVO == null) {
							TBPRD_CALENDARVO si_calenderVO2 = new TBPRD_CALENDARVO();
							si_calenderVO2.setcomp_id(si_calenderPK);
							try {
								BigDecimal str1 = new BigDecimal(str[1]);
								// NUMBER(6,2)
								if(getNumOfBigDecimal(str1) > 4)
									throw new Exception("");
								si_calenderVO2.setDIVIDEND_RATE(str1);
							} catch (Exception e) {
								error2.add(str[0]+":"+str[1]);
								continue;
							}
							dam.create(si_calenderVO2);
						} else {
							try {
								BigDecimal str1 = new BigDecimal(str[1]);
								// NUMBER(6,2)
								if(getNumOfBigDecimal(str1) > 4)
									throw new Exception("");
								si_calenderVO.setDIVIDEND_RATE(str1);
							} catch (Exception e) {
								error2.add(str[0]+":"+str[1]);
								continue;
							}
							dam.update(si_calenderVO);
						}
						break;
					case "SN":
						// check TBPRD_SIINFO
						TBPRD_SNINFOVO snInfoVO = new TBPRD_SNINFOVO();
						snInfoVO = (TBPRD_SNINFOVO) dam.findByPKey(TBPRD_SNINFOVO.TABLE_UID, str[0].trim());
						if(snInfoVO == null) {
							error.add(str[0]);
							continue;
						}
						//
						TBPRD_CALENDARVO sn_calenderVO = new TBPRD_CALENDARVO();
						TBPRD_CALENDARPK sn_calenderPK = new TBPRD_CALENDARPK();
						sn_calenderPK.setPRD_TYPE(inputVO.getPrdType());
						sn_calenderPK.setCAL_TYPE("1");
						sn_calenderPK.setPRD_ID(str[0].trim());
						try {
							sn_calenderPK.setCUS_DATE(new Timestamp(sdf.parse(str[2]).getTime()));
						} catch (Exception e) {
							error2.add(str[0]+":"+str[2]);
							continue;
						}
						sn_calenderVO = (TBPRD_CALENDARVO) dam.findByPKey(TBPRD_CALENDARVO.TABLE_UID, sn_calenderPK);
						if(sn_calenderVO == null) {
							TBPRD_CALENDARVO sn_calenderVO2 = new TBPRD_CALENDARVO();
							sn_calenderVO2.setcomp_id(sn_calenderPK);
							try {
								BigDecimal str1 = new BigDecimal(str[1]);
								// NUMBER(6,2)
								if(getNumOfBigDecimal(str1) > 4)
									throw new Exception("");
								sn_calenderVO2.setDIVIDEND_RATE(str1);
							} catch (Exception e) {
								error2.add(str[0]+":"+str[1]);
								continue;
							}
							dam.create(sn_calenderVO2);
						} else {
							try {
								BigDecimal str1 = new BigDecimal(str[1]);
								// NUMBER(6,2)
								if(getNumOfBigDecimal(str1) > 4)
									throw new Exception("");
								sn_calenderVO.setDIVIDEND_RATE(str1);
							} catch (Exception e) {
								error2.add(str[0]+":"+str[1]);
								continue;
							}
							dam.update(sn_calenderVO);
						}
						break;
					case "BOND":
						// check TBPRD_SIINFO
						TBPRD_BONDINFOVO bondInfoVO = new TBPRD_BONDINFOVO();
						bondInfoVO = (TBPRD_BONDINFOVO) dam.findByPKey(TBPRD_BONDINFOVO.TABLE_UID, str[0].trim());
						if(bondInfoVO == null) {
							error.add(str[0]);
							continue;
						}
						//
						TBPRD_CALENDARVO bond_calenderVO = new TBPRD_CALENDARVO();
						TBPRD_CALENDARPK bond_calenderPK = new TBPRD_CALENDARPK();
						bond_calenderPK.setPRD_TYPE(inputVO.getPrdType());
						bond_calenderPK.setCAL_TYPE("1");
						bond_calenderPK.setPRD_ID(str[0].trim());
						try {
							bond_calenderPK.setCUS_DATE(new Timestamp(sdf.parse(str[2]).getTime()));
						} catch (Exception e) {
							error2.add(str[0]+":"+str[2]);
							continue;
						}
						bond_calenderVO = (TBPRD_CALENDARVO) dam.findByPKey(TBPRD_CALENDARVO.TABLE_UID, bond_calenderPK);
						if(bond_calenderVO == null) {
							TBPRD_CALENDARVO bond_calenderVO2 = new TBPRD_CALENDARVO();
							bond_calenderVO2.setcomp_id(bond_calenderPK);
							try {
								BigDecimal str1 = new BigDecimal(str[1]);
								// NUMBER(6,2)
								if(getNumOfBigDecimal(str1) > 4)
									throw new Exception("");
								bond_calenderVO2.setDIVIDEND_RATE(str1);
							} catch (Exception e) {
								error2.add(str[0]+":"+str[1]);
								continue;
							}
							dam.create(bond_calenderVO2);
						} else {
							try {
								BigDecimal str1 = new BigDecimal(str[1]);
								// NUMBER(6,2)
								if(getNumOfBigDecimal(str1) > 4)
									throw new Exception("");
								bond_calenderVO.setDIVIDEND_RATE(str1);
							} catch (Exception e) {
								error2.add(str[0]+":"+str[1]);
								continue;
							}
							dam.update(bond_calenderVO);
						}
						break;
					case "INS":
						break;
					case "FUND":
						break;
					case "ETF":
						break;
					case "STOCK":
						break;
				}
			}
		}
		return_VO.setErrorList(error);
		return_VO.setErrorList2(error2);
		return_VO.setErrorList3(error3);
		this.sendRtnObject(return_VO);
	}
	
	public void uploadMATURITY(Object body, IPrimitiveMap header) throws JBranchException {
		PRD280InputVO inputVO = (PRD280InputVO) body;
		PRD230OutputVO return_VO = new PRD230OutputVO();
		dam = this.getDataAccessManager();

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
		List<String> error = new ArrayList<String>();
		List<String> error2 = new ArrayList<String>();
		List<String> error3 = new ArrayList<String>();
		List<String> error4 = new ArrayList<String>();
		Set<String> idList = new HashSet<String>();
		String tempPath = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
		List<String[]> dataCsv = CSVUtil.getBig5CSVFile(tempPath, inputVO.getFileName());
		if(!dataCsv.isEmpty()) {
			for(int i = 0; i < dataCsv.size(); i++) {
				String[] str = dataCsv.get(i);
				if(i == 0) {
					try {
						if(!"商品代碼".equals(str[0].substring(0, 4)))
							throw new Exception(str[0]);
						else if(!"到期日".equals(str[1].substring(0, 3)))
							throw new Exception(str[1]);
					} catch(Exception ex) {
						throw new APException(ex.getMessage() + ":上傳格式錯誤，請下載範例檔案");
					}
					continue;
				}
				// check prd_id 請PG判斷，第一欄若為空，就跳過去不判斷。
				if(StringUtils.isBlank(str[0]))
					continue;
				// 使用者很愛重覆上傳
				if(idList.contains(str[0].trim())) {
					error3.add(str[0]);
					continue;
				}
				idList.add(str[0].trim());
				// 2017/8/8 russle si, sn ,BOND insert to TBPRD_CALENDAR
				switch (inputVO.getPrdType()) {
					case "SI":
						// check TBPRD_SI
						TBPRD_SIVO siVO = new TBPRD_SIVO();
						siVO = (TBPRD_SIVO) dam.findByPKey(TBPRD_SIVO.TABLE_UID, str[0].trim());
						if(siVO == null) {
							error.add(str[0]);
							continue;
						}
						// 他的PK不能用,我認為到期日只有一筆啦
						// 直接去重複
						QueryConditionIF si_queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
						si_queryCondition.setQueryString("delete from TBPRD_CALENDAR where PRD_TYPE = :prd_type and CAL_TYPE = :cal_type and PRD_ID = :prd_id");
						si_queryCondition.setObject("prd_type", inputVO.getPrdType());
						si_queryCondition.setObject("cal_type", "2");
						si_queryCondition.setObject("prd_id", str[0].trim());
						dam.exeUpdate(si_queryCondition);
						//
						TBPRD_CALENDARVO si_calenderVO = new TBPRD_CALENDARVO();
						TBPRD_CALENDARPK si_calenderPK = new TBPRD_CALENDARPK();
						si_calenderPK.setPRD_TYPE(inputVO.getPrdType());
						si_calenderPK.setCAL_TYPE("2");
						si_calenderPK.setPRD_ID(str[0].trim());
						try {
							si_calenderPK.setCUS_DATE(new Timestamp(sdf.parse(str[1]).getTime()));
						} catch (Exception e) {
							error2.add(str[0]+":"+str[1]);
							continue;
						}
						si_calenderVO.setcomp_id(si_calenderPK);
						dam.create(si_calenderVO);
						break;
					case "SN":
						// check TBPRD_SN
						TBPRD_SNVO snVO = new TBPRD_SNVO();
						snVO = (TBPRD_SNVO) dam.findByPKey(TBPRD_SNVO.TABLE_UID, str[0].trim());
						if(snVO == null) {
							error.add(str[0]);
							continue;
						}
						// 他的PK不能用,我認為到期日只有一筆啦
						// 直接去重複
						QueryConditionIF sn_queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
						sn_queryCondition.setQueryString("delete from TBPRD_CALENDAR where PRD_TYPE = :prd_type and CAL_TYPE = :cal_type and PRD_ID = :prd_id");
						sn_queryCondition.setObject("prd_type", inputVO.getPrdType());
						sn_queryCondition.setObject("cal_type", "2");
						sn_queryCondition.setObject("prd_id", str[0].trim());
						dam.exeUpdate(sn_queryCondition);
						//
						TBPRD_CALENDARVO sn_calenderVO = new TBPRD_CALENDARVO();
						TBPRD_CALENDARPK sn_calenderPK = new TBPRD_CALENDARPK();
						sn_calenderPK.setPRD_TYPE(inputVO.getPrdType());
						sn_calenderPK.setCAL_TYPE("2");
						sn_calenderPK.setPRD_ID(str[0].trim());
						try {
							sn_calenderPK.setCUS_DATE(new Timestamp(sdf.parse(str[1]).getTime()));
						} catch (Exception e) {
							error2.add(str[0]+":"+str[1]);
							continue;
						}
						sn_calenderVO.setcomp_id(sn_calenderPK);
						dam.create(sn_calenderVO);
						break;
					case "BOND":
						// check TBPRD_BOND
						TBPRD_BONDVO bondVO = new TBPRD_BONDVO();
						bondVO = (TBPRD_BONDVO) dam.findByPKey(TBPRD_BONDVO.TABLE_UID, str[0].trim());
						if(bondVO == null) {
							error.add(str[0]);
							continue;
						}
						// 他的PK不能用,我認為到期日只有一筆啦
						// 直接去重複
						QueryConditionIF bond_queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
						bond_queryCondition.setQueryString("delete from TBPRD_CALENDAR where PRD_TYPE = :prd_type and CAL_TYPE = :cal_type and PRD_ID = :prd_id");
						bond_queryCondition.setObject("prd_type", inputVO.getPrdType());
						bond_queryCondition.setObject("cal_type", "2");
						bond_queryCondition.setObject("prd_id", str[0].trim());
						dam.exeUpdate(bond_queryCondition);
						//
						TBPRD_CALENDARVO bond_calenderVO = new TBPRD_CALENDARVO();
						TBPRD_CALENDARPK bond_calenderPK = new TBPRD_CALENDARPK();
						bond_calenderPK.setPRD_TYPE(inputVO.getPrdType());
						bond_calenderPK.setCAL_TYPE("2");
						bond_calenderPK.setPRD_ID(str[0].trim());
						try {
							bond_calenderPK.setCUS_DATE(new Timestamp(sdf.parse(str[1]).getTime()));
						} catch (Exception e) {
							error2.add(str[0]+":"+str[1]);
							continue;
						}
						bond_calenderVO.setcomp_id(bond_calenderPK);
						dam.create(bond_calenderVO);
						break;
					case "INS":
						break;
					case "FUND":
						break;
					case "ETF":
						break;
					case "STOCK":
						break;
				}
			}
		}
		return_VO.setErrorList(error);
		return_VO.setErrorList2(error2);
		return_VO.setErrorList3(error3);
		return_VO.setErrorList4(error4);
		this.sendRtnObject(return_VO);
	}
	
	public void downloadSimpleTemp(Object body, IPrimitiveMap header) throws Exception {
		notifyClientToDownloadFile("doc//PRD//PRD280_EXAMPLE2.csv", "上傳指定商品代碼範例.csv");
	    this.sendRtnObject(null);
	}
	public void uploadTemp(Object body, IPrimitiveMap header) throws Exception {
		PRD280InputVO inputVO = (PRD280InputVO) body;
		PRD280OutputVO return_VO = new PRD280OutputVO();
		XmlInfo xmlInfo = new XmlInfo();
		dam = this.getDataAccessManager();
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy/MM/dd");
		List<String> error3 = new ArrayList<String>();
		List<String> error4 = new ArrayList<String>();
		List<String> error5 = new ArrayList<String>();
		Set<String> idList = new HashSet<String>();
		String tempPath = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
		List<String[]> dataCsv = CSVUtil.getBig5CSVFile(tempPath, inputVO.getFileName());
		if(!dataCsv.isEmpty()) {
			Map<String, String> conYN = xmlInfo.doGetVariable("COMMON.YES_NO", FormatHelper.FORMAT_3);
			Map<String, String> obuYN = xmlInfo.doGetVariable("OBU.YES_NO", FormatHelper.FORMAT_3);
			for(int i = 0;i < dataCsv.size();i++) {
				String[] str = dataCsv.get(i);
				if(i == 0) {
					try {
						if(!"商品代碼".equals(str[0].trim()))
							throw new Exception(str[0]);
						else if(!"SI名稱".equals(str[1].trim()))
							throw new Exception(str[1]);
						else if(!"計價幣別".equals(str[2].trim()))
							throw new Exception(str[2]);
						else if(!"到期日期".equals(str[3].trim()))
							throw new Exception(str[3]);
						else if(!"商品風險等級".equals(str[4].trim()))
							throw new Exception(str[4]);
						else if(!"限專投申購(Y、空白)".equals(str[5].trim()))
							throw new Exception(str[5]);
						else if(!"限高資產申購(Y、空白)".equals(str[6].trim()))
							throw new Exception(str[6]);
						else if(!"限OBU申購(O、D)".equals(str[7].trim()))
							throw new Exception(str[7]);
						else if(!"募集開始日".equals(str[8].trim()))
							throw new Exception(str[8]);
						else if(!"募集結束日".equals(str[9].trim()))
							throw new Exception(str[9]);
						else if(!"股債類型".equals(str[10].substring(0, 4)))
							throw new Exception(str[10]);
						else if(!"保本率".equals(str[11].trim()))
							throw new Exception(str[11]);
						else if(!"最低申購金額".equals(str[12].trim()))
							throw new Exception(str[12]);
						else if(!"申購單位金額".equals(str[13].trim()))
							throw new Exception(str[13]);
						else if(!"到期年限".equals(str[14].trim()))
							throw new Exception(str[14]);
						else if(!"客製化商品(Y、空白)".equals(str[15].trim())) 
							throw new Exception(str[15]);
					} catch(Exception ex) {
						throw new APException(ex.getMessage() + ":上傳格式錯誤，請下載範例檔案");
					}
					continue;
				}
				// check prd_id 請PG判斷，第一欄若為空，就跳過去不判斷。
				if(StringUtils.isBlank(str[0]))
					continue;
				// 使用者很愛重覆上傳
				if(idList.contains(str[0].trim())) {
					error4.add(str[0]);
					continue;
				}
				// 限專投申購 & 限高資產申購兩個欄位，不可同時為Y
				if(StringUtils.equals(str[5],"Y") && StringUtils.equals(str[6],"Y") ) {
					error5.add(str[0]);
					continue;
				}
				idList.add(str[0].trim());
				// TBPRD_SI
				Boolean exist = false;
				QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				queryCondition.setQueryString("SELECT PRD_ID FROM TBPRD_SI where PRD_ID = :id");
				queryCondition.setObject("id", str[0].trim());
				List<Map<String, Object>> list = dam.exeQuery(queryCondition);
				if (list.size() > 0)
					exist = true;
				
				TBPRD_SIVO vo;
				if(exist)
					vo = (TBPRD_SIVO) dam.findByPKey(TBPRD_SIVO.TABLE_UID, str[0].trim());
				else {
					vo = new TBPRD_SIVO();
					if(utf_8_length(str[0]) > 16) {
						error3.add(str[0]);
						continue;
					}	
					else
						vo.setPRD_ID(str[0].trim());
				}
				//
				if(utf_8_length(str[1]) > 255) {
					error3.add(str[0]+":"+str[1]);
					continue;
				}	
				else
					vo.setSI_CNAME(str[1]);
				//
				vo.setSI_TYPE("SI");
				//
				if(utf_8_length(str[2]) > 3) {
					error3.add(str[0]+":"+str[2]);
					continue;
				}	
				else
					vo.setCURRENCY_STD_ID(str[2]);
				//
				if(StringUtils.isNotBlank(str[3])) {
					try {
						vo.setDATE_OF_MATURITY(new Timestamp(sdf.parse(str[3]).getTime()));
					} catch (Exception e) {
						try {
							vo.setDATE_OF_MATURITY(new Timestamp(sdf2.parse(str[3]).getTime()));
						} catch (Exception e2) {
							error3.add(str[0]+":"+str[3]);
							continue;
						}
					}
				}
				else
					vo.setDATE_OF_MATURITY(null);
				// 商品風險等級
				Map<String, String> riskmap = new HashMap<String, String>();
				riskmap.put("P1", "");riskmap.put("P2", "");riskmap.put("P3", "");riskmap.put("P4", "");
				if(StringUtils.isBlank(str[4]))
					vo.setRISKCATE_ID(str[4]);
				else if(riskmap.containsKey(str[4].trim()))
					vo.setRISKCATE_ID(str[4].trim());
				else {
					error3.add(str[0]+":"+str[4]);
					continue;
				}
				//改為非Y即N
				if(StringUtils.isNotBlank(str[5])) {
					if(StringUtils.equals("Y", str[5].trim())) {
						vo.setPI_BUY(str[5]);
					} else {
						vo.setPI_BUY("N");
					}			
				} else
					vo.setPI_BUY("N");
				//改為非Y即N
				if(StringUtils.isNotBlank(str[6])) {
					if(StringUtils.equals("Y", str[6].trim())) {
						vo.setHNWC_BUY(str[6]);
					} else {
						vo.setHNWC_BUY("N");
					}			
				} else
					vo.setHNWC_BUY("N");
				
				//SI 限OBU申購(O、D)邏輯調整
				if(StringUtils.isNotBlank(str[7])) {
					if(StringUtils.isBlank(obuYN.get(str[7].trim()))) {
						throw new JBranchException("限OBU申購(O、D)輸入格式有誤，請輸入O(是)、D(否)。");
					}else {
						vo.setOBU_BUY(str[7].toUpperCase());
					}
				}else {
					throw new JBranchException("限OBU申購(O、D)為必填欄位。");
				}
				
				//
				// 保本率
				if(StringUtils.isNotBlank(str[11])) {
					try {
						BigDecimal str11 = new BigDecimal(str[11]);
						// NUMBER(12, 2)
						if(getNumOfBigDecimal(str11) > 10)
							throw new Exception("");
						vo.setRATE_GUARANTEEPAY(str11);
					} catch (Exception e) {
						error3.add(str[0]+":"+str[11]);
						continue;
					}
				} else {
					vo.setRATE_GUARANTEEPAY(null);
				}
				
				// 到期年限
				if(StringUtils.isNotBlank(str[14])) {
					try {
						BigDecimal str14 = new BigDecimal(str[14]);
						// NUMBER(5, 2)
						if(getNumOfBigDecimal(str14) > 3)
							throw new Exception("");
						vo.setYEAR_OF_MATURITY(str14);
					} catch (Exception e) {
						error3.add(str[0]+":"+str[14]);
						continue;
					}
				} else {
					vo.setYEAR_OF_MATURITY(null);
				}
				
				// 客製化商品 改為非Y即N
				if(StringUtils.isNotBlank(str[15])) {
					if(StringUtils.equals("Y", str[15].trim())) {
						vo.setRECORD_FLAG(str[15]);
					} else {
						vo.setRECORD_FLAG("N");
					}			
				} else
					vo.setRECORD_FLAG("N");
				
				if(StringUtils.isBlank(vo.getCUSTOMER_LEVEL())) {
					if (null != vo.getYEAR_OF_MATURITY() && vo.getYEAR_OF_MATURITY().floatValue() <= 1.5
							&& "P1".equals(vo.getRISKCATE_ID())) {
						vo.setCUSTOMER_LEVEL("A1;A2;A3");
					} else if (null != vo.getYEAR_OF_MATURITY() && vo.getYEAR_OF_MATURITY().floatValue() >= 3 && vo.getYEAR_OF_MATURITY().floatValue() <= 10
							&& ("P2".equals(vo.getRISKCATE_ID()) || "P3".equals(vo.getRISKCATE_ID()))) {
						vo.setCUSTOMER_LEVEL("A2;A3");
					} else if ("Y".equals(vo.getPI_BUY())) {
						vo.setCUSTOMER_LEVEL("A2");
					}
				}
				//
				if(exist)
					dam.update(vo);
				else
					dam.create(vo);
				
				// TBPRD_SIINFO
				Boolean exist2 = false;
				queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				queryCondition.setQueryString("SELECT PRD_ID FROM TBPRD_SIINFO where PRD_ID = :id");
				queryCondition.setObject("id", str[0].trim());
				List<Map<String, Object>> list2 = dam.exeQuery(queryCondition);
				if (list2.size() > 0)
					exist2 = true;
				
				TBPRD_SIINFOVO vo2;
				if(exist2)
					vo2 = (TBPRD_SIINFOVO) dam.findByPKey(TBPRD_SIINFOVO.TABLE_UID, str[0].trim());
				else {
					vo2 = new TBPRD_SIINFOVO();
					vo2.setPRD_ID(str[0].trim());
				}
				//
				if(StringUtils.isNotBlank(str[8])) {
					try {
						vo2.setINV_SDATE(new Timestamp(sdf.parse(str[8]).getTime()));
					} catch (Exception e) {
						try {
							vo2.setINV_SDATE(new Timestamp(sdf2.parse(str[8]).getTime()));
						} catch (Exception e2) {
							error3.add(str[0]+":"+str[8]);
							continue;
						}
					}
				}
				else
					vo2.setINV_SDATE(null);
				//
				if(StringUtils.isNotBlank(str[9])) {
					try {
						vo2.setINV_EDATE(new Timestamp(sdf.parse(str[9]).getTime()));
					} catch (Exception e) {
						try {
							vo2.setINV_EDATE(new Timestamp(sdf2.parse(str[9]).getTime()));
						} catch (Exception e2) {
							error3.add(str[0]+":"+str[9]);
							continue;
						}
					}
				}
				else
					vo2.setINV_EDATE(null);
				//
				// 最低申購金額
				if(StringUtils.isNotBlank(str[12])) {
					try {
						BigDecimal str12 = new BigDecimal(str[12]);
						// NUMBER(13, 2)
						if(getNumOfBigDecimal(str12) > 11)
							throw new Exception("");
						vo2.setBASE_AMT_OF_PURCHASE(str12);
					} catch (Exception e) {
						error3.add(str[0]+":"+str[12]);
						continue;
					}
				}
				else
					vo2.setBASE_AMT_OF_PURCHASE(null);

				// 申購單位金額
				if(StringUtils.isNotBlank(str[13])) {
					try {
						BigDecimal str13 = new BigDecimal(str[13]);
						// NUMBER(13, 2)
						if(getNumOfBigDecimal(str13) > 11)
							throw new Exception("");
						vo2.setUNIT_AMT_OF_PURCHASE(str13);
					} catch (Exception e) {
						error3.add(str[0]+":"+str[13]);
						continue;
					}
				}
				else
					vo2.setUNIT_AMT_OF_PURCHASE(null);

				//
				if(exist)
					dam.update(vo2);
				else
					dam.create(vo2);
				
				if(StringUtils.isNotBlank(str[10])) {
					String stock_bond_type = str[10].trim().toUpperCase();
					if("S".equals(stock_bond_type) || "B".equals(stock_bond_type)) {
						addStockBondType(str[0].trim(), stock_bond_type);
					} else {
						throw new JBranchException("股債類型輸入格式有誤，請輸入S或B。");
					}
				} else {
					throw new JBranchException("股債類型為必填欄位。");
				}
			}
		}
		return_VO.setErrorList3(error3);
		return_VO.setErrorList4(error4);
		return_VO.setErrorList5(error5);
		this.sendRtnObject(return_VO);
	}
	
	
	
	
	
	public void downloadSample_inv(Object body, IPrimitiveMap header) throws Exception {
		notifyClientToDownloadFile("doc//PRD//PRD_INV_SAMPLE.csv", "募集上傳範例.csv");
		this.sendRtnObject(null);
	}
	
	public void downloadSample_rest(Object body, IPrimitiveMap header) throws Exception {
		notifyClientToDownloadFile("doc//PRD//PRD_REST_SAMPLE.csv", "休市上傳範例.csv");
		this.sendRtnObject(null);
	}

	public void downloadSample_dividend(Object body, IPrimitiveMap header) throws Exception {
		notifyClientToDownloadFile("doc//PRD//PRD_DIVIDEND_SAMPLE.csv", "配息上傳範例.csv");
		this.sendRtnObject(null);
	}
	
	public void downloadSample_maturity(Object body, IPrimitiveMap header) throws Exception {
		notifyClientToDownloadFile("doc//PRD//PRD_MATURITY_SAMPLE.csv", "到期上傳範例.csv");
		this.sendRtnObject(null);
	}
	
	/*
	 * 2023.01.30 #1404 上傳客群參數 使用delete insert 複製PRD230同名功能修改
	 *
	 */
	public void uploadCustomerLevel(Object body, IPrimitiveMap header) throws Exception {
		PRD280InputVO inputVO = (PRD280InputVO) body;
		PRD280OutputVO return_VO = new PRD280OutputVO();

		dam = this.getDataAccessManager();
		// 先清空
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("delete TBSYSPARAMETER where PARAM_TYPE = :PARAM_TYPE ");
		queryCondition.setObject("PARAM_TYPE", "PRD.SI_CUSTOMER_LEVEL");
		queryCondition.setQueryString(sql.toString());
		dam.exeUpdate(queryCondition);

		List<String> error = new ArrayList<String>();
		List<String> error2 = new ArrayList<String>();
		List<String> error3 = new ArrayList<String>();
		List<String> error4 = new ArrayList<String>();
		List<String> error5 = new ArrayList<String>();
		Set<String> idList = new HashSet<String>();
		String tempPath = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
		List<String[]> dataCsv = CSVUtil.getBig5CSVFile(tempPath, inputVO.getFileName());
		if (!dataCsv.isEmpty()) {
			// Map<String, String> conYN =
			// xmlInfo.doGetVariable("COMMON.YES_NO", FormatHelper.FORMAT_3);
			for (int i = 0; i < dataCsv.size(); i++) {
				String[] str = dataCsv.get(i);
				if (i == 0) {
					try {
						if (!"客群代碼".equals(str[0].trim()))
							throw new Exception(str[0]);
						else if (!"客群名稱".equals(str[1].trim()))
							throw new Exception(str[1]);
					} catch (Exception ex) {
						throw new APException(ex.getMessage() + ":上傳格式錯誤，請下載範例檔案");
					}
					continue;
				}
				// check prd_id 請PG判斷，第一欄若為空，就跳過去不判斷。
				if (StringUtils.isBlank(str[0]))
					continue;
				// 使用者很愛重覆上傳
				if (idList.contains(str[0].trim())) {
					error4.add(str[0]);
					continue;
				}


//				queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
//				sql = new StringBuffer();
//				sql.append("select PARAM_CODE, PARAM_NAME from TBSYSPARAMETER where PARAM_TYPE = :PARAM_TYPE and PARAM_CODE = :PARAM_CODE ");
//				queryCondition.setObject("PARAM_TYPE", "PRD.FUND_CUSTOMER_LEVEL");
//				queryCondition.setObject("PARAM_CODE", str[0].trim());
//				queryCondition.setQueryString(sql.toString());
//				List<Map<String, Object>> list = dam.exeQuery(queryCondition);
				TBSYSPARAMETERPK parameterPK = new TBSYSPARAMETERPK();
				parameterPK.setPARAM_TYPE("PRD.SI_CUSTOMER_LEVEL");
				parameterPK.setPARAM_CODE(str[0].trim());
				TBSYSPARAMETERVO parameterVO = new TBSYSPARAMETERVO();
				parameterVO.setcomp_id(parameterPK);
				parameterVO.setPARAM_NAME(str[1].trim());
				parameterVO.setPARAM_NAME_EDIT(str[1].trim());

				sql = new StringBuffer();
				sql.append("select max(PARAM_ORDER) as COUNT from TBSYSPARAMETER where PARAM_TYPE = :PARAM_TYPE");
				queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				queryCondition.setObject("PARAM_TYPE", "PRD.SI_CUSTOMER_LEVEL");
				queryCondition.setQueryString(sql.toString());
				List<Map<String, Object>> list2 = dam.exeQuery(queryCondition);
				if (null == list2.get(0).get("COUNT")) {
					parameterVO.setPARAM_ORDER(0);
				} else {
					parameterVO.setPARAM_ORDER(Integer.parseInt(list2.get(0).get("COUNT").toString()) + 1);
				}

				parameterVO.setVersion(new Long(0));
				parameterVO.setPARAM_STATUS("0");

				dam.create(parameterVO);
			}
		}
		return_VO.setErrorList(error);
		return_VO.setErrorList2(error2);
		return_VO.setErrorList3(error3);
		return_VO.setErrorList4(error4);
		return_VO.setErrorList5(error5);
		this.sendRtnObject(return_VO);
	}
	
	/*
	 * #1404 上傳專案參數 使用delete insert 複製PRD230同名功能修改
	 */
	public void uploadProject(Object body, IPrimitiveMap header) throws Exception {
		PRD280InputVO inputVO = (PRD280InputVO) body;
		PRD280OutputVO return_VO = new PRD280OutputVO();

		dam = this.getDataAccessManager();
		// 先清空
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("delete TBSYSPARAMETER where PARAM_TYPE = :PARAM_TYPE ");
		queryCondition.setObject("PARAM_TYPE", "PRD.SI_PROJECT");
		queryCondition.setQueryString(sql.toString());
		dam.exeUpdate(queryCondition);

		List<String> error = new ArrayList<String>();
		List<String> error2 = new ArrayList<String>();
		List<String> error3 = new ArrayList<String>();
		List<String> error4 = new ArrayList<String>();
		List<String> error5 = new ArrayList<String>();
		Set<String> idList = new HashSet<String>();
		String tempPath = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
		List<String[]> dataCsv = CSVUtil.getBig5CSVFile(tempPath, inputVO.getFileName());
		if (!dataCsv.isEmpty()) {
			// Map<String, String> conYN =
			// xmlInfo.doGetVariable("COMMON.YES_NO", FormatHelper.FORMAT_3);
			for (int i = 0; i < dataCsv.size(); i++) {
				String[] str = dataCsv.get(i);
				if (i == 0) {
					try {
						if (!"專案代碼".equals(str[0].trim()))
							throw new Exception(str[0]);
						else if (!"專案名稱".equals(str[1].trim()))
							throw new Exception(str[1]);
					} catch (Exception ex) {
						throw new APException(ex.getMessage() + ":上傳格式錯誤，請下載範例檔案");
					}
					continue;
				}
				// check prd_id 請PG判斷，第一欄若為空，就跳過去不判斷。
				if (StringUtils.isBlank(str[0]))
					continue;
				// 使用者很愛重覆上傳
				if (idList.contains(str[0].trim())) {
					error4.add(str[0]);
					continue;
				}



				TBSYSPARAMETERPK parameterPK = new TBSYSPARAMETERPK();
				parameterPK.setPARAM_TYPE("PRD.SI_PROJECT");
				parameterPK.setPARAM_CODE(str[0].trim());
				TBSYSPARAMETERVO parameterVO = new TBSYSPARAMETERVO();
				parameterVO.setcomp_id(parameterPK);
				parameterVO.setPARAM_NAME(str[1].trim());
				parameterVO.setPARAM_NAME_EDIT(str[1].trim());

				sql = new StringBuffer();
				sql.append("select max(PARAM_ORDER) as COUNT from TBSYSPARAMETER where PARAM_TYPE = :PARAM_TYPE");
				queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				queryCondition.setObject("PARAM_TYPE", "PRD.SI_PROJECT");
				queryCondition.setQueryString(sql.toString());
				List<Map<String, Object>> list2 = dam.exeQuery(queryCondition);
				if (null == list2.get(0).get("COUNT")) {
					parameterVO.setPARAM_ORDER(0);
				} else {
					parameterVO.setPARAM_ORDER(Integer.parseInt(list2.get(0).get("COUNT").toString()) + 1);
				}

				parameterVO.setVersion(new Long(0));
				parameterVO.setPARAM_STATUS("0");

				dam.create(parameterVO);
			}
		}
		return_VO.setErrorList(error);
		return_VO.setErrorList2(error2);
		return_VO.setErrorList3(error3);
		return_VO.setErrorList4(error4);
		return_VO.setErrorList5(error5);
		this.sendRtnObject(return_VO);
	}
	
	/*
	 * TBPRD_SI 標籤欄位處理
	 * #1404 專案、客群 2023.02.21
	 */
	public void updateTagsIndependent(String[] str, ArrayList<String> error6) throws DAOException {
		dam = this.getDataAccessManager();
		TBPRD_SIVO siVO = (TBPRD_SIVO) dam.findByPKey(TBPRD_SIVO.TABLE_UID, str[0].trim());
		// 專案代碼
		if (StringUtils.isNotBlank(str[11])) {
			if (str[11].length() > 20) {
				error6.add(str[0] + ":" + str[11]);
				return;
			}

			if (StringUtils.equals((str[11].trim()), "0")) {
				siVO.setPROJECT(null);
			} else if (StringUtils.equals((str[11].trim()), "$")) {
				
			} else {
				siVO.setPROJECT(str[11].trim());
			}
		}
		// 客群代碼
		if (StringUtils.isNotBlank(str[12])) {
			if (str[8].length() > 20) {
				error6.add(str[0] + ":" + str[12]);
				return;
			}
			if (StringUtils.equals((str[12].trim()), "0")) {
				siVO.setCUSTOMER_LEVEL(null);
			} else if (StringUtils.equals((str[12].trim()), "$")) {
				
			} else {
				siVO.setCUSTOMER_LEVEL(str[12].trim());
			}
		}
		
		if(StringUtils.isBlank(siVO.getCUSTOMER_LEVEL())) {
			if (null != siVO.getYEAR_OF_MATURITY() && siVO.getYEAR_OF_MATURITY().floatValue() <= 1.5
					&& "P1".equals(siVO.getRISKCATE_ID())) {
				siVO.setCUSTOMER_LEVEL("A1;A2;A3");
			} else if (null != siVO.getYEAR_OF_MATURITY() && siVO.getYEAR_OF_MATURITY().floatValue() >= 3 && siVO.getYEAR_OF_MATURITY().floatValue() <= 10
					&& ("P2".equals(siVO.getRISKCATE_ID()) || "P3".equals(siVO.getRISKCATE_ID()))) {
				siVO.setCUSTOMER_LEVEL("A2;A3");
			} else if ("Y".equals(siVO.getPI_BUY())) {
				siVO.setCUSTOMER_LEVEL("A1");
			}
		}
		dam.update(siVO);
	}
	
	/*
	 * #1404
	 */
	public void updateTBPRD_SI_Tags(Object body, IPrimitiveMap header) throws Exception {
		PRD280InputVO inputVO = (PRD280InputVO) body;
		PRD280OutputVO return_VO = new PRD280OutputVO();
		dam = this.getDataAccessManager();

		// check again
		// TBPRD_ETF
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT PRD_ID FROM TBPRD_SI where PRD_ID = :id ");
		queryCondition.setObject("id", inputVO.getPrd_id());
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		if (list.size() == 0)
			throw new APException("ehl_01_common_026");

		TBPRD_SIVO fundVO = (TBPRD_SIVO) dam.findByPKey(TBPRD_SIVO.TABLE_UID, inputVO.getPrd_id());

		// 專案
		if (StringUtils.isNotBlank(inputVO.getSi_project())) {
			fundVO.setPROJECT(inputVO.getSi_project());
		}
		// 客群
		if (StringUtils.isNotBlank(inputVO.getSi_customer_level())) {
			fundVO.setCUSTOMER_LEVEL(inputVO.getSi_customer_level());
		}
		dam.update(fundVO);

		this.sendRtnObject(return_VO);
	}
}