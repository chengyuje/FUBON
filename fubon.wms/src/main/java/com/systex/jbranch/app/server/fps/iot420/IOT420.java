package com.systex.jbranch.app.server.fps.iot420;

import static com.systex.jbranch.fubon.jlb.DataFormat.getCustIdMaskForHighRisk;
import static java.lang.String.format;
import static org.apache.commons.collections.CollectionUtils.isEmpty;
import static org.apache.commons.lang.StringUtils.defaultString;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.common.fps.table.TBCAM_CAL_SALES_TASKVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.serialnumber.SerialNumberUtil;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.CSVUtil;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

@Component("iot420")
@Scope("request")
public class IOT420 extends FubonWmsBizLogic {
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(IOT420.class);

	public void inquire(Object body, IPrimitiveMap header) throws JBranchException {
		IOT420InputVO inputVO = (IOT420InputVO) body;
		IOT420OutputVO outputVO = new IOT420OutputVO();
		List<Map<String, Object>> resultList = new ArrayList<>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		dam = this.getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		/**
		 * 案件呈現條件：
		 * 電訪狀態=2.電訪預約中、3.電訪處理中、5.電訪未成功、6.電訪疑義、7.取消電訪
		 * 且申請電訪之購買檢核編碼契撤案件欄位為空白；若契撤案件欄位非空白，則不用呈現
		 * */
		sb.append(" SELECT S05.REVIEW_STATUS, S05.PREMATCH_SEQ, S05.STATUS, S01.CASE_ID, ");
		sb.append(" TO_CHAR(S01.APPLY_DATE, 'YYYY/MM/DD') AS APPLY_DATE, ");
		sb.append(" S01.INSPRD_ID, S01.BRANCH_NBR, S01.RECRUIT_ID, S05.CALL_PERSON, ");
		sb.append(" S01.PROPOSER_NAME, S01.CUST_ID, S05.C_CALL_TYPE, NVL(S05.C_TIME2, S05.C_TIME) AS C_TIME, S05.C_TIME_MEMO, ");
		sb.append(" S01.INSURED_NAME, S01.INSURED_ID, S05.I_CALL_TYPE, NVL(S05.I_TIME2, S05.I_TIME) AS I_TIME, S05.I_TIME_MEMO, ");
		sb.append(" S01.PAYER_NAME, S01.PAYER_ID, S05.P_CALL_TYPE, NVL(S05.P_TIME2, S05.P_TIME) AS P_TIME, S05.P_TIME_MEMO, ");
		sb.append(" S01.CANCEL_CONTRACT_YN, S05.C_NEED_CALL_YN, S05.I_NEED_CALL_YN, S05.P_NEED_CALL_YN, ");
		sb.append(" S05.C_TEL_NO, S05.C_MOBILE, S05.I_TEL_NO, S05.I_MOBILE, S05.P_TEL_NO, S05.P_MOBILE ");
		sb.append(" FROM TBIOT_CALLOUT S05 ");
		sb.append(" LEFT JOIN TBIOT_PREMATCH S01 ON S01.PREMATCH_SEQ = S05.PREMATCH_SEQ ");
		
		if (inputVO.getFromIOT421() != null && inputVO.getFromIOT421() && 
			StringUtils.isNotBlank(inputVO.getAssign()) && "Y".equals(inputVO.getAssign())) {
			// 來自保險電訪作業＆是派件員
			sb.append(" WHERE S05.STATUS IN ('2', '3', '4', '5', '6', '7', '8') ");
		} else {
			sb.append(" WHERE S05.STATUS IN ('2', '3', '5', '6', '7') ");			
		}
		
		sb.append(" AND NVL(S01.CANCEL_CONTRACT_YN, 'N') = 'N' ");
		
		if (inputVO.getFromIOT421() != null && inputVO.getFromIOT421() && 
			StringUtils.isNotBlank(inputVO.getAssign()) && "N".equals(inputVO.getAssign())) {
			// 來自保險電訪作業＆非派件員
			String loginID = (String) getUserVariable(FubonSystemVariableConsts.LOGINID);
			sb.append(" AND S05.CALL_PERSON = :call_person ");
			qc.setObject("call_person", loginID);
		}
		
		if (StringUtils.isNotBlank(inputVO.getPrematch_seq())) {
			sb.append(" AND S05.PREMATCH_SEQ = :prematch_seq ");
			qc.setObject("prematch_seq", inputVO.getPrematch_seq());
		}
		
		if (StringUtils.isNotBlank(inputVO.getStatus())) {
			sb.append(" AND S05.STATUS = :status ");
			qc.setObject("status", inputVO.getStatus());
		}
		
		if (StringUtils.isNotBlank(inputVO.getCase_id())) {
			sb.append(" AND S01.CASE_ID = :case_id ");
			qc.setObject("case_id", inputVO.getCase_id());
		}
		
		if (StringUtils.isNotBlank(inputVO.getCust_id())) {
			sb.append(" AND S01.CUST_ID LIKE :cust_id ");
			qc.setObject("cust_id", inputVO.getCust_id() + "%");
		}
		
		if (StringUtils.isNotBlank(inputVO.getReview_status())) {
			if ("OK".equals(inputVO.getReview_status())) {
				sb.append(" AND S05.REVIEW_STATUS = 'OK' ");
			} else {
				sb.append(" AND NVL(S05.REVIEW_STATUS, 'NO') = 'NO' ");
			}
		}
		
		if (StringUtils.isNotBlank(inputVO.getCall_person())) {
			sb.append(" AND S05.CALL_PERSON LIKE :call_person2 ");
			qc.setObject("call_person2", inputVO.getCall_person() + "%");
		}
		
		if (inputVO.getS_apply_date() != null) {
			String s_apply_date = sdf.format(inputVO.getS_apply_date());
			sb.append(" AND TO_CHAR(S01.APPLY_DATE, 'YYYYMMDD') >= :s_apply_date ");
			qc.setObject("s_apply_date", s_apply_date);
		}
		
		if (inputVO.getE_apply_date() != null) {
			String e_apply_date = sdf.format(inputVO.getE_apply_date());
			sb.append(" AND TO_CHAR(S01.APPLY_DATE, 'YYYYMMDD') <= :e_apply_date ");
			qc.setObject("e_apply_date", e_apply_date);
		}
		
		if (StringUtils.isNotBlank(inputVO.getRecruit_id())) {
			sb.append(" AND S01.RECRUIT_ID LIKE :recruit_id ");
			qc.setObject("recruit_id", inputVO.getRecruit_id() + "%");
		}
		
		sb.append(" ORDER BY S05.PREMATCH_SEQ ");
		
		qc.setQueryString(sb.toString());
		resultList = dam.exeQuery(qc);
		
		for (Map<String, Object> map : resultList) {
			// 處理『方便聯絡時段』
			String c_time = map.get("C_TIME") == null ? null : map.get("C_TIME").toString();
			String i_time = map.get("I_TIME") == null ? null : map.get("I_TIME").toString();
			String p_time = map.get("P_TIME") == null ? null : map.get("P_TIME").toString();
			
			Map<String, String> cTimeMap = this.convertTime(c_time, "C");
			Map<String, String> iTimeMap = this.convertTime(i_time, "I");
			Map<String, String> pTimeMap = this.convertTime(p_time, "P");
			
			if (cTimeMap != null) map.putAll(cTimeMap);
			if (iTimeMap != null) map.putAll(iTimeMap);
			if (pTimeMap != null) map.putAll(pTimeMap);	
			
			// 處理要、被、繳呈現規則
			String cFlag = map.get("C_NEED_CALL_YN") == null ? "N" : map.get("C_NEED_CALL_YN").toString();
			String iFlag = map.get("I_NEED_CALL_YN") == null ? "N" : map.get("I_NEED_CALL_YN").toString();
			String pFlag = map.get("P_NEED_CALL_YN") == null ? "N" : map.get("P_NEED_CALL_YN").toString();
			
			String cust_id 	  = map.get("CUST_ID") == null ? "" : map.get("CUST_ID").toString();
			String insured_id = map.get("INSURED_ID") == null ? "" : map.get("INSURED_ID").toString();
			String payer_id   = map.get("PAYER_ID") == null ? "" : map.get("PAYER_ID").toString();
			
			String call_c_yn = "N";
			String call_i_yn = "N";
			String call_p_yn = "N";
			if (cFlag.equals("Y")) {
				call_c_yn = "Y";
			}
			if (iFlag.equals("Y") && (!insured_id.equals(cust_id))) {
				call_i_yn = "Y";
			}
			if (pFlag.equals("Y") && (!payer_id.equals(cust_id)) && (!payer_id.equals(insured_id))) {
				call_p_yn = "Y";
			}
			map.put("CALL_C_YN", call_c_yn);
			map.put("CALL_I_YN", call_i_yn);
			map.put("CALL_P_YN", call_p_yn);
		}
		
		outputVO.setResultList(resultList);
		this.sendRtnObject(outputVO);
	}
	
