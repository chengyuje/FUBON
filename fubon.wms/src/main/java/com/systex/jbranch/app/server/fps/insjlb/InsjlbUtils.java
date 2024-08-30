package com.systex.jbranch.app.server.fps.insjlb;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;

import com.systex.jbranch.comutil.callBack.CallBackExcute;
import com.systex.jbranch.comutil.collection.CollectionSearchUtils;
import com.systex.jbranch.comutil.collection.GenericMap;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.XmlInfo;

@SuppressWarnings({ "rawtypes", "unchecked"})
public class InsjlbUtils{		
	public static int calculateInsAge(Date systemDate , Date birthday) {
		// 系統時間
		Calendar systemCal = Calendar.getInstance();
		//生日
		Calendar birthdayCal = Calendar.getInstance();
		
		// 保險年齡相差年 = 系統年分 - 生日年分
		int ageYear = 0;
		// 保險年齡相差月
		int ageMonth = 0;
		// 保險年齡相差日
		int ageDay = 0;
		
		//判斷日期是否為空，若為空給予系統日期，若有則設定給systemCal
		systemCal.setTime(systemDate == null ? new Date() : systemDate);
		//設定生日
		birthdayCal.setTime(birthday);
		
		//計算相差年月日
		ageYear = systemCal.get(Calendar.YEAR) - birthdayCal.get(Calendar.YEAR);
		ageMonth = systemCal.get(Calendar.MONTH) - birthdayCal.get(Calendar.MONTH);
		ageDay = systemCal.get(Calendar.DATE) - birthdayCal.get(Calendar.DATE);
		
		//相差日為負，退一月，再判斷此月是否小於0，若是再退一年，並把此月加上12
		if ((ageMonth = ageDay < 0 ? ageMonth - 1 : ageMonth) < 0) {
			ageYear --;
			ageMonth += 12;
		}
		
		//四捨五入相差年，若月大於5則進一年
		if (ageMonth >= 6) {
			ageYear += 1;
		}
		
		return ageYear;
	}

	public static List<String> createErrorMsgList(String...args){
		List errorMsg = new ArrayList<String>();
		CollectionUtils.addAll(errorMsg, args);
		return errorMsg;
	}
	
	
	/**
	 * 日期比較
	 * 
	 * @param srcDate
	 * @param dstDate
	 * @return
	 */
	public static Boolean compareDate(Date srcDate, Date dstDate) {
		if (dstDate == null) {
			return true;
		} else if (srcDate != null && srcDate.compareTo(dstDate) >= 0) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * 取得最大年齡
	 * 
	 * @param age
	 * @return
	 */
	public static int getMaxAge(String[] age, int insuredAge) {
		int maxAge_int = 0;
		int curAge_int = 0;
		for (String age_str : age) {
			age_str = age_str.replace("@", "");
			curAge_int = getBigDecimal(age_str).intValue();
			if (maxAge_int < curAge_int) {
				maxAge_int = curAge_int;
			}
		}

		return maxAge_int;
	}
	
	/**
	 * 將 vo 得到的值轉化為 String 類型
	 * 
	 * @param obj
	 * @return
	 */
	public static String getString(Object obj) {
		if (obj != null) {
			return obj.toString();
		} else {
			return "";
		}
	}
	
	/**
	 * 將 vo 得到的值轉化為 BigDecimal 類型(返回值 1)
	 * 
	 * @param obj
	 * @return
	 */
	public static BigDecimal getBigDecimal(Object obj, BigDecimal res) {
		BigDecimal res_big = getBigDecimal(obj);
		if (BigDecimal.ZERO.compareTo(res_big) == 0) {
			return res;
		} else {
			return res_big;
		}
	}
	
	/**
	 * 將 vo 得到的值轉化為 BigDecimal 類型
	 * 
	 * @param obj
	 * @return
	 */
	public static BigDecimal getBigDecimal(Object obj) {
		if (obj != null && !"".equals(obj)) {
			return new BigDecimal(obj.toString());
		} else {
			return BigDecimal.ZERO;
		}
	}
	
	/**
	 * 將 值Math.round后返回
	 * 2012/12/20,賴禮強,ADD
	 * @param obj
	 * @return
	 */
	public static BigDecimal getRound(Object obj) {
		if (obj != null && !"".equals(obj)) {
			return new BigDecimal(Math.round(Double.parseDouble(getString(obj))));
		} else {
			return BigDecimal.ZERO;
		}
	}
	
	/**
	 * 轉目標類型：Timestamp
	 * 
	 * @param obj
	 * @return
	 */
	public static Timestamp getTimestamp(Object obj) {
		if (obj != null) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			return Timestamp.valueOf(sdf.format(obj));
		} else {
			return null;
		}
	}
	
