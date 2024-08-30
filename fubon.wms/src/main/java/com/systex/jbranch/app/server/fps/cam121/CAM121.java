package com.systex.jbranch.app.server.fps.cam121;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.common.fps.table.TBSYS_QST_ANSWERPK;
import com.systex.jbranch.app.common.fps.table.TBSYS_QST_ANSWERVO;
import com.systex.jbranch.app.common.fps.table.TBSYS_QST_QUESTIONVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataManager.DataManager;
import com.systex.jbranch.platform.common.dataManager.WorkStation;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * 
 * @author Ocean
 * @date 2016/06/14
 * @spec 
 */
@Component("cam121")
@Scope("request")
public class CAM121 extends FubonWmsBizLogic {
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(CAM121.class);
	
	SimpleDateFormat SDFYYYYMMDD = new SimpleDateFormat("yyyyMMdd");
	SimpleDateFormat SDFYYYYMMDDHHMMSS = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
	
	public void query (Object body, IPrimitiveMap header) throws JBranchException {
		
		CAM121InputVO inputVO = (CAM121InputVO) body;
		CAM121OutputVO outputVO = new CAM121OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		StringBuffer sb = new StringBuffer();
		// 為了ORDER BY QUESTION_VERSION 後，能拿到正確的ROWNUM，所以多一層SELECT
		sb.append("SELECT ROWNUM, QUESTION_VERSION, QUESTION_DESC, QUESTION_TYPE, ANS_OTHER_FLAG, ANS_MEMO_FLAG, STATUS, CREATOR, CREATETIME ");
		sb.append("FROM ( ");
		sb.append("SELECT QUESTION_VERSION, QUESTION_DESC, QUESTION_TYPE, ANS_OTHER_FLAG, ANS_MEMO_FLAG, STATUS, CREATOR, CREATETIME ");
		sb.append("FROM TBSYS_QST_QUESTION ");
		sb.append("WHERE 1=1 ");
		sb.append("AND MODULE_CATEGORY = 'CAM' ");
		
		if (StringUtils.isNotBlank(inputVO.getQuestionVersion()))
			sb.append("AND QUESTION_VERSION = :questionVersion ");
		if (StringUtils.isNotBlank(inputVO.getQuestionDesc()))
			sb.append("AND QUESTION_DESC LIKE :questionDesc ");
		if (null != inputVO.getsDate())
			sb.append("AND TO_CHAR(CREATETIME, 'yyyyMMdd') >= TO_CHAR(:sDate, 'yyyyMMdd') ");
		if (null != inputVO.geteDate())
			sb.append("AND TO_CHAR(CREATETIME, 'yyyyMMdd') <= TO_CHAR(:eDate, 'yyyyMMdd') ");
//		if (StringUtils.isNotBlank(inputVO.getModuleCategory()))
//			sb.append("AND MODULE_CATEGORY = :moduleCategory ");
		
		sb.append("ORDER BY QUESTION_VERSION");
		sb.append(") ");
		
		queryCondition.setQueryString(sb.toString());
		
		if (StringUtils.isNotBlank(inputVO.getQuestionVersion()))
			queryCondition.setObject("questionVersion", inputVO.getQuestionVersion());
		if (StringUtils.isNotBlank(inputVO.getQuestionDesc()))
			queryCondition.setObject("questionDesc", "%" + inputVO.getQuestionDesc() + "%");
		if (null != inputVO.getsDate())
			queryCondition.setObject("sDate", inputVO.getsDate());
		if (null != inputVO.geteDate())
			queryCondition.setObject("eDate", inputVO.geteDate());
		if (StringUtils.isNotBlank(inputVO.getModuleCategory()))
			queryCondition.setObject("moduleCategory", inputVO.getModuleCategory());
		
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		for (Map<String, Object> map : list) {
			
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			
			sb = new StringBuffer();
			sb.append("SELECT QUESTION_VERSION, ANSWER_SEQ, ANSWER_DESC ");
			sb.append("FROM TBSYS_QST_ANSWER ");
			sb.append("WHERE QUESTION_VERSION = :questionVersion ");
			sb.append("AND ANSWER_DESC <> '其他' ");
			sb.append("ORDER BY QUESTION_VERSION, ANSWER_SEQ ");
			
			queryCondition.setQueryString(sb.toString());
			queryCondition.setObject("questionVersion", map.get("QUESTION_VERSION"));
			
			List<Map<String, Object>> tempList = dam.exeQuery(queryCondition);
			String str = "";
			for (Map<String, Object> tMap : tempList) {
				str = str + tMap.get("ANSWER_DESC") + " \n";
			}

			map.put("ANSWER_DESC", str);
		}
		
		outputVO.setQuestionList(list);
		
		this.sendRtnObject(outputVO);
	}
	
