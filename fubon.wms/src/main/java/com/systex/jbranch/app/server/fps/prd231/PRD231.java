package com.systex.jbranch.app.server.fps.prd231;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

import com.systex.jbranch.app.common.fps.table.TBPRD_INVEST_AREAVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataManager.DataManager;
import com.systex.jbranch.platform.common.dataManager.WorkStation;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.CSVUtil;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * prd231
 * 
 * @author moron
 * @date 2016/09/1
 * @spec null
 */
@Component("prd231")
@Scope("request")
public class PRD231 extends FubonWmsBizLogic {
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(PRD231.class);
	
	public void inquire(Object body, IPrimitiveMap header) throws JBranchException {
		PRD231InputVO inputVO = (PRD231InputVO) body;
		PRD231OutputVO return_VO = new PRD231OutputVO();
		dam = this.getDataAccessManager();
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("select a.GLOBAL_ID,a.MKT_TIER1,a.MKT_TIER2,a.MKT_TIER3,b.GLOBAL_LIPPER_EID,b.GLOBAL_LIPPER_CID from ");
		sql.append("TBPRD_INVEST_AREA a left join TBPRD_GLOBAL_CLASS b on a.GLOBAL_ID = b.GLOBAL_ID WHERE 1=1 ");
		if (StringUtils.isNotBlank(inputVO.getMkt_tier1())) {
			sql.append("AND a.MKT_TIER1 = :mkt_tier1 ");
			queryCondition.setObject("mkt_tier1", inputVO.getMkt_tier1());
		}	
		if (StringUtils.isNotBlank(inputVO.getMkt_tier2())) {
			sql.append("AND a.MKT_TIER2 = :mkt_tier2 ");
			queryCondition.setObject("mkt_tier2", inputVO.getMkt_tier2());
		}	
		if (StringUtils.isNotBlank(inputVO.getMkt_tier3())) {
			sql.append("AND a.MKT_TIER3 = :mkt_tier3 ");
			queryCondition.setObject("mkt_tier3", inputVO.getMkt_tier3());
		}	
		sql.append("ORDER BY a.MKT_TIER1,a.MKT_TIER2,a.MKT_TIER3");
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		return_VO.setResultList(list);
		this.sendRtnObject(return_VO);
	}
	
	public void getGlobal(Object body, IPrimitiveMap header) throws JBranchException {
		PRD231OutputVO return_VO = new PRD231OutputVO();
		dam = this.getDataAccessManager();
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("select distinct GLOBAL_ID,GLOBAL_LIPPER_CID from TBPRD_GLOBAL_CLASS ");
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		return_VO.setResultList(list);
		this.sendRtnObject(return_VO);
	}
	
	public void getEID(Object body, IPrimitiveMap header) throws JBranchException {
		PRD231InputVO inputVO = (PRD231InputVO) body;
		PRD231OutputVO return_VO = new PRD231OutputVO();
		dam = this.getDataAccessManager();
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("select GLOBAL_LIPPER_EID from TBPRD_GLOBAL_CLASS where GLOBAL_ID = :id ");
		queryCondition.setObject("id", inputVO.getGlobal_id());
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		if (list.size() > 0)
			return_VO.setEid(ObjectUtils.toString(list.get(0).get("GLOBAL_LIPPER_EID")));
		else
			return_VO.setEid(null);
		
		// check exist
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sql = new StringBuffer();
		sql.append("SELECT GLOBAL_ID FROM TBPRD_INVEST_AREA where GLOBAL_ID = :id ");
		queryCondition.setObject("id", inputVO.getGlobal_id());
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list2 = dam.exeQuery(queryCondition);
		if (list2.size() > 0)
			return_VO.setIfExist(true);
		else
			return_VO.setIfExist(false);
		
		this.sendRtnObject(return_VO);
	}
	
