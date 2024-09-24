package com.systex.jbranch.app.server.fps.pms434;

import java.io.FileOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataManager.DataManager;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.query.ResultIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.CSVUtil;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * PMS434
 * 
 * @author Jeff Cheng
 * @date 2023/11/30
 * @spec null
 */
@Component("pms434")
@Scope("request")
public class PMS434 extends FubonWmsBizLogic {

	private Logger logger = LoggerFactory.getLogger(PMS434.class);

	private DataAccessManager dam = null;

	// 初始化查核區間
	public void initCheckInterval(Object body, IPrimitiveMap header) throws JBranchException, ParseException {
		PMS434OutputVO outputVO = new PMS434OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_SQL);

		StringBuilder sql = new StringBuilder();
		sql.append("SELECT DISTINCT YYYYMM AS LABEL, YYYYMM AS DATA ");
		sql.append("FROM TBJSB_ADDRTELMAIL_CHK ");
		sql.append("ORDER BY YYYYMM DESC");

		condition.setQueryString(sql.toString());
		outputVO.setResultList(dam.exeQuery(condition));

		this.sendRtnObject(outputVO);
	}

	public void query(Object body, IPrimitiveMap header) throws JBranchException, ParseException {
		PMS434InputVO inputVO = (PMS434InputVO) body;
		PMS434OutputVO outputVO = null;

		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		if ("0".equals(inputVO.getCompareResult())) {
			// 相符
			outputVO = this.query_0(inputVO, condition);
		} else if ("2".equals(inputVO.getCompareResult())) {
			// 集中度
			outputVO = this.query_2(inputVO, condition);
		}

		this.sendRtnObject(outputVO);
	}

	// 相符查詢
	private PMS434OutputVO query_0(PMS434InputVO inputVO, QueryConditionIF condition) throws JBranchException {
		PMS434OutputVO outputVO = new PMS434OutputVO();
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT ");
		sql.append("CHK.SEQ, CHK.POLICY_NO, CHK.CUST_ID, CHK.CHK_TYPE, CHK.CHECK_SOURCE_CONTENT, CHK.SOURCE_TYPE, ");
		sql.append("CHK.EMP_CUST_ID, CHK.EMP_NAME, CHK.BRANCH_ID, CHK.MATCH_YN, CHK.CHECKED_RESULT, ");
		sql.append("REL.RELATION, REL.OTHER_REL, REL.INSURANCE_NO ");
		sql.append("FROM TBJSB_ADDRTELMAIL_CHK CHK ");
		sql.append("LEFT JOIN TBCUST_EMP_REL REL ");
		sql.append("ON CHK.CUST_ID = REL.CUST_ID AND CHK.EMP_CUST_ID = REL.EMP_CUST_ID ");
		sql.append("WHERE 1 = 1 ");

		if (StringUtils.isNotBlank(inputVO.getCheckInterval())) {
			sql.append("AND CHK.YYYYMM = :checkInterval ");
			condition.setObject("checkInterval", inputVO.getCheckInterval());
		}

		if (StringUtils.isNotBlank(inputVO.getSimilarInfo()) && !StringUtils.equals("0", inputVO.getSimilarInfo())) {
			sql.append("AND CHK.CHK_TYPE = :similarInfo ");
			condition.setObject("similarInfo", inputVO.getSimilarInfo());
		}

		if (StringUtils.isNotBlank(inputVO.getCompareSource())
				&& !StringUtils.equals("0", inputVO.getCompareSource())) {
			sql.append("AND CHK.SOURCE_TYPE = :compareSource ");
			condition.setObject("compareSource", inputVO.getCompareSource());
		}

		if (StringUtils.isNotBlank(inputVO.getCustID())) {
			sql.append("AND CHK.CUST_ID = :custID ");
			condition.setObject("custID", inputVO.getCustID());
		}

		if (StringUtils.isNotBlank(inputVO.getEmpCustID())) {
			sql.append("AND CHK.EMP_CUST_ID = :empCustID ");
			condition.setObject("empCustID", inputVO.getEmpCustID());
		}

		sql.append("ORDER BY CHK.CHECK_SOURCE_CONTENT");
		condition.setQueryString(sql.toString());

		ResultIF list = dam.executePaging(condition, inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());

		// 按分頁，不用重查
		if (inputVO.getList().size() == 0) {
			outputVO.setTotalList(dam.exeQuery(condition));
		} else {
			outputVO.setTotalList(inputVO.getList());
		}
		outputVO.setTotalPage(list.getTotalPage());
		outputVO.setResultList(list);
		outputVO.setTotalRecord(list.getTotalRecord());
		outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());

		return outputVO;
	}

	// 集中度查詢
	private PMS434OutputVO query_2(PMS434InputVO inputVO, QueryConditionIF condition) throws JBranchException {
		PMS434OutputVO outputVO = new PMS434OutputVO();
		StringBuilder sql = new StringBuilder();
		sql.append("WITH A AS ( ");
		sql.append("SELECT ");
		sql.append("  CHK.SEQ, CHK.YYYYMM, CHK.POLICY_NO, CHK.CUST_ID_A, CHK.CUST_ID_B, CHK.SAL_COMPANY, CHK.CHK_TYPE, CHK.CHECK_SOURCE_CONTENT, ");
		sql.append("  REL.RELATION, REL.OTHER_REL, REL.INSURANCE_NO, ");
		sql.append("  CHK.EMP_CUST_ID, CHK.EMP_NAME, CHK.BRANCH_ID, CHK.MATCH_YN, CHK.CHECKED_RESULT ");
		sql.append("FROM ");
		sql.append("  TBJSB_ADDRTELMAIL_CON_CHK CHK ");
		sql.append("LEFT JOIN ");
		sql.append("  TBCUST_EMP_REL REL ");
		sql.append("ON CHK.CUST_ID_A = REL.CUST_ID AND CHK.CUST_ID_B = REL.EMP_CUST_ID ");
		sql.append("), ");
		sql.append("B AS ( ");
		sql.append("SELECT ");
		sql.append("  YYYYMM, CHK_TYPE, CHECK_SOURCE_CONTENT, COUNT(CHECK_SOURCE_CONTENT) TOTAL ");
		sql.append("FROM ");
		sql.append("  TBJSB_ADDRTELMAIL_CON_CHK ");
		sql.append("GROUP BY YYYYMM, CHK_TYPE, CHECK_SOURCE_CONTENT ");
		sql.append(") ");
		sql.append("SELECT ");
		sql.append("  B.TOTAL || '-' || ");
		sql.append("  RANK()OVER(PARTITION BY A.YYYYMM, A.CHK_TYPE, A.CHECK_SOURCE_CONTENT ORDER BY A.SEQ) TOTAL_ROW_COUNT, ");
		sql.append("  B.TOTAL, A.* ");
		sql.append("FROM ");
		sql.append(" A JOIN B on A.CHECK_SOURCE_CONTENT = B.CHECK_SOURCE_CONTENT ");
		sql.append(" AND A.CHK_TYPE = B.CHK_TYPE AND A.YYYYMM = B.YYYYMM ");
		sql.append("WHERE B.TOTAL > 3 ");

		if (StringUtils.isNotBlank(inputVO.getCheckInterval())) {
			sql.append("AND A.YYYYMM = :checkInterval ");
			condition.setObject("checkInterval", inputVO.getCheckInterval());
		}

		if (StringUtils.isNotBlank(inputVO.getSimilarInfo()) && !StringUtils.equals("0", inputVO.getSimilarInfo())) {
			sql.append("AND A.CHK_TYPE = :similarInfo ");
			condition.setObject("similarInfo", inputVO.getSimilarInfo());
		}

		sql.append("ORDER BY A.CHECK_SOURCE_CONTENT ");

		condition.setQueryString(sql.toString());

		List<Map<String, Object>> list = null;

		if (inputVO.getList().size() == 0) {
			list = checkConShow(dam.exeQuery(condition));
			if (StringUtils.isNotBlank(inputVO.getCustID()) || StringUtils.isNotBlank(inputVO.getEmpCustID())) {
				list = removeCustID(list, inputVO.getCustID(), inputVO.getEmpCustID());
			}
			outputVO.setTotalList(list);
		} else { // 按分頁，不用重查
			list = inputVO.getList();
			outputVO.setTotalList(inputVO.getList());
		}

		int size = list.size();
		int start = inputVO.getCurrentPageIndex() * 10;
		int end = start + 10 < size ? start + 10 : size;
		outputVO.setTotalRecord(size);
		outputVO.setTotalPage(size / 10 + (size % 10 != 0 ? 1 : 0));
		outputVO.setResultList(outputVO.getTotalList().subList(start, end));
		outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());
		return outputVO;
	}

	// 檢查集中度呈現資料: 群組都有異動 => 不顯示 群組其中有沒異動 => 顯示
	private List<Map<String, Object>> checkConShow(List<Map<String, Object>> list) {
		List<Map<String, Object>> returnList = new ArrayList<>();

		Map<String, Object> groupFirst = null;
		String firstChkType = "";
		String firstChkSrcContent = "";
		Integer firstTotal = 0;
		String firstChkResult = "";

		Integer nextGroupIdx = 0;

		int jump = 1;

		for (int i = 0; i < list.size(); i += jump) {
			Map<String, Object> groupNext = null;
			String nextChkType = "";
			String nextChkSrcContent = "";
			String nextChkResult = "";

			if (jump != 1) {
				jump = 1;
			}

			if (groupFirst == null) {
				groupFirst = list.get(i);
				firstChkType = String.valueOf(groupFirst.get("CHK_TYPE"));
				firstChkSrcContent = String.valueOf(groupFirst.get("CHECK_SOURCE_CONTENT"));
				firstTotal = Integer.valueOf(String.valueOf(groupFirst.get("TOTAL")));
				firstChkResult = String.valueOf(groupFirst.get("CHECKED_RESULT"));

				// 下個群組起始索引
				nextGroupIdx = i + firstTotal;

				// 群組第一個沒做異動, 檢查下個群組
				if (!StringUtils.equals("Y", firstChkResult)) {
					jump = firstTotal;
					groupFirst = null;
					for (int j = i; j < nextGroupIdx; j++) {
						returnList.add(list.get(j));
					}
				}
				continue;
			} else {
				groupNext = list.get(i);
				nextChkType = String.valueOf(groupNext.get("CHK_TYPE"));
				nextChkSrcContent = String.valueOf(groupNext.get("CHECK_SOURCE_CONTENT"));
				nextChkResult = String.valueOf(groupNext.get("CHECKED_RESULT"));
			}

			if (StringUtils.equals(firstChkType, nextChkType)
					&& StringUtils.equals(firstChkSrcContent, nextChkSrcContent)) {
				if (!StringUtils.equals("Y", nextChkResult)) {
					// 檢查這個群組中, 如其中沒做異動, 跳下個群組
					jump = nextGroupIdx - i;
					groupFirst = null;
					for (int k = nextGroupIdx - firstTotal; k < nextGroupIdx; k++) {
						returnList.add(list.get(k));
					}
				}
			}

			if (i == nextGroupIdx - 1) {
				groupFirst = null;
			}

		}
		return returnList;
	}

	private List<Map<String, Object>> removeCustID(List<Map<String, Object>> list, String custID, String empCustID) {
		List<Map<String, Object>> returnList = new ArrayList<>();
		if (StringUtils.isNotBlank(custID) && StringUtils.isBlank(empCustID)) {
			for (Map<String, Object> map : list) {
				String mapCustIDA = (String) map.get("CUST_ID_A");
				if (StringUtils.equals(mapCustIDA, custID)) {
					returnList.add(map);
				}
			}
		} else if (StringUtils.isNotBlank(empCustID) && StringUtils.isBlank(custID)) {
			for (Map<String, Object> map : list) {
				String mapCustIDB = (String) map.get("CUST_ID_B");
				if (StringUtils.equals(mapCustIDB, empCustID)) {
					returnList.add(map);
				}
			}
		} else if (StringUtils.isNotBlank(empCustID) && StringUtils.isNotBlank(custID)) {
			for (Map<String, Object> map : list) {
				String mapCustIDA = (String) map.get("CUST_ID_A");
				String mapCustIDB = (String) map.get("CUST_ID_B");
				if (StringUtils.equals(mapCustIDB, empCustID) && StringUtils.equals(mapCustIDA, custID)) {
					returnList.add(map);
				}
			}
		}
		return returnList;
	}

	// 重新比對
	public void reCompare(Object body, IPrimitiveMap header) throws JBranchException, ParseException {
		PMS434InputVO inputVO = (PMS434InputVO) body;
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_SQL);

		StringBuffer sql = new StringBuffer();
		sql.append("UPDATE TBJSB_ADDRTELMAIL_CHK CHK ");
		sql.append("SET (CHECKED_RESULT) = (");
		sql.append("SELECT 'Y' FROM TBCUST_EMP_REL REL WHERE REL.CUST_ID = CHK.CUST_ID  ");
		sql.append("AND REL.EMP_CUST_ID = CHK.EMP_CUST_ID) ");
		sql.append("WHERE CHK.YYYYMM = :YYYYMM ");
