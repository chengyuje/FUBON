package com.systex.jbranch.app.server.fps.kyc110;

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
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * KYC110
 * 
 * @author Jimmy
 * @date 2016/07/19
 * @spec null
 */
@Component("kyc110")
@Scope("request")
public class KYC110 extends FubonWmsBizLogic {
	Logger logger = LoggerFactory.getLogger(this.getClass());

	public void queryData(Object body, IPrimitiveMap<Object> header) throws JBranchException{
		KYC110InputVO inputVO = (KYC110InputVO) body;
		KYC110OutputVO outputVO = new KYC110OutputVO();
		try {
			QueryConditionIF qc = getDataAccessManager().getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			List<Map<String,Object>> Main_list = new ArrayList<Map<String,Object>>(); 
			StringBuilder sb = new StringBuilder();
			sb.append(" SELECT QUESTION_VERSION, QUESTION_DESC, QUESTION_DESC_ENG, QUESTION_TYPE, ANS_OTHER_FLAG, ANS_MEMO_FLAG, ");
			sb.append(" STATUS, CREATOR, to_char(CREATETIME,'YYYY/MM/DD') as CREATETIME ");
			sb.append(" FROM TBSYS_QST_QUESTION WHERE MODULE_CATEGORY = 'KYC' ");
			if(!"".equals(inputVO.getQUESTION_DESC())){
				sb.append(" AND QUESTION_DESC LIKE :question");
				qc.setObject("question", "%"+inputVO.getQUESTION_DESC()+"%");
			}
			if(inputVO.getsDate() != null && inputVO.geteDate()== null){
				sb.append(" AND TRUNC(CREATETIME) >= :sDate ");
				qc.setObject("sDate", inputVO.getsDate());
			}
			if(inputVO.geteDate() != null && inputVO.getsDate()== null){
				sb.append(" AND TRUNC(CREATETIME) <= :eDate ");
				qc.setObject("eDate", inputVO.geteDate());
			}
			if(inputVO.getsDate() != null && inputVO.geteDate()!= null){
				sb.append(" AND TRUNC(CREATETIME) >= :sDate and TRUNC(CREATETIME) <= :eDate ");
				qc.setObject("sDate", inputVO.getsDate());
				qc.setObject("eDate", inputVO.geteDate());
			}
			sb.append(" ORDER BY CREATETIME DESC, QUESTION_VERSION DESC ");
			qc.setQueryString(sb.toString());
			Main_list = getDataAccessManager().exeQuery(qc);
			for(Map<String, Object> map:Main_list){
				sb = new StringBuilder();
				qc = getDataAccessManager().getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sb.append(" SELECT QUESTION_VERSION, ANSWER_DESC, ANSWER_DESC_ENG, ANSWER_SEQ FROM TBSYS_QST_ANSWER WHERE QUESTION_VERSION = :question_version ");
				qc.setObject("question_version", map.get("QUESTION_VERSION"));
				qc.setQueryString(sb.toString());
				map.put("Ans", getDataAccessManager().exeQuery(qc));
				
				sb = new StringBuilder();
				qc = getDataAccessManager().getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sb.append(" SELECT QUESTION_VERSION, ANSWER_DESC, ANSWER_DESC_ENG, ANSWER_SEQ FROM TBSYS_QST_ANSWER_COMP WHERE QUESTION_VERSION = :question_version ");
				qc.setObject("question_version", map.get("QUESTION_VERSION"));
				qc.setQueryString(sb.toString());
				map.put("AnsComp", getDataAccessManager().exeQuery(qc));
				
				List<Map<String,Object>> picture_list = new ArrayList<Map<String,Object>>(); 
				sb = new StringBuilder();
				qc = getDataAccessManager().getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sb.append("SELECT a.PICTURE,b.DOC_ID,b.DOC_NAME FROM TBSYS_QST_QUESTION a,TBSYS_FILE_MAIN b where a.PICTURE = b.DOC_ID and QUESTION_VERSION = :question_version ");
				qc.setObject("question_version", map.get("QUESTION_VERSION"));
				qc.setQueryString(sb.toString());
				picture_list = getDataAccessManager().exeQuery(qc);
				if(picture_list.size()>0){
					for(Map<String, Object> picture:picture_list){
						map.put("DOC_ID", picture.get("DOC_ID"));
						map.put("DOC_NAME", picture.get("DOC_NAME"));
						map.put("PICTURE", "Y");
					}
				}else{
					map.put("PICTURE", "N");
				}
			}
			outputVO.setQstQustionLst(Main_list);
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s", StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
		sendRtnObject(outputVO);
	}
	
	
	public void delete(Object body, IPrimitiveMap<?> header) throws JBranchException {
		KYC110InputVO inputVO = (KYC110InputVO) body;
		
		QueryConditionIF qc = getDataAccessManager().getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuilder sb = new StringBuilder();
		sb.append(" DELETE FROM TBSYS_QST_ANSWER WHERE QUESTION_VERSION = :question_version");
		qc.setObject("question_version", inputVO.getQUESTION_VERSION());
		qc.setQueryString(sb.toString());
		getDataAccessManager().exeUpdate(qc);
		
		qc = getDataAccessManager().getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sb = new StringBuilder();
		sb.append(" DELETE FROM TBSYS_QST_ANSWER_COMP WHERE QUESTION_VERSION = :question_version");
		qc.setObject("question_version", inputVO.getQUESTION_VERSION());
		qc.setQueryString(sb.toString());
		getDataAccessManager().exeUpdate(qc);
		
		qc = getDataAccessManager().getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sb = new StringBuilder();
		sb.append(" DELETE FROM TBSYS_QST_QUESTION WHERE QUESTION_VERSION = :question_version");
		qc.setObject("question_version", inputVO.getQUESTION_VERSION());
		qc.setQueryString(sb.toString());
		getDataAccessManager().exeUpdate(qc);
		sendRtnObject(null);
	}
	
}
