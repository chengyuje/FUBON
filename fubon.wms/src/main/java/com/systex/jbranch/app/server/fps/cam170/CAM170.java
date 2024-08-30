package com.systex.jbranch.app.server.fps.cam170;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataManager.DataManager;
import com.systex.jbranch.platform.common.dataManager.WorkStation;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.query.ResultIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.CSVUtil;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * 
 * @author Ocean
 * @date 2016/06/20
 * @spec
 */
@Component("cam170")
@Scope("request")
public class CAM170 extends FubonWmsBizLogic {
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(CAM170.class);

	SimpleDateFormat SDFYYYYMMDD = new SimpleDateFormat("yyyyMMdd");
	SimpleDateFormat SDFYYYYMMDDHHMMSS = new SimpleDateFormat("yyyyMMdd HH:mm:ss");

	public void query(Object body, IPrimitiveMap header) throws JBranchException {
		
		XmlInfo xmlInfo = new XmlInfo();

		CAM170InputVO inputVO = (CAM170InputVO) body;
		CAM170OutputVO outputVO = new CAM170OutputVO();
		dam = this.getDataAccessManager();

		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT CAMP.EXAM_ID, CAMP.CAMPAIGN_ID, CAMP.CAMPAIGN_NAME, CAMP.CAMPAIGN_DESC, CAMP.START_DATE, CAMP.END_DATE, CAMP.CREATETIME ");
		sb.append("FROM TBCAM_SFA_CAMPAIGN CAMP ");
		sb.append("WHERE LEAD_PARA1 = 'Y' ");
		sb.append("AND EXAM_ID IS NOT NULL ");

		// 依系統角色決定下拉選單可視範圍
		if (xmlInfo.doGetVariable("FUBONSYS.BMMGR_ROLE", FormatHelper.FORMAT_2).containsKey(getUserVariable(FubonSystemVariableConsts.LOGINROLE))) { // 分行主管
			sb.append("AND EXISTS ( ");
			sb.append("  SELECT DISTINCT T.CAMPAIGN_ID, T.STEP_ID ");
			sb.append("  FROM TBCAM_SFA_LEADS T ");
			sb.append("  WHERE T.BRANCH_ID IN (:brIdList) ");
			sb.append("  AND CAMP.CAMPAIGN_ID = T.CAMPAIGN_ID ");
			sb.append("  AND CAMP.STEP_ID = T.STEP_ID ");
			sb.append(") ");
			
			queryCondition.setObject("brIdList", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
		} else { // 總行人員

		}

		// where 排版 2016/12/21
		if (!StringUtils.isBlank(inputVO.getCampID())) {
			sb.append("AND CAMPAIGN_ID like :campID ");
			queryCondition.setObject("campID", "%" + inputVO.getCampID() + "%");
		}
		if (!StringUtils.isBlank(inputVO.getCampName())) {
			sb.append("AND CAMPAIGN_NAME like :campName ");
			queryCondition.setObject("campName", "%" + inputVO.getCampName() + "%");
		}
		if (null != inputVO.getImportSDate()) {
			sb.append("AND TO_CHAR(CREATETIME, 'yyyyMMdd') >= TO_CHAR(:importSDate, 'yyyyMMdd') ");
			queryCondition.setObject("importSDate", inputVO.getImportSDate());
		}
		if (null != inputVO.getImportEDate()) {
			sb.append("AND TO_CHAR(CREATETIME, 'yyyyMMdd') <= TO_CHAR(:importEDate, 'yyyyMMdd') ");
			queryCondition.setObject("importEDate", inputVO.getImportEDate());
		}
		if (null != inputVO.getsDate()) {
			sb.append("AND TO_CHAR(START_DATE, 'yyyyMMdd') = TO_CHAR(:sDate, 'yyyyMMdd') ");
			queryCondition.setObject("sDate", inputVO.getsDate());
		}
		if (null != inputVO.geteDate()) {
			sb.append("AND END_DATE >= :eDate ");
			queryCondition.setObject("eDate", inputVO.geteDate());
		}
		if (null != inputVO.geteDate2()) {
			sb.append("AND END_DATE <= :eDate2 ");
			queryCondition.setObject("eDate2", inputVO.geteDate2());
		}
		sb.append("ORDER BY CREATETIME ");
		queryCondition.setQueryString(sb.toString());
		ResultIF list = dam.executePaging(queryCondition, inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());
		int totalPage_i = list.getTotalPage();
		int totalRecord_i = list.getTotalRecord();
		outputVO.setCampaignList(list);
		outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());// 當前頁次
		outputVO.setTotalPage(totalPage_i);// 總頁次
		outputVO.setTotalRecord(totalRecord_i);// 總筆數
		this.sendRtnObject(outputVO);
	}

