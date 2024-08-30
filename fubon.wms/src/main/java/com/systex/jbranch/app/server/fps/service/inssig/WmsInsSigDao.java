package com.systex.jbranch.app.server.fps.service.inssig;

import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import javax.sql.rowset.serial.SerialBlob;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import com.systex.jbranch.app.common.fps.table.TBIOT_MAINVO;
import com.systex.jbranch.app.common.fps.table.TBIOT_MAPP_PDFVO;
import com.systex.jbranch.comutil.collection.GenericMap;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.server.bizLogic.BizLogic;

@Repository("WmsInsSigDao")
public class WmsInsSigDao extends BizLogic implements WmsInsSigDaoInf{
	/**
	 * 取得視訊投保待簽署清單
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String , Object>> querySignList(GenericMap paramMap) throws DAOException, JBranchException {
		//保險文件編號
		String insId = paramMap.getNotNullStr("insId");
		//案件編號
		String caseId = paramMap.getNotNullStr("caseId");
		//分行送件批號
		String opBatchNo = paramMap.getNotNullStr("opBatchNo");
		//鍵機日起日
		String keyinDateS = paramMap.getNotNullStr("keyinDateS");
		//鍵機日迄日
		String keyinDateE = paramMap.getNotNullStr("keyinDateE");
		//要保人ID
		String custId = paramMap.getNotNullStr("custId");
		//被保人ID
		String insuredId = paramMap.getNotNullStr("insuredId");
		
		StringBuffer sb = new StringBuffer();
		sb.append(" SELECT 					");	//
		sb.append(" 	M.INS_KEYNO			");	//保險進件主檔主鍵
		sb.append("    ,M.INS_KIND			");	//保險種類
		sb.append("    ,M.INS_ID			");	//保險文件編號
		sb.append("    ,M.CASE_ID			");	//案件編號
		sb.append("    ,M.STATUS			");	//文件狀態
		sb.append("    ,M.CUST_ID			");	//要保人ID
		sb.append("    ,M.PROPOSER_NAME		");	//要保人NAME
		sb.append("    ,M.INSURED_ID		");	//被保人ID
		sb.append("    ,M.INSURED_NAME		");	//被保人NAME
		sb.append("    ,M.INSPRD_ID	　　		");	//險種代碼
		sb.append("    ,M.INSPRD_NAME	　　");	//險種名稱
		sb.append("    ,M.REAL_PREMIUM		");	//實收保費
		sb.append("    ,M.CURR_CD 			");	//幣別
		sb.append("    ,M.OP_BATCH_NO 		");	//分行送件批號
		sb.append("    ,M.BEF_SIGN_OPRID 	");	//總行行助簽收人(簽署前)
		sb.append("    ,TO_CHAR(M.KEYIN_DATE, 'YYYYMMDD') AS KEYIN_DATE ");	//鍵機日
		sb.append("    ,A.NP_REASON AS FC_NP_REASON 	");	//理專視訊簽單不通過原因
		sb.append("    ,B.NP_REASON AS BRM_NP_REASON 	");	//分行主管審核視訊簽單不通過原因
		sb.append("    ,C.NP_REASON AS HDM_NP_REASON 	");	//質檢人員視訊簽單不通過原因
		sb.append("	   ,CASE WHEN M.PREMIUM_TRANSSEQ IS NOT NULL OR M.I_PREMIUM_TRANSSEQ IS NOT NULL OR M.P_PREMIUM_TRANSSEQ IS NOT NULL THEN 'Y' ELSE 'N' END AS ECALL_DONE "); //保險資金電訪欄位註記
		sb.append(" FROM VWIOT_MAIN M       ");	//
		sb.append(" LEFT JOIN TBIOT_MAPPVIDEO_CHKLIST A ON A.PREMATCH_SEQ = M.PREMATCH_SEQ AND A.CHK_STEP = '1' AND A.CHK_CODE = '97' ");	//理專不通過原因
		sb.append(" LEFT JOIN TBIOT_MAPPVIDEO_CHKLIST B ON B.PREMATCH_SEQ = M.PREMATCH_SEQ AND B.CHK_STEP = '2' AND B.CHK_CODE = '97' ");	//分行主管不通過原因
		sb.append(" LEFT JOIN TBIOT_MAPPVIDEO_CHKLIST C ON C.PREMATCH_SEQ = M.PREMATCH_SEQ AND C.CHK_STEP = '3' AND C.CHK_CODE = '97' ");	//質檢人員不通過原因
		sb.append(" WHERE M.INS_KIND = '1' "); //壽險
		sb.append(" 	AND ((M.STATUS IN ('60', '70', '80') "); //總行行助點收送件(簽署後), 傳輸人壽批次產生, 人壽點收資料回饋
		sb.append("			  AND M.INS_SIGN_OPRID IS NULL AND M.INS_SIGN_DATE IS NULL AND M.INS_SIGN_YN IS NULL) "); //簽署人資料為空
		sb.append("			OR M.STATUS = '62') "); //總行行助退件(簽署後)
		sb.append(" 	AND SUBSTR(M.OP_BATCH_NO, 1, 1) = 'S' "); //視訊簽單批號第一碼為S
		sb.append(" 	AND (:insId IS NULL OR M.INS_ID = :insId) ");
		sb.append(" 	AND (:caseId IS NULL OR M.CASE_ID = :caseId) ");
		sb.append(" 	AND (:opBatchNo IS NULL OR M.OP_BATCH_NO = :opBatchNo) ");
		sb.append(" 	AND (:keyinDateS IS NULL OR TRUNC(M.KEYIN_DATE) >= TO_DATE(:keyinDateS, 'YYYYMMDD')) ");
		sb.append(" 	AND (:keyinDateE IS NULL OR TRUNC(M.KEYIN_DATE) <= (TO_DATE(:keyinDateE, 'YYYYMMDD') + 1)) ");
		sb.append(" 	AND (:custId IS NULL OR M.CUST_ID = :custId) ");
		sb.append(" 	AND (:insuredId IS NULL OR M.INSURED_ID = :insuredId) ");
		
		return getDataAccessManager().exeQuery(genDefaultQueryConditionIF()
				.setQueryString(sb.toString())
				.setObject("insId" , StringUtils.isBlank(insId) ? null : insId)
				.setObject("caseId" , StringUtils.isBlank(caseId) ? null : caseId)
				.setObject("opBatchNo" , StringUtils.isBlank(opBatchNo) ? null : opBatchNo)
				.setObject("keyinDateS" , StringUtils.isBlank(keyinDateS) ? null : keyinDateS)
				.setObject("keyinDateE" , StringUtils.isBlank(keyinDateE) ? null : keyinDateE)
				.setObject("custId" , StringUtils.isBlank(custId) ? null : custId)
				.setObject("insuredId" , StringUtils.isBlank(insuredId) ? null : insuredId)
			);
	}
	
	/**
	 * 人壽端回傳案件簽署結果，資料回寫
	 */
	public void updateCaseStatus(Map<String , Object> paramMap) throws Exception {
		SimpleDateFormat sdFormat = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
		
		BigDecimal insKeyNo = new BigDecimal(paramMap.get("insKeyNo").toString());	//保險進件主檔主鍵
		String isPass = ObjectUtils.toString(paramMap.get("isPass"));		//簽署狀態 Y/N
		String signOprId = ObjectUtils.toString(paramMap.get("signOprId"));	//簽署人ID
		String signDate = ObjectUtils.toString(paramMap.get("signDate"));	//簽署日期時間 YYYYMMDD HH24:MI:SS
		String rejReason = ObjectUtils.toString(paramMap.get("rejReason"));	//被退件原因代碼
		String rejReasonOther = ObjectUtils.toString(paramMap.get("rejReasonOther"));	//被退件原因為其他
		
		TBIOT_MAINVO mvo = (TBIOT_MAINVO)getDataAccessManager().findByPKey(TBIOT_MAINVO.TABLE_UID, insKeyNo);
		if(!StringUtils.equals("Y", isPass)) mvo.setSTATUS(new BigDecimal(62)); //退件:總行行助退件(簽署後)
		mvo.setINS_SIGN_OPRID(signOprId);
		mvo.setINS_SIGN_DATE(new Timestamp(sdFormat.parse(signDate).getTime()));
		mvo.setINS_SIGN_YN(isPass);
		mvo.setREJ_REASON(StringUtils.equals("N", isPass) ? rejReason : "");
		mvo.setREJ_REASON(StringUtils.equals("N", isPass) ? rejReasonOther : "");
		
		getDataAccessManager().update(mvo);
	}
	
