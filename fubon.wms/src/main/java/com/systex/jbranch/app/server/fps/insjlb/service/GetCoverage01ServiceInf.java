package com.systex.jbranch.app.server.fps.insjlb.service;

import java.util.ArrayList;
import java.util.Map;

import com.systex.jbranch.app.server.fps.insjlb.vo.DoGetCoverage01InputVO;
import com.systex.jbranch.app.server.fps.insjlb.vo.DoGetCoverage01OutputVO;
import com.systex.jbranch.platform.common.errHandle.JBranchException;

/**健檢攤列的各項資料(單筆)*/
public interface GetCoverage01ServiceInf {
	/**呼叫保險資訊源doGetCoverage01：健檢攤列的各項資料(單筆)
	 * @param DoGetCoverage01InputVO<br>
	 * --- List<Map> lstInsDetail：客戶保單資訊<br>
	 * ------ [String] ALLPRODUCTSID：商品ID<br>
	 * ------ [String] PREMIUM：單項總保費(原幣)<br>
	 * ------ [String] KIND：屬性歸類(中文)<br>
	 * ------ [String] SOCIALSECURITY：有無社保<br>
	 * ------ [String] INSURED_AGE：投保年齡<br>
	 * ------ [String] INSURED_GENDER：性別<br>
	 * ------ [String] PAYTYPE：繳法<br>
	 * ------ [String] JOB_GRADE：職等級<br>
	 * ------ [String] PREMTERM：繳費年期<br>
	 * ------ [String] ACCUTERM：累積年期(保障年期)<br>
	 * ------ [String] UNIT：單位<br>
	 * ------ [String] PLAN：計畫<br>
	 * ------ [String] PRODQUANTITY：商品數量或保額(原幣)<br>
	 * ------ [String] IDAYS：旅行平安險購買天數<br>
	 * ------ [String] ICOUNT<br>
	 * ------ [String] FIELDG：年金給付方式<br>
	 * ------ [String] FIELDX：年金每次金額<br>
	 * ------ [String] IOBJECT：保險對象<br>
	 * ------ [String] POLICYDESC：保單保全資料<br>
	 * 
	 * @return DoGetCoverage01OutputVO<br>
	 * --- List<Map> lstExpression：給付中間檔<br>
	 * ------[String] FirstKind 給付編號的第一層分類<br>
	 * ------[String] SecondKind 給付編號的第二層分類<br>
	 * ------[String] ExpressDESC 給付編號的中文名<br>
	 * ------[String] PayMode 付費模式<br>
	 * ------[BigDecimal] BegUnitPrice 給付起值<br>
	 * ------[BigDecimal] EndUnitPrice 給付迄值<br>
	 * ------[String] Description 給付起迄值<br>
	 * ------[BigDecimal] RelExpression RelExpression<br>
	 * ------[String] SortNo 給付編號<br>
	 * ------[BigDecimal] Mul_Unit 倍數<br>
	 * --- List<Map> lstCoverAgePrem：保障保費中間檔<br>
	 * ------[short] TheYear 攤列年度<br>
	 * ------[short] TheAge 攤列年齡<br>
	 * ------[BigDecimal] life 壽險保障<br>
	 * ------[BigDecimal] pa 意外保障<br>
	 * ------[BigDecimal] cl 癌症保障<br>
	 * ------[BigDecimal] ai 特定意外保障<br>
	 * ------[BigDecimal] ddb 重大疾病保障<br>
	 * ------[BigDecimal] premium 保費金額<br>
	 * ------[BigDecimal] repay 領回金額<br>
	 * ------[String] PolicyNo 保單號碼<br>
	 * ------[String] CustName 客戶姓名<br>
	 * ------[String] CustPolicy 保單號碼+客戶姓名<br>
	 * ------[String] PolicyID 保單ID(Key)<br>
	 * --- List<Map> CoverageTable
	 * ------[String] prem 繳費年期 <br>
	 * ------[short]  InsuredAge 保險年齡 <br>
	 * ------[String] InsAge 保險年齡 <br>
	 * ------[String] accu 累積年期 <br>
	 * ------[String] sex 性別 <br>
	 * ------[String] PayType 繳法 <br>
	 * ------[String] Kind 種類 <br>
	 * ------[String] Plan 計畫 <br>
	 * ------[String] Unit 單位 <br>
	 * ------[String] JobGrade 職業等級 <br>
	 * ------[String] IDay 保留 <br>
	 * ------[String] SocialSecurity 有無社保 <br>
	 * ------[String] ICount 保留 <br>
	 * ------[BigDecimal] CoverCaculUnit 保額計算單位 <br>
	 * ------[String] InsuredObject 投保對象 <br>
	 * ------[String] IObject 保險對象 <br>
	 * ------[String] CoverCaculUnitDesc 保額計算單位說 <br>
	 * ------[BigDecimal] CovRefPrem 繳費期滿的保障(滿期金) <br>
	 * ------[BigDecimal] CovRefAccu 保證期滿的保障 <br>
	 * ------[BigDecimal] CovRefWlife 終生的保障 <br>
	 * ------[BigDecimal] CovRefOther 其他用到的保障(看Coverelrepay的定義) <br>
	 * ------[BigDecimal] RepayPrem Repay繳費年期 <br>
	 * ------[BigDecimal] RepayAccu Repay累積年期 <br>
	 * ------[BigDecimal] RepayWlife Repay終生年期 <br>
	 * ------[BigDecimal] QuantityBase 保額\ <br>
	 * ------[BigDecimal] Quantity 數量 <br>
	 * ------[BigDecimal] IPremium 保費 <br>
	 * ------[short]  PremType 保費方式 <br>
	 * ------[BigDecimal] PremCaculUnit 保費計算基礎 <br>
	 * ------[String] RoundedMethod 保費取之第幾位 <br>
	 * ------[String] HighPremiumDiscount 高保費折扣率 <br>
	 * ------[BigDecimal] HighPremiumDiscountValue 高保費 <br>
	 * ------[String] HighPremiumDiscountID 高保費折扣 <br>
	 * ------[String] AllProductsID 商品ID <br>
	 * ------[String] CountType 保留 <br>
	 * ------[String] wlife 終身年齡 <br>
	 * ------[short]  MaxYear 這商品的最大年期或筆數 <br>
	 * ------[BigDecimal] Expmax 給付金額最大值 <br>
	 * ------[BigDecimal] Expmin 給付金額最小值 <br>
	 * ------[BigDecimal] Lifemax 壽險迄值 <br>
	 * ------[BigDecimal] Lifemin 壽險起值 <br>
	 * ------[BigDecimal] Pamax 意外險迄值 <br>
	 * ------[BigDecimal] Pamin 意外險起值 <br>
	 * ------[BigDecimal] Clmax 癌症保障迄值 <br>
	 * ------[BigDecimal] Clmin 癌症保障起值 <br>
	 * ------[BigDecimal] Aimax 特定意外壽險迄值 <br>
	 * ------[BigDecimal] Aimin 特定意外壽險起值 <br>
	 * ------[BigDecimal] Ddbmax 重大疾病保障迄值 <br>
	 * ------[BigDecimal] Ddbmin 重大疾病保障起值 <br>
	 * ------[short]  Premyear 繳費那一年(動態改變) <br>
	 * ------[String] InsCompany 保險公司名稱 <br>
	 * ------[String] ProdName 商品名稱 <br>
	 * ------[String] PolicyNo 保單號碼 <br>
	 * ------[Boolean] IsMain 是否主約 <br>
	 * ------[short]  floatyear 中間值(動態改變的年) <br>
	 * ------[short]  floatage 中間值(動態改變的年齡) <br>
	 * ------[BigDecimal] yearprem 年繳化保費 <br>
	 * ------[short]  investtype 是否投資年金型商品 <br>
	 * ------[String] FieldG 保證期間 <br>
	 * ------[String] FieldX 年金給付開始年齡 <br>
	 * ------[BigDecimal] RateCivcpo 匯率 <br>
	 * ------[short]  Test1  <br>
	 * ------[String] erreport  <br>
	 * ------[String] mutiexpmax  <br>
	 * ------[String] mutiexpmin  <br>
	 */
	public DoGetCoverage01OutputVO doGetCoverage01(DoGetCoverage01InputVO doGetCoverage01InputVO) throws JBranchException;
	
