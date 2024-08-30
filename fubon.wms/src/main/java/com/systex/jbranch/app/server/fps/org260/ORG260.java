package com.systex.jbranch.app.server.fps.org260;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.hibernate.SessionFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.orm.hibernate4.SessionFactoryUtils;
import org.springframework.stereotype.Component;

import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.CSVUtil;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;

import de.schlichtherle.io.FileInputStream;

@Component("org260")
@Scope("request")
public class ORG260 extends FubonWmsBizLogic {
	
	public DataAccessManager dam = null;
	SimpleDateFormat sdfYYYYMMDD = new SimpleDateFormat("yyyyMMdd");
	LinkedHashMap<String, String> headColumnMap = new LinkedHashMap<String, String>();
	
	public ORG260() {
		headColumnMap.put("員編", "UEMP_ID");
		headColumnMap.put("類別(5:計績/6:維護)", "AO_TYPE");
		headColumnMap.put("客戶ID", "CUST_ID");
	}
	
	/**
	 * 取得UHRM人員清單(由員工檔+角色檔) - 前端入口
	 * 
	 * @param body
	 * @param header
	 * @throws Exception
	 */
	public void getUHRMList (Object body, IPrimitiveMap header) throws Exception {
		sendRtnObject(this.getUHRMList(body));
	}
	
	/**
	 * 取得UHRM人員清單(由員工檔+角色檔) - 後端入口
	 * 
	 * @param body
	 * @throws Exception
	 */
	public ORG260OutputVO getUHRMList(Object body) throws Exception {
		
		ORG260InputVO inputVO = (ORG260InputVO) body;
		ORG260OutputVO outputVO = new ORG260OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		
		sb.append("SELECT PRIVILEGEID ");
		sb.append("FROM TBSYSSECUROLPRIASS ");
		sb.append("WHERE ROLEID = :roleID ");
		
		queryCondition.setObject("roleID", (String) getUserVariable(FubonSystemVariableConsts.LOGINROLE));
		
		queryCondition.setQueryString(sb.toString());
		
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		String priID = (String) list.get(0).get("PRIVILEGEID");
		
		sb = new StringBuffer();
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sb.append("SELECT DISTINCT MEM.EMP_NAME AS LABEL, MEM.EMP_ID AS DATA, MEM.BRANCH_NBR, MEM.BRANCH_NAME ");
		sb.append("FROM VWORG_EMP_UHRM_INFO MEM ");
		sb.append("WHERE 1 = 1 ");
		sb.append("AND MEM.PRIVILEGEID = 'UHRM002' ");
		
		switch (priID) {
			case "UHRM002" :
				sb.append("AND MEM.EMP_ID = :loginID ");
				queryCondition.setObject("loginID", (String) getUserVariable(FubonSystemVariableConsts.LOGINID));
				
				outputVO.setuEmpID((String) getUserVariable(FubonSystemVariableConsts.LOGINID));
				break;
			case "UHRM012" : 
				sb.append("AND EXISTS (SELECT 1 FROM TBORG_MEMBER MT WHERE MT.DEPT_ID = MEM.DEPT_ID AND MT.DEPT_ID = :loginArea) ");
				queryCondition.setObject("loginArea", (String) getUserVariable(FubonSystemVariableConsts.LOGIN_AREA));
				break;
			default:
				if (StringUtils.isNotEmpty(inputVO.getBranchAreaID())) {
					sb.append("AND EXISTS (SELECT 1 FROM TBORG_MEMBER MT WHERE MT.DEPT_ID = MEM.DEPT_ID AND MT.DEPT_ID = :branchAreaID) ");
					queryCondition.setObject("branchAreaID", inputVO.getBranchAreaID());
				} else if (StringUtils.isNotEmpty(inputVO.getRegionCenterID())) {
					sb.append("AND EXISTS (SELECT 1 FROM TBORG_MEMBER MT WHERE MT.DEPT_ID = MEM.DEPT_ID AND EXISTS (SELECT 1 FROM TBORG_DEFN D WHERE MT.DEPT_ID = D.DEPT_ID AND D.PARENT_DEPT_ID = :regionCenterID)) ");
					queryCondition.setObject("regionCenterID", inputVO.getRegionCenterID());
				} else if (StringUtils.equals("013", priID)) {
					sb.append("AND EXISTS (SELECT 1 FROM TBORG_MEMBER MT WHERE MT.DEPT_ID = MEM.DEPT_ID AND EXISTS (SELECT 1 FROM TBORG_DEFN D WHERE MT.DEPT_ID = D.DEPT_ID AND D.PARENT_DEPT_ID = :regionCenterID)) ");
					queryCondition.setObject("regionCenterID", (String) getUserVariable(FubonSystemVariableConsts.LOGIN_REGION));
				}
					
				break;
		}
			
		sb.append("ORDER BY MEM.EMP_ID ");

		queryCondition.setQueryString(sb.toString());
		
		outputVO.setUhrmList(dam.exeQuery(queryCondition));
		
		return outputVO;
	}
	
