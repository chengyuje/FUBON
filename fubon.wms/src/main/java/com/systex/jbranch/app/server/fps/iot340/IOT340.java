package com.systex.jbranch.app.server.fps.iot340;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.systex.jbranch.fubon.commons.PdfConfigVO;
import com.systex.jbranch.fubon.commons.PdfUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.common.fps.table.TBIOT_PPT_MAINVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.serialnumber.SerialNumberUtil;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.report.ReportIF;
import com.systex.jbranch.platform.common.report.factory.ReportFactory;
import com.systex.jbranch.platform.common.report.generator.ReportGeneratorIF;
import com.systex.jbranch.platform.common.report.reportdata.ReportData;
import com.systex.jbranch.platform.common.report.reportdata.ReportDataIF;
import com.systex.jbranch.platform.common.util.StringUtil;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;


/**
 * IOT340
 *
 * @author Jimmy
 * @date 2016/10/07
 * @spec null
 */

@Component("iot340")
@Scope("request")
public class IOT340 extends FubonWmsBizLogic{
	Logger logger = LoggerFactory.getLogger(this.getClass());
	DataAccessManager dam_obj;

	public void queryData(Object body, IPrimitiveMap<Object> header) throws JBranchException{
		IOT340InputVO inputVO = (IOT340InputVO) body;
		IOT340OutputVO outputVO = new IOT340OutputVO();
		dam_obj = this.getDataAccessManager();
		QueryConditionIF qc = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuilder sb = new StringBuilder();
		try {
			sb.append(" SELECT INS_ID,PPT_TYPE,STATUS,POLICY_NO,INSURED_ID,INSURED_NAME,CUST_ID,PROPOSER_NAME, ");
			sb.append(" REAL_PREMIUM,REJ_REASON,REJ_REASON_OTH,REMARK_BANK,SUBMIT_WAY,BATCH_SEQ, ");
			sb.append(" OP_BATCH_NO,BEF_SIGN_OPRID,to_char(BEF_SIGN_DATE,'YYYY/MM/DD HH24:MI:SS') as BEF_SIGN_DATE, ");
			sb.append(" BEF_SIGN_NO,SIGN_OPRID,to_char(SIGN_DATE,'YYYY/MM/DD HH24:MI:SS') as SIGN_DATE,SIGN_NO,AFT_SIGN_OPRID,to_char(AFT_SIGN_DATE,'YYYY/MM/DD HH24:MI:SS') as AFT_SIGN_DATE,decode(reg_type, 1, 1, reg_type-1) as REG_TYPE, ");
			sb.append(" INS_KEYNO,INS_KIND,SIGN_INC, ");
			sb.append(" DECODE (a.REJ_REASON, '10', a.REJ_REASON_OTH, "
					+ "(select b.PARAM_NAME from TBSYSPARAMETER b WHERE b.PARAM_TYPE = 'IOT.REJ_REASON' AND a.REJ_REASON = b.PARAM_CODE)) as REJ_REASON_SHOW ");
			sb.append(" FROM VWIOT_PPT_MAIN a where status in ('30','40', '42', '50', '52', '60', '62') ");
			if(inputVO.getOP_BATCH_NO() != null){
				sb.append(" And OP_BATCH_NO = :op_batch_no ");
				qc.setObject("op_batch_no", inputVO.getOP_BATCH_NO());
			}
			if(!"".equals(inputVO.getREG_TYPE())){
				sb.append(" And reg_type = :reg_type ");
				qc.setObject("reg_type", inputVO.getREG_TYPE());
			}
			if(!"".equals(inputVO.getSTATUS())){
				sb.append(" AND STATUS = :status ");
				qc.setObject("status", inputVO.getSTATUS());
			}
			if(!"".equals(inputVO.getBRANCH_NBR())){
				sb.append(" AND BRANCH_NBR = :branch_nbr ");
				qc.setObject("branch_nbr", inputVO.getBRANCH_NBR());
			}
			if(!"".equals(inputVO.getINS_ID())){
				sb.append(" AND INS_ID = :ins_id ");
				qc.setObject("ins_id", inputVO.getINS_ID());
			}
			if(!"".equals(inputVO.getCUST_ID())){
				sb.append(" AND CUST_ID = :cust_id ");
				qc.setObject("cust_id", inputVO.getCUST_ID().toUpperCase());
			}
			if(inputVO.getKEYIN_DATE_F() != null || inputVO.getKEYIN_DATE_T() != null){
				sb.append(" And trunc(keyin_date) between :keyin_date_f and :keyin_date_t ");
				if(inputVO.getKEYIN_DATE_T() == null){
					qc.setObject("keyin_date_f", inputVO.getKEYIN_DATE_F());
					qc.setObject("keyin_date_t", inputVO.getKEYIN_DATE_F());
				}
				if(inputVO.getKEYIN_DATE_F() == null){
					qc.setObject("keyin_date_f", inputVO.getKEYIN_DATE_T());
					qc.setObject("keyin_date_t", inputVO.getKEYIN_DATE_T());
				}
				if(inputVO.getKEYIN_DATE_F() != null && inputVO.getKEYIN_DATE_T() != null){
					qc.setObject("keyin_date_f", inputVO.getKEYIN_DATE_F());
					qc.setObject("keyin_date_t", inputVO.getKEYIN_DATE_T());
				}
			}
			if(inputVO.getAPPLY_DATE_F() != null || inputVO.getAPPLY_DATE_T() != null){
				sb.append(" And trunc(apply_date) between :apply_date_f and :apply_date_t ");
				if(inputVO.getAPPLY_DATE_T() == null){
					qc.setObject("apply_date_f", inputVO.getAPPLY_DATE_F());
					qc.setObject("apply_date_t", inputVO.getAPPLY_DATE_F());
				}
				if(inputVO.getAPPLY_DATE_F() == null){
					qc.setObject("apply_date_f", inputVO.getAPPLY_DATE_T());
					qc.setObject("apply_date_t", inputVO.getAPPLY_DATE_T());
				}
				if(inputVO.getAPPLY_DATE_F() != null && inputVO.getAPPLY_DATE_T() != null){
					qc.setObject("apply_date_f", inputVO.getAPPLY_DATE_F());
					qc.setObject("apply_date_t", inputVO.getAPPLY_DATE_T());
				}
			}
			if(!"".equals(inputVO.getPOLICY_NO1()) && !"".equals(inputVO.getPOLICY_NO2()) && !"".equals(inputVO.getPOLICY_NO3())){
				sb.append(" And POLICY_NO = :policy_no ");
				qc.setObject("policy_no", inputVO.getPOLICY_NO1()+"-"+inputVO.getPOLICY_NO2()+"-"+inputVO.getPOLICY_NO3());
			}
			if(!"".equals(inputVO.getPROD_NAME())){
				sb.append(" And PROD_NAME = :prod_name ");
				qc.setObject("prod_name", inputVO.getPROD_NAME());
			}
			if(!"".equals(inputVO.getBEF_SIGN_OPRID())){
				sb.append(" And BEF_SIGN_OPRID = :bef_sign_oprid ");
				qc.setObject("bef_sign_oprid", inputVO.getBEF_SIGN_OPRID());
			}
			if(inputVO.getBEF_SIGN_DATE() != null){
				sb.append(" And BEF_SIGN_DATE = :bef_sign_date ");
				qc.setObject("bef_sign_date", inputVO.getBEF_SIGN_DATE());
			}
			if(!"".equals(inputVO.getSIGN_OPRID())){
				sb.append(" And SIGN_OPRID = :sign_oprid ");
				qc.setObject("sign_oprid", inputVO.getSIGN_OPRID());
			}
			if(inputVO.getSIGN_DATE() != null){
				sb.append(" And SIGN_DATE = :sign_date ");
				qc.setObject("sign_date", inputVO.getSIGN_DATE());
			}
			if(!"".equals(inputVO.getAFT_SIGN_OPRID())){
				sb.append(" And AFT_SIGN_OPRID = :aft_sign_oprid ");
				qc.setObject("aft_sign_oprid", inputVO.getAFT_SIGN_OPRID());
			}
			if(inputVO.getAFT_SIGN_DATE() != null){
				sb.append(" And AFT_SIGN_DATE = :aft_sign_date ");
				qc.setObject("aft_sign_date", inputVO.getAFT_SIGN_DATE());
			}
			sb.append(" Order by STATUS, KEYIN_DATE ");
			qc.setQueryString(sb.toString());
			outputVO.setIOT_MAIN(dam_obj.exeQuery(qc));
			if(outputVO.getIOT_MAIN().size()>0){
				for(Map<String, Object> map:outputVO.getIOT_MAIN()){
					List<Map<String, Object>> outList = new ArrayList<Map<String,Object>>();
					qc = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
					sb = new StringBuilder();
					sb.append(" SELECT  A.DOC_SEQ, DECODE(A.DOC_SEQ, '6', A.DOC_OTH, B.PARAM_NAME) as DOC_NAME ");
					sb.append(" FROM TBIOT_PPT_DOCCHK A ");
					sb.append(" LEFT JOIN TBSYSPARAMETER B ON B.PARAM_CODE = A.DOC_SEQ ");
					sb.append(" WHERE  B.PARAM_TYPE = 'IOT.PPT_DOC_SEQ' ");
					sb.append(" AND A.INS_KEYNO = :ins_keyno ");
					sb.append(" ORDER BY A.DOC_SEQ ");
					qc.setObject("ins_keyno", map.get("INS_KEYNO").toString());
					qc.setQueryString(sb.toString());
					outList = dam_obj.exeQuery(qc);
					if(outList.size()>0){
						map.put("outList", outList);
					}
				}
			}
			sendRtnObject(outputVO);
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s", StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}

	public void submit(Object body, IPrimitiveMap<Object> header) throws JBranchException{
		Connection conn = null;
		String loginID = (String) getCommonVariable(SystemVariableConsts.LOGINID);
		IOT340InputVO inputVO = (IOT340InputVO) body;
		IOT340OutputVO outputVO = new IOT340OutputVO();
		List<Map<String , Object>> passList = new ArrayList<Map<String,Object>>();
		List<Map<String , Object>> nopassList = new ArrayList<Map<String,Object>>();

		for(int a = 0;a<inputVO.getIOT_MAINList().size();a++){
			if("Y".equals(inputVO.getIOT_MAINList().get(a).get("nopass"))){
				if(!"".equals(inputVO.getIOT_MAINList().get(a).get("nopassreason"))){
					if("10".equals(inputVO.getIOT_MAINList().get(a).get("nopassreason").toString()) && "".equals(inputVO.getIOT_MAINList().get(a).get("REJ_REASON_OTH"))){
						throw new APException("ehl_01_common_022");
					}else{
						nopassList.add(inputVO.getIOT_MAINList().get(a));
					}
				}else{
					throw new APException("ehl_01_common_022");
				}
			}
			if("Y".equals(inputVO.getIOT_MAINList().get(a).get("pass"))){
				passList.add(inputVO.getIOT_MAINList().get(a));
			}
		}

		if(passList.size()>0 || nopassList.size()>0){
			SerialNumberUtil sn = new SerialNumberUtil();
			java.util.Date date = new java.util.Date();
			SimpleDateFormat simple = new SimpleDateFormat("yyyyMMdd");
			String date_seqnum = simple.format(date).substring(3, 6);
			String B_SIGNNO = "";
			try {
				B_SIGNNO = "B"+date_seqnum+sn.getNextSerialNumber("IOT_B_BATCHNO");
			} catch (Exception e) {
				sn.createNewSerial("IOT_B_BATCHNO", "000000", null, null, null, 1, new Long("999999"), "y", new Long("0"), null);
				B_SIGNNO = "B"+date_seqnum+sn.getNextSerialNumber("IOT_B_BATCHNO");
			}
			String S_SIGNNO = "";
			try {
				S_SIGNNO = "S"+date_seqnum+sn.getNextSerialNumber("IOT_S_BATCHNO");
			} catch (Exception e) {
				sn.createNewSerial("IOT_S_BATCHNO", "000000", null, null, null, 1, new Long("999999"), "y", new Long("0"), null);
				S_SIGNNO = "S"+date_seqnum+sn.getNextSerialNumber("IOT_S_BATCHNO");
			}
			try {
				dam_obj = this.getDataAccessManager();
				dam_obj.setAutoCommit(false);
				TBIOT_PPT_MAINVO tmvo = new TBIOT_PPT_MAINVO();

				for(Map<String, Object> pass:passList){
					tmvo = new TBIOT_PPT_MAINVO();
					BigInteger next_status_change = new BigDecimal(pass.get("next_status").toString()).toBigInteger();
					String next_status = next_status_change.toString();
					BigDecimal next_status_change1 = new BigDecimal(next_status);
					BigDecimal ins_keyno_change = new BigDecimal(pass.get("INS_KEYNO").toString());
					Timestamp now_date = new Timestamp(date.getTime());
					switch (next_status) {
					case "40":
						tmvo = (TBIOT_PPT_MAINVO) dam_obj.findByPKey(TBIOT_PPT_MAINVO.TABLE_UID, ins_keyno_change);
						tmvo.setSTATUS(next_status_change1);
						tmvo.setBEF_SIGN_NO(B_SIGNNO);
						tmvo.setBEF_SIGN_DATE(now_date);
						tmvo.setBEF_SIGN_OPRID(loginID);
						dam_obj.update(tmvo);
						break;
					case "50":
						tmvo = (TBIOT_PPT_MAINVO) dam_obj.findByPKey(TBIOT_PPT_MAINVO.TABLE_UID, ins_keyno_change);
						tmvo.setSTATUS(next_status_change1);
						tmvo.setSIGN_NO(S_SIGNNO);
						tmvo.setSIGN_OPRID(loginID);
						tmvo.setSIGN_DATE(now_date);
						dam_obj.update(tmvo);
						break;
					case "60":
						tmvo = (TBIOT_PPT_MAINVO) dam_obj.findByPKey(TBIOT_PPT_MAINVO.TABLE_UID, ins_keyno_change);
						tmvo.setSTATUS(next_status_change1);
						tmvo.setAFT_SIGN_OPRID(loginID);
						tmvo.setAFT_SIGN_DATE(now_date);
						dam_obj.update(tmvo);
						break;
					default:
						break;
					}
				}
				for(Map<String, Object> nopass:nopassList){
					tmvo = new TBIOT_PPT_MAINVO();
					BigInteger nopass_next_status_change = new BigDecimal(nopass.get("next_status").toString()).toBigInteger();
					String nopass_next_status = nopass_next_status_change.toString();
					BigDecimal nopass_next_status_change1 = new BigDecimal(nopass_next_status);
					BigDecimal nopass_ins_key_change = new BigDecimal(nopass.get("INS_KEYNO").toString());
					Timestamp nopass_now_date = new Timestamp(date.getTime());

					switch (nopass_next_status) {
					case "42":
						System.out.println("42");
						tmvo = (TBIOT_PPT_MAINVO) dam_obj.findByPKey(TBIOT_PPT_MAINVO.TABLE_UID, nopass_ins_key_change);
						tmvo.setSTATUS(nopass_next_status_change1);
						tmvo.setBEF_SIGN_OPRID(loginID);
						tmvo.setBEF_SIGN_DATE(nopass_now_date);
						tmvo.setREJ_REASON(nopass.get("nopassreason").toString());
						if("10".equals(nopass.get("nopassreason"))){
							tmvo.setREJ_REASON_OTH(nopass.get("REJ_REASON_OTH").toString());
						}
						dam_obj.update(tmvo);
						break;
					case "52":
						System.out.println("52");
						tmvo = (TBIOT_PPT_MAINVO) dam_obj.findByPKey(TBIOT_PPT_MAINVO.TABLE_UID, nopass_ins_key_change);
						tmvo.setSTATUS(nopass_next_status_change1);
						tmvo.setBEF_SIGN_NO(null);
						tmvo.setSIGN_OPRID(loginID);
						tmvo.setSIGN_DATE(nopass_now_date);
						tmvo.setREJ_REASON(nopass.get("nopassreason").toString());
						if("10".equals(nopass.get("nopassreason"))){
							tmvo.setREJ_REASON_OTH(nopass.get("REJ_REASON_OTH").toString());
						}
						dam_obj.update(tmvo);
						break;
					case "62":
						System.out.println("62");
						tmvo = (TBIOT_PPT_MAINVO) dam_obj.findByPKey(TBIOT_PPT_MAINVO.TABLE_UID, nopass_ins_key_change);
						tmvo.setSTATUS(nopass_next_status_change1);
						tmvo.setSIGN_NO(null);
						tmvo.setAFT_SIGN_OPRID(loginID);
						tmvo.setAFT_SIGN_DATE(nopass_now_date);
						tmvo.setREJ_REASON(nopass.get("nopassreason").toString());
						if("10".equals(nopass.get("nopassreason"))){
							tmvo.setREJ_REASON_OTH(nopass.get("REJ_REASON_OTH").toString());
						}
						dam_obj.update(tmvo);
						break;
					default:
						break;
					}
				}
				dam_obj.isAutoCommit();
				sendRtnObject(true);
			} catch (Exception e) {
//				try {
					sendErrMsg("ehl_01_common_007");
//					conn.rollback();
//				} catch (SQLException e1) {
//					// TODO Auto-generated catch block
//					e1.printStackTrace();
//				}
			}
		}
	}

	public void Report(Object body, IPrimitiveMap<Object> header) throws JBranchException{
		IOT340InputVO inputVO = (IOT340InputVO) body;
		int total = 0;
		List<Map<String, Object>> printList = new ArrayList<Map<String,Object>>();
		for(Map<String, Object> print:inputVO.getIOT_MAINList()){
			BigInteger status_change = new BigDecimal(print.get("STATUS").toString()).toBigInteger();
			String status = status_change.toString();
			if("60".equals(status)){
				total++;
				String INSURED_NAME = print.get("INSURED_NAME").toString().replace(print.get("INSURED_NAME").toString().substring(1, 2), "O");
				print.put("INSURED_NAME", INSURED_NAME);
				print.put("seq", total);
				printList.add(print);
			}
		}
		if(printList.size()>0){
			String title = "";
			java.util.Date date = new java.util.Date();
			SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
			String now_date = format.format(date);

			String pdfURL = null;
			String txnCode = "IOT150";
			String reportID = "R1";
			ReportIF report = null;

			ReportFactory factory = new ReportFactory();
			ReportDataIF data = new ReportData();
			ReportGeneratorIF generator = factory.getGenerator();
			data.addParameter("printdate", now_date);
			data.addParameter("total", total);
			data.addRecordList("print", printList);
			switch (inputVO.getREG_TYPE()) {
			case "1":
				title = "新契約簽署後送件明細表";
				data.addParameter("title", title);
				report = generator.generateReport(txnCode, reportID, data);
				pdfURL = report.getLocation();
				notifyClientToDownloadFile(PdfUtil.process(new PdfConfigVO(this.getDataAccessManager(), pdfURL))
						, "新契約簽署後送件明細表.pdf");
//				notifyClientViewDoc(pdfURL, "pdf");
				break;
			case "2":
				title = "其他文件簽署後送件明細表";
				data.addParameter("title", title);
				report = generator.generateReport(txnCode, reportID, data);
				pdfURL = report.getLocation();
				notifyClientToDownloadFile(PdfUtil.process(new PdfConfigVO(this.getDataAccessManager(), pdfURL))
						, "其他文件簽署後送件明細表.pdf");
				break;
			default:
				break;
			}
		}

	}





}
