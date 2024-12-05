package com.systex.jbranch.app.server.fps.crm512;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.systex.jbranch.app.common.fps.table.TBCRM_CUST_MASTVO;
import com.systex.jbranch.app.common.fps.table.TBCRM_CUST_NOTEVO;
import com.systex.jbranch.app.server.fps.sot701.SOT701;
import com.systex.jbranch.app.server.fps.sot701.SOT701InputVO;
import com.systex.jbranch.comutil.collection.GenericMap;
import com.systex.jbranch.comutil.parse.JsonUtil;
import com.systex.jbranch.fubon.commons.CheckBossAdmin;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.fubon.commons.PdfUtil;
import com.systex.jbranch.fubon.commons.cbs.CBSService;
import com.systex.jbranch.fubon.commons.esb.vo.fp032151.FP032151OutputVO;
import com.systex.jbranch.fubon.webservice.rs.SeniorCitizenClientRS;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

@Component("crm512")
@Scope("request")
public class CRM512 extends FubonWmsBizLogic {

	public DataAccessManager dam = null;
	SimpleDateFormat sdfYYYYMMDD = new SimpleDateFormat("yyyyMMdd");
	SimpleDateFormat sdfYYYYMMDDHHMMSS = new SimpleDateFormat("yyyyMMddHHmmss");

	@Autowired@Qualifier("sot701")
	private SOT701 sot701;
	
	@Autowired
	private CBSService cbsservice;
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private String apiParam = "SYS.SENIOR_CITIZEN_URL";
	
