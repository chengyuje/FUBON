package com.systex.jbranch.app.server.fps.ins.parse;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.WordUtils;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.ibm.icu.text.SimpleDateFormat;
import com.systex.jbranch.comutil.collection.CollectionSearchUtils;
import com.systex.jbranch.comutil.collection.GenericMap;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.XmlInfo;

import java.util.Arrays;

/**
 * WS 調用回傳統一 Mapping
 * 請單純這個 Utils 只做 來源資料與回傳資料的 Mapping
 * 
 * @author 1800020 sen
 *
 */
public class WSMappingParserUtils {

	//=============================== 共用 Method，只要符合方法定義的都可自取 [使用前請詳閱公開註解]=============================== 
	//=========================== 統一 Mapping 轉換 ===========================
	/**
	 * 多筆資料 Mapping List<Map> to List<Map>
	 * @param fromList 來源資料
	 * @param processColumns 處理欄位
	 * @param isMappingByColumn 欄位處理 或是 entryKey 處理
	 * @return 處理過的 List<Map>
	 */
	public static List<Map<String, Object>> multiDataMapping(List<Map<String, Object>> fromList, String[] processColumns, boolean isMappingByColumn){
		List<Map<String, Object>> toList = new ArrayList<Map<String, Object>>();
		for(Map<String, Object> fromMap : fromList) {
			Map<String, Object> toMap = new HashMap<String, Object>();
			toMap = isMappingByColumn ? dataMappingByColumn(fromMap, processColumns) : dataMappingByEntryKey(fromMap, processColumns);
			toList.add(toMap);
		}
		return toList;
	}
	
	/**
	 * 單筆資料 Mapping Map to Map
	 * @param fromMap 來源資料
	 * @param processColumns 處理欄位
	 * @param isMappingByColumn 欄位處理 或是 entryKey 處理
	 * @return 處理過的 Map
	 */
	public static Map<String, Object> singleDataMapping(Map<String, Object> fromMap, String[] processColumns, boolean isMappingByColumn){
		return isMappingByColumn ? dataMappingByColumn(fromMap, processColumns) : dataMappingByEntryKey(fromMap, processColumns);
	}
	
	/**
	 * Mapping 單筆資料 Map to Map
	 * @param fromMap 來源資料
	 * @param toMap 結果資料
	 * @param processColumns 處理的欄位
	 */
	public static Map<String, Object> dataMappingByColumn(Map<String, Object> fromMap, String[] processColumns){
		// 根據 processColumns 找
		Map<String, Object> toMap = new HashMap<String, Object>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		for(String column : processColumns) {
			Object value = fromMap.get(column);
			if( value != null && value instanceof Date) {
				value = sdf.format(value);
			} 
			toMap.put(toLowerCamelCase(column), value);
		}
		return toMap;
	}
	
	/**
	 * Mapping 單筆資料 Map to Map
	 * @param fromMap 來源資料
	 * @param toMap 結果資料
	 * @param processColumns 不處理的欄位
	 */
	public static Map<String, Object> dataMappingByEntryKey(Map<String, Object> fromMap, String[] processColumns) {
		// 全 Mapping 除了 processColumns 的
		processColumns = processColumns!= null ? processColumns: new String[]{};
		Map<String, Object> toMap = new HashMap<String, Object>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		for(Entry<String, Object> entry : fromMap.entrySet()) {
			if(!Arrays.asList(processColumns).contains(entry.getKey())){
				Object value = entry.getValue();
				if( value != null && value instanceof Date) {
					value = sdf.format(value);
				} 
				toMap.put(toLowerCamelCase(entry.getKey()), value);
			}
		}
		return toMap;
	}
	
	//=========================== 小功具 - 轉換用 ===========================
	/**
	 * 小駝峰式命名法( lower camel case )
	 * 根據底線排除小駝峰命名
	 * @param column
	 * @return
	 */
	public static String toLowerCamelCase(String column){
		String parserString = WordUtils.capitalize(column.toLowerCase(), "_".toCharArray()).replaceAll("_", "");
		parserString = parserString.substring(0, 1).toLowerCase() + parserString.substring(1);
		return parserString;
	}
	
