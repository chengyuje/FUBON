package com.systex.jbranch.app.server.fps.ins140;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.sql.rowset.serial.SerialException;
import javax.xml.bind.DatatypeConverter;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.ibm.icu.text.SimpleDateFormat;
import com.lowagie.text.DocumentException;
import com.systex.jbranch.app.common.fps.table.TBINS_EXAM_MAINVO;
import com.systex.jbranch.app.common.fps.table.TBINS_REPORTVO;
import com.systex.jbranch.app.server.fps.cmsub302.PrintInsExaminationReportInputVO;
import com.systex.jbranch.app.server.fps.ins141.INS141;
import com.systex.jbranch.app.server.fps.ins142.INS142;
import com.systex.jbranch.app.server.fps.ins146.INS146;
import com.systex.jbranch.app.server.fps.ins147.INS147;
import com.systex.jbranch.app.server.fps.ins810.INS810;
import com.systex.jbranch.app.server.fps.ins810.INS810InputVO;
import com.systex.jbranch.app.server.fps.ins810.INS810OutputVO;
import com.systex.jbranch.app.server.fps.insjlb.InsjlbBusinessInf;
import com.systex.jbranch.app.server.fps.insjlb.vo.DoGetCoverage03InputVO;
import com.systex.jbranch.app.server.fps.insjlb.vo.DoGetCoverage03OutputVO;
import com.systex.jbranch.comutil.collection.GenericMap;
import com.systex.jbranch.comutil.io.JoinDifferentSysBizLogic;
import com.systex.jbranch.fubon.commons.PdfUtil;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.report.ReportIF;
import com.systex.jbranch.platform.common.util.ObjectUtil;
import com.systex.jbranch.platform.common.util.StringUtil;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;
import com.systex.jbranch.ws.external.service.ExternalErrorMsg;

import java.util.Arrays;
import java.util.Collections;

@Component("ins140")
@Scope("request")
public class INS140 extends JoinDifferentSysBizLogic {
	// 連線
	private DataAccessManager dam = null;
	
	@Autowired @Qualifier("insjlb")
	private InsjlbBusinessInf insjlb;
	
	@Autowired @Qualifier("ins810")
	private INS810 ins810;
	
	@Autowired @Qualifier("ins142")
	private INS142 ins142;
	
	@Autowired @Qualifier("ins141")
	private INS141 ins141;
	
	@Autowired @Qualifier("ins146")
	private INS146 ins146;
	
	@Autowired @Qualifier("ins147")
	private INS147 ins147;
	
	enum printType{
		
		P1("policyDetail"),P2("viewStructure"),P3("indYearSum"),P4("familyYear"),P5("viewSum"),P6("familyFeeGap"),P7("familySave");
		
		private String mappingPrintType;
		
		printType(String mappingPrintType){
			this.mappingPrintType = mappingPrintType;
		}
		
		public String getMappingPrintType(){
			return mappingPrintType;
		}
	}
	
	@SuppressWarnings("unchecked")
	public void printPolicyReport (Object body, IPrimitiveMap<Object> header) throws JBranchException, ClassNotFoundException, ParseException, InterruptedException {
		//input vo
		INS140InputVO inputVO = (INS140InputVO) body;
		//合併的清單url
		List<String> allFiles = new ArrayList<String>();
		//回傳其他功能需要參數的map
		Map<String,Object> paramMap = new HashMap<String, Object>();
		
		try {			
			//合併的清單url
			allFiles = getAllFiles(inputVO.getCustID(), inputVO.getSelf(), inputVO.getLstFamily(), inputVO.getItemList());			
			//合併PDF寫入資料庫並傳參數
			paramMap = mergeAndInsert(allFiles, inputVO.getCustID());
			
			notifyClientToDownloadFile(paramMap.get("filePath").toString() , paramMap.get("fileName").toString());
		} 
		catch (Exception e) {
			logger.error(StringUtil.getStackTraceAsString(e));
			throw new APException(e.getMessage());
		} 
	}
		