	public void getQstListByExamID(Object body, IPrimitiveMap header) throws JBranchException {

		CAM170InputVO inputVO = (CAM170InputVO) body;
		CAM170OutputVO outputVO = new CAM170OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		StringBuffer sb = new StringBuffer();
		sb.append("SELECT QN.EXAM_VERSION, QN.EXAM_NAME, QN.QST_NO, QN.QUESTION_VERSION, QST.QUESTION_DESC, QST.QUESTION_TYPE, QST.ANS_OTHER_FLAG, QST.ANS_MEMO_FLAG ");
		sb.append("FROM TBSYS_QUESTIONNAIRE QN ");
		sb.append("LEFT JOIN TBSYS_QST_QUESTION QST ON QST.QUESTION_VERSION = QN.QUESTION_VERSION ");
		sb.append("WHERE EXAM_VERSION = :examVersion ");
		sb.append("ORDER BY QST_NO ");

		queryCondition.setQueryString(sb.toString());

		queryCondition.setObject("examVersion", inputVO.getExamVersion());

		outputVO.setQuestionList(dam.exeQuery(queryCondition));

		this.sendRtnObject(outputVO);
	}

	public void getAnsList(Object body, IPrimitiveMap header) throws JBranchException {

		CAM170InputVO inputVO = (CAM170InputVO) body;
		CAM170OutputVO outputVO = new CAM170OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		StringBuffer sb = new StringBuffer();
		sb.append("SELECT ANS.QUESTION_VERSION, ANS.ANSWER_SEQ, ANS.ANSWER_DESC, ");
		sb.append("(SELECT COUNT(*) ");
		sb.append("FROM TBCAM_EXAMRECORD_DETAIL ");
		sb.append("WHERE RECORD_SEQ IN (SELECT RECORD_SEQ FROM TBCAM_EXAMRECORD WHERE EXAM_VERSION = :examVersion) ");
		sb.append("AND QUESTION_VERSION = ANS.QUESTION_VERSION ");
		sb.append("AND ANSWER_SEQ = ANS.ANSWER_SEQ) AS COUNTS ");
		sb.append("FROM TBSYS_QST_ANSWER ANS ");
		sb.append("WHERE ANS.QUESTION_VERSION = :questionVersion ");

		queryCondition.setQueryString(sb.toString());

		queryCondition.setObject("examVersion", inputVO.getExamVersion());
		queryCondition.setObject("questionVersion", inputVO.getQuestionVersion());

		outputVO.setAnswerList(dam.exeQuery(queryCondition));

		this.sendRtnObject(outputVO);
	}

	public void getAnsOthersList(Object body, IPrimitiveMap header) throws JBranchException {

		CAM170InputVO inputVO = (CAM170InputVO) body;
		CAM170OutputVO outputVO = new CAM170OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		StringBuffer sb = new StringBuffer();
		sb.append("SELECT EXAM.EXAM_VERSION, EXAM.RECORD_SEQ, DTL.QUESTION_VERSION, Q.QUESTION_DESC, DTL.ANSWER_SEQ, DTL.REMARK ");
		sb.append("FROM TBCAM_EXAMRECORD EXAM, TBCAM_EXAMRECORD_DETAIL DTL ");
		sb.append("LEFT JOIN TBSYS_QST_QUESTION Q ON DTL.QUESTION_VERSION = Q.QUESTION_VERSION ");
		sb.append("WHERE EXAM.RECORD_SEQ = DTL.RECORD_SEQ ");
		sb.append("AND EXAM_VERSION = :examVersion ");
		sb.append("AND DTL.QUESTION_VERSION = :questionVersion ");

		queryCondition.setQueryString(sb.toString());

		queryCondition.setObject("examVersion", inputVO.getExamVersion());
		queryCondition.setObject("questionVersion", inputVO.getQuestionVersion());

		outputVO.setAnswerList(dam.exeQuery(queryCondition));

		this.sendRtnObject(outputVO);
	}