	public void modify(Object body, IPrimitiveMap header) throws JBranchException {
		PRD231InputVO inputVO = (PRD231InputVO) body;
		dam = this.getDataAccessManager();
		
		TBPRD_INVEST_AREAVO vo = new TBPRD_INVEST_AREAVO();
		vo = (TBPRD_INVEST_AREAVO) dam.findByPKey(TBPRD_INVEST_AREAVO.TABLE_UID, inputVO.getGlobal_id());
		if (vo != null) {
			// edit
			vo.setMKT_TIER1(inputVO.getMkt_tier1());
			vo.setMKT_TIER2(inputVO.getMkt_tier2());
			vo.setMKT_TIER3(inputVO.getMkt_tier3());
			dam.update(vo);
		} else {
			// add
			TBPRD_INVEST_AREAVO cvo = new TBPRD_INVEST_AREAVO();
			cvo.setGLOBAL_ID(inputVO.getGlobal_id());
			cvo.setMKT_TIER1(inputVO.getMkt_tier1());
			cvo.setMKT_TIER2(inputVO.getMkt_tier2());
			cvo.setMKT_TIER3(inputVO.getMkt_tier3());
			dam.create(cvo);
		}
		this.sendRtnObject(null);
	}
	
	public void delete(Object body, IPrimitiveMap header) throws JBranchException {
		PRD231InputVO inputVO = (PRD231InputVO) body;
		dam = this.getDataAccessManager();
		
		TBPRD_INVEST_AREAVO vo = new TBPRD_INVEST_AREAVO();
		vo = (TBPRD_INVEST_AREAVO) dam.findByPKey(TBPRD_INVEST_AREAVO.TABLE_UID, inputVO.getGlobal_id());
		if (vo != null) {
			dam.delete(vo);
		} else {
			// 顯示資料不存在
			throw new APException("ehl_01_common_001");
		}
		this.sendRtnObject(null);
	}

