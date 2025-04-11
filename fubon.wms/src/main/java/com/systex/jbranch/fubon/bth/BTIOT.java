package com.systex.jbranch.fubon.bth;

import java.io.File;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;





import com.systex.jbranch.app.common.fps.table.TBIOT_MAINVO;
import com.systex.jbranch.fubon.bth.ftp.BthFtpJobUtil;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.scheduler.AuditLogUtil;
import com.systex.jbranch.platform.common.scheduler.SchedulerHelper;
import com.systex.jbranch.platform.common.util.CSVUtil;
import com.systex.jbranch.platform.server.bizLogic.BizLogic;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * 1.初版
 
 * @author 1500637
 * @date 2016/12/12
 
 **/
@Repository("btiot")
@Scope("prototype")
public class BTIOT extends BizLogic {
	private DataAccessManager dam = null;
	private BthFtpJobUtil bthUtil = new BthFtpJobUtil();
	private AuditLogUtil audit = null;
//	private Logger logger = LoggerFactory.getLogger(this.getClass());

	String today = new SimpleDateFormat("yyyyMMddHH").format(new Date());
	
	public void BTIOT001(Object body, IPrimitiveMap<?> header) throws Exception {
		// 記錄排程監控的log
		audit = (AuditLogUtil) ((Map<?, ?>) body).get(SchedulerHelper.AUDIT_PARAMETER_KEY);
		
		// 取得傳入參數 
		Map<String, Object> inputMap = (Map<String, Object>) body;
		Map<String, Object> jobParam = (Map<String, Object>) inputMap.get(SchedulerHelper.JOB_PARAMETER_KEY);
		
		// TBSYSFTP.FTPSETTINGID FTP畫面設定(CMMGR014)
		String ftpCode = (String) jobParam.get("ftpCode");
		String fileName = (String) jobParam.get("fileName");
		// csv的標頭
		String[] csvHeader = { "FLAG", "KEYIN_DATE", "OP_BATCH_NO", "INS_ID",
				"INSURED_NAME", "BRANCH_NBR", "BRANCH_NAME", "INSPRD_ID",
				"INSPRD_ANNUAL", "PAY_TYPE", "MOP2", "SPECIAL_CONDITION",
				"CURR_CD", "REAL_PREMIUM", "DOC_TYPE", "TARGET_ID_PCT",
				"DOC_NAME", "CASE_ID", "RECRUIT_ID", "RECRUIT_NAME", "SIGN_OPNAME", 
				"SIGN_DATE", "ASSISTANT_NAME" };
		String[] csvHeader_zh = { "FLAG", "鍵機日期", "分行送件批號", "保險文件編號", "被保人姓名",
				"分行代碼", "分行名稱", "險種代碼", "繳費年期", "躉繳分期繳", "分期繳別", "特殊條件", "幣別",
				"實收保費_原幣", "文件類別 ", "投資標的/比例", "保險文件明細", "案件編號", "業務員身分證字號",
				"業務員姓名", "簽署人姓名", "簽署日期", "行助姓名" };
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT  'A' AS FLAG, ");
		sb.append("A.INS_KEYNO, ");
		sb.append("TO_CHAR(A.KEYIN_DATE,'YYYY/MM/DD HH24:MI:SS') AS KEYIN_DATE, "); // 鍵機日期
		sb.append("A.OP_BATCH_NO AS OP_BATCH_NO, "); // 分行送件批號
		sb.append("A.INS_ID AS INS_ID, "); // 保險文件編號
		sb.append("A.INSURED_NAME AS INSURED_NAME, "); // 被保人姓名
		sb.append("A.BRANCH_NBR AS BRANCH_NBR, "); // 分行代碼
		sb.append("DECODE( A.BRANCH_NBR, '000', '總行', (SELECT BRANCH_NAME FROM VWORG_DEFN_INFO WHERE BRANCH_NBR = A.BRANCH_NBR)) AS BRANCH_NAME, "); // 分行名稱
		sb.append("A.INSPRD_ID AS INSPRD_ID, "); // 險種代碼
		sb.append("A.INSPRD_ANNUAL AS INSPRD_ANNUAL, "); // 繳費年期
		sb.append("(SELECT PARAM_NAME FROM TBSYSPARAMETER WHERE PARAM_TYPE = 'PRD.PAY_TYPE' AND PARAM_CODE = A.PAY_TYPE) PAY_TYPE, "); // 躉繳分期繳
		sb.append("(SELECT PARAM_NAME FROM TBSYSPARAMETER WHERE PARAM_TYPE = 'IOT.MOP2' AND PARAM_CODE = A.MOP2) MOP2, "); // 分期繳別
		sb.append("A.SPECIAL_CONDITION AS SPECIAL_CONDITION, "); // 特殊條件
		sb.append("A.CURR_CD AS CURR_CD, "); // 幣別
		sb.append("A.REAL_PREMIUM AS REAL_PREMIUM, "); // 實收保費_原幣
		sb.append("Decode(REG_TYPE, '3', 'OT', 'NB') AS DOC_TYPE, "); // 文件類別
		sb.append("(SELECT LISTAGG((SELECT TARGET_ID FROM TBPRD_INS_LINKING WHERE KEY_NO = PRD_LK_KEYNO)||':'||LINK_PCT, '|') WITHIN GROUP (ORDER BY INS_KEYNO) "); //投資標的/比例
		sb.append("FROM TBIOT_FUND_LINK WHERE INS_KEYNO = A.INS_KEYNO AND NVL(LINK_PCT, 0) <> 0)TARGET_ID_PCT, ");
		sb.append("(SELECT LISTAGG(DECODE(DOC_SEQ, '99', DOC_NAME_OTH, DOC_NAME), '|') WITHIN GROUP (ORDER BY INS_KEYNO, DOC_SEQ)DOC_NAME "); //保險文件明細
		sb.append("FROM TBIOT_DOC_CHK WHERE INS_KEYNO = A.INS_KEYNO AND DOC_CHK='Y' )DOC_NAME, ");
		sb.append("A.CASE_ID AS CASE_ID, "); //案件編號
		sb.append("D.CUST_ID AS RECRUIT_ID, "); //招攬人員ID
		sb.append("D.EMP_NAME AS RECRUIT_NAME, "); //招攬人員姓名
		sb.append("B.EMP_NAME AS SIGN_OPNAME, ");  //簽署人員姓名
		sb.append("TO_CHAR(A.SIGN_DATE ,'YYYY/MM/DD HH24:MI:SS') AS SIGN_DATE, ");   //簽署日期
		sb.append("C.EMP_NAME AS ASSISTANT_NAME "); //行助姓名
		sb.append("FROM VWIOT_MAIN A ");
		sb.append("LEFT OUTER JOIN TBORG_MEMBER B ON B.EMP_ID = A.SIGN_OPRID ");
		sb.append("LEFT OUTER JOIN TBORG_MEMBER C ON C.EMP_ID = A.AFT_SIGN_OPRID  ");
		sb.append("LEFT OUTER JOIN TBORG_MEMBER D ON D.EMP_ID = A.RECRUIT_ID ");
		sb.append("WHERE A.STATUS = '60' ");
		sb.append(" AND A.REG_TYPE IN ('1','2','3') ");
		sb.append(" AND A.COMPANY_NUM = 82 "); //只產生富壽件
		sb.append(" AND NVL(A.NO_PAPER_YN, 'N') = 'N' "); //排除無紙化案件
		 
		dam = this.getDataAccessManager();
		// 執行以上sql
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_SQL);
		qc.setQueryString(sb.toString());
		@SuppressWarnings("unchecked")
		// 將以上DATA包程一個LIST,並丟入執行
		List<Map<String, Object>> dataList = dam.exeQuery(qc);

