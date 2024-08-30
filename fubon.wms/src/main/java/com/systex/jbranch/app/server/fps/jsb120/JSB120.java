package com.systex.jbranch.app.server.fps.jsb120;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.ibm.icu.text.DateFormat;
import com.ibm.icu.text.SimpleDateFormat;
import com.systex.jbranch.fubon.bth.GenFileTools;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.CSVUtil;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

import jxl.read.biff.BiffException;

@Component("jsb120")
@Scope("request")
public class JSB120 extends FubonWmsBizLogic {
	private DataAccessManager dam = null;
	
	public void inquire (Object body, IPrimitiveMap header) throws JBranchException {
		JSB120OutputVO outputVO = new JSB120OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		
		sb.append(" SELECT PARAM_CODE AS INS_COM_ID, I.DATA_DATE, I.CREATETIME, I.ACC_VALUE_DATE, ");
		sb.append(" NVL(I.IMP_ROW_COUNT, 0) AS IMP_ROW_COUNT, ");
		sb.append(" NVL(I.UPDATE_ROW_COUNT, 0) AS UPDATE_ROW_COUNT FROM ( ");
		sb.append(" SELECT * FROM TBSYSPARAMETER WHERE PARAM_TYPE = 'JSB.INS_COM_NAME_02') P ");
		sb.append(" LEFT JOIN ( SELECT INS_COM_ID, INS_TYPE, DATA_DATE, CREATETIME, ");
		sb.append(" IMP_ROW_COUNT, UPDATE_ROW_COUNT, ACC_VALUE_DATE FROM TBJSB_INS_IMP ");
		sb.append(" WHERE IMP_TYPE = 'JSB_AST_02') I ON I.INS_COM_ID = P.PARAM_CODE ");
		sb.append(" ORDER BY TO_NUMBER(PARAM_CODE) ");
		
		qc.setQueryString(sb.toString());
		outputVO.setResultList(dam.exeQuery(qc));
		this.sendRtnObject(outputVO);
	}
	
