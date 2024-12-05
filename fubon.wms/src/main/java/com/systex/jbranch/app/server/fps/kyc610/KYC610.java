package com.systex.jbranch.app.server.fps.kyc610;

import java.io.File;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.ibm.icu.util.Calendar;
import com.systex.jbranch.app.common.fps.table.TBCRM_CUST_MASTVO;
import com.systex.jbranch.app.common.fps.table.TBKYC_PROEXAM_M_HISTVO;
import com.systex.jbranch.app.common.fps.table.TBORG_MEMBERVO;
import com.systex.jbranch.app.server.fps.kyc.chk.KYCCheckIdentityWeights;
import com.systex.jbranch.fubon.commons.esb.EsbUtil;
import com.systex.jbranch.platform.common.dataManager.DataManager;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.serialnumber.SerialNumberUtil;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;
/**
 * KYC610
 * 
 * @author Sam
 * @date 2018/05/24
 * @spec null
 */
@Component("kyc610")
@Scope("request")
public class KYC610 extends EsbUtil{
	Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private DataAccessManager dam_obj = null;
	
	@Autowired@Qualifier("KYCCheckIdentityWeights")
	private KYCCheckIdentityWeights KYCCheck;
		
	/**
	 * 隨機取得選取的問卷選項，且當日尚未做過的問卷
	 * @param qstType
	 * @param list
	 * @return  List<Map<String,Object>>>
	 */
	public List<Map<String, Object>> queryQuestion(String qstType ,List<TBKYC_PROEXAM_M_HISTVO> list) throws Exception{
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> questList = new ArrayList<Map<String,Object>>();
		List<String> examVer = new ArrayList<String>();//已做過的問卷編號
		List<Map<String,Object>> qualifiedExamVers = new ArrayList<Map<String,Object>>();//符條件的問卷編號

		//已做過的問卷編號
		for (TBKYC_PROEXAM_M_HISTVO vo:list) {
			String examV = vo.getEXAM_VERSION();
			examVer.add(examV);
		}
			dam_obj = getDataAccessManager();
			QueryConditionIF qc = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuilder sb = new StringBuilder();

			sb = new StringBuilder();
			sb.append(" SELECT distinct EXAM_VERSION	");
			sb.append("		FROM TBSYS_QUESTIONNAIRE	A LEFT JOIN TBSYS_QST_QUESTION B	");
			sb.append("		ON A.QUESTION_VERSION = B.QUESTION_VERSION	");
			sb.append("		WHERE A.STATUS='02' AND A.QUEST_TYPE in (:quest_type) AND A.ACTIVE_DATE <=SYSDATE AND A.EXPIRY_DATE >SYSDATE AND	");
			sb.append("						B.QUESTION_VERSION IS NOT NULL");
			if (examVer.size()>0)
				sb.append("		AND A.EXAM_VERSION NOT IN  (:examVer)");
			sb.append("		ORDER BY DBMS_RANDOM.VALUE");
			if (examVer.size()>0)
				qc.setObject("examVer", examVer);
			qc.setObject("quest_type", qstType);
			qc.setQueryString(sb.toString());
			qualifiedExamVers.addAll(dam_obj.exeQuery(qc));
			
			if (qualifiedExamVers.size()==0) 
				throw new JBranchException("無符合條件問卷");
			
			//依符合條件的問卷編號取得問卷內容
 			String examVersion = (String)qualifiedExamVers.get(0).get("EXAM_VERSION");
 			dam_obj = getDataAccessManager();
 			qc = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
 			sb = new StringBuilder();
 			sb.append(" SELECT n.EXAM_VERSION,n.QUESTION_VERSION,n.EXAM_NAME,n.QUEST_TYPE,	");
 			sb.append(" n.QST_NO,n.ESSENTIAL_FLAG,n.RL_VERSION,n.STATUS,n.ACTIVE_DATE,	");
 			sb.append(" Q.QUESTION_DESC,Q.QUESTION_TYPE,Q.DEFINITION,Q.CORR_ANS	");
 			sb.append(" from TBSYS_QUESTIONNAIRE n	");
 			sb.append("	,TBSYS_QST_QUESTION Q	");
 			sb.append(" where n.STATUS = '02' AND n.QUESTION_VERSION = Q.QUESTION_VERSION AND	");//待啟用
 			sb.append("				N.EXAM_VERSION = :examVersion and n.QUEST_TYPE=:questType");
 			sb.append("	ORDER BY QST_NO	");
 			//Get question by specific exam version;
 			qc.setObject("examVersion", examVersion);
 			qc.setObject("questType", qstType);
 			qc.setQueryString(sb.toString());
 			List<Map<String,Object>> query = dam_obj.exeQuery(qc); 
 			if(query.size()>0)
 				result.addAll(query);
		return result;
	}
	