	/**
	 * 物件轉Map 格式限定，物件定義裡的 property 不能是 java 以外的 class 
	 * ex: Ins9999InputVO 裡面有個 property 叫做 Ins888InputVO (X)
	 * ex: Ins9999InputVO 裡面有個 property String (O)
	 * @param object
	 * @return
	 * @throws IntrospectionException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	public static Map<String, Object> objectToMap(Object object) throws IntrospectionException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Map<String, Object> result = new HashMap<String, Object>();
	    BeanInfo info = Introspector.getBeanInfo(object.getClass());
	    for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
	        Method reader = pd.getReadMethod();
	        if (reader != null)
	            result.put(pd.getName(), reader.invoke(object));
	    }
	    return result;
	}
	
	/**
	 * 將 ws 的 map 轉成對應的pojo
	 * @param fromMap、cls
	 * @return
	 */
	public static <T> T mapToPojo (Map<String, Object> fromMap, Class cls){		
        Gson gson = new Gson();
		JsonElement jsonElement = gson.toJsonTree(fromMap);
		return (T) gson.fromJson(jsonElement, cls);		
	}
	
	public static Map<String, String> getContractStatus(String formatHelper) throws JBranchException{
		XmlInfo xmlInfo = new XmlInfo();
		return xmlInfo.doGetVariable("CRM.CRM239_CONTRACT_STATUS", formatHelper);
	}
	
	/**
	 * Map中所有key的值預設為空字串(with lower camel case)
	 * @param resultMap、keySets
	 * @return resultMap
	 */
	public static Map<String, Object> allKeysTheSameDefaultValueWithLowerCamel(String[] keySets, Object param){
		Map<String,Object> resultMap = new HashMap<String, Object>();
		for(String key : keySets){
			resultMap.put(toLowerCamelCase(key), param);
		}
		return resultMap;		
	}
	
	/**
	 * Map中所有key的值預設為空字串(with lower camel case)
	 * @param resultMap、keySets
	 * @return resultMap
	 */
	public static Map<String, Object> allKeysTheSameDefaultValue(String[] keySets, Object param){
		Map<String,Object> resultMap = new HashMap<String, Object>();
		for(String key : keySets){
			resultMap.put(key, param);
		}
		return resultMap;		
	}
	//===================== INS 特殊功能轉換用 Method，只要符合方法定義的也可自取 [使用前請詳閱公開註解] =====================
	//=========================== getAllPolicy 組資料 ===========================
	/**
	 * 1.4.2 保單資料
	 * 組合成ws要的資料格式 & 計算 年繳化應繳保費
	 * @param fromList
	 * @param toList
	 * @param sumInsYearFee
	 * @throws JBranchException
	 */
	public static BigDecimal wsAllPolicyMapping(List<Map<String, Object>> fromList, List<Map<String, Object>> toList) throws JBranchException {
		Map<String, String> statusXML = getContractStatus(FormatHelper.FORMAT_3);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		BigDecimal sumInsYearFee = BigDecimal.ZERO;
		String[] columns = new String[]{"POLICY_NBR", "INSURED_NAME", "INSURED_BIRTHDAY", "COM_ID", "PRD_ID", "PRD_NAME", "IS_MAIN", 
				"INSUREDAMT", "UPQTY_SEL", "CURR_CD", "INSYEARFEE", "LOCAL_INSYEARFEE", "EFFECTED_DATE", "STATUS", "INOUT", "PAYTYPE", "COVERCACULUNITDESC"};
		for(Map<String, Object> fromMap : fromList) {
			if(fromMap.get("PAY_TYPE")!=null){
				fromMap.put("PAYTYPE", fromMap.get("PAY_TYPE"));
			}
			Map<String, Object> toMap = singleDataMapping(fromMap, columns, true);
			
			toMap.put("policySerial", fromMap.get("KEYNO"));						// policySerial:行外保單序號
			toMap.put("insSeq", fromMap.get("INSSEQ"));								// insSeq : 保單自訂序號
			
			// PAYMENTYEAR_SEL 要字串
			toMap.put("paymentyearSelName", ObjectUtils.toString(fromMap.get("PAYMENTYEAR_SEL_NAME"), null));
			
			// 狀態要中文
			toMap.put("statusDesc", ObjectUtils.toString(statusXML.get(fromMap.get("STATUS")), null));
			
			//insuredId :　被保險人ID
			if("1".equals( ObjectUtils.toString(fromMap.get("INOUT")))){
				toMap.put("insuredId", fromMap.get("INSURED_ID"));
			}else{
				if("Y".equals(fromMap.get("IS_MAIN").toString())){
					toMap.put("insuredId", fromMap.get("INSURED_ID"));
				}else if("N".equals(fromMap.get("IS_MAIN").toString())){
					toMap.put("insuredId", fromMap.get("CUST_ID"));
				}							
			}
			if ("Y".equals(ObjectUtils.toString(fromMap.get("BENEFICIARY_YN")))) {
				toMap.put("beneficiaryYN", fromMap.get("是"));
			} else {
				toMap.put("beneficiaryYN", fromMap.get("否"));
			}
			
			toList.add(toMap);
			
			sumInsYearFee = sumInsYearFee.add(new GenericMap(fromMap).getBigDecimal("INSYEARFEE_YEAR"));
		}
		return sumInsYearFee;
	}
	