	public void upload (Object body, IPrimitiveMap header) throws Exception {
		JSB120InputVO inputVO = (JSB120InputVO) body;
		JSB120OutputVO outputVO = new JSB120OutputVO();
		List<Map<String, Object>> outputList = new ArrayList<>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		File dataFile = null;
		String loginID = (String) getUserVariable(FubonSystemVariableConsts.LOGINID);
		
		if (!StringUtils.isBlank(inputVO.getFileName())) {
			dataFile = new File((String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH), inputVO.getFileName());
			String fileName = dataFile.getName();
			List<Map<String, String>> resultList = null;
			
			if (fileName.toUpperCase().endsWith("CSV") || fileName.toUpperCase().endsWith("TXT")) {
				String tempPath = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
				List<String[]> dataCsv = CSVUtil.getBig5CSVFile(tempPath, inputVO.getFileName());
				resultList = this.readCSV(dataCsv);
				
			} else if (fileName.toUpperCase().endsWith("XLS")||fileName.toUpperCase().endsWith("XLSX")) {
				resultList = this.readXLS(dataFile);
				
			} else {
				throw new APException("上傳的檔案格式必須是CSV或Excel");
			}
			
			if (resultList.size() > 0) {
				String ins_com_id = inputVO.getIns_com_id();
				Timestamp data_date = new Timestamp(inputVO.getData_date().getTime());
				
				int headSize = 0;
				Map<String, String> headMap = new HashMap<>();
				Map<String, String> map = resultList.get(0);	// 欄位名稱
				for (int i = 1; i <= map.size(); i++) {
					// 排除多餘欄位
					if (StringUtils.isNotBlank(map.get(i+""))) {
						headSize++;
					} else {
						break;
					}
				}
				
				// 檢查欄位數量：每家保險公司的欄位進行上傳，若不符合需提示訊息『上傳檔案欄位與定義不符』
				dam = this.getDataAccessManager();
				QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				StringBuffer sb = new StringBuffer();
				sb.append(" SELECT COUNT(*) AS COUNT FROM TBJSB_INS_IMP_MAP ");
				sb.append(" WHERE SOURCE_FILE_TYPE IN ('JSB_AST_02') ");
				sb.append(" AND INS_COM_ID = :ins_com_id ");
				qc.setObject("ins_com_id", ins_com_id);
				
				qc.setQueryString(sb.toString());
				List<Map<String, Object>> list = dam.exeQuery(qc);
				int columSize = 0;
				if (null != list && list.size() > 0) {
					columSize = Integer.parseInt(list.get(0).get("COUNT").toString());
				} else {
					throw new APException("上傳檔案欄位與定義不符");
				}
				if (headSize != columSize) {
					throw new APException("上傳檔案欄位與定義不符");
				}
				
				//『保單號碼』所在欄位
				String policy_nbr_s = "0";
				//『受理編號』所在欄位
				String acceptid_s = "0";
				// 『保單狀態』所在欄位
				String soucr_status_s = "0";
				// 『中文專案名稱』所在欄位
				String policy_name_s = "0";
				
				// 取得檔案資料相對應欄位
				qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sb = new StringBuffer();
				sb.append(" SELECT SOURCE_FILE_TYPE, TARGET_FILE_TYPE, ");
				sb.append(" INS_COM_ID, INS_TYPE, SOUCE_COL_SEQ, TARGET_COL_SEQ ");
				sb.append(" FROM TBJSB_INS_IMP_MAP ");
				sb.append(" WHERE SOURCE_FILE_TYPE = 'JSB_AST_02' ");
				sb.append(" AND INS_COM_ID = :ins_com_id ");
				
				qc.setObject("ins_com_id", ins_com_id);
				qc.setQueryString(sb.toString());
				List<Map<String, Object>> mapList = dam.exeQuery(qc);
				
				Map<String, Integer> columMap = new HashMap<>();
				for (Map<String, Object> mapMap : mapList) {
					// 取得檔案資料相對應欄位
					columMap.put(mapMap.get("SOUCE_COL_SEQ").toString(), Integer.parseInt(mapMap.get("TARGET_COL_SEQ").toString()));
					
					// 取得『受理編號1』、『保單號碼2』、『中文專案名稱3』、『保單狀態44』所在欄位
					String targetColSeq = mapMap.get("TARGET_COL_SEQ") != null ? mapMap.get("TARGET_COL_SEQ").toString() : "";
					if (targetColSeq.equals("1")) {
						acceptid_s = mapMap.get("SOUCE_COL_SEQ").toString();
					} else if (targetColSeq.equals("2")) {
						policy_nbr_s = mapMap.get("SOUCE_COL_SEQ").toString();
					} else if (targetColSeq.equals("3")) {
						policy_name_s = mapMap.get("SOUCE_COL_SEQ").toString();
					} else if (targetColSeq.equals("44")) {
						soucr_status_s = mapMap.get("SOUCE_COL_SEQ").toString();
					}
				}
				
				// 判斷狀態的中文是否存在
				qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sb = new StringBuffer();
				sb.append(" SELECT * FROM TBJSB_INS_STATUS ");
				qc.setQueryString(sb.toString());
				List<Map<String, Object>> statusList = dam.exeQuery(qc);
				
				// 取得 TBJSB_AST_INS_MAST_SG01 VARCHAR2 欄位長度 & 是 NUMBER 的欄位
				qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sb = new StringBuffer();
				sb.append(" SELECT A.COLNO, A.COLTYPE, A.WIDTH, B.COMMENTS FROM SYS.COL A ");
				sb.append(" LEFT JOIN ALL_COL_COMMENTS B ON A.TNAME = B.TABLE_NAME ");
				sb.append(" AND A.CNAME = B.COLUMN_NAME ");
				sb.append(" WHERE A.TNAME = 'TBJSB_AST_INS_MAST_SG02' ");
				sb.append(" AND (A.COLTYPE = 'VARCHAR2' OR A.COLTYPE = 'NUMBER') ");
				qc.setQueryString(sb.toString());
				List<Map<String, Object>> widthList = dam.exeQuery(qc);
				
				// 每次上傳都將『TBJSB_AST_INS_MAST_SG02』、『TBJSB_INS_IMP_LOG』的相對應舊資料刪除
				qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sb = new StringBuffer();
				sb.append(" DELETE FROM TBJSB_AST_INS_MAST_SG02 WHERE INS_COM_ID = :ins_com_id ");
				qc.setObject("ins_com_id", ins_com_id);
				qc.setQueryString(sb.toString());
				dam.exeUpdate(qc);
				
				qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sb = new StringBuffer();
				sb.append(" DELETE FROM TBJSB_INS_IMP_LOG WHERE DATA_DATE = :data_date ");
				sb.append(" AND INS_COM_ID = :ins_com_id AND INS_TYPE = 'AST2' ");
				sb.append(" AND IMP_TYPE = 'JSB_AST_02' ");
				qc.setObject("data_date", data_date);
				qc.setObject("ins_com_id", ins_com_id);
				qc.setQueryString(sb.toString());
				dam.exeUpdate(qc);
				
				//JDBC
				GenFileTools gft = new GenFileTools();
				Connection conn = gft.getConnection();
				conn.setAutoCommit(false);
				
				sb = new StringBuffer();
				sb.append(" INSERT INTO TBJSB_AST_INS_MAST_SG02 ( ");
				sb.append(" CASE_NO, POLICY_NBR, POLICY_NAME, POLICY_ACTIVE_DATE, CUST_ID, ");
				sb.append(" CUST_NAME, INS_ID, INS_NAME, CRCY_TYPE, ACUM_PAID_POLICY_FEE, ");
				sb.append(" WDRAW_AMT, COMPANY_FEE, LIFE_FEE, OTHER_FEE, DIVI_AMT, ");
				sb.append(" TERMI_YEAR, TERMI_FEE, TERMI_DATE, ACC_VALUE, ACC_VALUE_DATE, ");
				sb.append(" CONTRACT_STATUS, TERMI_AMT, DEAD_AMT, DATA_DATE, BRANCH_NAME, ");
				sb.append(" SALES_ID, SALES_NAME, SERVICE_ID, CHANNEL, DEAD_AMT_TYPE, ");
				sb.append(" INS_CUST_POLICY_AGE, INS_CUST_CURR_AGE, ADD_PERCENT, POLICY_ASSURE_AMT, ACC_VALUE_01, ");
				sb.append(" ACC_VALUE_02, ACC_VALUE_03, ACC_VALUE_04, ACC_VALUE_05, ACC_VALUE_06, ");
				sb.append(" RTN_RATE, R_RATE, TOT_TERMI_FEE, STATUS_DESC, INS_COM_ID, UPDATE_STATUS, ");
				sb.append(" VERSION, CREATETIME, CREATOR, MODIFIER, LASTUPDATE ");
				sb.append(" ) VALUES ( ");
				sb.append(" ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ");
				sb.append(" ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ");
				sb.append(" ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ");
				sb.append(" ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ");
				sb.append(" ?, ?, ?, ?, ?, 'IM', ");
				sb.append(" 0, SYSDATE, ?, ?, SYSDATE) ");
				
				PreparedStatement pstmt = conn.prepareStatement(sb.toString());
				
				int imp_row_count = 0;		// 上傳成功總筆數
				int fail_row_count = 0;		// 錯誤保單(加總E01)
				int flag = 0;				// 資料上傳『新增』成功筆數
				
				// for取第1筆資料的『保價基準日』
				Date acc_value_date = null;
				
				Map<String, Object> policyNbrMap = this.getMastMap(inputVO.getIns_com_id());
				
				for (int i = 1; i < resultList.size(); i++) {
					String policy_nbr = resultList.get(i).get(policy_nbr_s);		// 保單號碼
					String acceptid = resultList.get(i).get(acceptid_s);			// 受理編號
					String soucr_status = resultList.get(i).get(soucr_status_s);	// 保單狀態(中文)
					String policy_name = resultList.get(i).get(policy_name_s);		// 中文專案名稱(保單名稱)
					
					/**
					 * E01: 上傳保單號碼空白
					 * W01: 判斷該筆資料保單狀態是否與『TBJSB_INS_INS_MAST』保單狀態欄位不相同
					 * **/
					String contract_status = "";	// for取得：保單狀態(代碼)，存入 TBJSB_AST_INS_MAST_SG02.CONTRACT_STATUS
					for (Map<String, Object> statusMap : statusList) {
						if (statusMap.get("SOURCE_STATUS").toString().trim().equals(soucr_status.trim())) {
							// CONTRACT_STATUS：需使用 TBJSB_INS_STATUS 進行轉換，取出CONTRACT_STUATS
							contract_status = statusMap.get("CONTRACT_STATUS").toString();
							break;
						}
					}
					
					if (StringUtils.isBlank(policy_nbr)) {
						// E01: 保單號碼空白
						// 參數：inputVO、錯誤代碼、受理編號、保單號碼、投保商品、保單狀態(中文)
						this.updateLog(inputVO, "E01", acceptid, policy_nbr, policy_name, soucr_status);
						fail_row_count++;
						continue;
					}
					
					if (policyNbrMap == null || policyNbrMap.size() == 0 || !policyNbrMap.containsKey(policy_nbr)) {
						// E07：保單檔無此保單號碼
						this.updateLog(inputVO, "E07", acceptid, policy_nbr, policy_name, soucr_status);
						fail_row_count++;
						continue;
					}
					
					// 將上傳資料放至相對應的位置
					String[] data = new String[44];
					for (String key : resultList.get(i).keySet()){
						if (null != columMap.get(key)) {
							int tcs = columMap.get(key);
							String val = resultList.get(i).get(key);
							data[tcs-1] = val;
							
							// 檢核上傳資料是否超出DB欄位大小
							if (StringUtils.isNotBlank(val)) {
								for (Map<String, Object> widthMap : widthList) {
									if (Integer.parseInt(widthMap.get("COLNO").toString()) == tcs) {
										if (widthMap.get("COLTYPE").toString().equals("VARCHAR2")) {
											// 檢核 VARCHAR2
											int valWidth = val.getBytes().length;
											if (valWidth > Integer.parseInt(widthMap.get("WIDTH").toString())) {
												String comments = widthMap.get("COMMENTS").toString();
												throw new JBranchException("第 " + i + " 筆資料欄位與『" + comments + "』定義不符");
											}
											if (val.length() > 2) {
												String s = val.substring(val.length()-2);
												if (".0".equals(s)) {
													data[tcs-1] = val.substring(0, val.length()-2);
												}
											}
										} else {
											// 檢核 NUMBER
											if (val.contains("%") || val.contains(",")) {
												// 防止報酬率等資料帶"%"或資料帶","
												val = val.replace("%", "").replace(",", "");
												data[tcs-1] = val;
											}
											boolean isNumeric = val.matches("[+-]?\\d*(\\.\\d+)?");
											if (!isNumeric) {
												String comments = widthMap.get("COMMENTS").toString();
												throw new JBranchException("第 " + i + " 筆資料欄位與『" + comments + "』定義不符");
											}
											if ("-".equals(val.trim())) {
												data[tcs-1] = null;
											}
										}
									}
								}
							}
						}
					}
					
					// 新增
					for (int d = 0; d < data.length; d++) {
						Date date = null;
						int colNbr = d + 1;
						if (colNbr == 1 || colNbr == 2) {
							// 處理受理編號、保單號碼
							String caseNo = data[d];
							if (StringUtils.isNotBlank(caseNo) && caseNo.contains("E")) {
				            	BigDecimal nbr = new BigDecimal(caseNo);
				            	data[d] = nbr.toPlainString();
				            }
							pstmt.setObject(colNbr, data[d]);
						
						} else if (colNbr == 21) {
							// 保單狀態（代碼）
							pstmt.setObject(colNbr, contract_status);
							
						} else if (colNbr == 4 || colNbr == 18 || colNbr == 20 || colNbr == 24) {
							// 處理日期格式
							if (StringUtils.isNotBlank(data[d])) {
								// 日期欄位可能會帶0，需另外判斷
								if ("0.0".equals(data[d]) || "0".equals(data[d]) || "-".equals(data[d].trim())) {
									pstmt.setObject(colNbr, null);
									
								} else {
									if (data[d].length() == 8) {
										try {
											if (Integer.parseInt(data[d]) == 0) {
												pstmt.setObject(colNbr, null);
											} else {
												date = sdf.parse(data[d]);													
											}
										} catch (Exception e) {
											throw new JBranchException("第 " + i + " 筆資料欄位與日期'YYYYMMDD'定義不符");
										}
										if (date != null) {
											pstmt.setObject(colNbr, new Timestamp(date.getTime()));												
										} else {
											pstmt.setObject(colNbr, null);											
										}
									} else {
										throw new JBranchException("第 " + i + " 筆資料欄位與日期'YYYYMMDD'定義不符");
									}
								}
								// 取第1筆有『保價基準日』的資料
								if (acc_value_date == null && colNbr == 20 && date != null) {
									acc_value_date = date;
								}
							} else {
								pstmt.setObject(colNbr, data[d]);
							}
						} else {
							pstmt.setObject(colNbr, data[d]);							
						}
					}
					pstmt.setObject(45, ins_com_id);		// INS_COM_ID ==> 使用者選擇的『保險公司』
					pstmt.setObject(46, loginID);
					pstmt.setObject(47, loginID);
					pstmt.addBatch();
					
					flag++;
					if (flag % 1000 == 0) {
						pstmt.executeBatch();
						conn.commit();
					}
				}
				pstmt.executeBatch();
				conn.commit();	
				
				// 第一金137的保價基準日若為0則改為同有效件的基準日 
				if ("137".equals(ins_com_id)) {
					qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
					sb = new StringBuffer();
					sb.append(" UPDATE TBJSB_AST_INS_MAST_SG02 SET ACC_VALUE_DATE = ( ");
					sb.append(" SELECT DISTINCT ACC_VALUE_DATE FROM TBJSB_AST_INS_MAST_SG02 ");
					sb.append(" WHERE INS_COM_ID = :ins_com_id AND ACC_VALUE_DATE IS NOT NULL ");
					sb.append(" ) WHERE INS_COM_ID = :ins_com_id AND ACC_VALUE_DATE IS NULL ");
					
					qc.setObject("ins_com_id", ins_com_id);
					qc.setQueryString(sb.toString());
					int delNbr = dam.exeUpdate(qc);
				}
				
				// 傳統型不匯入匯整檔 ==> 只匯入投資型
				qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sb = new StringBuffer();
				sb.append(" DELETE FROM TBJSB_AST_INS_MAST_SG02 SR WHERE SR.INS_COM_ID = :ins_com_id ");
				sb.append(" AND EXISTS (SELECT 'X' FROM TBJSB_AST_INS_MAST_SG TG ");
				sb.append(" WHERE TG.POLICY_NBR = SR.POLICY_NBR AND NVL(TG.SP_POLICY_NOTE, 'T') <> 'U') ");
				
				qc.setObject("ins_com_id", ins_com_id);
				qc.setQueryString(sb.toString());
				int delNbr = dam.exeUpdate(qc);
				imp_row_count = flag-delNbr;	// 上傳成功總筆數
				
				// 傳統型不匯入匯整檔 ==> 只匯入投資型，故連錯誤保單中的傳統型也一併移除
				// 錯誤保單中有『保單檔無此保單號碼』所以 TBJSB_INS_IMP_LOG 用 POLICY_NAME 來排除
				qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sb = new StringBuffer();
				sb.append(" DELETE FROM TBJSB_INS_IMP_LOG SR WHERE SR.IMP_TYPE = 'JSB_AST_02' ");
				sb.append(" AND SR.INS_COM_ID = :ins_com_id AND EXISTS (SELECT 'X' FROM TBJSB_AST_INS_MAST_SG TG ");
				sb.append(" WHERE TG.POLICY_FULL_NAME = SR.POLICY_NAME AND NVL(TG.SP_POLICY_NOTE, 'T') <> 'U') ");
				
				qc.setObject("ins_com_id", ins_com_id);
				qc.setQueryString(sb.toString());
				int delFail = dam.exeUpdate(qc);
				fail_row_count = fail_row_count-delFail;
				
				// INSERT 寫入『TBJSB_INS_IMP』每個『保險公司+商品類型』只留最新一筆，故每次INSERT前都先將舊的資料刪除
				// imp_row_count	上傳成功總筆數
				// fail_row_count	錯誤保單 				(加總E01~E04)
				// status_row_count	狀態異動總筆數			(加總W01)
				// status_ok_count	符合轉入契約狀況保單數	(加總非E04)
				// status_no_count	不符合轉入契約狀況保單數	(加總E04)
				qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sb = new StringBuffer();
				sb.append(" DELETE FROM TBJSB_INS_IMP WHERE INS_COM_ID = :ins_com_id AND IMP_TYPE = 'JSB_AST_02' ");
				
				qc.setObject("ins_com_id", ins_com_id);
				qc.setQueryString(sb.toString());
				dam.exeUpdate(qc);
				
				qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sb = new StringBuffer();
				sb.append(" INSERT INTO TBJSB_INS_IMP ( ");
				sb.append(" DATA_DATE, INS_COM_ID, INS_TYPE, IMP_TYPE, ");
				sb.append(" IMP_ROW_COUNT, FAIL_ROW_COUNT, ");
				if (null != acc_value_date) {
					sb.append(" ACC_VALUE_DATE, ");
				}
				sb.append(" VERSION, CREATETIME, CREATOR, MODIFIER, LASTUPDATE ");
				sb.append(" ) VALUES ( ");
				sb.append(" :data_date, :ins_com_id, 'AST2', 'JSB_AST_02', ");
				sb.append(" :imp_row_count, :fail_row_count, ");
				if (null != acc_value_date) {
					sb.append(" :acc_value_date, ");
				}
				sb.append(" 0, SYSDATE, :loginID, :loginID, SYSDATE) ");
				
				qc.setObject("data_date", data_date);
				qc.setObject("ins_com_id", Integer.parseInt(ins_com_id));
				qc.setObject("imp_row_count", imp_row_count);
				qc.setObject("fail_row_count", fail_row_count);
				// 保價基準日取第1筆資料
				if (null != acc_value_date) {
					qc.setObject("acc_value_date", new Timestamp(acc_value_date.getTime()));					
				}
				qc.setObject("loginID", loginID);
				qc.setQueryString(sb.toString());
				dam.exeUpdate(qc);
				
				Map<String, Object> resultMap = new HashMap<>();
				// 保險公司	(前端)	
				// 參考日期	(前端)轉格式	
				// 上傳日期	
				// 轉入保單數
				// 錯誤保單數
				// 總計保單數
				// 保價基準日
				resultMap.put("DATA_DATE", inputVO.getData_date());
				resultMap.put("TODAY", new Date());
				resultMap.put("IMP_ROW_COUNT", imp_row_count);
				resultMap.put("FAIL_ROW_COUNT", fail_row_count);
				resultMap.put("TOTAL_COUNT", fail_row_count + imp_row_count);
//				resultMap.put("ACC_VALUE_DATE", acc_value_date);
				outputList.add(resultMap);
			}
		} else {
			throw new APException("上傳檔案欄位與定義不符");
		}
		outputVO.setResultList(outputList);
		this.sendRtnObject(outputVO);
	}
	
