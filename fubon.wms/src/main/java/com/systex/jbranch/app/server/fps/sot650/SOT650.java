package com.systex.jbranch.app.server.fps.sot650;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Blob;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.common.fps.table.TBPRD_FCIVO;
import com.systex.jbranch.app.server.fps.pms429.PMS429InputVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.CSVUtil;
import com.systex.jbranch.platform.common.util.ObjectUtil;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

@Component("sot650")
@Scope("request")
public class SOT650  extends FubonWmsBizLogic {
	private DataAccessManager dam = null;	
	private Logger logger = LoggerFactory.getLogger(SOT650.class);
	
	/**查詢 **/
	public void inquire(Object body, IPrimitiveMap header) throws JBranchException {
		SOT650InputVO inputVO = (SOT650InputVO) body;
		SOT650OutputVO outputVO = new SOT650OutputVO();
		boolean isFCIPMRole = isFCIPMRole();
		
		dam = this.getDataAccessManager();		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		
		//400主機交易明細資料
		sql.append("SELECT C.CUST_ID, A.BATCH_SEQ, A.PROD_ID, A.TRADE_DATE, A.VALUE_DATE, A.PRD_PROFEE_RATE, ");
		sql.append(" CASE WHEN A.PROD_CURR = 'CNY' THEN 'CNH' ELSE A.PROD_CURR END AS PROD_CURR, A.MON_PERIOD, ");
		sql.append(" A.PURCHASE_AMT , A.SPOT_DATE, A.EXPIRE_DATE, A.RM_PROFEE, A.MUREX_SEQ, A.EMP_ID, B.TRADE_SEQ, A.STATUS, ");
		sql.append(" 'Y' AS REPRINT_YN, ");//是否可補印表單
		sql.append(" D.DEPT_ID AS BRANCH_NBR, E.DEPT_NAME AS BRANCH_NAME "); //解說人員分行
		sql.append(" FROM TBSOT_FCI_INV_DETAIL A  ");
		sql.append(" LEFT JOIN TBSOT_FCI_TRADE_D B ON B.BATCH_SEQ = A.BATCH_SEQ ");
		sql.append(" LEFT JOIN TBSOT_TRADE_MAIN C ON C.TRADE_SEQ = B.TRADE_SEQ ");
		sql.append(" LEFT JOIN TBORG_MEMBER D ON D.EMP_ID = B.NARRATOR_ID ");
		sql.append(" LEFT JOIN TBORG_DEFN E ON E.DEPT_ID = D.DEPT_ID ");
		sql.append(" WHERE TRUNC(A.TRADE_DATE) <> TRUNC(SYSDATE) "); //當日交易
		//非FCI PM角色，只能看到OP退件、OP主管未放行、已扣款交易、已放行交易、已解約交易
		if(!isFCIPMRole) {
			//可看到所有狀態
//			sql.append(" AND A.STATUS IN ('1', '2', '3', '4', '5', '8', '9', 'A') "); //OP退件、OP主管未放行、已扣款交易、已放行交易、已解約交易、已到期交易、扣款失敗、入帳失敗
			//非FCI PM角色且未指定客戶ID，可查詢解說人員分行為轄下分行的資料；若指定客戶ID則可查詢不限於自己的客戶
			if(StringUtils.isBlank(inputVO.getCUST_ID())) {
				sql.append(" AND D.DEPT_ID IN ( :branchList ) ");
				queryCondition.setObject("branchList", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
//				sql.append(" AND B.NARRATOR_ID = :loginId "); //只可查詢解說人員為本人
//				queryCondition.setObject("loginId", (String)SysInfo.getInfoValue(FubonSystemVariableConsts.LOGINID));
			}
		}
		if(StringUtils.isNotBlank(inputVO.getCUST_ID())) { //客戶ID
			sql.append(" AND C.CUST_ID = :custId ");
			queryCondition.setObject("custId", inputVO.getCUST_ID());
		}
		if(StringUtils.isNotBlank(inputVO.getPROD_ID())) { //商品代號
			sql.append(" AND A.PROD_ID = :prodId ");
			queryCondition.setObject("prodId", inputVO.getPROD_ID());
		}
		if(StringUtils.isNotBlank(inputVO.getSTATUS())) { //狀態
			sql.append(" AND A.STATUS = :status ");
			queryCondition.setObject("status", inputVO.getSTATUS());
		}
		if(StringUtils.isNotBlank(inputVO.getPROD_CURR())) { //計價幣別
			sql.append(" AND A.PROD_CURR = :prodCurr ");
			queryCondition.setObject("prodCurr", inputVO.getPROD_CURR());
		}
		if(StringUtils.isNotBlank(inputVO.getMON_PERIOD())) { //承作天期
			sql.append(" AND A.MON_PERIOD = :monPeriod ");
			queryCondition.setObject("monPeriod", inputVO.getMON_PERIOD());
		}
		if (inputVO.getsDate() != null) { //申購起日
			sql.append("  AND A.TRADE_DATE >= TRUNC(:startDate) ");
			queryCondition.setObject("startDate", inputVO.getsDate());
		}
		if (inputVO.geteDate() != null) { //申購迄日
			sql.append("  AND A.TRADE_DATE <= TRUNC(:endDate) ");
			queryCondition.setObject("endDate", inputVO.geteDate());
		}
		if (StringUtils.isNotBlank(inputVO.getEMP_ID())) { //理專員編
			sql.append(" AND A.EMP_ID = :empId ");
			queryCondition.setObject("empId", inputVO.getEMP_ID());
		}
		//業管資料
		sql.append(" UNION ");
		sql.append("SELECT B.CUST_ID, A.BATCH_SEQ, C.PROD_ID, A.TRADE_DATE, A.VALUE_DATE, A.PRD_PROFEE_RATE, A.PROD_CURR, A.MON_PERIOD, ");
		sql.append(" A.PURCHASE_AMT , A.SPOT_DATE, A.EXPIRE_DATE, A.RM_PROFEE, '' AS MUREX_SEQ, A.NARRATOR_ID AS EMP_ID, A.TRADE_SEQ, ");
		//交易狀態 	1：暫存 2：風控檢核中 ==> "6"暫存
		//			3：傳送OP交易並列印表單 ==> "7"OP未執行導入
		sql.append(" CASE WHEN B.TRADE_STATUS = '1' THEN '6' WHEN B.TRADE_STATUS = '2' THEN '6' WHEN B.TRADE_STATUS = '3' THEN '7' ELSE '' END AS STATUS, ");
		sql.append(" CASE WHEN B.TRADE_STATUS = '3' THEN 'Y' ELSE 'N' END AS REPRINT_YN, "); //3：傳送OP交易並列印表單，可補印表單
		sql.append(" D.DEPT_ID AS BRANCH_NBR, E.DEPT_NAME AS BRANCH_NAME "); //解說人員分行
		sql.append(" FROM TBSOT_FCI_TRADE_D A  ");
		sql.append(" LEFT JOIN TBSOT_TRADE_MAIN B ON B.TRADE_SEQ = A.TRADE_SEQ ");
		sql.append(" LEFT JOIN TBSOT_FCI_PRODID C ON C.BATCH_SEQ = A.BATCH_SEQ ");
		sql.append(" LEFT JOIN TBORG_MEMBER D ON D.EMP_ID = A.NARRATOR_ID ");
		sql.append(" LEFT JOIN TBORG_DEFN E ON E.DEPT_ID = D.DEPT_ID ");
		sql.append(" WHERE TRUNC(A.TRADE_DATE) <> TRUNC(SYSDATE) "); //當日交易
		sql.append(" AND A.BATCH_SEQ NOT IN (SELECT BATCH_SEQ FROM TBSOT_FCI_INV_DETAIL) "); //尚未導入資料
		//非FCI PM角色，不可看到暫存交易
		if(!isFCIPMRole) {
			sql.append(" AND B.TRADE_STATUS = '3' "); 
			//非FCI PM角色且未指定客戶ID，可查詢解說人員分行為轄下分行的資料；若指定客戶ID則可查詢不限於自己的客戶
			if(StringUtils.isBlank(inputVO.getCUST_ID())) {
				sql.append(" AND D.DEPT_ID IN ( :branchList ) ");
				queryCondition.setObject("branchList", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
//				sql.append(" AND B.NARRATOR_ID = :loginId "); //只可查詢解說人員為本人
//				queryCondition.setObject("loginId", (String)SysInfo.getInfoValue(FubonSystemVariableConsts.LOGINID));
			}
		}	
		if(StringUtils.isNotBlank(inputVO.getCUST_ID())) { //客戶ID
			sql.append(" AND B.CUST_ID = :custId ");
			queryCondition.setObject("custId", inputVO.getCUST_ID());
		}
		if(StringUtils.isNotBlank(inputVO.getPROD_ID())) { //商品代號
			sql.append(" AND C.PROD_ID = :prodId ");
			queryCondition.setObject("prodId", inputVO.getPROD_ID());
		}
		if(StringUtils.isNotBlank(inputVO.getSTATUS())) { //狀態
			sql.append(" AND (CASE WHEN B.TRADE_STATUS = '1' THEN '6' WHEN B.TRADE_STATUS = '2' THEN '6' WHEN B.TRADE_STATUS = '3' THEN '7' ELSE '' END) = :status ");
			
			queryCondition.setObject("status", inputVO.getSTATUS());
		}
		if(StringUtils.isNotBlank(inputVO.getPROD_CURR())) { //計價幣別
			sql.append(" AND A.PROD_CURR = :prodCurr ");
			queryCondition.setObject("prodCurr", inputVO.getPROD_CURR());
		}
		if(StringUtils.isNotBlank(inputVO.getMON_PERIOD())) { //承作天期
			sql.append(" AND A.MON_PERIOD = :monPeriod ");
			queryCondition.setObject("monPeriod", inputVO.getMON_PERIOD());
		}
		if (inputVO.getsDate() != null) { //申購起日
			sql.append("  AND A.TRADE_DATE >= TRUNC(:startDate) ");
			queryCondition.setObject("startDate", inputVO.getsDate());
		}
		if (inputVO.geteDate() != null) { //申購迄日
			sql.append("  AND A.TRADE_DATE <= TRUNC(:endDate) ");
			queryCondition.setObject("endDate", inputVO.geteDate());
		}
		if (StringUtils.isNotBlank(inputVO.getEMP_ID())) { //理專員編
			sql.append(" AND A.NARRATOR_ID = :empId ");
			queryCondition.setObject("empId", inputVO.getEMP_ID());
		}
		sql.append(" ORDER BY TRADE_DATE DESC ");		
		
		queryCondition.setQueryString(sql.toString());
		outputVO.setResultList(dam.exeQuery(queryCondition));
		
		this.sendRtnObject(outputVO);
	}
	
	// 匯出
	public void export(Object body, IPrimitiveMap header) throws Exception {
		SOT650InputVO inputVO = (SOT650InputVO) body;
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String fileName = "FCI歷史交易明細表_" + sdf.format(new Date()) + ".csv";
		XmlInfo xmlInfo = new XmlInfo();

		Map<String, String> INV_STATUS = xmlInfo.doGetVariable("SOT.FCI_INV_STATUS", FormatHelper.FORMAT_3);

		String[] csvHeader = getCsvHeader();
		String[] csvMain = getCsvMain();
		List<Object[]> csvData = new ArrayList<Object[]>();

		for (Map<String, Object> map : inputVO.getPrintList()) {
			String[] records = new String[csvHeader.length];
			map.put("BRANCH", checkIsNull(map, "BRANCH_NBR") + " - " + checkIsNull(map, "BRANCH_NAME"));
			map.put("SM_BRANCH", checkIsNull(map, "SM_BRANCH_NBR") + " - " + checkIsNull(map, "SM_BRANCH_NAME"));

			for (int i = 0; i < csvHeader.length; i++) {
				switch (csvMain[i]) {
				case "CUST_ID":
					String cid = checkIsNull(map, csvMain[i]);
					records[i] = StringUtils.isNotBlank(cid) ? cid.substring(0, 4) + "***" + cid.substring(7) : "";
					break;
				case "STATUS":
					records[i] = INV_STATUS.get(checkIsNull(map, csvMain[i]));
					break;
				case "MON_PERIOD":
					records[i] = checkIsNull(map, csvMain[i]) + "個月";
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

		String url = csv.generateCSV();

		notifyClientToDownloadFile(url, fileName);
	}

	private String[] getCsvHeader() {
		String[] str = { "批號", "客戶ID", "狀態", "商品代號", "申購日", "起息日", "承作天期", "商品收益率", "計價幣別", "承作金額", "比價日", "到期日", "理專收益率", "Murex單號", "理專員編", "分行別" };
		return str;
	}

	private String[] getCsvMain() {
		String[] str = { "BATCH_SEQ", "CUST_ID", "STATUS", "PROD_ID", "TRADE_DATE", "VALUE_DATE", "MON_PERIOD", "PRD_PROFEE_RATE", "PROD_CURR", "PURCHASE_AMT", "SPOT_DATE", "EXPIRE_DATE", "RM_PROFEE", "MUREX_SEQ", "EMP_ID", "BRANCH" };
		return str;
	}
	
	private String checkIsNull(Map map, String key) {

		if (StringUtils.isNotBlank(ObjectUtils.toString(map.get(key)))) {
			return String.valueOf(map.get(key));
		} else {
			return "";
		}
	}
	
	/***
	 * 是否為FCI PM角色
	 * @return true:是 false:否
	 * @throws JBranchException
	 */
	private boolean isFCIPMRole() throws JBranchException {
		dam = this.getDataAccessManager();		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		
		sql.append("SELECT 1 ");
		sql.append(" FROM TBSYSSECUROLPRIASS A ");
		sql.append(" INNER JOIN TBSYSPARAMETER P ON P.PARAM_TYPE = 'SOT.FCI_PM_PRIVILEGE_ID' AND P.PARAM_CODE = A.PRIVILEGEID ");
		sql.append(" WHERE A.ROLEID = :roleID ");
		queryCondition.setObject("roleID", (String)SysInfo.getInfoValue(FubonSystemVariableConsts.LOGINROLE));
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> chkList = dam.exeQuery(queryCondition);
		
		return CollectionUtils.isNotEmpty(chkList) ? true : false;
	}
}