	public void upload(Object body, IPrimitiveMap header) throws JBranchException {
		PRD231InputVO inputVO = (PRD231InputVO) body;
		PRD231OutputVO return_VO = new PRD231OutputVO();
		XmlInfo xmlInfo = new XmlInfo();
		dam = this.getDataAccessManager();
		
		// 2017/7/3 聽說這隻上傳時都會是中文
		List<String> error = new ArrayList<String>();
		List<String> error2 = new ArrayList<String>();
		List<String> error3 = new ArrayList<String>();
		List<String> error4 = new ArrayList<String>();
		Set<String> idList = new HashSet<String>();
		String tempPath = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
		List<String[]> dataCsv = CSVUtil.getBig5CSVFile(tempPath, inputVO.getFileName());
		if(!dataCsv.isEmpty()) {
			Map<String, String> mkt_tier1 = xmlInfo.doGetVariable("PRD.MKT_TIER1", FormatHelper.FORMAT_3);
			Map<String, String> lipper_map = new HashMap<>();
			QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			queryCondition.setQueryString("select distinct GLOBAL_LIPPER_CID, GLOBAL_ID from TBPRD_GLOBAL_CLASS");
			List<Map<String, String>> list = dam.exeQuery(queryCondition);
			for(Map<String, String> map : list) {
				lipper_map.put(map.get("GLOBAL_ID"), map.get("GLOBAL_LIPPER_CID"));
			}
			for(int i = 0;i < dataCsv.size();i++) {
				String[] str = dataCsv.get(i);
				if(i == 0) {
					try {
						if(!"第一層".equals(str[0].substring(0, 3)))
							throw new Exception(str[0]);
						else if(!"第二層".equals(str[1].substring(0, 3)))
							throw new Exception(str[1]);
						else if(!"第三層".equals(str[2].substring(0, 3)))
							throw new Exception(str[2]);
						else if(!"Lipper Global分類".equals(str[3].substring(0, 15)))
							throw new Exception(str[3]);
					} catch(Exception ex) {
						throw new APException(ex.getMessage() + ":上傳格式錯誤，請下載範例檔案");
					}
					continue;
				}
				// check GLOBAL_LIPPER_CID
				if(StringUtils.isBlank(str[3])) {
					error.add(str[3]);
					continue;
				}
				if(StringUtils.isBlank(lipper_map.get(str[3].trim()))) {
					error.add(str[3]);
					continue;
				}
				// 使用者很愛重覆上傳
				if(idList.contains(str[3].trim())) {
					error2.add(str[3]);
					continue;
				}
				idList.add(str[3].trim());
				// TBPRD_INVEST_AREA check edit
				Boolean exist = false;
				queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				StringBuffer sql = new StringBuffer();
				sql.append("SELECT GLOBAL_ID FROM TBPRD_INVEST_AREA where GLOBAL_ID = :id ");
				queryCondition.setObject("id", str[3].trim());
				queryCondition.setQueryString(sql.toString());
				List<Map<String, Object>> list2 = dam.exeQuery(queryCondition);
				if (list2.size() > 0)
					exist = true;
				
				// update TBPRD_INVEST_AREAVO
				if(exist) {
					TBPRD_INVEST_AREAVO vo = new TBPRD_INVEST_AREAVO();
					vo = (TBPRD_INVEST_AREAVO) dam.findByPKey(TBPRD_INVEST_AREAVO.TABLE_UID, str[3].trim());
					// 第1層
					if(StringUtils.isBlank(str[0])) {
						error3.add(str[3]+":"+str[0]);
						continue;
					}
					String mkt1str = getKeyByValue(mkt_tier1, str[0]);
					if(StringUtils.isBlank(mkt1str)) {
						error3.add(str[3]+":"+str[0]);
						continue;
					}
					vo.setMKT_TIER1(str[0]);
					// 第2層
					if(StringUtils.isBlank(str[1])) {
						error3.add(str[3]+":"+str[1]);
						continue;
					}
					// check in str[0]
					queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
					sql = new StringBuffer();
					sql.append("select A.NEXT_TIER_VALUE, B.PARAM_NAME from TBPRD_INVEST_AREA_MAPPING A ");
					sql.append("right join TBSYSPARAMETER B on B.PARAM_CODE = A.NEXT_TIER_VALUE and B.PARAM_TYPE='PRD.MKT_TIER2' ");
					sql.append("where A.TIER = '1' and A.VALUE = :value ");
					queryCondition.setObject("value", str[0]);
					queryCondition.setQueryString(sql.toString());
					List<Map<String, String>> mkt2list = dam.exeQuery(queryCondition);
					Map<String, String> mkt_tier2 = new HashMap<>();
					for(Map<String, String> map : mkt2list) {
						mkt_tier2.put(map.get("NEXT_TIER_VALUE"), map.get("PARAM_NAME"));
					}
					
					String mkt2str = mkt_tier2.get(str[1]);
					if(StringUtils.isBlank(mkt2str)) {
						error4.add(str[3]+":"+str[1]);
						continue;
					}
					vo.setMKT_TIER2(str[1]);
					// 第3層
					if(StringUtils.isBlank(str[2])) {
						error3.add(str[3]+":"+str[2]);
						continue;
					}
					// check in str[1]
					queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
					sql = new StringBuffer();
					sql.append("select A.NEXT_TIER_VALUE, B.PARAM_NAME from TBPRD_INVEST_AREA_MAPPING A ");
					sql.append("right join TBSYSPARAMETER B on B.PARAM_CODE = A.NEXT_TIER_VALUE and B.PARAM_TYPE='PRD.MKT_TIER3' ");
					sql.append("where A.TIER = '2' and A.VALUE = :value ");
					queryCondition.setObject("value", str[1]);
					queryCondition.setQueryString(sql.toString());
					List<Map<String, String>> mkt3list = dam.exeQuery(queryCondition);
					Map<String, String> mkt_tier3 = new HashMap<>();
					for(Map<String, String> map : mkt3list) {
						mkt_tier3.put(map.get("NEXT_TIER_VALUE"), map.get("PARAM_NAME"));
					}
					
					String mkt3str = mkt_tier3.get(str[2]);
					if(StringUtils.isBlank(mkt3str)) {
						error4.add(str[3]+":"+str[2]);
						continue;
					}
					vo.setMKT_TIER3(str[2]);
					//
					dam.update(vo);
				}
				// add TBPRD_INVEST_AREAVO
				else {
					TBPRD_INVEST_AREAVO vo = new TBPRD_INVEST_AREAVO();
					vo.setGLOBAL_ID(str[3].trim());
					// 第1層
					if(StringUtils.isBlank(str[0])) {
						error3.add(str[3]+":"+str[0]);
						continue;
					}
					String mkt1str = getKeyByValue(mkt_tier1, str[0]);
					if(StringUtils.isBlank(mkt1str)) {
						error3.add(str[3]+":"+str[0]);
						continue;
					}
					vo.setMKT_TIER1(str[0]);
					// 第2層
					if(StringUtils.isBlank(str[1])) {
						error3.add(str[3]+":"+str[1]);
						continue;
					}
					// check in str[0]
					queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
					sql = new StringBuffer();
					sql.append("select A.NEXT_TIER_VALUE, B.PARAM_NAME from TBPRD_INVEST_AREA_MAPPING A ");
					sql.append("right join TBSYSPARAMETER B on B.PARAM_CODE = A.NEXT_TIER_VALUE and B.PARAM_TYPE='PRD.MKT_TIER2' ");
					sql.append("where A.TIER = '1' and A.VALUE = :value ");
					queryCondition.setObject("value", str[0]);
					queryCondition.setQueryString(sql.toString());
					List<Map<String, String>> mkt2list = dam.exeQuery(queryCondition);
					Map<String, String> mkt_tier2 = new HashMap<>();
					for(Map<String, String> map : mkt2list) {
						mkt_tier2.put(map.get("NEXT_TIER_VALUE"), map.get("PARAM_NAME"));
					}
					
					String mkt2str = mkt_tier2.get(str[1]);
					if(StringUtils.isBlank(mkt2str)) {
						error4.add(str[3]+":"+str[1]);
						continue;
					}
					vo.setMKT_TIER2(str[1]);
					// 第3層
					if(StringUtils.isBlank(str[2])) {
						error3.add(str[3]+":"+str[2]);
						continue;
					}
					// check in str[1]
					queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
					sql = new StringBuffer();
					sql.append("select A.NEXT_TIER_VALUE, B.PARAM_NAME from TBPRD_INVEST_AREA_MAPPING A ");
					sql.append("right join TBSYSPARAMETER B on B.PARAM_CODE = A.NEXT_TIER_VALUE and B.PARAM_TYPE='PRD.MKT_TIER3' ");
					sql.append("where A.TIER = '2' and A.VALUE = :value ");
					queryCondition.setObject("value", str[1]);
					queryCondition.setQueryString(sql.toString());
					List<Map<String, String>> mkt3list = dam.exeQuery(queryCondition);
					Map<String, String> mkt_tier3 = new HashMap<>();
					for(Map<String, String> map : mkt3list) {
						mkt_tier3.put(map.get("NEXT_TIER_VALUE"), map.get("PARAM_NAME"));
					}
					
					String mkt3str = mkt_tier3.get(str[2]);
					if(StringUtils.isBlank(mkt3str)) {
						error4.add(str[3]+":"+str[2]);
						continue;
					}
					vo.setMKT_TIER3(str[2]);
					//
					dam.create(vo);
				}
			}
		}
		return_VO.setErrorList(error);
		return_VO.setErrorList2(error2);
		return_VO.setErrorList3(error3);
		return_VO.setErrorList4(error4);
		this.sendRtnObject(return_VO);
	}
	
