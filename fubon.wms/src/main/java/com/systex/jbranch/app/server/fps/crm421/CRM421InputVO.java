package com.systex.jbranch.app.server.fps.crm421;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;
import java.util.Map;

public class CRM421InputVO extends PagingInputVO{
	private String ao_code;
	private char[] bargain_status;
	private String cust_id;
	private String bra_nbr;
	private String con_degree;
	private String obu_yn;
	
	private Date apply_sDate;
	private Date apply_eDate;
	private Date trade_date;
	private String dmt_stock;
	private String frn_stock;
	private String dmt_bond;
	private String frn_bond;
	private String dmt_balanced;
	private String frn_balanced;
	private String buy_hk_mrk;
	private String sell_hk_mrk;
	private String buy_us_mrk;
	private String sell_us_mrk;
	private String buy_uk_mrk;
	private String sell_uk_mrk;	
	private String buy_jp_mrk;
	private String sell_jp_mrk;	
	private Date brg_sdate;
	private Date brg_edate;
	private String brg_reason;
	private String highest_auth_lv;
	private String highest_auth_lv_1;
	private String highest_auth_lv_2;
	private String applyPeriod_1;
	private String applyPeriod_2;
	private String prod_type;
	private String apply_cat;
	private String seq;
	
	private String apply_type;
	private String apply_status;
	private String prod_id;
	private String prod_name;
	private String trust_curr;
	private String purchase_amt;
	private String discount_type;
	private BigDecimal fee_rate;
	private BigDecimal fee_discount;
	private String fee;
	private String entrust_unit;
	private String entrust_amt;
	private String terminateAndApply;
	private List<Map<String, Object>> list;
	
	private BigDecimal defaultFeeRate;
	private List applySeqList;
	private String trustCurrType;
	
	private String terminateReason;
	private String terminateSEQ;
	
	private String trustTS;				//M:金錢信託 S:特金交易

	//金錢信託 定期定額相關
	private Boolean isTrueBargain = false;  //判斷真假議價 (選擇申請議價為真，其餘走假議價不覆核可套表)
	private String fakeID;  //若畫面選直接議價 會把客戶ID存入




