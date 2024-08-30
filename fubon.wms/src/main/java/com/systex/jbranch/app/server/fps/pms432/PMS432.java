package com.systex.jbranch.app.server.fps.pms432;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
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
 * PMS432
 * 
 * @author Jeff Cheng
 * @date 2023/11/30
 * @spec null
 */
@Component("pms432")
@Scope("request")
public class PMS432 extends FubonWmsBizLogic {

	private Logger logger = LoggerFactory.getLogger(PMS432.class);

	private DataAccessManager dam = null;

	// 初始化查核區間
	public void initCheckInterval(Object body, IPrimitiveMap header) throws JBranchException, ParseException {
		PMS432OutputVO outputVO = new PMS432OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_SQL);

		StringBuilder sql = new StringBuilder();
		sql.append("SELECT DISTINCT YYYYMM ");
		sql.append("FROM TBIOT_ADDRTELMAIL_CHK ");
		sql.append("ORDER BY YYYYMM");

		condition.setQueryString(sql.toString());
		outputVO.setResultList(dam.exeQuery(condition));

		this.sendRtnObject(outputVO);
	}

	public void query(Object body, IPrimitiveMap header) throws JBranchException, ParseException {
		PMS432InputVO inputVO = (PMS432InputVO) body;
		PMS432OutputVO outputVO = null;

		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		if ("0".equals(inputVO.getCompareResult())) {
			// 相符
			outputVO = this.query_0(inputVO, condition);
		} else if ("1".equals(inputVO.getCompareResult())) {
			// 不相符
		} else if ("2".equals(inputVO.getCompareResult())) {
			// 集中度
			outputVO = this.query_2(inputVO, condition);
		}

		this.sendRtnObject(outputVO);
	}

	// 相符查詢
	private PMS432OutputVO query_0(PMS432InputVO inputVO, QueryConditionIF condition) throws JBranchException {
		PMS432OutputVO outputVO = new PMS432OutputVO();
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT ");
		sql.append("CHK.SEQ, CHK.POLICY_NO, CHK.CUST_ID, CHK.CHK_TYPE, CHK.CHECK_SOURCE_CONTENT, CHK.SOURCE_TYPE, ");
		sql.append("CHK.EMP_CUST_ID, CHK.EMP_NAME, CHK.BRANCH_ID, CHK.MATCH_YN, CHK.CHECKED_RESULT, ");
		sql.append("REL.RELATION, REL.OTHER_REL, REL.INSURANCE_NO ");
		sql.append("FROM TBIOT_ADDRTELMAIL_CHK CHK ");
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

	// 不相符 => 匯出不相符資料
	private void query_1(PMS432InputVO inputVO, QueryConditionIF condition) {

	}

	// 集中度查詢
	private PMS432OutputVO query_2(PMS432InputVO inputVO, QueryConditionIF condition) throws JBranchException {
		PMS432OutputVO outputVO = new PMS432OutputVO();
		List<Map<String, Object>> list = null;
		if (inputVO.getList().size() == 0) {
			StringBuilder sql = new StringBuilder();
			sql.append("WITH A AS ( ");
			sql.append("SELECT ");
			sql.append("  CHK.SEQ, CHK.YYYYMM, CHK.POLICY_NO, CHK.CUST_ID_A, CHK.CUST_ID_B, CHK.CHK_TYPE, CHK.CHECK_SOURCE_CONTENT, ");
			sql.append("  REL.RELATION, REL.OTHER_REL, REL.INSURANCE_NO, ");
			sql.append("  CHK.EMP_CUST_ID, CHK.EMP_NAME, CHK.BRANCH_ID, CHK.MATCH_YN, CHK.CHECKED_RESULT ");
			sql.append("FROM ");
			sql.append("  TBIOT_ADDRTELMAIL_CON_CHK CHK ");
			sql.append("LEFT JOIN ");
			sql.append("  TBCUST_EMP_REL REL ");
			sql.append("ON CHK.CUST_ID_A = REL.CUST_ID AND CHK.CUST_ID_B = REL.EMP_CUST_ID ");
			sql.append("), ");
			sql.append("B AS ( ");
			sql.append("SELECT ");
			sql.append("  YYYYMM, CHK_TYPE, CHECK_SOURCE_CONTENT, COUNT(CHECK_SOURCE_CONTENT) TOTAL ");
			sql.append("FROM ");
			sql.append("  TBIOT_ADDRTELMAIL_CON_CHK ");
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

			if (StringUtils.isNotBlank(inputVO.getSimilarInfo())
					&& !StringUtils.equals("0", inputVO.getSimilarInfo())) {
				sql.append("AND A.CHK_TYPE = :similarInfo ");
				condition.setObject("similarInfo", inputVO.getSimilarInfo());
			}

//			if (StringUtils.isNotBlank(inputVO.getCustID())) {
//				sql.append("AND A.CUST_ID_A = :custID ");
//				condition.setObject("custID", inputVO.getCustID());
//			}
//			
//			if (StringUtils.isNotBlank(inputVO.getEmpCustID())) {
//				sql.append("AND A.CUST_ID_B = :empCustID ");
//				condition.setObject("empCustID", inputVO.getEmpCustID());
//			}

			sql.append("ORDER BY A.CHECK_SOURCE_CONTENT ");

			condition.setQueryString(sql.toString());

			list = checkConShow(dam.exeQuery(condition));
//			list = dam.exeQuery(condition);
			list = remove(list, inputVO.getCustID(), inputVO.getEmpCustID());
			outputVO.setTotalList(list);
		} else {
			// 按分頁，不用重查
			list = inputVO.getList();
			outputVO.setTotalList(inputVO.getList());
		}
		
		int size = list.size();
		int currentPageIdx = inputVO.getCurrentPageIndex();
		int startIdx = currentPageIdx * 10;
		int endIdx = (currentPageIdx * 10) + 10 < size ? (currentPageIdx * 10) + 10 : size;
		outputVO.setTotalRecord(size);
		outputVO.setTotalPage(size / 10 + (size % 10 != 0 ? 1 : 0));
		outputVO.setResultList(list.subList(startIdx, endIdx));
		outputVO.setCurrentPageIndex(currentPageIdx);
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
	
	private List<Map<String, Object>> remove(List<Map<String, Object>> list, String custID, String empCustID) {
		List<Map<String, Object>> returnList = new ArrayList<>();
		if (StringUtils.isNotBlank(custID) && StringUtils.isBlank(empCustID)) {
			for (Map<String, Object> map : list) {
				String custID_A = (String) map.get("CUST_ID_A");
				if (StringUtils.equals(custID_A, custID)) {
					returnList.add(map);
				}
			}
		} else if (StringUtils.isNotBlank(empCustID) && StringUtils.isBlank(custID)) {
			for (Map<String, Object> map : list) {
				String custID_B = (String) map.get("CUST_ID_B");
				if (StringUtils.equals(custID_B, empCustID)) {
					returnList.add(map);
				}
			}
		} else if (StringUtils.isNotBlank(empCustID) && StringUtils.isNotBlank(custID)) {
			for (Map<String, Object> map : list) {
				String custID_A = (String) map.get("CUST_ID_A");
				String custID_B = (String) map.get("CUST_ID_B");
				if (StringUtils.equals(custID_B, empCustID) && StringUtils.equals(custID_A, custID)) {
					returnList.add(map);
				}
			}
		} else {
			returnList = list;
		}
		
		return returnList;
	}

	// 重新比對
	public void reCompare(Object body, IPrimitiveMap header) throws JBranchException, ParseException {
		PMS432InputVO inputVO = (PMS432InputVO) body;
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_SQL);
			
		StringBuffer sql = new StringBuffer();
		sql.append("UPDATE TBIOT_ADDRTELMAIL_CHK CHK ");
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
		PMS432InputVO inputVO = (PMS432InputVO) body;
		dam = this.getDataAccessManager();

		List<Map<String, Object>> updateList = new ArrayList<>();
		List<Map<String, Object>> insertList = new ArrayList<>();
		List<Map<String, Object>> deleteList = new ArrayList<>();

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
				updateList.add(map);
			} else if ("Y".equals(map.get("CHECKED_RESULT")) || (uploadMark && list.size() == 0)) {
				insertList.add(map);
			} else if (list.size() > 0 && "N".equals(map.get("CHECKED_RESULT"))) {
				deleteList.add(map);
			}

			if (updateList.size() > 0) {
				updateRel_Row = this.updateRel(inputVO, updateList,
						dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL));
				updateList.clear();
			}
			
			if (insertList.size() > 0) {
				insertRel_Row = this.insertRel(inputVO, insertList,
						dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL));
				insertList.clear();
			}
			
			if(deleteList.size() > 0) {
				deleteRel_Row = this.deleteRel(inputVO, deleteList,
						dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL));
				deleteList.clear();
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
			this.updateChk(inputVO, dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL));
			this.updateConChk(inputVO, dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL));
		}
		
		sendRtnObject(null);
	}

	private int updateRel(PMS432InputVO inputVO, List<Map<String, Object>> updateList, QueryConditionIF condition)
			throws JBranchException, ParseException {
		int updateRel_Row = 0;
		for (Map<String, Object> map : updateList) {
			StringBuffer sql = new StringBuffer();
			sql.append("UPDATE TBCUST_EMP_REL ");
			if ("9".equals(map.get("RELATION"))) {
				sql.append("SET RELATION = :relation, INSURANCE_NO = :insuranceNo, OTHER_REL = :otherRelation ");
				condition.setObject("otherRelation", map.get("OTHER_REL"));
			} else {
				sql.append("SET RELATION = :relation, INSURANCE_NO = :insuranceNo ");
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
			condition.setQueryString(sql.toString());
			updateRel_Row = dam.exeUpdate(condition);
		}
		return updateRel_Row;
	}

	private int insertRel(PMS432InputVO inputVO, List<Map<String, Object>> insertList, QueryConditionIF condition)
			throws JBranchException, ParseException {
		int insertRel_Row = 0;
		for (Map<String, Object> map : insertList) {
			StringBuffer sql = new StringBuffer();
			sql.append("INSERT INTO TBCUST_EMP_REL ");
			sql.append("(CUST_ID, EMP_CUST_ID, RELATION, INSURANCE_NO");
			if ("9".equals(map.get("RELATION"))) {
				sql.append(", OTHER_REL) ");
				sql.append("VALUES (:custId, :empCustId, :relation, :insuranceNo, :otherRelation) ");
				condition.setObject("otherRelation", map.get("OTHER_REL"));
			} else {
				sql.append(") ");
				sql.append("VALUES (:custId, :empCustId, :relation, :insuranceNo) ");
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
			condition.setQueryString(sql.toString());
			insertRel_Row = dam.exeUpdate(condition);
		}
		return insertRel_Row;
	}
	
	private int deleteRel(PMS432InputVO inputVO, List<Map<String, Object>> deleteList, QueryConditionIF condition)
			throws JBranchException, ParseException {
		int deleteRel_Row = 0;
		for (Map<String, Object> map : deleteList) {
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
		}
		return deleteRel_Row;
	}

	private void updateChk(PMS432InputVO inputVO, QueryConditionIF condition) throws JBranchException, ParseException {
		String loginID = (String) getCommonVariable(SystemVariableConsts.LOGINID);
		for (Map<String, Object> map : inputVO.getList()) {
			StringBuffer sql = new StringBuffer();
			sql.append("UPDATE TBIOT_ADDRTELMAIL_CHK SET ");
			sql.append("MATCH_YN = :matchYN, CHECKED_RESULT = :chkResult, MODIFIER = :modifier, LASTUPDATE = SYSDATE ");
			sql.append("WHERE CUST_ID = :custId AND EMP_CUST_ID = :empCustId ");
			
			condition.setObject("matchYN", map.get("MATCH_YN"));
			condition.setObject("chkResult", map.get("CHECKED_RESULT"));
			condition.setObject("modifier", loginID);
			condition.setObject("custId", map.get("CUST_ID"));
			condition.setObject("empCustId", map.get("EMP_CUST_ID"));
			condition.setQueryString(sql.toString());
			dam.exeUpdate(condition);
		}
	}

	private void updateConChk(PMS432InputVO inputVO, QueryConditionIF condition) throws JBranchException, ParseException {
		String loginID = (String) getCommonVariable(SystemVariableConsts.LOGINID);
		for (Map<String, Object> map : inputVO.getList()) {
			StringBuffer sql = new StringBuffer();
			sql.append("UPDATE TBIOT_ADDRTELMAIL_CON_CHK SET ");
			sql.append("MATCH_YN = :matchYN, CHECKED_RESULT = :chkResult, MODIFIER = :modifier, LASTUPDATE = SYSDATE ");
			sql.append("WHERE CUST_ID_A = :custIdA and CUST_ID_B = :custIdB ");

			condition.setObject("matchYN", map.get("MATCH_YN"));
			condition.setObject("chkResult", map.get("CHECKED_RESULT"));
			condition.setObject("modifier", loginID);
			
			if (!inputVO.getUploadMark()) {
				condition.setObject("custIdA", map.get("CUST_ID_A"));
				condition.setObject("custIdB", map.get("CUST_ID_B"));
			} else {
				condition.setObject("custIdA", map.get("CUST_ID"));
				condition.setObject("custIdB", map.get("EMP_CUST_ID"));
			}
			
			condition.setQueryString(sql.toString());
			dam.exeUpdate(condition);
		}
	}

	// 下載範例檔
	public void downloadSimple(Object body, IPrimitiveMap header) throws Exception {
		this.notifyClientToDownloadFile("doc//PMS//PMS432_EXAMPLE.csv", "查核結果上傳範本.csv");
	}

	// 上傳檔案
	public void upload(Object body, IPrimitiveMap header) throws Exception {
		PMS432InputVO inputVO = (PMS432InputVO) body;
		PMS432OutputVO outputVO = new PMS432OutputVO();
		XmlInfo xmlInfo = new XmlInfo();
		dam = this.getDataAccessManager();

		String tempPath = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
		
		List<String[]> dataCsv = CSVUtil.getBig5CSVFile(tempPath, inputVO.getFileName());	

		List<Map<String, Object>> list = new ArrayList<>();

		if (!dataCsv.isEmpty() && dataCsv.size() > 1) {

			Map<String, String> relationMap = xmlInfo.doGetVariable("PMS.RELATION", FormatHelper.FORMAT_3);

			for (int i = 0; i < dataCsv.size(); i++) {
				Map<String, Object> map = new LinkedHashMap<>();
				String[] row = dataCsv.get(i);
				if (i == 0) {
					try {
						if (!"客戶ID".equals(row[0].trim()))
							throw new Exception(row[0]);
						else if (!"理專ID".equals(row[1].trim()))
							throw new Exception(row[1]);
						else if (!"客戶與理專關係".equals(row[2].trim()))
							throw new Exception(row[2]);
						else if (!"客戶與理專關係-其他".equals(row[3].trim()))
							throw new Exception(row[3]);
						else if (!"證明文件說明(保險文件編號) 限20字".equals(row[4].trim()))
							throw new Exception(row[4]);
					} catch (Exception ex) {
						throw new APException(ex.getMessage() + ":上傳格式錯誤，請下載範例檔案");
					}
					continue;
				}

				// 客戶ID(身分證字號)
				String custId = row[0].trim();
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
				String empCustId = row[1].trim();
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

	// 匯出檔案
	public void export(Object body, IPrimitiveMap header) throws JBranchException, ParseException {

		PMS432InputVO inputVO = (PMS432InputVO) body;

		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String fileName = "保單聯絡資訊與理專疑似相同查核報表_" + sdf.format(new Date()) + ".csv";

		String compareResult = inputVO.getCompareResult();

		String[] csvHeader = null;

		String[] csvMain = null;

		if ("2".equals(compareResult)) {
			// 集中度
			csvHeader = new String[] { "小計筆數", "保單號碼", "客戶ID", "被比對客戶ID", "相似項目", "相同內容", "小計總數", "相同資訊 理專ID/姓名/分行代號",
					"關係戶", "查核結果", "關係說明", "證明文件說明(保險文件編號)" };
			csvMain = new String[] { "TOTAL_ROW_COUNT", "POLICY_NO", "CUST_ID_A", "CUST_ID_B", "CHK_TYPE",
					"CHECK_SOURCE_CONTENT", "TOTAL", "EMP_CUST_ID", "EMP_NAME", "BRANCH_ID", "MATCH_YN",
					"CHECKED_RESULT", "RELATION", "INSURANCE_NO" };
		} else {
			// 相符 不相符
			csvHeader = new String[] { "保單號碼", "客戶ID", "相同內容", "比對檔案來源", "相同資訊 理專ID/姓名/分行代號", "關係戶", "查核結果", "關係說明",
					"證明文件說明(保險文件編號)" };
			csvMain = new String[] { "POLICY_NO", "CUST_ID", "CHECK_SOURCE_CONTENT", "SOURCE_TYPE", "EMP_CUST_ID",
					"EMP_NAME", "BRANCH_ID", "MATCH_YN", "CHECKED_RESULT", "RELATION", "INSURANCE_NO" };
		}

		List<Object[]> csvData = new ArrayList<>();

		for (Map<String, Object> map : inputVO.getList()) {

			String[] records = new String[csvHeader.length];

			for (int i = 0; i < csvMain.length; i++) {
				switch (csvMain[i]) {
				case "TOTAL_ROW_COUNT":
					if ("2".equals(compareResult))
						records[i] = "=\"" + String.valueOf(map.get(csvMain[i])) + "\"";
					break;
				case "POLICY_NO":
					records[i] = "=\"" + String.valueOf(map.get(csvMain[i])) + "\"";
					break;
				case "CUST_ID":
					if (!"2".equals(compareResult))
						records[i] = String.valueOf(map.get(csvMain[i]));
					break;
				case "CUST_ID_A":
					if ("2".equals(compareResult))
						records[i] = String.valueOf(map.get(csvMain[i]));
					break;
				case "CUST_ID_B":
					if ("2".equals(compareResult))
						records[i] = String.valueOf(map.get(csvMain[i]));
					break;
				case "CHK_TYPE":
					if ("2".equals(compareResult))
						records[i] = String.valueOf(map.get(csvMain[i]));
					break;
				case "SOURCE_CONTENT":
					records[i] = String.valueOf(map.get(csvMain[i]));
					break;
				case "CHECK_SOURCE_CONTENT":
					records[i] = String.valueOf(map.get(csvMain[i]));
					break;
				case "SOURCE_TYPE":
					records[i] = String.valueOf(map.get(csvMain[i]));
					break;
				case "TOTAL":
					if ("2".equals(compareResult))
						records[i] = String.valueOf(map.get(csvMain[i]));
					break;
				case "EMP_CUST_ID":
					records[i] = String.valueOf(map.get(csvMain[i]));
					break;
				case "EMP_NAME":
					records[i - 1] = records[i - 1] + "/" + String.valueOf(map.get(csvMain[i]));
					break;
				case "BRANCH_ID":
					records[i - 2] = records[i - 2] + "/" + String.valueOf(map.get(csvMain[i]));
					break;
				case "MATCH_YN":
					records[i - 2] = String.valueOf(map.get(csvMain[i]));
					break;
				case "CHECKED_RESULT":
					records[i - 2] = checkIsNull(map, csvMain[i]);
					break;
				case "RELATION":
					records[i - 2] = checkIsNull(map, csvMain[i]);
					break;
				case "INSURANCE_NO":
					records[i - 2] = checkIsNull(map, csvMain[i]);
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

		this.sendRtnObject(null);
	}

	private String checkIsNull(Map<String, Object> map, String key) {

		if (StringUtils.isNotBlank(ObjectUtils.toString(map.get(key)))) {
			return String.valueOf(map.get(key));
		} else {
			return "";
		}
	}
}
