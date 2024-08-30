package com.systex.jbranch.app.server.fps.fps910;

import com.systex.jbranch.app.common.fps.table.TBFPS_MODEL_ALLOCATIONVO;
import com.systex.jbranch.app.common.fps.table.TBFPS_MODEL_ALLOCATION_HEADVO;
import com.systex.jbranch.app.common.fps.table.TBFPS_MODEL_ALLOCATION_INSPK;
import com.systex.jbranch.app.common.fps.table.TBFPS_MODEL_ALLOCATION_INSVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataManager.DataManager;
import com.systex.jbranch.platform.common.dataManager.WorkStation;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.serialnumber.SerialNumberUtil;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
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

@Component("fps910")
@Scope("request")
public class FPS910 extends FubonWmsBizLogic {
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(FPS910.class);
	
	public void inquire(Object body, IPrimitiveMap header) throws JBranchException {
		FPS910InputVO inputVO = (FPS910InputVO) body;
		FPS910OutputVO return_VO = new FPS910OutputVO();
		WorkStation ws = DataManager.getWorkStation(uuid);
		dam = this.getDataAccessManager();
		
		// 判斷主管直接根據有無覆核權限
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT COUNT(ROLEID) AS COUNTS FROM TBSYSSECUROLPRIASS ");
		sql.append("WHERE PRIVILEGEID IN (SELECT PRIVILEGEID FROM TBSYSSECUPRIFUNMAP WHERE ITEMID = 'FPS910' AND FUNCTIONID = 'confirm') AND ROLEID = :roleID ");
		queryCondition.setObject("roleID", getUserVariable(FubonSystemVariableConsts.LOGINROLE));
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> privilegeCount = dam.exeQuery(queryCondition);
		//
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sql = new StringBuffer();
		sql.append("SELECT a.PARAM_NO, a.EFFECT_START_DATE, a.EFFECT_END_DATE, a.SUBMIT_DATE, a.PORTFOLIO, a.INV_AMT_TYPE, a.SETTING_TYPE, a.STATUS, a.CREATOR, a.CREATETIME, a.LASTUPDATE, a.MODIFIER, ");
		sql.append("DECODE(a.MODIFIER, NULL, NULL, b.EMP_NAME || '-' || a.MODIFIER) as EDITOR, ");
		sql.append("DECODE(a.CREATOR, NULL, NULL, c.EMP_NAME || '-' || a.CREATOR) as ADDOR ");
		sql.append("FROM TBFPS_MODEL_ALLOCATION_HEAD a ");
		sql.append("LEFT JOIN TBORG_MEMBER b on a.MODIFIER = b.EMP_ID ");
		sql.append("LEFT JOIN TBORG_MEMBER c on a.CREATOR = c.EMP_ID ");
		sql.append("WHERE a.PARAM_NO NOT IN (SELECT PARAM_NO FROM TBFPS_MODEL_ALLOCATION_HEAD WHERE STATUS = 'S' AND CREATOR != :creator) ");
		queryCondition.setObject("creator", ws.getUser().getUserID());
		// where
		if (inputVO.getDate() != null) {
			sql.append("AND TO_CHAR(:date, 'yyyyMM') = TO_CHAR(a.EFFECT_START_DATE, 'yyyyMM') ");
			queryCondition.setObject("date", inputVO.getDate());
		}
		if(StringUtils.isNotBlank(inputVO.getSetting_type())) {
			sql.append("AND a.SETTING_TYPE = :setting_type ");
			queryCondition.setObject("setting_type", inputVO.getSetting_type());
		}
		if(StringUtils.isNotBlank(inputVO.getPortfolio())) {
			sql.append("AND a.PORTFOLIO = :portfolio ");
			queryCondition.setObject("portfolio", inputVO.getPortfolio());
		}
		if("1".equals(inputVO.getSetting_type()) && !"3".equals(inputVO.getInv_amt_type())) {
			sql.append("AND a.INV_AMT_TYPE = :inv_amt_tpe ");
			queryCondition.setObject("inv_amt_tpe", inputVO.getInv_amt_type());
		}
		// 主管的狀態只會有”審核(覆核中)”、 ”核准(生效)”、”失效”
		if (((BigDecimal) privilegeCount.get(0).get("COUNTS")).compareTo(new BigDecimal(0)) > 0)
			sql.append("AND STATUS IN ('P', 'W', 'A', 'F') ");
		//
		sql.append("ORDER BY a.EFFECT_START_DATE DESC, a.LASTUPDATE DESC ");
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		return_VO.setResultList(list);
		
		this.sendRtnObject(return_VO);
	}
	
	public void init_detail(Object body, IPrimitiveMap header) throws JBranchException {
		FPS910InputVO inputVO = (FPS910InputVO) body;
		FPS910OutputVO return_VO = new FPS910OutputVO();
		dam = this.getDataAccessManager();
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT a.SEQNO, a.CUST_RISK_ATR, a.INV_PRD_TYPE, a.INV_PRD_TYPE_2, a.CURRENCY_STD_ID, a.PRD_TYPE, a.PARAM_NO, a.PRD_ID, a.INV_PERCENT, a.LASTUPDATE, ");
		sql.append("CASE WHEN a.PRD_ID IS NOT NULL THEN ( ");
		sql.append("CASE WHEN a.PRD_TYPE = 'MFD' THEN (SELECT b.FUND_CNAME FROM TBPRD_FUND b where b.PRD_ID = a.PRD_ID) ");
		sql.append("WHEN a.PRD_TYPE = 'ETF' THEN (SELECT b.ETF_CNAME FROM TBPRD_ETF b where b.PRD_ID = a.PRD_ID) ");
		sql.append("WHEN a.PRD_TYPE = 'INS' THEN (SELECT DISTINCT b.INSPRD_NAME FROM TBPRD_INS b where b.PRD_ID = a.PRD_ID) ");
		//2020-2-4 by Jacky 新增奈米投
		sql.append("WHEN a.PRD_TYPE = 'NANO' THEN (SELECT b.PRD_NAME FROM TBPRD_NANO b where b.PRD_ID = a.PRD_ID) ");		
		sql.append("ELSE NULL END) ELSE NULL END AS PRD_NAME, ");
		sql.append("CASE WHEN a.PRD_ID IS NOT NULL THEN ( ");
		sql.append("CASE WHEN a.PRD_TYPE = 'MFD' THEN (SELECT b.RISKCATE_ID FROM TBPRD_FUND b where b.PRD_ID = a.PRD_ID) ");
		sql.append("WHEN a.PRD_TYPE = 'ETF' THEN (SELECT b.RISKCATE_ID FROM TBPRD_ETF b where b.PRD_ID = a.PRD_ID) ");
		sql.append("WHEN a.PRD_TYPE = 'NANO' THEN (SELECT b.RISKCATE_ID FROM TBPRD_NANO b where b.PRD_ID = a.PRD_ID) ");
		sql.append("ELSE NULL END) ELSE NULL END AS RISKCATE_ID, ");
		sql.append("DECODE(a.MODIFIER, NULL, NULL, b.EMP_NAME || '-' || a.MODIFIER) as MODIFIER ");
		sql.append("FROM TBFPS_MODEL_ALLOCATION a ");
		sql.append("LEFT JOIN TBORG_MEMBER b on a.MODIFIER = b.EMP_ID ");
		sql.append("WHERE a.PARAM_NO = :param_no ");		
		
		if(StringUtils.isNotBlank(inputVO.getStock_bond_type())) {
			sql.append("AND a.STOCK_BOND_TYPE = :stock_bond_type ");
			queryCondition.setObject("stock_bond_type", inputVO.getStock_bond_type());
		}
		
		sql.append("ORDER BY a.CUST_RISK_ATR, a.INV_PRD_TYPE, RISKCATE_ID ");
		
		queryCondition.setObject("param_no", inputVO.getParam_no());
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		
		// TBFPS_MODEL_ALLOCATION_INS
		for(Map<String, Object> map : list) {
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			queryCondition.setQueryString("select * from TBFPS_MODEL_ALLOCATION_INS where SEQNO = :seq_no AND PRD_ID = :prd_id ");
			queryCondition.setObject("seq_no", map.get("SEQNO"));
			queryCondition.setObject("prd_id", map.get("PRD_ID"));
			List<Map<String, Object>> t_list = dam.exeQuery(queryCondition);
			map.put("fund_list", t_list);
		}
		return_VO.setResultList(list);
		this.sendRtnObject(return_VO);
	}
	
