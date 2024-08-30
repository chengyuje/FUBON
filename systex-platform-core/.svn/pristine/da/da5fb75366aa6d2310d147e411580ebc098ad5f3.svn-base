package com.systex.jbranch.platform.common.query.result.impl.hibernate;

import org.hibernate.ScrollableResults;

import com.systex.jbranch.platform.common.dataaccess.query.ScrollableQueryResultIF;

/**
 * 實作ScrollableQueryResultIF
 *
 */
public class ScrollableResultImpl implements ScrollableQueryResultIF {

	private ScrollableResults result;
	
	public ScrollableResultImpl(ScrollableResults result){
		this.result = result;
	}
	
	/**
	 * 請參考ScrollableQueryResultIF
	 */
	public boolean scroll(int i) {
		return result.scroll(i);
	}
	
	/**
	 * 請參考ScrollableQueryResultIF
	 */
	public void close(){
		result.close();
	}

	/**
	 * 請參考ScrollableQueryResultIF
	 */
	public boolean first() {
		return result.first();
	}

	/**
	 * 請參考ScrollableQueryResultIF
	 */
	public Object[] next() {
		return result.get();
	}

	/**
	 * 請參考ScrollableQueryResultIF
	 */
	public boolean isFirst() {
		return result.isFirst();
	}

	/**
	 * 請參考ScrollableQueryResultIF
	 */
	public boolean isLast() {
		return result.isLast();
	}

	/**
	 * 請參考ScrollableQueryResultIF
	 */
	public boolean last() {
		return result.last();
	}

	/**
	 * 請參考ScrollableQueryResultIF
	 */
	public boolean hasNext() {
		return result.next();
	}

	/**
	 * 請參考ScrollableQueryResultIF
	 */
	public boolean previous() {
		return result.previous();
	}

}
