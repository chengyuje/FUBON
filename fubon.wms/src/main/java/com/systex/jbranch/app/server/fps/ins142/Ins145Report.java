package com.systex.jbranch.app.server.fps.ins142;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.server.fps.cmsub302.CMSUB302Util;
import com.systex.jbranch.comutil.callBack.CallBackExcute;
import com.systex.jbranch.comutil.collection.CollectionSearchUtils;
import com.systex.jbranch.comutil.collection.GenericMap;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.report.factory.ReportFactory;
import com.systex.jbranch.platform.common.report.generator.ReportGeneratorIF;
import com.systex.jbranch.platform.common.report.reportdata.ReportData;
import com.systex.jbranch.platform.common.report.reportdata.ReportDataIF;

@Component("Ins145Report")
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class Ins145Report extends AbstractInsReport implements Ins145ReportInf{
	private final String RPT_INS145 = "INS145";// 檢視總表(詳實版)
	private final String RPT_R1 = "R1";
	
	/**
	 * 檢視總表(詳實版)
	 * 
	 * @param doOutputVO
	 * @param reportList
	 * @throws DAOException
	 * @throws JBranchException
	 */
	public String printReportINS145(List lstEx , Type type)throws DAOException, JBranchException {
		return printReportINS145(lstEx , null , null , type);
	}
	
	public String printReportINS145(List lstEx , Map<String, Object> insCompanyMap , Map<String, Object> sortNoNameMap , Type type) throws JBranchException {
		//整理後報表所需資料
		List<Map<String, Object>> resultList = null;
		//報表設定
		Map<String, String> cmrpt209R1Map = getBthXML("TBSYS.CMRPT209_R1");
		String [] titles = null;
		
		insCompanyMap = MapUtils.isNotEmpty(insCompanyMap) ? insCompanyMap : doGetInsCompany();
		sortNoNameMap = MapUtils.isNotEmpty(sortNoNameMap) ? sortNoNameMap : doGetSortNoName();

		lstEx = chgLstExpressionToRptData(lstEx , cmrpt209R1Map , insCompanyMap , sortNoNameMap , type);
		
		resultList = genReportIns145Data(doReSort(lstEx) , sortNoNameMap , cmrpt209R1Map);
		
		ReportGeneratorIF generator = ReportFactory.getGenerator();
		ReportDataIF rptData = new ReportData();
	
		rptData.addRecordList("Script Mult Data Set", resultList);
		rptData.addParameter("rptData" , getDate());
		rptData.addParameter("RPT_TYPE" , type.name());
		
		if(Type.COV03.equals(type)){
			rptData.addParameter("RPT_TITLE_NAME" , "報表5_保障項目明細表");
			titles = new String[]{"被保險人" , "保險公司" , "保單號碼" , "投保日期" , "保額" , "繳別/保費" , "險別" , "保單狀態"};
		}
		else if(Type.COV01.equals(type)){
			rptData.addParameter("RPT_TITLE_NAME" , "百科");
			titles = new String[]{"被保險人" , "保險公司" , "商品代碼" , "商品名稱" , "保額" , "繳費年期" , "應繳保費(男)" , "應繳保費(女)"};
		}
		
		
		
		for(int idx = 1 ; idx <= titles.length ; idx++){
			rptData.addParameter("TITLE" + String.valueOf(idx) , titles[idx-1]);
		}
		
		rptData.setMerge(true);
		
		return generator.generateReport(RPT_INS145, RPT_R1, rptData).getLocation();
	}
	
	/**coveage01做初始化lstEx：
	 * 依照 IS_DETAIL(合計為0，非合計為1)、
	 * RELATIONCODE(親屬關係代碼)、
	 * INSURED_ID(被保人id)、
	 * EFFECT_DATE(生效日)來做排序 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<Map<String, Object>> initIns145ExDataCov01(List lstEx) throws JBranchException{
		lstEx = CollectionSearchUtils.cloneMapInList(lstEx);
		//排除B0013
		lstEx = CollectionSearchUtils.selectMapNotInList(lstEx , "SORTNO", "B0013");
		//取得合計
		lstEx = CollectionSearchUtils.selectMapInListByString(lstEx , "IS_DETAIL", "1");
		//排序
//		Collections.sort(lstEx , getComparatorObj(new String[]{
//			"IS_DETAIL" , "RELATIONCODE" , "INSURED_ID" , "EFFECT_DATE"
//		}));
		Collections.sort(lstEx , getComparatorObj(new String[]{
			"IS_DETAIL" , "INSCOMPANY" , "POLICYNO" , "INSURED_ID"
		}));
	
		return lstEx;
	}

	
	public List<Map<String, Object>> initIns145ExDataCov03(List lstEx) throws JBranchException{
		lstEx = CollectionSearchUtils.selectMapNotInList(
			CollectionSearchUtils.cloneMapInList(lstEx) , "SORTNO", "B0013" , new CallBackExcute() {
				public <T> T callBack(GenericMap genericMap) {
					String effectDate = ObjectUtils.toString(genericMap.get("SIGNDATE")).replaceAll("/", "");
					
					if(StringUtils.isBlank(effectDate)){
						genericMap.put("EFFECT_DATE", new BigDecimal(0));
					}
					else{
						genericMap.put("EFFECT_DATE", new BigDecimal(effectDate));
					}
					return null;
				}
			}
		);
		
		Collections.sort(lstEx , getComparatorObj(new String[]{
			"IS_DETAIL" , "INSCOMPANY" , "POLICYNO" , "INSURED_ID"
		}));
			
		return lstEx;
	}
	
	
	public List<Map<String, Object>> chgLstExpressionToRptData (
		List<Map<String, Object>> doSortLstEx , //給附中間檔
		Map<String, String> cmrpt209R1Map ,		//報表設定()
		Map<String, Object> insCompanyMap ,		//保險公司代碼與名稱
		Map<String, Object> sortNoNameMap ,		//給付項目編號與名稱
		Type type
	) throws JBranchException{
		List<Map<String, Object>> payDetailList = new ArrayList<Map<String, Object>>();
		String lastKey = "";
		
		//複製一份並排除給付項目為B0013同時排序
		if(Type.COV03.equals(type)){
			doSortLstEx = initIns145ExDataCov03(doSortLstEx);
		}
		else if(Type.COV01.equals(type)){
			//依照IS_DETAIL(合計為0，非合計為1)、親屬關係代碼、被保人ID、生效日排序
			doSortLstEx = initIns145ExDataCov01(doSortLstEx);
		}
		
		for (Map<String, Object> lstExpression : doSortLstEx) {
			//給附編號
			String sortNo = getStringToZero(ObjectUtils.toString(lstExpression.get("SORTNO")));
			//報表對應位置
			String reportIdx = cmrpt209R1Map.get(sortNo);
			//報表給附項目設定
			String rptConfigForSortNo = getStringToZero(reportIdx);
			//同張保單所要設定的Map資訊
			Map<String, Object> custPolicyMap = null;
			
			//保險公司代碼
			String insCompany = ObjectUtils.toString(lstExpression.get("INSCOMPANY"));
			//保單號碼
			String policyNo = ObjectUtils.toString(lstExpression.get("POLICYNO"));
			//被保人ID
			String insuredId = ObjectUtils.toString(lstExpression.get("INSURED_ID"));
			
			//用來識別同張保單 + 不同被保人 + 保險公司，若KEY相同才會在同一張
			String nowKey =  insuredId + policyNo + insCompany;
			
			//同一間保險公司的同一張保單
			if(lastKey.equals(nowKey)) {
				//取最後一筆位置，並指向該位置的Map實體
				custPolicyMap = payDetailList.get(payDetailList.size() - 1);
			}
			//不是同一張保單的個別資訊
			else 
			{
//				System.out.println(lastKey + "\t:\t" + nowKey);
				custPolicyMap = new HashMap<String, Object>();

				//保險公司名稱 - 如果找不到名稱就直接塞保險公司代碼
				String insCompanyDsc = ObjectUtils.toString(insCompanyMap.get(insCompany));
				insCompanyDsc = StringUtils.isNotBlank(insCompanyDsc) ? insCompanyDsc : insCompany;
				//投保日期
				String signdate = CMSUB302Util.leftAddZDate(lstExpression.get("SIGNDATE"));
				//每期保費
				String payTypePemium = ObjectUtils.toString(lstExpression.get("PAYTYPEPEMIUM"));
				//主約險種代號
				String mainprod = ObjectUtils.toString(lstExpression.get("MAINPROD"));
				//保單狀態
				String policyStatus = ObjectUtils.toString(lstExpression.get("POLICYSTATUS"));
				
				//被保人ID
				custPolicyMap.put("INSURED_ID", insuredId);
				
				//被保險人
				if(Type.COV03.equals(type)){
					custPolicyMap.put("CUSTNAME", lstExpression.get("CUSTNAME"));
					
					
					//COV03 = 保單狀態 , COV01 =　應繳保費(女)
					custPolicyMap.put("POLICYSTATUS", "1".equals(lstExpression.get("IS_DETAIL")) ? "正常件" : "");
				}
				//無被保人
				else if(Type.COV01.equals(type)){
					custPolicyMap.put("CUSTNAME", "");
					custPolicyMap.put("POLICYSTATUS" , policyStatus);
				}
				
				//保險公司
				custPolicyMap.put("INSCOMPANY", insCompanyDsc);
				
				//COV03 = 保單號碼 , COV01 = 商品代碼
				custPolicyMap.put("POLICYNO", policyNo);
				
				//COV03 = 投保日期 , COV01 = 商品名稱
				custPolicyMap.put("SIGNDATE", signdate);
				
				//COV03 = 保額 , COV01 =　保額
				custPolicyMap.put("INSQUANTITY", lstExpression.get("INSQUANTITY"));
				
				//親屬關係代碼
				custPolicyMap.put("RELATIONCODE", lstExpression.get("RELATIONCODE"));
				
				//COV03 = 繳別/保費 , COV01 =　繳費年期
				custPolicyMap.put("PAYTYPEPEMIUM", payTypePemium);
				
				//COV03 = 險別 , COV01 =　應繳保費(男)
				custPolicyMap.put("MAINPROD", mainprod);
						
				payDetailList.add(custPolicyMap);
			}

			//將金額塞到對應的給付編號
			custPolicyMap.put(reportIdx , lstExpression.get("DESCRIPTION"));
			
			//將上一張保單號壓成此保單號，讓下一筆比對
			lastKey = nowKey;
		}
				
		Collections.sort(payDetailList , getComparatorObj(new String[]{
			"IS_DETAIL" , "RELATIONCODE" , "INSURED_ID" , "EFFECT_DATE"
		}));
		
		return payDetailList;
	}
	
	public List<Map<String, Object>> genReportIns145Data(
		List<Map<String, Object>> lstEx , 
		Map<String, Object> sortNoNameMap , 
		Map<String, String> cmrpt209R1Map
	){
		//報表所需要用的資料整理結果
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		//取得報表設定：key為給付編號sorNo , value為對應報表位置設定
		List<Map<String, Object>> cmrpt209List = new ArrayList<Map<String, Object>>();
		//要產出報表集合(紀錄A-H各幾行)
		Map<String , Integer> groupSizeMap = new HashMap<String , Integer>();
		//保單數
		int custLengthNumber = 0;
		int custStartNumber = 0;
		int custEndNumber = 0;
		int groupNum = 0;
		
		//每頁檢視保單數 - 一頁四筆，一筆一個人
		int onePageCustNum = 4;
		
		//總頁數 = 保單總數 / 每頁人數 + ((保單總數 %每頁人數的) > 0 ? 1 : 0)
		int pagecntNumber = 0;
		
		//整理報表設定
		for (String key : cmrpt209R1Map.keySet()) {
			String valueStr = ObjectUtils.toString(cmrpt209R1Map.get(key));
			
			Map<String, Object> rpt209Map = new HashMap<String, Object>();
			rpt209Map.put("key", key);
			rpt209Map.put("value", valueStr);
			cmrpt209List.add(rpt209Map);
			
			if (StringUtils.isNotBlank(valueStr)) {
				Integer size = groupSizeMap.get(valueStr.substring(0 , 1));
				groupSizeMap.put(valueStr.substring(0 , 1) , (size == null ? 0 : size) + 1);
			}
		}

		sort209(cmrpt209List);
		
		//保單數
		custLengthNumber = lstEx.size();
		
		//總頁數 = 保單總數 / 每頁人數 + ((保單總數 %每頁人數的) > 0 ? 1 : 0)
		pagecntNumber = (custLengthNumber / onePageCustNum) + (custLengthNumber % onePageCustNum > 0 ? 1 : 0);
		
//		List<String> tmp = new ArrayList();
		//逐筆跑設定
		for (Map<String, Object> cmrpt209Map : cmrpt209List) {
			String key = ObjectUtils.toString(cmrpt209Map.get("key"));
			String value = ObjectUtils.toString(cmrpt209Map.get("value"));
			
			//沒設定跳下一筆
			if (StringUtils.isBlank(key)) {
				continue;
			}
			
			//逐頁開始設定報表資料
			for (int pageNum = 0; pageNum < pagecntNumber; pageNum++) {
				Map<String, Object> resultMap = new HashMap<String, Object>();
				//當前頁數
				resultMap.put("PAGE", CMSUB302Util.leftAddZero(pageNum));
				
				if ("E01".equals(value.trim())) {
					resultMap.put("PAGE_BREAK", true);
				}
				
				//群組：例如A03，取A
				String groupStr = value.substring(0, 1);
				resultMap.put("GROUP_BY", groupStr);
				
				//群駔號碼：例如A03，取03轉換為3
				groupNum = Integer.valueOf(value.substring(1));
				
				//給付編號名稱
				String groupName = ObjectUtils.toString(sortNoNameMap.get(key));
				Integer groupSize = groupSizeMap.get(groupStr);
				
				//左邊項目名稱
				String tipNameStr = null;
				
				//項目長度
				Integer tipNameSize = null;
				
				//疾病醫療
				if (groupStr.matches("[A,B,H]")) {
					tipNameSize = (tipNameStr = "無大項區分部分").length();
				} 
				else if ("C".equals(groupStr)) {
					tipNameSize = (tipNameStr = "疾病醫療").length();
				} 
				//意外醫療
				else if ("D".equals(groupStr)) {
					tipNameSize = (tipNameStr = "意外醫療").length();
					groupName = groupName.replace("意外", "");
				} 
				//癌症醫療
				else if ("E".equals(groupStr)) {
					tipNameSize = (tipNameStr = "癌症醫療").length();
					groupName = "31204".equals(key) ? groupName : groupName.replace("癌症", "");
				} 
				//實支實付需收據
				else if ("F".equals(groupStr)) {
					tipNameSize = (tipNameStr = "實支實付需收據").length();
					groupName = groupName.replace("實支實付", "");
				} 
				//失能
				else if ("G".equals(groupStr)) {
					tipNameSize = (tipNameStr = "失能").length();
					groupName = groupName.replace("失能", "");
				} 
				
				//如果項目長度大於該欄位長度，同時當下設定的項目號碼 = 該項目總長
				if (tipNameSize > groupSize && groupNum == groupSize) {
					String addStr = "";
					
					for (int i = groupSize; i < tipNameSize; i++) {
						addStr += tipNameStr.substring(i - 1, i) + "\n";
					}
					
					addStr += tipNameStr.substring(tipNameSize - 1, tipNameSize);
					resultMap.put("TIP_NAME", addStr);
				} 
				else if (tipNameSize <= groupSize && groupNum > ((groupSize-tipNameSize) / 2) && groupNum <= (tipNameSize + ((groupSize-tipNameSize) / 2))) {
					resultMap.put("TIP_NAME", tipNameStr.substring((groupNum-((groupSize-tipNameSize) / 2)) - 1, groupNum-((groupSize-tipNameSize) / 2)));
				}
				
				resultMap.put("GROUP_NAME", groupName);
				
				// 取得檢視的起迄(下標)
				custStartNumber = pageNum * onePageCustNum;
				
				//當前頁數 * 每頁檢視保單數 >= 總保單數
				if ((pageNum + 1) * onePageCustNum >= custLengthNumber) {
					custEndNumber = custLengthNumber; //尾頁頁數 = 保單總數
				} 
				else {
					custEndNumber = (pageNum + 1) * onePageCustNum; //尾頁 = 當前頁數 * 每頁檢視保單數
				}
				
				// 按給付項目逐一取得給付金額(僅檢視的起迄)
				// 按區塊排序顯示組合list
				for (int custNum = custStartNumber; custNum < custEndNumber; custNum++) {
					Map<String, Object> custPolicyMap = lstEx.get(custNum);
					String index = String.valueOf(custNum - custStartNumber + 1);
					
					for(String custPolicyMapKey : new String[]{
						"CUSTNAME" 		, 	"INSCOMPANY" 		, "POLICYNO" 		, "PAYTYPEPEMIUM" 	, "MAINPROD" ,
						 "POLICYSTATUS" ,	"PROPOSER_NAME" 	, "BENEFITED_NAME" 	, "BENEFITED_NAME2" , "BENEFITED_NAME3"
					}){
						// 判斷如果存在合計時確認 繳費年其跟狀態(不需要呈現)
						if("合計".equals(resultMap.get("INSCOMPANY"  + index)) && ("PAYTYPEPEMIUM".equals(custPolicyMapKey)||"POLICYSTATUS".equals(custPolicyMapKey))) {
							resultMap.put(custPolicyMapKey + index , "");
						} else {
							resultMap.put(custPolicyMapKey + index , custPolicyMap.get(custPolicyMapKey));								
						}
					}
					
					if(!ObjectUtils.toString(custPolicyMap.get("SIGNDATE")).matches("0+$")){
						resultMap.put("SIGNDATE" + index , CMSUB302Util.removeZero(custPolicyMap.get("SIGNDATE")));
					}
					else{
						resultMap.put("SIGNDATE" + index , "");	
					}
					
					if(!"".equals(ObjectUtils.toString(custPolicyMap.get("INSQUANTITY")))){
						resultMap.put("INSQUANTITY" + index , custPolicyMap.get("INSQUANTITY"));	
					}
					else{
						resultMap.put("INSQUANTITY" + index , "");
					}
					
					
//					System.out.println(key + " , " + value + " , " + "GROUP0" + index + " : " + custPolicyMap.get(value));
					resultMap.put("GROUP0" + index, custPolicyMap.get(value));
				}
				
				resultList.add(resultMap);
			}
		}

		return resultList;
	}
	
	/**
	 * Carey 20120824 根據客戶跟投保時間排序
	 * 
	 * @param list
	 */
	private void sort209(List<Map<String, Object>> list) {
		Collections.sort(list, new Comparator<Map<String, Object>>() {
			public int compare(Map<String, Object> o1, Map<String, Object> o2) {
				String o1Val = ObjectUtils.toString(o1.get("value"));
				String o2Val = ObjectUtils.toString(o2.get("value"));
				return o1Val.compareTo(o2Val) > 0 ? 1 : -1;
			}
		});
	}

	
	public Comparator getComparatorObj(final String [] keys){
		return new Comparator<Map<String, Object>>() {
			public int compare(Map<String, Object> o1, Map<String, Object> o2) {
				int result = 0;
				
				for(String key : keys){
					result = ObjectUtils.toString(o1.get(key)).compareTo(ObjectUtils.toString(o2.get(key)));
					
					if(result != 0){
						return result;
					} 
				}
				
				return result;
			}
		};
	}
	
	private List<Map<String, Object>> doReSort (List<Map<String, Object>> lstExt) throws DAOException, JBranchException {
		List<Map<String, Object>> reSortCollectList = new ArrayList<Map<String, Object>>();
		Map<String, List<Map<String, Object>>> reSortCollectMap = new HashMap<String, List<Map<String, Object>>>();
		List<String> sortOrderList = new ArrayList<String>();
		
		for(Map<String, Object> map : lstExt) {
			if("合計".equals(map.get("INSCOMPANY"))) {
				reCombinSortMap(reSortCollectMap, map, "合計");
				sortOrderList.add(ObjectUtils.toString(map.get("INSURED_ID")));
			} else {
				reCombinSortMap(reSortCollectMap, map, ObjectUtils.toString(map.get("INSURED_ID")));
			}
		}

		if(reSortCollectMap.get("合計") == null) {
			for(Entry entry : reSortCollectMap.entrySet()) {
				reSortCollectList.addAll(reSortCollectMap.get(entry.getKey()));
			}
		} else {
			reSortCollectList.addAll(reSortCollectMap.get("合計"));
		}
		
		reSortOrder(sortOrderList, reSortCollectMap, reSortCollectList);
		return reSortCollectList;
	}
	
	// 分組收集
	private void reCombinSortMap(Map<String, List<Map<String, Object>>> reSortCollectMap, Map<String, Object> map, String keyName) {
		if(reSortCollectMap.containsKey(keyName)) {
			reSortCollectMap.get(keyName).add(map);
		} else {
			List<Map<String, Object>> newList = new ArrayList<Map<String, Object>>();
			newList.add(map);
			reSortCollectMap.put(keyName, newList);
		}
	}
	
	// 重新分組排序
	private void reSortOrder(List<String> sortOrderList, Map<String, List<Map<String, Object>>> reSortCollectMap, List<Map<String, Object>> reSortCollectList) throws DAOException, JBranchException {
		Map<String, String> custMap = getCustListInfo(sortOrderList);
		for(String insuredId : sortOrderList) {
			List<Map<String, Object>> singleInsuredList = reSortCollectMap.get(insuredId);
			
			for(Map<String, Object> singleInsuredMap : singleInsuredList) {
				if(StringUtils.isEmpty(ObjectUtils.toString(singleInsuredMap.get("CUSTNAME")))){
					singleInsuredMap.put("CUSTNAME", custMap.get(insuredId));
				}
			}
			
			Collections.sort(singleInsuredList , getComparatorObj(new String[]{
				"IS_DETAIL" , "RELATIONCODE" , "INSURED_ID" , "SIGNDATE"
			}));
			reSortCollectList.addAll(reSortCollectMap.get(insuredId));
		}
	}
	
	// 避免有附加情況找不到人名
	private Map<String, String> getCustListInfo(List<String> insueredId) throws DAOException, JBranchException {
		Map<String, String> result = new HashMap<String, String>();
		if(CollectionUtils.isEmpty(insueredId)) {
			return result;
		}
		DataAccessManager dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuilder sql = new StringBuilder();
		sql.append("select cust_id, cust_name from TBCRM_CUST_MAST where cust_id in (:insuredIdList)");
		queryCondition.setObject("insuredIdList", insueredId);
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> resultList = dam.exeQuery(queryCondition);
		if(CollectionUtils.isNotEmpty(resultList)) {
			for(Map<String, Object> map : resultList) {
				result.put(ObjectUtils.toString(map.get("CUST_ID")), ObjectUtils.toString(map.get("CUST_NAME")));
			}
		}
		return result;
	}

}
