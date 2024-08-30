package com.systex.jbranch.app.server.fps.fpsjlb.conf;

import static com.systex.jbranch.app.server.fps.fpsjlb.business.FpsjlbParamInf.PRD_ID;
import static com.systex.jbranch.app.server.fps.fpsjlb.business.FpsjlbParamInf.PRD_TYPE;
import static com.systex.jbranch.app.server.fps.fpsjlb.business.FpsjlbParamInf.RETURN_1M;
import static com.systex.jbranch.app.server.fps.fpsjlb.business.FpsjlbParamInf.RETURN_ANN_M;
import static com.systex.jbranch.app.server.fps.fpsjlb.business.FpsjlbParamInf.RETURN_D;
import static com.systex.jbranch.app.server.fps.fpsjlb.business.FpsjlbParamInf.DATA_DATE;
import static com.systex.jbranch.app.server.fps.fpsjlb.business.FpsjlbParamInf.DATA_YEARMONTH;
import org.apache.commons.lang.ArrayUtils;

import com.ibm.icu.util.Calendar;

public enum TableType {
	/**歷史月報酬*/
	TBFPS_PRD_RETURN_D(
		DATA_DATE , 
		"1231"
	) ,
	/**歷史日報酬*/
	TBFPS_PRD_RETURN_M(
		DATA_YEARMONTH , 
		"12"
	) ,
	/**蒙地卡羅模擬法 - 歷史月報酬*/
	MONTE_CARLO_TO_M(
		TBFPS_PRD_RETURN_M ,
		new String[]{
			PRD_ID , PRD_TYPE , RETURN_ANN_M
		} , 
		new String[]{
			RETURN_ANN_M
		} , 
		RETURN_ANN_M
	),
	/**效率前緣資產配置計算 - 歷史月報酬*/
	PORT_EFF_FRONTIER(
		TBFPS_PRD_RETURN_M , 
		new String[]{
			PRD_ID , PRD_TYPE , RETURN_1M
		} , 
		new String[]{
			RETURN_1M
		} ,
		RETURN_1M
	) ,
	/**投資組合波動度計算 - 歷史日報酬*/
	PORTFOLIO_VOLATILITY(
		TBFPS_PRD_RETURN_D , 
		new String[]{
			PRD_ID , PRD_TYPE , RETURN_D
		} , 
		new String[]{
			RETURN_D
		} , 
		RETURN_D
	),
	/**預期投資組合年化報酬模擬*/
	PORT_RTN_SIM(
		TBFPS_PRD_RETURN_M , 
		new String[]{
			PRD_ID , PRD_TYPE , RETURN_1M
		} , 
		new String[]{
			RETURN_1M
		} ,
		RETURN_1M
	);

	/**日月報酬對應的日期欄位*/
	private String dayColumnName;
	/**日月報酬table name*/
	private String tableName;
	/**日月報酬日期格式*/
	private String daySuffix;
	/**歷史查詢回傳欄位*/
	private String[] resultColumn;
	/**檢核不可為空欄位，程式篩選共同區間的判斷條件*/
	private String[] notNullColumn;
	/**要抓的報酬率欄位名稱*/
	private String rateName;
	
	TableType(String dayColumnName , String daySuffix){
		this.dayColumnName = dayColumnName; 
		this.daySuffix = daySuffix;
		this.tableName = this.name();
	}
	
	TableType(TableType type){
		this.dayColumnName = type.getDayColumnName(); 
		this.daySuffix = type.getDaySuffix();
	}
	
	TableType(TableType type , String[] resultColumn , String [] notNullColumn , String rateName){
		this(type);
		this.tableName = type.name();
		this.resultColumn = (String[]) ArrayUtils.add(resultColumn, type.getDayColumnName());
		this.notNullColumn = notNullColumn;
		this.rateName = rateName;
	}

	public String getDayColumnName() {
		return dayColumnName;
	}
	
	public String getDaySuffix() {
		return daySuffix;
	}

	public String[] getResultColumn() {
		return resultColumn;
	}

	public String[] getNotNullColumn() {
		return notNullColumn;
	}
	
	public String getTableName() {
		return tableName;
	}

	public String getRateName() {
		return rateName;
	}

	public String beforeSysDateStr(int year){
		year = Calendar.getInstance().get(Calendar.YEAR) - year - 1;
		return String.valueOf(year) + daySuffix;
	}

}
