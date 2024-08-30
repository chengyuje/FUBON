package com.systex.jbranch.app.server.fps.iot150;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

import com.systex.jbranch.fubon.commons.Manager;
import com.systex.jbranch.fubon.commons.PdfConfigVO;
import com.systex.jbranch.fubon.commons.PdfUtil;

import com.systex.jbranch.platform.common.errHandle.DAOException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.common.fps.table.TBIOT_MAINVO;
import com.systex.jbranch.common.io.util.PdfInputOutputUtils;
import com.systex.jbranch.comutil.collection.GenericMap;
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
import com.systex.jbranch.platform.common.util.CSVUtil;
import com.systex.jbranch.platform.common.util.StringUtil;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

import static org.apache.commons.lang.StringUtils.isNotBlank;

/**
 * IOT150
 *
 * @author Jimmy
 * @date 2016/09/22
 * @spec null
 */

@Component("iot150")
@Scope("request")
public class IOT150 extends FubonWmsBizLogic{
	Logger logger = LoggerFactory.getLogger(this.getClass());
	DataAccessManager dam_obj;

	public void queryData(Object body, IPrimitiveMap<Object> header){
		try {
			sendRtnObject(queryData(body));
		} catch (JBranchException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public IOT150OutputVO queryData(Object body) throws JBranchException{
		IOT150InputVO inputVO = (IOT150InputVO) body;
		IOT150OutputVO outputVO = new IOT150OutputVO();
		dam_obj = this.getDataAccessManager();
		QueryConditionIF qc = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuilder sb = new StringBuilder();
//		try {
			sb.append(" SELECT a.STATUS,a.INS_ID,a.POLICY_NO,a.INSURED_ID,a.INSURED_NAME,a.CUST_ID,a.PROPOSER_NAME,a.PROD_NAME,a.PROD_PERIOD, TO_CHAR(a.STATUS) AS STATUS_STR, ");
			sb.append(" a.OTH_TYPE,a.CURR_CD,a.PAY_TYPE,a.REJ_REASON,a.REJ_REASON_OTH,a.REMARK_BANK,a.SUBMIT_WAY,a.BATCH_SEQ, ");
			sb.append(" (a.REAL_PREMIUM + nvl((select sum(premium) from TBIOT_RIDER_DTL where INS_KEYNO = a.INS_KEYNO), 0))REAL_PREMIUM, ");
			sb.append(" to_char(a.SUBMIT_DATE,'YYYY/MM/DD HH24:MI:SS') as SUBMIT_DATE,to_char(a.INS_SUBMIT_DATE,'YYYY/MM/DD HH24:MI:SS') as INS_SUBMIT_DATE,a.INS_RCV_OPRID,a.REMARK_INS, ");
			sb.append(" to_char(a.INS_RCV_DATE,'YYYY/MM/DD HH24:MI:SS') as INS_RCV_DATE,a.OP_BATCH_NO,a.BEF_SIGN_NO,a.BEF_SIGN_OPRID,to_char(a.BEF_SIGN_DATE,'YYYY/MM/DD HH24:MI:SS') as BEF_SIGN_DATE, ");
			sb.append(" a.SIGN_OPRID,to_char(a.SIGN_DATE,'YYYY/MM/DD HH24:MI:SS') as SIGN_DATE,a.SIGN_NO,a.AFT_SIGN_OPRID, ");
			sb.append(" to_char(a.AFT_SIGN_DATE,'YYYY/MM/DD HH24:MI:SS') as AFT_SIGN_DATE,decode(a.reg_type, 1, 1, a.reg_type-1) as REG_TYPE, ");
			sb.append(" a.SIGN_INC,a.INS_KIND,a.INS_KEYNO, a.MAPPVIDEO_YN, a.PREMATCH_SEQ, ");
			sb.append(" DECODE (a.REJ_REASON, '10', a.REJ_REASON_OTH, ");
			sb.append(" (select b.PARAM_NAME from TBSYSPARAMETER b WHERE b.PARAM_TYPE = 'IOT.REJ_REASON' AND a.REJ_REASON = b.PARAM_CODE)) ");
			sb.append(" as REJ_REASON_SHOW, b.doc_name stay_bra_doc, c.doc_name to_hq_doc, a.CASE_ID, a.INS_SIGN_OPRID, ");
			sb.append(" to_char(a.INS_SIGN_DATE,'YYYY/MM/DD HH24:MI:SS') AS INS_SIGN_DATE, a.INS_SIGN_YN, ");
			sb.append(" F.COMPANY_NUM, E.CNAME AS INS_COM_NAME, CASE WHEN F.COMPANY_NUM = 82 THEN '富壽' ELSE '非富壽' END AS INS_SOURCE, ");
			sb.append(" (CASE WHEN F.FB_COM_YN = 'Y' THEN D.INSPRD_ID ELSE JPRD.PRODUCTID END) AS INSPRD_ID, ");
			sb.append(" (CASE WHEN F.FB_COM_YN = 'Y' THEN D.INSPRD_NAME ELSE JPRD.PRODUCTNAME END) AS INSPRD_NAME, ");
			sb.append(" (CASE WHEN F.FB_COM_YN = 'Y' THEN D.INSPRD_TYPE ELSE (CASE WHEN TRIM(JPRD.PRODUCTTYPE1) <> '投資型' THEN '1' ELSE '2' END) END) AS INSPRD_TYPE, ");
			sb.append(" PRE.CANCEL_CONTRACT_YN, a.BATCH_SETUP_EMPID, to_char(a.BATCH_SETUP_DATE, 'YYYY/MM/DD HH24:MI:SS') AS BATCH_SETUP_DATE, ");
			sb.append(" CASE WHEN A.PREMIUM_TRANSSEQ IS NOT NULL OR A.I_PREMIUM_TRANSSEQ IS NOT NULL OR A.P_PREMIUM_TRANSSEQ IS NOT NULL THEN 'Y' ELSE 'N' END AS RECORD_YN, ");
			sb.append(" PRE.SENIOR_AUTH_REMARKS, PRE.SENIOR_AUTH_ID ");
			sb.append(" FROM VWIOT_MAIN a " );
			sb.append(" LEFT JOIN (select ins_keyno, listagg(trim(decode(doc_seq, '99', doc_name_oth,doc_name)), chr(10) ) within group (order by doc_seq) as doc_name ");
			sb.append(" 	from TBIOT_DOC_CHK where doc_type = '1' And DOC_CHK = 'Y' group by ins_keyno) b ON a.ins_keyno = b.ins_keyno ");
			sb.append(" LEFT JOIN (select ins_keyno, listagg(trim(decode(doc_seq, '99', doc_name_oth,doc_name)), chr(10) ) within group (order by doc_seq) as doc_name ");
			sb.append(" 	from TBIOT_DOC_CHK where doc_type = '2' And DOC_CHK = 'Y' group by ins_keyno) c ON a.ins_keyno = c.ins_keyno ");
			sb.append(" LEFT JOIN TBPRD_INS_MAIN D on D.INSPRD_KEYNO = A.INSPRD_KEYNO ");
			sb.append(" LEFT JOIN TBJSB_INS_PROD_MAIN JPRD ON JPRD.PRODUCTSERIALNUM = A.INSPRD_KEYNO ");
			sb.append(" LEFT JOIN TBIOT_MAIN F ON F.INS_KEYNO = A.INS_KEYNO " );
			sb.append(" LEFT JOIN TBJSB_INS_PROD_COMPANY E ON E.SERIALNUM = F.COMPANY_NUM ");
			sb.append(" LEFT JOIN TBIOT_PREMATCH PRE ON PRE.PREMATCH_SEQ = A.PREMATCH_SEQ ");
			sb.append(" where a.status in ('38', '40', '42','45','46','47', '50', '52', '60', '62', '70', '80', '85') ");

			if(inputVO.getOP_BATCH_NO() != null){
				sb.append(" And a.OP_BATCH_NO = :op_batch_no ");
				qc.setObject("op_batch_no", inputVO.getOP_BATCH_NO());
			}
			if(!"".equals(inputVO.getREG_TYPE())){
				sb.append(" And decode(a.reg_type, 1, 1, a.reg_type-1) = :reg_type ");
				qc.setObject("reg_type", inputVO.getREG_TYPE());
			}
			if(!"".equals(inputVO.getSTATUS())){
				sb.append(" AND a.STATUS = :status ");
				qc.setObject("status", inputVO.getSTATUS());
			}
			if(!"".equals(inputVO.getBRANCH_NBR())){
				sb.append(" AND a.BRANCH_NBR = :branch_nbr ");
				qc.setObject("branch_nbr", inputVO.getBRANCH_NBR());
			}
			if(!"".equals(inputVO.getINS_ID())){
				sb.append(" AND a.INS_ID = :ins_id ");
				qc.setObject("ins_id", inputVO.getINS_ID());
			}
			if(!"".equals(inputVO.getCUST_ID())){
				sb.append(" AND a.CUST_ID = :cust_id ");
				qc.setObject("cust_id", inputVO.getCUST_ID());
			}
			if(inputVO.getKEYIN_DATE_F() != null || inputVO.getKEYIN_DATE_T() != null){
				sb.append(" And trunc(a.keyin_date) between trunc(:keyin_date_f) and trunc(:keyin_date_t) ");
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
				sb.append(" And trunc(a.apply_date) between trunc(:apply_date_f) and trunc(:apply_date_t) ");
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
				sb.append(" And a.POLICY_NO = :policy_no ");
				qc.setObject("policy_no", inputVO.getPOLICY_NO1()+"-"+inputVO.getPOLICY_NO2()+"-"+inputVO.getPOLICY_NO3());
			}
			if(!"".equals(inputVO.getPROD_NAME())){
				sb.append(" And a.PROD_NAME = :prod_name ");
				qc.setObject("prod_name", inputVO.getPROD_NAME());
			}
			if(!"".equals(inputVO.getBEF_SIGN_OPRID())){
				sb.append(" And a.BEF_SIGN_OPRID = :bef_sign_oprid ");
				qc.setObject("bef_sign_oprid", inputVO.getBEF_SIGN_OPRID());
			}
			if(inputVO.getBEF_SIGN_DATE() != null){
				sb.append(" And trunc(a.BEF_SIGN_DATE) = trunc(:bef_sign_date) ");
				qc.setObject("bef_sign_date", inputVO.getBEF_SIGN_DATE());
			}
			if(!"".equals(inputVO.getSIGN_OPRID())){
				sb.append(" And a.SIGN_OPRID = :sign_oprid ");
				qc.setObject("sign_oprid", inputVO.getSIGN_OPRID());
			}
			if(inputVO.getSIGN_DATE() != null){
				sb.append(" And trunc(a.SIGN_DATE) = trunc(:sign_date) ");
				qc.setObject("sign_date", inputVO.getSIGN_DATE());
			}
			if(!"".equals(inputVO.getAFT_SIGN_OPRID())){
				sb.append(" And a.AFT_SIGN_OPRID = :aft_sign_oprid ");
				qc.setObject("aft_sign_oprid", inputVO.getAFT_SIGN_OPRID());
			}
			if(inputVO.getAFT_SIGN_DATE() != null){
				sb.append(" And trunc(a.AFT_SIGN_DATE) = trunc(:aft_sign_date) ");
				qc.setObject("aft_sign_date", inputVO.getAFT_SIGN_DATE());
			}
			if(StringUtils.isNotBlank(inputVO.getBATCH_SETUP_EMPID())) {
				sb.append(" And a.BATCH_SETUP_EMPID = :batchEmpId ");
				qc.setObject("batchEmpId", inputVO.getBATCH_SETUP_EMPID());
			}
			if(inputVO.getBATCH_SETUP_DATE() != null) {
				sb.append(" And TRUNC(a.BATCH_SETUP_DATE) = TRUNC(:batchSetupDate) ");
				qc.setObject("batchSetupDate", inputVO.getBATCH_SETUP_DATE());
			}
//			sb.append(" Order by INS_ID,STATUS, KEYIN_DATE ");
			sb.append(" Order by a.INS_ID ");
			qc.setQueryString(sb.toString());
			outputVO.setIOT_MAIN(dam_obj.exeQuery(qc));
			if(outputVO.getIOT_MAIN().size()>0){
				for(Map<String, Object> map:outputVO.getIOT_MAIN()){
					List<Map<String, Object>> inList = new ArrayList<Map<String,Object>>();
					List<String> STAY_BRA_DOCList = new ArrayList<String>();

					List<Map<String, Object>> outList = new ArrayList<Map<String,Object>>();
					List<String> TO_HQ_DOCList = new ArrayList<String>();

					List<Map<String, Object>> SET_STATUSList = new ArrayList<Map<String,Object>>();

					//分行留存文件
					if(map.get("STAY_BRA_DOC") != null){
						STAY_BRA_DOCList = Arrays.asList(String.valueOf(map.get("STAY_BRA_DOC")).split("\n"));
						for(String s:STAY_BRA_DOCList){
							Map<String, Object> in = new HashMap<String, Object>();
							in.put("DOC_NAME", s);
							inList.add(in);
						}
					}
					map.put("inList", inList);

					//保險相關文件
					if(map.get("TO_HQ_DOC") != null){
						TO_HQ_DOCList = Arrays.asList(String.valueOf(map.get("TO_HQ_DOC")).split("\n"));
						for(String t:TO_HQ_DOCList){
							Map<String, Object> out = new HashMap<String, Object>();
							out.put("DOC_NAME", t);
							outList.add(out);
						}
					}
					map.put("outList", outList);

					//比對匯利專案狀態
					if("3".equals(inputVO.getREG_TYPE().toString())){
						qc = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
						sb = new StringBuilder();
						sb.append(" SELECT PARAM_CODE as DATA ,PARAM_NAME as LABEL FROM TBSYSPARAMETER WHERE PARAM_TYPE = 'IOT.REG4_STATUS' ");
						sb.append(" AND PARAM_CODE IN ( SELECT NEXT_STATUS FROM TBIOT_STATUS_FLOW WHERE INS_KIND = :ins_kind ");
						sb.append(" AND REG_TYPE = :reg_type AND CURR_STATUS = :status) ");
						qc.setObject("ins_kind", map.get("INS_KIND"));
						qc.setObject("reg_type", map.get("REG_TYPE"));
						qc.setObject("status", map.get("STATUS"));
						qc.setQueryString(sb.toString());
						SET_STATUSList = dam_obj.exeQuery(qc);
						if(SET_STATUSList.size()>0){
							map.put("SET_STATUS", SET_STATUSList);
						}
					}
				}
			}
			return outputVO;
//		} catch (Exception e) {
//			logger.error(String.format("發生錯誤:%s", StringUtil.getStackTraceAsString(e)));
//			throw new APException("系統發生錯誤請洽系統管理員");
//		}
	}

	public void submit(Object body, IPrimitiveMap<Object> header) throws JBranchException{
		Connection conn = null;
		String loginID = (String) getCommonVariable(SystemVariableConsts.LOGINID);
		IOT150InputVO inputVO = (IOT150InputVO) body;
		IOT150OutputVO outputVO = new IOT150OutputVO();
		if("".equals(inputVO.getREG_TYPE())){
			BigInteger regtype_change = new BigDecimal(inputVO.getIOT_MAINList().get(0).get("REG_TYPE").toString()).toBigInteger();
			String regtype = regtype_change.toString();
			inputVO.setREG_TYPE(regtype);
		}
		TBIOT_MAINVO tmvo = new TBIOT_MAINVO();
		List<Map<String, Object>> printList = new ArrayList<Map<String,Object>>();
		List<Map<String, Object>> printListU = new ArrayList<Map<String,Object>>();
		int total = 0;
		switch (inputVO.getREG_TYPE()) {
		case "3":
			for(int a = 0;a<inputVO.getIOT_MAINList().size();a++){
				if(inputVO.getIOT_MAINList().get(a).get("SELECT_SET_STATUS") != null){
					try {
						dam_obj = this.getDataAccessManager();
						dam_obj.setAutoCommit(false);
						tmvo = new TBIOT_MAINVO();
						BigDecimal ins_keyno_change = new BigDecimal(inputVO.getIOT_MAINList().get(a).get("INS_KEYNO").toString());
						tmvo = (TBIOT_MAINVO) dam_obj.findByPKey(TBIOT_MAINVO.TABLE_UID, ins_keyno_change);
						if(tmvo != null){
							BigDecimal status_change = new BigDecimal(inputVO.getIOT_MAINList().get(a).get("SELECT_SET_STATUS").toString());
							tmvo.setSTATUS(status_change);
							if(inputVO.getIOT_MAINList().get(a).get("REJ_REASON_OTH") != null){
								tmvo.setREJ_REASON_OTH(inputVO.getIOT_MAINList().get(a).get("REJ_REASON_OTH").toString());
							}
							if(inputVO.getIOT_MAINList().get(a).get("REMARK_BANK") != null){
								tmvo.setREMARK_BANK(inputVO.getIOT_MAINList().get(a).get("REMARK_BANK").toString());
							}
							java.util.Date date = new java.util.Date();
							Timestamp now_date = new Timestamp(date.getTime());
							tmvo.setAFT_SIGN_OPRID(loginID);
							tmvo.setAFT_SIGN_DATE(now_date);
//							tmvo.setBEF_SIGN_DATE(now_date);
//							tmvo.setBEF_SIGN_OPRID(loginID);
							dam_obj.update(tmvo);
						}
					} catch (Exception e) {
						logger.error(String.format("發生錯誤:%s", StringUtil.getStackTraceAsString(e)));
						throw new APException("系統發生錯誤請洽系統管理員");
					}
				}
			}
			sendRtnObject(true);
			break;
		default:
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

			if(passList.size()<= 0 && nopassList.size() <=0){
				sendRtnObject(false);
			}else{
				SerialNumberUtil sn = new SerialNumberUtil();
				java.util.Date date = new java.util.Date();
				SimpleDateFormat simple = new SimpleDateFormat("yyyyMMdd");
				String date_seqnum = simple.format(date).substring(3, 6);
				String B_SIGNNO = "";
				try {
					B_SIGNNO = "B" + date_seqnum + sn.getNextSerialNumber("IOT_B_BATCHNO");
				} catch (Exception e) {
					sn.createNewSerial("IOT_B_BATCHNO", "000000", null, null, null, 1, new Long("999999"), "y", new Long("0"), null);
					B_SIGNNO = "B" + date_seqnum + sn.getNextSerialNumber("IOT_B_BATCHNO");
				}
				String S_SIGNNO = "";
				try {
					S_SIGNNO = "S" + date_seqnum + sn.getNextSerialNumber("IOT_S_BATCHNO");
				} catch (Exception e) {
					sn.createNewSerial("IOT_S_BATCHNO", "000000", null, null, null, 1, new Long("999999"), "y", new Long("0"), null);
					S_SIGNNO = "S" + date_seqnum + sn.getNextSerialNumber("IOT_S_BATCHNO");
				}
				try {
					dam_obj = this.getDataAccessManager();
					dam_obj.setAutoCommit(false);
					tmvo = new TBIOT_MAINVO();

					int totalU = 0;
					//通過
					for (Map<String, Object> pass : passList) {
						tmvo = new TBIOT_MAINVO();
						BigInteger next_status_change = new BigDecimal(pass.get("next_status").toString()).toBigInteger();
						String next_status = next_status_change.toString();
						BigDecimal next_status_change1 = new BigDecimal(next_status);
						BigDecimal ins_keyno_change = new BigDecimal(pass.get("INS_KEYNO").toString());
						Timestamp now_date = new Timestamp(date.getTime());
						switch (next_status) {
						case "40":
							tmvo = (TBIOT_MAINVO) dam_obj.findByPKey(TBIOT_MAINVO.TABLE_UID, ins_keyno_change);
							tmvo.setSTATUS(next_status_change1);
							tmvo.setBEF_SIGN_NO(B_SIGNNO);
							tmvo.setBEF_SIGN_DATE(now_date);
							tmvo.setBEF_SIGN_OPRID(loginID);
							if(pass.get("REMARK_BANK") != null || StringUtils.isNotBlank(String.valueOf(pass.get("REMARK_BANK")))){
								tmvo.setREMARK_BANK(String.valueOf(pass.get("REMARK_BANK")));
							}
							dam_obj.update(tmvo);
							break;
						case "50":
							tmvo = (TBIOT_MAINVO) dam_obj.findByPKey(TBIOT_MAINVO.TABLE_UID, ins_keyno_change);
							tmvo.setSTATUS(next_status_change1);
							tmvo.setSIGN_NO(S_SIGNNO);
							tmvo.setSIGN_OPRID(loginID);
							tmvo.setSIGN_DATE(now_date);
							if(pass.get("REMARK_BANK") != null || StringUtils.isNotBlank(String.valueOf(pass.get("REMARK_BANK")))){
								tmvo.setREMARK_BANK(String.valueOf(pass.get("REMARK_BANK")));
							}
							dam_obj.update(tmvo);
							break;
						case "60":
							tmvo = (TBIOT_MAINVO) dam_obj.findByPKey(TBIOT_MAINVO.TABLE_UID, ins_keyno_change);
							tmvo.setSTATUS(next_status_change1);
							tmvo.setAFT_SIGN_OPRID(loginID);
							tmvo.setAFT_SIGN_DATE(now_date);
							if(pass.get("REMARK_BANK") != null || StringUtils.isNotBlank(String.valueOf(pass.get("REMARK_BANK")))){
								tmvo.setREMARK_BANK(String.valueOf(pass.get("REMARK_BANK")));
							}
							dam_obj.update(tmvo);

							//送進報表List,富壽才需要
							if(new BigDecimal(pass.get("COMPANY_NUM").toString()).toBigInteger().compareTo(BigInteger.valueOf(82)) == 0) {
								Map<String, Object> print = new HashMap<String, Object>();
//								total++;
								print.putAll(pass);
								String INSURED_NAME = "";
								if (print.get("INSURED_NAME") != null) {
									if (print.get("INSURED_NAME").toString().length() >= 2) {
										INSURED_NAME = print.get("INSURED_NAME").toString().replace(print.get("INSURED_NAME").toString().substring(1, 2), "O");
									} else {
										INSURED_NAME = print.get("INSURED_NAME").toString();
									}
								}
								
								print.put("INSURED_NAME", INSURED_NAME);
//								print.put("seq", total);
								
								/**
								 * #2091: WMS-CR-20240805-01_增檢核商品完訓日期調整保險電訪作業功能及投資型案件另外產生送件明細表：
								 * 送件點收，送件批號為"U"開頭之編碼，分開產生送件明細表(含補印)
								 * **/ 
								String op_batch_no = print.get("OP_BATCH_NO") != null ? print.get("OP_BATCH_NO").toString().substring(0, 1) : "";
								if ("U".equals(op_batch_no)) {
									totalU++;
									print.put("seq", totalU);
									printListU.add(print);
								} else {
									total++;
									print.put("seq", total);
									printList.add(print);
								}
							}
							break;
						default:
							break;
						}
					}

					//不通過
					for (Map<String, Object> nopass : nopassList) {
						tmvo = new TBIOT_MAINVO();
						BigInteger nopass_next_status_change = new BigDecimal(nopass.get("next_status").toString()).toBigInteger();
						String nopass_next_status = nopass_next_status_change.toString();
						BigDecimal nopass_next_status_change1 = new BigDecimal(nopass_next_status);
						BigDecimal nopass_ins_key_change = new BigDecimal(nopass.get("INS_KEYNO").toString());
						Timestamp nopass_now_date = new Timestamp(date.getTime());

						switch (nopass_next_status) {
						case "42":
							tmvo = (TBIOT_MAINVO) dam_obj.findByPKey(TBIOT_MAINVO.TABLE_UID, nopass_ins_key_change);
							tmvo.setSTATUS(nopass_next_status_change1);
							tmvo.setBEF_SIGN_OPRID(loginID);
							tmvo.setBEF_SIGN_DATE(nopass_now_date);
							tmvo.setREJ_REASON(nopass.get("nopassreason").toString());
							if("10".equals(nopass.get("nopassreason"))){
								tmvo.setREJ_REASON_OTH(nopass.get("REJ_REASON_OTH").toString());
							}
							if(nopass.get("REMARK_BANK") != null || StringUtils.isNotBlank(String.valueOf(nopass.get("REMARK_BANK")))){
								tmvo.setREMARK_BANK(String.valueOf(nopass.get("REMARK_BANK")));
							}
							dam_obj.update(tmvo);
							break;
						case "52":
							tmvo = (TBIOT_MAINVO) dam_obj.findByPKey(TBIOT_MAINVO.TABLE_UID, nopass_ins_key_change);
							tmvo.setSTATUS(nopass_next_status_change1);
							tmvo.setBEF_SIGN_NO(null);
							tmvo.setSIGN_OPRID(loginID);
							tmvo.setSIGN_DATE(nopass_now_date);
							tmvo.setREJ_REASON(nopass.get("nopassreason").toString());
							if("10".equals(nopass.get("nopassreason"))){
								tmvo.setREJ_REASON_OTH(nopass.get("REJ_REASON_OTH").toString());
							}
							if(nopass.get("REMARK_BANK") != null || StringUtils.isNotBlank(String.valueOf(nopass.get("REMARK_BANK")))){
								tmvo.setREMARK_BANK(String.valueOf(nopass.get("REMARK_BANK")));
							}
							dam_obj.update(tmvo);
							break;
						case "62":
							tmvo = (TBIOT_MAINVO) dam_obj.findByPKey(TBIOT_MAINVO.TABLE_UID, nopass_ins_key_change);
							tmvo.setSTATUS(nopass_next_status_change1);
							tmvo.setSIGN_NO(null);
							tmvo.setAFT_SIGN_OPRID(loginID);
							tmvo.setAFT_SIGN_DATE(nopass_now_date);
							tmvo.setREJ_REASON(nopass.get("nopassreason").toString());
							if("10".equals(nopass.get("nopassreason"))){
								tmvo.setREJ_REASON_OTH(nopass.get("REJ_REASON_OTH").toString());
							}
							if(nopass.get("REMARK_BANK") != null || StringUtils.isNotBlank(String.valueOf(nopass.get("REMARK_BANK")))){
								tmvo.setREMARK_BANK(String.valueOf(nopass.get("REMARK_BANK")));
							}
							dam_obj.update(tmvo);
							break;
						default:
							break;
						}
					}
					
					List<String> urlList = new ArrayList<String>();
					date = new java.util.Date();
					SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
					String now_date = format.format(date);
					String title = "";
					// 印報表
					if (printList.size() > 0) {
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
//								notifyClientToDownloadFile(PdfUtil.process(new PdfConfigVO(this.getDataAccessManager(), pdfURL)), "新契約簽署後送件明細表.pdf");
								urlList.add(pdfURL);
								break;
							case "2":
								title = "其他文件簽署後送件明細表";
								data.addParameter("title", title);
								report = generator.generateReport(txnCode, reportID, data);
								pdfURL = report.getLocation();
//								notifyClientToDownloadFile(PdfUtil.process(new PdfConfigVO(this.getDataAccessManager(), pdfURL)), "其他文件簽署後送件明細表.pdf");
								urlList.add(pdfURL);
								break;
							default:
								break;
						}
					}
					
					if (printListU.size() > 0) {
						String pdfURL = null;
						String txnCode = "IOT150";
						String reportID = "R1";
						ReportIF report = null;
						
						ReportFactory factory = new ReportFactory();
						ReportDataIF data = new ReportData();
						ReportGeneratorIF generator = factory.getGenerator();
						data.addParameter("printdate", now_date);
						data.addParameter("total", totalU);
						data.addRecordList("print", printListU);
						switch (inputVO.getREG_TYPE()) {
							case "1":
								title = "新契約簽署後送件明細表";
								data.addParameter("title", title);
								report = generator.generateReport(txnCode, reportID, data);
								pdfURL = report.getLocation();
								urlList.add(pdfURL);
//								notifyClientToDownloadFile(PdfUtil.process(new PdfConfigVO(this.getDataAccessManager(), pdfURL)), "新契約簽署後送件明細表.pdf");
								break;
							case "2":
								title = "其他文件簽署後送件明細表";
								data.addParameter("title", title);
								report = generator.generateReport(txnCode, reportID, data);
								pdfURL = report.getLocation();
								urlList.add(pdfURL);
//								notifyClientToDownloadFile(PdfUtil.process(new PdfConfigVO(this.getDataAccessManager(), pdfURL)), "其他文件簽署後送件明細表.pdf");
								break;
							default:
								break;
						}
					}
					if (CollectionUtils.isNotEmpty(urlList)) {
						String encryptUrl = PdfUtil.process(new PdfConfigVO(this.getDataAccessManager(), true, urlList));
						notifyClientToDownloadFile(encryptUrl, title + ".pdf");						
					}

					dam_obj.isAutoCommit();
					sendRtnObject(true);
				} catch (Exception e) {
					sendErrMsg("ehl_01_common_007");
				}
			}
			break;
		}
	}

	public void Report(Object body, IPrimitiveMap<Object> header) throws JBranchException{
		IOT150InputVO inputVO = (IOT150InputVO) body;
		List<String> urlList = new ArrayList<String>();
		int total = 0;
		int totalU = 0;
		List<Map<String, Object>> printList = new ArrayList<Map<String,Object>>();
		List<Map<String, Object>> printListU = new ArrayList<Map<String,Object>>();
		
		for (Map<String, Object> print : inputVO.getIOT_MAINList()) {
			String op_batch_no = print.get("OP_BATCH_NO") != null ? print.get("OP_BATCH_NO").toString().substring(0, 1) : "";
			BigInteger status_change = new BigDecimal(print.get("STATUS").toString()).toBigInteger();
			String status = status_change.toString();
			
			if ("60".equals(status)) {
				if ("U".equals(op_batch_no)) {
					totalU++;
					String INSURED_NAME = "";
					if (print.get("INSURED_NAME") != null) {
						if (print.get("INSURED_NAME").toString().length() >= 2) {
							INSURED_NAME = print.get("INSURED_NAME").toString().replace(print.get("INSURED_NAME").toString().substring(1, 2), "O");
						} else {
							INSURED_NAME = print.get("INSURED_NAME").toString();
						}
						
					}
					print.put("INSURED_NAME", INSURED_NAME);
					print.put("seq", totalU);
					printListU.add(print);
					
				} else {
					total++;
					String INSURED_NAME = "";
					if (print.get("INSURED_NAME") != null) {
						if (print.get("INSURED_NAME").toString().length() >= 2) {
							INSURED_NAME = print.get("INSURED_NAME").toString().replace(print.get("INSURED_NAME").toString().substring(1, 2), "O");
						} else {
							INSURED_NAME = print.get("INSURED_NAME").toString();
						}
						
					}
					print.put("INSURED_NAME", INSURED_NAME);
					print.put("seq", total);
					printList.add(print);
				}
			}
		}
		
		java.util.Date date = new java.util.Date();
		SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
		String now_date = format.format(date);
		
		String title = "";
		if (printList.size() > 0) {
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
				urlList.add(pdfURL);
//				notifyClientToDownloadFile(PdfUtil.process(new PdfConfigVO(this.getDataAccessManager(), pdfURL)), "新契約簽署後送件明細表.pdf");
				break;
			case "2":
				title = "其他文件簽署後送件明細表";
				data.addParameter("title", title);
				report = generator.generateReport(txnCode, reportID, data);
				pdfURL = report.getLocation();
				urlList.add(pdfURL);
//				notifyClientToDownloadFile(PdfUtil.process(new PdfConfigVO(this.getDataAccessManager(), pdfURL)), "其他文件簽署後送件明細表.pdf");
				break;
			default:
				break;
			}
		}
		
		if (printListU.size() > 0) {
			String pdfURL = null;
			String txnCode = "IOT150";
			String reportID = "R1";
			ReportIF report = null;
			
			ReportFactory factory = new ReportFactory();
			ReportDataIF data = new ReportData();
			ReportGeneratorIF generator = factory.getGenerator();
			data.addParameter("printdate", now_date);
			data.addParameter("total", totalU);
			data.addRecordList("print", printListU);
			switch (inputVO.getREG_TYPE()) {
			case "1":
				title = "新契約簽署後送件明細表";
				data.addParameter("title", title);
				report = generator.generateReport(txnCode, reportID, data);
				pdfURL = report.getLocation();
				urlList.add(pdfURL);
//				notifyClientToDownloadFile(PdfUtil.process(new PdfConfigVO(this.getDataAccessManager(), pdfURL)), "新契約簽署後送件明細表.pdf");
				break;
			case "2":
				title = "其他文件簽署後送件明細表";
				data.addParameter("title", title);
				report = generator.generateReport(txnCode, reportID, data);
				pdfURL = report.getLocation();
				urlList.add(pdfURL);
//				notifyClientToDownloadFile(PdfUtil.process(new PdfConfigVO(this.getDataAccessManager(), pdfURL)), "其他文件簽署後送件明細表.pdf");
				break;
			default:
				break;
			}
		}
		String encryptUrl = PdfUtil.process(new PdfConfigVO(this.getDataAccessManager(), true, urlList));
		notifyClientToDownloadFile(encryptUrl, title + ".pdf");
	}

