package com.systex.jbranch.app.server.fps.prd270;

import com.systex.jbranch.app.common.fps.table.TBPRD_SNINFOVO;
import com.systex.jbranch.app.common.fps.table.TBPRD_SNINFO_REVIEWVO;
import com.systex.jbranch.app.common.fps.table.TBPRD_SNVO;
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
import com.systex.jbranch.platform.server.info.*;
import com.systex.jbranch.platform.util.IPrimitiveMap;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * prd270
 *
 * @author moron
 * @date 2016/09/21
 * @spec null
 */
@Component("prd270")
@Scope("request")
public class PRD270 extends FubonWmsBizLogic {
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(PRD270.class);

	public void inquire(Object body, IPrimitiveMap header) throws JBranchException {
		WorkStation ws = DataManager.getWorkStation(uuid);
		PRD270InputVO inputVO = (PRD270InputVO) body;
		PRD270OutputVO return_VO = new PRD270OutputVO();
		dam = this.getDataAccessManager();

		// getRoleList
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT COUNT(ROLEID) AS COUNTS FROM TBSYSSECUROLPRIASS ");
		sql.append("WHERE PRIVILEGEID IN (SELECT PRIVILEGEID FROM TBSYSSECUPRIFUNMAP WHERE ITEMID = 'PRD270' AND FUNCTIONID = 'confirm') AND ROLEID = :roleID ");
		queryCondition.setObject("roleID", getUserVariable(FubonSystemVariableConsts.LOGINROLE));
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> privilegeCount = dam.exeQueryWithoutSort(queryCondition);
		//
		sql = new StringBuffer();
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sql.append("SELECT BASE_AMT_OF_PURCHASE, UNIT_AMT_OF_PURCHASE, RATE_GUARANTEEPAY, STOCK_BOND_TYPE, SEQ,PRD_ID,SN_CNAME_A,ISIN_CODE,CREDIT_RATING_SP,CREDIT_RATING_MODDY,CREDIT_RATING_FITCH,AVOUCH_CREDIT_RATING_SP,AVOUCH_CREDIT_RATING_MODDY,AVOUCH_CREDIT_RATING_FITCH,START_DATE_OF_BUYBACK,FIXED_RATE_DURATION,CURRENCY_EXCHANGE,FLOATING_DIVIDEND_RATE,INVESTMENT_TARGETS,CNR_YIELD,RATE_OF_CHANNEL,PERFORMANCE_REVIEW,ACT_TYPE,REVIEW_STATUS,CREATOR,PROJECT,CUSTOMER_LEVEL,PI_BUY,BOND_VALUE FROM ( ");
		// 覆核主管
		if (((BigDecimal) privilegeCount.get(0).get("COUNTS")).compareTo(new BigDecimal(0)) > 0) {
			sql.append("SELECT rw.BASE_AMT_OF_PURCHASE, rw.UNIT_AMT_OF_PURCHASE, rw.RATE_GUARANTEEPAY, rw.STOCK_BOND_TYPE, rw.SEQ,rw.PRD_ID,sn.SN_CNAME_A,rw.ISIN_CODE,rw.CREDIT_RATING_SP,rw.CREDIT_RATING_MODDY,rw.CREDIT_RATING_FITCH,rw.AVOUCH_CREDIT_RATING_SP,rw.AVOUCH_CREDIT_RATING_MODDY,rw.AVOUCH_CREDIT_RATING_FITCH,rw.START_DATE_OF_BUYBACK,rw.FIXED_RATE_DURATION,rw.CURRENCY_EXCHANGE,rw.FLOATING_DIVIDEND_RATE,rw.INVESTMENT_TARGETS,rw.CNR_YIELD,rw.RATE_OF_CHANNEL,rw.PERFORMANCE_REVIEW,rw.ACT_TYPE,rw.REVIEW_STATUS,rw.CREATOR, rw.PROJECT, rw.CUSTOMER_LEVEL,sn.PI_BUY,sn.BOND_VALUE ");
			sql.append("FROM TBPRD_SNINFO_REVIEW rw left join TBPRD_SN sn on rw.PRD_ID = sn.PRD_ID ");
			sql.append("WHERE rw.REVIEW_STATUS = 'W' ");
			sql.append("UNION ");
			sql.append("SELECT info.BASE_AMT_OF_PURCHASE, info.UNIT_AMT_OF_PURCHASE, sn.RATE_GUARANTEEPAY, info.STOCK_BOND_TYPE, null as SEQ,info.PRD_ID,sn.SN_CNAME_A,info.ISIN_CODE,info.CREDIT_RATING_SP,info.CREDIT_RATING_MODDY,info.CREDIT_RATING_FITCH,info.AVOUCH_CREDIT_RATING_SP,info.AVOUCH_CREDIT_RATING_MODDY,info.AVOUCH_CREDIT_RATING_FITCH,info.START_DATE_OF_BUYBACK,info.FIXED_RATE_DURATION,info.CURRENCY_EXCHANGE,info.FLOATING_DIVIDEND_RATE,info.INVESTMENT_TARGETS,info.CNR_YIELD,info.RATE_OF_CHANNEL,info.PERFORMANCE_REVIEW,rw.ACT_TYPE,rw.REVIEW_STATUS,null as CREATOR,sn.PROJECT,sn.CUSTOMER_LEVEL,sn.PI_BUY,sn.BOND_VALUE ");
			sql.append("FROM TBPRD_SNINFO info left join TBPRD_SN sn on info.PRD_ID = sn.PRD_ID ");
			sql.append("left join TBPRD_SNINFO_REVIEW rw on rw.PRD_ID = info.PRD_ID and rw.REVIEW_STATUS = 'W' ");
			sql.append("WHERE NVL(info.REVIEW_STATUS, 'Y') = 'Y' ");
			sql.append("AND info.PRD_ID NOT IN (SELECT PRD_ID FROM TBPRD_SNINFO_REVIEW WHERE REVIEW_STATUS = 'W') ");
		} else {
			sql.append("SELECT rw.BASE_AMT_OF_PURCHASE, rw.UNIT_AMT_OF_PURCHASE, rw.RATE_GUARANTEEPAY, rw.STOCK_BOND_TYPE, rw.SEQ,rw.PRD_ID,sn.SN_CNAME_A,rw.ISIN_CODE,rw.CREDIT_RATING_SP,rw.CREDIT_RATING_MODDY,rw.CREDIT_RATING_FITCH,rw.AVOUCH_CREDIT_RATING_SP,rw.AVOUCH_CREDIT_RATING_MODDY,rw.AVOUCH_CREDIT_RATING_FITCH,rw.START_DATE_OF_BUYBACK,rw.FIXED_RATE_DURATION,rw.CURRENCY_EXCHANGE,rw.FLOATING_DIVIDEND_RATE,rw.INVESTMENT_TARGETS,rw.CNR_YIELD,rw.RATE_OF_CHANNEL,rw.PERFORMANCE_REVIEW,rw.ACT_TYPE,rw.REVIEW_STATUS,null as CREATOR,rw.PROJECT,rw.CUSTOMER_LEVEL,sn.PI_BUY,sn.BOND_VALUE ");
			sql.append("FROM TBPRD_SNINFO_REVIEW rw left join TBPRD_SN sn on rw.PRD_ID = sn.PRD_ID ");
			sql.append("WHERE rw.REVIEW_STATUS = 'W' ");
			sql.append("AND rw.CREATOR = :creator ");
			sql.append("UNION ");
			sql.append("SELECT info.BASE_AMT_OF_PURCHASE, info.UNIT_AMT_OF_PURCHASE, sn.RATE_GUARANTEEPAY, info.STOCK_BOND_TYPE, null as SEQ,info.PRD_ID,sn.SN_CNAME_A,info.ISIN_CODE,info.CREDIT_RATING_SP,info.CREDIT_RATING_MODDY,info.CREDIT_RATING_FITCH,info.AVOUCH_CREDIT_RATING_SP,info.AVOUCH_CREDIT_RATING_MODDY,info.AVOUCH_CREDIT_RATING_FITCH,info.START_DATE_OF_BUYBACK,info.FIXED_RATE_DURATION,info.CURRENCY_EXCHANGE,info.FLOATING_DIVIDEND_RATE,info.INVESTMENT_TARGETS,info.CNR_YIELD,info.RATE_OF_CHANNEL,info.PERFORMANCE_REVIEW,rw.ACT_TYPE,rw.REVIEW_STATUS,null as CREATOR,sn.PROJECT,sn.CUSTOMER_LEVEL,sn.PI_BUY,sn.BOND_VALUE ");
			sql.append("FROM TBPRD_SNINFO info ");
			sql.append("left join TBPRD_SN sn on info.PRD_ID = sn.PRD_ID ");
			sql.append("left join TBPRD_SNINFO_REVIEW rw on rw.PRD_ID = info.PRD_ID and rw.REVIEW_STATUS = 'W' ");
			sql.append("WHERE NVL(info.REVIEW_STATUS, 'Y') = 'Y' ");
			sql.append("AND info.PRD_ID NOT IN (SELECT PRD_ID FROM TBPRD_SNINFO_REVIEW WHERE REVIEW_STATUS = 'W' AND CREATOR = :creator) ");
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
		PRD270InputVO inputVO = (PRD270InputVO) body;
		PRD270OutputVO return_VO = new PRD270OutputVO();
		dam = this.getDataAccessManager();

		// update
		if(StringUtils.equals("Y", inputVO.getStatus())) {
			// TBPRD_SN
			QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sql = new StringBuffer();
			sql.append("SELECT PRD_ID,SN_CNAME_A FROM TBPRD_SN where PRD_ID = :id ");
			queryCondition.setObject("id", inputVO.getPrd_id());
			queryCondition.setQueryString(sql.toString());
			List<Map<String, Object>> list = dam.exeQuery(queryCondition);
			if (list.size() > 0) {
				return_VO.setCname(ObjectUtils.toString(list.get(0).get("SN_CNAME_A")));
				return_VO.setCanEdit(true);
			} else {
				return_VO.setCanEdit(false);
				return_VO.setErrorMsg("ehl_01_common_026");
			}

			// TBPRD_SNINFO_REVIEW
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sql = new StringBuffer();
			sql.append("SELECT PRD_ID FROM TBPRD_SNINFO_REVIEW where PRD_ID = :id and REVIEW_STATUS = 'W' ");
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
			// TBPRD_SN
			QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sql = new StringBuffer();
			sql.append("SELECT PRD_ID, SN_CNAME, RISKCATE_ID, CURRENCY_STD_ID FROM TBPRD_SN where PRD_ID = :id ");
			queryCondition.setObject("id", inputVO.getPrd_id());
			queryCondition.setQueryString(sql.toString());
			List<Map<String, Object>> list = dam.exeQuery(queryCondition);
			if (list.size() > 0) {
				return_VO.setCname(ObjectUtils.toString(list.get(0).get("SN_CNAME")));
				return_VO.setRick_id(ObjectUtils.toString(list.get(0).get("RISKCATE_ID")));
				return_VO.setCurrency(ObjectUtils.toString(list.get(0).get("CURRENCY_STD_ID")));
				return_VO.setCanEdit(true);
			} else
				return_VO.setCanEdit(false);
		}
		// add
		else {
			// TBPRD_SN
			QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sql = new StringBuffer();
			sql.append("SELECT PRD_ID,SN_CNAME_A FROM TBPRD_SN where PRD_ID = :id ");
			queryCondition.setObject("id", inputVO.getPrd_id());
			queryCondition.setQueryString(sql.toString());
			List<Map<String, Object>> list = dam.exeQuery(queryCondition);
			if (list.size() > 0) {
				return_VO.setCname(ObjectUtils.toString(list.get(0).get("SN_CNAME_A")));
				return_VO.setCanEdit(true);
			} else {
				return_VO.setCanEdit(false);
				return_VO.setErrorMsg("ehl_01_common_026");
			}
			// TBPRD_SNINFO
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sql = new StringBuffer();
			sql.append("SELECT PRD_ID FROM TBPRD_SNINFO where PRD_ID = :id ");
			queryCondition.setObject("id", inputVO.getPrd_id());
			queryCondition.setQueryString(sql.toString());
			List<Map<String, Object>> list2 = dam.exeQuery(queryCondition);
			if (list2.size() > 0) {
				return_VO.setCanEdit(false);
				return_VO.setErrorMsg("ehl_01_common_027");
			}
			// TBPRD_SNINFO_REVIEW
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sql = new StringBuffer();
			sql.append("SELECT PRD_ID FROM TBPRD_SNINFO_REVIEW where PRD_ID = :id and REVIEW_STATUS = 'W' ");
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
		PRD270InputVO inputVO = (PRD270InputVO) body;
		dam = this.getDataAccessManager();

		// check again
		// TBPRD_SN
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT PRD_ID FROM TBPRD_SN where PRD_ID = :id ");
		queryCondition.setObject("id", inputVO.getPrd_id());
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		if (list.size() == 0)
			throw new APException("ehl_01_common_026");
		// TBPRD_SNINFO
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sql = new StringBuffer();
		sql.append("SELECT PRD_ID FROM TBPRD_SNINFO where PRD_ID = :id ");
		queryCondition.setObject("id", inputVO.getPrd_id());
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list2 = dam.exeQuery(queryCondition);
		if (list2.size() > 0)
			throw new APException("ehl_01_common_027");
		// TBPRD_SNINFO_REVIEW
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sql = new StringBuffer();
		sql.append("SELECT PRD_ID FROM TBPRD_SNINFO_REVIEW where PRD_ID = :id and REVIEW_STATUS = 'W' ");
		queryCondition.setObject("id", inputVO.getPrd_id());
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list3 = dam.exeQuery(queryCondition);
		if (list3.size() > 0)
			throw new APException("ehl_01_common_028");

		// seq
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sql = new StringBuffer();
		sql.append("SELECT SQ_TBPRD_SNINFO_REVIEW.nextval AS SEQ FROM DUAL ");
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> SEQLIST = dam.exeQuery(queryCondition);
		BigDecimal seqNo = (BigDecimal) SEQLIST.get(0).get("SEQ");
		// add TBPRD_SNINFO_REVIEW
		TBPRD_SNINFO_REVIEWVO vo = new TBPRD_SNINFO_REVIEWVO();
		vo.setSEQ(seqNo);
		vo.setPRD_ID(inputVO.getPrd_id());
		vo.setISIN_CODE(inputVO.getIsin_code());
		vo.setCREDIT_RATING_SP(inputVO.getRating_sp());
		vo.setCREDIT_RATING_MODDY(inputVO.getRating_moddy());
		vo.setCREDIT_RATING_FITCH(inputVO.getRating_fitch());
		vo.setAVOUCH_CREDIT_RATING_SP(inputVO.getSn_rating_sp());
		vo.setAVOUCH_CREDIT_RATING_MODDY(inputVO.getSn_rating_moddy());
		vo.setAVOUCH_CREDIT_RATING_FITCH(inputVO.getSn_rating_fitch());
		if(inputVO.getBuy_Date() != null)
			vo.setSTART_DATE_OF_BUYBACK(new Timestamp(inputVO.getBuy_Date().getTime()));
		vo.setFIXED_RATE_DURATION(inputVO.getFix_Date());
		vo.setCURRENCY_EXCHANGE(inputVO.getExchange());
		if(StringUtils.isNotBlank(inputVO.getDividend()))
			vo.setFLOATING_DIVIDEND_RATE(new BigDecimal(inputVO.getDividend()));
		vo.setINVESTMENT_TARGETS(inputVO.getTarget());
		if(StringUtils.isNotBlank(inputVO.getCnr_yield()))
			vo.setCNR_YIELD(new BigDecimal(inputVO.getCnr_yield()));
		if(StringUtils.isNotBlank(inputVO.getRate_channel()))
			vo.setRATE_OF_CHANNEL(new BigDecimal(inputVO.getRate_channel()));
		vo.setPERFORMANCE_REVIEW(inputVO.getPerformance_review());
		vo.setSTOCK_BOND_TYPE(inputVO.getStock_bond_type());
		vo.setPROJECT(inputVO.getSnProject());
		vo.setCUSTOMER_LEVEL(inputVO.getSnCustLevel());

		vo.setACT_TYPE("A");
		vo.setREVIEW_STATUS("W");
		vo.setBOND_VALUE(inputVO.getBond_value());
		dam.create(vo);
		this.sendRtnObject(null);
	}

	public void editData(Object body, IPrimitiveMap header) throws JBranchException {
		PRD270InputVO inputVO = (PRD270InputVO) body;
		dam = this.getDataAccessManager();

		// check again
		// TBPRD_SNINFO_REVIEW
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT PRD_ID FROM TBPRD_SNINFO_REVIEW where PRD_ID = :id and REVIEW_STATUS = 'W' ");
		queryCondition.setObject("id", inputVO.getPrd_id());
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		if (list.size() > 0)
			throw new APException("ehl_01_common_028");

		// seq
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sql = new StringBuffer();
		sql.append("SELECT SQ_TBPRD_SNINFO_REVIEW.nextval AS SEQ FROM DUAL ");
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> SEQLIST = dam.exeQuery(queryCondition);
		BigDecimal seqNo = (BigDecimal) SEQLIST.get(0).get("SEQ");
		// add TBPRD_SNINFO_REVIEW
		TBPRD_SNINFO_REVIEWVO vo = new TBPRD_SNINFO_REVIEWVO();
		vo.setSEQ(seqNo);
		vo.setPRD_ID(inputVO.getPrd_id());
		vo.setISIN_CODE(inputVO.getIsin_code());
		vo.setCREDIT_RATING_SP(inputVO.getRating_sp());
		vo.setCREDIT_RATING_MODDY(inputVO.getRating_moddy());
		vo.setCREDIT_RATING_FITCH(inputVO.getRating_fitch());
		vo.setAVOUCH_CREDIT_RATING_SP(inputVO.getSn_rating_sp());
		vo.setAVOUCH_CREDIT_RATING_MODDY(inputVO.getSn_rating_moddy());
		vo.setAVOUCH_CREDIT_RATING_FITCH(inputVO.getSn_rating_fitch());
		if(inputVO.getBuy_Date() != null)
			vo.setSTART_DATE_OF_BUYBACK(new Timestamp(inputVO.getBuy_Date().getTime()));
		else
			vo.setSTART_DATE_OF_BUYBACK(null);
		vo.setFIXED_RATE_DURATION(inputVO.getFix_Date());
		vo.setCURRENCY_EXCHANGE(inputVO.getExchange());
		if(StringUtils.isNotBlank(inputVO.getDividend()))
			vo.setFLOATING_DIVIDEND_RATE(new BigDecimal(inputVO.getDividend()));
		else
			vo.setFLOATING_DIVIDEND_RATE(null);
		vo.setINVESTMENT_TARGETS(inputVO.getTarget());
		if(StringUtils.isNotBlank(inputVO.getCnr_yield()))
			vo.setCNR_YIELD(new BigDecimal(inputVO.getCnr_yield()));
		else
			vo.setCNR_YIELD(null);
		if(StringUtils.isNotBlank(inputVO.getRate_channel()))
			vo.setRATE_OF_CHANNEL(new BigDecimal(inputVO.getRate_channel()));
		else
			vo.setRATE_OF_CHANNEL(null);
		vo.setPERFORMANCE_REVIEW(inputVO.getPerformance_review());
		vo.setSTOCK_BOND_TYPE(inputVO.getStock_bond_type());
		vo.setPROJECT(inputVO.getSnProject());
		vo.setCUSTOMER_LEVEL(inputVO.getSnCustLevel());
		vo.setACT_TYPE("M");
		vo.setREVIEW_STATUS("W");
		vo.setBOND_VALUE(inputVO.getBond_value());
		dam.create(vo);

		this.sendRtnObject(null);
	}

	public void deleteData(Object body, IPrimitiveMap header) throws JBranchException {
		PRD270InputVO inputVO = (PRD270InputVO) body;
		dam = this.getDataAccessManager();
		String prd_id = inputVO.getPrd_id();
		// check again
		// TBPRD_SNINFO_REVIEW
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT PRD_ID FROM TBPRD_SNINFO_REVIEW where PRD_ID = :id and REVIEW_STATUS = 'W' ");
		queryCondition.setObject("id", prd_id);
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		if (list.size() > 0)
			throw new APException("ehl_01_common_028");

		// seq
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sql = new StringBuffer();
		sql.append("SELECT SQ_TBPRD_SNINFO_REVIEW.nextval AS SEQ FROM DUAL ");
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> SEQLIST = dam.exeQuery(queryCondition);
		BigDecimal seqNo = (BigDecimal) SEQLIST.get(0).get("SEQ");
		// get ori
		TBPRD_SNINFOVO vo = new TBPRD_SNINFOVO();
		vo = (TBPRD_SNINFOVO) dam.findByPKey(TBPRD_SNINFOVO.TABLE_UID, prd_id);
		if (vo != null) {
			// add TBPRD_SNINFO_REVIEW
			TBPRD_SNINFO_REVIEWVO rvo = new TBPRD_SNINFO_REVIEWVO();
			rvo.setSEQ(seqNo);
			rvo.setPRD_ID(vo.getPRD_ID());
			rvo.setISIN_CODE(vo.getISIN_CODE());
			rvo.setCREDIT_RATING_SP(vo.getCREDIT_RATING_SP());
			rvo.setCREDIT_RATING_MODDY(vo.getCREDIT_RATING_MODDY());
			rvo.setCREDIT_RATING_FITCH(vo.getCREDIT_RATING_FITCH());
			rvo.setAVOUCH_CREDIT_RATING_SP(vo.getAVOUCH_CREDIT_RATING_SP());
			rvo.setAVOUCH_CREDIT_RATING_MODDY(vo.getAVOUCH_CREDIT_RATING_MODDY());
			rvo.setAVOUCH_CREDIT_RATING_FITCH(vo.getAVOUCH_CREDIT_RATING_FITCH());
			rvo.setSTART_DATE_OF_BUYBACK(vo.getSTART_DATE_OF_BUYBACK());
			rvo.setFIXED_RATE_DURATION(vo.getFIXED_RATE_DURATION());
			rvo.setCURRENCY_EXCHANGE(vo.getCURRENCY_EXCHANGE());
			rvo.setFLOATING_DIVIDEND_RATE(vo.getFLOATING_DIVIDEND_RATE());
			rvo.setINVESTMENT_TARGETS(vo.getINVESTMENT_TARGETS());
			rvo.setCNR_YIELD(vo.getCNR_YIELD());
			rvo.setRATE_OF_CHANNEL(vo.getRATE_OF_CHANNEL());
			rvo.setPERFORMANCE_REVIEW(vo.getPERFORMANCE_REVIEW());
			rvo.setSTOCK_BOND_TYPE(vo.getSTOCK_BOND_TYPE());
			rvo.setACT_TYPE("D");
			rvo.setREVIEW_STATUS("W");
			rvo.setBOND_VALUE(inputVO.getBond_value());
			dam.create(rvo);
		} else {
			// 顯示資料不存在
			throw new APException("ehl_01_common_001");
		}
		this.sendRtnObject(null);
	}

	public void review(Object body, IPrimitiveMap header) throws JBranchException {
		PRD270InputVO inputVO = (PRD270InputVO) body;
		dam = this.getDataAccessManager();

		// 2017/2/23
		for(Map<String, Object> rmap : inputVO.getReview_list()) {
			BigDecimal seq = new BigDecimal(ObjectUtils.toString(rmap.get("SEQ")));
			TBPRD_SNINFO_REVIEWVO rvo = (TBPRD_SNINFO_REVIEWVO) dam.findByPKey(TBPRD_SNINFO_REVIEWVO.TABLE_UID, seq);
			if (rvo != null) {
				// confirm
				if("Y".equals(inputVO.getStatus())) {
					// 新增
					if("A".equals(rvo.getACT_TYPE())) {
						// TBPRD_SNINFO
						TBPRD_SNINFOVO vo = new TBPRD_SNINFOVO();
						vo.setPRD_ID(rvo.getPRD_ID());
						vo.setISIN_CODE(rvo.getISIN_CODE());
						vo.setCREDIT_RATING_SP(rvo.getCREDIT_RATING_SP());
						vo.setCREDIT_RATING_MODDY(rvo.getCREDIT_RATING_MODDY());
						vo.setCREDIT_RATING_FITCH(rvo.getCREDIT_RATING_FITCH());
						vo.setAVOUCH_CREDIT_RATING_SP(rvo.getAVOUCH_CREDIT_RATING_SP());
						vo.setAVOUCH_CREDIT_RATING_MODDY(rvo.getAVOUCH_CREDIT_RATING_MODDY());
						vo.setAVOUCH_CREDIT_RATING_FITCH(rvo.getAVOUCH_CREDIT_RATING_FITCH());
						vo.setSTART_DATE_OF_BUYBACK(rvo.getSTART_DATE_OF_BUYBACK());
						vo.setFIXED_RATE_DURATION(rvo.getFIXED_RATE_DURATION());
						vo.setCURRENCY_EXCHANGE(rvo.getCURRENCY_EXCHANGE());
						vo.setFLOATING_DIVIDEND_RATE(rvo.getFLOATING_DIVIDEND_RATE());
						vo.setINVESTMENT_TARGETS(rvo.getINVESTMENT_TARGETS());
						vo.setCNR_YIELD(rvo.getCNR_YIELD());
						vo.setRATE_OF_CHANNEL(rvo.getRATE_OF_CHANNEL());
						vo.setPERFORMANCE_REVIEW(rvo.getPERFORMANCE_REVIEW());
						vo.setBASE_AMT_OF_PURCHASE(rvo.getBASE_AMT_OF_PURCHASE());
						vo.setUNIT_AMT_OF_PURCHASE(rvo.getUNIT_AMT_OF_PURCHASE());
						vo.setSTOCK_BOND_TYPE(rvo.getSTOCK_BOND_TYPE());

						vo.setACT_TYPE("A");
						vo.setREVIEW_STATUS("Y");
						dam.create(vo);

						TBPRD_SNVO svVO = new TBPRD_SNVO();
						svVO.setPRD_ID(rvo.getPRD_ID());
						svVO.setRATE_GUARANTEEPAY(rvo.getRATE_GUARANTEEPAY());
						svVO.setPROJECT(rvo.getPROJECT());
						svVO.setCUSTOMER_LEVEL(rvo.getCUSTOMER_LEVEL());
						svVO.setBOND_VALUE(rvo.getBOND_VALUE());
						dam.create(svVO);
					}
					// 修改
					else if("M".equals(rvo.getACT_TYPE())) {
						TBPRD_SNINFOVO vo = (TBPRD_SNINFOVO) dam.findByPKey(TBPRD_SNINFOVO.TABLE_UID, rvo.getPRD_ID());
						TBPRD_SNVO snVO = (TBPRD_SNVO) dam.findByPKey(TBPRD_SNVO.TABLE_UID, rvo.getPRD_ID());
						if (vo != null && snVO != null) {
							vo.setISIN_CODE(rvo.getISIN_CODE());
							vo.setCREDIT_RATING_SP(rvo.getCREDIT_RATING_SP());
							vo.setCREDIT_RATING_MODDY(rvo.getCREDIT_RATING_MODDY());
							vo.setCREDIT_RATING_FITCH(rvo.getCREDIT_RATING_FITCH());
							vo.setAVOUCH_CREDIT_RATING_SP(rvo.getAVOUCH_CREDIT_RATING_SP());
							vo.setAVOUCH_CREDIT_RATING_MODDY(rvo.getAVOUCH_CREDIT_RATING_MODDY());
							vo.setAVOUCH_CREDIT_RATING_FITCH(rvo.getAVOUCH_CREDIT_RATING_FITCH());
							vo.setSTART_DATE_OF_BUYBACK(rvo.getSTART_DATE_OF_BUYBACK());
							vo.setFIXED_RATE_DURATION(rvo.getFIXED_RATE_DURATION());
							vo.setCURRENCY_EXCHANGE(rvo.getCURRENCY_EXCHANGE());
							vo.setFLOATING_DIVIDEND_RATE(rvo.getFLOATING_DIVIDEND_RATE());
							vo.setINVESTMENT_TARGETS(rvo.getINVESTMENT_TARGETS());
							vo.setCNR_YIELD(rvo.getCNR_YIELD());

							updateRateOfChannelDate(vo, rvo);
							vo.setRATE_OF_CHANNEL(rvo.getRATE_OF_CHANNEL());

							vo.setPERFORMANCE_REVIEW(rvo.getPERFORMANCE_REVIEW());
							vo.setBASE_AMT_OF_PURCHASE(rvo.getBASE_AMT_OF_PURCHASE());
							vo.setUNIT_AMT_OF_PURCHASE(rvo.getUNIT_AMT_OF_PURCHASE());
							vo.setACT_TYPE("M");
							vo.setREVIEW_STATUS("Y");
							vo.setSTOCK_BOND_TYPE(rvo.getSTOCK_BOND_TYPE());
							dam.update(vo);

							snVO.setRATE_GUARANTEEPAY(rvo.getRATE_GUARANTEEPAY());
							snVO.setPROJECT(rvo.getPROJECT());
							snVO.setCUSTOMER_LEVEL(rvo.getCUSTOMER_LEVEL());
							snVO.setBOND_VALUE(rvo.getBOND_VALUE());
							dam.update(snVO);
						} else {
							// 顯示資料不存在
							throw new APException("ehl_01_common_001");
						}
					}
					// 刪除
					else if("D".equals(rvo.getACT_TYPE())) {
						TBPRD_SNINFOVO vo = (TBPRD_SNINFOVO) dam.findByPKey(TBPRD_SNINFOVO.TABLE_UID, rvo.getPRD_ID());

						if (vo != null) {
							dam.delete(vo);
						} else {
							// 顯示資料不存在
							throw new APException("ehl_01_common_001");
						}
					}
					
					// #0704: 保本率更新到 TBPRD_SN
					TBPRD_SNVO snvo = (TBPRD_SNVO) dam.findByPKey(TBPRD_SNVO.TABLE_UID, rvo.getPRD_ID());
					if (snvo != null) {
						snvo.setRATE_GUARANTEEPAY(rvo.getRATE_GUARANTEEPAY());
						dam.update(snvo);
					}

				}
				rvo.setREVIEW_STATUS(inputVO.getStatus());
				dam.update(rvo);
			} else
				throw new APException("ehl_01_common_001");
		}

		this.sendRtnObject(null);
	}

	/** 當 RATE_OF_CHANNEL 欄位數值變動時（rvo 該欄位的數值與 vo 不同） ，需要更新 RATE_OF_CHANNEL_DATE 時間 **/
	private void updateRateOfChannelDate(TBPRD_SNINFOVO vo, TBPRD_SNINFO_REVIEWVO rvo) {
		if (vo.getRATE_OF_CHANNEL() == null && rvo.getRATE_OF_CHANNEL() == null) return;
		if (vo.getRATE_OF_CHANNEL() != null && rvo.getRATE_OF_CHANNEL() != null && vo.getRATE_OF_CHANNEL().compareTo(rvo.getRATE_OF_CHANNEL()) == 0) return;

		vo.setRATE_OF_CHANNEL_DATE(new Timestamp(new Date().getTime()));
	}

	public void download(Object body, IPrimitiveMap header) throws JBranchException {
		WorkStation ws = DataManager.getWorkStation(uuid);
		PRD270InputVO inputVO = (PRD270InputVO) body;
		PRD270OutputVO return_VO = new PRD270OutputVO();
		dam = this.getDataAccessManager();

		// getRoleList
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT COUNT(ROLEID) AS COUNTS FROM TBSYSSECUROLPRIASS ");
		sql.append("WHERE PRIVILEGEID IN (SELECT PRIVILEGEID FROM TBSYSSECUPRIFUNMAP WHERE ITEMID = 'PRD270' AND FUNCTIONID = 'confirm') AND ROLEID = :roleID ");
		queryCondition.setObject("roleID", getUserVariable(FubonSystemVariableConsts.LOGINROLE));
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> privilegeCount = dam.exeQuery(queryCondition);
		//
		sql = new StringBuffer();
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sql.append("SELECT BASE_AMT_OF_PURCHASE, UNIT_AMT_OF_PURCHASE, RATE_GUARANTEEPAY, STOCK_BOND_TYPE, PRD_ID,SN_CNAME_A,ISIN_CODE,CREDIT_RATING_SP,CREDIT_RATING_MODDY,CREDIT_RATING_FITCH,AVOUCH_CREDIT_RATING_SP,AVOUCH_CREDIT_RATING_MODDY,AVOUCH_CREDIT_RATING_FITCH,START_DATE_OF_BUYBACK,FIXED_RATE_DURATION,CURRENCY_EXCHANGE,FLOATING_DIVIDEND_RATE,INVESTMENT_TARGETS,CNR_YIELD,RATE_OF_CHANNEL,PERFORMANCE_REVIEW,ACT_TYPE,REVIEW_STATUS,CREATOR FROM ( ");
		// 覆核主管
		if (((BigDecimal) privilegeCount.get(0).get("COUNTS")).compareTo(new BigDecimal(0)) > 0) {
			sql.append("SELECT rw.BASE_AMT_OF_PURCHASE, rw.UNIT_AMT_OF_PURCHASE, rw.RATE_GUARANTEEPAY, rw.STOCK_BOND_TYPE, rw.PRD_ID,sn.SN_CNAME_A,rw.ISIN_CODE,rw.CREDIT_RATING_SP,rw.CREDIT_RATING_MODDY,rw.CREDIT_RATING_FITCH,rw.AVOUCH_CREDIT_RATING_SP,rw.AVOUCH_CREDIT_RATING_MODDY,rw.AVOUCH_CREDIT_RATING_FITCH,rw.START_DATE_OF_BUYBACK,rw.FIXED_RATE_DURATION,rw.CURRENCY_EXCHANGE,rw.FLOATING_DIVIDEND_RATE,rw.INVESTMENT_TARGETS,rw.CNR_YIELD,rw.RATE_OF_CHANNEL,rw.PERFORMANCE_REVIEW,rw.ACT_TYPE,rw.REVIEW_STATUS,rw.CREATOR ");
			sql.append("FROM TBPRD_SNINFO_REVIEW rw left join TBPRD_SN sn on rw.PRD_ID = sn.PRD_ID ");
			sql.append("WHERE rw.REVIEW_STATUS = 'W' ");
			sql.append("UNION ");
			sql.append("SELECT info.BASE_AMT_OF_PURCHASE, info.UNIT_AMT_OF_PURCHASE, sn.RATE_GUARANTEEPAY, info.STOCK_BOND_TYPE, info.PRD_ID,sn.SN_CNAME_A,info.ISIN_CODE,info.CREDIT_RATING_SP,info.CREDIT_RATING_MODDY,info.CREDIT_RATING_FITCH,info.AVOUCH_CREDIT_RATING_SP,info.AVOUCH_CREDIT_RATING_MODDY,info.AVOUCH_CREDIT_RATING_FITCH,info.START_DATE_OF_BUYBACK,info.FIXED_RATE_DURATION,info.CURRENCY_EXCHANGE,info.FLOATING_DIVIDEND_RATE,info.INVESTMENT_TARGETS,info.CNR_YIELD,info.RATE_OF_CHANNEL,info.PERFORMANCE_REVIEW,info.ACT_TYPE,info.REVIEW_STATUS,null as CREATOR ");
			sql.append("FROM TBPRD_SNINFO info left join TBPRD_SN sn on info.PRD_ID = sn.PRD_ID ");
			sql.append("WHERE NVL(info.REVIEW_STATUS, 'Y') = 'Y' ");
			sql.append("AND info.PRD_ID NOT IN (SELECT PRD_ID FROM TBPRD_SNINFO_REVIEW WHERE REVIEW_STATUS = 'W') ");
		} else {
			sql.append("SELECT rw.BASE_AMT_OF_PURCHASE, rw.UNIT_AMT_OF_PURCHASE, rw.RATE_GUARANTEEPAY, rw.STOCK_BOND_TYPE, rw.PRD_ID,sn.SN_CNAME_A,rw.ISIN_CODE,rw.CREDIT_RATING_SP,rw.CREDIT_RATING_MODDY,rw.CREDIT_RATING_FITCH,rw.AVOUCH_CREDIT_RATING_SP,rw.AVOUCH_CREDIT_RATING_MODDY,rw.AVOUCH_CREDIT_RATING_FITCH,rw.START_DATE_OF_BUYBACK,rw.FIXED_RATE_DURATION,rw.CURRENCY_EXCHANGE,rw.FLOATING_DIVIDEND_RATE,rw.INVESTMENT_TARGETS,rw.CNR_YIELD,rw.RATE_OF_CHANNEL,rw.PERFORMANCE_REVIEW,rw.ACT_TYPE,rw.REVIEW_STATUS,null as CREATOR ");
			sql.append("FROM TBPRD_SNINFO_REVIEW rw left join TBPRD_SN sn on rw.PRD_ID = sn.PRD_ID ");
			sql.append("WHERE rw.REVIEW_STATUS = 'W' ");
			sql.append("AND rw.CREATOR = :creator ");
			sql.append("UNION ");
			sql.append("SELECT info.BASE_AMT_OF_PURCHASE, info.UNIT_AMT_OF_PURCHASE, sn.RATE_GUARANTEEPAY, info.STOCK_BOND_TYPE, info.PRD_ID,sn.SN_CNAME_A,info.ISIN_CODE,info.CREDIT_RATING_SP,info.CREDIT_RATING_MODDY,info.CREDIT_RATING_FITCH,info.AVOUCH_CREDIT_RATING_SP,info.AVOUCH_CREDIT_RATING_MODDY,info.AVOUCH_CREDIT_RATING_FITCH,info.START_DATE_OF_BUYBACK,info.FIXED_RATE_DURATION,info.CURRENCY_EXCHANGE,info.FLOATING_DIVIDEND_RATE,info.INVESTMENT_TARGETS,info.CNR_YIELD,info.RATE_OF_CHANNEL,info.PERFORMANCE_REVIEW,rw.ACT_TYPE,rw.REVIEW_STATUS,null as CREATOR ");
			sql.append("FROM TBPRD_SNINFO info ");
			sql.append("left join TBPRD_SN sn on info.PRD_ID = sn.PRD_ID ");
			sql.append("left join TBPRD_SNINFO_REVIEW rw on rw.PRD_ID = info.PRD_ID and rw.REVIEW_STATUS = 'W' ");
			sql.append("WHERE NVL(info.REVIEW_STATUS, 'Y') = 'Y' ");
			sql.append("AND info.PRD_ID NOT IN (SELECT PRD_ID FROM TBPRD_SNINFO_REVIEW WHERE REVIEW_STATUS = 'W' AND CREATOR = :creator) ");
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
			String fileName = "SN清單_"+ sdf.format(new Date()) + "_" + ws.getUser().getUserID() + ".csv";
			List listCSV = new ArrayList();
			for (Map<String, Object> map : list) {
				// 18 column
				String[] records = new String[18];
				int i = 0;
				if ("W".equals(ObjectUtils.toString(map.get("REVIEW_STATUS"))))
					records[i] = "覆核中";
				else
					records[i] = "已覆核";
				records[++i] = checkIsNull(map, "PRD_ID");
				records[++i] = checkIsNull(map, "SN_CNAME_A");
				records[++i] = checkIsNull(map, "ISIN_CODE");
				records[++i] = "標普：" + checkIsNull(map, "CREDIT_RATING_SP") + "\n穆迪：" + checkIsNull(map, "CREDIT_RATING_MODDY") + "\n惠譽：" + checkIsNull(map, "CREDIT_RATING_FITCH");
				records[++i] = "標普：" + checkIsNull(map, "AVOUCH_CREDIT_RATING_SP") + "\n穆迪：" + checkIsNull(map, "AVOUCH_CREDIT_RATING_MODDY") + "\n惠譽：" + checkIsNull(map, "AVOUCH_CREDIT_RATING_FITCH");
				records[++i] = "=\"" + checkIsNull(map, "START_DATE_OF_BUYBACK") + "\"";
				records[++i] = checkIsNull(map, "FIXED_RATE_DURATION");
				records[++i] = checkIsNull(map, "CURRENCY_EXCHANGE");
				records[++i] = checkIsNull(map, "FLOATING_DIVIDEND_RATE");
				records[++i] = checkIsNull(map, "INVESTMENT_TARGETS");
				records[++i] = checkIsNull(map, "RATE_OF_CHANNEL");
				records[++i] = checkIsNull(map, "CNR_YIELD");
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
			String[] csvHeader = new String[18];
			int j = 0;
			csvHeader[j] = "覆核狀態";
			csvHeader[++j] = "SN代碼";
			csvHeader[++j] = "SN名稱";
			csvHeader[++j] = "ISIN CODE";
			csvHeader[++j] = "發行機構評等-標普/穆迪/惠譽";
			csvHeader[++j] = "保證機構評等-標普/穆迪/惠譽";
			csvHeader[++j] = "開始受理贖回日";
			csvHeader[++j] = "固定配息期間";
			csvHeader[++j] = "幣轉";
			csvHeader[++j] = "浮動配息率";
			csvHeader[++j] = "連結標的";
			csvHeader[++j] = "通路服務費率";
			csvHeader[++j] = "CNR分配率";
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
		PRD270InputVO inputVO = (PRD270InputVO) body;
		PRD270OutputVO return_VO = new PRD270OutputVO();
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
						else if(!"ISIN CODE".equals(str[1].trim()))
							throw new Exception(str[1]);
						else if(!"發行機構評等-標普".equals(str[2].trim()))
							throw new Exception(str[2]);
						else if(!"發行機構評等-穆迪".equals(str[3].trim()))
							throw new Exception(str[3]);
						else if(!"發行機構評等-惠譽".equals(str[4].trim()))
							throw new Exception(str[4]);
						else if(!"保證機構評等-標普".equals(str[5].trim()))
							throw new Exception(str[5]);
						else if(!"保證機構評等-穆迪".equals(str[6].trim()))
							throw new Exception(str[6]);
						else if(!"保證機構評等-惠譽".equals(str[7].trim()))
							throw new Exception(str[7]);
						else if(!"開始受理贖回日".equals(str[8].trim()))
							throw new Exception(str[8]);
						else if(!"固定配息期間".equals(str[9].trim()))
							throw new Exception(str[9]);
						else if(!"幣轉".equals(str[10].trim()))
							throw new Exception(str[10]);
						else if(!"浮動配息率".equals(str[11].trim()))
							throw new Exception(str[11]);
						else if(!"連結標的".equals(str[12].substring(0, 4)))
							throw new Exception(str[12]);
						else if(!"CNR分配率".equals(str[13].trim()))
							throw new Exception(str[13]);
						else if(!"通路服務費率".equals(str[14].trim()))
							throw new Exception(str[14]);
						else if(!"計績檔次".equals(str[15].trim()))
							throw new Exception(str[15]);
						else if(!"股債類型".equals(str[16].substring(0, 4)))
							throw new Exception(str[16]);
						else if(!"保本率".equals(str[17].trim()))
							throw new Exception(str[17]);
						else if(!"最低申購金額".equals(str[18].trim()))
							throw new Exception(str[18]);
						else if(!"申購單位金額".equals(str[19].trim()))
							throw new Exception(str[19]);
						else if(!"專案代碼".equals(str[20].trim()))
							throw new Exception(str[20]);
						else if(!"客群代碼".equals(str[21].trim()))
							throw new Exception(str[21]);
						else if(!"票面價值".equals(str[22].trim()))
							throw new Exception(str[22]);
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
				// TBPRD_SN
				QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				StringBuffer sql = new StringBuffer();
				sql.append("SELECT PRD_ID, RATE_GUARANTEEPAY FROM TBPRD_SN where PRD_ID = :id ");
				queryCondition.setObject("id", str[0].trim());
				queryCondition.setQueryString(sql.toString());
				List<Map<String, Object>> list = dam.exeQuery(queryCondition);
				if (list.size() == 0) {
					error.add(str[0]);
					continue;
				}

				// TBPRD_SNINFO check edit
				Boolean exist = false;
				queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sql = new StringBuffer();
				sql.append("SELECT PRD_ID FROM TBPRD_SNINFO where PRD_ID = :id ");
				queryCondition.setObject("id", str[0].trim());
				queryCondition.setQueryString(sql.toString());
				List<Map<String, Object>> list2 = dam.exeQuery(queryCondition);
				if (list2.size() > 0)
					exist = true;
				// TBPRD_SNINFO_REVIEW
				queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sql = new StringBuffer();
				sql.append("SELECT PRD_ID FROM TBPRD_SNINFO_REVIEW where PRD_ID = :id and REVIEW_STATUS = 'W' ");
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
				sql.append("SELECT SQ_TBPRD_SNINFO_REVIEW.nextval AS SEQ FROM DUAL ");
				queryCondition.setQueryString(sql.toString());
				List<Map<String, Object>> SEQLIST = dam.exeQuery(queryCondition);
				BigDecimal seqNo = (BigDecimal) SEQLIST.get(0).get("SEQ");
				// add TBPRD_SNINFO_REVIEW
				TBPRD_SNINFO_REVIEWVO vo = new TBPRD_SNINFO_REVIEWVO();
				TBPRD_SNINFOVO info_vo = new TBPRD_SNINFOVO();
				info_vo = (TBPRD_SNINFOVO) dam.findByPKey(TBPRD_SNINFOVO.TABLE_UID, str[0].trim());
				TBPRD_SNVO snVO = (TBPRD_SNVO) dam.findByPKey(TBPRD_SNVO.TABLE_UID, str[0].trim());

				vo.setSEQ(seqNo);
				//
				if(utf_8_length(str[0]) > 16) {
					error3.add(str[0]);
					continue;
				} else
					vo.setPRD_ID(str[0].trim());
				//
				if(StringUtils.isNotBlank(str[1]) && !StringUtils.equals(str[1], "$")) {
					if(utf_8_length(str[1]) > 128) {
						error3.add(str[0]+":"+str[1]);
						continue;
					} else
						vo.setISIN_CODE(str[1]);
				} else
					vo.setISIN_CODE(info_vo != null ? info_vo.getISIN_CODE() : null);

				//
				if(StringUtils.isNotBlank(str[2]) && !StringUtils.equals(str[2], "$")) {
					if(utf_8_length(str[2]) > 10) {
						error3.add(str[0]+":"+str[2]);
						continue;
					} else
						vo.setCREDIT_RATING_SP(str[2]);
				} else
					vo.setCREDIT_RATING_SP(info_vo != null ? info_vo.getCREDIT_RATING_SP() : null);
				//
				if(StringUtils.isNotBlank(str[3]) && !StringUtils.equals(str[3], "$")) {
					if(utf_8_length(str[3]) > 10) {
						error3.add(str[0]+":"+str[3]);
						continue;
					} else
						vo.setCREDIT_RATING_MODDY(str[3]);
				} else
					vo.setCREDIT_RATING_MODDY(info_vo != null ? info_vo.getCREDIT_RATING_MODDY() : null);
				//
				if(StringUtils.isNotBlank(str[4]) && !StringUtils.equals(str[4], "$")) {
					if(utf_8_length(str[4]) > 10) {
						error3.add(str[0]+":"+str[4]);
						continue;
					} else
						vo.setCREDIT_RATING_FITCH(str[4]);
				} else
					vo.setCREDIT_RATING_FITCH(info_vo != null ? info_vo.getCREDIT_RATING_FITCH() : null);
				//
				if(StringUtils.isNotBlank(str[5]) && !StringUtils.equals(str[5], "$")) {
					if(utf_8_length(str[5]) > 10) {
						error3.add(str[0]+":"+str[5]);
						continue;
					} else
						vo.setAVOUCH_CREDIT_RATING_SP(str[5]);
				} else
					vo.setAVOUCH_CREDIT_RATING_SP(info_vo != null ? info_vo.getAVOUCH_CREDIT_RATING_SP() : null);
				//
				if(StringUtils.isNotBlank(str[6]) && !StringUtils.equals(str[6], "$")) {
					if(utf_8_length(str[6]) > 10) {
						error3.add(str[0]+":"+str[6]);
						continue;
					} else
						vo.setAVOUCH_CREDIT_RATING_MODDY(str[6]);
				} else
					vo.setAVOUCH_CREDIT_RATING_MODDY(info_vo != null ? info_vo.getAVOUCH_CREDIT_RATING_MODDY() : null);
				//
				if(StringUtils.isNotBlank(str[7]) && !StringUtils.equals(str[7], "$")) {
					if(utf_8_length(str[7]) > 10) {
						error3.add(str[0]+":"+str[7]);
						continue;
					} else
						vo.setAVOUCH_CREDIT_RATING_FITCH(str[7]);
				} else
					vo.setAVOUCH_CREDIT_RATING_FITCH(info_vo != null ? info_vo.getAVOUCH_CREDIT_RATING_FITCH() : null);
				//
				if(StringUtils.isNotBlank(str[8]) && !StringUtils.equals(str[8], "$")) {
					try {
						vo.setSTART_DATE_OF_BUYBACK(new Timestamp(sdf.parse(str[8]).getTime()));
					} catch (Exception e) {
						try {
							vo.setSTART_DATE_OF_BUYBACK(new Timestamp(sdf2.parse(str[8]).getTime()));
						} catch (Exception e2) {
							error3.add(str[0]+":"+str[8]);
							continue;
						}
					}
				} else
					vo.setSTART_DATE_OF_BUYBACK(info_vo != null ? info_vo.getSTART_DATE_OF_BUYBACK() : null);
//					vo.setSTART_DATE_OF_BUYBACK(null);
				//
				if(StringUtils.isNotBlank(str[9]) && !StringUtils.equals(str[9], "$")) {
					if(utf_8_length(str[9]) > 100) {
						error3.add(str[0]+":"+str[9]);
						continue;
					} else
						vo.setFIXED_RATE_DURATION(str[9]);
				} else
					vo.setFIXED_RATE_DURATION(info_vo != null ? info_vo.getFIXED_RATE_DURATION() : null);
				//
				if(StringUtils.isNotBlank(str[10]) && !StringUtils.equals(str[10], "$")) {
					if(utf_8_length(str[10]) > 5) {
						error3.add(str[0]+":"+str[10]);
						continue;
					} else
						vo.setCURRENCY_EXCHANGE(str[10]);
				} else
					vo.setCURRENCY_EXCHANGE(info_vo != null ? info_vo.getCURRENCY_EXCHANGE() : null);
				//
				if(StringUtils.isNotBlank(str[11]) && !StringUtils.equals(str[11], "$")) {
					try {
						BigDecimal str11 = new BigDecimal(str[11]);
						// NUMBER(6,2)
						if(getNumOfBigDecimal(str11) > 4)
							throw new Exception("");
						vo.setFLOATING_DIVIDEND_RATE(str11);
					} catch (Exception e) {
						error3.add(str[0]+":"+str[11]);
						continue;
					}
				} else
					vo.setFLOATING_DIVIDEND_RATE(info_vo != null ? info_vo.getFLOATING_DIVIDEND_RATE() : null);
//					vo.setFLOATING_DIVIDEND_RATE(null);
				//
				if(StringUtils.isNotBlank(str[12]) && !StringUtils.equals(str[12], "$")) {
					str[12] = str[12].replace("\n", "").replace("\r", "");
					String[] targets = str[12].split(";");
					if(targets.length > 20) {
						error3.add(str[0]+":"+str[12]);
						continue;
					} else
						vo.setINVESTMENT_TARGETS(str[12]);
				} else
					vo.setINVESTMENT_TARGETS(info_vo != null ? info_vo.getINVESTMENT_TARGETS() : null);
//					vo.setINVESTMENT_TARGETS(null);
				//
				if(StringUtils.isNotBlank(str[13]) && !StringUtils.equals(str[13], "$")) {
					try {
						BigDecimal str13 = new BigDecimal(str[13]);
						// NUMBER(9,6)
						if(getNumOfBigDecimal(str13) > 3)
							throw new Exception("");
						vo.setCNR_YIELD(str13);
					} catch (Exception e) {
						error3.add(str[0]+":"+str[13]);
						continue;
					}
				} else
					vo.setCNR_YIELD(info_vo != null ? info_vo.getCNR_YIELD() : null);
//					vo.setCNR_YIELD(null);
				//
				if(StringUtils.isNotBlank(str[14]) && !StringUtils.equals(str[14], "$")) {
					try {
						BigDecimal str14 = new BigDecimal(str[14]);
						// NUMBER(9,6)
						if(getNumOfBigDecimal(str14) > 3)
							throw new Exception("");
						vo.setRATE_OF_CHANNEL(str14);
					} catch (Exception e) {
						error3.add(str[0]+":"+str[14]);
						continue;
					}
				} else
					vo.setRATE_OF_CHANNEL(info_vo != null ? info_vo.getRATE_OF_CHANNEL() : null);
//					vo.setRATE_OF_CHANNEL(null);
				//
				if(StringUtils.isNotBlank(str[15]) && !StringUtils.equals(str[15], "$")) {
					if(utf_8_length(str[15]) > 6) {
						error3.add(str[0]+":"+str[15]);
						continue;
					} else
						vo.setPERFORMANCE_REVIEW(str[15]);
				} else
					vo.setPERFORMANCE_REVIEW(info_vo != null ? info_vo.getPERFORMANCE_REVIEW() : null);

				// 股債類型
				if(StringUtils.isNotBlank(str[16])) {
					String stock_bond_type = str[16].trim().toUpperCase();
					if("S".equals(stock_bond_type) || "B".equals(stock_bond_type) || "$".equals(stock_bond_type)) {
						if("$".equals(stock_bond_type)) {
							vo.setSTOCK_BOND_TYPE(info_vo != null ? info_vo.getSTOCK_BOND_TYPE() : null);
						} else {
							vo.setSTOCK_BOND_TYPE(stock_bond_type);
						}
					} else {
						throw new JBranchException("股債類型輸入格式有誤，請輸入S或B。");
					}
				} else {
					throw new JBranchException("股債類型為必填欄位。");
				}

				// 保本率
				if(StringUtils.isNotBlank(str[17]) && !StringUtils.equals(str[17], "$")) {
					try {
						BigDecimal str17 = new BigDecimal(str[17]);
						// NUMBER(12, 2)
						if(getNumOfBigDecimal(str17) > 10)
							throw new Exception("");
						vo.setRATE_GUARANTEEPAY(str17);
					} catch (Exception e) {
						error3.add(str[0]+":"+str[17]);
						continue;
					}
				} else
					vo.setRATE_GUARANTEEPAY((BigDecimal) list.get(0).get("RATE_GUARANTEEPAY"));

				// 最低申購金額
				if(StringUtils.isNotBlank(str[18]) && !StringUtils.equals(str[18], "$")) {
					try {
						BigDecimal str18 = new BigDecimal(str[18]);
						// NUMBER(13, 2)
						if(getNumOfBigDecimal(str18) > 11)
							throw new Exception("");
						vo.setBASE_AMT_OF_PURCHASE(str18);
					} catch (Exception e) {
						error3.add(str[0]+":"+str[18]);
						continue;
					}
				} else
					vo.setBASE_AMT_OF_PURCHASE(info_vo != null? info_vo.getBASE_AMT_OF_PURCHASE(): null);

				// 申購單位金額
				if(StringUtils.isNotBlank(str[19]) && !StringUtils.equals(str[19], "$")) {
					try {
						BigDecimal str19 = new BigDecimal(str[19]);
						// NUMBER(13, 2)
						if(getNumOfBigDecimal(str19) > 11)
							throw new Exception("");
						vo.setUNIT_AMT_OF_PURCHASE(str19);
					} catch (Exception e) {
						error3.add(str[0]+":"+str[19]);
						continue;
					}
				} else
					vo.setUNIT_AMT_OF_PURCHASE(info_vo != null? info_vo.getUNIT_AMT_OF_PURCHASE(): null);

				// 專案代碼
				if (StringUtils.isNotBlank(str[20])) {
					if (str[20].length() > 20) {
						error3.add(str[0] + ":" + str[20]);
						continue;
					}

					if (StringUtils.equals((str[20].trim()), "0")) {
						vo.setPROJECT(null);
					} else if (StringUtils.equals((str[20].trim()), "$")) {
						vo.setPROJECT(snVO != null ? snVO.getPROJECT() : null);
					} else {
						vo.setPROJECT(str[20].trim());
					}
				} else {
					vo.setPROJECT(snVO != null ? snVO.getPROJECT() : null);
				}
				// 客群代碼
				if (StringUtils.isNotBlank(str[21])) {
					if (str[21].length() > 20) {
						error3.add(str[0] + ":" + str[21]);
						continue;
					}

					if (StringUtils.equals((str[21].trim()), "0")) {
						vo.setCUSTOMER_LEVEL(null);
					} else if (StringUtils.equals((str[21].trim()), "$")) {
						vo.setCUSTOMER_LEVEL(snVO != null ? snVO.getCUSTOMER_LEVEL() : null);
					} else {
						vo.setCUSTOMER_LEVEL(str[21].trim());
					}
				} else {
					vo.setCUSTOMER_LEVEL(snVO != null ? snVO.getCUSTOMER_LEVEL() : null);
				}
				
				// 票面價值
				if(StringUtils.isNotBlank(str[22]) && !StringUtils.equals(str[22], "$")) {
					try {
						BigDecimal str22 = new BigDecimal(str[22]);
						// NUMBER(9, 0)
						if(getNumOfBigDecimal(str22) > 9) throw new Exception("");
						vo.setBOND_VALUE(str22);
					} catch (Exception e) {
						error3.add(str[0] + ":" + str[22]);
						continue;
					}
				} else {
					vo.setBOND_VALUE(snVO != null ? snVO.getBOND_VALUE() : null);
				}
				
				// 自動註記
				if(StringUtils.isBlank(vo.getCUSTOMER_LEVEL())  && "Y".equals(snVO.getPI_BUY()) ) {
					vo.setCUSTOMER_LEVEL("A1");
				}

				if(!exist)
					vo.setACT_TYPE("A");
				else
					vo.setACT_TYPE("M");
				vo.setREVIEW_STATUS("W");
				dam.create(vo);
			}
		}
		return_VO.setErrorList(error);
		return_VO.setErrorList2(error2);
		return_VO.setErrorList3(error3);
		return_VO.setErrorList4(error4);
		return_VO.setErrorList5(error5);
		return_VO.setErrorList6(error6);

		this.sendRtnObject(return_VO);
	}

	public void downloadSimple(Object body, IPrimitiveMap header) throws Exception {
		this.notifyClientToDownloadFile("doc//PRD//PRD270_EXAMPLE.csv", "上傳指定商品代碼範例.csv");
	}

	public void getRank(Object body, IPrimitiveMap header) throws JBranchException {
		PRD270OutputVO return_VO = new PRD270OutputVO();
		dam = this.getDataAccessManager();

		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		queryCondition.setQueryString("select PRD_ID, SN_CNAME, RISKCATE_ID, PRD_RANK FROM TBPRD_SN WHERE PRD_RANK IS NOT NULL ORDER BY PRD_RANK");
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		return_VO.setResultList(list);
		this.sendRtnObject(return_VO);
	}

	public void saveSort(Object body, IPrimitiveMap header) throws JBranchException {
		PRD270InputVO inputVO = (PRD270InputVO) body;
		dam = this.getDataAccessManager();

		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		queryCondition.setQueryString("UPDATE TBPRD_SN SET PRD_RANK = null WHERE PRD_RANK IS NOT NULL");
		dam.exeUpdate(queryCondition);

		for(Map<String, Object> map : inputVO.getReview_list()) {
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			queryCondition.setQueryString("UPDATE TBPRD_SN SET PRD_RANK = :rank, PRD_RANK_DATE = sysdate WHERE PRD_ID = :prd_id");
			queryCondition.setObject("rank", map.get("rank"));
			queryCondition.setObject("prd_id", map.get("prd_id"));
			dam.exeUpdate(queryCondition);
		}

		this.sendRtnObject(null);
	}

	public void downloadSimpleTemp(Object body, IPrimitiveMap header) throws Exception {
		notifyClientToDownloadFile("doc//PRD//PRD270_EXAMPLE2.csv", "上傳指定商品代碼範例.csv");
		this.sendRtnObject(null);
	}

	public void uploadTemp(Object body, IPrimitiveMap header) throws Exception {
		PRD270InputVO inputVO = (PRD270InputVO) body;
		PRD270OutputVO return_VO = new PRD270OutputVO();
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
						else if(!"SN名稱簡稱".equals(str[1].trim()))
							throw new Exception(str[1]);
						else if(!"SN名稱全稱".equals(str[2].trim()))
							throw new Exception(str[2]);
						else if(!"計價幣別".equals(str[3].trim()))
							throw new Exception(str[3]);
						else if(!"到期日期".equals(str[4].trim()))
							throw new Exception(str[4]);
						else if(!"商品風險等級".equals(str[5].trim()))
							throw new Exception(str[5]);
						else if(!"限專投申購(Y、空白)".equals(str[6].trim()))
							throw new Exception(str[6]);
						else if(!"限高資產申購(Y、空白)".equals(str[7].trim()))
							throw new Exception(str[7]);
						else if(!"限OBU申購(O、D、空白)".equals(str[8].trim()))
							throw new Exception(str[8]);
						else if(!"ISIN_CODE".equals(str[9].trim()))
							throw new Exception(str[9]);
						else if(!"募集開始日".equals(str[10].trim()))
							throw new Exception(str[10]);
						else if(!"募集結束日".equals(str[11].trim()))
							throw new Exception(str[11]);
						else if(!"股債類型".equals(str[12].substring(0, 4)))
							throw new Exception(str[12]);
						else if(!"保本率".equals(str[13]))
							throw new Exception(str[13]);
						else if(!"最低申購金額".equals(str[14]))
							throw new Exception(str[14]);
						else if(!"申購單位金額".equals(str[15]))
							throw new Exception(str[15]);
						else if(!"到期年限".equals(str[16]))
							throw new Exception(str[16]);
						else if(!"客製化商品(Y、空白)".equals(str[17]))
							throw new Exception(str[17]);
						else if(!"票面價值".equals(str[18]))
							throw new Exception(str[18]);
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
				if(StringUtils.equals(str[6],"Y") && StringUtils.equals(str[7],"Y") ) {
					error5.add(str[0]);
					continue;
				}
				idList.add(str[0].trim());
				// TBPRD_SN
				Boolean exist = false;
				QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				queryCondition.setQueryString("SELECT PRD_ID FROM TBPRD_SN where PRD_ID = :id");
				queryCondition.setObject("id", str[0].trim());
				List<Map<String, Object>> list = dam.exeQuery(queryCondition);
				if (list.size() > 0)
					exist = true;

				TBPRD_SNVO vo;
				if(exist)
					vo = (TBPRD_SNVO) dam.findByPKey(TBPRD_SNVO.TABLE_UID, str[0].trim());
				else {
					vo = new TBPRD_SNVO();
					if(utf_8_length(str[0]) > 16) {
						error3.add(str[0]);
						continue;
					} else
						vo.setPRD_ID(str[0].trim());
				}
				vo.setIS_SALE("Y");
				//
				if(utf_8_length(str[1]) > 255) {
					error3.add(str[0]+":"+str[1]);
					continue;
				} else
					vo.setSN_CNAME(str[1]);
				//
				if(utf_8_length(str[2]) > 255) {
					error3.add(str[0]+":"+str[2]);
					continue;
				} else
					vo.setSN_CNAME_A(str[2]);
				//
				vo.setSN_TYPE("SN");
				//
				if(utf_8_length(str[3]) > 3) {
					error3.add(str[0]+":"+str[3]);
					continue;
				} else
					vo.setCURRENCY_STD_ID(str[3]);
				//
				if(StringUtils.isNotBlank(str[4])) {
					try {
						vo.setDATE_OF_MATURITY(new Timestamp(sdf.parse(str[4]).getTime()));
					} catch (Exception e) {
						try {
							vo.setDATE_OF_MATURITY(new Timestamp(sdf2.parse(str[4]).getTime()));
						} catch (Exception e2) {
							error3.add(str[0]+":"+str[4]);
							continue;
						}
					}
				} else
					vo.setDATE_OF_MATURITY(null);
				// 商品風險等級
				Map<String, String> riskmap = new HashMap<String, String>();
				riskmap.put("P1", "");riskmap.put("P2", "");riskmap.put("P3", "");riskmap.put("P4", "");
				if(StringUtils.isBlank(str[5]))
					vo.setRISKCATE_ID(str[5]);
				else if(riskmap.containsKey(str[5].trim()))
					vo.setRISKCATE_ID(str[5].trim());
				else {
					error3.add(str[0]+":"+str[5]);
					continue;
				}
				//改為非Y即N
				if(StringUtils.isNotBlank(str[6])) {
					if(StringUtils.equals("Y", str[6].trim())) {
						vo.setPI_BUY(str[6]);
					} else {
						vo.setPI_BUY("N");
					}			
				} else
					vo.setPI_BUY("N");
				//改為非Y即N
				if(StringUtils.isNotBlank(str[7])) {
					if(StringUtils.equals("Y", str[7].trim())) {
						vo.setHNWC_BUY(str[7]);
					} else {
						vo.setHNWC_BUY("N");
					}			
				} else
					vo.setHNWC_BUY("N");
				
				//SN 限OBU申購(O、D、空白)邏輯調整
				if(StringUtils.isNotBlank(str[8])) {
					if(StringUtils.isBlank(obuYN.get(str[8].trim()))) {
						throw new JBranchException("限OBU申購(O、D、空白)輸入格式有誤，請輸入O(是)、D(否)、空白。");
					}else {
						vo.setOBU_BUY(str[8].toUpperCase());
					}
				}else {
					vo.setOBU_BUY(str[8]);
				}

				// 保本率
				if(StringUtils.isNotBlank(str[13])) {
					try {
						BigDecimal str13 = new BigDecimal(str[13]);
						// NUMBER(12, 2)
						if(getNumOfBigDecimal(str13) > 10)
							throw new Exception("");
						vo.setRATE_GUARANTEEPAY(str13);
					} catch (Exception e) {
						error3.add(str[0]+":"+str[13]);
						continue;
					}
				} else {
					vo.setRATE_GUARANTEEPAY(null);
				}

				// 到期年限
				if(StringUtils.isNotBlank(str[16])) {
					try {
						BigDecimal str16 = new BigDecimal(str[16]);
						// NUMBER(5, 2)
						if(getNumOfBigDecimal(str16) > 3)
							throw new Exception("");
						vo.setYEAR_OF_MATURITY(str16);
					} catch (Exception e) {
						error3.add(str[0]+":"+str[16]);
						continue;
					}
				} else {
					vo.setYEAR_OF_MATURITY(null);
				}
				
				// 客製化商品 改為非Y即N
				if(StringUtils.isNotBlank(str[17])) {
					if(StringUtils.equals("Y", str[17].trim())) {
						vo.setRECORD_FLAG(str[17]);
					} else {
						vo.setRECORD_FLAG("N");
					}			
				} else
					vo.setRECORD_FLAG("N");
				
				// 票面價值
				if(StringUtils.isNotBlank(str[18])) {
					try {
						BigDecimal str18 = new BigDecimal(str[18]);
						// NUMBER(9, 0)
						if(getNumOfBigDecimal(str18) > 9) throw new Exception("");
						vo.setBOND_VALUE(str18);
					} catch (Exception e) {
						error3.add(str[0] + ":" + str[18]);
						continue;
					}
				} else {
					vo.setBOND_VALUE(null);
				}

				// 自動註記
				if(StringUtils.isBlank(vo.getCUSTOMER_LEVEL()) && "Y".equals(vo.getPI_BUY()) ) {
					vo.setCUSTOMER_LEVEL("A1");
				}

				//
				if(exist)
					dam.update(vo);
				else
					dam.create(vo);

				// TBPRD_SNINFO
				Boolean exist2 = false;
				queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				queryCondition.setQueryString("SELECT PRD_ID FROM TBPRD_SNINFO where PRD_ID = :id");
				queryCondition.setObject("id", str[0].trim());
				List<Map<String, Object>> list2 = dam.exeQuery(queryCondition);
				if (list2.size() > 0)
					exist2 = true;

				TBPRD_SNINFOVO vo2;
				if(exist2)
					vo2 = (TBPRD_SNINFOVO) dam.findByPKey(TBPRD_SNINFOVO.TABLE_UID, str[0].trim());
				else {
					vo2 = new TBPRD_SNINFOVO();
					vo2.setPRD_ID(str[0].trim());
				}
				//
				if(utf_8_length(str[9]) > 128) {
					error3.add(str[0]+":"+str[9]);
					continue;
				} else
					vo2.setISIN_CODE(str[9]);
				//
				if(StringUtils.isNotBlank(str[10])) {
					try {
						vo2.setINV_SDATE(new Timestamp(sdf.parse(str[10]).getTime()));
					} catch (Exception e) {
						try {
							vo2.setINV_SDATE(new Timestamp(sdf2.parse(str[10]).getTime()));
						} catch (Exception e2) {
							error3.add(str[0]+":"+str[10]);
							continue;
						}
					}
				} else
					vo2.setINV_SDATE(null);
				//
				if(StringUtils.isNotBlank(str[11])) {
					try {
						vo2.setINV_EDATE(new Timestamp(sdf.parse(str[11]).getTime()));
					} catch (Exception e) {
						try {
							vo2.setINV_EDATE(new Timestamp(sdf2.parse(str[11]).getTime()));
						} catch (Exception e2) {
							error3.add(str[0]+":"+str[11]);
							continue;
						}
					}
				} else
					vo2.setINV_EDATE(null);

				// 股債類型
				if(StringUtils.isNotBlank(str[12])) {
					String stock_bond_type = str[12].trim().toUpperCase();
					if("S".equals(stock_bond_type) || "B".equals(stock_bond_type)) {
						vo2.setSTOCK_BOND_TYPE(stock_bond_type);
					} else {
						throw new JBranchException("股債類型輸入格式有誤，請輸入S或B。");
					}
				} else {
					throw new JBranchException("股債類型為必填欄位。");
				}

				// 最低申購金額
				if(StringUtils.isNotBlank(str[14])) {
					try {
						BigDecimal str14 = new BigDecimal(str[14]);
						// NUMBER(13, 2)
						if(getNumOfBigDecimal(str14) > 11)
							throw new Exception("");
						vo2.setBASE_AMT_OF_PURCHASE(str14);
					} catch (Exception e) {
						error3.add(str[0]+":"+str[14]);
						continue;
					}
				} else
					vo2.setBASE_AMT_OF_PURCHASE(null);

				// 申購單位金額
				if(StringUtils.isNotBlank(str[15])) {
					try {
						BigDecimal str15 = new BigDecimal(str[15]);
						// NUMBER(13, 2)
						if(getNumOfBigDecimal(str15) > 11)
							throw new Exception("");
						vo2.setUNIT_AMT_OF_PURCHASE(str15);
					} catch (Exception e) {
						error3.add(str[0]+":"+str[15]);
						continue;
					}
				} else
					vo2.setUNIT_AMT_OF_PURCHASE(null);
				//
				if(exist)
					dam.update(vo2);
				else
					dam.create(vo2);
			}
		}
		return_VO.setErrorList3(error3);
		return_VO.setErrorList4(error4);
		return_VO.setErrorList5(error5);
		this.sendRtnObject(return_VO);
	}