	/**呼叫保險資訊源doGetCoverage01：健檢攤列的各項資料(單筆)
	 * @param List<Map> lstInsDetail：客戶保單資訊<br>
	 * ------ [String] ALLPRODUCTSID：商品ID<br>
	 * ------ [String] PREMIUM：單項總保費(原幣)<br>
	 * ------ [String] KIND：屬性歸類(中文)<br>
	 * ------ [String] SOCIALSECURITY：有無社保<br>
	 * ------ [String] INSURED_AGE：投保年齡<br>
	 * ------ [String] INSURED_GENDER：性別<br>
	 * ------ [String] PAYTYPE：繳法<br>
	 * ------ [String] JOB_GRADE：職等級<br>
	 * ------ [String] PREMTERM：繳費年期<br>
	 * ------ [String] ACCUTERM：累積年期(保障年期)<br>
	 * ------ [String] UNIT：單位<br>
	 * ------ [String] PLAN：計畫<br>
	 * ------ [String] PRODQUANTITY：商品數量或保額(原幣)<br>
	 * ------ [String] IDAYS：旅行平安險購買天數<br>
	 * ------ [String] ICOUNT<br>
	 * ------ [String] FIELDG：年金給付方式<br>
	 * ------ [String] FIELDX：年金每次金額<br>
	 * ------ [String] IOBJECT：保險對象<br>
	 * ------ [String] POLICYDESC：保單保全資料<br>
	 * 
	 * @return DoGetCoverage01OutputVO<br>
	 * --- List<Map> lstExpression：給付中間檔<br>
	 * ------[String] FirstKind 給付編號的第一層分類<br>
	 * ------[String] SecondKind 給付編號的第二層分類<br>
	 * ------[String] ExpressDESC 給付編號的中文名<br>
	 * ------[String] PayMode 付費模式<br>
	 * ------[BigDecimal] BegUnitPrice 給付起值<br>
	 * ------[BigDecimal] EndUnitPrice 給付迄值<br>
	 * ------[String] Description 給付起迄值<br>
	 * ------[BigDecimal] RelExpression RelExpression<br>
	 * ------[String] SortNo 給付編號<br>
	 * ------[BigDecimal] Mul_Unit 倍數<br>
	 * --- List<Map> lstCoverAgePrem：保障保費中間檔<br>
	 * ------[short] TheYear 攤列年度<br>
	 * ------[short] TheAge 攤列年齡<br>
	 * ------[BigDecimal] life 壽險保障<br>
	 * ------[BigDecimal] pa 意外保障<br>
	 * ------[BigDecimal] cl 癌症保障<br>
	 * ------[BigDecimal] ai 特定意外保障<br>
	 * ------[BigDecimal] ddb 重大疾病保障<br>
	 * ------[BigDecimal] premium 保費金額<br>
	 * ------[BigDecimal] repay 領回金額<br>
	 * ------[String] PolicyNo 保單號碼<br>
	 * ------[String] CustName 客戶姓名<br>
	 * ------[String] CustPolicy 保單號碼+客戶姓名<br>
	 * ------[String] PolicyID 保單ID(Key)<br>
	 * --- List<Map> CoverageTable
	 * ------[String] prem 繳費年期 <br>
	 * ------[short]  InsuredAge 保險年齡 <br>
	 * ------[String] InsAge 保險年齡 <br>
	 * ------[String] accu 累積年期 <br>
	 * ------[String] sex 性別 <br>
	 * ------[String] PayType 繳法 <br>
	 * ------[String] Kind 種類 <br>
	 * ------[String] Plan 計畫 <br>
	 * ------[String] Unit 單位 <br>
	 * ------[String] JobGrade 職業等級 <br>
	 * ------[String] IDay 保留 <br>
	 * ------[String] SocialSecurity 有無社保 <br>
	 * ------[String] ICount 保留 <br>
	 * ------[BigDecimal] CoverCaculUnit 保額計算單位 <br>
	 * ------[String] InsuredObject 投保對象 <br>
	 * ------[String] IObject 保險對象 <br>
	 * ------[String] CoverCaculUnitDesc 保額計算單位說 <br>
	 * ------[BigDecimal] CovRefPrem 繳費期滿的保障(滿期金) <br>
	 * ------[BigDecimal] CovRefAccu 保證期滿的保障 <br>
	 * ------[BigDecimal] CovRefWlife 終生的保障 <br>
	 * ------[BigDecimal] CovRefOther 其他用到的保障(看Coverelrepay的定義) <br>
	 * ------[BigDecimal] RepayPrem Repay繳費年期 <br>
	 * ------[BigDecimal] RepayAccu Repay累積年期 <br>
	 * ------[BigDecimal] RepayWlife Repay終生年期 <br>
	 * ------[BigDecimal] QuantityBase 保額\ <br>
	 * ------[BigDecimal] Quantity 數量 <br>
	 * ------[BigDecimal] IPremium 保費 <br>
	 * ------[short]  PremType 保費方式 <br>
	 * ------[BigDecimal] PremCaculUnit 保費計算基礎 <br>
	 * ------[String] RoundedMethod 保費取之第幾位 <br>
	 * ------[String] HighPremiumDiscount 高保費折扣率 <br>
	 * ------[BigDecimal] HighPremiumDiscountValue 高保費 <br>
	 * ------[String] HighPremiumDiscountID 高保費折扣 <br>
	 * ------[String] AllProductsID 商品ID <br>
	 * ------[String] CountType 保留 <br>
	 * ------[String] wlife 終身年齡 <br>
	 * ------[short]  MaxYear 這商品的最大年期或筆數 <br>
	 * ------[BigDecimal] Expmax 給付金額最大值 <br>
	 * ------[BigDecimal] Expmin 給付金額最小值 <br>
	 * ------[BigDecimal] Lifemax 壽險迄值 <br>
	 * ------[BigDecimal] Lifemin 壽險起值 <br>
	 * ------[BigDecimal] Pamax 意外險迄值 <br>
	 * ------[BigDecimal] Pamin 意外險起值 <br>
	 * ------[BigDecimal] Clmax 癌症保障迄值 <br>
	 * ------[BigDecimal] Clmin 癌症保障起值 <br>
	 * ------[BigDecimal] Aimax 特定意外壽險迄值 <br>
	 * ------[BigDecimal] Aimin 特定意外壽險起值 <br>
	 * ------[BigDecimal] Ddbmax 重大疾病保障迄值 <br>
	 * ------[BigDecimal] Ddbmin 重大疾病保障起值 <br>
	 * ------[short]  Premyear 繳費那一年(動態改變) <br>
	 * ------[String] InsCompany 保險公司名稱 <br>
	 * ------[String] ProdName 商品名稱 <br>
	 * ------[String] PolicyNo 保單號碼 <br>
	 * ------[Boolean] IsMain 是否主約 <br>
	 * ------[short]  floatyear 中間值(動態改變的年) <br>
	 * ------[short]  floatage 中間值(動態改變的年齡) <br>
	 * ------[BigDecimal] yearprem 年繳化保費 <br>
	 * ------[short]  investtype 是否投資年金型商品 <br>
	 * ------[String] FieldG 保證期間 <br>
	 * ------[String] FieldX 年金給付開始年齡 <br>
	 * ------[BigDecimal] RateCivcpo 匯率 <br>
	 * ------[short]  Test1  <br>
	 * ------[String] erreport  <br>
	 * ------[String] mutiexpmax  <br>
	 * ------[String] mutiexpmin  <br>
	 */
	public DoGetCoverage01OutputVO doGetCoverage01(Map lstInsDetail) throws JBranchException;
}
