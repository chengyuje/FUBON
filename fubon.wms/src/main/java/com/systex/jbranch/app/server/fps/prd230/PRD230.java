package com.systex.jbranch.app.server.fps.prd230;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
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

import com.systex.jbranch.app.common.fps.table.TBPRD_FUNDINFOVO;
import com.systex.jbranch.app.common.fps.table.TBPRD_FUNDINFO_REVIEWVO;
import com.systex.jbranch.app.common.fps.table.TBPRD_FUNDVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataManager.DataManager;
import com.systex.jbranch.platform.common.dataManager.WorkStation;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.errHandle.NotFoundException;
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
 * prd230
 * 
 * #1339 基金標籤  主題 專案 2022.10.28
 * #1403 基金標籤  客群 2023.01.30
 * 
 * @author moron
 * @date 2016/09/26
 * @spec null
 */
@Component("prd230")
@Scope("request")
public class PRD230 extends FubonWmsBizLogic {
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(PRD230.class);

	public void inquire(Object body, IPrimitiveMap header) throws JBranchException {
		WorkStation ws = DataManager.getWorkStation(uuid);
		PRD230InputVO inputVO = (PRD230InputVO) body;
		PRD230OutputVO return_VO = new PRD230OutputVO();
		dam = this.getDataAccessManager();

		// getRoleList
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT COUNT(ROLEID) AS COUNTS FROM TBSYSSECUROLPRIASS ");
		sql.append("WHERE PRIVILEGEID IN (SELECT PRIVILEGEID FROM TBSYSSECUPRIFUNMAP WHERE ITEMID = 'PRD230' AND FUNCTIONID = 'confirm') AND ROLEID = :roleID ");
		queryCondition.setObject("roleID", getUserVariable(FubonSystemVariableConsts.LOGINROLE));
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> privilegeCount = dam.exeQuery(queryCondition);
		//
		sql = new StringBuffer();
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sql.append("SELECT STOCK_BOND_TYPE, SEQ,PRD_ID,FUND_CNAME_A,SELLING,LIPPER_ID,ALLOTMENT_RATIO,MAIN_PRD_SDATE,MAIN_PRD_EDATE,RAISE_FUND_SDATE,RAISE_FUND_EDATE,IPO,IPO_SDATE,IPO_EDATE,CNR_YIELD,CNR_MULTIPLE,MULTIPLE_SDATE,MULTIPLE_EDATE,CNR_DISCOUNT,RATE_DISCOUNT,IS_CNR_TARGET,CNR_TARGET_SDATE,CNR_TARGET_EDATE,CNR_FEE,FUS40,NO_E_PURCHASE,NO_E_OUT,NO_E_IN,NO_E_BUYBACK,QUOTA_CONTROL,Y_RETURN,Y_STD,SDATE,EDATE,LIPPER_RANK,LIPPER_BENCHMARK_ID,ACT_TYPE,REVIEW_STATUS,VIGILANT,CREATOR, WARNING, SUBJECT1, SUBJECT2, SUBJECT3, PROJECT1, PROJECT2, CUSTOMER_LEVEL, ");
		sql.append("CASE WHEN TRUNC(SYSDATE) BETWEEN MAIN_PRD_SDATE AND MAIN_PRD_EDATE THEN 'Y' ELSE 'N' END AS MAIN_PRD, ");
		sql.append("CASE WHEN TRUNC(SYSDATE) BETWEEN RAISE_FUND_SDATE AND RAISE_FUND_EDATE THEN 'Y' ELSE 'N' END AS RAISE_FUND ");
		sql.append("FROM ( ");
		// 覆核人員
		if (((BigDecimal) privilegeCount.get(0).get("COUNTS")).compareTo(new BigDecimal(0)) > 0) {
			sql.append("SELECT rw.STOCK_BOND_TYPE, rw.SEQ,rw.PRD_ID,CASE WHEN fund.FUND_CNAME_A IS NULL THEN fund.FUND_CNAME ELSE fund.FUND_CNAME_A END AS FUND_CNAME_A,rw.LIPPER_ID,rw.ALLOTMENT_RATIO,rw.MAIN_PRD,rw.MAIN_PRD_SDATE,rw.MAIN_PRD_EDATE,rw.RAISE_FUND,rw.RAISE_FUND_SDATE,rw.RAISE_FUND_EDATE,rw.IPO,rw.IPO_SDATE,rw.IPO_EDATE,rw.CNR_YIELD,rw.CNR_MULTIPLE,rw.MULTIPLE_SDATE,rw.MULTIPLE_EDATE,rw.CNR_DISCOUNT,rw.RATE_DISCOUNT,rw.IS_CNR_TARGET,rw.CNR_TARGET_SDATE,rw.CNR_TARGET_EDATE,rw.CNR_FEE,rw.FUS40,rw.NO_E_PURCHASE,rw.NO_E_OUT,rw.NO_E_IN,rw.NO_E_BUYBACK,rw.QUOTA_CONTROL,rw.Y_RETURN,rw.Y_STD,rw.SDATE,rw.EDATE,rw.LIPPER_RANK,rw.LIPPER_BENCHMARK_ID,rw.ACT_TYPE,rw.REVIEW_STATUS,rw.SELLING,rw.CREATOR ,VIGILANT, fund.WARNING, fund.SUBJECT1, fund.SUBJECT2, fund.SUBJECT3, rw.PROJECT1, rw.PROJECT2, rw.CUSTOMER_LEVEL ");
			sql.append("FROM TBPRD_FUNDINFO_REVIEW rw left join TBPRD_FUND fund on rw.PRD_ID = fund.PRD_ID ");
			sql.append("WHERE rw.REVIEW_STATUS = 'W' ");
			if (!StringUtils.equals("HOME", inputVO.getPassParams())) {
				sql.append("UNION ");
				sql.append("SELECT info.STOCK_BOND_TYPE, null as SEQ,info.PRD_ID,CASE WHEN fund.FUND_CNAME_A IS NULL THEN fund.FUND_CNAME ELSE fund.FUND_CNAME_A END AS FUND_CNAME_A,info.LIPPER_ID,info.ALLOTMENT_RATIO,info.MAIN_PRD,info.MAIN_PRD_SDATE,info.MAIN_PRD_EDATE,info.RAISE_FUND,info.RAISE_FUND_SDATE,info.RAISE_FUND_EDATE,info.IPO,info.IPO_SDATE,info.IPO_EDATE,info.CNR_YIELD,info.CNR_MULTIPLE,info.MULTIPLE_SDATE,info.MULTIPLE_EDATE,info.CNR_DISCOUNT,info.RATE_DISCOUNT,info.IS_CNR_TARGET,info.CNR_TARGET_SDATE,info.CNR_TARGET_EDATE,info.CNR_FEE,info.FUS40,info.NO_E_PURCHASE,info.NO_E_OUT,info.NO_E_IN,info.NO_E_BUYBACK,info.QUOTA_CONTROL,info.Y_RETURN,info.Y_STD,info.SDATE,info.EDATE,info.LIPPER_RANK,info.LIPPER_BENCHMARK_ID,rw.ACT_TYPE,rw.REVIEW_STATUS,info.SELLING,null as CREATOR ,info.VIGILANT, fund.WARNING, fund.SUBJECT1, fund.SUBJECT2, fund.SUBJECT3, fund.PROJECT1, fund.PROJECT2, fund.CUSTOMER_LEVEL  ");
				sql.append("FROM TBPRD_FUNDINFO info left join TBPRD_FUND fund on info.PRD_ID = fund.PRD_ID ");
				sql.append("left join TBPRD_FUNDINFO_REVIEW rw on rw.PRD_ID = info.PRD_ID and rw.REVIEW_STATUS = 'W' ");
				sql.append("WHERE NVL(info.REVIEW_STATUS, 'Y') = 'Y' ");
				sql.append("AND info.PRD_ID NOT IN (SELECT PRD_ID FROM TBPRD_FUNDINFO_REVIEW WHERE REVIEW_STATUS = 'W') ");
				// 2017/5/12 HOME?
				if (!StringUtils.isBlank(inputVO.getPrd_id())) {
					sql.append("AND info.PRD_ID like :id ");
					queryCondition.setObject("id", inputVO.getPrd_id() + "%");
				}
			}
		} else {
			sql.append("SELECT rw.STOCK_BOND_TYPE, rw.SEQ,rw.PRD_ID,CASE WHEN fund.FUND_CNAME_A IS NULL THEN fund.FUND_CNAME ELSE fund.FUND_CNAME_A END AS FUND_CNAME_A,rw.LIPPER_ID,rw.ALLOTMENT_RATIO,rw.MAIN_PRD,rw.MAIN_PRD_SDATE,rw.MAIN_PRD_EDATE,rw.RAISE_FUND,rw.RAISE_FUND_SDATE,rw.RAISE_FUND_EDATE,rw.IPO,rw.IPO_SDATE,rw.IPO_EDATE,rw.CNR_YIELD,rw.CNR_MULTIPLE,rw.MULTIPLE_SDATE,rw.MULTIPLE_EDATE,rw.CNR_DISCOUNT,rw.RATE_DISCOUNT,rw.IS_CNR_TARGET,rw.CNR_TARGET_SDATE,rw.CNR_TARGET_EDATE,rw.CNR_FEE,rw.FUS40,rw.NO_E_PURCHASE,rw.NO_E_OUT,rw.NO_E_IN,rw.NO_E_BUYBACK,rw.QUOTA_CONTROL,rw.Y_RETURN,rw.Y_STD,rw.SDATE,rw.EDATE,rw.LIPPER_RANK,rw.LIPPER_BENCHMARK_ID,rw.ACT_TYPE,rw.REVIEW_STATUS,rw.SELLING,null as CREATOR ,rw.VIGILANT, fund.WARNING, fund.SUBJECT1, fund.SUBJECT2, fund.SUBJECT3, rw.PROJECT1, rw.PROJECT2, rw.CUSTOMER_LEVEL  ");
			sql.append("FROM TBPRD_FUNDINFO_REVIEW rw left join TBPRD_FUND fund on rw.PRD_ID = fund.PRD_ID ");
			sql.append("WHERE rw.REVIEW_STATUS = 'W' ");
			sql.append("AND rw.CREATOR = :creator ");
			sql.append("UNION ");
			sql.append("SELECT info.STOCK_BOND_TYPE, null as SEQ,info.PRD_ID,CASE WHEN fund.FUND_CNAME_A IS NULL THEN fund.FUND_CNAME ELSE fund.FUND_CNAME_A END AS FUND_CNAME_A,info.LIPPER_ID,info.ALLOTMENT_RATIO,info.MAIN_PRD,info.MAIN_PRD_SDATE,info.MAIN_PRD_EDATE,info.RAISE_FUND,info.RAISE_FUND_SDATE,info.RAISE_FUND_EDATE,info.IPO,info.IPO_SDATE,info.IPO_EDATE,info.CNR_YIELD,info.CNR_MULTIPLE,info.MULTIPLE_SDATE,info.MULTIPLE_EDATE,info.CNR_DISCOUNT,info.RATE_DISCOUNT,info.IS_CNR_TARGET,info.CNR_TARGET_SDATE,info.CNR_TARGET_EDATE,info.CNR_FEE,info.FUS40,info.NO_E_PURCHASE,info.NO_E_OUT,info.NO_E_IN,info.NO_E_BUYBACK,info.QUOTA_CONTROL,info.Y_RETURN,info.Y_STD,info.SDATE,info.EDATE,info.LIPPER_RANK,info.LIPPER_BENCHMARK_ID,rw.ACT_TYPE,rw.REVIEW_STATUS,info.SELLING,null as CREATOR ,info.VIGILANT, fund.WARNING, fund.SUBJECT1, fund.SUBJECT2, fund.SUBJECT3, fund.PROJECT1, fund.PROJECT2, fund.CUSTOMER_LEVEL  ");
			sql.append("FROM TBPRD_FUNDINFO info left join TBPRD_FUND fund on info.PRD_ID = fund.PRD_ID ");
			sql.append("left join TBPRD_FUNDINFO_REVIEW rw on rw.PRD_ID = info.PRD_ID and rw.REVIEW_STATUS = 'W' ");
			sql.append("WHERE NVL(info.REVIEW_STATUS, 'Y') = 'Y' ");
			sql.append("AND info.PRD_ID NOT IN (SELECT PRD_ID FROM TBPRD_FUNDINFO_REVIEW WHERE REVIEW_STATUS = 'W' AND CREATOR = :creator) ");
			queryCondition.setObject("creator", ws.getUser().getUserID());
			// 2017/5/12
			if (!StringUtils.isBlank(inputVO.getPrd_id())) {
				sql.append("AND info.PRD_ID like :id ");
				queryCondition.setObject("id", inputVO.getPrd_id() + "%");
			}
		}
		sql.append(") WHERE 1=1 ");
		sql.append("ORDER BY CASE WHEN REVIEW_STATUS = 'W' THEN 0 WHEN REVIEW_STATUS = 'Y' THEN 1 ELSE 2 END ASC,PRD_ID ");
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		return_VO.setResultList(list);
		this.sendRtnObject(return_VO);
	}