	public void getAnswerList (Object body, IPrimitiveMap header) throws JBranchException {
		
		CAM121InputVO inputVO = (CAM121InputVO) body;
		CAM121OutputVO outputVO = new CAM121OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT ANS.QUESTION_VERSION, ANS.ANSWER_SEQ, ANS.ANSWER_DESC ");
		sb.append("FROM TBSYS_QST_ANSWER ANS ");
		sb.append("WHERE ANS.QUESTION_VERSION = :questionVersion ");
		sb.append("ORDER BY ANS.QUESTION_VERSION, ANS.ANSWER_SEQ ");
		
		queryCondition.setQueryString(sb.toString());
		queryCondition.setObject("questionVersion",inputVO.getQuestionVersion());

		outputVO.setAnswerList(dam.exeQuery(queryCondition));
		
		this.sendRtnObject(outputVO);
	}

	public void addQuestion (Object body, IPrimitiveMap header) throws JBranchException {
		
		CAM121InputVO inputVO = (CAM121InputVO) body;
		CAM121OutputVO outputVO = new CAM121OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		TBSYS_QST_QUESTIONVO vo = new TBSYS_QST_QUESTIONVO();
		vo.setQUESTION_VERSION(inputVO.getQuestionVersion());
		vo.setQUESTION_DESC(inputVO.getQuestionDesc());
		vo.setMODULE_CATEGORY("CAM");
		vo.setQUESTION_TYPE(inputVO.getQuestionType());
		vo.setANS_OTHER_FLAG((inputVO.getAnsOtherFlag() ? "Y" : "N"));
		vo.setANS_MEMO_FLAG((inputVO.getAnsMemoFlag() ? "Y" : "N"));
		vo.setSTATUS("N");
		
		dam.create(vo);
		
		if (("T".equals(inputVO.getQuestionType()) || "N".equals(inputVO.getQuestionType())) ||
			(("S".equals(inputVO.getQuestionType()) || "M".equals(inputVO.getQuestionType())) && inputVO.getAnsOtherFlag())) {
			TBSYS_QST_ANSWERVO aVO = new TBSYS_QST_ANSWERVO();
			TBSYS_QST_ANSWERPK aPK = new TBSYS_QST_ANSWERPK();
			aPK.setQUESTION_VERSION(inputVO.getQuestionVersion());
			aPK.setANSWER_SEQ(getAnswerSEQ(dam, queryCondition, inputVO.getQuestionVersion()));
			aVO.setcomp_id(aPK);
			if ((("S".equals(inputVO.getQuestionType()) || "M".equals(inputVO.getQuestionType())) && inputVO.getAnsOtherFlag())) {
				aVO.setANSWER_DESC("其他");
			} else {
				aVO.setANSWER_DESC(inputVO.getAnswerDesc());
			}
			
			dam.create(aVO);
		}
		
		this.sendRtnObject(null);
	}
	
