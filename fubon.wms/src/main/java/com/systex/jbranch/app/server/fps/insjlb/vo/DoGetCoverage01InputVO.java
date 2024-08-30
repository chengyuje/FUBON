package com.systex.jbranch.app.server.fps.insjlb.vo;

import java.util.Map;


@SuppressWarnings("rawtypes")
public class DoGetCoverage01InputVO {
	/** List<Map> lstInsDetail：客戶保單資訊<br>
	 * --- [String] ALLPRODUCTSID：商品ID<br>
	 * --- [String] PREMIUM：單項總保費(原幣)<br>
	 * --- [String] KIND：屬性歸類(中文)<br>
	 * --- [String] SOCIALSECURITY：有無社保<br>
	 * --- [String] INSURED_AGE：投保年齡<br>
	 * --- [String] INSURED_GENDER：性別<br>
	 * --- [String] PAYTYPE：繳法<br>
	 * --- [String] JOB_GRADE：職等級<br>
	 * --- [String] PREMTERM：繳費年期<br>
	 * --- [String] ACCUTERM：累積年期(保障年期)<br>
	 * --- [String] UNIT：單位<br>
	 * --- [String] PLAN：計畫<br>
	 * --- [String] PRODQUANTITY：商品數量或保額(原幣)<br>
	 * --- [String] IDAYS：旅行平安險購買天數<br>
	 * --- [String] ICOUNT<br>
	 * --- [String] FIELDG：年金給付方式<br>
	 * --- [String] FIELDX：年金每次金額<br>
	 * --- [String] IOBJECT：保險對象<br>
	 * --- [String] POLICYDESC：保單保全資料<br>
	 */
	private Map lstInsDetail;// 客戶保單資訊
	private Map coverage01Map;
	
	public Map getLstInsDetail() {
		return lstInsDetail;
	}
	public void setLstInsDetail(Map lstInsDetail) {
		this.lstInsDetail = lstInsDetail;
	}
	public Map getCoverage01Map() {
		return coverage01Map;
	}
	public void setCoverage01Map(Map coverage01Map) {
		this.coverage01Map = coverage01Map;
	}
}