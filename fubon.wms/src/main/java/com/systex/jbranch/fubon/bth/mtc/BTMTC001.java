package com.systex.jbranch.fubon.bth.mtc;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

import com.ibm.icu.text.SimpleDateFormat;
import com.systex.jbranch.fubon.bth.ftp.BthFtpJobUtil;
import com.systex.jbranch.fubon.commons.esb.EsbUtil;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.scheduler.AuditLogUtil;
import com.systex.jbranch.platform.common.scheduler.SchedulerHelper;
import com.systex.jbranch.platform.server.bizLogic.BizLogic;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 *	金錢信託套印_自益契約
 *	當日確認資料回傳主機
 *	
 **/

@Repository("btmtc001")
@Scope("prototype")
@SuppressWarnings("unchecked")
public class BTMTC001 extends BizLogic {
	private DataAccessManager dam = null;
	private BthFtpJobUtil bthUtil = new BthFtpJobUtil();
	private AuditLogUtil audit = null;
	String today = new SimpleDateFormat("yyyyMMdd").format(new Date());
	
	/**
	 * NMP2YAWK：委託人、受益人、監察人資料
	 * @param body
	 * @param header
	 * @throws Exception
	 */
	public void BTMTC001_NMP2YAWK(Object body, IPrimitiveMap<?> header) throws Exception {
		// 記錄排程監控的log
		audit = (AuditLogUtil) ((Map<?, ?>) body).get(SchedulerHelper.AUDIT_PARAMETER_KEY);
		String arg = ObjectUtils.toString(((Map)((Map)body).get(SchedulerHelper.JOB_PARAMETER_KEY)).get("exeDate"));	//傳入參數，執行日期(確認送出日期)
		Date exeDate = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		
		if(StringUtils.isNotBlank(arg)) {
			try {
				exeDate = sdf.parse(arg);
			} catch(Exception e) {}
		}
		
		if(exeDate != null) today = sdf.format(exeDate);
			
		//抓取SQL語法
		StringBuffer sb = new StringBuffer();				 
		dam = this.getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(exeDate == null ? DataAccessManager.QUERY_LANGUAGE_TYPE_SQL : DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		sb.append("SELECT A.*, A.BANK_ID || A.BRANCH_ID AS FIIN_CODE  FROM ( ");
		sb.append("SELECT M.CON_NO, D.CUST_ID, TO_MULTI_BYTE(D.CUST_NAME) AS CUST_NAME, TO_CHAR(D.BIRTH_DATE, 'YYYYMMDD') AS BIRTH_DATE, ");
		sb.append("D.CEN_ZIP_CODE, TO_MULTI_BYTE(D.CEN_ADDRESS) AS CEN_ADDRESS, D.COM_ZIP_CODE, TO_MULTI_BYTE(D.COM_ADDRESS) AS COM_ADDRESS, ");
		sb.append("D.TEL, D.MOBILE_NO, D.EMAIL, NVL(D.MINOR_YN, 'N') AS MINOR_YN, ");
		sb.append("D.LEG_AGENT_ID1, TO_MULTI_BYTE(D.LEG_AGENT_NAME1) AS LEG_AGENT_NAME1, D.LEG_AGENT_TEL1, D.LEG_AGENT_REL1, D.LEG_AGENT_OTR1, ");
		sb.append("D.LEG_AGENT_ID2, TO_MULTI_BYTE(D.LEG_AGENT_NAME2) AS LEG_AGENT_NAME2, D.LEG_AGENT_TEL2, D.LEG_AGENT_REL2, D.LEG_AGENT_OTR2, ");
		sb.append("D.SEAL_RETENTION_MTD, D.SEAL_UNDER7, TO_MULTI_BYTE(D.SEAL_UNDER7_NAME) AS SEAL_UNDER7_NAME, ");
		sb.append("D.SEAL_UNDER20_1, D.SEAL_UNDER20_2, TO_MULTI_BYTE(D.SEAL_UNDER20_NAME) AS SEAL_UNDER20_NAME, ");
		sb.append("CASE WHEN D.AGR_ACC_TYPE = 'A' THEN '012' ELSE D.OTR_BANK END AS BANK_ID, ");
		sb.append("CASE WHEN (D.AGR_ACC_TYPE = 'A' AND D.AGR_BRA_NBR IS NOT NULL) THEN ( ");
		sb.append("SELECT BRANCHID FROM TBSYS_BANK_BRANCH_SG WHERE BRANCHID <> '0000' AND BANKID = '012' AND BRANCHID LIKE D.AGR_BRA_NBR || '%' ");
		sb.append(") ELSE D.OTR_BRANCH END AS BRANCH_ID, ");
		sb.append("CASE WHEN D.AGR_ACC_TYPE = 'A' THEN D.AGR_ACC ELSE D.OTR_ACC END AS ACC_NBR, ");
		sb.append("M.CON_STATUS ");
		sb.append("FROM TBMTC_CONTRACT_MAIN M ");
		sb.append("LEFT JOIN TBMTC_CONTRACT_DETAIL D ON D.CON_NO = M.CON_NO ");
		sb.append("WHERE M.CON_STATUS = 'C' ");		
		
		//是否有傳入"傳送日期"參數
		if(exeDate != null) {
			sb.append(" AND TRUNC(M.LASTUPDATE) = TRUNC(:exeDate)) A ");
			qc.setObject("exeDate", exeDate);
		} else {
			sb.append(" AND TRUNC(M.LASTUPDATE) = TRUNC(SYSDATE)) A ");
		}
		
		//執行SQL
		qc.setQueryString(sb.toString());
		List<Map<String, Object>> dataList = dam.exeQuery(qc);
		
		//產出txt需要之參數
		String fileName = "NMP2YAWK" + "_" + today + ".txt";
		String path = SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH) + "reports\\";
		File file1 = new File(path + fileName);
		//若檔案已存在則刪除重新產生
		if(file1.exists()) file1.delete();
		file1.createNewFile();
		
		//把執行完SQL的結果產出txt
		int count = 0;
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file1, true), "Big5"));	//ANSI編碼
		try {
			writer.append("@" + today + System.lineSeparator());	//首筆放@+西原年月日8碼
			writer.flush();
			
			for(Map<String, Object> map : dataList) {
				writer.append(addBlankForString(map.get("CON_NO"), 15));						//序號
				writer.append(addBlankForString(map.get("CUST_ID"), 10));						//委託人證號
				writer.append(addFullBlankForString(map.get("CUST_NAME"), 20));					//委託人姓名
				writer.append(addZeroForNum(ObjectUtils.toString(map.get("BIRTH_DATE")), 8));	//生日
				writer.append(addBlankForString(map.get("CEN_ZIP_CODE"), 5));					//戶籍地址郵遞區號
				writer.append(addFullBlankForString(map.get("CEN_ADDRESS"), 33));				//戶籍地址
				writer.append(addBlankForString(map.get("COM_ZIP_CODE"), 5));					//通訊地址郵遞區號
				writer.append(addFullBlankForString(map.get("COM_ADDRESS"), 33));				//通訊地址
				writer.append(addBlankForString(map.get("TEL"), 20));							//電話
				writer.append(addBlankForString(map.get("MOBILE_NO"), 10));						//行動電話
				writer.append(addBlankForString(map.get("EMAIL"), 50));							//E-MAIL
				writer.append(addBlankForString(map.get("MINOR_YN"), 1));						//是否未成年
				writer.append(addBlankForString(map.get("LEG_AGENT_ID1"), 10));					//法定代理人１_證號
				writer.append(addFullBlankForString(map.get("LEG_AGENT_NAME1"), 10));			//法定代理人１_姓名
				writer.append(addBlankForString(map.get("LEG_AGENT_TEL1"), 20));				//法定代理人１_電話
				writer.append(addBlankForString(map.get("LEG_AGENT_REL1"), 1));					//法定代理人１_關係
//				writer.append(addFullBlankForString(map.get("LEG_AGENT_OTR1,"), 6));			//法定代理人１_其他
				writer.append(addBlankForString(map.get("LEG_AGENT_ID2"), 10));					//法定代理人２_證號
				writer.append(addFullBlankForString(map.get("LEG_AGENT_NAME2"), 10));			//法定代理人２_姓名
				writer.append(addBlankForString(map.get("LEG_AGENT_TEL2"), 20));				//法定代理人２_電話
				writer.append(addBlankForString(map.get("LEG_AGENT_REL2"), 1));					//法定代理人２_關係
//				writer.append(addFullBlankForString(map.get("LEG_AGENT_OTR2,"), 6));			//法定代理人２_其他
				writer.append(addBlankForString(map.get("SEAL_RETENTION_MTD"), 1));				//未成年人印鑑留存方式
				writer.append(addBlankForString(map.get("SEAL_UNDER7"), 1));					//未成年人印鑑留存方式_未滿7歲
				writer.append(addFullBlankForString(map.get("SEAL_UNDER7_NAME,"), 10));			//未成年人印鑑留存方式_未滿7歲僅併留單一法代印鑑
				writer.append(addBlankForString(map.get("SEAL_UNDER20_1"), 1));					//未成年人印鑑留存方式_滿7歲未滿20歲
				writer.append(addBlankForString(map.get("SEAL_UNDER20_2"), 1));					//未成年人印鑑留存方式_滿7歲未滿20歲併留法代印鑑
				writer.append(addFullBlankForString(map.get("SEAL_UNDER20_NAME,"), 10));		//未成年人印鑑留存方式_滿7歲未滿20歲僅併留單一法代印鑑
//				writer.append(addFullBlankForString(map.get("BANK_NAME,"), 30));				//給付帳號銀行名稱
//				writer.append(addFullBlankForString(map.get("BRANCH_NAME,"), 30));				//給付帳號銀行分行名稱
				writer.append(addBlankForString(map.get("FIIN_CODE"), 7));						//給付帳號銀行代碼(金資代碼)
				writer.append(addBlankForString(map.get("ACC_NBR"), 15));						//給付帳號
				
				writer.append(System.lineSeparator());
				writer.flush();
				count++;
			}
			writer.append("#" + addZeroForNum(String.valueOf(count), 10));	//尾筆放#+轉檔筆數10碼(右靠左補零)
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				writer.close();
			} catch(IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * BA01：信託契約資料
	 * @param body
	 * @param header
	 * @throws Exception
	 */
	public void BTMTC001_BA01(Object body, IPrimitiveMap<?> header) throws Exception {
		// 記錄排程監控的log
		audit = (AuditLogUtil) ((Map<?, ?>) body).get(SchedulerHelper.AUDIT_PARAMETER_KEY);
		String arg = ObjectUtils.toString(((Map)((Map)body).get(SchedulerHelper.JOB_PARAMETER_KEY)).get("exeDate"));	//傳入參數，執行日期(確認送出日期)
		Date exeDate = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		
		if(StringUtils.isNotBlank(arg)) {
			try {
				exeDate = sdf.parse(arg);
			} catch(Exception e) {}
		}

		if(exeDate != null) today = sdf.format(exeDate);
		
		//抓取SQL語法
		StringBuffer sb = new StringBuffer();				 
		dam = this.getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(exeDate == null ? DataAccessManager.QUERY_LANGUAGE_TYPE_SQL : DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		sb.append(" SELECT 	M.CON_NO, to_char(CREATETIME, 'YYYYMMDD') as CREATETIME, ");
		sb.append(" 		CASE CON_CURR WHEN 'T' THEN 'TWD' ELSE 'USD' END AS CON_CURR, ");
		sb.append(" 		'1' AS CON_TYPE, ");
		sb.append(" 		CASE WHEN LENGTH(CUST_ID) >= 10 THEN '' ELSE '1' END AS PERSON_TYPE, ");
		sb.append("         CURR1, AMT1, CURR2, AMT2, CURR3, AMT3, ");
		sb.append(" 		SIGNING_FEE, ");
		sb.append(" 		MODIFY_FEE, ");
		sb.append(" 		MNG_FEE_MIN, ");
		sb.append(" 		MNG_FEE_RATE1, ");
		sb.append(" 		MNG_FEE_RATE2, ");
		sb.append(" 		MNG_FEE_RATE3, ");
		sb.append("         END_YEARS, END_AMT_LIMIT, ");
		sb.append("         CASE TERM_CON WHEN 'A' THEN '1' WHEN 'B' THEN '2' ELSE '' END AS TERM_CON, ");
		sb.append("         CASE MODI_CON WHEN 'A' THEN '1' WHEN 'B' THEN '2' ELSE '' END AS MODI_CON, ");
		
		sb.append("         CASE WHEN (D.CON_NO IS NOT NULL AND T.CON_NO IS NULL) THEN ( ");
		sb.append("             CASE M.DISC_CON WHEN 'A' THEN '1' WHEN 'B' THEN '2' WHEN 'C' THEN '6' END ");
		sb.append("         ) ELSE '' END AS DISC_CON1, ");
		sb.append("         CASE WHEN T.CON_NO IS NOT NULL THEN ( ");
		sb.append("             CASE M.DISC_CON WHEN 'A' THEN '1' WHEN 'B' THEN '2' WHEN 'C' THEN ( ");
		sb.append("                 CASE M.APP_SUP WHEN 'C1' THEN '6' WHEN 'C2' THEN '3' END ");
		sb.append("             ) END ");
		sb.append("         ) ELSE '' END AS DISC_CON2, M.DISC_ID, ");
		
		sb.append("         NVL(D.DIS_TYPE, '3') AS DIS_TYPE ");
		sb.append(" FROM TBMTC_CONTRACT_MAIN M ");
		sb.append(" LEFT JOIN ( ");
		sb.append(" SELECT CON_NO, DIS_TYPE FROM TBMTC_CONTRACT_DETAIL WHERE REL_TYPE = '2' ");
		sb.append(" ) D ON M.CON_NO = D.CON_NO ");
		sb.append(" LEFT JOIN ( ");
		sb.append(" SELECT CON_NO, REL_TYPE FROM TBMTC_CONTRACT_DETAIL WHERE REL_TYPE = '3' ");
		sb.append(" ) T ON M.CON_NO = T.CON_NO ");
		sb.append(" WHERE CON_STATUS = 'C' ");
		
		//是否有傳入"傳送日期"參數
		if(exeDate != null) {
			sb.append(" AND TRUNC(LASTUPDATE) = TRUNC(:exeDate) ");
			qc.setObject("exeDate", exeDate);
		} else {
			sb.append(" AND TRUNC(LASTUPDATE) = TRUNC(SYSDATE) ");
		}
				
		//執行SQL
		qc.setQueryString(sb.toString());
		List<Map<String, Object>> dataList = dam.exeQuery(qc);
		
		//產出txt需要之參數
		String fileName = "NMP2YBWK" + "_" + today + ".txt";
		String path = SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH) + "reports\\";
		File file1 = new File(path + fileName);
		//若檔案已存在則刪除重新產生
		if(file1.exists()) file1.delete();
		file1.createNewFile();
		
		//把執行完SQL的結果產出txt
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file1, true), "Big5"));	//ANSI編碼
		int count = 0;
		try {
			writer.append("@" + today + System.lineSeparator());	//首筆放@+西原年月日8碼
			writer.flush();
			
			for(Map<String, Object> map : dataList){
				BigDecimal amt1 = map.get("AMT1") == null ? BigDecimal.ZERO : new BigDecimal(map.get("AMT1").toString());
				BigDecimal amt2 = map.get("AMT2") == null ? BigDecimal.ZERO : new BigDecimal(map.get("AMT2").toString());
				BigDecimal amt3 = map.get("AMT3") == null ? BigDecimal.ZERO : new BigDecimal(map.get("AMT3").toString());
				BigDecimal mng_fee_min = map.get("MNG_FEE_MIN") == null ? BigDecimal.ZERO : new BigDecimal(map.get("MNG_FEE_MIN").toString());
				BigDecimal mng_fee_rate1 = map.get("MNG_FEE_RATE1") == null ? BigDecimal.ZERO : new BigDecimal(map.get("MNG_FEE_RATE1").toString());
				BigDecimal mng_fee_rate2 = map.get("MNG_FEE_RATE2") == null ? BigDecimal.ZERO : new BigDecimal(map.get("MNG_FEE_RATE2").toString());
				BigDecimal mng_fee_rate3 = map.get("MNG_FEE_RATE3") == null ? BigDecimal.ZERO : new BigDecimal(map.get("MNG_FEE_RATE3").toString());
				
				writer.append(addBlankForString(map.get("CON_NO"), 15));							//序號
				writer.append(addZeroForNum(map.get("CREATETIME"), 8));								//建立日期
				writer.append(addBlankForString(map.get("CON_CURR"), 3));							//契約幣別
				writer.append(addBlankForString(map.get("CON_TYPE"), 1));							//信託種類
				writer.append(addBlankForString(map.get("PERSON_TYPE"), 1));						//信託型態
				writer.append(addBlankForString(map.get("CURR1"), 3));								//交付幣別１
				writer.append(addZeroForNum(new EsbUtil().decimalPadding(amt1, 12, 2), 14));		//交付幣別１交付金額
				writer.append(addBlankForString(map.get("CURR2"), 3));								//交付幣別２
				writer.append(addZeroForNum(new EsbUtil().decimalPadding(amt2, 12, 2), 14));		//交付幣別２交付金額
				writer.append(addBlankForString(map.get("CURR3"), 3));								//交付幣別３
				writer.append(addZeroForNum(new EsbUtil().decimalPadding(amt3, 12, 2), 14));		//交付幣別３交付金額
				writer.append(addZeroForNum(map.get("SIGNING_FEE"), 8));							//簽約費
				writer.append(addZeroForNum(map.get("MODIFY_FEE"), 8));								//修約費
				writer.append(addZeroForNum(new EsbUtil().decimalPadding(mng_fee_min, 8, 2), 10));	//每月最低管理費
				
				if(StringUtils.equals("TWD", map.get("CON_CURR").toString())) {	
					//台幣信託
					writer.append("000000000000000");	//信託報酬設定1Min
					writer.append("000000010000000");	//信託報酬設定1Max
					writer.append(addZeroForNum(new EsbUtil().decimalPadding(mng_fee_rate1, 4, 2), 6));	//信託報酬設定1費率%
					writer.append("000000010000000");	//信託報酬設定2Min
					writer.append("000000030000000");	//信託報酬設定2Max
					writer.append(addZeroForNum(new EsbUtil().decimalPadding(mng_fee_rate2, 4, 2), 6));	//信託報酬設定2費率%
					writer.append("000000030000000");	//信託報酬設定3Min
					writer.append("999999999999999");	//信託報酬設定3Max
					writer.append(addZeroForNum(new EsbUtil().decimalPadding(mng_fee_rate3, 4, 2), 6));	//信託報酬設定3費率%
				} else {	
					//外幣信託
					writer.append("000000000000000");	//信託報酬設定1Min
					writer.append("000000000300000");	//信託報酬設定1Max
					writer.append(addZeroForNum(new EsbUtil().decimalPadding(mng_fee_rate1, 4, 2), 6));	//信託報酬設定1費率%
					writer.append("000000000300000");	//信託報酬設定2Min
					writer.append("000000001000000");	//信託報酬設定2Max
					writer.append(addZeroForNum(new EsbUtil().decimalPadding(mng_fee_rate2, 4, 2), 6));	//信託報酬設定1費率%
					writer.append("000000001000000");	//信託報酬設定3Min
					writer.append("999999999999999");	//信託報酬設定3Max
					writer.append(addZeroForNum(new EsbUtil().decimalPadding(mng_fee_rate3, 4, 2), 6));	//信託報酬設定1費率%
				}
				writer.append("000000000000000");		//信託報酬設定4Min
				writer.append("000000000000000");		//信託報酬設定4Min
				writer.append("000000");					//信託報酬設定4費率%
				writer.append("000000000000000");		//信託報酬設定5Min
				writer.append("000000000000000");		//信託報酬設定5Min
				writer.append("000000");					//信託報酬設定5費率%
				
				BigDecimal end_amt_limit = map.get("END_AMT_LIMIT") == null ? BigDecimal.ZERO : new BigDecimal(map.get("END_AMT_LIMIT").toString());
				
				writer.append(addZeroForNum(map.get("END_YEARS"), 3));									//信託終止條件_成立滿N年
				writer.append(addZeroForNum(new EsbUtil().decimalPadding(end_amt_limit, 12, 2), 14));	//信託終止條件_財產低於N金額
				writer.append(addBlankForString(map.get("TERM_CON"), 1));								//提前終止條件
				writer.append(addBlankForString(map.get("MODI_CON"), 1));								//變更契約條件
				writer.append(addBlankForString(map.get("DISC_CON1"), 1));								//運用決定權條件設有1位監察人
				writer.append(addBlankForString(map.get("DISC_CON2"), 1));								//運用決定權條件設有2位監察人
				writer.append(addBlankForString(map.get("DISC_ID"), 10));								//運用決定權條件設有2位監察人如為3特定信託監察人證號
				writer.append(addBlankForString(map.get("DIS_TYPE"), 1));								//信託監察人解任
				writer.append(addZeroForNum(new EsbUtil().decimalPadding(BigDecimal.ZERO, 8, 0), 8));	//贈與稅申報代辦手續費
				
				writer.append(System.lineSeparator());
				writer.flush();
				count++;
			}
			writer.append("#" + addZeroForNum(String.valueOf(count), 10));	//尾筆放#+轉檔筆數10碼(右靠左補零)
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				writer.close();
			} catch(IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * BA02：委託人資料
	 * @param body
	 * @param header
	 * @throws Exception
	 */
	public void BTMTC001_BA02(Object body, IPrimitiveMap<?> header) throws Exception {
		// 記錄排程監控的log
		audit = (AuditLogUtil) ((Map<?, ?>) body).get(SchedulerHelper.AUDIT_PARAMETER_KEY);
		String arg = ObjectUtils.toString(((Map)((Map)body).get(SchedulerHelper.JOB_PARAMETER_KEY)).get("exeDate"));	//傳入參數，執行日期(確認送出日期)
		Date exeDate = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		
		if(StringUtils.isNotBlank(arg)) {
			try {
				exeDate = sdf.parse(arg);
			} catch(Exception e) {}
		}
		
		if(exeDate != null) today = sdf.format(exeDate);
		
		//抓取SQL語法
		StringBuffer sb = new StringBuffer();				 
		dam = this.getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(exeDate == null ? DataAccessManager.QUERY_LANGUAGE_TYPE_SQL : DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		sb.append(" SELECT 	CON_NO, CUST_ID ");
		sb.append(" FROM TBMTC_CONTRACT_MAIN ");
		sb.append(" WHERE CON_STATUS = 'C' ");
		
		//是否有傳入"傳送日期"參數
		if(exeDate != null) {
			sb.append(" AND TRUNC(LASTUPDATE) = TRUNC(:exeDate) ");
			qc.setObject("exeDate", exeDate);
		} else {
			sb.append(" AND TRUNC(LASTUPDATE) = TRUNC(SYSDATE) ");
		}
				
		//執行SQL
		qc.setQueryString(sb.toString());
		List<Map<String, Object>> dataList = dam.exeQuery(qc);
		
		//產出txt需要之參數
		String fileName = "NMP2YCWK" + "_" + today + ".txt";
		String path = SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH) + "reports\\";
		File file1 = new File(path + fileName);
		//若檔案已存在則刪除重新產生
		if(file1.exists()) file1.delete();
		file1.createNewFile();
				
		//把執行完SQL的結果產出txt
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file1, true), "Big5"));	//ANSI編碼
		int count = 0;
		try {
			writer.append("@" + today + System.lineSeparator());	//首筆放@+西原年月日8碼
			writer.flush();
			
			for(Map<String, Object> map : dataList){
				writer.append(addBlankForString(ObjectUtils.toString(map.get("CON_NO")), 15));	//序號
				writer.append(addBlankForString(ObjectUtils.toString(map.get("CUST_ID")), 10));	//委託人證號１
				writer.append(System.lineSeparator());
				writer.flush();
				count++;
			}
			writer.append("#" + addZeroForNum(String.valueOf(count), 10));	//尾筆放#+轉檔筆數10碼(右靠左補零)
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				writer.close();
			} catch(IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * BA03：受益人資料-固定給付
	 * @param body
	 * @param header
	 * @throws Exception
	 */
	public void BTMTC001_BA03(Object body, IPrimitiveMap<?> header) throws Exception {
		// 記錄排程監控的log
		audit = (AuditLogUtil) ((Map<?, ?>) body).get(SchedulerHelper.AUDIT_PARAMETER_KEY);
		String arg = ObjectUtils.toString(((Map)((Map)body).get(SchedulerHelper.JOB_PARAMETER_KEY)).get("exeDate"));	//傳入參數，執行日期(確認送出日期)
		Date exeDate = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		
		if(StringUtils.isNotBlank(arg)) {
			try {
				exeDate = sdf.parse(arg);
			} catch(Exception e) {}
		}
		
		if(exeDate != null) today = sdf.format(exeDate);
		
		//抓取SQL語法
		StringBuffer sb = new StringBuffer();				 
		dam = this.getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(exeDate == null ? DataAccessManager.QUERY_LANGUAGE_TYPE_SQL : DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		sb.append("SELECT CON_NO, CUST_ID, SIP_TYPE, ");
		sb.append("CASE WHEN (LENGTH(SIP_TYPE) = 1 AND SIP_TYPE = '1') THEN 'Y' ");
		sb.append("WHEN LENGTH(SIP_TYPE) > 1 THEN 'Y' ELSE '' END AS SIP_TYPE1, ");
		sb.append("BEN_AGE1, BEN_CURR1, BEN_AMT1, ");
		sb.append("BEN_AGE2, BEN_AGE3, BEN_CURR2, BEN_AMT2, ");
		sb.append("BEN_AGE4, BEN_CURR3, BEN_AMT3, ");
		sb.append("CASE WHEN (LENGTH(SIP_TYPE) = 1 AND SIP_TYPE = '2') THEN 'Y' ");
		sb.append("WHEN LENGTH(SIP_TYPE) > 1 THEN 'Y' ELSE '' END AS SIP_TYPE2, ");
		sb.append("AGR_MON_TYPE, M_CURR, M_AMT, A_MONTHS, A_CURR, A_AMT ");
		sb.append("FROM TBMTC_CONTRACT_MAIN ");
		sb.append("WHERE CON_STATUS = 'C' ");
		
		//是否有傳入"傳送日期"參數
		if(exeDate != null) {
			sb.append(" AND TRUNC(LASTUPDATE) = TRUNC(:exeDate) ");
			qc.setObject("exeDate", exeDate);
		} else {
			sb.append(" AND TRUNC(LASTUPDATE) = TRUNC(SYSDATE) ");
		}
				
		//執行SQL
		qc.setQueryString(sb.toString());
		List<Map<String, Object>> dataList = dam.exeQuery(qc);
		
		//產出txt需要之參數
		String fileName = "NMP2YDW1" + "_" + today + ".txt";
		String path = SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH) + "reports\\";
		File file1 = new File(path + fileName);
		//若檔案已存在則刪除重新產生
		if(file1.exists()) file1.delete();
		file1.createNewFile();
				
		//把執行完SQL的結果產出txt
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file1, true), "Big5"));	//ANSI編碼
		int count = 0;
		try {
			writer.append("@" + today + System.lineSeparator());	//首筆放@+西原年月日8碼
			writer.flush();
			
			for(Map<String, Object> map : dataList){
				//依受益人年紀進行給付，每月 10 日給付。或沒有依年紀也沒有依月份，寫一筆空的
				if(map.get("SIP_TYPE1") != null && StringUtils.equals("Y", map.get("SIP_TYPE1").toString())
						|| (map.get("SIP_TYPE1") == null && map.get("SIP_TYPE2") == null)) {
					this.setBA03_01(writer, map);
					count++;
				}
				
				//依約定月份 10 日進行給付
				if(map.get("SIP_TYPE2") != null && StringUtils.equals("Y", map.get("SIP_TYPE2").toString())) {
					String agr_mon_type = map.get("AGR_MON_TYPE") == null ? "" : map.get("AGR_MON_TYPE").toString();
					if (agr_mon_type.length() > 1) {
						//依約定月份給付類別 -- M：每月/A：每年某幾月 都有選
						for (int i = 0; i < 2; i++) {
							this.setBA03_02(writer, map, i == 0 ? "M" : "A");
							count++;
						}
					} else {
						this.setBA03_02(writer, map, agr_mon_type);
						count++;
					}
				}
			}
			writer.append("#" + addZeroForNum(String.valueOf(count), 10));	//尾筆放#+轉檔筆數10碼(右靠左補零)
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				writer.close();
			} catch(IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void setBA03_01(BufferedWriter writer, Map<String, Object> map) throws IOException {
		BigDecimal ben_amt1 = map.get("BEN_AMT1") == null ? BigDecimal.ZERO : new BigDecimal(map.get("BEN_AMT1").toString());
		BigDecimal ben_amt2 = map.get("BEN_AMT2") == null ? BigDecimal.ZERO : new BigDecimal(map.get("BEN_AMT2").toString());
		BigDecimal ben_amt3 = map.get("BEN_AMT3") == null ? BigDecimal.ZERO : new BigDecimal(map.get("BEN_AMT3").toString());
		
		writer.append(addBlankForString(map.get("CON_NO"), 15));						//序號
		writer.append(addBlankForString(map.get("CUST_ID"), 10));						//委託人證號１
		writer.append(addBlankForString(map.get("SIP_TYPE1"), 1));						//依受益人年紀進行給付，每月 10 日給付
		writer.append(addBlankForString(map.get("BEN_CURR1"), 3));						//依受益人年紀給付_滿幾歲(含)前_幣別
		writer.append(addZeroForNum(map.get("BEN_AGE1"), 3));							//依受益人年紀給付_滿幾歲(含)前
		writer.append(addZeroForNum(new EsbUtil().decimalPadding(ben_amt1, 12, 2), 14));//依受益人年紀給付_滿幾歲(含)前_金額
		writer.append(addBlankForString(map.get("BEN_CURR2"), 3));						//依受益人年紀給付_介於歲間_幣別
		writer.append(addZeroForNum(map.get("BEN_AGE2"), 3));							//依受益人年紀給付_超過幾歲
		writer.append(addZeroForNum(map.get("BEN_AGE3"), 3));							//依受益人年紀給付_未滿幾歲
		writer.append(addZeroForNum(new EsbUtil().decimalPadding(ben_amt2, 12, 2), 14));//依受益人年紀給付_介於歲間_金額
		writer.append(addBlankForString(map.get("BEN_CURR3"), 3));						//依受益人年紀給付_滿幾歲(含)後_幣別
		writer.append(addZeroForNum(map.get("BEN_AGE4"), 3));							//依受益人年紀給付_滿幾歲(含)後
		writer.append(addZeroForNum(new EsbUtil().decimalPadding(ben_amt3, 12, 2), 14));//依受益人年紀給付_滿幾歲(含)後_金額
		
		writer.append(addBlankForString("", 1));										//依約定月份 10 日進行給付
		
		writer.append(addBlankForString("", 1));										//依約定月份給付類別
		//
		writer.append(addBlankForString("", 3));										//依約定月份給付_每月_幣別
		writer.append(addZeroForNum(0, 14));											//依約定月份給付_每月_金額
		//
		writer.append(addBlankForString("", 3));										//依約定月份給付_每月幾月_幣別
		writer.append(addBlankForString("", 12));										//依約定月份給付_每年幾月
		writer.append(addZeroForNum(0, 14));											//依約定月份給付_每年幾月_金額
		
		writer.append(System.lineSeparator());
		writer.flush();
	}
	
	private void setBA03_02(BufferedWriter writer, Map<String, Object> map, String agr_mon_type) throws IOException {
		BigDecimal m_amt = map.get("M_AMT") == null ? BigDecimal.ZERO : new BigDecimal(map.get("M_AMT").toString());
		BigDecimal a_amt = map.get("A_AMT") == null ? BigDecimal.ZERO : new BigDecimal(map.get("A_AMT").toString());
		
		writer.append(addBlankForString(map.get("CON_NO"), 15));		//序號
		writer.append(addBlankForString(map.get("CUST_ID"), 10));		//委託人證號１
		writer.append(addBlankForString("", 1));						//依受益人年紀進行給付，每月 10 日給付
		writer.append(addBlankForString("", 3));						//依受益人年紀給付_滿幾歲(含)前_幣別
		writer.append(addZeroForNum(0, 3));								//依受益人年紀給付_滿幾歲(含)前
		writer.append(addZeroForNum(0, 14));							//依受益人年紀給付_滿幾歲(含)前_金額
		writer.append(addBlankForString(0, 3));							//依受益人年紀給付_介於歲間_幣別
		writer.append(addZeroForNum(0, 3));								//依受益人年紀給付_超過幾歲
		writer.append(addZeroForNum(0, 3));								//依受益人年紀給付_未滿幾歲
		writer.append(addZeroForNum(0, 14));							//依受益人年紀給付_介於歲間_金額
		writer.append(addBlankForString("", 3));						//依受益人年紀給付_滿幾歲(含)後_幣別
		writer.append(addZeroForNum(0, 3));								//依受益人年紀給付_滿幾歲(含)後
		writer.append(addZeroForNum(0, 14));							//依受益人年紀給付_滿幾歲(含)後_金額
		
		writer.append(addBlankForString(map.get("SIP_TYPE2"), 1));						//依約定月份 10 日進行給付
		
		writer.append(addBlankForString(agr_mon_type, 1));								//依約定月份給付類別
		
		String m_curr = "";
		String a_curr = "";
		String a_months = "";
		if ("M".equals(agr_mon_type)) {
			m_curr = map.get("M_CURR") == null ? "" : map.get("M_CURR").toString();
			a_amt = BigDecimal.ZERO;
			
		} else if ("A".equals(agr_mon_type)) {
			a_curr = map.get("A_CURR") == null ? "" : map.get("A_CURR").toString();
			m_amt = BigDecimal.ZERO;
			
			//YYYYYYYYYYYY(每一欄位代表1~12月) 例：1、4、7、10月 則為：Y  Y  Y  Y  
			String a_months_s = map.get("A_MONTHS") == null ? "" : map.get("A_MONTHS").toString();
			String[] a_months_arr = a_months_s.split(",");
			int months = 12;	//12個月
			String a_months_o = "";
			for (int i = 0; i < months; i++) {
				a_months_o += " ";
			}
			StringBuilder a_months_b = new StringBuilder(a_months_o);
			for (String month : a_months_arr) {
				int index = Integer.parseInt(month) - 1;  
				a_months_b.setCharAt(index, 'Y');
			}
			
			a_months = a_months_b.toString();
		}
		//
		writer.append(addBlankForString(m_curr, 3));									//依約定月份給付_每月_幣別
		writer.append(addZeroForNum(new EsbUtil().decimalPadding(m_amt, 12, 2), 14));	//依約定月份給付_每月_金額
		//
		writer.append(addBlankForString(a_curr, 3));									//依約定月份給付_每月幾月_幣別
		writer.append(addBlankForString(a_months, 12));									//依約定月份給付_每年幾月
		writer.append(addZeroForNum(new EsbUtil().decimalPadding(a_amt, 12, 2), 14));	//依約定月份給付_每年幾月_金額
		
		writer.append(System.lineSeparator());
		writer.flush();
	}
		
	/**
	 * BA03：受益人資料-特別給付
	 * @param body
	 * @param header
	 * @throws Exception
	 */
	public void BTMTC001_BA03_D(Object body, IPrimitiveMap<?> header) throws Exception {
		// 記錄排程監控的log
		audit = (AuditLogUtil) ((Map<?, ?>) body).get(SchedulerHelper.AUDIT_PARAMETER_KEY);
		String arg = ObjectUtils.toString(((Map)((Map)body).get(SchedulerHelper.JOB_PARAMETER_KEY)).get("exeDate"));	//傳入參數，執行日期(確認送出日期)
		Date exeDate = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		
		if(StringUtils.isNotBlank(arg)) {
			try {
				exeDate = sdf.parse(arg);
			} catch(Exception e) {}
		}
		
		if(exeDate != null) today = sdf.format(exeDate);
		
		//抓取SQL語法
		StringBuffer sb = new StringBuffer();				 
		dam = this.getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(exeDate == null ? DataAccessManager.QUERY_LANGUAGE_TYPE_SQL : DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		sb.append("SELECT CON_NO, CUST_ID, ");
		sb.append("CASE UNLIMIT_DOC_TYPE WHEN 'A' THEN '1' WHEN 'B' THEN '2' END AS DOC_N, ");
		sb.append("CASE LIMIT_DOC_TYPE WHEN 'A' THEN 'C' WHEN 'B' THEN 'E' END AS DOC_Y, ");
		sb.append("CASE EDU_YN WHEN 'Y' THEN 'Y' ELSE '' END AS EDU_YN, ");
		sb.append("CASE MED_YN WHEN 'Y' THEN 'Y' ELSE '' END AS MED_YN, ");
		sb.append("CASE MED_PAY_FOR WHEN 'A' THEN 'Y' WHEN 'A,B' THEN 'Y' ELSE '' END AS MED_1, ");
		sb.append("CASE MED_PAY_FOR WHEN 'B' THEN 'Y' WHEN 'A,B' THEN 'Y' ELSE '' END AS MED_2, ");
		sb.append("CASE NUR_YN WHEN 'Y' THEN 'Y' ELSE '' END AS NUR_YN, ");
		sb.append("CASE NUR_PAY_FOR WHEN 'A' THEN 'Y' WHEN 'A,B' THEN 'Y' ELSE '' END AS NUR_1, ");
		sb.append("CASE NUR_PAY_FOR WHEN 'B' THEN 'Y' WHEN 'A,B' THEN 'Y' ELSE '' END AS NUR_2, ");
		sb.append("CASE OTR_YN WHEN 'Y' THEN 'Y' ELSE '' END AS OTR_YN, ");
		sb.append("MAR_CURR, MAR_AMT, BIR_CURR, BIR_AMT, ");
		sb.append("HOS_CURR, HOS_AMT, OTR_CURR, OTR_AMT ");
		sb.append("FROM TBMTC_CONTRACT_MAIN ");
		sb.append("WHERE CON_STATUS = 'C' ");
		
		//是否有傳入"傳送日期"參數
		if(exeDate != null) {
			sb.append(" AND TRUNC(LASTUPDATE) = TRUNC(:exeDate) ");
			qc.setObject("exeDate", exeDate);
		} else {
			sb.append(" AND TRUNC(LASTUPDATE) = TRUNC(SYSDATE) ");
		}
				
		//執行SQL
		qc.setQueryString(sb.toString());
		List<Map<String, Object>> dataList = dam.exeQuery(qc);
		
		//產出txt需要之參數
		String fileName = "NMP2YDW2" + "_" + today + ".txt";
		String path = SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH) + "reports\\";
		File file1 = new File(path + fileName);
		//若檔案已存在則刪除重新產生
		if(file1.exists()) file1.delete();
		file1.createNewFile();
				
		//把執行完SQL的結果產出txt
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file1, true), "Big5"));	//ANSI編碼
		int count = 0;
		try {
			writer.append("@" + today + System.lineSeparator());	//首筆放@+西原年月日8碼
			writer.flush();
			
			for(Map<String, Object> map : dataList){
				BigDecimal mar_amt = map.get("MAR_AMT") == null ? BigDecimal.ZERO : new BigDecimal(map.get("MAR_AMT").toString());
				BigDecimal bir_amt = map.get("BIR_AMT") == null ? BigDecimal.ZERO : new BigDecimal(map.get("BIR_AMT").toString());
				BigDecimal hos_amt = map.get("HOS_AMT") == null ? BigDecimal.ZERO : new BigDecimal(map.get("HOS_AMT").toString());
				BigDecimal otr_amt = map.get("OTR_AMT") == null ? BigDecimal.ZERO : new BigDecimal(map.get("OTR_AMT").toString());
				
				writer.append(addBlankForString(map.get("CON_NO"), 15));						//序號
				writer.append(addBlankForString(map.get("CUST_ID"), 10));						//受益人證號
				writer.append(addBlankForString(map.get("DOC_N"), 1));							//1.申請特別給付－免單據
				writer.append(addBlankForString(map.get("DOC_Y"), 1));							//2.申請特別給付－附單據
				writer.append(addBlankForString(map.get("EDU_YN"), 1));							//2-1.教育費支出
				writer.append(addBlankForString(map.get("MED_YN"), 1));							//2-2.醫療費代墊款
				writer.append(addBlankForString(map.get("MED_1"), 1));							//2-2-1.受益人本人帳戶
				writer.append(addBlankForString(map.get("MED_2"), 1));							//2-2-2.墊款費用之信託監察人帳戶
				writer.append(addBlankForString(map.get("NUR_YN"), 1));							//2-3.安養護機構及看護支出
				writer.append(addBlankForString(map.get("NUR_1"), 1));							//2-3-1.受益人本人帳戶
				writer.append(addBlankForString(map.get("NUR_2"), 1));							//2-3-2.經書面指定之安養護機構及個人看護
				writer.append(addBlankForString(map.get("OTR_YN"), 1));							//2-4.其他
				writer.append(addBlankForString(map.get("MAR_CURR"), 3));						//2-4-1.其他_結婚_幣別
				writer.append(addZeroForNum(new EsbUtil().decimalPadding(mar_amt, 12, 2), 14));	//2-4-1.其他_結婚_金額
				writer.append(addBlankForString(map.get("BIR_CURR"), 3));						//2-4-2.其他_生育_幣別
				writer.append(addZeroForNum(new EsbUtil().decimalPadding(bir_amt, 12, 2), 14));	//2-4-2.其他_生育_金額
				writer.append(addBlankForString(map.get("HOS_CURR"), 3));						//2-4-3.其他_購屋_幣別
				writer.append(addZeroForNum(new EsbUtil().decimalPadding(hos_amt, 12, 2), 14));	//2-4-3.其他_購屋_金額
				writer.append(addBlankForString(map.get("OTR_CURR"), 3));						//2-4-4.其他_其他_幣別
				writer.append(addZeroForNum(new EsbUtil().decimalPadding(otr_amt, 12, 2), 14));	//2-4-4.其他_其他_金額
				
				writer.append(System.lineSeparator());
				writer.flush();
				count++;
			}
			writer.append("#" + addZeroForNum(String.valueOf(count), 10));	//尾筆放#+轉檔筆數10碼(右靠左補零)
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				writer.close();
			} catch(IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * BA05：監察人資料
	 * @param body
	 * @param header
	 * @throws Exception
	 */
	public void BTMTC001_BA05(Object body, IPrimitiveMap<?> header) throws Exception {
		// 記錄排程監控的log
		audit = (AuditLogUtil) ((Map<?, ?>) body).get(SchedulerHelper.AUDIT_PARAMETER_KEY);
		String arg = ObjectUtils.toString(((Map)((Map)body).get(SchedulerHelper.JOB_PARAMETER_KEY)).get("exeDate"));	//傳入參數，執行日期(確認送出日期)
		Date exeDate = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		
		if(StringUtils.isNotBlank(arg)) {
			try {
				exeDate = sdf.parse(arg);
			} catch(Exception e) {}
		}
		
		if(exeDate != null) today = sdf.format(exeDate);
		
		//抓取SQL語法
		StringBuffer sb = new StringBuffer();				 
		dam = this.getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(exeDate == null ? DataAccessManager.QUERY_LANGUAGE_TYPE_SQL : DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		sb.append("SELECT M.CON_NO, D.CUST_ID, D.REL_TYPE, ");
		sb.append("DECODE(D.REL_TYPE, '2', '1', '3', '1', '4', '2', '5', '3', '') LEV, ");
		sb.append("NVL(D.PAY_YN, 'N') PAY_YN, D.PAY_FREQ, D.PAY_CURR, D.PAY_AMT ");
		sb.append("FROM TBMTC_CONTRACT_MAIN M ");
		sb.append("LEFT JOIN TBMTC_CONTRACT_DETAIL D ON D.CON_NO = M.CON_NO AND D.REL_TYPE <> '1' ");
		sb.append("WHERE M.CON_STATUS = 'C' ");
		//是否有傳入"傳送日期"參數
		if(exeDate != null) {
			sb.append(" AND TRUNC(M.LASTUPDATE) = TRUNC(:exeDate) ");
			qc.setObject("exeDate", exeDate);
		} else {
			sb.append(" AND TRUNC(M.LASTUPDATE) = TRUNC(SYSDATE) ");
		}
		
		//執行SQL
		qc.setQueryString(sb.toString());
		List<Map<String, Object>> dataList = dam.exeQuery(qc);
		
		//產出txt需要之參數
		String fileName = "NMP2YEWK" + "_" + today + ".txt";
		String path = SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH) + "reports\\";
		File file1 = new File(path + fileName);
		//若檔案已存在則刪除重新產生
		if(file1.exists()) file1.delete();
		file1.createNewFile();
				
		//把執行完SQL的結果產出txt
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file1, true), "Big5"));	//ANSI編碼
		int count = 0;
		try {
			writer.append("@" + today + System.lineSeparator());	//首筆放@+西原年月日8碼
			writer.flush();
			
			for(Map<String, Object> map : dataList){
				BigDecimal pay_amt = map.get("PAY_AMT") == null ? BigDecimal.ZERO : new BigDecimal(map.get("PAY_AMT").toString());
				
				writer.append(addBlankForString(map.get("CON_NO"), 15));						//序號
				writer.append(addBlankForString(map.get("CUST_ID"), 10));						//監察人證號
				writer.append(addBlankForString(map.get("LEV"), 1));							//監察人順位
				writer.append(addBlankForString(map.get("PAY_YN"), 1));							//有無報酬及給付
				
				//"報酬及給付頻率 M：每月　S：每季（1. 4. 7. 10月當月）　H：每半年（1. 7月當月）　01～12（代表每年NN月）"
				String pay_freq = map.get("PAY_FREQ") == null ? "" : map.get("PAY_FREQ").toString();
				if (StringUtils.isNotBlank(pay_freq) && !pay_freq.equals("M") && !pay_freq.equals("S") && !pay_freq.equals("H")) {
					if (pay_freq.length() == 1) {
						pay_freq = "0" + pay_freq;
					}
				}
				writer.append(addBlankForString(pay_freq, 2));									//報酬及給付頻率
				
//				String pay_curr = map.get("PAY_CURR") == null ? "" : (map.get("PAY_CURR").toString().equals("TWD") ? "NTD" : "USD");
//				writer.append(addBlankForString(pay_curr, 3));									//監察費(幣別？)
				writer.append(addZeroForNum(new EsbUtil().decimalPadding(pay_amt, 8, 2), 10));	//監察費
				
				writer.append(System.lineSeparator());
				writer.flush();
				count++;
			}			
			writer.append("#" + addZeroForNum(String.valueOf(count), 10));	//尾筆放#+轉檔筆數10碼(右靠左補零)
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				writer.close();
			} catch(IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
     * 補0
     * @param 字串 長度
     * @return
     * @throws Exception
     */
	public String addZeroForNum(Object obj, int strLength) {
		String str = obj == null ? "" : ObjectUtils.toString(obj);
	    int strLen = str.length();
	    if (strLen < strLength) {
	        while (strLen < strLength) {
	            StringBuffer sb = new StringBuffer();
	            sb.append("0").append(str);// 左補0
	            // sb.append(str).append("0");//右補0
	            str = sb.toString();
	            strLen = str.length();
	        }
	    }
	    return str;
	}
	
	 /**
     * 補空格
     * @param 字串 長度
     * @return
     * @throws Exception
     */
	public String addBlankForString(Object obj, int strLength) {
		String str = obj == null ? "" : ObjectUtils.toString(obj);
	    int strLen = str.length();
	    if (strLen < strLength) {
	        while (strLen < strLength) {
	            StringBuffer sb = new StringBuffer();
	            sb.append(str).append(" ");//右補空格
	            str = sb.toString();
	            strLen = str.length();
	        }
	    } else if (strLen > strLength) {
	    	str = str.substring(0, strLength);
	    }
	    return str;
	}
	
	/**
	 * 需補全形空白
	 * @param 字串 長度
	 * @return
	 * @throws Exception
	 */
	public String addFullBlankForString(Object obj, int strLength) {
		String str = obj == null ? "" : ObjectUtils.toString(obj);
		int strLen = str.length();
		if (strLen < strLength) {
			while (strLen < strLength) {
				StringBuffer sb = new StringBuffer();
				sb.append(str).append("　");	//右補全形空白
				str = sb.toString();
				strLen = str.length();
			}
		} else if (strLen > strLength) {
			str = str.substring(0, strLength);
		}
		return str;
	}
}
