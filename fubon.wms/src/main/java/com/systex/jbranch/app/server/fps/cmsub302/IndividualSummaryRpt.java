package com.systex.jbranch.app.server.fps.cmsub302;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.birt.data.engine.impl.LogUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.systex.jbranch.app.server.fps.insjlb.vo.DoGetCoverage03OutputVO;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.log.LogHelper;

public class IndividualSummaryRpt
{
	private Map<String, Object> ignoredSortNoMap = null;
	private Map<String, Object> insuranceCompanies = null;
	private Map<String, Object> benefits = null;
	// 客戶保單標題資料
	private List<Map<String, Object>> customerPolicyList = new ArrayList<Map<String, Object>>();
	// 保險給付項目名稱清單
	private Map<String, Map<String, String>> sortNoList = new HashMap<String, Map<String, String>>();
	// 保險給付明細清單
	Map<String, Map<String, Map<String, Object>>> paymentDetail = null;
	// 上一筆的保險公司 + 保單號碼
	private String lastInsuranceCompany = "";
	private String lastPolicyNo = "";
	private String lastCustomerID = "";
	
	private static Logger logger = LoggerFactory.getLogger(IndividualSummaryRpt.class); 
	
	public IndividualSummaryRpt() {
		ignoredSortNoMap = new HashMap<String, Object>();
		this.ignoredSortNoMap.put("B0013", "投資帳戶參考價值(萬)");
	}
	
	public void setInsuranceCompanyMap(Map<String, Object> insuranceCompanyMap) {
		this.insuranceCompanies = insuranceCompanyMap;
	}

	public void setBenefitMap(Map<String, Object> benenfits) {
		this.benefits = benenfits;
	}

	public List<Map<String, Object>> generate(DoGetCoverage03OutputVO outputVO) throws DAOException, JBranchException {	
		// 解析保險給付明細清單
		paymentDetail = parsePaymentDetail(outputVO);
		sortPolicyList(customerPolicyList);
		// Sort insurance payment detail SORT No. list by Sort No.
		List<Map.Entry<String, Map<String, String>>> newSortNoList = arrangeSortNoList(sortNoList);
		List<Map<String, Object>> resultSet = new ArrayList<Map<String, Object>>();
		createResultSet(newSortNoList, resultSet);
		return resultSet;
	}

	private Map<String, Map<String, Map<String, Object>>> parsePaymentDetail(DoGetCoverage03OutputVO outputVO) 
			throws JBranchException {
		Map<String, Map<String, Map<String, Object>>> paymentDetail = new HashMap<String, Map<String, Map<String, Object>>>();
		int expressionCount = ((List<Map<String, Object>>) outputVO.getLstExpression()).size();
		int currentIndex = 0;
		
		for (int i=0; i < expressionCount; i=currentIndex) {
			Map<String, Object> expression = ((List<Map<String, Object>>) outputVO.getLstExpression()).get(i);			
			if (isIgnoredSortNo((String) expression.get("SORTNO"))) {
				currentIndex++;
				continue;
			}			
			// 儲存客戶保單標題資料 (保險公司 + 保單號碼)
			setPolicyInfo(expression);		
			// 取得保險給付明細資料
			Map<String, Map<String, Object>> paymentDetailItems = new HashMap<String, Map<String, Object>>();
			
			for (currentIndex=i; currentIndex < expressionCount; currentIndex++) {			
				Map<String, Object> item = ((List<Map<String, Object>>) outputVO.getLstExpression()).get(currentIndex);			
				if (isANewInsuranceNo((String) item.get("INSCOMPANY"), (String) item.get("POLICYNO")))
					break;
				
				if (isIgnoredSortNo((String) item.get("SORTNO")))
					continue;
					
				String sortNoID = setSortNoList(item);	
				Map<String, Object> detailItem = setPayDetailMap(customerPolicyList.size(), item);
				paymentDetailItems.put(sortNoID, detailItem);					
			}
			
			String mapKey =  lastPolicyNo + "_" + lastInsuranceCompany + "_" + lastCustomerID;
			logger.info("Add paymentDetailItems. mapKey=[" + mapKey + "], has " + 
					paymentDetailItems.size() + " items of payment detail.");
			paymentDetail.put(mapKey, paymentDetailItems);			
		}
		
		return paymentDetail;
	}

	private void setPolicyInfo(Map<String, Object> expression) throws JBranchException {
		Map<String, Object> customerPolicyMap = setCustPolicyMap(customerPolicyList.size(), expression);
		customerPolicyList.add(customerPolicyMap);
		lastCustomerID = expression.get("INSURED_ID").toString();
		lastInsuranceCompany = expression.get("INSCOMPANY").toString();
		lastPolicyNo = expression.get("POLICYNO").toString();
	}

