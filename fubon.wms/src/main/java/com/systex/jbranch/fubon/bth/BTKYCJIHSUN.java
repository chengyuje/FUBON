package com.systex.jbranch.fubon.bth;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

import com.systex.jbranch.app.common.fps.table.TBKYC_COOLING_PERIODVO;
import com.systex.jbranch.fubon.commons.cbs.CBSService;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.CSVUtil;
import com.systex.jbranch.platform.server.bizLogic.BizLogic;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * 
 * #0950 降等邏輯再檢核 2022/06/27 Sam Tu 相同客戶但套用日盛KYC的客戶名單 再套入降等邏輯看是否有正確降等
 * 日盛降等邏輯轉換錯誤名單: execute >> KYC_C4C3_FS.csv TBKYC_INVESTOREXAM_M_JS的C值與日盛原始C值
 * 轉換有誤的名單: checkCvalue >> KYC_JS_CVALUE_CHECK.csv TBKYC_INVESTOREXAM_M_JS中
 * SIGNOFF_ID or SIGNOFF_DATE為空的名單: printSignNullList >>
 * JIHSUN_KYC_SIGN_NULL.csv 2022.11.10 配合法規將相關降等邏輯年齡20歲調整為18歲
 * 
 *
 **/

@Repository("btkycjihsun")
@Scope("prototype")
public class BTKYCJIHSUN extends BizLogic {

	@Autowired
	private CBSService cbsService;
	@Autowired
	private DataAccessManager dam = null;