	public void goMerge (Object body, IPrimitiveMap header) throws Exception {
		JSB120InputVO inputVO = (JSB120InputVO) body;
		String loginID = (String) getUserVariable(FubonSystemVariableConsts.LOGINID);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String ins_com_id = inputVO.getIns_com_id();
		int update_row_count = 0;
		
		dam = this.getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		// STEP1：更新至主檔(TBJSB_AST_INS_MAST)，以MERGE INTO 方式寫入
		sb.append(" MERGE INTO TBJSB_AST_INS_MAST_SG TG USING ( ");
		sb.append(" SELECT A.*, B.FUBON_CONTRACT_STATUS, B.CONTRACT_STATUS_DESCR ");
		sb.append(" FROM TBJSB_AST_INS_MAST_SG02 A LEFT JOIN ( ");
		sb.append(" SELECT DISTINCT CONTRACT_STATUS, FUBON_CONTRACT_STATUS, CONTRACT_STATUS_DESCR ");
		sb.append(" FROM TBJSB_INS_STATUS) B ON A.CONTRACT_STATUS = B.CONTRACT_STATUS ");
		sb.append(" WHERE INS_COM_ID = :ins_com_id AND UPDATE_STATUS = 'IM' ");
		// 加上法巴規則：來檔未提供解約日期則不更新 
		sb.append(" AND NOT(INS_COM_ID = 114 AND RTRIM(STATUS_DESC) = '保戶主動解約' AND TERMI_DATE IS NULL) ");
		// 只更新投資型
		sb.append(" ) SR ON (TG.POLICY_NBR = SR.POLICY_NBR AND TG.MAST_SLA_TYPE = 'Y' AND TG.SP_POLICY_NOTE = 'U') ");
		sb.append(" WHEN MATCHED THEN ");
		sb.append(" UPDATE SET ");
		sb.append(" TG.CUST_ID   = CASE WHEN NVL(SR.CUST_ID  , 'X') <> 'X' AND LENGTH(TG.CUST_ID) <> 11 THEN SR.CUST_ID ELSE TG.CUST_ID END, ");
		sb.append(" TG.APPL_NAME = CASE WHEN NVL(SR.CUST_NAME, 'X') <> 'X' THEN SR.CUST_NAME ELSE TG.APPL_NAME END, ");
		sb.append(" TG.INS_ID 	 = CASE WHEN NVL(SR.INS_ID	 , 'X') <> 'X' THEN SR.INS_ID 	 ELSE TG.INS_ID    END, ");
		sb.append(" TG.INS_NAME  = CASE WHEN NVL(SR.INS_NAME , 'X') <> 'X' THEN SR.INS_NAME  ELSE TG.INS_NAME  END, ");
		
		if ("114".equals(ins_com_id)) {
			// 只更新法巴商品名稱
			sb.append(" TG.POLICY_FULL_NAME = CASE WHEN TG.POLICY_FULL_NAME <> SR.POLICY_NAME AND SR.POLICY_NAME IS NOT NULL THEN SR.POLICY_NAME ELSE TG.POLICY_FULL_NAME END, ");
			// 上傳法巴損益檔如有異動到 POLICY_FULL_NAME，則寫死 INS_TYPE、POLICY_SIMP_NAME、PRD_ID
			sb.append(" TG.INS_TYPE 		= CASE WHEN TG.POLICY_FULL_NAME <> SR.POLICY_NAME AND SR.POLICY_NAME IS NOT NULL THEN 'CDF2016002' ELSE TG.INS_TYPE END, ");
			sb.append(" TG.POLICY_SIMP_NAME = CASE WHEN TG.POLICY_FULL_NAME <> SR.POLICY_NAME AND SR.POLICY_NAME IS NOT NULL THEN '法巴-元氣滿滿' ELSE TG.POLICY_SIMP_NAME END, ");
			sb.append(" TG.PRD_ID 			= CASE WHEN TG.POLICY_FULL_NAME <> SR.POLICY_NAME AND SR.POLICY_NAME IS NOT NULL THEN 'CDF2016002' ELSE TG.PRD_ID END, ");
		}
		sb.append(" TG.ACUM_INS_AMT_ORGD = CASE WHEN SR.ACUM_PAID_POLICY_FEE IS NOT NULL THEN SR.ACUM_PAID_POLICY_FEE ELSE TG.ACUM_INS_AMT_ORGD END, ");
		sb.append(" TG.ACUM_PAID_POLICY_FEE = CASE WHEN SR.ACUM_PAID_POLICY_FEE IS NOT NULL THEN SR.ACUM_PAID_POLICY_FEE ELSE TG.ACUM_PAID_POLICY_FEE END, ");
		sb.append(" TG.ACCU_PREM = CASE WHEN SR.ACUM_PAID_POLICY_FEE IS NOT NULL THEN SR.ACUM_PAID_POLICY_FEE ELSE TG.ACCU_PREM END, ");
		sb.append(" TG.ACUM_WDRAW_POLICY_FEE = CASE WHEN SR.WDRAW_AMT IS NULL THEN SR.WDRAW_AMT ELSE TG.ACUM_WDRAW_POLICY_FEE END, ");
		sb.append(" TG.CONTRACT_DATE = CASE WHEN (TG.CONTRACT_STATUS <> FUBON_CONTRACT_STATUS AND SR.TERMI_DATE IS NOT NULL) THEN SR.TERMI_DATE ");
		sb.append(" WHEN (TG.CONTRACT_STATUS <> FUBON_CONTRACT_STATUS AND SR.TERMI_DATE IS NULL) THEN :data_date ELSE TG.CONTRACT_DATE END, ");
		sb.append(" TG.ACC_VALUE = CASE WHEN TG.SP_POLICY_NOTE = 'U' THEN SR.ACC_VALUE ELSE TG.ACC_VALUE END, ");
		sb.append(" TG.CAL_VALUE_YEAR = TO_CHAR(SR.ACC_VALUE_DATE,'YYYY'), ");
		sb.append(" TG.CAL_VALUE_MONTH = TO_CHAR(SR.ACC_VALUE_DATE,'MM'), ");
		sb.append(" TG.CAL_VALUE_DAY = TO_CHAR(SR.ACC_VALUE_DATE,'DD'), ");
		sb.append(" TG.CONTRACT_STATUS = SR.FUBON_CONTRACT_STATUS, ");
		sb.append(" TG.CONTRACT_TEXT = CASE WHEN SR.CONTRACT_STATUS_DESCR IS NOT NULL THEN SR.CONTRACT_STATUS_DESCR ELSE TG.CONTRACT_TEXT END, ");
		sb.append(" TG.TERMI_AMT = CASE WHEN SR.TERMI_AMT IS NOT NULL THEN SR.TERMI_AMT ELSE TG.TERMI_AMT END, ");
		sb.append(" TG.POLICY_ASSURE_AMT = CASE WHEN SR.POLICY_ASSURE_AMT IS NOT NULL THEN SR.POLICY_ASSURE_AMT ELSE TG.POLICY_ASSURE_AMT END, ");
//		sb.append(" TG.ACUM_INS_AMT_ORGD = SR.ACUM_PAID_POLICY_FEE, ");
//		sb.append(" TG.ACUM_PAID_POLICY_FEE = SR.ACUM_PAID_POLICY_FEE, ");
//		sb.append(" TG.ACCU_PREM = SR.ACUM_PAID_POLICY_FEE, ");
//		sb.append(" TG.ACUM_WDRAW_POLICY_FEE = SR.WDRAW_AMT, ");
//		sb.append(" TG.CONTRACT_DATE = SR.TERMI_DATE, ");
//		sb.append(" TG.ACC_VALUE = CASE WHEN TG.SP_POLICY_NOTE = 'U' THEN SR.ACC_VALUE ELSE TG.ACC_VALUE END, ");
//		sb.append(" TG.CAL_VALUE_YEAR = TO_CHAR(SR.ACC_VALUE_DATE,'YYYY'), ");
//		sb.append(" TG.CAL_VALUE_MONTH = TO_CHAR(SR.ACC_VALUE_DATE,'MM'), ");
//		sb.append(" TG.CAL_VALUE_DAY = TO_CHAR(SR.ACC_VALUE_DATE,'DD'), ");
//		sb.append(" TG.CONTRACT_STATUS = SR.FUBON_CONTRACT_STATUS, ");
//		sb.append(" TG.CONTRACT_TEXT = SR.CONTRACT_STATUS_DESCR, ");
//		sb.append(" TG.TERMI_AMT = SR.TERMI_AMT, ");
//		sb.append(" TG.POLICY_ASSURE_AMT = SR.POLICY_ASSURE_AMT, ");
		
		sb.append(" TG.LASTUPDATE = SYSDATE, ");
		sb.append(" TG.MODIFIER = :loginID ");
		
		Timestamp data_date = new Timestamp(inputVO.getData_date().getTime());
		qc.setObject("data_date" , data_date);
		qc.setObject("ins_com_id", ins_com_id);
		qc.setObject("loginID", loginID);
		qc.setQueryString(sb.toString());
		System.out.println(sb.toString());
		update_row_count = dam.exeUpdate(qc);
		
		// STEP2：更新 TBJSB_AST_INS_MAST_SG02.UPDAT_STATUS = 'UP'
		qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sb = new StringBuffer();
		sb.append(" UPDATE TBJSB_AST_INS_MAST_SG02 SET UPDATE_STATUS = 'UP' WHERE INS_COM_ID = :ins_com_id ");
		qc.setObject("ins_com_id", ins_com_id);
		qc.setQueryString(sb.toString());
		dam.exeUpdate(qc);
		
		// STEP3：更新 TBJSB_INS_IMP.UPDATE_ROW_COUNT	
		qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sb = new StringBuffer();
		sb.append(" UPDATE TBJSB_INS_IMP SET UPDATE_ROW_COUNT = :update_row_count, ");
		sb.append(" VERSION = VERSION+1, MODIFIER = :loginID, LASTUPDATE = SYSDATE ");
		sb.append(" WHERE INS_COM_ID = :ins_com_id AND INS_TYPE = 'AST2' AND IMP_TYPE = 'JSB_AST_02' ");
		
		qc.setObject("update_row_count", update_row_count);
		qc.setObject("loginID", loginID);
		qc.setObject("ins_com_id", ins_com_id);
		
		qc.setQueryString(sb.toString());
		dam.exeUpdate(qc);
		
		this.sendRtnObject(null);
	}
	