		OutputCsv((String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH)+"reports\\", fileName, csvHeader, csvHeader_zh, dataList);
		File file = new File((String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH) + "reports/" + fileName+"_" + today + ".csv");
	    //System.out.println((String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH) + "reports/" + fileName+"_" + today + ".csv");
		//System.out.println("file isFile:"+file.isFile());
		TBIOT_MAINVO vo = null;
//		判斷是否有檔案，若有成功才做下面的update
		if(file.exists()) { 
//			根據以上產檔的INS_KEYNO逐筆update
			java.util.Date date = new java.util.Date();
			Timestamp now_date = new Timestamp(date.getTime());
			
//			根據以上產檔的INS_KEYNO逐筆update
			for (Map<String, Object> map : dataList) {
//				將主鍵放入vo (TABLE_UID => 主鍵)				
				vo = (TBIOT_MAINVO) dam.findByPKey(TBIOT_MAINVO.TABLE_UID, (BigDecimal) map.get("INS_KEYNO"));
//				判斷若vo不是空值，才做update，若是空值的話不寫這個判斷會出錯				
				if (null != vo) {
					vo.setSTATUS(new BigDecimal("70"));
					vo.setLastupdate(now_date);
					vo.setModifier("BTIOT001");
					vo.setINS_SUBMIT_DATE(now_date);
					//dam.update(vo);
				}
			}
		}		
		