	/**
	 * 日盛降等邏輯轉換錯誤名單KYC_C4C3_FS.csv
	 * 20221229 調整查詢語法，CSV欄位名稱
	 * @param body
	 * @param header
	 * @throws Exception
	 */
	public void execute(Object body, IPrimitiveMap<?> header) throws Exception {
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> educationMap = xmlInfo.doGetVariable("KYC.EDUCATION", FormatHelper.FORMAT_3); // 教育程度
		Map<String, String> sickTypeMap = xmlInfo.doGetVariable("KYC.HEALTH_FLAG", FormatHelper.FORMAT_3); // 重大傷病註記
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// 取得檢核
		List<Map<String, Object>> checklist = getMainSqlResult();

		// 逐一檢核
		for (Map cust : checklist) {
			Map result = null;
			Integer age = 0;
			Boolean isTX = false;
			if (StringUtils.isNotBlank(checkIsNull(cust, "FUBON_BIRTH_DATE"))) {
				age = getAgeByBirth(sdf.parse(checkIsNull(cust, "FUBON_BIRTH_DATE")));
			} else {
				age = 100;
			}
			String sicktype = checkIsNull(cust, "CHANGE_SICK_TYPE");
			String eduction = checkIsNull(cust, "CUST_EDUCTION_AFTER");
			String cValue = checkIsNull(cust, "CUST_RISK_AFR");
			String degrade = checkIsNull(cust, "DEGRADE");
			String custID = checkIsNull(cust, "CUST_ID");
			
			
			String dueDate = checkIsNull(cust, "PRO_DATE");
			String deleteDate = checkIsNull(cust, "DELETE_DATE");
			if(StringUtils.isNotBlank(dueDate)) {
				dueDate = dueDate.substring(0,4) + dueDate.substring(5,7) + dueDate.substring(8,10);
			}
			if(StringUtils.isNotBlank(deleteDate)) {
				deleteDate = deleteDate.substring(0,4) + deleteDate.substring(5,7) + deleteDate.substring(8,10);
			}
			
			if (StringUtils.isNotBlank(checkIsNull(cust, "ISPRO"))) {
				isTX = isTX(dueDate, deleteDate);
			}
			Date degrade_date = StringUtils.isBlank(checkIsNull(cust, "DEGRADE_DATE")) ? null : sdf.parse(checkIsNull(cust, "DEGRADE_DATE"));
			Date cust_pro_date = StringUtils.isBlank(checkIsNull(cust, "PRO_DATE")) ? null : sdf.parse(checkIsNull(cust, "PRO_DATE"));
			if (isTX || StringUtils.isNotBlank(checkIsNull(cust, "CIF_REJM"))) {
				result = txDeclineLevel(age, sicktype, eduction, cValue, degrade, custID, degrade_date, cust_pro_date);
			} else {
				result = declineLevel(age, sicktype, eduction, cValue, degrade, custID, degrade_date);
			}
			cust.put("CHECKCVALUE", result.get("cValue"));
			cust.put("CHECKCVALUEDATE", result.get("expiry_date"));
		}

		
		// 清空TBKYC_COOLING_PERIOD資料
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("DELETE TBKYC_COOLING_PERIOD  WHERE SEQ LIKE 'JS%'");
		queryCondition.setQueryString(sql.toString());
		dam.exeUpdate(queryCondition);

		// 寫入CSV
		List listCSV = new ArrayList();
		for (Map<String, Object> map : checklist) {
			if (!cbsService.isNaturalPerson(cbsService.getCBSIDCode(checkIsNull(map, "CUST_ID")))) {
				continue;
			}
			String[] records = new String[16];
			int i = 0;
			if (!StringUtils.equals(checkIsNull(map, "CHECKCVALUE"), checkIsNull(map, "CUST_RISK_AFR")) || (StringUtils.isNotBlank(checkIsNull(map, "CHECKCVALUEDATE")) && !StringUtils.equals(checkIsNull(map, "EXPIRY_DATE"), checkIsNull(map, "CHECKCVALUEDATE")))) {
				records[i] = "=\"" + checkIsNull(map, "CUST_ID") + "\"";
				records[++i] = "=\"" + checkIsNull(map, "FUBON_BIRTH_DATE") + "\"";
				records[++i] = "=\"" + checkIsNull(map, "JIHSUN_BIRTH_DATE") + "\"";
				records[++i] = "=\"" + educationMap.get(checkIsNull(map, "CUST_EDUCTION_AFTER")) + "\"";
				records[++i] = "=\"" + checkIsNull(map, "SICK_TYPE") + "\"";
				records[++i] = "=\"" + sickTypeMap.get(checkIsNull(map, "CHANGE_SICK_TYPE")) + "\"";
				records[++i] = "=\"" + checkIsNull(map, "CIF_REJM") + "\"";
				records[++i] = StringUtils.isBlank(checkIsNull(map, "ISPRO")) ? "N" : "Y";
				records[++i] = "=\"" + checkIsNull(map, "DELETE_DATE") + "\"";
				records[++i] = "=\"" + checkIsNull(map, "PRO_DATE") + "\"";
				records[++i] = "=\"" + checkIsNull(map, "DEGRADE") + "\"";
				records[++i] = "=\"" + checkIsNull(map, "DEGRADE_DATE") + "\"";
				records[++i] = "=\"" + checkIsNull(map, "CUST_RISK_AFR") + "\"";
				records[++i] = "=\"" + checkIsNull(map, "EXPIRY_DATE") + "\"";
				records[++i] = "=\"" + checkIsNull(map, "CHECKCVALUE") + "\"";
				if (StringUtils.isBlank(checkIsNull(map, "CHECKCVALUEDATE"))) {
					records[++i] = "=\"" + checkIsNull(map, "EXPIRY_DATE") + "\"";
				} else {
					records[++i] = "=\"" + checkIsNull(map, "CHECKCVALUEDATE") + "\"";
				}
				listCSV.add(records);

				TBKYC_COOLING_PERIODVO cool = new TBKYC_COOLING_PERIODVO();
				cool.setSEQ(checkIsNull(map, "SEQ"));
				cool.setMEMO("日盛弱勢戶");
				dam.create(cool);
			}

		}
		// header
		String[] csvHeader = new String[16];
		int j = 0;
		csvHeader[j] = "客戶ID";
		csvHeader[++j] = "出生年月日_Fubon";
		csvHeader[++j] = "出生年月日_JSB";
		csvHeader[++j] = "學歷";
		csvHeader[++j] = "轉換前重大傷病註記";
		csvHeader[++j] = "轉換後重大傷病註記";
		csvHeader[++j] = "CIF_JSB專投註記";
		csvHeader[++j] = "REJM專投註記";
		csvHeader[++j] = "專投註銷日";
		csvHeader[++j] = "專投到期日";
		csvHeader[++j] = "免降註記";
		csvHeader[++j] = "免降到期日";
		csvHeader[++j] = "FS_KYC值";
		csvHeader[++j] = "FS_KYC到期日";
		csvHeader[++j] = "check_C值";
		csvHeader[++j] = "check_c值到期日";

		CSVUtil csv = new CSVUtil();
		csv.setHeader(csvHeader);
		csv.addRecordList(listCSV);
		csv.setFileName("KYC_C4C3_FS");
		csv.generateCSV();

	}

