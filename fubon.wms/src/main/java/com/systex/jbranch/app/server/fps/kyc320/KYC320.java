package com.systex.jbranch.app.server.fps.kyc320;

import com.systex.jbranch.app.common.fps.table.TBKYC_COOLING_PERIODVO;
import com.systex.jbranch.app.common.fps.table.TBKYC_INVESTOREXAM_MVO;
import com.systex.jbranch.app.common.fps.table.TBKYC_INVESTOREXAM_M_HISVO;
import com.systex.jbranch.app.server.fps.kyc.chk.KYCCheckIdentityWeights;
import com.systex.jbranch.app.server.fps.kyccons.KYCCons;
import com.systex.jbranch.app.server.fps.kycoperation.KycCoolingRelease;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.fubon.commons.cbs.CBSService;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.report.ReportIF;
import com.systex.jbranch.platform.common.report.factory.ReportFactory;
import com.systex.jbranch.platform.common.report.generator.ReportGeneratorIF;
import com.systex.jbranch.platform.common.report.reportdata.ReportData;
import com.systex.jbranch.platform.common.report.reportdata.ReportDataIF;
import com.systex.jbranch.platform.common.util.CSVUtil;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.common.util.StringUtil;
import com.systex.jbranch.platform.server.info.*;
import com.systex.jbranch.platform.util.IPrimitiveMap;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * MENU
 * 
 * @author Kevin
 * @date 2016/08/25
 * @spec null
 */
@Component("kyc320")
@Scope("prototype")
public class KYC320 extends FubonWmsBizLogic {
	
	@Autowired@Qualifier("KYCCheckIdentityWeights")
	private KYCCheckIdentityWeights KYCCheck;
	private DataAccessManager dam = null;	
	
	@Autowired
	private CBSService cbsservice;
	
	/***** init資訊 ******/
	public void init(Object body, IPrimitiveMap header) throws JBranchException {
		//個金、企金主管
		String[] financeBoss = (String[]) ArrayUtils.add((String[]) ArrayUtils.clone(KYCCons.PERSONAL_FINANCE_BOSS) , KYCCons.LEGAL_PERSON_FINANCE_BOSS);
		//法金op或主管
		String[] businessRole = (String[]) ArrayUtils.add((String[]) ArrayUtils.clone(new String[]{KYCCons.LEGAL_PERSON_FINANCE_OP}) , KYCCons.LEGAL_PERSON_FINANCE_BOSS);
		//登入者ID
		String loginID = (String) getCommonVariable(SystemVariableConsts.LOGINID);
		//roleID for PRIVILEGEID
		String main_role = (String) getUserVariable(FubonSystemVariableConsts.LOGINROLE);

		Integer cnt = null;
		Boolean reviewCoolingPeriod = false;
		Map<String , Object> resultMap = new Hashtable<String , Object>();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		// ===取得內外訪權限
		try {
			//判斷是否為法人或法人主管
			cnt = KYCCheck.queryOtherRoleCnt(loginID, businessRole);
			if(cnt != null && cnt > 0){
				String queryStr = "select dept_id , dept_name from TBORG_DEFN where DEPT_ID in(select dept_id from  TBORG_MEMBER WHERE emp_id = :empId) ";
				Map<String , Object> paramMap = new Hashtable<String , Object>();
				paramMap.put("empId", loginID);
				List<Map<String , Object>> deptList = exeQueryForMap(queryStr , paramMap);
				
				if(CollectionUtils.isNotEmpty(deptList)){
					resultMap = deptList.get(0);
				}
			}
			
			//判斷主管權限
			cnt = KYCCheck.queryOtherRoleCnt(loginID, financeBoss);
			resultMap.put("isBoss", cnt != null && cnt > 0);
				
			
			// get EMP privilegeid 
			StringBuffer sb = new StringBuffer();
			sb.append("SELECT PRIVILEGEID, ROLEID AS ROLE_ID ");
			sb.append("FROM TBSYSSECUROLPRIASS ");
			sb.append("WHERE ROLEID = :roleID ");
			queryCondition.setObject("roleID", main_role);
			queryCondition.setQueryString(sb.toString());
			
			List<Map<String, Object>> privilegeList = dam.exeQuery(queryCondition);
			String privilegeid = "";
			if (privilegeList.size() > 0) {
				privilegeid = (String) privilegeList.get(0).get("PRIVILEGEID");
			}
			for(String pri:financeBoss){
				if(privilegeid.equals(pri)){
					reviewCoolingPeriod = true;
				}
			}
			resultMap.put("reviewCoolingPeriod", reviewCoolingPeriod);
			
		} catch (Exception e) {
			resultMap.put("isBoss", false);
			e.printStackTrace();
		}
		
		sendRtnObject(resultMap);
	}
	
