package com.systex.jbranch.app.server.fps.jsb130;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.ArrayList;
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

import jxl.read.biff.BiffException;

@Component("jsb130")
@Scope("request")
public class JSB130 extends FubonWmsBizLogic {
	private DataAccessManager dam = null;
	Map<String, Object> mastMapData = new HashMap<String,Object>();
	
	public void getYearMonth (Object body, IPrimitiveMap header) throws JBranchException {
		JSB130OutputVO outputVO = new JSB130OutputVO();
		List<Map<String, Object>> resultList = new ArrayList<>();
		String thisMonth = null;
		
		dam = this.getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		sb.append(" SELECT TO_CHAR(SYSDATE, 'YYYYMM') AS MONTH1, ");
		sb.append(" TO_CHAR(ADD_MONTHS(SYSDATE, -1), 'YYYYMM') AS MONTH2, ");
		sb.append(" TO_CHAR(ADD_MONTHS(SYSDATE, -2), 'YYYYMM') AS MONTH3 FROM DUAL ");
		qc.setQueryString(sb.toString());
		List<Map<String, Object>> list = dam.exeQuery(qc);
		
		if (list.size() > 0) {
			thisMonth = list.get(0).get("MONTH1").toString();
			Map<String, Object> map = new HashMap<>();
			map.put("YEAR_MONTH", list.get(0).get("MONTH1"));
			resultList.add(map);
			map = new HashMap<>();
			map.put("YEAR_MONTH", list.get(0).get("MONTH2"));
			resultList.add(map);
			map = new HashMap<>();
			map.put("YEAR_MONTH", list.get(0).get("MONTH3"));
			resultList.add(map);
		}
		outputVO.setResultList(resultList);
		outputVO.setThisMonth(thisMonth);
		this.sendRtnObject(outputVO);
	}
	
	public void inquire (Object body, IPrimitiveMap header) throws JBranchException {
		JSB130OutputVO outputVO = new JSB130OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		
		sb.append(" SELECT PARAM_CODE AS INS_COM_ID, I.DATA_MONTH, I.DATA_DATE, I.CREATETIME, ");
		sb.append(" NVL(I.IMP_ROW_COUNT, 0) AS IMP_ROW_COUNT, ");
		sb.append(" NVL(I.UPDATE_ROW_COUNT, 0) AS UPDATE_ROW_COUNT FROM ( ");
		sb.append(" SELECT * FROM TBSYSPARAMETER WHERE PARAM_TYPE = 'JSB.INS_COM_NAME_03') P ");
		sb.append(" LEFT JOIN ( SELECT INS_COM_ID, INS_TYPE, DATA_DATE, CREATETIME, ");
		sb.append(" IMP_ROW_COUNT, UPDATE_ROW_COUNT, DATA_MONTH FROM TBJSB_INS_IMP ");
		sb.append(" WHERE IMP_TYPE = 'JSB_AST_03') I ON I.INS_COM_ID = P.PARAM_CODE ");
		sb.append(" ORDER BY TO_NUMBER(PARAM_CODE) ");
		
		qc.setQueryString(sb.toString());
		outputVO.setResultList(dam.exeQuery(qc));
		this.sendRtnObject(outputVO);
	}
	