	//答案
	public List<Map<String, Object>> queryAnswer(String exam_version,String question_version){
		
		List<Map<String, Object>> ansList = new ArrayList<Map<String,Object>>();
		
		if(StringUtils.isNotBlank(exam_version) && StringUtils.isNotBlank(question_version)){
			try {
				dam_obj = getDataAccessManager();
				QueryConditionIF qc = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				StringBuilder sb = new StringBuilder();
				//answer
				sb.append(" SELECT A.QUESTION_VERSION,A.ANSWER_SEQ,A.ANSWER_DESC,W.FRACTION ");
				sb.append(" FROM TBSYS_QST_ANSWER A,TBKYC_QUESTIONNAIRE_ANS_WEIGHT W ");//題目答案列表檔、答案權重檔
				sb.append(" WHERE A.QUESTION_VERSION = W.QUESTION_VERSION and A.ANSWER_SEQ = W.ANSWER_SEQ ");
				sb.append("  AND W.EXAM_VERSION = :exam_version AND A.QUESTION_VERSION = :question_version ");
				qc.setObject("exam_version", exam_version);
				qc.setObject("question_version", question_version);
				qc.setQueryString(sb.toString());
				ansList = dam_obj.exeQuery(qc);
				
			} catch (JBranchException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return ansList;
	}
		
	//列印
	public void printQst(Object body, IPrimitiveMap<Object> header) throws Exception{
		KYC610InputVO inputVO = (KYC610InputVO) body;
		KYC610OutputVO outputVO = new KYC610OutputVO();
		List<String> megerurl = new ArrayList<String>();
		String qstType = inputVO.getQUEST_TYPE();
		String qstName = qstType.equals("04") ? "專業投資人金融(含債券)專業知識評估" : qstType.equals("05") ? "結構型商品專業知識評估" : "";
		//問卷列印歷史記錄,Map key:EXAM_VERSION
		List<TBKYC_PROEXAM_M_HISTVO> limitList = queryQuestHis(inputVO);

		try{
			if (limitList.size() >= 2) {
				throw new JBranchException("已超過當日可執行次數");
			}
	
			//取得已啟用且當日未做過的問卷
			List<Map<String	,	Object>> questions	=	queryQuestion(qstType	,	limitList);
	
			//依問卷種類取得選項
			for (Map map:questions) {
				String examVer = (String) map.get("EXAM_VERSION");
				String qstVer = (String) map.get("QUESTION_VERSION");
				List<Map<String	,	Object>> options	=	queryOptions(examVer	,	qstVer);
				map.put("OPTION_LIST", options);
				outputVO.setExamVersion(examVer);
				inputVO.setEXAM_VERSION(examVer);
			}
			inputVO.setQuest_list(questions);

			KYC610_Qst qst = new	KYC610_Qst(); 
			qst.setDataAccessManager(getDataAccessManager());

			File tempPath = new File (DataManager.getRealPath() ,"temp//reports");
			SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmssSSS");
			String timeSeq = format.format(new Date());
			String tempQstPath = tempPath + "/proQst"+timeSeq+".pdf";
			String tempAnsPath = tempPath + "/proAns"+timeSeq+".pdf";
			
			String loginID = (String) getCommonVariable(SystemVariableConsts.LOGINID);
			String investBranchNbr = queuryInvestBranchNbr(loginID);//客戶承作問卷分行
			Map putStuffMap = new HashMap();
			putStuffMap.put("inputBranch", investBranchNbr);
			inputVO.setResultMap(putStuffMap);
			qst.genBlankPDF(inputVO , tempQstPath);//空白問卷與答案 mantis:5432
//			notifyClientToDownloadFile("/temp/reports/proAns.pdf", qstName+".pdf");
			
			Map<String,Object> resultMap = new HashMap<String,Object>();
			resultMap.put("TIME_SEQ", timeSeq);//檔案序號，題目答案都相同
			resultMap.put("QST_NAME", qstName);
			outputVO.setResultMap(resultMap);
			sendRtnObject(outputVO);
		}catch(Exception e){
			throw new JBranchException(e);
		}
	}

	
	public void getQstPdf(Object body, IPrimitiveMap<Object> header) throws Exception {
		KYC610InputVO inputVO = (KYC610InputVO)body;
		Map<String , Object> inputMap = inputVO.getResultMap();
		String timeSeq	=	(String) inputMap.get("TIME_SEQ");
		String qstName	=	(String) inputMap.get("QST_NAME");
		notifyClientToDownloadFile("temp/reports/proQst"+timeSeq+".pdf", qstName+"題目.pdf");
		getDataAccessManager().create(this.inputVoToProHisVo(inputVO));
	}
	public void getAnsPdf(Object body, IPrimitiveMap<Object> header) throws Exception {
		KYC610InputVO inputVO = (KYC610InputVO)body;
		Map<String , Object> inputMap = inputVO.getResultMap();
		String timeSeq	=	(String) inputMap.get("TIME_SEQ");
		String qstName	=	(String) inputMap.get("QST_NAME");
		notifyClientToDownloadFile("temp/reports/proAns"+timeSeq+".pdf", qstName+"答案.pdf");
		//Insert record
		getDataAccessManager().create(this.inputVoToProHisVo(inputVO));
	}
	public void delTmpPdf(Object body, IPrimitiveMap<Object> header) throws Exception {
		KYC610InputVO inputVO = (KYC610InputVO)body;
		Map<String , Object> inputMap = inputVO.getResultMap();
		String timeSeq	=	(String) inputMap.get("TIME_SEQ");
		String qstName	=	(String) inputMap.get("QST_NAME");
		//Remove temp pdf
		String serverPath = (String) SysInfo.getInfoValue(SystemVariableConsts.SERVER_PATH);
		File file = new File(serverPath+"\\temp\\reports\\proQst"+timeSeq+".pdf");
		file.delete();
		file = new File(serverPath+"\\temp\\reports\\proAns"+timeSeq+".pdf");
		file.delete();
		
	}
	//選項
	private List<Map<String, Object>> queryOptions(String exam_version,String question_version){
		List<Map<String, Object>> ansList = new ArrayList<Map<String,Object>>();
		if(StringUtils.isNotBlank(exam_version) && StringUtils.isNotBlank(question_version)){
			try {
				dam_obj = getDataAccessManager();
				QueryConditionIF qc = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				StringBuilder sb = new StringBuilder();
				//answer
				sb.append(" SELECT A.QUESTION_VERSION,A.ANSWER_SEQ,A.ANSWER_DESC	");
				sb.append(" FROM TBSYS_QST_ANSWER A	");//題目答案列表檔
				sb.append(" WHERE A.QUESTION_VERSION = :question_version ");
				qc.setObject("question_version", question_version);
				qc.setQueryString(sb.toString());
				ansList = dam_obj.exeQuery(qc);

				if (ansList.size()==0) 
					throw new JBranchException("無符合條件問卷");
			} catch (JBranchException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return ansList;
	}
	/**
	 * 判斷是否超過當日限制次數
	 * @param KYC610InputVO inputVo
	 * @param int limit
	 * @return 
	 * @throws JBranchException 
	 * @throws DAOException 
	 */
	private List<TBKYC_PROEXAM_M_HISTVO> queryQuestHis(KYC610InputVO inputVo) throws DAOException, JBranchException {
		dam_obj = getDataAccessManager();
		QueryConditionIF qc = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuilder sb = new StringBuilder();
		String custId = inputVo.getCUST_ID();
		//記錄表TBKYC_PROEXAM_M_HIST
		
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DATE, -1);
		List<Criterion> criList = new ArrayList<Criterion>();
		criList.add(Restrictions.eq("CUST_ID", custId));
		criList.add(Restrictions.ge("CREATE_DATE",getFormattedFromDateTime(new Date())));
		criList.add(Restrictions.le("CREATE_DATE", getFormattedToDateTime(new Date())));
		criList.add(Restrictions.eq("QUEST_TYPE", inputVo.getQUEST_TYPE()));
		List<TBKYC_PROEXAM_M_HISTVO> result = (List<TBKYC_PROEXAM_M_HISTVO>) dam_obj.findByCriteria(TBKYC_PROEXAM_M_HISTVO.TABLE_UID, criList);
		return result;
	}
		 
	private Date getFormattedFromDateTime(Date date) {
	    Calendar cal = Calendar.getInstance();
	    cal.setTime(date);
	    cal.set(Calendar.HOUR_OF_DAY, 0);
	    cal.set(Calendar.MINUTE, 0);
	    cal.set(Calendar.SECOND, 0);
	    return cal.getTime();
	}
	 
	private Date getFormattedToDateTime(Date date) {
	    Calendar cal = Calendar.getInstance();
	    cal.setTime(date);
	    cal.set(Calendar.HOUR_OF_DAY, 23);
	    cal.set(Calendar.MINUTE, 59);
	    cal.set(Calendar.SECOND, 59);
	    return cal.getTime();
	}
	private List<Map<String,Object>> getAnsList(String examVersion , String questVersion) throws Exception {
		dam_obj = getDataAccessManager();
		QueryConditionIF qc = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuilder sb = new StringBuilder();
		//記錄表TBKYC_PROEXAM_M_HIST
		sb = new StringBuilder().
		append(" SELECT EXAM_VERSION , QUESTION_VERSION , CORRECT_ANS").
		append(" from TBKYC_QUEST_CORR_ANS	").
		append(" where EXAM_VERSION = :examVersion and QUESTION_VERSION = :questionVersion");
		qc.setObject("examVersion", examVersion);
		qc.setObject("questionVersion", questVersion);
		qc.setQueryString(sb.toString());
		return dam_obj.exeQuery(qc);
	}

	
	private String getVersion() throws JBranchException{
		SerialNumberUtil sn = new SerialNumberUtil();
		Date date = new Date();
		SimpleDateFormat simple = new SimpleDateFormat("yyyyMMdd");
		String date_seqnum = simple.format(date);
		String seqNum = "";
		try {
			seqNum = "PRO"+date_seqnum+(sn.getNextSerialNumber("KYC610"));
		} catch (Exception e) {
			sn.createNewSerial("KYC610", "00000", null, null, null, 1,new Long("99999") , "y", new Long("0"), null);
			seqNum = "PRO"+date_seqnum+(sn.getNextSerialNumber("KYC610"));
		}
		return seqNum;
	}
	
	
	/**inputVo 轉 專業投資人測驗記錄明細
	 * @throws Exception **/
	private TBKYC_PROEXAM_M_HISTVO inputVoToProHisVo(KYC610InputVO inputVO) throws Exception{
		TBKYC_PROEXAM_M_HISTVO proHist = new TBKYC_PROEXAM_M_HISTVO();
		String loginID = (String) getCommonVariable(SystemVariableConsts.LOGINID);
		String investBranchNbr = queuryInvestBranchNbr(loginID);//客戶承作問卷分行
		TBCRM_CUST_MASTVO mastVo = (TBCRM_CUST_MASTVO) getDataAccessManager().findByPKey(TBCRM_CUST_MASTVO.TABLE_UID, inputVO.getCUST_ID());
//		try {
//			mastVo = dam_obj.findByPKey(TBCRM_CUST_MASTVO.TABLE_UID, inputVO.getCUST_ID())==null?
//				null:(TBCRM_CUST_MASTVO)dam_obj.findByPKey(TBCRM_CUST_MASTVO.TABLE_UID, inputVO.getCUST_ID());
//		} catch (Exception e) {
//			logger.error(e.getMessage(),e);
//		}
		proHist.setSEQ(this.getVersion());
		proHist.setCUST_ID(inputVO.getCUST_ID());
		proHist.setCUST_NAME(inputVO.getCUST_NAME());
		proHist.setEXAM_VERSION(inputVO.getEXAM_VERSION());
		proHist.setCUST_BRANCH_NBR(mastVo==null?"":mastVo.getBRA_NBR());
		proHist.setINVEST_BRANCH_NBR(investBranchNbr);
		proHist.setCOMP_NAME(inputVO.getCOMP_NAME());
		proHist.setCOMP_NBR(inputVO.getCOMP_NBR());
		proHist.setQUEST_TYPE(inputVO.getQUEST_TYPE());
		proHist.setCREATE_DATE(new Timestamp(new Date().getTime()));
		return proHist;
	}

	public String queuryInvestBranchNbr(String empId) throws Exception{
		//判斷當下鑑機人員是否為企金OP或主管
		if(KYCCheck.checkIsLegalPerson(empId)){
			TBORG_MEMBERVO memberVo = (TBORG_MEMBERVO) getDataAccessManager().findByPKey(TBORG_MEMBERVO.TABLE_UID, empId);
			return memberVo.getDEPT_ID();
		}
		else 
			return (String) getCommonVariable(SystemVariableConsts.LOGINBRH);
	}
		
	//Get customer name
	public void getCustName (Object body, IPrimitiveMap<Object> header) throws DAOException {
		KYC610InputVO inputVO = (KYC610InputVO) body;
		KYC610OutputVO outputVo = new KYC610OutputVO();
		dam_obj=getDataAccessManager();
		String custId = inputVO.getCUST_ID();
		TBCRM_CUST_MASTVO mastVo = (TBCRM_CUST_MASTVO) dam_obj.findByPKey(TBCRM_CUST_MASTVO.TABLE_UID, custId);
		outputVo.setCustName(mastVo.getCUST_NAME());
		sendRtnObject(outputVo);
	}
//end	
}
