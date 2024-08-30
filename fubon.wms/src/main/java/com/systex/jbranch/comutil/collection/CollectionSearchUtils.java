package com.systex.jbranch.comutil.collection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.ObjectUtils;

import com.systex.jbranch.comutil.callBack.CallBackExcute;
import com.systex.jbranch.platform.common.errHandle.JBranchException;

@SuppressWarnings({"rawtypes" , "unchecked"})
public class CollectionSearchUtils {
	/**判斷集合中是否有指定的元素存在**/
	public static boolean isfindInCollection(Collection collection , final String SEARCH_KEY){
		return CollectionUtils.find(collection, new Predicate(){
			public boolean evaluate(Object object) {
				return ((Map)object).get(SEARCH_KEY) != null;
			}
		}) != null;
	}
	
	/**Map in Collection 中找出value與mappingObj相同的Map出來*/
	public static Map findMapInColleciton(Collection collection , final String key , final Object mappingObj){
		return (Map)CollectionUtils.find(collection , new Predicate(){
			public boolean evaluate(Object object) {
				return mappingObj.equals(((Map)object).get(key));
			}
		});
	}
	
	/**Map in Collection 中找出value與mappingObj相同的Map出來*/
	public static Map findMapInColleciton(Collection collection , final String key , final Object...mappingObjs){
		return (Map)CollectionUtils.find(collection , new Predicate(){
			public boolean evaluate(Object object) {
				for(Object mappingObj : mappingObjs){
					if(mappingObj.equals(((Map)object).get(key)))
						return true;
				}
				
				return false;
			}
		});
	}
	
	/**Map in Collection 中找出指定key的值與mappingObj指定key的值相符的*/
	public static Map findMapByKey(Collection collection , final Map mappingObj , final String...keys){
		return (Map)CollectionUtils.find(collection , getfindMapPredicate(mappingObj , keys));
	}
	
	/**Map in Collection 中找出指定key的值與mappingObj指定key的值相符的*/
	public static <T extends Collection> T selectMapByKey(Collection collection , final Map mappingObj , final String...keys){
		return (T)CollectionUtils.select(collection , getfindMapPredicate(mappingObj , keys));
	}
	
	/**Map in Collection 中找出value與mappingObj相同的Map出來*/
	public static <T>List<T> selectMapInList(
		Collection collection , final String key , final Object mappingObj) throws JBranchException{
		return selectMapInList(collection , key , mappingObj , null);
	}
	
	public static <T>List<T> selectMapInList(
		Collection collection , final String key , final Object mappingObj , CallBackExcute callBack) throws JBranchException{
		return selectMapInList(collection , key , mappingObj , callBack , true);
	}
	
	public static <T>List<T> selectMapNotInList(
		Collection collection , final String key , final Object mappingObj) throws JBranchException{
		return selectMapNotInList(collection , key , mappingObj , null);
	}
	
	public static <T>List<T> selectMapNotInList(
		Collection collection , final String key , final Object mappingObj , CallBackExcute callBack) throws JBranchException{
		return selectMapInList(collection , key , mappingObj , callBack , false);
	}
	
	public static <T>List<T> selectMapInListByString(
		Collection collection , final String key , final String mappingObj , final CallBackExcute callBack) throws JBranchException{
		return selectMapInListByString(collection , key , mappingObj , callBack , true);
	}
	
	public static <T>List<T> selectMapInListByString(
		Collection collection , final String key , final String mappingObj) throws JBranchException{
		return selectMapInListByString(collection , key , mappingObj , null , true);
	}
	
	public static <T>List<T> selectMapNotInListByString(
		Collection collection , final String key , final String mappingObj , final CallBackExcute callBack) throws JBranchException{
		return selectMapInListByString(collection , key , mappingObj , null , false);
	}
	
	public static <T>List<T> selectMapNotInListByString(
		Collection collection , final String key , final String mappingObj) throws JBranchException{
		return selectMapInListByString(collection , key , mappingObj , null , false);
	}
	
	public static Predicate getfindMapPredicate(final Map mappingObj , final String...keys){
		return new Predicate(){
			public boolean evaluate(Object object) {
				boolean success = true;
				
				if(mappingObj == null && object == null){
					return true;
				}
				else if((mappingObj != null && object == null) || (mappingObj == null && object != null)){
					return false;
				}
				
				for(String key : keys){
					success = success && mappingObj.get(key).equals(((Map)object).get(key));
				}
				return success;
			}
		};
	}
	
