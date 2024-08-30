package com.systex.jbranch.app.server.fps.ins100;

import java.io.File;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.bind.DatatypeConverter;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.ibm.icu.text.SimpleDateFormat;
import com.systex.jbranch.app.common.fps.table.TBINS_EXAM_AGREE_HISVO;
import com.systex.jbranch.app.common.fps.table.TBINS_EXAM_POLICYNBRPK;
import com.systex.jbranch.app.common.fps.table.TBINS_EXAM_POLICYNBRVO;
import com.systex.jbranch.app.common.fps.table.TBINS_REPORTVO;
import com.systex.jbranch.app.server.fps.fps200.FPS200Api;
import com.systex.jbranch.app.server.fps.ins810.INS810;
import com.systex.jbranch.app.server.fps.ins810.INS810InputVO;
import com.systex.jbranch.app.server.fps.ins810.INS810OutputVO;
import com.systex.jbranch.comutil.collection.GenericMap;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.fubon.commons.PdfUtil;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.report.ReportIF;
import com.systex.jbranch.platform.common.report.factory.ReportFactory;
import com.systex.jbranch.platform.common.report.generator.ReportGeneratorIF;
import com.systex.jbranch.platform.common.report.reportdata.ReportData;
import com.systex.jbranch.platform.common.report.reportdata.ReportDataIF;
import com.systex.jbranch.platform.common.util.ObjectUtil;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;


/**
 * ins100
 * 
 * @author James
 * @date 2017/09/05
 * @spec null
 */
@Component("ins100")
@Scope("request")
public class INS100 extends FubonWmsBizLogic {
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(INS100.class);
	
	//======================== WEB 調用 ========================
	/**
	 * WEB 調用 取得保單號碼共用入口
	 * @param body
	 * isFilter 辨別資料撈取範圍全部或是尚未使用過的
	 * 
	 * @param header
	 * @throws JBranchException
	 */
	public void queryPolicyData (Object body, IPrimitiveMap header) throws JBranchException {
		INS100InputVO inputVO = (INS100InputVO) body;
		INS100OutputVO outputVO = new INS100OutputVO();
		List<Map<String, Object>> policyList = new ArrayList<Map<String, Object>>();

		policyList = getPolicyNumberList(inputVO.getCustId(), inputVO.getPolicyNbr(), inputVO.isFilter());
		outputVO.setPolicyList(policyList);
		this.sendRtnObject(outputVO);
	}
	
	/**
	 * WEB 調用 新增保單號碼
	 * @param body
	 * @param header
	 * @throws JBranchException
	 * @throws Exception
	 */
	public void addPolicyData (Object body, IPrimitiveMap header) throws JBranchException, Exception {
		INS100InputVO inputVO = (INS100InputVO) body;
		INS100OutputVO outputVO = new INS100OutputVO();

		int isCreate = addPolicyData(inputVO.getCustId(), inputVO.getPolicyNbr());
		System.out.println(isCreate);
		this.sendRtnObject(isCreate);
	}
	