	public void downloadSimple(Object body, IPrimitiveMap header) throws Exception {
		dam = this.getDataAccessManager();
		
		// gen csv
		XmlInfo xmlInfo = new XmlInfo();
		String fileName = "上傳指定投資區域對照表範例.csv";
		// header
		String[] csvHeader = new String[4];
		TreeMap<String, String> map1 = new TreeMap<>(xmlInfo.doGetVariable("PRD.MKT_TIER1", FormatHelper.FORMAT_3));
		csvHeader[0] = "第一層(中文)：" + ObjectUtils.toString(map1);
		// WTF LUL
		TreeMap<String, String> map2 = new TreeMap<>();
		TreeMap<String, String> map3 = new TreeMap<>();
		for (Map.Entry<String, String> entry : map1.entrySet()) {
			// two
			QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sql = new StringBuffer();
			sql.append("select A.NEXT_TIER_VALUE,B.PARAM_NAME from TBPRD_INVEST_AREA_MAPPING A ");
			sql.append("right join TBSYSPARAMETER B on B.PARAM_CODE = A.NEXT_TIER_VALUE and B.PARAM_TYPE='PRD.MKT_TIER2' ");
			sql.append("where A.TIER = '1' and A.VALUE = :value ");
			queryCondition.setObject("value", entry.getKey());
			queryCondition.setQueryString(sql.toString());
			List<Map<String, String>> list = dam.exeQuery(queryCondition);
			map2.put(entry.getKey() + "：" + entry.getValue(), "[" + joinListByCom(list, ", ") + "]");
			// three
			for(Map<String, String> twoMap : list) {
				QueryConditionIF queryCondition2 = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				StringBuffer sql2 = new StringBuffer();
				sql2.append("select A.NEXT_TIER_VALUE,B.PARAM_NAME from TBPRD_INVEST_AREA_MAPPING A ");
				sql2.append("right join TBSYSPARAMETER B on B.PARAM_CODE = A.NEXT_TIER_VALUE and B.PARAM_TYPE='PRD.MKT_TIER3' ");
				sql2.append("where A.TIER = '2' and A.VALUE = :value ");
				queryCondition2.setObject("value", twoMap.get("NEXT_TIER_VALUE"));
				queryCondition2.setQueryString(sql2.toString());
				List<Map<String, String>> list2 = dam.exeQuery(queryCondition2);
				map3.put(twoMap.get("NEXT_TIER_VALUE") + "：" + twoMap.get("PARAM_NAME"), "[" + joinListByCom(list2, ", ") + "]");
			}
		}
		csvHeader[1] = "第二層(中文)：" + ObjectUtils.toString(map2);
		csvHeader[2] = "第三層(中文)：" + ObjectUtils.toString(map3);
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		queryCondition.setQueryString("select distinct GLOBAL_LIPPER_CID, GLOBAL_ID from TBPRD_GLOBAL_CLASS");
		List<Map<String, String>> list = dam.exeQuery(queryCondition);
		csvHeader[3] = "Lipper Global分類(中文)：" + "[" + joinListByCom(list, ", ") + "]";
		
		CSVUtil csv = new CSVUtil();
		csv.setHeader(csvHeader);
		String url = csv.generateCSV();
		// download
		notifyClientToDownloadFile(url, fileName);
	}
	
	
	public void initMktTier1(Object body, IPrimitiveMap header) throws JBranchException {
		PRD231InputVO inputVO = (PRD231InputVO) body;
		PRD231OutputVO return_VO = new PRD231OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		//MKT_TIER1
		StringBuffer sql = new StringBuffer();
		sql.append("select a.MKT_TIER1 as DATA, b.PARAM_NAME as LABEL from (select distinct MKT_TIER1 from TBPRD_INVEST_AREA) a left join TBSYSPARAMETER b on a.MKT_TIER1 = b.PARAM_ORDER where 1 = 1 AND b.PARAM_TYPE = 'PRD.MKT_TIER1' ");
		sql.append("ORDER BY a.MKT_TIER1");
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		return_VO.setMktTier1List(list);
		this.sendRtnObject(return_VO);
	}
	