	private String formatStr(Object column) {
        return "=\"" + ObjectUtils.defaultIfNull(column, "") + "\"";
    }
	
	public void export (Object body, IPrimitiveMap header) throws Exception {
		JSB120InputVO inputVO = (JSB120InputVO) body;
		String exportType = inputVO.getExport_type();
		String fileName = "";     
		List<Map<String, Object>> resultList = new ArrayList<>();
		List listCSV = new ArrayList();
		CSVUtil csv = new CSVUtil();
		
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> comNameMap = xmlInfo.doGetVariable("JSB.INS_COM_NAME_02", FormatHelper.FORMAT_3);
		
		if ("OK".equals(exportType)) {
			// 轉入保單數
			if (StringUtils.isNotBlank(inputVO.getIns_com_id())) {
				String comName = comNameMap.get(inputVO.getIns_com_id());
				fileName = comName + "保險公司上傳保單檔彙整檔資料";
				
			} else {
				fileName = "全部上傳保單檔彙整檔資料";
			}
			resultList = this.getOK(inputVO);
			
			for (Map<String, Object> map : resultList) {
	            String[] records = new String[15];
	            int i = 0;
	            
	            records[i]   = formatStr(map.get("POLICY_NBR"));			// 保單號碼				POLICY_NBR
	            records[++i] = formatStr(map.get("POLICY_ACTIVE_DATE"));	// 生效日期				POLICY_ACTIVE_DATE
	            records[++i] = formatStr(map.get("CUST_ID"));				// 要保人ID				CUST_ID
	            records[++i] = formatStr(map.get("CUST_NAME"));				// 要保人姓名				CUST_NAME
	            records[++i] = formatStr(map.get("INS_ID"));				// 被保人ID				INS_ID
	            records[++i] = formatStr(map.get("INS_NAME"));				// 被保人姓名				INS_NAME
	            records[++i] = formatStr(map.get("POLICY_NAME"));			// 投保商品				POLICY_NAME
	            records[++i] = formatStr(map.get("ACUM_PAID_POLICY_FEE"));	// 累積所繳保費(原幣別)		ACUM_PAID_POLICY_FEE
	            records[++i] = formatStr(map.get("WDRAW_AMT"));				// 累積部提金額(原幣別)		WDRAW_AMT
	            records[++i] = formatStr(map.get("TERMI_DATE"));			// 解約日期				TERMI_DATE
	            records[++i] = formatStr(map.get("ACC_VALUE"));				// 保單價值(原幣別)		ACC_VALUE
	            records[++i] = formatStr(map.get("ACC_VALUE_DATE"));		// 保單價值基準日			ACC_VALUE_DATE
	            records[++i] = formatStr(map.get("CONTRACT_STATUS_DESCR"));	// 保單狀況				CONTRACT_STATUS_DESCR
	            records[++i] = formatStr(map.get("TERMI_AMT"));				// 解約金(原幣別)			TERMI_AMT
	            records[++i] = formatStr(map.get("POLICY_ASSURE_AMT"));		// 保額					POLICY_ASSURE_AMT
	            
	            listCSV.add(records);
	        }
			csv.setHeader(new String[]{"保單號碼", "生效日期", "要保人ID", "要保人姓名", "被保人ID", 
				  					   "被保人姓名", "投保商品", "累積所繳保費(原幣別)", "累積部提金額(原幣別)", "解約日期", 
				  					   "保單價值(原幣別)", "保單價值基準日", "保單狀況", "解約金(原幣別)", "保額"});
		} else {
			// 錯誤保單數
			fileName = "錯誤保單";
			resultList = this.getError(inputVO, "E");
			
			Map<String, String> errorTypeMap = xmlInfo.doGetVariable("JSB.IMP_ERROR_TYPE", FormatHelper.FORMAT_3);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
			for (Map<String, Object> map : resultList) {
				String[] records = new String[7];
	            int i = 0;
	            String data_date = "";
	            if (null != map.get("DATA_DATE")) {
	            	Date dataDate = (Date) map.get("DATA_DATE");
	            	data_date = sdf.format(dataDate);
	            }
	            // DATA_DATE	參考日期
	            records[i] = formatStr(data_date);
	            
	            // INS_COM_ID	保險公司
	            String ins_com_id = map.get("INS_COM_ID") != null ? map.get("INS_COM_ID").toString() : "";
	            records[++i] = formatStr(comNameMap.get(ins_com_id));
	            
	            records[++i] = "損益檔";
//	            records[++i] = formatStr(map.get("IMP_TYPE"));		// IMP_TYPE		上傳檔案
	            
	            String error_type = map.get("ERROR_TYPE") != null ? map.get("ERROR_TYPE").toString() : "";
	            records[++i] = formatStr(errorTypeMap.get(error_type));
//	            records[++i] = formatStr(map.get("ERROR_TYPE"));	// ERROR_TYPE	錯誤代碼
	            
	            records[++i] = formatStr(map.get("CASE_NO"));		// CASE_NO		受理編號
	            records[++i] = formatStr(map.get("POLICY_NBR"));	// POLICY_NBR	保單號碼
	            records[++i] = formatStr(map.get("POLICY_NAME"));	// POLICY_NAME	投保商品
	            listCSV.add(records);
			}
			csv.setHeader(new String[]{"參考日期", "保險公司", "上傳檔案", "錯誤代碼", "受理編號", "保單號碼", "投保商品"});
		}
		csv.addRecordList(listCSV);
        String url = csv.generateCSV();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        fileName = fileName + "_" + sdf.format(new Date())+ ".csv";
        notifyClientToDownloadFile(url, fileName);
        this.sendRtnObject(null);
	}
	