	public void inquire(Object body, IPrimitiveMap header) throws Exception {
		
		initUUID();
		
		KYC320OutputVO outputVO = new KYC320OutputVO();
	    outputVO = this.inquire(body);
	        
	    sendRtnObject(outputVO);
	}
	
	public KYC320OutputVO inquire(Object body) throws JBranchException {
		
		initUUID();

		KYC320InputVO inputVO = (KYC320InputVO) body;
		KYC320OutputVO outputVO = new KYC320OutputVO();
		dam = this.getDataAccessManager();
		StringBuffer sb = new StringBuffer();
		QueryConditionIF queryCondition = this.getDataAccessManager().getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		String loginRoleID = null != inputVO.getSelectRoleID() ? inputVO.getSelectRoleID() : (String) getUserVariable(FubonSystemVariableConsts.LOGINROLE);
		String memLoginFlag = null != inputVO.getMemLoginFlag() ? inputVO.getMemLoginFlag() : (String) getUserVariable(FubonSystemVariableConsts.LOGINROLE);
		
		// ===取得理專員額表 
		boolean openOpt = false;
		if (StringUtils.lowerCase(loginRoleID).indexOf("uhrm") >= 0 &&
			!StringUtils.equals(StringUtils.lowerCase(memLoginFlag), "uhrm")) {
			sb.append("SELECT COUNT(1) AS COUNTS ");
			sb.append("FROM TBCRM_CUST_MAST CM ");
			sb.append("WHERE EXISTS (SELECT 1 FROM VWORG_EMP_UHRM_INFO U WHERE CM.AO_CODE = U.UHRM_CODE) ");
			sb.append("AND CM.CUST_ID = :custID ");
			
			queryCondition.setObject("custID", inputVO.getCUST_ID());
			queryCondition.setQueryString(sb.toString().replaceAll("\\s+", " "));

			List<Map<String, Object>> list = dam.exeQuery(queryCondition);
			
			if (((BigDecimal) list.get(0).get("COUNTS")).compareTo(BigDecimal.ONE) >= 0) {
				openOpt = true;
			}
		} else {
			openOpt = true;
		}
		
		if (openOpt) {
			sb = new StringBuffer();
			queryCondition = this.getDataAccessManager().getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			
			sb.append("SELECT T.*, ");
			sb.append("       (SELECT DEPT_NAME FROM TBORG_DEFN WHERE DEPT_ID=T.INVEST_BRANCH_NBR) AS INVEST_BRANCH_NAME, ");
			sb.append("       CASE WHEN R.REPORT_FILE IS NULL THEN 'N' ELSE 'Y' END AS KYC, ");
			sb.append("       CASE WHEN R.REPORT_FILE_ENG IS NULL THEN 'N' ELSE 'Y' END AS KYC_ENG, ");
			sb.append("       R.CREATOR AS RPT_CREATOR_EMPID, ");
			sb.append("       CASE S.TYPE WHEN '1' THEN '1' ELSE '0' END AS TYPE, ");
			sb.append("       (SELECT COUNT(1) ");
			sb.append("        FROM TBKYC_INVESTOREXAM_M_HIS ");
			sb.append("        WHERE CUST_ID  = :CUST_ID ");
			sb.append("        AND STATUS = '03' ) AS COUT, ");
			sb.append("        C.STATUS AS COOL_STATUS, C.REC_SEQ AS COOL_REC_SEQ, L.INLINEEMPID, ");
			sb.append(" 	   MEM.EMP_NAME AS INLINEEMP_NAME, C.MEMO AS REMARK, CM.AGE, C.CUST_RISK_AFR as COOL_RISK_AFR ");
			sb.append("FROM TBKYC_INVESTOREXAM_M_HIS T  "); 
			sb.append("LEFT JOIN (SELECT SEQ, CUST_ID, '1' AS TYPE FROM TBKYC_INVESTOREXAM_M ) S ON T.SEQ = S.SEQ ");
			sb.append("LEFT JOIN TBKYC_REPORT R ON T.SEQ = R.SEQ "); 
			sb.append("LEFT JOIN TBKYC_COOLING_PERIOD C ON T.SEQ = C.SEQ ");
			sb.append("LEFT JOIN TBSYS_REC_LOG L ON C.REC_SEQ = L.TRANSSEQ ");
			sb.append("LEFT JOIN TBORG_MEMBER MEM ON  MEM.EMP_ID = L.INLINEEMPID ");
			sb.append("LEFT JOIN TBCRM_CUST_MAST CM ON CM.CUST_ID = T.CUST_ID ");
			sb.append("WHERE 1 = 1 ");
			sb.append("AND T.STATUS in ('03','99') "); 
			
			//where
			if(StringUtils.isNotBlank(inputVO.getCUST_ID())){
				sb.append("AND T.CUST_ID = :CUST_ID ");
			}
			
			// 承作通路
			if(!StringUtils.isBlank(inputVO.getINV())){
				// WebService 使用 EMP_ID 當作承作通路的依據，其他則是使用分行。
				sb.append(getTrTypeMap().containsKey(inputVO.getINV())? "AND T.EMP_ID = :inv " : "AND T.INVEST_BRANCH_NBR = :inv ");
				queryCondition.setObject("inv", inputVO.getINV());
			}
			
			if(inputVO.getsTime() != null){
				sb.append("AND TO_CHAR(T.CREATE_DATE,'YYYYMMDD') >= :CREATE_DATEE ");
				//日期轉換
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
				String dateString = sdf.format(inputVO.getsTime());
				queryCondition.setObject("CREATE_DATEE", dateString);
			}
			
			if(inputVO.geteTime() != null){
				sb.append("AND TO_CHAR(T.CREATE_DATE,'YYYYMMDD') <= :CREATE_DATEE2 ");
				//日期轉換
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
				String dateString = sdf.format(inputVO.geteTime());
				queryCondition.setObject("CREATE_DATEE2", dateString);
			}
			
			sb.append("ORDER BY T.CUST_ID, T.CREATE_DATE DESC ");
			
			queryCondition.setObject("CUST_ID" , inputVO.getCUST_ID());
			queryCondition.setQueryString(sb.toString().replaceAll("\\s+", " "));
			
			//#1956 KYC歷史查詢功能 - 調整法人可以冷靜期錄音解鎖
			List<Map> resultList = this.getDataAccessManager().exeQuery(queryCondition);
			for(Map map: resultList){
				map.put("isUnnaturalPerson", cbsservice.getCBSIDCode((String) map.get("CUST_ID")).matches("21|22|23|29|31|32|39"));
			}
			

			outputVO.setList(resultList); // 匯出全部用
		} else {
			throw new JBranchException("該客戶非UHRM轄下客戶");
		}
		
		return outputVO;
	}