	/**
     * 提供給 APP 使用的 API <br>
     * <code> <b> 2.5.13 </b> 產生報告書</code>
     * @param inputVO : custId : 客戶ID, family : 家庭關係戶, form : 列印表單
     * @return Object : fileCode : 檔案號碼, file : Pdf檔案
     * @throws JBranchException : <br>
     *                <p>
     *                             <b>客製訊息如下 :</b>
     *                             <li>查無資料: </li>
     *                </p>
     */
	@SuppressWarnings("unchecked")
	public void generateInsuranceReport (Object body, IPrimitiveMap<Object> header) throws JBranchException, ClassNotFoundException, ParseException, InterruptedException {
		//合併的清單url
		List<String> allFiles = new ArrayList<String>();
		//取得傳進來的資料
		GenericMap inputGmap = new GenericMap((Map) body);		
		//取得CUST_ID
		String custId = inputGmap.getNotNullStr("custId");
		//取得所有選取關係戶的身分證字號
		List<String> family = inputGmap.get("family");
		//取得列印表單的代碼
		List<Double> form = inputGmap.get("form");
		//用來接所有關係戶資料的list
		List<Map<String , Object>> relationAllList = new ArrayList<Map<String , Object>>();
		//用來接選取的關係戶資料的list
		List<Map<String , Object>> relationSelectedList = new ArrayList<Map<String , Object>>();
		//判斷是否只有本人
		Boolean self = false; 
		//用來存放產生出來的報告書
		Map outputMap = new HashMap(); 
		//判斷是否有行內外保單
		Boolean hasPolicy = null;
		//判斷是否有進行過財務安全試算
		Boolean hasQuestionnaire = null;
		//回傳其他功能需要參數的map
		Map<String,Object> paramMap = new HashMap<String, Object>();
		
		for(Double d:form) {
			switch(d.intValue()){
			case 1:{}
			case 2:{}
			case 3:{}
			case 4:{}
			case 5:{
				hasPolicy = getHasPolicy(custId);
				break;
			}
			case 6:{}
			case 7:{
				hasQuestionnaire = getHasQuestionnaire(custId);
				break;
			}
			default: break;
			}
		}
		
		if(hasPolicy!=null && hasQuestionnaire!=null && !hasPolicy && !hasQuestionnaire) {
			sendRtnObject(ExternalErrorMsg.getInstance("9900", "客戶並未有在本行建立過保單資料，或攜帶非本行保單進行過健診資料；也並未有進行過 家庭財務安全規劃 相關填寫；因此無法進行 保單健診報告書 與 家庭財務安全報告書 列印操作。"));
			return;
		} else {
			
			if(hasPolicy!=null && !hasPolicy) {
				sendRtnObject(ExternalErrorMsg.getInstance("9901", "客戶並未有在本行建立過保單資料，或攜帶非本行保單進行過健診資料；因此無法進行 保單健診報告書 列印操作"));
				return;
			}
			
			if(hasQuestionnaire!=null && !hasQuestionnaire) {
				sendRtnObject(ExternalErrorMsg.getInstance("9902", "客戶並未有進行過 家庭財務安全規劃 相關填寫；因此無法進行 家庭財務安全報告書 列印操作"));
				return;
			}
			
			if(hasPolicy == null && hasQuestionnaire == null){
				sendRtnObject(ExternalErrorMsg.getInstance("9903", "至少選擇一個項目進行列印，不能不選"));
				return;
			}
		}
		
		
		relationAllList = relationListCommon(custId);
		
		//沒有關係戶時，判斷只有本人
		if(relationAllList.size() == 0){
			self = true;
		}
		//有關係戶時，透過選取關係戶的身分證字號，取得選取關係戶的資料
		else
		{
			boolean contaisCustId = true;
			if(family != null){
				contaisCustId = family.contains(custId);
			}
			 
			if(family == null){
				family = new ArrayList<String>();
				family.add(custId);
			}else if(!contaisCustId){
				family.add(custId);
			}

			for(Map<String , Object>relation : relationAllList){
				for(String familyId : family){
					if(familyId.equals(relation.get("RELATION_ID"))){
						relationSelectedList.add(relation);
					}
				}
			}
		}
		
		//將form轉換成itemList
		List<String> formStrList = new ArrayList<String>();
		
		for(Double value : form){
			formStrList.add(printType.valueOf("P" + value.intValue()).getMappingPrintType());
		}
		
		List<Map<String,Object>> itemList = new ArrayList<Map<String,Object>>();
		
		for(String str : formStrList){
			Map<String,Object> resultMap = new HashMap<String, Object>();
			resultMap.put("key", str);
			resultMap.put("value", "Y");
			itemList.add(resultMap);
		}
						
		try {			
			//合併的清單url
			allFiles = getAllFiles(custId, self, relationSelectedList, itemList);
					
			//合併PDF寫入資料庫並傳參數
			paramMap = mergeAndInsert(allFiles, custId);
			
			byte[] reportData = (byte[]) paramMap.get("reportData");
					
			//fileCode : 每次產生報告書序號+1(與理規共用)
			outputMap.put("fileCode", paramMap.get("examKeyNo"));
			//file : PDF檔案的byte[]，使用Base64加密
			outputMap.put("file", DatatypeConverter.printBase64Binary(reportData));
			
			sendRtnObject(outputMap);
		} 
		catch (Exception e) {
			logger.error(StringUtil.getStackTraceAsString(e));
			throw new APException(e.getMessage());
		} 
	}
	
