package com.systex.jbranch.app.server.fps.crm210;

import java.sql.Date;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class CRM210InputVO extends PagingInputVO {

	private String cust_02;
	private String cust_04;
	private String cust_06;
	private String cust_03;
	private Date cust_05_sDate;
	private Date cust_05_eDate;
	private String cust_09;
	private String cust_07;
	private String cust_08;
	private String cust_10;
	private String cust_11;
	private String cust_12;
	private String cust_13;
	private String cust_14;
	private String cust_15;
	private String cust_18;
	private String amt_total_s;
	private String amt_total_e;
	private String amt_19;
	private String amt_01_s;
	private String amt_01_e;
	private String amt_02_s;
	private String amt_02_e;
	private String amt_13_s;
	private String amt_13_e;
	private String amt_05_s;
	private String amt_05_e;
	private String amt_14_s;
	private String amt_14_e;
	private String amt_09_s;
	private String amt_09_e;
	private String amt_15_s;
	private String amt_15_e;
	private String amt_10_s;
	private String amt_10_e;
	private String amt_16_s;
	private String amt_16_e;
	private String amt_11_s;
	private String amt_11_e;
	private String amt_17_s;
	private String amt_17_e;
	private String amt_12_s;
	private String amt_12_e;
	private String amt_18_s;
	private String amt_18_e;
	private String amt_25_s;
	private String amt_25_e;
	private String amt_26_s;
	private String amt_26_e;
	private String atr_01;
	private String atr_02;
	private Date atr_03_sDate;
	private Date atr_03_eDate;
	private String manage_01;
	private String manage_02;
	private Date manage_03_sDate;
	private Date manage_03_eDate;
	private String manage_04;
	private String manage_05_Date;
	private String manage_06_fee_s;
	private String manage_06_fee_e;
	private String roa_s;
	private String roa_e;
	private String campaign_ao_code;
	private Date campaign_date;
	
	private String actPrdNums;		// 活躍數
	private String IS_ACTUAL;		// 是否實動戶
	private String cust_w8ben;      // W-8BEN(W-8BEN-E)
	private String IS_VOC;			// 是否有承租保管箱
	private String hnwc_flag;		// 高資產客戶註記

	public String getIS_VOC() {
		return IS_VOC;
	}

	public void setIS_VOC(String iS_VOC) {
		IS_VOC = iS_VOC;
	}

	public String getCust_w8ben() {
		return cust_w8ben;
	}

	public void setCust_w8ben(String cust_w8ben) {
		this.cust_w8ben = cust_w8ben;
	}

	public String getIS_ACTUAL() {
		return IS_ACTUAL;
	}

	public void setIS_ACTUAL(String iS_ACTUAL) {
		IS_ACTUAL = iS_ACTUAL;
	}

	public String getActPrdNums() {
		return actPrdNums;
	}

	public void setActPrdNums(String actPrdNums) {
		this.actPrdNums = actPrdNums;
	}

	public String getCust_02() {
		return cust_02;
	}

	public void setCust_02(String cust_02) {
		this.cust_02 = cust_02;
	}

	public String getCust_04() {
		return cust_04;
	}

	public void setCust_04(String cust_04) {
		this.cust_04 = cust_04;
	}

	public String getCust_06() {
		return cust_06;
	}

	public void setCust_06(String cust_06) {
		this.cust_06 = cust_06;
	}

	public String getCust_03() {
		return cust_03;
	}

	public void setCust_03(String cust_03) {
		this.cust_03 = cust_03;
	}

	public Date getCust_05_sDate() {
		return cust_05_sDate;
	}

	public void setCust_05_sDate(Date cust_05_sDate) {
		this.cust_05_sDate = cust_05_sDate;
	}

	public Date getCust_05_eDate() {
		return cust_05_eDate;
	}

	public void setCust_05_eDate(Date cust_05_eDate) {
		this.cust_05_eDate = cust_05_eDate;
	}

	public String getCust_09() {
		return cust_09;
	}

	public void setCust_09(String cust_09) {
		this.cust_09 = cust_09;
	}

	public String getCust_07() {
		return cust_07;
	}

	public void setCust_07(String cust_07) {
		this.cust_07 = cust_07;
	}

	public String getCust_08() {
		return cust_08;
	}

	public void setCust_08(String cust_08) {
		this.cust_08 = cust_08;
	}

	public String getCust_10() {
		return cust_10;
	}

	public void setCust_10(String cust_10) {
		this.cust_10 = cust_10;
	}

	public String getCust_11() {
		return cust_11;
	}

	public void setCust_11(String cust_11) {
		this.cust_11 = cust_11;
	}

	public String getCust_12() {
		return cust_12;
	}

	public void setCust_12(String cust_12) {
		this.cust_12 = cust_12;
	}

	public String getCust_13() {
		return cust_13;
	}

	public void setCust_13(String cust_13) {
		this.cust_13 = cust_13;
	}

	public String getCust_14() {
		return cust_14;
	}

	public void setCust_14(String cust_14) {
		this.cust_14 = cust_14;
	}

	public String getCust_15() {
		return cust_15;
	}

	public void setCust_15(String cust_15) {
		this.cust_15 = cust_15;
	}

	public String getCust_18() {
		return cust_18;
	}

	public void setCust_18(String cust_18) {
		this.cust_18 = cust_18;
	}

	public String getAmt_total_s() {
		return amt_total_s;
	}

	public void setAmt_total_s(String amt_total_s) {
		this.amt_total_s = amt_total_s;
	}

	public String getAmt_total_e() {
		return amt_total_e;
	}

	public void setAmt_total_e(String amt_total_e) {
		this.amt_total_e = amt_total_e;
	}

	public String getAmt_19() {
		return amt_19;
	}

	public void setAmt_19(String amt_19) {
		this.amt_19 = amt_19;
	}

	public String getAmt_01_s() {
		return amt_01_s;
	}

	public void setAmt_01_s(String amt_01_s) {
		this.amt_01_s = amt_01_s;
	}

	public String getAmt_01_e() {
		return amt_01_e;
	}

	public void setAmt_01_e(String amt_01_e) {
		this.amt_01_e = amt_01_e;
	}

	public String getAmt_02_s() {
		return amt_02_s;
	}

	public void setAmt_02_s(String amt_02_s) {
		this.amt_02_s = amt_02_s;
	}

	public String getAmt_02_e() {
		return amt_02_e;
	}

	public void setAmt_02_e(String amt_02_e) {
		this.amt_02_e = amt_02_e;
	}

	public String getAmt_13_s() {
		return amt_13_s;
	}

	public void setAmt_13_s(String amt_13_s) {
		this.amt_13_s = amt_13_s;
	}

	public String getAmt_13_e() {
		return amt_13_e;
	}

	public void setAmt_13_e(String amt_13_e) {
		this.amt_13_e = amt_13_e;
	}

	public String getAmt_05_s() {
		return amt_05_s;
	}

	public void setAmt_05_s(String amt_05_s) {
		this.amt_05_s = amt_05_s;
	}

	public String getAmt_05_e() {
		return amt_05_e;
	}

	public void setAmt_05_e(String amt_05_e) {
		this.amt_05_e = amt_05_e;
	}

	public String getAmt_14_s() {
		return amt_14_s;
	}

	public void setAmt_14_s(String amt_14_s) {
		this.amt_14_s = amt_14_s;
	}

	public String getAmt_14_e() {
		return amt_14_e;
	}

	public void setAmt_14_e(String amt_14_e) {
		this.amt_14_e = amt_14_e;
	}

	public String getAmt_09_s() {
		return amt_09_s;
	}

	public void setAmt_09_s(String amt_09_s) {
		this.amt_09_s = amt_09_s;
	}

	public String getAmt_09_e() {
		return amt_09_e;
	}

	public void setAmt_09_e(String amt_09_e) {
		this.amt_09_e = amt_09_e;
	}

	public String getAmt_15_s() {
		return amt_15_s;
	}

	public void setAmt_15_s(String amt_15_s) {
		this.amt_15_s = amt_15_s;
	}

	public String getAmt_15_e() {
		return amt_15_e;
	}

	public void setAmt_15_e(String amt_15_e) {
		this.amt_15_e = amt_15_e;
	}

	public String getAmt_10_s() {
		return amt_10_s;
	}

	public void setAmt_10_s(String amt_10_s) {
		this.amt_10_s = amt_10_s;
	}

	public String getAmt_10_e() {
		return amt_10_e;
	}

	public void setAmt_10_e(String amt_10_e) {
		this.amt_10_e = amt_10_e;
	}

	public String getAmt_16_s() {
		return amt_16_s;
	}

	public void setAmt_16_s(String amt_16_s) {
		this.amt_16_s = amt_16_s;
	}

	public String getAmt_16_e() {
		return amt_16_e;
	}

	public void setAmt_16_e(String amt_16_e) {
		this.amt_16_e = amt_16_e;
	}

	public String getAmt_11_s() {
		return amt_11_s;
	}

	public void setAmt_11_s(String amt_11_s) {
		this.amt_11_s = amt_11_s;
	}

	public String getAmt_11_e() {
		return amt_11_e;
	}

	public void setAmt_11_e(String amt_11_e) {
		this.amt_11_e = amt_11_e;
	}

	public String getAmt_17_s() {
		return amt_17_s;
	}

	public void setAmt_17_s(String amt_17_s) {
		this.amt_17_s = amt_17_s;
	}

	public String getAmt_17_e() {
		return amt_17_e;
	}

	public void setAmt_17_e(String amt_17_e) {
		this.amt_17_e = amt_17_e;
	}

	public String getAmt_12_s() {
		return amt_12_s;
	}

	public void setAmt_12_s(String amt_12_s) {
		this.amt_12_s = amt_12_s;
	}

	public String getAmt_12_e() {
		return amt_12_e;
	}

	public void setAmt_12_e(String amt_12_e) {
		this.amt_12_e = amt_12_e;
	}

	public String getAmt_18_s() {
		return amt_18_s;
	}

	public void setAmt_18_s(String amt_18_s) {
		this.amt_18_s = amt_18_s;
	}

	public String getAmt_18_e() {
		return amt_18_e;
	}

	public void setAmt_18_e(String amt_18_e) {
		this.amt_18_e = amt_18_e;
	}

	public String getAmt_25_s() {
		return amt_25_s;
	}

	public void setAmt_25_s(String amt_25_s) {
		this.amt_25_s = amt_25_s;
	}

	public String getAmt_25_e() {
		return amt_25_e;
	}

	public void setAmt_25_e(String amt_25_e) {
		this.amt_25_e = amt_25_e;
	}

	public String getAmt_26_s() {
		return amt_26_s;
	}

	public void setAmt_26_s(String amt_26_s) {
		this.amt_26_s = amt_26_s;
	}

	public String getAmt_26_e() {
		return amt_26_e;
	}

	public void setAmt_26_e(String amt_26_e) {
		this.amt_26_e = amt_26_e;
	}

	public String getAtr_01() {
		return atr_01;
	}

	public void setAtr_01(String atr_01) {
		this.atr_01 = atr_01;
	}

	public String getAtr_02() {
		return atr_02;
	}

	public void setAtr_02(String atr_02) {
		this.atr_02 = atr_02;
	}

	public Date getAtr_03_sDate() {
		return atr_03_sDate;
	}

	public void setAtr_03_sDate(Date atr_03_sDate) {
		this.atr_03_sDate = atr_03_sDate;
	}

	public Date getAtr_03_eDate() {
		return atr_03_eDate;
	}

	public void setAtr_03_eDate(Date atr_03_eDate) {
		this.atr_03_eDate = atr_03_eDate;
	}

	public String getManage_01() {
		return manage_01;
	}

	public void setManage_01(String manage_01) {
		this.manage_01 = manage_01;
	}

	public String getManage_02() {
		return manage_02;
	}

	public void setManage_02(String manage_02) {
		this.manage_02 = manage_02;
	}

	public Date getManage_03_sDate() {
		return manage_03_sDate;
	}

	public void setManage_03_sDate(Date manage_03_sDate) {
		this.manage_03_sDate = manage_03_sDate;
	}

	public Date getManage_03_eDate() {
		return manage_03_eDate;
	}

	public void setManage_03_eDate(Date manage_03_eDate) {
		this.manage_03_eDate = manage_03_eDate;
	}

	public String getManage_04() {
		return manage_04;
	}

	public void setManage_04(String manage_04) {
		this.manage_04 = manage_04;
	}

	public String getManage_05_Date() {
		return manage_05_Date;
	}

	public void setManage_05_Date(String manage_05_Date) {
		this.manage_05_Date = manage_05_Date;
	}

	public String getManage_06_fee_s() {
		return manage_06_fee_s;
	}

	public void setManage_06_fee_s(String manage_06_fee_s) {
		this.manage_06_fee_s = manage_06_fee_s;
	}

	public String getManage_06_fee_e() {
		return manage_06_fee_e;
	}

	public void setManage_06_fee_e(String manage_06_fee_e) {
		this.manage_06_fee_e = manage_06_fee_e;
	}
	
	public String getRoa_s() {
		return roa_s;
	}

	public void setRoa_s(String roa_s) {
		this.roa_s = roa_s;
	}

	public String getRoa_e() {
		return roa_e;
	}

	public void setRoa_e(String roa_e) {
		this.roa_e = roa_e;
	}

	public String getCampaign_ao_code() {
		return campaign_ao_code;
	}

	public void setCampaign_ao_code(String campaign_ao_code) {
		this.campaign_ao_code = campaign_ao_code;
	}

	public Date getCampaign_date() {
		return campaign_date;
	}

	public void setCampaign_date(Date campaign_date) {
		this.campaign_date = campaign_date;
	}

	public String getHnwc_flag() {
		return hnwc_flag;
	}

	public void setHnwc_flag(String hnwc_flag) {
		this.hnwc_flag = hnwc_flag;
	}

}