	public void updQuestion (Object body, IPrimitiveMap header) throws JBranchException {
		
		WorkStation ws = DataManager.getWorkStation(uuid);
		CAM121InputVO inputVO = (CAM121InputVO) body;
		CAM121OutputVO outputVO = new CAM121OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		TBSYS_QST_QUESTIONVO vo = new TBSYS_QST_QUESTIONVO();
		vo = (TBSYS_QST_QUESTIONVO) dam.findByPKey(TBSYS_QST_QUESTIONVO.TABLE_UID, inputVO.getQuestionVersion());
		
		//改題問類型：單選複選變問答題
		if (("S".equals(vo.getQUESTION_TYPE()) || "M".equals(vo.getQUESTION_TYPE())) && 
			("T".equals(inputVO.getQuestionType()) || "N".equals(inputVO.getQuestionType()))){
			// 刪除所有可選擇項目
			StringBuffer sb = new StringBuffer();
			sb.append("DELETE FROM TBSYS_QST_ANSWER WHERE QUESTION_VERSION = :questionVersion");
			queryCondition.setQueryString(sb.toString());
			queryCondition.setObject("questionVersion",inputVO.getQuestionVersion());
			
			dam.exeUpdate(queryCondition);
			
			// 新增可供輸入答案
			TBSYS_QST_ANSWERVO aVO = new TBSYS_QST_ANSWERVO();
			TBSYS_QST_ANSWERPK aPK = new TBSYS_QST_ANSWERPK();
			aPK.setQUESTION_VERSION(inputVO.getQuestionVersion());
			aPK.setANSWER_SEQ(getAnswerSEQ(dam, queryCondition, inputVO.getQuestionVersion()));
			aVO.setcomp_id(aPK);
			aVO.setANSWER_DESC(inputVO.getAnswerDesc());
			
			dam.create(aVO);
			
			vo.setANS_OTHER_FLAG("N");
			vo.setANS_MEMO_FLAG("N");
		} else if (("T".equals(vo.getQUESTION_TYPE()) || "N".equals(vo.getQUESTION_TYPE())) && 
				   ("S".equals(inputVO.getQuestionType()) || "M".equals(inputVO.getQuestionType()))){
			StringBuffer sb = new StringBuffer();
			sb.append("DELETE FROM TBSYS_QST_ANSWER WHERE QUESTION_VERSION = :questionVersion AND ANSWER_DESC IS NULL");
			queryCondition.setQueryString(sb.toString());
			queryCondition.setObject("questionVersion",inputVO.getQuestionVersion());
			
			dam.exeUpdate(queryCondition);
			
			vo.setANS_OTHER_FLAG((inputVO.getAnsOtherFlag() ? "Y" : "N"));
			vo.setANS_MEMO_FLAG((inputVO.getAnsMemoFlag() ? "Y" : "N"));
		} else {
			vo.setANS_OTHER_FLAG((inputVO.getAnsOtherFlag() ? "Y" : "N"));
			vo.setANS_MEMO_FLAG((inputVO.getAnsMemoFlag() ? "Y" : "N"));
		}
		
		vo.setQUESTION_DESC(inputVO.getQuestionDesc());
		vo.setQUESTION_TYPE(inputVO.getQuestionType());
		vo.setModifier(ws.getUser().getUserID());
		vo.setLastupdate(new Timestamp(System.currentTimeMillis()));

		dam.update(vo);
		
		//是否有其他項目
		if (!inputVO.getAnsOtherFlag()) {
			StringBuffer sb = new StringBuffer();
			sb.append("DELETE FROM TBSYS_QST_ANSWER WHERE QUESTION_VERSION = :questionVersion AND ANSWER_DESC = '其他'");
			queryCondition.setQueryString(sb.toString());
			queryCondition.setObject("questionVersion",inputVO.getQuestionVersion());
			
			dam.exeUpdate(queryCondition);
		} else {
			StringBuffer sb = new StringBuffer();
			sb.append("SELECT COUNT(*) AS COUNTS ");
			sb.append("FROM TBSYS_QST_ANSWER ");
			sb.append("WHERE QUESTION_VERSION = :questionVersion ");
			sb.append("AND ANSWER_DESC = '其他'");
			queryCondition.setQueryString(sb.toString());
			queryCondition.setObject("questionVersion",inputVO.getQuestionVersion());
			
			List<Map<String, Object>> list = dam.exeQuery(queryCondition);
			if (((BigDecimal) list.get(0).get("COUNTS")).compareTo(new BigDecimal(0)) == 0) {
				TBSYS_QST_ANSWERVO aVO = new TBSYS_QST_ANSWERVO();
				TBSYS_QST_ANSWERPK aPK = new TBSYS_QST_ANSWERPK();
				aPK.setQUESTION_VERSION(inputVO.getQuestionVersion());
				aPK.setANSWER_SEQ(getAnswerSEQ(dam, queryCondition, inputVO.getQuestionVersion()));
				aVO.setcomp_id(aPK);
				aVO.setANSWER_DESC("其他");
				
				dam.create(aVO);
			}
		}
		
		this.sendRtnObject(null);
	}
	
	public void addAnswer (Object body, IPrimitiveMap header) throws JBranchException {
		
		CAM121InputVO inputVO = (CAM121InputVO) body;
		CAM121OutputVO outputVO = new CAM121OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		TBSYS_QST_ANSWERVO vo = new TBSYS_QST_ANSWERVO();
		TBSYS_QST_ANSWERPK pk = new TBSYS_QST_ANSWERPK();
		pk.setQUESTION_VERSION(inputVO.getQuestionVersion());
		pk.setANSWER_SEQ(getAnswerSEQ(dam, queryCondition, inputVO.getQuestionVersion()));
		vo.setcomp_id(pk);
		vo.setANSWER_DESC(inputVO.getAnswerDesc());
		
		dam.create(vo);
		
		this.sendRtnObject(null);
	}
	
