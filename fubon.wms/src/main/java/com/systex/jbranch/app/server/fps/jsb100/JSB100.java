package com.systex.jbranch.app.server.fps.jsb100;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

@Component("jsb100")
@Scope("request")
public class JSB100 extends FubonWmsBizLogic {
	private DataAccessManager dam = null;

	public void inquire (Object body, IPrimitiveMap header) throws JBranchException {
		JSB100InputVO inputVO = (JSB100InputVO) body;
		JSB100OutputVO outputVO = new JSB100OutputVO();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		dam = this.getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		StringBuffer sb = new StringBuffer();
		sb.append(" SELECT S03.SEQ_NO, S01.UPDATE_STATUS, S01.ACCEPTID, S01.POLICY_SIMP_NAME, S01.POLICY_FULL_NAME, ");
		sb.append(" CASE WHEN S03.SEQ_NO IS NULL THEN S01.POLICY_NBR ELSE S03.POLICY_NBR END AS POLICY_NBR, ");
		sb.append(" CASE WHEN S03.SEQ_NO IS NULL THEN S01.CUST_ID ELSE S03.CUST_ID END AS CUST_ID, ");
		sb.append(" CASE WHEN S03.SEQ_NO IS NULL THEN S01.APPL_NAME ELSE S03.APPL_NAME END AS APPL_NAME, ");
		sb.append(" CASE WHEN S03.SEQ_NO IS NULL THEN S01.INS_ID ELSE S03.INS_ID END AS INS_ID, ");
		sb.append(" CASE WHEN S03.SEQ_NO IS NULL THEN S01.INS_NAME ELSE S03.INS_NAME END AS INS_NAME, ");
		sb.append(" CASE WHEN S03.SEQ_NO IS NULL THEN S01.PROPOSER_BIRTH ELSE S03.PROPOSER_BIRTH END AS PROPOSER_BIRTH, ");
		sb.append(" CASE WHEN S03.SEQ_NO IS NULL THEN S01.INSURED_BIRTH ELSE S03.INSURED_BIRTH END AS INSURED_BIRTH, ");
		sb.append(" CASE WHEN S03.SEQ_NO IS NULL THEN S01.PROJECT_CODE ELSE S03.PROJECT_CODE END AS PROJECT_CODE, ");
		sb.append(" CASE WHEN S03.SEQ_NO IS NULL THEN S01.PAY_TYPE ELSE S03.PAY_TYPE END AS PAY_TYPE, ");
		sb.append(" CASE WHEN S03.SEQ_NO IS NULL THEN S01.POLICY_ASSURE_AMT ELSE S03.POLICY_ASSURE_AMT END AS POLICY_ASSURE_AMT, ");
		sb.append(" CASE WHEN S03.SEQ_NO IS NULL THEN S01.UNIT_NBR ELSE S03.UNIT_NBR END AS UNIT_NBR, ");
		sb.append(" CASE WHEN S03.SEQ_NO IS NULL THEN S01.CONTRACT_STATUS ELSE S03.CONTRACT_STATUS END AS CONTRACT_STATUS, ");
		sb.append(" CASE WHEN S03.SEQ_NO IS NULL THEN S01.SERVICE_EMP_ID ELSE S03.SERVICE_EMP_ID END AS SERVICE_EMP_ID, ");
		sb.append(" CASE WHEN S03.SEQ_NO IS NULL THEN S01.DATA_DATE ELSE S03.DATA_DATE END AS DATA_DATE, ");
		sb.append(" CASE WHEN S03.SEQ_NO IS NULL THEN S01.USER_UPDATE_DATE ELSE S03.LASTUPDATE END AS USER_UPDATE_DATE, ");
		sb.append(" S01.INS_COMP_NAME, S01.SEQ, S03.STATUS, S03.UPDATE_REASON ");
		sb.append(" FROM TBJSB_AST_INS_MAST_SG S01 ");
		sb.append(" LEFT JOIN (SELECT * FROM TBJSB_INS_EDIT WHERE STATUS IN ('E', 'P', 'R')) S03 ");
		sb.append(" ON S01.SEQ = S03.SEQ ");
		sb.append(" WHERE S01.ACCEPTID IS NOT NULL ");
		
		if (StringUtils.isNotBlank(inputVO.getCust_id())) {
			sb.append(" AND NVL(S03.CUST_ID, S01.CUST_ID) LIKE :cust_id ");
			qc.setObject("cust_id", inputVO.getCust_id() + "%");
		}
		
		if (StringUtils.isNotBlank(inputVO.getIns_id())) {
			sb.append("AND NVL(S03.INS_ID, S01.INS_ID) LIKE :ins_id ");
			qc.setObject("ins_id", inputVO.getIns_id() + "%");
		}
		
		if (StringUtils.isNotBlank(inputVO.getSeq())) {
			sb.append(" AND S01.SEQ = :seq ");
			qc.setObject("seq", inputVO.getSeq());
		}
		
		if (StringUtils.isNotBlank(inputVO.getPolicy_nbr())) {
			sb.append(" AND NVL2(S03.SEQ_NO, S03.POLICY_NBR, S01.POLICY_NBR) = :policy_nbr ");
			qc.setObject("policy_nbr", inputVO.getPolicy_nbr());
		}
		
		if (StringUtils.isNotBlank(inputVO.getAcceptid())) {
			sb.append(" AND S01.ACCEPTID = :acceptid ");
			qc.setObject("acceptid", inputVO.getAcceptid());
		}
		
		if (StringUtils.isNotBlank(inputVO.getUpdate_status())) {
			if ("E".equals(inputVO.getUpdate_status())) {
				sb.append(" AND S01.UPDATE_STATUS IN ('E', 'R') ");
			} else if ("P".equals(inputVO.getUpdate_status())) {
				sb.append(" AND S01.UPDATE_STATUS = 'P' ");
			}
//			qc.setObject("update_status", inputVO.getUpdate_status());
		}
		
		if (StringUtils.isNotBlank(inputVO.getContract_status())) {
			sb.append(" AND NVL(S03.CONTRACT_STATUS, S01.CONTRACT_STATUS) = :contract_status ");
			qc.setObject("contract_status", inputVO.getContract_status());
		}
		
		if (inputVO.getUser_update_date_s() != null) {
			String sDate = sdf.format(inputVO.getUser_update_date_s());
			sb.append(" AND TO_CHAR(NVL(S03.LASTUPDATE, S01.USER_UPDATE_DATE), 'YYYYMMDD') >= :sDate ");
			qc.setObject("sDate", sDate);
		}
		
		if (inputVO.getUser_update_date_e() != null) {
			String eDate = sdf.format(inputVO.getUser_update_date_e());
			sb.append(" AND TO_CHAR(NVL(S03.LASTUPDATE, S01.USER_UPDATE_DATE), 'YYYYMMDD') <= :eDate ");
			qc.setObject("eDate", eDate);
		}
		
		sb.append(" ORDER BY S01.ACCEPTID, S01.POLICY_NBR, S01.POLICY_FULL_NAME ");
		
		qc.setQueryString(sb.toString());
		outputVO.setResultList(dam.exeQuery(qc));
		this.sendRtnObject(outputVO);
	}
	