	/**
	 * 上傳UHRM與客戶對應檔
	 * 
	 * @param body
	 * @param header
	 * @throws Exception
	 */
	public void updUHRMList (Object body, IPrimitiveMap header) throws JBranchException, SQLException {
		
		ORG260InputVO inputVO = (ORG260InputVO) body;
		ORG260OutputVO outputVO = new ORG260OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		
		// 取得本次上傳序號
		sb.append("SELECT TO_CHAR(SYSDATE, 'yyyyMMdd') || 'IMP' || LPAD(COUNT(1) + 1, 4, '0') AS PRJ_ID ");
		sb.append("FROM TBCRM_TRS_UHRM_MAIN ");
		sb.append("WHERE TO_CHAR(SYSDATE, 'yyyyMMdd') = TO_CHAR(CREATETIME, 'yyyyMMdd')");
		
		queryCondition.setQueryString(sb.toString());
		
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		
		String prjID = (String) list.get(0).get("PRJ_ID");
		
		// 寫入主檔 ===== START
		sb = new StringBuffer();
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sb.append("INSERT INTO TBCRM_TRS_UHRM_MAIN (PRJ_ID, PRJ_STS, UPD_EMP_ID, UPD_DATETIME, VERSION, CREATETIME, CREATOR, MODIFIER, LASTUPDATE) ");
		sb.append("VALUES (:prjID, :prjSts, :updEmpID, :updDateTime, :version, SYSDATE, :creator, :modifier, SYSDATE) ");
		
		queryCondition.setObject("prjID", prjID);
		queryCondition.setObject("prjSts", "01");
		queryCondition.setObject("updEmpID", getUserVariable(FubonSystemVariableConsts.LOGINID));
		queryCondition.setObject("updDateTime", new Date());
		queryCondition.setObject("version", 0);
		queryCondition.setObject("creator", getUserVariable(FubonSystemVariableConsts.LOGINID));
		queryCondition.setObject("modifier", getUserVariable(FubonSystemVariableConsts.LOGINID));
		
		queryCondition.setQueryString(sb.toString());
		
		dam.exeUpdate(queryCondition);
		// 寫入主檔 ===== END
		
		Connection connection = null;
		PreparedStatement pstmtInsert = null;
		PreparedStatement pstmtQuery = null;
		ResultSet queeyResult = null;
		
		try {
			// 寫入明細檔 ===== JDBS START
			com.systex.jbranch.platform.common.dataaccess.datasource.DataSource ds = new com.systex.jbranch.platform.common.dataaccess.datasource.DataSource();
	        SessionFactory sf = (SessionFactory) PlatformContext.getBean(ds.getDataSource());
			DataSource dataSource = SessionFactoryUtils.getDataSource(sf);
			connection = dataSource.getConnection();
			
			File csvFile = new File((String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH), inputVO.getFileName());
			
			FileInputStream fi = new FileInputStream(csvFile);
			BufferedReader br = new BufferedReader(new InputStreamReader(fi, "BIG5"));
	
			// 將輸入資料轉型成List<Map<[資料庫欄位名稱], [欄位值]>> 方便下一步驟執行
			String[] head = br.readLine().split(",");
			String line = null;
			
			connection.setAutoCommit(false);
			
			sb = new StringBuffer();
			sb.append("INSERT INTO TBCRM_TRS_UHRM_DTL (SEQNO, PRJ_ID, CUST_ID, NEW_UEMP_ID, IMP_TYPE, IMP_STATUS, IMP_STS_RES, TRS_STATUS, VERSION, CREATETIME, CREATOR, MODIFIER, LASTUPDATE, NEW_AO_TYPE) ");
			sb.append("VALUES (SQ_TBCRM_TRS_UHRM_DTL.NEXTVAL, ?, ?, ?, NULL, NULL, NULL, NULL, 0, SYSDATE, ?, ?, SYSDATE, ?) ");
			// INSERT SQL 預先編譯
			pstmtInsert = connection.prepareStatement(sb.toString());

			while ((line = br.readLine()) != null) {
				String[] data = line.split(",");

				pstmtInsert.setObject(1, prjID);
				pstmtInsert.setObject(2, data[2]);
				pstmtInsert.setObject(3, data[0]);
				pstmtInsert.setObject(4, getUserVariable(FubonSystemVariableConsts.LOGINID));
				pstmtInsert.setObject(5, getUserVariable(FubonSystemVariableConsts.LOGINID));
				pstmtInsert.setObject(6, data[1]);

				pstmtInsert.addBatch();
			}
			
			pstmtInsert.executeBatch();
			connection.commit();
			// 寫入明細檔 ===== END
			
			// 判斷本次上傳資料正確性 ===== START
			/*
			 * IMP_TYPE
			 * ON-貼標
			 * OFF-拔標
			 * ERR-錯誤
			 */
			
			/*
			 * IMP_STATUS
			 * Y-成功
			 * N-失敗
			 */
			
			/* 
			 * IMP_STS_RES
			 * 01-客戶ID不存在
			 * 02-UHRM員編不存在
			 * 03-移轉前後指定UHRM員編相同
			 * 04-客戶ID重複2筆以上
			 * 05-指定UHRM員編非UHRM(該人員未取得UHRM角色)
			 * 99-客戶已有理專，不得貼標
			 */
			sb = new StringBuffer();
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sb.append("MERGE INTO TBCRM_TRS_UHRM_DTL A ");
			sb.append("USING ( ");
			sb.append("  SELECT UD.SEQNO, UD.CUST_ID, UD.NEW_UEMP_ID, ");
			sb.append("         CASE WHEN CM.CUST_ID IS NULL THEN 'ERR' ");
			sb.append("              WHEN UD.NEW_UEMP_ID IS NOT NULL AND CM.AO_CODE IS NOT NULL THEN 'ERR' ");
			sb.append("              WHEN UD.NEW_UEMP_ID IS NOT NULL AND MEM.EMP_ID IS NULL THEN 'ERR' ");
			sb.append("              WHEN UD.NEW_UEMP_ID = UPLIST.UEMP_ID AND UD.NEW_AO_TYPE = UEMP_AO_TYPE THEN 'ERR' ");
			sb.append("              WHEN (SELECT COUNT(1) FROM TBCRM_TRS_UHRM_DTL T WHERE T.PRJ_ID = UD.PRJ_ID AND T.CUST_ID = UD.CUST_ID) > 1 THEN 'ERR' ");
			sb.append("              WHEN UD.NEW_UEMP_ID IS NOT NULL AND MR.ROLE_ID IS NULL THEN 'ERR' ");
			sb.append("              WHEN UD.NEW_UEMP_ID IS NULL THEN 'OFF' ");
			sb.append("         ELSE 'ON' END AS IMP_TYPE, ");
			sb.append("         CASE WHEN CM.CUST_ID IS NULL THEN 'N' ");
			sb.append("              WHEN UD.NEW_UEMP_ID IS NOT NULL AND CM.AO_CODE IS NOT NULL THEN 'N' ");
			sb.append("              WHEN UD.NEW_UEMP_ID IS NOT NULL AND MEM.EMP_ID IS NULL THEN 'N' ");
			sb.append("              WHEN UD.NEW_UEMP_ID = UPLIST.UEMP_ID AND UD.NEW_AO_TYPE = UEMP_AO_TYPE THEN 'N' ");
			sb.append("              WHEN (SELECT COUNT(1) FROM TBCRM_TRS_UHRM_DTL T WHERE T.PRJ_ID = UD.PRJ_ID AND T.CUST_ID = UD.CUST_ID) > 1 THEN 'N' ");
			sb.append("              WHEN UD.NEW_UEMP_ID IS NOT NULL AND MR.ROLE_ID IS NULL THEN 'N' ");
			sb.append("         ELSE 'Y' END AS IMP_STATUS, ");
			sb.append("         CASE WHEN CM.CUST_ID IS NULL THEN '01' ");
			sb.append("              WHEN UD.NEW_UEMP_ID IS NOT NULL AND CM.AO_CODE IS NOT NULL THEN '99' ");
			sb.append("              WHEN UD.NEW_UEMP_ID IS NOT NULL AND MEM.EMP_ID IS NULL THEN '02' ");
			sb.append("              WHEN UD.NEW_UEMP_ID = UPLIST.UEMP_ID AND UD.NEW_AO_TYPE = UEMP_AO_TYPE THEN '03' ");
			sb.append("              WHEN (SELECT COUNT(1) FROM TBCRM_TRS_UHRM_DTL T WHERE T.PRJ_ID = UD.PRJ_ID AND T.CUST_ID = UD.CUST_ID) > 1 THEN '04' ");
			sb.append("              WHEN UD.NEW_UEMP_ID IS NOT NULL AND MR.ROLE_ID IS NULL THEN '05' ");
			sb.append("         ELSE NULL END AS IMP_STS_RES ");
			sb.append("  FROM TBCRM_TRS_UHRM_DTL UD ");
			sb.append("  LEFT JOIN TBCRM_CUST_MAST CM ON UD.CUST_ID = CM.CUST_ID ");
			sb.append("  LEFT JOIN TBORG_MEMBER MEM ON UD.NEW_UEMP_ID = EMP_ID ");
			sb.append("  LEFT JOIN TBORG_CUST_UHRM_PLIST UPLIST ON UD.CUST_ID = UPLIST.CUST_ID ");
			sb.append("  LEFT JOIN TBORG_MEMBER_ROLE MR ON UD.NEW_UEMP_ID = MR.EMP_ID ");
			sb.append("  WHERE UD.PRJ_ID = :prjID ");
			sb.append(") B ");
			sb.append("ON ( ");
			sb.append("  A.SEQNO = B.SEQNO ");
			sb.append(") ");
			sb.append("WHEN MATCHED THEN ");
			sb.append("  UPDATE SET A.IMP_TYPE = B.IMP_TYPE, ");
			sb.append("             A.IMP_STATUS = B.IMP_STATUS, ");
			sb.append("             A.IMP_STS_RES = B.IMP_STS_RES, ");
			sb.append("             A.LASTUPDATE = SYSDATE, ");
			sb.append("             A.MODIFIER = :modifier ");
			
			queryCondition.setObject("prjID", prjID);
			queryCondition.setObject("modifier", getUserVariable(FubonSystemVariableConsts.LOGINID));
			queryCondition.setQueryString(sb.toString());

			dam.exeUpdate(queryCondition);

			// 判斷本次上傳資料正確性 ===== END
			
			// 若負責人員為新RM，新增RM CODE FOR PERSON
			// 1-1. 取得無5 CODE 的RM
			sb = new StringBuffer();
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sb.append("SELECT DISTINCT NEW_UEMP_ID ");
			sb.append("FROM TBCRM_TRS_UHRM_DTL UD ");
			sb.append("LEFT JOIN TBORG_SALES_AOCODE AO ON UD.NEW_UEMP_ID = AO.EMP_ID AND AO.TYPE = '5' ");
			sb.append("WHERE UD.PRJ_ID = :prjID ");
			sb.append("AND IMP_TYPE = 'ON' ");
			sb.append("AND AO.AO_CODE IS NULL ");
			queryCondition.setObject("prjID", prjID);
			
			queryCondition.setQueryString(sb.toString());
			list = null;
			list = dam.exeQuery(queryCondition);
			
			// 2-1. 壓CODE
			for (Map<String, Object> map : list) {
				// 取得新code
				sb = new StringBuffer();
				queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sb.append("SELECT AO_CODE ");
				sb.append("FROM ( ");
				sb.append("  SELECT AO_CODE FROM TBORG_GEN_AO_CODE WHERE IS_BLACK_LIST = 'N' AND USE_FLAG = 'N' ");
				sb.append("  MINUS ");
				sb.append("  SELECT AO_CODE FROM TBORG_SALES_AOCODE_REVIEW WHERE REVIEW_STATUS = 'W' ");
				sb.append(") ");
				sb.append("WHERE ROWNUM = 1 ");
				
				queryCondition.setQueryString(sb.toString());
				
				List<Map<String, Object>> codeList = dam.exeQuery(queryCondition);
				
				sb = new StringBuffer();
				pstmtInsert = null;
				sb.append("INSERT INTO TBORG_SALES_AOCODE (EMP_ID, AO_CODE, TYPE, ACTIVE_DATE, ACT_TYPE, REVIEW_STATUS, VERSION, CREATETIME, CREATOR, MODIFIER, LASTUPDATE) ");
				sb.append("VALUES (?, ?, '5', sysdate, 'A', 'Y', 0, sysdate, ?, ?, sysdate) ");
				pstmtInsert = connection.prepareStatement(sb.toString());
				pstmtInsert.setObject(1, (String) map.get("NEW_UEMP_ID"));
				pstmtInsert.setObject(2, (String) codeList.get(0).get("AO_CODE"));
				pstmtInsert.setObject(3, getUserVariable(FubonSystemVariableConsts.LOGINID));
				pstmtInsert.setObject(4, getUserVariable(FubonSystemVariableConsts.LOGINID));
				
				pstmtInsert.execute();
				connection.commit();
				
				sb = new StringBuffer();
				pstmtInsert = null;
				sb.append("UPDATE TBORG_GEN_AO_CODE SET USE_FLAG = 'Y', USE_EMP_ID = ?, MODIFIER = ?, LASTUPDATE  = SYSDATE WHERE AO_CODE = ? ");
				pstmtInsert = connection.prepareStatement(sb.toString());
				
				pstmtInsert.setObject(1, (String) map.get("NEW_UEMP_ID"));
				pstmtInsert.setObject(2, getUserVariable(FubonSystemVariableConsts.LOGINID));
				pstmtInsert.setObject(3, (String) codeList.get(0).get("AO_CODE"));
				
				pstmtInsert.execute();
				connection.commit();
			}
			
			// 1-2. 取得無6 CODE 的RM
			sb = new StringBuffer();
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sb.append("SELECT DISTINCT NEW_UEMP_ID ");
			sb.append("FROM TBCRM_TRS_UHRM_DTL UD ");
			sb.append("LEFT JOIN TBORG_SALES_AOCODE AO ON UD.NEW_UEMP_ID = AO.EMP_ID AND AO.TYPE = '6' ");
			sb.append("WHERE UD.PRJ_ID = :prjID ");
			sb.append("AND IMP_TYPE = 'ON' ");
			sb.append("AND AO.AO_CODE IS NULL ");
			queryCondition.setObject("prjID", prjID);
			
			queryCondition.setQueryString(sb.toString());
			list = null;
			list = dam.exeQuery(queryCondition);
			
			// 2-2. 壓CODE
			for (Map<String, Object> map : list) {
				// 取得新code
				sb = new StringBuffer();
				queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sb.append("SELECT AO_CODE ");
				sb.append("FROM ( ");
				sb.append("  SELECT AO_CODE FROM TBORG_GEN_AO_CODE WHERE IS_BLACK_LIST = 'N' AND USE_FLAG = 'N' ");
				sb.append("  MINUS ");
				sb.append("  SELECT AO_CODE FROM TBORG_SALES_AOCODE_REVIEW WHERE REVIEW_STATUS = 'W' ");
				sb.append(") ");
				sb.append("WHERE ROWNUM = 1 ");
				
				queryCondition.setQueryString(sb.toString());
				
				List<Map<String, Object>> codeList = dam.exeQuery(queryCondition);
				
				sb = new StringBuffer();
				pstmtInsert = null;
				sb.append("INSERT INTO TBORG_SALES_AOCODE (EMP_ID, AO_CODE, TYPE, ACTIVE_DATE, ACT_TYPE, REVIEW_STATUS, VERSION, CREATETIME, CREATOR, MODIFIER, LASTUPDATE) ");
				sb.append("VALUES (?, ?, '6', sysdate, 'A', 'Y', 0, sysdate, ?, ?, sysdate) ");
				pstmtInsert = connection.prepareStatement(sb.toString());
				pstmtInsert.setObject(1, (String) map.get("NEW_UEMP_ID"));
				pstmtInsert.setObject(2, (String) codeList.get(0).get("AO_CODE"));
				pstmtInsert.setObject(3, getUserVariable(FubonSystemVariableConsts.LOGINID));
				pstmtInsert.setObject(4, getUserVariable(FubonSystemVariableConsts.LOGINID));
				
				pstmtInsert.execute();
				connection.commit();
				
				sb = new StringBuffer();
				pstmtInsert = null;
				sb.append("UPDATE TBORG_GEN_AO_CODE SET USE_FLAG = 'Y', USE_EMP_ID = ?, MODIFIER = ?, LASTUPDATE  = SYSDATE WHERE AO_CODE = ? ");
				pstmtInsert = connection.prepareStatement(sb.toString());
				
				pstmtInsert.setObject(1, (String) map.get("NEW_UEMP_ID"));
				pstmtInsert.setObject(2, getUserVariable(FubonSystemVariableConsts.LOGINID));
				pstmtInsert.setObject(3, (String) codeList.get(0).get("AO_CODE"));
				
				pstmtInsert.execute();
				connection.commit();
			}
			
			// 3. 寫入客戶移轉-紀錄(UHRM變更)
			sb = new StringBuffer();
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sb.append("INSERT INTO TBCRM_CUST_UHRM_CHGLOG ");
			sb.append("SELECT SQ_TBCRM_CUST_UHRM_CHGLOG.NEXTVAL, ");
			sb.append("       UD.CUST_ID, ");
			sb.append("       UP.UEMP_ID AS OLD_UEMP_ID, ");
			sb.append("       AO_OLD.AO_CODE AS OLD_UEMP_AOCODE, ");
			sb.append("       M_OLD.EMP_NAME AS OLD_UEMP_NAME, ");
			sb.append("       UD.NEW_UEMP_ID, ");
			sb.append("       AO_NEW.AO_CODE AS NEW_UEMP_AOCODE, ");
			sb.append("       M_NEW.EMP_NAME AS NEW_UEMP_NAME, ");
			sb.append("       0, ");
			sb.append("       SYSDATE, ");
			sb.append("       :creator, ");
			sb.append("       :modifier, ");
			sb.append("       SYSDATE, ");
			sb.append("       AO_OLD.TYPE, ");
			sb.append("       AO_NEW.TYPE ");
			sb.append("FROM TBCRM_TRS_UHRM_DTL UD ");
			sb.append("LEFT JOIN TBORG_CUST_UHRM_PLIST UP ON UD.CUST_ID = UP.CUST_ID ");
			sb.append("LEFT JOIN TBORG_MEMBER M_OLD ON UP.UEMP_ID = M_OLD.EMP_ID ");
			sb.append("LEFT JOIN TBORG_SALES_AOCODE AO_OLD ON UP.UEMP_ID = AO_OLD.EMP_ID AND UP.UEMP_AOCODE = AO_OLD.AO_CODE ");
			sb.append("LEFT JOIN TBORG_MEMBER M_NEW ON UD.NEW_UEMP_ID = M_NEW.EMP_ID ");
			sb.append("LEFT JOIN TBORG_SALES_AOCODE AO_NEW ON UD.NEW_UEMP_ID = AO_NEW.EMP_ID AND AO_NEW.TYPE = UD.NEW_AO_TYPE ");
			sb.append("WHERE UD.IMP_TYPE IN ('ON', 'OFF') ");
			sb.append("AND UD.PRJ_ID = :prjID ");
			sb.append("AND (UP.UEMP_ID IS NOT NULL OR UD.NEW_UEMP_ID IS NOT NULL) ");
			
			queryCondition.setObject("creator", getUserVariable(FubonSystemVariableConsts.LOGINID));
			queryCondition.setObject("modifier", getUserVariable(FubonSystemVariableConsts.LOGINID));
			queryCondition.setObject("prjID", prjID);
			
			queryCondition.setQueryString(sb.toString());
			
			dam.exeUpdate(queryCondition);

			// 4-1-1 貼標. 紀錄完成(step3)後，寫入UHRM團隊與客戶對應表
			sb = new StringBuffer();
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sb.append("MERGE INTO TBORG_CUST_UHRM_PLIST A ");
			sb.append("USING ( ");
			sb.append("  SELECT UD.CUST_ID, UD.NEW_UEMP_ID, AO_NEW.AO_CODE, M_NEW.EMP_NAME, AO_NEW.TYPE AS UEMP_AO_TYPE ");
			sb.append("  FROM TBCRM_TRS_UHRM_DTL UD ");
			sb.append("  LEFT JOIN TBORG_MEMBER M_NEW ON UD.NEW_UEMP_ID = M_NEW.EMP_ID ");
			sb.append("  LEFT JOIN TBORG_SALES_AOCODE AO_NEW ON UD.NEW_UEMP_ID = AO_NEW.EMP_ID AND AO_NEW.TYPE = UD.NEW_AO_TYPE ");
			sb.append("  WHERE UD.IMP_TYPE = 'ON' ");
			sb.append("  AND UD.PRJ_ID = :prjID ");
			sb.append(") B ");
			sb.append("ON ( ");
			sb.append("  A.CUST_ID = B.CUST_ID ");
			sb.append(") ");
			sb.append("WHEN MATCHED THEN ");
			sb.append("  UPDATE SET A.UEMP_ID = B.NEW_UEMP_ID, A.UEMP_AOCODE = B.AO_CODE, A.UEMP_NAME = B.EMP_NAME, A.LASTUPDATE = SYSDATE, A.MODIFIER = :modifier, A.UEMP_AO_TYPE = B.UEMP_AO_TYPE ");
			sb.append("WHEN NOT MATCHED THEN ");
			sb.append("  INSERT VALUES (SQ_TBORG_CUST_UHRM_PLIST.NEXTVAL, B.CUST_ID, B.NEW_UEMP_ID, B.AO_CODE, B.EMP_NAME, 0, SYSDATE, :creator, :modifier, SYSDATE, B.UEMP_AO_TYPE) ");
			
			queryCondition.setObject("prjID", prjID);
			queryCondition.setObject("creator", getUserVariable(FubonSystemVariableConsts.LOGINID));
			queryCondition.setObject("modifier", getUserVariable(FubonSystemVariableConsts.LOGINID));
			
			queryCondition.setQueryString(sb.toString());
			
			dam.exeUpdate(queryCondition);
			
			// 4-1-2 貼標. 寫入完成後，於客戶主檔貼標
			sb = new StringBuffer();
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sb.append("UPDATE TBCRM_CUST_MAST A ");
			sb.append("SET A.UEMP_ID = (SELECT UD.NEW_UEMP_ID ");
			sb.append("                 FROM TBCRM_TRS_UHRM_DTL UD ");
			sb.append("                 LEFT JOIN TBORG_MEMBER M_NEW ON UD.NEW_UEMP_ID = M_NEW.EMP_ID ");
			sb.append("                 LEFT JOIN TBORG_SALES_AOCODE AO_NEW ON UD.NEW_UEMP_ID = AO_NEW.EMP_ID AND AO_NEW.TYPE = '5' ");
			sb.append("                 WHERE UD.IMP_TYPE = 'ON' ");
			sb.append("                 AND UD.PRJ_ID = :prjID ");
			sb.append("                 AND A.CUST_ID = UD.CUST_ID), ");
			sb.append("    A.MODIFIER = :modifier, ");
			sb.append("    A.LASTUPDATE = SYSDATE ");
			sb.append("WHERE EXISTS (SELECT UD.CUST_ID ");
			sb.append("              FROM TBCRM_TRS_UHRM_DTL UD ");
			sb.append("              LEFT JOIN TBORG_MEMBER M_NEW ON UD.NEW_UEMP_ID = M_NEW.EMP_ID ");
			sb.append("              LEFT JOIN TBORG_SALES_AOCODE AO_NEW ON UD.NEW_UEMP_ID = AO_NEW.EMP_ID AND AO_NEW.TYPE = '5' ");
			sb.append("              WHERE UD.IMP_TYPE = 'ON' ");
			sb.append("              AND UD.PRJ_ID = :prjID ");
			sb.append("              AND A.CUST_ID = UD.CUST_ID) ");
			
			queryCondition.setObject("prjID", prjID);
			queryCondition.setObject("modifier", getUserVariable(FubonSystemVariableConsts.LOGINID));
			
			queryCondition.setQueryString(sb.toString());
			
			dam.exeUpdate(queryCondition);
			
			// 4-1-3貼標. 批次執行(次日生效)
			/* 當客戶被貼標時，客戶主檔寫入RM CODE，未結案名單(含參考資訊)需依下述規則
			 * >> 若客戶有分行理專，分行理專身上的未結案名單(含參考資訊)，分派至高端RM執行，新增參考資訊至原分行理專。
			 * >> 若客戶無分行理專，毋須新增參考資訊至原執行人員。
			 */
			
			// 4-2-1 拔標. 紀錄完成(step3)後，寫入UHRM團隊與客戶對應表
			sb = new StringBuffer();
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sb.append("MERGE INTO TBORG_CUST_UHRM_PLIST A ");
			sb.append("USING ( ");
			sb.append("  SELECT UD.CUST_ID, UD.NEW_UEMP_ID, AO_NEW.AO_CODE, M_NEW.EMP_NAME ");
			sb.append("  FROM TBCRM_TRS_UHRM_DTL UD ");
			sb.append("  LEFT JOIN TBORG_MEMBER M_NEW ON UD.NEW_UEMP_ID = M_NEW.EMP_ID ");
			sb.append("  LEFT JOIN TBORG_SALES_AOCODE AO_NEW ON UD.NEW_UEMP_ID = AO_NEW.EMP_ID AND AO_NEW.TYPE = '5' ");
			sb.append("  WHERE UD.IMP_TYPE = 'OFF' ");
			sb.append("  AND UD.PRJ_ID = :prjID ");
			sb.append(") B ");
			sb.append("ON ( ");
			sb.append("  A.CUST_ID = B.CUST_ID ");
			sb.append(") ");
			sb.append("WHEN MATCHED THEN ");
			sb.append("  UPDATE SET A.UEMP_ID = NULL, A.UEMP_AOCODE = NULL, A.UEMP_NAME = NULL, A.UEMP_AO_TYPE = NULL, A.LASTUPDATE = SYSDATE, A.MODIFIER = :modifier ");
			sb.append("  DELETE WHERE B.NEW_UEMP_ID IS NULL ");
			
			queryCondition.setObject("prjID", prjID);
			queryCondition.setObject("modifier", getUserVariable(FubonSystemVariableConsts.LOGINID));
			
			queryCondition.setQueryString(sb.toString());
			
			dam.exeUpdate(queryCondition);
			
			// 4-2-2 拔標. 寫入完成後，於客戶主檔拔標
			
			sb = new StringBuffer();
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sb.append("UPDATE TBCRM_CUST_MAST A ");
			sb.append("SET A.UEMP_ID = NULL, ");
			sb.append("    A.MODIFIER = :modifier, ");
			sb.append("    A.LASTUPDATE = SYSDATE ");
			sb.append("WHERE EXISTS (SELECT UD.CUST_ID ");
			sb.append("              FROM TBCRM_TRS_UHRM_DTL UD ");
			sb.append("              LEFT JOIN TBORG_MEMBER M_NEW ON UD.NEW_UEMP_ID = M_NEW.EMP_ID ");
			sb.append("              LEFT JOIN TBORG_SALES_AOCODE AO_NEW ON UD.NEW_UEMP_ID = AO_NEW.EMP_ID AND AO_NEW.TYPE = '5' ");
			sb.append("              WHERE UD.IMP_TYPE = 'OFF' ");
			sb.append("              AND UD.PRJ_ID = :prjID ");
			sb.append("              AND A.CUST_ID = UD.CUST_ID) ");
			
			queryCondition.setObject("prjID", prjID);
			queryCondition.setObject("modifier", getUserVariable(FubonSystemVariableConsts.LOGINID));
			
			queryCondition.setQueryString(sb.toString());
			
			dam.exeUpdate(queryCondition);
			
			// 4-2-3拔標. 批次執行(次日生效)
			/* 當客戶被拔標時，客戶主檔移除RM CODE，未結案名單(不含參考資訊)需依下述規則
			 * >> 若客戶有分行理專，將高端RM身上的名單移轉至分行理專，且分行理專身上的參考資訊作廢。
			 * >> 若客戶無分行理專，由分行主管待分派。
			 */
			
			
			// 5. 全部完成後，更新主檔PRJ_STS
			sb = new StringBuffer();
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sb.append("UPDATE TBCRM_TRS_UHRM_MAIN A ");
			sb.append("SET A.PRJ_STS = ( ");
			sb.append("  SELECT ");
			sb.append("         CASE WHEN ON_RM_COUNTS + OFF_RM_COUNTS = 0 THEN '04' ");
			sb.append("              WHEN ON_RM_COUNTS + OFF_RM_COUNTS > 0 AND ERR_RM_COUNTS > 0 THEN '03' ");
			sb.append("              WHEN ON_RM_COUNTS + OFF_RM_COUNTS > 0 AND ERR_RM_COUNTS = 0 THEN '02' ");
			sb.append("         ELSE '02' END AS PRJ_STS ");
			sb.append("  FROM TBCRM_TRS_UHRM_MAIN UD ");
			sb.append("  LEFT JOIN ( ");
			sb.append("    SELECT PRJ_ID, NVL(ON_RM_COUNTS, 0) AS ON_RM_COUNTS, NVL(OFF_RM_COUNTS, 0) AS OFF_RM_COUNTS, NVL(ERR_RM_COUNTS, 0) AS ERR_RM_COUNTS ");
			sb.append("    FROM ( ");
			sb.append("      SELECT PRJ_ID, IMP_TYPE, COUNT(IMP_TYPE) AS COUNTS ");
			sb.append("      FROM TBCRM_TRS_UHRM_DTL ");
			sb.append("      GROUP BY PRJ_ID, IMP_TYPE ");
			sb.append("    ) PIVOT (MAX(COUNTS) FOR IMP_TYPE IN ('ON' AS ON_RM_COUNTS, 'OFF' AS OFF_RM_COUNTS, 'ERR' AS ERR_RM_COUNTS)) ");
			sb.append("  ) UDS ON UD.PRJ_ID = UDS.PRJ_ID ");
			sb.append("  WHERE A.PRJ_ID = UD.PRJ_ID ");
			sb.append("), ");
			sb.append("A.LASTUPDATE = SYSDATE, ");
			sb.append("A.MODIFIER = :modifier ");
			sb.append("WHERE A.PRJ_ID = :prjID ");
			
			queryCondition.setObject("prjID", prjID);
			queryCondition.setObject("modifier", getUserVariable(FubonSystemVariableConsts.LOGINID));
			
			queryCondition.setQueryString(sb.toString());
			
			dam.exeUpdate(queryCondition);
		} catch (Exception e) {
			sb = new StringBuffer();
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sb.append("UPDATE TBCRM_TRS_UHRM_MAIN SET PRJ_STS = '05', LASTUPDATE = SYSDATE, MODIFIER = :modifier WHERE PRJ_ID = :prjID "); // 05-發生EXCEPTION
			
			queryCondition.setObject("prjID", prjID);
			queryCondition.setObject("modifier", getUserVariable(FubonSystemVariableConsts.LOGINID));
			
			queryCondition.setQueryString(sb.toString());
			
			dam.exeUpdate(queryCondition);
		} finally {
			if (pstmtInsert != null) {
				pstmtInsert.close();
			}
			if (pstmtQuery != null) {
				pstmtQuery.close();
			}
			if (connection != null) {
				connection.close();
			}
		}
		
		outputVO.setPrjID(prjID);
		
		sendRtnObject(outputVO);
	}
	
