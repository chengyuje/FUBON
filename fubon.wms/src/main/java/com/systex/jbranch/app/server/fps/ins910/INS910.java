package com.systex.jbranch.app.server.fps.ins910;

import java.io.File;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.sql.Blob;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.common.fps.table.TBINS_PARA_HEADERVO;
import com.systex.jbranch.app.common.fps.table.TBINS_REPORTVO;
import com.systex.jbranch.app.common.fps.table.TBPRD_INS_SUGGESTPK;
import com.systex.jbranch.app.common.fps.table.TBPRD_INS_SUGGESTVO;
import com.systex.jbranch.comutil.collection.GenericMap;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.ObjectUtil;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;

@Component("ins910")
@Scope("request")
public class INS910 extends FubonWmsBizLogic {
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(INS910.class);
	
	public void initial(Object body, IPrimitiveMap header) throws JBranchException {
		INS910InputVO inputVO = (INS910InputVO) body;
		INS910OutputVO return_VO = new INS910OutputVO();
		dam = this.getDataAccessManager();
		
		// TBINS_PARA_HEADER
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		queryCondition.setQueryString("SELECT PARA_NO, CAL_DESC, STATUS FROM (SELECT PARA_NO, CAL_DESC, STATUS FROM TBINS_PARA_HEADER WHERE STATUS IN ('A', 'P') and PARA_TYPE = :ins_type ORDER BY SUBMIT_DATE DESC) WHERE ROWNUM = 1");
		queryCondition.setObject("ins_type", inputVO.getIns_type());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		if(list.size() == 0)
			throw new APException("無設定初始資料，請聯絡IT人員");
		BigDecimal PARA_NO = new BigDecimal(list.get(0).get("PARA_NO").toString());
		return_VO.setStatus(list.get(0).get("STATUS").toString());
		return_VO.setCal_desc(ObjectUtils.toString(list.get(0).get("CAL_DESC")));
		
		// TBPRD_INS_SUGGEST
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		
		sql.append(" SELECT * FROM VWPRD_INS_SUGGEST SUGGEST ");
		sql.append(" WHERE 1 = 1  ");
		sql.append(" AND SUGGEST.PARA_NO = :para_no ");
		sql.append(" ORDER BY SUGGEST.PRD_ID, SUGGEST.INSPRD_ANNUAL "); // 排序
		
		queryCondition.setQueryString(sql.toString());
		queryCondition.setObject("para_no", PARA_NO);
		List<Map<String, Object>> list2 = dam.exeQuery(queryCondition);
		return_VO.setSuggestList(list2);
		
		// TBINS_REPORT
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		queryCondition.setQueryString("SELECT KEYNO, FILE_NAME FROM TBINS_REPORT WHERE PLAN_ID = TO_CHAR(:para_no)");
		queryCondition.setObject("para_no", PARA_NO);
		List<Map<String, Object>> list3 = dam.exeQuery(queryCondition);
		return_VO.setReportList(list3);
		
		// 2018/1/19 主管覆核要更新舊的非新的
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sql = new StringBuffer();
		sql.append("SELECT COUNT(ROLEID) AS COUNTS FROM TBSYSSECUROLPRIASS ");
		sql.append("WHERE PRIVILEGEID IN (SELECT PRIVILEGEID FROM TBSYSSECUPRIFUNMAP WHERE ITEMID = 'INS910' AND FUNCTIONID = 'confirm') AND ROLEID = :roleID ");
		queryCondition.setObject("roleID", getUserVariable(FubonSystemVariableConsts.LOGINROLE));
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> privilegeCount = dam.exeQuery(queryCondition);
		if(((BigDecimal) privilegeCount.get(0).get("COUNTS")).compareTo(new BigDecimal(0)) > 0)
			return_VO.setPara_no(PARA_NO);
		else if("P".equals(list.get(0).get("STATUS")))
			return_VO.setPara_no(PARA_NO);
		else {
			BigDecimal NEW_PARA_NO = new BigDecimal(getSN("HEADER"));
			return_VO.setPara_no(NEW_PARA_NO);
		}
		
		this.sendRtnObject(return_VO);
	}
	
