package com.systex.jbranch.app.server.fps.iot120;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialClob;
import javax.sql.rowset.serial.SerialException;

import com.systex.jbranch.fubon.commons.PdfConfigVO;
import com.systex.jbranch.fubon.commons.PdfUtil;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.common.fps.table.TBIOT_DOC_CHKPK;
import com.systex.jbranch.app.common.fps.table.TBIOT_DOC_CHKVO;
import com.systex.jbranch.app.common.fps.table.TBIOT_MAINVO;
import com.systex.jbranch.app.common.fps.table.TBIOT_MAIN_PDFVO;
import com.systex.jbranch.app.common.fps.table.TBIOT_MAPP_PDFVO;
import com.systex.jbranch.app.common.fps.table.TBIOT_RIDER_DTLVO;
import com.systex.jbranch.app.common.fps.table.TBSYS_WEB_SERVICE_LOGVO;
import com.systex.jbranch.app.server.fps.iot400.IOT400InputVO;
import com.systex.jbranch.app.server.fps.iot400.IOT400OutputVO;
import com.systex.jbranch.app.server.fps.iot920.FirstBuyDataVO;
import com.systex.jbranch.app.server.fps.iot920.FirstBuyInputVO;
import com.systex.jbranch.app.server.fps.iot920.INSIDDataVO;
import com.systex.jbranch.app.server.fps.iot920.INSIDInputVOinputVO;
import com.systex.jbranch.app.server.fps.iot920.IOT920;
import com.systex.jbranch.app.server.fps.iot920.chk_AbInputVO;
import com.systex.jbranch.app.server.fps.iot920.chk_AbOutputVO;
import com.systex.jbranch.comutil.collection.GenericMap;
import com.systex.jbranch.comutil.parse.JsonUtil;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.fubon.commons.http.client.HttpClientJsonUtils;
import com.systex.jbranch.fubon.commons.http.client.callback.DefHeaderCallBack;
import com.systex.jbranch.fubon.webservice.ws.FBMAPPService.GetMappCase;
import com.systex.jbranch.fubon.webservice.ws.FBMAPPService.GetMappCaseSoapUtils;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.errHandle.NotFoundException;
import com.systex.jbranch.platform.common.json.parser.JSONParser;
import com.systex.jbranch.platform.common.report.ReportIF;
import com.systex.jbranch.platform.common.report.factory.ReportFactory;
import com.systex.jbranch.platform.common.report.generator.ReportGeneratorIF;
import com.systex.jbranch.platform.common.report.reportdata.ReportData;
import com.systex.jbranch.platform.common.report.reportdata.ReportDataIF;
import com.systex.jbranch.platform.common.util.ObjectUtil;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.common.util.StringUtil;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * IOT120
 *
 * @author Jimmy
 * @date 2016/08/25
 * @spec null
 */
@Component("iot120")
@Scope("request")
public class IOT120 extends FubonWmsBizLogic {
	Logger logger = LoggerFactory.getLogger(this.getClass());
	DataAccessManager dam_obj;

	public void retrieve_data(Object body, IPrimitiveMap<Object> header) throws JBranchException, ParseException {
		IOT120InputVO inputVO = (IOT120InputVO) body;
		IOT120OutputVO outputVO = new IOT120OutputVO();

		dam_obj = this.getDataAccessManager();
		QueryConditionIF qc =
				dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuilder sb = new StringBuilder();

		sb.append(" Select A.REG_TYPE,A.INS_ID, A.CASE_ID,to_char(A.KEYIN_DATE,'YYYY/MM/DD HH:MI:SS') KEYIN_DATE,A.WRITE_REASON,A.WRITE_REASON_OTH,A.BRANCH_NBR,P.RECRUIT_ID,P.AO_CODE,A.REF_CON_ID,A.IS_EMP,P.INSURED_ID, ");
		sb.append(" P.INSURED_NAME,P.INSURED_CM_FLAG,P.CUST_ID,P.PROPOSER_NAME,P.PROPOSER_BIRTH,P.PROPOSER_CM_FLAG, ");
		sb.append(" P.REPRESET_ID,P.RLT_BT_PROREP,P.REPRESET_CM_FLAG,P.INSPRD_ID,PRD.INSPRD_ANNUAL,P.REAL_PREMIUM,P.BASE_PREMIUM,PRD.PAY_TYPE,P.MOP2,A.FIRST_PAY_WAY,PRD.CURR_CD,A.APPLY_DATE, ");
		sb.append(" A.AB_TRANSSEQ,P.MATCH_DATE,P.CUST_RISK,P.CUST_RISK_DUE as KYC_DUE_DATE,PRD.SPECIAL_CONDITION,A.STATUS,A.OP_DATE,A.QC_ADD,A.QC_ERASER,A.QC_ANC_DOC,A.QC_STAMP,A.PROPOSER_TRANSSEQ, ");
		sb.append(" A.INSURED_TRANSSEQ,A.TERMINATED_INC,A.SIGN_INC,P.INSPRD_KEYNO,A.INS_KEYNO,PRD.INSPRD_NAME,PRD.INSPRD_TYPE,PRD.AB_EXCH_RATE, PRD.NEED_MATCH, ");
		sb.append(" A.LOAN_PRD_YN, A.QC_IMMI, A.PREMATCH_SEQ, A.GUILD_RPT_DATE, A.NOT_PASS_REASON, A.QC_PROPOSER_CHG, ");
		sb.append(" A.PREMIUM_USAGE, A.PAY_WAY, P.LOAN_SOURCE_YN, A.DOC_KEYIN_DATE, P.PAYER_ID, P.PAYER_NAME, P.RLT_BT_PROPAY, P.PAYER_CM_FLAG , P.AML, P.PRECHECK, ");
		sb.append(" P.PROPOSER_INCOME1, P.C_KYC_INCOME, P.PROPOSER_INCOME3, P.INSURED_INCOME1, P.I_KYC_INCOME, P.INSURED_INCOME3, ");
		sb.append(" P.LOAN_CHK1_YN, P.LOAN_CHK2_YN, P.LOAN_CHK3_YN, P.CD_CHK_YN, P.STATUS AS PREMATCH_STATUS, ");
		sb.append(" P.C_LOAN_CHK1_YN, P.C_LOAN_CHK2_YN, P.C_LOAN_CHK3_YN, P.C_CD_CHK_YN, P.I_LOAN_CHK1_YN, P.I_LOAN_CHK2_YN, P.I_LOAN_CHK3_YN, P.I_CD_CHK_YN, ");
		sb.append(" P.CONTRACT_END_YN, P.S_INFITEM_LOAN_YN, P.PROPOSER_WORK, P.INSURED_WORK, P.C_LOAN_APPLY_DATE, P.C_PREM_DATE, P.I_LOAN_APPLY_DATE, P.P_LOAN_APPLY_DATE, ");
		sb.append(" P.C_LOAN_APPLY_YN, P.I_LOAN_APPLY_YN, P.P_LOAN_APPLY_YN, P.LOAN_SOURCE2_YN, P.AB_SENIOR_YN, P.C_SALE_SENIOR_YN, P.I_SALE_SENIOR_YN, P.P_SALE_SENIOR_YN, ");
		sb.append(" P.C_SALE_SENIOR_TRANSSEQ, P.I_SALE_SENIOR_TRANSSEQ, P.P_SALE_SENIOR_TRANSSEQ, ");
		sb.append(" P.INSURED_BIRTH, P.PAYER_BIRTH, A.QC_APEC, A.QC_LOAN_CHK, A.QC_SIGNATURE, P.C_SENIOR_PVAL, P.COMPANY_NUM, B.CNAME AS INS_COM_NAME, P.FB_COM_YN, P.CANCEL_CONTRACT_YN, ");
		sb.append(" CASE WHEN D.C_PREMIUM_TRANSSEQ IS NOT NULL THEN D.C_PREMIUM_TRANSSEQ ELSE C.PREMIUM_TRANSSEQ END AS PREMIUM_TRANSSEQ, ");
		sb.append(" CASE WHEN D.I_PREMIUM_TRANSSEQ IS NOT NULL THEN D.I_PREMIUM_TRANSSEQ ELSE C.I_PREMIUM_TRANSSEQ END AS I_PREMIUM_TRANSSEQ, ");
		sb.append(" CASE WHEN D.P_PREMIUM_TRANSSEQ IS NOT NULL THEN D.P_PREMIUM_TRANSSEQ ELSE C.P_PREMIUM_TRANSSEQ END AS P_PREMIUM_TRANSSEQ, ");
		sb.append(" P.BUSINESS_REL, A.PAY_SERV_RETURN_CODE, A.PAY_SERV_REMIT_NATNO, A.REMIT_COUNTRY_FLAG, P.MAPPVIDEO_YN, P.DIGITAL_AGREESIGN_YN, ");
		sb.append(" NVL(A.FUND_VERIFY_PDF_YN, 'N') AS FUND_VERIFY_PDF_YN, NVL(A.FUND_VERIFY_PAPER_YN, 'N') AS FUND_VERIFY_PAPER_YN, NVL(A.NO_PAPER_YN, 'N') AS NO_PAPER_YN ");
		sb.append(" From TBIOT_MAIN A ");
		sb.append(" LEFT OUTER JOIN TBIOT_PREMATCH P ON P.PREMATCH_SEQ = A.PREMATCH_SEQ ");
		sb.append(" LEFT OUTER JOIN TBPRD_INS_MAIN PRD ON PRD.INSPRD_KEYNO = P.INSPRD_KEYNO ");
		sb.append(" LEFT JOIN TBJSB_INS_PROD_COMPANY B ON B.SERIALNUM = P.COMPANY_NUM ");
		sb.append(" LEFT JOIN TBIOT_TRANSSEQ C ON C.PREMATCH_SEQ = A.PREMATCH_SEQ AND C.UPDATE_STATUS = 'UP' ");
		sb.append("    AND C.LASTUPDATE = (SELECT MAX(LASTUPDATE) FROM TBIOT_TRANSSEQ WHERE PREMATCH_SEQ = A.PREMATCH_SEQ AND UPDATE_STATUS = 'UP') ");
		sb.append(" LEFT JOIN TBIOT_CALLOUT D ON D.PREMATCH_SEQ = A.PREMATCH_SEQ AND D.STATUS = '4' "); //電訪成功
		sb.append(" Where A.INS_KEYNO = :in_INSKEYNO ");

		qc.setObject("in_INSKEYNO", inputVO.getINS_KEYNO());

		qc.setQueryString(sb.toString());
		outputVO.setINS_INFORMATION(dam_obj.exeQuery(qc));
		Map<String, Object> list = outputVO.getINS_INFORMATION().get(0);

		String prematchSeq = "";
		if(list != null) {
			prematchSeq = ObjectUtils.toString(list.get("PREMATCH_SEQ"));

			if(!StringUtils.equals(list.get("PREMATCH_STATUS").toString(), "3")) {
				throw new APException("此適合度檢核編號尚未完成主管覆核");
			}
		}

		// 客戶風險及適配日
		dam_obj = this.getDataAccessManager();
		qc = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sb = new StringBuilder();
		sb.append(" select MATCH_DATE,CUST_RISK from TBIOT_PREMATCH ");
		sb.append(" where CUST_ID = :cust_id and INSPRD_ID = :insprd_id ");
		sb.append(" and trunc(match_date) = trunc(:apply_date) order by CREATETIME DESC ");
		qc.setObject("cust_id", list.get("CUST_ID").toString());
		qc.setObject("insprd_id", list.get("INSPRD_ID").toString());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date apply_date = sdf.parse(list.get("APPLY_DATE").toString());
		qc.setObject("apply_date", apply_date);
		qc.setQueryString(sb.toString());
		outputVO.setPreMatchList(dam_obj.exeQuery(qc));

		// 投資型連結標的清單
		dam_obj = this.getDataAccessManager();
		qc = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sb = new StringBuilder();
		sb.append(" select b.TARGET_ID,b.LINKED_NAME,a.LINK_PCT as ALLOCATION_RATIO,b.PRD_RISK,a.INS_KEYNO,a.PRD_LK_KEYNO ");
		sb.append(" from TBIOT_FUND_LINK a,TBPRD_INS_LINKING b ");
		sb.append(" where a.PRD_LK_KEYNO = b.KEY_NO and (a.INS_KEYNO = :ins_keyno OR a.PREMATCH_SEQ  = :prematchSeq) ");
		qc.setObject("ins_keyno", inputVO.getINS_KEYNO());
		qc.setObject("prematchSeq", prematchSeq);
		qc.setQueryString(sb.toString());
		outputVO.setINVESTList(dam_obj.exeQuery(qc));

		// 附約資料
		dam_obj = this.getDataAccessManager();
		qc = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sb = new StringBuilder();
		sb.append(" select a.RIDER_DTL_KEYNO,a.INSPRD_KEYNO,b.INSPRD_ID as DTL_INSPRD_ID,b.INSPRD_NAME,a.INSURED_NAME,a.RLT_WITH_INSURED,b.INSPRD_ANNUAL, ");
		sb.append(" TO_CHAR(a.PREMIUM,'FM999,999,999,999') as PREMIUM ");// 顯示千分位符號
		sb.append(" from TBIOT_RIDER_DTL a,TBPRD_INS_MAIN b ");
		sb.append(" where a.INSPRD_KEYNO = b.INSPRD_KEYNO ");
		sb.append(" and a.INS_KEYNO = :in_INSKEYNO ");
		sb.append(" order by a.RIDER_DTL_KEYNO ");
		qc.setObject("in_INSKEYNO", inputVO.getINS_KEYNO());
		qc.setQueryString(sb.toString());
		outputVO.setINS_RIDER_DTLList(dam_obj.exeQuery(qc));
		
		//保險資金證明文件
		dam_obj = this.getDataAccessManager();
		qc = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		qc.setQueryString("SELECT * FROM TBIOT_MAIN_PDF WHERE INS_KEYNO = :inskeyno ");
		qc.setObject("inskeyno", inputVO.getINS_KEYNO());
		outputVO.setFileList(dam_obj.exeQuery(qc));
		
		sendRtnObject(outputVO);
	}

