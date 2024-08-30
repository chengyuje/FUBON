package com.systex.jbranch.app.server.fps.prd250;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

import com.systex.jbranch.app.common.fps.table.*;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.platformdao.table.TBSYSPARAMETERPK;
import com.systex.jbranch.platform.common.platformdao.table.TBSYSPARAMETERVO;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataManager.DataManager;
import com.systex.jbranch.platform.common.dataManager.WorkStation;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.CSVUtil;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * prd250
 *
 * @author moron
 * @date 2016/08/24
 * @spec null
 */
@Component("prd250")
@Scope("request")
public class PRD250 extends FubonWmsBizLogic {
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(PRD250.class);

	public void inquire(Object body, IPrimitiveMap header) throws JBranchException {
		WorkStation ws = DataManager.getWorkStation(uuid);
		PRD250InputVO inputVO = (PRD250InputVO) body;
		PRD250OutputVO return_VO = new PRD250OutputVO();
		dam = this.getDataAccessManager();

		// getRoleList
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT COUNT(ROLEID) AS COUNTS FROM TBSYSSECUROLPRIASS ");
		sql.append("WHERE PRIVILEGEID IN (SELECT PRIVILEGEID FROM TBSYSSECUPRIFUNMAP WHERE ITEMID = 'PRD250' AND FUNCTIONID = 'confirm') AND ROLEID = :roleID ");
		queryCondition.setObject("roleID", getUserVariable(FubonSystemVariableConsts.LOGINROLE));
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> privilegeCount = dam.exeQuery(queryCondition);
		//
		sql = new StringBuffer();
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sql.append("SELECT STOCK_BOND_TYPE, SEQ,PRD_ID,BOND_CNAME_A,ISIN_CODE,BOND_PRIORITY,CREDIT_RATING_SP,CREDIT_RATING_MODDY,CREDIT_RATING_FITCH,BOND_CREDIT_RATING_SP,BOND_CREDIT_RATING_MODDY,BOND_CREDIT_RATING_FITCH,ISSUER_BUYBACK,RISK_CHECKLIST,CNR_YIELD,CNR_MULTIPLE,MULTIPLE_SDATE,MULTIPLE_EDATE,ACT_TYPE,REVIEW_STATUS,CREATOR, PROJECT, CUSTOMER_LEVEL FROM ( ");
		// 覆核人員
		if (((BigDecimal) privilegeCount.get(0).get("COUNTS")).compareTo(new BigDecimal(0)) > 0) {
			sql.append("SELECT rw.STOCK_BOND_TYPE, rw.SEQ,rw.PRD_ID,bond.BOND_CNAME_A,rw.ISIN_CODE,rw.BOND_PRIORITY,rw.CREDIT_RATING_SP,rw.CREDIT_RATING_MODDY,rw.CREDIT_RATING_FITCH,rw.BOND_CREDIT_RATING_SP,rw.BOND_CREDIT_RATING_MODDY,rw.BOND_CREDIT_RATING_FITCH,rw.ISSUER_BUYBACK,rw.RISK_CHECKLIST,rw.CNR_YIELD,rw.CNR_MULTIPLE,rw.MULTIPLE_SDATE,rw.MULTIPLE_EDATE,rw.ACT_TYPE,rw.REVIEW_STATUS,rw.CREATOR, rw.PROJECT, rw.CUSTOMER_LEVEL ");
			sql.append("FROM TBPRD_BONDINFO_REVIEW rw left join TBPRD_BOND bond on rw.PRD_ID = bond.PRD_ID ");
			sql.append("WHERE rw.REVIEW_STATUS = 'W' ");
			sql.append("UNION ");
			sql.append("SELECT info.STOCK_BOND_TYPE, null as SEQ,info.PRD_ID,bond.BOND_CNAME_A,info.ISIN_CODE,info.BOND_PRIORITY,info.CREDIT_RATING_SP,info.CREDIT_RATING_MODDY,info.CREDIT_RATING_FITCH,info.BOND_CREDIT_RATING_SP,info.BOND_CREDIT_RATING_MODDY,info.BOND_CREDIT_RATING_FITCH,info.ISSUER_BUYBACK,info.RISK_CHECKLIST,info.CNR_YIELD,info.CNR_MULTIPLE,info.MULTIPLE_SDATE,info.MULTIPLE_EDATE,rw.ACT_TYPE,rw.REVIEW_STATUS,null as CREATOR, bond.PROJECT, bond.CUSTOMER_LEVEL ");
			sql.append("FROM TBPRD_BONDINFO info left join TBPRD_BOND bond on info.PRD_ID = bond.PRD_ID ");
			sql.append("left join TBPRD_BONDINFO_REVIEW rw on rw.PRD_ID = info.PRD_ID and rw.REVIEW_STATUS = 'W' ");
			sql.append("WHERE NVL(info.REVIEW_STATUS, 'Y') = 'Y' ");
			sql.append("AND info.PRD_ID NOT IN (SELECT PRD_ID FROM TBPRD_BONDINFO_REVIEW WHERE REVIEW_STATUS = 'W') ");
		} else {
			sql.append("SELECT rw.STOCK_BOND_TYPE, rw.SEQ,rw.PRD_ID,bond.BOND_CNAME_A,rw.ISIN_CODE,rw.BOND_PRIORITY,rw.CREDIT_RATING_SP,rw.CREDIT_RATING_MODDY,rw.CREDIT_RATING_FITCH,rw.BOND_CREDIT_RATING_SP,rw.BOND_CREDIT_RATING_MODDY,rw.BOND_CREDIT_RATING_FITCH,rw.ISSUER_BUYBACK,rw.RISK_CHECKLIST,rw.CNR_YIELD,rw.CNR_MULTIPLE,rw.MULTIPLE_SDATE,rw.MULTIPLE_EDATE,rw.ACT_TYPE,rw.REVIEW_STATUS,null as CREATOR, rw.PROJECT, rw.CUSTOMER_LEVEL ");
			sql.append("FROM TBPRD_BONDINFO_REVIEW rw left join TBPRD_BOND bond on rw.PRD_ID = bond.PRD_ID ");
			sql.append("WHERE rw.REVIEW_STATUS = 'W' ");
			sql.append("AND rw.CREATOR = :creator ");
			sql.append("UNION ");
			sql.append("SELECT info.STOCK_BOND_TYPE, null as SEQ,info.PRD_ID,bond.BOND_CNAME_A,info.ISIN_CODE,info.BOND_PRIORITY,info.CREDIT_RATING_SP,info.CREDIT_RATING_MODDY,info.CREDIT_RATING_FITCH,info.BOND_CREDIT_RATING_SP,info.BOND_CREDIT_RATING_MODDY,info.BOND_CREDIT_RATING_FITCH,info.ISSUER_BUYBACK,info.RISK_CHECKLIST,info.CNR_YIELD,info.CNR_MULTIPLE,info.MULTIPLE_SDATE,info.MULTIPLE_EDATE,rw.ACT_TYPE,rw.REVIEW_STATUS,null as CREATOR, bond.PROJECT, bond.CUSTOMER_LEVEL ");
			sql.append("FROM TBPRD_BONDINFO info left join TBPRD_BOND bond on info.PRD_ID = bond.PRD_ID ");
			sql.append("left join TBPRD_BONDINFO_REVIEW rw on rw.PRD_ID = info.PRD_ID and rw.REVIEW_STATUS = 'W' ");
			sql.append("WHERE NVL(info.REVIEW_STATUS, 'Y') = 'Y' ");
			sql.append("AND info.PRD_ID NOT IN (SELECT PRD_ID FROM TBPRD_BONDINFO_REVIEW WHERE REVIEW_STATUS = 'W' AND CREATOR = :creator) ");
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
		PRD250InputVO inputVO = (PRD250InputVO) body;
		PRD250OutputVO return_VO = new PRD250OutputVO();
		dam = this.getDataAccessManager();

		// update
		if(StringUtils.equals("Y", inputVO.getStatus())) {
			// TBPRD_BOND
			QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sql = new StringBuffer();
			sql.append("SELECT PRD_ID,BOND_CNAME_A FROM TBPRD_BOND where PRD_ID = :id ");
			queryCondition.setObject("id", inputVO.getPrd_id());
			queryCondition.setQueryString(sql.toString());
			List<Map<String, Object>> list = dam.exeQuery(queryCondition);
			if (list.size() > 0) {
				return_VO.setBond_name(ObjectUtils.toString(list.get(0).get("BOND_CNAME_A")));
				return_VO.setCanEdit(true);
			} else {
				return_VO.setCanEdit(false);
				return_VO.setErrorMsg("ehl_01_common_026");
			}

			// TBPRD_BONDINFO_REVIEW
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sql = new StringBuffer();
			sql.append("SELECT PRD_ID FROM TBPRD_BONDINFO_REVIEW where PRD_ID = :id and REVIEW_STATUS = 'W' ");
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
			// TBPRD_BOND 有限制可申購
			QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sql = new StringBuffer();
//			sql.append("SELECT PRD_ID, BOND_CNAME_A, RISKCATE_ID FROM TBPRD_BOND where PRD_ID = :id and IS_SALE = 'Y' and SYSDATE <= DATE_OF_MATURITY");
			sql.append(" SELECT PRD.PRD_ID, PRD.BOND_CNAME_A, PRD.RISKCATE_ID, INFO.STOCK_BOND_TYPE ");
			sql.append(" FROM TBPRD_BOND PRD LEFT JOIN TBPRD_BONDINFO INFO ON PRD.PRD_ID = INFO.PRD_ID ");
			sql.append(" where PRD.PRD_ID = :id and PRD.IS_SALE = 'Y' and SYSDATE <= PRD.DATE_OF_MATURITY ");
			queryCondition.setObject("id", inputVO.getPrd_id());
			queryCondition.setQueryString(sql.toString());
			List<Map<String, Object>> list = dam.exeQuery(queryCondition);
			if (list.size() > 0) {
				if (StringUtils.isBlank(ObjectUtils.toString(list.get(0).get("STOCK_BOND_TYPE")))) {
					return_VO.setCanEdit(false);
					return_VO.setErrorMsg(inputVO.getPrd_id() + "：該商品尚未維護股債類型，故不可設為主推商品。");
				}
				return_VO.setBond_name(ObjectUtils.toString(list.get(0).get("BOND_CNAME_A")));
				return_VO.setRick_id(ObjectUtils.toString(list.get(0).get("RISKCATE_ID")));
				return_VO.setCanEdit(true);
			} else
				return_VO.setCanEdit(false);
		}
		// add
		else {
			// TBPRD_BOND
			QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sql = new StringBuffer();
			sql.append("SELECT PRD_ID,BOND_CNAME_A FROM TBPRD_BOND where PRD_ID = :id ");
			queryCondition.setObject("id", inputVO.getPrd_id());
			queryCondition.setQueryString(sql.toString());
			List<Map<String, Object>> list = dam.exeQuery(queryCondition);
			if (list.size() > 0) {
				return_VO.setBond_name(ObjectUtils.toString(list.get(0).get("BOND_CNAME_A")));
				return_VO.setCanEdit(true);
			} else {
				return_VO.setCanEdit(false);
				return_VO.setErrorMsg("ehl_01_common_026");
			}
			// TBPRD_BONDINFO
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sql = new StringBuffer();
			sql.append("SELECT PRD_ID FROM TBPRD_BONDINFO where PRD_ID = :id ");
			queryCondition.setObject("id", inputVO.getPrd_id());
			queryCondition.setQueryString(sql.toString());
			List<Map<String, Object>> list2 = dam.exeQuery(queryCondition);
			if (list2.size() > 0) {
				return_VO.setCanEdit(false);
				return_VO.setErrorMsg("ehl_01_common_027");
			}
			// TBPRD_BONDINFO_REVIEW
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sql = new StringBuffer();
			sql.append("SELECT PRD_ID FROM TBPRD_BONDINFO_REVIEW where PRD_ID = :id and REVIEW_STATUS = 'W' ");
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

	private void addReviewStockBondType (String prd_id, String stock_bond_type, TBPRD_BONDINFOVO infoVO) throws JBranchException {
		List<Map<String,Object>> resultList = new ArrayList<Map<String,Object>>();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();

		sql.append("SELECT PRD_ID FROM TBPRD_BONDINFO_REVIEW where PRD_ID = :prd_id AND REVIEW_STATUS = 'W' ");

		queryCondition.setObject("prd_id", prd_id);
		queryCondition.setQueryString(sql.toString());
		resultList = dam.exeQuery(queryCondition);

		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		if(resultList.size() > 0) {
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sql = new StringBuffer();
			sql.append("UPDATE TBPRD_BONDINFO_REVIEW SET STOCK_BOND_TYPE = :stock_bond_type ");
			sql.append("WHERE PRD_ID = :prd_id AND REVIEW_STATUS = 'W' ");
			if("$".equals(stock_bond_type)) {
				queryCondition.setObject("stock_bond_type", infoVO != null ? infoVO.getSTOCK_BOND_TYPE() : null);
			} else {
				queryCondition.setObject("stock_bond_type", stock_bond_type);
			}
			queryCondition.setObject("prd_id", prd_id);
			queryCondition.setQueryString(sql.toString());
			dam.exeUpdate(queryCondition);
		}
	}

	private void addStockBondType (String prd_id, String stock_bond_type) throws JBranchException {
		List<Map<String,Object>> resultList = new ArrayList<Map<String,Object>>();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();

		sql.append("SELECT PRD_ID FROM TBPRD_BONDINFO where PRD_ID = :prd_id ");

		queryCondition.setObject("prd_id", prd_id);
		queryCondition.setQueryString(sql.toString());
		resultList = dam.exeQuery(queryCondition);

		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		if(resultList.size() > 0) {
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sql = new StringBuffer();
			sql.append("UPDATE TBPRD_BONDINFO SET STOCK_BOND_TYPE = :stock_bond_type WHERE PRD_ID = :prd_id ");

			queryCondition.setObject("stock_bond_type", stock_bond_type);
			queryCondition.setObject("prd_id", prd_id);
			queryCondition.setQueryString(sql.toString());
			dam.exeUpdate(queryCondition);
		}
	}

	public void addData(Object body, IPrimitiveMap header) throws JBranchException {
		PRD250InputVO inputVO = (PRD250InputVO) body;
		dam = this.getDataAccessManager();

		// check again
		// TBPRD_BOND
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT PRD_ID FROM TBPRD_BOND where PRD_ID = :id ");
		queryCondition.setObject("id", inputVO.getPrd_id());
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		if (list.size() == 0)
			throw new APException("ehl_01_common_026");
		// TBPRD_BONDINFO
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sql = new StringBuffer();
		sql.append("SELECT PRD_ID FROM TBPRD_BONDINFO where PRD_ID = :id ");
		queryCondition.setObject("id", inputVO.getPrd_id());
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list2 = dam.exeQuery(queryCondition);
		if (list2.size() > 0)
			throw new APException("ehl_01_common_027");
		// TBPRD_BONDINFO_REVIEW
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sql = new StringBuffer();
		sql.append("SELECT PRD_ID FROM TBPRD_BONDINFO_REVIEW where PRD_ID = :id and REVIEW_STATUS = 'W' ");
		queryCondition.setObject("id", inputVO.getPrd_id());
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list3 = dam.exeQuery(queryCondition);
		if (list3.size() > 0)
			throw new APException("ehl_01_common_028");

		// seq
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sql = new StringBuffer();
		sql.append("SELECT SQ_TBPRD_BONDINFO_REVIEW.nextval AS SEQ FROM DUAL ");
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> SEQLIST = dam.exeQuery(queryCondition);
		BigDecimal seqNo = (BigDecimal) SEQLIST.get(0).get("SEQ");
		// add TBPRD_BONDINFO_REVIEW
		TBPRD_BONDINFO_REVIEWVO vo = new TBPRD_BONDINFO_REVIEWVO();
		vo.setSEQ(seqNo);
		vo.setPRD_ID(inputVO.getPrd_id());
		vo.setISIN_CODE(inputVO.getIsin_code());
		vo.setBOND_PRIORITY(inputVO.getBond_pri());
		vo.setCREDIT_RATING_SP(inputVO.getRating_sp());
		vo.setCREDIT_RATING_MODDY(inputVO.getRating_moddy());
		vo.setCREDIT_RATING_FITCH(inputVO.getRating_fitch());
		vo.setBOND_CREDIT_RATING_SP(inputVO.getBond_rating_sp());
		vo.setBOND_CREDIT_RATING_MODDY(inputVO.getBond_rating_moddy());
		vo.setBOND_CREDIT_RATING_FITCH(inputVO.getBond_rating_fitch());
		vo.setISSUER_BUYBACK(inputVO.getBuyback());
		vo.setRISK_CHECKLIST(inputVO.getChecklist());
		// 2017/5/24 mark
//		if(StringUtils.isNotBlank(inputVO.getYield()))
//			vo.setCNR_YIELD(new BigDecimal(inputVO.getYield()));
		// 2017/4/11 CNR加減碼、加碼區間起日、加碼區間迄日 以前要又不要先註解
//		if(StringUtils.isNotBlank(inputVO.getMultiple()))
//			vo.setCNR_MULTIPLE(new BigDecimal(inputVO.getMultiple()));
//		if(inputVO.getMulti_sDate() != null)
//			vo.setMULTIPLE_SDATE(new Timestamp(inputVO.getMulti_sDate().getTime()));
//		if(inputVO.getMulti_eDate() != null)
//			vo.setMULTIPLE_EDATE(new Timestamp(inputVO.getMulti_eDate().getTime()));

		vo.setPROJECT(inputVO.getBondProject());
		vo.setCUSTOMER_LEVEL(inputVO.getBondCustLevel());
		vo.setACT_TYPE("A");
		vo.setREVIEW_STATUS("W");
		dam.create(vo);
		this.sendRtnObject(null);
	}

	public void editData(Object body, IPrimitiveMap header) throws JBranchException {
		PRD250InputVO inputVO = (PRD250InputVO) body;
		dam = this.getDataAccessManager();

		// check again
		// TBPRD_BONDINFO_REVIEW
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT PRD_ID FROM TBPRD_BONDINFO_REVIEW where PRD_ID = :id and REVIEW_STATUS = 'W' ");
		queryCondition.setObject("id", inputVO.getPrd_id());
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		if (list.size() > 0)
			throw new APException("ehl_01_common_028");

		// seq
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sql = new StringBuffer();
		sql.append("SELECT SQ_TBPRD_BONDINFO_REVIEW.nextval AS SEQ FROM DUAL ");
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> SEQLIST = dam.exeQuery(queryCondition);
		BigDecimal seqNo = (BigDecimal) SEQLIST.get(0).get("SEQ");
		// add TBPRD_BONDINFO_REVIEW
		TBPRD_BONDINFO_REVIEWVO vo = new TBPRD_BONDINFO_REVIEWVO();
		vo.setSEQ(seqNo);
		vo.setPRD_ID(inputVO.getPrd_id());
		vo.setISIN_CODE(inputVO.getIsin_code());
		vo.setBOND_PRIORITY(inputVO.getBond_pri());
		vo.setCREDIT_RATING_SP(inputVO.getRating_sp());
		vo.setCREDIT_RATING_MODDY(inputVO.getRating_moddy());
		vo.setCREDIT_RATING_FITCH(inputVO.getRating_fitch());
		vo.setBOND_CREDIT_RATING_SP(inputVO.getBond_rating_sp());
		vo.setBOND_CREDIT_RATING_MODDY(inputVO.getBond_rating_moddy());
		vo.setBOND_CREDIT_RATING_FITCH(inputVO.getBond_rating_fitch());
		vo.setISSUER_BUYBACK(inputVO.getBuyback());
		vo.setRISK_CHECKLIST(inputVO.getChecklist());
//		if(StringUtils.isNotBlank(inputVO.getYield()))
//			vo.setCNR_YIELD(new BigDecimal(inputVO.getYield()));
//		else
//			vo.setCNR_YIELD(null);
//		if(StringUtils.isNotBlank(inputVO.getMultiple()))
//			vo.setCNR_MULTIPLE(new BigDecimal(inputVO.getMultiple()));
//		else
//			vo.setCNR_MULTIPLE(null);
//		if(inputVO.getMulti_sDate() != null)
//			vo.setMULTIPLE_SDATE(new Timestamp(inputVO.getMulti_sDate().getTime()));
//		else
//			vo.setMULTIPLE_SDATE(null);
//		if(inputVO.getMulti_eDate() != null)
//			vo.setMULTIPLE_EDATE(new Timestamp(inputVO.getMulti_eDate().getTime()));
//		else
//			vo.setMULTIPLE_EDATE(null);

		vo.setPROJECT(inputVO.getBondProject());
		vo.setCUSTOMER_LEVEL(inputVO.getBondCustLevel());

		vo.setACT_TYPE("M");
		vo.setREVIEW_STATUS("W");
		dam.create(vo);

		if(StringUtils.isNotBlank(inputVO.getStock_bond_type()))
			addReviewStockBondType(inputVO.getPrd_id(), inputVO.getStock_bond_type());

		this.sendRtnObject(null);
	}

	public void deleteData(Object body, IPrimitiveMap header) throws JBranchException {
		PRD250InputVO inputVO = (PRD250InputVO) body;
		dam = this.getDataAccessManager();
		String prd_id = inputVO.getPrd_id();
		// check again
		// TBPRD_BONDINFO_REVIEW
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT PRD_ID FROM TBPRD_BONDINFO_REVIEW where PRD_ID = :id and REVIEW_STATUS = 'W' ");
		queryCondition.setObject("id", prd_id);
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		if (list.size() > 0)
			throw new APException("ehl_01_common_028");

		// seq
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sql = new StringBuffer();
		sql.append("SELECT SQ_TBPRD_BONDINFO_REVIEW.nextval AS SEQ FROM DUAL ");
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> SEQLIST = dam.exeQuery(queryCondition);
		BigDecimal seqNo = (BigDecimal) SEQLIST.get(0).get("SEQ");
		// get ori
		TBPRD_BONDINFOVO vo = new TBPRD_BONDINFOVO();
		vo = (TBPRD_BONDINFOVO) dam.findByPKey(TBPRD_BONDINFOVO.TABLE_UID, prd_id);
		if (vo != null) {
			// add TBPRD_BONDINFO_REVIEW
			TBPRD_BONDINFO_REVIEWVO rvo = new TBPRD_BONDINFO_REVIEWVO();
			rvo.setSEQ(seqNo);
			rvo.setPRD_ID(vo.getPRD_ID());
			rvo.setISIN_CODE(vo.getISIN_CODE());
			rvo.setBOND_PRIORITY(vo.getBOND_PRIORITY());
			rvo.setCREDIT_RATING_SP(vo.getCREDIT_RATING_SP());
			rvo.setCREDIT_RATING_MODDY(vo.getCREDIT_RATING_MODDY());
			rvo.setCREDIT_RATING_FITCH(vo.getCREDIT_RATING_FITCH());
			rvo.setBOND_CREDIT_RATING_SP(vo.getBOND_CREDIT_RATING_SP());
			rvo.setBOND_CREDIT_RATING_MODDY(vo.getBOND_CREDIT_RATING_MODDY());
			rvo.setBOND_CREDIT_RATING_FITCH(vo.getBOND_CREDIT_RATING_FITCH());
			rvo.setISSUER_BUYBACK(vo.getISSUER_BUYBACK());
			rvo.setRISK_CHECKLIST(vo.getRISK_CHECKLIST());
//			rvo.setCNR_YIELD(vo.getCNR_YIELD());
//			rvo.setCNR_MULTIPLE(vo.getCNR_MULTIPLE());
//			rvo.setMULTIPLE_SDATE(vo.getMULTIPLE_SDATE());
//			rvo.setMULTIPLE_EDATE(vo.getMULTIPLE_EDATE());
			rvo.setACT_TYPE("D");
			rvo.setREVIEW_STATUS("W");
			dam.create(rvo);

			list = new ArrayList<Map<String,Object>>();
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

			StringBuffer sb = new StringBuffer();
			sb.append("select STOCK_BOND_TYPE from TBPRD_BONDINFO where PRD_ID = :prd_id ");

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
		PRD250InputVO inputVO = (PRD250InputVO) body;
		dam = this.getDataAccessManager();

		// 2017/2/21
		for(Map<String, Object> rmap : inputVO.getReview_list()) {
			TBPRD_BONDINFO_REVIEWVO rvo = new TBPRD_BONDINFO_REVIEWVO();
			BigDecimal seq = new BigDecimal(ObjectUtils.toString(rmap.get("SEQ")));
			rvo = (TBPRD_BONDINFO_REVIEWVO) dam.findByPKey(TBPRD_BONDINFO_REVIEWVO.TABLE_UID, seq);
			if (rvo != null) {
				// confirm
				if("Y".equals(inputVO.getStatus())) {
					// 新增
					if("A".equals(rvo.getACT_TYPE())) {
						// TBPRD_BONDINFO
						TBPRD_BONDINFOVO vo = new TBPRD_BONDINFOVO();
						vo.setPRD_ID(rvo.getPRD_ID());
						vo.setISIN_CODE(rvo.getISIN_CODE());
						vo.setBOND_PRIORITY(rvo.getBOND_PRIORITY());
						vo.setCREDIT_RATING_SP(rvo.getCREDIT_RATING_SP());
						vo.setCREDIT_RATING_MODDY(rvo.getCREDIT_RATING_MODDY());
						vo.setCREDIT_RATING_FITCH(rvo.getCREDIT_RATING_FITCH());
						vo.setBOND_CREDIT_RATING_SP(rvo.getBOND_CREDIT_RATING_SP());
						vo.setBOND_CREDIT_RATING_MODDY(rvo.getBOND_CREDIT_RATING_MODDY());
						vo.setBOND_CREDIT_RATING_FITCH(rvo.getBOND_CREDIT_RATING_FITCH());
						vo.setISSUER_BUYBACK(rvo.getISSUER_BUYBACK());
						vo.setRISK_CHECKLIST(rvo.getRISK_CHECKLIST());
//						vo.setCNR_YIELD(rvo.getCNR_YIELD());
//						vo.setCNR_MULTIPLE(rvo.getCNR_MULTIPLE());
//						vo.setMULTIPLE_SDATE(rvo.getMULTIPLE_SDATE());
//						vo.setMULTIPLE_EDATE(rvo.getMULTIPLE_EDATE());
						vo.setACT_TYPE("A");
						vo.setREVIEW_STATUS("Y");
						dam.create(vo);

						TBPRD_BONDVO bondVO = new TBPRD_BONDVO();
						bondVO.setPRD_ID(rvo.getPRD_ID());
						bondVO.setPROJECT(rvo.getPROJECT());
						bondVO.setCUSTOMER_LEVEL(rvo.getCUSTOMER_LEVEL());
						dam.create(bondVO);
					}
					// 修改
					else if("M".equals(rvo.getACT_TYPE())) {
						TBPRD_BONDINFOVO vo = new TBPRD_BONDINFOVO();
						vo = (TBPRD_BONDINFOVO) dam.findByPKey(TBPRD_BONDINFOVO.TABLE_UID, rvo.getPRD_ID());
						TBPRD_BONDVO bondVO = (TBPRD_BONDVO) dam.findByPKey(TBPRD_BONDVO.TABLE_UID, rvo.getPRD_ID());

						if (vo != null && bondVO != null) {
							vo.setISIN_CODE(rvo.getISIN_CODE());
							vo.setBOND_PRIORITY(rvo.getBOND_PRIORITY());
							vo.setCREDIT_RATING_SP(rvo.getCREDIT_RATING_SP());
							vo.setCREDIT_RATING_MODDY(rvo.getCREDIT_RATING_MODDY());
							vo.setCREDIT_RATING_FITCH(rvo.getCREDIT_RATING_FITCH());
							vo.setBOND_CREDIT_RATING_SP(rvo.getBOND_CREDIT_RATING_SP());
							vo.setBOND_CREDIT_RATING_MODDY(rvo.getBOND_CREDIT_RATING_MODDY());
							vo.setBOND_CREDIT_RATING_FITCH(rvo.getBOND_CREDIT_RATING_FITCH());
							vo.setISSUER_BUYBACK(rvo.getISSUER_BUYBACK());
							vo.setRISK_CHECKLIST(rvo.getRISK_CHECKLIST());
//							vo.setCNR_YIELD(rvo.getCNR_YIELD());
//							vo.setCNR_MULTIPLE(rvo.getCNR_MULTIPLE());
//							vo.setMULTIPLE_SDATE(rvo.getMULTIPLE_SDATE());
//							vo.setMULTIPLE_EDATE(rvo.getMULTIPLE_EDATE());
							vo.setACT_TYPE("M");
							vo.setREVIEW_STATUS("Y");
							dam.update(vo);

							bondVO.setPROJECT(rvo.getPROJECT());
							bondVO.setCUSTOMER_LEVEL(rvo.getCUSTOMER_LEVEL());
							dam.update(bondVO);
						} else {
							// 顯示資料不存在
							throw new APException("ehl_01_common_001");
						}
					}
					// 刪除
					else if("D".equals(rvo.getACT_TYPE())) {
						TBPRD_BONDINFOVO vo = new TBPRD_BONDINFOVO();
						vo = (TBPRD_BONDINFOVO) dam.findByPKey(TBPRD_BONDINFOVO.TABLE_UID, rvo.getPRD_ID());
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
						sb.append("select STOCK_BOND_TYPE from TBPRD_BONDINFO_REVIEW where SEQ = :seq ");

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
		PRD250InputVO inputVO = (PRD250InputVO) body;
		PRD250OutputVO return_VO = new PRD250OutputVO();
		dam = this.getDataAccessManager();

		// getRoleList
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT COUNT(ROLEID) AS COUNTS FROM TBSYSSECUROLPRIASS ");
		sql.append("WHERE PRIVILEGEID IN (SELECT PRIVILEGEID FROM TBSYSSECUPRIFUNMAP WHERE ITEMID = 'PRD250' AND FUNCTIONID = 'confirm') AND ROLEID = :roleID ");
		queryCondition.setObject("roleID", getUserVariable(FubonSystemVariableConsts.LOGINROLE));
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> privilegeCount = dam.exeQuery(queryCondition);
		//
		sql = new StringBuffer();
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sql.append("SELECT STOCK_BOND_TYPE, PRD_ID,BOND_CNAME_A,ISIN_CODE,BOND_PRIORITY,CREDIT_RATING_SP,CREDIT_RATING_MODDY,CREDIT_RATING_FITCH,BOND_CREDIT_RATING_SP,BOND_CREDIT_RATING_MODDY,BOND_CREDIT_RATING_FITCH,ISSUER_BUYBACK,RISK_CHECKLIST,CNR_YIELD,CNR_MULTIPLE,MULTIPLE_SDATE,MULTIPLE_EDATE,ACT_TYPE,REVIEW_STATUS, CREATOR FROM ( ");
		// 覆核人員
		if (((BigDecimal) privilegeCount.get(0).get("COUNTS")).compareTo(new BigDecimal(0)) > 0) {
			sql.append("SELECT rw.STOCK_BOND_TYPE, rw.PRD_ID,bond.BOND_CNAME_A,rw.ISIN_CODE,rw.BOND_PRIORITY,rw.CREDIT_RATING_SP,rw.CREDIT_RATING_MODDY,rw.CREDIT_RATING_FITCH,rw.BOND_CREDIT_RATING_SP,rw.BOND_CREDIT_RATING_MODDY,rw.BOND_CREDIT_RATING_FITCH,rw.ISSUER_BUYBACK,rw.RISK_CHECKLIST,rw.CNR_YIELD,rw.CNR_MULTIPLE,rw.MULTIPLE_SDATE,rw.MULTIPLE_EDATE,rw.ACT_TYPE,rw.REVIEW_STATUS,rw.CREATOR ");
			sql.append("FROM TBPRD_BONDINFO_REVIEW rw left join TBPRD_BOND bond on rw.PRD_ID = bond.PRD_ID ");
			sql.append("WHERE rw.REVIEW_STATUS = 'W' ");
			sql.append("UNION ");
			sql.append("SELECT info.STOCK_BOND_TYPE, info.PRD_ID,bond.BOND_CNAME_A,info.ISIN_CODE,info.BOND_PRIORITY,info.CREDIT_RATING_SP,info.CREDIT_RATING_MODDY,info.CREDIT_RATING_FITCH,info.BOND_CREDIT_RATING_SP,info.BOND_CREDIT_RATING_MODDY,info.BOND_CREDIT_RATING_FITCH,info.ISSUER_BUYBACK,info.RISK_CHECKLIST,info.CNR_YIELD,info.CNR_MULTIPLE,info.MULTIPLE_SDATE,info.MULTIPLE_EDATE,rw.ACT_TYPE,rw.REVIEW_STATUS,null as CREATOR ");
			sql.append("FROM TBPRD_BONDINFO info left join TBPRD_BOND bond on info.PRD_ID = bond.PRD_ID ");
			sql.append("left join TBPRD_BONDINFO_REVIEW rw on rw.PRD_ID = info.PRD_ID and rw.REVIEW_STATUS = 'W' ");
			sql.append("WHERE NVL(info.REVIEW_STATUS, 'Y') = 'Y' ");
			sql.append("AND info.PRD_ID NOT IN (SELECT PRD_ID FROM TBPRD_BONDINFO_REVIEW WHERE REVIEW_STATUS = 'W') ");
		} else {
			sql.append("SELECT rw.STOCK_BOND_TYPE, rw.PRD_ID,bond.BOND_CNAME_A,rw.ISIN_CODE,rw.BOND_PRIORITY,rw.CREDIT_RATING_SP,rw.CREDIT_RATING_MODDY,rw.CREDIT_RATING_FITCH,rw.BOND_CREDIT_RATING_SP,rw.BOND_CREDIT_RATING_MODDY,rw.BOND_CREDIT_RATING_FITCH,rw.ISSUER_BUYBACK,rw.RISK_CHECKLIST,rw.CNR_YIELD,rw.CNR_MULTIPLE,rw.MULTIPLE_SDATE,rw.MULTIPLE_EDATE,rw.ACT_TYPE,rw.REVIEW_STATUS,null as CREATOR ");
			sql.append("FROM TBPRD_BONDINFO_REVIEW rw left join TBPRD_BOND bond on rw.PRD_ID = bond.PRD_ID ");
			sql.append("WHERE rw.REVIEW_STATUS = 'W' ");
			sql.append("AND rw.CREATOR = :creator ");
			sql.append("UNION ");
			sql.append("SELECT info.STOCK_BOND_TYPE, info.PRD_ID,bond.BOND_CNAME_A,info.ISIN_CODE,info.BOND_PRIORITY,info.CREDIT_RATING_SP,info.CREDIT_RATING_MODDY,info.CREDIT_RATING_FITCH,info.BOND_CREDIT_RATING_SP,info.BOND_CREDIT_RATING_MODDY,info.BOND_CREDIT_RATING_FITCH,info.ISSUER_BUYBACK,info.RISK_CHECKLIST,info.CNR_YIELD,info.CNR_MULTIPLE,info.MULTIPLE_SDATE,info.MULTIPLE_EDATE,rw.ACT_TYPE,rw.REVIEW_STATUS,null as CREATOR ");
			sql.append("FROM TBPRD_BONDINFO info left join TBPRD_BOND bond on info.PRD_ID = bond.PRD_ID ");
			sql.append("left join TBPRD_BONDINFO_REVIEW rw on rw.PRD_ID = info.PRD_ID and rw.REVIEW_STATUS = 'W' ");
			sql.append("WHERE NVL(info.REVIEW_STATUS, 'Y') = 'Y' ");
			sql.append("AND info.PRD_ID NOT IN (SELECT PRD_ID FROM TBPRD_BONDINFO_REVIEW WHERE REVIEW_STATUS = 'W' AND CREATOR = :creator) ");
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
			String fileName = "海外債清單_"+ sdf.format(new Date()) + "_" + ws.getUser().getUserID() + ".csv";
			List listCSV = new ArrayList();
			for (Map<String, Object> map : list) {
				// 14 column
				String[] records = new String[15];
				int i = 0;
				if ("W".equals(ObjectUtils.toString(map.get("REVIEW_STATUS"))))
					records[i] = "覆核中";
				else
					records[i] = "已覆核";
				records[++i] = checkIsNull(map, "PRD_ID");
				records[++i] = checkIsNull(map, "BOND_CNAME_A");
				records[++i] = checkIsNull(map, "ISIN_CODE");
				records[++i] = checkIsNull(map, "BOND_PRIORITY");
				records[++i] = "標普：" + checkIsNull(map, "CREDIT_RATING_SP") + "\n穆迪：" + checkIsNull(map, "CREDIT_RATING_MODDY") + "\n惠譽：" + checkIsNull(map, "CREDIT_RATING_FITCH");
				records[++i] = "標普：" + checkIsNull(map, "BOND_CREDIT_RATING_SP") + "\n穆迪：" + checkIsNull(map, "BOND_CREDIT_RATING_MODDY") + "\n惠譽：" + checkIsNull(map, "BOND_CREDIT_RATING_FITCH");
				records[++i] = checkIsNull(map, "ISSUER_BUYBACK");
				records[++i] = "Y".equals(checkIsNull(map, "RISK_CHECKLIST")) ? "是" : "否";
//				records[++i] = checkIsNull(map, "CNR_YIELD");
//				records[++i] = checkIsNull(map, "CNR_MULTIPLE");
//				records[++i] = "=\"" + checkIsNull(map, "MULTIPLE_SDATE") + "\"";
//				records[++i] = "=\"" + checkIsNull(map, "MULTIPLE_EDATE") + "\"";
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

				listCSV.add(records);
			}
			// header
			String[] csvHeader = new String[15];
			int j = 0;
			csvHeader[j] = "覆核狀態";
			csvHeader[++j] = "海外債代碼";
			csvHeader[++j] = "海外債名稱";
			csvHeader[++j] = "ISIN CODE";
			csvHeader[++j] = "債券順位";
			csvHeader[++j] = "發行機構評等-標普/穆迪/惠譽";
			csvHeader[++j] = "債券評等-標普/穆迪/惠譽";
			csvHeader[++j] = "發行機構提前買回";
			csvHeader[++j] = "需填寫風險檢核表";
//			csvHeader[++j] = "CNR分配率";
//			csvHeader[++j] = "CNR加減碼";
//			csvHeader[++j] = "加碼區間起日";
//			csvHeader[++j] = "加碼區間迄日";
//			csvHeader[++j] = "狀態";
			csvHeader[++j] = "股債類型";

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
		PRD250InputVO inputVO = (PRD250InputVO) body;
		PRD250OutputVO return_VO = new PRD250OutputVO();
		XmlInfo xmlInfo = new XmlInfo();
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
			Map<String, String> conYN = xmlInfo.doGetVariable("COMMON.YES_NO", FormatHelper.FORMAT_3);
			Map<String, String> spDtl = xmlInfo.doGetVariable("PRD.CREDIT_RATING_SP_DTL", FormatHelper.FORMAT_3);
			for(int i = 0;i < dataCsv.size();i++) {
				String[] str = dataCsv.get(i);
				if (i == 0) {
					try {
						if (!"商品代碼".equals(str[0].trim()))
							throw new Exception(str[0]);
						else if (!"ISIN CODE".equals(str[1].trim()))
							throw new Exception(str[1]);
						else if (!"債券順位".equals(str[2].trim()))
							throw new Exception(str[2]);
						else if (!"發行機構評等-標普".equals(str[3].trim()))
							throw new Exception(str[3]);
						else if (!"發行機構評等-穆迪".equals(str[4].trim()))
							throw new Exception(str[4]);
						else if (!"發行機構評等-惠譽".equals(str[5].trim()))
							throw new Exception(str[5]);
						else if (!"債券評等-標普".equals(str[6].trim()))
							throw new Exception(str[6]);
						else if (!"債券評等-穆迪".equals(str[7].trim()))
							throw new Exception(str[7]);
						else if (!"債券評等-惠譽".equals(str[8].trim()))
							throw new Exception(str[8]);
						else if (!"發行機構提前買回".equals(str[9].trim()))
							throw new Exception(str[9]);
						else if (!"需填寫風險檢核表".equals(str[10].trim()))
							throw new Exception(str[10]);
						else if (!"股債類型(S:股票型、B:債券型)".equals(str[11].trim()))
							throw new Exception(str[11]);
						else if (!"專案代碼".equals(str[12].trim()))
							throw new Exception(str[12]);
						else if (!"客群代碼".equals(str[13].trim()))
							throw new Exception(str[13]);
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
				idList.add(str[0].trim());
				// TBPRD_BOND
				QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				StringBuffer sql = new StringBuffer();
				sql.append("SELECT PRD_ID FROM TBPRD_BOND where PRD_ID = :id ");
				queryCondition.setObject("id", str[0].trim());
				queryCondition.setQueryString(sql.toString());
				List<Map<String, Object>> list = dam.exeQuery(queryCondition);
				if (list.size() == 0) {
					error.add(str[0]);
					continue;
				}

				// TBPRD_BONDINFO check edit
				Boolean exist = false;
				queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sql = new StringBuffer();
				sql.append("SELECT PRD_ID FROM TBPRD_BONDINFO where PRD_ID = :id ");
				queryCondition.setObject("id", str[0].trim());
				queryCondition.setQueryString(sql.toString());
				List<Map<String, Object>> list2 = dam.exeQuery(queryCondition);
				if (list2.size() > 0)
					exist = true;
				// TBPRD_BONDINFO_REVIEW
				queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sql = new StringBuffer();
				sql.append("SELECT PRD_ID FROM TBPRD_BONDINFO_REVIEW where PRD_ID = :id and REVIEW_STATUS = 'W' ");
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
				sql.append("SELECT SQ_TBPRD_BONDINFO_REVIEW.nextval AS SEQ FROM DUAL ");
				queryCondition.setQueryString(sql.toString());
				List<Map<String, Object>> SEQLIST = dam.exeQuery(queryCondition);
				BigDecimal seqNo = (BigDecimal) SEQLIST.get(0).get("SEQ");
				// add TBPRD_BONDINFO_REVIEW
				TBPRD_BONDINFO_REVIEWVO vo = new TBPRD_BONDINFO_REVIEWVO();
				TBPRD_BONDINFOVO info_vo = new TBPRD_BONDINFOVO();
				info_vo = (TBPRD_BONDINFOVO) dam.findByPKey(TBPRD_BONDINFOVO.TABLE_UID, str[0].trim());
				TBPRD_BONDVO bondVO = (TBPRD_BONDVO) dam.findByPKey(TBPRD_BONDVO.TABLE_UID, str[0].trim());

				vo.setSEQ(seqNo);
				//
				if (utf_8_length(str[0]) > 16) {
					error3.add(str[0]);
					continue;
				} else
					vo.setPRD_ID(str[0].trim());
				//
				if (StringUtils.isNotBlank(str[1]) && !StringUtils.equals(str[1], "$")) {
					if (utf_8_length(str[1]) > 128) {
						error3.add(str[0] + ":" + str[1]);
						continue;
					}
					vo.setISIN_CODE(str[1]);
				} else
					vo.setISIN_CODE(info_vo != null ? info_vo.getISIN_CODE() : null);

				if (StringUtils.isNotBlank(str[2]) && !StringUtils.equals(str[2], "$")) {
					if (utf_8_length(str[2]) > 40) {
						error3.add(str[0] + ":" + str[2]);
						continue;
					}
					vo.setBOND_PRIORITY(str[2]);
				} else
					vo.setBOND_PRIORITY(info_vo != null ? info_vo.getBOND_PRIORITY() : null);

				if(StringUtils.isNotBlank(str[3]) && !StringUtils.equals(str[3], "$")) {
					if(StringUtils.isBlank(spDtl.get(str[3].trim()))) {
						error3.add(str[0]+":"+str[3]);
						continue;
					}
					vo.setCREDIT_RATING_SP(str[3]);
				} else
					vo.setCREDIT_RATING_SP(info_vo != null ? info_vo.getCREDIT_RATING_SP() : null);
//					vo.setCREDIT_RATING_SP(str[3]);
				//
				if (StringUtils.isNotBlank(str[4]) && !StringUtils.equals(str[4], "$")) {
					if (utf_8_length(str[4]) > 10) {
						error3.add(str[0] + ":" + str[4]);
						continue;
					}
					vo.setCREDIT_RATING_MODDY(str[4]);
				} else
					vo.setCREDIT_RATING_MODDY(info_vo != null ? info_vo.getCREDIT_RATING_MODDY() : null);

				if (StringUtils.isNotBlank(str[5]) && !StringUtils.equals(str[5], "$")) {
					if (utf_8_length(str[5]) > 10) {
						error3.add(str[0] + ":" + str[5]);
						continue;
					}
					vo.setCREDIT_RATING_FITCH(str[5]);
				} else
					vo.setCREDIT_RATING_FITCH(info_vo != null ? info_vo.getCREDIT_RATING_FITCH() : null);

				if(StringUtils.isNotBlank(str[6]) && !StringUtils.equals(str[6], "$")) {
					if(StringUtils.isBlank(spDtl.get(str[6].trim()))) {
						error3.add(str[0]+":"+str[6]);
						continue;
					}
					vo.setBOND_CREDIT_RATING_SP(str[6]);
				} else
					vo.setBOND_CREDIT_RATING_SP(info_vo != null ? info_vo.getBOND_CREDIT_RATING_SP() : null);

				if (StringUtils.isNotBlank(str[7]) && !StringUtils.equals(str[7], "$")) {
					if (utf_8_length(str[7]) > 10) {
						error3.add(str[0] + ":" + str[7]);
						continue;
					}
					vo.setBOND_CREDIT_RATING_MODDY(str[7]);
				} else
					vo.setBOND_CREDIT_RATING_MODDY(info_vo != null ? info_vo.getBOND_CREDIT_RATING_MODDY() : null);

				if (StringUtils.isNotBlank(str[8]) && !StringUtils.equals(str[8], "$")) {
					if (utf_8_length(str[8]) > 10) {
						error3.add(str[0] + ":" + str[8]);
						continue;
					}
					vo.setBOND_CREDIT_RATING_FITCH(str[8]);
				} else
					vo.setBOND_CREDIT_RATING_FITCH(info_vo != null ? info_vo.getBOND_CREDIT_RATING_FITCH() : null);

				if (StringUtils.isNotBlank(str[9]) && !StringUtils.equals(str[9], "$")) {
					if (utf_8_length(str[9]) > 20) {
						error3.add(str[0] + ":" + str[9]);
						continue;
					}
					vo.setISSUER_BUYBACK(str[9]);
				} else
					vo.setISSUER_BUYBACK(info_vo != null ? info_vo.getISSUER_BUYBACK() : null);

				if(StringUtils.isNotBlank(str[10]) && !StringUtils.equals(str[10], "$")) {
					if(StringUtils.isBlank(conYN.get(str[10]))) {
						error3.add(str[0]+":"+str[10]);
						continue;
					}
					vo.setRISK_CHECKLIST(str[10]);
				} else
					vo.setRISK_CHECKLIST(info_vo != null ? info_vo.getRISK_CHECKLIST() : null);

				// 專案代碼
				if (StringUtils.isNotBlank(str[12])) {
					if (str[12].length() > 20) {
						error3.add(str[0] + ":" + str[12]);
						continue;
					}

					if (StringUtils.equals((str[12].trim()), "0")) {
						vo.setPROJECT(null);
					} else if (StringUtils.equals((str[12].trim()), "$")) {
						vo.setPROJECT(bondVO != null ? bondVO.getPROJECT() : null);
					} else {
						vo.setPROJECT(str[12].trim());
					}
				} else {
					vo.setPROJECT(bondVO != null ? bondVO.getPROJECT() : null);
				}
				// 客群代碼 
				if (StringUtils.isNotBlank(str[13])) {
					if (str[13].length() > 20) {
						error3.add(str[0] + ":" + str[13]);
						continue;
					}

					if (StringUtils.equals((str[13].trim()), "0")) {
						vo.setCUSTOMER_LEVEL(null);
					} else if (StringUtils.equals((str[13].trim()), "$")) {
						vo.setCUSTOMER_LEVEL(bondVO != null ? bondVO.getCUSTOMER_LEVEL() : null);
					} else {
						vo.setCUSTOMER_LEVEL(str[13].trim());
					}
				} else {
					vo.setCUSTOMER_LEVEL(bondVO != null ? bondVO.getCUSTOMER_LEVEL() : null);
				}

				if(!exist)
					vo.setACT_TYPE("A");
				else
					vo.setACT_TYPE("M");
				vo.setREVIEW_STATUS("W");
				dam.create(vo);

				if(StringUtils.isNotBlank(str[11])) {
					String stock_bond_type = str[11].trim().toUpperCase();
					if("S".equals(stock_bond_type) ||
							"B".equals(stock_bond_type) ||
							"$".equals(stock_bond_type)) {
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
		return_VO.setErrorList6(error6);
		this.sendRtnObject(return_VO);
	}

	public void downloadSimple(Object body, IPrimitiveMap header) throws Exception {
		this.notifyClientToDownloadFile("doc//PRD//PRD250_EXAMPLE.csv", "上傳指定商品代碼範例.csv");
	}

	public void tempAddData(Object body, IPrimitiveMap header) throws JBranchException {
		PRD250InputVO inputVO = (PRD250InputVO) body;
		PRD250OutputVO return_VO = new PRD250OutputVO();
		XmlInfo xmlInfo = new XmlInfo();
		dam = this.getDataAccessManager();
	}

	public void upload_inquire(Object body, IPrimitiveMap header) throws JBranchException {
		PRD250InputVO inputVO = (PRD250InputVO) body;
		PRD250OutputVO return_VO = new PRD250OutputVO();
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
		sql.append("inner join (select DOC_ID,DOC_NAME,DOC_TYPE from TBSYS_FILE_MAIN where SUBSYSTEM_TYPE = 'PRD' and DOC_TYPE = '02' ");
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
		sql.append("inner join (select DOC_ID,DOC_NAME,DOC_TYPE from TBSYS_FILE_MAIN where SUBSYSTEM_TYPE = 'PRD' and DOC_TYPE = '02' ");
		sql.append(") b on a.DOC_ID = b.DOC_ID ");
		sql.append("inner join (select DOC_ID,DOC_FILE_TYPE,DOC_URL,DOC_START_DATE,DOC_DUE_DATE,FILE_NAME,CREATETIME from TBSYS_FILE_DETAIL where DOC_VERSION_STATUS = '2' and sysdate BETWEEN DOC_START_DATE and DOC_DUE_DATE) c on b.DOC_ID = c.DOC_ID ");
		queryCondition.setQueryString(sql.toString());
		list.addAll(dam.exeQuery(queryCondition));
		return_VO.setResultList(list);
		this.sendRtnObject(return_VO);
	}

	public void getRank(Object body, IPrimitiveMap header) throws JBranchException {
		PRD250InputVO inputVO = (PRD250InputVO) body;
		PRD250OutputVO return_VO = new PRD250OutputVO();
		dam = this.getDataAccessManager();

		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		queryCondition.setQueryString("select PRD_ID, BOND_CNAME_A, RISKCATE_ID, PRD_RANK, PRD_RANK_DATE FROM TBPRD_BOND WHERE PRD_RANK IS NOT NULL ORDER BY PRD_RANK");
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		return_VO.setResultList(list);

		// select
		if(inputVO.getDate() != null) {
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sql = new StringBuffer();
			sql.append("SELECT a.PRD_ID, b.BOND_CNAME_A, b.RISKCATE_ID, a.PRD_RANK FROM TBPRD_RANK a ");
			sql.append("LEFT JOIN TBPRD_BOND b on a.PRD_ID = b.PRD_ID ");
			sql.append("WHERE a.PRD_TYPE = 'BND' ");
			sql.append("AND a.EFFECT_DATE = :eff_date ");
			queryCondition.setObject("eff_date", inputVO.getDate());
			queryCondition.setQueryString(sql.toString());
			List<Map<String, Object>> list2 = dam.exeQuery(queryCondition);
			return_VO.setResultList2(list2);
		}
		// 最近的
		else {
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			queryCondition.setQueryString("select DISTINCT EFFECT_DATE FROM TBPRD_RANK WHERE PRD_TYPE = 'BND' AND EFFECT_DATE > trunc(sysdate) ORDER BY EFFECT_DATE");
			List<Map<String, Object>> list3 = dam.exeQuery(queryCondition);
			if(list3.size() > 0) {
				return_VO.setLastDate((Timestamp) list3.get(0).get("EFFECT_DATE"));

				queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				StringBuffer sql = new StringBuffer();
				sql.append("SELECT a.PRD_ID, b.BOND_CNAME_A, b.RISKCATE_ID, a.PRD_RANK FROM TBPRD_RANK a ");
				sql.append("LEFT JOIN TBPRD_BOND b on a.PRD_ID = b.PRD_ID ");
				sql.append("WHERE a.PRD_TYPE = 'BND' ");
				sql.append("AND a.EFFECT_DATE = :eff_date ");
				queryCondition.setObject("eff_date", list3.get(0).get("EFFECT_DATE"));
				queryCondition.setQueryString(sql.toString());
				List<Map<String, Object>> list2 = dam.exeQuery(queryCondition);
				return_VO.setResultList2(list2);
			}
		}

		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		queryCondition.setQueryString("select DISTINCT EFFECT_DATE FROM TBPRD_RANK WHERE PRD_TYPE = 'BND' AND EFFECT_DATE > trunc(sysdate) ORDER BY EFFECT_DATE");
		List<Map<String, Object>> list3 = dam.exeQuery(queryCondition);
		return_VO.setResultList3(list3);

		this.sendRtnObject(return_VO);
	}

	public void saveSort(Object body, IPrimitiveMap header) throws JBranchException {
		PRD250InputVO inputVO = (PRD250InputVO) body;
		dam = this.getDataAccessManager();

		// del first
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		queryCondition.setQueryString("DELETE FROM TBPRD_RANK WHERE PRD_TYPE = 'BND' AND EFFECT_DATE = :eff_date");
		queryCondition.setObject("eff_date", inputVO.getDate());
		dam.exeUpdate(queryCondition);
		// then add
		for(Map<String, Object> map : inputVO.getReview_list()) {
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			queryCondition.setQueryString("SELECT SQ_TBPRD_RANK.nextval AS SEQ FROM DUAL");
			List<Map<String, Object>> SEQLIST = dam.exeQuery(queryCondition);
			BigDecimal seqNo = (BigDecimal) SEQLIST.get(0).get("SEQ");

			TBPRD_RANKVO vo = new TBPRD_RANKVO();
			vo.setSEQ(seqNo);
			vo.setPRD_TYPE("BND");
			vo.setPRD_ID(ObjectUtils.toString(map.get("prd_id")));
			vo.setPRD_RANK(new BigDecimal(ObjectUtils.toString(map.get("rank"))));
			vo.setEFFECT_DATE(new Timestamp(inputVO.getDate().getTime()));
			dam.create(vo);
		}

		this.sendRtnObject(null);
	}

	public void downloadSimpleTemp(Object body, IPrimitiveMap header) throws Exception {
		notifyClientToDownloadFile("doc//PRD//PRD250_EXAMPLE2.csv", "上傳指定商品代碼範例.csv");
		this.sendRtnObject(null);
	}

	public void uploadTemp(Object body, IPrimitiveMap header) throws Exception {
		PRD250InputVO inputVO = (PRD250InputVO) body;
		PRD250OutputVO return_VO = new PRD250OutputVO();
		XmlInfo xmlInfo = new XmlInfo();
		dam = this.getDataAccessManager();

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy/MM/dd");
		List<String> error3 = new ArrayList<String>();
		List<String> error4 = new ArrayList<String>();
		Set<String> idList = new HashSet<String>();
		String tempPath = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
		List<String[]> dataCsv = CSVUtil.getBig5CSVFile(tempPath, inputVO.getFileName());
		if(!dataCsv.isEmpty()) {
			Map<String, String> conYN = xmlInfo.doGetVariable("COMMON.YES_NO", FormatHelper.FORMAT_3);
			for(int i = 0;i < dataCsv.size();i++) {
				String[] str = dataCsv.get(i);
				if(i == 0) {
					try {
						if(!"商品代碼".equals(str[0].trim()))
							throw new Exception(str[0]);
						else if(!"海外債中文名稱簡稱".equals(str[1].trim()))
							throw new Exception(str[1]);
						else if(!"海外債中文名稱全稱".equals(str[2].trim()))
							throw new Exception(str[2]);
						else if(!"計價幣別".equals(str[3].trim()))
							throw new Exception(str[3]);
						else if(!"到期日期".equals(str[4].trim()))
							throw new Exception(str[4]);
						else if(!"商品風險等級".equals(str[5].trim()))
							throw new Exception(str[5]);
						else if(!"限專投申購".equals(str[6].trim()))
							throw new Exception(str[6]);
						else if(!"限高資產申購".equals(str[7].trim()))
							throw new Exception(str[7]);
						else if(!"限OBU申購".equals(str[8].trim()))
							throw new Exception(str[8]);
						else if(!"ISIN CODE".equals(str[9].trim()))
							throw new Exception(str[9]);
						else if(!"發行日".equals(str[10].trim()))
							throw new Exception(str[10]);
						else if(!"股債類型(S:股票型、B:債券型)".equals(str[11].trim()))
							throw new Exception(str[11]);
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
				// TBPRD_BOND
				Boolean exist = false;
				QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				queryCondition.setQueryString("SELECT PRD_ID FROM TBPRD_BOND where PRD_ID = :id");
				queryCondition.setObject("id", str[0].trim());
				List<Map<String, Object>> list = dam.exeQuery(queryCondition);
				if (list.size() > 0)
					exist = true;

				TBPRD_BONDVO vo;
				if(exist)
					vo = (TBPRD_BONDVO) dam.findByPKey(TBPRD_BONDVO.TABLE_UID, str[0].trim());
				else {
					vo = new TBPRD_BONDVO();
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
					vo.setBOND_CNAME(str[1]);
				//
				if(utf_8_length(str[2]) > 255) {
					error3.add(str[0]+":"+str[2]);
					continue;
				} else
					vo.setBOND_CNAME_A(str[2]);
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
				//
				if(StringUtils.isNotBlank(str[6])) {
					if(StringUtils.isBlank(conYN.get(str[6].trim()))) {
						error3.add(str[0]+":"+str[6]);
						continue;
					}
					vo.setPI_BUY(str[6]);
				} else
					vo.setPI_BUY(str[6]);
				//
				if(StringUtils.isNotBlank(str[7])) {
					if(StringUtils.isBlank(conYN.get(str[7].trim()))) {
						error3.add(str[0]+":"+str[7]);
						continue;
					}
					vo.setHNWC_BUY(str[7]);
				} else
					vo.setHNWC_BUY(str[7]);
				//
				if(StringUtils.isNotBlank(str[8])) {
					if(StringUtils.isBlank(conYN.get(str[8].trim()))) {
						error3.add(str[0]+":"+str[8]);
						continue;
					}
					vo.setOBU_BUY(str[8]);
				} else
					vo.setOBU_BUY(str[8]);
				//
				if(exist)
					dam.update(vo);
				else
					dam.create(vo);

				// TBPRD_BONDINFO
				Boolean exist2 = false;
				queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				queryCondition.setQueryString("SELECT PRD_ID FROM TBPRD_BONDINFO where PRD_ID = :id");
				queryCondition.setObject("id", str[0].trim());
				List<Map<String, Object>> list2 = dam.exeQuery(queryCondition);
				if (list2.size() > 0)
					exist2 = true;

				TBPRD_BONDINFOVO vo2;
				if(exist2)
					vo2 = (TBPRD_BONDINFOVO) dam.findByPKey(TBPRD_BONDINFOVO.TABLE_UID, str[0].trim());
				else {
					vo2 = new TBPRD_BONDINFOVO();
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
						vo2.setDATE_OF_FLOTATION(new Timestamp(sdf.parse(str[10]).getTime()));
					} catch (Exception e) {
						try {
							vo2.setDATE_OF_FLOTATION(new Timestamp(sdf2.parse(str[10]).getTime()));
						} catch (Exception e2) {
							error3.add(str[0]+":"+str[10]);
							continue;
						}
					}
				} else
					vo2.setDATE_OF_FLOTATION(null);
				//
				if(exist)
					dam.update(vo2);
				else
					dam.create(vo2);

				if(StringUtils.isNotBlank(str[11])) {
					String stock_bond_type = str[11].trim().toUpperCase();
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
		this.sendRtnObject(return_VO);
	}

	/*
	 * #1404 上傳專案參數 使用delete insert 複製PRD230同名功能修改
	 */
	public void uploadProject(Object body, IPrimitiveMap header) throws Exception {
		PRD250InputVO inputVO = (PRD250InputVO) body;
		PRD250OutputVO return_VO = new PRD250OutputVO();

		dam = this.getDataAccessManager();
		// 先清空
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("delete TBSYSPARAMETER where PARAM_TYPE = :PARAM_TYPE ");
		queryCondition.setObject("PARAM_TYPE", "PRD.BOND_PROJECT");
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
				parameterPK.setPARAM_TYPE("PRD.BOND_PROJECT");
				parameterPK.setPARAM_CODE(str[0].trim());
				TBSYSPARAMETERVO parameterVO = new TBSYSPARAMETERVO();
				parameterVO.setcomp_id(parameterPK);
				parameterVO.setPARAM_NAME(str[1].trim());
				parameterVO.setPARAM_NAME_EDIT(str[1].trim());

				sql = new StringBuffer();
				sql.append("select max(PARAM_ORDER) as COUNT from TBSYSPARAMETER where PARAM_TYPE = :PARAM_TYPE");
				queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				queryCondition.setObject("PARAM_TYPE", "PRD.BOND_PROJECT");
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
	 * 2023.01.30 #1404 上傳客群參數 使用delete insert 複製PRD230同名功能修改
	 *
	 */
	public void uploadCustomerLevel(Object body, IPrimitiveMap header) throws Exception {
		PRD250InputVO inputVO = (PRD250InputVO) body;
		PRD250OutputVO return_VO = new PRD250OutputVO();

		dam = this.getDataAccessManager();
		// 先清空
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("delete TBSYSPARAMETER where PARAM_TYPE = :PARAM_TYPE ");
		queryCondition.setObject("PARAM_TYPE", "PRD.BOND_CUSTOMER_LEVEL");
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
				parameterPK.setPARAM_TYPE("PRD.BOND_CUSTOMER_LEVEL");
				parameterPK.setPARAM_CODE(str[0].trim());
				TBSYSPARAMETERVO parameterVO = new TBSYSPARAMETERVO();
				parameterVO.setcomp_id(parameterPK);
				parameterVO.setPARAM_NAME(str[1].trim());
				parameterVO.setPARAM_NAME_EDIT(str[1].trim());

				sql = new StringBuffer();
				sql.append("select max(PARAM_ORDER) as COUNT from TBSYSPARAMETER where PARAM_TYPE = :PARAM_TYPE");
				queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				queryCondition.setObject("PARAM_TYPE", "PRD.BOND_CUSTOMER_LEVEL");
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
	 * #1404
	 */
	public void updateTBPRD_BOND_Tags(Object body, IPrimitiveMap header) throws Exception {
		PRD250InputVO inputVO = (PRD250InputVO) body;
		PRD250OutputVO return_VO = new PRD250OutputVO();
		dam = this.getDataAccessManager();

		// check again
		// TBPRD_BOND
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT PRD_ID FROM TBPRD_BOND where PRD_ID = :id ");
		queryCondition.setObject("id", inputVO.getPrd_id());
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		if (list.size() == 0)
			throw new APException("ehl_01_common_026");

		TBPRD_BONDVO bondVO = (TBPRD_BONDVO) dam.findByPKey(TBPRD_BONDVO.TABLE_UID, inputVO.getPrd_id());

		// 專案
		if (StringUtils.isNotBlank(inputVO.getBondProject())) {
			bondVO.setPROJECT(inputVO.getBondProject());
		}
		// 客群
		if (StringUtils.isNotBlank(inputVO.getBondCustLevel())) {
			bondVO.setCUSTOMER_LEVEL(inputVO.getBondCustLevel());
		}
		dam.update(bondVO);

		this.sendRtnObject(return_VO);
	}

	private void addReviewStockBondType (String prd_id, String stock_bond_type) throws JBranchException {
		addReviewStockBondType(prd_id,stock_bond_type,null);
	}
}