	public void Export(Object body, IPrimitiveMap header) throws JBranchException {
		IOT150InputVO inputVO = (IOT150InputVO) body;

		if(CollectionUtils.isEmpty(inputVO.getIOT_MAINList()))
			return;
		
		XmlInfo xmlInfo = new XmlInfo();
		List<Map<String, Object>> list = inputVO.getIOT_MAINList();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String fileName = "壽險契約送件點收_" + sdf.format(new Date())
				+ "_"+ (String) getUserVariable(FubonSystemVariableConsts.LOGINID) + ".csv";
		List listCSV =  new ArrayList();
		for(Map<String, Object> map : list){
			String[] records = null;
			int i = 0;
			// 新契約進件/其他文件
			if("1".equals(map.get("REG_TYPE").toString().substring(0, 1)) || "2".equals(map.get("REG_TYPE").toString().substring(0, 1))){
				records = new String[36];
				records[i]   = checkIsNull(map, "");  // 備註(傳給人壽)
				records[++i] = xmlInfo.getVariable("IOT.MAIN_STATUS", checkIsNull(map, "STATUS_STR"), FormatHelper.FORMAT_3); // 狀態
				records[++i] = checkIsNull(map, "REJ_REASON_SHOW"); // 退件原因
				records[++i] = checkIsNull(map, "INS_ID"); // 保險文件編號
				records[++i] = checkIsNull(map, "SENIOR_AUTH_REMARKS"); // 高齡客戶投保關懷說明
				records[++i] = checkIsNull(map, "SENIOR_AUTH_ID"); // 高齡客戶關懷主管員編

				if("2".equals(map.get("REG_TYPE").toString().substring(0, 1)))
					records[++i] = checkIsNull(map, "POLICY_NO"); // 保單號碼

				records[++i] = checkIsNull(map, "INSURED_ID"); // 被保人ID
				records[++i] = checkIsNull(map, "INSURED_NAME"); // 被保險人姓名
				records[++i] = checkIsNull(map, "CUST_ID"); // 要保人ID
				records[++i] = checkIsNull(map, "PROPOSER_NAME"); // 要保人姓名
				records[++i] = checkIsNull(map, "INSPRD_ID"); // 主約險種

				if("2".equals(map.get("REG_TYPE").toString().substring(0, 1)))
					records[++i] = xmlInfo.getVariable("IOT.OTH_TYPE", checkIsNull(map, "OTH_TYPE"), FormatHelper.FORMAT_3); // 主約險種

				records[++i] = getInOutList(map, "outList"); // 保險相關文件

				if("1".equals(map.get("REG_TYPE").toString().substring(0, 1))){
					records[++i] = checkIsNull(map, "REAL_PREMIUM"); // 實收保費(原幣)
					records[++i] = checkIsNull(map, "CURR_CD"); // 幣別
					records[++i] = xmlInfo.getVariable("IOT.PAY_TYPE", checkIsNull(map, "PAY_TYPE"), FormatHelper.FORMAT_3); // 躉繳/分期繳
				}
				records[++i] = xmlInfo.getVariable("IOT.SUBMIT_WAY", checkIsNull(map, "SUBMIT_WAY"), FormatHelper.FORMAT_3); // 送達方式
				records[++i] = xmlInfo.getVariable("IOT.BATCH_SEQ", checkIsNull(map, "BATCH_SEQ"), FormatHelper.FORMAT_3); // 批次
				records[++i] = checkIsNull(map, "SUBMIT_DATE"); // 送達日
				records[++i] = checkIsNull(map, "INS_SUBMIT_DATE"); // 電子送件時間
				records[++i] = checkIsNull(map, "INS_RCV_OPRID"); // 人壽點收人
				records[++i] = checkIsNull(map, "REMARK_INS"); // 人壽回饋說明
				records[++i] = checkIsNull(map, "INS_RCV_DATE"); // 人壽簽收時間
				records[++i] = checkIsNull(map, "OP_BATCH_NO"); // 分行送件批號
				records[++i] = checkIsNull(map, "BATCH_SETUP_EMPID"); // 行助員編(批次設定)
				records[++i] = checkIsNull(map, "BATCH_SETUP_DATE"); // 行助簽收日(批次設定)
				records[++i] = checkIsNull(map, "BEF_SIGN_OPRID"); // 行助員編(簽署前)
				records[++i] = checkIsNull(map, "BEF_SIGN_DATE"); // 行助簽收日(簽署前)
				records[++i] = checkIsNull(map, "BEF_SIGN_NO"); // 總行送件批號
				records[++i] = checkIsNull(map, "SIGN_OPRID"); // 簽署人員編
				records[++i] = checkIsNull(map, "SIGN_DATE"); // 簽署日期
				records[++i] = checkIsNull(map, "SIGN_NO"); // 簽署人批號
				records[++i] = checkIsNull(map, "AFT_SIGN_OPRID"); // 行助員編(簽署後)
				records[++i] = checkIsNull(map, "AFT_SIGN_DATE"); // 行助簽收日(簽署後)
				records[++i] = getInOutList(map, "inList"); // 分行留存文件
				records[++i] = checkIsNull(map, "INS_SOURCE"); // 保單進件來源
				records[++i] = checkIsNull(map, "INS_COM_NAME"); // 保險公司
			}
			// 其他文件-匯率專案
			if("3".equals(map.get("REG_TYPE").toString().substring(0, 1))){
				records = new String[11];
				records[i]   = checkIsNull(map, "");  // 備註(傳給人壽)
				records[++i] = checkIsNull(map, "STATUS"); // 狀態
				records[++i] = checkIsNull(map, "INS_ID"); // 保險文件編號
				records[++i] = checkIsNull(map, "CUST_ID"); // 立約人ID
				records[++i] = checkIsNull(map, "PROPOSER_NAME"); // 立約人姓名
				records[++i] = checkIsNull(map, "PROD_NAME"); // 適用專案
				records[++i] = getInOutList(map, "outList"); // 保險相關文件
				records[++i] = checkIsNull(map, "OP_BATCH_NO"); // 分行送件批號
				records[++i] = checkIsNull(map, "AFT_SIGN_DATE"); // 簽收時間
				records[++i] = checkIsNull(map, "AFT_SIGN_OPRID"); // 簽收人
				records[++i] = getInOutList(map, "inList"); // 分行留存文件
			}


			listCSV.add(records);
		}
		//header
		String [] csvHeader = null;
		if("1".equals(list.get(0).get("REG_TYPE").toString().substring(0, 1)) || "2".equals(list.get(0).get("REG_TYPE").toString().substring(0, 1))){
			csvHeader = new String[36];
			int j = 0;
			csvHeader[j] = "備註(傳給人壽)";
			csvHeader[++j] = "狀態";
			csvHeader[++j] = "退件原因";
			csvHeader[++j] = "保險文件編號";
			csvHeader[++j] = "高齡客戶投保關懷說明";
			csvHeader[++j] = "高齡客戶關懷主管員編";
			if("2".equals(list.get(0).get("REG_TYPE").toString().substring(0, 1)))
				csvHeader[++j] = "保單號碼";
			csvHeader[++j] = "被保人ID";
			csvHeader[++j] = "被保險人姓名";
			csvHeader[++j] = "要保人ID";
			csvHeader[++j] = "要保人姓名";
			csvHeader[++j] = "主約險種";
			if("2".equals(list.get(0).get("REG_TYPE").toString().substring(0, 1)))
				csvHeader[++j] = "文件種類";
			csvHeader[++j] = "保險相關文件";
			if("1".equals(list.get(0).get("REG_TYPE").toString().substring(0, 1))){
				csvHeader[++j] = "實收保費(原幣)";
				csvHeader[++j] = "幣別";
				csvHeader[++j] = "躉繳/分期繳";
			}
			csvHeader[++j] = "送達方式";
			csvHeader[++j] = "批次";
			csvHeader[++j] = "送達日";
			csvHeader[++j] = "電子送件時間";
			csvHeader[++j] = "人壽點收人";
			csvHeader[++j] = "人壽回饋說明";
			csvHeader[++j] = "人壽簽收時間";
			csvHeader[++j] = "分行送件批號";
			csvHeader[++j] = "行助員編(批次設定)";
			csvHeader[++j] = "行助簽收日(批次設定)";
			csvHeader[++j] = "行助員編(簽署前)";
			csvHeader[++j] = "行助簽收日(簽署前)";
			csvHeader[++j] = "總行送件批號";
			csvHeader[++j] = "簽署人員編";
			csvHeader[++j] = "簽署日期";
			csvHeader[++j] = "簽署人批號";
			csvHeader[++j] = "行助員編(簽署後)";
			csvHeader[++j] = "行助簽收日(簽署後)";
			csvHeader[++j] = "分行留存文件";
			csvHeader[++j] = "保單進件來源";
			csvHeader[++j] = "保險公司";
		}if("3".equals(list.get(0).get("REG_TYPE").toString().substring(0, 1))){
			int j = 0;
			csvHeader = new String[11];
			csvHeader[j] = "備註(傳給人壽)";
			csvHeader[++j] = "狀態";
			csvHeader[++j] = "保險文件編號";
			csvHeader[++j] = "立約人ID";
			csvHeader[++j] = "立約人姓名";
			csvHeader[++j] = "適用專案";
			csvHeader[++j] = "保險相關文件";
			csvHeader[++j] = "分行送件批號";
			csvHeader[++j] = "簽收時間";
			csvHeader[++j] = "簽收人";
			csvHeader[++j] = "分行留存文件";
		}


		CSVUtil csv = new CSVUtil();
		csv.setHeader(csvHeader);
		csv.addRecordList(listCSV);
		String url = csv.generateCSV();
		notifyClientToDownloadFile(url, fileName);
		this.sendRtnObject(null);
	}