	/** 取得題庫對應之題目/答案/已作答之答案 **/
	public void getQusBankList(Object body, IPrimitiveMap header) throws JBranchException, Exception {

		CRM512InputVO inputVO = (CRM512InputVO) body;
		CRM512OutputVO outputVO = new CRM512OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		
		logger.info("step 1 : call FP032151 start " + new SimpleDateFormat("yyyy-MM-dd :hh:mm:ss").format(new Date()));
		
		// === 無論新/舊客戶，都需呼叫電文(FP032151)更新客戶主檔之客戶生日 START ===	
		SOT701InputVO inputVO_iot701 = new SOT701InputVO();
		inputVO_iot701.setCustID(inputVO.getCustID().toUpperCase());

		FP032151OutputVO fp032151VO = sot701.getFP032151Data(inputVO_iot701, null);
		
		logger.info("step 1 : call FP032151 end " + new SimpleDateFormat("yyyy-MM-dd :hh:mm:ss").format(new Date()));
		
		logger.info("step 2 : check CUST MAST data start " + new SimpleDateFormat("yyyy-MM-dd :hh:mm:ss").format(new Date()));
		
		// 取得分行名稱
		sb = new StringBuffer();
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sb.append("SELECT CUST_ID, CUST_NAME, BIRTH_DATE ");
		sb.append("FROM TBCRM_CUST_MAST ");
		sb.append("WHERE 1 = 1 ");
		sb.append("AND CUST_ID = :custID ");
		
		queryCondition.setObject("custID", inputVO.getCustID().toUpperCase());
		
		queryCondition.setQueryString(sb.toString());
		
		List<Map<String, Object>> custDtl = dam.exeQuery(queryCondition);
	
		if (custDtl.size() > 0) {
			logger.info("step 2-1 : update CUST MAST start " + new SimpleDateFormat("yyyy-MM-dd :hh:mm:ss").format(new Date()));
			
			if (null == custDtl.get(0).get("BIRTH_DATE")) {
				Date BDAY = (StringUtils.isBlank(fp032151VO.getBDAY())) ? null : new SimpleDateFormat("ddMMyyyy").parse(fp032151VO.getBDAY());
				
				queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sb = new StringBuffer();

				queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sb.append("UPDATE TBCRM_CUST_MAST ");
				sb.append("SET BIRTH_DATE = :birthDate ");
				sb.append("WHERE CUST_ID = :custID");
				
				queryCondition.setObject("custID", inputVO.getCustID().toUpperCase());
				queryCondition.setObject("birthDate", new Timestamp(BDAY.getTime()));
				
				queryCondition.setQueryString(sb.toString());

				dam.exeUpdate(queryCondition);
			}

			logger.info("step 2-1 : update CUST MAST end " + new SimpleDateFormat("yyyy-MM-dd :hh:mm:ss").format(new Date()));
		} else {
			logger.info("step 3 : insert CUST MAST start " + new SimpleDateFormat("yyyy-MM-dd :hh:mm:ss").format(new Date()));

			TBCRM_CUST_MASTVO custVO = new TBCRM_CUST_MASTVO();
			
			custVO.setCUST_ID(inputVO.getCustID().toUpperCase());
			custVO.setCUST_NAME(fp032151VO.getCUST_NAME());
			
			// 取電文生日，生日轉換成日期給前端
			if(!cbsservice.checkJuristicPerson(inputVO_iot701.getCustID())){
				Date BDAY = (StringUtils.isBlank(fp032151VO.getBDAY())) ? null : new SimpleDateFormat("ddMMyyyy").parse(fp032151VO.getBDAY());
				custVO.setBIRTH_DATE(new Timestamp(BDAY.getTime()));
			}
			
			dam.create(custVO);
			
			logger.info("step 3 : insert CUST MAST end " + new SimpleDateFormat("yyyy-MM-dd :hh:mm:ss").format(new Date()));

			logger.info("step 3 : insert CUST NOTE start " + new SimpleDateFormat("yyyy-MM-dd :hh:mm:ss").format(new Date()));

			// 客戶註記檔		
			TBCRM_CUST_NOTEVO tcn = new TBCRM_CUST_NOTEVO();
			tcn.setCUST_ID(inputVO.getCustID().toUpperCase());
			
			dam.create(tcn);
			
			logger.info("step 3 : insert CUST NOTE end " + new SimpleDateFormat("yyyy-MM-dd :hh:mm:ss").format(new Date()));
		}
		
		// === END ===
		
		TBCRM_CUST_MASTVO custVO = (TBCRM_CUST_MASTVO) getDataAccessManager().findByPKey(TBCRM_CUST_MASTVO.TABLE_UID, inputVO.getCustID().toUpperCase());

		logger.info("step 4 : check CUST AGE start " + new SimpleDateFormat("yyyy-MM-dd :hh:mm:ss").format(new Date()));
		if (queryHighAge(inputVO)) { //大於等於64.5歲
			logger.info("step 4 : check CUST AGE end (>=64.5) " + new SimpleDateFormat("yyyy-MM-dd :hh:mm:ss").format(new Date()));
			logger.info("step 5 : call API(getOldCust_DTL) start " + new SimpleDateFormat("yyyy-MM-dd :hh:mm:ss").format(new Date()));

			XmlInfo xmlinfo = new XmlInfo();
			Gson gson = JsonUtil.genDefaultGson();
			
			String apiName = "getOldCust_DTL";
			String url = xmlinfo.getVariable(apiParam, apiName, "F3");

			logger.info(apiName + " url:" + url);

			GenericMap inputGmap = new GenericMap();
			if (StringUtils.isNotEmpty(inputVO.getCustID())) {
				inputGmap.put("CUST_ID", inputVO.getCustID());
			}
			
			logger.info(apiName + " inputVO:" + gson.toJson(inputGmap.getParamMap()));

			List<Map<String, Object>> list = new SeniorCitizenClientRS().getList(url, inputGmap);
			
			logger.info("step 5 : call API(getOldCust_DTL) end " + new SimpleDateFormat("yyyy-MM-dd :hh:mm:ss").format(new Date()));
			
			logger.info("step 6 : get API LIST start " + new SimpleDateFormat("yyyy-MM-dd :hh:mm:ss").format(new Date()));

			for (Map<String, Object> map : list) {
				logger.info(apiName + " return:" + map);

				// 取得分行名稱
				sb = new StringBuffer();
				queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sb.append("SELECT BRANCH_NBR, BRANCH_NAME ");
				sb.append("FROM VWORG_DEFN_INFO ");
				sb.append("WHERE 1 = 1 ");
				sb.append("AND BRANCH_NBR = :branchNbr ");
				
				queryCondition.setObject("branchNbr", (String) map.get("CHG_DEPT_NAME"));
				
				queryCondition.setQueryString(sb.toString());
				
				List<Map<String, Object>> branchList = dam.exeQuery(queryCondition);
			
				if (branchList.size() > 0) {
					map.put("CHG_DEPT_NAME", map.get("CHG_DEPT_NAME") + "-" + branchList.get(0).get("BRANCH_NAME"));
				}
			
				// 取得人員名稱
				sb = new StringBuffer();
				queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sb.append("SELECT EMP_ID, EMP_NAME ");
				sb.append("FROM TBORG_MEMBER ");
				sb.append("WHERE 1 = 1 ");
				sb.append("AND EMP_ID = :empID ");
				
				queryCondition.setObject("empID", (String) map.get("CHG_CREATOR_NAME"));
				
				queryCondition.setQueryString(sb.toString());
				
				List<Map<String, Object>> memList = dam.exeQuery(queryCondition);
				
				if (memList.size() > 0) {
					map.put("CHG_CREATOR_NAME", map.get("CHG_CREATOR_NAME") + "-" + memList.get(0).get("EMP_NAME"));
				}
				
				map.put("CUST_NAME", (null != custVO ? custVO.getCUST_NAME() : ""));
			}
			
			logger.info("step 6 : get API LIST end " + new SimpleDateFormat("yyyy-MM-dd :hh:mm:ss").format(new Date()));

			outputVO.setQusBankList(list);
		} else {
			logger.info("step 4 : check CUST AGE end (<64.5) " + new SimpleDateFormat("yyyy-MM-dd :hh:mm:ss").format(new Date()));
			throw new APException("該客戶非65歲含以上客戶或客戶ID有誤");
		}
		
		sendRtnObject(outputVO);
	}
	
