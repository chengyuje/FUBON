package com.systex.jbranch.app.server.fps.fps930;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.common.fps.table.TBFPS_CUSTRISK_VOLATILITYPK;
import com.systex.jbranch.app.common.fps.table.TBFPS_CUSTRISK_VOLATILITYVO;
import com.systex.jbranch.app.common.fps.table.TBFPS_CUSTRISK_VOLATILITY_HEADVO;
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

@Component("fps930")
@Scope("request")
public class FPS930 extends FubonWmsBizLogic {
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(FPS930.class);
	
	public void inquire(Object body, IPrimitiveMap header) throws JBranchException {
		FPS930InputVO inputVO = (FPS930InputVO) body;
		FPS930OutputVO return_VO = new FPS930OutputVO();
		WorkStation ws = DataManager.getWorkStation(uuid);
		dam = this.getDataAccessManager();
		
		// 判斷主管直接根據有無覆核權限
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT COUNT(ROLEID) AS COUNTS FROM TBSYSSECUROLPRIASS ");
		sql.append("WHERE PRIVILEGEID IN (SELECT PRIVILEGEID FROM TBSYSSECUPRIFUNMAP WHERE ITEMID = 'FPS930' AND FUNCTIONID = 'confirm') AND ROLEID = :roleID ");
		queryCondition.setObject("roleID", getUserVariable(FubonSystemVariableConsts.LOGINROLE));
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> privilegeCount = dam.exeQuery(queryCondition);
		//
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sql = new StringBuffer();
		sql.append("SELECT a.PARAM_NO, a.ALERT_TYPE, a.EFFECT_START_DATE, a.EFFECT_END_DATE, a.SUBMIT_DATE, a.STATUS, a.CREATOR, a.CREATETIME, a.LASTUPDATE, a.MODIFIER, ");
		sql.append("DECODE(a.MODIFIER, NULL, NULL, b.EMP_NAME || '-' || a.MODIFIER) as EDITOR, ");
		sql.append("DECODE(a.CREATOR, NULL, NULL, c.EMP_NAME || '-' || a.CREATOR) as ADDOR ");
		sql.append("FROM TBFPS_CUSTRISK_VOLATILITY_HEAD a ");
		sql.append("LEFT JOIN TBORG_MEMBER b on a.MODIFIER = b.EMP_ID ");
		sql.append("LEFT JOIN TBORG_MEMBER c on a.CREATOR = c.EMP_ID ");
		sql.append("WHERE a.PARAM_NO NOT IN (SELECT PARAM_NO FROM TBFPS_CUSTRISK_VOLATILITY_HEAD WHERE STATUS = 'S' AND CREATOR != :creator) ");
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
	
	public void init_detail(Object body, IPrimitiveMap header) throws JBranchException {
		FPS930InputVO inputVO = (FPS930InputVO) body;
		FPS930OutputVO return_VO = new FPS930OutputVO();
		dam = this.getDataAccessManager();
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT a.PARAM_NO, a.CUST_RISK_ATR, a.VOL_TYPE, a.VOLATILITY, a.REINV_STOCK_VOL, a.LASTUPDATE, ");
		sql.append("DECODE(a.MODIFIER, NULL, NULL, b.EMP_NAME || '-' || a.MODIFIER) as MODIFIER ");
		sql.append("FROM TBFPS_CUSTRISK_VOLATILITY a ");
		sql.append("LEFT JOIN TBORG_MEMBER b on a.MODIFIER = b.EMP_ID ");
		sql.append("WHERE a.PARAM_NO = :param_no ");
		sql.append("ORDER BY a.CUST_RISK_ATR ");
		queryCondition.setObject("param_no", inputVO.getParam_no());
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		return_VO.setResultList(list);
		
		this.sendRtnObject(return_VO);
	}
	
	public void create(Object body, IPrimitiveMap header) throws Exception {
		FPS930InputVO inputVO = (FPS930InputVO) body;
		dam = this.getDataAccessManager();
		
		// TBFPS_CUSTRISK_VOLATILITY_HEAD
		String seq_main = new SimpleDateFormat("yyyyMM").format(new Date()) + StringUtils.leftPad(getSN(), 4, "0");
		TBFPS_CUSTRISK_VOLATILITY_HEADVO vo_main = new TBFPS_CUSTRISK_VOLATILITY_HEADVO();
		vo_main.setPARAM_NO(seq_main);
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, 1);
		cal.set(Calendar.DATE, 1);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		vo_main.setEFFECT_START_DATE(new Timestamp(cal.getTime().getTime()));
		vo_main.setALERT_TYPE("1");
		vo_main.setSTATUS("S");
		dam.create(vo_main);
		
		this.sendRtnObject(null);
	}
	
