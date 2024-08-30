package com.systex.jbranch.platform.common.dataaccess.query;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

/**
 * 此介面定義了查詢結果類別
 * 繼承了List介面並擴充相關查詢結果資訊
 */
public interface ResultIF extends List{
	
	/**
	 * 取得查詢結果總頁數
	 * @return
	 */
	public int getTotalPage();
	
	/**
	 * 取得查詢結果總筆數
	 * @return
	 */
	public int getTotalRecord();
	
	/**
	 * 取得所有查詢結果資料，以List封裝
	 * 若是VO，則回傳的結果是List<VO>
	 * 若是資料，則回傳的結果為List<Map<fieldName, value>>
	 * @return
	 */
	public List<Object[]> getData();

	/**
	 * for StoredProcedure output parameter
	 * 當輸出值為數值型態時皆轉換為java.math.BigDecimal
	 * @param index
	 * @return
	 */
	public BigDecimal getCallableOutBigDecimal(int index);
	
	/**
	 * for StoredProcedure output parameter
	 * 當輸出值為Binary型態時皆轉換為byte array
	 * @param index
	 * @return
	 */
	public byte[] getCallableOutByteArray(int index);
	
	/**
	 * for StoredProcedure output parameter
	 * 當輸出值為日期型態時皆轉換為java.sql.Timestamp
	 * @param index
	 * @return
	 */
	public Timestamp getCallableOutTimestamp(int index);
	
	/**
	 * for StoredProcedure output parameter
	 * 當輸出值為文字型態時皆轉換為java.lang.String
	 * @param index
	 * @return
	 */
	public String getCallableOutString(int index);
}