	private String checkIsNull(Map map, String key) {
		if(StringUtils.isNotBlank(String.valueOf(map.get(key))) && map.get(key) != null){
			return String.valueOf(map.get(key));
		}else{
			return "";
		}
	}

	private String getInOutList(Map map, String key) {
		if(StringUtils.isNotBlank(String.valueOf(map.get(key))) && map.get(key) != null){
			String out ="";
			for(Map<String, Object> item : (List<Map<String, Object>>)map.get(key)){
				out += (StringUtils.isBlank(out) ? "" : ", ") + ObjectUtils.toString(item.get("DOC_NAME"));
			}
			System.out.println(out);
			return out;
		} else {
			return "";
		}
	}

	public void getPdfFile(Object body, IPrimitiveMap header) throws Exception{
		GenericMap inputMap = new GenericMap((Map)body);
		DataAccessManager dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		StringBuffer sbr = new StringBuffer();
		sbr.append(" SELECT ");
		sbr.append("   PDF_FILE ");
		sbr.append(" FROM TBIOT_MAPP_PDF ");
		sbr.append(" WHERE CASE_ID IN( ");
		sbr.append("   SELECT CASE_ID FROM TBIOT_MAIN ");
		sbr.append("   WHERE REG_TYPE = 1 ");
		sbr.append("   AND INS_ID = :INS_ID ");
		sbr.append(" ) ");

		List<Map<String , Object>> resultList = dam.exeQuery(queryCondition.setQueryString(sbr.toString()).setObject("INS_ID", inputMap.getNotNullStr("INS_ID")));

		if(CollectionUtils.isEmpty(resultList)){
			sendErrMsg("無此文件");
			return;
		}

		GenericMap resutlGmap = new GenericMap(resultList.get(0));
		Blob pdfFile = resutlGmap.get("PDF_FILE");

		int blobLength = (int) pdfFile.length();
		byte[] reportData = pdfFile.getBytes(1, blobLength);
		//byte[] decryptReportData = Base64.decodeBase64(reportData);

		String filePath = "reports/" + UUID.randomUUID().toString();
		String url = new PdfInputOutputUtils().doWritePdfFile(reportData , filePath);

		notifyClientToDownloadFile(
				PdfUtil.process(new PdfConfigVO(
						this.getDataAccessManager(), url)), "電子投保.pdf");
	}

