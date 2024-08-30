package com.systex.jbranch.app.server.fps.cmmgr005;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.errHandle.APException;

/**
 * 取得排程相關資訊
 * @author Eli
 * @date 2017/12/12
 *
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class CMMGR005BatchInfo {
	private static String[] wkStr = new String[]{"", "周日","周一","周二","周三","周四","周五","周六"};
	private static String[] wkStrEng = new String[]{"", "SUN","MON","TUE","WED","THU","FRI","SAT"};
	private static final String TPEBNKVIPBAT01T = "TPEBNKVIPBAT01T";
	private static String periodExpresstion = "";
	private static String HR = "";
	private static String MIN = "";
	private static String SEC = "";
	private static String SLASH = "/";
	private static String COMMA = ",";
	private static String DASH = "-";
	private static String COLON = ":";
	private List srcList;
	private List ap1List = new ArrayList();
	private List ap2List = new ArrayList();
	private List errList = new ArrayList();
	
	/**Observer*/
	public void setSrcList(List inList) {
		srcList = inList;
		if (srcList != null) interprete(srcList); 
	}
	
	public List getAp1List() {
		return Collections.unmodifiableList(ap1List);
	}
	public List getAp2List() {
		return Collections.unmodifiableList(ap2List);
	}
	public List getErrList() {
		return Collections.unmodifiableList(errList);
	}
	
	/**解譯srcList，並用規範模式將整理過的資訊塞進指定的list中*/
	private void interprete(List<Map<String, String>> srcList) {
		Map<String, String> mapAp1 = new HashMap<String, String>();
		Map<String, String> mapAp2 = new HashMap<String, String>();
		for (Map srcMap: srcList) {
			try {
				setApMap(srcMap, processorIsAp01(srcMap)? mapAp1: mapAp2);
			} catch(Exception e) {
				errList.add(String.format("【排程代號: %s】【執行週期: %s】【執行主機: %s】", 
						checkMap(srcMap, "SCHEDULEID"), checkMap(srcMap, "CRONEXPRESSION"), checkMap(srcMap, "PROCESSOR")));
			}
		}
		integration(mapAp1, ap1List);
		integration(mapAp2, ap2List);
	}
	
	/**整合為UI呈現的資料結構*/
	private void integration(Map<String, String> mapAp, List apList) {
		Object[] keys = mapAp.keySet().toArray();
		Arrays.sort(keys);
		HashMap integrationMap = new HashMap();
		for (int i = 0; i < keys.length; i++) {
			integrationMap.put("TIME_POINT", keys[i]);
			integrationMap.put("BATCHES", mapAp.get(keys[i]));
			apList.add((Map)integrationMap.clone());
			integrationMap.clear();
		}
	}

	/**確認是否processor為 TPEBNKVIPBAT01P*/
	private boolean processorIsAp01(Map mapAP) {
		return TPEBNKVIPBAT01T.equals(checkMap(mapAP, "PROCESSOR").toUpperCase());
	}

	/**取得資訊放入Map中*/
	private void setApMap(Map srcMap, Map mapAp) throws APException {
		List<String> apList = new ArrayList<String>();
		apList = getTime(checkMap(srcMap, "CRONEXPRESSION"));
		
		for (String each: apList) {
			if (mapAp.containsKey(each)) {
				String beforeMapInfo = checkMap(mapAp, each);
				String afterMapInfo = beforeMapInfo + "、" + checkMap(srcMap, "SCHEDULEID") + periodExpresstion;
				mapAp.put(each, afterMapInfo);
			} else {
				mapAp.put(each, checkMap(srcMap, "SCHEDULEID") + periodExpresstion);
			}
		}
	}

	/**解譯時間表達式*/
	private List<String> getTime(String cronexpression) throws APException {
		periodExpresstion = "";
		List<String> timeStatementList = new ArrayList<String>();
		String[] expArr = cronexpression.split(" ");
		checkLegalExp(expArr);
		timeUnitLogic(expArr);
		periodLogic(expArr);
		minLogic();
		wrapQuote();
		addTimeStatementList(timeStatementList);
		
		return timeStatementList;
	}

	/**判斷格式是否正確*/
	private void checkLegalExp(String[] expArr) throws APException {
		if (expArr.length != 6) throw new APException("CRONEXPRESSION 不正確");
	}

	/**將時間陳述式加入參數List中*/
	private void addTimeStatementList(List<String> timeStatementList) {
		if (hasComma(HR)) compositeLogic(COMMA, timeStatementList);
		else if (hasDash(HR)) compositeLogic(DASH, timeStatementList);
	    else timeStatementList.add(compositeTime());
	}

	/**組合時間邏輯*/
	private void compositeLogic(String separator, List<String> timeStatementList) {
		for (Object each: hrLogic(separator)) {
			HR  = formatTime(each);
			timeStatementList.add(compositeTime());
		}
	}

	/**依據不同的分離符號，來返回hr陣列*/
	private Object[] hrLogic(String separator) {
		if (COMMA.equals(separator)) return HR.split(COMMA);
		if (DASH.equals(separator)) {
			String[] hrArr = HR.split(DASH);
			Integer start = Integer.parseInt(hrArr[0]);
			Integer end = Integer.parseInt(hrArr[1]);
			ArrayList<String> hrTransformList = new ArrayList();
			for(int i= start; i<= end; i++) {
				hrTransformList.add(String.valueOf(i));
			}
			return hrTransformList.toArray();
		}
		return new String[]{};
	}

	/**組合時間, hr:min:sec*/
	private String compositeTime() {
		return String.format("%s%s%s%s%s", HR, COLON, MIN, COLON, SEC);
	}

	/**處理時、分、秒*/
	private void timeUnitLogic(String[] expArr) {
		SEC = formatTime(expArr[0]);
		MIN = formatTime(expArr[1]);
		HR  = formatTime(expArr[2]);
	}

	/**將表達式括符起來*/
	private void wrapQuote() {
		periodExpresstion = "".equals(periodExpresstion) ? "" : "("+ periodExpresstion + ")";
	}

	/**分鐘處理邏輯*/
	private void minLogic() {
		if (hasSlash(MIN)) {
			String[] mins = MIN.split(SLASH); 
			MIN = formatTime(mins[0]);
			periodExpresstion = periodExpresstion + "每" + mins[1] + "分鐘";
		}
	}

	/**判斷字串是否包含 "/"*/
	private boolean hasSlash(String str) {
		return str.indexOf(SLASH) > -1;
	}
	
	/**判斷字串是否包含 "/"*/
	private boolean hasComma(String str) {
		return str.indexOf(COMMA) > -1;
	}
	
	/**判斷字串是否包含 "/"*/
	private boolean hasDash(String str) {
		return str.indexOf(DASH) > -1;
	}
	
	/**日、月、周的解譯*/
	private void periodLogic(String[] expArr) {
		periodExpresstion = (checkTimeOperator(expArr[3])) ? "" : expArr[3] + "日";
		periodExpresstion = (checkTimeOperator(expArr[4])) ? periodExpresstion : expArr[4] + "月" + periodExpresstion;
		periodExpresstion = (checkTimeOperator(expArr[5])) ? periodExpresstion : weekLogic(expArr[5].toUpperCase());
	}

	/**周的解譯*/
	private String weekLogic(String dayOfWeek) {
		if (dayOfWeek.length() == 3) {
			return wkStr[Arrays.asList(wkStrEng).indexOf(dayOfWeek)] + periodExpresstion;
		} else {
			return wkStr[Integer.parseInt(dayOfWeek)] + periodExpresstion;
		}
	}

	/**判斷時間符號*/
	private boolean checkTimeOperator(String expArrElement) {
		return "*".equals(expArrElement) || "?".equals(expArrElement);
	}

	/**將時間補為雙位數 ex: 1 => 01*/
	private String formatTime(Object expArrElement) {
		String element = isNotNull(expArrElement);
		return  element.length() == 1 ? "0" + element : element;
	}

	/**檢查Map取出欄位是否為Null*/
	private String checkMap (Map map, String key) {
		if (null != map && null != map.get(key)) {
			return String.valueOf(map.get(key));
		} else {
			return "";
		}
	}
	
	/** 將參數去空白，否則為 ""*/
	private String isNotNull(Object obj) {
		try {
			return obj.toString().trim();
		} catch (Exception e) {
			return "";
		}
	}
}