	public void save (Object body, IPrimitiveMap header) throws JBranchException {
		JSB100InputVO inputVO = (JSB100InputVO) body;
		JSB100OutputVO outputVO = new JSB100OutputVO();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String loginID = (String) getUserVariable(FubonSystemVariableConsts.LOGINID);
		
		dam = this.getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		
		String seqNo = inputVO.getSeq_no();
		if (null == seqNo) {
			seqNo = this.getSN();
			// 新增
			sb.append(" INSERT INTO TBJSB_INS_EDIT ( ");
			sb.append(" SEQ_NO, SEQ, ACCEPTID, STATUS, POLICY_NBR, POLICY_FULL_NAME, ");
			sb.append(" APPL_NAME, CUST_ID, PROPOSER_BIRTH, ");
			sb.append(" INS_NAME, INS_ID, INSURED_BIRTH, ");
			sb.append(" PROJECT_CODE, PAY_TYPE, UNIT_NBR, ");
			sb.append(" POLICY_ASSURE_AMT, ");
			sb.append(" CONTRACT_STATUS, SERVICE_EMP_ID, DATA_DATE, UPDATE_REASON, APPLY_ID, ");
			sb.append(" VERSION, CREATETIME, CREATOR, MODIFIER, LASTUPDATE ");
			sb.append(" ) VALUES ( ");
			sb.append(" :seq_no, :seq, :acceptid, 'E', :policy_nbr, :policy_full_name, ");
			sb.append(" :appl_name, :cust_id, TO_DATE(:proposer_birth, 'yyyyMMdd'), ");
			sb.append(" :ins_name, :ins_id, TO_DATE(:insured_birth, 'yyyyMMdd'), ");
			sb.append(" :project_code, :pay_type,  :unit_nbr, ");
			
			if (StringUtils.isNotBlank(inputVO.getPolicy_assure_amt())) {
				sb.append(" :policy_assure_amt, ");
			} else {
				sb.append(" NULL, ");
			}
			
			sb.append(" :contract_status, :service_emp_id, TO_DATE(:data_date, 'yyyyMMdd'), :update_reason, :loginID, ");
			sb.append(" 0, SYSDATE, :loginID, :loginID, SYSDATE) ");
			
			qc.setObject("seq_no", Integer.parseInt(seqNo));
			qc.setObject("seq", inputVO.getSeq());
			qc.setObject("acceptid", inputVO.getAcceptid());
			qc.setObject("policy_full_name", inputVO.getPolicy_full_name());
			
		} else {
			// 更新
			sb.append(" UPDATE TBJSB_INS_EDIT ");
			sb.append(" SET STATUS = 'E', ");
			sb.append(" POLICY_NBR = :policy_nbr, ");
			sb.append(" APPL_NAME = :appl_name, ");
			sb.append(" CUST_ID = :cust_id, ");
			sb.append(" PROPOSER_BIRTH = TO_DATE(:proposer_birth, 'yyyyMMdd'), ");
			sb.append(" INS_NAME = :ins_name, ");
			sb.append(" INS_ID = :ins_id, ");
			sb.append(" INSURED_BIRTH = TO_DATE(:insured_birth, 'yyyyMMdd'), ");
			sb.append(" PROJECT_CODE = :project_code, ");
			sb.append(" PAY_TYPE = :pay_type, ");
			
			if (StringUtils.isNotBlank(inputVO.getPolicy_assure_amt())) {
				sb.append(" POLICY_ASSURE_AMT = :policy_assure_amt, ");				
			} else {
				sb.append(" POLICY_ASSURE_AMT = NULL, ");
			}
			
			sb.append(" UNIT_NBR = :unit_nbr, ");
			sb.append(" CONTRACT_STATUS = :contract_status, ");
			sb.append(" SERVICE_EMP_ID = :service_emp_id, ");
			sb.append(" DATA_DATE = TO_DATE(:data_date, 'yyyyMMdd'), ");
			sb.append(" UPDATE_REASON = :update_reason, ");
			sb.append(" APPLY_ID = :loginID, ");
			sb.append(" VERSION = VERSION+1, ");
			sb.append(" MODIFIER = :loginID, ");
			sb.append(" LASTUPDATE = SYSDATE ");
			sb.append(" WHERE SEQ_NO = :seq_no ");
			
			qc.setObject("seq_no", seqNo);
		}
		
		qc.setObject("policy_nbr"		, inputVO.getPolicy_nbr());
		qc.setObject("appl_name"		, inputVO.getAppl_name());
		qc.setObject("cust_id"			, inputVO.getCust_id());
		qc.setObject("proposer_birth"	, inputVO.getProposer_birth() != null ? sdf.format(inputVO.getProposer_birth()) : null);
		qc.setObject("ins_name"			, inputVO.getIns_name());
		qc.setObject("ins_id"			, inputVO.getIns_id());
		qc.setObject("insured_birth"	, inputVO.getInsured_birth() != null ? sdf.format(inputVO.getInsured_birth()) : null);
		qc.setObject("project_code"		, inputVO.getProject_code());
		qc.setObject("pay_type"			, inputVO.getPay_type());
		
		if (StringUtils.isNotBlank(inputVO.getPolicy_assure_amt())) {
			qc.setObject("policy_assure_amt", inputVO.getPolicy_assure_amt());			
		}
		
		qc.setObject("unit_nbr"			, inputVO.getUnit_nbr());
		qc.setObject("contract_status"	, inputVO.getContract_status());
		qc.setObject("service_emp_id"	, inputVO.getService_emp_id());
		qc.setObject("data_date"		, inputVO.getData_date() != null ? sdf.format(inputVO.getData_date()) : null);
		qc.setObject("update_reason"	, inputVO.getUpdate_reason());
		qc.setObject("loginID"			, loginID);
		
		qc.setQueryString(sb.toString());
		dam.exeUpdate(qc);
		
		// 更新保單主檔的修改狀態 TBJSB_AST_INS_MAST_SG.UPDATE_STATUS
		qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sb = new StringBuffer();
		sb.append(" UPDATE TBJSB_AST_INS_MAST_SG SET UPDATE_STATUS = 'E' WHERE SEQ = :seq ");
		
		qc.setObject("seq", inputVO.getSeq());
		qc.setQueryString(sb.toString());
		dam.exeUpdate(qc);
		
		outputVO.setSTATUS("E");		// 修改中(E)
		outputVO.setSEQ_NO(seqNo);
		this.sendRtnObject(outputVO);
	}
	
