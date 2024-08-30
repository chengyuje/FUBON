package com.systex.jbranch.app.server.fps.crm999;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Service
 * @author Eli
 * @date 2017/11/23
 * @spec 提供 {@link #getParamList(Object, ArrayList)} 方法
 * 
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class CRM999Service {
	private static final SimpleDateFormat dateFormatOfSlash = new SimpleDateFormat("yyyy/MM/dd");
	
	/**
	 * 返回參數List，兩個參數必須具有相關性 (for execute <b>(問號/設值)</b>style)
	 * @param inputVO Object : <b>通常規範為一個資料物件</b>
	 * @param list ArrayList : <b>欲取得參數陣列</b>，<b>必須是第一個參數<code>inputVO</code>的屬性，且名稱必須符合規範(駝峰or table cols style)</b>
	 * @return ArrayList : <code>inputVO</code><b>屬性的陣列值</b>
	 * @throws Exception
	 */
	public static ArrayList getParamList(Object inputVO, ArrayList list) throws Exception {
		return generateParamList(generateFieldPair(inputVO.getClass(), inputVO), list);
	}
	
	private static Map generateFieldPair(Class clz, Object inputVO) throws Exception {
		Map pair = new HashMap();
		for(Field field : clz.getDeclaredFields()) {
			String fieldName = field.getName();
			String fieldGetMethodName = fieldName.replaceFirst("^.", "get" + fieldName.substring(0 , 1).toUpperCase());//get方法名稱 
			Object fieldInstance = inputVO.getClass().getMethod(fieldGetMethodName).invoke(inputVO);//取該field對應的get方法得回傳值
			pair.put(formatStr(fieldName), fieldInstance);
		}
		return pair;	
	}
	
	private static ArrayList generateParamList(Map rawData, ArrayList colList) throws Exception{
		ArrayList paramList = new ArrayList();
		for (Object col : colList) {
			paramList.add(processFormat(rawData.get(formatStr(col))));
		}
		return paramList;
	}

	private static Object processFormat(Object value) {
		if (value instanceof Date) return formatDate((Date)value);
		return value;
	}

	private static Object formatStr(Object str) {
		return ((String)str).replaceAll("_","").toUpperCase();
	}
	
	private static Object formatDate(Date date) {
		return dateFormatOfSlash.format(date);
	}
}
