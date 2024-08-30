package com.systex.jbranch.app.server.fps.crm651;

import com.opensymphony.util.TextUtils;
import com.systex.jbranch.app.common.fps.table.TBCRM_CUST_DKYC_DATAVO;
import com.systex.jbranch.fubon.commons.cbs.CBSService;
import com.systex.jbranch.fubon.commons.cbs.service.FP032151Service;
import com.systex.jbranch.fubon.commons.esb.EsbUtil;
import com.systex.jbranch.fubon.commons.esb.vo.fp032151.FP032151OutputVO;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.serialnumber.SerialNumberUtil;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.util.IPrimitiveMap;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * crm511
 * 
 * @author moron
 * @date 2016/05/27
 * @spec null
 */
@Component("crm651")
@Scope("request")
public class CRM651 extends EsbUtil {

	@Autowired
	private CBSService cbsservice;

	@Autowired
	private FP032151Service fp032151Service;

	private DataAccessManager dam = null;

	public void fillKyc(Object body, IPrimitiveMap header) throws JBranchException {

		CRM651InputVO inputVO = (CRM651InputVO) body;
		CRM651OutputVO return_VO = new CRM651OutputVO();
		dam = this.getDataAccessManager();

		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("WITH BASE AS ( ");
		sql.append("  SELECT VIP_DEGREE, CON_DEGREE ");
		sql.append("  FROM TBCRM_CUST_MAST ");
		sql.append("  WHERE CUST_ID = :ID ");
		sql.append(") ");
		sql.append("SELECT a.*, b.ANS_ID, b.ANS_CONTENT ");
		sql.append("FROM TBCRM_DKYC_QSTN_SET a ");
		sql.append("LEFT JOIN TBCRM_DKYC_ANS_SET b ON a.QSTN_ID = b.QSTN_ID ");
		sql.append("WHERE ( ");
		sql.append("      case when a.vip_degree is null then 1 else INSTR(a.VIP_DEGREE, (SELECT NVL(VIP_DEGREE,'M') FROM BASE)) end > 0 ");
		sql.append("  AND case when a.aum_degree is null then 1 else INSTR(a.AUM_DEGREE, (SELECT CON_DEGREE FROM BASE)) end > 0");
		sql.append(") ");
		sql.append("AND ( ");
		sql.append("     (SYSDATE BETWEEN a.VALID_BGN_DATE and a.VALID_END_DATE) ");
		sql.append("  OR (SYSDATE >= a.VALID_BGN_DATE AND a.VALID_END_DATE IS NULL) ");
		sql.append("  OR (SYSDATE <= a.VALID_END_DATE AND a.VALID_BGN_DATE IS NULL) ");
		sql.append("  OR (a.VALID_BGN_DATE IS NULL AND a.VALID_END_DATE IS NULL) ");
		sql.append(") ");
		sql.append("ORDER BY a.DISPLAY_LAYER, a.DISPLAY_ORDER, a.QSTN_TYPE, a.QSTN_ID, b.DISPLAY_ORDER ");

		queryCondition.setQueryString(sql.toString());
		queryCondition.setObject("ID", inputVO.getCust_id());

		return_VO.setResultList(dam.exeQuery(queryCondition));

		this.sendRtnObject(return_VO);
	}

	public void kycConfirm(Object body, IPrimitiveMap header) throws Exception {

		CRM651InputVO inputVO = (CRM651InputVO) body;
		dam = this.getDataAccessManager();

		// del first
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		condition.setQueryString("delete TBCRM_CUST_DKYC_DATA where CUST_ID = :cust_id ");
		condition.setObject("cust_id", inputVO.getCust_id());
		dam.exeUpdate(condition);

		// TBCRM_CUST_DKYC_DATA
		for (Map<String, Object> map : inputVO.getData()) {
			TBCRM_CUST_DKYC_DATAVO vo = new TBCRM_CUST_DKYC_DATAVO();
			vo.setSEQ(new BigDecimal(getSN()));
			vo.setCUST_ID(inputVO.getCust_id());
			vo.setQSTN_ID((String) map.get("QSTN_ID"));

			if (map.get("set") instanceof List)
				vo.setANS_IDS(TextUtils.join(",", (List) map.get("set")));
			else
				vo.setANS_IDS((String) map.get("set"));

			vo.setCUST_ID(inputVO.getCust_id());
			vo.setANS_CONTENT(ObjectUtils.toString(map.get("ANS_CONTENT")));
			vo.setNOTE_CONTENT(ObjectUtils.toString(map.get("NOTE_CONTENT")));

			dam.create(vo);
		}

		this.sendRtnObject(null);
	}

