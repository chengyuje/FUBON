package com.systex.jbranch.comutil.collection;


import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;

@SuppressWarnings({"rawtypes" , "unchecked"})
public class GenericMap {
	private Map paramMap;
	
	public GenericMap(){
		this.paramMap = new HashMap();
	}
	
	public GenericMap(Map paramMap){
		this.paramMap = paramMap;
	}
	
	public <T>T get(Object key){
		return (T)paramMap.get(key);
	}
	
	public <T>T get(Object key , Class<T> cls){
		return (T)paramMap.get(key);
	}
	
	public static <T> T get(Object key , Map map){
		return (T)map.get(key);
	}
	
	public GenericMap put(Object key , Object val){
		paramMap.put(key, val);
		return this;
	}
	
	public GenericMap notNullPut(Object key , Object val){
		if(val != null)
			paramMap.put(key, val);
		return this;
	}
	
	public GenericMap putAll(GenericMap genericMap){
		paramMap.putAll(genericMap.paramMap);
		return this;
	}
	
	public String getStr(Object key){
		return (String)paramMap.get(key);
	}
	
	public String getStr(Object key, String defaultStr){
		return ObjectUtils.toString(paramMap.get( key ), defaultStr);
	}
	
	public String getNotNullStr(Object key){
		return ObjectUtils.toString(paramMap.get(key));
	}

	public boolean isStringBank(Object key){
		return StringUtils.isBlank(getNotNullStr(key));
	}
	
	public boolean isStringNotBank(Object key){
		return !isStringBank(key);
	}
	
	public boolean isNull(Object key){
		return get(key) == null;
	}
	
	public boolean isNotNull(Object key){
		return !isNull(key);
	}
	
	public <T>T get(Object key , T defaultObj){
		return (T) (isNull(key) ? defaultObj : get(key));
	}
	
	
	public BigDecimal getBigDecimal(String key) {
		String numberPattern = "\\-?((\\d+\\.\\d+(f|F|d|D)?)|(\\d+(f|F|d|D|l|L)?))$";
		Object obj = paramMap.get(key);
		
		if (obj == null) {
			return BigDecimal.ZERO;
		}
		else if(obj instanceof BigDecimal){
			return (BigDecimal)obj;
		}
		else if(obj instanceof String && obj.toString().matches(numberPattern)){
			return new BigDecimal(new Double(obj.toString()));
		}
		else if(obj instanceof Short){
			return new BigDecimal((Short)obj);
		}
		else if(obj instanceof Integer){
			return new BigDecimal((Integer)obj);
		}
		else if(obj instanceof Float){
			return new BigDecimal((Float)obj);
		}
		else if(obj instanceof Double){
			return new BigDecimal((Double)obj);
		}
		
		return BigDecimal.ZERO;
	}
	
	public Date getDate(String key){
		return get(key);
	}
	
	public Date getParseDate(String key) throws ParseException{
		String format = "yyyyMMddHHmmssSSS";
		Matcher matcher = Pattern.compile("\\d").matcher(getNotNullStr(key));
		StringBuffer sbr = new StringBuffer();
		
		while(matcher.find()){
			sbr.append(matcher.group());
		}
		
		format = format.substring(0 , sbr.length());
		return new SimpleDateFormat(format).parse(sbr.toString());
	}
	
	public String dateFormatString(String key , String pattern) throws ParseException{
		Date date = null;
		
		if((date = getDate(key)) == null){
			return "";
		}
		
		return new SimpleDateFormat(pattern).format(date);
	}
	
	
	public boolean matches(String key , String pattern){
		return getNotNullStr(key).matches(pattern);
	}
	
	public boolean equals(String key , Object val){
		Object val2 = get(key); 
		
		if(val == null && val2 == null){
			return true;
		}
		else if((val == null && val2 != null) ||(val != null && val2 == null)){
			return false;
		}
		
		return val.equals(val2);
	}
	
	public boolean nEquals(String key , Object val){
		return !equals(key , val);
	}
	
	public Map getParamMap() {
		return paramMap;
	}

	public void setParamMap(Map paramMap) {
		this.paramMap = paramMap;
	}
}
