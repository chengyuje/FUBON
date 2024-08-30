package com.systex.jbranch.fubon.bth;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

import com.systex.jbranch.app.common.fps.table.TBKYC_COOLING_PERIODVO;
import com.systex.jbranch.platform.common.dataManager.DataManager;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.scheduler.AuditLogUtil;
import com.systex.jbranch.platform.common.scheduler.SchedulerHelper;
import com.systex.jbranch.platform.common.util.CSVUtil;
import com.systex.jbranch.platform.common.util.PathUtil;
import com.systex.jbranch.platform.server.bizLogic.BizLogic;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * #1817: 保險名單聯絡資訊比對 處理不相符資料的產檔
 * 
 * @author Sam Tu
 * @date 2023/11/30
 **/
@Repository("btiot011")
@Scope("prototype")
public class BTIOT011 extends BizLogic {

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private AuditLogUtil audit = null;

	private DataAccessManager dam = null;

	public void execute(Object body, IPrimitiveMap<?> header) throws Exception {

		// 取得傳入參數
		Map<String, Object> inputMap = (Map<String, Object>) body;
		Map<String, Object> jobParameter = (Map<String, Object>) inputMap.get(SchedulerHelper.JOB_PARAMETER_KEY);

		// 語法使用參數
		String chkType = (String) jobParameter.get("pckName"); // 相似項目

		// 取得不相符資料
		BigDecimal total = (BigDecimal) getResultTotal().get(0).get("TOTAL");
		int totalInt = total.intValue();

		// 參數
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> checkItem = xmlInfo.doGetVariable("PMS.SIMILAR_INFO", FormatHelper.FORMAT_3); // 查核項目
		Map<String, String> sourceItem = xmlInfo.doGetVariable("PMS.COMPARE_SOURCE", FormatHelper.FORMAT_3); // 檔案來源
		Map<String, String> genParam = xmlInfo.doGetVariable("BTIOT011", FormatHelper.FORMAT_3); // 產檔參數
		int custCount = Integer.parseInt(genParam.get("CUSTCOUNT"));
		int genTotal = Integer.parseInt(genParam.get("GENTOTAL"));

		// 寫入CSV
		List listCSV = new ArrayList();
		// header
		String[] csvHeader = new String[4];
		int j = 0;
		csvHeader[j] = "保單號碼";
		csvHeader[++j] = "客戶ID";
		csvHeader[++j] = "不相同內容";
//		csvHeader[++j] = "查核項目";
//		csvHeader[++j] = "檔案來源";
		csvHeader[++j] = "理專ID";
//		csvHeader[++j] = "理專姓名";
//		csvHeader[++j] = "分行代號";

		int count = 0;
		int page = 0;

		for (int cust = 0; cust < totalInt; cust = cust + custCount) {

			List<Map<String, Object>> checklist = getMainSqlResult(chkType, cust, custCount);
			System.out.println(totalInt + "-" + cust + "-" + custCount + "-" + checklist.size() + "-" + listCSV.size());

			for (Map<String, Object> map : checklist) {

				String[] records = new String[4];
//				int i = 0;
				records[0] = "=\"" + checkIsNull(map, "POLICY_NO") + "\"";
				records[1] = "=\"" + checkIsNull(map, "CUST_ID") + "\"";
				records[2] = "=\"" + checkIsNull(map, "CONTENT") + "\"";
//				records[++i] = "=\"" + checkItem.get(checkIsNull(map, "CHK_TYPE"))+ "\"";
//				records[++i] = "=\"" + sourceItem.get(checkIsNull(map, "SOURCE")) + "\"";
				records[3] = "=\"" + checkIsNull(map, "EMP_CUST_ID") + "\"";
//				records[++i] = "=\"" + checkIsNull(map, "EMP_NAME") + "\"";
//				records[++i] = "=\"" + checkIsNull(map, "BRANCH_ID") + "\"";
				listCSV.add(records);
				count = count + 1;

				if (count == genTotal) {
					page = page + 1;
					CSVUtil csv = new CSVUtil();
					csv.setHeader(csvHeader);
					csv.addRecordList(listCSV);
					csv.setFileName(getFileName(chkType) + page);
					csv.generateCSV();
					listCSV = new ArrayList();
					count = 0;
				}

			}
		}
		if (listCSV.size() != 0) {
			page = page + 1;
			CSVUtil csv = new CSVUtil();
			csv.setHeader(csvHeader);
			csv.addRecordList(listCSV);
			csv.setFileName(getFileName(chkType) + page);
			csv.generateCSV();
			listCSV.clear();
			count = 0;
		}
	}