	//SEQ序號
	private String getSN() throws JBranchException {

		SerialNumberUtil sn = new SerialNumberUtil();
		String seqNum = "";

		try {
			seqNum = sn.getNextSerialNumber("CRM651");
		} catch (Exception e) {
			sn.createNewSerial("CRM651", "0000000000", null, null, null, 1, new Long("9999999999"), "y", new Long("0"), null);
			seqNum = sn.getNextSerialNumber("CRM651");
		}

		return seqNum;
	}

	//客戶首頁上KYC ，僅顯示第一層問題
	public void queryFirstSubject(Object body, IPrimitiveMap header) throws Exception {

		CRM651InputVO inputVO = (CRM651InputVO) body;
		CRM651OutputVO return_VO = new CRM651OutputVO();
		dam = this.getDataAccessManager();

		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("WITH BASE AS ( ");
		sql.append("  SELECT VIP_DEGREE, CON_DEGREE ");
		sql.append("  FROM TBCRM_CUST_MAST ");
		sql.append("  WHERE CUST_ID = :cust_id");
		sql.append(") ");
		sql.append(", BASE2 AS ( ");
		sql.append("  SELECT b1.QSTN_ID, ");
		sql.append("         CASE WHEN listagg(b2.ANS_CONTENT, ',') within group (order by b2.ANS_CONTENT) is not null then listagg(b2.ANS_CONTENT, ',') within group (order by b2.ANS_CONTENT) else listagg(b1.ANS_CONTENT, ',') within group (order by b1.ANS_CONTENT) end as ANS_CONTENT ");
		sql.append("  FROM TBCRM_CUST_DKYC_DATA b1 ");
		sql.append("  LEFT JOIN TBCRM_DKYC_ANS_SET b2 ON b1.QSTN_ID = b2.QSTN_ID WHERE b1.CUST_ID = :cust_id AND b1.ANS_IDS like ('%' || b2.ANS_ID || '%') ");
		sql.append("  GROUP BY b1.QSTN_ID");
		sql.append(") ");
		sql.append("SELECT a.QSTN_ID, a.QSTN_CONTENT, b.ANS_CONTENT, a.QSTN_FORMAT ");
		sql.append("FROM TBCRM_DKYC_QSTN_SET a ");
		sql.append("LEFT JOIN ( ");
		sql.append("  SELECT a.QSTN_ID, case when b.ANS_CONTENT is null then a.ANS_CONTENT else b.ANS_CONTENT end as ANS_CONTENT ");
		sql.append("  FROM TBCRM_CUST_DKYC_DATA a ");
		sql.append("  LEFT JOIN BASE2 b on a.QSTN_ID = b.QSTN_ID ");
		sql.append("  WHERE a.CUST_ID = :cust_id ");
		sql.append(") b ON a.QSTN_ID = b.QSTN_ID ");
		sql.append("WHERE a.DISPLAY_LAYER = '1' ");
		sql.append("AND ( ");
		sql.append("      case when a.vip_degree is null then 1 else INSTR(a.VIP_DEGREE, (SELECT NVL(VIP_DEGREE,'M') FROM BASE) ) end > 0 ");
		sql.append("  AND case when a.aum_degree is null then 1 else INSTR(a.AUM_DEGREE, (SELECT CON_DEGREE FROM BASE) ) end > 0");
		sql.append(") ");
		sql.append("AND (");
		sql.append("     (SYSDATE BETWEEN a.VALID_BGN_DATE and a.VALID_END_DATE) ");
		sql.append("  OR (SYSDATE >= a.VALID_BGN_DATE AND a.VALID_END_DATE IS NULL) ");
		sql.append("  OR (SYSDATE <= a.VALID_END_DATE AND a.VALID_BGN_DATE IS NULL) ");
		sql.append("  OR (a.VALID_BGN_DATE IS NULL AND a.VALID_END_DATE IS NULL)");
		sql.append(") ");
		sql.append("ORDER BY a.DISPLAY_ORDER, a.QSTN_TYPE, a.QSTN_ID ");

		queryCondition.setObject("cust_id", inputVO.getCust_id());
		queryCondition.setQueryString(sql.toString());

		return_VO.setResultList(dam.exeQuery(queryCondition));

		//發送電文
		FP032151OutputVO fp032151OutputVO;
		if (inputVO.getData067050_067101_2() != null && inputVO.getData067050_067000() != null && inputVO.getData067050_067112() != null)
			fp032151OutputVO = fp032151Service.transfer(inputVO.getCust_id(), inputVO.getData067050_067101_2(), inputVO.getData067050_067000(), inputVO.getData067050_067112());
		else
			fp032151OutputVO = fp032151Service.search(inputVO.getCust_id());

		return_VO.setFp032151OutputVO(fp032151OutputVO);

		this.sendRtnObject(return_VO);
	}

