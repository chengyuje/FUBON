package com.systex.jbranch.platform.common.dataaccess.query;

/**
 * 定義Scrollable查詢結果類別
 *
 */	
public interface ScrollableQueryResultIF{
	
	/**
	 * scroll指定筆數
	 * @param i
	 * @return
	 */
	boolean scroll(int i);
	
	/**
	 * 游標移植第一筆
	 * @return
	 */
	boolean first(); 
	
	/**
	 * 游標移植最後一筆
	 * @return
	 */
	boolean last();
	
	/**
	 * 回傳現在游標是否位在第一筆
	 * @return
	 */
	boolean isFirst();
	
	/**
	 * 回傳現在游標是否位在最後一筆
	 * @return
	 */
	boolean isLast();
	
	/**
	 * 是否還有下一筆資料
	 * @return
	 */
	boolean hasNext();
	
	/** 
	 * 游標回至前一筆
	 * @return
	 */
	boolean previous();
	
	/**
	 * 取得下一筆資料
	 * @return
	 */
	Object [] next();
	
	/**
	 * 
	 */
	void close();
}