	public void exportUP (Object body, IPrimitiveMap header) throws Exception {
		JSB120InputVO inputVO = (JSB120InputVO) body;
		List listCSV = new ArrayList();
		CSVUtil csv = new CSVUtil();
		String fileName = "";
		
		dam = this.getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		sb.append(" SELECT POLICY_NBR, CUST_ID, APPL_NAME, INS_ID, INS_NAME, POLICY_FULL_NAME, ACUM_INS_AMT_ORGD, ");
		sb.append(" ACUM_WDRAW_POLICY_FEE, ACC_VALUE, CONTRACT_TEXT, TERMI_AMT, POLICY_ASSURE_AMT, POLICY_ACTIVE_DATE, CONTRACT_DATE, ");
		sb.append(" CAL_VALUE_YEAR || '/' || LPAD(CAL_VALUE_MONTH, 2, '0') || '/' || LPAD(CAL_VALUE_DAY, 2, '0') AS ACC_VALUE_DATE ");
		sb.append(" FROM TBJSB_AST_INS_MAST_SG WHERE 1 = 1 ");
		sb.append(" AND NVL(SP_POLICY_NOTE, 'T') = 'U' ");		// 僅會上傳投資型，故需篩選出投資型保單
		
		if (StringUtils.isNotBlank(inputVO.getIns_com_id())) {
			XmlInfo xmlInfo = new XmlInfo();
			Map<String, String> comNameMap = xmlInfo.doGetVariable("JSB.INS_COM_NAME_02", FormatHelper.FORMAT_3);
			String comName = comNameMap.get(inputVO.getIns_com_id());
			fileName = comName + "保險公司更新保單資料";
			
			sb.append(" AND COM_ID = :com_id ");			
			qc.setObject("com_id", inputVO.getIns_com_id());

			// 帶入『保單價值基準日』ACC_VALUE_DATE
			if (null != inputVO.getAcc_value_date()) {
				sb.append(" AND CAL_VALUE_YEAR || LPAD(CAL_VALUE_MONTH, 2, '0') || LPAD(CAL_VALUE_DAY, 2, '0') = TO_CHAR(:acc_value_date, 'YYYYMMDD') ");
				qc.setObject("acc_value_date", new Timestamp(inputVO.getAcc_value_date().getTime()));
			}
		} else {
			fileName = "全部更新保單檔資料";
			
			// 用保價參考日時間匯出當月的
			sb.append(" AND CAL_VALUE_YEAR || LPAD(CAL_VALUE_MONTH, 2, '0') = TO_CHAR(SYSDATE, 'YYYYMM') ");
		}
		qc.setQueryString(sb.toString());
		List<Map<String, Object>> resultList = dam.exeQuery(qc);
		
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> comNameMap = xmlInfo.doGetVariable("JSB.INS_COM_NAME_02", FormatHelper.FORMAT_3);
		
		for (Map<String, Object> map : resultList) {
            String[] records = new String[15];
            int i = 0;
            
            records[i]   = formatStr(map.get("POLICY_NBR"));			// 保單號碼				POLICY_NBR
            records[++i] = formatStr(map.get("POLICY_ACTIVE_DATE"));	// 生效日期				POLICY_ACTIVE_DATE
            records[++i] = formatStr(map.get("CUST_ID"));				// 要保人ID				CUST_ID
            records[++i] = formatStr(map.get("APPL_NAME"));				// 要保人姓名				APPL_NAME
            records[++i] = formatStr(map.get("INS_ID"));				// 被保人ID				INS_ID
            records[++i] = formatStr(map.get("INS_NAME"));				// 被保人姓名				INS_NAME
            records[++i] = formatStr(map.get("POLICY_FULL_NAME"));		// 投保商品				POLICY_FULL_NAME
            records[++i] = formatStr(map.get("ACUM_INS_AMT_ORGD"));		// 累積所繳保費(原幣別)		ACUM_INS_AMT_ORGD
            records[++i] = formatStr(map.get("ACUM_WDRAW_POLICY_FEE"));	// 累積部提金額(原幣別)		ACUM_WDRAW_POLICY_FEE
            records[++i] = formatStr(map.get("CONTRACT_DATE"));			// 解約日期				CONTRACT_DATE
            records[++i] = formatStr(map.get("ACC_VALUE"));				// 保單價值(原幣別)		ACC_VALUE
            records[++i] = formatStr(map.get("ACC_VALUE_DATE"));		// 保單價值基準日			CAL_VALUE_YEAR、CAL_VALUE_MONTH、CAL_VALUE_DAY
            records[++i] = formatStr(map.get("CONTRACT_TEXT"));			// 保單狀況				CONTRACT_TEXT
            records[++i] = formatStr(map.get("TERMI_AMT"));				// 解約金(原幣別)			TERMI_AMT
            records[++i] = formatStr(map.get("POLICY_ASSURE_AMT"));		// 保額					POLICY_ASSURE_AMT
            
            listCSV.add(records);
        }
		csv.setHeader(new String[]{"保單號碼", "生效日期", "要保人ID", "要保人姓名", "被保人ID", 
				   				   "被保人姓名", "投保商品", "累積所繳保費(原幣別)", "累積部提金額(原幣別)", "解約日期", 
				   				   "保單價值(原幣別)", "保單價值基準日", "保單狀況", "解約金(原幣別)", "保額"});
		
		csv.addRecordList(listCSV);
        String url = csv.generateCSV();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        fileName = fileName + "_" + sdf.format(new Date())+ ".csv";
        notifyClientToDownloadFile(url, fileName);
        this.sendRtnObject(null);
	}
	
