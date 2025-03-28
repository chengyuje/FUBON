package com.systex.jbranch.app.server.fps.pms227;

import java.io.File;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import jxl.Sheet;
import jxl.Workbook;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.fubon.bth.GenFileTools;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.query.ResultIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.CSVUtil;
import com.systex.jbranch.platform.common.util.StringUtil;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * @Description : 個別商品轉介查詢
 * @Author      : 20161112 zhouyiqiong
 * @Editor      : 20161112 zhouyiqiong
 * @Editor      : 20220523 ocean	   : WMS-CR-20220517-01_新增實動戶上傳註記
 */
@Component("pms227")
@Scope("request")
public class PMS227 extends FubonWmsBizLogic {

	private DataAccessManager dam = null;
	private static String yeaMon = new SimpleDateFormat("yyyyMM").format(new Date()); //預設這個月分
	private static String finalstring;
	private Logger logger = LoggerFactory.getLogger(PMS227.class);

	/** 上傳-個別商品轉介 **/
	public void importData(Object body, IPrimitiveMap header) throws Exception {

		PMS227InputVO inputVO = (PMS227InputVO) body;
		PMS227OutputVO outputVO = new PMS227OutputVO();
		dam = this.getDataAccessManager();

		//JDBC
		GenFileTools gft = new GenFileTools();
		Connection conn = gft.getConnection();
		conn.setAutoCommit(false);

		StringBuffer sb = new StringBuffer();
		sb.append("INSERT INTO TBPMS_PRD_REC_U (");
		sb.append("  RPT_YM, ");
		sb.append("  REF_YM, ");
		sb.append("  CUST_ID, ");
		sb.append("  TP_YM, ");
		sb.append("  INV_FEE, ");
		sb.append("  INS_FEE, ");
		sb.append("  REF_BONUS, ");
		sb.append("  ADJ_TYPE, ");
		sb.append("  RNUM, ");
		sb.append("  VERSION, ");
		sb.append("  CREATETIME, ");
		sb.append("  CREATOR, ");
		sb.append("  MODIFIER, ");
		sb.append("  LASTUPDATE");
		sb.append(")");
		sb.append("VALUES (");
		sb.append("  ? ");
		sb.append("  ?, ");
		sb.append("  ?, ");
		sb.append("  ?, ");
		sb.append("  ?, ");
		sb.append("  ?, ");
		sb.append("  ?, ");
		sb.append("  '1', ");
		sb.append("  ?, ");
		sb.append("  0, ");
		sb.append("  sysdate, ");
		sb.append("  ?, ");
		sb.append("  ?, ");
		sb.append("  sysdate");
		sb.append(")");

		PreparedStatement pstmt = conn.prepareStatement(sb.toString());

		int flag = 0;

		try {
			List<String> list = new ArrayList<String>();
			String joinedPath = new File((String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH), inputVO.getFileName()).toString();
			Workbook workbook = Workbook.getWorkbook(new File(joinedPath));
			Sheet sheet[] = workbook.getSheets();
			String lab = null;

			//清空臨時表

			QueryConditionIF dcon = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			String dsql = " TRUNCATE TABLE TBPMS_PRD_REC_U ";
			dcon.setQueryString(dsql.toString());
			dam.exeUpdate(dcon);

			for (int a = 0; a < sheet.length; a++) {
				for (int i = 1; i < sheet[a].getRows(); i++) {
					for (int j = 0; j < sheet[a].getColumns(); j++) {
						lab = sheet[a].getCell(j, i).getContents();
						list.add(lab);
					}

					//判斷當前上傳數據欄位個數是否一致
					if (list.size() != 6) {
						throw new APException("上傳數據欄位個數不一致");
					}

					pstmt.setString(1, inputVO.getsTime());
					pstmt.setString(2, list.get(0));
					pstmt.setString(3, list.get(1));
					pstmt.setString(4, list.get(2));
					pstmt.setString(5, list.get(3));
					pstmt.setString(6, list.get(4));
					pstmt.setString(7, list.get(5));
					pstmt.setString(8, String.valueOf(flag));
					pstmt.setString(9, (String) getUserVariable(FubonSystemVariableConsts.LOGINID));
					pstmt.setString(10, (String) getUserVariable(FubonSystemVariableConsts.LOGINID));

					pstmt.addBatch();

					flag++;

					if (flag % 1000 == 0) {
						pstmt.executeBatch();
						conn.commit();
					}

					list.clear();
				}

				pstmt.executeBatch();
				conn.commit();
			}

			//資料上傳成功
			outputVO.setFlag(flag);
			this.sendRtnObject(outputVO);
		} catch (Exception e) {
			if (conn != null)
				try {
					conn.rollback();
				} catch (Exception e2) {
				}
			logger.error("發生錯誤:%s", StringUtil.getStackTraceAsString(e));
			throw new APException("資料上傳失敗,錯誤發生在第" + (flag + 1) + "筆," + e.getMessage());
		} finally {
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (Exception e) {
				}
			if (conn != null)
				try {
					conn.close();
				} catch (Exception e) {
				}
		}
	}