	/**
	 * 取得關係戶成員
	 * */
	public void relationList (Object body,IPrimitiveMap header) throws JBranchException{
		
		INS140InputVO inputVO = (INS140InputVO) body;
		INS140OutputVO outputVO = new INS140OutputVO();
		List<Map<String , Object>> relationList = new ArrayList<Map<String , Object>>();

		relationList = relationListCommon(inputVO.getCustID());
		
		outputVO.setRelationList(relationList);
		
		sendRtnObject(outputVO);
	}
	
	/**
	 * 取得關係戶成員
	 * */
	//共用
	public List<Map<String , Object>> relationListCommon (String custId) throws JBranchException{

		INS810InputVO ins810InputVO = new INS810InputVO();
		INS810OutputVO ins810OutputVO = new INS810OutputVO();
		List<Map<String , Object>> relationList = new ArrayList<Map<String , Object>>();
		DataAccessManager dam = getDataAccessManager();
		
		ins810InputVO.setLoginAOCode(getLoginAoCode());
		ins810InputVO.setCUST_ID(custId);
		ins810InputVO.setLoginBranch(getLoginBranch());
		ins810InputVO.setIsCallCoverage(true); // 資訊源要
		ins810OutputVO = getIns810().getFamailyLst(ins810InputVO);
		
		relationList = ins810OutputVO.getGenealogyList();
		
		for(Map<String, Object> relationMap : relationList) {
			ins810InputVO.setBirthday((Date)relationMap.get("RELATION_BIRTHDAY"));
			relationMap.put("RELATION_INSAGE", getIns810().getAge(ins810InputVO).getAge());
			
			// 暫時先加在這裡再討論 是否提到 getIns810().getFamailyLst
			// relationMap.put("RELATION_GENDER", "1".equals(relationMap.get("RELATION_GENDER")) ? "M" : "2".equals(relationMap.get("RELATION_GENDER")) ? "F" : "");
		}
		return relationList;
	}
	
	/**
	 * 進行健診資料確認 & 財務規安全規劃確認
	 * 傳出兩個參數 new boolean() {有無保單資料(行內外), 是否填過財務安全問卷}
	 * 如果沒有任何行內或行外保單
	 * 		1. [保單健診報告書] 下的 6 個 tick 全部不能勾選
	 * 		2. 跳出一般訊息: 沒有任何保單，無法列印[保單健診報告書]
	 * 
	 * 如果沒有做過[家庭財務安全規劃]
	 * 		1. [家庭財務安全報告書] 下的 3 個 tick 全部不能勾選
	 * 		2. 跳出一般訊息: 沒有規劃家庭財務安全，無法列印[家庭財務安全報告書]
	 * @param body
	 * @param header
	 * @throws JBranchException 
	 * @throws DAOException 
	 */
	public void checkCustPrint(Object body,IPrimitiveMap header) throws DAOException, JBranchException {
		INS140InputVO inputVO140 = (INS140InputVO) body;
		sendRtnObject(new Boolean[]{getHasPolicy(inputVO140.getCustID()), getHasQuestionnaire(inputVO140.getCustID())});
	}
	
	/**
	 * 取得是否有行內外保單
	 * @return
	 * @throws JBranchException 
	 */
	private boolean getHasPolicy(String custId) throws JBranchException {
		boolean hasPolicy = false;
		String loginBraNbr = (String) getCommonVariable(SystemVariableConsts.LOGINBRH);
		ArrayList<String> loginAO = (ArrayList<String>) getUserVariable(FubonSystemVariableConsts.LOGIN_AOCODE_LIST);
		INS810InputVO ins810InputVO = new INS810InputVO();
		ins810InputVO.setCUST_ID(custId);
		ins810InputVO.setLoginAOCode(loginAO);
		ins810InputVO.setLoginBranch(loginBraNbr);
		List result = getIns810().queryInOutBuyMutiPolicy(ins810InputVO);
		if(CollectionUtils.isNotEmpty(result) && result.size() > 0) {
			hasPolicy = true;
		}
		return hasPolicy;
	}
	