	/**
	 * 產檔: TBKYC_INVESTOREXAM_M_JS的C值與日盛原始C值 轉換有誤的名單
	 * 
	 * @param body
	 * @param header
	 * @throws Exception
	 */
	public void checkCvalue(Object body, IPrimitiveMap<?> header) throws Exception {
		// 取得檢核清單
		List<Map<String, Object>> checklist = getCvalueCheckList();
		// 寫入CSV
		List listCSV = new ArrayList();
		// 逐一檢核
		for (Map cust : checklist) {
			if (checkTransferCvalueError(cust)) {
				String[] records = new String[3];
				records[0] = "=\"" + checkIsNull(cust, "CUST_ID") + "\"";
				records[1] = "=\"" + checkIsNull(cust, "TRANFER_CVALUE") + "\"";
				records[2] = "=\"" + checkIsNull(cust, "ORI_CVALUE") + "\"";
				listCSV.add(records);
			}

		}
		// header
		String[] csvHeader = new String[13];
		int j = 0;
		csvHeader[j] = "客戶ID";
		csvHeader[++j] = "TBKYC_INVESTOREXAM_M_JS的C值";
		csvHeader[++j] = "日盛原始C值";

		CSVUtil csv = new CSVUtil();
		csv.setHeader(csvHeader);
		csv.addRecordList(listCSV);
		csv.setFileName("JIHSUN_KYC_CVALUE_CHECK");
		csv.generateCSV();
	}

	/**
	 * 產檔: TBKYC_INVESTOREXAM_M_JS中 SIGNOFF_ID or SIGNOFF_DATE為空的名單
	 * 
	 * @param body
	 * @param header
	 * @throws Exception
	 */
	public void printSignNullList(Object body, IPrimitiveMap<?> header) throws Exception {
		// 取得檢核清單
		List<Map<String, Object>> list = getSignNullList();
		// 寫入CSV
		List listCSV = new ArrayList();
		// 逐一檢核
		for (Map cust : list) {
			String[] records = new String[3];
			records[0] = "=\"" + checkIsNull(cust, "CUST_ID") + "\"";
			records[1] = "=\"" + checkIsNull(cust, "SIGNOFF_ID") + "\"";
			records[2] = "=\"" + checkIsNull(cust, "SIGNOFF_DATE") + "\"";
			listCSV.add(records);
		}
		// header
		String[] csvHeader = new String[13];
		int j = 0;
		csvHeader[j] = "客戶ID";
		csvHeader[++j] = "SIGNOFF_ID";
		csvHeader[++j] = "SIGNOFF_DATE";

		CSVUtil csv = new CSVUtil();
		csv.setHeader(csvHeader);
		csv.addRecordList(listCSV);
		csv.setFileName("JIHSUN_KYC_SIGN_NULL");
		csv.generateCSV();
	}

	private List<Map<String, Object>> getSignNullList() throws DAOException, JBranchException {
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append(" select CUST_ID, SIGNOFF_ID, SIGNOFF_DATE from TBKYC_INVESTOREXAM_M_JS where SIGNOFF_ID is null or SIGNOFF_DATE is null ");
		queryCondition.setQueryString(sql.toString());
		return dam.exeQuery(queryCondition);
	}

	private boolean checkTransferCvalueError(Map cust) {
		String transferCvalue = checkIsNull(cust, "TRANFER_CVALUE");
		String oriCvalue = checkIsNull(cust, "ORI_CVALUE");

		if (StringUtils.isBlank(transferCvalue) || StringUtils.isBlank(oriCvalue)) {
			return true;
		} else
			switch (transferCvalue) {
			case "C1":
				if (!(oriCvalue.equals("1") || oriCvalue.equals("2"))) {
					return true;
				}
				break;
			case "C2":
				if (!oriCvalue.equals("3")) {
					return true;
				}
				break;
			case "C3":
				if (!oriCvalue.equals("4")) {
					return true;
				}
				break;
			case "C4":
				if (!oriCvalue.equals("5")) {
					return true;
				}
				break;
			default:
				return false;
			}
		return false;
	}