	public void add(Object body, IPrimitiveMap header) throws Exception {
		FPS910InputVO inputVO = (FPS910InputVO) body;
		dam = this.getDataAccessManager();
		this.add_table(inputVO, false);
		this.sendRtnObject(null);
	}
	
	public void add_table(FPS910InputVO inputVO, Boolean isConfirm) throws Exception {
		WorkStation ws = DataManager.getWorkStation(uuid);
		dam = this.getDataAccessManager();
		String param_no = inputVO.getParam_no();
		// clear old TBFPS_MODEL_ALLOCATION_INS
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		queryCondition.setQueryString("DELETE FROM TBFPS_MODEL_ALLOCATION_INS WHERE PARAM_NO = :param_no");
		queryCondition.setObject("param_no", param_no);
		dam.exeUpdate(queryCondition);
		// 同時適用, 舊的改1然後再新增2
		String ant_type3_no = null;
		if("3".equals(inputVO.getInv_amt_type())) {
			// old
			TBFPS_MODEL_ALLOCATION_HEADVO old_vo = (TBFPS_MODEL_ALLOCATION_HEADVO) dam.findByPKey(TBFPS_MODEL_ALLOCATION_HEADVO.TABLE_UID, inputVO.getParam_no());
			if(isConfirm) old_vo.setSTATUS("P");
			else old_vo.setSTATUS("S");
			old_vo.setEFFECT_START_DATE(new Timestamp(inputVO.getDate().getTime()));
			old_vo.setINV_AMT_TYPE("1");
			old_vo.setCreator(ws.getUser().getUserID());
			dam.update(old_vo);
			// new
			ant_type3_no = new SimpleDateFormat("yyyyMM").format(new Date()) + StringUtils.leftPad(getSN("MAIN"), 4, "0");
			TBFPS_MODEL_ALLOCATION_HEADVO new_vo = new TBFPS_MODEL_ALLOCATION_HEADVO();
			new_vo.setPARAM_NO(ant_type3_no);
			new_vo.setEFFECT_START_DATE(new Timestamp(inputVO.getDate().getTime()));
			new_vo.setSETTING_TYPE(inputVO.getSetting_type());
			new_vo.setPORTFOLIO("FUBON01");
			new_vo.setINV_AMT_TYPE("2");
			if(isConfirm) new_vo.setSTATUS("P");
			else new_vo.setSTATUS("S");
			dam.create(new_vo);
		} else {
			TBFPS_MODEL_ALLOCATION_HEADVO vo = (TBFPS_MODEL_ALLOCATION_HEADVO) dam.findByPKey(TBFPS_MODEL_ALLOCATION_HEADVO.TABLE_UID, inputVO.getParam_no());
			if (vo != null) {
				if(isConfirm) vo.setSTATUS("P");
				else vo.setSTATUS("S");
				vo.setEFFECT_START_DATE(new Timestamp(inputVO.getDate().getTime()));
				vo.setINV_AMT_TYPE(inputVO.getInv_amt_type());
				vo.setCreator(ws.getUser().getUserID());
				dam.update(vo);
			} else
				throw new APException("ehl_01_common_001");
		}
		
		// del first
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		queryCondition.setQueryString("DELETE FROM TBFPS_MODEL_ALLOCATION WHERE PARAM_NO = :param_no");
		queryCondition.setObject("param_no", inputVO.getParam_no());
		dam.exeUpdate(queryCondition);
		// then add
		// circle 1 股票型
		if(null != inputVO.getStockList()) {
			for(Map<String, Object> map : inputVO.getStockList()) {
				addVO(map, param_no, "S", ant_type3_no);
			}			
		}
		// circle 1 債券型
		if(null != inputVO.getBondsList()) {
			for(Map<String, Object> map : inputVO.getBondsList()) {
				addVO(map, param_no, "B", ant_type3_no);
			}			
		}
		//目標理財（不分股債類型）
		if(null != inputVO.getTotalList()) {
			for(Map<String, Object> map : inputVO.getTotalList()) {
				addVO(map, param_no, null, ant_type3_no);
			}			
		}
//		this.sendRtnObject(null);
	}
	
