package com.systex.jbranch.app.server.fps.fps950;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
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

import com.systex.jbranch.app.common.fps.table.TBFPS_MARKET_TRENDVO;
import com.systex.jbranch.app.common.fps.table.TBFPS_MARKET_TREND_DETAILVO;
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

@Component("fps950")
@Scope("request")
public class FPS950 extends FubonWmsBizLogic {
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(FPS950.class);
	
	public void inquire(Object body, IPrimitiveMap header) throws JBranchException {
		FPS950InputVO inputVO = (FPS950InputVO) body;
		FPS950OutputVO return_VO = new FPS950OutputVO();
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
		sql.append("SELECT a.PARAM_NO, a.EFFECT_START_DATE, a.EFFECT_END_DATE, a.SUBMIT_DATE, a.MARKET_OVERVIEW, a.STATUS, a.CREATOR, a.CREATETIME, a.LASTUPDATE, a.MODIFIER, ");
		sql.append("DECODE(a.MODIFIER, NULL, NULL, b.EMP_NAME || '-' || a.MODIFIER) as EDITOR, ");
		sql.append("DECODE(a.CREATOR, NULL, NULL, c.EMP_NAME || '-' || a.CREATOR) as ADDOR ");
		sql.append("FROM TBFPS_MARKET_TREND a ");
		sql.append("LEFT JOIN TBORG_MEMBER b on a.MODIFIER = b.EMP_ID ");
		sql.append("LEFT JOIN TBORG_MEMBER c on a.CREATOR = c.EMP_ID ");
		sql.append("WHERE a.PARAM_NO NOT IN (SELECT PARAM_NO FROM TBFPS_MARKET_TREND WHERE STATUS = 'S' AND CREATOR != :creator) ");
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
		FPS950InputVO inputVO = (FPS950InputVO) body;
		FPS950OutputVO return_VO = new FPS950OutputVO();
		dam = this.getDataAccessManager();
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT a.PARAM_NO, a.SEQ_NO, a.TYPE, a.TREND, a.OVERVIEW, a.LASTUPDATE, ");
		sql.append("DECODE(a.MODIFIER, NULL, NULL, b.EMP_NAME || '-' || a.MODIFIER) as MODIFIER ");
		sql.append("FROM TBFPS_MARKET_TREND_DETAIL a ");
		sql.append("LEFT JOIN TBORG_MEMBER b on a.MODIFIER = b.EMP_ID ");
		sql.append("WHERE a.PARAM_NO = :param_no ORDER BY a.TYPE");
		queryCondition.setObject("param_no", inputVO.getParam_no());
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		return_VO.setResultList(list);
		
		this.sendRtnObject(return_VO);
	}
	
	public void add(Object body, IPrimitiveMap header) throws Exception {
		FPS950InputVO inputVO = (FPS950InputVO) body;
		WorkStation ws = DataManager.getWorkStation(uuid);
		dam = this.getDataAccessManager();
		
		TBFPS_MARKET_TRENDVO vo = new TBFPS_MARKET_TRENDVO();
		vo = (TBFPS_MARKET_TRENDVO) dam.findByPKey(TBFPS_MARKET_TRENDVO.TABLE_UID, inputVO.getParam_no());
		if (vo != null) {
			vo.setSTATUS("S");
			vo.setMARKET_OVERVIEW(inputVO.getMarket_overview());
			vo.setEFFECT_START_DATE(new Timestamp(inputVO.getDate().getTime()));
			vo.setCreator(ws.getUser().getUserID());
			dam.update(vo);
		} else
			throw new APException("ehl_01_common_001");
		
		// del first
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		queryCondition.setQueryString("DELETE FROM TBFPS_MARKET_TREND_DETAIL WHERE PARAM_NO = :param_no");
		queryCondition.setObject("param_no", inputVO.getParam_no());
		dam.exeUpdate(queryCondition);
		// then add
		for(Map<String, Object> map : inputVO.getTotalList()) {
			String seq = getSN("DETAIL");
			TBFPS_MARKET_TREND_DETAILVO dvo = new TBFPS_MARKET_TREND_DETAILVO();
			dvo.setPARAM_NO(inputVO.getParam_no());
			dvo.setSEQ_NO(seq);
			dvo.setTYPE(ObjectUtils.toString(map.get("TYPE")));
			dvo.setTREND(ObjectUtils.toString(map.get("TREND")));
			dvo.setOVERVIEW(ObjectUtils.toString(map.get("OVERVIEW")));
			dam.create(dvo);
		}
		
		this.sendRtnObject(null);
	}
	
	public void delete(Object body, IPrimitiveMap header) throws Exception {
		FPS950InputVO inputVO = (FPS950InputVO) body;
		dam = this.getDataAccessManager();
		
		TBFPS_MARKET_TRENDVO vo = new TBFPS_MARKET_TRENDVO();
		vo = (TBFPS_MARKET_TRENDVO) dam.findByPKey(TBFPS_MARKET_TRENDVO.TABLE_UID, inputVO.getParam_no());
		if (vo != null)
			dam.delete(vo);
		else
			throw new APException("ehl_01_common_001");
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		queryCondition.setQueryString("DELETE FROM TBFPS_MARKET_TREND_DETAIL WHERE PARAM_NO = :param_no");
		queryCondition.setObject("param_no", inputVO.getParam_no());
		dam.exeUpdate(queryCondition);
		
		this.sendRtnObject(null);
	}
	