	private Map<String, String> convertTime(String time, String type) throws JBranchException {
		Map<String, String> map = new HashMap<>();
		if (StringUtils.isNotBlank(time) && time.length() == 4) {
			String time1 = time.substring(0, 1).equals("1") ? "Y" : "N";
			String time2 = time.substring(1, 2).equals("1") ? "Y" : "N";
			String time3 = time.substring(2, 3).equals("1") ? "Y" : "N";
			String time4 = time.substring(3).equals("1") 	? "Y" : "N";
			
			map.put(type + "_TIME1", time1);
			map.put(type + "_TIME2", time2);
			map.put(type + "_TIME3", time3);
			map.put(type + "_TIME4", time4);
			
		} else {
			map = null;
		}
		return map;
	}
	
	public void getEmpList(Object body, IPrimitiveMap header) throws JBranchException {
		IOT420OutputVO outputVO = new IOT420OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		sb.append(" SELECT PARAM_NAME FROM TBSYSPARAMETER WHERE PARAM_TYPE = 'CALLOUT.CALL_PERSON' AND PARAM_CODE = 'CALL' ");
		qc.setQueryString(sb.toString());
		List<Map<String, Object>> list = dam.exeQuery(qc);
		
		if (list != null && list.size() > 0) {
			String empList_s = list.get(0).get("PARAM_NAME").toString();
			String[] empList = empList_s.split(",");
			
			qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sb = new StringBuffer();
			sb.append(" SELECT EMP_ID, EMP_NAME FROM VWORG_BRANCH_EMP_DETAIL_INFO WHERE EMP_ID IN (:emp_id) ");
			
			qc.setObject("emp_id", empList);
			qc.setQueryString(sb.toString());
			outputVO.setResultList(dam.exeQuery(qc));
		}
		
		this.sendRtnObject(outputVO);
	}
	