	/**
	 * 1.4.3 保單詳細資料
	 * 組合成ws要的資料格式 
	 * @param mainYMap 主約資料 單筆
	 * @param mainNList 附約資料 多筆
	 * @throws JBranchException
	 */
	public static Map<String, Object> wsGetPolicyContentMapping(Map<String, Object> mainYMap, List<Map<String, Object>> mainNList) throws JBranchException {
		Map<String, Object> outputMap = new HashMap<String, Object>();
		
		String[] columns = new String[]{"POLICY_NBR", "INSURED_ID", "INSURED_NAME", "INSURED_BIRTHDAY", "COM_ID", "PRD_KEYNO", "PRD_ID", "PRD_NAME", "EFFECTED_DATE"};
		
		// "UPQTY_SEL", "INSUREDAMT" 不用要特殊處理
		String[] items = new String[]{"CURR_CD", "PAYTYPE", "INSURED_OBJECT", "COVERYEAR_SEL", "KIND_SEL", "UPTYPE"}; 

		// 處理主約資料
		Map<String, Object> mainContractMap = WSMappingParserUtils.singleDataMapping(mainYMap, columns, true);	
		mainContractMap.put("policySerial", mainYMap.get("KEYNO"));
		mainContractMap.put("insSeq", mainYMap.get("INSSEQ"));
		mainContractMap.put("insyearfee", new BigDecimal(mainYMap.get("INSYEARFEE").toString().replaceAll(",", "")));
		mainContractMap.put("item", WSMappingParserUtils.mapDataToItemList(items, mainYMap, true));
		String beneficiaryYN = mainYMap.get("BENEFICIARY_YN") != null ? ("Y".equals(mainYMap.get("BENEFICIARY_YN").toString()) ? "是" : "否") : "";
		mainContractMap.put("beneficiaryYN", beneficiaryYN);
			
		// 處理附約資料
		List<Map<String, Object>> finalMainNList = new ArrayList<Map<String,Object>>();
			
		for(Map<String,Object> mainNMap : mainNList){
			Map<String, Object> attachmentMap = WSMappingParserUtils.singleDataMapping(mainNMap, columns, true);	
			attachmentMap.put("policySerial", mainNMap.get("KEYNO"));
			attachmentMap.put("insSeq", mainNMap.get("INSSEQ"));
			attachmentMap.put("insyearfee", new BigDecimal(mainNMap.get("INSYEARFEE").toString().replaceAll(",", "")));
			attachmentMap.put("item", WSMappingParserUtils.mapDataToItemList(items, mainNMap, true));
			finalMainNList.add(attachmentMap);
		}
			
		outputMap.put("mainContract", mainContractMap);
		outputMap.put("attachment", finalMainNList);
		return outputMap;
	}
	
	
	/**
	 * 1.4.5 目前保障商品
	 * 組合成ws要的資料格式
	 * 格式轉換目前保障商品 (就是你的既有保單拉)
	 * @param fromData
	 * @return
	 */
	public static List<Map<String, Object>> getCurrentPolicyList (List<Map<String, Object>> fromData, int type){
		if(CollectionUtils.isEmpty(fromData)) {
			return null;
		}
		
		List<Map<String, Object>> currentPolicyList = new ArrayList<Map<String, Object>>();
		for(Map<String, Object> fromMap :fromData) {
			Map<String, Object> currentMap = new HashMap<String, Object>();
			currentMap.put("insseq", fromMap.get("INSSEQ"));
			currentMap.put("insco", fromMap.get("INSCO"));
			currentMap.put("prdId", fromMap.get(""));
			currentMap.put("prdName", fromMap.get("PRD_NAME"));
			currentMap.put("currCd", fromMap.get("CURR_CD"));
			currentMap.put("insuredamt", fromMap.get("INSUREDAMT")); // 保額
			currentMap.put("unit", fromMap.get("unit")); // 單位
			
			if(type == 4) {
				// 一次給付金D	住院日額C	長期看護每月給付W
				// 底下為重大疾病有的
				currentMap.put("coverageD", fromMap.get("coverage_D")); // 一次給付金額
				currentMap.put("coverageC", fromMap.get("coverage_C")); // 住院日額
				currentMap.put("coverageW", fromMap.get("coverage_W")); // 長期看護每月給付
			} else {
				currentMap.put("coverage", fromMap.get("coverage")); // 保障
				currentMap.put("insyearfee", fromMap.get("INSYEARFEE")); // 保費
				currentMap.put("localInsyearfee", fromMap.get("LOCAL_INSYEARFEE")); // 保費(台幣)
			}

			currentPolicyList.add(currentMap);
		}
		return currentPolicyList;
	}
	
