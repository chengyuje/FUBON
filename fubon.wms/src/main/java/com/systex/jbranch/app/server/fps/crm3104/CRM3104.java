package com.systex.jbranch.app.server.fps.crm3104;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.CSVUtil;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

@Component("crm3104")
@Scope("request")
public class CRM3104  extends FubonWmsBizLogic {
	private DataAccessManager dam = null;	
	private Logger logger = LoggerFactory.getLogger(CRM3104.class);
	private List<Map<String, Object>> scsslist = null ; 
	
	/**查詢 **/
	public void inquire(Object body, IPrimitiveMap header) throws JBranchException {
		CRM3104InputVO inputVO = (CRM3104InputVO) body;
		CRM3104OutputVO return_VO = new CRM3104OutputVO();
		
//		if (StringUtils.isBlank(inputVO.getPRJ_ID())) {//專案代碼為必輸欄位
//			throw new APException("請先選擇專案");
//		}
		
		dam = this.getDataAccessManager();		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		
		sql.append("SELECT A.PRJ_ID, A.CUST_ID, D.CUST_NAME, B.PRJ_NAME, C.ACT_DATE, TO_CHAR(C.ACT_DATE, 'YYYY-MM-DD') AS ACT_DATE_STR, ");
		sql.append(" C.ORG_AO_BRH, C.ORG_AO_CODE, E.EMP_NAME AS ORG_EMP_NAME, E.EMP_ID AS ORG_EMP_ID, G.DEPT_NAME AS ORG_BRANCH_NAME, ");
		sql.append(" C.NEW_AO_BRH, C.NEW_AO_CODE, F.EMP_NAME AS NEW_EMP_NAME, F.EMP_ID AS NEW_EMP_ID, H.DEPT_NAME AS NEW_BRANCH_NAME, ");
		sql.append(" B.PRJ_EXE_DATE, TO_CHAR(B.PRJ_EXE_DATE, 'YYYY-MM-DD') AS PRJ_EXE_DATE_STR, C.TRS_TYPE ");
		sql.append(" FROM TBCRM_TRS_PRJ_ROTATION_D A ");
		sql.append(" INNER JOIN TBCRM_TRS_PRJ_MAST B ON B.PRJ_ID = A.PRJ_ID ");
		sql.append(" INNER JOIN TBCRM_TRS_AOCHG_PLIST C ON C.CUST_ID = A.CUST_ID ");
		sql.append(" LEFT JOIN TBCRM_CUST_MAST D ON D.CUST_ID = A.CUST_ID ");
		sql.append(" LEFT JOIN VWORG_EMP_INFO E ON E.AO_CODE = C.ORG_AO_CODE ");
		sql.append(" LEFT JOIN VWORG_EMP_INFO F ON F.AO_CODE = C.NEW_AO_CODE ");
		sql.append(" LEFT JOIN TBORG_DEFN G ON G.DEPT_ID = C.ORG_AO_BRH ");
		sql.append(" LEFT JOIN TBORG_DEFN H ON H.DEPT_ID = C.NEW_AO_BRH ");
		sql.append(" WHERE A.STATUS = '7' "); //輪調已執行_未帶走客戶
		sql.append("   AND C.PROCESS_STATUS = 'S' AND C.NEW_AO_CODE IN (SELECT AO_CODE FROM TBORG_SALES_AOCODE WHERE EMP_ID = A.EMP_ID) "); //已完成移轉且新理專為輪調理專
		sql.append("   AND TRUNC(C.ACT_DATE) BETWEEN TRUNC(B.PRJ_EXE_DATE) AND TRUNC(ADD_MONTHS(B.PRJ_EXE_DATE, 12)) "); //輪調執行一年內有移轉
		// where
		if (!StringUtils.isBlank(inputVO.getPRJ_ID())) {
			sql.append(" AND A.PRJ_ID = :prjId ");
			queryCondition.setObject("prjId", inputVO.getPRJ_ID());
		}
		if (!StringUtils.isBlank(inputVO.getEmp_id())) {
			sql.append(" AND A.EMP_ID = :empId ");
			queryCondition.setObject("empId", inputVO.getEmp_id());
		}
		if (!StringUtils.isBlank(inputVO.getRegion_center_id())) {
			sql.append(" AND D.REGION_CENTER_ID = :center_id ");
			queryCondition.setObject("center_id", inputVO.getRegion_center_id());
		}
		if (!StringUtils.isBlank(inputVO.getBranch_area_id())) {
			sql.append(" AND D.BRANCH_AREA_ID = :bra_area ");
			queryCondition.setObject("bra_area", inputVO.getBranch_area_id());
		}
		if (!StringUtils.isBlank(inputVO.getBra_nbr())) {
			sql.append(" AND A.BRANCH_NBR = :bra_nbr ");
			queryCondition.setObject("bra_nbr", inputVO.getBra_nbr());
		}
		if(inputVO.getPRJ_EXE_DATE() != null){
			sql.append(" AND TRUNC(B.PRJ_EXE_DATE) = TRUNC(:prjExeDate) ");
			queryCondition.setObject("prjExeDate", inputVO.getPRJ_EXE_DATE());
		}
		sql.append(" ORDER BY A.BRANCH_NBR, A.EMP_ID ");
		
		queryCondition.setQueryString(sql.toString());
		return_VO.setResultList(dam.exeQuery(queryCondition));
		this.sendRtnObject(return_VO);
	}
	
	// 匯出
	public void export(Object body, IPrimitiveMap header) throws Exception {
		CRM3104InputVO inputVO = (CRM3104InputVO) body;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String fileName = "輪調一年內移回原理專_" + sdf.format(new Date()) + ".csv";
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> TRS_TYPE = xmlInfo.doGetVariable("CRM.TRS_TYPE", FormatHelper.FORMAT_3);

		String[] csvHeader = getCsvHeader();
		String[] csvMain = getCsvMain();
		List<Object[]> csvData = new ArrayList<Object[]>();

		for (Map<String, Object> map : inputVO.getPrintList()) {
			String[] records = new String[csvHeader.length];
			for (int i = 0; i < csvHeader.length; i++) {
				switch (csvMain[i]) {
				case "TRS_TYPE":
					records[i] = TRS_TYPE.get(checkIsNull(map, csvMain[i]));
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
	
	private String checkIsNull(Map map, String key) {

		if (StringUtils.isNotBlank(ObjectUtils.toString(map.get(key)))) {
			return String.valueOf(map.get(key));
		} else {
			return "";
		}
	}
	
	private long changeStringToLong(String str) {
		BigDecimal bd = new BigDecimal(str);
		return bd.longValue();
	}
	
	private String[] getCsvHeader() {
		String[] str = { "專案名稱", "專案執行日", "客戶ID", "客戶姓名", "原分行代碼", "原分行名稱", "原理專員編", "原理專AOCODE", "原理專姓名", "新分行代碼", "新分行名稱", "新理專員編", "新理專AOCODE", "新理專姓名", "移轉日期", "移轉類別"};
		return str;
	}

	private String[] getCsvMain() {
		String[] str = { "PRJ_NAME", "PRJ_EXE_DATE_STR", "CUST_ID", "CUST_NAME", "ORG_AO_BRH", "ORG_BRANCH_NAME", "ORG_EMP_ID", "ORG_AO_CODE", "ORG_EMP_NAME", "NEW_AO_BRH", "NEW_BRANCH_NAME", "NEW_EMP_ID", "NEW_AO_CODE", "NEW_EMP_NAME", "ACT_DATE_STR", "TRS_TYPE"};
		return str;
	}
	
}