	// 特殊條件
	public void Ins_spcnd(Object body, IPrimitiveMap<Object> header)
			throws JBranchException {
		IOT120InputVO inputVO = (IOT120InputVO) body;
		IOT120OutputVO outputVO = new IOT120OutputVO();
		dam_obj = this.getDataAccessManager();
		QueryConditionIF qc =
				dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuilder sb = new StringBuilder();
		try {
			sb.append(" SELECT KEY_NO as DATA,SPECIAL_CONDITION as LABEL ");
			sb.append(" FROM TBPRD_INS_SPECIAL_CND ");
			sb.append(" WHERE INSPRD_ID = :insprd_id ");
			sb.append(" AND PAY_TYPE = :pay_type ");
			sb.append(" ORDER BY SEQ ");

			qc.setObject("insprd_id", inputVO.getINSPRD_ID().toUpperCase());
			qc.setObject("pay_type", inputVO.getPAY_TYPE());

			qc.setQueryString(sb.toString());
			outputVO.setSPECIAL_CONList(dam_obj.exeQuery(qc));
			sendRtnObject(outputVO);
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s", StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}

	public void Get_PdfInfo(Object body, IPrimitiveMap<Object> header)
			throws JBranchException {
		IOT120InputVO inputVO = (IOT120InputVO) body;
		IOT120OutputVO outputVO = new IOT120OutputVO();
		String AO_ID = inputVO.getAO_ID();

		String WebSerivceReturn = new String();
		Map<String, Object> webservicePdfData = new HashMap<String, Object>();
		Map<String, Object> webservicePdfData_Result = new HashMap<String, Object>();

		try {
			webservicePdfData = getWebServiceInfo(1, inputVO.getCASE_ID());
			if (webservicePdfData.get("ReturnCode").toString().equals("100")
					&& webservicePdfData.get("Result") != null) {
				webservicePdfData_Result = (Map<String, Object>) webservicePdfData.get("Result");
				if (webservicePdfData_Result.get("CaseID").toString().equals(inputVO.getCASE_ID())) {

					TBIOT_MAPP_PDFVO tmpdf = new TBIOT_MAPP_PDFVO();
					dam_obj = this.getDataAccessManager();

					String strPdfFile;
					if(webservicePdfData_Result.get("PDF_FILE") != null){
						strPdfFile = webservicePdfData_Result.get("PDF_FILE").toString();
					}else{
						throw new JBranchException("PDF檔案為空");
					}

					Blob blob = decryptBase64(strPdfFile);

					if(StringUtils.equals("Y", inputVO.getFromIOT110())) {
						//購買檢核檢視行動要保書
						String filePath = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
						String uuid = UUID.randomUUID().toString();
						String fileName = String.format("%s.pdf", uuid);

						if (blob != null) {
							int blobLength = (int) blob.length();
							byte[] blobAsBytes = blob.getBytes(1, blobLength);

							File targetFile = new File(filePath, fileName);
							OutputStream out = new FileOutputStream(targetFile);
							out.write(blobAsBytes);
							out.close();
						} else {
							throw new APException("PDF檔案大小為空");
						}
						//return_VO.setFILE_URL(filePath + fileName);
						notifyClientViewDoc(PdfUtil.process(new PdfConfigVO(this.getDataAccessManager(), "temp//" + fileName)), "pdf");
						//this.sendRtnObject(return_VO);
					} else {
						int count = chkPdfCASE_ID(inputVO.getCASE_ID());
						if(count > 0){
							int iotStatusCount = chkIOTStatus(inputVO.getCASE_ID());
							//已簽署過保險文件不可再更新
							if(iotStatusCount == 0){
								tmpdf = (TBIOT_MAPP_PDFVO)dam_obj.findByPKey(TBIOT_MAPP_PDFVO.TABLE_UID, inputVO.getCASE_ID());
								if(tmpdf != null){
									tmpdf.setPDF_FILE(blob);
									dam_obj.update(tmpdf);
								}
							}
						}else{
							tmpdf.setCASE_ID(inputVO.getCASE_ID());
							tmpdf.setPDF_FILE(blob);
							dam_obj.create(tmpdf);
						}

						outputVO.setWebservicePdfData(webservicePdfData);
					}

					sendRtnObject(outputVO);
				}
			}
		} catch (Exception e) {
			throw new JBranchException(e.getMessage(), e);
		} finally {
			logger.info("call ws for PDF WebService End");
		}
	}

	// 2018/04/03
	// call Web Service
	public void Get_InsInfo(Object body, IPrimitiveMap<Object> header)
			throws Exception {
		String path = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
		IOT120InputVO inputVO = (IOT120InputVO) body;
		IOT120OutputVO outputVO = new IOT120OutputVO();

		// 接收回傳資料
		String webSerivceReturn = new String();
		Map<String, Object> webServiceData = new HashMap<String, Object>();
		Map<String, Object> webServiceResult = new HashMap<String, Object>();
		Map<String, Object> soapWebServiceData = new HashMap<String, Object>();

//		try {
			//新案件API
			webServiceData = getWebServiceInfo(2, inputVO.getCASE_ID());
			if (webServiceData.get("ReturnCode") != null
					&& webServiceData.get("ReturnCode").toString().equals("100")
					&& StringUtils.isNotBlank(webServiceData.get("Result").toString())) {
				webServiceResult = parseWsResult(webServiceData, inputVO);

			} else {
				//舊案件API
//				soapWebServiceData = getSoapWebServiceInfo(inputVO.getCASE_ID());
//				webServiceResult = parseSoapWsResult(soapWebServiceData, inputVO);

				throw new JBranchException("MAPP API錯誤。ReturnCode:" + webServiceData.get("ReturnCode") + ", Message: " + webServiceData.get("Message"));
			}

			outputVO.setWebserviceData(webServiceResult);
			
			//富壽繳款服務API //IOT110呼叫的時候不用CALL此API
			if(StringUtils.equals("Y", inputVO.getFromIOT120())) {
				Map<String, Object> payServData = new HashMap<String, Object>();
				Map<String, Object> payServResult = new HashMap<String, Object>();
	
				payServData = getWebServiceInfo(3, inputVO.getCASE_ID());
				if (payServData.get("ReturnCode") != null
						&& payServData.get("ReturnCode").toString().equals("100")
						&& StringUtils.isNotBlank(payServData.get("Result").toString())) {
					payServResult = (Map<String, Object>) payServData.get("Result");
				}
				//ReturnCode=100:成功，不須檢富繳款服務單  200:無繳款服務單資訊  300:尚未審核通過
				payServResult.put("ReturnCode", payServData.get("ReturnCode").toString());
				outputVO.setWsPayServData(payServResult);
			}

			sendRtnObject(outputVO);

//		} catch (Exception e) {
//			try{
//				logger.info("新案件API Error：");
//				e.printStackTrace();
//				//舊案件API
//				soapWebServiceData = getSoapWebServiceInfo(inputVO.getCASE_ID());
//				webServiceResult = parseSoapWsResult(soapWebServiceData, inputVO);
//				outputVO.setWebserviceData(webServiceResult);
//				sendRtnObject(outputVO);
//			} catch(Exception ex) {
//				logger.error("舊案件API：" + ex.getMessage());
//				throw new JBranchException(ex.getMessage(), ex);
//			}
//		} finally {
//			logger.info("call ws End");
//		}
	}

	/***
	 * 投資型商品適配日(要保人保險購買檢核的鍵機日)須等於要保書申請日
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void getInvPreMatch(Object body, IPrimitiveMap<Object> header) throws JBranchException {

		IOT120InputVO inputVO = (IOT120InputVO) body;
		IOT120OutputVO outputVO = new IOT120OutputVO();
		dam_obj = this.getDataAccessManager();
		QueryConditionIF qc = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuilder sb = new StringBuilder();
		if (inputVO.getAPPLY_DATE() != null) {
			try {
				sb.append(" select MATCH_DATE, CUST_RISK, C_SENIOR_PVAL, PREMATCH_SEQ from TBIOT_PREMATCH ");
				sb.append(" where CUST_ID = :cust_id and INSPRD_ID = :insprd_id ");
				sb.append(" and TRUNC(MATCH_DATE) = TRUNC(:apply_date) ORDER BY CREATETIME DESC ");

				qc.setObject("cust_id", inputVO.getCUST_ID().toUpperCase());
				qc.setObject("insprd_id", inputVO.getINSPRD_ID().toUpperCase());
				qc.setObject("apply_date", inputVO.getAPPLY_DATE());
				qc.setQueryString(sb.toString());

				List<Map<String, Object>> pList = dam_obj.exeQuery(qc);
				outputVO.setPreMatchList(pList);

				//取得投資型連結標的清單
				List<Map<String, Object>> invList = new ArrayList<Map<String, Object>>();
				if(CollectionUtils.isNotEmpty(pList)) {
					invList = getInvestList(ObjectUtils.toString(pList.get(0).get("PREMATCH_SEQ")));
				}
				outputVO.setINVESTList(invList);

				sendRtnObject(outputVO);
			} catch (Exception e) {
				logger.error(String.format("發生錯誤:%s", StringUtil.getStackTraceAsString(e)));
				throw new APException("系統發生錯誤請洽系統管理員");
			}
		}
	}

	//根據案件編號取得購買檢核資料
	public void get_PreMatch(Object body, IPrimitiveMap<Object> header) throws JBranchException {
		IOT120InputVO inputVO = (IOT120InputVO) body;
		IOT120OutputVO outputVO = new IOT120OutputVO();

		//取得購買檢核資料
		List<Map<String, Object>> preList = get_PreMatch(inputVO);
		outputVO.setPreMatchList(preList);

		//取得投資型連結標的清單
		outputVO.setINVESTList(getInvestList(inputVO.getPREMATCH_SEQ()));

		sendRtnObject(outputVO);
	}

	private List<Map<String, Object>> getInvestList(String prematchSeq) throws DAOException, JBranchException {
		dam_obj = this.getDataAccessManager();
		QueryConditionIF qc = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuilder sb = new StringBuilder();

		sb.append(" select b.TARGET_ID,b.LINKED_NAME,a.LINK_PCT as ALLOCATION_RATIO,b.PRD_RISK,a.INS_KEYNO,a.PRD_LK_KEYNO ");
		sb.append(" from TBIOT_FUND_LINK a,TBPRD_INS_LINKING b ");
		sb.append(" where a.PRD_LK_KEYNO = b.KEY_NO and a.PREMATCH_SEQ = :prematchSeq ");
		qc.setObject("prematchSeq", prematchSeq);
		qc.setQueryString(sb.toString());

		return dam_obj.exeQuery(qc);
	}

	/***
	 * 根據案件編號取得購買檢核資料
	 * @param inputVO
	 * @return
	 * @throws DAOException
	 * @throws JBranchException
	 */
	public List<Map<String, Object>> get_PreMatch(IOT120InputVO inputVO) throws DAOException, JBranchException {
		dam_obj = this.getDataAccessManager();
		QueryConditionIF qc = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuilder sb = new StringBuilder();

		sb.append(" Select A.PREMATCH_SEQ, A.INS_KIND, A.INS_ID, A.CASE_ID, A.POLICY_NO1, A.POLICY_NO2, A.POLICY_NO3, A.OTH_TYPE, A.BRANCH_NBR ");
		sb.append(" 	,A.CUST_ID, A.CUST_RISK, A.PROPOSER_NAME, A.PROPOSER_BIRTH, A.INSURED_ID, A.INSURED_NAME, A.REPRESET_ID, A.REPRESET_NAME, A.RLT_BT_PROREP ");
		sb.append(" 	,A.PAYER_ID, A.PAYER_NAME, A.RLT_BT_PROPAY, A.INSPRD_KEYNO, A.INSPRD_ID, A.REAL_PREMIUM, A.BASE_PREMIUM, A.MOP2, A.LOAN_SOURCE_YN, A.APPLY_DATE ");
		sb.append(" 	,A.MATCH_DATE, A.RECRUIT_ID, A.PROPOSER_CM_FLAG, A.INSURED_CM_FLAG, A.PAYER_CM_FLAG, A.AML,PRECHECK, A.PROPOSER_INCOME1, A.PROPOSER_INCOME2 ");
		sb.append(" 	,A.PROPOSER_INCOME3, A.INSURED_INCOME1, A.INSURED_INCOME2, A.INSURED_INCOME3, A.LOAN_CHK1_YN, A.LOAN_CHK2_YN, A.LOAN_CHK3_YN, A.CD_CHK_YN, A.INCOME_REMARK ");
		sb.append(" 	,A.C_LOAN_CHK1_YN, A.C_LOAN_CHK2_YN, A.C_LOAN_CHK3_YN, A.C_CD_CHK_YN, A.I_LOAN_CHK1_YN, A.I_LOAN_CHK2_YN, A.I_LOAN_CHK3_YN, A.I_CD_CHK_YN ");
		sb.append("		,A.LOAN_SOURCE_REMARK, A.STATUS, A.CUST_RISK_DUE AS KYC_DUE_DATE, A.AO_CODE, A.REPRESET_CM_FLAG ");
		sb.append("		,P.INSPRD_TYPE AS PRODUCT_TYPE, P.SPECIAL_CONDITION, P.INSPRD_NAME AS CNCT_NAME, P.CURR_CD, P.INSPRD_ANNUAL, P.PAY_TYPE, P.AB_EXCH_RATE ");
		sb.append(" 	,A.CONTRACT_END_YN, A.S_INFITEM_LOAN_YN, A.PROPOSER_WORK, A.INSURED_WORK, A.C_LOAN_APPLY_DATE, A.C_PREM_DATE, A.I_LOAN_APPLY_DATE, A.P_LOAN_APPLY_DATE ");
		sb.append(" 	,A.C_LOAN_APPLY_YN, A.I_LOAN_APPLY_YN, A.P_LOAN_APPLY_YN, A.LOAN_SOURCE2_YN, A.AB_SENIOR_YN, A.C_SALE_SENIOR_YN, A.I_SALE_SENIOR_YN, A.P_SALE_SENIOR_YN ");
		sb.append(" 	,A.INSURED_BIRTH, A.PAYER_BIRTH, A.C_KYC_INCOME, A.I_KYC_INCOME, A.C_SALE_SENIOR_TRANSSEQ, A.I_SALE_SENIOR_TRANSSEQ, A.P_SALE_SENIOR_TRANSSEQ ");
		sb.append(" 	,P.NEED_MATCH, A.C_SENIOR_PVAL, A.COMPANY_NUM, B.CNAME AS INS_COM_NAME, A.FB_COM_YN, A.CANCEL_CONTRACT_YN ");
		sb.append("     ,CASE WHEN D.C_PREMIUM_TRANSSEQ IS NOT NULL THEN D.C_PREMIUM_TRANSSEQ ELSE C.PREMIUM_TRANSSEQ END AS PREMIUM_TRANSSEQ ");
		sb.append("     ,CASE WHEN D.I_PREMIUM_TRANSSEQ IS NOT NULL THEN D.I_PREMIUM_TRANSSEQ ELSE C.I_PREMIUM_TRANSSEQ END AS I_PREMIUM_TRANSSEQ ");
		sb.append("     ,CASE WHEN D.P_PREMIUM_TRANSSEQ IS NOT NULL THEN D.P_PREMIUM_TRANSSEQ ELSE C.P_PREMIUM_TRANSSEQ END AS P_PREMIUM_TRANSSEQ ");
		sb.append("     ,A.BUSINESS_REL, A.MAPPVIDEO_YN, A.DIGITAL_AGREESIGN_YN ");
		sb.append(" From TBIOT_PREMATCH A ");
		sb.append(" LEFT JOIN TBPRD_INS_MAIN P on P.INSPRD_KEYNO = A.INSPRD_KEYNO ");
		sb.append(" LEFT JOIN TBJSB_INS_PROD_COMPANY B ON B.SERIALNUM = A.COMPANY_NUM ");
		sb.append(" LEFT JOIN TBIOT_TRANSSEQ C ON C.PREMATCH_SEQ = A.PREMATCH_SEQ AND C.UPDATE_STATUS = 'UP' ");
		sb.append("    AND C.LASTUPDATE = (SELECT MAX(LASTUPDATE) FROM TBIOT_TRANSSEQ WHERE PREMATCH_SEQ = A.PREMATCH_SEQ AND UPDATE_STATUS = 'UP') ");
		sb.append(" LEFT JOIN TBIOT_CALLOUT D ON D.PREMATCH_SEQ = A.PREMATCH_SEQ AND D.STATUS = '4' "); //電訪成功
		sb.append(" 	Where A.REG_TYPE = '1' ");

		if(StringUtils.isNotBlank(inputVO.getPREMATCH_SEQ())) {
			sb.append("	AND A.PREMATCH_SEQ = :prematchSeq ");
			qc.setObject("prematchSeq", inputVO.getPREMATCH_SEQ());
		}

		if(StringUtils.isNotBlank(inputVO.getCASE_ID())) {
			sb.append("	AND A.CASE_ID = :caseId ");
			qc.setObject("caseId", inputVO.getCASE_ID());
		}

		if(StringUtils.isNotBlank(inputVO.getREG_TYPE())) {
			sb.append("	AND A.OTH_TYPE = :othType ");
			qc.setObject("othType", inputVO.getREG_TYPE());
		}

		qc.setQueryString(sb.toString());
		List<Map<String, Object>> list = dam_obj.exeQuery(qc);

		if(CollectionUtils.isEmpty(list)) {
			throw new APException("查無此適合度檢核資料");
		} else if(!StringUtils.equals("3", ObjectUtils.toString(list.get(0).get("STATUS")))) {	//3:已核可
			throw new APException("此適合度檢核編號尚未完成主管覆核");
		}

		return list;
	}

	public void chkData(Object body, IPrimitiveMap<Object> header) throws JBranchException {
		String First_buy = "";
		String chk_Ab = "";
		IOT120InputVO inputVO = (IOT120InputVO) body;
		IOT120OutputVO outputVO = new IOT120OutputVO();
		IOT920 iot920 = (IOT920) PlatformContext.getBean("iot920");
		FirstBuyInputVO iot920_FBInputVO = new FirstBuyInputVO();
		FirstBuyDataVO iot920_FBOutputVO = new FirstBuyDataVO();
		iot920_FBInputVO.setINSPRD_TYPE(inputVO.getPRODUCT_TYPE());
		iot920_FBInputVO.setCURR_CD(inputVO.getCURR_CD());
		iot920_FBInputVO.setCUST_ID(inputVO.getCUST_ID().toUpperCase());
		// 判斷首購
		iot920_FBOutputVO = iot920.chk_FirstBuy(iot920_FBInputVO);
		First_buy = iot920_FBOutputVO.getFirstBuy_YN();
		// 非常態錄音電訪判斷
		chk_AbInputVO iot920_AbinputVO = new chk_AbInputVO();
		chk_AbOutputVO iot920_AbOutputVO = new chk_AbOutputVO();
		iot920_AbinputVO.setREAL_PREMIUM(inputVO.getREAL_PREMIUM());
		iot920_AbinputVO.setPAY_TYPE(inputVO.getPAY_TYPE());
		iot920_AbinputVO.setMOP2(inputVO.getMOP2());
		iot920_AbinputVO.setAB_EXCH_RATE(inputVO.getAB_EXCH_RATE());
		iot920_AbinputVO.setUNDER_YN(inputVO.getUNDER_YN());
		iot920_AbinputVO.setPRO_YN(inputVO.getPRO_YN());
		iot920_AbinputVO.setFirstBuy_YN(First_buy);
		iot920_AbOutputVO = iot920.chk_Ab(iot920_AbinputVO);
		chk_Ab = iot920_AbOutputVO.getAbTranSEQ_YN();
		if(StringUtils.equals("Y", inputVO.getC_SALE_SENIOR_YN())) chk_Ab = "N"; //要保人若已有高齡銷售錄音註記則不需再做非常態錄音檢核

		dam_obj = this.getDataAccessManager();
		QueryConditionIF qc = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuilder sb = new StringBuilder();
//		try {
			if (inputVO.getRECRUIT_ID() != null) {
				List<String> checkLicenses = new ArrayList<String>();
				qc = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sb = new StringBuilder();

				sb.append(" SELECT  'X' ,	CASE	WHEN	CERTIFICATE_EX_DATE	<	SYSDATE	THEN	'Y' END	AS	EXPIRED	,	");
				sb.append("					CASE	WHEN	UNREG_DATE	IS NOT NULL	THEN	'Y'	END	AS	UNREG		");
				sb.append("	FROM  TBORG_MEMBER_CERT ");
				sb.append(" where CERTIFICATE_CODE='01' ");
				sb.append(" and emp_id = :recruit_id ");

				qc.setObject("recruit_id", inputVO.getRECRUIT_ID());

				qc.setQueryString(sb.toString());
				checkLicenses = dam_obj.exeQuery(qc);
				// 檢核證照
				if (checkLicenses.size() <= 0) {
					throw new APException("ehl_01_iot120_002");
				} else {
					List<String> checkREC = new ArrayList<String>();
					if (inputVO.getAB_TRANSSEQ() != null) {
						qc = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
						sb = new StringBuilder();
						sb.append(" select 'x' from TBSYS_REC_LOG ");
						sb.append(" where TRANSSEQ = :transseq ");
						sb.append(" and to_date(transdate, 'yyyymmdd') between ");
						sb.append(" to_date(:transdate1, 'yyyymmdd') and to_date(:transdate2, 'yyyymmdd')+7 ");
						sb.append(" and CUSTID = :custid ");
						sb.append(" and substr(transseq, 7, 1) = '7' ");
						sb.append(" and ((:INSPRD_TYPE1 in ('2','3') and substr(transseq, 8, 1) = '2') Or ");
						sb.append(" (:INSPRD_TYPE2 = '1' and :curr_cd <> 'TWD' and substr(transseq, 8, 1) = '1')) ");

						qc.setObject("transseq", inputVO.getAB_TRANSSEQ());
						SimpleDateFormat simple = new SimpleDateFormat("yyyy-MM-dd");
						String transdate = simple.format(inputVO.getAPPLY_DATE()).replaceAll("-", "");
						qc.setObject("transdate1", transdate);
						qc.setObject("transdate2", transdate);
						qc.setObject("custid", inputVO.getCUST_ID().toUpperCase());
						qc.setObject("INSPRD_TYPE1", inputVO.getPRODUCT_TYPE());
						qc.setObject("INSPRD_TYPE2", inputVO.getPRODUCT_TYPE());
						qc.setObject("curr_cd", inputVO.getCURR_CD());
						qc.setQueryString(sb.toString());
						checkREC = dam_obj.exeQuery(qc);
					}

					if (inputVO.getPRODUCT_TYPE() != null) {
						if (!"1".equals(inputVO.getPRODUCT_TYPE().toString())) {
							if ("Y".equals(chk_Ab) && inputVO.getAB_TRANSSEQ() == null) {
								if(StringUtils.equals("Y", inputVO.getCANCEL_CONTRACT_YN())) {
									//契撤案件不用檢核非常態錄音
									outputVO = submit(inputVO);
								} else {
									throw new APException("ehl_01_iot120_009");
								}
							} else if (("Y".equals(chk_Ab) && inputVO.getAB_TRANSSEQ() != null)
									|| ("N".equals(chk_Ab) && inputVO.getAB_TRANSSEQ() != null)) {
								if (checkREC.size() <= 0) {
									throw new APException("ehl_01_iot120_007");
								} else {
									outputVO = submit(inputVO);
								}
							} else if ("N".equals(chk_Ab) && inputVO.getAB_TRANSSEQ() == null) {
								outputVO = submit(inputVO);
							}
						} else {
							// 針對非投資型外幣商品
							if (!"TWD".equals(inputVO.getCURR_CD())) {
								if ("Y".equals(chk_Ab) && StringUtils.isBlank(inputVO.getAB_TRANSSEQ())) {
									if(StringUtils.equals("Y", inputVO.getCANCEL_CONTRACT_YN())) {
										//契撤案件不用檢核非常態錄音
										outputVO = submit(inputVO);
									} else {
										throw new APException("ehl_01_iot120_009");
									}
								} else if (("Y".equals(chk_Ab) && StringUtils.isNotBlank(inputVO.getAB_TRANSSEQ()))
										|| ("N".equals(chk_Ab) && StringUtils.isNotBlank(inputVO.getAB_TRANSSEQ()))) {
									if (checkREC.size() <= 0) {
										throw new APException("ehl_01_iot120_007");
									} else {
										outputVO = submit(inputVO);
									}
								} else if ("N".equals(chk_Ab) && StringUtils.isBlank(inputVO.getAB_TRANSSEQ())) {
									outputVO = submit(inputVO);
								}
							} else if (StringUtils.isNotBlank(inputVO.getAB_TRANSSEQ())) {
								// 針對非投資型非外幣，但有輸入非常態交易錄音序號
								if (checkREC.size() <= 0) {
									throw new APException("ehl_01_iot120_007");
								} else {
									outputVO = submit(inputVO);
								}
							} else {
								// 非投資型非外幣
								outputVO = submit(inputVO);
							}
						}
					}
				}
			} else {
				throw new APException("ehl_01_iot120_002");
			}
//		} catch (Exception e) {
//			logger.error(String.format("發生錯誤:%s", StringUtil.getStackTraceAsString(e)));
//			throw new APException(e.getMessage());
//		}
		sendRtnObject(outputVO);
	}

	/***
	 * 取得兩個日期間的營業日數(工作日)
	 * @param startDate
	 * @param endDate
	 * @return
	 * @throws DAOException
	 * @throws JBranchException
	 */
	private int getBizDateDiff(Date startDate, Date endDate) throws DAOException, JBranchException {
		dam_obj = this.getDataAccessManager();
		QueryConditionIF qc = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuilder sb = new StringBuilder();

		sb.append("SELECT COUNT(1) AS CNT FROM ");
		sb.append(" (SELECT TO_CHAR(:startDate + (level - 1), 'yyyyMMdd') AS WDATE ");
		sb.append(" 	FROM DUAL ");
		sb.append(" 	CONNECT BY TRUNC(:startDate) + level - 1 <= TRUNC(:endDate) ");
		sb.append(" 	MINUS ");
		sb.append(" 	SELECT TO_CHAR(HOL_DATE, 'yyyyMMdd') AS WDATE ");
		sb.append(" 	FROM TBBTH_HOLIDAY) ");

		qc.setObject("startDate", startDate);
		qc.setObject("endDate", endDate);
		qc.setQueryString(sb.toString());
		List<Map<String, Object>> cntList = dam_obj.exeQuery(qc);

		if(CollectionUtils.isNotEmpty(cntList)) {
			return Integer.parseInt(cntList.get(0).get("CNT").toString());
		} else {
			return 0;
		}
	}

	private boolean validPremiumTransSeq(String prematchSeq, String premiumSeq, String type) throws DAOException, JBranchException {
		dam_obj = this.getDataAccessManager();
		QueryConditionIF qc = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuilder sb = new StringBuilder();

		sb.append("SELECT 1 AS CNT FROM TBIOT_TRANSSEQ ");
		sb.append(" WHERE PREMATCH_SEQ = :prematchSeq AND UPDATE_STATUS = 'UP' ");
		if(StringUtils.equals("1", type)) { //要保人
			sb.append(" AND PREMIUM_TRANSSEQ = :cTranseq ");
			qc.setObject("cTranseq", premiumSeq);
		} else if(StringUtils.equals("2", type)) { //被保人
			sb.append(" AND I_PREMIUM_TRANSSEQ = :iTranseq ");
			qc.setObject("iTranseq", premiumSeq);
		} else if(StringUtils.equals("3", type)) { //繳款人
			sb.append(" AND P_PREMIUM_TRANSSEQ = :pTranseq ");
			qc.setObject("pTranseq", premiumSeq);
		}
		qc.setObject("prematchSeq", prematchSeq);
		qc.setQueryString(sb.toString());
		List<Map<String, Object>> cntList = dam_obj.exeQuery(qc);

		return CollectionUtils.isEmpty(cntList) ? false : true;
	}
	
	public void submit(Object body, IPrimitiveMap<Object> header) throws JBranchException {
		IOT120InputVO inputVO = (IOT120InputVO) body;
		IOT120OutputVO outputVO = submit(inputVO);
		sendRtnObject(outputVO);
	}
	
	public IOT120OutputVO submit(IOT120InputVO inputVO) throws JBranchException {
		IOT920 iot920 = (IOT920) PlatformContext.getBean("iot920");
		INSIDInputVOinputVO iot920_inputVO = new INSIDInputVOinputVO();
		INSIDDataVO iot920_outputVO = new INSIDDataVO();
		IOT120OutputVO outputVO = new IOT120OutputVO();
		String loginbrh = (String) getCommonVariable(SystemVariableConsts.LOGINBRH);
		String CASE_ID = "";
		String INS_ID = "";
		String SIGN_INC = "";
		String errorMsg = "";
		iot920_inputVO.setBRANCH_NBR(loginbrh);
		iot920_inputVO.setINS_KIND("1");
		iot920_inputVO.setREG_TYPE(inputVO.getREG_TYPE());
		if ("new".equals(inputVO.getOPR_STATUS())) {
			try {
				dam_obj = this.getDataAccessManager();
				QueryConditionIF qc = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				StringBuilder sb = new StringBuilder();
				
				//取得INS_KEYNO
				String INS_KEYNO;
				if(StringUtils.isBlank(inputVO.getINS_KEYNO())) {
					//若有上傳檔案需先產生INS_KEYNO寫入TBIOT_MAIN_PDF，這裡就不需再次產生
					INS_KEYNO = getINS_KEYNO();
				} else {
					INS_KEYNO = inputVO.getINS_KEYNO();
				}
				BigDecimal keyno_change = new BigDecimal(INS_KEYNO);

				// 文件檢核
				int in_DOC_N = 0;
				int out_DOC_N = 0;

				if (inputVO.getInList() != null) {
					Edit_INSPRD_ID(inputVO.getInList(), keyno_change, "1");
					for (Map<String, Object> INitem : inputVO.getInList()) {
						//1:紙本必檢附 3:APP必檢附，列表中只會取其一
						if (("1".equals(INitem.get("DOC_LEVEL")) || "3".equals(INitem.get("DOC_LEVEL"))) && "N".equals(INitem.get("DOC_CHK"))) {
							inputVO.setSTATUS("10");
							in_DOC_N++;
						}
						if ("Y".equals(INitem.get("SIGN_INC"))) {
							SIGN_INC = "Y";
						}
						if(!StringUtils.equals("Y", inputVO.getCANCEL_CONTRACT_YN()) 
								&& StringUtils.equals("Y", inputVO.getBUSINESS_REL()) && (Double)INitem.get("DOC_SEQ") == 9) {
							if(!"Y".equals(INitem.get("DOC_CHK"))) {
								inputVO.setSTATUS("10");
								outputVO.setErrorMsg("分行留存文件應檢附「洗錢防制姓名檢核結果」");
							}
						}
					}
				}

				if (inputVO.getOutList() != null) {
					Edit_INSPRD_ID(inputVO.getOutList(), keyno_change, "2");

					for (Map<String, Object> OUTitem : inputVO.getOutList()) {
						//1:紙本必檢附 3:APP必檢附，列表中只會取其一
						if (("1".equals(OUTitem.get("DOC_LEVEL")) || "3".equals(OUTitem.get("DOC_LEVEL"))) && "N".equals(OUTitem.get("DOC_CHK"))) {
							inputVO.setSTATUS("10");
							out_DOC_N++;
						}
						if ("Y".equals(OUTitem.get("SIGN_INC"))) {
							SIGN_INC = "Y";
						}
						if(StringUtils.equals("Y", inputVO.getCANCEL_CONTRACT_YN()) && (Double)OUTitem.get("DOC_SEQ") == 98) {
							if(!"Y".equals(OUTitem.get("DOC_CHK"))) {
								inputVO.setSTATUS("10");
								outputVO.setErrorMsg("契撤案件需檢附「契撤申請書」");
							}
						}
						if(!StringUtils.equals("Y", inputVO.getCANCEL_CONTRACT_YN()) && 
								StringUtils.equals("2", inputVO.getREMIT_COUNTRY_FLAG()) &&
								(Double)OUTitem.get("DOC_SEQ") == 30) {
							if(!"Y".equals(OUTitem.get("DOC_CHK"))) {
								inputVO.setSTATUS("10");
								outputVO.setErrorMsg("境外匯款件，境外匯款聲明書為必備文件。");
							}
						}
					}
				}

				if (in_DOC_N <= 0 && out_DOC_N <= 0) {
//					inputVO.setSTATUS("20");
				}
				if ("Y".equals(inputVO.getQC_ERASER())
						|| (in_DOC_N > 0 && out_DOC_N > 0)
						|| ("N".equals(inputVO.getQC_STAMP())
						&& "".equals(inputVO.getPROPOSER_TRANSSEQ())
						&& "".equals(inputVO.getINSURED_TRANSSEQ()))) {
					inputVO.setSTATUS("10");
				}

				//檢核項目-公會通報日
				if(inputVO.getGUILD_RPT_DATE() != null && inputVO.getAPPLY_DATE() != null) {
					//要保書申請日在公會通報日之前
					int diffDate = getBizDateDiff(inputVO.getAPPLY_DATE(), inputVO.getGUILD_RPT_DATE());

					if(inputVO.getGUILD_RPT_DATE().compareTo(inputVO.getAPPLY_DATE()) < 0) {
						//公會通報日在要保書申請日之前
						diffDate = getBizDateDiff(inputVO.getGUILD_RPT_DATE(), inputVO.getAPPLY_DATE());
					}

					if(diffDate > 2) {
						//若公會通報日-要保書申請日>2個工作天
						inputVO.setSTATUS("10");	//理專進件
					}
				} else {
					//若公會通報日為空白
					inputVO.setSTATUS("10");	//理專進件
				}

				//再檢查一次保費來源錄音序號
				//要保人高齡/保費來源錄音序號檢核 (契撤案件不須檢核)
				if(StringUtils.equals(inputVO.getPREMIUM_TRANSSEQ_NEED_YN(), "Y")) {
					//要保人高齡+要保人、被保人、繳款人保費來源=Y，任一成立則要保人需輸入錄音序號
					if(StringUtils.isEmpty(inputVO.getPREMIUM_TRANSSEQ())) {
						//未輸入要保人高齡/保費來源錄音序號
						inputVO.setSTATUS("10");	//理專進件
					} else {
//						if(!validPremiumTransSeq(inputVO.getPREMATCH_SEQ(), inputVO.getPREMIUM_TRANSSEQ(), "1")) {
//							//有輸入要保人高齡/保費來源錄音序號，但與總行匯入不同
//							throw new APException("NOT_VALIDPREMIUM_SEQ");
//						}
					}
				}						
				//被保人高齡/保費來源錄音序號檢核 (契撤案件不須檢核)
				if(StringUtils.equals(inputVO.getI_PREMIUM_TRANSSEQ_NEED_YN(), "Y")) {
					//被保人高齡+保費來源=Y，任一成立則被保人需輸入錄音序號
					if(StringUtils.isEmpty(inputVO.getI_PREMIUM_TRANSSEQ())) {
						//未輸入被保人高齡/保費來源錄音序號
						inputVO.setSTATUS("10");	//理專進件
					} else {
//						if(!validPremiumTransSeq(inputVO.getPREMATCH_SEQ(), inputVO.getI_PREMIUM_TRANSSEQ(), "2")) {
//							//有輸入被保人高齡/保費來源錄音序號，但與總行匯入不同
//							throw new APException("NOT_VALIDPREMIUM_SEQ");
//						}
					}
				}
				//繳款人高齡/保費來源錄音序號檢核 (契撤案件不須檢核)
				if(StringUtils.equals(inputVO.getP_PREMIUM_TRANSSEQ_NEED_YN(), "Y")) {
					//繳款人高齡+保費來源=Y，任一成立則繳款人需輸入錄音序號
					if(StringUtils.isEmpty(inputVO.getP_PREMIUM_TRANSSEQ())) {
						//未輸入繳款人高齡/保費來源錄音序號
						inputVO.setSTATUS("10");	//理專進件
					} else {
//						if(!validPremiumTransSeq(inputVO.getPREMATCH_SEQ(), inputVO.getP_PREMIUM_TRANSSEQ(), "3")) {
//							//有輸入繳款人高齡/保費來源錄音序號，但與總行匯入不同
//							throw new APException("NOT_VALIDPREMIUM_SEQ");
//						}
					}
				}
				//保費資金上傳檔案檢核
				if(StringUtils.equals("Y", inputVO.getFUND_VERIFY_PDF_YN())) {
					//若"以PDF檔方式上傳"有勾選，但沒有上傳檔案，狀態為"10 理專進件"
					if(!StringUtils.equals("Y", inputVO.getFundFileUploadYN())) {
						inputVO.setSTATUS("10");	//理專進件
						outputVO.setErrorMsg("資金證明上傳與資金檢附確認不一致，請確認！");
					}
				} else {
					//若"以PDF檔方式上傳"無勾選，但有上傳檔案，狀態為"10 理專進件"
					if(StringUtils.equals("Y", inputVO.getFundFileUploadYN())) {
						inputVO.setSTATUS("10");	//理專進件
						outputVO.setErrorMsg("資金證明上傳與資金檢附確認不一致，請確認！");
					}
				}

				dam_obj = this.getDataAccessManager();
				TBIOT_MAINVO tmvo = new TBIOT_MAINVO();
				tmvo.setRECRUIT_ID(inputVO.getRECRUIT_ID());
				tmvo.setINS_KEYNO(keyno_change);
				tmvo.setINS_KIND("1");

				tmvo.setREG_TYPE(inputVO.getREG_TYPE());
				tmvo.setBRANCH_NBR(loginbrh);
				tmvo.setCUST_ID(inputVO.getCUST_ID().toUpperCase());
				tmvo.setPROPOSER_NAME(inputVO.getPROPOSER_NAME().toUpperCase());

				tmvo.setPROPOSER_CM_FLAG(inputVO.getPROPOSER_CM_FLAG());
				tmvo.setINSURED_ID(inputVO.getINSURED_ID().toUpperCase());
				tmvo.setINSURED_NAME(inputVO.getINSURED_NAME());
				tmvo.setINSURED_CM_FLAG(inputVO.getINSURED_CM_FLAG());
				if (inputVO.getREPRESET_ID() != null) {
					tmvo.setREPRESET_ID(inputVO.getREPRESET_ID().toUpperCase());
				}
				tmvo.setRLT_BT_PROREP(inputVO.getRLT_BT_PROREP());
				tmvo.setREPRESET_CM_FLAG(inputVO.getREPRESET_CM_FLAG());
				tmvo.setAO_CODE(inputVO.getAO_CODE());
				tmvo.setIS_EMP(inputVO.getIS_EMP());
				BigDecimal insprd_keyno = new BigDecimal(inputVO.getCNCT_INSPRD_KEYNO());
				tmvo.setINSPRD_KEYNO(insprd_keyno);
				if (!"".equals(inputVO.getREAL_PREMIUM())) {
					String RP = inputVO.getREAL_PREMIUM().replaceAll(",", "");// 去掉千分位符號
					BigDecimal real_premium = new BigDecimal(RP);
					tmvo.setREAL_PREMIUM(real_premium);
				}

				if (inputVO.getBASE_PREMIUM() != null && !"".equals(inputVO.getBASE_PREMIUM())) {
					String BP = inputVO.getBASE_PREMIUM().replaceAll(",", "");// 去掉千分位符號
					BigDecimal base_premium = new BigDecimal(BP);
					tmvo.setBASE_PREMIUM(base_premium);
				}
				tmvo.setMOP2(inputVO.getMOP2());
				tmvo.setFIRST_PAY_WAY(inputVO.getFIRST_PAY_WAY());
				Timestamp apply_date = new Timestamp(inputVO.getAPPLY_DATE().getTime());
				tmvo.setAPPLY_DATE(apply_date);

				Timestamp keyin_date = new Timestamp(inputVO.getKEYIN_DATE().getTime());
				tmvo.setKEYIN_DATE(keyin_date);
				// 適配日調整 //非投資型保單不寫是配日期
				if (inputVO.getMATCH_DATE() != null && !StringUtils.equals("1", inputVO.getPRODUCT_TYPE())) {
					Timestamp MATCH_DATE = new Timestamp(inputVO.getMATCH_DATE().getTime());
					tmvo.setMATCH_DATE(MATCH_DATE);
				}

				BigDecimal status = new BigDecimal(inputVO.getSTATUS());
				tmvo.setQC_ADD(inputVO.getQC_ADD());
				tmvo.setAB_TRANSSEQ(inputVO.getAB_TRANSSEQ());
				tmvo.setTERMINATED_INC(inputVO.getTERMINATED_INC());
				tmvo.setQC_ERASER(inputVO.getQC_ERASER());
				tmvo.setQC_ANC_DOC(inputVO.getQC_ANC_DOC());
				tmvo.setQC_STAMP(inputVO.getQC_STAMP());
				tmvo.setPROPOSER_TRANSSEQ(inputVO.getPROPOSER_TRANSSEQ());
				tmvo.setINSURED_TRANSSEQ(inputVO.getINSURED_TRANSSEQ());
				if (!"".equals(SIGN_INC)) {
					tmvo.setSIGN_INC(SIGN_INC);
				} else {
					tmvo.setSIGN_INC("Y");
				}
				tmvo.setSTATUS(status);
				Date OP_DATE_NOW = new Date();
				if ("20".equals(inputVO.getSTATUS().toString())) {
					tmvo.setOP_DATE(new Timestamp(OP_DATE_NOW.getTime()));
				}
				if (!"".equals(inputVO.getREF_CON_ID())) {
					tmvo.setREF_CON_ID(inputVO.getREF_CON_ID());
				}
				if (!"".equals(inputVO.getWRITE_REASON())) {
					tmvo.setWRITE_REASON(inputVO.getWRITE_REASON());
				}
				if (inputVO.getWRITE_REASON_OTH() != null) {
					tmvo.setWRITE_REASON_OTH(inputVO.getWRITE_REASON_OTH());
				}

				// 保險文件編號
				if (inputVO.getINS_ID() != null) {
					INS_ID = iot920.check_INS_ID("1", inputVO.getINS_ID());
				} else {
					iot920_outputVO = iot920.getINS_ID(iot920_inputVO);
					INS_ID = iot920_outputVO.getINS_ID().toString();
				}
				tmvo.setINS_ID(INS_ID);

				// 案件編號
				if (StringUtils.isNotBlank(inputVO.getCASE_ID())) {
					CASE_ID = chkCASE_ID(inputVO.getCASE_ID());
					tmvo.setCASE_ID(CASE_ID);
					
					//非契撤件，若案件編號已存在電訪作業檔，輸入的購買檢核編號與電訪作業購買檢核編號不符，則顯示錯誤訊息
					if(!StringUtils.equals("Y", inputVO.getCANCEL_CONTRACT_YN()) && !validCallReportPrematch(inputVO.getCASE_ID(), inputVO.getPREMATCH_SEQ())) {
						throw new APException("該案件已申請電訪購買檢核編碼輸入錯誤，請與理專確認");
					}
					//更新保費資金上傳檔的CASE_ID
					updateCASE_ID4File(keyno_change, inputVO.getCASE_ID());
				}

				//適合度檢核編號
				tmvo.setPREMATCH_SEQ(inputVO.getPREMATCH_SEQ());
				if(inputVO.getGUILD_RPT_DATE() != null) {
					tmvo.setGUILD_RPT_DATE(new Timestamp(inputVO.getGUILD_RPT_DATE().getTime()));
				}
				tmvo.setLOAN_PRD_YN(inputVO.getLOAN_PRD_YN());
				tmvo.setNOT_PASS_REASON(inputVO.getNOT_PASS_REASON());
				tmvo.setPREMIUM_TRANSSEQ(inputVO.getPREMIUM_TRANSSEQ());
				tmvo.setI_PREMIUM_TRANSSEQ(inputVO.getI_PREMIUM_TRANSSEQ());
				tmvo.setP_PREMIUM_TRANSSEQ(inputVO.getP_PREMIUM_TRANSSEQ());
				tmvo.setQC_IMMI(inputVO.getQC_IMMI());
				tmvo.setLOAN_SOURCE_YN(inputVO.getLOAN_SOURCE_YN());
				tmvo.setQC_APEC(inputVO.getQC_APEC());
				tmvo.setQC_LOAN_CHK(inputVO.getQC_LOAN_CHK());
				tmvo.setQC_SIGNATURE(inputVO.getQC_SIGNATURE());
				tmvo.setFB_COM_YN(inputVO.getFB_COM_YN());
				tmvo.setCOMPANY_NUM(inputVO.getCOMPANY_NUM());
				tmvo.setPAY_SERV_RETURN_CODE(inputVO.getPAY_SERV_RETURN_CODE());
				tmvo.setPAY_SERV_REMIT_NATNO(inputVO.getPAY_SERV_REMIT_NATNO());
				tmvo.setREMIT_COUNTRY_FLAG(inputVO.getREMIT_COUNTRY_FLAG());
				tmvo.setFUND_VERIFY_PDF_YN(inputVO.getFUND_VERIFY_PDF_YN());
				tmvo.setFUND_VERIFY_PAPER_YN(inputVO.getFUND_VERIFY_PAPER_YN());
				tmvo.setNO_PAPER_YN(inputVO.getNO_PAPER_YN());
				
				dam_obj.create(tmvo);

				// 避免要保人生日存入時分秒
				dam_obj = this.getDataAccessManager();
				qc = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sb = new StringBuilder();

				sb.append(" update TBIOT_MAIN set PROPOSER_BIRTH = trunc(:birthday) where INS_KEYNO = :ins_keyno ");
				qc.setObject("birthday", inputVO.getPROPOSER_BIRTH());
				qc.setObject("ins_keyno", keyno_change);

				qc.setQueryString(sb.toString());
				dam_obj.exeUpdate(qc);

				if (inputVO.getINS_RIDER_DTLList().size() > 0) {
					TBIOT_RIDER_DTLVO trd;
					for (Map<String, Object> INS_RIDER_DTL : inputVO.getINS_RIDER_DTLList()) {
						List<Map<String, Object>> RIDER_DTL_KEYNOList = new ArrayList<Map<String, Object>>();
						dam_obj = this.getDataAccessManager();
						qc = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
						sb = new StringBuilder();

						sb.append(" select TBIOT_MAIN_SEQ.NEXTVAL from dual ");
						qc.setQueryString(sb.toString());

						RIDER_DTL_KEYNOList = dam_obj.exeQuery(qc);
						String RIDER_DTL_KEYNO = RIDER_DTL_KEYNOList.get(0).get("NEXTVAL").toString();
						BigDecimal rider_dtl_keyno_change = new BigDecimal(RIDER_DTL_KEYNO);

						dam_obj = this.getDataAccessManager();
						trd = new TBIOT_RIDER_DTLVO();

						trd.setRIDER_DTL_KEYNO(rider_dtl_keyno_change);
						trd.setINS_KEYNO(keyno_change);

						if (INS_RIDER_DTL.get("INSPRD_KEYNO") != null
								&& StringUtils.isNotBlank(String.valueOf(INS_RIDER_DTL.get("INSPRD_KEYNO")))) {
							BigDecimal INSPRD_KEYNO = new BigDecimal(INS_RIDER_DTL.get("INSPRD_KEYNO").toString());
							trd.setINSPRD_KEYNO(INSPRD_KEYNO);
						} else {
							throw new APException("ehl_02_common_002");
						}

						if (INS_RIDER_DTL.get("INSURED_NAME") != null
								&& StringUtils.isNotBlank(String.valueOf(INS_RIDER_DTL.get("INSURED_NAME")))) {
							trd.setINSURED_NAME(INS_RIDER_DTL.get("INSURED_NAME").toString());
						} else {
							throw new APException("ehl_02_common_002");
						}

						if (INS_RIDER_DTL.get("RLT_WITH_INSURED") != null
								&& StringUtils.isNotBlank(String.valueOf(INS_RIDER_DTL.get("RLT_WITH_INSURED")))) {
							trd.setRLT_WITH_INSURED(INS_RIDER_DTL.get("RLT_WITH_INSURED").toString());
						} else {
							throw new APException("ehl_02_common_002");
						}

						if (INS_RIDER_DTL.get("PREMIUM") != null
								&& StringUtils.isNotBlank(String.valueOf(INS_RIDER_DTL.get("PREMIUM")))) {
							BigDecimal PREMIUM = new BigDecimal(INS_RIDER_DTL.get("PREMIUM").toString().replaceAll(",", ""));
							trd.setPREMIUM(PREMIUM);
						} else {
							throw new APException("ehl_02_common_002");
						}
						dam_obj.create(trd);
					}
				}
				outputVO.setINSKEY_NO(INS_KEYNO);
				if ("2".equals(inputVO.getREG_TYPE().toString())) {
					outputVO.setINS_ID(INS_ID);
					return outputVO;
				} else {
					return outputVO;
				}
			} catch (Exception e) {
				logger.error(String.format("發生錯誤:%s", StringUtil.getStackTraceAsString(e)));
				if ("ehl_02_IOT920_001".equals(e.getMessage())) {
					outputVO.setErrorINS_ID(inputVO.getINS_ID());
					return outputVO;
				} else if ("ehl_02_common_002".equals(e.getMessage())) {
					outputVO.setErrorINS_RIDER(e.getMessage());
					return outputVO;
				} else if("NOT_VALIDPREMIUM_SEQ".equals(e.getMessage())) {
					throw new APException("保費來源錄音序號異常，請洽保險商品處。");
				} else {
					throw new APException("系統發生錯誤請洽系統管理員" + StringUtil.getStackTraceAsString(e));
				}

			}
		} else if ("UPDATE".equals(inputVO.getOPR_STATUS())) {
			QueryConditionIF qc = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuilder sb = new StringBuilder();
			BigDecimal ins_keyno_change = new BigDecimal(inputVO.getINS_KEYNO());

			// 文件檢核
			int in_DOC_N = 0;
			int out_DOC_N = 0;

			// 險種代碼更改時，留存文件清除
			if (inputVO.isEditINSPRD_ID()) {
				dam_obj = getDataAccessManager();
				qc = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sb = new StringBuilder();
				sb.append(" DELETE TBIOT_DOC_CHK where INS_KEYNO = :ins_keyno ");
				qc.setObject("ins_keyno", ins_keyno_change);
				qc.setQueryString(sb.toString());
				dam_obj.exeUpdate(qc);
			}

			if (inputVO.getInList() != null) {
				// 險種代碼更改時，送件文件重新新增
				if (inputVO.isEditINSPRD_ID()) {
					Edit_INSPRD_ID(inputVO.getInList(), ins_keyno_change, "1");
				}

				for (Map<String, Object> INitem : inputVO.getInList()) {
					//1:紙本必檢附 3:APP必檢附，列表中只會取其一
					if (("1".equals(INitem.get("DOC_LEVEL")) || "3".equals(INitem.get("DOC_LEVEL")))
							&& "N".equals(INitem.get("DOC_CHK"))) {
						inputVO.setSTATUS("10");
						in_DOC_N++;
					}

					if ("Y".equals(INitem.get("SIGN_INC"))) {
						SIGN_INC = "Y";
					}
					
					if(!StringUtils.equals("Y", inputVO.getCANCEL_CONTRACT_YN()) 
							&& StringUtils.equals("Y", inputVO.getBUSINESS_REL()) && (Double)INitem.get("DOC_SEQ") == 9) {
						if(!"Y".equals(INitem.get("DOC_CHK"))) {
							inputVO.setSTATUS("10");
							outputVO.setErrorMsg("分行留存文件應檢附「洗錢防制姓名檢核結果」");
						}
					}
				}
			}

			if (inputVO.getOutList() != null) {
				// 險種代碼更改時，留存文件重新新增
				if (inputVO.isEditINSPRD_ID()) {
					Edit_INSPRD_ID(inputVO.getOutList(), ins_keyno_change, "2");
				}

				for (Map<String, Object> OUTitem : inputVO.getOutList()) {
					if (("1".equals(OUTitem.get("DOC_LEVEL")) || "3".equals(OUTitem.get("DOC_LEVEL")))
							&& "N".equals(OUTitem.get("DOC_CHK"))) {
						inputVO.setSTATUS("10");
						out_DOC_N++;
					}

					if ("Y".equals(OUTitem.get("SIGN_INC"))) {
						SIGN_INC = "Y";
					}
					
					if(StringUtils.equals("Y", inputVO.getCANCEL_CONTRACT_YN()) && (Double)OUTitem.get("DOC_SEQ") == 98) {
						if(!"Y".equals(OUTitem.get("DOC_CHK"))) {
							inputVO.setSTATUS("10");
							outputVO.setErrorMsg("契撤案件需檢附「契撤申請書」");
						}
					}
					if(!StringUtils.equals("Y", inputVO.getCANCEL_CONTRACT_YN()) && 
							StringUtils.equals("2", inputVO.getREMIT_COUNTRY_FLAG()) &&
							(Double)OUTitem.get("DOC_SEQ") == 30) {
						if(!"Y".equals(OUTitem.get("DOC_CHK"))) {
							inputVO.setSTATUS("10");
							outputVO.setErrorMsg("境外匯款件，境外匯款聲明書為必備文件。");
						}
					}
				}
			}

			int check_QC = 0;
			if ("Y".equals(inputVO.getQC_ERASER())
					|| ("N".equals(inputVO.getQC_STAMP())
							&& inputVO.getPROPOSER_TRANSSEQ() == null && inputVO.getINSURED_TRANSSEQ() == null)) {
				inputVO.setSTATUS("10");
				check_QC++;
			}

			if (in_DOC_N <= 0 && out_DOC_N <= 0 && check_QC <= 0) {
//				inputVO.setSTATUS("20");
			}

			//檢核項目-公會通報日
			if(inputVO.getGUILD_RPT_DATE() != null && inputVO.getAPPLY_DATE() != null) {
				//要保書申請日在公會通報日之前
				int diffDate = getBizDateDiff(inputVO.getAPPLY_DATE(), inputVO.getGUILD_RPT_DATE());

				if(inputVO.getGUILD_RPT_DATE().compareTo(inputVO.getAPPLY_DATE()) < 0) {
					//公會通報日在要保書申請日之前
					diffDate = getBizDateDiff(inputVO.getGUILD_RPT_DATE(), inputVO.getAPPLY_DATE());
				}

				if(diffDate > 2) {
					//若公會通報日-要保書申請日>2個工作天
					inputVO.setSTATUS("10");	//理專進件
				}
			} else {
				//若公會通報日為空白
				inputVO.setSTATUS("10");	//理專進件
			}

			//再檢查一次保費來源錄音序號
			//要保人高齡/保費來源錄音序號檢核 (契撤案件不須檢核)
			if(StringUtils.equals(inputVO.getPREMIUM_TRANSSEQ_NEED_YN(), "Y")) {
				//要保人高齡+要保人、被保人、繳款人保費來源=Y，任一成立則要保人需輸入錄音序號
				if(StringUtils.isEmpty(inputVO.getPREMIUM_TRANSSEQ())) {
					//未輸入要保人高齡/保費來源錄音序號
					inputVO.setSTATUS("10");	//理專進件
				} else {
//					if(!validPremiumTransSeq(inputVO.getPREMATCH_SEQ(), inputVO.getPREMIUM_TRANSSEQ(), "1")) {
//						//有輸入要保人高齡/保費來源錄音序號，但與總行匯入不同
//						throw new APException("保費來源錄音序號異常，請洽保險商品處。");
//					}
				}
			}						
			//被保人高齡/保費來源錄音序號檢核 (契撤案件不須檢核)
			if(StringUtils.equals(inputVO.getI_PREMIUM_TRANSSEQ_NEED_YN(), "Y")) {
				//被保人高齡+保費來源=Y，任一成立則被保人需輸入錄音序號
				if(StringUtils.isEmpty(inputVO.getI_PREMIUM_TRANSSEQ())) {
					//未輸入被保人高齡/保費來源錄音序號
					inputVO.setSTATUS("10");	//理專進件
				} else {
//					if(!validPremiumTransSeq(inputVO.getPREMATCH_SEQ(), inputVO.getI_PREMIUM_TRANSSEQ(), "2")) {
//						//有輸入被保人高齡/保費來源錄音序號，但與總行匯入不同
//						throw new APException("保費來源錄音序號異常，請洽保險商品處。");
//					}
				}
			}
			//繳款人高齡/保費來源錄音序號檢核 (契撤案件不須檢核)
			if(StringUtils.equals(inputVO.getP_PREMIUM_TRANSSEQ_NEED_YN(), "Y")) {
				//繳款人高齡+保費來源=Y，任一成立則繳款人需輸入錄音序號
				if(StringUtils.isEmpty(inputVO.getP_PREMIUM_TRANSSEQ())) {
					//未輸入繳款人高齡/保費來源錄音序號
					inputVO.setSTATUS("10");	//理專進件
				} else {
//					if(!validPremiumTransSeq(inputVO.getPREMATCH_SEQ(), inputVO.getP_PREMIUM_TRANSSEQ(), "3")) {
//						//有輸入繳款人高齡/保費來源錄音序號，但與總行匯入不同
//						throw new APException("保費來源錄音序號異常，請洽保險商品處。");
//					}
				}
			}

			// 案件編號
			if (StringUtils.isNotBlank(inputVO.getCASE_ID())) {
				//非契撤件，若案件編號已存在電訪作業檔，輸入的購買檢核編號與電訪作業購買檢核編號不符，則顯示錯誤訊息
				if(!StringUtils.equals("Y", inputVO.getCANCEL_CONTRACT_YN()) && !validCallReportPrematch(inputVO.getCASE_ID(), inputVO.getPREMATCH_SEQ())) {
					throw new APException("該案件已申請電訪購買檢核編碼輸入錯誤，請與理專確認");
				}
				//更新保費資金上傳檔的CASE_ID
				updateCASE_ID4File(ins_keyno_change, inputVO.getCASE_ID());
			}
			
			//保費資金上傳檔案檢核
			if(StringUtils.equals("Y", inputVO.getFUND_VERIFY_PDF_YN())) {
				//若"以PDF檔方式上傳"有勾選，但沒有上傳檔案，狀態為"10 理專進件"
				if(!StringUtils.equals("Y", inputVO.getFundFileUploadYN())) {
					inputVO.setSTATUS("10");	//理專進件
					outputVO.setErrorMsg("資金證明上傳與資金檢附確認不一致，請確認！");
				}
			} else {
				//若"以PDF檔方式上傳"無勾選，但有上傳檔案，狀態為"10 理專進件"
				if(StringUtils.equals("Y", inputVO.getFundFileUploadYN())) {
					inputVO.setSTATUS("10");	//理專進件
					outputVO.setErrorMsg("資金證明上傳與資金檢附確認不一致，請確認！");
				}
			}
			
			dam_obj = this.getDataAccessManager();
			TBIOT_MAINVO tmvo = new TBIOT_MAINVO();
			tmvo = (TBIOT_MAINVO) dam_obj.findByPKey(TBIOT_MAINVO.TABLE_UID,ins_keyno_change);

			tmvo.setRECRUIT_ID(inputVO.getRECRUIT_ID());
			tmvo.setINS_KIND("1");
			// 保險文件編號
			if (inputVO.getINS_ID() != null) {
				tmvo.setINS_ID(inputVO.getINS_ID());
			} else {
				iot920_outputVO = iot920.getINS_ID(iot920_inputVO);
				INS_ID = iot920_outputVO.getINS_ID().toString();
				tmvo.setINS_ID(INS_ID);
			}
			tmvo.setCASE_ID(inputVO.getCASE_ID());
			tmvo.setPREMATCH_SEQ(inputVO.getPREMATCH_SEQ());
			tmvo.setREG_TYPE(inputVO.getREG_TYPE());
			tmvo.setBRANCH_NBR(loginbrh);
			tmvo.setCUST_ID(inputVO.getCUST_ID().toUpperCase());
			tmvo.setPROPOSER_NAME(inputVO.getPROPOSER_NAME());

			tmvo.setPROPOSER_CM_FLAG(inputVO.getPROPOSER_CM_FLAG());
			tmvo.setINSURED_ID(inputVO.getINSURED_ID().toUpperCase());
			tmvo.setINSURED_NAME(inputVO.getINSURED_NAME());
			tmvo.setINSURED_CM_FLAG(inputVO.getINSURED_CM_FLAG());

			if (inputVO.getREPRESET_ID() != null) {
				tmvo.setREPRESET_ID(inputVO.getREPRESET_ID().toUpperCase());
			}

			tmvo.setRLT_BT_PROREP(inputVO.getRLT_BT_PROREP());
			tmvo.setREPRESET_CM_FLAG(inputVO.getREPRESET_CM_FLAG());
			tmvo.setAO_CODE(inputVO.getAO_CODE());
			tmvo.setIS_EMP(inputVO.getIS_EMP());
			BigDecimal insprd_keyno = new BigDecimal(inputVO.getCNCT_INSPRD_KEYNO());
			tmvo.setINSPRD_KEYNO(insprd_keyno);
			String RP = inputVO.getREAL_PREMIUM().replaceAll(",", "");// 去掉千分位符號
			BigDecimal real_premium = new BigDecimal(RP);
			tmvo.setREAL_PREMIUM(real_premium);

			if (inputVO.getBASE_PREMIUM() != null && !"".equals(inputVO.getBASE_PREMIUM())) {
				String BP = inputVO.getBASE_PREMIUM().replaceAll(",", "");
				BigDecimal base_premium = new BigDecimal(BP);
				tmvo.setBASE_PREMIUM(base_premium);
			}

			tmvo.setMOP2(inputVO.getMOP2());
			tmvo.setFIRST_PAY_WAY(inputVO.getFIRST_PAY_WAY());
			Timestamp apply_date = new Timestamp(inputVO.getAPPLY_DATE().getTime());
			tmvo.setAPPLY_DATE(apply_date);

			//非投資型保單不寫是配日期
			if (inputVO.getMATCH_DATE() != null && !StringUtils.equals("1", inputVO.getPRODUCT_TYPE())) {
				Timestamp MATCH_DATE = new Timestamp(inputVO.getMATCH_DATE().getTime());
				tmvo.setMATCH_DATE(MATCH_DATE);
			} else {
				tmvo.setMATCH_DATE(null);
			}

			tmvo.setQC_ADD(inputVO.getQC_ADD());
			tmvo.setAB_TRANSSEQ(inputVO.getAB_TRANSSEQ());
			tmvo.setTERMINATED_INC(inputVO.getTERMINATED_INC());
			tmvo.setQC_ERASER(inputVO.getQC_ERASER());
			tmvo.setQC_ANC_DOC(inputVO.getQC_ANC_DOC());
			tmvo.setQC_STAMP(inputVO.getQC_STAMP());
			tmvo.setPROPOSER_TRANSSEQ(inputVO.getPROPOSER_TRANSSEQ());
			tmvo.setINSURED_TRANSSEQ(inputVO.getINSURED_TRANSSEQ());
			if (!"".equals(SIGN_INC)) {
				tmvo.setSIGN_INC(SIGN_INC);
			} else {
				tmvo.setSIGN_INC("Y");
			}
			BigDecimal status = new BigDecimal(inputVO.getSTATUS());
			tmvo.setSTATUS(status);
			Date now = new Date();
			Timestamp op_date = new Timestamp(now.getTime());
			if ("20".equals(inputVO.getSTATUS().toString())) {
				tmvo.setOP_DATE(op_date);
			} else if ("10".equals(inputVO.getSTATUS())) {
				tmvo.setOP_DATE(null);
			}
			if (!"".equals(inputVO.getREF_CON_ID())) {
				tmvo.setREF_CON_ID(inputVO.getREF_CON_ID());
			}
			if (!"".equals(inputVO.getWRITE_REASON())) {
				tmvo.setWRITE_REASON(inputVO.getWRITE_REASON());
			}
			if (inputVO.getWRITE_REASON_OTH() != null) {
				tmvo.setWRITE_REASON_OTH(inputVO.getWRITE_REASON_OTH());
			}

			if(inputVO.getGUILD_RPT_DATE() != null) {
				tmvo.setGUILD_RPT_DATE(new Timestamp(inputVO.getGUILD_RPT_DATE().getTime()));
			}
			tmvo.setLOAN_PRD_YN(inputVO.getLOAN_PRD_YN());
			tmvo.setNOT_PASS_REASON(inputVO.getNOT_PASS_REASON());
			tmvo.setPREMIUM_TRANSSEQ(inputVO.getPREMIUM_TRANSSEQ());
			tmvo.setI_PREMIUM_TRANSSEQ(inputVO.getI_PREMIUM_TRANSSEQ());
			tmvo.setP_PREMIUM_TRANSSEQ(inputVO.getP_PREMIUM_TRANSSEQ());
			tmvo.setQC_IMMI(inputVO.getQC_IMMI());
			tmvo.setLOAN_SOURCE_YN(inputVO.getLOAN_SOURCE_YN());
			tmvo.setQC_APEC(inputVO.getQC_APEC());
			tmvo.setQC_LOAN_CHK(inputVO.getQC_LOAN_CHK());
			tmvo.setQC_SIGNATURE(inputVO.getQC_SIGNATURE());
			tmvo.setFB_COM_YN(inputVO.getFB_COM_YN());
			tmvo.setCOMPANY_NUM(inputVO.getCOMPANY_NUM());
			tmvo.setPAY_SERV_RETURN_CODE(inputVO.getPAY_SERV_RETURN_CODE());
			tmvo.setPAY_SERV_REMIT_NATNO(inputVO.getPAY_SERV_REMIT_NATNO());
			tmvo.setREMIT_COUNTRY_FLAG(inputVO.getREMIT_COUNTRY_FLAG());
			tmvo.setFUND_VERIFY_PDF_YN(inputVO.getFUND_VERIFY_PDF_YN());
			tmvo.setFUND_VERIFY_PAPER_YN(inputVO.getFUND_VERIFY_PAPER_YN());
			tmvo.setNO_PAPER_YN(inputVO.getNO_PAPER_YN());

			dam_obj.update(tmvo);

			// 避免要保人生日存入時分秒
			dam_obj = this.getDataAccessManager();
			qc =
				dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sb = new StringBuilder();

			sb.append(" update TBIOT_MAIN set PROPOSER_BIRTH = trunc(:birthday) where INS_KEYNO = :ins_keyno ");
			qc.setObject("birthday", inputVO.getPROPOSER_BIRTH());
			qc.setObject("ins_keyno", ins_keyno_change);

			qc.setQueryString(sb.toString());
			dam_obj.exeUpdate(qc);

			// 先刪除投資型連結標的清單
			dam_obj = getDataAccessManager();
			qc =
				dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sb = new StringBuilder();

			sb.append(" delete TBIOT_FUND_LINK where INS_KEYNO = :ins_keyno  ");
			qc.setObject("ins_keyno", ins_keyno_change);

			qc.setQueryString(sb.toString());
			dam_obj.exeUpdate(qc);

			if (inputVO.getDELETE_INS_RIDER_DTLList().size() > 0
					&& inputVO.getDELETE_INS_RIDER_DTLList() != null) {
				TBIOT_RIDER_DTLVO trd;
				for (String delete_trd : inputVO.getDELETE_INS_RIDER_DTLList()) {
					dam_obj = this.getDataAccessManager();
					trd = new TBIOT_RIDER_DTLVO();

					BigDecimal delete_trd_change = new BigDecimal(delete_trd);

					trd = (TBIOT_RIDER_DTLVO) dam_obj.findByPKey(TBIOT_RIDER_DTLVO.TABLE_UID, delete_trd_change);
					dam_obj.delete(trd);
				}
			}

			if (inputVO.getINS_RIDER_DTLList().size() > 0) {
				TBIOT_RIDER_DTLVO trd;
				for (Map<String, Object> INS_RIDER_DTL : inputVO.getINS_RIDER_DTLList()) {
					if (INS_RIDER_DTL.get("RIDER_DTL_KEYNO") != null) {
						dam_obj = this.getDataAccessManager();
						trd = new TBIOT_RIDER_DTLVO();

						BigDecimal rider_dtl_keyno_change = new BigDecimal(INS_RIDER_DTL.get("RIDER_DTL_KEYNO").toString());
						trd = (TBIOT_RIDER_DTLVO) dam_obj.findByPKey(TBIOT_RIDER_DTLVO.TABLE_UID,rider_dtl_keyno_change);
						BigDecimal INSPRD_KEYNO =
								new BigDecimal(INS_RIDER_DTL.get("INSPRD_KEYNO").toString());
						trd.setINSPRD_KEYNO(INSPRD_KEYNO);
						trd.setINSURED_NAME(INS_RIDER_DTL.get("INSURED_NAME").toString());
						trd.setRLT_WITH_INSURED(INS_RIDER_DTL.get("RLT_WITH_INSURED").toString());
						if (INS_RIDER_DTL.get("PREMIUM") != null) {
							if (!"".equals(INS_RIDER_DTL.get("PREMIUM"))) {
								BigDecimal PREMIUM =
										new BigDecimal(INS_RIDER_DTL.get("PREMIUM").toString().replaceAll(",", ""));
								trd.setPREMIUM(PREMIUM);
							}
						}
						dam_obj.update(trd);
					} else {
						List<Map<String, Object>> RIDER_DTL_KEYNOList = new ArrayList<Map<String, Object>>();
						dam_obj = this.getDataAccessManager();
						qc =
							dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
						sb = new StringBuilder();

						sb.append(" select TBIOT_MAIN_SEQ.NEXTVAL from dual ");
						qc.setQueryString(sb.toString());
						RIDER_DTL_KEYNOList = dam_obj.exeQuery(qc);

						String RIDER_DTL_KEYNO = RIDER_DTL_KEYNOList.get(0).get("NEXTVAL").toString();
						BigDecimal new_rider_dtl_keyno_change = new BigDecimal(RIDER_DTL_KEYNO);

						dam_obj = this.getDataAccessManager();
						trd = new TBIOT_RIDER_DTLVO();

						trd.setRIDER_DTL_KEYNO(new_rider_dtl_keyno_change);
						trd.setINS_KEYNO(ins_keyno_change);
						BigDecimal INSPRD_KEYNO = new BigDecimal(INS_RIDER_DTL.get("INSPRD_KEYNO").toString());
						trd.setINSPRD_KEYNO(INSPRD_KEYNO);
						trd.setINSURED_NAME(INS_RIDER_DTL.get("INSURED_NAME").toString());
						trd.setRLT_WITH_INSURED(INS_RIDER_DTL.get("RLT_WITH_INSURED").toString());

						if (INS_RIDER_DTL.get("PREMIUM") != null) {
							if (!"".equals(INS_RIDER_DTL.get("PREMIUM"))) {
								BigDecimal PREMIUM =
										new BigDecimal(INS_RIDER_DTL.get("PREMIUM").toString().replaceAll(",", ""));
								trd.setPREMIUM(PREMIUM);
							}
						}
						dam_obj.create(trd);
					}
				}
			}
			return outputVO;
		}
		return outputVO;
	}

	// 留存/送件文件新增
	public void Edit_INSPRD_ID(List<Map<String, Object>> in_outList, BigDecimal ins_keyno_change, String type) throws JBranchException {

		for (Map<String, Object> INitem : in_outList) {
			dam_obj = this.getDataAccessManager();
			TBIOT_DOC_CHKPK tdc_pk = new TBIOT_DOC_CHKPK();
			tdc_pk.setDOC_TYPE(type);
			tdc_pk.setINS_KEYNO(ins_keyno_change);
			tdc_pk.setDOC_SEQ(new BigDecimal(INitem.get("DOC_SEQ").toString()));
			TBIOT_DOC_CHKVO tdc_vo = new TBIOT_DOC_CHKVO();
			tdc_vo = (TBIOT_DOC_CHKVO) dam_obj.findByPKey(TBIOT_DOC_CHKVO.TABLE_UID, tdc_pk);
			if (tdc_vo == null) {
				tdc_vo = new TBIOT_DOC_CHKVO();
				tdc_vo.setcomp_id(tdc_pk);
				tdc_vo.setDOC_CHK(INitem.get("DOC_CHK").toString());
				tdc_vo.setDOC_NAME(INitem.get("DOC_NAME").toString());
				tdc_vo.setDOC_LEVEL(INitem.get("DOC_LEVEL").toString());
				if (INitem.get("SIGN_INC") != null) {
					tdc_vo.setSIGN_INC(INitem.get("SIGN_INC").toString());
				} else {
					tdc_vo.setSIGN_INC("Y");
				}
				if (INitem.get("DOC_NAME_OTH") != null) {
					tdc_vo.setDOC_NAME_OTH(INitem.get("DOC_NAME_OTH").toString());
				}
				dam_obj.create(tdc_vo);
			} else {
				throw new APException("ehl_01_common_016");
			}
		}
	}

	public void printRecording1(Object body) throws JBranchException {
		String loginbrh = (String) getCommonVariable(SystemVariableConsts.LOGINBRHNAME);
		String loginName = (String) getCommonVariable(SystemVariableConsts.LOGINNAME);
		String loginFirstName = "";
		if (loginbrh == null) {
			loginbrh = "";
		}
		if (loginName != null) {
			loginFirstName = loginName.substring(0, 1);
		}
		String url = null;
		String txnCode = "IOT120";
		String reportID = "R1";
		ReportIF report = null;

		ReportDataIF data = new ReportData();
		ReportGeneratorIF gen = ReportFactory.getGenerator();

		IOT120InputVO return_VO = (IOT120InputVO) body;
		// 轉民國
		String year = "";
		String mon = "";
		String day = "";
		if (return_VO.getAPPLY_DATE() != null) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(return_VO.getAPPLY_DATE());
			year = cal.get(Calendar.YEAR) - 1911 + "";
			mon = cal.get(Calendar.MONTH) + 1 + "";
			if (mon.length() < 2) {
				mon = "0" + mon;
			}
			day = cal.get(Calendar.DAY_OF_MONTH) + "";
			if (day.length() < 2) {
				day = "0" + day;
			}
		} else {
			Calendar cal = Calendar.getInstance();
			year = cal.get(Calendar.YEAR) - 1911 + "";
			mon = cal.get(Calendar.MONTH) + 1 + "";
			if (mon.length() < 2) {
				mon = "0" + mon;
			}
			day = cal.get(Calendar.DAY_OF_MONTH) + "";
			if (day.length() < 2) {
				day = "0" + day;
			}
		}

		data.addParameter("DATE", year + mon + day);

		data.addParameter("INS_ID", return_VO.getINS_ID());
		data.addParameter("INSURED_ID", return_VO.getINSURED_ID().toUpperCase());
		data.addParameter("INSURED_NAME", return_VO.getINSURED_NAME());
		data.addParameter("CUST_ID", return_VO.getCUST_ID().toUpperCase());
		data.addParameter("PROPOSER_NAME", return_VO.getPROPOSER_NAME());
		data.addParameter("BRANCH_NAME", loginbrh);
		data.addParameter("LOGIN_NAME", loginFirstName);
		// data.addParameter("BIRTHDAY",
		// return_VO.getPROPOSER_BIRTH());//2017/10/27出生日期改為XX年XX月XX日所以用不到駐掉
		data.addParameter("EMP_NAME", return_VO.getEMP_NAME());

		report = gen.generateReport(txnCode, reportID, data);
		url = report.getLocation();
		String encryptUrl = PdfUtil.process(new PdfConfigVO(this.getDataAccessManager(), url));
		notifyClientToDownloadFile(encryptUrl, "錄音照會單.pdf");
		// notifyClientViewDoc(url, "pdf");
	}

	public void printRecording2(Object body) throws JBranchException {
		String loginbrh = (String) getCommonVariable(SystemVariableConsts.LOGINBRHNAME);
		String loginName = (String) getCommonVariable(SystemVariableConsts.LOGINNAME);
		String loginFirstName = "";
		if (loginbrh == null) {
			loginbrh = "";
		}
		if (loginName != null) {
			loginFirstName = loginName.substring(0, 1);
		}
		String url = null;
		String txnCode = "IOT120";
		String reportID = "R2";
		ReportIF report = null;

		ReportDataIF data = new ReportData();
		ReportGeneratorIF gen = ReportFactory.getGenerator();

		// 轉民國
		IOT120InputVO return_VO = (IOT120InputVO) body;
		String year = "";
		String mon = "";
		String day = "";
		if (return_VO.getAPPLY_DATE() != null) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(return_VO.getAPPLY_DATE());
			year = cal.get(Calendar.YEAR) - 1911 + "";
			mon = cal.get(Calendar.MONTH) + 1 + "";
			if (mon.length() < 2) {
				mon = "0" + mon;
			}
			day = cal.get(Calendar.DAY_OF_MONTH) + "";
			if (day.length() < 2) {
				day = "0" + day;
			}
		} else {
			Calendar cal = Calendar.getInstance();
			year = cal.get(Calendar.YEAR) - 1911 + "";
			mon = cal.get(Calendar.MONTH) + 1 + "";
			if (mon.length() < 2) {
				mon = "0" + mon;
			}
			day = cal.get(Calendar.DAY_OF_MONTH) + "";
			if (day.length() < 2) {
				day = "0" + day;
			}
		}

		data.addParameter("DATE", year + mon + day);
		data.addParameter("INS_ID", return_VO.getINS_ID());
		data.addParameter("INSURED_ID", return_VO.getINSURED_ID());
		data.addParameter("INSURED_NAME", return_VO.getPROPOSER_NAME());
		data.addParameter("CUST_ID", return_VO.getCUST_ID().toUpperCase());
		data.addParameter("PROPOSER_NAME", return_VO.getINSURED_NAME());
		data.addParameter("BRANCH_NAME", loginbrh);
		data.addParameter("LOGIN_NAME", loginFirstName);
		// data.addParameter("BIRTHDAY",
		// return_VO.getPROPOSER_BIRTH());//2017/10/27出生日期改為XX年XX月XX日所以用不到駐掉
		data.addParameter("EMP_NAME", return_VO.getEMP_NAME());

		report = gen.generateReport(txnCode, reportID, data);
		url = report.getLocation();
		String encryptUrl = PdfUtil.process(new PdfConfigVO(this.getDataAccessManager(), url));
		notifyClientToDownloadFile(encryptUrl, "錄音照會單.pdf");
		//notifyClientViewDoc(url, "pdf");
	}