	/** 儲存 **/
	public void save(Object body, IPrimitiveMap header) throws JBranchException, Exception {

		CRM512InputVO inputVO = (CRM512InputVO) body;
		CRM512OutputVO outputVO = new CRM512OutputVO();
		
		String loginEmpID = (String) getUserVariable(FubonSystemVariableConsts.LOGINID);
		
		Boolean checkBossFlag = false;
		if (StringUtils.isNotEmpty(inputVO.getCheckBossFlag()) && StringUtils.equals(inputVO.getCheckBossFlag(), "Y")) {
			checkBossFlag = true;
		}
		
		if (checkBossFlag) {
			CheckBossAdmin cba = (CheckBossAdmin) PlatformContext.getBean("checkBossAdmin");
			
			Boolean checkResult = cba.checkBossAdmin(loginEmpID, inputVO.getBossEmpID(), inputVO.getBossEmpPWD());
			if (checkResult) {
				saveBossChkFunc(inputVO, loginEmpID);
				
				saveFunc(inputVO, loginEmpID);
			}
		} else {
			saveFunc(inputVO, loginEmpID);
		}
	
		sendRtnObject(outputVO);
	}
	
	public void saveBossChkFunc(CRM512InputVO inputVO, String loginEmpID) throws JBranchException, Exception {

		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		
		List<Map<String, Object>> qusBankListBefore = inputVO.getQusBankListBefore();
		List<Map<String, Object>> qusBankListAfter = inputVO.getQusBankListAfter();
		for (Map<String, Object> mapBefore : qusBankListBefore) {
			for (Map<String, Object> mapAfter : qusBankListAfter) {
				if (StringUtils.equals((String) mapBefore.get("QUESTION_VERSION"), (String) mapAfter.get("QUESTION_VERSION"))) {	// 同一題					
					
					List<Map<String, Object>> answerBefore = (List<Map<String, Object>>) mapBefore.get("answer");
					List<Map<String, Object>> answerAfter = (List<Map<String, Object>>) mapAfter.get("answer");
					
					String AFT_ANSWER_SEQ_LIST = ""; 	// 答案序號(=異動選項)
					String BEF_ANSWER_SEQ_LIST = ""; 	// 答案序號(=異動選項)
					String AFT_ANSWER_SEQ_REMARK = "";	// 答案序號(=異動選項名稱)
					String BEF_ANSWER_SEQ_REMARK = "";	// 答案序號(=異動選項名稱)
					String AFT_REMARK = "";				// 答案備註(=異動選項)
					String BEF_REMARK = "";				// 答案備註(=異動選項)
		
					for (Map<String, Object> ansBefore : answerBefore) {
						for (Map<String, Object> ansAfter : answerAfter) {
							if (StringUtils.equals((ansBefore.get("ANSWER_SEQ") + "").replace(".0", ""), (ansAfter.get("ANSWER_SEQ") + "").replace(".0", ""))) {
								switch ((String) mapAfter.get("QUESTION_TYPE")) {
									case "S":	// 單選
									case "M":	// 複選
										if (StringUtils.equals((String) ansAfter.get("ANSWER_NGCHECK"), "Y")) {
											AFT_ANSWER_SEQ_LIST += (ansBefore.get("ANSWER_SEQ") + "").replace(".0", "") + ";";
											AFT_ANSWER_SEQ_REMARK += ansAfter.get("ANSWER_DESC") + ";";
										}
										
										if (StringUtils.equals((String) ansBefore.get("ANSWER_NGCHECK"), "Y")) {
											BEF_ANSWER_SEQ_LIST += (ansBefore.get("ANSWER_SEQ") + "").replace(".0", "") + ";";
											BEF_ANSWER_SEQ_REMARK += ansBefore.get("ANSWER_DESC") + ";";
										}
										
										break;
									case "T":	// 文字
										AFT_REMARK = (null != ansAfter.get("ANSWER_REMARK") ? (String) ansAfter.get("ANSWER_REMARK") : "");
										BEF_REMARK = (null != ansAfter.get("ANSWER_REMARK") ? (String) ansAfter.get("ANSWER_REMARK") : "");
										
										break;
								}
							}
						}
					}
					
					queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
					sb = new StringBuffer();
					
					sb.append("INSERT INTO TBCRM_OLD_AGE_CHECK_LIST ( ");
					sb.append("  SEQNO, ");
					sb.append("  CUST_ID, ");
					sb.append("  CUST_NAME, ");
					sb.append("  EXAM_VERSION, ");
					sb.append("  QUESTION_VERSION, ");
					sb.append("  BEF_ANSWER_SEQ_LIST, ");
					sb.append("  BEF_ANSWER_SEQ_REMARK, ");
					sb.append("  AFT_ANSWER_SEQ_LIST, ");
					sb.append("  AFT_ANSWER_SEQ_REMARK, ");
					sb.append("  BEF_REMARK, ");
					sb.append("  AFT_REMARK, ");
					sb.append("  AUTH_DIRECTOR_EMP_ID, ");
					sb.append("  AUTH_DATE, ");
					sb.append("  VERSION, CREATETIME, CREATOR, MODIFIER, LASTUPDATE, ");
					sb.append("  AUTH_REASON ");
					sb.append(") ");
					sb.append("VALUES ( ");
					sb.append("  TBCRM_OLD_AGE_CHECK_LIST_SEQ.NEXTVAL, ");
					sb.append("  :CUST_ID, ");
					sb.append("  :CUST_NAME, ");
					sb.append("  :EXAM_VERSION, ");
					sb.append("  :QUESTION_VERSION, ");
					sb.append("  :BEF_ANSWER_SEQ_LIST, ");
					sb.append("  :BEF_ANSWER_SEQ_REMARK, ");
					sb.append("  :AFT_ANSWER_SEQ_LIST, ");
					sb.append("  :AFT_ANSWER_SEQ_REMARK, ");
					sb.append("  :BEF_REMARK, ");
					sb.append("  :AFT_REMARK, ");
					sb.append("  :AUTH_DIRECTOR_EMP_ID, ");
					sb.append("  SYSDATE, ");
					sb.append("  0, SYSDATE, :loginID, :loginID, SYSDATE, ");
					sb.append("  :AUTH_REASON ");
					sb.append(") ");

					queryCondition.setQueryString(sb.toString());

					queryCondition.setObject("CUST_ID", mapAfter.get("CUST_ID"));
					queryCondition.setObject("CUST_NAME", mapAfter.get("CUST_NAME"));
					queryCondition.setObject("EXAM_VERSION", mapAfter.get("EXAM_VERSION"));
					queryCondition.setObject("QUESTION_VERSION", mapAfter.get("QUESTION_VERSION"));
					
					switch ((String) mapAfter.get("QUESTION_TYPE")) {
						case "S":	// 單選
						case "M":	// 複選
							queryCondition.setObject("BEF_ANSWER_SEQ_LIST", BEF_ANSWER_SEQ_LIST);
							queryCondition.setObject("BEF_ANSWER_SEQ_REMARK", BEF_ANSWER_SEQ_REMARK);
							queryCondition.setObject("AFT_ANSWER_SEQ_LIST", AFT_ANSWER_SEQ_LIST);
							queryCondition.setObject("AFT_ANSWER_SEQ_REMARK", AFT_ANSWER_SEQ_REMARK);
							queryCondition.setObject("BEF_REMARK", "");
							queryCondition.setObject("AFT_REMARK", "");
							break;
						case "T":	// 文字
							queryCondition.setObject("BEF_ANSWER_SEQ_LIST", "");
							queryCondition.setObject("BEF_ANSWER_SEQ_REMARK", "");
							queryCondition.setObject("AFT_ANSWER_SEQ_LIST", "");
							queryCondition.setObject("AFT_ANSWER_SEQ_REMARK", "");
							queryCondition.setObject("BEF_REMARK", BEF_REMARK);
							queryCondition.setObject("AFT_REMARK", AFT_REMARK);
							break;
					}
					
					queryCondition.setObject("AUTH_DIRECTOR_EMP_ID", inputVO.getBossEmpID());
					queryCondition.setObject("loginID", getUserVariable(FubonSystemVariableConsts.LOGINID));
					queryCondition.setObject("AUTH_REASON", inputVO.getAuthReason());

					dam.exeUpdate(queryCondition);
				}
			}
		}
	}
	
