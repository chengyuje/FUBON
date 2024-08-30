package com.systex.jbranch.app.server.fps.kyc214;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;



















import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.StringUtil;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * KYC214
 * 
 * @author Jimmy
 * @date 2016/08/03
 * @spec null
 */
@Component("kyc214")
@Scope("request")
public class KYC214 extends FubonWmsBizLogic {
	Logger logger = LoggerFactory.getLogger(this.getClass());
	
	public void queryData(Object body, IPrimitiveMap<Object> header) throws JBranchException{
		KYC214InputVO inputVO = (KYC214InputVO) body;
		KYC214OutputVO outputVO = new KYC214OutputVO();
		try {
			QueryConditionIF qc = getDataAccessManager().getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			List<Map<String,Object>> Main_list = new ArrayList<Map<String,Object>>(); 
			StringBuilder sb = new StringBuilder();
			sb.append(" SELECT QUESTION_VERSION, QUESTION_DESC,QUESTION_TYPE,ANS_OTHER_FLAG,ANS_MEMO_FLAG,STATUS,CREATOR,to_char(CREATETIME,'YYYY/MM/DD') as CREATETIME FROM TBSYS_QST_QUESTION WHERE 1=1 and MODULE_CATEGORY = 'KYC'");
			if(!"".equals(inputVO.getQUESTION_DESC())){
				sb.append(" AND QUESTION_DESC LIKE :question");
				qc.setObject("question", "%"+inputVO.getQUESTION_DESC()+"%");
			}

			if(inputVO.getsDate() != null && inputVO.geteDate() == null){
				sb.append(" AND CREATETIME >= :sDate ");
				qc.setObject("sDate", inputVO.getsDate());
			}
			if(inputVO.getsDate() == null && inputVO.geteDate() != null){
				sb.append(" AND CREATETIME <= :eDate ");
				qc.setObject("eDate", inputVO.geteDate());
			}
			if(inputVO.getsDate() != null && inputVO.geteDate() != null){
				sb.append(" AND CREATETIME >= :sDate and trunc(CREATETIME) <= :eDate ");
				qc.setObject("sDate", inputVO.getsDate());
				qc.setObject("eDate", inputVO.geteDate());
			}

			sb.append(" ORDER BY QUESTION_VERSION DESC");
			qc.setQueryString(sb.toString());
			Main_list = getDataAccessManager().exeQuery(qc);
			for(Map<String, Object> map:Main_list){
				sb = new StringBuilder();
				qc = getDataAccessManager().getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sb.append(" SELECT QUESTION_VERSION, ANSWER_DESC,ANSWER_SEQ FROM TBSYS_QST_ANSWER WHERE QUESTION_VERSION = :question_version ");
				qc.setObject("question_version", map.get("QUESTION_VERSION"));
				qc.setQueryString(sb.toString());
				map.put("answer", getDataAccessManager().exeQuery(qc));
			}
			outputVO.setQstQustionLst(Main_list);
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s", StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
		sendRtnObject(outputVO);
	}
	
	public void addData(Object body, IPrimitiveMap<Object> header) throws JBranchException{
		KYC214InputVO inputVO = (KYC214InputVO) body;
		String loginID = (String) getCommonVariable(SystemVariableConsts.LOGINID);
		QueryConditionIF qc = getDataAccessManager().getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuilder sb = new StringBuilder();
		if(inputVO.getPreview_data()==null){
			for(Map<String, Object> question :inputVO.getSelectData()){
				String quertion_version = (String) question.get("QUESTION_VERSION");
				qc = getDataAccessManager().getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sb = new StringBuilder();
				sb.append(" insert into TBSYS_QUESTIONNAIRE(EXAM_VERSION,QUESTION_VERSION,VERSION,CREATETIME,CREATOR) ");
				sb.append(" values(:exam_version,:question_version,'1',sysdate, :creator) ");
				qc.setObject("exam_version", inputVO.getEXAM_VERSION());
				qc.setObject("question_version", quertion_version);
				qc.setObject("creator", loginID);
				qc.setQueryString(sb.toString());
				getDataAccessManager().exeUpdate(qc);
				List<Map<String, Object>> answer = new ArrayList<Map<String,Object>>();
				answer=(List<Map<String, Object>>) question.get("Ans");
				for(Map<String, Object> ans :answer ){
					Object answer_seq = ans.get("ANSWER_SEQ");
					qc = getDataAccessManager().getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
					sb = new StringBuilder();
					sb.append(" insert into tbkyc_questionnaire_ans_weight(EXAM_VERSION,QUESTION_VERSION,ANSWER_SEQ,VERSION,CREATETIME,CREATOR) ");
					sb.append(" values(:exam_version,:question_version,:answer_seq,'1',sysdate, :creator) ");
					qc.setObject("exam_version", inputVO.getEXAM_VERSION());
					qc.setObject("question_version", quertion_version);
					qc.setObject("answer_seq", answer_seq);
					qc.setObject("creator", loginID);
					qc.setQueryString(sb.toString());
					getDataAccessManager().exeUpdate(qc);
				}
			}
		}else{
			List<Map<String, Object>> dropdata = new ArrayList<Map<String,Object>>();
			dropdata.addAll(inputVO.getSelectData());
			for(Map<String, Object> original : inputVO.getPreview_data()){
				for(Map<String, Object> question :inputVO.getSelectData()){
					if(original.get("EXAM_VERSION").equals(inputVO.getEXAM_VERSION()) 
							&& original.get("QUESTION_VERSION").equals(question.get("QUESTION_VERSION"))){
							dropdata.remove(question);
					}
				}
			}
			for(Map<String, Object> updata :dropdata ){
				qc = getDataAccessManager().getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sb = new StringBuilder();
				sb.append(" insert into TBSYS_QUESTIONNAIRE(EXAM_VERSION,QUESTION_VERSION,VERSION,CREATETIME,CREATOR) ");
				sb.append(" values(:exam_version,:question_version,'1',sysdate, :creator) ");
				qc.setObject("exam_version", inputVO.getEXAM_VERSION());
				qc.setObject("question_version", updata.get("QUESTION_VERSION"));
				qc.setObject("creator", loginID);
				qc.setQueryString(sb.toString());
				getDataAccessManager().exeUpdate(qc);
				List<Map<String, Object>> answer = new ArrayList<Map<String,Object>>();
				answer=(List<Map<String, Object>>) updata.get("Ans");
				for(Map<String, Object> ans :answer ){
					Object answer_seq = ans.get("ANSWER_SEQ");
					qc = getDataAccessManager().getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
					sb = new StringBuilder();
					sb.append(" insert into tbkyc_questionnaire_ans_weight(EXAM_VERSION,QUESTION_VERSION,ANSWER_SEQ,VERSION,CREATETIME,CREATOR) ");
					sb.append(" values(:exam_version,:question_version,:answer_seq,'1',sysdate, :creator) ");
					qc.setObject("exam_version", inputVO.getEXAM_VERSION());
					qc.setObject("question_version", updata.get("QUESTION_VERSION"));
					qc.setObject("answer_seq", answer_seq);
					qc.setObject("creator", loginID);
					qc.setQueryString(sb.toString());
					getDataAccessManager().exeUpdate(qc);
				}
			}
		}
		sendRtnObject(null);
	}
	
}