	public void showAssign(Object body, IPrimitiveMap header) throws JBranchException {
		IOT420OutputVO outputVO = new IOT420OutputVO();
		String loginID = (String) getUserVariable(FubonSystemVariableConsts.LOGINID);
		Boolean showAssign = false;
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> map = xmlInfo.doGetVariable("CALLOUT.CALL_PERSON", FormatHelper.FORMAT_3);
		
		String assignList = map.get("ASSIGN");
		String[] assignArray = assignList.split(",");
		
		for (String assign : assignArray) {
			if (assign.equals(loginID)) {
				showAssign = true;
				break;
			}
		}
		
		outputVO.setShowAssign(showAssign);
		this.sendRtnObject(outputVO);
	}
	
	public void assign(Object body, IPrimitiveMap header) throws JBranchException {
		IOT420InputVO inputVO = (IOT420InputVO) body;
		String loginID = (String) getUserVariable(FubonSystemVariableConsts.LOGINID);
		String call_person = inputVO.getCall_person();
		dam = this.getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		
		sb.append(" UPDATE TBIOT_CALLOUT SET ");
		sb.append(" STATUS = '3', ");
		sb.append(" ASSIGN_PERSON = :loginID, ");
		sb.append(" CALL_PERSON = :call_person, ");
		sb.append(" VERSION = VERSION+1, ");
		sb.append(" MODIFIER = :loginID, ");
		sb.append(" LASTUPDATE = SYSDATE ");
		sb.append(" WHERE PREMATCH_SEQ IN (:prematch_seq) ");
		
		qc.setObject("loginID", loginID);
		qc.setObject("call_person", call_person);
		qc.setObject("prematch_seq", inputVO.getAssign_seq());
		qc.setQueryString(sb.toString());
		dam.exeUpdate(qc);
		
		qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sb = new StringBuffer();
		sb.append(" SELECT CUST_ID FROM TBIOT_PREMATCH WHERE PREMATCH_SEQ IN (:prematch_seq) ");
		qc.setObject("prematch_seq", inputVO.getAssign_seq());
		qc.setQueryString(sb.toString());
		List<Map<String, Object>> list = dam.exeQuery(qc);
		
		TBCAM_CAL_SALES_TASKVO vo = null;
		for (Map<String, Object> map : list) {
			// 派件完成寫入 TBCAM_CAL_SALES_TASK
			vo = new TBCAM_CAL_SALES_TASKVO();
			BigDecimal seqNo = new BigDecimal(getSEQ());
			while(checkID(seqNo)) {
				seqNo = new BigDecimal(getSEQ());
			}
			vo.setSEQ_NO(seqNo);
			vo.setEMP_ID(call_person);						// 電訪人員
			vo.setCUST_ID(map.get("CUST_ID").toString());	// 要保人ID
			vo.setTASK_DATE(new Timestamp(System.currentTimeMillis()));
			vo.setTASK_STIME("0900");
			vo.setTASK_ETIME("1800");
			vo.setTASK_TITLE("您有一筆案件需要進行電訪");
			vo.setTASK_MEMO("您有一筆案件需要進行電訪");
			vo.setTASK_SOURCE("A9");
			vo.setTASK_TYPE("03");
			vo.setTASK_STATUS("I");
			dam.create(vo);	
		}
		
		this.sendRtnObject(null);
	}
	