	public void changeKmtTier1(Object body, IPrimitiveMap header) throws JBranchException {
		PRD231InputVO inputVO = (PRD231InputVO) body;
		PRD231OutputVO return_VO = new PRD231OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		//MKT_TIER2
		StringBuffer sql = new StringBuffer();
		sql.append("select a.MKT_TIER2 as DATA, b.PARAM_NAME as LABEL from (select distinct MKT_TIER2 from TBPRD_INVEST_AREA where 1 = 1 ");
		sql.append("AND MKT_TIER1 = :mkt_tier1 ");
		sql.append(") a left join TBSYSPARAMETER b on a.MKT_TIER2 = b.PARAM_ORDER where b.PARAM_TYPE = 'PRD.MKT_TIER2' ORDER BY a.MKT_TIER2");
		queryCondition.setQueryString(sql.toString());
		queryCondition.setObject("mkt_tier1", inputVO.getMkt_tier1());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		return_VO.setMktTier2List(list);
		
		//MKT_TIER3
		sql = new StringBuffer();
		sql.append("select a.MKT_TIER3 as DATA, b.PARAM_NAME as LABEL from (select distinct MKT_TIER3 from TBPRD_INVEST_AREA where 1 = 1 ");
		sql.append("AND MKT_TIER1 = :mkt_tier1 ");
		sql.append(") a left join TBSYSPARAMETER b on a.MKT_TIER3 = b.PARAM_ORDER where 1 = 1 AND b.PARAM_TYPE = 'PRD.MKT_TIER3' ORDER BY a.MKT_TIER3");
		queryCondition.setQueryString(sql.toString());
		queryCondition.setObject("mkt_tier1", inputVO.getMkt_tier1());
		list = dam.exeQuery(queryCondition);
		return_VO.setMktTier3List(list);
		
		this.sendRtnObject(return_VO);
	}
	