	// 上傳後結果檢視，按取消，資料不會彙入匯整檔中，如當月有上傳過，將清空重新上傳
	public void cancel (Object body, IPrimitiveMap header) throws Exception {
		JSB120InputVO inputVO = (JSB120InputVO) body;
		String ins_com_id = inputVO.getIns_com_id();
		Timestamp data_date = new Timestamp(inputVO.getData_date().getTime());
		
		// 清空該保險公司在 TBJSB_INS_IMP、TBJSB_AST_INS_MAST_SG02、TBJSB_INS_IMP_LOG 的資料
		dam = this.getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		sb.append(" DELETE FROM TBJSB_INS_IMP WHERE INS_COM_ID = :ins_com_id AND IMP_TYPE = 'JSB_AST_02' ");
		qc.setObject("ins_com_id", ins_com_id);
		qc.setQueryString(sb.toString());
		dam.exeUpdate(qc);
		
		
		qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sb = new StringBuffer();
		sb.append(" DELETE FROM TBJSB_AST_INS_MAST_SG02 WHERE INS_COM_ID = :ins_com_id ");
		qc.setObject("ins_com_id", ins_com_id);
		qc.setQueryString(sb.toString());
		dam.exeUpdate(qc);
		
		qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sb = new StringBuffer();
		sb.append(" DELETE FROM TBJSB_INS_IMP_LOG WHERE DATA_DATE = :data_date ");
		sb.append(" AND INS_COM_ID = :ins_com_id AND INS_TYPE = 'AST2' ");
		sb.append(" AND IMP_TYPE = 'JSB_AST_02' ");
		qc.setObject("data_date", data_date);
		qc.setObject("ins_com_id", ins_com_id);
		qc.setQueryString(sb.toString());
		dam.exeUpdate(qc);
		
		this.sendRtnObject(null);
	}
	