	public void add(Object body, IPrimitiveMap header) throws Exception {
		FPS930InputVO inputVO = (FPS930InputVO) body;
		WorkStation ws = DataManager.getWorkStation(uuid);
		dam = this.getDataAccessManager();
		
		TBFPS_CUSTRISK_VOLATILITY_HEADVO vo = new TBFPS_CUSTRISK_VOLATILITY_HEADVO();
		vo = (TBFPS_CUSTRISK_VOLATILITY_HEADVO) dam.findByPKey(TBFPS_CUSTRISK_VOLATILITY_HEADVO.TABLE_UID, inputVO.getParam_no());
		if (vo != null) {
			vo.setSTATUS("S");
			vo.setALERT_TYPE(inputVO.getAlert_type());
			vo.setEFFECT_START_DATE(new Timestamp(inputVO.getDate().getTime()));
			vo.setCreator(ws.getUser().getUserID());
			dam.update(vo);
		} else
			throw new APException("ehl_01_common_001");
		
		// del first
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		queryCondition.setQueryString("DELETE FROM TBFPS_CUSTRISK_VOLATILITY WHERE PARAM_NO = :param_no");
		queryCondition.setObject("param_no", inputVO.getParam_no());
		dam.exeUpdate(queryCondition);
		// then add
		for(Map<String, Object> map : inputVO.getTotalList()) {
			TBFPS_CUSTRISK_VOLATILITYVO dvo = new TBFPS_CUSTRISK_VOLATILITYVO();
			TBFPS_CUSTRISK_VOLATILITYPK dpk = new TBFPS_CUSTRISK_VOLATILITYPK();
			dpk.setCUST_RISK_ATR(ObjectUtils.toString(map.get("CUST_RISK_ATR")));
			dpk.setPARAM_NO(inputVO.getParam_no());
			dvo.setcomp_id(dpk);
			dvo.setVOL_TYPE(ObjectUtils.toString(map.get("VOL_TYPE")));
			dvo.setVOLATILITY(new BigDecimal(ObjectUtils.toString(map.get("VOLATILITY"))));
			dvo.setREINV_STOCK_VOL(new BigDecimal(ObjectUtils.toString(map.get("REINV_STOCK_VOL"))));
			dam.create(dvo);
		}
		
		this.sendRtnObject(null);
	}
	
	public void copy(Object body, IPrimitiveMap header) throws Exception {
		FPS930InputVO inputVO = (FPS930InputVO) body;
		dam = this.getDataAccessManager();
		
		// TBFPS_CUSTRISK_VOLATILITY_HEAD
		String seq;
		TBFPS_CUSTRISK_VOLATILITY_HEADVO head_vo = new TBFPS_CUSTRISK_VOLATILITY_HEADVO();
		head_vo = (TBFPS_CUSTRISK_VOLATILITY_HEADVO) dam.findByPKey(TBFPS_CUSTRISK_VOLATILITY_HEADVO.TABLE_UID, inputVO.getParam_no());
		if (head_vo != null) {
			seq = new SimpleDateFormat("yyyyMM").format(new Date()) + StringUtils.leftPad(getSN(), 4, "0");
			TBFPS_CUSTRISK_VOLATILITY_HEADVO vo_new = new TBFPS_CUSTRISK_VOLATILITY_HEADVO();
			vo_new.setPARAM_NO(seq);
			vo_new.setEFFECT_START_DATE(head_vo.getEFFECT_START_DATE());
			vo_new.setEFFECT_END_DATE(head_vo.getEFFECT_END_DATE());
			vo_new.setALERT_TYPE(head_vo.getALERT_TYPE());
			vo_new.setSTATUS("S");
			dam.create(vo_new);
		} else
			throw new APException("ehl_01_common_001");
		
		// TBFPS_CUSTRISK_VOLATILITY
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		queryCondition.setQueryString("SELECT * FROM TBFPS_CUSTRISK_VOLATILITY WHERE PARAM_NO = :param_no");
		queryCondition.setObject("param_no", inputVO.getParam_no());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		for(Map<String, Object> map : list) {
			TBFPS_CUSTRISK_VOLATILITYVO dvo = new TBFPS_CUSTRISK_VOLATILITYVO();
			TBFPS_CUSTRISK_VOLATILITYPK dpk = new TBFPS_CUSTRISK_VOLATILITYPK();
			dpk.setCUST_RISK_ATR(ObjectUtils.toString(map.get("CUST_RISK_ATR")));
			dpk.setPARAM_NO(seq);
			dvo.setcomp_id(dpk);
			dvo.setVOL_TYPE(ObjectUtils.toString(map.get("VOL_TYPE")));
			dvo.setVOLATILITY(new BigDecimal(ObjectUtils.toString(map.get("VOLATILITY"))));
			dam.create(dvo);
		}
		
		this.sendRtnObject(null);
	}
	