	public void changeKmtTier2(Object body, IPrimitiveMap header) throws JBranchException {
		PRD231InputVO inputVO = (PRD231InputVO) body;
		PRD231OutputVO return_VO = new PRD231OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		//MKT_TIER3
		StringBuffer sql = new StringBuffer();
		sql.append("select a.MKT_TIER3 as DATA, b.PARAM_NAME as LABEL from (select distinct MKT_TIER3 from TBPRD_INVEST_AREA where 1 = 1 ");
		sql.append("AND MKT_TIER2 = :mkt_tier2 ");
		sql.append(") a left join TBSYSPARAMETER b on a.MKT_TIER3 = b.PARAM_ORDER where 1 = 1 AND b.PARAM_TYPE = 'PRD.MKT_TIER3' ORDER BY a.MKT_TIER3");
		queryCondition.setQueryString(sql.toString());
		queryCondition.setObject("mkt_tier2", inputVO.getMkt_tier2());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		return_VO.setMktTier3List(list);
		
		this.sendRtnObject(return_VO);
	}
	
	private String checkIsNull(Map map, String key) {
		if(StringUtils.isNotBlank(ObjectUtils.toString(map.get(key)))) {
			return String.valueOf(map.get(key));
		} else {
			return "";
		}
	}
	
	
	public void download(Object body, IPrimitiveMap header) throws JBranchException {
		WorkStation ws = DataManager.getWorkStation(uuid);
		PRD231InputVO inputVO = (PRD231InputVO) body;
		PRD231OutputVO return_VO = new PRD231OutputVO();
		dam = this.getDataAccessManager();
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT THIRD.GLOBAL_ID, THIRD.MKT_TIER1, THIRD.MKT_TIER2, T.PARAM_NAME AS MKT_TIER3, THIRD.GLOBAL_LIPPER_EID, THIRD.GLOBAL_LIPPER_CID ");
		sql.append("FROM ( ");
		sql.append("  SELECT SECOND.GLOBAL_ID, SECOND.MKT_TIER1, T.PARAM_NAME AS MKT_TIER2, SECOND.MKT_TIER3, SECOND.GLOBAL_LIPPER_EID, SECOND.GLOBAL_LIPPER_CID ");
		sql.append("  FROM ( ");
		sql.append("    SELECT FIRST.GLOBAL_ID, T.PARAM_NAME AS MKT_TIER1, FIRST.MKT_TIER2, FIRST.MKT_TIER3, FIRST.GLOBAL_LIPPER_EID, FIRST.GLOBAL_LIPPER_CID ");
		sql.append("    FROM ( ");
		sql.append("      select a.GLOBAL_ID,a.MKT_TIER1,a.MKT_TIER2,a.MKT_TIER3,b.GLOBAL_LIPPER_EID,b.GLOBAL_LIPPER_CID from ");
		sql.append("      TBPRD_INVEST_AREA a left join TBPRD_GLOBAL_CLASS b on a.GLOBAL_ID = b.GLOBAL_ID WHERE 1 = 1 ");
		if (StringUtils.isNotBlank(inputVO.getMkt_tier1())) {
			sql.append("AND a.MKT_TIER1 = :mkt_tier1 ");
			queryCondition.setObject("mkt_tier1", inputVO.getMkt_tier1());
		}
		if (StringUtils.isNotBlank(inputVO.getMkt_tier2())) {
			sql.append("AND a.MKT_TIER2 = :mkt_tier2 ");
			queryCondition.setObject("mkt_tier2", inputVO.getMkt_tier2());
		}
		if (StringUtils.isNotBlank(inputVO.getMkt_tier3())) {
			sql.append("AND a.MKT_TIER3 = :mkt_tier3 ");
			queryCondition.setObject("mkt_tier3", inputVO.getMkt_tier3());
		}
		sql.append("ORDER BY a.MKT_TIER1,a.MKT_TIER2,a.MKT_TIER3");
		sql.append("    ) FIRST ");
		sql.append("    LEFT JOIN TBSYSPARAMETER T ");
		sql.append("      ON FIRST.MKT_TIER1 = T.PARAM_CODE ");
		sql.append("      AND T.PARAM_TYPE = 'PRD.MKT_TIER1' ");
		sql.append("  ) SECOND ");
		sql.append("  LEFT JOIN TBSYSPARAMETER T ");
		sql.append("    ON SECOND.MKT_TIER2 = T.PARAM_CODE ");
		sql.append("    AND T.PARAM_TYPE = 'PRD.MKT_TIER2' ");
		sql.append(") THIRD ");
		sql.append("LEFT JOIN TBSYSPARAMETER T ");
		sql.append("  ON THIRD.MKT_TIER3 = T.PARAM_CODE ");
		sql.append("  AND T.PARAM_TYPE = 'PRD.MKT_TIER3' ");
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		if(list.size() > 0) {
			// gen csv
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			String fileName = "投資區域對照表_"+ sdf.format(new Date()) + "_" + ws.getUser().getUserID() + ".csv";
			List listCSV = new ArrayList();
			for (Map<String, Object> map : list) {
				// 5 column
				String[] records = new String[5];
				int i = 0;
				records[i] = checkIsNull(map, "MKT_TIER1");
				records[++i] = checkIsNull(map, "MKT_TIER2");
				records[++i] = checkIsNull(map, "MKT_TIER3");
				records[++i] = checkIsNull(map, "GLOBAL_LIPPER_CID");
				records[++i] = checkIsNull(map, "GLOBAL_LIPPER_EID");
				listCSV.add(records);
			}
			// header
			String[] csvHeader = new String[5];
			int j = 0;
			csvHeader[j] = "第一層";
			csvHeader[++j] = "第二層(23類)";
			csvHeader[++j] = "第三層(33類)";
			csvHeader[++j] = "Lipper Global分類	";
			csvHeader[++j] = "Lipper Global分類英文名稱";
						
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

	
	
	
}