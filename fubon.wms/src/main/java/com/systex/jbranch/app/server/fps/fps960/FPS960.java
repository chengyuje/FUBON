package com.systex.jbranch.app.server.fps.fps960;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

import com.systex.jbranch.app.common.fps.table.TBFPS_AUTHORITYVO;
import com.systex.jbranch.app.common.fps.table.TBFPS_AUTHORITY_DETAILPK;
import com.systex.jbranch.app.common.fps.table.TBFPS_AUTHORITY_DETAILVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataManager.DataManager;
import com.systex.jbranch.platform.common.dataManager.WorkStation;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.CSVUtil;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;

@Component("fps960")
@Scope("request")
public class FPS960 extends FubonWmsBizLogic {
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(FPS960.class);
	
	public void getRole(Object body, IPrimitiveMap header) throws JBranchException {
		// no use
//		FPS960OutputVO return_VO = new FPS960OutputVO();
//		dam = this.getDataAccessManager();
//		
//		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
//		queryCondition.setQueryString("SELECT PARAM_NAME, listagg(PARAM_CODE,',') WITHIN GROUP (ORDER BY PARAM_CODE) as ROLE_ID FROM TBSYSPARAMETER WHERE PARAM_TYPE = 'FUBONSYS.FPS_BUSINESS_ROLE' GROUP BY PARAM_NAME");
//		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
//		return_VO.setResultList(list);
//		this.sendRtnObject(return_VO);
	}
	
	public void getList(Object body, IPrimitiveMap header) throws JBranchException {
		FPS960InputVO inputVO = (FPS960InputVO) body;
		FPS960OutputVO return_VO = new FPS960OutputVO();
		WorkStation ws = DataManager.getWorkStation(uuid);
		dam = this.getDataAccessManager();
		
		// 判斷主管直接根據有無覆核權限
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT COUNT(ROLEID) AS COUNTS FROM TBSYSSECUROLPRIASS ");
		sql.append("WHERE PRIVILEGEID IN (SELECT PRIVILEGEID FROM TBSYSSECUPRIFUNMAP WHERE ITEMID = 'FPS960' AND FUNCTIONID = 'confirm') AND ROLEID = :roleID ");
		queryCondition.setObject("roleID", getUserVariable(FubonSystemVariableConsts.LOGINROLE));
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> privilegeCount = dam.exeQueryWithoutSort(queryCondition);
		//
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sql = new StringBuffer();
		sql.append("WITH BASE AS ( ");
		sql.append("select PARAM_CODE,PARAM_NAME from TBSYSPARAMETER where PARAM_TYPE='FUBONSYS.HEADMGR_ROLE' ");
		sql.append("union ");
		sql.append("select PARAM_CODE,PARAM_NAME from TBSYSPARAMETER where PARAM_TYPE='FUBONSYS.FAIA_ROLE' ");
		sql.append("), ");
		sql.append("BASE2 AS ( ");
		sql.append("SELECT ROLE_NAME as PARAM_NAME, listagg(ROLE_ID,',') WITHIN GROUP (ORDER BY ROLE_ID) as PARAM_CODE FROM TBORG_ROLE WHERE REVIEW_STATUS = 'Y' GROUP BY ROLE_NAME ");
		sql.append("), ");
		sql.append("BASE3 AS ( ");
		sql.append("SELECT DISTINCT a.PARAM_NO, DECODE(c.PARAM_NAME,NULL,d.PARAM_NAME,c.PARAM_NAME) AS PARAM_NAME FROM TBFPS_AUTHORITY a ");
		sql.append("LEFT JOIN TBFPS_AUTHORITY_DETAIL b on a.PARAM_NO = b.PARAM_NO ");
		sql.append("LEFT JOIN BASE c on c.PARAM_CODE = b.ROLE_ID ");
		sql.append("LEFT JOIN BASE2 d on d.PARAM_CODE like '%' || b.ROLE_ID || '%' ");
		sql.append(") ");
		sql.append("SELECT a.PARAM_NO, a.EFFECT_START_DATE, a.AUTH_TYPE, a.STATUS, a.LASTUPDATE, a.MODIFIER, a.CREATOR, ");
		sql.append("listagg(b.PARAM_NAME,', ') WITHIN GROUP (ORDER BY b.PARAM_NAME) as ROLE_NAME, ");
		sql.append("c.EMP_NAME AS MODNAME, DECODE(a.MODIFIER, NULL, NULL, c.EMP_NAME || '-' || a.MODIFIER) as EDITOR, ");
		sql.append("d.EMP_NAME AS ADDNAME, DECODE(a.CREATOR, NULL, NULL, d.EMP_NAME || '-' || a.CREATOR) as ADDOR ");
		sql.append("FROM TBFPS_AUTHORITY a ");
		sql.append("LEFT JOIN BASE3 b on a.PARAM_NO = b.PARAM_NO ");
		sql.append("LEFT JOIN TBORG_MEMBER c on a.MODIFIER = c.EMP_ID ");
		sql.append("LEFT JOIN TBORG_MEMBER d on a.CREATOR = d.EMP_ID ");
		sql.append("WHERE a.PARAM_NO NOT IN (SELECT PARAM_NO FROM TBFPS_AUTHORITY WHERE STATUS = 'S' AND CREATOR != :creator) ");
		queryCondition.setObject("creator", ws.getUser().getUserID());
		// where
		if (inputVO.getDate() != null) {
			sql.append("AND TO_CHAR(:date, 'yyyyMM') = TO_CHAR(a.EFFECT_START_DATE, 'yyyyMM') ");
			queryCondition.setObject("date", inputVO.getDate());
		}
		// 主管的狀態只會有”審核(覆核中)”、 ”核准(生效)”、”失效”
		if (((BigDecimal) privilegeCount.get(0).get("COUNTS")).compareTo(new BigDecimal(0)) > 0)
			sql.append("AND a.STATUS IN ('P', 'W', 'A', 'F') ");
		//
		sql.append("GROUP BY a.PARAM_NO, a.EFFECT_START_DATE, a.AUTH_TYPE, a.STATUS, a.LASTUPDATE, a.MODIFIER, a.CREATOR, c.EMP_NAME, d.EMP_NAME, DECODE(a.MODIFIER, NULL, NULL, c.EMP_NAME || '-' || a.MODIFIER), DECODE(a.CREATOR, NULL, NULL, d.EMP_NAME || '-' || a.CREATOR) ");
		sql.append("ORDER BY a.EFFECT_START_DATE DESC, a.LASTUPDATE DESC ");
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		return_VO.setResultList(list);
		
		this.sendRtnObject(return_VO);
	}
	