	private List<Map<String, Object>> getOK(JSB120InputVO inputVO) throws JBranchException {
		List<Map<String, Object>> OKList = new ArrayList<>();
		dam = this.getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		sb.append(" SELECT POLICY_NBR, CUST_ID, CUST_NAME, INS_ID, INS_NAME, POLICY_NAME, ");
		sb.append(" ACUM_PAID_POLICY_FEE, WDRAW_AMT, ACC_VALUE, TERMI_AMT, POLICY_ASSURE_AMT, ");
		sb.append(" SG.CONTRACT_STATUS, STATUS_DESC, S.CONTRACT_STATUS_DESCR, ");
		sb.append(" TO_CHAR(POLICY_ACTIVE_DATE, 'YYYY/MM/DD') AS POLICY_ACTIVE_DATE, ");
		sb.append(" TO_CHAR(TERMI_DATE, 'YYYY/MM/DD') AS TERMI_DATE, ");
		sb.append(" TO_CHAR(ACC_VALUE_DATE, 'YYYY/MM/DD') AS ACC_VALUE_DATE ");
		sb.append(" FROM TBJSB_AST_INS_MAST_SG02 SG LEFT JOIN ( ");
		sb.append(" SELECT DISTINCT CONTRACT_STATUS, CONTRACT_STATUS_DESCR FROM TBJSB_INS_STATUS ");
		sb.append(" ) S ON SG.CONTRACT_STATUS = S.CONTRACT_STATUS WHERE 1 = 1 ");
		
		if (StringUtils.isNotBlank(inputVO.getIns_com_id())) {
			sb.append(" AND INS_COM_ID = :ins_com_id ");
			qc.setObject("ins_com_id", inputVO.getIns_com_id());
			
		} else {
			sb.append(" AND TO_CHAR(LASTUPDATE, 'YYYYMM') = TO_CHAR(SYSDATE, 'YYYYMM') ");
			sb.append(" ORDER BY INS_COM_ID ");
		}
		qc.setQueryString(sb.toString());
		OKList = dam.exeQuery(qc);
		return OKList;
	}
	
	private List<Map<String, Object>> getError(JSB120InputVO inputVO, String errType) throws JBranchException {
		List<Map<String, Object>> errorList = new ArrayList<>();
		dam = this.getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		sb.append(" SELECT * FROM TBJSB_INS_IMP_LOG WHERE DATA_DATE = :data_date ");
		sb.append(" AND INS_COM_ID = :ins_com_id AND INS_TYPE = 'AST2' ");
		sb.append(" AND IMP_TYPE = 'JSB_AST_02' ");
		
		if (StringUtils.isNotBlank(errType)) {
			if ("E".equals(errType)) {
				sb.append(" AND ERROR_TYPE <> 'W01' ");
			} else {
				sb.append(" AND ERROR_TYPE = :error_type ");
				qc.setObject("error_type", errType);
			}
		}
		qc.setObject("data_date", new Timestamp(inputVO.getData_date().getTime()));
		qc.setObject("ins_com_id", inputVO.getIns_com_id());
		qc.setQueryString(sb.toString());
		errorList = dam.exeQuery(qc);
		return errorList;
	}
	