	public void getUpdResultList (Object body, IPrimitiveMap header) throws JBranchException {
		
		ORG260InputVO inputVO = (ORG260InputVO) body;
		ORG260OutputVO outputVO = new ORG260OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		
		sb.append("SELECT CUST_ID, NEW_UEMP_ID, IMP_TYPE, IMP_STATUS, IMP_STS_RES ");
		sb.append("FROM TBCRM_TRS_UHRM_DTL ");
		sb.append("WHERE PRJ_ID = :prjID ");
		sb.append("ORDER BY SEQNO ");
		
		queryCondition.setObject("prjID", inputVO.getPrjID());
		queryCondition.setQueryString(sb.toString());
		
		outputVO.setResultList(dam.exeQuery(queryCondition));
		
		sendRtnObject(outputVO);
	}
	
	public void query (Object body, IPrimitiveMap header) throws JBranchException {
		
		ORG260InputVO inputVO = (ORG260InputVO) body;
		ORG260OutputVO outputVO = new ORG260OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		
		sb.append("SELECT PRIVILEGEID ");
		sb.append("FROM TBSYSSECUROLPRIASS ");
		sb.append("WHERE ROLEID = :roleID ");
		
		queryCondition.setObject("roleID", (String) getUserVariable(FubonSystemVariableConsts.LOGINROLE));
		
		queryCondition.setQueryString(sb.toString());
		
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		String priID = (String) list.get(0).get("PRIVILEGEID");
		
		sb = new StringBuffer();
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sb.append("SELECT UP.UEMP_ID, UP.UEMP_AOCODE, CASE WHEN UP.UEMP_AO_TYPE = '5' THEN '計績' ELSE '維護' END AS AO_TYPE, UP.UEMP_NAME, UP.CUST_ID, CM.CUST_NAME, UP.MODIFIER, UP.LASTUPDATE ");
		sb.append("FROM TBORG_CUST_UHRM_PLIST UP ");
		sb.append("LEFT JOIN TBCRM_CUST_MAST CM ON UP.CUST_ID = CM.CUST_ID ");
		sb.append("WHERE 1 = 1 ");
		
		switch (priID) {
			case "UHRM002" :
				sb.append("AND UP.UEMP_ID = :loginID ");
				queryCondition.setObject("loginID", (String) getUserVariable(FubonSystemVariableConsts.LOGINID));
				
				break;
			case "UHRM012" : 
			case "UHRM013" : 
			case "UHRM045" : 
			case "UHRM046" : 
			case "UHRM014" : 
				if (StringUtils.isNotBlank(inputVO.getuEmpID())) {
					sb.append("AND UP.UEMP_ID = :uEmpID ");
					queryCondition.setObject("uEmpID", inputVO.getuEmpID());
				}
				
				break;
		}
		sb.append("ORDER BY CUST_ID ");
		
		queryCondition.setQueryString(sb.toString());
		
		outputVO.setResultList(dam.exeQuery(queryCondition));
		
		sendRtnObject(outputVO);
	}
	
