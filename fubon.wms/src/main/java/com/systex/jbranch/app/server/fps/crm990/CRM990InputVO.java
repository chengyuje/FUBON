package com.systex.jbranch.app.server.fps.crm990;

import java.sql.Date;
import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class CRM990InputVO extends PagingInputVO{
	//放行用
	private String[] release_ids;		 		//多筆客訴編號
	
	//查詢用
//	private Date s_happen_date;					//發生日期(起)
//	private Date e_happen_date;					//發生日期(迄)
	private Date s_createtime;					//建案日期(起)
	private Date e_createtime;					//建案日期(迄)
	private String handle_step;					//狀態(處理進度)
	
	//新增、修改用
	private String pri_id;						//登入者PRIVILEGEID
	private String complain_list_id;			//客訴編號
	private String case_type;					//案件分流
	private String emp_id;						//理專員編
	private String emp_name;					//專員姓名
	private String ao_code;						//AO Code
	private String service_yn;					//是否在職 (01:是   02:否)
	private String rev_emp_id;					//覆核人員
	private String branch_nbr;					//分行別
	private Date happen_date;					//發生日期
	private Date end_date;						//結案日期
	private String grade;						//申訴等級
	private String complain_source;				//客訴來源
	private String complain_type;				//客訴類型
	private String complain_summary;			//客訴摘要
	private String complain_product;			//客訴商品
	private String complain_product_currency;	//客訴商品幣別
	private String complain_product_amoun;		//客訴商品金額
	private Date buy_date;						//申購日期
	private String cust_id;   					//客戶統編
	private String cust_name;					//客戶姓名
	private Date birthdate;						//出生年月日(西元)
	private String occup;						//職業別
	private String phone;						//聯絡電話
	private String education;					//學歷
	private Date open_acc_date;					//開戶日期
	private String total_asset;					//總往來資產AUM
	private String check_sheet;					//是否寄送對帳單
	private String buy_product_type;			//銀行往來商品項目
	private String fileName;					//加附投資明細損益表
	private String realfileName;				//加附投資明細損益表
	private String reportID;					//前一筆招攬/事件報告書的客訴編號
	private String reportName;					//上傳招攬/事件報告書
	private String realReportName;				//上傳招攬/事件報告書
	private String problem_describe;			//問題實況
	private String cust_describe;				//客戶訴求
	private String handle_condition1;			//第一級處理情形
	private String handle_condition2;			//第二級處理情形
	private String handle_condition3;			//第三級處理情形
	private String handle_condition4;			//總行處理情形
	private Date createtime;					//建立日期
	
	private String next_emp_id;					//下一流程人員
	private String reject_type;					//退回至
	private String reason;						//退件原因
	
	private Boolean intiQuery;					//是否為初始查詢
	
	private String treat_cust_fairly;			//是否符合公平待客原則(Y/N)
	
	public String getPri_id() {
		return pri_id;
	}

	public void setPri_id(String pri_id) {
		this.pri_id = pri_id;
	}
	
//	public Date getS_happen_date() {
//		return s_happen_date;
//	}
//	
//	public void setS_happen_date(Date s_happen_date) {
//		this.s_happen_date = s_happen_date;
//	}
//
//	public Date getE_happen_date() {
//		return e_happen_date;
//	}
//
//	public void setE_happen_date(Date e_happen_date) {
//		this.e_happen_date = e_happen_date;
//	}

	public String[] getRelease_ids() {
		return release_ids;
	}
	
	public void setRelease_ids(String[] release_ids) {
		this.release_ids = release_ids;
	}
	
	public Date getS_createtime() {
		return s_createtime;
	}


	public void setS_createtime(Date s_createtime) {
		this.s_createtime = s_createtime;
	}

	public Date getE_createtime() {
		return e_createtime;
	}

	public void setE_createtime(Date e_createtime) {
		this.e_createtime = e_createtime;
	}

	public String getHandle_step() {
		return handle_step;
	}

	public void setHandle_step(String handle_step) {
		this.handle_step = handle_step;
	}

	public String getComplain_list_id() {
		return complain_list_id;
	}

	public void setComplain_list_id(String complain_list_id) {
		this.complain_list_id = complain_list_id;
	}

	public String getCase_type() {
		return case_type;
	}

	public void setCase_type(String case_type) {
		this.case_type = case_type;
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

	public String getAo_code() {
		return ao_code;
	}

	public void setAo_code(String ao_code) {
		this.ao_code = ao_code;
	}

	public String getService_yn() {
		return service_yn;
	}

	public void setService_yn(String service_yn) {
		this.service_yn = service_yn;
	}

	public String getRev_emp_id() {
		return rev_emp_id;
	}

	public void setRev_emp_id(String rev_emp_id) {
		this.rev_emp_id = rev_emp_id;
	}

	public String getBranch_nbr() {
		return branch_nbr;
	}

	public void setBranch_nbr(String branch_nbr) {
		this.branch_nbr = branch_nbr;
	}

	public Date getHappen_date() {
		return happen_date;
	}

	public void setHappen_date(Date happen_date) {
		this.happen_date = happen_date;
	}

	public Date getEnd_date() {
		return end_date;
	}

	public void setEnd_date(Date end_date) {
		this.end_date = end_date;
	}

	public String getGrade() {
		return grade;
	}

	public void setGrade(String grade) {
		this.grade = grade;
	}

	public String getComplain_source() {
		return complain_source;
	}

	public void setComplain_source(String complain_source) {
		this.complain_source = complain_source;
	}

	public String getComplain_type() {
		return complain_type;
	}

	public void setComplain_type(String complain_type) {
		this.complain_type = complain_type;
	}

	public String getComplain_summary() {
		return complain_summary;
	}

	public void setComplain_summary(String complain_summary) {
		this.complain_summary = complain_summary;
	}

	public String getComplain_product() {
		return complain_product;
	}

	public void setComplain_product(String complain_product) {
		this.complain_product = complain_product;
	}

	public String getComplain_product_currency() {
		return complain_product_currency;
	}

	public void setComplain_product_currency(String complain_product_currency) {
		this.complain_product_currency = complain_product_currency;
	}

	public String getComplain_product_amoun() {
		return complain_product_amoun;
	}

	public void setComplain_product_amoun(String complain_product_amoun) {
		this.complain_product_amoun = complain_product_amoun;
	}
	
	public Date getBuy_date() {
		return buy_date;
	}

	public void setBuy_date(Date buy_date) {
		this.buy_date = buy_date;
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

	public Date getBirthdate() {
		return birthdate;
	}

	public void setBirthdate(Date birthdate) {
		this.birthdate = birthdate;
	}

	public String getOccup() {
		return occup;
	}

	public void setOccup(String occup) {
		this.occup = occup;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getEducation() {
		return education;
	}

	public void setEducation(String education) {
		this.education = education;
	}
	
	public Date getOpen_acc_date() {
		return open_acc_date;
	}

	public void setOpen_acc_date(Date open_acc_date) {
		this.open_acc_date = open_acc_date;
	}

	public String getTotal_asset() {
		return total_asset;
	}

	public void setTotal_asset(String total_asset) {
		this.total_asset = total_asset;
	}

	public String getCheck_sheet() {
		return check_sheet;
	}

	public void setCheck_sheet(String check_sheet) {
		this.check_sheet = check_sheet;
	}

	public String getBuy_product_type() {
		return buy_product_type;
	}

	public void setBuy_product_type(String buy_product_type) {
		this.buy_product_type = buy_product_type;
	}
	
	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getRealfileName() {
		return realfileName;
	}

	public void setRealfileName(String realfileName) {
		this.realfileName = realfileName;
	}

	public String getReportID() {
		return reportID;
	}

	public void setReportID(String reportID) {
		this.reportID = reportID;
	}

	public String getReportName() {
		return reportName;
	}

	public void setReportName(String reportName) {
		this.reportName = reportName;
	}

	public String getRealReportName() {
		return realReportName;
	}

	public void setRealReportName(String realReportName) {
		this.realReportName = realReportName;
	}

	public String getProblem_describe() {
		return problem_describe;
	}

	public void setProblem_describe(String problem_describe) {
		this.problem_describe = problem_describe;
	}

	public String getCust_describe() {
		return cust_describe;
	}

	public void setCust_describe(String cust_describe) {
		this.cust_describe = cust_describe;
	}

	public String getHandle_condition1() {
		return handle_condition1;
	}

	public void setHandle_condition1(String handle_condition1) {
		this.handle_condition1 = handle_condition1;
	}

	public String getHandle_condition2() {
		return handle_condition2;
	}

	public void setHandle_condition2(String handle_condition2) {
		this.handle_condition2 = handle_condition2;
	}

	public String getHandle_condition3() {
		return handle_condition3;
	}

	public void setHandle_condition3(String handle_condition3) {
		this.handle_condition3 = handle_condition3;
	}

	public String getHandle_condition4() {
		return handle_condition4;
	}

	public void setHandle_condition4(String handle_condition4) {
		this.handle_condition4 = handle_condition4;
	}

	public Date getCreatetime() {
		return createtime;
	}

	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}

	public String getNext_emp_id() {
		return next_emp_id;
	}

	public void setNext_emp_id(String next_emp_id) {
		this.next_emp_id = next_emp_id;
	}

	public String getReject_type() {
		return reject_type;
	}

	public void setReject_type(String reject_type) {
		this.reject_type = reject_type;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public Boolean getIntiQuery() {
		return intiQuery;
	}

	public void setIntiQuery(Boolean intiQuery) {
		this.intiQuery = intiQuery;
	}

	public String getTreat_cust_fairly() {
		return treat_cust_fairly;
	}

	public void setTreat_cust_fairly(String treat_cust_fairly) {
		this.treat_cust_fairly = treat_cust_fairly;
	}
	
}