	private Map getTrTypeMap() throws JBranchException {
		return new XmlInfo().doGetVariable("KYC.TR_TYPE", FormatHelper.FORMAT_3);
	}

	/**冷靜期解除立即生效(主管覆核)**/
	public void coolingPeriodReview(Object body, IPrimitiveMap header) throws Exception{
		KYC320InputVO inputVO = (KYC320InputVO) body;
		
		//(1)檢核錄音序號及覆核主管
		checkRECandAdmin(inputVO);
		
		//(2)更新所有資料並傳送電文給主機
		KycCoolingRelease kyccoolingrelease = (KycCoolingRelease) PlatformContext.getBean("kyccoolingrelease");

		kyccoolingrelease.coolingReleaseUpdate(inputVO.getSEQ());  //解除冷靜期，更新C值資料
		kyccoolingrelease.coolingReleaseSend390(inputVO.getSEQ());  //解除冷靜期，更新C值資料給主機

		sendRtnObject(null);
	}
	
	private void checkRECandAdmin(KYC320InputVO inputVO) throws Exception{
		DataAccessManager dam = getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		
		//檢核錄音序號
		sql.append(" SELECT R.TRANSSEQ, R.BRANCHNBR ");
		sql.append(" FROM TBSYS_REC_LOG R ");
		sql.append(" LEFT JOIN TBKYC_COOLING_PERIOD C ON R.CUSTID = C.CUST_ID ");
		sql.append(" WHERE R.CUSTID = :CUST_ID ");
		sql.append(" AND R.TRANSSEQ = :TRANSSEQ ");
		sql.append(" AND R.PRODTYPE = 'KYC' ");
		sql.append(" AND R.PRODID = 'COOLING_RELEASE' ");
		sql.append(" AND R.TRANSDATE BETWEEN TO_CHAR(C.CREATE_DATE,'YYYYMMDD') AND TO_CHAR(C.EFFECTIVE_DATE,'YYYYMMDD') ");
		
		queryCondition.setObject("CUST_ID" , inputVO.getCUST_ID());
		queryCondition.setObject("TRANSSEQ" , inputVO.getCOOLING_REC_SEQ());

		queryCondition.setQueryString(sql.toString());
		
		List<Map<String, String>> rec_list = dam.exeQuery(queryCondition);
		
		if(CollectionUtils.isEmpty(rec_list)){
			throw new JBranchException("無此錄音序號");
			
		}else{
			
			//驗證覆核主管的分行是否與錄音分行相同
			String loginID = (String) getCommonVariable(SystemVariableConsts.LOGINID);
			List<Map<String, String>> bossMsglist = null;
			
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sql = new StringBuffer();
			
			queryCondition.setQueryString("select DEPT_ID from TBORG_MEMBER where EMP_ID = :emp_id ");
			queryCondition.setObject("emp_id", loginID);
			bossMsglist = dam.exeQuery(queryCondition);
			
//			if (!rec_list.get(0).get("BRANCHNBR").equals(bossMsglist.get(0).get("DEPT_ID"))) {
//				throw new JBranchException("非錄音承作分行");
//			}
			
			//將錄音序號寫入冷靜期資料檔
			TBKYC_COOLING_PERIODVO tcpVO = new TBKYC_COOLING_PERIODVO();
			List<Criterion> criList = new ArrayList<Criterion>();
			criList.add(Restrictions.eq("CUST_ID", inputVO.getCUST_ID()));
			criList.add(Restrictions.eq("STATUS", "C"));
			List<TBKYC_COOLING_PERIODVO> result = (List<TBKYC_COOLING_PERIODVO>) dam.findByCriteria(TBKYC_COOLING_PERIODVO.TABLE_UID, criList);
//			Date current = new Date();
			for(TBKYC_COOLING_PERIODVO tvo : result) {
				tvo.setSIGNOFF_DATE(new Timestamp(new Date().getTime()));
				tvo.setSIGNOFF_ID(loginID);
				tvo.setREC_SEQ(rec_list.get(0).get("TRANSSEQ"));
				tvo.setMEMO("輸入錄音序號解除冷靜期");
				getDataAccessManager().update(tvo);
			}
			
		}
	}
	