	//第一層+第二層問題
	public void getKyc(Object body, IPrimitiveMap header) throws JBranchException {

		CRM651InputVO inputVO = (CRM651InputVO) body;
		CRM651OutputVO return_VO = new CRM651OutputVO();
		dam = this.getDataAccessManager();

		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("select a.*, b.QSTN_FORMAT ");
		sql.append("from TBCRM_CUST_DKYC_DATA a ");
		sql.append("left join TBCRM_DKYC_QSTN_SET b on a.QSTN_ID = b.QSTN_ID ");
		sql.append("where a.CUST_ID = :id ");

		queryCondition.setObject("id", inputVO.getCust_id());
		queryCondition.setQueryString(sql.toString());

		return_VO.setResultList(dam.exeQuery(queryCondition));

		this.sendRtnObject(return_VO);
	}

	public void getKycQ2Data(Object body, IPrimitiveMap header) throws Exception {

		CRM651InputVO inputVO = (CRM651InputVO) body;
		CRM651OutputVO outputVO = new CRM651OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		
		List<Map<String, Object>> personalIformation = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> questList = new ArrayList<Map<String, Object>>();

		//依類型找題目
		questList = queryQuestion(inputVO.getQUESTION_TYPE());

		queryCondition = getDataAccessManager().getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sb = new StringBuffer();
		sb.append("select a.EXPIRY_DATE, ");
		sb.append(" 	  a.SEQ, ");
		sb.append(" 	  a.CUST_NAME, ");
		sb.append(" 	  a.CUST_ID, ");
		sb.append(" 	  a.EXAM_VERSION, ");
		sb.append(" 	  a.CREATE_DATE as KYC_TEST_DATE, ");
		sb.append(" 	  c.GENDER,b.CUST_EDUCTION_AFTER as EDUCATION, ");
		sb.append(" 	  b.CUST_CAREER_AFTER as CAREER, ");
		sb.append(" 	  b.CUST_MARRIAGE_AFTER as MARRAGE, ");
		sb.append(" 	  b.CUST_CHILDREN_AFTER as CHILD_NO, ");
		sb.append(" 	  b.CUST_HEALTH_AFTER as SICK_TYPE, ");
		sb.append(" 	  b.ANSWER_2, ");
		sb.append(" 	  a.CUST_RISK_AFR, ");
		sb.append(" 	  c.BRA_NBR, ");
		sb.append(" 	  c.RPRS_NAME, ");
		sb.append(" 	  c.RPRS_ID, ");
		sb.append(" 	  b.CUST_TEL, ");
		sb.append(" 	  b.CUST_EMAIL as EMAIL_ADDR, ");
		sb.append(" 	  b.CUST_EMAIL_BEFORE as EMAIL_BEFORE, ");
		sb.append(" 	  b.CUST_ADDRESS as CUST_ADDR_1, ");
		sb.append(" 	  b.UPDATE_YN, a.REC_SEQ, ");
		sb.append(" 	  TRIM(b.CUST_SCHOOL) AS CUST_SCHOOL, ");
		sb.append(" 	  TRIM(b.CUST_EDU_CHANGE) AS CUST_EDU_CHANGE, ");
		sb.append(" 	  b.CUST_EMAIL_BEFORE, ");
		sb.append(" 	  b.SAMEEMAIL_REASON, ");
		sb.append(" 	  b.SAMEEMAIL_CHOOSE ");
		sb.append("from TBKYC_INVESTOREXAM_M_HIS a, TBKYC_INVESTOREXAM_D_HIS b, TBCRM_CUST_MAST c ");
		sb.append("where a.SEQ = b.SEQ ");
		sb.append("and a.CUST_ID = b.CUST_ID ");
		sb.append("and a.CUST_ID = c.CUST_ID ");
		sb.append("and a.CUST_ID = :cust_id ");
		sb.append("and (a.CUST_ID, a.EXPIRY_DATE, a.STATUS) IN ( ");
		sb.append("  SELECT CUST_ID, MAX(EXPIRY_DATE) AS EXPIRY_DATE, STATUS ");
		sb.append("  FROM TBKYC_INVESTOREXAM_M_HIS ");
		sb.append("  WHERE STATUS = '03' "); // 01:待審核 / 03:已生效 / 04:已刪除
		sb.append("  GROUP BY CUST_ID, STATUS ");
		sb.append(") ");
		sb.append("and trunc(a.CREATE_DATE) >= to_date('20220801', 'yyyymmdd') ");
		
		queryCondition.setObject("cust_id", inputVO.getCust_id().toUpperCase());
		
		queryCondition.setQueryString(sb.toString());
		
		personalIformation = getDataAccessManager().exeQueryWithoutSort(queryCondition);
		
		//若有該問卷的題目
		if (CollectionUtils.isNotEmpty(questList)) {
			for (int questIdx = 0; questIdx < questList.size(); questIdx++) {
				String exam_version = ObjectUtils.toString(questList.get(questIdx).get("EXAM_VERSION"));
				String question_version = ObjectUtils.toString(questList.get(questIdx).get("QUESTION_VERSION"));
				String rl_version = ObjectUtils.toString(questList.get(questIdx).get("RL_VERSION"));
				List<Map<String, Object>> ansMapList = queryAnswer(exam_version, question_version); //依照問卷版本及題目找出答案

				if (personalIformation.size() > 0) {
					//分割答案(依照題目來分割)
					String[] ansList = ObjectUtils.toString(personalIformation.get(0).get("ANSWER_2")).split(";");
					
					//如果題目的長度少於答案的長度
					if (questIdx < ansList.length && !"".equals(ansList[questIdx].toString())) {
						String[] ansList2 = ansList[questIdx].split(",");//切割複選題目
	
						for (Map<String, Object> map : ansMapList) {
							//將那題有選的設定為true
							for (String ans2 : ansList2) {
								if (map.get("ANSWER_SEQ").toString().equals(ans2.trim())) {
									map.put("select", true);
								}
							}
						}
					}
				}
				
				//設定該題的答案
				questList.get(questIdx).put("ANSWER_LIST", ansMapList);

				//依照風險級距版號取風險屬性設定
				queryCondition = getDataAccessManager().getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sb = new StringBuffer();
				sb.append("SELECT CUST_RL_ID, RL_NAME,RL_UP_RATE, PROD_RL_UP_RATE ");
				sb.append("FROM TBKYC_QUESTIONNAIRE_RISK_LEVEL N ");
				sb.append("WHERE N.RL_VERSION = :rl_version ");
				sb.append("order by RL_UP_RATE ");

				queryCondition.setObject("rl_version", rl_version);
				queryCondition.setQueryString(sb.toString());

				questList.get(questIdx).put("RISK_LIST", getDataAccessManager().exeQueryWithoutSort(queryCondition));
				
				if (personalIformation.size() > 0) {
					questList.get(questIdx).put("KYC_TEST_DATE", personalIformation.get(0).get("KYC_TEST_DATE"));
				}
			} 

			//將題目設定給輸出物件
			outputVO.setQuestionnaireList(questList);
		}
		
		
		this.sendRtnObject(outputVO);
	}