	private void addVO (Map<String, Object> map, String param_no, String stock_bond_type, String ant_type3_no) throws JBranchException {
		// circle 2
		List<Map<String, Object>> list2 = (List<Map<String, Object>>) map.get("SUBITEM");
		for(Map<String, Object> map2 : list2) {
			// 固定收益商品
			if("2".equals(map2.get("INV_PRD_TYPE"))) {
				String seq = getSN("DETAIL");
				TBFPS_MODEL_ALLOCATIONVO vo_detail = new TBFPS_MODEL_ALLOCATIONVO();
				vo_detail.setSEQNO(new BigDecimal(seq));
				vo_detail.setCUST_RISK_ATR(ObjectUtils.toString(map2.get("CUST_RISK_ATR")));
				vo_detail.setINV_PRD_TYPE(ObjectUtils.toString(map2.get("INV_PRD_TYPE")));
				vo_detail.setPARAM_NO(param_no);
				vo_detail.setINV_PERCENT(new BigDecimal(ObjectUtils.toString(map2.get("INV_PERCENT"))));
				vo_detail.setSTOCK_BOND_TYPE(stock_bond_type);
				dam.create(vo_detail);
				// type 3 new
				if(ant_type3_no != null) {
					String seq2 = getSN("DETAIL");
					TBFPS_MODEL_ALLOCATIONVO vo_detail2 = new TBFPS_MODEL_ALLOCATIONVO();
					vo_detail2.setSEQNO(new BigDecimal(seq2));
					vo_detail2.setCUST_RISK_ATR(ObjectUtils.toString(map2.get("CUST_RISK_ATR")));
					vo_detail2.setINV_PRD_TYPE(ObjectUtils.toString(map2.get("INV_PRD_TYPE")));
					vo_detail2.setPARAM_NO(ant_type3_no);
					vo_detail2.setINV_PERCENT(new BigDecimal(ObjectUtils.toString(map2.get("INV_PERCENT"))));
					vo_detail2.setSTOCK_BOND_TYPE(stock_bond_type);
					dam.create(vo_detail2);
				}
			}
			// circle 3
			else {
				List<Map<String, Object>> list3 = (List<Map<String, Object>>) map2.get("SUBITEM");
				for(Map<String, Object> map3 : list3) {
					String seq = getSN("DETAIL");
					TBFPS_MODEL_ALLOCATIONVO vo_detail = new TBFPS_MODEL_ALLOCATIONVO();
					vo_detail.setSEQNO(new BigDecimal(seq));
					vo_detail.setCUST_RISK_ATR(ObjectUtils.toString(map3.get("CUST_RISK_ATR")));
					vo_detail.setINV_PRD_TYPE(ObjectUtils.toString(map3.get("INV_PRD_TYPE")));
					vo_detail.setINV_PRD_TYPE_2(ObjectUtils.toString(map3.get("INV_PRD_TYPE_2")));
					vo_detail.setPARAM_NO(param_no);
					vo_detail.setPRD_ID(ObjectUtils.toString(map3.get("PRD_ID")));
					vo_detail.setPRD_TYPE(ObjectUtils.toString(map3.get("PRD_TYPE")));
					vo_detail.setCURRENCY_STD_ID(ObjectUtils.toString(map3.get("CURRENCY_STD_ID")));
					vo_detail.setINV_PERCENT(new BigDecimal(ObjectUtils.toString(map3.get("INV_PERCENT"))));
					vo_detail.setSTOCK_BOND_TYPE(stock_bond_type);
					dam.create(vo_detail);
					// TBFPS_MODEL_ALLOCATION_INS
					if(map3.get("fund_list") != null) {
						List<Map<String, Object>> fund_list = (List<Map<String, Object>>) map3.get("fund_list");
						for(Map<String, Object> fund_map : fund_list) {
							TBFPS_MODEL_ALLOCATION_INSPK tpk = new TBFPS_MODEL_ALLOCATION_INSPK();
							TBFPS_MODEL_ALLOCATION_INSVO tvo = new TBFPS_MODEL_ALLOCATION_INSVO();
							tpk.setSEQNO(new BigDecimal(seq));
							tpk.setPRD_ID(ObjectUtils.toString(map3.get("PRD_ID")));
							tpk.setTARGET_ID(ObjectUtils.toString(fund_map.get("TARGET_ID")));
							tvo.setcomp_id(tpk);
							tvo.setPARAM_NO(param_no);
							dam.create(tvo);
						}
					}
					// type 3 new
					if(ant_type3_no != null) {
						String seq2 = getSN("DETAIL");
						TBFPS_MODEL_ALLOCATIONVO vo_detail2 = new TBFPS_MODEL_ALLOCATIONVO();
						vo_detail2.setSEQNO(new BigDecimal(seq2));
						vo_detail2.setCUST_RISK_ATR(ObjectUtils.toString(map3.get("CUST_RISK_ATR")));
						vo_detail2.setINV_PRD_TYPE(ObjectUtils.toString(map3.get("INV_PRD_TYPE")));
						vo_detail2.setINV_PRD_TYPE_2(ObjectUtils.toString(map3.get("INV_PRD_TYPE_2")));
						vo_detail2.setPARAM_NO(ant_type3_no);
						vo_detail2.setPRD_ID(ObjectUtils.toString(map3.get("PRD_ID")));
						vo_detail2.setPRD_TYPE(ObjectUtils.toString(map3.get("PRD_TYPE")));
						vo_detail2.setCURRENCY_STD_ID(ObjectUtils.toString(map3.get("CURRENCY_STD_ID")));
						vo_detail2.setINV_PERCENT(new BigDecimal(ObjectUtils.toString(map3.get("INV_PERCENT"))));
						vo_detail2.setSTOCK_BOND_TYPE(stock_bond_type);
						dam.create(vo_detail2);
						// TBFPS_MODEL_ALLOCATION_INS
						if(map3.get("fund_list") != null) {
							List<Map<String, Object>> fund_list = (List<Map<String, Object>>) map3.get("fund_list");
							for(Map<String, Object> fund_map : fund_list) {
								TBFPS_MODEL_ALLOCATION_INSPK tpk = new TBFPS_MODEL_ALLOCATION_INSPK();
								TBFPS_MODEL_ALLOCATION_INSVO tvo = new TBFPS_MODEL_ALLOCATION_INSVO();
								tpk.setSEQNO(new BigDecimal(seq2));
								tpk.setPRD_ID(ObjectUtils.toString(map3.get("PRD_ID")));
								tpk.setTARGET_ID(ObjectUtils.toString(fund_map.get("TARGET_ID")));
								tvo.setcomp_id(tpk);
								tvo.setPARAM_NO(ant_type3_no);
								dam.create(tvo);
							}
						}
					}
				}
			}
		}
	}
	
	public void copy(Object body, IPrimitiveMap header) throws Exception {
		FPS910InputVO inputVO = (FPS910InputVO) body;
		dam = this.getDataAccessManager();
		
		// TBFPS_MODEL_ALLOCATION_HEAD
		String seq;
		TBFPS_MODEL_ALLOCATION_HEADVO head_vo = (TBFPS_MODEL_ALLOCATION_HEADVO) dam.findByPKey(TBFPS_MODEL_ALLOCATION_HEADVO.TABLE_UID, inputVO.getParam_no());
		if (head_vo != null) {
			seq = new SimpleDateFormat("yyyyMM").format(new Date()) + StringUtils.leftPad(getSN("MAIN"), 4, "0");
			TBFPS_MODEL_ALLOCATION_HEADVO vo_new = new TBFPS_MODEL_ALLOCATION_HEADVO();
			vo_new.setPARAM_NO(seq);
			vo_new.setEFFECT_START_DATE(head_vo.getEFFECT_START_DATE());
			vo_new.setEFFECT_END_DATE(head_vo.getEFFECT_END_DATE());
			vo_new.setSETTING_TYPE(head_vo.getSETTING_TYPE());
			vo_new.setPORTFOLIO(head_vo.getPORTFOLIO());
			vo_new.setINV_AMT_TYPE(head_vo.getINV_AMT_TYPE());
			vo_new.setSTATUS("S");
			dam.create(vo_new);
		} else
			throw new APException("ehl_01_common_001");
		
		// TBFPS_MODEL_ALLOCATION
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		queryCondition.setQueryString("SELECT * FROM TBFPS_MODEL_ALLOCATION WHERE PARAM_NO = :param_no");
		queryCondition.setObject("param_no", inputVO.getParam_no());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		for(Map<String, Object> map : list) {
			String seq2 = getSN("DETAIL");
			TBFPS_MODEL_ALLOCATIONVO vo_detail = new TBFPS_MODEL_ALLOCATIONVO();
			vo_detail.setSEQNO(new BigDecimal(seq2));
			vo_detail.setCUST_RISK_ATR(ObjectUtils.toString(map.get("CUST_RISK_ATR")));
			vo_detail.setINV_PRD_TYPE(ObjectUtils.toString(map.get("INV_PRD_TYPE")));
			vo_detail.setINV_PRD_TYPE_2(ObjectUtils.toString(map.get("INV_PRD_TYPE_2")));
			vo_detail.setPARAM_NO(seq);
			vo_detail.setPRD_ID(ObjectUtils.toString(map.get("PRD_ID")));
			vo_detail.setPRD_TYPE(ObjectUtils.toString(map.get("PRD_TYPE")));
			vo_detail.setCURRENCY_STD_ID(ObjectUtils.toString(map.get("CURRENCY_STD_ID")));
			vo_detail.setINV_PERCENT(new BigDecimal(ObjectUtils.toString(map.get("INV_PERCENT"))));
			vo_detail.setSTOCK_BOND_TYPE(ObjectUtils.toString(map.get("STOCK_BOND_TYPE")));
			dam.create(vo_detail);
			
			// TBFPS_MODEL_ALLOCATION_INS
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			queryCondition.setQueryString("SELECT * FROM TBFPS_MODEL_ALLOCATION_INS WHERE SEQNO = :seq_no AND PRD_ID = :prd_id");
			queryCondition.setObject("seq_no", map.get("SEQNO"));
			queryCondition.setObject("prd_id", map.get("PRD_ID"));
			List<Map<String, Object>> list2 = dam.exeQuery(queryCondition);
			for(Map<String, Object> map2 : list2) {
				TBFPS_MODEL_ALLOCATION_INSPK tpk = new TBFPS_MODEL_ALLOCATION_INSPK();
				TBFPS_MODEL_ALLOCATION_INSVO tvo = new TBFPS_MODEL_ALLOCATION_INSVO();
				tpk.setSEQNO(new BigDecimal(seq2));
				tpk.setPRD_ID(ObjectUtils.toString(map.get("PRD_ID")));
				tpk.setTARGET_ID(ObjectUtils.toString(map2.get("TARGET_ID")));
				tvo.setcomp_id(tpk);
				tvo.setPARAM_NO(seq);
				dam.create(tvo);
			}
		}
		
		this.sendRtnObject(null);
	}
	