	public void getExample(Object body, IPrimitiveMap header) throws JBranchException {
		
		CSVUtil csv = new CSVUtil();

		// 設定表頭
		csv.setHeader(headColumnMap.keySet().toArray(new String[headColumnMap.keySet().size()]));
		String url = csv.generateCSV();
		notifyClientToDownloadFile(url, "UHRM上傳客戶資料檔.csv");
		
		sendRtnObject(null);
	}
	
	public void export (Object body, IPrimitiveMap header) throws JBranchException {
		
		ORG260InputVO inputVO = (ORG260InputVO) body;
		
		List<Map<String, Object>> exportLst = inputVO.getExportList();
		List<Object[]> csvData = new ArrayList<Object[]>();
		
		String[] csvHeader = new String[] { "員工編號", "RM_CODE", "CODE類別", "員工姓名", "客戶ID", "客戶姓名", "最後修改人", "最後修改時間"};
		String[] csvMain = new String[] { "UEMP_ID", "UEMP_AOCODE", "AO_TYPE", "UEMP_NAME", "CUST_ID", "CUST_NAME", "MODIFIER", "LASTUPDATE"};

		if (exportLst.size() > 0) {
			for (Map<String, Object> map : exportLst) {
				String[] records = new String[csvHeader.length];
				for (int i = 0; i < csvHeader.length; i++) {
					records[i] = checkIsNull(map, csvMain[i]);
				}
				
				csvData.add(records);
			}
			
			CSVUtil csv = new CSVUtil();
			
			// 設定表頭
			csv.setHeader(csvHeader);
			// 添加明細的List
			csv.addRecordList(csvData);

			// 執行產生csv并收到該檔的url
			String url = csv.generateCSV();

			// 將url送回FlexClient存檔
			notifyClientToDownloadFile(url, "UHRM客戶資料檔" + sdfYYYYMMDD.format(new Date()) + ".csv");
		}
		
		sendRtnObject(null);
	}