	/**
	 * 轉目標類型：Timestamp
	 * 
	 * @param obj
	 * @return
	 */
	public static Timestamp getTimestamp(String str) {
		if (!StringUtils.isBlank(str)) {
			String[] str_arr = str.split("/");
			String date_str = "" + (Integer.valueOf(str_arr[0]) + 1911) + "-" + str_arr[1] + "-" + str_arr[2] + " 00:00:00";
			return Timestamp.valueOf(date_str);
		} else {
			return null;
		}
	}
	
	/**
	 * 轉目標類型：Date
	 * 
	 * @param obj
	 * @return
	 */
	public static Date getDate(Object obj) {
		if (obj != null) {
			Calendar cld = Calendar.getInstance();
			cld.set(getDateInt(obj, "yyyy", false),
					getDateInt(obj, "MM", false) - 1,
					getDateInt(obj, "dd", false),
					0,0,0);
			return cld.getTime();
		} else {
			return null;
		}
	}
	
	/**
	 * 將 Date 類型取得 int 類型(由 pattern 指定 年、月、日)
	 * 
	 * @param obj
	 * @return
	 */
	public static int getDateInt(Object obj, String pattern, Boolean yyyBol) {
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		if (obj != null) {
			if (yyyBol) {
				return Integer.valueOf(sdf.format(obj)) - 1911;
			} else {
				return Integer.valueOf(sdf.format(obj));
			}
		} else {
			return 0;
		}
	}
	
	/**
	 * 將非家庭戶(個人)的資訊移除
	 * @param data_lst
	 * @param input_lst
	 * @return
	 */
	public static List changeCoverage(List dataLst , List<Map<String , Object>> familyList){
		//如果有其一為空直接回傳不用做移除動作
		if(CollectionUtils.isEmpty(dataLst) || CollectionUtils.isEmpty(familyList))
			return dataLst;
		
		//由後至前逐筆比對
		for(int dataIndexIdx = dataLst.size() - 1 ; dataIndexIdx >= 0 ; dataIndexIdx--){
			Map<String, Object> dataMap = (Map<String, Object>)dataLst.get(dataIndexIdx);
			String INSURED_ID =  ObjectUtils.toString(dataMap.get("INSURED_ID")).trim();
			
			//如果不在家庭戶中，則移除該筆資料於清單
			if(CollectionSearchUtils.findMapInColleciton(familyList , "RELATION_ID" , ObjectUtils.toString(INSURED_ID)) == null){
				dataLst.remove(dataIndexIdx);
			}
		}
	
		return dataLst;
	}
		
	public static String getParameterVal1(String queryKey , String valKey) throws JBranchException{
		return getParameter1(queryKey).get(valKey);
	}
	
	public static String getParameterVal2(String queryKey , String valKey) throws JBranchException{
		return getParameter2(queryKey).get(valKey);
	}
	
	public static String getParameterVal3(String queryKey , String valKey) throws JBranchException{
		return getParameter3(queryKey).get(valKey);
	}
	
	public static Map<String , String> getParameter1(String queryKey) throws JBranchException{
		return getConfigParameter(queryKey ,FormatHelper.FORMAT_1);
	}
	
	public static Map<String , String> getParameter2(String queryKey) throws JBranchException{
		return getConfigParameter(queryKey ,FormatHelper.FORMAT_2);
	}
	
	public static Map<String , String> getParameter3(String queryKey) throws JBranchException{
		return getConfigParameter(queryKey ,FormatHelper.FORMAT_3);
	}
	
	public static Map<String , String> getConfigParameter(String queryKey , String format) throws JBranchException{
		XmlInfo xmlInfo = new XmlInfo();
		return xmlInfo.doGetVariable(queryKey ,format);
	}
	
	/**分割字串後找出相近的數字*/
	public static Map findSimilarIntValNumber(String itemStr , String valStr , String separator ){
		if(!valStr.matches("((\\d+)|(\\-\\d+))(@?)") || !itemStr.matches("(((\\d+)|(\\-\\d+))(@?),)+")){
			Map result = new HashMap();
			result.put("item", itemStr.replaceFirst(",", ""));
			return result;
		}
		
		String reVal = valStr.replaceFirst("\\@", "");
		return findSimilarIntVal(itemStr , reVal , separator , false);
	}
	