	//=========================== getProductOption 下拉組資料 ===========================
	/**
	 * 格式化 prod入口 下拉相關的
	 * @param fromMap
	 * @param toList
	 * @throws JBranchException 
	 */
	public static void dropProdInfoMapping(Map<String, Object> fromMap, Map<String, Object> toMap) throws JBranchException {
		List<Map<String, Object>> toList = new ArrayList<Map<String, Object>>();
		WSMappingParserUtils.dropProdItemMapping(fromMap, toList);
		
		String isWL = ObjectUtils.toString(fromMap.get("IS_WL"));
		Integer wlTerm = "N".equals(isWL) ? -1 : Integer.parseInt(ObjectUtils.toString(fromMap.get("WL_TERM")).replaceAll("@", ""));
		
		toMap.put("prdId", fromMap.get("PRD_ID"));
		toMap.put("prdName", fromMap.get("PRD_NAME"));
		toMap.put("item", toList);
		toMap.put("isWL", wlTerm);
		toMap.put("covercaculunitdesc", ObjectUtils.toString(fromMap.get("COVERCACULUNITDESC")));
	}
	
	/**
	 * 格式化 prod 下拉相關的
	 * @param fromMap
	 * @param toList
	 * @throws JBranchException 
	 */
	@SuppressWarnings("unchecked")
	public static void dropProdItemMapping(Map<String, Object> fromMap, List<Map<String, Object>> toList) throws JBranchException {
		String[] findKey = new String[]{"E", "P", "Y", "A", "O", "K"};
		String[] findCode = new String[]{"currCd", "assureAmt", "paymentyearSel", "coveryearSel", "insuredObject", "kindSel"};
		int i = 0;
		for(String key:findKey) {
			if("P".equals(key)) {
				// 決定用 P 還是 U
				String itemP = ObjectUtils.toString(fromMap.get("ITEM_" + "P"));
				String itemU = ObjectUtils.toString(fromMap.get("ITEM_" + "U"));
				
				String useKey = itemP.isEmpty() ? (itemU.isEmpty() ? "" : "U") : "P";
				
				// 如果 U & P 都沒有
				if(useKey.isEmpty()) {
					toList.add(doDropProdInfoMapping(findCode[i], "保額", "", ""));	
					toList.add(doDropProdInfoMapping("uptype", "保額類型", "", ""));
				} 
				
				// 根據取得的 U 或 P 進行處理， U必須幫她拆 5-30,40,50 >> 5-30 要拆
				else {
					StringBuffer reCombinItemInsuedamt = new StringBuffer();
					String itemInsuedamt = ObjectUtils.toString(fromMap.get("ITEM_" + useKey));
					String titleInsuedamt = ObjectUtils.toString(fromMap.get("TITLE_" + useKey));
					
					if("U".equals(useKey)) {
						String[] firstParser = itemInsuedamt.split(",");
						for(String firstValue:firstParser) {
							String[] secondParser = firstValue.split("-");
							if(secondParser.length == 1) {
								reCombinItemInsuedamt.append((reCombinItemInsuedamt.length() == 0 ? "" : ",") + secondParser[0]);
							} else {
								for(int value = Integer.parseInt(secondParser[0]); value<=Integer.parseInt(secondParser[1]); value++)
									reCombinItemInsuedamt.append((reCombinItemInsuedamt.length() == 0 ? "" : ",") + value);
							}
						}
					} 
					itemInsuedamt = reCombinItemInsuedamt.length() == 0 ? itemInsuedamt : reCombinItemInsuedamt.toString();
					String listInsuedamt = itemInsuedamt;
					toList.add(doDropProdInfoMapping(findCode[i], titleInsuedamt, itemInsuedamt, listInsuedamt));
					toList.add(doDropProdInfoMapping("uptype", "保額類型", "U".equals(useKey) ? "01" : "02", ""));
				}
			} else {
				toList.add(doDropProdInfoMapping(findCode[i], ObjectUtils.toString(fromMap.get("TITLE_" + key)), ObjectUtils.toString(fromMap.get("ITEM_" + key)), ObjectUtils.toString(fromMap.get("LIST_" + key))));
			}
			i += 1;
		}
		
		// 繳別另外做，取得 XML
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> payTypeMap = xmlInfo.doGetVariable("INS.PAY_TYPE", FormatHelper.FORMAT_3);
		StringBuffer optionCode = new StringBuffer();
		StringBuffer optionName = new StringBuffer();
		
		boolean isFirst = true;
		for(Entry<String, String> entry : payTypeMap.entrySet()) {
			if(isFirst) {
				optionCode.append(entry.getKey());
				optionName.append(entry.getValue());
			} else {
				optionCode.append("," + entry.getKey());
				optionName.append("," + entry.getValue());
			}
			isFirst = false;
		}
		
		toList.add(doDropProdInfoMapping("paytype", "繳別", optionCode.toString(), optionName.toString()));
	}
	