	/**
	* 檢查Map取出欄位是否為Null
	*/
	private String checkIsNull(Map<String, Object> map, String key) {
		
		if (StringUtils.isNotBlank(String.valueOf(map.get(key))) && null != map.get(key)) {
			return String.valueOf(map.get(key));
		} else {
			return "";
		}
	}
	
	/**
	 * 取得UHRM人員清單(由員工檔+角色檔) - 前端入口
	 * 
	 * @param body
	 * @param header
	 * @throws Exception
	 */
	public void getUHRMListByType (Object body, IPrimitiveMap header) throws Exception {
		sendRtnObject(this.getUHRMListByType(body));
	}
	
	/**
	 * 取得UHRM人員清單(由員工檔+角色檔) - 後端入口
	 * 
	 * @param body
	 * @throws Exception
	 */
	public ORG260OutputVO getUHRMListByType(Object body) throws Exception {
		
		ORG260InputVO inputVO = (ORG260InputVO) body;
		ORG260OutputVO outputVO = new ORG260OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		
		sb.append("SELECT PRIVILEGEID ");
		sb.append("FROM TBSYSSECUROLPRIASS ");
		sb.append("WHERE ROLEID = :roleID ");
		
		queryCondition.setObject("roleID", (String) getUserVariable(FubonSystemVariableConsts.LOGINROLE));
		
		queryCondition.setQueryString(sb.toString());
		
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		String priID = (String) list.get(0).get("PRIVILEGEID");

		sb = new StringBuffer();
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sb.append("SELECT MEM.UHRM_CODE || '-' || MEM.EMP_NAME || '(' || CASE WHEN MEM.CODE_TYPE = '1' THEN '主' WHEN MEM.CODE_TYPE = '2' THEN '副' ELSE '維護' END || ')' AS LABEL, ");
		sb.append("       MEM.EMP_ID || ',' || MEM.CODE_TYPE AS DATA, ");
		sb.append("       MEM.UHRM_CODE ");
		sb.append("FROM VWORG_EMP_UHRM_INFO MEM ");
		sb.append("WHERE MEM.PRIVILEGEID = 'UHRM002' ");
		sb.append("AND MEM.UHRM_CODE IS NOT NULL ");
		
		switch (priID) {
			case "UHRM002" :
				sb.append("AND MEM.EMP_ID = :loginID ");
				queryCondition.setObject("loginID", (String) getUserVariable(FubonSystemVariableConsts.LOGINID));
				
				outputVO.setuEmpID((String) getUserVariable(FubonSystemVariableConsts.LOGINID));
				break;
			case "UHRM012" : 
				sb.append("AND EXISTS (SELECT 1 FROM TBORG_MEMBER MT WHERE MT.DEPT_ID = MEM.DEPT_ID AND MT.DEPT_ID = :loginArea) ");
				queryCondition.setObject("loginArea", (String) getUserVariable(FubonSystemVariableConsts.LOGIN_AREA));
				
				break;
			case "UHRM013" : 
			case "UHRM045" : 
			case "UHRM046" : 
			case "UHRM014" : 
				outputVO.setuEmpID("");
			default :
				if (StringUtils.isNotEmpty(inputVO.getBranchAreaID())) {
					sb.append("AND EXISTS (SELECT 1 FROM TBORG_MEMBER MT WHERE MT.DEPT_ID = MEM.DEPT_ID AND MT.DEPT_ID = :branchAreaID) ");
					queryCondition.setObject("branchAreaID", inputVO.getBranchAreaID());
				} else if (StringUtils.isNotEmpty(inputVO.getRegionCenterID())) {
					sb.append("AND EXISTS (SELECT 1 FROM TBORG_MEMBER MT WHERE MT.DEPT_ID = MEM.DEPT_ID AND EXISTS (SELECT 1 FROM TBORG_DEFN D WHERE MT.DEPT_ID = D.DEPT_ID AND D.PARENT_DEPT_ID = :regionCenterID)) ");
					queryCondition.setObject("regionCenterID", inputVO.getRegionCenterID());
				} else if (StringUtils.equals("013", priID)) {
					sb.append("AND EXISTS (SELECT 1 FROM TBORG_MEMBER MT WHERE MT.DEPT_ID = MEM.DEPT_ID AND EXISTS (SELECT 1 FROM TBORG_DEFN D WHERE MT.DEPT_ID = D.DEPT_ID AND D.PARENT_DEPT_ID = :regionCenterID)) ");
					queryCondition.setObject("regionCenterID", (String) getUserVariable(FubonSystemVariableConsts.LOGIN_REGION));
				}
					
				break;
		}
		sb.append("ORDER BY MEM.EMP_ID, MEM.CODE_TYPE ");

		queryCondition.setQueryString(sb.toString());
		
		outputVO.setUhrmList(dam.exeQuery(queryCondition));
		
		return outputVO;
	}