	public void saveFunc(CRM512InputVO inputVO, String loginEmpID) throws JBranchException, Exception {

		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		
		// 取得登入者資訊
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sb = new StringBuffer();
		
		sb.append("SELECT EMP_ID, EMP_NAME, DEPT_ID ");
		sb.append("FROM TBORG_MEMBER ");
		sb.append("WHERE EMP_ID = :loginID ");
		
		queryCondition.setQueryString(sb.toString());
		
		queryCondition.setObject("loginID", loginEmpID);
		
		List<Map<String, Object>> loginEmp = dam.exeQuery(queryCondition);
		// =====
		
		XmlInfo xmlinfo = new XmlInfo();
		Gson gson = JsonUtil.genDefaultGson();
		
		String apiName = "updOldCust_DTL";
		String url = xmlinfo.getVariable(apiParam, apiName, "F3");
		
		logger.info(apiName + " url:" + url);
		
		GenericMap inputGmap = new GenericMap();
		inputGmap.put("CUST_ID", inputVO.getCustID());
		inputGmap.put("LOGIN_EMP_ID", getUserVariable(FubonSystemVariableConsts.LOGINID));
		inputGmap.put("LOGIN_EMP_NAME", loginEmp.get(0).get("EMP_NAME"));
		inputGmap.put("LOGIN_DEPT_ID", StringUtils.equals("000", (String) getUserVariable(FubonSystemVariableConsts.LOGINBRH)) ? loginEmp.get(0).get("DEPT_ID") : (String) getUserVariable(FubonSystemVariableConsts.LOGINBRH));
		
		List<Map<String, Object>> inputAnswerList = new ArrayList<Map<String, Object>>();
		
		for (Map<String, Object> map : inputVO.getQusBankListAfter()) {
			List<Map<String, Object>> ansList = (List<Map<String, Object>>) map.get("answer");
			
			for (Map<String, Object> maps : ansList) {
				GenericMap ansAftMap = new GenericMap();

				ansAftMap.put("EXAM_VERSION", map.get("EXAM_VERSION"));
				ansAftMap.put("QUESTION_VERSION", map.get("QUESTION_VERSION"));
				ansAftMap.put("QUESTION_CLASS", map.get("QUESTION_CLASS"));
				ansAftMap.put("QUESTION_NAME", map.get("QUESTION_NAME"));
				ansAftMap.put("QUESTION_TYPE", map.get("QUESTION_TYPE"));
				
				ansAftMap.put("ANSWER_SEQ", (maps.get("ANSWER_SEQ") + "").replace(".0", ""));
				ansAftMap.put("ANSWER_DESC", maps.get("ANSWER_DESC"));
				ansAftMap.put("ANSWER_NGCHECK", maps.get("ANSWER_NGCHECK"));
				ansAftMap.put("ANSWER_REMARK", maps.get("ANSWER_REMARK"));
				
//				ansAftMap.put("REPORT_YN", map.get("REPORT_YN"));
//				ansAftMap.put("CUST_ID", map.get("CUST_ID"));
//				ansAftMap.put("CUST_NAME", map.get("CUST_NAME"));
//				ansAftMap.put("QST_NO", (maps.get("QST_NO") + "").replace(".0", ""));
//				ansAftMap.put("QUESTION_CLASS_NAME", map.get("QUESTION_CLASS_NAME"));
//				ansAftMap.put("QUESTION_NAME_NAME", map.get("QUESTION_NAME_NAME"));
//				ansAftMap.put("QUESTION_DESCR", map.get("QUESTION_DESCR"));
//				ansAftMap.put("QUESTION_REMARK", map.get("QUESTION_REMARK"));
//				ansAftMap.put("REQUIRED_YN", map.get("REQUIRED_YN"));
//				ansAftMap.put("CAN_MODIFY_PRI_LIST", map.get("CAN_MODIFY_PRI_LIST"));
//				ansAftMap.put("SYSTEM_CHK_YN", map.get("SYSTEM_CHK_YN"));
//				ansAftMap.put("CHG_DEPT_NAME", map.get("CHG_DEPT_NAME"));
//				ansAftMap.put("CHG_CREATOR_NAME", map.get("CHG_CREATOR_NAME"));
//				ansAftMap.put("CHG_CREATOR_TIME", map.get("CHG_CREATOR_TIME"));
				
//				ansAftMap.put("ANSWER_FLAG", maps.get("ANSWER_FLAG"));
//				ansAftMap.put("RESULT_YN", maps.get("RESULT_YN"));
//				ansAftMap.put("FINACIAL_COGNITION_YN", maps.get("FINACIAL_COGNITION_YN"));
//				ansAftMap.put("RESULT_FLAG", maps.get("RESULT_FLAG"));
//				ansAftMap.put("RESULT_ANSWER_SEQ", maps.get("RESULT_ANSWER_SEQ"));
				
				inputAnswerList.add(ansAftMap.getParamMap());
			}
		}
		
		inputGmap.put("answer", inputAnswerList);
		
		logger.info(apiName + " inputVO:" + gson.toJson(inputGmap.getParamMap()));
		
		Map<String, Object> map = new SeniorCitizenClientRS().getMap(url, inputGmap);
				
		logger.info(apiName + " return:" + map);
		
		switch((String) map.get("EXEC_FLAG")) {
			case "S":
				break;
			case "F":
				throw new JBranchException("儲存失敗");
		}
	}

