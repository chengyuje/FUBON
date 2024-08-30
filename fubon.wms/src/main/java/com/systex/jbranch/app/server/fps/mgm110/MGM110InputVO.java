package com.systex.jbranch.app.server.fps.mgm110;

import java.sql.Date;
import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class MGM110InputVO extends PagingInputVO{
	
	private String act_seq;				//活動代碼
	private String seq;					//案件序號、活動附件序號
	private String region_center_id;	//業務處
	private String branch_area_id;		//營運區
	private String branch_nbr;			//分行
	private String ao_code;				//理專AO CODE
	private String cust_id;				//推薦人ID
	private String cust_name;			//客戶姓名
	private String bemgm_cust_id;		//被推薦人ID
	private List<Map<String,String>> branch_list;
	private String ao_list;
	private String role;
	private String act_type;			//活動類型-- M：MGM / V：VIP
	
	//MGM覆核&退回用
	private String action_type;
	private String mgm_flag;
	
	//MGM鍵機用
	private String mgm_cust_id;			//推薦人ID
	private String mgm_cust_name;		//推薦人姓名
	private Date mgm_start_date;		//推薦日
	private String be_mgm_cust_id;		//被推薦人ID
	private String be_mgm_cust_name;	//被推薦人姓名
	private String be_mgm_cust_phone;	//連絡電話
	private String memo;				//備註
	private String mgm_sign;			//推薦人簽署(Y/N)
	private String be_mgm_sign;			//被推薦人簽署(Y/N)
	
	private String mgm_sign_form_name;				//推薦人簽署表單
	private String real_mgm_sign_form_name;			//推薦人簽署表單
	private String be_mgm_sign_form_name;			//推薦人簽署表單
	private String real_be_mgm_sign_form_name;		//推薦人簽署表單
	private String formType;						//簽署表單型態 (推薦人/被推薦人)
	
	//禮贈品兌換申請使用
	private String rec_name;			//收件人姓名
	private String rec_tel_no;			//收件人電話
	private String rec_mobile_no;		//收件人手機
	private String address;				//郵寄地址
	private String exchange;			//當次兌換點數 / 金額
//	private String exchange_points;		//當次兌換點數
//	private String exchange_reward;		//當次兌換金額
	private List<Map<String,String>> apply_gift_list;
	
	public String getAct_seq() {
		return act_seq;
	}
	
	public void setAct_seq(String act_seq) {
		this.act_seq = act_seq;
	}
	
	public String getSeq() {
		return seq;
	}
	
	public void setSeq(String seq) {
		this.seq = seq;
	}
	
	public String getRegion_center_id() {
		return region_center_id;
	}
	
	public void setRegion_center_id(String region_center_id) {
		this.region_center_id = region_center_id;
	}
	
	public String getBranch_area_id() {
		return branch_area_id;
	}
	
	public void setBranch_area_id(String branch_area_id) {
		this.branch_area_id = branch_area_id;
	}
	
	public String getBranch_nbr() {
		return branch_nbr;
	}
	
	public void setBranch_nbr(String branch_nbr) {
		this.branch_nbr = branch_nbr;
	}
	
	public String getAo_code() {
		return ao_code;
	}
	
	public void setAo_code(String ao_code) {
		this.ao_code = ao_code;
	}
	
	public String getCust_id() {
		return cust_id;
	}
	
	public void setCust_id(String cust_id) {
		this.cust_id = cust_id;
	}
	
	public String getCust_name() {
		return cust_name;
	}
	
	public void setCust_name(String cust_name) {
		this.cust_name = cust_name;
	}
	
	public String getBemgm_cust_id() {
		return bemgm_cust_id;
	}

	public void setBemgm_cust_id(String bemgm_cust_id) {
		this.bemgm_cust_id = bemgm_cust_id;
	}

	public List<Map<String, String>> getBranch_list() {
		return branch_list;
	}
	
	public void setBranch_list(List<Map<String, String>> branch_list) {
		this.branch_list = branch_list;
	}
	
	public String getAo_list() {
		return ao_list;
	}
	
	public void setAo_list(String ao_list) {
		this.ao_list = ao_list;
	}
	
	public String getRole() {
		return role;
	}
	
	public void setRole(String role) {
		this.role = role;
	}
	
	public String getAct_type() {
		return act_type;
	}
	
	public void setAct_type(String act_type) {
		this.act_type = act_type;
	}
	
	public String getAction_type() {
		return action_type;
	}
	
	public void setAction_type(String action_type) {
		this.action_type = action_type;
	}
	
	public String getMgm_flag() {
		return mgm_flag;
	}
	
	public void setMgm_flag(String mgm_flag) {
		this.mgm_flag = mgm_flag;
	}
	
	public String getMgm_cust_id() {
		return mgm_cust_id;
	}
	
	public void setMgm_cust_id(String mgm_cust_id) {
		this.mgm_cust_id = mgm_cust_id;
	}
	
	public String getMgm_cust_name() {
		return mgm_cust_name;
	}
	
	public void setMgm_cust_name(String mgm_cust_name) {
		this.mgm_cust_name = mgm_cust_name;
	}
	
	public Date getMgm_start_date() {
		return mgm_start_date;
	}
	
	public void setMgm_start_date(Date mgm_start_date) {
		this.mgm_start_date = mgm_start_date;
	}
	
	public String getBe_mgm_cust_id() {
		return be_mgm_cust_id;
	}
	
	public void setBe_mgm_cust_id(String be_mgm_cust_id) {
		this.be_mgm_cust_id = be_mgm_cust_id;
	}
	
	public String getBe_mgm_cust_name() {
		return be_mgm_cust_name;
	}
	
	public void setBe_mgm_cust_name(String be_mgm_cust_name) {
		this.be_mgm_cust_name = be_mgm_cust_name;
	}
	
	public String getBe_mgm_cust_phone() {
		return be_mgm_cust_phone;
	}
	
	public void setBe_mgm_cust_phone(String be_mgm_cust_phone) {
		this.be_mgm_cust_phone = be_mgm_cust_phone;
	}
	
	public String getMemo() {
		return memo;
	}
	
	public void setMemo(String memo) {
		this.memo = memo;
	}
	
	public String getMgm_sign() {
		return mgm_sign;
	}
	
	public void setMgm_sign(String mgm_sign) {
		this.mgm_sign = mgm_sign;
	}
	
	public String getBe_mgm_sign() {
		return be_mgm_sign;
	}
	
	public void setBe_mgm_sign(String be_mgm_sign) {
		this.be_mgm_sign = be_mgm_sign;
	}
	
	public String getMgm_sign_form_name() {
		return mgm_sign_form_name;
	}

	public void setMgm_sign_form_name(String mgm_sign_form_name) {
		this.mgm_sign_form_name = mgm_sign_form_name;
	}

	public String getReal_mgm_sign_form_name() {
		return real_mgm_sign_form_name;
	}

	public void setReal_mgm_sign_form_name(String real_mgm_sign_form_name) {
		this.real_mgm_sign_form_name = real_mgm_sign_form_name;
	}

	public String getBe_mgm_sign_form_name() {
		return be_mgm_sign_form_name;
	}

	public void setBe_mgm_sign_form_name(String be_mgm_sign_form_name) {
		this.be_mgm_sign_form_name = be_mgm_sign_form_name;
	}

	public String getReal_be_mgm_sign_form_name() {
		return real_be_mgm_sign_form_name;
	}

	public void setReal_be_mgm_sign_form_name(String real_be_mgm_sign_form_name) {
		this.real_be_mgm_sign_form_name = real_be_mgm_sign_form_name;
	}

	public String getFormType() {
		return formType;
	}

	public void setFormType(String formType) {
		this.formType = formType;
	}

	public String getRec_name() {
		return rec_name;
	}
	
	public void setRec_name(String rec_name) {
		this.rec_name = rec_name;
	}
	
	public String getRec_tel_no() {
		return rec_tel_no;
	}
	
	public void setRec_tel_no(String rec_tel_no) {
		this.rec_tel_no = rec_tel_no;
	}
	
	public String getRec_mobile_no() {
		return rec_mobile_no;
	}
	
	public void setRec_mobile_no(String rec_mobile_no) {
		this.rec_mobile_no = rec_mobile_no;
	}
	
	public String getAddress() {
		return address;
	}
	
	public void setAddress(String address) {
		this.address = address;
	}
	
	public String getExchange() {
		return exchange;
	}
	
	public void setExchange(String exchange) {
		this.exchange = exchange;
	}
	public List<Map<String, String>> getApply_gift_list() {
		return apply_gift_list;
	}
	
	public void setApply_gift_list(List<Map<String, String>> apply_gift_list) {
		this.apply_gift_list = apply_gift_list;
	}
}
