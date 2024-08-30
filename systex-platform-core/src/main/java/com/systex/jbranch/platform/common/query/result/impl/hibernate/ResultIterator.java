package com.systex.jbranch.platform.common.query.result.impl.hibernate;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 實作查詢結果Iterator interface
 *
 */
public class ResultIterator implements Iterator{

	private List datas = null;
	private Map<Object,Short> columnIndexMap = null;
	private String[] names         = null;
	private KeySet keySet = null;
	
	private int currentIdx = 0;
	
	
	ResultIterator(List datas, Map<Object,Short> columnIndexMap,KeySet keySet, String[] names){
		this.datas = datas;
		this.columnIndexMap = columnIndexMap;
		this.keySet = keySet;
		this.names = names;
	}
	
	/**
	 * 實作Iterator interface
	 */
	public boolean hasNext() {
		if(currentIdx+1 > datas.size())
			return false;
		else
			return true;
	}

	/**
	 * 實作Iterator interface
	 */
	public Object next() {
		Map map = new ColumnMap((Object[])datas.get(currentIdx),columnIndexMap,keySet, names);
		currentIdx++;
		return map;
		
	}

	/**
	 * 實作Iterator interface
	 */
	public void remove() {
		throw new UnsupportedOperationException("remove() Unsupported");
	}

}