	public void delete(Object body, IPrimitiveMap header) throws Exception {
		FPS930InputVO inputVO = (FPS930InputVO) body;
		dam = this.getDataAccessManager();
		
		TBFPS_CUSTRISK_VOLATILITY_HEADVO vo = new TBFPS_CUSTRISK_VOLATILITY_HEADVO();
		vo = (TBFPS_CUSTRISK_VOLATILITY_HEADVO) dam.findByPKey(TBFPS_CUSTRISK_VOLATILITY_HEADVO.TABLE_UID, inputVO.getParam_no());
		if (vo != null)
			dam.delete(vo);
		else
			throw new APException("ehl_01_common_001");
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		queryCondition.setQueryString("DELETE FROM TBFPS_CUSTRISK_VOLATILITY WHERE PARAM_NO = :param_no");
		queryCondition.setObject("param_no", inputVO.getParam_no());
		dam.exeUpdate(queryCondition);
		
		this.sendRtnObject(null);
	}
	
	public void goReview(Object body, IPrimitiveMap header) throws Exception {
		FPS930InputVO inputVO = (FPS930InputVO) body;
		dam = this.getDataAccessManager();
		
		this.add(body, header);
		
		TBFPS_CUSTRISK_VOLATILITY_HEADVO vo = new TBFPS_CUSTRISK_VOLATILITY_HEADVO();
		vo = (TBFPS_CUSTRISK_VOLATILITY_HEADVO) dam.findByPKey(TBFPS_CUSTRISK_VOLATILITY_HEADVO.TABLE_UID, inputVO.getParam_no());
		if (vo != null) {
			vo.setSTATUS("P");
			dam.update(vo);
		} else
			throw new APException("ehl_01_common_001");
		
		this.sendRtnObject(null);
	}
	
	public void review(Object body, IPrimitiveMap header) throws Exception {
		FPS930InputVO inputVO = (FPS930InputVO) body;
		dam = this.getDataAccessManager();
		
		TBFPS_CUSTRISK_VOLATILITY_HEADVO vo = new TBFPS_CUSTRISK_VOLATILITY_HEADVO();
		vo = (TBFPS_CUSTRISK_VOLATILITY_HEADVO) dam.findByPKey(TBFPS_CUSTRISK_VOLATILITY_HEADVO.TABLE_UID, inputVO.getParam_no());
		if (vo != null) {
			vo.setSTATUS(inputVO.getStatus());
			if("W".equals(inputVO.getStatus()))
				vo.setSUBMIT_DATE(new Timestamp(System.currentTimeMillis()));
			dam.update(vo);
		} else
			throw new APException("ehl_01_common_001");
		
		this.sendRtnObject(null);
	}
	
	private String getSN() throws JBranchException {
		SerialNumberUtil sn = new SerialNumberUtil();
		String seqNum = "";
		try {
			seqNum = sn.getNextSerialNumber("TBFPS_CUSTRISK_VOLATILITY_HEAD");
		}
		catch(Exception e) {
			sn.createNewSerial("TBFPS_CUSTRISK_VOLATILITY_HEAD", "0000", 1, "m", new Timestamp(System.currentTimeMillis()), 1, new Long("9999"), "y", new Long("0"), null);
			seqNum = sn.getNextSerialNumber("TBFPS_CUSTRISK_VOLATILITY_HEAD");
		}
		return seqNum;
	}
	
	
	
}