	/**
	 * 取得是否有進行過財務安全試算
	 * @return
	 * @throws JBranchException 
	 * @throws DAOException 
	 */
	private boolean getHasQuestionnaire(String custId) throws DAOException, JBranchException {
		boolean hasQuestionnaire = false;
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = null;
		StringBuffer sbfSql = null;
		
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sbfSql = new StringBuffer();
		sbfSql.append(" SELECT COUNT(CUST_ID) AS COUNT FROM TBINS_CUST_FAMILY_DATA WHERE CUST_ID = :custId");						
		queryCondition.setObject("custId", custId);
		queryCondition.setQueryString(sbfSql.toString());
		List result = dam.exeQuery(queryCondition);
		if(CollectionUtils.isNotEmpty(result)) {
			BigDecimal count = new GenericMap((Map<String, Object>)result.get(0)).getBigDecimal("COUNT");
			if(count.compareTo(BigDecimal.ZERO) == 1) {
				hasQuestionnaire = true;
			} 
		}
		return hasQuestionnaire;
	}
	
	/**
	 * 檢查Map取出欄位是否為Null
	 */
	private String checkIsNull(Map<String, Object> map, String key) {
		if (StringUtils.isNotBlank(String.valueOf(map.get(key))) && null != map.get(key)) {
			return String.valueOf(map.get(key));
		} else {
			return "";
		}
	}
	