	/**
	 * 人壽端回傳已簽署案件PDF，回寫進TBIOT_MAPP_PDF
	 */
	public void updateCasePDF(GenericMap paramMap) throws Exception {
		//案件編號
		String caseId = paramMap.getNotNullStr("caseId");
		//PDF檔案
		MultipartFile pdfFile = paramMap.get("pdfFile");
		
		TBIOT_MAPP_PDFVO tmpdf = (TBIOT_MAPP_PDFVO)getDataAccessManager().findByPKey(TBIOT_MAPP_PDFVO.TABLE_UID, caseId);
		Blob blob = new SerialBlob(pdfFile.getBytes());
		tmpdf.setPDF_FILE(blob);
		getDataAccessManager().update(tmpdf);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Map<String , Object>> queryForJwtString(String jwtstring) throws DAOException, JBranchException {
		String queryStr = " select * from TBSYS_SSO_INFO where VN_SYS_ADDR = :JWT_STRING ";
		
		return getDataAccessManager().exeQuery(genDefaultQueryConditionIF()
			.setQueryString(queryStr)
			.setObject("JWT_STRING" , jwtstring)
		);
	}
	
	/** 刪除過時資料，保留2天 **/
	public int delTbsysSsoInfoTimeOutData() throws DAOException, JBranchException{
		String queryStr = "DELETE FROM TBSYS_SSO_INFO where SYS_CODE = 'INS_SIG' AND TRUNC(CREATETIME + 1) < TRUNC(SYSDATE)";
		return getDataAccessManager().exeUpdate(genDefaultQueryConditionIF().setQueryString(queryStr));
	}
}
