package com.systex.jbranch.app.server.fps.kyc510;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.common.fps.table.TBKYC_QUESTIONNAIRE_FLW_DETAILVO;
import com.systex.jbranch.app.common.fps.table.TBSYS_QUESTIONNAIREPK;
import com.systex.jbranch.app.common.fps.table.TBSYS_QUESTIONNAIREVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.query.ResultIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.StringUtil;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * KYC510
 * 
 * @author Sam
 * @date 2018/05/21
 * @spec null
 */
@Component("kyc510")
@Scope("request")
public class KYC510 extends FubonWmsBizLogic {
	Logger logger = LoggerFactory.getLogger(this.getClass());

	DataAccessManager dam_obj = null;

	public void queryData(Object body, IPrimitiveMap<Object> header)
			throws JBranchException {
		KYC510InputVO inputVO = (KYC510InputVO) body;
		KYC510OutputVO outputVO = new KYC510OutputVO();
		try {
			dam_obj = new DataAccessManager();
			QueryConditionIF qc = dam_obj
					.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuilder sb = new StringBuilder();
			sb.append(" Select distinct b.EXAM_NAME,b.EXAM_VERSION,b. STATUS,to_char(b.ACTIVE_DATE,'YYYY/MM/DD') as ACTIVE_DATE,	");
			sb.append("				to_char(b.EXPIRY_DATE,'YYYY/MM/DD') as EXPIRY_DATE, b.MODIFIER,to_char(b.LASTUPDATE,'YYYY/MM/DD') as LASTUPDATE,	");
			sb.append("				to_char(flw.CREATETIME,'YYYY/MM/DD')	 as APPROVE_TIME	");
			sb.append(" from TBSYS_QUESTIONNAIRE b	");
			sb.append("	LEFT JOIN TBKYC_QUESTIONNAIRE_FLW_DETAIL flw	");
			sb.append("		ON b.EXAM_VERSION = flw.EXAM_VERSION	");
			sb.append(" where quest_type in ('04','05') ");// QUEST_TYPE
															// 1='金融(含債券)專案知識評估'
															// ;2='結構型專案知識評估'
			if (StringUtils.isNotBlank(inputVO.getEXAM_NAME())) {
				sb.append(" and b.exam_name like :exam_name ");
				qc.setObject("exam_name", "%" + inputVO.getEXAM_NAME() + "%");
			}
			if (StringUtils.isNotBlank(inputVO.getEXAM_VERSION())) {
				sb.append(" And b.exam_version = :exam_version ");
				qc.setObject("exam_version", inputVO.getEXAM_VERSION());
			}
			if (inputVO.getLASTUPDATE() != null) {
				sb.append(" And trunc(b.lastupdate) = :lastupdate ");
				qc.setObject("lastupdate", inputVO.getLASTUPDATE());
			}
			if (StringUtils.isNotBlank(inputVO.getPRO_TYPE())) {
				sb.append(" And b.QUEST_TYPE = :quest_type ");
				qc.setObject("quest_type", inputVO.getPRO_TYPE());
			}
			if (StringUtils.isNotBlank(inputVO.getSTATUS())) {
				sb.append(" And b.status = :status ");
				qc.setObject("status", inputVO.getSTATUS());
			}
			sb.append(" order by LASTUPDATE DESC ");
			qc.setQueryString(sb.toString());
			ResultIF list = dam_obj.executePaging(qc,
					inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());
			int totalPage_i = list.getTotalPage();
			int totalRecord_i = list.getTotalRecord();
			outputVO.setQuestionList(list);
			outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());
			outputVO.setTotalPage(totalPage_i);
			outputVO.setTotalRecord(totalRecord_i);
			// outputVO.setQuestionList(dam_obj.exeQuery(qc));
			sendRtnObject(outputVO);
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",
					StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}