	public static <T>List<T> selectMapInListByString(
		Collection collection , 
		final String key , 
		final String mappingObj , 
		final CallBackExcute callBack , 
		final boolean positiveNegative) throws JBranchException {
		
		List resultList = new ArrayList<T>();
		
		select(collection , new Predicate(){
			public boolean evaluate(Object object) {
				GenericMap genericMap = new GenericMap((Map)object);
				String valStr = ObjectUtils.toString(genericMap.get(key));
				boolean result = mappingObj.equals(valStr);
				
				if((result = positiveNegative ? result : !result) && callBack != null){
					callBack.callBack(genericMap);
				}
				
				return result;
			}
		} , resultList);
		
		return resultList;
	}
	
	public static <T>List<T> selectMapInList(Collection collection , final String key , 
		final Object mappingObj , final CallBackExcute callBack , final boolean positiveNegative) throws JBranchException {
		
		List resultList = new ArrayList<T>();
		
		select(collection , new Predicate(){
			public boolean evaluate(Object object) {
				GenericMap genericMap = new GenericMap((Map)object);
				boolean result = mappingObj.equals(genericMap.get(key));
				
				if((result = positiveNegative ? result : !result) && callBack != null){
					callBack.callBack(genericMap);
				}
				
				return result;
			}
		} , resultList);
		
		return resultList;
	}
	
    public static void select(Collection<Map> inputCollection, Predicate predicate, Collection<Map> outputCollection) throws JBranchException {
        if (inputCollection != null && predicate != null) {
            for (Iterator<Map> iter = inputCollection.iterator(); iter.hasNext();) {
                Map item = iter.next();
                if (predicate.evaluate(item)) {
                    outputCollection.add(cloneMap(item));
                }
            }
        }
    }
    
    public static Object find(Collection collection , final Object obj) throws JBranchException {
    	return CollectionUtils.find(collection, new Predicate(){
			public boolean evaluate(Object arg0) {
				return arg0.equals(obj);
			}
    	});
    }
	
	public static boolean isMapEquals(Map map1 , Map map2 , String[] keys){
		if((MapUtils.isEmpty(map1) && MapUtils.isNotEmpty(map2)) || 
		   (MapUtils.isEmpty(map2) && MapUtils.isNotEmpty(map1))){
			return false;
		}
		
		for(Object key : keys){
			if(!map1.get(key).equals(map2.get(key)))
				return false;
		}
		
		return true;
	}
	
	/**只能複製map本身，map中的元素若為物件則會指向同一個實體**/
	public static List<? extends Map> cloneMapInList(List<? extends Map> list) throws JBranchException{
		List copyList = new ArrayList();
		
		for(Map obj : list){
			copyList.add(cloneMap((Map)obj));
		}
		
		return copyList;
	}
	
	/**只能複製map本身，map中的元素若為物件則會指向同一個實體**/
	public static Map cloneMap(Map map) throws JBranchException{
		if(map == null)
			return null;
		
		Map cloneMap = null;
		
		try{
			cloneMap = map.getClass().newInstance();
			
			for(Object key : map.keySet()){
				cloneMap.put(key, map.get(key));
			}
		}catch(Exception ex){
			throw new JBranchException(ex); 
		}
		
		return cloneMap;
	}
	
	public static Collection<Map> addEntry(Collection<Map> collection , Map newEnrty){
		for(Map map : collection){
			map.putAll(newEnrty);
		}
		return collection;
	}
	
	public static <T extends Collection>T addEntry(T collection , String key , Object val){
		for(Map map : (Collection<Map>)collection){
			map.put(key , val);
		}
		return collection;
	}
	
	public static StringBuffer reString(List<String> datas, String separator , boolean isEnd){
		StringBuffer sbr = new StringBuffer();
		
		for(int i = 0 ; i < datas.size() -1 ; i++){
			sbr.append(datas.get(i)).append(separator);
		}
		
		sbr.append(datas.get(datas.size() -1)).append(isEnd ? separator : "");
		
		return sbr;
	}
	
	public static StringBuffer reString(String[] datas, String separator , boolean isEnd){
		StringBuffer sbr = new StringBuffer();
		
		for(int i = 0 ; i < datas.length -1 ; i++){
			sbr.append(datas[i]).append(separator);
		}
		
		sbr.append(datas[datas.length -1]).append(isEnd ? separator : "");
		
		return sbr;
	}
	
}
