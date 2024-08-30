package com.systex.jbranch.app.server.fps.crm990;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Blob;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.sql.rowset.serial.SerialException;

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
import com.systex.jbranch.app.common.fps.table.TBCRM_CUST_COMPLAINVO;
import com.systex.jbranch.app.common.fps.table.TBCRM_CUST_COMPLAIN_FLOWVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataManager.DataManager;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.serialnumber.SerialNumberUtil;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.ObjectUtil;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * creater : Carley 2018/03/29
 * modifier: Ocean  2021/12/06
 * 
 */
@Component("crm990")
@Scope("request")
public class CRM990 extends FubonWmsBizLogic {
	
	DataAccessManager dam = null;
	
	//檔案位置
	private File tempFile = new File(DataManager.getRealPath(), "temp//reports");
	
	//日期格式化物件
	private static final SimpleDateFormat dateFormatOfSlash = new SimpleDateFormat("yyyy/MM/dd");
	private static final SimpleDateFormat dateFormatOfNoSlash = new SimpleDateFormat("yyyyMMdd");

	//查詢客訴案件
	public void query(Object body, IPrimitiveMap header) throws JBranchException {
		
		CRM990InputVO inputVO = (CRM990InputVO) body;
		CRM990OutputVO outputVO = new CRM990OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		StringBuffer sb = new StringBuffer();
		sb.append("SELECT COM.COMPLAIN_LIST_ID, COM.BRANCH_NBR, O.DEPT_NAME, COM.GRADE, COM.HAPPEN_DATE, DET.ROLE_ID, ");
		sb.append("       FLOW.PRO_EMP_ID, PRO.EMP_NAME AS PRO_EMP_NAME, COM.HANDLE_STEP, COM.CUST_NAME, COM.CUST_ID, ");
		sb.append("       COM.COMPLAIN_PRODUCT, COM.EMP_ID, COM.EMP_NAME, COM.END_DATE, COM.PROCESS_DAYS, ");
		sb.append("       COM.CREATOR, CRE.EMP_NAME AS CREATOR_NAME, COM.CREATETIME, ");
		sb.append("       COM.EDITOR_CONDITION1, EDIT1.EMP_NAME AS EDIT1_NAME, ");
		sb.append("       COM.EDITOR_CONDITION2, EDIT2.EMP_NAME AS EDIT2_NAME, ");
		sb.append("       COM.EDITOR_CONDITION3, EDIT3.EMP_NAME AS EDIT3_NAME, ");
		sb.append("       COM.EDITOR_CONDITION4, EDIT4.EMP_NAME AS EDIT4_NAME, ");
		sb.append("       COM.EDITOR_CONDITION5, EDIT5.EMP_NAME AS EDIT5_NAME ");
		sb.append("FROM TBCRM_CUST_COMPLAIN COM ");
		sb.append("LEFT JOIN ( ");
		sb.append("  SELECT * ");
		sb.append("  FROM ( ");
		sb.append("    SELECT COMPLAIN_LIST_ID, EMP_ID AS PRO_EMP_ID, ROW_NUMBER() OVER (PARTITION BY COMPLAIN_LIST_ID ORDER BY CREATETIME DESC) AS SORT ");
		sb.append("    FROM TBCRM_CUST_COMPLAIN_FLOW WHERE NEXT_EMP_ID IS NULL ");
		sb.append("  ) ");
		sb.append("  WHERE SORT = 1 ");
		sb.append(") FLOW ON COM.COMPLAIN_LIST_ID = FLOW.COMPLAIN_LIST_ID ");
		sb.append("LEFT JOIN TBORG_MEMBER PRO ON FLOW.PRO_EMP_ID = PRO.EMP_ID ");
		sb.append("LEFT JOIN TBORG_MEMBER CRE ON COM.CREATOR = CRE.EMP_ID ");
		sb.append("LEFT JOIN TBORG_MEMBER EDIT1 ON COM.EDITOR_CONDITION1 = EDIT1.EMP_ID ");
		sb.append("LEFT JOIN TBORG_MEMBER EDIT2 ON COM.EDITOR_CONDITION2 = EDIT2.EMP_ID ");
		sb.append("LEFT JOIN TBORG_MEMBER EDIT3 ON COM.EDITOR_CONDITION3 = EDIT3.EMP_ID ");
		sb.append("LEFT JOIN TBORG_MEMBER EDIT4 ON COM.EDITOR_CONDITION4 = EDIT4.EMP_ID ");
		sb.append("LEFT JOIN TBORG_MEMBER EDIT5 ON COM.EDITOR_CONDITION5 = EDIT5.EMP_ID ");
		sb.append("LEFT JOIN VWORG_BRANCH_EMP_DETAIL_INFO DET ON FLOW.PRO_EMP_ID = DET.EMP_ID ");
		sb.append("LEFT JOIN TBORG_DEFN O ON COM.BRANCH_NBR = O.DEPT_ID ");
		sb.append("WHERE HANDLE_STEP <> 'D' ");

		// 依系統角色決定下拉選單可視範圍
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> headmgrMap = xmlInfo.doGetVariable("FUBONSYS.HEADMGR_ROLE", FormatHelper.FORMAT_2); //總行
		if (!headmgrMap.containsKey((String) getUserVariable(FubonSystemVariableConsts.LOGINROLE))) { //非總行人員查詢
			sb.append("AND COM.BRANCH_NBR IN (:branch_nbr_list ) ");
			queryCondition.setObject("branch_nbr_list", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
		}

		//客戶統編
		if (StringUtils.isNotBlank(inputVO.getCust_id())) {
			sb.append("AND COM.CUST_ID = :cust_id ");
			queryCondition.setObject("cust_id", inputVO.getCust_id());
		}

		//客戶姓名
		if (StringUtils.isNotBlank(inputVO.getCust_name())) {
			sb.append("AND COM.CUST_NAME like :cust_name ");
			queryCondition.setObject("cust_name", inputVO.getCust_name() + "%");
		}

		//分行別
		if (StringUtils.isNotBlank(inputVO.getBranch_nbr())) {
			sb.append("AND COM.BRANCH_NBR = :branch_nbr ");
			queryCondition.setObject("branch_nbr", inputVO.getBranch_nbr());
		}

		//建案日期(起)
		if (inputVO.getS_createtime() != null) {
			sb.append("AND TRUNC(COM.CREATETIME) >= TRUNC( :start ) ");
			queryCondition.setObject("start", inputVO.getS_createtime());
		}

		//建案日期(迄)
		if (inputVO.getE_createtime() != null) {
			sb.append("AND TRUNC(COM.CREATETIME) <= TRUNC( :end ) ");
			queryCondition.setObject("end", inputVO.getE_createtime());
		}

		//狀態
		if (StringUtils.isNotBlank(inputVO.getHandle_step())) {
			if ("C".equals(inputVO.getHandle_step())) { //客服案件
				sb.append("AND COM.BRANCH_NBR = '806' ");
			} else {
				sb.append("AND COM.HANDLE_STEP = :handle_step ");
				queryCondition.setObject("handle_step", inputVO.getHandle_step());
			}
		} else {
			if (inputVO.getIntiQuery()) { //判斷是否為初始畫面查詢
				String priID = inputVO.getPri_id();
				String loginID = (String) getCommonVariable(SystemVariableConsts.LOGINID);

				// 20211206 modify by ocean #0798: WMS-CR-20211115-01_新增客戶關懷中心角色權限及調整內控品管科角色權限 : 新增「客戶關懷中心人員」於查詢/儲存/查找主管中(原程式鎖定內控品管科經辦，新增客戶關懷中心經辦)，包含後端程式(JAVA)與前端程式(HTML、JS)。
				switch (priID) {
//				case "041":
				case "043":
				case "045":
				case "071":
					sb.append("AND COM.HANDLE_STEP = 'A' ");
					break;
//				case "042":
				case "044":
				case "046":
				case "072":
					sb.append("AND COM.HANDLE_STEP = 'A1' ");
					break;
				}

				sb.append("AND FLOW.PRO_EMP_ID = :emp_id AND COM.HANDLE_STEP <> 'E' ");
				queryCondition.setObject("emp_id", loginID); //登入者(被代理人) ID
			}
		}
		sb.append("ORDER BY COM.HAPPEN_DATE DESC ");

		queryCondition.setQueryString(sb.toString());
		outputVO.setResultList(dam.exeQuery(queryCondition));

		this.sendRtnObject(outputVO);
	}

	//取得理專資訊
	public void getEmpInfo(Object body, IPrimitiveMap header) throws JBranchException {
		
		CRM990InputVO inputVO = (CRM990InputVO) body;
		CRM990OutputVO outputVO = new CRM990OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		StringBuffer sb = new StringBuffer();
		sb.append("SELECT ORG.EMP_ID, ORG.EMP_NAME, EMP.AO_CODE, ");
		sb.append("       (CASE SERVICE_FLAG WHEN 'A' THEN '01' WHEN 'I' THEN '02' END) AS SERVICE_YN ");
		sb.append("FROM TBORG_MEMBER ORG ");
		sb.append("LEFT JOIN VWORG_BRANCH_EMP_DETAIL_INFO EMP ON ORG.EMP_ID = EMP.EMP_ID ");
		sb.append("WHERE 1 = 1 ");

		if (StringUtils.isNotBlank(inputVO.getEmp_id())) {
			sb.append("AND ORG.EMP_ID = :emp_id ");
			queryCondition.setObject("emp_id", inputVO.getEmp_id());
		}

		if (StringUtils.isNotBlank(inputVO.getAo_code())) {
			sb.append("AND EMP.AO_CODE = :ao_code ");
			queryCondition.setObject("ao_code", inputVO.getAo_code());
		}

		queryCondition.setQueryString(sb.toString());
		outputVO.setResultList(dam.exeQuery(queryCondition));

		this.sendRtnObject(outputVO);
	}

	//取得客戶基本資料
	public void getCustData(Object body, IPrimitiveMap header) throws JBranchException {
		
		CRM990InputVO inputVO = (CRM990InputVO) body;
		CRM990OutputVO outputVO = new CRM990OutputVO();

		Date today = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(today);
		cal.add(Calendar.DAY_OF_MONTH, -1); //總往來資產AUM:帶入前日AUM餘額。

		String year = String.valueOf(cal.get(Calendar.YEAR));
		String month = String.valueOf(cal.get(Calendar.MONTH));
		if (month.length() == 1) {
			month = "0" + month;
		}
		String day = String.valueOf(cal.get(Calendar.DAY_OF_MONTH));
		if (day.length() == 1) {
			day = "0" + day;
		}

		if (StringUtils.isNotBlank(inputVO.getCust_id())) {
			dam = this.getDataAccessManager();
			QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

			StringBuffer sb = new StringBuffer();
			sb.append("SELECT CUST.*, AUM.AUM_AMT ");
			sb.append("FROM (");
			sb.append("  SELECT CUST_ID, CUST_NAME, BIRTH_DATE, EDUCATION_STAT, ");
			sb.append("  BILL_TYPE FROM TBCRM_CUST_MAST WHERE CUST_ID = :cust_id ");
			sb.append(") CUST ");
			sb.append("LEFT JOIN ( ");
			sb.append("  SELECT CUST_ID, AUM_AMT ");
			sb.append("  FROM TBCRM_CUST_AUM_DAILY_HIST ");
			sb.append("  WHERE CUST_ID = :cust_id ");
			sb.append("  AND DATA_YEAR = :year ");
			sb.append("  AND DATA_MONTH = :month ");
			sb.append("  AND DATA_DAY = :day ");
			sb.append(") AUM ON CUST.CUST_ID = AUM.CUST_ID ");

			queryCondition.setObject("cust_id", inputVO.getCust_id());
			queryCondition.setObject("year", year);
			queryCondition.setObject("month", month);
			queryCondition.setObject("day", day);
			
			queryCondition.setQueryString(sb.toString());
			
			outputVO.setResultList(dam.exeQuery(queryCondition));
		}

		this.sendRtnObject(outputVO);
	}

	//取得客戶開戶日期
	public void getOpenDate(Object body, IPrimitiveMap header) throws JBranchException {
		
		CRM990InputVO inputVO = (CRM990InputVO) body;
		CRM990OutputVO outputVO = new CRM990OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();

		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();

		if (StringUtils.isNotBlank(inputVO.getBranch_nbr()) && StringUtils.isNotBlank(inputVO.getCust_id())) {
			sb.append("SELECT OPEN_DATE ");
			sb.append("FROM TBCRM_ACCT_MAST ");
			sb.append("WHERE 1 = 1 ");
			sb.append("AND NVL(ACCT_STATUS,'X') <> '2' ");
			sb.append("AND SUBSTR(ACCT_TYPE, 1, 2) <> 'LN' ");
			sb.append("AND BRA_NBR = :bra_nbr AND CUST_ID = :cust_id ");
			sb.append("ORDER BY OPEN_DATE ");

			queryCondition.setObject("bra_nbr", inputVO.getBranch_nbr());
			queryCondition.setObject("cust_id", inputVO.getCust_id());
			
			queryCondition.setQueryString(sb.toString());
			
			resultList = dam.exeQuery(queryCondition);
		}
		
		outputVO.setResultList(resultList);
		
		this.sendRtnObject(outputVO);
	}

	//查詢最大結案日期(一級限1個營業日、二級限3個營業日、三級限5個營業日<結案日-建立日，當日結案為1日>)
	public void getMaxEndDate(Object body, IPrimitiveMap header) throws JBranchException {
		
		CRM990InputVO inputVO = (CRM990InputVO) body;
		CRM990OutputVO outputVO = new CRM990OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();

		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();

		if (StringUtils.isNotBlank(inputVO.getGrade())) {
			if (inputVO.getHappen_date() != null) {
				sb.append("SELECT PABTH_UTIL.FC_getBusiDay( :happen_date , 'TWD', :days ) AS MAX_END_DATE FROM DUAL ");
				queryCondition.setObject("happen_date", inputVO.getHappen_date());
			} else {
				sb.append("SELECT PABTH_UTIL.FC_getBusiDay(SYSDATE, 'TWD', :days ) AS MAX_END_DATE FROM DUAL ");
			}

			switch (inputVO.getGrade()) {
				case "1":
					queryCondition.setObject("days", "1");
					break;
				case "2":
					queryCondition.setObject("days", "3");
					break;
				case "3":
					queryCondition.setObject("days", "5");
					break;
			}
			
			queryCondition.setQueryString(sb.toString());
			
			resultList = dam.exeQuery(queryCondition);
		}
		
		outputVO.setResultList(resultList);
		
		this.sendRtnObject(outputVO);
	}

	//檢核結案日期(一級限1個營業日、二級限3個營業日、三級限5個營業日<結案日-建立日，當日結案為1日>)
	private void checkEndDate(Date createDate, Date endDate, String grade) throws JBranchException {
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		
		if (endDate != null && StringUtils.isNotBlank(grade)) {
			sb.append("SELECT PABTH_UTIL.FC_getBusiDay( :createDate , 'TWD', :days ) AS MAX_END_DATE FROM DUAL ");
			queryCondition.setObject("createDate", createDate);

			switch (grade) {
				case "1":
					queryCondition.setObject("days", "1");
					break;
				case "2":
					queryCondition.setObject("days", "3");
					break;
				case "3":
					queryCondition.setObject("days", "5");
					break;
			}

			queryCondition.setQueryString(sb.toString());
			
			List<Map<String, Object>> maxEndDateList = dam.exeQuery(queryCondition);
			
			Date max_end_date = (Date) maxEndDateList.get(0).get("MAX_END_DATE");
			if (endDate.after(max_end_date)) {
				
				switch (grade) {
					case "1":
						throw new APException("一級客訴：結案日期限為1個營業日。");
					case "2":
						throw new APException("二級客訴：結案日期限為3個營業日。");
					case "3":
						throw new APException("三級客訴：結案日期限為5個營業日。");
				}
			}
		}
	}

	//取得目前處理進度
	private String getHandleStep(String realPri) throws JBranchException {
		
		String handleStep = "";
		switch (realPri) {
			case "006":
			case "007":
			case "009":
				handleStep = "1";
				break;
			case "010":
			case "011":
				handleStep = "2";
				break;
			case "012":
				handleStep = "3";
				break;
			case "013":
				handleStep = "4";
				break;
			case "038":
				handleStep = "C1";
				break;
			case "039":
				handleStep = "C2";
				break;
			case "040":
				handleStep = "C3";
				break;
//			case "041":
			case "071": // 20211206 modify by ocean #0798: WMS-CR-20211115-01_新增客戶關懷中心角色權限及調整內控品管科角色權限 : 新增「客戶關懷中心人員」於查詢/儲存/查找主管中(原程式鎖定內控品管科經辦，新增客戶關懷中心經辦)，包含後端程式(JAVA)與前端程式(HTML、JS)。
				handleStep = "A";
				break;
//			case "042":
			case "072": // 20211206 modify by ocean #0798: WMS-CR-20211115-01_新增客戶關懷中心角色權限及調整內控品管科角色權限 : 新增「客戶關懷中心人員」於查詢/儲存/查找主管中(原程式鎖定內控品管科經辦，新增客戶關懷中心經辦)，包含後端程式(JAVA)與前端程式(HTML、JS)。
				handleStep = "A1";
				break;
		}
		
		return handleStep;
	}

	//計算處理天數
	private int calProDays(Date createtime, Date endDate) throws JBranchException {
		
		int days = this.differentDays(createtime, endDate); //結案日-建立日
		
		if (days == 0) {
			days = 1; //當日結案為1日
		} else {
			for (int i = 1; i <= 5; i++) {
				QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				StringBuffer sb = new StringBuffer();
				sb.append("SELECT PABTH_UTIL.FC_getBusiDay( SYSDATE , 'TWD', :days ) AS MAX_END_DATE FROM DUAL ");

				queryCondition.setObject("days", i);
				queryCondition.setQueryString(sb.toString());
				List<Map<String, Object>> maxEndDateList = dam.exeQuery(queryCondition);
				Date max_end_date = (Date) maxEndDateList.get(0).get("MAX_END_DATE");

				if (this.differentDays(max_end_date, endDate) == 0) {
					days = i;
					break;
				} else if (this.differentDays(endDate, max_end_date) > 0) {
					i--;
					days = i;
					break;
				}
			}
		}
		return days;
	}

	private void checkReport(CRM990InputVO inputVO) throws JBranchException, SerialException, SQLException, IOException {
		
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		
		String comID = inputVO.getComplain_list_id();
		String repID = inputVO.getReportID();
		
		if (StringUtils.isNotBlank(inputVO.getReportName())) {
			String tempPath = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
			String joinedPath = new File(tempPath, inputVO.getReportName()).toString();
			Path path = Paths.get(joinedPath);
			byte[] data = Files.readAllBytes(path);
			saveReport(data, inputVO);
		} else if (StringUtils.isNotBlank(repID)) {
			if (StringUtils.isNotBlank(comID)) {
				if (comID.equals(repID))
					return;
				else
					saveReportByHis(inputVO, repID);
			} else {
				throw new APException("系統發生錯誤請洽系統管理員");
			}
		} else {
			sb.append("DELETE FROM TBCRM_CUST_COMPLAIN_REPORT WHERE COMPLAIN_LIST_ID = :complain_list_id ");

			queryCondition.setObject("complain_list_id", comID);
			
			queryCondition.setQueryString(sb.toString());
			
			dam.exeUpdate(queryCondition);
		}
	}

	//儲存上傳招攬/事件報告書
	private void saveReport(byte[] data, CRM990InputVO inputVO) throws JBranchException, SerialException, SQLException {
		
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		
		String id = inputVO.getComplain_list_id();
		String loginID = (String) getCommonVariable(SystemVariableConsts.LOGINID);
		
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		
		sb.append("SELECT * FROM TBCRM_CUST_COMPLAIN_REPORT WHERE COMPLAIN_LIST_ID = :complain_list_id ");

		queryCondition.setObject("complain_list_id", id);
		
		queryCondition.setQueryString(sb.toString());
		
		resultList = dam.exeQuery(queryCondition);

		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		if (resultList.size() > 0) {
			//修改
			sb = new StringBuffer();
			sb.append("UPDATE TBCRM_CUST_COMPLAIN_REPORT ");
			sb.append("SET REPORT_NAME = :report_name, ");
			sb.append("    REPORT = :report, ");
			sb.append("    VERSION = (SELECT VERSION FROM TBCRM_CUST_COMPLAIN_REPORT WHERE COMPLAIN_LIST_ID = :complain_list_id) +1, ");
			sb.append("    MODIFIER = :modifier, ");
			sb.append("    LASTUPDATE = SYSDATE ");
			sb.append("WHERE COMPLAIN_LIST_ID = :complain_list_id ");

		} else {
			//新增
			sb = new StringBuffer();
			sb.append("INSERT INTO TBCRM_CUST_COMPLAIN_REPORT (COMPLAIN_LIST_ID, REPORT_NAME, REPORT, CREATETIME, CREATOR, MODIFIER, LASTUPDATE) ");
			sb.append("VALUES (:complain_list_id, :report_name, :report, SYSDATE, :modifier, :modifier, SYSDATE) ");
		}

		queryCondition.setObject("complain_list_id", id);
		queryCondition.setObject("report_name", inputVO.getRealReportName());
		queryCondition.setObject("report", ObjectUtil.byteArrToBlob(data));
		queryCondition.setObject("modifier", loginID);
		
		queryCondition.setQueryString(sb.toString());

		dam.exeUpdate(queryCondition);
	}

	private void saveReportByHis(CRM990InputVO inputVO, String hisReportID) throws JBranchException, SerialException, SQLException {

		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		
		String id = inputVO.getComplain_list_id();
		String loginID = (String) getCommonVariable(SystemVariableConsts.LOGINID);
		
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
	
		sb.append("SELECT * FROM TBCRM_CUST_COMPLAIN_REPORT WHERE COMPLAIN_LIST_ID = :complain_list_id ");

		queryCondition.setObject("complain_list_id", id);
		
		queryCondition.setQueryString(sb.toString());
		
		resultList = dam.exeQuery(queryCondition);

		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		if (resultList.size() > 0) {
			//修改
			sb = new StringBuffer();
			sb.append("UPDATE TBCRM_CUST_COMPLAIN_REPORT NEW ");
			sb.append("SET (NEW.REPORT_NAME, NEW.REPORT) = (SELECT OLD.REPORT_NAME, OLD.REPORT FROM TBCRM_CUST_COMPLAIN_REPORT OLD WHERE OLD.COMPLAIN_LIST_ID = :his_complain_list_id ), ");
			sb.append("    VERSION = (SELECT VERSION FROM TBCRM_CUST_COMPLAIN_REPORT WHERE COMPLAIN_LIST_ID = :complain_list_id ) + 1, ");
			sb.append("    MODIFIER = :modifier, ");
			sb.append("    LASTUPDATE = SYSDATE ");
			sb.append("WHERE NEW.COMPLAIN_LIST_ID = :complain_list_id ");
		} else {
			//新增
			sb = new StringBuffer();
			sb.append("INSERT INTO TBCRM_CUST_COMPLAIN_REPORT ");
			sb.append("SELECT :complain_list_id, REPORT_NAME, REPORT, '1', SYSDATE, :modifier, :modifier, SYSDATE ");
			sb.append("FROM TBCRM_CUST_COMPLAIN_REPORT ");
			sb.append("WHERE COMPLAIN_LIST_ID = :his_complain_list_id ");
		}

		queryCondition.setObject("complain_list_id", id);
		queryCondition.setObject("his_complain_list_id", hisReportID);
		queryCondition.setObject("modifier", loginID);
		
		queryCondition.setQueryString(sb.toString());
		
		dam.exeUpdate(queryCondition);
	}

	//儲存
	public void save(Object body, IPrimitiveMap header) throws Exception {
		
		CRM990InputVO inputVO = (CRM990InputVO) body;
		CRM990OutputVO outputVO = new CRM990OutputVO();
		dam = this.getDataAccessManager();
		String loginID = (String) getCommonVariable(SystemVariableConsts.LOGINID);

		if (StringUtils.isNotBlank(inputVO.getComplain_list_id())) {
			String id = inputVO.getComplain_list_id();
			//修改
			TBCRM_CUST_COMPLAINVO vo = (TBCRM_CUST_COMPLAINVO) dam.findByPKey(TBCRM_CUST_COMPLAINVO.TABLE_UID, id);
			if (vo != null) {
				//檢核結案日期
				checkEndDate(vo.getCreatetime(), inputVO.getEnd_date(), inputVO.getGrade());

				vo.setCASE_TYPE(inputVO.getCase_type());
				vo.setEMP_ID(inputVO.getEmp_id());
				vo.setEMP_NAME(inputVO.getEmp_name());
				vo.setAO_CODE(inputVO.getAo_code());
				vo.setSERVICE_YN(inputVO.getService_yn());
				vo.setBRANCH_NBR(inputVO.getBranch_nbr());

				if (inputVO.getHappen_date() != null) {
					vo.setHAPPEN_DATE(new Timestamp(inputVO.getHappen_date().getTime()));
				}

				if (inputVO.getEnd_date() != null) {
					Date createtime = vo.getCreatetime();
					Date endDate = inputVO.getEnd_date();
					vo.setEND_DATE(new Timestamp(endDate.getTime()));
					//計算處理天數
					int days = calProDays(createtime, endDate);
					vo.setPROCESS_DAYS(new BigDecimal(days));
				}

				vo.setGRADE(inputVO.getGrade());
				vo.setCOMPLAIN_SOURCE(inputVO.getComplain_source());
				vo.setCOMPLAIN_TYPE(inputVO.getComplain_type());
				vo.setCOMPLAIN_SUMMARY(inputVO.getComplain_summary());
				vo.setCOMPLAIN_PRODUCT(inputVO.getComplain_product());
				vo.setCOMPLAIN_PRODUCT_CURRENCY(inputVO.getComplain_product_currency());

				if (!StringUtils.isBlank(inputVO.getComplain_product_amoun()))
					vo.setCOMPLAIN_PRODUCT_AMOUN(new BigDecimal(inputVO.getComplain_product_amoun()));

				if (inputVO.getBuy_date() != null)
					vo.setBUY_DATE(new Timestamp(inputVO.getBuy_date().getTime()));

				vo.setCUST_ID(inputVO.getCust_id());
				vo.setCUST_NAME(inputVO.getCust_name());

				if (inputVO.getBirthdate() != null)
					vo.setBIRTHDATE(new Timestamp(inputVO.getBirthdate().getTime()));

				vo.setOCCUP(inputVO.getOccup());
				vo.setPHONE(inputVO.getPhone());
				vo.setEDUCATION(inputVO.getEducation());

				if (inputVO.getOpen_acc_date() != null)
					vo.setOPEN_ACC_DATE(new Timestamp(inputVO.getOpen_acc_date().getTime()));

				if (!StringUtils.isBlank(inputVO.getTotal_asset()))
					vo.setTOTAL_ASSET(new BigDecimal(inputVO.getTotal_asset()));

				vo.setCHECK_SHEET(inputVO.getCheck_sheet());
				vo.setBUY_PRODUCT_TYPE(inputVO.getBuy_product_type());

				if (!StringUtils.isBlank(inputVO.getFileName())) {
					String tempPath = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
					String joinedPath = new File(tempPath, inputVO.getFileName()).toString();
					Path path = Paths.get(joinedPath);
					byte[] data = Files.readAllBytes(path);
					vo.setUPLOAD_FILE(ObjectUtil.byteArrToBlob(data));
				}

				checkReport(inputVO);

				vo.setPROBLEM_DESCRIBE(inputVO.getProblem_describe());
				vo.setCUST_DESCRIBE(inputVO.getCust_describe());

				//若為一級客訴，僅儲存至一級處理情形，其餘為null，二、三級亦同。
				//總行建案無論是幾級客訴案件，皆為填寫總行處理情形。
				int intGrade = Integer.parseInt(inputVO.getGrade());
				vo.setHANDLE_CONDITION1(inputVO.getHandle_condition1());
				vo.setHANDLE_CONDITION2(intGrade > 1 ? inputVO.getHandle_condition2() : null);
				vo.setHANDLE_CONDITION3(intGrade > 2 ? inputVO.getHandle_condition3() : null);
				vo.setHANDLE_CONDITION4(inputVO.getHandle_condition4());

				//取得目前處理進度
				String realPri = this.getRealPirID(loginID);
				String handleStep = getHandleStep(realPri);

				//案件若為總行科長待覆核(尚未結案)時，總行經辦依舊可以修改案件內容，但不更改目前處理進度。
				if (!"A1".equals(vo.getHANDLE_STEP()) && !"A".equals(handleStep)) {
					if (StringUtils.isNotBlank(handleStep)) {
						vo.setHANDLE_STEP(handleStep);
					} else {
						throw new APException("系統發生錯誤請洽系統管理員");
					}
				}

				String grade = inputVO.getGrade();
				if (realPri != null) {
					if (this.is806()) {
						//客服代分行建案
						if (!"806".equals(inputVO.getBranch_nbr())) {
							vo.setEDITOR_CONDITION1(null);
							vo.setEDITOR_CONDITION2(null);
							vo.setEDITOR_CONDITION3(null);
							vo.setEDITOR_CONDITION4(null);
							vo.setEDITOR_CONDITION5(null);
						} else {
							if (!"1".equals(grade) && !"038".equals(realPri)) {
								if (StringUtils.isBlank(vo.getEDITOR_CONDITION2()))
									vo.setEDITOR_CONDITION2(loginID);
							}
							if ("3".equals(grade) && "040".equals(realPri))
								vo.setEDITOR_CONDITION3(loginID);
						}
					} else {
						switch (realPri) {
//							case "041":
							case "071":
								vo.setEDITOR_CONDITION4(loginID);
								break;
//							case "042":
							case "072":
								vo.setEDITOR_CONDITION5(loginID);
								break;
							default :
								if (StringUtils.isBlank(vo.getEDITOR_CONDITION1())) {
									vo.setEDITOR_CONDITION1(loginID);
								}
								
								if (StringUtils.equals(realPri, "012") || StringUtils.equals(realPri, "013")) { //營運督導以上
									if (StringUtils.isBlank(vo.getEDITOR_CONDITION2())) {
										vo.setEDITOR_CONDITION2(loginID);
									}
									if (StringUtils.equals(realPri, "013")) { //處/處副主管以上
										if (StringUtils.isBlank(vo.getEDITOR_CONDITION3())) {
											vo.setEDITOR_CONDITION3(loginID);
										}
									}
								}
								break;
						}
					}
					
					vo.setTREAT_CUST_FAIRLY(inputVO.getTreat_cust_fairly());
					
					dam.update(vo);
					
					outputVO.setResultList(getCaseByID(id));
					outputVO.setComplain_list_id(id);
					
					this.sendRtnObject(outputVO);
				} else {
					throw new APException("系統發生錯誤請洽系統管理員");
				}
			} else {
				throw new APException("系統發生錯誤請洽系統管理員");
			}
		} else {
			//新增
			Date createtime = new Date();
			//檢核結案日期
			checkEndDate(createtime, inputVO.getEnd_date(), inputVO.getGrade());

			TBCRM_CUST_COMPLAINVO vo = new TBCRM_CUST_COMPLAINVO();
			String id = this.getComplainSeq();
			inputVO.setComplain_list_id(id); // for上傳招攬/事件報告書

			vo.setCOMPLAIN_LIST_ID(id);
			vo.setCASE_TYPE(inputVO.getCase_type());
			vo.setEMP_ID(inputVO.getEmp_id());
			vo.setEMP_NAME(inputVO.getEmp_name());
			vo.setAO_CODE(inputVO.getAo_code());
			vo.setSERVICE_YN(inputVO.getService_yn());
			vo.setBRANCH_NBR(inputVO.getBranch_nbr());

			if (inputVO.getHappen_date() != null)
				vo.setHAPPEN_DATE(new Timestamp(inputVO.getHappen_date().getTime()));

			if (inputVO.getEnd_date() != null) {
				Date endDate = inputVO.getEnd_date();
				vo.setEND_DATE(new Timestamp(endDate.getTime()));
				//計算處理天數
				int days = calProDays(createtime, endDate);
				vo.setPROCESS_DAYS(new BigDecimal(days));
			}

			vo.setGRADE(inputVO.getGrade());
			vo.setCOMPLAIN_SOURCE(inputVO.getComplain_source());
			vo.setCOMPLAIN_TYPE(inputVO.getComplain_type());
			vo.setCOMPLAIN_SUMMARY(inputVO.getComplain_summary());
			vo.setCOMPLAIN_PRODUCT(inputVO.getComplain_product());
			vo.setCOMPLAIN_PRODUCT_CURRENCY(inputVO.getComplain_product_currency());

			if (!StringUtils.isBlank(inputVO.getComplain_product_amoun()))
				vo.setCOMPLAIN_PRODUCT_AMOUN(new BigDecimal(inputVO.getComplain_product_amoun()));

			if (inputVO.getBuy_date() != null)
				vo.setBUY_DATE(new Timestamp(inputVO.getBuy_date().getTime()));

			vo.setCUST_ID(inputVO.getCust_id());
			vo.setCUST_NAME(inputVO.getCust_name().trim().replace("　", ""));

			if (inputVO.getBirthdate() != null)
				vo.setBIRTHDATE(new Timestamp(inputVO.getBirthdate().getTime()));

			vo.setOCCUP(inputVO.getOccup());
			vo.setPHONE(inputVO.getPhone());
			vo.setEDUCATION(inputVO.getEducation());

			if (inputVO.getOpen_acc_date() != null)
				vo.setOPEN_ACC_DATE(new Timestamp(inputVO.getOpen_acc_date().getTime()));

			if (!StringUtils.isBlank(inputVO.getTotal_asset()))
				vo.setTOTAL_ASSET(new BigDecimal(inputVO.getTotal_asset()));

			vo.setCHECK_SHEET(inputVO.getCheck_sheet());
			vo.setBUY_PRODUCT_TYPE(inputVO.getBuy_product_type());

			if (!StringUtils.isBlank(inputVO.getFileName())) {
				String tempPath = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
				String joinedPath = new File(tempPath, inputVO.getFileName()).toString();
				Path path = Paths.get(joinedPath);
				byte[] data = Files.readAllBytes(path);
				vo.setUPLOAD_FILE(ObjectUtil.byteArrToBlob(data));
			}

			checkReport(inputVO);

			vo.setPROBLEM_DESCRIBE(inputVO.getProblem_describe());
			vo.setCUST_DESCRIBE(inputVO.getCust_describe());

			//取得目前處理進度
			String realPri = this.getRealPirID(loginID);
			String handleStep = getHandleStep(realPri);
			if (StringUtils.isNotBlank(handleStep)) {
				vo.setHANDLE_STEP(handleStep);
			} else {
				throw new APException("系統發生錯誤請洽系統管理員");
			}

			//若為一級客訴，僅儲存至一級處理情形，其餘為null，二、三級亦同。
			//總行建案無論是幾級客訴案件，皆為填寫總行處理情形。
			int intGrade = Integer.parseInt(inputVO.getGrade());
			vo.setHANDLE_CONDITION1(inputVO.getHandle_condition1());
			vo.setHANDLE_CONDITION2(intGrade > 1 ? inputVO.getHandle_condition2() : null);
			vo.setHANDLE_CONDITION3(intGrade > 2 ? inputVO.getHandle_condition3() : null);
			vo.setHANDLE_CONDITION4(inputVO.getHandle_condition4());

			String grade = inputVO.getGrade();
			if (realPri != null) {
				if (this.is806()) {
					if ("806".equals(inputVO.getBranch_nbr())) {
						vo.setEDITOR_CONDITION1(loginID);
						if (!"1".equals(grade) && !"038".equals(realPri))
							vo.setEDITOR_CONDITION2(loginID);
						if ("3".equals(grade) && "040".equals(realPri))
							vo.setEDITOR_CONDITION3(loginID);
					}
				} else {
					switch (realPri) {
//						case "041":
						case "071":
							vo.setEDITOR_CONDITION4(loginID);
							break;
//						case "042":
						case "072":
							vo.setEDITOR_CONDITION5(loginID);
							break;
						default :
							if (StringUtils.isBlank(vo.getEDITOR_CONDITION1())) {
								vo.setEDITOR_CONDITION1(loginID);
							}
							
							if (StringUtils.equals(realPri, "012") || StringUtils.equals(realPri, "013")) { //營運督導以上
								if (StringUtils.isBlank(vo.getEDITOR_CONDITION2())) {
									vo.setEDITOR_CONDITION2(loginID);
								}
								if (StringUtils.equals(realPri, "013")) { //處/處副主管以上
									if (StringUtils.isBlank(vo.getEDITOR_CONDITION3())) {
										vo.setEDITOR_CONDITION3(loginID);
									}
								}
							}
							break;
					}
				}

				vo.setTREAT_CUST_FAIRLY(inputVO.getTreat_cust_fairly());
				dam.create(vo);

				//新增客訴流程明細檔(TBCRM_CUST_COMPLAIN_FLOW)
				TBCRM_CUST_COMPLAIN_FLOWVO flowVO = new TBCRM_CUST_COMPLAIN_FLOWVO();
				BigDecimal flowSeq = this.getFlowSeq();

				flowVO.setSEQ(flowSeq);
				flowVO.setCOMPLAIN_LIST_ID(id);
				flowVO.setEMP_ID(loginID);
				flowVO.setSTATUS("W"); //中途：W、結案：E、退件：B、抽回：R
				dam.create(flowVO);

				outputVO.setComplain_list_id(id);
				outputVO.setResultList(getCaseByID(id));
				this.sendRtnObject(outputVO);

			} else {
				throw new APException("系統發生錯誤請洽系統管理員");
			}
		}
	}

	//依據客訴編號找出客訴案件
	private List<Map<String, Object>> getCaseByID(String id) throws JBranchException {
		
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();

		sb.append("SELECT MAI.*, REP.REPORT_NAME ");
		sb.append("FROM TBCRM_CUST_COMPLAIN MAI ");
		sb.append("LEFT JOIN TBCRM_CUST_COMPLAIN_REPORT REP ON MAI.COMPLAIN_LIST_ID = REP.COMPLAIN_LIST_ID ");
		sb.append("WHERE MAI.COMPLAIN_LIST_ID = :complain_list_id ");

		queryCondition.setObject("complain_list_id", id);
		
		queryCondition.setQueryString(sb.toString());
		
		return dam.exeQuery(queryCondition);
	}

	//查詢是否還有下一處理人員
	public void checkNext(Object body, IPrimitiveMap header) throws JBranchException {
		
		CRM990InputVO inputVO = (CRM990InputVO) body;
		CRM990OutputVO outputVO = new CRM990OutputVO();
		dam = this.getDataAccessManager();
		
		boolean hasNextStep = false;

		if (StringUtils.isNotBlank(inputVO.getComplain_list_id())) {
			String pk = inputVO.getComplain_list_id();
			TBCRM_CUST_COMPLAINVO vo = (TBCRM_CUST_COMPLAINVO) dam.findByPKey(TBCRM_CUST_COMPLAINVO.TABLE_UID, pk);

			if (vo != null) {
				String handle_step = vo.getHANDLE_STEP();

				switch (vo.getGRADE()) {
				case "1":
				case "2":
					if ("806".equals(vo.getBRANCH_NBR())) {
						switch (handle_step) {
							case "C1":
							case "C2":
								hasNextStep = true;
								break;
						}
					} else {
						switch (handle_step) {
							case "1":
							case "2":
							case "3":
							case "C1":
							case "C2":
							case "C3":
								hasNextStep = true;
								break;
						}
					}
					break;

				case "3":
					if ("806".equals(vo.getBRANCH_NBR())) {
						switch (handle_step) {
							case "C1":
							case "C2":
								hasNextStep = true;
								break;
						}
					} else {
						switch (handle_step) {
							case "1":
							case "2":
							case "3":
							case "4":
							case "A":
							case "C1":
							case "C2":
							case "C3":
								hasNextStep = true;
								break;
						}
					}
					break;
				}
			}
		}
		
		outputVO.setHasNextStep(hasNextStep);
		
		this.sendRtnObject(outputVO);
	}

	//查詢客訴案件詳細資訊
	public void getDetail(Object body, IPrimitiveMap header) throws JBranchException {
		
		CRM990InputVO inputVO = (CRM990InputVO) body;
		CRM990OutputVO outputVO = new CRM990OutputVO();
		
		String id = inputVO.getComplain_list_id();
		if (StringUtils.isNotBlank(id)) {
			outputVO.setResultList(getCaseByID(id));
			
			this.sendRtnObject(outputVO);
		}
	}

	//結案
	public void endTheCase(Object body, IPrimitiveMap header) throws JBranchException {
		
		CRM990InputVO inputVO = (CRM990InputVO) body;
		
		this.endTheCase(inputVO.getComplain_list_id());
		this.sendRtnObject(null);
	}

	//實作結案
	private void endTheCase(String complain_list_id) throws JBranchException {
		
		dam = this.getDataAccessManager();

		if (StringUtils.isNotBlank(complain_list_id)) {
			//更新客訴管理主檔
			TBCRM_CUST_COMPLAINVO listVO = (TBCRM_CUST_COMPLAINVO) dam.findByPKey(TBCRM_CUST_COMPLAINVO.TABLE_UID, complain_list_id);
			if (listVO != null) {
				listVO.setHANDLE_STEP("E"); //E.結案(已放行)
				listVO.setREAL_END_DATE(new Timestamp(new Date().getTime()));
				dam.update(listVO);
			} else {
				throw new APException("系統發生錯誤請洽系統管理員");
			}

			//更新客訴流程明細檔
			QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sb = new StringBuffer();

			sb.append("SELECT * ");
			sb.append("FROM ( ");
			sb.append("  SELECT SEQ, COMPLAIN_LIST_ID, EMP_ID, NEXT_EMP_ID, ROW_NUMBER() OVER (PARTITION BY COMPLAIN_LIST_ID ORDER BY CREATETIME DESC) AS SORT ");
			sb.append("  FROM TBCRM_CUST_COMPLAIN_FLOW ");
			sb.append(") ");
			sb.append("WHERE SORT = 1 ");
			sb.append("AND COMPLAIN_LIST_ID = :complain_list_id ");
			sb.append("AND NEXT_EMP_ID IS NULL ");

			queryCondition.setObject("complain_list_id", complain_list_id);
			queryCondition.setQueryString(sb.toString());
			List<Map<String, Object>> list = dam.exeQuery(queryCondition);

			if (list.size() > 0) {
				BigDecimal seq = new BigDecimal(list.get(0).get("SEQ").toString());
				TBCRM_CUST_COMPLAIN_FLOWVO flowVO = (TBCRM_CUST_COMPLAIN_FLOWVO) dam.findByPKey(TBCRM_CUST_COMPLAIN_FLOWVO.TABLE_UID, seq);
				if (flowVO != null) {
					String loginID = (String) getCommonVariable(SystemVariableConsts.LOGINID);
					flowVO.setEMP_ID(loginID);
					flowVO.setSTATUS("E");
					flowVO.setSUBMIT_DATE(new Timestamp(new Date().getTime()));
					dam.update(flowVO);
				}
			} else {
				throw new APException("系統發生錯誤請洽系統管理員");
			}
		} else {
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}

	//取得目前處理人員
	public void getFlow(Object body, IPrimitiveMap header) throws JBranchException {
		CRM990InputVO inputVO = (CRM990InputVO) body;
		CRM990OutputVO outputVO = new CRM990OutputVO();
		List<Map<String, Object>> resultList = new ArrayList<>();

		if (StringUtils.isNotBlank(inputVO.getComplain_list_id())) {
			dam = this.getDataAccessManager();
			QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

			StringBuffer sb = new StringBuffer();
			sb.append("SELECT FLOW.*, ORG.JOB_TITLE_NAME, ORG.EMP_NAME, ORG.DEPT_ID, DEFN.DEPT_NAME ");
			sb.append("FROM ( ");
			sb.append("  SELECT COMPLAIN_LIST_ID, EMP_ID, NEXT_EMP_ID, ROW_NUMBER() OVER (PARTITION BY COMPLAIN_LIST_ID ORDER BY CREATETIME DESC) AS SORT ");
			sb.append("  FROM TBCRM_CUST_COMPLAIN_FLOW WHERE COMPLAIN_LIST_ID = :complain_list_id ");
			sb.append(") FLOW ");
			sb.append("LEFT JOIN TBORG_MEMBER ORG ON FLOW.EMP_ID = ORG.EMP_ID ");
			sb.append("LEFT JOIN TBORG_DEFN DEFN ON ORG.DEPT_ID = DEFN.DEPT_ID ");
			sb.append("WHERE FLOW.SORT = 1 AND FLOW.NEXT_EMP_ID IS NULL ");

			queryCondition.setObject("complain_list_id", inputVO.getComplain_list_id());
			queryCondition.setQueryString(sb.toString());
			List<Map<String, Object>> list = dam.exeQuery(queryCondition);

			if (list.size() > 0) {
				Map<String, Object> tempMap = list.get(0);
				Map<String, Object> map = new HashMap<String, Object>();

				map.put("DEPT_ID", tempMap.get("DEPT_ID"));
				map.put("DEPT_NAME", tempMap.get("DEPT_NAME"));
				map.put("JOB_TITLE_NAME", tempMap.get("JOB_TITLE_NAME"));
				map.put("EMP_ID", tempMap.get("EMP_ID"));
				map.put("EMP_NAME", tempMap.get("EMP_NAME"));

				resultList.add(map);
			}

			outputVO.setResultList(resultList);
			this.sendRtnObject(outputVO);
		}
	}

	//取第一級主管(非建案人員，有可能是客服部代為建案)
	public void getFirstFlow(Object body, IPrimitiveMap header) throws JBranchException {
		CRM990InputVO inputVO = (CRM990InputVO) body;
		CRM990OutputVO outputVO = new CRM990OutputVO();
		List<Map<String, Object>> resultList = new ArrayList<>();

		if (StringUtils.isNotBlank(inputVO.getComplain_list_id())) {
			dam = this.getDataAccessManager();
			QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

			StringBuffer sb = new StringBuffer();
			sb.append("SELECT FIR.FIRST_EMP_ID, ORG.EMP_NAME AS FIRST_EMP_NAME, ORG.DEPT_ID, ");
			sb.append("       DEFN.DEPT_NAME AS FIRST_DEPT_NAME, ORG.JOB_TITLE_NAME AS FIRST_JOB_TITLE_NAME ");
			sb.append("FROM ( ");
			sb.append("  SELECT EDITOR_CONDITION1 AS FIRST_EMP_ID ");
			sb.append("  FROM TBCRM_CUST_COMPLAIN ");
			sb.append("  WHERE COMPLAIN_LIST_ID = :complain_list_id ");
			sb.append(") FIR ");
			sb.append("LEFT JOIN TBORG_MEMBER ORG ON FIR.FIRST_EMP_ID = ORG.EMP_ID ");
			sb.append("LEFT JOIN TBORG_DEFN DEFN ON ORG.DEPT_ID = DEFN.DEPT_ID ");

			queryCondition.setObject("complain_list_id", inputVO.getComplain_list_id());
			queryCondition.setQueryString(sb.toString());
			List<Map<String, Object>> list = dam.exeQuery(queryCondition);

			if (list.size() > 0) {
				Map<String, Object> tempMap = list.get(0);
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("FIRST_DEPT_NAME", tempMap.get("FIRST_DEPT_NAME"));
				map.put("FIRST_JOB_TITLE_NAME", tempMap.get("FIRST_JOB_TITLE_NAME"));
				map.put("FIRST_EMP_ID", tempMap.get("FIRST_EMP_ID"));
				map.put("FIRST_EMP_NAME", tempMap.get("FIRST_EMP_NAME"));
				resultList.add(map);
			}
		}
		outputVO.setResultList(resultList);
		this.sendRtnObject(outputVO);
	}

	// 取得下一流程人員資訊
	public void getRevList(Object body, IPrimitiveMap header) throws JBranchException {
		
		CRM990InputVO inputVO = (CRM990InputVO) body;
		CRM990OutputVO outputVO = new CRM990OutputVO();
		dam = this.getDataAccessManager();

		if (StringUtils.isNotBlank(inputVO.getComplain_list_id())) {
			TBCRM_CUST_COMPLAINVO vo = (TBCRM_CUST_COMPLAINVO) dam.findByPKey(TBCRM_CUST_COMPLAINVO.TABLE_UID, inputVO.getComplain_list_id());
			if (vo != null) {
				String loginID = (String) getCommonVariable(SystemVariableConsts.LOGINID);
				String priID = this.getRealPirID(loginID);
				String branch_nbr = vo.getBRANCH_NBR();

				dam = this.getDataAccessManager();
				QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

				StringBuffer sb = new StringBuffer();
				sb.append("SELECT EMP.EMP_ID, EMP.EMP_NAME, EMP.ROLE_ID, ROLE.ROLE_NAME, EMP.PRIVILEGEID ");
				sb.append("FROM VWORG_EMP_INFO EMP ");
				sb.append("LEFT JOIN TBORG_ROLE ROLE ON EMP.ROLE_ID = ROLE.ROLE_ID ");
				sb.append("WHERE 1 = 1 ");

				//客服部建檔
				if (this.is806()) {
					if ("806".equals(branch_nbr)) { //客服案件
						if ("038".equals(priID)) {
							sb.append("AND EMP.PRIVILEGEID = '039' ");
						} else if ("039".equals(priID)) {
							sb.append("AND EMP.PRIVILEGEID = '040' ");
						}
					} else { //建立人客服部/營管部幫分行建檔==>作業案件給作業主管(一級)、其他給分行個金主管(一級)==>營運督導(二級)==>處副主管(三級)==>總行經辦、總行科長
						if ("OP".equals(vo.getCASE_TYPE())) {
							sb.append("AND EMP.PRIVILEGEID = '006' ");
							sb.append("AND EMP.BRANCH_NBR = :branch_nbr ");
							queryCondition.setObject("branch_nbr", branch_nbr);

						} else {
							Map<String, String> map = this.getDefnInfo(branch_nbr);
							if (map.size() > 0) {
								String branch_area_id = map.get("BRANCH_AREA_ID");
								String region_center_id = map.get("REGION_CENTER_ID");

								sb.append("AND (EMP.PRIVILEGEID IN ('010', '011') AND EMP.BRANCH_NBR = :branch_nbr ) ");
								sb.append("OR (EMP.PRIVILEGEID = '012' AND EMP.BRANCH_AREA_ID = :branch_area_id ) ");
								sb.append("OR (EMP.PRIVILEGEID = '013' AND EMP.REGION_CENTER_ID = :region_center_id ) ");

								queryCondition.setObject("branch_nbr", branch_nbr);
								queryCondition.setObject("branch_area_id", branch_area_id);
								queryCondition.setObject("region_center_id", region_center_id);
							}
						}
					}
				} else {
					int intPriID = Integer.parseInt(priID);
					Map<String, String> map = this.getDefnInfo(branch_nbr);
					if (map.size() > 0 && intPriID < 13) {
						String branch_area_id = map.get("BRANCH_AREA_ID");
						String region_center_id = map.get("REGION_CENTER_ID");

						/**************** 作業客訴 ****************/
						if ("OP".equals(vo.getCASE_TYPE())) {
							if ("006".equals(priID) || "007".equals(priID) || "009".equals(priID)) { //作業主管 & 業務主管
								sb.append("AND (EMP.PRIVILEGEID IN ('010', '011') AND EMP.BRANCH_NBR = :branch_nbr ) ");
								sb.append("OR (EMP.PRIVILEGEID = '012' AND EMP.BRANCH_AREA_ID = :branch_area_id ) ");
								sb.append("OR (EMP.PRIVILEGEID = '013' AND EMP.REGION_CENTER_ID = :region_center_id ) ");

								queryCondition.setObject("branch_nbr", branch_nbr);
								queryCondition.setObject("branch_area_id", branch_area_id);
								queryCondition.setObject("region_center_id", region_center_id);

							}
							/**************** 理財、消金、其他客訴 ****************/
						} else {
							if ("006".equals(priID) || "007".equals(priID) || "009".equals(priID)) { //作業主管 & 業務主管
								sb.append("AND (EMP.PRIVILEGEID = '012' AND EMP.BRANCH_AREA_ID = :branch_area_id ) ");
								sb.append("OR (EMP.PRIVILEGEID = '013' AND EMP.REGION_CENTER_ID = :region_center_id ) ");

								queryCondition.setObject("branch_area_id", branch_area_id);
								queryCondition.setObject("region_center_id", region_center_id);
							}
						}

						if ("010".equals(priID) || "011".equals(priID)) { //分行(助理)個金主管
							sb.append("AND (EMP.PRIVILEGEID = '012' AND EMP.BRANCH_AREA_ID = :branch_area_id ) ");
							sb.append("OR (EMP.PRIVILEGEID = '013' AND EMP.REGION_CENTER_ID = :region_center_id ) ");

							queryCondition.setObject("branch_area_id", branch_area_id);
							queryCondition.setObject("region_center_id", region_center_id);

						} else if ("012".equals(priID)) { //營運督導
							sb.append("AND (EMP.PRIVILEGEID = '013' AND EMP.REGION_CENTER_ID = :region_center_id ) ");
							queryCondition.setObject("region_center_id", region_center_id);
						}
					} else {
						switch (priID) {
							case "013": //業務處處長
								sb.append("AND EMP.PRIVILEGEID IN ('071') AND EMP.REGION_CENTER_ID IS NULL "); //'041', 
								break;
//							case "041": //總行經辦
							case "071":
							case "043":
							case "045":
								sb.append("AND EMP.PRIVILEGEID IN ('072') AND EMP.REGION_CENTER_ID IS NULL "); //'042', 
								break;
						}
					}
				}
				sb.append("ORDER BY EMP.PRIVILEGEID ");

				queryCondition.setQueryString(sb.toString());
				outputVO.setResultList(dam.exeQuery(queryCondition));

				this.sendRtnObject(outputVO);
			} else {
				throw new APException("系統發生錯誤請洽系統管理員");
			}
		} else {
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}

	public void getNextEmp(Object body, IPrimitiveMap header) throws JBranchException {
		CRM990InputVO inputVO = (CRM990InputVO) body;
		CRM990OutputVO outputVO = new CRM990OutputVO();
		dam = this.getDataAccessManager();

		if (StringUtils.isNotBlank(inputVO.getComplain_list_id())) {
			List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
			TBCRM_CUST_COMPLAINVO vo = (TBCRM_CUST_COMPLAINVO) dam.findByPKey(TBCRM_CUST_COMPLAINVO.TABLE_UID, inputVO.getComplain_list_id());
			if (vo != null) {
				String loginID = (String) getCommonVariable(SystemVariableConsts.LOGINID);
				String priID = this.getRealPirID(loginID);
				String LoginBrh = (String) SysInfo.getInfoValue(FubonSystemVariableConsts.LOGINBRH);
				String branch_nbr = vo.getBRANCH_NBR();

				dam = this.getDataAccessManager();
				QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

				StringBuffer sb = new StringBuffer();
				sb.append("SELECT EMP_ID, EMP_NAME, JOB_TITLE_NAME, PRI.PRIVILEGEID ");
				sb.append("FROM VWORG_BRANCH_EMP_DETAIL_INFO EMP ");
				sb.append("LEFT JOIN TBSYSSECUROLPRIASS PRI ON EMP.ROLE_ID = PRI.ROLEID ");
				sb.append("WHERE 1 = 1 ");

				//客服部建檔
				if (this.is806()) {
					if ("806".equals(branch_nbr)) { //客服案件
						if ("038".equals(priID)) {
							sb.append("AND PRI.PRIVILEGEID = '039' ");
						} else if ("039".equals(priID)) {
							sb.append("AND PRI.PRIVILEGEID = '040' ");
						}
					} else { //建立人客服部/營管部幫分行建檔==>作業案件給作業主管(一級)、其他給分行個金主管(一級)==>營運督導(二級)==>處副主管(三級)==>總行經辦、總行科長、總行部主管
						if ("OP".equals(vo.getCASE_TYPE())) {
							sb.append("AND PRI.PRIVILEGEID = '006' ");
							sb.append("AND EMP.BRANCH_NBR = :branch_nbr ");
							queryCondition.setObject("branch_nbr", branch_nbr);

						} else {
							Map<String, String> map = this.getDefnInfo(branch_nbr);
							if (map.size() > 0) {
								sb.append("AND (PRI.PRIVILEGEID IN ('010', '011') AND EMP.BRANCH_NBR = :branch_nbr ) ");
								queryCondition.setObject("branch_nbr", branch_nbr);
							}
						}
					}
				} else {
					Map<String, String> map = this.getDefnInfo(branch_nbr);
					int intPriID = Integer.parseInt(priID);
					if (map.size() > 0 && intPriID < 13) {
						String branch_area_id = map.get("BRANCH_AREA_ID");
						String region_center_id = map.get("REGION_CENTER_ID");

						/**************** 作業客訴 ****************/
						if ("OP".equals(vo.getCASE_TYPE())) {
							if ("006".equals(priID) || "007".equals(priID) || "009".equals(priID)) { //作業主管 & 業務主管
								sb.append("AND (PRI.PRIVILEGEID IN ('010', '011') AND EMP.BRANCH_NBR = :branch_nbr ) ");
								queryCondition.setObject("branch_nbr", branch_nbr);
							}
							/**************** 理財、消金、其他客訴 ****************/
						} else {
							if ("006".equals(priID) || "007".equals(priID) || "009".equals(priID)) { //作業主管 & 業務主管
								sb.append("AND (PRI.PRIVILEGEID = '012' AND EMP.BRANCH_AREA_ID = :branch_area_id ) ");
								queryCondition.setObject("branch_area_id", branch_area_id);
							}
						}

						if ("010".equals(priID) || "011".equals(priID)) { //分行(助理)個金主管
							sb.append("AND (PRI.PRIVILEGEID = '012' AND EMP.BRANCH_AREA_ID = :branch_area_id ) ");
							queryCondition.setObject("branch_area_id", branch_area_id);

						} else if ("012".equals(priID)) { //營運督導
							sb.append("AND (PRI.PRIVILEGEID = '013' AND EMP.REGION_CENTER_ID = :region_center_id ) ");
							queryCondition.setObject("region_center_id", region_center_id);
						}
					} else {
						// 20211206 modify by ocean #0798: WMS-CR-20211115-01_新增客戶關懷中心角色權限及調整內控品管科角色權限 : 新增「客戶關懷中心人員」於查詢/儲存/查找主管中(原程式鎖定內控品管科經辦，新增客戶關懷中心經辦)，包含後端程式(JAVA)與前端程式(HTML、JS)。
						switch (priID) {
						case "013": //業務處處
							sb.append("AND PRI.PRIVILEGEID IN ('071') AND EMP.REGION_CENTER_ID IS NULL "); //'041'
							break;
//						case "041": //總行經辦
//							sb.append("AND PRI.PRIVILEGEID = '042' AND EMP.REGION_CENTER_ID IS NULL ");
//							break;
						case "043": //總行經辦
							sb.append("AND PRI.PRIVILEGEID = '044' ");
							break;
						case "045": //總行經辦
							sb.append("AND PRI.PRIVILEGEID = '046' ");
							break;
						case "071": //總行經辦
							sb.append("AND PRI.PRIVILEGEID = '072' AND EMP.REGION_CENTER_ID IS NULL ");
							break;

						}
					}
				}
				queryCondition.setQueryString(sb.toString());
				resultList = dam.exeQuery(queryCondition);
				outputVO.setResultList(resultList);

				this.sendRtnObject(outputVO);
			} else {
				throw new APException("系統發生錯誤請洽系統管理員");
			}
		} else {
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}

	public void saveSubmit(Object body, IPrimitiveMap header) throws JBranchException {
		CRM990InputVO inputVO = (CRM990InputVO) body;
		dam = this.getDataAccessManager();

		String id = inputVO.getComplain_list_id();
		if (StringUtils.isNotBlank(id)) {
			TBCRM_CUST_COMPLAINVO vo = (TBCRM_CUST_COMPLAINVO) dam.findByPKey(TBCRM_CUST_COMPLAINVO.TABLE_UID, id);

			if (vo != null) {
				String grade = inputVO.getGrade();
				String case_type = inputVO.getCase_type();
				String loginID = (String) getCommonVariable(SystemVariableConsts.LOGINID);

				//若案件退回，重跑過程中，選擇下一覆核人員與原先不同，需清除原先處理人員。
				if (StringUtils.isNotBlank(grade)) {
					String priID = this.getRealPirID(loginID);
					if (!"OP".equals(case_type)) {
						if ("006".equals(priID) || "007".equals(priID) || "009".equals(priID)) {
							if (StringUtils.isNotBlank(vo.getEDITOR_CONDITION2())) {
								if (!inputVO.getNext_emp_id().equals(vo.getEDITOR_CONDITION2())) {
									vo.setEDITOR_CONDITION2(null);
								}
							}
						}
					}
					if ("010".equals(priID) || "011".equals(priID) || "038".equals(priID)) {
						if (StringUtils.isNotBlank(vo.getEDITOR_CONDITION2())) {
							if (!inputVO.getNext_emp_id().equals(vo.getEDITOR_CONDITION2())) {
								vo.setEDITOR_CONDITION2(null);
							}
						}
					}
					if ("012".equals(priID) || "039".equals(priID)) {
						if (StringUtils.isNotBlank(vo.getEDITOR_CONDITION3())) {
							if (!inputVO.getNext_emp_id().equals(vo.getEDITOR_CONDITION3())) {
								vo.setEDITOR_CONDITION3(null);
							}
						}
					}
					if ("013".equals(priID)) {
						if (StringUtils.isNotBlank(vo.getEDITOR_CONDITION4())) {
							if (!inputVO.getNext_emp_id().equals(vo.getEDITOR_CONDITION4())) {
								vo.setEDITOR_CONDITION4(null);
							}
						}
					}
					if ("071".equals(priID)) { //"041".equals(priID) || 
						if (StringUtils.isNotBlank(vo.getEDITOR_CONDITION5())) {
							if (!inputVO.getNext_emp_id().equals(vo.getEDITOR_CONDITION5())) {
								vo.setEDITOR_CONDITION5(null);
							}
						}
					}
				}

				String next_emp_id = inputVO.getNext_emp_id();
				if (StringUtils.isNotBlank(next_emp_id)) {
					String priID = this.getRealPirID(next_emp_id);
					//取得目前處理進度
					String handleStep = getHandleStep(priID);
					vo.setHANDLE_STEP(handleStep);

					dam.update(vo);

					QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

					StringBuffer sb = new StringBuffer();
					sb.append("SELECT * ");
					sb.append("FROM TBCRM_CUST_COMPLAIN_FLOW ");
					sb.append("WHERE STATUS = 'W' ");
					sb.append("AND COMPLAIN_LIST_ID = :complain_list_id ");

					queryCondition.setObject("complain_list_id", id);
					queryCondition.setQueryString(sb.toString());
					List<Map<String, Object>> list = dam.exeQuery(queryCondition);

					if (list.size() > 0) {
						BigDecimal seq = new BigDecimal(list.get(0).get("SEQ").toString());
						TBCRM_CUST_COMPLAIN_FLOWVO flowVO = (TBCRM_CUST_COMPLAIN_FLOWVO) dam.findByPKey(TBCRM_CUST_COMPLAIN_FLOWVO.TABLE_UID, seq);
						if (flowVO != null) {
							flowVO.setEMP_ID(loginID);
							flowVO.setNEXT_EMP_ID(next_emp_id);
							flowVO.setSUBMIT_DATE(new Timestamp(new Date().getTime()));

							//　W：中途、E：結案、BO：退回上一級、BF：退回第一級、R：抽回
							String rejectType = inputVO.getReject_type();
							if (StringUtils.isBlank(rejectType)) {
								flowVO.setSTATUS("E"); //E：結案 (案件已處理，處理狀態顯示"結案")			
							} else {
								if ("one".equals(rejectType)) {
									flowVO.setSTATUS("BO"); //BO：退回上一級

								} else if ("first".equals(rejectType)) {
									flowVO.setSTATUS("BF"); //BF：退回第一級
								}

								if (StringUtils.isNotBlank(inputVO.getReason())) { //退件原因
									flowVO.setREASON(inputVO.getReason());
								}
							}
							dam.update(flowVO);
						}
					}

					TBCRM_CUST_COMPLAIN_FLOWVO flowVO = new TBCRM_CUST_COMPLAIN_FLOWVO();
					flowVO.setSEQ(this.getFlowSeq());
					flowVO.setCOMPLAIN_LIST_ID(id);
					flowVO.setEMP_ID(next_emp_id);
					flowVO.setSTATUS("W"); //W：中途

					dam.create(flowVO);
					this.sendRtnObject(null);
				}
			} else {
				throw new APException("系統發生錯誤請洽系統管理員");
			}
		} else {
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}

	public void getPriID(Object body, IPrimitiveMap header) throws JBranchException {
		CRM990OutputVO outputVO = new CRM990OutputVO();
		String loginID = (String) getCommonVariable(SystemVariableConsts.LOGINID);
		outputVO.setPrivilege_id(this.getRealPirID(loginID));
		this.sendRtnObject(outputVO);
	}

	//查詢是否有前一級
	public void checkFoword(Object body, IPrimitiveMap header) throws JBranchException {
		CRM990InputVO inputVO = (CRM990InputVO) body;
		CRM990OutputVO outputVO = new CRM990OutputVO();
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();

		boolean hasFowordStep = false;
		String pri_emp_id = null;
		String grade = inputVO.getGrade();
		String id = inputVO.getComplain_list_id();
		if (StringUtils.isNotBlank(id)) {
			String loginID = (String) getCommonVariable(SystemVariableConsts.LOGINID);
			String priID = this.getRealPirID(loginID);

			TBCRM_CUST_COMPLAINVO vo = (TBCRM_CUST_COMPLAINVO) dam.findByPKey(TBCRM_CUST_COMPLAINVO.TABLE_UID, id);
			String editor1 = vo.getEDITOR_CONDITION1();
			String editor2 = vo.getEDITOR_CONDITION2();
			String editor3 = vo.getEDITOR_CONDITION3();
			String editor4 = vo.getEDITOR_CONDITION4();

			if (!"006".equals(priID) && !"007".equals(priID) && !"009".equals(priID) && !"038".equals(priID)) {
				if ("1".equals(grade)) {
					if (StringUtils.isNotBlank(editor1) && !loginID.equals(editor1)) {
						pri_emp_id = editor1;
						hasFowordStep = true;
					}
				} else if ("2".equals(grade)) {
					if ("010".equals(priID) || "011".equals(priID) || "012".equals(priID) || "039".equals(priID)) {
						if (StringUtils.isNotBlank(editor1) && !loginID.equals(editor1)) {
							pri_emp_id = editor1;
							hasFowordStep = true;
						}
					} else {
						if (StringUtils.isNotBlank(editor2) && !loginID.equals(editor2)) {
							pri_emp_id = editor2;
							hasFowordStep = true;
						} else if (StringUtils.isNotBlank(editor1) && !loginID.equals(editor1)) {
							pri_emp_id = editor1;
							hasFowordStep = true;
						}
					}
				} else if ("3".equals(grade)) {
					if ("010".equals(priID) || "011".equals(priID) || "012".equals(priID) || "039".equals(priID)) {
						if (StringUtils.isNotBlank(editor1) && !loginID.equals(editor1)) {
							pri_emp_id = editor1;
							hasFowordStep = true;
						}
					} else if ("013".equals(priID) || "040".equals(priID)) {
						if (StringUtils.isNotBlank(editor2) && !loginID.equals(editor2)) {
							pri_emp_id = editor2;
							hasFowordStep = true;
						} else if (StringUtils.isNotBlank(editor1) && !loginID.equals(editor1)) {
							pri_emp_id = editor1;
							hasFowordStep = true;
						}
					} else {
						if (StringUtils.isNotBlank(editor3) && !loginID.equals(editor3)) {
							pri_emp_id = editor3;
							hasFowordStep = true;
						} else if (StringUtils.isNotBlank(editor2) && !loginID.equals(editor2)) {
							pri_emp_id = editor2;
							hasFowordStep = true;
						} else if (StringUtils.isNotBlank(editor1) && !loginID.equals(editor1)) {
							pri_emp_id = editor1;
							hasFowordStep = true;
						}
					}
				}
			}
		}

		if (StringUtils.isNotBlank(pri_emp_id)) {
			dam = this.getDataAccessManager();
			QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sb = new StringBuffer();
			sb.append("SELECT ORG.*, DEFN.DEPT_NAME AS PRI_DEPT_NAME ");
			sb.append("FROM ( ");
			sb.append("  SELECT EMP_ID AS PRI_EMP_ID, EMP_NAME AS PRI_EMP_NAME, ");
			sb.append("         DEPT_ID AS PRI_DEPT_ID, JOB_TITLE_NAME AS PRI_JOB_TITLE_NAME ");
			sb.append("  FROM TBORG_MEMBER ");
			sb.append("  WHERE EMP_ID = :emp_id ");
			sb.append(") ORG ");
			sb.append("LEFT JOIN TBORG_DEFN DEFN ON ORG.PRI_DEPT_ID = DEFN.DEPT_ID ");

			queryCondition.setObject("emp_id", pri_emp_id);
			queryCondition.setQueryString(sb.toString());
			resultList = dam.exeQuery(queryCondition);
		}
		outputVO.setResultList(resultList);
		outputVO.setHasFowordStep(hasFowordStep);
		this.sendRtnObject(outputVO);
	}

	//查詢登入者是否可抽回案件
	public void checkTakeBack(Object body, IPrimitiveMap header) throws JBranchException {
		
		CRM990InputVO inputVO = (CRM990InputVO) body;
		CRM990OutputVO outputVO = new CRM990OutputVO();

		if (StringUtils.isNotBlank(inputVO.getComplain_list_id())) {
			dam = this.getDataAccessManager();
			QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sb = new StringBuffer();
			sb.append("SELECT * ");
			sb.append("FROM ( ");
			sb.append("  SELECT SEQ, COMPLAIN_LIST_ID, EMP_ID, NEXT_EMP_ID, ROW_NUMBER() OVER (PARTITION BY COMPLAIN_LIST_ID ORDER BY CREATETIME DESC) AS SORT ");
			sb.append("  FROM TBCRM_CUST_COMPLAIN_FLOW ");
			sb.append("  WHERE COMPLAIN_LIST_ID = :complain_list_id ");
			sb.append(") ");
			sb.append("WHERE SORT = 2 ");

			queryCondition.setObject("complain_list_id", inputVO.getComplain_list_id());
			queryCondition.setQueryString(sb.toString());
			outputVO.setResultList(dam.exeQuery(queryCondition));
		}

		this.sendRtnObject(outputVO);
	}

	//查詢客訴軌跡
	public void showFlow(Object body, IPrimitiveMap header) throws JBranchException {
		
		CRM990InputVO inputVO = (CRM990InputVO) body;
		CRM990OutputVO outputVO = new CRM990OutputVO();

		if (StringUtils.isNotBlank(inputVO.getComplain_list_id())) {
			dam = this.getDataAccessManager();
			QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sb = new StringBuffer();
			sb.append("SELECT FLOW.*, ORG1.EMP_NAME, ORG2.EMP_NAME AS NEXT_EMP_NAME ");
			sb.append("FROM ( ");
			sb.append("  SELECT * ");
			sb.append("  FROM TBCRM_CUST_COMPLAIN_FLOW ");
			sb.append("  WHERE COMPLAIN_LIST_ID = :complain_list_id ");
			sb.append("  ORDER BY SUBMIT_DATE DESC ");
			sb.append(") FLOW ");
			sb.append("LEFT JOIN TBORG_MEMBER ORG1 ON ORG1.EMP_ID = FLOW.EMP_ID ");
			sb.append("LEFT JOIN TBORG_MEMBER ORG2 ON ORG2.EMP_ID = FLOW.NEXT_EMP_ID ");

			queryCondition.setObject("complain_list_id", inputVO.getComplain_list_id());
			queryCondition.setQueryString(sb.toString());
			outputVO.setResultList(dam.exeQuery(queryCondition));
		}
		this.sendRtnObject(outputVO);
	}

	//取得客訴表-投資明細損益表
	public void downloadFile(Object body, IPrimitiveMap header) throws Exception {
		
		CRM990InputVO inputVO = (CRM990InputVO) body;
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		String id = inputVO.getComplain_list_id();
		if (StringUtils.isNotBlank(id)) {
			TBCRM_CUST_COMPLAINVO vo = (TBCRM_CUST_COMPLAINVO) dam.findByPKey(TBCRM_CUST_COMPLAINVO.TABLE_UID, id);

			if (vo != null && vo.getUPLOAD_FILE() != null) {
				try {
					String filePath = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
					String uuid = UUID.randomUUID().toString();
					String fileName = String.format("投資明細損益表%s.xls", dateFormatOfNoSlash.format(new Date()));
					Blob blob = (Blob) vo.getUPLOAD_FILE();
					int blobLength = (int) blob.length();
					byte[] blobAsBytes = blob.getBytes(1, blobLength);

					File targetFile = new File(filePath, uuid);
					FileOutputStream fos = new FileOutputStream(targetFile);
					fos.write(blobAsBytes);
					fos.close();

					notifyClientToDownloadFile("temp//" + uuid, fileName);
					this.sendRtnObject(null);

				} catch (Exception e) {
					logger.debug(e.getMessage(), e);
				}
			}
		}
	}

	//列印
	public void exportPdf(Object body, IPrimitiveMap header) throws Exception {
		
		CRM990InputVO inputVO = (CRM990InputVO) body;
		//產生PDF檔案
		this.exportPdf(inputVO);
	}

	private void exportPdf(CRM990InputVO inputVO) throws Exception {
		
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = null;
		String id = inputVO.getComplain_list_id();

		if (StringUtils.isNotBlank(id)) {
			TBCRM_CUST_COMPLAINVO vo = (TBCRM_CUST_COMPLAINVO) dam.findByPKey(TBCRM_CUST_COMPLAINVO.TABLE_UID, id);
			if (null != vo) {
				XmlInfo xmlInfo = new XmlInfo();
				Map<String, String> sourceMap = xmlInfo.doGetVariable("CRM.COMPLAIN_SOURCE", FormatHelper.FORMAT_3); //客訴來源
				Map<String, String> educationMap = xmlInfo.doGetVariable("CRM.EDUCATION_STAT", FormatHelper.FORMAT_3); //學歷
				Map<String, String> checkMap = xmlInfo.doGetVariable("CRM.CHECK_SHEET", FormatHelper.FORMAT_3); //對帳單類別
				Map<String, String> billsMap = xmlInfo.doGetVariable("CRM.BILLS_TYPE", FormatHelper.FORMAT_3); //對帳單類別(for舊資料)

				Document.compress = false;
				Rectangle rectPageSize = new Rectangle(PageSize.A4); // 設定版面
				Document document = new Document(rectPageSize, 20, 20, 18, 15); // 設定邊距

				String uuid = UUID.randomUUID().toString();
				String fileName = String.format("客訴處理單%s.pdf", dateFormatOfNoSlash.format(new Date()));
				PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(tempFile + "//" + uuid));

				document.open();
				//				document.open();
				document.newPage();
				File nowpath = new File(DataManager.getRealPath(), "doc//FPS");

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
				text.add(new Chunk("　" + ObjectUtils.defaultIfNull(dateFormatOfSlash.format(vo.getHAPPEN_DATE()), "　　　　").toString() + "　", nFont9).setUnderline(0.2f, -2f));
				text.add(new Chunk("  編號：", nFont11));
				text.add(new Chunk("　" + ObjectUtils.defaultIfNull(vo.getCOMPLAIN_LIST_ID(), "　　　　").toString() + "　", nFont9).setUnderline(0.2f, -2f));
				text.add(new Chunk("  單位別：", nFont11));
				text.add(new Chunk("　" + ObjectUtils.defaultIfNull(vo.getBRANCH_NBR(), "　　　　").toString() + "　", nFont9).setUnderline(0.2f, -2f));

				String deptName = "";
				if (StringUtils.isNotBlank(vo.getBRANCH_NBR())) {
					queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
					queryCondition.setQueryString("select nvl(DEPT_NAME,'') DEPT_NAME from TBORG_DEFN where DEPT_ID = :dept_id ");
					queryCondition.setObject("dept_id", vo.getBRANCH_NBR());

					List<Map<String, Object>> list = dam.exeQuery(queryCondition);

					if (list.size() > 0) {
						if (list.get(0).get("DEPT_NAME") != null)
							deptName = list.get(0).get("DEPT_NAME").toString();
					}
				}

				text.add(new Chunk(deptName + "　", nFont9).setUnderline(0.2f, -2f));

				text.setAlignment(Paragraph.ALIGN_LEFT);
				cell1.addElement(text);
				table.addCell(cell1);
				document = setPdfPTable(document, rowCnt + 2, table);

				table = getPdfPTable();
				cell1 = new PdfPCell();
				cell1.setBorderWidth(0);
				cell1.setFixedHeight(30);
				text = new Paragraph("主管簽核：", nFont11);
				text.add(new Chunk(" " + ObjectUtils.defaultIfNull(this.getEmpName(vo.getEDITOR_CONDITION1()), "　　　　").toString() + " ", nFont9).setUnderline(0.2f, -2f));
				text.add(new Chunk(" 專員：", nFont11));
				text.add(new Chunk(" " + ObjectUtils.defaultIfNull(vo.getEMP_NAME(), "　　　　").toString(), nFont9).setUnderline(0.2f, -2f));
				text.add(new Chunk("(申訴事件主要服務人員) ", nFont8).setUnderline(0.2f, -2f));

				text.add(new Chunk(" AO CODE：", nFont11));
				text.add(new Chunk(" " + ObjectUtils.defaultIfNull(vo.getAO_CODE(), "　　　　").toString() + " ", nFont9).setUnderline(0.2f, -2f));
				text.add(new Chunk(" 員編：", nFont11));
				text.add(new Chunk(" " + ObjectUtils.defaultIfNull(vo.getEMP_ID(), "　　　　").toString() + " ", nFont9).setUnderline(0.2f, -2f));
				text.add(new Chunk("  是否在職：", nFont11));

				if ("01".equals(vo.getSERVICE_YN())) {
					text.add(new Chunk(" 是 ", nFont9).setUnderline(0.2f, -2f));
				} else {
					text.add(new Chunk(" " + ObjectUtils.defaultIfNull(vo.getSERVICE_YN() == "02" ? "否" : null, "　　　　").toString() + " ", nFont9).setUnderline(0.2f, -2f));
				}

				text.setAlignment(Paragraph.ALIGN_LEFT);
				cell1.addElement(text);
				table.addCell(cell1);
				document = setPdfPTable(document, rowCnt + 1, table);

				table = getPdfPTable();
				cell1 = new PdfPCell();

				String grade1 = "□", grade2 = "□", grade3 = "□";

				switch (ObjectUtils.defaultIfNull(vo.getGRADE(), "").toString().charAt(0)) {
				case '1':
					grade1 = "■";
					break;
				case '2':
					grade2 = "■";
					break;
				case '3':
					grade3 = "■";
					break;
				}
				text = new Paragraph("等級(勾選) " + grade1 + "1★　　 " + grade2 + "2★★　　 " + grade3 + "3★★★", nFontB12);
				text.setAlignment(Paragraph.ALIGN_LEFT);
				cell1.addElement(text);

				text = new Paragraph("客訴來源：", nFontB12);
				String source = null;
				if (StringUtils.isNotBlank(vo.getCOMPLAIN_SOURCE())) {
					if (sourceMap.containsKey(vo.getCOMPLAIN_SOURCE())) {
						source = sourceMap.get(vo.getCOMPLAIN_SOURCE());
					}
				}
				text.add(new Chunk(" " + ObjectUtils.defaultIfNull(source, "　　　　").toString() + " ", nFont9).setUnderline(0.2f, -2f));
				text.setAlignment(Paragraph.ALIGN_LEFT);
				cell1.addElement(text);

				text = new Paragraph("", nFont12);
				text.add(new Chunk("客訴摘要：", nFontB12));
				text.add(new Chunk(ObjectUtils.defaultIfNull(vo.getCOMPLAIN_SUMMARY(), "　　　　") + " ", nFont12));
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
				text.add(new Chunk("　" + ObjectUtils.defaultIfNull(vo.getCUST_NAME(), "　").toString() + "　", nFontB12).setUnderline(0.2f, -2f));

				String brithdate = ""; //生日轉成yyyy/mm/dd格式
				brithdate = ObjectUtils.defaultIfNull(vo.getBIRTHDATE(), "").toString();

				if (brithdate != null && brithdate != "")
					brithdate = dateFormatOfNoSlash.format(vo.getBIRTHDATE()).toString();
				else
					brithdate = "  ";

				text.add(new Chunk("出生年月日(西元)：", nFontB12));
				text.add(new Chunk("　" + brithdate + "　", nFontB12).setUnderline(0.2f, -2f));

				text.add(new Chunk("身分證字號：", nFontB12));
				text.add(new Chunk("　" + ObjectUtils.defaultIfNull(vo.getCUST_ID(), " 　 ").toString() + "　", nFontB12).setUnderline(0.2f, -2f));

				text.add(new Chunk("職業：", nFontB12));
				//				String occup = null;
				//				if(StringUtils.isNotBlank(vo.getOCCUP())){
				//					if(occupMap.containsKey(vo.getOCCUP())){
				//						occup = occupMap.get(vo.getOCCUP());
				//					}					
				//				}
				text.add(new Chunk("　" + ObjectUtils.defaultIfNull(vo.getOCCUP(), " 　 ").toString() + "　", nFontB12).setUnderline(0.2f, -2f));

				text.setAlignment(Paragraph.ALIGN_LEFT);
				cell1.addElement(text);

				text = new Paragraph("聯絡電話：", nFontB12);
				text.add(new Chunk("　" + ObjectUtils.defaultIfNull(vo.getPHONE(), "　　").toString() + "　", nFontB12).setUnderline(0.2f, -2f));

				text.add(new Chunk("學歷：", nFontB12));
				String education = null;
				if (StringUtils.isNotBlank(vo.getEDUCATION())) {
					if (educationMap.containsKey(vo.getEDUCATION())) {
						education = educationMap.get(vo.getEDUCATION());
					}
				}
				text.add(new Chunk("　" + ObjectUtils.defaultIfNull(education, "　　").toString() + "　", nFontB12).setUnderline(0.2f, -2f));
				text.add(new Paragraph("\n"));
				cell1.addElement(text);

				text = new Paragraph("B.往來資訊", nFontB12);
				text.setAlignment(Paragraph.ALIGN_LEFT);
				cell1.addElement(text);

				String open_acc_date = null; //開戶日期轉成yyyy/mm/dd格式
				if (vo.getOPEN_ACC_DATE() != null)
					open_acc_date = dateFormatOfSlash.format(vo.getOPEN_ACC_DATE()).toString();
				text = new Paragraph("開戶日期：", nFontB12);
				text.add(new Chunk("　" + ObjectUtils.defaultIfNull(open_acc_date, "　　").toString() + "　", nFontB12).setUnderline(0.2f, -2f));

				text.add(new Chunk("總往來資產AUM：", nFontB12));
				text.add(new Chunk("　" + ObjectUtils.defaultIfNull(vo.getTOTAL_ASSET(), "　　").toString() + "　", nFontB12).setUnderline(0.2f, -2f));
				text.setAlignment(Paragraph.ALIGN_LEFT);
				cell1.addElement(text);

				text = new Paragraph("對帳單類別:", nFontB12);
				String check_sheet = null;
				if (StringUtils.isNotBlank(vo.getCHECK_SHEET())) {
					if (checkMap.containsKey(vo.getCHECK_SHEET())) {
						check_sheet = checkMap.get(vo.getCHECK_SHEET());
					}
					if (billsMap.containsKey(vo.getCHECK_SHEET())) {
						check_sheet = billsMap.get(vo.getCHECK_SHEET());
					}
				}

				text.add(new Chunk("　" + ObjectUtils.defaultIfNull(check_sheet, "　　").toString() + "　", nFontB12).setUnderline(0.2f, -2f));
				text.add(new Paragraph("\n"));
				text.setAlignment(Paragraph.ALIGN_LEFT);
				cell1.addElement(text);

				text = new Paragraph("C.往來商品項目", nFontB12);
				text.setAlignment(Paragraph.ALIGN_LEFT);
				cell1.addElement(text);

				text = new Paragraph("銀行：", nFontB12);
				text.add(new Chunk("　　　　　　" + ObjectUtils.defaultIfNull(vo.getBUY_PRODUCT_TYPE(), "　　　　　　").toString() + "　　　　　　", nFont12).setUnderline(0.2f, -2f));

				//確認此客訴單是否有附（加附投資明細損益表）
				if (null != vo.getUPLOAD_FILE()) {
					text.add(new Chunk("（加附投資明細損益表）", nFontB12));
				}
				text.setAlignment(Paragraph.ALIGN_LEFT);
				cell1.addElement(text);
				text = new Paragraph("集團：", nFontB12);
				text.add(new Chunk("　　　　　　　　　　　　　　　　　　", nFont12).setUnderline(0.2f, -2f));
				text.setAlignment(Paragraph.ALIGN_LEFT);
				cell1.addElement(text);

				text = new Paragraph("主要客訴項目或商品╱金額：", nFontB12);
				text.add(new Chunk("　　　(" + ObjectUtils.defaultIfNull(vo.getCOMPLAIN_PRODUCT(), "").toString() + ")", nFont12).setUnderline(0.2f, -2f));
				if (StringUtils.isNotBlank(vo.getCOMPLAIN_PRODUCT_CURRENCY()))
					text.add(new Chunk("　" + vo.getCOMPLAIN_PRODUCT_CURRENCY() + "：", nFont12).setUnderline(0.2f, -2f));
				else
					text.add(new Chunk("　　　　　　　", nFont12).setUnderline(0.2f, -2f));
				if (null != vo.getCOMPLAIN_PRODUCT_AMOUN())
					text.add(new Chunk("╱ $" + ObjectUtils.defaultIfNull(vo.getCOMPLAIN_PRODUCT_AMOUN(), "　　　　　　　").toString() + "　　", nFont12).setUnderline(0.2f, -2f));
				else
					text.add(new Chunk("　　　　　　　", nFont12).setUnderline(0.2f, -2f));
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
				String problem = ObjectUtils.defaultIfNull(vo.getPROBLEM_DESCRIBE(), "").toString();
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
				String desc = ObjectUtils.defaultIfNull(vo.getCUST_DESCRIBE(), "").toString();
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
				document = setPdfPTable(document, rowCnt + 1, table);

				table = getPdfPTable();
				cell1 = new PdfPCell();
				String condition1 = ObjectUtils.defaultIfNull(vo.getHANDLE_CONDITION1(), "").toString();
				text = new Paragraph("", nFont12);
				text.add(new Chunk("各單位處理：（請詳載處理過程、並務必釐清服務或銷售流程是否皆已依規辦理）\n", nFontB12));
				text.add(new Chunk(condition1, nFont11));
				text.add(new Paragraph("\n"));
				text.setAlignment(Paragraph.ALIGN_LEFT);
				cell1.addElement(text);
				table.addCell(cell1);
				document = setPdfPTable(document, rowCnt + 1 + condition1.split("\n").length, table);

				//.....................................................................
				document.add(new Paragraph("\n")); //空行
				//.....................................................................

				table = getPdfPTable();
				cell1 = new PdfPCell();
				cell1.setBorderWidth(0);
				cell1.setFixedHeight(30);
				text = new Paragraph("第二級處理情形\n", nFontB12);
				text.setAlignment(Paragraph.ALIGN_LEFT);
				cell1.addElement(text);
				table.addCell(cell1);
				document = setPdfPTable(document, rowCnt + 1, table);

				table = getPdfPTable();
				cell1 = new PdfPCell();
				String condition2 = ObjectUtils.defaultIfNull(vo.getHANDLE_CONDITION2(), "").toString();
				text = new Paragraph("", nFont12);
				text.add(new Chunk("營運督導/客服科長：（若各單位處理無效，由督導代表總行協助處理）\n", nFontB12));
				text.add(new Chunk(condition2, nFont11));
				text.add(new Paragraph("\n"));
				text.setAlignment(Paragraph.ALIGN_LEFT);
				cell1.addElement(text);
				table.addCell(cell1);
				document = setPdfPTable(document, rowCnt + 1 + condition2.split("\n").length, table);

				//.....................................................................
				document.add(new Paragraph("\n")); //空行
				//.....................................................................

				table = getPdfPTable();
				cell1 = new PdfPCell();
				cell1.setBorderWidth(0);
				cell1.setFixedHeight(30);
				text = new Paragraph("第三級處理情形\n", nFontB12);
				text.setAlignment(Paragraph.ALIGN_LEFT);
				cell1.addElement(text);
				table.addCell(cell1);
				document = setPdfPTable(document, rowCnt + 1, table);

				table = getPdfPTable();
				cell1 = new PdfPCell();
				String condition3 = ObjectUtils.defaultIfNull(vo.getHANDLE_CONDITION3(), "").toString();
				text = new Paragraph("", nFont12);
				text.add(new Chunk("個金分行業務各處/副主管：\n", nFontB12));
				text.add(new Chunk(condition3, nFont11));
				text.add(new Paragraph("\n"));
				text.setAlignment(Paragraph.ALIGN_LEFT);
				cell1.addElement(text);
				table.addCell(cell1);
				document = setPdfPTable(document, rowCnt + 2 + condition3.split("\n").length, table);

				//.....................................................................
				document.add(new Paragraph("\n")); //空行
				//.....................................................................

				table = getPdfPTable();
				cell1 = new PdfPCell();
				cell1.setBorderWidth(0);
				cell1.setFixedHeight(30);
				text = new Paragraph("總行處理情形\n", nFontB12);
				text.setAlignment(Paragraph.ALIGN_LEFT);
				cell1.addElement(text);
				table.addCell(cell1);
				document = setPdfPTable(document, rowCnt + 1, table);

				table = getPdfPTable();
				cell1 = new PdfPCell();
				String condition4 = ObjectUtils.defaultIfNull(vo.getHANDLE_CONDITION4(), "").toString();
				text = new Paragraph("", nFont12);
				text.add(new Chunk("總行處理：（此欄位由總行處理單位人員填寫）\n", nFontB12));
				text.add(new Chunk(condition4, nFont11));
				text.add(new Paragraph("\n"));
				text.setAlignment(Paragraph.ALIGN_LEFT);
				cell1.addElement(text);
				table.addCell(cell1);
				document = setPdfPTable(document, rowCnt + 2 + condition4.split("\n").length, table);

				//.....................................................................
				document.add(new Paragraph("\n", nFont8)); //空行
				//.....................................................................

				table = getPdfPTable();
				cell1 = new PdfPCell();
				cell1.setFixedHeight(70);
				text = new Paragraph("結案日期:", nFont12);
				String endDate = ObjectUtils.defaultIfNull(vo.getEND_DATE(), "").toString();
				if (endDate != null && !"".equals(endDate)) {
					endDate = dateFormatOfNoSlash.format(vo.getEND_DATE()).toString();
				} else {
					endDate = "　　　";
				}
				text.add(new Chunk("　　" + endDate + "　　", nFont12).setUnderline(0.2f, -2f));

				StringBuffer sb = new StringBuffer("\n處理人員:");

				if (StringUtils.isNotBlank(vo.getEDITOR_CONDITION1())) {
					String editor1 = this.getEmpName(vo.getEDITOR_CONDITION1());
					sb.append("第一級-" + editor1).append("    ");
				}
				if (StringUtils.isNotBlank(vo.getEDITOR_CONDITION2())) {
					String editor2 = this.getEmpName(vo.getEDITOR_CONDITION2());
					sb.append("第二級-" + editor2).append("    ");
				}
				if (StringUtils.isNotBlank(vo.getEDITOR_CONDITION3())) {
					String editor3 = this.getEmpName(vo.getEDITOR_CONDITION3());
					sb.append("第三級-" + editor3).append("    ");
				}
				if (StringUtils.isNotBlank(vo.getEDITOR_CONDITION4())) {
					String editor4 = this.getEmpName(vo.getEDITOR_CONDITION4());
					sb.append("\n總行1-" + editor4).append("    ");
				}
				if (StringUtils.isNotBlank(vo.getEDITOR_CONDITION5())) {
					String editor5 = this.getEmpName(vo.getEDITOR_CONDITION5());
					sb.append("總行2-" + editor5).append("    ");
				}

				text.add(new Chunk(sb.toString(), nFont12));
				text.setAlignment(Paragraph.ALIGN_LEFT);
				cell1.addElement(text);
				table.addCell(cell1);
				document = setPdfPTable(document, rowCnt + 3, table);

				document.close();
				notifyClientToDownloadFile("temp/reports/" + uuid, fileName);

			} else {
				throw new APException("此客訴單無法列印！");
			}
		}
	}

	//用EMP_ID 查 EMP_NAME
	private String getEmpName(String emp_id) throws Exception {
		String emp_name = null;
		if (StringUtils.isNotBlank(emp_id)) {
			dam = this.getDataAccessManager();
			QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			queryCondition.setQueryString("SELECT EMP_NAME FROM TBORG_MEMBER WHERE EMP_ID = :emp_id ");
			queryCondition.setObject("emp_id", emp_id);
			List<Map<String, Object>> list = dam.exeQuery(queryCondition);
			if (list.size() > 0) {
				if (list.get(0).get("EMP_NAME") != null)
					emp_name = list.get(0).get("EMP_NAME").toString();
			}
		}
		return emp_name;
	}

	private PdfPTable getPdfPTable() throws Exception {
		PdfPTable table = new PdfPTable(1);
		int[] widths = new int[] { 84 };
		table.setWidths(widths);
		table.setWidthPercentage(98);
		return table;
	}

	private Document setPdfPTable(Document document, int rowCnt, PdfPTable table) throws Exception {
		if (rowCnt > 70) {
			document.newPage();
		}
		document.add(table);
		return document;
	}

	//列印備查簿
	public void exportResult(Object body, IPrimitiveMap<?> header) throws Exception {
		CRM990InputVO inputVO = (CRM990InputVO) body;

		//撈取所需資料
		List<Map<String, Object>> resultList = this.getExportResult(inputVO);

		//相關資訊設定
		String fileName = String.format("客訴備查簿%s.xlsx", dateFormatOfNoSlash.format(new Date()));
		String uuid = UUID.randomUUID().toString();

		//建置Excel
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("客訴備查簿" + dateFormatOfNoSlash.format(new Date()));
		sheet.setDefaultColumnWidth(20);
		sheet.setDefaultRowHeightInPoints(20);

		//Subject font型式
		XSSFFont font = workbook.createFont();
		font.setColor(HSSFColor.BLACK.index);
		font.setBoldweight((short) Font.NORMAL);
		font.setFontHeight((short) 500);

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
		row.setHeight((short) 800);
		XSSFCell sCell = row.createCell(0);
		sCell.setCellStyle(SubjectStyle);
		sCell.setCellValue("個　人　金　融　總　處　客　訴　備　查　簿　");
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 5));

		//日期
		String dateStartStr = "";
		String dateEndStr = "";

		if (inputVO.getS_createtime() != null) {
			dateStartStr = dateFormatOfSlash.format(inputVO.getS_createtime());
		}
		if (inputVO.getE_createtime() != null) {
			dateEndStr = dateFormatOfSlash.format(inputVO.getE_createtime());
		}

		XSSFCell sCell2 = row.createCell(6);
		sCell2.setCellStyle(SubjectStyle);
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 6, 10));
		sCell2.setCellValue("日期：" + dateStartStr + " ～ " + dateEndStr);

		index++;

		//header
		String[] headerLine1 = { "建案日期", "編號", "客訴等級", "客戶姓名", "身分證字號", "客戶訴求", "處理情形", "結案日期", "第一級處理人員", "第二級處理人員", "第三級處理人員", "總行處理人員1", "總行處理人員2", "是否符合公平待客原則" };

		String[] mainLine = { "CREATETIME", "COMPLAIN_LIST_ID", "GRADE", "CUST_NAME", "CUST_ID", "CUST_DESCRIBE", "HANDLE_CONDITION", "END_DATE", "EDITOR_CONDITION1", "EDITOR_CONDITION2", "EDITOR_CONDITION3", "EDITOR_CONDITION4", "EDITOR_CONDITION5", "TREAT_CUST_FAIRLY" };
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

		if (resultList.size() > 0) {
			for (Map<String, Object> map : resultList) {
				row = sheet.createRow(index);

				if (map.size() > 0) {
					for (int j = 0; j < mainLine.length; j++) {
						XSSFCell cell = row.createCell(j);
						cell.setCellStyle(mainStyle);
						if (mainLine[j].indexOf("HANDLE_CONDITION") >= 0) { //處理狀態欄位
							String datastr = "";
							if (map.containsKey("HANDLE_CONDITION1") && (map.get("HANDLE_CONDITION1") != null)) {
								if (map.get("HANDLE_CONDITION1") != null) {
									datastr += "[一級處理情形]：" + map.get("HANDLE_CONDITION1") + "\n";
								}
								if (map.get("HANDLE_CONDITION2") != null) {
									datastr += "[二級處理情形]：" + map.get("HANDLE_CONDITION2") + "\n";
								}
								if (map.get("HANDLE_CONDITION3") != null) {
									datastr += "[三級處理情形]：" + map.get("HANDLE_CONDITION3") + "\n";
								}
								if (map.get("HANDLE_CONDITION4") != null) {
									datastr += "[總行處理情形]：" + map.get("HANDLE_CONDITION4");
								}
							}
							cell.setCellValue(datastr);

						} else if (mainLine[j].indexOf("EDITOR_CONDITION1") >= 0) { //第一級處理人員欄位
							String editor = "";
							if (map.containsKey("EDITOR_CONDITION1") && map.get("EDITOR_CONDITION1") != null) {
								editor = map.get("EDITOR_CONDITION1").toString() + map.get("EDIT_EMP_NAME1").toString();
							}
							cell.setCellValue(editor);

						} else if (mainLine[j].indexOf("EDITOR_CONDITION2") >= 0) { //第二級處理人員欄位
							String editor = "";
							if (map.containsKey("EDITOR_CONDITION2") && map.get("EDITOR_CONDITION2") != null) {
								editor = map.get("EDITOR_CONDITION2").toString() + map.get("EDIT_EMP_NAME2").toString();
							}
							cell.setCellValue(editor);
						} else if (mainLine[j].indexOf("EDITOR_CONDITION3") >= 0) { //第三級處理人員欄位
							String editor = "";
							if (map.containsKey("EDITOR_CONDITION3") && map.get("EDITOR_CONDITION3") != null) {
								editor = map.get("EDITOR_CONDITION3").toString() + map.get("EDIT_EMP_NAME3").toString();
							}
							cell.setCellValue(editor);
						} else if (mainLine[j].indexOf("EDITOR_CONDITION4") >= 0) { //總行處理人員1欄位
							String editor = "";
							if (map.containsKey("EDITOR_CONDITION4") && map.get("EDITOR_CONDITION4") != null) {
								editor = map.get("EDITOR_CONDITION4").toString() + map.get("EDIT_EMP_NAME4").toString();
							}
							cell.setCellValue(editor);
						} else if (mainLine[j].indexOf("EDITOR_CONDITION5") >= 0) { //總行處理人員2欄位
							String editor = "";
							if (map.containsKey("EDITOR_CONDITION5") && map.get("EDITOR_CONDITION5") != null) {
								editor = map.get("EDITOR_CONDITION5").toString() + map.get("EDIT_EMP_NAME5").toString();
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
		String filePath = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
		File targetFile = new File(filePath, uuid);
		FileOutputStream fos = new FileOutputStream(targetFile);

		workbook.write(fos);
		workbook.close();
		notifyClientToDownloadFile("temp//" + uuid, fileName);
	}

	//取得列印備查簿資料：
	//建案日期、編號、客訴等級、客戶姓名、身分證字號、客戶訴求、(各階)處理情形、結案日期、
	//第一級處人員、第二級處人員、第三級處人員、總行處人員1、總行處人員2
	private List<Map<String, Object>> getExportResult(CRM990InputVO inputVO) throws JBranchException {
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();

		sb.append("SELECT TO_CHAR(COM.CREATETIME,'YYYY/MM/DD') AS CREATETIME, COM.TREAT_CUST_FAIRLY, ");
		sb.append("       COM.COMPLAIN_LIST_ID, COM.GRADE, TO_CHAR(COM.END_DATE,'YYYY/MM/DD') AS END_DATE, ");
		sb.append("       COM.BRANCH_NBR, COM.CUST_NAME, COM.CUST_ID, COM.CUST_DESCRIBE, COM.HANDLE_STEP, ");
		sb.append("       COM.HANDLE_CONDITION1, COM.HANDLE_CONDITION2, ");
		sb.append("       COM.HANDLE_CONDITION3, COM.HANDLE_CONDITION4, ");
		sb.append("       COM.EDITOR_CONDITION1, ORG1.EMP_NAME AS EDIT_EMP_NAME1, ");
		sb.append("       COM.EDITOR_CONDITION2, ORG2.EMP_NAME AS EDIT_EMP_NAME2, ");
		sb.append("       COM.EDITOR_CONDITION3, ORG3.EMP_NAME AS EDIT_EMP_NAME3, ");
		sb.append("       COM.EDITOR_CONDITION4, ORG4.EMP_NAME AS EDIT_EMP_NAME4, ");
		sb.append("       COM.EDITOR_CONDITION5, ORG5.EMP_NAME AS EDIT_EMP_NAME5 ");
		sb.append("FROM TBCRM_CUST_COMPLAIN COM ");
		sb.append("LEFT JOIN TBORG_MEMBER ORG1 ON COM.EDITOR_CONDITION1 = ORG1.EMP_ID ");
		sb.append("LEFT JOIN TBORG_MEMBER ORG2 ON COM.EDITOR_CONDITION2 = ORG2.EMP_ID ");
		sb.append("LEFT JOIN TBORG_MEMBER ORG3 ON COM.EDITOR_CONDITION3 = ORG3.EMP_ID ");
		sb.append("LEFT JOIN TBORG_MEMBER ORG4 ON COM.EDITOR_CONDITION4 = ORG4.EMP_ID ");
		sb.append("LEFT JOIN TBORG_MEMBER ORG5 ON COM.EDITOR_CONDITION5 = ORG5.EMP_ID ");
		sb.append("WHERE COM.HANDLE_STEP <> 'D' ");

		// 依系統角色決定下拉選單可視範圍
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> headmgrMap = xmlInfo.doGetVariable("FUBONSYS.HEADMGR_ROLE", FormatHelper.FORMAT_2); //總行
		if (!headmgrMap.containsKey((String) getUserVariable(FubonSystemVariableConsts.LOGINROLE))) { //非總行人員查詢
			sb.append("AND COM.BRANCH_NBR IN (:branch_nbr_list ) ");
			queryCondition.setObject("branch_nbr_list", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
		}

		//客戶統編
		if (StringUtils.isNotBlank(inputVO.getCust_id())) {
			sb.append("AND COM.CUST_ID = :cust_id ");
			queryCondition.setObject("cust_id", inputVO.getCust_id());
		}

		//分行別
		if (StringUtils.isNotBlank(inputVO.getBranch_nbr())) {
			sb.append("AND COM.BRANCH_NBR = :branch_nbr ");
			queryCondition.setObject("branch_nbr", inputVO.getBranch_nbr());
		}

		//建案日期(起)
		if (inputVO.getS_createtime() != null) {
			sb.append("AND TRUNC(COM.CREATETIME) >= TRUNC( :start ) ");
			queryCondition.setObject("start", inputVO.getS_createtime());
		}

		//發生日期(迄)
		if (inputVO.getE_createtime() != null) {
			sb.append("AND TRUNC(COM.CREATETIME) <= TRUNC( :end ) ");
			queryCondition.setObject("end", inputVO.getE_createtime());
		}

		//狀態
		if (StringUtils.isNotBlank(inputVO.getHandle_step())) {
			if ("C".equals(inputVO.getHandle_step())) { //客服案件
				sb.append("AND COM.BRANCH_NBR = '806' ");
			} else {
				sb.append("AND COM.HANDLE_STEP = :handle_step ");
				queryCondition.setObject("handle_step", inputVO.getHandle_step());
			}
		}

		sb.append("ORDER BY COM.CREATETIME DESC ");
		
		queryCondition.setQueryString(sb.toString());
		
		resultList = dam.exeQuery(queryCondition);

		return resultList;
	}

	//取得組織
	private Map<String, String> getDefnInfo(String branch_nbr) throws JBranchException {
		
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		
		Map<String, String> map = new HashMap<>();
		
		sb.append("SELECT REGION_CENTER_ID, BRANCH_AREA_ID FROM VWORG_DEFN_INFO WHERE BRANCH_NBR = :branch_nbr ");
		
		queryCondition.setObject("branch_nbr", branch_nbr);
		
		queryCondition.setQueryString(sb.toString());
		
		List<Map<String, String>> list = dam.exeQuery(queryCondition);
		
		if (list.size() > 0) {
			map = list.get(0);
		}
		
		return map;
	}

	//取得客訴編號
	private String getComplainSeq() throws JBranchException {
		
		SerialNumberUtil sn = new SerialNumberUtil();
		String seqNum = "";
		DateFormat df = new SimpleDateFormat("yyyyMMdd");
		String date = df.format(new Date());

		try {
			seqNum = date + String.format("%04d", Integer.valueOf(sn.getNextSerialNumber("TBCRM_CUST_COMPLAIN")));
		} catch (Exception e) {
			sn.createNewSerial("TBCRM_CUST_COMPLAIN", "0000", 1, "d", null, 1, new Long("9999"), "y", new Long("0"), null);
			seqNum = date + String.format("%04d", Integer.valueOf(sn.getNextSerialNumber("TBCRM_CUST_COMPLAIN")));
		}

		return seqNum;
	}

	// 產生seq No
	private BigDecimal getFlowSeq() throws JBranchException {
		
		dam = this.getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuilder sb = new StringBuilder();
		
		BigDecimal seqNum = null;
		List<Map<String, Object>> newTrade_SEQNOList = new ArrayList<Map<String, Object>>();
		
		sb.append(" SELECT SQ_TBCRM_CUST_COMPLAIN_FLOW.NEXTVAL FROM DUAL ");
		
		qc.setQueryString(sb.toString());
		
		newTrade_SEQNOList = dam.exeQuery(qc);
		
		seqNum = new BigDecimal(newTrade_SEQNOList.get(0).get("NEXTVAL").toString());
		
		return seqNum;
	}

	// date2比date1多的天数
	private int differentDays(Date date1, Date date2) {
		
		Calendar cal1 = Calendar.getInstance();
		cal1.setTime(date1);

		Calendar cal2 = Calendar.getInstance();
		cal2.setTime(date2);
		int day1 = cal1.get(Calendar.DAY_OF_YEAR);
		int day2 = cal2.get(Calendar.DAY_OF_YEAR);

		int year1 = cal1.get(Calendar.YEAR);
		int year2 = cal2.get(Calendar.YEAR);
		if (year1 != year2) { //不同年
			int timeDistance = 0;
			for (int i = year1; i < year2; i++) {
				if (i % 4 == 0 && i % 100 != 0 || i % 400 == 0) { //閏年
					timeDistance += 366;
				} else { //不是閏年
					timeDistance += 365;
				}
			}
			return timeDistance + (day2 - day1);

		} else { //同一年
			return day2 - day1;
		}
	}

	//可能會以兼職的角色登入，要抓取登入者的主要PRIVILEGEID(非兼職的PRIVILEGEID。若主要PRIVILEGEID不在範圍內，才取兼職的PRIVILEGEID)
	private String getRealPirID(String emp_id) throws JBranchException {
		
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();

		sb.append("SELECT PRI.PRIVILEGEID, MAIN_TYPE ");
		sb.append("FROM ( ");
		sb.append("  SELECT ROLE_ID, 'Y' AS MAIN_TYPE FROM VWORG_BRANCH_EMP_DETAIL_INFO WHERE EMP_ID = :emp_id ");
		sb.append("  UNION ALL ");
		sb.append("  SELECT ROLE_ID, 'N' AS MAIN_TYPE FROM VWORG_EMP_PLURALISM_INFO WHERE EMP_ID = :emp_id ");
		sb.append(") EMP ");
		sb.append("LEFT JOIN TBSYSSECUROLPRIASS PRI ON EMP.ROLE_ID = PRI.ROLEID ");
		sb.append("WHERE 1 = 1 ");
		sb.append("AND PRI.PRIVILEGEID IN ('006', '007', '009', '010', '011', '012', '013', '038', '039', '040', '071', '072') "); //'041', '042', 

		queryCondition.setObject("emp_id", emp_id);
		
		queryCondition.setQueryString(sb.toString());
		
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);

		String pri_id = null;
		if (list.size() == 1) {
			pri_id = list.get(0).get("PRIVILEGEID").toString();
		} else if (list.size() > 1) {
			for (int i = 0; i < list.size(); i++) {
				if ("Y".equals(list.get(i).get("MAIN_TYPE").toString())) {
					pri_id = list.get(i).get("PRIVILEGEID").toString();
				}
			}
		}
		
		return pri_id;
	}

	//查詢登入者是否為"客服部"
	private boolean is806() throws JBranchException {
		
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		
		sb.append("SELECT CASE WHEN INSTR(DEPT_ID, '806') > 0 THEN '806' ELSE DEPT_ID END TERRITORY_ID ");
		sb.append("FROM TBORG_MEMBER ");
		sb.append("WHERE EMP_ID = :emp_id ");

		String loginID = (String) getCommonVariable(SystemVariableConsts.LOGINID);
		queryCondition.setObject("emp_id", loginID);
		queryCondition.setQueryString(sb.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		if (list.size() > 0 && list.get(0).get("TERRITORY_ID") != null) {
			String territory_id = list.get(0).get("TERRITORY_ID").toString();
			if ("806".equals(territory_id))
				return true;
		}
		
		return false;
	}

	// 檢查Map取出欄位是否為Null
	private String checkMap(Map map, String key) {
		
		if (null != map && null != map.get(key)) {
			return String.valueOf(map.get(key));
		} else {
			return "";
		}
	}

	//刪除
	public void deleteCase(Object body, IPrimitiveMap header) throws JBranchException {
		
		CRM990InputVO inputVO = (CRM990InputVO) body;
		dam = this.getDataAccessManager();

		String id = inputVO.getComplain_list_id();
		if (StringUtils.isNotBlank(id)) {
			TBCRM_CUST_COMPLAINVO vo = (TBCRM_CUST_COMPLAINVO) dam.findByPKey(TBCRM_CUST_COMPLAINVO.TABLE_UID, id);
			if (null != vo) {
				vo.setHANDLE_STEP("D"); //D.已刪除
				dam.update(vo);

				this.sendRtnObject(null);
			}
		} else {
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}

	//總行科長放行
	public void release(Object body, IPrimitiveMap header) throws JBranchException {
		
		CRM990InputVO inputVO = (CRM990InputVO) body;
		String[] release_ids = inputVO.getRelease_ids();
		dam = this.getDataAccessManager();
		
		if (release_ids.length > 0) {
			for (String complain_list_id : release_ids) {
				TBCRM_CUST_COMPLAINVO vo = (TBCRM_CUST_COMPLAINVO) dam.findByPKey(TBCRM_CUST_COMPLAINVO.TABLE_UID, complain_list_id);
				vo.setEDITOR_CONDITION5((String) getCommonVariable(SystemVariableConsts.LOGINID));
				dam.update(vo);

				this.endTheCase(complain_list_id);
			}
		} else {
			throw new APException("無案件可覆核");
		}
		
		this.sendRtnObject(null);
	}

	//取消結案(客訴處理流程會回到第一級)
	public void revive(Object body, IPrimitiveMap header) throws JBranchException {
		
		CRM990InputVO inputVO = (CRM990InputVO) body;
		String id = inputVO.getComplain_list_id();
		dam = this.getDataAccessManager();
		
		String loginID = (String) getCommonVariable(SystemVariableConsts.LOGINID);
		
		if (StringUtils.isNotBlank(id)) {
			TBCRM_CUST_COMPLAINVO vo = (TBCRM_CUST_COMPLAINVO) dam.findByPKey(TBCRM_CUST_COMPLAINVO.TABLE_UID, id);
			if (null != vo) {
				String editor1 = StringUtils.isEmpty(vo.getEDITOR_CONDITION1()) ? vo.getCreator() : vo.getEDITOR_CONDITION1();
				String realPri = this.getRealPirID(editor1);
				String handleStep = getHandleStep(realPri);
				vo.setHANDLE_STEP(handleStep);
				dam.update(vo);

				//新增客訴流程明細檔(TBCRM_CUST_COMPLAIN_FLOW)
				TBCRM_CUST_COMPLAIN_FLOWVO flowVO = new TBCRM_CUST_COMPLAIN_FLOWVO();
				flowVO.setSEQ(this.getFlowSeq());
				flowVO.setCOMPLAIN_LIST_ID(id);
				flowVO.setEMP_ID(loginID);
				flowVO.setNEXT_EMP_ID(editor1);
				flowVO.setSTATUS("RV"); //W：中途、E：結案、BO：退回上一級、BF：退回第一級、R：抽回、RV：取消結案
				flowVO.setSUBMIT_DATE(new Timestamp(new Date().getTime()));
				dam.create(flowVO);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {

				}
				
				TBCRM_CUST_COMPLAIN_FLOWVO newFlowVO = new TBCRM_CUST_COMPLAIN_FLOWVO();
				newFlowVO.setSEQ(this.getFlowSeq());
				newFlowVO.setCOMPLAIN_LIST_ID(id);
				newFlowVO.setEMP_ID(editor1);
				newFlowVO.setSTATUS("W"); //W：中途、E：結案、BO：退回上一級、BF：退回第一級、R：抽回、RV：取消結案
				dam.create(newFlowVO);

				this.sendRtnObject(null);
			} else {
				throw new APException("系統發生錯誤請洽系統管理員");
			}
		} else {
			throw new APException("無案件可取消結案");
		}
	}

	//取得客戶最近一筆客訴案件所有相關資訊
	public void getHisComInfo(Object body, IPrimitiveMap header) throws JBranchException {
		
		CRM990InputVO inputVO = (CRM990InputVO) body;
		CRM990OutputVO outputVO = new CRM990OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();

		if (StringUtils.isNotBlank(inputVO.getCust_id())) {
			
			sb.append("SELECT REP.REPORT_NAME, REP.REPORT, COM.* ");
			sb.append("FROM (");
			sb.append("  SELECT * ");
			sb.append("  FROM ( ");
			sb.append("    SELECT * ");
			sb.append("    FROM TBCRM_CUST_COMPLAIN ");
			sb.append("    WHERE HANDLE_STEP <> 'D' ");
			sb.append("    AND CUST_ID = :cust_id ");

			if (StringUtils.isNotBlank(inputVO.getComplain_list_id())) {
				sb.append("    AND COMPLAIN_LIST_ID <> :complain_list_id ");
				queryCondition.setObject("complain_list_id", inputVO.getComplain_list_id());
			}
			sb.append("    ORDER BY CREATETIME DESC ");
			sb.append("  ) ");
			sb.append("  WHERE ROWNUM = 1 ");
			sb.append(") COM ");
			sb.append("LEFT JOIN TBCRM_CUST_COMPLAIN_REPORT REP ON COM.COMPLAIN_LIST_ID = REP.COMPLAIN_LIST_ID ");

			queryCondition.setObject("cust_id", inputVO.getCust_id());
			
			queryCondition.setQueryString(sb.toString());
			
			resultList = dam.exeQuery(queryCondition);
		}

		outputVO.setResultList(resultList);
		
		this.sendRtnObject(outputVO);
	}

	//計算客訴管理(處/處副以下)待覆核案件數
	public void reviewCount(Object body, IPrimitiveMap header) throws JBranchException {
		
		CRM990OutputVO outputVO = new CRM990OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		
		sb.append("SELECT FLOW.EMP_ID, COUNT(*) AS COUNT ");
		sb.append("FROM ( ");
		sb.append("  SELECT * ");
		sb.append("  FROM TBCRM_CUST_COMPLAIN ");
		sb.append("  WHERE HANDLE_STEP = '1' ");
		sb.append("  OR HANDLE_STEP = '2' ");
		sb.append("  OR HANDLE_STEP = '3' ");
		sb.append("  OR HANDLE_STEP = '4'");
		sb.append(") COM ");
		sb.append("LEFT JOIN ( ");
		sb.append("  SELECT * ");
		sb.append("  FROM ( ");
		sb.append("    SELECT COMPLAIN_LIST_ID, EMP_ID, ROW_NUMBER() OVER (PARTITION BY COMPLAIN_LIST_ID ORDER BY CREATETIME DESC) AS SORT ");
		sb.append("    FROM TBCRM_CUST_COMPLAIN_FLOW ");
		sb.append("    WHERE NEXT_EMP_ID IS NULL ");
		sb.append("  ) WHERE SORT = 1 ");
		sb.append(") FLOW ON COM.COMPLAIN_LIST_ID = FLOW.COMPLAIN_LIST_ID ");
		sb.append("WHERE FLOW.EMP_ID = :emp_id GROUP BY FLOW.EMP_ID ");

		queryCondition.setObject("emp_id", (String) getCommonVariable(SystemVariableConsts.LOGINID));
		
		queryCondition.setQueryString(sb.toString());
		
		outputVO.setResultList(dam.exeQuery(queryCondition));

		this.sendRtnObject(outputVO);
	}

	//檢視招攬/事件報告書
	public void getReportView(Object body, IPrimitiveMap header) throws Exception {
		
		CRM990InputVO inputVO = (CRM990InputVO) body;
		CRM990OutputVO outputVO = new CRM990OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		StringBuffer sb = new StringBuffer();
		sb.append("SELECT REPORT_NAME, REPORT FROM TBCRM_CUST_COMPLAIN_REPORT WHERE COMPLAIN_LIST_ID = :complain_list_id ");
		queryCondition.setObject("complain_list_id", inputVO.getComplain_list_id());
		queryCondition.setQueryString(sb.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);

		String filePath = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
		String fileName = (String) list.get(0).get("REPORT_NAME");
		int index = fileName.lastIndexOf(".");
		String data_name = fileName.substring(index);

		String uuid = UUID.randomUUID().toString() + data_name;
		Blob blob = (Blob) list.get(0).get("REPORT");
		int blobLength = (int) blob.length();
		byte[] blobAsBytes = blob.getBytes(1, blobLength);

		File targetFile = new File(filePath, uuid);
		FileOutputStream fos = new FileOutputStream(targetFile);
		fos.write(blobAsBytes);
		fos.close();

		outputVO.setPdfUrl("temp//" + uuid);
		this.sendRtnObject(outputVO);
	}
}