	private String getFileName(String chkType) {
		String date = new SimpleDateFormat("yyyyMM").format(new Date());
		String FileName = "";
		switch (chkType) {
		case "1":
			FileName = "BTIOT011_" + date + "_RES_ADDRESS_";
			break;
		case "2":
			FileName = "BTIOT011_" + date + "_CONTACT_ADDRESS_";
			break;
		case "3":
			FileName = "BTIOT011_" + date + "_DAYTIME_PHONE_";
			break;
		case "4":
			FileName = "BTIOT011_" + date + "_NIGHT_PHONE_";
			break;
		case "5":
			FileName = "BTIOT011_" + date + "_CELL_PHONE_";
			break;
		case "6":
			FileName = "BTIOT011_" + date + "_EMAIL1_";
			break;
		case "7":
			FileName = "BTIOT011_" + date + "_EMAIL2_";
			break;
		}
		return FileName;
	}

	private List<Map<String, Object>> getResultTotal() throws DAOException, JBranchException {
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT count(1) as TOTAL FROM TBIOT_ADDRTELMAIL_COMPARE C ");
		sql.append(" WHERE TO_CHAR(C.APL_DATE, 'yyyyMMdd') >= to_char(add_months(SYSDATE, -3), 'yyyyMMdd') ");
		sql.append(
				" AND NOT EXISTS (SELECT DISTINCT CHK.CUST_ID FROM TBIOT_ADDRTELMAIL_CHK CHK where CHK.CUST_ID = C.CUST_ID)   ");
		queryCondition.setQueryString(sql.toString());
		return dam.exeQuery(queryCondition);
	}

	private List<Map<String, Object>> getMainSqlResult(String chkType, int cust, int custCount)
			throws DAOException, JBranchException {
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT ");
//		sql.append(" DISTINCT POLICY_NO, CUST_ID, CONTENT, CHK_TYPE, SOURCE, BRANCH_ID, EMP_CUST_ID, EMP_NAME ");
		sql.append(" DISTINCT POLICY_NO, CUST_ID, CONTENT, EMP_CUST_ID ");
		sql.append(" FROM ( ");
		sql.append(" SELECT ");
		sql.append(" COMPARE.POLICY_NO||'-'||COMPARE.POLICY_SEQ||'-'||COMPARE.ID_DUP AS POLICY_NO, ");
		sql.append(" COMPARE.CUST_ID, ");
		sql.append(getSelectedColumnSql(chkType));
		sql.append(" EMPINFO.SOURCE, ");
		sql.append(" CASE WHEN EMPINFO.SOURCE = 'E' THEN COMPARE.BRANCH_ID ELSE EMPINFO.BRANCH END AS BRANCH_ID , ");
		sql.append(" CASE WHEN EMPINFO.SOURCE = 'E' THEN COMPARE.EMP_ID ELSE EMPINFO.EMP_ID END AS EMP_ID , ");
		sql.append(
				" CASE WHEN EMPINFO.SOURCE = 'E' THEN COMPARE.EMP_CUST_ID ELSE EMPINFO.CUST_ID END AS EMP_CUST_ID , ");
		sql.append(" CASE WHEN EMPINFO.SOURCE = 'E' THEN COMPARE.EMP_NAME ELSE EMPINFO.EMP_NAME END AS EMP_NAME  ");
		sql.append(" FROM ");
		sql.append(" ( SELECT * FROM  ");
		sql.append(" (SELECT ROWNUM as SEQ , C.* FROM ( ");
		sql.append(" SELECT C.* FROM TBIOT_ADDRTELMAIL_COMPARE C  ");
		sql.append(" WHERE TO_CHAR(C.APL_DATE, 'yyyyMMdd') >= to_char(add_months(SYSDATE, -3), 'yyyyMMdd')  ");
		sql.append(
				" AND NOT EXISTS (SELECT DISTINCT CHK.CUST_ID FROM TBIOT_ADDRTELMAIL_CHK CHK where CHK.CUST_ID = C.CUST_ID)   ");
		sql.append(" ORDER BY POLICY_NO,POLICY_SEQ,ID_DUP) C ");
		sql.append(" ) C  ");
		sql.append(" WHERE ");
		sql.append(" SEQ >= :MINSEQ ");
		sql.append(" and SEQ < :MAXSEQ ");
		sql.append(" ) COMPARE ");
		sql.append(getWhereConditionSql(chkType));
		sql.append(" ) ");
		sql.append(" ORDER BY POLICY_NO, CUST_ID ");
		queryCondition.setObject("MINSEQ", cust);
		queryCondition.setObject("MAXSEQ", cust + custCount);
		queryCondition.setQueryString(sql.toString());
		return dam.exeQuery(queryCondition);
	}