	public void checkID(Object body, IPrimitiveMap header) throws JBranchException {
		PRD230InputVO inputVO = (PRD230InputVO) body;
		PRD230OutputVO return_VO = new PRD230OutputVO();
		dam = this.getDataAccessManager();

		// update
		if (StringUtils.equals("Y", inputVO.getStatus())) {
			// TBPRD_FUND
			QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sql = new StringBuffer();
			sql.append("SELECT PRD_ID,LIPPER_ID,FUND_CNAME_A FROM TBPRD_FUND where PRD_ID = :id ");
			queryCondition.setObject("id", inputVO.getPrd_id());
			queryCondition.setQueryString(sql.toString());
			List<Map<String, Object>> list = dam.exeQuery(queryCondition);
			if (list.size() > 0) {
				return_VO.setLipper(ObjectUtils.toString(list.get(0).get("LIPPER_ID")));
				return_VO.setCname(ObjectUtils.toString(list.get(0).get("FUND_CNAME_A")));
				return_VO.setCanEdit(true);
			} else {
				return_VO.setCanEdit(false);
				return_VO.setErrorMsg("ehl_01_common_026");
			}

			// TBPRD_FUNDINFO_REVIEW
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sql = new StringBuffer();
			sql.append("SELECT PRD_ID FROM TBPRD_FUNDINFO_REVIEW where PRD_ID = :id and REVIEW_STATUS = 'W' ");
			queryCondition.setObject("id", inputVO.getPrd_id());
			queryCondition.setQueryString(sql.toString());
			List<Map<String, Object>> list2 = dam.exeQuery(queryCondition);
			if (list2.size() > 0) {
				return_VO.setCanEdit(false);
				return_VO.setErrorMsg("ehl_01_common_028");
			}
		}
		// sort
		else if (StringUtils.equals("S", inputVO.getStatus())) {
			// TBPRD_FUND 有限制可申購
			QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sql = new StringBuffer();
			// sql.append("SELECT PRD_ID, FUND_CNAME_A, RISKCATE_ID, CURRENCY_STD_ID FROM TBPRD_FUND where PRD_ID = :id and IS_SALE = '1'");
			sql.append("SELECT PRD.PRD_ID, PRD.FUND_CNAME_A, PRD.RISKCATE_ID, PRD.CURRENCY_STD_ID, INFO.STOCK_BOND_TYPE ");
			sql.append("FROM TBPRD_FUND PRD LEFT JOIN TBPRD_FUNDINFO INFO ON PRD.PRD_ID = INFO.PRD_ID ");
			sql.append("WHERE PRD.PRD_ID = :prd_id AND PRD.IS_SALE = '1' ");

			System.out.println(inputVO.getStock_bond_type());
			if (StringUtils.isNotBlank(inputVO.getStock_bond_type())) {
				sql.append("AND INFO.STOCK_BOND_TYPE = :stock_bond_type ");
				queryCondition.setObject("stock_bond_type", inputVO.getStock_bond_type());
			}

			queryCondition.setObject("prd_id", inputVO.getPrd_id());
			queryCondition.setQueryString(sql.toString());
			List<Map<String, Object>> list = dam.exeQuery(queryCondition);
			if (list.size() > 0) {
				return_VO.setCname(ObjectUtils.toString(list.get(0).get("FUND_CNAME_A")));
				return_VO.setRisk_id(ObjectUtils.toString(list.get(0).get("RISKCATE_ID")));
				return_VO.setCurrency(ObjectUtils.toString(list.get(0).get("CURRENCY_STD_ID")));
				return_VO.setCanEdit(true);
			} else
				return_VO.setCanEdit(false);
		}
		// add
		else {
			// TBPRD_FUND
			QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sql = new StringBuffer();
			sql.append("SELECT PRD_ID,LIPPER_ID,FUND_CNAME_A FROM TBPRD_FUND where PRD_ID = :id ");
			queryCondition.setObject("id", inputVO.getPrd_id());
			queryCondition.setQueryString(sql.toString());
			List<Map<String, Object>> list = dam.exeQuery(queryCondition);
			if (list.size() > 0) {
				return_VO.setLipper(ObjectUtils.toString(list.get(0).get("LIPPER_ID")));
				return_VO.setCname(ObjectUtils.toString(list.get(0).get("FUND_CNAME_A")));
				return_VO.setCanEdit(true);
			} else {
				return_VO.setCanEdit(false);
				return_VO.setErrorMsg("ehl_01_common_026");
			}
			// TBPRD_FUNDINFO
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sql = new StringBuffer();
			sql.append("SELECT PRD_ID FROM TBPRD_FUNDINFO where PRD_ID = :id ");
			queryCondition.setObject("id", inputVO.getPrd_id());
			queryCondition.setQueryString(sql.toString());
			List<Map<String, Object>> list2 = dam.exeQuery(queryCondition);
			if (list2.size() > 0) {
				return_VO.setCanEdit(false);
				return_VO.setErrorMsg("ehl_01_common_027");
			}
			// TBPRD_FUNDINFO_REVIEW
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sql = new StringBuffer();
			sql.append("SELECT PRD_ID FROM TBPRD_FUNDINFO_REVIEW where PRD_ID = :id and REVIEW_STATUS = 'W' ");
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
		PRD230InputVO inputVO = (PRD230InputVO) body;
		dam = this.getDataAccessManager();

		// check again
		// TBPRD_FUND
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT PRD_ID FROM TBPRD_FUND where PRD_ID = :id ");
		queryCondition.setObject("id", inputVO.getPrd_id());
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		if (list.size() == 0)
			throw new APException("ehl_01_common_026");
		// TBPRD_FUNDINFO
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sql = new StringBuffer();
		sql.append("SELECT PRD_ID FROM TBPRD_FUNDINFO where PRD_ID = :id ");
		queryCondition.setObject("id", inputVO.getPrd_id());
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list2 = dam.exeQuery(queryCondition);
		if (list2.size() > 0)
			throw new APException("ehl_01_common_027");
		// TBPRD_FUNDINFO_REVIEW
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sql = new StringBuffer();
		sql.append("SELECT PRD_ID FROM TBPRD_FUNDINFO_REVIEW where PRD_ID = :id and REVIEW_STATUS = 'W' ");
		queryCondition.setObject("id", inputVO.getPrd_id());
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list3 = dam.exeQuery(queryCondition);
		if (list3.size() > 0)
			throw new APException("ehl_01_common_028");

		// seq
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sql = new StringBuffer();
		sql.append("SELECT SQ_TBPRD_FUNDINFO_REVIEW.nextval AS SEQ FROM DUAL ");
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> SEQLIST = dam.exeQuery(queryCondition);
		BigDecimal seqNo = (BigDecimal) SEQLIST.get(0).get("SEQ");
		// add TBPRD_FUNDINFO_REVIEW
		TBPRD_FUNDINFO_REVIEWVO vo = new TBPRD_FUNDINFO_REVIEWVO();
		vo.setSEQ(seqNo);
		vo.setPRD_ID(inputVO.getPrd_id());
		vo.setLIPPER_ID(inputVO.getLipper_id());
		if (StringUtils.isNotBlank(inputVO.getAllot()))
			vo.setALLOTMENT_RATIO(new BigDecimal(inputVO.getAllot()));
		vo.setVIGILANT(inputVO.getVigilant());
		// MAIN_PRD
		Date now = new Date();
		Boolean main_prd1, main_prd2;
		if (inputVO.getMain_sDate() != null) {
			vo.setMAIN_PRD_SDATE(new Timestamp(inputVO.getMain_sDate().getTime()));
			main_prd1 = now.after(inputVO.getMain_sDate());
		} else
			main_prd1 = false;
		if (inputVO.getMain_eDate() != null) {
			vo.setMAIN_PRD_EDATE(new Timestamp(inputVO.getMain_eDate().getTime()));
			main_prd2 = now.before(inputVO.getMain_eDate());
		} else
			main_prd2 = true;
		// vo.setMAIN_PRD(main_prd1 && main_prd2 ? "Y" : "N");
		// RAISE_FUND
		Boolean raise_fund1, raise_fund2;
		if (inputVO.getRaise_sDate() != null) {
			vo.setRAISE_FUND_SDATE(new Timestamp(inputVO.getRaise_sDate().getTime()));
			raise_fund1 = now.after(inputVO.getRaise_sDate());
		} else
			raise_fund1 = false;
		if (inputVO.getRaise_eDate() != null) {
			vo.setRAISE_FUND_EDATE(new Timestamp(inputVO.getRaise_eDate().getTime()));
			raise_fund2 = now.before(inputVO.getRaise_eDate());
		} else
			raise_fund2 = true;
		// vo.setRAISE_FUND(raise_fund1 && raise_fund2 ? "Y" : "N");
		//
		if (inputVO.getRaise_sDate() != null)
			vo.setRAISE_FUND_SDATE(new Timestamp(inputVO.getRaise_sDate().getTime()));
		if (inputVO.getRaise_eDate() != null)
			vo.setRAISE_FUND_EDATE(new Timestamp(inputVO.getRaise_eDate().getTime()));
		vo.setIPO(inputVO.getIpo());
		if (inputVO.getIpo_sDate() != null)
			vo.setIPO_SDATE(new Timestamp(inputVO.getIpo_sDate().getTime()));
		if (inputVO.getIpo_eDate() != null)
			vo.setIPO_EDATE(new Timestamp(inputVO.getIpo_eDate().getTime()));
		if (StringUtils.isNotBlank(inputVO.getYield()))
			vo.setCNR_YIELD(new BigDecimal(inputVO.getYield()));
		if (StringUtils.isNotBlank(inputVO.getPlus()))
			vo.setCNR_MULTIPLE(new BigDecimal(inputVO.getPlus()));
		if (inputVO.getMulti_sDate() != null)
			vo.setMULTIPLE_SDATE(new Timestamp(inputVO.getMulti_sDate().getTime()));
		if (inputVO.getMulti_eDate() != null)
			vo.setMULTIPLE_EDATE(new Timestamp(inputVO.getMulti_eDate().getTime()));
		if (StringUtils.isNotBlank(inputVO.getCnr_discount()))
			vo.setCNR_DISCOUNT(new BigDecimal(inputVO.getCnr_discount()));
		if (StringUtils.isNotBlank(inputVO.getRate_discount()))
			vo.setRATE_DISCOUNT(new BigDecimal(inputVO.getRate_discount()));
		vo.setIS_CNR_TARGET(inputVO.getCnr_target());
		if (inputVO.getCnrtar_sDate() != null)
			vo.setCNR_TARGET_SDATE(new Timestamp(inputVO.getCnrtar_sDate().getTime()));
		if (inputVO.getCnrtar_eDate() != null)
			vo.setCNR_TARGET_EDATE(new Timestamp(inputVO.getCnrtar_eDate().getTime()));
		if (StringUtils.isNotBlank(inputVO.getFee()))
			vo.setCNR_FEE(new BigDecimal(inputVO.getFee()));
		vo.setFUS40(inputVO.getFus40());
		vo.setNO_E_PURCHASE(inputVO.getPurchase());
		vo.setNO_E_OUT(inputVO.getOut());
		vo.setNO_E_IN(inputVO.getEin());
		vo.setNO_E_BUYBACK(inputVO.getBuyback());
		vo.setQUOTA_CONTROL(inputVO.getControl());
		if (StringUtils.isNotBlank(inputVO.getYreturn()))
			vo.setY_RETURN(new BigDecimal(inputVO.getYreturn()));
		if (StringUtils.isNotBlank(inputVO.getStd()))
			vo.setY_STD(new BigDecimal(inputVO.getStd()));
		if (inputVO.getsDate() != null)
			vo.setSDATE(new Timestamp(inputVO.getsDate().getTime()));
		if (inputVO.geteDate() != null)
			vo.setEDATE(new Timestamp(inputVO.geteDate().getTime()));
		if (StringUtils.isNotBlank(inputVO.getLipper_rank()))
			vo.setLIPPER_RANK(new BigDecimal(inputVO.getLipper_rank()));
		if (StringUtils.isNotBlank(inputVO.getLipper_ben_id()))
			vo.setLIPPER_BENCHMARK_ID(new BigDecimal(inputVO.getLipper_ben_id()));
		if (StringUtils.isNotBlank(inputVO.getFund_project1()))
			vo.setPROJECT1(inputVO.getFund_project1());
		if (StringUtils.isNotBlank(inputVO.getFund_project2()))
			vo.setPROJECT2(inputVO.getFund_project2());
		if (StringUtils.isNotBlank(inputVO.getCustomer_level()))
			vo.setCUSTOMER_LEVEL(inputVO.getCustomer_level());
		vo.setACT_TYPE("A");
		vo.setREVIEW_STATUS("W");
		dam.create(vo);

		if (StringUtils.isNotBlank(inputVO.getStock_bond_type()))
			addReviewStockBondType(inputVO.getPrd_id(), inputVO.getStock_bond_type());

		this.sendRtnObject(null);
	}

	private void addReviewStockBondType(String prd_id, String stock_bond_type) throws JBranchException {
		addReviewStockBondType(prd_id,stock_bond_type,null);		
	}
	/*
	 * #1404
	 */
	private void addReviewStockBondType(String prd_id, String stock_bond_type, TBPRD_FUNDINFOVO info_vo) throws JBranchException {
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();

		sql.append("SELECT PRD_ID FROM TBPRD_FUNDINFO_REVIEW where PRD_ID = :prd_id AND REVIEW_STATUS = 'W' ");

		queryCondition.setObject("prd_id", prd_id);
		queryCondition.setQueryString(sql.toString());
		resultList = dam.exeQuery(queryCondition);

		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		if (resultList.size() > 0) {
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sql = new StringBuffer();
			sql.append("UPDATE TBPRD_FUNDINFO_REVIEW SET STOCK_BOND_TYPE = :stock_bond_type ");
			sql.append("WHERE PRD_ID = :prd_id AND REVIEW_STATUS = 'W' ");		
			if("$".equals(stock_bond_type)){
				queryCondition.setObject("stock_bond_type", info_vo != null ? info_vo.getSTOCK_BOND_TYPE() : null);
			} else {
				queryCondition.setObject("stock_bond_type", stock_bond_type);
			}
			queryCondition.setObject("prd_id", prd_id);
			queryCondition.setQueryString(sql.toString());
			dam.exeUpdate(queryCondition);
		}
	}

	private void addStockBondType(String prd_id, String stock_bond_type) throws JBranchException {
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();

		sql.append("SELECT PRD_ID FROM TBPRD_FUNDINFO where PRD_ID = :prd_id ");

		queryCondition.setObject("prd_id", prd_id);
		queryCondition.setQueryString(sql.toString());
		resultList = dam.exeQuery(queryCondition);

		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		if (resultList.size() > 0) {
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sql = new StringBuffer();
			sql.append("UPDATE TBPRD_FUNDINFO SET STOCK_BOND_TYPE = :stock_bond_type WHERE PRD_ID = :prd_id ");

			queryCondition.setObject("stock_bond_type", stock_bond_type);
			queryCondition.setObject("prd_id", prd_id);
			queryCondition.setQueryString(sql.toString());
			dam.exeUpdate(queryCondition);
		}
	}

	public void editData(Object body, IPrimitiveMap header) throws JBranchException {
		PRD230InputVO inputVO = (PRD230InputVO) body;
		dam = this.getDataAccessManager();

		// check again
		// TBPRD_FUNDINFO_REVIEW
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT PRD_ID FROM TBPRD_FUNDINFO_REVIEW where PRD_ID = :id and REVIEW_STATUS = 'W' ");
		queryCondition.setObject("id", inputVO.getPrd_id());
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		if (list.size() > 0)
			throw new APException("ehl_01_common_028");

		// seq
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sql = new StringBuffer();
		sql.append("SELECT SQ_TBPRD_FUNDINFO_REVIEW.nextval AS SEQ FROM DUAL ");
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> SEQLIST = dam.exeQuery(queryCondition);
		BigDecimal seqNo = (BigDecimal) SEQLIST.get(0).get("SEQ");
		// add TBPRD_FUNDINFO_REVIEW
		TBPRD_FUNDINFO_REVIEWVO vo = new TBPRD_FUNDINFO_REVIEWVO();
		vo.setSEQ(seqNo);
		vo.setPRD_ID(inputVO.getPrd_id());
		vo.setLIPPER_ID(inputVO.getLipper_id());
		if (StringUtils.isNotBlank(inputVO.getAllot()))
			vo.setALLOTMENT_RATIO(new BigDecimal(inputVO.getAllot()));
		else
			vo.setALLOTMENT_RATIO(null);
		vo.setVIGILANT(inputVO.getVigilant());
		// MAIN_PRD
		Date now = new Date();
		Boolean main_prd1, main_prd2;
		if (inputVO.getMain_sDate() != null) {
			vo.setMAIN_PRD_SDATE(new Timestamp(inputVO.getMain_sDate().getTime()));
			main_prd1 = now.after(inputVO.getMain_sDate());
		} else
			main_prd1 = false;
		if (inputVO.getMain_eDate() != null) {
			vo.setMAIN_PRD_EDATE(new Timestamp(inputVO.getMain_eDate().getTime()));
			main_prd2 = now.before(inputVO.getMain_eDate());
		} else
			main_prd2 = true;
		// vo.setMAIN_PRD(main_prd1 && main_prd2 ? "Y" : "N");
		// RAISE_FUND
		Boolean raise_fund1, raise_fund2;
		if (inputVO.getRaise_sDate() != null) {
			vo.setRAISE_FUND_SDATE(new Timestamp(inputVO.getRaise_sDate().getTime()));
			raise_fund1 = now.after(inputVO.getRaise_sDate());
		} else
			raise_fund1 = false;
		if (inputVO.getRaise_eDate() != null) {
			vo.setRAISE_FUND_EDATE(new Timestamp(inputVO.getRaise_eDate().getTime()));
			raise_fund2 = now.before(inputVO.getRaise_eDate());
		} else
			raise_fund2 = true;
		// vo.setRAISE_FUND(raise_fund1 && raise_fund2 ? "Y" : "N");
		//
		if (inputVO.getRaise_sDate() != null)
			vo.setRAISE_FUND_SDATE(new Timestamp(inputVO.getRaise_sDate().getTime()));
		else
			vo.setRAISE_FUND_SDATE(null);
		if (inputVO.getRaise_eDate() != null)
			vo.setRAISE_FUND_EDATE(new Timestamp(inputVO.getRaise_eDate().getTime()));
		else
			vo.setRAISE_FUND_EDATE(null);
		vo.setIPO(inputVO.getIpo());
		if (inputVO.getIpo_sDate() != null)
			vo.setIPO_SDATE(new Timestamp(inputVO.getIpo_sDate().getTime()));
		else
			vo.setIPO_SDATE(null);
		if (inputVO.getIpo_eDate() != null)
			vo.setIPO_EDATE(new Timestamp(inputVO.getIpo_eDate().getTime()));
		else
			vo.setIPO_EDATE(null);
		if (StringUtils.isNotBlank(inputVO.getYield()))
			vo.setCNR_YIELD(new BigDecimal(inputVO.getYield()));
		else
			vo.setCNR_YIELD(null);
		if (StringUtils.isNotBlank(inputVO.getPlus()))
			vo.setCNR_MULTIPLE(new BigDecimal(inputVO.getPlus()));
		else
			vo.setCNR_MULTIPLE(null);
		if (inputVO.getMulti_sDate() != null)
			vo.setMULTIPLE_SDATE(new Timestamp(inputVO.getMulti_sDate().getTime()));
		else
			vo.setMULTIPLE_SDATE(null);
		if (inputVO.getMulti_eDate() != null)
			vo.setMULTIPLE_EDATE(new Timestamp(inputVO.getMulti_eDate().getTime()));
		else
			vo.setMULTIPLE_EDATE(null);
		if (StringUtils.isNotBlank(inputVO.getCnr_discount()))
			vo.setCNR_DISCOUNT(new BigDecimal(inputVO.getCnr_discount()));
		else
			vo.setCNR_DISCOUNT(null);
		if (StringUtils.isNotBlank(inputVO.getRate_discount()))
			vo.setRATE_DISCOUNT(new BigDecimal(inputVO.getRate_discount()));
		else
			vo.setRATE_DISCOUNT(null);
		vo.setIS_CNR_TARGET(inputVO.getCnr_target());
		if (inputVO.getCnrtar_sDate() != null)
			vo.setCNR_TARGET_SDATE(new Timestamp(inputVO.getCnrtar_sDate().getTime()));
		else
			vo.setCNR_TARGET_SDATE(null);
		if (inputVO.getCnrtar_eDate() != null)
			vo.setCNR_TARGET_EDATE(new Timestamp(inputVO.getCnrtar_eDate().getTime()));
		else
			vo.setCNR_TARGET_EDATE(null);
		if (StringUtils.isNotBlank(inputVO.getFee()))
			vo.setCNR_FEE(new BigDecimal(inputVO.getFee()));
		else
			vo.setCNR_FEE(null);
		vo.setFUS40(inputVO.getFus40());
		vo.setNO_E_PURCHASE(inputVO.getPurchase());
		vo.setNO_E_OUT(inputVO.getOut());
		vo.setNO_E_IN(inputVO.getEin());
		vo.setNO_E_BUYBACK(inputVO.getBuyback());
		vo.setQUOTA_CONTROL(inputVO.getControl());
		if (StringUtils.isNotBlank(inputVO.getYreturn()))
			vo.setY_RETURN(new BigDecimal(inputVO.getYreturn()));
		else
			vo.setY_RETURN(null);
		if (StringUtils.isNotBlank(inputVO.getStd()))
			vo.setY_STD(new BigDecimal(inputVO.getStd()));
		else
			vo.setY_STD(null);
		if (inputVO.getsDate() != null)
			vo.setSDATE(new Timestamp(inputVO.getsDate().getTime()));
		else
			vo.setSDATE(null);
		if (inputVO.geteDate() != null)
			vo.setEDATE(new Timestamp(inputVO.geteDate().getTime()));
		else
			vo.setEDATE(null);
		if (StringUtils.isNotBlank(inputVO.getLipper_rank()))
			vo.setLIPPER_RANK(new BigDecimal(inputVO.getLipper_rank()));
		else
			vo.setLIPPER_RANK(null);
		if (StringUtils.isNotBlank(inputVO.getLipper_ben_id()))
			vo.setLIPPER_BENCHMARK_ID(new BigDecimal(inputVO.getLipper_ben_id()));
		else
			vo.setLIPPER_BENCHMARK_ID(null);
		if (StringUtils.isNotBlank(inputVO.getFund_project1()))
			vo.setPROJECT1(inputVO.getFund_project1());
		else 
			vo.setPROJECT1(null);
		if (StringUtils.isNotBlank(inputVO.getFund_project2()))
			vo.setPROJECT2(inputVO.getFund_project2());
		else 
			vo.setPROJECT2(null);
		if (StringUtils.isNotBlank(inputVO.getCustomer_level()))
			vo.setCUSTOMER_LEVEL(inputVO.getCustomer_level());
		else 
			vo.setCUSTOMER_LEVEL(null);
		vo.setACT_TYPE("M");
		vo.setREVIEW_STATUS("W");
		dam.create(vo);

		if (StringUtils.isNotBlank(inputVO.getStock_bond_type()))
			addReviewStockBondType(inputVO.getPrd_id(), inputVO.getStock_bond_type());

		this.sendRtnObject(null);
	}

	public void deleteData(Object body, IPrimitiveMap header) throws JBranchException {
		PRD230InputVO inputVO = (PRD230InputVO) body;
		dam = this.getDataAccessManager();

		// check again
		// TBPRD_FUNDINFO_REVIEW
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT PRD_ID FROM TBPRD_FUNDINFO_REVIEW where PRD_ID = :id and REVIEW_STATUS = 'W' ");
		queryCondition.setObject("id", inputVO.getPrd_id());
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		if (list.size() > 0)
			throw new APException("ehl_01_common_028");

		// seq
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sql = new StringBuffer();
		sql.append("SELECT SQ_TBPRD_FUNDINFO_REVIEW.nextval AS SEQ FROM DUAL ");
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> SEQLIST = dam.exeQuery(queryCondition);
		BigDecimal seqNo = (BigDecimal) SEQLIST.get(0).get("SEQ");
		// get ori
		TBPRD_FUNDINFOVO vo = new TBPRD_FUNDINFOVO();
		vo = (TBPRD_FUNDINFOVO) dam.findByPKey(TBPRD_FUNDINFOVO.TABLE_UID, inputVO.getPrd_id());
		TBPRD_FUNDVO fund_vo = new TBPRD_FUNDVO();
		fund_vo = (TBPRD_FUNDVO) dam.findByPKey(TBPRD_FUNDVO.TABLE_UID, inputVO.getPrd_id());
		if (vo != null) {
			// add TBPRD_FUNDINFO_REVIEW
			TBPRD_FUNDINFO_REVIEWVO rvo = new TBPRD_FUNDINFO_REVIEWVO();
			rvo.setSEQ(seqNo);
			rvo.setPRD_ID(vo.getPRD_ID());
			rvo.setVIGILANT(vo.getVIGILANT());
			rvo.setLIPPER_ID(vo.getLIPPER_ID());
			rvo.setALLOTMENT_RATIO(vo.getALLOTMENT_RATIO());
			rvo.setMAIN_PRD(vo.getMAIN_PRD());
			rvo.setMAIN_PRD_SDATE(vo.getMAIN_PRD_SDATE());
			rvo.setMAIN_PRD_EDATE(vo.getMAIN_PRD_EDATE());
			rvo.setRAISE_FUND(vo.getRAISE_FUND());
			rvo.setRAISE_FUND_SDATE(vo.getRAISE_FUND_SDATE());
			rvo.setRAISE_FUND_EDATE(vo.getRAISE_FUND_EDATE());
			rvo.setIPO(vo.getIPO());
			rvo.setIPO_SDATE(vo.getIPO_SDATE());
			rvo.setIPO_EDATE(vo.getIPO_EDATE());
			rvo.setCNR_YIELD(vo.getCNR_YIELD());
			rvo.setCNR_MULTIPLE(vo.getCNR_MULTIPLE());
			rvo.setMULTIPLE_SDATE(vo.getMULTIPLE_SDATE());
			rvo.setMULTIPLE_EDATE(vo.getMULTIPLE_EDATE());
			rvo.setCNR_DISCOUNT(vo.getCNR_DISCOUNT());
			rvo.setRATE_DISCOUNT(vo.getRATE_DISCOUNT());
			rvo.setIS_CNR_TARGET(vo.getIS_CNR_TARGET());
			rvo.setCNR_TARGET_SDATE(vo.getCNR_TARGET_SDATE());
			rvo.setCNR_TARGET_EDATE(vo.getCNR_TARGET_EDATE());
			rvo.setCNR_FEE(vo.getCNR_FEE());
			rvo.setFUS40(vo.getFUS40());
			rvo.setNO_E_PURCHASE(vo.getNO_E_PURCHASE());
			rvo.setNO_E_OUT(vo.getNO_E_OUT());
			rvo.setNO_E_IN(vo.getNO_E_IN());
			rvo.setNO_E_BUYBACK(vo.getNO_E_BUYBACK());
			rvo.setQUOTA_CONTROL(vo.getQUOTA_CONTROL());
			rvo.setY_RETURN(vo.getY_RETURN());
			rvo.setY_STD(vo.getY_STD());
			rvo.setSDATE(vo.getSDATE());
			rvo.setEDATE(vo.getEDATE());
			rvo.setLIPPER_RANK(vo.getLIPPER_RANK());
			rvo.setLIPPER_BENCHMARK_ID(vo.getLIPPER_BENCHMARK_ID());
			rvo.setPROJECT1(fund_vo.getPROJECT1());
			rvo.setPROJECT2(fund_vo.getPROJECT2());
			rvo.setCUSTOMER_LEVEL(fund_vo.getCUSTOMER_LEVEL());
			rvo.setACT_TYPE("D");
			rvo.setREVIEW_STATUS("W");
			dam.create(rvo);
		} else {
			// 顯示資料不存在
			throw new APException("ehl_01_common_001");
		}
		this.sendRtnObject(null);
	}

	public void review(Object body, IPrimitiveMap header) throws JBranchException {
		PRD230InputVO inputVO = (PRD230InputVO) body;
		dam = this.getDataAccessManager();

		// 2017/2/21
		for (Map<String, Object> rmap : inputVO.getReview_list()) {
			TBPRD_FUNDINFO_REVIEWVO rvo = new TBPRD_FUNDINFO_REVIEWVO();
			BigDecimal seq = new BigDecimal(ObjectUtils.toString(rmap.get("SEQ")));
			rvo = (TBPRD_FUNDINFO_REVIEWVO) dam.findByPKey(TBPRD_FUNDINFO_REVIEWVO.TABLE_UID, seq);
			if (rvo != null) {
				// confirm
				if ("Y".equals(inputVO.getStatus())) {
					// 新增
					if ("A".equals(rvo.getACT_TYPE())) {
						// TBPRD_FUNDINFO
						TBPRD_FUNDINFOVO vo = new TBPRD_FUNDINFOVO();
						vo.setPRD_ID(rvo.getPRD_ID());
						vo.setLIPPER_ID(rvo.getLIPPER_ID());
						vo.setALLOTMENT_RATIO(rvo.getALLOTMENT_RATIO());
						vo.setMAIN_PRD(rvo.getMAIN_PRD());
						vo.setVIGILANT(rvo.getVIGILANT());
						vo.setMAIN_PRD_SDATE(rvo.getMAIN_PRD_SDATE());
						vo.setMAIN_PRD_EDATE(rvo.getMAIN_PRD_EDATE());
						vo.setRAISE_FUND(rvo.getRAISE_FUND());
						vo.setRAISE_FUND_SDATE(rvo.getRAISE_FUND_SDATE());
						vo.setRAISE_FUND_EDATE(rvo.getRAISE_FUND_EDATE());
						vo.setIPO(rvo.getIPO());
						vo.setIPO_SDATE(rvo.getIPO_SDATE());
						vo.setIPO_EDATE(rvo.getIPO_EDATE());
						vo.setCNR_YIELD(rvo.getCNR_YIELD());
						vo.setCNR_MULTIPLE(rvo.getCNR_MULTIPLE());
						vo.setMULTIPLE_SDATE(rvo.getMULTIPLE_SDATE());
						vo.setMULTIPLE_EDATE(rvo.getMULTIPLE_EDATE());
						vo.setCNR_DISCOUNT(rvo.getCNR_DISCOUNT());
						vo.setRATE_DISCOUNT(rvo.getRATE_DISCOUNT());
						vo.setIS_CNR_TARGET(rvo.getIS_CNR_TARGET());
						vo.setCNR_TARGET_SDATE(rvo.getCNR_TARGET_SDATE());
						vo.setCNR_TARGET_EDATE(rvo.getCNR_TARGET_EDATE());
						vo.setCNR_FEE(rvo.getCNR_FEE());
						vo.setFUS40(rvo.getFUS40());
						vo.setNO_E_PURCHASE(rvo.getNO_E_PURCHASE());
						vo.setNO_E_OUT(rvo.getNO_E_OUT());
						vo.setNO_E_IN(rvo.getNO_E_IN());
						vo.setNO_E_BUYBACK(rvo.getNO_E_BUYBACK());
						vo.setQUOTA_CONTROL(rvo.getQUOTA_CONTROL());
						vo.setY_RETURN(rvo.getY_RETURN());
						vo.setY_STD(rvo.getY_STD());
						vo.setSDATE(rvo.getSDATE());
						vo.setEDATE(rvo.getEDATE());
						vo.setLIPPER_RANK(rvo.getLIPPER_RANK());
						vo.setLIPPER_BENCHMARK_ID(rvo.getLIPPER_BENCHMARK_ID());
						vo.setACT_TYPE("A");
						vo.setREVIEW_STATUS("Y");
						dam.create(vo);
						
						TBPRD_FUNDVO fund_vo = new TBPRD_FUNDVO();
						fund_vo = (TBPRD_FUNDVO) dam.findByPKey(TBPRD_FUNDVO.TABLE_UID, rvo.getPRD_ID());
						fund_vo.setPROJECT1(rvo.getPROJECT1());
						fund_vo.setPROJECT2(rvo.getPROJECT2());
						fund_vo.setCUSTOMER_LEVEL(rvo.getCUSTOMER_LEVEL());
						dam.update(fund_vo);
					}
					// 修改
					else if ("M".equals(rvo.getACT_TYPE())) {
						TBPRD_FUNDINFOVO vo = new TBPRD_FUNDINFOVO();
						vo = (TBPRD_FUNDINFOVO) dam.findByPKey(TBPRD_FUNDINFOVO.TABLE_UID, rvo.getPRD_ID());
						if (vo != null) {
							vo.setLIPPER_ID(rvo.getLIPPER_ID());
							vo.setALLOTMENT_RATIO(rvo.getALLOTMENT_RATIO());
							vo.setMAIN_PRD(rvo.getMAIN_PRD());
							vo.setMAIN_PRD_SDATE(rvo.getMAIN_PRD_SDATE());
							vo.setMAIN_PRD_EDATE(rvo.getMAIN_PRD_EDATE());
							vo.setRAISE_FUND(rvo.getRAISE_FUND());
							vo.setRAISE_FUND_SDATE(rvo.getRAISE_FUND_SDATE());
							vo.setRAISE_FUND_EDATE(rvo.getRAISE_FUND_EDATE());
							vo.setVIGILANT(rvo.getVIGILANT());
							vo.setIPO(rvo.getIPO());
							vo.setIPO_SDATE(rvo.getIPO_SDATE());
							vo.setIPO_EDATE(rvo.getIPO_EDATE());
							vo.setCNR_YIELD(rvo.getCNR_YIELD());
							vo.setCNR_MULTIPLE(rvo.getCNR_MULTIPLE());
							vo.setMULTIPLE_SDATE(rvo.getMULTIPLE_SDATE());
							vo.setMULTIPLE_EDATE(rvo.getMULTIPLE_EDATE());
							vo.setCNR_DISCOUNT(rvo.getCNR_DISCOUNT());
							vo.setRATE_DISCOUNT(rvo.getRATE_DISCOUNT());
							vo.setIS_CNR_TARGET(rvo.getIS_CNR_TARGET());
							vo.setCNR_TARGET_SDATE(rvo.getCNR_TARGET_SDATE());
							vo.setCNR_TARGET_EDATE(rvo.getCNR_TARGET_EDATE());
							vo.setCNR_FEE(rvo.getCNR_FEE());
							vo.setFUS40(rvo.getFUS40());
							vo.setNO_E_PURCHASE(rvo.getNO_E_PURCHASE());
							vo.setNO_E_OUT(rvo.getNO_E_OUT());
							vo.setNO_E_IN(rvo.getNO_E_IN());
							vo.setNO_E_BUYBACK(rvo.getNO_E_BUYBACK());
							vo.setQUOTA_CONTROL(rvo.getQUOTA_CONTROL());
							vo.setY_RETURN(rvo.getY_RETURN());
							vo.setY_STD(rvo.getY_STD());
							vo.setSDATE(rvo.getSDATE());
							vo.setEDATE(rvo.getEDATE());
							vo.setLIPPER_RANK(rvo.getLIPPER_RANK());
							vo.setLIPPER_BENCHMARK_ID(rvo.getLIPPER_BENCHMARK_ID());
							vo.setACT_TYPE("M");
							vo.setREVIEW_STATUS("Y");
							dam.update(vo);
							
							TBPRD_FUNDVO fund_vo = new TBPRD_FUNDVO();
							fund_vo = (TBPRD_FUNDVO) dam.findByPKey(TBPRD_FUNDVO.TABLE_UID, rvo.getPRD_ID());
							fund_vo.setPROJECT1(rvo.getPROJECT1());
							fund_vo.setPROJECT2(rvo.getPROJECT2());
							fund_vo.setCUSTOMER_LEVEL(rvo.getCUSTOMER_LEVEL());
							dam.update(fund_vo);
						} else {
							// 顯示資料不存在
							throw new APException("ehl_01_common_001");
						}
					}
					// 刪除
					else if ("D".equals(rvo.getACT_TYPE())) {
						TBPRD_FUNDINFOVO vo = new TBPRD_FUNDINFOVO();
						vo = (TBPRD_FUNDINFOVO) dam.findByPKey(TBPRD_FUNDINFOVO.TABLE_UID, rvo.getPRD_ID());
						if (vo != null) {
							dam.delete(vo);
						} else {
							// 顯示資料不存在
							throw new APException("ehl_01_common_001");
						}
					}

					if (!"D".equals(rvo.getACT_TYPE())) {
						List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
						QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

						StringBuffer sb = new StringBuffer();
						sb.append("select STOCK_BOND_TYPE from TBPRD_FUNDINFO_REVIEW where SEQ = :seq ");

						queryCondition.setObject("seq", seq.toString());
						queryCondition.setQueryString(sb.toString());
						list = dam.exeQuery(queryCondition);
						if (list.size() > 0) {
							String stock_bond_type = null;
							if (list.get(0).get("STOCK_BOND_TYPE") != null) {
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
		PRD230InputVO inputVO = (PRD230InputVO) body;
		PRD230OutputVO return_VO = new PRD230OutputVO();
		XmlInfo xmlInfo = new XmlInfo();
		dam = this.getDataAccessManager();

		// getRoleList
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT COUNT(ROLEID) AS COUNTS FROM TBSYSSECUROLPRIASS ");
		sql.append("WHERE PRIVILEGEID IN (SELECT PRIVILEGEID FROM TBSYSSECUPRIFUNMAP WHERE ITEMID = 'PRD230' AND FUNCTIONID = 'confirm') AND ROLEID = :roleID ");
		queryCondition.setObject("roleID", getUserVariable(FubonSystemVariableConsts.LOGINROLE));
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> privilegeCount = dam.exeQuery(queryCondition);

		//
		sql = new StringBuffer();
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sql.append("SELECT STOCK_BOND_TYPE, SEQ,PRD_ID,FUND_CNAME_A,SELLING,LIPPER_ID,ALLOTMENT_RATIO,MAIN_PRD_SDATE,MAIN_PRD_EDATE,RAISE_FUND_SDATE,RAISE_FUND_EDATE,IPO,IPO_SDATE,IPO_EDATE,CNR_YIELD,CNR_MULTIPLE,MULTIPLE_SDATE,MULTIPLE_EDATE,CNR_DISCOUNT,RATE_DISCOUNT,IS_CNR_TARGET,CNR_TARGET_SDATE,CNR_TARGET_EDATE,CNR_FEE,FUS40,NO_E_PURCHASE,NO_E_OUT,NO_E_IN,NO_E_BUYBACK,QUOTA_CONTROL,Y_RETURN,Y_STD,SDATE,EDATE,LIPPER_RANK,LIPPER_BENCHMARK_ID,ACT_TYPE,REVIEW_STATUS,VIGILANT, CREATOR, WARNING, SUBJECT1, SUBJECT2, SUBJECT3, PROJECT1, PROJECT2, CUSTOMER_LEVEL, ");
		sql.append("CASE WHEN TRUNC(SYSDATE) BETWEEN MAIN_PRD_SDATE AND MAIN_PRD_EDATE THEN 'Y' ELSE 'N' END AS MAIN_PRD, ");
		sql.append("CASE WHEN TRUNC(SYSDATE) BETWEEN RAISE_FUND_SDATE AND RAISE_FUND_EDATE THEN 'Y' ELSE 'N' END AS RAISE_FUND ");
		sql.append("FROM ( ");
		// 覆核人員
		if (((BigDecimal) privilegeCount.get(0).get("COUNTS")).compareTo(new BigDecimal(0)) > 0) {
			sql.append("SELECT rw.STOCK_BOND_TYPE, rw.SEQ,rw.PRD_ID,CASE WHEN fund.FUND_CNAME_A IS NULL THEN fund.FUND_CNAME ELSE fund.FUND_CNAME_A END AS FUND_CNAME_A,rw.LIPPER_ID,rw.ALLOTMENT_RATIO,rw.MAIN_PRD,rw.MAIN_PRD_SDATE,rw.MAIN_PRD_EDATE,rw.RAISE_FUND,rw.RAISE_FUND_SDATE,rw.RAISE_FUND_EDATE,rw.IPO,rw.IPO_SDATE,rw.IPO_EDATE,rw.CNR_YIELD,rw.CNR_MULTIPLE,rw.MULTIPLE_SDATE,rw.MULTIPLE_EDATE,rw.CNR_DISCOUNT,rw.RATE_DISCOUNT,rw.IS_CNR_TARGET,rw.CNR_TARGET_SDATE,rw.CNR_TARGET_EDATE,rw.CNR_FEE,rw.FUS40,rw.NO_E_PURCHASE,rw.NO_E_OUT,rw.NO_E_IN,rw.NO_E_BUYBACK,rw.QUOTA_CONTROL,rw.Y_RETURN,rw.Y_STD,rw.SDATE,rw.EDATE,rw.LIPPER_RANK,rw.LIPPER_BENCHMARK_ID,rw.ACT_TYPE,rw.REVIEW_STATUS,rw.SELLING,rw.CREATOR,rw.VIGILANT, fund.WARNING, fund.SUBJECT1, fund.SUBJECT2, fund.SUBJECT3, fund.PROJECT1, fund.PROJECT2, fund.CUSTOMER_LEVEL  ");
			sql.append("FROM TBPRD_FUNDINFO_REVIEW rw left join TBPRD_FUND fund on rw.PRD_ID = fund.PRD_ID ");
			sql.append("WHERE rw.REVIEW_STATUS = 'W' ");
			sql.append("UNION ");
			sql.append("SELECT info.STOCK_BOND_TYPE, null as SEQ,info.PRD_ID,CASE WHEN fund.FUND_CNAME_A IS NULL THEN fund.FUND_CNAME ELSE fund.FUND_CNAME_A END AS FUND_CNAME_A,info.LIPPER_ID,info.ALLOTMENT_RATIO,info.MAIN_PRD,info.MAIN_PRD_SDATE,info.MAIN_PRD_EDATE,info.RAISE_FUND,info.RAISE_FUND_SDATE,info.RAISE_FUND_EDATE,info.IPO,info.IPO_SDATE,info.IPO_EDATE,info.CNR_YIELD,info.CNR_MULTIPLE,info.MULTIPLE_SDATE,info.MULTIPLE_EDATE,info.CNR_DISCOUNT,info.RATE_DISCOUNT,info.IS_CNR_TARGET,info.CNR_TARGET_SDATE,info.CNR_TARGET_EDATE,info.CNR_FEE,info.FUS40,info.NO_E_PURCHASE,info.NO_E_OUT,info.NO_E_IN,info.NO_E_BUYBACK,info.QUOTA_CONTROL,info.Y_RETURN,info.Y_STD,info.SDATE,info.EDATE,info.LIPPER_RANK,info.LIPPER_BENCHMARK_ID,rw.ACT_TYPE,rw.REVIEW_STATUS,info.SELLING,null as CREATOR,info.VIGILANT, fund.WARNING, fund.SUBJECT1, fund.SUBJECT2, fund.SUBJECT3, fund.PROJECT1, fund.PROJECT2, fund.CUSTOMER_LEVEL  ");
			sql.append("FROM TBPRD_FUNDINFO info left join TBPRD_FUND fund on info.PRD_ID = fund.PRD_ID ");
			sql.append("left join TBPRD_FUNDINFO_REVIEW rw on rw.PRD_ID = info.PRD_ID and rw.REVIEW_STATUS = 'W' ");
			sql.append("WHERE NVL(info.REVIEW_STATUS, 'Y') = 'Y' ");
			sql.append("AND info.PRD_ID NOT IN (SELECT PRD_ID FROM TBPRD_FUNDINFO_REVIEW WHERE REVIEW_STATUS = 'W') ");
			// 2017/5/12
			if (!StringUtils.isBlank(inputVO.getPrd_id())) {
				sql.append("AND info.PRD_ID like :id ");
				queryCondition.setObject("id", inputVO.getPrd_id() + "%");
			}
		} else {
			sql.append("SELECT rw.STOCK_BOND_TYPE, rw.SEQ,rw.PRD_ID,CASE WHEN fund.FUND_CNAME_A IS NULL THEN fund.FUND_CNAME ELSE fund.FUND_CNAME_A END AS FUND_CNAME_A,rw.LIPPER_ID,rw.ALLOTMENT_RATIO,rw.MAIN_PRD,rw.MAIN_PRD_SDATE,rw.MAIN_PRD_EDATE,rw.RAISE_FUND,rw.RAISE_FUND_SDATE,rw.RAISE_FUND_EDATE,rw.IPO,rw.IPO_SDATE,rw.IPO_EDATE,rw.CNR_YIELD,rw.CNR_MULTIPLE,rw.MULTIPLE_SDATE,rw.MULTIPLE_EDATE,rw.CNR_DISCOUNT,rw.RATE_DISCOUNT,rw.IS_CNR_TARGET,rw.CNR_TARGET_SDATE,rw.CNR_TARGET_EDATE,rw.CNR_FEE,rw.FUS40,rw.NO_E_PURCHASE,rw.NO_E_OUT,rw.NO_E_IN,rw.NO_E_BUYBACK,rw.QUOTA_CONTROL,rw.Y_RETURN,rw.Y_STD,rw.SDATE,rw.EDATE,rw.LIPPER_RANK,rw.LIPPER_BENCHMARK_ID,rw.ACT_TYPE,rw.REVIEW_STATUS,rw.SELLING,null as CREATOR ,rw.VIGILANT, fund.WARNING, fund.SUBJECT1, fund.SUBJECT2, fund.SUBJECT3, fund.PROJECT1, fund.PROJECT2, fund.CUSTOMER_LEVEL   ");
			sql.append("FROM TBPRD_FUNDINFO_REVIEW rw left join TBPRD_FUND fund on rw.PRD_ID = fund.PRD_ID ");
			sql.append("WHERE rw.REVIEW_STATUS = 'W' ");
			sql.append("AND rw.CREATOR = :creator ");
			sql.append("UNION ");
			sql.append("SELECT info.STOCK_BOND_TYPE, null as SEQ,info.PRD_ID,CASE WHEN fund.FUND_CNAME_A IS NULL THEN fund.FUND_CNAME ELSE fund.FUND_CNAME_A END AS FUND_CNAME_A,info.LIPPER_ID,info.ALLOTMENT_RATIO,info.MAIN_PRD,info.MAIN_PRD_SDATE,info.MAIN_PRD_EDATE,info.RAISE_FUND,info.RAISE_FUND_SDATE,info.RAISE_FUND_EDATE,info.IPO,info.IPO_SDATE,info.IPO_EDATE,info.CNR_YIELD,info.CNR_MULTIPLE,info.MULTIPLE_SDATE,info.MULTIPLE_EDATE,info.CNR_DISCOUNT,info.RATE_DISCOUNT,info.IS_CNR_TARGET,info.CNR_TARGET_SDATE,info.CNR_TARGET_EDATE,info.CNR_FEE,info.FUS40,info.NO_E_PURCHASE,info.NO_E_OUT,info.NO_E_IN,info.NO_E_BUYBACK,info.QUOTA_CONTROL,info.Y_RETURN,info.Y_STD,info.SDATE,info.EDATE,info.LIPPER_RANK,info.LIPPER_BENCHMARK_ID,rw.ACT_TYPE,rw.REVIEW_STATUS,info.SELLING,null as CREATOR,info.VIGILANT, fund.WARNING, fund.SUBJECT1, fund.SUBJECT2, fund.SUBJECT3, fund.PROJECT1, fund.PROJECT2, fund.CUSTOMER_LEVEL  ");
			sql.append("FROM TBPRD_FUNDINFO info left join TBPRD_FUND fund on info.PRD_ID = fund.PRD_ID ");
			sql.append("left join TBPRD_FUNDINFO_REVIEW rw on rw.PRD_ID = info.PRD_ID and rw.REVIEW_STATUS = 'W' ");
			sql.append("WHERE NVL(info.REVIEW_STATUS, 'Y') = 'Y' ");
			sql.append("AND info.PRD_ID NOT IN (SELECT PRD_ID FROM TBPRD_FUNDINFO_REVIEW WHERE REVIEW_STATUS = 'W' AND CREATOR = :creator) ");
			queryCondition.setObject("creator", ws.getUser().getUserID());
			// 2017/5/12
			if (!StringUtils.isBlank(inputVO.getPrd_id())) {
				sql.append("AND info.PRD_ID like :id ");
				queryCondition.setObject("id", inputVO.getPrd_id() + "%");
			}
		}
		sql.append(") WHERE 1=1 ");
		sql.append("ORDER BY CASE WHEN REVIEW_STATUS = 'W' THEN 0 WHEN REVIEW_STATUS = 'Y' THEN 1 ELSE 2 END ASC,PRD_ID ");
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		if (list.size() > 0) {
			// gen csv
			Map<String, String> comm_yn = xmlInfo.doGetVariable("COMMON.YES_NO", FormatHelper.FORMAT_3);
			Map<String, String> vigi_hs = xmlInfo.doGetVariable("PRD.FUND_VIGILANT", FormatHelper.FORMAT_3);
			Map<String, String> warning = xmlInfo.doGetVariable("PRD.FUND_C_ALERT", FormatHelper.FORMAT_3);
			Map<String, String> subject = xmlInfo.doGetVariable("PRD.FUND_SUBJECT", FormatHelper.FORMAT_3);
			Map<String, String> project = xmlInfo.doGetVariable("PRD.FUND_PROJECT", FormatHelper.FORMAT_3);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			String fileName = "基金清單_" + sdf.format(new Date()) + "_" + ws.getUser().getUserID() + ".csv";
			List listCSV = new ArrayList();
			for (Map<String, Object> map : list) {
				// 24 column
				String[] records = new String[31];
				int i = 0;

				if ("W".equals(ObjectUtils.toString(map.get("REVIEW_STATUS"))))
					records[i] = "覆核中";
				else
					records[i] = "已覆核";
				records[++i] = "=\"" + checkIsNull(map, "PRD_ID") + "\"";
				records[++i] = checkIsNull(map, "FUND_CNAME_A");
				records[++i] = checkIsNull(map, "ALLOTMENT_RATIO");
				records[++i] = vigi_hs.get(checkIsNull(map, "VIGILANT"));
				records[++i] = comm_yn.get(checkIsNull(map, "MAIN_PRD"));
				records[++i] = "=\"" + checkIsNull(map, "MAIN_PRD_SDATE") + "\"";
				records[++i] = "=\"" + checkIsNull(map, "MAIN_PRD_EDATE") + "\"";
				records[++i] = comm_yn.get(checkIsNull(map, "RAISE_FUND"));
				records[++i] = "=\"" + checkIsNull(map, "RAISE_FUND_SDATE") + "\"";
				records[++i] = "=\"" + checkIsNull(map, "RAISE_FUND_EDATE") + "\"";
				records[++i] = checkIsNull(map, "IPO");
				records[++i] = "=\"" + checkIsNull(map, "IPO_SDATE") + "\"";
				records[++i] = "=\"" + checkIsNull(map, "IPO_EDATE") + "\"";
				records[++i] = checkIsNull(map, "CNR_YIELD");
				records[++i] = checkIsNull(map, "CNR_MULTIPLE");
				records[++i] = "=\"" + checkIsNull(map, "MULTIPLE_SDATE") + "\"";
				records[++i] = "=\"" + checkIsNull(map, "MULTIPLE_EDATE") + "\"";
				records[++i] = checkIsNull(map, "CNR_DISCOUNT");
				records[++i] = checkIsNull(map, "RATE_DISCOUNT");
				records[++i] = "=\"" + checkIsNull(map, "CNR_TARGET_SDATE") + "\"";
				records[++i] = "=\"" + checkIsNull(map, "CNR_TARGET_EDATE") + "\"";
				records[++i] = checkIsNull(map, "CNR_FEE");

				if ("S".equals(ObjectUtils.toString(map.get("STOCK_BOND_TYPE")))) {
					records[++i] = "股票型";
				} else if ("B".equals(ObjectUtils.toString(map.get("STOCK_BOND_TYPE")))) {
					records[++i] = "債券型";
				} else {
					records[++i] = "";
				}
				records[++i] = checkIsNull(map, "WARNING");
				records[++i] = warning.get(checkIsNull(map, "WARNING"));
				records[++i] = subject.get(checkIsNull(map, "SUBJECT1"));
				records[++i] = subject.get(checkIsNull(map, "SUBJECT2"));
				records[++i] = subject.get(checkIsNull(map, "SUBJECT3"));
				records[++i] = project.get(checkIsNull(map, "PROJECT1"));
				records[++i] = project.get(checkIsNull(map, "PROJECT2"));

				listCSV.add(records);
			}
			// header
			String[] csvHeader = new String[31];
			int j = 0;
			csvHeader[j] = "覆核狀態";
			csvHeader[++j] = "基金代碼";
			csvHeader[++j] = "基金名稱";
			csvHeader[++j] = "拆帳比率%";
			csvHeader[++j] = "警示基金";
			csvHeader[++j] = "衛星";
			csvHeader[++j] = "衛星時間起日";
			csvHeader[++j] = "衛星時間迄日";
			csvHeader[++j] = "核心";
			csvHeader[++j] = "核心時間起日";
			csvHeader[++j] = "核心時間迄日";
			csvHeader[++j] = "IPO/專案";
			csvHeader[++j] = "IPO/專案時間起日";
			csvHeader[++j] = "IPO/專案時間迄日";
			csvHeader[++j] = "CNR分配率";
			csvHeader[++j] = "CNR加減碼";
			csvHeader[++j] = "核心區間起日";
			csvHeader[++j] = "核心區間迄日";
			csvHeader[++j] = "CNR收益扣減率";
			csvHeader[++j] = "實際收益扣減率";
			csvHeader[++j] = "基金管理費標的計績追溯起日";
			csvHeader[++j] = "基金管理費標的計績追溯迄日";
			csvHeader[++j] = "CNR基金管理費回饋";
			csvHeader[++j] = "股債類型";
			csvHeader[++j] = "警語代碼";
			csvHeader[++j] = "警語訊息";
			csvHeader[++j] = "主題名稱1";
			csvHeader[++j] = "主題名稱2";
			csvHeader[++j] = "主題名稱3";
			csvHeader[++j] = "專案名稱1";
			csvHeader[++j] = "專案名稱2";

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
		if (StringUtils.isNotBlank(ObjectUtils.toString(map.get(key)))) {
			return String.valueOf(map.get(key));
		} else {
			return "";
		}
	}

	public void upload(Object body, IPrimitiveMap header) throws Exception {
		PRD230InputVO inputVO = (PRD230InputVO) body;
		PRD230OutputVO return_VO = new PRD230OutputVO();
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
		if (!dataCsv.isEmpty()) {
			// Map<String, String> conYN =
			// xmlInfo.doGetVariable("COMMON.YES_NO", FormatHelper.FORMAT_3);
			for (int i = 0; i < dataCsv.size(); i++) {
				String[] str = dataCsv.get(i);
				if (i == 0) {
					try {
						if (!"商品代碼".equals(str[0].trim()))
							throw new Exception(str[0]);
						else if (!"拆帳比率".equals(str[1].trim()))
							throw new Exception(str[1]);
						else if (!"警示基金(H:持有 、S:減碼)".equals(str[2].trim()))
							throw new Exception(str[2]);
						else if (!"衛星時間起日".equals(str[3].substring(0, 6)))
							throw new Exception(str[3]);
						else if (!"衛星時間迄日".equals(str[4].substring(0, 6)))
							throw new Exception(str[4]);
						else if (!"核心時間起日".equals(str[5].trim()))
							throw new Exception(str[5]);
						else if (!"核心時間迄日".equals(str[6].trim()))
							throw new Exception(str[6]);
						else if (!"IPO/專案".equals(str[7].trim()))
							throw new Exception(str[7]);
						else if (!"IPO/專案時間起日".equals(str[8].trim()))
							throw new Exception(str[8]);
						else if (!"IPO/專案時間迄日".equals(str[9].trim()))
							throw new Exception(str[9]);
						else if (!"CNR分配率".equals(str[10].trim()))
							throw new Exception(str[10]);
						else if (!"CNR加減碼".equals(str[11].trim()))
							throw new Exception(str[11]);
						else if (!"核心區間起日".equals(str[12].substring(0, 6)))
							throw new Exception(str[12]);
						else if (!"核心區間迄日".equals(str[13].substring(0, 6)))
							throw new Exception(str[13]);
						else if (!"CNR收益扣減率".equals(str[14].trim()))
							throw new Exception(str[14]);
						else if (!"實際收益扣減率".equals(str[15].trim()))
							throw new Exception(str[15]);
						else if (!"基金管理費標的計績追溯起日".equals(str[16].trim()))
							throw new Exception(str[16]);
						else if (!"基金管理費標的計績追溯迄日".equals(str[17].trim()))
							throw new Exception(str[17]);
						else if (!"CNR基金管理費回饋".equals(str[18].trim()))
							throw new Exception(str[18]);
						else if (!"股債類型(S:股票型、B:債券型)".equals(str[19].trim()))
							throw new Exception(str[19]);
						else if (!"警語".equals(str[20].trim()))
							throw new Exception(str[20]);
						else if (!"主題代碼1".equals(str[21].trim()))
							throw new Exception(str[21]);
						else if (!"主題代碼2".equals(str[22].trim()))
							throw new Exception(str[22]);
						else if (!"主題代碼3".equals(str[23].trim()))
							throw new Exception(str[23]);
						else if (!"專案代碼1".equals(str[24].trim()))
							throw new Exception(str[24]);
						else if (!"專案代碼2".equals(str[25].trim()))
							throw new Exception(str[25]);
						else if (!"客群代碼".equals(str[26].trim()))
							throw new Exception(str[26]);
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
				// TBPRD_FUND
				String lipper = null;
				QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				StringBuffer sql = new StringBuffer();
				sql.append("SELECT PRD_ID,LIPPER_ID FROM TBPRD_FUND where PRD_ID = :id ");
				queryCondition.setObject("id", str[0].trim());
				queryCondition.setQueryString(sql.toString());
				List<Map<String, Object>> list = dam.exeQuery(queryCondition);
				if (list.size() == 0) {
					error.add(str[0]);
					continue;
				} else
					lipper = ObjectUtils.toString(list.get(0).get("LIPPER_ID"));
				
				dam.newTransactionExeMethod(this, "updateTags", Arrays.asList(str, error6));

				// TBPRD_FUNDINFO check edit
				Boolean exist = false;
				queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sql = new StringBuffer();
				sql.append("SELECT PRD_ID FROM TBPRD_FUNDINFO where PRD_ID = :id ");
				queryCondition.setObject("id", str[0].trim());
				queryCondition.setQueryString(sql.toString());
				List<Map<String, Object>> list2 = dam.exeQuery(queryCondition);
				if (list2.size() > 0)
					exist = true;
				// TBPRD_FUNDINFO_REVIEW
				queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sql = new StringBuffer();
				sql.append("SELECT PRD_ID FROM TBPRD_FUNDINFO_REVIEW where PRD_ID = :id and REVIEW_STATUS = 'W' ");
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
				sql.append("SELECT SQ_TBPRD_FUNDINFO_REVIEW.nextval AS SEQ FROM DUAL ");
				queryCondition.setQueryString(sql.toString());
				List<Map<String, Object>> SEQLIST = dam.exeQuery(queryCondition);
				BigDecimal seqNo = (BigDecimal) SEQLIST.get(0).get("SEQ");
				// add TBPRD_FUNDINFO_REVIEW
				TBPRD_FUNDINFO_REVIEWVO vo = new TBPRD_FUNDINFO_REVIEWVO();
				TBPRD_FUNDINFOVO info_vo = new TBPRD_FUNDINFOVO();
				info_vo = (TBPRD_FUNDINFOVO) dam.findByPKey(TBPRD_FUNDINFOVO.TABLE_UID, str[0].trim());
				TBPRD_FUNDVO fund_vo = new TBPRD_FUNDVO();
				fund_vo = (TBPRD_FUNDVO) dam.findByPKey(TBPRD_FUNDVO.TABLE_UID, str[0].trim());

				vo.setSEQ(seqNo);
				//
				if (utf_8_length(str[0]) > 16) {
					error3.add(str[0]);
					continue;
				} else
					vo.setPRD_ID(str[0].trim());
				//
				vo.setLIPPER_ID(lipper);
				//
				if (StringUtils.isBlank(str[1]) || StringUtils.equals(str[1],"$")) {
					vo.setALLOTMENT_RATIO(info_vo != null ? info_vo.getALLOTMENT_RATIO() : null);
				} else {
					try {
						BigDecimal str1 = new BigDecimal(str[1]);
						// NUMBER(6,2)
						if (getNumOfBigDecimal(str1) > 4)
							throw new Exception("");
						vo.setALLOTMENT_RATIO(str1);
					} catch (Exception e) {
						error3.add(str[0] + ":" + str[1]);
						continue;
					}
				}

				// if(StringUtils.isNotBlank(str[2])) {
				// if(StringUtils.isBlank(conYN.get(str[2]))) {
				// error3.add(str[0]+":"+str[2]);
				// continue;
				// }
				// vo.setVIGILANT(str[2]);
				// } else {
				// vo.setVIGILANT(info_vo != null ? info_vo.getVIGILANT() :
				// null);
				// }
				//
				if (StringUtils.isBlank(str[2]) || StringUtils.equals(str[2],"$")) {
					vo.setVIGILANT(null);
				} else  {
					String vigilant = str[2].trim().toUpperCase();
					if ("H".equals(vigilant) || "S".equals(vigilant)) {
						vo.setVIGILANT(vigilant);
					} else {
						throw new Exception("警示基金輸入格式有誤，請輸入H或S或空白。");
					}
				}

				if (StringUtils.isBlank(str[3]) || StringUtils.equals(str[3],"$")) {
					vo.setMAIN_PRD_SDATE(info_vo != null ? info_vo.getMAIN_PRD_SDATE() : null);
				} else  {
					try {
						vo.setMAIN_PRD_SDATE(new Timestamp(sdf.parse(str[3]).getTime()));
					} catch (Exception e) {
						try {
							vo.setMAIN_PRD_SDATE(new Timestamp(sdf2.parse(str[3]).getTime()));
						} catch (Exception e2) {
							error3.add(str[0] + ":" + str[3]);
							continue;
						}
					}
				}

				if (StringUtils.isBlank(str[4]) || StringUtils.equals(str[4],"$")) {
					vo.setMAIN_PRD_EDATE(info_vo != null ? info_vo.getMAIN_PRD_EDATE() : null);
				} else  {
					try {
						vo.setMAIN_PRD_EDATE(new Timestamp(sdf.parse(str[4]).getTime()));
					} catch (Exception e) {
						try {
							vo.setMAIN_PRD_EDATE(new Timestamp(sdf2.parse(str[4]).getTime()));
						} catch (Exception e2) {
							error3.add(str[0] + ":" + str[4]);
							continue;
						}
					}
				}

				if (StringUtils.isBlank(str[5]) || StringUtils.equals(str[5],"$")) {
					vo.setRAISE_FUND_SDATE(info_vo != null ? info_vo.getRAISE_FUND_SDATE() : null);
				} else  {
					try {
						vo.setRAISE_FUND_SDATE(new Timestamp(sdf.parse(str[5]).getTime()));
					} catch (Exception e) {
						try {
							vo.setRAISE_FUND_SDATE(new Timestamp(sdf2.parse(str[5]).getTime()));
						} catch (Exception e2) {
							error3.add(str[0] + ":" + str[5]);
							continue;
						}
					}
				}

				if (StringUtils.isBlank(str[6]) || StringUtils.equals(str[6],"$")) {
					vo.setRAISE_FUND_EDATE(info_vo != null ? info_vo.getRAISE_FUND_EDATE() : null);
				} else {
					try {
						vo.setRAISE_FUND_EDATE(new Timestamp(sdf.parse(str[6]).getTime()));
					} catch (Exception e) {
						try {
							vo.setRAISE_FUND_EDATE(new Timestamp(sdf2.parse(str[6]).getTime()));
						} catch (Exception e2) {
							error3.add(str[0] + ":" + str[6]);
							continue;
						}
					}
				}

				if (StringUtils.isBlank(str[7]) || StringUtils.equals(str[7],"$")) {
					vo.setIPO(info_vo != null ? info_vo.getIPO() : null);
				} else {
					if (utf_8_length(str[7]) > 100) {
						error3.add(str[0] + ":" + str[7]);
						continue;
					} else
						vo.setIPO(str[7]);
				}

				if (StringUtils.isBlank(str[8]) || StringUtils.equals(str[8],"$")) {
					vo.setIPO_SDATE(info_vo != null ? info_vo.getIPO_SDATE() : null);
				} else {
					try {
						vo.setIPO_SDATE(new Timestamp(sdf.parse(str[8]).getTime()));
					} catch (Exception e) {
						try {
							vo.setIPO_SDATE(new Timestamp(sdf2.parse(str[8]).getTime()));
						} catch (Exception e2) {
							error3.add(str[0] + ":" + str[8]);
							continue;
						}
					}
				}

				if (StringUtils.isBlank(str[9]) || StringUtils.equals(str[9],"$")) {
					vo.setIPO_EDATE(info_vo != null ? info_vo.getIPO_EDATE() : null);
				} else {
					try {
						vo.setIPO_EDATE(new Timestamp(sdf.parse(str[9]).getTime()));
					} catch (Exception e) {
						try {
							vo.setIPO_EDATE(new Timestamp(sdf2.parse(str[9]).getTime()));
						} catch (Exception e2) {
							error3.add(str[0] + ":" + str[9]);
							continue;
						}
					}
				}

				if (StringUtils.isBlank(str[10]) || StringUtils.equals(str[10],"$")) {
					vo.setCNR_YIELD(info_vo != null ? info_vo.getCNR_YIELD() : null);
				} else {
					try {
						BigDecimal str10 = new BigDecimal(str[10]);
						// NUMBER(6,2)
						if (getNumOfBigDecimal(str10) > 4)
							throw new Exception("");
						vo.setCNR_YIELD(str10);
					} catch (Exception e) {
						error3.add(str[0] + ":" + str[10]);
						continue;
					}
				}

				if (StringUtils.isBlank(str[11]) || StringUtils.equals(str[11],"$")) {
					vo.setCNR_MULTIPLE(info_vo != null ? info_vo.getCNR_MULTIPLE() : null);
				} else {
					try {
						BigDecimal str11 = new BigDecimal(str[11]);
						// NUMBER(6,2)
						if (getNumOfBigDecimal(str11) > 4)
							throw new Exception("");
						vo.setCNR_MULTIPLE(str11);
					} catch (Exception e) {
						error3.add(str[0] + ":" + str[11]);
						continue;
					}
				}

				if (StringUtils.isBlank(str[12]) || StringUtils.equals(str[12],"$")) {
					vo.setMULTIPLE_SDATE(info_vo != null ? info_vo.getMULTIPLE_SDATE() : null);
				} else {
					try {
						vo.setMULTIPLE_SDATE(new Timestamp(sdf.parse(str[12]).getTime()));
					} catch (Exception e) {
						try {
							vo.setMULTIPLE_SDATE(new Timestamp(sdf2.parse(str[12]).getTime()));
						} catch (Exception e2) {
							error3.add(str[0] + ":" + str[12]);
							continue;
						}
					}
				}

				if (StringUtils.isBlank(str[13]) || StringUtils.equals(str[13],"$")) {
					vo.setMULTIPLE_EDATE(info_vo != null ? info_vo.getMULTIPLE_EDATE() : null);
				} else {
					try {
						vo.setMULTIPLE_EDATE(new Timestamp(sdf.parse(str[13]).getTime()));
					} catch (Exception e) {
						try {
							vo.setMULTIPLE_EDATE(new Timestamp(sdf2.parse(str[13]).getTime()));
						} catch (Exception e2) {
							error3.add(str[0] + ":" + str[13]);
							continue;
						}
					}
				}

				if (StringUtils.isBlank(str[14]) || StringUtils.equals(str[14],"$")) {
					vo.setCNR_DISCOUNT(info_vo != null ? info_vo.getCNR_DISCOUNT() : null);
				} else {
					try {
						BigDecimal str14 = new BigDecimal(str[14]);
						// NUMBER(6,2)
						if (getNumOfBigDecimal(str14) > 4)
							throw new Exception("");
						vo.setCNR_DISCOUNT(str14);
					} catch (Exception e) {
						error3.add(str[0] + ":" + str[14]);
						continue;
					}
				}

				if (StringUtils.isBlank(str[15]) || StringUtils.equals(str[15],"$")) {
					vo.setRATE_DISCOUNT(info_vo != null ? info_vo.getRATE_DISCOUNT() : null);
				} else {
					try {
						BigDecimal str15 = new BigDecimal(str[15]);
						// NUMBER(6,2)
						if (getNumOfBigDecimal(str15) > 4)
							throw new Exception("");
						vo.setRATE_DISCOUNT(str15);
					} catch (Exception e) {
						error3.add(str[0] + ":" + str[15]);
						continue;
					}
				}

				if (StringUtils.isBlank(str[16]) || StringUtils.equals(str[16],"$")) {
					vo.setCNR_TARGET_SDATE(info_vo != null ? info_vo.getCNR_TARGET_SDATE() : null);
				} else {
					try {
						vo.setCNR_TARGET_SDATE(new Timestamp(sdf.parse(str[16]).getTime()));
					} catch (Exception e) {
						try {
							vo.setCNR_TARGET_SDATE(new Timestamp(sdf2.parse(str[16]).getTime()));
						} catch (Exception e2) {
							error3.add(str[0] + ":" + str[16]);
							continue;
						}
					}
				}

				if (StringUtils.isBlank(str[17]) || StringUtils.equals(str[17],"$")) {
					vo.setCNR_TARGET_EDATE(info_vo != null ? info_vo.getCNR_TARGET_EDATE() : null);
				} else {
					try {
						vo.setCNR_TARGET_EDATE(new Timestamp(sdf.parse(str[17]).getTime()));
					} catch (Exception e) {
						try {
							vo.setCNR_TARGET_EDATE(new Timestamp(sdf2.parse(str[17]).getTime()));
						} catch (Exception e2) {
							error3.add(str[0] + ":" + str[17]);
							continue;
						}
					}
				}

				if (StringUtils.isBlank(str[18]) || StringUtils.equals(str[18],"$")) {
					vo.setCNR_FEE(info_vo != null ? info_vo.getCNR_FEE() : null);
				} else {
					try {
						vo.setCNR_FEE(new BigDecimal(str[18]));
					} catch (Exception e) {
						error3.add(str[0] + ":" + str[18]);
						continue;
					}
				}
				
				// 專案代碼1
				if (StringUtils.isBlank(str[24]) || StringUtils.equals(str[24],"$")) {
					vo.setPROJECT1(fund_vo != null ? fund_vo.getPROJECT1() : null);
				} else {
					if (StringUtils.equals((str[24].trim()), "0")) {
						vo.setPROJECT1(null);
					} else if (str[24].trim().length() > 2) {
						error3.add(str[0] + ":" + str[24]);
						continue;
					}  else {
						vo.setPROJECT1(str[24].trim());
					}
				}
				// 專案代碼2
				if (StringUtils.isBlank(str[25]) || StringUtils.equals(str[25],"$")) {
					vo.setPROJECT2(fund_vo != null ? fund_vo.getPROJECT2() : null);
				} else {
					if (StringUtils.equals((str[25].trim()), "0")) {
						vo.setPROJECT2(null);
					} else if (str[25].trim().length() > 2) {
						error3.add(str[0] + ":" + str[25]);
						continue;
					}  else {
						vo.setPROJECT2(str[25].trim());
					}
				}
				// 客群代碼
				if (StringUtils.isBlank(str[26]) || StringUtils.equals(str[26],"$")) {
					vo.setCUSTOMER_LEVEL(fund_vo != null ? fund_vo.getCUSTOMER_LEVEL() : null);
				} else {
					if (StringUtils.equals((str[26].trim()), "0")) {
						vo.setCUSTOMER_LEVEL(null);
					} else if (str[26].trim().length() > 20) {
						error3.add(str[0] + ":" + str[26]);
						continue;
					}  else {
						vo.setCUSTOMER_LEVEL(str[26].trim());
					}
				}

				if (!exist)
					vo.setACT_TYPE("A");
				else
					vo.setACT_TYPE("M");
				vo.setREVIEW_STATUS("W");
				dam.create(vo);

				if (StringUtils.isNotBlank(str[19])) {
					String stock_bond_type = str[19].trim().toUpperCase();
					if ("S".equals(stock_bond_type) || "B".equals(stock_bond_type) || "$".equals(stock_bond_type)) {
						addReviewStockBondType(str[0].trim(), stock_bond_type,info_vo);
					} else {
						throw new Exception("股債類型輸入格式有誤，請輸入S或B。");
					}
				} else {
					throw new Exception("股債類型為必填欄位。");
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

	/*
	 * #1072 原為updateWarning() #1339 改為updateTBPRD_FUND() 2022.10.28
	 */
	public void updateTBPRD_FUND(Object body, IPrimitiveMap header) throws Exception {
		PRD230InputVO inputVO = (PRD230InputVO) body;
		PRD230OutputVO return_VO = new PRD230OutputVO();
		dam = this.getDataAccessManager();

		// check again
		// TBPRD_FUND
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT PRD_ID, WARNING FROM TBPRD_FUND where PRD_ID = :id ");
		queryCondition.setObject("id", inputVO.getPrd_id());
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		if (list.size() == 0)
			throw new APException("ehl_01_common_026");

		TBPRD_FUNDVO fundVO = (TBPRD_FUNDVO) dam.findByPKey(TBPRD_FUNDVO.TABLE_UID, inputVO.getPrd_id());

		// 警語
		if (StringUtils.isNotBlank(inputVO.getWarning())) {
			fundVO.setWARNING(inputVO.getWarning());
		} else {
			fundVO.setWARNING(null);
		}
		// 主題代碼1
		if (StringUtils.isNotBlank(inputVO.getFund_subject1())) {
			fundVO.setSUBJECT1(inputVO.getFund_subject1());
		} else {
			fundVO.setSUBJECT1(null);
		}
		// 主題代碼2
		if (StringUtils.isNotBlank(inputVO.getFund_subject2())) {
			fundVO.setSUBJECT2(inputVO.getFund_subject2());
		} else {
			fundVO.setSUBJECT2(null);
		}
		// 主題代碼3
		if (StringUtils.isNotBlank(inputVO.getFund_subject3())) {
			fundVO.setSUBJECT3(inputVO.getFund_subject3());
		} else {
			fundVO.setSUBJECT3(null);
		}

		dam.update(fundVO);

		this.sendRtnObject(return_VO);
	}

	/*
	 * #1404 改用delete insert
	 */
	public void uploadWarning(Object body, IPrimitiveMap header) throws Exception {
		PRD230InputVO inputVO = (PRD230InputVO) body;
		PRD230OutputVO return_VO = new PRD230OutputVO();
		dam = this.getDataAccessManager();
		// 先清空
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		// 2354
		sql.append("delete TBSYSPARAMETER where PARAM_TYPE = :PARAM_TYPE_C OR PARAM_TYPE = :PARAM_TYPE_E ");
		queryCondition.setObject("PARAM_TYPE_C", "PRD.FUND_C_ALERT");
		queryCondition.setObject("PARAM_TYPE_E", "PRD.FUND_E_ALERT");
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
						if (!"警語代碼".equals(str[0].trim()))
							throw new Exception(str[0]);
							//2354
						else if (!"中文警語訊息".equals(str[1].trim()))
							throw new Exception(str[1]);
						else if (!"英文警語訊息".equals(str[2].trim()))
							throw new Exception(str[2]);
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

				// 2354
				if (StringUtils.isBlank(str[1])){
					error.add(Integer.toString(i+1));
					continue;
				} else if (StringUtils.isBlank(str[2])){
					error2.add(Integer.toString(i+1));
					continue;
				}

				dataUpdate("PRD.FUND_C_ALERT", str[0].trim(), str[1].trim());
				dataUpdate("PRD.FUND_E_ALERT", str[0].trim(), str[2].trim());
			}
		}
		return_VO.setErrorList(error);
		return_VO.setErrorList2(error2);
		return_VO.setErrorList3(error3);
		return_VO.setErrorList4(error4);
		return_VO.setErrorList5(error5);
		this.sendRtnObject(return_VO);
	}

	// 2354
	private void dataUpdate(String param_type, String param_code, String param_name) throws Exception {
		TBSYSPARAMETERPK parameterPK = new TBSYSPARAMETERPK();
		parameterPK.setPARAM_TYPE(param_type);
		parameterPK.setPARAM_CODE(param_code);
		TBSYSPARAMETERVO parameterVO = new TBSYSPARAMETERVO();
		parameterVO.setcomp_id(parameterPK);
		parameterVO.setPARAM_NAME(param_name);
		parameterVO.setPARAM_NAME_EDIT(param_name);
		StringBuffer sql = new StringBuffer();
		sql.append("select max(PARAM_ORDER) as COUNT from TBSYSPARAMETER where PARAM_TYPE = :PARAM_TYPE");
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		queryCondition.setObject("PARAM_TYPE", param_type);
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

	/*
	 * 2022.11.22 #1339 上傳主題參數 改成delete insert
	 */
	public void uploadSubject(Object body, IPrimitiveMap header) throws Exception {
		PRD230InputVO inputVO = (PRD230InputVO) body;
		PRD230OutputVO return_VO = new PRD230OutputVO();

		dam = this.getDataAccessManager();
		// 先清空
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("delete TBSYSPARAMETER where PARAM_TYPE = :PARAM_TYPE ");
		queryCondition.setObject("PARAM_TYPE", "PRD.FUND_SUBJECT");
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
						if (!"主題代碼".equals(str[0].trim()))
							throw new Exception(str[0]);
						else if (!"主題名稱".equals(str[1].trim()))
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
//				queryCondition.setObject("PARAM_TYPE", "PRD.FUND_SUBJECT");
//				queryCondition.setObject("PARAM_CODE", str[0].trim());
//				queryCondition.setQueryString(sql.toString());
//				List<Map<String, Object>> list = dam.exeQuery(queryCondition);
				TBSYSPARAMETERPK parameterPK = new TBSYSPARAMETERPK();
				parameterPK.setPARAM_TYPE("PRD.FUND_SUBJECT");
				parameterPK.setPARAM_CODE(str[0].trim());
				TBSYSPARAMETERVO parameterVO = new TBSYSPARAMETERVO();
				parameterVO.setcomp_id(parameterPK);
				parameterVO.setPARAM_NAME(str[1].trim());
				parameterVO.setPARAM_NAME_EDIT(str[1].trim());

				sql = new StringBuffer();
				sql.append("select max(PARAM_ORDER) as COUNT from TBSYSPARAMETER where PARAM_TYPE = :PARAM_TYPE");
				queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				queryCondition.setObject("PARAM_TYPE", "PRD.FUND_SUBJECT");
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
	 * 2022.11.22 #1339 上傳專案參數 改成delete insert
	 */
	public void uploadProject(Object body, IPrimitiveMap header) throws Exception {
		PRD230InputVO inputVO = (PRD230InputVO) body;
		PRD230OutputVO return_VO = new PRD230OutputVO();

		dam = this.getDataAccessManager();
		// 先清空
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("delete TBSYSPARAMETER where PARAM_TYPE = :PARAM_TYPE ");
		queryCondition.setObject("PARAM_TYPE", "PRD.FUND_PROJECT");
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
				parameterPK.setPARAM_TYPE("PRD.FUND_PROJECT");
				parameterPK.setPARAM_CODE(str[0].trim());
				TBSYSPARAMETERVO parameterVO = new TBSYSPARAMETERVO();
				parameterVO.setcomp_id(parameterPK);
				parameterVO.setPARAM_NAME(str[1].trim());
				parameterVO.setPARAM_NAME_EDIT(str[1].trim());

				sql = new StringBuffer();
				sql.append("select max(PARAM_ORDER) as COUNT from TBSYSPARAMETER where PARAM_TYPE = :PARAM_TYPE");
				queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				queryCondition.setObject("PARAM_TYPE", "PRD.FUND_PROJECT");
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
	 * 2023.01.30 #1404 上傳客群參數 使用delete insert
	 */
	public void uploadCustomerLevel(Object body, IPrimitiveMap header) throws Exception {
		PRD230InputVO inputVO = (PRD230InputVO) body;
		PRD230OutputVO return_VO = new PRD230OutputVO();

		dam = this.getDataAccessManager();
		// 先清空
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("delete TBSYSPARAMETER where PARAM_TYPE = :PARAM_TYPE ");
		queryCondition.setObject("PARAM_TYPE", "PRD.FUND_CUSTOMER_LEVEL");
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
				parameterPK.setPARAM_TYPE("PRD.FUND_CUSTOMER_LEVEL");
				parameterPK.setPARAM_CODE(str[0].trim());
				TBSYSPARAMETERVO parameterVO = new TBSYSPARAMETERVO();
				parameterVO.setcomp_id(parameterPK);
				parameterVO.setPARAM_NAME(str[1].trim());
				parameterVO.setPARAM_NAME_EDIT(str[1].trim());

				sql = new StringBuffer();
				sql.append("select max(PARAM_ORDER) as COUNT from TBSYSPARAMETER where PARAM_TYPE = :PARAM_TYPE");
				queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				queryCondition.setObject("PARAM_TYPE", "PRD.FUND_CUSTOMER_LEVEL");
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

	public void downloadSimple(Object body, IPrimitiveMap header) throws Exception {
		this.notifyClientToDownloadFile("doc//PRD//PRD230_EXAMPLE.csv", "上傳指定商品代碼範例.csv");
	}

	public void downloadWarningSimple(Object body, IPrimitiveMap header) throws Exception {
		this.notifyClientToDownloadFile("doc//PRD//PRD230_WARNING_EXAMPLE.csv", "上傳警語範例.csv");
	}

	public void downloadSubjectSimple(Object body, IPrimitiveMap header) throws Exception {
		this.notifyClientToDownloadFile("doc//PRD//PRD230_SUBJECT_EXAMPLE.csv", "上傳主題代碼範例.csv");
	}

	public void downloadProjectSimple(Object body, IPrimitiveMap header) throws Exception {
		this.notifyClientToDownloadFile("doc//PRD//PRD230_PROJECT_EXAMPLE.csv", "上傳專案代碼範例.csv");
	}
	public void downloadCustomerLevelSimple(Object body, IPrimitiveMap header) throws Exception {
		this.notifyClientToDownloadFile("doc//PRD//PRD230_CUSTOMER_LEVEL_EXAMPLE.csv", "上傳客群代碼範例.csv");
	}

	public void downloadWarning(Object body, IPrimitiveMap header) throws Exception {

		PRD230OutputVO return_VO = new PRD230OutputVO();
		dam = this.getDataAccessManager();

		// getRoleList
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		// sql.append("select a.PARAM_CODE, a.PARAM_NAME, NVL((select PARAM_NAME from tbsysparameter where PARAM_TYPE = :PARAM_TYPE_E and PARAM_CODE = a.PARAM_CODE),'123') as PARAM_NAME_2 from tbsysparameter a where a.PARAM_TYPE = :PARAM_TYPE_C order by a.param_order");
		sql.append("SELECT * FROM (SELECT param_order,PARAM_CODE, PARAM_NAME, ROW_NUMBER() OVER (PARTITION BY PARAM_CODE ORDER BY PARAM_NAME desc) AS row_num "+
		"FROM tbsysparameter where PARAM_TYPE = :PARAM_TYPE_C or PARAM_TYPE = :PARAM_TYPE_E) "+
		"PIVOT (MAX(PARAM_NAME) FOR row_num IN (1 AS PARAM_NAME, 2 AS PARAM_NAME_2)) ORDER BY param_order");
		queryCondition.setObject("PARAM_TYPE_C", "PRD.FUND_C_ALERT");
		queryCondition.setObject("PARAM_TYPE_E", "PRD.FUND_E_ALERT");
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		if (list.size() > 0) {
			// gen csv
			String fileName = "警語.csv";
			List listCSV = new ArrayList();
			for (Map<String, Object> map : list) {
				// 24 column
				String[] records = new String[3];
				int i = 0;

				records[i] = checkIsNull(map, "PARAM_CODE");
				records[++i] = checkIsNull(map, "PARAM_NAME");
				records[++i] = checkIsNull(map, "PARAM_NAME_2");

				listCSV.add(records);
			}
			// header
			String[] csvHeader = new String[3];
			int j = 0;
			csvHeader[j] = "警語代碼";
			//2354
			csvHeader[++j] = "中文警語訊息";
			csvHeader[++j] = "英文警語訊息";

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

	public void downloadSubject(Object body, IPrimitiveMap header) throws Exception {

		PRD230OutputVO return_VO = new PRD230OutputVO();
		dam = this.getDataAccessManager();

		// getRoleList
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("select PARAM_CODE, PARAM_NAME from tbsysparameter where PARAM_TYPE = :PARAM_TYPE order by param_order ");
		queryCondition.setObject("PARAM_TYPE", "PRD.FUND_SUBJECT");
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		if (list.size() > 0) {
			// gen csv
			String fileName = "主題.csv";
			List listCSV = new ArrayList();
			for (Map<String, Object> map : list) {
				// 24 column
				String[] records = new String[2];
				int i = 0;

				records[i] = checkIsNull(map, "PARAM_CODE");
				records[++i] = checkIsNull(map, "PARAM_NAME");

				listCSV.add(records);
			}
			// header
			String[] csvHeader = new String[2];
			int j = 0;
			csvHeader[j] = "主題代碼";
			csvHeader[++j] = "主題名稱";

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

	/*
	 * #1404: 給其他交易模組共用下載
	 */
	public void downloadProject(Object body, IPrimitiveMap header) throws Exception {
		PRD230InputVO inputVO = (PRD230InputVO) body;
		PRD230OutputVO return_VO = new PRD230OutputVO();
		dam = this.getDataAccessManager();

		// getRoleList
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("select PARAM_CODE, PARAM_NAME from tbsysparameter where PARAM_TYPE = :PARAM_TYPE order by param_order ");
		queryCondition.setObject("PARAM_TYPE", inputVO.getDownloadParamType());
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		if (list.size() > 0) {
			// gen csv
			String fileName = "專案.csv";
			List listCSV = new ArrayList();
			for (Map<String, Object> map : list) {
				// 24 column
				String[] records = new String[2];
				int i = 0;

				records[i] = checkIsNull(map, "PARAM_CODE");
				records[++i] = checkIsNull(map, "PARAM_NAME");

				listCSV.add(records);
			}
			// header
			String[] csvHeader = new String[2];
			int j = 0;
			csvHeader[j] = "專案代碼";
			csvHeader[++j] = "專案名稱";

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
	
	/*
	 * #1404: 給其他模組共用下載參數
	 */
	public void downloadCustomerLevel(Object body, IPrimitiveMap header) throws Exception {
		PRD230InputVO inputVO = (PRD230InputVO) body;
		PRD230OutputVO return_VO = new PRD230OutputVO();
		dam = this.getDataAccessManager();

		// getRoleList
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("select PARAM_CODE, PARAM_NAME from tbsysparameter where PARAM_TYPE = :PARAM_TYPE order by param_order ");
		queryCondition.setObject("PARAM_TYPE", inputVO.getDownloadParamType());
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		if (list.size() > 0) {
			// gen csv
			String fileName = "客群.csv";
			List listCSV = new ArrayList();
			for (Map<String, Object> map : list) {
				// 24 column
				String[] records = new String[2];
				int i = 0;

				records[i] = checkIsNull(map, "PARAM_CODE");
				records[++i] = checkIsNull(map, "PARAM_NAME");

				listCSV.add(records);
			}
			// header
			String[] csvHeader = new String[2];
			int j = 0;
			csvHeader[j] = "客群代碼";
			csvHeader[++j] = "客群名稱";

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
	/*
	 * TBPRD_FUND 相關欄位處理 
	 * #1072 警語維護 
	 * #1339 標籤 2022.10.28
	 * #1404 客群 2023.01.30
		 */
	public void updateTags(String[] str, ArrayList<String> error6) throws NotFoundException, DAOException {
		dam = this.getDataAccessManager();
		TBPRD_FUNDVO fundVO = (TBPRD_FUNDVO) dam.findByPKey(TBPRD_FUNDVO.TABLE_UID, str[0].trim());
		// 警語
		if (StringUtils.isNotBlank(str[20])) {
			if (StringUtils.equals((str[20].trim()), "0")) {
				fundVO.setWARNING(null);
			} else if(StringUtils.equals(str[20].trim(), "$")) {
				
			} else if (str[20].trim().length() > 3) {
				error6.add(str[0] + ":" + str[20]);
				return;
			} else {
				fundVO.setWARNING(str[20].trim());
			}
		}
		// 主題代碼1
		if (StringUtils.isNotBlank(str[21])) {
			if (StringUtils.equals((str[21].trim()), "0")) {
				fundVO.setSUBJECT1(null);
			} else if(StringUtils.equals(str[21].trim(), "$")) {
				
			}  else if (str[21].trim().length() > 2) {
				error6.add(str[0] + ":" + str[21]);
				return;
			}  else {
				fundVO.setSUBJECT1(str[21].trim());
			}
		}
		// 主題代碼2
		if (StringUtils.isNotBlank(str[22])) {
			if (StringUtils.equals((str[22].trim()), "0")) {
				fundVO.setSUBJECT2(null);
			} else if(StringUtils.equals(str[22].trim(), "$")) {
				
			}   else if (str[22].trim().length() > 2) {
				error6.add(str[0] + ":" + str[22]);
				return;
			}  else {
				fundVO.setSUBJECT2(str[22].trim());
			}
		}
		// 主題代碼3
		if (StringUtils.isNotBlank(str[23])) {
			if (StringUtils.equals((str[23].trim()), "0")) {
				fundVO.setSUBJECT3(null);
			} else if(StringUtils.equals(str[23].trim(), "$")) {
				
			}   else if (str[23].trim().length() > 2) {
				error6.add(str[0] + ":" + str[23]);
				return;
			}  else {
				fundVO.setSUBJECT3(str[23].trim());
			}
		}
		try {
			dam.update(fundVO);
		} catch (Exception e) {
			error6.add(str[0] + ":" + str[20]);
		}

	}

}