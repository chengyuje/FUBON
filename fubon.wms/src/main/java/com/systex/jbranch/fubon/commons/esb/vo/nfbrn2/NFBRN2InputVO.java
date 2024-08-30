package com.systex.jbranch.fubon.commons.esb.vo.nfbrn2;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by SebastianWu on 2016/10/14.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class NFBRN2InputVO {
    @XmlElement
	private String TRADE_DATE;              //交易日期
    @XmlElement
	private String EFF_DATE;                //生效日期
    @XmlElement
	private String BATCH_SEQ;				//批號
    @XmlElement
	private String CUST_ID;					//客戶ID
    @XmlElement
	private String BRANCH_NBR;				//分行別
    @XmlElement
	private String PAY_TYPE;				//繳費方式
    @XmlElement
	private String FEE_DEBIT_ACCT;			//繳款帳號
    @XmlElement
	private String NARRATOR_ID;				//推薦人
    @XmlElement
	private String EMP_ID;					//鍵機人員
    @XmlElement
	private String CONFIRM;					//確認碼
    @XmlElement
	private String MINORITY_YN;				//未成年
    @XmlElement
	private String PROSPECTUS_TYPE;			//公開說明書交付方式
    @XmlElement
	private String FILLER_1;				//預留欄位
    @XmlElement
	private String CERTIFICATE_ID_1;		//憑證號碼1
    @XmlElement
	private String TRUST_ACCT_1;			//信託帳號
    @XmlElement
	private String OUT_PROD_ID_1;			//原始基金 轉出商品ID1
    @XmlElement
	private String OUT_UNIT_1;				//單位數1
    @XmlElement
	private String TRANSFER_TYPE_1;			//轉換方式1
    @XmlElement
	private String IN_PROD_ID_1_1;			//轉入基金1
    @XmlElement
	private String IN_UNIT_1_1;				//轉入單位數1
    @XmlElement
	private String IN_PROD_RISK_LV_1_1;		//轉入基金1 風險等級
    @XmlElement
	private String IN_PROD_ID_1_2;			//轉入基金2
    @XmlElement
	private String IN_UNIT_1_2;				//轉入單位數2
    @XmlElement
	private String IN_PROD_RISK_LV_1_2;		//轉入基金2 風險等級
    @XmlElement
	private String IN_PROD_ID_1_3;			//轉入基金3
    @XmlElement
	private String IN_UNIT_1_3;				//轉入單位數3
    @XmlElement
	private String IN_PROD_RISK_LV_1_3;		//轉入基金3 風險等級
    @XmlElement
	private String CERTIFICATE_ID_2;		//憑證號碼2
    @XmlElement
	private String TRUST_ACCT_2;			//信託帳號
    @XmlElement
	private String OUT_PROD_ID_2;			//原始基金 轉出商品ID2
    @XmlElement
	private String OUT_UNIT_2;				//單位數2    
    @XmlElement
	private String TRANSFER_TYPE_2;			//轉換方式2
    @XmlElement
	private String IN_PROD_ID_2_1;			//轉入基金1
    @XmlElement
	private String IN_UNIT_2_1;				//轉入單位數1
    @XmlElement
	private String IN_PROD_RISK_LV_2_1;		//轉入基金1 風險等級
    @XmlElement
	private String IN_PROD_ID_2_2;			//轉入基金2
    @XmlElement
	private String IN_UNIT_2_2;				//轉入單位數2
    @XmlElement
	private String IN_PROD_RISK_LV_2_2;		//轉入基金2 風險等級
    @XmlElement
	private String IN_PROD_ID_2_3;			//轉入基金3
    @XmlElement
	private String IN_UNIT_2_3;				//轉入單位數3
    @XmlElement
	private String IN_PROD_RISK_LV_2_3;		//轉入基金3 風險等級
    
    
	public String getTRADE_DATE() {
		return TRADE_DATE;
	}
	public void setTRADE_DATE(String trade_date) {
		TRADE_DATE = trade_date;
	}
	public String getEFF_DATE() {
		return EFF_DATE;
	}
	public void setEFF_DATE(String eff_date) {
		EFF_DATE = eff_date;
	}
	public String getBATCH_SEQ() {
		return BATCH_SEQ;
	}
	public void setBATCH_SEQ(String batch_seq) {
		BATCH_SEQ = batch_seq;
	}
	public String getCUST_ID() {
		return CUST_ID;
	}
	public void setCUST_ID(String cust_id) {
		CUST_ID = cust_id;
	}
	public String getBRANCH_NBR() {
		return BRANCH_NBR;
	}
	public void setBRANCH_NBR(String branch_nbr) {
		BRANCH_NBR = branch_nbr;
	}
	public String getPAY_TYPE() {
		return PAY_TYPE;
	}
	public void setPAY_TYPE(String pay_type) {
		PAY_TYPE = pay_type;
	}
	public String getFEE_DEBIT_ACCT() {
		return FEE_DEBIT_ACCT;
	}
	public void setFEE_DEBIT_ACCT(String fee_debit_acct) {
		FEE_DEBIT_ACCT = fee_debit_acct;
	}
	public String getNARRATOR_ID() {
		return NARRATOR_ID;
	}
	public void setNARRATOR_ID(String narrator_id) {
		NARRATOR_ID = narrator_id;
	}
	public String getEMP_ID() {
		return EMP_ID;
	}
	public void setEMP_ID(String emp_id) {
		EMP_ID = emp_id;
	}
	public String getCONFIRM() {
		return CONFIRM;
	}
	public void setCONFIRM(String confirm) {
		CONFIRM = confirm;
	}
	public String getMINORITY_YN() {
		return MINORITY_YN;
	}
	public void setMINORITY_YN(String minority_yn) {
		MINORITY_YN = minority_yn;
	}
	public String getPROSPECTUS_TYPE() {
		return PROSPECTUS_TYPE;
	}
	public void setPROSPECTUS_TYPE(String prospectus_type) {
		PROSPECTUS_TYPE = prospectus_type;
	}
	public String getFILLER_1() {
		return FILLER_1;
	}
	public void setFILLER_1(String filler_1) {
		FILLER_1 = filler_1;
	}
	public String getCERTIFICATE_ID_1() {
		return CERTIFICATE_ID_1;
	}
	public void setCERTIFICATE_ID_1(String certificate_id_1) {
		CERTIFICATE_ID_1 = certificate_id_1;
	}
	public String getTRUST_ACCT_1() {
		return TRUST_ACCT_1;
	}
	public void setTRUST_ACCT_1(String trust_acct_1) {
		TRUST_ACCT_1 = trust_acct_1;
	}
	public String getOUT_PROD_ID_1() {
		return OUT_PROD_ID_1;
	}
	public void setOUT_PROD_ID_1(String out_prod_id_1) {
		OUT_PROD_ID_1 = out_prod_id_1;
	}
	public String getOUT_UNIT_1() {
		return OUT_UNIT_1;
	}
	public void setOUT_UNIT_1(String out_unit_1) {
		OUT_UNIT_1 = out_unit_1;
	}
	public String getTRANSFER_TYPE_1() {
		return TRANSFER_TYPE_1;
	}
	public void setTRANSFER_TYPE_1(String transfer_type_1) {
		TRANSFER_TYPE_1 = transfer_type_1;
	}
	public String getIN_PROD_ID_1_1() {
		return IN_PROD_ID_1_1;
	}
	public void setIN_PROD_ID_1_1(String in_prod_id_1_1) {
		IN_PROD_ID_1_1 = in_prod_id_1_1;
	}
	public String getIN_UNIT_1_1() {
		return IN_UNIT_1_1;
	}
	public void setIN_UNIT_1_1(String in_unit_1_1) {
		IN_UNIT_1_1 = in_unit_1_1;
	}
	public String getIN_PROD_RISK_LV_1_1() {
		return IN_PROD_RISK_LV_1_1;
	}
	public void setIN_PROD_RISK_LV_1_1(String in_prod_risk_lv_1_1) {
		IN_PROD_RISK_LV_1_1 = in_prod_risk_lv_1_1;
	}
	public String getIN_PROD_ID_1_2() {
		return IN_PROD_ID_1_2;
	}
	public void setIN_PROD_ID_1_2(String in_prod_id_1_2) {
		IN_PROD_ID_1_2 = in_prod_id_1_2;
	}
	public String getIN_UNIT_1_2() {
		return IN_UNIT_1_2;
	}
	public void setIN_UNIT_1_2(String in_unit_1_2) {
		IN_UNIT_1_2 = in_unit_1_2;
	}
	public String getIN_PROD_RISK_LV_1_2() {
		return IN_PROD_RISK_LV_1_2;
	}
	public void setIN_PROD_RISK_LV_1_2(String in_prod_risk_lv_1_2) {
		IN_PROD_RISK_LV_1_2 = in_prod_risk_lv_1_2;
	}
	public String getIN_PROD_ID_1_3() {
		return IN_PROD_ID_1_3;
	}
	public void setIN_PROD_ID_1_3(String in_prod_id_1_3) {
		IN_PROD_ID_1_3 = in_prod_id_1_3;
	}
	public String getIN_UNIT_1_3() {
		return IN_UNIT_1_3;
	}
	public void setIN_UNIT_1_3(String in_unit_1_3) {
		IN_UNIT_1_3 = in_unit_1_3;
	}
	public String getIN_PROD_RISK_LV_1_3() {
		return IN_PROD_RISK_LV_1_3;
	}
	public void setIN_PROD_RISK_LV_1_3(String in_prod_risk_lv_1_3) {
		IN_PROD_RISK_LV_1_3 = in_prod_risk_lv_1_3;
	}	
	public String getCERTIFICATE_ID_2() {
		return CERTIFICATE_ID_2;
	}
	public void setCERTIFICATE_ID_2(String certificate_id_2) {
		CERTIFICATE_ID_2 = certificate_id_2;
	}
	public String getTRUST_ACCT_2() {
		return TRUST_ACCT_2;
	}
	public void setTRUST_ACCT_2(String trust_acct_2) {
		TRUST_ACCT_2 = trust_acct_2;
	}
	public String getOUT_PROD_ID_2() {
		return OUT_PROD_ID_2;
	}
	public void setOUT_PROD_ID_2(String out_prod_id_2) {
		OUT_PROD_ID_2 = out_prod_id_2;
	}
	public String getOUT_UNIT_2() {
		return OUT_UNIT_2;
	}
	public void setOUT_UNIT_2(String out_unit_2) {
		OUT_UNIT_2 = out_unit_2;
	}
	public String getTRANSFER_TYPE_2() {
		return TRANSFER_TYPE_2;
	}
	public void setTRANSFER_TYPE_2(String transfer_type_2) {
		TRANSFER_TYPE_2 = transfer_type_2;
	}
	public String getIN_PROD_ID_2_1() {
		return IN_PROD_ID_2_1;
	}
	public void setIN_PROD_ID_2_1(String in_prod_id_2_1) {
		IN_PROD_ID_2_1 = in_prod_id_2_1;
	}
	public String getIN_UNIT_2_1() {
		return IN_UNIT_2_1;
	}
	public void setIN_UNIT_2_1(String in_unit_2_1) {
		IN_UNIT_2_1 = in_unit_2_1;
	}
	public String getIN_PROD_RISK_LV_2_1() {
		return IN_PROD_RISK_LV_2_1;
	}
	public void setIN_PROD_RISK_LV_2_1(String in_prod_risk_lv_2_1) {
		IN_PROD_RISK_LV_2_1 = in_prod_risk_lv_2_1;
	}
	public String getIN_PROD_ID_2_2() {
		return IN_PROD_ID_2_2;
	}
	public void setIN_PROD_ID_2_2(String in_prod_id_2_2) {
		IN_PROD_ID_2_2 = in_prod_id_2_2;
	}
	public String getIN_UNIT_2_2() {
		return IN_UNIT_2_2;
	}
	public void setIN_UNIT_2_2(String in_unit_2_2) {
		IN_UNIT_2_2 = in_unit_2_2;
	}
	public String getIN_PROD_RISK_LV_2_2() {
		return IN_PROD_RISK_LV_2_2;
	}
	public void setIN_PROD_RISK_LV_2_2(String in_prod_risk_lv_2_2) {
		IN_PROD_RISK_LV_2_2 = in_prod_risk_lv_2_2;
	}
	public String getIN_PROD_ID_2_3() {
		return IN_PROD_ID_2_3;
	}
	public void setIN_PROD_ID_2_3(String in_prod_id_2_3) {
		IN_PROD_ID_2_3 = in_prod_id_2_3;
	}
	public String getIN_UNIT_2_3() {
		return IN_UNIT_2_3;
	}
	public void setIN_UNIT_2_3(String in_unit_2_3) {
		IN_UNIT_2_3 = in_unit_2_3;
	}
	public String getIN_PROD_RISK_LV_2_3() {
		return IN_PROD_RISK_LV_2_3;
	}
	public void setIN_PROD_RISK_LV_2_3(String in_prod_risk_lv_2_3) {
		IN_PROD_RISK_LV_2_3 = in_prod_risk_lv_2_3;
	}
}