	/**
	 * 實際組合產品下拉的資料
	 * @param itemCode
	 * @param itemName
	 * @param optionCode
	 * @param optionName
	 * @return
	 */
	private static Map<String, Object> doDropProdInfoMapping(String itemCode, String itemName, String optionCode, String optionName) {
		Map<String, Object> toMap = new HashMap<String, Object>();
		toMap.put("itemCode", itemCode);
		toMap.put("itemName", itemName);
		Map<String, Object> optionMap = new HashMap<String, Object>();
		optionMap.put("optionCode", optionCode);
		optionMap.put("optionName", optionName);
		toMap.put("option", optionMap);
		return toMap;
	}

	//=========================== getPolicyContent、updatePolicy item 組資料 ===========================
	/**
	 * 把 map 資料中的 item 資訊轉換成ws要的 (for 保險詳細資料的 item)
	 * @param keyArray
	 * @param fromData
	 * @return
	 */
	public static List<Map<String, Object>> mapDataToItemList(String[] keyArray, Map<String, Object> fromData, boolean keyParser) {
		List<Map<String, Object>> itemList = new ArrayList<Map<String, Object>>();
		String [] specialCase = new String[]{"UPQTY_SEL", "INSUREDAMT", "PAYMENTYEAR_SEL"};
		for(String key : keyArray) {			
			String parserKey = "";
			if(keyParser) {
				parserKey = toLowerCamelCase(key);
			}
			itemList.add(mapDataToItemMap(keyParser ? parserKey : key, fromData.get(key)));
		}
		
		// PAYMENTYEAR_SEL 要字串
		itemList.add(mapDataToItemMap("paymentyearSel", ObjectUtils.toString(fromData.get("PAYMENTYEAR_SEL"), null)));
		
		String upqtySel = ObjectUtils.toString(fromData.get(specialCase[0]));
		String insuredamt = ObjectUtils.toString(fromData.get(specialCase[1]));
		String value = upqtySel.isEmpty() ? insuredamt : upqtySel;
		itemList.add(mapDataToItemMap("assureAmt", value));
		return itemList;
	}
	