	/**分割字串後找出相近的數字*/
	public static Map findSimilarIntVal(String itemStr , String valStr , String separator ){
		if(!valStr.matches("((\\d+)|(\\-\\d+))(@?)") || !itemStr.matches("(((\\d+)|(\\-\\d+))(@?),)+")){
			Map result = new HashMap();
			result.put("item", itemStr);
			return result;
		}
		
		String reVal = valStr.replaceFirst("\\@", "");
		
		Map valMap1 = findSimilarIntVal(itemStr , reVal , separator , false);
		String val1 = ObjectUtils.toString(valMap1.get("item"));
		
		Map valMap2 = findSimilarIntVal(itemStr , reVal , separator , true);
		String val2 = ObjectUtils.toString(valMap2.get("item"));
		
		//都沒比對出來
		if(StringUtils.isEmpty(val1) && StringUtils.isEmpty(val2)){
			return null;
		}
		//純數值比對出來，滿期沒比對出來
		else if(StringUtils.isEmpty(val1)){
			return valMap2;
		}
		//純數值比對出來，滿期沒比對出來
		else if(StringUtils.isEmpty(val2)){
			return valMap1;
		}
		
		int valTmp1 = new BigDecimal(reVal)
			.subtract(new BigDecimal(val1)).intValue();
		int valTmp2 = new BigDecimal(reVal)
			.subtract(new BigDecimal(val2.replaceFirst("\\@", ""))).intValue();
		
		valTmp1 = valTmp1 < 0 ? 0 - valTmp1 : valTmp1;
		valTmp2 = valTmp2 < 0 ? 0 - valTmp2 : valTmp2;
		
		if(valTmp1 < valTmp2){
			return valMap1;
		}
		else if(valTmp1 > valTmp2){
			return valMap2;
		}
		
		return valStr.matches("\\d+@$") ? valMap2 : valMap1;
	}
	
//	public static void main(String...args){
//		System.out.println(findSimilarIntVal("天才,白癡," , "13" , "," , true));
//	}
	
	/**分割字串後找出相近的數字，isMouse = true代表判斷有待小老鼠的，反之判斷純數字*/
	public static Map findSimilarIntVal(String itemStr , String valStr , String separator , boolean isMouse){		
		int val = new BigDecimal(valStr.replaceFirst("@", "")).intValue();
		int last = 99999;
		String[] items = itemStr.split(separator);
		String resultItem = null;
		Map result = new HashMap();
		
		int resultIdx = 0;
		int idx = -1;
		
		for(String item : items){
			idx++;
			
			if(item.equals(valStr)){
				result.put("idx", idx);
				result.put("item", item);
				return result;
			}
		}
		
		idx = -1;
		for(String item : items){
			String pattern = null;
			idx++;
			
			if(isMouse){
				pattern = "((\\d+)|(\\-\\d+))@?";
			}
			else if(!isMouse){
				pattern = "(\\d+)|(\\-\\d+)";
			}
			else{
				continue;
			}
			
			valStr = valStr.replaceFirst("@$", "");
			
			if(item.matches(pattern)){
				int itemYval = new BigDecimal(item.replaceFirst("@$", "")).intValue();
				//當比差距數
				int tmp = val - itemYval;
				tmp = tmp < 0 ? 0 - tmp : tmp;
				
				//上一次比對結果相距數
				int lastTmp = val - last;
				lastTmp = lastTmp < 0 ? 0 - lastTmp : lastTmp;
				
				if(tmp < lastTmp){
					last = itemYval;
					resultItem = item;
					resultIdx = idx;
				}
				else if(tmp == lastTmp){
					if(itemYval < last){
						last = itemYval;
						resultItem = item;
						resultIdx = idx;
					}
				}
			}
		}
		
		if(resultItem != null){
			result.put("idx", resultIdx);
			result.put("item", resultItem);
		}
		return result;
	}
	
	public static Map reMapKey(CallBackExcute callBack , Map map , Map<String , String>...rePatterns){
		GenericMap gMap = new GenericMap(reMapKey(map , rePatterns));
		
		if(callBack != null){
			callBack.callBack(gMap);
		}
		
		return gMap.getParamMap();
	}
	
	public static List<Map> reMapKey(List<Map> list , Map<String , String>...rePatterns){
		List resultList = new ArrayList();
		
		if(CollectionUtils.isEmpty(list)){
			return list;
		}
		
		for(Map map : list){
			if(MapUtils.isEmpty(map) || ArrayUtils.isEmpty(rePatterns)){
				continue;
			}

			for(Map<String , String> rePattern : rePatterns){
				map = reMapKeyContent(map , rePattern);
			}
			
			resultList.add(map);
		}
		return resultList;
	}
	
