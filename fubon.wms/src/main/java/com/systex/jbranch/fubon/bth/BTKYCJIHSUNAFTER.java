package com.systex.jbranch.fubon.bth;

import static org.apache.commons.lang.StringUtils.isNotBlank;

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
 * 偲偲 2023/03/27 敬請協助產出業管系統4/2 原日盛高齡、弱勢客戶承作至C3、C4客戶名單
 * 
 *
 **/

@Repository("btkycjihsunafter")
@Scope("prototype")
public class BTKYCJIHSUNAFTER extends BizLogic {

	@Autowired
	private CBSService cbsService;
	@Autowired
	private DataAccessManager dam = null;


	private List getMainSqlResult() throws DAOException, JBranchException {
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT MHIS.CUST_ID,   ");
		sql.append("MAST.BIRTH_DATE BIRTH_DATE,   ");
		sql.append("CUMI.EDUCATION_CODE, ");
		sql.append("CASE CUMI.EDUCATION_CODE ");
		sql.append("WHEN '01' THEN '博士' ");
		sql.append("WHEN '02' THEN '碩士' ");
		sql.append("WHEN '03' THEN '大學' ");
		sql.append("WHEN '04' THEN '大專' ");
		sql.append("WHEN '05' THEN '高中職' ");
		sql.append("WHEN '06' THEN '國中以下' ");
		sql.append("WHEN '07' THEN '不識字' ");
		sql.append("WHEN '08' THEN '其他' ");
		sql.append("ELSE '其他' END EDUCATION_CHINESE, ");
		sql.append("CUMI.ANNOTATION_FLAG_12 SICK_TYPE,  ");
		sql.append("CASE CUMI.ANNOTATION_FLAG_12  ");
		sql.append("WHEN 'Y' THEN '2'  ");
		sql.append("ELSE '1' END  ");
		sql.append("AS CHANGE_SICK_TYPE,  ");
		sql.append("REJM.CIF_NO AS ISPRO,  ");
		sql.append("SUBSTR(REJM.MEMO,25,8) PRO_DATE,  ");
		sql.append("SUBSTR(REJM.MEMO,17,8) DELETE_DATE,  ");
		sql.append("SUBSTR(CUMI.RELIGIOUS_REF,81,8) DEGRADE_DATE,  ");
		sql.append("MHIS.CUST_RISK_AFR,   ");
		sql.append("MHIS.EXPIRY_DATE,  ");
		sql.append("MHIS.SIGNOFF_DATE,  ");
		sql.append("MHIS.CREATETIME, ");
		sql.append("MHIS.SEQ ");
		sql.append("FROM TBKYC_INVESTOREXAM_M_HIS MHIS  ");
		sql.append("LEFT JOIN TBCRM_CUST_MAST MAST ON MAST.CUST_ID = MHIS.CUST_ID ");
		sql.append("LEFT JOIN ODS_BANCS.MICM@ODSTOWMS MICM ON MHIS.CUST_ID = MICM.ID_NO ");
		sql.append("LEFT JOIN ODS_BANCS.CUMI@ODSTOWMS CUMI ON CUMI.CUST_NO = MICM.CUSTOMER_NO ");
		sql.append("LEFT JOIN ODS_BANCS.REJM@ODSTOWMS REJM ON REJM.CIF_NO = MICM.CUSTOMER_NO ");
		sql.append("AND REJM.REASON_CODE ='98' ");
		sql.append("AND REJM.INCIDENT_TYPE ='0002' ");
		sql.append("WHERE MHIS.STATUS = '03'  ");
		sql.append("and MHIS.SEQ like 'JSKYC%'  ");
		sql.append("and MHIS.SEQ not like 'JSKYCREC%'  ");
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
		Boolean degrade_end = (degrade_date == null || degrade_date.before(sdf.parse("20230402"))) ? true : false;
		

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
     	Boolean degrade_end = (degrade_date == null || degrade_date.before(sdf.parse("20230402"))) ? true : false;
		

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
	
	private int getAgeByBirth(Date birthday) throws ParseException {
		//指定2023.04.02
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		// Calendar：日歷
		/* 從Calendar對象中或得一個Date對象 */
		Calendar cal = Calendar.getInstance();
		cal.setTime(sdf.parse("20230402"));
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
	 * 是否為專投 日期檢查  日期固定在20230402
	 * 
	 * 
	 * @throws JBranchException
	 * @throws ParseException
	 * @throws DAOException
	 **/
	private boolean isTX(String DUE_END_DATE, String DELETE_END_DATE) throws DAOException, ParseException, JBranchException {
		return (StringUtils.isBlank(DUE_END_DATE) || Integer.parseInt(DUE_END_DATE) >= 20230402) && (StringUtils.isBlank(DELETE_END_DATE) || Integer.parseInt(DELETE_END_DATE) > 20230402);
	}
	
	/**
	 * 
	 */
	public void execute(Object body, IPrimitiveMap<?> header) throws Exception {
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> educationMap = xmlInfo.doGetVariable("KYC.EDUCATION", FormatHelper.FORMAT_3); // 教育程度
		Map<String, String> sickTypeMap = xmlInfo.doGetVariable("KYC.HEALTH_FLAG", FormatHelper.FORMAT_3); // 重大傷病註記
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMdd");
		// 取得檢核
		List<Map<String, Object>> checklist = getMainSqlResult();

		// 逐一檢核
		for (Map cust : checklist) {
			Map result = null;
			Integer age = 0;
			Boolean isTX = false;
			if (StringUtils.isNotBlank(checkIsNull(cust, "BIRTH_DATE"))) {
				age = getAgeByBirth(sdf.parse(checkIsNull(cust, "BIRTH_DATE")));
			} else {
				age = 100;
			}
			cust.put("AGE", age);
			String sicktype = checkIsNull(cust, "CHANGE_SICK_TYPE");
			String eduction = dealEducation(checkIsNull(cust, "EDUCATION_CODE"));
			String cValue = checkIsNull(cust, "CUST_RISK_AFR");
			String degrade = "N";
			if(StringUtils.isNotBlank(checkIsNull(cust, "DEGRADE_DATE"))
					&& ("2023".equals(checkIsNull(cust, "DEGRADE_DATE").subSequence(0, 4)) || "2024".equals(checkIsNull(cust, "DEGRADE_DATE").subSequence(0, 4)) )) {
				degrade = "Y";
			} else {
				cust.put("DEGRADE_DATE", null);
			}
			String custID = checkIsNull(cust, "CUST_ID");
			
			
			String dueDate = checkIsNull(cust, "PRO_DATE");
			String deleteDate = checkIsNull(cust, "DELETE_DATE");
			
			if (StringUtils.isNotBlank(checkIsNull(cust, "ISPRO"))) {
				isTX = isTX(dueDate, deleteDate);
			}
			Date degrade_date = StringUtils.isBlank(checkIsNull(cust, "DEGRADE_DATE")) ? null : sdf2.parse(checkIsNull(cust, "DEGRADE_DATE"));
			Date cust_pro_date = StringUtils.isBlank(checkIsNull(cust, "PRO_DATE")) ? null : sdf2.parse(checkIsNull(cust, "PRO_DATE"));
			if (isTX) {
				result = txDeclineLevel(age, sicktype, eduction, cValue, degrade, custID, degrade_date, cust_pro_date);
			} else {
				result = declineLevel(age, sicktype, eduction, cValue, degrade, custID, degrade_date);
			}
			cust.put("CHECKCVALUE", result.get("cValue"));
			cust.put("CHECKCVALUEDATE", result.get("expiry_date"));
		}

		// 寫入CSV
		List listCSV = new ArrayList();
		for (Map<String, Object> map : checklist) {
			if (!cbsService.isNaturalPerson(cbsService.getCBSIDCode(checkIsNull(map, "CUST_ID")))) {
				continue;
			}
			String[] records = new String[16];
			int i = 0;
		
				records[i] = "=\"" + checkIsNull(map, "CUST_ID") + "\"";
				records[++i] = "=\"" + checkIsNull(map, "BIRTH_DATE") + "\"";
				records[++i] = "=\"" + checkIsNull(map, "AGE") + "\"";
				records[++i] = "=\"" + checkIsNull(map, "EDUCATION_CODE") + "\"";
				records[++i] = "=\"" + checkIsNull(map, "EDUCATION_CHINESE") + "\"";
				records[++i] = "=\"" + checkIsNull(map, "SICK_TYPE") + "\"";
				records[++i] = "=\"" + checkIsNull(map, "PRO_DATE") + "\"";
				records[++i] = "=\"" + checkIsNull(map, "DELETE_DATE") + "\"";
				records[++i] = "=\"" + checkIsNull(map, "DEGRADE_DATE") + "\"";
				records[++i] = "=\"" + checkIsNull(map, "CUST_RISK_AFR") + "\"";
				records[++i] = "=\"" + checkIsNull(map, "CREATETIME") + "\"";
				records[++i] = "=\"" + checkIsNull(map, "EXPIRY_DATE") + "\"";
				records[++i] = "=\"" + checkIsNull(map, "CHECKCVALUE") + "\"";

				listCSV.add(records);

		}
		// header
		String[] csvHeader = new String[13];
		int j = 0;
		csvHeader[j] = "客戶ID";
		csvHeader[++j] = "出生年月日";
		csvHeader[++j] = "年齡";
		csvHeader[++j] = "學歷代碼";
		csvHeader[++j] = "學歷名稱";
		csvHeader[++j] = "是否重大傷病註記";
		csvHeader[++j] = "專投到期日";
		csvHeader[++j] = "專投註銷日";
		csvHeader[++j] = "免降等到期日";
		csvHeader[++j] = "KYC值";
		csvHeader[++j] = "KYC承作日期";
		csvHeader[++j] = "KYC到期日";
		csvHeader[++j] = "KYC依本行規則降等後結果(比對用，非實際將客戶降等)";

		CSVUtil csv = new CSVUtil();
		csv.setHeader(csvHeader);
		csv.addRecordList(listCSV);
		csv.setFileName("JIHSUN_KYC_TRACELIST");
		csv.generateCSV();

	}
	
	private String dealEducation(String education) {
    	//主機學歷若收電文【7不識字】或【8其他】，問卷帶出時是不是顯示未填(空值)，而非【年滿20歲不識字】
    	//20200519_CBS_麗文_KYC_教育程度問題 >> 改成給"7"(其他)
    	
    	//處理字串數
    	if(isNotBlank(education)){
    		education =  String.valueOf(Integer.parseInt(education));   		
    		if(education.matches("7|8")){
    			return "7";
    		} else {
    			return education;
    		}
    		
    	} else {
    		return "";
    	}

		
	}
}