	/**
	 * 單純 put item key value
	 * @param key
	 * @param value
	 * @return
	 */
	public static Map<String, Object> mapDataToItemMap(String key, Object value) {
		Map<String, Object> itemMap = new HashMap<String, Object>();
		itemMap.put("itemCode", key);
		itemMap.put("optionCode", value);
		return itemMap;
	}

	/**
	 * 將 ws 的 list item 轉成比較好拿的 map
	 * @param itemList
	 * @return
	 */
	public static Map<String, Object> itemListToMap(List<Map<String, Object>> itemList) {
		Map<String, Object> finalItemMap = new HashMap<String, Object>();
		for(Map<String, Object> itemMap : itemList) {
			String itemCode = ObjectUtils.toString(itemMap.get("itemCode"));
			if(!itemCode.isEmpty()) {
				finalItemMap.put(itemCode, itemMap.get("optionCode"));
			}
		}
		return finalItemMap;
	}
	
	/**
	 * 不同繳費年期會有不一樣的商品 & 上限下限
	 * @param currentMap
	 * @param insprdAnnualList
	 */
	public static void suggestPaymentSelMapping(Map<String, Object> currentMap, List<Map<String, Object>> insprdAnnualList) {
		String[] keyNo = String.valueOf(currentMap.get("KEY_NO")).split(",");
		String[] insprdAnnual = String.valueOf(currentMap.get("INSPRD_ANNUAL")).split(",");
		for(int i = 0 ; i<insprdAnnual.length ; i++) {
			Map<String, Object> insprdAnnualMap = new HashMap<String, Object>();
			insprdAnnualMap.put("paymentyearSel", insprdAnnual[i]);
			insprdAnnualMap.put("prdKeyNo", keyNo[i]); // 保險主檔主鍵
			insprdAnnualList.add(insprdAnnualMap);
		}
	}
	
