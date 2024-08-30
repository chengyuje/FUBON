package com.systex.jbranch.platform.common.query.result.impl.hibernate;

import java.util.AbstractMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 實作Map介面，提供複雜查詢結果的欄位資訊
 *
 */
public class ColumnMap extends AbstractMap implements Map{

	private Object [] values;
	private String [] columnNames;
	private Set<Entry>    set = null;
	private Map<Object,Short> columnIndexMap = null;
	private KeySet keys = null;

	/**
	 * constructor
	 * @param values
	 * @param columnIndexMap
	 * @param keys
	 * @param columnNames
	 */
	ColumnMap(Object [] values,Map<Object,Short> columnIndexMap,KeySet keys, String ...columnNames){
		this.values         = values;
		this.columnNames    = columnNames;
		this.columnIndexMap = columnIndexMap;
		this.keys           = keys;
	}

	/**
	 * 實作map interface
	 */
	public Set<Entry> entrySet() {
		set = new HashSet<Entry>();
		for(int i=0; i<values.length; i++){
//			set.add(columnNames[i]+"="+values[i]);
			set.add(new EntryImpl(columnNames[i], values[i]));
		}
		return set;
	}

	/**
	 * 實作map interface
	 */
	public Set keySet(){
		return keys;
	}

	/**
	 * 實作map interface
	 */
	public Object get(Object key){
		Short index = columnIndexMap.get(key);
		if(index != null){
			return values[index];
		}else{
			return null;
		}
	}

	/**
	 * 實作map interface
	 */
	public void set(Object key, Object value){
		Short index = columnIndexMap.get(key);
		if(index != null){
			values[index] = value;
		}
	}
    
    @Override
    public String toString() {
        return entrySet().toString();
    }

}
