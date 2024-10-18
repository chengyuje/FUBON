package com.systex.jbranch.app.server.fps.kyc211;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.mozilla.javascript.Undefined;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import com.systex.jbranch.app.common.fps.table.TBKYC_QUESTIONNAIRE_ANS_WEIGHTPK;
import com.systex.jbranch.app.common.fps.table.TBKYC_QUESTIONNAIRE_ANS_WEIGHTVO;
import com.systex.jbranch.app.common.fps.table.TBKYC_QUESTIONNAIRE_RISK_LEVELPK;
import com.systex.jbranch.app.common.fps.table.TBKYC_QUESTIONNAIRE_RISK_LEVELVO;
import com.systex.jbranch.app.common.fps.table.TBKYC_QUESTIONNAIRE_RISK_LRATEPK;
import com.systex.jbranch.app.common.fps.table.TBKYC_QUESTIONNAIRE_RISK_LRATEVO;
import com.systex.jbranch.app.common.fps.table.TBKYC_QUESTIONNAIRE_RISK_SCOREPK;
import com.systex.jbranch.app.common.fps.table.TBKYC_QUESTIONNAIRE_RISK_SCOREVO;
import com.systex.jbranch.app.common.fps.table.TBSYS_QUESTIONNAIREPK;
import com.systex.jbranch.app.common.fps.table.TBSYS_QUESTIONNAIREVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.serialnumber.SerialNumberUtil;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.StringUtil;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;

import java.util.Collections;


/**
 * KYC211
 * 
 * @author Jimmy
 * @date 2016/07/22
 * @spec null
 */
@Component("kyc211")
@Scope("request")
public class KYC211 extends FubonWmsBizLogic{
	Logger logger = LoggerFactory.getLogger(this.getClass());
	
