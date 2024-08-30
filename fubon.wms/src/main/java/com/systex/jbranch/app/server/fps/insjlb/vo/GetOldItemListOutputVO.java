package com.systex.jbranch.app.server.fps.insjlb.vo;

import java.util.List;

@SuppressWarnings("rawtypes")
public class GetOldItemListOutputVO {
	/**List oldItemLlist內容：<br>
	 * --- [String] SOURCE_TYPE：既有保障來源<br>
	 * --- [String] INSCO ： 保險公司編號<br>
	 * --- [String] INSCO_NAME： 保險公司名稱<br>
	 * --- [String] POLICYNO	：保單號碼<br>
	 * --- [String] PROD_ID：商品代號<br>
	 * --- [String] PROD_NAME：商品名稱<br>
	 * --- [String] QUANTITY：保額<br>
	 * --- [BigDecimal] PREMIUM：年化保費<br>
	 * --- [BigDecimal] coverage：保障金額(元)<br>
	 * --- [List]lstCoverage：保障清單<br>
	 * --------- [String]planTypes：<br>
	 * ------------ L:壽險 (B0009) <br>
	 * ------------ P:意外險 (B0010) <br>
	 * ------------ HD:醫療住院 (10401_S) <br>
	 * ------------ HR:醫療實支實付 (?) <br>
	 * ------------ C:癌症住院 (30401_1S) <br>
	 * ------------ D:重大疾病 (61106) <br>
	 * ------------ W:長期看護 (按月?) <br>
	 * ------------ R:生存金<br>
	 */
	private List oldItemLlist;
	
	/**List lstLogTable：錯誤資訊*/
	private List lstLogTable;
	
	public List getLstLogTable() {
		return lstLogTable;
	}
	public void setLstLogTable(List lstLogTable) {
		this.lstLogTable = lstLogTable;
	}

	public List getOldItemLlist() {
		return oldItemLlist;
	}

	public void setOldItemLlist(List oldItemLlist) {
		this.oldItemLlist = oldItemLlist;
	}
}
