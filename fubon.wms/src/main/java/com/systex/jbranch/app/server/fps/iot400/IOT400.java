package com.systex.jbranch.app.server.fps.iot400;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Blob;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.ObjectUtils;
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
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.ObjectUtil;
import com.systex.jbranch.platform.common.util.StringUtil;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

@Component("iot400")
@Scope("request")
public class IOT400 extends FubonWmsBizLogic {
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(IOT400.class);

	public void queryData(Object body, IPrimitiveMap header) throws JBranchException {
		IOT400InputVO inputVO = (IOT400InputVO) body;
		IOT400OutputVO outputVO = new IOT400OutputVO();
		outputVO = this.queryFiles(inputVO);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		dam = this.getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		
		sb.append(" SELECT S01.PREMATCH_SEQ, S01.CASE_ID, S01.INSPRD_KEYNO, S01.INSPRD_ID, ");
		sb.append(" S02.INSPRD_NAME, S01.REAL_PREMIUM, S02.CURR_CD, S02.PAY_TYPE, ");
		sb.append(" S01.CONTRACT_END_YN, S01.S_INFITEM_LOAN_YN, S01.LOAN_SOURCE_YN, S01.LOAN_SOURCE2_YN, ");
		sb.append(" S01.C_SALE_SENIOR_YN, S01.I_SALE_SENIOR_YN, S01.P_SALE_SENIOR_YN, ");
		sb.append(" S01.C_LOAN_APPLY_DATE, S01.I_LOAN_APPLY_DATE, S01.P_LOAN_APPLY_DATE, ");
		sb.append(" S01.C_LOAN_CHK2_DATE, S01.I_LOAN_CHK2_DATE, S01.LOAN_CHK2_DATE, ");
		sb.append(" S01.C_LOAN_CHK1_YN, S01.I_LOAN_CHK1_YN, S01.LOAN_CHK1_YN, ");
		sb.append(" S01.C_LOAN_CHK2_YN, S01.I_LOAN_CHK2_YN, S01.LOAN_CHK2_YN, ");
		sb.append(" S01.C_LOAN_CHK3_YN, S01.I_LOAN_CHK3_YN, S01.LOAN_CHK3_YN, ");
		sb.append(" S01.C_CD_CHK_YN, S01.I_CD_CHK_YN, S01.CD_CHK_YN, S02.INSPRD_TYPE, ");
		sb.append(" S01.CUST_ID, S01.INSURED_ID, S01.PAYER_ID, S01.REPRESET_ID, ");
		sb.append(" S01.PROPOSER_NAME, S01.INSURED_NAME, S01.PAYER_NAME, S01.REPRESET_NAME, ");
		sb.append(" S01.PROPOSER_BIRTH, S01.INSURED_BIRTH, S01.PAYER_BIRTH, R.BIRTH_DATE AS REPRESET_BIRTH, ");
		sb.append(" TRUNC(MONTHS_BETWEEN(SYSDATE, S01.PROPOSER_BIRTH) / 12) AS C_AGE, ");
		sb.append(" TRUNC(MONTHS_BETWEEN(SYSDATE, S01.INSURED_BIRTH)  / 12) AS I_AGE, ");
		sb.append(" TRUNC(MONTHS_BETWEEN(SYSDATE, S01.PAYER_BIRTH) 	  / 12) AS P_AGE, ");
		sb.append(" CASE WHEN S05.STATUS IS NOT NULL THEN S05.C_TEL_NO ELSE ");
		sb.append(" (CC.TEL_NO || TRIM(CASE WHEN CC.EXT_NO IS NOT NULL THEN '#' || CC.EXT_NO ELSE '' END)) END AS C_TEL_NO, ");
		sb.append(" CASE WHEN S05.STATUS IS NOT NULL THEN S05.I_TEL_NO ELSE ");
		sb.append(" (IC.TEL_NO || TRIM(CASE WHEN IC.EXT_NO IS NOT NULL THEN '#' || IC.EXT_NO ELSE '' END)) END AS I_TEL_NO, ");
		sb.append(" CASE WHEN S05.STATUS IS NOT NULL THEN S05.P_TEL_NO ELSE ");
		sb.append(" (PC.TEL_NO || TRIM(CASE WHEN PC.EXT_NO IS NOT NULL THEN '#' || PC.EXT_NO ELSE '' END)) END AS P_TEL_NO, ");
		sb.append(" CASE WHEN S05.STATUS IS NOT NULL THEN S05.C_MOBILE ELSE CC.MOBILE_NO END AS C_MOBILE, ");
		sb.append(" CASE WHEN S05.STATUS IS NOT NULL THEN S05.I_MOBILE ELSE IC.MOBILE_NO END AS I_MOBILE, ");
		sb.append(" CASE WHEN S05.STATUS IS NOT NULL THEN S05.P_MOBILE ELSE PC.MOBILE_NO END AS P_MOBILE, ");
		sb.append(" S05.C_TIME, S05.I_TIME, S05.P_TIME, S05.SPECIAL_MEMO, S05.STATUS, S06.PDF_FILE, ");
		sb.append(" S05.I_REPRESET_NAME, S05.I_REPRESET_ID, S05.I_REPRESET_BIRTH, ");
		sb.append(" S05.P_REPRESET_NAME, S05.P_REPRESET_ID, S05.P_REPRESET_BIRTH ");
		sb.append(" FROM TBIOT_PREMATCH S01 ");
		sb.append(" LEFT JOIN TBPRD_INS_MAIN S02 ON S01.INSPRD_KEYNO = S02.INSPRD_KEYNO ");
		sb.append(" LEFT JOIN TBCRM_CUST_MAST C ON S01.CUST_ID = C.CUST_ID ");
		sb.append(" LEFT JOIN TBCRM_CUST_MAST I ON S01.INSURED_ID = I.CUST_ID ");
		sb.append(" LEFT JOIN TBCRM_CUST_MAST P ON S01.PAYER_ID = P.CUST_ID ");
		sb.append(" LEFT JOIN TBCRM_CUST_MAST R ON S01.REPRESET_ID = R.CUST_ID ");
		sb.append(" LEFT JOIN TBCRM_CUST_CONTACT CC ON S01.CUST_ID = CC.CUST_ID ");
		sb.append(" LEFT JOIN TBCRM_CUST_CONTACT IC ON S01.INSURED_ID = IC.CUST_ID ");
		sb.append(" LEFT JOIN TBCRM_CUST_CONTACT PC ON S01.PAYER_ID = PC.CUST_ID ");
		sb.append(" LEFT JOIN TBIOT_CALLOUT S05 ON S01.PREMATCH_SEQ = S05.PREMATCH_SEQ ");
		sb.append(" LEFT JOIN TBIOT_MAPP_PDF S06 ON S01.CASE_ID = S06.CASE_ID ");
		sb.append(" WHERE S01.PREMATCH_SEQ = :prematch_seq ");
		
		qc.setObject("prematch_seq", inputVO.getPREMATCH_SEQ());
		qc.setQueryString(sb.toString());
		List<Map<String, Object>> resultList = dam.exeQuery(qc);
		outputVO.setResultList(resultList);
		
		Boolean showMsg = false;
		if (resultList.size() > 0 && resultList.get(0).get("CASE_ID") != null) {
			String case_id = resultList.get(0).get("CASE_ID").toString();
			if (StringUtils.isNotBlank(case_id)) {
				qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sb = new StringBuffer();
				sb.append(" SELECT S05.FAIL_CALL_YN FROM TBIOT_CALLOUT S05 ");
				sb.append(" LEFT JOIN TBIOT_PREMATCH S01 ON S01.PREMATCH_SEQ = S05.PREMATCH_SEQ ");
				sb.append(" WHERE S01.CASE_ID = :case_id ");
				qc.setObject("case_id", case_id);
				qc.setQueryString(sb.toString());
				List<Map<String, Object>> sameCaseList = dam.exeQuery(qc);
				
				for (Map<String, Object> map : sameCaseList) {
					if (map.get("FAIL_CALL_YN") != null && StringUtils.isNotBlank(map.get("FAIL_CALL_YN").toString())) {
						String fail_call_yn = map.get("FAIL_CALL_YN").toString();
						if ("Y".equals(fail_call_yn)) {
							showMsg = true;
							break;
						}
					}
				}
			}
		}
		outputVO.setShowMsg(showMsg);
		this.sendRtnObject(outputVO);
	}
	