	/**
	 * 儲存客戶保單標題資料
	 */
	private Map<String, Object> setCustPolicyMap(int position, Map<String, Object> expression) throws JBranchException {		
		Map<String, Object> customerPolicyMap = new HashMap<String, Object>();
		customerPolicyMap.put("colPos", position);
		String mapKey = expression.get("POLICYNO") + "_" + expression.get("INSCOMPANY") + "_" + expression.get("INSURED_ID");
		customerPolicyMap.put("MAPKEY", mapKey);
		customerPolicyMap.put("CUSTNAME", expression.get("CUSTNAME"));
		
		logger.info("setCustPolicyMap(): Position=" + position + ", Map Key=" + mapKey);
		
		if (insuranceCompanies != null && insuranceCompanies.get((String) expression.get("INSCOMPANY")) != null)
			customerPolicyMap.put("INSCOMPANY", insuranceCompanies.get((String) expression.get("INSCOMPANY")));
		else
			customerPolicyMap.put("INSCOMPANY", expression.get("INSCOMPANY"));
		
		customerPolicyMap.put("CUSTPOLICY", expression.get("CUSTPOLICY"));
		customerPolicyMap.put("RELATIONCODE", "" + expression.get("RELATIONCODE"));
		customerPolicyMap.put("POLICYNO", expression.get("POLICYNO"));
		customerPolicyMap.put("SIGNDATE", CMSUB302Util.leftAddZDate(expression.get("SIGNDATE")));
		customerPolicyMap.put("INSQUANTITY", CMSUB302Util.getBigDecimal(expression.get("INSQUANTITY")));
		customerPolicyMap.put("PAYTYPEPREMIUM", expression.get("PAYTYPEPEMIUM"));
		customerPolicyMap.put("MAINPROD", expression.get("MAINPROD"));
		customerPolicyMap.put("POLICYSTATUS", expression.get("POLICYSTATUS"));
		customerPolicyMap.put("PROPOSER_NAME", benefits.get(((String) expression.get("INSSEQ")) + "4"));
		customerPolicyMap.put("BENEFITED_NAME", benefits.get((String) expression.get("INSSEQ")));
		customerPolicyMap.put("BENEFITED_NAME2", benefits.get(((String) expression.get("INSSEQ")) + "2"));
		customerPolicyMap.put("BENEFITED_NAME3", benefits.get(((String) expression.get("INSSEQ")) + "3"));
		
		return customerPolicyMap;
	}
	
	private String setSortNoList(Map<String, Object> item) {
		String sortNoID = CMSUB302Util.getString(item.get("SORTNO"));	
		
		if (sortNoList.get(sortNoID) == null) {
			Map<String, String> sortNoItem = new HashMap<String, String>();
			sortNoItem.put("SORT_NO", sortNoID);
			sortNoItem.put("SORTNO_FIRST", sortNoID.substring(0, 1));
			sortNoItem.put("EXPRESSDESC", CMSUB302Util.getString(item.get("EXPRESSDESC")));
			sortNoItem.put("FIRSTKINDDESC", CMSUB302Util.getString(item.get("FIRSTKINDDESC")));
			sortNoList.put(sortNoID, sortNoItem);
			
			logger.info("Add item to sortNoLst. SortNo=" + 
					sortNoID + ", ExpressDesc=" + CMSUB302Util.getString(item.get("EXPRESSDESC")) + 
					", FirstKindDesc=" + CMSUB302Util.getString(item.get("FIRSTKINDDESC")));
		}
		
		return sortNoID;
	}
	
	/**
	 * 是否為可忽略的保險給付項目
	 * 
	 * @param sortNo
	 * @return
	 */
	private boolean isIgnoredSortNo(String sortNo) {
		if (this.ignoredSortNoMap.get(sortNo.trim()) != null)
			return true;
		else
			return false;
	}
	
	/**
	 * 是否為新的一筆客戶保單標題資料
	 * 
	 * @param insuranceCompany
	 * @param policyNo
	 * @return
	 */
	private boolean isANewInsuranceNo(String insuranceCompany, String policyNo) {
		if (!lastInsuranceCompany.equals(insuranceCompany.trim()) || !lastPolicyNo.equals(policyNo.trim())) {
			logger.info("Information of Customer Policy No. is the end. (mapKey = " + 
					lastPolicyNo + "_" + lastInsuranceCompany + "_" + lastCustomerID + ")");
			return true;
		}
		
		return false;
	}
	