	public void delete(Object body, IPrimitiveMap header) throws Exception {
		FPS910InputVO inputVO = (FPS910InputVO) body;
		dam = this.getDataAccessManager();
		
		// TBFPS_MODEL_ALLOCATION_HEAD
		TBFPS_MODEL_ALLOCATION_HEADVO vo = new TBFPS_MODEL_ALLOCATION_HEADVO();
		vo = (TBFPS_MODEL_ALLOCATION_HEADVO) dam.findByPKey(TBFPS_MODEL_ALLOCATION_HEADVO.TABLE_UID, inputVO.getParam_no());
		if (vo != null)
			dam.delete(vo);
		else
			throw new APException("ehl_01_common_001");
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		queryCondition.setQueryString("DELETE FROM TBFPS_MODEL_ALLOCATION WHERE PARAM_NO = :param_no");
		queryCondition.setObject("param_no", inputVO.getParam_no());
		dam.exeUpdate(queryCondition);
		
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		queryCondition.setQueryString("DELETE FROM TBFPS_MODEL_ALLOCATION_INS WHERE PARAM_NO = :param_no");
		queryCondition.setObject("param_no", inputVO.getParam_no());
		dam.exeUpdate(queryCondition);
		
		this.sendRtnObject(null);
	}
	
	public void create(Object body, IPrimitiveMap header) throws Exception {
		FPS910InputVO inputVO = (FPS910InputVO) body;
		dam = this.getDataAccessManager();
		
		// TBFPS_MODEL_ALLOCATION_HEAD
		String seq_main = new SimpleDateFormat("yyyyMM").format(new Date()) + StringUtils.leftPad(getSN("MAIN"), 4, "0");
		TBFPS_MODEL_ALLOCATION_HEADVO vo_main = new TBFPS_MODEL_ALLOCATION_HEADVO();
		vo_main.setPARAM_NO(seq_main);
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, 1);
		cal.set(Calendar.DATE, 1);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		vo_main.setEFFECT_START_DATE(new Timestamp(cal.getTime().getTime()));
		vo_main.setSETTING_TYPE(inputVO.getSetting_type());
		vo_main.setPORTFOLIO("FUBON01");
		vo_main.setSTATUS("S");
		dam.create(vo_main);
		