	// 更新：TBJSB_INS_IMP_LOG（日盛保單匯入錯誤資訊檔）
	// 參數：inputVO、錯誤代碼、受理編號、保單號碼、投保商品、保單狀態(中文)
	private void updateLog(JSB120InputVO inputVO, String error_type, 
						   String case_no, String policy_nbr, String policy_name, String contract_status) throws JBranchException {
		
		String loginID = (String) getUserVariable(FubonSystemVariableConsts.LOGINID);
		dam = this.getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		sb.append(" INSERT INTO TBJSB_INS_IMP_LOG ( ");
		sb.append(" SEQ_NO, DATA_DATE, INS_COM_ID, INS_TYPE, IMP_TYPE, ");
		sb.append(" ERROR_TYPE, CASE_NO, POLICY_NBR, POLICY_NAME, CONTRACT_STATUS, ");
		sb.append(" VERSION, CREATETIME, CREATOR, MODIFIER, LASTUPDATE ");
		sb.append(" ) VALUES ( ");
		sb.append(" :seq_no, :data_date, :ins_com_id, 'AST2', 'JSB_AST_02', ");
		sb.append(" :error_type, :case_no, :policy_nbr, :policy_name, :contract_status, ");
		sb.append(" 0, SYSDATE, :loginID, :loginID, SYSDATE) ");
		
		qc.setObject("seq_no", this.getSN());
		qc.setObject("data_date", new Timestamp(inputVO.getData_date().getTime()));
		qc.setObject("ins_com_id", inputVO.getIns_com_id());
		qc.setObject("error_type", error_type);
		qc.setObject("case_no", case_no);
		qc.setObject("policy_nbr", policy_nbr);
		qc.setObject("policy_name", policy_name);
		qc.setObject("contract_status", contract_status);
		qc.setObject("loginID", loginID);
		
		qc.setQueryString(sb.toString());
		dam.exeUpdate(qc);
	}
	
	private Map<String, Object> getMastMap(String ins_com_id) throws JBranchException {
		Map<String, Object> mastMap = new HashMap<>();
		
		// 判斷該筆資料保單狀態是否與『TBJSB_INS_INS_MAST』保單狀態欄位是否相同
		dam = this.getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		
		sb.append(" SELECT DISTINCT M.POLICY_NBR ");
		sb.append(" FROM TBJSB_AST_INS_MAST_SG M WHERE M.MAST_SLA_TYPE = 'Y' ");
		sb.append(" AND COM_ID = :ins_com_id ");
		qc.setQueryString(sb.toString());
		qc.setObject("ins_com_id", ins_com_id);
		List<Map<String, Object>> mastList = dam.exeQuery(qc);
		
		if (null != mastList && mastList.size() > 0) {
			for (Map<String,Object> map : mastList) {
				mastMap.put(ObjectUtils.toString(map.get("POLICY_NBR")), map.get("POLICY_NBR"));				
			}
					
		}
		return mastMap;
	}
	
	/**
	 * 讀取CSV檔案格式
	 * 
	 * @param file
	 * @return List<Map<Integer,String>>
	 * @throws JBranchException
	 */
	public List<Map<String,String>> readCSV(List<String[]> dataCsv) throws JBranchException{
		List<Map<String,String>> resultList = new ArrayList<>();
		Map<String,String> map = null;
		if(!dataCsv.isEmpty()) {
			for (int i = 0; i < dataCsv.size(); i++) {
				map = new HashMap<String,String>();
				String[] str = dataCsv.get(i);				
				for (int v = 0; v < str.length; v++) {
					map.put(v + 1 + "", str[v]);
				}
				resultList.add(map);
			}
		}
		return resultList;
	}
	
	/**
	 * 讀取txt檔案格式
	 * 
	 * @param file
	 * @return List<Map<Integer,String>>
	 * @throws JBranchException
	 * @throws FileNotFoundException 
	 * @throws UnsupportedEncodingException 
	 */
//	public List<Map<String,String>> readTXT(File dataFile) throws Exception {
//		List<Map<String,String>> resultList = new ArrayList<>();
//		Map<String,String> map = null;
//		
//		FileInputStream fi = new FileInputStream(dataFile);
//		BufferedReader br = new BufferedReader(new InputStreamReader(fi, "big5"));
//		// 將輸入資料轉型成List<Map<[資料庫欄位名稱], [欄位值]>> 方便下一步驟執行
//		String[] head = br.readLine().split(",");
//		String line = null;
//		
//		while((line = br.readLine()) != null) {
//			String[] data = line.split(",");
//			HashMap<String, String> dataMap = new HashMap<String, String>();
//			for (int i = 0; i < data.length; i++) {
//				dataMap.put(i+1+"", data[i]);
//			}
//			resultList.add(dataMap);
//		}
//		return resultList;
//	}
	
	/**
	 * 讀取Excel檔案格式
	 * 
	 * @param file
	 * @return List<Map<Integer,String>>
	 * @throws JBranchException
	 * @throws IOException 
	 * @throws InvalidFormatException 
	 * @throws EncryptedDocumentException 
	 * @throws BiffException 
	 * @throws  
	 */
	public List<Map<String,String>> readXLS(File file) throws JBranchException, IOException, EncryptedDocumentException, InvalidFormatException{
		List<Map<String,String>> resultList = new ArrayList<>();
		Workbook wb = WorkbookFactory.create(file);
		Sheet sheet = wb.getSheetAt(0);
		Iterator<Row> rowIterator = sheet.iterator();
		Map<String,String> map = null;
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		int cellNum = 0;
		int i = 0;
		while (rowIterator.hasNext()) {
			map = new HashMap<String,String>();
			Row row = rowIterator.next();
			if (i == 0) {
				cellNum = row.getLastCellNum();
			}
			for (int j = 0; j < cellNum; j++) {
				try {
					map.put(j + 1 + "", getValue(row.getCell(j), j, wb, df, false));
				} catch (Exception e) {
					throw new JBranchException("第 " + i+1 + " 筆資料，第 " + j+1 + " 個欄位有誤。" );
				}
	        }
			resultList.add(map);			
			i++;
		}
		return resultList;
		
	}
	
	private String getValue(Cell cell, int index, Workbook book, DateFormat df, boolean isKey) throws IOException{
        // 空白或空
        if (cell == null || cell.getCellType()==Cell.CELL_TYPE_BLANK ) {
            if (isKey) {
            } else {
                return "";
            }
        }

        // 0. 數字 型別
        if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
            if (HSSFDateUtil.isCellDateFormatted(cell)) {
                Date date = cell.getDateCellValue();
                return df.format(date);
            }
            String val = cell.getNumericCellValue()+"";
            val = val.toUpperCase();
            if (val.contains("E")) {
            	BigDecimal nbr = new BigDecimal(val);
            	val = nbr.toPlainString();
            }
            return val;
        }

        // 1. String型別
        if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
            String val = cell.getStringCellValue();
            if (val == null || val.trim().length()==0) {
                if (book != null) {
//                    book.close();
                }
                return "";
            }
            return val.trim();
        }

        // 2. 公式 CELL_TYPE_FORMULA
        if (cell.getCellType() == Cell.CELL_TYPE_FORMULA) {
            return cell.getStringCellValue();
        }

        // 4. 布林值 CELL_TYPE_BOOLEAN
        if (cell.getCellType() == Cell.CELL_TYPE_BOOLEAN) {
            return cell.getBooleanCellValue()+"";
        }

        // 5.	錯誤 CELL_TYPE_ERROR
        return "";
    }
	
	/**
	 * 取得序號
	 * @return String : SEQ
	 * @throws JBranchException
	 */
	private String getSN() throws JBranchException {
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT TBJSB_INS_IMP_LOG_SEQ.nextval AS SEQ FROM DUAL ");
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> SEQLIST = dam.exeQuery(queryCondition);

		return ObjectUtils.toString(SEQLIST.get(0).get("SEQ"));
	}
	
}