	// 這裡的刪除是指將狀態改成刪除事實上保留資料
	public void deleteData(Object body, IPrimitiveMap<Object> header)
			throws JBranchException {
		KYC510InputVO inputVO = (KYC510InputVO) body;
		String loginID = (String) getCommonVariable(SystemVariableConsts.LOGINID);
		try {
			dam_obj = new DataAccessManager();
			QueryConditionIF qc = dam_obj
					.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuilder sb = new StringBuilder();
			sb.append(" update TBSYS_QUESTIONNAIRE set STATUS = '05',MODIFIER = :modifier,LASTUPDATE = sysdate where EXAM_VERSION = :exam_version ");
			qc.setObject("exam_version", inputVO.getDelete_Data());
			qc.setObject("modifier", loginID);
			qc.setQueryString(sb.toString());
			dam_obj.exeUpdate(qc);
			sendRtnObject(null);
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",
					StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}

	public void addQuestionnaireInitial(Object body,
			IPrimitiveMap<Object> header) throws JBranchException {
		KYC510InputVO inputVO = (KYC510InputVO) body;
		KYC510OutputVO outputVO = new KYC510OutputVO();
		List<Map<String, Object>> question = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> Remark = new ArrayList<Map<String, Object>>();
		// List<Map<String, Object>> risklist = new
		// ArrayList<Map<String,Object>>();
		try {
			// 取得問卷題目
			dam_obj = new DataAccessManager();
			QueryConditionIF qc = dam_obj
					.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuilder sb = new StringBuilder();
			sb.append(" select a.exam_version,a.quest_type,a.qst_no, ");
			sb.append(" 	b.question_type, ");
			sb.append(" 	b.question_desc, b.ans_other_flag, b.ans_memo_flag, a.question_version, a.ACTIVE_DATE, a.ESSENTIAL_FLAG, a.RL_VERSION, A.EXPIRY_DATE,	");
			sb.append("		b.corr_ans	");
			sb.append(" from TBSYS_QUESTIONNAIRE a, TBSYS_QST_QUESTION b");
			sb.append("		where a.question_version = b.question_version AND a.exam_version = :exam_version ");
			sb.append("		order by qst_no");
			qc.setObject("exam_version", inputVO.getEXAM_VERSION());
			qc.setQueryString(sb.toString());
			question = dam_obj.exeQuery(qc);
			
			dam_obj = new DataAccessManager();
			qc = dam_obj
					.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sb = new StringBuilder();
			sb.append(" select SIGNOFF_ID,SIGNOFF_NAME,to_char(CREATETIME,'YYYY/MM/DD') as CREATETIME,REMARK ");
			sb.append(" from TBKYC_QUESTIONNAIRE_FLW_DETAIL ");
			sb.append(" where EXAM_VERSION = :remark_exam_version ");
			qc.setObject("remark_exam_version", inputVO.getEXAM_VERSION());
			qc.setQueryString(sb.toString());
			Remark = dam_obj.exeQuery(qc);
			if (Remark.size() > 0) {
				question.get(0).put("Remark", Remark);
			}
			
			// 取得問卷題目答案
			for (Map<String, Object> questionS : question) {
				String question_version = (String) questionS.get("QUESTION_VERSION");
				String exam_version = (String) questionS.get("EXAM_VERSION");
				dam_obj = new DataAccessManager();
				qc = dam_obj
						.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sb = new StringBuilder();
				sb.append(" select a.question_version,a.answer_seq,a.answer_desc ");
				sb.append(" from tbsys_qst_answer a	,TBSYS_QUESTIONNAIRE b ");
				sb.append(" where a.question_version = :question_version ");
				sb.append(" and b.exam_version = :exam_version ");
				sb.append(" and b.question_version = a.question_version ");
				sb.append(" order by a.answer_seq ");
				qc.setObject("question_version", question_version);
				qc.setObject("exam_version", exam_version);
				qc.setQueryString(sb.toString());
				questionS.put("answer", dam_obj.exeQuery(qc));
			}
			outputVO.setQuestionList(question);
			sendRtnObject(outputVO);

		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s", StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}

	public void editSaveData(Object body, IPrimitiveMap<Object> header)
			throws JBranchException {
		KYC510InputVO inputVO = (KYC510InputVO) body;
		String loginID = (String) getCommonVariable(SystemVariableConsts.LOGINID);
		String errorMsg = "";
		dam_obj = new DataAccessManager();
		QueryConditionIF qc = dam_obj
				.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuilder sb = new StringBuilder();
		try {
			//刪除題目
			if(inputVO.getDEL_QUESTION().size() > 0){
				for (Map<String, Object> delete_data : inputVO.getDEL_QUESTION()){
					dam_obj = new DataAccessManager();
					String questionVersion = (String) delete_data.get("QUESTION_VERSION");
					String examVersion = inputVO.getEXAM_VERSION();					
					
					TBSYS_QUESTIONNAIREPK question_pk = new TBSYS_QUESTIONNAIREPK();
					question_pk.setEXAM_VERSION(examVersion);
					question_pk.setQUESTION_VERSION(questionVersion.toString());
					
					TBSYS_QUESTIONNAIREVO question_vo = new TBSYS_QUESTIONNAIREVO();
					question_vo = (TBSYS_QUESTIONNAIREVO)
							dam_obj.findByPKey(TBSYS_QUESTIONNAIREVO.TABLE_UID, question_pk);
					
					if(question_vo != null){
						dam_obj.delete(question_vo);
					}else{
						errorMsg = "ehl_01_common_009";
						throw new APException(errorMsg);
					}					
				}
			}
			
			if (inputVO.getCopy()) {
				for (Map<String, Object> question_data : inputVO.getUpdate_preview_data()) {
					dam_obj = new DataAccessManager();
					String examVersion = inputVO.getEXAM_VERSION();
					String questionVersion = question_data.get("QUESTION_VERSION").toString();
					
					TBSYS_QUESTIONNAIREPK question_pk = new TBSYS_QUESTIONNAIREPK();
					question_pk.setEXAM_VERSION(examVersion);
					question_pk.setQUESTION_VERSION(questionVersion);
					
					TBSYS_QUESTIONNAIREVO question_vo = new TBSYS_QUESTIONNAIREVO();
					
					question_vo.setcomp_id(question_pk);
					question_vo.setEXAM_NAME(inputVO.getEXAM_NAME());
					question_vo.setQUEST_TYPE(inputVO.getPRO_TYPE());
					question_vo.setQST_NO(new BigDecimal(question_data.get("QST_NO").toString()));
					question_vo.setESSENTIAL_FLAG(question_data.get("ESSENTIAL_FLAG").toString());
					question_vo.setRL_VERSION(inputVO.getRL_VERSION());
					question_vo.setSTATUS("01");
					question_vo.setACTIVE_DATE(new Timestamp(inputVO.getACTIVE_DATE().getTime()));
					question_vo.setEXPIRY_DATE(new Timestamp(inputVO.getEXPIRY_DATE().getTime()));
					dam_obj.create(question_vo);
				}
			} else {
				for (Map<String, Object> question_data : inputVO.getUpdate_preview_data()) {
					String questionVersion = (String) 
							question_data.get("QUESTION_VERSION");
					String examVersion = inputVO.getEXAM_VERSION();
					
					dam_obj = new DataAccessManager();
					
					TBSYS_QUESTIONNAIREPK question_pk = new TBSYS_QUESTIONNAIREPK();
					question_pk.setEXAM_VERSION(examVersion);
					question_pk.setQUESTION_VERSION(questionVersion.toString());
					
					TBSYS_QUESTIONNAIREVO question_vo = new TBSYS_QUESTIONNAIREVO();
					question_vo = (TBSYS_QUESTIONNAIREVO)
							dam_obj.findByPKey(TBSYS_QUESTIONNAIREVO.TABLE_UID, question_pk);
					
					if (question_vo == null) {
						question_pk = new TBSYS_QUESTIONNAIREPK();
						question_pk.setEXAM_VERSION(examVersion);
						question_pk.setQUESTION_VERSION(questionVersion.toString());
						question_vo = new TBSYS_QUESTIONNAIREVO();
						question_vo.setcomp_id(question_pk);
						question_vo.setEXAM_NAME(inputVO.getEXAM_NAME());
						question_vo.setQUEST_TYPE(inputVO.getPRO_TYPE());
						question_vo.setQST_NO(new BigDecimal(question_data.get("QST_NO").toString()));
						question_vo.setESSENTIAL_FLAG(question_data.get("ESSENTIAL_FLAG").toString());
						question_vo.setRL_VERSION(inputVO.getRL_VERSION());
						question_vo.setSTATUS("01");
						question_vo.setACTIVE_DATE(new Timestamp(inputVO.getACTIVE_DATE().getTime()));
						question_vo.setEXPIRY_DATE(new Timestamp(inputVO.getEXPIRY_DATE().getTime()));
						dam_obj.create(question_vo);
					} else {
						if (question_data.get("select") != null) {
							errorMsg = "選取的題目重複";
							throw new APException(errorMsg);
						} else {
							question_vo.setEXAM_NAME(inputVO.getEXAM_NAME());
							question_vo.setQUEST_TYPE(inputVO.getPRO_TYPE());
							question_vo.setQST_NO(new BigDecimal(question_data.get("QST_NO").toString()));
							question_vo.setESSENTIAL_FLAG(question_data.get("ESSENTIAL_FLAG").toString());
							question_vo.setRL_VERSION(inputVO.getRL_VERSION());
							question_vo.setACTIVE_DATE(new Timestamp(inputVO.getACTIVE_DATE().getTime()));
							question_vo.setEXPIRY_DATE(new Timestamp(inputVO.getEXPIRY_DATE().getTime()));
							question_vo.setSTATUS("01");
							dam_obj.update(question_vo);
//							TBKYC_QUESTIONNAIRE_FLW_DETAILVO flw = new TBKYC_QUESTIONNAIRE_FLW_DETAILVO();
//							flw = (TBKYC_QUESTIONNAIRE_FLW_DETAILVO) dam_obj.findByFields(question_vo);
//							flw.set
						}
					}
				}
			}
			sendRtnObject(null);
		} catch (Exception e) {
			if (errorMsg.length() > 0) {
				throw new APException(errorMsg);
			} else {
				logger.error(String.format("發生錯誤:%s", StringUtil.getStackTraceAsString(e)));
				throw new APException("系統發生錯誤請洽系統管理員");
			}
		}
	}

	public void deleteQuestion(Object body, IPrimitiveMap<Object> header)
			throws JBranchException {
		KYC510InputVO inputVO = (KYC510InputVO) body;
		dam_obj = new DataAccessManager();
		QueryConditionIF qc = dam_obj
				.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuilder sb = new StringBuilder();
		try {
			sb.append(" Delete tbkyc_questionnaire_ans_weight where EXAM_VERSION = :exam_version and QUESTION_VERSION = :question_version ");
			qc.setObject("exam_version", inputVO.getInExam_Version());
			qc.setObject("question_version", inputVO.getInQuest_Version());
			qc.setQueryString(sb.toString());
			dam_obj.exeUpdate(qc);
			qc = dam_obj
					.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sb = new StringBuilder();
			sb.append(" Delete TBSYS_QUESTIONNAIRE where EXAM_VERSION = :exam_version and QUESTION_VERSION = :question_version ");
			qc.setObject("exam_version", inputVO.getInExam_Version());
			qc.setObject("question_version", inputVO.getInQuest_Version());
			qc.setQueryString(sb.toString());
			dam_obj.exeUpdate(qc);
			sendRtnObject(null);
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s", StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}

	}

	public void queryQstData(Object body, IPrimitiveMap<Object> header)
			throws JBranchException {
		KYC510InputVO inputVO = (KYC510InputVO) body;
		KYC510OutputVO outputVO = new KYC510OutputVO();
		try {
			dam_obj = new DataAccessManager();
			QueryConditionIF qc = dam_obj
					.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			List<Map<String, Object>> Main_list = new ArrayList<Map<String, Object>>();
			StringBuilder sb = new StringBuilder();
			sb.append(" SELECT QUESTION_VERSION, QUESTION_DESC,QUESTION_TYPE,ANS_OTHER_FLAG,ANS_MEMO_FLAG,STATUS,CREATOR,to_char(CREATETIME,'YYYY/MM/DD') as CREATETIME	");
			sb.append("		FROM TBSYS_QST_QUESTION WHERE MODULE_CATEGORY = 'PRO'");
			if (!"".equals(inputVO.getQUESTION_DESC())) {
				sb.append(" AND QUESTION_DESC LIKE :question");
				qc.setObject("question", "%" + inputVO.getQUESTION_DESC() + "%");
			}

			if (inputVO.getsDate() != null && inputVO.geteDate() == null) {
				sb.append(" AND CREATETIME >= :sDate ");
				qc.setObject("sDate", inputVO.getsDate());
			}
			if (inputVO.getsDate() == null && inputVO.geteDate() != null) {
				sb.append(" AND CREATETIME <= :eDate ");
				qc.setObject("eDate", inputVO.geteDate());
			}
			if (inputVO.getsDate() != null && inputVO.geteDate() != null) {
				sb.append(" AND CREATETIME >= :sDate and trunc(CREATETIME) <= :eDate ");
				qc.setObject("sDate", inputVO.getsDate());
				qc.setObject("eDate", inputVO.geteDate());
			}

			sb.append(" ORDER BY QUESTION_VERSION DESC");
			qc.setQueryString(sb.toString());
			Main_list = dam_obj.exeQuery(qc);
			for (Map<String, Object> map : Main_list) {
				sb = new StringBuilder();
				qc = dam_obj
						.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sb.append(" SELECT QUESTION_VERSION, ANSWER_DESC,ANSWER_SEQ	");
				sb.append("		FROM TBSYS_QST_ANSWER WHERE QUESTION_VERSION = :question_version ");
				qc.setObject("question_version", map.get("QUESTION_VERSION"));
				qc.setQueryString(sb.toString());
				map.put("answer", dam_obj.exeQuery(qc));
			}
			outputVO.setQstQustionLst(Main_list);
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",
					StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
		sendRtnObject(outputVO);
	}

	public void addQstData(Object body, IPrimitiveMap<Object> header)
			throws JBranchException {
		KYC510InputVO inputVO = (KYC510InputVO) body;
		String loginID = (String) getCommonVariable(SystemVariableConsts.LOGINID);
		dam_obj = new DataAccessManager();
		QueryConditionIF qc = dam_obj
				.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuilder sb = new StringBuilder();
		if (inputVO.getPreview_data() == null) {
			for (Map<String, Object> question : inputVO.getSelectData()) {
				String quertion_version = (String) question
						.get("QUESTION_VERSION");
				qc = dam_obj
						.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sb = new StringBuilder();
				sb.append(" insert into TBSYS_QUESTIONNAIRE(EXAM_VERSION,QUESTION_VERSION,VERSION,CREATETIME,CREATOR) ");
				sb.append(" values(:exam_version,:question_version,'1',sysdate, :creator) ");
				qc.setObject("exam_version", inputVO.getEXAM_VERSION());
				qc.setObject("question_version", quertion_version);
				qc.setObject("creator", loginID);
				qc.setQueryString(sb.toString());
				dam_obj.exeUpdate(qc);
				List<Map<String, Object>> answer = new ArrayList<Map<String, Object>>();
				answer = (List<Map<String, Object>>) question.get("Ans");
				for (Map<String, Object> ans : answer) {
					Object answer_seq = ans.get("ANSWER_SEQ");
					qc = dam_obj
							.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
					sb = new StringBuilder();
					sb.append(" insert into tbkyc_questionnaire_ans_weight(EXAM_VERSION,QUESTION_VERSION,ANSWER_SEQ,VERSION,CREATETIME,CREATOR) ");
					sb.append(" values(:exam_version,:question_version,:answer_seq,'1',sysdate, :creator) ");
					qc.setObject("exam_version", inputVO.getEXAM_VERSION());
					qc.setObject("question_version", quertion_version);
					qc.setObject("answer_seq", answer_seq);
					qc.setObject("creator", loginID);
					qc.setQueryString(sb.toString());
					dam_obj.exeUpdate(qc);
				}
			}
		} else {
			List<Map<String, Object>> dropdata = new ArrayList<Map<String, Object>>();
			dropdata.addAll(inputVO.getSelectData());
			for (Map<String, Object> original : inputVO.getPreview_data()) {
				for (Map<String, Object> question : inputVO.getSelectData()) {
					if (original.get("EXAM_VERSION").equals(
							inputVO.getEXAM_VERSION())
							&& original.get("QUESTION_VERSION").equals(
									question.get("QUESTION_VERSION"))) {
						dropdata.remove(question);
					}
				}
			}
			for (Map<String, Object> updata : dropdata) {
				qc = dam_obj
						.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sb = new StringBuilder();
				sb.append(" insert into TBSYS_QUESTIONNAIRE(EXAM_VERSION,QUESTION_VERSION,VERSION,CREATETIME,CREATOR) ");
				sb.append(" values(:exam_version,:question_version,'1',sysdate, :creator) ");
				qc.setObject("exam_version", inputVO.getEXAM_VERSION());
				qc.setObject("question_version", updata.get("QUESTION_VERSION"));
				qc.setObject("creator", loginID);
				qc.setQueryString(sb.toString());
				dam_obj.exeUpdate(qc);
				List<Map<String, Object>> answer = new ArrayList<Map<String, Object>>();
				answer = (List<Map<String, Object>>) updata.get("Ans");
				for (Map<String, Object> ans : answer) {
					Object answer_seq = ans.get("ANSWER_SEQ");
					qc = dam_obj
							.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
					sb = new StringBuilder();
					sb.append(" insert into tbkyc_questionnaire_ans_weight(EXAM_VERSION,QUESTION_VERSION,ANSWER_SEQ,VERSION,CREATETIME,CREATOR) ");
					sb.append(" values(:exam_version,:question_version,:answer_seq,'1',sysdate, :creator) ");
					qc.setObject("exam_version", inputVO.getEXAM_VERSION());
					qc.setObject("question_version",
							updata.get("QUESTION_VERSION"));
					qc.setObject("answer_seq", answer_seq);
					qc.setObject("creator", loginID);
					qc.setQueryString(sb.toString());
					dam_obj.exeUpdate(qc);
				}
			}
		}
		sendRtnObject(null);
	}

}
