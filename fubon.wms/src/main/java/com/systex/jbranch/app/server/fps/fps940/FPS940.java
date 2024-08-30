package com.systex.jbranch.app.server.fps.fps940;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.common.fps.table.TBFPS_OTHER_PARAVO;
import com.systex.jbranch.app.common.fps.table.TBFPS_OTHER_PARA_HEADVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataManager.DataManager;
import com.systex.jbranch.platform.common.dataManager.WorkStation;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.serialnumber.SerialNumberUtil;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;

@Component("fps940")
@Scope("request")
public class FPS940 extends FubonWmsBizLogic {
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(FPS940.class);
	
	public void inquire(Object body, IPrimitiveMap header) throws JBranchException {
		FPS940InputVO inputVO = (FPS940InputVO) body;
		FPS940OutputVO return_VO = new FPS940OutputVO();
		WorkStation ws = DataManager.getWorkStation(uuid);
		dam = this.getDataAccessManager();
		
		// 判斷主管直接根據有無覆核權限
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT COUNT(ROLEID) AS COUNTS FROM TBSYSSECUROLPRIASS ");
		sql.append("WHERE PRIVILEGEID IN (SELECT PRIVILEGEID FROM TBSYSSECUPRIFUNMAP WHERE ITEMID = 'FPS940' AND FUNCTIONID = 'confirm') AND ROLEID = :roleID ");
		queryCondition.setObject("roleID", getUserVariable(FubonSystemVariableConsts.LOGINROLE));
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> privilegeCount = dam.exeQuery(queryCondition);
		//
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sql = new StringBuffer();
		sql.append("SELECT a.PARAM_NO, a.EFFECT_START_DATE, a.EFFECT_END_DATE, a.SUBMIT_DATE, a.STATUS, a.CREATOR, a.CREATETIME, a.LASTUPDATE, a.MODIFIER, ");
		sql.append("DECODE(a.MODIFIER, NULL, NULL, b.EMP_NAME || '-' || a.MODIFIER) as EDITOR, ");
		sql.append("DECODE(a.CREATOR, NULL, NULL, c.EMP_NAME || '-' || a.CREATOR) as ADDOR ");
		sql.append("FROM TBFPS_OTHER_PARA_HEAD a ");
		sql.append("LEFT JOIN TBORG_MEMBER b on a.MODIFIER = b.EMP_ID ");
		sql.append("LEFT JOIN TBORG_MEMBER c on a.CREATOR = c.EMP_ID ");
		sql.append("WHERE a.PARAM_NO NOT IN (SELECT PARAM_NO FROM TBFPS_OTHER_PARA_HEAD WHERE STATUS = 'S' AND CREATOR != :creator) ");
		queryCondition.setObject("creator", ws.getUser().getUserID());
		// where
		if (inputVO.getDate() != null) {
			sql.append("AND TO_CHAR(:date, 'yyyyMM') = TO_CHAR(a.EFFECT_START_DATE, 'yyyyMM') ");
			queryCondition.setObject("date", inputVO.getDate());
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
	
	public void init_detail(Object body, IPrimitiveMap header) throws Exception {
		FPS940InputVO inputVO = (FPS940InputVO) body;
		FPS940OutputVO return_VO = new FPS940OutputVO();
		dam = this.getDataAccessManager();
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		queryCondition.setQueryString("SELECT * from TBFPS_OTHER_PARA WHERE PARAM_NO = :param_no");
		queryCondition.setObject("param_no", inputVO.getParam_no());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		return_VO.setResultList(list);
		
		TBFPS_OTHER_PARA_HEADVO head_vo = (TBFPS_OTHER_PARA_HEADVO) dam.findByPKey(TBFPS_OTHER_PARA_HEADVO.TABLE_UID, inputVO.getParam_no());
		if("P".equals(head_vo.getSTATUS())) {
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sql = new StringBuffer();
			sql.append("SELECT a.EFFECT_START_DATE, b.* FROM TBFPS_OTHER_PARA_HEAD a ");
			sql.append("LEFT JOIN TBFPS_OTHER_PARA b on a.PARAM_NO = b.PARAM_NO ");
			sql.append("WHERE a.STATUS = 'A' ");
			queryCondition.setQueryString(sql.toString());
			List<Map<String, Object>> list2 = dam.exeQuery(queryCondition);
			return_VO.setResultList2(list2);
		}
		
		this.sendRtnObject(return_VO);
	}
	
	public void copy(Object body, IPrimitiveMap header) throws Exception {
		FPS940InputVO inputVO = (FPS940InputVO) body;
		dam = this.getDataAccessManager();
		
		// TBFPS_OTHER_PARA_HEAD
		String seq;
		TBFPS_OTHER_PARA_HEADVO head_vo = (TBFPS_OTHER_PARA_HEADVO) dam.findByPKey(TBFPS_OTHER_PARA_HEADVO.TABLE_UID, inputVO.getParam_no());
		if (head_vo != null) {
			seq = new SimpleDateFormat("yyyyMM").format(new Date()) + StringUtils.leftPad(getSN("MAIN"), 4, "0");
			TBFPS_OTHER_PARA_HEADVO vo_new = new TBFPS_OTHER_PARA_HEADVO();
			vo_new.setPARAM_NO(seq);
			vo_new.setEFFECT_START_DATE(head_vo.getEFFECT_START_DATE());
			vo_new.setEFFECT_END_DATE(head_vo.getEFFECT_END_DATE());
			vo_new.setSTATUS("S");
			dam.create(vo_new);
		} else
			throw new APException("ehl_01_common_001");
		
		// TBFPS_OTHER_PARA
		TBFPS_OTHER_PARAVO para_vo = (TBFPS_OTHER_PARAVO) dam.findByPKey(TBFPS_OTHER_PARAVO.TABLE_UID, inputVO.getParam_no());
		if (para_vo != null) {
			TBFPS_OTHER_PARAVO vo_new = new TBFPS_OTHER_PARAVO();
			vo_new.setPARAM_NO(seq);
			vo_new.setPLAN_AMT_1(para_vo.getPLAN_AMT_1());
			vo_new.setPLAN_AMT_2(para_vo.getPLAN_AMT_2());
			vo_new.setGEN_LEAD_PARA1(para_vo.getGEN_LEAD_PARA1());
			vo_new.setGEN_LEAD_PARA2(para_vo.getGEN_LEAD_PARA2());
			vo_new.setFAIL_STATUS(para_vo.getFAIL_STATUS());
			vo_new.setEFFICIENT_LIMIT(para_vo.getEFFICIENT_LIMIT());
			vo_new.setEFFICIENT_POINTS(para_vo.getEFFICIENT_POINTS());
			vo_new.setDEPOSIT_CURR(para_vo.getDEPOSIT_CURR());
			vo_new.setFUND_AUM(para_vo.getFUND_AUM());
			vo_new.setDEPOSIT_AUM(para_vo.getDEPOSIT_AUM());
			vo_new.setSISN_BASE_PURCHASE(para_vo.getSISN_BASE_PURCHASE());
			vo_new.setBOND_BASE_PURCHASE(para_vo.getBOND_BASE_PURCHASE());
			vo_new.setEXCHANGE_RATE(para_vo.getEXCHANGE_RATE());
			vo_new.setSPP_ACHIVE_RATE_1(para_vo.getSPP_ACHIVE_RATE_1());
			vo_new.setSPP_ACHIVE_RATE_2(para_vo.getSPP_ACHIVE_RATE_2());
			vo_new.setRF_RATE(para_vo.getRF_RATE());
			vo_new.setAVAILABLE_AMT(para_vo.getAVAILABLE_AMT());
			vo_new.setUNIVERSITY_FEE_1(para_vo.getUNIVERSITY_FEE_1());
			vo_new.setUNIVERSITY_FEE_2(para_vo.getUNIVERSITY_FEE_2());
			vo_new.setUNIVERSITY_FEE_3(para_vo.getUNIVERSITY_FEE_3());
			vo_new.setUNIVERSITY_COST_1(para_vo.getUNIVERSITY_COST_1());
			vo_new.setUNIVERSITY_COST_2(para_vo.getUNIVERSITY_COST_2());
			vo_new.setUNIVERSITY_COST_3(para_vo.getUNIVERSITY_COST_3());
			vo_new.setGRADUATED_FEE_1(para_vo.getGRADUATED_FEE_1());
			vo_new.setGRADUATED_FEE_2(para_vo.getGRADUATED_FEE_2());
			vo_new.setGRADUATED_FEE_3(para_vo.getGRADUATED_FEE_3());
			vo_new.setGRADUATED_COST_1(para_vo.getGRADUATED_COST_1());
			vo_new.setGRADUATED_COST_2(para_vo.getGRADUATED_COST_2());
			vo_new.setGRADUATED_COST_3(para_vo.getGRADUATED_COST_3());
			vo_new.setDOCTORAL_FEE_1(para_vo.getDOCTORAL_FEE_1());
			vo_new.setDOCTORAL_FEE_2(para_vo.getDOCTORAL_FEE_2());
			vo_new.setDOCTORAL_FEE_3(para_vo.getDOCTORAL_FEE_3());
			vo_new.setDOCTORAL_COST_1(para_vo.getDOCTORAL_COST_1());
			vo_new.setDOCTORAL_COST_2(para_vo.getDOCTORAL_COST_2());
			vo_new.setDOCTORAL_COST_3(para_vo.getDOCTORAL_COST_3());
			vo_new.setFEATURE_DESC(para_vo.getFEATURE_DESC());
			vo_new.setCASH_PREPARE_AGE_1(para_vo.getCASH_PREPARE_AGE_1());
			vo_new.setCASH_PREPARE_AGE_2(para_vo.getCASH_PREPARE_AGE_2());
			vo_new.setCASH_PREPARE_AGE_3(para_vo.getCASH_PREPARE_AGE_3());
			vo_new.setCASH_PREPARE_1(para_vo.getCASH_PREPARE_1());
			vo_new.setCASH_PREPARE_2(para_vo.getCASH_PREPARE_2());
			vo_new.setCASH_PREPARE_3(para_vo.getCASH_PREPARE_3());
			vo_new.setCASH_PREPARE_4(para_vo.getCASH_PREPARE_4());
			
			dam.create(vo_new);
		}
		this.sendRtnObject(null);
	}
	
	public void create(Object body, IPrimitiveMap header) throws Exception {
		FPS940InputVO inputVO = (FPS940InputVO) body;
		dam = this.getDataAccessManager();
		
		// TBFPS_OTHER_PARA_HEAD
		String seq_main = new SimpleDateFormat("yyyyMM").format(new Date()) + StringUtils.leftPad(getSN("MAIN"), 4, "0");
		TBFPS_OTHER_PARA_HEADVO vo_main = new TBFPS_OTHER_PARA_HEADVO();
		vo_main.setPARAM_NO(seq_main);
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, 1);
		cal.set(Calendar.DATE, 1);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		vo_main.setEFFECT_START_DATE(new Timestamp(cal.getTime().getTime()));
		vo_main.setSTATUS("S");
		dam.create(vo_main);
		
		this.sendRtnObject(null);
	}
	
	public void add(Object body, IPrimitiveMap header) throws Exception {
		FPS940InputVO inputVO = (FPS940InputVO) body;
		WorkStation ws = DataManager.getWorkStation(uuid);
		dam = this.getDataAccessManager();
		
		// TBFPS_OTHER_PARA_HEAD
		TBFPS_OTHER_PARA_HEADVO head_vo = (TBFPS_OTHER_PARA_HEADVO) dam.findByPKey(TBFPS_OTHER_PARA_HEADVO.TABLE_UID, inputVO.getParam_no());
		if (head_vo != null) {
			head_vo.setSTATUS("S");
			head_vo.setEFFECT_START_DATE(new Timestamp(inputVO.getDate().getTime()));
			head_vo.setCreator(ws.getUser().getUserID());
			dam.update(head_vo);
		} else
			throw new APException("ehl_01_common_001");
		
		// TBFPS_OTHER_PARA
		TBFPS_OTHER_PARAVO para_vo = (TBFPS_OTHER_PARAVO) dam.findByPKey(TBFPS_OTHER_PARAVO.TABLE_UID, inputVO.getParam_no());
		if (para_vo != null) {
			para_vo.setPLAN_AMT_1(inputVO.getPlan_amt_1());
			para_vo.setPLAN_AMT_2(inputVO.getPlan_amt_2());
			para_vo.setGEN_LEAD_PARA1(inputVO.getGen_lead_para1());
			para_vo.setGEN_LEAD_PARA2(inputVO.getGen_lead_para2());
			para_vo.setFAIL_STATUS(inputVO.getFail_status());
			para_vo.setEFFICIENT_LIMIT(inputVO.getEfficient_limit());
			para_vo.setEFFICIENT_POINTS(inputVO.getEfficient_points());
			para_vo.setDEPOSIT_CURR(inputVO.getDeposit_curr());
			para_vo.setFUND_AUM(inputVO.getFund_aum());
			para_vo.setDEPOSIT_AUM(inputVO.getDeposit_aum());
			para_vo.setSISN_BASE_PURCHASE(inputVO.getSisn_base_purchase());
			para_vo.setBOND_BASE_PURCHASE(inputVO.getBond_base_purchase());
			para_vo.setEXCHANGE_RATE(inputVO.getExchange_rate());
			para_vo.setSPP_ACHIVE_RATE_1(inputVO.getSpp_achive_rate_1());
			para_vo.setSPP_ACHIVE_RATE_2(inputVO.getSpp_achive_rate_2());
			para_vo.setRF_RATE(inputVO.getRf_rate());
			para_vo.setAVAILABLE_AMT(inputVO.getAvailable_amt());
			//
			para_vo.setUNIVERSITY_FEE_1(inputVO.getUniversity_fee_1());
			para_vo.setUNIVERSITY_FEE_2(inputVO.getUniversity_fee_2());
			para_vo.setUNIVERSITY_FEE_3(inputVO.getUniversity_fee_3());
			para_vo.setUNIVERSITY_COST_1(inputVO.getUniversity_cost_1());
			para_vo.setUNIVERSITY_COST_2(inputVO.getUniversity_cost_2());
			para_vo.setUNIVERSITY_COST_3(inputVO.getUniversity_cost_3());
			para_vo.setGRADUATED_FEE_1(inputVO.getGraduated_fee_1());
			para_vo.setGRADUATED_FEE_2(inputVO.getGraduated_fee_2());
			para_vo.setGRADUATED_FEE_3(inputVO.getGraduated_fee_3());
			para_vo.setGRADUATED_COST_1(inputVO.getGraduated_cost_1());
			para_vo.setGRADUATED_COST_2(inputVO.getGraduated_cost_2());
			para_vo.setGRADUATED_COST_3(inputVO.getGraduated_cost_3());
			para_vo.setDOCTORAL_FEE_1(inputVO.getDoctoral_fee_1());
			para_vo.setDOCTORAL_FEE_2(inputVO.getDoctoral_fee_2());
			para_vo.setDOCTORAL_FEE_3(inputVO.getDoctoral_fee_3());
			para_vo.setDOCTORAL_COST_1(inputVO.getDoctoral_cost_1());
			para_vo.setDOCTORAL_COST_2(inputVO.getDoctoral_cost_2());
			para_vo.setDOCTORAL_COST_3(inputVO.getDoctoral_cost_3());
			para_vo.setFEATURE_DESC(inputVO.getFeature_desc());
			para_vo.setCASH_PREPARE_AGE_1(inputVO.getCash_prepare_age_1());
			para_vo.setCASH_PREPARE_AGE_2(inputVO.getCash_prepare_age_2());
			para_vo.setCASH_PREPARE_AGE_3(inputVO.getCash_prepare_age_3());
			para_vo.setCASH_PREPARE_1(inputVO.getCash_prepare_1());
			para_vo.setCASH_PREPARE_2(inputVO.getCash_prepare_2());
			para_vo.setCASH_PREPARE_3(inputVO.getCash_prepare_3());
			para_vo.setCASH_PREPARE_4(inputVO.getCash_prepare_4());
			dam.update(para_vo);
		} else {
			TBFPS_OTHER_PARAVO vo_new = new TBFPS_OTHER_PARAVO();
			vo_new.setPARAM_NO(inputVO.getParam_no());
			vo_new.setPLAN_AMT_1(inputVO.getPlan_amt_1());
			vo_new.setPLAN_AMT_2(inputVO.getPlan_amt_2());
			vo_new.setGEN_LEAD_PARA1(inputVO.getGen_lead_para1());
			vo_new.setGEN_LEAD_PARA2(inputVO.getGen_lead_para2());
			vo_new.setFAIL_STATUS(inputVO.getFail_status());
			vo_new.setEFFICIENT_LIMIT(inputVO.getEfficient_limit());
			vo_new.setEFFICIENT_POINTS(inputVO.getEfficient_points());
			vo_new.setDEPOSIT_CURR(inputVO.getDeposit_curr());
			vo_new.setFUND_AUM(inputVO.getFund_aum());
			vo_new.setDEPOSIT_AUM(inputVO.getDeposit_aum());
			vo_new.setSISN_BASE_PURCHASE(inputVO.getSisn_base_purchase());
			vo_new.setBOND_BASE_PURCHASE(inputVO.getBond_base_purchase());
			vo_new.setEXCHANGE_RATE(inputVO.getExchange_rate());
			vo_new.setSPP_ACHIVE_RATE_1(inputVO.getSpp_achive_rate_1());
			vo_new.setSPP_ACHIVE_RATE_2(inputVO.getSpp_achive_rate_2());
			vo_new.setRF_RATE(inputVO.getRf_rate());
			vo_new.setAVAILABLE_AMT(inputVO.getAvailable_amt());
			//
			vo_new.setUNIVERSITY_FEE_1(inputVO.getUniversity_fee_1());
			vo_new.setUNIVERSITY_FEE_2(inputVO.getUniversity_fee_2());
			vo_new.setUNIVERSITY_FEE_3(inputVO.getUniversity_fee_3());
			vo_new.setUNIVERSITY_COST_1(inputVO.getUniversity_cost_1());
			vo_new.setUNIVERSITY_COST_2(inputVO.getUniversity_cost_2());
			vo_new.setUNIVERSITY_COST_3(inputVO.getUniversity_cost_3());
			vo_new.setGRADUATED_FEE_1(inputVO.getGraduated_fee_1());
			vo_new.setGRADUATED_FEE_2(inputVO.getGraduated_fee_2());
			vo_new.setGRADUATED_FEE_3(inputVO.getGraduated_fee_3());
			vo_new.setGRADUATED_COST_1(inputVO.getGraduated_cost_1());
			vo_new.setGRADUATED_COST_2(inputVO.getGraduated_cost_2());
			vo_new.setGRADUATED_COST_3(inputVO.getGraduated_cost_3());
			vo_new.setDOCTORAL_FEE_1(inputVO.getDoctoral_fee_1());
			vo_new.setDOCTORAL_FEE_2(inputVO.getDoctoral_fee_2());
			vo_new.setDOCTORAL_FEE_3(inputVO.getDoctoral_fee_3());
			vo_new.setDOCTORAL_COST_1(inputVO.getDoctoral_cost_1());
			vo_new.setDOCTORAL_COST_2(inputVO.getDoctoral_cost_2());
			vo_new.setDOCTORAL_COST_3(inputVO.getDoctoral_cost_3());
			vo_new.setFEATURE_DESC(inputVO.getFeature_desc());
			vo_new.setCASH_PREPARE_AGE_1(inputVO.getCash_prepare_age_1());
			vo_new.setCASH_PREPARE_AGE_2(inputVO.getCash_prepare_age_2());
			vo_new.setCASH_PREPARE_AGE_3(inputVO.getCash_prepare_age_3());
			vo_new.setCASH_PREPARE_1(inputVO.getCash_prepare_1());
			vo_new.setCASH_PREPARE_2(inputVO.getCash_prepare_2());
			vo_new.setCASH_PREPARE_3(inputVO.getCash_prepare_3());
			vo_new.setCASH_PREPARE_4(inputVO.getCash_prepare_4());
			dam.create(vo_new);
		}
		this.sendRtnObject(null);
	}
	
	public void delete(Object body, IPrimitiveMap header) throws Exception {
		FPS940InputVO inputVO = (FPS940InputVO) body;
		dam = this.getDataAccessManager();
		
		TBFPS_OTHER_PARA_HEADVO vo = (TBFPS_OTHER_PARA_HEADVO) dam.findByPKey(TBFPS_OTHER_PARA_HEADVO.TABLE_UID, inputVO.getParam_no());
		if (vo != null)
			dam.delete(vo);
		else
			throw new APException("ehl_01_common_001");
		
		TBFPS_OTHER_PARAVO oth_vo = (TBFPS_OTHER_PARAVO) dam.findByPKey(TBFPS_OTHER_PARAVO.TABLE_UID, inputVO.getParam_no());
		if (oth_vo != null)
			dam.delete(oth_vo);
		
		this.sendRtnObject(null);
	}
	
	public void goReview(Object body, IPrimitiveMap header) throws Exception {
		FPS940InputVO inputVO = (FPS940InputVO) body;
		dam = this.getDataAccessManager();
		
		this.add(body, header);
		
		TBFPS_OTHER_PARA_HEADVO vo = (TBFPS_OTHER_PARA_HEADVO) dam.findByPKey(TBFPS_OTHER_PARA_HEADVO.TABLE_UID, inputVO.getParam_no());
		if (vo != null) {
			vo.setSTATUS("P");
			dam.update(vo);
		} else
			throw new APException("ehl_01_common_001");
		
		this.sendRtnObject(null);
	}
	
	public void review(Object body, IPrimitiveMap header) throws Exception {
		FPS940InputVO inputVO = (FPS940InputVO) body;
		dam = this.getDataAccessManager();
		
		TBFPS_OTHER_PARA_HEADVO vo = (TBFPS_OTHER_PARA_HEADVO) dam.findByPKey(TBFPS_OTHER_PARA_HEADVO.TABLE_UID, inputVO.getParam_no());
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
					ans = sn.getNextSerialNumber("TBFPS_OTHER_PARA_HEAD");
				}
				catch(Exception e) {
					sn.createNewSerial("TBFPS_OTHER_PARA_HEAD", "0000", 1, "m", new Timestamp(System.currentTimeMillis()), 1, new Long("9999"), "y", new Long("0"), null);
					ans = sn.getNextSerialNumber("TBFPS_OTHER_PARA_HEAD");
				}
				break;
		}
		return ans;
	}
	
	
	
	
}