	public void export(Object body, IPrimitiveMap header) throws JBranchException {
		IOT420InputVO inputVO = (IOT420InputVO) body;
		SimpleDateFormat dateSdf = new SimpleDateFormat("yyyyMMdd");
		List<Map<String, Object>> list = inputVO.getResultList();
		List listCSV = new ArrayList();
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> calloutStatusMap = xmlInfo.doGetVariable("CALLOUT_STATUS", FormatHelper.FORMAT_3);
		Map<String, String> callTypeMap = xmlInfo.doGetVariable("CALLOUT.CALL_TYPE", FormatHelper.FORMAT_3);
		Map<String, String> timeMap = xmlInfo.doGetVariable("CALLOUT.TIME", FormatHelper.FORMAT_3);
		
		String[] csvHeader = {"檢視狀態", "適合度檢核編碼",	"電訪狀態", "案件編號", "要保書填寫申請日", 	"主約險種代號", 
							  "要保人姓名",	"要保人ID",	"通訊電話", "手機號碼", "要保人電訪類型",		"要保人方便連絡時段",	"備註",
							  "被保險人姓名", "被保險人ID",	"通訊電話", "手機號碼", "被保險人電訪類型", 	"被保險人方便連絡時段",	"備註",
							  "繳款人姓名", 	"繳款人ID",	"通訊電話", "手機號碼", "繳款人電訪類型", 	"繳款人方便連絡時段",	"備註",
							  "分行", "招攬人員員編", "電訪員"};
		
		String[] csvMain = {"REVIEW_STATUS", "PREMATCH_SEQ", "STATUS", "CASE_ID", "APPLY_DATE", "INSPRD_ID",
							"PROPOSER_NAME", "CUST_ID",		"C_TEL_NO",	"C_MOBILE", "C_CALL_TYPE", "C_TIME", "C_TIME_MEMO",
							"INSURED_NAME",  "INSURED_ID",	"I_TEL_NO",	"I_MOBILE", "I_CALL_TYPE", "I_TIME", "I_TIME_MEMO",
							"PAYER_NAME", 	 "PAYER_ID",	"P_TEL_NO",	"P_MOBILE", "P_CALL_TYPE", "P_TIME", "P_TIME_MEMO",
							"BRANCH_NBR", "RECRUIT_ID", "CALL_PERSON"};
		
		if (isEmpty(list))
			listCSV.add(new String[] { "查無資料" });
		else {
			for (Map<String, Object> map : list) {
				String[] records = new String[csvHeader.length];
				String CALL_C_YN = map.get("CALL_C_YN") != null ? map.get("CALL_C_YN").toString() : "";
				String CALL_I_YN = map.get("CALL_I_YN") != null ? map.get("CALL_I_YN").toString() : "";
				String CALL_P_YN = map.get("CALL_P_YN") != null ? map.get("CALL_P_YN").toString() : "";

				String cell = "";
				for (int i = 0; i < csvHeader.length; i++) {
					switch (csvMain[i]) {
						case "REVIEW_STATUS":
							String review_status = map.get("REVIEW_STATUS") != null ? map.get("REVIEW_STATUS").toString() : "";
							records[i] = review_status.equals("OK") ? "檢視完成" : "未完成";
							break;
						case "STATUS":
							String status = calloutStatusMap.get((String) map.get("STATUS"));
							records[i] = StringUtils.isNotEmpty(status) ? status : "";
							break;
//						case "APPLY_DATE":	// 最新異動日期
//							records[i] = formatDate(map.get(csvMain[i]), timeSdf);
//							break;
						case "PROPOSER_NAME":
						case "CUST_ID":
						case "C_TEL_NO":
						case "C_MOBILE":
						case "C_TIME_MEMO":
							cell = "";
							if (CALL_C_YN.equals("Y")) {
								cell = map.get(csvMain[i]) != null ? map.get(csvMain[i]).toString() : "";
							}
							records[i] = cell;
							break;
						case "C_CALL_TYPE":
							String c_call_type = "";
							if (CALL_C_YN.equals("Y")) {
								c_call_type = map.get("C_CALL_TYPE") != null ? map.get("C_CALL_TYPE").toString() : "";
							}
							String cCallType = callTypeMap.get(c_call_type);
							records[i] = StringUtils.isNotEmpty(cCallType) ? cCallType : "";
							break;
						case "C_TIME":
							String c_time = "";
							if (CALL_C_YN.equals("Y")) {
								c_time = timeMap.get((String) map.get(csvMain[i]));
							}
							records[i] = StringUtils.isNotEmpty(c_time) ? c_time : "";
							break;
						case "INSURED_NAME":
						case "INSURED_ID":
						case "I_TEL_NO":
						case "I_MOBILE":
						case "I_TIME_MEMO":
							cell = "";
							if (CALL_I_YN.equals("Y")) {
								cell = map.get(csvMain[i]) != null ? map.get(csvMain[i]).toString() : "";
							}
							records[i] = cell;
							break;
						case "I_CALL_TYPE":
							String i_call_type = "";
							if (CALL_I_YN.equals("Y")) {
								i_call_type = map.get("I_CALL_TYPE") != null ? map.get("I_CALL_TYPE").toString() : "";
							}
							String iCallType = callTypeMap.get(i_call_type);
							records[i] = StringUtils.isNotEmpty(iCallType) ? iCallType : "";
							break;
						case "I_TIME":
							String i_time = "";
							if (CALL_I_YN.equals("Y")) {
								i_time = timeMap.get((String) map.get(csvMain[i]));
							}
							records[i] = StringUtils.isNotEmpty(i_time) ? i_time : "";
							break;
						case "PAYER_NAME":
						case "PAYER_ID":
						case "P_TEL_NO":
						case "P_MOBILE":
						case "P_TIME_MEMO":
							cell = "";
							if (CALL_P_YN.equals("Y")) {
								cell = map.get(csvMain[i]) != null ? map.get(csvMain[i]).toString() : "";
							}
							records[i] = cell;
							break;
						case "P_CALL_TYPE":
							String p_call_type = "";
							if (CALL_P_YN.equals("Y")) {
								p_call_type = map.get("P_CALL_TYPE") != null ? map.get("P_CALL_TYPE").toString() : "";
							}
							String pCallType = callTypeMap.get(p_call_type);
							records[i] = StringUtils.isNotEmpty(pCallType) ? pCallType : "";
							break;
						case "P_TIME":
							String p_time = "";
							if (CALL_P_YN.equals("Y")) {
								p_time = timeMap.get((String) map.get(csvMain[i]));
							}
							records[i] = StringUtils.isNotEmpty(p_time) ? p_time : "";
							break;
//						case "BRANCH_NBR":	// 理專歸屬行
//							records[i] = format("%s-%s", defaultString((String) map.get(csvMain[i])), defaultString((String) map.get("BRANCH_NAME")));
//							break;
						default:
							records[i] = defaultString((String) map.get(csvMain[i]));
							break;
					}
				}

				for (int index = 0; index < records.length; index++)
					records[index] = format("=\"%s\"", records[index]);

				listCSV.add(records);
			}
		}

		CSVUtil csv = new CSVUtil();
		csv.setHeader(csvHeader);
		csv.addRecordList(listCSV);
		String csvName = (inputVO.getFromIOT421() != null && inputVO.getFromIOT421()) ? "保險電訪作業" : "保險電訪派件作業";
		notifyClientToDownloadFile(csv.generateCSV(), format(csvName + "_%s.csv", dateSdf.format(new Date())));
	}
	