	/***
	 * 人壽待簽署管理報表
	 * @param body
	 * @param header
	 * @throws Exception
	 */
	public void genInsSigReport(Object body, IPrimitiveMap header) throws Exception {
		GenericMap inputMap = new GenericMap((Map)body);
		List<String> urlList = new ArrayList<String>();
		String pdfURL = null;
		String txnCode = "IOT150";
		String reportID = "R2";
		ReportIF report = null;
		SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
		String now_date = format.format(new java.util.Date());

		DataAccessManager dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuilder sb = new StringBuilder();

		sb.append(" SELECT ROW_NUMBER() OVER (ORDER BY A.INS_ID) AS RNUM, A.INS_ID, A.INSURED_NAME, A.OP_BATCH_NO, A.SIGN_OPRID, TO_CHAR(A.SIGN_DATE, 'YYYY/MM/DD') AS SIGN_DATE, ");
		sb.append(" P.PARAM_NAME AS STATUS, CASE WHEN M.EMP_ID IS NULL THEN A.INS_SIGN_OPRID ELSE M.EMP_ID END AS INS_SIGN_OPRID, ");
		sb.append(" TO_CHAR(A.INS_SIGN_DATE, 'YYYY/MM/DD') AS INS_SIGN_DATE, A.INS_SIGN_YN ");
		sb.append(" FROM VWIOT_MAIN A ");
		sb.append(" LEFT JOIN TBSYSPARAMETER P ON P.PARAM_TYPE = 'IOT.MAIN_STATUS' AND P.PARAM_CODE = A.STATUS ");
		sb.append(" LEFT JOIN TBORG_MEMBER M ON M.CUST_ID = A.INS_SIGN_OPRID ");
		sb.append(" WHERE A.OP_BATCH_NO LIKE 'S%' ");

		if(StringUtils.equals("Y", inputMap.getNotNullStr("UNSIGN"))) {
			//未簽署
			queryCondition.setQueryString(sb.toString() + " AND A.STATUS IN ('60', '70', '80') AND A.INS_SIGN_OPRID IS NULL AND A.INS_SIGN_DATE IS NULL AND A.INS_SIGN_YN IS NULL ");
//			queryCondition.setQueryString(sb.toString());
			List<Map<String, Object>> data_list = dam.exeQuery(queryCondition);

			ReportFactory factory = new ReportFactory();
			ReportDataIF data = new ReportData();
			ReportGeneratorIF generator = factory.getGenerator();

			data.addRecordList("print", data_list);
			data.addParameter("total", data_list.size());
			data.addParameter("printdate", now_date);
			data.addParameter("RPT_TYPE", "未簽署");
			data.addParameter("RPT_REMARK", "說明：請預審人員盡速至富壽簽署系統進行簽署作業");

			report = generator.generateReport(txnCode, reportID, data);
			pdfURL = report.getLocation();
			urlList.add(pdfURL);
		}

		if(StringUtils.equals("Y", inputMap.getNotNullStr("SIGNED"))) {
			//已簽署
			queryCondition.setQueryString(sb.toString() + " AND A.STATUS IN ('60', '70', '80') AND A.INS_SIGN_OPRID IS NOT NULL AND A.INS_SIGN_DATE IS NOT NULL AND A.INS_SIGN_YN IS NOT NULL AND A.STATUS <> '62' ");
//			queryCondition.setQueryString(sb.toString());
			List<Map<String, Object>> data_list = dam.exeQuery(queryCondition);

			ReportFactory factory = new ReportFactory();
			ReportDataIF data = new ReportData();
			ReportGeneratorIF generator = factory.getGenerator();

			data.addRecordList("print", data_list);
			data.addParameter("total", data_list.size());
			data.addParameter("printdate", now_date);
			data.addParameter("RPT_TYPE", "已簽署");
			data.addParameter("RPT_REMARK", "  ");

			report = generator.generateReport(txnCode, reportID, data);
			pdfURL = report.getLocation();
			urlList.add(pdfURL);
		}

		if(StringUtils.equals("Y", inputMap.getNotNullStr("REJECT"))) {
			//需重新簽署
			queryCondition.setQueryString(sb.toString() + " AND A.STATUS = '62' ");
//			queryCondition.setQueryString(sb.toString());
			List<Map<String, Object>> data_list = dam.exeQuery(queryCondition);

			ReportFactory factory = new ReportFactory();
			ReportDataIF data = new ReportData();
			ReportGeneratorIF generator = factory.getGenerator();

			data.addRecordList("print", data_list);
			data.addParameter("total", data_list.size());
			data.addParameter("printdate", now_date);
			data.addParameter("RPT_TYPE", "需重新簽署");
			data.addParameter("RPT_REMARK", "說明：該文件將由人壽退件回分行，重新送件");

			report = generator.generateReport(txnCode, reportID, data);
			pdfURL = report.getLocation();
			urlList.add(pdfURL);
		}

		String url = PdfUtil.mergePDF(urlList, false); //雙面列印為TRUE
    	notifyClientToDownloadFile(url, "視訊簽單審核後送人壽待簽署報表.pdf");
	}

