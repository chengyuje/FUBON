package com.systex.jbranch.platform.common.util;

import java.util.Hashtable;
import java.util.Set;

/**
 * ThreadDataPool類別，用來存放個別Thread的全域變數，生命週期與Thread相同。
 * 所存入的資料無順序性。
 * 
 * @author Hong-jie
 * @version 1.0
 * @since 2009/03/12 
 */
public class ThreadDataPool {
	
	public static final String KEY_UUID = "$uuid";
	
	
	private static ThreadLocal<Hashtable<String, Object>> pool = new ThreadLocal<Hashtable<String, Object>>() {
        protected synchronized Hashtable<String, Object> initialValue() {
            return new Hashtable<String, Object>();
        }
    }; 
    
    /**
     * 依照指定Key值，取得Thread全域變數
     * @param key
     * @return 全域變數值
     */
    public static Object getData(String key){
    	return pool.get().get(key);
    }
    
    /**
     * 存入Thread全域變數
     * @param key
     * @param value
     */
    public static void setData(String key, Object value){
    	pool.get().put(key, value);
    }
    
    /**
     * 刪除Thread全域變數
     * @param key
     */
    public static void removeData(String key){
    	pool.get().remove(key);
    }
    
    /**
     * 取得所有Thread全域變數Key值
     * @return 所有的Key Set
     */
    public static Set<String> getAllKeys(){
    	return pool.get().keySet();
    }
    
    /**
     * 檢查全域變數Key值是否已存在
     * @param key
     * @return true = 已存在, false = 不存在
     */
    public static boolean isKeyContains(String key){
    	return pool.get().containsKey(key);
    }
    
    /**
     * 取得Pool目前儲存資料筆數
     * @return 筆數
     */
    public static int getPoolSize(){
    	return pool.get().size();
    }
   
}