	private Object getWhereConditionSql(String chkType) {
		StringBuffer sql = new StringBuffer();

		switch (chkType) {
		case "1":
			sql.append(
					" INNER JOIN TBORG_SALES_ADDR EMPINFO ON EMPINFO.DATA <> COMPARE.CHECK_RES_ADDRESS AND COMPARE.CUST_ID <> EMPINFO.CUST_ID ");
			break;
		case "2":
			sql.append(
					" INNER JOIN TBORG_SALES_ADDR EMPINFO ON EMPINFO.DATA <> COMPARE.CHECK_CONTACT_ADDRESS AND COMPARE.CUST_ID <> EMPINFO.CUST_ID ");
			break;
		case "3":
			sql.append(
					" INNER JOIN TBORG_SALES_TEL EMPINFO ON EMPINFO.DATA <> COMPARE.CHECK_DAYTIME_PHONE AND COMPARE.CUST_ID <> EMPINFO.CUST_ID ");
			break;
		case "4":
			sql.append(
					" INNER JOIN TBORG_SALES_TEL EMPINFO ON EMPINFO.DATA <> COMPARE.CHECK_NIGHT_PHONE AND COMPARE.CUST_ID <> EMPINFO.CUST_ID ");
			break;
		case "5":
			sql.append(
					" INNER JOIN TBORG_SALES_TEL EMPINFO ON EMPINFO.DATA <> COMPARE.CHECK_CELL_PHONE AND COMPARE.CUST_ID <> EMPINFO.CUST_ID ");
			break;
		case "6":
			sql.append(
					" INNER JOIN TBORG_SALES_MAIL EMPINFO ON EMPINFO.DATA <> COMPARE.CHECK_EMAIL1 AND COMPARE.CUST_ID <> EMPINFO.CUST_ID ");
			break;
		case "7":
			sql.append(
					" INNER JOIN TBORG_SALES_MAIL EMPINFO ON EMPINFO.DATA <> COMPARE.CHECK_EMAIL2 AND COMPARE.CUST_ID <> EMPINFO.CUST_ID ");
			break;
		}
		return sql.toString();
	}

	private Object getSelectedColumnSql(String chkType) {
		StringBuffer sql = new StringBuffer();

		switch (chkType) {
		case "1":
			sql.append(" COMPARE.RES_ADDRESS AS CONTENT, ");
			sql.append(" COMPARE.CHECK_RES_ADDRESS AS CHECK_CONTENT, ");
			sql.append(" '1' AS CHK_TYPE, ");
			break;
		case "2":
			sql.append(" COMPARE.CONTACT_ADDRESS AS CONTENT, ");
			sql.append(" COMPARE.CHECK_CONTACT_ADDRESS AS CHECK_CONTENT, ");
			sql.append(" '2' AS CHK_TYPE, ");
			break;
		case "3":
			sql.append(" COMPARE.DAYTIME_PHONE AS CONTENT, ");
			sql.append(" COMPARE.CHECK_DAYTIME_PHONE AS CHECK_CONTENT, ");
			sql.append(" '3' AS CHK_TYPE, ");
			break;
		case "4":
			sql.append(" COMPARE.NIGHT_PHONE AS CONTENT, ");
			sql.append(" COMPARE.CHECK_NIGHT_PHONE AS CHECK_CONTENT, ");
			sql.append(" '4' AS CHK_TYPE, ");
			break;
		case "5":
			sql.append(" COMPARE.CELL_PHONE AS CONTENT, ");
			sql.append(" COMPARE.CHECK_CELL_PHONE AS CHECK_CONTENT, ");
			sql.append(" '5' AS CHK_TYPE, ");
			break;
		case "6":
			sql.append(" COMPARE.EMAIL1 AS CONTENT, ");
			sql.append(" COMPARE.CHECK_EMAIL1 AS CHECK_CONTENT, ");
			sql.append(" '6' AS CHK_TYPE, ");
			break;
		case "7":
			sql.append(" COMPARE.EMAIL2 AS CONTENT, ");
			sql.append(" COMPARE.CHECK_EMAIL2 AS CHECK_CONTENT, ");
			sql.append(" '6' AS CHK_TYPE, ");
			break;
		}
		return sql.toString();
	}

	private String checkIsNull(Map map, String key) {
		if (StringUtils.isNotBlank(ObjectUtils.toString(map.get(key)))) {
			return String.valueOf(map.get(key));
		} else {
			return "";
		}
	}

	/*
	 * 將產出的未符合檔案變成一個zip檔
	 */
	public void zipFiles(Object body, IPrimitiveMap<?> header) throws Exception {

		String date = new SimpleDateFormat("yyyyMM").format(new Date());
		String path = PathUtil.getServerHome() + "\\temp\\reports";

		// 建立檔案輸出流
		FileOutputStream fos = new FileOutputStream(path + "\\" + "BTIOT011_" + date + ".zip"); // 輸出檔名
		ZipOutputStream zipOut = new ZipOutputStream(fos); // 用檔案輸出流建立出 Zip 輸出流

		File folder = new File(path);
		String[] list = folder.list();
//		System.out.println("list的長度" + list.length);
		for (String name : list) {
			if (name.indexOf("BTIOT011_" + date) >= 0 && name.indexOf(".zip") < 0) {
				// 在 zip 內加入一個項目
				// (可以是一個檔名，或用目錄來表示)
				byte[] str2Bytes = Files.readAllBytes(Paths.get(path + "\\" + name)); // 檔案轉為 Bytes
				zipOut.putNextEntry(new ZipEntry(name));
				zipOut.write(str2Bytes);
			}
		}

		zipOut.close();
		fos.close();

	}

}