	//查詢題目
	public List<Map<String, Object>> queryQuestion(String question_type) throws Exception {

		List<Map<String, Object>> questList = new ArrayList<Map<String, Object>>();

		if (StringUtils.isNotBlank(question_type)) {
			QueryConditionIF queryCondition = getDataAccessManager().getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuilder sb = new StringBuilder();

			//問卷資料檔，題庫題目檔
			sb = new StringBuilder();
			sb.append("SELECT n.EXAM_VERSION, ");
			sb.append("       n.QUESTION_VERSION,");
			sb.append("       n.EXAM_NAME, ");
			sb.append("       n.QUEST_TYPE, ");
			sb.append("       n.QST_NO, ");
			sb.append("       n.ESSENTIAL_FLAG, ");
			sb.append("       n.RL_VERSION, ");
			sb.append("       n.STATUS, ");
			sb.append("       n.ACTIVE_DATE, ");
			sb.append("       n.SCORE_TYPE, ");
			sb.append("       n.INT_SCORE, ");
			sb.append("       n.RS_VERSION, ");
			sb.append("       n.RLR_VERSION, ");
			sb.append("       Q.QUESTION_DESC, ");
			sb.append("       Q.QUESTION_DESC_ENG, ");
			sb.append("       Q.QUESTION_TYPE, ");
			sb.append("       Q.ANS_OTHER_FLAG, ");
			sb.append("       Q.ANS_MEMO_FLAG, ");
			sb.append("       Q.DEFINITION, ");
			sb.append("       Q.PICTURE ");
			sb.append("from TBSYS_QUESTIONNAIRE n,TBSYS_QST_QUESTION Q ");
			sb.append("where n.STATUS = '02' ");
			sb.append("AND n.QUESTION_VERSION = Q.QUESTION_VERSION ");//待啟用
			sb.append("and n.QUEST_TYPE = :quest_type ");
			sb.append("AND (ACTIVE_DATE) = (");
			sb.append("  SELECT MAX(ACTIVE_DATE) ");
			sb.append("  FROM TBSYS_QUESTIONNAIRE ");
			sb.append("  WHERE STATUS = '02' ");
			sb.append("  AND QUEST_TYPE = :quest_type ");
			sb.append("  AND ACTIVE_DATE <= SYSDATE");
			sb.append(") ");
			sb.append("order by QST_NO "); //預計啟用日小於等於系統日

			queryCondition.setObject("quest_type", question_type);
			queryCondition.setQueryString(sb.toString());
			
			questList = getDataAccessManager().exeQueryWithoutSort(queryCondition);
		}

		return questList;
	}