	public void showPDF(Object body, IPrimitiveMap header) throws Exception {
		IOT120InputVO inputVO = (IOT120InputVO) body;
		IOT120OutputVO return_VO = new IOT120OutputVO();

		dam_obj = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam_obj
				.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();

		sql.append("SELECT CASE_ID,PDF_FILE FROM TBIOT_MAPP_PDF where CASE_ID = :case_id ");
		queryCondition.setObject("case_id", inputVO.getCASE_ID());
		queryCondition.setQueryString(sql.toString());

		List<Map<String, Object>> list = dam_obj.exeQuery(queryCondition);
		if (list.size() > 0 && list.get(0).get("CASE_ID") != null) {
			String filePath = (String)
					SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
			String uuid = UUID.randomUUID().toString();
			String fileName = String.format("%s.pdf", uuid);

			Blob blob = (Blob) list.get(0).get("PDF_FILE");
			if (blob != null) {
				int blobLength = (int) blob.length();
				byte[] blobAsBytes = blob.getBytes(1, blobLength);
				//byte[] decryptBytes = Base64.decodeBase64(blobAsBytes);

				File targetFile = new File(filePath, fileName);
				OutputStream out = new FileOutputStream(targetFile);
				out.write(blobAsBytes);
				out.close();
			} else {
				throw new APException("PDF檔案大小為空");
			}
			//return_VO.setFILE_URL(filePath + fileName);
			notifyClientViewDoc(PdfUtil.process(new PdfConfigVO(this.getDataAccessManager(), "temp//" + fileName)), "pdf");
			//this.sendRtnObject(return_VO);
		}
	}