	/**
     * 查詢非富壽資料
     */
    @SuppressWarnings({"unchecked"})
    public void inquireNotFubonData(Object body, IPrimitiveMap<Object> header) throws APException {
        IOT150NotFbInputVO inputVO = (IOT150NotFbInputVO) body;
        IOT150NotFbOutputVO outputVO = new IOT150NotFbOutputVO();
        try {
            Manager manager = Manager.manage(this.getDataAccessManager())
                    .append("select IOT.INS_KEYNO, ")
                    .append("       IOT.INS_ID, ")
                    .append("       DECODE(IOT.REG_TYPE, '1', '新契約', '非新契約') REG_TYPE_NAME, ")
                    .append("       IOT.REG_TYPE, ")
                    .append("       IOT.REMARK_BANK, ")
                    .append("       IOT.NOT_FB_BATCH_SEQ, ")
                    .append("       IOT.NOT_FB_BATCH_DATE,")
                    .append("       IOT.NOT_FB_SIGN_DATE, ")
                    .append("       IOT.NOT_FB_OP_NAME, ")
                    .append("		JPRD.PRODUCTID AS INSPRD_ID, ")
                    .append("       IOT.COMPANY_NUM ")
                    .append("from TBIOT_MAIN IOT ")
                    .append("left join TBJSB_INS_PROD_MAIN JPRD ON JPRD.PRODUCTSERIALNUM = IOT.INSPRD_KEYNO ")
                    .append("where IOT.COMPANY_NUM <> 82 ") // 非富壽案件
                    .append("and IOT.STATUS = '60' ") // 狀態固定為「總行點收送件(簽署後)」
                    .append("and IOT.REG_TYPE " + (inputVO.isNewReg() ? "=" : "<>") + "'1'")
                    .condition(isNotBlank(inputVO.getAftSignOprId()), "and IOT.AFT_SIGN_OPRID = :aftSignOprId ", "aftSignOprId", inputVO.getAftSignOprId())
                    .condition(inputVO.getCompanyNum() != null, "and IOT.COMPANY_NUM = :companyNum ", "companyNum", inputVO.getCompanyNum());

            if (inputVO.getAftSignDateStart() != null && inputVO.getAftSignDateEnd() != null) {
                manager.append("and IOT.AFT_SIGN_DATE between :aftSignDateStart and :aftSignDateEnd ")
                        .put("aftSignDateStart", inputVO.getAftSignDateStart())
                        .put("aftSignDateEnd", inputVO.getAftSignDateEnd());
            }

            if (inputVO.getNotFbBatchDateStart() != null && inputVO.getNotFbBatchDateEnd() != null) {
                manager.append("and IOT.NOT_FB_BATCH_DATE between :notFbBatchDateStart and :notFbBatchDateEnd ")
                        .put("notFbBatchDateStart", inputVO.getNotFbBatchDateStart())
                        .put("notFbBatchDateEnd", inputVO.getNotFbBatchDateEnd());
            }
            outputVO.setData(manager.query());
        } catch (JBranchException e) {
            logger.error(e.getMessage());
            throw new APException("查詢過程發生錯誤，請洽系統管理員！");
        }
        this.sendRtnObject(outputVO);
    }

