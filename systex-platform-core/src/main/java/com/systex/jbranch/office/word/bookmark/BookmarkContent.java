package com.systex.jbranch.office.word.bookmark;

import org.dom4j.Node;

public interface BookmarkContent<T> {
	
	/**
	 * 填加書籤物件
	 * @param element 書籤文字內容
	 */
	public void appendContent(T element);

	/**
	 * @param value 書籤文字
	 * @throws Exception
	 */
	public void setText(String value) throws Exception;
	
//	/**
//	 * 設定書籤文字
//	 * @param value 書籤文字
//	 * @param font 字型
//	 * @param color 顏色
//	 * @param size 字型大小
//	 */
//	public void setText(String value, String font, String color, String size) throws Exception;
	
	/**
	 * 取得書籤文字
	 * @return 書籤文字
	 */
	public String getText();
	
	/**
	 * @param value 功能變數內容
	 * @throws Exception
	 */
	public void setFuncationVarable(String value) throws Exception;
	
//	/**
//	 * 新增功能變數
//	 * @param value 功能變數內容
//	 * @param font 字型
//	 * @param color 顏色
//	 * @param size 字型大小
//	 */
//	public void setFuncationVarable(String value, String font, String color, String size) throws Exception;
	
	/**
	 * 功能變數內容
	 * @return string
	 */
	public String getFuncationVarable();
	
	/**
	 * 插入啟始頁碼
	 * @param startPage 啟始頁碼
	 */
	public void setPageCode(int startPage, int totalPage) throws Exception;
	
	/**
	 * 清除書籤內容
	 */
	public void clearContent();
}