	// 檢核案件編號是否重複
	public String chkCASE_ID(String CASE_ID) throws APException {
		try {
			List<Map<String, Object>> case_idList = new ArrayList<Map<String, Object>>();
			dam_obj = this.getDataAccessManager();
			QueryConditionIF qc = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuilder sb = new StringBuilder();
			sb.append(" select count(*) as count from TBIOT_MAIN where CASE_ID = :case_id and status <> '99' ");
			qc.setObject("case_id", CASE_ID);
			qc.setQueryString(sb.toString());

			case_idList = dam_obj.exeQuery(qc);

			BigDecimal count_change = new BigDecimal(case_idList.get(0).get("COUNT").toString());
			int count = count_change.intValue();
			if (count > 0) {
				throw new APException("案件編號重複");
			} else {
				return CASE_ID;
			}
		} catch (JBranchException e) {
			e.printStackTrace();
			throw new APException(e.getMessage());
		}
	}
	
	/***
	 * 電訪作業檔不存在相同案件編號但購買檢核編號不同的資料
	 * @param caseId
	 * @param prematchSeq
	 * STATUS:
	 	1.未申請　
		2.電訪預約中　
		3.電訪處理中　
		4.電訪成功　
		5.電訪未成功　
		6.電訪疑義　
		7.取消電訪　
		8.退件處理-契撤
	 * @return true:通過檢核	false:未通過檢核
	 * @throws JBranchException 
	 * @throws DAOException 
	 */
	private boolean validCallReportPrematch(String caseId, String prematchSeq) throws DAOException, JBranchException {
		dam_obj = this.getDataAccessManager();
		QueryConditionIF qc = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT 1 FROM TBIOT_CALLOUT A ");
		sb.append(" LEFT JOIN TBIOT_PREMATCH B ON B.PREMATCH_SEQ = A.PREMATCH_SEQ ");
		sb.append(" WHERE A.PREMATCH_SEQ <> :prematchSeq AND B.CASE_ID = :caseId AND A.STATUS IN ('2', '3', '4', '5', '6') ");
		qc.setObject("caseId", caseId);
		qc.setObject("prematchSeq", prematchSeq);
		qc.setQueryString(sb.toString());
		List<Map<String, Object>> list = dam_obj.exeQuery(qc);
		
		return CollectionUtils.isEmpty(list) ? true : false;
	}