	// 檢視行動投保要保書
	public void getPDF(Object body, IPrimitiveMap header) throws JBranchException {
		IOT400InputVO inputVO = (IOT400InputVO) body;
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();

		sb.append(" SELECT * FROM TBIOT_MAPP_PDF WHERE CASE_ID = :case_id ");
		queryCondition.setObject("case_id", inputVO.getCASE_ID());
		queryCondition.setQueryString(sb.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		String errorMag = null;

		try {
			if (list.size() > 0 && list.get(0).get("PDF_FILE") != null) {
				String filePath = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
				String uuid = UUID.randomUUID().toString();
				String fileName = String.format("%s.pdf", uuid);
				Blob blob = (Blob) list.get(0).get("PDF_FILE");
				int blobLength = (int) blob.length();
				byte[] blobAsBytes = blob.getBytes(1, blobLength);

				File targetFile = new File(filePath, fileName);
				FileOutputStream fos = new FileOutputStream(targetFile);
				fos.write(blobAsBytes);
				fos.close();
				// 下載時 PDF 時，進行加密
//				notifyClientToDownloadFile(PdfUtil.process(new PdfConfigVO(this.getDataAccessManager(), "temp//" + fileName)), "行動投保要保書.pdf");
				notifyClientViewDoc("temp//" + fileName, "pdf");
			} else {
				errorMag = "查無此資料請洽系統管理員";
			}
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s", StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}

		if (StringUtils.isNotBlank(errorMag)) {
			throw new APException(errorMag);
		}
		
		this.sendRtnObject(null);
	}
	
	public void upload(Object body, IPrimitiveMap header) throws Exception {
		IOT400InputVO inputVO = (IOT400InputVO) body;
		IOT400OutputVO outputVO = new IOT400OutputVO();
		
		if (StringUtils.isNotBlank(inputVO.getFileName())) {
			byte[] data = Files.readAllBytes(Paths.get(new File((String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH), inputVO.getFileName()).toString()));
			String loginID = (String) getUserVariable(FubonSystemVariableConsts.LOGINID);
			dam = this.getDataAccessManager();
			QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sb = new StringBuffer();
			
			sb.append(" INSERT INTO TBIOT_CALLOUT_FILES ( ");
			sb.append(" PREMATCH_SEQ, SEQ, FILE_NAME, ATTACH_FILE, ");
			sb.append(" VERSION, CREATETIME, CREATOR, MODIFIER, LASTUPDATE ");
			sb.append(" ) VALUES ( ");
			sb.append(" :prematch_seq, :seq, :file_name, :attach_file, ");
			sb.append(" 0, SYSDATE, :loginID, :loginID, SYSDATE) ");
			
			qc.setObject("prematch_seq"	, inputVO.getPREMATCH_SEQ());
			qc.setObject("seq"			, inputVO.getSEQ());
			qc.setObject("file_name"	, inputVO.getFileRealName());
			qc.setObject("attach_file"	, ObjectUtil.byteArrToBlob(data));
			qc.setObject("loginID"		, loginID);
			qc.setQueryString(sb.toString());
			dam.exeUpdate(qc);
			
			outputVO = this.queryFiles(inputVO);
			this.sendRtnObject(outputVO);
		}
	}
	
	public void delete(Object body, IPrimitiveMap header) throws JBranchException {
		IOT400InputVO inputVO = (IOT400InputVO) body;
		IOT400OutputVO outputVO = new IOT400OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		sb.append(" DELETE FROM TBIOT_CALLOUT_FILES WHERE PREMATCH_SEQ = :prematch_seq AND SEQ = :seq ");
		qc.setObject("prematch_seq", inputVO.getPREMATCH_SEQ());
		qc.setObject("seq", inputVO.getSEQ());
		qc.setQueryString(sb.toString());
		dam.exeUpdate(qc);
		
		outputVO = this.queryFiles(inputVO);
		this.sendRtnObject(outputVO);
	}
	
	private IOT400OutputVO queryFiles(IOT400InputVO inputVO) throws JBranchException {
		IOT400OutputVO outputVO = new IOT400OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		sb.append(" SELECT * FROM TBIOT_CALLOUT_FILES WHERE PREMATCH_SEQ = :prematch_seq ");
		qc.setObject("prematch_seq", inputVO.getPREMATCH_SEQ());
		qc.setQueryString(sb.toString());
		outputVO.setFileList(dam.exeQuery(qc));
		
		qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sb = new StringBuffer();
		sb.append(" SELECT MAX(SEQ) AS SEQ FROM TBIOT_CALLOUT_FILES WHERE PREMATCH_SEQ = :prematch_seq ");
		qc.setObject("prematch_seq"	, inputVO.getPREMATCH_SEQ());
		qc.setQueryString(sb.toString());
		List<Map<String, Object>> resultList = dam.exeQuery(qc);
		int seq = 0;
		if (resultList != null && resultList.size() > 0) {
			seq = resultList.get(0).get("SEQ") != null ? Integer.parseInt(resultList.get(0).get("SEQ").toString()) : seq;
			seq++;
		}
		outputVO.setSEQ(seq);
		return outputVO;
	}
	
	public void download(Object body, IPrimitiveMap header) throws Exception {
		IOT400InputVO inputVO = (IOT400InputVO) body;
		dam = this.getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		
		String filePath = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
		String uuid = UUID.randomUUID().toString();
		
		sb.append(" SELECT * FROM TBIOT_CALLOUT_FILES WHERE PREMATCH_SEQ = :prematch_seq AND SEQ = :seq ");
		qc.setObject("prematch_seq", inputVO.getPREMATCH_SEQ());
		qc.setObject("seq", inputVO.getSEQ());
		qc.setQueryString(sb.toString());
		List<Map<String, Object>> list = dam.exeQuery(qc);
		
		String fileName = (String) list.get(0).get("FILE_NAME");
		Blob blob = (Blob) list.get(0).get("ATTACH_FILE");
		int blobLength = (int) blob.length();
		byte[] blobAsBytes = blob.getBytes(1, blobLength);

		File targetFile = new File(filePath, uuid);
		FileOutputStream fos = new FileOutputStream(targetFile);
		fos.write(blobAsBytes);
		fos.close();
		notifyClientToDownloadFile("temp//" + uuid, fileName);
	}
	
	public void save(Object body, IPrimitiveMap header) throws JBranchException {
		IOT400InputVO inputVO = (IOT400InputVO) body;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String loginID = (String) getUserVariable(FubonSystemVariableConsts.LOGINID);
		dam = this.getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		
		sb.append(" SELECT * FROM TBIOT_CALLOUT WHERE PREMATCH_SEQ = :prematch_seq ");
		qc.setObject("prematch_seq", inputVO.getPREMATCH_SEQ());
		qc.setQueryString(sb.toString());
		List<Map<String, Object>> list = dam.exeQuery(qc);
		
		qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sb = new StringBuffer();
		
		if (list != null && list.size() > 0) {
			// 更新
			sb.append(" UPDATE TBIOT_CALLOUT SET ");
			
			sb.append(" CUST_ID = :cust_id, ");
			sb.append(" PROPOSER_NAME = :proposer_name, ");
			sb.append(" PROPOSER_BIRTH = TO_DATE(:proposer_birth, 'yyyyMMdd'), ");
			sb.append(" REPRESET_ID = :represet_id, ");
			sb.append(" REPRESET_NAME = :represet_name, ");
			sb.append(" REPRESET_BIRTH = TO_DATE(:represet_birth, 'yyyyMMdd'), ");
			sb.append(" C_TEL_NO = :c_tel_no, ");
			sb.append(" C_MOBILE = :c_mobile, ");
			sb.append(" C_TIME = :c_time, ");
			sb.append(" C_NEED_CALL_YN = :c_need_call_yn, ");
			sb.append(" C_CALL_TYPE = :c_call_type, ");
			
			sb.append(" INSURED_ID = :insured_id, ");
			sb.append(" INSURED_NAME = :insured_name, ");
			sb.append(" INSURED_BIRTH = TO_DATE(:insured_birth, 'yyyyMMdd'), ");
			sb.append(" I_REPRESET_ID = :i_represet_id, ");
			sb.append(" I_REPRESET_NAME = :i_represet_name, ");
			sb.append(" I_REPRESET_BIRTH = TO_DATE(:i_represet_birth, 'yyyyMMdd'), ");
			sb.append(" I_TEL_NO = :i_tel_no, ");
			sb.append(" I_MOBILE = :i_mobile, ");
			sb.append(" I_TIME = :i_time, ");
			sb.append(" I_NEED_CALL_YN = :i_need_call_yn, ");
			sb.append(" I_CALL_TYPE = :i_call_type, ");
			
			sb.append(" PAYER_ID = :payer_id, ");
			sb.append(" PAYER_NAME = :payer_name, ");
			sb.append(" PAYER_BIRTH = TO_DATE(:payer_birth, 'yyyyMMdd'), ");
			sb.append(" P_REPRESET_ID = :p_represet_id, ");
			sb.append(" P_REPRESET_NAME = :p_represet_name, ");
			sb.append(" P_REPRESET_BIRTH = TO_DATE(:p_represet_birth, 'yyyyMMdd'), ");
			sb.append(" P_TEL_NO = :p_tel_no, ");
			sb.append(" P_MOBILE = :p_mobile, ");
			sb.append(" P_TIME = :p_time, ");
			sb.append(" P_NEED_CALL_YN = :p_need_call_yn, ");
			sb.append(" P_CALL_TYPE = :p_call_type, ");
			
			sb.append(" SPECIAL_MEMO = :special_memo, ");
			sb.append(" VERSION = VERSION+1, ");
			sb.append(" MODIFIER = :loginID, ");
			sb.append(" LASTUPDATE = SYSDATE ");
			sb.append(" WHERE PREMATCH_SEQ = :prematch_seq ");
			
		} else {
			// 新增
			sb.append(" INSERT INTO TBIOT_CALLOUT ( ");
			sb.append(" PREMATCH_SEQ, STATUS, C_CALL_TYPE, I_CALL_TYPE, P_CALL_TYPE, ");
			sb.append(" INSPRD_TYPE, SPECIAL_MEMO, ");
			sb.append(" REPRESET_ID, REPRESET_NAME, REPRESET_BIRTH, ");
			sb.append(" I_REPRESET_ID, I_REPRESET_NAME, I_REPRESET_BIRTH, ");
			sb.append(" P_REPRESET_ID, P_REPRESET_NAME, P_REPRESET_BIRTH, ");
			sb.append(" C_NEED_CALL_YN, CUST_ID, 	PROPOSER_NAME, PROPOSER_BIRTH, C_TEL_NO, C_MOBILE, C_TIME, ");
			sb.append(" I_NEED_CALL_YN, INSURED_ID, INSURED_NAME,  INSURED_BIRTH,  I_TEL_NO, I_MOBILE, I_TIME, ");
			sb.append(" P_NEED_CALL_YN, PAYER_ID, 	PAYER_NAME,    PAYER_BIRTH,    P_TEL_NO, P_MOBILE, P_TIME, ");
			sb.append(" VERSION, CREATETIME, CREATOR, MODIFIER, LASTUPDATE ");
			sb.append(" ) VALUES ( ");
			sb.append(" :prematch_seq, '1', :c_call_type, :i_call_type, :p_call_type, ");
			sb.append(" :insprd_type, :special_memo, ");
			sb.append(" :represet_id, :represet_name, TO_DATE(:represet_birth, 'yyyyMMdd'), ");
			sb.append(" :i_represet_id, :i_represet_name, TO_DATE(:i_represet_birth, 'yyyyMMdd'), ");
			sb.append(" :p_represet_id, :p_represet_name, TO_DATE(:p_represet_birth, 'yyyyMMdd'), ");
			sb.append(" :c_need_call_yn, :cust_id, 	  :proposer_name, TO_DATE(:proposer_birth, 'yyyyMMdd'), :c_tel_no, :c_mobile, :c_time, ");
			sb.append(" :i_need_call_yn, :insured_id, :insured_name,  TO_DATE(:insured_birth, 'yyyyMMdd'), 	:i_tel_no, :i_mobile, :i_time, ");
			sb.append(" :p_need_call_yn, :payer_id,   :payer_name, 	  TO_DATE(:payer_birth, 'yyyyMMdd'), 	:p_tel_no, :p_mobile, :p_time, ");
			sb.append(" 0, SYSDATE, :loginID, :loginID, SYSDATE) ");
			
			qc.setObject("insprd_type", inputVO.getINSPRD_TYPE());
		}
		
		qc.setObject("prematch_seq", inputVO.getPREMATCH_SEQ());
		qc.setObject("special_memo", inputVO.getSPECIAL_MEMO());
		qc.setObject("represet_id", inputVO.getREPRESET_ID());
		qc.setObject("represet_name", inputVO.getREPRESET_NAME());
		qc.setObject("represet_birth", inputVO.getREPRESET_BIRTH() != null ? sdf.format(inputVO.getREPRESET_BIRTH()) : null);
		qc.setObject("i_represet_id", inputVO.getI_REPRESET_ID());
		qc.setObject("i_represet_name", inputVO.getI_REPRESET_NAME());
		qc.setObject("i_represet_birth", inputVO.getI_REPRESET_BIRTH() != null ? sdf.format(inputVO.getI_REPRESET_BIRTH()) : null);
		qc.setObject("p_represet_id", inputVO.getP_REPRESET_ID());
		qc.setObject("p_represet_name", inputVO.getP_REPRESET_NAME());
		qc.setObject("p_represet_birth", inputVO.getP_REPRESET_BIRTH() != null ? sdf.format(inputVO.getP_REPRESET_BIRTH()) : null);
		qc.setObject("cust_id", inputVO.getCUST_ID());
		qc.setObject("proposer_name", inputVO.getPROPOSER_NAME());
		qc.setObject("proposer_birth", inputVO.getPROPOSER_BIRTH() != null ? sdf.format(inputVO.getPROPOSER_BIRTH()) : null);
		qc.setObject("c_tel_no", inputVO.getC_TEL_NO());
		qc.setObject("c_mobile", inputVO.getC_MOBILE() != null ? inputVO.getC_MOBILE().trim() : null);
		qc.setObject("c_time", inputVO.getC_TIME());
		qc.setObject("insured_id", inputVO.getINSURED_ID());
		qc.setObject("insured_name", inputVO.getINSURED_NAME());
		qc.setObject("insured_birth", inputVO.getINSURED_BIRTH() != null ? sdf.format(inputVO.getINSURED_BIRTH()) : null);
		qc.setObject("i_tel_no", inputVO.getI_TEL_NO());
		qc.setObject("i_mobile", inputVO.getI_MOBILE() != null ? inputVO.getI_MOBILE().trim() : null);
		qc.setObject("i_time", inputVO.getI_TIME());
		qc.setObject("payer_id", inputVO.getPAYER_ID());
		qc.setObject("payer_name", inputVO.getPAYER_NAME());
		qc.setObject("payer_birth", inputVO.getPAYER_BIRTH() != null ? sdf.format(inputVO.getPAYER_BIRTH()) : null);
		qc.setObject("p_tel_no", inputVO.getP_TEL_NO());
		qc.setObject("p_mobile", inputVO.getP_MOBILE() != null ? inputVO.getP_MOBILE().trim() : null);
		qc.setObject("p_time", inputVO.getP_TIME());
		qc.setObject("loginID", loginID);
		qc.setObject("c_call_type", inputVO.getC_CALL_TYPE());
		qc.setObject("i_call_type", inputVO.getI_CALL_TYPE());
		qc.setObject("p_call_type", inputVO.getP_CALL_TYPE());
		qc.setObject("c_need_call_yn", inputVO.getC_NEED_CALL_YN());
		qc.setObject("i_need_call_yn", inputVO.getI_NEED_CALL_YN());
		qc.setObject("p_need_call_yn", inputVO.getP_NEED_CALL_YN());
		
		qc.setQueryString(sb.toString());
		dam.exeUpdate(qc);
		this.sendRtnObject(null);
	}
	
	public void getAge(Object body, IPrimitiveMap header) throws JBranchException {
		IOT400InputVO inputVO = (IOT400InputVO) body;
		IOT400OutputVO outputVO = new IOT400OutputVO();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		dam = this.getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		sb.append(" SELECT TRUNC(MONTHS_BETWEEN(SYSDATE, TO_DATE(:proposer_birth, 'YYYYMMDD')) / 12) AS AGE FROM dual ");
		qc.setObject("proposer_birth", inputVO.getPROPOSER_BIRTH() != null ? sdf.format(inputVO.getPROPOSER_BIRTH()) : null);
		qc.setQueryString(sb.toString());
		outputVO.setResultList(dam.exeQuery(qc));
		this.sendRtnObject(outputVO);
	}
	
	// 送出預約
	public void reserve(Object body, IPrimitiveMap header) throws JBranchException {
		IOT400InputVO inputVO = (IOT400InputVO) body;
		String loginID = (String) getUserVariable(FubonSystemVariableConsts.LOGINID);
		String prematch_seq = inputVO.getPREMATCH_SEQ();
		dam = this.getDataAccessManager();
		
		/**
		 * 更新 TBIOT_CALLOUT.STATUS
		 * 檢查電訪狀態：
		 *  1.未申請
		 *  2.電訪預約中
		 *  3.電訪處理中
		 *  4.電訪成功
		 *  5.電訪未成功
		 *  6.電訪疑義
		 *  7.取消電訪
		 *  8.退件處理-契撤
		 * **/
		this.updateStatus("2", prematch_seq, loginID);
		
		// 申請完成寫入 TBCAM_CAL_SALES_TASK
		TBCAM_CAL_SALES_TASKVO vo = null;
		// 取得派件人員
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> callPersonMap = xmlInfo.doGetVariable("CALLOUT.CALL_PERSON", FormatHelper.FORMAT_3);
		String assign = callPersonMap.get("ASSIGN");
		String[] empIDs = assign.split(",");
		for (String empID : empIDs) {
			vo = new TBCAM_CAL_SALES_TASKVO();
			BigDecimal seqNo = new BigDecimal(getSEQ());
			while(checkID(seqNo)) {
				seqNo = new BigDecimal(getSEQ());
			}
			vo.setSEQ_NO(seqNo);
			vo.setEMP_ID(empID);
			vo.setCUST_ID(inputVO.getCUST_ID());
			vo.setTASK_DATE(new Timestamp(System.currentTimeMillis()));
			vo.setTASK_STIME("0900");
			vo.setTASK_ETIME("1800");
			vo.setTASK_TITLE("您有一筆案件需要進行派件");
			vo.setTASK_MEMO("您有一筆案件需要進行派件");
			vo.setTASK_SOURCE("A9");
			vo.setTASK_TYPE("03");
			vo.setTASK_STATUS("I");
			dam.create(vo);			
		}
		this.sendRtnObject(null);
	}
	
	// 檢視完成：更新 TBIOT_CALLOUT.REVIEW_STATUS = "OK"
	public void reviewOK(Object body, IPrimitiveMap header) throws JBranchException {
		IOT400InputVO inputVO = (IOT400InputVO) body;
		String loginID = (String) getUserVariable(FubonSystemVariableConsts.LOGINID);
		String prematch_seq = inputVO.getPREMATCH_SEQ();
		
		dam = this.getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		
		sb.append(" UPDATE TBIOT_CALLOUT SET ");
		sb.append(" REVIEW_STATUS = 'OK', ");
		sb.append(" MODIFIER = :loginID, ");
		sb.append(" LASTUPDATE = SYSDATE ");
		sb.append(" WHERE PREMATCH_SEQ = :prematch_seq ");
		
		qc.setObject("loginID", loginID);
		qc.setObject("prematch_seq", prematch_seq);
		qc.setQueryString(sb.toString());
		dam.exeUpdate(qc);
		
		this.sendRtnObject(null);
	}
	
	// 退回：中控-派件人員，點選退回按鈕，產生小視窗供電訪人員輸入退回原因。電訪狀態自動壓回「未申請」。電訪申請日期請異動為空白
	public void reject(Object body, IPrimitiveMap header) throws JBranchException {
		IOT400InputVO inputVO = (IOT400InputVO) body;
		String loginID = (String) getUserVariable(FubonSystemVariableConsts.LOGINID);
		String prematch_seq = inputVO.getPREMATCH_SEQ();
		
		dam = this.getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		
		sb.append(" INSERT INTO TBIOT_CALLOUT_REJECT ( ");
		sb.append(" PREMATCH_SEQ, SEQ, REJECT_REASON, ");
		sb.append(" VERSION, CREATETIME, CREATOR, MODIFIER, LASTUPDATE ");
		sb.append(" ) VALUES ( ");
		sb.append(" :prematch_seq, :seq, :reject_reason, ");
		sb.append(" 0, SYSDATE, :loginID, :loginID, SYSDATE) ");
		
		qc.setObject("prematch_seq", prematch_seq);
		qc.setObject("seq", this.getRejectSeq());
		qc.setObject("reject_reason", inputVO.getREJECT_REASON());
		qc.setObject("loginID", loginID);
		qc.setQueryString(sb.toString());
		dam.exeUpdate(qc);
		
		/**
		 * 更新 TBIOT_CALLOUT.STATUS
		 * 檢查電訪狀態：
		 *  1.未申請
		 *  2.電訪預約中
		 *  3.電訪處理中
		 *  4.電訪成功
		 *  5.電訪未成功
		 *  6.電訪疑義
		 *  7.取消電訪
		 *  8.退件處理-契撤
		 * **/
//		this.updateStatus("1", prematch_seq, loginID);
		
		qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sb = new StringBuffer();
		// 『退回』需將檢視狀態改為未檢視，並且刪除已派件之電訪人員
		sb.append(" UPDATE TBIOT_CALLOUT SET ");
		sb.append(" STATUS = '1', ");
		sb.append(" APPLY_DATE = NULL, ");
		sb.append(" REVIEW_STATUS = NULL, ");
		sb.append(" CALL_PERSON = NULL, ");
		
		// #2172：電訪預約方便聯絡時間拿掉 18：00~20：00
		// 若是在途件原有勾選時段18：00~20：00，當被派件人員退回(狀態改為未申請)時，由理專重新執行電訪預約，時段請拿掉18：00~20：00不顯示
		sb.append(" C_TIME  = CASE WHEN C_TIME  IS NOT NULL THEN SUBSTR(C_TIME , 1, 3) || '0' ELSE NULL END, ");
		sb.append(" C_TIME2 = CASE WHEN C_TIME2 IS NOT NULL THEN SUBSTR(C_TIME2, 1, 3) || '0' ELSE NULL END, ");
		sb.append(" I_TIME  = CASE WHEN I_TIME  IS NOT NULL THEN SUBSTR(I_TIME , 1, 3) || '0' ELSE NULL END, ");
		sb.append(" I_TIME2 = CASE WHEN I_TIME2 IS NOT NULL THEN SUBSTR(I_TIME2, 1, 3) || '0' ELSE NULL END, ");
		sb.append(" P_TIME  = CASE WHEN P_TIME  IS NOT NULL THEN SUBSTR(P_TIME , 1, 3) || '0' ELSE NULL END, ");
		sb.append(" P_TIME2 = CASE WHEN P_TIME2 IS NOT NULL THEN SUBSTR(P_TIME2, 1, 3) || '0' ELSE NULL END, ");
		
		sb.append(" MODIFIER = :loginID, ");
		sb.append(" LASTUPDATE = SYSDATE ");
		sb.append(" WHERE PREMATCH_SEQ = :prematch_seq ");
		
		qc.setObject("loginID", loginID);
		qc.setObject("prematch_seq", prematch_seq);
		qc.setQueryString(sb.toString());
		dam.exeUpdate(qc);
		
		this.sendRtnObject(null);
	}
	
	/**
	 * 點選時檢核「電訪作業檔」，若有相同案件編號已存在：
	 * 符合電訪狀態=2.電訪預約中、3.電訪處理中、5.電訪未成功、6.電訪疑義、8.退件處理-契撤時，
	 * 產生小視窗提醒：「相同案件編號案件已有電訪記錄，請確認!」，無法執行電訪預約/取消作業
	 * 若案件電訪狀態=1.未申請、7.取消電訪，可執行電訪預約/取消作業。
	 * 
	 * WMS-CR-20240805-01_增檢核商品完訓日期調整保險電訪作業功能及投資型案件另外產生送件明細表
	 * 新增加：4.電訪成功 ==> 也不可再預約電訪
	 * **/
	public void checkCaseID(Object body, IPrimitiveMap header) throws JBranchException {
		IOT400InputVO inputVO = (IOT400InputVO) body;
		IOT400OutputVO outputVO = new IOT400OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		sb.append(" SELECT S01.CASE_ID, S01.PREMATCH_SEQ FROM TBIOT_PREMATCH S01 ");
		sb.append(" LEFT JOIN TBIOT_CALLOUT S05 ON S01.PREMATCH_SEQ = S05.PREMATCH_SEQ ");
		sb.append(" WHERE S01.CASE_ID = :case_id ");
		sb.append(" AND S05.STATUS IN ('2', '3', '4', '5', '6', '8') ");
		sb.append(" AND S01.PREMATCH_SEQ <> :prematch_seq ");
		qc.setObject("case_id", inputVO.getCASE_ID());
		qc.setObject("prematch_seq", inputVO.getPREMATCH_SEQ());
		qc.setQueryString(sb.toString());
		outputVO.setResultList(dam.exeQuery(qc));
		this.sendRtnObject(outputVO);
	}
	
	private void updateStatus(String status, String prematch_seq, String loginID) throws JBranchException {
		dam = this.getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		
		/**
		 * 更新 TBIOT_CALLOUT.STATUS
		 * 檢查電訪狀態：
		 *  1.未申請
		 *  2.電訪預約中
		 *  3.電訪處理中
		 *  4.電訪成功
		 *  5.電訪未成功
		 *  6.電訪疑義
		 *  7.取消電訪
		 *  8.退件處理-契撤
		 * **/
		sb.append(" UPDATE TBIOT_CALLOUT SET ");
		sb.append(" STATUS = :status, ");
		if ("1".equals(status)) {
			sb.append(" APPLY_DATE = NULL, ");
		} else if ("2".equals(status)) {
			sb.append(" APPLY_DATE = SYSDATE, ");
		}
		sb.append(" MODIFIER = :loginID, ");
		sb.append(" LASTUPDATE = SYSDATE ");
		sb.append(" WHERE PREMATCH_SEQ = :prematch_seq ");
		
		qc.setObject("status", status);
		qc.setObject("loginID", loginID);
		qc.setObject("prematch_seq", prematch_seq);
		qc.setQueryString(sb.toString());
		dam.exeUpdate(qc);
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
	
	/**
	 * 取得序號
	 * @return String : SEQ
	 * @throws JBranchException
	 */
	private String getRejectSeq() throws JBranchException {
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT TBIOT_CALLOUT_REJECT_SEQ.nextval AS SEQ FROM DUAL ");
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> SEQLIST = dam.exeQuery(queryCondition);

		return ObjectUtils.toString(SEQLIST.get(0).get("SEQ"));
	}
}