	public void goReview(Object body, IPrimitiveMap header) throws Exception {
		INS910InputVO inputVO = (INS910InputVO) body;
		dam = this.getDataAccessManager();
		
		// 如果找的到, 代表上面查詢是覆核 return_VO.setPara_no(PARA_NO);
		TBINS_PARA_HEADERVO head_vo = (TBINS_PARA_HEADERVO) dam.findByPKey(TBINS_PARA_HEADERVO.TABLE_UID, inputVO.getPara_no());
		boolean canFindHeadVO = (head_vo != null);
		if (canFindHeadVO) {
			head_vo.setSUBMIT_DATE(new Timestamp(System.currentTimeMillis()));
			head_vo.setCAL_DESC(inputVO.getCal_desc());
			dam.update(head_vo);
		}
		else {
			head_vo = new TBINS_PARA_HEADERVO();
			head_vo.setPARA_NO(inputVO.getPara_no());
			head_vo.setPARA_TYPE(inputVO.getIns_type());
			head_vo.setSUBMIT_DATE(new Timestamp(System.currentTimeMillis()));
			head_vo.setCAL_DESC(inputVO.getCal_desc());
			head_vo.setSTATUS("P");
			dam.create(head_vo);
		}
		saveAuthlogRecode(head_vo.getPARA_NO(), "3"); 
		
		// TBPRD_INS_SUGGEST
		QueryConditionIF del_con = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		del_con.setQueryString("DELETE FROM TBPRD_INS_SUGGEST WHERE PARA_NO = :para_no");
		del_con.setObject("para_no", inputVO.getPara_no());
		dam.exeUpdate(del_con);
		for(Map<String, Object> map : inputVO.getSuggestList()) {
			TBPRD_INS_SUGGESTPK sug_pk = new TBPRD_INS_SUGGESTPK();
			sug_pk.setKEY_NO(new BigDecimal(map.get("KEY_NO").toString()));
			sug_pk.setPARA_NO(inputVO.getPara_no());
			sug_pk.setSUGGEST_TYPE(map.get("SUGGEST_TYPE").toString());
			TBPRD_INS_SUGGESTVO sug_vo = new TBPRD_INS_SUGGESTVO();
			sug_vo.setcomp_id(sug_pk);
			sug_vo.setINSDATA_KEYNO(ObjectUtils.toString(map.get("INSDATA_KEYNO")));
			sug_vo.setPOLICY_AMT_DISTANCE(new BigDecimal(map.get("POLICY_AMT_DISTANCE").toString()));
			sug_vo.setCVRG_RATIO(new BigDecimal(map.get("CVRG_RATIO").toString()));
			sug_vo.setPOLICY_AMT_MIN(new BigDecimal(map.get("POLICY_AMT_MIN").toString()));
			sug_vo.setPOLICY_AMT_MAX(new BigDecimal(map.get("POLICY_AMT_MAX").toString()));
			
			// 當類型為[退休規劃]，顯示[資產傳承] CheckBox。存入 TBPRD_INS_SUGGEST.ESTATE_PLAN
			if("5".equals(map.get("SUGGEST_TYPE").toString())) {
				String estatePlan = ObjectUtils.toString(map.get("ESTATE_PLAN"));
				sug_vo.setESTATE_PLAN(StringUtils.isNotEmpty(estatePlan) ? estatePlan : "N"); // 資產傳承
			}
			
			// 當類型為[商品組合-保障]，添加滿期金計算類型[EARNED_CAL_WAY]、滿期金年度[EARNED_YEAR]、滿期金比[EARNED_RATIO]
			if("9".equals(map.get("SUGGEST_TYPE").toString())) {
				sug_vo.setEARNED_CAL_WAY(ObjectUtils.toString(map.get("EARNED_CAL_WAY"))); // 添加滿期金計算類型
				sug_vo.setEARNED_YEAR(new GenericMap(map).getBigDecimal("EARNED_YEAR")); // 滿期金年度
				sug_vo.setEARNED_RATIO(new GenericMap(map).getBigDecimal("EARNED_RATIO")); // 滿期金比
			}
			
			dam.create(sug_vo);
		}
		
		// TBINS_REPORT
		// 非覆核修改舊的, 搬移舊上傳的檔案
		if(inputVO.getFile_seq() != null && !canFindHeadVO) {
			QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			queryCondition.setQueryString("SELECT * FROM TBINS_REPORT WHERE KEYNO = :key_no");
			queryCondition.setObject("key_no", inputVO.getFile_seq());
			List<Map<String, Object>> list = dam.exeQuery(queryCondition);
			for(Map<String, Object> map : list) {
				TBINS_REPORTVO rep_vo = new TBINS_REPORTVO();
				rep_vo.setKEYNO(new BigDecimal(getSN("REPORT")));
				rep_vo.setPLAN_ID(inputVO.getPara_no().toString());
				if(map.get("FILE_NAME") != null)
					rep_vo.setFILE_NAME(ObjectUtils.toString(map.get("FILE_NAME")));
				if(map.get("REPORT_FILE") != null)
					rep_vo.setREPORT_FILE((Blob) map.get("REPORT_FILE"));
				dam.create(rep_vo);
			}
		} else {
			if(StringUtils.isNotBlank(inputVO.getFileName())) {
				del_con = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				del_con.setQueryString("DELETE FROM TBINS_REPORT WHERE PLAN_ID = TO_CHAR(:para_no)");
				del_con.setObject("para_no", inputVO.getPara_no());
				dam.exeUpdate(del_con);
				
				String tempPath = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
				byte[] data = Files.readAllBytes(new File(tempPath, inputVO.getFileName()).toPath());
				BigDecimal key_no = new BigDecimal(getSN("REPORT"));
				TBINS_REPORTVO rep_vo = new TBINS_REPORTVO();
				rep_vo.setKEYNO(key_no);
				rep_vo.setPLAN_ID(inputVO.getPara_no().toString());
				rep_vo.setFILE_NAME(inputVO.getFileRealName());
				rep_vo.setREPORT_FILE(ObjectUtil.byteArrToBlob(data));
				dam.create(rep_vo);
			}
		}
		
		this.sendRtnObject(null);
	}
	