	public int chkPdfCASE_ID(String CASE_ID) throws APException {
		try {
			List<Map<String, Object>> case_idList = new ArrayList<Map<String, Object>>();
			dam_obj = this.getDataAccessManager();
			QueryConditionIF qc =
					dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuilder sb = new StringBuilder();
			sb.append(" select count(*) as count from TBIOT_MAPP_PDF where CASE_ID = :case_id ");
			qc.setObject("case_id", CASE_ID);
			qc.setQueryString(sb.toString());

			case_idList = dam_obj.exeQuery(qc);

			BigDecimal count_change =
					new BigDecimal(case_idList.get(0).get("COUNT").toString());
			int count = count_change.intValue();
			return count;
		} catch (JBranchException e) {
			e.printStackTrace();
			throw new APException(e.getMessage());
		}
	}

	public int chkIOTStatus(String CASE_ID) throws APException {
		try {
			List<Map<String, Object>> iot_statusList = new ArrayList<Map<String, Object>>();
			dam_obj = this.getDataAccessManager();
			QueryConditionIF qc =
					dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuilder sb = new StringBuilder();
			sb.append(" select count(*) as count from TBIOT_MAIN where CASE_ID = :case_id ");
			sb.append(" and STATUS in ('50', '60', '62', '70', '80', '85') ");


			qc.setObject("case_id", CASE_ID);
			qc.setQueryString(sb.toString());

			iot_statusList = dam_obj.exeQuery(qc);

			int count = 0;
			if(CollectionUtils.isNotEmpty(iot_statusList)){
				BigDecimal count_change =
						new BigDecimal(iot_statusList.get(0).get("COUNT").toString());
				count = count_change.intValue();
			}

			return count;
		} catch (JBranchException e) {
			e.printStackTrace();
			throw new APException(e.getMessage());
		}
	}
	