	private List<Map<String, Object>> getCvalueCheckList() throws DAOException, JBranchException {
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append(" select M.CUST_ID, M.CUST_RISK_AFR as TRANFER_CVALUE, ORI.RISK_TYPE as ORI_CVALUE  from TBKYC_INVESTOREXAM_M_JS M ");
		sql.append(" left join TBKYC_JS_CVALUE_CHECK ORI on ORI.CUST_ID = M.CUST_ID  ");
		sql.append(" order by M.CUST_ID  ");
		queryCondition.setQueryString(sql.toString());
		return dam.exeQuery(queryCondition);
	}

	private List getMainSqlResult() throws DAOException, JBranchException {
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT MHIS.CUST_ID,  ");
		sql.append("MAST.BIRTH_DATE FUBON_BIRTH_DATE,  ");
		sql.append("DHIS.CUST_EDUCTION_AFTER,  ");
		sql.append("CUMI.SICK_TYPE, ");
		sql.append("CASE CUMI.SICK_TYPE ");
		sql.append("WHEN 'Y' THEN '2' ");
		sql.append("ELSE '1' END ");
		sql.append("AS CHANGE_SICK_TYPE, ");
		sql.append("REJM.CUST_ID AS ISPRO, ");
		sql.append("REJM.DELETE_DATE, ");
		sql.append("REJM.PRO_DATE, ");
		sql.append("CUMI.DEGRADE, ");
		sql.append("CUMI.DEGRADE_DATE, ");
		sql.append("MHIS.CUST_RISK_AFR,  ");
		sql.append("MHIS.EXPIRY_DATE, ");
		sql.append("CIF.REJM AS CIF_REJM, ");
		sql.append("CIF.BIRTH_DATE AS JIHSUN_BIRTH_DATE, ");
		sql.append("MHIS.SEQ AS SEQ ");
		sql.append("FROM TBKYC_INVESTOREXAM_M_HIS MHIS ");
		sql.append("LEFT JOIN TBKYC_INVESTOREXAM_D_HIS DHIS ON DHIS.SEQ = MHIS.SEQ ");
		sql.append("LEFT JOIN TBCRM_CUST_MAST MAST ON MAST.CUST_ID = MHIS.CUST_ID ");
		sql.append("LEFT JOIN CUMI_FOR_FS CUMI ON CUMI.CUST_ID = MHIS.CUST_ID ");
		sql.append("LEFT JOIN REJM_FOR_FS REJM ON REJM.CUST_ID = MHIS.CUST_ID ");
		sql.append("LEFT JOIN CIF_JSB CIF ON CIF.CUST_ID = MHIS.CUST_ID ");
		sql.append("WHERE MHIS.STATUS = '03' ");
		sql.append("and MHIS.SEQ like 'JSKYC%' ");
		sql.append("and MHIS.SEQ not like 'JSKYCREC%' ");
		sql.append("and MHIS.CUST_RISK_AFR IN('C3','C4') ");
		queryCondition.setQueryString(sql.toString());
		return dam.exeQuery(queryCondition);
	}

