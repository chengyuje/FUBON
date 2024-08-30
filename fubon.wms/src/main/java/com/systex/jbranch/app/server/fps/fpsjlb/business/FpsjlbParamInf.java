package com.systex.jbranch.app.server.fps.fpsjlb.business;


public interface FpsjlbParamInf {
	/**產品代碼*/
	public static final String PRD_ID = "PRD_ID";
	/**產品類型*/
	public static final String PRD_TYPE = "PRD_TYPE";
	/**產品類型*/
	public static final String PRD_ID_TYPE = PRD_ID + "_" + PRD_TYPE;
	/**權重*/
	public static final String WEIGHT = "WEIGHT";
	/**淨值*/
	public static final String PRD_NAV = "PRD_NAV";
	/**日報酬率*/
	public static final String RETURN_D = "RETURN_D";
	/**年化月報酬率*/
	public static final String RETURN_ANN_M = "RETURN_ANN_M";
	/**年化日報酬率*/
	public static final String RETURN_ANN_D = "RETURN_ANN_D";
	/**近1月報酬率**/
	public static final String RETURN_1M = "RETURN_1M";
	/**近1年報酬率**/
  public static final String RETURN_1Y = "RETURN_1Y";
	/**月報酬率*/
	public static final String RETURN_MTD = "RETURN_MTD";
	/**日報酬資料日期 - 淨值日*/
	public static final String DATA_DATE = "DATA_DATE";
	/**月報酬資料日期 */
	public static final String DATA_YEARMONTH = "DATA_YEARMONTH";
	/**比例下限*/
	public static final String LOWER = "LOWER";//比例上限
	/**比例上限*/
	public static final String UPPER = "UPPER";
	/**市場無風險利率*/
	public static final String CAPM_RATE = "CAPM_RATE";
	/**單筆投資金額或定期定額投資*/
	public static final String CASHFLOW = "CASHFLOW";
	/**單筆投資金額*/
	public static final String SINGLE_CASHFLOW = "SINGLE_CASHFLOW";
	/**投資的期數 (月數)*/
	public static final String SIM_PERIOD = "SIM_PERIOD";
	/**模擬次數*/
	public static final String SIM_TIMES = "SIM_TIMES";
	/**執行類型*/
	public static final String EXCUTE_TYPE = "EXCUTE_TYPE";
	/**摩地卡羅單筆*/
	public static final String MONTE_CARLO = "MONTE_CARLO";
	/**摩地卡羅定期*/
	public static final String MONTE_CARLO_REQ = "MONTE_CARLO_REQ";
	/**摩地卡羅單筆定期*/
	public static final String MONTE_CARLO_SINGLE_REQ = "MONTE_CARL_SINGLE_REQ";
	/**客戶ID*/
	public static final String CUST_ID = "CUST_ID";
	public final double RECORD_NULL_VAL = 0.0001;
	/**年度範圍區間*/
	public static final String DATA_DATE_YEAR = "DATA_DATE_YEAR";
	/**點的數量*/
	public static final String POINT_SIZE = "POINT_SIZE";
	/**傳入參數-產品參數集合*/
	public static final String RET_LIST = "RET_LIST";
	/**回傳*/
	public static final String RESULT = "RESULT";
	/**結束日期*/
	public static final String END_DATE = "END_DATE";
	/**維持年期*/
	public static final String STAY_YEAR = "STAY_YEAR";
	/**定期金額*/
	public static final String EACH_YEAR = "EACH_YEAR";
	
	/**單筆資料*/
	public static final String PURCHASED_SINGLE_LIST = "PURCHASED_SINGLE_LIST";
	/**定額資料*/
	public static final String PURCHASED_PERIOD_LIST = "PURCHASED_PERIOD_LIST";
	/**單筆 Total*/
	public static final String PURCHASED_SINGLE_VALUE = "PURCHASED_SINGLE_VALUE";
	/**定額 Total*/
	public static final String PURCHASED_PERIOD_VALUE = "PURCHASED_PERIOD_VALUE";
	/**最小共同區間*/
	public static final String DATA_DATE_MONTH_MIN = "DATA_DATE_MONTH_MIN";
	/**最大共同區間*/
	public static final String DATA_DATE_MONTH_MAX = "DATA_DATE_MONTH_MAX";
	/**剩餘月分*/
	public static final String REMAINING_MONTH = "REMAINING_MONTH";

}