	/**
	 * 取得031人員清單(由員工檔+角色檔) - 前端入口
	 * 
	 * @param body
	 * @param header
	 * @throws Exception
	 */
	public void get031EmpList (Object body, IPrimitiveMap header) throws Exception {
		sendRtnObject(this.get031EmpList(body));
	}
	
	/**
	 * 取得031人員清單(由員工檔+角色檔) - 後端入口
	 * 
	 * @param body
	 * @throws Exception
	 */
	public ORG260OutputVO get031EmpList(Object body) throws Exception {
		
		ORG260InputVO inputVO = (ORG260InputVO) body;
		ORG260OutputVO outputVO = new ORG260OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT PRIVILEGEID ");
		sb.append("FROM TBSYSSECUROLPRIASS ");
		sb.append("WHERE ROLEID = :roleID ");
		
		queryCondition.setObject("roleID", (String) getUserVariable(FubonSystemVariableConsts.LOGINROLE));
		
		queryCondition.setQueryString(sb.toString());
		
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		String priID = (String) list.get(0).get("PRIVILEGEID");
		
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sb = new StringBuffer();
		sb.append("SELECT DISTINCT MEM.EMP_NAME AS LABEL, MEM.EMP_ID AS DATA ");
		sb.append("FROM VWORG_EMP_UHRM_INFO MEM ");
		sb.append("WHERE 1 = 1 ");
		sb.append("AND MEM.ROLE_ID IS NOT NULL ");
		
		switch (priID) {
			case "UHRM002" :
				sb.append("AND MEM.EMP_ID = :loginID ");
				queryCondition.setObject("loginID", (String) getUserVariable(FubonSystemVariableConsts.LOGINID));
				
				outputVO.setuEmpID((String) getUserVariable(FubonSystemVariableConsts.LOGINID));
				break;
			case "UHRM012" : 
				sb.append("AND EXISTS (SELECT 1 FROM TBORG_MEMBER MT WHERE MT.DEPT_ID = MEM.DEPT_ID AND MT.EMP_ID = :loginID) ");
				queryCondition.setObject("loginID", (String) getUserVariable(FubonSystemVariableConsts.LOGINID));
				
				break;
			case "UHRM013" : 
			case "UHRM045" : 
			case "UHRM046" : 
			case "UHRM014" : 
				outputVO.setuEmpID("");
				break;
			default:
				if (StringUtils.isNotEmpty(inputVO.getBranchAreaID())) {
					sb.append("AND EXISTS (SELECT 1 FROM TBORG_MEMBER MT WHERE MT.DEPT_ID = MEM.DEPT_ID AND MT.DEPT_ID = :branchAreaID) ");
					queryCondition.setObject("branchAreaID", inputVO.getBranchAreaID());
				} else if (StringUtils.isNotEmpty(inputVO.getRegionCenterID())) {
					sb.append("AND EXISTS (SELECT 1 FROM TBORG_MEMBER MT WHERE MT.DEPT_ID = MEM.DEPT_ID AND EXISTS (SELECT 1 FROM TBORG_DEFN D WHERE MT.DEPT_ID = D.DEPT_ID AND D.PARENT_DEPT_ID = :regionCenterID)) ");
					queryCondition.setObject("regionCenterID", inputVO.getRegionCenterID());
				} else if (StringUtils.equals("013", priID)) {
					sb.append("AND EXISTS (SELECT 1 FROM TBORG_MEMBER MT WHERE MT.DEPT_ID = MEM.DEPT_ID AND EXISTS (SELECT 1 FROM TBORG_DEFN D WHERE MT.DEPT_ID = D.DEPT_ID AND D.PARENT_DEPT_ID = :regionCenterID)) ");
					queryCondition.setObject("regionCenterID", (String) getUserVariable(FubonSystemVariableConsts.LOGIN_REGION));
				}
					
				break;
		}
		
		sb.append("ORDER BY MEM.EMP_ID ");
		
		queryCondition.setQueryString(sb.toString());
		
		outputVO.setUhrmList(dam.exeQuery(queryCondition));
		
		return outputVO;
	}
	
}