	public void download(Object body, IPrimitiveMap header) throws Exception {
		FPS960InputVO inputVO = (FPS960InputVO) body;
		dam = this.getDataAccessManager();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT a.EMP_ID, b.ROLE_ID, DECODE(b.JOB_TITLE_NAME,NULL,b.ROLE_NAME,b.JOB_TITLE_NAME) AS ROLE_NAME FROM TBFPS_AUTHORITY_DETAIL a ");
		sql.append("LEFT JOIN TBORG_ROLE b on a.ROLE_ID = b.ROLE_ID ");
		sql.append("WHERE a.PARAM_NO = :param_no ");
		queryCondition.setQueryString(sql.toString());
		queryCondition.setObject("param_no", inputVO.getParam_no());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		if(list.size() > 0) {
			// gen csv
			String fileName = "人員檔案_"+ sdf.format(new Date()) + ".csv";
			List listCSV = new ArrayList();
			for (Map<String, Object> map : list) {
				// 3 column
				String[] records = new String[3];
				int i = 0;
				records[i] = checkIsNull(map, "EMP_ID");
				records[++i] = checkIsNull(map, "ROLE_ID");
				records[++i] = checkIsNull(map, "ROLE_NAME");
				listCSV.add(records);
			}
			// header
			String[] csvHeader = new String[3];
			int j = 0;
			csvHeader[j] = "員工編號";
			csvHeader[++j] = "角色代碼";
			csvHeader[++j] = "角色名稱";
			
			CSVUtil csv = new CSVUtil();
			csv.setHeader(csvHeader);  
			csv.addRecordList(listCSV);
			String url = csv.generateCSV();
			// download
			notifyClientToDownloadFile(url, fileName);
		} else
			this.sendRtnObject(null);
	}
	private String checkIsNull(Map map, String key) {
		if (map.get(key) == null) {
			return "";
		} else if(StringUtils.isNotBlank(String.valueOf(map.get(key)))){
			return String.valueOf(map.get(key));
		}else{
			return "";
		}
	}
	
	public void downloadSimple(Object body, IPrimitiveMap header) throws Exception {
		notifyClientToDownloadFile("doc//FPS//FPS960_EXAMPLE.csv", "上傳範例.csv");
	}
	