    /**
     * 產生非富壽送件明細表
     * 每次請求只會是「單一送件類型+保險公司」
     */
    public void printNotFubonReport(Object body, IPrimitiveMap<Object> header) throws JBranchException {
        IOT150NotFbInputVO inputVO = (IOT150NotFbInputVO) (body);
        Collection<BigDecimal> iotKeySet = inputVO.getInsKeyNoMap().keySet();

        if (!inputVO.isRePrint()) { // 產生送件明細表才需要
            List<TBIOT_MAINVO> iotData = getIotMain(iotKeySet);

            checkNotFubonIotDataToPrint(iotData);
            updateNotFubonIotMainBeforePrint(inputVO, iotData);
        }

        List<Map<String, Object>> printData = getNotFubonReportPrintData(iotKeySet);
        String pdfURL = generateNotFubonReport(inputVO.isRePrint(), printData);

        notifyClientToDownloadFile(
                PdfUtil.process(new PdfConfigVO(this.getDataAccessManager(), pdfURL))
                , String.format("其他文件簽署後送件明細表%s.pdf", inputVO.isRePrint() ? "（補印）" : "")); //目前新契約沒有非富壽案件
        this.sendRtnObject(new IOT150NotFbOutputVO());
		}

    /**
     * 產生保險公司簽收紀錄（非富壽）
     **/
    public void reviewNotFubonData(Object body, IPrimitiveMap<Object> header) throws JBranchException {
        IOT150NotFbReviewFormVO formVO = (IOT150NotFbReviewFormVO) body;
        List<TBIOT_MAINVO> iotData = getIotMain(formVO.getInsKeyNo());
        checkNotFubonIotDataToReveiew(iotData);
        updateNotFubonIotMainWhileReview(formVO, iotData);

        this.sendRtnObject(new IOT150NotFbOutputVO());
    }