	/** 專業投資人降等判斷 **/
	private Map txDeclineLevel(Integer age, String sicktype, String eduction, String cValue, String degrade, String custID, Date degrade_date, Date cust_pro_date) throws Exception {
		Map<String, String> map = new HashMap<>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

		cValue = cValue == null ? "" : cValue;
		eduction = eduction == null ? "" : eduction;
		age = age == null ? 0 : age;

		// 初始值為原C值
		map.put("cValue", cValue);

		// 判斷是否可做kyc
		chkCanKyc(age, sicktype, eduction, custID);

		// 18歲以下且學歷為國高中，最高C2
		if (age < 18 && eduction.matches("[6]")) {
			map.put("cValue", cValue.replaceAll("([3-9]|(\\d\\d+))$", "2"));

			// try {logInfo(custID , "txDeclineLevel " , "20歲以下且學歷為國高中，最高C2:" +
			// map.get("cValue"));} catch(Exception e) {}

			return map;
		}

		// 判斷免降等註記是否有效
		Boolean degrade_end = (degrade_date == null || degrade_date.before(new Date())) ? true : false;

		// try {logInfo(custID , "txDeclineLevel " , "degrade_end:" +
		// ObjectUtils.toString(degrade_end));} catch(Exception e) {}

		// 弱勢客戶
		if (chkNoSpecialSigningReduceLevel(age, sicktype, eduction)) {
			if (StringUtils.equals("Y", degrade) && !degrade_end) {
				// 有免降等註記且未到期 ==> 不降等；若C值為C4，則KYC到期日=免降等到期日
				if ("C4".equals(cValue)) {
					map.put("expiry_date", sdf.format(degrade_date));

					// try {logInfo(custID , "txDeclineLevel " , "expiry_date:"
					// + sdf.format(degrade_date));} catch(Exception e) {}
				}
			} else {
				// 沒有免降等註記 ==> 最高C3
				map.put("cValue", cValue.replaceAll("([4-9]|(\\d\\d+))$", "3"));

				// try {logInfo(custID , "txDeclineLevel " , "cValue:" +
				// map.get("cValue"));} catch(Exception e) {}
			}
		} else {
			// 非弱勢客戶不降等
		}

		// try {logInfo(custID , "txDeclineLevel " , "cValue:" +
		// map.get("cValue"));} catch(Exception e) {}

		return map;
	}

	/** 非專業投資人降等判斷 **/
	private Map declineLevel(Integer age, String sicktype, String eduction, String cValue, String degrade, String custID, Date degrade_date) throws Exception {
		Map<String, String> map = new HashMap<>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

		cValue = cValue == null ? "" : cValue;
		eduction = eduction == null ? "" : eduction;
		age = age == null ? 0 : age;

		// 初始值為原C值
		map.put("cValue", cValue);

		// 判斷是否可做kyc
		chkCanKyc(age, sicktype, eduction, custID);

		// 18歲以下且學歷為國高中，最高C2
		if (age < 18 && eduction.matches("[6]")) {
			map.put("cValue", cValue.replaceAll("([3-9]|(\\d\\d+))$", "2"));

			// try {logInfo(custID , "declineLevel " , "20歲以下且學歷為國高中，最高C2:" +
			// map.get("cValue"));} catch(Exception e) {}

			return map;
		}

		// 判斷免降等註記是否有效
		Boolean degrade_end = (degrade_date == null || degrade_date.before(new Date())) ? true : false;

		// try {logInfo(custID , "declineLevel " , "degrade_end:" +
		// ObjectUtils.toString(degrade_end));} catch(Exception e) {}

		// 弱勢客戶
		if (chkNoSpecialSigningReduceLevel(age, sicktype, eduction)) {
			if (StringUtils.equals("Y", degrade) && !degrade_end) {
				// 有免降等註記且未到期 ==> 不降等；若C值為C3 or C4，則KYC到期日=免降等到期日
				if (cValue.matches("C3|C4")) {
					map.put("expiry_date", sdf.format(degrade_date));

					// try {logInfo(custID , "declineLevel " , "expiry_date:" +
					// sdf.format(degrade_date));} catch(Exception e) {}
				}
			} else {
				// 沒有免降等註記 ==> 最高C2
				map.put("cValue", cValue.replaceAll("([3-9]|(\\d\\d+))$", "2"));

				// try {logInfo(custID , "declineLevel " , "沒有免降等註記 ==> 最高C2:" +
				// map.get("cValue"));} catch(Exception e) {}
			}
		} else {
			// 非弱勢客戶不降等
		}

		// try {logInfo(custID , "declineLevel " , "cValue:" +
		// map.get("cValue"));} catch(Exception e) {}

		return map;
	}