		//更新無紙化案件傳送人壽日期
		exeUpdateForMap(getUpdateInsSubmitDateSql(), new HashMap());
		//更新無紙化案件人壽回饋簽收日期
		exeUpdateForMap(getUpdateInsRcvDateSql(), new HashMap());
		
//		ftpUpload(ftpCode);

	}
	
	//更新無紙化案件傳送人壽日期
	//因不用傳送人壽就都押上今天日期
	private String getUpdateInsSubmitDateSql() {
		return new StringBuffer()
			.append("UPDATE TBIOT_MAIN ")
			.append(" SET INS_SUBMIT_DATE = SYSDATE, MODIFIER = 'BTIOT001', LASTUPDATE = SYSDATE ")
			.append(" WHERE STATUS = '60' AND REG_TYPE IN ('1','2','3') AND COMPANY_NUM = 82 AND NVL(NO_PAPER_YN, 'N') = 'Y' AND INS_SUBMIT_DATE IS NULL ")
			.toString();
	}
	
	//更新無紙化案件人壽回饋簽收日期
	//因人壽不會會饋，都押上今天日期
	private String getUpdateInsRcvDateSql() {
		return new StringBuffer()
			.append("UPDATE TBIOT_BATCH_INFO ")
			.append(" SET INS_RCV_DATE = SYSDATE, INS_RCV_OPRID = 'VP1', LASTUPDATE = SYSDATE, MODIFIER = 'BTIOT001' ")
			.append(" WHERE INS_RCV_DATE IS NULL AND BATCH_INFO_KEYNO IN (SELECT BATCH_INFO_KEYNO FROM TBIOT_MAIN ")
			.append(" 			WHERE STATUS = '60' AND REG_TYPE IN ('1','2','3') AND COMPANY_NUM = 82 AND NVL(NO_PAPER_YN, 'N') = 'Y') ")
			.toString();
	}
	
	//產出CSVO
	private String OutputCsv(String path,String fileName, String[] csvHeader, String[] csvHeader_zh,
			List<Map<String, Object>> datalist) throws JBranchException {
		
		List<Object[]> csvData = new ArrayList<Object[]>();
		
//		檔案路徑+檔案名稱
		path = path + "reports\\";
		
		for(Map<String, Object> map : datalist){
			String[] records = new String[csvHeader.length];
//			一個欄位一個欄位判斷，取出來的欄位是否為null
			for (int i = 0; i < csvHeader.length; i++) {
				records[i] = checkIsNull(map, csvHeader[i]).toString() ;
			}
			csvData.add(records);
		}
		
		CSVUtil csvUtil = new CSVUtil();
		csvUtil.setFileName(fileName +"_"+today);
		if(fileName != null && csvHeader_zh.length > 0){
			csvUtil.setHeader(csvHeader_zh);
		}
		
		csvUtil.addRecordList(csvData);
		
		return csvUtil.generateCSV();
	}
	
//	private void ftpUpload(String ftpCode) throws Exception {
//		try {
//			bthUtil.ftpPutFile(ftpCode);
//		} catch (Exception e) {
//			audit.audit(ExceptionUtils.getStackTrace(e));
//		}
//	}
	
	/**
	* 檢查Map取出欄位是否為Null
	*/
	private String checkIsNull(Map<String, Object> map, String key) {		
		if (StringUtils.isNotBlank(String.valueOf(map.get(key))) && null != map.get(key)) {
			if(key == "DOC_NAME"){
				return String.valueOf(map.get(key)).replaceAll(",", "，");
			}else
				return String.valueOf(map.get(key));
		} else {
			return "";
		}
	}
}