	public void addQuestionnaireInitial(Object body, IPrimitiveMap<Object> header) throws JBranchException{
		KYC211InputVO inputVO = (KYC211InputVO) body;
		KYC211OutputVO outputVO = new KYC211OutputVO();
		List<Map<String, Object>> question = new ArrayList<Map<String,Object>>();
		List<Map<String, Object>> Remark = new ArrayList<Map<String,Object>>();
//		List<Map<String, Object>> risklist = new ArrayList<Map<String,Object>>(); 
		try {
			//取得問卷題目
			QueryConditionIF qc = getDataAccessManager().getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuilder sb = new StringBuilder();
			sb.append(" select a.exam_version,a.quest_type,a.qst_no, ");
			sb.append(" b.question_type, ");
			sb.append(" b.question_desc,b.question_desc_eng,b.ans_other_flag,b.ans_memo_flag,a.question_version,a.ACTIVE_DATE,a.ESSENTIAL_FLAG, ");
			sb.append(" a.RL_VERSION,a.RS_VERSION,a.QST_WEIGHT ,a.SCORE_TYPE ,a.INT_SCORE, a.RLR_VERSION ");
			sb.append(" from TBSYS_QUESTIONNAIRE a,TBSYS_QST_QUESTION b ");
			sb.append(" where a.question_version = b.question_version and a.exam_version = :exam_version order by qst_no");
			qc.setObject("exam_version", inputVO.getEXAM_VERSION());
			qc.setQueryString(sb.toString());
			question=getDataAccessManager().exeQuery(qc);
			qc = getDataAccessManager().getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sb = new StringBuilder();
			sb.append(" select SIGNOFF_ID,SIGNOFF_NAME,to_char(CREATETIME,'YYYY/MM/DD') as CREATETIME,REMARK ");
			sb.append(" from TBKYC_QUESTIONNAIRE_FLW_DETAIL ");
			sb.append(" where EXAM_VERSION = :remark_exam_version ");
			qc.setObject("remark_exam_version", inputVO.getEXAM_VERSION());
			qc.setQueryString(sb.toString());
			Remark = getDataAccessManager().exeQuery(qc);
			if(Remark.size()>0){
				question.get(0).put("Remark", Remark);
			}
			//取得問卷題目答案和權重
			for(Map<String,Object> questionS : question){
				String question_version = (String) questionS.get("QUESTION_VERSION");
				String exam_version = (String) questionS.get("EXAM_VERSION");
				qc = getDataAccessManager().getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sb = new StringBuilder();
				sb.append(" select a.question_version,a.answer_seq,a.answer_desc,a.answer_desc_eng,b.fraction " );
				sb.append(" from tbsys_qst_answer a,tbkyc_questionnaire_ans_weight b ");
				sb.append(" where a.question_version = :question_version ");
				sb.append(" and b.exam_version = :exam_version ");
				sb.append(" and b.question_version = a.question_version ");
				sb.append(" and a.answer_seq = b.answer_seq ");
				sb.append(" order by a.answer_seq ");
				qc.setObject("question_version", question_version);
				qc.setObject("exam_version", exam_version);
				qc.setQueryString(sb.toString());
				questionS.put("answer", getDataAccessManager().exeQuery(qc));
				//取得差異表選項
				qc = getDataAccessManager().getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sb = new StringBuilder();
				sb.append(" select a.question_version,a.answer_seq,a.answer_desc,a.answer_desc_eng " );
				sb.append(" from tbsys_qst_answer_comp a ");
				sb.append(" where a.question_version = :question_version ");
				sb.append(" order by a.answer_seq ");
				qc.setObject("question_version", question_version);
				qc.setQueryString(sb.toString());
				questionS.put("answerComp", getDataAccessManager().exeQuery(qc));
				//取得風險級距
				String rl_version = (String) questionS.get("RL_VERSION");
				if(rl_version!=null){
					qc = getDataAccessManager().getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
					sb = new StringBuilder();
					sb.append(" select * from TBKYC_QUESTIONNAIRE_RISK_LEVEL ");
					sb.append(" where RL_VERSION =  :rl_version");
					sb.append(" order by CUST_RL_ID ");
					qc.setObject("rl_version", rl_version);
					qc.setQueryString(sb.toString());
					questionS.put("risk", getDataAccessManager().exeQuery(qc));
				}
				
				//取得可承受風險損失率
				String rlr_version = (String) questionS.get("RLR_VERSION");
				if(rlr_version!=null){
					qc = getDataAccessManager().getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
					sb = new StringBuilder();
					sb.append(" select CUST_RL_ID as LRATE_CUST_RL_ID, RL_NAME as LRATE_RL_NAME, RL_UP_RATE as LRATE_RL_UP_RATE ");
				    sb.append(" from TBKYC_QUESTIONNAIRE_RISK_LRATE ");
					sb.append(" where RLR_VERSION =  :rlr_version");
					sb.append(" order by CUST_RL_ID ");
					qc.setObject("rlr_version", rlr_version);
					qc.setQueryString(sb.toString());
					questionS.put("lRate", getDataAccessManager().exeQuery(qc));
				}
				
				//取得風險承受能力
				String rs_version = (String) questionS.get("RS_VERSION");
				if(rs_version!=null){
					qc = getDataAccessManager().getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
					sb = new StringBuilder();
					sb.append(" SELECT DISTINCT C_VAL ");
					sb.append(" FROM ( ");
					sb.append("       SELECT DISTINCT C_SCORE_BTN AS C_VAL ");
					sb.append("       FROM TBKYC_QUESTIONNAIRE_RISK_SCORE ");
					sb.append("       WHERE RS_VERSION = :rs_version ");
					sb.append("       UNION ALL ");
					sb.append("       SELECT DISTINCT C_SCORE_TOP AS C_VAL "); 
					sb.append("       FROM TBKYC_QUESTIONNAIRE_RISK_SCORE ");
					sb.append("       WHERE RS_VERSION = :rs_version ) ");
					sb.append(" ORDER BY C_VAL ASC ");
					qc.setObject("rs_version", rs_version);
					qc.setQueryString(sb.toString());
					questionS.put("C_VAL", getDataAccessManager().exeQuery(qc));
					
					qc = getDataAccessManager().getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
					sb = new StringBuilder();
					sb.append(" SELECT DISTINCT W_VAL ");
					sb.append(" FROM ( ");
					sb.append("       SELECT DISTINCT W_SCORE_BTN AS W_VAL ");
					sb.append("       FROM TBKYC_QUESTIONNAIRE_RISK_SCORE ");
					sb.append("       WHERE RS_VERSION = :rs_version ");
					sb.append("       UNION ALL ");
					sb.append("       SELECT DISTINCT W_SCORE_TOP AS W_VAL "); 
					sb.append("       FROM TBKYC_QUESTIONNAIRE_RISK_SCORE ");
					sb.append("       WHERE RS_VERSION = :rs_version ) ");
					sb.append(" ORDER BY W_VAL ASC ");
					qc.setObject("rs_version", rs_version);
					qc.setQueryString(sb.toString());
					questionS.put("W_VAL", getDataAccessManager().exeQuery(qc));
					
					qc = getDataAccessManager().getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
					sb = new StringBuilder();
					sb.append(" SELECT C_LEVEL,W_LEVEL,CUST_RL_ID ");
					sb.append(" FROM TBKYC_QUESTIONNAIRE_RISK_SCORE ");
					sb.append(" WHERE RS_VERSION = :rs_version ");
					sb.append(" ORDER BY W_LEVEL,C_LEVEL ASC");
					qc.setObject("rs_version", rs_version);
					qc.setQueryString(sb.toString());
					questionS.put("CW_CUST_RL_ID", getDataAccessManager().exeQuery(qc));
					
				}
			}
			outputVO.setQuestionList(question);
			sendRtnObject(outputVO);
			
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s", StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");		
		}
	}


	
	public void editSaveData(Object body, IPrimitiveMap<Object> header) throws JBranchException{
			KYC211InputVO inputVO = (KYC211InputVO) body;
			String loginID = (String) getCommonVariable(SystemVariableConsts.LOGINID);
			String errorMsg = "";
			QueryConditionIF qc = getDataAccessManager().getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuilder sb = new StringBuilder();
			try {
				if(inputVO.getCopy()){
					if(inputVO.getRISK_LEVEL() != null){
						for(Map<String,Object> map:inputVO.getRISK_LEVEL()){
							TBKYC_QUESTIONNAIRE_RISK_LEVELPK risk_pk = new TBKYC_QUESTIONNAIRE_RISK_LEVELPK();
							TBKYC_QUESTIONNAIRE_RISK_LEVELVO risk_vo = new TBKYC_QUESTIONNAIRE_RISK_LEVELVO();
								if(map.get("ECUST_RL_ID")==null){
									risk_pk.setCUST_RL_ID(map.get("CUST_RL_ID").toString());
								}else{
									risk_pk.setCUST_RL_ID(map.get("ECUST_RL_ID").toString());
								}
								risk_pk.setRL_VERSION(inputVO.getRL_VERSION());
								risk_vo = new TBKYC_QUESTIONNAIRE_RISK_LEVELVO();
								risk_vo.setcomp_id(risk_pk);
								if(map.get("ERL_NAME")==null){
									risk_vo.setRL_NAME(map.get("RL_NAME").toString());
								}else{
									risk_vo.setRL_NAME(map.get("ERL_NAME").toString());
								}
								if(map.get("ERL_NAME_ENG")==null){
									risk_vo.setRL_NAME_ENG(map.get("RL_NAME_ENG").toString());
								}else{
									risk_vo.setRL_NAME_ENG(map.get("ERL_NAME_ENG").toString());
								}
								if(map.get("ERL_UP_RATE")==null){
									risk_vo.setRL_UP_RATE(new BigDecimal(map.get("RL_UP_RATE").toString()));
								}else{
									risk_vo.setRL_UP_RATE(new BigDecimal(map.get("ERL_UP_RATE").toString()));
								}
								risk_vo.setPROD_RL_UP_RATE(map.get("PROD_RL_UP_RATE").toString());
								getDataAccessManager().create(risk_vo);
						}
					}
					
					if(inputVO.getLRATE_RISK_LEVEL() != null){
						for(Map<String,Object> map:inputVO.getLRATE_RISK_LEVEL()){
							TBKYC_QUESTIONNAIRE_RISK_LRATEPK risk_pk = new TBKYC_QUESTIONNAIRE_RISK_LRATEPK();
							TBKYC_QUESTIONNAIRE_RISK_LRATEVO risk_vo = new TBKYC_QUESTIONNAIRE_RISK_LRATEVO();
								if(map.get("LCUST_RL_ID")==null){
									risk_pk.setCUST_RL_ID(map.get("LRATE_CUST_RL_ID").toString());
								}else{
									risk_pk.setCUST_RL_ID(map.get("LCUST_RL_ID").toString());
								}
								risk_pk.setRLR_VERSION(inputVO.getRLR_VERSION());
								risk_vo = new TBKYC_QUESTIONNAIRE_RISK_LRATEVO();
								risk_vo.setcomp_id(risk_pk);
								if(map.get("LRL_NAME")==null){
									risk_vo.setRL_NAME(map.get("LRATE_RL_NAME").toString());
								}else{
									risk_vo.setRL_NAME(map.get("LRL_NAME").toString());
								}
								if(map.get("LRL_UP_RATE")==null){
									risk_vo.setRL_UP_RATE(new BigDecimal(map.get("LRATE_RL_UP_RATE").toString()));
								}else{
									risk_vo.setRL_UP_RATE(new BigDecimal(map.get("LRL_UP_RATE").toString()));
								}

								getDataAccessManager().create(risk_vo);
						}
					}
					
					if(inputVO.getCUST_RL_ID() != null){
						int j = 0;
						for(Map<String,Object> map : inputVO.getCUST_RL_ID()){
							int i = 0;
							for(Map.Entry<String,Object> entry : map.entrySet()){
							
								TBKYC_QUESTIONNAIRE_RISK_SCOREPK rs_pk = new TBKYC_QUESTIONNAIRE_RISK_SCOREPK();
								TBKYC_QUESTIONNAIRE_RISK_SCOREVO rs_vo = new TBKYC_QUESTIONNAIRE_RISK_SCOREVO();
								
								rs_pk.setRS_VERSION(inputVO.getRS_VERSION());
								rs_pk.setC_LEVEL(String.valueOf(i));
								rs_pk.setW_LEVEL(String.valueOf(j));
								rs_vo = new TBKYC_QUESTIONNAIRE_RISK_SCOREVO();
								rs_vo.setcomp_id(rs_pk);
								rs_vo.setC_SCORE_BTN(new BigDecimal(String.valueOf(inputVO.getC_VAL().get(i))));
								rs_vo.setC_SCORE_TOP(new BigDecimal(String.valueOf(inputVO.getC_VAL().get(i+1))));
								rs_vo.setW_SCORE_BTN(new BigDecimal(String.valueOf(inputVO.getW_VAL().get(j))));
								rs_vo.setW_SCORE_TOP(new BigDecimal(String.valueOf(inputVO.getW_VAL().get(j+1))));
								rs_vo.setCUST_RL_ID(entry.getValue().toString());
								getDataAccessManager().create(rs_vo);
								i++;
							}
							j++;
						}
					}
					
					for(Map<String,Object> question_data:inputVO.getUpdate_preview_data()){
						
						TBSYS_QUESTIONNAIREPK question_pk = new TBSYS_QUESTIONNAIREPK();
						question_pk.setEXAM_VERSION(inputVO.getEXAM_VERSION());
						question_pk.setQUESTION_VERSION(question_data.get("QUESTION_VERSION").toString());
						TBSYS_QUESTIONNAIREVO question_vo = new TBSYS_QUESTIONNAIREVO();
						question_vo = new TBSYS_QUESTIONNAIREVO();
						question_vo.setcomp_id(question_pk);
						question_vo.setEXAM_NAME(inputVO.getEXAM_NAME());
						question_vo.setQUEST_TYPE(inputVO.getQUEST_TYPE());
						question_vo.setQST_NO(new BigDecimal(question_data.get("QST_NO").toString()));
						question_vo.setESSENTIAL_FLAG(question_data.get("ESSENTIAL_FLAG").toString());
						question_vo.setRL_VERSION(inputVO.getRL_VERSION());
						question_vo.setRS_VERSION(inputVO.getRS_VERSION());
						question_vo.setRLR_VERSION(inputVO.getRLR_VERSION());
						question_vo.setSTATUS("01");
						question_vo.setACTIVE_DATE(new Timestamp(inputVO.getACTIVE_DATE().getTime()));
						question_vo.setQST_WEIGHT(new BigDecimal(question_data.get("QST_WEIGHT").toString()));
						if(!StringUtils.isEmpty(question_data.get("SCORE_TYPE"))){
							question_vo.setSCORE_TYPE(question_data.get("SCORE_TYPE").toString());
						}else{
							question_vo.setSCORE_TYPE("");
						}
						question_vo.setINT_SCORE(inputVO.getINT_SCORE());
						getDataAccessManager().create(question_vo);
						
						List<Map<String, Object>> ansList = new ArrayList<Map<String,Object>>();
						ansList = (List<Map<String, Object>>) question_data.get("answer");
						for(Map<String, Object> ans:ansList){
							if(ans.get("FRACTION") == null){
								errorMsg = "未設定權重分數";
								throw new APException(errorMsg);
							}else{
								TBKYC_QUESTIONNAIRE_ANS_WEIGHTPK ans_pk = new TBKYC_QUESTIONNAIRE_ANS_WEIGHTPK();
								ans_pk.setANSWER_SEQ(new BigDecimal(ans.get("ANSWER_SEQ").toString()));
								ans_pk.setEXAM_VERSION(inputVO.getEXAM_VERSION());
								ans_pk.setQUESTION_VERSION(ans.get("QUESTION_VERSION").toString());
								TBKYC_QUESTIONNAIRE_ANS_WEIGHTVO ans_vo = new TBKYC_QUESTIONNAIRE_ANS_WEIGHTVO();
								ans_vo.setcomp_id(ans_pk);
								ans_vo.setFRACTION(new BigDecimal(ans.get("FRACTION").toString()));
								getDataAccessManager().create(ans_vo);

							}
						}
					}

				}else{
					if(inputVO.getDEL_CUST_RL_ID() != null){
						for(String del:inputVO.getDEL_CUST_RL_ID()){
							TBKYC_QUESTIONNAIRE_RISK_LEVELPK risk_pk = new TBKYC_QUESTIONNAIRE_RISK_LEVELPK();
							risk_pk.setCUST_RL_ID(del);
							risk_pk.setRL_VERSION(inputVO.getRL_VERSION());
							TBKYC_QUESTIONNAIRE_RISK_LEVELVO risk_vo = new TBKYC_QUESTIONNAIRE_RISK_LEVELVO();
							risk_vo = (TBKYC_QUESTIONNAIRE_RISK_LEVELVO) getDataAccessManager().findByPKey(TBKYC_QUESTIONNAIRE_RISK_LEVELVO.TABLE_UID, risk_pk);
							if(risk_vo != null){
								getDataAccessManager().delete(risk_vo);
							}
						}
					}
					if(inputVO.getLRATE_DEL_CUST_RL_ID() != null){
						for(String del : inputVO.getLRATE_DEL_CUST_RL_ID()){
							TBKYC_QUESTIONNAIRE_RISK_LRATEPK risk_pk = new TBKYC_QUESTIONNAIRE_RISK_LRATEPK();
							risk_pk.setCUST_RL_ID(del);
							risk_pk.setRLR_VERSION(inputVO.getRLR_VERSION());
							TBKYC_QUESTIONNAIRE_RISK_LRATEVO risk_vo = new TBKYC_QUESTIONNAIRE_RISK_LRATEVO();
							risk_vo = (TBKYC_QUESTIONNAIRE_RISK_LRATEVO) getDataAccessManager().findByPKey(TBKYC_QUESTIONNAIRE_RISK_LRATEVO.TABLE_UID, risk_pk);
							if(risk_vo != null){
								getDataAccessManager().delete(risk_vo);
							}
						}
					}
					if(inputVO.getRISK_LEVEL() != null){
						for(Map<String,Object> map:inputVO.getRISK_LEVEL()){
							TBKYC_QUESTIONNAIRE_RISK_LEVELPK risk_pk = new TBKYC_QUESTIONNAIRE_RISK_LEVELPK();
							TBKYC_QUESTIONNAIRE_RISK_LEVELVO risk_vo = new TBKYC_QUESTIONNAIRE_RISK_LEVELVO();
							if(map.get("ECUST_RL_ID")==null){
								risk_pk.setCUST_RL_ID(map.get("CUST_RL_ID").toString());
							}else{
								risk_pk.setCUST_RL_ID(map.get("ECUST_RL_ID").toString());
							}
							risk_pk.setRL_VERSION(inputVO.getRL_VERSION());
							risk_vo = (TBKYC_QUESTIONNAIRE_RISK_LEVELVO) getDataAccessManager().findByPKey(TBKYC_QUESTIONNAIRE_RISK_LEVELVO.TABLE_UID, risk_pk);
							if(risk_vo == null){
								
								risk_pk = new TBKYC_QUESTIONNAIRE_RISK_LEVELPK();
								risk_pk.setCUST_RL_ID(map.get("ECUST_RL_ID").toString());
								risk_pk.setRL_VERSION(inputVO.getRL_VERSION());
								risk_vo = new TBKYC_QUESTIONNAIRE_RISK_LEVELVO();
								risk_vo.setcomp_id(risk_pk);
								risk_vo.setRL_NAME(map.get("ERL_NAME").toString());
								risk_vo.setRL_NAME_ENG(map.get("ERL_NAME_ENG").toString());
								risk_vo.setRL_UP_RATE(new BigDecimal(map.get("ERL_UP_RATE").toString()));
								risk_vo.setPROD_RL_UP_RATE(map.get("PROD_RL_UP_RATE").toString());
								getDataAccessManager().create(risk_vo);
							}else{
								if(map.get("ERL_NAME_ENG") != null) {
									risk_vo.setRL_NAME_ENG(map.get("ERL_NAME_ENG").toString());
								}
								risk_vo.setPROD_RL_UP_RATE(map.get("PROD_RL_UP_RATE").toString());
								getDataAccessManager().update(risk_vo);
							}
						}
					}
					if(inputVO.getLRATE_RISK_LEVEL() != null){
						for(Map<String,Object> map : inputVO.getLRATE_RISK_LEVEL()){
							TBKYC_QUESTIONNAIRE_RISK_LRATEPK risk_pk = new TBKYC_QUESTIONNAIRE_RISK_LRATEPK();
							TBKYC_QUESTIONNAIRE_RISK_LRATEVO risk_vo = new TBKYC_QUESTIONNAIRE_RISK_LRATEVO();
							if(map.get("LCUST_RL_ID")==null){
								risk_pk.setCUST_RL_ID(map.get("LRATE_CUST_RL_ID").toString());
							}else{
								risk_pk.setCUST_RL_ID(map.get("LCUST_RL_ID").toString());
							}
							risk_pk.setRLR_VERSION(inputVO.getRLR_VERSION());
							risk_vo = (TBKYC_QUESTIONNAIRE_RISK_LRATEVO) getDataAccessManager().findByPKey(TBKYC_QUESTIONNAIRE_RISK_LRATEVO.TABLE_UID, risk_pk);
							if(risk_vo == null){
								
								risk_pk = new TBKYC_QUESTIONNAIRE_RISK_LRATEPK();
								risk_pk.setCUST_RL_ID(map.get("LCUST_RL_ID").toString());
								risk_pk.setRLR_VERSION(inputVO.getRLR_VERSION());
								risk_vo = new TBKYC_QUESTIONNAIRE_RISK_LRATEVO();
								risk_vo.setcomp_id(risk_pk);
								risk_vo.setRL_NAME(map.get("LRL_NAME").toString());
								risk_vo.setRL_UP_RATE(new BigDecimal(map.get("LRL_UP_RATE").toString()));
								getDataAccessManager().create(risk_vo);
								
							}else{
							
								if(map.get("LRL_NAME")==null){
									risk_vo.setRL_NAME(map.get("LRATE_RL_NAME").toString());
								}else{
									risk_vo.setRL_NAME(map.get("LRL_NAME").toString());
								}
								
								if(map.get("LRL_UP_RATE")==null){
									risk_vo.setRL_UP_RATE(new BigDecimal(map.get("LRATE_RL_UP_RATE").toString()));
								}else{
									risk_vo.setRL_UP_RATE(new BigDecimal(map.get("LRL_UP_RATE").toString()));
								}
								
								
								getDataAccessManager().update(risk_vo);
							}
						}
					}
					if(inputVO.getCUST_RL_ID() != null){
						int j = 0;
						for(Map<String,Object> map : inputVO.getCUST_RL_ID()){
							int i = 0;
							for(Map.Entry<String,Object> entry : map.entrySet()){
							
								TBKYC_QUESTIONNAIRE_RISK_SCOREPK rs_pk = new TBKYC_QUESTIONNAIRE_RISK_SCOREPK();
								TBKYC_QUESTIONNAIRE_RISK_SCOREVO rs_vo = new TBKYC_QUESTIONNAIRE_RISK_SCOREVO();
								
								rs_pk.setRS_VERSION(inputVO.getRS_VERSION());
								rs_pk.setC_LEVEL(String.valueOf(i));
								rs_pk.setW_LEVEL(String.valueOf(j));
								rs_vo = (TBKYC_QUESTIONNAIRE_RISK_SCOREVO) getDataAccessManager().findByPKey(TBKYC_QUESTIONNAIRE_RISK_SCOREVO.TABLE_UID, rs_pk);
								
								if(rs_vo == null){
									rs_pk = new TBKYC_QUESTIONNAIRE_RISK_SCOREPK();
									rs_pk.setRS_VERSION(inputVO.getRS_VERSION());
									rs_pk.setC_LEVEL(String.valueOf(i));
									rs_pk.setW_LEVEL(String.valueOf(j));
									rs_vo = new TBKYC_QUESTIONNAIRE_RISK_SCOREVO();
									rs_vo.setcomp_id(rs_pk);
									rs_vo.setC_SCORE_BTN(new BigDecimal(String.valueOf(inputVO.getC_VAL().get(i))));
									rs_vo.setC_SCORE_TOP(new BigDecimal(String.valueOf(inputVO.getC_VAL().get(i+1))));
									rs_vo.setW_SCORE_BTN(new BigDecimal(String.valueOf(inputVO.getW_VAL().get(j))));
									rs_vo.setW_SCORE_TOP(new BigDecimal(String.valueOf(inputVO.getW_VAL().get(j+1))));
									rs_vo.setCUST_RL_ID(entry.getValue().toString());
									getDataAccessManager().create(rs_vo);
								}else{
									rs_vo.setC_SCORE_BTN(new BigDecimal(String.valueOf(inputVO.getC_VAL().get(i))));
									rs_vo.setC_SCORE_TOP(new BigDecimal(String.valueOf(inputVO.getC_VAL().get(i+1))));
									rs_vo.setW_SCORE_BTN(new BigDecimal(String.valueOf(inputVO.getW_VAL().get(j))));
									rs_vo.setW_SCORE_TOP(new BigDecimal(String.valueOf(inputVO.getW_VAL().get(j+1))));
									rs_vo.setCUST_RL_ID(entry.getValue().toString());
									getDataAccessManager().update(rs_vo);
								}
								i++;
							}
							j++;
						}
					}

					if(inputVO.getDEL_QUESTION() != null){
						for(Map<String, Object> del_quest:inputVO.getDEL_QUESTION()){
							List<Map<String, Object>> del_ansList = new ArrayList<Map<String,Object>>();
							del_ansList = (List<Map<String, Object>>) del_quest.get("answer");
							for(Map<String, Object> ans:del_ansList){
								TBKYC_QUESTIONNAIRE_ANS_WEIGHTPK ans_pk = new TBKYC_QUESTIONNAIRE_ANS_WEIGHTPK();
								ans_pk.setANSWER_SEQ(new BigDecimal(ans.get("ANSWER_SEQ").toString()));
								ans_pk.setEXAM_VERSION(inputVO.getEXAM_VERSION());
								ans_pk.setQUESTION_VERSION(ans.get("QUESTION_VERSION").toString());
								TBKYC_QUESTIONNAIRE_ANS_WEIGHTVO ans_vo = new TBKYC_QUESTIONNAIRE_ANS_WEIGHTVO();
								ans_vo = (TBKYC_QUESTIONNAIRE_ANS_WEIGHTVO) getDataAccessManager().findByPKey(TBKYC_QUESTIONNAIRE_ANS_WEIGHTVO.TABLE_UID, ans_pk);
								if(ans_vo != null){
									getDataAccessManager().delete(ans_vo);
								}
							}
							TBSYS_QUESTIONNAIREPK question_pk = new TBSYS_QUESTIONNAIREPK();
							question_pk.setEXAM_VERSION(inputVO.getEXAM_VERSION());
							question_pk.setQUESTION_VERSION(del_quest.get("QUESTION_VERSION").toString());
							TBSYS_QUESTIONNAIREVO question_vo = new TBSYS_QUESTIONNAIREVO();
							question_vo = (TBSYS_QUESTIONNAIREVO) getDataAccessManager().findByPKey(TBSYS_QUESTIONNAIREVO.TABLE_UID, question_pk);
							if(question_vo != null){
								getDataAccessManager().delete(question_vo);
							}
						}
					}
					
					for(Map<String,Object> question_data:inputVO.getUpdate_preview_data()){
						
						TBSYS_QUESTIONNAIREPK question_pk = new TBSYS_QUESTIONNAIREPK();
						question_pk.setEXAM_VERSION(inputVO.getEXAM_VERSION());
						question_pk.setQUESTION_VERSION(question_data.get("QUESTION_VERSION").toString());
						TBSYS_QUESTIONNAIREVO question_vo = new TBSYS_QUESTIONNAIREVO();
						question_vo = (TBSYS_QUESTIONNAIREVO) getDataAccessManager().findByPKey(TBSYS_QUESTIONNAIREVO.TABLE_UID, question_pk);
						if(question_vo == null){
							question_pk = new TBSYS_QUESTIONNAIREPK();
							question_pk.setEXAM_VERSION(inputVO.getEXAM_VERSION());
							question_pk.setQUESTION_VERSION(question_data.get("QUESTION_VERSION").toString());
							question_vo = new TBSYS_QUESTIONNAIREVO();
							question_vo.setcomp_id(question_pk);
							question_vo.setEXAM_NAME(inputVO.getEXAM_NAME());
							question_vo.setQUEST_TYPE(inputVO.getQUEST_TYPE());
							question_vo.setQST_NO(new BigDecimal(question_data.get("QST_NO").toString()));
							question_vo.setESSENTIAL_FLAG(question_data.get("ESSENTIAL_FLAG").toString());
							question_vo.setRL_VERSION(inputVO.getRL_VERSION());
							question_vo.setRS_VERSION(inputVO.getRS_VERSION());
							question_vo.setRLR_VERSION(inputVO.getRLR_VERSION());
							question_vo.setQST_WEIGHT(new BigDecimal(question_data.get("QST_WEIGHT").toString()));
							if(!StringUtils.isEmpty(question_data.get("SCORE_TYPE"))){
								question_vo.setSCORE_TYPE(question_data.get("SCORE_TYPE").toString());
							}else{
								question_vo.setSCORE_TYPE("");
							}
							question_vo.setINT_SCORE(inputVO.getINT_SCORE());
							question_vo.setSTATUS("01");
							question_vo.setACTIVE_DATE(new Timestamp(inputVO.getACTIVE_DATE().getTime()));
							getDataAccessManager().create(question_vo);
						}else{
							if(question_data.get("select") != null){
								errorMsg = "選取的題目重複";
								throw new APException(errorMsg);
							}else{
								question_vo.setQST_NO(new BigDecimal(question_data.get("QST_NO").toString()));
								question_vo.setESSENTIAL_FLAG(question_data.get("ESSENTIAL_FLAG").toString());
								question_vo.setACTIVE_DATE(new Timestamp(inputVO.getACTIVE_DATE().getTime()));
								question_vo.setQST_WEIGHT(new BigDecimal(question_data.get("QST_WEIGHT").toString()));
								if(!StringUtils.isEmpty(question_data.get("SCORE_TYPE"))){
									question_vo.setSCORE_TYPE(question_data.get("SCORE_TYPE").toString());
								}else{
									question_vo.setSCORE_TYPE("");
								}
								question_vo.setINT_SCORE(inputVO.getINT_SCORE());
								getDataAccessManager().update(question_vo);
							}
						}
						
						List<Map<String, Object>> ansList = new ArrayList<Map<String,Object>>();
						ansList = (List<Map<String, Object>>) question_data.get("answer");
						for(Map<String, Object> ans:ansList){
							if(ans.get("FRACTION") == null){
								errorMsg = "未設定權重分數";
								throw new APException(errorMsg);
							}else{
								TBKYC_QUESTIONNAIRE_ANS_WEIGHTPK ans_pk = new TBKYC_QUESTIONNAIRE_ANS_WEIGHTPK();
								ans_pk.setANSWER_SEQ(new BigDecimal(ans.get("ANSWER_SEQ").toString()));
								ans_pk.setEXAM_VERSION(inputVO.getEXAM_VERSION());
								ans_pk.setQUESTION_VERSION(ans.get("QUESTION_VERSION").toString());
								TBKYC_QUESTIONNAIRE_ANS_WEIGHTVO ans_vo = new TBKYC_QUESTIONNAIRE_ANS_WEIGHTVO();
								ans_vo = (TBKYC_QUESTIONNAIRE_ANS_WEIGHTVO) getDataAccessManager().findByPKey(TBKYC_QUESTIONNAIRE_ANS_WEIGHTVO.TABLE_UID, ans_pk);
								if(ans_vo == null){
									ans_pk = new TBKYC_QUESTIONNAIRE_ANS_WEIGHTPK();
									ans_pk.setANSWER_SEQ(new BigDecimal(ans.get("ANSWER_SEQ").toString()));
									ans_pk.setEXAM_VERSION(inputVO.getEXAM_VERSION());
									ans_pk.setQUESTION_VERSION(ans.get("QUESTION_VERSION").toString());
									ans_vo = new TBKYC_QUESTIONNAIRE_ANS_WEIGHTVO();
									ans_vo.setcomp_id(ans_pk);
									ans_vo.setFRACTION(new BigDecimal(ans.get("FRACTION").toString()));
									getDataAccessManager().create(ans_vo);
								}else{
									ans_vo.setFRACTION(new BigDecimal(ans.get("FRACTION").toString()));
									getDataAccessManager().update(ans_vo);
								}

							}
						}
					}
				}
				sendRtnObject(null);
			} catch (Exception e) {
				if(errorMsg.length()>0){
					throw new APException(errorMsg);
				}else{
					logger.error(String.format("發生錯誤:%s", StringUtil.getStackTraceAsString(e)));
					throw new APException("系統發生錯誤請洽系統管理員");	
				}
			}
	}
	
	
	public void deleteQuestion(Object body, IPrimitiveMap<Object> header) throws JBranchException{
		KYC211InputVO inputVO = (KYC211InputVO) body;		
		QueryConditionIF qc = getDataAccessManager().getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuilder sb = new StringBuilder();
		try {
			sb.append(" Delete tbkyc_questionnaire_ans_weight where EXAM_VERSION = :exam_version and QUESTION_VERSION = :question_version ");
			qc.setObject("exam_version", inputVO.getInExam_Version());
			qc.setObject("question_version", inputVO.getInQuest_Version());
			qc.setQueryString(sb.toString());
			getDataAccessManager().exeUpdate(qc);
			qc = getDataAccessManager().getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sb = new StringBuilder();
			sb.append(" Delete TBSYS_QUESTIONNAIRE where EXAM_VERSION = :exam_version and QUESTION_VERSION = :question_version ");
			qc.setObject("exam_version", inputVO.getInExam_Version());
			qc.setObject("question_version", inputVO.getInQuest_Version());
			qc.setQueryString(sb.toString());
			getDataAccessManager().exeUpdate(qc);
			sendRtnObject(null);
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s", StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");		
		}
		
	}
	
	private String getSeq() throws JBranchException{
		SerialNumberUtil sn = new SerialNumberUtil();
		java.util.Date date = new java.util.Date();
		SimpleDateFormat simple = new SimpleDateFormat("yyyyMMdd");
		String date_seqnum = simple.format(date);
		String seqNum = "";
		try {
			seqNum = "KYC"+date_seqnum+(sn.getNextSerialNumber("KYC111")).substring(1, 5);
		} catch (Exception e) {
			sn.createNewSerial("KYC211", "00000", null, null, null, 1,new Long("99999") , "y", new Long("0"), null);
			seqNum = "KYC"+date_seqnum+(sn.getNextSerialNumber("KYC111")).substring(1, 5);
		}
		return seqNum;
	}
	
}