	/***
	 * CALL富壽API
	 * @param type
	 * @param caseId
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> getWebServiceInfo(int type, String caseId)
			throws Exception {

		// 加入Header欄位
		Map<String, Object> header = new HashMap<String, Object>();
		header.put("SYS_CHANNEL_ID", "BA0001");

		// 讀取XML參數表中的值
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> MappCaseMobileMap = xmlInfo.doGetVariable("IOT.GETMAPPCASE_MOBILE", FormatHelper.FORMAT_3);
		String ip      = getIPAddress();
		String SysCode = "";
		String WsID    = "";
		String WsPwd   = "";
		String url     = "";

		switch (type) {
		case 1: //電子要保書PDF檔案
			SysCode = MappCaseMobileMap.get("PDF_SYSCODE").toString();
			WsID    = MappCaseMobileMap.get("PDF_WS_ID").toString();
			WsPwd   = MappCaseMobileMap.get("PDF_WS_PWD").toString();
			url     = MappCaseMobileMap.get("PDF_URL").toString();
			break;
		case 2: //電子要保書內容
			SysCode = MappCaseMobileMap.get("CASE_SYSCODE").toString();
			WsID    = MappCaseMobileMap.get("CASE_WS_ID").toString();
			WsPwd   = MappCaseMobileMap.get("CASE_WS_PWD").toString();
			url     = MappCaseMobileMap.get("CASE_URL").toString();
			break;
		case 3: //繳款服務單API
			SysCode = MappCaseMobileMap.get("PAY_SERV_SYSCODE").toString();
			WsID    = MappCaseMobileMap.get("PAY_SERV_WS_ID").toString();
			WsPwd   = MappCaseMobileMap.get("PAY_SERV_WS_PWD").toString();
			url     = MappCaseMobileMap.get("PAY_SERV_URL").toString();
			break;
		default:
			break;
		}

		// 需傳入之參數
		String jsonRequestData = "{ "
				+ "\"WsID\":\""     + WsID    + "\", "
				+ "\"WsPwd\":\""    + WsPwd   + "\", "
				+ "\"SysCode\":\""  + SysCode + "\", "
				+ "\"ClientIP\":\"" + ip      + "\", "
				+ "\"CaseId\":\""   + caseId  + "\""
			+ "}";

		// 接收回傳資料
		String webSerivceReturn = new String();
		Map<String, Object> webserviceData = new HashMap<String, Object>();
		
		logger.info("IOT120 getWebServiceInfo begins:");
		logger.info("IOT120 getWebServiceInfo begins: URL: " + url);
		logger.info("IOT120 getWebServiceInfo begins: jsonRequestData: " + jsonRequestData);
		logger.info("IOT120 getWebServiceInfo begins: header: " + header);
		
		//Send Request
		GenericMap result = HttpClientJsonUtils.sendJsonRequest(url, jsonRequestData, 900000000, header, new DefHeaderCallBack());
		//get Response Data
		webSerivceReturn = (String) result.getParamMap().get("body");
		
		if(type == 1) {
			logger.info("IOT120 getWebServiceInfo returns: 電子要保書PDF檔案 result: " + webSerivceReturn.substring(0, 100));
		} else if(type == 2) {
			logger.info("IOT120 getWebServiceInfo returns: 電子要保書內容 result: " + webSerivceReturn);
		} else if(type == 3) {
			logger.info("IOT120 getWebServiceInfo returns: 繳款服務單內容 result: " + webSerivceReturn);
		}
		
		webserviceData = JsonUtil.genDefaultGson().fromJson(webSerivceReturn,HashMap.class);
		
		logger.info("IOT120 getWebServiceInfo ends:");
		
		//將WebService寫入Log資料表
		IOT920 iot920 = (IOT920) PlatformContext.getBean("iot920");
		String txnId = this.getClass().getSimpleName() + "." + new Object() {}.getClass().getEnclosingMethod().getName();
		iot920.writeWebServiceLog(txnId, url, header.toString(), jsonRequestData, (type == 1 ? webSerivceReturn.substring(0, 100) : webSerivceReturn));

		return webserviceData;
	}

	public Map<String, Object> getSoapWebServiceInfo(String caseId) throws Exception{
		Map<String, Object> webServiceData = new HashMap<String, Object>();
		String WebSerivceReturn = new String();
		JSONParser parser       = new JSONParser();
		GetMappCase clinet      = new GetMappCase();;

		WebSerivceReturn = GetMappCaseSoapUtils.getCase(caseId);

		Object obj = parser.parse(WebSerivceReturn);
		webServiceData = (Map<String, Object>) obj;

		return webServiceData;
	}

	public String getIPAddress() {
		InetAddress localIP;
		String ip = "";
		try {
			localIP = InetAddress.getLocalHost();
			ip      = localIP.getHostAddress();
			return ip;
		} catch (Exception e) {
			logger.info("Error: " + e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	public Map<String, Object> parseWsResult(Map<String, Object> webServiceData, IOT120InputVO inputVO)
			throws DAOException, JBranchException {

		XmlInfo xmlinfo = new XmlInfo();
		Map<String, Object> webServiceResult = new HashMap<String, Object>();

		List<String> AINS_KIND = new ArrayList<String>(); // 主被保險人附約險種代號
		List<String> ONEAINS_KIND = new ArrayList<String>(); // 主被保險人一年期附約險種代號
		List<String> MINS_EXEMPT = new ArrayList<String>(); // 主被保險人豁免保險費附約險種代號
		List<String> FUND_CODE1 = new ArrayList<String>(); // 人壽標的代號;TARGET_ID
		List<String> INST_PREM_UALL_PERT1 = new ArrayList<String>(); // 配置比例;ALLOCATION_RATIO
		List<Map<String, Object>> INS_RIDER_DTLList = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> INVESTList = new ArrayList<Map<String, Object>>();
		String FPAY_KIND = "";
		String AO_ID = "";
		Double MINS_PREMIUM;


		webServiceResult = (Map<String, Object>) webServiceData.get("Result");
		if (webServiceResult.get("CaseID").toString().equals(inputVO.getCASE_ID())) {
			if (webServiceResult.get("DATA_DISP") != null) {

				AO_ID = (String) webServiceResult.get("SING_SALE_ID");
				webServiceResult.put("AO_ID", AO_ID);

				dam_obj = getDataAccessManager();
				QueryConditionIF qc =
						dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				StringBuilder sb = new StringBuilder();

				sb.append(" SELECT EMP_ID FROM TBORG_MEMBER WHERE CUST_ID = :custid ");
				sb.append(" AND SERVICE_FLAG = 'A' ");
				sb.append(" AND CHANGE_FLAG IN('A', 'M', 'P') ");
				qc.setObject("custid", webServiceResult.get("SING_SALE_ID"));
				qc.setQueryString(sb.toString());

				List<Map<String, Object>> EMP_IDList = dam_obj.exeQuery(qc);

				if (CollectionUtils.isNotEmpty(EMP_IDList) && EMP_IDList.size() > 0) {
					webServiceResult.put("SING_SALE_ID", EMP_IDList.get(0).get("EMP_ID"));
				} else {
					webServiceResult.put("SING_SALE_ID", "");
				}

				//電子要保書的首期繳費方式轉換為畫面上的首期繳費方式
				FPAY_KIND = xmlinfo.getVariable("IOT.FPAY_DIND_BANK", webServiceResult.get("FPAY_KIND").toString().substring(0, 1), "F3");
				webServiceResult.put("FPAY_KIND", FPAY_KIND);

				MINS_PREMIUM = (Double) webServiceResult.get("MINS_PREMIUM");
				webServiceResult.put("MINS_PREMIUM", MINS_PREMIUM);

//				if (MINS_PREMIUM != null) {
//					PREMIUM = Integer.parseInt(MINS_PREMIUM);
//					webServiceResult.put("MINS_PREMIUM", PREMIUM);
//				} else {
//					webServiceResult.put("MINS_PREMIUM", 0);
//				}

				AINS_KIND = (List<String>) webServiceResult.get("AINS_KIND");
				ONEAINS_KIND = (List<String>) webServiceResult.get("ONEAINS_KIND");
				MINS_EXEMPT = (List<String>) webServiceResult.get("MINS_EXEMPT");
				FUND_CODE1 = (List<String>) webServiceResult.get("FUND_CODE1");
				INST_PREM_UALL_PERT1 = (List<String>) webServiceResult.get("INST_PREM_UALL_PERT1");

				if (CollectionUtils.isNotEmpty(AINS_KIND) && AINS_KIND.size() > 0) {
					for (String s : AINS_KIND) {
						Map<String, Object> ins_rider = new HashMap<String, Object>();
						ins_rider.put("DTL_INSPRD_ID", s);
						ins_rider.put("INSURED_NAME", webServiceResult.get("I_NAME"));
						INS_RIDER_DTLList.add(ins_rider);
					}
				}

				if (CollectionUtils.isNotEmpty(ONEAINS_KIND) && ONEAINS_KIND.size() > 0) {
					for (String s : ONEAINS_KIND) {
						Map<String, Object> ins_rider = new HashMap<String, Object>();
						ins_rider.put("DTL_INSPRD_ID", s);
						ins_rider.put("INSURED_NAME", webServiceResult.get("I_NAME"));
						INS_RIDER_DTLList.add(ins_rider);
					}
				}

				if (CollectionUtils.isNotEmpty(MINS_EXEMPT) && MINS_EXEMPT.size() > 0) {
					for (String s : MINS_EXEMPT) {
						Map<String, Object> ins_rider = new HashMap<String, Object>();
						ins_rider.put("DTL_INSPRD_ID", s);
						ins_rider.put("INSURED_NAME", webServiceResult.get("I_NAME"));
						INS_RIDER_DTLList.add(ins_rider);
					}
				}

				if ((CollectionUtils.isNotEmpty(FUND_CODE1) && FUND_CODE1.size() > 0)
						|| (CollectionUtils.isNotEmpty(INST_PREM_UALL_PERT1) && INST_PREM_UALL_PERT1.size() > 0)) {
					int fundSize = CollectionUtils.isNotEmpty(FUND_CODE1) ? FUND_CODE1.size() : 0;
					int intSize = CollectionUtils.isNotEmpty(INST_PREM_UALL_PERT1) ? INST_PREM_UALL_PERT1.size() : 0;

					int listSize = fundSize > intSize ? fundSize : intSize;

					if(listSize > 0) {
						//要保人高齡投資標的檢核
						List<String> targetList = getSenionInvList(inputVO);

						for (int i = 0; i < listSize; i++) {
							Map<String, Object> invest = new HashMap<String, Object>();

							if( i < fundSize && targetList.contains(FUND_CODE1.get(i))) //有在要保人高齡可投資標的中
								invest.put("TARGET_ID", FUND_CODE1.get(i));
							else
								invest.put("TARGET_ID", "");

							if( i < intSize && targetList.contains(FUND_CODE1.get(i)))
								invest.put("ALLOCATION_RATIO", INST_PREM_UALL_PERT1.get(i));
							else
								invest.put("ALLOCATION_RATIO", "");

							INVESTList.add(invest);
						}
					}

				}


				webServiceResult.put("INS_RIDER_DTLList", INS_RIDER_DTLList);
				webServiceResult.put("INVESTList", INVESTList);
				webServiceData.put("Result", webServiceResult);
				webServiceData.put("type", "RestWS");
			} else {
				throw new JBranchException("文件編號為空");
			}

		}
		return webServiceData;
	}

	public Map<String, Object> parseSoapWsResult(Map<String, Object> soapWebServiceData, IOT120InputVO inputVO)
			throws DAOException, JBranchException {

		XmlInfo xmlinfo = new XmlInfo();
		Map<String, Object> webServiceResult = new HashMap<String, Object>();

		List<String> AINS_KIND    = new ArrayList<String>();//主被保險人附約險種代號
		List<String> ONEAINS_KIND = new ArrayList<String>();//主被保險人一年期附約險種代號
		List<String> SMINS_EXEMPT = new ArrayList<String>();//主被保險人豁免保險費附約險種代號
		List<Map<String, Object>> INS_RIDER_DTLList = new ArrayList<Map<String,Object>>();
		String FPAY_DIND = "";//首期繳費方式
		String AO_ID = "";
		//為正式環境，暫時備註，
		GetMappCase clinet = null;

		if(StringUtils.isNotBlank(soapWebServiceData.get("CASE_DATA").toString())){

			webServiceResult = (Map<String, Object>) soapWebServiceData.get("CASE_DATA");

			dam_obj = getDataAccessManager();
			QueryConditionIF qc = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuilder sb = new StringBuilder();

			sb.append(" SELECT EMP_ID FROM TBORG_MEMBER WHERE CUST_ID = :custid ");

			qc.setObject("custid", webServiceResult.get("SING_SALE_ID"));
			qc.setQueryString(sb.toString());
			List<Map<String, Object>> EMP_IDList = dam_obj.exeQuery(qc);

			if(CollectionUtils.isNotEmpty(EMP_IDList) && EMP_IDList.size()>0){
				webServiceResult.put("SING_SALE_ID", EMP_IDList.get(0).get("EMP_ID"));
			}else{
				webServiceResult.put("SING_SALE_ID", "");
			}

			//電子要保書的首期繳費方式轉換為畫面上的首期繳費方式
			FPAY_DIND = xmlinfo.getVariable("IOT.FPAY_DIND_BANK", webServiceResult.get("FPAY_DIND").toString().substring(0, 1), "F3");
			webServiceResult.put("FPAY_DIND", FPAY_DIND);


			AINS_KIND    = (List<String>) webServiceResult.get("AINS_KIND");
			ONEAINS_KIND = (List<String>) webServiceResult.get("ONEAINS_KIND");
			SMINS_EXEMPT = (List<String>) webServiceResult.get("SMINS_EXEMPT");

			if(CollectionUtils.isNotEmpty(AINS_KIND) && AINS_KIND.size()>0){
				for(String s:AINS_KIND){
					Map<String, Object> ins_rider = new HashMap<String, Object>();
					ins_rider.put("DTL_INSPRD_ID", s);
					ins_rider.put("INSURED_NAME", webServiceResult.get("I_NAME"));
					INS_RIDER_DTLList.add(ins_rider);
				}
			}

			if(CollectionUtils.isNotEmpty(ONEAINS_KIND) && ONEAINS_KIND.size() > 0){
				for(String s : ONEAINS_KIND){
					Map<String, Object> ins_rider = new HashMap<String, Object>();
					ins_rider.put("DTL_INSPRD_ID", s);
					ins_rider.put("INSURED_NAME", webServiceResult.get("I_NAME"));
					INS_RIDER_DTLList.add(ins_rider);
				}
			}


			if(CollectionUtils.isNotEmpty(SMINS_EXEMPT) && SMINS_EXEMPT.size() > 0){
				for(String s : SMINS_EXEMPT){
					Map<String, Object> ins_rider = new HashMap<String, Object>();
					ins_rider.put("DTL_INSPRD_ID", s);
					ins_rider.put("INSURED_NAME", webServiceResult.get("I_NAME"));
					INS_RIDER_DTLList.add(ins_rider);
				}
			}

			webServiceResult.put("INS_RIDER_DTLList", INS_RIDER_DTLList);
			soapWebServiceData.put("CASE_DATA", webServiceResult);
			soapWebServiceData.put("type", "SoapWS");
		}
		System.out.println("webServiceData:" + soapWebServiceData);
		return soapWebServiceData;
	}


	public byte[] convertFileToByteArray(String sourcePath) throws IOException {
		InputStream inputStream = null;
		try {
			inputStream = new FileInputStream(sourcePath);
			return readFullyFile(inputStream);
		} finally {
			if (inputStream != null) {
				inputStream.close();
			}
		}
	}

	public void convertByteArrayToFile(byte[] bArray, String path) {
		try {
			// Create file
			OutputStream out = new FileOutputStream(path);
			out.write(bArray);
			out.close();
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}
	}

	public byte[] readFullyFile(InputStream stream) throws IOException {
		byte[] buffer = new byte[8192];
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		int bytesRead;
		while ((bytesRead = stream.read(buffer)) != -1) {
			baos.write(buffer, 0, bytesRead);
		}
		return baos.toByteArray();
	}

	public Blob decryptBase64(String strPdfFile) throws SerialException, SQLException, APException{
		Blob blob = new SerialBlob(strPdfFile.getBytes());
		int blobLength = (int) blob.length();
		byte[] blobAsBytes = blob.getBytes(1, blobLength);
		byte[] decryptBytes = Base64.decodeBase64(blobAsBytes);

		blob = new javax.sql.rowset.serial.SerialBlob(decryptBytes);
		return blob;
	}
	
	public void chkValid(Object body, IPrimitiveMap<Object> header) throws DAOException, JBranchException {
		IOT120InputVO inputVO = (IOT120InputVO)body;
		IOT120OutputVO return_VO = chkValid(inputVO);
		this.sendRtnObject(return_VO);
	}
	
	public IOT120OutputVO chkValid(IOT120InputVO inputVO) throws DAOException, JBranchException {
		IOT120OutputVO return_VO = new IOT120OutputVO();
		dam_obj = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();

		if (inputVO != null) {
			queryCondition = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sql = new StringBuffer();
			sql.append("SELECT  CASE WHEN TRUNC(CERTIFICATE_EX_DATE) < TRUNC(:applyDate) THEN 'Y' ELSE 'N' END AS EXPIRED, ");
			sql.append("        CASE WHEN UNREG_DATE IS NULL THEN 'N' ");
			sql.append("             WHEN TRUNC(UNREG_DATE) > TRUNC(:applyDate) THEN 'N' ");
			sql.append("        ELSE 'Y' END AS UNREG ");
			sql.append("FROM TBORG_MEMBER_CERT ");
			sql.append("WHERE CERTIFICATE_CODE = '01' ");
			sql.append("AND EMP_ID = :empID ");

			queryCondition.setObject("empID", inputVO.getRECRUIT_ID());
			queryCondition.setObject("applyDate", inputVO.getAPPLY_DATE());

			queryCondition.setQueryString(sql.toString());

			return_VO.setREFList(dam_obj.exeQuery(queryCondition));

			/**
			 * #0543: WMS-CR-20190905-01_新增理專保險新商品教育訓練檢核
			 * 	1.要保人保險購買檢核：
  			 *	 	1.1 若險種不存在E學院課程商品維護，則無需檢核
  			 *	 	1.2 若險種存在E學院課程商品維護，需檢核招攬人員編是否有存在E學院課程商品查詢檔
     		 *			1.2.1若員編有存在則可以通過檢核點
     		 *			1.2.2若員編不存在則提示訊息，主約商品教育訓練尚未完成，無法通過檢核==>無法取適合度檢核編碼。
     		 *	2.新契約送件登錄/打包及退件修改：
  			 *		2.1 若險種不存在E學院課程商品維護，則無需檢核
  			 *		2.2 若險種存在E學院課程商品維護，需檢核招攬人員編是否有存在E學院課程商品查詢檔
     		 *			1.2.1若員編有存在則可以通過檢核點
     		 *			1.2.2若員編不存在則提示訊息，主約商品教育訓練尚未完成，無法通過檢核==>理專進件。
			 */