	public static Map reMapKey(Map map , Map<String , String>...rePatterns){
		if(MapUtils.isEmpty(map) || ArrayUtils.isEmpty(rePatterns)){
			return map;
		}

		for(Map<String , String> rePattern : rePatterns){
			map = reMapKeyContent(map , rePattern);
		}
		
		return map;
	}
	
	public static Map reMapKeyContent(Map map , Map<String , String> rePattern){
		if(MapUtils.isEmpty(map) || MapUtils.isEmpty(rePattern)){
			return map;
		}
		
		Set<String> keys = map.keySet();
		Map resultMap = new HashMap();
		
		for(String key : keys){
			Object value = map.get(key);	
			String reKey = null;
			Set<String> patternKeys = rePattern.keySet();
			
			for(String patternKey : patternKeys){
				if(key.matches(patternKey)){
					reKey = rePattern.get(patternKey);
					break;
				}
			}
			
			reKey = reKey == null ? key : reKey;
			resultMap.put(reKey , value);
		}
		
		return resultMap;
	}
	
	
	/**
	 * 把 String 類型的數據加上千分撇節
	 * 
	 * @param fromChar
	 * @return
	 */
	public static String changeStr(String fromChar) {
		Boolean isFirst_bol = false;
		String newString_str = "";
		
		if ("0".equals(fromChar) || fromChar == null) {
			return "0";
		} 
		else {
			String[] arrAtr = fromChar.split("\\.");
			fromChar = arrAtr[0];
			
			for (int index_int = fromChar.length(); index_int >= 3; index_int -= 3) {
				if (isFirst_bol) {
					newString_str = fromChar.substring(index_int - 3, index_int) + "," + newString_str;
				} 
				else {
					newString_str = fromChar.substring(index_int - 3, index_int);
					isFirst_bol = true;
				}
			}
			if (fromChar.length() % 3 != 0) {
				if (fromChar.length() < 3) {
					newString_str = fromChar;
				} else {
					newString_str = fromChar.substring(0, fromChar.length() % 3) + "," + newString_str;
				}
			}
			if (arrAtr.length == 2) {
				newString_str = newString_str + "." + arrAtr[1];
			}
			return newString_str;
		}
	}
	
	public static GenericMap reDftSettingToMap(String dftSetting) throws JBranchException{
		GenericMap dftSettingGmap = new GenericMap();
		
		if(StringUtils.isBlank(dftSetting))
			return dftSettingGmap;
		
		String[] dftSettings = dftSetting.split("-");
		Matcher matcher = Pattern.compile("\\|?([A-Za-z])\\|?").matcher(dftSettings[0]);
		Matcher matcher2 = Pattern.compile("\\|?([0-9a-zA-Z\\u4E00-\\u9FA5]+)\\|?").matcher(dftSettings[1]);
		
		//兩相對應的才會抓出來
		while(matcher.find() && matcher2.find()){
			dftSettingGmap.put(matcher.group(1), matcher2.group(1));
		}
		
		return dftSettingGmap;
	}
	
	/**取得有效年分-虛歲算法*/
	public static int doGetEffectedDate(Date effDate){
		int age = doGetEffectedDateRealAge(new Date() , effDate);
		return age + 1;
	}
	
	/**取得有效年分-實歲算法*/
	public static int doGetEffectedDateRealAge(Date effDate){
		int age = doGetEffectedDateRealAge(new Date() , effDate);
		return age == 0 ? 1 : age;
	}
	
	public static int doGetEffectedDateRealAge(Date sysDate , Date effDate){		
		Calendar sysCal = Calendar.getInstance();
		sysCal.setTime(sysDate);
		
		int sysYear = sysCal.get(Calendar.YEAR);//系統
		int effCalYear = 0;
		
		//生效日
		if(effDate != null){
			Calendar effCal = Calendar.getInstance();
			effCal.setTime(effDate);
			effCalYear = effCal.get(Calendar.YEAR);//系統
		}
		else{
			effCalYear = sysYear;//系統
		}
		
		return sysYear - effCalYear;
	}
	
	/**比對item對應位置去找list對應位置的內容*/
	public static String findByItemList(String item , String list , String mappingVal , String separator){
		String[] items = item.split(separator);
		String[] lists = list.split(separator);
		
		int i = 0;
		for(String tmpItem : items){
			if(tmpItem.equals(mappingVal)){
				return lists[i];
			}
			i++;
		}
		
		return "";
	}
}