    private void updateNotFubonIotMainWhileReview(IOT150NotFbReviewFormVO formVO, List<TBIOT_MAINVO> iotData) throws DAOException {
        Timestamp notFbSignDate = new Timestamp(formVO.getNotFbSignDate().getTime());

        for (TBIOT_MAINVO mainvo: iotData) {
            mainvo.setNOT_FB_SIGN_DATE(notFbSignDate);
            mainvo.setNOT_FB_OP_NAME(formVO.getNotFbOpName());
            mainvo.setSTATUS(new BigDecimal(80));
            getDataAccessManager().update(mainvo);
        }
    }

    private void checkNotFubonIotDataToReveiew(List<TBIOT_MAINVO> iotData) throws APException {
        if (iotData.isEmpty())
            throw new APException("無任何資料可簽收！");

        // 已有「送保險公司日期」且「保險公司回饋簽收日期」為空白才可以進行簽收
        for (TBIOT_MAINVO vo : iotData) {
            if (vo.getNOT_FB_BATCH_DATE() == null ||
                    vo.getNOT_FB_SIGN_DATE() != null)
                throw new APException("已有新資料，請查詢後再次簽收！");
        }
    }

    private void checkNotFubonIotDataToPrint(List<TBIOT_MAINVO> iotData) throws APException {
        if (iotData.isEmpty())
            throw new APException("無任何資料可送件！");

        // 人壽點收人、人壽簽收時間均為空值才能進行列印
        for (TBIOT_MAINVO vo : iotData) {
            if (StringUtils.isNotBlank(vo.getNOT_FB_OP_NAME()) ||
                    vo.getNOT_FB_SIGN_DATE() != null)
                throw new APException("已有新資料，請再次查詢以產生最新的送件明細表！");
        }
    }

    private List<TBIOT_MAINVO> getIotMain(Collection<BigDecimal> insKeyNos) throws JBranchException {
        List<Criterion> criteria = new LinkedList<>();
        criteria.add(Restrictions.in("INS_KEYNO", insKeyNos));

        return (List<TBIOT_MAINVO>) this.getDataAccessManager()
                .findByCriteria(TBIOT_MAINVO.TABLE_UID, criteria);
    }

    private String generateNotFubonReport(boolean isRePrint, List<Map<String, Object>> printData) throws JBranchException {
        ReportDataIF reportData = new ReportData();

        reportData.addParameter("isRePrint", isRePrint); // 是否為「補印」
        reportData.addParameter("printdate", new SimpleDateFormat("yyyy/MM/dd").format(new Date())); // 列印日
        reportData.addParameter("total", printData.size()); // 總計筆數
        reportData.addParameter("companyName", printData.get(0).get("COMPANY_NAME")); // 收件公司
        reportData.addParameter("regTypeName", printData.get(0).get("REG_TYPE_NAME")); // 送件類型名稱（新契約、非新契約）
        reportData.addParameter("notFbBatchSeq", printData.get(0).get("NOT_FB_BATCH_SEQ")); // 送保險公司批號
        reportData.addRecordList("print", printData); // 送件資料

        return ReportFactory
                .getGenerator()
                .generateReport("IOT150", "R3", reportData)
                .getLocation();
    }