	public void goAdd(Object body, IPrimitiveMap header) throws JBranchException {
		FPS960InputVO inputVO = (FPS960InputVO) body;
		FPS960OutputVO return_VO = new FPS960OutputVO();
		dam = this.getDataAccessManager();
		
		// seq
		QueryConditionIF qcSeq = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		qcSeq.setQueryString("SELECT SQ_TBFPS_AUTHORITY.nextval AS SEQ FROM DUAL");
		List<Map<String, Object>> SEQLIST = dam.exeQuery(qcSeq);
		BigDecimal seqNo = (BigDecimal) SEQLIST.get(0).get("SEQ");
		
		TBFPS_AUTHORITYVO vo = new TBFPS_AUTHORITYVO();
		vo.setPARAM_NO(seqNo.toString());
		vo.setEFFECT_START_DATE(new Timestamp(inputVO.getDate().getTime()));
		vo.setAUTH_TYPE(inputVO.getSetType());
		vo.setSTATUS("S");
		dam.create(vo);
		
		// 自訂人員
		if("2".equals(inputVO.getSetType())) {
			// 角色
			for(Map<String, Object> map : inputVO.getChkRole()) {
				String[] roleArr = StringUtils.split(ObjectUtils.toString(map.get("ROLE_ID")), ",");
				for(String id : roleArr) {
					// get all emp_id by role_id
					QueryConditionIF qcRole = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
					StringBuffer qcSql = new StringBuffer();
					qcSql.append("SELECT DISTINCT EMP_ID FROM ( ");
					qcSql.append("SELECT EMP_ID FROM vworg_branch_emp_detail_info WHERE ROLE_ID = :role_id ");
					qcSql.append("UNION ");
					qcSql.append("SELECT EMP_ID FROM vworg_emp_pluralism_info WHERE ROLE_ID = :role_id ");
					qcSql.append(") ");
					qcRole.setObject("role_id", id);
					qcRole.setQueryString(qcSql.toString());
					List<Map<String, Object>> emp_list = dam.exeQuery(qcRole);
					for(Map<String, Object> map2 : emp_list) {
						// seq
						QueryConditionIF qcSeq2 = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
						qcSeq2.setQueryString("SELECT SQ_TBFPS_AUTHORITY_DETAIL.nextval AS SEQ FROM DUAL");
						List<Map<String, Object>> SEQLIST2 = dam.exeQuery(qcSeq2);
						BigDecimal seqNo2 = (BigDecimal) SEQLIST2.get(0).get("SEQ");
						
						TBFPS_AUTHORITY_DETAILVO dvo = new TBFPS_AUTHORITY_DETAILVO();
						TBFPS_AUTHORITY_DETAILPK dpk = new TBFPS_AUTHORITY_DETAILPK();
						dpk.setPARAM_NO(seqNo.toString());
						dpk.setSEQ_NO(seqNo2.toString());
						dvo.setcomp_id(dpk);
						dvo.setROLE_ID(id);
						dvo.setEMP_ID(ObjectUtils.toString(map2.get("EMP_ID")));
						dam.create(dvo);
					}
				}
			}
		}
		// 依上傳名單
		else {
			Integer success = 0;
			List<String> error = new ArrayList<String>();
			List<String> error2 = new ArrayList<String>();
			Set<String> idList = new HashSet<String>();
			String tempPath = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
			List<String[]> dataCsv = CSVUtil.getBig5CSVFile(tempPath, inputVO.getFileName());
			if(!dataCsv.isEmpty()) {
				for(int i = 0;i < dataCsv.size();i++) {
					String[] str = dataCsv.get(i);
					if(i == 0) {
						try {
							if(!"員工編號".equals(str[0].trim()))
								throw new Exception(str[0]);
						} catch(Exception ex) {
							throw new APException(ex.getMessage() + ":上傳格式錯誤，請下載範例檔案");
						}
						continue;
					}
					// 請PG判斷，第一欄若為空，就跳過去不判斷。
					if(StringUtils.isBlank(str[0]))
						continue;
					// 使用者很愛重覆上傳
					if(idList.contains(str[0].trim())) {
						error2.add(str[0]);
						continue;
					}
					idList.add(str[0].trim());
					// TBORG_MEMBER_ROLE
					QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
					StringBuffer sql = new StringBuffer();
					sql.append("SELECT DISTINCT ROLE_ID FROM ( ");
					sql.append("SELECT ROLE_ID FROM vworg_branch_emp_detail_info WHERE EMP_ID = :emp_id ");
					sql.append("UNION ");
					sql.append("SELECT ROLE_ID FROM vworg_emp_pluralism_info WHERE EMP_ID = :emp_id ");
					sql.append(") ");
					queryCondition.setObject("emp_id", str[0].trim());
					queryCondition.setQueryString(sql.toString());
					List<Map<String, Object>> list = dam.exeQuery(queryCondition);
					if (list.size() == 0) {
						error.add(str[0]);
						continue;
					}
					for(Map<String, Object> map : list) {
						// seq
						QueryConditionIF qcSeq2 = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
						qcSeq2.setQueryString("SELECT SQ_TBFPS_AUTHORITY_DETAIL.nextval AS SEQ FROM DUAL");
						List<Map<String, Object>> SEQLIST2 = dam.exeQuery(qcSeq2);
						BigDecimal seqNo2 = (BigDecimal) SEQLIST2.get(0).get("SEQ");
						
						TBFPS_AUTHORITY_DETAILVO dvo = new TBFPS_AUTHORITY_DETAILVO();
						TBFPS_AUTHORITY_DETAILPK dpk = new TBFPS_AUTHORITY_DETAILPK();
						dpk.setPARAM_NO(seqNo.toString());
						dpk.setSEQ_NO(seqNo2.toString());
						dvo.setcomp_id(dpk);
						dvo.setROLE_ID(ObjectUtils.toString(map.get("ROLE_ID")));
						dvo.setEMP_ID(str[0].trim());
						dam.create(dvo);
						success += 1;
					}
				}
			}
			
			if(success == 0)
				throw new APException("ehl_01_cus130_003");
			return_VO.setErrorList(error);
			return_VO.setErrorList2(error2);
		}
		
		this.sendRtnObject(return_VO);
	}
	