	// 送出覆核(P)/取消修改(C)/退回(R): 更新『TBJSB_INS_EDIT』及『TBJSB_AST_INS_MAST_SG』狀態
	public void updateStatus (Object body, IPrimitiveMap header) throws JBranchException {
		JSB100InputVO inputVO = (JSB100InputVO) body;
		String seqNo = inputVO.getSeq_no();
		if (null != seqNo) {
			String status = inputVO.getStatus();
			String loginID = (String) getUserVariable(FubonSystemVariableConsts.LOGINID);
			dam = this.getDataAccessManager();
			QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sb = new StringBuffer();
			
			// 更新 TBJSB_INS_EDIT.STATUS
			sb.append(" UPDATE TBJSB_INS_EDIT ");
			sb.append(" SET STATUS = :status, ");
			if (status.equals("P")) {
				sb.append(" APPLY_DATE = SYSDATE, ");
			}
			sb.append(" VERSION = VERSION+1, ");
			sb.append(" MODIFIER = :loginID, ");
			sb.append(" LASTUPDATE = SYSDATE ");
			sb.append(" WHERE SEQ_NO = :seq_no ");
			
			qc.setObject("status", status);
			qc.setObject("loginID", loginID);
			qc.setObject("seq_no", seqNo);
			qc.setQueryString(sb.toString());
			dam.exeUpdate(qc);
			
			// 更新保單主檔的修改狀態 TBJSB_AST_INS_MAST_SG.UPDATE_STATUS
			qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sb = new StringBuffer();
			sb.append(" UPDATE TBJSB_AST_INS_MAST_SG ");
			
			if ("C".equals(status)) {
				sb.append(" SET UPDATE_STATUS = NULL ");
				
			} else {
				sb.append(" SET UPDATE_STATUS = :status ");	
				qc.setObject("status", status);
			}
			sb.append(" WHERE SEQ = :seq ");
			
			qc.setObject("seq", inputVO.getSeq());
			qc.setQueryString(sb.toString());
			dam.exeUpdate(qc);
			
		} else {
			throw new APException("系統發生錯誤請洽系統管理員");
		}
		this.sendRtnObject(null);
	}
	
