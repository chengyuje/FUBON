package com.systex.jbranch.office.word;

import java.util.Map;

import org.dom4j.Document;

import com.systex.jbranch.office.word.bookmark.BookmarkContent;

public abstract class WordReader {

	protected static final String XML_ENCODING = "UTF-8";
	protected Document doc;
	protected String srcPath;
	
	/**
	 * @param path 來源檔路徑
	 * @throws Exception
	 */
	public WordReader(String path) throws Exception {
		srcPath = path;
		doc = initDoc(path);
	}
	
	/**
	 * 取得doc
	 * @return doc
	 */
	public Document getDocument(){
		return this.doc;
	}
	
	/**
	 * @param path 來源檔路徑
	 * @return Dom4j document instance
	 * @throws Exception
	 */
	protected abstract Document initDoc(String path) throws Exception;

	/**
	 * 取得所有書籤
	 * @return map
	 */
	public abstract Map<String, BookmarkContent> getBookmarkRange();
	
	/**
	 * 儲存檔案
	 * @throws Exception
	 */
	public void save() throws Exception{
		this.save(srcPath);
	}
	
	/**
	 * 釋放資源
	 * @throws Exception
	 */
	public abstract void close() throws Exception;
	
	
	/**
	 * 另存檔案
	 * @param savePath 存檔路徑
	 * @throws Exception
	 */
	public abstract void save(String savePath) throws Exception;
	
	/**
	 * 取得文件頁數
	 * @return 文件頁數
	 */
	public abstract int getTotalPageSize();
	
	/**
	 * 取得建構類別
	 * @return WordBuilder
	 */
	public abstract WordBuilder getBuilder();

}