    private void updateNotFubonIotMainBeforePrint(IOT150NotFbInputVO inputVO, List<TBIOT_MAINVO> iotData) throws JBranchException {
        Map<BigDecimal, String> userInputMap = inputVO.getInsKeyNoMap();
        String notFbBatchSeq = getNotFbBatchSeq(inputVO.getCompanyNum());
        Timestamp notFbBatchDate = new Timestamp(new Date().getTime());

        for (TBIOT_MAINVO vo : iotData) {
            vo.setREMARK_BANK(userInputMap.get(vo.getINS_KEYNO()));
            vo.setNOT_FB_BATCH_DATE(notFbBatchDate);
            vo.setNOT_FB_BATCH_SEQ(notFbBatchSeq);
            this.getDataAccessManager().update(vo);
        }
    }

    /**
     * 批號邏輯=保險公司代碼+YYMMDD+2碼流水號
     **/
    @SuppressWarnings({"unchecked"})
    private String getNotFbBatchSeq(BigDecimal companyNum) throws JBranchException {
        List<Map<String, Object>> data = Manager.manage(this.getDataAccessManager())
                .append("select COM_CODE || TO_CHAR(SYSDATE, 'YYMMDD') || LPAD(TBIOT_MAIN_NOT_FB_BATCH_SEQ.NEXTVAL, 2, '0') BATCH_SEQ ")
                .append("from TBJSB_INS_PROD_COMPANY ")
                .append("where SERIALNUM = :companyNum ")
                .put("companyNum", companyNum)
                .query();
        return ObjectUtils.toString(data.get(0).get("BATCH_SEQ"));
    }

    @SuppressWarnings({"unchecked"})
    private List<Map<String, Object>> getNotFubonReportPrintData(Collection<BigDecimal> insKeyNoList) throws JBranchException {
        return Manager.manage(this.getDataAccessManager())
                .append("select RANK() over (order by IOT.INS_KEYNO) SEQ, ")
                .append("       IOT.INS_ID, ")
                .append("       IOT.INSURED_NAME, ")
                .append("       IOT.REMARK_BANK, ")
                .append("       DECODE(IOT.REG_TYPE, '1', '新契約', '非新契約') REG_TYPE_NAME, ")
                .append("       IOT.NOT_FB_SIGN_DATE, ")
                .append("       IOT.NOT_FB_OP_NAME, ")
                .append("       IOT.NOT_FB_BATCH_SEQ, ")
                .append("       BAT.OP_BATCH_NO, ")
                .append("       COM.CNAME COMPANY_NAME, ")
                .append("       IOT.COMPANY_NUM ")
                .append("from TBIOT_MAIN IOT ")
                .append("left join TBIOT_BATCH_INFO BAT on (BAT.BATCH_INFO_KEYNO = IOT.BATCH_INFO_KEYNO) ")
                .append("left join TBJSB_INS_PROD_COMPANY COM on (COM.SERIALNUM = IOT.COMPANY_NUM) ")
                .append("where IOT.INS_KEYNO in (:insKeyNos) ")
                .put("insKeyNos", insKeyNoList)
                .query();
    }

    /**
     * 取工作日
     **/
    @SuppressWarnings({"unchecked"})
    public void getBusDate(Object body, IPrimitiveMap header) throws APException {
        IOT150NotFbOutputVO outputVO = new IOT150NotFbOutputVO();
        try {
            List<Map<String, Object>> data = Manager.manage(this.getDataAccessManager())
                    // 查詢條件日期起日為「系統日-1工作天」
                    .append("select TRUNC(PABTH_UTIL.FC_GETBUSIDAY(SYSDATE, 'TWD', -1)) as BUS_DATE from DUAL ")
                    .query();
            outputVO.setBusDate((Date) data.get(0).get("BUS_DATE"));
        } catch (JBranchException e) {
            throw new APException("查詢過程發生錯誤，請洽系統管理員！");
        }
        sendRtnObject(outputVO);
    }
    
    //保險資金電訪報表
    public void exportRecordYN(Object body, IPrimitiveMap header) throws JBranchException {
		DataAccessManager dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuilder sb = new StringBuilder();

		sb.append(" SELECT A.INS_ID, A.CASE_ID, A.CUST_ID, A.INSURED_ID, A.INSPRD_ID, P.PARAM_NAME AS STATUS, ");
		sb.append("		CASE WHEN A.PREMIUM_TRANSSEQ IS NOT NULL OR A.I_PREMIUM_TRANSSEQ IS NOT NULL OR A.P_PREMIUM_TRANSSEQ IS NOT NULL THEN 'Y' ELSE 'N' END AS RECORD_YN, ");
		sb.append("		A.BEF_SIGN_OPRID, TO_CHAR(A.BEF_SIGN_DATE, 'YYYY/MM/DD') AS BEF_SIGN_DATE, ");
		sb.append("		A.AFT_SIGN_OPRID, TO_CHAR(A.AFT_SIGN_DATE, 'YYYY/MM/DD') AS AFT_SIGN_DATE ");
		sb.append(" FROM VWIOT_MAIN A ");
		sb.append(" LEFT JOIN TBSYSPARAMETER P ON P.PARAM_TYPE = 'IOT.MAIN_STATUS' AND P.PARAM_CODE = A.STATUS ");
		sb.append(" LEFT JOIN TBIOT_PREMATCH PRE ON PRE.PREMATCH_SEQ = A.PREMATCH_SEQ ");
		sb.append(" WHERE A.STATUS IN ('60', '70') "); //總行行助點收送件(簽署後), 傳輸人壽批次產生 
		sb.append(" AND NVL(PRE.CANCEL_CONTRACT_YN, 'N') = 'N' "); //非契撤案件
		sb.append(" AND (A.PREMIUM_TRANSSEQ IS NOT NULL OR A.I_PREMIUM_TRANSSEQ IS NOT NULL OR A.P_PREMIUM_TRANSSEQ IS NOT NULL) "); //電訪註記欄位為Y
		queryCondition.setQueryString(sb.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);			
			
//		XmlInfo xmlInfo = new XmlInfo();
//		List<Map<String, Object>> list = inputVO.getIOT_MAINList();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String fileName = "新契約保險資金電訪欄位註記_" + sdf.format(new Date()) + "_"+ (String) getUserVariable(FubonSystemVariableConsts.LOGINID) + ".csv";
		List listCSV =  new ArrayList();
		for(Map<String, Object> map : list){
			String[] records = null;
			int i = 0;
			records = new String[11];
			records[i]   = checkIsNull(map, "INS_ID");
			records[++i] = checkIsNull(map, "CASE_ID");
			records[++i] = checkIsNull(map, "CUST_ID");
			records[++i] = checkIsNull(map, "INSURED_ID");
			records[++i] = checkIsNull(map, "INSPRD_ID");
			records[++i] = checkIsNull(map, "STATUS");
			records[++i] = checkIsNull(map, "RECORD_YN");
			records[++i] = checkIsNull(map, "BEF_SIGN_OPRID");
			records[++i] = checkIsNull(map, "BEF_SIGN_DATE");
			records[++i] = checkIsNull(map, "AFT_SIGN_OPRID");
			records[++i] = checkIsNull(map, "AFT_SIGN_DATE");
			listCSV.add(records);
		}
		
		//header
		String [] csvHeader = null;
		csvHeader = new String[11];
		int j = 0;
		csvHeader[j] = "保險文件編號";
		csvHeader[++j] = "案件編號";
		csvHeader[++j] = "要保人ID";
		csvHeader[++j] = "被保人ID";
		csvHeader[++j] = "險種代碼";
		csvHeader[++j] = "文件狀態";
		csvHeader[++j] = "錄音註記";
		csvHeader[++j] = "點收經辦員編(簽署前)";
		csvHeader[++j] = "簽署人員員編";
		csvHeader[++j] = "點收經辦員編(簽署後)";
		csvHeader[++j] = "行助簽收日(簽署後)";

		CSVUtil csv = new CSVUtil();
		csv.setHeader(csvHeader);
		csv.addRecordList(listCSV);
		String url = csv.generateCSV();
		notifyClientToDownloadFile(url, fileName);
		
		this.sendRtnObject(null);
	}
}