	//=========================== getInsuranceTiedSaleRefData 組資料 ===========================
	//2.9.1 商品資料格式處理
	public static void wsGetInsuranceTiedSaleRefDataMapping(List<Map<String, Object>> fromList, List<Map<String, Object>> toList) throws JBranchException {
		for(Map<String, Object> fromMap : fromList){
			Map<String, Object> toMap = new HashMap<String, Object>();
			toMap.put("prdId", fromMap.get("PROD_ID"));  // 險種代碼
			toMap.put("prdName", fromMap.get("PROD_NAME"));  // 險種名稱
			toMap.put("paymentyearSel", fromMap.get("PREMTERM_NAME"));  // 繳費年期
			toMap.put("curPolicyYear", fromMap.get("YEAR"));  // 保險年度
			toMap.put("insyearfee", fromMap.get("PREMIUM"));  // 保費
			toMap.put("insuredamt", fromMap.get("QUANTITY"));  // 保額
			toMap.put("unit", fromMap.get("COVERUNIT"));  // 單位
			toMap.put("aliveFee", fromMap.get("REPAY"));  // 當年度生存金 
			
			toList.add(toMap);
		}
	}
	
	//2.9.3 商品資料格式處理
	public static void dataProcess(List<Map<String, Object>> fromList, List<Map<String, Object>> toList, List<Map<String, Object>> dataList, int index) throws JBranchException {		
		for(Map<String, Object> dataMap : dataList){
			Map<String, Object> fromMap = CollectionSearchUtils.findMapInColleciton(fromList, "PRD_ID", dataMap.get("prdId"));
			int position = WSMappingParserUtils.findIndexInStringWithComma(ObjectUtils.toString(fromMap.get("KEY_NO")), ObjectUtils.toString(dataMap.get("prdKeyNo")));
			fromMap.put("insPrdAnnualModel"+index, dataMap.get("paymentyearSel"));
			fromMap.put("policyFee"+index, dataMap.get("insyearfee"));
			fromMap.put("assureAmt"+index, dataMap.get("insuredamt"));
			fromMap.put("prdUnit"+index, fromMap.get("PRD_UNIT"));
			fromMap.put("prodKeyno"+index, WSMappingParserUtils.findValueInStringWithComma(ObjectUtils.toString(fromMap.get("INSDATA_KEYNO")), position));
			if(index == 3){
				fromMap.put("earnedYear", WSMappingParserUtils.findValueInStringWithComma(ObjectUtils.toString(fromMap.get("EARNED_YEAR")), position));
				fromMap.put("earnedRatio", WSMappingParserUtils.findValueInStringWithComma(ObjectUtils.toString(fromMap.get("EARNED_RATIO")), position));
				fromMap.put("earnedCalWay", WSMappingParserUtils.findValueInStringWithComma(ObjectUtils.toString(fromMap.get("EARNED_CAL_WAY")), position));		
			}
			
			toList.add(fromMap);			
		}
	}
	
	/**
	 * 找出含有逗號的字串，切開後的字串陣列裡與key值相同的index
	 * @param key
	 * @param target
	 * @return index
	 */
	public static int findIndexInStringWithComma(String target, String key) throws JBranchException{	
		int index=0;		
		String[] str = target.split(",");		
		for(int i=0;i<str.length;i++){
			if(key.equals(str[i])){
				index=i;
			}
		}		
		return index;	
	}
	
	/**
	 * 找出含有逗號的字串，切開後的字串陣列裡特定index的值
	 * @param key
	 * @param index
	 * @return str[index]
	 */
	public static String findValueInStringWithComma(String target, int index) throws JBranchException{	
		String[] str = target.split(",");
		return str[index];		
	}
		
	// 我是測試測試測試喔 不重要所以說三次
	public static void main(String[] arg){
		Map<String, Object> fromMap = new HashMap<String, Object>();
		fromMap.put("CUST_ID", "ASDFG");
		fromMap.put("BIRTHDAY", new Date());
		fromMap.put("GENDER", 1);
		fromMap.put("MONEY", new BigDecimal("23456.8976"));
		System.out.println(new Gson().toJson(dataMappingByColumn(fromMap, new String[]{"CUST_ID", "GENDER"})));
		
		toLowerCamelCase("CUST_DDD_AAA");
	}
}