	public void copy(Object body, IPrimitiveMap header) throws Exception {
		FPS950InputVO inputVO = (FPS950InputVO) body;
		dam = this.getDataAccessManager();
		
		// TBFPS_MARKET_TREND
		String seq;
		TBFPS_MARKET_TRENDVO vo = new TBFPS_MARKET_TRENDVO();
		vo = (TBFPS_MARKET_TRENDVO) dam.findByPKey(TBFPS_MARKET_TRENDVO.TABLE_UID, inputVO.getParam_no());
		if (vo != null) {
			seq = StringUtils.leftPad(getSN("MAIN"), 8, "0");
			TBFPS_MARKET_TRENDVO vo_new = new TBFPS_MARKET_TRENDVO();
			vo_new.setPARAM_NO(seq);
			vo_new.setEFFECT_START_DATE(vo.getEFFECT_START_DATE());
			vo_new.setEFFECT_END_DATE(vo.getEFFECT_END_DATE());
			vo_new.setMARKET_OVERVIEW(vo.getMARKET_OVERVIEW());
			vo_new.setSTATUS("S");
			dam.create(vo_new);
		} else
			throw new APException("ehl_01_common_001");
		
		// TBFPS_MARKET_TREND_DETAIL
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		queryCondition.setQueryString("SELECT * FROM TBFPS_MARKET_TREND_DETAIL WHERE PARAM_NO = :param_no");
		queryCondition.setObject("param_no", inputVO.getParam_no());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		for(Map<String, Object> map : list) {
			String seq2 = getSN("DETAIL");
			TBFPS_MARKET_TREND_DETAILVO vo_new2 = new TBFPS_MARKET_TREND_DETAILVO();
			vo_new2.setPARAM_NO(seq);
			vo_new2.setSEQ_NO(seq2);
			vo_new2.setTYPE(ObjectUtils.toString(map.get("TYPE")));
			vo_new2.setTREND(ObjectUtils.toString(map.get("TREND")));
			vo_new2.setOVERVIEW(ObjectUtils.toString(map.get("OVERVIEW")));
			dam.create(vo_new2);
		}
		
		this.sendRtnObject(null);
	}
	
	public void upload(Object body, IPrimitiveMap header) throws Exception {
		FPS950InputVO inputVO = (FPS950InputVO) body;
		FPS950OutputVO return_VO = new FPS950OutputVO();
		XmlInfo xmlInfo = new XmlInfo();
		dam = this.getDataAccessManager();
		
		Integer success = 0;
		List<String> error = new ArrayList<String>();
		List<String> error2 = new ArrayList<String>();
		List<String> error3 = new ArrayList<String>();
		Set<String> nameList = new HashSet<String>();
		String tempPath = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
		List<String[]> dataCsv = CSVUtil.getBig5CSVFile(tempPath, inputVO.getFileName());
		if(!dataCsv.isEmpty()) {
			Map<String, String> mkt_tier3 = xmlInfo.doGetVariable("PRD.MKT_TIER3", FormatHelper.FORMAT_3);
			mkt_tier3.remove("033");mkt_tier3.remove("999");
			Map<String, String> trend_type = xmlInfo.doGetVariable("FPS.TREND_TYPE", FormatHelper.FORMAT_3);
			// TBFPS_MARKET_TREND
			String seq_main = StringUtils.leftPad(getSN("MAIN"), 8, "0");
			TBFPS_MARKET_TRENDVO vo_main = new TBFPS_MARKET_TRENDVO();
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
			//
			for(int i = 0;i < dataCsv.size();i++) {
				String[] str = dataCsv.get(i);
				if(i == 0) {
					try {
						if(!"市場別".equals(str[0].substring(0, 3)))
							throw new Exception(str[0]);
						else if(!"市場趨勢".equals(str[1].substring(0, 4)))
							throw new Exception(str[1]);
						else if(!"市場概況".equals(str[2].trim()))
							throw new Exception(str[2]);
					} catch(Exception ex) {
						throw new APException(ex.getMessage() + ":上傳格式錯誤，請下載範例檔案");
					}
					continue;
				}
				// 使用者很愛重覆上傳
				if(nameList.contains(str[0].trim())) {
					error.add(str[0]);
					continue;
				}
				nameList.add(str[0].trim());
				
				// TBFPS_MARKET_TREND_DETAIL
				String seq_de = getSN("DETAIL");
				TBFPS_MARKET_TREND_DETAILVO vo_de = new TBFPS_MARKET_TREND_DETAILVO();
				vo_de.setPARAM_NO(seq_main);
				vo_de.setSEQ_NO(seq_de);
				//
				if(StringUtils.isNotBlank(str[0])) {
					// 補3碼0
					str[0] = StringUtils.leftPad(str[0].trim(), 3, "0");
					if(StringUtils.isBlank(mkt_tier3.get(str[0]))) {
						error2.add(str[0]);
						continue;
					}
					vo_de.setTYPE(str[0]);
				}
				else {
					error3.add(str[0]);
					continue;
				}
				//
				if(StringUtils.isNotBlank(str[1])) {
					if(StringUtils.isBlank(trend_type.get(str[1]))) {
						error2.add(str[0]+":"+str[1]);
						continue;
					}
					vo_de.setTREND(str[1]);
				}
				else {
					error3.add(str[0]+":"+str[1]);
					continue;
				}
				//
				if(utf_8_length(str[2]) > 4000) {
					error2.add(str[0]+":"+str[2]);
					continue;
				}
				else if(StringUtils.isBlank(str[0])) {
					error3.add(str[0]+":"+str[2]);
					continue;
				}
				else
					vo_de.setOVERVIEW(str[2].trim());
				//
				dam.create(vo_de);
				
				success += 1;
			}
		}
		
		if(success == 0)
			throw new APException("ehl_01_cus130_003");
		return_VO.setErrorList(error);
		return_VO.setErrorList2(error2);
		return_VO.setErrorList3(error3);
		this.sendRtnObject(return_VO);
	}
	public void downloadSimple(Object body, IPrimitiveMap header) throws Exception {
		// gen csv
		XmlInfo xmlInfo = new XmlInfo();
		String fileName = "上傳範例.csv";
		// header
		String[] csvHeader = new String[3];
		TreeMap<String, String> map1 = new TreeMap<>(xmlInfo.doGetVariable("PRD.MKT_TIER3", FormatHelper.FORMAT_3));
		map1.remove("033");map1.remove("999");
		csvHeader[0] = "市場別" + ObjectUtils.toString(map1);
		TreeMap<String, String> map2 = new TreeMap<>(xmlInfo.doGetVariable("FPS.TREND_TYPE", FormatHelper.FORMAT_3));
		csvHeader[1] = "市場趨勢" + ObjectUtils.toString(map2);
		csvHeader[2] = "市場概況";
		
		CSVUtil csv = new CSVUtil();
		csv.setHeader(csvHeader);
		String url = csv.generateCSV();
		// download
		notifyClientToDownloadFile(url, fileName);
	}
	