	public void save(Object body, IPrimitiveMap header) throws JBranchException {
		IOT420InputVO inputVO = (IOT420InputVO) body;
		List<Map<String, Object>> list = inputVO.getResultList();
		String loginID = (String) getUserVariable(FubonSystemVariableConsts.LOGINID);
		dam = this.getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		
		for (Map<String, Object> map : list) {
			String prematch_seq = map.get("PREMATCH_SEQ").toString();
			String c_time2 = this.convertTime2(map, "C");
			String i_time2 = this.convertTime2(map, "I");
			String p_time2 = this.convertTime2(map, "P");
			
			qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sb = new StringBuffer();
			sb.append(" UPDATE TBIOT_CALLOUT SET ");
			sb.append(" C_TIME2 = :c_time2, ");
			sb.append(" C_TIME_MEMO = :c_time_memo, ");
			sb.append(" I_TIME2 = :i_time2, ");
			sb.append(" I_TIME_MEMO = :i_time_memo, ");
			sb.append(" P_TIME2 = :p_time2, ");
			sb.append(" P_TIME_MEMO = :p_time_memo, ");
			sb.append(" VERSION = VERSION+1, ");
			sb.append(" MODIFIER = :loginID, ");
			sb.append(" LASTUPDATE = SYSDATE ");
			sb.append(" WHERE PREMATCH_SEQ = :prematch_seq ");
			
			qc.setObject("c_time2", c_time2);
			qc.setObject("i_time2", i_time2);
			qc.setObject("p_time2", p_time2);
			
			qc.setObject("c_time_memo", map.get("C_TIME_MEMO"));
			qc.setObject("i_time_memo", map.get("I_TIME_MEMO"));
			qc.setObject("p_time_memo", map.get("P_TIME_MEMO"));
			
			qc.setObject("loginID", loginID);
			qc.setObject("prematch_seq", prematch_seq);
			qc.setQueryString(sb.toString());
			dam.exeUpdate(qc);
		}
		this.sendRtnObject(null);
	}
	
