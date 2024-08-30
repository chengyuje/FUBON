package com.systex.jbranch.app.server.fps.crm999;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Blob;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.util.CellRangeAddress;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.fubon.jlb.DataFormat;
import com.systex.jbranch.platform.common.dataManager.DataManager;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.query.ResultIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.StringUtil;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * MENU
 * 
 * @author Eli
 * @date 2017/08/17
 * @spec 
 */
 
@Component("crm999")
@Scope("request")
@SuppressWarnings({ "rawtypes", "unchecked" })
public class CRM999 extends FubonWmsBizLogic {
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(CRM999.class);
	
	//檔案位置
	private File tempFile = new File (DataManager.getRealPath() ,"temp//reports");
	private static final String filePath;
	
	//路徑
	private static final String TmpPath = "temp/";
	private static final String TmpReportPath = "temp/reports/";
	
	//日期格式化物件
	private static final SimpleDateFormat dateFormatOfSlash = new SimpleDateFormat("yyyy/MM/dd");
	private static final SimpleDateFormat dateFormatOfNoSlash = new SimpleDateFormat("yyyyMMdd");
	private static final SimpleDateFormat dateFormatOfStandardSecond = new SimpleDateFormat("yyyyMMddHHmmss");
	
	static {
		String tmpPath = "";
		try {
			tmpPath = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
		} catch (JBranchException e) {
			e.printStackTrace();
		} finally {
			filePath = tmpPath;
		}
	}
	
	
	/**
	 * 轉入客訴資料庫管理功能-查詢畫面 INIT VIEW
	 * 取得登入者的身份
	 * @param body
	 * @param header
	 * @return void
	 * @throws Exception 
	 * @see CRM999#isA01
	 * @see CRM999#isLevel1
	 * @see CRM999#isLevel2
	 * @see CRM999#isLevel3
	 * @see CRM999#isPreLevel
	 */
	public void start(Object body, IPrimitiveMap<?> header) throws Exception {
		CRM999InputVO inputVO  = (CRM999InputVO) body;
		CRM999OutputVO returnVO = new CRM999OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		
		String account = (String)getUserVariable(FubonSystemVariableConsts.LOGINID);
		//取得登入者的身份
		sql.append("select case when INSTR(a.DEPT_ID, '806') > 0 then '806' else a.DEPT_ID end TERRITORY_ID, C.SA_JOB_TITLE_ID ");
		sql.append("from WMSUSER.TBORG_MEMBER A ");
		sql.append("left join TBORG_MEMBER_ROLE B on A.EMP_ID = B.EMP_ID ");
		sql.append("left join TBORG_ROLE_MAPPING C on B.ROLE_ID = C.ROLE_ID ");
		sql.append("where A.EMP_ID = ?");
		queryCondition.setString(0, account);
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> qryLst = dam.exeQuery(queryCondition);
		
		List<Object> jobTitleList = new ArrayList<>();
		List<Object> territoryList = new ArrayList<>();
		for(Map<String, Object> map : qryLst ) {
			jobTitleList.add(map.get("SA_JOB_TITLE_ID"));
			territoryList.add(map.get("TERRITORY_ID"));
		}
		String jobTitle = getUserPsSaJobTitle(jobTitleList);
		String territory =(String) territoryList.get(jobTitleList.indexOf(jobTitle));
	
		//Ray	201305280040-01	開放區督導可建客訴單
		jobTitle = "B05".equals(jobTitle) ? "B01" : "C05".equals(jobTitle) ? "C01" : "A12".equals(jobTitle) ? "A01" : jobTitle;
	
		if (null == jobTitle || null == territory) {
			throw new APException("登入者資料錯誤，無法使用客訴功能！");
		} else {
			inputVO.setTerritoryId(territory);
			inputVO.setJobTitleId(jobTitle);
		}
		
		/**
		 * 客訴流程分四種 [2015-06-11 不要看]
		 * 1. 金服/作業主管 -> 理財督導 -> 區部長 -> 通路
		 * 2. 金服/作業主管 -> 作業督導 -> 作管
		 * 3. P1業助 -> 總督導 -> 區部長 -> 通路
		 * 4. 客服組長 -> 客服科長 -> 客服部長
		 */
		String jobLevel = "";
		if (isLevel1(jobTitle) || isPreLevel(jobTitle) || isOP01(jobTitle)) {
			jobLevel = "1";	
			inputVO.setBranchId(territory);
		} else if (isLevel2(jobTitle)) {
			jobLevel = "2";
		} else if (isLevel3(jobTitle)) {
			jobLevel = "3";
		}
		inputVO.setJobLevel(jobLevel);
	
		inputVO.setIsA01(isA01(jobTitle) ? "Y" : "N");
	
		//Ray	201305280040-01	開放區督導可建客訴單
		inputVO.setCanAdd(("1".equals(jobLevel) && !"B02".equals(jobTitle)) || isA01(jobTitle) || "B01".equals(jobTitle) ? "Y" : "N");
	
		//總行才可執行退件動作
		inputVO.setRejectFlag(isA01(jobTitle) ? "Y" : "N");
	
		System.out.println("territory:" + territory + " jobTitle:" + jobTitle + " jobLevel:" + jobLevel + " IsA01:" + inputVO.getIsA01() + " CanAdd:" + inputVO.getCanAdd());
		
		returnVO.setParamVO(inputVO);
		this.sendRtnObject(returnVO);
	}

	/**
	 * 轉入客訴資料建立畫面
	 * @param body
	 * @param header
	 * @return void
	 * @throws Exception 
	 * @see CRM999#setA01List
	 */
	public void add(Object body, IPrimitiveMap<?> header) throws Exception {
		CRM999InputVO inputVO  = (CRM999InputVO) body;
		CRM999OutputVO returnVO = new CRM999OutputVO();
		
		System.out.println("ISA01:" + inputVO.getIsA01());
		if ("Y".equals(inputVO.getIsA01())) {
			List a01List = setA01List(inputVO);
			
			inputVO.setComplainTypeList((List)a01List.get(0));
			inputVO.setComplainModeList((List)a01List.get(1));
			inputVO.setComplainRequestList((List)a01List.get(2));
			inputVO.setHandleStatusList((List)a01List.get(3));
			inputVO.setAddByA01("Y");
		}
		inputVO.setIs806("806".equals(inputVO.getTerritoryId()) ? "Y" : "N");
		inputVO.setIsA01(isA01(inputVO.getJobTitleId()) ? "Y" : "N");
		inputVO.setMyCase("Y");
		inputVO.setHandleStep("1");
		inputVO.setJobLv("1");//ADD新增流程時，jobLV固定設為1
		returnVO.setParamVO(inputVO);
		
		this.sendRtnObject(returnVO);
	}