	public void getOthersAnswer(Object body, IPrimitiveMap header) throws JBranchException {

		CAM170InputVO inputVO = (CAM170InputVO) body;
		CAM170OutputVO outputVO = new CAM170OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		StringBuffer sb = new StringBuffer();
		sb.append("SELECT REMARK ");
		sb.append("FROM TBCAM_EXAMRECORD_DETAIL ");
		sb.append("WHERE QUESTION_VERSION = :questionVersion ");
		sb.append("AND ANSWER_SEQ = :answerSEQ ");
		sb.append("AND RECORD_SEQ IN (SELECT RECORD_SEQ FROM TBCAM_EXAMRECORD WHERE EXAM_VERSION = :examVersion) ");
		sb.append("AND REMARK IS NOT NULL ");

		queryCondition.setQueryString(sb.toString());

		queryCondition.setObject("examVersion", inputVO.getExamVersion());
		queryCondition.setObject("questionVersion", inputVO.getQuestionVersion());
		queryCondition.setObject("answerSEQ", inputVO.getAnswerSEQ());

		outputVO.setOtherAnswerList(dam.exeQuery(queryCondition));

		this.sendRtnObject(outputVO);
	}

	public void export(Object body, IPrimitiveMap header) throws JBranchException {

		XmlInfo xmlInfo = new XmlInfo();

		WorkStation ws = DataManager.getWorkStation(uuid);
		CAM170InputVO inputVO = (CAM170InputVO) body;
		CAM170OutputVO outputVO = new CAM170OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		StringBuffer sb = new StringBuffer();
		sb.append("SELECT LEADS.CAMPAIGN_NAME, LEADS.BRANCH_NAME, LEADS.CUST_ID, CM.CUST_NAME, QUS.QST_NO, ");
		sb.append("       QUS.QUESTION_DESC, ");
		sb.append("       CASE WHEN ANS.ANSWER_DESC = '其他' THEN '其他-' || BASE.REMARK WHEN ANS.ANSWER_DESC IS NULL THEN BASE.REMARK ELSE ANS.ANSWER_DESC END AS ANSWER_DESC, ");
		sb.append("       LEADS.AO_CODE, LEADS.EMP_NAME, BASE.LASTUPDATE ");
		sb.append("FROM ( ");
		sb.append("  SELECT CAMP.CAMPAIGN_ID, CAMP.STEP_ID, CAMP.CAMPAIGN_NAME, CAMP.EXAM_ID, LEADS.CUST_ID, ");
		sb.append("         CASE WHEN LENGTH(LEADS.AO_CODE) = 3 THEN LEADS.AO_CODE ELSE NULL END AS AO_CODE, LEADS.EMP_ID, INFO.EMP_NAME, DEFN.DEPT_ID AS BRANCH_NBR, DEFN.DEPT_NAME AS BRANCH_NAME ");
		sb.append("  FROM TBCAM_SFA_CAMPAIGN CAMP, TBCAM_SFA_LEADS LEADS ");
		sb.append("  LEFT JOIN TBORG_MEMBER INFO ON LEADS.EMP_ID = INFO.EMP_ID ");
		sb.append("  LEFT JOIN TBORG_DEFN DEFN ON DEFN.DEPT_ID = LEADS.BRANCH_ID ");
		sb.append("  WHERE CAMP.CAMPAIGN_ID = LEADS.CAMPAIGN_ID ");
		sb.append("  AND CAMP.STEP_ID = LEADS.STEP_ID ");
		sb.append("  AND INSTR(LEADS.SFA_LEAD_ID, '_S', -1) = 0 ");
		sb.append("  AND CAMP.EXAM_ID = :examVersion ");
		sb.append(") LEADS ");
		sb.append("LEFT JOIN TBCRM_CUST_MAST CM ON CM.CUST_ID = LEADS.CUST_ID ");
		sb.append("LEFT JOIN ( ");
		sb.append("  SELECT Q.EXAM_VERSION, Q.QUESTION_VERSION, QUES.QUESTION_DESC, Q.QST_NO ");
		sb.append("  FROM TBSYS_QUESTIONNAIRE Q ");
		sb.append("  LEFT JOIN TBSYS_QST_QUESTION QUES ON Q.QUESTION_VERSION = QUES.QUESTION_VERSION ");
		sb.append("  WHERE Q.EXAM_VERSION = :examVersion ");
		sb.append(") QUS ON 1 = 1 ");
		sb.append("LEFT JOIN ( ");
		sb.append("  SELECT EXAM.EXAM_VERSION, EXAM.CUST_ID, EXAM.CUST_NAME, DTL.QUESTION_VERSION, DTL.ANSWER_SEQ, DTL.REMARK, TO_CHAR(DTL.LASTUPDATE, 'yyyy-MM-dd HH24:mi:ss') AS LASTUPDATE ");
		sb.append("  FROM TBCAM_EXAMRECORD EXAM, TBCAM_EXAMRECORD_DETAIL DTL ");
		sb.append("  WHERE EXAM.RECORD_SEQ = DTL.RECORD_SEQ ");
		sb.append("  AND EXAM.EXAM_VERSION = :examVersion ");
		sb.append(") BASE ON LEADS.EXAM_ID = BASE.EXAM_VERSION AND LEADS.CUST_ID = BASE.CUST_ID AND BASE.QUESTION_VERSION = QUS.QUESTION_VERSION ");
		sb.append("LEFT JOIN TBSYS_QST_ANSWER ANS ON BASE.ANSWER_SEQ = ANS.ANSWER_SEQ AND BASE.QUESTION_VERSION = ANS.QUESTION_VERSION ");
		sb.append("WHERE LEADS.EXAM_ID = :examVersion ");
		// 依系統角色決定下拉選單可視範圍
		if (xmlInfo.doGetVariable("FUBONSYS.BMMGR_ROLE", FormatHelper.FORMAT_2).containsKey(getUserVariable(FubonSystemVariableConsts.LOGINROLE))) { // 分行主管
			sb.append("AND LEADS.BRANCH_NBR IN (:brIdList) ");
			
			queryCondition.setObject("brIdList", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
		} else { // 總行人員

		}
		sb.append("ORDER BY LEADS.EXAM_ID, LEADS.BRANCH_NBR, LEADS.CUST_ID, QUS.QST_NO, BASE.ANSWER_SEQ ");
		//		sb.append("SELECT LEADS.CAMPAIGN_NAME, LEADS.DEPT_NAME, BASE.CUST_ID, BASE.CUST_NAME, BASE.QUESTION_DESC, BASE.ANSWER_DESC, LEADS.AO_CODE, LEADS.EMP_NAME ");
		//		sb.append("FROM (SELECT EXAM.EXAM_VERSION, EXAM.CUST_ID, EXAM.CUST_NAME, DTL.QUESTION_VERSION, DTL.ANSWER_SEQ, DTL.REMARK, QUS.QUESTION_DESC, ");
		//		sb.append("CASE WHEN ANS.ANSWER_DESC = '其他' THEN '其他-'||DTL.REMARK WHEN ANS.ANSWER_DESC IS NULL THEN DTL.REMARK ELSE ANS.ANSWER_DESC END AS ANSWER_DESC ");
		//		sb.append("FROM TBCAM_EXAMRECORD EXAM, TBCAM_EXAMRECORD_DETAIL DTL ");
		//		sb.append("LEFT JOIN TBSYS_QST_QUESTION QUS ON DTL.QUESTION_VERSION = QUS.QUESTION_VERSION ");
		//		sb.append("LEFT JOIN TBSYS_QST_ANSWER ANS ON DTL.ANSWER_SEQ = ANS.ANSWER_SEQ AND DTL.QUESTION_VERSION = ANS.QUESTION_VERSION ");
		//		sb.append("WHERE EXAM.RECORD_SEQ = DTL.RECORD_SEQ) BASE ");
		//		sb.append("LEFT JOIN TBSYS_QUESTIONNAIRE Q ON BASE.EXAM_VERSION = Q.EXAM_VERSION AND BASE.QUESTION_VERSION = Q.QUESTION_VERSION ");
		//		sb.append("LEFT JOIN (SELECT CAMP.CAMPAIGN_ID, CAMP.STEP_ID, CAMP.CAMPAIGN_NAME, CAMP.EXAM_ID, LEADS.CUST_ID, INFO.AO_CODE, LEADS.EMP_ID, INFO.EMP_NAME, DEFN.DEPT_NAME ");
		//		sb.append("FROM TBCAM_SFA_CAMPAIGN CAMP, TBCAM_SFA_LEADS LEADS ");
		//		sb.append("LEFT JOIN VWORG_BRANCH_EMP_DETAIL_INFO INFO ON LEADS.EMP_ID = INFO.EMP_ID ");
		//		sb.append("LEFT JOIN TBORG_DEFN DEFN ON DEFN.DEPT_ID = LEADS.BRANCH_ID ");
		//		sb.append("WHERE CAMP.CAMPAIGN_ID = LEADS.CAMPAIGN_ID ");
		//		sb.append("AND CAMP.STEP_ID = LEADS.STEP_ID ");
		//		sb.append("AND INSTR(LEADS.SFA_LEAD_ID, '_S', -1) = 0) LEADS ON LEADS.EXAM_ID = BASE.EXAM_VERSION AND LEADS.CUST_ID = BASE.CUST_ID ");
		//		sb.append("WHERE BASE.EXAM_VERSION = :examVersion ");
		//		sb.append("ORDER BY BASE.EXAM_VERSION, BASE.CUST_ID, Q.QST_NO, BASE.ANSWER_SEQ ");

		queryCondition.setQueryString(sb.toString());

		queryCondition.setObject("examVersion", inputVO.getExamVersion());

		String[] csvHeader = { "分行", "客戶ID", "客戶姓名", "問卷題目", "答案", "維護理專姓名", "維護理專AO Code", "回報時間" };
		String[] csvMain = { "BRANCH_NAME", "CUST_ID", "CUST_NAME", "QUESTION_DESC", "ANSWER_DESC", "EMP_NAME", "AO_CODE", "LASTUPDATE" };
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		if (list.size() > 0) {
			String fileName = (String) list.get(0).get("CAMPAIGN_NAME") + "_問卷統計結果_" + SDFYYYYMMDD.format(new Date()) + "_" + ws.getUser().getUserID() + ".csv";

			List listCSV = new ArrayList();
			for (Map<String, Object> map : list) {
				String[] records = new String[csvMain.length];

				for (int i = 0; i < csvMain.length; i++) {
					switch (csvMain[i]) {
						case "QUESTION_DESC" :
						case "ANSWER_DESC" :
						case "AO_CODE" :
							records[i] = "=\"" + checkIsNull(map, csvMain[i]) + "\"";
							break;
						case "LASTUPDATE" : 
							try {
								records[i] = "=\"" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse((String) map.get(csvMain[i]))) + "\"";
							} catch (Exception e) {
								records[i] = "";
							}
							break;
						default :
							records[i] = checkIsNull(map, csvMain[i]);
					}
				}
				listCSV.add(records);
			}

			CSVUtil csv = new CSVUtil();
			csv.setHeader(csvHeader);
			csv.addRecordList(listCSV);
			String url = csv.generateCSV();

			// download
			notifyClientToDownloadFile(url, fileName);
		} else {
			outputVO.setReportList(list);
		}

		this.sendRtnObject(outputVO);
	}

	/**
	 * 檢查Map取出欄位是否為Null
	 */
	private String checkIsNull(Map map, String key) {

		if (StringUtils.isNotBlank((String) map.get(key))) {
			return String.valueOf(map.get(key));
		} else {
			return "";
		}
	}
}