	/** 查詢-個別商品轉介 **/
	public void inquire(Object body, IPrimitiveMap header) throws JBranchException {
		
		PMS227InputVO inputVO = (PMS227InputVO) body;
		PMS227OutputVO outputVO = new PMS227OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		
		try {
			sql.append("SELECT RPT_YM, ");
			sql.append("       REF_YM, ");
			sql.append("       REF_DATE, ");
			sql.append("       CASE_SEQ, ");
			sql.append("       REGION_CENTER_ID, ");
			sql.append("       REGION_CENTER_NAME, ");
			sql.append("       BRANCH_AREA_ID, ");
			sql.append("       BRANCH_AREA_NAME, ");
			sql.append("       BRANCH_NBR, ");
			sql.append("       BRANCH_NAME, ");
			sql.append("       REF_SR, ");
			sql.append("       D.PARAM_NAME AS REF_SR_NAME, ");
			sql.append("       REF_ID, ");
			sql.append("       REF_NAME, ");
			sql.append("       REF_PROD, ");
			sql.append("       S.PARAM_NAME AS REF_PROD_NAME, ");
			sql.append("       CUST_ID, ");
			sql.append("       CUST_NAME, ");
			sql.append("       USER_ID, ");
			sql.append("       USER_NAME, ");
			sql.append("       TP_ID, ");
			sql.append("       TP_AO_CODE, ");
			sql.append("       TP_YM, ");
			sql.append("       INV_FEE, ");
			sql.append("       INS_FEE, ");
			sql.append("       IS_RM_CODE, ");
			sql.append("       IS_BONUS, ");
			sql.append("       REF_BONUS, ");
			sql.append("       ADJ_TYPE, ");
			sql.append("       T.VERSION, ");
			sql.append("       T.CREATETIME, ");
			sql.append("       T.CREATOR, ");
			sql.append("       T.MODIFIER, ");
			sql.append("       T.LASTUPDATE ");
			sql.append("FROM  TBPMS_PRD_REC T ");
			sql.append("LEFT JOIN TBSYSPARAMETER S ON S.PARAM_TYPE = 'CAM.REF_PROD' AND S.PARAM_CODE = T.REF_PROD ");
			sql.append("LEFT JOIN TBSYSPARAMETER D ON D.PARAM_TYPE = 'CAM.REF_SALES_ROLE' AND D.PARAM_CODE = T.REF_SR ");
			sql.append("WHERE 1 = 1 ");

			if (!StringUtils.isBlank(inputVO.getRegion())) {
				sql.append("AND REGION_CENTER_ID = :regionCenter ");
				condition.setObject("regionCenter", inputVO.getRegion());
			}
			
			if (!StringUtils.isBlank(inputVO.getOp())) {
				sql.append("AND BRANCH_AREA_ID = :branchArea ");
				condition.setObject("branchArea", inputVO.getOp());
			}
			
			if (!StringUtils.isBlank(inputVO.getBranch())) {
				sql.append("AND BRANCH_NBR = :branchNbr ");
				condition.setObject("branchNbr", inputVO.getBranch());
			}
			
			if (!StringUtils.isBlank(inputVO.getREF_ID())) {
				sql.append("AND REF_ID = :refid ");
				condition.setObject("refid", inputVO.getREF_ID());
			}
			
			if (!StringUtils.isBlank(inputVO.getTP_ID())) {
				sql.append("AND TP_ID = :tpid ");
				condition.setObject("tpid", inputVO.getTP_ID());
			}
			
			if (!StringUtils.isBlank(inputVO.getsTime())) {
				sql.append("AND TRIM(RPT_YM) = :sTime ");
				condition.setObject("sTime", inputVO.getsTime().trim());
			}
			
			sql.append("ORDER BY T.REF_YM,T.CUST_ID,T.BRANCH_NBR,T.TP_ID ");
			
			condition.setQueryString(sql.toString());
			
			ResultIF list = dam.executePaging(condition, inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());
			
			outputVO.setResultList(list);
			outputVO.setCsvList(dam.exeQuery(condition));
			outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());// 當前頁次
			outputVO.setTotalPage(list.getTotalPage());					// 總頁次
			outputVO.setTotalRecord(list.getTotalRecord());				// 總筆數
			
			this.sendRtnObject(outputVO);
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s", StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}

	/** 匯出-個別商品轉介 **/
	public void export(Object body, IPrimitiveMap header) throws JBranchException {
		
		PMS227OutputVO return_VO2 = (PMS227OutputVO) body;
		List<Map<String, Object>> list = return_VO2.getList();
		
		String[] csvHeader = {"轉介年月", "業務處代碼", "業務處", "營運區代碼", "營運區", "分行代碼", "分行", 
			  	  			  "轉介案件編號", "轉介日期", "轉介人身份", "轉介人員編", "轉介人姓名", "轉介商品", 
			  	  			  "客戶ID", "客戶姓名", 
			  	  			  "受轉介人員編", "受轉介人姓名", 
			  	  			  "計績理專員編", "計績AO_CODE", "計績年月", 
			  	  			  "首日成交投資收益", "首日成交保險收益", "客戶當月是否有RM CODE", "是否符合領獎資格", "轉介獎金", 
							  "資料來源"};
		String[] csvMain   = {"REF_YM", "REGION_CENTER_ID", "REGION_CENTER_NAME", "BRANCH_AREA_ID", "BRANCH_AREA_NAME", "BRANCH_NBR", "BRANCH_NAME", 
							  "CASE_SEQ", "REF_DATE", "REF_SR_NAME", "REF_ID", "REF_NAME", "REF_PROD_NAME", 
							  "CUST_ID", "CUST_NAME", 
							  "USER_ID", "USER_NAME", 
							  "TP_ID", "TP_AO_CODE", "TP_YM", 
							  "INV_FEE", "INS_FEE", "IS_RM_CODE", "IS_BONUS", "REF_BONUS", 
							  "ADJ_TYPE"};
		
		String fileName = "個別商品轉介查詢_" + (new SimpleDateFormat("yyyyMMddHH:mm:ss")).format(new Date()) + ".csv";
		List<Object[]> csvData = new ArrayList<Object[]>();
		if (list.size() > 0) {
			for (Map<String, Object> map : list) {
				String[] records = new String[csvHeader.length];
				for (int i = 0; i < csvHeader.length; i++) {
					switch (csvMain[i]) {
						case "REGION_CENTER_ID":
						case "BRANCH_AREA_ID":
						case "BRANCH_NBR":
						case "CASE_SEQ":
						case "REF_ID":
						case "REF_PROD_NAME":
						case "CUST_ID":
						case "USER_ID":
						case "TP_ID":
						case "TP_AO_CODE":
							records[i] = checkIsNullAndTrans(map, csvMain[i]);
							break;
						case "ADJ_TYPE":
							records[i] = checkIsNullType(map, csvMain[i]);
							break;
						default:
							records[i] = checkIsNull(map, csvMain[i]);
							break;
					}
				}

				csvData.add(records);
			}

			CSVUtil csv = new CSVUtil();
			csv.setHeader(csvHeader);
			csv.addRecordList(csvData);
			notifyClientToDownloadFile(csv.generateCSV(), fileName); //download
		} else {
			return_VO2.setResultList(list);
			this.sendRtnObject(return_VO2);
		}
	}

	/** 上傳-有投保客戶名單/實動戶名單 **/
	public void importCustData(Object body, IPrimitiveMap header) throws Exception {

		PMS227InputVO inputVO = (PMS227InputVO) body;
		PMS227OutputVO outputVO = new PMS227OutputVO();

		GenFileTools gft = new GenFileTools();
		Connection conn = gft.getConnection();
		conn.setAutoCommit(false);
		
		StringBuffer sb = new StringBuffer();
		PreparedStatement pstmt = null;
		
		Path path = Paths.get(new File((String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH), inputVO.getFileName()).toString());
		List<String> lines = FileUtils.readLines(new File(path.toString()), "big5");
		PreparedStatement pstmtTruncate;
		
		switch (inputVO.getUploadName()) {
			case "importCustData" :
				pstmtTruncate = conn.prepareStatement("TRUNCATE TABLE TBPMS_NOINS_CUST_U ");
				pstmtTruncate.execute();
				conn.commit();

				sb = new StringBuffer();
				sb.append("INSERT INTO TBPMS_NOINS_CUST_U ( ");
				sb.append("  DATA_YM, ");
				sb.append("  CUST_NO, ");
				sb.append("  RNUM, ");
				sb.append("  VERSION, CREATETIME, CREATOR, MODIFIER, LASTUPDATE ");
				sb.append(") ");
				sb.append("VALUES ( ");
				sb.append("  ?, ");
				sb.append("  ?, ");
				sb.append("  ?, ");
				sb.append("  0, SYSDATE, ?, ?, SYSDATE ");
				sb.append(") ");

				pstmt = conn.prepareStatement(sb.toString());

				int flag = 0;

				try {
					for (int i = 1; i < lines.size(); i++) {
						liesCheck(lines.get(i).toString());
						String str_line = finalstring;
						String[] str = str_line.split(",");
						if (str.length < 2)
							throw new APException("第" + (i + 1) + "筆,欄位數小於兩筆!");

						flag++;

						pstmt.setString(1, (str[0].equals("")) ? yeaMon : str[0]);
						pstmt.setString(2, (str[1].equals("") ? "0" : str[1]));
						pstmt.setBigDecimal(3, new BigDecimal(flag));
						pstmt.setString(4, (String) getUserVariable(FubonSystemVariableConsts.LOGINID));
						pstmt.setString(5, (String) getUserVariable(FubonSystemVariableConsts.LOGINID));

						pstmt.addBatch();

						if (flag % 1000 == 0) {
							pstmt.executeBatch();
							conn.commit();
						}
					}

					pstmt.executeBatch();
					conn.commit();

					outputVO.setFlag(flag);
				} catch (JBranchException e) {
					conn.rollback();
					e.printStackTrace();
					throw new APException("第" + (flag + 1) + "筆檔案的資料有誤!");
				} finally {
					pstmt.close();
					conn.close();
				}
				
				break;
			case "importActualCust":
				// 20240617 偲偲 #0002052: 請調整實動戶上傳功能(請調整實動戶上傳功能，因僅在客戶首頁使用最新資訊，故請改為TRUNCATE INSERT)
//				PreparedStatement pstmtDEL = conn.prepareStatement("DELETE FROM TBPMS_CUST_ACTUAL WHERE YYYYMM = ? ");
//				pstmtDEL.setString(1, inputVO.getsTime());
//				pstmtDEL.execute();
//				conn.commit();
				pstmtTruncate = conn.prepareStatement("TRUNCATE TABLE TBPMS_CUST_ACTUAL ");
				pstmtTruncate.execute();
				conn.commit();
				
				sb = new StringBuffer();
				sb.append("INSERT INTO TBPMS_CUST_ACTUAL ( ");
				sb.append("  YYYYMM, ");
				sb.append("  CUST_ID, ");
				sb.append("  VERSION, CREATETIME, CREATOR, MODIFIER, LASTUPDATE ");
				sb.append(") ");
				sb.append("VALUES ( ");
				sb.append("  ?, ");
				sb.append("  ?, ");
				sb.append("  0, SYSDATE, ?, ?, SYSDATE ");
				sb.append(") ");

				pstmt = conn.prepareStatement(sb.toString());

				int tag = 0;
				
				try {
					for (int i = 1; i < lines.size(); i++) {
						liesCheck(lines.get(i).toString());
						String str_line = finalstring;
						String[] str = str_line.split(",");
						
						if (str.length < 2)
							throw new APException("第" + (i + 1) + "筆,欄位數小於兩筆!");

						if (str[1].equals(""))
							throw new APException("第" + (i + 1) + "筆,客戶ID有誤!");
						
						tag++;
						
						pstmt.setString(1, (str[0].equals("")) ? inputVO.getsTime() : str[0]);
						pstmt.setString(2, str[1]);
						pstmt.setString(3, (String) getUserVariable(FubonSystemVariableConsts.LOGINID));
						pstmt.setString(4, (String) getUserVariable(FubonSystemVariableConsts.LOGINID));

						pstmt.addBatch();
						
						if (tag % 1000 == 0) {
							pstmt.executeBatch();
							conn.commit();
						}
					}

					pstmt.executeBatch();
					conn.commit();

					outputVO.setFlag(tag);
				} catch (JBranchException e) {
					conn.rollback();
					e.printStackTrace();
					throw new APException("第" + (tag + 1) + "筆檔案的資料有誤!");
				} finally {
					pstmt.close();
					conn.close();
				}
				
				break;
			case "importROA" :
				pstmtTruncate = conn.prepareStatement("TRUNCATE TABLE TBPMS_ROA ");
				pstmtTruncate.execute();
				conn.commit();

				sb = new StringBuffer();
				sb.append("INSERT INTO TBPMS_ROA ( ");
				sb.append("  CUST_ID, ");
				sb.append("  ROA, ");
				sb.append("  VERSION, CREATETIME, CREATOR, MODIFIER, LASTUPDATE ");
				sb.append(") ");
				sb.append("VALUES ( ");
				sb.append("  ?, ");
				sb.append("  ?, ");
				sb.append("  0, SYSDATE, ?, ?, SYSDATE ");
				sb.append(") ");

				pstmt = conn.prepareStatement(sb.toString());

				int index = 0;

				try {
					for (int i = 1; i < lines.size(); i++) {
						liesCheck(lines.get(i).toString());
						String str_line = finalstring;
						String[] str = str_line.split(",");
						if (str.length < 2)
							throw new APException("第" + (i + 1) + "筆,欄位數小於兩筆!");

						index++;

						pstmt.setString(1, str[0]);
						pstmt.setString(2, (str[1].equals("") ? "0" : str[1]));
						pstmt.setString(3, (String) getUserVariable(FubonSystemVariableConsts.LOGINID));
						pstmt.setString(4, (String) getUserVariable(FubonSystemVariableConsts.LOGINID));

						pstmt.addBatch();

						if (index % 1000 == 0) {
							pstmt.executeBatch();
							conn.commit();
						}
					}
					pstmt.executeBatch();
					conn.commit();
					outputVO.setFlag(index);
				} catch (JBranchException e) {
					conn.rollback();
					e.printStackTrace();
					throw new APException("第" + (index + 1) + "筆檔案的資料有誤!");
				} finally {
					pstmt.close();
					conn.close();
				}
				
				break;
		}
		
		this.sendRtnObject(null);
	}

	/** 查詢-投保客戶筆數/實動戶筆數_計算 **/
	public void countNoCust(Object body, IPrimitiveMap header) throws JBranchException {
		
		PMS227InputVO inputVO = (PMS227InputVO) body;
		PMS227OutputVO outputVO = new PMS227OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		
		switch (inputVO.getUploadName()) {
			case "importCustData" :
				sql.append("SELECT COUNT(*) AS COU, ");
				sql.append("       DATA_YM,　");
				sql.append("       MAX(CREATETIME) AS CREATETIME ");
				sql.append("FROM TBPMS_NOINS_CUST ");
				sql.append("WHERE DATA_YM = :YM ");
				sql.append("GROUP BY DATA_YM HAVING COUNT(*) > 0");
				break;
			case "importActualCust":
				sql.append("SELECT COUNT(*) AS COU, ");
				sql.append("       YYYYMM AS DATA_YM,　");
				sql.append("       MAX(CREATETIME) AS CREATETIME ");
				sql.append("FROM TBPMS_CUST_ACTUAL ");
				sql.append("WHERE YYYYMM = :YM ");
				sql.append("GROUP BY YYYYMM HAVING COUNT(*) > 0");
				break;
		}

		queryCondition.setObject("YM", inputVO.getsTime());
		
		queryCondition.setQueryString(sql.toString());
		
		outputVO.setCustList(dam.exeQuery(queryCondition));
		
		this.sendRtnObject(outputVO);
	}

	/** 下載-範例 **/
	public void downLoad(Object body, IPrimitiveMap header) throws Exception {
		
		PMS227InputVO inputVO = (PMS227InputVO) body;
		
		switch (inputVO.getUploadName()) {
			case "importData":
				notifyClientToDownloadFile("doc//PMS//PMS227_EXAMPLE.xls", "PMS227_個別商品轉介查詢_上傳範例.xls"); 
				break;
			case "importCustData":
				notifyClientToDownloadFile("doc//PMS//PMS227_CUST_EXAMPLE.csv", "有投保客戶名單_上傳範例.csv"); 
				break;
			case "importActualCust":
				notifyClientToDownloadFile("doc//PMS//PMS227_CUST_EXAMPLE.csv", "實動戶客戶名單_上傳範例.csv"); 
				break;
			case "importROA":
				notifyClientToDownloadFile("doc//PMS//PMS227_ROA.csv", "客戶ROA_上傳範例.csv"); 
				break;
		}
		
		this.sendRtnObject(null);
	}

	/** 調用存儲過程 **/
	public void callStored(Object body, IPrimitiveMap header) throws JBranchException {
		
		PMS227InputVO inputVO = (PMS227InputVO) body;
		PMS227OutputVO outputVO = new PMS227OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		
		String spName = "";
		switch (inputVO.getUploadName()) {
			case "importData":
				spName = "SP_TBPMS_PRD_REC";
				break;
			case "importCustData":
				spName = "SP_TBPMS_NOINS_CUST";
				break;
			case "importActualCust":
				break;
			case "importROA":
				spName = "SP_TBPMS_ROA";
				break;
		}
		
		if (StringUtils.isNotEmpty(spName)) {
			if (!spName.equals("SP_TBPMS_ROA")) {
				sb.append("CALL PABTH_BTPMS726.").append(spName).append("(?, ?) ");
				
				qc.setString(1, inputVO.getsTime());
				qc.registerOutParameter(2, Types.VARCHAR);				
			} else {
				sb.append("CALL PABTH_BTPMS726.SP_TBPMS_ROA()");
			}
			
			qc.setQueryString(sb.toString());
			
			Map<Integer, Object> resultMap = dam.executeCallable(qc);
			String str = (String) resultMap.get(2);
			String[] strs = null;
			if (str != null) {
				strs = str.split("；");
				if (strs != null && strs.length > 5) {
					str = strs[0] + "；" + strs[1] + "；" + strs[2] + "；" + strs[3] + "；" + strs[4] + "...等";
				}
			}

			outputVO.setErrorMessage(str);
		}
		
		this.sendRtnObject(outputVO);
	}
	
	/** 檢查Map取出欄位是否為Null **/
	private String checkIsNull(Map map, String key) {
		if (StringUtils.isNotBlank(String.valueOf(map.get(key))) && map.get(key) != null) {
			return String.valueOf(map.get(key));
		} else {
			return "";
		}
	}

	/** 檢查Map取出欄位是否為Null **/
	private String checkIsNullAndTrans(Map map, String key) {
		if (StringUtils.isNotBlank(String.valueOf(map.get(key))) && map.get(key) != null) {
			return String.valueOf("=\"" + map.get(key) + "\"");
		} else {
			return "";
		}
	}

	/** 檢查Map取出欄位是否為Null **/
	private String checkIsNullType(Map map, String key) {
		if (StringUtils.isNotBlank(String.valueOf(map.get(key))) && map.get(key) != null) {
			String type = String.valueOf(map.get(key));
			if (String.valueOf(map.get(key)).equals("0"))
				type = "系統資料";
			else if (String.valueOf(map.get(key)).equals("1"))
				type = "上傳資料";
			return type;

		} else {
			return "";
		}
	}

	/** 找字串"\""功能 **/
	public String liesCheck(String lines) throws JBranchException {

		if (lines.indexOf("\"") != -1) {
			//抓出第一個"
			int acc = lines.indexOf("\"");
			
			//原本所有字串
			StringBuffer lines1 = new StringBuffer(lines.toString());
			
			//切掉前面的字串
			String ori = lines.substring(0, acc);
			String oriline = ori.toString();

			String fist = lines1.substring(acc);
			fist = fist.substring(1, fist.indexOf("\"", 1));
			String[] firstsub = fist.split(",");
			for (int i = 0; i < firstsub.length; i++) {
				oriline += firstsub[i].toString();
			}
			
			lines = oriline + lines.substring(acc).replaceFirst("\"" + fist + "\"", "");

		}
		
		if (lines.indexOf("\"") != -1) {
			liesCheck(lines);
		} else {
			finalstring = lines;
		}
		
		return lines;
	}
}