	/*
	 * #1404 上傳專案參數 使用delete insert 複製PRD230同名功能修改
	 */
	public void uploadProject(Object body, IPrimitiveMap header) throws Exception {
		PRD270InputVO inputVO = (PRD270InputVO) body;
		PRD270OutputVO return_VO = new PRD270OutputVO();

		dam = this.getDataAccessManager();
		// 先清空
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("delete TBSYSPARAMETER where PARAM_TYPE = :PARAM_TYPE ");
		queryCondition.setObject("PARAM_TYPE", "PRD.SN_PROJECT");
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
				parameterPK.setPARAM_TYPE("PRD.SN_PROJECT");
				parameterPK.setPARAM_CODE(str[0].trim());
				TBSYSPARAMETERVO parameterVO = new TBSYSPARAMETERVO();
				parameterVO.setcomp_id(parameterPK);
				parameterVO.setPARAM_NAME(str[1].trim());
				parameterVO.setPARAM_NAME_EDIT(str[1].trim());

				sql = new StringBuffer();
				sql.append("select max(PARAM_ORDER) as COUNT from TBSYSPARAMETER where PARAM_TYPE = :PARAM_TYPE");
				queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				queryCondition.setObject("PARAM_TYPE", "PRD.SN_PROJECT");
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
	 *
	 * 2023.01.30 #1404 上傳客群參數 使用delete insert 複製PRD230同名功能修改
	 *
	 */
	public void uploadCustomerLevel(Object body, IPrimitiveMap header) throws Exception {
		PRD270InputVO inputVO = (PRD270InputVO) body;
		PRD270OutputVO return_VO = new PRD270OutputVO();

		dam = this.getDataAccessManager();
		// 先清空
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("delete TBSYSPARAMETER where PARAM_TYPE = :PARAM_TYPE ");
		queryCondition.setObject("PARAM_TYPE", "PRD.SN_CUSTOMER_LEVEL");
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

				TBSYSPARAMETERPK parameterPK = new TBSYSPARAMETERPK();
				parameterPK.setPARAM_TYPE("PRD.SN_CUSTOMER_LEVEL");
				parameterPK.setPARAM_CODE(str[0].trim());
				TBSYSPARAMETERVO parameterVO = new TBSYSPARAMETERVO();
				parameterVO.setcomp_id(parameterPK);
				parameterVO.setPARAM_NAME(str[1].trim());
				parameterVO.setPARAM_NAME_EDIT(str[1].trim());

				sql = new StringBuffer();
				sql.append("select max(PARAM_ORDER) as COUNT from TBSYSPARAMETER where PARAM_TYPE = :PARAM_TYPE");
				queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				queryCondition.setObject("PARAM_TYPE", "PRD.SN_CUSTOMER_LEVEL");
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
}