			List<Map<String, Object>> chkList = new ArrayList<Map<String,Object>>();
			Map<String,Object> chkMap = new HashMap<String, Object>();

			// 1. 險種是否存在於E學院課程商品維護中 TBPRD_AO_TRAN_LIST
			queryCondition = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sql = new StringBuffer();

			sql.append("SELECT CLASS_ID, PRD_ID ");
			sql.append("FROM TBPRD_AO_TRAN_LIST ");
			sql.append("WHERE PRD_ID = :prdID ");
			queryCondition.setObject("prdID", inputVO.getINSPRD_ID());

			queryCondition.setQueryString(sql.toString());

			List<Map<String, Object>> prdExistsList = dam_obj.exeQuery(queryCondition);

			// 險種存在E學院課程商品維護
			if (prdExistsList.size() > 0) {
				// 若險種存在E學院課程商品維護，需檢核招攬人員編是否有存在E學院課程商品查詢檔
				queryCondition = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sql = new StringBuffer();

				sql.append("SELECT PRD_ID, EMP_ID, STATUS, CASE WHEN TRUNC(:applyDate) < TRUNC(COMPLETE_DATE) THEN 'Y' ELSE 'N' END AS OVERDUE ");
				sql.append("FROM ( ");
				sql.append("  SELECT PRD_ID, EMP_ID, STATUS, COMPLETE_DATE ");
				sql.append("  FROM TBPRD_AO_TRAN ");
				sql.append("  WHERE EMP_ID = :empID ");
				sql.append("  AND PRD_ID = :prdID ");
				sql.append("  AND STATUS = '1' ");
				sql.append("  ORDER BY CREATE_DATE ");
				sql.append(") ");
				sql.append("WHERE ROWNUM = 1 ");

				queryCondition.setObject("empID", inputVO.getRECRUIT_ID());
				queryCondition.setObject("prdID", inputVO.getINSPRD_ID());
				queryCondition.setObject("applyDate", inputVO.getAPPLY_DATE());

				queryCondition.setQueryString(sql.toString());

				List<Map<String, Object>> empExistsList = dam_obj.exeQuery(queryCondition);

				if (empExistsList.size() > 0) {
					chkMap.put("STUDY_COMPLETED", "Y");
					chkMap.put("OVERDUE", empExistsList.get(0).get("OVERDUE").toString());
					chkList.add(chkMap);
				} else {
					chkMap.put("STUDY_COMPLETED", "N");
					chkMap.put("OVERDUE", "Y");
					chkList.add(chkMap);
				}
			}
			// 險種不存在E學院課程商品維護 => 無需檢核
			else {
				chkMap.put("STUDY_COMPLETED", "Y");
				chkMap.put("OVERDUE", "N");
				chkList.add(chkMap);
			}

