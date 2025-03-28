package com.systex.jbranch.app.server.fps.jsb110;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

@Component("jsb110")
@Scope("request")
public class JSB110 extends FubonWmsBizLogic {
	private DataAccessManager dam = null;
	Map<String, Object> mastMapData = new HashMap<String,Object>();
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void inquire (Object body, IPrimitiveMap header) throws JBranchException {
		JSB110OutputVO outputVO = new JSB110OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		StringBuffer sb = new StringBuffer();
		sb.append(" WITH TB1 AS ( SELECT PARAM_CODE, ");
		sb.append(" REGEXP_SUBSTR(PARAM_NAME, '[^,]+', 1, 1) AS PROD_TYPE1, ");
		sb.append(" REGEXP_SUBSTR(PARAM_NAME, '[^,]+', 1, 2) AS PROD_TYPE2 ");
		sb.append(" FROM TBSYSPARAMETER WHERE PARAM_TYPE = 'JSB.INS_COM_PROD'), ");
		sb.append(" TB2 AS ( ");
		sb.append(" SELECT * FROM TB1 UNPIVOT (VALUE FOR PROD_TYPE IN (PROD_TYPE1, PROD_TYPE2))), ");
		sb.append(" TB3 AS ( ");
		sb.append(" SELECT TB2.PARAM_CODE AS INS_COM_ID, TB2.VALUE AS INS_TYPE FROM TB2) ");
		sb.append(" SELECT T.*, I.DATA_DATE, I.CREATETIME, ");
		sb.append(" NVL(I.IMP_ROW_COUNT, 0) AS IMP_ROW_COUNT, ");
		sb.append(" NVL(I.UPDATE_ROW_COUNT, 0) AS UPDATE_ROW_COUNT ");
		sb.append(" FROM TB3 T LEFT JOIN ( ");
		sb.append(" SELECT INS_COM_ID, INS_TYPE, DATA_DATE, CREATETIME, ");
		sb.append(" IMP_ROW_COUNT, UPDATE_ROW_COUNT ");
		sb.append(" FROM TBJSB_INS_IMP WHERE IMP_TYPE = 'JSB_AST_01' ");
		sb.append(" ) I ON I.INS_COM_ID = T.INS_COM_ID AND I.INS_TYPE = T.INS_TYPE ");
		sb.append(" ORDER BY TO_NUMBER(T.INS_COM_ID), T.INS_TYPE ");
		
		qc.setQueryString(sb.toString());
		outputVO.setResultList(dam.exeQuery(qc));
		this.sendRtnObject(outputVO);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked", "unused" })
	public void upload (Object body, IPrimitiveMap header) throws Exception {
		JSB110InputVO inputVO = (JSB110InputVO) body;
		JSB110OutputVO outputVO = new JSB110OutputVO();
		List<Map<String, Object>> outputList = new ArrayList<>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String today = sdf.format(new Date());
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
				String ins_type = inputVO.getIns_type();
				Timestamp data_date = new Timestamp(inputVO.getData_date().getTime());
				
				int headSize = 0;
				Map<String, String> headMap = new HashMap<>();
				Map<String, String> map = resultList.get(0);		// 欄位名稱
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
				sb.append(" WHERE SOURCE_FILE_TYPE IN ('JSB_AST_01') ");
				sb.append(" AND INS_COM_ID = :ins_com_id AND INS_TYPE = :ins_type ");
				qc.setObject("ins_com_id", ins_com_id);
				qc.setObject("ins_type", ins_type);
				qc.setQueryString(sb.toString());
				List<Map<String, Object>> list = dam.exeQuery(qc);
				int columSize = 0;
				if (null != list && list.size() > 0) {
					columSize = Integer.parseInt(list.get(0).get("COUNT").toString());
					if ("114".equals(ins_com_id) && "2".equals(ins_type)) {
						// 法巴投資型需要+1
						columSize += 1;
					}
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
				sb.append(" WHERE SOURCE_FILE_TYPE = 'JSB_AST_01' ");
				sb.append(" AND INS_COM_ID = :ins_com_id ");
				sb.append(" AND INS_TYPE = :ins_type ");
				
				qc.setObject("ins_com_id", ins_com_id);
				qc.setObject("ins_type", ins_type);
				qc.setQueryString(sb.toString());
				List<Map<String, Object>> mapList = dam.exeQuery(qc);
				
				Map<String, Integer> columMap = new HashMap<>();
				for (Map<String, Object> mapMap : mapList) {
					// 取得檔案資料相對應欄位
					columMap.put(mapMap.get("SOUCE_COL_SEQ").toString(), Integer.parseInt(mapMap.get("TARGET_COL_SEQ").toString()));
					
					// 取得『受理編號1』、『保單號碼2』、『中文專案名稱3』、『保單狀態49』所在欄位
					String targetColSeq = mapMap.get("TARGET_COL_SEQ") != null ? mapMap.get("TARGET_COL_SEQ").toString() : "";
					if (targetColSeq.equals("1")) {
						acceptid_s = mapMap.get("SOUCE_COL_SEQ").toString();
					} else if (targetColSeq.equals("2")) {
						policy_nbr_s = mapMap.get("SOUCE_COL_SEQ").toString();
					} else if (targetColSeq.equals("3")) {
						policy_name_s = mapMap.get("SOUCE_COL_SEQ").toString();
					} else if (targetColSeq.equals("49")) {
						soucr_status_s = mapMap.get("SOUCE_COL_SEQ").toString();
					}
				}
				
				// 將『受理編號』每次寫入時放入Set,判斷是否已存在,已存在該筆資料則顯示錯誤
				Set<String> idSet = new HashSet<String>();
				
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
				sb.append(" WHERE A.TNAME = 'TBJSB_AST_INS_MAST_SG01' ");
				sb.append(" AND (A.COLTYPE = 'VARCHAR2' OR A.COLTYPE = 'NUMBER') ");
				qc.setQueryString(sb.toString());
				List<Map<String, Object>> widthList = dam.exeQuery(qc);
				
				// 每次上傳都將『TBJSB_AST_INS_MAST_SG01』、『TBJSB_INS_IMP_LOG』的相對應舊資料刪除
				qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sb = new StringBuffer();
				sb.append(" DELETE FROM TBJSB_AST_INS_MAST_SG01 ");
				sb.append(" WHERE INS_COM_ID = :ins_com_id AND PROD_TYPE = :prod_type ");
				qc.setObject("ins_com_id", ins_com_id);
				qc.setObject("prod_type", ins_type);
				qc.setQueryString(sb.toString());
				dam.exeUpdate(qc);
				
				qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sb = new StringBuffer();
				sb.append(" DELETE FROM TBJSB_INS_IMP_LOG WHERE DATA_DATE = :data_date ");
				sb.append(" AND INS_COM_ID = :ins_com_id AND INS_TYPE = :ins_type ");
				sb.append(" AND IMP_TYPE = 'JSB_AST_01' ");
				qc.setObject("data_date", data_date);
				qc.setObject("ins_com_id", ins_com_id);
				qc.setObject("ins_type", ins_type);
				qc.setQueryString(sb.toString());
				dam.exeUpdate(qc);
				
				//JDBC
				GenFileTools gft = new GenFileTools();
				Connection conn = gft.getConnection();
				conn.setAutoCommit(false);
				
				sb = new StringBuffer();
				sb.append(" INSERT INTO TBJSB_AST_INS_MAST_SG01 ( ");
				sb.append(" CASE_NO, POLICY_NBR, POLICY_NAME, CUST_ID, CUST_NAME, ");
				sb.append(" INS_ID, INS_NAME, TEL_NO, CELL_PHONE, PERM_ADDRESS, ");
				sb.append(" CONTRACT_STATUS, STATUS_CHG_DATE, ACC_VALUE, POLICY_FEE, POLICY_OVER_FEE, ");
				sb.append(" TOTAL_FEE, POLICY_ACTIVE_DATE, ZIP_CODE, CONTACT_ADDRESS, BRANCH_NAME, ");
				sb.append(" SALES_NAME, SALES_ID, REF_NAME, REF_ID, CRCY_TYPE, ");
				sb.append(" ACC_VALUE_DATE, INS_TYPE_NAME, CHANNEL, SERVICE_ID, JSB_PRD_ID, ");
				sb.append(" POLICY_ASSURE_AMT, PAY_TYPE, INACTIVE_DATE, PAY_TERM_YEAR, PROD_DESCR, ");
				sb.append(" DUE_DATE, AGE, PAY_TYPE_DESCR, FIRST_FEE, FIRST_TARGET_FEE, ");
				sb.append(" FIRST_OVER_FEE, ACUM_PAID_POLICY_FEE, ACUM_PAID_POLICY_FEE_TWD, FIRST_POLICY_FEE, ACC_VALUE_ORG, ");
				sb.append(" ACC_VALUE_TWD, REDEEM_AMT, WDRAW_AMT, STATUS_DESC, ACCEPTID, ");
				sb.append(" PARIS_ACCEPTID, INS_COM_ID, PROD_TYPE, UPDATE_STATUS, ");
				sb.append(" VERSION, CREATETIME, CREATOR, MODIFIER, LASTUPDATE ");
				sb.append(" ) VALUES ( ");
				sb.append(" ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ");
				sb.append(" ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ");
				sb.append(" ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ");
				sb.append(" ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ");
				sb.append(" ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ");
				sb.append(" ?, ?, ?, 'IM', ");
				sb.append(" 0, SYSDATE, ?, ?, ?) ");
				
				PreparedStatement pstmt = conn.prepareStatement(sb.toString());
				
				int imp_row_count = 0;		// 上傳成功總筆數
				int fail_row_count = 0;		// 錯誤保單 				(加總E01~E04)
				int status_row_count = 0;	// 狀態異動總筆數			(加總W01)
				int status_ok_count = 0;	// 符合轉入契約狀況保單數	(加總非E04)
				int status_no_count = 0;	// 不符合轉入契約狀況保單數	(加總E04)
				int flag = 0;				// 資料上傳『新增』成功筆數
				
				
				Map<String, Object> acceptidMap = this.getMastMap(inputVO.getIns_com_id(), "A");
				Map<String, Object> policyNbrMap = this.getMastMap(inputVO.getIns_com_id(), "P");
				this.loadMastData(inputVO.getIns_com_id()); //載入保險公司data
				Calendar cal = Calendar.getInstance();
				for (int i = 1; i < resultList.size(); i++) {
					String policy_nbr = resultList.get(i).get(policy_nbr_s);		// 保單號碼
					String acceptid = resultList.get(i).get(acceptid_s);			// 受理編號
					String soucr_status = resultList.get(i).get(soucr_status_s);	// 保單狀態(中文)
					String policy_name = resultList.get(i).get(policy_name_s);		// 中文專案名稱(保單名稱)
					
					// 取得該筆資料在『TBJSB_INS_INS_MAST』對應的資料
					Map<String, Object> mastMap = new HashMap<>();
					// 參數：受理編號、保單號碼、投保商品
					mastMap = this.getMastData(acceptid, policy_nbr);
					
					/**
					 * E01: 上傳保單號碼空白
					 * E02: 受理序號空白
					 * E03: 受理序號重覆
					 * E04: 保單檔無此受理序號（有上傳受理編號但是『TBJSB_AST_INS_MAST_SG』無相對應之受理序號）
					 * E05: 狀態有問題--狀態不在『TBJSB_INS_STATUS』
					 * E07: 保單檔無此保單號碼 
					 * E08: 受理編號長度錯誤
					 * W01: 判斷該筆資料保單狀態是否與『TBJSB_INS_INS_MAST_SG』保單狀態欄位不相同
					 * **/
					String contract_status = "";	// for取得：保單狀態(代碼)，存入 TBJSB_AST_INS_MAST_SG01.CONTRACT_STATUS
					Boolean exist = false;
					for (Map<String, Object> statusMap : statusList) {
						if (statusMap.get("SOURCE_STATUS").toString().trim().equals(soucr_status.trim())) {
							// CONTRACT_STATUS：需使用 TBJSB_INS_STATUS 進行轉換，取出CONTRACT_STUATS
							contract_status = statusMap.get("CONTRACT_STATUS").toString();
//							resultList.get(i).put(soucr_status_s, contract_status);
							exist = true;
							break;
						}
					}
					boolean different = mastMap == null || mastMap.size() == 0;
					if (mastMap != null && mastMap.size() > 0) {
						String mast_status = mastMap.get("CONTRACT_STATUS") != null ? mastMap.get("CONTRACT_STATUS").toString().trim() : null;	// 01、22：有效	10：停效
						
						different = false;
						if (StringUtils.isNotBlank(mast_status)) {
							if (contract_status.substring(0, 2).equals("AC") && (mast_status.equals("01") || mast_status.equals("22"))) {
								different = false;
							} else if (!contract_status.substring(0, 2).equals("AC") && mast_status.equals("10")) {
								different = false;
							} else {
								different = true;
							}
						} else {
							different = true;
						}
					}
					
					if (StringUtils.isBlank(policy_nbr)) {
						// E01: 保單號碼空白
						// 參數：inputVO、錯誤代碼、受理編號、保單號碼、投保商品、保單狀態(中文)、TBJSB_INS_IMP_LOG所有資料
						this.updateLog(inputVO, "E01", acceptid, policy_nbr, policy_name, soucr_status, mastMap, different);
						fail_row_count++;
						continue;
					}
					
					if (StringUtils.isBlank(acceptid)) {
						// E02: 受理編號空白
						this.updateLog(inputVO, "E02", acceptid, policy_nbr, policy_name, soucr_status, mastMap, different);
						fail_row_count++;
						continue;
					} else {
						// 受理編號上傳檢查, 欄位長度為12碼
						if (acceptid.trim().length() != 12) {
							// E08: 受理編號長度錯誤
							this.updateLog(inputVO, "E08", acceptid, policy_nbr, policy_name, soucr_status, mastMap, different);
							fail_row_count++;
							continue;
							
						} else {
							if (!idSet.contains(acceptid)) {
								idSet.add(acceptid);
							} else {
								// E03: 受理編號重複
								this.updateLog(inputVO, "E03", acceptid, policy_nbr, policy_name, soucr_status, mastMap, different);
								fail_row_count++;
								continue;
							}
						}
					}
					
					// Map<String, Object> acceptidMap = this.getMastData(acceptid, policy_nbr, "B");
					if (acceptidMap == null || acceptidMap.size() == 0 || !acceptidMap.containsKey(acceptid)) {
						// E04: 無受理序號（有上傳受理編號但是『TBJSB_AST_INS_MAST_SG』無相對應之受理序號）
						this.updateLog(inputVO, "E04", acceptid, policy_nbr, policy_name, soucr_status, mastMap, different);
						fail_row_count++;
						continue;
					}
					
					if (exist) {
						status_ok_count++;
					}  else {
						// E05: 狀態有問題--狀態不在『TBJSB_INS_STATUS』
						this.updateLog(inputVO, "E05", acceptid, policy_nbr, policy_name, soucr_status, mastMap, different);
						fail_row_count++;
						status_no_count++;
						continue;
					}
					
					if (policyNbrMap == null || policyNbrMap.size() == 0 || !policyNbrMap.containsKey(policy_nbr)) {
						// E07：保單檔無此保單號碼
						this.updateLog(inputVO, "E07", acceptid, policy_nbr, policy_name, soucr_status, mastMap, different);
						fail_row_count++;
						continue;
					}
					
					/**
					 * WMS-CR-20250319-01_修改非富壽保險公司每月保單檔上傳
					 * 1. 當 CONTRACT_DATE 與上傳日期比較在5年以前，且 CONTRACT_STATUS = 13、15、26 時，將不匯入系統，且將這些保單移至錯誤保單中。
					 * 2. 錯誤保單數訊息新增項目：無效保單逾五年
					 * **/
					Date contractDate = new Date();
					if (mastMap != null && mastMap.size() > 0 && mastMap.get("CONTRACT_DATE") != null && mastMap.get("CONTRACT_STATUS") != null) {
						String contractStatus = mastMap.get("CONTRACT_STATUS").toString();
						
						contractDate = (Date) mastMap.get("CONTRACT_DATE");	
						cal.setTime(contractDate);
						cal.add(Calendar.YEAR, 5);	// 加5年
						
						Date d = cal.getTime();
						String cd = sdf.format(d);
						if (cd.compareTo(today) < 0 && (contractStatus.equals("13") || contractStatus.equals("15") || contractStatus.equals("26"))) {
							// E09：無效保單逾五年
							this.updateLog(inputVO, "E09", acceptid, policy_nbr, policy_name, soucr_status, mastMap, different);
							fail_row_count++;
							continue;
						}
					}
					
					// W01的判斷要在E01~E08之後
					if (different) {
						// W01: 判斷該筆資料保單狀態是否與『TBJSB_INS_INS_MAST』契約狀態欄位不相同
						this.updateLog(inputVO, "W01", acceptid, policy_nbr, policy_name, soucr_status, mastMap, different);
						status_row_count++;
					}
					
					// 將上傳資料放至相對應的位置
					String[] data = new String[51];
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
											boolean isNumeric = val.matches("[+-]?\\d*(\\.\\d+)?");
											if (!isNumeric) {
												String comments = widthMap.get("COMMENTS").toString();
												throw new JBranchException("第 " + i + " 筆資料欄位與『" + comments + "』定義不符");
											}
										}
									}
								}
							}
						}
					}
					
					// 新增
					for (int d = 0; d < data.length; d++) {
						int colNbr = d + 1;
						if (colNbr == 1 || colNbr == 2) {
							// 處理受理編號、保單號碼
							String caseNo = data[d];
							if (caseNo.contains("E")) {
				            	BigDecimal nbr = new BigDecimal(caseNo);
				            	data[d] = nbr.toPlainString();
				            }
							pstmt.setObject(colNbr, data[d]);
							
						} else if (colNbr == 11) {
							// 保單狀態（代碼）
							pstmt.setObject(colNbr, contract_status);
							
						} else if (colNbr == 12) {
							// 判斷匯入資料使用S05轉換後是否與S06保單狀態中文(CONTRACT_TEXT)是否相同, 
							// 若相同則填上S06保單狀態異動日(CONTRACT_DATE), 維持原來的資料日期),不同則填上系統日期(最新日期)
							contractDate = new Date();
							if (!different) {
								if (mastMap.get("CONTRACT_DATE") != null) {
									contractDate = (Date) mastMap.get("CONTRACT_DATE");									
								} else {
									contractDate = null;
								}
							}
							
							if (contractDate != null) {
								pstmt.setObject(12, new Timestamp(contractDate.getTime()));								
							} else {
								pstmt.setObject(12, null);
							}
							
						} else if (colNbr == 17 || colNbr == 26 || colNbr == 33 || colNbr == 36) {
							// 處理日期格式
							if (StringUtils.isNotBlank(data[d])) {
								// 台灣人壽(中信)投資型/傳統型：的日期欄位可能會帶0，需另外判斷
								if ("0.0".equals(data[d])) {
									pstmt.setObject(colNbr, null);
								} else {
									Date date = null;
									
									if ("78".equals(ins_com_id) || "83".equals(ins_com_id)) {
										// 新光(78)、遠雄(83)『保單生效日17、保單到期日36』為民國年
										try {
											String yyyyMMdd = this.changeACtoROC(data[d]);
											date = sdf.parse(yyyyMMdd);								
										} catch (Exception e) {
											throw new JBranchException("第 " + i + " 筆資料欄位與日期'YYYMMDD'(民國年)定義不符");
										}
									} else if ("129".equals(ins_com_id)) {
										// 友邦(129)『保單生效日17、保單到期日36、停效日期33』為YYYY/MM/DD 
										try {
											sdf = new SimpleDateFormat("yyyy/MM/dd");
											date = sdf.parse(data[d]);											
										} catch (Exception e) {
											throw new JBranchException("第 " + i + " 筆資料欄位與日期'YYYY/MM/DD'定義不符");
										}
									} else {
										try {
											date = sdf.parse(data[d]);
										} catch (Exception e) {
											throw new JBranchException("第 " + i + " 筆資料欄位與日期'YYYYMMDD'定義不符");
										}
									}
									pstmt.setObject(colNbr, new Timestamp(date.getTime()));										
								}
							} else {
								pstmt.setObject(colNbr, data[d]);
							}
						} else {
							pstmt.setObject(colNbr, data[d]);							
						}
					}
					pstmt.setObject(52, ins_com_id);	// INS_COM_ID ==> 使用者選擇的『保險公司』
					pstmt.setObject(53, ins_type);		// PROD_TYPE  ==> 使用者選擇的『商品類型』
					pstmt.setObject(54, loginID);
					pstmt.setObject(55, loginID);
					pstmt.setObject(56, data_date);		// LASTUPDATE ==> 用來存取使用者選擇的『參考日期』
					pstmt.addBatch();
					
					flag++;
					if (flag % 1000 == 0) {
						pstmt.executeBatch();
						conn.commit();
					}
				}
				pstmt.executeBatch();
				conn.commit();	
				
				imp_row_count = flag;	// 上傳成功總筆數
				
				// INSERT 寫入『TBJSB_INS_IMP』每個『保險公司+商品類型』只留最新一筆，故每次INSERT前都先將舊的資料刪除
				// imp_row_count	上傳成功總筆數
				// fail_row_count	錯誤保單 				(加總E01~E04)
				// status_row_count	狀態異動總筆數			(加總W01)
				// status_ok_count	符合轉入契約狀況保單數	(加總非E04)
				// status_no_count	不符合轉入契約狀況保單數	(加總E04)
				
				qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sb = new StringBuffer();
				sb.append(" DELETE FROM TBJSB_INS_IMP WHERE INS_COM_ID = :ins_com_id ");
				sb.append(" AND INS_TYPE = :ins_type AND IMP_TYPE = 'JSB_AST_01' ");
				
				qc.setObject("ins_com_id", ins_com_id);
				qc.setObject("ins_type", ins_type);
				qc.setQueryString(sb.toString());
				dam.exeUpdate(qc);
				
				qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sb = new StringBuffer();
				sb.append(" INSERT INTO TBJSB_INS_IMP ( ");
				sb.append(" DATA_DATE, INS_COM_ID, INS_TYPE, IMP_TYPE, IMP_ROW_COUNT, ");
				sb.append(" FAIL_ROW_COUNT, STATUS_ROW_COUNT, ");
				sb.append(" DATA_MONTH, STATUS_OK_COUNT, STATUS_NO_COUNT, ");
				sb.append(" VERSION, CREATETIME, CREATOR, MODIFIER, LASTUPDATE ");
				sb.append(" ) VALUES ( ");
				sb.append(" :data_date, :ins_com_id, :ins_type, 'JSB_AST_01', :imp_row_count, ");
				sb.append(" :fail_row_count, :status_row_count, ");
				sb.append(" :data_month, :status_ok_count, :status_no_count, ");
				sb.append(" 0, SYSDATE, :loginID, :loginID, SYSDATE) ");
				
				qc.setObject("data_date", data_date);
				qc.setObject("ins_com_id", Integer.parseInt(ins_com_id));
				qc.setObject("ins_type", ins_type);
				qc.setObject("imp_row_count", imp_row_count);
				qc.setObject("fail_row_count", fail_row_count);
				qc.setObject("status_row_count", status_row_count);
				qc.setObject("data_month", null);
				qc.setObject("status_ok_count", status_ok_count);
				qc.setObject("status_no_count", status_no_count);
				qc.setObject("loginID", loginID);
				qc.setQueryString(sb.toString());
				dam.exeUpdate(qc);
				
				Map<String, Object> resultMap = new HashMap<>();
				// 保險公司	(前端)	
				// 商品類型	(前端)	
				// 參考日期	(前端)轉格式	
				// 上傳日期	
				// 符合轉入契約狀況保單數	
				// 不符合轉入契約狀況保單數	
				// 錯誤保單數
				// 總計保單數
				resultMap.put("DATA_DATE", inputVO.getData_date());
				resultMap.put("TODAY", new Date());
				resultMap.put("STATUS_OK_COUNT", status_ok_count);
				resultMap.put("STATUS_NO_COUNT", status_no_count);
				resultMap.put("FAIL_ROW_COUNT", fail_row_count);
				resultMap.put("TOTAL_COUNT", fail_row_count + imp_row_count);
				outputList.add(resultMap);
			}
		} else {
			throw new APException("上傳檔案欄位與定義不符");
		}
		outputVO.setResultList(outputList);
		this.sendRtnObject(outputVO);
	}
	
	public void goMerge (Object body, IPrimitiveMap header) throws Exception {
		JSB110InputVO inputVO = (JSB110InputVO) body;
		String loginID = (String) getUserVariable(FubonSystemVariableConsts.LOGINID);
		String ins_com_id = inputVO.getIns_com_id();
		String ins_type = inputVO.getIns_type();
		int update_row_count = 0;
		
		dam = this.getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		// STEP1：更新至主檔(TBJSB_AST_INS_MAST)，以MERGE INTO 方式寫入
		sb.append(" MERGE INTO TBJSB_AST_INS_MAST_SG TG USING ( ");
		sb.append(" SELECT A.*, B.FUBON_CONTRACT_STATUS, B.CONTRACT_STATUS_DESCR ");
		sb.append(" FROM TBJSB_AST_INS_MAST_SG01 A LEFT JOIN ( ");
		sb.append(" SELECT DISTINCT CONTRACT_STATUS, FUBON_CONTRACT_STATUS, CONTRACT_STATUS_DESCR ");
		sb.append(" FROM TBJSB_INS_STATUS) B ON A.CONTRACT_STATUS = B.CONTRACT_STATUS ");
		sb.append(" WHERE INS_COM_ID = :ins_com_id AND PROD_TYPE = :prod_type AND UPDATE_STATUS = 'IM') SR ");
		sb.append(" ON (TG.ACCEPTID = SR.CASE_NO AND TG.POLICY_NBR = SR.POLICY_NBR AND TG.MAST_SLA_TYPE = 'Y') ");
		sb.append(" WHEN MATCHED THEN ");
		sb.append(" UPDATE SET ");
		sb.append(" TG.CUST_ID   = CASE WHEN NVL(SR.CUST_ID  , 'X') <> 'X' AND LENGTH(TG.CUST_ID) <> 11 THEN SR.CUST_ID ELSE TG.CUST_ID END, ");
		sb.append(" TG.APPL_NAME = CASE WHEN NVL(SR.CUST_NAME, 'X') <> 'X' THEN SR.CUST_NAME ELSE TG.APPL_NAME END, ");
		sb.append(" TG.INS_ID 	 = CASE WHEN NVL(SR.INS_ID	 , 'X') <> 'X' THEN SR.INS_ID 	 ELSE TG.INS_ID    END, ");
		sb.append(" TG.INS_NAME  = CASE WHEN NVL(SR.INS_NAME , 'X') <> 'X' THEN SR.INS_NAME  ELSE TG.INS_NAME  END, ");
		
		sb.append(" TG.CONTRACT_DATE = CASE WHEN (TG.CONTRACT_STATUS <> FUBON_CONTRACT_STATUS AND SR.INACTIVE_DATE IS NOT NULL) THEN SR.INACTIVE_DATE ");
		sb.append(" WHEN (TG.CONTRACT_STATUS <> FUBON_CONTRACT_STATUS AND SR.INACTIVE_DATE IS NULL) THEN :data_date ELSE TG.CONTRACT_DATE END, ");
//		sb.append(" TG.CONTRACT_DATE = SR.INACTIVE_DATE, ");
		
		sb.append(" TG.CONTRACT_STATUS = SR.FUBON_CONTRACT_STATUS, ");
		sb.append(" TG.CONTRACT_TEXT = SR.CONTRACT_STATUS_DESCR, ");
		
		sb.append(" TG.POLICY_ACTIVE_DATE = CASE WHEN SR.POLICY_ACTIVE_DATE IS NOT NULL THEN SR.POLICY_ACTIVE_DATE ELSE TG.POLICY_ACTIVE_DATE END, ");
//		sb.append(" TG.POLICY_ACTIVE_DATE = SR.POLICY_ACTIVE_DATE, ");
		
		// 僅『法巴(114)投資型(2)』可更新：保單簡稱 (POLICY_SIMP_NAME)、險種代碼(INS_TYPE、PRD_ID) 這三個欄位
		if ("114".equals(ins_com_id) && "2".equals(ins_type)) {
			// 中文專案名稱
			sb.append(" TG.POLICY_SIMP_NAME = SR.POLICY_NAME, ");
			// 對應日盛保代產品代碼
			sb.append(" TG.INS_TYPE = SR.JSB_PRD_ID, ");
			sb.append(" TG.PRD_ID = SR.JSB_PRD_ID, ");
		}
		
		sb.append(" TG.POLICY_ASSURE_AMT = CASE WHEN SR.POLICY_ASSURE_AMT IS NOT NULL THEN SR.POLICY_ASSURE_AMT ELSE TG.POLICY_ASSURE_AMT END, ");
//		sb.append(" TG.POLICY_ASSURE_AMT = SR.POLICY_ASSURE_AMT, ");

		sb.append(" TG.PAY_TERM_YEAR = CASE WHEN SR.PAY_TERM_YEAR IS NOT NULL THEN SR.PAY_TERM_YEAR ELSE TG.PAY_TERM_YEAR END, ");
//		sb.append(" TG.PAY_TERM_YEAR = SR.PAY_TERM_YEAR, ");
		
		sb.append(" TG.SYS_YEAR = TO_CHAR(SR.LASTUPDATE,'YYYY'), ");
		sb.append(" TG.SYS_MONTH = TO_CHAR(SR.LASTUPDATE,'MM'), ");
		sb.append(" TG.SYS_DAY = TO_CHAR(SR.LASTUPDATE,'DD'), ");
		sb.append(" TG.LASTUPDATE = SYSDATE, ");
		sb.append(" TG.MODIFIER = :loginID ");
		

		Timestamp data_date = new Timestamp(inputVO.getData_date().getTime());
		qc.setObject("data_date" , data_date);
		qc.setObject("ins_com_id", ins_com_id);
		qc.setObject("prod_type" , ins_type);
		qc.setObject("loginID"   , loginID);
		qc.setQueryString(sb.toString());
		update_row_count = dam.exeUpdate(qc);
		
		// STEP2：更新 TBJSB_AST_INS_MAST_SG01.UPDAT_STATUS = 'UP'
		qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sb = new StringBuffer();
		sb.append(" UPDATE TBJSB_AST_INS_MAST_SG01 SET UPDATE_STATUS = 'UP' ");
		sb.append(" WHERE INS_COM_ID = :ins_com_id AND PROD_TYPE = :prod_type ");
		
		qc.setObject("ins_com_id", ins_com_id);
		qc.setObject("prod_type" , ins_type);
		qc.setQueryString(sb.toString());
		dam.exeUpdate(qc);
		
		// STEP3：更新 TBJSB_INS_IMP.UPDATE_ROW_COUNT
		qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sb = new StringBuffer();
		sb.append(" UPDATE TBJSB_INS_IMP SET UPDATE_ROW_COUNT = :update_row_count, ");
		sb.append(" VERSION = VERSION+1, MODIFIER = :loginID, LASTUPDATE = SYSDATE ");
		sb.append(" WHERE INS_COM_ID = :ins_com_id AND INS_TYPE = :ins_type AND IMP_TYPE = 'JSB_AST_01' ");
		
		qc.setObject("update_row_count", update_row_count);
		qc.setObject("loginID"   , loginID);
		qc.setObject("ins_com_id", ins_com_id);
		qc.setObject("ins_type"  , ins_type);
		
		qc.setQueryString(sb.toString());
		dam.exeUpdate(qc);
		
		this.sendRtnObject(null);
	}
	
	private String formatStr(Object column) {
        return "=\"" + ObjectUtils.defaultIfNull(column, "") + "\"";
    }
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void export (Object body, IPrimitiveMap header) throws Exception {
		JSB110InputVO inputVO = (JSB110InputVO) body;
		String exportType = inputVO.getExport_type();
		String fileName = "";     
		List<Map<String, Object>> resultList = new ArrayList<>();
		List listCSV = new ArrayList();
		CSVUtil csv = new CSVUtil();
		
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> comNameMap = xmlInfo.doGetVariable("JSB.INS_COM_NAME_01", FormatHelper.FORMAT_3);
		Map<String, String> insTypeMap = new HashMap();
		insTypeMap.put("1", "傳統型");
		insTypeMap.put("2", "投資型");
		
		if ("OK".equals(exportType)) {
			// 符合轉入契約狀況保單數
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
	            records[++i] = formatStr(map.get("CASE_NO"));				// 受理編號				CASE_NO
	            records[++i] = formatStr(map.get("POLICY_NAME"));			// 中文專案名稱			POLICY_NAME
	            records[++i] = formatStr(map.get("CUST_ID"));				// 要保人ID				CUST_ID
	            records[++i] = formatStr(map.get("CUST_NAME"));				// 要保人姓名				CUST_NAME
	            records[++i] = formatStr(map.get("INS_ID"));				// 被保人ID				INS_ID
	            records[++i] = formatStr(map.get("INS_NAME"));				// 被保人姓名				INS_NAME
	            records[++i] = formatStr(map.get("CONTRACT_STATUS_DESCR"));	// 保單狀態				CONTRACT_STATUS_DESCR
	            records[++i] = formatStr(map.get("POLICY_ACTIVE_DATE"));	// 生效日					POLICY_ACTIVE_DATE
	            records[++i] = formatStr(map.get("JSB_PRD_ID"));			// 對應日盛保代產品代號		JSB_PRD_ID
	            records[++i] = formatStr(map.get("POLICY_ASSURE_AMT"));		// 保額					POLICY_ASSURE_AMT
	            records[++i] = formatStr(map.get("INACTIVE_DATE"));			// 停效/失效日期			INACTIVE_DATE
	            records[++i] = formatStr(map.get("PAY_TERM_YEAR"));			// 繳費年期				PAY_TERM_YEAR
	            
//	            records[++i] = formatStr(map.get("INS_COM_ID")); 			// 保險公司代碼			INS_COM_ID
	            String ins_com_id = map.get("INS_COM_ID") != null ? map.get("INS_COM_ID").toString() : "";
	            records[++i] = formatStr(comNameMap.get(ins_com_id));
//	            records[++i] = formatStr(map.get("PROD_TYPE")); 			// 商品類型				PROD_TYPE
	            String ins_type = map.get("PROD_TYPE") != null ? map.get("PROD_TYPE").toString() : "";
	            records[++i] = formatStr(insTypeMap.get(ins_type));
	            listCSV.add(records);
	        }
			csv.setHeader(new String[]{"保單號碼", "受理編號", "中文專案名稱", "要保人ID", "要保人姓名", 
									   "被保人ID", "被保人姓名", "保單狀態", "生效日", "對應日盛保代產品代號", 
									   "保額", "停效/失效日期", "繳費年期", "保險公司", "商品類型"});
		} else {
			if ("NO".equals(exportType)) {
				// 不符合轉入契約狀況保單數
				fileName = "不符合轉入契約狀況保單";
				resultList = this.getError(inputVO, "E05");
			} else {
				// 錯誤保單數
				fileName = "錯誤保單";
				resultList = this.getError(inputVO, "E");
			}
			
			Map<String, String> errorTypeMap = xmlInfo.doGetVariable("JSB.IMP_ERROR_TYPE", FormatHelper.FORMAT_3);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
			for (Map<String, Object> map : resultList) {
				String[] records = new String[8];
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
//	            records[++i] = formatStr(map.get("INS_TYPE"));		// INS_TYPE		險種類別
	            String ins_type = map.get("INS_TYPE") != null ? map.get("INS_TYPE").toString() : "";
	            records[++i] = formatStr(insTypeMap.get(ins_type));	
//	            records[++i] = formatStr(map.get("IMP_TYPE"));		// IMP_TYPE		上傳檔案
	            records[++i] = "保單檔";
	            String error_type = map.get("ERROR_TYPE") != null ? map.get("ERROR_TYPE").toString() : "";
	            records[++i] = formatStr(errorTypeMap.get(error_type));
//	            records[++i] = formatStr(map.get("ERROR_TYPE"));	// ERROR_TYPE	錯誤代碼
	            records[++i] = formatStr(map.get("CASE_NO"));		// CASE_NO		受理編號
	            records[++i] = formatStr(map.get("POLICY_NBR"));	// POLICY_NBR	保單號碼
	            records[++i] = formatStr(map.get("POLICY_NAME"));	// POLICY_NAME	投保商品
	            listCSV.add(records);
			}
			csv.setHeader(new String[]{"參考日期", "保險公司", "險種類別", "上傳檔案", 
									   "錯誤代碼", "受理編號", "保單號碼", "投保商品"});
		}
		csv.addRecordList(listCSV);
        String url = csv.generateCSV();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        fileName = fileName + "_" + sdf.format(new Date())+ ".csv";
        notifyClientToDownloadFile(url, fileName);
        this.sendRtnObject(null);
	}
	
	public void exportUP (Object body, IPrimitiveMap header) throws Exception {
		JSB110InputVO inputVO = (JSB110InputVO) body;
		List listCSV = new ArrayList();
		CSVUtil csv = new CSVUtil();
		String fileName = "";
		
		dam = this.getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		sb.append(" SELECT POLICY_NBR, ACCEPTID, POLICY_SIMP_NAME, CUST_ID, ");
		sb.append(" APPL_NAME, INS_ID, INS_NAME, CONTRACT_TEXT, ");
		sb.append(" POLICY_ACTIVE_DATE, PRD_ID, POLICY_ASSURE_AMT_DIV10K, ");
		sb.append(" CONTRACT_DATE, PAY_TERM_YEAR, COM_ID, ");
		sb.append(" CASE WHEN SP_POLICY_NOTE = 'U' THEN '投資型' ELSE '傳統型' END AS SP_POLICY_NOTE ");
		sb.append(" FROM TBJSB_AST_INS_MAST_SG WHERE 1 = 1 ");
		
		if (StringUtils.isNotBlank(inputVO.getIns_com_id())) {
			XmlInfo xmlInfo = new XmlInfo();
			Map<String, String> comNameMap = xmlInfo.doGetVariable("JSB.INS_COM_NAME_01", FormatHelper.FORMAT_3);
			String comName = comNameMap.get(inputVO.getIns_com_id());
			fileName = comName + "保險公司更新保單資料";
			
			sb.append(" AND COM_ID = :com_id ");			
			qc.setObject("com_id", inputVO.getIns_com_id());

			if (StringUtils.isNotBlank(inputVO.getIns_type())) {
				String ins_type = inputVO.getIns_type();
				if ("1".equals(ins_type)) {
					// 傳統型
					sb.append(" AND NVL(SP_POLICY_NOTE, '1') <> 'U' ");
				} else {
					// 投資型
					sb.append(" AND NVL(SP_POLICY_NOTE, '1') = 'U' ");
				}
			}
			if (null != inputVO.getData_date()) {
				sb.append(" AND SYS_YEAR || LPAD(SYS_MONTH, 2, '0') || LPAD(SYS_DAY, 2, '0') = TO_CHAR(:data_date, 'YYYYMMDD') ");
				qc.setObject("data_date", new Timestamp(inputVO.getData_date().getTime()));
			}
		} else {
			fileName = "全部更新保單檔資料";
			
			sb.append(" AND SYS_YEAR || LPAD(SYS_MONTH, 2, '0') = TO_CHAR(SYSDATE, 'YYYYMM') ");
		}
		qc.setQueryString(sb.toString());
		List<Map<String, Object>> resultList = dam.exeQuery(qc);
		
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> comNameMap = xmlInfo.doGetVariable("JSB.INS_COM_NAME_01", FormatHelper.FORMAT_3);
		
		for (Map<String, Object> map : resultList) {
            String[] records = new String[15];
            int i = 0;
            records[i]   = formatStr(map.get("POLICY_NBR"));				// 保單號碼				POLICY_NBR
            records[++i] = formatStr(map.get("ACCEPTID"));					// 受理編號				ACCEPTID
            records[++i] = formatStr(map.get("POLICY_SIMP_NAME"));			// 中文專案名稱			POLICY_SIMP_NAME
            records[++i] = formatStr(map.get("CUST_ID"));					// 要保人ID				CUST_ID
            records[++i] = formatStr(map.get("APPL_NAME"));					// 要保人姓名				APPL_NAME
            records[++i] = formatStr(map.get("INS_ID"));					// 被保人ID				INS_ID
            records[++i] = formatStr(map.get("INS_NAME"));					// 被保人姓名				INS_NAME
            records[++i] = formatStr(map.get("CONTRACT_TEXT"));				// 保單狀態				CONTRACT_TEXT
            records[++i] = formatStr(map.get("POLICY_ACTIVE_DATE"));		// 生效日					POLICY_ACTIVE_DATE
            records[++i] = formatStr(map.get("PRD_ID"));					// 對應日盛保代產品代號		PRD_ID
            records[++i] = formatStr(map.get("POLICY_ASSURE_AMT_DIV10K"));	// 保額					POLICY_ASSURE_AMT_DIV10K
            records[++i] = formatStr(map.get("CONTRACT_DATE"));				// 停效/失效日期			CONTRACT_DATE
            records[++i] = formatStr(map.get("PAY_TERM_YEAR"));				// 繳費年期				PAY_TERM_YEAR
            
//          records[++i] = formatStr(map.get("COM_ID")); 					// 保險公司代碼			COM_ID
            String com_id = map.get("COM_ID") != null ? map.get("COM_ID").toString() : "";
            records[++i] = formatStr(comNameMap.get(com_id));
            
            records[++i] = formatStr(map.get("SP_POLICY_NOTE")); 			// 商品類型				SP_POLICY_NOTE （U:投資型、空值:傳統型）

            listCSV.add(records);
        }
		csv.setHeader(new String[]{"保單號碼", "受理編號", "中文專案名稱", "要保人ID", "要保人姓名", 
								   "被保人ID", "被保人姓名", "保單狀態", "生效日", "對應日盛保代產品代號", 
								   "保額", "停效/失效日期", "繳費年期", "保險公司", "商品類型"});
		
		csv.addRecordList(listCSV);
        String url = csv.generateCSV();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        fileName = fileName + "_" + sdf.format(new Date())+ ".csv";
        notifyClientToDownloadFile(url, fileName);
        this.sendRtnObject(null);
	}
	
	// 上傳後結果檢視，按取消，資料不會彙入匯整檔中，如當月有上傳過，將清空重新上傳
	public void cancel (Object body, IPrimitiveMap header) throws Exception {
		JSB110InputVO inputVO = (JSB110InputVO) body;
		String ins_com_id = inputVO.getIns_com_id();
		String ins_type = inputVO.getIns_type();
		Timestamp data_date = new Timestamp(inputVO.getData_date().getTime());
		
		// 清空該保險公司在 TBJSB_INS_IMP、TBJSB_AST_INS_MAST_SG01、TBJSB_INS_IMP_LOG 的資料
		dam = this.getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		sb.append(" DELETE FROM TBJSB_INS_IMP WHERE INS_COM_ID = :ins_com_id ");
		sb.append(" AND INS_TYPE = :ins_type AND IMP_TYPE = 'JSB_AST_01' ");
		
		qc.setObject("ins_com_id", ins_com_id);
		qc.setObject("ins_type", ins_type);
		qc.setQueryString(sb.toString());
		dam.exeUpdate(qc);
		
		
		qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sb = new StringBuffer();
		sb.append(" DELETE FROM TBJSB_AST_INS_MAST_SG01 ");
		sb.append(" WHERE INS_COM_ID = :ins_com_id AND PROD_TYPE = :prod_type ");
		qc.setObject("ins_com_id", ins_com_id);
		qc.setObject("prod_type", ins_type);
		qc.setQueryString(sb.toString());
		dam.exeUpdate(qc);
		
		qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sb = new StringBuffer();
		sb.append(" DELETE FROM TBJSB_INS_IMP_LOG WHERE DATA_DATE = :data_date ");
		sb.append(" AND INS_COM_ID = :ins_com_id AND INS_TYPE = :ins_type ");
		sb.append(" AND IMP_TYPE = 'JSB_AST_01' ");
		qc.setObject("data_date", data_date);
		qc.setObject("ins_com_id", ins_com_id);
		qc.setObject("ins_type", ins_type);
		qc.setQueryString(sb.toString());
		dam.exeUpdate(qc);
		
		this.sendRtnObject(null);
	}
	
	private List<Map<String, Object>> getOK(JSB110InputVO inputVO) throws JBranchException {
		List<Map<String, Object>> OKList = new ArrayList<>();
		dam = this.getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		sb.append(" SELECT POLICY_NBR, CASE_NO, POLICY_NAME, CUST_ID, CUST_NAME, INS_ID, INS_NAME, ");
		sb.append(" STATUS_DESC, S.CONTRACT_STATUS_DESCR, POLICY_ACTIVE_DATE, JSB_PRD_ID, POLICY_ASSURE_AMT, ");
		sb.append(" INACTIVE_DATE, PAY_TERM_YEAR, INS_COM_ID, PROD_TYPE ");
		sb.append(" FROM TBJSB_AST_INS_MAST_SG01 SG LEFT JOIN ( ");
		sb.append(" SELECT DISTINCT CONTRACT_STATUS, CONTRACT_STATUS_DESCR FROM TBJSB_INS_STATUS ");
		sb.append(" ) S ON SG.CONTRACT_STATUS = S.CONTRACT_STATUS WHERE 1 = 1 ");
		
		if (StringUtils.isNotBlank(inputVO.getIns_com_id())) {
			sb.append(" AND INS_COM_ID = :ins_com_id ");
			sb.append(" AND PROD_TYPE = :prod_type AND LASTUPDATE = :data_date ");
			
			qc.setObject("ins_com_id", inputVO.getIns_com_id());
			qc.setObject("prod_type", inputVO.getIns_type());
			qc.setObject("data_date", new Timestamp(inputVO.getData_date().getTime()));
			
		} else {
			sb.append(" AND TO_CHAR(LASTUPDATE, 'YYYYMM') = TO_CHAR(SYSDATE, 'YYYYMM') ");
			sb.append(" ORDER BY INS_COM_ID, PROD_TYPE ");
		}
		qc.setQueryString(sb.toString());
		OKList = dam.exeQuery(qc);
		return OKList;
	}
	
	private List<Map<String, Object>> getError(JSB110InputVO inputVO, String errType) throws JBranchException {
		List<Map<String, Object>> errorList = new ArrayList<>();
		dam = this.getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		sb.append(" SELECT * FROM TBJSB_INS_IMP_LOG WHERE DATA_DATE = :data_date ");
		sb.append(" AND INS_COM_ID = :ins_com_id AND INS_TYPE = :ins_type ");
		sb.append(" AND IMP_TYPE = 'JSB_AST_01' ");
		
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
		qc.setObject("ins_type", inputVO.getIns_type());
		qc.setQueryString(sb.toString());
		errorList = dam.exeQuery(qc);
		return errorList;
	}
	
	// 更新：TBJSB_INS_IMP_LOG（日盛保單匯入錯誤資訊檔）
	// 參數：inputVO、錯誤代碼、受理編號、保單號碼、投保商品、保單狀態(中文)
	private void updateLog(JSB110InputVO inputVO, String error_type, 
						   String case_no, String policy_nbr, String policy_name, 
						   String contract_status, Map<String, Object> mastMap, Boolean different) throws JBranchException {
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String loginID = (String) getUserVariable(FubonSystemVariableConsts.LOGINID);
		dam = this.getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		sb.append(" INSERT INTO TBJSB_INS_IMP_LOG ( ");
		sb.append(" SEQ_NO, DATA_DATE, INS_COM_ID, INS_TYPE, IMP_TYPE, ");
		sb.append(" ERROR_TYPE, CASE_NO, POLICY_NBR, POLICY_NAME, CONTRACT_STATUS, ");
		sb.append(" STATUS_CHG_DATE, ");
		if (mastMap != null && mastMap.size() > 0) {
			sb.append(" PRE_CONTRACT_STATUS, PRE_STATUS_CHG_DATE, ");
		}
		sb.append(" VERSION, CREATETIME, CREATOR, MODIFIER, LASTUPDATE ");
		sb.append(" ) VALUES ( ");
		sb.append(" :seq_no, :data_date, :ins_com_id, :ins_type, 'JSB_AST_01', ");
		sb.append(" :error_type, :case_no, :policy_nbr, :policy_name, :contract_status, ");
		sb.append(" TO_DATE(:status_chg_date, 'yyyyMMdd'), ");
		
		Date status_chg_date = new Date();
		String pre_contract_status = null;
		Date pre_status_chg_date = null;
		if (mastMap != null && mastMap.size() > 0) {
			sb.append(" :pre_contract_status, TO_DATE(:pre_status_chg_date, 'yyyyMMdd'), ");
			
			pre_contract_status = mastMap.get("CONTRACT_TEXT") != null ? mastMap.get("CONTRACT_TEXT").toString() : null;
			pre_status_chg_date = mastMap.get("CONTRACT_DATE") != null ? (Date) mastMap.get("CONTRACT_DATE") : null;
			qc.setObject("pre_contract_status", pre_contract_status);
			
			if (pre_status_chg_date == null) {
				pre_status_chg_date = new Date();
		        qc.setObject("pre_status_chg_date", sdf.format(pre_status_chg_date));
		        
			} else {
				qc.setObject("pre_status_chg_date", sdf.format(pre_status_chg_date));	
			}	
			
			if (!different) {
				status_chg_date = pre_status_chg_date;
			}
		}
		sb.append(" 0, SYSDATE, :loginID, :loginID, SYSDATE) ");
		
		qc.setObject("seq_no", this.getSN());
		qc.setObject("data_date", new Timestamp(inputVO.getData_date().getTime()));
		qc.setObject("ins_com_id", inputVO.getIns_com_id());
		qc.setObject("ins_type", inputVO.getIns_type());
		qc.setObject("error_type", error_type);
		qc.setObject("case_no", case_no);
		qc.setObject("policy_nbr", policy_nbr);
		qc.setObject("policy_name", policy_name);
		qc.setObject("contract_status", contract_status);
		qc.setObject("status_chg_date", status_chg_date != null ? sdf.format(status_chg_date) : sdf.format(new Date()));
		qc.setObject("loginID", loginID);
		
		qc.setQueryString(sb.toString());
		dam.exeUpdate(qc);
	}
	
	@SuppressWarnings("unchecked")
	private void loadMastData(String ins_com_id) throws JBranchException {
		// 判斷該筆資料保單狀態是否與『TBJSB_INS_INS_MAST』保單狀態欄位是否相同
		dam = this.getDataAccessManager();
		QueryConditionIF qc = dam
				.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();

		sb.append(" SELECT M.ACCEPTID, M.POLICY_NBR, M.POLICY_SIMP_NAME, ");
		sb.append(" M.CONTRACT_STATUS, M.CONTRACT_TEXT, M.CONTRACT_DATE ");
		sb.append(" FROM TBJSB_AST_INS_MAST_SG M WHERE M.MAST_SLA_TYPE = 'Y' ");
		sb.append(" AND COM_ID = :ins_com_id ");
		
		qc.setObject("ins_com_id", ins_com_id);

		qc.setQueryString(sb.toString());
		List<Map<String, Object>> mastList = dam.exeQuery(qc);
		if (null != mastList && mastList.size() > 0) {
			for(Map<String,Object> map:mastList){
				mastMapData.put(ObjectUtils.toString(map.get("ACCEPTID"))+ObjectUtils.toString(map.get("POLICY_NBR")), map);
			}
		}
		
	}
	// 參數：受理編號、保單號碼、投保商品
	@SuppressWarnings("unchecked")
	private Map<String, Object> getMastData(String case_no, String policy_nbr) throws JBranchException {
		return (Map<String, Object>)mastMapData.get(case_no+policy_nbr);
	}
	
	private Map<String, Object> getMastMap(String ins_com_id, String type) throws JBranchException {
		Map<String, Object> mastMap = new HashMap<>();
		
		// 判斷該筆資料保單狀態是否與『TBJSB_INS_INS_MAST』保單狀態欄位是否相同
		dam = this.getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		if ("A".equals(type)) {
			sb.append(" SELECT DISTINCT M.ACCEPTID ");
		} else {
			sb.append(" SELECT DISTINCT M.POLICY_NBR ");			
		}
		sb.append(" FROM TBJSB_AST_INS_MAST_SG M WHERE M.MAST_SLA_TYPE = 'Y' ");
		sb.append(" AND COM_ID = :ins_com_id ");
		qc.setQueryString(sb.toString());
		qc.setObject("ins_com_id", ins_com_id);
		List<Map<String, Object>> mastList = dam.exeQuery(qc);
		
		if (null != mastList && mastList.size() > 0) {
			if ("A".equals(type)) {
				for (Map<String,Object> map : mastList) {
					mastMap.put(ObjectUtils.toString(map.get("ACCEPTID")),map.get("ACCEPTID"));				
				}	
			} else {
				for (Map<String,Object> map : mastList) {
					mastMap.put(ObjectUtils.toString(map.get("POLICY_NBR")),map.get("POLICY_NBR"));				
				}
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
	 * 讀取Excel檔案格式
	 * 
	 * @param file
	 * @return List<Map<Integer,String>>
	 * @throws JBranchException
	 * @throws IOException 
	 * @throws InvalidFormatException 
	 * @throws EncryptedDocumentException 
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
					int dataNbr = i + 1;
					int colNbr = j + 1;
					throw new JBranchException("第 " + dataNbr + " 筆資料，第 " + colNbr + " 個欄位有誤。" );
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
	
	/**
	 * 民國年轉西元年
	 */
	private String changeACtoROC(String AC) {
		if (StringUtils.isNotBlank(AC)) {
			// 防止字串變數值 XXX.0 ==> 將小數點以後都移除
			if (AC.contains(".")) {
				int index = AC.indexOf(".");
				AC = AC.substring(0, index);
			}
			if (AC.trim().length() == 6) {
				// 前補0
				AC = "0" + AC;
			}
			String yyyy = String.valueOf(Integer.parseInt(AC.substring(0, 3)) + 1911);
			String mm = AC.substring(3,5);
			String dd = AC.substring(5);
			return yyyy + mm + dd;
			
		} else {
			return null;
		}
	}
	
}