	public Boolean getIsTrueBargain() {
		return isTrueBargain;
	}
	public void setIsTrueBargain(Boolean isTrueBargain) {
		this.isTrueBargain = isTrueBargain;
	}
	public String getFakeID() {
		return fakeID;
	}
	public void setFakeID(String fakeID) {
		this.fakeID = fakeID;
	}
	public String getTrustTS() {
		return trustTS;
	}
	public void setTrustTS(String trustTS) {
		this.trustTS = trustTS;
	}
	public String getApply_status() {
		return apply_status;
	}
	public void setApply_status(String apply_status) {
		this.apply_status = apply_status;
	}
	public String getAo_code() {
		return ao_code;
	}
	public void setAo_code(String ao_code) {
		this.ao_code = ao_code;
	}
	public String getHighest_auth_lv_1() {
		return highest_auth_lv_1;
	}
	public void setHighest_auth_lv_1(String highest_auth_lv_1) {
		this.highest_auth_lv_1 = highest_auth_lv_1;
	}
	public String getHighest_auth_lv_2() {
		return highest_auth_lv_2;
	}
	public void setHighest_auth_lv_2(String highest_auth_lv_2) {
		this.highest_auth_lv_2 = highest_auth_lv_2;
	}
	public String getTerminateSEQ() {
		return terminateSEQ;
	}
	public void setTerminateSEQ(String terminateSEQ) {
		this.terminateSEQ = terminateSEQ;
	}
	public String getTerminateReason() {
		return terminateReason;
	}
	public void setTerminateReason(String terminateReason) {
		this.terminateReason = terminateReason;
	}
	public String getTrustCurrType() {
		return trustCurrType;
	}
	public void setTrustCurrType(String trustCurrType) {
		this.trustCurrType = trustCurrType;
	}
	public List getApplySeqList() {
		return applySeqList;
	}
	public void setApplySeqList(List applySeqList) {
		this.applySeqList = applySeqList;
	}
	public BigDecimal getDefaultFeeRate() {
		return defaultFeeRate;
	}
	public void setDefaultFeeRate(BigDecimal defaultFeeRate) {
		this.defaultFeeRate = defaultFeeRate;
	}
	public char[] getBargain_status() {
		return bargain_status;
	}
	public void setBargain_status(char[] bargain_status) {
		this.bargain_status = bargain_status;
	}
	public String getCust_id() {
		return cust_id;
	}
	public void setCust_id(String cust_id) {
		this.cust_id = cust_id;
	}
	public String getCon_degree() {
		return con_degree;
	}
	public void setCon_degree(String con_degree) {
		this.con_degree = con_degree;
	}
	public Date getApply_sDate() {
		return apply_sDate;
	}
	public void setApply_sDate(Date apply_sDate) {
		this.apply_sDate = apply_sDate;
	}
	public Date getApply_eDate() {
		return apply_eDate;
	}
	public void setApply_eDate(Date apply_eDate) {
		this.apply_eDate = apply_eDate;
	}
	public String getDmt_stock() {
		return dmt_stock;
	}
	public void setDmt_stock(String dmt_stock) {
		this.dmt_stock = dmt_stock;
	}
	public String getFrn_stock() {
		return frn_stock;
	}
	public void setFrn_stock(String frn_stock) {
		this.frn_stock = frn_stock;
	}
	public String getDmt_bond() {
		return dmt_bond;
	}
	public void setDmt_bond(String dmt_bond) {
		this.dmt_bond = dmt_bond;
	}
	public String getFrn_bond() {
		return frn_bond;
	}
	public void setFrn_bond(String frn_bond) {
		this.frn_bond = frn_bond;
	}
	public String getDmt_balanced() {
		return dmt_balanced;
	}
	public void setDmt_balanced(String dmt_balanced) {
		this.dmt_balanced = dmt_balanced;
	}
	public String getFrn_balanced() {
		return frn_balanced;
	}
	public void setFrn_balanced(String frn_balanced) {
		this.frn_balanced = frn_balanced;
	}
	public String getBuy_hk_mrk() {
		return buy_hk_mrk;
	}
	public void setBuy_hk_mrk(String buy_hk_mrk) {
		this.buy_hk_mrk = buy_hk_mrk;
	}
	public String getSell_hk_mrk() {
		return sell_hk_mrk;
	}
	public void setSell_hk_mrk(String sell_hk_mrk) {
		this.sell_hk_mrk = sell_hk_mrk;
	}
	public String getBuy_us_mrk() {
		return buy_us_mrk;
	}
	public void setBuy_us_mrk(String buy_us_mrk) {
		this.buy_us_mrk = buy_us_mrk;
	}
	public String getSell_us_mrk() {
		return sell_us_mrk;
	}
	public void setSell_us_mrk(String sell_us_mrk) {
		this.sell_us_mrk = sell_us_mrk;
	}
	public String getBuy_uk_mrk() {
		return buy_uk_mrk;
	}
	public void setBuy_uk_mrk(String buy_uk_mrk) {
		this.buy_uk_mrk = buy_uk_mrk;
	}
	public String getSell_uk_mrk() {
		return sell_uk_mrk;
	}
	public void setSell_uk_mrk(String sell_uk_mrk) {
		this.sell_uk_mrk = sell_uk_mrk;
	}
	public String getBuy_jp_mrk() {
		return buy_jp_mrk;
	}
	public void setBuy_jp_mrk(String buy_jp_mrk) {
		this.buy_jp_mrk = buy_jp_mrk;
	}
	public String getSell_jp_mrk() {
		return sell_jp_mrk;
	}
	public void setSell_jp_mrk(String sell_jp_mrk) {
		this.sell_jp_mrk = sell_jp_mrk;
	}
	public Date getBrg_sdate() {
		return brg_sdate;
	}
	public void setBrg_sdate(Date brg_sdate) {
		this.brg_sdate = brg_sdate;
	}
	public Date getBrg_edate() {
		return brg_edate;
	}
	public void setBrg_edate(Date brg_edate) {
		this.brg_edate = brg_edate;
	}
	public String getBrg_reason() {
		return brg_reason;
	}
	public void setBrg_reason(String brg_reason) {
		this.brg_reason = brg_reason;
	}
	public String getApplyPeriod_1() {
		return applyPeriod_1;
	}
	public void setApplyPeriod_1(String applyPeriod_1) {
		this.applyPeriod_1 = applyPeriod_1;
	}
	public String getApplyPeriod_2() {
		return applyPeriod_2;
	}
	public void setApplyPeriod_2(String applyPeriod_2) {
		this.applyPeriod_2 = applyPeriod_2;
	}
	public String getHighest_auth_lv() {
		return highest_auth_lv;
	}
	public void setHighest_auth_lv(String highest_auth_lv) {
		this.highest_auth_lv = highest_auth_lv;
	}
	public String getProd_type() {
		return prod_type;
	}
	public void setProd_type(String prod_type) {
		this.prod_type = prod_type;
	}
	public String getApply_cat() {
		return apply_cat;
	}
	public void setApply_cat(String apply_cat) {
		this.apply_cat = apply_cat;
	}
	public String getSeq() {
		return seq;
	}
	public void setSeq(String seq) {
		this.seq = seq;
	}
	public String getApply_type() {
		return apply_type;
	}
	public void setApply_type(String apply_type) {
		this.apply_type = apply_type;
	}
	public String getProd_id() {
		return prod_id;
	}
	public void setProd_id(String prod_id) {
		this.prod_id = prod_id;
	}
	public String getTrust_curr() {
		return trust_curr;
	}
	public void setTrust_curr(String trust_curr) {
		this.trust_curr = trust_curr;
	}
	public String getPurchase_amt() {
		return purchase_amt;
	}
	public void setPurchase_amt(String purchase_amt) {
		this.purchase_amt = purchase_amt;
	}
	public String getDiscount_type() {
		return discount_type;
	}
	public void setDiscount_type(String discount_type) {
		this.discount_type = discount_type;
	}
	public BigDecimal getFee_rate() {
		return fee_rate;
	}
	public void setFee_rate(BigDecimal fee_rate) {
		this.fee_rate = fee_rate;
	}
	public BigDecimal getFee_discount() {
		return fee_discount;
	}
	public void setFee_discount(BigDecimal fee_discount) {
		this.fee_discount = fee_discount;
	}
	public String getFee() {
		return fee;
	}
	public void setFee(String fee) {
		this.fee = fee;
	}
	public String getEntrust_unit() {
		return entrust_unit;
	}
	public void setEntrust_unit(String entrust_unit) {
		this.entrust_unit = entrust_unit;
	}
	public String getEntrust_amt() {
		return entrust_amt;
	}
	public void setEntrust_amt(String entrust_amt) {
		this.entrust_amt = entrust_amt;
	}
	public String getProd_name() {
		return prod_name;
	}
	public void setProd_name(String prod_name) {
		this.prod_name = prod_name;
	}
	public Date getTrade_date() {
		return trade_date;
	}
	public void setTrade_date(Date trade_date) {
		this.trade_date = trade_date;
	}
	public String getTerminateAndApply() {
		return terminateAndApply;
	}
	public void setTerminateAndApply(String terminateAndApply) {
		this.terminateAndApply = terminateAndApply;
	}
	public String getBra_nbr() {
		return bra_nbr;
	}
	public void setBra_nbr(String bra_nbr) {
		this.bra_nbr = bra_nbr;
	}
	public String getObu_yn() {
		return obu_yn;
	}
	public void setObu_yn(String obu_yn) {
		this.obu_yn = obu_yn;
	}
	public List<Map<String, Object>> getList() {
		return list;
	}
	public void setList(List<Map<String, Object>> list) {
		this.list = list;
	}
	

}