	//答案
	public List<Map<String, Object>> queryAnswer(String exam_version, String question_version) throws Exception {

		List<Map<String, Object>> ansList = new ArrayList<Map<String, Object>>();

		if (StringUtils.isNotBlank(exam_version) && StringUtils.isNotBlank(question_version)) {
			QueryConditionIF queryCondition = getDataAccessManager().getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuilder sb = new StringBuilder();

			sb.append("SELECT A.QUESTION_VERSION, ");
			sb.append("       A.ANSWER_SEQ, ");
			sb.append("       A.ANSWER_DESC, ");
			sb.append("       A.ANSWER_DESC_ENG, ");
			sb.append("       W.FRACTION ");
			sb.append("FROM TBSYS_QST_ANSWER A,TBKYC_QUESTIONNAIRE_ANS_WEIGHT W ");//題目答案列表檔、答案權重檔
			sb.append("WHERE A.QUESTION_VERSION = W.QUESTION_VERSION  ");
			sb.append("and A.ANSWER_SEQ = W.ANSWER_SEQ ");
			sb.append("AND W.EXAM_VERSION = :exam_version ");
			sb.append("AND A.QUESTION_VERSION = :question_version ");

			queryCondition.setObject("exam_version", exam_version);
			queryCondition.setObject("question_version", question_version);

			queryCondition.setQueryString(sb.toString());
			
			ansList = getDataAccessManager().exeQueryWithoutSort(queryCondition);
		}

		return ansList;
	}
}