	/**
	 * 儲存給付明細資料
	 * @param position
	 * @param expressionItem
	 * @return
	 */
	private Map<String, Object> setPayDetailMap(int position, Map<String, Object> expressionItem) throws JBranchException {
		HashMap<String, Object> payDetail_map = new HashMap<String, Object>();
		payDetail_map.put("colPos", position);
		payDetail_map.put("DESCRIPTION", CMSUB302Util.getString(expressionItem.get("DESCRIPTION")));
		
		String message = "setPayDetailMap(): Position=" + position + ", Sort No=" + 
						CMSUB302Util.getString(expressionItem.get("SORTNO")) + ", EXPRESSDESC=" + 
						CMSUB302Util.getString(expressionItem.get("EXPRESSDESC")) + ", DESCRIPTION=" + 
						CMSUB302Util.getString(expressionItem.get("DESCRIPTION"));
		logger.info(message);
		
		return payDetail_map;
	}
	
	private void sortPolicyList(List<Map<String, Object>> customerPolicyList) {
		// 排序客戶保險資料
		List arrayListHashMap = customerPolicyList;
		MapComparator mapCompare = new MapComparator();
		mapCompare.addCompareKey("RELATIONCODE", MapComparator.VAL_TYPE_STRING, MapComparator.ORDER_TYPE_ASCENDING);
		mapCompare.addCompareKey("CUSTNAME", MapComparator.VAL_TYPE_STRING, MapComparator.ORDER_TYPE_ASCENDING);
		mapCompare.addCompareKey("SIGNDATE", MapComparator.VAL_TYPE_STRING, MapComparator.ORDER_TYPE_ASCENDING);
		mapCompare.addCompareKey("CUSTPOLICY", MapComparator.VAL_TYPE_STRING, MapComparator.ORDER_TYPE_ASCENDING);
		Collections.sort(arrayListHashMap, mapCompare);
		
		List<String> keys = new ArrayList<String>();
		keys.add("colPos");
		keys.add("MAPKEY");
		dumpList(customerPolicyList, "customerPolicyList", keys);
	}

	private void dumpList(List<Map<String, Object>> source, String listName, List<String> keys) {
		// Dump list to log file.
		for (Map<String, Object> item : source) {
			StringBuilder message = new StringBuilder();
			message.append("Dump " + listName + " data");
			for (String keyName : keys)
				message.append(", " + keyName + "=[" + item.get(keyName) + "]");
			
			logger.info( message.toString());
		}	
	}
	
	private List<Map.Entry<String, Map<String, String>>> arrangeSortNoList(Map<String, Map<String, String>> sortNoList) {
		List<Map.Entry<String, Map<String, String>>> newSortNoList = new ArrayList<Map.Entry<String, Map<String, String>>>(sortNoList.entrySet());
	    
		Collections.sort(newSortNoList, new Comparator<Map.Entry<String, Map<String, String>>>() {
            public int compare(Map.Entry<String, Map<String, String>> entry1, Map.Entry<String, Map<String, String>> entry2) {
                return (entry1.getKey().compareTo(entry2.getKey()));
            }
	    });
		// Dump sort No. list
		for (Map.Entry<String, Map<String, String>> item : newSortNoList)
			logger.info("viewSumByTrue(): sortNoLst SORT_NO=" + item.getKey());
		
		return newSortNoList;
	}
	
	private void createResultSet(List<Map.Entry<String, Map<String, String>>> newSortNoList,
			List<Map<String, Object>> resultSet) throws JBranchException {
		int customerCount = customerPolicyList.size(); // 總檢視人數	
		int numOfCustEachPage = 4; // 每頁檢視人數
		int numOfTotalPage = customerCount / numOfCustEachPage + (customerCount % numOfCustEachPage > 0 ? 1 : 0);
		int beginIndex = 0;
		int endIndex = 0;	
		
		for (int pageIndex = 0; pageIndex < numOfTotalPage; pageIndex++) {
			// 取得檢視的起迄(下標)
			beginIndex = pageIndex * numOfCustEachPage;
			
			if (isLastPage(customerCount, numOfCustEachPage, pageIndex))
				endIndex = customerCount;
			else
				endIndex = (pageIndex + 1) * numOfCustEachPage;
			
			for (Map.Entry<String, Map<String, String>> item : newSortNoList)
				resultSet.add(setResultSetItem(pageIndex, beginIndex, endIndex, item.getValue()));
		}
	}

	/**
	 * 判斷是否為最後一頁
	 * 
	 * @param customerCount
	 * @param numOfCustEachPage
	 * @param page
	 * @return
	 */
	private boolean isLastPage(int customerCount, int numOfCustEachPage, int page) {
		return ((page + 1) * numOfCustEachPage) >= customerCount;
	}
	