	/**
	 * WEB 調用 刪除保單號碼
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void deletePolicyData (Object body, IPrimitiveMap header) throws JBranchException {
		INS100InputVO inputVO = (INS100InputVO) body;

		deletePolicyData(inputVO.getCustId(), inputVO.getPolicyNbr());
		this.sendRtnObject(null);
	}
		
	/**
	 * 下載保單健診同意書
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void print1 (Object body, IPrimitiveMap header) throws JBranchException {
		INS100InputVO inputVO = (INS100InputVO) body;
		String reportURL = print1Common(inputVO.getCustId(), "WEB");
		String now_date = new SimpleDateFormat("YYYYMMdd").format(new Date());
		notifyClientToDownloadFile(reportURL, now_date+"保單健診同意書.pdf");
	}
	
	/**
	 * 下載保單返還簽收單
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void download_Single (Object body, IPrimitiveMap header) throws JBranchException {
		INS100InputVO inputVO = (INS100InputVO) body;
		String branchID = (String) getUserVariable(SystemVariableConsts.LOGINBRH);
		String branchName = (String) getCommonVariable(SystemVariableConsts.LOGINBRHNAME);
		String loginID = (String) getCommonVariable(SystemVariableConsts.LOGINID);
		
		// 產出pdf
		String url = null;
		String txnCode = "INS100";
		String reportID = "R2";
		ReportIF report = null;
		ReportFactory factory = new ReportFactory();
		ReportDataIF data = new ReportData();
		ReportGeneratorIF gen = factory.getGenerator();	
		
		List<String> url_list = new ArrayList<String>();
		List<Map<String, Object>> totalList = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> list_1 = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> list_2 = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> EMP_CUSTList = new ArrayList<Map<String,Object>>();
		
		//業務人員身分證字號
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuilder sb = new StringBuilder();
		sb.append(" select CUST_ID from TBORG_MEMBER ")
			.append(" where EMP_ID = :empid ");
		queryCondition.setObject("empid", loginID);
		queryCondition.setQueryString(sb.toString());
		EMP_CUSTList = dam.exeQuery(queryCondition);
		
		//保單號碼清單
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sb = new StringBuilder();
		sb.append(" SELECT POLICY_NBR ");
		sb.append(" FROM TBINS_EXAM_POLICYNBR ");
		sb.append(" WHERE CUST_ID = :custId ");
		queryCondition.setObject("custId", inputVO.getCustId());
		queryCondition.setQueryString(sb.toString());
		List<Map<String, Object>> recordList = dam.exeQuery(queryCondition);	
		int m = 1;
		for (Map<String, Object> map : recordList) {
			Map<String, Object> addMap = new HashMap<String, Object>();
			addMap.put("POLICY_NBR", map.get("POLICY_NBR"));
			addMap.put("XXX_NO", m);
			totalList.add(addMap);
			m++;
		}
		
		reportFormat(data, list_1, list_2, totalList);
		
		if (list_1.size() > 0) {
			data.addRecordList("Script Mult Data Set", list_1);
		}
		if (list_2.size() > 0) {
			data.addRecordList("Script Mult Data Set2", list_2);
		}
		
		data.addParameter("CUST_ID", EMP_CUSTList.get(0).get("CUST_ID"));
		data.addParameter("BRANCHID", branchID);
		data.addParameter("BRANCHNAME", branchName);
		
		report = gen.generateReport(txnCode, reportID, data);
		url = report.getLocation();
		url_list.add(url);
		
		String reportURL = PdfUtil.mergePDF(url_list,false);
		
		Date now = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("YYYYMMdd");
		String now_date = sdf.format(now);
		
		notifyClientToDownloadFile(reportURL, now_date+"保單返還簽收單.pdf");
	}
	
	/**
	 * 下載家庭財務安全問卷
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void download_Family (Object body, IPrimitiveMap header) throws JBranchException {
		INS100InputVO inputVO = (INS100InputVO) body;
		
		// 產出pdf
		String url = null;
		String txnCode = "INS100";
		String reportID = "R3";
		ReportIF report = null;
		ReportFactory factory = new ReportFactory();
		ReportDataIF data = new ReportData();
		ReportGeneratorIF gen = factory.getGenerator();		

		List<String> url_list = new ArrayList<String>();
		XmlInfo xmlinfo = new XmlInfo();
		
		if("0".equals(inputVO.getClick())){
			String gender = xmlinfo.getVariable("CRM.CUST_GENDER", inputVO.getGender(), "F3");
			String custIdHidden = hiddenCode(inputVO.getAdd_custId());
			data.addParameter("CUST_ID", custIdHidden);
			data.addParameter("CUST_NAME", inputVO.getCust_name());
			data.addParameter("GENDER", gender);
			data.addParameter("AGE", inputVO.getAge());
		}else{
			int intvalue = Integer.parseInt(inputVO.getClick());
			String custIdHidden = hiddenCode(inputVO.getResultList().get(intvalue-1).get("CUST_ID").toString());
			Object custIdHiddenObj = custIdHidden;
			data.addParameter("CUST_ID", custIdHiddenObj);
			data.addParameter("CUST_NAME", inputVO.getResultList().get(intvalue-1).get("CUST_NAME"));
			String gender = xmlinfo.getVariable("CRM.CUST_GENDER", String.valueOf(inputVO.getResultList().get(intvalue-1).get("GENDER")), "F3");
			data.addParameter("GENDER", gender);
			int agevalue = (int) Float.parseFloat(String.valueOf(inputVO.getResultList().get(intvalue-1).get("INSURED_AGE")));
			data.addParameter("AGE", String.valueOf(agevalue));
		}
	
		report = gen.generateReport(txnCode, reportID, data);
		url = report.getLocation();
		url_list.add(url);
		
		String reportURL = PdfUtil.mergePDF(url_list,false);
		
		Date now = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("YYYYMMdd");
		String now_date = sdf.format(now);
		
		notifyClientToDownloadFile(reportURL, now_date+"家庭財務安全問卷.pdf");
	}
	
	/**
	 * 上傳保單健診同意書
	 * @param body
	 * @param header
	 * @throws Exception 
	 */
	public void uploadAgreeHis(Object body, IPrimitiveMap header) throws Exception{
		INS100InputVO inputVO = (INS100InputVO) body;
		
		//取上傳資料
		String tempPath = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
		byte[] data = Files.readAllBytes(new File(tempPath, inputVO.getFileName()).toPath());
		String realfileName = inputVO.getRealfileName();
		
		uploadAgreeCommon(inputVO.getCustId(), data, realfileName, null);
	}
	
	//======================== WEB 處理========================
	
