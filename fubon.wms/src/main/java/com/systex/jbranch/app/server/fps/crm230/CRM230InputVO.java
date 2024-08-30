package com.systex.jbranch.app.server.fps.crm230;

import java.sql.Date;
import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class CRM230InputVO extends PagingInputVO {

	//cust
	private String cust_id;
	private String belong_bra_nbr;
	private String country_nbr;
	private String gender;
	private String marriage;
	private String education;
	private String child;
	private String vip_degree;
	private String ao_code;
	private String cust_risk_atr;
	private String sal_company;
	private String co_acct;
	private String complain;
	private String ebank;
	private String voice;
	private String app;
	private String investor;
	private String rec;
	private Date birth_sDate;
	private Date birth_eDate;
	private List aolist;
	private String campaign_ao_code;
	private Date campaign_date;
	private String campaign_name;
	private String campaign_desc;
	private List<Map<String, String>> campaign_custlist;
	private List<String> availbranchlist;

	private String cust_w8ben;
	//group
	private String group;
	private String group_name;
	private List<Map<String, String>> grouplist;

	//crm211 or crm221來的
	private String cust_02;
	private String source;

	private String companyTYPE;
	private String keyWord;

	private String uEmpID;

	// add by Carley 2019/08/05
	//信用卡
	private String hold_card_flg; //是否持有有效信用卡
	private String ms_type; //持有正/附卡
	private Date spec_date; //特定日期
	private String opp_ms_type; //無有效...者
	private String spec_check_yn; //有無特定日期

	// add by Carley 2019/08/05
	private String cust_type; //客戶別（02：自然人、03：法人）
	private String sal_acc_yn; //薪轉戶

	public String getCust_w8ben() {
		return cust_w8ben;
	}

	public void setCust_w8ben(String cust_w8ben) {
		this.cust_w8ben = cust_w8ben;
	}

	public String getuEmpID() {
		return uEmpID;
	}

	public void setuEmpID(String uEmpID) {
		this.uEmpID = uEmpID;
	}

	public String getCust_id() {
		return cust_id;
	}

	public void setCust_id(String cust_id) {
		this.cust_id = cust_id;
	}

	public String getBelong_bra_nbr() {
		return belong_bra_nbr;
	}

	public void setBelong_bra_nbr(String belong_bra_nbr) {
		this.belong_bra_nbr = belong_bra_nbr;
	}

	public String getCountry_nbr() {
		return country_nbr;
	}

	public void setCountry_nbr(String country_nbr) {
		this.country_nbr = country_nbr;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getMarriage() {
		return marriage;
	}

	public void setMarriage(String marriage) {
		this.marriage = marriage;
	}

	public String getEducation() {
		return education;
	}

	public void setEducation(String education) {
		this.education = education;
	}

	public String getChild() {
		return child;
	}

	public void setChild(String child) {
		this.child = child;
	}

	public String getVip_degree() {
		return vip_degree;
	}

	public void setVip_degree(String vip_degree) {
		this.vip_degree = vip_degree;
	}

	public String getAo_code() {
		return ao_code;
	}

	public void setAo_code(String ao_code) {
		this.ao_code = ao_code;
	}

	public String getCust_risk_atr() {
		return cust_risk_atr;
	}

	public void setCust_risk_atr(String cust_risk_atr) {
		this.cust_risk_atr = cust_risk_atr;
	}

	public String getSal_company() {
		return sal_company;
	}

	public void setSal_company(String sal_company) {
		this.sal_company = sal_company;
	}

	public String getCo_acct() {
		return co_acct;
	}

	public void setCo_acct(String co_acct) {
		this.co_acct = co_acct;
	}

	public String getComplain() {
		return complain;
	}

	public void setComplain(String complain) {
		this.complain = complain;
	}

	public String getEbank() {
		return ebank;
	}

	public void setEbank(String ebank) {
		this.ebank = ebank;
	}

	public String getVoice() {
		return voice;
	}

	public void setVoice(String voice) {
		this.voice = voice;
	}

	public String getApp() {
		return app;
	}

	public void setApp(String app) {
		this.app = app;
	}

	public String getInvestor() {
		return investor;
	}

	public void setInvestor(String investor) {
		this.investor = investor;
	}

	public String getRec() {
		return rec;
	}

	public void setRec(String rec) {
		this.rec = rec;
	}

	public Date getBirth_sDate() {
		return birth_sDate;
	}

	public void setBirth_sDate(Date birth_sDate) {
		this.birth_sDate = birth_sDate;
	}

	public Date getBirth_eDate() {
		return birth_eDate;
	}

	public void setBirth_eDate(Date birth_eDate) {
		this.birth_eDate = birth_eDate;
	}

	public List getAolist() {
		return aolist;
	}

	public void setAolist(List aolist) {
		this.aolist = aolist;
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

	public String getCampaign_name() {
		return campaign_name;
	}

	public void setCampaign_name(String campaign_name) {
		this.campaign_name = campaign_name;
	}

	public String getCampaign_desc() {
		return campaign_desc;
	}

	public void setCampaign_desc(String campaign_desc) {
		this.campaign_desc = campaign_desc;
	}

	public List<Map<String, String>> getCampaign_custlist() {
		return campaign_custlist;
	}

	public void setCampaign_custlist(List<Map<String, String>> campaign_custlist) {
		this.campaign_custlist = campaign_custlist;
	}

	public List<String> getAvailbranchlist() {
		return availbranchlist;
	}

	public void setAvailbranchlist(List<String> availbranchlist) {
		this.availbranchlist = availbranchlist;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public String getGroup_name() {
		return group_name;
	}

	public void setGroup_name(String group_name) {
		this.group_name = group_name;
	}

	public List<Map<String, String>> getGrouplist() {
		return grouplist;
	}

	public void setGrouplist(List<Map<String, String>> grouplist) {
		this.grouplist = grouplist;
	}

	public String getCust_02() {
		return cust_02;
	}

	public void setCust_02(String cust_02) {
		this.cust_02 = cust_02;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getCompanyTYPE() {
		return companyTYPE;
	}

	public void setCompanyTYPE(String companyTYPE) {
		this.companyTYPE = companyTYPE;
	}

	public String getKeyWord() {
		return keyWord;
	}

	public void setKeyWord(String keyWord) {
		this.keyWord = keyWord;
	}

	public String getHold_card_flg() {
		return hold_card_flg;
	}

	public void setHold_card_flg(String hold_card_flg) {
		this.hold_card_flg = hold_card_flg;
	}

	public String getMs_type() {
		return ms_type;
	}

	public void setMs_type(String ms_type) {
		this.ms_type = ms_type;
	}

	public Date getSpec_date() {
		return spec_date;
	}

	public void setSpec_date(Date spec_date) {
		this.spec_date = spec_date;
	}

	public String getOpp_ms_type() {
		return opp_ms_type;
	}

	public void setOpp_ms_type(String opp_ms_type) {
		this.opp_ms_type = opp_ms_type;
	}

	public String getSpec_check_yn() {
		return spec_check_yn;
	}

	public void setSpec_check_yn(String spec_check_yn) {
		this.spec_check_yn = spec_check_yn;
	}

	public String getCust_type() {
		return cust_type;
	}

	public void setCust_type(String cust_type) {
		this.cust_type = cust_type;
	}

	public String getSal_acc_yn() {
		return sal_acc_yn;
	}

	public void setSal_acc_yn(String sal_acc_yn) {
		this.sal_acc_yn = sal_acc_yn;
	}
}