	public void review(Object body, IPrimitiveMap header) throws JBranchException {
		INS910InputVO inputVO = (INS910InputVO) body;
		dam = this.getDataAccessManager();
		
		TBINS_PARA_HEADERVO vo = (TBINS_PARA_HEADERVO) dam.findByPKey(TBINS_PARA_HEADERVO.TABLE_UID, inputVO.getPara_no());
		if (vo != null) {
			vo.setSTATUS(inputVO.getStatus());
			if("A".equals(inputVO.getStatus())) {
				vo.setEFFECT_DATE(new Timestamp(System.currentTimeMillis()));
				
				// old code 更新前一筆核准資料的"有效截止日"
				QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				queryCondition.setQueryString("SELECT PARA_NO, CAL_DESC, STATUS FROM (SELECT PARA_NO, CAL_DESC, STATUS FROM TBINS_PARA_HEADER WHERE STATUS = 'A' and PARA_NO < :para_no and PARA_TYPE = :ins_type ORDER BY SUBMIT_DATE DESC) WHERE ROWNUM = 1");
				queryCondition.setObject("para_no", inputVO.getPara_no());
				queryCondition.setObject("ins_type", inputVO.getIns_type());
				List<Map<String, Object>> end_para_no_list = dam.exeQuery(queryCondition);
				if(end_para_no_list.size() > 0 && end_para_no_list.get(0).get("PARA_NO") != null) {
					BigDecimal end_para_no = (BigDecimal) end_para_no_list.get(0).get("PARA_NO");
					TBINS_PARA_HEADERVO endHeaderVO = (TBINS_PARA_HEADERVO) dam.findByPKey(TBINS_PARA_HEADERVO.TABLE_UID, end_para_no);
					if(endHeaderVO != null) {
						endHeaderVO.setEXPIRY_DATE(new Timestamp(System.currentTimeMillis()));
						dam.update(endHeaderVO);
					}
				}
				//
			}
			dam.update(vo);
			saveAuthlogRecode(vo.getPARA_NO(), "A".equals(vo.getSTATUS()) ? "1" : "2"); 
		} else
			throw new APException("ehl_01_common_001");
		
		this.sendRtnObject(null);
	}
	
	//------------------------------old code--------------------------------
	public void queryPrd(Object body, IPrimitiveMap<Object> header) throws JBranchException {
		dam = this.getDataAccessManager();
		INS910InputVO inputVO = (INS910InputVO) body;
		INS910OutputVO outputVO = new INS910OutputVO();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT INSPRD_NAME , ");
		sb.append("       LISTAGG(INSPRD_ANNUAL,',') WITHIN GROUP (ORDER BY INSPRD_ANNUAL) AS ANNUAL , ");
		sb.append("       PRD_UNIT, CURR_CD,");
		sb.append("       (SELECT PARAM_NAME FROM TBSYSPARAMETER WHERE PARAM_TYPE = 'INS.UNIT' AND PARAM_CODE = PRD_UNIT) AS UNIT_NAME ");
		sb.append("FROM TBPRD_INS ");
		sb.append("WHERE 1=1 ");
		sb.append("AND PRD_ID = :prdid GROUP BY INSPRD_NAME , PRD_UNIT, CURR_CD ");
		qc.setObject("prdid", inputVO.getPRD_ID());
		qc.setQueryString(sb.toString());
		
		List<Map<String, Object>> list = this.getDataAccessManager().exeQuery(qc);
		outputVO.setResultList(list);
		sendRtnObject(outputVO);
	}
	