	//======================== WS 調用 ========================
	/**
	 * ws 調用 getPolicyNumber 取得保單號碼
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	@SuppressWarnings("rawtypes")
	public void getPolicyNumber (Object body, IPrimitiveMap header) throws JBranchException {
		GenericMap inputGmap = new GenericMap((Map) body);
		System.out.println(new Gson().toJson(inputGmap.getParamMap()));
		String custId = inputGmap.getNotNullStr("custId");
		Boolean isFilter = inputGmap.get("isFilter", Boolean.class);
		
		// 取得保單號碼
		List<Map<String, Object>> policyNbrListMap = getPolicyNumberList(custId, null, isFilter);
		Map<String, Object> policyNbrMap = new HashMap<String, Object>();
		
		//存放查詢出來的保單號碼
		List<String> policyNbrList = new ArrayList<String>();
		
		//取得map內的保單編號
		for(Map<String, Object> map : policyNbrListMap){
			policyNbrList.add((String) map.get("POLICY_NBR"));
		}
		
		policyNbrMap.put("custId", custId);
		policyNbrMap.put("policyNbr", policyNbrList);
		System.out.println(new Gson().toJson(policyNbrMap));
		this.sendRtnObject(policyNbrMap);
	}
	
	/**
     * ws 調用 generateAgreement 新增保單號碼 & 下載同意書
     * 提供給 APP 使用的 API <br>
     * <code> <b> 2.5.8 </b> 產生保單健診同意書</code>
     * @param inputVO : custId : 客戶ID, insSeq : 保單號碼
     * @return Object : file : Pdf檔案
     * @throws JBranchException : <br>
     * <p>
     * 		<b>客製訊息如下 :</b>
     * 		<li>查無資料: </li>
     * </p>
     */
	public void generateAgreement (Object body , IPrimitiveMap header) throws Exception {
		Map<String, Object> outputMap = new HashMap<String, Object>(); //用來存放產生出來的保單健診同意書
		
		GenericMap inputGmap = new GenericMap((Map) body);	
		String custId = inputGmap.getNotNullStr("custId");	// 客戶 ID
		List<String> policyNbrs = inputGmap.get("policyNbr"); // 保單號碼清單
		
		// 取得保單號碼
		List<Map<String, Object>> policyNbrListMap = getPolicyNumberList(custId, null, false);
		//存放查詢出來所有的保單號碼
		List<String> policyNbrList = new ArrayList<String>();
		//取得map內的保單編號
		for(Map<String, Object> map : policyNbrListMap){
			policyNbrList.add((String) map.get("POLICY_NBR"));
		}
		
		deleteMultiPolicyData(custId, policyNbrList); //刪除所有原本保單資料
		
		addMultiPolicyData(custId, policyNbrs); // 儲存多筆保單資料
		
		// 合併檔案
		String reportURL = print1Common(custId, "APP"); //合併過後的PDF檔路徑
		String tempPath = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH); //完整到temp的路徑
		File file =  new File(tempPath , reportURL.replaceFirst("^temp.", ""));
		byte[] resultByte =  Files.readAllBytes(file.toPath()); //將PDF轉成byte[]
    	outputMap.put("file", DatatypeConverter.printBase64Binary(resultByte)); // PDF檔案的byte[],使用Base64加密
    	