//		sql.append("AND (CHK.CHECKED_RESULT <> 'N' OR CHK.CHECKED_RESULT IS NULL) ");
		condition.setObject("YYYYMM", inputVO.getCheckInterval());
		condition.setQueryString(sql.toString());
		dam.exeUpdate(condition);

		this.sendRtnObject(null);
	}

	// 檢查TBCUST_EMP_REL，存在 => 覆蓋 不存在 => 新增
	public void checkUpdateInsert(Object body, IPrimitiveMap header) throws JBranchException, ParseException {
		PMS434InputVO inputVO = (PMS434InputVO) body;
		dam = this.getDataAccessManager();
		
		Map<String, Object> updateMap = new HashMap<>();
		Map<String, Object> insertMap = new HashMap<>();
		Map<String, Object> deleteMap = new HashMap<>();
		
		boolean uploadMark = inputVO.getUploadMark();
		int updateRel_Row = 0;
		int insertRel_Row = 0;
		int deleteRel_Row = 0;

		for (Map<String, Object> map : inputVO.getList()) {
			QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuilder sql = new StringBuilder();
			sql.append("SELECT 'Y' FROM TBCUST_EMP_REL WHERE CUST_ID = :custId AND EMP_CUST_ID = :empCustId");
			condition.setQueryString(sql.toString());
			if (!"2".equals(inputVO.getCompareResult()) || uploadMark) {
				condition.setObject("custId", map.get("CUST_ID"));
				condition.setObject("empCustId", map.get("EMP_CUST_ID"));
			} else {
				condition.setObject("custId", map.get("CUST_ID_A"));
				condition.setObject("empCustId", map.get("CUST_ID_B"));
			}
			List<Map<String, Object>> list = dam.exeQuery(condition);

			if (list.size() > 0 && !"N".equals(map.get("CHECKED_RESULT"))) {
				updateMap.putAll(map);
			} else if ("Y".equals(map.get("CHECKED_RESULT")) || (uploadMark && list.size() == 0)) {
				insertMap.putAll(map);
			} else if (list.size() > 0 && "N".equals(map.get("CHECKED_RESULT"))) {
				deleteMap.putAll(map);
			}

			if (updateMap.size() > 0) {
				updateRel_Row += this.updateRel(inputVO, updateMap, condition);
				updateMap.clear();
			}

			if (insertMap.size() > 0) {
				insertRel_Row += this.insertRel(inputVO, insertMap, condition);
				insertMap.clear();
			}

			if (deleteMap.size() > 0) {
				deleteRel_Row += this.deleteRel(inputVO, deleteMap, condition);
				deleteMap.clear();
			}
		}

		if (!uploadMark) {
			if (updateRel_Row > 0 || insertRel_Row > 0 || deleteRel_Row > 0 || inputVO.getList().size() > 0) {
				if (!StringUtils.equals("2", inputVO.getCompareResult())) {
					this.updateChk(inputVO, dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL));
				} else {
					this.updateConChk(inputVO, dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL));
				}
			}
		} else {
			if ("1".equals(inputVO.getCompareType())) {
				this.updateChk(inputVO, dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL));
			} else {
				this.updateConChk(inputVO, dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL));
			}
		}

		sendRtnObject(null);
	}

	private int updateRel(PMS434InputVO inputVO, Map<String, Object> map, QueryConditionIF condition)
			throws JBranchException, ParseException {
		int updateRel_Row = 0;
		StringBuffer sql = new StringBuffer();
		sql.append("UPDATE TBCUST_EMP_REL ");
		sql.append("SET RELATION = :relation, INSURANCE_NO = :insuranceNo, COMPARE = :compare ");
		if ("9".equals(map.get("RELATION"))) {
			sql.append(", OTHER_REL = :otherRelation ");
			condition.setObject("otherRelation", map.get("OTHER_REL"));
		} 
		sql.append("WHERE CUST_ID = :custId AND EMP_CUST_ID = :empCustId");

		if (!StringUtils.equals("2", inputVO.getCompareResult()) || inputVO.getUploadMark()) {
			condition.setObject("custId", map.get("CUST_ID"));
			condition.setObject("empCustId", map.get("EMP_CUST_ID"));
		} else {
			condition.setObject("custId", map.get("CUST_ID_A"));
			condition.setObject("empCustId", map.get("CUST_ID_B"));
		}
		condition.setObject("relation", map.get("RELATION"));
		condition.setObject("insuranceNo", map.get("INSURANCE_NO"));
		condition.setObject("compare", inputVO.getCompareType());
		condition.setQueryString(sql.toString());
		updateRel_Row = dam.exeUpdate(condition);

		return updateRel_Row;
	}

	private int insertRel(PMS434InputVO inputVO, Map<String, Object> map, QueryConditionIF condition)
			throws JBranchException, ParseException {
		int insertRel_Row = 0;
		StringBuffer sql = new StringBuffer();
		sql.append("INSERT INTO TBCUST_EMP_REL ");
		sql.append("(CUST_ID, EMP_CUST_ID, RELATION, INSURANCE_NO, COMPARE");
		
		if ("9".equals(map.get("RELATION"))) {
			sql.append(", OTHER_REL) ");
			sql.append("VALUES (:custId, :empCustId, :relation, :insuranceNo, :compare, :otherRelation) ");
			condition.setObject("otherRelation", map.get("OTHER_REL"));
		} else {
			sql.append(") ");
			sql.append("VALUES (:custId, :empCustId, :relation, :insuranceNo, :compare) ");
		}

		if (!StringUtils.equals("2", inputVO.getCompareResult()) || inputVO.getUploadMark()) {
			condition.setObject("custId", map.get("CUST_ID"));
			condition.setObject("empCustId", map.get("EMP_CUST_ID"));
		} else {
			condition.setObject("custId", map.get("CUST_ID_A"));
			condition.setObject("empCustId", map.get("CUST_ID_B"));
		}

		condition.setObject("relation", map.get("RELATION"));
		condition.setObject("insuranceNo", map.get("INSURANCE_NO"));
		condition.setObject("compare", inputVO.getCompareType());
		condition.setQueryString(sql.toString());
		insertRel_Row = dam.exeUpdate(condition);

		return insertRel_Row;
	}

	private int deleteRel(PMS434InputVO inputVO, Map<String, Object> map, QueryConditionIF condition)
			throws JBranchException, ParseException {
		int deleteRel_Row = 0;
		StringBuffer sql = new StringBuffer();
		sql.append("DELETE FROM TBCUST_EMP_REL WHERE CUST_ID = :custId AND EMP_CUST_ID = :empCustId ");

		if (!"2".equals(inputVO.getCompareResult())) {
			condition.setObject("custId", map.get("CUST_ID"));
			condition.setObject("empCustId", map.get("EMP_CUST_ID"));
		} else {
			condition.setObject("custId", map.get("CUST_ID_A"));
			condition.setObject("empCustId", map.get("CUST_ID_B"));
		}

		condition.setQueryString(sql.toString());
		deleteRel_Row = dam.exeUpdate(condition);
		return deleteRel_Row;
	}

	private void updateChk(PMS434InputVO inputVO, QueryConditionIF condition) throws JBranchException, ParseException {
		String loginID = (String) getCommonVariable(SystemVariableConsts.LOGINID);
		for (Map<String, Object> map : inputVO.getList()) {
			StringBuffer sql = new StringBuffer();
			sql.append("UPDATE TBJSB_ADDRTELMAIL_CHK SET ");
			sql.append("CHECKED_RESULT = :chkResult, MODIFIER = :modifier, LASTUPDATE = SYSDATE ");
			if (!inputVO.getUploadMark()) {
//				sql.append("WHERE SEQ = :seq");
//				condition.setObject("seq", map.get("SEQ"));
				sql.append("WHERE CUST_ID = :custId AND EMP_CUST_ID = :empCustId ");
				condition.setObject("custId", map.get("CUST_ID"));
				condition.setObject("empCustId", map.get("EMP_CUST_ID"));
			} else {
				sql.append("WHERE CUST_ID = :custId AND EMP_CUST_ID = :empCustId ");
				condition.setObject("custId", map.get("CUST_ID"));
				condition.setObject("empCustId", map.get("EMP_CUST_ID"));
			}

			condition.setObject("chkResult", map.get("CHECKED_RESULT"));
			condition.setObject("modifier", loginID);
			condition.setQueryString(sql.toString());
			dam.exeUpdate(condition);
		}
	}

	private void updateConChk(PMS434InputVO inputVO, QueryConditionIF condition)
			throws JBranchException, ParseException {
		String loginID = (String) getCommonVariable(SystemVariableConsts.LOGINID);
		for (Map<String, Object> map : inputVO.getList()) {
			StringBuffer sql = new StringBuffer();
			sql.append("UPDATE TBJSB_ADDRTELMAIL_CON_CHK SET ");
			sql.append("CHECKED_RESULT = :chkResult, MODIFIER = :modifier, LASTUPDATE = SYSDATE ");
			sql.append("WHERE CUST_ID_A = :custIdA and CUST_ID_B = :custIdB ");

			if (!inputVO.getUploadMark()) {
				condition.setObject("chkResult", map.get("CHECKED_RESULT"));
				condition.setObject("custIdA", map.get("CUST_ID_A"));
				condition.setObject("custIdB", map.get("CUST_ID_B"));
			} else {
				condition.setObject("chkResult", "Y");
				condition.setObject("custIdA", map.get("CUST_ID"));
				condition.setObject("custIdB", map.get("EMP_CUST_ID"));
			}
			condition.setObject("modifier", loginID);
			condition.setQueryString(sql.toString());
			dam.exeUpdate(condition);
		}
	}

	// 下載範例檔
	public void downloadSimple(Object body, IPrimitiveMap header) throws Exception {
		PMS434InputVO inputVO = (PMS434InputVO) body;
		if (!"2".equals(inputVO.getCompareType())) {
			this.notifyClientToDownloadFile("doc//PMS//PMS432_EXAMPLE.csv", "查核結果上傳範本.csv");
		} else {
			this.notifyClientToDownloadFile("doc//PMS//PMS432_EXAMPLE2.csv", "查核結果上傳範本_集中度.csv");
		}

	}
	
	// 上傳檔案
	public void upload(Object body, IPrimitiveMap header) throws Exception {
		PMS434InputVO inputVO = (PMS434InputVO) body;
		PMS434OutputVO outputVO = new PMS434OutputVO();
		XmlInfo xmlInfo = new XmlInfo();
		dam = this.getDataAccessManager();

		String tempPath = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
		List<String[]> dataCsv = CSVUtil.getBig5CSVFile(tempPath, inputVO.getFileName());

		String[] csvHeader = null;

		if (!"2".equals(inputVO.getCompareType())) {
			csvHeader = new String[] { "客戶ID", "理專ID", "客戶與理專關係", "客戶與理專關係-其他", "證明文件說明(保險文件編號) 限20字" };
		} else {
			csvHeader = new String[] { "客戶ID", "被比對客戶ID", "客戶與被比對客戶ID關係", "客戶與被比對客戶ID關係-其他", "證明文件說明(保險文件編號) 限20字" };
		}

		List<Map<String, Object>> list = new ArrayList<>();

		if (!dataCsv.isEmpty() && dataCsv.size() > 1) {

			Map<String, String> relationMap = xmlInfo.doGetVariable("PMS.RELATION", FormatHelper.FORMAT_3);

			for (int i = 0; i < dataCsv.size(); i++) {
				Map<String, Object> map = new LinkedHashMap<>();
				String[] row = dataCsv.get(i);
				if (i == 0) {
					try {
						if (!csvHeader[0].equals(row[0].trim()))
							throw new Exception(row[0]);
						else if (!csvHeader[1].equals(row[1].trim()))
							throw new Exception(row[1]);
						else if (!csvHeader[2].equals(row[2].trim()))
							throw new Exception(row[2]);
						else if (!csvHeader[3].equals(row[3].trim()))
							throw new Exception(row[3]);
						else if (!csvHeader[4].equals(row[4].trim()))
							throw new Exception(row[4]);
					} catch (Exception ex) {
						throw new APException(ex.getMessage() + ":上傳格式錯誤，請下載範例檔案");
					}
					continue;
				}

				// 客戶ID(身分證字號)
				String custId = row[0].replace("\u3000", "").trim();
				if (StringUtils.isNotBlank(custId)) {
					if (row[0].matches("^[a-zA-Z][0-9]{9}$")) {
						map.put("CUST_ID", custId.toUpperCase());
					} else {
						throw new JBranchException("客戶ID格式錯誤，請輸入正確身份證字號格式!");
					}
				} else {
					throw new JBranchException("請輸入客戶ID!");
				}

				// 理專ID(身分證字號)
				String empCustId = row[1].replace("\u3000", "").trim();
				if (StringUtils.isNotBlank(empCustId)) {
					if (row[1].matches("^[a-zA-Z][0-9]{9}$")) {
						map.put("EMP_CUST_ID", empCustId.toUpperCase());
					} else {
						throw new JBranchException("理專ID格式錯誤，請輸入正確身份證字號格式!");
					}
				} else {
					throw new JBranchException("請輸入理專ID!");
				}

				// 客戶與理專關係(1 ~ 9) : 如是9，客戶與理專關係-其他必填，反之不用填。
				String relation = row[2].trim();
				String otherRel = row[3].trim();
				if (StringUtils.isNotBlank(relation)) {
					if (StringUtils.isNotBlank(relationMap.get(relation))) {
						if ("9".equals(relation)) {
							if (StringUtils.isBlank(otherRel)) {
								throw new JBranchException("客戶與理專關係是9，客戶與理專關係-其他不能為空!");
							} else {
								map.put("RELATION", relation);
								map.put("OTHER_REL", otherRel);
							}
						} else {
							if (StringUtils.isNotBlank(otherRel)) {
								throw new JBranchException("客戶與理專關係是1~8，客戶與理專關係-其他不需填寫!");
							} else {
								map.put("RELATION", relation);
							}
						}
					} else {
						throw new JBranchException("客戶與理專關係格式錯誤，請輸入1~9!");
					}
				} else {
					throw new JBranchException("請輸入客戶與理專關係!");
				}

				// 證明文件說明(保險文件編號) : 限20字
				String insuranceNo = row[4].trim();
				if (StringUtils.isNotBlank(insuranceNo)) {
					if (insuranceNo.length() > 20) {
						throw new JBranchException("證明文件說明(保險文件編號) 限20字!");
					} else {
						map.put("INSURANCE_NO", insuranceNo);
					}
				} else {
					throw new JBranchException("請輸入證明文件說明(保險文件編號)!");
				}

				list.add(map);
			}
		} else {
			throw new JBranchException("上傳檔案，必須有資料!");
		}

		outputVO.setResultList(list);

		this.sendRtnObject(outputVO);
	}
	

	// 匯出 Excel
	public void export(Object body, IPrimitiveMap header) throws Exception {

		PMS434InputVO inputVO = (PMS434InputVO) body;

		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String fileName = "保單聯絡資訊與理專疑似相同查核報表_" + sdf.format(new Date()) + ".xlsx";
		String uuid = UUID.randomUUID().toString();
		String Path = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);

		String filePath = Path + uuid;

		String compareResult = inputVO.getCompareResult();

		String[] xlsxHeader = null;
		Map<String, String[]> xlsxHeader2 = new HashMap<>();
		String[] xlsxMain = null;

		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> SIMILAR_INFO = xmlInfo.doGetVariable("JSB.SIMILAR_INFO", FormatHelper.FORMAT_3);
		Map<String, String> RELATION = xmlInfo.doGetVariable("PMS.RELATION", FormatHelper.FORMAT_3);
		Map<String, String> RELATION_YN = xmlInfo.doGetVariable("PMS.RELATION_YN", FormatHelper.FORMAT_3);
		Map<String, String> CHECK_RESULT_YN = xmlInfo.doGetVariable("PMS.CHECK_RESULT_YN", FormatHelper.FORMAT_3);

		if ("2".equals(compareResult)) {
			// 集中度
			xlsxHeader = new String[] { "小計筆數", "保單號碼", "客戶ID", "被比對客戶", "相似項目", "相同內容", "小計總數", "相同資訊 理專ID/姓名/分行代號",
					"關係戶", "查核結果", "關係說明", "證明文件說明(保險文件編號)" };
			xlsxMain = new String[] { "TOTAL_ROW_COUNT", "POLICY_NO", "CUST_ID_A", "CUST_ID_B", "SAL_COMPANY",
					"CHK_TYPE", "CHECK_SOURCE_CONTENT", "TOTAL", "EMP_INFO", "MATCH_YN", "CHECKED_RESULT", "RELATION",
					"INSURANCE_NO" };

			xlsxHeader2.put("被比對客戶", new String[] { "ID", "薪轉戶公司" });

		} else {
			// 相符
			xlsxHeader = new String[] { "保單號碼", "客戶ID", "相似項目", "相同內容", "比對檔案來源", "相同資訊 理專ID/姓名/分行代號", "關係戶", "查核結果",
					"關係說明", "證明文件說明(保險文件編號)" };
			xlsxMain = new String[] { "POLICY_NO", "CUST_ID", "CHK_TYPE", "CHECK_SOURCE_CONTENT", "SOURCE_TYPE",
					"EMP_INFO", "MATCH_YN", "CHECKED_RESULT", "RELATION", "INSURANCE_NO" };
		}

		SXSSFWorkbook wb = new SXSSFWorkbook();
		SXSSFSheet sheet = wb.createSheet("保單聯絡資訊與理專疑似相同查核報表_" + sdf.format(new Date()));

		Integer index = 0;

		// Header1
		SXSSFRow row = sheet.createRow(index);

		for (int i = 0, cellLocate = 0; i < xlsxHeader.length; i++) {
			SXSSFCell cell = row.createCell(cellLocate);
			cell.setCellValue(xlsxHeader[i]);
			sheet.setColumnWidth(cellLocate, getColumnWidth(xlsxHeader[i]));
			if ("2".equals(compareResult)) {
				if (xlsxHeader2.containsKey(xlsxHeader[i])) {
					String[] header2 = xlsxHeader2.get(xlsxHeader[i]);
					sheet.addMergedRegion(new CellRangeAddress(0, 0, cellLocate, cellLocate + header2.length - 1));
					cellLocate += header2.length;
					continue;
				} else {
					sheet.addMergedRegion(new CellRangeAddress(0, 1, cellLocate, cellLocate));
				}
			}
			cellLocate++;
		}
		index++;

		// Header2
		if ("2".equals(compareResult)) {
			row = sheet.createRow(index);
			for (int i = 0; i < xlsxHeader.length; i++) {
				if (xlsxHeader2.containsKey(xlsxHeader[i])) {
					String[] header2 = xlsxHeader2.get(xlsxHeader[i]);
					for (int j = 0; j < header2.length; j++) {
						SXSSFCell cell = row.createCell(i + j);
						cell.setCellValue(header2[j]);
						sheet.setColumnWidth(i + j, getColumnWidth(header2[j]));
					}
				}
			}
			index++;
		}

		this.dealEmpInfo(inputVO.getList());

		for (Map<String, Object> map : inputVO.getList()) {

			row = sheet.createRow(index);

			for (int i = 0; i < xlsxMain.length; i++) {
				SXSSFCell cell = row.createCell(i);
				switch (xlsxMain[i]) {
				case "SAL_COMPANY":
					cell.setCellValue(String.valueOf(map.get(xlsxMain[i]) == null ? "" : map.get(xlsxMain[i])));
					break;
				case "CHK_TYPE":
					cell.setCellValue(SIMILAR_INFO.get(String.valueOf(map.get(xlsxMain[i]))));
					break;
				case "TOTAL":
					cell.setCellValue(String.format("%.0f", map.get(xlsxMain[i])));
					break;
				case "CHECKED_RESULT":
					cell.setCellValue(CHECK_RESULT_YN.get(checkIsNull(map, xlsxMain[i])));
					break;
				case "MATCH_YN":
					cell.setCellValue(RELATION_YN.get(checkIsNull(map, xlsxMain[i])));
					break;
				case "RELATION":
					String rel = String.valueOf(map.get(xlsxMain[i]));
					if (!"9".equals(rel)) {
						cell.setCellValue(RELATION.get(rel));
					} else {
						cell.setCellValue(RELATION.get(rel) + "-" + String.valueOf(map.get("OTHER_REL")));
					}
					break;
				default:
					cell.setCellValue(checkIsNull(map, xlsxMain[i]));
					break;
				}
			}
			index++;
		}

		wb.write(new FileOutputStream(filePath));
		notifyClientToDownloadFile(DataManager.getSystem().getPath().get("temp").toString() + uuid, fileName);
		wb.dispose();
		wb.close();
		sendRtnObject(null);
	}

	private String checkIsNull(Map<String, Object> map, String key) {
		if (StringUtils.isNotBlank(ObjectUtils.toString(map.get(key)))) {
			return String.valueOf(map.get(key));
		} else {
			return "";
		}
	}

	// 合併理專資訊
	private void dealEmpInfo(List<Map<String, Object>> list) {
		for (Map<String, Object> map : list) {
			StringBuilder sb = new StringBuilder();
			sb.append(map.get("EMP_CUST_ID"));
			sb.append("/");
			sb.append(map.get("EMP_NAME"));
			sb.append("/");
			sb.append(map.get("BRANCH_ID"));
			map.remove("EMP_CUST_ID");
			map.remove("EMP_NAME");
			map.remove("BRANCH_ID");
			map.put("EMP_INFO", sb.toString());
		}
	}

	private Integer getColumnWidth(String column) {
		Integer width = 0;
		switch (column) {
		case "相同內容":
			width = 15000;
			break;
		case "相同資訊 理專ID/姓名/分行代號":
		case "證明文件說明(保險文件編號)":
			width = 8700;
			break;
		case "查核結果":
		case "關係說明":
			width = 5500;
			break;
		default:
			if (!"被比對客戶".equals(column))
				width = 3700;
			break;
		}

		return width;
	}
}