	//查詢網銀KYC
	public void inquireKYC(Object body, IPrimitiveMap header) throws JBranchException {
		KYC320InputVO inputVO = (KYC320InputVO) body;
		KYC320OutputVO outputVO = new KYC320OutputVO();
		dam = this.getDataAccessManager();  
		QueryConditionIF queryCondition = this.getDataAccessManager().getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		String quesType = "02";
		if(inputVO.getKYCcustID() != null && inputVO.getKYCcustID().length() <= 8) {
			quesType = "03";
		}
		
		// 取得網銀KYC 
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT * FROM ( ");
		sql.append("SELECT ");
		sql.append("I.PROFILE_TEST_ID, EXAM.QST_NO, QM.QUESTION_DESC, ");
		sql.append("QD.answer_seq, QD.ANSWER_DESC, ");
		sql.append("I.ANSWER FROM TBSYS_QUESTIONNAIRE EXAM ");
		sql.append("LEFT JOIN TBSYS_QST_QUESTION QM ON EXAM.QUESTION_VERSION = QM.QUESTION_VERSION ");
		sql.append("LEFT JOIN TBSYS_QST_ANSWER QD ON QM.QUESTION_VERSION = QD.QUESTION_VERSION ");
		sql.append("INNER JOIN PEOPSOFT.PS_FP_INVESTOREXAMRECORD I ON I.EXAMID=EXAM.EXAM_VERSION AND I.QID=EXAM.QUESTION_VERSION AND I.QSEQUENCE=EXAM.QST_NO ");
		sql.append("WHERE ");
		sql.append(" I.PROFILE_TEST_ID IN ( ");
		sql.append("SELECT SEQ from TBKYC_INVESTOREXAM_M_HIS  ");
		sql.append(" WHERE CUST_ID = :custID  AND TO_CHAR(CREATE_DATE,'YYYYMMDD')= :createDate AND INVEST_BRANCH_NBR='999' ) ");
		sql.append("AND EXAM.QUEST_TYPE = :quesType  ");
		sql.append("GROUP BY I.PROFILE_TEST_ID, EXAM.QST_NO,QM.QUESTION_DESC,QD.answer_seq,QD.ANSWER_DESC,I.ANSWER ");
		sql.append("ORDER BY I.PROFILE_TEST_ID, EXAM.QST_NO,QD.answer_seq ");
		sql.append(")A ");
		sql.append("union all ");
		sql.append("SELECT * FROM ( ");
		sql.append("SELECT ");
		sql.append("I.PROFILE_TEST_ID, ");
		sql.append("EXAM.QST_NO, ");
		sql.append("QM.QUESTION_DESC, ");
		sql.append("QD.answer_seq ,QD.ANSWER_DESC , ");
		sql.append("I.ANSWER FROM TBSYS_QUESTIONNAIRE EXAM ");
		sql.append("LEFT JOIN TBSYS_QST_QUESTION QM ON EXAM.QUESTION_VERSION = QM.QUESTION_VERSION ");
		sql.append("left join TBSYS_QST_ANSWER QD on QM.QUESTION_VERSION = QD.QUESTION_VERSION ");
		sql.append("INNER JOIN PEOPSOFT.PS_FP_INVESTOREXAMRECORD I ON 'KYC10000101'||I.EXAMID=EXAM.EXAM_VERSION AND '20170826'||I.QID=EXAM.QUESTION_VERSION AND I.QSEQUENCE=EXAM.QST_NO ");
		sql.append("WHERE ");
		sql.append("I.PROFILE_TEST_ID IN ( ");
		sql.append("select SEQ from TBKYC_INVESTOREXAM_M_HIS ");
		sql.append("WHERE CUST_ID = :custID AND TO_CHAR(CREATE_DATE,'YYYYMMDD')= :createDate AND INVEST_BRANCH_NBR='999' ) ");
		sql.append("AND EXAM.QUEST_TYPE = :quesType ");
		sql.append("GROUP BY I.PROFILE_TEST_ID, EXAM.QST_NO,QM.QUESTION_DESC,QD.answer_seq,QD.ANSWER_DESC,I.ANSWER ");
		sql.append("ORDER BY I.PROFILE_TEST_ID, EXAM.QST_NO,QD.answer_seq ");
		sql.append(") D ");
		
		
		queryCondition.setObject("custID" , inputVO.getKYCcustID());
		queryCondition.setObject("quesType" , quesType);
		queryCondition.setObject("createDate" , inputVO.getKYCcreateDate());
		queryCondition.setQueryString(sql.toString());
		outputVO.setKYCCustId(inputVO.getKYCcustID());
		outputVO.setKYCList(dam.exeQuery(queryCondition));
	
		this.sendRtnObject(outputVO);
	}
	

	/***** 修改檔案 
	 * @throws Exception ******/
	public void getDetail(Object body, IPrimitiveMap header) throws Exception {
		KYC320InputVO inputVO = (KYC320InputVO) body;
		String loginID = (String) getCommonVariable(SystemVariableConsts.LOGINID);
		TBKYC_INVESTOREXAM_MVO vo = (TBKYC_INVESTOREXAM_MVO) this.getDataAccessManager().findByPKey(TBKYC_INVESTOREXAM_MVO.TABLE_UID , inputVO.getSEQ());
		TBKYC_INVESTOREXAM_M_HISVO hisVo = (TBKYC_INVESTOREXAM_M_HISVO) this.getDataAccessManager().findByPKey(TBKYC_INVESTOREXAM_M_HISVO.TABLE_UID , inputVO.getSEQ());
		boolean isVoNotEmpty = vo != null;
		
		Map<String , Object> errorMsg = new Hashtable<String , Object>();
		List<String> supervisoList = KYCCheck.checkBossBossJurisdiction(hisVo.getCreator() , loginID);
		if (CollectionUtils.isNotEmpty(supervisoList)){
			errorMsg.put("ErrorMsg", supervisoList);
			sendRtnObject(errorMsg);
			return;
		}
		else if(!KYCCheck.checkInBossUnderJurisdiction(loginID , hisVo.getCreator())){
			errorMsg.put("ErrorMsg016", "ehl_02_KYC310_016");
			sendRtnObject(errorMsg);
			return;
		}
		
		if ("1".equals(inputVO.getTYPE())) {
			if(!isVoNotEmpty)
				throw new APException("主檔上傳失敗");
			
			vo.setOUT_ACCESS(inputVO.getOUT_ACCESS());
			this.getDataAccessManager().update(vo);
		}
		
		if (hisVo == null) 
			throw new APException("副檔上傳失敗");
		
		hisVo.setOUT_ACCESS(inputVO.getOUT_ACCESS());
		this.getDataAccessManager().update(hisVo);
		
		sendRtnObject(null);
	}
	
	/******* 修改錄音編號 *******/
	public void updateREC_SEQ(Object body, IPrimitiveMap header) throws Exception {
		KYC320InputVO inputVO = (KYC320InputVO) body;
		
		exeUpdateForQcf(genDefaultQueryConditionIF()
				.setQueryString(new StringBuilder()
				.append(" update TBKYC_INVESTOREXAM_M set REC_SEQ = :rec_seq")
				.append(" where SEQ = :seq ").toString())
				.setObject("rec_seq", inputVO.getREC_SEQ())
				.setObject("seq", inputVO.getSEQ()));
		
		exeUpdateForQcf(genDefaultQueryConditionIF()
				.setQueryString(new StringBuilder()
				.append(" update TBKYC_INVESTOREXAM_M_HIS set REC_SEQ = :rec_seq")
				.append(" where SEQ = :seq ").toString())
				.setObject("rec_seq", inputVO.getREC_SEQ())
				.setObject("seq", inputVO.getSEQ()));
			
		sendRtnObject(null);	
	}

	/******* 列印功能 *******/
	@SuppressWarnings("unchecked")
	public void print(Object body, IPrimitiveMap header)
			throws JBranchException, FileNotFoundException, IOException {
		String url = null;
		String txnCode = "KYC320";
		String reportID = "R2";
		ReportIF report = null;
		KYC320OutputVO return_VO = (KYC320OutputVO) body;
		Timestamp TM = new Timestamp(System.currentTimeMillis());

		List<Map<String, Object>> list = return_VO.getList();
		ReportFactory factory = new ReportFactory();
		ReportDataIF data = new ReportData();
		ReportGeneratorIF gen = factory.getGenerator(); 
		SimpleDateFormat sdfYYYY = new SimpleDateFormat("yyyy/MM/dd");
		String loginBranch = (String) getCommonVariable(SystemVariableConsts.LOGINBRH);
		String sysDayStr = sdfYYYY.format(TM);
		Date sTime = return_VO.getsTime();
		Date eTime = return_VO.geteTime();
		String inv = return_VO.getInv();
		String custId = return_VO.getCustId();
		String branchName = "";
		
		data.addRecordList("Script Mult Data Set", list);
		data.addRecordList("Script Mult Data Set1", list);
		data.addParameter("SYSDATE", sysDayStr);
		
		if(CollectionUtils.isNotEmpty(return_VO.getList()))
			data.addParameter("ROW", return_VO.getList().size() + "");
		else
			data.addParameter("ROW", 0 + "");
		data.addParameter("BRANCH", loginBranch);

		data.addParameter("IS_BRANCH_HAS_DATA", "");
		
		if(!StringUtils.isBlank(inv)){
			Map<String, String> trTypeMap = getTrTypeMap();

			if(!trTypeMap.containsKey(inv)){
				List<Map<String , String>> deptNames = getDataAccessManager().exeQuery(
					genDefaultQueryConditionIF().setQueryString(
						" SELECT DEPT_NAME FROM TBORG_DEFN WHERE DEPT_ID = :DEPT_ID "
					).setObject("DEPT_ID", inv)
				);
				
				if(CollectionUtils.isNotEmpty(deptNames)){
					branchName = ObjectUtils.toString(deptNames.get(0).get("DEPT_NAME"));
				}
			}
			else{
				branchName = trTypeMap.get(inv);
			}
				
			//查在這段時間是偶有此kyc紀錄
			QueryConditionIF queryCondition = genDefaultQueryConditionIF();
			StringBuffer sb = new StringBuffer()
				.append(" select count(1)cnt from TBKYC_INVESTOREXAM_M_HIS where ")
				.append(trTypeMap.containsKey(inv)? "EMP_ID = :inv ": "INVEST_BRANCH_NBR = :inv ");
			
			queryCondition.setObject("inv", inv);
			
			if(sTime != null){
				sb.append(" and TO_CHAR(CREATE_DATE,'YYYYMMDD') >= :CREATE_DATEE ");
				//日期轉換
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
				String dateString = sdf.format(sTime);
				queryCondition.setObject("CREATE_DATEE", dateString);
			}
			
			if(eTime != null){
				sb.append(" and TO_CHAR(CREATE_DATE,'YYYYMMDD') <= :CREATE_DATEE2 ");
				//日期轉換
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
				String dateString = sdf.format(eTime);
				queryCondition.setObject("CREATE_DATEE2", dateString);
			}
			
			if(StringUtils.isNotBlank(custId)){
				sb.append(" AND CUST_ID = :custId ");
				queryCondition.setObject("custId", custId);
			}
			
			Map<String , BigDecimal> countMap = (Map<String , BigDecimal>)getDataAccessManager().exeQuery(
				queryCondition.setQueryString(sb.toString())
			).get(0);
			
			if(countMap.get("CNT").intValue() == 0){
				String branchNbr = trTypeMap.containsKey(inv)? "999": inv;
				if(sTime != null && eTime != null){
					data.addParameter("IS_BRANCH_HAS_DATA", 
						sdfYYYY.format(sTime) + " ~ " + sdfYYYY.format(eTime) + " " +
								branchNbr + "-" + branchName + "無KYC資料");
				}
				else if(eTime != null){
					data.addParameter("IS_BRANCH_HAS_DATA", 
							sdfYYYY.format(eTime) + " " + branchNbr + "-" + branchName + "無KYC資料");
				}
				else if(sTime != null){
					data.addParameter("IS_BRANCH_HAS_DATA", 
						sdfYYYY.format(sTime) + " " + branchNbr + "-" + branchName + "無KYC資料");
				}
				else{
					data.addParameter("IS_BRANCH_HAS_DATA", branchNbr + "-" + branchName + "無KYC資料");
				}
			}
		}

		report = gen.generateReport(txnCode, reportID, data);
		url = report.getLocation();

		notifyClientToDownloadFile(url, "KYC320.pdf");
	}

	/**
	 * 檢查Map取出欄位是否為Null
	 */
	private String checkIsNull(Map map, String key) {
		if (StringUtils.isNotBlank(String.valueOf(map.get(key)))) {
			return String.valueOf(map.get(key));
		} else {
			return "";
		}
	}

	private String checkIsNu(Map map, String key) {

		if (StringUtils.isNotBlank(String.valueOf(map.get(key)))) {
			return String.valueOf(map.get(key));
		} else {
			return "0";
		}
	}
	
	/*****列印KYC*****/
	public void printKYC(Object body, IPrimitiveMap header) throws JBranchException {
		KYC320InputVO inputVO = (KYC320InputVO) body;
		
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		sb.append(" SELECT SEQ, REPORT_FILE, REPORT_FILE_ENG FROM TBKYC_REPORT WHERE SEQ = :seq ");
		queryCondition.setObject("seq", inputVO.getSEQ());
		queryCondition.setQueryString(sb.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		
		Object reportFile = null;
		if(StringUtils.equals("Y", inputVO.getIsPrintKYCEng())) {
			reportFile = list.get(0).get("REPORT_FILE_ENG");	//英文版KYC
		} else {
			reportFile = list.get(0).get("REPORT_FILE");		//中文版KYC
		}
		
		String errorMag = null;		
		try {
			if (list.size() > 0 && reportFile != null) {
				String filePath = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
				String uuid = UUID.randomUUID().toString();
				String fileName = String.format("%s.pdf", uuid);
				Blob blob = (Blob) reportFile;
				int blobLength = (int) blob.length();
				byte[] blobAsBytes = blob.getBytes(1, blobLength);

				File targetFile = new File(filePath, fileName);
				FileOutputStream fos = new FileOutputStream(targetFile);
				fos.write(blobAsBytes);
				fos.close();
				notifyClientViewDoc("temp//" + fileName, "pdf");
			} else {
				errorMag ="查無此資料請洽系統管理員";
			}
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
		
		if (StringUtils.isNotBlank(errorMag)) {
			throw new APException(errorMag);
		}
		this.sendRtnObject(null);
	}
	
	
	/* === 產出CSV==== */
	public void export(Object body, IPrimitiveMap header) throws JBranchException {
		
		KYC320OutputVO outputVO = (KYC320OutputVO) body;
		List<Map<String, Object>> list = outputVO.getKYCList();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String fileName = "網銀KYC歷史資料下載_" + sdf.format(new Date()) + ".csv";
		List listCSV = new ArrayList();
		
		if (list.size() == 0) {
			String[] records = new String[2];
			records[0] = "查無資料";
			listCSV.add(records);
		} else {
		for (Map<String, Object> map : list) {
			String[] records = new String[5];
			int i = 0;
			records[i] = checkIsNull(map, "PROFILE_TEST_ID"); 
			records[++i] = checkIsNull(map, "QST_NO"); 		
			records[++i] = checkIsNull(map, "QUESTION_DESC"); 			
			records[++i] = checkIsNull(map, "ANSWER_DESC"); 		
			records[++i] = checkIsNull(map, "ANSWER"); 			

			listCSV.add(records);
		}
	}
		
		// header
		String[] csvHeader = new String[5];
		int j = 0;
		csvHeader[j] = "測試/聲明代號";
		csvHeader[++j] = "題號";
		csvHeader[++j] = "題目";
		csvHeader[++j] = "選項";
		csvHeader[++j] = "答案";

		CSVUtil csv = new CSVUtil();
		csv.setHeader(csvHeader);
		csv.addRecordList(listCSV);
		String url = csv.generateCSV();
		notifyClientToDownloadFile(url, fileName);
		this.sendRtnObject(null);
	}
	
	
}
