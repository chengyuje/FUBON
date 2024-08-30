package com.systex.jbranch.comutil.collection;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.ArrayUtils;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class MapObjectUtils {	
	public static <T>T mapToObject(Map<String , Object> map , T object) throws Exception{
		return mapToObject(map , object , getDefaultMapToObjScase());
	}
	
	public static <T>T mapToObject(Map<String , Object> map , T object , SpecialCase...specialCaseArr) throws Exception{
		forEachMethod:
		for(Method method : object.getClass().getDeclaredMethods()){
			
			if(ArrayUtils.isEmpty(specialCaseArr)){
				return mapToObject(map , object);
			}
			
			for(SpecialCase specialCase : specialCaseArr){
				 if(specialCase.isSpaceial(method)){
					 specialCase.mapToObject(object, map);
					 continue forEachMethod;
				 }
			}
			
		}
		
		return object;
	}
	
	public static List<Map> arrayObjectToMap(Object [] arrays) throws Exception{
		return arrayObjectToMap(arrays , getDefaultObjToMapScase());
	}
	
	public static List<Map> arrayObjectToMap(Object [] arrays , SpecialCase...specialCase) throws Exception{
		List<Map> mapList = new ArrayList<Map>();
		
		for(Object obj : arrays)
			mapList.add(objectToMap(obj , specialCase));
		
		return mapList;
	}
	
	public static List<Map<String , Object>> arrayObjectToMap(List<Object> arrays) throws Exception{
		return arrayObjectToMap(arrays , getDefaultObjToMapScase());
	}
	
	public static List<Map<String , Object>> arrayObjectToMap(List<Object> arrays , SpecialCase...specialCase) throws Exception{
		List<Map<String , Object>> mapList = new ArrayList<Map<String , Object>>();
		
		for(Object obj : arrays)
			mapList.add(objectToMap(obj , specialCase));
		
		return mapList;
	}
	
	public static <T> Map<String , Object> objectToMap(T object) throws Exception{
		return objectToMap(object , getDefaultObjToMapScase());
	}
	
	public static <T> Map<String , Object> objectToMap(T object , SpecialCase...specialCaseArr) throws Exception{
		Map<String , Object> map = new HashMap<String , Object>();
		forEachMethod:
		for(Method method : object.getClass().getDeclaredMethods()){
			if(ArrayUtils.isEmpty(specialCaseArr)){
				return map;
			}
			
			for(SpecialCase specialCase : specialCaseArr){
				 if(specialCase.isSpaceial(method)){
					 specialCase.mapToObject(object, map);
					 continue forEachMethod;
				 }
			}
			
			//如果沒有特殊的就以預設的萊處理
			getDefaultObjToMapScase().mapToObject(object, map);
		}
		return map;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static SpecialCase getDefaultObjToMapScase(){
		return new SpecialCase(){
			public boolean isSpaceial(Method method) {
				return true;
			}
			
			public void mapToObject(Object object, Map map) throws Exception {
				for(Method method : object.getClass().getDeclaredMethods()){
					//只抓不傳參數的get
					if(!method.getName().matches("^get.+$") || method.getParameterTypes().length > 0){
						continue;
					}
					
					map.put(method.getName().replaceFirst("^get" , "") , method.invoke(object));
				}
			}
		};
	}
	
	public static SpecialCase getDefaultMapToObjScase(){
		return new SpecialCase(){
			@Override
			public boolean isSpaceial(Method method) throws Exception {
				return true;
			}

			@Override
			public void mapToObject(Object object, Map map) throws Exception {
				for(Method method : object.getClass().getDeclaredMethods()){
					if(method.getName().matches("^set.+$") && method.getParameterTypes().length == 1){
						String key = method.getName().replaceFirst("^set" , "");
						
						if(map.get(key) != null){
							method.invoke(object , map.get(method.getName().replaceFirst("^set", "")));	
						}
					}
				}
			}
		};
	}
	
	//預設大寫key
	public static SpecialCase getInsMapToObjUpperCase(){
		return new SpecialCase(){
			public boolean isSpaceial(Method method) {
				return true;
			}
			
			public void mapToObject(Object object, Map map) throws Exception {
				for(Method method : object.getClass().getDeclaredMethods()){
					if(!method.getName().matches("^get.+$") || method.getParameterTypes().length > 0)
						continue;
				
					map.put(method.getName().replaceFirst("^get" , "").toUpperCase() , method.invoke(object));
				}
			}
		};
	}
	
	/** map key 轉駝峰式 **/
	public static List<Map> reHumpByKey(List<Map> list){
		for(int idx = 0 ; idx < list.size() ; idx++){
			list.set(idx , reHumpByKey(list.get(idx)));
		}
		
		return list;
	}
	
	/** map key 轉駝峰式 **/
	public static Map<String , Object> reHumpByKey(Map<String , Object> map){
		Map<String , Object> reMap = new HashMap<String , Object>();
		
		Set<String> keys = map.keySet();
		
		for(String key : keys){
			String lowerCaseKey = key.toLowerCase();
			String reLowerCaseKey = "";
			
			if(!key.toString().matches(".*_.*")){
				reMap.put(lowerCaseKey , map.get(key));
				continue;
			}
			
			String[] splitLowerKeyChrs = lowerCaseKey.split("_");
			
			for(String splitLowerKeyChr : splitLowerKeyChrs){
				reLowerCaseKey += 
					splitLowerKeyChr.substring(0 , 1).toUpperCase() + 
					splitLowerKeyChr.substring(1 , splitLowerKeyChr.length()); 
			}
			
			reLowerCaseKey = 
				reLowerCaseKey.substring(0 , 1).toLowerCase() + 
				reLowerCaseKey.substring(1 , reLowerCaseKey.length());
			
			reMap.put(reLowerCaseKey , map.get(key));
		}
		
		return reMap;
	}
}
