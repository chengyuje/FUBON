package com.systex.jbranch.platform.common.query.result.impl.hibernate;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.AbstractList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.systex.jbranch.platform.common.dataaccess.query.ResultIF;

/**
 * 實作ResultIF
 * 用於複雜查詢的回傳結果
 *
 */
public class ResultImpl extends AbstractList implements ResultIF{

	private Logger logger = LoggerFactory.getLogger(ResultImpl.class);
	
	private List     list;
	private String[] names         = null;
	private KeySet   keySet        = null;
	private int      totalPage     = -1;
	private int      totalRecord   = -1;
	private boolean  isObjectArray = false;
	private Map<Object,Short>    columnIndexMap     = null;
	private Map<Integer, Object> outputParameterMap = new HashMap<Integer, Object>();//for StoredProcedure
	private String[] columnNames;
	private int beginRow = -1;
	private int endRow = -1;

	public ResultImpl(List list,Map<Object,Short> columnIndexMap,boolean isObjectArray){
		this.list           = list;
		this.isObjectArray  = isObjectArray;
		this.columnIndexMap = columnIndexMap;
		logger.info("query rows size={}", list.size());
//		if(list.size()>0){
//			recordType = (list.get(0) instanceof VOBase ?
//					RECORD_TYPE_VO : RECORD_TYPE_OBJECT_ARRAY);
//		}
	}

	/**
	 * 請參考ResultIF
	 * @param index
	 * @param value
	 */
	public void setOutParameter(int index, Object value){
		outputParameterMap.put(index, value);
	}

    /**
     * 實作List interface
     */
	public Object get(int index) {
		if(isObjectArray && names != null){
			return new ColumnMap((Object[])list.get(index),columnIndexMap,keySet, names);
		}else{
			return list.get(index);
		}
	}
	
    /**
     * 實作List interface
     */
	public Object set(int index, Object element) {
		// TODO Auto-generated method stub
		return list.set(index, element);
	}

	/**
     * 實作List interface
     */
	public int size() {
		return list.size();
	}

    /**
     * 實作List interface
     */
	public Iterator iterator(){
        if (isObjectArray) {
		return new ResultIterator(list,columnIndexMap,keySet, names);
	}
        else {
            return list.iterator();
        }
    }

	/**
	 * 請參考ResultIF
	 */
	public int getTotalPage(){
		return totalPage;
	}

	/**
	 * 設定總頁數
	 * @param totalPage
	 */
	public void setTotalPage(int totalPage){
		this.totalPage = totalPage;
	}

	/**
	 * 設定查詢結果欄位名稱
	 * @param names
	 */
	public void setColumnName(String ...names){
		this.names = names;
		keySet = new KeySet(names);
	}

	/**
	 * 請參考ResultIF
	 */
	public List<Object[]> getData() {
		return this.list;
	}

	/**
	 * 請參考ResultIF
	 */
	public int getTotalRecord() {
		return totalRecord;
	}

	/**
	 * 設定查詢總筆數
	 * @param totalRecord
	 */
	public void setTotalRecord(int totalRecord) {
		this.totalRecord = totalRecord;
	}

	/**
	 * 請參考ResultIF
	 */
	public BigDecimal getCallableOutBigDecimal(int index){
		return (BigDecimal) outputParameterMap.get(index);
	}

	/**
	 * 請參考ResultIF
	 */
	public byte[] getCallableOutByteArray(int index){
		return (byte[]) outputParameterMap.get(index);
	}

	/**
	 * 請參考ResultIF
	 */
	public Timestamp getCallableOutTimestamp(int index){
		return (Timestamp)outputParameterMap.get(index);
	}

	/**
	 * 請參考ResultIF
	 */
	public String getCallableOutString(int index){
		return (String)outputParameterMap.get(index);
	}

	public String[] getColumnNames() {
		return columnNames;
	}

	public void setColumnNames(String[] columnNames) {
		this.columnNames = columnNames;
	}

	public int getBeginRow() {
		return beginRow;
	}

	public void setBeginRow(int beginRow) {
		this.beginRow = beginRow;
	}

	public int getEndRow() {
		return endRow;
	}

	public void setEndRow(int endRow) {
		this.endRow = endRow;
	}

}