	public void chkCanKyc(Integer age, String sicktype, String eduction, String custID) throws Exception {
		// 無法辦理KYC
		if ("3".equals(sicktype)) {
			throw new Exception("有全民健康保險重大傷病證明，而且會影響本人對投資商品及其風險之理解，無法承做問卷");
		} else if (isNaturalPerson(custID) && age >= 18 && "8".equals(eduction)) {
			throw new Exception("年齡18歲(含)以上不識字，無法承做問卷");
		}
	}

	private boolean isNaturalPerson(String custId) {
		return StringUtils.length(custId) >= 10;
	}

	public boolean chkNoSpecialSigningReduceLevel(Integer age, String sicktype, String eduction) {
		boolean isReduceLevel = false;

		// 年齡65歲(含)以上
		isReduceLevel = age >= 65;

		// try{logInfo("chkNoSpecialSigningReduceLevel" , "age >= 70 " ,
		// "age >= 70:" + ObjectUtils.toString(isReduceLevel));} catch(Exception
		// e) {}

		// 年齡18歲(含)以上，教育程度圍國中(含以下)
		isReduceLevel = isReduceLevel || (age >= 18 && eduction.matches("[6]"));

		// try{logInfo("chkNoSpecialSigningReduceLevel" ,
		// "年齡20歲(含)以上，教育程度圍國中(含以下) " , "年齡20歲(含)以上，教育程度圍國中(含以下):" +
		// ObjectUtils.toString((age >= 20 && eduction.matches("[6]"))));}
		// catch(Exception e) {}

		// 有全民健康保險重大傷病證明，但不影響投資風險理解
		isReduceLevel = isReduceLevel || "2".equals(sicktype);

		// try{logInfo("chkNoSpecialSigningReduceLevel" , "重大傷病證明 " , "重大傷病證明:"
		// + ObjectUtils.toString("2".equals(sicktype)));} catch(Exception e) {}

		return isReduceLevel;
	}

	private String checkIsNull(Map map, String key) {
		if (StringUtils.isNotBlank(ObjectUtils.toString(map.get(key)))) {
			return String.valueOf(map.get(key));
		} else {
			return "";
		}
	}

	private int getAgeByBirth(Date birthday) {
		// Calendar：日歷
		/* 從Calendar對象中或得一個Date對象 */
		Calendar cal = Calendar.getInstance();
		/* 把出生日期放入Calendar類型的bir對象中，進行Calendar和Date類型進行轉換 */
		Calendar bir = Calendar.getInstance();
		bir.setTime(birthday);
		/* 如果生日大於當前日期，則拋出異常：出生日期不能大於當前日期 */
		if (cal.before(birthday)) {
			throw new IllegalArgumentException("The birthday is before Now,It‘s unbelievable");
		}
		/* 取出當前年月日 */
		int yearNow = cal.get(Calendar.YEAR);
		int monthNow = cal.get(Calendar.MONTH);
		int dayNow = cal.get(Calendar.DAY_OF_MONTH);
		/* 取出出生年月日 */
		int yearBirth = bir.get(Calendar.YEAR);
		int monthBirth = bir.get(Calendar.MONTH);
		int dayBirth = bir.get(Calendar.DAY_OF_MONTH);
		/* 大概年齡是當前年減去出生年 */
		int age = yearNow - yearBirth;
		/* 如果出當前月小與出生月，或者當前月等於出生月但是當前日小於出生日，那麽年齡age就減一歲 */
		if (monthNow < monthBirth || (monthNow == monthBirth && dayNow < dayBirth)) {
			age--;
		}
		return age;
	}

	/**
	 * 是否為專投 日期檢查
	 * 產日盛降等邏輯轉換錯誤名單: execute >> KYC_C4C3_FS.csv
	 * 
	 * @throws JBranchException
	 * @throws ParseException
	 * @throws DAOException
	 **/
	private boolean isTX(String DUE_END_DATE, String DELETE_END_DATE) throws DAOException, ParseException, JBranchException {

		return (StringUtils.isBlank(DUE_END_DATE) || Integer.parseInt(DUE_END_DATE) >= Integer.parseInt(cbsService.getCBSTestDate().substring(0, 8))) && (StringUtils.isBlank(DELETE_END_DATE) || Integer.parseInt(DELETE_END_DATE) > Integer.parseInt(cbsService.getCBSTestDate().substring(0, 8)));
	}
}