	/**
	 * 取出該筆欲修改客訴資料表 [editInit]<br>
	 * Rex 201409090273-00 組織調整修改客訴資料庫內容
	 * @param body
	 * @param header
	 * @return void
	 * @throws Exception 
	 * @see CRM999#queryCustomerComplainList
	 * @see CRM999#isNotNull
	 * @see CRM999#setA01List
	 * @see CRM999#fileIsExist
	 */
	public void update(Object body, IPrimitiveMap<?> header) throws Exception {
		CRM999InputVO inputVO  = (CRM999InputVO) body;
		CRM999OutputVO returnVO  = new CRM999OutputVO();
	
		try {
			List resultList = queryCustomerComplainList(inputVO);
			
			Map m = null;
			if(resultList.size()!=0){
				 m = (Map)resultList.get(0);
			}else{
				returnVO.setErrorMsg("查無該筆客訴表!!");
				this.sendRtnObject(returnVO);
				return;
			}
			
			inputVO.setFlowType(isNotNull(m.get("FLOW")));
			inputVO.setComplainListId(ObjectUtils.defaultIfNull( m.get("COMPLAIN_LIST_ID"),"").toString());
			inputVO.setBranchId(ObjectUtils.defaultIfNull( m.get("BRANCH_ID"),"").toString());
			inputVO.setGrade(ObjectUtils.defaultIfNull( m.get("GRADE"),"").toString());
			inputVO.setComplainSource(ObjectUtils.defaultIfNull( m.get("COMPLAIN_SOURCE"),"").toString());
			inputVO.setComplainTypeSel(ObjectUtils.defaultIfNull( m.get("COMPLAIN_TYPE_SEL"),"").toString());
			inputVO.setOtherSource(ObjectUtils.defaultIfNull( m.get("OTHER_SOURCE"),"").toString());
			inputVO.setComplainSummary(ObjectUtils.defaultIfNull( m.get("COMPLAIN_SUMMARY"),"").toString());
			inputVO.setCustId(ObjectUtils.defaultIfNull( m.get("CUST_ID"),"").toString());
			inputVO.setCustName(ObjectUtils.defaultIfNull( m.get("CUST_NAME"),"").toString());
	
			if(m.get("BIRTHDATE") != null) {
				inputVO.setBirthday(dateFormatOfSlash.parse(dateFormatOfSlash.format(m.get("BIRTHDATE"))));
			}
			inputVO.setOccup(ObjectUtils.defaultIfNull( m.get("OCCUP"),"").toString());
			inputVO.setPhone(ObjectUtils.defaultIfNull( m.get("PHONE"),"").toString());
			inputVO.setEducation(ObjectUtils.defaultIfNull( m.get("EDUCATION"),"").toString());
	
			if(m.get("OPEN_ACC_DATE") != null) {
				inputVO.setOpenAccountDate(dateFormatOfSlash.parse(dateFormatOfSlash.format(m.get("OPEN_ACC_DATE"))));
			}
	
			inputVO.setTotalAsset(ObjectUtils.defaultIfNull( m.get("TOTAL_ASSET"),"").toString());
			inputVO.setCheckSheet(ObjectUtils.defaultIfNull( m.get("CHECK_SHEET"),"").toString());
			inputVO.setPersonId(ObjectUtils.defaultIfNull( m.get("PERSON_ID"),"").toString());
			inputVO.setPersonName(ObjectUtils.defaultIfNull( m.get("PERSON_NAME"),"").toString());
			inputVO.setAOcode(ObjectUtils.defaultIfNull( m.get("AO_CODE"),"").toString());
			inputVO.setServiceStatus(ObjectUtils.defaultIfNull( m.get("SERVICE_STATUS"),"").toString());
			inputVO.setBuyProductType(ObjectUtils.defaultIfNull( m.get("BUY_PRODUCT_TYPE"),"").toString());
	
			if(m.get("BUY_DATE") != null) {
				inputVO.setBuyDate(dateFormatOfSlash.parse(dateFormatOfSlash.format(m.get("BUY_DATE"))));;
			}
			
			inputVO.setComplainProduct(ObjectUtils.defaultIfNull( m.get("COMPLAIN_PRODUCT"),"").toString());
			inputVO.setComplainProductCurrency(ObjectUtils.defaultIfNull( m.get("COMPLAIN_PRODUCT_CURRENCY"),"").toString());
			inputVO.setComplainProductAmount(ObjectUtils.defaultIfNull( m.get("COMPLAIN_PRODUCT_AMOUNT"),"").toString());
			inputVO.setProblemDescribe(ObjectUtils.defaultIfNull( m.get("PROBLEM_DESCRIBE"),"").toString());
			inputVO.setCustDescribe(ObjectUtils.defaultIfNull( m.get("CUST_DESCRIBE"),"").toString());
			inputVO.setHandleCondition1(ObjectUtils.defaultIfNull( m.get("HANDLE_CONDITION1"),"").toString());
			inputVO.setHandleCondition2(ObjectUtils.defaultIfNull( m.get("HANDLE_CONDITION2"),"").toString());
			inputVO.setHandleCondition3(ObjectUtils.defaultIfNull( m.get("HANDLE_CONDITION3"),"").toString());
			inputVO.setComplainType(ObjectUtils.defaultIfNull( m.get("COMPLAIN_TYPE"),"").toString());
			inputVO.setComplainMode(ObjectUtils.defaultIfNull( m.get("COMPLAIN_MODE"),"").toString());
			inputVO.setComplainRequest(ObjectUtils.defaultIfNull( m.get("COMPLAIN_REQUEST"),"").toString());
			inputVO.setHandleStatus(ObjectUtils.defaultIfNull( m.get("HANDLE_STATUS"),"").toString());
			inputVO.setHandleStep(ObjectUtils.defaultIfNull( m.get("HANDLE_STEP"),"").toString());
			
			if(m.get("FMT_CREATE_DATE") != null) {
				inputVO.setFmt_create_date(dateFormatOfSlash.parse(m.get("FMT_CREATE_DATE").toString()));;
			}
			if(m.get("FMT_END_DATE") != null) {
				inputVO.setFmt_end_date(dateFormatOfSlash.parse(m.get("FMT_END_DATE").toString()));;
			}
	
			if ("Y".equals(inputVO.getIsA01())) {
				inputVO.setMyCase("Y");
			} else {
				inputVO.setMyCase(isNotNull(m.get("MYCASE")));
			}
	
			//目前處理層級
			inputVO.setJobLevel(isNotNull(m.get("HANDLE_STEP")));;
			
			//前端控管權限用 => myCaseOtherFlag
			inputVO.setJobLv(isNotNull(m.get("HANDLE_STEP")));
			
			//總行人員才可維護客訴型態區塊
			if ("Y".equals(inputVO.getIsA01())) {
				List a01List = setA01List(inputVO);
				
				inputVO.setComplainTypeList((List)a01List.get(0));
				inputVO.setComplainModeList((List)a01List.get(1));
				inputVO.setComplainRequestList((List)a01List.get(2));
				inputVO.setHandleStatusList((List)a01List.get(3));
			}
	
			//第三級處理人員預設文字
			//Rex 201409090273-00 組織調整修改客訴資料庫內容
			String defaultStr = "";
			System.out.println("HANDLE_STEP:" + isNotNull(m.get("HANDLE_STEP")));
			System.out.println("HANDLE_CONDITION3:" + isNotNull(m.get("HANDLE_CONDITION3")));
			System.out.println("JobTitleId:" + inputVO.getJobTitleId());
			if ("3".equals(isNotNull(m.get("HANDLE_STEP"))) && "".equals(isNotNull(m.get("HANDLE_CONDITION3")))) {
				if("3".equals(inputVO.getGrade())){
					if (isA01(inputVO.getJobTitleId()) || "P01".equals(inputVO.getJobTitleId())) {
						defaultStr = "處主管:\n\n\n總行人員:";
					} else if (isA01(inputVO.getJobTitleId()) || "B04".equals(inputVO.getJobTitleId())) {
						defaultStr = "私人銀行部部長:\n\n\n總行人員:";
					} else if ("CS01".equals(inputVO.getJobTitleId())) {
						defaultStr = "客服部部長:\n\n\n";
					}
					inputVO.setHandleCondition3(defaultStr);
				}
			}
	
			if (fileIsExist(isNotNull(inputVO.getComplainListId()))){  //投資明細表
				inputVO.setUploadFile("1");
			} else {
				inputVO.setUploadFile("0");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		returnVO.setParamVO(inputVO);
		this.sendRtnObject(returnVO);
	}

	/**
	 * 客訴資料表-退件 backInit
	 * @param body
	 * @param header
	 * @return void
	 * @throws Exception 
	 * @see CRM999#isNotNull
	 */
	public void backStep(Object body, IPrimitiveMap<?> header) throws Exception {
		CRM999InputVO inputVO  = (CRM999InputVO) body;
		CRM999OutputVO returnVO = new CRM999OutputVO();

		backStep1(inputVO);
		backStep2(inputVO);
		
		returnVO.setParamVO(inputVO);
		this.sendRtnObject(returnVO);
	}

	private void backStep1(CRM999InputVO inputVO) throws DAOException, JBranchException {
		//取得上一流程資訊
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = null;
		String listId = isNotNull(inputVO.getComplainListId());
		String flow  = isNotNull(inputVO.getFlowType());
		
		StringBuffer sqlBuffer = new StringBuffer();
		String lastOrgStr = "無";	//預設上一流程資訊-ID
		String lastNameStr = "無";	//預設上一流程資訊-姓名
		String nowEmpStr = "";		//目前使用負責人
		if(!("".equals(listId))) {
			sqlBuffer.append("select BRANCH_ID, HANDLE_STEP, EDITOR_CONDITION1, EDITOR_CONDITION2, EDITOR_CONDITION3, ")
					 .append("EDITOR_CONDITION4, EDITOR_CONDITION5, EDITOR_CONDITION6, PRE_EDITOR, NEXT_EMP ")
					 .append("from peopsoft.PS_FP_CUST_COMPLAIN_LIST ")
					 .append("where COMPLAIN_LIST_ID=? ");

			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			queryCondition.setQueryString(sqlBuffer.toString());
			queryCondition.setString(0, listId);
			
			Map dataMap;
			List<Map> list = dam.exeQuery(queryCondition);
			if (list.size() == 0) dataMap = null;
			else dataMap = list.get(0);
			
			if (dataMap != null) {
				sqlBuffer.setLength(0);
				sqlBuffer.append("select A.EMP_ID, A.EMP_NAME, B.DEPT_NAME ")
						 .append("from WMSUSER.TBORG_MEMBER A ")
						 .append("left join TBORG_DEFN B on A.DEPT_ID = B.DEPT_ID ")
						 .append("where A.EMP_ID=? ");
				queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				queryCondition.setQueryString(sqlBuffer.toString());
				queryCondition.setString(0, backStep1ToGetEditor(dataMap));
				Map dataMap2;
				List<Map> list2 = dam.exeQuery(queryCondition);
				if (list2.size() == 0) dataMap2 = null;
				else dataMap2 = list2.get(0);
				
				if (dataMap2 != null) {
					lastOrgStr = checkMap(dataMap2, "DEPT_NAME");
					lastNameStr = checkMap(dataMap2, "EMP_NAME") + "(" + checkMap(dataMap2, "EMP_ID") + ")";
					nowEmpStr = checkMap(dataMap2, "EMP_ID");
				}
			}
		}
		inputVO.setFlowType(flow);
		inputVO.setComplainListId(listId);
		inputVO.setLastOrg(lastOrgStr);
		inputVO.setLastName(lastNameStr);
		inputVO.setNextEmp(nowEmpStr);
	}

	private String backStep1ToGetEditor(Map dataMap) throws APException {
		String editor = "";
		if ("A2".equals(dataMap.get("HANDLE_STEP"))){ editor = ObjectUtils.defaultIfNull(dataMap.get("EDITOR_CONDITION5"), isNotNull(dataMap.get("EDITOR_CONDITION6"))).toString(); }
		if ("A1".equals(dataMap.get("HANDLE_STEP"))){ editor = ObjectUtils.defaultIfNull(dataMap.get("EDITOR_CONDITION4"), isNotNull(dataMap.get("EDITOR_CONDITION5"))).toString(); }
		if ("A".equals(dataMap.get("HANDLE_STEP"))){ editor = ObjectUtils.defaultIfNull(dataMap.get("EDITOR_CONDITION3"), isNotNull(dataMap.get("EDITOR_CONDITION4"))).toString(); }
		if ("3".equals(dataMap.get("HANDLE_STEP"))){ editor = ObjectUtils.defaultIfNull(dataMap.get("EDITOR_CONDITION2"), isNotNull(dataMap.get("EDITOR_CONDITION1"))).toString(); }
		if ("2".equals(dataMap.get("HANDLE_STEP"))){ editor = ObjectUtils.defaultIfNull(dataMap.get("EDITOR_CONDITION1"), isNotNull(dataMap.get("EDITOR_CONDITION2"))).toString(); }
		if ("1".equals(dataMap.get("HANDLE_STEP"))){ 
			if (dataMap.get("EDITOR_CONDITION1") == null) throw new APException("無法退件，無上一流程!");
			editor = ObjectUtils.defaultIfNull(dataMap.get("PRE_EDITOR"), dataMap.get("EDITOR_CONDITION1")).toString();
			if(StringUtils.isBlank(editor)){ editor = dataMap.get("NEXT_EMP").toString(); }
		}
		return editor;
	}

	private void backStep2(CRM999InputVO inputVO) throws DAOException, JBranchException {
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = null;
		StringBuffer sqlBuffer = new StringBuffer();
		//取得目前流程
		sqlBuffer.append("select A.EMP_ID, A.EMP_NAME, B.DEPT_NAME ")
				 .append("from TBORG_MEMBER A ")
				 .append("left join TBORG_DEFN B on A.DEPT_ID = B.DEPT_ID ")
				 .append("where A.EMP_ID=? ");
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		queryCondition.setQueryString(sqlBuffer.toString());
		queryCondition.setString(0, (String)getUserVariable(FubonSystemVariableConsts.LOGINID));
		Map dataMap;
		List<Map> list = dam.exeQuery(queryCondition);
		if (list.size() == 0) dataMap = null;
		else dataMap = list.get(0);
	
		if (dataMap != null) {
			inputVO.setOrg(checkMap(dataMap, "DEPT_NAME"));
			inputVO.setEmp(checkMap(dataMap, "EMP_ID"));
			inputVO.setName(checkMap(dataMap, "EMP_NAME"));
		}
		
	}

	/**
	 * 查詢客訴資料表
	 * @param body
	 * @param header
	 * @return void
	 * @throws Exception 
	 * @see CRM999#queryCustomerComplainList
	 */
	public void query(Object body, IPrimitiveMap<?> header) throws Exception {
		CRM999InputVO inputVO  = (CRM999InputVO) body;
		CRM999OutputVO returnVO = new CRM999OutputVO();
		
		try {
			returnVO.setResultList1(queryCustomerComplainList(inputVO));
		}catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
		
		this.sendRtnObject(returnVO);
	}

	/**
	 * 取得客訴表-投資明細損益表
	 * @param body
	 * @param header
	 * @return void
	 * @throws Exception 
	 */
	public void getUploadFile(Object body, IPrimitiveMap<?> header)
			throws Exception {
		CRM999InputVO inputVO = (CRM999InputVO)body;
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		sb.append("select CUST_NAME, UPLOAD_FILE from peopsoft.PS_FP_CUST_COMPLAIN_LIST where COMPLAIN_LIST_ID = :id ");
		queryCondition.setObject("id", inputVO.getComplainListId());
		queryCondition.setQueryString(sb.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		String errorMag = null;
		
		try {
			if (list.size() > 0 && list.get(0).get("UPLOAD_FILE") != null) {
				String uuid = UUID.randomUUID().toString();
				String fileName = String.format("投資明細損益表%s.xls", dateFormatOfNoSlash.format(new Date()));
				Blob blob = (Blob) list.get(0).get("UPLOAD_FILE");
				int blobLength = (int) blob.length();
				byte[] blobAsBytes = blob.getBytes(1, blobLength);
	
				File targetFile = new File(filePath, uuid);
				FileOutputStream fos = new FileOutputStream(targetFile);
				fos.write(blobAsBytes);
				fos.close();
				notifyClientToDownloadFile(TmpPath + uuid , fileName);
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
	}

	/**
	 * 儲存客訴資料表
	 * @param body
	 * @param header
	 * @return void
	 * @throws Exception 
	 * @see CRM999#isNotNull
	 * @see CRM999#updateCustomerComplainList
	 * @see CRM999#saveCustomerComplainList
	 */
	public void save(Object body, IPrimitiveMap<?> header) throws Exception {
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		CRM999InputVO inputVO  = (CRM999InputVO) body;
		CRM999OutputVO returnVO  = new CRM999OutputVO();
		
		String personId = "";
		if (inputVO.getPersonId() != null) {
			personId = inputVO.getPersonId();//判斷是否為本行專員
		}
		
		boolean result = false;
		
		if(!personId.equals("") && !personId.equals(null)){
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			queryCondition.setQueryString("select EMP_ID from TBORG_MEMBER where EMP_ID = ? "); //判斷是否為本行專員
			queryCondition.setString(0, personId);
			
			List<Map<String, Object>> qryLst2 = dam.exeQuery(queryCondition);
			
			
			if (qryLst2.size() == 0){
				inputVO.setHandleStep("1");
				returnVO.setErrorMsg("此「專員員編」不為本行專員 請再確認，如已離職請建為空值。");
				this.sendRtnObject(returnVO);
				return;
			}
		}
	
		//現行AOcode 不管是3code、7code，都設為P1
		inputVO.setCustKind("P1");
	
		try {
			String complainListId = isNotNull(inputVO.getComplainListId());
			Object status = inputVO.getStatus();
			if(!"".equals(complainListId)){ //有退件人ID
				if (!"0".equals(status)) {
					String account = (String)getUserVariable(FubonSystemVariableConsts.LOGINID);
					//驗證錯誤訊息============START
					try {
						queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
						queryCondition.setQueryString("select EDITOR_CONDITION1, EDITOR_CONDITION2, EDITOR_CONDITION3 FROM peopsoft.PS_FP_CUST_COMPLAIN_LIST where COMPLAIN_LIST_ID = ? ");
						queryCondition.setString(0, complainListId);
						List<Map<String, Object>> qryLst3 = dam.exeQuery(queryCondition);
						String editor1 = isNotNull(qryLst3.get(0).get("EDITOR_CONDITION1"));
						String editor2 = isNotNull(qryLst3.get(0).get("EDITOR_CONDITION2"));
						String editor3 = isNotNull(qryLst3.get(0).get("EDITOR_CONDITION3"));
						//金服主管及區督導皆為空白則代表客訴單為總行建立, 則需覆核
						if ("".equals(editor1) && "".equals(editor2) && account.equals(editor3)) {
							returnVO.setErrorMsg("客訴資料表建立失敗，不可與建立者同一人！");
							this.sendRtnObject(returnVO);
							return;
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					//驗證錯誤訊息============END
				}
				result = updateCustomerComplainList(inputVO);
			} else {//無退件人ID
				result = saveCustomerComplainList(inputVO);
			}
	
			if(result==true){
				returnVO.setMsg("Y");  //儲存成功
			}else{
				returnVO.setMsg("N");  //儲存失敗
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	
		this.sendRtnObject(returnVO);
	}

	/**
	 * 儲存客訴表至DB<br>
	 * Rex 201409090273-00 組織調整修改客訴資料庫內容<br>
	 * Rex 201409010191-02 新增FCH業務組織人員權限<br>
	 * ismanagement : 案件分流為理財客訴(1)或是消金客訴(3)或是FCH客訴(4)分類
	 * @param inputVO CRM999InputVO
	 * @return boolean
	 * @throws Exception 
	 */
	private boolean saveCustomerComplainList(CRM999InputVO inputVO)throws Exception{
		CRM999_SQL_PARAM sp = new CRM999_SQL_PARAM();
		setHandleStepToSave(inputVO);
		setSaveComplainListId(inputVO);
		saveMain(inputVO, sp);
		saveFlow(inputVO, sp);
	
		try {
			if (execute(sp.getSqlArray(), sp.getParamArray())) {
				uploadFile(inputVO);
				return true;
			}
			return false;
		} catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}

	/*
	 * 儲存客訴主表
	 * */
	private void saveMain(CRM999InputVO inputVO, CRM999_SQL_PARAM sp) throws Exception {
		Object[] cols 
		= { "COMPLAIN_LIST_ID","CREATE_DATE","BRANCH_ID","GRADE","COMPLAIN_SOURCE","COMPLAIN_TYPE_SEL",
		    "OTHER_SOURCE","COMPLAIN_SUMMARY","CUST_ID","CUST_NAME","BIRTHDATE","OCCUP","PHONE","EDUCATION",
		    "OPEN_ACC_DATE","TOTAL_ASSET","CHECK_SHEET","PERSON_ID","PERSON_NAME","AO_CODE","CUST_KIND",
		    "SERVICE_STATUS","BUY_PRODUCT_TYPE","BUY_DATE","COMPLAIN_PRODUCT","COMPLAIN_PRODUCT_CURRENCY",
		    "COMPLAIN_PRODUCT_AMOUNT","PROBLEM_DESCRIBE","CUST_DESCRIBE","HANDLE_CONDITION1","HANDLE_STEP",
		    "EDITOR_CONDITION1","UPLOAD_FILE","END_DATE","COST_TIME","COMPLAIN_TYPE","COMPLAIN_MODE",
		    "COMPLAIN_REQUEST","HANDLE_STATUS","FLOW","NEXT_EMP","FROM_CS","PRE_EDITOR"};
		
		String sql1 = getSaveSQLwithColsArray(cols);
		
	
		//Ray	201305280040-01	開放區督導可建客訴單===============START 【多HANDLE_CONDITION2, EDITOR_CONDITION2 欄位】
		if (checkB01ToSave(inputVO)) {
				String[] b01Cols = {"HANDLE_CONDITION2", "EDITOR_CONDITION2"};
				ArrayList colsList = new ArrayList(Arrays.asList(cols));
				colsList.addAll(Arrays.asList(b01Cols));
				cols = colsList.toArray(); //BO1's cols
				sql1 = getSaveSQLwithColsArray(cols);
		}
		//Ray	201305280040-01	開放區督導可建客訴單===============END
	
		//若是總行人員新增客訴單
		if ("3".equals(inputVO.getJobLevel())) {
			sql1 = sql1.replaceAll("HANDLE_CONDITION1", "HANDLE_CONDITION3");
			sql1 = sql1.replaceAll("EDITOR_CONDITION1", "EDITOR_CONDITION3");
		}
		
		//for paramColList
		ArrayList paramColList = new ArrayList();
		paramColList.addAll(Arrays.asList(cols));
		paramColList.set(paramColList.indexOf("BIRTHDATE"), "BIRTHDAY");
		paramColList.set(paramColList.indexOf("OPEN_ACC_DATE"), "OPEN_ACCOUNT_DATE");
		paramColList.set(paramColList.indexOf("CREATE_DATE"), "FMT_CREATE_DATE");
		paramColList.set(paramColList.indexOf("END_DATE"), "FMT_END_DATE");
		paramColList.set(paramColList.indexOf("FLOW"), "FLOW_TYPE");
		paramColList.remove(paramColList.indexOf("UPLOAD_FILE"));
		ArrayList proccessedParamList = CRM999Service.getParamList(inputVO, paramColList);
		
		//paramSettingLogic
		proccessedParamList.set(paramColList.indexOf("FMT_CREATE_DATE"), getSaveCreateDate(inputVO));
		proccessedParamList.set(paramColList.indexOf("OTHER_SOURCE"), isNotNull(inputVO.getOtherSource()));
		if (checkB01ToSave(inputVO)) {
			proccessedParamList.set(paramColList.indexOf("HANDLE_CONDITION1"), getB01HandleCondition1ToSave(inputVO)); 
			proccessedParamList.set(paramColList.indexOf("HANDLE_CONDITION2"), inputVO.getHandleCondition2());
			proccessedParamList.set(paramColList.indexOf("EDITOR_CONDITION2"), getManFromNotSaveOrCSLogic(inputVO));
		}else{
			proccessedParamList.set(paramColList.indexOf("HANDLE_CONDITION1"), getElseHandleCondition1ToSave(inputVO)); 
		}
		proccessedParamList.set(paramColList.indexOf("HANDLE_STEP"), saveOnly(inputVO) ? "1" : inputVO.getHandleStep());
		proccessedParamList.set(paramColList.indexOf("EDITOR_CONDITION1"), getManFromNotSaveOrCSLogic(inputVO));
		proccessedParamList.set(paramColList.indexOf("FMT_END_DATE"), getEndDateToSave(inputVO));
		proccessedParamList.set(paramColList.indexOf("COST_TIME"), handleStepIsEnd(inputVO) ? "1" : "");
		proccessedParamList.set(paramColList.indexOf("NEXT_EMP"), getNextEmpToSave(inputVO));
		proccessedParamList.set(paramColList.indexOf("FROM_CS"), isFromCs(inputVO) ? "Y" : "N");
		proccessedParamList.set(paramColList.indexOf("PRE_EDITOR"), saveOnly(inputVO) ? (String)getUserVariable(FubonSystemVariableConsts.LOGINID) : "");
	
		sp.setSql1(sql1);
		sp.setParam1(proccessedParamList);
	}

	/*
	 * 儲存客訴流程表
	 * */
	private void saveFlow(CRM999InputVO inputVO, CRM999_SQL_PARAM sp) throws JBranchException {
		ArrayList param2 = new ArrayList();
		ArrayList param3 = new ArrayList();
		String sql2 = "";
		String sql3 = "";
		if ("1".equals(inputVO.getStatus())) {
			sql2 = "insert into peopsoft.PS_FP_COMPLAIN_FLOWDTL(COMPLAIN_LIST_ID,EMP_FROM,EMP_TO,STATUS,DATE_FROM) values (?,?,?,?,SYSDATE)";
			param2.add(inputVO.getComplainListId());
			param2.add((String)getUserVariable(FubonSystemVariableConsts.LOGINID));
			param2.add(inputVO.getNextEmp());
			param2.add("W");
	
			sql3 = "update peopsoft.PS_FP_COMPLAIN_FLOWDTL set STATUS='E' where COMPLAIN_LIST_ID=? and EMP_TO=?";
			param3.add(inputVO.getComplainListId());
			param3.add("1".equals(inputVO.getGrade()) ? inputVO.getNextEmp() : (String)getUserVariable(FubonSystemVariableConsts.LOGINID));
		}
		
		sp.setSql2(sql2);
		sp.setSql3(sql3);
		sp.setParam2(param2);
		sp.setParam3(param3);
	}

	private Object getNextEmpToSave(CRM999InputVO inputVO) throws JBranchException {
		return "0".equals(inputVO.getStatus()) ? (String)getUserVariable(FubonSystemVariableConsts.LOGINID) : inputVO.getNextEmp();
	}

	private Object getEndDateToSave(CRM999InputVO inputVO) {
		if (null != inputVO.getFmt_end_date()) {
			if (handleStepIsEnd(inputVO)) {
				return dateFormatOfSlash.format(new Date());
			} else {
				return dateFormatOfSlash.format(inputVO.getFmt_end_date());
			}
		}
		return null;
	}

	private Object getManFromNotSaveOrCSLogic(CRM999InputVO inputVO) throws JBranchException {
		return saveOnly(inputVO) || "0".equals(inputVO.getStatus()) ? "" : (String)getUserVariable(FubonSystemVariableConsts.LOGINID);
	}

	private String getElseHandleCondition1ToSave(CRM999InputVO inputVO) {
		return "3".equals(inputVO.getJobLevel()) ? inputVO.getHandleCondition3() : inputVO.getHandleCondition1();
	}

	private String getB01HandleCondition1ToSave(CRM999InputVO inputVO) {
		return "".equals(inputVO.getHandleCondition1())? inputVO.getHandleCondition2() : inputVO.getHandleCondition1() ;
	}

	private String getSaveCreateDate(CRM999InputVO inputVO) {
		if ("3".equals(inputVO.getJobLevel()) && inputVO.getFmt_create_date() != null) {
			return dateFormatOfSlash.format(inputVO.getFmt_create_date());
		} else {
			return dateFormatOfSlash.format(new Date());
		}
	}

	private String getSaveSQLwithColsArray(Object[] cols) {
		StringBuffer saveColSQLBuffer = new StringBuffer();
		saveColSQLBuffer.append("insert into peopsoft.PS_FP_CUST_COMPLAIN_LIST (");
		
		StringBuffer saveParamSQLBuffer = new StringBuffer();
		saveParamSQLBuffer.append("values (");
		
		for (Object col : cols) {
			String column = (String)col;
			//col
			saveColSQLBuffer.append(column);
			
			//param
			if (column.indexOf("DATE") > -1) {
				saveParamSQLBuffer.append("to_date(?,'yyyy/MM/dd')");
			} else if (column.indexOf("UPLOAD_FILE") > -1) {
				saveParamSQLBuffer.append("empty_blob()");
			} else {
				saveParamSQLBuffer.append("?");
			}
			
			if (col != cols[cols.length-1]) {
				saveColSQLBuffer.append(",");
				saveParamSQLBuffer.append(",");
			} else {
				saveColSQLBuffer.append(")");
				saveParamSQLBuffer.append(")");
			} 
		}
		return saveColSQLBuffer.toString() + saveParamSQLBuffer.toString();
	}

	private void setHandleStepToSave(CRM999InputVO inputVO) throws Exception {
		String grade = inputVO.getGrade();	//申訴等級	    
		String handleStep = inputVO.getHandleStep();	//等級
		boolean is806 = "806".equals(inputVO.getTerritoryId());	//客服
		String step = "";
		if (isA01(inputVO.getJobTitleId())) {
			if("2".equals(grade) || "1".equals(grade)){
				inputVO.setHandleStep("E");
			}else{
				inputVO.setHandleStep("A");
			}	
		}else {
			if("0".equals(inputVO.getStatus())){
				step = "1";
			}else if("1".equals(inputVO.getStatus())) {
				if(is806){
					if("1".equals(handleStep)){
						step = "2";
					}else if("2".equals(handleStep)){
						step = "3";
					}else if("3".equals(handleStep)){
						step = "E";
					}
				}else{
					step = getHandleStep(inputVO);
					if (isOP01(inputVO.getJobTitleId())) {
						if (opNextFlag(inputVO)) {
							step = "1"; //OP01作業主管的下一階段必須送給其分行主管 20170925 Eli
						} else {
							step = "2"; //若該方行無作業主管，比照原邏輯送給督導
						}
					}
				}
			}
			inputVO.setHandleStep(step);
		}
	}

	/**
	 * 修改客訴單內容
	 * @param inputVO CRM999InputVO
	 * @return boolean
	 * @throws Exception 
	 */
	private boolean updateCustomerComplainList(CRM999InputVO inputVO)throws Exception
	{
		CRM999_SQL_PARAM sp = new CRM999_SQL_PARAM();
		updateSendInfo(inputVO, sp);
		setHandleStepToUpdate(inputVO);
		updateOtherInfo(inputVO, sp);
		uploadFile(inputVO);	
		return execute(sp.getSqlArray(), sp.getParamArray());
	}

	private void updateSendInfo(CRM999InputVO inputVO, CRM999_SQL_PARAM sp) throws JBranchException {
		String account = (String)getUserVariable(FubonSystemVariableConsts.LOGINID);
		if ("1".equals(inputVO.getStatus())) {
			String sql2 = "", sql3 = "", sql4 = "";
			List param2 = new ArrayList();
			List param3 = new ArrayList();
			List param4 = new ArrayList();
	
			if ("sys".equals(inputVO.getNextEmp())) {//沒有下一處理人員
				sql2 = "update peopsoft.PS_FP_COMPLAIN_FLOWDTL set STATUS='E' where COMPLAIN_LIST_ID=? and EMP_TO=?";
				param2.add(inputVO.getComplainListId());
				param2.add(account);
	
				sql3 = "update peopsoft.PS_FP_CUST_COMPLAIN_LIST set NEXT_EMP='sys' where COMPLAIN_LIST_ID=?";
				param3.add(inputVO.getComplainListId());
				
			} else {
				sql2 = "update peopsoft.PS_FP_COMPLAIN_FLOWDTL set STATUS='E' where COMPLAIN_LIST_ID=? and EMP_TO=?";
				param2.add(inputVO.getComplainListId());
				param2.add(account);
	
				sql3 = "insert into peopsoft.PS_FP_COMPLAIN_FLOWDTL(COMPLAIN_LIST_ID,EMP_FROM,EMP_TO,STATUS,DATE_FROM) values (?,?,?,?,SYSDATE)";
				param3.add(inputVO.getComplainListId());
				param3.add(account);
				param3.add(inputVO.getNextEmp());
				param3.add("W");
	
				sql4 = "update peopsoft.PS_FP_CUST_COMPLAIN_LIST set NEXT_EMP=? where COMPLAIN_LIST_ID=?";
				param4.add(inputVO.getNextEmp());
				param4.add(inputVO.getComplainListId());
			}
			
			sp.setSql2(sql2);
			sp.setParam2(param2);
			sp.setSql3(sql3);
			sp.setParam3(param3);
			sp.setSql4(sql4);
			sp.setParam4(param4);
		}
	}

	private void setHandleStepToUpdate(CRM999InputVO inputVO) throws JBranchException {
		String handleStep = inputVO.getHandleStep();		//表示目前處理進度
		String status = inputVO.getStatus();	//判斷是否需要更新handleStep 0=不更新 1=更新
		boolean is806 = "806".equals(inputVO.getTerritoryId());		//客服
		
		if ("1".equals(status)) {
			if ("sys".equals(inputVO.getNextEmp())) {//沒有下一處理人員
				inputVO.setHandleStep("E");
			} else {
				String step = "1";	//預設if(isPreLevel(jobTitleId))
				if(is806){
					if("806".equals(inputVO.getBranchId())){
						if("1".equals(handleStep)){
							step = "2";
						}else if("2".equals(handleStep)){
							step = "3";
						}else if("3".equals(handleStep)){
							step = "E";
						}
					}else{
						step = handleStep;
					}
				}else{
					step = getHandleStep(inputVO);
				}
				System.out.println("updateCustomerComplainList STEP : " + step);
	
				inputVO.setHandleStep(step);
			}
		}
	}

	private String getHandleStep(CRM999InputVO inputVO) {
		String jobTitleId = inputVO.getJobTitleId();
		String step = "1";
		String handleStep = inputVO.getHandleStep();		//表示目前處理進度
		String grade = inputVO.getGrade();		
		if(isLevel1(jobTitleId)){ step = "2"; }
		if(isLevel2(jobTitleId)){ step = "3"; }
		if(isLevel3(jobTitleId) && !isA01(jobTitleId)){ step = "3".equals(grade) ? "A" : "E" ; }
		if(isA01(jobTitleId)){
			if("Y".equals(inputVO.getAddByA01())){
				step = "E";
				if("3".equals(grade)){
					step = "A1";
					if("A1".equals(handleStep)){ step = "A2";}
					if("A2".equals(handleStep)){ step = "E"; }
				}
			}else{
				if("1".equals(grade) || "2".equals(grade)){
					if("1".equals(handleStep)){
						step = "2";
					}else if("2".equals(handleStep)){
						step = "3";
					}else if("3".equals(handleStep)){
						step = "E";
					}
				}else if("3".equals(grade)){
					if("1".equals(handleStep)){
						step = "2";
					}else if("2".equals(handleStep)){
						step = "3";
					}else if("3".equals(handleStep)){
						step = "A";
					}else if("A".equals(handleStep)){
						step = "A1";
					}else if("A1".equals(handleStep)){
						step = "A2";
					}else if("A2".equals(handleStep)){
						step = "E";
					}
				}
			}
		}
		return step;
	}

	private void updateOtherInfo(CRM999InputVO inputVO, CRM999_SQL_PARAM sp) throws Exception {
		String account = (String)getUserVariable(FubonSystemVariableConsts.LOGINID);
		String status = inputVO.getStatus();	//判斷是否需要更新handleStep 0=不更新 1=更新
		String grade = inputVO.getGrade();
		String level = isNotNull(inputVO.getJobLevel());
		boolean isA01 = "Y".equals(inputVO.getIsA01());
		
		//for paramList
		ArrayList paramColList = new ArrayList();
		StringBuffer sqlBuffer = new StringBuffer();
		
		String[] commonCols 
			= {"OTHER_SOURCE", "COMPLAIN_SUMMARY", "CUST_ID", "CUST_NAME", "BIRTHDATE",
			   "OCCUP", "PHONE", "EDUCATION", "OPEN_ACC_DATE", "TOTAL_ASSET", "PERSON_ID",
			   "PERSON_NAME", "AO_CODE", "SERVICE_STATUS", "BUY_PRODUCT_TYPE", "COMPLAIN_PRODUCT",
			   "COMPLAIN_PRODUCT_CURRENCY", "COMPLAIN_PRODUCT_AMOUNT", "BUY_DATE", "PROBLEM_DESCRIBE",
			   "CUST_DESCRIBE", "HANDLE_CONDITION1", "GRADE", "COMPLAIN_SOURCE", "COMPLAIN_TYPE_SEL" };
	
		
		if ("1".equals(level)) { //第一級處理人員更新
		    sqlBuffer = getUpdateSQLwithColsArray(commonCols);
		    paramColList.addAll(Arrays.asList(commonCols));
	
			//如果上一流程為作業主管，則不寫入EDITOR_CONDITION1 20170926 Eli
			if(!"OP01".equals(isNotNull(inputVO.getLastOrgTitle()))) {  
				sqlBuffer.append("EDITOR_CONDITION1= '" + account + "', "); 
			}
			
			if ("1".equals(status)) {
				if ("B01".equals(inputVO.getJobTitleId()) || "B02".equals(inputVO.getJobTitleId()) || "CS02".equals(inputVO.getJobTitleId())) {
					if ("2".equals(grade) || "3".equals(grade)) {
						sqlBuffer.append("EDITOR_CONDITION2= '" + account + "' , HANDLE_CONDITION2=?, ");
						paramColList.add("HANDLE_CONDITION1");
					}
				}
			}
			
			paramColList.set(paramColList.indexOf("BIRTHDATE"), "BIRTHDAY");
			paramColList.set(paramColList.indexOf("OPEN_ACC_DATE"), "OPEN_ACCOUNT_DATE");
			
		} else if ("2".equals(level)) { //第二級處理人員更新
			sqlBuffer.append("UPDATE peopsoft.PS_FP_CUST_COMPLAIN_LIST SET EDITOR_CONDITION2= '" + account + "', HANDLE_CONDITION2=?, ");
			paramColList.add("HANDLE_CONDITION2");
			
			if ("A".equals(inputVO.getHandleStep())) {  //特例判斷，如果該督導也有處長身分，將會直送總行，所以設定EDITOR_CONDITION3 and HANDLE_CONDITION3
				sqlBuffer.append("EDITOR_CONDITION3='" + account + "', HANDLE_CONDITION3=?, ");
				paramColList.add("HANDLE_CONDITION2");
			}
			
			
		} else if ("3".equals(level) && !isA01) {  //第三級處理人員更新 
			sqlBuffer.append("UPDATE peopsoft.PS_FP_CUST_COMPLAIN_LIST SET EDITOR_CONDITION3= '" + account + "' , HANDLE_CONDITION3=?, ");
			paramColList.add("HANDLE_CONDITION3");
		} else { //總行
			//for sql
			String[] spcCols 
			= {"HANDLE_CONDITION2", "HANDLE_CONDITION3", "CREATE_DATE", "COMPLAIN_TYPE",
			   "COMPLAIN_MODE", "COMPLAIN_REQUEST", "HANDLE_STATUS", "CHECK_SHEET"};
			
			ArrayList colsList = new ArrayList(Arrays.asList(commonCols));
			colsList.addAll(Arrays.asList(spcCols));
		    sqlBuffer = getUpdateSQLwithColsArray(colsList.toArray());

		    String handleStep = inputVO.getHandleStep();
		    if ("3".equals(inputVO.getGrade())) {
		    	if("A1".equals(handleStep)){
					sqlBuffer.append("EDITOR_CONDITION4= '" + account + "', ");	
				}else if("A2".equals(handleStep)){
					sqlBuffer.append("EDITOR_CONDITION5= '" + account + "', ");	
				}else if ("E".equals(handleStep)) {
					sqlBuffer.append("EDITOR_CONDITION6= '" + account + "', ");
				}
		    } else {
		    	sqlBuffer.append("EDITOR_CONDITION4= '" + account + "', ");	
		    }
			
			
			//for paramColList
			paramColList.addAll(colsList);
			paramColList.set(paramColList.indexOf("BIRTHDATE"), "BIRTHDAY");
			paramColList.set(paramColList.indexOf("OPEN_ACC_DATE"), "OPEN_ACCOUNT_DATE");
			paramColList.set(paramColList.indexOf("CREATE_DATE"), "FMT_CREATE_DATE");
		}
	
		//當處理進度等於E表示結案
		if ("E".equals(inputVO.getHandleStep()) || "A1".equals(inputVO.getHandleStep()) || "A2".equals(inputVO.getHandleStep())) {
			if (inputVO.getFmt_end_date()==null) {
				sqlBuffer.append("END_DATE=SYSDATE, COST_TIME=ROUND(SYSDATE - CREATE_DATE + 0.5, 0), ");	
			} else {
				sqlBuffer.append("END_DATE=TO_DATE(?,'YYYY/MM/DD'), COST_TIME=ROUND(TO_DATE(?,'YYYY/MM/DD') - TO_DATE(?,'YYYY/MM/DD') + 0.5, 0), ");
				paramColList.add("FMT_END_DATE");
				paramColList.add("FMT_END_DATE");
				paramColList.add("FMT_CREATE_DATE");
			}
		}else{
			if (inputVO.getFmt_end_date()==null) {
				sqlBuffer.append("END_DATE='', ");
			}else{
				sqlBuffer.append("END_DATE=TO_DATE(?,'YYYY/MM/DD'), ");
				paramColList.add("FMT_END_DATE");
			}
		}
	
		sqlBuffer.append("HANDLE_STEP=? WHERE COMPLAIN_LIST_ID = ? ");
		paramColList.add("HANDLE_STEP");
		paramColList.add("COMPLAIN_LIST_ID");
		sp.setSql1(sqlBuffer.toString());
		sp.setParam1(CRM999Service.getParamList(inputVO, paramColList));
	}

	private StringBuffer getUpdateSQLwithColsArray(Object[] cols) {
		StringBuffer updateBufferSQL = new StringBuffer();
		updateBufferSQL.append("update peopsoft.PS_FP_CUST_COMPLAIN_LIST set ");
		
		for (Object col : cols) {
			String column = (String)col;
			if (column.indexOf("DATE") > 0 ) {
				updateBufferSQL.append(column).append(" = TO_DATE(?, 'YYYY/MM/DD'), ");
			} else {
				updateBufferSQL.append(column).append(" = ?, ");
			}
			
		}
		return updateBufferSQL;
	}

	/**
	 * 設定流程相關資訊
	 * @param body
	 * @param header
	 * @return void
	 * @throws Exception 
	 */
	public void next(Object body, IPrimitiveMap<?> header) throws Exception {
		CRM999InputVO inputVO  = (CRM999InputVO) body;
		
		setLastInfo(inputVO);    
		setCurrentInfo(inputVO); 
		setNextInfo(inputVO);    
	
		CRM999OutputVO returnVO = new CRM999OutputVO();
		returnVO.setParamVO(inputVO);
		this.sendRtnObject(returnVO);
	}

	/**
	 * 取得上一流程人員資訊
	 * @param inputVO
	 * @return void
	 * @throws Exception 
	 */
	private void setLastInfo(CRM999InputVO inputVO) throws Exception {
		//default
		String lastOrgStr = "無";
		String lastNameStr = "無";
		
		String listId = isNotNull(inputVO.getComplainListId());
		if(!"".equals(listId)){ 	
			//取得資訊
			StringBuffer sqlBuffer = new StringBuffer();
			sqlBuffer.append("select A.EMP_ID, A.EMP_NAME, B.DEPT_NAME, D.SA_JOB_TITLE_ID from peopsoft.PS_FP_COMPLAIN_FLOWDTL D ")
					 .append("left join WMSUSER.TBORG_MEMBER A on A.EMP_ID = D.EMP_FROM ")
					 .append("left join WMSUSER.TBORG_DEFN B on A.DEPT_ID = B.DEPT_ID ")
					 .append("left join WMSUSER.TBORG_MEMBER_ROLE C on A.EMP_ID = C.EMP_ID ")
					 .append("left join WMSUSER.TBORG_ROLE_MAPPING D on C.ROLE_ID = D.ROLE_ID ")
					 .append("where D.COMPLAIN_LIST_ID= ? and D.STATUS='W' ");
			dam = this.getDataAccessManager();
			QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			queryCondition.setQueryString(sqlBuffer.toString());
			queryCondition.setString(0, listId);
			List<Map> list = dam.exeQuery(queryCondition);
	
			if (list.size() > 0) {
				Map map = list.get(0);
				lastOrgStr = checkMap(map, "DEPT_NAME");
				lastNameStr =  checkMap(map, "EMP_NAME") + "(" + checkMap(map, "EMP_ID") + ")";
				Object lastOrgTitle = map.get("SA_JOB_TITLE_ID");
				if (null != lastOrgTitle) {
					inputVO.setLastOrgTitle(lastOrgTitle.toString());
				}
			}
		}
		inputVO.setLastOrg(lastOrgStr);
		inputVO.setLastName(lastNameStr);
	}

	/**
	 * 預設目前流程資訊-流程單位、負責人ID、負責人 姓名
	 * @param empId
	 * @param inputVO
	 */
	private void setCurrentInfo(CRM999InputVO inputVO) throws Exception{
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		String sql = "select A.EMP_ID PERSON_ID, A.EMP_NAME NAME, B.DEPT_NAME DESCR from WMSUSER.TBORG_MEMBER A left join WMSUSER.TBORG_DEFN B on A.DEPT_ID = B.DEPT_ID where A.EMP_ID=? ";
		queryCondition.setQueryString(sql);
		queryCondition.setString(0, (String)getUserVariable(FubonSystemVariableConsts.LOGINID));
		List<Map> list = dam.exeQuery(queryCondition);
		
		for(Map map : list) {
			inputVO.setOrg(checkMap(map, "DESCR"));
			inputVO.setEmp(checkMap(map, "PERSON_ID"));
			inputVO.setName(checkMap(map, "NAME"));
		}
	}

	/**
	 * 取得下一流程人員資訊
	 * @param inputVO
	 * @return void
	 * @throws Exception 
	 */
	private void setNextInfo(CRM999InputVO inputVO) throws Exception {
		String branchId  = isNotNull(inputVO.getBranchId());	 //客訴分行
		String territory  = isNotNull(inputVO.getTerritoryId()); //使用者分行
		String title = isNotNull(inputVO.getJobTitleId());	     //職稱ID
		String jobLv  = isNotNull(inputVO.getJobLevel());		 //處理人員等級[1,2,3,A,A1,A2(總行)] 
		
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("select A.EMP_ID PERSON_ID, A.EMP_NAME NAME, B.DEPT_NAME DESCR ")
				 .append("from TBORG_MEMBER A ")
				 .append("left join TBORG_DEFN B         on A.DEPT_ID = B.DEPT_ID ")
				 .append("left join TBORG_MEMBER_ROLE C  on A.EMP_ID = C.EMP_ID ")
				 .append("left join TBORG_ROLE_MAPPING D on C.ROLE_ID = D.ROLE_ID ");
	
		if ("806".equals(territory)) { //客服
			if (branchId.equals(territory)) {
				if ("CS03".equals(title)) { 
					sqlBuffer.append("where D.SA_JOB_TITLE_ID ='CS02' order by A.DEPT_ID ");
				}
				else if ("CS02".equals(title)) {
					sqlBuffer.append("where D.SA_JOB_TITLE_ID ='CS01' order by A.DEPT_ID ");
				}
				else if ("CS01".equals(title)) {
					sqlBuffer.append("where A.DEPT_ID ='175B' ");
				}
			} else {
				sqlBuffer.append("where D.SA_JOB_TITLE_ID in ('C01','C02','C05','OP01','E01') and A.DEPT_ID = '" + branchId + "' ");
			}
		}else if ("998".equals(branchId)) { //不管
			if ("1".equals(jobLv)){
				if("E01".equals(title)){ 
					sqlBuffer.append("where D.SA_JOB_TITLE_ID ='B02' order by A.DEPT_ID ");
				}
				else if("B02".equals(title)){ 
					sqlBuffer.append("where (D.SA_JOB_TITLE_ID ='B04' or A.DEPT_ID = '175B') order by A.DEPT_ID desc ");
				}
			}
			if ("3".equals(jobLv)) {
				sqlBuffer.append("where A.DEPT_ID ='175B' ");
			}
		} else {
			if ("OP01".equals(title)) { 
				if (opNextFlag(inputVO)) {  //作業主管下一流程為該分行主管 20170926 Eli
					sqlBuffer.append("where D.SA_JOB_TITLE_ID in ('C01','C02') and B.DEPT_ID = '" + branchId + "'order by A.DEPT_ID ");
				} else { //該作業主管若其分行無個金主管，下一流程照舊送給營運督導 20171002 Eli
					sqlBuffer.append("where D.SA_JOB_TITLE_ID in ('B01','B05') order by A.DEPT_ID ");
				}
			} else {
				//20180328 將督導下一流程可選擇處長、總行，修正為只能選擇處長
				if ("1".equals(jobLv)) {
					setNextEmp(sqlBuffer.toString(), inputVO);
					if ("B01".equals(title)) { 
						sqlBuffer.append("where D.SA_JOB_TITLE_ID = 'P01' and A.DEPT_ID in ('171','172','174') ") ;
					} else {
						sqlBuffer.append("where D.SA_JOB_TITLE_ID in ('B01','B05') order by A.DEPT_ID ");
					}
				}
				if("2".equals(jobLv)){
					setNextEmp(sqlBuffer.toString(), inputVO);
					sqlBuffer.append("where D.SA_JOB_TITLE_ID in ('P01') and a.DEPT_ID in ('171','172','174') ");
				}
				if ("3".equals(jobLv) || "A".equals(jobLv) || "A1".equals(jobLv)) {
					//20171023 使用TBORG_ROLE_MAPPING來管控組織人員，若以後有人員組織異動，直接從db改即可
					sqlBuffer.append("where D.SA_JOB_TITLE_ID='A12' and a.EMP_ID <> '" + (String)getUserVariable(FubonSystemVariableConsts.LOGINID) + "'");
				}
			}
		}
		inputVO.setAllUsr(getNextEmpList(sqlBuffer.toString()));
	}

	/**
	 * 客訴資料表-退件
	 * @param body
	 * @param header
	 * @return void
	 * @throws Exception 
	 */
	public void back(Object body, IPrimitiveMap<?> header) throws Exception {
		CRM999InputVO inputVO  = (CRM999InputVO) body;
		CRM999OutputVO returnVO  = new CRM999OutputVO();
		Map dataMap = preBackQuery(inputVO);
		try {
			preBackQuery(inputVO);
			if("E".equals(dataMap.get("HANDLE_STEP"))){
				returnVO.setErrorMsg("結案客訴資料表退件失敗！");
			}else{
				if(backLogic(inputVO, dataMap)){
					returnVO.setMsg("Y"); //退件成功
				}else{
					returnVO.setMsg("N"); //退件失敗
				}
			}
		}catch (Exception e){
			returnVO.setErrorMsg(e.getMessage());
			e.printStackTrace();
		}
		this.sendRtnObject(returnVO);
	}

	private Map preBackQuery(CRM999InputVO inputVO) throws DAOException, JBranchException {
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = null;
		StringBuffer sqlBuffer = new StringBuffer()
			.append("select NEXT_EMP, HANDLE_STEP, EDITOR_CONDITION1, EDITOR_CONDITION2, EDITOR_CONDITION3,")
			.append("EDITOR_CONDITION4, EDITOR_CONDITION5, EDITOR_CONDITION6, PRE_EDITOR ")
			.append("from peopsoft.PS_FP_CUST_COMPLAIN_LIST ")
			.append("where COMPLAIN_LIST_ID=? ");
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		queryCondition.setQueryString(sqlBuffer.toString());
		queryCondition.setString(0, isNotNull(inputVO.getComplainListId()));
		return (Map) dam.exeQuery(queryCondition).get(0);
		
	}

	private boolean backLogic(CRM999InputVO inputVO, Map dataMap) throws JBranchException {
		String account = (String)getUserVariable(FubonSystemVariableConsts.LOGINID);
		String listId = isNotNull(inputVO.getComplainListId());
		String reason  = isNotNull(inputVO.getBackReason());
		String editor = "";
		String handleStep = "";
		String[] sqls = new String[3];
		boolean result;

		String dataHandleStep = (String)dataMap.get("HANDLE_STEP");
		if ("E".equals(dataHandleStep)) {  //取消結案才會符合此條件。
			Map info = cancelHandleStepLogic(dataMap);
			editor = (String)info.get("editor");
			handleStep = (String)info.get("step");
		} else if ("A2".equals(dataHandleStep)) {
			editor = ObjectUtils.defaultIfNull(dataMap.get("EDITOR_CONDITION5"), dataMap.get("EDITOR_CONDITION6")).toString();
			handleStep = "A1";
		} else if ("A1".equals(dataHandleStep)) {
			editor = ObjectUtils.defaultIfNull(dataMap.get("EDITOR_CONDITION4"), dataMap.get("EDITOR_CONDITION5")).toString();
			handleStep = "A";
		} else if ("A".equals(dataHandleStep)) {
			editor = ObjectUtils.defaultIfNull(dataMap.get("EDITOR_CONDITION3"), dataMap.get("EDITOR_CONDITION4")).toString();
			handleStep = "3";
		} else if ("3".equals(dataHandleStep)) {
			editor = ObjectUtils.defaultIfNull(dataMap.get("EDITOR_CONDITION2"), dataMap.get("EDITOR_CONDITION3")).toString();
			handleStep = "2";
		} else if ("2".equals(dataHandleStep)) {
			editor = ObjectUtils.defaultIfNull(dataMap.get("EDITOR_CONDITION1"), dataMap.get("EDITOR_CONDITION2")).toString();
			handleStep = "1";
		} else if ("1".equals(dataHandleStep)) {
			editor = ObjectUtils.defaultIfNull(dataMap.get("PRE_EDITOR"), dataMap.get("EDITOR_CONDITION1")).toString();
			if(StringUtils.isBlank(editor)){ editor = dataMap.get("NEXT_EMP").toString(); }
			handleStep = "1";
		}
		sqls[0] = "UPDATE peopsoft.PS_FP_CUST_COMPLAIN_LIST SET NEXT_EMP=?, HANDLE_STEP=? WHERE COMPLAIN_LIST_ID = ? ";
		sqls[1] = "UPDATE peopsoft.PS_FP_COMPLAIN_FLOWDTL SET STATUS='B', REASON=? WHERE COMPLAIN_LIST_ID=? AND EMP_TO=?";
		sqls[2] = "INSERT INTO peopsoft.PS_FP_COMPLAIN_FLOWDTL(COMPLAIN_LIST_ID,EMP_FROM,EMP_TO,STATUS,DATE_FROM) VALUES (?,?,?,?,SYSDATE)";

		ArrayList param1 = new ArrayList();
		ArrayList param2 = new ArrayList();
		ArrayList param3 = new ArrayList();

		param1.add(editor);
		param1.add(handleStep);
		param1.add(listId);

		param2.add(reason);
		param2.add(listId);
		param2.add(account);

		param3.add(listId);
		param3.add(account);
		param3.add(editor);
		param3.add("W");
		Object[] params = new Object[]{param1,param2,param3};
		
		try {
			result = execute(sqls, params);
		} catch (Exception e) {
			result = false;
		}
		return result;
	}

	private Map cancelHandleStepLogic(Map dataMap) throws APException {

		HashMap map = new HashMap();

		//如果最後一階是系統，退件給第五階，其餘退上一階 
		if (dataMap.get("EDITOR_CONDITION6") != null) {
			map.put("editor", dataMap.get("EDITOR_CONDITION5"));
			map.put("step", "A1");
		} else if (dataMap.get("EDITOR_CONDITION5") != null) {
			map.put("editor", dataMap.get("EDITOR_CONDITION4"));
			map.put("step", "A");
		}  else if (dataMap.get("EDITOR_CONDITION4") != null) {
			map.put("editor", dataMap.get("EDITOR_CONDITION3"));
			map.put("step", "3");
		} else if (dataMap.get("EDITOR_CONDITION3") != null) {
			map.put("editor", dataMap.get("EDITOR_CONDITION2"));
			map.put("step", "2");
		} else if (dataMap.get("EDITOR_CONDITION2") != null) {
			map.put("editor", dataMap.get("EDITOR_CONDITION1"));
			map.put("step", "1");
		} else {
			throw new APException("無法退件");
		}

		return map;
	}

	/**
	 * 刪除客訴資料
	 * @param body
	 * @param header
	 * @return void
	 * @throws Exception 
	 */
	public void del(Object body, IPrimitiveMap<?> header) throws Exception {
		CRM999InputVO inputVO  = (CRM999InputVO) body;
		CRM999OutputVO returnVO = new CRM999OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		queryCondition.setQueryString("update peopsoft.PS_FP_CUST_COMPLAIN_LIST set STATUS='D' where COMPLAIN_LIST_ID= :id ");
		queryCondition.setObject("id", inputVO.getComplainListId());
		
		try{
			dam.exeUpdate(queryCondition);
			returnVO.setMsg("Y");   //刪除成功
		}catch (Exception e) {
			returnVO.setMsg("N");	//刪除失敗
		}
		
		this.sendRtnObject(returnVO);
	}

	/**
	 * 覆核多批案件
	 * @param body
	 * @param header
	 * @return void
	 * @throws Exception 
	 */
	public void sendBatchComplains(Object body, IPrimitiveMap<?> header) throws Exception {
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		CRM999InputVO inputVO  = (CRM999InputVO) body;
		CRM999OutputVO returnVO = new CRM999OutputVO();
		StringBuffer sqlBuffer = new StringBuffer();
		
		String checkCol = "EDITOR_CONDITION6";
		if ("3".equals(inputVO.getHandleStep())) {
			checkCol = "EDITOR_CONDITION3";
		}
		
		sqlBuffer.append("update peopsoft.PS_FP_CUST_COMPLAIN_LIST ")
				 .append("set " + checkCol + " = :editor, ")
				 .append("NEXT_EMP = :next, ")
				 .append("HANDLE_STEP= :step ")
				 .append("where COMPLAIN_LIST_ID= :id ");
		
		queryCondition.setQueryString(sqlBuffer.toString());
		queryCondition.setObject("editor", (String)getUserVariable(FubonSystemVariableConsts.LOGINID));
		queryCondition.setObject("next", "sys");
		queryCondition.setObject("step", "E");
		
		//processLogic
		int successCnt = 0;
		int failCnt = 0;
		String[] idArray = inputVO.getComplainListId().split(",");
		
		for(String id : idArray) {
			try {
				queryCondition.setObject("id", id);
				dam.exeUpdate(queryCondition);
				successCnt++;
			} catch (Exception e) {
				failCnt++;
				e.printStackTrace();
			}
		}
		
		returnVO.setMsg("共有 " + successCnt + " 筆覆核成功 ! " + failCnt + " 筆失敗! ");
		this.sendRtnObject(returnVO);
	}
	
	
	/**
	 * 取消結案-功能
	 * @param body
	 * @param header
	 * @return void
	 * @throws Exception 
	 * @see CRM999#isNotNull
	 * @see CRM999#execute
	 */
	public void cancel(Object body, IPrimitiveMap<?> header) throws Exception {
		CRM999InputVO inputVO  = (CRM999InputVO) body;
		CRM999OutputVO returnVO = new CRM999OutputVO();
		try {
			Map cancelInfo = preCancelQuery(inputVO);
			if (cancelInfo == null) {
				returnVO.setErrorMsg("該案件尚未結案，無法取消！");
				this.sendRtnObject(returnVO);
				return;
			} 
			if (cancelInfo.get("EDITOR_CONDITION1") == null && cancelInfo.get("EDITOR_CONDITION3") != null) {
				returnVO.setErrorMsg("該案件由總行建立已結案，無法取消！");
				this.sendRtnObject(returnVO);
				return;
			}
			cancelLogic(inputVO);
			backLogic(inputVO, preBackQuery(inputVO));
		} catch (Exception e) {
			returnVO.setErrorMsg(e.getMessage());
			this.sendRtnObject(returnVO);
			return;
		}
	
		this.sendRtnObject(returnVO);
	}

	private Map preCancelQuery(CRM999InputVO inputVO) throws DAOException, JBranchException {
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		String sql = "SELECT EDITOR_CONDITION1, EDITOR_CONDITION3 FROM peopsoft.PS_FP_CUST_COMPLAIN_LIST WHERE COMPLAIN_LIST_ID=? AND HANDLE_STEP='E'";
		queryCondition.setQueryString(sql);
		queryCondition.setString(0, inputVO.getComplainListId());
		List<Map> list = dam.exeQuery(queryCondition);
		if (list.size() == 0) return null;
		return list.get(0);
	}

	private void cancelLogic(CRM999InputVO inputVO) throws DAOException, JBranchException {
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("update peopsoft.PS_FP_CUST_COMPLAIN_LIST ")
				 .append("set END_DATE='', COST_TIME='' ")
				 .append("where COMPLAIN_LIST_ID= :id ");
		queryCondition.setQueryString(sqlBuffer.toString());
		queryCondition.setObject("id", inputVO.getComplainListId());
		dam.setAutoCommit(true);
		try {
			dam.exeUpdate(queryCondition);
		} finally {
			dam.setAutoCommit(false);
		}
		
	}

	/**
	 * 列印 客訴單PDF檔
	 * @param body
	 * @param header
	 * @return void
	 * @throws Exception 
	 * @see CRM999#exportPdf
	 */
	public void expPdf(Object body, IPrimitiveMap<?> header)
			throws Exception {
		CRM999InputVO inputVO = (CRM999InputVO)body;

		//產生PDF檔案
		exportPdf(inputVO);
	}

	/**
	 * 列印客訴資料表PDF
	 * Ray	201310140078-00	修改客訴處理單之列印格式
	 * Rex 	201409090273-00 組織調整修改客訴資料庫內容
	 * @param inputVO CRM999InputVO
	 * @return void
	 * @throws Exception 
	 * @see CRM999#queryCustomerComplainList
	 * @see CRM999#setPdfPTable
	 * @see CRM999#getPdfPTable
	 * @see CRM999#isNotNull
	 */
	private void exportPdf(CRM999InputVO inputVO) throws Exception{
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = null;
		
		Document.compress = false;
		Rectangle rectPageSize = new Rectangle(PageSize.A4);// 設定版面
		Document document = new Document(rectPageSize, 20, 20, 18, 15);// 設定邊距
	
	
		String uuid = UUID.randomUUID().toString();
		String fileName = String.format("客訴處理單%s.pdf", dateFormatOfNoSlash.format(new Date()));
		PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(tempFile + "//" + uuid));
	
		document.open();
		List resultList = queryCustomerComplainList(inputVO);
	
		Map m = new HashMap();
		if (resultList.size()>0){
			m = (Map)resultList.get(0);
		} else {
			throw new APException("此客訴單無法列印！");
		}
	
		document.open();
		document.newPage();
		File nowpath = new File (DataManager.getRealPath() , "doc//FPS");
		//標楷體
		BaseFont bfChinese = BaseFont.createFont(nowpath + "/kaiu.ttf", "Identity-H", BaseFont.NOT_EMBEDDED);
		Font nFont8 = new Font(bfChinese, 8, Font.NORMAL);
		Font nFont9 = new Font(bfChinese, 9, Font.NORMAL);
		Font nFontB10 = new Font(bfChinese, 10, Font.BOLD);
		Font nFont11 = new Font(bfChinese, 11, Font.NORMAL);
		Font nFont12 = new Font(bfChinese, 12, Font.NORMAL);
		Font nFontB12 = new Font(bfChinese, 12, Font.BOLD);
		Font nFont18 = new Font(bfChinese, 18, Font.NORMAL);
	
		int rowCnt = 0;
		//.....................................................................
		PdfPTable table = getPdfPTable();
		PdfPCell cell1 = new PdfPCell();
		cell1.setPadding(0);
		cell1.setBorderWidth(0);
		Paragraph text = new Paragraph("【　個　人　金　融　總　處　　財　管　客　訴　處　理　單　】", nFont18);
		text.setAlignment(Paragraph.ALIGN_CENTER);
		//cell1.setFixedHeight(18);
		cell1.addElement(text);
		table.addCell(cell1);
		document = setPdfPTable(document, rowCnt + 4, table);
	
		//.....................................................................
		table = getPdfPTable();
		cell1 = new PdfPCell();
		cell1.setBorderWidth(0);
		cell1.setFixedHeight(30);
		text = new Paragraph("日期：", nFont11);
		text.add(new Chunk("　"+ ObjectUtils.defaultIfNull( m.get("FMT_CREATE_DATE"),"　　　　").toString() +"　",nFont9).setUnderline(0.2f, -2f));
		text.add(new Chunk("  編號：",nFont11));
		text.add(new Chunk("　"+ ObjectUtils.defaultIfNull( m.get("COMPLAIN_LIST_ID"),"　　　　").toString() +"　",nFont9).setUnderline(0.2f, -2f));
		text.add(new Chunk("  單位別：",nFont11));
		text.add(new Chunk("　"+ ObjectUtils.defaultIfNull( m.get("BRANCH_ID"),"　　　　").toString() +"　",nFont9).setUnderline(0.2f, -2f));
	
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		queryCondition.setQueryString("select nvl(DEPT_NAME,'') DEPT_NAME from TBORG_DEFN where DEPT_ID = ? ");
		queryCondition.setString(0, isNotNull(m.get("BRANCH_ID")));
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		String deptName = "";
		if (list.size() > 0) {
			deptName = isNotNull(list.get(0).get("DEPT_NAME"));
		}
		
		text.add(new Chunk(deptName + "　",nFont9).setUnderline(0.2f, -2f));
	
		text.setAlignment(Paragraph.ALIGN_LEFT);
		cell1.addElement(text);
		table.addCell(cell1);
		document= setPdfPTable(document, rowCnt + 2, table);
	
		table = getPdfPTable();
		cell1 = new PdfPCell();
		cell1.setBorderWidth(0);
		cell1.setFixedHeight(30);
		text = new Paragraph("主管簽核：", nFont11);
		text.add(new Chunk(" "+ ObjectUtils.defaultIfNull( m.get("EDITOR_CONDITION1_NM"),"　　　　").toString() +" ",nFont9).setUnderline(0.2f, -2f));
		text.add(new Chunk(" 專員：",nFont11));
		text.add(new Chunk(" "+ ObjectUtils.defaultIfNull( m.get("PERSON_NAME"),"　　　　").toString() ,nFont9).setUnderline(0.2f, -2f));
		text.add(new Chunk("(申訴事件主要服務人員) ",nFont8).setUnderline(0.2f, -2f));
	
		text.add(new Chunk(" AO CODE：",nFont11));
		text.add(new Chunk(" "+ ObjectUtils.defaultIfNull( m.get("AO_CODE"),"　　　　").toString() +" ",nFont9).setUnderline(0.2f, -2f));
		text.add(new Chunk(" 員編：",nFont11));
		text.add(new Chunk(" "+ ObjectUtils.defaultIfNull( m.get("PERSON_ID"),"　　　　").toString() +" ",nFont9).setUnderline(0.2f, -2f));
		text.add(new Chunk("  是否在職：",nFont11));
		text.add(new Chunk(" "+ ObjectUtils.defaultIfNull( m.get("SERVICE_STATUS"),"　　　　").toString() +" ",nFont9).setUnderline(0.2f, -2f));
		text.setAlignment(Paragraph.ALIGN_LEFT);
		cell1.addElement(text);
		table.addCell(cell1);
		document= setPdfPTable(document, rowCnt + 1, table);
	
		table = getPdfPTable();
		cell1 = new PdfPCell();
		String grade1="□", grade2="□", grade3="□";
		switch(ObjectUtils.defaultIfNull( m.get("GRADE"),"").toString().charAt(0)){
		case '1' : grade1="■";
		break ;
		case '2' : grade2="■";
		break ;
		case '3' : grade3="■";
		break ;
		}
		text = new Paragraph("等級(勾選) "+ grade1 +"1★　　 "+ grade2 +"2★★　　 "+ grade3 +"3★★★", nFontB12);
		text.setAlignment(Paragraph.ALIGN_LEFT);
		cell1.addElement(text);
	
		String[] source = new String[]{"", "", "", "", "", "", "", "", ""};
	
		try {
			source[Integer.parseInt(isNotNull(m.get("COMPLAIN_SOURCE"))) - 1] = "V";
		} catch (Exception e) {
//			e.printStackTrace();
		}
	
		text = new Paragraph("客訴來源：", nFontB12);
		text.add(new Chunk("　　"+ source[0] +"　　",nFontB12).setUnderline(0.2f, -2f));
		text.add(new Chunk("銀行局(電話)",nFontB12));
		text.add(new Chunk("　　"+ source[1] +"　　",nFontB12).setUnderline(0.2f, -2f));
		text.add(new Chunk("銀行局(書函) ",nFontB12));
		text.add(new Chunk("　　"+ source[2] +"　　",nFontB12).setUnderline(0.2f, -2f));
		text.add(new Chunk("其他主管機關",nFontB12));
		text.add(new Chunk("　　"+ source[3] +"　　",nFontB12).setUnderline(0.2f, -2f));
		text.add(new Chunk("存證信函",nFontB12));
		text.add(new Chunk("　　"+ source[4] +"　　",nFontB12).setUnderline(0.2f, -2f));
		text.add(new Chunk("律師/陳情/書函",nFontB12));
		text.add(new Chunk("　　"+ source[5] +"　　",nFontB12).setUnderline(0.2f, -2f));
		text.add(new Chunk("臨櫃",nFontB12));
		text.add(new Chunk("　　"+ source[6] +"　　",nFontB12).setUnderline(0.2f, -2f));
		text.add(new Chunk("電話",nFontB12));
		text.add(new Chunk("　　"+ source[7] +"　　",nFontB12).setUnderline(0.2f, -2f));
		text.add(new Chunk("客服",nFontB12));
		text.add(new Chunk("　　"+ source[8] +"　　",nFontB12).setUnderline(0.2f, -2f));
		text.add(new Chunk("金融消費評議中心",nFontB12));
		text.setAlignment(Paragraph.ALIGN_LEFT);
		cell1.addElement(text);
	
		text = new Paragraph("", nFont12);
		text.add(new Chunk("客訴摘要：", nFontB12));
		text.add(new Chunk(ObjectUtils.defaultIfNull( m.get("COMPLAIN_SUMMARY"),"　　　　") + " ", nFont12));
		text.add(new Paragraph("\n"));
		text.setAlignment(Paragraph.ALIGN_LEFT);
		cell1.addElement(text);
		table.addCell(cell1);
		document = setPdfPTable(document, rowCnt + 5, table);
		//.....................................................................
		document.add(new Paragraph("\n")); //空行
		//.....................................................................
		table = getPdfPTable();
		cell1 = new PdfPCell();
		text = new Paragraph("A.客戶基本資料\n", nFontB12);
		text.setAlignment(Paragraph.ALIGN_LEFT);
		cell1.addElement(text);
		text = new Paragraph("姓名：", nFontB12);
		text.add(new Chunk("　"+ ObjectUtils.defaultIfNull( m.get("CUST_NAME"),"　").toString() +"　",nFontB12).setUnderline(0.2f, -2f));        
	
		String brithdate = "";//生日轉成yyyy/mm/dd格式
		brithdate =ObjectUtils.defaultIfNull( m.get("BIRTHDATE"),"").toString();
		if(brithdate!=null && brithdate!="")
			brithdate = dateFormatOfNoSlash.format(m.get("BIRTHDATE")).toString();
		else
			brithdate="  ";
		text.add(new Chunk("出生年月日(西元)：",nFontB12));
		text.add(new Chunk("　"+ brithdate +"　",nFontB12).setUnderline(0.2f, -2f));
		text.add(new Chunk("身分證字號：",nFontB12));
		text.add(new Chunk("　"+ ObjectUtils.defaultIfNull( m.get("CUST_ID")," 　 ").toString() +"　",nFontB12).setUnderline(0.2f, -2f));
		text.add(new Chunk("職業：",nFontB12));
		text.add(new Chunk("　"+ ObjectUtils.defaultIfNull( m.get("OCCUP")," 　 ").toString() +"　",nFontB12).setUnderline(0.2f, -2f));
		text.setAlignment(Paragraph.ALIGN_LEFT);
		cell1.addElement(text);
		text = new Paragraph("聯絡電話：", nFontB12);
		text.add(new Chunk("　"+ ObjectUtils.defaultIfNull( m.get("PHONE"),"　　").toString() +"　",nFontB12).setUnderline(0.2f, -2f));
		text.add(new Chunk("學歷：",nFontB12));
		text.add(new Chunk("　"+ ObjectUtils.defaultIfNull( m.get("EDUCATION"),"　　").toString() +"　",nFontB12).setUnderline(0.2f, -2f));
		text.add(new Paragraph("\n"));
		cell1.addElement(text);
	
		text = new Paragraph("B.往來資訊", nFontB12);
		text.setAlignment(Paragraph.ALIGN_LEFT);
		cell1.addElement(text);
	
		String open_acc_date = "";//開戶日期轉成yyyy/mm/dd格式
		open_acc_date =ObjectUtils.defaultIfNull( m.get("OPEN_ACC_DATE"),"").toString();
		if(open_acc_date!=null && open_acc_date!="")
			open_acc_date = dateFormatOfNoSlash.format(m.get("OPEN_ACC_DATE")).toString();
		text = new Paragraph("開戶日期：", nFontB12);
		text.add(new Chunk("　"+ open_acc_date +"　",nFontB12).setUnderline(0.2f, -2f));
		text.add(new Chunk("總往來資產AUM：",nFontB12));
		text.add(new Chunk("　"+ ObjectUtils.defaultIfNull( m.get("TOTAL_ASSET"),"　　").toString() +"　",nFontB12).setUnderline(0.2f, -2f));
		text.setAlignment(Paragraph.ALIGN_LEFT);
		cell1.addElement(text);
		String checkSheet1="", checkSheet2="", checkSheet3="", checkSheet4="";
		
		String checkSheet = checkMap(m, "CHECK_SHEET");
		if (!"".equals(checkSheet)){
			switch(checkSheet){
			case "1" : checkSheet1="V";
			break ;
			case "2" : checkSheet2="V";
			break ;
			case "3" : checkSheet3="V";
			break ;
			case "4" : checkSheet4="V";
			break ;
			}
		}
		text = new Paragraph("對帳單類別:", nFontB12);
		text.add(new Chunk("　　"+ checkSheet1 +"　　",nFontB12).setUnderline(0.2f, -2f));
		text.add(new Chunk("信託",nFontB12));
		text.add(new Chunk("　　"+ checkSheet2 +"　　",nFontB12).setUnderline(0.2f, -2f));
		text.add(new Chunk("白金對帳單",nFontB12));
		text.add(new Chunk("　　"+ checkSheet3 +"　　",nFontB12).setUnderline(0.2f, -2f));
		text.add(new Chunk("電子",nFontB12));
		text.add(new Chunk("　　"+ checkSheet4 +"　　",nFontB12).setUnderline(0.2f, -2f));
		text.add(new Chunk("自取或不寄送",nFontB12));
		text.add(new Paragraph("\n"));
		text.setAlignment(Paragraph.ALIGN_LEFT);
		cell1.addElement(text);
	
		text = new Paragraph("C.往來商品項目", nFontB12);
		text.setAlignment(Paragraph.ALIGN_LEFT);
		cell1.addElement(text);
		
		text = new Paragraph("銀行：", nFontB12);
		text.add(new Chunk( "　　　　　　" + ObjectUtils.defaultIfNull( m.get("BUY_PRODUCT_TYPE"),"　　　　　　").toString() +"　　　　　　",nFont12).setUnderline(0.2f, -2f));
		
		//確認此客訴單是否有附（加附投資明細損益表）
		if (fileIsExist(isNotNull(inputVO.getComplainListId()))){
			text.add(new Chunk("（加附投資明細損益表）",nFontB12));	
		}
		text.setAlignment(Paragraph.ALIGN_LEFT);
		cell1.addElement(text);
		text = new Paragraph("集團：", nFontB12);
		text.add(new Chunk("　　　　　　　　　　　　　　　　　　",nFont12).setUnderline(0.2f, -2f));
		text.setAlignment(Paragraph.ALIGN_LEFT);
		cell1.addElement(text);
		//text = new Paragraph("主要客訴商品與期投資金額：＿＿＿＿＿＿＿＿＿＿＿＿＿＿＿＿＿＿＿＿\n\n", nFont12);
		text = new Paragraph("主要客訴項目或商品╱金額：", nFontB12);
		text.add(new Chunk("　　　("+ ObjectUtils.defaultIfNull( m.get("COMPLAIN_PRODUCT"),"").toString() +")",nFont12).setUnderline(0.2f, -2f));
		if (!ObjectUtils.defaultIfNull(m.get("COMPLAIN_PRODUCT_CURRENCY"),"").toString().equals(""))
			text.add(new Chunk("　"+ ObjectUtils.defaultIfNull( m.get("COMPLAIN_PRODUCT_CURRENCY"),"").toString() +"：",nFont12).setUnderline(0.2f, -2f));
		else
			text.add(new Chunk("　　　　　　　",nFont12).setUnderline(0.2f, -2f));
		if (!ObjectUtils.defaultIfNull(m.get("COMPLAIN_PRODUCT_AMOUNT"),"").toString().equals(""))
			text.add(new Chunk("╱ $" + ObjectUtils.defaultIfNull( m.get("COMPLAIN_PRODUCT_AMOUNT"),"　　　　　　　").toString() +"　　",nFont12).setUnderline(0.2f, -2f));
		else
			text.add(new Chunk("　　　　　　　",nFont12).setUnderline(0.2f, -2f));
		text.add(new Paragraph("\n"));
		text.setAlignment(Paragraph.ALIGN_LEFT);
		cell1.addElement(text);
		table.addCell(cell1);
		document = setPdfPTable(document, rowCnt + 10, table);
		//.....................................................................
		document.add(new Paragraph("\n")); //空行
		//.....................................................................
		table = getPdfPTable();
		cell1 = new PdfPCell();
		//text = new Paragraph("問題實況（請金服主管詳載過程、包括聯繫時間、對象、內容、結果......等等資訊）\n"nFontB1212);
		String problem = ObjectUtils.defaultIfNull( m.get("PROBLEM_DESCRIBE"),"").toString();
	
		text = new Paragraph("", nFont12);
		text.add(new Chunk("問題實況", nFontB12));
		text.add(new Chunk("（請詳載過程、包括時間、對象、內容、結果......等資訊）\n", nFontB12));
		text.add(new Chunk(problem, nFont12));
		text.add(new Paragraph("\n"));
		text.setAlignment(Paragraph.ALIGN_LEFT);
		cell1.addElement(text);
		table.addCell(cell1);
		document = setPdfPTable(document, rowCnt + 3 + problem.split("\n").length, table);
		//.....................................................................
		document.add(new Paragraph("\n")); //空行
		//.....................................................................
		table = getPdfPTable();
		cell1 = new PdfPCell();
		//text = new Paragraph("客戶訴求（請確認客戶實際訴求或附註其應含訴求、並註明訴求金額）\n", nFont12);
		String desc = ObjectUtils.defaultIfNull( m.get("CUST_DESCRIBE"),"").toString();
		text = new Paragraph("", nFont12);
		text.add(new Chunk("客戶訴求", nFontB12));
		text.add(new Chunk("（請確認客戶實際訴求或隱含訴求、並註明訴求金額）\n", nFontB12));
		text.add(new Chunk(desc, nFont12));
		text.add(new Paragraph("\n"));
		text.setAlignment(Paragraph.ALIGN_LEFT);
		cell1.addElement(text);
		table.addCell(cell1);
		document = setPdfPTable(document, rowCnt + 3 + desc.split("\n").length, table);
		//.....................................................................
		document.add(new Paragraph("\n")); //空行
		//.....................................................................
	
		table = getPdfPTable();
		cell1 = new PdfPCell();
		cell1.setBorderWidth(0);
		cell1.setFixedHeight(30);
		text = new Paragraph("第一級處理情形\n", nFontB12);
		text.setAlignment(Paragraph.ALIGN_LEFT);
		cell1.addElement(text);
		table.addCell(cell1);
		document= setPdfPTable(document, rowCnt + 1, table);
	
		table = getPdfPTable();
		cell1 = new PdfPCell();
		String condition1 = ObjectUtils.defaultIfNull( m.get("HANDLE_CONDITION1"),"").toString();
		text = new Paragraph("", nFont12);
		text.add(new Chunk("各單位處理：（請詳載處理過程、並務必釐清服務或銷售流程是否皆已依規辦理）\n", nFontB12));
		text.add(new Chunk(condition1, nFont11));
		text.add(new Paragraph("\n"));
		text.setAlignment(Paragraph.ALIGN_LEFT);
		cell1.addElement(text);
		table.addCell(cell1);
		document= setPdfPTable(document, rowCnt + 1 + condition1.split("\n").length, table);
	
		//.....................................................................
		document.add(new Paragraph("\n")); //空行
	
		table = getPdfPTable();
		cell1 = new PdfPCell();
		cell1.setBorderWidth(0);
		cell1.setFixedHeight(30);
		text = new Paragraph("第二級處理情形\n", nFontB12);
		text.setAlignment(Paragraph.ALIGN_LEFT);
		cell1.addElement(text);
		table.addCell(cell1);
		document= setPdfPTable(document, rowCnt + 1, table);
		//.....................................................................
		table = getPdfPTable();
		cell1 = new PdfPCell();
		String condition2 = ObjectUtils.defaultIfNull( m.get("HANDLE_CONDITION2"),"").toString();
		text = new Paragraph("", nFont12);
		text.add(new Chunk("營運督導/客服科長：（若各單位處理無效，由督導代表總行協助處理）\n", nFontB12));
		text.add(new Chunk(condition2, nFont11));
		text.add(new Paragraph("\n"));
		text.setAlignment(Paragraph.ALIGN_LEFT);
		cell1.addElement(text);
		table.addCell(cell1);
		document= setPdfPTable(document, rowCnt + 1 + condition2.split("\n").length, table);
	
		//.....................................................................
		document.add(new Paragraph("\n")); //空行
	
		table = getPdfPTable();
		cell1 = new PdfPCell();
		cell1.setBorderWidth(0);
		cell1.setFixedHeight(30);
		text = new Paragraph("第三級處理情形\n", nFontB12);
		text.setAlignment(Paragraph.ALIGN_LEFT);
		cell1.addElement(text);
		table.addCell(cell1);
		document= setPdfPTable(document, rowCnt + 1, table);
		//.....................................................................
		table = getPdfPTable();
		cell1 = new PdfPCell();
		String condition3 = ObjectUtils.defaultIfNull( m.get("HANDLE_CONDITION3"),"").toString();
		text = new Paragraph("", nFont12);
		text.add(new Chunk("總行處理：（此欄位由總行處理單位人員填寫）\n", nFontB12));
		text.add(new Chunk(condition3, nFont11));
		text.add(new Paragraph("\n"));
		text.setAlignment(Paragraph.ALIGN_LEFT);
		cell1.addElement(text);
		table.addCell(cell1);
		document= setPdfPTable(document, rowCnt + 2 + condition3.split("\n").length, table);
	
	
		//.....................................................................
		document.add(new Paragraph("\n", nFont8)); //空行
		//.....................................................................
		table = getPdfPTable();
		cell1 = new PdfPCell();
		cell1.setFixedHeight(70);
		//text = new Paragraph("客戶訴求（請確認客戶實際訴求或附註其應含訴求、並註明訴求金額）\n", nFont12);
		text = new Paragraph("結案日期:", nFont12);
		String endDate = ObjectUtils.defaultIfNull( m.get("END_DATE"),"").toString();
	
		if (endDate!=null && endDate !="") {
			endDate = dateFormatOfNoSlash.format(m.get("END_DATE")).toString();
		} else {
			endDate = "　　　";
		}
	
		text.add(new Chunk("　　"+ endDate +"　　",nFont12).setUnderline(0.2f, -2f));
	
		StringBuffer sb = new StringBuffer("\n處理人員:");
		System.out.println("EDITOR_CONDITION1:" + m.get("EDITOR_CONDITION1") + "EDITOR_CONDITION2:" + m.get("EDITOR_CONDITION2") + "EDITOR_CONDITION3:" + m.get("EDITOR_CONDITION3") + "EDITOR_CONDITION4:" + m.get("EDITOR_CONDITION4"));
		if (!"".equals(isNotNull(m.get("EDITOR_CONDITION1")))) {
			sb.append("第一級-" + isNotNull(m.get("EDITOR_CONDITION1_NM"))).append("    ");
		}
		if (!"".equals(isNotNull(m.get("EDITOR_CONDITION2")))) {
			sb.append("第二級-" + isNotNull(m.get("EDITOR_CONDITION2_NM"))).append("    ");
		}
		if (!"".equals(isNotNull(m.get("EDITOR_CONDITION3")))) {
			sb.append("第三級-" + isNotNull(m.get("EDITOR_CONDITION3_NM"))).append(", ");
			if (!"".equals(isNotNull(m.get("EDITOR_CONDITION4")))) {
				sb.append(isNotNull(m.get("EDITOR_CONDITION4_NM")));
			}
		}
		//Jay	201508030465-00	調整理規系統客訴資料庫功能
		if (!"".equals(isNotNull(m.get("EDITOR_CONDITION5")))) {
			sb.append("\n覆核人員:第四級-" + isNotNull(m.get("EDITOR_CONDITION5_NM"))).append("    ");
		}
		if (!"".equals(isNotNull(m.get("EDITOR_CONDITION6")))) {
			sb.append("第五級-" + isNotNull(m.get("EDITOR_CONDITION6_NM"))).append("    ");
		}
		text.add(new Chunk(sb.toString(), nFont12));
		text.setAlignment(Paragraph.ALIGN_LEFT);
		cell1.addElement(text);
		table.addCell(cell1);
		document = setPdfPTable(document, rowCnt + 3, table);
	
		document.close();
		notifyClientToDownloadFile(TmpReportPath +   uuid, fileName);		
	}

	/**
	 * get
	 * @return PdfPTable
	 * @throws Exception 
	 */
	private PdfPTable getPdfPTable() throws Exception {
		PdfPTable table = new PdfPTable(1);
		int[] widths = new int[] {84};
		table.setWidths(widths);
		table.setWidthPercentage(98);
		return table;
	}

	/**
	 * set
	 * @return PdfPTable
	 * @throws Exception 
	 */
	private Document setPdfPTable(Document document, int rowCnt, PdfPTable table) throws Exception {
		if (rowCnt > 70) {
			document.newPage();
		}
		document.add(table);
		return document;
	}

	/**
	 * 匯出Excel
	 * @param body
	 * @param header
	 * @return void
	 * @throws Exception 
	 * @see CRM999#queryCustomerComplainList
	 */
	public void expExcel(Object body, IPrimitiveMap<?> header) throws Exception {
		CRM999InputVO inputVO  = (CRM999InputVO) body;
		
		//撈取所需資料
		List<Map<String, Object>> list = (List<Map<String, Object>>)queryCustomerComplainData(inputVO);
		
		//相關資訊設定
		String fileName = String.format("客訴資料%s.xlsx", dateFormatOfNoSlash.format(new Date()));
		String uuid = UUID.randomUUID().toString();
		
		//建置Excel
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("客訴資料" + dateFormatOfNoSlash.format(new Date()));
		sheet.setDefaultColumnWidth(20);
		sheet.setDefaultRowHeightInPoints(20);
		
		// 表頭 CELL型式
		XSSFCellStyle headingStyle = workbook.createCellStyle();
		headingStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
		headingStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
		headingStyle.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);// 填滿顏色
		headingStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		headingStyle.setBorderBottom((short) 1);
		headingStyle.setBorderTop((short) 1);
		headingStyle.setBorderLeft((short) 1);
		headingStyle.setBorderRight((short) 1);
		headingStyle.setWrapText(true);

		
		String[] mainLine = {"FMT_CREATE_DATE", "COMPLAIN_TYPE", "COMPLAIN_MODE", "COMPLAIN_REQUEST",
				"HANDLE_STATUS", "CUST_NAME", "CUST_ID", "COMPLAIN_PRODUCT", "COMPLAIN_PRODUCT_CURRENCY", "COMPLAIN_PRODUCT_AMOUNT",
				"AO_CODE", "CUST_KIND", "PERSON_NAME", "BRANCH_ID", "FMT_END_DATE", "COST_TIME", "REMARKS", "DELIBERATE",
				"COMPLAIN_PRODUCT", "FMT_BUY_DATE", "GRADE", "HANDLE_STEP"};
		//header
		Integer index = 0; // first row
		XSSFRow row = sheet.createRow(index);
		XSSFCell cellHead = row.createCell(6);
		cellHead.setCellStyle(headingStyle);
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 6, 8));
		String dateStartStr = "";
		String dateEndStr = "";
		if (inputVO.getStartDate()!=null) {
			 dateStartStr = dateFormatOfSlash.format(inputVO.getStartDate());
		} 
		if (inputVO.getEndDate()!=null) {
			dateEndStr = dateFormatOfSlash.format(inputVO.getEndDate());
		}
		
		cellHead.setCellValue("日期:" + dateStartStr + "~" + dateEndStr);

		index++;
		
		// 資料 CELL型式
		XSSFCellStyle mainStyle = workbook.createCellStyle();
		mainStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
		mainStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
		mainStyle.setBorderBottom((short) 1);
		mainStyle.setBorderTop((short) 1);
		mainStyle.setBorderLeft((short) 1);
		mainStyle.setBorderRight((short) 1);

		if (list.size() > 0) {
			for (Map<String, Object> map : list) {
				row = sheet.createRow(index);

				if (map.size() > 0) {
					for (int j = 0; j < mainLine.length; j++) {
						XSSFCell cell = row.createCell(j);
						cell.setCellStyle(mainStyle);
						cell.setCellValue(checkMap(map, mainLine[j]));
					}

					index++;
				}
			}
		} else {
			row = sheet.createRow(index);
			XSSFCell cell = row.createCell(0);
			cell.setCellStyle(mainStyle);
			cell.setCellValue("查無資料！");

		}
		
		File targetFile = new File(filePath, uuid);
		FileOutputStream fos = new FileOutputStream(targetFile);
		
		workbook.write(fos);
		workbook.close();
		notifyClientToDownloadFile(TmpPath + uuid, fileName);
	}

	/**
	 * 列印備查簿<br>
	 * Rex 201409090273-00 組織調整修改客訴資料庫內容
	 * @param body
	 * @param header
	 * @return void
	 * @throws Exception 
	 * @see CRM999#queryCustomerComplainList
	 */
	public void expMemo(Object body, IPrimitiveMap<?> header) throws Exception {
		CRM999InputVO inputVO  = (CRM999InputVO) body;
		
		//撈取所需資料
		List<Map<String, Object>> list = (List<Map<String, Object>>)queryCustomerComplainData(inputVO);
		
		//相關資訊設定
		String fileName = String.format("客訴備忘錄%s.xlsx", dateFormatOfNoSlash.format(new Date()));
		String uuid = UUID.randomUUID().toString();
		
		//建置Excel
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("客訴備忘錄" + dateFormatOfNoSlash.format(new Date()));
		sheet.setDefaultColumnWidth(20);
		sheet.setDefaultRowHeightInPoints(20);
		
		//Subject font型式
		XSSFFont font = workbook.createFont();
		font.setColor(HSSFColor.BLACK.index);
		font.setBoldweight((short)Font.NORMAL);
		font.setFontHeight((short)500);

		// Subject cell型式
		XSSFCellStyle SubjectStyle = workbook.createCellStyle();
		SubjectStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
		SubjectStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
		SubjectStyle.setBorderBottom((short) 1);
		SubjectStyle.setBorderTop((short) 1);
		SubjectStyle.setBorderLeft((short) 1);
		SubjectStyle.setBorderRight((short) 1);
		SubjectStyle.setWrapText(true);
		SubjectStyle.setFont(font);

		// 表頭 CELL型式
		XSSFCellStyle headingStyle = workbook.createCellStyle();
		headingStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
		headingStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
		headingStyle.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);// 填滿顏色
		headingStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		headingStyle.setBorderBottom((short) 1);
		headingStyle.setBorderTop((short) 1);
		headingStyle.setBorderLeft((short) 1);
		headingStyle.setBorderRight((short) 1);
		headingStyle.setWrapText(true);

		Integer index = 0; // first row
		
		//Subject
		XSSFRow row = sheet.createRow(index);
		row.setHeight((short)800);
		XSSFCell sCell = row.createCell(0);
		sCell.setCellStyle(SubjectStyle);
		sCell.setCellValue("個　人　金　融　總　處　客　訴　備　查　簿　");
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 5));

		//日期
		String dateStartStr = "";
		String dateEndStr = "";
		if (inputVO.getStartDate()!=null) {
			 dateStartStr = dateFormatOfSlash.format(inputVO.getStartDate());
		} 
		if (inputVO.getEndDate()!=null) {
			dateEndStr = dateFormatOfSlash.format(inputVO.getEndDate());
		}

		XSSFCell sCell2 = row.createCell(6);
		sCell2.setCellStyle(SubjectStyle);
		sheet.addMergedRegion(new CellRangeAddress(0,0,6,10));
		sCell2.setCellValue("日期:" + dateStartStr + "~" + dateEndStr);
		
		index++;
		
		//header
		String[] headerLine1 = {"日期","編號","客訴等級","客戶姓名","身分證字號","客訴摘要/訴求","處理情形","結案日期","第1級處理人員","第2級處理人員","第3級處理人員"};   //,"第4級處理人員","第5級處理人員"
		String[] mainLine    = {"FMT_CREATE_DATE","COMPLAIN_LIST_ID","GRADE","CUST_NAME","CUST_ID","CUST_DESCRIBE","HANDLE_CONDITION","FMT_END_DATE","EDITOR_CONDITION1","EDITOR_CONDITION2","EDITOR_CONDITION3"}; //,"EDITOR_CONDITION4","EDITOR_CONDITION5"
		Integer startFlag = 0;
		Integer endFlag = 0;
		ArrayList<String> tempList = new ArrayList<String>(); //比對用
		
	    row = sheet.createRow(index);
		for (int i = 0; i < headerLine1.length; i++) {
			String headerLine = headerLine1[i];
			if (tempList.indexOf(headerLine) < 0) {
				tempList.add(headerLine);
				XSSFCell cell = row.createCell(i);
				cell.setCellStyle(headingStyle);
				cell.setCellValue(headerLine1[i]);

				if (endFlag != 0) {
					sheet.addMergedRegion(new CellRangeAddress(0, 0, startFlag, endFlag)); // firstRow, endRow, firstColumn, endColumn
				}
				startFlag = i;
				endFlag = 0;
			} else {
				endFlag = i;
			}
		}
		if (endFlag != 0) { //最後的CELL若需要合併儲存格，則在這裡做
			sheet.addMergedRegion(new CellRangeAddress(0, 0, startFlag, endFlag)); // firstRow, endRow, firstColumn, endColumn
		}
		
		index++;
		
		// 資料 CELL型式
		XSSFCellStyle mainStyle = workbook.createCellStyle();
		mainStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
		mainStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
		mainStyle.setBorderBottom((short) 1);
		mainStyle.setBorderTop((short) 1);
		mainStyle.setBorderLeft((short) 1);
		mainStyle.setBorderRight((short) 1);
		if (list.size() > 0) {
			for (Map<String, Object> map : list) {
				row = sheet.createRow(index);

				if (map.size() > 0) {
					for (int j = 0; j < mainLine.length; j++) {
						XSSFCell cell = row.createCell(j);
						cell.setCellStyle(mainStyle);
						if (mainLine[j].indexOf("HANDLE_CONDITION") >= 0){ //處理狀態欄位
							String datastr = "";
							if (map.containsKey("HANDLE_CONDITION1") && (map.get("HANDLE_CONDITION1") != null)) {
								if (map.get("HANDLE_CONDITION1") != null) {
									datastr += "各單位處理：" + map.get("HANDLE_CONDITION1") + "\n";
								}
								if (map.get("HANDLE_CONDITION2") != null) {
									datastr += "區督導處理：" + map.get("HANDLE_CONDITION2") + "\n";
								}
								if (map.get("HANDLE_CONDITION3") != null) {
									datastr += "總行處理　：" + map.get("HANDLE_CONDITION3");
								} 
							}
							cell.setCellValue(datastr);

						} else if(mainLine[j].indexOf("EDITOR_CONDITION3") >= 0) { //第3級處理人員欄位
							String editor = "";
							if (map.containsKey("EDITOR_CONDITION3")) {
								  editor = isNotNull(map.get("EDITOR_CONDITION3"))
										+ isNotNull(map.get("EDITOR_CONDITION3_NM"));
//	             						+ "\n"
//	             						+ isNotNull(map.get("EDITOR_CONDITION4"))
//	             						+ isNotNull(map.get("EDITOR_CONDITION4_NM"));
							 }
							cell.setCellValue(editor);
						
						} else { //其餘欄位
							cell.setCellValue(checkMap(map, mainLine[j]));
						}
					}

					index++;
				} 
			}
		} else { 
			row = sheet.createRow(index);
			XSSFCell cell = row.createCell(0);
			cell.setCellStyle(mainStyle);
			cell.setCellValue("查無資料！");
		}
		
		File targetFile = new File(filePath, uuid);
		FileOutputStream fos = new FileOutputStream(targetFile);
		
		workbook.write(fos);
		workbook.close();
		notifyClientToDownloadFile(TmpPath + uuid, fileName);
	}

	public void showComplainFlow(Object body, IPrimitiveMap<?> header) throws Exception {
		CRM999InputVO inputVO = (CRM999InputVO)body;
		CRM999OutputVO returnVO = new CRM999OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("select DTL.EMP_FROM, DTL.EMP_TO, DTL.STATUS, DTL.DATE_FROM, DTL.REASON, ");
		sql.append("INFO1.EMP_NAME EMP_FROM_NAME, decode(DTL.EMP_TO,'sys','系統',INFO2.EMP_NAME) EMP_TO_NAME ");
		sql.append("from peopsoft.PS_FP_COMPLAIN_FLOWDTL DTL ");
		sql.append("left join TBORG_MEMBER INFO1 on DTL.EMP_FROM = INFO1.EMP_ID ");
		sql.append("left join TBORG_MEMBER INFO2 on DTL.EMP_TO = INFO2.EMP_ID ");
		sql.append("where complain_list_id = :complainListId ");
		sql.append("order by DATE_FROM desc ");
	
		queryCondition.setQueryString(sql.toString());
		queryCondition.setObject("complainListId", inputVO.getComplainListId());
		
		try {
			ResultIF list = 
					dam.executePaging(queryCondition,
					inputVO.getCurrentPageIndex() + 1,
					inputVO.getPageCount());
			if (list != null) {
				int totalPage_i = list.getTotalPage();
				int totalRecord_i = list.getTotalRecord();
				returnVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());// 當前頁次
				returnVO.setTotalPage(totalPage_i);// 總頁次
				returnVO.setTotalRecord(totalRecord_i);// 總筆數
			}
			returnVO.setResultList1(list);
		} catch(Exception e) {
			throw new APException("系統發生錯誤請洽系統管理員");
		}
		
		this.sendRtnObject(returnVO);
	}

	private String getUserPsSaJobTitle(List jobTitleList) {
		//Ray	201312040109-00	人員同時督導及金服主管權限時則以督導為主要權限
		//Eli 20171011 以最大權限為主要權限
		String jobTitle = null;
		if (jobTitleList.contains("A12")) {
			jobTitle = "A12";
		} else if (jobTitleList.contains("P01")) {
			jobTitle = "P01";
		} else if (jobTitleList.contains("B05")) {
			jobTitle = "B05";
		} else {
			Iterator it = jobTitleList.iterator();
			while(it.hasNext()) {
				Object nextObj = it.next();
				if (null != nextObj) {
					jobTitle = nextObj.toString();
					break;
				}
			}
		}
		return jobTitle;
	}

	/**
	 * 取得A01List
	 * @param  inputVO:CRM999InputVO
	 * @return List
	 * @throws Exception 
	 * @see CRM999#queryCustomerComplainList
	 */
	private List setA01List(CRM999InputVO inputVO) throws Exception {
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = null;
		String[] type = new String[]{"COMPLAIN_TYPE", "COMPLAIN_MODE", "COMPLAIN_REQUEST", "HANDLE_STATUS"};
		String sql = "";
		ArrayList datas = new ArrayList();
		for (int i = 0; i < type.length; i++) {
			sql = "select SEQ, NAME from peopsoft.PS_FP_COMPLAIN_MTN where TYPE=? and (STATUS is null or STATUS<>'E') order by SEQ";
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			queryCondition.setQueryString(sql);
			queryCondition.setString(0, type[i]);
			datas.add(dam.exeQuery(queryCondition));
		}
	
		return datas;
	}

	/**
	 * 判斷有無上傳投資明細損益表
	 * @param id
	 * @return boolean (true:有檔案，false:無檔案)
	 * @throws Exception 
	 */
	private boolean fileIsExist(String id) throws Exception {
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		sb.append("select UPLOAD_FILE from peopsoft.PS_FP_CUST_COMPLAIN_LIST where COMPLAIN_LIST_ID = :id ");
		queryCondition.setObject("id", id);
		queryCondition.setQueryString(sb.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		String errorMag = null;
		
		try {
			if (list.size() > 0 && list.get(0).get("UPLOAD_FILE") != null) {
				Blob blob = (Blob) list.get(0).get("UPLOAD_FILE");
				int blobLength = (int) blob.length();
				if (blobLength > 0) { //有上傳檔案，回傳true
					return true;
				}
			}
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
		
		if (StringUtils.isNotBlank(errorMag)) {
			throw new APException(errorMag);
		}
		return false;
	}

	/**
	 * 取得JobLv1、JobLv2的下一流程預設人員<br>
	 * @param bufferString
	 * @param inputVO
	 * @return void
	 * @throws Exception 
	 */
	private void setNextEmp(String bufferString, CRM999InputVO inputVO) throws Exception {
		dam = this.getDataAccessManager();
		String territory  = isNotNull(inputVO.getTerritoryId()); //使用者分行
		String jobLv = isNotNull(inputVO.getJobLevel());		 //處理人員等級[1,2,3,A,A1,A2(總行)] 
		
		StringBuffer nextEmpBuffer = new StringBuffer().append(bufferString);
		if ("1".equals(jobLv)) {
			nextEmpBuffer.append("where D.SA_JOB_TITLE_ID in ('B01','B05') and a.DEPT_ID in (select DEPT_ID from WMSUSER.VWORG_DEFN_BRH where BRANCH_NBR = ?) ");
		} else if ("2".equals(jobLv)) {
			nextEmpBuffer.append("where a.DEPT_ID in ('171','172','174','012','105') and a.DEPT_ID in (select DEPT_ID from WMSUSER.VWORG_DEFN_BRH where BRANCH_NBR = ?) ");
		}
					
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		queryCondition.setQueryString(nextEmpBuffer.toString());
		queryCondition.setString(0, territory);
		List<Map> list = dam.exeQuery(queryCondition);
		
		if (list.size() > 0) { 
			inputVO.setNextEmp(checkMap(list.get(0), "PERSON_ID"));
		}
	}

	/**
	 * 取得下一流程人員List<br>
	 * EX: [南港 - 邱**** 190853, ... ]
	 * @param sql
	 * @return ArrayList
	 * @throws Exception 
	 */
	private ArrayList getNextEmpList (String sql) throws Exception {
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		queryCondition.setQueryString(sql);
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);

		ArrayList al = new ArrayList();
		HashMap map = new HashMap();
		for(Map<String, Object> m : list) {
			map.put("PERSON_ID", m.get("PERSON_ID"));
			map.put("NAME", m.get("DESCR") + " - " + m.get("NAME") + " (" + m.get("PERSON_ID") + ")");
			al.add(map.clone());
			m.clear();
		}
		return al;
	}

	/**
	 * SQL execute service
	 * @param sqls String[]
	 * @param params Object[]
	 * @return boolean:執行時是否有錯誤
	 * @throws Exception 
	 */
	private boolean execute(String[] sqls, Object[] params) throws Exception{
		QueryConditionIF condition = null;
		dam = this.getDataAccessManager();
		
		//執行SQL立即commit
		dam.setAutoCommit(true);
		try {
			//Ray	201404160057-00	送給二級主管時的存取方式直接由PreparedStatement處理
			for (int i = 0; i < sqls.length; i++) {
				String str = sqls[i];
				if ("".equals(str)) continue;
				
				ArrayList arr = (ArrayList)params[i];
				for (int j = 0; j < arr.size(); j++) {
					//Ray	201405220240-00	特定符號轉為全型,避免顯示錯誤 (設置參數)
					str = str.replaceFirst("\\?", "'" + strFilter(arr.get(j)) + "'");
				}
				System.out.println(str);
				
				if(!("".equals(str))){
					condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
					//設置SQL
					condition.setQueryString(str);
					//執行
					dam.exeUpdate(condition);
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			//會影響到其他transaction，再此關閉autoCommit;
			dam.setAutoCommit(false);
		}

		return true;
	}

	/**
	 * 儲存客訴表-投資明細損益表
	 * @param inputVO CRM999InputVO
	 * @return boolean
	 * @throws Exception 
	 */
	private boolean uploadFile(CRM999InputVO inputVO) throws Exception{
		try{
			//先判斷有無上傳檔案，無上傳檔案不用繼續執行，會直接catch NullPointerException 並 return false
			File targetFile = new File(filePath, inputVO.getUploadFileName());
		
			//1、有檔案執行儲存邏輯
			Blob blob = null;
			dam = this.getDataAccessManager();
			QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			dam.setAutoCommit(true);//使SQL立即commit
			
			//2、準備SQL
			queryCondition.setQueryString("select UPLOAD_FILE from peopsoft.PS_FP_CUST_COMPLAIN_LIST where COMPLAIN_LIST_ID = :listId for update nowait ");
			queryCondition.setObject("listId", inputVO.getComplainListId());
			List<Map<String,Object>> list = dam.exeQuery(queryCondition);
			blob = (Blob)list.get(0).get("UPLOAD_FILE");
			
			InputStream in = new FileInputStream(targetFile);
			int length = (int)targetFile.length();
			byte bt[] = null;
			bt = new byte[length];
			in.read(bt);

			// 3、更新blob
			OutputStream outStream = blob.setBinaryStream(1);
			outStream.write(bt);
			outStream.flush();
			outStream.close();
			in.close();
		}catch(NullPointerException e) {
			return false;
		}catch(Exception e) {
			return false;
		}finally {
			//會影響到其他transaction，再此關閉autoCommit;
			dam.setAutoCommit(false);
		}
		return true;
	}

	/**
	 * 查詢客訴記錄  
	 * @param inputVO CRM999InputVO
	 * @return ResultIF (源自 dam.executePaging)
	 * @throws Exception 
	 */
	private List queryCustomerComplainList(CRM999InputVO inputVO) throws Exception {
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		queryCondition = this.setQueryConditionInfo(inputVO, queryCondition);
		return dam.exeQuery(queryCondition);
		//		try{
//			list = dam.executePaging(queryCondition,
//					inputVO.getCurrentPageIndex() + 1,
//					inputVO.getPageCount());
//
//		} catch (Exception e) {
//			logger.debug(String.format("發生錯誤:%s",StringUtil.getStackTraceAsString(e)));
//			throw new APException("系統發生錯誤請洽系統管理員");
//		}
//		return list;
	}

	private void setSaveComplainListId(CRM999InputVO inputVO) {
		inputVO.setComplainListId(formatComplainListId(inputVO.getCustId()));
	}

	private boolean handleStepIsEnd(CRM999InputVO inputVO) {
		return "E".equals(inputVO.getHandleStep());
	}

	private boolean checkB01ToSave(CRM999InputVO inputVO) {
		String jobTitleId = inputVO.getJobTitleId();
		String grade = inputVO.getGrade();
		return "B01".equals(jobTitleId) && ("1".equals(grade) || "2".equals(grade) || "3".equals(grade));
	}

	//Ray	201404160057-00	若為806的客訴則送出後由客服科長處理
	private boolean saveOnly(CRM999InputVO inputVO) {
		return "998".equals(inputVO.getTerritoryId()) || isFromCs(inputVO);
	}

	//是否為客服指派分行
	private boolean isFromCs(CRM999InputVO inputVO) {
		return "806".equals(inputVO.getTerritoryId()) && !inputVO.getTerritoryId().equals(inputVO.getBranchId());
	}

	private String formatComplainListId(String custId) {
		return (StringUtils.isNotBlank(custId)) ? custId + "-" + dateFormatOfStandardSecond.format(Calendar.getInstance().getTime()) : "0000000000-" + dateFormatOfStandardSecond.format(Calendar.getInstance().getTime()); //如果客戶統編為null以0取代統編;
	}

	private boolean isPreLevel(String jobTitle) {
		return "E01".equals(jobTitle) || "CS03".equals(jobTitle); 
	}

	private boolean isLevel1(String jobTitle) {
		return "C01".equals(jobTitle) || "C02".equals(jobTitle);
	}

	private boolean isLevel2(String jobTitle) {
		return "B01".equals(jobTitle) || "OP04".equals(jobTitle) || "CS02".equals(jobTitle) || "FCHM".equals(jobTitle) || "B02".equals(jobTitle);
	}

	private boolean isLevel3(String jobTitle) {
		return "P01".equals(jobTitle) || "A01".equals(jobTitle) || "A12".equals(jobTitle) || "CS01".equals(jobTitle) || "B04".equals(jobTitle);
	}

	private boolean isA01(String jobTitle) {
		return "A01".equals(jobTitle) || "A12".equals(jobTitle);
	}
	
	private boolean isOP01(String jobTitle) {
		return "OP01".equals(jobTitle);
	}
	
	private String strFilter(Object str) {
		String tmpStr = "";
		try {
			tmpStr = str.toString().trim();
			tmpStr = tmpStr.replaceAll("'", "’").replaceAll("\\$", "＄").replaceAll("\\?", "？");
		} catch (Exception e) {
		}
		return tmpStr;
	}

	/**
	 * 將參數去空白，否則為 ""
	 * @param obj
	 * @return string
	 */
	private String isNotNull(Object obj) {
		try {
			return obj.toString().trim();
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * 檢查Map取出欄位是否為Null
	 */
	private String checkMap(Map map, String key) {
		if (null != map && null != map.get(key)) {
			if ("CUST_ID".equals(key)) {
				return DataFormat.getCustIdMaskForHighRisk(String.valueOf(map.get(key)));
			} else {
				return String.valueOf(map.get(key));
			}
		} else {
			return "";
		}
	}
	
	
	private QueryConditionIF setQueryConditionInfo (CRM999InputVO inputVO, QueryConditionIF queryCondition) throws Exception {
		String loginUserId = (String)getUserVariable(FubonSystemVariableConsts.LOGINID); //登入者(使用者ID)

		boolean isJobLv1 = "Y".equals(inputVO.getCanAdd());		//是否為可新增帳號
		boolean is998 = "998".equals(inputVO.getTerritoryId());
		boolean is806 = "806".equals(inputVO.getTerritoryId());	//客服
		boolean isA01 = "Y".equals(inputVO.getIsA01());			//是否為總行

		System.out.println("is998:" + is998 + " isA01:" + isA01 + " is806:" + is806);

		StringBuffer sql = new StringBuffer();
		ArrayList paramList = new ArrayList();

		//多取一個CUST_ID1 僅供頁面上排序使用
		sql.append("SELECT TO_CHAR(p.CREATE_DATE, 'yyyy/MM/dd') AS FMT_CREATE_DATE, NVL2(BUY_DATE, TO_CHAR(p.BUY_DATE, 'yyyy/MM/dd'),'')AS FMT_BUY_DATE, NVL2(END_DATE, TO_CHAR(p.END_DATE,'yyyy/MM/dd'),'')AS FMT_END_DATE, HANDLE_STEP STEP_FOR_UI, ");
		sql.append("CASE WHEN p.NEXT_EMP='sys' THEN ' ' ELSE p.NEXT_EMP END AS NEXT_EMP_ID, p.*, p.CUST_ID as CUST_ID1 , p.BRANCH_ID || '-' || DEFN.DEPT_NAME AS BRANCH_ID_TEXT, ");
		sql.append("A1.EMP_NAME AS EDITOR_CONDITION1_NM,A2.EMP_NAME AS EDITOR_CONDITION2_NM,A3.EMP_NAME AS EDITOR_CONDITION3_NM,A4.EMP_NAME AS EDITOR_CONDITION4_NM,A5.EMP_NAME AS NEXT_EMP_NM ,A6.EMP_NAME AS EDITOR_CONDITION5_NM,A7.EMP_NAME AS EDITOR_CONDITION6_NM, CASE WHEN p.NEXT_EMP= ? THEN 'Y' ELSE 'N' END AS MYCASE ");

		paramList.add(loginUserId);

		sql.append( isA01 ? ", CASE WHEN p.END_DATE IS NULL THEN 'N' ELSE 'Y' END AS UNDO " : ", 'N' AS UNDO ");	//Ray	201307150187-00	開放總行可取消結案案件
		sql.append(" FROM peopsoft.PS_FP_CUST_COMPLAIN_LIST p ");
		sql.append(" LEFT JOIN WMSUSER.TBORG_DEFN DEFN ON p.BRANCH_ID = DEFN.DEPT_ID AND DEFN.ORG_TYPE = '50' AND DEFN.DEPT_ID <= '900' AND DEFN.DEPT_ID >= '200' ");
		sql.append(" LEFT JOIN WMSUSER.TBORG_MEMBER A1 ON P.EDITOR_CONDITION1=A1.EMP_ID LEFT JOIN WMSUSER.TBORG_MEMBER A2 ON P.EDITOR_CONDITION2=A2.EMP_ID LEFT JOIN WMSUSER.TBORG_MEMBER A3 ON P.EDITOR_CONDITION3=A3.EMP_ID LEFT JOIN WMSUSER.TBORG_MEMBER A4 ON P.EDITOR_CONDITION4=A4.EMP_ID LEFT JOIN WMSUSER.TBORG_MEMBER A5 ON P.NEXT_EMP = A5.EMP_ID LEFT JOIN WMSUSER.TBORG_MEMBER A6 ON P.EDITOR_CONDITION5 = A6.EMP_ID LEFT JOIN WMSUSER.TBORG_MEMBER A7 ON P.EDITOR_CONDITION6 = A7.EMP_ID WHERE ");

		//當使用編號當查詢條件時　 執行此條件 EX:編輯、列印 【初始資料】
		if (StringUtils.isNotBlank(inputVO.getComplainListId())) {
			sql.append(" ((STATUS IS NULL OR STATUS <> 'D') AND COMPLAIN_LIST_ID = ? ");
			paramList.add(inputVO.getComplainListId());
		}else { //【點擊查詢】
			if (is806) {
				sql.append(" (FROM_CS='Y' AND (STATUS IS NULL OR STATUS <> 'D')) OR ");
			}

			sql.append(" (NEXT_EMP = ? AND (STATUS IS NULL OR STATUS <> 'D')) OR ((STATUS IS NULL OR STATUS <> 'D') ");
			paramList.add(loginUserId);

			//客戶ID判斷
			if (StringUtils.isNotBlank(inputVO.getCustId())) {
				sql.append(" and p.CUST_ID =");

				if (isA01 || isJobLv1) {
					sql.append(" ?");
				} else {
					sql.append(" (select CUST_ID from WMSUSER.TBCRM_CUST_MAST where vip_degree");
					//總區督導審核7 Code 客戶
					if (is998){
						sql.append(" =");
					}else {
						sql.append(" <>");
					}
					sql.append(" 'V' and CUST_ID = ?)");
				}
				paramList.add(inputVO.getCustId());
			}

			//分行別判斷
			//Rex 201501290237-00  客訴資料庫總督導僅可查詢轄下分行
			if (is998) {
				sql.append(" and (BRANCH_ID in(SELECT BRANCH_NBR FROM WMSUSER.VWORG_DEFN_BRH WHERE DEPT_ID = (SELECT DEPT_ID FROM WMSUSER.TBORG_MEMBER WHERE EMP_ID =?)) OR BRANCH_ID = '998') ");
				paramList.add(loginUserId);
			} else if (StringUtils.isNotBlank(inputVO.getBranchId())) {
				if (isJobLv1){//第一層處理人員只能看所在的分行
					sql.append(" and BRANCH_ID = ?");
					paramList.add(inputVO.getBranchId());
				}
				else {
					//	其餘則顯示所管轄分行
					sql.append(" and BRANCH_ID =(SELECT BRANCH_NBR FROM WMSUSER.VWORG_DEFN_BRH WHERE DEPT_ID = (SELECT DEPT_ID FROM WMSUSER.TBORG_MEMBER WHERE EMP_ID =?) AND BRANCH_NBR = ?)");
					paramList.add(loginUserId);
					paramList.add(inputVO.getBranchId());
				}
			} else if ("Y".equals(inputVO.getIsA01())) {
				//總行 則顯示所管轄分行及自建客訴單


			} else if ("FCHM".equals(inputVO.getJobTitleId())) {
				sql.append(" and EDITOR_CONDITION2 = ? ");
				paramList.add(loginUserId);
			} else if (is806) {
				sql.append(" and (BRANCH_ID in(SELECT BRANCH_NBR FROM WMSUSER.VWORG_DEFN_BRH WHERE DEPT_ID = (SELECT DEPT_ID FROM WMSUSER.TBORG_MEMBER WHERE EMP_ID =?)) OR BRANCH_ID = '806') ");
				paramList.add(loginUserId);
			} else {
				//其餘則顯示所管轄分行
				sql.append(" and BRANCH_ID in(SELECT BRANCH_NBR FROM WMSUSER.VWORG_DEFN_BRH WHERE DEPT_ID = (SELECT DEPT_ID FROM WMSUSER.TBORG_MEMBER WHERE EMP_ID =?))");
				paramList.add(loginUserId);
			}

			// 起始日
			if (inputVO.getStartDate() != null) {
//			if (StringUtils.isNotBlank(inputVO.getStartDate())) {
				sql.append(" and ? <= TO_CHAR(CREATE_DATE,'yyyy/MM/dd') ");
				paramList.add(dateFormatOfSlash.format(inputVO.getStartDate()));
			}
//			// 迄止日
			if (inputVO.getEndDate() != null) {
				sql.append(" and ? >= TO_CHAR(CREATE_DATE,'yyyy/MM/dd')");
				paramList.add(dateFormatOfSlash.format(inputVO.getEndDate()));
			}

			//狀態
			if (StringUtils.isNotBlank(inputVO.getStatus()) && !"0".equals(inputVO.getStatus())) {
				sql.append(" and HANDLE_STEP = ?");
				paramList.add(inputVO.getStatus());
			}
		}
		sql.append(") order by MYCASE desc, CREATE_DATE desc");
		System.out.println("QUERY SQL ===========");
		System.out.println(sql.toString());
		System.out.println("===========");
		
		//設定QueryCondtion
		queryCondition.setQueryString(sql.toString());
		
		int index = 0; //參數索引
		for (Object param : paramList) {
			queryCondition.setString(index++, param.toString());
		}
		
		return queryCondition;
	}
	
	/**
	 * 查詢客訴記錄(For 列印) 
	 * @param inputVO CRM999InputVO
	 * @return Object (源自 dam.exeQuery)
	 * @throws Exception 
	 */
	private Object queryCustomerComplainData(CRM999InputVO inputVO) throws Exception {
		Object obj = null;
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		try{
			obj = dam.exeQuery(this.setQueryConditionInfo(inputVO, queryCondition));
		} catch (Exception e) {
			logger.debug(String.format("發生錯誤:%s",StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
		return obj;
	}
	
	private boolean opNextFlag(CRM999InputVO inputVO) throws Exception{
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		String sql = "SELECT A.EMP_ID PERSON_ID,A.EMP_NAME NAME,B.DEPT_NAME DESCR from WMSUSER.TBORG_MEMBER A LEFT JOIN WMSUSER.TBORG_DEFN B ON A.DEPT_ID = B.DEPT_ID LEFT JOIN WMSUSER.TBORG_MEMBER_ROLE C ON A.EMP_ID = C.EMP_ID LEFT JOIN WMSUSER.TBORG_ROLE_MAPPING D ON C.ROLE_ID = D.ROLE_ID WHERE D.SA_JOB_TITLE_ID IN ('C01','C02') AND B.DEPT_ID = '" + inputVO.getBranchId() + "' ORDER BY A.DEPT_ID" ;
		queryCondition.setQueryString(sql);
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		return list.size() > 0 ? true : false ;
	}
}
