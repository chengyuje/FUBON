package com.systex.jbranch.app.server.fps.sqm120;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class SQM120EditInputVO extends PagingInputVO {

	private String case_no;				//案件編號
	private String qtnType;				//問卷別 WMS01：理專抽樣問卷 WMS02：投資/保險 WMS03：臨櫃 WMS04：開戶 WMS05：簡訊
	private String seq;					//主檔問卷流水編號
	private String end_date;			//應結案日期
	private String data_date;			//主檔資料日期
	private String ao_code;				//主檔aocode
	private String region_center_id;	//主檔業務處代碼
	private String branch_area_id;		//主檔營運區代碼
	private String branch_nbr;			//分行代碼
	private String emp_id;				//明細檔員工編號
	private String emp_name;			//明細檔員工姓名
	private String cust_id;				//主檔客戶ID
	private String cust_name;			//主檔客戶姓名
	private String resp_note;			//主檔質化意見
	private String qst_version;			//主檔題目版本
	private String trade_date;			//主檔交易日期
	private String brh_desc;			//明細檔分行說明/問題釐清
	private String waiting_time;		//明細檔客戶來行等待時間
	private String working_time;		//明細檔交易作業時間
	private String cur_job_y;			//明細檔經辦/專員現職年資-年
	private String cur_job_m;			//明細檔經辦/專員現職年資-月
	private String sup_emp_name;		//明細檔主管姓名
	private String sup_emp_id;			//明細檔主管員編
	private String sup_cur_job;			//明細檔主管職務
	private String improve_desc;		//明細檔改善計畫
	private String op_sup_remark;		//明細檔督導簽核意見
	private String rc_vice_sup_remark;	//明細檔處副主管簽核意見
	private String last_visit_date;		//明細檔最近一次訪談記錄建立時間
	private String con_degree;			//明細檔貢獻度等級
	private String frq_day;				//明細檔聯繫頻率
	private String deduction_initial;	//明細檔處長裁示	Y:扣分   N:不扣分
	private String rc_sup_remark;		//明細檔處長簽核意見
	private String headmgr_remark;		//明細檔總行簽核意見
	private String cur_job;				//明細檔經辦/專員職務
	private String owner_emp_id;		//主檔待處理人ID
	private String creator;				//主檔建立人
	private String returns_remark;		//簽核流程檔-簽核意見
	private String returns_type;		//判斷是否退件給建立人
	private String save_type;			//判斷暫存或結案
	private String loginID; // 2020-2-17 by Jacky
							// WMS-CR-20190906-01_擬增設M+系統通知批示服務滿意度調查案件功能
	private String isFrom; // SQM410--> 判斷是否從M+進來
	private String prededuction_initial; // 督導建議是否扣分
	private String agent_type;           //用來判斷是本人交易(main)或代理人交易(agent)
	
	
	
	public String getAgent_type() {
		return agent_type;
	}

	public void setAgent_type(String agent_type) {
		this.agent_type = agent_type;
	}

	public String getPrededuction_initial() {
		return prededuction_initial;
	}

	public void setPrededuction_initial(String prededuction_initial) {
		this.prededuction_initial = prededuction_initial;
	}

	public String getIsFrom() {
		return isFrom;
	}

	public void setIsFrom(String isFrom) {
		this.isFrom = isFrom;
	}

	public String getLoginID() {
		return loginID;
	}

	public void setLoginID(String loginID) {
		this.loginID = loginID;
	}

	public String getCase_no() {
		return case_no;
	}

	public void setCase_no(String case_no) {
		this.case_no = case_no;
	}

	public String getQtnType() {
		return qtnType;
	}

	public void setQtnType(String qtnType) {
		this.qtnType = qtnType;
	}

	public String getSeq() {
		return seq;
	}

	public void setSeq(String seq) {
		this.seq = seq;
	}

	public String getData_date() {
		return data_date;
	}

	public void setData_date(String data_date) {
		this.data_date = data_date;
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

	public String getEmp_id() {
		return emp_id;
	}

	public void setEmp_id(String emp_id) {
		this.emp_id = emp_id;
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

	public String getResp_note() {
		return resp_note;
	}

	public void setResp_note(String resp_note) {
		this.resp_note = resp_note;
	}

	public String getQst_version() {
		return qst_version;
	}

	public void setQst_version(String qst_version) {
		this.qst_version = qst_version;
	}

	public String getTrade_date() {
		return trade_date;
	}

	public void setTrade_date(String trade_date) {
		this.trade_date = trade_date;
	}

	public String getBrh_desc() {
		return brh_desc;
	}

	public void setBrh_desc(String brh_desc) {
		this.brh_desc = brh_desc;
	}

	public String getWaiting_time() {
		return waiting_time;
	}

	public void setWaiting_time(String waiting_time) {
		this.waiting_time = waiting_time;
	}

	public String getWorking_time() {
		return working_time;
	}

	public void setWorking_time(String working_time) {
		this.working_time = working_time;
	}

	public String getCur_job_y() {
		return cur_job_y;
	}

	public void setCur_job_y(String cur_job_y) {
		this.cur_job_y = cur_job_y;
	}

	public String getCur_job_m() {
		return cur_job_m;
	}

	public void setCur_job_m(String cur_job_m) {
		this.cur_job_m = cur_job_m;
	}

	public String getSup_emp_name() {
		return sup_emp_name;
	}

	public void setSup_emp_name(String sup_emp_name) {
		this.sup_emp_name = sup_emp_name;
	}

	public String getSup_emp_id() {
		return sup_emp_id;
	}

	public void setSup_emp_id(String sup_emp_id) {
		this.sup_emp_id = sup_emp_id;
	}

	public String getSup_cur_job() {
		return sup_cur_job;
	}

	public void setSup_cur_job(String sup_cur_job) {
		this.sup_cur_job = sup_cur_job;
	}

	public String getImprove_desc() {
		return improve_desc;
	}

	public void setImprove_desc(String improve_desc) {
		this.improve_desc = improve_desc;
	}

	public String getOp_sup_remark() {
		return op_sup_remark;
	}

	public void setOp_sup_remark(String op_sup_remark) {
		this.op_sup_remark = op_sup_remark;
	}

	public String getRc_vice_sup_remark() {
		return rc_vice_sup_remark;
	}

	public void setRc_vice_sup_remark(String rc_vice_sup_remark) {
		this.rc_vice_sup_remark = rc_vice_sup_remark;
	}

	public String getLast_visit_date() {
		return last_visit_date;
	}

	public void setLast_visit_date(String last_visit_date) {
		this.last_visit_date = last_visit_date;
	}

	public String getCon_degree() {
		return con_degree;
	}

	public void setCon_degree(String con_degree) {
		this.con_degree = con_degree;
	}

	public String getFrq_day() {
		return frq_day;
	}

	public void setFrq_day(String frq_day) {
		this.frq_day = frq_day;
	}

	public String getDeduction_initial() {
		return deduction_initial;
	}

	public void setDeduction_initial(String deduction_initial) {
		this.deduction_initial = deduction_initial;
	}

	public String getRc_sup_remark() {
		return rc_sup_remark;
	}

	public void setRc_sup_remark(String rc_sup_remark) {
		this.rc_sup_remark = rc_sup_remark;
	}

	public String getHeadmgr_remark() {
		return headmgr_remark;
	}

	public void setHeadmgr_remark(String headmgr_remark) {
		this.headmgr_remark = headmgr_remark;
	}

	public String getEnd_date() {
		return end_date;
	}

	public void setEnd_date(String end_date) {
		this.end_date = end_date;
	}

	public String getEmp_name() {
		return emp_name;
	}

	public void setEmp_name(String emp_name) {
		this.emp_name = emp_name;
	}

	public String getAo_code() {
		return ao_code;
	}

	public void setAo_code(String ao_code) {
		this.ao_code = ao_code;
	}

	public String getCur_job() {
		return cur_job;
	}

	public void setCur_job(String cur_job) {
		this.cur_job = cur_job;
	}

	public String getOwner_emp_id() {
		return owner_emp_id;
	}

	public void setOwner_emp_id(String owner_emp_id) {
		this.owner_emp_id = owner_emp_id;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public String getReturns_remark() {
		return returns_remark;
	}

	public void setReturns_remark(String returns_remark) {
		this.returns_remark = returns_remark;
	}

	public String getReturns_type() {
		return returns_type;
	}

	public void setReturns_type(String returns_type) {
		this.returns_type = returns_type;
	}

	public String getSave_type() {
		return save_type;
	}

	public void setSave_type(String save_type) {
		this.save_type = save_type;
	}

}