	public void upload (Object body, IPrimitiveMap header) throws Exception {
		JSB130InputVO inputVO = (JSB130InputVO) body;
		JSB130OutputVO outputVO = new JSB130OutputVO();
		List<Map<String, Object>> outputList = new ArrayList<>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		File dataFile = null;
		String loginID = (String) getUserVariable(FubonSystemVariableConsts.LOGINID);		
		String ins_com_id = inputVO.getIns_com_id();
		String data_month = inputVO.getData_month();
		
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
				sb.append(" WHERE SOURCE_FILE_TYPE IN ('JSB_AST_03') ");
				sb.append(" AND INS_COM_ID = :ins_com_id ");
				qc.setObject("ins_com_id", ins_com_id);
				
				qc.setQueryString(sb.toString());
				List<Map<String, Object>> list = dam.exeQuery(qc);
				int columSize = 0;
				if (null != list && list.size() > 0) {
					columSize = Integer.parseInt(list.get(0).get("COUNT").toString());
					if ("106".equals(ins_com_id) || "121".equals(ins_com_id)) {
						// 106南山、121宏利(台壽)：都需+1
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
				// 要保人ID
				String cust_id_s = "0";
				// 要保人姓名
				String cust_name_s = "0";
				// 被保人ID
				String ins_id_s = "0";
				// 被保人姓名
				String ins_name_s = "0";
				// 原幣別(累積實繳保費)
				String acum_paid_policy_fee_s = "0";
				// 主附約
				String policy_type_s = "0";
				
				// 取得檔案資料相對應欄位
				qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sb = new StringBuffer();
				sb.append(" SELECT SOURCE_FILE_TYPE, TARGET_FILE_TYPE, ");
				sb.append(" INS_COM_ID, INS_TYPE, SOUCE_COL_SEQ, TARGET_COL_SEQ ");
				sb.append(" FROM TBJSB_INS_IMP_MAP ");
				sb.append(" WHERE SOURCE_FILE_TYPE = 'JSB_AST_03' ");
				sb.append(" AND INS_COM_ID = :ins_com_id ");
				
				qc.setObject("ins_com_id", ins_com_id);
				qc.setQueryString(sb.toString());
				List<Map<String, Object>> mapList = dam.exeQuery(qc);
				
				Map<String, Integer> columMap = new HashMap<>();
				for (Map<String, Object> mapMap : mapList) {
					// 取得檔案資料相對應欄位
					columMap.put(mapMap.get("SOUCE_COL_SEQ").toString(), Integer.parseInt(mapMap.get("TARGET_COL_SEQ").toString()));
					
					// 取得『受理編號2』、『保單號碼3』、『投保商品4』、『要保人6』、『要保人ID7』、『被保險人8』、
					// 『被保險人ID9』、『原幣別(累積實繳保費)16』、『主附約21』、『保單狀態25』所在欄位
					String targetColSeq = mapMap.get("TARGET_COL_SEQ") != null ? mapMap.get("TARGET_COL_SEQ").toString() : "";
					if (targetColSeq.equals("2")) {
						acceptid_s = mapMap.get("SOUCE_COL_SEQ").toString();
					} else if (targetColSeq.equals("3")) {
						policy_nbr_s = mapMap.get("SOUCE_COL_SEQ").toString();
					} else if (targetColSeq.equals("4")) {
						policy_name_s = mapMap.get("SOUCE_COL_SEQ").toString();
					} else if (targetColSeq.equals("6")) {
						cust_name_s = mapMap.get("SOUCE_COL_SEQ").toString();
					} else if (targetColSeq.equals("7")) {
						cust_id_s = mapMap.get("SOUCE_COL_SEQ").toString();
					} else if (targetColSeq.equals("8")) {
						ins_name_s = mapMap.get("SOUCE_COL_SEQ").toString();
					} else if (targetColSeq.equals("9")) {
						ins_id_s = mapMap.get("SOUCE_COL_SEQ").toString();
					} else if (targetColSeq.equals("16")) {
						acum_paid_policy_fee_s = mapMap.get("SOUCE_COL_SEQ").toString();
					} else if (targetColSeq.equals("21")) {
						policy_type_s = mapMap.get("SOUCE_COL_SEQ").toString();
					} else if (targetColSeq.equals("25")) {
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
				sb.append(" WHERE A.TNAME = 'TBJSB_INS_BILL_SG01' ");
				sb.append(" AND (A.COLTYPE = 'VARCHAR2' OR A.COLTYPE = 'NUMBER') ");
				qc.setQueryString(sb.toString());
				List<Map<String, Object>> widthList = dam.exeQuery(qc);
				
				// 每次上傳都將『TBJSB_INS_BILL_SG01』、『TBJSB_INS_IMP_LOG』的相對應舊資料刪除
				qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sb = new StringBuffer();
				sb.append(" DELETE FROM TBJSB_INS_BILL_SG01 WHERE INS_COM_ID = :ins_com_id ");
				qc.setObject("ins_com_id", ins_com_id);
				qc.setQueryString(sb.toString());
				dam.exeUpdate(qc);
				
				qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sb = new StringBuffer();
				sb.append(" DELETE FROM TBJSB_INS_IMP_LOG WHERE DATA_DATE = :data_date ");
				sb.append(" AND INS_COM_ID = :ins_com_id AND INS_TYPE = 'BILL' ");
				sb.append(" AND IMP_TYPE = 'JSB_AST_03' ");
				qc.setObject("data_date", data_date);
				qc.setObject("ins_com_id", ins_com_id);
				qc.setQueryString(sb.toString());
				dam.exeUpdate(qc);
				
				//JDBC
				GenFileTools gft = new GenFileTools();
				Connection conn = gft.getConnection();
				conn.setAutoCommit(false);
				
				sb = new StringBuffer();
				sb.append(" INSERT INTO TBJSB_INS_BILL_SG01 ( ");
				sb.append(" DATA_MONTH, CASE_NO, POLICY_NBR, POLICY_NAME, DATA_DATE, ");
				sb.append(" CUST_NAME, CUST_ID, INS_NAME, INS_ID, INS_COM_NAME, ");
				sb.append(" PAY_TERM_YEAR, POLICY_ACTIVE_DATE, CRCY_TYPE, POLICY_ASSURE_AMT, FIRST_FEE, ");
				sb.append(" ACUM_PAID_POLICY_FEE, PROD_TYPE, CONTRACT_STATUS, PAY_TYPE, ACC_VALUE, ");
				sb.append(" POLICY_TYPE, INACTIVE_DATE, DIVI_AMT, WDRAW_AMT, STATUS_DESC, ");
				sb.append(" UPDATE_STATUS, INS_COM_ID, VERSION, CREATETIME, CREATOR, MODIFIER, LASTUPDATE ");
				sb.append(" ) VALUES ( ");
				sb.append(" ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ");
				sb.append(" ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ");
				sb.append(" ?, ?, ?, ?, ?, 'IM', ?, 0, SYSDATE, ?, ?, SYSDATE) ");
				
				PreparedStatement pstmt = conn.prepareStatement(sb.toString());
				
				int imp_row_count = 0;		// 上傳成功總筆數
				int fail_row_count = 0;		// 錯誤保單數
				int warn_row_count = 0;		// 比對有異動單數
				int flag = 0;				// 資料上傳『新增』成功筆數
				
				// for：需排除的附約名稱
				XmlInfo xmlInfo = new XmlInfo();
				Map<String, String> subExMap = xmlInfo.doGetVariable("JSB.PROD_SUB_EX", FormatHelper.FORMAT_3);
				Set<String> subExSet = new HashSet<>();
				for (int i = 1; i <= subExMap.size(); i++) {
					subExSet.add(subExMap.get(i+""));
				}
				
				Map<String, Object> acceptidMap = this.getMastMap(inputVO.getIns_com_id(), "A");
				Map<String, Object> policyNbrMap = this.getMastMap(inputVO.getIns_com_id(), "P");
				this.loadMastData(inputVO.getIns_com_id()); //載入保險公司data
				
				for (int i = 1; i < resultList.size(); i++) {
					String policy_nbr = resultList.get(i).get(policy_nbr_s);						// 保單號碼
					String acceptid = resultList.get(i).get(acceptid_s);							// 受理編號
					String soucr_status = resultList.get(i).get(soucr_status_s);					// 保單狀態(中文)
					String policy_name = resultList.get(i).get(policy_name_s);						// 中文專案名稱(保單名稱)
					String cust_id = resultList.get(i).get(cust_id_s);								// 要保人ID
					String cust_name = resultList.get(i).get(cust_name_s);							// 要保人姓名
					String ins_id = resultList.get(i).get(ins_id_s);								// 被保人ID
					String ins_name = resultList.get(i).get(ins_name_s);							// 被保人姓名
					String policy_type = resultList.get(i).get(policy_type_s);						// 主附約
					String acum_paid_policy_fee = resultList.get(i).get(acum_paid_policy_fee_s);	// 原幣別(累積實繳保費)
					
					/** 
					 * 友邦(129) 及 南山(106) 的附約不要寫入彙整檔(SG) 
					 *  1.友邦要用參數『JSB.PROD_SUB_EX』排除相同保單名稱之附約
					 *  2.南山用上傳之對帳單『主附約』欄位判斷
					 * **/
					if ("106".equals(ins_com_id) && StringUtils.isNotBlank(policy_type) && "附約".equals(policy_type.trim())) {
						// 南山用上傳之對帳單『主附約』欄位判斷
						continue;
					}
					if ("129".equals(ins_com_id) && subExSet.contains(policy_name)) {
						// 友邦要用參數『JSB.PROD_SUB_EX』排除相同保單名稱之附約
						continue;
					}
					
					/**
					 * E01: 上傳保單號碼空白
					 * E04: 保單檔無此受理序號
					 * E07: 保單檔無此保單號碼 
					 * E08: 受理編號長度錯誤
					 * W02:	比對『TBJSB_AST_INS_MAST』要保人ID及姓名不一致則寫入 (用保險公司＋保單號碼 比對)
					 * W03:	比對『TBJSB_AST_INS_MAST』被保人ID及姓名不一致則寫入 (用保險公司＋保單號碼 比對)
					 * W04:	比對『TBJSB_AST_INS_MAST』累計已繳保費，如果匯入資料比較小則寫入 (用保險公司＋保單號碼 比對) ==> 僅比對主約即可(附約不須比對)
					 * **/
					String contract_status = "";	// for 取得保單狀態代碼，存入：TBJSB_INS_BILL_SG01.CONTRACT_STATUS
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
						this.updateLog(inputVO, "E01", acceptid, policy_nbr, policy_name, soucr_status, null, null);
						fail_row_count++;
						continue;
					}
					
					if (acceptidMap == null || acceptidMap.size() == 0 || !acceptidMap.containsKey(acceptid)) {
						// E04: 保單檔無此受理序號
						this.updateLog(inputVO, "E04", acceptid, policy_nbr, policy_name, soucr_status, null, null);
						fail_row_count++;
						continue;
					}
					
					if (policyNbrMap == null || policyNbrMap.size() == 0 || !policyNbrMap.containsKey(policy_nbr)) {
						// E07: 保單檔無此保單號碼
						this.updateLog(inputVO, "E07", acceptid, policy_nbr, policy_name, soucr_status, null, null);
						fail_row_count++;
						continue;
					}
					
					// E08: 受理編號長度錯誤
					if (StringUtils.isBlank(acceptid)) {
						this.updateLog(inputVO, "E08", acceptid, policy_nbr, policy_name, soucr_status, null, null);
						fail_row_count++;
						continue;
						
					} else {
						// 受理編號上傳檢查, 欄位長度為12碼
						if (acceptid.trim().length() != 12) {
							this.updateLog(inputVO, "E08", acceptid, policy_nbr, policy_name, soucr_status, null, null);
							fail_row_count++;
							continue;
						}
					}
					
					// 取得該筆資料在『TBJSB_INS_INS_MAST_SG』對應的資料
					Map<String, Object> mastMap = new HashMap<>();
					// 參數：保險公司ID、保單號碼
					mastMap = this.getMastData(ins_com_id, policy_nbr);
					
					if (mastMap != null && mastMap.size() > 0) {
						BigDecimal policyFeeMast = mastMap.get("ACUM_PAID_POLICY_FEE") != null ? new BigDecimal(mastMap.get("ACUM_PAID_POLICY_FEE").toString()) : BigDecimal.ZERO;
						BigDecimal policyFee = StringUtils.isNotBlank(acum_paid_policy_fee) ? new BigDecimal(acum_paid_policy_fee) : BigDecimal.ZERO;
						
						Map<String, Object> dataMap = new HashMap<>();
						dataMap.put("CUST_ID", cust_id);
						dataMap.put("APPL_NAME", cust_name);
						dataMap.put("INS_ID", ins_id);
						dataMap.put("INS_NAME", ins_name);
						dataMap.put("ACUM_PAID_POLICY_FEE", policyFee);
						
						String custID 	= mastMap.get("CUST_ID")   != null ? mastMap.get("CUST_ID").toString() : "";
						String applName = mastMap.get("APPL_NAME") != null ? mastMap.get("APPL_NAME").toString() : "";
						String insID 	= mastMap.get("INS_ID")    != null ? mastMap.get("INS_ID").toString() : "";
						String insName  = mastMap.get("INS_NAME")  != null ? mastMap.get("INS_NAME").toString() : "";
						
						// 若是同一筆的要保人ID及姓名不一致且被保人ID及姓名不一致，則寫入一筆即可
						if (!custID.equals(cust_id) || !applName.equals(cust_name)) {
							// W02:	比對『TBJSB_AST_INS_MAST』要保人ID及姓名不一致則寫入(檔案一樣要吃進系統)
							this.updateLog(inputVO, "W02", acceptid, policy_nbr, policy_name, soucr_status, dataMap, mastMap);
							warn_row_count++;
							
						} else if (!insID.equals(ins_id) || !insName.equals(ins_name)) {
							// W03:	比對『TBJSB_AST_INS_MAST』被保人ID及姓名不一致則寫入(檔案一樣要吃進系統)
							this.updateLog(inputVO, "W03", acceptid, policy_nbr, policy_name, soucr_status, dataMap, mastMap);
							warn_row_count++;
						}
						
						if (policyFee.compareTo(policyFeeMast) < 0) {
							if (StringUtils.isNotBlank(policy_type)) {
								if (!"附約".equals(policy_type.trim())) {
									// W04:	比對『TBJSB_AST_INS_MAST』累計已繳保費，如果匯入資料比較小則寫入 ==> 僅比對主約即可(附約不須比對)
									this.updateLog(inputVO, "W04", acceptid, policy_nbr, policy_name, soucr_status, dataMap, mastMap);
									warn_row_count++;
								}
							} else {
								// 上傳對帳單沒有『主附約』欄位 ==> 要用商品檔排除附約（若商品名稱在參數『JSB.PROD_SUB_EX』中，則視為附約）
								// W07:	比對『TBJSB_AST_INS_MAST』累計已繳保費，如果匯入資料比較小則寫入 ==> 僅比對主約即可(附約不須比對)
								if (!subExSet.contains(policy_name)) {
									this.updateLog(inputVO, "W04", acceptid, policy_nbr, policy_name, soucr_status, dataMap, mastMap);															
									warn_row_count++;
								}
							}
						}
					}
					
					// 將上傳資料放至相對應的位置
					String[] data = new String[25];
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
						if (colNbr == 1) {
							// 對帳單年月
							pstmt.setObject(colNbr, data_month);
						
						} else if (colNbr == 4 && "78".equals(ins_com_id)) {
							// 新壽有7+2張保單號碼因編碼問題, 需寫死商品名稱
							Map<String,String> pmap = new HashMap<String,String>();
							pmap.put("1091030287", "1091030287");
							pmap.put("1093920926", "1093920926");
							pmap.put("1090630515", "1090630515");
							pmap.put("1092658083", "1092658083");
							pmap.put("1091985125", "1091985125");
							pmap.put("1091016723", "1091016723");
							pmap.put("1092925365", "1092925365");
							
							Map<String,String> pmap2 = new HashMap<String,String>();
							pmap2.put("1103130623", "1103130623");
							pmap2.put("1102967617", "1102967617");
							
							if (pmap.containsKey(policy_nbr)) {
								pstmt.setObject(colNbr, "新光人壽好鑫動利率變動型終身壽險（定期給付型）");	
								
							} else if (pmap2.containsKey(policy_nbr)) {
								pstmt.setObject(colNbr, "新光人壽安鑫富貴利率變動型終身壽險（定期給付型）");
								
							} else {								
								pstmt.setObject(colNbr, data[d]);
							}
							
						} else if (colNbr == 5) {
							// 參考日期
							pstmt.setObject(colNbr, data_date);
							
						} else if (colNbr == 13 && "85".equals(ins_com_id)) {
							// 安聯(85)幣別空白＝南非幣
							String crcy_type = data[d];
							if (StringUtils.isBlank(crcy_type)) {
								crcy_type = "南非幣";
							}
							pstmt.setObject(colNbr, crcy_type);								
							
						} else if (colNbr == 18) {
							// 保單狀態(代碼)
							pstmt.setObject(colNbr, contract_status);
							
						} else if (colNbr == 12 || colNbr == 22) {
							// 處理日期格式
							if (StringUtils.isNotBlank(data[d])) {
								// 日期欄位可能會帶0，需另外判斷
								if ("0.0".equals(data[d]) || "0".equals(data[d]) || "-".equals(data[d].trim())) {
									pstmt.setObject(colNbr, null);
								} else {
									if (data[d].length() == 8) {
										try {
//											System.out.println("==================");
//											System.out.println(data[d]);
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
							} else {
								pstmt.setObject(colNbr, data[d]);
							}
						} else {
							pstmt.setObject(colNbr, data[d]);							
						}
					}
					pstmt.setObject(26, ins_com_id);
					pstmt.setObject(27, loginID);
					pstmt.setObject(28, loginID);
					pstmt.addBatch();
					
					flag++;
					if (flag % 1000 == 0) {
						pstmt.executeBatch();
						conn.commit();
					}
				}
				pstmt.executeBatch();
				conn.commit();	
				
				// 對帳上傳時排除特定保險公司的保單號碼及受理編號(失效保單：'JSB.POLICY_ACCEPT_EX')，除時需加判斷是否為無效保單才排除
				qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sb = new StringBuffer();
				sb.append(" DELETE FROM TBJSB_INS_BILL_SG01 SG WHERE EXISTS ( ");
				sb.append(" SELECT 'X' FROM TBSYSPARAMETER PR ");
				sb.append(" WHERE PR.PARAM_TYPE = 'JSB.POLICY_ACCEPT_EX' ");
				sb.append(" AND SG.POLICY_NBR = PR.PARAM_CODE AND SG.CASE_NO = PR.PARAM_NAME ");
				sb.append(" AND SG.CONTRACT_STATUS IN ('22', '15', '25', '20', '13', '26', '21')) ");
				
				qc.setQueryString(sb.toString());
				int delNbr = dam.exeUpdate(qc);
				imp_row_count = flag-delNbr;	// 上傳成功總筆數
				
				if ("85".equals(ins_com_id)) {
					// 『安聯』停效保單 ==> ACC_VALUE 皆調整為0
					qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
					sb = new StringBuffer();
					sb.append(" UPDATE TBJSB_INS_BILL_SG01 SET ACC_VALUE = '0' WHERE INS_COM_ID = 85 AND CONTRACT_STATUS = 'IC03' ");
					qc.setQueryString(sb.toString());
					dam.exeUpdate(qc);
				}
				
				if ("137".equals(ins_com_id)) {
					// 第一金的對帳單上傳"不定期繳"要改為"彈性繳" 
					qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
					sb = new StringBuffer();
					sb.append(" UPDATE TBJSB_INS_BILL_SG01 SET PAY_TYPE = '彈性繳' WHERE INS_COM_ID = 137 AND PAY_TYPE = '不定期繳' ");
					qc.setQueryString(sb.toString());
					dam.exeUpdate(qc);
				}
				
				if ("124".equals(ins_com_id)) {
					// 安達的對帳單上傳附約繳別空白 要進行更新 主約:彈性繳->躉繳 主約:年繳/月繳->同主約
					qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
					sb = new StringBuffer();
					sb.append(" MERGE INTO TBJSB_INS_BILL_SG01 TG USING( ");
					sb.append(" SELECT POLICY_NBR,PAY_TYPE FROM TBJSB_INS_BILL_SG01   ");
					sb.append(" WHERE INS_COM_ID = 124  AND PAY_TYPE IS NOT NULL) SR ");
					sb.append(" ON (TG.POLICY_NBR = SR.POLICY_NBR) ");
					sb.append(" WHEN MATCHED THEN ");
					sb.append(" UPDATE SET TG.PAY_TYPE = CASE WHEN TG.PAY_TYPE IS NOT NULL THEN TG.PAY_TYPE WHEN SR.PAY_TYPE = '彈性繳' THEN '躉繳' ELSE SR.PAY_TYPE END ");
					qc.setQueryString(sb.toString());
					dam.exeUpdate(qc);
				}
				// INSERT 寫入『TBJSB_INS_IMP』每個『保險公司+商品類型』只留最新一筆，故每次INSERT前都先將舊的資料刪除
				// imp_row_count	上傳成功總筆數
				// fail_row_count	錯誤保單 				(加總E01~E04)
				// status_row_count	狀態異動總筆數			(加總W01)
				// status_ok_count	符合轉入契約狀況保單數	(加總非E04)
				// status_no_count	不符合轉入契約狀況保單數	(加總E04)
				qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sb = new StringBuffer();
				sb.append(" DELETE FROM TBJSB_INS_IMP WHERE INS_COM_ID = :ins_com_id AND IMP_TYPE = 'JSB_AST_03' ");
				
				qc.setObject("ins_com_id", ins_com_id);
				qc.setQueryString(sb.toString());
				dam.exeUpdate(qc);
				
				// 將『有效保單但沒上傳』的資料新增至『TBJSB_INS_IMP_LOG』並記下筆數
				int e07_row_count = 0;
				qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sb = new StringBuffer();
				sb.append(" INSERT INTO TBJSB_INS_IMP_LOG ( ");
				sb.append(" SEQ_NO, DATA_DATE, INS_COM_ID, INS_TYPE, DATA_MONTH, IMP_TYPE, ");
				sb.append(" ERROR_TYPE, CASE_NO, POLICY_NBR, POLICY_NAME, CONTRACT_STATUS, ");
				sb.append(" VERSION, CREATETIME, CREATOR, MODIFIER, LASTUPDATE) ");
				sb.append(" WITH SR AS ( ");
				sb.append(" SELECT A.ACCEPTID, A.POLICY_NBR, A.POLICY_FULL_NAME, A.CONTRACT_STATUS ");
				sb.append(" FROM TBJSB_AST_INS_MAST_SG A ");
				sb.append(" WHERE COM_ID = :ins_com_id AND CONTRACT_STATUS IN ('01', '04', '05', '10', '16') ");
				sb.append(" AND NOT EXISTS ( ");
				sb.append(" SELECT 'Y' FROM TBJSB_INS_BILL_SG01 B WHERE A.COM_ID = B.INS_COM_ID ");
				sb.append(" AND DATA_MONTH = :data_month AND A.POLICY_NBR = B.POLICY_NBR) ");
				
				// 排除日盛(對帳單)不檢核清單
				sb.append(" AND NOT EXISTS ( ");
				sb.append(" SELECT 'Y' FROM TBJSB_INS_BILL_EXCLUDE E WHERE A.COM_ID = E.COM_ID AND A.POLICY_NBR = E.POLICY_NBR AND A.ACCEPTID = E.ACCEPTID) ");
				
				sb.append(" ) ");
				sb.append(" SELECT TBJSB_INS_IMP_LOG_SEQ.nextval, ");
				sb.append(" :data_date, :ins_com_id, 'BILL', :data_month, 'JSB_AST_03', ");
				sb.append(" 'E06', SR.ACCEPTID, SR.POLICY_NBR, SR.POLICY_FULL_NAME, ");
				sb.append(" CASE WHEN SR.CONTRACT_STATUS = '01' THEN '有效' ELSE '停效' END, ");
				sb.append(" 0, SYSDATE, :loginID, :loginID, SYSDATE FROM SR ");
				
				qc.setObject("ins_com_id", ins_com_id);
				qc.setObject("data_month", data_month);
				qc.setObject("data_date" , data_date);
				qc.setObject("loginID"   , loginID);
				qc.setQueryString(sb.toString());
				e07_row_count = dam.exeUpdate(qc);
				
				qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sb = new StringBuffer();
				sb.append(" INSERT INTO TBJSB_INS_IMP ( ");
				sb.append(" DATA_DATE, INS_COM_ID, INS_TYPE, IMP_TYPE, ");
				sb.append(" IMP_ROW_COUNT, FAIL_ROW_COUNT, DATA_MONTH, E07_ROW_COUNT, ");
				sb.append(" VERSION, CREATETIME, CREATOR, MODIFIER, LASTUPDATE ");
				sb.append(" ) VALUES ( ");
				sb.append(" :data_date, :ins_com_id, 'BILL', 'JSB_AST_03', ");
				sb.append(" :imp_row_count, :fail_row_count, :data_month, :e07_row_count, ");
				sb.append(" 0, SYSDATE, :loginID, :loginID, SYSDATE) ");
				
				qc.setObject("data_date"	 , data_date);
				qc.setObject("ins_com_id"	 , Integer.parseInt(ins_com_id));
				qc.setObject("imp_row_count" , imp_row_count);
				qc.setObject("fail_row_count", fail_row_count);
				qc.setObject("data_month"	 , data_month);
				qc.setObject("e07_row_count" , e07_row_count);
				qc.setObject("loginID"		 , loginID);
				qc.setQueryString(sb.toString());
				dam.exeUpdate(qc);
				
				Map<String, Object> resultMap = new HashMap<>();
				// 保險公司	(前端)	
				// 對帳單年月	(前端)
				
				// 參考日期	(前端)轉格式	
				resultMap.put("DATA_DATE", inputVO.getData_date());
				// 上傳日期
				resultMap.put("TODAY", new Date());
				// 轉入對帳單彙整檔資料筆數
				resultMap.put("IMP_ROW_COUNT", imp_row_count);
				// 契約狀況有效但無上傳資料保單數
				resultMap.put("E07_ROW_COUNT", e07_row_count);
				// 比對有異動單數
				resultMap.put("WARN_ROW_COUNT", warn_row_count);
				// 錯誤保單數
				resultMap.put("FAIL_ROW_COUNT", fail_row_count);
				// 總計保單數
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
		JSB130InputVO inputVO = (JSB130InputVO) body;
		String loginID = (String) getUserVariable(FubonSystemVariableConsts.LOGINID);
		String ins_com_id = inputVO.getIns_com_id();
		String data_month = inputVO.getData_month();
		int update_row_count = 0;
		
		dam = this.getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		// STEP1
//		sb.append(" DELETE FROM TBJSB_INS_BILL TG WHERE EXISTS ( ");
//		sb.append(" SELECT 'X' FROM TBJSB_INS_BILL_SG01 SR ");
//		sb.append(" WHERE INS_COM_ID = :ins_com_id AND YEARMONTH = :data_month ");
//		sb.append(" AND TG.POLICY_NO = SR.POLICY_NBR AND TG.PRODUCT_NAME = SR.POLICY_NAME) ");
//		
//		qc.setObject("ins_com_id", ins_com_id);
//		qc.setObject("data_month", data_month);
//		qc.setQueryString(sb.toString());
//		dam.exeUpdate(qc);
		
//		qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
//		sb = new StringBuffer();
//		// STEP2：更新至：日盛保代_對帳單資料檔(TBJSB_INS_BILL)，以 MERGE INTO 方式寫入
//		sb.append(" MERGE INTO TBJSB_INS_BILL TG USING ( ");
//		sb.append(" SELECT A.*, B.CONTRACT_STATUS_DESCR FROM ( ");
//		sb.append(" SELECT * FROM TBJSB_INS_BILL_SG01 WHERE INS_COM_ID = :ins_com_id ");
//		sb.append(" AND DATA_MONTH = :data_month AND UPDATE_STATUS = 'IM' ");
//		
//		// 加判斷 106南山 及 129友邦 只撈主約
//		if ("106".equals(ins_com_id)) {
//			sb.append(" AND POLICY_TYPE = '主約' ");
//			
//		} else if ("129".equals(ins_com_id)) {
//			sb.append(" AND POLICY_NAME NOT IN ( ");
//			sb.append(" SELECT PARAM_NAME FROM TBSYSPARAMETER WHERE PARAM_TYPE = 'JSB.PROD_SUB_EX') ");
//		}
//		
//		sb.append(" ) A LEFT JOIN ( ");
//		sb.append(" SELECT DISTINCT CONTRACT_STATUS, FUBON_CONTRACT_STATUS, CONTRACT_STATUS_DESCR ");
//		sb.append(" FROM TBJSB_INS_STATUS) B ON A.CONTRACT_STATUS = B.CONTRACT_STATUS ");
//		sb.append(" ) SR ");
//		sb.append(" ON (TG.YEARMONTH = SR.DATA_MONTH AND TG.POLICY_NO = SR.POLICY_NBR AND TG.PRODUCT_NAME = SR.POLICY_NAME) ");
//		sb.append(" WHEN MATCHED THEN ");
//		sb.append(" UPDATE SET ");
//		sb.append(" TG.APPL_ID = SR.CUST_ID, ");
//		sb.append(" TG.APPL_NAME = SR.CUST_NAME, ");
//		sb.append(" TG.INSURED_ID = SR.INS_ID, ");
//		sb.append(" TG.INSURED_NAME = SR.INS_NAME, ");
//		sb.append(" TG.COM_NAME = SR.INS_COM_NAME, ");
//		sb.append(" TG.ACCEPTID = SR.CASE_NO, ");
//		sb.append(" TG.PRODUCTTYPE = SR.PROD_TYPE, ");
//		sb.append(" TG.POLICYSTATUS = SR.CONTRACT_STATUS_DESCR, ");
//		sb.append(" TG.EFF_DATE = SR.POLICY_ACTIVE_DATE, ");
//		sb.append(" TG.PREM_DATE = SR.DATA_DATE, ");
//		sb.append(" TG.CURRENCY = SR.CRCY_TYPE, ");
//		sb.append(" TG.ACCU_PREM_ORI = SR.ACUM_PAID_POLICY_FEE, ");
//		sb.append(" TG.ACC_VALUE_ORGD = SR.ACC_VALUE, ");
//		sb.append(" TG.POLICYMODE = SR.PAY_TYPE, ");
//		sb.append(" TG.LASTUPDATE = SYSDATE, ");
//		sb.append(" TG.MODIFIER = :loginID ");
//		sb.append(" WHEN NOT MATCHED THEN ");
//		sb.append(" INSERT ( ");
//		sb.append(" YEARMONTH, APPL_ID, APPL_NAME, INSURED_ID, INSURED_NAME, COM_NAME,POLICY_NO, ACCEPTID, PRODUCT_NAME ");
//		sb.append(" ,PRODUCTTYPE, POLICYSTATUS, EFF_DATE, PREM_DATE, CURRENCY, ACCU_PREM_ORI, ACC_VALUE_ORGD, POLICYMODE ");
//		sb.append(" ,CUST_FLG, VERSION, CREATETIME, CREATOR, MODIFIER, LASTUPDATE) ");
//		sb.append(" VALUES ( ");
//		sb.append(" :data_month, SR.CUST_ID, SR.CUST_NAME, SR.INS_ID, SR.INS_NAME, SR.INS_COM_NAME, SR.POLICY_NBR, SR.CASE_NO, SR.POLICY_NAME ");
//		sb.append(" ,SR.PROD_TYPE, SR.STATUS_DESC, SR.POLICY_ACTIVE_DATE, SR.DATA_DATE, SR.CRCY_TYPE, SR.ACUM_PAID_POLICY_FEE, SR.ACC_VALUE,PAY_TYPE ");
//		sb.append(" ,'' , 0, SYSDATE, :loginID, :loginID, SYSDATE) ");
//		
//		qc.setObject("ins_com_id", ins_com_id);
//		qc.setObject("data_month", data_month);
//		qc.setObject("loginID", loginID);
//		qc.setQueryString(sb.toString());
//		dam.exeUpdate(qc);
		
		// STEP3：更新至主檔(TBJSB_AST_INS_MAST)，以MERGE INTO 方式寫入
//		qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
//		sb = new StringBuffer();
		sb.append(" MERGE INTO TBJSB_AST_INS_MAST_SG TG USING ( ");
		sb.append(" SELECT A.*, B.FUBON_CONTRACT_STATUS, B.CONTRACT_STATUS_DESCR, ");
		sb.append(" CASE WHEN A.PAY_TYPE IS NULL OR A.PAY_TYPE = '' THEN '' ELSE COALESCE(C.PARAM_CODE,'F') END AS PARAM_CODE ");
		sb.append(" FROM TBJSB_INS_BILL_SG01 A LEFT JOIN ( ");
		sb.append(" SELECT DISTINCT CONTRACT_STATUS, FUBON_CONTRACT_STATUS, CONTRACT_STATUS_DESCR ");
		sb.append(" FROM TBJSB_INS_STATUS) B ON A.CONTRACT_STATUS = B.CONTRACT_STATUS ");
		sb.append(" LEFT JOIN TBSYSPARAMETER C ON C.PARAM_TYPE = 'PMS.PAY_YQD' AND A.PAY_TYPE = C.PARAM_NAME ");
		sb.append(" WHERE INS_COM_ID = :ins_com_id AND DATA_MONTH = :data_month AND UPDATE_STATUS = 'IM' ");
		sb.append(" ) SR ON (TG.POLICY_NBR = SR.POLICY_NBR AND SR.CASE_NO = TG.ACCEPTID AND SR.POLICY_NAME = TG.POLICY_FULL_NAME ");
		
		if ("83".equals(ins_com_id)) {
			// 加上判斷遠雄人壽(83)用 被保人ID 為KEY 值 SR.INS_ID = TG.INS_ID
			sb.append(" AND SR.INS_ID = TG.INS_ID ");
			
		} else if ("86".equals(ins_com_id)) {
			// 加上判斷全球人壽(86)用保額為KEY 值 SR.POLICY_ASSURE_AMT = TG.POLICY_ASSURE_AMT
			sb.append(" AND ((SR.POLICY_TYPE = '主約' AND 1=1) OR (SR.POLICY_TYPE = '附約' AND SR.POLICY_ASSURE_AMT = TG.POLICY_ASSURE_AMT)) ");
		}
		
		sb.append(" ) WHEN MATCHED THEN ");
		sb.append(" UPDATE SET ");
		sb.append(" TG.CUST_ID   = CASE WHEN NVL(SR.CUST_ID  , 'X') <> 'X' AND LENGTH(TG.CUST_ID) <> 11 THEN SR.CUST_ID ELSE TG.CUST_ID END, ");
		sb.append(" TG.APPL_NAME = CASE WHEN NVL(SR.CUST_NAME, 'X') <> 'X' THEN SR.CUST_NAME ELSE TG.APPL_NAME END, ");
		
		// 加判斷遠雄人壽(83)不更新被保人ID
		if (!"83".equals(ins_com_id)) {
			sb.append(" TG.INS_ID = CASE WHEN NVL(SR.INS_ID, 'X') <> 'X' THEN SR.INS_ID ELSE TG.INS_ID END, ");			
		}
		
		sb.append(" TG.INS_NAME = CASE WHEN NVL(SR.INS_NAME, 'X') <> 'X' THEN SR.INS_NAME ELSE TG.INS_NAME END, ");
		
		sb.append(" TG.PAY_TERM_YEAR = CASE WHEN SR.PAY_TERM_YEAR IS NOT NULL THEN SR.PAY_TERM_YEAR ELSE TG.PAY_TERM_YEAR END, ");
		sb.append(" TG.POLICY_ACTIVE_DATE = CASE WHEN SR.POLICY_ACTIVE_DATE IS NOT NULL THEN SR.POLICY_ACTIVE_DATE ELSE TG.POLICY_ACTIVE_DATE END, ");
//		sb.append(" TG.PAY_TERM_YEAR = SR.PAY_TERM_YEAR, ");
//		sb.append(" TG.POLICY_ACTIVE_DATE = SR.POLICY_ACTIVE_DATE, ");
		
		// 加判斷全球人壽(86)不更新保額
		if (!"86".equals(ins_com_id)) {			
			sb.append(" TG.POLICY_ASSURE_AMT = CASE WHEN SR.POLICY_ASSURE_AMT IS NOT NULL THEN SR.POLICY_ASSURE_AMT ELSE TG.POLICY_ASSURE_AMT END, ");
//			sb.append(" TG.POLICY_ASSURE_AMT = SR.POLICY_ASSURE_AMT, ");
		}
		
		sb.append(" TG.POLICY_FEE = CASE WHEN SR.FIRST_FEE IS NOT NULL THEN SR.FIRST_FEE ELSE TG.POLICY_FEE END, ");
		sb.append(" TG.ACUM_INS_AMT_ORGD = CASE WHEN SR.ACUM_PAID_POLICY_FEE IS NOT NULL THEN SR.ACUM_PAID_POLICY_FEE ELSE TG.ACUM_INS_AMT_ORGD END, ");
		sb.append(" TG.ACUM_PAID_POLICY_FEE = CASE WHEN SR.ACUM_PAID_POLICY_FEE IS NOT NULL THEN SR.ACUM_PAID_POLICY_FEE ELSE TG.ACUM_PAID_POLICY_FEE END, ");
		sb.append(" TG.ACCU_PREM = CASE WHEN SR.ACUM_PAID_POLICY_FEE IS NOT NULL THEN SR.ACUM_PAID_POLICY_FEE ELSE TG.ACCU_PREM END, ");
//		sb.append(" TG.POLICY_FEE = SR.FIRST_FEE, ");
//		sb.append(" TG.ACUM_INS_AMT_ORGD = SR.ACUM_PAID_POLICY_FEE, ");
//		sb.append(" TG.ACUM_PAID_POLICY_FEE = SR.ACUM_PAID_POLICY_FEE, ");
//		sb.append(" TG.ACCU_PREM = SR.ACUM_PAID_POLICY_FEE, ");
		sb.append(" TG.DATA_DATE = SR.DATA_DATE, ");
		sb.append(" TG.SP_POLICY_NOTE = CASE WHEN SR.PROD_TYPE = '投資型' THEN 'U' ELSE '' END, ");
		sb.append(" TG.CONTRACT_STATUS = SR.FUBON_CONTRACT_STATUS, ");
		sb.append(" TG.CONTRACT_TEXT = SR.CONTRACT_STATUS_DESCR, ");
		sb.append(" TG.PAY_TYPE = SR.PARAM_CODE, ");
		sb.append(" TG.ACC_VALUE = CASE WHEN SR.PROD_TYPE = '傳統型' THEN TG.ACC_VALUE WHEN SUBSTR(SR.CONTRACT_STATUS, 1, 2) = 'AC' THEN SR.ACC_VALUE ELSE 0 END, ");

		sb.append(" TG.CONTRACT_DATE = CASE WHEN TG.CONTRACT_STATUS <> FUBON_CONTRACT_STATUS AND SR.INACTIVE_DATE IS NOT NULL THEN SR.INACTIVE_DATE ");
		sb.append(" WHEN TG.CONTRACT_STATUS <> FUBON_CONTRACT_STATUS AND SR.INACTIVE_DATE IS NULL THEN :data_date ELSE TG.CONTRACT_DATE END, ");
		sb.append(" TG.ACUM_WDRAW_POLICY_FEE = CASE WHEN SR.WDRAW_AMT IS NOT NULL THEN SR.WDRAW_AMT ELSE TG.ACUM_WDRAW_POLICY_FEE END, ");
//		sb.append(" TG.CONTRACT_DATE = SR.INACTIVE_DATE, ");
//		sb.append(" TG.ACUM_WDRAW_POLICY_FEE = SR.WDRAW_AMT, ");
		
		sb.append(" TG.LASTUPDATE = SYSDATE, ");
		sb.append(" TG.MODIFIER = :loginID ");
		
		Timestamp data_date = new Timestamp(inputVO.getData_date().getTime());
		qc.setObject("data_date" , data_date);
		qc.setObject("ins_com_id", ins_com_id);
		qc.setObject("data_month", data_month);
		qc.setObject("loginID", loginID);
		qc.setQueryString(sb.toString());
		update_row_count = dam.exeUpdate(qc);
		
		// STEP4：更新 TBJSB_INS_BILL_SG01.UPDAT_STATUS = 'UP'
		qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sb = new StringBuffer();
		sb.append(" UPDATE TBJSB_INS_BILL_SG01 SET UPDATE_STATUS = 'UP' WHERE INS_COM_ID = :ins_com_id and DATA_MONTH = :data_month");
		qc.setObject("ins_com_id", ins_com_id);
		qc.setObject("data_month", data_month);
		qc.setQueryString(sb.toString());
		dam.exeUpdate(qc);
		
		// STEP5：更新 TBJSB_INS_IMP.UPDATE_ROW_COUNT	
		qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sb = new StringBuffer();
		sb.append(" UPDATE TBJSB_INS_IMP SET UPDATE_ROW_COUNT = :update_row_count, ");
		sb.append(" VERSION = VERSION+1, MODIFIER = :loginID, LASTUPDATE = SYSDATE ");
		sb.append(" WHERE INS_COM_ID = :ins_com_id AND INS_TYPE = 'BILL' AND IMP_TYPE = 'JSB_AST_03' ");
		
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
		JSB130InputVO inputVO = (JSB130InputVO) body;
		String exportType = inputVO.getExport_type();
		String fileName = "";     
		List<Map<String, Object>> resultList = new ArrayList<>();
		List listCSV = new ArrayList();
		CSVUtil csv = new CSVUtil();
		
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> comNameMap = xmlInfo.doGetVariable("JSB.INS_COM_NAME_03", FormatHelper.FORMAT_3);
		
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
	            String[] records = new String[23];
	            int i = 0;
	            
	            records[i]   = formatStr(map.get("CUST_NAME"));				// 要保人				CUST_NAME
	            records[++i] = formatStr(map.get("CUST_ID"));				// 要保人ID			CUST_ID
	            records[++i] = formatStr(map.get("INS_NAME"));	 			// 被保險人			INS_NAME
	            records[++i] = formatStr(map.get("INS_ID"));	 			// 被保險人ID			INS_ID
	            
	            records[++i] = formatStr(map.get("INS_COM_ID"));	 		// 保險公司			INS_COM_ID	            
	            String ins_com_id = map.get("INS_COM_ID") != null ? map.get("INS_COM_ID").toString() : "";
	            records[++i] = formatStr(comNameMap.get(ins_com_id));
	            
	            records[++i] = formatStr(map.get("POLICY_NAME"));	 		// 保險商品			POLICY_NAME
	            records[++i] = formatStr(map.get("PAY_TERM_YEAR"));	 		// 商品應繳費年期		PAY_TERM_YEAR
	            records[++i] = formatStr(map.get("POLICY_ACTIVE_DATE"));	// 生效日期			POLICY_ACTIVE_DATE
	            records[++i] = formatStr(map.get("CRCY_TYPE"));	 			// 幣別     			CRCY_TYPE
	            records[++i] = formatStr(map.get("POLICY_ASSURE_AMT"));	 	// 保額				POLICY_ASSURE_AMT
	            records[++i] = formatStr(map.get("FIRST_FEE"));	 			// 原幣別首期實繳保費	FIRST_FEE
	            records[++i] = formatStr(map.get("ACUM_PAID_POLICY_FEE"));	// 原幣別累積實繳保費	ACUM_PAID_POLICY_FEE
	            records[++i] = formatStr(map.get("DATA_DATE"));	 			// 參考日期			DATA_DATE
	            records[++i] = formatStr(map.get("PROD_TYPE"));	 			// 保險類型			PROD_TYPE
	            records[++i] = formatStr(map.get("POLICY_NBR"));	 		// 保單號碼			POLICY_NBR
	            records[++i] = formatStr(map.get("CASE_NO"));	 			// 受理編號			CASE_NO
	            records[++i] = formatStr(map.get("CONTRACT_STATUS_DESCR"));	// 保單狀態			CONTRACT_STATUS_DESCR
	            records[++i] = formatStr(map.get("PAY_TYPE"));	 			// 繳別				PAY_TYPE
	            
	            // 上傳對帳單僅『投資型』會有保價，傳統型保價不應該有值
	            if (map.get("PROD_TYPE") != null && "投資型".equals(map.get("PROD_TYPE").toString().trim())) {
	            	records[++i] = formatStr(map.get("ACC_VALUE"));	 		// 原幣別保單價值		ACC_VALUE
	            } else {
	            	records[++i] = "";	 									// 原幣別保單價值		ACC_VALUE
	            }
	            records[++i] = formatStr(map.get("POLICY_TYPE"));	 		// 主附約				POLICY_TYPE
	            records[++i] = formatStr(map.get("INACTIVE_DATE"));	 		// 停效/失效日期		INACTIVE_DATE
	            records[++i] = formatStr(map.get("WDRAW_AMT"));	 			// 投資型累計部份提領	WDRAW_AMT

	            listCSV.add(records);
	        }
			csv.setHeader(new String[]{"要保人", "要保人ID", "被保險人", "被保險人ID", "保險公司", 
									   "保險公司", "保險商品", "商品應繳費年期", "生效日期", "幣別", 
									   "保額", "原幣別首期實繳保費", "原幣別累積實繳保費", "參考日期", "保險類型", 
									   "保單號碼", "受理編號", "保單狀態", "繳別", "原幣別保單價值", 
									   "主附約", "停效/失效日期", "投資型累計部份提領"});
			
		} else {
			if ("E06".equals(exportType)) {
				// 契約狀況有效但無上傳資料保單數
				fileName = "契約狀況有效但無上傳資料保單";
				resultList = this.getError(inputVO, exportType);
			} else if ("W".equals(exportType)) {
				// 比對有差異單數
				fileName = "比對有差異保單";
				resultList = this.getError(inputVO, exportType);
			} else {
				// 錯誤保單數
				fileName = "錯誤保單";
				resultList = this.getError(inputVO, null);
			}
			
			Map<String, String> errorTypeMap = xmlInfo.doGetVariable("JSB.IMP_ERROR_TYPE", FormatHelper.FORMAT_3);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
			for (Map<String, Object> map : resultList) {
				String[] records = new String[7];
				if ("W".equals(exportType)) {
					records = new String[17];
				}
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
	            // IMP_TYPE		上傳檔案
	            records[++i] = "對帳單";
	            // ERROR_TYPE	錯誤代碼
	            String error_type = map.get("ERROR_TYPE") != null ? map.get("ERROR_TYPE").toString() : "";
	            records[++i] = formatStr(errorTypeMap.get(error_type));
	            records[++i] = formatStr(map.get("CASE_NO"));		// CASE_NO		受理編號
	            records[++i] = formatStr(map.get("POLICY_NBR"));	// POLICY_NBR	保單號碼
	            records[++i] = formatStr(map.get("POLICY_NAME"));	// POLICY_NAME	投保商品
	            
	            if ("W".equals(exportType)) {
	            	records[++i] = formatStr(map.get("ACUM_PAID_POLICY_FEE"));		// ACUM_PAID_POLICY_FEE		累計已繳保費
		            records[++i] = formatStr(map.get("PRE_ACUM_PAID_POLICY_FEE"));	// PRE_ACUM_PAID_POLICY_FEE	前次累計已繳保費
	            	records[++i] = formatStr(map.get("CUST_ID"));					// CUST_ID	　		要保人ID
	            	records[++i] = formatStr(map.get("PRE_CUST_ID"));				// PRE_CUST_ID		前次要保人ID
	            	records[++i] = formatStr(map.get("CUST_NAME"));					// CUST_NAME	　	要保人姓名
	            	records[++i] = formatStr(map.get("PRE_CUST_NAME"));				// PRE_CUST_NAME	前次要保人姓名
	            	records[++i] = formatStr(map.get("INS_ID"));					// INS_ID	　		被保險人ID
	            	records[++i] = formatStr(map.get("PRE_INS_ID"));				// PRE_INS_ID	　	前次被保險人ID
	            	records[++i] = formatStr(map.get("INS_NAME"));					// INS_NAME	　		被保險人姓名
	            	records[++i] = formatStr(map.get("PRE_INS_NAME"));				// PRE_INS_NAME	　	前次被保險人姓名
	            }
	            
	            listCSV.add(records);
			}
			if ("W".equals(exportType)) {
				csv.setHeader(new String[]{"參考日期", "保險公司", "上傳檔案", "錯誤代碼", "受理編號", "保單號碼", "投保商品", "累計已繳保費", "前次累計已繳保費",
										   "要保人ID", "前次要保人ID", "要保人姓名", "前次要保人姓名", "被保險人ID", "前次被保險人ID", "被保險人姓名", "前次被保險人姓名"});
			} else {
				csv.setHeader(new String[]{"參考日期", "保險公司", "上傳檔案", "錯誤代碼", "受理編號", "保單號碼", "投保商品"});				
			}
		}
		csv.addRecordList(listCSV);
        String url = csv.generateCSV();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        fileName = fileName + "_" + sdf.format(new Date())+ ".csv";
        notifyClientToDownloadFile(url, fileName);
        this.sendRtnObject(null);
	}
	
	public void exportUP (Object body, IPrimitiveMap header) throws Exception {
		JSB130InputVO inputVO = (JSB130InputVO) body;
		List listCSV = new ArrayList();
		CSVUtil csv = new CSVUtil();
		String fileName = "";
		
		dam = this.getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		sb.append(" SELECT APPL_NAME, CUST_ID, INS_NAME, INS_ID, COM_ID, COM_NAME, ");
		sb.append(" POLICY_FULL_NAME, PAY_TERM_YEAR, POLICY_ACTIVE_DATE, CRCY_TYPE, ");
		sb.append(" POLICY_ASSURE_AMT, POLICY_FEE, ACUM_PAID_POLICY_FEE, DATA_DATE, ");
		sb.append(" SP_POLICY_NOTE, POLICY_NBR, ACCEPTID, CONTRACT_TEXT, PAY_TYPE, ");
		sb.append(" ACC_VALUE, MAST_SLA_TYPE, CONTRACT_DATE, ACUM_WDRAW_POLICY_FEE ");
		sb.append(" FROM TBJSB_AST_INS_MAST_SG WHERE 1 = 1 ");
		
		if (StringUtils.isNotBlank(inputVO.getIns_com_id())) {
			XmlInfo xmlInfo = new XmlInfo();
			Map<String, String> comNameMap = xmlInfo.doGetVariable("JSB.INS_COM_NAME_03", FormatHelper.FORMAT_3);
			String comName = comNameMap.get(inputVO.getIns_com_id());
			fileName = comName + "保險公司更新保單資料";
			
			sb.append(" AND DATA_DATE = :data_date ");			
			sb.append(" AND COM_ID = :ins_com_id ");	
			
			qc.setObject("data_date", inputVO.getData_date());
			qc.setObject("ins_com_id", inputVO.getIns_com_id());

		} else {
			fileName = "全部更新保單檔資料";
			
			sb.append(" AND TO_CHAR(DATA_DATE, 'YYYYMM') = TO_CHAR(SYSDATE, 'YYYYMM') ");
		}
		qc.setQueryString(sb.toString());
		List<Map<String, Object>> resultList = dam.exeQuery(qc);
		
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> payTypeMap = xmlInfo.doGetVariable("PMS.PAY_YQD", FormatHelper.FORMAT_3);
		
		for (Map<String, Object> map : resultList) {
            String[] records = new String[23];
            int i = 0;
            records[i]   = formatStr(map.get("APPL_NAME"));					// 要保人				APPL_NAME
            records[++i] = formatStr(map.get("CUST_ID"));					// 要保人ID			CUST_ID
            records[++i] = formatStr(map.get("INS_NAME"));					// 被保險人			INS_NAME
            records[++i] = formatStr(map.get("INS_ID"));					// 被保險人ID			INS_ID
            records[++i] = formatStr(map.get("COM_ID"));					// 保險公司			COM_ID
            records[++i] = formatStr(map.get("COM_NAME"));					// 保險公司			COM_NAME
            records[++i] = formatStr(map.get("POLICY_FULL_NAME"));			// 保險商品			POLICY_FULL_NAME
            records[++i] = formatStr(map.get("PAY_TERM_YEAR"));				// 商品應繳費年期		PAY_TERM_YEAR
            records[++i] = formatStr(map.get("POLICY_ACTIVE_DATE"));		// 生效日期			POLICY_ACTIVE_DATE
            records[++i] = formatStr(map.get("CRCY_TYPE"));					// 幣別     			CRCY_TYPE
            records[++i] = formatStr(map.get("POLICY_ASSURE_AMT"));			// 保額				POLICY_ASSURE_AMT
            records[++i] = formatStr(map.get("POLICY_FEE"));				// 原幣別首期實繳保費	POLICY_FEE
            records[++i] = formatStr(map.get("ACUM_PAID_POLICY_FEE"));		// 原幣別累積實繳保費	ACUM_PAID_POLICY_FEE
            records[++i] = formatStr(map.get("DATA_DATE"));					// 參考日期			DATA_DATE
            
            if (map.get("SP_POLICY_NOTE") != null && "U".equals(map.get("SP_POLICY_NOTE").toString())) {
            	records[++i] = formatStr("投資型");
            } else {
            	records[++i] = formatStr("傳統型");
            }
//          records[++i] = formatStr(map.get("SP_POLICY_NOTE"));			// 保險類型			SP_POLICY_NOTE（U:投資型、空值:傳統型）
            
            records[++i] = formatStr(map.get("POLICY_NBR"));				// 保單號碼			POLICY_NBR
            records[++i] = formatStr(map.get("ACCEPTID"));					// 受理編號			ACCEPTID
            records[++i] = formatStr(map.get("CONTRACT_TEXT"));				// 保單狀態			CONTRACT_TEXT
            
            String pay_type = map.get("PAY_TYPE") == null ? "" : payTypeMap.get(map.get("PAY_TYPE"));
            records[++i] = formatStr(pay_type);								// 繳別				PAY_TYPE
            
            records[++i] = formatStr(map.get("ACC_VALUE"));					// 原幣別保單價值		ACC_VALUE
            records[++i] = formatStr(map.get("MAST_SLA_TYPE"));				// 主附約				MAST_SLA_TYPE
            records[++i] = formatStr(map.get("CONTRACT_DATE"));				// 停效/失效日期		CONTRACT_DATE
            records[++i] = formatStr(map.get("ACUM_WDRAW_POLICY_FEE"));		// 投資型累計部份提領	ACUM_WDRAW_POLICY_FEE
            listCSV.add(records);
        }
		csv.setHeader(new String[]{"要保人", "要保人ID", "被保險人", "被保險人ID", "保險公司", 
								   "保險公司", "保險商品", "商品應繳費年期", "生效日期", "幣別", 
								   "保額", "原幣別首期實繳保費", "原幣別累積實繳保費", "參考日期", "保險類型", 
								   "保單號碼", "受理編號", "保單狀態", "繳別", "原幣別保單價值", 
								   "主附約", "停效/失效日期", "投資型累計部份提領"});
		
		csv.addRecordList(listCSV);
        String url = csv.generateCSV();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        fileName = fileName + "_" + sdf.format(new Date())+ ".csv";
        notifyClientToDownloadFile(url, fileName);
        this.sendRtnObject(null);
	}
	
	// 上傳後結果檢視，按取消，資料不會彙入匯整檔中，如當月有上傳過，將清空重新上傳
	public void cancel (Object body, IPrimitiveMap header) throws Exception {
		JSB130InputVO inputVO = (JSB130InputVO) body;
		String ins_com_id = inputVO.getIns_com_id();
		Timestamp data_date = new Timestamp(inputVO.getData_date().getTime());
		
		// 清空該保險公司在 TBJSB_INS_IMP、TBJSB_INS_BILL_SG01、TBJSB_INS_IMP_LOG 的資料
		dam = this.getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		sb.append(" DELETE FROM TBJSB_INS_IMP WHERE INS_COM_ID = :ins_com_id AND IMP_TYPE = 'JSB_AST_03' ");
		qc.setObject("ins_com_id", ins_com_id);
		qc.setQueryString(sb.toString());
		dam.exeUpdate(qc);
		
		
		qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sb = new StringBuffer();
		sb.append(" DELETE FROM TBJSB_INS_BILL_SG01 WHERE INS_COM_ID = :ins_com_id ");
		qc.setObject("ins_com_id", ins_com_id);
		qc.setQueryString(sb.toString());
		dam.exeUpdate(qc);
		
		qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sb = new StringBuffer();
		sb.append(" DELETE FROM TBJSB_INS_IMP_LOG WHERE DATA_DATE = :data_date ");
		sb.append(" AND INS_COM_ID = :ins_com_id AND INS_TYPE = 'BILL' ");
		sb.append(" AND IMP_TYPE = 'JSB_AST_03' ");
		qc.setObject("data_date", data_date);
		qc.setObject("ins_com_id", ins_com_id);
		qc.setQueryString(sb.toString());
		dam.exeUpdate(qc);
		
		this.sendRtnObject(null);
	}
	
	private List<Map<String, Object>> getOK(JSB130InputVO inputVO) throws JBranchException {
		List<Map<String, Object>> OKList = new ArrayList<>();
		dam = this.getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		sb.append(" SELECT CUST_NAME, CUST_ID, INS_NAME, INS_ID, INS_COM_ID, ");
		sb.append(" POLICY_NAME, PAY_TERM_YEAR, POLICY_ACTIVE_DATE, CRCY_TYPE, ");
		sb.append(" POLICY_ASSURE_AMT, FIRST_FEE, ACUM_PAID_POLICY_FEE, ");
		sb.append(" DATA_DATE, PROD_TYPE, POLICY_NBR, CASE_NO, ");
		sb.append(" B.CONTRACT_STATUS, STATUS_DESC, S.CONTRACT_STATUS_DESCR, ");
		sb.append(" PAY_TYPE, ACC_VALUE, POLICY_TYPE, INACTIVE_DATE, WDRAW_AMT ");
		sb.append(" FROM TBJSB_INS_BILL_SG01 B LEFT JOIN ( ");
		sb.append(" SELECT DISTINCT CONTRACT_STATUS, CONTRACT_STATUS_DESCR FROM TBJSB_INS_STATUS ");
		sb.append(" ) S ON B.CONTRACT_STATUS = S.CONTRACT_STATUS ");
		sb.append(" WHERE 1 = 1 ");
		
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
	
	private List<Map<String, Object>> getError(JSB130InputVO inputVO, String errType) throws JBranchException {
		List<Map<String, Object>> errorList = new ArrayList<>();
		dam = this.getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		sb.append(" SELECT * FROM TBJSB_INS_IMP_LOG WHERE DATA_DATE = :data_date ");
		sb.append(" AND INS_COM_ID = :ins_com_id AND INS_TYPE = 'BILL' AND IMP_TYPE = 'JSB_AST_03' ");
		
		if (StringUtils.isNotBlank(errType)) {
			if ("W".equals(errType)) {
				sb.append(" AND ERROR_TYPE LIKE 'W%' ");
			} else {
				sb.append(" AND ERROR_TYPE = :error_type ");
				qc.setObject("error_type", errType);				
			}
		} else {
			sb.append(" AND ERROR_TYPE LIKE 'E%' AND ERROR_TYPE <> 'E06' ");
		}
		qc.setObject("data_date", new Timestamp(inputVO.getData_date().getTime()));
		qc.setObject("ins_com_id", inputVO.getIns_com_id());
		qc.setQueryString(sb.toString());
		errorList = dam.exeQuery(qc);
		return errorList;
	}
	
	// 更新：TBJSB_INS_IMP_LOG（日盛保單匯入錯誤資訊檔）
	// 參數：inputVO、錯誤代碼、受理編號、保單號碼、投保商品、保單狀態(中文)
	private void updateLog(JSB130InputVO inputVO, String error_type, String case_no, String policy_nbr, String policy_name, String contract_status, 
							Map<String, Object> dataMap, Map<String, Object> mastMap) throws JBranchException {
		
		boolean mastFlag = null != mastMap && mastMap.size() > 0;
		String loginID = (String) getUserVariable(FubonSystemVariableConsts.LOGINID);
		dam = this.getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		sb.append(" INSERT INTO TBJSB_INS_IMP_LOG ( ");
		sb.append(" SEQ_NO, DATA_DATE, INS_COM_ID, INS_TYPE, IMP_TYPE, ");
		sb.append(" ERROR_TYPE, CASE_NO, POLICY_NBR, POLICY_NAME, CONTRACT_STATUS, ");
		if (mastFlag) {
			sb.append(" CUST_ID, PRE_CUST_ID, ");
			sb.append(" CUST_NAME, PRE_CUST_NAME, ");
			sb.append(" INS_ID, PRE_INS_ID, ");
			sb.append(" INS_NAME, PRE_INS_NAME, ");
			sb.append(" ACUM_PAID_POLICY_FEE, PRE_ACUM_PAID_POLICY_FEE, ");
		}
		sb.append(" VERSION, CREATETIME, CREATOR, MODIFIER, LASTUPDATE ");
		sb.append(" ) VALUES ( ");
		sb.append(" :seq_no, :data_date, :ins_com_id, 'BILL', 'JSB_AST_03', ");
		sb.append(" :error_type, :case_no, :policy_nbr, :policy_name, :contract_status, ");
		if (mastFlag) {
			sb.append(" :cust_id, :pre_cust_id, ");
			sb.append(" :cust_name, :pre_cust_name, ");
			sb.append(" :ins_id, :pre_ins_id, ");
			sb.append(" :ins_name, :pre_ins_name, ");
			sb.append(" :acum_paid_policy_fee, :pre_acum_paid_policy_fee, ");
		}
		sb.append(" 0, SYSDATE, :loginID, :loginID, SYSDATE) ");
		
		qc.setObject("seq_no", this.getSN());
		qc.setObject("data_date", new Timestamp(inputVO.getData_date().getTime()));
		qc.setObject("ins_com_id", inputVO.getIns_com_id());
		qc.setObject("error_type", error_type);
		qc.setObject("case_no", case_no);
		qc.setObject("policy_nbr", policy_nbr);
		qc.setObject("policy_name", policy_name);
		qc.setObject("contract_status", contract_status);
		
		if (mastFlag) {
			qc.setObject("cust_id"				, dataMap.get("CUST_ID"));
			qc.setObject("cust_name"			, dataMap.get("APPL_NAME"));
			qc.setObject("ins_id"				, dataMap.get("INS_ID"));
			qc.setObject("ins_name"				, dataMap.get("INS_NAME"));
			qc.setObject("acum_paid_policy_fee"	, dataMap.get("ACUM_PAID_POLICY_FEE"));
			
			qc.setObject("pre_cust_id"				, mastMap.get("CUST_ID"));
			qc.setObject("pre_cust_name"			, mastMap.get("APPL_NAME"));
			qc.setObject("pre_ins_id"				, mastMap.get("INS_ID"));
			qc.setObject("pre_ins_name"				, mastMap.get("INS_NAME"));
			qc.setObject("pre_acum_paid_policy_fee"	, mastMap.get("ACUM_PAID_POLICY_FEE"));
		}
		
		qc.setObject("loginID", loginID);
		
		qc.setQueryString(sb.toString());
		dam.exeUpdate(qc);
	}
	
	@SuppressWarnings("unchecked")
	private void loadMastData(String com_id) throws JBranchException {
		// 判斷該筆資料保單狀態是否與『TBJSB_INS_INS_MAST』保單狀態欄位是否相同
		dam = this.getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();

		sb.append(" SELECT M.COM_ID, M.ACCEPTID, M.POLICY_NBR, M.POLICY_SIMP_NAME, ");
		sb.append(" M.CONTRACT_STATUS, M.CONTRACT_TEXT, M.CONTRACT_DATE, ");
		sb.append(" M.CUST_ID, M.APPL_NAME, M.INS_NAME, M.INS_ID, M.ACUM_PAID_POLICY_FEE ");
		sb.append(" FROM TBJSB_AST_INS_MAST_SG M WHERE M.MAST_SLA_TYPE = 'Y' ");
		sb.append(" AND M.COM_ID = :com_id ");
		
		qc.setObject("com_id", com_id);

		qc.setQueryString(sb.toString());
		List<Map<String, Object>> mastList = dam.exeQuery(qc);
		if (null != mastList && mastList.size() > 0) {
			for (Map<String,Object> map : mastList) {
				mastMapData.put(ObjectUtils.toString(map.get("ACCEPTID"))+ObjectUtils.toString(map.get("POLICY_NBR")), map);
			}
		}
	}
	
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