	public BigDecimal getAnswerSEQ (DataAccessManager dam, QueryConditionIF queryCondition, String questionVersion) throws JBranchException {
		
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT QUESTION_VERSION, MAX(ANSWER_SEQ) AS MAX_ANSWER_SEQ ");
		sb.append("FROM TBSYS_QST_ANSWER ");
		sb.append("WHERE QUESTION_VERSION = :questionVersion ");
		sb.append("GROUP BY QUESTION_VERSION ");
		
		queryCondition.setQueryString(sb.toString());
		queryCondition.setObject("questionVersion", questionVersion);
		
		List<Map<String, Object>> temp = dam.exeQuery(queryCondition);
		
		BigDecimal answerSEQ = new BigDecimal(0);
		if (temp.size() > 0) {
			answerSEQ = ((BigDecimal) temp.get(0).get("MAX_ANSWER_SEQ")).add(new BigDecimal(1));
		}
		
		return answerSEQ;
	}
	
	public void delAnswer (Object body, IPrimitiveMap header) throws JBranchException {
		
		CAM121InputVO inputVO = (CAM121InputVO) body;
		CAM121OutputVO outputVO = new CAM121OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		if (StringUtils.isBlank(inputVO.getAnswerSEQ())) {
			StringBuffer sb = new StringBuffer();
			sb.append("DELETE FROM TBSYS_QST_ANSWER WHERE QUESTION_VERSION = :questionVersion");
			queryCondition.setQueryString(sb.toString());
			queryCondition.setObject("questionVersion",inputVO.getQuestionVersion());
			
			dam.exeUpdate(queryCondition);
		} else {
			TBSYS_QST_ANSWERVO vo = new TBSYS_QST_ANSWERVO();
			TBSYS_QST_ANSWERPK pk = new TBSYS_QST_ANSWERPK();
			pk.setQUESTION_VERSION(inputVO.getQuestionVersion());
			pk.setANSWER_SEQ(BigDecimal.valueOf(Integer.valueOf(inputVO.getAnswerSEQ())));
			vo.setcomp_id(pk);
			vo = (TBSYS_QST_ANSWERVO) dam.findByPKey(TBSYS_QST_ANSWERVO.TABLE_UID, vo.getcomp_id());

			dam.delete(vo);
		}
		
		this.sendRtnObject(null);
	}
	
	public void delQandA (Object body, IPrimitiveMap header) throws JBranchException {
		
		CAM121InputVO inputVO = (CAM121InputVO) body;
		CAM121OutputVO outputVO = new CAM121OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		TBSYS_QST_QUESTIONVO qVO = new TBSYS_QST_QUESTIONVO();
		qVO = (TBSYS_QST_QUESTIONVO) dam.findByPKey(TBSYS_QST_QUESTIONVO.TABLE_UID, inputVO.getQuestionVersion());
		dam.delete(qVO);

		StringBuffer sb = new StringBuffer();
		sb.append("DELETE FROM TBSYS_QST_ANSWER WHERE QUESTION_VERSION = :questionVersion");
		queryCondition.setQueryString(sb.toString());
		queryCondition.setObject("questionVersion",inputVO.getQuestionVersion());
		dam.exeUpdate(queryCondition);

		this.sendRtnObject(null);
	}
	
	public void checkQusExist (Object body, IPrimitiveMap header) throws JBranchException {
		
		CAM121InputVO inputVO = (CAM121InputVO) body;
		CAM121OutputVO outputVO = new CAM121OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		TBSYS_QST_QUESTIONVO qVO = new TBSYS_QST_QUESTIONVO();
		qVO = (TBSYS_QST_QUESTIONVO) dam.findByPKey(TBSYS_QST_QUESTIONVO.TABLE_UID, inputVO.getQuestionVersion());
		
		if (null == qVO) {
			StringBuffer sb = new StringBuffer();
			sb.append("DELETE FROM TBSYS_QST_ANSWER WHERE QUESTION_VERSION = :questionVersion");
			queryCondition.setQueryString(sb.toString());
			queryCondition.setObject("questionVersion",inputVO.getQuestionVersion());
			dam.exeUpdate(queryCondition);
		}
		
		this.sendRtnObject(null);
	}

	public void initial (Object body, IPrimitiveMap header) throws JBranchException {
		CAM121OutputVO outputVO = new CAM121OutputVO();
		dam = this.getDataAccessManager();
		
		// 序號命名規則：系統日字串(YYYYMMDD)+下一序號(0000~9999)
		outputVO.setQuestionVersion(new SimpleDateFormat("yyyyMMdd").format(new Date()) + StringUtils.leftPad(getSeqNum(), 4, "0"));
		
		sendRtnObject(outputVO);
	}
	
	private String getSeqNum() throws JBranchException {
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT SQ_SYS_QST_QUESTION.nextval AS SEQ FROM DUAL ");
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> SEQLIST = dam.exeQuery(queryCondition);
		
		return ObjectUtils.toString(SEQLIST.get(0).get("SEQ"));
	}
	
}