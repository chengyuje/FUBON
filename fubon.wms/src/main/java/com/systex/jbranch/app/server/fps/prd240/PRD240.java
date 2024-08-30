package com.systex.jbranch.app.server.fps.prd240;

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
import java.util.TreeMap;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.common.fps.table.TBPRD_ETFVO;
import com.systex.jbranch.app.common.fps.table.TBPRD_ETF_REVIEWVO;
import com.systex.jbranch.app.common.fps.table.TBPRD_RANKVO;
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
 * prd240
 * #1404 專案、客群標籤
 * 下載and下載範例 JS端呼叫PRD230功能
 *
 * @author moron
 * @date 2016/08/24
 * @spec null
 */
@Component("prd240")
@Scope("request")
public class PRD240 extends FubonWmsBizLogic {
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(PRD240.class);

	public void etf_inquire(Object body, IPrimitiveMap header) throws JBranchException {
		WorkStation ws = DataManager.getWorkStation(uuid);
		PRD240InputVO inputVO = (PRD240InputVO) body;
		PRD240OutputVO return_VO = new PRD240OutputVO();
		dam = this.getDataAccessManager();

		// getRoleList
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT COUNT(ROLEID) AS COUNTS FROM TBSYSSECUROLPRIASS ");
		sql.append("WHERE PRIVILEGEID IN (SELECT PRIVILEGEID FROM TBSYSSECUPRIFUNMAP WHERE ITEMID = 'PRD240' AND FUNCTIONID = 'confirm') AND ROLEID = :roleID ");
		queryCondition.setObject("roleID", getUserVariable(FubonSystemVariableConsts.LOGINROLE));
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> privilegeCount = dam.exeQuery(queryCondition);
		//
		sql = new StringBuffer();
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sql.append("SELECT STOCK_BOND_TYPE, SEQ,STOCK_CODE,PRD_ID,ETF_CNAME,CNR_YIELD,CNR_MULTIPLE,MULTIPLE_SDATE,MULTIPLE_EDATE,INV_TARGET,ACT_TYPE,REVIEW_STATUS,CREATOR,PROJECT,CUSTOMER_LEVEL FROM ( ");
		// 覆核人員
		if (((BigDecimal) privilegeCount.get(0).get("COUNTS")).compareTo(new BigDecimal(0)) > 0) {
			sql.append("SELECT rw.STOCK_BOND_TYPE, rw.SEQ,rw.STOCK_CODE,rw.PRD_ID,etf.ETF_CNAME,rw.CNR_YIELD,rw.CNR_MULTIPLE,rw.MULTIPLE_SDATE,rw.MULTIPLE_EDATE,rw.INV_TARGET,rw.ACT_TYPE,rw.REVIEW_STATUS,rw.CREATOR, rw.PROJECT, rw.CUSTOMER_LEVEL ");
			sql.append("FROM TBPRD_ETF_REVIEW rw left join TBPRD_ETF etf on rw.PRD_ID = etf.PRD_ID ");
			sql.append("WHERE rw.REVIEW_STATUS = 'W' ");
			if(!StringUtils.equals("HOME", inputVO.getPassParams())) {
				sql.append("UNION ");
				sql.append("SELECT etf.STOCK_BOND_TYPE, null as SEQ,etf.STOCK_CODE,etf.PRD_ID,etf.ETF_CNAME,etf.CNR_YIELD,etf.CNR_MULTIPLE,etf.MULTIPLE_SDATE,etf.MULTIPLE_EDATE,etf.INV_TARGET,rw.ACT_TYPE,rw.REVIEW_STATUS,null as CREATOR, etf.PROJECT, etf.CUSTOMER_LEVEL ");
				sql.append("FROM TBPRD_ETF etf ");
				sql.append("left join TBPRD_ETF_REVIEW rw on rw.PRD_ID = etf.PRD_ID and rw.REVIEW_STATUS = 'W' ");
				sql.append("WHERE NVL(etf.REVIEW_STATUS, 'Y') = 'Y' ");
				sql.append("AND etf.PRD_ID NOT IN (SELECT PRD_ID FROM TBPRD_ETF_REVIEW WHERE REVIEW_STATUS = 'W') ");
			}
		}
		else {
			sql.append("SELECT rw.STOCK_BOND_TYPE, rw.SEQ,rw.STOCK_CODE,rw.PRD_ID,etf.ETF_CNAME,rw.CNR_YIELD,rw.CNR_MULTIPLE,rw.MULTIPLE_SDATE,rw.MULTIPLE_EDATE,rw.INV_TARGET,rw.ACT_TYPE,rw.REVIEW_STATUS,null as CREATOR, rw.PROJECT, rw.CUSTOMER_LEVEL ");
			sql.append("FROM TBPRD_ETF_REVIEW rw left join TBPRD_ETF etf on rw.PRD_ID = etf.PRD_ID ");
			sql.append("WHERE rw.REVIEW_STATUS = 'W' ");
			sql.append("AND rw.CREATOR = :creator ");
			sql.append("UNION ");
			sql.append("SELECT etf.STOCK_BOND_TYPE, null as SEQ,etf.STOCK_CODE,etf.PRD_ID,etf.ETF_CNAME,etf.CNR_YIELD,etf.CNR_MULTIPLE,etf.MULTIPLE_SDATE,etf.MULTIPLE_EDATE,etf.INV_TARGET,rw.ACT_TYPE,rw.REVIEW_STATUS,null as CREATOR, etf.PROJECT, etf.CUSTOMER_LEVEL ");
			sql.append("FROM TBPRD_ETF etf ");
			sql.append("left join TBPRD_ETF_REVIEW rw on rw.PRD_ID = etf.PRD_ID and rw.REVIEW_STATUS = 'W' ");
			sql.append("WHERE NVL(etf.REVIEW_STATUS, 'Y') = 'Y' ");
			sql.append("AND etf.PRD_ID NOT IN (SELECT PRD_ID FROM TBPRD_ETF_REVIEW WHERE REVIEW_STATUS = 'W' AND CREATOR = :creator) ");
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

	public void etf_checkID(Object body, IPrimitiveMap header) throws JBranchException {
		PRD240InputVO inputVO = (PRD240InputVO) body;
		PRD240OutputVO return_VO = new PRD240OutputVO();
		dam = this.getDataAccessManager();

		// update
		if(StringUtils.equals("Y", inputVO.getStatus())) {
			// TBPRD_ETF
			QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sql = new StringBuffer();
			sql.append("SELECT PRD_ID,ETF_CNAME FROM TBPRD_ETF where PRD_ID = :id ");
			queryCondition.setObject("id", inputVO.getPrd_id());
			queryCondition.setQueryString(sql.toString());
			List<Map<String, Object>> list = dam.exeQuery(queryCondition);
			if (list.size() > 0) {
				return_VO.setEtf_name(ObjectUtils.toString(list.get(0).get("ETF_CNAME")));
				return_VO.setCanEdit(true);
			}
			else {
				return_VO.setCanEdit(false);
				return_VO.setErrorMsg("ehl_01_common_026");
			}

			// TBPRD_ETF_REVIEW
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sql = new StringBuffer();
			sql.append("SELECT PRD_ID FROM TBPRD_ETF_REVIEW where PRD_ID = :id and REVIEW_STATUS = 'W' ");
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
			// TBPRD_ETF 有限制可申購
			QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sql = new StringBuffer();
//			sql.append("SELECT PRD_ID, ETF_CNAME, RISKCATE_ID, CURRENCY_STD_ID FROM TBPRD_ETF where PRD_ID = :id and IS_SALE is null");
			sql.append("SELECT PRD_ID, ETF_CNAME, RISKCATE_ID, CURRENCY_STD_ID, STOCK_BOND_TYPE FROM TBPRD_ETF ");
			sql.append("where PRD_ID = :id and IS_SALE is null ");

			if(StringUtils.isNotBlank(inputVO.getStock_bond_type())) {
				sql.append("AND STOCK_BOND_TYPE = :stock_bond_type ");
				queryCondition.setObject("stock_bond_type", inputVO.getStock_bond_type());
			}

			queryCondition.setObject("id", inputVO.getPrd_id());
			queryCondition.setQueryString(sql.toString());
			List<Map<String, Object>> list = dam.exeQuery(queryCondition);
			if (list.size() > 0) {
				if (StringUtils.isBlank(ObjectUtils.toString(list.get(0).get("STOCK_BOND_TYPE")))) {
					return_VO.setCanEdit(false);
					return_VO.setErrorMsg(inputVO.getPrd_id() + "：該商品尚未維護股債類型，故不可設為主推商品。");
				}
				return_VO.setEtf_name(ObjectUtils.toString(list.get(0).get("ETF_CNAME")));
				return_VO.setRick_id(ObjectUtils.toString(list.get(0).get("RISKCATE_ID")));
				return_VO.setCurrency(ObjectUtils.toString(list.get(0).get("CURRENCY_STD_ID")));
				return_VO.setCanEdit(true);
			}
			else
				return_VO.setCanEdit(false);
		}
		// add
		else {
			// no add
		}

		this.sendRtnObject(return_VO);
	}

//	public void addData(Object body, IPrimitiveMap header) throws JBranchException {
//		PRD240InputVO inputVO = (PRD240InputVO) body;
//		dam = this.getDataAccessManager();
//
//		// check again
//		// TBPRD_ETF
//		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
//		StringBuffer sql = new StringBuffer();
//		sql.append("SELECT PRD_ID FROM TBPRD_ETF where PRD_ID = :id ");
//		queryCondition.setObject("id", inputVO.getPrd_id());
//		queryCondition.setQueryString(sql.toString());
//		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
//		if (list.size() > 0)
//			throw new APException("ehl_01_common_026");
//		// TBPRD_ETF_REVIEW
//		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
//		sql = new StringBuffer();
//		sql.append("SELECT PRD_ID FROM TBPRD_ETF_REVIEW where PRD_ID = :id and REVIEW_STATUS = 'W' ");
//		queryCondition.setObject("id", inputVO.getPrd_id());
//		queryCondition.setQueryString(sql.toString());
//		List<Map<String, Object>> list2 = dam.exeQuery(queryCondition);
//		if (list2.size() > 0)
//			throw new APException("ehl_01_common_026");
//
//		// seq
//		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
//		sql = new StringBuffer();
//		sql.append("SELECT SQ_TBPRD_ETF_REVIEW.nextval AS SEQ FROM DUAL ");
//		queryCondition.setQueryString(sql.toString());
//		List<Map<String, Object>> SEQLIST = dam.exeQuery(queryCondition);
//		BigDecimal seqNo = (BigDecimal) SEQLIST.get(0).get("SEQ");
//		// add TBPRD_ETF_REVIEW
//		TBPRD_ETF_REVIEWVO vo = new TBPRD_ETF_REVIEWVO();
//		vo.setSEQ(seqNo);
////		vo.setSTOCK_CODE(STOCK_CODE);
//		vo.setPRD_ID(inputVO.getPrd_id());
//		if(StringUtils.isNotBlank(inputVO.getCnr_mult()))
//			vo.setCNR_MULTIPLE(new BigDecimal(inputVO.getCnr_mult()));
//		vo.setACT_TYPE("A");
//		vo.setREVIEW_STATUS("W");
//		dam.create(vo);
//		this.sendRtnObject(null);
//	}

	private void addReviewStockBondType (String prd_id, String stock_bond_type) throws JBranchException {
		addReviewStockBondType(prd_id,stock_bond_type,null);
	}

	private void addStockBondType (String prd_id, String stock_bond_type) throws JBranchException {
		List<Map<String,Object>> resultList = new ArrayList<Map<String,Object>>();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();

		sql.append("SELECT PRD_ID FROM TBPRD_ETF where PRD_ID = :prd_id ");

		queryCondition.setObject("prd_id", prd_id);
		queryCondition.setQueryString(sql.toString());
		resultList = dam.exeQuery(queryCondition);

		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		if(resultList.size() > 0) {
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sql = new StringBuffer();
			sql.append("UPDATE TBPRD_ETF SET STOCK_BOND_TYPE = :stock_bond_type WHERE PRD_ID = :prd_id ");

			queryCondition.setObject("stock_bond_type", stock_bond_type);
			queryCondition.setObject("prd_id", prd_id);
			queryCondition.setQueryString(sql.toString());
			dam.exeUpdate(queryCondition);
		}
	}

	public void etf_editData(Object body, IPrimitiveMap header) throws JBranchException {
		PRD240InputVO inputVO = (PRD240InputVO) body;
		dam = this.getDataAccessManager();

		// check again
		// TBPRD_ETF_REVIEW
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT PRD_ID FROM TBPRD_ETF_REVIEW where PRD_ID = :id and REVIEW_STATUS = 'W' ");
		queryCondition.setObject("id", inputVO.getPrd_id());
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		if (list.size() > 0)
			throw new APException("ehl_01_common_028");

		// seq
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sql = new StringBuffer();
		sql.append("SELECT SQ_TBPRD_ETF_REVIEW.nextval AS SEQ FROM DUAL ");
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> SEQLIST = dam.exeQuery(queryCondition);
		BigDecimal seqNo = (BigDecimal) SEQLIST.get(0).get("SEQ");
		// add TBPRD_ETF_REVIEW
		TBPRD_ETF_REVIEWVO vo = new TBPRD_ETF_REVIEWVO();
		vo.setSEQ(seqNo);
		vo.setPRD_ID(inputVO.getPrd_id());
		if(StringUtils.isNotBlank(inputVO.getYield()))
			vo.setCNR_YIELD(new BigDecimal(inputVO.getYield()));
		else
			vo.setCNR_YIELD(null);
		if(StringUtils.isNotBlank(inputVO.getCnr_mult()))
			vo.setCNR_MULTIPLE(new BigDecimal(inputVO.getCnr_mult()));
		else
			vo.setCNR_MULTIPLE(null);
		if(inputVO.getMulti_sDate() != null)
			vo.setMULTIPLE_SDATE(new Timestamp(inputVO.getMulti_sDate().getTime()));
		else
			vo.setMULTIPLE_SDATE(null);
		if(inputVO.getMulti_eDate() != null)
			vo.setMULTIPLE_EDATE(new Timestamp(inputVO.getMulti_eDate().getTime()));
		else
			vo.setMULTIPLE_EDATE(null);
		if(inputVO.getEtf_project() != null)
			vo.setPROJECT(inputVO.getEtf_project());
		else
			vo.setPROJECT(null);
		if(inputVO.getEtf_customer_level() != null)
			vo.setCUSTOMER_LEVEL(inputVO.getEtf_customer_level());
		else
			vo.setCUSTOMER_LEVEL(null);
		vo.setINV_TARGET(inputVO.getInv_target());
		vo.setACT_TYPE("M");
		vo.setREVIEW_STATUS("W");
		dam.create(vo);

		if(StringUtils.isNotBlank(inputVO.getStock_bond_type()))
			addReviewStockBondType(inputVO.getPrd_id(), inputVO.getStock_bond_type());

		this.sendRtnObject(null);
	}

	public void etf_deleteData(Object body, IPrimitiveMap header) throws JBranchException {
		PRD240InputVO inputVO = (PRD240InputVO) body;
		dam = this.getDataAccessManager();

		// check again
		// TBPRD_ETF_REVIEW
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		String prd_id = inputVO.getPrd_id();
		sql.append("SELECT PRD_ID FROM TBPRD_ETF_REVIEW where PRD_ID = :id and REVIEW_STATUS = 'W' ");
		queryCondition.setObject("id", prd_id);
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		if (list.size() > 0)
			throw new APException("ehl_01_common_028");

		// seq
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sql = new StringBuffer();
		sql.append("SELECT SQ_TBPRD_ETF_REVIEW.nextval AS SEQ FROM DUAL ");
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> SEQLIST = dam.exeQuery(queryCondition);
		BigDecimal seqNo = (BigDecimal) SEQLIST.get(0).get("SEQ");
		// get ori
		TBPRD_ETFVO vo = new TBPRD_ETFVO();
		vo = (TBPRD_ETFVO) dam.findByPKey(TBPRD_ETFVO.TABLE_UID, prd_id);
		if (vo != null) {
			// add TBPRD_ETF_REVIEW
			TBPRD_ETF_REVIEWVO rvo = new TBPRD_ETF_REVIEWVO();
			rvo.setSEQ(seqNo);
			rvo.setPRD_ID(vo.getPRD_ID());
			rvo.setCNR_YIELD(vo.getCNR_YIELD());
			rvo.setCNR_MULTIPLE(vo.getCNR_MULTIPLE());
			rvo.setMULTIPLE_SDATE(vo.getMULTIPLE_SDATE());
			rvo.setMULTIPLE_EDATE(vo.getMULTIPLE_EDATE());
			rvo.setINV_TARGET(vo.getINV_TARGET());
			rvo.setPROJECT(vo.getPROJECT());
			rvo.setCUSTOMER_LEVEL(vo.getCUSTOMER_LEVEL());
			rvo.setACT_TYPE("D");
			rvo.setREVIEW_STATUS("W");
			dam.create(rvo);

			list = new ArrayList<Map<String,Object>>();
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

			StringBuffer sb = new StringBuffer();
			sb.append("select STOCK_BOND_TYPE from TBPRD_ETF where PRD_ID = :prd_id ");

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

	public void etf_review(Object body, IPrimitiveMap header) throws JBranchException {
		PRD240InputVO inputVO = (PRD240InputVO) body;
		dam = this.getDataAccessManager();

		// 2017/2/21
		for(Map<String, Object> rmap : inputVO.getReview_list()) {
			TBPRD_ETF_REVIEWVO rvo = new TBPRD_ETF_REVIEWVO();
			BigDecimal seq = new BigDecimal(ObjectUtils.toString(rmap.get("SEQ")));
			rvo = (TBPRD_ETF_REVIEWVO) dam.findByPKey(TBPRD_ETF_REVIEWVO.TABLE_UID, seq);
			if (rvo != null) {
				// confirm
				if("Y".equals(inputVO.getStatus())) {
					// 新增
					if("A".equals(rvo.getACT_TYPE())) {
						// no add
					}
					// 修改
					else if("M".equals(rvo.getACT_TYPE())) {
						TBPRD_ETFVO vo = new TBPRD_ETFVO();
						vo = (TBPRD_ETFVO) dam.findByPKey(TBPRD_ETFVO.TABLE_UID, rvo.getPRD_ID());
						if (vo != null) {
							vo.setCNR_YIELD(rvo.getCNR_YIELD());
							vo.setCNR_MULTIPLE(rvo.getCNR_MULTIPLE());
							vo.setMULTIPLE_SDATE(rvo.getMULTIPLE_SDATE());
							vo.setMULTIPLE_EDATE(rvo.getMULTIPLE_EDATE());
							vo.setINV_TARGET(rvo.getINV_TARGET());
							vo.setPROJECT(rvo.getPROJECT());
							vo.setCUSTOMER_LEVEL(rvo.getCUSTOMER_LEVEL());
							vo.setACT_TYPE("M");
							vo.setREVIEW_STATUS("Y");
							dam.update(vo);

							List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
							QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

							StringBuffer sb = new StringBuffer();
							sb.append("select STOCK_BOND_TYPE from TBPRD_ETF_REVIEW where SEQ = :seq ");

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
						} else {
							// 顯示資料不存在
							throw new APException("ehl_01_common_001");
						}
					}
					// 刪除
					else if("D".equals(rvo.getACT_TYPE())) {
						TBPRD_ETFVO vo = new TBPRD_ETFVO();
						vo = (TBPRD_ETFVO) dam.findByPKey(TBPRD_ETFVO.TABLE_UID, rvo.getPRD_ID());
						if (vo != null) {
							dam.delete(vo);
						} else {
							// 顯示資料不存在
							throw new APException("ehl_01_common_001");
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

	public void etf_download(Object body, IPrimitiveMap header) throws JBranchException {
		WorkStation ws = DataManager.getWorkStation(uuid);
		PRD240InputVO inputVO = (PRD240InputVO) body;
		PRD240OutputVO return_VO = new PRD240OutputVO();
		XmlInfo xmlInfo = new XmlInfo();
		dam = this.getDataAccessManager();

		// getRoleList
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT COUNT(ROLEID) AS COUNTS FROM TBSYSSECUROLPRIASS ");
		sql.append("WHERE PRIVILEGEID IN (SELECT PRIVILEGEID FROM TBSYSSECUPRIFUNMAP WHERE ITEMID = 'PRD240' AND FUNCTIONID = 'confirm') AND ROLEID = :roleID ");
		queryCondition.setObject("roleID", getUserVariable(FubonSystemVariableConsts.LOGINROLE));
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> privilegeCount = dam.exeQuery(queryCondition);
		//
		sql = new StringBuffer();
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sql.append("SELECT STOCK_BOND_TYPE, STOCK_CODE,PRD_ID,ETF_CNAME,CNR_YIELD,CNR_MULTIPLE,MULTIPLE_SDATE,MULTIPLE_EDATE,INV_TARGET,ACT_TYPE,REVIEW_STATUS,CREATOR FROM ( ");
		// 覆核人員
		if (((BigDecimal) privilegeCount.get(0).get("COUNTS")).compareTo(new BigDecimal(0)) > 0) {
			sql.append("SELECT rw.STOCK_BOND_TYPE, rw.STOCK_CODE,rw.PRD_ID,etf.ETF_CNAME,rw.CNR_YIELD,rw.CNR_MULTIPLE,rw.MULTIPLE_SDATE,rw.MULTIPLE_EDATE,rw.INV_TARGET,rw.ACT_TYPE,rw.REVIEW_STATUS,rw.CREATOR ");
			sql.append("FROM TBPRD_ETF_REVIEW rw left join TBPRD_ETF etf on rw.PRD_ID = etf.PRD_ID ");
			sql.append("WHERE rw.REVIEW_STATUS = 'W' ");
			sql.append("UNION ");
			sql.append("SELECT etf.STOCK_BOND_TYPE, etf.STOCK_CODE,etf.PRD_ID,etf.ETF_CNAME,etf.CNR_YIELD,etf.CNR_MULTIPLE,etf.MULTIPLE_SDATE,etf.MULTIPLE_EDATE,etf.INV_TARGET,rw.ACT_TYPE,rw.REVIEW_STATUS,null as CREATOR ");
			sql.append("FROM TBPRD_ETF etf ");
			sql.append("left join TBPRD_ETF_REVIEW rw on rw.PRD_ID = etf.PRD_ID and rw.REVIEW_STATUS = 'W' ");
			sql.append("WHERE NVL(etf.REVIEW_STATUS, 'Y') = 'Y' ");
			sql.append("AND etf.PRD_ID NOT IN (SELECT PRD_ID FROM TBPRD_ETF_REVIEW WHERE REVIEW_STATUS = 'W') ");
		}
		else {
			sql.append("SELECT rw.STOCK_BOND_TYPE, rw.STOCK_CODE,rw.PRD_ID,etf.ETF_CNAME,rw.CNR_YIELD,rw.CNR_MULTIPLE,rw.MULTIPLE_SDATE,rw.MULTIPLE_EDATE,rw.INV_TARGET,rw.ACT_TYPE,rw.REVIEW_STATUS,null as CREATOR ");
			sql.append("FROM TBPRD_ETF_REVIEW rw left join TBPRD_ETF etf on rw.PRD_ID = etf.PRD_ID ");
			sql.append("WHERE rw.REVIEW_STATUS = 'W' ");
			sql.append("AND rw.CREATOR = :creator ");
			sql.append("UNION ");
			sql.append("SELECT etf.STOCK_BOND_TYPE, etf.STOCK_CODE,etf.PRD_ID,etf.ETF_CNAME,etf.CNR_YIELD,etf.CNR_MULTIPLE,etf.MULTIPLE_SDATE,etf.MULTIPLE_EDATE,etf.INV_TARGET,rw.ACT_TYPE,rw.REVIEW_STATUS,null as CREATOR ");
			sql.append("FROM TBPRD_ETF etf ");
			sql.append("left join TBPRD_ETF_REVIEW rw on rw.PRD_ID = etf.PRD_ID and rw.REVIEW_STATUS = 'W' ");
			sql.append("WHERE NVL(etf.REVIEW_STATUS, 'Y') = 'Y' ");
			sql.append("AND etf.PRD_ID NOT IN (SELECT PRD_ID FROM TBPRD_ETF_REVIEW WHERE REVIEW_STATUS = 'W' AND CREATOR = :creator) ");
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
			Map<String, String> mkt_tier3 = new HashMap(xmlInfo.doGetVariable("PRD.MKT_TIER3", FormatHelper.FORMAT_1));
			mkt_tier3.remove("033");mkt_tier3.remove("999");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			String fileName = "ETF清單_"+ sdf.format(new Date()) + "_" + ws.getUser().getUserID() + ".csv";
			List listCSV = new ArrayList();
			for (Map<String, Object> map : list) {
				// 9 column
				String[] records = new String[9];
				int i = 0;
				if ("W".equals(ObjectUtils.toString(map.get("REVIEW_STATUS"))))
					records[i] = "覆核中";
				else
					records[i] = "已覆核";
				records[++i] = checkIsNull(map, "PRD_ID");
				records[++i] = checkIsNull(map, "ETF_CNAME");
				records[++i] = checkIsNull(map, "CNR_YIELD");
				records[++i] = checkIsNull(map, "CNR_MULTIPLE");
				records[++i] = "=\"" + checkIsNull(map, "MULTIPLE_SDATE") + "\"";
				records[++i] = "=\"" + checkIsNull(map, "MULTIPLE_EDATE") + "\"";
				records[++i] = mkt_tier3.get(map.get("INV_TARGET"));

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
			String[] csvHeader = new String[9];
			int j = 0;
			csvHeader[j] = "覆核狀態";
			csvHeader[++j] = "ETF代碼";
			csvHeader[++j] = "ETF名稱";
			csvHeader[++j] = "CNR分配率";
			csvHeader[++j] = "CNR加減碼";
			csvHeader[++j] = "加碼區間起日";
			csvHeader[++j] = "加碼區間迄日";
			csvHeader[++j] = "投資標的(市場別)";
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

	public void etf_upload(Object body, IPrimitiveMap header) throws JBranchException {
		PRD240InputVO inputVO = (PRD240InputVO) body;
		PRD240OutputVO return_VO = new PRD240OutputVO();
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
			Map<String, String> mkt_tier3 = new HashMap(xmlInfo.doGetVariable("PRD.MKT_TIER3", FormatHelper.FORMAT_3));
			mkt_tier3.remove("033");mkt_tier3.remove("999");
			for(int i = 0;i < dataCsv.size();i++) {
				String[] str = dataCsv.get(i);
				if(i == 0) {
					try {
						if(!"商品代碼".equals(str[0].trim()))
							throw new Exception(str[0]);
						else if(!"CNR分配率".equals(str[1].trim()))
							throw new Exception(str[1]);
						else if(!"CNR加減碼".equals(str[2].trim()))
							throw new Exception(str[2]);
						else if(!"加碼區間起日".equals(str[3].substring(0, 6)))
							throw new Exception(str[3]);
						else if(!"加碼區間迄日".equals(str[4].substring(0, 6)))
							throw new Exception(str[4]);
						else if(!"投資標的".equals(str[5].substring(0, 4)))
							throw new Exception(str[5]);
						else if(!"股債類型".equals(str[6].substring(0, 4)))
							throw new Exception(str[6]);
						else if(!"專案代碼".equals(str[7].substring(0, 4)))
							throw new Exception(str[7]);
						else if(!"客群代碼".equals(str[8].substring(0, 4)))
							throw new Exception(str[8]);
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
				// TBPRD_ETF
				QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				StringBuffer sql = new StringBuffer();
				sql.append("SELECT PRD_ID FROM TBPRD_ETF where PRD_ID = :id ");
				queryCondition.setObject("id", str[0].trim());
				queryCondition.setQueryString(sql.toString());
				List<Map<String, Object>> list = dam.exeQuery(queryCondition);
				if (list.size() == 0) {
					error.add(str[0]);
					continue;
				}
//				dam.newTransactionExeMethod(this, "updateTagsIndependent", Arrays.asList(str, error6));

				// TBPRD_ETF_REVIEW
				queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sql = new StringBuffer();
				sql.append("SELECT PRD_ID FROM TBPRD_ETF_REVIEW where PRD_ID = :id and REVIEW_STATUS = 'W' ");
				queryCondition.setObject("id", str[0].trim());
				queryCondition.setQueryString(sql.toString());
				List<Map<String, Object>> list2 = dam.exeQuery(queryCondition);
				if (list2.size() > 0) {
					error2.add(str[0]);
					continue;
				}

				// seq
				queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sql = new StringBuffer();
				sql.append("SELECT SQ_TBPRD_INSINFO_REVIEW.nextval AS SEQ FROM DUAL ");
				queryCondition.setQueryString(sql.toString());
				List<Map<String, Object>> SEQLIST = dam.exeQuery(queryCondition);
				BigDecimal seqNo = (BigDecimal) SEQLIST.get(0).get("SEQ");
				// add TBPRD_ETF_REVIEW
				TBPRD_ETF_REVIEWVO vo = new TBPRD_ETF_REVIEWVO();
				TBPRD_ETFVO info_vo = new TBPRD_ETFVO();
				info_vo = (TBPRD_ETFVO) dam.findByPKey(TBPRD_ETFVO.TABLE_UID, str[0].trim());

				vo.setSEQ(seqNo);
				//
				if(utf_8_length(str[0]) > 10) {
					error3.add(str[0]);
					continue;
				}
				else
					vo.setPRD_ID(str[0].trim());
				//
				if(StringUtils.isNotBlank(str[1]) && !StringUtils.equals(str[1], "$")) {
					try {
						BigDecimal str1 = new BigDecimal(str[1]);
						// NUMBER(6,2)
						if(getNumOfBigDecimal(str1) > 4)
							throw new Exception("");
						vo.setCNR_YIELD(str1);
					} catch (Exception e) {
						error3.add(str[0]+":"+str[1]);
						continue;
					}
				} else {
					vo.setCNR_YIELD(info_vo != null ? info_vo.getCNR_YIELD() : null);
				}

				if(StringUtils.isNotBlank(str[2]) && !StringUtils.equals(str[2], "$")) {
					try {
						BigDecimal str2 = new BigDecimal(str[2]);
						// NUMBER(6,2)
						if(getNumOfBigDecimal(str2) > 4)
							throw new Exception("");
						vo.setCNR_MULTIPLE(str2);
					} catch (Exception e) {
						error3.add(str[0]+":"+str[2]);
						continue;
					}
				} else {
					vo.setCNR_MULTIPLE(info_vo != null ? info_vo.getCNR_MULTIPLE() : null);
				}

				if(StringUtils.isNotBlank(str[3]) && !StringUtils.equals(str[3], "$")) {
					try {
						vo.setMULTIPLE_SDATE(new Timestamp(sdf.parse(str[3]).getTime()));
					} catch (Exception e) {
						try {
							vo.setMULTIPLE_SDATE(new Timestamp(sdf2.parse(str[3]).getTime()));
						} catch (Exception e2) {
							error3.add(str[0]+":"+str[3]);
							continue;
						}
					}
				} else {
					vo.setMULTIPLE_SDATE(info_vo != null ? info_vo.getMULTIPLE_SDATE() : null);
				}

				if(StringUtils.isNotBlank(str[4]) && !StringUtils.equals(str[4], "$")) {
					try {
						vo.setMULTIPLE_EDATE(new Timestamp(sdf.parse(str[4]).getTime()));
					} catch (Exception e) {
						try {
							vo.setMULTIPLE_EDATE(new Timestamp(sdf2.parse(str[4]).getTime()));
						} catch (Exception e2) {
							error3.add(str[0]+":"+str[4]);
							continue;
						}
					}
				} else {
					vo.setMULTIPLE_EDATE(info_vo != null ? info_vo.getMULTIPLE_EDATE() : null);
				}

				if(StringUtils.isNotBlank(str[5]) && !StringUtils.equals(str[5], "$")) {
					// 補3碼0
					str[5] = StringUtils.leftPad(str[5].trim(), 3, "0");
					if(StringUtils.isBlank(mkt_tier3.get(str[5]))) {
						error3.add(str[0]+":"+str[5]);
						continue;
					}
					vo.setINV_TARGET(str[5]);
				} else {
					vo.setINV_TARGET(info_vo != null ? info_vo.getINV_TARGET() : null);
				}
				
				// 專案代碼
				if (StringUtils.isNotBlank(str[7]) && !StringUtils.equals((str[7].trim()), "$")) {
					if (str[7].length() > 20) {
						error3.add(str[0] + ":" + str[7]);
						continue;
					}

					if (StringUtils.equals((str[7].trim()), "0")) {
						vo.setPROJECT(null);
					} else {
						vo.setPROJECT(str[7].trim());
					}
				} else {
					vo.setPROJECT(info_vo != null ? info_vo.getPROJECT() : null);
				}
				// 客群代碼
				if (StringUtils.isNotBlank(str[8]) && !StringUtils.equals((str[8].trim()), "$")) {
					if (str[8].length() > 20) {
						error3.add(str[0] + ":" + str[8]);
						continue;
					}
					if (StringUtils.equals((str[8].trim()), "0")) {
						vo.setCUSTOMER_LEVEL(null);
					} else {
						vo.setCUSTOMER_LEVEL(str[8].trim());
					}
				} else {
					vo.setCUSTOMER_LEVEL(info_vo != null ? info_vo.getCUSTOMER_LEVEL() : null);
				}

				vo.setACT_TYPE("M");
				vo.setREVIEW_STATUS("W");
				dam.create(vo);

				if(StringUtils.isNotBlank(str[6])) {
					String stock_bond_type = str[6].trim().toUpperCase();
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
		return_VO.setErrorList6(error6);
		this.sendRtnObject(return_VO);
	}

	/*
	 * #1404
	 */
	private void addReviewStockBondType(String prd_id, String stock_bond_type, TBPRD_ETFVO info_vo) throws DAOException, JBranchException {
		List<Map<String,Object>> resultList = new ArrayList<Map<String,Object>>();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();

		sql.append("SELECT PRD_ID FROM TBPRD_ETF_REVIEW where PRD_ID = :prd_id AND REVIEW_STATUS = 'W' ");

		queryCondition.setObject("prd_id", prd_id);
		queryCondition.setQueryString(sql.toString());
		resultList = dam.exeQuery(queryCondition);

		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		if(resultList.size() > 0) {
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sql = new StringBuffer();
			sql.append("UPDATE TBPRD_ETF_REVIEW SET STOCK_BOND_TYPE = :stock_bond_type ");
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

	/*
	 * TBPRD_ETF 標籤欄位處理
	 * #1404 專案、客群 2023.01.30
	 */
	public void updateTagsIndependent(String[] str, ArrayList<String> error6) throws DAOException {
		dam = this.getDataAccessManager();
		TBPRD_ETFVO etfVO = (TBPRD_ETFVO) dam.findByPKey(TBPRD_ETFVO.TABLE_UID, str[0].trim());
		// 專案代碼
		if (StringUtils.isNotBlank(str[7])) {
			if (str[7].length() > 20) {
				error6.add(str[0] + ":" + str[7]);
				return;
			}

			if (StringUtils.equals((str[7].trim()), "0")) {
				etfVO.setPROJECT(null);
			} else if (StringUtils.equals((str[7].trim()), "$")) {
				
			} else {
				etfVO.setPROJECT(str[7].trim());
			}
		}
		// 客群代碼
		if (StringUtils.isNotBlank(str[8])) {
			if (str[8].length() > 20) {
				error6.add(str[0] + ":" + str[8]);
				return;
			}
			if (StringUtils.equals((str[8].trim()), "0")) {
				etfVO.setCUSTOMER_LEVEL(null);
			} else if (StringUtils.equals((str[8].trim()), "$")) {
				
			} else {
				etfVO.setCUSTOMER_LEVEL(str[8].trim());
			}
		}
		dam.update(etfVO);
	}

	/*
	 * #1404 上傳專案參數 使用delete insert 複製PRD230同名功能修改
	 */
	public void uploadProject(Object body, IPrimitiveMap header) throws Exception {
		PRD240InputVO inputVO = (PRD240InputVO) body;
		PRD240OutputVO return_VO = new PRD240OutputVO();

		dam = this.getDataAccessManager();
		// 先清空
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("delete TBSYSPARAMETER where PARAM_TYPE = :PARAM_TYPE ");
		queryCondition.setObject("PARAM_TYPE", "PRD.ETF_PROJECT");
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
				parameterPK.setPARAM_TYPE("PRD.ETF_PROJECT");
				parameterPK.setPARAM_CODE(str[0].trim());
				TBSYSPARAMETERVO parameterVO = new TBSYSPARAMETERVO();
				parameterVO.setcomp_id(parameterPK);
				parameterVO.setPARAM_NAME(str[1].trim());
				parameterVO.setPARAM_NAME_EDIT(str[1].trim());

				sql = new StringBuffer();
				sql.append("select max(PARAM_ORDER) as COUNT from TBSYSPARAMETER where PARAM_TYPE = :PARAM_TYPE");
				queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				queryCondition.setObject("PARAM_TYPE", "PRD.ETF_PROJECT");
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
		PRD240InputVO inputVO = (PRD240InputVO) body;
		PRD240OutputVO return_VO = new PRD240OutputVO();

		dam = this.getDataAccessManager();
		// 先清空
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("delete TBSYSPARAMETER where PARAM_TYPE = :PARAM_TYPE ");
		queryCondition.setObject("PARAM_TYPE", "PRD.ETF_CUSTOMER_LEVEL");
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
				parameterPK.setPARAM_TYPE("PRD.ETF_CUSTOMER_LEVEL");
				parameterPK.setPARAM_CODE(str[0].trim());
				TBSYSPARAMETERVO parameterVO = new TBSYSPARAMETERVO();
				parameterVO.setcomp_id(parameterPK);
				parameterVO.setPARAM_NAME(str[1].trim());
				parameterVO.setPARAM_NAME_EDIT(str[1].trim());

				sql = new StringBuffer();
				sql.append("select max(PARAM_ORDER) as COUNT from TBSYSPARAMETER where PARAM_TYPE = :PARAM_TYPE");
				queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				queryCondition.setObject("PARAM_TYPE", "PRD.ETF_CUSTOMER_LEVEL");
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

	public void etf_downloadSimple(Object body, IPrimitiveMap header) throws Exception {
		// gen csv
		XmlInfo xmlInfo = new XmlInfo();
		String fileName = "上傳指定商品代碼範例.csv";
		// header
		String[] csvHeader = new String[9];
		csvHeader[0] = "商品代碼";
		csvHeader[1] = "CNR分配率";
		csvHeader[2] = "CNR加減碼";
		csvHeader[3] = "加碼區間起日{西元年/月/日}";
		csvHeader[4] = "加碼區間迄日";
		TreeMap<String, String> map1 = new TreeMap<>(xmlInfo.doGetVariable("PRD.MKT_TIER3", FormatHelper.FORMAT_3));
		map1.remove("033");map1.remove("999");
		csvHeader[5] = "投資標的(市場別)" + ObjectUtils.toString(map1);
		csvHeader[6] = "股債類型(S:股票型、B:債券型)";
		csvHeader[7] = "專案代碼";
		csvHeader[8] = "客群代碼";

		CSVUtil csv = new CSVUtil();
		csv.setHeader(csvHeader);
		String url = csv.generateCSV();
		// download
		this.notifyClientToDownloadFile(url, fileName);
	}

	public void upload_inquire(Object body, IPrimitiveMap header) throws JBranchException {
		PRD240InputVO inputVO = (PRD240InputVO) body;
		PRD240OutputVO return_VO = new PRD240OutputVO();
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

	public void etf_getRank(Object body, IPrimitiveMap header) throws JBranchException {
		PRD240InputVO inputVO = (PRD240InputVO) body;
		PRD240OutputVO return_VO = new PRD240OutputVO();
		dam = this.getDataAccessManager();

		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		queryCondition.setQueryString("select PRD_ID, ETF_CNAME, RISKCATE_ID, PRD_RANK, PRD_RANK_DATE FROM TBPRD_ETF WHERE PRD_RANK IS NOT NULL ORDER BY PRD_RANK");
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		return_VO.setResultList(list);

		// select
		if(inputVO.getDate() != null) {
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sql = new StringBuffer();
			sql.append("SELECT a.PRD_ID, b.ETF_CNAME, b.RISKCATE_ID, a.PRD_RANK FROM TBPRD_RANK a ");
			sql.append("LEFT JOIN TBPRD_ETF b on a.PRD_ID = b.PRD_ID ");
			sql.append("WHERE a.PRD_TYPE = 'ETF' ");
			sql.append("AND a.EFFECT_DATE = :eff_date ");
			queryCondition.setObject("eff_date", inputVO.getDate());
			queryCondition.setQueryString(sql.toString());
			List<Map<String, Object>> list2 = dam.exeQuery(queryCondition);
			return_VO.setResultList2(list2);
		}
		// 最近的
		else {
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			queryCondition.setQueryString("select DISTINCT EFFECT_DATE FROM TBPRD_RANK WHERE PRD_TYPE = 'ETF' AND EFFECT_DATE > trunc(sysdate) ORDER BY EFFECT_DATE");
			List<Map<String, Object>> list3 = dam.exeQuery(queryCondition);
			if(list3.size() > 0) {
				return_VO.setLastDate((Timestamp) list3.get(0).get("EFFECT_DATE"));

				queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				StringBuffer sql = new StringBuffer();
				sql.append("SELECT a.PRD_ID, b.ETF_CNAME, b.RISKCATE_ID, a.PRD_RANK FROM TBPRD_RANK a ");
				sql.append("LEFT JOIN TBPRD_ETF b on a.PRD_ID = b.PRD_ID ");
				sql.append("WHERE a.PRD_TYPE = 'ETF' ");
				sql.append("AND a.EFFECT_DATE = :eff_date ");
				queryCondition.setObject("eff_date", list3.get(0).get("EFFECT_DATE"));
				queryCondition.setQueryString(sql.toString());
				List<Map<String, Object>> list2 = dam.exeQuery(queryCondition);
				return_VO.setResultList2(list2);
			}
		}

		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		queryCondition.setQueryString("select DISTINCT EFFECT_DATE FROM TBPRD_RANK WHERE PRD_TYPE = 'ETF' AND EFFECT_DATE > trunc(sysdate) ORDER BY EFFECT_DATE");
		List<Map<String, Object>> list3 = dam.exeQuery(queryCondition);
		return_VO.setResultList3(list3);

		this.sendRtnObject(return_VO);
	}

	public void etf_saveSort(Object body, IPrimitiveMap header) throws JBranchException {
		PRD240InputVO inputVO = (PRD240InputVO) body;
		dam = this.getDataAccessManager();

		// del first
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		queryCondition.setQueryString("DELETE FROM TBPRD_RANK WHERE PRD_TYPE = 'ETF' AND EFFECT_DATE = :eff_date");
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
			vo.setPRD_TYPE("ETF");
			vo.setPRD_ID(ObjectUtils.toString(map.get("prd_id")));
			vo.setPRD_RANK(new BigDecimal(ObjectUtils.toString(map.get("rank"))));
			vo.setEFFECT_DATE(new Timestamp(inputVO.getDate().getTime()));
			dam.create(vo);
		}

		this.sendRtnObject(null);
	}
	
	/*
	 * #1404
	 */
	public void updateTBPRD_ETF_Tags(Object body, IPrimitiveMap header) throws Exception {
		PRD240InputVO inputVO = (PRD240InputVO) body;
		PRD240OutputVO return_VO = new PRD240OutputVO();
		dam = this.getDataAccessManager();

		// check again
		// TBPRD_ETF
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT PRD_ID FROM TBPRD_ETF where PRD_ID = :id ");
		queryCondition.setObject("id", inputVO.getPrd_id());
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		if (list.size() == 0)
			throw new APException("ehl_01_common_026");

		TBPRD_ETFVO fundVO = (TBPRD_ETFVO) dam.findByPKey(TBPRD_ETFVO.TABLE_UID, inputVO.getPrd_id());

		// 專案
		if (StringUtils.isNotBlank(inputVO.getEtf_project())) {
			fundVO.setPROJECT(inputVO.getEtf_project());
		}
		// 客群
		if (StringUtils.isNotBlank(inputVO.getEtf_customer_level())) {
			fundVO.setCUSTOMER_LEVEL(inputVO.getEtf_customer_level());
		}
		dam.update(fundVO);

		this.sendRtnObject(return_VO);
	}



}