	public void downloadMarket(Object body, IPrimitiveMap header) throws Exception {
		XmlInfo xmlInfo = new XmlInfo();
		TreeMap<String, String> map1 = new TreeMap<>(xmlInfo.doGetVariable("PRD.MKT_TIER3", FormatHelper.FORMAT_3));
		map1.remove("033");map1.remove("999");
		
		List listCSV = new ArrayList();
		for (Map.Entry<String, String> entry : map1.entrySet()) {
			String[] records = new String[2];
			records[0] = "=\"" + entry.getKey() + "\"";
			records[1] = entry.getValue();
			listCSV.add(records);
		}
		// header
		String[] csvHeader = new String[2];
		csvHeader[0] = "代碼";
		csvHeader[1] = "名稱";
		
		CSVUtil csv = new CSVUtil();
		csv.setHeader(csvHeader);  
		csv.addRecordList(listCSV);
		String url = csv.generateCSV();
		// download
		this.notifyClientToDownloadFile(url, "市場別代碼與名稱對照表.csv");
	}
	
	public void goReview(Object body, IPrimitiveMap header) throws Exception {
		FPS950InputVO inputVO = (FPS950InputVO) body;
		dam = this.getDataAccessManager();
		
		this.add(body, header);
		
		TBFPS_MARKET_TRENDVO vo = new TBFPS_MARKET_TRENDVO();
		vo = (TBFPS_MARKET_TRENDVO) dam.findByPKey(TBFPS_MARKET_TRENDVO.TABLE_UID, inputVO.getParam_no());
		if (vo != null) {
			vo.setSTATUS("P");
			dam.update(vo);
		} else
			throw new APException("ehl_01_common_001");
		
		this.sendRtnObject(null);
	}
	
	public void review(Object body, IPrimitiveMap header) throws Exception {
		FPS950InputVO inputVO = (FPS950InputVO) body;
		dam = this.getDataAccessManager();
		
		TBFPS_MARKET_TRENDVO vo = new TBFPS_MARKET_TRENDVO();
		vo = (TBFPS_MARKET_TRENDVO) dam.findByPKey(TBFPS_MARKET_TRENDVO.TABLE_UID, inputVO.getParam_no());
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
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		switch(name) {
			case "MAIN":
				sql.append("SELECT SQ_TBFPS_MARKET_TREND_MAIN.nextval AS SEQ FROM DUAL");
				break;
			case "DETAIL":
				sql.append("SELECT SQ_TBFPS_MARKET_TREND_DETAIL.nextval AS SEQ FROM DUAL");
				break;
		}
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> SEQLIST = dam.exeQuery(queryCondition);
		return ObjectUtils.toString(SEQLIST.get(0).get("SEQ"));
	}
	
	
	
}