			return_VO.setTMSList(chkList);
		}
		return return_VO;
	}

	private List<String> getSenionInvList(IOT120InputVO inputVO) throws DAOException, JBranchException	{
		dam_obj = this.getDataAccessManager();
		QueryConditionIF qc = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		List<String> list = new ArrayList<String>();

		sb.append(" select A.TARGET_ID ");
		sb.append(" from TBPRD_INS_LINKING A ");
		sb.append(" INNER JOIN TBIOT_PREMATCH P ON P.PREMATCH_SEQ = :prematchSeq ");
		sb.append(" INNER JOIN TBCRM_CUST_MAST C ON C.CUST_ID = P.CUST_ID ");
		sb.append(" where A.INSPRD_ID = :insprd_id and substr(A.PRD_RISK, 2, 1) <= substrb(C.CUST_RISK_ATR, 2, 1) ");
		sb.append("  AND (P.C_SENIOR_PVAL IS NULL OR substrb(A.PRD_RISK, 2, 1) <= substrb(P.C_SENIOR_PVAL, 2, 1)) "); //要保人高齡限制P值

		qc.setObject("prematchSeq", inputVO.getPREMATCH_SEQ());
		qc.setObject("insprd_id", inputVO.getINSPRD_ID());
		qc.setQueryString(sb.toString());
		List<Map<String, Object>> targetList = dam_obj.exeQuery(qc);

		if(CollectionUtils.isNotEmpty(targetList)) {
			for(Map<String, Object> map : targetList) {
				list.add(ObjectUtils.toString(map.get("TARGET_ID")));
			}
		}

		return list;
	}
	
	//上傳保險資金證明文件
	public void uploadFile(Object body, IPrimitiveMap header) throws Exception {
		IOT120InputVO inputVO = (IOT120InputVO) body;
		IOT120OutputVO outputVO = new IOT120OutputVO();
		dam_obj = this.getDataAccessManager();
						
		if (StringUtils.isNotBlank(inputVO.getFileName())) {
			//取得INS_KEYNO
			String INS_KEYNO;
			if(StringUtils.isBlank(inputVO.getINS_KEYNO())) {
				INS_KEYNO = getINS_KEYNO();
			} else {
				INS_KEYNO = inputVO.getINS_KEYNO();
			}
			BigDecimal insKeyno = new BigDecimal(INS_KEYNO);
			
			byte[] data = Files.readAllBytes(Paths.get(new File((String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH), inputVO.getFileName()).toString()));
			TBIOT_MAIN_PDFVO tmpdfvo = (TBIOT_MAIN_PDFVO) dam_obj.findByPKey(TBIOT_MAIN_PDFVO.TABLE_UID, insKeyno);
			if(tmpdfvo == null) { //新增
				tmpdfvo = new TBIOT_MAIN_PDFVO();
				tmpdfvo.setINS_KEYNO(insKeyno);
				tmpdfvo.setCASE_ID(inputVO.getCASE_ID());
				tmpdfvo.setFILE_NAME(inputVO.getFileRealName());
				tmpdfvo.setPDF_FILE(ObjectUtil.byteArrToBlob(data));
				dam_obj.create(tmpdfvo);
			} else { //修改
				tmpdfvo.setCASE_ID(inputVO.getCASE_ID());
				tmpdfvo.setFILE_NAME(inputVO.getFileRealName());
				tmpdfvo.setPDF_FILE(ObjectUtil.byteArrToBlob(data));
				dam_obj.update(tmpdfvo);
			}
			
			//
			dam_obj = this.getDataAccessManager();
			QueryConditionIF qc = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			qc.setQueryString("SELECT * FROM TBIOT_MAIN_PDF WHERE INS_KEYNO = :inskeyno ");
			qc.setObject("inskeyno", insKeyno);
			
			outputVO.setFileList(dam_obj.exeQuery(qc));
			outputVO.setINSKEY_NO(INS_KEYNO);
			
			this.sendRtnObject(outputVO);
		}
	}
	
	//刪除保險資金證明文件
	public void deleteFile(Object body, IPrimitiveMap header) throws JBranchException {
		IOT120InputVO inputVO = (IOT120InputVO) body;
		IOT120OutputVO outputVO = new IOT120OutputVO();
		dam_obj = this.getDataAccessManager();
		TBIOT_MAIN_PDFVO tmpdfvo = (TBIOT_MAIN_PDFVO) dam_obj.findByPKey(TBIOT_MAIN_PDFVO.TABLE_UID, new BigDecimal(inputVO.getINS_KEYNO()));
		if(tmpdfvo != null) {
			dam_obj.delete(tmpdfvo);
		}
		
		outputVO.setFileList(new ArrayList());
		this.sendRtnObject(outputVO);
	}
	
	//檢視保險資金證明文件
	public void viewFile(Object body, IPrimitiveMap header) throws Exception {
		IOT120InputVO inputVO = (IOT120InputVO) body;
		IOT120OutputVO outputVO = new IOT120OutputVO();
		dam_obj = this.getDataAccessManager();
		QueryConditionIF qc = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		String filePath = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
		String uuid = UUID.randomUUID().toString();
		
		qc.setObject("insKeyno", inputVO.getINS_KEYNO());
		qc.setQueryString(" SELECT * FROM TBIOT_MAIN_PDF WHERE INS_KEYNO = :insKeyno ");
		List<Map<String, Object>> list = dam_obj.exeQuery(qc);
		
		String fileName = (String) list.get(0).get("FILE_NAME");
		Blob blob = (Blob) list.get(0).get("PDF_FILE");
		int blobLength = (int) blob.length();
		byte[] blobAsBytes = blob.getBytes(1, blobLength);

		File targetFile = new File(filePath, uuid);
		FileOutputStream fos = new FileOutputStream(targetFile);
		fos.write(blobAsBytes);
		fos.close();
		notifyClientToDownloadFile("temp//" + uuid, fileName);
	}
	
	//更新保險資金證明文件檔中的CASE_ID欄位
	private void updateCASE_ID4File(BigDecimal insKeyno, String caseId) throws NotFoundException, DAOException {
		dam_obj = this.getDataAccessManager();
		TBIOT_MAIN_PDFVO tmpdfvo = (TBIOT_MAIN_PDFVO) dam_obj.findByPKey(TBIOT_MAIN_PDFVO.TABLE_UID, insKeyno);
		if(tmpdfvo != null) {
			tmpdfvo.setCASE_ID(caseId);
			dam_obj.update(tmpdfvo);
		}
	}
		
	//取得TBIOT_MAIN.INS_KEYNO
	private String getINS_KEYNO() throws DAOException, JBranchException {
		List<Map<String, Object>> INS_KEYNOList = new ArrayList<Map<String, Object>>();
		dam_obj = this.getDataAccessManager();
		QueryConditionIF qc = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuilder sb = new StringBuilder();

		sb.append(" select TBIOT_MAIN_SEQ.NEXTVAL from dual ");
		qc.setQueryString(sb.toString());

		INS_KEYNOList = dam_obj.exeQuery(qc);

		return INS_KEYNOList.get(0).get("NEXTVAL").toString();
	}
		
	//若有(1)移除保費資金證明檔案(2)修改檢附文件資料，則沒有儲存就回上一頁，需做檢核，看狀態是否該為"理專進件"
	public void updateStatus10(Object body, IPrimitiveMap header) throws Exception {
		IOT120InputVO inputVO = (IOT120InputVO) body;
		IOT120OutputVO outputVO = new IOT120OutputVO();
		
		dam_obj = this.getDataAccessManager();
		TBIOT_MAINVO tmvo = new TBIOT_MAINVO();
		tmvo = (TBIOT_MAINVO) dam_obj.findByPKey(TBIOT_MAINVO.TABLE_UID, new BigDecimal(inputVO.getINS_KEYNO()));
		if(tmvo != null) {
			tmvo.setSTATUS(new BigDecimal("10")); //理專進件
			dam_obj.update(tmvo);
		}
		this.sendRtnObject(outputVO);
	}
}