	/** 取得異動歷程 **/
	public void getCustHisByQus(Object body, IPrimitiveMap header) throws JBranchException, Exception {
		
		CRM512InputVO inputVO = (CRM512InputVO) body;
		CRM512OutputVO outputVO = new CRM512OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		
		XmlInfo xmlinfo = new XmlInfo();
		Gson gson = JsonUtil.genDefaultGson();
		
		String apiName = "getOldCust_QuesHis_DTL";
		String url = xmlinfo.getVariable(apiParam, apiName, "F3");
		
		logger.info(apiName + " url:" + url);
		
		GenericMap inputGmap = new GenericMap();
		inputGmap.put("CUST_ID", inputVO.getCustID());
		inputGmap.put("EXAM_VERSION", inputVO.getExamVersion());
		inputGmap.put("QUESTION_VERSION", inputVO.getQuestionVersion());
		
		logger.info(apiName + " inputVO:" + gson.toJson(inputGmap.getParamMap()));

		List<Map<String, Object>> list = new SeniorCitizenClientRS().getList(url, inputGmap);
		
		for (Map<String, Object> map : list) {
			logger.info(apiName + " return:" + map);
			
			// 取得分行名稱
			sb = new StringBuffer();
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sb.append("SELECT BRANCH_NBR, BRANCH_NAME ");
			sb.append("FROM VWORG_DEFN_INFO ");
			sb.append("WHERE 1 = 1 ");
			sb.append("AND BRANCH_NBR = :branchNbr ");
			
			queryCondition.setObject("branchNbr", (String) map.get("CHG_DEPT"));
			
			queryCondition.setQueryString(sb.toString());
			
			List<Map<String, Object>> branchList = dam.exeQuery(queryCondition);
		
			if (branchList.size() > 0) {
				map.put("CHG_DEPT", map.get("CHG_DEPT") + "-" + branchList.get(0).get("BRANCH_NAME"));
			}
		}
		
		outputVO.setCustHisByQusList(list);
		
		sendRtnObject(outputVO);
	}

