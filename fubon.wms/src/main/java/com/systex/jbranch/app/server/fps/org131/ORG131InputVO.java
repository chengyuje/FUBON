package com.systex.jbranch.app.server.fps.org131;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class ORG131InputVO extends PagingInputVO{
	
	private String cust_id;
	private String emp_id ;
	private String emp_name;			//應徵者
	private String intv_emp_id;
	private String intv_emp_name;
//	private String rc_id;
//	private String op_id;             	//營運區
//	private String br_id;			  	//分行
	private String region_center_id; 
	
	private String branch_area_id;
	private String branch_nbr;
	private String branch_nbr_back;
	
//	private String rc_ida;
//	private String op_ida;             	//營運區
//	private String br_ida;			  	//分行
	private Date brch_recv_case_date;
	private Date recv_case_sdate;	  	//進件日期開始
	private Date recv_case_edate;		//進件日期結束
	private Date ho_recv_case_date;
	private Date oa_sup_rt_date;
	private Date brch_ini_int_date;
	private String status;				//目前狀態
	private Date sCreDate;
	private Date book_onbo_sdate;		//預定報到日期開始
	private Date book_onbo_edate;		//預定報到日期結束
	private String job_rank;
	private String job_title_name;
	private String ao_job_rank;
	private String ao_job_rank1;
	private Date testDate;
	private String status_reason;
	private String result;
	private String desc;
	private String region_return;
	private String return_remark;
	private String black_listed;
	private String prev_job_exp;
	private String cur_job;
	private String cur_job_name;
	private String cur_aum;
	private String cur_m_goal;
	private String actual_accomplish;
	private String cur_fee_income;
	private String cust_cnt;
	private String ao_year_of_exp;
	private Date able_onboard_date;
	private String prev_job;
	private String rc_sup_emp_id;
	private String op_sup_emp_id;
	private String hr;
	private String trans_from_branch_id;
	private String trans_remark;
	private String resume_source;
	private String recommender_emp_id;
	private String recomm_emp_id;
	private String recommend_awardee_emp_id;
	private String recommend_letter;
	private String resign_return;
	private String financial_exp;
	private String pre_fin_inst;
	private String other_pre_fin_inst;
	private String other_fi;
	private String resign_reason;
	private String cust_satisfaction;
	private List<String> req_certificate;
	private String req_certificate1;
	private String achievement;
	private String sales_skill;
	private String active;
	private String pressure_manage;
	private String communication;
	private String problem_solving_skill;
	private String intv_sup_remark;
	private String hire_status;
	private Date createtime;
	private String creator;
	private String modifier;
	private Date lastupdate;
	private List<Map<String, Object>> data;
	private String login_id;
	private String sTime;
	private String type;
	private String roleID;
	private String mroleID;
	private String seqno;
	
	private String fileName;
	private String fileRealName;
	// 2017/4/19
	private String fileLabel;
	private String fileData;
	
	private String fee_6m_ability;
	private String fee_1y_ability;
	private String suggest_job;
	private String suggest_salary;
	
	private Date evDate;
	private String honest;
	private String kindly;
	private String prof;
	private String innov;
	private String organize;
	private String selfImprove;
	private String expectation;
	private Date bookedOnboardDate;
	private BigDecimal expSalary;
	private BigDecimal preMonthGoal;
	private BigDecimal pre6MAccomplish;
	private BigDecimal exp6MAbility;
	private BigDecimal exp1YAbility;
	private BigDecimal expPropertyLoan;
	private BigDecimal expCreditLoan;
	private String hireStatusTransRemark;
	private String prefType;
	private String buLayer1;
	private String buLayer2;
	private String buLayer3;
	private String buLayer4;
	private String buLayer5;
	private String buLayer6;
	private String buLayer7;
	private String buLayer8;
	private String crosBuLayer1;
	private String crosBuLayer2;
	private String crosBuLayer3;
	private String crosBuLayer4;
	private String crosBuLayer5;
	private String crosBuLayer6;
	private String crosBuLayer7;
	private String crosBuLayer8;
	private boolean isSupervisor;
	private String fhcSeq;
	
	// add by ocean : WMS-CR-20190710-01_金控版面談評估表格式內容調整
	private String credit_status;
	private String credit_dtl;
	
	//#1509
	private BigDecimal seq;
	
	public String getCredit_status() {
		return credit_status;
	}

	public void setCredit_status(String credit_status) {
		this.credit_status = credit_status;
	}

	public String getCredit_dtl() {
		return credit_dtl;
	}

	public void setCredit_dtl(String credit_dtl) {
		this.credit_dtl = credit_dtl;
	}
	public String getFee_6m_ability() {
		return fee_6m_ability;
	}

	public void setFee_6m_ability(String fee_6m_ability) {
		this.fee_6m_ability = fee_6m_ability;
	}

	public String getFee_1y_ability() {
		return fee_1y_ability;
	}

	public void setFee_1y_ability(String fee_1y_ability) {
		this.fee_1y_ability = fee_1y_ability;
	}

	public String getSuggest_job() {
		return suggest_job;
	}

	public void setSuggest_job(String suggest_job) {
		this.suggest_job = suggest_job;
	}

	public String getSuggest_salary() {
		return suggest_salary;
	}

	public void setSuggest_salary(String suggest_salary) {
		this.suggest_salary = suggest_salary;
	}

	public String getBranch_nbr_back() {
		return branch_nbr_back;
	}

	public void setBranch_nbr_back(String branch_nbr_back) {
		this.branch_nbr_back = branch_nbr_back;
	}

	public String getCust_id() {
		return cust_id;
	}
	
	public void setCust_id(String cust_id) {
		this.cust_id = cust_id;
	}
	
	public String getEmp_id() {
		return emp_id;
	}
	
	public void setEmp_id(String emp_id) {
		this.emp_id = emp_id;
	}
	
	public String getEmp_name() {
		return emp_name;
	}
	
	public void setEmp_name(String emp_name) {
		this.emp_name = emp_name;
	}
	
	public String getIntv_emp_id() {
		return intv_emp_id;
	}
	
	public void setIntv_emp_id(String intv_emp_id) {
		this.intv_emp_id = intv_emp_id;
	}
	
	public String getIntv_emp_name() {
		return intv_emp_name;
	}
	
	public void setIntv_emp_name(String intv_emp_name) {
		this.intv_emp_name = intv_emp_name;
	}
	
//	public String getRc_ida() {
//		return rc_ida;
//	}
//	
//	public void setRc_ida(String rc_ida) {
//		this.rc_ida = rc_ida;
//	}
//	
//	public String getOp_ida() {
//		return op_ida;
//	}
//	
//	public void setOp_ida(String op_ida) {
//		this.op_ida = op_ida;
//	}
//	
//	public String getBr_ida() {
//		return br_ida;
//	}
//	
//	public void setBr_ida(String br_ida) {
//		this.br_ida = br_ida;
//	}
	
	public Date getBrch_recv_case_date() {
		return brch_recv_case_date;
	}
	
	public void setBrch_recv_case_date(Date brch_recv_case_date) {
		this.brch_recv_case_date = brch_recv_case_date;
	}
	
	public Date getRecv_case_sdate() {
		return recv_case_sdate;
	}
	
	public void setRecv_case_sdate(Date recv_case_sdate) {
		this.recv_case_sdate = recv_case_sdate;
	}
	
	public Date getRecv_case_edate() {
		return recv_case_edate;
	}
	
	public void setRecv_case_edate(Date recv_case_edate) {
		this.recv_case_edate = recv_case_edate;
	}
	
	public Date getHo_recv_case_date() {
		return ho_recv_case_date;
	}
	
	public void setHo_recv_case_date(Date ho_recv_case_date) {
		this.ho_recv_case_date = ho_recv_case_date;
	}
	
	public Date getOa_sup_rt_date() {
		return oa_sup_rt_date;
	}
	
	public void setOa_sup_rt_date(Date oa_sup_rt_date) {
		this.oa_sup_rt_date = oa_sup_rt_date;
	}
	
	public Date getBrch_ini_int_date() {
		return brch_ini_int_date;
	}
	
	public void setBrch_ini_int_date(Date brch_ini_int_date) {
		this.brch_ini_int_date = brch_ini_int_date;
	}
	
	public String getStatus() {
		return status;
	}
	
	public void setStatus(String status) {
		this.status = status;
	}
	
	public Date getsCreDate() {
		return sCreDate;
	}
	
	public void setsCreDate(Date sCreDate) {
		this.sCreDate = sCreDate;
	}
	
	public Date getBook_onbo_sdate() {
		return book_onbo_sdate;
	}
	
	public void setBook_onbo_sdate(Date book_onbo_sdate) {
		this.book_onbo_sdate = book_onbo_sdate;
	}
	
	public Date getBook_onbo_edate() {
		return book_onbo_edate;
	}
	
	public void setBook_onbo_edate(Date book_onbo_edate) {
		this.book_onbo_edate = book_onbo_edate;
	}
	
	public String getJob_rank() {
		return job_rank;
	}
	
	public void setJob_rank(String job_rank) {
		this.job_rank = job_rank;
	}
	
	public String getJob_title_name() {
		return job_title_name;
	}
	
	public void setJob_title_name(String job_title_name) {
		this.job_title_name = job_title_name;
	}
	
	public String getAo_job_rank() {
		return ao_job_rank;
	}
	
	public void setAo_job_rank(String ao_job_rank) {
		this.ao_job_rank = ao_job_rank;
	}
	
	public String getAo_job_rank1() {
		return ao_job_rank1;
	}
	
	public void setAo_job_rank1(String ao_job_rank1) {
		this.ao_job_rank1 = ao_job_rank1;
	}
	
	public Date getTestDate() {
		return testDate;
	}
	
	public void setTestDate(Date testDate) {
		this.testDate = testDate;
	}
	
	public String getStatus_reason() {
		return status_reason;
	}
	
	public void setStatus_reason(String status_reason) {
		this.status_reason = status_reason;
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

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getRegion_return() {
		return region_return;
	}

	public void setRegion_return(String region_return) {
		this.region_return = region_return;
	}

	public String getReturn_remark() {
		return return_remark;
	}

	public void setReturn_remark(String return_remark) {
		this.return_remark = return_remark;
	}

	public String getBlack_listed() {
		return black_listed;
	}

	public void setBlack_listed(String black_listed) {
		this.black_listed = black_listed;
	}

	public String getPrev_job_exp() {
		return prev_job_exp;
	}

	public void setPrev_job_exp(String prev_job_exp) {
		this.prev_job_exp = prev_job_exp;
	}

	public String getCur_job() {
		return cur_job;
	}

	public void setCur_job(String cur_job) {
		this.cur_job = cur_job;
	}

	public String getCur_job_name() {
		return cur_job_name;
	}

	public void setCur_job_name(String cur_job_name) {
		this.cur_job_name = cur_job_name;
	}

	public String getCur_aum() {
		return cur_aum;
	}

	public void setCur_aum(String cur_aum) {
		this.cur_aum = cur_aum;
	}

	public String getCur_m_goal() {
		return cur_m_goal;
	}

	public void setCur_m_goal(String cur_m_goal) {
		this.cur_m_goal = cur_m_goal;
	}

	public String getActual_accomplish() {
		return actual_accomplish;
	}

	public void setActual_accomplish(String actual_accomplish) {
		this.actual_accomplish = actual_accomplish;
	}

	public String getCur_fee_income() {
		return cur_fee_income;
	}

	public void setCur_fee_income(String cur_fee_income) {
		this.cur_fee_income = cur_fee_income;
	}

	public String getCust_cnt() {
		return cust_cnt;
	}

	public void setCust_cnt(String cust_cnt) {
		this.cust_cnt = cust_cnt;
	}

	public String getAo_year_of_exp() {
		return ao_year_of_exp;
	}

	public void setAo_year_of_exp(String ao_year_of_exp) {
		this.ao_year_of_exp = ao_year_of_exp;
	}

	public Date getAble_onboard_date() {
		return able_onboard_date;
	}

	public void setAble_onboard_date(Date able_onboard_date) {
		this.able_onboard_date = able_onboard_date;
	}

	public String getPrev_job() {
		return prev_job;
	}

	public void setPrev_job(String prev_job) {
		this.prev_job = prev_job;
	}

	public String getRc_sup_emp_id() {
		return rc_sup_emp_id;
	}

	public void setRc_sup_emp_id(String rc_sup_emp_id) {
		this.rc_sup_emp_id = rc_sup_emp_id;
	}

	public String getOp_sup_emp_id() {
		return op_sup_emp_id;
	}

	public void setOp_sup_emp_id(String op_sup_emp_id) {
		this.op_sup_emp_id = op_sup_emp_id;
	}

	public String getHr() {
		return hr;
	}

	public void setHr(String hr) {
		this.hr = hr;
	}

	public String getTrans_from_branch_id() {
		return trans_from_branch_id;
	}

	public void setTrans_from_branch_id(String trans_from_branch_id) {
		this.trans_from_branch_id = trans_from_branch_id;
	}

	public String getTrans_remark() {
		return trans_remark;
	}

	public void setTrans_remark(String trans_remark) {
		this.trans_remark = trans_remark;
	}

	public String getResume_source() {
		return resume_source;
	}

	public void setResume_source(String resume_source) {
		this.resume_source = resume_source;
	}

	public String getRecommender_emp_id() {
		return recommender_emp_id;
	}

	public void setRecommender_emp_id(String recommender_emp_id) {
		this.recommender_emp_id = recommender_emp_id;
	}

	public String getRecomm_emp_id() {
		return recomm_emp_id;
	}

	public void setRecomm_emp_id(String recomm_emp_id) {
		this.recomm_emp_id = recomm_emp_id;
	}

	public String getRecommend_awardee_emp_id() {
		return recommend_awardee_emp_id;
	}

	public void setRecommend_awardee_emp_id(String recommend_awardee_emp_id) {
		this.recommend_awardee_emp_id = recommend_awardee_emp_id;
	}

	public String getRecommend_letter() {
		return recommend_letter;
	}

	public void setRecommend_letter(String recommend_letter) {
		this.recommend_letter = recommend_letter;
	}

	public String getResign_return() {
		return resign_return;
	}

	public void setResign_return(String resign_return) {
		this.resign_return = resign_return;
	}

	public String getFinancial_exp() {
		return financial_exp;
	}

	public void setFinancial_exp(String financial_exp) {
		this.financial_exp = financial_exp;
	}

	public String getPre_fin_inst() {
		return pre_fin_inst;
	}

	public void setPre_fin_inst(String pre_fin_inst) {
		this.pre_fin_inst = pre_fin_inst;
	}

	public String getOther_pre_fin_inst() {
		return other_pre_fin_inst;
	}

	public void setOther_pre_fin_inst(String other_pre_fin_inst) {
		this.other_pre_fin_inst = other_pre_fin_inst;
	}

	public String getOther_fi() {
		return other_fi;
	}

	public void setOther_fi(String other_fi) {
		this.other_fi = other_fi;
	}

	public String getResign_reason() {
		return resign_reason;
	}

	public void setResign_reason(String resign_reason) {
		this.resign_reason = resign_reason;
	}

	public String getCust_satisfaction() {
		return cust_satisfaction;
	}

	public void setCust_satisfaction(String cust_satisfaction) {
		this.cust_satisfaction = cust_satisfaction;
	}

	public List<String> getReq_certificate() {
		return req_certificate;
	}

	public void setReq_certificate(List<String> req_certificate) {
		this.req_certificate = req_certificate;
	}

	public String getReq_certificate1() {
		return req_certificate1;
	}

	public void setReq_certificate1(String req_certificate1) {
		this.req_certificate1 = req_certificate1;
	}

	public String getAchievement() {
		return achievement;
	}

	public void setAchievement(String achievement) {
		this.achievement = achievement;
	}

	public String getSales_skill() {
		return sales_skill;
	}

	public void setSales_skill(String sales_skill) {
		this.sales_skill = sales_skill;
	}

	public String getActive() {
		return active;
	}

	public void setActive(String active) {
		this.active = active;
	}

	public String getPressure_manage() {
		return pressure_manage;
	}

	public void setPressure_manage(String pressure_manage) {
		this.pressure_manage = pressure_manage;
	}

	public String getCommunication() {
		return communication;
	}

	public void setCommunication(String communication) {
		this.communication = communication;
	}

	public String getProblem_solving_skill() {
		return problem_solving_skill;
	}

	public void setProblem_solving_skill(String problem_solving_skill) {
		this.problem_solving_skill = problem_solving_skill;
	}

	public String getIntv_sup_remark() {
		return intv_sup_remark;
	}

	public void setIntv_sup_remark(String intv_sup_remark) {
		this.intv_sup_remark = intv_sup_remark;
	}

	public String getHire_status() {
		return hire_status;
	}

	public void setHire_status(String hire_status) {
		this.hire_status = hire_status;
	}

	public Date getCreatetime() {
		return createtime;
	}

	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public String getModifier() {
		return modifier;
	}

	public void setModifier(String modifier) {
		this.modifier = modifier;
	}

	public Date getLastupdate() {
		return lastupdate;
	}

	public void setLastupdate(Date lastupdate) {
		this.lastupdate = lastupdate;
	}

	public List<Map<String, Object>> getData() {
		return data;
	}

	public void setData(List<Map<String, Object>> data) {
		this.data = data;
	}

	public String getLogin_id() {
		return login_id;
	}

	public void setLogin_id(String login_id) {
		this.login_id = login_id;
	}

	public String getsTime() {
		return sTime;
	}

	public void setsTime(String sTime) {
		this.sTime = sTime;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getRoleID() {
		return roleID;
	}

	public void setRoleID(String roleID) {
		this.roleID = roleID;
	}

	public String getMroleID() {
		return mroleID;
	}

	public void setMroleID(String mroleID) {
		this.mroleID = mroleID;
	}

	public String getSeqno() {
		return seqno;
	}

	public void setSeqno(String seqno) {
		this.seqno = seqno;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileRealName() {
		return fileRealName;
	}

	public void setFileRealName(String fileRealName) {
		this.fileRealName = fileRealName;
	}

	public String getFileLabel() {
		return fileLabel;
	}

	public void setFileLabel(String fileLabel) {
		this.fileLabel = fileLabel;
	}

	public String getFileData() {
		return fileData;
	}

	public void setFileData(String fileData) {
		this.fileData = fileData;
	}

	public Date getEvDate() {
		return evDate;
	}

	public void setEvDate(Date evDate) {
		this.evDate = evDate;
	}

	public String getHonest() {
		return honest;
	}

	public void setHonest(String honest) {
		this.honest = honest;
	}

	public String getKindly() {
		return kindly;
	}

	public void setKindly(String kindly) {
		this.kindly = kindly;
	}

	public String getProf() {
		return prof;
	}

	public void setProf(String prof) {
		this.prof = prof;
	}

	public String getInnov() {
		return innov;
	}

	public void setInnov(String innov) {
		this.innov = innov;
	}

	public String getOrganize() {
		return organize;
	}

	public void setOrganize(String organize) {
		this.organize = organize;
	}

	public String getSelfImprove() {
		return selfImprove;
	}

	public void setSelfImprove(String selfImprove) {
		this.selfImprove = selfImprove;
	}

	public String getExpectation() {
		return expectation;
	}

	public void setExpectation(String expectation) {
		this.expectation = expectation;
	}

	public Date getBookedOnboardDate() {
		return bookedOnboardDate;
	}

	public void setBookedOnboardDate(Date bookedOnboardDate) {
		this.bookedOnboardDate = bookedOnboardDate;
	}

	public BigDecimal getExpSalary() {
		return expSalary;
	}

	public void setExpSalary(BigDecimal expSalary) {
		this.expSalary = expSalary;
	}

	public BigDecimal getPreMonthGoal() {
		return preMonthGoal;
	}

	public void setPreMonthGoal(BigDecimal preMonthGoal) {
		this.preMonthGoal = preMonthGoal;
	}

	public BigDecimal getPre6MAccomplish() {
		return pre6MAccomplish;
	}

	public void setPre6MAccomplish(BigDecimal pre6mAccomp) {
		pre6MAccomplish = pre6mAccomp;
	}

	public BigDecimal getExp6MAbility() {
		return exp6MAbility;
	}

	public void setExp6MAbility(BigDecimal exp6mAbility) {
		exp6MAbility = exp6mAbility;
	}

	public BigDecimal getExp1YAbility() {
		return exp1YAbility;
	}

	public void setExp1YAbility(BigDecimal exp1yAbility) {
		exp1YAbility = exp1yAbility;
	}

	public BigDecimal getExpPropertyLoan() {
		return expPropertyLoan;
	}

	public void setExpPropertyLoan(BigDecimal expPropertyLoan) {
		this.expPropertyLoan = expPropertyLoan;
	}

	public BigDecimal getExpCreditLoan() {
		return expCreditLoan;
	}

	public void setExpCreditLoan(BigDecimal expCreditLoan) {
		this.expCreditLoan = expCreditLoan;
	}

	public String getHireStatusTransRemark() {
		return hireStatusTransRemark;
	}

	public void setHireStatusTransRemark(String hireStatusTransRemark) {
		this.hireStatusTransRemark = hireStatusTransRemark;
	}

	public String getPrefType() {
		return prefType;
	}

	public void setPrefType(String prefType) {
		this.prefType = prefType;
	}

	public String getBuLayer1() {
		return buLayer1;
	}

	public void setBuLayer1(String buLayer1) {
		this.buLayer1 = buLayer1;
	}

	public String getBuLayer2() {
		return buLayer2;
	}

	public void setBuLayer2(String buLayer2) {
		this.buLayer2 = buLayer2;
	}

	public String getBuLayer3() {
		return buLayer3;
	}

	public void setBuLayer3(String buLayer3) {
		this.buLayer3 = buLayer3;
	}

	public String getBuLayer4() {
		return buLayer4;
	}

	public void setBuLayer4(String buLayer4) {
		this.buLayer4 = buLayer4;
	}

	public String getBuLayer5() {
		return buLayer5;
	}

	public void setBuLayer5(String buLayer5) {
		this.buLayer5 = buLayer5;
	}

	public String getBuLayer6() {
		return buLayer6;
	}

	public void setBuLayer6(String buLayer6) {
		this.buLayer6 = buLayer6;
	}

	public String getBuLayer7() {
		return buLayer7;
	}

	public void setBuLayer7(String buLayer7) {
		this.buLayer7 = buLayer7;
	}

	public String getBuLayer8() {
		return buLayer8;
	}

	public void setBuLayer8(String buLayer8) {
		this.buLayer8 = buLayer8;
	}

	public String getCrosBuLayer1() {
		return crosBuLayer1;
	}

	public void setCrosBuLayer1(String crosBuLayer1) {
		this.crosBuLayer1 = crosBuLayer1;
	}

	public String getCrosBuLayer2() {
		return crosBuLayer2;
	}

	public void setCrosBuLayer2(String crosBuLayer2) {
		this.crosBuLayer2 = crosBuLayer2;
	}

	public String getCrosBuLayer3() {
		return crosBuLayer3;
	}

	public void setCrosBuLayer3(String crosBuLayer3) {
		this.crosBuLayer3 = crosBuLayer3;
	}

	public String getCrosBuLayer4() {
		return crosBuLayer4;
	}

	public void setCrosBuLayer4(String crosBuLayer4) {
		this.crosBuLayer4 = crosBuLayer4;
	}

	public String getCrosBuLayer5() {
		return crosBuLayer5;
	}

	public void setCrosBuLayer5(String crosBuLayer5) {
		this.crosBuLayer5 = crosBuLayer5;
	}

	public String getCrosBuLayer6() {
		return crosBuLayer6;
	}

	public void setCrosBuLayer6(String crosBuLayer6) {
		this.crosBuLayer6 = crosBuLayer6;
	}

	public String getCrosBuLayer7() {
		return crosBuLayer7;
	}

	public void setCrosBuLayer7(String crosBuLayer7) {
		this.crosBuLayer7 = crosBuLayer7;
	}

	public String getCrosBuLayer8() {
		return crosBuLayer8;
	}

	public void setCrosBuLayer8(String crosBuLayer8) {
		this.crosBuLayer8 = crosBuLayer8;
	}

	public boolean isSupervisor() {
		return isSupervisor;
	}

	public void setSupervisor(boolean isSupervisor) {
		this.isSupervisor = isSupervisor;
	}

	public String getFhcSeq() {
		return fhcSeq;
	}

	public void setFhcSeq(String fhcSeq) {
		this.fhcSeq = fhcSeq;
	}

	public BigDecimal getSeq() {
		return seq;
	}

	public void setSeq(BigDecimal seq) {
		this.seq = seq;
	}
	
}
