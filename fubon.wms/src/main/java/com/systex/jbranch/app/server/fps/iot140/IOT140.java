package com.systex.jbranch.app.server.fps.iot140;

import com.systex.jbranch.app.common.fps.table.TBIOT_MAINVO;
import com.systex.jbranch.app.server.fps.iot920.IOT920;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.fubon.commons.PdfConfigVO;
import com.systex.jbranch.fubon.commons.PdfUtil;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.report.ReportIF;
import com.systex.jbranch.platform.common.report.factory.ReportFactory;
import com.systex.jbranch.platform.common.report.generator.ReportGeneratorIF;
import com.systex.jbranch.platform.common.report.reportdata.ReportData;
import com.systex.jbranch.platform.common.report.reportdata.ReportDataIF;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.common.util.StringUtil;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * IOT140
 *
 * @author Jimmy
 * @date 2016/09/22
 * @spec null
 *
 *
 * @author Eli
 * @date 20190626 Mantis#6608  針對其他文件其批次編號邏輯更改
 */

@Component("iot140")
@Scope("request")
public class IOT140 extends FubonWmsBizLogic {
	Logger logger = LoggerFactory.getLogger(this.getClass());
	DataAccessManager dam_obj = null;

	public void Initial(Object body, IPrimitiveMap<Object> header)
			throws JBranchException {
		IOT140InputVO inputVO = (IOT140InputVO) body;
		IOT140OutputVO outputVO = new IOT140OutputVO();
		String loginBranch = (String) getCommonVariable(SystemVariableConsts.LOGINBRH);
		dam_obj = getDataAccessManager();
		QueryConditionIF qc = dam_obj
				.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuilder sb = new StringBuilder();
		try {
			sb.append(" select OP_BATCH_NO,count(*) cnt ");
			sb.append(" from VWIOT_MAIN ");
			sb.append(" where status='42' and branch_nbr = :LOGINBRH ");
			sb.append(" Group by OP_BATCH_NO ");
			qc.setObject("LOGINBRH", loginBranch);
			qc.setQueryString(sb.toString());
			outputVO.setBounced(dam_obj.exeQuery(qc));
			sendRtnObject(outputVO);
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}

	public void queryData(Object body, IPrimitiveMap<Object> header)
			throws JBranchException {
		String loginBranch = (String) getCommonVariable(SystemVariableConsts.LOGINBRH);
		IOT140InputVO inputVO = (IOT140InputVO) body;
		IOT140OutputVO outputVO = new IOT140OutputVO();
		dam_obj = getDataAccessManager();
		QueryConditionIF qc = dam_obj
				.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuilder sb = new StringBuilder();
//		try {
			sb.append(" Select A.KEYIN_DATE, A.INS_KEYNO, A.STATUS, A.BRANCH_NBR, A.INS_ID, A.CASE_ID, ");
			sb.append(" to_char(A.APPLY_DATE,'YYYY/MM/DD') as APPLY_DATE, A.OTH_TYPE, A.PROPOSER_NAME, A.INSURED_NAME, ");
			sb.append(" A.TERMINATED_INC, D.OP_BATCH_NO, A.SIGN_INC, A.REG_TYPE,   ");
			sb.append(" (SELECT PROD_PERIOD FROM TBPRD_FX_DISCOUNT_PROD WHERE FXD_KEYNO = A.FXD_KEYNO ) PROD_PERIOD, "); //適用專案
			sb.append(" DECODE (A.REJ_REASON, '10', A.REJ_REASON_OTH,(select b.PARAM_NAME from TBSYSPARAMETER b  ");
			sb.append(" 	WHERE b.PARAM_TYPE = 'IOT.REJ_REASON' AND A.REJ_REASON = b.PARAM_CODE)) as REJ_REASON, ");
			sb.append(" A.LOAN_PRD_YN, A.QC_IMMI, to_char(A.DOC_KEYIN_DATE,'YYYY/MM/DD') as DOC_KEYIN_DATE, C.MAPPVIDEO_YN, C.DIGITAL_AGREESIGN_YN, ");
			sb.append(" A.COMPANY_NUM, B.CNAME AS INS_COM_NAME, CASE WHEN A.COMPANY_NUM = 82 THEN '富壽' ELSE '非富壽' END AS INS_SOURCE, A.FB_COM_YN, ");
			sb.append(" (CASE WHEN A.FB_COM_YN = 'Y' THEN P.INSPRD_ID ELSE JPRD.PRODUCTID END) AS INSPRD_ID, ");
			sb.append(" (CASE WHEN A.FB_COM_YN = 'Y' THEN P.INSPRD_NAME ELSE JPRD.PRODUCTNAME END) AS INSPRD_NAME, ");
			sb.append(" (CASE WHEN A.FB_COM_YN = 'Y' THEN P.INSPRD_TYPE ELSE (CASE WHEN TRIM(JPRD.PRODUCTTYPE1) <> '投資型' THEN '1' ELSE '2' END) END) AS INSPRD_TYPE, ");
			sb.append(" (CASE WHEN A.FB_COM_YN = 'Y' THEN P.INSPRD_TYPE ELSE (CASE WHEN TRIM(JPRD.PRODUCTTYPE1) <> '投資型' THEN '1' ELSE '2' END) END) AS PRODUCT_TYPE, ");
			sb.append(" E.ITEM_REMRK ");
			sb.append(" From TBIOT_MAIN A ");
			sb.append(" LEFT JOIN TBPRD_INS_MAIN P on P.INSPRD_KEYNO = A.INSPRD_KEYNO ");
			sb.append(" LEFT JOIN TBJSB_INS_PROD_COMPANY B ON B.SERIALNUM = A.COMPANY_NUM ");
			sb.append(" LEFT JOIN TBIOT_PREMATCH C ON C.PREMATCH_SEQ = A.PREMATCH_SEQ ");
			sb.append(" LEFT JOIN TBIOT_BATCH_INFO D ON D.BATCH_INFO_KEYNO = A.BATCH_INFO_KEYNO ");
			sb.append(" LEFT JOIN TBJSB_INS_PROD_MAIN JPRD ON JPRD.PRODUCTSERIALNUM = A.INSPRD_KEYNO ");
			sb.append(" LEFT JOIN TBCRM_NPOLM E ON E.APPL_ID = A.CUST_ID ");
			sb.append(" 	AND E.POLICY_NO = A.POLICY_NO1 ");   
			sb.append(" 	AND E.POLICY_SEQ = A.POLICY_NO2 ");
			sb.append(" 	AND E.ID_DUP = NVL(A.POLICY_NO3, ' ') ");
			sb.append(" where 1 = 1 And A.BRANCH_NBR = :branch_nbr ");
			sb.append(" And  decode(A.REG_TYPE, 1, 1, A.REG_TYPE-1) = :reg_type ");
			qc.setObject("branch_nbr", loginBranch);
			qc.setObject("reg_type", inputVO.getREG_TYPE());
			if(inputVO.getINCLUDED_REJECT() != null){
				if("Y".equals(inputVO.getINCLUDED_REJECT().toString())){
					sb.append(" And A.STATUS in ('10','20','30','42','45','46') ");
				}else{
					sb.append(" And A.STATUS in ('10','20','30','46') ");
				}
			}else{
				sb.append(" And A.STATUS in ('10','20','30','46') ");
			}
			sb.append(" and trunc(A.KEYIN_DATE) >= NVL(to_date(:keyin_date_f,'yyyyMMdd') , A.KEYIN_DATE) ");
			if(inputVO.getKEYIN_DATE_F().equals(inputVO.getKEYIN_DATE_T()))
				sb.append(" and trunc(A.KEYIN_DATE) >= NVL(to_date(:keyin_date_t,'yyyyMMdd') , A.KEYIN_DATE) ");
			if(!inputVO.getKEYIN_DATE_F().equals(inputVO.getKEYIN_DATE_T()))
				sb.append(" and trunc(A.KEYIN_DATE) <= NVL(to_date(:keyin_date_t,'yyyyMMdd') , A.KEYIN_DATE) ");

			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			String d_f_s = sdf.format(inputVO.getKEYIN_DATE_F());
			String d_t_s = sdf.format(inputVO.getKEYIN_DATE_T());
			qc.setObject("keyin_date_f", d_f_s);
			qc.setObject("keyin_date_t", d_t_s);
			if (!"".equals(inputVO.getCUST_ID())) {
				sb.append(" And A.CUST_ID = :custid ");
				qc.setObject("custid", inputVO.getCUST_ID().toUpperCase());
			}
			if (!"".equals(inputVO.getINSURED_ID())) {
				sb.append(" And A.INSURED_ID = :insured_id ");
				qc.setObject("insured_id", inputVO.getINSURED_ID().toUpperCase());
			}
			if (!"".equals(inputVO.getPOLICY_NO1())
					&& !"".equals(inputVO.getPOLICY_NO2())
					&& !"".equals(inputVO.getPOLICY_NO3())) {
				sb.append(" And A.POLICY_NO1||'-'||A.POLICY_NO2||'-'||A.POLICY_NO3 = :policy_no ");
				qc.setObject("policy_no",
						inputVO.getPOLICY_NO1() + "-" + inputVO.getPOLICY_NO2()
								+ "-" + inputVO.getPOLICY_NO3());
			}
			if (!"".equals(inputVO.getINS_ID())) {
				sb.append(" And A.INS_ID = :insid ");
				qc.setObject("insid", inputVO.getINS_ID());
			}
			if (!"".equals(inputVO.getOP_BATCH_OPRID())) {
				sb.append(" And A.RECRUIT_ID = :recruitid ");
				qc.setObject("recruitid", inputVO.getOP_BATCH_OPRID());
			}
			if(StringUtils.isNotBlank(inputVO.getFB_COM_YN())) {
				if(StringUtils.equals("Y", inputVO.getFB_COM_YN())) {
					sb.append(" And A.COMPANY_NUM = 82 ");
				} else if(StringUtils.equals("N", inputVO.getFB_COM_YN())) {
					sb.append(" And A.COMPANY_NUM <> 82 ");
				}
			}

			sb.append(" ORDER BY A.STATUS ,D.OP_BATCH_NO, A.INS_ID");

			qc.setQueryString(sb.toString());
			outputVO.setIOT_MAIN(dam_obj.exeQuery(qc));
			sendRtnObject(outputVO);
//		} catch (Exception e) {
//			logger.error(String.format("發生錯誤:%s", StringUtil.getStackTraceAsString(e)));
//			throw new APException("系統發生錯誤請洽系統管理員");
//		}

	}

	/******* 列印功能
	 * @return *******/
	public List<String> print(String regType, String opBatchNo,Boolean reprint) throws JBranchException {
		String txnCode = "IOT140";
		ReportIF report = null;
		String url = null;

		ReportGeneratorIF gen = ReportFactory.getGenerator();

		dam_obj = getDataAccessManager();
		QueryConditionIF qc = dam_obj
				.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		StringBuffer sb = new StringBuffer();
		sb.append("SELECT A.INS_ID,")	// 保險文件編號
			.append(" (select param_name from TBSYSPARAMETER where param_type = 'IOT.CM_FLAG' ")
			.append(" and param_code = A.INSURED_CM_FLAG)CM_FLAG, ")// 客訴註記 (待澄清)
			.append(" A.INSURED_ID,")	// 被保險人ID
			.append(" A.INSURED_NAME,")	// 被保險人姓名
			.append(" A.CUST_ID, A.PROPOSER_NAME,")//立約人by 匯利
			.append(" (CASE WHEN A.FB_COM_YN = 'Y' THEN P.INSPRD_ID ELSE JPRD.PRODUCTID END) AS INSPRD_ID, ")
			.append(" (CASE WHEN A.FB_COM_YN = 'Y' THEN P.INSPRD_NAME ELSE JPRD.PRODUCTNAME END) AS INSPRD_NAME, ")
			.append(" (CASE WHEN A.FB_COM_YN = 'Y' THEN P.INSPRD_TYPE ELSE (CASE WHEN TRIM(JPRD.PRODUCTTYPE1) <> '投資型' THEN '1' ELSE '2' END) END) AS INSPRD_TYPE, ")
			// 產品類型
			.append(" DECODE(A.REG_TYPE, 3, "
					+ "(select param_name from TBSYSPARAMETER where param_type='IOT.OTH_TYPE' and param_code = A.OTH_TYPE),"
					+ "(select param_name from TBSYSPARAMETER where param_type='IOT.OTH_TYPE' and param_code = P.INSPRD_TYPE)) as INSPRD_TYPE_OTHER ,")
			.append(" (SELECT EMP_NAME FROM TBORG_MEMBER WHERE EMP_ID = A.RECRUIT_ID) AS RECRUIT_NAME, ")	// 銷售人員
			.append(" (SELECT PROD_NAME FROM TBPRD_FX_DISCOUNT_PROD WHERE FXD_KEYNO = A.FXD_KEYNO ) AS PROD_NAME, ")//匯利專案檔期
			.append(" (SELECT PROD_PERIOD FROM TBPRD_FX_DISCOUNT_PROD WHERE FXD_KEYNO = A.FXD_KEYNO ) AS PROD_PERIOD, ")//匯利專案檔期
			.append(" A.KEYIN_DATE, ")
			.append(" A.SIGN_INC, ")//是否需簽屬
			.append(" DOC_NAME_2,")		// 保險相關文件
			.append(" DOC_NAME_1,")		// 分行留存文件
			.append(" A.QC_ERASER, ")		// 可擦拭書寫
			.append(" A.QC_IMMI, ")		// 檢查有檢附立即投入批註書(Y/N)
			.append(" A.PREMATCH_SEQ, ")	// 購買檢核編號
			.append(" A.CASE_ID, ")	// 案件編號
			.append(" CASE WHEN SUBSTR(D.OP_BATCH_NO, 1, 1) IN ('E', 'S', 'N') THEN '案件編號：' ELSE '' END AS CASE_TITLE, ")	// 案件編號TITLE
			.append(" A.COMPANY_NUM, ") //保險公司代碼
			.append(" E.CNAME AS INS_COM_NAME, ") //保險公司名稱
			.append(" CASE WHEN A.COMPANY_NUM = 82 THEN '富壽' ELSE '非富壽' END AS INS_SOURCE, ") //進件來源
			.append(" (A.POLICY_NO1 || ' - ' || NVL(A.POLICY_NO2, '  ') || ' - ' || NVL(A.POLICY_NO3, '  ')) AS POLICY_NBR, ")
			.append(" PARAM.PARAM_NAME AS OTH_BARCODE, ") //其他送件登錄分案頁BARCODE
			.append(" NVL(PRE.CANCEL_CONTRACT_YN, 'N') AS CANCEL_CONTRACT_YN, ")
			.append(" CASE WHEN A.PREMIUM_TRANSSEQ IS NOT NULL OR A.I_PREMIUM_TRANSSEQ IS NOT NULL OR A.P_PREMIUM_TRANSSEQ IS NOT NULL THEN 'Y' ELSE 'N' END AS RECORD_YN, ")
			.append(" CASE WHEN PRE.SENIOR_AUTH_REMARKS IS NULL THEN '' ELSE '高齡客戶投保關懷說明：' || PRE.SENIOR_AUTH_REMARKS END AS SENIOR_AUTH_REMARKS, ")
			.append(" CASE WHEN PRE.SENIOR_AUTH_ID IS NULL THEN '' ELSE '關懷主管員編：' || PRE.SENIOR_AUTH_ID END AS SENIOR_AUTH_ID, ")
			.append(" NVL(DCHK.DOC_CHK, 'N') AS FOREIGN_DOC_YN, ") //”境外匯款聲明書”有勾選為Y，否則為N
			.append(" CASE WHEN A.FIRST_PAY_WAY = '1' AND A.PAY_SERV_RETURN_CODE = '100' THEN '匯款件線上填寫繳款服務單成功' ")
			.append(" 	   WHEN A.FIRST_PAY_WAY = '1' AND A.PAY_SERV_RETURN_CODE <> '100' THEN '匯款件需檢附紙本繳款服務單或便簽' ELSE '' END AS REMIT_REMARK, ")
			.append(" CASE WHEN A.FIRST_PAY_WAY = '1' AND A.REMIT_COUNTRY_FLAG = '2' THEN '匯款國家非境內應檢附境外匯款聲明書' ELSE '' END AS REMIT_COUNTRY_REMARK ")
			.append(" FROM TBIOT_MAIN A ")
			.append(" LEFT JOIN (select distinct INS_KEYNO, listagg(decode(DOC_SEQ,99,'■'||DOC_NAME||DOC_NAME_OTH,'■'||DOC_NAME), chr(10)) within group (order by DOC_SEQ)")
			.append(" over (partition by INS_KEYNO )DOC_NAME_1 from TBIOT_DOC_CHK WHERE DOC_TYPE = '1' and DOC_CHK='Y') B ON A.INS_KEYNO = B.INS_KEYNO ")
			.append(" LEFT JOIN (select distinct INS_KEYNO, listagg(decode(DOC_SEQ,99,'■'||DOC_NAME||DOC_NAME_OTH,'■'||DOC_NAME), chr(10)) within group (order by DOC_SEQ)")
			.append(" over (partition by INS_KEYNO )DOC_NAME_2 from TBIOT_DOC_CHK WHERE DOC_TYPE = '2' And DOC_CHK='Y') C ON A.INS_KEYNO = C.INS_KEYNO ")
			.append(" LEFT JOIN TBPRD_INS_MAIN P on P.INSPRD_KEYNO = A.INSPRD_KEYNO ")
			.append(" LEFT JOIN TBJSB_INS_PROD_COMPANY E ON E.SERIALNUM = A.COMPANY_NUM ")
			.append(" LEFT JOIN TBIOT_BATCH_INFO D ON D.BATCH_INFO_KEYNO = A.BATCH_INFO_KEYNO ")
			.append(" LEFT JOIN TBJSB_INS_PROD_MAIN JPRD ON JPRD.PRODUCTSERIALNUM = A.INSPRD_KEYNO ")
			.append(" LEFT JOIN TBSYSPARAMETER PARAM ON PARAM.PARAM_TYPE = 'IOT.OTH_TYPE_BARCODE' AND PARAM.PARAM_CODE = A.OTH_TYPE ")
			.append(" LEFT JOIN TBIOT_PREMATCH PRE on PRE.PREMATCH_SEQ = A.PREMATCH_SEQ ")
			.append(" LEFT JOIN TBIOT_DOC_CHK DCHK on DCHK.INS_KEYNO = A.INS_KEYNO AND DCHK.DOC_TYPE = '2' AND DCHK.DOC_SEQ = 30 ")
			.append(" WHERE 1 = 1")
			.append(" AND decode(A.reg_type, 1, 1, A.reg_type-1) = :regType ")
			.append(" AND D.OP_BATCH_NO = :opBatchNo ")
			.append(" ORDER BY A.INS_ID ");

		qc.setQueryString(sb.toString());
		qc.setObject("regType", regType);
		qc.setObject("opBatchNo", opBatchNo);

		@SuppressWarnings("unchecked")
		List<Map<String, Object>> list = dam_obj.exeQuery(qc);
		//被保人姓名遮蓋處理
		for(Map<String,Object> map:list){
			if(map.get("INSURED_NAME") != null){
				if(!"".equals(map.get("INSURED_NAME").toString())){
					if(map.get("INSURED_NAME").toString().length()>1){
						map.put("INSURED_NAME", map.get("INSURED_NAME").toString().substring(0, 1)+"Ｏ"+map.get("INSURED_NAME").toString().substring(2));
					}
				}
			}
		}

		ReportDataIF data = new ReportData();
		String reportID_1 = null;
		String reportID_2 = null;
		List<String> urltemp = new ArrayList<String>();

		//取建機日
		String year = "";
		String mon = "";
		String day = "";
		if(list.get(0).get("KEYIN_DATE") != null){
			int keyin_date_year = (Integer.parseInt(list.get(0).get("KEYIN_DATE").toString().substring(0, 4)))-1911;//西元年換算民國年
			year = String.valueOf(keyin_date_year);

			mon = list.get(0).get("KEYIN_DATE").toString().substring(5, 7);
			day = list.get(0).get("KEYIN_DATE").toString().substring(8, 10);
		}else{
			Calendar cal = Calendar.getInstance();
		    year = cal.get(Calendar.YEAR) - 1911 + "";
		    mon = cal.get(Calendar.MONTH) + 1 + "";
		    if(mon.length()<2){
		    	mon = "0"+mon;
		    }
		    day = cal.get(Calendar.DAY_OF_MONTH) + "";
		    if(day.length()<2){
		    	day = "0"+day;
		    }
		    data.addParameter("twDate", year + mon + day);
		}


	    data.addParameter("twDate", year + mon + day);

		if ("1".equals(regType)) {
			// 新契約
			reportID_1 = "R1";
			reportID_2 = "R2";

		} else if("2".equals(regType)){
			// 其他
			reportID_1 = "R3";
			reportID_2 = "R4";

		} else if("3".equals(regType)){
			//匯利
			reportID_1 = "R5";
	
			List<Map<String, Object>> temp = new ArrayList<Map<String,Object>>();
			temp.addAll(list);
			for(int i = 0; i < temp.size(); i++){
				List<Map<String, Object>> to_do = new ArrayList<Map<String,Object>>();
				for(int j = 0; j < list.size(); j++){
					if(temp.get(i).get("PROD_PERIOD").toString().equals(list.get(j).get("PROD_PERIOD").toString())){
						to_do.add(list.get(j));
					}
				}
				if(to_do.size() > 0){
					String branchID = (String) getCommonVariable(SystemVariableConsts.LOGINBRH);
					data.addRecordList("INSPRD", to_do);
					data.addParameter("opBatchNo", opBatchNo);
					data.addParameter("cnt", to_do.size());
					if("000".equals(branchID)){
						data.addParameter("branch", (String) getCommonVariable(SystemVariableConsts.LOGINBRH)+"總行");
					}else{
						qc = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
						sb = new StringBuffer();
						sb.append(" select DEPT_NAME from TBORG_DEFN where DEPT_ID = :branch_num ");
						qc.setObject("branch_num", (String) getCommonVariable(SystemVariableConsts.LOGINBRH));
						qc.setQueryString(sb.toString());
						List<Map<String, Object>> BRANCH_NAMEList = dam_obj.exeQuery(qc);
	
						data.addParameter("branch", (String) getCommonVariable(SystemVariableConsts.LOGINBRH)+BRANCH_NAMEList.get(0).get("DEPT_NAME").toString());
					}
	
			    	// 明細表
					report = gen.generateReport(txnCode, reportID_1, data);
					String url_1 = report.getLocation();
	
	
					urltemp.add(url_1);
	
					//刪除重複
					for(Map<String, Object> k:to_do){
						list.remove(k);
					}
				}
			}
		}

	    if(!"3".equals(regType)){
	    	String branchID = (String) getCommonVariable(SystemVariableConsts.LOGINBRH);
			urltemp = new ArrayList<String>();

	    	data.addRecordList("INSPRD", list);
			data.addParameter("opBatchNo", opBatchNo);
			data.addParameter("INS_COM_NAME", ObjectUtils.toString(list.get(0).get("INS_COM_NAME")));
			data.addParameter("INS_SOURCE", ObjectUtils.toString(list.get(0).get("INS_SOURCE")));
			data.addParameter("SENIOR_AUTH_REMARKS", ObjectUtils.toString(list.get(0).get("SENIOR_AUTH_REMARKS")));
			data.addParameter("SENIOR_AUTH_ID", ObjectUtils.toString(list.get(0).get("SENIOR_AUTH_ID")));
			data.addParameter("cnt", list.size());

			if("000".equals(branchID)){
				data.addParameter("branch",
						(String) getCommonVariable(SystemVariableConsts.LOGINBRH)+"總行");
			}else{
				qc = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sb = new StringBuffer();
				sb.append(" select DEPT_NAME from TBORG_DEFN where DEPT_ID = :branch_num ");
				qc.setObject("branch_num", (String) getCommonVariable(SystemVariableConsts.LOGINBRH));
				qc.setQueryString(sb.toString());
				List<Map<String, Object>> BRANCH_NAMEList = dam_obj.exeQuery(qc);

				data.addParameter("branch", (String) getCommonVariable(SystemVariableConsts.LOGINBRH)+BRANCH_NAMEList.get(0).get("DEPT_NAME").toString());
			}

			// 明細表
			report = gen.generateReport(txnCode, reportID_1, data);
			String url_1 = report.getLocation();
			urltemp.add(url_1);

			if(!"2".equals(regType)){
				for(Map<String, Object> map:list){
					List<Map<String, Object>> tempList = new ArrayList<Map<String,Object>>();
					tempList.add(map);
					data.addRecordList("INSPRD", tempList);
					// 封面
					report = gen.generateReport(txnCode, reportID_2, data);
					String url_2 = report.getLocation();
					urltemp.add(url_2);
				}
			}else{
				//補印送件明細不需要印分案頁
				if(!reprint){
					for(Map<String, Object> map:list){
						List<Map<String, Object>> tempList = new ArrayList<Map<String,Object>>();
						tempList.add(map);
						data.addRecordList("INSPRD", tempList);
						// 封面
						report = gen.generateReport(txnCode, reportID_2, data);
						String url_2 = report.getLocation();
						urltemp.add(url_2);
					}
				}
			}

	    }

		return urltemp;
//		notifyClientToDownloadFile(url+url, opBatchNo+"送件明細表.pdf");
//		notifyClientViewDoc(url, "pdf");
	}

	/** 產生送件批號 **/
	private Map<String, String> getOpBatchInfo(String regType) throws JBranchException {
		/** 分行送件批號：字母 + 西元年 yymmdd + 4碼流水號 **/
		String opBatchNo = regType + new SimpleDateFormat("yyMMdd").format(new Date()) + getSerialNumber();
		Map<String, String> result = new HashMap<String, String>();
		result.put("OP_BATCH_NO", opBatchNo);

		try {
			String batchInfoKeyNo = getTBIOT_MAIN_SEQ();
			result.put("BATCH_INFO_KEYNO", batchInfoKeyNo);
			insertBatchInfo(opBatchNo, batchInfoKeyNo);
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
		return result;
	}

	/** 取得送件批號後四碼流水號 **/
	private String getSerialNumber() throws JBranchException {
		List<Map> result = exeQueryForQcf(genDefaultQueryConditionIF().setQueryString("SELECT LPAD(TBIOT_OP_BATCH_SEQ.nextval, 4, '0') AS SEQ FROM DUAL "));
		return ObjectUtils.toString(result.get(0).get("SEQ"));
	}

	/**
	 * 產生送件打包點收資料
	 * @param opBatchNo 分行送件批號
	 * @param batchInfoKeyNo 送件打包主鍵
	 * @throws JBranchException
	 */
	private void insertBatchInfo(String opBatchNo, String batchInfoKeyNo) throws JBranchException {
		String loginID = (String) getCommonVariable(SystemVariableConsts.LOGINID);		
		exeUpdateForQcf(genDefaultQueryConditionIF().setQueryString(new StringBuilder()
				.append("INSERT INTO TBIOT_BATCH_INFO(BATCH_INFO_KEYNO, OP_BATCH_NO, OP_BATCH_DATE,OP_BATCH_OPRID,CREATETIME,CREATOR) ")
				.append("VALUES( :batchInfoKeyNo, :opBatchNo, sysdate, :loginID, sysdate, :creator) ")
				.toString())
				.setObject("batchInfoKeyNo", batchInfoKeyNo)
				.setObject("opBatchNo", opBatchNo)
				.setObject("loginID", loginID)
				.setObject("creator", loginID));
	}

	/** 取得 TBIOT_MAIN_SEQ.NEXTVAL **/
	private String getTBIOT_MAIN_SEQ() throws JBranchException {
		List<Map> result = exeQueryForQcf(genDefaultQueryConditionIF().setQueryString("select TBIOT_MAIN_SEQ.NEXTVAL from dual "));
		return result.get(0).get("NEXTVAL").toString();
	}

	//補印送件明細
	public void rePrint(Object body, IPrimitiveMap<Object> header) throws JBranchException {
		IOT140InputVO inputVO = (IOT140InputVO) body;
		IOT140OutputVO outputVO = new IOT140OutputVO();

		List<String> urlList = new ArrayList<String>();
		List<String> checkOP_BATCH_NOList = new ArrayList<String>();
		boolean submit_OK = false;
		for (Map<String, Object> IOT_MAIN : inputVO.getIOT_MAINList()) {
			if("Y".equals(IOT_MAIN.get("choice")) && IOT_MAIN.get("OP_BATCH_NO") != null){
				//分行送件批號
				checkOP_BATCH_NOList.add(IOT_MAIN.get("OP_BATCH_NO").toString());
			}
		}
		//確認分行送件批號是否有重複
		HashSet h = new HashSet<>(checkOP_BATCH_NOList);
		checkOP_BATCH_NOList.clear();
		checkOP_BATCH_NOList.addAll(h);
		for(String OP_BATCH_NO:checkOP_BATCH_NOList){
			for(String tmndurl:print(inputVO.getREG_TYPE(), OP_BATCH_NO, true)){
				urlList.add(tmndurl);
			}
		}

		if(urlList.size()>0){
			submit_OK = true;
			String url = null;
			List<String> megerurl = new ArrayList<String>();
			for(int i=0;i<urlList.size();i++){
				megerurl.add(urlList.get(i).toString());
			}
			String encryptUrl = PdfUtil.process(new PdfConfigVO(this.getDataAccessManager(), true, megerurl));

			Date now = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			String nowtime = sdf.format(now);
			notifyClientToDownloadFile(encryptUrl, nowtime+"送件明細表.pdf");
		}
		outputVO.setSubmit_check(submit_OK);
		sendRtnObject(outputVO);
	}

	/** 其他文件登錄文件種類為數字時返回 true **/
	private boolean otherTypeIsNumber(Object otherType) {
		if(StringUtils.isEmpty((String) otherType)){
			return false;
		}else{
			return otherType.toString().matches("\\d");
		}
	}

	/** 將狀態更新成打包送件 **/
	private void updateStatus30(Object insKeyNo, Object batchInfoKeyNo) throws JBranchException {
		exeUpdateForQcf(genDefaultQueryConditionIF().setQueryString(new StringBuilder()
				.append("update TBIOT_MAIN set BATCH_INFO_KEYNO = :batchKeyNo, STATUS = 30 ")
				.append("where INS_KEYNO = :insKeyNo ")
				.toString())
				.setObject("batchKeyNo", batchInfoKeyNo)
				.setObject("insKeyNo", insKeyNo));
	}

	/**
	 * 產生報表
	 * @param source 打包來源資料
	 * @param urlList 報表的 Url 容器
	 * @param inputRegType inputVO 的 RegType
	 * @param dataRegTypeIsE 是否為要保書
	 * @throws JBranchException
	 */
	private void genReport(List<Map<String, Object>> source, List<String> urlList, String inputRegType, String dataRegType) throws JBranchException {
		if (CollectionUtils.isEmpty(source)) return;
		String opBatchNo = "";
		String batchInfoKeyNo = "";
		for (Map<String, Object> each : source) {
			/** 同個打包來源只要使用一個批號即可 **/
			if (StringUtils.isBlank(opBatchNo)) {
				Map<String, String> infoMap = getOpBatchInfo(getSign(StringUtils.isNotBlank(dataRegType) ? dataRegType : (String) each.get("REG_TYPE"), (String) each.get("OTH_TYPE")));
				opBatchNo = infoMap.get("OP_BATCH_NO");
				batchInfoKeyNo = infoMap.get("BATCH_INFO_KEYNO");
			}
			try {
				updateStatus30(each.get("INS_KEYNO"), batchInfoKeyNo);
			} catch (Exception e) {
				logger.error(String.format("發生錯誤:%s",StringUtil.getStackTraceAsString(e)));
				throw new APException("系統發生錯誤請洽系統管理員");
			}
		}
		urlList.addAll(print(inputRegType, opBatchNo,false));
	}

	/** 依照登錄類型（REG_TYPE）給定特定字母，此字母用在流水號第一位 **/
	private String getSign(String regType, String othType) {
		switch (regType) {
			case "U":
				return "U";	//文件種類為契變且為投資型保單
			case "H":
				return "H";	//房貸壽險商品
			case "E":
				return "E";	//電子要保書
			case "S":
				return "S";	//電子要保書視訊投保
			case "N":
				return "N";	//電子要保書，線上簽署行動投保服務同意書案件
			case "O":
				return "O";	//非富壽
			case "1":
			case "2":
				return "P";	//新契約
			case "3":
			case "4":
				if (othType.matches("\\d")) {
					return "C";	//其他文件，數字使用 C
				} else {
					return "B";	//其他文件，非數字使用 B
				}
		}
		return "";
	}

	/**
	 * 打包
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void submit(Object body, IPrimitiveMap<Object> header) throws JBranchException {
		IOT140InputVO inputVO = (IOT140InputVO) body;
		IOT140OutputVO outputVO = new IOT140OutputVO();
		final List<Map<String, Object>> terminatedC = new ArrayList<Map<String, Object>>();
		final List<Map<String, Object>> terminatedCInv = new ArrayList<Map<String, Object>>();
		final List<Map<String, Object>> terminatedB = new ArrayList<Map<String, Object>>();
		final List<Map<String, Object>> terminatedBInv = new ArrayList<Map<String, Object>>();

		final List<Map<String, Object>> signC = new ArrayList<Map<String, Object>>();
		final List<Map<String, Object>> signCInv = new ArrayList<Map<String, Object>>();
		final List<Map<String, Object>> signB = new ArrayList<Map<String, Object>>();
		final List<Map<String, Object>> signBInv = new ArrayList<Map<String, Object>>();

		final List<Map<String, Object>> online = new ArrayList<Map<String, Object>>();
		final List<Map<String, Object>> onlineInv = new ArrayList<Map<String, Object>>();
		final List<Map<String, Object>> digitalSign = new ArrayList<Map<String, Object>>();
		final List<Map<String, Object>> digitalSignInv = new ArrayList<Map<String, Object>>();
		final List<Map<String, Object>> mappVideo = new ArrayList<Map<String, Object>>();
		final List<Map<String, Object>> mappVideoInv = new ArrayList<Map<String, Object>>();
		final List<Map<String, Object>> other = new ArrayList<Map<String, Object>>();
		final List<Map<String, Object>> otherInv = new ArrayList<Map<String, Object>>();

		final List<Map<String, Object>> loanPrd = new ArrayList<Map<String, Object>>();
		final List<Map<String, Object>> loanPrdInv = new ArrayList<Map<String, Object>>();
		final List<Map<String, Object>> qcImmB = new ArrayList<Map<String, Object>>();
		final List<Map<String, Object>> qcImmBInv = new ArrayList<Map<String, Object>>();
		final List<Map<String, Object>> qcImmC = new ArrayList<Map<String, Object>>();
		final List<Map<String, Object>> qcImmCInv = new ArrayList<Map<String, Object>>();
		final List<List<Map<String, Object>>> notFBList = new ArrayList<List<Map<String, Object>>>();
		List<Map<String, Object>> notFB = new ArrayList<Map<String, Object>>();
		String companyNumString = "";
		final List<Map<String, Object>> reviseInv = new ArrayList<Map<String, Object>>();
		
		boolean submitOK = false;

		//先將IOT_MAINList以保險公司代碼排序
		Collections.sort(inputVO.getIOT_MAINList(), new Comparator<Map<String, Object>>() {
			public int compare(Map<String, Object> o1, Map<String, Object> o2) {
				return (int) ((Double)o1.get("COMPANY_NUM") - (Double)o2.get("COMPANY_NUM"));
			}
		});
		
		for (Map<String, Object> IOT_MAIN : inputVO.getIOT_MAINList()) {
			if("Y".equals(IOT_MAIN.get("choice"))){
				submitOK = true;
				if (!"30".equals(IOT_MAIN.get("STATUS"))) {
					
					if(StringUtils.equals("3", ObjectUtils.toString(IOT_MAIN.get("REG_TYPE"))) &&
							StringUtils.equals("2", ObjectUtils.toString(IOT_MAIN.get("OTH_TYPE"))) &&
							StringUtils.equals("U", ObjectUtils.toString(IOT_MAIN.get("ITEM_REMRK")))) {
						//文件種類為2.契變，且為投資型保單(ITEM_REMRK=U)，則批號開頭為U並打包為同一包，順序在C/B/O之前
						//其他則依現行邏輯打包
						reviseInv.add(IOT_MAIN);
					} else {
						if (StringUtils.equals("N", ObjectUtils.toString(IOT_MAIN.get("FB_COM_YN")))) {
							/** 非富壽案件 **/
							//非富壽案件，各保險公司分別打包
							if(!StringUtils.equals(companyNumString, ObjectUtils.toString(IOT_MAIN.get("COMPANY_NUM")))) {
								//與之前保險公司不同							
								if(StringUtils.isNotBlank(companyNumString)) {
									notFBList.add(notFB);
									notFB = new ArrayList<Map<String, Object>>();
								}
								companyNumString = ObjectUtils.toString(IOT_MAIN.get("COMPANY_NUM"));
							}
							notFB.add(IOT_MAIN);
						} else if(StringUtils.equals("Y", ObjectUtils.toString(IOT_MAIN.get("LOAN_PRD_YN")))) {
							/** 房貸壽險商品 **/
							if(!"1".equals(ObjectUtils.toString(IOT_MAIN.get("PRODUCT_TYPE")))) {
								loanPrdInv.add(IOT_MAIN);	//投資型商品另外打包
							} else {
								loanPrd.add(IOT_MAIN);
							}
						} else if (StringUtils.equals("Y", ObjectUtils.toString(IOT_MAIN.get("DIGITAL_AGREESIGN_YN")))) {
							/** 電子要保書，線上簽署行動投保服務同意書案件 **/
							if(!"1".equals(ObjectUtils.toString(IOT_MAIN.get("PRODUCT_TYPE")))) {
								digitalSignInv.add(IOT_MAIN);	//投資型商品另外打包
							} else {
								digitalSign.add(IOT_MAIN);
							}
						} else if (StringUtils.equals("Y", ObjectUtils.toString(IOT_MAIN.get("MAPPVIDEO_YN")))) {
							/** 行動要保書視訊投保 **/
							if(!"1".equals(ObjectUtils.toString(IOT_MAIN.get("PRODUCT_TYPE")))) {
								mappVideoInv.add(IOT_MAIN);	//投資型商品另外打包
							} else {
								mappVideo.add(IOT_MAIN);
							}
						} else if (!isNumeric(IOT_MAIN.get("INS_ID").toString().substring(5, 6))) {
							/** 保險文件編號第 6 碼不為數字，為行動要保書 **/
							if(!"1".equals(ObjectUtils.toString(IOT_MAIN.get("PRODUCT_TYPE")))) {
								onlineInv.add(IOT_MAIN);	//投資型商品另外打包
							} else {
								online.add(IOT_MAIN);
							}
						} else if ("Y".equals(IOT_MAIN.get("TERMINATED_INC"))) {
							/** 是否含解約，並依類型分為 C、B 大項 **/
							if (otherTypeIsNumber(IOT_MAIN.get("OTH_TYPE"))) {
								if(!"1".equals(ObjectUtils.toString(IOT_MAIN.get("PRODUCT_TYPE")))) {
									terminatedCInv.add(IOT_MAIN);	//投資型商品另外打包
								} else {
									terminatedC.add(IOT_MAIN);
								}
							} else {
								if(!"1".equals(ObjectUtils.toString(IOT_MAIN.get("PRODUCT_TYPE")))) {
									terminatedBInv.add(IOT_MAIN);	//投資型商品另外打包
								} else {
									terminatedB.add(IOT_MAIN);
								}
							}
						} else if ("Y".equals(IOT_MAIN.get("QC_IMMI"))) {
							/** 檢查是否有檢附立即投入批註書 **/
							if (otherTypeIsNumber(IOT_MAIN.get("OTH_TYPE"))) {
								if(!"1".equals(ObjectUtils.toString(IOT_MAIN.get("PRODUCT_TYPE")))) {
									qcImmCInv.add(IOT_MAIN);	//投資型商品另外打包
								} else {
									qcImmC.add(IOT_MAIN);
								}
							} else {
								if(!"1".equals(ObjectUtils.toString(IOT_MAIN.get("PRODUCT_TYPE")))) {
									qcImmBInv.add(IOT_MAIN);	//投資型商品另外打包
								} else {
									qcImmB.add(IOT_MAIN);
								}
							}
						} else if ("Y".equals(IOT_MAIN.get("SIGN_INC"))) {
							/** 是否需要簽署，並依類型分為 C、B 大項 **/
							if (otherTypeIsNumber(IOT_MAIN.get("OTH_TYPE"))) {
								if(!"1".equals(ObjectUtils.toString(IOT_MAIN.get("PRODUCT_TYPE")))) {
									signCInv.add(IOT_MAIN);	//投資型商品另外打包
								} else {
									signC.add(IOT_MAIN);
								}
							} else {
								if(!"1".equals(ObjectUtils.toString(IOT_MAIN.get("PRODUCT_TYPE")))) {
									signBInv.add(IOT_MAIN);	//投資型商品另外打包
								} else {
									signB.add(IOT_MAIN);
								}
							}
						} else  {
							if(!"1".equals(ObjectUtils.toString(IOT_MAIN.get("PRODUCT_TYPE")))) {
								otherInv.add(IOT_MAIN);	//投資型商品另外打包
							} else {
								other.add(IOT_MAIN);
							}
						}
					}
				}
			}
		}
		//最後一筆非富壽的資料要加上去
		if(CollectionUtils.isNotEmpty(notFB)) {
			notFBList.add(notFB);
		}

		List<String> urlList = new ArrayList<String>();
		/** 報表無順序要求，因為 C 類是比較急迫的報表，所以先讓 C 類報表印出來 **/
		//文件種類為2.契變，且為投資型保單(ITEM_REMRK=U)，則批號開頭為U並打包為同一包，順序在C/B/O之前
		genReport(reviseInv, urlList, inputVO.getREG_TYPE(), "U");
		genReport(terminatedC, urlList, inputVO.getREG_TYPE(), "");
		genReport(signC, urlList, inputVO.getREG_TYPE(), "");
		genReport(qcImmC, urlList, inputVO.getREG_TYPE(), "");
		genReport(terminatedCInv, urlList, inputVO.getREG_TYPE(), "");
		genReport(signCInv, urlList, inputVO.getREG_TYPE(), "");
		genReport(qcImmCInv, urlList, inputVO.getREG_TYPE(), "");
		genReport(terminatedB, urlList, inputVO.getREG_TYPE(), "");
		genReport(signB, urlList, inputVO.getREG_TYPE(), "");
		genReport(qcImmB, urlList, inputVO.getREG_TYPE(), "");
		genReport(terminatedBInv, urlList, inputVO.getREG_TYPE(), "");
		genReport(signBInv, urlList, inputVO.getREG_TYPE(), "");
		genReport(qcImmBInv, urlList, inputVO.getREG_TYPE(), "");
		genReport(online, urlList, inputVO.getREG_TYPE(), "E");
		genReport(other, urlList, inputVO.getREG_TYPE(), "");
		genReport(loanPrd, urlList, inputVO.getREG_TYPE(), "H");
		genReport(onlineInv, urlList, inputVO.getREG_TYPE(), "E");
		genReport(otherInv, urlList, inputVO.getREG_TYPE(), "");
		genReport(loanPrdInv, urlList, inputVO.getREG_TYPE(), "H");
		genReport(mappVideo, urlList, inputVO.getREG_TYPE(), "S");
		genReport(mappVideoInv, urlList, inputVO.getREG_TYPE(), "S");
		genReport(digitalSign, urlList, inputVO.getREG_TYPE(), "N");
		genReport(digitalSignInv, urlList, inputVO.getREG_TYPE(), "N");
		//非富壽案件，各保險公司分別打包
		for (List<Map<String, Object>> notFBItem : notFBList) {
			genReport(notFBItem, urlList, inputVO.getREG_TYPE(), "O");
		}
		
		if (CollectionUtils.isNotEmpty(urlList)) {
			String encryptUrl = PdfUtil.process(new PdfConfigVO(this.getDataAccessManager(), true, urlList));
			notifyClientToDownloadFile(encryptUrl,
					new SimpleDateFormat("yyyyMMdd").format(new Date()) + "送件明細表.pdf");
		}

		outputVO.setSubmit_check(submitOK);
		sendRtnObject(outputVO);
	}

	public void unPack(Object body, IPrimitiveMap<Object> header)
			throws JBranchException {
		IOT140InputVO inputVO = (IOT140InputVO) body;
		IOT140OutputVO outputVO = new IOT140OutputVO();
		IOT920 iot920 = (IOT920) PlatformContext.getBean("iot920");
		List<Map<String, Object>> NotunpackList = new ArrayList<Map<String, Object>>();
		String loginRoleID = (String) getCommonVariable(SystemVariableConsts.LOGINROLE);
		// check unpack
		for (Map<String, Object> IOT_MAIN : inputVO.getIOT_MAINList()) {
			if ("Y".equals(IOT_MAIN.get("choice"))) {
				if ("N".equals(iot920.unPackchk(IOT_MAIN.get("STATUS").toString(),
						loginRoleID))) {
					NotunpackList.add(IOT_MAIN);
					outputVO.setINS_KEYNO(IOT_MAIN.get("INS_KEYNO").toString());
					sendRtnObject(outputVO);
					break;
				}

			}
		}
		if (NotunpackList.size() <= 0) {
			// do unpack
			for (Map<String, Object> IOT_MAIN : inputVO.getIOT_MAINList()) {
				if ("Y".equals(IOT_MAIN.get("choice"))) {
					dam_obj = getDataAccessManager();
					TBIOT_MAINVO tmvo = new TBIOT_MAINVO();
					BigDecimal ins_keyno_change = new BigDecimal(IOT_MAIN.get(
							"INS_KEYNO").toString());
					tmvo = (TBIOT_MAINVO) dam_obj.findByPKey(
							TBIOT_MAINVO.TABLE_UID, ins_keyno_change);
					BigDecimal status_change = new BigDecimal("20");
					tmvo.setSTATUS(status_change);
					tmvo.setBATCH_INFO_KEYNO(null);
					dam_obj.update(tmvo);
				}
			}
			sendRtnObject(null);
		}
	}

	public void delData(Object body, IPrimitiveMap<Object> header)
			throws JBranchException {
		IOT140InputVO inputVO = (IOT140InputVO) body;
		String loginID = (String) getCommonVariable(SystemVariableConsts.LOGINID);
		String loginRoleID = (String)getCommonVariable(SystemVariableConsts.LOGINROLE);
		dam_obj = getDataAccessManager();
		QueryConditionIF qc = dam_obj
				.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuilder sb = new StringBuilder();
		List<Map<String, Object>> chkList = new ArrayList<Map<String, Object>>();
		String chk = "";
		try {
			sb.append(" select  FN_CHK_IOTSTATUSFLOW(:status,'99',:roleID) as CHK from dual ");
			qc.setObject("status", inputVO.getIOT_MAIN().get("STATUS")
					.toString());
			qc.setObject("roleID", loginRoleID);
			qc.setQueryString(sb.toString());
			chkList = dam_obj.exeQuery(qc);
			chk = chkList.get(0).get("CHK").toString();
			if ("N".equals(chk)) {
				sendRtnObject(false);
			} else if ("Y".equals(chk)) {
				dam_obj = getDataAccessManager();
				TBIOT_MAINVO tmvo = new TBIOT_MAINVO();
				BigDecimal ins_keyno_change = new BigDecimal(inputVO
						.getIOT_MAIN().get("INS_KEYNO").toString());
				tmvo = (TBIOT_MAINVO) dam_obj.findByPKey(
						TBIOT_MAINVO.TABLE_UID, ins_keyno_change);
				tmvo.setBATCH_INFO_KEYNO(null);
				BigDecimal status_change = new BigDecimal("99");
				tmvo.setSTATUS(status_change);
				tmvo.setDELETE_OPRID(loginID);
				Date now = new Date();
				tmvo.setDELETE_DATE(new Timestamp(now.getTime()));
				if("1".equals(inputVO.getIOT_MAIN().get("REG_TYPE"))){
					String INS_ID = inputVO.getIOT_MAIN().get("INS_ID").toString();
					String INS_ID_CHANGE = INS_ID.substring(0, 3)+"A"+INS_ID.substring(4, INS_ID.length());
					tmvo.setINS_ID(INS_ID_CHANGE);
				}
				dam_obj.update(tmvo);

				sendRtnObject(true);
			}
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",
					StringUtil.getStackTraceAsString(e)));
			throw new APException("同一保險文件編號不可重複刪除，請確認");
		}
	}

	private boolean isNumeric(String value) {
		try {
			Integer.parseInt(value);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

}