	public void approve (Object body, IPrimitiveMap header) throws JBranchException {
		JSB100InputVO inputVO = (JSB100InputVO) body;
		String seqNo = inputVO.getSeq_no();
		String seq = inputVO.getSeq();
		String acceptid = inputVO.getAcceptid();
		String policy_nbr = inputVO.getPolicy_nbr();
		String policy_full_name = inputVO.getPolicy_full_name();
		Boolean chgStatus = false;
		
		// 核可: 比對『TBJSB_AST_INS_MAST_SG』及『TBJSB_INS_EDIT』欄位差異，
		// 寫入『TBJSB_INS_EDIT_LOG』，並更新『TBJSB_AST_INS_MAST』相對應欄位及狀態
		dam = this.getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		sb.append(" SELECT * FROM TBJSB_INS_EDIT WHERE SEQ_NO = :seq_no ");
		qc.setObject("seq_no", seqNo);
		qc.setQueryString(sb.toString());
		List<Map<String, Object>> editList = dam.exeQuery(qc);
		Map<String, Object> editMap = editList.get(0);
		
		qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sb = new StringBuffer();
		sb.append(" SELECT * FROM TBJSB_AST_INS_MAST_SG WHERE SEQ = :seq ");
		
		qc.setObject("seq", seq);
		qc.setQueryString(sb.toString());
		List<Map<String, Object>> insList = dam.exeQuery(qc);
		Map<String, Object> insMap = insList.get(0);
		
		// 比對用
		Map<String, String> map = new HashMap<>();
		map.put("POLICY_NBR"		, "保單號碼");
		map.put("APPL_NAME"			, "要保人姓名");
		map.put("CUST_ID"			, "要保人ID");
		map.put("PROPOSER_BIRTH"	, "要保人生日");
		map.put("INS_NAME"			, "被保人姓名");
		map.put("INS_ID"			, "被保人ID");
		map.put("INSURED_BIRTH"		, "被保人生日");
		map.put("PROJECT_CODE"		, "專案代號");
		map.put("PAY_TYPE"			, "繳別");
		map.put("POLICY_ASSURE_AMT"	, "保額");
		map.put("UNIT_NBR"			, "單位");
		map.put("CONTRACT_STATUS"	, "契約狀態");
		map.put("SERVICE_EMP_ID"	, "保單服務人員ID");
		map.put("DATA_DATE"			, "參考日期");
		
		// 串『異動記錄』每個欄位逐一寫入 TABLE TBJSB_INS_EDIT_LOG.FIELD_CHANGE_LOG
		String field_change_log = "";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		Map<String, String> tempMap = new HashMap<>();
		String ins = null;
		String edit = null;
		for (String key : map.keySet()) {
			if (key.equals("PROPOSER_BIRTH")) {
				String insBirth = insMap.get("PROPOSER_BIRTH") != null ? sdf.format(insMap.get("PROPOSER_BIRTH")) : "";
				String editBirth = editMap.get("PROPOSER_BIRTH") != null ? sdf.format(editMap.get("PROPOSER_BIRTH")) : "";
				if (!insBirth.equals(editBirth)) {
					if (field_change_log.length() > 0) {
						field_change_log += ",";
					}
					field_change_log += map.get(key) + "：" + insBirth + "→" + editBirth;
				}
			} else if (key.equals("INSURED_BIRTH")) {
				String insBirth = insMap.get("INSURED_BIRTH") != null ? sdf.format(insMap.get("INSURED_BIRTH")) : "";
				String editBirth = editMap.get("INSURED_BIRTH") != null ? sdf.format(editMap.get("INSURED_BIRTH")) : "";
				if (!insBirth.equals(editBirth)) {
					if (field_change_log.length() > 0) {
						field_change_log += ",";
					}
					field_change_log += map.get(key) + "：" + insBirth + "→" + editBirth;
				}
			} else if (key.equals("DATA_DATE")) {
				String dataDate = insMap.get("DATA_DATE") != null ? sdf.format(insMap.get("DATA_DATE")) : "";
				String editDataDate = editMap.get("DATA_DATE") != null ? sdf.format(editMap.get("DATA_DATE")) : "";
				if (!dataDate.equals(editDataDate)) {
					if (field_change_log.length() > 0) {
						field_change_log += ",";
					}
					field_change_log += map.get(key) + "：" + dataDate + "→" + editDataDate;
				}
			} else {
				tempMap = this.compareMap(insMap, editMap, key);
				if (tempMap.get("CHANGE_YN").equals("Y")) {
					ins = tempMap.get("INS_DATA") == null ? "" : tempMap.get("INS_DATA");
					edit = tempMap.get("EDIT_DATA") == null ? "" : tempMap.get("EDIT_DATA");
					
					if (field_change_log.length() > 0) {
						field_change_log += "，";
					}
					// 繳別
					if (key.equals("PAY_TYPE")) {
						XmlInfo xmlInfo = new XmlInfo();
						Map<String, String> payTypeMap = xmlInfo.doGetVariable("PMS.PAY_YQD", FormatHelper.FORMAT_3);
						ins = payTypeMap.get(ins);
						edit = payTypeMap.get(edit);
					}
					// 契約狀態
					if (key.equals("CONTRACT_STATUS")) {
						XmlInfo xmlInfo = new XmlInfo();
						Map<String, String> statusMap = xmlInfo.doGetVariable("CRM.CRM239_CONTRACT_STATUS", FormatHelper.FORMAT_3);
						ins = statusMap.get(ins);
						edit = statusMap.get(edit);
						chgStatus = true;
					}
					field_change_log += map.get(key) + "：" + ins + "→" + edit;
				}
			}
		}
		
		String loginID = (String) getUserVariable(FubonSystemVariableConsts.LOGINID);
		// 寫入『TBJSB_INS_EDIT_LOG』
		qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sb = new StringBuffer();
		sb.append(" INSERT INTO TBJSB_INS_EDIT_LOG ( ");
		sb.append(" SEQ_NO, SEQ, ACCEPTID, POLICY_NBR, POLICY_FULL_NAME, FIELD_CHANGE_LOG, ");
		sb.append(" VERSION, CREATETIME, CREATOR, MODIFIER, LASTUPDATE ");
		sb.append(" ) VALUES ( ");
		sb.append(" :seq_no, :seq, :acceptid, :policy_nbr, :policy_full_name, :field_change_log, ");
		sb.append(" 0, SYSDATE, :loginID, :loginID, SYSDATE) ");
		
		qc.setObject("seq_no", seqNo);
		qc.setObject("seq", seq);
		qc.setObject("acceptid", acceptid);
		qc.setObject("policy_nbr", policy_nbr);
		qc.setObject("policy_full_name", policy_full_name);
		qc.setObject("field_change_log", field_change_log);
		qc.setObject("loginID", loginID);
		
		qc.setQueryString(sb.toString());
		dam.exeUpdate(qc);
		
		// 更新『TBJSB_INS_EDIT』覆核主管(APPROVE_ID)、覆核日期(APPROVE_DATE	)
		qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sb = new StringBuffer();
		sb.append(" UPDATE TBJSB_INS_EDIT ");
		sb.append(" SET APPROVE_ID = :loginID, APPROVE_DATE = SYSDATE, STATUS = 'A' WHERE SEQ_NO = :seq_no ");
		
		qc.setObject("loginID", loginID);
		qc.setObject("seq_no", seqNo);
		qc.setQueryString(sb.toString());
		dam.exeUpdate(qc);
		
		// 人工更新需備份保單檔資料：將『TBJSB_AST_INS_MAST_SG』資料備份寫入『TBJSB_AST_INS_MAST_LOG』
		qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sb = new StringBuffer();
		sb.append(" INSERT INTO TBJSB_AST_INS_MAST_LOG ");
		sb.append(" SELECT :seq_no, CUST_ID, UNIT_NBR, COM_ID, COM_NAME, POLICY_NBR, ID_DUP, POLICY_SEQ, ");
		sb.append(" APPL_NAME, INS_TYPE, POLICY_SIMP_NAME, POLICY_FULL_NAME, POLICY_ASSURE_AMT_DIV10K, ");
		sb.append(" POLICY_ASSURE_AMT, UNIT, INS_NAME, INS_ID, PAY_TYPE, POLICY_FEE, POLICY_OVER_FEE, ");
		sb.append(" ACUM_INS_AMT_ORGD, ACUM_PAID_POLICY_FEE, ACUM_WDRAW_POLICY_FEE, INV_INS_AMT, POLICY_YEAR, ");
		sb.append(" PAY_TERM_YEAR, POLICY_ACTIVE_DATE, APPL_DATE, SERVICE_EMP_ID, EMP_NAME, CONTRACT_STATUS, ");
		sb.append(" CONTRACT_TEXT, CONTRACT_DATE, PAID_TIMES, SYS_YEAR, SYS_MONTH, SYS_DAY, ");
		sb.append(" CAL_VALUE_YEAR, CAL_VALUE_MONTH, CAL_VALUE_DAY, ACC_VALUE, LOAN_QUOTA, LOANED_AMT, ");
		sb.append(" LOAN_RATE, ACUM_COST_TWD, EXCH_RATE, TERMI_AMT, SECRET_YN, DATA_DATE, MAST_SLA_TYPE, ");
		sb.append(" INV_DATE_START, SP_POLICY_NOTE, COMM_PRJ_NAME, TERM_CNT, CRCY_TYPE, ");
		sb.append(" AGMT_CRCY_TYPE_FORD, AGMT_AMT_FORD, SEQ, PRD_ID, PROJECT_CODE, REL_TYPE, ACCU_PREM, ");
		sb.append(" INS_COMP_NAME, CONSENT_YN, ACUM_INT_AMT, TX_SRC, PROPOSER_BIRTH, INSURED_BIRTH, ");
		sb.append(" BILL_REMRK, BILL_DATE, ACCEPTID, 0, SYSDATE, :loginID, :loginID, SYSDATE, ");
		sb.append(" UPDATE_STATUS, USER_UPDATE_DATE ");
		sb.append(" FROM TBJSB_AST_INS_MAST_SG WHERE SEQ = :seq ");
		
		qc.setObject("seq_no", seqNo);
		qc.setObject("loginID", loginID);
		qc.setObject("seq", seq);
		qc.setQueryString(sb.toString());
		dam.exeUpdate(qc);
		
		// 更新『TBJSB_AST_INS_MAST』相對應欄位及狀態
		qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sb = new StringBuffer();
		sb.append(" UPDATE TBJSB_AST_INS_MAST_SG ");
		sb.append(" SET POLICY_NBR = :policy_nbr, APPL_NAME = :appl_name, CUST_ID = :cust_id, ");
		sb.append(" INS_NAME = :ins_name, INS_ID = :ins_id, PROJECT_CODE = :project_code, ");
		sb.append(" PAY_TYPE = :pay_type, UNIT_NBR = :unit_nbr, ");
		
		if (StringUtils.isNotBlank(inputVO.getPolicy_assure_amt())) {
			sb.append(" POLICY_ASSURE_AMT = :policy_assure_amt, ");				
		} else {
			sb.append(" POLICY_ASSURE_AMT = NULL, ");
		}
		
		sb.append(" CONTRACT_TEXT = (SELECT TRIM('　' FROM PARAM_NAME) FROM TBSYSPARAMETER WHERE PARAM_TYPE = 'CRM.CRM239_CONTRACT_STATUS' AND PARAM_CODE = :contract_status ), ");
		sb.append(" CONTRACT_STATUS = :contract_status, ");
		// 若有變更保單狀態，『CONTRACT_DATE』需改為覆核日期(系統日期)。
		if (chgStatus) {
			sb.append(" CONTRACT_DATE = SYSDATE, ");
		}
		
		sb.append(" SERVICE_EMP_ID = :service_emp_id, ");
		sb.append(" UPDATE_STATUS = 'A', USER_UPDATE_DATE = :user_update_date, ");
		sb.append(" PROPOSER_BIRTH = TO_DATE(:proposer_birth, 'yyyyMMdd'), ");
		sb.append(" INSURED_BIRTH = TO_DATE(:insured_birth, 'yyyyMMdd'), ");
		sb.append(" DATA_DATE = TO_DATE(:data_date, 'yyyyMMdd'), ");
		sb.append(" MODIFIER = :loginID, LASTUPDATE = SYSDATE ");
		sb.append(" WHERE SEQ = :seq ");
		
		qc.setObject("policy_nbr", inputVO.getPolicy_nbr());
		qc.setObject("appl_name", inputVO.getAppl_name());
		qc.setObject("cust_id", inputVO.getCust_id());
		qc.setObject("proposer_birth", inputVO.getProposer_birth() != null ? sdf.format(inputVO.getProposer_birth()) : null);
		qc.setObject("ins_name", inputVO.getIns_name());
		qc.setObject("ins_id", inputVO.getIns_id());
		qc.setObject("insured_birth", inputVO.getInsured_birth() != null ? sdf.format(inputVO.getInsured_birth()) : null);
		qc.setObject("project_code", inputVO.getProject_code());
		qc.setObject("pay_type", inputVO.getPay_type());
		if (StringUtils.isNotBlank(inputVO.getPolicy_assure_amt())) {
			qc.setObject("policy_assure_amt", inputVO.getPolicy_assure_amt());			
		}
		qc.setObject("unit_nbr", inputVO.getUnit_nbr());
		qc.setObject("contract_status", inputVO.getContract_status());
		qc.setObject("service_emp_id", inputVO.getService_emp_id());
		qc.setObject("data_date", inputVO.getData_date() != null ? sdf.format(inputVO.getData_date()) : null);
		qc.setObject("user_update_date", new Timestamp(inputVO.getUser_update_date().getTime()));
		qc.setObject("loginID", loginID);
		qc.setObject("seq", seq);
		
		qc.setQueryString(sb.toString());
		dam.exeUpdate(qc);
		
		this.sendRtnObject(null);
	}
	