	/** 匯出PDF **/
	public void exportPDF(Object body, IPrimitiveMap header) throws JBranchException, Exception {
		
		CRM512InputVO inputVO = (CRM512InputVO) body;
		CRM512OutputVO outputVO = new CRM512OutputVO();
		dam = this.getDataAccessManager();
		
		// pdf暫存檔檔名
		String fileName = "HighAsset_" + inputVO.getCustID() + "_" + sdfYYYYMMDDHHMMSS.format(new Date()) + ".pdf";
		
		// 產出pdf暫存位置
		List<String> megerurl = new ArrayList<String>();
		
		// root path
		String serverPath = (String) SysInfo.getInfoValue(SystemVariableConsts.SERVER_PATH);
		
		// pdf產生物件
		FubonWmsBizLogic crm512PDF = null;
				
		crm512PDF = new CRM512_Report();
		crm512PDF.setDataAccessManager(dam);
		
		//gen pdf內容
		crm512PDF.getClass()
		 		 .getMethod("genCRM512PDF", new Class[]{String.class, String.class, String.class})
		 		 .invoke(crm512PDF , new Object[]{getUserVariable(FubonSystemVariableConsts.LOGINID), inputVO.getCustID(), fileName});

		megerurl.add("/temp/reports/" + fileName);
		
		String url = PdfUtil.mergePDF(megerurl, false);
		    	
    	// download
    	notifyClientToDownloadFile(url, fileName);
    				
    	sendRtnObject(outputVO);
	}

    public void isHighAge(Object body, IPrimitiveMap header) throws JBranchException {
    	
    	CRM512InputVO inputVO = (CRM512InputVO) body;
		CRM512OutputVO outputVO = new CRM512OutputVO();
		
		if (queryHighAge(inputVO)) {
			outputVO.setHighAge(true);
		} else {
			outputVO.setHighAge(false);
		}
		
		sendRtnObject(outputVO);
    }
    
    /***
     * 檢核客戶是否>=64.5歲
     * @param inputVO
     * @return true:是  false:否
     * @throws JBranchException
     */
    private boolean queryHighAge(CRM512InputVO inputVO) throws JBranchException {
	
    	dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		
		// 限65歲(含)以上客戶可以維護
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sb = new StringBuffer();
		
		sb.append("SELECT 1 ");
		sb.append("FROM TBCRM_CUST_MAST ");
		sb.append("WHERE CUST_ID = :custID ");
		sb.append("AND ROUND(MONTHS_BETWEEN(SYSDATE, BIRTH_DATE) / 12, 2) >= 64.5 ");
		
		queryCondition.setQueryString(sb.toString());
		queryCondition.setObject("custID", inputVO.getCustID());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		
		return CollectionUtils.isNotEmpty(list);
    }

}