	public void goConfirm(Object body, IPrimitiveMap header) throws JBranchException {
		FPS960InputVO inputVO = (FPS960InputVO) body;
		dam = this.getDataAccessManager();
		
		TBFPS_AUTHORITYVO vo = new TBFPS_AUTHORITYVO();
		vo = (TBFPS_AUTHORITYVO) dam.findByPKey(TBFPS_AUTHORITYVO.TABLE_UID, inputVO.getParam_no());
		if (vo != null) {
			vo.setSTATUS("P");
			dam.update(vo);
		} else
			throw new APException("ehl_01_common_001");
		
		this.sendRtnObject(null);
	}
	
	public void goDelete(Object body, IPrimitiveMap header) throws JBranchException {
		FPS960InputVO inputVO = (FPS960InputVO) body;
		dam = this.getDataAccessManager();
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		queryCondition.setQueryString("DELETE FROM TBFPS_AUTHORITY WHERE PARAM_NO = :param_no");
		queryCondition.setObject("param_no", inputVO.getParam_no());
		dam.exeUpdate(queryCondition);
		
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		queryCondition.setQueryString("DELETE FROM TBFPS_AUTHORITY_DETAIL WHERE PARAM_NO = :param_no");
		queryCondition.setObject("param_no", inputVO.getParam_no());
		dam.exeUpdate(queryCondition);
		
		this.sendRtnObject(null);
	}
	
	public void goReview(Object body, IPrimitiveMap header) throws JBranchException {
		FPS960InputVO inputVO = (FPS960InputVO) body;
		dam = this.getDataAccessManager();
		
		for(Map<String, Object> map : inputVO.getReview_list()) {
			TBFPS_AUTHORITYVO vo = new TBFPS_AUTHORITYVO();
			vo = (TBFPS_AUTHORITYVO) dam.findByPKey(TBFPS_AUTHORITYVO.TABLE_UID, (String)map.get("PARAM_NO"));
			if (vo != null) {
				vo.setSTATUS(inputVO.getStatus());
				if("W".equals(inputVO.getStatus()))
					vo.setEFFECT_START_DATE(new Timestamp(System.currentTimeMillis()));
				dam.update(vo);
			} else
				throw new APException("ehl_01_common_001");
		}
		
		this.sendRtnObject(null);
	}
	
	public void checkEMP(Object body, IPrimitiveMap header) throws JBranchException {
		FPS960InputVO inputVO = (FPS960InputVO) body;
		FPS960OutputVO return_VO = new FPS960OutputVO();
		dam = this.getDataAccessManager();
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT EMP_ID FROM TBFPS_AUTHORITY_DETAIL WHERE PARAM_NO = (SELECT * FROM ( ");
		sql.append("SELECT PARAM_NO FROM TBFPS_AUTHORITY WHERE STATUS = 'A' ORDER BY EFFECT_START_DATE DESC ");
		sql.append(") WHERE ROWNUM = 1) ");
		sql.append("AND EMP_ID = :emp_id AND ROLE_ID = :role_id");
		queryCondition.setObject("emp_id", getUserVariable(FubonSystemVariableConsts.LOGINID));
		queryCondition.setObject("role_id", getUserVariable(FubonSystemVariableConsts.LOGINROLE));
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		if(list.size() > 0)
			return_VO.setHaveAuth(true);
		else
			return_VO.setHaveAuth(false);
		
		this.sendRtnObject(return_VO);
	}
	
	
	
}