	public void getEditLog (Object body, IPrimitiveMap header) throws JBranchException {
		JSB100InputVO inputVO = (JSB100InputVO) body;
		JSB100OutputVO outputVO = new JSB100OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		StringBuffer sb = new StringBuffer();
		sb.append(" SELECT E.APPLY_ID, E.APPROVE_ID, L.FIELD_CHANGE_LOG, ");
		sb.append(" TO_CHAR(E.APPLY_DATE, 'yyyy/MM/dd hh24:mi:ss') AS APPLY_DATE, ");
		sb.append(" TO_CHAR(E.APPROVE_DATE, 'yyyy/MM/dd hh24:mi:ss') AS APPROVE_DATE ");
		sb.append(" FROM TBJSB_INS_EDIT_LOG L ");
		sb.append(" LEFT JOIN TBJSB_INS_EDIT E ON L.SEQ_NO = E.SEQ_NO ");
		sb.append(" WHERE L.SEQ = :seq ");
		sb.append(" ORDER BY E.APPLY_DATE DESC ");
		
		qc.setObject("seq", inputVO.getSeq());
		qc.setQueryString(sb.toString());
		outputVO.setResultList(dam.exeQuery(qc));
		this.sendRtnObject(outputVO);
	}
	
	private Map<String, String> compareMap(Map<String, Object> insMap, Map<String, Object> editMap, String key) throws JBranchException {
		Map<String, String> resultMap = new HashMap<>();
		boolean changeFlag = false;
		String insData = null;
		String editData = null;
		
		if (insMap.get(key) != null) {
			insData = insMap.get(key).toString();
		}
		if (editMap.get(key) != null) {
			editData = editMap.get(key).toString();
		}
		
		if (insData == null && editData == null) {
			changeFlag = false;
		} else {
			if (insData == null || editData == null) {
				changeFlag = true;
			} else {
				if (insData.equals(editData)) {
					changeFlag = false;
				} else {
					changeFlag = true;
				}
			}
		}
		resultMap.put("CHANGE_YN", changeFlag == true ? "Y" : "N");
		resultMap.put("INS_DATA", insData);
		resultMap.put("EDIT_DATA", editData);
		return resultMap;
	}
	
	/**
	 * 取得序號
	 * @return String : SEQ
	 * @throws JBranchException
	 */
	private String getSN() throws JBranchException {
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT TBJSB_INS_EDIT_SEQ.nextval AS SEQ FROM DUAL ");
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> SEQLIST = dam.exeQuery(queryCondition);

		return ObjectUtils.toString(SEQLIST.get(0).get("SEQ"));
	}
	
}