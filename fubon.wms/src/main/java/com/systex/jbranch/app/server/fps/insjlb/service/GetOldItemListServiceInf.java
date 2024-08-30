package com.systex.jbranch.app.server.fps.insjlb.service;

import com.systex.jbranch.app.server.fps.insjlb.vo.GetOdItemListInputVO;
import com.systex.jbranch.app.server.fps.insjlb.vo.GetOldItemListOutputVO;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
/**取得既有保障明細**/
public interface GetOldItemListServiceInf {
	/**@param GetOdItemListInputVO<br>
	 * 	---	String planTypes 保險規劃缺口類型： <br> 
	 * 	------ L:壽險  <br>
	 * 	------ P:意外險  <br>
	 * 	------ H:醫療住院卻口  <br>
	 * 	------ C:癌症住院缺口  <br>
	 * 	------ D:重大疾病缺口  <br>
	 * 	------ W:長期看護缺口  <br>
	 * 	------ R:生存金 , 可放入多個，使用逗號區隔 <br>
	 * 	--- String custId：客戶ID  <br>
	 * 	--- String loginBranch ：登陸者分行  <br>
	 * 	--- List<String> loginAOCode ：登陸者的ao_code  <br>
	 * PS.登入者查的客戶,是要屬於自己的或是客戶沒有掛AO code的客戶，因此參數需要傳AO CODE跟分行<p>
	 * 
	 * @return GetOldItemListOutputVO<br>
	 * --- List oldItemLlist內容：<br>
	 * ------ [String] SOURCE_TYPE：既有保障來源<br>
	 * ------ [String] INSCO ： 保險公司編號<br>
	 * ------ [String] INSCO_NAME： 保險公司名稱<br>
	 * ------ [String] POLICYNO	：保單號碼<br>
	 * ------ [String] PROD_ID：商品代號<br>
	 * ------ [String] PROD_NAME：商品名稱<br>
	 * ------ [String] QUANTITY：保額<br>
	 * ------ [BigDecimal] PREMIUM：年化保費<br>
	 * ------ [BigDecimal] coverage：保障金額(元)<br>
	 * ------ [List]lstCoverage：保障清單<br>
	 * --------- [String]planTypes：<br>
	 * ------------ L:壽險 (B0009) <br>
	 * ------------ P:意外險 (B0010) <br>
	 * ------------ HD:醫療住院 (10401_S) <br>
	 * ------------ HR:醫療實支實付 (?) <br>
	 * ------------ C:癌症住院 (30401_1S) <br>
	 * ------------ D:重大疾病 (61106) <br>
	 * ------------ W:長期看護 (按月?) <br>
	 * ------------ R:生存金<br>
	 * --- List lstLogTable：錯誤資訊
	 */
	public GetOldItemListOutputVO getOldItemList(GetOdItemListInputVO getOdItemListInputVO)throws JBranchException;
}