    	this.sendRtnObject(outputMap);
	}
	
	/**
	 * ws 調用 uploadAgreement 上傳保單健診同意書
     * 提供給 APP 使用的 API <br>
     * <code> <b> 2.5.9 </b> 上傳保單健診同意書</code>
     * @param inputVO : custId : 身分證字號, file : Pdf檔案
     * @return Object : null
	 * @throws Exception 
     */
	@SuppressWarnings("rawtypes")
	public void uploadAgreement(Object body, IPrimitiveMap header) throws Exception{
		GenericMap inputGmap = new GenericMap((Map) body);	
		String custId = inputGmap.getNotNullStr("custId"); // 取得CUST_ID
		byte[] file = DatatypeConverter.parseBase64Binary(inputGmap.getNotNullStr("file")); // 取得file的byte[]，使用base64解密
		String now_date = new SimpleDateFormat("YYYYMMdd").format(new Date());
		String fileName = now_date+"保單健診同意書.pdf";
		uploadAgreeCommon(custId, file, fileName, "APP");
	}
	
	//======================== WS 處理 ========================
	/**
	 * 多筆保單資料新增
	 * @param custId
	 * @param policyNbrs
	 * @throws JBranchException
	 * @throws Exception
	 */
	public void addMultiPolicyData (String custId, List<String> policyNbrs) throws JBranchException, Exception {
		//多筆資料時，無則新增，有則修改
		for(String policyNbr : policyNbrs){			
			addPolicyData(custId, policyNbr);
		}
	}
	
	/**
	 * 多筆保單資料刪除
	 * @param custId
	 * @param policyNbrs
	 * @throws JBranchException
	 * @throws Exception
	 */
	public void deleteMultiPolicyData (String custId, List<String> policyNbrs) throws JBranchException, Exception {
		//多筆資料時，無則新增，有則修改
		for(String policyNbr : policyNbrs){			
			deletePolicyData(custId, policyNbr);
		}
	}

	//======================== 共用 調用 & 處理========================
	/**
	 * 共用統一取得保單號碼
	 * @param custId 客戶 ID
	 * @param policyNr 保單號碼 (如果有提供會包含)
	 * @param isFilter 是否要過濾資料 true 為尚未使用的保單號碼、 false 為全部保單號碼
	 * @return
	 * @throws JBranchException
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getPolicyNumberList  (String custId, String policyNbr, boolean isFilter) throws JBranchException {
		ArrayList<String> loginAO = (ArrayList<String>) getUserVariable(FubonSystemVariableConsts.LOGIN_AOCODE_LIST);
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT POLICY_NBR ");
		sb.append(" FROM TBINS_EXAM_POLICYNBR ");
		sb.append(" WHERE CUST_ID = :custId ");
		
		// 如果需要過濾
		if(isFilter) {
			sb.append(" and POLICY_NBR not in(select POLICY_NBR from TBINS_OUTBUY_MAST where 1=1 and CUST_ID = :custId");
			if(!org.springframework.util.StringUtils.isEmpty(policyNbr)) {
				sb.append(" and POLICY_NBR != :policyNbr "); // 排除自己(當前保單要撈)
				queryCondition.setObject("policyNbr", policyNbr);
			}				
			sb.append(" ) ");
			// 保單輸入時間不能小於 上傳時間
			sb.append(" and CREATETIME < (SELECT CREATETIME FROM TBINS_EXAM_AGREE_HIS WHERE CUST_ID = :custId ");
			sb.append(" AND AO_CODE IN (:loginAO) ");
			sb.append(" ORDER BY CREATETIME DESC FETCH FIRST ROWS ONLY) ");
			queryCondition.setObject("loginAO", loginAO);
		}
					
		queryCondition.setObject("custId", custId);
		queryCondition.setQueryString(sb.toString());
		return dam.exeQuery(queryCondition);
	}
	
	/**
	 * 共用統一進行新增保單
	 * @param custId
	 * @param policyNbr
	 * @throws JBranchException
	 * @throws Exception
	 */
	public int addPolicyData (String custId, String policyNbr) throws JBranchException, Exception {
		dam = this.getDataAccessManager();
		
		//判斷客戶ID下該保單號碼是否存在
		TBINS_EXAM_POLICYNBRPK dPK = new TBINS_EXAM_POLICYNBRPK(custId, policyNbr);
		TBINS_EXAM_POLICYNBRVO vo = new TBINS_EXAM_POLICYNBRVO(dPK);
		vo = (TBINS_EXAM_POLICYNBRVO) dam.findByPKey(TBINS_EXAM_POLICYNBRVO.TABLE_UID, vo.getcomp_id());
		
		int isCreate = (null == vo) ? 0 : 1;	// 0 不存在; 1 已存在;
		
		//若不存在，則新增ㄧ筆
		if (isCreate == 0) {
			vo = new TBINS_EXAM_POLICYNBRVO();
			vo.setcomp_id(dPK);					
			dam.create(vo);
		}
		return isCreate; 		
	}
	
	/**
	 * 共用統一進行刪除保單號碼
	 * @param custId
	 * @param policyNbr
	 * @throws JBranchException
	 * @throws Exception
	 */
	public void deletePolicyData (String custId, String policyNbr) throws JBranchException {
		dam = this.getDataAccessManager();
		
		TBINS_EXAM_POLICYNBRPK dPK = new TBINS_EXAM_POLICYNBRPK(custId, policyNbr);
		TBINS_EXAM_POLICYNBRVO vo = new TBINS_EXAM_POLICYNBRVO(dPK);
		vo = (TBINS_EXAM_POLICYNBRVO) dam.findByPKey(TBINS_EXAM_POLICYNBRVO.TABLE_UID, vo.getcomp_id());
		dam.delete(vo);
	}
	
	/**
	 * 共用統一調用 下載保單健診同意書 產生檔案 URL
	 * @param custId
	 * @return
	 * @throws JBranchException
	 */
	public String print1Common (String custId, String web_app) throws JBranchException {
		 // 產出pdf
		String url = null;
		String txnCode = "INS100";
		String reportID = "R1";
		ReportIF report = null;
		ReportFactory factory = new ReportFactory();
		ReportDataIF data = new ReportData();
		ReportGeneratorIF gen = factory.getGenerator();		
		
		List<String> url_list = new ArrayList<String>();
		
		List<Map<String, Object>> totalList = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> list_1 = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> list_2 = new ArrayList<Map<String, Object>>();
		
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);		
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT POLICY_NBR ");
		sb.append(" FROM TBINS_EXAM_POLICYNBR ");
		sb.append(" WHERE CUST_ID = :custId ");
		sb.append(" ORDER BY LASTUPDATE ");
		queryCondition.setObject("custId", custId);
		queryCondition.setQueryString(sb.toString());
		List<Map<String, Object>> recordList = dam.exeQuery(queryCondition);	
		int m = 1;
		for (Map<String, Object> map : recordList) {
			Map<String, Object> addMap = new HashMap<String, Object>();
			addMap.put("POLICY_NBR", map.get("POLICY_NBR"));
			addMap.put("XXX_NO", m);
			totalList.add(addMap);
			m++;
		}
		
		reportFormat(data, list_1, list_2, totalList);
				
		if (list_1.size() > 0) {
			data.addRecordList("Script Mult Data Set", list_1);
		}
		if (list_2.size() > 0) {
			data.addRecordList("Script Mult Data Set2", list_2);
		}
		
		//判斷是否來自app
		data.addParameter("WEB_APP",web_app);
		
		data.addParameter("PageFoot","第一聯：台北富邦銀行留存");
		report = gen.generateReport(txnCode, reportID, data);
		url = report.getLocation();
		url_list.add(url);
		
		data.addParameter("PageFoot","第二聯：客戶留存");
		report = gen.generateReport(txnCode, reportID, data);
		url = report.getLocation();
		url_list.add(url);
		
		String reportURL = PdfUtil.mergePDF(url_list,true);
		
		return reportURL;
		
	}
	
	/**
	 * 客戶ID隱碼用
	 * @param custId
	 * @return
	 * @throws JBranchException
	 */
	public String hiddenCode (String custId) throws JBranchException{
		String cutIdFront = custId.substring(0, 4);
		String custIdBack = custId.substring(8, 10);
		custId = cutIdFront + "****" + custIdBack;
		return custId;		
	}
	
	/**
	 * 資料庫回來的 ResultImpl 會有 java.lang.unsupportedoperationexception 問題
	 * 處理資料庫回來的資料 List 不能直接在塞資料
	 * @param target
	 * @param list
	 */
	private void doIteratorChange(List<Map<String, Object>> target, List<Map<String, Object>> list) {
		Iterator<Map<String, Object>> iterator = list.iterator();
		while (iterator.hasNext()) {
			target.add(iterator.next());
		}
	}
	
	/**
	 * 共用統一上傳保單健診同意書方法
	 * @param custId
	 * @param data
	 * @throws Exception 
	 */
	@SuppressWarnings("unchecked")
	public void uploadAgreeCommon(String custId, byte[] data, String fileName, String identifier) throws Exception{

		ArrayList<String> loginAO = (ArrayList<String>) getUserVariable(FubonSystemVariableConsts.LOGIN_AOCODE_LIST);
		List<Map<String, Object>> KEYNOList = new ArrayList<Map<String,Object>>();
		
		//取號
		INS810 ins810 = (INS810) PlatformContext.getBean("ins810");
		INS810InputVO ins810inputVO = new INS810InputVO();
		INS810OutputVO ins810outputVO = new INS810OutputVO();
		ins810outputVO = ins810.getInsSeq(ins810inputVO);
		
		dam = getDataAccessManager();
		String AGREE_KEYNO = "AGRE"+ins810outputVO.getInsSeq();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuilder sb = new StringBuilder();
		
		sb.append(" select TBINS_REPORT_SEQ.NEXTVAL from dual ");
		qc.setQueryString(sb.toString());
		KEYNOList = dam.exeQuery(qc);
		
		TBINS_EXAM_AGREE_HISVO teah = new TBINS_EXAM_AGREE_HISVO();
		teah.setAGREE_KEYNO(AGREE_KEYNO);
		teah.setAO_CODE(loginAO.get(0));
		teah.setCUST_ID(custId);
		dam.create(teah);
		
		TBINS_REPORTVO tr = new TBINS_REPORTVO();
		tr.setKEYNO(new BigDecimal(String.valueOf(KEYNOList.get(0).get("NEXTVAL"))));
		tr.setPLAN_ID(AGREE_KEYNO);
		tr.setREPORT_FILE(ObjectUtil.byteArrToBlob(data));
		tr.setFILE_NAME(fileName);
		dam.create(tr);
		
		if("APP".equals(identifier)){
			Map<String,Object> forwardMap = new HashMap<String, Object>();
			forwardMap.put("custId", custId);
			forwardMap.put("planId", AGREE_KEYNO);
			forwardMap.put("fileType", 1.0);
			forwardMap.put("title", "轉寄同意書");
			Object body = (Object)forwardMap;
			
			FPS200Api fps200api = (FPS200Api) PlatformContext.getBean("fps200api");
			fps200api.forwardFile(forwardMap, dam);
		}
		
		sendRtnObject(null);
	}
	
	/**
	 * 格式化 保單健診同意書 (下載 & 返還)
	 * @param data
	 * @param list_1 
	 * @param list_2
	 * @param totalList
	 */
	private void reportFormat(ReportDataIF data, List list_1, List list_2, List totalList) {
		//totalList，隨後加上<以下空白>字樣，再補上空白(預設20格)。
		Map<String, Object> addBlank = new HashMap<String, Object>();		
		addBlank.put("XXX_NO","<以下空白>");
		addBlank.put("POLICY_NBR","<以下空白>");
		totalList.add(addBlank);

		for (int i=0 ; i<totalList.size() ; i++ ) {
			if (i%2 == 0) {
				list_1.add(totalList.get(i));
			} else {
				list_2.add(totalList.get(i));
			}
		}
		
		for (int i=list_1.size();i<10;i++) {
			addBlank = new HashMap<String, Object>();
			addBlank.put("XXX_NO","");
			addBlank.put("POLICY_NBR","");
			list_1.add(addBlank);
		}
		for (int i=list_2.size();i<10;i++) {
			addBlank = new HashMap<String, Object>();
			addBlank.put("XXX_NO","");
			addBlank.put("POLICY_NBR","");
			list_2.add(addBlank);
		}
		
		// 大於20筆要額外處理
		if(totalList.size()>=20) {
			if(list_1.size() != list_2.size()) { // 如果兩邊不一致，一定是補上右邊
				addBlank = new HashMap<String, Object>();
				addBlank.put("XXX_NO","");
				addBlank.put("POLICY_NBR","");
				list_2.add(addBlank);
			}
		}
	}
	
	/**
	 * 提出查詢某客戶的保單健診查詢資料
	 * @param custId
	 * @param LoginBraNbr
	 * @param loginAO
	 * @return  (本人、關係人 all)
	 * @throws JBranchException
	 * @throws ParseException
	 */
	public List<Map<String, Object>> getCustSearchResult(String custId, String LoginBraNbr, ArrayList<String> loginAO) throws JBranchException, ParseException {
		List<Map<String, Object>> dataList = new ArrayList<Map<String,Object>>();
		List<Map<String, Object>> dataListTmp = new ArrayList<Map<String,Object>>();
		List<Map<String, Object>> dataListTmp2 = new ArrayList<Map<String,Object>>();
		List<Map<String, Object>> dataListTmp3 = new ArrayList<Map<String,Object>>();
		List<Map<String, Object>> queryInsCustMast = new ArrayList<Map<String,Object>>();
		List<Map<String, Object>> queryAstInsMast = new ArrayList<Map<String,Object>>();
		
		//1.查詢客戶主檔
		doIteratorChange(dataList, query_CustMast(custId, LoginBraNbr, loginAO));
		//2.查詢非行內客戶 查:TBINS_CUST_MAST				
		doIteratorChange(queryInsCustMast, queryInsCustMast(custId, LoginBraNbr, loginAO));
		//3.查詢要保人下所有保單
		doIteratorChange(queryAstInsMast, query_AstInsMast(custId, LoginBraNbr, loginAO));
		
		Boolean inPut = false;
		//過濾非行內客戶(CUST_ID)
		if (CollectionUtils.isEmpty(dataList)) {
			dataList = queryInsCustMast;
		} else {
			for (Map<String, Object> newMap : queryInsCustMast) {
				Map<String, Object> tmpMap = new HashMap<String, Object>();
				for (Map<String, Object> oldMap : dataList) {				
					if (!newMap.get("CUST_ID").toString().equals(oldMap.get("CUST_ID").toString())) {
						for(String key :newMap.keySet()){
							tmpMap.put(key, newMap.get(key));
	                    }
						inPut = true;
					} else {
						inPut = false;
						break;
					}							
				}
				if (inPut){
					dataListTmp.add(tmpMap);	
				}					
			}
			dataList.addAll(dataListTmp);
		}
		
		//過濾要保人下所有保單(CUST_ID)
		if (CollectionUtils.isEmpty(dataList)) {
			dataList = queryAstInsMast;
		} else {
			for (Map<String, Object> newMap : queryAstInsMast) {
				Map<String, Object> tmpMap = new HashMap<String, Object>();
				for (Map<String, Object> oldMap : dataList) {				
					if (!newMap.get("CUST_ID").toString().equals(oldMap.get("CUST_ID").toString())) {
						for(String key :newMap.keySet()){
							tmpMap.put(key, newMap.get(key));
	                    }
						inPut = true;
					} else {
						inPut = false;
						break;
					}							
				}
				if (inPut){
					dataListTmp2.add(tmpMap);	
				}					
			}
			dataList.addAll(dataListTmp2);
		}			
		
		//計算保險年齡
		INS810 ins810 = (INS810) PlatformContext.getBean("ins810");
		INS810InputVO ins810inputVO = null;
		INS810OutputVO ins810outputVO = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		for(Map<String, Object> map : dataList){
			ins810inputVO = new INS810InputVO();
			ins810outputVO = new INS810OutputVO();
			if(map.get("BIRTH_DATE") != null){
				Date birthday = sdf.parse(String.valueOf(map.get("BIRTH_DATE")));
				ins810inputVO.setBirthday(birthday);
				ins810outputVO = ins810.getAge(ins810inputVO);
				map.put("INSURED_AGE", ins810outputVO.getAge());
			}else{
				map.put("INSURED_AGE", "");
			}				
		}
		
		//4.查詢關係戶
		ins810inputVO = new INS810InputVO();
		ins810inputVO.setCUST_ID(custId);
		ins810inputVO.setLoginAOCode(loginAO);
		ins810inputVO.setLoginBranch(LoginBraNbr);
		
		ins810outputVO = ins810.getFamailyLst(ins810inputVO);
		List<Map<String, Object>> ins810List = ins810outputVO.getGenealogyList();
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		
		if (ins810List != null) {
			for(int i=0; i<ins810List.size();i++) {
				Map<String, Object> resultMap = new HashMap<String, Object>();
				resultMap.put("CUST_ID", ins810List.get(i).get("RELATION_ID"));
				resultMap.put("CUST_NAME", ins810List.get(i).get("RELATION_NAME"));
				resultMap.put("BIRTH_DATE", ins810List.get(i).get("RELATION_BIRTHDAY"));
				resultMap.put("GENDER", ins810List.get(i).get("RELATION_GENDER"));
				resultMap.put("AO", ins810List.get(i).get("AO_CODE"));
				resultMap.put("BRA_NBR", ins810List.get(i).get("BRA_NBR"));
				resultMap.put("REPORT_DATE", ins810List.get(i).get("REPORT_DATE"));
				resultMap.put("SOURCE", "關係戶");
				resultList.add(resultMap);
			}
		}
		
		//計算保險年齡			
		for(Map<String, Object> map : resultList){
			ins810inputVO = new INS810InputVO();
			ins810outputVO = new INS810OutputVO();
			List<Map<String, Object>> tempList = new ArrayList<Map<String, Object>>();
			// 沒有生日的就排掉不要出現 (#0000091)
			if (map.get("BIRTH_DATE") != null) {
				Date birthday = sdf.parse(String.valueOf(map.get("BIRTH_DATE")));
				ins810inputVO.setBirthday(birthday);
				ins810outputVO = ins810.getAge(ins810inputVO);
				map.put("INSURED_AGE", ins810outputVO.getAge());				
				tempList.add(map);
			}
			resultList = tempList;
		}
		
		//過濾關係戶(CUST_ID)
		if (CollectionUtils.isEmpty(dataList)) {
			dataList = resultList;
		} else {
			List<Map<String, Object>> oldArrayList = dataList;
			for (Map<String, Object> newMap : resultList) {
				Map<String, Object> tmpMap = new HashMap<String, Object>();
				for (Map<String, Object> oldMap : oldArrayList) {				
					if (!newMap.get("CUST_ID").toString().equals(oldMap.get("CUST_ID").toString())) {
						for(String key :newMap.keySet()){
							tmpMap.put(key, newMap.get(key));
	                    }
						inPut = true;
					} else {
						inPut = false;
						break;
					}							
				}
				if (inPut){
					dataListTmp3.add(tmpMap);	
				}					
			}
	        dataList.addAll(dataListTmp3);
		}	
		return dataList;
	}
	
	//======================== 客戶相關資料查詢 ========================
	//查詢個人
	public void query(Object body, IPrimitiveMap header) throws JBranchException, ParseException{
		INS100InputVO inputVO = (INS100InputVO) body;
		INS100OutputVO outputVO = new INS100OutputVO();
		String LoginBraNbr = (String) getCommonVariable(SystemVariableConsts.LOGINBRH);
		ArrayList<String> loginAO = (ArrayList<String>) getUserVariable(FubonSystemVariableConsts.LOGIN_AOCODE_LIST);
		outputVO.setResultList(getCustSearchResult(inputVO.getCustId(), LoginBraNbr, loginAO));
		sendRtnObject(outputVO);
	}
	
	//查詢個人
	public List<Map<String, Object>> query_AstInsMast(String custId,String LoginBraNbr,List<String> loginAO){
		
		List<Map<String, Object>> dataList = new ArrayList<Map<String,Object>>();
		dam = getDataAccessManager();
		QueryConditionIF qc = null;
		StringBuffer sb = null;
		
		try {
			qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sb = new StringBuffer();
			
			sb.append(" SELECT DISTINCT  A.INS_ID as CUST_ID, A.INS_NAME as CUST_NAME, ")
			.append(" decode(A.INS_ID, C.CUST_ID, C.BIRTH_DATE, B.BIRTH_DATE) as BIRTH_DATE, ")
			.append(" decode(A.INS_ID, C.CUST_ID, C.GENDER, B.GENDER) as GENDER, '要保人下所有保單' as SOURCE, ")
			.append(" (SELECT CREATETIME FROM TBINS_EXAM_AGREE_HIS ")
			.append(" WHERE CUST_ID = A.INS_ID AND AO_CODE in (:loginAO) ")
			.append(" ORDER BY CREATETIME DESC FETCH FIRST ROWS ONLY) REPORT_DATE ")
			.append(" FROM TBCRM_AST_INS_MAST A ")
			.append(" ,TBCRM_CUST_MAST B, TBCRM_CUST_MAST C ")
			.append(" WHERE A.CUST_ID = B.CUST_ID ")
			.append(" AND A.INS_ID = C.CUST_ID ")
			.append(" AND ( ((B.AO_CODE IS NULL AND B.BRA_NBR = :loginBraNbr) or (B.AO_CODE IS NOT NULL AND B.AO_CODE in (:loginAO))) ")
			.append("   OR  ((C.AO_CODE IS NULL AND C.BRA_NBR = :loginBraNbr) or (C.AO_CODE IS NOT NULL AND C.AO_CODE in (:loginAO))) ) ")
			.append(" and A.CUST_ID = :custId ");
			
//			switch (inputVO.getIdSelect()) {
//			case "1"://查詢要保人下的被保人
//				sb.append(" and A.CUST_ID = :custId ");
//				break;
//			case "2"://查詢被保人
//				sb.append(" and A.INS_ID = :custId ");
//				break;
//			default:
//				break;
//			}	
			
			qc.setObject("custId", custId);
			qc.setObject("loginBraNbr", LoginBraNbr);
			if(loginAO.size()>0){
				qc.setObject("loginAO", loginAO);
			}else{
				qc.setObject("loginAO", null);
			}
			
			qc.setQueryString(sb.toString());
			
			dataList = dam.exeQuery(qc);
//			outputVO.setResultList(dam.exeQuery(qc));
		
		} catch (JBranchException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return dataList;
	}
	
	//查詢關係戶
	public void query_REL(Object body, IPrimitiveMap header) throws JBranchException{
		INS100InputVO inputVO = (INS100InputVO) body;
		INS810 ins810 = (INS810) PlatformContext.getBean("ins810");
		INS810InputVO ins810inputVO = null;
		INS810OutputVO ins810outputVO = null;
		ins810inputVO = new INS810InputVO();
		ins810inputVO.setCUST_ID(inputVO.getCustId());
		ins810inputVO.setLoginAOCode((ArrayList<String>) getUserVariable(FubonSystemVariableConsts.LOGIN_AOCODE_LIST));
		ins810inputVO.setLoginBranch((String) getCommonVariable(SystemVariableConsts.LOGINBRH));
		
		INS100OutputVO outputVO = new INS100OutputVO();
		
		try {
			ins810outputVO = ins810.getFamailyLst(ins810inputVO);
			List<Map<String, Object>> ins810List = ins810outputVO.getGenealogyList();
			List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
			
			for(int i=0; i<ins810List.size();i++) {
				Map<String, Object> resultMap = new HashMap<String, Object>();
				resultMap.put("CUST_ID", ins810List.get(i).get("RELATION_ID"));
				resultMap.put("CUST_NAME", ins810List.get(i).get("RELATION_NAME"));
				resultMap.put("BIRTH_DATE", ins810List.get(i).get("RELATION_BIRTHDAY"));
				resultMap.put("GENDER", ins810List.get(i).get("RELATION_GENDER"));
				resultMap.put("AO", ins810List.get(i).get("AO_CODE"));
				resultMap.put("BRA_NBR", ins810List.get(i).get("BRA_NBR"));
				resultMap.put("REPORT_DATE", ins810List.get(i).get("REPORT_DATE"));
				resultList.add(resultMap);
			}
			
			outputVO.setResultList(resultList);
			
			//計算保險年齡
			if(outputVO.getResultList().size()>0){
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				for(Map<String, Object> map:outputVO.getResultList()){
					ins810inputVO = new INS810InputVO();
					ins810outputVO = new INS810OutputVO();
					Date birthday = sdf.parse(String.valueOf(map.get("BIRTH_DATE")));
					ins810inputVO.setBirthday(birthday);
					ins810outputVO = ins810.getAge(ins810inputVO);
					map.put("INSURED_AGE", ins810outputVO.getAge());
				}
			}
	
			sendRtnObject(outputVO);
		} catch (JBranchException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//查無資料，則查詢客戶主檔
	public List<Map<String, Object>> query_CustMast(String custId,String LoginBraNbr,List<String> loginAO){
		
		List<Map<String, Object>> dataList = new ArrayList<Map<String,Object>>();
		dam = getDataAccessManager();
		QueryConditionIF qc = null;
		StringBuffer sb = null;
		
		try {
			qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sb = new StringBuffer();
			
			sb.append(" SELECT CUST_ID, CUST_NAME, BIRTH_DATE, GENDER, '本人' as SOURCE, ")
				.append(" (SELECT CREATETIME FROM TBINS_EXAM_AGREE_HIS ")
				.append(" WHERE CUST_ID = A.CUST_ID ")
				.append(" AND AO_CODE IN (:loginAO) ")
				.append(" ORDER BY CREATETIME DESC FETCH FIRST ROWS ONLY) REPORT_DATE ")
				.append(" FROM TBCRM_CUST_MAST A ")
				.append(" WHERE CUST_ID = :custid ")
				.append(" AND ((AO_CODE IS NULL AND BRA_NBR = :loginBraNbr) or ")
				.append(" (AO_CODE IS NOT NULL AND AO_CODE in (:loginAO) ) ) ");
			
			if(loginAO.size()>0){
				qc.setObject("loginAO", loginAO);
			}else{
				qc.setObject("loginAO", null);
			}
			
			qc.setObject("custid", custId);
			qc.setObject("loginBraNbr", LoginBraNbr);
			
			qc.setQueryString(sb.toString());
			
			dataList = dam.exeQuery(qc);
			
		} catch (JBranchException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return dataList;
		
	}
	
	// 如果連客戶主檔都沒有資料就可能是非行內客戶 查:TBINS_CUST_MAST 
	private List<Map<String, Object>> queryInsCustMast(String custId, String LoginBraNbr,List<String> loginAO) {
		List<Map<String, Object>> dataList = new ArrayList<Map<String,Object>>();
		dam = getDataAccessManager();
		QueryConditionIF qc = null;
		StringBuffer sb = null;
		
		try {
			qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sb = new StringBuffer();
			sb.append("SELECT INS.CUST_ID, INS.CUST_NAME, INS.BIRTH_DATE, INS.GENDER, '本人' as SOURCE, ");
			sb.append(" (SELECT CREATETIME FROM TBINS_EXAM_AGREE_HIS ");
			sb.append(" WHERE CUST_ID = INS.CUST_ID ");
			sb.append(" AND AO_CODE IN (:loginAO) ");
			sb.append(" ORDER BY CREATETIME DESC FETCH FIRST ROWS ONLY) REPORT_DATE ");
			sb.append(" FROM TBINS_CUST_MAST INS ");
			sb.append("WHERE INS.CUST_ID = :custid ");
			
			if(loginAO.size()>0){
				qc.setObject("loginAO", loginAO);
			}else{
				qc.setObject("loginAO", null);
			}
			
			qc.setObject("custid", custId);
			
			qc.setQueryString(sb.toString());
			
			dataList = dam.exeQuery(qc);
			
		} catch (JBranchException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return dataList;
	}

	
}