		this.sendRtnObject(null);
	}
	
	public void upload(Object body, IPrimitiveMap header) throws Exception {
		FPS910InputVO inputVO = (FPS910InputVO) body;
		FPS910OutputVO return_VO = new FPS910OutputVO();
		XmlInfo xmlInfo = new XmlInfo();
		dam = this.getDataAccessManager();
		
		Integer success = 0;
		String tempPath = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
		List<String[]> dataCsv = CSVUtil.getBig5CSVFile(tempPath, inputVO.getFileName());
		if(!dataCsv.isEmpty()) {
//			Map<String, String> inv_type2 = new HashMap(xmlInfo.doGetVariable("FPS.INV_PRD_TYPE_2", FormatHelper.FORMAT_3));
			Map<String, String> risk_atr = new HashMap(xmlInfo.doGetVariable("FPS.CUST_RISK_ATR", FormatHelper.FORMAT_3));
			Map<String, String> risk_atr2 = new HashMap(xmlInfo.doGetVariable("FPS.CUST_RISK_ATR_2", FormatHelper.FORMAT_3));
			Map<String, String> deposit_cur = new HashMap(xmlInfo.doGetVariable("FPS.DEPOSIT_CUR", FormatHelper.FORMAT_3));
			
			// TBFPS_MODEL_ALLOCATION_HEAD
			String seq_main = new SimpleDateFormat("yyyyMM").format(new Date()) + StringUtils.leftPad(getSN("MAIN"), 4, "0");
			TBFPS_MODEL_ALLOCATION_HEADVO vo_main = new TBFPS_MODEL_ALLOCATION_HEADVO();
			vo_main.setPARAM_NO(seq_main);
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.MONTH, 1);
			cal.set(Calendar.DATE, 1);
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			vo_main.setEFFECT_START_DATE(new Timestamp(cal.getTime().getTime()));
			vo_main.setSETTING_TYPE(inputVO.getSetting_type());
			vo_main.setPORTFOLIO("FUBON01");
			vo_main.setSTATUS("S");
			dam.create(vo_main);
			
			//目標理財
			Map<String, BigDecimal> spp_map = new HashMap<String, BigDecimal>();
			spp_map.put("C1-1", new BigDecimal(0)); spp_map.put("C1-2", new BigDecimal(0));
			spp_map.put("C2-3", new BigDecimal(0)); spp_map.put("C2-4", new BigDecimal(0));
			spp_map.put("C3-5", new BigDecimal(0)); spp_map.put("C3-6", new BigDecimal(0));
			spp_map.put("C4-7", new BigDecimal(0)); spp_map.put("C4-8", new BigDecimal(0));
			
//			Map<String, BigDecimal> bonds_map = new HashMap<String, BigDecimal>();
//			bonds_map.put("C1-1", new BigDecimal(0)); bonds_map.put("C1-2", new BigDecimal(0));
//			bonds_map.put("C2-3", new BigDecimal(0)); bonds_map.put("C2-4", new BigDecimal(0));
//			bonds_map.put("C3-5", new BigDecimal(0)); bonds_map.put("C3-6", new BigDecimal(0));
//			bonds_map.put("C4-7", new BigDecimal(0)); bonds_map.put("C4-8", new BigDecimal(0));
			
			//全資產
			Map<String, BigDecimal> stock_map2 = new HashMap<String, BigDecimal>();
			stock_map2.put("C1", new BigDecimal(0)); stock_map2.put("C2", new BigDecimal(0));
			stock_map2.put("C3", new BigDecimal(0)); stock_map2.put("C4", new BigDecimal(0));
			
			Map<String, BigDecimal> bonds_map2 = new HashMap<String, BigDecimal>();
			bonds_map2.put("C1", new BigDecimal(0)); bonds_map2.put("C2", new BigDecimal(0));
			bonds_map2.put("C3", new BigDecimal(0)); bonds_map2.put("C4", new BigDecimal(0));
			
			// check 投組類型
			// 固定收益商品只能一個
//			Map<String, Integer> prd_type = new HashMap<String, Integer>();
//			prd_type.put("C1", 0);prd_type.put("C2", 0);prd_type.put("C3", 0);prd_type.put("C4", 0);
//			// 台幣商品只能一個
//			Map<String, Integer> prd_type2 = new HashMap<String, Integer>();
//			prd_type2.put("C1", 0);prd_type2.put("C2", 0);prd_type2.put("C3", 0);prd_type2.put("C4", 0);
			//
			for(int i = 0;i < dataCsv.size();i++) {
				String[] str = dataCsv.get(i);
				// 目標理財規劃
				if("1".equals(inputVO.getSetting_type())) {
					if(i == 0) {
						try {
							if(!"客戶風險屬性".equals(str[0].substring(0, 6)))
								throw new Exception(str[0]);
							else if(!"商品類型".equals(str[1].substring(0, 4)))
								throw new Exception(str[1]);
							else if(!"商品代號".equals(str[2].trim()))
								throw new Exception(str[2]);
							else if(!"佔比%".equals(str[3].trim()))
								throw new Exception(str[3]);
//							else if(!"股債類型".equals(str[4].substring(0, 4)))
//								throw new Exception(str[4]);
						} catch(Exception ex) {
							throw new APException(ex.getMessage() + ":上傳格式錯誤，請下載範例檔案");
						}
						continue;
					}
					
					String seq = getSN("DETAIL");
					TBFPS_MODEL_ALLOCATIONVO vo_detail = new TBFPS_MODEL_ALLOCATIONVO();
					vo_detail.setSEQNO(new BigDecimal(seq));
					
					// CUST_RISK_ATR
					if(StringUtils.isNotBlank(str[0])) {
						if(StringUtils.isBlank(risk_atr.get(str[0])))
							throw new APException("客戶風險屬性：" + str[0] + "，欄位檢核錯誤。");
						vo_detail.setCUST_RISK_ATR(str[0]);
					}
					else
						throw new APException("客戶風險屬性：" + str[0] + "，欄位不得為空。");
					
					// INV_PRD_TYPE
					vo_detail.setINV_PRD_TYPE("3");		//投組商品類型 - 1 儲蓄型保險+存款  2 固定收益商品  3 基股類商品
					
					// PARAM_NO
					vo_detail.setPARAM_NO(seq_main);
					
					// PRD_TYPE
					if(StringUtils.isNotBlank(str[1])) {
						if("1".equals(str[1].trim()))
							vo_detail.setPRD_TYPE("MFD");
						else if("3".equals(str[1].trim()))
							vo_detail.setPRD_TYPE("INS");
						else
							throw new APException("客戶風險屬性：" + str[0]+":"+str[1] + "，商品類型欄位檢核錯誤。");
					} else
						throw new APException("客戶風險屬性：" + str[0]+":商品類型欄位不得為空。");
					
					// PRD_ID
					if("1".equals(str[1].trim())) {
						// 補4碼0
						str[2] = StringUtils.leftPad(str[2].trim(), 4, "0");
						QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
						queryCondition.setQueryString("SELECT PRD_ID, RISKCATE_ID, CURRENCY_STD_ID FROM TBPRD_FUND where PRD_ID = :id");
						queryCondition.setObject("id", str[2]);
						List<Map<String, Object>> list = dam.exeQuery(queryCondition);
						if (list.size() > 0) {
							vo_detail.setPRD_ID(ObjectUtils.toString(list.get(0).get("PRD_ID")));
							vo_detail.setCURRENCY_STD_ID(ObjectUtils.toString(list.get(0).get("CURRENCY_STD_ID")));
							
							// RISKCATE_ID
							if(list.get(0).get("RISKCATE_ID") != null) {
								Integer num1 = new Integer(str[0].trim().substring(1, 2));
								Integer num2 = new Integer(list.get(0).get("RISKCATE_ID").toString().substring(1, 2));
								if(num2 > num1)
									throw new APException("客戶風險屬性：" + str[0]+":"+str[2] + "，發生商品不適配的情況。");
							} else
								throw new APException("客戶風險屬性：" + str[0]+":"+str[2] + "，此商品無風險等級。");
						}
						else
							throw new APException("客戶風險屬性：" + str[0]+":"+str[2] + "，商品主檔無此商品。");
//					} 
//					else if ("2".equals(str[1].trim())) {
//						QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
//						queryCondition.setQueryString("SELECT PRD_ID, RISKCATE_ID, CURRENCY_STD_ID FROM TBPRD_ETF where PRD_ID = :id");
//						queryCondition.setObject("id", str[2].trim());
//						List<Map<String, Object>> list = dam.exeQuery(queryCondition);
//						if (list.size() > 0) {
//							vo_detail.setPRD_ID(ObjectUtils.toString(list.get(0).get("PRD_ID")));
//							vo_detail.setCURRENCY_STD_ID(ObjectUtils.toString(list.get(0).get("CURRENCY_STD_ID")));
//							
//							// RISKCATE_ID
//							if(list.get(0).get("RISKCATE_ID") != null) {
//								Integer num1 = new Integer(str[0].trim().substring(1, 2));
//								Integer num2 = new Integer(list.get(0).get("RISKCATE_ID").toString().substring(1, 2));
//								if(num2 > num1)
//									throw new APException("客戶風險屬性：" + str[0]+":"+str[2] + "，發生商品不適配的情況。");
//							} else
//								throw new APException("客戶風險屬性：" + str[0]+":"+str[2] + "，此商品無風險等級。");
//						}
//						else
//							throw new APException("客戶風險屬性：" + str[0]+":"+str[2] + "，商品主檔無此商品。");
					} else {
						QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
						queryCondition.setQueryString("SELECT PRD_ID, CURR_CD FROM TBPRD_INS where PRD_ID = :id AND INS_TYPE = '2'");
						queryCondition.setObject("id", str[2].trim());
						List<Map<String, Object>> list = dam.exeQuery(queryCondition);
						if (list.size() > 0) {
							vo_detail.setPRD_ID(ObjectUtils.toString(list.get(0).get("PRD_ID")));
							vo_detail.setCURRENCY_STD_ID(ObjectUtils.toString(list.get(0).get("CURR_CD")));
						}
						else
							throw new APException("客戶風險屬性：" + str[0]+":"+str[2] + "，商品主檔無此商品。");
					}
					
					// INV_PERCENT
					if(StringUtils.isNotBlank(str[3])) {
						try {
							BigDecimal str3 = new BigDecimal(str[3]);
							// NUMBER(5,2)
							if(getNumOfBigDecimal(str3) > 3)
								throw new Exception("");
							vo_detail.setINV_PERCENT(str3);
							spp_map.put(str[0], spp_map.get(str[0]).add(new BigDecimal(str[3])));
						} catch (Exception e) {
							throw new APException("客戶風險屬性：" + str[0] + ":" + str[3] + "，佔比欄位檢核錯誤。");
						}
					}
					else
						throw new APException("客戶風險屬性：" + str[0] + ":" + str[3] + "，佔比欄位不得為空。");
					
//					// STOCK_BOND_TYPE
//					if(StringUtils.isNotBlank(str[4])) {
//						String stock_bond_type = str[4].trim().toUpperCase();
//						
//						if(!"S".equals(stock_bond_type) && !"B".equals(stock_bond_type)) {
//							throw new APException("客戶風險屬性：" + str[0]+":"+str[4] + "，股債頁籤欄位檢核錯誤。");
//						} else 
//							vo_detail.setSTOCK_BOND_TYPE(str[4]);
//						
//						//個風險等級佔比加總
//						if("S".equals(stock_bond_type)) {
//							stock_map.put(str[0], stock_map.get(str[0]).add(new BigDecimal(str[3])));							
//						} else {
//							bonds_map.put(str[0], bonds_map.get(str[0]).add(new BigDecimal(str[3])));
//						}
//					} else
//						throw new APException("客戶風險屬性：" + str[0] + ":" + str[4] + "，股債頁籤欄位不得為空。");
					
					dam.create(vo_detail);
					success += 1;
				}
				// 全資產規劃
				else {
					if(i == 0) {
						try {
							if(!"客戶風險屬性".equals(str[0].substring(0, 6)))
								throw new Exception(str[0]);
							else if(!"投組類型".equals(str[1].substring(0, 4)))
								throw new Exception(str[1]);
							else if(!"商品代號".equals(str[2].substring(0, 4)))
								throw new Exception(str[2]);
							else if(!"佔比%".equals(str[3].trim()))
								throw new Exception(str[3]);
							else if(!"股債頁籤".equals(str[4].substring(0, 4)))
								throw new Exception(str[4]);
						} catch(Exception ex) {
							throw new APException(ex.getMessage() + ":上傳格式錯誤，請下載範例檔案");
						}
						continue;
					}
					
					String seq = getSN("DETAIL");
					TBFPS_MODEL_ALLOCATIONVO vo_detail = new TBFPS_MODEL_ALLOCATIONVO();
					vo_detail.setSEQNO(new BigDecimal(seq));
					
					// PARAM_NO
					vo_detail.setPARAM_NO(seq_main);
					
					// CUST_RISK_ATR
					if(StringUtils.isNotBlank(str[0])) {
						if(StringUtils.isBlank(risk_atr2.get(str[0])))
							throw new APException("客戶風險屬性：" + str[0] + "，欄位檢核錯誤。");
						vo_detail.setCUST_RISK_ATR(str[0]);
					} else
						throw new APException("客戶風險屬性：" + str[0] + "，欄位不得為空。");
					
					// INV_PERCENT
					if(StringUtils.isNotBlank(str[3])) {
						try {
							BigDecimal str3 = new BigDecimal(str[3]);
							// NUMBER(5,2)
							if(getNumOfBigDecimal(str3) > 3)
								throw new Exception("");
							vo_detail.setINV_PERCENT(str3);
						} catch (Exception e) {
							throw new APException("客戶風險屬性：" + str[0]+":"+str[3] + "，佔比欄位檢核錯誤。");
						}
					} else
						throw new APException("客戶風險屬性：" + str[0]+":"+str[3] + "，佔比欄位不得為空。");
					
					// 投組類型{1=基金, 2=ETF, 3=投資型保險}
					if(StringUtils.isNotBlank(str[1])) {
//						// 台幣商品只要一個
//						if("11".equals(str[1].trim())) {
//							vo_detail.setINV_PRD_TYPE("1");
//							vo_detail.setINV_PRD_TYPE_2("1");
//							if(prd_type2.get(str[0]) > 1)
//								throw new APException("客戶風險屬性：" + str[0] + ":台幣商品只需一個。");
//							prd_type2.put(str[0], prd_type2.get(str[0]) + 1);
//							vo_detail.setCURRENCY_STD_ID("TWD");
//						}
//						else if("12".equals(str[1].trim())) {
//							vo_detail.setINV_PRD_TYPE("1");
//							vo_detail.setINV_PRD_TYPE_2("2");
//							if(StringUtils.isBlank(deposit_cur.get(str[3])))
//								throw new APException("客戶風險屬性：" + str[0]+":"+str[3] + "，幣別欄位檢核錯誤。");
//							else
//								vo_detail.setCURRENCY_STD_ID(str[3]);
//						}
//						else if("13".equals(str[1].trim())) {
//							vo_detail.setINV_PRD_TYPE("1");
//							vo_detail.setINV_PRD_TYPE_2("3");
//						}
//						// 固定收益商品只要一個
//						else if("2".equals(str[1].trim())) {
//							vo_detail.setINV_PRD_TYPE("2");
//							if(prd_type.get(str[0]) > 1)
//								throw new APException("客戶風險屬性：" + str[0] + ":固定收益商品只需一個。");
//							prd_type.put(str[0], prd_type.get(str[0]) + 1);
//						}
						
						// 基股類商品 1=基金, 2=ETF, 3=投資型保險
						if("1".equals(str[1].trim())) {
							vo_detail.setINV_PRD_TYPE("3");
							vo_detail.setPRD_TYPE("MFD");
							// 補4碼0
							str[2] = StringUtils.leftPad(str[2].trim(), 4, "0");
							QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
							queryCondition.setQueryString("SELECT PRD_ID, RISKCATE_ID, CURRENCY_STD_ID FROM TBPRD_FUND where PRD_ID = :id");
							queryCondition.setObject("id", str[2]);
							List<Map<String, Object>> list = dam.exeQuery(queryCondition);
							if (list.size() > 0) {
								vo_detail.setPRD_ID(ObjectUtils.toString(list.get(0).get("PRD_ID")));
								vo_detail.setCURRENCY_STD_ID(ObjectUtils.toString(list.get(0).get("CURRENCY_STD_ID")));
								// RISKCATE_ID
								if(list.get(0).get("RISKCATE_ID") != null) {
									Integer num1 = new Integer(str[0].trim().substring(1, 2));
									Integer num2 = new Integer(list.get(0).get("RISKCATE_ID").toString().substring(1, 2));
									if(num2 > num1)
										throw new APException("客戶風險屬性：" + str[0]+":"+str[2] + "，發生商品不適配的情況。");
								} else
									throw new APException("客戶風險屬性：" + str[0]+":"+str[2] + "，此商品無風險等級。");
							} else
								throw new APException("客戶風險屬性：" + str[0]+":"+str[2] + "，商品主檔無此商品。");
							
						} else if ("2".equals(str[1].trim())) {
							vo_detail.setINV_PRD_TYPE("3");
							vo_detail.setPRD_TYPE("ETF");
							QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
							queryCondition.setQueryString("SELECT PRD_ID, RISKCATE_ID, CURRENCY_STD_ID FROM TBPRD_ETF where PRD_ID = :id");
							queryCondition.setObject("id", str[2].trim());
							List<Map<String, Object>> list = dam.exeQuery(queryCondition);
							if (list.size() > 0) {
								vo_detail.setPRD_ID(ObjectUtils.toString(list.get(0).get("PRD_ID")));
								vo_detail.setCURRENCY_STD_ID(ObjectUtils.toString(list.get(0).get("CURRENCY_STD_ID")));
								// RISKCATE_ID
								if(list.get(0).get("RISKCATE_ID") != null) {
									Integer num1 = new Integer(str[0].trim().substring(1, 2));
									Integer num2 = new Integer(list.get(0).get("RISKCATE_ID").toString().substring(1, 2));
									if(num2 > num1)
										throw new APException("客戶風險屬性：" + str[0]+":"+str[2] + "，發生商品不適配的情況。");
								} else
									throw new APException("客戶風險屬性：" + str[0]+":"+str[2] + "，此商品無風險等級。");
							} else
								throw new APException("客戶風險屬性：" + str[0]+":"+str[2] + "，商品主檔無此商品。");
							
						} else if ("3".equals(str[1].trim())) {
							vo_detail.setINV_PRD_TYPE("3");
							vo_detail.setPRD_TYPE("INS");
							QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
							queryCondition.setQueryString("SELECT PRD_ID, CURR_CD FROM TBPRD_INS where PRD_ID = :id AND INS_TYPE = '2'");
							queryCondition.setObject("id", str[2].trim());
							List<Map<String, Object>> list = dam.exeQuery(queryCondition);
							if (list.size() > 0) {
								vo_detail.setPRD_ID(ObjectUtils.toString(list.get(0).get("PRD_ID")));
								vo_detail.setCURRENCY_STD_ID(ObjectUtils.toString(list.get(0).get("CURR_CD")));
							} else
								throw new APException("客戶風險屬性：" + str[0]+":"+str[2] + "，商品主檔無此商品。");
						} else if ("4".equals(str[1].trim())) {
							vo_detail.setINV_PRD_TYPE("3");
							vo_detail.setPRD_TYPE("NANO");
							QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
							queryCondition.setQueryString("SELECT PRD_ID, RISKCATE_ID, CURRENCY_STD_ID FROM TBPRD_NANO where PRD_ID = :id");
							queryCondition.setObject("id", str[2].trim());
							List<Map<String, Object>> list = dam.exeQuery(queryCondition);
							if (list.size() > 0) {
								vo_detail.setPRD_ID(ObjectUtils.toString(list.get(0).get("PRD_ID")));
								vo_detail.setCURRENCY_STD_ID(ObjectUtils.toString(list.get(0).get("CURRENCY_STD_ID")));
								// RISKCATE_ID
								if(list.get(0).get("RISKCATE_ID") != null) {
									Integer num1 = new Integer(str[0].trim().substring(1, 2));
									Integer num2 = new Integer(list.get(0).get("RISKCATE_ID").toString().substring(1, 2));
									if(num2 > num1)
										throw new APException("客戶風險屬性：" + str[0]+":"+str[2] + "，發生商品不適配的情況。");
								} else
									throw new APException("客戶風險屬性：" + str[0]+":"+str[2] + "，此商品無風險等級。");
							} else
								throw new APException("客戶風險屬性：" + str[0]+":"+str[2] + "，商品主檔無此商品。");
							
						}else
							throw new APException("客戶風險屬性：" + str[0]+":"+str[1] + "，欄位檢核錯誤。");
					} else
						throw new APException("客戶風險屬性：" + str[0]+":投組類型欄位不得為空。");
					
					// STOCK_BOND_TYPE
					if(StringUtils.isNotBlank(str[4])) {
						String stock_bond_type = str[4].trim().toUpperCase();
						
						if(!"S".equals(stock_bond_type) && !"B".equals(stock_bond_type)) {
							throw new APException("客戶風險屬性：" + str[0]+":"+str[4] + "，股債頁籤欄位檢核錯誤。");
						} else 
							vo_detail.setSTOCK_BOND_TYPE(str[4]);
						
						//個風險等級佔比加總
						if("S".equals(stock_bond_type)) {
							stock_map2.put(str[0], stock_map2.get(str[0]).add(new BigDecimal(str[3])));							
						} else {
							bonds_map2.put(str[0], bonds_map2.get(str[0]).add(new BigDecimal(str[3])));
						}
					} else
						throw new APException("客戶風險屬性：" + str[0] + ":" + str[4] + "，股債頁籤欄位不得為空。");
					
					dam.create(vo_detail);
					success += 1;
				}
			}
			
			// 非特定 need one
//			if("2".equals(inputVO.getSetting_type())) {
//				for(Map.Entry<String, Integer> entry : prd_type.entrySet()) {
//					String key = entry.getKey();
//					Integer value = entry.getValue();
//					
//					if(value == 0)
//						throw new APException("客戶風險屬性：" + key + "，固定收益商品至少要有一個。");
//				}
//				for(Map.Entry<String, Integer> entry : prd_type2.entrySet()) {
//					String key = entry.getKey();
//					Integer value = entry.getValue();
//					
//					if(value == 0)
//						throw new APException("客戶風險屬性：" + key + "，台幣商品至少要有一個。");
//				}
//			}
			
			// 佔比100%
			if("1".equals(inputVO.getSetting_type())) {
				for(Map.Entry<String, BigDecimal> entry : spp_map.entrySet()) {
					String key = entry.getKey();
					BigDecimal value = entry.getValue();
					if(!new BigDecimal(100).equals(value))
						throw new APException("客戶風險屬性：" + key + "，佔比必須為100%。");
				}
//				for(Map.Entry<String, BigDecimal> entry : bonds_map.entrySet()) {
//					String key = entry.getKey();
//					BigDecimal value = entry.getValue();
//					
//					if(!new BigDecimal(100).equals(value))
//						throw new APException("客戶風險屬性：" + key + "，佔比必須為100%。");
//				}
			} else {
				for(Map.Entry<String, BigDecimal> entry : stock_map2.entrySet()) {
					String key = entry.getKey();
					BigDecimal value = entry.getValue();
					
					if(!new BigDecimal(100).equals(value))
						throw new APException("客戶風險屬性：" + key + "，佔比必須為100%。");
				}
				for(Map.Entry<String, BigDecimal> entry : bonds_map2.entrySet()) {
					String key = entry.getKey();
					BigDecimal value = entry.getValue();
					
					if(!new BigDecimal(100).equals(value))
						throw new APException("客戶風險屬性：" + key + "，佔比必須為100%。");
				}
			}
		}
		
		if(success == 0)
			throw new APException("ehl_01_cus130_003");
		
		this.sendRtnObject(return_VO);
	}
	public void downloadSimple(Object body, IPrimitiveMap header) throws Exception {
		FPS910InputVO inputVO = (FPS910InputVO) body;
		dam = this.getDataAccessManager();
		
		if("1".equals(inputVO.getSetting_type()))
			notifyClientToDownloadFile("doc//FPS//FPS910_EXAMPLE.csv", "目標理財規劃上傳範例.csv");
		else
			notifyClientToDownloadFile("doc//FPS//FPS910_EXAMPLE2.csv", "全資產規劃上傳範例.csv");
	}
	
	public void goReview(Object body, IPrimitiveMap header) throws Exception {
		FPS910InputVO inputVO = (FPS910InputVO) body;
		dam = this.getDataAccessManager();
		
		this.add_table(inputVO, true);
		
		this.sendRtnObject(null);
	}
	
	public void review(Object body, IPrimitiveMap header) throws Exception {
		FPS910InputVO inputVO = (FPS910InputVO) body;
		dam = this.getDataAccessManager();
		
		TBFPS_MODEL_ALLOCATION_HEADVO vo = new TBFPS_MODEL_ALLOCATION_HEADVO();
		vo = (TBFPS_MODEL_ALLOCATION_HEADVO) dam.findByPKey(TBFPS_MODEL_ALLOCATION_HEADVO.TABLE_UID, inputVO.getParam_no());
		if (vo != null) {
			vo.setSTATUS(inputVO.getStatus());
			if("W".equals(inputVO.getStatus()))
				vo.setSUBMIT_DATE(new Timestamp(System.currentTimeMillis()));
			dam.update(vo);
		} else
			throw new APException("ehl_01_common_001");
		
		this.sendRtnObject(null);
	}
	
	private String getSN(String name) throws JBranchException {
		String ans = "";
		switch(name) {
			case "MAIN":
				SerialNumberUtil sn = new SerialNumberUtil();
				try {
					ans = sn.getNextSerialNumber("TBFPS_MODEL_ALLOCATION_HEAD");
				}
				catch(Exception e) {
					sn.createNewSerial("TBFPS_MODEL_ALLOCATION_HEAD", "0000", 1, "m", new Timestamp(System.currentTimeMillis()), 1, new Long("9999"), "y", new Long("0"), null);
					ans = sn.getNextSerialNumber("TBFPS_MODEL_ALLOCATION_HEAD");
				}
				break;
			case "DETAIL":
				QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				queryCondition.setQueryString("SELECT SQ_TBFPS_MODEL_ALLOCATION.nextval AS SEQ FROM DUAL");
				List<Map<String, Object>> SEQLIST = dam.exeQuery(queryCondition);
				ans = ObjectUtils.toString(SEQLIST.get(0).get("SEQ"));
				break;
		}
		return ans;
	}
	
	public void download_main(Object body, IPrimitiveMap header) throws Exception {
		dam = this.getDataAccessManager();
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("select PRD_TYPE, PRD_ID, PRD_NAME, CURRENCY_STD_ID, LASTUPDATE, INV_SDATE, INV_EDATE from ( ");
		// TBPRD_FUND
		sql.append("select distinct 1 as num, '基金' AS PRD_TYPE, a.PRD_ID, a.FUND_CNAME AS PRD_NAME, a.CURRENCY_STD_ID, b.LASTUPDATE, b.MAIN_PRD_SDATE as INV_SDATE, b.MAIN_PRD_EDATE as INV_EDATE ");
		sql.append("from TBPRD_FUND a ");
		sql.append("left join TBPRD_FUNDINFO b on a.PRD_ID = b.PRD_ID ");
		sql.append("where b.MAIN_PRD = 'Y' AND TRUNC(SYSDATE) BETWEEN b.MAIN_PRD_SDATE AND b.MAIN_PRD_EDATE ");
		sql.append("union ");
		// TBPRD_ETF
		sql.append("select 2 as num, 'ETF' AS PRD_TYPE, PRD_ID, ETF_CNAME AS PRD_NAME, CURRENCY_STD_ID, LASTUPDATE, PRD_RANK_DATE AS INV_SDATE, NULL AS INV_EDATE ");
		sql.append("from TBPRD_ETF ");
		sql.append("where PRD_RANK is not null ");
		sql.append("union ");
		// TBPRD_STOCK
//		sql.append("select 3 as num, PRD_RANK, '股票' AS PRD_TYPE, PRD_ID, STOCK_CNAME AS PRD_NAME, CURRENCY_STD_ID, PRD_RANK_DATE as MAIN_PRD_SDATE, null as MAIN_PRD_EDATE ");
//		sql.append("from TBPRD_STOCK ");
//		sql.append("where PRD_RANK is not null ");
//		sql.append("union ");
		// TBPRD_BOND
		sql.append("select 4 as num, '海外債' AS PRD_TYPE, a.PRD_ID, a.BOND_CNAME_A AS PRD_NAME, a.CURRENCY_STD_ID, b.LASTUPDATE, b.INV_SDATE, b.INV_EDATE ");
		sql.append("from TBPRD_BOND a ");
		sql.append("left join TBPRD_BONDINFO b on a.PRD_ID = b.PRD_ID ");
		sql.append("where a.PRD_RANK is not null ");
		sql.append("union ");
		// TBPRD_SI
		sql.append("select 5 as num, 'SI' AS PRD_TYPE, a.PRD_ID, a.SI_CNAME AS PRD_NAME, a.CURRENCY_STD_ID, b.LASTUPDATE, b.INV_SDATE, b.INV_EDATE ");
		sql.append("from TBPRD_SI a ");
		sql.append("left join TBPRD_SIINFO b on a.PRD_ID = b.PRD_ID ");
		sql.append("where TRUNC(SYSDATE) BETWEEN b.INV_SDATE AND b.INV_EDATE ");
		sql.append("union ");
		// TBPRD_SN
		sql.append("select 6 as num, 'SN' AS PRD_TYPE, a.PRD_ID, a.SN_CNAME_A AS PRD_NAME, a.CURRENCY_STD_ID, b.LASTUPDATE, b.INV_SDATE, b.INV_EDATE ");
		sql.append("from TBPRD_SN a ");
		sql.append("left join TBPRD_SNINFO b on a.PRD_ID = b.PRD_ID ");
		sql.append("where TRUNC(SYSDATE) BETWEEN b.INV_SDATE AND b.INV_EDATE ");
		sql.append("union ");
		// TBPRD_INS 儲蓄型
		sql.append("select 7 as num, '儲蓄型保險' AS PRD_TYPE, PRD_ID, INSPRD_NAME AS PRD_NAME, CURR_CD AS CURRENCY_STD_ID, PRD_RANK_DATE as LASTUPDATE, PRD_RANK_DATE as INV_SDATE, null as INV_EDATE ");
		sql.append("from TBPRD_INS ");
		sql.append("where PRD_RANK is not null and INS_TYPE = '1' ");
		sql.append("union ");
		// TBPRD_INS 投資型
		sql.append("select 8 as num, '投資型保險' AS PRD_TYPE, PRD_ID, INSPRD_NAME AS PRD_NAME, CURR_CD AS CURRENCY_STD_ID, PRD_RANK_DATE as LASTUPDATE, PRD_RANK_DATE as INV_SDATE, null as INV_EDATE ");
		sql.append("from TBPRD_INS ");
		sql.append("where PRD_RANK is not null and INS_TYPE = '2' ");
		sql.append(") order by num, INV_SDATE desc ");
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		
		// gen csv
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
		String fileName = "主推商品_"+ sdf.format(new Date()) +".csv";
		List listCSV = new ArrayList();
		for (Map<String, Object> map : list) {
			// MAX 7
			String[] records = new String[7];
			int i = 0;
			records[i] = checkIsNull(map, "PRD_TYPE");
			records[++i] = checkIsNull(map, "PRD_ID");
			records[++i] = checkIsNull(map, "PRD_NAME");
			records[++i] = checkIsNull(map, "CURRENCY_STD_ID");
			if(StringUtils.isNotBlank(checkIsNull(map, "LASTUPDATE")))
				records[++i] = "=\"" + sdf2.format(sdf2.parse(checkIsNull(map, "LASTUPDATE"))) + "\"";
			else
				records[++i] = "";
			if(StringUtils.isNotBlank(checkIsNull(map, "INV_SDATE")))
				records[++i] = "=\"" + sdf2.format(sdf2.parse(checkIsNull(map, "INV_SDATE"))) + "\"";
			else
				records[++i] = "";
			if(StringUtils.isNotBlank(checkIsNull(map, "INV_EDATE")))
				records[++i] = "=\"" + sdf2.format(sdf2.parse(checkIsNull(map, "INV_EDATE"))) + "\"";
			else
				records[++i] = "";
			listCSV.add(records);
		}
		// header
		String[] csvHeader = new String[7];
		int j = 0;
		csvHeader[j] = "商品分類";
		csvHeader[++j] = "商品代號";
		csvHeader[++j] = "商品名稱";
		csvHeader[++j] = "幣別";
		csvHeader[++j] = "商品維護日";
		csvHeader[++j] = "主推/募集起日";
		csvHeader[++j] = "主推/募集迄日";
		
		CSVUtil csv = new CSVUtil();
		csv.setHeader(csvHeader);  
		csv.addRecordList(listCSV);
		String url = csv.generateCSV();
		// download
		notifyClientToDownloadFile(url, fileName);
	}
	private String checkIsNull(Map map, String key) {
		if(StringUtils.isNotBlank(ObjectUtils.toString(map.get(key)))) {
			return String.valueOf(map.get(key));
		}else{
			return "";
		}
	}
	
	
	
}