	private String convertTime2(Map<String, Object> map, String type) throws JBranchException {
		String time1 = map.get(type + "_TIME1") == null ? "0" : (map.get(type + "_TIME1").toString().equals("Y") ? "1" : "0");
		String time2 = map.get(type + "_TIME2") == null ? "0" : (map.get(type + "_TIME2").toString().equals("Y") ? "1" : "0");
		String time3 = map.get(type + "_TIME3") == null ? "0" : (map.get(type + "_TIME3").toString().equals("Y") ? "1" : "0");
		String time4 = map.get(type + "_TIME4") == null ? "0" : (map.get(type + "_TIME4").toString().equals("Y") ? "1" : "0");
		String time = time1 + time2 + time3 + time4;
		return time;
	}
	
	/**檢查seq No */
	private Boolean checkID(BigDecimal seqNo) throws JBranchException {
		Boolean ans = false;
		TBCAM_CAL_SALES_TASKVO vo = new TBCAM_CAL_SALES_TASKVO();
		vo = (TBCAM_CAL_SALES_TASKVO) dam.findByPKey(TBCAM_CAL_SALES_TASKVO.TABLE_UID, seqNo);
		if (vo != null)
			ans = true;
		else
			ans = false;
		return ans;
	}
	
	/**產生seq No */
	private String getSEQ() throws JBranchException {
		SerialNumberUtil seq = new SerialNumberUtil();
		String seqNum = "";
		try {
			seqNum = seq.getNextSerialNumber("TBCAM_CAL_SALES_TASK");
		} catch(Exception e) {
			seq.createNewSerial("CRM121", "0000000000", null, null, null, 6, new Long("99999999"), "y", new Long("0"), null);
			seqNum = seq.getNextSerialNumber("TBCAM_CAL_SALES_TASK");
		}
		return seqNum;
	}
	
	// 格式時間
//	private String formatDate(Object date, SimpleDateFormat sdf) {
//		if (date != null)
//			return sdf.format(date);
//		else
//			return "";
//	}
}