	public void queryKEYNO(Object body, IPrimitiveMap<Object> header) throws JBranchException {
		INS910InputVO inputVO = (INS910InputVO) body;
		INS910OutputVO outputVO = new INS910OutputVO();
		dam = this.getDataAccessManager();
		
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT KEY_NO  FROM TBPRD_INS  WHERE 1=1 ");
		sb.append(" AND PRD_ID = :prdid AND INSPRD_ANNUAL = :annual ");
		qc.setObject("prdid", inputVO.getPRD_ID());
		qc.setObject("annual", inputVO.getINSPRD_ANNUAL());
		qc.setQueryString(sb.toString());
		List<Map<String, Object>> list = dam.exeQuery(qc);
		outputVO.setResultList(list);
		
		if(list.size() == 0)
			throw new APException("無保險商品資料，請聯絡IT人員");
		BigDecimal KEY_NO = new BigDecimal(ObjectUtils.toString(list.get(0).get("KEY_NO")));
		qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		qc.setQueryString("SELECT INSDATA_KEYNO FROM TBPRD_INS_COMPARED WHERE KEY_NO = :keyno");
		qc.setObject("keyno", KEY_NO);
		List<Map<String, Object>> list2 = dam.exeQuery(qc);
		if(list2.size() > 0)
			outputVO.setInsdata_key_no(ObjectUtils.toString(list2.get(0).get("INSDATA_KEYNO")));
		else
			throw new APException("此保險商品尚未連結資訊源，無法推薦");
		
		sendRtnObject(outputVO);
	}
	
	public void queryAge(Object body, IPrimitiveMap<Object> header) throws JBranchException{
		dam = this.getDataAccessManager();
		INS910InputVO inputVO = (INS910InputVO) body;
		INS910OutputVO outputVO = new INS910OutputVO();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT MIN_AGE, MAX_AGE FROM TBPRD_INS_AGE WHERE F_KEY_NO = :f_key_no AND INSURED_OBJECT = '1' ");
		
		qc.setObject("f_key_no", inputVO.getKEY_NO());
		qc.setQueryString(sb.toString());
		
		List<Map<String, Object>> list = this.getDataAccessManager().exeQuery(qc);
		outputVO.setResultList(list);
		sendRtnObject(outputVO);
	}
	//------------------------------old code--------------------------------
	
	private String getSN(String name) throws JBranchException {
		String ans = "";
		switch(name) {
			case "HEADER":
				QueryConditionIF qc_header = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				qc_header.setQueryString("SELECT TBPRD_INS_SEQ.nextval AS SEQ FROM DUAL");
				List<Map<String, Object>> head_list = dam.exeQuery(qc_header);
				ans = ObjectUtils.toString(head_list.get(0).get("SEQ"));
				break;
			case "REPORT":
				QueryConditionIF qc_report = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				qc_report.setQueryString("SELECT TBINS_REPORT_SEQ.nextval AS SEQ FROM DUAL");
				List<Map<String, Object>> report_list = dam.exeQuery(qc_report);
				ans = ObjectUtils.toString(report_list.get(0).get("SEQ"));
				break;
		}
		return ans;
	}
	
	/**
	 * 紀錄每次操作 log
	 * 申請人 authType = 3 / 主管覆核[1-同意, 2-退回]
	 * @param paraNo
	 * @param authType
	 * @throws DAOException
	 * @throws JBranchException
	 */
	private void saveAuthlogRecode(BigDecimal paraNo, String authType) throws DAOException, JBranchException {
		String empId = (String) getCommonVariable(SystemVariableConsts.LOGINID);
		dam = this.getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer insertSql = new StringBuffer();
		insertSql.append(" INSERT INTO TBINS_PARA_AUTHLOG(PARA_NO, AUTH_TYPE, AUTH_EMPID, AUTH_DATE, COMMENTS, CREATETIME, CREATOR, MODIFIER, LASTUPDATE) ");
		insertSql.append(" VALUES(:PARA_NO, :AUTH_TYPE, :AUTH_EMPID, :AUTH_DATE, :COMMENTS, :CREATETIME, :CREATOR, :MODIFIER, :LASTUPDATE) ");
		qc.setObject("PARA_NO", paraNo);
		qc.setObject("AUTH_TYPE", authType);
		qc.setObject("AUTH_EMPID", empId);
		qc.setObject("AUTH_DATE", new Date());
		qc.setObject("COMMENTS", null);
		qc.setObject("CREATETIME", new Date());
		qc.setObject("CREATOR", empId);
		qc.setObject("MODIFIER", empId);
		qc.setObject("LASTUPDATE", new Date());
		qc.setQueryString(insertSql.toString());
		dam.exeUpdate(qc);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
}