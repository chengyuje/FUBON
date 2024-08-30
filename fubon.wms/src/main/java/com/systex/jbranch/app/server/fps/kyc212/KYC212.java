package com.systex.jbranch.app.server.fps.kyc212;

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
import com.systex.jbranch.platform.util.IPrimitiveMap;


/**
 * KYC212
 * 
 * @author Jimmy
 * @date 2016/08/03
 * @spec null
 */



@Component("kyc212")
@Scope("request")
public class KYC212 extends FubonWmsBizLogic{
	Logger logger = LoggerFactory.getLogger(this.getClass());
	
	public void InitialWeight(Object body, IPrimitiveMap<Object> header) throws JBranchException{
		KYC212InputVO inputVO = (KYC212InputVO) body;
		KYC212OutputVO outputVO = new KYC212OutputVO();
		try {
			QueryConditionIF qc = getDataAccessManager().getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuilder sb = new StringBuilder();
			sb.append(" select answer_desc,fraction,a.ANSWER_SEQ,a.EXAM_VERSION,a.QUESTION_VERSION ");
			sb.append(" from tbkyc_questionnaire_ans_weight a,tbsys_qst_answer b ");
			sb.append(" where a.question_version = b.question_version ");
			sb.append(" and a.answer_seq = b.answer_seq ");
			sb.append(" and a.exam_version = :exam_version ");
			sb.append(" and a.question_version = :question_version ");
			qc.setObject("exam_version", inputVO.getEXAM_VERSION());
			qc.setObject("question_version", inputVO.getQUESTION_VERSION());
			qc.setQueryString(sb.toString());
			outputVO.setWeights(getDataAccessManager().exeQuery(qc));
			sendRtnObject(outputVO);
			
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s", StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");		
		}
	}
	
	public void SaveWeight(Object body, IPrimitiveMap<Object> header) throws JBranchException{
		KYC212InputVO inputVO = (KYC212InputVO) body;
		try {
			
			for(Map<String, Object> weight:inputVO.getWeights()){
				QueryConditionIF qc = getDataAccessManager().getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				StringBuilder sb = new StringBuilder();
				sb.append(" update tbkyc_questionnaire_ans_weight set FRACTION = :fraction ");
				sb.append(" where EXAM_VERSION = :exam_version and QUESTION_VERSION = :question_version and ANSWER_SEQ = :answer_seq ");
				qc.setObject("fraction", weight.get("FRACTION"));
				qc.setObject("exam_version", inputVO.getEXAM_VERSION());
				qc.setObject("question_version", inputVO.getQUESTION_VERSION());
				qc.setObject("answer_seq", weight.get("ANSWER_SEQ"));
				qc.setQueryString(sb.toString());
				getDataAccessManager().exeUpdate(qc);
			}
			sendRtnObject(null);
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s", StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");		
		}
	}
}