	/**
	 * 個人化彙總表-組合map
	 * 
	 * @param pageIndex
	 * @param beginIndx
	 * @param endIndex
	 * @param customerPolicyList
	 * @return
	 */
	private Map<String, Object> setResultSetItem(int pageIndex, int beginIndx, int endIndex, Map<String, String> sortNoItem) 
			throws JBranchException {		
		Map<String, Object> resultSetItem = new HashMap<String, Object>();
		resultSetItem.put("PAGE", CMSUB302Util.leftAddZero(pageIndex)); 
		resultSetItem.put("EXPRESSDESC", sortNoItem.get("EXPRESSDESC"));
		resultSetItem.put("FIRSTKINDDESC", sortNoItem.get("FIRSTKINDDESC"));

		if (is1stPrioritySortNo((String) sortNoItem.get("SORTNO_FIRST")))
			resultSetItem.put("SORTNO_FIRST", "");
		else
			resultSetItem.put("SORTNO_FIRST", sortNoItem.get("SORTNO_FIRST"));

		logger.info("Generate Sort No.=" + sortNoItem.get("SORT_NO") + 
				", ExpressDesc=" + sortNoItem.get("EXPRESSDESC"));
		
		int customerIndex = 0;	// 客戶(下標)
		int currentIndex = 1;	// 當頁下標
		for (customerIndex = beginIndx; customerIndex < endIndex; customerIndex++) {
			// 客戶保單標題資料
			resultSetItem.put("colPos_", currentIndex);
			resultSetItem.put("CUSTNAME_" + currentIndex, getPolicyInfo(customerIndex, "CUSTNAME"));
			resultSetItem.put("INSCOMPANY_" + currentIndex, getPolicyInfo(customerIndex, "INSCOMPANY"));
			resultSetItem.put("POLICYNO_" + currentIndex, getPolicyInfo(customerIndex, "POLICYNO"));
			resultSetItem.put("SIGNDATE_" + currentIndex, CMSUB302Util.removeZero(getPolicyInfo(customerIndex, "SIGNDATE")));
			resultSetItem.put("INSQUANTITY_" + currentIndex, getPolicyInfo(customerIndex, "INSQUANTITY"));
			resultSetItem.put("PAYTYPEPREMIUM_" + currentIndex, getPolicyInfo(customerIndex, "PAYTYPEPREMIUM"));
			resultSetItem.put("MAINPROD_" + currentIndex, getPolicyInfo(customerIndex, "MAINPROD"));
			resultSetItem.put("POLICYSTATUS_" + currentIndex, getPolicyInfo(customerIndex, "POLICYSTATUS"));
			resultSetItem.put("BENEFITED_NAME_" + currentIndex, getPolicyInfo(customerIndex, "BENEFITED_NAME"));
			resultSetItem.put("BENEFITED_NAME2_" + currentIndex, getPolicyInfo(customerIndex, "BENEFITED_NAME2"));
			resultSetItem.put("BENEFITED_NAME3_" + currentIndex, getPolicyInfo(customerIndex, "BENEFITED_NAME3"));
			resultSetItem.put("PROPOSER_NAME_" + currentIndex, getPolicyInfo(customerIndex, "PROPOSER_NAME"));	
			// 保險給付明細資料
			String mapKey = customerPolicyList.get(customerIndex).get("MAPKEY").toString();
			Map<String, Map<String, Object>> detailItems = paymentDetail.get(mapKey);	
			if (isEmptyPaymentDetailItem(detailItems, mapKey))		
				continue;
			
			Map<String, Object> item = detailItems.get(sortNoItem.get("SORT_NO"));
			if (item != null) {			
				resultSetItem.put("DESCRIPTION_" + (customerIndex - beginIndx + 1), item.get("DESCRIPTION"));
				logger.info("setResultSetItem(): Generate detail description=" + 
						item.get("DESCRIPTION"));
			}
			
			currentIndex++;
		}
		
		return resultSetItem;
	}

	private boolean is1stPrioritySortNo(String sortNoFirstChar) {
		return "B".equals(sortNoFirstChar);
	}
	
	private Object getPolicyInfo(int index, String key) {
		return customerPolicyList.get(index).get(key);
	}
	
	private boolean isEmptyPaymentDetailItem(Map<String, Map<String, Object>> detailItems, String key) {
		if (detailItems == null || detailItems.isEmpty()) {
			logger.info("Could not find any item. (Map key: " + key + ")");
			return true;
		}
		else {
			logger.info("Generate payment detail Info. (Map key: " + key + ")");
			return false;
		}
	}
}