	//取得合併的清單url
	public List<String> getAllFiles(String custId, Boolean self, List<Map<String , Object>> lstFamily, List<Map<String , Object>> itemList) throws Exception {
		//取db連線操控物件
		DataAccessManager dam = this.getDataAccessManager();
		//資訊源相關服務
		InsjlbBusinessInf insjlbService = getInsjlb();
		//保單資料查詢相關
		INS810 ins810Service = getIns810();
		//取行內外保單資訊
		INS810InputVO ins810InputVO = new INS810InputVO();
		//資訊源回傳資料
		DoGetCoverage03OutputVO doGetCoverage03OutputVO = null;
		//產生後的報表對應
		Map<String , List<String>> reportNumMap = new HashMap<String , List<String>>();
		//將報表排序的key
		List<String> keys = new ArrayList<String>();
		//查詢可視保單清單
		List<Map<String , Object>> allCustData = new ArrayList<Map<String , Object>>();
		//合併的清單url
		List<String> allFiles = new ArrayList<String>();
		
		//本人
		if(self) {
			allCustData.add(new GenericMap()
				.put("CUST_ID", custId)
				.put("AO", getLoginAoCode())
				.put("BRANCH", getLoginBranch())
				.getParamMap());
		}
		//家庭戶清單
		else if(CollectionUtils.isNotEmpty(lstFamily)){
			for(Map<String , Object> familyMap : lstFamily){
				allCustData.add(new GenericMap()
					.put("CUST_ID", familyMap.get("RELATION_ID"))
					.put("AO", familyMap.get("AO_CODE"))
					.put("BRANCH", familyMap.get("BRA_NBR"))
					.getParamMap()
				);
			}
		}
		
		//查詢保單資料
		ins810InputVO.setInOutBuyMutiPolicyList(allCustData);
		List lstData = ins810Service.queryInOutBuyMutiPolicy(ins810InputVO);
		
		Integer idx = 0;
//		logger.info(new Gson().toJson(lstData));
		for (Map<String, Object> map : itemList) {
			String reportType = ObjectUtils.toString(map.get("key")) + ObjectUtils.toString(map.get("value"));
			
			// 現有保單一覽表
			if ("policyDetailY".equals(reportType)) {
				ReportIF report_INS141 = getIns141().printReport(lstData);
				
				if(report_INS141 != null && StringUtils.isNotBlank(report_INS141.getLocation())){
					reportNumMap.put("INS141" , Arrays.asList(new String[]{report_INS141.getLocation()}));
				}
			} 
			else if("familyFeeGapY".equals(reportType)){
				String url146 = getIns146().printReport(custId, this.uuid, this.conversation);
				reportNumMap.put("INS146" , Arrays.asList(new String[]{url146}));
			}
			else if("familySaveY".equals(reportType)){
				String url147 = getIns147().printReport(custId, this.uuid, this.conversation);
				reportNumMap.put("INS147" , Arrays.asList(new String[]{url147}));
			}
			else {
				//判斷是否已取過資訊源資料，有抓過就不需重複抓
				if(doGetCoverage03OutputVO == null){
					DoGetCoverage03InputVO doGetInputVO = new DoGetCoverage03InputVO();
					// 客戶ID
					doGetInputVO.setInsCustID(custId);		
					// 呼叫類型(1: 要保人'得到全家',2: 被保險人)
					doGetInputVO.setCallType("1");						
					// 是否包含非家庭戶
					doGetInputVO.setIncludeAll(CollectionUtils.isNotEmpty(lstFamily) ? "Y" : "N");
					// 客戶保單資訊
					doGetInputVO.setLstInsData(lstData);				
					// 家庭戶客戶id清單
					doGetInputVO.setLstFamily(lstFamily);	
					// 是否存檔(Y: 存檔,N: 不存檔,空白/null：亦不存檔
					doGetInputVO.setDoSave("Y");
					
					if(CollectionUtils.isEmpty(doGetInputVO.getLstFamily())){
						QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
						queryCondition.setQueryString(
							" SELECT CUST_ID , CUST_ID RELATION_ID , CUST_NAME , BIRTH_DATE RELATION_BIRTHDAY, GENDER RELATION_GENDER FROM TBCRM_CUST_MAST WHERE CUST_ID =:custId "
						);
						
						queryCondition.setObject("custId" , custId);
						List result = dam.exeQuery(queryCondition);
						
						doGetInputVO.setLstFamily(result);
					}
					
					//取資訊源資料
					doGetCoverage03OutputVO = insjlbService.doGetCoverage03(doGetInputVO);
					
					//檢核判斷，若有錯誤則跳回頁面顯示錯誤信息
					System.out.println(doGetCoverage03OutputVO.getLstLogTable());
					if (CollectionUtils.isNotEmpty(doGetCoverage03OutputVO.getLstLogTable())) {
						List<Map<String,Object>> errorMsg = doGetCoverage03OutputVO.getLstLogTable();
						String tableName = "TBSYSI18N";
						String[] columns = {"TEXT"};
						
						for(Map<String,Object> errorMap : errorMsg){
							System.out.println(errorMap);
							Map<String,Object> conditions = new HashMap<String, Object>();
							conditions.put("CODE", ObjectUtils.toString(errorMap.get("logStr")));
							System.out.println(conditions);
							List<Map<String,Object>> msgList = getDBList(dam, tableName, columns, conditions);
							System.out.println(msgList);
							if(CollectionUtils.isNotEmpty(msgList)){
								errorMap.put("logStr", ObjectUtils.toString(msgList.get(0).get("TEXT")));								
							}
						}
											
						throw new Exception(errorMsg.toString());
					}
				}
				
				PrintInsExaminationReportInputVO reportVo = new PrintInsExaminationReportInputVO();
				reportVo.setInsCustId(custId);	// 客戶ID
				reportVo.setLstFamily(lstFamily);	// 家庭戶ID清單
				String type = null;
				
				//列印報表的FLAG
				reportType = ObjectUtils.toString(map.get("key")) + ObjectUtils.toString(map.get("value"));
				
				if("viewStructureY".equals(reportType)){
					type = "INS142";// 保障項目彙總表 
					reportVo.setViewStructure(true);
				}
				else if("indYearSumY".equals(reportType)){
					type = "INS143";// 個人保險保障彙總表
					reportVo.setIndYearSum(true);
				}
				else if("familyYearY".equals(reportType)){
					type = "INS144";// 家庭保險保障彙總表
					reportVo.setFamilyYear(true);
				}
				else if("viewSumY".equals(reportType)){
					type = "INS145";// 保障項目明細表
					reportVo.setViewSumAll(true);
				}
				
				if(StringUtils.isNotBlank(type)){
					String [] ins142ReportTmp = ins142.insReportCreate(this , reportVo , doGetCoverage03OutputVO);
					
					if(!ArrayUtils.isEmpty(ins142ReportTmp)){
						reportNumMap.put(type , Arrays.asList(ins142ReportTmp));
					}
				}
			}
		}
		
		if(CollectionUtils.isEmpty(reportNumMap.keySet())){
			throw new JBranchException("report file is empty");
		}

		for(String key : reportNumMap.keySet()){
			keys.add(key);
		}
		
		Collections.sort(keys);
		allFiles.add("doc//INS//INS000_COVER.pdf");
		for(String key : keys){
			allFiles.addAll(reportNumMap.get(key));
		}
		
		return allFiles;		
	}
	
	//合併PDF寫入資料庫並傳參數
	public Map<String,Object> mergeAndInsert(List<String> allFiles, String custId) throws JBranchException, IOException, SerialException, SQLException, DocumentException{
		//取db連線操控物件
		DataAccessManager dam = this.getDataAccessManager();
		//完整到temp的路徑
		String sysPath = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
		//要寫入的blob的byte陣列
		byte[] reportData = null;
		//合併的檔案位置
		String filePath = null;
		//產生檔名
		String now_date = new SimpleDateFormat("YYYYMMdd").format(new Date());
		String fileName = now_date + "保單健診報告書.pdf";
		//合併後檔案位置
		File reportFile = null;
		//TBINS_EXAM_MAIN的序號
		String examKeyNo = null;
		//回傳其他功能需要參數的map
		Map<String,Object> paramMap = new HashMap<String, Object>();
				
		//合併的檔案位置
//		filePath = PdfUtil.mergePDF(allFiles , false);
		
		// 合併所有PDF＆加密碼
		filePath = PdfUtil.mergePDF(allFiles, custId);
		
		//合併檔案
		reportFile =  new File(sysPath , filePath.replaceFirst("^temp.", ""));
		// 新增or修改健診報告書主檔
		reportData = Files.readAllBytes(reportFile.toPath());
		
		TBINS_EXAM_MAINVO mainVO = new TBINS_EXAM_MAINVO();
		mainVO.setEXAM_KEYNO(examKeyNo = ins810.doGetInsSeqBefore("EXAM"));
		mainVO.setPRINT_YN("Y");
		mainVO.setPRINT_DATE(new Timestamp(new Date().getTime()));
		mainVO.setCUST_ID(custId);
		mainVO.setEXAM_DATE(new Timestamp(new Date().getTime()));
		dam.create(mainVO);
		
		// 新增保險規劃健診報表檔
		TBINS_REPORTVO vo = new TBINS_REPORTVO();
		vo.setKEYNO(new BigDecimal(ins810.doGetSeq("TBINS_REPORT_SEQ")));
		vo.setPLAN_ID(examKeyNo);
		vo.setREPORT_FILE(ObjectUtil.byteArrToBlob(reportData));
		vo.setFILE_NAME(fileName);
		dam.create(vo);
		
		paramMap.put("filePath", filePath);
		paramMap.put("fileName", fileName);
		paramMap.put("reportData", reportData);
		paramMap.put("examKeyNo", examKeyNo);
		
		return paramMap;		
	}
	
	//共用查出table資訊的list
	public List<Map<String, Object>> getDBList(DataAccessManager dam, String tableName, String[] columns, Map<String, Object> conditions) throws DAOException, JBranchException {
		QueryConditionIF queryCondition = null;
		StringBuilder sb = new StringBuilder();
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sb.append(" SELECT ");
		int i = 0;
		for(String column : columns) {
			if(i == 0) {
				sb.append(column + " ");
			} else {
				sb.append(", " + column + " ");
			}
			i+=1;
		  }
		sb.append(" FROM ").append(tableName);
		sb.append(" WHERE 1=1 ");
		  
		for(Entry<String, Object> entry : conditions.entrySet()) {
			sb.append(" AND " + entry.getKey() + " = :" +entry.getKey() + " ");
			queryCondition.setObject(entry.getKey(), entry.getValue());
		  }
		queryCondition.setQueryString(sb.toString());
		
		System.out.println(queryCondition);
		
		System.out.println(dam.exeQuery(queryCondition));
		
		return dam.exeQuery(queryCondition);
	}
	
	//getter & setter
	public InsjlbBusinessInf getInsjlb() {
		return insjlb;
	}

	public void setInsjlb(InsjlbBusinessInf insjlb) {
		this.insjlb = insjlb;
	}

	public INS810 getIns810() {
		return ins810;
	}

	public void setIns810(INS810 ins810) {
		this.ins810 = ins810;
	}

	public INS142 getIns142() {
		return ins142;
	}

	public void setIns142(INS142 ins142) {
		this.ins142 = ins142;
	}

	public INS141 getIns141() {
		return ins141;
	}

	public void setIns141(INS141 ins141) {
		this.ins141 = ins141;
	}
	
	public INS146 getIns146() {
		return ins146;
	}
	
	public void setIns146(INS146 ins146) {
		this.ins146 = ins146;
	}

	public INS147 getIns147() {
		return ins147;
	}

	public void setIns147(INS147 ins147) {
		this.ins147 = ins147;
	}	
	
}
